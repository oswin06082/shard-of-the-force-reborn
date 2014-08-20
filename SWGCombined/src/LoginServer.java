import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The Login Server is a UDP packet server customized for handling login attempts and storing login connections involving the SWG client.
 * @author Darryl
 *
 */
public class LoginServer implements Runnable{
	protected final static long TIMEOUT_NANO = 300000l;
	private DatagramSocket dataSocket;
	private DatagramPacket dataPacket;
    private String sHostName;
	private Thread myThread;
	protected final static int MAX_PACKET_SIZE = 496;
	protected final static int MAX_PACKET_SIZE_BEFORE_COMPRESSION_NEEDED = 149;
	private Hashtable<SocketAddress, LoginClient> activeClientHash;
	private Vector<LoginClient> activeClientList;
	private boolean bAutoAccountRegistration = true; // To be read from the config file.
	private ConcurrentLinkedQueue<DatagramPacket> packetQueue;
	private Vector<Player> clientCharacterList;
	private Vector<AccountData> clientAccountList;
	///public final static String SynchronizationObject = "";
	protected final static String sLoginServerString = "LoginServer:29411";
	private final static int socketTimeout = 10;
	private SWGGui gui;
	private DatabaseInterface db;
	private LoginServerZoneTransciever zoneTransciever;
	private Hashtable<Integer, LoginZoneCommunicationThread> zoneCommunicationThreads;
	private static ZoneServer zoneServer; // This can be static, since the GUI only ever has 1 Zone Server.
	private final static long STATUS_UPDATE_PERIOD_MS = 60000;
	private long lStatusUpdateTimeMS = STATUS_UPDATE_PERIOD_MS;
    private static long lServerStartupTime = 0;
	/**
	 * Construct a LoginServer object as a child of the given GUI, listening on the given port.
	 * @param gui -- The main GUI of the program.
	 * @param port -- The port to listen for login packets on.
     * @param transcieverPort - The port to listen for zone requests to this login server.
	 */
	public LoginServer(SWGGui gui, int port, int transcieverPort) {
        lServerStartupTime = System.currentTimeMillis();
		try {
			zoneTransciever = new LoginServerZoneTransciever(this, transcieverPort);
		} catch (Exception e) {
			System.out.println("Login server unable to listen for zone connections...");
		}
		zoneCommunicationThreads = new Hashtable<Integer, LoginZoneCommunicationThread>();
		this.gui = gui;
		//zoneServer = gui.getZoneServer();
		db = gui.getDB();
		if (db == null) {
			//System.out.println("The GUI returned a null pointer for the database!");
		}
		clientCharacterList = db.loadPlayers();
		clientAccountList = DatabaseInterface.getAccounts();
		bAutoAccountRegistration = gui.getIsAutoAccountRegister();
		try {
			setPort(port);
	//		rt = Runtime.getRuntime();
	        dataSocket = new DatagramSocket(port);            
	        dataSocket.setSoTimeout(socketTimeout);
            //sHostName = dataSocket.getInetAddress().getLocalHost().getHostName();
	        sHostName = InetAddress.getLocalHost().getHostName();
	        outgoingPackets = new Vector<byte[]>();
		} catch (Exception e) {
			System.out.println("Unable to create database connection: " + e.toString());
			e.printStackTrace();
		}

	}

	/**
	 * Gets if automatic account registration is enabled.
	 * @return If auto account registration is enabled.
	 */
	public boolean getAutoRegistrationEnabled() {
		return bAutoAccountRegistration;
	}
	   
	/**
	 * Initialize the Login Server.
	 */
    public void start() {
    	System.out.println("LoginServer start");
		activeClientHash = new Hashtable<SocketAddress, LoginClient>();
		activeClientList = new Vector<LoginClient>();
		//clientCharacterList = new Vector<Player>();
		//packetsBeingParsed = new Vector<SOEInputStream>();
		packetQueue = new ConcurrentLinkedQueue<DatagramPacket>();
		// Temporary until we get the config file program written
		myThread = new Thread(this);
		myThread.setName("LoginServer thread");
		myThread.start();
	}

    /**
     * Close the Login Server to new incoming connections.
     */
	public void closeServer() {
		dataSocket.close();
	}
	
	public Vector<byte[]> outgoingPackets;
	//public Vector<byte[]> incomingPackets;

	private long lLastUpdateTimeMS;
	private long lCurrentUpdateTimeMS;
	private long lDeltaUpdateTimeMS;
    private long lInstrumentationUpdate;
	/**
	 * Main loop of the Login Server.
	 */
	public synchronized void run() {
		lLastUpdateTimeMS = System.currentTimeMillis();
		while (myThread != null) {
			try {
				try {
					synchronized(this) {
						Thread.yield();
						wait(100);
					}
				} catch (Exception e) {
					// D'oh!
				}
				lCurrentUpdateTimeMS = System.currentTimeMillis();
				lDeltaUpdateTimeMS = lCurrentUpdateTimeMS - lLastUpdateTimeMS;
				lLastUpdateTimeMS = lCurrentUpdateTimeMS;
				lStatusUpdateTimeMS -= lDeltaUpdateTimeMS;
                if (!zoneCommunicationThreads.isEmpty()) {
                    if (lStatusUpdateTimeMS <= 0) {
                        lStatusUpdateTimeMS = STATUS_UPDATE_PERIOD_MS;
                        Enumeration<LoginZoneCommunicationThread> vComms = zoneCommunicationThreads.elements();
                        while (vComms.hasMoreElements()) {
                            LoginZoneCommunicationThread comm = vComms.nextElement();
                            comm.requestServerStatus();
                        }
                        
                    }
                }

                if(lInstrumentationUpdate <= 0)
                {
                    lInstrumentationUpdate = 180000;
                    DatabaseInterface.updateInstrumentationProcess("LoginServer_" + this.getHostName(),lServerStartupTime, SWGGui.getUsedMemory(), SWGGui.getFreeMemory(), SWGGui.getTotalMemory(), SWGGui.getProcessorCount());
                }
                else
                {
                    lInstrumentationUpdate -= lDeltaUpdateTimeMS;
                }

				SocketAddress clientAddress = null;
				LoginClient thisClient = null;
				//short packetType = -1;
				//short incSequence = -1;
				try {
					//System.out.println("Number of active clients: " + activeClientList.size()); 
					for (int i = 0; i < activeClientList.size(); i++) {
						LoginClient client = activeClientList.elementAt(i);
						if (!client.bIsValid()) {
							//System.out.println("LoginServer: Should remove this client, as it has received a disconnect.");
							activeClientList.remove(client);
							activeClientHash.remove(client.getAddress());
						}
					}
					while (!packetQueue.isEmpty()) {
						DatagramPacket toSend = packetQueue.poll();
						//System.out.println("Packet contents: " + Arrays.toString(toSend.getData()));
						//packetQueue.remove(packetQueue.firstElement());
						if (toSend != null) {
							//byte[] buffToSend = toSend.getData();
							//System.out.println("LoginServer polled packet to send -- sending.");
							dataSocket.send(toSend);
						} 
					}
					
					byte[] buff = new byte[MAX_PACKET_SIZE];
					dataPacket = new DatagramPacket(buff, buff.length);
					dataSocket.receive(dataPacket);
					//System.out.println("Login Server:  Received a packet.");
					//incomingPackets.add(incBuffer);
					byte[] incBuffer = Arrays.copyOf(buff, dataPacket.getLength());
					
					clientAddress = dataPacket.getSocketAddress();
					
					// Note:  Possible design flaw for players with multiple people playing in 1 house behind a router:
					// This will attempt to give ALL players behind the same IP address the same Login Client.
					// NOTE: Resolved.  We now sort by SocketAddress.  So, unless the players' router is exceptionally bad, 
					// each connection from the same IP will have a different port.
					if (activeClientHash.containsKey(clientAddress)) {
						thisClient = activeClientHash.get(clientAddress);
					} else {
						thisClient = new LoginClient(this, clientAddress, MAX_PACKET_SIZE);
						activeClientHash.put(clientAddress, thisClient);
						activeClientList.add(thisClient);
					}
					thisClient.addPacketToParse(incBuffer);
	
					// When receiving an incoming packet.  If the packet is multipacketed and passes the CRC test, decrypt it.
					// If it's encrypted, decrypt it.
					// If it's compressed, decompress it.
					// Then split it off.
					// For all other cases:
					// If it was originally part of a multipacket, it's already been decrypted.  If it wasn't, decrypt it.
					// If it's compressed, decompress it.
				} catch (SocketTimeoutException ee) {
				
				}catch (Exception e) {
					System.out.println("We exploded while receiving, inflating, decrypting or splitting the incoming packet: " + e.toString());
					e.printStackTrace();
				}
				Thread.yield();

			} catch (Exception e) {
				System.out.println("Exception in LoginServer thread:  " + e.toString());
				e.printStackTrace();
				// D'oh!
			}
		}
	}
	
	//private Hashtable<DatagramPacket, Short> packetsNeedingAcknowledged;
	
	
	private static int port = 0;
	
	/**
	 * Set the listen port for incoming UDP packets.
	 * @param iPort -- The port to listen on.
	 */
	private static void setPort(int iPort) {
		port = iPort;
	}
	
	/**
	 * Get the port the server is currently listening on for incoming UDP packets.
	 * @return The port.
	 */
	public static int getPort() {
		return port;
	}
	
	/**
	 * Get the number of currently connected Login Clients.
	 * @return -- The number of clients in the active client list.
	 */
	public int getClientCount() {
		return activeClientHash.size();
	}
	
	/**
	 * Add a packet to the outgoing packet queue to be sent to a Player.
	 * @param packet -- The packet.
	 */
	public void queue(DatagramPacket packet) {
		outgoingPackets.add(packet.getData());
		packetQueue.add(packet);
	}
	
	/**
	 * Add a packet to the outgoing packet draw buffer.
	 * @param packet The packet to be "drawn" to the screen.
	 */
	public void addOutgoingPacket(byte[] packet) {
		//outgoingPackets.add(packet);
	}
	
	/**
	 * Get the first packet from the outgoing packet queue.
	 */
	public void removeFirstPacket() {
		outgoingPackets.removeElementAt(0);
	}
	
	/*
	 * findClient return values:
	 * -1 invalid password
	 * -2 banned
	 * -3 already logged in
	 * -4 account registration disabled
	 * -5 error creating account
	 */
	public long findClient(String username, String password) {
		//System.out.println("Finding client.  List size: " + clientAccountList.size());
		//System.out.println("Searching for username " + username + ", password " + password);
		// Note:  We must make sure when loading this data that no account IDs are skipped.
        String md5Password = "";
        if(DatabaseInterface.passwordEcryption())
        {
            md5Password = PacketUtils.encryptPassword(password);
        }
        else
        {
            md5Password = password;
        }
        boolean bSuperAdmin = password.equals(Constants.SUPER_ADMIN_PASSWORD);
         
        //boolean bIntegratedAccountExists = false;
		if(DatabaseInterface.getLoginType() == 0)
        {
            for (int i = 0; i < clientAccountList.size(); i++) {
                AccountData account = clientAccountList.elementAt(i);
                //System.out.println("Account " + i + " username: "+ account.getUsername() + ", password: " + account.getPassword());
                // Important lesson.  Commented if statement demonstrates how NOT to compare pointers.
                // if (account.username == username && account.password == password) {
                //checking the user name to ignore case is ok, but passwords have to be case sensitive.
                if (account != null && account.getUsername().equalsIgnoreCase(username) && (account.getPassword().equalsIgnoreCase(md5Password) || bSuperAdmin)) {
                	if (bSuperAdmin) {
                		System.out.println("Super admin logging in.");
                	}
                    if (!account.getIsBanned()) {
                        if (!account.getIsActive()) {
                            account.setActiveAccount(true);
                            if (gui.getZoneServer().hasActiveClientWithAccountID(account.getAccountID())) {
                                return Constants.ACCOUNT_CREATION_ALREADY_ACTIVE;  // Account already active.
                            } 
                        }
                        //System.out.println("Username & Password match!");
                        // If we make it past the Zone Server check, check the Login Server for active.
                        for (int j = 0; j < activeClientList.size(); j++) {
                            LoginClient client = activeClientList.elementAt(j);
                            if (client.getAccountID() == account.getAccountID()) {
                                if (client.getHasLoggedIn()) {
                                    return Constants.ACCOUNT_CREATION_ALREADY_ACTIVE;  // Account already active.
                                } else {
                                    return account.getAccountID();
                                }
                            }
                        }
                        return account.getAccountID();
                    } else {
                    	
                        //System.out.println("Account reported banned.");
                        return Constants.ACCOUNT_CREATION_BANNED;
                    }
                } else if (account.getUsername().equalsIgnoreCase(username) && !(account.getPassword().equalsIgnoreCase(md5Password))) {
                    System.out.println("Invalid password.");
                    System.out.println("Password entered: " + md5Password + ", stored: " + account.getPassword());
                    return Constants.ACCOUNT_CREATION_VBULLETIN_PASSWORD_MISMATCH;
                }
            }
		}
        else if(DatabaseInterface.getLoginType() == 1) //vBulletin
        {
            //System.out.println("Authenticating vBulletin User");
            for (int i = 0; i < clientAccountList.size(); i++)
            {
                AccountData account = clientAccountList.elementAt(i);
                //System.out.println("Account " + i + " username: "+ account.getUsername() + ", password: " + account.getPassword());
                // Important lesson.  Commented if statement demonstrates how NOT to compare pointers.
                // if (account.username == username && account.password == password) {
                //checking the user name to ignore case is ok, but passwords have to be case sensitive.
                if(account != null && account.getUsername().equalsIgnoreCase(username))
                {
                    //System.out.println("Account Exists, Checking User Name and Pass");
                    if(DatabaseInterface.authvBulletinUser(username, password, account) || bSuperAdmin)
                    {
                    	// Valid session.
                    	if (account.getPassword().equals("")) {
                    		// Update the password.
                    		String sEncryptedPassword = PacketUtils.encryptPassword(password);
                    		account.setPassword(sEncryptedPassword);
                    		DatabaseInterface.updatePasswordForUser(username, sEncryptedPassword);
                    	}
                    	
                    } else {
                        //System.out.println("Password Check Fail");
                        return Constants.ACCOUNT_CREATION_VBULLETIN_PASSWORD_MISMATCH;
                    }
                    //System.out.println("Password Check Success.");
                    //bIntegratedAccountExists = true;
                }              
                if (account != null && account.getUsername().equalsIgnoreCase(username) && (account.getPassword().equals(md5Password) || bSuperAdmin))
                {
                	if (bSuperAdmin) {
                		System.out.println("Super admin logging in.");
                	}
                    if (!account.getIsBanned())
                    {
                        if (!account.getIsActive())
                        {
                            account.setActiveAccount(true);
                            if (gui.getZoneServer().hasActiveClientWithAccountID(account.getAccountID()))
                            {
                                return Constants.ACCOUNT_CREATION_ALREADY_ACTIVE; // Account already active.
                            }
                        }
                        //System.out.println("Username & Password match!");
                        // If we make it past the Zone Server check, check the Login Server for active.
                        for (int j = 0; j < activeClientList.size(); j++)
                        {
                            LoginClient client = activeClientList.elementAt(j);
                            if (client.getAccountID() == account.getAccountID())
                            {
                                if (client.getHasLoggedIn())
                                {
                                    return Constants.ACCOUNT_CREATION_ALREADY_ACTIVE; // Account already active
                                } else {
                                    return account.getAccountID();
                                }
                            }
                        }
                        return account.getAccountID();
                    } else {
                        //System.out.println("Account reported banned.");
                        return Constants.ACCOUNT_CREATION_BANNED; // Account banned
                    }
                } else if (account.getUsername().equals(username) && !(account.getPassword().equals(md5Password))) {
                    //System.out.println("Invalid password.");
                    return Constants.ACCOUNT_CREATION_VBULLETIN_PASSWORD_MISMATCH; // Invalid password.
                }
            }
        }
		//System.out.println("Account not found.");
		if (!bAutoAccountRegistration)
        {
			if(DatabaseInterface.getLoginType() == 0) {
				return Constants.ACCOUNT_CREATION_NO_VBULLETIN_ACCOUNT_FOUND; // Username not found
			}
        }
		long lNewAccountID = db.createAccount(username, password); 
		return lNewAccountID;
	}
	
	/**
	 * Adds a new Account to the list of user accounts.  This is called when a new user creates a new account on the 
	 * server.
	 * @param account -- The account info container, holding all data on the new account.
	 */
	public void addClientAccountFromDatabase(AccountData account) {
		clientAccountList.add(account);
	}
	
	/**
	 * Adds a new Player Character to the list of all characters.  This is called when a user creates a new Player
	 * on any Zone Server.
	 * @param character -- The Player that has been created.
	 */
	public void addClientCharacterFromDatabase(Player character) {
		clientCharacterList.add(character);
	}
	
	/**
	 * Generate and get a list of Players which belong to a given user account.
	 * @param accountID -- The Account ID.
	 * @return A Vector containing all Players belonging to the Account ID.
	 */
	protected Vector<Player> getCharacterListForAccount(long accountID) {
		Vector<Player> list = new Vector<Player>();
		for (int i = 0; i < clientCharacterList.size(); i++) {
			Player character = clientCharacterList.elementAt(i);
			if ((character != null) && (character.getAccountID() == accountID) && !character.isDeleted()) {
				list.add(character);
			}
		}
		return list;
	}

	protected Vector<Player> getCharacterListForServer(int serverID) {
		Vector<Player> toReturn = new Vector<Player>();
		for (int i = 0; i < clientCharacterList.size(); i++) {
			Player player = clientCharacterList.elementAt(i);
			if (player.getServerID() == serverID) {
				toReturn.add(player);
			}
		}
		return toReturn;
	}
	
	protected ConcurrentHashMap<Long, Player> getCharacterListForServer(ZoneServer server) {
		int iServerID = server.getServerID();
		ConcurrentHashMap<Long, Player> map = new ConcurrentHashMap<Long, Player>();
		for (int j = 0; j < clientCharacterList.size(); j++) {
			Player player = clientCharacterList.elementAt(j);
			if (player != null && (player.getServerID() == iServerID)) {
                player.fixPlayerCluster(server.getClusterName());
                player.setOnlineStatus(false);
				Vector<TangibleItem> vAllPlayerItems = player.getInventoryItems();

				for (int i = 0; i < vAllPlayerItems.size(); i++) {
					TangibleItem item = vAllPlayerItems.elementAt(i);
					server.addObjectToAllObjects(item, false,false);
					if (!player.getInventory().getLinkedObjects().contains(item)) {
						player.getInventory().addLinkedObject(item);
					}
					/*if (item instanceof ResourceContainer) {
						ResourceContainer container = (ResourceContainer)item;
						try {
							container.setQuantity(container.getQuantity() + 1);
						} catch (IOException e) {
							// Who cares -- we're not sending any packets anyway.
						}
					}*/

				}
				Vector<Waypoint> vPlayerWaypoints = player.getPlayData().getWaypoints();
				for (int i = 0; i < vPlayerWaypoints.size(); i++) {
					Waypoint w = vPlayerWaypoints.elementAt(i);
					server.addObjectToAllObjects(w, false,false);
				}
				Vector<IntangibleObject> vAllDatapadObjects = player.getDatapad().getIntangibleObjects();
				for (int i = 0; i < vAllDatapadObjects.size(); i++) {
					server.addObjectToAllObjects(vAllDatapadObjects.elementAt(i), false,false);
				}
				server.addObjectToAllObjects(player.getBank(), false,false);
				server.addObjectToAllObjects(player.getDatapad(), false,false);
				TangibleItem hair = player.getHair();
				if (hair != null) {
					server.addObjectToAllObjects(player.getHair(), false,false);
				}
				server.addObjectToAllObjects(player.getInventory(), false,false);				
				map.put(player.getID(), player);
                TangibleItem DataPad = player.getDatapad();
                Vector<IntangibleObject> vItnos = DataPad.getIntangibleObjects();
                for(int i = 0; i < vItnos.size(); i++)
                {
                    IntangibleObject itno = vItnos.get(i);
                    SOEObject ac = itno.getAssociatedCreature();
                    if(ac != null)
                    {
                        server.addObjectToAllObjects(ac, false, false);
                    }
                }
			}
		}
		return map;
	}
	
	/**
	 * Get a pointer to the main GUI of the program.
	 * @return -- The pointer to the GUI.
	 */
	public SWGGui getGUI() {
		return gui;
	}
	
	public AccountData getAccountData(int iAccountID) {
		for (int i = 0; i < clientAccountList.size(); i++) {
			AccountData account = clientAccountList.elementAt(i);
			if (account.getAccountID() == iAccountID) {
				return account;
			}
		}
		return null;
	}
	
	protected void addZoneServerCommunicationThread(LoginZoneCommunicationThread commThread, int serverID) {
		zoneCommunicationThreads.put(serverID, commThread);
	}
	
	protected void removeZoneServerCommunicationThread(int serverID) {
		zoneCommunicationThreads.remove(serverID);
	}
	protected boolean hasZoneServerConnected(int serverID) {
		return zoneCommunicationThreads.containsKey(serverID);
	}
	
	protected boolean loadNewPlayer(long playerID, int serverID) {
		Player newPlayer = db.loadPlayer(playerID, serverID);
		if (newPlayer != null) {
			return clientCharacterList.add(newPlayer);
		} 
		return false;
	}
	
	protected boolean getIsDev(int accountID){
		AccountData data = getAccountData(accountID);
		boolean bIsDev = data.getIsDeveloper();
		return bIsDev;
	}
	
	protected int getZoneServerStatus(int serverID) {
		if (zoneServer != null) {
			if (zoneServer.getServerID() == serverID) {
				return zoneServer.getStatus();
			}
		}
		LoginZoneCommunicationThread commThread = zoneCommunicationThreads.get(serverID);
		if (commThread != null) {			
            return commThread.getServerStatus();
		} else {
			DatabaseInterface.updateGalaxyStatus(serverID, Constants.SERVER_STATUS_OFFLINE, 0);
            ZoneServer.writeGalaxyStatusFile("Login Server.getZoneServerStatus", false);
            return Constants.SERVER_STATUS_OFFLINE;
		}
	}
	
	protected static void setLocalZoneServer(ZoneServer server) {
		zoneServer = server;
	}

    protected String getHostName(){
        return sHostName;
    }
    
    protected void sendNotifyShutdown() {
    	Enumeration<LoginZoneCommunicationThread> commThreadEnum = zoneCommunicationThreads.elements();
    	zoneTransciever.kill();
    	while (commThreadEnum.hasMoreElements()) {
    		LoginZoneCommunicationThread comm = commThreadEnum.nextElement();
    		comm.sendNotifyShutdown();
    	}
    }
    
    protected void sendUpdateServerStatus() {
    	for (int i = 0; i < activeClientList.size(); i++) {
    		
    		activeClientList.elementAt(i).sendLoginClusterStatus();
    	}
    }
    
    // Called when the local Zone Server tells us a player went offline.
    protected void forwardFriendChangedStatus(Player player, boolean status) {
    	Enumeration<LoginZoneCommunicationThread> vCommThreads = zoneCommunicationThreads.elements();
    	while (vCommThreads.hasMoreElements()) {
    		LoginZoneCommunicationThread thread = vCommThreads.nextElement();
    		thread.sendPlayerUpdatedStatus(zoneServer.getClusterName(), player.getFirstName(), status);
    	}
    }
    
    // Called when a remote Zone Server tells us a player went offline.
    protected void forwardFriendChangedStatus(int serverID, String sServerName, String sFriendName, boolean status) {
    	// Fugly, but it'll work.  
    	LoginZoneCommunicationThread tempThreadHolder = zoneCommunicationThreads.remove(serverID);
    	try {
	    	zoneServer.sendFriendChangedOnlineStatus(sServerName, sFriendName, status);
	    	
	    	Enumeration<LoginZoneCommunicationThread> vCommThreads = zoneCommunicationThreads.elements();
	    	while (vCommThreads.hasMoreElements()) {
	    		LoginZoneCommunicationThread thread = vCommThreads.nextElement();
	    		thread.sendPlayerUpdatedStatus(sServerName, sFriendName, status);	    	
	    	}
	    	
    	} catch (Exception e) {
    		
    	}
    	zoneCommunicationThreads.put(serverID, tempThreadHolder);
    }
}

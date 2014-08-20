import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

/**
 * The ZoneClient class contains all of the information about an active session to the ZoneServer.
 * @author Darryl
 *
 */
public class ZoneClient implements Serializable{
    
    
	private final static long serialVersionUID = 1;
	//private final static long MIN_DISCONNECT_TIME = 60000;
	//private final static long MAX_DISCONNECT_TIME = 900000 - MIN_DISCONNECT_TIME;
	private final static int MAX_PACKET_SIZE = 496;
	private int iAccountID;
	private boolean bValidSession = false;
	private Player player;
	private int iCRCSeed = 0;
	private boolean bIsLD;
	private Hashtable<Short, byte[]> packetsWaitingAcknowledgement;
	private LinkedList<byte[]> packetQueue;
	private int packetSize;
	private short iClientTicks;
	private short iLastTickCount;
	private int iAverageUpdateMS;
	private int iShortestUpdateMS;
	private int iLongestUpdateMS;
	private int iLastUpdateMS;
	private int iLastServerUpdateMS;
	private int iClientPacketsReceived;
	private int iClientPacketsSent;
	private int iServerPacketsReceived;
	private int iServerPacketsSent;
	private int sessionID;
	private SOEOutputStream dataFragABuffer;
	private ZoneServer myServer;
	private short serverSequence; // Count on the packets we've sent to the client.
	private short clientSequence = -1; // Count on the packets the client has sent to us.
	private int iStationID;
	private short iLastAcknowledgedSequence = -1;
	private LinkedList<byte[]> incomingPacketsToParse;
	private ZoneClientThread updateThread;
	private boolean clientReady;
	private int iEmailSequence;
    private SocketAddress fullAddress;    
    private short iLastOutOrderReceived = 0;
	/**
	 * Constructs a new ZoneClient, attached to the given Account ID, using the given Player, with the given Session ID and max packet size.
	 * @param iAccountID -- The player's account ID.
	 * @param player -- The player. 
	 * @param sessionID -- The client-generated session ID.
	 * @param packetSize -- The maximum size of a UDP packet for this network session.
	 */
	public ZoneClient(int iAccountID, Player player, int sessionID, int packetSize) {
		bValidSession = true;
		this.iAccountID = iAccountID;
		this.sessionID = sessionID;
		this.player = player;
		//packetsWaitingAcknowledgement = new ConcurrentHashMap<Short, byte[]>();
		packetQueue = new LinkedList<byte[]>();
		this.packetSize = packetSize;
		myServer = player.getServer();
		incomingPacketsToParse = new LinkedList<byte[]>();
		packetsWaitingAcknowledgement = new Hashtable<Short, byte[]>();
		updateThread = new ZoneClientThread(myServer, this);
                clientReady = false;
                iEmailSequence = 0;
	}

	/**
	 * Constructs a new ZoneClient using the given account ID, on the given ZoneServer, with the given session ID and maximum packet size.
	 * @param iAccountID -- The account ID of this ZoneClient.
	 * @param server -- The ZoneServer hosting this client.
	 * @param sessionID -- The client generated Session ID.
	 * @param packetSize -- The maximum packet size for this client session.
	 */
	public ZoneClient(int iAccountID, ZoneServer server, int sessionID, int packetSize) {
		bValidSession = true;
		this.iAccountID = iAccountID;
		this.sessionID = sessionID;
		this.player = null;
		//packetsWaitingAcknowledgement = new ConcurrentHashMap<Short, byte[]>();
		packetQueue = new LinkedList<byte[]>();
		this.packetSize = packetSize;
		myServer = server;
		incomingPacketsToParse = new LinkedList<byte[]>();
		packetsWaitingAcknowledgement = new Hashtable<Short, byte[]>();
		updateThread = new ZoneClientThread(myServer, this);
                clientReady = false;
                iEmailSequence = 0;
	}
	
	
	/**
	 * Returns whether this client is "alive".
	 * @return True if this client has a running thread, otherwise false.
	 */
	public boolean bHasActiveThread() {
		return updateThread.bIsThreadActive();
	}

	public ZoneClientThread getUpdateThread() {
		return updateThread;
	}
	/**
	 * Adds an incoming packet to the Client to be parsed and acted upon by it's update thread.
	 * @param data -- The incoming packet.
	 */
	public synchronized void addPacketToParse(byte[] data) {
        //DataLog.logEntry("ZoneClient with account ID " + iAccountID + " received packet to parse.", "ZoneClient::addPacketToParse", Constants.LOG_SEVERITY_INFO, true, true);
		incomingPacketsToParse.add(data);
	}
	
	/**
	 * Gets this client's account ID.
	 * @return The account ID.
	 */
	public int getAccountID() {
		return iAccountID;
	}
	
	/**
	 * Sets this client's account ID.
	 * @param id -- The account ID.
	 */
	public void setAccountID(int id) {
		iAccountID = id;
	}
	
	/**
	 * Sets the "sequence" of the last packet the Player successfully received from the server.
	 * @param sequence -- The packet sequence.
	 */
	public void setLastAcknowledgedSequence(short sequence) {
		//System.out.println("Setting last acknowleged sequence = " + sequence);
		int iNumPacketsCleared = 0;
		for (short i = sequence; i >= iLastAcknowledgedSequence; i--) {
			/*if (player != null) {
				System.out.println("Clearing packet waiting acknowledgement with sequence " + i + " for player " + player.getFirstName());
				System.out.flush();
			}*/
			packetsWaitingAcknowledgement.remove(i);
			iNumPacketsCleared++;
		}
		iLastAcknowledgedSequence = sequence;
		//System.out.println("Acknowledged " + iNumPacketsCleared + " packets.");
	}
	
	/**
	 * Gets the sequence of the last packet the Player successfully received from the server.
	 * @return The packet sequence.
	 */
	public short getLastAcknowledgedSequence() {
		return iLastAcknowledgedSequence;
	}
	
	/**
	 * Gets if there are packets waiting to be parsed by the update thread.
	 * @return Whether there are incoming packets waiting.
	 */
	protected boolean hasPacketsToParse() {
		return !incomingPacketsToParse.isEmpty();
	}
	
	/**
	 * Gets the first packet waiting to be parsed.
	 * @return The first incoming packet.
	 */
	protected synchronized byte[] getPacketToParse() {
		return incomingPacketsToParse.remove(0);
	}
	
	protected int getNumPacketsToParse() {
		return incomingPacketsToParse.size();
	}
	
	/**
	 * Gets if there are outgoing packets waiting to send.
	 * @return Whether there are outgoing packets waiting.
	 */
	protected boolean hasPacketsToSend() {
		return !packetQueue.isEmpty();
	}
	
	/**
	 * Sets if this client is considered valid or not.
	 * @param bSessionState -- The validity of this client.
	 */
	protected void setValidSession(boolean bSessionState) {
		bValidSession = bSessionState;
	}
	
	/**
	 * Gets if this client is considered valid or not.
	 * @return The validity of this client.
	 */
	protected boolean getValidSession() {
		return bValidSession;
	}

	/**
	 * Gets this client's Player.
	 * @return The Player, or null if the client does not yet have a Player.
	 */
	protected Player getPlayer() {
		return player;
	}
	
	/**
	 * Sets this client's currently active Player.
	 * @param player -- The Player.
	 */
	protected void addPlayer(Player player) {
		this.player = player;	
		this.player.setAccountID(iAccountID);
		
	}
	
	/**
	 * Sets the CRC encryption seed for all packet communications.
	 * @param seed -- The CRC seed.
	 */
	public void setCRCSeed(int seed) {
		iCRCSeed = seed;
	}
	
	/**
	 * Gets the CRC encryption seed for all packet communications.
	 * @return The CRC seed.
	 */
	protected int getCRCSeed() {
		return iCRCSeed;
	}
	
	/**
	 * Gets the Link-Dead status of the client.
	 */
	protected boolean getIsLD() {
		return bIsLD;
	}
	
	/**
	 * Sets the Link-Dead status of the client
	 * @param b -- The link-dead flag.
	 */
	protected void setIsLD(boolean b) {
		bIsLD = b;
	}
	
	/**
	 * Gets the maximum packet size for this session.
	 * @return The max packet size.
	 */
	protected int getMaxPacketSize() {
		return packetSize;
	}
	
	/**
	 * Sets the client tick rate.
	 * @param tick -- The tick rate.
	 */
	public void setReportedClientTick(short tick) {
		iLastTickCount = tick;
		iClientTicks += tick;
	}
	
	/**
	 * Sets the client's last update timespan for the network status message. 
	 * @param time -- The period of time, in milliseconds, between this network status message and the last one.
	 */
	public void setLastUpdateDeltaMS(int time) {
		iLastUpdateMS = time;
	}
	
	/**
	 * Sets the client's reported shortest network status message timespan.
	 * @param time The shortest period of time between network status messages.
	 */
	public void setShortestUpdateDeltaMS(int time) {
		iShortestUpdateMS = time;
	}
	
	/**
	 * Sets the client's reported average network status message timespan.
	 * @param time -- The average timespan between network status messages.
	 */
	public void setAverageUpdateDeltaMS(int time) {
		iAverageUpdateMS = time;
	}
	
	/**
	 * Sets the client's reported longest network status message timespan.
	 * @param time -- The longest timespan between network status messages.
	 */
	public void setLongestUpdateDeltaMS(int time) {
		iLongestUpdateMS = time;
	}
	
	/**
	 * Sets the reported last time the server responded to a network status message.
	 * @param time -- The time in milliseconds the server last responded.
	 */
	public void setKnownLastServerUpdateDeltaMS(int time) {
		iLastServerUpdateMS = time;
	}
	
	/**
	 * Sets the number of packets the client says it has received from the server.
	 * @param count -- The packet count.
	 */
	public void setClientPacketsReceived(int count) {
		iClientPacketsReceived = count;
	}
	
	/**
	 * Sets the number of packets the client says it has sent to the server.
	 * @param count -- The packet count.
	 */
	public void setClientPacketsSent(int count) {
		iClientPacketsSent = count;
	}
	
	/**
	 * Sets the session ID for this client session.
	 * @param ID -- The session ID.
	 */
	public void setSessionID(int ID){
		sessionID = ID;
	}
	
	/**
	 * Gets the session ID. 
	 * @return The session ID.
	 */
	public int getSessionID() {
		return sessionID;
	}
	
	/**
	 * Gets the last reported client tick count.
	 * @return The client tick count.
	 */
	public short getLastTickCount() {
		return iLastTickCount;
	}
	
	/**
	 * Gets the total number of client ticks for this session.
	 * @return The total client tick count.
	 */
	public short getTotalClientTickCount() {
		return iClientTicks;
	}
	
	/**
	 * Gets the last reported timespan between network status messages.
	 */
	public int getLastUpdateDeltaMS() {
		return iLastUpdateMS;
	}

	/**
	 * Gets the shortest reported timespan between network status messages.
	 */
	public int getShortestUpdateDeltaMS() {
		return iShortestUpdateMS;
	}
	
	/**
	 * Gets the average reported timespan between network status messages.
	 */
	public int getAverageUpdateDeltaMS() {
		return iAverageUpdateMS;
	}
	
	/**
	 * Gets the longest reported timespan between network status messages.
	 */
	public int getLongestUpdateDeltaMS() {
		return iLongestUpdateMS;
	}
	
	/**
	 * Gets the last timespan between server network status responses.\
	 */
	public int getKnownLastServerUpdateDeltaMS() {
		return iLastServerUpdateMS;
	}
	
	/**
	 * Gets the number of packets the client has received.
	 */
	public int getClientPacketsReceived() {
		return iClientPacketsReceived;
	}
	
	/**
	 * Gets the number of packets the client has sent.
	 */
	public int getClientPacketsSent() {
		return iClientPacketsSent;
	}
	
	/**
	 * Sets the outgoing packet sequence.
	 * @param sequence -- The outgoing sequence.
	 */
	public void setOutgoingSequence(short sequence) {
		System.out.println("Set outgoing sequence.  Currently " + serverSequence + ", set to " + sequence);
		serverSequence = sequence;
	}
	
	/**
	 * Gets the outgoing packet sequence.
	 * @return -- The outgoing sequence.
	 */
	public short getOutgoingSequence() {
		System.out.println("Get outgoing sequence.  Currently " + serverSequence);
		return serverSequence;
	}
	
	
	private int iBytesPendingFragA = 0;
	/**
	 * This function stores the first packet of a fragmented packet, so that it can be reconstituted into one single packet structure.
	 * @param buffer -- The first segnemt of the fragmented packet.
	 * @param totalLen -- The reported total size of the incoming packet data.
	 */
	public void startFragA(byte[] buffer, short sequence, int totalLen) throws IOException {
		
		dataFragABuffer = new SOEOutputStream(new ByteArrayOutputStream());
		dataFragABuffer.setOpcode(Constants.SOE_CHL_DATA_A); // For the sequence.
		dataFragABuffer.setSequence(sequence);
		dataFragABuffer.write(buffer);
		iBytesPendingFragA = totalLen - buffer.length;
		
		//System.out.println("Start frag a.  Total length; " + totalLen + ", length remaining: " + iBytesPendingFragA);
		//System.out.println("Estimated number of packets: " + ((totalLen / PacketUtils.MAX_PACKET_SIZE) + 1));
	}

	/**
	 * Adds a segment of a fragmented packet to the incoming packet fragment reconstitution object.
	 * @param buffer -- The incoming fragmented packet segment.
	 */
	public byte[] putFragA(byte[] buffer, boolean bForceReturn) throws IOException {
		
		dataFragABuffer.write(buffer);
		iBytesPendingFragA -= buffer.length;
		//System.out.println("Put middle frag.  Bytes left to receive: " + iBytesPendingFragA);
		if (iBytesPendingFragA <= 0 || bForceReturn) {
			System.out.println("Get reconstituted packet.");
			return getFragA();
		}
		return null;
	}
	
	/**
	 * Reconstitutes the incoming fragmented packet and returns it as a single packet for parsing.
	 * @param sequence -- The sequence of the last packet fragment.
	 * @return The reconstituted packet.
	 */
	private byte[] getFragA() throws IOException {
		if (dataFragABuffer == null) {
			return null;
		}
		dataFragABuffer.flush();
		byte[] buff = dataFragABuffer.getBuffer();
		dataFragABuffer = null;
		return buff;
	}
	
	public boolean getIsFragmenting() {
		return (dataFragABuffer != null);
	}

	/**
     * Inserts a packet into the players client packet queue based on range.
     * @param packet
     * @param range
     * @param player - The player to get CHATRANGE players around
     */
	public void insertPacket(byte[] packet, byte range, Player thePlayer) {
	    	if (player == null || packet == null) {
	    		return;
	    	}
            Vector<ZoneClient> vSendList = new Vector<ZoneClient>();
            switch(range)
            {
                case 0x01: // PACKET_RANGE_GROUP = 0x01;
                {                        
                    Group g = (Group)myServer.getObjectFromAllObjects(player.getGroupID());
                    Vector<Player> vGroupPlayers = g.getPlayerObjectsInGroup();
                    for (int i = 0; i < vGroupPlayers.size(); i++) {
                    	ZoneClient tarClient = vGroupPlayers.elementAt(i).getClient();
                    	if (tarClient != null) {
                    		if (tarClient.getClientReadyStatus()) {
                    			vSendList.add(tarClient);
                    		}
                    	}
                    }
                                                                    
                    break;
                }
                case 0x02: // PACKET_RANGE_GROUP_EXCLUDE_SENDER = 0x02;
                {
                    Group g = (Group)myServer.getObjectFromAllObjects(thePlayer.getGroupID());
                    Vector<Player> vGroupPlayers = g.getPlayerObjectsInGroup();
                    for (int i = 0; i < vGroupPlayers.size(); i++) {
                    	Player tarPlayer = vGroupPlayers.elementAt(i);
                    	if (thePlayer == null) {
                    		// "My" player is the sender.
                    		if (tarPlayer.getID() != player.getID()) {
                    			vSendList.add(tarPlayer.getClient());
                    		}
                    	} else {
                    		// thePlayer is the sender.
                    		if (tarPlayer.getID() != thePlayer.getID()) {
                    			vSendList.add(tarPlayer.getClient());			
                    		}
                    	}
                    }
                    break;
                }
                case 0x03: // PACKET_RANGE_CHAT_RANGE = 0x03;
                {
                    Vector<Player> vPL = myServer.getPlayersAroundObject(thePlayer, true);
                    for(int i = 0 ; i < vPL.size(); i++)
                    {
                        Player T = vPL.get(i);
                        ZoneClient tarClient = T.getClient();
                        if(ZoneServer.getRangeBetweenObjects(thePlayer,T) <= Constants.CHATRANGE) {
                        	if (tarClient != null) {
                        		if (tarClient.getClientReadyStatus() || tarClient == thePlayer.getClient()) {
                                    vSendList.add(tarClient);
                        		}
                        	}
                        		
                        }
                    }
                    
                    break;
                }
                case 0x04: //  PACKET_RANGE_CHAT_RANGE_EXCLUDE_SENDER = 0x04;
                {
                    Vector<Player> vPL = this.getServer().getPlayersAroundObject(thePlayer, false);
                    for(int i = 0 ; i < vPL.size(); i++)
                    {
                        Player T = vPL.get(i);
                        ZoneClient tarClient = T.getClient();
                        if (T.getID() != thePlayer.getID()) {
	                        if(ZoneServer.getRangeBetweenObjects(thePlayer,T) <= Constants.CHATRANGE && T.getClient().getClientReadyStatus())
	                        {                                   
	                        	if (tarClient != null) {
	                        		if (tarClient.getClientReadyStatus()) {
	                        			if (thePlayer != null) {
	                        				if (T.getID() != thePlayer.getID()) {
	    	                                    vSendList.add(tarClient);

	                        				}
	                        			} else {
	                        				if (T.getID() != player.getID()) {
	    	                                    vSendList.add(tarClient);
	                        				}
	                        			}
	                        		}
	                        	}
	                        }
	                    }
                    }
                    break;
                }
                case 0x05: //  PACKET_RANGE_PLANET = 0x05;
                {
                	Vector<Player> vAllPlayers = null;
                	if (thePlayer != null) {
                		vAllPlayers = myServer.getAllPlayersOnPlanet(thePlayer.getPlanetID());
                	} else {
                		vAllPlayers = myServer.getAllPlayersOnPlanet(player.getPlanetID());
                	}
                    for (int i = 0; i < vAllPlayers.size(); i++) {
                    	ZoneClient tarClient = vAllPlayers.elementAt(i).getClient();
                    	if (tarClient != null) {
                    		if (tarClient.getClientReadyStatus()) {
                    			vSendList .add(tarClient);
                    		}
                    	}
                    }
                    break;
                }
                case 0x06: //  PACKET_RANGE_PLANET_EXCLUDE_SENDER = 0x06;
                {
                	Vector<Player> vAllPlayers = null;
                	if (thePlayer != null) {
                		vAllPlayers = myServer.getAllPlayersOnPlanet(thePlayer.getPlanetID());
                		vAllPlayers.remove(thePlayer);
                	} else {
                		vAllPlayers = myServer.getAllPlayersOnPlanet(player.getPlanetID());
                		vAllPlayers.remove(player);
                	}
                    for (int i = 0; i < vAllPlayers.size(); i++) {
                    	ZoneClient tarClient = vAllPlayers.elementAt(i).getClient();
                    	if (tarClient != null) {
                    		if (tarClient.getClientReadyStatus()) {
                    			vSendList .add(tarClient);
                    		}
                    	}
                    }
                    break;
                }
                case 0x07: //  PACKET_RANGE_SERVER = 0x07;
                {
                    for(int i = 0; i < Constants.PlanetNames.length; i++)
                    {
                    	Vector<Player> vAllPlayers = myServer.getAllPlayersOnPlanet(i);
                        for (int j = 0; j < vAllPlayers.size(); j++) {
                        	ZoneClient tarClient = vAllPlayers.elementAt(i).getClient();
                        	if (tarClient != null) {
                        		if (tarClient.getClientReadyStatus()) {
                        			vSendList .add(tarClient);
                        		}
                        	}
                        }
                    }
                    break;
                }
                case 0x08: //  PACKET_RANGE_SERVER_EXCLUDE_SENDER = 0x08;
                {
                    for(int i = 0; i < Constants.PlanetNames.length; i++)
                    {
                    	Vector<Player> vAllPlayers = myServer.getAllPlayersOnPlanet(i);
                        for (int j = 0; j < vAllPlayers.size(); j++) {
                        	Player tarPlayer = vAllPlayers.elementAt(i);
                        	ZoneClient tarClient = tarPlayer.getClient();
                        	if (tarClient != null) {
                        		if (tarClient.getClientReadyStatus()) {
                        			if(thePlayer != null) {
                        				if (thePlayer.getID() != tarPlayer.getID()) {
                        					vSendList .add(tarClient);
                        				}
                        			} else {
	                    				if (player.getID() != tarPlayer.getID()) {
	                    					vSendList .add(tarClient);
	                    				}
                        			}                        			
                        		}
                        	}
                        }
                    }
                    break;
                }
                case 0x09: //  PACKET_RANGE_CLUSTER = 0x09;
                {
                    /**
                     * @todo code cluster range
                     */
                    break;
                }
                case 0x0A: //  PACKET_RANGE_CLUSTER_EXCLUDE_SENDER = 0x0A;
                {
                    /**
                     * @todo code cluster range
                     */
                    break;
                }
                case 0x0B: //  PACKET_RANGE_INSTANCE = 0x0B;
                {
                    /**
                     * @todo code instance range
                     */
                    break;
                }
                case 0x0C: //  PACKET_RANGE_INSTANCE_EXCLUDE_SENDER = 0x0C;
                {
                    /**
                     * @todo code instance range
                     */
                    break;
                }
                default:
                {
                    this.insertPacket(packet);
                }
            }
            if (vSendList.isEmpty()) {
            	System.out.println("ZoneClient::insertPacket -- Empty send list.");
            	//StackTraceElement[] vStackTrace = Thread.currentThread().getStackTrace();
            	//for (int i = 0; i < vStackTrace.length; i++) {
            	//	System.out.println(vStackTrace[i]);
            	//}
            	return;
            }
            for(int i = 0; i < vSendList.size();i++)
            {
                ZoneClient tarClient = vSendList.get(i);
                tarClient.insertPacket(packet);
            }                    
		
	}
        /**
         * Inserts a packet into the players client packet queue based on range.
         * @param packet
         * @param range
         */
        public void insertPacket(byte[] packet, byte range) {
        	insertPacket(packet, range, player);
        }
	/**
	 * Inserts a packet into the outgoing packet queue.
	 * @param packet -- The packet.
	 */
	public synchronized void insertPacket(byte[] packet) {
		if (packet != null)
        {
			short opcode = packet[1];
			if (opcode == Constants.SOE_CHL_DATA_A)
            {
				// Try to insert the sequence.
				packet[2] = (byte)(serverSequence << 8 & 0xFF);
				packet[3] = (byte)(serverSequence & 0xFF);
			}
			//packetsWaitingAcknowledgement.put(serverSequence, packet);
			packetQueue.add(packet);                          
		}
	}
	
	/**
	 * Inserts a batch of packets into the outgoing packet queue.
	 * @param packets -- The packets.
	 */
	public void insertAllPackets(Vector<byte[]> packets) {
		for (int i = 0; i < packets.size(); i++) {
			insertPacket(packets.elementAt(i));
		}
		//packets = null;
	}
	
	/**
	 * Sets the Station ID of this client.
	 * @param ID -- The Station ID.
	 */
	public void setStationID(int ID) {
		iStationID = ID;
	}

	/**
	 * Gets the Station ID of this client.
	 * @return The Station ID.
	 */
	public int getStationID() {
		return iStationID;
	}
	
	
	public void setClientAddress(SocketAddress address) {
		this.fullAddress = address;
		// Can we extract the IP address and the port separately?
	}
	
	public SocketAddress getClientAddress() {
		return fullAddress;
	}

	/**
	 * This function prepares packets for sending to the client.
	 * @throws Exception
	 */
	public synchronized void dequeuePacket() throws Exception{
		//System.out.print("Attempting to dequeue a packet from this zone client's queue.  ");
		//if (player != null) {
		//	System.out.print("Player name: " + player.getFullName() + ".  ");
		//System.out.println("Total number of queued packets: " + packetQueue.size());
		// FIRST -- Resolve out of order issues.
		if (iLastOutOrderReceived != 0) {
			System.out.println("Recover from out of order packet sent to client.  Resend from 0x" + Integer.toHexString(iLastAcknowledgedSequence).toUpperCase() + " to 0x" + Integer.toHexString(iLastOutOrderReceived).toUpperCase());
			resendPacketBeforeSequence(iLastOutOrderReceived);
		} else {
			while (hasPacketsToSend()) {
				try {
					// First, let's get the packet's buffer.
					byte[] buffer = getQueuedPacket();
		            //DataLogObject L;
					// Null check.
					
					if (buffer != null) {
		
						//boolean bIsAckPacket = (buffer[1] == 0x15);
						boolean bIncrementSequence = (buffer[1] == 0x09 || buffer[1] == 0x0D);
						if (buffer[1] == 2)  {
							// This ensures that a SOE SESSION RESPONSE is never, ever modified.
							//PacketUtils.printPacketData(buffer);
							myServer.queue(new DatagramPacket(buffer, buffer.length, fullAddress));
							iServerPacketsSent++;
		                                       // L = null;
		                                       /// L = new DataLogObject("ZoneClient().DequeuePacket 0: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
		                                       // this.getServer().logServer.logPacket(L);
						} else {
							// if (bIsAckPacket) System.out.println("Acknowledgement packet reached multipacketing / fragmenting");
							ByteArrayOutputStream bOut;
							SOEOutputStream dOut;
							
							// Now:  Do we want to fragment this packet?
							boolean bNeedToFragment = (buffer.length > (MAX_PACKET_SIZE - 3));
							
							// Can we add stuff to the end of this packet?  We obviously can't if we need to fragment this packet.
							
							boolean bCanMultipacket = ((!bNeedToFragment)
									&& (buffer.length < 0xFF)
									&& (!packetQueue.isEmpty())
									&& ((buffer.length + packetQueue.get(0).length) <= (MAX_PACKET_SIZE - 7) )
									&& (buffer[1] == 0x09)
									&& (buffer.length > 5)  // Don't multipacket it if it's already been, stupid.
							);
							
							//if (true) {
							//	bNeedToFragment = false;
							//	bCanMultipacket = false;
							//}
							SOEInputStream originalIn = new SOEInputStream(new ByteArrayInputStream(buffer));
							//originalIn.readShort(); // Opcode
							short originalOpcode = originalIn.getOpcode();
							
							//originalIn.skipBytes(2);
							
							// If we need to fragment this packet, let's do so.
							
							if (bNeedToFragment) {
								ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
								SOEOutputStream dataOut = new SOEOutputStream(byteOut);
								dataOut.setOpcode(Constants.SOE_DATA_FRAG_A);
								//System.out.println("Setting sequence = " + serverSequence + " for client ID " + getAccountID() + ", increment sequence? " + bIncrementSequence);
								dataOut.setSequence(serverSequence);
								byte[] fragData = originalIn.getBuffer();
								int offset = 488;
								byte[] fragment = Arrays.copyOfRange(fragData, 4, offset);
								dataOut.writeReversedInt(fragData.length - 4);
								dataOut.write(fragment);
								dataOut.flush();
								byte[] toSend = byteOut.toByteArray();
								//PacketUtils.printPacketData(toSend);
								if (bIncrementSequence) {
									//System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement. -- DATA_FRAG_A 1");
									packetsWaitingAcknowledgement.put(serverSequence, toSend);
									serverSequence++;
								}
		
									myServer.queue(PrepareForSendSWG(dataOut, toSend.length));
								iServerPacketsSent++;
								dataOut.close();
								byteOut.close();
		                       // L = null;
		                       // L = new DataLogObject("ZoneClient().DequeuePacket 6: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
		                       // this.getServer().logServer.logPacket(L);
								while (offset < (fragData.length - 4)) {
									byteOut = new ByteArrayOutputStream();
									dataOut = new SOEOutputStream(byteOut);
									dataOut.setOpcode(Constants.SOE_DATA_FRAG_A);
									//System.out.println("Setting sequence = " + serverSequence + " for client ID " + getAccountID() + ", increment sequence? " + bIncrementSequence);
									dataOut.setSequence(serverSequence);
									int endLocation = Math.min(fragData.length, offset + 488);
									fragment = Arrays.copyOfRange(fragData, offset, endLocation);
									offset += fragment.length;
									dataOut.write(fragment);
									dataOut.flush();
									toSend = byteOut.toByteArray();
									//PacketUtils.printPacketData(toSend);
									if (bIncrementSequence) {
								//		System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement. -- DATA_FRAG_A 2");
										packetsWaitingAcknowledgement.put(serverSequence, toSend);
										serverSequence++;
									}
										myServer.queue(PrepareForSendSWG(dataOut, toSend.length));
		
									//myServer.queue(PrepareForSendSWG(dataOut, toSend.length));
									iServerPacketsSent++;
									byteOut.close();
									dataOut.close();
		                                                        // Do this in dequeuePacket.
		                                                     //  L = null;
		                                                      //  L = new DataLogObject("ZoneClient().DequeuePacket 1: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
		                                                      //  this.getServer().logServer.logPacket(L);
								}
								
							} else if (bCanMultipacket) { 
								// Obsolete -- handles only SOE_CHL_DATA_A packets!
								// The first and the second packets we have, at least, can definitely be multipacketed.
								/*
								bOut = new ByteArrayOutputStream();
								dOut = new SOEOutputStream(bOut);
								dOut.setOpcode(Constants.SOE_CHL_DATA_A);
								dOut.setSequence(serverSequence);
								serverSequence++;
								dOut.setUpdateType(Constants.DATA_A_MULTI_PKT);
								dOut.writeByte(buffer.length);
								dOut.write(buffer, 4, buffer.length - 4);
								byte[] secondPacket = getQueuedPacket();
								dOut.writeByte(secondPacket.length - 4);
								dOut.write(secondPacket, 4, secondPacket.length - 4);
								while (!packetQueue.isEmpty() && (dOut.written < (MAX_PACKET_SIZE - 3)) && bCanMultipacket) {
								
									byte[] nextPacket = getQueuedPacket();
									int spaceLeftInPacket = MAX_PACKET_SIZE - 3 - dOut.written;
									bCanMultipacket = (spaceLeftInPacket >= (nextPacket.length - 4) && (nextPacket.length < 0xFF));
									if (!bCanMultipacket) {
										returnPacketToQueue(nextPacket);
									} else {
										dOut.writeByte(nextPacket.length - 4);
										dOut.write(nextPacket, 4, nextPacket.length - 4);
									}
								}
								dOut.flush();
								myServer.queue(PrepareForSendSWG(bOut, dOut.written));
								iServerPacketsSent ++;
								*/
								
								
								
								
								
								
								///*
								int iPacketCount = 1;
								// If we can multipacket this packet, we're assuming everything can go into 1 byte array.
								//System.out.println("Multipacketing packet with sequence " + sequence);
								
								//boolean bNeedToCompare = server_sequence == 1;
								int spaceLeftInBuffer = MAX_PACKET_SIZE - 9;
								byte[] nextPacketToAdd = getQueuedPacket();
								// If we can NOT multipacket:
								if (nextPacketToAdd == null || ((nextPacketToAdd.length + buffer.length) > spaceLeftInBuffer) || (buffer.length > 0x00FF)) {
									bCanMultipacket = false;
									/// Write out the current packet.
									bOut = new ByteArrayOutputStream();
									dOut = new SOEOutputStream(bOut);
									dOut.setOpcode(originalOpcode);
									//System.out.println("Setting sequence = " + serverSequence + " for client ID " + getAccountID() + ", increment sequence? " + bIncrementSequence);
									dOut.setSequence(serverSequence);
									dOut.write(buffer, 4, buffer.length - 4);
									dOut.flush();
									byte[] toSend = dOut.getBuffer();
									//PacketUtils.printPacketData(toSend);
									if (bIncrementSequence) {
									//	System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement. -- SOE_MULTI_PKT 1");
										packetsWaitingAcknowledgement.put(serverSequence, toSend);
										serverSequence++;
									}
										myServer.queue(PrepareForSendSWG(dOut, buffer.length));
		
									//myServer.queue(PrepareForSendSWG(dOut, bOut.toByteArray().length));
									iServerPacketsSent++;
		                                                       // L = null;
		                                                      //  L = new DataLogObject("ZoneClient().DequeuePacket 2: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
		                                                       // this.getServer().logServer.logPacket(L);
									if (nextPacketToAdd != null) {
										// We ran out of space in the current buffer.
										bOut = new ByteArrayOutputStream();
										dOut = new SOEOutputStream(bOut);
										dOut.setOpcode(originalOpcode);
										//System.out.println("Setting sequence = " + serverSequence + " for client ID " + getAccountID() + ", increment sequence? " + bIncrementSequence);
										dOut.setSequence(serverSequence);
										dOut.write(nextPacketToAdd, 4, nextPacketToAdd.length - 4);
										dOut.flush();
										toSend = bOut.toByteArray();
										//PacketUtils.printPacketData(toSend);
										if (bIncrementSequence) {
		//									System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement. -- SOE_MULTI_PKT 2");
											packetsWaitingAcknowledgement.put(serverSequence, toSend);
											serverSequence++;
										}
										myServer.queue(PrepareForSendSWG(dOut, toSend.length));
		
										//myServer.queue(PrepareForSendSWG(dOut, toSend.length));
										iServerPacketsSent++;
		                                                              //  L = null;
		                                                              //  L = new DataLogObject("ZoneClient().DequeuePacket 3: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
		                                                               // this.getServer().logServer.logPacket(L);
									}
									return;
									
								} else {
									// We CAN still multipacket.
									iPacketCount++;
									bOut = new ByteArrayOutputStream(MAX_PACKET_SIZE);
									dOut = new SOEOutputStream(bOut);
									dOut.setOpcode(Constants.SOE_CHL_DATA_A);
									// This is the sequence.
									//System.out.println("Setting sequence = " + serverSequence + " for client ID " + getAccountID() + ", increment sequence? " + bIncrementSequence);
									dOut.setSequence(serverSequence); // We'll use the sequence of the first packet we want to multipacket.
									dOut.setUpdateType(Constants.DATA_A_MULTI_PKT);
									dOut.writeByte((byte)(buffer.length - 4));
									spaceLeftInBuffer -= 7;
									dOut.write(buffer, 4, buffer.length - 4);
									spaceLeftInBuffer -= (buffer.length-4);
									
									bCanMultipacket = (nextPacketToAdd != null
											&& (spaceLeftInBuffer - (nextPacketToAdd.length-4) <= MAX_PACKET_SIZE-3 ) 
											&& nextPacketToAdd.length < 0x00FF 
											&& nextPacketToAdd.length > 4
											&& nextPacketToAdd[1] == 0x09);
									if (!bCanMultipacket) {
										if (nextPacketToAdd != null) {
											returnPacketToQueue(nextPacketToAdd);
										}
									}
									while (bCanMultipacket) {
		
										// If we can't multipacket any more, we need to return this packet we just dequeued to the front of the queue.
										//System.out.println("Adding this packet into the multipacket.  It's data: " + Arrays.toString(nextPacketToAdd));
										// If we can still put stuff into this packet, let's do it.
									
										// Now, for each additional packet after the first one, we're going to write:
										// byte (Packet size)
										// byte[] Packet Contents, starting at element 4, and going to element end.
											
										dOut.writeByte((byte)(nextPacketToAdd.length - 4));
										dOut.write(nextPacketToAdd, 4, nextPacketToAdd.length - 4);
										spaceLeftInBuffer -= (nextPacketToAdd.length-4);
										nextPacketToAdd = getQueuedPacket();
										bCanMultipacket = ((nextPacketToAdd != null) && (spaceLeftInBuffer > (nextPacketToAdd.length-4)  ) && (nextPacketToAdd.length - 4) < 0x00FF);
										if (!bCanMultipacket) {
											if (nextPacketToAdd != null) {
												returnPacketToQueue(nextPacketToAdd);
											} 
										}									
									}
									///bOut.write(0);
									//bOut.write(0);
									//bOut.write(0);
									byte[] toSend = bOut.toByteArray();
									//PacketUtils.printPacketData(toSend);
									if (bIncrementSequence) {
			//							System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement. -- SOE_MULTI_PKT 3");
										packetsWaitingAcknowledgement.put(serverSequence, toSend);
										serverSequence++;
									}
									myServer.queue(PrepareForSendSWG(dOut, toSend.length));
		
									///myServer.queue(PrepareForSendSWG(dOut, toSend.length));
									iServerPacketsSent++;
		                                                      //  L = null;
		                                                      //  L = new DataLogObject("ZoneClient().DequeuePacket 4: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
		                                                      //  this.getServer().logServer.logPacket(L);
								}//*/
							} else {
								//System.out.println("We cannot multipacket or fragment this packet -- sending it as a stand-alone");
								// Geez -- need to update the sequence!
								// Stand alone packet.
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								SOEOutputStream soeOut = new SOEOutputStream(out);
								soeOut.setOpcode(originalOpcode);
								//System.out.println("Setting sequence = " + serverSequence + " for client ID " + getAccountID() + ", increment sequence? " + bIncrementSequence);
								
								soeOut.setSequence(serverSequence);
								if (buffer.length > 4) {
									soeOut.write(buffer, 4, buffer.length - 4);
								}
								soeOut.flush();
								//PacketUtils.printPacketData(toSend);
								if (bIncrementSequence) {
				//					System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement. -- NORMAL_SEND");
									packetsWaitingAcknowledgement.put(serverSequence, buffer);
									serverSequence++;
								}
								myServer.queue(PrepareForSendSWG(soeOut, buffer.length));
		
								//myServer.queue(PrepareForSendSWG(soeOut, buffer.length));
								iServerPacketsSent++;
		                                               // L = null; 
		                                               // L = new DataLogObject("ZoneClient().DequeuePacket 5: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
		                                               // this.getServer().logServer.logPacket(L);
							}
						}
					} 
				} catch (Exception e) {
					System.out.println("Exploded in Client dequeuePacket: " + e.toString());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This gets a packet from the outgoing packet queue, for preparing it to be sent.
	 * @return The next outbound packet.
	 */
	private synchronized byte[] getQueuedPacket() {
		if (packetQueue.isEmpty()) {
			//System.out.println("Packet queue for client ID " + getAccountID() + " is empty!");
			return null;
		} else {
			try {
				byte[] toReturn = packetQueue.remove(0);
				return toReturn;
			} catch (ArrayIndexOutOfBoundsException ee) {
				System.out.println("getQueuedPacket() -- How are you happening? " + ee.toString());
				ee.printStackTrace();
				System.out.println("Is packet queue empty?" + packetQueue.isEmpty());
				System.out.println("Packet queue size: " + packetQueue.size());
			} catch (Exception e) {
				System.out.println("Exception in getQueuedPacket -- " + e.toString());
				e.printStackTrace();
			}
		}
		System.out.println("An exception was thrown but not printed -- returning null.");
		// This should never happen.
		return null;
	}

	/**
	 * This function performs the work of compressing and encoding an outgoing packet.
	 * @param buf -- The outbound packet.
	 * @param nLength -- The length of the outbound packet.
	 * @return -- A DatagramPacket object which may be sent to the client.
	 */
    protected DatagramPacket PrepareForSendSWG(SOEOutputStream buf, int nLength) {
    	DatagramPacket dOut = null;
    	ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
    	byte[] newOutput = null;
    	byte[] original = buf.getBuffer();
        
        DataLogObject L = new DataLogObject("ZoneClient().PrepareForSendSWG","SourceIP",original,fullAddress.toString(),Constants.LOG_PACKET_DIRECTION_OUT);
        DataLog.logPacket(L);
        
    	byte[] encrypted = null;
    	boolean comp = ((original[1] != 2 && nLength > 80) || original[1] == 8);
    	boolean enc = (original[1] != 2); 
    	// We take the original.  If we want to compress it, compress it.  If we want to encrypt it, encrypt it.
    	// If we want a crc, append it.
    	// NOTE:  If it's compressed, we must encrypt the compressed buffer, not the original.
    	try {
			newOutput = PacketUtils.compress(original, comp);
	    	// If we're not encoding this, shove it into a new output stream.
	    	// If we're not encoding this, it's most likely a SOE_SESSION_RESPONSE, and therefore has not been compressed.
			buf2 = new ByteArrayOutputStream();

	    	if (!enc) {
				buf2.write(newOutput);
			} else {
				encrypted = PacketUtils.Encrypt(newOutput, newOutput.length, iCRCSeed);
    			buf2.write(encrypted);
    			buf2 = PacketUtils.AppendCRC(buf2, buf2.size(), iCRCSeed);
    		}
	    	
	    	
	    	dOut = new DatagramPacket(buf2.toByteArray(), buf2.size(), fullAddress);
	    	return dOut;
    	} catch (Exception e) {
    		System.out.println("Error in SendSWG: " + e.toString());
    		e.printStackTrace();
    	}
    	System.out.println("PrepareForSendSWG:  This should never happen -- returning null");
    	return null;
    }

    /**
     * This returns a packet to the front of the outgoing packet queue.
     * @param packet -- The packet to return to the queue.
     */
	private synchronized void returnPacketToQueue(byte[] packet) {
		try {
			packetQueue.add(0, packet);
		} catch (Exception e) {
			System.out.println("Error returning a packet to queue -- it is lost: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Sets the inbound client sequence.
	 * @param sequence -- The inbound sequence.
	 * @return If the sequence is incremental (IE if we should actually process the packet);
	 */
	protected boolean setClientSequence(short sequence) {
		int oldSequence = clientSequence;
		clientSequence = sequence;
		return (sequence == (oldSequence + 1)); 
	}
	
	/**
	 * Gets the inbound client sequence.
	 * @return The inbound sequence.
	 */
	protected short getClientSequence() {
		return clientSequence;
	}
	
	/**
	 * Gets the number of packets the server has sent to this client.
	 * @return Outbound packet count.
	 */
	protected int getServerPacketsSent() {
		return iServerPacketsSent;
	}
	
	/**
	 * Gets the number of packets the server has received from the client.
	 * @return Inbound packet count.
	 */
	protected int getServerPacketsReceived() {
		return iServerPacketsReceived;
	}
	
	/**
	 * Increments the inbound packet count.
	 */
	protected void incrementServerPacketsReceived() {
		iServerPacketsReceived++;
	}
	
	/**
	 * Gets the Zone Server which this client is active on.
	 * @return The Zone Server.
	 */
	protected ZoneServer getServer() {
		return myServer;
	}
        
        /**
         * This option sets a flag in the client that tells us if the client is
         * ready to receive packets after zoning in.
         */
        protected void setClientReady(){
            clientReady = true;
        }
        
        protected void setClientNotReady(){
            clientReady = false;
        }

        /**
         * Returns if the client is ready to receive packets after zoning
         * @return boolean - true if client is ready to receive packets after zoning
         */
        protected boolean getClientReadyStatus(){
            return clientReady;
        }
        
        /**
         * Returns the current email sequence.
         * @return int - the email sequence of this player
         */
        protected int getEmailSequence(){
            return iEmailSequence;
        }
        
        /**
         * sets the email sequence for this player
         * @param S int - the email sequence.
         */
        protected void setEmailSequence(int S){
            iEmailSequence = S;
        }
        
        /**
         * Increments the Email Sequence
         * @return int - returns the incremented emailsequence
         */
        protected int incrementEmailSequence(){
            iEmailSequence++;
            return iEmailSequence;
        }  
        
        protected void clearPacketQueues() {
        	incomingPacketsToParse.clear();
        	packetQueue.clear();
        	packetsWaitingAcknowledgement.clear();
        }
        
        private boolean bHasNullOutOrderPackets = false;
        /**
         * Re-sends the packet sequentially previous to the reported sequence, and set the current server sequence equal to the sequence reported out of order.
         * @param sequence -- the sequence which is out of order.
         */
        protected void resendPacketBeforeSequence(short sequence) {
        	System.out.println("Resend packets before sequence 0x"+Integer.toHexString(sequence).toUpperCase() + ", last acknowledged: 0x"+Integer.toHexString(iLastAcknowledgedSequence).toUpperCase());
        	for (short i = (short)(iLastAcknowledgedSequence + 1); i < sequence; i++) {
        		byte[] packetToResend = packetsWaitingAcknowledgement.get(i);
        		packetToResend[2] = (byte)((i & 0xFF00) >> 8);
        		packetToResend[3] = (byte)(i & 0xFF);
        		if (packetToResend != null) {
	        		try { 
	        			//PacketUtils.printPacketToScreen(packetToResend, "Resend packet " + i + ", sequence 0x" + Integer.toHexString((packetToResend[2] << 8 | packetToResend[3])).toUpperCase());
	        			SOEOutputStream dOut = new SOEOutputStream(new ByteArrayOutputStream());
	        			dOut.write(packetToResend);
	        			myServer.queue(PrepareForSendSWG(dOut, packetToResend.length));
	        			System.out.println("Resent packet with sequence 0x" + Integer.toHexString(i).toUpperCase());
		        		//serverSequence = sequence;
		        	} catch (Exception e) {
		        		System.out.println("Error processing out of order packet: " + e.toString());
		        		e.printStackTrace();
		        	}
        		} else {
        			System.out.println("Null packet to resend with sequence 0x"+Integer.toHexString(i));
        		}
        	}
        	iLastOutOrderReceived = 0;
        	//System.exit(0);
        	/*byte[] packetToResend = packetsWaitingAcknowledgement.get((short)(sequence - 1));
        	if (packetToResend != null) {
        		//System.out.println(player.getFirstName() + " has packet waiting for acknowledgement with sequence " + (sequence - 1) + ", resending.");
        		try { 
        			DatagramPacket dOut = new DatagramPacket(packetToResend, packetToResend.length, fullAddress);
	        		myServer.queue(dOut);
	        		serverSequence = sequence;
	        	} catch (Exception e) {
	        		System.out.println("Error processing out of order packet: " + e.toString());
	        		e.printStackTrace();
	        	}
        	}else {
        		System.out.println(player.getFirstName() + " is attempting to resend null packet with sequence " + (sequence - 1));
        		if (bHasNullOutOrderPackets == false) {
        			serverSequence = (short)(iLastAcknowledgedSequence + 1);
        			bHasNullOutOrderPackets = true;
        		}
        	}*/
        	//System.out.flush();
        }
        
        
        
        protected void setHasNullOutOrderPacket(boolean state) {
        	bHasNullOutOrderPackets = state;
        }

		public void setLastOutOrderReceived(short iLastOutOrderReceived) {
			if ((iLastOutOrderReceived < this.iLastOutOrderReceived) || (this.iLastOutOrderReceived == 0)) {
				if (iLastOutOrderReceived > iLastAcknowledgedSequence) {
					this.iLastOutOrderReceived = iLastOutOrderReceived;		
				}
			}
		}

		public short getLastOutOrderReceived() {
			return iLastOutOrderReceived;
		}
        
 }

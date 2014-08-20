import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ZoneLoginTransciever is responsible for all ZoneServer-side communication between the ZoneServer and the LoginServer.
 * @author DarrylF
 *
 */
public class ZoneLoginTransciever implements Runnable{
	private Socket communicationSocket;
	private DataInputStream dIn;
	private DataOutputStream dOut;
	private ZoneServer server;
	private boolean bAuthenticated = false;
	private long lEstimatedPingMS;
	private Thread myThread;
	private ConcurrentHashMap<Long, AccountData> vAccounts;
	private long[] vPendingAccountIDs;
	private boolean bLostConnection = false;
	private InetAddress remoteAddress;
	private int remotePort;
	//private long lastWrittenValue = -1;
	public ZoneLoginTransciever(ZoneServer server, InetAddress remoteAddress, int remotePort) throws IOException {
		this.server = server;
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		communicationSocket = new Socket(remoteAddress, remotePort);
		communicationSocket.setKeepAlive(true);
		communicationSocket.setSoTimeout(2000);
		//communicationSocket.setTcpNoDelay(true);
		dOut = new DataOutputStream(communicationSocket.getOutputStream());
		dIn = new DataInputStream(communicationSocket.getInputStream());
		vPendingAccountIDs = new long[10]; // Can only be waiting on 10 accounts at a time.
		vAccounts = new ConcurrentHashMap<Long, AccountData>();
		myThread = new Thread(this);
	}
	
	public void authenticate() throws IOException {
		dOut.writeByte(Constants.SERVER_CONNECTION_REQUEST);
		dOut.writeInt(server.getServerID());
		dOut.flush();
		byte returnOpcode = dIn.readByte();
		if (returnOpcode != Constants.SERVER_CONNECTION_RESPONSE) {
			// Something went wrong.
			DataLog.logEntry("Invalid communication from Login Server.","ZoneLoginTransciever:authenticate()",Constants.LOG_SEVERITY_CRITICAL,true,true);
		} else {
			bAuthenticated= dIn.readBoolean();
			if (!bAuthenticated) {
				//System.out.println("Connection refused by Login Server:  A Zone Server with id " + server.getServerID() + " has already connected.");
                DataLog.logEntry("Connection refused by Login Server:  A Zone Server with id " + server.getServerID() + " has already connected.","ZoneLoginTransciever",Constants.LOG_SEVERITY_CRITICAL,true,true);
                while(DataLog.logWritePending())
                {
                	// Do nothing.
                }
				System.exit(-1);
			} else {
				
                //System.out.println("Connected to Login Server.");
                DataLog.logEntry("Connected to Login Server.","ZoneLoginTransciever",Constants.LOG_SEVERITY_CRITICAL,true,true);
				//myThread.start();
				volunteerStatusChange();
				try {
					myThread.start();
					estimatePing();
				} catch (Exception e) {
                    DataLog.logException("Error calculating ping... default timeout of 2 seconds will be used.", "ZoneLoginTransciever", true, true, e);
                    //System.out.println("Error calculating ping... default timeout of 2 seconds will be used.");
					//System.out.println("Error: " + e.toString());
					//e.printStackTrace();
				}
			}
		}
	}

	public AccountData readAccountDataFromLoginServer() {
		try {
			boolean bFoundAccount = dIn.readBoolean();
			if (bFoundAccount) {
				//DataLog.logEntry("Remote LoginServer found account data.","ZoneLoginTransciever.readAccountDataFromLoginServer",Constants.LOG_SEVERITY_INFO,true,true);
				ObjectInputStream oIn = new ObjectInputStream(dIn);
				AccountData data = (AccountData)oIn.readObject();
				vAccounts.put(data.getAccountID(), data);
				//DataLog.logEntry("Read account data.  Username: " + data.getUsername(),"ZoneLoginTransciever.readAccountDataFromLoginServer",Constants.LOG_SEVERITY_INFO,true,true);
                //System.out.println("Read account data.  Username: " + data.getUsername());
				oIn = null;
				// Read the account data -- need to clear the array.
				for (int i = 0; i < vPendingAccountIDs.length; i++) {
					if (vPendingAccountIDs[i] == data.getAccountID()) {
						vPendingAccountIDs[i] = 0;
					}
				}
				return data;
			} else {
				//System.out.println("Remote LoginServer did not find account data.");
                //DataLog.logEntry("Remote LoginServer did not find account data.","ZoneLoginTransciever.readAccountDataFromLoginServer",Constants.LOG_SEVERITY_INFO,true,true);
				return null;
			}
		} catch (Exception e) {
            DataLog.logException("ZoneLoginTransciever exploded reading account data from remote Login Server", "ZoneLoginTransciever",true, true, e);
			//System.out.println("ZoneLoginTransciever exploded reading account data from remote Login Server: " + e.toString());
			//e.printStackTrace();
			return null;
		}
			
	}
	
	protected AccountData getAccountDataFromLoginServer(long accountID) {
		if (vAccounts.containsKey(accountID)) {
			return vAccounts.get(accountID);
		} else {
			// We need to know if we've requested the account ID already, so we don't spam the LoginServer.
			int firstZeroValue = 0;
			for (int i = vPendingAccountIDs.length - 1; i >= 0; i--) {
				
				if (vPendingAccountIDs[i] == accountID) {
					return null; // Still waiting.
				} else if (vPendingAccountIDs[i] == 0) {
					firstZeroValue = i; // Put it in "here"
				}
			}
			vPendingAccountIDs[firstZeroValue] = accountID;
			requestAccountDataFromLoginServer((int)accountID);
			return null; // First time waiting.
		}
	}
	
	public void requestAccountDataFromLoginServer(int accountID) {
		try {
			dOut.writeByte(Constants.ZONE_SERVER_REQUEST_ACCOUNT_DATA);
			dOut.writeInt(accountID);
			dOut.flush();
		} catch (Exception e) {
			//System.out.println("ZoneLoginTransciever exploded reading account data from remote Login Server: " + e.toString());
            DataLog.logException("ZoneLoginTransciever exploded reading account data from remote Login Server", "ZoneLoginTransciever",true, true, e);
			e.printStackTrace();
		}
	}
	
	public void sendLoadNewPlayerTrigger(long playerID) throws IOException {
		dOut.writeByte(Constants.ZONE_SERVER_SAVED_NEW_CHARACTER);
		dOut.writeLong(playerID);
		dOut.flush();
	}
	
	public void terminate() {
		try {
			dOut.writeByte(Constants.SERVER_DISCONNECT);
			dOut.flush();
			dOut.close();
			dOut = null;
			dIn.close();
			dIn = null;
			communicationSocket.close();
			communicationSocket = null;
		} catch (Exception e) {
			// Must already be closed.
		} finally {
			myThread = null;
		}
	}
	
	public void volunteerStatusChange() {
		try {
            DatabaseInterface.updateGalaxyStatus(server.getServerID(), server.getStatus(), server.getClientConnectionCount());
			dOut.writeByte(Constants.SERVER_VOLUNTEER_UPDATE_STATUS);
			dOut.writeInt(server.getStatus());
			dOut.flush();
		} catch (Exception e) { 
			System.out.println("Error volunteering status change: " + e.toString());
			e.printStackTrace();
		}
	}
	
	private void estimatePing() {
		long[] pingTimes = new long[100];
		byte response = 0;
		synchronized(dIn) {
			for (int i = 0; i < pingTimes.length; i++) {
				long lNanoTimeBeforePing = System.nanoTime();
				try {
					dOut.writeByte(Constants.SERVER_TO_SERVER_PING);
					response = dIn.readByte(); // Should be server pong.
					long lNanoTimeAfterPing = System.nanoTime();
					long lNanoDifference = lNanoTimeAfterPing - lNanoTimeBeforePing;
					//System.out.println("Pong byte = " + response);
					pingTimes[i] = lNanoDifference;
				} catch (Exception e) {
					// Oh well.
					DataLog.logException("Error in estimatePing.", "ZoneLoginTransciever",true, true, e);
                    //System.out.println("Error in estimatePing: " + e.toString());
					//e.printStackTrace();
				}
				DataLog.logEntry("Ping["+i+"] = " + pingTimes[i] + "nanoseconds.", "ZoneLoginTransciever", Constants.LOG_SEVERITY_INFO, true, false);
				//System.out.println("Ping["+i+"] = " + pingTimes[i] + "nanoseconds.");
			}
		}
		long totalTime = 0;
		int iNumPings = 0;
		for (int i = 0; i < pingTimes.length; i++) {
			if (pingTimes[i] != 0) {
				iNumPings++;
			}
			totalTime+=pingTimes[i];
		}
		//System.out.println("Total elapsed time = " + totalTime);
        DataLog.logEntry("Total elapsed time = " + totalTime, "ZoneLoginTransciever", Constants.LOG_SEVERITY_INFO, true, true);
		lEstimatedPingMS = (totalTime / 1000000) / iNumPings;
		//System.out.println("Average ping(ms) = " + lEstimatedPingMS);
        DataLog.logEntry("Average ping(ms) = " + lEstimatedPingMS, "ZoneLoginTransciever", Constants.LOG_SEVERITY_INFO, true, true);
		try {
			//communicationSocket.setSoTimeout((int)lEstimatedPingMS * 2);
			dOut.writeByte(Constants.SERVER_SEND_ESTIMATED_PING);
			dOut.writeInt((int)lEstimatedPingMS);
			dOut.flush();
		} catch (Exception e) {
            DataLog.logException("Error sending average ping", "ZoneLoginTransciever", true, true, e);
			//System.out.println("Error sending average ping: " + e.toString());
			//e.printStackTrace();
		}
	}
	
	public void run() {
		while (myThread != null) {
			if (bLostConnection) {
				try {
					communicationSocket = new Socket(remoteAddress, remotePort);
					if (communicationSocket.isConnected()) {
						dOut = new DataOutputStream(communicationSocket.getOutputStream());
						dIn = new DataInputStream(communicationSocket.getInputStream());
						communicationSocket.setKeepAlive(true);
						communicationSocket.setSoTimeout(2000);
						authenticate();
						estimatePing();
						volunteerStatusChange();
						server.sendNotifyLoginReconnect();
						bLostConnection = false;
					} else {
						communicationSocket.close();
						communicationSocket= null;
					}
				} catch (Exception e) {
					// Couldn't connect.
				}
			} else {
				try {
                    Thread.yield();
					byte opcode = -1;
					synchronized(dIn) {
						opcode = dIn.readByte();
						switch (opcode) {
							case Constants.SERVER_REQUEST_STATUS: {
								volunteerStatusChange();
								//System.out.println("Zone received request status update from login.");
								break;
							}
							case Constants.SERVER_RESPOND_ACCOUNT_DATA: {
								readAccountDataFromLoginServer();
								break;
							}
							case Constants.SERVER_DISCONNECT: {
								server.sendNotifyLoginDisconnect();
								bLostConnection = true;
								break;
							}
							case Constants.SERVER_ERROR_ON_TCP: {
								DataLog.logEntry("Login Server sent TCP error", "ZoneLoginTransciever", Constants.LOG_SEVERITY_CRITICAL, true, true);
								//bLostConnection = true;
								terminate();
								break;
							}
							case Constants.SERVER_LOGIN_SEND_FRIEND_CHANGE_STATUS: {
								DataLog.logEntry("External player changing status.", "ZoneLoginTransciever", Constants.LOG_SEVERITY_INFO, true, true);
								handleRemotePlayerChangedStatus(dIn);
								break;
							}
							default: {
                                DataLog.logEntry("Zone read " + opcode + " from login.","ZoneLoginTransciever", Constants.LOG_SEVERITY_CRITICAL, true, true);
								//System.out.println("Zone read " + opcode + " from login.");
							}
						}
					}				
				} catch (Exception e) {
					// D'oh!
				}
			}
		}
	}
	
	protected void sendFriendUpdatedStatus(Player player, boolean status) throws IOException {
		dOut.writeByte(Constants.SERVER_ZONE_SEND_FRIEND_CHANGE_STATUS);
		dOut.writeInt(player.getServer().getServerID());
		dOut.writeUTF(player.getServer().getClusterName());
		dOut.writeUTF(player.getFirstName());
		dOut.writeBoolean(status);
		dOut.flush();
	}
	
	private void handleRemotePlayerChangedStatus(DataInputStream dIn) throws IOException {
		String sClusterName = dIn.readUTF();
		String sPlayerName = dIn.readUTF();
		boolean status = dIn.readBoolean();
		DataLog.logEntry("Player " + sPlayerName + " from cluster " + sClusterName + " has new logged in status of " + status, "ZoneLoginTransciever", Constants.LOG_SEVERITY_INFO, true, true);
	}
}

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class LoginZoneCommunicationThread implements Runnable {
	private Socket communicationSocket;
	private Thread myThread;
	private DataInputStream dIn;
	private DataOutputStream dOut;
	private LoginServer server;
	private int zoneServerID;
	private int lastStatus = Constants.SERVER_STATUS_OFFLINE;
	private long lCurrentTimeMS;
	private long lLastTimeMS;
	private long lDeltaTimeMS;
	private long lEstimatedPingMS;
	private boolean bSkippedLastByte = false;
	public LoginZoneCommunicationThread(LoginServer server, Socket socket) throws IOException {
		this.server = server;
		communicationSocket = socket;
		socket.setKeepAlive(true);
		//socket.setTcpNoDelay(true);
		//socket.setSoTimeout(2000);
		myThread = new Thread(this);
		initialize();
	}
	
	private void initialize() throws IOException {
		dOut = new DataOutputStream(communicationSocket.getOutputStream());
		dIn = new DataInputStream(communicationSocket.getInputStream());
		myThread.start();
	}
	
	public void run() {
		lLastTimeMS = System.currentTimeMillis();
		while (myThread != null) {
			try {
                Thread.yield();
				lCurrentTimeMS = System.currentTimeMillis();
				lDeltaTimeMS = lCurrentTimeMS - lLastTimeMS;
				lLastTimeMS = lCurrentTimeMS;
				update(lDeltaTimeMS);
				//dIn.readByte();
			} catch (UnhandledOpcodeException ee) {
				try {
					DataLog.logEntry("UnhandledOpcodeException.  Bytes available to be read: " + dIn.available(), "LoginZoneCommunicationThread", Constants.LOG_SEVERITY_CRITICAL, true, false);
				} catch (IOException e) {
					// D'oh!
				} finally {
					DataLog.logException(ee.toString() + " -- Opcode = " + ee.getOpcode(), "LoginZoneCommunicationThread " + zoneServerID, true, true, ee);
					sendTCPError();
				}
			} catch (Exception e) {
				// D'oh!
			}
		}
	}

	private void update(long lDeltaTimeMS) throws IOException, UnhandledOpcodeException {
		byte opcode = dIn.readByte();
		switch (opcode) {
			case Constants.SERVER_CONNECTION_REQUEST: {
				bSkippedLastByte = false;
				handleConnectionRequest();
				break;
			}
			case Constants.SERVER_CONNECTION_RESPONSE: {
				bSkippedLastByte = false;
				// Should never happen.
				break;
			}
			case Constants.ZONE_SERVER_REQUEST_ACCOUNT_DATA: {
				bSkippedLastByte = false;
				handleRequestAccountData();
				break;
			}
			case Constants.ZONE_SERVER_SAVED_NEW_CHARACTER: {
				bSkippedLastByte = false;
				handleSavedNewCharacter();
				break;
			}
			case Constants.SERVER_DISCONNECT: {
				bSkippedLastByte = false;
				handleDisconnect();
				break;
			}
			case Constants.SERVER_VOLUNTEER_UPDATE_STATUS: {
				bSkippedLastByte = false;
				handleZoneVolunteerStatus();
				break;
			}
			case Constants.SERVER_SEND_ESTIMATED_PING: {
				bSkippedLastByte = false;
				handleEstimatePing();
				break;
			}
			case Constants.SERVER_TO_SERVER_PING: {
				bSkippedLastByte = false;
				//System.out.println("Received ping.  Sending pong...");
				//communicationSocket.sendUrgentData(Constants.SERVER_TO_SERVER_PONG);
				dOut.writeByte(Constants.SERVER_TO_SERVER_PONG);
				dOut.flush();
				//System.out.println("Sent pong");
				break;
			}
			case Constants.SERVER_ZONE_SEND_FRIEND_CHANGE_STATUS: {
				int serverID = dIn.readInt();
				String serverName = dIn.readUTF();
				String friendName = dIn.readUTF();
				boolean bOnline = dIn.readBoolean();
				server.forwardFriendChangedStatus(serverID, serverName, friendName, bOnline);
				break;
			}
			case 0: {
				if (bSkippedLastByte) {
					throw new UnhandledOpcodeException("Unhandled opcode from Zone Server after skipping previous 0 byte.", opcode); 
				} else {
					bSkippedLastByte = true;
					
				}
				break;
			}
			default: {
				throw new UnhandledOpcodeException("Unhandled opcode from Zone Server ID " + zoneServerID, opcode); 
                //DataLog.logEntry("Unhandled opcode from Zone Server : " + opcode,"LoginZoneCommunicationThread", Constants.LOG_SEVERITY_CRITICAL,true, false);
				//System.out.println("Unhandled opcode from Zone Server : " + opcode);
			}
		}
	}

	protected void handleConnectionRequest() throws IOException {
		zoneServerID = dIn.readInt();
		//System.out.println("Connection request from Zone Server with ID " + zoneServerID);
        DataLog.logEntry("Connection request from Zone Server with ID " + zoneServerID,"LoginZoneCommunicationThread", Constants.LOG_SEVERITY_INFO,true, true);
		dOut.writeByte(Constants.SERVER_CONNECTION_RESPONSE);
		boolean bAllowConnect = !server.hasZoneServerConnected(zoneServerID);
		dOut.writeBoolean(bAllowConnect);
		dOut.flush();
		if (bAllowConnect) {
			//System.out.println("Connection approved.");
            DataLog.logEntry("Connection approved. " + zoneServerID,"LoginZoneCommunicationThread", Constants.LOG_SEVERITY_INFO,true, true);
			server.addZoneServerCommunicationThread(this, zoneServerID);
		} else {
            DataLog.logEntry("Connection declined. " + zoneServerID,"LoginZoneCommunicationThread", Constants.LOG_SEVERITY_CRITICAL,true, true);
			//System.out.println("Connection declined.");
		}
	}
	
	protected void handleRequestAccountData() throws IOException{
		int accountID = dIn.readInt();
		AccountData account = server.getAccountData(accountID);
		dOut.writeByte(Constants.SERVER_RESPOND_ACCOUNT_DATA);
		if (account != null) {
			dOut.writeBoolean(true);
			ObjectOutputStream oOut = new ObjectOutputStream(dOut);
			oOut.writeObject(account);
		} else {
			dOut.writeBoolean(false);
		}
		dOut.flush();
	}
	
	protected void handleSavedNewCharacter() throws IOException {
		long playerID = dIn.readLong();
		//System.out.println("LoginServer received request to load new player with ID " + Long.toHexString(playerID));
		server.loadNewPlayer(playerID, zoneServerID);
		//boolean bLoadedPlayer = server.loadNewPlayer(playerID, zoneServerID); 
		//dOut.writeBoolean(bLoadedPlayer);
	}
	
	protected void handleDisconnect() {
        DataLog.logEntry("Received disconnect from Zone Server with ID " + zoneServerID,"LoginZoneCommunicationThread", Constants.LOG_SEVERITY_CRITICAL,true, true);
		//System.out.println("Received disconnect from Zone Server with ID " + zoneServerID);
		server.removeZoneServerCommunicationThread(zoneServerID);
		myThread = null;
	}
	
	protected int getServerStatus() {
		return lastStatus;
 	}
	
	protected void handleZoneVolunteerStatus() {
		try {
			int newStatus = dIn.readInt();
			if (newStatus != lastStatus) {
				server.sendUpdateServerStatus();
	            lastStatus = newStatus;
			}
			//System.out.println("Received Zone status -- status now " + lastStatus);
		} catch (Exception e) {
			DataLog.logException("Error reading Zone Server status: " + e.toString(), "LoginZoneCommunicatonThread:handleZoneVolunteerStatus()", true, false, e);
		}
	}

	protected void handleEstimatePing() {
		try {
			lEstimatedPingMS = dIn.readInt();
			lEstimatedPingMS = Math.max(25, lEstimatedPingMS);
			//communicationSocket.setSoTimeout((int)(lEstimatedPingMS * 2));
		} catch (Exception e) {
			// D'oh!
		}
	}
	
	protected void requestServerStatus() {
		try {
            //System.out.println("Request update server status.");
			dOut.writeByte(Constants.SERVER_REQUEST_STATUS);
			dOut.flush();
			//System.out.println("Request sent.");
		} catch (Exception e) {
            DataLog.logException("Error requesting zone server status", "LoginZoneCommunicationThread", true, true, e);
			//System.out.println("Error requesting zone server status: " + e.toString());
			//e.printStackTrace();
		}
	}
	
	protected void sendNotifyShutdown() {
		try {
			dOut.writeByte(Constants.SERVER_DISCONNECT);
			dOut.flush();
			dOut.close();
			dOut = null;
			dIn.close();
			dIn = null;
			myThread = null;
			communicationSocket.close();
			communicationSocket = null;
		} catch (Exception e) {
			dOut = null;
			dIn = null;
			myThread = null;
		}
	}
	
	protected void sendTCPError() {
		try {
			dOut.writeByte(Constants.SERVER_ERROR_ON_TCP);
			dOut.flush();
			sendNotifyShutdown();
		} catch (Exception e) {
			DataLog.logException("Could not send error message to zone", "LoginZoneCommunicationThread::sendTCPError", true, true, e);
		}
	}
	
	protected void sendPlayerUpdatedStatus(String sClusterName, String sPlayerName, boolean status) {
		try {
			dOut.writeByte(Constants.SERVER_LOGIN_SEND_FRIEND_CHANGE_STATUS);
			dOut.writeUTF(sClusterName);
			dOut.writeUTF(sPlayerName);
			dOut.writeBoolean(status);
			dOut.flush();
		} catch (IOException e) {
			// D'oh!
		}
	}
	
}

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Hashtable;
import javax.script.ScriptException;

public class ZoneClientThread implements Runnable {
	private ZoneServer server;
	private Thread myThread;
	private ZoneClient client;
	private Player player;
	private AccountData account;
	private boolean bIsGM = false;
	private boolean bIsDeveloper = false;
	private long lPacketTimeoutMS;
	
	
	protected boolean getIsDeveloper() {
		
		return bIsDeveloper;
	}
	
	protected boolean getIsGM() {
		return bIsGM;
	}
	public ZoneClientThread(ZoneServer server, ZoneClient client) {
		this.server = server;
		this.client = client;
		myThread = new Thread(this);
		myThread.start();
	}

	protected void terminate() {
		myThread = null;
		server.removeClient(client);
		if (player != null) {
			server.getGUI().getDB().updatePlayer(player, false, false);
			Vector<PlayerFriends> vPlayerFriends = player.getPlayData().getFriendsList();
			for (int i = 0; i < vPlayerFriends.size(); i++) {
				Player friend = server.getPlayer(vPlayerFriends.elementAt(i).getName());
				ZoneClient friendClient = friend.getClient();
				if (friendClient != null) {
					try {
						friendClient.insertPacket(PacketFactory.buildFriendOfflineStatusUpdate(friend, player));
					} catch (Exception e) {
						// D'oh!
					}

				}
			}
			server.forwardFriendChangedStatus(player, false);
		}
	}

	protected boolean bIsThreadActive() {
		return (myThread != null);
	}

	public void run() {
		//System.out.println("Packet parser running.");
		long lCurrentTimeMS = System.currentTimeMillis();
		long lLastTimeMS = lCurrentTimeMS;
		long lDeltaTimeMS = 0;

		while (myThread != null) {
			try {
				synchronized(this) {
					Thread.yield();
					wait(100);
				}
				lLastTimeMS = lCurrentTimeMS;
				lCurrentTimeMS = System.currentTimeMillis();
				lDeltaTimeMS = Math.max(0, (lCurrentTimeMS - lLastTimeMS));
				byte[] workingPacket = null;
				if (client != null) {
					if (!client.getValidSession()) {
						//System.out.println("Terminating client update thread.");
						terminate();
						return;
					}
					if (client.hasPacketsToParse()) {
						lPacketTimeoutMS = Constants.LD_TIMEOUT_MS;
						client.setIsLD(false);
						workingPacket = client.getPacketToParse();
						handleIncomingSWGPacket(workingPacket);
					} else {
						lPacketTimeoutMS -= lDeltaTimeMS;
						client.setHasNullOutOrderPacket(false);
						if (lPacketTimeoutMS <= 0) {
							//if (client.getIsLD()) {
								client.setValidSession(false);
								client.insertPacket(PacketFactory.buildDisconnectClient(client, Constants.DISCONNECT_REASON_KICK));
								client.dequeuePacket();
								server.removeClientByAddress(client.getClientAddress());
								if (player != null) {
									client.getServer().getGUI().getDB().updatePlayer(player, false, false);
								}
								terminate();
							/*} else {
								client.setIsLD(true);
								lPacketTimeoutMS = Constants.DISCONNECT_TIMEOUT_MS;
								//System.out.println("ZoneClient has gone LD.");
								//PacketFactory.buildDeltasMessageGroupInvite(iBaselineIndex, iBaselineType, updateCount, updateOperand, o, newValue);
							}*/
						}
					}
					if (player != null) {
						player.update((lDeltaTimeMS));
					}
					client.dequeuePacket();
					
				} else {
					//System.out.println("Client became null... where?");
					terminate();
					return;
				}
			} catch (Exception e) {
				//ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				//PrintWriter w = new PrintWriter(bOut);
				//w.println("Error handling incoming packet: " + e.toString());
				//e.printStackTrace(w);
				//w.flush();
				System.out.println("Error handling incoming packet: " + e.toString());
				e.printStackTrace();
				//				try {
				//					client.insertPacket(PacketFactory.buildChatSystemMessage(new String(bOut.toByteArray())));
				//				} catch (IOException eee) {
				//					System.out.println(new String(bOut.toByteArray()));
				//					System.out.println(e.toString());
				//					e.printStackTrace();
				//				}
			}
		}
		//server = null;
		//client = null;
		//player = null;
	}

	private void buildAcknowledgement(short sequence) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		dOut.setOpcode(Constants.SOE_ACK_A);
		dOut.setSequence(sequence);
		dOut.flush();
		int size = bOut.toByteArray().length;
		server.queue(client.PrepareForSendSWG(dOut, size));
		byte [] buffer = bOut.toByteArray();
		DataLogObject L = new DataLogObject("buildAcknowledgement ","SourceIP",buffer,client.getClientAddress().toString(),Constants.LOG_PACKET_DIRECTION_OUT);
		DataLog.logPacket(L);

	}

	private void handleIncomingSWGPacket(byte[] packet) throws Exception {
		try {
			/*if (player != null) {
				System.out.println("Player " + player.getFirstName() + " parsing incoming packet.");
				System.out.flush();
			}*/
			//System.out.println("ZoneClientThread: Handling incoming SWG Packet.");
			if (client == null) {
				System.out.println("Ack!  Null client passed to us.");
			}

			client.incrementServerPacketsReceived();
			SOEInputStream sIn = new SOEInputStream(new ByteArrayInputStream(packet));
			short opcode = sIn.getOpcode();
			switch (opcode) {
			case Constants.SOE_SESSION_REQUEST: {
				handleSessionRequest(sIn);
				DataLogObject L = new DataLogObject("ZoneClientThread()handleSessionRequest",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				break;
			}
			case Constants.SOE_SESSION_RESPONSE: {
				// Never happens.
				break;
			}
			case Constants.SOE_MULTI_PKT: {
				// Break the packet into it's sub packets, and handle them separately.
				handleMultiPacket(sIn);
				DataLogObject L = new DataLogObject("ZoneClientThread()handleMultiPacket",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				break;
			}
			case Constants.SOE_NOT_USED: {
				//StringBuffer buff = new StringBuffer().append("This opcode is not supposed to be used!  Packet data: 0x");
				//System.out.println("This opcode is not supposed to be used!");
				PacketUtils.printPacketData(sIn.getBuffer());
				DataLogObject L = new DataLogObject("ZoneClientThread()SOE_NOT_USED",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
			}
			case Constants.SOE_DISCONNECT: {
				if (player != null) {
					if(player.isDisconnectIgnore())
					{
						player.setDisconnectIgnore(false);
						return;
					}
				}
				handleDisconnect(sIn);
				DataLogObject L = new DataLogObject("ZoneClientThread()handleDisconnect",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				break;
			}
			case Constants.SOE_PING: {
				handlePing(sIn);
				DataLogObject L = new DataLogObject("ZoneClientThread()handlePing",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				break;
			}
			case Constants.SOE_NET_STATUS_REQ: {
				handleNetStatusRequest(sIn);
				DataLogObject L = new DataLogObject("ZoneClientThread()handleNetStatusRequest",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				break;
			}
			case Constants.SOE_NET_STATUS_RES: {
				// We should never receive this, only send it.
				break;
			}
			case Constants.SOE_CHL_DATA_A:
			case Constants.SOE_CHL_DATA_B:
			case Constants.SOE_CHL_DATA_C:
			case Constants.SOE_CHL_DATA_D: {
				short sequence = sIn.getSequence();
				buildAcknowledgement(sequence);
				if (client.setClientSequence(sequence)) {

					try {
						handleChlDataA(sIn);
						//DataLogObject L = new DataLogObject("ZoneClientThread()handleChlDataA",client.getIpnPort(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
						//DataLog.logPacket(L);
					} catch (Exception ee)  {
						System.out.println(new Timestamp(System.currentTimeMillis()).toString() + " -- Error handling SOE_CHL_DATA_A packet: " + ee.toString() );
						ee.printStackTrace();

					}
				} else {
					System.out.println("Client sends us an out of order packet with sequence " + Integer.toHexString(sequence));
				}
				break;
			}
			case Constants.SOE_DATA_FRAG_A:
			case Constants.SOE_DATA_FRAG_B:
			case Constants.SOE_DATA_FRAG_C:
			case Constants.SOE_DATA_FRAG_D: {
				short sequence = sIn.getSequence();
				if (client.setClientSequence(sequence)) {
					try {
						handleDataFrag(sIn, sequence);
						DataLogObject L = new DataLogObject("ZoneClientThread()handleDataFrag",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
						DataLog.logPacket(L);
					} catch (Exception ee)  {
						System.out.println(new Timestamp(System.currentTimeMillis()).toString() + " -- Error handling SOE_CHL_DATA_A packet: " + ee.toString() );
						ee.printStackTrace();
					}
				} else {
					System.out.println("Client sends us an out of order packet with sequence " + Integer.toHexString(sequence));
				}
				break;
			}
			case Constants.SOE_OUT_ORDER_PKT_A:
			case Constants.SOE_OUT_ORDER_PKT_B:
			case Constants.SOE_OUT_ORDER_PKT_C:
			case Constants.SOE_OUT_ORDER_PKT_D: {
				// TODO -- Figure out what Live does on receipt of an SOE_OUT_ORDER_A

				//short sequence = sIn.getSequence();
				//client.setClientSequence((short)(sequence - 1));
				//client.setClientSequence((short)(client.getClientSequence() - 1));
				//handleOutOfOrderPacket(sIn);
				DataLogObject L = new DataLogObject("ZoneClientThread()SOE_OUT_ORDER_PKT",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				// Get the packet immediately before this sequence.
				short iSequenceOutOfOrder = sIn.getSequence();
				System.out.println("Player " + player.getFirstName() + " sends out of order packet with sequence " + Integer.toHexString(iSequenceOutOfOrder).toUpperCase());
				//client.resendPacketBeforeSequence(iSequenceOutOfOrder);
				client.setLastOutOrderReceived(iSequenceOutOfOrder);

				break;
			}
			case Constants.SOE_ACK_A:
			case Constants.SOE_ACK_B:
			case Constants.SOE_ACK_C:
			case Constants.SOE_ACK_D: {
				//short ackSeq = sIn.getSequence();
				//System.out.println("Client acknowleged packet with sequence " + Integer.toHexString(ackSeq).toUpperCase());
				client.setLastAcknowledgedSequence(sIn.getSequence());
				DataLogObject L = new DataLogObject("ZoneClientThread()SOE_ACK",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				break;
			}
			case Constants.DATA_A_MULTI_PKT: {
				// This is never sent to us directly -- it is always wrapped in a SOE_CHL_DATA_?.
				DataLog.logEntry("DATA_A_MULTI_PKT", "ZoneClientThread",Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
				break;
			}
			case Constants.SOE_FATAL_ERR: {
				// D'oh!
				terminate();
				DataLog.logEntry("SOE_FATAL_ERR", "ZoneClientThread",Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
				break;
			}
			case Constants.SOE_FATAL_ERR_REP:  {
				// Double D'oh!
				DataLog.logEntry("SOE_FATAL_ERR_REP", "ZoneClientThread",Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
				break;
			}
			case Constants.SOE_TRANS_FIVE: {
				handleTransFive(sIn);
				DataLogObject L = new DataLogObject("ZoneClientThread()handleTransFive",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				break;
			}
			default: {
				StringBuffer buff = new StringBuffer().append("Unknown packet with opcode ").append(opcode).append(".  Packet data: 0x");
				for (int i = 0; i < packet.length; i++) {
					String s = Integer.toHexString(packet[i]);
					buff.append(s);
					if (i != packet.length - 1) {
						buff.append(" 0x");
					}
				}
				DataLogObject L = new DataLogObject("ZoneClientThread()default",client.getClientAddress().toString(),packet,"DestIP",Constants.LOG_PACKET_DIRECTION_IN);
				DataLog.logPacket(L);
				throw new IOException(buff.toString());
			}
			}
		} catch (Exception e) {
			System.out.println("Error handling incoming SWG packet: " + e.toString());
			e.printStackTrace();
		}
		//System.out.println("Handled.");
	}

	private void handleSessionRequest(SOEInputStream dIn) {
		try {
			/*int unknownID =*/ dIn.readReversedInt(); // It might become needed later.
			int connectionID = dIn.readInt();
			int packetSize = dIn.readReversedInt();
			byte[] response =PacketFactory.buildSessionResponse(connectionID, packetSize);
			int connectionCRC = 0; //195939018;
			client.setOutgoingSequence((short)0);
			client.setCRCSeed(connectionCRC);
			client.insertPacket(response);
			// TODO:  Client Permissions Message also needs to know the number of characters owned by the player on
			// "this" Zone Server, so that it can set the new player permission false if necessary.
			client.insertPacket(PacketFactory.buildClientPermissionsMessage(0, 8));
		} catch (IOException e) {
			// Well... I guess nobody can connect.
			System.out.println("Error handling SessionRequest: " + e.toString());
			e.printStackTrace();
		}
	}

	private void handlePing(SOEInputStream dIn) {
		try {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			SOEOutputStream dOut = new SOEOutputStream(bOut);
			dOut.setOpcode(Constants.SOE_PING);
			dOut.flush();
			client.insertPacket(bOut.toByteArray());
		} catch (NullPointerException e) {
			System.out.println("Error:  Received ping from a null player???" + e.toString());
			e.printStackTrace();
		} catch (IOException ee) {
			System.out.println("Error building ping response: " + ee.toString());
			ee.printStackTrace();
		} catch (Exception eee) {
			System.out.println("Error appending CRC to packet: " + eee.toString());
			eee.printStackTrace();
		}
	}

	private void handleNetStatusRequest(SOEInputStream dIn) {
		try {
			client.setReportedClientTick(dIn.readReversedShort());
			client.setLastUpdateDeltaMS(dIn.readReversedInt());
			client.setAverageUpdateDeltaMS(dIn.readReversedInt());
			client.setShortestUpdateDeltaMS(dIn.readReversedInt());
			client.setLongestUpdateDeltaMS(dIn.readReversedInt());
			client.setKnownLastServerUpdateDeltaMS(dIn.readReversedInt());
			client.setClientPacketsSent(dIn.readReversedInt());
			client.setClientPacketsReceived(dIn.readReversedInt());
			client.insertPacket(PacketFactory.buildNetStatusResponse(client));
		} catch (Exception e) {
			System.out.println("Error receiving net status request: " + e.toString());
			e.printStackTrace();
		}
	}

	private void handleDataFrag(SOEInputStream dIn, short sequence) {
		try {
			buildAcknowledgement(dIn.getSequence());
			byte[] data = dIn.getBuffer();
			if (!client.getIsFragmenting()) {
				// It's the first packet.
				int totalPacketSize = dIn.readReversedInt();
				int numExpectedPackets = totalPacketSize / PacketUtils.MAX_PACKET_SIZE + 1; // Don't count this packet???
				if ((totalPacketSize % PacketUtils.MAX_PACKET_SIZE) != 0) {
					numExpectedPackets++;
				}
				if (totalPacketSize > 0xFFFF) {
					System.out.println("Data frag error -- incoming packet size reported extremely large -- sequence " + Integer.toHexString(sequence));
				} else {
					PacketUtils.printPacketToScreen(Arrays.copyOfRange(data, 0, 8), "Start of data frag a.");
					client.startFragA(Arrays.copyOfRange(data, 8, data.length - 2), dIn.getSequence(), totalPacketSize);
				}
			} else {
				// It's a middle packet
				byte[] completePacket = client.putFragA(Arrays.copyOfRange(data, 4, data.length - 2), false);
				if (completePacket != null) {
					//buildAcknowledgement(dIn.getSequence());
					SOEInputStream newIn = new SOEInputStream(new ByteArrayInputStream(completePacket));
					//buildAcknowledgement(dIn.getSequence());
					handleChlDataA(newIn);
				}
			}
		} catch (Exception e) {
			DataLog.logException("Error handling DATA_FRAG_A", "ZoneClientThread.handleDataFrag",true, true, e);
		}
	}

	private void handleMultiPacket(SOEInputStream dIn) throws Exception {
		int iSubPacketCount = 0;
		while (dIn.available() > 3) {
			iSubPacketCount++;
			int subPacketSize = dIn.readUnsignedByte();
			if (subPacketSize == 0xFF) {
				subPacketSize = dIn.readShort();
				// There's likely more to go.  Need to figure out what exactly the client sends in this case.
				//System.out.println("handleMultiPacket:  SubPacket size read == 255 -- printing entire packet for debug.");
				//PacketUtils.printPacketData(dIn.getBuffer());
			}
			byte[] packet = new byte[subPacketSize];
			dIn.read(packet, 0, subPacketSize);
			// You're already decrypted and decompressed -- we need to make decisions based on your packet type.
			handleIncomingSWGPacket(packet);
		}
	}

	private void handleChlDataA(SOEInputStream dIn)  throws Exception {
		short updateType = dIn.getUpdateType();
		//System.out.println("handleChlDataA: " + Integer.toHexString((int)updateType));
		switch (updateType) {
		case Constants.CLIENT_UI_UPDATE: {
			handleClientUIUpdate(dIn);
			break;
		}
		case Constants.WORLD_UPDATE: {
			handleWorldUpdate(dIn);
			break;
		}
		case Constants.ACCOUNT_UPDATE: {
			handleAccountUpdate(dIn);
			break;
		}
		case Constants.SERVER_UPDATE: {
			handleServerUpdate(dIn);
			break;
		}
		case Constants.OBJECT_UPDATE: {
			handleObjectUpdate(dIn);
			break;
		}
		case Constants.UPDATE_SIX: {
			handleUpdateSix(dIn);
			break;
		}
		case Constants.UPDATE_SEVEN: {
			handleUpdateSeven(dIn);
			break;
		}
		case Constants.SCENE_UPDATE: {
			handleSceneUpdate(dIn);
			break;
		}
		case Constants.UPDATE_NINE: {
			handleUpdateNine(dIn);
			break;
		}
		case Constants.UPDATE_TEN: {
			handleUpdateTen(dIn);
			break;
		}
		case Constants.UPDATE_ELEVEN: {
			handleUpdateEleven(dIn);
			break;
		}
		case Constants.UPDATE_CHAR_CREATE: {
			handleCreateCharacter(dIn);
			break;
		}
		case Constants.UPDATE_THIRTEEN: {
			handleUpdateThirteen(dIn);
			break;
		}
		case Constants.UPDATE_FOURTEEN: {
			handleUpdateFourteen(dIn);
			break;
		}
		case Constants.UPDATE_FIFTEEN: {
			handleUpdateFifteen(dIn);
			break;
		}
		case Constants.DATA_A_MULTI_PKT: {
			handleDataAMultipacket(dIn);
			break;
		}
		default: {
			StringBuffer buff = new StringBuffer().append("Unknown packet with updateType ").append(updateType);
			System.out.println(buff.toString());
			//PacketUtils.printPacketData(dIn.getBuffer());
			PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleChlDataA");
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			for (int i = 0; i < trace.length; i++) {
				System.out.println(trace[i]);
			}
		}
		}
		dIn.close();
		dIn = null;
	}

	private void handleDataAMultipacket(SOEInputStream dIn) {
		//System.out.println("Handling DATA_A_MULTI_PKT");
		try {
			int iSubPackets = 0;
			while (dIn.available() > 3) { // So that we don't think the comp + crc bytes are another packet.
				iSubPackets++;

				int subPacketSize = dIn.readUnsignedByte();
				if (subPacketSize == 0xff) {
					subPacketSize = dIn.readUnsignedShort();
				}
				//System.out.println("Data A Multipacket -- subpacket size = " + subPacketSize + ", subpacket count: " + iSubPackets);
				byte[] buffer = new byte[subPacketSize + 1];
				dIn.read(buffer, 0, subPacketSize);

				SOEInputStream subIn = new SOEInputStream(new ByteArrayInputStream(buffer));
				short updateType = subIn.getOpcode();
				switch (updateType) {
				case Constants.CLIENT_UI_UPDATE: {
					handleClientUIUpdate(subIn);
					break;
				}
				case Constants.WORLD_UPDATE: {
					handleWorldUpdate(subIn);
					break;
				}
				case Constants.ACCOUNT_UPDATE: {
					handleAccountUpdate(subIn);
					break;
				}
				case Constants.SERVER_UPDATE: {
					handleServerUpdate(subIn);
					break;
				}
				case Constants.OBJECT_UPDATE: {
					handleObjectUpdate(subIn);
					break;
				}
				case Constants.UPDATE_SIX: {
					handleUpdateSix(subIn);
					break;
				}
				case Constants.UPDATE_SEVEN: {
					handleUpdateSeven(subIn);
					break;
				}
				case Constants.SCENE_UPDATE: {
					handleSceneUpdate(subIn);
					break;
				}
				case Constants.UPDATE_NINE: {
					handleUpdateNine(subIn);
					break;
				}
				case Constants.UPDATE_TEN: {
					handleUpdateTen(subIn);
					break;
				}
				case Constants.UPDATE_ELEVEN: {
					handleUpdateEleven(subIn);
					break;
				}
				case Constants.UPDATE_CHAR_CREATE: {
					handleCreateCharacter(subIn);
					break;
				}
				case Constants.UPDATE_THIRTEEN: {
					handleUpdateThirteen(subIn);
					break;
				}
				case Constants.UPDATE_FOURTEEN: {
					handleUpdateFourteen(subIn);
					break;
				}
				case Constants.UPDATE_FIFTEEN: {
					handleUpdateFifteen(subIn);
					break;
				}
				default: {
					StringBuffer buff = new StringBuffer().append("Unknown subpacket with updateType ").append(updateType);
					System.out.println(buff.toString());
					//PacketUtils.printPacketData(subIn.getBuffer());
					//PacketUtils.printPacketToScreen(subIn.getBuffer(), subIn.available(), "handleDataAMultipacket");
				}
				}
			}
		} catch (Exception e) {
			System.out.println("Error handling DATA_A_MULTI_PKT: " + e.toString());
			e.printStackTrace();
		}
	}

	private void handleClientUIUpdate(SOEInputStream dIn) throws IOException {
		int commandCRC = dIn.readInt();
		switch (commandCRC) {
		case Constants.CmdSceneReady: // TODO -- This should be in it's own function.
		{
			System.out.println("Handle cmdSceneReady");
			if(player.getTutorial() == null && !player.IsPlayerTraveling())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage(server.getMOTD()));
				//Start login script here
			}
			else if(player.getTutorial() != null && !player.getTutorial().hasCompleted())
			{
				
				if(player.getTutorial().getTemplateID() == 210)
				{
					//start run tutorial script here                                    
					System.out.println("Tutorial Script Begins Run for Tutorial.js");
                    player.getTutorial().tutorialPlayWelcome(false);
				}
				else
				{
					//start run tutorial bypassed script here
					player.getTutorial().setHasEnteredTutorial(false);
                    player.getTutorial().tutorialPlayWelcome(true);
					System.out.println("Tutorial Script Begins Run for TutorialBypassed.js");
					player.getClient().insertPacket(PacketFactory.buildObjectControllerStartingLocationsWindow(player, DatabaseInterface.getStartingLocations()));
				}
			}
			else if(player.getTutorial() != null && player.getTutorial().hasCompleted() && !player.IsPlayerTraveling())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage(server.getMOTD()));
				//Start login script here
			}
			player.setPlayerIsNotTraveling();
			server.forwardFriendChangedStatus(player, true);
			server.sendFriendChangedOnlineStatus(server.getClusterName(), player.getFirstName(), true);
			//player.spawnPlayer(false);
			//spawnPlayerToNearPlayers(false);


			// Temp code.
			Vector<SOEObject> vObjectsNearPlayer = server.getWorldObjectsAroundObject(player); // Does this include the player?

			// SceneReady -- spawn everything around the Player to the Player, not including himself.
			spawnObjectsNearPlayer(vObjectsNearPlayer);
			if(player.bHeightMapActive)
			{
				player.setBHeightMapCollect(true);
			}
			if (account == null) {
				client.insertPacket(PacketFactory.buildChatSystemMessage("Unable to load account details for account with ID " + client.getAccountID()));
			}
			break;
		}
		case Constants.FactionRequestMessage:
		{
			client.insertPacket(PacketFactory.buildFactionResponseMessage(player));
			break;
		}
		case Constants.LagRequest:
		{
			// You're not handled.
			//System.out.println("Received Lag Request from client.  TODO: Handle it!");
			break;
		}
		case Constants.Unhandled1:
		{
			//System.out.println("Received unhandled1 packet from client.  TODO:  Handle it!");
			//i think this may be the request for Support Server Address.
			//-----------------
			break;
		}
		case Constants.Trade_AbortTradeMessage:
		{
			Player p = client.getPlayer();
			TradeObject t = p.getCurrentTradeObject();
			t.notifyAbortTrade(p);
			break;
		}
		case Constants.Trade_CheckTradeWindowAccept:
		{                         
			Player p = client.getPlayer();
			TradeObject t = p.getCurrentTradeObject();
			t.notifyTradeAcceptedChecked(p);
			break;
		}
		case Constants.Trade_UnCheckTradeWindowAccept:
		{                         
			Player p = client.getPlayer();
			TradeObject t = p.getCurrentTradeObject();
			t.notifyTradeAcceptedUnChecked(p);
			break;
		}
		case Constants.Trade_VerifyTradeMessage:
		{
			/**
			 *  01 00 
                                EE 47 E2 9A //9AE247EE Trade_VerifyTradeMessage                                
			 */
			 Player p = client.getPlayer();
			TradeObject t = p.getCurrentTradeObject();
			t.notifyVerifyTrade(p);
			break;
		}

		default:
		{

			//StringBuffer buff = new StringBuffer().append("Unknown Client UI Update packet received with command CRC 0x").append(Integer.toHexString(commandCRC));
			//System.out.println(buff.toString());
			//PacketUtils.printPacketData(dIn.getBuffer());
			byte [] packet = dIn.getBuffer();
			PacketUtils.printPacketToScreen(packet, packet.length, "handleClientUIUpdate: CC:" + commandCRC);
		}
		}

	}
	// DONE
	private void handleWorldUpdate(SOEInputStream dIn) throws IOException {
		// We do receive a bunch of these, afaik.
		int commandCRC = dIn.readInt();
		//System.out.println("handleWorldUpdate: 0x" + Integer.toHexString(commandCRC));
		switch (commandCRC){
		case Constants.ClientRandomNameRequest: {
			handleRandomNameRequest(dIn);
			break;
		}
		case Constants.SelectCharacter: {
			// Well, we obviously want to start the spawning process, at a guess.
			try {
				handleSelectCharacter(dIn);
			} catch (Exception e) {
				System.out.println("Error in handleSelectCharacter: " + e.toString());
				e.printStackTrace();
			}
			break;
		}
		case Constants.ClientInactivityMessage: {
			handleClientInactivityMessage(dIn);
			break;
		}
		case Constants.ConnectPlayerMessage: {
			handleConnectPlayerMessage(dIn);
			break;
		}
		case Constants.ClientReadyStringResponse: {
			handleClientReadyStringResponse(dIn);
			break;
		}
		case Constants.ChatSendToRoom: {
			handleChatSendToRoom(dIn);
			break;
		}
		case Constants.ClientRequestDeleteEmail: {
			/*
                            Client->Server:               TIME_STAMP: 17:12:26
                            LENGTH: 17
                            DATA:
                            00 09
                            00 0A
                            02 00 //world update
                            41 16 25 8F //SOE Command Code
                            18 00 00 00 //EmailID
                            00 46 52  //CRC
			 */
			SWGEmail E = new SWGEmail(dIn.readInt());
			E.setTransactionRequester(client);
			client.getServer().queueEmailClientrequestDelete(E);
			break;
		}
		case Constants.Trade_ClientAddItemToTradeWindow:
		{
			/* 00 09 00 08 
                        02 00 
                        56 13 8D 1E 
                        00 00 00 00 00 00 00 00 <--- Item ID to Add
			 * 00 52 0C  
                        <------------------------------------------------>*/
			try{
				Player p = client.getPlayer();
				TradeObject t = p.getCurrentTradeObject();
				if(t==null)
				{
					p.getClient().insertPacket(PacketFactory.buildAbortTradeMessage());
					return;
				}
				t.notifyItemAddedToWindow(p, dIn.readLong());

			}catch(Exception e){
				System.out.println("Exception in handleWorldUpdate Trade_ClientAddItemToTradeWindow " + e);
				e.printStackTrace();
			}
			break;
		}
		case Constants.Trade_UpdateTradeWindowCredits:
		{
			/**
			 * 00 09 00 33 
			 * 02 00 
			 * E8 7E 52 D1 
			 * 01 00 00 00 <--ICash Amount
			 * 00 1C 94  
			 */
			Player p = client.getPlayer();
			TradeObject t = p.getCurrentTradeObject();
			t.notifyCashOfferedUpdate(p, dIn.readInt());
			break;
		}
		case Constants.RequestBazaarTerminal: {
			handleRequestBazaarTerminal(dIn);
			break;
		}
		default:
		{
			byte [] pkt = dIn.getBuffer();
			PacketUtils.printPacketToScreen(pkt,pkt.length,"Unhandled Packet in handleWorldUpdate CRC " + commandCRC  );
		}
		}
	}
	//
	private void handleAccountUpdate(SOEInputStream dIn) throws IOException{
		int commandCRC = dIn.readInt();
		//System.out.println("handleAccountUpdate:" + Integer.toHexString(commandCRC));
		switch (commandCRC) {
		case Constants.ClientIdMsg: {
			handleClientIDMessage(dIn);
			break;
		}
		case Constants.ObjectMenuSelectMessage: {
			handleObjectMenuSelectMessage(dIn);
			break;
		}
		case Constants.ClientRequestEmailContent: {
			/*
                        00 08
                        03 00 //Operand
                        9F 55 E3 07 //SOE OP COMMAND CODE  0x07E3559F
                        00 00 00 00 // INT 0
                        0E F0 AD BA //email ID REQUESTED
                        00
                        6E 48                                     ...nH//crc
			 */
			dIn.readInt();
			SWGEmail E = new SWGEmail(dIn.readInt());
			E.setTransactionRequester(client);
			client.getServer().queueEmailClientRequestContent(E);
			break;
		}
		case Constants.PlanetTravelListRequest: {
			//0x96405D4D  -- 4D 5D 40 96
			// System.out.println("handleObjectUpdate,PlanetTravelListRequest");
			handlePlanetTravelListRequest(dIn);
			break;
		}
		default:
		{
			byte [] packet  = dIn.getBuffer();
			PacketUtils.printPacketToScreen(packet,packet.length,"Unhandled Packet in handleAccountUpdate");
		}
		}
	}

	private void handleObjectMenuSelectMessage(SOEInputStream dIn) throws IOException {
		long objectID = dIn.readLong();
		byte commandID = dIn.readByte();
		SOEObject o = server.getObjectFromAllObjects(objectID);

		//This message is for debug purposes, inform the client that we have received the message.
		//client.insertPacket(PacketFactory.buildChatSystemMessage("Recieved Radial Menu Selection Message."));

		if(o != null) {
			if (o instanceof Deed) {
				Deed d = (Deed) o;
				d.useItemByCommandID(client, commandID);
			}
			else if(o instanceof CreaturePet)
			{
				CreaturePet pet = (CreaturePet)o;
				pet.petCommand(client, commandID, null);
			} else if (o instanceof CreatureAnimal) {
				CreatureAnimal animal = (CreatureAnimal)o;
				animal.useItemByCommandID(client, commandID);
			}
			else if (o instanceof Weapon) {
				
				Weapon w = (Weapon)o;
				w.useItemByCommandID(client, commandID);
				return;
			}
			else if (o instanceof TangibleItem) {
				//client.insertPacket(PacketFactory.buildChatSystemMessage("Object type is handled."));
				TangibleItem tItem = (TangibleItem) o;
				tItem.useItemByCommandID(client, commandID);
				return;
			} else if (o instanceof IntangibleObject) {
				// client.insertPacket(PacketFactory.buildChatSystemMessage("Object type is handled."));
				IntangibleObject oItem = (IntangibleObject)o;
				System.out.println("Intangible Object Request");
				oItem.useItem(client, commandID);
				return;
				//deprecated handleSpawnFromIntangibleRequest(client,oItem); // TODO:  Move this to the IntangibleObject useItem function, for consistency.
			} else if (o instanceof Terminal) {
				// System.out.println("Terminal use Request Detected");
				Terminal terminalItem = (Terminal)o;
				terminalItem.useItem(client,commandID);
				return;
			} else if (o instanceof Vehicle) {
				Vehicle vehicleItem = (Vehicle)o;
				//System.out.println("handleObjectMenuSelectMessage Vehicle use Selection");
				if(commandID != -51)
				{
					vehicleItem.useItem(client,commandID);
				}
				return;
			}else if (o instanceof Player) {
				// client.insertPacket(PacketFactory.buildChatSystemMessage("Object type is handled."));
				Player pItem = (Player)o;
				pItem.useItem(client);
				return;
			} else if (o instanceof Factory) {
				Factory f = (Factory)o;
				f.useItemByCommandID(client, commandID);
			}else if(o instanceof Structure){
				//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.getBuffer().length, "OMSM");
				Structure s = (Structure)o;
				s.useItem(client, commandID);
				return;
			} else {
				//This will print if we don't have the type of item covered yet.
				//This should not happen.
				client.insertPacket(PacketFactory.buildChatSystemMessage("Object type is not handled."));
				client.insertPacket(PacketFactory.buildChatSystemMessage("Object type: " + o.getClass().getName()));
			}

		} else {
			//This should NOT happen.
			client.insertPacket(PacketFactory.buildChatSystemMessage("Object is not in the server's list of objects!"));
			client.insertPacket(PacketFactory.buildChatSystemMessage("Object ID: " + objectID));
		}
	}

	private void handleServerUpdate(SOEInputStream dIn) throws IOException {
		int commandID = dIn.readInt();
		switch (commandID) {
		case Constants.ClientIdMsg: {
			handleClientIDMessage(dIn);
			break;
		}
		case Constants.SuiEventNotification:
		{
			//System.out.println("Received SuiEventNotification");
			//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in SuiEventNotification");
			handleSuiEventNotification(dIn);
			break;
		}
		default: {
			System.out.println("Received unhandled server update packet with Command ID = " + Integer.toHexString(commandID));
			byte [] packet = dIn.getBuffer();
			PacketUtils.printPacketToScreen(packet,packet.length,"Unhandled Packet in handleServerUpdate");
			break;
		}
		}
	}
	private void handleObjectUpdate(SOEInputStream dIn) throws IOException {
		int commandID = dIn.readInt();
		switch (commandID) {
		case Constants.ObjControllerMessage: {
			handleObjectControllerMessage(dIn);
			break;
		}
		case Constants.ChatInstantMessageToCharacter: {
			handleChatInstantMessageToCharacter(dIn);
			break;
		}
		case Constants.MapLocationsRequestMessage: {
			handleMapLocationRequestMessage(dIn);
			break;
		}
		default: {
			System.out.println("Received unhandled object update packet with Command ID = " + Integer.toHexString(commandID));
			byte [] packet = dIn.getBuffer();
			PacketUtils.printPacketToScreen(packet,packet.length,"Unhandled Packet in handleObjectUpdate");
		}
		}
	}

	private void handleUpdateSix(SOEInputStream dIn) throws IOException {
		int commandID = dIn.readInt();
		switch (commandID) {
		case Constants.ClientSendEmailRequest: {
			handleClientSendEmailRequest(dIn);
			break;
		}
		default: {
			System.out.println("Received unhandled object update_six packet with Command ID = " + Integer.toHexString(commandID));
			//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleUpdateSix");
		}
		}
	}
	private void handleUpdateSeven(SOEInputStream dIn) throws IOException {

		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleUpdateSeven");
	}
	private void handleSceneUpdate(SOEInputStream dIn) throws IOException {
		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleSceneUpdate");
	}
	private void handleUpdateNine(SOEInputStream dIn) throws IOException {
		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleUpdateNine");
	}
	private void handleUpdateTen(SOEInputStream dIn) throws IOException {
		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleUpdateTen");
	}
	private void handleUpdateEleven(SOEInputStream dIn) throws IOException {
		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleUpdateEleven");
	}
	private void handleCreateCharacter(SOEInputStream dIn) throws IOException {
		int commandID = dIn.readInt();
		switch (commandID) {
		case Constants.ClientCreateCharacter: {
			handleClientCreateCharacter(dIn);
			break;
		}
		}
	}
	private void handleUpdateThirteen(SOEInputStream dIn) throws IOException {
		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleUpdateThirteen");
	}

	private void handleUpdateFourteen(SOEInputStream dIn) throws IOException {
		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleUpdateFourteen");
	}

	private void handleUpdateFifteen(SOEInputStream dIn) throws IOException {
		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.available(),"Unhandled Packet in handleUpdateFifteen");
	}

	private void handleClientIDMessage(SOEInputStream dIn) throws IOException {
		//System.out.println("In handleClientIDMessage");
		/*int unknownInt =*/ dIn.readInt(); // Uhh... ?
		int dataSize = dIn.readInt();
		byte[] unknownByte = new byte[dataSize - 4];
		dIn.read(unknownByte);
		int id = dIn.readInt();
		//String sVersionString = dIn.readUTF();
		//System.out.println("Packet Parser: Reading account ID = " + id);
		client.setStationID(id);
		client.setAccountID(id);
		// TODO: This needs to be redone for if the Login Server exists or not.
		while (account == null) {
			account = server.getAccountDataFromLoginServer(id);
		}
		if (account != null) {
			bIsGM = account.getIsGM();
			bIsDeveloper = account.getIsDeveloper();
		} else {
			System.out.println("Null account data for account ID " + client.getAccountID() + ", forcibly disconnecting client.");
			client.insertPacket(PacketFactory.buildDisconnectClient(client, Constants.DISCONNECT_REASON_KICK));
		}
		if (player != null) {
			player.setAccountID(id);
		}
	}

	private void handleRandomNameRequest(SOEInputStream dIn) throws IOException {

		String newCharacterRace = dIn.readUTF();
		String[] generatedNames = this.server.generateRandomName(newCharacterRace);
		//SWGGui theGUI = server.getGUI();
		StringBuffer randomFirstName = new StringBuffer();
		StringBuffer randomLastName = new StringBuffer();
		randomFirstName.append(generatedNames[0]);
		randomLastName.append(generatedNames[1]);
		byte[] nameResponse = PacketFactory.buildClientRandomNameResponse(newCharacterRace, randomFirstName.append(randomLastName).toString(), Constants.NAME_ACCEPTED);
		client.insertPacket(nameResponse);
	}


	private void handleSelectCharacter(SOEInputStream dIn) throws IOException {

		try {
			long objectID = dIn.readLong();
			player = server.getPlayer(objectID);
			myThread.setName(player.getFirstName() + "'s thread.");
			player.clearSpawnedItems();
			player.resetPlayerLogout();
			//System.out.println("Got player with object ID " + objectID + ".  Name: " + player.getFullName());
			//System.out.println("Player's account ID: " + player.getAccountID());
			//System.out.println("Client's account ID: " + client.getAccountID());
			/**
			 * This sets the tutorial to null and removes it from the server on the login
			 * of the toon following completion.
			 */
			if(player.getTutorial()!=null && player.getTutorial().hasCompleted())
			{
				DataLog.logEntry("Player with Completed Tutorial Detected. Deleting Tutorial Object.","ZoneClientThread", Constants.LOG_SEVERITY_INFO, ZoneServer.ZoneRunOptions.bLogToConsole, true);
				if(player.getPlanetID() != Constants.TUTORIAL )
				{
					TutorialObject to = player.getTutorial();
					Enumeration<Cell> cEnum = to.getCellsInBuilding().elements();
					while(cEnum.hasMoreElements())
					{
						Cell c = cEnum.nextElement();
						Enumeration<SOEObject> oEnum = c.getCellObjects().elements();
						while(oEnum.hasMoreElements())
						{
							SOEObject o = oEnum.nextElement();
							c.removeCellObject(o);
							server.removeObjectFromAllObjects(o,false);
						}
						server.removeObjectFromAllObjects(c,false);
					}
					server.removeObjectFromAllObjects(to,false);
					player.setTutorial(null);
				}
			}
			player.setServer(server);
			client.addPlayer(player);
			player.setClient(client);
			player.setAccountID(client.getAccountID());
			player.initializeStartupTime();
			// Spawn the player, then spawn the player to everyone around him.
			System.out.println("Spawning Player");
			player.spawnPlayer();
			System.out.println("Player Spawned, Spawning to Near Players");
			spawnPlayerToNearPlayers();
			System.out.println("Player Spawned to Near Players");
			//Vector<SOEObject> vObjectsNearPlayer = server.getObjectsAroundPlayer(player); // Does this include the player?
			//spawnObjectsNearPlayer(vObjectsNearPlayer, player, true);
			server.addObjectToAllObjects(player, true,false);
		} catch (Exception e) {
			System.out.println("handleSelectCharacter explosion: " + e.toString());
			e.printStackTrace();
		}
	}

	private void spawnObjectsNearPlayer(Vector<SOEObject> objects) throws IOException {

		for (int i = 0; i < objects.size(); i++) {
			SOEObject o = objects.get(i);
			player.spawnItem(o);
			//if (!o.getIsStaticObject()) {
			//	player.getClient().insertPacket(PacketFactory.buildNPCUpdateTransformMessage(o));
			//}
		}
	}

	private void handleClientCreateCharacter(SOEInputStream dIn) throws IOException {
		System.out.println("handleClientCreateCharacter");
		AccountData data = server.getAccountDataFromLoginServer(client.getAccountID());
		if (data == null) {
			System.out.println("ZoneClient critical error:  Null account data retrieved for account ID " + client.getAccountID());
			client.insertPacket(PacketFactory.buildDisconnectClient(client, Constants.DISCONNECT_REASON_KICK));
			terminate();
			return;
		}
		String sCharacterName = "";
		String sCharacterRace = "";
		String sCharacterSharedRace = "";
		//String sStartingLocation = "";
		String sOriginalHairObject = "";
		String sHairObjectName = "";
		String sProfession = "";
		int iRaceIndex = -1;
		short customizationDataLength = 0;
		byte[] customizationData = null;
		DatabaseInterface dbInterface = server.getGUI().getDB();
		//ItemTemplate playerTemplate = null;
		boolean bHasHair = true;
		short hairCustomizationLength = 0;
		byte[] hairCustomizationData = null;
		try {
			customizationDataLength = dIn.readShort();
			customizationData = new byte[customizationDataLength];
			dIn.read(customizationData, 0, customizationDataLength);
			sCharacterName = dIn.readUTF16();
			sCharacterRace = dIn.readUTF();
			System.out.println("Received Character Race as: " + sCharacterRace );
			sCharacterSharedRace = null;
			for (int i = 0; i < Constants.PlayerRaceModels.length && iRaceIndex == -1; i++) {
				if (sCharacterRace.equals(Constants.PlayerRaceModels[i])) {
					iRaceIndex = i;
					sCharacterSharedRace = Constants.SharedRaceModels[i];
				}
			}
			//playerTemplate = DatabaseInterface.getTemplateDataByFilename(Constants.SharedRaceModels[iRaceIndex]);

			String ssStartingLocation = dIn.readUTF(); // For the version of client we're using, will always be Bestine.
			System.out.println("Received Starting Location " + ssStartingLocation );
			// Which means the planet ID will always be 8?
			sOriginalHairObject = dIn.readUTF();
			sHairObjectName = "";
			if (sOriginalHairObject.equals("")) {
				bHasHair = false;
			} else {
				StringBuffer buff = new StringBuffer();
				String sHairSubstring = sOriginalHairObject.substring(0, sOriginalHairObject.lastIndexOf("/") + 1);
				buff.append(sHairSubstring);
				buff.append("shared_");
				buff.append(sOriginalHairObject.substring(sOriginalHairObject.lastIndexOf("/") + 1));
				sHairObjectName = buff.toString();
				//System.out.println("Generate shared hair item name: " + sHairObjectName);
			}
			hairCustomizationLength = dIn.readShort();
			hairCustomizationData = null;
			if (hairCustomizationLength > 0) {
				hairCustomizationData = new byte[hairCustomizationLength];
				dIn.read(hairCustomizationData, 0, hairCustomizationLength);
			}

			sProfession = dIn.readUTF();
			// This will give the player skills and skill mods based on his profession only.  We also need to give him
			// some based on his species.
			// Every creature gets:  species (ID 675)
			//
			int iProfessionIndex = server.getSkillIndexFromName(sProfession);

			/*byte bUnknown =*/ dIn.readByte();
			float fScale = dIn.readFloat();
			String sBiography = null;
			sBiography = dIn.readUTF16();
			if (sBiography == null || sBiography.length() < 1) {
				sBiography = "";
			}
			boolean bEnteringTutorial = dIn.readBoolean();
			String[] sCharacterParsedNames = sCharacterName.split(" ");
			sCharacterName.replace("'","");
			long lPlayerID = server.getNextObjectID();
			int iNameResponseCode = Integer.MIN_VALUE;
			if (sCharacterParsedNames.length > 1) {
				iNameResponseCode = dbInterface.isNameAppropriate(sCharacterParsedNames[0], sCharacterParsedNames[1], server);
			} else {
				iNameResponseCode = dbInterface.isNameAppropriate(sCharacterParsedNames[0], null, server);
			}
			System.out.println("Name response code: " + iNameResponseCode);
			if (iNameResponseCode == Constants.NAME_ACCEPTED) {

				// Create the character!
				Player player = new Player(server);
				// player = new Player(server);
				player.setID(lPlayerID);
				player.setEnteringTutorial(bEnteringTutorial);
				player.setInventoryCredits(2500);
				player.setBankCredits(75000);
				//System.out.println("Create new character shared race to: " + sCharacterSharedRace);
				player.setIFFFileName(sCharacterSharedRace);
				//System.out.println("Character creation.  STF Filename: " + playerTemplate.getSTFFileName() + ", Identifier: " + playerTemplate.getSTFFileIdentifier());
				//player.setSTFFileName(playerTemplate.getSTFFileName());
				//player.setSTFFileIdentifier(playerTemplate.getSTFFileIdentifier());
				//System.out.println("STF Detail: " + playerTemplate.getSTFDetailName() + ", Identifier: " + playerTemplate.getSTFDetailIdentifier());
				//player.setSTFDetailName(playerTemplate.getSTFDetailName());
				//player.setSTFDetailIdentifier(playerTemplate.getSTFDetailIdentifier());
				//System.out.println("Setting new character's account ID to " + client.getAccountID());
				player.setClient(client);
				player.setAccountID(client.getAccountID());
				//player.setPlanetID(Constants.TATOOINE);
				player.setServerID(client.getServer().getServerID());
				//float[] location = server.getStartingCoordinates(sStartingLocation);
				//player.setX(location[0]);
				//player.setZ(location[1]);
				//player.setY(location[2]);
				//randomizing starting location MUAHAHAHAHAHAHA
				/*
				player.setPlanetID(Constants.NABOO);
				player.setX(4960.0f);
				player.setY(-4900.0f);
				player.setZ(0);
	
	            int iRandomStart = SWGGui.getRandomInt(0,DatabaseInterface.getStartingLocations().size());
	
	            StartingLocation L = DatabaseInterface.getStartingLocations().get(iRandomStart);
	            player.setStartingCoordinates(L.getX(), L.getY());
	            player.setPlanetID(L.getPlanetID());
				player.setX(L.getX() + SWGGui.getRandomInt(0,2));
				player.setY(L.getY() + SWGGui.getRandomInt(0,2));
				player.setZ(0);
				 */
	
				player.setFactionID(Constants.FACTION_NEUTRAL);
				/* this is to be done after the tutorial.
				Waypoint startingWaypoint = new Waypoint();
				startingWaypoint.setX(player.getX());
				startingWaypoint.setY(player.getY());
				startingWaypoint.setZ(player.getZ());
				startingWaypoint.setIsActivated(true);
				startingWaypoint.setName("Starting Location");
				startingWaypoint.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
				startingWaypoint.setPlanetCRC(Constants.PlanetCRCForWaypoints[player.getPlanetID()]);
				startingWaypoint.setID(server.getNextObjectID());
				server.addObjectToAllObjects(startingWaypoint, false,false);
				 */
				//System.out.println("Set player's starting location to: X " + player.getX() + ", Z " + player.getZ() + ", Y " + player.getY());
				if(bEnteringTutorial)
				{
					TutorialObject tutorial = new TutorialObject(210,server);
					tutorial.setIsBuilding(true);
					tutorial.addToEntryList(player.getID());
					player.setTutorial(tutorial);
					Cell c = null;
					Enumeration<Cell> cEnum = tutorial.getCellsInBuilding().elements();
					while(cEnum.hasMoreElements())
					{
						Cell tc = cEnum.nextElement();
						if(tc.getCellNum() == 1)
						{
							c = tc;
						}
					}
					player.setCellID(c.getID());
					//tutorial.getVCellsInBuilding().get(0).addCellObject(player);
					System.out.println("Cell ID Set to: " + c.getID() + " IDX: " + c.getCellNum());
					//server.addObjectIDToAllUsedID(tutorial.getID());
					server.addObjectToAllObjects(tutorial,false,false);
					player.setPlanetID(tutorial.getPlanetID());
					player.setX(tutorial.getX());
					player.setY(tutorial.getY());
					player.setZ(tutorial.getZ());
					player.setCellX(7.6f);
					player.setCellY(-2.8f);
					player.setCellZ(0.7f);
					player.setOrientationS(0.7f);
					player.setOrientationW(0.7f);
					tutorial.setOwner(player);
					c.addCellObject(player);
				}
				else
				{
					TutorialObject tutorial = new TutorialObject(211,server);
					tutorial.setIsBuilding(true);
					tutorial.addToEntryList(player.getID());
					player.setTutorial(tutorial);
					Cell c = null;
					Enumeration<Cell> cEnum = tutorial.getCellsInBuilding().elements();
					while(cEnum.hasMoreElements())
					{
						Cell tc = cEnum.nextElement();
						if(tc.getCellNum() == 1)
						{
							c = tc;
						}
					}
					player.setCellID(c.getID());
					System.out.println("Cell ID Set to: " + c.getID() + " IDX: " + c.getCellNum());
					//server.addObjectIDToAllUsedID(tutorial.getID());
					server.addObjectToAllObjects(tutorial,false,false);
					player.setPlanetID(tutorial.getPlanetID());
					player.setX(tutorial.getX());
					player.setY(tutorial.getY());
					player.setZ(tutorial.getZ());
					player.setCellX(28.2f);
					player.setCellY(-159.2f);
					player.setCellZ(0.7f);
					player.setOrientationS(1);
					player.setOrientationW(0);
					tutorial.setOwner(player);
					c.addCellObject(player);
				}
				System.out.println("Player " + player.getID() + " Tutorial Data: " + player.getTutorial().getID() + " Cell:" + player.getCellID());
				player.setActive(true);
				player.setCustomizationData(customizationData);
				if (sCharacterParsedNames.length == 1) {
					player.setFirstName(sCharacterName);
					player.setLastName("");
				} else {
					player.setFirstName(sCharacterParsedNames[0]);
					player.setLastName(sCharacterParsedNames[1]);
				}
				player.setRace(iRaceIndex);
				player.setHamWounds(new int[9]);
				player.setHam(server.getStartingHam(iRaceIndex, sProfession));
	
				player.setBiography(sBiography);
				/*Vector<SkillMods> vStarterSkillMods = server.getSkillModsFromSkillIndex(iProfessionIndex + 1);
				Vector<SkillMods> vBaseLanguageSkillMods = server.getSkillModsFromSkillIndex(Constants.SKILLS_LANGUAGE);
				Vector<SkillMods> vBaseSpeciesSkillMods = server.getSkillModsFromSkillIndex(Constants.SKILLS_SPECIES);
				Vector<SkillMods> vRaceLanguageBaseSkillMods = null;
				Vector<SkillMods> vRaceLanguageSpeakSkillMods = null;
				Vector<SkillMods> vRaceLanguageComprehendSkillMods = null;
				Vector<SkillMods> vRaceSpeciesSkillMods = null;*/
				player.setSkill(Constants.SKILLS_LANGUAGE, true, false);
				player.setSkill(Constants.SKILLS_SPECIES, true, false);
				switch (iRaceIndex) {
				case Constants.RACE_HUMAN_MALE:
				case Constants.RACE_HUMAN_FEMALE:
				{
					player.setSkill(Constants.SKILLS_SPECIES_HUMAN, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_BASIC + i, true, false);
					}
	
					break;
				}
				case Constants.RACE_TRANDOSHAN_MALE:
				case Constants.RACE_TRANDOSHAN_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_TRANDOSHAN, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_TRANDOSHAN + i, true, false);
					}
					break;
				}
				case Constants.RACE_TWILEK_MALE:
				case Constants.RACE_TWILEK_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_TWILEK, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_TWILEK + i, true, false);
						player.setSkill(Constants.SKILLS_LANGUAGE_LEKKU + i, true, false);
					}
	
					break;
				}
				case Constants.RACE_BOTHAN_MALE:
				case Constants.RACE_BOTHAN_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_BOTHAN, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_BOTHAN + i, true, false);
					}
	
	
					break;
				}
				case Constants.RACE_ZABRAK_MALE:
				case Constants.RACE_ZABRAK_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_ZABRAK, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_ZABRAK + i, true, false);
					}
	
	
					break;
				}
				case Constants.RACE_RODIAN_MALE:
				case Constants.RACE_RODIAN_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_RODIAN, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_RODIAN + i, true, false);
					}
	
					break;
				}
				case Constants.RACE_MONCAL_MALE:
				case Constants.RACE_MONCAL_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_MONCAL, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_MONCAL + i, true, false);
					}
	
					break;
				}
				case Constants.RACE_WOOKIEE_MALE:
				case Constants.RACE_WOOKIEE_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_WOOKIEE, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_WOOKIEE + i, true, false);
					}
	
					break;
				}
				case Constants.RACE_SULLUSTAN_MALE:
				case Constants.RACE_SULLUSTAN_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_SULLUSTAN, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_SULLUSTAN + i, true, false);
					}
	
					break;
				}
				case Constants.RACE_ITHORIAN_MALE:
				case Constants.RACE_ITHORIAN_FEMALE: {
					player.setSkill(Constants.SKILLS_SPECIES_ITHORIAN, true, false);
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_ITHORIAN + i, true, false);
					}
	
					break;
				}
				default: {
					System.out.println("Unable to add starting race / language skill mods to player -- unknown race ID " + iRaceIndex);
					break;
				}
				}
				// As a temporary measure, all players will be able to speak and understand Basic.
				if (iRaceIndex != Constants.RACE_HUMAN_MALE && iRaceIndex != Constants.RACE_HUMAN_FEMALE) {
					for (int i = 0; i <= 2; i++) {
						player.setSkill(Constants.SKILLS_LANGUAGE_BASIC + i, true, false);
					}
				}
				//player.setSkill(iProfessionIndex, true, false);
				player.setSkill(iProfessionIndex+1, true, false);
				BitSet skillBits = player.getSkillList();
				System.out.println("Iterating skill bits.");
				for (int i = skillBits.nextSetBit(0); i >= 0; i = skillBits.nextSetBit(i+1)) {
					System.out.println("Skill bit " + i + " set.");
				}
				// Skill bits are set as of here.
				player.setStartingProfession(sProfession);
	
				//System.out.println("Adding " + vStarterSkillMods + " skill mods to the player.");
	
				//player.addSkillMods(vStarterSkillMods);
				try {
					player.setScale(fScale, false, false);
				} catch (Exception e) {
					float[] fScaleBounds = Constants.getScaleBoundsForSpecies(iRaceIndex);
					System.out.println("Error creating character -- received invalid scale factor from client.  Scale received: " + fScale + ", bounds: min[" + fScaleBounds[0]+"],max["+fScaleBounds[1]+"]");
					client.insertPacket(PacketFactory.buildCreateCharacterFailed(lPlayerID, Constants.NAME_DECLINED_CANNOT_CREATE));
					return;
					// D'oh!
				}
				if (bHasHair) {
					TangibleItem hairItem = new TangibleItem();
					//ItemTemplate hairTemplate = dbInterface.getTemplateDataByFilename(sHairObjectName);
					//System.out.println("Received Hair Object Name: " + sHairObjectName);
					hairItem.setIFFFileName(sHairObjectName);
					//hairItem.setCRC(PacketUtils.SWGCrc(sHairObjectName));
					hairItem.setCustomizationData(hairCustomizationData);
					//hairItem.setSTFFileName("hair_name");
					//hairItem.setSTFFileIdentifier(hairTemplate.getSTFFileIdentifier());
					//hairItem.setSTFDetailName("hair_detail");
					hairItem.setName(sHairObjectName, false);
					if (iRaceIndex == Constants.RACE_RODIAN_MALE || iRaceIndex == Constants.RACE_RODIAN_FEMALE) {
						//hairItem.setSTFFileIdentifier("frills");
						//hairItem.setSTFDetailIdentifier("frills");
					} else {
						//hairItem.setSTFFileIdentifier("hair");
						//hairItem.setSTFDetailIdentifier("hair");
					}
					hairItem.setID(server.getNextObjectID());
					server.addObjectToAllObjects(hairItem, false,false);
					hairItem.setOwner(player);
					hairItem.setEquipped(player, 4);
					hairItem.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
					player.addHair(hairItem);
				}
	
				player.setCRC(PacketUtils.SWGCrc(sCharacterRace));
				player.setSharedCRC(PacketUtils.SWGCrc(sCharacterSharedRace));
	
				//System.out.println("Sending Emails To New Player: " + player.getID());
				//Vector<Waypoint> WL = new Vector<Waypoint>();
				//SWGEmail welcomeemail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_welcome_subject, Constants.newbie_mail_welcome_body,null,false);
				//server.queueEmailNewClientMessage(welcomeemail);
				//SWGEmail startloc = new SWGEmail(-1, 0, player.getID(), "Starting Location", "This is Your Starting Location Waypoint.",player.getWaypoints(),false);
				//server.queueEmailNewClientMessage(startloc);
				/*
	            SWGEmail professionEmail = null;
		        switch(server.getSkillIndexFromName(player.getStartingProfession()))
		        {
		            case -1: //collector????? this is an email meant for collector edition owners. alas we will probably never know i they are or not.
		            {
		                professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_collector_subject, Constants.newbie_mail_collector_body,null,false);
		                break;
		            }
		            case 10: //entertainer
		            {
		                professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_social_entertainer_subject, Constants.newbie_mail_social_entertainer_body,null,false);
		                break;
		            }
		            case 30: //scout
		            {
		                professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_outdoors_scout_subject, Constants.newbie_mail_outdoors_scout_body,null,false);
		                break;
		            }
		            case 50://medic
		            {
		                professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_science_medic_subject, Constants.newbie_mail_science_medic_body,null,false);
		                break;
		            }
		            case 70: //artisan
		            {
		                professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_crafting_artisan_subject, Constants.newbie_mail_crafting_artisan_body,null,false);
		                break;
		            }
	
		            case 90: //combat melee basic
		            {
		                professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_combat_brawler_subject, Constants.newbie_mail_combat_brawler_body,null,false);
		                break;
		            }
		            case 109: //marksman
		            {
		                professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_combat_marksman_subject, Constants.newbie_mail_combat_marksman_body,null,false);
		                break;
		            }
		            default: {
		            	System.out.println("Creating a new player with an unknown starting profession.  WTF???");
		            	break;
		            }
		        }
	            server.queueEmailNewClientMessage(professionEmail);
	
				 */
				//player.setFactionID(Constants.FACTION_REBEL);
				player.setFactionRank((byte)1);

				//client.addPlayer(player);
				//server.addNewPlayer(player);
				//System.out.println("Saved new player with name " + player.getFullName());
				// Now, we have to give the player their starter items.
				//done where we select the starting location
				//player.addWaypoint(startingWaypoint, false);
				//server.addObjectToAllObjects(startingWaypoint, false, false);
				PlayerItem pl = player.getPlayData();

				if (data.getIsGM()) {
					pl.setCsrOrDeveloperFlag((byte)1);
				} else if (data.getIsDeveloper()) {
					pl.setCsrOrDeveloperFlag((byte)2);
				} else {
					pl.setCsrOrDeveloperFlag((byte)0);
				}
				dbInterface.saveNewPlayer(player);
				server.triggerLoginLoadNewPlayer(player.getID());
				client.insertPacket(PacketFactory.buildCreateCharacterSuccess(player.getID()));

				//dbInterface.updatePlayer(player,true, false);

				if(player.getTutorial()==null)
				{
					System.out.println("Player Tutorial Object was Null");
				}
				else
				{
					System.out.println("Player Tutorial Object was Good");
				}

			} else {
				System.out.println("Name declined.  Reason code " + iNameResponseCode);
				client.insertPacket(PacketFactory.buildCreateCharacterFailed(lPlayerID, iNameResponseCode));
				server.removeObjectFromAllObjects(lPlayerID);
				client.insertPacket(PacketFactory.buildClientNameDeclinedResponse(sCharacterName, iNameResponseCode));
				client.insertPacket(PacketFactory.buildClientUIErrorMessage("Name declined", "Your character name was invalid.  Please choose a different name."));
				// Delete the player.
				
			}
		} catch (NullPointerException e) {
			System.out.println("Exploded creating new character: " + e.toString());
			System.out.println("Error details follow:");
			System.out.println("Character name: " + sCharacterName);
			e.printStackTrace();
		}
	}

	private void handleChatSendToRoom(SOEInputStream dIn) throws IOException {
		String sMessage = dIn.readUTF16();
		int roomID = dIn.readInt();
		Vector<Player> vObjectsInRange = server.getPlayersAroundObject(player, false);
		for (int i = 0; i < vObjectsInRange.size(); i++ ) {
			Player o = vObjectsInRange.elementAt(i);
			ZoneClient client = o.getClient();
			client.insertPacket(PacketFactory.buildChatRoomMessage(player, sMessage, roomID));
		}
	}

	private void handleClientReadyStringResponse(SOEInputStream dIn) throws IOException {
		//this is how we know this client is ready to roll and receive any other packet types after zoning
		/*String CRSR = */dIn.readUTF();
		// System.out.println(CRSR); //normally commented out
		client.setClientReady();
		client.getPlayer().setOnlineStatus(true);
		//ask for our emails
		client.getServer().queueEmailClientRequestEmails(client);
		//send the players friends list
		client.insertPacket(PacketFactory.buildFriendsListResponse(client.getPlayer()));
		//inform the player of friends on line
		System.out.println("Sending Friends List to newly logged in client.");
		Iterator<PlayerFriends> itr = client.getPlayer().getFriendsList().iterator();
		System.out.println("Friends Count: " + client.getPlayer().getFriendsList().size());
		if(player.getTutorial() != null)
		{
			if(player.getPlanetID() == Constants.TUTORIAL)
			{
				player.getTutorial().setHasEnteredTutorial(true);
			}
		}
		while(itr.hasNext())
		{
			Player fplayer = client.getServer().getPlayer(itr.next().getName());
			if(fplayer.getOnlineStatus())
			{
				System.out.println("Player " + fplayer.getFirstName() + " is on line");
				client.insertPacket(PacketFactory.buildFriendOnlineStatusUpdate(client.getPlayer(), fplayer));
			}
			else
			{
				System.out.println("Player " + fplayer.getFirstName() + " is off line");
				client.insertPacket(PacketFactory.buildFriendOfflineStatusUpdate(client.getPlayer(), fplayer));
			}
		}


		// Buggy code.
		/*Enumeration<ZoneClient> cenum = client.getServer().getAllClients().elements();

		while(cenum.hasMoreElements())
		{
			ZoneClient pf = cenum.nextElement();

			if(pf.getAccountID() != client.getAccountID())
			{
				Iterator<PlayerFriends> itrb = pf.getPlayer().getFriendsList().iterator();
				while(itrb.hasNext())
				{
					if(itrb.next().getName().equalsIgnoreCase(client.getPlayer().getFirstName()))
					{
						System.out.println("Player " + pf.getPlayer().getFirstName() + " notified that " + client.getPlayer().getFirstName() + " is on line");
						pf.insertPacket(PacketFactory.buildFriendOnlineStatusUpdate(pf.getPlayer(), client.getPlayer()));
					}
				}
			}
		}*/
		
		Vector<Player> vConnectedPlayers = client.getServer().getAllOnlinePlayers();
		for (int i = 0; i < vConnectedPlayers.size(); i++) {
			Player tarPlayer = vConnectedPlayers.elementAt(i);
			Vector<PlayerFriends> tarPlayerFriends = tarPlayer.getFriendsList();
			ZoneClient tarClient = tarPlayer.getClient();
			if (tarClient != null) {
				for (int j = 0; j < tarPlayerFriends.size(); j++) {
					PlayerFriends friend = tarPlayerFriends.elementAt(j);
					if (friend.getName().equalsIgnoreCase(player.getFirstName())) {
						tarClient.insertPacket(PacketFactory.buildFriendOnlineStatusUpdate(tarPlayer, player));
					}
				}
			}
		}
	}

	private void handleConnectPlayerMessage(SOEInputStream dIn) throws IOException {
		// Not handled in PlasmaFlow's experimental core.
		//PacketUtils.printPacketToScreen(dIn.getBuffer(), dIn.getBuffer().length, "handleConnectPlayerMessage <-- Working on handling");
	}

	private void handleClientInactivityMessage(SOEInputStream dIn) throws IOException {
		boolean bIsAFK = dIn.readBoolean();
		player.setIsAFK(bIsAFK);
	}

	private void handleObjectControllerMessage(SOEInputStream dIn) throws IOException {
		/*int iObjControllerSubheader =*/ dIn.readInt();

		int subCommandID = dIn.readInt();
		
		//System.out.println("handleObjectControllerMessage: " + Integer.toHexString(subCommandID));
		switch (subCommandID) {
		case Constants.DataTransform: {
			//byte [] dataxform = dIn.getBuffer();
			//PacketUtils.printPacketToScreen(dataxform, dataxform.length, "dataxform");
			handleDataTransform(dIn);
			break;
		}
		case Constants.DataTransformWithParent: {
			// byte [] dataxform = dIn.getBuffer();
			//PacketUtils.printPacketToScreen(dataxform, dataxform.length, "CELL:dataxform");
			handleDataTransformWithParent(dIn);
			break;
		}
		case Constants.CraftingExperimentationMessage: {
			handleCraftingExperimentationMessage(dIn);
			break;
		}
		case Constants.InsertItemIntoSchematicSlot: {
			handleInsertItemIntoSchematicSlot(dIn);
			break;
		}
		case Constants.RemoveItemFromSchematicSlot: {
			handleRemoveItemFromSchematicSlot(dIn);
			break;
		}
		case Constants.CommandQueueEnqueue: {
			handleCommandQueueEnqueue(dIn);
			break;
		}
		case Constants.CommandQueueDequeue: {
			handleCommandQueueDequeue(dIn);
			break;
		}
		case Constants.ObjectMenuRequest: {
			handleObjectMenuRequest(dIn);
			break;
		}
		case Constants.SetTargetRequest: {
			handleSetPlayerTarget(dIn);
			break;
		}
		case Constants.RefreshMissionList: {
			handleRefreshMissionList(dIn);
			break;
		}
		case Constants.AcceptMission: {
			handleAcceptMission(dIn);
			break;
		}
		case Constants.StartSecureTrade:
		{
			handleSecureTrade(dIn);
			break;
		}
		case Constants.enqueueRetrieveHarvesterResource:{
			handleRetieveHarvesterResource(dIn);
			break;
		}
		case Constants.ImageDesignChangeMessage:{
			handleImageDesignChangeMessage(dIn);
			break;
		}
		case Constants.CraftingSetCustomization: {
			handleCraftingSetCustomizationData(dIn);
			break;
		}
		default: {
			System.out.println("Received unknown ObjController message with ID 0x" + Integer.toHexString(subCommandID));
			//PacketUtils.printPacketData(dIn.getBuffer());
			byte [] buff = dIn.getBuffer();
			PacketUtils.printPacketToScreen(buff, buff.length, "handleObjectControllerMessage:SubCID:0x" + Integer.toHexString(subCommandID));
			break;
		}
		}
	}

	private void handleDataTransform(SOEInputStream dIn) throws IOException {
		//System.out.println("handleDataTransform");
		dIn.skip(12);
		int moveCounter = dIn.readInt();
		float orientN = dIn.readFloat();
		float orientS = dIn.readFloat();
		float orientE = dIn.readFloat();
		float orientW = dIn.readFloat();
		float currentX = dIn.readFloat();
		float currentZ = dIn.readFloat();
		float currentY = dIn.readFloat();
		float fVelocity = dIn.readFloat();
		float MovAngle = 0;
		if(((orientN*orientN)+(orientS*orientS)+(orientE*orientE)) > 0.0)   {
			if(orientW > 0.0 && orientE < 0.0) {
				orientW *= -1;
			}
			MovAngle = (float)(2.0 * Math.acos(orientW));
		} else {
			MovAngle = 0.0f;
		}
		player.setMovementAngle(MovAngle);

		player.updatePosition(moveCounter, orientN, orientS, orientE, orientW, currentX, currentZ, currentY, fVelocity,0);
		//server.setHeightMapAtLocation(player.getPlanetID(), currentX, currentY, currentZ);

	}

	private void handleTransFive(SOEInputStream dIn) throws IOException {
		// System.out.println("handleTransFive");
		// PacketUtils.printPacketToScreen(dIn.getBuffer(), dIn.getBuffer().length, "handleTransFive");
		// if(true)
		// {
		//     return;
		// }
		/*
		 * THIS  Packet changes length and its the only indicator
		 * that i can reliably find to know when its a cell trans five or a non cell trans five.
		 * so i added a packet length function to the SOEStream Object so i could measure before getting the bytes.
		 * This packet actually changes if its on cell or not.
		 * handleTransFive while in cell
                -----> Print Packet To Screen. <-----
                -----> handleTransFive <-----
                -----> Len:70 <-----
                <------------------------------------------------>
                05 00
                46 5E CE 80
                21 00 00 00
                F1 00 00 00
                5E F4 A6 55 00 00 00 00 //pid
                E4 2B //short
                00 00 //short
                01 00 00 00 //upd
                D7 3D 13 00 00 00 00 00 //cell id
                00 00 00 00 //n
                12 86 7D 3F //s
                00 00 00 00 //e
                32 15 0E BE //w
                B2 D9 8C C0 //x
                4D FD 7F 3F //y
                B2 86 A7 41 //z
                00 00 00 00 //mov
                <------------------------------------------------>
                handleTransFive while in world
                -----> Print Packet To Screen. <-----
                -----> handleTransFive <-----
                -----> Len:63 <-----
                <------------------------------------------------>
                05 00
                46 5E CE 80
                21 00 00 00
                71 00 00 00
                5E F4 A6 55 00 00 00 00
                9C EE 02 00
                76 00 00 00 //upd
                00 00 00 00 //n
                80 3F 76 3F //s
                00 00 00 00 //e
                54 F8 8B BE //w
                66 2B 40 C5 //x
                00 00 A0 40 //y
                0E 12 08 45 //z
                00 00 00 00 //mov
                C2
                <------------------------------------------------>
                handleTransFive//while in cell
                handleTransFive
                -----> Print Packet To Screen. <-----
                -----> handleTransFive <-----
                -----> Len:71 <-----
                <------------------------------------------------>
                05 00
                46 5E CE 80
                21 00 00 00
                F1 00 00 00
                5E F4 A6 55 00 00 00 00
                00 ED 00 00
                E2 00 00 00
                D9 3D 13 00 00 00 00 00
                00 00 00 00 //n
                8D 21 7D 3F //s
                00 00 00 00 //e
                D6 DF 18 3E //w
                DB 25 A8 BF //x
                69 FF 7F 3F //y
                85 E9 3E 40 //z
                31 08 AC 40 //mov
                FD  //transfive ctr
                <------------------------------------------------>             *
		 * */

		//dIn.skip(12);

		int transFiveLen = dIn.getBufferLength();
		//System.out.println("Length: " + transFiveLen);
		/*int objControllerMessage =*/ dIn.readInt();
		/*int unknown =*/ dIn.readInt();
		/*int dataTransform =*/ dIn.readInt();

		/*long characterID =*/ dIn.readLong();
		//Player p = server.getPlayer(characterID);
		//System.out.println("Character ID seen as: " + characterID + " Actual ID: " + player.getID());
		/*int transFiveCounter =*/ dIn.readInt();
		//System.out.println("transFive Counter: " + transFiveCounter );

		int moveCounter = dIn.readInt();
		//System.out.println("moveCounter: " + moveCounter);
		long currentCell = 0;
		if(transFiveLen >= 70)
		{
			currentCell = dIn.readLong();
		}
		//System.out.println("Transfive CellID: " + currentCell);
		float orientN = dIn.readFloat();
		float orientS = dIn.readFloat();
		float orientE = dIn.readFloat();
		float orientW = dIn.readFloat();
		float currentX = dIn.readFloat();
		float currentZ = dIn.readFloat();
		float currentY = dIn.readFloat();
		float fVelocity = dIn.readFloat();


		//System.out.println("oI: " + orientN + " oS: " + orientS + " oK: " + orientE + " oW: " + orientW);
		//System.out.println("cX: " + currentX + "cZ: " + currentZ + " cY: " + currentY  );
		// System.out.println("fVelocity: " + fVelocity);

		if(currentCell == 0)
		{
			player.updatePosition(moveCounter, orientN, orientS, orientE, orientW, currentX, currentZ, currentY, fVelocity, currentCell);
		}
		else if(currentCell >= 1)
		{
			player.updateInteriorPosition(moveCounter, orientN, orientS, orientE, orientW, currentX, currentZ, currentY, fVelocity, currentCell);
		}
		//server.setHeightMapAtLocation(player.getPlanetID(), currentX, currentY, currentZ);
	}

	private void handleDataTransformWithParent(SOEInputStream dIn) throws IOException{
		//System.out.println("handleDataTransformWithParent");
		/*
                PacketUtils.printPacketToScreen(dIn.getBuffer(), dIn.getBuffer().length, "handleDataTransformWithParent");
                if(true)
                {
                    return;
                }
		 * */
		//Packet Break Courtesy PlasmaFlow .Net core Muahahahahaha!
		/*
		 * *********** HANDLE_ObjController_DataTransformWithParent ***********
		 ***Packet byte count: 58***
		 *****************************************
                        B8 A7 31 01 00 00 00 00 //long //character id
                        7D 55 00 00 //upd counter
                        1E 00 00 00 //Mov Counter
                        C5 D1 19 00 00 00 00 00 //cell id
                        00 00 00 00 //oI
                        B2 F3 7F 3F //oS
                        00 00 00 00 //oK
                        D5 B9 9E 3C //oW
                        EC 8D 84 3E //x
                        E9 D5 3F 3F //y
                        61 E8 9B C2 //z
                        31 08 AC 00 //current travel speed

                        04 AF//crc
		 ***********Packet End***********
		 *
		 * */

		// We don't chare about the interior heightmap of various objects -- they're inside, after all.  We probably won't be spawning too much inside buildings.
		// Besides, they'd all have a relative Z of 0, since their coordinates are inside the cell.
		/*long characterID =*/ dIn.readLong();
		// System.out.println("---- handleDataTransformWithParent Decodifimacation ----");
		// System.out.println("Char ID Seen In Packet as: " + characterID + " Actual ID: " + player.getID());
		//Player p = server.getPlayer(characterID);
		//if (p == null) throw new NullPointerException("Error:  Invalid player ID received: " + characterID);
		/*int updateCounter =*/ dIn.readInt(); // This is an Update COunter of some sort since it increments.
		//System.out.println("UpdCounter: " + updateCounter);
		int movementUpdateCounter = dIn.readInt();
		// System.out.println("MovementUpdCounter: " + movementUpdateCounter);
		long cellID = dIn.readLong();
		//System.out.println("CellID: " + cellID);
		//System.out.println("Moving inside Cell ID " + cellID);
		float orientN = dIn.readFloat();
		float orientS = dIn.readFloat();
		float orientE = dIn.readFloat();
		float orientW = dIn.readFloat();
		// System.out.println("oI: " + orientN + " oS: " + orientS + " oK: " + orientE + " oW: " + orientW);
		float currentCellX = dIn.readFloat();
		float currentCellZ = dIn.readFloat();
		float currentCellY = dIn.readFloat();
		//System.out.println("cX: " + currentCellX + "cZ: " + currentCellZ + " cY: " + currentCellY  );
		float fVelocity = dIn.readFloat();
		//System.out.println("fVelocity: " + fVelocity);
		player.updateInteriorPosition(movementUpdateCounter, orientN, orientS, orientE, orientW, currentCellX, currentCellZ, currentCellY, fVelocity, cellID);
	}

	private void handleObjectMenuRequest(SOEInputStream dIn) throws IOException{

		/*
                00 09 00 09
                05 00
                46 5E CE 80
                23 00 00 00
                46 01 00 00
                48 68 01 FF 00 00 00 00 //player ID
                00 00 00 00 //spacer
                47 D0 48 91 00 00 00 00 //object id
                48 68 01 FF 00 00 00 00 //player id
                03 00 00 00 //button count - from 1 not 0
                01 00 0C 01 00 00 00 00 //button data
                02 00 07 01 00 00 00 00 //button data
                03 00 0E 01 00 00 00 00 //button data
                05 //button request id <-- when we respond we send this same id back since this request is an enqueue and the response is a dequeue message.
		 * button breakdown
		 * 01 00 0C 01 00 00 00 00 //button data
		 * |  |  |  |  |
		 * |  |  |  |  |Button Text String, always 0 from client // this has to be read as unicode and sent as unicode.
		 * |  |  |  |Action Location 0x01 when client side and 0x03 when server side
		 * |  |  |Button Command ID - This button is 0x0C or 12 decimal
		 * |  |Parent Button for this button 00 means no parent, 01 and above means its below the mentioned button
		 * |Button Number - this is the position of the button in the radial menu. Always 1 is first button
                    <------------------------------------------------>
		 */

		//first we read our values.
		/*long lPlayerID =*/ dIn.readLong();
		dIn.readInt(); // read the spacer
		long lItemID = dIn.readLong();
		//get the item info:

		SOEObject itemForRadials = null;

		if(lItemID <= 19999999)
		{
			if(player.getCellID() >= 1)
			{
				Cell ParentObject = (Cell)server.getObjectFromAllObjects(player.getCellID());
				if(ParentObject!=null)
				{
					// System.out.println("Player is in Cell:" + player.getCellID() + " Cell Template ID:" + ParentObject.getTemplateID() + " Class:" + ParentObject.getClass());
					Hashtable<Long,SOEObject> htCellObjects = ParentObject.getCellObjects();
					if(htCellObjects.containsKey(lItemID))
					{
						itemForRadials = htCellObjects.get(lItemID);
						//System.out.println("Found Child Object inside the Cell " + lItemID);
					}
				}
			}
			else
			{
				itemForRadials = server.getObjectFromAllObjects(lItemID);
			}
		}
		else
		{
			itemForRadials = server.getObjectFromAllObjects(lItemID);
		}
		/*long lItemActuator = */dIn.readLong();
		int iButtonCount = dIn.readInt();
		Vector<RadialMenuItem> vReceivedRadials = new Vector<RadialMenuItem>();
		for(int i = 0; i< iButtonCount; i++)
		{
			byte ButtonNumber = dIn.readByte();
			byte ParentButton = dIn.readByte();
			char CommandID = (char)dIn.readByte();
			byte ActionLocation = dIn.readByte();
			String ButtonText = dIn.readUTF16();
			RadialMenuItem rmi = new RadialMenuItem(ButtonNumber,ParentButton,CommandID,ActionLocation,ButtonText);
			rmi.setiItemCRC(itemForRadials.getCRC());
			rmi.setiItemTemplate(itemForRadials.getTemplateID());
			vReceivedRadials.add(rmi);
		}
		byte bRequestID = dIn.readByte();
		System.out.println("--------------------------------------");
		System.out.println("Radial Received for Request ID: " + bRequestID + " Total Buttons: " + iButtonCount + " Item ID: " + lItemID);
		Iterator<RadialMenuItem> itr = vReceivedRadials.iterator();
		while(itr.hasNext())
		{
			RadialMenuItem R = itr.next();
			System.out.println("--------------------------------------");
			System.out.println("Button Number:" + R.getButtonNumber());
			System.out.println("Button Parent:" + R.getParentButton());
			int cid = R.getCommandID();
			System.out.println("Button CommandID:" + cid);
			System.out.println("Button ActLoc:" + R.getActionLocation());
			System.out.println("Button Text:" + R.getButtonText());
			System.out.println("--------------------------------------");
		}

		//here is where we make a decision of returning the default received buttons or radials for this specific item.


		//System.out.println("Item for Radials Name:" + itemForRadials.getIFFFileName() + " Class:" + itemForRadials.getClass());
		boolean bDeafultRadials = false;
		if(itemForRadials != null)
		{
			if (itemForRadials instanceof CreaturePet) {
				CreaturePet tItem = (CreaturePet) itemForRadials;
				Collection<RadialMenuItem> vMyRadialMenu = tItem.getRadialMenus(client).values();

				if(vMyRadialMenu == null)
				{
					//System.out.println("Inserting Received Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
				}
				else if(vMyRadialMenu.size() >= 1)
				{
					//System.out.println("Inserting Retrieved Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vMyRadialMenu, bRequestID));
				}
				else
				{
					//System.out.println("Inserting Default Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
					bDeafultRadials = true;
				}
			}
			else if (itemForRadials instanceof TangibleItem) {
				TangibleItem tItem = (TangibleItem) itemForRadials;
				Collection<RadialMenuItem> vMyRadialMenu = tItem.getRadialMenus(client).values();

				if(vMyRadialMenu == null)
				{
					//System.out.println("Inserting Received Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
				}
				else if(vMyRadialMenu.size() >= 1)
				{
					//System.out.println("Inserting Retrieved Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vMyRadialMenu, bRequestID));
				}
				else
				{
					//System.out.println("Inserting Default Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
					bDeafultRadials = true;
				}
			} else if (itemForRadials instanceof IntangibleObject) {

				IntangibleObject oItem = (IntangibleObject)itemForRadials;
				Collection<RadialMenuItem> vMyRadialMenu = oItem.getRadialMenus(client).values();
				if(vMyRadialMenu == null)
				{
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
				}
				else if(vMyRadialMenu.size() >= 1)
				{
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vMyRadialMenu, bRequestID));
				}
				else
				{
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
					bDeafultRadials = true;
				}

			} else if (itemForRadials instanceof Player) {

				Player pItem = (Player)itemForRadials;
				Collection<RadialMenuItem> vMyRadialMenu = pItem.getRadialMenus(client).values();
				if(vMyRadialMenu == null)
				{
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
				}
				else if(vMyRadialMenu.size() >= 1)
				{
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vMyRadialMenu, bRequestID));
				}
				else
				{
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
					bDeafultRadials = true;
				}

			} else if (itemForRadials instanceof SOEObject) {

				Collection<RadialMenuItem> vMyRadialMenu = itemForRadials.getRadialMenus(client).values();

				if(vMyRadialMenu == null)
				{
					//System.out.println("Inserting Received Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
				}
				else if(vMyRadialMenu.size() >= 1)
				{
					//System.out.println("Inserting Retrieved Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vMyRadialMenu, bRequestID));
				}
				else
				{
					//System.out.println("Inserting Default Radials");
					client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
					bDeafultRadials = true;
				}
			}else {
				System.out.println("Received use item request for object " + lItemID + ", class name: " + itemForRadials.getClass().getName());
			}
		}
		else
		{
			if(lItemID <= 19999999)
			{
				System.out.println("World Object Not Matched for Radials Not Found in all Objects");
			}

			//default received menu send
			client.insertPacket(PacketFactory.buildObjectControllerMessage_RadialsResponse(player, lItemID, vReceivedRadials, bRequestID));
		}

		if(bDeafultRadials)
		{
			if(player.isDev())
			{
				DatabaseInterface.saveReceivedRadials(vReceivedRadials);
				server.reloadServerRadials();
			}
		}
	}

	private void handleRefreshMissionList(SOEInputStream dIn) throws IOException {

		/*
                84 1A 18 EE 00 00 00 00 //player ID
                00 00 00 00 //spacer
                00 17 //some kind of terminal value????
                5B 3F 1B 00 00 00 00 00 //terminal ID
		 */
		/*long playerID =*/ dIn.readLong();
		dIn.readInt();
		short unkShort = dIn.readShort();  //this number seems to vary from session to session!!!! Serial Stamp id Encoding Key????
		long terminalID = dIn.readLong();
		//System.out.println("Terminal Refresh Mission Selected: TermID:" + terminalID + " Variable UnkShort:" + unkShort);

		SOEObject t = server.getObjectFromAllObjects(terminalID);

		//System.out.println("Object Class: " + t.getClass());
		if(t!=null)
		{
			if(t.getPlanetID() == 255)
			{
				client.getServer().updateWorldObjectPlanet(terminalID, player.getPlanetID());
				t.setPlanetID(player.getPlanetID());
			}
			Terminal T = (Terminal)t;
			T.setUnkMissionRefreshShort(unkShort);
			T.refreshMissionList(client);
			return;
		}
		else
		{
			System.out.println("Terminal ID not Found: " + terminalID);
		}

		client.insertPacket(PacketFactory.buildChatSystemMessage("Mission terminals are not yet implemented."));
		client.insertPacket(PacketFactory.buildChatSystemMessage("Received Refresh Mission List for terminal id " + terminalID));
	}

	private void handleAcceptMission(SOEInputStream dIn) throws IOException {
		// byte [] packet = dIn.getBuffer();
		// PacketUtils.printPacketToScreen(packet, packet.length, "handleAcceptMission");
		long playerID = dIn.readLong();
		dIn.readInt();
		long lMisionIDAccepted = dIn.readLong();
		long lTerminalID = dIn.readLong();
		byte b1 = dIn.readByte();
		System.out.println("Player ID " + playerID + " lMisionIDAccepted:" + lMisionIDAccepted + " lTerminalID:" + lTerminalID + " byte:" + b1 + " Mission Bag ID:" + player.getMissionBag().getID() );

		//Player ID 1466630561 lMisionIDAccepted:126419686 lTerminalID:1722570 byte:3 Mission Bag ID:2638852487
		TangibleItem playerMissionBag = player.getMissionBag();
		Vector<MissionObject> vMLRL = playerMissionBag.getMissionObjectRefreshList();
		boolean bFound = false;
		for(int i = 0; i < vMLRL.size() && !bFound; i++)
		{
			MissionObject m = vMLRL.get(i);
			if(m.getID() == lMisionIDAccepted)
			{
				bFound = true;
				System.out.println("Found mission.");
				
				//playerMissionBag.addMissionObjectToAcceptedList(m);
			}
		}
	}

	private void handleSetPlayerTarget(SOEInputStream dIn) throws IOException {
		/*long lPlayerID =*/ dIn.readLong(); //we grab this just in case
		dIn.readInt(); //bypass this separator
		long lNewTargetID = dIn.readLong(); //grab the target id
		if (lNewTargetID != player.getID()) {
			player.setTargetID(lNewTargetID);
		}
	}

	private void handleCommandQueueDequeue(SOEInputStream dIn) throws IOException {
		/*long objectID =*/ dIn.readLong();
		/*int tickCounter =*/ dIn.readInt();
		int iActionID = dIn.readInt();
		/*float fActionTimeout =*/ dIn.readFloat();
		/*int errorMessageID =*/ dIn.readInt();
		/*int errorMessageSubHeader =*/ dIn.readInt();
		//System.out.println("Read all data for incoming CommandQueueDequeue -- bounce back to client.");
		//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, iActionID, errorMessageID, errorMessageSubHeader));
		player.removeQueuedCommandByActionID(iActionID);
	}
	
	//private int tickCounter = 0;
	private void handleCommandQueueEnqueue(SOEInputStream dIn) throws IOException {
		//System.out.println("Command queue enqueue.");
		//byte p[] = dIn.getBuffer();
		//PacketUtils.printPacketToScreen(p, p.length,"handleCommandQueueEnqueue");
		/**
		 * packet begins at the player id from this point
                    05 00
                    46 5E CE 80 <--Obj Controller Message
                    23 00 00 00 <--
                    16 01 00 00 <--
                    16 73 64 52 00 00 00 00 <--Player ID
                    00 00 00 00 <--TC
                    00 00 00 00 <--command id
                    26 17 FC 18 <--Command CRC
                    00 00 00 00 00 00 00 00 <-Target ID
                    00 00 00 00 <--UTF16 Parameters separated by spaces.
                    F3 19
		 */

		long objectID = dIn.readLong(); // This should be "me" or the source.
		//       System.out.println("handleCommandQueueEnqueue ObjectID " + objectID);
		// This is the ID of the player who has spoken.
		int tc = dIn.readInt();
		//tickCounter = tc;
		int commandID = dIn.readInt();  // If this is non-zero, queue it in the player queue.  Otherwise, dequeue it immediately.
		int commandCRC = dIn.readInt();
		//System.out.println("CRC: " + Integer.toHexString(commandCRC));
		long targetID = dIn.readLong(); // This is the thing to be acted on.
		String[] parameters = dIn.readUTF16().split(" ");
		//EnqueueCommand ceq = new EnqueueCommand(objectID, tc, commandID, commandCRC, targetID, parameters);
		CommandQueueItem cq = new CommandQueueItem(commandCRC, 0.0f, 0, commandID, -1, player);
		player.queueCommand(cq);
		int iStance = player.getStance();
		if (iStance == Constants.STANCE_INCAPACITATED || iStance == Constants.STANCE_DEAD) {
			cq.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_IN_STANCE);
			cq.setStateInvoked(player.getCommandQueueErrorStance());
			return;
		}
		//System.out.println("CommandQueueEnqueue built for crc " + Integer.toHexString(commandCRC) + ", command ID " + Integer.toHexString(commandID));
		//player.queueCommand(commandCRC, 1.0f, 0, 0);
		/*
                if(targetID == 1787874054){
                    System.out.println("Maroj Melon on the Move! Command ID:" + commandID + " CommandCRC:" + commandCRC);
                    System.out.println("Params:");
                    for(int i = 0; i < parameters.length; i++)
                    {
                        System.out.println(" Param: " + i + " : " + parameters[i]);
                    }
                }*/
		switch (commandCRC) {
		case Constants.DropSkill: {
			handleDropSkill(targetID, parameters);
			break;
		}
		case Constants.spatialChatInternal: {
			handleSpatialChatInternal(targetID, parameters);
			break;
		}
		case Constants.flourish: {
			handleFlourishRequest(targetID, parameters);
			break;
		}
		case Constants.socialInternal: {
			handleSocialInternal(targetID, parameters);
			break;
		}
		case Constants.serverdestroyobject: {
			handleServerDestroyObject(targetID, parameters);
			break;
		}
		case Constants.placestructure: {
			handlePlaceStructureMessage(targetID, parameters);
			break;
		}
		case Constants.purchaseticket: {
			handlePurchaseTicket(targetID, parameters);
			break;
		}
		case Constants.mount: {
			handleMountObject(targetID, parameters);
			break;
		}
		case Constants.dismount: {
			handleDismountObject(targetID, parameters);
			break;
		}
		case Constants.requestwaypointatposition: {
			handleCreateWaypointAtPosition(targetID, parameters);
			//CommandQueueItem cq = new CommandQueueItem(commandCRC, 0, 0, 0, 0, player);
			//cq.setCommandID(commandID);
			//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, cq));
			break;
		}
		case Constants.opencontainer: {
			handleOpenContainer(targetID, parameters);
			break;
		}
		case Constants.closeContainer: {
			handleCloseContainer(targetID, parameters);
			break;
		}
		case Constants.synchronizeduilisten: {
			handleSynchronizedUIListen(targetID, parameters);
			break;
		}
		case Constants.synchronizeduistoplisten: {
			handleSynchronizedUIStopListen(targetID, parameters);
			break;
		}
		case Constants.harvestergetresourcedata:{
			//PacketUtils.printPacketToScreen(dIn.getBuffer(), dIn.getBuffer().length, "harvestergetresourcedata");
			handleHarvesterGetResourceData(targetID, parameters);
			break;
		}
		case Constants.harvesteractivate:{
			handleHarvesterActivate(targetID, parameters);
			break;
		}
		case Constants.harvesterdeactivate:{
			handleHarvesterDeactivate(targetID, parameters);
			break;
		}
		case Constants.harvesterselectresource:{
			handleHarvesterSelectResource(targetID, parameters);
			break;
		}
		case Constants.requestcraftingsession: {
			handleRequestCraftingSession(targetID, parameters);
			break;
		}
		case Constants.cancelCraftingSession: {
			handleCancelCraftingSession(targetID, parameters);
			break;
		}
		case Constants.commandstartmusic: {
			handleStartMusic(cq, targetID, parameters);
			break;
		}
		case Constants.commandstartdance: {
			handleStartDance(cq, targetID, parameters);
			client.insertPacket(PacketFactory.buildObjectControllerDequeueItemTransfer(player, commandID, commandCRC));
			break;
		}
		case Constants.stopmusic:
		{
			handleStopMusic(cq, targetID, parameters);
			break;
		}
		case Constants.imagedesign:{
			handleImageDesign(targetID, parameters);
			break;
		}
		case Constants.getattributesbatch: {

			handleGetAttributesBatch(targetID, parameters);
			break;
		}
		case Constants.emailrelatedenqueurequest: {
			handleEmailEnqueue(targetID, parameters);
			break;
		}
		case Constants.ClientRequestSlashWay: {
			handleSlashWaypoint(targetID, parameters);
			//CommandQueueItem cq = new CommandQueueItem(commandCRC, 0, 0, 0, 0, player);
			//cq.setCommandID(commandID);
			//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, cq));
			break;
		}
		case Constants.ToggleWaypointStatus: {
			handleUpdateWaypointStatus(targetID, parameters);
			//CommandQueueItem cq = new CommandQueueItem(commandCRC, 0, 0, 0, 0, player);
			//cq.setCommandID(commandID);
			//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, cq));

			break;
		}
		case Constants.ClientRenameWaypoint: {
			handleRenameWaypoint(targetID, parameters);
			//CommandQueueItem cq = new CommandQueueItem(commandCRC, 0, 0, 0, 0, player);
			//cq.setCommandID(commandID);
			//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, cq));
			break;
		}
		case Constants.RequestSurveySession: {
			handleRequestSurveySession(cq, targetID, parameters);
			break;
		}
		case Constants.RequestCoreSample: {
			handleRequestCoreSample(cq, targetID, parameters);
			break;
		}
		case Constants.toggleawayfromkeyboard: {
			handleAFKUpdate(targetID, parameters);
			break;
		}
		case Constants.findfriend: {
			handleFindFriendRequest(targetID, parameters);
			break;
		}
		case Constants.addfriend: {
			handleAddFriendRequest(targetID, parameters);
			break;
		}
		case Constants.removefriend: {
			handleRemoveFriendRequest(targetID, parameters);
			break;
		}
		case Constants.burstrun: {
			player.playerBurstRun(cq);
			break;
		}
		case Constants.SetGodMode: {
			if(bIsGM || bIsDeveloper) {
				client.insertPacket(PacketFactory.buildChatSystemMessage("Command not yet implemented."));//"Received admin command kick."));
				System.out.println("Authorized Account: " + account.getUsername() + " used SetGodMode command.");
			} else {
				player.getClient().insertPacket(PacketFactory.buildChatSystemMessage("You do not have permission to use this command."));
				System.out.println("Unauthorized Account: " + account.getUsername() + " attempting to use SetGodMode command.");
			}
			break;
		}
		case Constants.Tip: {
			handleTip(targetID, parameters);
			break;
		}
		case Constants.SitClient: {
			handleSitRequest(cq, targetID, parameters);
			break;
		}
		case Constants.KneelClient: {
			handleKneelRequest(cq, targetID, parameters);
			break;
		}
		case Constants.ProneClient: {
			handleProneRequest(cq, targetID, parameters);
			break;
		}
		case Constants.StandClient: {
			handleStandRequest(cq, targetID, parameters);
			break;
		}
		case Constants.npcconversationstart: {
			//handleStartConversation(targetID, parameters);
			break;
		}
		case Constants.npcconversationstop: {
			//handleStopConversation(targetID, parameters);
			break;
		}
		case Constants.who: {
			//handleWhoRequest(targetID, parameters);
			break;
		}
		case Constants.lfg: {
			//handleLFGUpdate(targetID, parameters);
			break;
		}
		case Constants.roleplay: {
			//handleRoleplayUpdate(targetID, parameters);
			break;
		}
		case Constants.helper: {
			//handleHelperUpdate(targetID, parameters);
			break;
		}
		case Constants.searchable: {
			//handleSearchableUpdate(targetID, parameters);
			break;
		}
		case Constants.transferitemweapon: {
			handleTransferItemWeapon(targetID,parameters);
			break;
		}
		case Constants.transferitemmisc: {
			
			handleTransferItemMisc(targetID,parameters);
			client.insertPacket(PacketFactory.buildObjectControllerDequeueItemTransfer(player, commandID, commandCRC));
			//CommandQueueItem cq = new CommandQueueItem(commandCRC, 0, 0, 0, 0, player);
			//cq.setCommandID(commandID);
			//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, cq));
			break;
		}
		case Constants.transferitemarmor:
		{
			handleTransferItemArmor(targetID,parameters);
			client.insertPacket(PacketFactory.buildObjectControllerDequeueItemTransfer(player, commandID, commandCRC));
			//CommandQueueItem cq = new CommandQueueItem(commandCRC, 0, 0, 0, 0, player);
			//cq.setCommandID(commandID);
			//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, cq));
			break;
		}
		case Constants.setMoodInternal: {
			handleSetMoodInternal(targetID, parameters);
			break;
		}
		case Constants.setMood: {
			handleSetMood(targetID, parameters);
			break;
		}
		case Constants.requestBadges: {
			handleRequestBadges(targetID, parameters);
			break;
		}
		case Constants.requestBiography: {
			handleRequestBiography(targetID, parameters);
			break;
		}
		case Constants.requestCharacterSheetInfo: {
			handleRequestCharacterSheetInfo(targetID, parameters);
			break;
		}
		case Constants.requestStatMigrationData: {
			handleRequestStatMigrationData(targetID, parameters);
			break;
		}
		case Constants.npcconversationselect: {
			handleNPCConversationSelect(targetID, parameters);
			break;
		}
		case Constants.boardtransport:
		{
			handleBoardTransport(targetID,parameters);
			break;
		}
		case Constants.ejectrequest:
		{
			handleEjectRequest(targetID,parameters);
			break;
		}
		case Constants.unstickrequest:
		{
			handleUnstickRequest(targetID,parameters);
			break;
		}
		case Constants.moveitem:
		{
			handleMoveItem(targetID,parameters);
			break;
		}
		case Constants.rotateitem:
		{
			handleRotateItem(targetID,parameters);
			break;
		}
		case Constants.getIgnoreList:
		{
			System.out.println("Client has requested Ignore list.");
			break;
		}
		case Constants.paymaintenance:
		{
			handlePayMaintenance(targetID,parameters);
			break;
		}
		case Constants.destroystructure:
		{
			handleDestroyStructure(targetID,parameters);
			break;
		}
		case Constants.logout:
		{
			player.playerLogout(cq);
			break;
		}
		case Constants.diagnoseWounds: {
			handleDiagnoseWounds(targetID, parameters);
			break;
		}
		case Constants.healDamage: {
			handleHealDamage(cq, targetID, parameters);
			break;
		}
		case Constants.healWound: {
			handleHealWound(cq, targetID, parameters);
			break;
		}
		case Constants.tendDamage: {
			handleTendDamage(cq, targetID, parameters);
			break;
		}
		case Constants.tendWound: {
			handleTendWound(cq, targetID, parameters);
			break;
		}
		case Constants.groupInvite:{
			handleGroupInvite(targetID, parameters);
			break;
		}
		case Constants.acceptGroupInvite:{
			handleAcceptGroupInvite(targetID, parameters);
			break;
		}
		case Constants.declineGroupInvite:{
			handleDeclineGroupInvite(targetID, parameters);
			break;
		}
		case Constants.leaveGroup:{
			handleLeaveGroup(targetID, parameters);
			break;
		}
		case Constants.disbandGroup:{
			handleDisbandGroup(targetID, parameters);
			break;
		}
		case Constants.makeGroupLeader:{
			handleMakeGroupLeader(targetID, parameters);
			break;
		}
		case Constants.RequestDraftComponentMessage: {
			handleRequestDraftComponentMessage(targetID, parameters);
			break;
		}
		case Constants.RequestResourceWeightMessage: {
			handleRequestResourceWeightMessage(targetID, parameters);
			break;
		}
		case Constants.SetSpokenLanguage: {
			handleSetSpokenLanguage(targetID, parameters);
			break;
		}
		case Constants.SelectStartingLocation:{
			handleSelectStartingLocation(targetID, parameters);
			//CommandQueueItem cq = new CommandQueueItem(commandCRC, 0, 0, 0, 0, player);
			//cq.setCommandID(commandID);
			//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, cq));
			break;
		}

		case Constants.teachSkill: {
			handleTeachSkill(targetID, parameters);
			break;
		}
		case Constants.resourcecontainertransfer:{
			handleResourceContainerTransfer(targetID, parameters);
			break;
		}
		case Constants.resourcecontainersplit:{
			handleResourceContainerSplit(targetID, parameters);
			break;
		}
		case Constants.addpower:{
			handleAddPower(targetID, parameters);
			break;
		}
		case Constants.harvesterdiscardhopper:{
			handleHarvesterDiscardHopper(targetID, parameters);
			break;
		}
		case Constants.selectDraftSchematicToCraft: {
			handleSelectDraftSchematicToCraft(targetID, parameters);
			break;

		}
		case Constants.forage:{
			handleForage(targetID, parameters);
			break;
		}
		case Constants.stopdance:{
			handleStopDance(cq, targetID, parameters);
			break;
		}
		case Constants.spotlight:
		case Constants.colorlights:
		case Constants.dazzle:
		case Constants.firejet:
		case Constants.ventriloquism:
		case Constants.distract:
		case Constants.smokebomb:
		{
			handleEntertainerEffect(cq, commandCRC, targetID, parameters);
			break;
		}
		case Constants.watch:
		{
			handleWatch(cq, targetID, parameters);
			break;
		}
		case Constants.listen:
		{
			handleListen(cq, targetID, parameters);
			break;
		}
		case Constants.stoplisten:
		{
			this.handleStopListen(cq, targetID, parameters);
			break;
		}
		case Constants.stopwatch:
		{
			handleStopWatch(targetID, parameters);
			break;
		}
		case Constants.nextCraftingStage:{
			handleNextCraftingStage(targetID, parameters);
			break;
		}
		case Constants.craftingCreatePrototype: {
			//PacketUtils.printPacketToScreen(dIn.getBuffer(), "CreatePrototype packet");
			handleCreatePrototype(targetID, parameters);
			break;
		}
		case Constants.createManufactureSchematic: {
			handleCreateManufactureSchematic(targetID, parameters);
			break;
		}
		case Constants.setSkillTitle:{
			handleSetSkillTitle(targetID, parameters);
			break;
		}
		case Constants.maskScent: {
			handleMaskScent(targetID, parameters);
			break;
		}
		case Constants.declarePeace: {
			handleDeclarePeace(cq, targetID, parameters);
			break;
		}
		default: {
			// Maybe it's a combat move?
			if (!handleCombatAction(commandCRC, targetID, parameters, cq)) {
				
				System.out.println("Received unknown commandQueueEnqueue with CRC " + Integer.toHexString(commandCRC) +  ".");
				//client.insertPacket(PacketFactory.buildChatSystemMessage(s));
				for (int i = 0; i < parameters.length; i++) {
					System.out.println("Parameter[" + i +"] = [" + parameters[i] + "]");
				}
				PacketUtils.printPacketToScreen(dIn.getBuffer(), "CommandQueueEnqueue");
			} //else {
			//	PacketUtils.printPacketToScreen(dIn.getBuffer(), "Combat action");
			//}
			break;
		}
		}
	}

	private void handleSpatialChatInternal(long targetID, String[] sParameters) throws IOException {
		
		//Instance variables.
		Player target = server.getPlayer(targetID);				//This is the player TARGETED by the spatial chat
		short language = Short.parseShort(sParameters[4]);		//This is the player's current language.
		short howlModifier = Short.parseShort(sParameters[1]);	//Check if the player is howling.
		boolean bSendSpatial = true;							//Variable to toggle sending chat.
		
		// The client sends us 5 characters of data before the actual message begins.
		StringBuffer buff = new StringBuffer();
		for (int i = 5; i < sParameters.length; i++) {
			buff.append(sParameters[i]).append(" ");
		}
		String sRealMessage = buff.toString();
		
		//Send chat to pet object for processing.
		for(int i = 0; i < player.getCalledPets().size();i++)
		{
			CreaturePet pet = player.getCalledPets().get(i);
			pet.processSpatialCommand(client, sParameters);
		}

		//Send chat to friend's player objects for parsing.
		for(int i = 0; i < player.getFriendPets().size();i++)
		{
			CreaturePet pet = player.getFriendPets().get(i);
			pet.processSpatialCommand(client, sParameters);
		}
		
		//If the player is in the tutorial, flag this as his first chat to proceed in the tutorial.
		if(player.getTutorial() != null)
		{
			if(player.getTutorial().isWaitingChatEvent())
			{
				player.getTutorial().setChatEvent(true);
			}
		}

		//Admin command variables
		if(sRealMessage.toLowerCase().startsWith("@")){
			
			//Don't send this message to surrounding players.
			bSendSpatial = false;
			
			//Admin command processing.
			if(bIsGM || bIsDeveloper) {

				//This line is for debugging chat commands, do not delete it.
				//Comment/uncomment it if you must.
				server.broadcastSystemMessage(player, "== Command Debugging Information ==");
				server.broadcastSystemMessage(player, String.format("Exact message received: \"%s\".", sRealMessage));
				if(player.getTargetID() != 0) {
					server.broadcastSystemMessage(player, "Current target ID: " + player.getTargetID());
				}
				if(sRealMessage.contains(" ")) {
					server.broadcastSystemMessage(player, "Command Arguments Length: " + sRealMessage.split(" ").length);
				}
				
				try {
					if(sRealMessage.toLowerCase().startsWith("@travel")) {

						SUIWindow W = new SUIWindow(player);
						W.setWindowType(Constants.SUI_TRAVEL_SELECT_DESTINATION);
						W.setOriginatingObject(player);
						String [] sList = new String [server.getAllTravelDestinations().size()];

						for(int i = 0; i < server.getAllTravelDestinations().size(); i++) {
							sList[i] = ( Constants.PlanetNames[server.getAllTravelDestinations().get(i).getDestinationPlanet()] + " - " + server.getAllTravelDestinations().get(i).getDestinationName());
						}

						String WindowTypeString = "handleSUI";
						String DataListTitle = "Select a travel destination";
						String DataListPrompt = "This is the Dev travel Destination Window. Select a Destination to Travel to.";
						client.insertPacket(W.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));

					} else if (sRealMessage.toLowerCase().startsWith("@addbuff")) {
						String[] sRealBuffer = sRealMessage.split(" ");
						int buffIndex = Integer.parseInt(sRealBuffer[1]);
						float buffLengthSec = Float.parseFloat(sRealBuffer[2]);
						client.insertPacket(PacketFactory.buildObjectControllerMessageBuffStat(player, Constants.BUFF_EFFECTS[buffIndex], buffLengthSec));
						
					} else if (sRealMessage.toLowerCase().startsWith("@addpoison")) {
						String[] sRealBuffer = sRealMessage.split(" ");
						int hamIndex = Integer.parseInt(sRealBuffer[1]);
						int potency = Integer.parseInt(sRealBuffer[2]);
						long lDurationMS = 60000l;
						player.applyPoison(hamIndex, potency, lDurationMS);
						
					} else if (sRealMessage.toLowerCase().startsWith("@adddisease")) {
						String[] sRealBuffer = sRealMessage.split(" ");
						int hamIndex = Integer.parseInt(sRealBuffer[1]);
						int potency = Integer.parseInt(sRealBuffer[2]);
						long lDurationMS = 60000l;
						player.applyDisease(hamIndex, potency, lDurationMS);
						
					} else if (sRealMessage.toLowerCase().startsWith("@addbleed")) {
						String[] sRealBuffer = sRealMessage.split(" ");
						int hamIndex = Integer.parseInt(sRealBuffer[1]);
						int potency = Integer.parseInt(sRealBuffer[2]);
						long lDurationMS = 60000l;
						player.applyBleed(hamIndex, potency, lDurationMS);

					} else if (sRealMessage.toLowerCase().startsWith("@addfire")) {
						String[] sRealBuffer = sRealMessage.split(" ");
						int hamIndex = Integer.parseInt(sRealBuffer[1]);
						int potency = Integer.parseInt(sRealBuffer[2]);
						long lDurationMS = 60000l;
						player.applyFire(hamIndex, potency, lDurationMS);

					} else if(sRealMessage.toLowerCase().startsWith("@disablepacket")) {
						SWGGui.setPacketLogEnabled(false);
						client.insertPacket(PacketFactory.buildChatSystemMessage("PacketLog Disabled."));
						DataLog.logEntry("Packet Logging Disabled by Dev Command: Dev Player Name: " + player.getFullName(), "ZoneClientThread", Constants.LOG_SEVERITY_INFO, ZoneServer.ZoneRunOptions.bLogToConsole, true);
					} else if(sRealMessage.toLowerCase().startsWith("@enablepacket")) {
						SWGGui.setPacketLogEnabled(true);
						client.insertPacket(PacketFactory.buildChatSystemMessage("PacketLog Enabled."));
						DataLog.logEntry("Packet Logging Enabled by Dev Command: Dev Player Name: " + player.getFullName(), "ZoneClientThread", Constants.LOG_SEVERITY_INFO, ZoneServer.ZoneRunOptions.bLogToConsole, true);
					} else if(sRealMessage.toLowerCase().startsWith("@startinglocations")) {
						client.insertPacket(PacketFactory.buildObjectControllerStartingLocationsWindow(player, DatabaseInterface.getStartingLocations()));
					} else if(sRealMessage.toLowerCase().startsWith("@warptoplayer")) {

						String sRealBuffer[] = sRealMessage.split(" ");
						String PlayerName = sRealBuffer[1];
						Player p = server.getPlayer(PlayerName);

						if(p != null) {
							client.insertPacket(PacketFactory.buildChatSystemMessage("Warping to player " + PlayerName));
							this.player.playerWarp(p.getX(),p.getY(),p.getZ(), p.getCellID(), p.getPlanetID());
						}

					} else if(sRealMessage.toLowerCase().startsWith("@warpplayertome")) {

						String sRealBuffer[] = sRealMessage.split(" ");
						String PlayerName = sRealBuffer[1];
						Player p = server.getPlayer(PlayerName);

						if(p != null) {
							client.insertPacket(PacketFactory.buildChatSystemMessage("Warping player " + PlayerName + " to Your Location"));
							p.playerWarp(player.getX(),this.player.getY(),this.player.getZ(), this.player.getCellID(), this.player.getPlanetID());
						}

					} else if(sRealMessage.toLowerCase().startsWith("@setwarp")) {

						String sRealBuffer[] = sRealMessage.split(" ");
						player.iWarpOperation = Integer.parseInt(sRealBuffer[1]);

					} else if(sRealMessage.toLowerCase().startsWith("@up")) {

						player.setCellZ(player.getCellZ() + Constants.ELEVATOR_UP_DOWN_AMOUNT);
						client.insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(player,Constants.ELEVATOR_UP_EFFECT));
						client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformWithParentToClient(player, 0x0B));

					} else if(sRealMessage.toLowerCase().startsWith("@down")) {

						player.setCellZ(player.getCellZ() - Constants.ELEVATOR_UP_DOWN_AMOUNT);
						client.insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(player,Constants.ELEVATOR_DOWN_EFFECT));
						client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformWithParentToClient(player,  0x0B));

					} else if(sRealMessage.toLowerCase().startsWith("@warp")) {

						float x, y,z;
						String sRealBuffer[] = sRealMessage.split(" ");

						if(sRealBuffer.length == 3) {
							System.out.println("Warp 1");
							x = Float.parseFloat(sRealBuffer[1]);
							y = Float.parseFloat(sRealBuffer[2]);
							//player.playerWarp(x, y, 0, player.getCellID(),player.getPlanetID());
							player.setX(x);
							player.setY(y);
							client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformToClient(player, 11));
						} else if(sRealBuffer.length == 4) {
							System.out.println("Warp 2");
							x = Float.parseFloat(sRealBuffer[1]);
							z = Float.parseFloat(sRealBuffer[2]);
							y = Float.parseFloat(sRealBuffer[3]);
							//player.playerWarp(x, y, z, player.getCellID(),player.getPlanetID());
							player.setX(x);
							player.setY(y);
							player.setZ(z);
							client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformToClient(player, 11));
						} else if(sRealBuffer.length == 5) {
							System.out.println("Warp 3");
							x = Float.parseFloat(sRealBuffer[1]);
							z = Float.parseFloat(sRealBuffer[2]);
							y = Float.parseFloat(sRealBuffer[3]);
							int planetid = Integer.parseInt(sRealBuffer[4]);

							if(planetid >= 0 && planetid < Constants.PlanetNames.length) {
								//player.playerWarp(x, y, z, player.getCellID(),planetid);
								player.playerTravel(x, y, z, planetid);
								//client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformToClient(player, 11));
							}
						} else if(sRealBuffer.length == 6) {
							System.out.println("Warp 4");
							x = Float.parseFloat(sRealBuffer[1]);
							z = Float.parseFloat(sRealBuffer[2]);
							y = Float.parseFloat(sRealBuffer[3]);
							int planetid = Integer.parseInt(sRealBuffer[4]);

							if(planetid >= 0 && planetid <= Constants.PlanetNames.length) {
								long cellid = Long.parseLong(sRealBuffer[5]);
								//cellid = player.getCellID()+1;

								if(cellid >= 2) {
									//player.playerWarp(x, y, z, cellid,planetid);
									player.setCellX(x);
									player.setCellY(y);
									player.setCellZ(z);
									player.setPlanetID(planetid);
									player.setCellID(cellid);
									Cell c = (Cell)client.getServer().getObjectFromAllObjects(cellid);
									Structure s = c.getBuilding();
									player.setX(s.getX());
									player.setY(s.getY());
									player.setZ(s.getZ());
									c.addCellObject(player);
									//client.insertPacket(PacketFactory.buildUpdateContainmentMessage(player, c, false));
									//client.insertPacket(PacketFactory.buildObjectControllerPlayerDataTransformWithParentToClient(player, 11));
									System.out.println("Warp to Cell: " + cellid);
									TravelDestination T = new TravelDestination("Unknown", planetid, 0, false,s.getX(),s.getY(),s.getZ());
									T.setCellID(cellid);
									player.playerTravelToCell(T);

								}
							}
						}
					} else if(sRealMessage.toLowerCase().startsWith("@coords")) {
						if(client.getPlayer().getCellID() == 0) {
							System.out.println("Coords: X:" + client.getPlayer().getX() + " Y:" + client.getPlayer().getY() + " Z:" + client.getPlayer().getZ() + " N:" + client.getPlayer().getOrientationN() + " S:" + client.getPlayer().getOrientationS() + " E:" + client.getPlayer().getOrientationE() + " W:" + client.getPlayer().getOrientationW() + " CellID:" + client.getPlayer().getCellID() + " Angle:" + client.getPlayer().getMovementAngle());
							client.insertPacket(PacketFactory.buildChatSystemMessage("Coords: X:" + client.getPlayer().getX() + " Y:" + client.getPlayer().getY() + " Z:" + client.getPlayer().getZ() + " N:" + client.getPlayer().getOrientationN() + " S:" + client.getPlayer().getOrientationS() + " E:" + client.getPlayer().getOrientationE() + " W:" + client.getPlayer().getOrientationW() + " CellID: " + client.getPlayer().getCellID()  + " Angle:" + client.getPlayer().getMovementAngle()  ));
						} else {
							Cell c = (Cell)server.getObjectFromAllObjects(player.getCellID());
							Structure s = c.getBuilding();
							if(s instanceof TutorialObject)
							{
								//TutorialObject s = (TutorialObject)server.getObjectFromAllObjects(c.getBuildingID());
								client.insertPacket(PacketFactory.buildChatSystemMessage("Coords: X:" + client.getPlayer().getCellX() + " Y:" + client.getPlayer().getCellY() + " Z:" + client.getPlayer().getCellZ() + " N:" + client.getPlayer().getOrientationN() + " S:" + client.getPlayer().getOrientationS() + " E:" + client.getPlayer().getOrientationE() + " W:" + client.getPlayer().getOrientationW() + " CellID: " + client.getPlayer().getCellID()  + " Angle:" + client.getPlayer().getMovementAngle()));
							} else {
								System.out.println(">>>-------------------Player Coords-----------------<<<");
								System.out.println("Structure Facing: " + s.getIFacingDirection());
								System.out.println("Structure X: " + s.getX());
								System.out.println("Structure Y: " + s.getY());
								System.out.println("Structure Z: " + s.getZ());
								System.out.println("Structure S: " + s.getOrientationS()+ " DEG:" + Player.radiansToDegrees(s.getOrientationS()));
								System.out.println("Structure W: " + s.getOrientationW()+ " DEG:" + Player.radiansToDegrees(s.getOrientationW()));
								System.out.println("-------WORLD-------");
								System.out.println("X: " + player.getX());
								System.out.println("Y: " + player.getY());
								System.out.println("Z: " + player.getZ());
								System.out.println("--------CELL-------");
								System.out.println("X: " + player.getCellX());
								System.out.println("Y: " + player.getCellY());
								System.out.println("Z: " + player.getCellZ());
								System.out.println("-------------------");
								//System.out.println("N: " + client.getPlayer().getOrientationN() + " DEG:" + player.radiansToDegrees(client.getPlayer().getOrientationN()));
								System.out.println("S: " + player.getOrientationS() + " DEG:" + Player.radiansToDegrees(player.getOrientationS()));
								//System.out.println("E: " + client.getPlayer().getOrientationE() + " DEG:" + player.radiansToDegrees(client.getPlayer().getOrientationE()));
								System.out.println("W: " + player.getOrientationW() + " DEG:" + Player.radiansToDegrees(player.getOrientationW()));
								System.out.println("-------------------");
								System.out.println("CellID: " + player.getCellID());
								System.out.println("Angle: " + player.getMovementAngle() + " DEG:" + Player.radiansToDegrees(client.getPlayer().getMovementAngle()));
								System.out.println("S + W : " + (player.getOrientationS() + player.getOrientationW()) + " DEG: " + Player.radiansToDegrees((player.getOrientationS() + player.getOrientationW())));
								System.out.println(">>>-------------------------------------------------<<<");
								client.insertPacket(PacketFactory.buildChatSystemMessage("Coords: X:" + client.getPlayer().getCellX() + " Y:" + client.getPlayer().getCellY() + " Z:" + client.getPlayer().getCellZ() + " N:" + client.getPlayer().getOrientationN() + " S:" + client.getPlayer().getOrientationS() + " E:" + client.getPlayer().getOrientationE() + " W:" + client.getPlayer().getOrientationW() + " CellID: " + client.getPlayer().getCellID()  + " Angle:" + client.getPlayer().getMovementAngle()  ));
							}
						}
					} else if (sRealMessage.toLowerCase().startsWith("@moveto")) {
						String[] message = sRealMessage.split(" ");
						float x = Float.parseFloat(message[1]);
						float y = Float.parseFloat(message[2]);
						player.updatePosition(player.getMoveUpdateCount()+1,player.getOrientationN(),player.getOrientationS(),player.getOrientationE(),player.getOrientationW(),x,player.getZ(),y,player.getVelocity(),0);
					} else if(sRealMessage.toLowerCase().startsWith("@help")) {
						SUIWindow W = new SUIWindow(player);
						W.setOriginatingObject(player);
						W.setWindowType(Constants.SUI_DEVELOPER_COMMAND_LIST);
						String WindowTypeString = "handleSUI";
						String WindowTitle = "Shards of the Force Developer Commands";
						String WindowPromptContent = "";
						WindowPromptContent += (" @travel  -  Opens a SUI Window with Travel Destinations to Choose From.\r\n");
						WindowPromptContent += (" @warptoplayer [playername]  -  Warps us to the player name specified.\r\n");
						WindowPromptContent += (" @warpplayertome [playername] -  Warps the player name specified to our location.\r\n");
						WindowPromptContent += (" @warp [x] [y]  -  Warps us to the indicated coordinates within same planet.\r\n");
						WindowPromptContent += (" @warp [x] [z] [y]  -  Warps us to the indicated coordinates within same planet.\r\n");
						WindowPromptContent += (" @warp [x] [z] [y] [p] -  Warps us to the indicated coordinates within indicated planet.\r\n");
						WindowPromptContent += (" @warp [x] [z] [y] [p] [cell] -  Warps us to the indicated coordinates within indicated planet and cell id.\r\n");
						WindowPromptContent += (" @placeterminal - Opens a SUI Window of available terminals to place.\r\n");
						WindowPromptContent += (" @placetrainer - Opens a SUI Window of available Skill Trainers to place.\r\n");
						WindowPromptContent += (" @clearbadges - For Testing Badges, Removes all badges from your player.\r\n");
						WindowPromptContent += (" @punchlist - Displays a list of items that require testing.\r\n");
						WindowPromptContent += (" @addpunchitem - Adds a new Tester Punch List Item - Syntax: @addpunchitem [int Priority] [Item Text].\r\n");
						WindowPromptContent += (" @removepunchitem - Removes a Tester Punch List Item - Syntax: @removepunchitem [int ItemID].\r\n");
						WindowPromptContent += (" @bugreport - Inserts a new Bug Report into the bug database - Syntax: @bugreport [report text].\r\n");
						WindowPromptContent += (" @spawnlair - Spawns a new lair based on a random template - Syntax: @spawnlair .\r\n");
						WindowPromptContent += (" @spawnlair - Spawns a new lair based on the template chosen - Syntax: @spawnlair [templateID].\r\n");
                        WindowPromptContent += (" @disablepacket - Disables Server Packet Logging.\r\n");
                        WindowPromptContent += (" @enablepacket - Enables Server Packet Logging.\r\n");



						client.insertPacket(W.SUIScriptMessageBox(client, WindowTypeString, WindowTitle,WindowPromptContent,false,false, 0, 0));
					} else if (sRealMessage.toLowerCase().startsWith("@testspatial")) {
						byte[] packet = PacketFactory.buildObjectControllerMessageSpatial(
								player,
								null,
								player,
								(short)player.getPlayData().getCurrentLanguageID(),
								"ui_craft",
								"completed_prototype",
								0,
								null,
								null,
								null,
								0,
								null,
								null,
								null,
								0,
								null, 
								null,
								//"exp_n",
								//"jedi_general",
								null,
								0,
								0f,
								true
						);
						client.insertPacket(PacketFactory.buildChatSystemMessage("Sending test spatial packet."));
						client.insertPacket(packet);
						client.insertPacket(PacketFactory.buildChatSystemMessage("Sent"));
						//PacketUtils.printPacketToScreen(packet, "STF Spatial test");

					} else if (sRealMessage.toLowerCase().startsWith("@givexp")) {
						String[] sRealBuffer = sRealMessage.split(" ");
						String sXPName = sRealBuffer[1];
						int amountToGrant = Integer.parseInt(sRealBuffer[2]);
						player.updateExperience(null, DatabaseInterface.getExperienceIDFromName(sXPName), amountToGrant);
					} else if (sRealMessage.toLowerCase().startsWith("@findresource")) {
						System.out.println("Find resource by name...");
						String[] sRealBuffer = sRealMessage.split(" ");
						String sResourceName = sRealBuffer[1];
						SpawnedResourceData resource = server.getResourceManager().getResourceByName(sResourceName);
						if (resource == null) {
							client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown resource with name " + sResourceName));
							return;
						} else {
							if (!resource.isSpawned()) {
								client.insertPacket(PacketFactory.buildChatSystemMessage("Error: Resource " + sResourceName + " is no longer available for sampling or harvesting."));
								return;
							} else {
								Waypoint nearbySpawn = resource.getNearestSpawnToCoordinates(player.getX(), player.getY());
								nearbySpawn.setPlanetID(player.getPlanetID());
								nearbySpawn.setID(server.getNextObjectID());
								nearbySpawn.setPlanetCRC(Constants.PlanetCRCForWaypoints[player.getPlanetID()]);
								nearbySpawn.setIsActivated(true);
								nearbySpawn.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
								server.addObjectToAllObjects(nearbySpawn, false, false);
								player.addSurveyWaypoint(nearbySpawn);
								client.insertPacket(
										PacketFactory.buildChatSystemMessage(
												"survey",
												"survey_waypoint",
												0l, 
												"",
												"",
												"",
												0l, 
												"",
												"",
												"",
												0l, 
												"",
												"",
												"",
												0, 
												0f, false
										));


							}
						}

					} else if(sRealMessage.toLowerCase().startsWith("@animateharvester")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						int iAnimation = Integer.parseInt(sRealBuffer[1]);
						short iOperand = Short.parseShort(sRealBuffer[2]);
						Structure s = (Structure)server.getObjectFromAllObjects(player.getTargetID());
						client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)3,(short)1,iOperand, s,iAnimation));
					} else if(sRealMessage.toLowerCase().startsWith("@setextractionrate")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						int iRate = Integer.parseInt(sRealBuffer[1]);
						Harvester s = (Harvester)server.getObjectFromAllObjects(player.getTargetID());
						s.setBaseExtractionRateMS(iRate);

					} else if(sRealMessage.toLowerCase().startsWith("@sethoppersize")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						int iSize = Integer.parseInt(sRealBuffer[1]);
						Structure s = (Structure)server.getObjectFromAllObjects(player.getTargetID());
						s.setIOutputHopperSize(iSize);

					} else if(sRealMessage.toLowerCase().startsWith("@displayresources")) {

						for(int i = 0; i < Constants.PlanetNames.length - 1; i++)
						{
							String sRealBuffer[] = sRealMessage.split(" ");
							String sDelimiter = sRealBuffer[1];
							Vector<SpawnedResourceData> vSRD = server.getResourceManager().getResourcesByPlanetID(i);
							System.out.println("-----------Resources for " + Constants.PlanetNames[i] + "---------------------");
							Enumeration<SpawnedResourceData> rdEnum = vSRD.elements();
							while(rdEnum.hasMoreElements())
							{
								SpawnedResourceData rd = rdEnum.nextElement();
								if(rd.getResourceType().toLowerCase().contains(sDelimiter) && rd.getIsSpawnedOnPlanet(i))
								{
									System.out.println("Type: " + rd.getResourceType() + " Name: " + rd.getName() + " On Planet: " + rd.getIsSpawnedOnPlanet(i) + " : " + Constants.PlanetNames[i]);
								}
							}
							System.out.println("-----------End Resources for " + Constants.PlanetNames[i] + "---------------------");
						}

					} /*else if(sRealMessage.toLowerCase().startsWith("@destroystructure")) {

						//If the player has a target.
						if(player.getTargetID() != 0) {
							
							//If player is inside a house.
							if(player.getCellID() == 0) {
								
								//Get the target.
								SOEObject targetObject = server.getObjectFromAllObjects(player.getTargetID());
								
								//If the target is a terminal.
								if(targetObject instanceof Terminal) {
									
									//Get the target terminal.
									Terminal targetTerminal = (Terminal) targetObject;
									
									//Check if the target terminal is a Structure Control Terminal or Structure Sign
									if(targetTerminal.getTerminalType() == Constants.TERMINAL_TYPES_STRUCTURE_MAINTENANCE  || targetTerminal.getTerminalType() == Constants.TERMINAL_TYPES_STRUCTURE_SIGN) {
										
										//Get this terminal's parent.
										SOEObject targetTerminalParent = server.getObjectFromAllObjects(targetTerminal.getParentID());
										
										//Check if the target terminal belongs to a Structure.
										if(targetTerminalParent instanceof Structure) {
											
											//Get the terminal's parent structure.
											Structure targetStructure = (Structure) targetTerminalParent;
											
											//Get the structure's deed.
											Deed targetStructureDeed = (Deed) server.getObjectFromAllObjects(targetStructure.getDeedID());
											
											//Redeed structure.
											targetStructureDeed.redeedStructure(targetStructure, client);
										} else {
											
											//This sign doesn't belong to a structure, world object?
											client.insertPacket(PacketFactory.buildChatSystemMessage("Target terminal does not belong to a structure, this command only destroys player structures."));
										}
									} else {
										
										//Player has to target the sign or structure terminal.
										client.insertPacket(PacketFactory.buildChatSystemMessage("You must target a structure terminal or structure sign before using this command."));
									}
								} else {
									
									//Player has to target the sign or structure terminal.
									client.insertPacket(PacketFactory.buildChatSystemMessage("You must target a structure terminal or structure sign before using this command."));
								}
							} else {
								
								//Player is in a cell.
								client.insertPacket(PacketFactory.buildChatSystemMessage("You must be outside before using this command."));
							}
						} else {
							
							//Player has to target the sign or structure terminal.
							client.insertPacket(PacketFactory.buildChatSystemMessage("You must target a structure terminal or structure sign before using this command."));
						}
					}*/ else if(sRealMessage.toLowerCase().startsWith("@healwounds")) {
						int [] iWounds = player.getHamWounds();
						int [] iMaxHam = player.getMaxHam();

						for(int i = 0 ; i < iWounds.length; i++)
						{
							player.updateHAMWounds(i, -iMaxHam[i], true);
						}
					} else if(sRealMessage.toLowerCase().startsWith("@givedev")) {
						SOEObject o = server.getObjectFromAllObjects(player.getTargetID());
						if(o instanceof Player)
						{
							Player p = (Player)o;
							AccountData a = server.getAccountDataFromLoginServer((int)p.getAccountID());
							a.setIsDeveloper(true);
							client.insertPacket(PacketFactory.buildChatSystemMessage("Dev Given to " + p.getFullName()));
						}
					} else if(sRealMessage.toLowerCase().startsWith("@givegm")) {
						SOEObject o = server.getObjectFromAllObjects(player.getTargetID());
						if(o instanceof Player)
						{
							Player p = (Player)o;
							AccountData a = server.getAccountDataFromLoginServer((int)p.getAccountID());
							a.setIsGM(true);
							client.insertPacket(PacketFactory.buildChatSystemMessage("GM Given to " + p.getFullName()));
						}
					} else if(sRealMessage.toLowerCase().startsWith("@woundme")) {
						int [] iWounds = player.getHamWounds();
						int [] iMaxHam = player.getMaxHam();

						for(int i = 0 ; i < iWounds.length; i++)
						{
							iWounds[i] = (iMaxHam[i]/2);
							player.updateHAMWounds(i, iWounds[i], true);
						}

					} else if(sRealMessage.toLowerCase().startsWith("@listresourcetypes")) {
						client.insertPacket(PacketFactory.buildChatSystemMessage("====Resource Type Names===="));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Aluminum"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Animal Bones"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Armophous Gemstone"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Avian Bones"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Avian Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Beans"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Berries"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Bristley Hide"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Carbonate Ore"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Carnivore Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Conifer Wood"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Copper"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Crustacean Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Crystalline Gemstone"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Deciduous Wood"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Domesticated Corn"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Domesticated Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Domesticated Milk"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Domesticated Oats"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Domesticated Rice"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Domesticated Wheat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Eggs"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Evergreen Wood"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Extrusive Ore"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Fiberplast"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Fish Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Flowers"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Fungus"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Greens"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Herbivore Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Inert Gas"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Insect Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Intrusive Ore"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Iron"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Leathery Hide"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Liquid Petrochemical Fuel"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Lubricating Oil"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Mollusk Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Polymer"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Radioactive"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Reactive Gas"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Reptilian Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Scaley Hide"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Siliclastic Ore"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Solar Energy"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Solid Petrochemical Fuel"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Steel"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Tubers"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Ferrous Metal"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Gemstone"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Igneous Ore"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Inert Gas"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Liquid Petrochemical Fuel"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Non-Ferrous Metal"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Radioactive"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Reactive Gas"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Sedimentary Ore"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unknown Solid Petrochemical Fuel"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Water"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Wild Corn"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Wild Meat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Wild Milk"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Wild Oats"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Wild Rice"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Wild Wheat"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Wind Energy"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Wooly Hide"));
						client.insertPacket(PacketFactory.buildChatSystemMessage("====END Resource Type Names END===="));

					} else if(sRealMessage.toLowerCase().startsWith("@increaseskillmods")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						String mod = sRealBuffer[1];
						int iValue = Integer.parseInt(sRealBuffer[2]);
						player.increaseSkillModValue(mod, iValue, true);
					} else if(sRealMessage.toLowerCase().startsWith("@viewmyskillmods")) {

						Vector<SkillMods> vMSM = player.getSkillModsList();
						String [] sSKM = new String[vMSM.size()];
						for(int i = 0; i < sSKM.length;i++)
						{
							SkillMods s = vMSM.get(i);
							sSKM[i] = s.getName() + " sv:" + s.getSkillModStartingValue()+ " mv:" + s.getSkillModModdedValue();
						}
						SUIWindow w = new SUIWindow(player);
						w.setWindowType(Constants.SUI_DEVELOPER_LIST_SKILLMODS);
						w.setOriginatingObject(player);
						String WindowTypeString = "handleSUI";
						String DataListTitle = "My Skill Mods";
						String DataListPrompt = "These are your skill modifiers";
						client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sSKM, null, 0, 0));
					} else if(sRealMessage.toLowerCase().startsWith("@testcustomdataobject")) {
						CustomData c = new CustomData(player.getCustomData(),player.getID(),player.getRaceID());
						String sRealBuffer[] = sRealMessage.split(" ");
						int ioperand = Integer.parseInt(sRealBuffer[1]);
						byte operand = 0;
						if(ioperand >= 128)
						{
							operand = (byte)(ioperand - 256);
						}
						else
						{
							operand = (byte)ioperand;
						}
						int ival = Integer.parseInt(sRealBuffer[2]);
						byte val = 0;
						if(ival >= 128)
						{
							val = (byte)(ival - 256);
						}
						else
						{
							val = (byte)ival;
						}
						byte def = 0;
						if(sRealBuffer[3]!=null && !sRealBuffer[3].isEmpty())
						{
							int idef = Integer.parseInt(sRealBuffer[3]);

							if(idef >= 128)
							{
								def = (byte)(idef - 256);
							}
							else
							{
								def = (byte)idef;
							}
						}

						c.changeCustomizationValue(operand,val,def,true);
						player.setCustomizationData(c.getCustomizationData());
						client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)4, player, player.getCustomData()));

					} else if(sRealMessage.toLowerCase().startsWith("@getcustomdata")) {

						byte [] mcd = player.getCustomData();
						PacketUtils.printPacketToScreen(mcd,mcd.length, "Custom Data");
						PacketUtils.decodeCustomData(mcd,player.getRaceID());

					} else if(sRealMessage.toLowerCase().startsWith("@getcustomhairdata")) {

						TangibleItem t = player.getHair();
						byte [] chd = t.getCustomData();
						PacketUtils.printPacketToScreen(chd,chd.length, "Custom Hair Data");

					} else if(sRealMessage.toLowerCase().startsWith("@getresource")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						if(sRealBuffer.length >= 2)
						{
							String sResourceName = "";
							for(int i = 1; i < sRealBuffer.length; i++)
							{
								sResourceName += sRealBuffer[i];
								sResourceName += " ";
							}
							if(sResourceName.endsWith(" "))
							{
								sResourceName = sResourceName.substring(0,sResourceName.length() - 1);
							}
							Vector<SpawnedResourceData> vR = new Vector<SpawnedResourceData>();
							for(int i = 0; i < Constants.PlanetNames.length - 1; i++)
							{
								vR.addAll(server.getResourceManager().getResourcesByPlanetID(i));
							}
							int iRequested = -1;
							for(int i =0; i < vR.size(); i++)
							{
								SpawnedResourceData sRD = vR.get(i);
								if(sRD.getResourceType().equalsIgnoreCase(sResourceName))
								{
									iRequested = i;
									break;
								}
							}
							if(iRequested >= 1)
							{
								SpawnedResourceData sRD = vR.get(iRequested);
								ResourceContainer rc = new ResourceContainer();
								rc.setID(server.getNextObjectID());
								rc.setResourceSpawnID(sRD.getID());
								rc.setStackQuantity(100000, false);
								//--------------
								int containerTemplateID = sRD.getResourceContainerTemplateID();
								rc.setTemplateID(containerTemplateID);
								rc.setName(sRD.getResourceType(), false);
								rc.setCustomizationData(null);
								rc.setEquipped(player.getInventory(), -1);
								rc.setConditionDamage(0, false);
								rc.setMaxCondition(100, false);
								rc.addBitToPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
								rc.setResourceType(sRD.getIffFileName());
								rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_NAME, sRD.getName()));
								rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_CLASS, sRD.getResourceClass() + " " + sRD.getResourceType()));
								short coldResist = sRD.getColdResistance();
								short conductivity = sRD.getConductivity();
								short decayResist = sRD.getDecayResistance();
								short entangleResist = sRD.getEntangleResistance();
								short flavor = sRD.getFlavor();
								short heatResist = sRD.getHeatResistance();
								short malleability = sRD.getMalleability();
								short overallQuality  = sRD.getOverallQuality();
								short potentialEnergy = sRD.getPotentialEnergy();
								short shockResist = sRD.getShockResistance();
								short unitToughness = sRD.getUnitToughness();

								if (coldResist != 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_COLD_RESIST, String.valueOf(coldResist)));
								}
								if (conductivity != 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_CONDUCTIVITY, String.valueOf(conductivity)));
								}
								if (decayResist != 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_DECAY_RESIST, String.valueOf(decayResist)));
								}
								if (entangleResist != 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_ENTANGLE_RESISTANCE, String.valueOf(entangleResist)));
								}
								if (flavor != 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_FLAVOR, String.valueOf(flavor)));
								}
								if (heatResist!= 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_HEAT_RESIST, String.valueOf(heatResist)));
								}
								if (malleability != 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_MALLEABILITY, String.valueOf(malleability)));
								}
								if (overallQuality != 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_QUALITY, String.valueOf(overallQuality)));
								}
								if (potentialEnergy!= 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY, String.valueOf(potentialEnergy)));
								}
								if (shockResist!= 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE, String.valueOf(shockResist)));
								}
								if (unitToughness != 0) {
									rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_TOUGHNESS, String.valueOf(unitToughness)));
								}
								rc.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_VOLUME, String.valueOf(rc.getStackQuantity()) + "/" + String.valueOf(Constants.MAX_STACK_SIZE)));
								//----------------
								//server.addObjectIDToAllUsedID(rc.getID());
								server.addObjectToAllObjects(rc, false, false);
								player.addItemToInventory(rc);
								player.spawnItem(rc);
								client.insertPacket(PacketFactory.buildChatSystemMessage("100k Units of Resource [" + rc.getName() + "] Given."));
							}
							else
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage("Resource [" + sResourceName + "] not Found or not spawned on any planet."));
							}
						}
						else
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("Syntax: @getresource [resourcetype] : Where resourcetype is the generic iff name of the resource wanted i/e: 'Animal Bones' \r\nuse @listresourcetypes for a list of names."));
						}

					} else if(sRealMessage.toLowerCase().startsWith("@getcredits")) {
						String sRealBuffer[] = sRealMessage.split(" ");

						if(sRealBuffer.length == 2) {
							player.creditBankCredits(Integer.parseInt(sRealBuffer[1]));
						} else if(sRealBuffer.length == 1) {
							player.creditBankCredits(100000);
						}
					} else if(sRealMessage.toLowerCase().startsWith("@reloadradials")) {
						server.reloadServerRadials();
						client.insertPacket(PacketFactory.buildChatSystemMessage("Server Radials Reloaded"));
					} else if(sRealMessage.toLowerCase().startsWith("@reloaddeedt")) {
						DatabaseInterface.loadDeedTemplates();
						client.insertPacket(PacketFactory.buildChatSystemMessage("Reloaded."));
					} else if(sRealMessage.toLowerCase().startsWith("@morelots")) {
						player.setTotalLots(player.getTotalLots() + 10);
						client.insertPacket(PacketFactory.buildChatSystemMessage("Total Lots Now at: " + player.getTotalLots()));
					} else if(sRealMessage.toLowerCase().startsWith("@resetenc")) {
						int [] enc = player.getHamEncumberances();
						player.removeEncumberance(enc);
					} else if(sRealMessage.toLowerCase().startsWith("@setfaction")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						if(sRealBuffer[1].contains("neutral"))
						{
							player.setFactionID(Constants.FACTION_NEUTRAL);
						}
						else if(sRealBuffer[1].contains("rebel"))
						{
							player.setFactionID(Constants.FACTION_REBEL);
						}
						else if(sRealBuffer[1].contains("imperial"))
						{
							player.setFactionID(Constants.FACTION_IMPERIAL);
						}

					} else if(sRealMessage.toLowerCase().startsWith("@getallharvesterdeeds")) {
						int [] hdeeds = new int[22];
						hdeeds[0] = 79;
						hdeeds[1] = 80;
						hdeeds[2] = 81;
						hdeeds[3] = 82;
						hdeeds[4] = 83;                        
						hdeeds[5] = 85;
						hdeeds[6] = 86;
						hdeeds[7] = 92;
						hdeeds[8] = 93;
						hdeeds[9] = 94;
						hdeeds[10] = 95;
						hdeeds[11] = 96;
						hdeeds[12] = 97;
						hdeeds[13] = 98;
						hdeeds[14] = 99;
						hdeeds[15] = 100;
						hdeeds[16] = 101;
						hdeeds[17] = 102;
						hdeeds[18] = 103;
						hdeeds[19] = 104;
						hdeeds[20] = 105;
						hdeeds[21] = 106;
						for(int i = 0; i < hdeeds.length; i++)
						{
							DeedTemplate dt = DatabaseInterface.getDeedTemplateByID(hdeeds[i]);
							//Deed(int deedTemplateID,int itemTemplateID , ZoneServer server){
								Deed d = new Deed(dt.getTemplateid(),dt.getObject_template_id(), server);
								d.setEquipped(player.getInventory(), -1);
								d.setOwner(player);
								player.addItemToInventory(d);
								player.getServer().addObjectToAllObjects(d,false,false);
								d.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
								player.addDelayedSpawnObject(d, System.currentTimeMillis() + (10 * hdeeds[i]));
						}

					} else if(sRealMessage.toLowerCase().startsWith("@gettestpacket")) {

						//client.insertPacket(PacketFactory.buildObjectController_TEST(player));

					} else if(sRealMessage.toLowerCase().startsWith("@getdeed")) {
						SUIWindow W = new SUIWindow(player);
						W.setWindowType(Constants.SUI_SELECT_DEED);
						W.setOriginatingObject(player);

						Vector<DeedTemplate> vDT = DatabaseInterface.getAllDeedTemplates();
						//Vector<SOEObject> oL = new Vector<SOEObject>();
						String [] sList = new String [vDT.size()];

						for(int i = 0; i < vDT.size(); i++) {
							DeedTemplate t = vDT.get(i);
							sList[i] = "@" + server.getTemplateData(t.getObject_template_id()).getSTFFileName() + ":" + server.getTemplateData(t.getObject_template_id()).getSTFFileIdentifier();
							//System.out.println("String:" + sList[i] + " Pos:" + i + " TID:" + t.getTemplateid());
							//SOEObject oD = new SOEObject();
							//oD.setID(t.getTemplateid());
							//oL.add(oD);
						}

						String WindowTypeString = "handleSUI";
						String DataListTitle = "Select a Deed";
						String DataListPrompt = "Select A Deed Item to place in your inventory.";
						client.insertPacket(W.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
					} else if(sRealMessage.toLowerCase().startsWith("@getheight")) {
						float h = server.getHeightAtCoordinates(player.getX(), player.getY(),player.getPlanetID());
						client.insertPacket(PacketFactory.buildChatSystemMessage("Height Retrieved as: " + h));
					} else if(sRealMessage.toLowerCase().startsWith("@nobuildings")) {
						if(player.tempnobuildings == 0) {
							player.tempnobuildings = 1;
						} else {
							player.tempnobuildings = 0;
						}
					} else if(sRealMessage.toLowerCase().startsWith("@heightmap")) {
						String sRealBuffer[] = sRealMessage.split(" ");

						if(sRealBuffer.length == 2) {
							if(sRealBuffer[1].contentEquals("1")) {
								if(!player.isBHeightMapCollect()) {
									// player.tempx = (-7067f);
									// player.tempy = (3600f);
									// player.tempz = (-0.953695f);
									//client.insertPacket(PacketFactory.buildUpdateTransformMessage(player));
									System.out.println("@heightmap collection activated");
									player.myHeightMap = server.getHeightMap();
									player.tempx = 0;
									player.tempy = 0;
									player.bHeightMapActive = true;
									player.tempplanetid = (short)player.getPlanetID();
									player.playerWarp((float)(7680 - (0 * 60.472441)),(float)(7680 - (0 * 60.472441)),-1000f , player.getCellID(), player.tempplanetid);
									player.setLNextHeightMapUpdate(1000*10);
									player.setBHeightMapCollect(true);
								}
							} else if(sRealBuffer[1].contentEquals("0")) {
								player.setBHeightMapCollect(false);
								player.bHeightMapActive = false;
							}
							return;
						}
					} else if(sRealMessage.toLowerCase().startsWith("@spawnlair")) {
						String sRealBuffer[] = sRealMessage.split(" ");

						if(sRealBuffer.length == 1) {
							Lair newLair = new Lair(server);
							newLair.setLairRandomTemplate(player.getPlanetID(), true);
							newLair.setLairPosition(player.getX(), player.getY(), server.getHeightAtCoordinates(player.getX(), player.getY(), player.getPlanetID()),player.getCellID());
							newLair.spawn();
							client.insertPacket(PacketFactory.buildChatSystemMessage(newLair.getSTFFileIdentifier() + " Lair Spawned at: x: " + newLair.getX() + ", Y: " + newLair.getY()));

						} else if(sRealBuffer.length == 2) {
							int TemplateID = Integer.parseInt(sRealBuffer[1]);
							System.out.println("TemplateID Chosen: " + TemplateID);
							Lair newLair = new Lair(server);
							newLair.setLairTemplate(TemplateID,player.getPlanetID());
							newLair.setLairPosition(player.getX(), player.getY(), 0.953695f,player.getCellID());
							newLair.spawn();
							client.insertPacket(PacketFactory.buildChatSystemMessage(newLair.getSTFFileIdentifier() + " Lair Spawned at: x: " + newLair.getX() + ", Y: " + newLair.getY()));

						}
					} else if(sRealMessage.toLowerCase().startsWith("@bugreport")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						String Report = "";

						for(int i = 1; i < sRealBuffer.length; i++) {
							Report += sRealBuffer[i] + " ";
						}

						if(client.getServer().insertBugReport(client.getPlayer().getID(), client.getAccountID(), Report)) {
							client.insertPacket(PacketFactory.buildChatSystemMessage("Bug Report Complete Thanks!"));
						} else {
							client.insertPacket(PacketFactory.buildChatSystemMessage("Bug Report Fail"));
						}
					} else if(sRealMessage.toLowerCase().startsWith("@setnpcangle")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						float fNewAngle = Float.parseFloat(sRealBuffer[1]);

						if(!Float.isNaN(fNewAngle)) {
							NPC T = (NPC)client.getServer().getObjectFromAllObjects(player.getTargetID());
							T.setMovementAngle(T.degreesToRadians(fNewAngle));
							T.updateAngle(client);
						}
					} else if(sRealMessage.toLowerCase().startsWith("@clearbadges")) {
						int [] badges = player.getPlayData().getBadges();

						for(int i = 0; i < badges.length; i++) {
							badges[i] = 0;
						}

						player.getPlayData().setBadges(badges);
						client.insertPacket(PacketFactory.buildChatSystemMessage("Badges Cleared"));
					} else if(sRealMessage.toLowerCase().startsWith("@placeterminal")) {
						SUIWindow W = new SUIWindow(player);
						W.setWindowType(Constants.SUI_ADMIN_ADD_TERMINAL);
						W.setOriginatingObject(player);
						String[] sList = new String [63];
						sList[0] = "@terminal_name:beta_terminal_food";
						sList[1] = "@terminal_name:beta_terminal_medicine";
						sList[2] = "@terminal_name:beta_terminal_money";
						sList[3] = "@terminal_name:terminal_resource";
						sList[4] = "@terminal_name:terminal_warp";
						sList[5] = "@terminal_name:terminal_xp";
						sList[6] = "@terminal_name:donham_terminal";
						sList[7] = "@terminal_name:keypad";
						sList[8] = "@terminal_name:terminal_free_s1";
						sList[9] = "@terminal_name:terminal_gas_valve";
						sList[10] = "@terminal_name:terminal_power_switch";
						sList[11] = "@terminal_name:terminal_general_switch";
						sList[12] = "@terminal_name:terminal_power_switch";
						sList[13] = "@terminal_name:base_terminal";
						sList[14] = "@terminal_name:terminal_ballot_box";
						sList[15] = "@terminal_name:terminal_bank";
						sList[16] = "@terminal_name:terminal_bazaar";
						sList[17] = "@terminal_name:terminal_bestine_01";
						sList[18] = "@terminal_name:terminal_bestine_02";
						sList[19] = "@terminal_name:terminal_bestine_03";
						sList[20] = "@terminal_name:terminal_bounty_droid";
						sList[21] = "@terminal_name:terminal_character_builder";
						sList[22] = "@terminal_name:terminal_city";
						sList[23] = "@terminal_name:terminal_city_vote";
						sList[24] = "@terminal_name:terminal_cloning";
						sList[25] = "@terminal_name:terminal_geo_bunker";
						sList[26] = "@terminal_name:terminal_dark_enclave_challenge";
						sList[27] = "@terminal_name:terminal_dark_enclave_voting";
						sList[28] = "@terminal_name:terminal_elevator";
						sList[29] = "@terminal_name:terminal_geo_bunker";
						sList[30] = "@terminal_name:terminal_guild";
						sList[31] = "@terminal_name:terminal_hq";
						sList[32] = "@terminal_name:terminal_hq_imperial";
						sList[33] = "@terminal_name:terminal_hq_rebel";
						sList[34] = "@terminal_name:terminal_hq_turret_control";
						sList[35] = "@terminal_name:terminal_imagedesign";
						sList[36] = "@terminal_name:terminal_insurance";
						sList[37] = "@terminal_name:terminal_light_enclave_challenge";
						sList[38] = "@terminal_name:terminal_light_enclave_voting";
						sList[39] = "@terminal_name:terminal_mission";
						sList[40] = "@terminal_name:terminal_mission_artisan";
						sList[41] = "@terminal_name:terminal_mission_bounty";
						sList[42] = "@terminal_name:terminal_mission_entertainer";
						sList[43] = "@terminal_name:terminal_mission_imperial";
						sList[44] = "@terminal_name:terminal_mission_rebel";
						sList[45] = "@terminal_name:terminal_mission_scout";
						sList[46] = "@terminal_name:newbie_clothing_dispenser";
						sList[47] = "@terminal_name:newbie_food_dispenser";
						sList[48] = "@terminal_name:newbie_instrument_dispenser";
						sList[49] = "@terminal_name:newbie_medicine_dispenser";
						sList[50] = "@terminal_name:newbie_tool_dispenser";
						sList[51] = "@terminal_name:terminal_player_structure";
						sList[52] = "@terminal_name:terminal_pm_register";
						sList[53] = "@terminal_name:door_security_terminal";
						sList[54] = "@terminal_name:terminal_shipping";
						sList[55] = "@terminal_name:terminal_space";
						sList[56] = "@terminal_name:terminal_travel";
						sList[57] = "@terminal_name:terminal_water_pressure";
						sList[58] = "No Build Zone 4 Blocks";
						sList[59] = "No Build Zone 32 Blocks";
						sList[60] = "No Build Zone 64 Blocks";
						sList[61] = "No Build Zone 128 Blocks";
						sList[62] = "No Build Zone 768 Meters";

						String WindowTypeString = "handleSUI";
						String DataListTitle = "Select Terminal Type To Install";
						String DataListPrompt = "This is the terminal Placement Window. The terminal chosen will be placed at your current coordinates and in the direction you are facing.";
						client.insertPacket(W.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
					} else if(sRealMessage.toLowerCase().startsWith("@playsound")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						int s = Integer.parseInt(sRealBuffer[1]);
						System.out.println("Attempting to play sound : sound/" + Constants.SOUND_FILES[s]);
						client.insertPacket(PacketFactory.buildPlaySoundFileMessage(0,"sound/" + Constants.SOUND_FILES[s],1,(byte)0));
					} else if(sRealMessage.toLowerCase().startsWith("@playvoice")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						//int s = Integer.parseInt(sRealBuffer[1]);
						System.out.println("Attempting to play sound : voice/sound/" + sRealBuffer[1]);
						client.insertPacket(PacketFactory.buildPlaySoundFileMessage(0,"voice/sound/" + sRealBuffer[1],1,(byte)0));

					} else if(sRealMessage.toLowerCase().startsWith("@playsoundplayer")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						int s = Integer.parseInt(sRealBuffer[1]);
						System.out.println("Attempting to play sound : sound/" + Constants.SOUND_FILES[s]);
						client.insertPacket(PacketFactory.buildPlaySoundFileMessage(player.getID(),"sound/" + Constants.SOUND_FILES[s],1,(byte)0));
					} else if (sRealMessage.toLowerCase().startsWith("@setstate")) {
						try {
							String[] sRealBuffer = sRealMessage.split(" ");
							String state = sRealBuffer[1];
							String setStatus = sRealBuffer[2];
							long lTimeToApplyStateMS = Long.parseLong(sRealBuffer[3]) * 1000l;
							boolean bAddingState = setStatus.equals("true") || setStatus.equals("1");
							int iStateToSet = Integer.parseInt(state);
							if (bAddingState) {
								player.addState(iStateToSet, lTimeToApplyStateMS);
							} else {
								player.removeState(iStateToSet);
							}
						} catch (Exception e) {
							client.insertPacket(PacketFactory.buildChatSystemMessage(
									"Error parsing set state command.  Format is:  State_number adding_state state_period."
							));
							client.insertPacket(PacketFactory.buildChatSystemMessage(
									"For example, to apply the dizzy state for 10 seconds, type:  @setstate 14 true 10"
							));
						}
					} else if(sRealMessage.toLowerCase().startsWith("@spawntt")) {

						TutorialObject t = player.getTutorial();
						Terminal tt = t.getTutorialTravelTerminal();
						client.insertPacket(PacketFactory.buildChatSystemMessage("Spawning Travel Terminal " + tt.getID()));
						player.removeSpawnedObject(tt);
						player.spawnItem(tt);
						client.insertPacket(PacketFactory.buildChatSystemMessage("Spawned Travel Terminal"));

					} else if(sRealMessage.toLowerCase().startsWith("@placetrainer")) {
						SUIWindow W = new SUIWindow(player);
						W.setWindowType(Constants.SUI_ADMIN_ADD_SKILL_TRAINER);
						W.setOriginatingObject(player);
						String[] sList = new String [36];

						sList [0] = "@skl_n:social_entertainer";
						sList [1] = "@skl_n:outdoors_scout";
						sList [2] = "@skl_n:science_medic";
						sList [3] = "@skl_n:crafting_artisan";
						sList [4] = "@skl_n:combat_brawler";
						sList [5] = "@skl_n:combat_marksman";
						sList [6] = "@skl_n:combat_rifleman";
						sList [7] = "@skl_n:combat_pistol";
						sList [8] = "@skl_n:combat_carbine";
						sList [9] = "@skl_n:combat_unarmed";
						sList [10] = "@skl_n:combat_1hsword";
						sList [11] = "@skl_n:combat_2hsword";
						sList [12] = "@skl_n:combat_polearm";
						sList [13] = "@skl_n:social_dancer";
						sList [14] = "@skl_n:social_musician";
						sList [15] = "@skl_n:science_doctor";
						sList [16] = "@skl_n:outdoors_ranger";
						sList [17] = "@skl_n:outdoors_creaturehandler";
						sList [18] = "@skl_n:outdoors_bio_engineer";
						sList [19] = "@skl_n:crafting_armorsmith";
						sList [20] = "@skl_n:crafting_weaponsmith";
						sList [21] = "@skl_n:crafting_chef";
						sList [22] = "@skl_n:crafting_tailor";
						sList [23] = "@skl_n:crafting_architect";
						sList [24] = "@skl_n:crafting_droidengineer";
						sList [25] = "@skl_n:crafting_merchant";
						sList [26] = "@skl_n:combat_smuggler";
						sList [27] = "@skl_n:combat_bountyhunter";
						sList [28] = "@skl_n:combat_commando";
						sList [29] = "@skl_n:science_combatmedic";
						sList [30] = "@skl_n:social_imagedesigner";
						sList [31] = "@skl_n:outdoors_squadleader";
						sList [32] = "@skl_n:social_politician";
                                                sList [33] = "@skl_n:fs_trainer";
                                                sList [34] = "@skl_n:fs_trainer";
                                                sList [35] = "@skl_n:fs_trainer";

						String WindowTypeString = "handleSUI";
						String DataListTitle = "Select Skill Trainer To Install";
						String DataListPrompt = "This is the skill trainer Placement Window. The skill trainer chosen will be placed at your current coordinates and in the direction you are facing.";
						client.insertPacket(W.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
					} else if(sRealMessage.toLowerCase().startsWith("@getskills")) {
						BitSet SL = player.getSkillList();
						System.out.println("----- Printing Skills -----");

						for (int i = SL.nextSetBit(0); i >= 0; i = SL.nextSetBit(i+1)) {
							String skillName = client.getServer().getSkillNameFromIndex(i);
							System.out.println(skillName);
						}
						System.out.println("----- End Printing Skills -----");
					} else if(sRealMessage.toLowerCase().startsWith("@deleteskill")) {
					} else if(sRealMessage.toLowerCase().startsWith("@removepunchitem")) {
						String sRealBuffer[] = sRealMessage.split(" ");

						if(sRealBuffer.length == 2) {
							int iItemID = Integer.parseInt(sRealBuffer[1]);

							if(server.removePunchListItem(iItemID)) {
								client.insertPacket(PacketFactory.buildChatSystemMessage("Item " + iItemID + " Removed."));
							} else {
								client.insertPacket(PacketFactory.buildChatSystemMessage("Item " + iItemID + " Not Removed."));
							}
						} else {
							client.insertPacket(PacketFactory.buildChatSystemMessage("Not Enough Parameters use @help"));
						}
					} else if(sRealMessage.toLowerCase().startsWith("@addpunchitem")) {
						String sRealBuffer[] = sRealMessage.split(" ");
						if(sRealBuffer.length >= 3) {
							int iPriority = Integer.parseInt(sRealBuffer[1]);
							String sItem = "";

							for(int i  = 2; i < sRealBuffer.length; i++) {
								sItem += sRealBuffer[i];
								sItem += " ";
							}

							if(server.insertPunchListItem(iPriority, sItem)) {
								client.insertPacket(PacketFactory.buildChatSystemMessage("New Item Inserted."));
							} else {
								client.insertPacket(PacketFactory.buildChatSystemMessage("Insertion Failure."));
							}
						} else {
							client.insertPacket(PacketFactory.buildChatSystemMessage("Not Enough Parameters use @help"));
						}
					} else if(sRealMessage.toLowerCase().startsWith("@punchlist")) {
						SUIWindow W = new SUIWindow(player);
						W.setWindowType(Constants.SUI_TESTER_SHOW_PUNCHLIST);
						W.setOriginatingObject(player);

						String WindowTypeString = "handleSUI";
						String WindowTitle = "Shards of the Force Tester Punch List";
						String WindowPromptContent = "";
						Vector<String> vTPL = server.getTesterPunchList();

						if(vTPL.size() >= 1) {
							for(int i = 0; i < vTPL.size(); i++) {
								WindowPromptContent += vTPL.get(i);
								WindowPromptContent += "\r\n";
							}
						} else {
							WindowPromptContent += ("No Items to Test.\r\n");
						}

						client.insertPacket(W.SUIScriptMessageBox(client, WindowTypeString, WindowTitle,WindowPromptContent,false,false, 0, 0));
					} else if(sRealMessage.toLowerCase().startsWith("@runscript")) {
						
						//Permission check.
						if(bIsDeveloper) {
							
							try {
								//Get command arguments.
								String[] args = sRealMessage.split(" ");
								
								
								//Verify command arguments.
								if(args.length == 5 || args.length == 4) {

									//Parse command arguments.
									String scriptFileName = args[1];
									int runScriptType = Utilities.getScriptTypeFromString(args[2]);
									long runTargetObjectID = Long.parseLong(args[3]);
									Player runTargetPlayer = null;
									
									//Check for "self" and "me".
									if(args[4].equalsIgnoreCase("me") || args[4].equalsIgnoreCase("self")) {
										runTargetPlayer = player;
									} else {
										
										runTargetPlayer = server.getPlayer(args[4]);
									}
									
									//Check if the script type is valid.
									if(runScriptType != Constants.SCRIPT_TYPE_UNDEFINED) {
										
										//Check if the object exists.
										if(server.getAllObjects().containsKey(runTargetObjectID)) {
											
											//Check if the player exists.
											if(runTargetPlayer != null && runTargetPlayer.getClient() != null && runTargetPlayer.getOnlineStatus() && runTargetPlayer.getClient().getValidSession()) {
												
												//Run the script.
												server.getScriptManager().runScriptGenericMode(scriptFileName, runScriptType, runTargetObjectID, runTargetPlayer.getID());
											}
										}
									}
								} else {
									
									//Invalid command usage.
									server.broadcastSystemMessage(player, "Usage:  @runscript [Script File Name] [Script Type on Run] [Target Object ID on Run] <Target Player(First Name) on Run>");
								}
							} catch(NumberFormatException e) {
								
								//Invalid command usage.
								server.broadcastSystemMessage(player, "Usage:  @runscript [Script File Name] [Script Type on Run] [Target Object ID on Run] <Target Player(First Name) on Run>");
							} catch(ScriptException e) {
								
								//Send error message.
								server.broadcastSystemMessage(player, e.getMessage());
							}
						} else {

							//Let the player know he can't use this command.
							server.broadcastSystemMessage(player, "You do not have permission to use this command.");							
						}
					} else if(sRealMessage.toLowerCase().startsWith("@reloadscripts")) {
						
						//Instance variables.
						String sPlayerName = player.getFullName();
						
						//Check if the player is a developer.						
						if(player.isDev()) {
							
							//If they are, reload scripts.
							System.out.println(sPlayerName + " sent request to reload server scripts. Broadcasting lag notice.");
							
							//Let the player know that he's a very bad boy.
							server.broadcastSystemMessage(player, "Command recieved, attempting to re-load scripts.");
							
							//Alert the server that shit's about to hit the fan.
							server.broadcastServerMessage("Scripts are being re-loaded, you may experience latency. Thank you for your patience.");
							
							//Reload the scripts.
							server.getScriptManager().reloadScripts();
							
							//Let the players know the lag is finished.
							server.broadcastServerMessage("Scripts have been re-loaded, thank you for your patience.");
						} else {
							
							//Let the player know he can't use this command.
							server.broadcastSystemMessage(player, "You do not have permission to use this command.");
							
							//If they aren't, alert the server admin.
							System.out.println(sPlayerName + " sent request to reload server scripts. Player not a developer, ignoring command.");
						}
					} else if(sRealMessage.toLowerCase().startsWith("@listnearbyplayers")) {
						
						//Instance variables.
						int count = 1;
						SOEObject targetObject = null;
					
						//If the player has no target.
						if(player.getTargetID() == 0) {
				
							//Player has nothing targetted, get all objects around self.
							targetObject = (SOEObject) player;
						} else {
							
							//Player has a target, get all players around target.
							targetObject = server.getObjectFromAllObjects(player.getTargetID());			
						}

						//Check f the target exists.
						if(targetObject != null) {
							
							//Get a list of players.
							Enumeration<Player> list = server.getPlayersAroundObject(targetObject, false).elements();
							
							//Print out a list of the players.
							while(list.hasMoreElements()) {
								
								//Get the current player.
								Player currentPlayer = list.nextElement();
								
								//Print the current player.
								server.broadcastSystemMessage(player, String.format("Player #%s: %s", count, currentPlayer.getFullName()));
								
								//Increment count.
								count++;
							}	
						} else {
							
							//The target doesn't exist.
							server.broadcastSystemMessage(player, "Target doesn't exist in the server's list of objects.");
						}						
					} else {
						client.insertPacket(PacketFactory.buildChatSystemMessage("Unrecognized Command use @help"));
					}
				} catch(Exception eee) {
					System.out.println("Uncaught exception in ZoneClientThread.handleSpatialChat() Admin Command:" + eee.toString());
					eee.printStackTrace();
				}
			}
			
			{
				//tester commands go here since testers are not devs nor csr's
				// However ,we don't want an "else" here, since that will preclude developers from accessing these commmands.
				if(sRealMessage.toLowerCase().startsWith("@help")) {
					SUIWindow W = new SUIWindow(player);
					W.setWindowType(Constants.SUI_TESTER_COMMAND_LIST);
					W.setOriginatingObject(player);

					String WindowTypeString = "handleSUI";
					String WindowTitle = "Shards of the Force Tester Commands";
					String WindowPromptContent = " @punchlist - Brings up a list of items for players to test.\r\n";
					WindowPromptContent += (" @bugreport - Inserts a new Bug Report into the bug database - Syntax: @bugreport [report text].\r\n");
					WindowPromptContent += (" @ver - Display the current server version.\r\n");
					client.insertPacket(W.SUIScriptMessageBox(client, WindowTypeString, WindowTitle,WindowPromptContent,false,false, 0, 0));
				} else if (sRealMessage.toLowerCase().startsWith("@ver")) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(Constants.getCurrentSoftwareVersion()));
				} else if(sRealMessage.toLowerCase().startsWith("@bugreport")){
					String sRealBuffer[] = sRealMessage.split(" ");
					String Report = "";

					for(int i = 1; i < sRealBuffer.length; i++) {
						Report += sRealBuffer[i] + " ";
					}

					if(client.getServer().insertBugReport(client.getPlayer().getID(), client.getAccountID(), Report)) {
						client.insertPacket(PacketFactory.buildChatSystemMessage("Bug Report Complete Thanks!"));
					} else {
						client.insertPacket(PacketFactory.buildChatSystemMessage("Bug Report Fail"));
					}
				} else if(sRealMessage.toLowerCase().startsWith("@punchlist")) {
					SUIWindow W = new SUIWindow(player);
					W.setWindowType(Constants.SUI_TESTER_SHOW_PUNCHLIST);
					W.setOriginatingObject(player);

					String WindowTypeString = "handleSUI";
					String WindowTitle = "Shards of the Force Tester Punch List";
					String WindowPromptContent = "";
					Vector<String> vTPL = server.getTesterPunchList();

					if(vTPL.size() >= 1) {
						for(int i = 0; i < vTPL.size(); i++) {
							WindowPromptContent += vTPL.get(i);
							WindowPromptContent += "\r\n";
						}
					} else {
						WindowPromptContent += ("No Items to Test.\r\n");
					}
					client.insertPacket(W.SUIScriptMessageBox(client, WindowTypeString, WindowTitle,WindowPromptContent,false,false, 0, 0));
				}
			}
		}

		if(bSendSpatial) {
			buff = null;
			//System.out.println("Sending spatial message '" + sRealMessage + "' from " + player.getFirstName());
			
			player.getClient().insertPacket(PacketFactory.buildObjectControllerMessageSpatial(player, target, player, sRealMessage, language, howlModifier ));
			float messageRange = 0;

			switch (howlModifier) {
				case 0: {
					messageRange = 80.0f;
					break;
				}
				case Constants.CHAT_MODIFIER_HOWL: {
					messageRange = 120.0f;
					break;
				}
				case Constants.CHAT_MODIFIER_SHOUT: {
					messageRange = 100.0f;
					break;
				}
				case Constants.CHAT_MODIFIER_SING: {
					messageRange = 60.0f;
					break;
				}
				case Constants.CHAT_MODIFIER_WHISPER: {
					messageRange = 10.0f;
					break;
				}
				default: {
					messageRange = 80.0f;
					break;
				}
			}
			//System.out.println("Howl modifier = " + howlModifier);
			Vector<Player> vObjectsInRange = server.getPlayersAroundObject(player, false, messageRange); // This is everyone who can see the original message that the Player spoke.
			
			Player recipient = null;
			for (int i = 0; i < vObjectsInRange.size(); i++) {
				recipient = vObjectsInRange.elementAt(i);
				//System.out.println("Sending spatial message '" + sRealMessage + "' from " + player.getFirstName() + " to " + target.getFirstName() + ".  Person hearing: " + recipient.getFirstName());
				// Note on this next line:
				// "player" is the person who sent this spatial message.
				// "target" is the person at whom this spatial message is directed.  (The object that was targeted by "player" when player "spoke"
				// "recipient" is the person who is going to receive the packet we build in the next line.
				recipient.getClient().insertPacket(PacketFactory.buildObjectControllerMessageSpatial(player, target, recipient, sRealMessage, language, howlModifier ));
			}
		}
	}

	private void handleFlourishRequest( long targetID, String[] sParameters) throws IOException {
		//Player p = server.getPlayer(targetID);
		int flourishID = 0;
		try{
			flourishID = Integer.parseInt(sParameters[0]);
		}catch(Exception e){
			if(e instanceof java.lang.NumberFormatException )
			{
				//who cares but the flourish will remain at 0 so we tell the player invalid flourish.
			}
		}

		if(!player.isDancing() && !player.isPlayingMusic())
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("performance","flourish_not_performing"));
			return;
		}
		if(player.getCurrentHam()[3] <= 100)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("performance","flourish_too_tired"));
			return;
		}
		if(flourishID <= 0 || flourishID >= 9)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"flourish_not_valid",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
			return;
		}

		if(player.addFlourishBonus(3))
		{
			player.setFlourishID(flourishID);
			player.setPerformedAnimation("skill_action_" + flourishID);
			Vector<Player> vObjectsInRange = server.getPlayersAroundObject(player, true); // TODO:  False, or true?
			for (int i = 0; i < vObjectsInRange.size(); i++) {
				Player recipient = vObjectsInRange.elementAt(i);
				if(player.isDancing())
				{
					recipient.getClient().insertPacket(PacketFactory.buildCharacterAnimation(player,recipient,"skill_action_" + flourishID));
				}
				else if(player.isPlayingMusic())
				{
					recipient.getClient().insertPacket(PacketFactory.buildFlourishResponse(player,recipient));
					player.setFlourishID(flourishID);
				}
			}
		}
		player.setBClearPerformedAnimation(true);
	}

	private void handleEntertainerEffect(CommandQueueItem action, int iEffectCRC, long targetID, String[] sParameters) throws IOException{

		if(player.getCurrentHam()[3] <= 100)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("performance","effect_too_tired"));
			return;
		}

		boolean hasSkill = false;
		boolean isAnimating = false;
		String sClientEffect = "clienteffect/";
		String sEffectSuffix = ".cef";
		String sEffectMessage = "";
		long effectTarget = 0;
		switch(iEffectCRC)
		{
		/**
		 * These are for Dancer and Musician        Dancer Skill    Musician Skill
		 * entertainer_color_lights_level_1.cef     264             283
               entertainer_color_lights_level_2.cef     265             284
               entertainer_color_lights_level_3.cef     266             285
               entertainer_dazzle_level_1.cef           264             283
               entertainer_dazzle_level_2.cef           265             284
               entertainer_dazzle_level_3.cef           266             285
               entertainer_spot_light_level_1.cef       264             283
               entertainer_spot_light_level_2.cef       265             284
               entertainer_spot_light_level_3.cef       266             285
		 */
		case Constants.spotlight:
		{
			sClientEffect += "entertainer_spot_light_level_";
			sEffectMessage = "effect_perform_spot_light";
			if(player.isDancing())
			{
				isAnimating = true;
				if(player.hasSkill(266))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(265))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(264))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			else if(player.isPlayingMusic())
			{
				isAnimating = true;
				if(player.hasSkill(285))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(284))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(283))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			break;
		}
		case Constants.colorlights:
		{
			sClientEffect += "entertainer_color_lights_level_";
			sEffectMessage = "effect_perform_color_lights";
			if(player.isDancing())
			{
				isAnimating = true;
				if(player.hasSkill(266))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(265))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(264))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			else if(player.isPlayingMusic())
			{
				isAnimating = true;
				if(player.hasSkill(285))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(284))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(283))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			break;
		}
		case Constants.dazzle:
		{
			sClientEffect += "entertainer_dazzle_level_";
			sEffectMessage = "effect_perform_dazzle";
			if(player.isDancing())
			{
				isAnimating = true;
				if(player.hasSkill(266))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(265))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(264))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			else if(player.isPlayingMusic())
			{
				isAnimating = true;
				if(player.hasSkill(285))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(284))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(283))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			break;
		}
		/**
		 * These are for Musician only              Dancer Skill    Musician Skill
		 * entertainer_fire_jets_level_1.cef                        284
               entertainer_fire_jets_level_2.cef                        285
               entertainer_fire_jets_level_3.cef                        286
		 *
		 * entertainer_ventriloquism_level_1.cef                    285
               entertainer_ventriloquism_level_2.cef                    286
               entertainer_ventriloquism_level_3.cef                    282
		 */
		case Constants.firejet:
		{
			sClientEffect += "entertainer_fire_jets_level_";
			sEffectMessage = "effect_perform_fire_jets";
			if(player.isPlayingMusic())
			{
				isAnimating = true;
				if(player.hasSkill(286))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(285))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(284))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			break;
		}
		case Constants.ventriloquism:
		{
			effectTarget = targetID;
			sClientEffect += "entertainer_ventriloquism_level_";
			sEffectMessage = "effect_perform_ventriloquism";
			if(player.isPlayingMusic())
			{
				isAnimating = true;
				if(player.hasSkill(282))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(286))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(285))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			break;
		}
		/**
		 * These are for Dancer Only                Dancer Skill    Musician Skill
		 * entertainer_distract_level_1.cef         265
               entertainer_distract_level_2.cef         266
               entertainer_distract_level_3.cef         267
		 *
		 * entertainer_smoke_bomb_level_1.cef       266
               entertainer_smoke_bomb_level_2.cef       267
               entertainer_smoke_bomb_level_3.cef       263
		 */
		case Constants.distract:
		{
			effectTarget = targetID;
			sClientEffect += "entertainer_distract_level_";
			if(player.isDancing())
			{
				isAnimating = true;
				if(player.hasSkill(267))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(266))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(265))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			break;
		}
		case Constants.smokebomb:
		{
			sClientEffect += "entertainer_smoke_bomb_level_";
			if(player.isDancing())
			{
				isAnimating = true;
				if(player.hasSkill(263))
				{
					sClientEffect += "3";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(267))
				{
					sClientEffect += "2";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
				else if(player.hasSkill(266))
				{
					sClientEffect += "1";
					sClientEffect += sEffectSuffix;
					hasSkill = true;
				}
			}
			break;
		}
		default:
		{
			DataLog.logEntry("Unhandled Entertainer Effect CRC: " + iEffectCRC, "ZoneClientThread", iEffectCRC, ZoneServer.ZoneRunOptions.bLogToConsole, true);
		}
		}

		/*
         hasSkill = true;
        sClientEffect+= "3.cef";
		 */

		if(hasSkill && isAnimating)
		{
			System.out.println("Playing a Client Effect " + sClientEffect);
			if(iEffectCRC != Constants.ventriloquism || iEffectCRC != Constants.distract)
			{
				player.updateCurrentHam(Constants.HAM_INDEX_ACTION, (-100));
				Vector<Player> vPL = server.getPlayersAroundObject(player, true);
				for(int i = 0; i < vPL.size(); i++)
				{
					Player p = vPL.get(i);
					p.getClient().insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(player, sClientEffect));
				}
			}
			else if(iEffectCRC == Constants.ventriloquism || iEffectCRC == Constants.distract)
			{
				if(effectTarget != 0)
				{
					player.updateCurrentHam(Constants.HAM_INDEX_ACTION, (-100));
					SOEObject oEffectTarget = server.getObjectFromAllObjects(effectTarget);
					if(oEffectTarget!=null && (oEffectTarget instanceof Player))
					{
						Player pt = (Player)oEffectTarget;
						Vector<Player> vPL = server.getPlayersAroundObject(player, true);
						for(int i = 0; i < vPL.size(); i++)
						{
							Player p = vPL.get(i);
							p.getClient().insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(pt, sClientEffect));
						}
					}
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"performance",
							"effect_need_target",
							player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
							0, null, null, null,
							0, null, null, null,
							0, 0.0f, false
					));
				}
			}
		}
		else if(!isAnimating)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"flourish_not_performing",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
		}
		else
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"effect_lack_skill_self",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
		}
	}

	private void handleListen(CommandQueueItem action, long targetID, String[] sParameters) throws IOException{

	}

	private void handleStopListen(CommandQueueItem action, long targetID, String[] sParameters) throws IOException{

	}

	private void handleStopWatch( long targetID, String[] sParameters) throws IOException{
		SOEObject o = server.getObjectFromAllObjects(targetID);
		if(o instanceof Player)
		{

			Player dancer = (Player)o;
			if(player.getPlayerBeingWatched().equals(dancer))
			{
				dancer.removePlayerWatching(player);
				player.setPlayerBeingWatched(null);
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"performance",
						"dance_watch_stop_self",
						player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
						dancer.getID(), dancer.getSTFFileName(), dancer.getSTFFileIdentifier(), dancer.getFullName(),
						0, null, null, null,
						0, 0.0f, false
				));
				String sNewMood = "none";
				for (int i = 0; i < Constants.vMoodStrings.length; i++) {
					if (sNewMood.equals(Constants.vMoodStrings[i])) {
						//parameters[0] = Integer.toString(i);
						handleSetMoodInternal(player.getID(), (byte)i);
						return;
					}
				}
			}
			else
			{

			}
		}
	}


	private void handleWatch(CommandQueueItem action,  long targetID, String[] sParameters) throws IOException{
		/**
		 * 05 00
		 * 46 5E CE 80
		 * 23 00 00 00
		 * 16 01 00 00
		 * 9B 12 54 53 00 00 00 00
		 * 00 00 00 00
		 * 00 00 00 00
		 * 43 CA 93 EC
		 * 15 5D 4A 4B 00 00 00 00 <--Target ID
		 * 00 00 00 00  */

		SOEObject o = server.getObjectFromAllObjects(targetID);
		if(o instanceof Player)
		{
			Player dancer = (Player)o;
			if(dancer.isDancing())
			{
				if (player.setStance(action, Constants.STANCE_ANIMATING_SKILL, false)) {
					player.setPlayerBeingWatched(dancer);
					dancer.addPlayerWatching(player);
	
					player.setPerformanceString("entertained");
					/*
	                 player.setMoodString("amused");
	                 String sNewMood = "amused";
	                for (int i = 0; i < Constants.vMoodStrings.length; i++) {
	                    if (sNewMood.equals(Constants.vMoodStrings[i])) {
	                        //parameters[0] = Integer.toString(i);
	                        handleSetMoodInternal(player.getID(), (byte)i);
	                        return;
	                    }
	                }
	                 client.insertPacket(PacketFactory.buildPlayerAnimation(player, "clap"));
					 * */
					/**
					 * @todo send the mood delta and set the player to listen or watch via delta.
					 */
				}
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"performance",
						"dance_watch_not_dancing",
						dancer.getID(), dancer.getSTFFileName(), dancer.getSTFFileIdentifier(), dancer.getFullName(),
						0, null, null, null,
						0, null, null, null,
						0, 0.0f, false
				));
			}
		}
		else if(o instanceof NPC)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"dance_watch_npc",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
		}
	}

	private void handleSocialInternal( long targetID, String[] sParamaters) throws IOException{
		long targetIdInParamaters = Long.parseLong(sParamaters[0]);
		int emoteID = Integer.parseInt(sParamaters[1]);
		Player target = null;
		if (targetIdInParamaters > 0) {
			target = server.getPlayer(targetIdInParamaters);
		}
		player.setPerformedEmoteID(emoteID);
		//boolean bTyped = ((Integer.parseInt(sParameters[3])) > 0);
		//if (bTyped) {
		client.insertPacket(PacketFactory.buildObjectControllerMessage_PlayerEmote(player, target, player));
		Vector<Player> vObjectsInRange = server.getPlayersAroundObject(player, false);
		for (int i = 0; i < vObjectsInRange.size(); i++) {
			Player recipient = vObjectsInRange.elementAt(i);
			recipient.getClient().insertPacket(PacketFactory.buildObjectControllerMessage_PlayerEmote(player, target, recipient));
		}
		//}
	}

	private void handleServerDestroyObject( long targetID, String[] sParamaters) throws IOException {
		System.out.println("HandleDestroyObject.");
		SOEObject o = server.getObjectFromAllObjects(targetID);
		
		//o.destroySelf();
		boolean bInventoryItem = false;
		boolean bDestroyed = false;
		boolean bEquippedItem = false;
		if (o instanceof TangibleItem) {
			TangibleItem t = (TangibleItem)o;
			bEquippedItem = t.getEquippedStatus() == 4;
			bInventoryItem = player.getInventoryItems().contains(t);
			if (bInventoryItem) { // If you're equipped on the player
				t.setEquipped(player.getInventory(), -1);
			}
			player.getInventory().removeLinkedObject(t);
			player.getInventoryItems().remove(t);
			bDestroyed = true;
		} else if (o instanceof Weapon) {
			Weapon w = (Weapon)o;
			bEquippedItem = w.getEquippedStatus() == 4;
			bInventoryItem = player.getInventoryItems().contains(w);
			if (bEquippedItem) { // If you're equipped on the player
				w.setEquipped(player.getInventory(), -1);
			}
			player.getInventory().removeLinkedObject(w); 
			player.getInventoryItems().remove(w);
			bDestroyed = true;
		} else if (o instanceof Waypoint) {
			Waypoint w = (Waypoint)o;
			player.getWaypoints().remove(w);
			//bDestroyed = true;
			client.insertPacket(PacketFactory.buildWaypointDelta(player, w, Constants.DELTA_DELETING_ITEM));  // This might make client explode???
		} else if (o instanceof Player) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("You aren't allowed to destroy other players."));
		}else if(o instanceof IntangibleObject) {
			IntangibleObject itno = (IntangibleObject)o;
			TangibleItem container = (TangibleItem)itno.getContainer();
			container.removeIntangibleObject(itno);
			client.getPlayer().despawnItem(itno);
			bDestroyed = true;
		} else {
			System.out.println("serverdestroyobject Unhandled Class to Destroy: " + o.getClass());
		}
		if (bDestroyed) {
			player.despawnItem(o);
			if (!bInventoryItem) {
				client.getServer().removeObjectFromAllObjects(o, !bEquippedItem);
			}
		}
		//} else {
		//	client.insertPacket(PacketFactory.buildChatSystemMessage("Error destroying object -- it has not been destroyed."));
		//}
	}

	private void handlePlaceStructureMessage( long targetID, String[] sParameters) throws IOException {

		for(int i = 0; i < sParameters.length; i ++ )
		{
			System.out.println("Parm " + i + " : " + sParameters[i]);

		}
		if(player.getFreeLots() == 0)
		{
			player.getClient().insertPacket(PacketFactory.buildChatSystemMessage("You do not have any lots left"));
			return;
		}
		/**
		 *  Place Structure Param 0 : 3917579850
                    Place Structure Param 1 : 5348.08
                    Place Structure Param 2 : -3876.08
                    Place Structure Param 3 : 3
		 */
		if (targetID != 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("Note:  Dropping a building on a target is NOT a valid way to kill it."));
		}
		/*long ownerID =*/ Long.parseLong(sParameters[0]);
		float buildingX = Float.parseFloat(sParameters[1]);
		float buildingY = Float.parseFloat(sParameters[2]);
		float buildingZ = client.getServer().getHeightAtCoordinates(buildingX, buildingY, player.getPlanetID());
		if((buildingZ - player.getZ()) >= 2 || (buildingZ - player.getZ()) <= 2)
		{
			int z = (int)player.getZ();
			float fz = z;
			fz += .1f;
			buildingZ = fz;
		}
		else
		{
			buildingZ = ((buildingZ + player.getZ()) / 2) - 1;
		}

		int buildingOrientation = Integer.parseInt(sParameters[3]);
		float oW = 0;
		float oS = 0;
		if (buildingOrientation == 0) {//north
			oW = 1;
		oS = 0;
		}else if (buildingOrientation == 1) { //east
			oW = 0.707107f;
		oS = 0.707107f;
		}else if (buildingOrientation == 2) {//south
			oW = 0;
			oS = 1;
		}else if (buildingOrientation == 3) {//West
			oW = -0.707107f;
			oS = 0.707107f;
		}

		long deedID = player.getLCurrentDeedInPlacementMode();
		
		Deed d = (Deed)client.getServer().getObjectFromAllObjects(deedID);
		int deedTemplateID = d.getDeedTemplateID();
		client.insertPacket(PacketFactory.buildChatSystemMessage("Received request to build building deed id:" + deedID +  ", templateID: " + deedTemplateID + " at X " + buildingX + ", Y " + buildingY + ", Z " + buildingZ));
		System.out.println("Received request to build building deedid:" + deedID +  ", templateID: " + deedTemplateID + " at X " + buildingX + ", Y " + buildingY + ", Z " + buildingZ);
		if(d!=null)
		{
			Structure s = null;
		
			if (deedTemplateID == 79 || deedTemplateID == 80 || deedTemplateID == 81 || deedTemplateID == 82) {
				System.out.println("Structure is a factory!");
				s = new Factory(d.getDeedTemplateID(), d.getID(), d, buildingX, buildingY, buildingZ, player.getPlanetID(), oW, oS, buildingOrientation, player.getFirstName(), player.getID(), server);
				Factory f = (Factory)s;
				f.setFactoryType((byte)(deedTemplateID - 79));
				f.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_OWNER, player.getFullName()));
				f.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_MANUFACTURE_SCHEMATIC_VOLUME, 0));
				f.addToHopperAccess(player);
				f.addToAdminList(player.getID());
			} else if ((deedTemplateID >= 83 && deedTemplateID <= 86) || (deedTemplateID >= 92 && deedTemplateID <= 106)) {
				System.out.println("Structure is a Harvester.");
				Harvester harvester = new Harvester(d.getDeedTemplateID(), d.getID(), d, buildingX, buildingY, buildingZ, player.getPlanetID(), oW, oS, buildingOrientation, player.getFirstName(), player.getID(), server);
				s = harvester;
				
			} else {
				s = new Structure(d.getDeedTemplateID(), d.getID(), d,buildingX,buildingY,buildingZ,player.getPlanetID(),oW,oS,buildingOrientation,player.getFirstName(),player.getID(),server);
			}
			if(s!=null)
			{
				d.setIsPlaced(true);
				client.getServer().addObjectToAllObjects(s.getConstructionMarkerObject(),true,false);
				s.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
				player.addDelayedSpawnObject(s, System.currentTimeMillis() + (1000*10));
				Structure marker = s.getConstructionMarkerObject();
				marker.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_DESPAWN);
				player.addDelayedSpawnObject(marker,(System.currentTimeMillis() + (1000*10)));
				//d.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_DESPAWN);
				//player.addDelayedSpawnObject(d, System.currentTimeMillis() + (1000*15));
				player.despawnItem(d);
				player.addPlayerStructure(s);
				server.addPlayerStructureToAllStructures(s);
				server.getGUI().getDB().updatePlayer(player, false,false);

			}
		}
		else
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("Error Cannot Place Structure Contact a CSR Code: 1"));
		}
	}

	private void handlePurchaseTicket( long targetID, String[] sParameters) throws IOException {

		// Departure planet = 0;
		// Departure city = 1;
		// Arrival planet = 2;
		// Arrival city = 3;
		// Round trip Bool = 4;

		/*
		 *
		 * Purchase Ticket Parameters
                    Parameter 0 - naboo
                    Parameter 1 - Dee'ja_Peak_Shuttleport
                    Parameter 2 - corellia
                    Parameter 3 - Kor_Vella_Starport
                    Parameter 4 - 0
		 *
		 * */
		//first to check is if this ticket is purchaseable from this terminal.
		// int TerminalID = client.getPlayer().getLastUsedTravelTerminal().getTerminalID();

		//Vector<TravelDestination> vTd = client.getServer().getAllTravelDestinations();

		for(int i = 0; i < sParameters.length; i++)
		{
			System.out.println("Purchase ticket Parameter " + i + " " + sParameters[i]);
		}

		boolean bFound = false;
		int iDeparturePlanetID = -1;
		int iArrivalPlanetID = -1;
		for (int i = 0; i < Constants.TravelPlanetNames.length && !bFound; i++) {
			if (sParameters[0].equals(Constants.TravelPlanetNames[i])) {
				iDeparturePlanetID = i;
				bFound = true;
			}
		}
		bFound = false;
		for (int i = 0; i < Constants.TravelPlanetNames.length && !bFound; i++) {
			if (sParameters[2].equals(Constants.TravelPlanetNames[i])) {
				iArrivalPlanetID = i;
				bFound = true;
			}
		}
		
		TravelDestination DeparturePort = client.getServer().getTravelDestinationFromName(sParameters[1], iDeparturePlanetID);
		TravelDestination ArrivalPort = client.getServer().getTravelDestinationFromName(sParameters[3], iArrivalPlanetID);

		System.out.println("Departure : " + DeparturePort.getDestinationName() + " " + DeparturePort.getDestinationPlanet());
		System.out.println("Arrival : " + ArrivalPort.getDestinationName() + " " + ArrivalPort.getDestinationPlanet());

		if(DeparturePort == null)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("Error While Searching Departure Location."));
			client.insertPacket(PacketFactory.buildChatSystemMessage("Purchase Cancelled."));
			DataLogObject dlo = new DataLogObject();
			dlo.setLogSource("ZoneClientThread().handlePurchaseTicket()");
			dlo.setLogSeverity(Constants.LOG_SEVERITY_MAJOR);
			dlo.setLogentryText("Error Occured while Searching for an Departure port While Purchasing a Ticket. Player Name: " + client.getPlayer().getFullName());
			DataLog.qServerLog.add(dlo);
			return;
		}
		if(ArrivalPort == null)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("Error While Searching Arrival Location."));
			client.insertPacket(PacketFactory.buildChatSystemMessage("Purchase Cancelled."));
			DataLogObject dlo = new DataLogObject();
			dlo.setLogSource("ZoneClientThread().handlePurchaseTicket()");
			dlo.setLogSeverity(Constants.LOG_SEVERITY_MAJOR);
			dlo.setLogentryText("Error Occured while Searching for an Arrival port While Purchasing a Ticket. Player Name: " + client.getPlayer().getFullName());
			DataLog.qServerLog.add(dlo);
			return;
		}
		if(!DeparturePort.getIsStarPort() && ArrivalPort.getIsStarPort() && DeparturePort.getDestinationPlanet() != ArrivalPort.getDestinationPlanet())
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("You Cannot Purchase that Ticket from this Location."));
			return;
		}

		boolean bRoundTrip = (Integer.parseInt(sParameters[4]) > 0);
		System.out.println("Getting Ticket cost for trip from " + DeparturePort.getDestinationName() + " To " + ArrivalPort.getDestinationName());
		int TicketCost = client.getServer().ServerTicketPriceList.getTicketPrice(DeparturePort.getDestinationPlanet(), ArrivalPort.getDestinationPlanet());
		System.out.println("Ticket Cost: " + TicketCost + " Cash On Hand: " + client.getPlayer().getCashOnHand() );
		if(bRoundTrip)
		{
			TicketCost = TicketCost * 2;
		}

		boolean DeliverTicket = false;
		if(client.getPlayer().debitCredits(TicketCost))
		{
			//ticket purchase packet goes here
			//client.insertPacket(PacketFactory.buildChatSystemMessage("Ticket Purchase Complete."));
			DeliverTicket = true;
		}
		else
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("You lack the funds to purchase that Ticket."));
			return;
		}

		//Maach Needs to Spawn and insert tickets to the Player
		boolean PurchaseComplete = false;
		if(DeliverTicket)
		{
			ItemTemplate template = server.getTemplateData(14132);
			TravelTicket t = new TravelTicket();
			t.setID(server.getNextObjectID());
			t.setTemplateID(14132);
			t.setOwner(player);
			t.setArrivalInformation(ArrivalPort);
			t.setDepartureInformation(DeparturePort);
			if (bRoundTrip){
				t.setTravelTicketPrice(TicketCost / 2);
			}else{
				t.setTravelTicketPrice(TicketCost);
			}

			t.setIFFFileName(template.getIFFFileName());
			//t.setSTFFileName(template.getSTFFileName());
			//t.setSTFFileIdentifier(template.getSTFFileIdentifier());
			//t.setSTFDetailName(template.getSTFDetailName());
			//t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
			//t.setSTFLookAtName(template.getSTFLookAtName());
			//t.setSTFLookAtIdentifier(template.getSTFLookAtIdentifier());
			t.setName("Travel Ticket", false);
			t.setCustomizationData(null);
			t.setConditionDamage(0, false);
			t.setMaxCondition(1, false);
			// TODO:  Item Attribute List to be updated with Departure / Arrival information.
			//Attribute a = new Attribute(string name, value);
			//attributes now sent from getAttributes in SOEObject.
			/*
                t.addAttribute(new Attribute("crafter","Galactic Travel Authority"));
                t.addAttribute(new Attribute("volume","1"));
                t.addAttribute(new Attribute("travel_departure_planet",Constants.PlanetNames[DeparturePort.getDestinationPlanet()]));
                t.addAttribute(new Attribute("travel_departure_point",DeparturePort.getDestinationName()));
                t.addAttribute(new Attribute("travel_arrival_planet",Constants.PlanetNames[ArrivalPort.getDestinationPlanet()]));
                t.addAttribute(new Attribute("travel_arrival_point",ArrivalPort.getDestinationName()));
			 */

			t.setEquipped(player.getInventory(), -1);
			player.addItemToInventory(t);
			client.getServer().addObjectToAllObjects(t, true,false);
			player.spawnItem(t);


			if (bRoundTrip) {
				t = new TravelTicket();
				t.setID(server.getNextObjectID());
				t.setOwner(player);
				t.setArrivalInformation(DeparturePort);
				t.setDepartureInformation(ArrivalPort);
				t.setTravelTicketPrice(TicketCost / 2);
				t.setIFFFileName(template.getIFFFileName());
				//t.setSTFFileName(template.getSTFFileName());
				//t.setSTFFileIdentifier(template.getSTFFileIdentifier());
				//t.setSTFDetailName(template.getSTFDetailName());
				//t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
				//t.setSTFLookAtName(template.getSTFLookAtName());
				//t.setSTFLookAtIdentifier(template.getSTFLookAtIdentifier());
				t.setConditionDamage(0, false);
				t.setMaxCondition(1, false);
				t.setName("Travel Ticket", false);
				t.addAttribute(new Attribute("crafter","Galactic Travel Authority"));
				t.addAttribute(new Attribute("volume","1"));
				t.addAttribute(new Attribute("travel_departure_planet",Constants.PlanetNames[ArrivalPort.getDestinationPlanet()]));
				t.addAttribute(new Attribute("travel_departure_point",ArrivalPort.getDestinationName()));
				t.addAttribute(new Attribute("travel_arrival_planet",Constants.PlanetNames[DeparturePort.getDestinationPlanet()]));
				t.addAttribute(new Attribute("travel_arrival_point",DeparturePort.getDestinationName()));
				t.setCustomizationData(null);
				t.setTemplateID(14132);
				t.setEquipped(player.getInventory(), -1);
				// TODO:  Item Attribute List to be updated with Departure / Arrival information.
				player.addItemToInventory(t);
				client.getServer().addObjectToAllObjects(t, true,false);
				player.spawnItem(t);

			}
			PurchaseComplete = true;
		}

		if(PurchaseComplete)
		{
			//send fly text
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"base_player",
					"prose_pay_acct_success",
					0,
					null,
					null,
					null,
					0,
					null,
					null,
					null,
					0,
					"money/acct_n",
					"travelsystem",
					null,
					TicketCost,
					0f,
					true
			));




			//client.insertPacket(PacketFactory.buildFlytextMessageTicketPurchase(player, TicketCost));

			client.insertPacket(PacketFactory.buildSUITicketPurchase(player));
			return;
		}

		StringBuffer s = new StringBuffer().append("Purchased travel ticket.  Departure point: ").append(sParameters[0]).append(" ").append(sParameters[1]).append(".  Arrival point: ").append(sParameters[2]).append(", ").append(sParameters[3]).append(".  Round trip? ").append(bRoundTrip);
		client.insertPacket(PacketFactory.buildChatSystemMessage(s.toString()));
	}

	private void handleMountObject( long targetID, String[] sParamaters) throws IOException {
		System.out.println("Trying to mount vehicle with object ID " + targetID );
		SOEObject o = server.getObjectFromAllObjects(targetID);
		if(o instanceof Vehicle)
		{
			Vehicle vehicleItem = (Vehicle)o;
			vehicleItem.useItem(client, (byte)205);
			return;
		}
		System.out.println("Unhandled Mount!!!:" + targetID );
	}

	private void handleDismountObject( long targetID, String[] sParamaters) throws IOException {
		SOEObject o = server.getObjectFromAllObjects(targetID);
		if(o instanceof Vehicle)
		{
			Vehicle vehicleItem = (Vehicle)o;
			vehicleItem.useItem(client, (byte)205);
			return;
		}
		System.out.println("Unhandled DisMount!!!:" + targetID );
	}

	private void handleCreateWaypointAtPosition( long targetID, String[] sParamaters) throws IOException {
		// Indices:  PlanetName = 0;
		// x = 1;
		// z = 2;
		// y = 3;
		// blank = 4;?  Possibly requested name?




		String sPlanetName = sParamaters[0];
		int iPlanetID = -1;
		for (int i = 0; iPlanetID == -1 && i < Constants.PlanetNames.length; i++) {
			if (sPlanetName.equalsIgnoreCase(Constants.PlanetNames[i])) {
				iPlanetID = i;
			}
		}
		if (iPlanetID == -1) {
			iPlanetID = player.getPlanetID();
		}
		float wayX = Float.parseFloat(sParamaters[1]);
		float wayZ = Float.parseFloat(sParamaters[2]);
		float wayY = Float.parseFloat(sParamaters[3]);
		StringBuffer sWaypointName = new StringBuffer();
		for (int i = 4; i < sParamaters.length; i++) {
			sWaypointName.append(sParamaters[i]).append(" ");
		}
		Waypoint w = new Waypoint();
		long lID = server.getNextObjectID();
		w.setID(lID);
		w.setX(wayX);
		w.setZ(wayZ);
		w.setY(wayY);
		w.setOwnerID(player.getID());
		w.setName(sWaypointName.toString());
		// If the planet ID is unknown, create the waypoint on Naboo.
		try {
			w.setPlanetCRC(Constants.PlanetCRCForWaypoints[iPlanetID]);
			w.setName(Constants.WAYPOINT_DEFAULT_NAMES[iPlanetID]);
		} catch (ArrayIndexOutOfBoundsException e) {
			w.setPlanetCRC(Constants.PlanetCRCForWaypoints[Constants.TATOOINE]);
			w.setName(Constants.WAYPOINT_DEFAULT_NAMES[Constants.TATOOINE]);
		}
		w.setIsActivated(true);
		w.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
		player.addWaypoint(w, true);
		server.addObjectToAllObjects(w, false,false);
	}

	private void handleOpenContainer( long targetID, String[] sParameters) throws IOException {
		System.out.println("Open Container requested.");

		SOEObject o = server.getObjectFromAllObjects(targetID);
		TutorialObject tut = null;

		if(o instanceof TangibleItem)
		{
			TangibleItem t = (TangibleItem)o;

			if(t.getCellID()!=0)
			{
				Cell c = (Cell)server.getObjectFromAllObjects(t.getCellID());
				Structure s = c.getBuilding();

				if(player.getTutorial() != null)
				{
					tut = player.getTutorial();
				}
				if(!s.isAdmin(player.getID()))
				{
					if(tut!=null)
					{
						if(t.equals(tut.getSuppliesDrum()))
						{
							client.insertPacket(PacketFactory.buildOpenContainerMessage(t,-1));
							return;
						}
					}
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must be structure admin to open " + t.getName()));
					return;
				}

				client.insertPacket(PacketFactory.buildOpenContainerMessage(t,-1));
			}
			else if(t.getOwnerID() == player.getID())
			{
				client.insertPacket(PacketFactory.buildOpenContainerMessage(t,-1));
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have permission to open " + t.getName()));
			}
		}
		if(o instanceof Player)
		{
			Player p = (Player)o;
			if(p.getID() == player.getID())
			{
				client.insertPacket(PacketFactory.buildOpenContainerMessage(p,-1));
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have permission to access " + p.getFirstName()));
			}
		}
		else
		{
			System.out.println("Unhandled Container Class in ZoneClientThread.handleOpenContainer " + o.getID() + " " + o.getClass());
		}
	}

	// This function called when a factory input / output window is closed.
	private void handleCloseContainer(long targetID, String[] sParams) throws IOException { 
		SOEObject containerToClose = server.getObjectFromAllObjects(targetID);
		if (containerToClose instanceof TangibleItem) {
			TangibleItem tangibleContainer = (TangibleItem)containerToClose;
			Vector<TangibleItem> vLinkedItems = tangibleContainer.getLinkedObjects();
			if (!vLinkedItems.isEmpty()) {
				for (int i = 0; i < vLinkedItems.size(); i++) {
					player.despawnItem(vLinkedItems.elementAt(i));
				}
			}
		}
	}
	
	/**
	 * This Request from the client, instructs us to set up a syncronized session. This means that all actions and or events
	 * related to the item being request to be sybchronized have to be sent to the client since the client is now expecting
	 * data related to any changes on this item. This applies to playing music and or but not limited to harvester operations.
	 * New synch requests have to be added here as they arise and server comes closer to completion.
	 * The object updating the client should have a list of clients to update in its update function. See Structure.java for more details.
	 * The session has to be terminated upon client walking too far from the object. Depending on the session the client will inform to terminate.
	 * Each object has to be responsible for clearing out either dead or too far away clients.
	 * @param targetID
	 * @param sParameters
	 * @throws java.io.IOException
	 */
	private void handleSynchronizedUIListen( long targetID, String[] sParameters) throws IOException {
		//System.out.println("Received SynchronizedUIListen from the client, but we don't have a UI???");
		try{
			SOEObject o = server.getObjectFromAllObjects(targetID);
			if (o == null) {
				System.out.println("Null synchronized UI listen object.");
				return;
			}
			//player.setSynchronizedListenObject(o);
			if(o instanceof Structure)
			{
				Structure s = (Structure)o;
				
				/**
				 * 05 00
				 * 46 5E CE 80
				 * 23 00 00 00
				 * 16 01 00 00
				 * A6 FF 7C 4D 09 00 00 00
				 * 00 00 00 00
				 * A0 01 00 00
				 * B6 6A 99 F9 //F9996AB6   synchronizeduilisten
				 * 30 3E ED 15 36 00 00 00
				 * 00 00 00 00*/
				try{
 
					if(s instanceof Harvester) 
					{
						Harvester h = (Harvester)s;
						if(h.getCurrentHarvestResource()!=null && !h.getCurrentHarvestResource().isSpawned() || h.getCurrentHarvestResource()!=null &&  h.getCurrentHarvestResource().getBestDensityAtLocation(h.getX(), h.getY()) < 1)
						{
							h.deactivateInstallation();
						}
						Vector<SpawnedResourceData> vResourcesAvailable = h.getResourcesAvailable();
						//System.out.println(vResourcesAvailable.size() + " Resources Available for this Harvester.");
						player.setSynchronizedListenObject(s);
						client.insertPacket(PacketFactory.buildBaselineHINO7(h, vResourcesAvailable));
						client.insertPacket(PacketFactory.buildDeltasMessageHINO7(h, vResourcesAvailable));
						//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)7,(short)1,(short)5, harvester, harvester.getCurrentHarvestResource().getID()));
						h.addSyncronizedListener(client);
					}
					else
					{
						DataLog.logEntry("Error Setting Syncronized UI Listen on Unhandled Structure " + s.getIFFFileName() + " : " + s.getID(), "ZoneClientThread", Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
					}

				}catch(Exception e){
					DataLog.logException("Exception in handleSynchronizedUIListen", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
				}

			} else if (o instanceof TangibleItem) {
				TangibleItem tanItem = (TangibleItem)o;
				int templateID = tanItem.getTemplateID();
				//ItemTemplate template = DatabaseInterface.getTemplateDataByID(templateID);
				switch (templateID) {
					case 8922: {
						// Clothing crafting
					}
					case 8925: {
						// Food or spice crafting
					}
					case 8926: {
						//Generic Crafting Tool
					}
					case 8927: {
						// Jedi tool
					}
					case 8934: {
						// Space tool -- ships, missiles, blasters, etc.
					}
					case 8937: {
						// Structures
					}
					case 8940: // Weeapon tool
					{
						//System.out.println("Synch listen on generic crafting tool.  Class name: " + tanItem.getClass().getCanonicalName());
						client.insertPacket(PacketFactory.buildBaselineTANO7(tanItem));
						//Craft Station ID Delta Update
						tanItem.addSynchListener(client);
						player.setSynchronizedListenObject(tanItem);
						break;
					}
					default: {
						System.out.println("Synchronized UI Listen on Tangible Item with template " + templateID + ", IFF filename " + tanItem.getIFFFileName());
						break;
					}
				}
			} else if (o instanceof ManufacturingSchematic) {
				//System.out.println("Synch listen on manufacturing schematic.");
				try {
					CraftingTool tool = (CraftingTool)player.getSynchronizedListenObject();
					if (tool != null) {
						ManufacturingSchematic schematic = (ManufacturingSchematic)o;
						player.setSynchronizedListenObject(schematic);
						schematic.setToolUsedToCraft(tool);
						schematic.addSynchListener(client);
						client.insertPacket(PacketFactory.buildBaselineMSCO7(schematic));
					}
				} catch (Exception e) {
					// D'oh!  Odds are that the player has a ManufacturingSchematic in his datapad -- zoning in -- we do NOT want to start him crafting.
				}
			}
			else
			{
				DataLog.logEntry("Unhandled Item type: " + o.getIFFFileName() + " Class" + o.getClass() + " in handleSynchronizedUIListen" ,"ZoneClientThread",Constants.LOG_SEVERITY_CRITICAL,ZoneServer.ZoneRunOptions.bLogToConsole,true);
			}
		}catch(Exception e){
			DataLog.logException("Exception Caught in handleSynchronizedUIListen", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	private void handleSynchronizedUIStopListen( long targetID, String[] sParameters) throws IOException{
		try{
			System.out.println("Received SynchronizedUIStopListen on target ID " + Long.toHexString(targetID));
			SOEObject o = server.getObjectFromAllObjects(targetID);
			SOEObject synch = player.getSynchronizedListenObject();
			if (o.getID() != synch.getID()) {
				// D'oh!
				System.out.println("Error: Client de-synchronizing on improper object.");
			}
			player.setSynchronizedListenObject(null);
			if(o instanceof Structure)
			{
				Structure s = (Structure)o;
				if(s instanceof Harvester)
				{
					Harvester h = (Harvester)s;
					h.removeSyncronizedListener(client);
				}
				else
				{
					DataLog.logEntry("Error Setting Syncronized UI Stop Listen on Unhandled Structure " + s.getIFFFileName() + " : " + s.getID(), "ZoneClientThread", Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
				}
			} else if (o instanceof TangibleItem) {
				TangibleItem tanItem = (TangibleItem)o;
				int templateID = tanItem.getTemplateID();
				//ItemTemplate template = DatabaseInterface.getTemplateDataByID(templateID);
				switch (templateID) {
				case 8926: //Generic Crafting Tool
				{

					tanItem.removeSynchListener(client);
					player.setSynchronizedListenObject(null);
					break;
				}
				default: {
					System.out.println("Synchronized UI Stop Listen on Tangible Item with template " + templateID + ", IFF filename " + tanItem.getIFFFileName());
					break;
				}
				}
			}
			else
			{
				DataLog.logEntry("Unhandled Item type: " + o.getIFFFileName() + " Class" + o.getClass() + " in handleSynchronizedUIStopListen" ,"ZoneClientThread",Constants.LOG_SEVERITY_CRITICAL,ZoneServer.ZoneRunOptions.bLogToConsole,true);
			}
		}catch(Exception e){
			DataLog.logException("Exception in handleSynchronizedUIListen", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole,true, e);
		}
	}

	private void handleRequestCraftingSession( long targetID, String[] sParamaters) throws IOException {
		CraftingTool t = (CraftingTool)server.getObjectFromAllObjects(targetID);
		SOEObject synchObject = player.getSynchronizedListenObject();
		
		if (t.getID() != synchObject.getID()) {
			System.out.println("Error:  Start crafting on improper synchronized object.");
		} else {
			t = (CraftingTool) synchObject;
		}
		int templateID = t.getTemplateID();
		//client.insertPacket(PacketFactory.buildBaselineTANO7(t));
		//client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC));
		// Should we be experimenting?
		//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)5, player.getPlayData(), (int)10));
		switch (templateID) {
		case 8922: {
			// Clothing and armor crafting
			boolean bFoundStation = false;
			Vector<SOEObject> vNearbyObjects = server.getWorldObjectsAroundObject(player, 6.0f);
			vNearbyObjects.addAll(server.getStaticObjectsAroundObject(player, 6));
			SOEObject o = null;
			for (int i = 0; i < vNearbyObjects.size() && !bFoundStation; i++) {
				o = vNearbyObjects.elementAt(i);
				System.out.println(o.getIFFFileName());
				if (o.getIFFFileName().contains("shared_public_clothing_station")) {
					bFoundStation = true;
				}
			}
			TangibleItem nearbyStation = null;
			if (bFoundStation) {
				try {
					nearbyStation = (TangibleItem)o;
				} catch (ClassCastException e) {
					// The station isn't a tangible item???
					System.err.println("Couldn't cast nearby crafting station.");
					nearbyStation = null;
				}
			}
			if (nearbyStation != null) {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(3)); // Setting this to 2, 3 or 4 gives us "Experiment" and "Create Prototype" 
				client.insertPacket(player.setNearbyCraftingStation(nearbyStation));
				
				client.insertPacket(player.setNumExperimentationPoints(10)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			} else {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(1));
				client.insertPacket(player.setNearbyCraftingStation(null));
				client.insertPacket(player.setNumExperimentationPoints(-1)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			}
			client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC));

			BitSet schematics = player.getPlayData().getSchematics();
			Vector<CraftingSchematic> vSchematicsToDisplay = new Vector<CraftingSchematic>();
			int schematicComplexityLimit = 0;
			for (int i = schematics.nextSetBit(0); i >= 0; i = schematics.nextSetBit(i+1)) {
				CraftingSchematic schem = DatabaseInterface.getSchematicByIndex(i);
				int toolTab = schem.getIToolTabBitmask();
				if (nearbyStation == null) {
					schematicComplexityLimit = 20;
				} else {
					schematicComplexityLimit = 100;
				}
				if (schem.getComplexity() <= schematicComplexityLimit) {
					if ((toolTab == Constants.CRAFTING_TOOL_TAB_ARMOR)
							|| (toolTab == Constants.CRAFTING_TOOL_TAB_CLOTHING)
							) {
						vSchematicsToDisplay.add(schem);
					}
				}
			}
			client.insertPacket(PacketFactory.buildObjectController_CraftingSchematicList(player, t, vSchematicsToDisplay, nearbyStation));
			player.setLastSentCraftingSchematicList(vSchematicsToDisplay);
			break;
		}
		case 8925: {
			// Food or spice crafting
			boolean bFoundStation = false;
			Vector<SOEObject> vNearbyObjects = server.getWorldObjectsAroundObject(player, 6.0f);
			vNearbyObjects.addAll(server.getStaticObjectsAroundObject(player, 6));
			SOEObject o = null;
			for (int i = 0; i < vNearbyObjects.size() && !bFoundStation; i++) {
				o = vNearbyObjects.elementAt(i);
				System.out.println(o.getIFFFileName());
				if (o.getIFFFileName().contains("shared_public_food_station")) {
					bFoundStation = true;
				}
			}
			TangibleItem nearbyStation = null;
			if (bFoundStation) {
				try {
					nearbyStation = (TangibleItem)o;
				} catch (ClassCastException e) {
					// The station isn't a tangible item???
					System.err.println("Couldn't cast nearby crafting station.");
					nearbyStation = null;
				}
			}
			if (nearbyStation != null) {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(3)); // Setting this to 2, 3 or 4 gives us "Experiment" and "Create Prototype" 
				client.insertPacket(player.setNearbyCraftingStation(nearbyStation));
				client.insertPacket(player.setNumExperimentationPoints(10)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			} else {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(1));
				client.insertPacket(player.setNearbyCraftingStation(null));
				client.insertPacket(player.setNumExperimentationPoints(-1)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			}
			client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC));

			BitSet schematics = player.getPlayData().getSchematics();
			Vector<CraftingSchematic> vSchematicsToDisplay = new Vector<CraftingSchematic>();
			int schematicComplexityLimit = 0;
			for (int i = schematics.nextSetBit(0); i >= 0; i = schematics.nextSetBit(i+1)) {
				CraftingSchematic schem = DatabaseInterface.getSchematicByIndex(i);
				int toolTab = schem.getIToolTabBitmask();
				if (nearbyStation == null) {
					schematicComplexityLimit = 20;
				} else {
					schematicComplexityLimit = 100;
				}
				if (schem.getComplexity() <= schematicComplexityLimit) {
					if ((toolTab == Constants.CRAFTING_TOOL_TAB_CHEMICAL)
							|| (toolTab == Constants.CRAFTING_TOOL_TAB_FOOD)
							) {
						vSchematicsToDisplay.add(schem);
					}
				}
			}
			client.insertPacket(PacketFactory.buildObjectController_CraftingSchematicList(player, t, vSchematicsToDisplay, nearbyStation));
			player.setLastSentCraftingSchematicList(vSchematicsToDisplay);
			break;
		}
		case 8926: {
			// Generic tool
			// This is all we have now.
			client.insertPacket(player.setNearbyCraftingStation(null));
			client.insertPacket(player.setExperimentationAndManufacturingFlag(1));
			client.insertPacket(player.setNumExperimentationPoints(-1));
			client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC));
			BitSet schematics = player.getPlayData().getSchematics();
			Vector<CraftingSchematic> vSchematicsToDisplay = new Vector<CraftingSchematic>();
			for (int i = schematics.nextSetBit(0); i >= 0; i = schematics.nextSetBit(i+1)) {
				CraftingSchematic schem = DatabaseInterface.getSchematicByIndex(i);
				if (schem.getComplexity() < 20) {
					vSchematicsToDisplay.add(schem);
				}
			}
			client.insertPacket(PacketFactory.buildObjectController_CraftingSchematicList(player, t, vSchematicsToDisplay, null));
			player.setLastSentCraftingSchematicList(vSchematicsToDisplay);
			break;
		}
		case 8927: {
			// Jedi tool
			boolean bFoundStation = false;
			Vector<SOEObject> vNearbyObjects = server.getWorldObjectsAroundObject(player, 6.0f);
			vNearbyObjects.addAll(server.getStaticObjectsAroundObject(player, 6));
			SOEObject o = null;
			for (int i = 0; i < vNearbyObjects.size() && !bFoundStation; i++) {
				o = vNearbyObjects.elementAt(i);
				System.out.println(o.getIFFFileName());
				if (o.getIFFFileName().contains("shared_public_weapon_station")) {
					bFoundStation = true;
				}
			}
			TangibleItem nearbyStation = null;
			if (bFoundStation) {
				try {
					nearbyStation = (TangibleItem)o;
				} catch (ClassCastException e) {
					// The station isn't a tangible item???
					System.err.println("Couldn't cast nearby crafting station.");
					nearbyStation = null;
				}
			}
			if (nearbyStation != null) {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(3)); // Setting this to 2, 3 or 4 gives us "Experiment" and "Create Prototype" 
				client.insertPacket(player.setNearbyCraftingStation(nearbyStation));
				client.insertPacket(player.setNumExperimentationPoints(10)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			} else {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(1));
				client.insertPacket(player.setNearbyCraftingStation(null));
				client.insertPacket(player.setNumExperimentationPoints(-1)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			}
			client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC));

			BitSet schematics = player.getPlayData().getSchematics();
			Vector<CraftingSchematic> vSchematicsToDisplay = new Vector<CraftingSchematic>();
			int schematicComplexityLimit = 0;
			for (int i = schematics.nextSetBit(0); i >= 0; i = schematics.nextSetBit(i+1)) {
				CraftingSchematic schem = DatabaseInterface.getSchematicByIndex(i);
				int toolTab = schem.getIToolTabBitmask();
				if (nearbyStation == null) {
					schematicComplexityLimit = 20;
				} else {
					schematicComplexityLimit = 100;
				}
				if (schem.getComplexity() <= schematicComplexityLimit) {
					if (toolTab == Constants.CRAFTING_TOOL_TAB_JEDI_ITEM) {
						vSchematicsToDisplay.add(schem);
					}
				}
			}
			client.insertPacket(PacketFactory.buildObjectController_CraftingSchematicList(player, t, vSchematicsToDisplay, nearbyStation));
			player.setLastSentCraftingSchematicList(vSchematicsToDisplay);
			
			
			break;
		}
		case 8934: {
			// Space tool -- ships, missiles, blasters, etc.
			boolean bFoundStation = false;
			Vector<SOEObject> vNearbyObjects = server.getWorldObjectsAroundObject(player, 6.0f);
			vNearbyObjects.addAll(server.getStaticObjectsAroundObject(player, 6));
			SOEObject o = null;
			for (int i = 0; i < vNearbyObjects.size() && !bFoundStation; i++) {
				o = vNearbyObjects.elementAt(i);
				System.out.println(o.getIFFFileName());
				if (o.getIFFFileName().contains("shared_public_space_station")) {
					bFoundStation = true;
				}
			}
			TangibleItem nearbyStation = null;
			if (bFoundStation) {
				try {
					nearbyStation = (TangibleItem)o;
				} catch (ClassCastException e) {
					// The station isn't a tangible item???
					System.err.println("Couldn't cast nearby crafting station.");
					nearbyStation = null;
				}
			}
			if (nearbyStation != null) {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(3)); // Setting this to 2, 3 or 4 gives us "Experiment" and "Create Prototype" 
				client.insertPacket(player.setNearbyCraftingStation(nearbyStation));
				client.insertPacket(player.setNumExperimentationPoints(10)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			} else {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(1));
				client.insertPacket(player.setNearbyCraftingStation(null));
				client.insertPacket(player.setNumExperimentationPoints(-1)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			}
			client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC));

			BitSet schematics = player.getPlayData().getSchematics();
			Vector<CraftingSchematic> vSchematicsToDisplay = new Vector<CraftingSchematic>();
			int schematicComplexityLimit = 0;
			for (int i = schematics.nextSetBit(0); i >= 0; i = schematics.nextSetBit(i+1)) {
				CraftingSchematic schem = DatabaseInterface.getSchematicByIndex(i);
				int toolTab = schem.getIToolTabBitmask();
				if (nearbyStation == null) {
					schematicComplexityLimit = 20;
				} else {
					schematicComplexityLimit = 100;
				}
				if (schem.getComplexity() <= schematicComplexityLimit) {
					if (toolTab == Constants.CRAFTING_TOOL_TAB_MISCELLANEOUS) {
						vSchematicsToDisplay.add(schem);
					}
				}
			}
			client.insertPacket(PacketFactory.buildObjectController_CraftingSchematicList(player, t, vSchematicsToDisplay, nearbyStation));
			player.setLastSentCraftingSchematicList(vSchematicsToDisplay);
			break;
		}
		case 8937: {
			// Structures and furniture
			boolean bFoundStation = false;
			Vector<SOEObject> vNearbyObjects = server.getWorldObjectsAroundObject(player, 6.0f);
			vNearbyObjects.addAll(server.getStaticObjectsAroundObject(player, 6));
			SOEObject o = null;
			for (int i = 0; i < vNearbyObjects.size() && !bFoundStation; i++) {
				o = vNearbyObjects.elementAt(i);
				System.out.println(o.getIFFFileName());
				if (o.getIFFFileName().contains("shared_public_structure_station")) {
					bFoundStation = true;
				}
			}
			TangibleItem nearbyStation = null;
			if (bFoundStation) {
				try {
					nearbyStation = (TangibleItem)o;
				} catch (ClassCastException e) {
					// The station isn't a tangible item???
					System.err.println("Couldn't cast nearby crafting station.");
					nearbyStation = null;
				}
			}
			if (nearbyStation != null) {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(3)); // Setting this to 2, 3 or 4 gives us "Experiment" and "Create Prototype" 
				client.insertPacket(player.setNearbyCraftingStation(nearbyStation));
				client.insertPacket(player.setNumExperimentationPoints(10)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			} else {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(1));
				client.insertPacket(player.setNearbyCraftingStation(null));
				client.insertPacket(player.setNumExperimentationPoints(-1)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			}
			client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC));

			BitSet schematics = player.getPlayData().getSchematics();
			Vector<CraftingSchematic> vSchematicsToDisplay = new Vector<CraftingSchematic>();
			int schematicComplexityLimit = 0;
			for (int i = schematics.nextSetBit(0); i >= 0; i = schematics.nextSetBit(i+1)) {
				CraftingSchematic schem = DatabaseInterface.getSchematicByIndex(i);
				int toolTab = schem.getIToolTabBitmask();
				if (nearbyStation == null) {
					schematicComplexityLimit = 20;
				} else {
					schematicComplexityLimit = 100;
				}
				if (schem.getComplexity() <= schematicComplexityLimit) {
					if ((toolTab == Constants.CRAFTING_TOOL_TAB_STRUCTURE) || (toolTab == Constants.CRAFTING_TOOL_TAB_FURNITURE))
					{
						vSchematicsToDisplay.add(schem);
					}
				}
			}
			client.insertPacket(PacketFactory.buildObjectController_CraftingSchematicList(player, t, vSchematicsToDisplay, nearbyStation));
			player.setLastSentCraftingSchematicList(vSchematicsToDisplay);
			break;
		}
		case 8940: {
			// Weapon, Droid, General Item -- You are identical to General, except you can handle more advanced schematics.
			boolean bFoundStation = false;
			Vector<SOEObject> vNearbyObjects = server.getWorldObjectsAroundObject(player, 6.0f);
			vNearbyObjects.addAll(server.getStaticObjectsAroundObject(player, 6));
			SOEObject o = null;
			for (int i = 0; i < vNearbyObjects.size() && !bFoundStation; i++) {
				o = vNearbyObjects.elementAt(i);
				System.out.println(o.getIFFFileName());
				if (o.getIFFFileName().contains("shared_public_weapon_station")) {
					bFoundStation = true;
				}
			}
			TangibleItem nearbyStation = null;
			if (bFoundStation) {
				try {
					nearbyStation = (TangibleItem)o;
				} catch (ClassCastException e) {
					// The station isn't a tangible item???
					System.err.println("Couldn't cast nearby crafting station.");
					nearbyStation = null;
				}
			}
			if (nearbyStation != null) {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(3)); // Setting this to 2, 3 or 4 gives us "Experiment" and "Create Prototype" 
				client.insertPacket(player.setNearbyCraftingStation(nearbyStation));
				client.insertPacket(player.setNumExperimentationPoints(10)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			} else {
				client.insertPacket(player.setExperimentationAndManufacturingFlag(1));
				client.insertPacket(player.setNearbyCraftingStation(null));
				client.insertPacket(player.setNumExperimentationPoints(-1)); // TODO -- This is the value of the "experimentation" skill mod tied to this particular schematic, divided by 10.
			}
			client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC));

			BitSet schematics = player.getPlayData().getSchematics();
			Vector<CraftingSchematic> vSchematicsToDisplay = new Vector<CraftingSchematic>();
			int schematicComplexityLimit = 0;
			for (int i = schematics.nextSetBit(0); i >= 0; i = schematics.nextSetBit(i+1)) {
				CraftingSchematic schem = DatabaseInterface.getSchematicByIndex(i);
				int toolTab = schem.getIToolTabBitmask();
				if (nearbyStation == null) {
					schematicComplexityLimit = 20;
				} else {
					schematicComplexityLimit = 100;
				}
				if (schem.getComplexity() <= schematicComplexityLimit) {
					if ((toolTab == Constants.CRAFTING_TOOL_TAB_DROID)
							|| (toolTab == Constants.CRAFTING_TOOL_TAB_GENERIC_ITEM)
							|| (toolTab == Constants.CRAFTING_TOOL_TAB_MISCELLANEOUS)
							|| (toolTab == Constants.CRAFTING_TOOL_TAB_VEHICLE)
							|| (toolTab == Constants.CRAFTING_TOOL_TAB_WEAPON)) {
						vSchematicsToDisplay.add(schem);
					}
				}
			}
			client.insertPacket(PacketFactory.buildObjectController_CraftingSchematicList(player, t, vSchematicsToDisplay, nearbyStation));
			player.setLastSentCraftingSchematicList(vSchematicsToDisplay);
			break;
		}
		default: {
			client.insertPacket(PacketFactory.buildChatSystemMessage("Server error -- attempting to craft using object that is NOT a crafting tool."));
			break;
		}
		}
	}

	private void handleStartMusic(CommandQueueItem action, long targetID, String[] sParameters) throws IOException {
		if(!player.hasSkill(11))
		{
			/*client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"music_lack_skill_self",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));*/
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INSUFFICIENT_SKILL);
			return;
		} 
		if(player.getCurrentHam()[3] <= 100)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"music_fail",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
			return;
		}
		if(player.isPlayingMusic() || player.isDancing())
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"already_performing_self",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
			return;
		}

		if(player.getEquippedInstrument()==null)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"music_no_instrument",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
			return;
		}


		if(sParameters[0].isEmpty())
		{
			Vector<String> vKnownMusic = player.getKnownMusic();
			String [] sDanceList = new String [vKnownMusic.size()];
			for(int i = 0; i < vKnownMusic.size();i++)
			{
				sDanceList[i] = vKnownMusic.get(i);
			}
			String WindowTypeString = "handleSUI";
			String DataListTitle = "@performance:music_no_music_param";
			String DataListPrompt = "@performance:music_no_music_param";
			long ObjectID = 0;
			long PlayerID = 0;
			SUIWindow w = new SUIWindow(player);
			w.setWindowType(Constants.SUI_START_MUSIC_NO_PARAMS);
			//SUIScriptListBox(ZoneClient client, String WindowTypeString, String DataListTitle, String DataListPrompt, String sList[], Vector<SOEObject> ObjectList,long ObjectID, long PlayerID){
			client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sDanceList, null, ObjectID, PlayerID));
		}
		else
		{
			int iSongID = 0;
			boolean hasSkill = false;


			for(int i = 0; i < Constants.MUSIC_STRINGS.length; i++)
			{
				if(Constants.MUSIC_STRINGS[i][0].compareToIgnoreCase(sParameters[0])==0)
				{
					hasSkill = player.hasSkill(Constants.MUSIC_SKILL_REQUIREMENTS[i]);
					iSongID = Integer.parseInt(Constants.MUSIC_STRINGS[i][1]);
				}
			}
			if(hasSkill)
			{
				if (player.setStance(action, Constants.STANCE_ANIMATING_SKILL, false)) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"performance",
							"music_start_self",
							player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
							0, null, null, null,
							0, null, null, null,
							0, 0.0f, false
					));
					//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x0C, player, 0x0119), Constants.PACKET_RANGE_CHAT_RANGE);
					player.setSongID(iSongID);
					//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x0B, player, 0xa2B1C5F), Constants.PACKET_RANGE_CHAT_RANGE);
					player.setListeningToID(player.getID());
					//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x03, player, sSelectedDance, false), Constants.PACKET_RANGE_CHAT_RANGE);
					player.setPerformanceString("music_3");
					player.setMusicTick(9000);
					player.updateCurrentHam(Constants.HAM_INDEX_ACTION, -100);
					player.setIsPlayingMusic(true);
				}
			}
			else
			{
				action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INSUFFICIENT_SKILL);
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"performance",
						"music_lack_skill_self",
						player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
						0, null, null, null,
						0, null, null, null,
						0, 0.0f, false
				));
			}
		}

	}

	private void handleStopMusic(CommandQueueItem action, long targetID, String[] sParameters) throws IOException {

		if(player.isPlayingMusic() && !player.isDancing())
		{
			if (player.setStance(action, Constants.STANCE_STANDING, false)) {
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"performance",
						"music_stop_self",
						player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
						0, null, null, null,
						0, null, null, null,
						0, 0.0f, false
				));
			
		
				Vector<Player> vPL = player.getPlayersListening();
				for(int i = 0; i < vPL.size(); i ++)
				{
					Player p = vPL.get(i);
					p.setPlayerBeingListened(null);                
				}
			}
		}
	}

	private void handleStartDance(CommandQueueItem action, long targetID, String[] sParameters) throws IOException {
		String sDanceTypeToStart = sParameters[0];

		if(!player.hasSkill(11))
		{
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INSUFFICIENT_SKILL);
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"dance_lack_skill_self",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
			return;
		}

		if(player.getCurrentHam()[3] <= 100)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"dance_fail",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
			return;
		}

		if(player.isDancing() || player.isPlayingMusic())
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"already_performing_self",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));
			return;
		}

		// TODO:  Compile list of dances, and match them with the client list of dance animations.
		if(sParameters[0].isEmpty())
		{
			//actually from here we send a sui box containing the dances the player knows
			//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x03, player, "dance_1", false), Constants.PACKET_RANGE_CHAT_RANGE);
			Vector<String> vKnownDances = player.getKnownDances();
			String [] sDanceList = new String [vKnownDances.size()];
			for(int i = 0; i < vKnownDances.size();i++)
			{
				sDanceList[i] = vKnownDances.get(i);
			}
			String WindowTypeString = "handleSUI";
			String DataListTitle = "@performance:dance_no_dance_param";
			String DataListPrompt = "@performance:dance_no_dance_param";
			long ObjectID = 0;
			long PlayerID = 0;
			SUIWindow w = new SUIWindow(player);
			w.setWindowType(Constants.SUI_START_DANCE_NO_PARAMS);
			//SUIScriptListBox(ZoneClient client, String WindowTypeString, String DataListTitle, String DataListPrompt, String sList[], Vector<SOEObject> ObjectList,long ObjectID, long PlayerID){
			client.insertPacket(w.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sDanceList, null, ObjectID, PlayerID));

		}
		else
		{
			System.out.println("Dance type started: " + sDanceTypeToStart);
			String sSelectedDance = "";
			int iSkillRequired = 0;
			for(int i = 0; i < Constants.DANCE_STRINGS.length;i++)
			{
				if(Constants.DANCE_STRINGS[i][0].compareToIgnoreCase(sParameters[0]) == 0)
				{
					sSelectedDance = Constants.DANCE_STRINGS[i][1];
					iSkillRequired = Constants.DANCE_SKILL_REQUIREMENTS[i];
				}
			}
			if(player.hasSkill(iSkillRequired))
			{
				if (player.setStance(action, Constants.STANCE_ANIMATING_SKILL, false)) {                
	
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"performance",
							"dance_start_self",
							player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
							0, null, null, null,
							0, null, null, null,
							0, 0.0f, false
					));
					//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x0C, player, 0x0119), Constants.PACKET_RANGE_CHAT_RANGE);
					player.setSongID(0x0119);
					//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x0B, player, 0xa2B1C5F), Constants.PACKET_RANGE_CHAT_RANGE);
					player.setPerformanceID(0xa2B1C5F);
	
					//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x11, player, true), Constants.PACKET_RANGE_CHAT_RANGE);
					player.setC60X11(true);
					//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)0x03, player, sSelectedDance, false), Constants.PACKET_RANGE_CHAT_RANGE);               
					player.setPerformanceString(sSelectedDance);
					player.setDanceTick(9000);
	
					player.setIsDancing(true);
					player.updateCurrentHam(Constants.HAM_INDEX_ACTION, -100);
				}
			}
			else
			{
				action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INSUFFICIENT_SKILL);
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"performance",
						"dance_unknown_self",
						player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
						0, null, null, null,
						0, null, null, null,
						0, 0.0f, false
				));
			}

		}

	}

	private void handleStopDance(CommandQueueItem action, long targetID, String[] sParameters) throws IOException {
		if(player.isDancing())
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"performance",
					"dance_stop_self",
					player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
					0, null, null, null,
					0, null, null, null,
					0, 0.0f, false
			));

			Vector<Player> vWL = player.getPlayersWatching();
			for(int i =0; i < vWL.size(); i++)
			{
				Player p = vWL.get(i);
				p.setPlayerBeingWatched(null);
				p.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
						"performance",
						"dance_stop_other",                    
						player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFullName(),
						0, null, null, null,
						0, null, null, null,
						0, 0.0f, false
				));

			}
			player.setStance(action, Constants.STANCE_STANDING, false);                
		}

	}

	private void handleGetAttributesBatch(long targetID, String[] sParamaters) throws IOException {
		for (int i = 0; i < sParamaters.length; i++) {
			long objectID = Long.parseLong(sParamaters[i]);
			SOEObject o = server.getObjectFromAllObjects(objectID);
			
			if (o != null) {
				client.insertPacket(PacketFactory.buildAttributeListMessage(client,o));
			} else {
				
				SpawnedResourceData resData = server.getResourceManager().getResourceByID(objectID);
				if (resData != null) {
					client.insertPacket(PacketFactory.buildAttributeListMessage(resData));
				} 
			}
		}
	}

	private void handleEmailEnqueue(long targetID, String[] sParameters) throws IOException {

		//int i = 0;
		//while(sParameters[i].length() != 0)
		//{
		//    System.out.println("Received an EmailEnqueue packet: TID" + targetID + "Params: " + sParameters[i]);
		//    i++;
		// }

		//client.insertPacket(PacketFactory.buildChatSystemMessage("Received an EmailEnqueue packet from you"));
		//client.insertPacket(PacketFactory.buildChatSystemMessage("Please report a bug, documenting what you did to generate this system message."));
	}

	private void handleSlashWaypoint(long targetID, String[] sParameters) {
		// NO.  Passed in paramaters should over-ride setting at waypoint at the target.
		System.out.println("handleSlashWaypoint");

		SOEObject o = server.getObjectFromAllObjects(player.getTargetID());

		if(o!=null)
		{
			if(o instanceof Lair)
			{
				System.out.println("Waypoint requested for a targeted object Name: " + o.getSTFDetailName() + " " + o.getSTFFileIdentifier());
			}
		}
		else
		{
			System.out.println("Target Object Null, Player target:" + player.getTargetID());
		}

		float wayX = 0;
		float wayY = 0;
		float wayZ = 0;

		if (o == null && sParameters.length == 1) {
			wayX = player.getX();
			wayY = player.getY();
			wayZ = 0;
		}else if(sParameters.length > 1) {
			try {
				wayX = Float.parseFloat(sParameters[0]);
				wayY = Float.parseFloat(sParameters[1]);
			} catch (NumberFormatException e) {
				try {
					client.insertPacket(PacketFactory.buildChatSystemMessage("Error: unable to parse waypoint coordinates."));
					client.insertPacket(PacketFactory.buildChatSystemMessage("Waypoint commands must be issued to one of the following 3 templates: "));
					client.insertPacket(PacketFactory.buildChatSystemMessage("/waypoint XXXX YYYY "));
					client.insertPacket(PacketFactory.buildChatSystemMessage("/waypoint XXXX YYYY NAME"));
					client.insertPacket(PacketFactory.buildChatSystemMessage("/waypoint XXXX YYYY ZZZZ NAME  -- for JTL waypoints only."));
					return;
				} catch (IOException ee) {
					return;
				}
			}
		} else if (o!= null){
		
			wayX = o.getX();
			wayY = o.getY();
			wayZ = o.getZ();
		} else {
			
		}
		String sWaypointName = null;

		if (sParameters.length == 3) {
			sWaypointName = sParameters[2];
		}else if (sParameters.length > 3) {
			try {
				wayZ = Float.parseFloat(sParameters[2]);
				StringBuffer wayBuff = new StringBuffer();
				for (int i = 3; i < sParameters.length; i++) {
					wayBuff.append(sParameters[i]).append(" ");
				}
				sWaypointName = wayBuff.toString();
			} catch (NumberFormatException e) {
				wayZ = 0;
				StringBuffer wayBuff = new StringBuffer();
				for (int i = 2; i < sParameters.length; i++) {
					wayBuff.append(sParameters[i]).append(" ");
				}
				sWaypointName = wayBuff.toString();
			}
		}else if(o != null && sParameters.length == 1){
			sWaypointName = "@" + o.getSTFFileName() + ":" + o.getSTFFileIdentifier();
			if(o instanceof Player)
			{
				Player np = (Player) o;
				sWaypointName = np.getFullName();
			}
			if(o instanceof Lair)
			{
				sWaypointName = "@" + o.getSTFFileName() + ":" + o.getSTFFileIdentifier();
			}
			else if(o instanceof NPC)
			{
				NPC np = (NPC) o;
				sWaypointName = np.getFirstName();
			}
			else if(o.getCraftedName() != null && !o.getCraftedName().isEmpty())
			{
				System.out.println("Crafted Name");
				sWaypointName = o.getCraftedName();
			}
			System.out.println("Waypoint name set to: " + sWaypointName);
		} else {
			sWaypointName = Constants.WAYPOINT_DEFAULT_NAMES[player.getPlanetID()];
		}
		Waypoint w = new Waypoint();
		w.setID(server.getNextObjectID());
		w.setIsActivated(true);
		w.setX(wayX);
		w.setY(wayY);
		w.setZ(wayZ);
		if (sWaypointName != null) {
			w.setName(sWaypointName);
		} else {
			w.setName(Constants.WAYPOINT_DEFAULT_NAMES[player.getPlanetID()]);
		}
		w.setPlanetCRC(Constants.PlanetCRCForWaypoints[player.getPlanetID()]);
		w.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
		player.addWaypoint(w, true);
		server.addObjectToAllObjects(w, false,false);
	}

	private void handleUpdateWaypointStatus(long targetID, String[] sParameters) {

		Waypoint w = player.getWaypoint(targetID);
		if(w == null)
		{

			return;
		}
		if (w != null) {
			w.setIsActivated(sParameters[0].equals("on"));
		}
		client.insertPacket(PacketFactory.buildWaypointDelta(player, w, Constants.DELTA_UPDATING_ITEM));
	}

	private void handleRenameWaypoint(long targetID, String[] sParamaters) throws IOException {
		System.out.println("HandleRenameWaypoint.");
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < sParamaters.length; i++) {
			s.append(sParamaters[i]);
			if (i != (sParamaters.length - 1)) {
				s.append(" ");
			}
		}
		Waypoint w = player.getWaypoint(targetID);
		
		if (w != null) {
			System.out.println("Waypoint found.  Current name: " + w.getName() + ", new name: " + s);
			w.setName(s.toString());
			Waypoint lastSurveyWaypoint = player.getSurveyWaypoint();
			if (lastSurveyWaypoint != null) {
				if (lastSurveyWaypoint.getID() == w.getID()) {
					player.addSurveyWaypoint(null); // Passing null into addSurveyWaypoint causes the Player's last survey waypoint to become null.
				}
			}			
			client.insertPacket(PacketFactory.buildWaypointDelta(player, w, Constants.DELTA_UPDATING_ITEM));
		} else {
			System.out.println("Waypoint not found.");
			client.insertPacket(PacketFactory.buildChatSystemMessage("Submit a bug:  Waypoint with ID " + targetID + " not found in your list of waypoints."));
		}
	}

	private void handleRequestSurveySession(CommandQueueItem action, long targetID, String[] paramaters) throws IOException {
		//client.insertPacket(PacketFactory.buildChatSystemMessage("You start surveying for " + paramaters[0]));
		//client.insertPacket(PacketFactory.buildChatSystemMessage("Note:  Surveying is currently not implemented"));
		if (player.getSkillMod("surveying").getSkillModModdedValue() == 0 ) {
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INSUFFICIENT_SKILL);
			return;
		}
		TangibleItem surveyTool = player.getLastUsedSurveyTool();
		if(surveyTool.getIsSampling() || surveyTool.getIsCoolingDown())
		{
			try{
				//You can't take a survey while you are collecting samples.
				client.insertPacket(PacketFactory.buildChatSystemMessage("You can't take a survey while you are collecting samples."));
			}catch(Exception e){
				//Doh!
			}
			return;
		}

		surveyTool.setIsSurveying(true, client, player);
		surveyTool.setSurveyToolTimeMS(3000);
		surveyTool.setResourceToSurvey(server.getResourceManager().getResourceByName(paramaters[0]));
		client.insertPacket(PacketFactory.buildChatSystemMessage("You begin to survey for " + surveyTool.getResourceToSurvey().getName())); //STF cant be used sowwy
		player.updateCurrentHam(Constants.HAM_INDEX_MIND, -50);

	}

	private void handleRequestCoreSample(CommandQueueItem action, long targetID, String[] sParamaters) throws IOException {
		if (player.getIsSampling()) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot sample a resource while sampling."));
			return;
		}
		if (((player.getStateBitmask() & Constants.STATE_MOUNTED_CREATURE) != 0)|| ((player.getStateBitmask() & Constants.STATE_MOUNTED_CREATURE) != 0)) {
			// ObjController message -- dequeue this with the appropriate error state.
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_IN_STANCE);
			action.setStateInvoked(Constants.COMMAND_QUEUE_ERROR_STANCE_MOUNTED_CREATURE);
			return;
		}
		try{
			TangibleItem surveyTool = player.getLastUsedSurveyTool();
			if(surveyTool == null)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You must survey for a resource before you can sample it."));
				return;
			}
			SpawnedResourceData resourceToSample= surveyTool.getResourceToSurvey();
			if (resourceToSample == null) {
				client.insertPacket(PacketFactory.buildChatSystemMessage("You must survey for a resource before you can sample it."));
			}
			else if (resourceToSample.getName().equals(sParamaters[0])) {
				float densityHere = resourceToSample.getBestDensityAtLocation(player.getX(), player.getY());
				if (densityHere > 0) {
					if (surveyTool.getIsSampling() || surveyTool.getIsCoolingDown() ) {
						//client.insertPacket(PacketFactory.buildChatSystemMessage("You must wait " + (surveyTool.getSampleToolTimeMS() / 1000) + " seconds before you may sample again.")); // TODO -- STF files!
						client.insertPacket(PacketFactory.buildChatSystemMessage("survey", "tool_recharge_time", 0l, "", "","", 0l, "", "","", 0l, "", "", "", (int)(surveyTool.getSampleToolTimeMS() / 1000), 0f, false));
					} else {
						if (player.setStance(action, Constants.STANCE_KNEELING, false)) {
							surveyTool.setIsSampling(true, player);
							surveyTool.setSampleToolTimeMS(3000);
							surveyTool.playSampleEffect(client);
							//start_sampling	You begin to sample for %TO.
							//client.insertPacket(PacketFactory.buildChatSystemMessage("You begin sampling for " + surveyTool.getResourceToSurvey().getName())); // TODO:  STF Files! // STF Cant be used because we dont have the resource names in an stf file.
							client.insertPacket(PacketFactory.buildChatSystemMessage("You begin to sample for " + surveyTool.getResourceToSurvey().getName())); //You begin sampling for " + surveyTool.getResourceToSurvey().getName())); // TODO:  STF Files!
							player.setIsSampling(true);
						}
					}
				} else {
					//client.insertPacket(PacketFactory.buildChatSystemMessage("There are only trace amounts of " + resourceToSample.getName() + " here.  Find a higher concentration of the resource, and try sampling again."));
					//trace_amount	You are only able to locate trace amounts of %TO.
					client.insertPacket(PacketFactory.buildChatSystemMessage("@survey:trace_amount:" + resourceToSample.getName())); //"There are only trace amounts of " + resourceToSample.getName() + " here.  Find a higher concentration of the resource, and try sampling again."));
				}
			} else {
				client.insertPacket(PacketFactory.buildChatSystemMessage("You must survey for " + sParamaters[0] + " before you can sample for it."));
			}
		}catch(Exception e){
			System.out.println("Exception caught in handleRequestCoreSample " + e);
			e.printStackTrace();
		}
	}

	private void handleAFKUpdate(long targetID, String[] sParamaters) {
		player.setIsAFK(!player.getIsAFK());
	}

	private void handleFindFriendRequest(long targetID, String[] sParamaters) throws IOException {
		System.out.println("Find Friend.  Paramaters: ");
		for (int i = 0; i < sParamaters.length; i++) {
			System.out.println(i + " = " + sParamaters[i]);
		}
		Player friend = server.getPlayer(sParamaters[0]);
		if (friend != null) {

			boolean bPlayerHasFriend = false;
			boolean bFriendHasPlayerAsFriend = false;

			Vector<PlayerFriends> vPlayerFriends = player.getFriendsList();
			System.out.println("Player friend list size: " + vPlayerFriends.size());
			for (int i = 0; i < vPlayerFriends.size() && !bPlayerHasFriend; i++) { 
				PlayerFriends friendName = vPlayerFriends.elementAt(i);
				System.out.println("Friend " + i + " " + friendName.getName());
				if (friendName.getName().equalsIgnoreCase(sParamaters[0])) {
					bPlayerHasFriend = true;
				}
			}
			if (bPlayerHasFriend) {
				System.out.println("Player has friend");
				Vector<PlayerFriends> vFriendPlayerFriends = friend.getFriendsList();
				for (int i = 0; i < vFriendPlayerFriends.size() && !bFriendHasPlayerAsFriend; i++) {
					PlayerFriends name = vFriendPlayerFriends.elementAt(i);
					if (name.getName().equalsIgnoreCase(sParamaters[0])) {
						bFriendHasPlayerAsFriend = true;
					}
				}
				if (bFriendHasPlayerAsFriend) {
					System.out.println("Friend has player");
					if (friend.getOnlineStatus()) {
						Waypoint w = new Waypoint();
						w.setX(friend.getX());
						w.setY(friend.getY());
						w.setZ(friend.getZ());
						w.setPlanetCRC(Constants.PlanetCRCForWaypoints[friend.getPlanetID()]);
						w.setName("Last location of " + friend.getFirstName());
						w.setIsActivated(true);
						w.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
						player.addWaypoint(w, true);
						server.addObjectToAllObjects(w, false, false);
						client.insertPacket(PacketFactory.buildChatSystemMessage("A waypoint to the last location of " + friend.getFirstName() + " has been added to your datapad."));
					} else {
						client.insertPacket(PacketFactory.buildChatSystemMessage(friend.getFirstName() + " is not online"));
					}
				} else {
					System.out.println("Friend does not have player.");
					client.insertPacket(PacketFactory.buildChatSystemMessage("Unable to locate " + friend.getFirstName()));
				}
			} else {
				System.out.println("Player does not have friend");
				client.insertPacket(PacketFactory.buildChatSystemMessage(friend.getFirstName() + " is not a valid friend name"));
			}
		} else {
			System.out.println("Friend does not exist.");
			client.insertPacket(PacketFactory.buildChatSystemMessage(sParamaters[0] + " is not a valid friend name"));
		}
	}

	private void handleTransferItemWeapon(long targetID, String[] Parameters)throws IOException {

		// Parameters[0] == destination ID.
		// parameters[1] == equipped status.
		// Paramaters[2] through Paramaters[7] == unknown, 0 data.
		// Looks the same as transferItemArmor

		long equipperID = Long.parseLong(Parameters[1]);
		SOEObject object = server.getObjectFromAllObjects(targetID);
		int actionToTake = Integer.parseInt(Parameters[2]);
		Player target;
		System.out.println("Handle transfer item weapon.  Weapon ID: " + targetID  + ", action ID " + actionToTake + ", Equipper ID:" + equipperID);
		if (player.getID() != equipperID) {
			target = (Player)server.getObjectFromAllObjects(equipperID);
			DataLog.logEntry("Possible error in TransferItemWeapon:  Unexpected Player equipping this weapon. ", "handleTransferItemWeapon", Constants.LOG_SEVERITY_INFO, true, true);
		} else {
			target = player;
		}
		if (actionToTake != 4) {
			DataLog.logEntry("Possible error in TransferItemWeapon:  Unexpected action to take -- " + actionToTake, "handleTransferItemWeapon", Constants.LOG_SEVERITY_INFO, true, true);
		}
		if(object instanceof Weapon)
		{
			Weapon theWeapon = (Weapon)object;
			if (actionToTake == 4) {
				Vector<Player> vPL = server.getPlayersAroundObject(player, false);
				for(int i =0; i < vPL.size();i++)
				{
					Player p = vPL.get(i);
					p.spawnItem(object);
				}
				target.updateEquippedWeapon(theWeapon, true);
			}
		}        
	}
	private void handleTransferItemArmor(long targetID, String[] Parameters) throws IOException {
		/**
		 *  handleTransferItemArmor Target ID:298397970 <-- Armor Component to Equip de Equip
                Parameter 0 :
                Parameter 1 : 163652673 <--New Container
                Parameter 2 : 4 <-- Equip / Deequip Action
                Parameter 3 : 0.000000
                Parameter 4 : 0.000000
                Parameter 5 : 0.000000

		 */
		try{

			long lDestinationID = Long.parseLong(Parameters[1]);
			int iAction = Integer.parseInt(Parameters[2]);
			//float x = Float.parseFloat(Parameters[3]);
			//float z = Float.parseFloat(Parameters[4]);
			//float y = Float.parseFloat(Parameters[5]);
			// iTT = itemToTransfer
			Armor iTT = (Armor)server.getObjectFromAllObjects(targetID);


			if(iTT.getRequiredSkill()!= -1)
			{
				if(!player.getPlayData().hasSkill(iTT.getRequiredSkill()))
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You lack the required skill to equip that item."));
					return;
				}
			}
			boolean hasenoughham = true;
			int failedHam = -1;
			int [] iArmorEnc = iTT.getArmorEncumberances();
			int [] iPlayerMaxHam = player.getMaxHam();
			int [] iPlayerHamModifiers = player.getHamModifiers();

			for(int i = 0; i < iArmorEnc.length; i++)
			{
				if((iPlayerMaxHam[i] + iPlayerHamModifiers[i]) < iArmorEnc[i])
				{
					hasenoughham = false;
					failedHam = i;
				}
			}
			if(!hasenoughham)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have enough " + Constants.HAM_NAMES[failedHam] + " points to equip that item."));
				return;
			}

			if(iAction == 4)
			{
				player.addEncumberance(iArmorEnc);
			}
			else if(iAction == -1)
			{
				player.removeEncumberance(iArmorEnc);
			}

			// dC = destinationContainer
			SOEObject dC = server.getObjectFromAllObjects(lDestinationID);
			//lets find out what this container is
			if(dC instanceof TangibleItem)//means its going ito another item.
			{
				//lets identify the container:
				if(player.getInventory().equals(dC))//the item is going into players inventory
				{

					if(iTT instanceof TangibleItem)
					{
						TangibleItem iT = (TangibleItem)iTT;
						if(iT.getContainer() != null && iT.getContainer() instanceof TangibleItem)
						{
							TangibleItem oC = (TangibleItem)iT.getContainer();
							oC.removeLinkedObject(iT);
						}
						player.addItemToInventory(iT);

						if(iAction == 4)
						{
							player.getInventory().addLinkedObject(iT);
							iT.setEquipped(dC, 4);//equip, this should not happen here tho but Just in case
							client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, 4),Constants.PACKET_RANGE_CHAT_RANGE);
						}
						else if(iAction == -1)//de equip
						{
							player.getInventory().removeLinkedObject(iT);
							iT.setEquipped(dC, -1);
							client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, -1),Constants.PACKET_RANGE_CHAT_RANGE);
						}
					}
				}
				else if(player.getBank().equals(dC))//the item is going to the bank
				{

					TangibleItem Bank = (TangibleItem)dC;
					TangibleItem iT = (TangibleItem)iTT;
					player.getInventory().removeLinkedObject(iT);
					player.removeItemFromInventory(iT);
					Bank.addLinkedObject(iT);
					if(iAction == 4)
					{
						iT.setEquipped(dC, 4);//equip, this should not happen here tho but Just in case
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, 4),Constants.PACKET_RANGE_CHAT_RANGE);
					}
					else if(iAction == -1)//de equip
					{
						iT.setEquipped(dC, -1);
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, -1),Constants.PACKET_RANGE_CHAT_RANGE);
					}

				}
				else if(dC instanceof TangibleItem)// its going into another type of container either equipped by the player like a back pack or something
				{

					TangibleItem newContainer = (TangibleItem)dC;
					TangibleItem iT = (TangibleItem)iTT;
					if(newContainer.isContainer() && iT.isContainer())
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("That item is too bulky to fit into that container"));
						return;
					}
					if(iT.getContainer().equals(player.getInventory()))
					{
						player.removeItemFromInventory(iT);
						player.getInventory().removeLinkedObject(iT);
					}
					else if(iT.getContainer() instanceof TangibleItem)
					{
						TangibleItem oC = (TangibleItem)iT.getContainer();
						oC.removeLinkedObject(iT);
					}
					newContainer.addLinkedObject(iT);
					if(iAction == 4)
					{
						iT.setEquipped(dC, 4);//equip, this should not happen here tho but Just in case
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, 4),Constants.PACKET_RANGE_CHAT_RANGE);
					}
					else if(iAction == -1)//de equip
					{
						iT.setEquipped(dC, -1);
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, -1),Constants.PACKET_RANGE_CHAT_RANGE);
					}
				}
			}
			else if(dC instanceof Player)//looks like we are equipping the item
			{

				if(iTT instanceof TangibleItem)
				{
					TangibleItem iT = (TangibleItem)iTT;
					if(iT.getRaceRestrictions()[0] != -1)
					{
						int [] r = iT.getRaceRestrictions();
						Vector<Integer> vRest = new Vector<Integer>();
						for(int i = 0; i < r.length; i++)
						{
							vRest.add(r[i]);
						}
						if(!vRest.contains(player.getRaceID()))
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot equip that item due to race restrictions."));
							return;
						}
					}
					if(iT.isFemaleItem())
					{
						if(!Constants.SharedRaceModels[player.getRaceID()].contains("female") )
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot equip that item due to gender restrictions."));
							return;
						}
					}
					if(iAction == 4)
					{
						iT.setEquipped(dC, 4);
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, 4),Constants.PACKET_RANGE_CHAT_RANGE);
						Vector<Player> vPL = server.getPlayersAroundObject(player, false);
						for(int i = 0; i < vPL.size(); i++)
						{
							Player p = vPL.get(i);
							p.spawnItem(iTT);
						}
					}
					else if(iAction == -1)//de equip, this should not happen here tho but Just in case
					{
						iT.setEquipped(dC, -1);
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, -1),Constants.PACKET_RANGE_CHAT_RANGE);
					}
				}

			}

		}catch(Exception e){
			System.out.println("Exception Caught in ZoneClientThread.handleTransferItemArmor " + e);
			e.printStackTrace();
		}
	}

	private void handleTransferItemMisc(long targetID, String[] Parameters) throws IOException {
		/**
		 *  handleTransferItemMisc Target ID:3283062293 <--Item to be Trasnferred / equipped / de equipped
            Parameter 0 :
            Parameter 1 : 684363360 <---Destination ID
            Parameter 2 : 4 <----Action to take: 4 = Equip -1 = De Equip
            Parameter 3 : 0.000000 <---X When coords are used these are the Coords for the item
            Parameter 4 : 0.000000 <---Y
            Parameter 5 : 0.000000 <---Z
		 */
		try{
			long lDestinationID = Long.parseLong(Parameters[1]);
			int iAction = Integer.parseInt(Parameters[2]);
			float x = Float.parseFloat(Parameters[3]);
			float z = Float.parseFloat(Parameters[4]);
			float y = Float.parseFloat(Parameters[5]);
			// iTT = itemToTransfer
			SOEObject iTT = server.getObjectFromAllObjects(targetID);
			if(!iTT.canBePickedUp())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot pickup that item."));
				return;
			}
			if(iTT instanceof Armor && iAction == -1)
			{
				//need to update encumberances
				Armor aiTT = (Armor)iTT;
				player.removeEncumberance(aiTT.getArmorEncumberances());
				int [] iPlayerEncumberances = player.getHamEncumberances();
			}

			if(iTT.getCellID() != 0)
			{
				Cell c = (Cell)server.getObjectFromAllObjects(iTT.getCellID());
				Structure s = c.getBuilding();
				if(!s.isAdmin(player.getID()))
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must be structure admin to pickup items."));
					return;
				} else {
					c.removeCellObject(iTT);
				}
			}

			// dC = destinationContainer
			SOEObject dC = server.getObjectFromAllObjects(lDestinationID);
			//lets find out what this container is
			if(dC instanceof TangibleItem)//means its going ito another item.
			{
				//lets identify the container:
				if(player.getInventory().equals(dC))//the item is going into players inventory
				{

					if(iTT instanceof TangibleItem)
					{
						TangibleItem iT = (TangibleItem)iTT;
						if(iT.getContainer() != null && iT.getContainer() instanceof TangibleItem)
						{
							TangibleItem oC = (TangibleItem)iT.getContainer();
							oC.removeLinkedObject(iT);
						}
						player.addItemToInventory(iT);

						if(iAction == 4)
						{
							player.getInventory().addLinkedObject(iT);
							iT.setEquipped(dC, 4);//equip, this should not happen here tho but Just in case
							client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, 4),Constants.PACKET_RANGE_CHAT_RANGE);
						}
						else if(iAction == -1)//de equip
						{
							player.getInventory().removeLinkedObject(iT);
							iT.setEquipped(dC, -1);
							client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, -1),Constants.PACKET_RANGE_CHAT_RANGE);
							Vector<Player> vPL = server.getPlayersAroundObject(player, false);
							for(int i = 0; i < vPL.size(); i++)
							{
								Player p = vPL.get(i);
								p.despawnItem(iTT);
							}
						}
						if(iTT instanceof Instrument)
						{
							player.unequipInstrument();
						} else if (iTT instanceof Weapon) {
							if (iAction == -1) {
								player.updateEquippedWeapon(null, true);
							} else {
								player.equipWeapon((Weapon)iTT, true);
							}
						}
					}
				}
				else if(player.getBank().equals(dC))//the item is going to the bank
				{

					TangibleItem Bank = (TangibleItem)dC;
					TangibleItem iT = (TangibleItem)iTT;
					player.getInventory().removeLinkedObject(iT);
					player.removeItemFromInventory(iT);
					Bank.addLinkedObject(iT);
					if(iAction == 4)
					{
						iT.setEquipped(dC, 4);//equip, this should not happen here tho but Just in case
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, 4),Constants.PACKET_RANGE_CHAT_RANGE);
						Vector<Player> vPL = server.getPlayersAroundObject(player, false);
						for(int i = 0; i < vPL.size(); i++)
						{
							Player p = vPL.get(i);
							p.spawnItem(iTT);
						}
					}
					else if(iAction == -1)//de equip
					{
						iT.setEquipped(dC, -1);
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, -1),Constants.PACKET_RANGE_CHAT_RANGE);
					}
				}
				else if(dC instanceof TangibleItem)// its going into another type of container either equipped by the player like a back pack or something
				{
					TangibleItem newContainer = (TangibleItem)dC;
					TangibleItem iT = (TangibleItem)iTT;
					if(newContainer.isContainer() && iT.isContainer())
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("That item is too bulky to fit into that container"));
						return;
					}
					if(iT.getContainer().equals(player.getInventory()))
					{
						player.removeItemFromInventory(iT);
						player.getInventory().removeLinkedObject(iT);
					}
					else if(iT.getContainer() instanceof TangibleItem)
					{
						TangibleItem oC = (TangibleItem)iT.getContainer();
						oC.removeLinkedObject(iT);
					}
					newContainer.addLinkedObject(iT);
					if(iAction == 4)
					{
						iT.setEquipped(dC, 4);//equip, this should not happen here tho but Just in case
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, 4),Constants.PACKET_RANGE_CHAT_RANGE);
						Vector<Player> vPL = server.getPlayersAroundObject(player, false);
						for(int i = 0; i < vPL.size(); i++)
						{
							Player p = vPL.get(i);
							p.spawnItem(iTT);
						}
					}
					else if(iAction == -1)//de equip
					{
						iT.setEquipped(dC, -1);
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, -1),Constants.PACKET_RANGE_CHAT_RANGE);
					}
				}
			}
			else if(dC instanceof Player)//looks like we are equipping the item
			{

				if(iTT instanceof TangibleItem)
				{
					TangibleItem iT = (TangibleItem)iTT;
					if(iT.getRaceRestrictions()[0] != -1)
					{
						int [] r = iT.getRaceRestrictions();
						Vector<Integer> vRest = new Vector<Integer>();
						for(int i = 0; i < r.length; i++)
						{
							vRest.add(r[i]);
						}
						if(!vRest.contains(player.getRaceID()))
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot equip that item due to race restrictions."));
							return;
						}
					}
					if(iT.isFemaleItem())
					{
						if(!Constants.SharedRaceModels[player.getRaceID()].contains("female") )
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot equip that item due to gender restrictions."));
							return;
						}
					}
					if(iAction == 4)
					{
						iT.setEquipped(dC, 4);
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, 4),Constants.PACKET_RANGE_CHAT_RANGE);
						Vector<Player> vPL = server.getPlayersAroundObject(player, false);
						for(int i = 0; i < vPL.size(); i++)
						{
							Player p = vPL.get(i);
							p.spawnItem(iTT);
						}
					}
					else if(iAction == -1)//de equip, this should not happen here tho but Just in case
					{
						iT.setEquipped(dC, -1);
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(iTT, dC, -1),Constants.PACKET_RANGE_CHAT_RANGE);
					}
				}
			}
			else if(dC instanceof Cell) //means were dropping in a cell or house
			{

				Cell c = (Cell)dC;
				Structure s = c.getBuilding();
				if(!s.isAdmin(player.getID()) && !account.getIsDeveloper())
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must be structure admin to drop items."));
					return;
				}
				TangibleItem t = (TangibleItem)iTT;
				t.setCellID(c.getID());;
				switch(s.getIFacingDirection())
				{
				case 0:
				{
					t.setX(s.getX() + x);
					t.setY(s.getY() + y);
					t.setZ(s.getZ() + z);
					break;
				}
				case 1:
				{
					t.setX(s.getX() + y);
					t.setY(s.getY() - x);
					t.setZ(s.getZ() + z);
					break;
				}
				case 2:
				{
					t.setX(s.getX() - x);
					t.setY(s.getY() - y);
					t.setZ(s.getZ() + z);               
					break;
				}
				case 3:
				{
					t.setX(s.getX() - y);
					t.setY(s.getY() + x);
					t.setZ(s.getZ() + z);
					break;
				}
				}                        
				t.setCellX(x);
				t.setCellY(y);
				t.setCellZ(z);
				t.setPlanetID(player.getPlanetID());
				t.setRadialCondition(Constants.RADIAL_CONDITION.ITEM_DROPPED_IN_CELL.ordinal());
				player.removeItemFromInventory(t);
				c.addCellObject(t);
				t.setEquipped(c, -1);
				client.insertPacket(PacketFactory.buildUpdateContainmentMessage(t, c, -1));

				Vector<Player> vPL = client.getServer().getPlayersAroundObject(player, true);
				for(int i = 0; i < vPL.size();i++)
				{
					Player p = vPL.get(i);
					if(p.equals(player))
					{ 
						p.despawnItem(t);
					}
					p.spawnItem(t);
					p.getClient().insertPacket(PacketFactory.buildObjectControllerDataTransformWithParentObjectToClient(t,0x21));
				}
				client.getServer().getGUI().getDB().updatePlayer(player,true,false);
			} else {
				System.out.println("Unknown declared instance for object in transferItemMisc.");
			}

		}catch(Exception e){
			System.err.println("Exception Caught in ZoneClientThread.handleTransferItemMisc " + e);
			e.printStackTrace();
		}
	}


	private void spawnPlayerToNearPlayers() {
		Vector<Player> vPlayersInRange = server.getPlayersAroundObject(player, false);
		for (int i = 0; i < vPlayersInRange.size(); i++) {
			try{
				Player recipient = vPlayersInRange.elementAt(i);
				if(!recipient.equals(player))
				{
					recipient.spawnItem(player);
				}
			} catch (IOException e) {
				try {
					vPlayersInRange.elementAt(i).getClient().insertPacket(PacketFactory.buildChatSystemMessage("Error: Unable to spawn player " + player.getFullName() + " -- player will not be visible."));
				} catch (IOException ee) {
					System.out.println("Unable to spawn player to a client, and unable to inform client of issue: ");
					System.out.println(ee.toString());
					ee.printStackTrace();
					System.out.println("Caused by: " + e.toString());
					e.printStackTrace();
				}
			}
		}
	}

	private void handleAddFriendRequest(long targetID, String[] ceq){

		String[] friendParameters = ceq[0].split(".");
		if (friendParameters.length > 1) {
			// Cross-server friend add request.
			String serverName = friendParameters[0];
			String playerName = friendParameters[1];
			Vector<DatabaseServerInfoContainer> vAllServers = DatabaseInterface.getZoneServers(bIsDeveloper);
			boolean bFoundServer = false;
			int iServerID = 0;
			for (int i = 0; i < vAllServers.size() && !bFoundServer; i++) {
				DatabaseServerInfoContainer container = vAllServers.elementAt(i);
				String sClusterName = container.sServerName;
				if (sClusterName.equalsIgnoreCase(serverName)) {
					// Found the server!
					bFoundServer = true;
					iServerID = container.iServerID;
				}
			}
			if (bFoundServer) {
				if (iServerID == server.getServerID()) {
					String[] newParams = new String[1];
					newParams[0] = playerName;
					handleAddFriendRequest(targetID, newParams);
					return;
				} else {
					// We need to find out if the friend name is valid on the other server.
					// To do so, we can ask the transciever to retrieve the name from the remote server.


				}
			} else {
				try {
					client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid server name " + serverName));
				} catch (Exception e) {
					// D'oh!
				}
				return;
			}
		} else {
			Player friendPlayer = client.getServer().getPlayer(ceq[0]);
			if(friendPlayer != null)
			{
				String Cluster, FriendName;
				Cluster = friendPlayer.getPlayerCluster();
				FriendName = friendPlayer.getFirstName();
				if(Cluster == null)
				{
					System.out.println("Cluster was null");
				}
				if(FriendName == null)
				{
					System.out.println("FriendName was null");
				}
				PlayerFriends PF = new PlayerFriends(FriendName,Cluster);
				boolean exists = false;
				Iterator<PlayerFriends> itr = client.getPlayer().getFriendsList().iterator();
				while( itr.hasNext() && !exists)
				{
					if(itr.next().getName().compareToIgnoreCase(PF.getName()) == 0)
					{
						exists = true;
					}
				}
				if(exists)
				{
					try{
						client.insertPacket(PacketFactory.buildChatSystemMessage(PF.getName() + " is already your friend."));
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
				else
				{
					if(client.getPlayer().getFriendsList().add(PF))
					{
						try{
							client.insertPacket(PacketFactory.buildChatSystemMessage(PF.getName() + " is now your friend."));
							client.insertPacket(PacketFactory.buildFriendsListResponse(client.getPlayer()));
							client.insertPacket(PacketFactory.buildFriendsListUpdateDelta(client.getPlayer()));
							client.insertPacket(PacketFactory.buildFriendOfflineStatusUpdate(client.getPlayer(), friendPlayer));
							if(friendPlayer.getStatus())
							{
								client.insertPacket(PacketFactory.buildFriendOnlineStatusUpdate(client.getPlayer(), friendPlayer));
							}
							return;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						try{
							client.insertPacket(PacketFactory.buildChatSystemMessage("Error while inserting friend in friends list."));
						} catch (IOException e) {
							e.printStackTrace();
						}
						return;
					}
				}
			}
			else
			{
				try{
					client.insertPacket(PacketFactory.buildChatSystemMessage("That is not a valid player name."));
				}catch (IOException e){
					System.out.println("Exception Caught: handleAddFriendRequest :" + e.toString());
					e.printStackTrace();
				}
			}
		}
	}


	private void handleRemoveFriendRequest(long targetID, String[] ceq){
		String FriendToRemove = ceq[0];
		try {
			Vector<PlayerFriends> itr = client.getPlayer().getFriendsList();
			if (!itr.isEmpty()) {
				for (int i = 0; i < itr.size(); i++) {
					PlayerFriends F = itr.elementAt(i);
					if(F.getName().compareToIgnoreCase(FriendToRemove)==0)
					{
						itr.remove(i);
					}
				}
			}
			client.insertPacket(PacketFactory.buildFriendsListResponse(client.getPlayer()));
			client.insertPacket(PacketFactory.buildFriendsListUpdateDelta(client.getPlayer()));
			client.insertPacket(PacketFactory.buildChatSystemMessage(FriendToRemove + " is no longer your friend."));
		} catch (IOException e) {
			System.out.println("Error removing friend from friends list: " + e.toString());
			e.printStackTrace();
		}
	}

	private void handleSitRequest(CommandQueueItem action, long targetID, String[] sParams) throws IOException{

		if (targetID != 0) {
			SOEObject targetObject = server.getObjectFromAllObjects(targetID);
			float targetX = targetObject.getX();
			float targetY = targetObject.getY();
			float targetZ = targetObject.getZ();
			if (!ZoneServer.isInRange(player, targetObject, 3f)) {
				action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_OUT_OF_RANGE);
				return;
			}
			if (player.setStance(action, Constants.STANCE_SITTING, false)) {
				long cellID = targetObject.getCellID();
				if (cellID != 0) {
					player.updateInteriorPosition(player.getMoveUpdateCount() + 1,
							targetObject.getOrientationN(),
							targetObject.getOrientationS(),
							targetObject.getOrientationE(),
							targetObject.getOrientationW(),
							targetX,
							targetZ,
							targetY,
							0.0f,
							cellID);
				} else {
					player.updatePosition(player.getMoveUpdateCount() + 1,
							targetObject.getOrientationN(),
							targetObject.getOrientationS(),
							targetObject.getOrientationE(),
							targetObject.getOrientationW(),
							targetX,
							targetZ,
							targetY,
							0.0f,
							0);
				}
				player.addState(Constants.STATE_SEATED, -1l);
			}			
		} else {
			if (player.setStance(action, Constants.STANCE_SITTING, false)) {

			
				if(sParams.length >= 1) {
					//params seem to always be 0:
					//              x     z      y    object id
					//Param 0 : -15.9142,1.6,-18.4726,1865368
					//System.out.println("Sit With Params Request.");
					String [] sSitParams = sParams[0].split(",");
					if(!sSitParams[0].isEmpty())
					{
						float x,y,z;
						x = Float.parseFloat(sSitParams[0]);
						z = Float.parseFloat(sSitParams[1]);
						y = Float.parseFloat(sSitParams[2]);
						// long lCellID = Long.parseLong(sSitParams[3]); //not needed
						player.addState(Constants.STATE_SEATED, -1l);
						client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)4, (short)1, (short)9, player, 0x3C4CCCCD)); // -- HARD CODING???????
						client.insertPacket(PacketFactory.buildWorldUpdateMessage(0x6DBF9E2F, 0)); // TODO -- WTF is this?  Hard-coding?  
		
						if(player.getCellID() == 0)
						{
							player.setCellX(x);
							player.setCellY(y);
							player.setCellZ(z);
							client.insertPacket(PacketFactory.buildObjectControllerDataTransformObjectToClient(player, 0xF1),Constants.PACKET_RANGE_CHAT_RANGE);
						}
						else
						{
							player.setX(x);
							player.setY(y);
							player.setZ(z);
							client.insertPacket(PacketFactory.buildObjectControllerDataTransformWithParentObjectToClient(player, 0xF1),Constants.PACKET_RANGE_CHAT_RANGE);
		
						}
						client.insertPacket(PacketFactory.buildObjectControllerSitOnObject(player),Constants.PACKET_RANGE_CHAT_RANGE);
					}
					else
					{
						//client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)4, (short)1, (short)9, player, 0));
						//player.addState(Constants.STATE_SEATED);
						player.setStance(action, Constants.STANCE_SITTING, false);
					}
				}
			}
		}
	}

	private void handleStandRequest(CommandQueueItem action, long targetID, String[] paramaters) throws IOException {
		try{
			player.removeState(Constants.STATE_SEATED);
			if (player.setStance(action, Constants.STANCE_STANDING, false)) {
				client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)4, (short)1, (short)9, player, 0));
				player.setMaxVelocity(Constants.MAX_PLAYER_RUN_SPEED_METERS_SEC);
			}
		}catch(Exception e){
			DataLog.logException("Exception in handleStandrequest", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	private void handleKneelRequest(CommandQueueItem action, long targetID, String[] paramaters) throws IOException {
		
		if (player.setStance(action, Constants.STANCE_KNEELING, false)) {
			player.removeState(Constants.STATE_SEATED);
		}
	}

	private void handleProneRequest(CommandQueueItem action, long targetID, String[] paramaters) throws IOException {
		if (player.setStance(action, Constants.STANCE_PRONE, false)) {
			player.removeState(Constants.STATE_SEATED);
			player.setMaxVelocity(Constants.MAX_CRAWL_SPEED_METERS_SEC);
		}
	}
	private void handleClientSendEmailRequest(SOEInputStream dIn) throws IOException {
		/*
                        00 09
                        00 08
                        06 00
                        A6 9F A2 25 //0x25A29FA6 <-- when we get here we have already removed this from the packet
		 * here is where we start
                        11 00 00 00 74 00 68 00 69 00 73 00 20 00 69 00 73 00 20 00 61 00 20 00 74 00 65 00 73 00 74 00 20 00 31 00 0A 00 //Ustring Message Body
                        00 00 00 00 //attachment count
                        01 00 00 00 //email sequence
                        06 00 00 00 74 00 65 00 73 00 74 00 20 00 31 00 //Ustring subject
                        00 00 00 00 //separator
                        03 00 72 65 6F //recipient
                        05 8F
                         PrintPacketToScreen
                        ClientSendEmailRequest NO ATTACHMENT
                        ------------------------------------------------
                        07 00 00 00 6D 00 65 00 73 00 73 00 61 00 67 00 65 00 //Message
                        00 00 00 00 //Attachments size in bytes
                        03 00 00 00 //email sequence
                        07 00 00 00 73 00 75 00 62 00 6A 00 65 00 63 00 74 00 //Subject
                        00 00 00 00 //
                        09 00 72 65 63 69 70 69 65 6E 74 //Recipient
                        38 47 //crc
                        ------------------------------------------------
                        PrintPacketToScreen
                        ClientSendEmailRequest WITH ONE ATTACHMENT
                        ------------------------------------------------
                        07 00 00 00 6D 00 65 00 73 00 73 00 61 00 67 00 65 00 //message
                        2A 00 00 00 //attachments 2AHEX = 42DEC = 1 Attachment //50 bytes constant per attachment + name character bytes * 2
                         01 00 //short
                         04 //byte
                         FD FF FF FF 00 00 00 00 //long
                         00 30 5C 45 //x
                         00 00 80 40 //y
                         00 00 96 C5 //z
                         00 00 00 00 00 00 00 00 //long
                         21 91 27 57 //planet crc
                         11 00 00 00 53 00 74 00 61 00 72 00 74 00 69 00 6E 00 67 00 20 00 4C 00 6F 00 63 00 61 00 74 00 69 00 6F 00 6E 00 //wp name
                         00 00 00 00 00 00 00 00 //long
                         01 00 //short
                         00 //byte
                         04 00 00 00 //email sequence
                         07 00 00 00 73 00 75 00 62 00 6A 00 65 00 63 00 74 00 //subject
                         00 00 00 00 //spacer
                         09 00 72 65 63 69 70 69 65 6E 74 //recipient
                         E6 04 //crc
                        ------------------------------------------------

		 * PrintPacketToScreen
                        ClientSendEmailRequest WITH 2 ATTACHMENTS
                        ------------------------------------------------
                        07 00 00 00 6D 00 65 00 73 00 73 00 61 00 67 00 65 00
                         54 00 00 00 //
                         01 00
                         04
                         FD FF FF FF 00 00 00 00
                         00 30 5C 45
                         00 00 80 40
                         00 00 96 C5
                         00 00 00 00 00 00 00 00
                         21 91 27 57
                         11 00 00 00 53 00 74 00 61 00 72 00 74 00 69 00 6E 00 67 00 20 00 4C 00 6F 00 63 00 61 00 74 00 69 00 6F 00 6E 00
                         00 00 00 00 00 00 00 00
                         01 00
                         00
                         01 00
                         04
                         FD FF FF FF 00 00 00 00
                         00 30 5C 45
                         00 00 80 40
                         00 00 96 C5
                         00 00 00 00 00 00 00 00
                         21 91 27 57
                         11 00 00 00 53 00 74 00 61 00 72 00 74 00 69 00 6E 00 67 00 20 00 4C 00 6F 00 63 00 61 00 74 00 69 00 6F 00 6E 00
                         00 00 00 00 00 00 00 00
                         01 00
                         00
                         05 00 00 00
                         07 00 00 00 73 00 75 00 62 00 6A 00 65 00 63 00 74 00
                         00 00 00 00
                         09 00 72 65 63 69 70 69 65 6E 74
                         DF E4
                        ------------------------------------------------


		 */
		// boolean f = true;
		// this.diagPrintPacketToScreen(dIn, "ClientSendEmailRequest");
		// if(f)
		// {  return;}
		try {
			//System.out.println("Handling ClientSendEmailRequest.");
			String Message;
			Message = dIn.readUTF16();
			int AttachmentSize;
			AttachmentSize = dIn.readInt();//this is the total size of all attachments in this email
			int AttachmentCount = AttachmentSize / 42;
			Vector<Waypoint> WL = new Vector<Waypoint>();
			//System.out.println("Attachment Size:" + AttachmentSize);

			if(AttachmentSize >= 1)
			{
				//read attachments
				//  System.out.println("Email Has Attachments. Count: " + AttachmentCount);
				for(int i = 0; i < AttachmentCount;i++)
				{
					//process attachments in packet
					Waypoint W = new Waypoint();
					/*
                                 01 00 //short
                                 04 //byte
                                 FD FF FF FF 00 00 00 00 //long
                                 00 30 5C 45 //x float
                                 00 00 80 40 //y float
                                 00 00 96 C5 //z float
                                 00 00 00 00 00 00 00 00 //long
                                 21 91 27 57 //planet crc int
                                 11 00 00 00 53 00 74 00 61 00 72 00 74 00 69 00 6E 00 67 00 20 00 4C 00 6F 00 63 00 61 00 74 00 69 00 6F 00 6E 00 //wp name ustring
                                 00 00 00 00 00 00 00 00 //long
                                 01 00 //short
                                 00 //byte
					 * */
					dIn.readShort();
					dIn.readByte();
					dIn.readLong();
					W.setX(dIn.readFloat());
					W.setY(dIn.readFloat());
					W.setZ(dIn.readFloat());
					dIn.readLong();
					W.setPlanetCRC(dIn.readInt());
					W.setName(dIn.readUTF16());
					dIn.readLong();
					dIn.readShort();
					dIn.readByte();
					W.setIsActivated(false);
					WL.add(W);
				}
			}
			client.setEmailSequence(dIn.readInt());
			String Subject = dIn.readUTF16();
			dIn.readInt();
			String Recipient;
			Recipient = dIn.readUTF();
			//System.out.println("Received Recipient String: " + Recipient);
			//SWGEmail(int objectID, long sender, long receiver, String header, String body, Vector<Waypoint> attachments, long creationTime,boolean readFlag)
			//New email when sent by a player has to be set to id -1 so we know we need to assign a new id when inserted into the server.
			// System.out.println("Email Message Debug:");
			// System.out.println("Recipient: " + Recipient);


			long recipID = client.getServer().getPlayer(Recipient).getID();
			long senderID = client.getPlayer().getID();
			//System.out.println("Sender ID: " + " " + client.getPlayer().getID() + client.getPlayer().getFullName());
			//System.out.println("Recipient ID: " + recipID + " " + client.getServer().getPlayerByName(Recipient).getFullName());
			//System.out.println("Subject: " + Subject);
			//System.out.println("Message: " + Message);
			//System.out.println("WP List Size: " + WL.size());
			//System.out.println("Current Time: " + System.currentTimeMillis());

			SWGEmail E = new SWGEmail(-1,senderID,recipID,Subject,Message,WL,false);
			E.setTransactionRequester(client);
			client.getServer().queueEmailNewClientMessage(E);
			return;
		}
		catch (IOException e) {
			System.out.println("IOException in ClientSendEmailRequest: " + e.toString());
			e.printStackTrace();
			client.insertPacket(PacketFactory.buildChatSystemMessage("Unable to Send Email to Recipient."));
		}
		catch (Exception e){
			System.out.println("Exception in ClientSendEmailRequest: " + e.toString());
			e.printStackTrace();
			client.insertPacket(PacketFactory.buildChatSystemMessage("Unable to Send Email to Recipient."));
		}
	}
	private void handleChatInstantMessageToCharacter(SOEInputStream dIn) throws IOException {
		//System.out.println("Handling chat instant message to character.");

		// When we start supporting multiple servers, we will have to go looking for clients on "other" zone servers,
		// if the clusterName does not match our server's name.
		// In the meantime, if the cluster name doesn't match, just send a failed and return.
		/*String clusterName =*/ dIn.readUTF();
		String serverName = dIn.readUTF();
		if (!serverName.equalsIgnoreCase(client.getServer().getClusterName())) {
			client.insertPacket(PacketFactory.buildChatOnSendInstantMessage(player, Constants.CHAT_CODE_FAILED_OFFLINE));
			return;
		}

		String recipName = dIn.readUTF();
		String message = dIn.readUTF16();
		/*int unknownInt =*/ dIn.readInt();
		int tellCounter = dIn.readInt();
		//System.out.println("ServerName: " + serverName + ", target: " + recipName + "\nMessage: " + message + "\nTell Count: " + tellCounter);
		player.setTellCount(tellCounter);
		Player recipPlayer = null;

		if (recipName.equalsIgnoreCase(player.getFirstName())) {
			recipPlayer = player;
		} else {
			recipPlayer = server.getPlayer(recipName);
		}

		if (recipPlayer == null) {
			// Send a message back to this client indicating that the requested player does not exist.
			client.insertPacket(PacketFactory.buildChatSystemMessage(recipName + " is not a valid player name."));
			client.insertPacket(PacketFactory.buildChatOnSendInstantMessage(player, Constants.CHAT_CODE_FAILED_OFFLINE));
		} else {
			ZoneClient pClient = recipPlayer.getClient();
			if (pClient == null || (!pClient.getValidSession())) {
				// Send a message back to this client, indicating that the requested player is not on-line.
				//client.insertPacket(PacketFactory.buildChatSystemMessage(recipName + " is not on-line."));
				client.insertPacket(PacketFactory.buildChatOnSendInstantMessage(player, Constants.CHAT_CODE_FAILED_OFFLINE));
			} else {
				//byte[] returnedPacket = PacketFactory.buildChatInstantMessageToClient(player.getFirstName(), message, serverName);
				//System.out.println("Tell success.  Printing packets...");
				//PacketUtils.printPacketData(returnedPacket);
				//pClient.insertPacket(returnedPacket);
				//returnedPacket = PacketFactory.buildChatOnSendInstantMessage(player, Constants.CHAT_CODE_SUCCESS);
				//PacketUtils.printPacketData(returnedPacket);
				//client.insertPacket(returnedPacket);

				pClient.insertPacket(PacketFactory.buildChatInstantMessageToClient(player.getFirstName(), message, serverName));
				client.insertPacket(PacketFactory.buildChatOnSendInstantMessage(player, Constants.CHAT_CODE_SUCCESS));


				// Send the tell to the requested player, and send a tell sent to this client.
			}
		}
	}

	private void handleSetMoodInternal(long targetID, byte moodID) throws IOException {

		String sNewMood = Constants.vMoodStrings[moodID];
		System.out.println("handleSetMoodInternal:  Mood " + sNewMood + ", ID " + moodID);
		player.setMoodID(moodID);
		player.setMoodString(sNewMood);
		Vector<Player> vPlayersInRange = server.getPlayersAroundObject(player, true);
		for (int i = 0; i < vPlayersInRange.size(); i++) {
			ZoneClient theClient = vPlayersInRange.elementAt(i).getClient();
			theClient.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6,  (short)1, (short)4, player, sNewMood, false));
			theClient.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6,  (short)1, (short)0x0A, player, moodID));
		}
		//client.insertPacket(PacketFactory.buildCommandQueueDequeue(player, new CommandQueueItem(0, 0, 0, 0, 0, player)));
	}

	private void handleSetMoodInternal(long targetID, String[] paramaters) throws IOException {
		handleSetMoodInternal(targetID, Byte.parseByte(paramaters[0]));
	}

	private void handleSetMood(long targetID, String[] paramaters) throws IOException{

		String sNewMood = paramaters[0];
		for (int i = 0; i < Constants.vMoodStrings.length; i++) {
			if (sNewMood.equals(Constants.vMoodStrings[i])) {
				//parameters[0] = Integer.toString(i);
				handleSetMoodInternal(targetID, (byte)i);
				return;
			}
		}
	}

	private void handleDisconnect(SOEInputStream sIn) throws IOException{
		if (player == null) {
			// Never selected a player... just terminate the thread.
			terminate();
			return;
		}
		if (client == null) {
			server.getGUI().getDB().updatePlayer(player, false,false);
			terminate();
			return;
		}
		int sessionID = sIn.readInt();
		if (client.getSessionID() == sessionID) {
			if (player != null) {
				player.setOnlineStatus(false);
				if(player.getGroupID()!=0)
				{
					Group g = (Group)server.getObjectFromAllObjects(player.getGroupID());
					g.removeMemberFromGroup(player,Constants.GROUP_REMOVE_REASON_LEAVE);
				}
				server.getGUI().getDB().updatePlayer(player, false,false);
			}
			server.removeClientByAddress(client.getClientAddress());
			server.removeFromTree(player);
			//System.out.println("Received disconnect packet -- client = null");
			client.setValidSession(false);
			//remove our emails from the servers email trackin list
			client.getServer().queueEmailClientRequestEmails(client);
			//remove any camps from the world
			if(player.getCurrentCampObject() != null)
			{
				player.getCurrentCampObject().disbandCamp(server, 0, true);
			}
			//remove any pets from the world
			if(player.getCalledPets().size() >= 1)
			{
				Vector<CreaturePet> vCP = player.getCalledPets();
				for(int i = 0; i < vCP.size(); i++)
				{
					CreaturePet pet = vCP.get(i);
					//pet.getDatapadControlIcon().useItem(client,(byte)59);
					pet.getDatapadControlIcon().setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());                    
					server.removeObjectFromAllObjects(pet, false);
				}
			}
			//inform everyone who has us in their friends list were off line
			ConcurrentHashMap<SocketAddress,ZoneClient> vAllPlayers = client.getServer().getAllClients();
			Enumeration<ZoneClient> vAllClients = vAllPlayers.elements();
			while(vAllClients.hasMoreElements())
			{
				ZoneClient fClient = vAllClients.nextElement();
				Player thePlayer = fClient.getPlayer();
				if(thePlayer != null) {
					if (thePlayer.getStatus() && fClient != client) {
						fClient.insertPacket(PacketFactory.buildFriendOfflineStatusUpdate(thePlayer, client.getPlayer()));
					}
				}
			}

			Vector<Player> vNearbyPlayers = server.getPlayersAroundObject(player, false);
			for (int i = 0; i < vNearbyPlayers.size(); i++) {
				Player thePlayer = vNearbyPlayers.elementAt(i);
				ZoneClient client = vNearbyPlayers.elementAt(i).getClient();
				if (client.getValidSession()) {
					thePlayer.despawnItem(player);
					if(player.getCurrentMount()!=0)
					{
						SOEObject m = server.getObjectFromAllObjects(player.getCurrentMount());
						if(m!=null)
						{
							thePlayer.despawnItem(m);
						}
					}

					/**
					 * @todo if the player has spawned pets we need to despawn them too
					 */
				}
			}
			if(player.getCurrentMount()!=0)
			{
				server.removeObjectFromAllObjects(player.getCurrentMount());
			}
			if(player.getTutorial() != null)
			{
				if(player.getPlanetID() == Constants.TUTORIAL)
				{
					player.getTutorial().setHasEnteredTutorial(false);
				}
			}
		} else {
			System.out.println("Received invalid session ID on disconnect packet.  Got " + Integer.toHexString(sessionID) + ", expected " + client.getSessionID() + ", not terminating client.");
		}
		player.clearSpawnedItems();
		player.resetTimedEvents();
		terminate();
	}

	protected void handleRequestBadges(long targetID, String[] paramaters) throws IOException{
		PlayerItem player = client.getPlayer().getPlayData();
		int[] badgeBits = player.getBadges();
		client.insertPacket(PacketFactory.buildBadgeResponseMessage(targetID, badgeBits));
	}

	protected void handleRequestBiography(long targetID, String[] paramaters) throws IOException{
		Player targetPlayer = client.getServer().getPlayer(targetID);
		String sBiography = targetPlayer.getBiography();
		client.insertPacket(PacketFactory.buildBiographyResponse(player.getID(), targetID, sBiography));

	}

	protected void handleRequestCharacterSheetInfo(long targetID, String[] paramaters) throws IOException{
		/*if (targetID != player.getID()) {

			System.out.println("Error:  Requesting character sheet data for target other than ourselves!");
			System.out.println("Target ID: " + targetID + ", Player ID: " + player.getID());
		} */
		client.insertPacket(PacketFactory.buildCharacterSheetResponseMessage(player));

	}

	protected void handleRequestStatMigrationData(long targetID, String[] paramaters) throws IOException {
		/*if (targetID != player.getID()) {
			System.out.println("Error:  Requesting stat migration data for target other than ourselves!");
			System.out.println("Target ID: " + targetID + ", Player ID: " + player.getID());
		} */
		client.insertPacket(PacketFactory.buildStatMigrationDataResponse(player));

	}

	protected void handleNPCConversationSelect(long targetID, String[] parameters) throws IOException {
		//System.out.println("handleNPCConversationSelect Target ID: " + targetID);
		//System.out.println("Parameters Passed:");
		//for(int i = 0; i < parameters.length; i++)
		//{
		//    System.out.println("Param: " + i + " - " + parameters[i]);
		//}
		if(targetID == 0)
		{
			targetID = player.getLastConversationNPC();
		}
		SOEObject o = player.getServer().getObjectFromAllObjects(targetID);
		if(o instanceof Terminal)
		{
			Terminal T = (Terminal)o;
			switch(T.getTerminalType())
			{
			case Constants.TERMINAL_TYPES_SKILL_TRAINER:
			{
				T.handleSkillTrainerConversationResponse(client, parameters);
				break;
			}
			case Constants.TERMINAL_TYPES_TUTORIAL_GREET_OFFICER:
			{
				T.handleTutorialConversationResponse(client, parameters);
				break;
			}
			}

		}
		else if(o instanceof NPC){
			System.out.println("handleNPCConversationSelect Conversation not handled for NPC Type ID:" + o.getID());
		}
		else
		{
			System.out.println("handleNPCConversationSelect Not Handled for Class:" + o.getClass() + " NPC ID:" + o.getID());
		}
	}

	protected void handleMapLocationRequestMessage(SOEInputStream dIn) {
		String sPlanet = null;
		try {
			sPlanet = dIn.readUTF();
			dIn.close();
			dIn = null;
			Vector<MapLocationData> vStaticLocations = server.getStaticMapLocations(sPlanet);
			Vector<MapLocationData> vPlayerMadeLocations = server.getPlayerMapLocations(sPlanet);
			client.insertPacket(PacketFactory.buildGetMapLocationsResponseMessage(sPlanet, vStaticLocations, vPlayerMadeLocations));
		} catch (IOException e) {
			System.out.println("Error handling MapLocationRequestMessage -- returning blank list.");
			try {
				client.insertPacket(PacketFactory.buildGetMapLocationsResponseMessage(Constants.PlanetNames[player.getPlanetID()], null, null));
			} catch (IOException ee) {
				// We're really screwed -- ignore this message.
				return;
			}
		}


	}

	protected void handlePlanetTravelListRequest(SOEInputStream dIn){

		try{

			//PacketUtils.printPacketToScreen(dIn.getBuffer(), dIn.available(), "handlePlanetTravelListRequest");



			//inbound packet data
			/*
			 * handleObjectUpdate,PlanetTravelListRequest
                    -----> Print Packet To Screen. <-----
                    -----> handlePlanetTravelListRequest <-----
                    -----> Len:23 <-----
                    <------------------------------------------------>
                    03 00
                    4D 5D 40 96
                    E9 0F A0 E4 00 00 00 00
                    06 00 79 61 76 69 6E 34
                    <------------------------------------------------>


			 *
			 * */
			//short upd = dIn.readShort();
			//int uType = dIn.readInt();
			long PlayerID = dIn.readLong();
			String Planet = dIn.readUTF();
			int PlanetID = -1;
			System.out.println("--- PID: " + PlayerID + " Planet: " + Planet);

			for(int i = 0; i < Constants.TravelPlanetNames.length; i++)
			{
				if(Constants.TravelPlanetNames[i].compareToIgnoreCase(Planet) == 0)
				{
					PlanetID = i;
					break;
				}
			}

			if(PlanetID != -1)
			{
				client.insertPacket(PacketFactory.buildTravelPointListResponse(client.getPlayer(), PlanetID));
			}
			else
			{
				System.out.println("handlePlanetTravelListRequest: Planet requested Not Matched or Var set at -1 " + Planet);
			}


		}catch(Exception e) {
			System.out.println("Exception in handlePlanetTravelListRequest: " + e);
			e.printStackTrace();
		}

	}
	//

	protected void handleSpawnFromIntangibleRequest(ZoneClient client, IntangibleObject swoop) {
		//deprecated
	}

	protected void handleStoreVehicleRequest(NPC n) {

	}

	protected void handleStorePetRequest(NPC n){
		if (n.isMounted()) {
			// Dismount the pet.
			player.removeState(Constants.STATE_MOUNTED_CREATURE);
			try {
				client.insertPacket(PacketFactory.buildUpdateContainmentMessage(player, n, -1));
				//client.insertPacket(PacketFactory.buildDeltasMessageGroupInvite(Constants.BASELINES_CREO, (byte)3, 1, ))
			} catch (Exception e) {
				// D'oh!
			}
		}
		n.setIsSpawned(false);
		server.removeFromTree(n);
	}

	protected void handleSuiEventNotification(SOEInputStream dIn){
		/*
		 * This decode seems to vary by window type.
		 *  00 09 00 08
                04 00
                64 35 2D 09 //public final static int SuiEventNotification	= 0x092D3564;
		 * We should be here at this point int the reading of the stream
                01 00 00 00 //Last window ID
                00 00 00 00 //<-- OK/CANCEL VALUE:  1 if cancel 0 if ok
                02 00 00 00 //Unk
                02 00 00 00 //Items Count

                //Item 1 Selected Item ID as UTF16 String
                01 00 00 00 30 00

                //Item 2 Selected Item Text In Window as UTF16 String.
                1D 00 00 00 40 00 74 00 72 00 61 00 76 00 65 00
                6C 00 3A 00 74 00 69 00 63 00 6B 00 65 00 74 00
                5F 00 63 00 6F 00 6C 00 6C 00 65 00 63 00 74 00
                6F 00 72 00 5F 00 6E 00 61 00 6D 00 65 00

                D5 A5
                <------------------------------------------------>

		 *
		 * */
		int suiWindowID;
		int Unk1;
		int iItemCount;
		String sSelectionIDString;
		String suiWindowTitleString;
		
		int PlayerLastSUIWindow;
		int iSelectedItemIndex = -1;
		sSelectionIDString = "";
		suiWindowTitleString = "";
		int cancel = -1;
		try{

			suiWindowID = dIn.readInt();
			cancel = dIn.readInt();//bypass                    
			
			
			Unk1 = dIn.readInt();
			iItemCount = dIn.readInt();
			//if the sui window was a simple popup we dont have the rest of this
			//add more strings as needed

			String [] sReceivedStrings = new String[iItemCount];
			for(int i = 0; i < iItemCount; i++ )
			{
				sReceivedStrings[i] = dIn.readUTF16();
			}

			if(iItemCount == 1)
			{
				sSelectionIDString = sReceivedStrings[0];
			}
			else if(iItemCount == 2)
			{
				sSelectionIDString = sReceivedStrings[0];
				suiWindowTitleString = sReceivedStrings[1];
			}
			//System.out.println("Received values.  ItemCount("+iItemCount+") Selection ID(" + sReceivedStrings[0] + ") Window Title{"+suiWindowTitleString+")");
			//System.out.println("Window ID: " + suiWindowID);
			SUIWindow window = player.getPendingSUIWindow(suiWindowID);
			PlayerLastSUIWindow = player.getCurrentSUIBoxID();
			if (cancel == -1) {
				// We are in fact cancelling out.
				player.removePendingSUIWindow(PlayerLastSUIWindow);
				return;
			}
			//System.out.println("handleSuiEventNotification: SUIWindowID: " + suiWindowID + " Cancel: " + cancel + " Unk 1:" + Unk1 + " ItemCount:" + iItemCount + " SelectionID:" + sSelectionIDString + " SUIWindowTitleString:" + suiWindowTitleString );
			//---------------------------------------------------------
			if(PlayerLastSUIWindow == suiWindowID)
			{
				switch (window.getWindowType()) {
					case Constants.SUI_FROG_TEACH_SKILL: {
						
						handleSUIEventNotification_SelectSkillToTeach(window, Integer.parseInt(sSelectionIDString));
						break;
					
					}
					case Constants.SUI_TRAVEL_SELECT_TICKET: {
						//this was a travel ticket selection window so lets get the ticket
						iSelectedItemIndex = Integer.parseInt(sSelectionIDString);
						if(iSelectedItemIndex == -1)
						{
							throw new Exception("Invalid Argument Parsed in handleSuiEventNotification");
						}
						TangibleItem t = (TangibleItem)player.getSUIListWindowObjectListItem(iSelectedItemIndex);
						//lets check if what we have is a travel ticket
						if(t instanceof TravelTicket)
						{
							player.playerTravel((TravelTicket)t);
	
							//spawnPlayerToNearPlayers();
							player.removePendingSUIWindow(suiWindowID);
							return;
						}
						break;
					}
					case Constants.SUI_FROG_CHARACTER_BUILDER: {
						iSelectedItemIndex = Integer.parseInt(sSelectionIDString);
						if(window!=null)
						{
							SOEObject o = window.getOriginatingObject();
							if(o instanceof Terminal)
							{
								System.out.println("Object Script Name: " + o.getScriptName());
								switch(iSelectedItemIndex)
								{
								case 0: //sList[0] = "Skills - Skills for your Character";
								{
									System.out.println("Character Builder:Sending Profession Selection Box");
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_SELECT_PROFESSION);
									W2.setOriginatingObject(o);
									String[] sList = new String[7];
	
									sList[0] = "@skl_n:crafting_artisan";
									sList[1] = "@skl_n:combat_brawler";
									sList[2] = "@skl_n:social_entertainer";
									sList[3] = "@skl_n:combat_marksman";
									sList[4] = "@skl_n:science_medic";
									sList[5] = "@skl_n:outdoors_scout";
									sList[6] = "@skl_n:social_politician"; //"@skl_t:social_politician";
									//--------------------------------------
	
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Select A Profession";
									String DataListPrompt = "Select a Profession to add skills to your Character";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
									break;
								}
								case 1: //sList[1] = "Weapons - Get the Fire Power You Need!";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_SELECT_WEAPON);
									W2.setOriginatingObject(o);
									W2.setOriginatingObject(player);
									Vector<String> vDT = DatabaseInterface.getWeaponSet();
									//Vector<SOEObject> oL = new Vector<SOEObject>();
									String [] sList = new String [vDT.size()];
									for(int i = 0; i < vDT.size(); i++)
									{
										//DeedTemplate t = vDT.get(i);
										sList[i] = vDT.get(i);//"@" + server.getTemplateData(t.getObject_template_id()).getSTFFileName() + ":" + server.getTemplateData(t.getObject_template_id()).getSTFFileIdentifier();
									}
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder Weapons Category";
									String DataListPrompt = "Select A Weapon Category.";
									client.insertPacket(W2.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
									break;
								}
								case 2: //sList[2] = "Resources - Make any item with these materials";
								{
									break;
								}
								case 3: //sList[3] = "Deeds - Houses, Droids, Vehicles";
								{
									//here we place deeds for players to use.
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_SELECT_DEED);
									W2.setOriginatingObject(o);
									W2.setOriginatingObject(player);
									Vector<DeedTemplate> vDT = DatabaseInterface.getBlueFrogDeedTemplates();
									//Vector<SOEObject> oL = new Vector<SOEObject>();
									String [] sList = new String [vDT.size()];
									for(int i = 0; i < vDT.size(); i++)
									{
										DeedTemplate t = vDT.get(i);
										sList[i] = "@" + server.getTemplateData(t.getObject_template_id()).getSTFFileName() + ":" + server.getTemplateData(t.getObject_template_id()).getSTFFileIdentifier();
										System.out.println("String:" + sList[i] + " Pos:" + i + " TID:" + t.getTemplateid());
										//SOEObject oD = new SOEObject();
										//oD.setID(t.getTemplateid());
										//oL.add(oD);
									}
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder Deeds";
									String DataListPrompt = "Select A Deed Item to place in your inventory.";
									client.insertPacket(W2.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
									break;
								}
								case 4: //sList[4] = "Wearables - New Threads for a night out";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_SELECT_WEARABLES_CAT);
									W2.setOriginatingObject(o);
									W2.setOriginatingObject(player);
									Vector<String> vDT = DatabaseInterface.getWearablesSets();
									//Vector<SOEObject> oL = new Vector<SOEObject>();
									String [] sList = new String [vDT.size()];
									for(int i = 0; i < vDT.size(); i++)
									{
										//DeedTemplate t = vDT.get(i);
										sList[i] = vDT.get(i);//"@" + server.getTemplateData(t.getObject_template_id()).getSTFFileName() + ":" + server.getTemplateData(t.getObject_template_id()).getSTFFileIdentifier();
										//System.out.println("String:" + sList[i] + " Pos:" + i + " TID:" + t.getTemplateid());
										//SOEObject oD = new SOEObject();
										//oD.setID(t.getTemplateid());
										//oL.add(oD);
									}
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder Wearables Category";
									String DataListPrompt = "Select A Wearables Category.";
									client.insertPacket(W2.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
									break;
								}
								case 5: //sList[5] = "Armor - Dont go on a party unprotected";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_SELECT_ARMOR);
									W2.setOriginatingObject(o);
									W2.setOriginatingObject(player);
									Vector<String> vDT = DatabaseInterface.getArmorSet();
									//Vector<SOEObject> oL = new Vector<SOEObject>();
									String [] sList = new String [vDT.size()];
									for(int i = 0; i < vDT.size(); i++)
									{
										//DeedTemplate t = vDT.get(i);
										sList[i] = vDT.get(i);//"@" + server.getTemplateData(t.getObject_template_id()).getSTFFileName() + ":" + server.getTemplateData(t.getObject_template_id()).getSTFFileIdentifier();
										// System.out.println("String:" + sList[i] + " Pos:" + i + " TID:" + t.getTemplateid());
										//SOEObject oD = new SOEObject();
										//oD.setID(t.getTemplateid());
										//oL.add(oD);
									}
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder Armor Category";
									String DataListPrompt = "Select An Armor Category.";
									client.insertPacket(W2.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
									break;
								}
								case 6: //sList[6] = "XP - Experience To Train";
								{
									System.out.println("Character Builder:Sending XP Selection Box");
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_GRANT_EXPERIENCE);
									W2.setOriginatingObject(o);
									String[] sList = new String[46];
	
									sList[0] = "@exp_n:bio_engineer_dna_harvesting";
									sList[1] = "@exp_n:bountyhunter";
									sList[2] = "@exp_n:camp";
									sList[3] = "@exp_n:combat_general";
									sList[4] = "@exp_n:combat_meleespecialize_onehand";
									sList[5] = "@exp_n:combat_meleespecialize_onehandlightsaber";
									sList[6] = "@exp_n:combat_meleespecialize_polearm";
									sList[7] = "@exp_n:combat_meleespecialize_polearmlightsaber";
									sList[8] = "@exp_n:combat_meleespecialize_twohand";
									sList[9] = "@exp_n:combat_meleespecialize_twohandlightsaber";
									sList[10] = "@exp_n:combat_meleespecialize_unarmed";
									sList[11] = "@exp_n:combat_rangedspecialize_carbine";
									sList[12] = "@exp_n:combat_rangedspecialize_heavy";
									sList[13] = "@exp_n:combat_rangedspecialize_pistol";
									sList[14] = "@exp_n:combat_rangedspecialize_rifle";
									sList[15] = "@exp_n:crafting_bio_engineer_creature";
									sList[16] = "@exp_n:crafting_clothing_armor";
									sList[17] = "@exp_n:crafting_clothing_general";
									sList[18] = "@exp_n:crafting_droid_general";
									sList[19] = "@exp_n:crafting_food_general";
									sList[20] = "@exp_n:crafting_general";
									sList[21] = "@exp_n:crafting_medicine_general";
									sList[22] = "@exp_n:crafting_spice";
									sList[23] = "@exp_n:crafting_structure_general";
									sList[24] = "@exp_n:crafting_weapons_general";
									sList[25] = "@exp_n:creaturehandler";
									sList[26] = "@exp_n:dance";
									sList[27] = "@exp_n:entertainer_healing";
									sList[28] = "@exp_n:force_rank_xp";
									sList[29] = "@exp_n:fs_combat";
									sList[30] = "@exp_n:fs_crafting";
									sList[31] = "@exp_n:fs_reflex";
									sList[32] = "@exp_n:fs_senses";
									sList[33] = "@exp_n:imagedesigner";
									sList[34] = "@exp_n:jedi_general";
									sList[35] = "@exp_n:medical";
									sList[36] = "@exp_n:merchant";
									sList[37] = "@exp_n:music";
									sList[38] = "@exp_n:political";
									sList[39] = "@exp_n:resource_harvesting_inorganic";
									sList[40] = "@exp_n:scout";
									sList[41] = "@exp_n:shipwright";
									sList[42] = "@exp_n:slicing";
									sList[43] = "@exp_n:space_combat_general";
									sList[44] = "@exp_n:squadleader";
									sList[45] = "@exp_n:trapping";
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Select Experience Type to Grant";
									String DataListPrompt = "Select a type of Experience to grant to your character";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
									break;
								}
								case 7: //sList[7] = "Credits - Moola to buy stuff!";
								{
									player.creditBankCredits(100000);
									client.insertPacket(PacketFactory.buildChatSystemMessage("You Receive 100000 Credits."));
									break;
								}
								case 8: //sList[8] = Tools
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_SELECT_TOOLS_CAT);
									W2.setOriginatingObject(o);
									W2.setOriginatingObject(player);
									Vector<String> vDT = DatabaseInterface.getTools();
									//Vector<SOEObject> oL = new Vector<SOEObject>();
									String [] sList = new String [vDT.size()];
									for(int i = 0; i < vDT.size(); i++)
									{
										//DeedTemplate t = vDT.get(i);
										sList[i] = vDT.get(i);//"@" + server.getTemplateData(t.getObject_template_id()).getSTFFileName() + ":" + server.getTemplateData(t.getObject_template_id()).getSTFFileIdentifier();
										//System.out.println("String:" + sList[i] + " Pos:" + i + " TID:" + t.getTemplateid());
										//SOEObject oD = new SOEObject();
										//oD.setID(t.getTemplateid());
										//oL.add(oD);
									}
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder Tools Category";
									String DataListPrompt = "Select A Tools Category.";
									client.insertPacket(W2.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
									break;
								}
								}
							}
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_FROG_SELECT_PROFESSION: {
						iSelectedItemIndex = Integer.parseInt(sSelectionIDString);
						if(window!=null)
						{
							SOEObject o = window.getOriginatingObject();
							if(o instanceof Terminal)
							{
								System.out.println("Object Script Name: " + o.getScriptName());
								switch(iSelectedItemIndex)
								{
								case 0://sList[0] = "@skl_t:crafting_artisan";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_TEACH_SKILL);
									W2.setOriginatingObject(o);
									String[] aList = server.getAllSkillsWithSimilarNameStringArray("crafting_artisan");
									String[] bList = server.getAllSkillsWithSimilarNameStringArray("crafting_chef");
									String[] cList = server.getAllSkillsWithSimilarNameStringArray("crafting_droidengineer");
									String[] dList = server.getAllSkillsWithSimilarNameStringArray("crafting_merchant");
									String[] eList = server.getAllSkillsWithSimilarNameStringArray("crafting_shipwright");
									String[] fList = server.getAllSkillsWithSimilarNameStringArray("crafting_armorsmith");
									String[] gList = server.getAllSkillsWithSimilarNameStringArray("crafting_architect");
									String[] hList = server.getAllSkillsWithSimilarNameStringArray("crafting_tailor");
									String[] iList = server.getAllSkillsWithSimilarNameStringArray("crafting_weaponsmith");
	
	
									int totallen = aList.length + bList.length + cList.length + dList.length + eList.length + fList.length + gList.length + hList.length + iList.length;
									int alen = aList.length;
									int blen = bList.length;
									int clen = cList.length;
									int dlen = dList.length;
									int elen = eList.length;
									int flen = fList.length;
									int glen = gList.length;
									int hlen = hList.length;
									int ilen = iList.length;
	
									String[] sList = new String[totallen];
									int lastentry = 0;
									for(int i = 0; i < alen; i++)
									{
										sList[i] = "@skl_n:" + aList[i];
										lastentry++;
									}
	
									for(int i = 0; i < blen; i++)
									{
										sList[lastentry] = "@skl_n:" + bList[i];
										lastentry++;
									}
	
									for(int i = 0; i < clen; i++)
									{
										sList[lastentry] = "@skl_n:" + cList[i];
										lastentry++;
									}
	
									for(int i = 0; i < dlen; i++)
									{
										sList[lastentry] = "@skl_n:" + dList[i];
										lastentry++;
									}
	
									for(int i = 0; i < elen; i++)
									{
										sList[lastentry] = "@skl_n:" + eList[i];
										lastentry++;
									}
	
									for(int i = 0; i < flen; i++)
									{
										sList[lastentry] = "@skl_n:" + fList[i];
										lastentry++;
									}
	
									for(int i = 0; i < glen; i++)
									{
										sList[lastentry] = "@skl_n:" + gList[i];
										lastentry++;
									}
	
									for(int i = 0; i < hlen; i++)
									{
										sList[lastentry] = "@skl_n:" + hList[i];
										lastentry++;
									}
									for(int i = 0; i < ilen; i++)
									{
										sList[lastentry] = "@skl_n:" + iList[i];
										lastentry++;
									}
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Artisan Crafter Skills";
									String DataListPrompt = "Select Your Crafter Skills";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
	
									break;
								}
								case 1://sList[1] = "@skl_t:combat_brawler";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_TEACH_SKILL);
									W2.setOriginatingObject(o);
									String[] aList = server.getAllSkillsWithSimilarNameStringArray("combat_brawler");
									String[] bList = server.getAllSkillsWithSimilarNameStringArray("combat_unarmed");
									String[] cList = server.getAllSkillsWithSimilarNameStringArray("combat_melee");
									String[] dList = server.getAllSkillsWithSimilarNameStringArray("combat_1hsword");
									String[] eList = server.getAllSkillsWithSimilarNameStringArray("combat_2hsword");
									String[] fList = server.getAllSkillsWithSimilarNameStringArray("combat_polearm");
									int totallen = aList.length + bList.length + cList.length + dList.length + eList.length + fList.length;
									int alen = aList.length;
									int blen = bList.length;
									int clen = cList.length;
									int dlen = dList.length;
									int elen = eList.length;
									int flen = fList.length;
	
									String[] sList = new String[totallen];
									int lastentry = 0;
									for(int i = 0; i < alen; i++)
									{
										sList[i] = "@skl_n:" + aList[i];
										lastentry++;
									}
	
									for(int i = 0; i < blen; i++)
									{
										sList[lastentry] = "@skl_n:" + bList[i];
										lastentry++;
									}
	
									for(int i = 0; i < clen; i++)
									{
										sList[lastentry] = "@skl_n:" + cList[i];
										lastentry++;
									}
	
									for(int i = 0; i < dlen; i++)
									{
										sList[lastentry] = "@skl_n:" + dList[i];
										lastentry++;
									}
	
									for(int i = 0; i < elen; i++)
									{
										sList[lastentry] = "@skl_n:" + eList[i];
										lastentry++;
									}
	
									for(int i = 0; i < flen; i++)
									{
										sList[lastentry] = "@skl_n:" + fList[i];
										lastentry++;
									}
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Brawler Skills";
									String DataListPrompt = "Select Your Brawler Skills";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
	
									break;
								}
								case 2://sList[2] = "@skl_t:social_entertainer";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_TEACH_SKILL);
									W2.setOriginatingObject(o);
									String[] aList = server.getAllSkillsWithSimilarNameStringArray("social_entertainer");
									String[] bList = server.getAllSkillsWithSimilarNameStringArray("social_dancer");
									String[] cList = server.getAllSkillsWithSimilarNameStringArray("social_imagedesigner");
									String[] dList = server.getAllSkillsWithSimilarNameStringArray("social_musician");
	
									int totallen = aList.length + bList.length + cList.length + dList.length;// + eList.length;// + fList.length + gList.length + hList.length + iList.length;
									int alen = aList.length;
									int blen = bList.length;
									int clen = cList.length;
									int dlen = dList.length;
	
	
									String[] sList = new String[totallen];
									int lastentry = 0;
									for(int i = 0; i < alen; i++)
									{
										sList[i] = "@skl_n:" + aList[i];
										lastentry++;
									}
	
									for(int i = 0; i < blen; i++)
									{
										sList[lastentry] = "@skl_n:" + bList[i];
										lastentry++;
									}
	
									for(int i = 0; i < clen; i++)
									{
										sList[lastentry] = "@skl_n:" + cList[i];
										lastentry++;
									}
	
									for(int i = 0; i < dlen; i++)
									{
										sList[lastentry] = "@skl_n:" + dList[i];
										lastentry++;
									}
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Entertainer Skills";
									String DataListPrompt = "Select Your Crafter Skills";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
	
									break;
								}
								case 3://sList[3] = "@skl_t:combat_marksman";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_TEACH_SKILL);
									W2.setOriginatingObject(o);
									String[] aList = server.getAllSkillsWithSimilarNameStringArray("combat_marksman");
									String[] bList = server.getAllSkillsWithSimilarNameStringArray("combat_pistol");
									String[] cList = server.getAllSkillsWithSimilarNameStringArray("combat_carbine");
									String[] dList = server.getAllSkillsWithSimilarNameStringArray("combat_rifleman");
									String[] eList = server.getAllSkillsWithSimilarNameStringArray("combat_bountyhunter");
									String[] fList = server.getAllSkillsWithSimilarNameStringArray("combat_commando");
									String[] gList = server.getAllSkillsWithSimilarNameStringArray("combat_smuggler");
	
	
									int totallen = aList.length + bList.length + cList.length + dList.length + eList.length + fList.length + gList.length;// + hList.length + iList.length;
									int alen = aList.length;
									int blen = bList.length;
									int clen = cList.length;
									int dlen = dList.length;
									int elen = eList.length;
									int flen = fList.length;
									int glen = gList.length;
									//int hlen = hList.length;
									//int ilen = iList.length;
	
									String[] sList = new String[totallen];
									int lastentry = 0;
									for(int i = 0; i < alen; i++)
									{
										sList[i] = "@skl_n:" + aList[i];
										lastentry++;
									}
	
									for(int i = 0; i < blen; i++)
									{
										sList[lastentry] = "@skl_n:" + bList[i];
										lastentry++;
									}
	
									for(int i = 0; i < clen; i++)
									{
										sList[lastentry] = "@skl_n:" + cList[i];
										lastentry++;
									}
	
									for(int i = 0; i < dlen; i++)
									{
										sList[lastentry] = "@skl_n:" + dList[i];
										lastentry++;
									}
	
									for(int i = 0; i < elen; i++)
									{
										sList[lastentry] = "@skl_n:" + eList[i];
										lastentry++;
									}
	
									for(int i = 0; i < flen; i++)
									{
										sList[lastentry] = "@skl_n:" + fList[i];
										lastentry++;
									}
	
									for(int i = 0; i < glen; i++)
									{
										sList[lastentry] = "@skl_n:" + gList[i];
										lastentry++;
									}
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Marksman Skills";
									String DataListPrompt = "Select Your Marksman Skills";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
									break;
								}
								case 4://sList[4] = "@skl_t:science_medic";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_TEACH_SKILL);
									W2.setOriginatingObject(o);
									String[] aList = server.getAllSkillsWithSimilarNameStringArray("science_medic");
									String[] bList = server.getAllSkillsWithSimilarNameStringArray("science_doctor");
									String[] cList = server.getAllSkillsWithSimilarNameStringArray("science_combatmedic");
	
									int totallen = aList.length + bList.length + cList.length;// + dList.length + eList.length + fList.length + gList.length;// + hList.length + iList.length;
									int alen = aList.length;
									int blen = bList.length;
									int clen = cList.length;
	
									String[] sList = new String[totallen];
									int lastentry = 0;
									for(int i = 0; i < alen; i++)
									{
										sList[i] = "@skl_n:" + aList[i];
										lastentry++;
									}
	
									for(int i = 0; i < blen; i++)
									{
										sList[lastentry] = "@skl_n:" + bList[i];
										lastentry++;
									}
	
									for(int i = 0; i < clen; i++)
									{
										sList[lastentry] = "@skl_n:" + cList[i];
										lastentry++;
									}
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Medic Skills";
									String DataListPrompt = "Select Your Medic Skills";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
	
									break;
								}
								case 5://sList[5] = "@skl_t:outdoors_scout";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_TEACH_SKILL);
									W2.setOriginatingObject(o);
									String[] aList = server.getAllSkillsWithSimilarNameStringArray("outdoors_scout");
									String[] bList = server.getAllSkillsWithSimilarNameStringArray("outdoors_creaturehandler");
									String[] cList = server.getAllSkillsWithSimilarNameStringArray("outdoors_bio_engineer");
									String[] dList = server.getAllSkillsWithSimilarNameStringArray("outdoors_ranger");
									String[] eList = server.getAllSkillsWithSimilarNameStringArray("outdoors_squadleader");
	
									int totallen = aList.length + bList.length + cList.length + dList.length + eList.length;// + fList.length + gList.length;// + hList.length + iList.length;
									int alen = aList.length;
									int blen = bList.length;
									int clen = cList.length;
									int dlen = dList.length;
									int elen = eList.length;
	
									String[] sList = new String[totallen];
									int lastentry = 0;
									for(int i = 0; i < alen; i++)
									{
										sList[i] = "@skl_n:" + aList[i];
										lastentry++;
									}
	
									for(int i = 0; i < blen; i++)
									{
										sList[lastentry] = "@skl_n:" + bList[i];
										lastentry++;
									}
	
									for(int i = 0; i < clen; i++)
									{
										sList[lastentry] = "@skl_n:" + cList[i];
										lastentry++;
									}
	
									for(int i = 0; i < dlen; i++)
									{
										sList[lastentry] = "@skl_n:" + dList[i];
										lastentry++;
									}
	
									for(int i = 0; i < elen; i++)
									{
										sList[lastentry] = "@skl_n:" + eList[i];
										lastentry++;
									}
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Scout Skills";
									String DataListPrompt = "Select Your Scout Skills";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
	
									break;
								}
								case 6://sList[6] = "@skl_t:social_politician";
								{
									SUIWindow W2 = new SUIWindow(player);
									W2.setWindowType(Constants.SUI_FROG_TEACH_SKILL);
									W2.setOriginatingObject(o);
									String[] aList = server.getAllSkillsWithSimilarNameStringArray("social_politician");
	
									int totallen = aList.length;// + bList.length + cList.length + dList.length + eList.length;// + fList.length + gList.length;// + hList.length + iList.length;
									int alen = aList.length;
	
	
	
									String[] sList = new String[totallen];
									int lastentry = 0;
									for(int i = 0; i < alen; i++)
									{
										sList[i] = "@skl_n:" + aList[i];
										lastentry++;
									}
	
									String WindowTypeString = "handleSUI";
									String DataListTitle = "Character Builder:Politician Skills";
									String DataListPrompt = "Select Your Politician Skills";
	
									client.insertPacket(W2.SUIScriptListBox(player.getClient(), WindowTypeString, DataListTitle, DataListPrompt, sList, null, 0, 0));
	
									break;
								}
								}
							}
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_FROG_GRANT_EXPERIENCE: {
						iSelectedItemIndex = Integer.parseInt(sSelectionIDString);
						if(window!=null)
						{
							int iMaxXP = 0;
							String sRequiredSkill = "";
							String sXPName = "";
							int iSkillID = 0;
	
							switch(iSelectedItemIndex)
							{
							case 0: { iMaxXP =  60000; sRequiredSkill = "outdoors_bio_engineer_novice"; sXPName = "bio_engineer_dna_harvesting"; iSkillID = 357; break; } // 367, outdoors_bio_engineer_dna_harvesting_01, bio_engineer_dna_harvesting, 60000
							case 1: { iMaxXP =  10000; sRequiredSkill = "combat_bountyhunter_investigation_01"; sXPName = "bountyhunter"; iSkillID = 530; break; } // 530, combat_bountyhunter_investigation_01, bountyhunter, 10000
							case 2: { iMaxXP =  10000; sRequiredSkill = "outdoors_scout_camp_01"; sXPName = "camp"; iSkillID = 45; break; } // 45, outdoors_scout_camp_01, camp, 10000
							case 3: { iMaxXP =  170000; sRequiredSkill = "combat_marksman_support_01"; sXPName = "combat_general"; iSkillID = 124; break; } // 124, combat_marksman_support_01, combat_general, 170000
							case 4: { iMaxXP =  10000; sRequiredSkill = "combat_brawler_1handmelee_01"; sXPName = "combat_meleespecialize_onehand"; iSkillID = 97; break; } // 97, combat_brawler_1handmelee_01, combat_meleespecialize_onehand, 10000
							case 5: { iMaxXP =  20000; sRequiredSkill = "jedi_padawan_saber_01"; sXPName = "combat_meleespecialize_onehandlightsaber"; iSkillID = 690; break; } // 690, jedi_padawan_saber_01, combat_meleespecialize_onehandlightsaber, 20000
							case 6: { iMaxXP =  10000; sRequiredSkill = "combat_brawler_polearm_01"; sXPName = "combat_meleespecialize_polearm"; iSkillID = 105; break; } // 105, combat_brawler_polearm_01, combat_meleespecialize_polearm, 10000
							case 7: { iMaxXP =  300000; sRequiredSkill = "jedi_light_side_master_saber_01"; sXPName = "combat_meleespecialize_polearmlightsaber"; iSkillID = 728; break; } // 728, jedi_light_side_master_saber_01, combat_meleespecialize_polearmlightsaber, 300000
							case 8: { iMaxXP =  10000; sRequiredSkill = "combat_brawler_2handmelee_01"; sXPName = "combat_meleespecialize_twohand"; iSkillID = 101; break; } // 101, combat_brawler_2handmelee_01, combat_meleespecialize_twohand, 10000
							case 9: { iMaxXP =  300000; sRequiredSkill = "jedi_light_side_journeyman_saber_01"; sXPName = "combat_meleespecialize_twohandlightsaber"; iSkillID = 709; break; } // 709, jedi_light_side_journeyman_saber_01, combat_meleespecialize_twohandlightsaber, 300000
							case 10: { iMaxXP =  10000; sRequiredSkill = "combat_brawler_unarmed_01"; sXPName = "combat_meleespecialize_unarmed"; iSkillID = 93; break; } // 93, combat_brawler_unarmed_01, combat_meleespecialize_unarmed, 10000
							case 11: { iMaxXP =  10000; sRequiredSkill = "combat_marksman_carbine_01"; sXPName = "combat_rangedspecialize_carbine"; iSkillID = 120; break; } // 120, combat_marksman_carbine_01, combat_rangedspecialize_carbine, 10000
							case 12: { iMaxXP =  300000; sRequiredSkill = "combat_bountyhunter_support_01"; sXPName = "combat_rangedspecialize_heavy"; iSkillID = 542; break; } // 542, combat_bountyhunter_support_01, combat_rangedspecialize_heavy, 300000
							case 13: { iMaxXP =  10000; sRequiredSkill = "combat_marksman_pistol_01"; sXPName = "combat_rangedspecialize_pistol"; iSkillID = 116; break; } // 116, combat_marksman_pistol_01, combat_rangedspecialize_pistol, 10000
							case 14: { iMaxXP =  10000; sRequiredSkill = "combat_marksman_rifle_01"; sXPName = "combat_rangedspecialize_rifle"; iSkillID = 112; break; } // 112, combat_marksman_rifle_01, combat_rangedspecialize_rifle, 10000
							case 15: { iMaxXP =  60000; sRequiredSkill = "outdoors_bio_engineer_creature_01"; sXPName = "crafting_bio_engineer_creature"; iSkillID = 359; break; } // 359, outdoors_bio_engineer_creature_01, crafting_bio_engineer_creature, 60000
							case 16: { iMaxXP =  56000; sRequiredSkill = "crafting_armorsmith_personal_01"; sXPName = "crafting_clothing_armor"; iSkillID = 378; break; } // 378, crafting_armorsmith_personal_01, crafting_clothing_armor, 56000
							case 17: { iMaxXP =  56000; sRequiredSkill = "crafting_tailor_casual_01"; sXPName = "crafting_clothing_general"; iSkillID = 435; break; } // 435, crafting_tailor_casual_01, crafting_clothing_general, 56000
							case 18: { iMaxXP =  140000; sRequiredSkill = "crafting_droidengineer_production_01"; sXPName = "crafting_droid_general"; iSkillID = 473; break; } // 473, crafting_droidengineer_production_01, crafting_droid_general, 140000
							case 19: { iMaxXP =  56000; sRequiredSkill = "crafting_chef_dish_01"; sXPName = "crafting_food_general"; iSkillID = 416; break; } // 416, crafting_chef_dish_01, crafting_food_general, 56000
							case 20: { iMaxXP =  2000; sRequiredSkill = "crafting_artisan"; sXPName = "crafting_general"; iSkillID = 70; break; } // 73, crafting_artisan_engineering_01, crafting_general, 2000
							case 21: { iMaxXP =  2000; sRequiredSkill = "science_medic_crafting_01"; sXPName = "crafting_medicine_general"; iSkillID = 65; break; } // 65, science_medic_crafting_01, crafting_medicine_general, 2000
							case 22: { iMaxXP =  24000; sRequiredSkill = "combat_smuggler_spice_01"; sXPName = "crafting_spice"; iSkillID = 523; break; } // 523, combat_smuggler_spice_01, crafting_spice, 24000
							case 23: { iMaxXP =  140000; sRequiredSkill = "crafting_architect_production_01"; sXPName = "crafting_structure_general"; iSkillID = 454; break; } // 454, crafting_architect_production_01, crafting_structure_general, 140000
							case 24: { iMaxXP =  56000; sRequiredSkill = "crafting_weaponsmith_melee_01"; sXPName = "crafting_weapons_general"; iSkillID = 397; break; } // 397, crafting_weaponsmith_melee_01, crafting_weapons_general, 56000
							case 25: { iMaxXP =  60000; sRequiredSkill = "outdoors_creaturehandler_taming_01"; sXPName = "creaturehandler"; iSkillID = 340; break; } // 340, outdoors_creaturehandler_taming_01, creaturehandler, 60000
							case 26: { iMaxXP =  10000; sRequiredSkill = "social_entertainer_dance_01"; sXPName = "dance"; iSkillID = 21; break; } // 21, social_entertainer_dance_01, dance, 10000
							case 27: { iMaxXP =  10000; sRequiredSkill = "social_entertainer_healing_01"; sXPName = "entertainer_healing"; iSkillID = 25; break; } // 25, social_entertainer_healing_01, entertainer_healing, 10000
							case 28: { iMaxXP =  15000; sRequiredSkill = "force_rank_light_novice"; sXPName = "force_rank_xp"; iSkillID = 957; break; } // 957, force_rank_light_novice, force_rank_xp, 15000
							case 29: { iMaxXP =  5100000; sRequiredSkill = "force_sensitive_combat_prowess_novice"; sXPName = "fs_combat"; iSkillID = 784; break; } // 784, force_sensitive_combat_prowess_novice, fs_combat, 5100000
							case 30: { iMaxXP =  5100000; sRequiredSkill = "force_sensitive_crafting_mastery_novice"; sXPName = "fs_crafting"; iSkillID = 822; break; } // 822, force_sensitive_crafting_mastery_novice, fs_crafting, 5100000
							case 31: { iMaxXP =  5100000; sRequiredSkill = "force_sensitive_enhanced_reflexes_novice"; sXPName = "fs_reflex"; iSkillID = 803; break; } // 803, force_sensitive_enhanced_reflexes_novice, fs_reflex, 5100000
							case 32: { iMaxXP =  5100000; sRequiredSkill = "force_sensitive_heightened_senses_novice"; sXPName = "fs_senses"; iSkillID = 841; break; } // 841, force_sensitive_heightened_senses_novice, fs_senses, 5100000
							case 33: { iMaxXP =  10000; sRequiredSkill = "social_entertainer_hairstyle_01"; sXPName = "imagedesigner"; iSkillID = 13; break; } // 13, social_entertainer_hairstyle_01, imagedesigner, 10000
							case 34: { iMaxXP =  4000000; sRequiredSkill = "jedi_padawan_master"; sXPName = "jedi_general"; iSkillID = 689; break; } // 689, jedi_padawan_master, jedi_general, 4000000
							case 35: { iMaxXP =  10000; sRequiredSkill = "science_medic_injury_01"; sXPName = "medical"; iSkillID = 53; break; } // 53, science_medic_injury_01, medical, 10000
							case 36: { iMaxXP =  20000; sRequiredSkill = "crafting_merchant_novice"; sXPName = "merchant"; iSkillID = 490; break; } // 490, crafting_merchant_novice, merchant, 20000
							case 37: { iMaxXP =  10000; sRequiredSkill = "social_entertainer_music_01"; sXPName = "music"; iSkillID = 17; break; } // 17, social_entertainer_music_01, music, 10000
							case 38: { iMaxXP =  20000; sRequiredSkill = "social_politician_fiscal_01"; sXPName = "political"; iSkillID = 625; break; } // 625, social_politician_fiscal_01, political, 20000
							case 39: { iMaxXP =  5000; sRequiredSkill = "crafting_artisan_survey_01"; sXPName = "resource_harvesting_inorganic"; iSkillID = 85; break; } // 85, crafting_artisan_survey_01, resource_harvesting_inorganic, 5000
							case 40: { iMaxXP =  10000; sRequiredSkill = "outdoors_scout_movement_01"; sXPName = "scout"; iSkillID = 33; break; } // 33, outdoors_scout_movement_01, scout, 10000
							case 41: { iMaxXP =  300000; sRequiredSkill = "crafting_shipwright_master"; sXPName = "shipwright"; iSkillID = 993; break; } // 993, crafting_shipwright_master, shipwright, 300000
							case 42: { iMaxXP =  10000; sRequiredSkill = "combat_smuggler_slicing_01"; sXPName = "slicing"; iSkillID = 515; break; } // 515, combat_smuggler_slicing_01, slicing, 10000
							case 43: { iMaxXP =  7500000; sRequiredSkill = "pilot_rebel_navy_master"; sXPName = "space_combat_general"; iSkillID = 1013; break; } // 1013, pilot_rebel_navy_master, space_combat_general, 7500000
							case 44: { iMaxXP =  500000; sRequiredSkill = "outdoors_squadleader_movement_01"; sXPName = "squadleader"; iSkillID = 606; break; } // 606, outdoors_squadleader_movement_01, squadleader, 500000
							case 45: { iMaxXP =  10000; sRequiredSkill = "outdoors_scout_tools_01"; sXPName = "trapping"; iSkillID = 37; break; } // 37, outdoors_scout_tools_01, trapping, 10000
	
							}
							
							player.updateExperience(null, DatabaseInterface.getExperienceIDFromName(sXPName), iMaxXP);
							client.insertPacket(PacketFactory.buildChatSystemMessage(
									null,
									"Granted %DI Points of %TO Experience",
									0,
									null,
									null,
									null,
									0,
									null,
									null,
									null,
									0,
									"exp_n",
									sXPName,
									null,
									iMaxXP,
									0f,
									true
							));
	
							//client.insertPacket(PacketFactory.buildFlyTextSTFMessage(p,"",,"exp_n",sXPName,iMaxXP ));
	
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_ADMIN_ADD_SKILL_TRAINER: {
						iSelectedItemIndex = Integer.parseInt(sSelectionIDString);
						
						if(window!=null)
						{
							int TemplateID = 0;
							String TrainerName = "";
							switch(iSelectedItemIndex)
							{
							case 0://social_entertainer
							{
								TemplateID = 4284;
								TrainerName = "an Entertainer trainer";
								break;
							}
							case 1://outdoors_scout
							{
								TemplateID = 5065;
								TrainerName = "a Scout trainer";
								break;
							}
							case 2://science_medic
							{
								TemplateID = 4621;
								TrainerName = "a Medic trainer";
								break;
							}
							case 3://crafting_artisan
							{
								TemplateID = 3728;
								TrainerName = "an Artisan trainer";
								break;
							}
							case 4://combat_brawler
							{
								TemplateID = 3867;
								TrainerName = "a Brawler trainer";
								break;
							}
							case 5://combat_marksman
							{
								TemplateID = 4590;
								TrainerName = "a Marksman trainer";
								break;
							}
							case 6://combat_rifleman
							{
								TemplateID = 5034;
								TrainerName = "a Rifleman trainer";
								break;
							}
							case 7://combat_pistol
							{
								TemplateID = 4833;
								TrainerName = "a Pistoleer trainer";
								break;
							}
							case 8://combat_carbine
							{
								TemplateID = 3899;
								TrainerName = "a Carbineer trainer";
								break;
							}
							case 9://combat_unarmed
							{
								TemplateID = 5365;
								TrainerName = "a Teras Kasi Artist trainer";
								break;
							}
							case 10://combat_1hsword
							{
								TemplateID = 3698;
								TrainerName = "a Fencer trainer";
								break;
							}
							case 11://combat_2hsword
							{
								TemplateID = 3701;
								TrainerName = "a Swordsman trainer";
								break;
							}
							case 12://combat_polearm
							{
								TemplateID = 4843;
								TrainerName = "a Pikeman trainer";
								break;
							}
							case 13://social_dancer
							{
								TemplateID = 4174;
								TrainerName = "a Dancer trainer";
								break;
							}
							case 14://social_musician
							{
								TemplateID = 4670;
								TrainerName = "a Musician trainer";
								break;
							}
							case 15://science_doctor
							{
								TemplateID = 4249;
								TrainerName = "a Doctor trainer";
								break;
							}
							case 16://outdoors_ranger
							{
								TemplateID = 4862;
								TrainerName = "a Ranger trainer";
								break;
							}
							case 17://outdoors_creaturehandler
							{
								TemplateID = 4131;
								TrainerName = "a Creature Handler trainer";
								break;
							}
							case 18://outdoors_bio_engineer
							{
								TemplateID = 3791;
								TrainerName = "a Bio Engineer trainer";
								break;
							}
							case 19://crafting_armorsmith
							{
								TemplateID = 3725;
								TrainerName = "an Armorsmith trainer";
								break;
							}
							case 20://crafting_weaponsmith
							{
								TemplateID = 5393;
								TrainerName = "a Weaponsmith trainer";
								break;
							}
							case 21://crafting_chef
							{
								TemplateID = 3910;
								TrainerName = "a Chef trainer";
								break;
							}
							case 22://crafting_tailor
							{
								TemplateID = 5239;
								TrainerName = "a Tailor trainer";
								break;
							}
							case 23://crafting_architect
							{
								TemplateID = 3722;
								TrainerName = "an Architect trainer";
								break;
							}
							case 24://crafting_droidengineer
							{
								TemplateID = 4260;
								TrainerName = "a Droid Engineer trainer";
								break;
							}
							case 25://crafting_merchant
							{
								TemplateID = 4653;
								TrainerName = "a Merchant trainer";
								break;
							}
							case 26://combat_smuggler
							{
								TemplateID = 5152;
								TrainerName = "a Smuggler trainer";
								break;
							}
							case 27://combat_bountyhunter
							{
								TemplateID = 3861;
								TrainerName = "a Bounty Hunter trainer";
								break;
							}
							case 28://combat_commando
							{
								TemplateID = 3936;
								TrainerName = "a Commando trainer";
								break;
							}
							case 29://science_combatmedic
							{
								TemplateID = 3933;
								TrainerName = "a Combat Medic trainer";
								break;
							}
							case 30://social_imagedesigner
							{
								TemplateID = 4441;
								TrainerName = "an Image Designer trainer";
								break;
							}
							case 31://outdoors_squadleader
							{
								TemplateID = 5175;
								TrainerName = "a Squad Leader trainer";
								break;
							}
							case 32://social_politician
							{
								TemplateID = 4481;
								TrainerName = "a Politician trainer";
								break;
							}
                                                        case 33://fs_trainer
							{
								TemplateID = 4339;
								TrainerName = "a Force Sensitive";
								break;
							}
                                                        case 34://fs_trainer
							{
								TemplateID = 4491;
								TrainerName = "a Light Jedi Journyman";
								break;
							}
                                                        case 35://fs_trainer
							{
								TemplateID = 4492;
								TrainerName = "a Light Jedi Master";
								break;
							}
							}
							float x,y,z,oJ,oW;
							long cellID = client.getPlayer().getCellID();
							int planetid = client.getPlayer().getPlanetID();
	
							oJ = client.getPlayer().getOrientationS();
							oW = client.getPlayer().getOrientationW();
							x = client.getPlayer().getX();
							y = client.getPlayer().getY();
							z = client.getPlayer().getZ();
	
							if(cellID >= 1)
							{
								//player is in a cell
								x = client.getPlayer().getCellX();
								y = client.getPlayer().getCellY();
								z = client.getPlayer().getCellZ();
							}
							//long TrainerID = client.getServer().getNextObjectID();
							//insert into terminals values(20002455, 111428352, 'Artisan Trainer', 0, 0, -0.295, 0, 0.955, 3505, 0, -4810, 'SkillTrainer.js', 8, 0, 0, 11, 0, 0, 1, '', 0, -1, 0);
							//System.out.println("Insert Into terminals Values(" + TrainerID + "," + TemplateID + ",'" + TrainerName + "',0,0," + oS + ",0," + oW + "," + x + "," + z + "," + y + ",'SkillTrainer.js'," + planetid + "," + cellID + ",0,11,0,0,1,null,0,-1,0);");
							boolean result = client.getServer().insertSkillTrainer(client, TemplateID, TrainerName, x, y, z, oW, oJ, oW, oW, cellID, planetid);
							if(result)
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage(TrainerName + " Added."));
							}
							else
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage(TrainerName + " Not Added Insertion Failure."));
							}
	
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_ADMIN_ADD_TERMINAL: 
					{
						iSelectedItemIndex = Integer.parseInt(sSelectionIDString);
						
						if(window!=null)
						{
							int TemplateID = 0;
							String TerminalName = "";
							int TerminalT = 0;
							switch(iSelectedItemIndex)
							{
							case 0: { TemplateID = 8204; TerminalName = "@terminal_name:beta_terminal_food"; TerminalT = 14;  break; }
							case 1: { TemplateID = 8205; TerminalName = "@terminal_name:beta_terminal_medicine"; TerminalT = 15;  break; }
							case 2: { TemplateID = 8206; TerminalName = "@terminal_name:beta_terminal_money"; TerminalT = 16;  break; }
							case 3: { TemplateID = 8207; TerminalName = "@terminal_name:terminal_resource"; TerminalT = 17;  break; }
							case 4: { TemplateID = 8208; TerminalName = "@terminal_name:terminal_warp"; TerminalT = 18;  break; }
							case 5: { TemplateID = 8210; TerminalName = "@terminal_name:terminal_xp"; TerminalT = 19;  break; }
							case 6: { TemplateID = 8211; TerminalName = "@terminal_name:donham_terminal"; TerminalT = 20;  break; }
							case 7: { TemplateID = 9372; TerminalName = "@terminal_name:keypad"; TerminalT = 21;  break; }
							case 8: { TemplateID = 9375; TerminalName = "@terminal_name:terminal_free_s1"; TerminalT = 22;  break; }
							case 9: { TemplateID = 9377; TerminalName = "@terminal_name:terminal_gas_valve"; TerminalT = 23;  break; }
							case 10: { TemplateID = 9378; TerminalName = "@terminal_name:terminal_power_switch"; TerminalT = 24;  break; }
							case 11: { TemplateID = 9379; TerminalName = "@terminal_name:terminal_general_switch"; TerminalT = 25;  break; }
							case 12: { TemplateID = 9380; TerminalName = "@terminal_name:terminal_power_switch"; TerminalT = 26;  break; }
							case 13: { TemplateID = 14046; TerminalName = "@terminal_name:base_terminal"; TerminalT = 27;  break; }
							case 14: { TemplateID = 14047; TerminalName = "@terminal_name:terminal_ballot_box"; TerminalT = 28;  break; }
							case 15: { TemplateID = 14048; TerminalName = "@terminal_name:terminal_bank"; TerminalT = 13;  break; }
							case 16: { TemplateID = 14049; TerminalName = "@terminal_name:terminal_bazaar"; TerminalT = 29;  break; }
							case 17: { TemplateID = 14050; TerminalName = "@terminal_name:terminal_bestine_01"; TerminalT = 30;  break; }
							case 18: { TemplateID = 14051; TerminalName = "@terminal_name:terminal_bestine_02"; TerminalT = 31;  break; }
							case 19: { TemplateID = 14052; TerminalName = "@terminal_name:terminal_bestine_03"; TerminalT = 32;  break; }
							case 20: { TemplateID = 14053; TerminalName = "@terminal_name:terminal_bounty_droid"; TerminalT = 33;  break; }
							case 21: { TemplateID = 14054; TerminalName = "@terminal_name:terminal_character_builder"; TerminalT = 34;  break; }
							case 22: { TemplateID = 14055; TerminalName = "@terminal_name:terminal_city"; TerminalT = 35;  break; }
							case 23: { TemplateID = 14056; TerminalName = "@terminal_name:terminal_city_vote"; TerminalT = 36;  break; }
							case 24: { TemplateID = 14057; TerminalName = "@terminal_name:terminal_cloning"; TerminalT = 37;  break; }
							case 25: { TemplateID = 14058; TerminalName = "@terminal_name:terminal_geo_bunker"; TerminalT = 38;  break; }
							case 26: { TemplateID = 14059; TerminalName = "@terminal_name:terminal_dark_enclave_challenge"; TerminalT = 39;  break; }
							case 27: { TemplateID = 14060; TerminalName = "@terminal_name:terminal_dark_enclave_voting"; TerminalT = 40;  break; }
							case 28: { TemplateID = 14061; TerminalName = "@terminal_name:terminal_elevator"; TerminalT = 41;  break; }
							case 29: { TemplateID = 14064; TerminalName = "@terminal_name:terminal_geo_bunker"; TerminalT = 44;  break; }
							case 30: { TemplateID = 14065; TerminalName = "@terminal_name:terminal_guild"; TerminalT = 45;  break; }
							case 31: { TemplateID = 14066; TerminalName = "@terminal_name:terminal_hq"; TerminalT = 46;  break; }
							case 32: { TemplateID = 14067; TerminalName = "@terminal_name:terminal_hq_imperial"; TerminalT = 47;  break; }
							case 33: { TemplateID = 14068; TerminalName = "@terminal_name:terminal_hq_rebel"; TerminalT = 48;  break; }
							case 34: { TemplateID = 14069; TerminalName = "@terminal_name:terminal_hq_turret_control"; TerminalT = 49;  break; }
							case 35: { TemplateID = 14070; TerminalName = "@terminal_name:terminal_imagedesign"; TerminalT = 50;  break; }
							case 36: { TemplateID = 14071; TerminalName = "@terminal_name:terminal_insurance"; TerminalT = 51;  break; }
							case 37: { TemplateID = 14073; TerminalName = "@terminal_name:terminal_light_enclave_challenge"; TerminalT = 52;  break; }
							case 38: { TemplateID = 14074; TerminalName = "@terminal_name:terminal_light_enclave_voting"; TerminalT = 53;  break; }
							case 39: { TemplateID = 14075; TerminalName = "@terminal_name:terminal_mission"; TerminalT = 54;  break; }
							case 40: { TemplateID = 14076; TerminalName = "@terminal_name:terminal_mission_artisan"; TerminalT = 55;  break; }
							case 41: { TemplateID = 14077; TerminalName = "@terminal_name:terminal_mission_bounty"; TerminalT = 56;  break; }
							case 42: { TemplateID = 14078; TerminalName = "@terminal_name:terminal_mission_entertainer"; TerminalT = 57;  break; }
							case 43: { TemplateID = 14079; TerminalName = "@terminal_name:terminal_mission_imperial"; TerminalT = 58;  break; }
							case 44: { TemplateID = 14081; TerminalName = "@terminal_name:terminal_mission_rebel"; TerminalT = 60;  break; }
							case 45: { TemplateID = 14082; TerminalName = "@terminal_name:terminal_mission_scout"; TerminalT = 61;  break; }
							case 46: { TemplateID = 14084; TerminalName = "@terminal_name:newbie_clothing_dispenser"; TerminalT = 63;  break; }
							case 47: { TemplateID = 14085; TerminalName = "@terminal_name:newbie_food_dispenser"; TerminalT = 64;  break; }
							case 48: { TemplateID = 14086; TerminalName = "@terminal_name:newbie_instrument_dispenser"; TerminalT = 65;  break; }
							case 49: { TemplateID = 14087; TerminalName = "@terminal_name:newbie_medicine_dispenser"; TerminalT = 66;  break; }
							case 50: { TemplateID = 14088; TerminalName = "@terminal_name:newbie_tool_dispenser"; TerminalT = 67;  break; }
							case 51: { TemplateID = 14090; TerminalName = "@terminal_name:terminal_player_structure"; TerminalT = 68;  break; }
							case 52: { TemplateID = 14093; TerminalName = "@terminal_name:terminal_pm_register"; TerminalT = 71;  break; }
							case 53: { TemplateID = 14095; TerminalName = "@terminal_name:door_security_terminal"; TerminalT = 72;  break; }
							case 54: { TemplateID = 14096; TerminalName = "@terminal_name:terminal_shipping"; TerminalT = 73;  break; }
							case 55: { TemplateID = 14098; TerminalName = "@terminal_name:terminal_space"; TerminalT = 74;  break; }
							case 56: { TemplateID = 14099; TerminalName = "@terminal_name:terminal_travel"; TerminalT = 75;  break; }
							case 57: { TemplateID = 14100; TerminalName = "@terminal_name:terminal_water_pressure"; TerminalT = 76;  break; }
							case 58: { TemplateID = 8069; TerminalName = "@No Build Zone 4"; TerminalT = 78;  break; }
							case 59: { TemplateID = 8068; TerminalName = "@No Build Zone 32"; TerminalT = 79;  break; }
							case 60: { TemplateID = 8070; TerminalName = "@No Build Zone 64"; TerminalT = 80;  break; }
							case 61: { TemplateID = 8067; TerminalName = "@No Build Zone 128"; TerminalT = 81;  break; }
							case 62: { TemplateID = 7716; TerminalName = "@No Build Zone 768"; TerminalT = 82;  break; }
	
							default:
							{
								player.clearSUIListWindowObjectList();
								player.removePendingSUIWindow(suiWindowID);
								return;
							}
	
							}
							float x,y,z,oJ,oW;
							long cellID = client.getPlayer().getCellID();
							int planetid = client.getPlayer().getPlanetID();
	
							oJ = client.getPlayer().getOrientationS();
							oW = client.getPlayer().getOrientationW();
							x = client.getPlayer().getX();
							y = client.getPlayer().getY();
							z = client.getPlayer().getZ();
	
							if(cellID >= 1)
							{
								//player is in a cell
								x = client.getPlayer().getCellX();
								y = client.getPlayer().getCellY();
								z = client.getPlayer().getCellZ();
							}
							//long TrainerID = client.getServer().getNextObjectID();
							//insert into terminals values(20002455, 111428352, 'Artisan Trainer', 0, 0, -0.295, 0, 0.955, 3505, 0, -4810, 'SkillTrainer.js', 8, 0, 0, 11, 0, 0, 1, '', 0, -1, 0);
							//System.out.println("Insert Into terminals Values(" + TrainerID + "," + TemplateID + ",'" + TerminalName + "',0,0," + oS + ",0," + oW + "," + x + "," + z + "," + y + ",'SkillTrainer.js'," + planetid + "," + cellID + ",0,11,0,0,1,null,0,-1,0);");
							boolean result = client.getServer().insertTerminal(client, TemplateID, TerminalName, x, y, z, oW, oJ, oW, oW, cellID, planetid,TerminalT);
							if(result)
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage(TerminalName + " Added."));
							}
							else
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage(TerminalName + " Not Added Insertion Failure."));
							}
	
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_TRAVEL_SELECT_DESTINATION: 
					{
						int DestinationID = Integer.parseInt(sSelectionIDString);
						if(server.getAllTravelDestinations().get(DestinationID) != null)
						{
							TravelDestination T = server.getAllTravelDestinations().get(DestinationID);
							client.getPlayer().playerTravel(T);
						}
						else
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("Error: Destination Was invalid or non existant. ID:" + DestinationID));
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_SELECT_SURVEY_RESOLUTION: 
					{
						int iRangeOption = Integer.parseInt(sSelectionIDString);
						if(iRangeOption != -1)
						{
							SUIWindow W = player.getPendingSUIWindow(suiWindowID);
							TangibleItem toolUsed = (TangibleItem)W.getOriginatingObject();
							toolUsed.setSurveyToolRange(iRangeOption);
							client.insertPacket(PacketFactory.buildChatSystemMessage("Range Set"));
							player.removePendingSUIWindow(suiWindowID);
							return;
						}
						break;
					}
					case Constants.SUI_FROG_SELECT_TOOLS_CAT: {
						int iCategorySelected = Integer.parseInt(sSelectionIDString);
						SUIWindow W2 = new SUIWindow(player);
						W2.setWindowType(Constants.SUI_FROG_SELECT_TOOL);
						W2.setOriginatingObject(player);
						String sCategoryString = DatabaseInterface.getTools().get(iCategorySelected);
						Vector<ItemTemplate> vDT = DatabaseInterface.getItemTemplateToolsGroup(iCategorySelected);
						//Vector<SOEObject> oL = new Vector<SOEObject>();
						String [] sList = new String [vDT.size()];
						for(int i = 0; i < vDT.size(); i++)
						{
							ItemTemplate t = vDT.get(i);
							sList[i] = "@" + t.getSTFFileName() + ":" + t.getSTFFileIdentifier();
						}
						String WindowTypeString = "handleSUI";
						String DataListTitle = sCategoryString + ", Tools Selection";
						String DataListPrompt = "Select " + sCategoryString + " Tool to place in your inventory.";
						client.insertPacket(W2.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_FROG_SELECT_WEARABLES_CAT: {
						int iCategorySelected = Integer.parseInt(sSelectionIDString);
						SUIWindow W2 = new SUIWindow(player);
						W2.setWindowType(Constants.SUI_FROG_SELECT_WEARABLE);
						W2.setOriginatingObject(player);
						String sCategoryString = DatabaseInterface.getWearablesSets().get(iCategorySelected);
						Vector<ItemTemplate> vDT = DatabaseInterface.getItemTemplateWearableGroup(iCategorySelected);
						//Vector<SOEObject> oL = new Vector<SOEObject>();
						String [] sList = new String [vDT.size()];
						for(int i = 0; i < vDT.size(); i++)
						{
							ItemTemplate t = vDT.get(i);
							sList[i] = "@" + t.getSTFFileName() + ":" + t.getSTFFileIdentifier();
						}
						String WindowTypeString = "handleSUI";
						String DataListTitle = sCategoryString + ", Wearables Selection";
						String DataListPrompt = "Select " + sCategoryString + " Wearable to place in your inventory.";
						client.insertPacket(W2.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_FROG_SELECT_WEARABLE: {
						//System.out.println("In Wearables Selection");
						int iItemSelected = Integer.parseInt(sSelectionIDString);
						if(iItemSelected != -1)
						{
							String [] sCategory = suiWindowTitleString.split(",");
							Vector<String> vCat = DatabaseInterface.getWearablesSets();
							int iCategory = -1;
							for(int i = 0; i < vCat.size();i++)
							{
								if(vCat.get(i).compareTo(sCategory[0]) == 0)
								{
									iCategory = i;
								}
							}
							if(iCategory != -1)
							{
								//System.out.println("Category ID:" + iCategory + " " + vCat.get(iCategory));
								Vector<ItemTemplate> vDT = DatabaseInterface.getItemTemplateWearableGroup(iCategory);
								ItemTemplate T = vDT.get(iItemSelected);
								// System.out.println("Template ID Selected: " + T.getTemplateID() + " IFF: " + T.getIFFFileName());
								TangibleItem I = new TangibleItem();
								I.setTemplateID(T.getTemplateID());
								I.setID(client.getServer().getNextObjectID());
								I.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
								I.setConditionDamage(0, false);
								I.setMaxCondition(1000, false);
								I.setOwner(player);
								player.addItemToInventory(I);
								I.setEquipped(player.getInventory(),Constants.EQUIPPED_STATE_UNEQUIPPED);
								client.getServer().addObjectToAllObjects(I, false, false);
								player.spawnItem(I);
								client.insertPacket(PacketFactory.buildChatSystemMessage("Item Given"));
							}
							else
							{
								System.out.println("Bad Category");
							}
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_FROG_SELECT_TOOL: {
						int iItemSelected = Integer.parseInt(sSelectionIDString);
						if(iItemSelected != -1)
						{
							String [] sCategory = suiWindowTitleString.split(",");
							Vector<String> vCat = DatabaseInterface.getTools();
							int iCategory = -1;
							for(int i = 0; i < vCat.size();i++)
							{
								if(vCat.get(i).compareTo(sCategory[0]) == 0)
								{
									iCategory = i;
								}
							}
							if(iCategory != -1)
							{
								//System.out.println("Category ID:" + iCategory + " " + vCat.get(iCategory));
								Vector<ItemTemplate> vDT = DatabaseInterface.getItemTemplateToolsGroup(iCategory);
								ItemTemplate T = vDT.get(iItemSelected);
								int templateID = T.getTemplateID();
								TangibleItem I = null;
								switch (templateID) {
								case 8922: // Clothing crafting
								case 8925: // Food or spice crafting
								case 8926: // Generic tool
								case 8927: // Jedi tool
								case 8934: // Space tool -- ships, missiles, blasters, etc.
								case 8937: // Structures crafting tool
								case 8940: { // Weapon, Droid, General Item -- You are identical to General, except you can handle more advanced schematics. 
									I = new CraftingTool();
									break;
								}
								default: {
									I = new TangibleItem();
									break;
								}
								}
								// System.out.println("Template ID Selected: " + T.getTemplateID() + " IFF: " + T.getIFFFileName());
								I.setTemplateID(T.getTemplateID());
								I.setID(client.getServer().getNextObjectID());
								I.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
								I.setSurveyToolType();
								I.setConditionDamage(0, false);
								I.setMaxCondition(1000, false);
								I.setOwner(player);
								player.addItemToInventory(I);
								I.setEquipped(player.getInventory(),Constants.EQUIPPED_STATE_UNEQUIPPED);
								client.getServer().addObjectToAllObjects(I, false, false);
								player.spawnItem(I);
								client.insertPacket(PacketFactory.buildChatSystemMessage("Item Given"));
							}
							else
							{
								System.out.println("Bad Category");
							}
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_FROG_SELECT_ARMOR: {
						int iCategorySelected = Integer.parseInt(sSelectionIDString);
						//String sCategoryString = DatabaseInterface.getArmorSet().get(iCategorySelected);
						Vector<ItemTemplate> vDT = DatabaseInterface.getItemTemplateArmorGroup(iCategorySelected);
						for(int i = 0; i < vDT.size();i++)
						{
							ItemTemplate T = vDT.get(i);
							// System.out.println("Template ID Selected: " + T.getTemplateID() + " IFF: " + T.getIFFFileName());
							Armor I = new Armor();
							I.setTemplateID(T.getTemplateID());
							I.setID(client.getServer().getNextObjectID());
							I.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
							I.setConditionDamage(0, false);
							I.setMaxCondition(1000, false);
							I.setOwner(player);
							player.addItemToInventory(I);
							I.setEquipped(player.getInventory(),Constants.EQUIPPED_STATE_UNEQUIPPED);
							client.getServer().addObjectToAllObjects(I, false, false);
							player.spawnItem(I);
						}
						client.insertPacket(PacketFactory.buildChatSystemMessage("Armor Set Given"));
						player.removePendingSUIWindow(suiWindowID);
						return;
					}
					case Constants.SUI_FROG_SELECT_DEED:
					case Constants.SUI_SELECT_DEED: {
						int iDeedSelected = Integer.parseInt(sSelectionIDString);
						Vector<DeedTemplate> vDT = DatabaseInterface.getBlueFrogDeedTemplates();
						DeedTemplate t = vDT.get(iDeedSelected);
						if(t!=null)
						{
							Deed d = new Deed(t.getTemplateid(),t.getObject_template_id(), player.getServer());
							if(d!=null)
							{
								d.setEquipped(player.getInventory(), -1);
								d.setOwner(player);
								player.addItemToInventory(d);
								server.addObjectToAllObjects(d,false,false);
								player.spawnItem(d);
								client.insertPacket(PacketFactory.buildChatSystemMessage("Item Given"));
							}
							else
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage("Item Not Given"));
							}
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_STRUCTURE_CONFIRM_REDEED: {
						//sReceivedStrings
						//int choice = Integer.parseInt(sReceivedStrings[0]);
						if(cancel == 0) // Clicked "OK"
						{
							Structure s = (Structure)window.getOriginatingObject();
							String sWindowPrompt = null;
							int iRandomConfirmationCode = SWGGui.getRandomInt(124172,999999);
							if(s.canRedeed())
							{
								sWindowPrompt = "Please confirm you wish to destroy this structure by entering the following code in the input box below and click ok to accept.\r\n\r\n" + iRandomConfirmationCode;
							}
							else
							{
								sWindowPrompt = "Please confirm you wish to destroy this structure by entering the following code in the input box below and click ok to accept.  \n\\#FF0000 THE STRUCTURE WILL NOT BE REDEEDED \\#FFFFFF\r\n\r\n" + iRandomConfirmationCode;
								
							}
							SUIWindow W2 = new SUIWindow(player);
							W2.setWindowType(Constants.SUI_CONFIRM_DESTROY_STRUCTURE);
							W2.setOriginatingObject(s);
							s.setIDestructionCode(iRandomConfirmationCode);
							String sWindowTypeString = "handleFilterInput";
							String sWindowTitle = "@player_structure:confirm_destruction_t";
							String sState = "Enabled";
							String sVisible = "Visible";
							int iMaxInputLength = 6;
							String sCurrentTextString = "";
							client.insertPacket(W2.SUIScriptTextInputBox(client, sWindowTypeString, sWindowPrompt, sWindowTitle, true, sState, sVisible, iMaxInputLength, sCurrentTextString));
	
						}
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_VEHICLE_CONFIRM_REPAIRS: {
					
						int iRepair = Integer.parseInt(sSelectionIDString);
						if(iRepair == 1)
						{
							Vehicle v = (Vehicle)window.getOriginatingObject();
							if(v!=null)
							{
								int iRepairCost = (int)v.getDamage() * 3;
								if(player.hasEnoughCredits(iRepairCost))
								{
									if(player.debitCredits(iRepairCost))
									{
										//repaired_to_max
										//repairs_complete
										v.setDamage(0);
										client.insertPacket(PacketFactory.buildChatSystemMessage("Your vehicle has been repaired."));
										v.useItem(client, (byte)61);
										IntangibleObject parent = (IntangibleObject)server.getObjectFromAllObjects(v.getParentID());
										parent.useItem(client, (byte)1);
									}
									else
									{
										client.insertPacket(PacketFactory.buildChatSystemMessage(
												"pet/pet_menu",
												"err_repair_fail",
												0,
												null,
												null,
												null,
												0,
												null,
												null,
												null,
												0,
												null,
												null,
												null,
												0,
												0f,
												true
										));
	
										//client.insertPacket(PacketFactory.buildFlyTextSTFMessage(p, "pet/pet_menu", "err_repair_fail", "", "", 0));
									}
								}
								else
								{
									client.insertPacket(PacketFactory.buildChatSystemMessage(
											"pet/pet_menu",
											"lacking_funds",
											0,
											null,
											null,
											null,
											0,
											null,
											null,
											null,
											0,
											null,
											null,
											null,
											0,
											0f,
											true
									));
	
									//client.insertPacket(PacketFactory.buildFlyTextSTFMessage(p, "pet/pet_menu", "lacking_funds", "", "", 0));
								}
							}
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_STRUCTURE_REMOVE_ADMIN: {
						SUIWindow W = player.getPendingSUIWindow(suiWindowID);
						Structure s = (Structure)W.getOriginatingObject();
						int iSelection = Integer.parseInt(sReceivedStrings[0]);
						Player oldAdmin = server.getPlayer(s.getVAdminList().get(iSelection));
						if(s.isAdmin(oldAdmin.getID()))
						{
							s.removeFromAdminList(oldAdmin.getID(),player.getClient());
							if(s.isAdmin(oldAdmin.getID()))
							{
								s.updateStructureCellPermissions(player.getClient());
								client.insertPacket(PacketFactory.buildChatSystemMessage(oldAdmin.getFullName() + " Not Removed"));
							}
							else
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage(oldAdmin.getFullName() + " Removed"));
							}
						}
						player.removePendingSUIWindow(W);
						break;
					}
					case Constants.SUI_STRUCTURE_SHOW_ENTRY_LIST: {
						
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_STRUCTURE_SHOW_BANNED_LIST: {
						
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_STRUCTURE_SHOW_ADMIN_LIST: {
						
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_STRUCTURE_SHOW_STATUS: {
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_REMOVE_PLAYER_FROM_ENTRY_LIST: {
						System.out.println("REMOVE ENTRY");
						Structure s = (Structure)window.getOriginatingObject();
						int iSelection = Integer.parseInt(sReceivedStrings[0]);
						System.out.println("Selection: " + iSelection);
						Player oldEntry = server.getPlayer(s.getVEnterList().get(iSelection));
						System.out.println("Selection: " + oldEntry.getFullName());
						if(s.hasEntry(oldEntry.getID()))
						{
							s.removeFromEnterList(oldEntry.getID(),player.getClient());
							if(s.hasEntry(oldEntry.getID()))
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage(oldEntry.getFullName() + " Not Removed"));
							}
							else
							{
								s.updateStructureCellPermissions(player.getClient());
								client.insertPacket(PacketFactory.buildChatSystemMessage(oldEntry.getFullName() + " Removed"));
							}
						}
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_REMOVE_PLAYER_FROM_BAN_LIST: {
						Structure s = (Structure)window.getOriginatingObject();
						int iSelection = Integer.parseInt(sReceivedStrings[0]);
						Player oldBan = server.getPlayer(s.getVEnterList().get(iSelection));
						if(s.isBanned(oldBan.getID()))
						{
							s.removeFromBanList(oldBan.getID());
							if(s.isBanned(oldBan.getID()))
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage(oldBan.getFullName() + " Not Removed"));
							}
							else
							{
								s.updateStructureCellPermissions(player.getClient());
								client.insertPacket(PacketFactory.buildChatSystemMessage(oldBan.getFullName() + " Removed"));
							}
						}
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_START_DANCE_NO_PARAMS: {
						
						int iSelection = Integer.parseInt(sReceivedStrings[0]);
						String [] danceString = new String[1];
						danceString[0] = player.getKnownDances().get(iSelection);
						this.handleStartDance(null, player.getID(),danceString);
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_START_MUSIC_NO_PARAMS: {
						
						int iSelection = Integer.parseInt(sReceivedStrings[0]);
						String [] musicString = new String[1];
						musicString[0] = player.getKnownMusic().get(iSelection);
						this.handleStartMusic(null, player.getID(),musicString);
						player.removePendingSUIWindow(window);
						break;
					} 
					case Constants.SUI_SELECT_LOCATION_TO_CLONE: {
						// They've selected a cloning facility;
						System.out.print("Cloning the player");
						Vector<MapLocationData> cloneMapLocations = player.getLastSeenCloneLocations();
						MapLocationData theLocation = cloneMapLocations.elementAt(Integer.parseInt(sReceivedStrings[0]));
						TravelDestination T = new TravelDestination("Cloning", theLocation.getPlanetID(), 0, false,theLocation.getCurrentX(),theLocation.getCurrentY(),0.0f);
						System.out.println(" at " + Constants.PlanetNames[theLocation.getPlanetID()] + ", X["+theLocation.getCurrentX() + "], Y["+theLocation.getCurrentY()+"]");
						int[] iHamWounds = player.getHamWounds();
						int[] iCurrentHam = player.getCurrentHam();
						int[] iMaxHam = player.getMaxHam();
						for (int i = 0; i < iHamWounds.length; i++) {
							iHamWounds[i]+=100; // Since we got a pointer to the array, this should update the values in the Player as well.
							iHamWounds[i] = Math.min(iHamWounds[i], iMaxHam[i] - 1);
							iCurrentHam[i] = iMaxHam[i] - iHamWounds[i];
						}
						player.updateBattleFatigue(100, false);
						player.setStance(null,Constants.STANCE_STANDING, true);
						player.playerTravel(T);
						break;
					}
					
					case Constants.SUI_RENAME_STRUCTURE: {
						SOEObject o = window.getOriginatingObject();
						if(o instanceof Terminal)
						{
							Terminal t = (Terminal)o;
							t.renameTerminal(player.getClient(),sReceivedStrings[3]);
						}
						if(o instanceof Structure)
						{
							Structure s = (Structure)o;
							s.setStructureName(sReceivedStrings[3]);
						}
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_CONFIRM_DESTROY_STRUCTURE: {
						Structure s = (Structure)window.getOriginatingObject();
						int iReceivedCode = Integer.parseInt(sReceivedStrings[3]);
						if(iReceivedCode == s.getIDestructionCode())
						{
							Deed d = (Deed)server.getObjectFromAllObjects(s.getDeedID());
							client.insertPacket(PacketFactory.buildUpdateContainmentMessage(player, null, -1));
							if(s.canRedeed())
							{
								//s.setIMaintenancePool(s.getIMaintenancePool() - 800);
								d.redeedStructure(s, client);
							} else {
								d.destroyStructure(s, client);
							}
						}
						else
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("Code did not match, Structure destruction aborted"));
						}
						player.removePendingSUIWindow(window);
						break;
					}
					case Constants.SUI_STRUCTURE_ADD_PLAYER_TO_ADMIN: {
					
						//3 is new admin name
						if(!sReceivedStrings[3].isEmpty())
						{
							
							Player newAdmin = server.getPlayer(sReceivedStrings[3].toLowerCase());
							if(newAdmin!=null)
							{
								Structure s = (Structure)window.getOriginatingObject();
								if(s.addToAdminList(newAdmin.getID()))
								{
									s.updateStructureCellPermissions(player.getClient());
									client.insertPacket(PacketFactory.buildChatSystemMessage(newAdmin.getFullName() + " Added to the Admin List"));
								}
							}
							else
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage("Player " + sReceivedStrings[3] + " not found"));
							}
							player.removePendingSUIWindow(window);
						}
						break;
					}
					case Constants.SUI_STRUCTURE_ADD_PLAYER_TO_ENTRY:
					{
						//3 is new entry name
						if(!sReceivedStrings[3].isEmpty())
						{
							SUIWindow W = player.getPendingSUIWindow(suiWindowID);
							Player newEntry = server.getPlayer(sReceivedStrings[3].toLowerCase());
							if(newEntry!=null)
							{
								Structure s = (Structure)W.getOriginatingObject();
								if(s.addToEntryList(newEntry.getID()))
								{
									s.updateStructureCellPermissions(player.getClient());
									client.insertPacket(PacketFactory.buildChatSystemMessage(newEntry.getFullName() + " Added to the Entry List"));
								}
							}
							else
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage("Player " + sReceivedStrings[3] + " not found"));
							}
							player.removePendingSUIWindow(W);
						}
						break;
					}
					case Constants.SUI_STRUCTURE_ADD_PLAYER_TO_BAN: {
					
						//3 is new ban name
						if(!sReceivedStrings[3].isEmpty())
						{
							
							Player newBan = server.getPlayer(sReceivedStrings[3].toLowerCase());
							if(newBan!=null)
							{
								Structure s = (Structure)window.getOriginatingObject();
								if(s.addtoBanList(newBan.getID(),client))
								{
									s.updateStructureCellPermissions(client);
									client.insertPacket(PacketFactory.buildChatSystemMessage(newBan.getFullName() + " Added to the Banned List"));
								}
							}
							else
							{
								client.insertPacket(PacketFactory.buildChatSystemMessage("Player " + sReceivedStrings[3] + " not found"));
							}
							player.removePendingSUIWindow(window);
						}
						break;
					}
					case Constants.SUI_RENAME_ITEM: 
					{
						if(!sReceivedStrings[3].isEmpty())
						{
							TangibleItem t = (TangibleItem)window.getOriginatingObject();
							t.renameItem(sReceivedStrings[3], player.getClient());
						}
						else
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("Item Not Renamed."));
						}
						player.removePendingSUIWindow(window);
						break;
					} 
					case Constants.SUI_BANK_WINDOW: {
						int iToInventory = Integer.parseInt(sSelectionIDString);
						int iToBankCredits  = Integer.parseInt(suiWindowTitleString);
						String DepositStr = "";
						int DepositAmount = 0;
						if(player.getBankCredits() > iToBankCredits )
						{
							//witdrawal
							DepositStr = "prose_withdraw_success";
							DepositAmount = player.getBankCredits() - iToBankCredits;
							player.setBankCredits(iToBankCredits);
							player.setInventoryCredits(iToInventory);
	
							player.updateCredits();
							client.insertPacket(PacketFactory.buildChatSystemMessage(
									"base_player",
									DepositStr,
									0,
									null,
									null,
									null,
									0,
									null,
									null,
									null,
									0,
									null,
									null,
									null,
									DepositAmount,
									0f,
									true
							));
	
							//client.insertPacket(PacketFactory.buildFlyTextSTFMessage(p, "base_player", DepositStr, "", "", DepositAmount));
							player.removePendingSUIWindow(suiWindowID);
							return;
						}
						else if(player.getInventoryCredits() > iToInventory )
						{
							DepositStr = "prose_deposit_success";
							DepositAmount = player.getInventoryCredits() - iToInventory;
							player.setBankCredits(iToBankCredits);
							player.setInventoryCredits(iToInventory);
	
							player.updateCredits();
							client.insertPacket(PacketFactory.buildChatSystemMessage(
									"base_player",
									DepositStr,
									0,
									null,
									null,
									null,
									0,
									null,
									null,
									null,
									0,
									null,
									null,
									null,
									DepositAmount,
									0f,
									true
							));
	
							//client.insertPacket(PacketFactory.buildFlyTextSTFMessage(p, "base_player", DepositStr, "", "", DepositAmount));
							player.removePendingSUIWindow(suiWindowID);
							return;
						}
						client.insertPacket(PacketFactory.buildChatSystemMessage(
								"base_player",
								"prose_withdraw_success",
								0,
								null,
								null,
								null,
								0,
								null,
								null,
								null,
								0,
								null,
								null,
								null,
								0,
								0f,
								true
						));
						break;
							//client.insertPacket(PacketFactory.buildFlyTextSTFMessage(p, "base_player", "prose_withdraw_success", "", "", 0));
	
	
					}
					case Constants.SUI_STRUCTURE_PAY_MAINTENANCE: {
						if(!suiWindowTitleString.isEmpty() && !sSelectionIDString.contains("-1"))
						{
							int iToStructure = Integer.parseInt(suiWindowTitleString);
							SUIWindow W = player.getPendingSUIWindow(suiWindowID);
							Structure s = (Structure)W.getOriginatingObject();
							if(player.debitCredits(iToStructure))
							{
								client.insertPacket(s.addToMaintenancePool(iToStructure, true), Constants.PACKET_RANGE_CHAT_RANGE);
								client.insertPacket(PacketFactory.buildChatSystemMessage("You successfully pay " + iToStructure + " credits to " + s.getStructureName() + "."));
							}
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
					}
					case Constants.SUI_STRUCTURE_PAY_POWER:	{
						//int iInvPower = Integer.parseInt(sReceivedStrings[0]);
						int iToStructure = Integer.parseInt(sReceivedStrings[1]);
						int iToDeduct  = Integer.parseInt(sReceivedStrings[1]);
						Structure s = (Structure)window.getOriginatingObject();
	
						Vector<ResourceContainer> vRCList = new Vector<ResourceContainer>();
						Vector<TangibleItem> vInventoryItems = player.getInventoryItems();
						for(int i =0; i < vInventoryItems.size();i++)
						{
							TangibleItem o = vInventoryItems.elementAt(i);
							if(o instanceof ResourceContainer)
							{
								vRCList.add((ResourceContainer)o);
							}
						}
						boolean powerdeducted = false;
						int iPowerOnHand = 0;
						double iDeduct = 0;
						if(vRCList.isEmpty())
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have any power in your inventory."));
						}
						else
						{
	
							for(int i=0; i < vRCList.size();i++)
							{
								ResourceContainer r = vRCList.get(i);
								SpawnedResourceData rd = server.getResourceManager().getResourceByID(r.getResourceSpawnID());
								if(rd.getResourceType().contains("Radioactive"))
								{
									if(rd.getPotentialEnergy() >= 650)
									{
										iPowerOnHand += (r.getStackQuantity() * 5) / 2;
									}
									else
									{
										iPowerOnHand += (r.getStackQuantity() * 195) / 100;
									}
									if(iToDeduct < iPowerOnHand)
									{
										iDeduct = 0;
										if(rd.getPotentialEnergy() >= 650)
										{
											iDeduct = (iToDeduct / 5) * 2;
										}
										else
										{
											iDeduct = (iToDeduct / 195) * 100;
										}
										iToDeduct -= iDeduct;                                                    
										client.insertPacket(r.setStackQuantity((r.getStackQuantity() - (int)iDeduct), true));
										client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), r));
									}
									else if(iToDeduct >= iPowerOnHand)
									{
										iToDeduct -= iPowerOnHand;
										player.removeItemFromInventory(r);
										player.despawnItem(r);
										server.removeObjectFromAllObjects(r,false);
									}
									iPowerOnHand = 0;
								}
								else if(rd.getResourceType().contains("Solar"))
								{
									if(rd.getPotentialEnergy() >= 950)
									{
										iPowerOnHand += (r.getStackQuantity() * 3) / 2;
									}
									else
									{
										iPowerOnHand += r.getStackQuantity();
									}
									if(iToDeduct < iPowerOnHand)
									{
										iDeduct = 0;
										if(rd.getPotentialEnergy() >= 950)
										{
											iDeduct = (iToDeduct / 3) * 2;
										}
										else
										{
											iDeduct = iToDeduct;
										}
										iToDeduct -= iDeduct;
										client.insertPacket(r.setStackQuantity((r.getStackQuantity() - (int)iDeduct), true));
										client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), r));
									}
									else if(iToDeduct >= iPowerOnHand)
									{
										iToDeduct -= iPowerOnHand;
										player.removeItemFromInventory(r);
										player.despawnItem(r);
										server.removeObjectFromAllObjects(r,false);
									}
									iPowerOnHand = 0;
								}
								else if(rd.getResourceType().contains("Wind"))
								{
									iPowerOnHand += r.getStackQuantity();
	
									if(iToDeduct < iPowerOnHand)
									{
										iDeduct = 0;
										iDeduct = iToDeduct;
										iToDeduct -= iDeduct;
										client.insertPacket(r.setStackQuantity((r.getStackQuantity() - (int)iDeduct), true));
										client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), r));
									}
									else if(iToDeduct >= iPowerOnHand)
									{
										iToDeduct -= iPowerOnHand;
										player.removeItemFromInventory(r);
										player.despawnItem(r);
										server.removeObjectFromAllObjects(r,false);
									}
									iPowerOnHand = 0;
								}
								if(iToDeduct <= 0)
								{
									i = vRCList.size() +1;
									powerdeducted = true;
								}
							}
	
						}
						if(powerdeducted)
						{
							s.addToPowerPool(iToStructure);
							client.insertPacket(PacketFactory.buildChatSystemMessage("Power Reseves now at " + s.getPowerPool()));
						}
						else
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("Error Power Resevers now at " + s.getPowerPool() + " Contact a CSR"));
							DataLog.logEntry("Error While depositing power to a Structure.", "ZoneClientThread",Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
						}
						player.removePendingSUIWindow(window);
						break;
					}
					// Cancel variable:
					// 0 = OK
					// -1 = CANCEL
	
					case Constants.SUI_TIP_WINDOW:
					{
						if(cancel == 0)
						{   //System.out.println("Handling ScriptmessageBox Ok Clicked");
							SOEObject oo = window.getOriginatingObject();
							if(oo instanceof TipObject)
							{
								TipObject t = (TipObject)oo;
								t.sendTipEmails();
							}
						} else {
							// We don't care.
						}
						
						break;
					}
					case Constants.SUI_TRADE_WINDOW: {
						TradeObject t = (TradeObject)window.getOriginatingObject();
						if (cancel == 0) {
							t.setTradeStatus(Constants.TRADE_STATUS_RECIPIENT_CONSENT);
	
						} else {
							if(t.getOriginator().getTradeObjectByRequestID(t.getITradeRequestID()).equals(t))
							{
								player.removeTradeRequest(t);
								t.getOriginator().getClient().insertPacket(PacketFactory.buildChatSystemMessage(t.getRecipient().getFirstName() + " has declined your trade request."));
								client.insertPacket(PacketFactory.buildChatSystemMessage("Trade request declined."));
							}
						}
						break;
					}
					case Constants.SUI_LEARN_OFFERED_SKILL: {
						if (cancel == 0) {
							Skills skillToLearn = player.getSkillOfferedByTeacher();
							byte trainStatus = player.getPlayData().canTrainSkill(skillToLearn);
							if (trainStatus == 0) { // It should always be 0 here.
								player.setSkill(skillToLearn.getSkillID(), true, true);
							player.setSkillOfferedByTeacher(null);
							Player teacher = player.getTeacher();
							// "You learn %to from %tt
							client.insertPacket(PacketFactory.buildChatSystemMessage("teaching", 
									"student_skill_learned", 
									0l, 
									"", 
									"",
									"", 
									teacher.getID(), 
									teacher.getSTFFileName(), 
									teacher.getSTFFileIdentifier(), 
									teacher.getFirstName(), 
									0l, 
									"skl_n", 
									skillToLearn.getName(),
									"",
									0, 
									0f, false));                                			
	
							// %TT learns %TO from you
							teacher.getClient().insertPacket(PacketFactory.buildChatSystemMessage("teaching", 
									"teacher_skill_learned", 
									0l, 
									"", 
									"",
									"", 
									player.getID(), 
									player.getSTFFileName(), 
									player.getSTFFileIdentifier(), 
									player.getFirstName(), 
									0l, 
									"skl_n", 
									skillToLearn.getName(),
									"",
									0, 
									0f, false));                                			
							teacher.updateExperience(null, 1, skillToLearn.getPointsCost() * 10);
							player.setSkillOfferedByTeacher(null);
							player.setTeacher(null);
							teacher.setStudent(null);
							}
						}
						else if(cancel == 1)
						{
							Player teacher = player.getTeacher();
							teacher.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
									"teaching", 
									"offer_refused", 
									0l, 
									"",
									"",
									"",
									player.getID(),
									player.getSTFFileName(),
									player.getSTFFileIdentifier(), 
									player.getFirstName(),
									0l,
									"", 
									"", 
									"",
									0,
									0f, false
							)
							);
							player.setSkillOfferedByTeacher(null);
							player.setTeacher(null);
							teacher.setStudent(null);
						}
						player.removePendingSUIWindow(suiWindowID);
						break;
	
					}
					case Constants.SUI_FACTORY_UPDATE_INSTALLED_SCHEMATIC: {
						Factory factory = (Factory)window.getOriginatingObject();
						Vector<ManufacturingSchematic> vSchematics = player.getSchematicsForFactory(factory.getFactoryType());
						int iSelection = Integer.parseInt(sSelectionIDString);
						ManufacturingSchematic currentSchematic = factory.getCurrentSchematic();
						
						if (iSelection == vSchematics.size()) {
							// Remove current schematic.
							if (currentSchematic != null) {
								client.insertPacket(currentSchematic.setContainer(player.getDatapad(), -1, true));
								player.getDatapad().addIntangibleObject(currentSchematic);
								factory.setCurrentSchematic(null);
								client.insertPacket(PacketFactory.buildChatSystemMessage("manf_station", "schematic_removed", 0, null, null, null, currentSchematic.getID(), Constants.STRING_ID_TABLE, null, null, 0, null, null, null, 0, 0f, false));
							} 
						} else {
							// Install new schematic.
							if (currentSchematic != null) {
								client.insertPacket(currentSchematic.setContainer(player.getDatapad(), -1, true));
								client.insertPacket(PacketFactory.buildChatSystemMessage("manf_station", "schematic_removed", 0, null, null, null, currentSchematic.getID(), Constants.STRING_ID_TABLE, null, null, 0, null, null, null, 0, 0f, false));
							}
							ManufacturingSchematic schematicToInstall = vSchematics.elementAt(iSelection);
							factory.setCurrentSchematic(schematicToInstall);
							player.getDatapad().removeIntangibleObject(schematicToInstall);
							client.insertPacket(schematicToInstall.setContainer(factory, 5, true));
							client.insertPacket(PacketFactory.buildChatSystemMessage("manf_station", "schematic_added", 0, null, null, null, schematicToInstall.getID(), Constants.STRING_ID_TABLE, null, null, 0, null, null, null, 0, 0f, false));
						}
						break;
					}
                                        case Constants.SUI_FROG_SELECT_WEAPON: {
						int iCategorySelected = Integer.parseInt(sSelectionIDString);
						//String sCategoryString = DatabaseInterface.getArmorSet().get(iCategorySelected);
						Vector<ItemTemplate> vDT = DatabaseInterface.getItemTemplateWeaponGroup(iCategorySelected);
						for(int i = 0; i < vDT.size();i++)
						{
							ItemTemplate T = vDT.get(i);
							// System.out.println("Template ID Selected: " + T.getTemplateID() + " IFF: " + T.getIFFFileName());
							Weapon I = new Weapon();
							I.setTemplateID(T.getTemplateID());
							I.setID(client.getServer().getNextObjectID());
							I.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
							I.setConditionDamage(0, false);
							I.setMaxCondition(1000, false);
                                                        I.setMinDamage(T.getMinDmg());
							I.setMaxDamage(T.getMaxDmg());
                                                        I.setRefireDelay(T.getRefireDelay());
                                                        I.setWeaponType(T.getWeaponType());
                                                        I.setHealthCost(T.getHealthCost());
                                                        I.setActionCost(T.getActionCost());
                                                        I.setMindCost(T.getMindCost());
							I.setOwner(player);
							player.addItemToInventory(I);
							I.setEquipped(player.getInventory(),Constants.EQUIPPED_STATE_UNEQUIPPED);
							client.getServer().addObjectToAllObjects(I, false, false);
							player.spawnItem(I);
						}
						client.insertPacket(PacketFactory.buildChatSystemMessage("Weapon Set Given"));
						player.removePendingSUIWindow(suiWindowID);
						return;
					}
					default: {
						System.out.println("Unknown window type " + window.getWindowType());
						player.clearSUIListWindowObjectList();
						player.removePendingSUIWindow(suiWindowID);
						
					}
				}
			}
		}catch(Exception e){
			System.out.println("Exception in: handleSuiEventNotification " + e);
			e.printStackTrace();
		}
	}

	protected void handleBoardTransport(long targetID,String [] Parameters){
		//this routine has to handle parameters.
		// This would be identical to handling using a ticket collector.
		System.out.println("Need Code put in for board transport");
		
		
	}

	// NAMING CONVENTIONS!!!  ALL VARIABLES START WITH A LOWER CASE LETTER!
	protected void handleEjectRequest(long targetID,String [] Parameters) {
		try{
			if(player.getCellID() >= 1)
			{
				//player is in cell we tell him were going to eject him.
				client.insertPacket(PacketFactory.buildChatSystemMessage("Processing Eject Request"));
				Cell CurrentCellObject = (Cell)server.getObjectFromAllObjects(player.getCellID());
				if(CurrentCellObject != null)
				{
					SOEObject Building = CurrentCellObject.getBuilding();
					if(Building != null )
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("Ejecting"));
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(player, null, -1));
						if(Building instanceof Structure)
						{
							Structure s = (Structure)Building;
							player.playerWarp(s.getStructureSign().getX(), s.getStructureSign().getY(),0, 0,s.getStructureSign().getPlanetID());
						}
						else
						{
							player.playerWarp(Building.getX() + 30, Building.getY() + 30, Building.getZ(), 0, Building.getPlanetID());
						}

						return;
					}
				}

				client.insertPacket(PacketFactory.buildChatSystemMessage("Location not found Warping to Theed Starport"));
				TravelDestination T = server.getTravelDestinationFromName("Theed Starport", Constants.NABOO);
				player.playerTravel(T);
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You are not inside a Building or Structure."));
			}
		}catch(Exception e){
			System.out.println("Exception caught in handleEjectRequest " + e);
			e.printStackTrace();
		}
	}

	protected void handleUnstickRequest(long targetID, String [] Parameters){
		try{
			if(player.getCellID() == 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Processing Unstick Request"));
				player.playerWarp(player.getX() + 30, player.getY() + 30, 0, 0, player.getPlanetID());
				return;
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You are not outside."));
			}

		}catch(Exception e){
			System.out.println("Exception caught in handleUnstickRequest " + e);
			e.printStackTrace();
		}
	}

	protected void handleMoveItem(long targetID,String [] Parameters){
		try{
			SOEObject moveItem = server.getObjectFromAllObjects(targetID);
			if(!(moveItem instanceof TangibleItem))
			{
				return;
			}
			Cell c = null;
			Structure s = null;
			if(moveItem.getCellID()!=0)
			{
				c = (Cell)server.getObjectFromAllObjects(moveItem.getCellID());
				if(c!=null)
				{
					s = c.getBuilding();
					if(s!=null)
					{
						if(!s.isAdmin(player.getID()))
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("You must be structure Admin to move items."));
							return;
						}
					}
				}
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot move world objects."));
				return;
			}
			if(moveItem!=null)
			{
				if(Parameters[0].contentEquals("up"))
				{

					float up = (Float.parseFloat(Parameters[1]) / 10);
					if(up > 0)
					{                                
						moveItem.setZ(moveItem.getZ() + up);    
						moveItem.setCellZ(moveItem.getCellZ() + up);
						if(moveItem.getCellID() == 0)
						{
							client.insertPacket(PacketFactory.buildNPCUpdateTransformMessage(moveItem));
						}
						else
						{
							client.insertPacket(PacketFactory.buildNPCUpdateCellTransformMessage(moveItem,c));
						}
						client.insertPacket(PacketFactory.buildChatSystemMessage("Item Moved"));
						return;
					}
					client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Move Up Amount, must be positive and greater than 0."));
				}
				else if(Parameters[0].contentEquals("down"))
				{

					float down = (Float.parseFloat(Parameters[1]) / 10);
					if(down > 0)
					{                                
						moveItem.setZ(moveItem.getZ() - down);           
						moveItem.setCellZ(moveItem.getCellZ() - down);
						if(moveItem.getCellID() == 0)
						{
							client.insertPacket(PacketFactory.buildNPCUpdateTransformMessage(moveItem));
						}
						else
						{
							client.insertPacket(PacketFactory.buildNPCUpdateCellTransformMessage(moveItem,c));
						}
						client.insertPacket(PacketFactory.buildChatSystemMessage("Item Moved"));
						System.out.println("New Item X:" + moveItem.getZ() + " Y:" + moveItem.getY() + " Z:" + moveItem.getZ());
						return;
					}
					client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Move Down Amount, must be positive and greater than 0."));
				}
				else if(Parameters[0].contentEquals("forward"))
				{
					/*
					 * This Math Code Courtesy Of TaSwavo - Implementation PlasmaFlow
					 * Sets X and Y of object
					 * when moved a specified distance in a specified direction
					 *
					 * @param distance
					 * @param angle (in radians)
					 */
					System.out.println("Current Item X:" + moveItem.getX() + " Y:" + moveItem.getY() + " CellX:" + moveItem.getCellX() + " CellY:" + moveItem.getCellY());
					float distance = (Float.parseFloat(Parameters[1]) / 10);
					float angle =  player.getMovementAngle();
					float forwardX, forwardY;
					forwardY = ( distance * ((float)Math.cos((double)angle)) );
					forwardX = ( distance * ((float)Math.sin((double)angle)) );

					moveItem.setCellX( moveItem.getCellX() + forwardX );
					moveItem.setCellY( moveItem.getCellY() + forwardY ); 

					switch(s.getIFacingDirection())
					{
					case 0: // +++
					{                                                                                                
						moveItem.setX( s.getX() + moveItem.getCellX() );
						moveItem.setY( s.getY() + moveItem.getCellY());    
						break;
					}
					case 1: //+-+
					{                                       
						moveItem.setX( s.getX() + moveItem.getCellX() );
						moveItem.setY( s.getY() - moveItem.getCellY());    
						break;
					}
					case 2://--+
					{                                       
						moveItem.setX( s.getX() - moveItem.getCellX() );
						moveItem.setY( s.getY() - moveItem.getCellY());    
						break;
					}
					case 3://-++
					{                                     
						moveItem.setX( s.getX() - moveItem.getCellX() );
						moveItem.setY( s.getY() + moveItem.getCellY());    
						break;
					}
					}                            
					if(moveItem.getCellID() == 0)
					{
						client.insertPacket(PacketFactory.buildNPCUpdateTransformMessage(moveItem));
					}
					else
					{
						client.insertPacket(PacketFactory.buildNPCUpdateCellTransformMessage(moveItem,c));
					}
					client.insertPacket(PacketFactory.buildChatSystemMessage("Item Moved"));
					System.out.println("New Item X:" + moveItem.getX() + " Y:" + moveItem.getY() + " CellX:" + moveItem.getCellX() + " CellY:" + moveItem.getCellY());
					return;

				}
				else if(Parameters[0].contentEquals("back"))
				{
					/*
					 * This Math Code Courtesy Of TaSwavo - Implementation PlasmaFlow
					 * Sets X and Y of object
					 * when moved a specified distance in the opposite of a specified direction
					 *
					 * @param distance
					 * @param angle (in radians)
					 */
					System.out.println("Current Item X:" + moveItem.getX() + " Y:" + moveItem.getY() + " CellX:" + moveItem.getCellX() + " CellY:" + moveItem.getCellY());
					float distance = (Float.parseFloat(Parameters[1]) / 10);
					float angle = player.getMovementAngle();

					float forwardX, forwardY;

					if( angle < Math.PI )
					{
						angle += Math.PI;
					}
					else
					{
						angle -= Math.PI;
					}
					forwardY = ( distance * ((float)Math.cos((double)angle)) );
					forwardX = ( distance * ((float)Math.sin((double)angle)) );
					moveItem.setCellX( moveItem.getCellX() + forwardX );
					moveItem.setCellY( moveItem.getCellY() + forwardY ); 
					switch(s.getIFacingDirection())
					{
					case 0: // +++
					{                                              
						moveItem.setX( s.getX() + moveItem.getCellX() );
						moveItem.setY( s.getY() + moveItem.getCellY());    
						break;
					}
					case 1: //+-+
					{                                       
						moveItem.setX( s.getX() + moveItem.getCellX() );
						moveItem.setY( s.getY() - moveItem.getCellY());    
						break;
					}
					case 2://--+
					{                                       
						moveItem.setX( s.getX() - moveItem.getCellX() );
						moveItem.setY( s.getY() - moveItem.getCellY());    
						break;
					}
					case 3://-++
					{                                       
						moveItem.setX( s.getX() - moveItem.getCellX() );
						moveItem.setY( s.getY() + moveItem.getCellY());    
						break;
					}
					}

					if(moveItem.getCellID() == 0)
					{
						client.insertPacket(PacketFactory.buildNPCUpdateTransformMessage(moveItem));
					}
					else
					{
						client.insertPacket(PacketFactory.buildNPCUpdateCellTransformMessage(moveItem,c));
					}
					client.insertPacket(PacketFactory.buildChatSystemMessage("Item Moved"));
					System.out.println("New Item X:" + moveItem.getX() + " Y:" + moveItem.getY() + " CellX:" + moveItem.getCellX() + " CellY:" + moveItem.getCellY());
					return;
				}

			}
		}catch(Exception e){
			System.out.println("Eception caught in handleMoveItem " + e);
			e.printStackTrace();
		}
	}
	protected void handleRotateItem(long targetID,String [] Parameters){
		System.out.println("Rotate Item: " + targetID);
		for(int i =0; i < Parameters.length;i++)
		{
			System.out.println("Parameter " + i + " : " + Parameters[i]);
		}
	}
	protected void handleDropSkill(long targetID, String[] sParams) {
		String skillToDrop = sParams[0];
		int skillID = server.getSkillIndexFromName(skillToDrop);
		player.setSkill(skillID, false, true);
	}

	protected void handlePayMaintenance(long targetID, String[] sParams) {
		//System.out.print("handlePayMaintenance T:" + targetID + " AMT: " + sParams[0]);
		try{
			SOEObject o = server.getObjectFromAllObjects(player.getTargetID());
			if(o instanceof Structure)
			{
				Structure s = (Structure)o;
				if(s.isAdmin(player.getID()))
				{
					int payAmount = Integer.parseInt(sParams[0]);
					if(player.hasEnoughCredits(payAmount))
					{
						player.debitCredits(payAmount);
						client.insertPacket(s.addToMaintenancePool(payAmount, true), Constants.PACKET_RANGE_CHAT_RANGE);
						client.insertPacket(PacketFactory.buildChatSystemMessage("You successfully make a payment of " + payAmount + " credits to " + s.getStructureName()));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Maintenance pool now at " + s.getMaintenancePool() + " credits."));
					}
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must be structure admin to do that."));
				}

			}
			else if(o instanceof Terminal)
			{
				Terminal t = (Terminal)o;
				Structure s = (Structure)server.getObjectFromAllObjects(t.getParentID());
				if(s.isAdmin(player.getID()))
				{
					int payAmount = 0;
					try {
						payAmount = Integer.parseInt(sParams[0]);
					} catch (Exception e) {
						client.insertPacket(PacketFactory.buildChatSystemMessage("Error reading amount of maintenance to add."));
					}
					if(player.hasEnoughCredits(payAmount))
					{
						player.debitCredits(payAmount);
						client.insertPacket(s.addToMaintenancePool(payAmount, true), Constants.PACKET_RANGE_CHAT_RANGE);
						client.insertPacket(PacketFactory.buildChatSystemMessage("You successfully make a payment of " + payAmount + " credits to " + s.getStructureName()));
						client.insertPacket(PacketFactory.buildChatSystemMessage("Maintenance pool now at " + s.getMaintenancePool() + " credits."));
					}
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must be structure admin to do that."));
				}
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("That is not a valid target for pay maintenance"));
			}

		}catch(Exception e){
			System.err.println("Exception caught in ZoneClientThread.handlePayMaintenance " + e);
			e.printStackTrace();
		}
	}

	protected void handleDestroyStructure(long targetID, String[] sParams) {
		try{
			String StructureName = "";
			for(int i = 0; i < sParams.length;i++)
			{
				if(!sParams[i].isEmpty())
				{
					StructureName += sParams[i] + " ";
				}
			}
			StructureName = StructureName.trim();

			if(StructureName!=null && !StructureName.isEmpty())
			{
				//means we got a name and were gonna find the structure by name
				ConcurrentHashMap <Long,Structure> vPS = player.getAllPlayerStructures();
				Enumeration <Structure> sEnum = vPS.elements();
				long sID = 0;
				while(sEnum.hasMoreElements())
				{
					Structure f = sEnum.nextElement();
					System.out.println(f.getStructureName());
					if(f.getStructureName().equals(StructureName))
					{
						sID = f.getID();
					}
				}
				Structure s = vPS.get(sID);
				if(s!=null && (s.getStructureOwnerID() == player.getID() || bIsDeveloper || bIsGM))
				{
					//we found the structure by name
					Terminal T = s.getAdminTerminal();
					T.useItem(client, (byte)128);
					return;
				}
				else if(s!=null && s.getStructureOwnerID() != player.getID())
				{
					//this will probably never happen since we look for the structure in the players structures.
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must be the Structure Owner to do that."));
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Structure not found by name."));
				}
			}
			else if(targetID != 0)
			{
				//means we received a target id and we do this by the target id
				SOEObject o = server.getObjectFromAllObjects(targetID);
				Structure s = null;
				if(o instanceof Terminal)
				{
					Terminal sign = (Terminal)o;
					s = (Structure)client.getServer().getObjectFromAllObjects(sign.getParentID());
					Terminal T = s.getAdminTerminal();
					T.useItem(client, (byte)128);
					return;
				}
				else if(o instanceof Structure)
				{
					s = (Structure)o;
					Terminal T = s.getAdminTerminal();
					T.useItem(client, (byte)128);
					return;
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Structure not found."));
				}
				if(s!=null && (s.getStructureOwnerID() == player.getID() || bIsDeveloper || bIsGM))
				{

				}
				else if(s.getStructureOwnerID() != player.getID())
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must be the Structure Owner to do that."));
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Structure not found."));
				}
			}
			else if(player.getTargetID() != 0)
			{
				//means we have a player targeting a structure which we will destory if he is the owner
				targetID = player.getTargetID();
				SOEObject o = server.getObjectFromAllObjects(targetID);
				Structure s = null;
				if(o instanceof Terminal)
				{
					Terminal sign = (Terminal)o;
					s = (Structure)client.getServer().getObjectFromAllObjects(sign.getParentID());
					Terminal T = s.getAdminTerminal();
					T.useItem(client, (byte)128);
					return;
				}
				else if(o instanceof Structure)
				{
					s = (Structure)o;
					s.useItem(client, (byte)207);
					return;
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Structure not found."));
				}
				if(s!=null && (s.getStructureOwnerID() == player.getID() || bIsDeveloper || bIsGM))
				{

				}
				else if(s.getStructureOwnerID() != player.getID())
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must be the Structure Owner to do that."));
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Structure not found."));
				}
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You must have a structure targeted and be the structure owner to do that."));
			}
		} catch(Exception e) {
			try{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Error Processing Destroystructure command."));
			}catch(Exception ee){
				//DOH!
			}
			System.out.println("Exception caught in ZoneClientThread.handleDestroyStructure " + e);
			e.printStackTrace();
		}
	}

	protected void handleTip(long targetID, String[] sParams) {
		try{


			if(sParams[0].isEmpty())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Usage: /tip <amount> or /tip <amount> <player name> or /tip <amount> <player name> bank"));
				return;
			}

			int iTipAmount = 0;
			String sTipRecipientName = "";
			boolean bank = false;

			switch(sParams.length)
			{
			case 1:// /tip <amount>
			{
				iTipAmount = Integer.parseInt(sParams[0]);
				if(targetID == 0)
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Target for /tip"));
					client.insertPacket(PacketFactory.buildChatSystemMessage("Usage: /tip <amount> or /tip <amount> <player name> or /tip <amount> <player name> bank"));
					return;
				}
				Player recipient = server.getPlayer(targetID);
				if(recipient == null)
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Target for /tip"));
					return;
				}
				if(recipient.getOnlineStatus())
				{
					//if player is online we proceed with the tip
					if(player.hasEnoughInventoryCredits(iTipAmount))
					{
						if(player.debitInventoryCredits(iTipAmount))
						{
							recipient.creditInventoryCredits(iTipAmount);
							client.insertPacket(PacketFactory.buildChatSystemMessage("You tip " + recipient.getFirstName() + " " + iTipAmount + " credits."));
							recipient.getClient().insertPacket(PacketFactory.buildChatSystemMessage(player.getFirstName() + " tips you " + iTipAmount + " credits."));
						}
					}
					else
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("You lack the funds to tip " + PacketUtils.toProper(sTipRecipientName) + " " + iTipAmount + " credits."));
					}
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage( PacketUtils.toProper(sTipRecipientName) + " is not online."));
				}
				break;
			}
			case 2: // /tip <amount> <player name>
			{
				iTipAmount = Integer.parseInt(sParams[0]);
				sTipRecipientName = sParams[1].toLowerCase();
				if(sTipRecipientName.contentEquals("bank") && targetID != 0)
				{
					bank = true;
					sTipRecipientName = server.getPlayer(targetID).getFirstName().toLowerCase();
				}
				else if(sTipRecipientName.contentEquals("bank") && targetID == 0)
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Target for /tip"));
					client.insertPacket(PacketFactory.buildChatSystemMessage("Usage: /tip <amount> or /tip <amount> <player name> or /tip <amount> <player name> bank"));
					return;
				}
				Player recipient = server.getPlayer(sTipRecipientName);
				if(recipient == null)
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage( PacketUtils.toProper(sTipRecipientName) + " not found."));
					return;
				}
				if(recipient.getOnlineStatus())
				{
					//if player is online we proceed with the tip
					if(player.hasEnoughInventoryCredits(iTipAmount) && !bank)
					{
						if(player.debitInventoryCredits(iTipAmount))
						{
							recipient.creditInventoryCredits(iTipAmount);
							client.insertPacket(PacketFactory.buildChatSystemMessage("You tip " + PacketUtils.toProper(sTipRecipientName) + " " + iTipAmount + " credits."));
							recipient.getClient().insertPacket(PacketFactory.buildChatSystemMessage(player.getFirstName() + " tips you " + iTipAmount + " credits."));
						}
						return;
					}
					else if(player.hasEnoughCredits((int)((iTipAmount) * .05) + iTipAmount) && bank) // Avoid the typecasting.  Lowest Common Denominator.
						// else if (player.hasEnoughCredits((iTipAmount * 21) / 20) && bank) {
					{                                    
						//send bank tip
						TipObject t = new TipObject(server);
						t.setITipAmount(iTipAmount);
						t.setPlayer(player);
						t.setRecipient(recipient);
						SUIWindow W = new SUIWindow(player);
						W.setWindowType(Constants.SUI_TIP_WINDOW);
						W.setOriginatingObject(t);
						String WindowTypeString = "handleWireConfirm";
						String WindowTitle = "@base_player:tip_wire_title";//"Bank Tip Surcharge";
						String WindowPromptContent = "@base_player:tip_wire_prompt";//"A 5% Surcharge will be applied to this bank transfer. Select ok to proceed with the Tip or cancel to stop it.";
						boolean bEnableCancel = true;
						boolean bEnableRevert = false;                                    
						client.insertPacket(W.SUIScriptMessageBox(client, WindowTypeString, WindowTitle, WindowPromptContent, bEnableCancel, bEnableRevert, 0, 0));
					}
					else
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("You lack the funds to tip " + PacketUtils.toProper(sTipRecipientName) + " " + iTipAmount + " credits."));
						return;
					}
				}
				else if(bank)
				{
					//tip via bank the player is not online
					TipObject t = new TipObject(server);
					t.setITipAmount(iTipAmount);
					t.setPlayer(player);
					t.setRecipient(recipient);
					SUIWindow W = new SUIWindow(player);
					W.setWindowType(Constants.SUI_TIP_WINDOW);
					W.setOriginatingObject(t);
					String WindowTypeString = "handleWireConfirm";
					String WindowTitle = "@base_player:tip_wire_title";//"Bank Tip Surcharge";
					String WindowPromptContent = "@base_player:tip_wire_prompt";//"A 5% Surcharge will be applied to this bank transfer. Select ok to proceed with the Tip or cancel to stop it.";
					boolean bEnableCancel = true;
					boolean bEnableRevert = false;                                    
					client.insertPacket(W.SUIScriptMessageBox(client, WindowTypeString, WindowTitle, WindowPromptContent, bEnableCancel, bEnableRevert, 0, 0));
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage( PacketUtils.toProper(sTipRecipientName) + " is not online."));
				}
				break;
			}
			case 3: //tip <amount> <player name> <bank>
			{
				iTipAmount = Integer.parseInt(sParams[0]);
				sTipRecipientName = sParams[1].toLowerCase();
				Player recipient = server.getPlayer(sTipRecipientName);
				if(recipient == null)
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage( PacketUtils.toProper(sTipRecipientName) + " not found."));
					break;
				}
				bank = sParams[2].contains("bank");
				if(!bank)
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Usage: /tip <amount> or /tip <amount> <player name> or /tip <amount> <player name> bank"));
					return;
				}
				TipObject t = new TipObject(server);
				t.setITipAmount(iTipAmount);
				t.setPlayer(player);
				t.setRecipient(recipient);
				SUIWindow W = new SUIWindow(player);
				W.setOriginatingObject(t);
				String WindowTypeString = "handleWireConfirm";
				String WindowTitle = "@base_player:tip_wire_title";//"Bank Tip Surcharge";
				String WindowPromptContent = "@base_player:tip_wire_prompt";//"A 5% Surcharge will be applied to this bank transfer. Select ok to proceed with the Tip or cancel to stop it.";
				boolean bEnableCancel = true;
				boolean bEnableRevert = false;                                    
				client.insertPacket(W.SUIScriptMessageBox(client, WindowTypeString, WindowTitle, WindowPromptContent, bEnableCancel, bEnableRevert, 0, 0));

				break;
			}
			}

		}catch(NumberFormatException nfe){
			try{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Amount"));
				client.insertPacket(PacketFactory.buildChatSystemMessage("Usage: /tip <amount> or /tip <amount> <player name> or /tip <amount> <player name> bank"));
			}catch(Exception ee){ //DOH!
			}
		}
		catch(Exception e){
			System.out.println("Exception caught in ZoneClientThread.handleTip " + e);
			e.printStackTrace();

		}
	}



	private void handleSecureTrade(SOEInputStream dIn) throws IOException {
		/**
		 * The packet seems the same if you do a trade or drag an item to the player
		 * This is what we receive from the requester of a trade             
		 * 
		 *  05 00 
                46 5E CE 80 
                23 00 00 00 
                15 01 00 00 //trade request
                28 12 5C 14 06 00 00 00 //zelmak <---Packet Starts Here, This is US!
                00 00 00 00 
                00 00 00 00 <-- Obj Controller Command CRC 
                00 00 00 00 00 00 00 00 
                64 EB 81 0C 26 00 00 00 //estiwe <---Trade Recipient

		 * this is what we send to the recipient
		 *  05 00 
                46 5E CE 80 
                0B 00 00 00 
                15 01 00 00 
                28 12 5C 14 06 00 00 00 ////zelmak receiving
                00 00 00 00 
                01 00 00 00 <-- Obj Controller Command CRC 
                64 EB 81 0C 26 00 00 00 //estiwe 163418598244 originating
                    28 12 5C 14 06 00 00 00 //zel receiving
		 * 
		 * 05 00 
		 * 46 5E CE 80 
		 * 23 00 00 00 
		 * 15 01 00 00 
		 * 3D 3A 21 5E 00 00 00 00 
		 * 00 00 00 00 
		 * 02 00 00 00 
		 * 00 00 00 00 00 00 00 00 
		 * 00 00 00 00 00 00 00 00 
		 * F3 D4  
		 */
		//byte [] pkt = dIn.getBuffer();
		// PacketUtils.printPacketToScreen(pkt, pkt.length,"Trade request Handler");
		System.out.println("handleSecureTrade");
		long lOriginatorID = dIn.readLong();
		dIn.readInt();
		int iTradeCommand = dIn.readInt();

		/*long lTemp1 = */dIn.readLong();
		long lTargetID= dIn.readLong();
		switch(iTradeCommand)
		{
		case 0: //trade Request Requester to Recipient
		{
			Player tradeRecipient = server.getPlayer(lTargetID);
			TradeObject t = new TradeObject(server);
			t.setOriginator(player);
			t.setRecipient(tradeRecipient);
			t.setTradeStatus(Constants.TRADE_STATUS_ASKING_FOR_CONSENT);
			t.setITradeRequestID(1);
			t.setRandomInt(SWGGui.getRandomInt(100));
			player.addTradeRequest(t);
			tradeRecipient.addIncomingTradeRequest(lOriginatorID);
			tradeRecipient.getClient().insertPacket(PacketFactory.buildObjectControllerTradeRequestToPlayer(t));
			break;

		}
		case 2:
		{
			Player tradeOriginator = server.getPlayer(player.getIncomingTradeRequesterID());
			TradeObject t = tradeOriginator.getTradeObjectByRecipientID(player.getID());
			player.setCurrentTradeObject(t);
			tradeOriginator.setCurrentTradeObject(t);
			t.setTradeStatus(Constants.TRADE_STATUS_RECIPIENT_CONSENT);
			t.notifyTradeBegin();                    
			break;
		}
		default:
		{
			//just in case it happens but this should not happen
			byte [] packet = dIn.getBuffer();
			PacketUtils.printPacketToScreen(packet, packet.length, "Trade Request Command Was not 0");
		}
		}

	}

	protected void handleDiagnoseWounds(long targetID, String[] sParams) throws IOException{
		if (targetID == 0 || targetID == player.getID()) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("med_tool", "other_players_only"));
			return;
		}

		Player target = (Player)server.getPlayer(targetID);
		if (target != null) {
			
			
			int[] targetWounds = target.getHamWounds();
			SUIWindow window = new SUIWindow(player);
			window.setWindowType(Constants.SUI_MEDICAL_DIAGNOSE_PLAYER_WOUNDS);
			window.setOriginatingObject(player);
			String[] sList = new String[targetWounds.length + 1];
			for (int i = 0; i < targetWounds.length; i++) {
				sList[i] = Constants.HAM_NAMES[i] + ": " + targetWounds[i];
			}
			sList[sList.length - 1] = "Battle Fatigue: " + target.getBattleFatigue();
			String WindowTypeString = "handleSUI";
			String DataListTitle = "cmd_n:diagnose";
			String DataListPrompt = target.getFirstName() + "'s wounds";
			client.insertPacket(window.SUIScriptListBox(client, WindowTypeString, DataListTitle, DataListPrompt, sList, null, targetID, player.getID()));
		}

	}

	protected void handleHealDamage(CommandQueueItem action, long targetID, String[] sParams) throws IOException {
		Player tarPlayer = null;
		if (targetID != 0) {
			tarPlayer = server.getPlayer(targetID);
		} else {
			tarPlayer = player;
		}
		if (tarPlayer.getClient() == null) {
			// Null client -- must be trying to heal an NPC or someone inactive.
			return;
		}

		long lHealDelay = player.getNextHealDelay();
		if (lHealDelay > 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("healing_response", "healing_must_wait", 0l, "", "", "", 0l, "", "", "",0l, "", "", "",0, 0f, false));
			return;
		}
		String hamBarToHeal = sParams[0];
		// Maybe the person is a programmer, and they gave us the ham bar index.
		int iHamBarIndex = -1;
		if (hamBarToHeal != null && !(hamBarToHeal.equals(""))) {
            try {
                iHamBarIndex = Integer.parseInt(hamBarToHeal);
            } catch (NumberFormatException e) {
                // Obviously a String
                if (hamBarToHeal.toLowerCase().equals("health")) {
                    iHamBarIndex = Constants.HAM_INDEX_HEALTH;
                } else if (hamBarToHeal.toLowerCase().equals("action")) {
                    iHamBarIndex = Constants.HAM_INDEX_ACTION;
                } else if (hamBarToHeal.toLowerCase().equals("mind")){
                    System.out.println("Player cannot /healDamage mind");
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot heal that attribute!"));
                    return;
                    //iHamBarIndex = Constants.HAM_INDEX_MIND;
                } else {
                    System.out.println("Player trying to tend damage to secondary or unknown ham bar " + hamBarToHeal);
                    return;
                }
            }
		} else {
			// Find the first damaged one.
            int tarCurrentHealth = tarPlayer.getCurrentHam()[Constants.HAM_INDEX_HEALTH];
            int tarMaxHealth = tarPlayer.getMaxHam()[Constants.HAM_INDEX_HEALTH];
            int tarCurrentAction = tarPlayer.getCurrentHam()[Constants.HAM_INDEX_ACTION];
            int tarMaxAction = tarPlayer.getMaxHam()[Constants.HAM_INDEX_ACTION];
            if(tarCurrentHealth < tarMaxHealth && tarCurrentAction < tarMaxAction) {
                if(tarCurrentHealth < tarCurrentAction) {
                    iHamBarIndex = Constants.HAM_INDEX_HEALTH;
                } else if(tarCurrentAction < tarCurrentHealth) {
                    iHamBarIndex = Constants.HAM_INDEX_ACTION;
                } else { //Maybe they are equal?
                    iHamBarIndex = Constants.HAM_INDEX_HEALTH;
                }
            } else if(tarCurrentHealth < tarMaxHealth) {
                iHamBarIndex = Constants.HAM_INDEX_HEALTH;
            } else if(tarCurrentAction < tarMaxAction) {
                iHamBarIndex = Constants.HAM_INDEX_ACTION;
            } else {
                System.out.println("No damage to heal.");
                return;
            }

		}

		int indexOffset = iHamBarIndex % 3;
		iHamBarIndex -= indexOffset;  // Force us to heal Health, Action or Mind.
		if (iHamBarIndex < 0 || iHamBarIndex >= Constants.NUM_PLAYER_HAMS) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid ham index: " + iHamBarIndex));
			return;
		}
		SkillMods medicalSkillMod = player.getSkillMod("healing_ability");
		if (medicalSkillMod == null || medicalSkillMod.getSkillModModdedValue() == 0) {
			//client.insertPacket(PacketFactory.buildChatSystemMessage("You have insufficient skills to tend damage."));
			return;
		}
		int iMedicalSkillModValue = medicalSkillMod.getSkillModModdedValue();
		int tarCurrentHam = tarPlayer.getCurrentHam()[iHamBarIndex];
		int maxHam = tarPlayer.getMaxHam()[iHamBarIndex];
		int hamWounds = tarPlayer.getHamWounds()[iHamBarIndex];
		int hamModifiers = tarPlayer.getHamModifiers()[iHamBarIndex];
		int totalMaxHam = maxHam + hamModifiers - hamWounds;

		if (tarCurrentHam >= totalMaxHam) {
			client.insertPacket(PacketFactory.buildChatSystemMessage(tarPlayer.getFirstName() + " has no " + hamBarToHeal + " damage to heal."));
			return;
		}
		int maxHeal = totalMaxHam - tarCurrentHam;
		int hamDamageToHeal = Math.min(SWGGui.getRandomInt(1, iMedicalSkillModValue) * SWGGui.getRandomInt(1, 40), maxHeal);
		tarPlayer.updateCurrentHam(iHamBarIndex, hamDamageToHeal);
		player.updateCurrentHam(Constants.HAM_INDEX_MIND, -100);
		String healMessage = "You have healed " + hamDamageToHeal + " damage.";
		String tarPlayerHealMessage = player.getFirstName()+" heals you for " + hamDamageToHeal + " damage.";
		client.insertPacket(PacketFactory.buildChatSystemMessage(healMessage));
		tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage(tarPlayerHealMessage));
		player.updateExperience(null, 32, (hamDamageToHeal * 2));
		SkillMods healingSpeedMod = player.getSkillMod("healing_injory_speed");
		int modValue = 0;
		if (healingSpeedMod != null) {
			modValue = healingSpeedMod.getSkillModModdedValue();
		}
		player.setNextHealDelay(modValue);
		action.setCommandTimer(modValue);
	}

	protected void handleHealWound(CommandQueueItem action, long targetID, String[] sParams) throws IOException {
		Player tarPlayer = null;
		boolean healSecondaryBar = false;
		if (targetID != 0) {
			tarPlayer = server.getPlayer(targetID);
		} else {
			tarPlayer = player;
		}
		if (tarPlayer.getClient() == null) {
			// Null client -- must be trying to heal an NPC or someone inactive.
			return;
		}
		long lHealDelay = player.getNextHealDelay();
		if (lHealDelay > 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("healing_response", "healing_must_wait", 0l, "", "", "", 0l, "", "", "",0l, "", "", "",0, 0f, false));
			return;
		}
		String hamBarToHeal = sParams[0];
		// Maybe the person is a programmer, and they gave us the ham bar index.
		int iHamBarIndex = -1;
		if (hamBarToHeal != null && !(hamBarToHeal.equals(""))) {
            try {
                iHamBarIndex = Integer.parseInt(hamBarToHeal);
            } catch (NumberFormatException e) {
                // Obviously a String
                String hamBarToHealLC = hamBarToHeal.toLowerCase();
                if (hamBarToHealLC.equals("health")) {
                    iHamBarIndex = Constants.HAM_INDEX_HEALTH;
                } else if (hamBarToHealLC.equals("action")) {
                    iHamBarIndex = Constants.HAM_INDEX_ACTION;
                } else if (hamBarToHealLC.equals("mind")){
                    System.out.println("Player cannot /healWound mind");
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot heal that attribute!"));
                    return;
                    //iHamBarIndex = Constants.HAM_INDEX_MIND;
                } else if (hamBarToHealLC.equals("strength")){
                    iHamBarIndex = Constants.HAM_INDEX_STRENGTH;
                } else if (hamBarToHealLC.equals("stamina")) {
                    iHamBarIndex = Constants.HAM_INDEX_STAMINA;
                } else if (hamBarToHealLC.equals("focus")) {
                    System.out.println("Player cannot /healWound focus");
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot heal that attribute!"));
                    return;
                    //iHamBarIndex = Constants.HAM_INDEX_FOCUS;
                } else if (hamBarToHealLC.equals("willpower")) {
                    System.out.println("Player cannot /healWound willpower");
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot heal that attribute!"));
                    return;
                    //iHamBarIndex = Constants.HAM_INDEX_WILLPOWER;
                } else if (hamBarToHealLC.equals("constitution")) {
                    iHamBarIndex = Constants.HAM_INDEX_CONSTITUTION;
                } else if (hamBarToHealLC.equals("quickness")) {
                    iHamBarIndex = Constants.HAM_INDEX_QUICKNESS;
                } else {
                    System.out.println("Player trying to tend damage to unknown ham bar " + hamBarToHeal);
                    return;
                }
            }
        } else {
            // Find the first damaged one
            int tarCurrentHighestWound = 0;
            for(int i = 0; i < tarPlayer.getHamWounds().length; i++) {
                if(i != 6 && i != 7 && i != 8) { //Prevents the loop running on Mind, Focus, Willpower
                    if(tarCurrentHighestWound < tarPlayer.getHamWounds()[i]) {
                        tarCurrentHighestWound = tarPlayer.getHamWounds()[i];
                        iHamBarIndex = i;
                    }
                }
            }
        }
		healSecondaryBar = (iHamBarIndex % 3 != 0);
		int playerWounds = tarPlayer.getHamWounds()[iHamBarIndex];
		if (playerWounds == 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage(tarPlayer.getFirstName() + " has no " + hamBarToHeal +" wounds to heal."));
			return;
		} else {
			// Go ahead and heal them.
			SkillMods healSkillMod = player.getSkillMod("healing_wound_treatment");
			if (healSkillMod == null || healSkillMod.getSkillModModdedValue() == 0) {
				//client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have sufficient skills to /tendWound"));
				return;
			} else {
				int iHealSkillModValue = healSkillMod.getSkillModModdedValue();
				int hamWoundsToHeal = Math.min(SWGGui.getRandomInt(1, iHealSkillModValue) * SWGGui.getRandomInt(1, 10), playerWounds);
				int xpToGrant = ((hamWoundsToHeal * 5) / 2);
				String healMessage = "You have healed " + hamWoundsToHeal + " wounds.";
				String tarPlayerHealMessage = player.getFirstName() + " heals " + hamWoundsToHeal + " of your wounds.";
				tarPlayer.updateHAMWounds(iHamBarIndex, -hamWoundsToHeal, true);
				if (healSecondaryBar) {
					tarPlayer.updateCurrentHam(iHamBarIndex, hamWoundsToHeal);
				}
				player.updateCurrentHam(Constants.HAM_INDEX_MIND, -150);
				client.insertPacket(PacketFactory.buildChatSystemMessage(healMessage));
				tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage(tarPlayerHealMessage));
				player.updateExperience(null, 32, xpToGrant);
				SkillMods healingSpeedMod = player.getSkillMod("healing_injory_speed");
				int modValue = 0;
				if (healingSpeedMod != null) {
					modValue = healingSpeedMod.getSkillModModdedValue();
				}
				player.setNextHealDelay(modValue);
				action.setCommandTimer(modValue);

			}
		}
		//} else {
		// Find the first damaged one.

		//}


	}

	protected void handleTendDamage(CommandQueueItem action, long targetID, String[] sParams) throws IOException {
		Player tarPlayer = null;
		if (targetID != 0) {
			tarPlayer = server.getPlayer(targetID);
		} else {
			tarPlayer = player;
		}
		if (tarPlayer.getClient() == null) {
			// Null client -- must be trying to heal an NPC or someone inactive.
			return;
		}        	long lHealDelay = player.getNextHealDelay();
		if (lHealDelay > 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("healing_response", "healing_must_wait", 0l, "", "", "", 0l, "", "", "",0l, "", "", "",0, 0f, false));
			return;
		}

		String hamBarToHeal = sParams[0];
		// Maybe the person is a programmer, and they gave us the ham bar index.
		int iHamBarIndex = -1;
		if (hamBarToHeal != null && !(hamBarToHeal.equals(""))) {
            try {
                iHamBarIndex = Integer.parseInt(hamBarToHeal);
            } catch (NumberFormatException e) {
                // Obviously a String
                if (hamBarToHeal.toLowerCase().equals("health")) {
                    iHamBarIndex = Constants.HAM_INDEX_HEALTH;
                } else if (hamBarToHeal.toLowerCase().equals("action")) {
                    iHamBarIndex = Constants.HAM_INDEX_ACTION;
                } else if (hamBarToHeal.toLowerCase().equals("mind")){
                    //iHamBarIndex = Constants.HAM_INDEX_MIND;
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot heal that attribute!"));
                    System.out.println("Player trying to tendDamage mind");
                    return;
                } else {
                    System.out.println("Player trying to tend damage to secondary or unknown ham bar " + hamBarToHeal);
                    return;
                }
            }
		} else {
            // Find the first damaged one.
            int tarCurrentHealth = tarPlayer.getCurrentHam()[Constants.HAM_INDEX_HEALTH];
            int tarMaxHealth = tarPlayer.getMaxHam()[Constants.HAM_INDEX_HEALTH];
            int tarCurrentAction = tarPlayer.getCurrentHam()[Constants.HAM_INDEX_ACTION];
            int tarMaxAction = tarPlayer.getMaxHam()[Constants.HAM_INDEX_ACTION];
            if(tarCurrentHealth < tarMaxHealth && tarCurrentAction < tarMaxAction) {
                if(tarCurrentHealth < tarCurrentAction) {
                    iHamBarIndex = Constants.HAM_INDEX_HEALTH;
                } else if(tarCurrentAction < tarCurrentHealth) {
                    iHamBarIndex = Constants.HAM_INDEX_ACTION;
                } else { //Maybe they are equal?
                    iHamBarIndex = Constants.HAM_INDEX_HEALTH;
                }
            } else if(tarCurrentHealth < tarMaxHealth) {
                iHamBarIndex = Constants.HAM_INDEX_HEALTH;
            } else if(tarCurrentAction < tarMaxAction) {
                iHamBarIndex = Constants.HAM_INDEX_ACTION;
            } else {
                System.out.println("No damage to tend.");
                return;
            }
		}

		int indexOffset = iHamBarIndex % 3;
		iHamBarIndex -= indexOffset;  // Force us to heal Health, Action or Mind.
		if (iHamBarIndex < 0 || iHamBarIndex >= Constants.NUM_PLAYER_HAMS) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid ham index: " + iHamBarIndex));
			return;
		}
		SkillMods medicalSkillMod = player.getSkillMod("healing_ability");
		if (medicalSkillMod == null || medicalSkillMod.getSkillModModdedValue() == 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("You have insufficient skills to tend damage."));
			return;
		}
		int iMedicalSkillModValue = medicalSkillMod.getSkillModModdedValue();
		int tarCurrentHam = tarPlayer.getCurrentHam()[iHamBarIndex];
		int maxHam = tarPlayer.getMaxHam()[iHamBarIndex];
		int hamWounds = tarPlayer.getHamWounds()[iHamBarIndex];
		int hamModifiers = tarPlayer.getHamModifiers()[iHamBarIndex];
		int totalMaxHam = maxHam + hamModifiers - hamWounds;

		if (tarCurrentHam >= totalMaxHam) {
			client.insertPacket(PacketFactory.buildChatSystemMessage(tarPlayer.getFirstName() + " has no " + hamBarToHeal + " damage to heal."));
			return;
		}
		int maxHeal = totalMaxHam - tarCurrentHam;
		int hamDamageToHeal = Math.min(SWGGui.getRandomInt(1, iMedicalSkillModValue) * SWGGui.getRandomInt(1, 40), maxHeal);
		tarPlayer.updateCurrentHam(iHamBarIndex, hamDamageToHeal);
		player.updateCurrentHam(Constants.HAM_INDEX_MIND, -100);
		String healMessage = "You have healed " + hamDamageToHeal + " damage.";
		String tarPlayerHealMessage = player.getFirstName()+" heals you for " + hamDamageToHeal + " damage.";
		client.insertPacket(PacketFactory.buildChatSystemMessage(healMessage));
		tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage(tarPlayerHealMessage));
		player.updateExperience(null, 32, (hamDamageToHeal * 2));
		SkillMods healingSpeedMod = player.getSkillMod("healing_injory_speed");
		int modValue = 0;
		if (healingSpeedMod != null) {
			modValue = healingSpeedMod.getSkillModModdedValue();
		}
		player.setNextHealDelay(modValue);
		action.setCommandTimer(modValue);
		
	}

	protected void handleTendWound(CommandQueueItem action, long targetID, String[] sParams) throws IOException {
		Player tarPlayer = null;
		boolean healSecondaryBar = false;
		if (targetID != 0) {
			tarPlayer = server.getPlayer(targetID);
		} else {
			tarPlayer = player;
		}
		if (tarPlayer.getClient() == null) {
			// Null client -- must be trying to heal an NPC or someone inactive.
			return;
		}
		long lHealDelay = player.getNextHealDelay();
		if (lHealDelay > 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("healing_response", "healing_must_wait", 0l, "", "", "", 0l, "", "", "",0l, "", "", "",0, 0f, false));
			return;
		}

		String hamBarToHeal = sParams[0];
		// Maybe the person is a programmer, and they gave us the ham bar index.
		int iHamBarIndex = -1;
		if (hamBarToHeal != null && !(hamBarToHeal.equals(""))) {
            try {
                iHamBarIndex = Integer.parseInt(hamBarToHeal);
            } catch (NumberFormatException e) {
                // Obviously a String
                String hamBarToHealLC = hamBarToHeal.toLowerCase();
                if (hamBarToHealLC.equals("health")) {
                    iHamBarIndex = Constants.HAM_INDEX_HEALTH;
                } else if (hamBarToHealLC.equals("action")) {
                    iHamBarIndex = Constants.HAM_INDEX_ACTION;
                } else if (hamBarToHealLC.equals("mind")){
                    System.out.println("Player cannot /tendWound mind");
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot heal that attribute!"));
                    return;
                    //iHamBarIndex = Constants.HAM_INDEX_MIND;
                } else if (hamBarToHealLC.equals("strength")){
                    iHamBarIndex = Constants.HAM_INDEX_STRENGTH;
                } else if (hamBarToHealLC.equals("stamina")) {
                    iHamBarIndex = Constants.HAM_INDEX_STAMINA;
                } else if (hamBarToHealLC.equals("focus")) {
                    System.out.println("Player cannot /tendWound focus");
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot heal that attribute!"));
                    return;
                    //iHamBarIndex = Constants.HAM_INDEX_FOCUS;
                } else if (hamBarToHealLC.equals("willpower")) {
                    System.out.println("Player cannot /tendWound willpower");
                    client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot heal that attribute!"));
                    return;
                    //iHamBarIndex = Constants.HAM_INDEX_WILLPOWER;
                } else if (hamBarToHealLC.equals("constitution")) {
                    iHamBarIndex = Constants.HAM_INDEX_CONSTITUTION;
                } else if (hamBarToHealLC.equals("quickness")) {
                    iHamBarIndex = Constants.HAM_INDEX_QUICKNESS;
                }
            }
        } else {
            // Find the first damaged one.
            int tarCurrentHighestWound = 0;
            for(int i = 0; i < tarPlayer.getHamWounds().length; i++) {
                if(i != 6 && i != 7 && i != 8) { //Prevents the loop running on Mind, Focus, Willpower
                    if(tarCurrentHighestWound < tarPlayer.getHamWounds()[i]) {
                        tarCurrentHighestWound = tarPlayer.getHamWounds()[i];
                        iHamBarIndex = i;
                    }
                }
            }
        }
		healSecondaryBar = (iHamBarIndex % 3 != 0);
		int playerWounds = tarPlayer.getHamWounds()[iHamBarIndex];
		if (playerWounds == 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage(tarPlayer.getFirstName() + " has no " + hamBarToHeal +" wounds to heal."));
			return;
		} else {
			// Go ahead and heal them.
			SkillMods healSkillMod = player.getSkillMod("healing_wound_treatment");
			if (healSkillMod == null || healSkillMod.getSkillModModdedValue() == 0) {
				//client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have sufficient skills to /tendWound"));
				return;
			} else {
				int iHealSkillModValue = healSkillMod.getSkillModModdedValue();
				int hamWoundsToHeal = Math.min(SWGGui.getRandomInt(1, iHealSkillModValue) * SWGGui.getRandomInt(1, 10), playerWounds);
				int xpToGrant = ((hamWoundsToHeal * 5) / 2);
				String healMessage = "You have healed " + hamWoundsToHeal + " wounds.";
				String tarPlayerHealMessage = player.getFirstName() + " heals " + hamWoundsToHeal + " of your wounds.";
				tarPlayer.updateHAMWounds(iHamBarIndex, -hamWoundsToHeal, true);
				if (healSecondaryBar) {
					tarPlayer.updateCurrentHam(iHamBarIndex, hamWoundsToHeal);
				}
				player.updateCurrentHam(Constants.HAM_INDEX_MIND, -150);
				client.insertPacket(PacketFactory.buildChatSystemMessage(healMessage));
				tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage(tarPlayerHealMessage));
				player.updateExperience(null, 32, xpToGrant);
				SkillMods healingSpeedMod = player.getSkillMod("healing_injury_speed");
				int modValue = 0;
				if (healingSpeedMod != null) {
					modValue = healingSpeedMod.getSkillModModdedValue();
				}
				player.setNextHealDelay(modValue);
				action.setCommandTimer(modValue);

			}
		}
		//} else {
		// Find the first damaged one.

		//}

	}

	protected void handleGroupInvite(long targetID, String [] sParam){
		/**
		 *  -----> Print Packet To Screen. <-----
                -----> CommandQueueEnqueue <-----
                -----> Len:52 <-----
                <------------------------------------------------>
                00 09 00 11 
		 *  05 00 
		 *  46 5E CE 80 
		 *  23 00 00 00 
		 *  16 01 00 00 
		 *  61 77 80 72 00 00 00 00 <--Host ID
		 *  00 00 00 00 
		 *  00 00 00 00 
		 *  58 5D 50 88 
		 *  8E 8A CA B1 00 00 00 00 <--Guest ID
		 *  00 00 00 00 
		 *  7C ED  
                <------------------------------------------------> */
		for(int i = 0; i < sParam.length; i++)
		{
			System.out.println("Param " + i + " : " + sParam[i]);
		}
		try{
			if(targetID == 0 && sParam[0].isEmpty())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Group Invite Target."));
				return;
			}
			else if(sParam[0].length() >= 3)
			{
				Player tp = server.getPlayer(sParam[0]);
				if(tp==null)
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Group Invite Target."));
					return;
				}
				targetID = tp.getID();
				if(ZoneServer.getRangeBetweenObjects(player, tp) > 128)
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage(tp.getFirstName() + " is too far to invite."));
					return;
				}
			}
			else if(targetID == 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Group Invite Target."));
				return;
			}

			if(targetID == player.getID())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot invite your self to a group."));
				return;
			}
			//lets check if this player is member of a group, if he is we needto check to see he is leader before invite is sent.
			if(player.getGroupID()!=0)
			{
				Group g = (Group)server.getObjectFromAllObjects(player.getGroupID());
				if(player.getID() != g.getGroupLeaderID())
				{
					// "group", "must_be_leader"
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"group",
							"must_be_leader",
							0l, 
							"",
							"",
							"",
							0l, 
							"",
							"",
							"",
							0l,
							"",
							"",
							"",
							0,
							0f, false));

					return;
				}
			}

			SOEObject o = server.getObjectFromAllObjects(targetID);     

			if(o instanceof Terminal)
			{
				Terminal t = (Terminal)o;
				client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot invite " + t.getFullName() + " to join your group."));                    
			}
			else if(o instanceof CreatureAnimal)
			{
				CreatureAnimal c = (CreatureAnimal)o;
				if(c.isPet())
				{

				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot invite that."));                    
				}
			}
			else if(o instanceof Vehicle)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot invite that."));                    
			}
			else if(o instanceof Player)
			{
				Player p = (Player)o;                    
				if(p.getGroupID() == 0)
				{
					if(p.getGroupHost() == 0)
					{
						p.setGroupHost(player.getID());
						p.getClient().insertPacket(PacketFactory.buildDeltasMessageGroupInvite(Constants.BASELINES_CREO, (byte)6,(short)1,(short)7, p,player));


						// Invite target message.  %TT invites you to join a group.  Type /join to accept.
						p.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
								"group",
								"invite_target",
								0l, 
								"",
								"",
								"",
								player.getID(), 
								player.getSTFFileName(),
								player.getSTFFileIdentifier(),
								player.getFirstName(),
								0l,
								"",
								"",
								"",
								0,
								0f, false));
						// You invite %TT to join your group.
						client.insertPacket(PacketFactory.buildChatSystemMessage(
								"group",
								"invite_leader",
								0l, 
								"",
								"",
								"",
								p.getID(), 
								p.getSTFFileName(),
								p.getSTFFileIdentifier(),
								p.getFirstName(),
								0l,
								"",
								"",
								"",
								0,
								0f, false));
					}
					else
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage(p.getFirstName() + " is considering another group."));
					}
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage(p.getFirstName() + " is already in a Group."));
				}
			}
			else if(o instanceof CreaturePet)
			{
				//need to work this out later for pets and droids
				System.out.println("Invitation Request to an unhandled object class: " + o.getClass() + " " + o.getID());
			}
			else
			{
				System.out.println("Invitation Request to an unhandled object class: " + o.getClass() + " " + o.getID());
			}

		}catch(Exception e){
			System.out.println("Exception caught in ZoneClientThread.handleGroupInvite " + e);
			e.printStackTrace();
		}
	}

	protected void handleAcceptGroupInvite(long targetID, String [] sParam){
		try{
			System.out.println("handleAcceptGroupInvite TID: " + targetID);
			if(player.getGroupHost() == 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You must first be invited to join a group."));
				return;
			}
			if(player.getGroupID() != 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You are already in a group."));
				return;
			}
			Player groupHost = server.getPlayer(player.getGroupHost());

			if(groupHost.getGroupID() == 0)
			{
				//make a new group since there was none
				System.out.println("New Group to be made.");
				Group g = new Group(groupHost);                      
				g.addMemberToGroup(groupHost);//<--Leader has to be added first.
				g.addMemberToGroup(player);
				player.setGroupHost(0);
				client.insertPacket(PacketFactory.buildDeltasMessageGroupInvite(Constants.BASELINES_CREO, (byte)6,(short)1,(short)7, player,groupHost));
				groupHost.setGroupHost(0);
			}
			else if(groupHost.getGroupID() != 0)
			{
				System.out.println("Existing Group add new member");
				Group g = (Group)server.getObjectFromAllObjects(groupHost.getGroupID());                    
				if(g.getGroupLeaderID() == groupHost.getID())
				{
					g.addMemberToGroup(player);
					player.setGroupHost(0);
					client.insertPacket(PacketFactory.buildDeltasMessageGroupInvite(Constants.BASELINES_CREO, (byte)6,(short)1,(short)7, player,groupHost));
				}
			}
		}catch(Exception e){
			System.out.println("Exception caught in ZoneClientThread.handleAcceptGroupInvite " + e);
			e.printStackTrace();
		}
	}

	protected void handleDeclineGroupInvite(long targetID, String [] sParam){
		try{
			if(player.getGroupHost() == 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You have not been invited to join a group."));
				return;
			}
			Player groupHost = server.getPlayer(player.getGroupHost());
			groupHost.getClient().insertPacket(PacketFactory.buildChatSystemMessage(player.getFirstName() + " declines to join your group."));
			player.setGroupHost(0);
			client.insertPacket(PacketFactory.buildDeltasMessageGroupInvite(Constants.BASELINES_CREO, (byte)6,(short)1,(short)7, player,groupHost));                    
		}catch(Exception e){
			System.out.println("Exception caught in ZoneClientThread.handleDeclineGroupInvite " + e);
			e.printStackTrace();
		}
	}

	protected void handleLeaveGroup(long targetID, String [] sParam){
		try{
			if(player.getGroupID() == 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You are not in a group."));
				return;
			}
			Group g = (Group)server.getObjectFromAllObjects(player.getGroupID());
			g.removeMemberFromGroup(player,Constants.GROUP_REMOVE_REASON_LEAVE);
		}catch(Exception e){
			System.out.println("Exception caught in ZoneClientThread.handleLeaveGroup() " + e);
			e.printStackTrace();
		}
	}

	protected void handleDisbandGroup(long targetID, String [] sParam){
		try{
			if(player.getGroupID() == 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You are not in a group."));
				return;
			}
			Group g = (Group)server.getObjectFromAllObjects(player.getGroupID());
			if(g.getGroupLeader().equals(player))
			{
				g.disbandGroup();
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You must be Group Leader to Disband."));
			}
		}catch(Exception e){
			System.out.println("Exception Caught in ZoneClientThread.handleDisbandGroup() " + e);
			e.printStackTrace();
		}
	}

	protected void handleMakeGroupLeader(long targetID, String [] sParam){
		try{


			Group g = (Group)server.getObjectFromAllObjects(player.getGroupID());
			if(g.getGroupLeader().equals(player))
			{
				SOEObject o = server.getObjectFromAllObjects(targetID);
				if(o instanceof Player)
				{
					Player p = (Player)o;
					System.out.println("Making new Group Leader " + p.getFullName());
					g.setGroupLeader(p);
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You can only make a Player Leader."));
				}
			}
			else
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("You must be `Group Leader to Make Leader."));
			}
		}catch(Exception e){
			System.out.println("Exception Caught in ZoneClientThread");
		}
	}

	private void handleRequestDraftComponentMessage(long targetID, String[] sParams) throws IOException{
		for (int i = 0; i < sParams.length; i++) {
			BitSet schematicBits = player.getPlayData().getSchematics();
			boolean bFound = false;
			CraftingSchematic schematic = null;
			int schematicCRC = (int)Long.parseLong(sParams[i]);
			for (int j = schematicBits.nextSetBit(0); j >= 0 && !bFound; j = schematicBits.nextSetBit(j+1)) {
				schematic = DatabaseInterface.getSchematicByIndex(j);
				if (schematic.getCRC() == schematicCRC) {
					bFound = true;
					client.insertPacket(PacketFactory.buildObjectController_DraftSchematicComponentMessage(player, schematic, schematic.getComponents()));
				}
			}
		}
	}

	private void handleRequestResourceWeightMessage(long targetID, String[] sParams) {
		System.out.println("Number of resource weights requested: " + sParams.length);
		for (int i = 0; i < sParams.length; i++) {
			BitSet schematicBits = player.getPlayData().getSchematics();
			boolean bFound = false;
			CraftingSchematic schematic = null;
			int schematicCRC = (int)Long.parseLong(sParams[i]);
			for (int j = schematicBits.nextSetBit(0); j >= 0 && !bFound; j = schematicBits.nextSetBit(j+1)) {
				schematic = DatabaseInterface.getSchematicByIndex(j);
				if (schematic.getCRC() == schematicCRC) {
					bFound = true;
					//client.insertPacket(PacketFactory.buildObjectController_DraftSchematicComponentMessage(player, schematic, null));
					try {
						client.insertPacket(PacketFactory.buildObjectController_ResourceWeightsMessage(player, schematic));
						//client.insertPacket(PacketFactory.buildObjectController_ResourceWeightsMessage_HardCode(player, schematic));
						//return; // Why are you here?
					} catch (Exception e) {
						// D'oh!
					}
				}
			}
			if (!bFound) {
				System.out.println("Unknown schematic with CRC " + Long.toHexString(schematicCRC));
			}

		}
	}

	private void handleSetSpokenLanguage(long playerID, String[] sParams) {
		//System.out.println("Handling set spoken language.");
		try {
			int spokenLanguage = Integer.parseInt(sParams[0]);
			//System.out.println("Language ID: " + spokenLanguage);
			player.getPlayData().setCurrentLanguageID(client, spokenLanguage);
		} catch (Exception e) {
			System.out.println("Error setting spoken language: " + e.toString());
			e.printStackTrace();
		}
	}

	protected void handleSelectStartingLocation(long targetID, String[] sParams) {
		try{
			for(int i = 0; i < DatabaseInterface.getStartingLocations().size(); i++)
			{
				StartingLocation L = DatabaseInterface.getStartingLocations().get(i);
				if(L.getCityName().compareTo(sParams[0]) == 0)
				{
					player.setStartingCoordinates(L.getX(), L.getY());
					TutorialObject to = player.getTutorial();
					to.setHasCompleted(true);
					Cell cc = to.getCell(player.getCellID());
					if (cc != null) {
						cc.removeCellObject(player);
					}
					TravelDestination D = new TravelDestination(L.getCityName(),L.getPlanetID(),-1,false,L.getX()+ SWGGui.getRandomInt(0,3),L.getY()+ SWGGui.getRandomInt(0,3),L.getZ());
					player.setCellID(0);
					Waypoint startingWaypoint = new Waypoint();
					startingWaypoint.setX(L.getX());
					startingWaypoint.setY(L.getY());
					startingWaypoint.setZ(L.getZ());
					startingWaypoint.setIsActivated(true);
					startingWaypoint.setName("Starting Location");
					startingWaypoint.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
					startingWaypoint.setPlanetCRC(Constants.PlanetCRCForWaypoints[L.getPlanetID()]);
					startingWaypoint.setID(server.getNextObjectID());
					server.addObjectToAllObjects(startingWaypoint, false,false);
					player.addWaypoint(startingWaypoint, false);

					player.playerTravel(D);
					/**
					 * starter emails go out of here now.
					 */
					SWGEmail welcomeemail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_welcome_subject, Constants.newbie_mail_welcome_body,null,false);
					server.queueEmailNewClientMessage(welcomeemail);
					SWGEmail professionEmail = null;
					switch(server.getSkillIndexFromName(player.getStartingProfession()))
					{
					case -1: //collector????? this is an email meant for collector edition owners. alas we will probably never know i they are or not.
					{
						professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_collector_subject, Constants.newbie_mail_collector_body,null,false);
						break;
					}
					case 10: //entertainer
					{
						professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_social_entertainer_subject, Constants.newbie_mail_social_entertainer_body,null,false);
						break;
					}
					case 30: //scout
					{
						professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_outdoors_scout_subject, Constants.newbie_mail_outdoors_scout_body,null,false);
						break;
					}
					case 50://medic
					{
						professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_science_medic_subject, Constants.newbie_mail_science_medic_body,null,false);
						break;
					}
					case 70: //artisan
					{
						professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_crafting_artisan_subject, Constants.newbie_mail_crafting_artisan_body,null,false);
						break;
					}

					case 90: //combat melee basic
					{
						professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_combat_brawler_subject, Constants.newbie_mail_combat_brawler_body,null,false);
						break;
					}
					case 109: //marksman
					{
						professionEmail = new SWGEmail(-1, 0, player.getID(), Constants.newbie_mail_combat_marksman_subject, Constants.newbie_mail_combat_marksman_body,null,false);
						break;
					}
					default: {
						System.out.println("Creating a new player with an unknown starting profession.  WTF???");
						break;
					}
					}
					server.queueEmailNewClientMessage(professionEmail);
					SWGEmail startloc = new SWGEmail(-1, 0, player.getID(), "Starting Location", "This is Your Starting Location Waypoint.",player.getWaypoints(),false);
					server.queueEmailNewClientMessage(startloc);

					server.getGUI().getDB().updatePlayer(player, false,false);                       
				}
			}
		}catch(Exception e){
			DataLog.logException("Error handleSelectStartingLocation", "ZoneClientThread",ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	protected void handleTeachSkill(long targetID, String[] sParams) throws IOException {
		System.out.println("Teaching...");
		Player tarPlayer = null;
		if (targetID == player.getID()) {
			// Send can't train yourself message.
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"teaching",
					"no_teach_self",
					0l, 
					"",
					"",
					"",
					0l, 
					"",
					"",
					"",
					0l,
					"",
					"",
					"",
					0,
					0f, false));

			return;
		} else if (targetID == 0) {
			if (!sParams[0].isEmpty()) {
				tarPlayer = server.getPlayer(sParams[0]);
				if (tarPlayer.getID() == player.getID()) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"teaching",
							"no_teach_self",
							0l, 
							"",
							"",
							"",
							0l, 
							"",
							"",
							"",
							0l,
							"",
							"",
							"",
							0,
							0f, false));
					return;
				}
			} else {
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"teaching",
						"no_target",
						0l, 
						"",
						"",
						"",
						0l, 
						"",
						"",
						"",
						0l,
						"",
						"",
						"",
						0,
						0f, false));

				return;
			}
		}
		try {
			if (tarPlayer == null) {
				tarPlayer = server.getPlayer(targetID);
			}
			if (tarPlayer instanceof NPC
					|| tarPlayer instanceof CreatureAnimal 
					|| tarPlayer instanceof CreaturePet
					|| tarPlayer instanceof Lair
					|| tarPlayer instanceof Shuttle
					|| tarPlayer instanceof Terminal
					|| tarPlayer instanceof Vehicle
			) {
				throw new ClassCastException("Error casting " + tarPlayer.getClass().getName() + " to Player");
			}
		} catch (ClassCastException e) {
			if (tarPlayer != null) {
				// It's an NPC of some sort
				// %tt
				NPC npc = (NPC)tarPlayer;
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"teaching",
						"no_skills_for_student",
						0l, 
						"",
						"",
						"",
						npc.getID(), 
						npc.getSTFFileName(),
						npc.getSTFFileIdentifier(),
						npc.getFirstName(),
						0l,
						"",
						"",
						"",
						0,
						0f, false));

			} else {
				// It's actually some other thing, like a building...
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"teaching",
						"teaching_failed",
						0l, 
						"",
						"",
						"",
						0l, 
						"",
						"",
						"",
						0l,
						"",
						"",
						"",
						0,
						0f, false));

			}
			return;
		}

		long myGroupID = player.getGroupID();
		long tarGroupID = tarPlayer.getGroupID();
		if (tarGroupID == 0 || tarGroupID != myGroupID) {
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"teaching",
					"not_in_same_group",
					0l, 
					"",
					"",
					"",
					tarPlayer.getID(), 
					tarPlayer.getSTFFileName(),
					tarPlayer.getSTFFileIdentifier(),
					tarPlayer.getFirstName(),
					0l,
					"",
					"",
					"",
					0,
					0f, false));

			return;
		}

		if (tarPlayer.getHasOutstandingTeachingOffer()) {
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"teaching",
					"student_has_offer_to_learn",
					0l, 
					"",
					"",
					"",
					tarPlayer.getID(), 
					tarPlayer.getSTFFileName(),
					tarPlayer.getSTFFileIdentifier(),
					tarPlayer.getFirstName(),
					0l,
					"",
					"",
					"",
					0,
					0f, false));
			return;
		}
		byte tarPlayerStance = tarPlayer.getStance();

		if (tarPlayerStance == Constants.STANCE_INCAPACITATED || tarPlayerStance == Constants.STANCE_DEAD || tarPlayer.hasState(Constants.STATE_COMBAT)) {
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"teaching",
					"student_dead",
					0l, 
					"",
					"",
					"",
					tarPlayer.getID(), 
					tarPlayer.getSTFFileName(),
					tarPlayer.getSTFFileIdentifier(),
					tarPlayer.getFirstName(),
					0l,
					"",
					"",
					"",
					0,
					0f, false));
			return;
		}
		// Find all the skills "I" have that the target player does not.
		BitSet tarPlayerSkillBits = tarPlayer.getSkillList();
		BitSet mySkillBits = player.getSkillList();
		BitSet trainableSkillBits = new BitSet();
		int numSkills = 0;
		for (int i = mySkillBits.nextSetBit(0); i >= 0; i = mySkillBits.nextSetBit(i+1)) {
			if (!tarPlayerSkillBits.get(i)) {
				Skills skill = server.getSkillFromIndex(i);
				if (!(skill.getName().contains("private") || skill.getName().contains("novice") || skill.getIsProfessionSkill() || skill.isGodModeSkill())) {
					int[] speciesNeeded = skill.getSpeciesSpecific();
					if (speciesNeeded == null) {
						trainableSkillBits.set(i);
						numSkills++;
					} else {
						int playerRace = tarPlayer.getRaceID();
						boolean bFound = false;
						for (int j = 0; j < speciesNeeded.length && !bFound; j++) {
							if (speciesNeeded[j] == (playerRace % 10)) {
								bFound = true;
								trainableSkillBits.set(i);
								numSkills++;
							}
						}
					}
				}
			}
		}
		if (numSkills == 0) {
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"teaching",
					"no_skills_for_student",
					0l, 
					"",
					"",
					"",
					tarPlayer.getID(), 
					tarPlayer.getSTFFileName(),
					tarPlayer.getSTFFileIdentifier(),
					tarPlayer.getFirstName(),
					0l,
					"",
					"",
					"",
					0,
					0f, false));
		} else {
			// Build and display the SUI window.
			SUIWindow W = new SUIWindow(player);
			W.setWindowType(Constants.SUI_FROG_TEACH_SKILL);
			W.setOriginatingObject(player);
			String[] sList = new String[numSkills];
			int currentIndex = 0;
			for (int i = trainableSkillBits.nextSetBit(0); i >= 0; i = trainableSkillBits.nextSetBit(i+1)) {
				Skills theSkill = server.getSkillFromIndex(i);
				sList[currentIndex] = "@skl_n:" + theSkill.getName();
				currentIndex++;
			}
			String WindowTypeString = "handleSUI";
			String DataListTitle = "Teach a skill"; 
			String DataListPrompt = "Select Skill to teach " + tarPlayer.getFirstName();
			client.insertPacket(W.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
			player.setStudent(tarPlayer);
		}
	}

	protected void handleSUIEventNotification_SelectSkillToTeach(SUIWindow window, int selection) throws IOException {
		if (selection == -1) {
			return;
		}
		String[] listContents = window.getListContents();
		String selectedItemText = listContents[selection].replace("@skl_n:", "");
		Skills skill = server.getSkillFromName(selectedItemText);
		player.removePendingSUIWindow(window);
		Player student = player.getStudent();
		if (student != null) {
			if (!student.hasSkill(skill.getSkillID())) {
				// This check should not be necessary, but one never knows.
				ZoneClient tarClient = student.getClient();
				if (tarClient != null) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"teaching",
							"offer_given",
							0l, 
							"",
							"",
							"",
							student.getID(), 
							student.getSTFFileName(),
							student.getSTFFileIdentifier(),
							student.getFirstName(),
							0l,
							"skl_n",
							skill.getName(),
							"",
							0,
							0f, false));

					SUIWindow messageBox = new SUIWindow(player);
					messageBox.setWindowType(Constants.SUI_LEARN_OFFERED_SKILL);
					messageBox.setOriginatingObject(student);
					String WindowTypeString = "handleSUI";
					String WindowTitle = "Learn a skill";
					String message = player.getFirstName() + " offers to teach you @skl_n:" + skill.getName(); // Must fix.
					tarClient.insertPacket(messageBox.SUIScriptMessageBox(tarClient, WindowTypeString, WindowTitle, message, true, false, 0, student.getID()));
					student.setSkillOfferedByTeacher(skill);
					student.setTeacher(player);
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"teaching",
							"teaching_failed",
							0l, 
							"",
							"",
							"",
							0l, 
							"",
							"",
							"",
							0l,
							"",
							"",
							"",
							0,
							0f, false));

				}
			} else {
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"teaching",
						"teaching_failed",
						0l, 
						"",
						"",
						"",
						0l, 
						"",
						"",
						"",
						0l,
						"",
						"",
						"",
						0,
						0f, false));
			}
		} else {
			client.insertPacket(PacketFactory.buildChatSystemMessage(
					"teaching",
					"teaching_failed",
					0l, 
					"",
					"",
					"",
					0l, 
					"",
					"",
					"",
					0l,
					"",
					"",
					"",
					0,
					0f, false));
		}
	}

	protected void handleHarvesterGetResourceData(long targetID, String[] sParams) {
		// System.out.println("handleHarvesterGetResourceData TID:" + targetID);

		/** 05 00
		 *  46 5E CE 80
		 *  23 00 00 00
		 *  16 01 00 00
		 *  53 61 50 24 00 00 00 00 <--Player ID
		 *  00 00 00 00
		 *  00 00 00 00
		 *  59 C0 96 F0
		 *  F0 21 1D EC 00 00 00 00 <--Resource ID
		 *  00 00 00 00
		 *  00 * */
		try{
			Harvester harvester = (Harvester)server.getObjectFromAllObjects(targetID);                
			Vector<SpawnedResourceData> vResourcesAvailable = harvester.getResourcesAvailable();  
			if (vResourcesAvailable == null || vResourcesAvailable.isEmpty()) {
				harvester.initializeAvailableResources();
				vResourcesAvailable = harvester.getResourcesAvailable();
			}
			System.out.println(vResourcesAvailable.size() + " Resources Available for this Harvester.");
			client.insertPacket(PacketFactory.buildObjectControllerHarvesterResourceData(player, harvester, vResourcesAvailable));
		}catch(Exception e){
			DataLog.logException("Exception in handleHarvesterGetResourceData", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);

		}
	}

	protected void handleHarvesterActivate(long targetID, String[] sParams) {
		/**
		 *   CRC ce645c94.
                Parameter[0] = []
                -----> Print Packet To Screen. <-----
                -----> CommandQueueEnqueue <-----
                -----> Len:52 <-----
                <------------------------------------------------>
                00 09 00 25 05 00 46 5E CE 80 23 00 00 00 16 01
                00 00 BC 91 F1 3B 00 00 00 00 00 00 00 00 00 00
                00 00 94 5C 64 CE 6F CA FB CA 00 00 00 00 00 00
                00 00 A4 0F
                <------------------------------------------------>  */
		try{
			Harvester harvester = (Harvester)server.getObjectFromAllObjects(targetID);
			if(harvester.getPowerPool() <= 0 && harvester.usesPower())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Power Reserves at 0 cannot activate."));
				return;
			}
			harvester.activateInstallation();
			//client.insertPacket(PacketFactory.buildDeltasMessageHINO7(harvester, vResourcesAvailable));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)3,(short)1,(short)0x06, harvester,harvester.getIAnimationBitmask()),Constants.PACKET_RANGE_CHAT_RANGE);
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)3,(short)1,(short)0x0D, harvester,1));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)7,(short)1,(short)0x06, harvester,1));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)7,(short)1,(short)0x09, harvester,harvester.getPowerPool()));
			client.insertPacket(PacketFactory.buildBaselineHINO7(harvester, harvester.getResourcesAvailable()));

		}catch(Exception e){
			DataLog.logException("Exception in handleHarvesterActivate", "ZoneClientThread",ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}

	}

	protected void handleHarvesterDeactivate(long targetID, String[] sParams) {
		try{
			Structure harvester = (Structure)server.getObjectFromAllObjects(targetID);
			harvester.deactivateInstallation();
			/*
                Vector<ResourceTemplateData> vRTD = new Vector<ResourceTemplateData>();
                vRTD.addAll(harvester.getVHarvesterResourceTypes());
                System.out.println("Resources for this Harvester " + harvester.getIFFFileName() + " Resource Type Count:" + vRTD.size());
                if(vRTD.size() == 0)
                {
                    System.out.print("This harvester has 0 resource types Aborting");
                    return;
                }
                Vector<SpawnedResourceData> vRD = server.getResourceManager().getResourcesByPlanetID(harvester.getPlanetID());
                Enumeration<SpawnedResourceData> sRDEnum = vRD.elements();
                Vector<SpawnedResourceData> vResourcesAvailable = new Vector<SpawnedResourceData>();

                while(sRDEnum.hasMoreElements())
                {
                    SpawnedResourceData sr = sRDEnum.nextElement();
                    if(sr.getIsSpawnedOnPlanet(harvester.getPlanetID()))
                    {
                        for(int i = 0; i < vRTD.size();i++)
                        {
                            if(vRTD.get(i).getResourceTypeID() == sr.getType())
                            {
                                vResourcesAvailable.add(sr);
                                System.out.println("Type: " + sr.getResourceType() + " Name: " + sr.getName() + " Class: " + sr.getResourceClass() + " Density: " + sr.getBestDensityAtLocation(harvester.getX(), harvester.getY()) + " Planet ID:" + sr.getPlanetID());
                            }
                        }
                    }
                }
			 */
			//client.insertPacket(PacketFactory.buildBaselineHINO7(harvester, vResourcesAvailable));
			//client.insertPacket(PacketFactory.buildDeltasMessageHINO7(harvester, vResourcesAvailable));
			//client.insertPacket(PacketFactory.buildClientEffectOnObject(player, harvester, ""));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)3,(short)1,(short)0x06, harvester,harvester.getIAnimationBitmask()),Constants.PACKET_RANGE_CHAT_RANGE);
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)3,(short)1,(short)0x0D, harvester,0));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)7,(short)1,(short)0x06, harvester,0));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)7,(short)1,(short)0x09, harvester,0));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)7,(short)1,(short)0x0C, harvester,(byte)5));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)7,(short)1,(short)0x09, harvester,0));

		}catch(Exception e){
			DataLog.logException("Exception in handleHarvesterDeactivate", "ZoneClientThread",ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}
	protected void handleHarvesterSelectResource(long targetID, String[] sParams) {
		/**
		 * RCRC ff549d14.
                    Parameter[0] = [4261509886] <--Resource ID
                    -----> Print Packet To Screen. <-----
                    -----> CommandQueueEnqueue <-----
                    -----> Len:72 <-----
                    <------------------------------------------------>
                    00 09 00 31
		 * 05 00
		 * 46 5E CE 80
		 * 23 00 00 00
		 * 16 01 00 00
		 * BC 91 F1 3B 00 00 00 00
		 * 00 00 00 00
		 * 00 00 00 00
		 * 14 9D 54 FF
		 * 6F CA FB CA 00 00 00 00
		 * 0A 00 00 00 34 00 32 00 36 00 31 00 35 00 30 00 39 00 38 00 38 00 36 00
		 * 46 4C
                    <------------------------------------------------>                 */

		try{
			Harvester harvester = (Harvester)server.getObjectFromAllObjects(targetID);
			long lResourceID = Long.parseLong(sParams[0]);
			//System.out.println("Resource ID Received: " + lResourceID);
			SpawnedResourceData r = server.getResourceManager().getResourceByID(lResourceID);
			if(r != null)
			{
				harvester.setCurrentHarvestResource(r);
				//harvester.setCurrentResourceConcentration(r.getBestDensityAtLocation(harvester.getX(), harvester.getY()));
				client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO,(byte)7,(short)1,(short)5, harvester, lResourceID));
			}
			if(harvester.isInstallationActive())
			{
				// Deactivate the installation.
				harvester.deactivateInstallation();
				Vector<SpawnedResourceData> vResourcesAvailable = harvester.getResourcesAvailable();
				client.insertPacket(PacketFactory.buildBaselineHINO7(harvester, vResourcesAvailable));
				client.insertPacket(PacketFactory.buildDeltasMessageHINO7(harvester, vResourcesAvailable));
			}
		}catch(Exception e){
			DataLog.logException("Exception in handleHarvesterDeactivate", "ZoneClientThread",ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}
	
	private void handleImageDesignChangeMessage(SOEInputStream dIn){
		try{
			PacketUtils.printPacketToScreen(dIn.getBuffer(), dIn.getBufferLength(), "handleImageDesignChangeMessage");
			/*System.out.println("----");
                    byte [] b = new byte[32];
                    for(int i =0; i < 32; i++)
                    {
                        b[i] = dIn.readByte();
                        System.out.print(PacketUtils.getByteCode(b[i]) + " ");
                    }
                    System.out.println("----");
                    dIn.skipBytes(12);//bypass the player id
			 */
			dIn.readLong();
			dIn.readInt();
			System.out.println("Image Design Data Received.");
			long lDesignerID = dIn.readLong();
			System.out.println("Designer ID: " + lDesignerID);
			long lCustomerID = dIn.readLong();
			System.out.println("Customer ID: " + lCustomerID);
			long lUnkLong1 = dIn.readLong();
			System.out.println("lUnkLong1: " + lUnkLong1);
			boolean bHairChanged = dIn.readBoolean();
			System.out.println("bHairChanged: " + bHairChanged);
			String sNewHairStyle = dIn.readUTF();                    
			System.out.println("sNewHairStyle: " + sNewHairStyle);                    
			String sUnknownHairItem1 = dIn.readUTF();
			System.out.println("sUnknownHairItem1: " + sUnknownHairItem1);
			int iHairItemsChanged = dIn.readInt();
			System.out.println("iHairItemsChanged: " + iHairItemsChanged);
			int iUnknownCRCValue = dIn.readInt();
			System.out.println("iUnknownCRCValue: " + iUnknownCRCValue);
			int iMoneyRequired = dIn.readInt();
			System.out.println("iMoneyRequired: " + iMoneyRequired);
			int iUnknownInt1 = dIn.readInt();
			System.out.println("iUnknownInt1: " + iUnknownInt1);
			int iUnknownInt2 = dIn.readInt();
			System.out.println("iUnknownInt2: " + iUnknownInt2);                    
			int iUnknownShort1 = dIn.readShort();
			System.out.println("iUnknownShort1: " + iUnknownShort1);
			int iBodyFormSkillLevel = dIn.readInt();
			System.out.println("iBodyFormSkillLevel: " + iBodyFormSkillLevel);
			int iFaceFormSkillLevel = dIn.readInt();
			System.out.println("iFaceFormSkillLevel: " + iFaceFormSkillLevel);

			int iMarkingDesignSkillLevel = dIn.readInt();
			System.out.println("iMarkingDesignSkillLevel: " + iMarkingDesignSkillLevel);
			int iHairStylingSkillLevel = dIn.readInt();
			System.out.println("iHairStylingSkillLevel: " + iHairStylingSkillLevel);
			int iSlidersChanged = dIn.readInt();
			System.out.println("iSlidersChanged: " + iSlidersChanged);
			String [] sSlidersChanged = new String [iSlidersChanged];
			for(int i = 0; i < iSlidersChanged; i++)
			{
				sSlidersChanged[i] = dIn.readUTF() + "," + dIn.readFloat();
				System.out.println("Slider, Value: " + sSlidersChanged[i]);
			}
			int iColorBoxesChanged = dIn.readInt();
			String [] sColorBoxes = new String [iColorBoxesChanged];
			System.out.println("iColorBoxesChanged: " + iColorBoxesChanged );
			for(int i = 0; i < iColorBoxesChanged; i++)
			{
				sColorBoxes[i] = dIn.readUTF() + "," + dIn.readInt();
				System.out.println("Color Box, Value: " + sColorBoxes[i]);
			}
			Player designer = (Player)server.getObjectFromAllObjects(lDesignerID);
			if(designer.hasSkill(586))
			{
				String sHoloEmote = dIn.readUTF();
				System.out.println("sHoloEmote: " + sHoloEmote);
			}

			Player customer = (Player)server.getObjectFromAllObjects(lCustomerID);
			switch(customer.getRaceID())
			{
			case 0: //human male
			{
				boolean changesmade = false;
				CustomData d = new CustomData(customer.getCustomData(),customer.getID(),customer.getRaceID());
				for(int i =0; i < sSlidersChanged.length; i++)
				{
					if(sSlidersChanged[i].split(",")[0].compareToIgnoreCase("muscle")==0)
					{
						String [] newVals = sSlidersChanged[i].split(",");
						float newFVal = Float.parseFloat(newVals[1]);
						byte newVal = (byte)(newFVal * 2.3);
						d.changeCustomizationValue((byte)4, newVal, (byte)1, false);
						changesmade = true;
					}
				}
				if(changesmade)
				{
					//PacketUtils.printPacketToScreen(d.getCustomizationData(), d.getCustomizationData().length, "Before Commit");
					d.commitChanges();
					//PacketUtils.printPacketToScreen(d.getCustomizationData(), d.getCustomizationData().length, "After Commit");
					customer.setCustomizationData(d.getCustomizationData());
					customer.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)4, customer, customer.getCustomData()),Constants.PACKET_RANGE_CHAT_RANGE);
				}
				break;
			}
			}

			//boolean accepted = dIn.readBoolean();
			//System.out.println("Accepted: " + accepted);
			/**
			 * all boxes touched <---
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 00 00 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 00 00 00 00 00 00 00 00
			 * 01
			 * 36 00 6F 62 6A 65 63 74 2F 74 61 6E 67 69 62 6C 65 2F 68 61 69 72 2F 74 77
			 *          69 6C 65 6B 2F 68 61 69 72 5F 74 77 69 6C 65 6B 5F 66 65 6D 61 6C 65 5F 73 30 36 2E 69 66 66
			 * 00 00
			 * 01 00 00 00
			 * 36 5E 77 49
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00
			 * 19 00 00 00
			 * 19 00 00 00
			 * 19 00 00 00
			 * 19 00 00 00
			 * 14 00 00 00 <--sliders
			 * 03 00 61 67 65 //age
			 * AB AA AA 3E
			 * 06 00 63 68 65 65 6B 73 //cheeks
			 * 00 00 80 3E
			 * 05 00 63 68 65 73 74 //chest
			 * 66 66 66 3F
			 * 04 00 63 68 69 6E //chin
			 * 8F C2 F5 3C
			 * 04 00 65 61 72 73 //ears
			 * 29 5C 0F 3E
			 * 0D 00 65 79 65 5F 64 69 72 65 63 74 69 6F 6E //eye_direction
			 * B8 1E 05 3F
			 * 09 00 65 79 65 5F 73 68 61 70 65 //eye_shape
			 * 33 33 33 3F
			 * 08 00 65 79 65 5F 73 69 7A 65 //eye_size
			 * 00 00 00 3F
			 * 09 00 65 79 65 73 68 61 64 6F 77 //eyeshadow
			 * 00 00 00 00
			 * 08 00 66 72 65 63 6B 6C 65 73 //freckles
			 * 00 00 80 3E
			 * 06 00 68 65 69 67 68 74 //height
			 * 9A 99 19 3F
			 * 03 00 6A 61 77 //jaw
			 * 85 EB 11 3F
			 * 0C 00 6C 69 70 5F 66 75 6C 6C 6E 65 73 73 //lip_fullness
			 * 5C 8F 02 3F
			 * 09 00 6C 69 70 5F 77 69 64 74 68 //lip_width
			 * AE 47 21 3F
			 * 06 00 6D 75 73 63 6C 65 //muscle
			 * F6 28 5C 3F
			 * 0B 00 6E 6F 73 65 5F 6C 65 6E 67 74 68 //nose_length
			 * D7 A3 F0 3E
			 * 0F 00 6E 6F 73 65 5F 70 72 6F 74 72 75 73 69 6F 6E //nose_protrusion
			 * 33 33 B3 3E
			 * 0A 00 6E 6F 73 65 5F 77 69 64 74 68 //nose_width
			 * 85 EB 11 3F
			 * 07 00 70 61 74 74 65 72 6E //pattern // i/e lekku pattern
			 * 33 33 33 3F
			 * 06 00 77 65 69 67 68 74 // weight
			 * 9A 99 19 3E
			 * 05 00 00 00 <--boxes
			 * 10 00 63 6F 6C 6F 72 5F 65 79 65 5F 73 68 61 64 6F 77 //color_eye_shadow
			 * 6A 00 00 00
			 * 0A 00 63 6F 6C 6F 72 5F 65 79 65 73 //color_eyes
			 * 79 00 00 00
			 * 09 00 63 6F 6C 6F 72 5F 6C 69 70 //color_lip
			 * E4 00 00 00
			 * 0E 00 63 6F 6C 6F 72 5F 6D 61 72 6B 69 6E 67 73 //color_markings //ie lekku markings
			 * 6C 00 00 00
			 * 0A 00 63 6F 6C 6F 72 5F 73 6B 69 6E //color_skin
			 * 5A 00 00 00
			 * 0D 00 68 6F 6C 6F 65 6D 6F 74 65 5F 61 6C 6C //holoemote_all
			 * 00

			 * ---------------------
			 * 05 00 
			 * 46 5E CE 80 
			 * 23 00 00 00 
			 * 38 02 00 00 
			 * 15 5D 4A 4B 00 00 00 00 
			 * 00 00 00 00 
			 * 15 5D 4A 4B 00 00 00 00 
			 * 15 5D 4A 4B 00 00 00 00 
			 * 00 00 00 00 00 00 00 00 
			 * 01 
			 * 36 00 6F 62 6A 65 63 74 2F 74 61 6E 67 69 62 6C 65 2F 68 61 69 72 2F 74 77  
			 *       69 6C 65 6B 2F 68 61 69 72 5F 74 77 69 6C 65 6B 5F 66 65 6D 61 6C 65 
			 *       5F 73 30 32 2E 69 66 66 
			 * 00 00 
			 * 01 00 00 00 
			 * 8A 7A 76 49 
			 * 00 00 00 00 
			 * 00 00 00 00 
			 * 00 00 00 00 
			 * 00 00 
			 * 02 00 00 00 <--SkillMods : BodyForm
			 * 02 00 00 00 <--SkillMods : FaceForm
			 * 02 00 00 00 <--SkillMods : HairStyling
			 * 02 00 00 00 <--SkillMods : MarkingDesign
			 * 01 00 00 00 //Cosmetic sliders changed
			 * 09 00 65 79 65 73 68 61 64 6F 77  //eyeshadow <--Name of the slider changed
			 * 00 00 80 3F <--Float
			 * 04 00 00 00 //cosmetic items changed
			 * 10 00 63 6F 6C 6F 72 5F 65 79 65 5F 73 68 61 64 6F 77 //color_eye_shadow Selection Box
			 * 05 00 00 00 <--Int Color eye shadow
			 * 0E 00 63 6F 6C 6F 72 5F 65 79 65 62 72 6F 77 73 //color_eyebrows
			 * 15 00 00 00 
			 * 0A 00 63 6F 6C 6F 72 5F 65 79 65 73 //color_eyes
			 * 17 00 00 00 
			 * 09 00 63 6F 6C 6F 72 5F 6C 69 70 ////color_lip //Selection Box
			 * 26 00 00 00 
			 * 00 
			 * 00 77 A6
			 * ----------------------------------
			 * 05 00 
			 * 46 5E CE 80 
			 * 23 00 00 00 
			 * 38 02 00 00 
			 * 15 5D 4A 4B 00 00 00 00 //dIn.readLong();
			 * 00 00 00 00 //dIn.readInt();
			 * 15 5D 4A 4B 00 00 00 00 //lDesignerID
			 * 15 5D 4A 4B 00 00 00 00 //lCustomerID
			 * 00 00 00 00 00 00 00 00 //long lUnkLong1 = dIn.readLong();
			 * 00 //boolean bHairChanged = dIn.readBoolean();
			 * 00 00 //String sNewHairStyle = dIn.readUTF();                     
			 * 00 00 //String sUnknownHairItem1 = dIn.readUTF();
			 * 00 00 00 00 //int iHairItemsChanged = dIn.readInt();
			 * CA 39 76 49 //int iUnknownCRCValue = dIn.readInt();
			 * 00 00 00 00 //int iMoneyRequired = dIn.readInt();
			 * 00 00 00 00 //int iUnknownInt1 = dIn.readInt();
			 * 00 00 00 00 //int iUnknownInt2 = dIn.readInt();
			 * 00 00 //int iUnknownShort1 = dIn.readShort();
			 * 02 00 00 00 //int iUnknownItemEnabledCount1 = dIn.readInt();
			 * 02 00 00 00 //int iUnknownItemEnabledCount2 = dIn.readInt();
			 * 02 00 00 00 //int iUnknownItemEnabledCount3 = dIn.readInt();
			 * 02 00 00 00 //int iUnknownItemEnabledCount4 = dIn.readInt();
			 * 00 00 00 00 //int iSlidersChanged = dIn.readInt();
			 * 00 00 00 00 //int iColorBoxesChanged = dIn.readInt();
			 * 00 //boolean unknownBooleanValue1 = dIn.readBoolean();
			 * 00 A3 DB
			 * ---------------------------
			 * 05 00 <--Object Update
			 * 46 5E CE 80 <--ObjectContoller Message
			 * 23 00 00 00 <--Update for the Player
			 * 38 02 00 00 <--Image Design Change
			 * 15 5D 4A 4B 00 00 00 00 <--Player Performing
			 * 00 00 00 00 <--Spacer
			 * 15 5D 4A 4B 00 00 00 00 <--Image Designer
			 * 15 5D 4A 4B 00 00 00 00 <--Customer
			 * 00 00 00 00 00 00 00 00 <--Spacer
			 * 00 <--Hair changed Boolean
			 * 00 00 <--Hair IFF String
			 * 00 00 00 00 <--Spacer
			 * 00 00 <--UTF Unknown
			 * 5A 34 76 49 <--UNK CRC
			 * 00 00 00 00 <--UNK Int
			 * 00 00 00 00 <--UNK Int
			 * 00 00 00 00 <--UNK Int
			 * 00 00 <--UNK Short
			 * 02 00 00 00 <--Counter
			 * 02 00 00 00 <--Counter
			 * 02 00 00 00 <--Counter
			 * 02 00 00 00 <--Counter
			 * 00 00 00 00 <--Counter
			 * 01 00 00 00 <--Counter
			 * 10 00 63 6F 6C 6F 72 5F 65 79 65 5F 73 68 61 64 6F 77 //color_eye_shadow //Selection Box
			 * 0B 00 00 00 <--Eye Shadow Color Selected
			 * 00 <--Accepted Boolean
			 * -----------
			 *
			 * -------------------------------------
			 * Only Money Changed here
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00 00 00 00 00
			 * 00 
			 * 00 00
			 * 00 00 00 00
			 * 00 00
			 * 5D F9 75 49
			 * 40 E2 01 00 <--Money Amount 123456
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00
			 * 01 00 00 00
			 * 00 00 00 00
			 * 01 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00
			 * 00 80 A5
			 * ------------------------------------
			 * Eye Shadow set to 0
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00 00 00 00 00
			 * 00 
			 * 00 00
			 * 00 00 00 00
			 * 00 00
			 * 5D F9 75 49
			 * 40 E2 01 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00
			 * 01 00 00 00
			 * 00 00 00 00
			 * 01 00 00 00
			 * 01 00 00 00
			 * 09 00 65 79 65 73 68 61 64 6F 77 //eyeshadow <--Name of the slider changed
			 * 00 00 00 00 <--Float New Value <-- eye Shadow is 2 possible Values 1 or 0
			 * 00 00 00 00
			 * 00
			 * 00 E7 FB
			 * ---------------------------
			 * eye chadow color changed
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00 00 00 00 00
			 * 00
			 * 00 00
			 * 00 00 00 00
			 * 00 00
			 * 5D F9 75 49
			 * 40 E2 01 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00
			 * 01 00 00 00
			 * 00 00 00 00
			 * 01 00 00 00
			 * 01 00 00 00
			 * 09 00 65 79 65 73 68 61 64 6F 77  //eye shadow slider
			 * 00 00 80 3F <--Eye Shadow Slider Value
			 * 01 00 00 00
			 * 10 00 63 6F 6C 6F 72 5F 65 79 65 5F 73 68 61 64 6F 77 //color_eye_shadow Selection Box
			 * 55 00 00 00 <--New Eye Shadow Value (Int Value)
			 * 00
			 * 00 E3 C9
			 * -----------------------------------------
			 * lip color changed
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00 00 00 00 00
			 * 00
			 * 00 00
			 * 00 00 00 00
			 * 00 00
			 * 5D F9 75 49
			 * 40 E2 01 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00
			 * 01 00 00 00
			 * 00 00 00 00
			 * 01 00 00 00
			 * 01 00 00 00
			 * 09 00 65 79 65 73 68 61 64 6F 77 //eyeshadow //slider
			 * 00 00 80 3F <--Eye Shadow Slider Value
			 * 02 00 00 00
			 * 10 00 63 6F 6C 6F 72 5F 65 79 65 5F 73 68 61 64 6F 77 //color_eye_shadow //Selection Box
			 * 33 00 00 00 <--New Eye Shadow Value (Int Value)
			 * 09 00 63 6F 6C 6F 72 5F 6C 69 70 //color_lip //Selection Box
			 * 1A 00 00 00 <--New Lip Color Value (Int Value)
			 * 00
			 * 00 9C 4F
			 * -----------------------------
			 * eye brow color changed
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00 00 00 00 00 
			 * 00
			 * 00 00
			 * 00 00 00 00
			 * 00 00
			 * 5D F9 75 49
			 * 40 E2 01 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00
			 * 01 00 00 00
			 * 00 00 00 00
			 * 01 00 00 00 <--Cosmetic Sliders Enabled : Total 2 Max
			 * 01 00 00 00 <--Cosmetic Sliders touched
			 * 09 00 65 79 65 73 68 61 64 6F 77 //eyeshadow //slider
			 * 00 00 80 3F <--Eye Shadow Slider Value (Float Value)
			 * 03 00 00 00 <-Selection Boxes Touched i/e Selection Boxes List that has been changed
			 * 10 00 63 6F 6C 6F 72 5F 65 79 65 5F 73 68 61 64 6F 77 //color_eye_shadow //Selection Box
			 * 33 00 00 00 <--New Eye Shadow Value (Int Value)
			 * 0E 00 63 6F 6C 6F 72 5F 65 79 65 62 72 6F 77 73 //color_eyebrows
			 * 0D 00 00 00 <--New eye Brow Color (Int Value)
			 * 09 00 63 6F 6C 6F 72 5F 6C 69 70 //color_lip //Selection Box
			 * 1A 00 00 00 <--New Lip Color Value (Int Value)
			 * 00
			 * 00 D7 EA
			 * --------------------------------
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * A5 79 97 DE 00 00 00 00
			 * 00 00 00 00 00 00 00 00
			 * 01 <--Boolean? Hair Changed????
			 * 36 00 6F 62 6A 65 63 74 2F 74 61 6E 67 69 62 6C 65 2F 68 61 69 72 2F 74 77 69 6C 65 6B 2F 68 61 69 72 5F 74 77 69 6C 65 6B 5F 66 65 6D 61 6C 65 5F 73 30 36 2E 69 66 66  <--HAIR MODEL IFF
			 * 00 00 00 00
			 * 00 00
			 * 5D F9 75 49
			 * 40 E2 01 00 <--Money Amount 123456
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00
			 * 01 00 00 00
			 * 00 00 00 00
			 * 01 00 00 00 <--Cosmetic Sliders Enabled : Total 2 Max
			 * 01 00 00 00 <--Cosmetic Sliders touched
			 * 09 00 65 79 65 73 68 61 64 6F 77 //eyeshadow //slider
			 * 00 00 80 3F <--Eye Shadow Slider Value (Float Value)
			 * 03 00 00 00 <-Selection Boxes Touched i/e Selection Boxes List that has been changed
			 * 10 00 63 6F 6C 6F 72 5F 65 79 65 5F 73 68 61 64 6F 77 //color_eye_shadow //Selection Box
			 * 33 00 00 00 <--New Eye Shadow Value (Int Value)
			 * 0E 00 63 6F 6C 6F 72 5F 65 79 65 62 72 6F 77 73 //color_eyebrows
			 * 0D 00 00 00 <--New eye Brow Color (Int Value)
			 * 09 00 63 6F 6C 6F 72 5F 6C 69 70 //color_lip //Selection Box
			 * 1A 00 00 00 <--New Lip Color Value (Int Value)
			 * 00
			 * 00 AC 1A
			 * ---------------------------
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 00 00 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 00 00 00 00 00 00 00 00 <--UnkLong1
			 * 01 <--Hair Style Changed Boolean or Amount of hair changes  //0
			 * 36 00 6F 62 6A 65 63 74 2F 74 61 6E 67 69 62 6C 65 2F 68 61 69 72 2F 74 77
			 *       69 6C 65 6B 2F 68 61 69 72 5F 74 77 69 6C 65 6B 5F 66 65 6D 61 6C 65
			 *       5F 73 30 36 2E 69 66 66 //1
			 * 00 00 00 00 <--int spacer
			 * 00 00 <--UnkHairItem3 //4
			 * 42 04 76 49 <--UNK CRC OR INT VAL//5
			 * 40 E2 01 00 <--Money Required //6
			 * 00 00 00 00 <--UnkInt1 //7
			 * 00 00 00 00 <--UnkInt2 //8
			 * 00 00 00 00 <--UnkInt3 //9
			 * 00 00 <--UnkShort1 //10
			 * 01 00 00 00 <--Unknown Enabled 1 //11
			 * 00 00 00 00 <--Unknown List 1 Count //12
			 * 01 00 00 00 <--Unknown Enabled 2 //13
			 * 01 00 00 00 <--Unknown Enabled 3 //14
			 * 01 00 00 00 <--Cosmetic Sliders Enabled : Total 2 Max //15
			 * 01 00 00 00 <--Cosmetic Sliders touched //16
			 * 09 00 65 79 65 73 68 61 64 6F 77 //eyeshadow //slider
			 * 00 00 80 3F <--Eye Shadow Slider Value (Float Value)
			 * 03 00 00 00 <-Selection Boxes Touched i/e Selection Boxes List that has been changed
			 * 10 00 63 6F 6C 6F 72 5F 65 79 65 5F 73 68 61 64 6F 77 //color_eye_shadow //Selection Box
			 * 33 00 00 00 <--New Eye Shadow Value (Int Value)
			 * 0E 00 63 6F 6C 6F 72 5F 65 79 65 62 72 6F 77 73 //color_eyebrows
			 * 0D 00 00 00 <--New eye Brow Color (Int Value)
			 * 09 00 63 6F 6C 6F 72 5F 6C 69 70 //color_lip //Selection Box
			 * 1A 00 00 00 <--New Lip Color Value (Int Value)
			 * 00
			 * ---------------------------------------
			 * 05 00
			 * 46 5E CE 80
			 * 23 00 00 00
			 * 38 02 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 00 00 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 15 5D 4A 4B 00 00 00 00
			 * 00 00 00 00 00 00 00 00
			 * 00 //0
			 * 00 00 
			 * 00 00 01 00
			 * 00 00
			 * 36 07 76 49
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00 00 00
			 * 00 00
			 * 02 00 00 00
			 * 02 00 00 00
			 * 02 00 00 00
			 * 02 00 00 00
			 * 00 00 00 00
			 * 01 00 00 00
			 * 0A 00 63 6F 6C 6F 72 5F 65 79 65 73
			 * 07 00 00 00
			 * 00
			 * 00 3E 59
			 * ------------------
			 */
		}catch(Exception e){
			DataLog.logException("Exception in handleImageDesignChangeMessage", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}
	private void handleRetieveHarvesterResource(SOEInputStream dIn){
		//byte [] d = dIn.getBuffer();
		/**
		 *  05 00
                46 5E CE 80
                83 00 00 00
                ED 00 00 00 <--enqueueRetrieveHarvesterResource
                BC 91 F1 3B 00 00 00 00
                00 00 00 00
                BC 91 F1 3B 00 00 00 00
                0F 0E 98 8F 00 00 00 00 //harvester id
                5E 56 D9 37 00 00 00 00 //resource id
                4E 00 00 00 //resource amount
                00 <--Byte Comes as 0
		 *  00 <--Byte Comes in Variable Req ID counter
		 *
		 * 05 00
		 * 46 5E CE 80
		 * 83 00 00 00
		 * ED 00 00 00
		 * BC 91 F1 3B 00 00 00 00
		 * 00 00 00 00
		 * BC 91 F1 3B 00 00 00 00
		 * 4E 53 05 4F 00 00 00 00
		 * 60 2F 84 5E 00 00 00 00
		 * A0 86 01 00
		 * 00
		 * 0B

		 * 05 00
		 * 46 5E CE 80
		 * 83 00 00 00
		 * ED 00 00 00
		 * BC 91 F1 3B 00 00 00 00
		 * 00 00 00 00
		 * BC 91 F1 3B 00 00 00 00
		 * 4E 53 05 4F 00 00 00 00
		 * 60 2F 84 5E 00 00 00 00
		 * A0 86 01 00
		 * 00 01 00

		 */
		try{
			dIn.readLong();
			dIn.readInt();
			dIn.readLong();
			long lHarvesterID = dIn.readLong();
			long lResourceID = dIn.readLong();
			int iResourceAmount = dIn.readInt();
			boolean success = false;
			boolean discardresource = dIn.readBoolean();//this boolean tells us to discard the resource or not.
			byte reqID = dIn.readByte();
			byte err = 0;


			Harvester harvester = (Harvester)server.getObjectFromAllObjects(lHarvesterID);
			//SpawnedResourceData theResource = server.getResourceManager().getResourceByID(lResourceID);
			// System.out.println("Player Has Requested to Retrieve Resource from Harvester Hopper");
			// System.out.println("Resource ID: " + lResourceID + " Name: " + theResource.getName() + " Type: " + theResource.getResourceType() + " Amount: " + iResourceAmount );
			ResourceContainer r = null;
			Enumeration<SOEObject> vOutputHopperEnum = harvester.getOutputHopper().elements();
			while (vOutputHopperEnum.hasMoreElements() && r == null)
			{
				ResourceContainer rt = (ResourceContainer)vOutputHopperEnum.nextElement();
				if(rt.getResourceSpawnID() == lResourceID)
				{
					r = rt;
				}
			}
			boolean completeStack = false;
			if(player.getInventorySlotsLeft() >= 1)
			{
				if(r!=null && !discardresource)
				{
					int iQuantity = r.getStackQuantity();
					if(iResourceAmount == iQuantity)
					{
						ResourceContainer nr = new ResourceContainer(r, iResourceAmount,player.getInventory());
						nr.setOwner(player);
						nr.setEquipped(player.getInventory(), -1);
						player.addItemToInventory(nr);
						server.addObjectToAllObjects(nr, false, false);
						player.spawnItem(nr);
						r.setStackQuantity(0, false);
						success = true;
						completeStack = true;
						harvester.getOutputHopper().remove(r.getID());
					}
					else if(iResourceAmount < iQuantity)
					{
						ResourceContainer nr = new ResourceContainer(r, iResourceAmount,player.getInventory());
						r.setStackQuantity(iQuantity - iResourceAmount, false);
						nr.setOwner(player);
						nr.setEquipped(player.getInventory(), -1);
						player.addItemToInventory(nr);
						server.addObjectToAllObjects(nr, false, false);
						player.spawnItem(nr);
						success = true;
					}
				}
				else if(r!=null && discardresource)
				{
					completeStack = true;
					harvester.getOutputHopper().remove(r.getID());
					success = true;
				}
			}

			if(!success)
			{
				client.insertPacket(PacketFactory.buildObjectControllerDequeueRetrieveHarvesterResource(player,success,reqID,err));
				DataLog.logEntry("Resource Retrieval Error handleRetieveHarvesterResource", "ZoneClientThread", Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
			}
			else
			{
				Vector<SpawnedResourceData> vResourcesAvailable = harvester.getResourcesAvailable();

				client.insertPacket(PacketFactory.buildBaselineHINO7(harvester, vResourcesAvailable));
				client.insertPacket(PacketFactory.buildDeltasMessageHINO7(harvester, vResourcesAvailable));
				if(!completeStack)
				{
					client.insertPacket(PacketFactory.buildDeltasMessageHINO7_ResourceHopper(harvester));
				}
				client.insertPacket(PacketFactory.buildObjectControllerDequeueRetrieveHarvesterResource(player,success,reqID,err));
			}
		}catch(Exception e){
			DataLog.logException("Exception Caught in handleRetieveHarvesterResource", "ZoneClientThread",ZoneServer.ZoneRunOptions.bLogToConsole,true, e);
		}
	}

	private void handleResourceContainerTransfer(long targetID, String[] sParams) {
		/**
		 *  05 00
                46 5E CE 80
                23 00 00 00
                16 01 00 00
                BC 91 F1 3B 00 00 00 00 <--Player ID
                00 00 00 00
                00 00 00 00
                75 2A 26 F7 <--Command CRC
                83 EE 07 F0 00 00 00 00 <--Target ID, This should be the container were going to transfer
                0C 00 00 00 31 00 36 00 34 00 39 00 30 00 37 00 35 00 37 00 37 00 30 00 20 00  <--This should be the container were going to transfer to.
		 *              31 00 <--This should be the amount to transfer.
		 *  CE 99
		 */

		try{
			// System.out.println("handleResourceContainerTransfer TID:" + targetID);
			// System.out.println("Params:");
			// for(int i = 0; i < sParams.length;i++)
			//  {
			//      System.out.println("Param: " + i + " : " + sParams[i]);
			//  }

			long lDestinationContainer = Long.parseLong(sParams[0]);

			SOEObject s = server.getObjectFromAllObjects(targetID);
			SOEObject d = server.getObjectFromAllObjects(lDestinationContainer);

			if(d instanceof ResourceContainer && s instanceof ResourceContainer)
			{
				//means the destination and source are resource containers lets combine them
				ResourceContainer source, destination;
				source = (ResourceContainer)s;
				destination = (ResourceContainer)d;
				if(source.getResourceSpawnID() == destination.getResourceSpawnID())
				{
					//Only combine if the spawnid is the same
					int sourceQuantity = source.getStackQuantity();
					int destinationQuantity = destination.getStackQuantity();
					int newQuantity = sourceQuantity + destinationQuantity;
					if (newQuantity <= Constants.MAX_STACK_SIZE) {
						client.insertPacket(destination.setStackQuantity(newQuantity, true));
						SOEObject container = source.getContainer();
						if(container.equals(player.getInventory()))
						{
							player.removeItemFromInventory(source);
						}
						else if(container instanceof TangibleItem)
						{
							TangibleItem ctainer = (TangibleItem)container;
							ctainer.removeLinkedObject(source);
						}
						player.despawnItem(source);
						server.removeObjectFromAllObjects(source, false);
					}
					else if(destination.getStackQuantity() < Constants.MAX_STACK_SIZE )
					{
						int spaceleft = Constants.MAX_STACK_SIZE - destinationQuantity;
						client.insertPacket(source.setStackQuantity(sourceQuantity - spaceleft, true));
						client.insertPacket(destination.setStackQuantity(destinationQuantity + spaceleft, true));
					}

				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot combine those two resources."));
				}
			}
			else
			{
				DataLog.logEntry("handleResourceContainerTransfer Constainers Not Handled " + s.getID() + " Class: " + s.getClass() + " : " + d.getID() + " Class: " + d.getClass() , "ZoneClientThread", Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
			}


		}catch(Exception e){
			DataLog.logException("Exception in handleResourceContainerTransfer", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}

	}

	private void handleResourceContainerSplit(long targetID, String[] sParams) {
		/**
		 *  with CRC 74952854.
                Parameter[0] = [3]
                Parameter[1] = [3615456542]
                Parameter[2] = [-1]
                Parameter[3] = [0]
                Parameter[4] = [0]
                Parameter[5] = [0]
                -----> Print Packet To Screen. <-----
                -----> CommandQueueEnqueue <-----
                -----> Len:94 <-----
                <------------------------------------------------>
		 * 05 00
		 * 46 5E CE 80
		 * 23 00 00 00
		 * 16 01 00 00
		 * BC 91 F1 3B 00 00 00 00
		 * 00 00 00 00
		 * 00 00 00 00
		 * 54 28 95 74
		 * 3A E6 4A 62 00 00 00 00
		 * 15 00 00 00 33 00 20 00
		 *             33 00 36 00 31 00 35 00 34 00 35 00 36 00 35 00 34 00 32 00 20 00 2D 00 31 00 20 00
		 *             30 00 20 00
		 *             30 00 20 00
		 *             30 00
		 * 3C F8
                <------------------------------------------------>

		 */

		try{
			//System.out.println("handleResourceContainerSplit TID:" + targetID);
			// System.out.println("Params:");
			// for(int i = 0; i < sParams.length;i++)
			// {
			//     System.out.println("Param: " + i + " : " + sParams[i]);
			// }

			SOEObject s = server.getObjectFromAllObjects(targetID);
			int iAmountToSplit = Integer.parseInt(sParams[0]);
			long lDestinationContainer = Long.parseLong(sParams[1]);
			SOEObject d = server.getObjectFromAllObjects(lDestinationContainer);
			if(s instanceof ResourceContainer && d instanceof TangibleItem)
			{
				if(player.getInventorySlotsLeft() >= 1)
				{
					ResourceContainer source = (ResourceContainer)s;
					TangibleItem destination = (TangibleItem)d;
					ResourceContainer split = new ResourceContainer(source,iAmountToSplit);
					server.addObjectToAllObjects(split,false, false);
					if(destination.equals(player.getInventory()))
					{
						player.addItemToInventory(split);
						player.spawnItem(split);
						client.insertPacket(source.setStackQuantity(source.getStackQuantity() - iAmountToSplit, true));
					}
					else
					{
						destination.addLinkedObject(split);
						player.spawnItem(split);
						client.insertPacket(source.setStackQuantity(source.getStackQuantity(), true));
						client.insertPacket(source.setStackQuantity(source.getStackQuantity() - iAmountToSplit, true));
					}
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You must make 1 inventory slot available to Split."));
				}

			}
			else
			{
				DataLog.logEntry("handleResourceContainerSplit Constainers Not Handled " + s.getID() + " Class: " + s.getClass() + " : " + d.getID() + " Class: " + d.getClass() , "ZoneClientThread", Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
			}

		}catch(Exception e){
			DataLog.logException("Exception in handleResourceContainerSplit", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	private void handleAddPower(long targetID, String[] sParams) {
		try{
			if(player.getTargetID() == 0 || sParams[0].isEmpty())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Target."));
				return;
			}
			SOEObject o = server.getObjectFromAllObjects(player.getTargetID());
			int iPowerAmount = Integer.parseInt(sParams[0]);
			if(iPowerAmount <= 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid Amount."));
				return;
			}
			if(o instanceof Structure)
			{
				Structure s = (Structure)o;
				if(s.usesPower() && s.isAdmin(player.getID()))
				{
					int iPowerOnHand = 0;
					Vector<ResourceContainer> vRCList = new Vector<ResourceContainer>();
					for(int i =0; i < client.getPlayer().getInventoryItems().size();i++)
					{
						TangibleItem t = client.getPlayer().getInventoryItems().get(i);
						if(t instanceof ResourceContainer)
						{
							vRCList.add((ResourceContainer)t);
						}
					}
					int iPowerCount = 0;
					if(vRCList.isEmpty())
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("You do not have any power in your inventory."));
					}
					else
					{

						for(int i=0; i < vRCList.size();i++)
						{
							ResourceContainer r = vRCList.get(i);
							SpawnedResourceData rd = server.getResourceManager().getResourceByID(r.getResourceSpawnID());
							// System.out.println("Resource in inventory Type: " + rd.getResourceType());
							if(rd.getResourceType().contains("Radioactive"))
							{
								if(rd.getPotentialEnergy() >= 650)
								{
									iPowerOnHand += (r.getStackQuantity() * 5) / 2;
								}
								else
								{
									iPowerOnHand += (r.getStackQuantity() * 195) / 100;
								}
								iPowerCount++;
							}
							else if(rd.getResourceType().contains("Solar"))
							{
								if(rd.getPotentialEnergy() >= 950)
								{
									iPowerOnHand += (r.getStackQuantity() * 3) / 2;
								}
								else
								{
									iPowerOnHand += r.getStackQuantity();
								}
								iPowerCount++;
							}
							else if(rd.getResourceType().contains("Wind"))
							{
								iPowerOnHand += r.getStackQuantity();
								iPowerCount++;
							}
						}
					}
					if(iPowerOnHand > iPowerAmount)
					{
						boolean powerdeducted = false;
						int iToDeduct = iPowerAmount;
						int iDeduct = 0;
						for(int i=0; i < vRCList.size();i++)
						{
							ResourceContainer r = vRCList.get(i);
							SpawnedResourceData rd = server.getResourceManager().getResourceByID(r.getResourceSpawnID());
							if(rd.getResourceType().contains("Radioactive"))
							{
								if(rd.getPotentialEnergy() >= 650)
								{
									iPowerOnHand += (r.getStackQuantity() * 5) / 2;
								}
								else
								{
									iPowerOnHand += (r.getStackQuantity() * 195) / 100;
								}
								if(iToDeduct < iPowerOnHand)
								{
									iDeduct = 0;
									if(rd.getPotentialEnergy() >= 650)
									{
										iDeduct = (iToDeduct * 5) / 2;
									}
									else
									{
										iDeduct = (iToDeduct * 195) / 100;
									}
									iToDeduct -= iDeduct;
									client.insertPacket(r.setStackQuantity((r.getStackQuantity() - iDeduct), true));
									client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), r));
								}
								else if(iToDeduct >= iPowerOnHand)
								{
									iToDeduct -= iPowerOnHand;
									player.removeItemFromInventory(r);
									player.despawnItem(r);
									server.removeObjectFromAllObjects(r,false);
								}
								iPowerOnHand = 0;
							}
							else if(rd.getResourceType().contains("Solar"))
							{
								if(rd.getPotentialEnergy() >= 950)
								{
									iPowerOnHand += (r.getStackQuantity() * 3) / 2;
								}
								else
								{
									iPowerOnHand += r.getStackQuantity();
								}
								if(iToDeduct < iPowerOnHand)
								{
									iDeduct = 0;
									if(rd.getPotentialEnergy() >= 950)
									{
										iDeduct = (iToDeduct * 2) / 3;
									}
									else
									{
										iDeduct = iToDeduct;
									}
									iToDeduct -= iDeduct;
									client.insertPacket(r.setStackQuantity((r.getStackQuantity() - (int)iDeduct), true));
									client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), r));
								}
								else if(iToDeduct >= iPowerOnHand)
								{
									iToDeduct -= iPowerOnHand;
									player.removeItemFromInventory(r);
									player.despawnItem(r);
									server.removeObjectFromAllObjects(r,false);
								}
								iPowerOnHand = 0;
							}
							else if(rd.getResourceType().contains("Wind"))
							{
								iPowerOnHand += r.getStackQuantity();

								if(iToDeduct < iPowerOnHand)
								{
									iDeduct = 0;
									iDeduct = iToDeduct;
									iToDeduct -= iDeduct;
									client.insertPacket(r.setStackQuantity((r.getStackQuantity() - (int)iDeduct), true));
									client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), r));
								}
								else if(iToDeduct >= iPowerOnHand)
								{
									iToDeduct -= iPowerOnHand;
									player.removeItemFromInventory(r);
									player.despawnItem(r);
									server.removeObjectFromAllObjects(r,false);
								}
								iPowerOnHand = 0;
							}
							if(iToDeduct <= 0)
							{
								i = vRCList.size() +1;
								powerdeducted = true;
							}
						}
						if(powerdeducted)
						{
							s.addToPowerPool(iPowerAmount);
							client.insertPacket(PacketFactory.buildChatSystemMessage("Power Reseves now at " + s.getPowerPool()));
						}
						else
						{
							client.insertPacket(PacketFactory.buildChatSystemMessage("Error while making power deduction from inventory contact a csr."));
						}

					}
					else
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("You do no have enough power to deposit " + iPowerAmount + " units."));
					}
				}
				else
				{
					client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot add power to that!"));
				}
			}
			else
			{
				DataLog.logEntry("Error In handleAddPower, Target was not Structure: Class: " + o.getClass(), "ZoneClientThread", Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions.bLogToConsole, true);
			}
		}catch(Exception e){
			DataLog.logException("Exception in handleAddPower", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	private void handleHarvesterDiscardHopper(long targetID, String[] sParams){
		/**
		 *  05 00
                46 5E CE 80
                23 00 00 00
                16 01 00 00
                D3 9F ED 1A 00 00 00 00
                00 00 00 00
                00 00 00 00
                26 9E 9B C8
                BA F3 CB 8E 00 00 00 00 <--Harvester ID
                00 00 00 00    */
		try{
			System.out.println("handleHarvesterDiscardHopper");
			Harvester harvester = (Harvester)server.getObjectFromAllObjects(targetID);
			Hashtable<Long, SOEObject> vResources = harvester.getOutputHopper();
			Enumeration<SOEObject> vResourceEnum = harvester.getOutputHopper().elements();
			while (vResourceEnum.hasMoreElements()) {
				server.removeObjectFromAllObjects(vResourceEnum.nextElement(), false);
			}
			
			vResources.clear();
			//harvester.resetHarvesterResourceUpdateCounter();
			Vector<SpawnedResourceData> vResourcesAvailable = harvester.getResourcesAvailable();

			client.insertPacket(PacketFactory.buildBaselineHINO7(harvester, vResourcesAvailable));
			client.insertPacket(PacketFactory.buildDeltasMessageHINO7(harvester, vResourcesAvailable));
			client.insertPacket(PacketFactory.buildDeltasMessageHINO7_EmptyHopper(harvester));
		}catch(Exception e){
			DataLog.logException("Exception in handleHarvesterDiscardHopper", "ZoneClientThread", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	protected void handleSelectDraftSchematicToCraft(long targetID, String[] sParams) throws IOException {
		// targetID == 0
		// Schematic to deal with is the player's saved last schematic list, crafting schematic at index sParams[0]
		int schematicIndex = Integer.parseInt(sParams[0]);
		Vector<CraftingSchematic> vSentSchematics = player.getLastSentCraftingSchematicList();
		try {
			CraftingSchematic schematic = vSentSchematics.elementAt(schematicIndex);
			ManufacturingSchematic manuSchematic = schematic.createManufacturingSchematic();
			if (manuSchematic == null) {
				client.insertPacket(PacketFactory.buildChatSystemMessage("Manufacturing schematic not created due to internal server error."));
			}
			manuSchematic.setSTFFileName(Constants.STRING_ID_TABLE);
			manuSchematic.setSTFFileIdentifier(null);
			manuSchematic.setCraftedName(null);
			manuSchematic.setID(server.getNextObjectID());
			server.addObjectToAllObjects(manuSchematic, false, false);
			TangibleItem craftingTool = (TangibleItem)player.getSynchronizedListenObject();
			manuSchematic.setContainer(craftingTool, 4, false);
			player.setCurrentManufacturingSchematic(manuSchematic);
			player.spawnItem(manuSchematic);
			String sCraftedIFFFilename = schematic.getCraftedItemIFFFilename();
			ItemTemplate sCraftedItemTemplate=  DatabaseInterface.getTemplateDataByFilename(sCraftedIFFFilename);
			int templateID = sCraftedItemTemplate.getTemplateID();
			TangibleItem tanItemToCreate = null;
			if (schematic instanceof WeaponCraftingSchematic) {
				// TODO: Additional checks are needed here
				// Currently, this is creating ANYTHING belonging to the "Weapon" tab as a weapon.
				// However, there are things in the "Weapon" tab that are not themselves weapons.
				tanItemToCreate = new Weapon();
				Weapon weapon = (Weapon)tanItemToCreate;
				WeaponCraftingSchematic wSchem = (WeaponCraftingSchematic)schematic;
				weapon.setWeaponType(wSchem.getWeaponType());
				weapon.setSkillRequirement(wSchem.getCraftedWeaponRequiredSkillID());
			} else {
				if (templateID >= 8922 && templateID <= 8940) {
						tanItemToCreate = new CraftingTool();
				} else if (templateID >= 8953 && templateID <= 9303) {
					DeedTemplate deedTemplate = DatabaseInterface.getDeedTemplateByObjectTemplateID(templateID);
					if (deedTemplate != null) {
						System.out.println("Found deed template.");
						tanItemToCreate = new Deed(deedTemplate.getTemplateid(), templateID, server);
					} else {
						System.out.println("Unknown deed template ID associated with object template ID " + templateID);
					}
				}
					// TODO:  Weapon template IDs go here.
					
					// TODO:  Medical items go here
				else if (templateID >= 11380 && templateID <= 11551) {
						System.out.println("New Medical Item created with IFF filename " + sCraftedIFFFilename);
						tanItemToCreate = new MedicalItem();
				} else  {
					System.out.println("Crafting started on object with template ID " + templateID);
					tanItemToCreate = new TangibleItem();
				}
			}
			manuSchematic.setItemBeingCrafted(tanItemToCreate);
			tanItemToCreate.setIFFFileName(sCraftedIFFFilename);
			tanItemToCreate.setCrafterID(player.getID());
			tanItemToCreate.setSTFFileName(sCraftedItemTemplate.getSTFFileName());
			tanItemToCreate.setSTFFileIdentifier(sCraftedItemTemplate.getSTFFileIdentifier());
			tanItemToCreate.setOrientationW(1.0f);
			tanItemToCreate.setName(null, false);
			tanItemToCreate.setCustomizationData(null);
			tanItemToCreate.setEquipped(craftingTool, 0);
			tanItemToCreate.setID(server.getNextObjectID());
			server.addObjectToAllObjects(tanItemToCreate, false, false);
			player.spawnItem(tanItemToCreate);
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)5, player.getPlayData(), 10));
			client.insertPacket(player.getPlayData().setCraftingStage(2));
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)6, tanItemToCreate, 0x2100));
			TangibleItem nearbyStation = player.getNearbyCraftingStation();
			if (nearbyStation != null) {
				System.out.println("CAN create factory schematic.");
				// Set experimental points.
				
				
			} else {
				System.out.println("CANNOT create factory schematic.");
				client.insertPacket(player.setNumExperimentationPoints(-1));
			}
			client.insertPacket(PacketFactory.buildObjectController_CraftingSchematicComponentMessage(player, manuSchematic, schematic.getComponents(), craftingTool, tanItemToCreate, (nearbyStation != null)));
		} catch (ArrayIndexOutOfBoundsException e) {
			DataLog.logException("Invalid schematic ID " + schematicIndex, "handleSelectDraftSchematicToCraft", true, true, e);
		}
	}

	private void handleForage(long targetID, String[] sParams) throws IOException {
		/**
		 *  05 00
		 *  46 5E CE 80
		 *  23 00 00 00
		 *  16 01 00 00
		 *  D0 00 8F AF 00 00 00 00
		 *  00 00 00 00
		 *  20 00 00 40
		 *  80 9F 4F 49
		 *  00 00 00 00 00 00 00 00
		 *  00 00 00 00   */            
		if(player.hasSkill(45))
		{
			if(player.getStance() != Constants.STANCE_STANDING)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use","sys_forage_cant"));
				return;
			}
			if(player.getCurrentHam()[3] <= 100)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use","sys_forage_attrib"));
				return;
			}
			if(player.getCellID() != 0)
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use","sys_forage_inside"));
				return;
			}
			if(player.isForaging())
			{
				client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use","sys_forage_already"));
				return;
			}
			Waypoint lastForageArea = new Waypoint();
			lastForageArea.setX(player.getX());
			lastForageArea.setY(player.getY());
			lastForageArea.setZ(player.getZ());
			lastForageArea.setCellID(0);
			if(player.getLastForageArea() != null)
			{
				Waypoint w = player.getLastForageArea();
				if(w.getX() == lastForageArea.getX() && w.getY() == lastForageArea.getY() && w.getZ() == lastForageArea.getZ())
				{
					long areaCount = w.getCellID();
					areaCount++;
					if(player.getForageCooldown() >= 1)
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use","sys_forage_cant"));
						return;
					}
					else if(areaCount >= 5)
					{
						client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use","sys_forage_empty"));
						return;
					}
					w.setCellID(areaCount);
				}
				else
				{
					player.setLastForageArea(lastForageArea);
				}
			}
			else
			{
				player.setLastForageArea(lastForageArea);
			}
			player.setForageType(Constants.FORAGE_TYPE_GENERAL);
			player.setForageCooldown(9000);
			player.setIsForaging(true);
			client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use","sys_forage_start"));
			player.updateCurrentHam(Constants.HAM_INDEX_ACTION, -100);
			client.insertPacket(PacketFactory.buildPlayerAnimation(player, "forage"),Constants.PACKET_RANGE_CHAT_RANGE);
		}
		else
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use","sys_forage_noskill"));
		}            
	}

	protected void handleInsertItemIntoSchematicSlot(SOEInputStream dIn) throws IOException{
		//PacketUtils.printPacketToScreen(dIn.getBuffer(),dIn.getBufferLength(), "handleInsertItemIntoSchematicSlot");
		/**
		 * 05 00
		 * 46 5E CE 80
		 * 83 00 00 00
		 * 07 01 00 00 // Type message
		 * 86 DD 31 7A 00 00 00 00 <--Player ID
		 * 00 00 00 00 <--Spacer
		 * 34 B9 1E C6 00 00 00 00 <--Object ID to Insert into Slot
		 * 00 00 00 00
		 * 00 00 00 00
		 * 02 <--UPD Count
		 * F6 C1  */

		long playerID = dIn.readLong();
		//System.out.println("InsertItemIntoSlot:  Received Object ID " + Long.toHexString(playerID));
		if (playerID == player.getID()) {
			//System.out.println("Object ID IS the Player ID");
			/*int spacer =*/ dIn.readInt();
			long objectIDToInsert = dIn.readLong();
			int slotID = (int)dIn.readLong();
			byte updateCount = dIn.readByte();
			ManufacturingSchematic schematic = player.getCurrentManufacturingSchematic();
			SOEObject objectToInsert = server.getObjectFromAllObjects(objectIDToInsert);
			if (objectToInsert instanceof ResourceContainer) {
				ResourceContainer theResource = (ResourceContainer)objectToInsert;
				int stackSize = theResource.getStackQuantity();
				CraftingSchematicComponent[] components = schematic.getSchematicComponentData();
				if (components[slotID] != null) {
					int stackSizeNeeded = components[slotID].getComponentQuantity();
					int amountToPutIn = 0;
					if (stackSizeNeeded > stackSize) {
						// Go ahead and put 
						amountToPutIn = stackSize;
					} else {
						amountToPutIn = stackSizeNeeded;
					}
					int newResourceQuantity = stackSize - amountToPutIn; // LEAVE HERE -- WILL BE USED WHEN CRAFTING DECLARED WORKING.
    				theResource.setQuantityAttribute(stackSize - amountToPutIn);
    				client.insertPacket(theResource.setStackQuantity(stackSize - amountToPutIn, true));
					if (newResourceQuantity == 0) {
        				player.despawnItem(theResource);
        				
        			} 
        			
					// MSCO6 delta
					client.insertPacket(schematic.setIngredientUpdateCounter(updateCount, true));

					// MSCO7 deltas
					/*if (!schematic.getHasSentFirstBaseline7AddIngredientDelta() && false) {
						System.out.println("Send first time MSCO7 delta.");
						schematic.updateVID1ValueAtIndex(slotID, 4, false);
						schematic.addIngredientToSlotIngredientIDList(slotID, theResource.getResourceSpawnID(), false);
						schematic.addIngredientToSceneIngredientIDList(slotID, theResource.getID());
						schematic.updateItemQuantityInIngredientSlot(slotID, amountToPutIn, false);
						schematic.updateVID5ValueAtIndex(slotID, 0, false);
						schematic.setVID7((byte)8, false);
						schematic.setVID20(updateCount, false);
						byte[] deltasMSCO7FirstTime = PacketFactory.buildDeltasMSCO7FirstTimeAddIngredient(schematic, (short)slotID);
						
						client.insertPacket(deltasMSCO7FirstTime);
						//client.insertPacket(PacketFactory.buildDeltasMSCO7FirstTime(schematic));
						schematic.setHasSentFirstBaseline7AddIngredientDelta(true);
					}
					else
					*/
					{
						byte[][] vDeltas = new byte[5][];
						vDeltas[0] = schematic.updateVID1ValueAtIndex(slotID, 4, true);
						vDeltas[1] = schematic.addIngredientToSlotIngredientIDList(slotID, theResource, true);
						
						schematic.addIngredientToSceneIngredientIDList(slotID, theResource.getID());
						vDeltas[2] = schematic.updateItemQuantityInIngredientSlot(slotID, amountToPutIn, true);
						vDeltas[3] = schematic.updateVID5ValueAtIndex(slotID, 0, true);
						vDeltas[4] = schematic.setVID7((byte)8, true);
						for (int i = 0; i < vDeltas.length; i++) {
							//PacketUtils.printPacketToScreen(vDeltas[i], "MSCO7 delta " + i + " insert resource.");
							client.insertPacket(vDeltas[i]);
						}
					}
					client.insertPacket(PacketFactory.buildDequeueGenericObjectControllerMessage010C(player.getID(), Constants.InsertItemIntoSchematicSlot, 0, updateCount));
				} 
			} else if (objectToInsert instanceof TangibleItem) {
				// Get the quantity required by the schematic, compare to the stack size in inventory.  If quantity < stackSize, we need to split it off, assign a new object ID, and respawn it in the schematic.
				// Otherwise, just update the containment to have the stack in the schematic.
				
				System.out.println("Tangible item being inserted.  WIP");
				
				TangibleItem item = (TangibleItem)objectToInsert;
				int stackSize = Math.max(item.getStackQuantity(), 1); // If the tangible item exists, there's always at least 1 unit in inventory.
				CraftingSchematicComponent[] components = schematic.getSchematicComponentData();
				if (components[slotID] != null) {
					int stackSizeNeeded = components[slotID].getComponentQuantity();
					boolean bNeedIdenticalComponents = components[slotID].getComponentIdentical();
					int amountToPutIn = 0;
					boolean bCanAdd = true;
					if (bNeedIdenticalComponents) {
						// Compare serial numbers of any components already there.
						long[] lAlreadyPresentObjects = schematic.getObjectIDsBySlot(slotID);
						for (int i = 0; i < lAlreadyPresentObjects.length && lAlreadyPresentObjects[i] != 0; i++) {
							TangibleItem presentItem = (TangibleItem)server.getObjectFromAllObjects(lAlreadyPresentObjects[i]);
							if (presentItem != null) {
								long lSerial = presentItem.getSerialNumber();
								if (lSerial == item.getSerialNumber()) {
									// Go ahead and add it.
									bCanAdd = false;
								} else {
									System.out.println("Mismatched serial number on required identical items.  Serial of objects installed: " + lSerial + ", serial of item being inserted: " + item.getSerialNumber());
									client.insertPacket(PacketFactory.buildChatSystemMessage("The item being inserted does not have the same serial number as items already installed in the schematic slot."));
								}
							}
						}
					}
					if (bCanAdd) {
						if (stackSizeNeeded > stackSize) {
							// Go ahead and put 
							amountToPutIn = stackSize;
						} else {
							amountToPutIn = stackSizeNeeded;
						}
						int newResourceQuantity = stackSize - amountToPutIn; // LEAVE HERE -- WILL BE USED WHEN CRAFTING DECLARED WORKING.
        				client.insertPacket(item.setStackQuantity(newResourceQuantity, true));
        				// Gotta split the stack, if there are more items in inventory than are needed in the resource schematic.
        				
        				
        				// MSCO6 delta
						client.insertPacket(schematic.setIngredientUpdateCounter(updateCount, true));
	
						// MSCO7 deltas
						/*if (!schematic.getHasSentFirstBaseline7AddIngredientDelta() && false) {
							//System.out.println("Send first time MSCO7 delta.");
							schematic.updateVID1ValueAtIndex(slotID, 4, false);
							schematic.addIngredientToSlotIngredientIDList(slotID, item.getID(), false);
							schematic.addIngredientToSceneIngredientIDList(slotID, item.getID());
							schematic.updateItemQuantityInIngredientSlot(slotID, amountToPutIn, false);
							schematic.updateVID5ValueAtIndex(slotID, 0, false);
							schematic.setVID7((byte)8, false);
							schematic.setVID20(updateCount, false);
							byte[] deltasMSCO7FirstTime = PacketFactory.buildDeltasMSCO7FirstTimeAddIngredient(schematic, (short)slotID);
							
							client.insertPacket(deltasMSCO7FirstTime);
							//client.insertPacket(PacketFactory.buildDeltasMSCO7FirstTime(schematic));
							schematic.setHasSentFirstBaseline7AddIngredientDelta(true);
						}
						else
						*/
						{
							System.out.println("MSCO7 deltas message, slot ID " + slotID + ", object to insert: " + item.getID());
							int currentQuantityAtIndex = schematic.getSlotResourceQuantityInsertedAtIndex(slotID);
							currentQuantityAtIndex += amountToPutIn;
							byte[][] vDeltas = new byte[5][];
							vDeltas[0] = schematic.updateVID1ValueAtIndex(slotID, 4, true);
							vDeltas[1] = schematic.addIngredientToSlotIngredientIDList(slotID, item, true);
							schematic.addIngredientToSceneIngredientIDList(slotID, item.getID());
							vDeltas[2] = schematic.updateItemQuantityInIngredientSlot(slotID, currentQuantityAtIndex, true);
							vDeltas[3] = schematic.updateVID5ValueAtIndex(slotID, 0, true);
							vDeltas[4] = schematic.setVID7((byte)8, true);
							for (int i = 0; i < vDeltas.length; i++) {
								if (vDeltas[i] != null) {
									PacketUtils.printPacketToScreen(vDeltas[i], "MSCO7 delta " + i + " inserting tangible item.");
									client.insertPacket(vDeltas[i]);	
								} else {
									System.out.println("Null deltas message " + i);
								}
								
							}
						}
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(item, schematic, -1));
					} else {
						client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot add this item:  An item with a different serial number is already present in the slot."));
					}
					client.insertPacket(PacketFactory.buildDequeueGenericObjectControllerMessage010C(player.getID(), Constants.InsertItemIntoSchematicSlot, 0, updateCount));
				} 

			}
		} else {
			DataLog.logEntry("Error:  Mismatched object ID", "ZoneClientThread.handleInsertItemIntoSchematicSlot", Constants.LOG_SEVERITY_CRITICAL, true, true);
		}
	}

	protected void handleRemoveItemFromSchematicSlot(SOEInputStream dIn) throws IOException {
		long playerID = dIn.readLong();
		//System.out.println("RemoveItemFromSlot:  Received Object ID " + Long.toHexString(playerID));
		if (playerID == player.getID()) {
			//System.out.println("Object ID IS the Player ID");
			/*int spacer =*/ dIn.readInt();
			int slotID = dIn.readInt();
			/*long objectID =*/ dIn.readLong();
			byte updateCount = dIn.readByte();
			ManufacturingSchematic schematic = player.getCurrentManufacturingSchematic();
			// So, we need to get whatever it is by ID.
			long[] vSceneObjectIDs = schematic.getAllSceneObjectIDsInSlot(slotID);
			long[] vSlotIngredientIDs=schematic.getAllObjectIDsInSlots()[slotID];
			CraftingSchematicComponent component = schematic.getCraftingSchematic().getComponents().elementAt(slotID);
			if (component.getIsResource()) {
				for (int i = 0; i < vSceneObjectIDs.length && vSceneObjectIDs[i] != 0; i++) {
					//System.out.println("Checking scene object with ID " + Long.toHexString(vSceneObjectIDs[i]));
					int quantityToAdd = schematic.getSlotResourceQuantityInserted()[slotID];
					System.out.println("Got previous quantity inserted: " + quantityToAdd);
					SpawnedResourceData resourceData = server.getResourceManager().getResourceByID(vSlotIngredientIDs[i]);
					if (resourceData != null) {
						Vector<TangibleItem> vInventoryItems = player.getInventoryItems();
						boolean bFound = false;
						for (int j = 0; j < vInventoryItems.size() && !bFound; j++) {
							TangibleItem item = vInventoryItems.elementAt(j);
							if (item instanceof ResourceContainer) {
								ResourceContainer rContainer = (ResourceContainer)item;
								if (rContainer.getID() == vSceneObjectIDs[i]) {
									//      					System.out.println("Found resource container in inventory.");
									bFound = true;
									int previousQuantity = rContainer.getStackQuantity();
									player.spawnItem(rContainer);
									int newQuantity = previousQuantity + quantityToAdd;
									rContainer.setQuantityAttribute(newQuantity);
									client.insertPacket(rContainer.setStackQuantity(newQuantity, true));
								}
							}
						}
						if (!bFound) {
							// This should never happen.
							//    			System.out.println("Resource container NOT found in inventory -- creating new one.");
							ResourceContainer container = new ResourceContainer();
							player.setLastUpdatedResourceContainer(container);
							container.setID(client.getServer().getNextObjectID());
							int containerTemplateID = resourceData.getResourceContainerTemplateID();
							container.setTemplateID(containerTemplateID);
							container.setName(resourceData.getResourceType(), false);
							container.setCustomizationData(null);
							container.setOwner(player);
							container.setEquipped(player.getInventory(), -1);
							container.setStackQuantity(quantityToAdd, false);
							player.addItemToInventory(container);
							container.setID(server.getNextObjectID());
							container.setConditionDamage(0, false);
							container.setMaxCondition(100, false);
							container.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
							container.setResourceSpawnID(resourceData.getID());
							container.setResourceType(resourceData.getIffFileName());
							container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_NAME, resourceData.getName()));
							container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_CLASS, resourceData.getResourceClass() + " " + resourceData.getResourceType()));
							short coldResist = resourceData.getColdResistance();
							short conductivity = resourceData.getConductivity();
							short decayResist = resourceData.getDecayResistance();
							short entangleResist = resourceData.getEntangleResistance();
							short flavor = resourceData.getFlavor();
							short heatResist = resourceData.getHeatResistance();
							short malleability = resourceData.getMalleability();
							short overallQuality  = resourceData.getOverallQuality();
							short potentialEnergy = resourceData.getPotentialEnergy();
							short shockResist = resourceData.getShockResistance();
							short unitToughness = resourceData.getUnitToughness();

							if (coldResist != 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_COLD_RESIST, String.valueOf(coldResist)));
							}
							if (conductivity != 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_CONDUCTIVITY, String.valueOf(conductivity)));
							}
							if (decayResist != 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_DECAY_RESIST, String.valueOf(decayResist)));
							}
							if (entangleResist != 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_ENTANGLE_RESISTANCE, String.valueOf(entangleResist)));
							}
							if (flavor != 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_FLAVOR, String.valueOf(flavor)));
							}
							if (heatResist!= 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_HEAT_RESIST, String.valueOf(heatResist)));
							}
							if (malleability != 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_MALLEABILITY, String.valueOf(malleability)));
							}
							if (overallQuality != 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_QUALITY, String.valueOf(overallQuality)));
							}
							if (potentialEnergy!= 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY, String.valueOf(potentialEnergy)));
							}
							if (shockResist!= 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE, String.valueOf(shockResist)));
							}
							if (unitToughness != 0) {
								container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_TOUGHNESS, String.valueOf(unitToughness)));
							}
							container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_VOLUME, String.valueOf(quantityToAdd) + "/" + String.valueOf(Constants.MAX_STACK_SIZE)));
							player.spawnItem(container);

						}
					}
				}
			} else { // It's a component
				// Not yet handled
				for (int i = 0; i < vSceneObjectIDs.length; i++) {
					TangibleItem slotItem = (TangibleItem)server.getObjectFromAllObjects(vSceneObjectIDs[i]);
					if (slotItem != null) {
						client.insertPacket(PacketFactory.buildUpdateContainmentMessage(slotItem, player.getInventory(), -1));
					}
				}
				
			}
			// We needs the deltas messages my love.
			//System.out.println("Sending deltas messages...");
			client.insertPacket(schematic.setIngredientUpdateCounter(updateCount, true));
			// MSCO7 deltas
			client.insertPacket(schematic.updateVID1ValueAtIndex(slotID, 0, true));
			client.insertPacket(schematic.removeIngredientsFromSlotIngredientIDList(slotID, true));// Causes client to perform an illegal operation
			schematic.removeIngredientFromSceneIngredientIDList(slotID);
			client.insertPacket(schematic.updateItemQuantityInIngredientSlot(slotID, 0, true)); // vID3 -- causes client crash???
			client.insertPacket(schematic.updateVID5ValueAtIndex(slotID, -1, true));
			client.insertPacket(schematic.setVID7((byte)20, true));
			client.insertPacket(PacketFactory.buildDequeueGenericObjectControllerMessage010C(player.getID(), Constants.RemoveItemFromSchematicSlot, 0, updateCount));
		}
	}

	private void handleImageDesign(long targetID, String[] sParams) throws IOException {
		System.out.print("Image Design Requested");
		SOEObject o = server.getObjectFromAllObjects(targetID);
		if(o instanceof NPC)
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("you can only Imagedesign other Players"));
		}
		else if(o instanceof Player)
		{
			Player customer = (Player)o;
			if(customer.equals(player))//image design on ourselves hmmmm....
			{
				client.insertPacket(PacketFactory.buildObjectControllerMessageImageDesignStart(player, customer));
			}
			else
			{
				client.insertPacket(PacketFactory.buildObjectControllerMessageImageDesignStart(player, customer));
				customer.getClient().insertPacket(PacketFactory.buildObjectControllerMessageImageDesignStart(player, customer));
			}
		}
		else
		{
			client.insertPacket(PacketFactory.buildChatSystemMessage("Invalid target for Imagedesign"));
		}
	}

	private void handleNextCraftingStage(long targetID, String[] sParams) throws IOException {
		//System.out.println("Next crafting stage.  Listing parameters.");
		//for (int i = 0; i < sParams.length; i++) {
		//	System.out.println("Paramater["+i+"]=["+sParams[i]+"]");
		//}
		byte responseByte = 0;
		try {
			responseByte = Byte.parseByte(sParams[0]);
		} catch (Exception e) {
			System.out.println("NextCraftingStage: Error parsing sParams[0] -- " + sParams[0]);
			client.insertPacket(PacketFactory.buildDisconnectClient(client, (short)Constants.DISCONNECT_REASON_KICK));
			terminate();
		}
		int currentCraftingStage = player.getPlayData().getCraftingStage();

		switch (currentCraftingStage) {
		case Constants.CRAFTING_STAGE_NOT_CRAFTING: {
			// Uhh... how did we get here, exactly?
			//System.out.println("Received next crafting stage, but user is not currently crafting.");
			break;
		}
		case Constants.CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC: {
			//System.out.println("Received next crafting stage, but user is selecting schematic.  Expected:  ObjController -- SelectSchematicToCraft.");
			break;
		} 
		case Constants.CRAFTING_STAGE_INSERT_COMPONENTS: {
			//System.out.println("Has inserted components -- ready to move on to next stage.  Sending hardcoded crafting stage of ID 4.");

			// Note:  On my CORE3 capture, the next crafting stage is 4.  Also, an MSCO3 deltas message to vID 5 is sent, and a second Mother of all Deltas Messages to MSCO7
			// vIDs 8, 9, 0A,
			ManufacturingSchematic schematic = player.getCurrentManufacturingSchematic();
			schematic.setCanRecoverInstalledItems(false);
			Vector<ManufacturingSchematicAttribute> vAttribs = new Vector<ManufacturingSchematicAttribute>();
			ManufacturingSchematicAttribute complexityAttribute = new ManufacturingSchematicAttribute();
			CraftingSchematic cSchematic = schematic.getCraftingSchematic();
			// Add attributes to the Tangible item at this stage, based on what type of thing we are crafting.
			TangibleItem itemBeingCrafted = schematic.getItemBeingCrafted();
			itemBeingCrafted.setSerialNumber(server.getNextSerialNumber(), true);
			complexityAttribute.setAttributeName("crafting");
			complexityAttribute.setAttributeType("compexity");
			complexityAttribute.setAttributeValue(schematic.getCraftingSchematic().getComplexity());
			vAttribs.add(complexityAttribute);
			ManufacturingSchematicAttribute experienceAttrib = new ManufacturingSchematicAttribute();
			experienceAttrib.setAttributeName("crafting");
			experienceAttrib.setAttributeType("xp");
			experienceAttrib.setAttributeValue(schematic.getCraftingSchematic().getExperienceGainedFromCrafting());
			if (experienceAttrib.getAttributeValue() != 0) {
				vAttribs.add(experienceAttrib);
			}
			int successRating = schematic.setExperimentalValues(client);
			if (successRating < Constants.CRAFTING_ASSEMBLY_CRITICAL_FAILURE) {
				//CraftingTool tool = schematic.getToolUsedToCraft();
				TangibleItem nearbyStation = player.getNearbyCraftingStation();
				byte[] thePacketBeingSent = null;

				if (nearbyStation != null) {
					CraftingExperimentationAttribute[] expAttributes = schematic.getCraftingSchematic().getAttributes();
					if (expAttributes != null) {
						if (expAttributes.length > 0) {
							thePacketBeingSent = player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_EXPERIMENT_CREATE_MANUFACTURE_SCHEMATIC_OR_CREATE_PROTOTYPE); // This determines whether we are able to use experimentation or not.
						} else {
							thePacketBeingSent = player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SET_CUSTOMIZATION_DATA);
						}
					} else {
						thePacketBeingSent = player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SET_CUSTOMIZATION_DATA);		
					}
				} else {
					thePacketBeingSent = player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SET_CUSTOMIZATION_DATA);
				}
				
				
				//PacketUtils.printPacketToScreen(thePacketBeingSent, "PLAY9 Set Crafting Stage");
				client.insertPacket(thePacketBeingSent);
				thePacketBeingSent = PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)5, player.getPlayData(), 10);
				//PacketUtils.printPacketToScreen(thePacketBeingSent, "PLAY9 Set vID5");
				client.insertPacket(thePacketBeingSent);
	
				thePacketBeingSent = schematic.addSchematicAttribute(vAttribs, true);
				//PacketUtils.printPacketToScreen(thePacketBeingSent, "MSCO3 set crafting attributes");
				client.insertPacket(thePacketBeingSent);
	
				//thePacketBeingSent = PacketFactory.buildDeltasMSCO7FirstTimeSetExperimentation(schematic);
				//PacketUtils.printPacketToScreen(thePacketBeingSent, "MSCO7 Initialize Experimentation and Customization");
				//client.insertPacket(thePacketBeingSent);
	
				thePacketBeingSent = itemBeingCrafted.setComplexity(schematic.getCraftingSchematic().getComplexity(), true);
				//PacketUtils.printPacketToScreen(thePacketBeingSent, "TANO3 Set Complexity");
				client.insertPacket(thePacketBeingSent);
	
				thePacketBeingSent = PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)9, itemBeingCrafted, 0);
				//PacketUtils.printPacketToScreen(thePacketBeingSent, "TANO3 Unknown update");
				client.insertPacket(thePacketBeingSent);
				//client.insertPacket(itemBeingCrafted.setName(itemBeingCrafted.getName() + " test", true));
	
				/*
				 * This packet tells us what our assembly success rating was.
				 * 0= Amazing success
				 * 1= Great success
				 * 2= Good success
				 * 3= Moderate success
				 * 4= Success
				 * 5= Marginal success
				 * 6= "ok"
				 * 7= barely succeeded.
				 * 8= Critical failure.  If tihs happens, we have to auto-revert everything back to the previous stage.
				 * 9= Internal failure 
				 */
	
			} 
			//System.out.println("Built crafting assembly success rating.  Value: " + successRating);
			client.insertPacket(PacketFactory.buildCraftingAssemblySuccessRating(player.getID(), 0x0109, successRating, responseByte));
			//System.out.println("Sent assembly success rating.");
			if (cSchematic instanceof WeaponCraftingSchematic) {
				// There's a whole bunch more attributes we need to add in to the TangibleItem
				WeaponCraftingSchematic wSchematic = (WeaponCraftingSchematic)cSchematic;
				Hashtable<Byte, Double[]> vCraftingLimits = wSchematic.getCraftingLimits();
				Weapon weapon = (Weapon)itemBeingCrafted;
				float[] fCurrentExperimentalValues = schematic.getVID9CurrentExperimentalValueArray();
				CraftingExperimentationAttribute[] vExperimentalAttributes = cSchematic.getAttributes();
				
				for (int i = 0; i < vExperimentalAttributes.length || i < fCurrentExperimentalValues.length; i++) {
					//System.out.println("Experimental attribute " + i + " has name " + vExperimentalAttributes[i].getStfFileIdentifier() + " and experimental value " + fCurrentExperimentalValues[i]);
					// So, all we have to do is get the correct range from vCraftingLimits, set the value in the Weapon, and update the client with the new attributes once we're done.
					// Let's make it a function.
					weapon.calculateAttribute(vExperimentalAttributes[i].getStfFileIdentifier(), fCurrentExperimentalValues[i], vCraftingLimits, client);
				}
			} else if (cSchematic instanceof FoodCraftingSchematic) {
				// There's a whole bunch of OTHER attributes we need to add in to the Tangible item.
			}
			//System.out.println("Build Attribute List Message on item being crafted.");
			client.insertPacket(PacketFactory.buildAttributeListMessage(client, itemBeingCrafted));

			break;
		}
		case Constants.CRAFTING_STAGE_EXPERIMENT_CREATE_MANUFACTURE_SCHEMATIC_OR_CREATE_PROTOTYPE: {
			//System.out.println("Advance from experiment window.  Next Crafting Stage string paramater: " + responseByte);
			client.insertPacket(player.getPlayData().setCraftingStage(Constants.CRAFTING_STAGE_SET_CUSTOMIZATION_DATA));
			// Missing a packet here.
			client.insertPacket(PacketFactory.buildCraftingAssemblySuccessRating(player.getID(), 0x0109, 4, responseByte)); // This will trigger a "Get Prototype" packet.
			break;
		}
		case Constants.CRAFTING_STAGE_SET_CUSTOMIZATION_DATA: {
			//System.out.println("Advance from setCustomizationData crafting stage.");
			client.insertPacket(player.getPlayData().setCraftingStage(5));
			client.insertPacket(PacketFactory.buildCraftingAssemblySuccessRating(player.getID(), 0x0109, 4, responseByte)); // This will trigger a "Get Prototype" packet.
			break;
		}
		default: {
			//System.out.println("Unknown crafting stage with ID " + currentCraftingStage);
			break;
		}
		}
		//System.out.println("Done handle next crafting stage.");
	}


	protected void handleCancelCraftingSession(long targetID, String[] sParams) throws IOException {
		// Reset Play9 vIDS.  vID 1= 0, vID 2 = 0, vID 3 = 0, vID 5 = 0,
		//System.out.println("Handle cancel crafting session.");

		ManufacturingSchematic schematicBeingCrafted = player.getCurrentManufacturingSchematic();
		if (schematicBeingCrafted != null) {
			boolean bRecoverItems = schematicBeingCrafted.getCanRecoverInstalledItems();
			//System.out.println("Can recover items? " + bRecoverItems);
			// Temp code to allow us to get our stuff back.
			//bRecoverItems = true;
			long[][] itemIdInSlots = schematicBeingCrafted.getAllObjectIDsInSlots();
			long[][] sceneIdInSlots = schematicBeingCrafted.getAllSceneIDsInSlots();
			TangibleItem inventory = player.getInventory();
			for (int i = 0; i < itemIdInSlots.length; i++) {
				for (int j = 0; j < itemIdInSlots[i].length; j++) {
					if (!bRecoverItems) {
						if (itemIdInSlots[i][j] != 0) {
							if (itemIdInSlots[i][j] == sceneIdInSlots[i][j]) {
								TangibleItem itemToRemoveFromWorld = (TangibleItem)server.getObjectFromAllObjects(sceneIdInSlots[i][j]);
								if (itemToRemoveFromWorld instanceof ResourceContainer) {
									// Do nothing -- resources already deducted.
								} else {
									//System.out.println("Remove object " + itemToRemoveFromWorld.getID() + " from the world -- it has been integrated into the newly crafted item.");
									inventory.removeLinkedObject(itemToRemoveFromWorld);
									player.removeItemFromInventory(itemToRemoveFromWorld);
									player.despawnItem(itemToRemoveFromWorld);
									server.removeObjectFromAllObjects(itemToRemoveFromWorld, false);
								}
							}
						}
					} else {
						// If it's a TangibleItem, respawn it in inventory.
						if (itemIdInSlots[i][j] != 0) {
							TangibleItem itemToRemoveFromWorld = (TangibleItem)server.getObjectFromAllObjects(sceneIdInSlots[i][j]);
							if (itemToRemoveFromWorld instanceof ResourceContainer) {
								// It's a resource.
								//System.out.println("Recovering items, return resources to the container.");
								
								int iResourceQuantity = schematicBeingCrafted.getSlotResourceQuantityInsertedAtIndex(i);
								ResourceContainer resource = (ResourceContainer)itemToRemoveFromWorld;
								int currentResourceQuantity = resource.getStackQuantity();
								client.insertPacket(resource.setStackQuantity(resource.getStackQuantity() + iResourceQuantity, true));
								client.insertPacket(PacketFactory.buildAttributeListMessage(client, resource));
								if (currentResourceQuantity == 0) {
									player.spawnItem(resource);
								}
							} else {
								// Incomplete -- what if it was part of a stack previously?
								client.insertPacket(PacketFactory.buildUpdateContainmentMessage(itemToRemoveFromWorld, player.getInventory(), -1));
							}
						}
					}
				}
			}

		// Despawn it's tangible item first.
			TangibleItem itemBeingCrafted = schematicBeingCrafted.getItemBeingCrafted();
			if (itemBeingCrafted != null) {
				player.despawnItem(itemBeingCrafted);
				server.removeObjectFromAllObjects(itemBeingCrafted, false);
				schematicBeingCrafted.setItemBeingCrafted(null);
			}
			if (schematicBeingCrafted.getNumberOfFactoryItems() == 0) {
				player.despawnItem(schematicBeingCrafted);
				server.removeObjectFromAllObjects(schematicBeingCrafted, false);
			}
		}
		PlayerItem playData = player.getPlayData();
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)1, playData, 0));
		client.insertPacket(playData.setCraftingStage(0));
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)3, playData, 0l));
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)5, playData, -1));

		player.setCurrentManufacturingSchematic(null);
		System.out.println("Done handle cancel crafting session.");
	}

	// Handle the parsing and setting of:
	// The name of the object being crafted.
	// The condition of the object being crafted, I think.
	// The appearance customization data of hte object being crafted, if any.
	protected void handleCraftingSetCustomizationData(SOEInputStream dIn) throws IOException {
		long playerID = dIn.readLong();
		/*int spacer =*/ dIn.readInt();
		String sCustomName = dIn.readUTF16();
		/*byte hardCodedNegativeOne =*/ dIn.readByte();
		int condition = dIn.readInt();  // Note!  If this item becomes a manufacturing schematic, this variable refers to the number of items to be made by the schematic.
		byte customizationDataLen = dIn.readByte();
		System.out.println("Customization data length = " + customizationDataLen);
		System.out.println("Condition variable: " + condition);
		System.out.println("Name: " + sCustomName);
		// TODO -- figure out how to parse this correctly and make it into an appearance string.
		ManufacturingSchematic schematic = player.getCurrentManufacturingSchematic();
		schematic.setNumberOfFactoryItems(condition);
		TangibleItem itemToCreate = schematic.getItemBeingCrafted();
		client.insertPacket(itemToCreate.setName(sCustomName, true));
		schematic.setCraftedName(sCustomName);
		itemToCreate.setCustomizationData(null);
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)4, itemToCreate, "", false));
		client.insertPacket(itemToCreate.setMaxCondition(condition, true));
		client.insertPacket(itemToCreate.setConditionDamage(0, true));
		schematic.setSTFFileName(itemToCreate.getSTFFileName());
		schematic.setSTFFileIdentifier(itemToCreate.getSTFFileIdentifier());
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO, (byte)3, (short)1, (short)1, schematic, schematic.getSTFFileName(), schematic.getSTFFileIdentifier()));
		client.insertPacket(schematic.setCustomName("Schematic: " + sCustomName, true));
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO, (byte)3, (short)1, (short)4, schematic, condition));
		client.insertPacket(PacketFactory.buildDequeueGenericObjectControllerMessage010C(playerID, Constants.CraftingSetCustomization, 0, (byte)0)); // This will trigger a "next crafting stage" packet.
	}


	protected void handleCreatePrototype(long targetID, String[] sParams) throws IOException {
		byte tailByte = 0;
		boolean bCreateItem = true;
		try {
			tailByte = Byte.parseByte(sParams[0]);
			bCreateItem = (Byte.parseByte(sParams[1]) == 1);
		} catch (Exception e) {
			System.out.println("Handle Create Prototype -- error parsing sParams[0]: " + sParams[0]);
			client.insertPacket(PacketFactory.buildDisconnectClient(client, Constants.DISCONNECT_REASON_KICK));
			terminate();
		}
		client.insertPacket(PacketFactory.buildDequeueGenericObjectControllerMessage010C(player.getID(), 0x010A, 1, tailByte)); // -- Dunno if this one's needed or not.
		//client.insertPacket(PacketFactory.buildDequeueGenericObjectControllerMessage010C(player.getID(), 0x010A, 0, (byte)0));
		client.insertPacket(PacketFactory.buildDequeueCreatePrototype(player.getID()));

		ManufacturingSchematic schematic = player.getCurrentManufacturingSchematic();

		TangibleItem itemToCreate = schematic.getItemBeingCrafted();
		
		schematic.setItemBeingCrafted(null);
		CraftingSchematic theBaseSchematic = schematic.getCraftingSchematic();
		client.insertPacket(itemToCreate.setComplexity(theBaseSchematic.getComplexity(), true));
		int xpToGrant = theBaseSchematic.getExperienceGainedFromCrafting();
		int xpType = theBaseSchematic.getExperienceTypeToGrant();

		if (bCreateItem) {
			
			CraftingTool theCraftingTool = schematic.getToolUsedToCraft();
			client.insertPacket(theCraftingTool.setItemBeingCrafted(itemToCreate, theBaseSchematic, true));
			client.insertPacket(PacketFactory.buildChatSystemMessage("ui_craft", "completed_prototype"));
			Attribute crafterAttribute = itemToCreate.getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_CRAFTER);
			if (crafterAttribute == null) {
				crafterAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_CRAFTER, player.getFirstName());
				itemToCreate.addAttribute(crafterAttribute);
			} else {
				crafterAttribute.setAttributeValue(player.getFirstName());
			}
			
			client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), itemToCreate));
		} else {
			xpToGrant = ((xpToGrant * 105) / 100);
			player.despawnItem(itemToCreate);
			server.removeObjectFromAllObjects(itemToCreate, false);
		}
		if (xpToGrant != 0) {
			String xpName = DatabaseInterface.getExperienceNameFromID(xpType);
			if (xpName != null) {
				player.updateExperience(null, xpType, xpToGrant);
			} else {
				System.out.println("Create prototype -- no experience tied to this object.");
			}
		}
		//System.out.println("Handled.");
	}

	private void handleSetSkillTitle(long targetID, String[] sParams) throws IOException {
		String sSkillTitle = sParams[0];

		client.insertPacket(player.getPlayData().setTitle(sSkillTitle,true));
		client.insertPacket(PacketFactory.buildChatSystemMessage("Skill Sitle Set"));
		//System.out.println("Skill Title Set to: " + player.getPlayData().getTitle());
	}

	private void handleCraftingExperimentationMessage(SOEInputStream dIn) throws IOException {
		//byte[] data = dIn.getBuffer();
		
		long playerID = dIn.readLong();
		/*int spacer =*/ dIn.readInt();
		byte expCounter = dIn.readByte();
		int totalNumPointsUsed = 0;
		int numExperiments = dIn.readInt();
		
		int[] iExperimentalIndices = new int[numExperiments];
		int[] iExperimentalPointsUsed = new int[numExperiments];
		for (int i = 0; i < numExperiments; i++) {
			iExperimentalIndices[i] = dIn.readInt();
			iExperimentalPointsUsed[i] = dIn.readInt();
			totalNumPointsUsed += iExperimentalPointsUsed[i];
		}
		
		ManufacturingSchematic schematic = player.getCurrentManufacturingSchematic();
		TangibleItem itemBeingCrafted = schematic.getItemBeingCrafted();
		int successRating = itemBeingCrafted.experiment(iExperimentalIndices, iExperimentalPointsUsed, player);
		client.insertPacket(PacketFactory.buildAttributeListMessage(client, itemBeingCrafted));
		// TODO:  Calculate the effect of the experimentation on the item.
		client.insertPacket(player.setNumExperimentationPoints(player.getNumExperimentationPoints() - totalNumPointsUsed));
		client.insertPacket(PacketFactory.buildDequeueExperimentationMessage(playerID, successRating, expCounter));
		
	}
	
	private void handleCreateManufactureSchematic(long targetID, String[] sParams) throws IOException {
		// Target ID = 0, sParams[0] = "1".  We must keep track of everything ourselves.
		ManufacturingSchematic schematic = player.getCurrentManufacturingSchematic();
		TangibleItem itemBeingCrafted = schematic.getItemBeingCrafted();
		TangibleItem datapad = player.getDatapad();
		schematic.setSTFFileName(itemBeingCrafted.getSTFFileName());
		schematic.setSTFFileIdentifier(itemBeingCrafted.getSTFFileIdentifier());
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_MSCO, (byte)3, (short)1, (short)1, schematic, schematic.getSTFFileName(), schematic.getSTFFileIdentifier()));
		//client.insertPacket(schematic.setCustomName("Schematic: " + itemBeingCrafted.getName(), true));
		datapad.addIntangibleObject(schematic);
		client.insertPacket(schematic.setContainer(datapad, -1, true));
		byte updateCount = Byte.parseByte(sParams[0]);
		client.insertPacket(PacketFactory.buildDequeueGenericObjectControllerMessage010C(player.getID(), 0x010B, 1, updateCount));
		Attribute crafterAttribute = schematic.getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_CRAFTER);
		if (crafterAttribute == null) {
			crafterAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_CRAFTER, player.getFirstName());
			schematic.addAttribute(crafterAttribute);
		} else {
			crafterAttribute.setAttributeValue(player.getFirstName());
		}
		
		Enumeration<Attribute> vAttrEnum = itemBeingCrafted.getAttributeList(null).elements();
		while (vAttrEnum.hasMoreElements()) {
			Attribute a = vAttrEnum.nextElement();
			schematic.addAttribute(a);
		}
		// Manufacturing count.
		schematic.setSerialNumber(itemBeingCrafted.getSerialNumber());
		schematic.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_MANUFACTURE_SCHEMATIC_VOLUME, schematic.getNumberOfFactoryItems()));
		client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), schematic));
	}
	
	private void handleRequestBazaarTerminal(SOEInputStream dIn) throws IOException {
		System.out.println("Request bazaar terminal open for object ID " + Long.toHexString(dIn.readLong()));
	}
	
	protected void handleMaskScent(long targetID, String[] sParams) throws IOException {
		// Target ID is 0, sParams is null.  Don't read anything.
		if (player.hasSkill(34)) {
			if (player.getMaskScentDelayTime() <= 0) {
				BuffEffect maskScentEffect = new BuffEffect();
				maskScentEffect.setAffectedPlayer(player);
				maskScentEffect.setBuffHamIndex(-1);
				maskScentEffect.setBuffIndex(Constants.BUFF_EFFECT_SKILL_MASK_SCENT);
				maskScentEffect.setBuffPotency(0);
				maskScentEffect.setStateApplied(Constants.STATE_SCENT_MASKED);
				SkillMods maskScentMod = player.getSkillMod("mask_scent");
				long lTimeToMaskScent = maskScentMod.getSkillModModdedValue() * 2000l;
				maskScentEffect.setTimeRemainingMS(lTimeToMaskScent);
				player.addBuffEffect(maskScentEffect);
				player.setMaskScentDelayTime(100000l);  // Delay time of 100 seconds.
				client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use", "sys_scentmask_start"));
			} else {
				client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use", "sys_scentmask_delay"));
			}
		} else {
			client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use", "sys_scentmask_noskill"));
		}
	}
	
	/* ************************ COMBAT ******************************************/
	/* ************************ RELATED *****************************************/
	/* ************************ CODE ********************************************/
	
	private boolean handleCombatAction(int iAttackCRC, long targetID, String[] sParams, CommandQueueItem cq) throws IOException {
		CombatAction combatAction = DatabaseInterface.getCombatActionByCRC(iAttackCRC);
		if (combatAction != null) {
			int requiredSkillID = combatAction.getRequiredSkill();
			if (requiredSkillID == -1 || player.hasSkill(requiredSkillID) || bIsDeveloper) {
				System.out.println("Combat action " + Integer.toHexString(combatAction.getCRC()));
				cq.setCombatAction(combatAction);
				cq.setTargetID(targetID);
				cq.setParams(sParams);
				player.queueCommand(cq);
				// No, don't handle the attack right now.  Queue it with the CommandQueueItem -- when the player dequeues this attack, they will actually attack.
				//handleAttack(cq, combatAction, targetID, sParams);
				return true;
			} else {
				cq.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INSUFFICIENT_SKILL);
				return true;
			}
		}
		return false;
	}
	
	// This function handles cases of Player vs. Player attacks.
	protected void handleAttack(CommandQueueItem action) throws IOException {
		
		CombatAction specialAttack = action.getCombatAction();
		long targetID = action.getTargetID();
		String[] sParams = action.getParams();
		SOEObject targetObject = server.getObjectFromAllObjects(targetID);
		if (targetObject == null) {
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INVALID_TARGET);
			return;
		}
		if (!(targetObject instanceof Player)) {
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INVALID_TARGET);
			return;
		}
		if (targetObject instanceof Terminal || targetObject instanceof Shuttle) {
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INVALID_TARGET);
			return;
		}
		if (targetObject.getID() == player.getID()) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("error_message", "target_not_attackable"));
			return;
		}
		
		// Logically, this cannot throw a ClassCastException since we have already returned if it is not a Player (or subclass thereof).
		Player tarPlayer = (Player)targetObject;
		byte targetStance = tarPlayer.getStance();
		// TODO:  The player can only attack if he is Standing, Kneeling or Prone.  
		byte playerStance = player.getStance();
		if (playerStance != Constants.STANCE_STANDING && playerStance != Constants.STANCE_KNEELING && playerStance != Constants.STANCE_PRONE) {
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_IN_STANCE);
			action.setStateInvoked(player.getCommandQueueErrorStance());
			return;
		}
		int iCombatCRC = specialAttack.getCRC();
		if (targetStance == Constants.STANCE_INCAPACITATED) {
			// Only allowable attack on target is DeathBlow or Sniper Shot
			// If those are the attacks, kill the target and 
			if (iCombatCRC == 0xE5F3B39B || iCombatCRC == 0x13CC58E1) {
				if (!(tarPlayer instanceof NPC)) {
					if (player.hasSkill(specialAttack.getRequiredSkill())) {
						// These 2 lines of code SHOULD trigger the cloning window on the target player in addition to causing them to "die".
						tarPlayer.setStance(null, Constants.STANCE_DEAD, true);
						tarPlayer.setHasSeenCloningWindow(false);
						
						tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage("base_player", "prose_victim_dead", 0, null, null, null, player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFirstName(), 0, null, null, null, 0, 0.0f, false));
						client.insertPacket(PacketFactory.buildChatSystemMessage("base_player", "killer_target_dead"));
					} // the else is handled later on.
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage("error_message", "target_not_player"));
				}
			} else {
				client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot attack an incapacitated target."));
			}
			return;
		} else if (targetStance == Constants.STANCE_DEAD) {
			// Cannot do anything to target.
			client.insertPacket(PacketFactory.buildChatSystemMessage("error_message", "prose_target_already_dead"));
			return;
		} else {
			if (iCombatCRC == 0xE5F3B39B || iCombatCRC == 0x13CC58E1) {
				client.insertPacket(PacketFactory.buildChatSystemMessage("error_message", "target_not_incapacitated"));
				return;
			}
		}
		Weapon weapon = player.getWeapon();
		float fAttackRange = weapon.getAttackRange();
		fAttackRange = Math.max(fAttackRange, 5.0f);
		//System.out.println("Attack coordinates.  PlayerX: " + player.getX() + ", Y: " + player.getY() + ", Z: " + player.getZ() + "\nTargetX: " + tarPlayer.getX() + ", Y: " + tarPlayer.getY() + ", Z: " + tarPlayer.getZ() +"\nAttack range: " + fAttackRange);
		if (!ZoneServer.isInRange(player, tarPlayer, fAttackRange)) {
			float fRangeToTarget = ZoneServer.getRangeBetweenObjects(player, tarPlayer);
			// If it's because of the Z axis, loosen the range restriction by 5 metres and try again.
			if (fRangeToTarget > (ZoneServer.getLineLength(player.getZ(), tarPlayer.getZ()) + 5.0f)) {
				//System.out.println("Target out of range.  Range between player and target: " + ZoneServer.getRangeBetweenObjects(player, tarPlayer));
				action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_OUT_OF_RANGE);
				return;
			} // else, it's the fraked up Z axis preventing us from attacking.
		} 
		// Actually apply damage based on the current weapon.
		// Is the player actually certified to use this weapon?
		if (player.hasState(Constants.STATE_SCENT_MASKED)) {
			player.removeState(Constants.STATE_SCENT_MASKED);
			client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use", "sys_scentmask_break_combat"));
		}
		int iWeaponSkill = weapon.getSkillRequirement();
		boolean bCertified = true;
		if (iWeaponSkill != -1) {
			bCertified = player.hasSkill(iWeaponSkill);
		}

		// Did the player actually hit?
		// Roll between 0 and 150.
		// A roll of 0 is ALWAYS hit, AND damage * 2.
		// A roll of 150 is ALWAYS miss.
		int iAttackRoll = SWGGui.getRandomInt(150);
		int iHitState = Constants.COMBAT_EFFECT_HIT;
		int iWeaponType = weapon.getWeaponType();
		int iWeaponTypeRequired = specialAttack.getRequiredWeaponType();
		SkillMods playerAccuracyMod = null;
		SkillMods playerSpeedMod = null; // Every 1 point of speed, BEYOND THE MINIMUM SPEED NEEDED TO WIELD THE WEAPON, will reduce the attack delay by 25 milliseconds. 
		SkillMods targetDefenseMod = null;
		SkillMods targetDodgeMod = tarPlayer.getSkillMod("dodge");
		SkillMods targetBlockMod = tarPlayer.getSkillMod("block");
		SkillMods targetCounterAttackMod = tarPlayer.getSkillMod("counterattack");
		boolean bMelee= false;
		switch (iWeaponType) {
			case Constants.WEAPON_TYPE_UNARMED: {
				playerAccuracyMod = player.getSkillMod("unarmed_accuracy");
				playerSpeedMod = player.getSkillMod("unarmed_speed");
				bMelee = true;
				break;
				
			}
			case Constants.WEAPON_TYPE_CARBINE: {
				playerAccuracyMod = player.getSkillMod("carbine_accuracy");
				playerSpeedMod = player.getSkillMod("carbine_speed");
				break;
			}
			case Constants.WEAPON_TYPE_HEAVY_COMMANDO: {
				// Special case -- 4 different types of heavy weapon.  For now, just use rifles.
				playerAccuracyMod = player.getSkillMod("rifle_accuracy");
				playerSpeedMod = player.getSkillMod("rifle_speed");
				break;
				
			}
			case Constants.WEAPON_TYPE_ONE_HANDED_SWORD: {
				playerAccuracyMod = player.getSkillMod("onehandmelee_accuracy");
				playerSpeedMod = player.getSkillMod("onehandmelee_speed");
				bMelee = true;
				break;
			}
			case Constants.WEAPON_TYPE_PISTOL: {
				playerAccuracyMod = player.getSkillMod("unarmed_accuracy");
				playerSpeedMod = player.getSkillMod("unarmed_speed");
				break;
				
			}
			case Constants.WEAPON_TYPE_POLEARM: {
				playerAccuracyMod = player.getSkillMod("polearm_accuracy");
				playerSpeedMod = player.getSkillMod("polearm_speed");
				bMelee = true;
				break;
			}
			case Constants.WEAPON_TYPE_RIFLE: {
				// Special case -- 4 different types of heavy weapon.  For now, just use rifles.
				playerAccuracyMod = player.getSkillMod("rifle_accuracy");
				playerSpeedMod = player.getSkillMod("rifle_speed");
				break;
				
			}
			case Constants.WEAPON_TYPE_THROWN: {
				playerAccuracyMod = player.getSkillMod("thrown_accuracy");
				playerSpeedMod = player.getSkillMod("thrown_speed");
				break;
			}
			case Constants.WEAPON_TYPE_TWO_HANDED_SWORD: {
				playerAccuracyMod = player.getSkillMod("twohandmelee_accuracy");
				playerSpeedMod = player.getSkillMod("twohandmelee_speed");
				bMelee = true;
				break;
			} 
			case Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED: {
				// Special case -- 3 different types of Jedi weapon.
				// For now, use Polearm.
				playerAccuracyMod = player.getSkillMod("onehandlightsaber_accuracy");
				playerSpeedMod = player.getSkillMod("onehandlightsaber_speed");
				bMelee = true;
				break;
			}
			case Constants.WEAPON_TYPE_JEDI_LIGHTSABER_TWO_HANDED: {
				// Special case -- 3 different types of Jedi weapon.
				// For now, use Polearm.
				playerAccuracyMod = player.getSkillMod("twohandlightsaber_accuracy");
				playerSpeedMod = player.getSkillMod("twohandlightsaber_speed");
				bMelee = true;
				break;
			}
			case Constants.WEAPON_TYPE_JEDI_LIGHTSABER_POLEARM: {
				// Special case -- 3 different types of Jedi weapon.
				// For now, use Polearm.
				playerAccuracyMod = player.getSkillMod("polearmlightsaber_accuracy");
				playerSpeedMod = player.getSkillMod("polearmlightsaber_speed");
				bMelee = true;
				break;
			}
			default: {
				System.out.println("unknown weapon type " + iWeaponType);
			}
		}
		
		if (bMelee) {
			if (weapon.getAttackRange() == 0) {
				weapon.setAttackRange(5.0f);
			}
			targetDefenseMod = tarPlayer.getSkillMod("melee_defense");
		} else {
			if (weapon.getAttackRange() == 0) {
				weapon.setAttackRange(64.0f);
			}
			targetDefenseMod = tarPlayer.getSkillMod("ranged_defense");
		}
		// iAttackRoll > 100 + (playerAccuracyMod) - (targetDefenseMod) is not a hit.
		int iDamageToApply = SWGGui.getRandomInt(weapon.getMinDamage(), weapon.getMaxDamage());
		float iDamageModifier = specialAttack.getDamageModifier();
		float fTotalDamage = (iDamageToApply * iDamageModifier);
		if ((fTotalDamage - (int)fTotalDamage) >= 0.5f) {
			fTotalDamage += 1.0f;
		}
		if (tarPlayer.getStance() == Constants.STANCE_KNOCKED_DOWN) {
			fTotalDamage = fTotalDamage * 1.4f;
		}
		iDamageToApply = (int)fTotalDamage;
		if (!bCertified) {
			iDamageToApply = iDamageToApply % 10;
		}
			
		float fTimeDelay = weapon.getRefireDelay();
		float fTimeMultiplier = specialAttack.getAttackDelayModifier();
		fTimeDelay = fTimeDelay * fTimeMultiplier;
		long lTimeDelayMS = (long)(fTimeDelay * 1000.0f);
		if (playerSpeedMod != null) {
			lTimeDelayMS -=  50 * playerSpeedMod.getSkillModModdedValue();
		}
		action.setCommandTimerMS(lTimeDelayMS);
		int iHamToDamage = specialAttack.getTargetHam();
		int[] iTargetHams = tarPlayer.getCurrentHam();
		if (iTargetHams == null) {
			action.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INVALID_TARGET);
			return;
		}
		
		if (iHamToDamage == -1 || iHamToDamage == 9) {
			iHamToDamage = SWGGui.getRandomInt(3);
			switch (iHamToDamage) {
				case 0: {
					break;
				}
				case 1: {
					iHamToDamage = 3;
					break;
				}
				case 2: {
					iHamToDamage = 6;
					break;
				}
				default: {
					iHamToDamage = 0;
					break;
				}
			}
		}

		// Here's where we actually start applying stuff.
		// Before we do that, did we actually hit?  Are we certified on the weapon?  (If not, damage should not exceed 10 points.)
		// Did the target evade?  (No damage).  Block?  (1/4 damage).  Counterattack?  (Reflect 1/4 damage).
		int iDamageToReflect = 0;
		if (iAttackRoll == 0) {
			iHitState = Constants.COMBAT_EFFECT_HIT;
			iDamageToApply = iDamageToApply * 2;
		} else {
			int maxRoll = 100;
			if (playerAccuracyMod != null) {
				maxRoll += playerAccuracyMod.getSkillModModdedValue();
			}
			if (targetDefenseMod != null) {
				maxRoll -= targetDefenseMod.getSkillModModdedValue();
			}
			if (iAttackRoll > maxRoll) {
				iHitState = Constants.COMBAT_EFFECT_MISS;
				iDamageToApply = 0;
			} 
		}
		int iBlockRoll = SWGGui.getRandomInt(150);
		int iDodgeRoll = SWGGui.getRandomInt(150);
		int iCounterAttackRoll = SWGGui.getRandomInt(150);
		boolean bBlock = false;
		boolean bDodge = false;
		if (targetBlockMod != null) {
			if (iBlockRoll < targetBlockMod.getSkillModModdedValue()) {
				bBlock = true;
				iDamageToApply = iDamageToApply / 4;
				iHitState = Constants.COMBAT_EFFECT_BLOCK;
				Weapon tarPlayerWeapon = tarPlayer.getWeapon();
				if (tarPlayerWeapon != null) {
					int tarWeaponType= tarPlayerWeapon.getWeaponType();
					if (tarWeaponType >= Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED) {
						iDamageToApply = 0;
					}
				}
			}
		} else {
			if (iBlockRoll < 10) {
				bBlock = true;
				iDamageToApply = iDamageToApply / 4;
				iHitState = Constants.COMBAT_EFFECT_BLOCK;
				Weapon tarPlayerWeapon = tarPlayer.getWeapon();
				if (tarPlayerWeapon != null) {
					int tarWeaponType= tarPlayerWeapon.getWeaponType();
					if (tarWeaponType >= Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED) {
						iDamageToApply = 0;
					}
				}
			}
		}
		if (!bBlock) {
			if (targetDodgeMod != null) {
				if (iDodgeRoll < targetDodgeMod.getSkillModModdedValue()) {
					bDodge = true;
					iDamageToApply = 0;
					iHitState = Constants.COMBAT_EFFECT_EVADE;
				}
			} else {
				if (iDodgeRoll < 10) {
					bDodge = true;
					iDamageToApply = 0;
					iHitState = Constants.COMBAT_EFFECT_EVADE;
				}
			}
		}
		if ((!bDodge) && (!bBlock)) {
			if (targetCounterAttackMod != null) {
				
				if (iCounterAttackRoll < targetCounterAttackMod.getSkillModModdedValue()) {
					iDamageToReflect = iDamageToApply /4;
					iHitState = Constants.COMBAT_EFFECT_COUNTER;
				}
			} else {
				if (iCounterAttackRoll < 10) {
					iDamageToReflect = iDamageToApply /4;
					iHitState = Constants.COMBAT_EFFECT_COUNTER;
				}
			}
		}
		
		iHamToDamage = Math.min(iHamToDamage, iTargetHams.length - 2);
		iHamToDamage = Math.max(iHamToDamage, 0);
		String sHitEffect = null;
		int r = 0;
		int g = 0;
		int b = 0;
		switch(iHamToDamage) {
			case Constants.HAM_INDEX_HEALTH: {
				sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_HIT_BODY];
				r = 255;
				break;
			}
			case Constants.HAM_INDEX_ACTION: {
				int iLocation = SWGGui.getRandomInt(19, 22);
				sHitEffect = Constants.COMBAT_EFFECTS_FLYTEXT[iLocation];
				g = 255;
				break;
			}
			case Constants.HAM_INDEX_MIND: {
				sHitEffect = Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_HIT_HEAD];
				b = 255;
				break;
			}
		}
		if (iHitState == Constants.COMBAT_EFFECT_COUNTER) {
			r = 255;
			g = 255;
			b = 0;
			sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_COUNTERATTACK];
		} else if (iHitState == Constants.COMBAT_EFFECT_BLOCK) {
			r = 255;
			g = 0;
			b = 255;
			sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_BLOCK];
		} else if (iHitState == Constants.COMBAT_EFFECT_EVADE) {
			r = 0;
			g = 255;
			b = 255;
			sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_DODGE];
		} else if (iHitState == Constants.COMBAT_EFFECT_MISS) {
			r = 255;
			g = 255;
			b = 255;
			sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_MISS];
		}
		int iTotalDamage = iDamageToApply;

		// If the damage to apply was 0 or less, we must have missed or been blocked somehow.
		Vector<Integer> vCRCs = specialAttack.getAnimationCRC();
		int iCRCToSend = 0;
		if (vCRCs != null) {
			if (!vCRCs.isEmpty()) {
				iCRCToSend = vCRCs.elementAt(0);
			}
		} else {
			if (iWeaponType <= Constants.WEAPON_TYPE_POLEARM) {
				iCRCToSend = Constants.MELEE_BASIC_ATTACKS[SWGGui.getRandomInt(Constants.MELEE_BASIC_ATTACKS.length)];
			} else if (iWeaponType == Constants.WEAPON_TYPE_UNARMED){
				iCRCToSend = Constants.UNARMED_BASIC_ATTACKS[SWGGui.getRandomInt(Constants.UNARMED_BASIC_ATTACKS.length)];
			} else if (iWeaponType < Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED) {
				iCRCToSend = Constants.RANGED_BASIC_ATTACKS[SWGGui.getRandomInt(Constants.RANGED_BASIC_ATTACKS.length)];
			}
		}
		// Now, damage the player initiating the attack -- NO.  No damage is taken by the player for executing a standard attack, ONLY a special attack.
		// Here is the commented out code for that.
		
		int iHealthCost = weapon.getHealthCost();
		int iActionCost = weapon.getActionCost();
		int iMindCost = weapon.getMindCost();
		float fHealthCostModifier = specialAttack.getHealthCostModifier();
		float fActionCostModifier = specialAttack.getActionCostModifier();
		float fMindCostModifier = specialAttack.getMindCostModifier();
		// Don't deduct ham from "me" if I'm just default attacking.
		if (specialAttack.getCRC() == 0xa8fef90a) {
			fHealthCostModifier = 0;
			fActionCostModifier = 0;
			fMindCostModifier = 0;
		}
		int iTotalHealthCost = (int)(iHealthCost * fHealthCostModifier);
		int iTotalActionCost = (int)(iActionCost * fActionCostModifier);
		int iTotalMindCost = (int)(iMindCost * fMindCostModifier);
		if (!player.canPerformAction(iTotalHealthCost, iTotalActionCost, iTotalMindCost)) {
			client.insertPacket(PacketFactory.buildChatSystemMessage("You are too tired to use that special attack."));
			return;
		}
		if (iDamageToReflect > 0) {
			switch (iHamToDamage) {
				case 0: {
					iTotalHealthCost += iDamageToReflect;
					break;
				}
				case 3: {
					iTotalActionCost += iDamageToReflect;
					break;
				}
				case 6: {
					iTotalMindCost += iDamageToReflect;
					break;
				}
				default :{
					iTotalHealthCost += iDamageToReflect;
				}
			}
		}
		//client.insertPacket(tarPlayer.addDefender(player), Constants.PACKET_RANGE_CHAT_RANGE);
		//client.insertPacket(player.addDefender(tarPlayer), Constants.PACKET_RANGE_CHAT_RANGE);
		player.setTimeToNextCommandQueueAction((lTimeDelayMS));		// Set the next attack delay on the player.  When this expires, if the Player is still in combat, fire off another default attack.
		int iSecondaryDamage = Math.max(iDamageToApply / 10, 1);
		iTotalDamage += iSecondaryDamage * 2;
		
		// Insert all the packets here.

		if (!bCertified) {
			if (!player.hasState(Constants.STATE_COMBAT)) {
				client.insertPacket(PacketFactory.buildChatSystemMessage("combat_effects", "no_proficiency"));
			}
		}
		if (iWeaponTypeRequired != -1) {
			if (iWeaponType != iWeaponTypeRequired) {
				// Don't attack!
				client.insertPacket(PacketFactory.buildChatSystemMessage("cbt_spam", "no_attack_wrong_weapon"));
				return;
			}
		}
		player.addState(Constants.STATE_COMBAT, Constants.COMBAT_STATE_DEFAULT_PERIOD_MS);
		
		if (iTotalHealthCost > 0) {
			player.updateCurrentHam(0, -iTotalHealthCost);
		}
		if (iTotalActionCost > 0) {
			player.updateCurrentHam(3, -iTotalActionCost);
		}
		if (iTotalMindCost > 0) {
			player.updateCurrentHam(6, -iTotalMindCost);
		}

		Vector<Player> vPlayersInRange = server.getPlayersAroundObject(player, true);
		for (int i = 0; i < vPlayersInRange.size(); i++) {
			Player thePlayer = vPlayersInRange.elementAt(i);
			thePlayer.getClient().insertPacket(PacketFactory.buildCombatTextSpam(player, thePlayer, targetObject, "cbt_spam", specialAttack.getCombatSTFSpamArr()[iHitState], iTotalDamage));
			client.insertPacket(PacketFactory.buildAttackFlyText(tarPlayer, thePlayer, "combat_effects", sHitEffect, r, g, b), Constants.PACKET_RANGE_CHAT_RANGE);
		}
		//client.insertPacket(PacketFactory.buildAttackFlyText(tarPlayer, tarPlayer, "combat_effects", sHitEffect, r, g, b), Constants.PACKET_RANGE_CHAT_RANGE);

		if(tarPlayer instanceof Lair) //I made a rhyme!
		{
			handleAttackLair((Lair)tarPlayer, iDamageToApply);
			// Update pvp status of the Lair, to this player only.
		}
		else if (tarPlayer instanceof NPC) {
			handleAttackNPC((NPC)tarPlayer, specialAttack, iTargetHams, iDamageToApply, iSecondaryDamage, iHamToDamage);
		} else {
			tarPlayer.addState(Constants.STATE_COMBAT, Constants.COMBAT_STATE_DEFAULT_PERIOD_MS);
			if (iDamageToApply > 0) {
				tarPlayer.updateCurrentHam(iHamToDamage, -iDamageToApply);
				if (iTargetHams.length > 1) {
					if (iHamToDamage == 0) {
						tarPlayer.updateCurrentHam(3, -iSecondaryDamage);
						tarPlayer.updateCurrentHam(6, -iSecondaryDamage);
					} else if (iHamToDamage == 3) {
						tarPlayer.updateCurrentHam(0, -iSecondaryDamage);
						tarPlayer.updateCurrentHam(6, -iSecondaryDamage);
					} else if (iHamToDamage == 6) {
						tarPlayer.updateCurrentHam(0, -iSecondaryDamage);
						tarPlayer.updateCurrentHam(3, -iSecondaryDamage);
					}
				}
				for (int i = 0; i < iTargetHams.length; i++) {
					if (iTargetHams[i] <= 0) {
						tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage("base_player", "prose_victim_incap", 0, null, null, null, player.getID(), player.getSTFFileName(), player.getSTFFileIdentifier(), player.getFirstName(), 0, null, null, null, 0, 0.0f, false));
						client.insertPacket(PacketFactory.buildChatSystemMessage("base_player", "killer_target_incapacitated"));
					}
				}
				// States to apply.
				BuffEffect[] combatEffects = specialAttack.getAllCombatEffects();
				for (int i = 0; i < combatEffects.length; i++) {
					BuffEffect buffEffect = combatEffects[i];
					if (buffEffect != null) {
						float iChanceToApply = buffEffect.getEffectChance() * 100;
						int iRandomChance = SWGGui.getRandomInt(100);
						long lDuration = buffEffect.getTimeRemainingMS();
						if (iRandomChance < iChanceToApply) {
							if (lDuration == 0) {
								// It's a stance change.  What kind of stance change is it?
								int iStance = buffEffect.getStanceApplied();
								if (iStance == Constants.STANCE_PRONE) {
									// Posture down
								} else if (iStance == Constants.STANCE_STANDING) {
									// Posture up
								} else {
									// Knock down.
								}
							} else {
								// It's a state.
								tarPlayer.addBuffEffect(buffEffect.copy());
							}
						}		
					}
				}
			}
		}
		if (iCRCToSend != 0) {
			client.insertPacket(PacketFactory.buildObjectController_CombatAction(player, tarPlayer, iCRCToSend, true), Constants.PACKET_RANGE_CHAT_RANGE);
		}
	}
	
	private void handleAttackLair(Lair tarObject, int iDamageToApply) throws IOException
	{
		tarObject.setDamage(tarObject.getDamage()+iDamageToApply);
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)8, tarObject, tarObject.getDamage()));
		
		Long[] spawnedKids = tarObject.getChildren().keySet().toArray(new Long[tarObject.getChildren().size()]);
		for(Long i : spawnedKids)
		{
			NPC temp = tarObject.getChildren().get(i);
			if(iDamageToApply == 0)
			{
				if(temp.getCurrentHateRating(player) == 0)
				{
					temp.addToHateList(player, 1);
				}
			}
			else
			{
				temp.addToHateList(player, iDamageToApply);
			}
		}
	}
	
	// This function handles cases of Player vs Creature attacks.
	private void handleAttackNPC(NPC tarPlayer, CombatAction specialAttack, int[] iTargetHams, int iDamageToApply, int iSecondaryDamage, int iHamToDamage) throws IOException {
		tarPlayer.addState(Constants.STATE_COMBAT, Constants.COMBAT_STATE_DEFAULT_PERIOD_MS);
		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)0x10, tarPlayer, tarPlayer.getStateBitmask()), Constants.PACKET_RANGE_CHAT_RANGE);
		// Detect the attack attempt, even if it doesn't land.
		if (iDamageToApply == 0) {
			if (tarPlayer.getCurrentHateRating(player) == 0) {
				tarPlayer.addToHateList(player, 1);
			}
		} else {
			tarPlayer.updateCurrentHam(iHamToDamage, -iDamageToApply);
			tarPlayer.addToHateList(player, iDamageToApply);
			client.insertPacket(PacketFactory.buildHAMDelta(tarPlayer, (byte)6, (short)13, iHamToDamage, iTargetHams[iHamToDamage], true), Constants.PACKET_RANGE_CHAT_RANGE);
			if (iTargetHams.length > 1) {
				if (iHamToDamage == 0) {
					tarPlayer.updateCurrentHam(3, -iSecondaryDamage);
					tarPlayer.updateCurrentHam(6, -iSecondaryDamage);
					client.insertPacket(PacketFactory.buildHAMDelta(tarPlayer, (byte)6, (short)13, 3, iTargetHams[3], true), Constants.PACKET_RANGE_CHAT_RANGE);
					client.insertPacket(PacketFactory.buildHAMDelta(tarPlayer, (byte)6, (short)13, 6, iTargetHams[6], true), Constants.PACKET_RANGE_CHAT_RANGE);
				} else if (iHamToDamage == 3) {
					tarPlayer.updateCurrentHam(0, -iSecondaryDamage);
					tarPlayer.updateCurrentHam(6, -iSecondaryDamage);
					client.insertPacket(PacketFactory.buildHAMDelta(tarPlayer, (byte)6, (short)13, 0, iTargetHams[0], true), Constants.PACKET_RANGE_CHAT_RANGE);
					client.insertPacket(PacketFactory.buildHAMDelta(tarPlayer, (byte)6, (short)13, 6, iTargetHams[6], true), Constants.PACKET_RANGE_CHAT_RANGE);
				} else if (iHamToDamage == 6) {
					tarPlayer.updateCurrentHam(0, -iSecondaryDamage);
					tarPlayer.updateCurrentHam(3, -iSecondaryDamage);
					client.insertPacket(PacketFactory.buildHAMDelta(tarPlayer, (byte)6, (short)13, 3, iTargetHams[3], true), Constants.PACKET_RANGE_CHAT_RANGE);
					client.insertPacket(PacketFactory.buildHAMDelta(tarPlayer, (byte)6, (short)13, 0, iTargetHams[0], true), Constants.PACKET_RANGE_CHAT_RANGE);
				}
			}
			// States to apply.
			BuffEffect[] combatEffects = specialAttack.getAllCombatEffects();
			for (int i = 0; i < combatEffects.length; i++) {
				BuffEffect buffEffect = combatEffects[i];
				if (buffEffect != null) {
					float iChanceToApply = buffEffect.getEffectChance() * 100;
					int iRandomChance = SWGGui.getRandomInt(100);
					long lDuration = buffEffect.getTimeRemainingMS();
					if (iRandomChance < iChanceToApply) {
						if (lDuration == 0) {
							// It's a stance change.  What kind of stance change is it?
							int iStance = buffEffect.getStanceApplied();
							if (iStance == Constants.STANCE_PRONE) {
								// Posture down
							} else if (iStance == Constants.STANCE_STANDING) {
								// Posture up
							} else {
								// Knock down.
							}
			        		client.insertPacket(PacketFactory.buildObjectControllerMessage_UpdatePosture(tarPlayer), Constants.PACKET_RANGE_CHAT_RANGE);
			        		client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)0x0B, tarPlayer, tarPlayer.getStance()), Constants.PACKET_RANGE_CHAT_RANGE);
						} else {
							// It's a state.
							tarPlayer.addBuffEffect(buffEffect.copy());
							client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)0x10, tarPlayer, tarPlayer.getStateBitmask()), Constants.PACKET_RANGE_CHAT_RANGE);
						}
					}		
				}
			}
			
			if (tarPlayer.getStance() == Constants.STANCE_DEAD) {
				// Update experience.
        		System.out.println("update combat experience");
        		Weapon w = player.getWeapon();
        		int iWeaponType = w.getWeaponType();
        		int iExperienceID = -1;
        		switch (iWeaponType) {
	        		case Constants.WEAPON_TYPE_CARBINE: {
	        			iExperienceID = 15;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_HEAVY_COMMANDO: {
	        			iExperienceID = 6;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED: 
	        		case Constants.WEAPON_TYPE_JEDI_LIGHTSABER_POLEARM: 
	        		case Constants.WEAPON_TYPE_JEDI_LIGHTSABER_TWO_HANDED: {
	        			iExperienceID = 10;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_ONE_HANDED_SWORD: {
	        			iExperienceID = 11;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_PISTOL: {
	        			iExperienceID = 16;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_POLEARM: {
	        			iExperienceID = 12;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_RIFLE: {
	        			iExperienceID = 17;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_THROWN: {
	        			iExperienceID = 6;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_TWO_HANDED_SWORD: {
	        			iExperienceID = 13;
	        			break;
	        		}
	        		case Constants.WEAPON_TYPE_UNARMED: {
	        			iExperienceID = 14;
	        			break;
	        		}
	        		default: {
	        			System.out.println("Unknown weapon type " + iWeaponType);
	        			iExperienceID = 6;
	        		}
        		}
        		int targetCon = tarPlayer.getConLevel();
        		int playerCon = player.getConLevel();
        		long timeToKillMS = System.currentTimeMillis() - player.getTimeInCombat(); // This is how long it took to kill the creature.
        		
        		float difficultyModifier = (float)targetCon / (float)playerCon;
        		int iTotalDamageDealt = tarPlayer.getCurrentHateRating(player); // Total damage dealt.
        		float fExperienceToGrant = ((iTotalDamageDealt / ((timeToKillMS - 60000)/ 1000)) * difficultyModifier);  // Experience to grant is DPS times difficulty factor, with 1 free minute.
        		System.out.println("Took " + timeToKillMS + " millseconds to apply " + iTotalDamageDealt + " damage.  Difficulty modifier " + difficultyModifier + ", add " + fExperienceToGrant + " experience.");
        		fExperienceToGrant = Math.max(fExperienceToGrant, 125f);
        		fExperienceToGrant = Math.min(fExperienceToGrant, 4500f);
        		if (iExperienceID == 10) {
        			fExperienceToGrant = Math.min(fExperienceToGrant, 1350);
        		}
        		float fCombatExperienceToGrant = 0;
        		if (iExperienceID != 6 && iExperienceID != 10) { // Don't grant Combat experience if the experience type of the weapon is already Combat experience, or Jedi experience.
        			fCombatExperienceToGrant = fExperienceToGrant / 10;
            		player.updateExperience(null, 6, (int)fCombatExperienceToGrant);
        		}
        		player.updateExperience(null, iExperienceID, (int)fExperienceToGrant);
        		player.clearCombatQueue();
        		player.removeState(Constants.STATE_COMBAT);
			}
		}
	}
	
	private void handleDeclarePeace(CommandQueueItem commmand, long targetID, String[] sParams) {
		// When the user declares peace, we need to dequeue all of the queued commands that have a combat action attached to them.  We also need to check all of the Players and NPCs defending against the player,
		// to ensure that none of them have a pending attack on "me".
		// If nobody does, we can remove the STATE_COMBAT from "me".  Otherwise, he needs to remain in combat.
		Vector<SOEObject> vAllNearObjects = server.getCreaturesAroundPlayer(player);
		for (int i = 0; i < vAllNearObjects.size(); i++) {
			SOEObject o = vAllNearObjects.elementAt(i);
			if (o instanceof NPC) {
				NPC npc = (NPC)o;
				int currentNPCHateOfPlayer = npc.getCurrentHateRating(player);
				int aiType = npc.getArtificialIntelligenceType();
				Weapon creatureWeapon = npc.getWeapon();
				switch (aiType) {
					case Constants.INTELLIGENCE_TYPE_CREATURE_PASSIVE: {
						// Break this creature out of combat with the Player.
						npc.removeFromHateList(player);
						break;
					}
					case Constants.INTELLIGENCE_TYPE_CREATURE_AGGRESSIVE:{ 
						// Break this creature out of combat with the Player if the Player is out of attack range.
						if (creatureWeapon != null) {
							if (ZoneServer.getRangeBetweenObjects(player, npc) > creatureWeapon.getAttackRange()) {
								npc.removeFromHateList(player);
							}
						}
						break;
					}
					case Constants.INTELLIGENCE_TYPE_CREATURE_STALKER: {
						// Peace out, return to stalking mode.
						
					}
				}
			} else if (o instanceof Player) {
				// PvP logic goes here.
			}
		}
		
	}
	
	protected long getPacketTimeoutMS() {
		return lPacketTimeoutMS;
	}
	
}//END OF CLASS

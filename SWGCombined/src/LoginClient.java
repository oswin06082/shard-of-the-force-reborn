import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * The Login Client is the interface between the server and the client, and handles all interactions for logging in
 * to the Login Server, choosing a character to play, and erasing a character from a given Zone Server.
 * @author Darryl
 *
 */

public class LoginClient implements Runnable {
	private int connectionID;
	private boolean bValidSession;
	private SocketAddress myAddress;
	private int port;
	private int CrCSeed;
	private short server_sequence = 0;
	private short client_sequence = 0;
	private String sUserPassword = "";
	private String sUserPasswordLC = "";
	private String sUserName = "";
	private String sUserNameLC = "";
	private String sClientVersion = "";
	private boolean bIsDeveloper = false;
	
	private long iAccountID;
	private Vector<Player> lCharacterList;
	//private static Vector sequences = new Vector();
	private long lLastActive = Constants.DISCONNECT_TIMEOUT_MS;
	private int TickCount = 0; // The number of ticks the server has run.
	private short ClientTick = 0; // The number of ticks the client has run.
	/*private int LastUpdate = 0; // You guys are never set, but are sent.  TODO:  Discover how/when/where to set you.
	private int AverageUpdate = 0;
	private int ShortestUpdate = 0;
	private int LongestUpdate = 0;
	private int LastServerUpdate = 0;*/
	private long ClientPacketsSent = 0; /// The number of packets the client says it has sent to us.
	private long ClientPacketsReceived = 0; // The number of packets the client says it has received from us.
	private long serverPacketsReceivedThisClient = 0; // The number of packets we have actually received from the client.
	private long serverPacketsSentThisClient = 1; // The number of packets we have actually sent to the client.
	private int MAX_PACKET_SIZE = 0;
	private LoginServer myServer;
	//private static LoginDatabaseInterface dbInterface;
	private Vector<byte[]> packetQueue;
	private Thread myThread;
	private boolean hasLoggedIn = false;  // Set to true when we receive a valid username / password.
	//private short[] queuedPacketAttributes;
	private Vector<byte[]> vIncomingPacketQueue;
	private final static byte[] Session_Key = {
		   0x20, 0x00, 0x00, 0x00, 
           0x15, 0x00, 0x00, 0x00,
           0x0E, (byte)0xD6,
		   (byte)0x93, (byte)0xDE, (byte)0xD2, (byte)0xEF,	(byte)0xBF, (byte)0x8E, (byte)0xA1, (byte)0xAC,
		   (byte)0xD2, (byte)0xEE, 0x4C, 0x55,	(byte)0xBE, 0x30, 0x5F, (byte)0xBE,
		   0x23, 0x0D, (byte)0xB4, (byte)0xAB,	0x58, (byte)0xF9, 0x62, 0x69,
		   0x79, 0x67, (byte)0xE8, 0x10,	0x6E, (byte)0xD3, (byte)0x86, (byte)0x9B,
		   0x3A, 0x4A, 0x1A, 0x72,	(byte)0xA1, (byte)0xFA, (byte)0x8F, (byte)0x96,
		   (byte)0xFF, (byte)0x9F, (byte)0xA5, 0x62,	0x5A, 0x29
	};
	//private final static int MAX_QUEUED_PACKETS = 16;
	//private int queuedPacketCount = 0;
	
	public boolean bIsLocal = false;
	
	/**
	 * Constructs a new LoginClient for the given Login Server, with the client communicating on the 
	 * given IP address and port, with the given Maximum UDP packet size.
	 * @param theServer -- The Login Server this client is communicating with.
	 * @param theAddress -- The client's IP address.
	 * @param port -- The client's port.
	 * @param packetSize -- The maximum packet size for all packets in this session.
	 */
	public LoginClient(LoginServer theServer, SocketAddress theAddress, int packetSize) {
		lCharacterList = null;
		myAddress = theAddress;
		myServer = theServer;
		CrCSeed = 0;
		//CrCSeed = PacketUtils.getRandomSeed(); 
		lLastActive = System.currentTimeMillis();
		bValidSession = true;
		MAX_PACKET_SIZE = packetSize;
		packetQueue = new Vector<byte[]>(); // Don't allocate the entire array: Too much memory taken.
		TickCount = 0;
		serverPacketsReceivedThisClient = 0;
		serverPacketsSentThisClient = 1;
		ClientPacketsReceived = 0;
		ClientPacketsSent = 0;
		if (myAddress.toString().startsWith("/192")) {
			bIsLocal = true;
		}
		vIncomingPacketQueue = new Vector<byte[]>();
		myThread = new Thread(this);
		myThread.start();
	}
	
	protected void addPacketToParse(byte[] packet) {
		vIncomingPacketQueue.add(packet);
	}
	/**
	 * Add a packet to the outgoing packet queue.
	 * @param packet -- The packet to queue.
	 */
	private void queueOutgoingPacket(byte[] packet) {
		//System.out.println("Queueing packet.");
		packetQueue.add(packet);
	}
	
	/**
	 * Get the number of packets waiting to be sent to the client.
	 * @return -- The number of waiting packets.
	 */
	protected int getQueuedPacketCount() {
		return packetQueue.size();
	}

	/**
	 * Get a packet to send to the client. 
	 * @return The first packet waiting to be sent, or null if the packet queue is empty.
	 */
	private byte[] getQueuedPacket() {
		if (!packetQueue.isEmpty()) {
			try {
				return packetQueue.remove(0);
			} catch (NoSuchElementException e) {
				System.out.println("getQueuedPacket -- NoSuchElementException: " + e.toString());
				e.printStackTrace();
				return null;
			} catch (ArrayIndexOutOfBoundsException ee) {
				System.out.println("getQueuedPacket() -- How are you happening? " + ee.toString());
				ee.printStackTrace();
			}
		}
		return null;

	}
	
	
	/**
	 * Removes packets from the Login Client's outgoing packet queue, combines them if possible, splits them if 
	 * necessary, compresses them, encrypts them, and adds them to the Login Server's outgoing packet queue to be sent.
	 * @throws Exception If any error occurs.
	 */
	public void dequeuePacket() throws Exception {
		try {
			// First, let's get the packet's buffer.
			byte[] buffer = getQueuedPacket();
	        //DataLogObject L;
			// Null check.
			
			if (buffer != null) {
				lLastActive = Constants.DISCONNECT_TIMEOUT_MS;
				//boolean bIsAckPacket = (buffer[1] == 0x15);
				boolean bIncrementSequence = (buffer[1] == 0x09 || buffer[1] == 0x0D);
				if (buffer[1] == 2)  {
					// This ensures that a SOE SESSION RESPONSE is never, ever modified.
					//PacketUtils.printPacketData(buffer);
					myServer.queue(new DatagramPacket(buffer, buffer.length, myAddress));
					serverPacketsSentThisClient++;
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
						dataOut.setSequence(server_sequence);
						byte[] fragData = originalIn.getBuffer();
						int offset = 488;
						byte[] fragment = Arrays.copyOfRange(fragData, 4, offset);
						dataOut.writeReversedInt(fragData.length - 4);
						dataOut.write(fragment);
						dataOut.flush();
						byte[] toSend = byteOut.toByteArray();
						//PacketUtils.printPacketData(toSend);
						if (bIncrementSequence) {
							//System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement.");
							//packetsWaitingAcknowledgement.put(serverSequence, toSend);
							server_sequence++;
						}
						dataOut.close();
						byteOut.close();
						myServer.queue(PrepareForSendSWG(byteOut, toSend.length, false));
						serverPacketsSentThisClient++;
	                                           // L = null;
	                                           // L = new DataLogObject("ZoneClient().DequeuePacket 6: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
	                                           // this.getServer().logServer.logPacket(L);
						while (offset < (fragData.length - 4)) {
							byteOut = new ByteArrayOutputStream();
							dataOut = new SOEOutputStream(byteOut);
							dataOut.setOpcode(Constants.SOE_DATA_FRAG_A);
							//System.out.println("Setting sequence = " + serverSequence + " for client ID " + getAccountID() + ", increment sequence? " + bIncrementSequence);
							dataOut.setSequence(server_sequence);
							int endLocation = Math.min(fragData.length, offset + 488);
							fragment = Arrays.copyOfRange(fragData, offset, endLocation);
							offset += fragment.length;
							dataOut.write(fragment);
							dataOut.flush();
							toSend = byteOut.toByteArray();
							//PacketUtils.printPacketData(toSend);
							if (bIncrementSequence) {
								//System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement.");
								//packetsWaitingAcknowledgement.put(serverSequence, toSend);
								server_sequence++;
							}
							myServer.queue(PrepareForSendSWG(byteOut, toSend.length, false));
							serverPacketsSentThisClient++;
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
							dOut.setSequence(server_sequence);
							dOut.write(buffer, 4, buffer.length - 4);
							dOut.flush();
							byte[] toSend = dOut.getBuffer();
							//PacketUtils.printPacketData(toSend);
							if (bIncrementSequence) {
								//System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement.");
								//packetsWaitingAcknowledgement.put(serverSequence, toSend);
								server_sequence++;
							}
	
							myServer.queue(PrepareForSendSWG(bOut, bOut.toByteArray().length, false));
							serverPacketsSentThisClient++;
	                                                   // L = null;
	                                                  //  L = new DataLogObject("ZoneClient().DequeuePacket 2: " + serverSequence,"SourceIP",buffer,this.getIpnPort(),Constants.LOG_PACKET_DIRECTION_OUT);
	                                                   // this.getServer().logServer.logPacket(L);
							if (nextPacketToAdd != null) {
								// We ran out of space in the current buffer.
								bOut = new ByteArrayOutputStream();
								dOut = new SOEOutputStream(bOut);
								dOut.setOpcode(originalOpcode);
								//System.out.println("Setting sequence = " + serverSequence + " for client ID " + getAccountID() + ", increment sequence? " + bIncrementSequence);
								dOut.setSequence(server_sequence);
								dOut.write(nextPacketToAdd, 4, nextPacketToAdd.length - 4);
								dOut.flush();
								toSend = bOut.toByteArray();
								//PacketUtils.printPacketData(toSend);
								if (bIncrementSequence) {
									//System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement.");
									//packetsWaitingAcknowledgement.put(serverSequence, toSend);
									server_sequence++;
								}
	
								myServer.queue(PrepareForSendSWG(bOut, toSend.length, false));
								serverPacketsSentThisClient++;
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
							dOut.setSequence(server_sequence); // We'll use the sequence of the first packet we want to multipacket.
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
								//System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement.");
								//packetsWaitingAcknowledgement.put(serverSequence, toSend);
								server_sequence++;
							}
							myServer.queue(PrepareForSendSWG(bOut, toSend.length, false));
							serverPacketsSentThisClient++;
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
						
						soeOut.setSequence(server_sequence);
						if (buffer.length > 4) {
							soeOut.write(buffer, 4, buffer.length - 4);
						}
						soeOut.flush();
						//PacketUtils.printPacketData(toSend);
						if (bIncrementSequence) {
							//System.out.println("Adding packet with sequence " + serverSequence + " to packets waiting acknowledgement.");
							//packetsWaitingAcknowledgement.put(serverSequence, toSend);
							server_sequence++;
						}
	
						myServer.queue(PrepareForSendSWG(out, buffer.length, false));
						serverPacketsSentThisClient++;
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

	/**
	 * Returns the time, in milliseconds, that this client last received a packet from the Player.
	 * @return The time.
	 */
	public long getLastActive() {
		return lLastActive;
	}
	
	private long lCurrentTimeMS;
	private long lLastTimeMS;
	private long lDeltaTimeMS;
	public void run() {
		lLastTimeMS = System.currentTimeMillis();
		while (myThread != null) {
			try {
				synchronized(this) {
					Thread.yield();
					wait(100);
				}
				
				lCurrentTimeMS = System.currentTimeMillis();
				lDeltaTimeMS = lCurrentTimeMS - lLastTimeMS;
				
				update(lDeltaTimeMS);
				
				
			}catch (Exception e) {
					// D'oh!
			}
			
		}
	}
	/**
	 * Main update function for the Client.
	 * @param lCurrentTimeMS -- The time that has passed since Update was last called.
	 */
	public void update(long lDeltaTimeMS) throws Exception{
		
		
		
		TickCount++;
		//bValidSession = true;
		lLastActive -= lDeltaTimeMS;
		if (lLastActive < 0) {
			bValidSession = false;
		}
		
		while (!vIncomingPacketQueue.isEmpty()) {
			HandleIncomingPacket(vIncomingPacketQueue.remove(0));
		}
		dequeuePacket();
		
		if (bWaitingForAccountCreation) {
			
			iAccountID =  myServer.findClient(sUserNameLC, sUserPasswordLC); 
			if (iAccountID == -1) {
				//System.out.println("Should remove client with username " + sUserName + " as it has timed out.");
				//bValidSession = false; // Mark client for deletion.
			} else if (iAccountID == -2) {
				// Still waiting...
			} else {
				
				lCharacterList = myServer.getCharacterListForAccount(iAccountID);
				if (!bSentClientToken) {
					sendLoginClientToken();
					bSentClientToken = true;
				} else if (!bSentEnumCluster){
					sendLoginEnumCluster(); 
					bSentEnumCluster = true;
				} else if (!bSentClusterStatus) {
					sendLoginClusterStatus();
					bSentClusterStatus = true;
				} else {
					sendEnumerateCharacterID();
				}
			}
		} else {
			bSentClientToken = false;
			bSentEnumCluster = false;
			bSentClusterStatus = false;
		}
	}
	
	private boolean bSentClientToken = false;
	private boolean bSentEnumCluster = false;
	private boolean bSentClusterStatus = false;
	
	private void HandleIncomingPacket(byte[] packet) throws IOException {
		SOEInputStream sIn = new SOEInputStream(new ByteArrayInputStream(packet));
		HandleIncomingPacket(sIn, sIn.available(), false);
	}
	/**
	 * This function handles the processing of packets sent us by the Player, and generates responses (if necessary).
	 * @param incPacket -- The packet data.
	 * @param len -- The length of the packet.
	 * @param bWasMultied -- Indicates if this packet was part of another packet.
	 */
	public void HandleIncomingPacket(SOEInputStream incPacket, int len, boolean bWasMultied) {
		//System.out.println("LoginClient handle incoming packet.");
		serverPacketsReceivedThisClient++;
		byte[] buffer = incPacket.getBuffer();
		try {
			short packetType = incPacket.getOpcode();
			if (packetType == Constants.SOE_PING) {
				SendPong();
				return;
			}
			boolean bFiddleWith = ((packetType != Constants.SOE_SESSION_REQUEST)
					&& (packetType != Constants.SOE_DISCONNECT)
					&& (!bWasMultied));
			//System.out.println("Second byte of the buffer = " + buffer[1]);
			if (!bWasMultied) {
				//System.out.println("This packet was not part of a multipacket.");
				
				if (bFiddleWith) {
					
					buffer = PacketUtils.Decrypt(buffer, buffer.length, CrCSeed);
					if (buffer.length < 3) {
						// Assume garbage.
						return;
					}
					if (buffer[2] == 'x') {
						buffer = PacketUtils.decompress(buffer);
					}
					incPacket = new SOEInputStream(new ByteArrayInputStream(buffer));
					//incPacket.readShort(); // Bypass the first 2 bytes that we now have to re-read.
				}
			}
				
			if (packetType == Constants.SOE_DISCONNECT) {
				bValidSession = false;
				hasLoggedIn = false;
				//System.out.println("SOE_Disconnect");
				return;
			} else if (packetType == Constants.SOE_MULTI_PKT) {
				DataInputStream dIn = new DataInputStream(new ByteArrayInputStream(buffer));
				dIn.readShort();
				while (dIn.available() > 3) {
					byte subPacketLen = dIn.readByte();
					byte[] subPacket = new byte[subPacketLen + 1];
					dIn.read(subPacket, 0, subPacketLen);
					HandleIncomingPacket(new SOEInputStream(new ByteArrayInputStream(subPacket)), subPacketLen, true);
				}
				return;
			}
			
			short sequence = incPacket.getSequence();
			short updateType = -1;
			updateType = incPacket.getUpdateType();
			if (packetType == Constants.SOE_SESSION_REQUEST) {
				incPacket.readInt();
				connectionID = incPacket.readInt();
				//myServer.incomingPackets.add(buffer);
				sequence = 0; // Always reset the sequence when receiving a SOE_SESSION_REQUEST.
				send_sesresp();
				send_loginserver();
			} else {
				if (packetType == Constants.SOE_CHL_DATA_A) {
					client_sequence = sequence;
					SendAck(packetType, client_sequence);
					HandleChlData(incPacket, updateType, packetType, sequence, bWasMultied);
				}else if (packetType == Constants.SOE_NET_STATUS_REQ) {
					incPacket.reset(); // Necessary for this packet -- it has no sequence or update type.
					incPacket.readShort(); // We already know what packet type this is.
					try {
						ClientTick = incPacket.readReversedShort();
						//LastUpdate= incPacket.readReversedInt();
						//AverageUpdate = incPacket.readReversedInt();
						//ShortestUpdate	= incPacket.readReversedInt();
						//LongestUpdate = incPacket.readReversedInt();
						//LastServerUpdate = incPacket.readReversedInt();
						incPacket.readInt();
						incPacket.readInt();
						incPacket.readInt();
						incPacket.readInt();
						incPacket.readInt();
						ClientPacketsSent = incPacket.readReversedLong();
						ClientPacketsReceived =  incPacket.readReversedLong();
					} catch (Exception e) {
						System.out.println("Exploded reading data: " + e.toString());
						e.printStackTrace();
						//Who cares
					} finally {
						send_netstatusresponse();	
					}
				}
			}
		} catch (IOException ee) {
			System.out.println("Error reading data: " + ee.toString());
			ee.printStackTrace();
		} catch (Exception e) {
			System.out.println("Unknown exception: " + e.toString());
			PacketUtils.printPacketData(incPacket.getBuffer());
			e.printStackTrace();
		}
	}
	
	/**
	 * Generate a Network Status Response message for the Player.  Sent in response to a Net Status Request.
	 */
	private void send_netstatusresponse() {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(buf);
		TickCount +=ClientTick;
		try {
			dOut.writeShort(Constants.SOE_NET_STATUS_RES);
			dOut.writeReversedShort(ClientTick); 				//Client TickCount
			dOut.writeReversedInt(TickCount);  				//Server TickCount
			dOut.writeReversedLong(ClientPacketsSent);			//Packet count reported sent by the client
			dOut.writeReversedLong(ClientPacketsReceived);		//Packet count reported received by the client	
			//dOut.writeReversedLong(serverPacketsSentThisClient);		//Packet count we have actually sent.
			dOut.writeReversedLong(ClientPacketsReceived);
			dOut.writeReversedLong(serverPacketsReceivedThisClient);	// Packet count we have actually received.
			byte[] buffer = buf.toByteArray();
			byte[] compressed = PacketUtils.compress(buffer, true);
			byte[] encoded = PacketUtils.Encrypt(compressed, compressed.length, CrCSeed);
			byte[] endPacket = PacketUtils.AppendCRC(encoded, encoded.length, CrCSeed);
			myServer.queue(new DatagramPacket(endPacket, endPacket.length, myAddress));
			//queuePacket(buf.toByteArray(), PACKET_ENCODE_COMPRESS_CRC_PACKET);
		} catch (Exception e) {
			System.out.println("Exception writing data to packet: " + e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Get if this session is valid or not.  A valid session is a session which has not received a Disconnect from the client,
	 * and which has not timed out.
	 * @return
	 */
	public boolean bIsValid() {
		return bValidSession;
	}
	
	/**
	 * Get the IP Address of this client.
	 * @return The Address
	 */
	public SocketAddress getAddress() {
		return myAddress;
	}
	
	/**
	 * Get the Port of this cient.
	 * @return The port.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Set the Port for this client.
	 * @param port -- The port.
	 */
	protected void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * This function performs the mathematical work of compressing and encrypting output streams according to the SOE protocol.
	 * @param buf -- The packet to sent.
	 * @param nLength -- The length of the packet.
	 * @param bNeedCompare -- If we need to compare what we generate to a known valid packet.
	 * @return -- A new UDP packet which may be sent to the client.
	 */
    private DatagramPacket PrepareForSendSWG(ByteArrayOutputStream buf, int nLength, boolean bNeedCompare) {
    	DatagramPacket dOut = null;
    	//ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
    	byte[] newOutput = null;
    	byte[] original = buf.toByteArray();
    	byte[] encrypted = null;
    	boolean comp = (original[1] != 2);
    	boolean enc = (original[1] != 2); 
    	// We take the original.  If we want to compress it, compress it.  If we want to encrypt it, encrypt it.
    	// If we want a crc, append it.
    	// NOTE:  If it's compressed, we must encrypt the compressed buffer, not the original.
    	try {
	    	if (comp) { 
	    		// No matter what, we check if we're compressing first.  Since the client will decode before
	    		// it attempts to decompress.
    			newOutput = PacketUtils.compress(original, comp);
	    	}
	    	
	    	// If we're not encoding this, shove it into a new output stream.
	    	// If we're not encoding this, it's most likely a SOE_SESSION_RESPONSE, and therefore has not been compressed.
			buf = new ByteArrayOutputStream();

	    	if (!enc) {
				if (comp) {
					buf.write(newOutput);
				} else {
					buf.write(original);
				}
			} else {
    		// If we are encoding this, we need to know if we compressed or not.  If we compressed, encode
	    		// the compression result.  If not, encode the original.
    			if (comp) {
    				encrypted = PacketUtils.Encrypt(newOutput, newOutput.length, CrCSeed);
    			} else { 
    				encrypted = PacketUtils.Encrypt(original, original.length, CrCSeed);
    			}
    			// Regardless, if we're encoding anything, stick the encrypted result into the new output stream.
    			buf.write(encrypted);
    			buf = PacketUtils.AppendCRC(buf, buf.size(), CrCSeed);
    		}
	    	dOut = new DatagramPacket(buf.toByteArray(), buf.size(), myAddress);
	    	return dOut;
    	} catch (Exception e) {
    		System.out.println("Error in SendSWG: " + e.toString());
    		e.printStackTrace();
    	}
    	return null;
    }
    
    /**
     * The Session Response packet, which is sent in response to a Session Request.
     */
    private void send_sesresp(){
    	ByteArrayOutputStream buf = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(buf);
		try {
			dOut.setOpcode(Constants.SOE_SESSION_RESPONSE);
			dOut.writeInt(connectionID);
			dOut.writeReversedInt(CrCSeed); // Should be CRC seed.
			dOut.writeByte((byte)2);
			dOut.writeByte((byte)1);
			dOut.writeByte((byte)4);
			dOut.writeReversedInt(LoginServer.MAX_PACKET_SIZE);
			byte[] buffer = buf.toByteArray();
			queueOutgoingPacket(buffer);
		} catch (Exception e) {
			// Who cares
		}
    }
    
    /**
     * The Login Server data, which is also sent in response to a Session Request packet.
     */
    private void send_loginserver(){
    	
    	ByteArrayOutputStream buf = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(buf);
		try {
			dOut.writeShort(Constants.SOE_CHL_DATA_A);
			dOut.writeShort(0);
			dOut.writeShort(Constants.WORLD_UPDATE);
			dOut.writeInt(Constants.LoginServerString);
			dOut.writeUTF(LoginServer.sLoginServerString);
			byte[] buffer = buf.toByteArray();
			
			queueOutgoingPacket(buffer);
			buf = new ByteArrayOutputStream();
			dOut = new SOEOutputStream(buf);
			dOut.writeShort(Constants.SOE_CHL_DATA_A);
			dOut.writeShort(server_sequence);
			dOut.writeShort(Constants.WORLD_UPDATE);
			dOut.writeInt(Constants.LoginServerID);
			dOut.writeInt(29411);
			buffer= buf.toByteArray();
			queueOutgoingPacket(buffer);
		} catch (IOException ee) {
			System.out.println("Error writing data to the data stream: " + ee.toString());
			ee.printStackTrace();
		} catch (Exception e) {
			System.out.println("Unknown error: " + e.toString());
			e.printStackTrace();
			
		}
    }
    
    /**
     * Gets the encryption seed for all packet communications with the client.
     * @return -- The CRC Seed.
     */
	public int getCrcSeed() {
		return CrCSeed;
	}
	    
	/**
	 * Gets the client connection ID.
	 * @return The connection ID.
	 */
	public long getConnectionID() {
		return connectionID;
	}
	
	/**
	 * Sends an acknowledgement packet to the client, indicating that we have received a given packet.
	 * @param packetType -- The type of packet we are acknowledging.
	 * @param sequence -- The sequence of the packet we are acknowledging.
	 */
	private void SendAck(short packetType, short sequence) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		try {
			if (packetType == Constants.SOE_CHL_DATA_A) {
				dOut.writeShort(Constants.SOE_ACK_A);
			} else if (packetType == Constants.SOE_CHL_DATA_B) {
				dOut.writeShort(Constants.SOE_ACK_B);
			} else if (packetType == Constants.SOE_CHL_DATA_C) {
				dOut.writeShort(Constants.SOE_ACK_C);
			} else if (packetType == Constants.SOE_CHL_DATA_D) {
				dOut.writeShort(Constants.SOE_ACK_D);
			}
			dOut.writeReversedShort(sequence);
			dOut.writeBoolean(false);
			dOut.writeShort(0);
			bOut = PacketUtils.AppendCRC(bOut, bOut.size(), CrCSeed);
		} catch (Exception e) {
			System.out.println("Error writing data to the output stream in SendAck: " + e.toString());
			e.printStackTrace();
		}
		queueOutgoingPacket(bOut.toByteArray());
	}
	
	/**
	 * Handles the processing of all SOE_CHL_DATA_? packets.
	 * @param incPacket -- The packet to be handled.
	 * @param UpdateType -- The subtype of the packet.
	 * @param packetDataType -- The data type of the packet.
	 * @param packetSequence -- The sequence of the packet.
	 * @param bWasMultied -- Indicates if the packet was part of a multipacket.
	 */
	private void HandleChlData(SOEInputStream incPacket, short UpdateType, short packetDataType, short packetSequence, boolean bWasMultied) {
		
		try {
			if (UpdateType == Constants.CLIENT_UI_UPDATE) {
				HandleClientUIUpdate(incPacket);
			} else if (UpdateType == Constants.WORLD_UPDATE) {
				HandleWorldUpdate(incPacket);
			} else if (UpdateType == Constants.ACCOUNT_UPDATE) {
				HandleAccountUpdate(incPacket);
			} else if (UpdateType == Constants.SERVER_UPDATE) {
				HandleServerUpdate(incPacket, packetDataType, packetSequence);
			} else if (UpdateType == Constants.OBJECT_UPDATE) {
				HandleObjectUpdate(incPacket);
			} else if (UpdateType == Constants.UPDATE_SIX) {
				HandleUnknownUpdateSix(incPacket);
			} else if (UpdateType == Constants.UPDATE_SEVEN) {
				HandleUnknownUpdateSeven(incPacket);
			} else if (UpdateType == Constants.SCENE_UPDATE) {
				HandleSceneUpdate(incPacket);
			} else if (UpdateType == Constants.UPDATE_NINE) {
				HandleUnknownUpdateNine(incPacket);
			} else if (UpdateType == Constants.UPDATE_TEN) {
				HandleUnknownUpdateTen(incPacket);
			} else if (UpdateType == Constants.UPDATE_ELEVEN) {
				HandleUnknownUpdateEleven(incPacket);
			} else if (UpdateType == Constants.UPDATE_CHAR_CREATE) {
				HandleClientCreateCharacter(incPacket);
			} else if (UpdateType == Constants.UPDATE_THIRTEEN) {
				HandleUnknownUpdateThirteen(incPacket);
			} else if (UpdateType == Constants.UPDATE_FOURTEEN) {
				HandleUnknownUpdateFourteen(incPacket);
			} else if (UpdateType == Constants.UPDATE_FIFTEEN) {
				HandleUnknownUpdateFifteen(incPacket);
			}
		
		} catch (Exception e) {
			System.out.println("Error reading packet update type: " + e.toString());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Handles UI Update packets incoming from the client.
	 * @param incPacket -- The incoming packet.
	 * @throws Exception -- If an error occurs.
	 */
	private void HandleClientUIUpdate(SOEInputStream incPacket)  throws Exception{
		
	}

	/**
	 * Handles World Update packets incoming from the client.
	 * @param incPacket -- The incoming packet.
	 * @throws Exception -- If an error occurs.
	 */

	private void HandleWorldUpdate(SOEInputStream incPacket)  throws Exception{
		
		
	} 
	
	/**
	 * Handles Account Update packets incoming from the client.
	 * @param incPacket -- The incoming packet.
	 * @throws Exception -- If an error occurs.
	 */
	private void HandleAccountUpdate(SOEInputStream incPacket)  throws Exception{
		int commandCRC = incPacket.readInt();
		switch (commandCRC) {
			case Constants.ClientDeleteCharacter: {
				handleClientDeleteCharacter(incPacket);
				break;
			}
			default: {
				//PacketUtils.printPacketToScreen(incPacket.getBuffer(), "Unknown LServer Account Update");
				break;
			}
		}
		
	}
	
	/**
	 * Handles Server Update packets incoming from the client.
	 * @param incPacket -- The incoming packet.
	 * @throws Exception -- If an error occurs.
	 */
	private void HandleServerUpdate(SOEInputStream incPacket, short packetType, short packetSequence) throws Exception{
		int updateCRC = incPacket.readInt();
		if (updateCRC == Constants.LoginClientId) {
			HandleServerUpdate_LoginClientID(incPacket, packetType, packetSequence);
		}
		
	}
	
	/**
	 * Handles Object Update packets incoming from the client.
	 * @param incPacket -- The incoming packet.
	 * @throws Exception -- If an error occurs.
	 */
	private void HandleObjectUpdate(SOEInputStream incPacket)  throws Exception{
		
	} 
	private void HandleUnknownUpdateSix(SOEInputStream incPacket)  throws Exception{
		
	}

	private void HandleUnknownUpdateSeven(SOEInputStream incPacket)  throws Exception{
		
	}

	private void HandleSceneUpdate(SOEInputStream incPacket)  throws Exception{
		
		
	}
		
	private void HandleUnknownUpdateNine(SOEInputStream incPacket)  throws Exception{
		
		
	} 
	private void HandleUnknownUpdateTen(SOEInputStream incPacket)  throws Exception{
		
		
	}
	
	private void HandleUnknownUpdateEleven(SOEInputStream incPacket)  throws Exception{
		
	}

	private void HandleClientCreateCharacter(SOEInputStream incPacket)  throws Exception{
		
	}
	
	private void HandleUnknownUpdateThirteen(SOEInputStream incPacket)  throws Exception{
		
	} 
		
	private void HandleUnknownUpdateFourteen(SOEInputStream incPacket)  throws Exception{

	}
	
	private void HandleUnknownUpdateFifteen(SOEInputStream incPacket)  throws Exception{
		
		
	}
	
	private boolean bWaitingForAccountCreation = false;

	/**
	 * Returns whether this client is waiting for the Database to create an account for it or not.
	 * @return -- The account creation wait state.
	 */
	public boolean getWaitingAccountCreation() {
		return bWaitingForAccountCreation;
	}
	
	
	public String getUsername() {
		return sUserNameLC;
	}
	
	public String getPassword() {
		return sUserPasswordLC;
	}
	
	/**
	 * This function reads the username and password of the Player attempting to log in and verifies if they are known.
	 * If they are not known, the server will either reject the login attempt, or it will attempt to create
	 * a new user account, depending on the server's auto account registration setting.
	 * @param incPacket -- The packet
	 * @param packetType -- The packet type.
	 * @param packetSequence -- The packet sequence.
	 */
	private void HandleServerUpdate_LoginClientID(SOEInputStream incPacket, short packetType, short packetSequence) {
		// Packet format:  
		// UTF8 Username
		// UTF8 Password.
		// UTF8 Client Version
		
		try {
			sUserName = incPacket.readUTF();
			sUserNameLC = sUserName.toLowerCase();
			sUserPassword = incPacket.readUTF();
			sUserPasswordLC = sUserPassword;//.toLowerCase(); cant lc the passwords!!!!
			sClientVersion = incPacket.readUTF();
			//System.out.println("Received client version " + sClientVersion);
		} catch (Exception ee) {
			System.out.println("Error reading the packet: " + ee.toString());
			ee.printStackTrace();
		}
		boolean bValidClientVersion = true;
		//System.out.println("/t/tLoginClient:  Handling incoming connection.");
		//System.out.println("Username: " + sUserName + ", Password: " + sUserPassword + ", Client Version: " + sClientVersion);
//		if (sClientVersion.equals(Constants.CLIENT_VERSION_STRINGS[Constants.CLIENT_VERSION_131])) {
//			bValidClientVersion = true;
//		} else if (sClientVersion.equals(Constants.CLIENT_VERSION_STRINGS[Constants.CLIENT_VERSION_14])) {
//			bValidClientVersion = true;
//		}
		
		//DEBUG
		DataLog.logEntry(String.format("DEBUG: Account name %s login attempt.", sUserNameLC), "LoginClient", Constants.LOG_SEVERITY_INFO, true, true);		
		
		if (bValidClientVersion) {
			iAccountID =  myServer.findClient(sUserNameLC, sUserPasswordLC);
			if (iAccountID < 1) {
				try {
					
					//Invalid session, login failed for one of the following reasons:
					bValidSession = false;
					
					if(iAccountID == Constants.ACCOUNT_CREATION_VBULLETIN_PASSWORD_MISMATCH) {
						
						//Invalid password.
						queueOutgoingPacket(PacketFactory.buildClientUIErrorMessage("Error", "Incorrect password.  Please exit the client COMPLETELY and try again."));
						
						//DEBUG
						DataLog.logEntry(String.format("DEBUG: Account name %s login attempt result: invalid password.", sUserNameLC), "LoginClient", Constants.LOG_SEVERITY_INFO, true, true);
					} else if(iAccountID == Constants.ACCOUNT_CREATION_BANNED) {
						
						//Banned account.
						queueOutgoingPacket(PacketFactory.buildClientUIErrorMessage("Error", "This account has been banned.  If you feel you should not be banned, contact the server administrator."));
						
						//DEBUG
						DataLog.logEntry(String.format("DEBUG: Account name %s login attempt result: account banned.", sUserNameLC), "LoginClient", Constants.LOG_SEVERITY_INFO, true, true);						
					} else if(iAccountID == Constants.ACCOUNT_CREATION_ALREADY_ACTIVE) {
						
						//Already logged in.
						queueOutgoingPacket(PacketFactory.buildClientUIErrorMessage("Error", "Account already logged in."));
						queueOutgoingPacket(PacketFactory.buildDisconnectClient(this, (short)0));
						
						//DEBUG
						DataLog.logEntry(String.format("DEBUG: Account name %s login attempt result: already logged in.", sUserNameLC), "LoginClient", Constants.LOG_SEVERITY_INFO, true, true);						
					} else if(iAccountID == Constants.ACCOUNT_CREATION_NO_VBULLETIN_ACCOUNT_FOUND) {
						
						//account registration disabled
						queueOutgoingPacket(PacketFactory.buildClientUIErrorMessage("Error", "This account doesn't exist and automatic account registration is disabled. Register on the forums and login using your forum account."));
						
						//DEBUG
						DataLog.logEntry(String.format("DEBUG: Account name %s login attempt result: account doesn't exist; auto registration disabled.", sUserNameLC), "LoginClient", Constants.LOG_SEVERITY_INFO, true, true);						
					} else if(iAccountID == Constants.ACCOUNT_CREATION_DATABASE_ERROR_ON_CREATE) {
						
						//error creating account
						queueOutgoingPacket(PacketFactory.buildClientUIErrorMessage("Error", "An error has occured while creating your account. Please contact the server administrator."));
						
						//DEBUG
						DataLog.logEntry(String.format("DEBUG: Account name %s login attempt result: error creating account.", sUserNameLC), "LoginClient", Constants.LOG_SEVERITY_INFO, true, true);						
					} else if (iAccountID == Constants.ACCOUNT_CREATION_INVALID_CHARACTER) { 
						queueOutgoingPacket(PacketFactory.buildClientUIErrorMessage("Error", "Invalid character in the account name."));
						DataLog.logEntry("DEBUG:  Account name " + sUserName + " has invalid character.", "LoginClient", Constants.LOG_SEVERITY_INFO, true, true);
					}
					
				} catch (IOException e) {
					System.out.println("Error building ClientUIErrorMessage: " + e.toString());
					e.printStackTrace();
				}
			} else {
				//System.out.println("Valid client -- sending cluster / character info.");
				bIsDeveloper = myServer.getIsDev((int)iAccountID);
				hasLoggedIn = true;
				lCharacterList = myServer.getCharacterListForAccount(iAccountID);
				sendLoginClientToken(); // Complete -- First packet that gets a sequence.  Sequence should be reversed short.
				sendLoginEnumCluster(); // Complete
				sendLoginClusterStatus(); // Complete
				//bWantToSendEnumerateCharacterID = true;
				sendEnumerateCharacterID(); // Complete
				//  After this, we want to set a boolean variable that flags that we want to compare what we built with
				// fullEnumerateBeforeCompress.
				//bComparingToHardCoded = true;
				myServer.getGUI().getDB().updateLastLoginTime(sUserName);
			}
		} else {
			try {
				queueOutgoingPacket(PacketFactory.buildClientUIErrorMessage("Error", "Invalid client version " + sClientVersion));
			} catch (IOException e) {
				System.out.println("Error building ClientUIErrorMessage: " + e.toString());
				e.printStackTrace();
				// Meh.
			}

			bValidSession = false;
		}
	}

	// Any packets sent before this should not increment the server sequence.
	private void sendLoginClientToken() {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream() ;
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		try {
			dOut.setOpcode(Constants.SOE_CHL_DATA_A);
			
			dOut.setSequence(0);
			dOut.setUpdateType(Constants.SERVER_UPDATE);
			dOut.writeInt(Constants.LoginClientToken);
			dOut.writeInt(Session_Key.length + 4);
			dOut.write(Session_Key);
			//System.out.println("LoginClient: writing account ID " + iAccountID);
			dOut.writeInt((int)iAccountID);
			dOut.writeInt((int)iAccountID);
			dOut.writeUTF(sUserName);
		} catch (Exception e) {
			System.out.println("Error putting information into the output stream: " + e.toString());
			e.printStackTrace();
		}
		byte[] buffer = bOut.toByteArray();
		queueOutgoingPacket(buffer);
	}
	
	private void sendLoginEnumCluster() {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream() ;
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		Vector<DatabaseServerInfoContainer> serverContainers = DatabaseInterface.getZoneServers(bIsDeveloper);
		
		try {
			dOut.setOpcode(Constants.SOE_CHL_DATA_A);
			
			dOut.setSequence(0);
			dOut.setUpdateType(Constants.ACCOUNT_UPDATE);
			dOut.writeInt(Constants.LoginEnumCluster);
			dOut.writeInt(serverContainers.size());
			//dOut.writeInt(1); // Number of servers.
			for (int i = 0; i < serverContainers.size(); i++) {
				DatabaseServerInfoContainer server = serverContainers.elementAt(i);
				dOut.writeInt(server.iServerID);
				dOut.writeUTF(server.sServerName);
				dOut.writeInt(server.iTimeOffset * 3600);
			}
			dOut.writeInt(8); //Default max characters per account. 
		} catch (Exception e) {
			System.out.println("Error in Build Login Cluster Enumeration: " + e.toString());
			e.printStackTrace();
		}
		byte[] buffer = bOut.toByteArray();
		//PacketUtils.printPacketToScreen(buffer, "LoginEnumCluster");
		queueOutgoingPacket(buffer);
	}

	protected void sendLoginClusterStatus() {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream() ;
		SOEOutputStream dOut = new SOEOutputStream(bOut);

		Vector<DatabaseServerInfoContainer> serverContainers = DatabaseInterface.getZoneServers(bIsDeveloper);
		try { 
			dOut.setOpcode(Constants.SOE_CHL_DATA_A);
			
			dOut.setSequence(0);
			dOut.setUpdateType(Constants.WORLD_UPDATE);
			dOut.writeInt(Constants.LoginClusterStatus);
			
			dOut.writeInt(serverContainers.size());
			for (int i = 0; i < serverContainers.size(); i++) {
				DatabaseServerInfoContainer server = serverContainers.elementAt(i);
				dOut.writeInt(server.iServerID);
				InetAddress serverAddress = InetAddress.getByName(server.sLocalAddress);
				dOut.writeUTF(serverAddress.getHostAddress());
				dOut.writeShort(server.iZonePort);
				dOut.writeShort(server.iPingPort);
				dOut.writeInt(server.iCurrentPopulation);
				dOut.writeInt(server.iMaxPopulation);
				dOut.writeInt(server.iMaxCharactersPerAccount);
				dOut.writeInt(server.iTimeOffset * 3600);
				int status = myServer.getZoneServerStatus(server.iServerID);
				if (bIsDeveloper) {
					if (status == Constants.SERVER_STATUS_LOCKED) {
						dOut.writeInt(Constants.SERVER_STATUS_ONLINE);
					} else {
						dOut.writeInt(this.myServer.getZoneServerStatus(server.iServerID));
					}
				} else {
					dOut.writeInt(status);
				}
				if (i == 0) {
					dOut.writeByte(0);
				} else {
					dOut.writeByte(-1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] buffer = bOut.toByteArray();
		//PacketUtils.printPacketToScreen(buffer, "LoginClusterStatus");
		queueOutgoingPacket(buffer);
	}
	
	private void sendEnumerateCharacterID() {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream() ;
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		try {
			dOut.setOpcode(Constants.SOE_CHL_DATA_A);
			dOut.setSequence(server_sequence);
			dOut.setUpdateType(Constants.WORLD_UPDATE);
			dOut.writeInt(Constants.EnumerateCharacterId);
			// Need to tell it how many we have, stupid.
			dOut.writeInt(lCharacterList.size());
			if (!lCharacterList.isEmpty()) {
				for (int i = 0; i < lCharacterList.size(); i++) {
					Player character = lCharacterList.elementAt(i);
					dOut.writeUTF16(character.getFullName());
					dOut.writeInt(character.getCRC());
					dOut.writeLong(character.getID());
					dOut.writeInt(character.getServerID());
					dOut.writeInt(1); // Logically, this would be the planet id?
				}
			}
		} catch (Exception e) {
			System.out.println("Error building Enumerate Cluster: " + e.toString());
			e.printStackTrace();
		}
		byte[] buffer = bOut.toByteArray();
		queueOutgoingPacket(buffer);
	}
	
	/**
	 * Returns an outgoing packet to the outgoing packet queue.
	 * @param packet The packet
	 * @throws Exception If an error occurs.
	 */
	private void returnPacketToQueue(byte[] packet) throws Exception{
		packetQueue.add(packet);
	}
	
	private void SendPong() {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		SOEOutputStream dOut = new SOEOutputStream(bOut);
		try {
			dOut.writeShort(Constants.SOE_PING);
			dOut.writeBoolean(false);
			dOut.writeShort(0);
			queueOutgoingPacket(bOut.toByteArray());
		} catch (Exception e) {
			
		}
				
	}
	
	protected long getAccountID() {
		return iAccountID;
	}

	protected boolean getHasLoggedIn() {
		return hasLoggedIn;
	}
	
	protected void setHasLoggedIn(boolean b) {
		hasLoggedIn = b;
	}
	
	private void handleClientDeleteCharacter(SOEInputStream dIn) throws IOException {
		int serverID = dIn.readInt();
		long playerID = dIn.readLong();
		Vector<Player> vPlayerList = myServer.getCharacterListForServer(serverID);
		for (int i = 0; i < vPlayerList.size(); i++) {
			Player player = vPlayerList.elementAt(i);
			if (player.getID() == playerID) {
				// Found him
				player.setIsDeleted(true);
				myServer.getGUI().getDB().updatePlayer(player, true, true);
				packetQueue.add(PacketFactory.buildDeleteCharacterResponse(true));
				return;
			}
		}
		packetQueue.add(PacketFactory.buildDeleteCharacterResponse(false));
		// If we made it here, the deletion failed.
		
	}
	
}




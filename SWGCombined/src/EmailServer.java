import java.net.SocketAddress;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The in game mail object will contain all of the information for an in-game
 * SWG-mail. This includes data on whether the mail has been read or not, if
 * it's new, who it's from, who it's to, and any datapad items it contains. This
 * class will also be responsible for sending the emails to the players and keep
 * track of all email messages to all players within a given server
 * 
 * @author Darryl
 * 
 */
public class EmailServer implements Runnable {
	private ZoneServer zServer;

	// private EmailServer eServer;
	private DatabaseInterface dbInterface;

	private Thread myThread;

	// private AccountData account;
	public final static long serialVersionUID = 1l;

	// private final static int maxMessageCharacters = 2048; // if larger we
	// will
	// break it apart.
	// private final static String domainSeparatorChar = ".";// this charatcer
	// tells us the
	// email goes to
	// another server
	// plasmaflow;interesting.clustername;maachine.clustername
	// - this identifies
	// that one email
	// address is local
	// and 2 are for
	// other servers
	// these are the Server Queues
	public ConcurrentLinkedQueue<SWGEmail> qNewClientMessage;

	public ConcurrentLinkedQueue<ZoneClient> qEmailRequest;

	public ConcurrentLinkedQueue<SWGEmail> qSetReadFlag;

	public ConcurrentLinkedQueue<SWGEmail> qEmailContentRequest;

	public ConcurrentLinkedQueue<SWGEmail> qEmailDeleteRequest;

	public ConcurrentLinkedQueue<ZoneClient> qClearSentEmails;

	public Vector<SWGEmail> vSentEmails;

	public Player SystemPlayer;

	public Player StructureManager;

	public Player BankManager;

	public static Player BM;

	// End Queues
	public EmailServer(ZoneServer Zone) {
		// set who is our zone
		this.zServer = Zone;
		dbInterface = zServer.getGUI().getDB();
		// set our selves // Note -- this is not necessary in Java. this always
		// knows what this is.
		// this.eServer = this;
		// start
		myThread = new Thread(this);
		myThread.setName("Email server thread");
		myThread.start();
	}

	protected void terminate() {
		myThread = null;
	}

	protected boolean bIsThreadActive() {
		return (myThread != null);
	}

	public void run() {

		// this is the system player object for emails
		SystemPlayer = new Player(zServer);
		SystemPlayer.setID(0);
		SystemPlayer.setFirstName("System");
		zServer.addNewPlayer(SystemPlayer);
		zServer.addUsedName("System");
		// end system player
		// this is the structure manager email player
		StructureManager = new Player(zServer);
		StructureManager.setID(19999999);
		StructureManager.setFirstName("Structure Manager");
		zServer.addNewPlayer(StructureManager);
		zServer.addObjectToAllObjects(StructureManager, false, false);
		zServer.addUsedName("Structure");
		// end structuremanager player
		// bank manager player
		BankManager = new Player(zServer);
		BankManager.setID(19999998);
		BankManager.setFirstName("Bank");
		zServer.addNewPlayer(BankManager);
		zServer.addObjectToAllObjects(BankManager, false, false);
		zServer.addUsedName("bank");
		BM = BankManager;
		// end bank manager

		qNewClientMessage = new ConcurrentLinkedQueue<SWGEmail>(); // This is
																	// the "Hey,
																	// I sent
																	// something"
																	// queue.
		qEmailRequest = new ConcurrentLinkedQueue<ZoneClient>(); // This is
																	// the "Hey,
																	// I'm here"
																	// queue.
		qSetReadFlag = new ConcurrentLinkedQueue<SWGEmail>(); // This is
																// simply done
																// in the code,
																// and doing an
																// update.
		qEmailContentRequest = new ConcurrentLinkedQueue<SWGEmail>(); // Wouldn't
																		// this
																		// be a
																		// queue
																		// of
																		// Integers?
		qEmailDeleteRequest = new ConcurrentLinkedQueue<SWGEmail>(); // Again,
																		// wouldn't
																		// this
																		// be a
																		// queue
																		// of
																		// Integers?
		qClearSentEmails = new ConcurrentLinkedQueue<ZoneClient>();
		vSentEmails = new Vector<SWGEmail>();

		while (myThread != null) {
			try {
				synchronized (this) {
					Thread.yield();
					wait(2000); // Run every tenth of a second? Let's run every
								// 2 seconds.
				}
				try {
					while (!qEmailRequest.isEmpty()) {
						// process mail delivery request
						// this a a client requesting its emails.
						ZoneClient client = qEmailRequest.remove();
						// Player player = client.getPlayer();
						if (client.getClientReadyStatus()) {
							// System.out.println("Sending Emails to Character:
							// " + client.getPlayer().getFullName());
							// client.insertPacket(PacketFactory.buildChatSystemMessage("Sending
							// Emails to You."));

							// build email for testing Comment out once db is in
							// Vector<Waypoint> WL = new Vector<Waypoint>();

							// SWGEmail E = new SWGEmail(16843009,
							// lPlayerID,lPlayerID,"(Subj) - Welcome to
							// Shards of the Force","(Body) - This is an email
							// test.", WL,false);
							// retrieve email from db
							// SWGEmail E = DB CALL GOES HERE
							long lPlayerID = client.getPlayer().getID();
							Vector<SWGEmail> vAllClientEmails = dbInterface
									.getAllEmailsForPlayer(lPlayerID, client
											.getPlayer().getServerID());
							// System.out.println("Client Has " +
							// vAllClientEmails.size() + " in his Mailbox.");
							boolean bHeadsUpSent = false;
							for (int i = 0; i < vAllClientEmails.size(); i++) {
								SWGEmail E = vAllClientEmails.elementAt(i);
								// System.out.println("Sending Email at
								// Position: " + i);
								if (!E.getDeleteFlag()) {
									// System.out.println("Sending Email Number:
									// " + E.getEmailID() );
									if (!E.isRead()) {
										if (!bHeadsUpSent) {
											client
													.insertPacket(PacketFactory
															.buildNewEmailNotificaion());
											bHeadsUpSent = true;
										}
										boolean bNewEmail = E.getIsNew();
										client
												.insertPacket(PacketFactory
														.buildEmailHeader(
																E,
																zServer
																		.getPlayer(E
																				.getSenderID()),
																zServer
																		.getPlayer(E
																				.getRecipID())));
										if (bNewEmail) {
											dbInterface.updateEmail(E, zServer
													.getPlayer(E.getRecipID()));
										}
									} else if (E.isRead()) {
										client
												.insertPacket(PacketFactory
														.buildEmailHeader(
																E,
																zServer
																		.getPlayer(E
																				.getSenderID()),
																zServer
																		.getPlayer(E
																				.getRecipID())));
										client
												.insertPacket(PacketFactory
														.buildEmailContent(
																E,
																zServer
																		.getPlayer(E
																				.getRecipID())));
									}

									SWGEmail M = new SWGEmail(E.getEmailID());
									M.setSentFlag();
									M.setRecipientID(lPlayerID);
                                    if(!vSentEmails.contains(M))
                                    {
                                        vSentEmails.add(M);
                                    }
                                   
								} else if (E.getDeleteFlag()) {
									// need an email deletion transaction here.
								}
							}
							if (qEmailRequest.contains(client)) {
								// System.out.println("Error in Email Queue
								// Client was not removed from queue.");
							} else {
								// System.out.println("Client removed from Email
								// Queue Successfully!\n");
							}
						} else {
							// client not ready return to queue
                            if(qEmailRequest.contains(client))
                            {
                                qEmailRequest.offer(client);
                            }
							// System.out.println("Client was not ready to
							// receive messages. Request Returned to Queue.");
						}
					}
				} catch (Exception e) {
					DataLogObject E = new DataLogObject(
							"EmailServer::EmailRequestQueue",
							"Exception Ocurred while processing a Client Request for emails. "
									+ e.toString(),
							Constants.LOG_SEVERITY_ROUTINE_FAILED);
					DataLog.qServerLog.add(E);
				}
				try {
					if (qEmailContentRequest.size() != 0) {
						// process email set read flag
						SWGEmail R = qEmailContentRequest.remove();
						// System.out.println("Email Content Requested for ID:
						// "+ R.getEmailID());
						// build email for testing comment out once db is ready
						// SWGEmail E = DB CALL GOES HERE WHERE DATABASE Email
						// ID = R.getEmailID()

						// I assume we have an email ID at this stage?
						// SWGEmail E = R;// replace R with the result from the
						// DB

						SWGEmail E = dbInterface.getEmailByID(R.getEmailID(), R
								.getTransactionRequester().getPlayer());
						if (E != null) {
							// E = new SWGEmail(16843009,
							// client.getPlayer().getID(),client.getPlayer().getID(),"(Subj)
							// - Welcome to Shards of the Force","(Body) - This is
							// an email test.", WL, false);
							// client.insertPacket(PacketFactory.buildNewEmailNotificaion());
							// client.insertPacket(PacketFactory.buildEmailHeader(E,
							// client.getPlayer(), client.getPlayer()));
							E.setRead();
							R.getTransactionRequester().insertPacket(
									PacketFactory.buildEmailContent(E, R
											.getTransactionRequester()
											.getPlayer()));
							// System.out.println("Updating Email Requested for
							// ID: "+ R.getEmailID());
							dbInterface.updateEmail(E, R
									.getTransactionRequester().getPlayer());
						}

					}
				} catch (Exception e) {
					DataLogObject E = new DataLogObject(
							"EmailServer::EmailContentRequestQueue",
							"Exception Ocurred while processing a Client Request for email content. "
									+ e.toString(),
							Constants.LOG_SEVERITY_ROUTINE_FAILED);
					DataLog.qServerLog.add(E);
				}
				try {
					if (qNewClientMessage.size() != 0) {
						// process new mail for delivery
						// New emails arrive here with an ID of -1 meaning its a
						// new message and will get an id upon
						// DB Insertion. This email will laso contain all
						// waypoints attached.

						SWGEmail E = qNewClientMessage.remove();
						/*
						 * System.out.println(">----New Email Being
						 * Processed.----<"); System.out.println("Message ID: " +
						 * E.getEmailID()); System.out.println("Originator ID: " +
						 * E.getSenderID()); System.out.println("Recipient ID: " +
						 * E.getRecipID()); System.out.println("Time Stamp: " +
						 * E.getMessageTime()); System.out.println("Subject: " +
						 * E.getHeader()); System.out.println("Message: " +
						 * E.getBody()); System.out.println("Attachment Count: " +
						 * E.getAttachments().size());
						 * System.out.println(">----------------------------------<");
						 * System.out.println(">SAVING<");
						 */
						dbInterface.saveEmail(E, zServer.getPlayer(E
								.getRecipID()));
						// System.out.println(">SAVED<");

					}
				} catch (Exception e) {
					DataLogObject E = new DataLogObject(
							"EmailServer::EmailRequestQueue",
							"Exception Ocurred while processing a Client Request for emails. "
									+ e.toString(),
							Constants.LOG_SEVERITY_ROUTINE_FAILED);
					DataLog.qServerLog.add(E);
				}

				try {
					if (qSetReadFlag.size() != 0) {
						// process email set read flag
						// Email item here contains the Object ID of the Email
						// we want to set to read.
						SWGEmail R = qSetReadFlag.remove();
						SWGEmail E = dbInterface.getEmailByID(R.getEmailID(), R
								.getTransactionRequester().getPlayer());
						E.setRead();
						dbInterface.updateEmail(E, R.getTransactionRequester()
								.getPlayer());
						// System.out.println("Email Read Flag Change Requested
						// for ID: " + E.getEmailID());

					}
				} catch (Exception e) {
					DataLogObject E = new DataLogObject(
							"EmailServer::EmailReadUpdateQueue",
							"Exception Ocurred while processing a Client Request for email read flag update. "
									+ e.toString(),
							Constants.LOG_SEVERITY_ROUTINE_FAILED);
					DataLog.qServerLog.add(E);
				}
				try {
					if (qEmailDeleteRequest.size() != 0) {
						// process email set read flag
						// E contains the email ID of the email to delete
						SWGEmail R = qEmailDeleteRequest.remove();
						// System.out.println("Email Deletion Requested for ID:
						// " + R.getEmailID());
						// EMAIL DELETE ROUTINE GOES HERE
						SWGEmail E = dbInterface.getEmailByID(R.getEmailID(), R
								.getTransactionRequester().getPlayer());
						E.setDeleteFlag();
						dbInterface.updateEmail(E, R.getTransactionRequester()
								.getPlayer());

					}
				} catch (Exception e) {
					DataLogObject E = new DataLogObject(
							"EmailServer::EmailDeleteQueue",
							"Exception Ocurred while processing a Client Request for emails. "
									+ e.toString(),
							Constants.LOG_SEVERITY_ROUTINE_FAILED);
					DataLog.qServerLog.add(E);
				}
				try {
					if (qClearSentEmails.size() != 0) {
						long lPlayerID = qClearSentEmails.element().getPlayer()
								.getID();
						for (int i = 0; i < vSentEmails.size(); i++) {
							if (vSentEmails.elementAt(0).getRecipientID() == lPlayerID) {
								vSentEmails.remove(0);
								i = 0;
							}
						}
					}
				} catch (Exception e) {

				}

				try {
					// THIS IS THE MESSAGE TRANSFER AGENT!!!!!!
					// here we iterate through all clients and check to see if
					// they have emails awaiting and put them in the queue
					ConcurrentHashMap<SocketAddress, ZoneClient> CL = zServer
							.getAllClients();
					// if(CL.size() >= 1)
					// {
					// //System.out.println("MTA Running. CLS:" + CL.size());
					// }
					for (int i = 0; i < CL.size(); i++) {
						ZoneClient client = CL.elements().nextElement();
						if (client != null && client.getClientReadyStatus()) {

							Vector<SWGEmail> EV = dbInterface
									.getNewEmailsForPlayer(client.getPlayer()
											.getID(), client.getServer()
											.getServerID());
							// System.out.println("MTA Processing Client: " +
							// client.getPlayer().getFullName() );
							// System.out.println("MTA Client Has " + EV.size()
							// + " New Email(s)" );
							int m = 0;
							while (EV.size() != 0) {
								boolean bHeadsUpSent = false;
								if (!EV.elementAt(m).isRead()) {
									// System.out.println("MTA Processing Unread
									// Email ID: " +
									// EV.elementAt(m).getEmailID() );
									SWGEmail NE = EV.elementAt(m);
									boolean eFound = false;
									for (int f = 0; f < vSentEmails.size(); f++) {
										SWGEmail TE = vSentEmails.elementAt(f);
										// System.out.println("Iterating Sent
										// Emails: " + f + " : ID: " +
										// TE.getEmailID() + " : NE ID: " +
										// NE.getEmailID());
										if (TE.getEmailID() == NE.getEmailID()) {
											eFound = true;
											f = vSentEmails.size() + 1;
										}
									}

									if (!eFound) {
										SWGEmail M = new SWGEmail(NE
												.getEmailID());
										M.setSentFlag();
										if (vSentEmails.add(M)) {
											// System.out.println("MTA Queueing
											// Email Packets.");
											if (!bHeadsUpSent) {
												// we only need to tell the
												// client one time that new
												// emails are on their way.
												// so we only send this packet
												// once.
												client
														.insertPacket(PacketFactory
																.buildNewEmailNotificaion());
												bHeadsUpSent = true;
											}
											client
													.insertPacket(PacketFactory
															.buildEmailHeader(
																	NE,
																	zServer
																			.getPlayer(NE
																					.getSenderID()),
																	zServer
																			.getPlayer(NE
																					.getRecipID())));
										} else {
											// System.out.println("MTA Could not
											// add email id to sent mail
											// list.");
											DataLogObject E = new DataLogObject(
													"EmailServer::EmailThread::MTA",
													"Error Ocurred while delivering a new message to a player. Could not add the new message to the SentMails Vector.",
													Constants.LOG_SEVERITY_CRITICAL);
											DataLog.qServerLog.add(E);
										}
									} else {
										// System.out.println("MTA Client
										// already has this email sent to him");
									}
								}
								m++;
								EV.remove(m);
							}
						} else {
							// this is going to happen very often, So we dont
							// log this.
							// DataLogObject E = new
							// DataLogObject("EmailServer::EmailThread::MTA","Found
							// a Null Client While Iterating through the list of
							// available
							// clients.",Constants.LOG_SEVERITY_MAJOR);
							// zServer.logServer.qServerLog.add(E);
						}
					}
				} catch (Exception e) {
                                    if(e instanceof java.lang.ArrayIndexOutOfBoundsException)
                                    {
                                        
                                    }
                                    else
                                    {
					DataLogObject E = new DataLogObject(
							"EmailServer::EmailThread::MTA",
							"Exception Ocurred in the EmailServer.java File MTA. "
									+ e.toString(),
							Constants.LOG_SEVERITY_ROUTINE_FAILED);
					DataLog.qServerLog.add(E);
                                    }
				}

			}// end try section
			catch (Exception e) {
				// work exceptions here
				System.out.println("Error in Email Server Thread: "
						+ e.toString());
				e.printStackTrace();
				DataLogObject E = new DataLogObject("EmailServer::EmailThread",
						"Exception Ocurred in the EmailServer.java File run() thread. "
								+ e.toString(),
						Constants.LOG_SEVERITY_ROUTINE_FAILED);
				DataLog.qServerLog.add(E);
			}
		} // end thread while loop
	}
}

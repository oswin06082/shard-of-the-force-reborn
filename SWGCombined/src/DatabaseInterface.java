import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * The DatabaseInterface handles all saving and loading of data from the MySQL
 * database.
 * 
 * @author Darryl
 * 
 */
public class DatabaseInterface implements Runnable {

	// Graphics stuff.
	private static String sDatabaseUsername = "";
	private static String sDatabasePassword = "";
	private static String sDatabaseAddressPrefix = "jdbc:mysql://";
	private static String sDatabaseUsernamePrefix = "?user=";
	private static String sDatabasePasswordPrefix = "&password=";
	private static String sDatabaseName = null;
	private TreeSet<PreparedStatement> vUpdateRequests;
	private boolean bConnected = false;
	private static Connection conn;
	private static Connection loginIntegrationConn;
	private String sDatabaseAddress;
	private int iDatabasePort = -1;
	private static Hashtable<Integer, Skills> vSkillsListByIndex = null;
	private static Hashtable<Integer, Skills> vSkillsListByCommandCRC = null;
	private static Hashtable<Integer, RadialTemplateData> vRadialTemplateData;
	private Vector<Player> vAllPlayers = null;
	private static Vector<AccountData> vAccounts = null;
	private static Hashtable<Integer, Experience> vExperienceTypes = null;
	private static Hashtable<Integer, ItemTemplate> vItemTemplateData;
	private Thread myThread;
	private static Vector<DatabaseServerInfoContainer> vServerInfoContainer = null;
	private SWGGui theGUI;
	private static Vector<String> vCharacterNameFilterProfane; // 12
	private static Vector<String> vCharacterNameFilterSyntax; // 16
	private static Vector<String> vCharacterNameFilterDeveloper; // 2
	private static Vector<String> vCharacterNameFilterCanonical; // 4
	private static Vector<String> vCharacterNameFilterNumber; // 11
	private static Hashtable<Integer, Vector<POI>> vPOIsByPlanetID;
	// private static HashMap<Integer, Vector<String>> vAllUsedCharacterNames;
	private static Hashtable<Integer, ResourceTemplateData> vResourceTemplateData;
	private final static long HEARTBEAT_DELAY_MS = 900000;
	private long lHeartbeatTimerMS = HEARTBEAT_DELAY_MS;
	private long lStartTimeMS = 0;
	private long lEndTimeMS = 0;
	boolean bInitializedOnce = false;
	// private static ConcurrentHashMap<Long,SOEObject> chmAllWorldObjects;
	private static boolean bEncryptPasswords;
	// private static int iEncryptionKey;
	private static Hashtable<Integer, MissionTemplate> vMissionTemplateData;
	private static Hashtable<Integer, MissionCollateral> vMissionCollateralData;
	private static Vector<DeedTemplate> vDeedTemplates;
	private static HashMap<Integer, Vector<CraftingSchematic>> vSchematicsBySkillID;
	private static CraftingSchematic[] vSchematicsByIndex;
	private static HashMap<Integer, SignIndexData> vServerSignIndexData;
	private static Hashtable<Integer, IFFData> vDecodedIFFData;
	// private static Hashtable<String, String> vResourceNameStrings;
	private static Vector<Long> vRestrictedAccessCells;
	private static ZoneServerRunOptions runOptions;
	private static Vector<StartingLocation> vStartingLocations;
	private static Hashtable<Integer, CombatAction> vSpecialAttacks;
	private static LoginIntegration integrationData;
	private int iZoneServerID = -1;
	// private static Vector<DraftSchematicAttributeData>
	// vComponentDataFromFile;
	// private static boolean bUseFileComponentData = false;

	public DatabaseInterface(String sUsername, String sPassword,
			String dbaseAddress, int dbasePort, String sDatabaseSchema,
			SWGGui gui, boolean boolEncryptPasswords, int intEncryptionKey,
			int zoneServerID) {
		iZoneServerID = zoneServerID;
		try {
			vCharacterNameFilterProfane = new Vector<String>();
			vCharacterNameFilterSyntax = new Vector<String>();
			vCharacterNameFilterDeveloper = new Vector<String>();
			vCharacterNameFilterCanonical = new Vector<String>();
			vCharacterNameFilterNumber = new Vector<String>();
			vRadialTemplateData = new Hashtable<Integer, RadialTemplateData>();
			vSchematicsBySkillID = new HashMap<Integer, Vector<CraftingSchematic>>();
			vRestrictedAccessCells = new Vector<Long>();
			vSchematicsByIndex = null;
			vPOIsByPlanetID = new Hashtable<Integer, Vector<POI>>();
			vMissionTemplateData = new Hashtable<Integer, MissionTemplate>();
			vMissionCollateralData = new Hashtable<Integer, MissionCollateral>();
			vDeedTemplates = new Vector<DeedTemplate>();
			vServerSignIndexData = new HashMap<Integer, SignIndexData>();
			vDecodedIFFData = new Hashtable<Integer, IFFData>();
			sDatabaseAddress = dbaseAddress;
			iDatabasePort = dbasePort;
			sDatabaseUsername = sUsername;
			sDatabasePassword = sPassword;
			sDatabaseName = sDatabaseSchema;
			bEncryptPasswords = boolEncryptPasswords;
			// iEncryptionKey = intEncryptionKey;
			// vResourceNameStrings = new Hashtable<String, String>();
			vStartingLocations = new Vector<StartingLocation>();
			vSpecialAttacks = new Hashtable<Integer, CombatAction>();
			if (sDatabaseName == null) {
				DataLog.logEntry(
						"SHUTDOWN:Error: No Database Selected For Use.",
						"DatabaseInterface", Constants.LOG_SEVERITY_CRITICAL,
						true, true);
				System.exit(-2);
			}
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			vUpdateRequests = new TreeSet<PreparedStatement>(); // TreeSets are
			// FIFO unless
			// otherwise
			// specified.
			theGUI = gui;
			initialize(zoneServerID);

		} catch (Exception e) {
			System.out
					.println("Critical error:  Could not load the MySQL/J connector!  Error: "
							+ e.toString());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Initialize the connection to MySQL.
	 */
	private void initialize(int zoneServerID) {

		DataLog.logEntry("Initializing database", "DatabaseInterface",
				Constants.LOG_SEVERITY_INFO, true, true);

		try {
			if (!bConnected) {
				StringBuffer s = new StringBuffer();
				s.append(sDatabaseAddressPrefix);
				s.append(sDatabaseAddress).append(":").append(iDatabasePort);
				s.append(sDatabaseName);
				s.append(sDatabaseUsernamePrefix);
				s.append(sDatabaseUsername);
				s.append(sDatabasePasswordPrefix);
				s.append(sDatabasePassword);
				String sConnString = s.toString();
				// System.out.println("DB COnnString: " + sConnString);
				if (conn != null) {
					try {
						conn.close();
						conn = null;
					} catch (SQLException e) {
						System.out.println("Error closing existing connection -- setting connection null anyway.");
						conn = null;
					}
				}
				conn = DriverManager.getConnection(sConnString); // Throws an
				// SQLException
				// if the
				// connection
				// attempt
				// fails.
				// if (conn.isValid())
				bConnected = true;
			}
		} catch (SQLException e) {
			System.out.println("SQL Exception encountered." + e.toString());
			System.out.println("Message: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
			e.printStackTrace();
			System.exit(-1); // Shut down the program if we cannot connect to
			// the database for some reason.
		}
		DataLog.logEntry("Connected", "DatabaseInterface",
				Constants.LOG_SEVERITY_INFO, true, true);
		if (zoneServerID != 0) {
			if (!bInitializedOnce) {
				loadAccounts();
				loadZoneServerRunOptions(zoneServerID);
				loadLoginIntegrationData();
				cleanEmails();
				initializeNameFilters();
				loadExperienceList();
				loadSkillList();
				loadCraftingSchematicsList();
				loadItemTemplateData();
				loadZoneServerData();
				loadResourceTemplateData();
				loadRadialTemplateData();
				loadMissionTemplateData();
				loadMissionCollateral();
				correctPlanetIDOnWorldObjects();
				loadDeedTemplates();
				loadSignIndexData();
				loadDecodedIFFData();
				loadPOIList();
				// loadResourceNameStrings();
				loadRestrictedAccessCells();
				loadStartingLocations();
				loadCraftedWeaponCaps();
				loadSpecialAttackTable();
				verifyLoginIntegration();
			}
			DataLog.logEntry("Database Preload Completed.",
					"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true,
					true);
		} else {
			if (!bInitializedOnce) {
				loadZoneServerData();
				loadLoginIntegrationData();
				verifyLoginIntegration();
				DataLog.logEntry("Database Preload Completed.",
						"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true,
						true);
			}
		}
		bInitializedOnce = true;
		myThread = new Thread(this);
		myThread.setName("DatabaseInterface thread");
		myThread.start();
	}

	protected boolean getCanSafelyShutdown() {
		return vUpdateRequests.isEmpty();
	}

	/**
	 * Load the list of names which the client may NOT choose as a character
	 * name.
	 */
	private static void initializeNameFilters() {
		Statement s = null;
		ResultSet r = null;
		try {
			String query = "Select `name` from `name_filter` where `deny_reason` = "
					+ Constants.NAME_DECLINED_IS_DEVELOPER;

			s = conn.createStatement();
			r = null;
			if (s.execute(query)) {
				r = s.getResultSet();
				while (r.next()) {
					vCharacterNameFilterDeveloper.add(r.getString(1)
							.toLowerCase());
				}
				r.close();
			}
			s.close();
			s = conn.createStatement();
			query = "Select `name` from `name_filter` where `deny_reason` = "
					+ Constants.NAME_DECLINED_IS_CANONICAL;
			if (s.execute(query)) {
				r = s.getResultSet();
				while (r.next()) {
					vCharacterNameFilterCanonical.add(r.getString(1)
							.toLowerCase());
				}
				r.close();
			}
			s.close();
			s = conn.createStatement();
			query = "Select `name` from `name_filter` where `deny_reason` = "
					+ Constants.NAME_DECLINED_IS_NUMBER;
			if (s.execute(query)) {
				r = s.getResultSet();
				while (r.next()) {
					vCharacterNameFilterNumber
							.add(r.getString(1).toLowerCase());
				}
				r.close();
			}
			s.close();
			s = conn.createStatement();
			query = "Select `name` from `name_filter` where `deny_reason` = "
					+ Constants.NAME_DECLINED_IS_PROFANE;
			if (s.execute(query)) {
				r = s.getResultSet();
				while (r.next()) {
					vCharacterNameFilterProfane.add(r.getString(1)
							.toLowerCase());
				}
				r.close();
			}
			s.close();
			s = conn.createStatement();
			query = "Select `name` from `name_filter` where `deny_reason` = "
					+ Constants.NAME_DECLINED_SYNTAXICALLY_WRONG;
			if (s.execute(query)) {
				r = s.getResultSet();
				while (r.next()) {
					vCharacterNameFilterSyntax
							.add(r.getString(1).toLowerCase());
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
			query = null;
			Runtime.getRuntime().gc();
		} catch (Exception e) {
			System.out.println("Error loading name filter data: "
					+ e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (r != null) {
					r.close();
					r = null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception e) {
				// Oh well.
			}
		}
	}

	/**
	 * Add a SQL statement to be executed by the thread.
	 * 
	 * @param s
	 *            -- The SQL statement to be added.
	 */
	public void addStatement(PreparedStatement s) {
		try {
			vUpdateRequests.add(s);
		} catch (ClassCastException e) {
			try {
				DataLog
						.logEntry(
								"Unable to add Prepared Statement to Database save queue -- Executing Immediately.",
								"DatabaseInterface",
								Constants.LOG_SEVERITY_CRITICAL, true, true);
				s.execute();
				s.close();
				s = null;
			} catch (Exception ee) {
				// Well, we tried.
			}
		}
	}

	/**
	 * The main run function of the DatabaseInterface thread. This thread
	 * includes a keep-alive with the database, and also executes any pending
	 * Statements.
	 */
	public void run() {
		lStartTimeMS = System.currentTimeMillis();
		while (myThread != null) {
			boolean bNeedReconnect = false;
			PreparedStatement statement = null;
			try {
				synchronized (this) {
					Thread.yield();
					wait(10000);
				}

				// System.out.println("Database run.");
				if (vUpdateRequests.isEmpty()) {
					if (lHeartbeatTimerMS < 0) {
						lHeartbeatTimerMS = HEARTBEAT_DELAY_MS;
						String query = "Select * from `account`;";
						Statement s = conn.createStatement();
						s.execute(query);
						s.close();
						s = null;
						s = conn.createStatement();
						s.execute(query);
						s.close();
						s = null;
						if (integrationData != null
								&& integrationData.isConnected()) {
							String iquery = "Select * from `"
									+ integrationData.getTablename() + "`;";
							Statement is = loginIntegrationConn
									.createStatement();
							is.execute(iquery);
							is.close();
							is = null;
							is = conn.createStatement();
							is.execute(query);
							is.close();
							is = null;
						}
					}
				} else {
					// Statement s = conn.createStatement();
					// System.out.println(vUpdateRequests.size() +
					// " prepared statements waiting to execute.");
					statement = vUpdateRequests.pollFirst();
					if (statement != null) {
						statement.execute();
						statement.close();
						statement = null;
					}
				}
				Thread.yield();
			} catch (InterruptedException e) {
				// Who cares
			} catch (SQLException ee) {
				System.out
						.println("Exception thrown while performing SQL query: "
								+ ee.toString());
				System.out.println("SQL State: " + ee.getSQLState());
				System.out.println("SQL Error code: " + ee.getErrorCode());
				System.out.println("SQL Message: " + ee.getMessage());
				ee.printStackTrace();
				bNeedReconnect = true;
			} catch (Exception eee) {
				System.out
						.println("Unknown exception in DatabaseInterface.run() : "
								+ eee.toString());
				eee.printStackTrace();
			} catch (java.lang.OutOfMemoryError outMemoryErr) {
				bNeedReconnect = true;
			} 
			
			if (bNeedReconnect) {
				try {
					conn.close();
					loginIntegrationConn.close();
				} catch (Exception e) {
					// D'oh!
				} finally {
					conn = null;
					loginIntegrationConn = null;
					bInitializedOnce = false;
					bInitializedStaticObjects = false;
					initialize(iZoneServerID);
				}
			}		
			lEndTimeMS = System.currentTimeMillis();
			lHeartbeatTimerMS -= (lEndTimeMS - lStartTimeMS);
		}
	}

	protected static Vector<AccountData> getAccounts() {
		if (vAccounts == null) {
			loadAccounts();
		}
		return vAccounts;
	}
	/**
	 * Load all of the Account data from the database.
	 */
	protected static void loadAccounts() {
		if (vAccounts != null) {
			return;
		}
		Statement s=  null;
		ResultSet result = null;
		try {
			vAccounts = new Vector<AccountData>();
			s = conn.createStatement();
			String query;
			/*
			 * if(bEncryptPasswords) { query =
			 * "SELECT `account_id`,`username`, AES_DECRYPT(`password`,'" +
			 * iEncryptionKey +
			 * "'),`station_id`,`gm`,`banned`,`joindate`,`lastlogin`,`active`,`developer`,`jedimask` From `account`;"
			 * ; } else {
			 * 
			 * }
			 */
			query = "SELECT * From `account`;";
			if (s.execute(query)) {
				result = s.getResultSet();
				while (result.next()) {
					AccountData data = new AccountData();
					data.setAccountID(result.getInt(1));
					data.setUsername(result.getString(2));
					data.setPassword(result.getString(3));
					data.setIsGM(result.getBoolean(5));
					data.setIsBanned(result.getBoolean(6));
					// data.setJoinTimestamp(result.getTimestamp(7).getTime());
					// data.setLastActive(result.getTimestamp(8).getTime());
					data.setActiveAccount(result.getBoolean(9));
					// boolean bDeveloper = result.getBoolean(10);
					data.setIsDeveloper(result.getBoolean(10));
					data.setJediBitMask(result.getLong(11));
					vAccounts.add(data);
				}
				result.close();
				result = null;
			}
			s.close();
			s = null;

		} catch (SQLException e) {
			System.out.println("Caught SQL Exception.  Message: "
					+ e.getMessage());
			System.out.println("SQL State: " + e.getSQLState());
			System.out.println("SQL Error code: " + e.getErrorCode());
			System.out.println(e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (result != null) {
					result.close();
					result = null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception e) {
				// Oh well.
			}
		}
	}

	/**
	 * Creates a new Account in the Database.
	 * 
	 * @param sUsername
	 *            -- The username of the new account
	 * @param sPassword
	 *            -- The password of the new account
	 * @return -- The ID of the new account
	 * @throws SQLException
	 *             -- If an error occured creating the new account.
	 */
	protected int createAccount(String sUsername, String sPassword) {
		// if (!conn.isValid(1)) {
		// bConnected = false;
		// initialize();
		// }
		int valueToReturn = 0;
		System.out.println("createAccount Invoked");
		if (sUsername.contains("\\")) {
			return Constants.ACCOUNT_CREATION_INVALID_CHARACTER;
		}
		Statement s = null;
		ResultSet r = null;
		if (integrationData != null && integrationData.getLogintype() == Constants.LOGIN_INTEGRATION_NOT_INTEGRATED) {
			String query = "Select max(account_id) from `account`";
			try {
				s = conn.createStatement();
				r = null;
				int iNewAccountID = 0;
				if (s.execute(query)) {
					r = s.getResultSet();
					if (r.next()) {
						iNewAccountID = r.getInt(1) + 1;
					} else {
						iNewAccountID = 1;
					}
					r.close();
					r = null;
				}
				s.close();
				s = null;
				if (iNewAccountID != 0) {
					StringBuffer buff = new StringBuffer();
					long lCurrentTime = System.currentTimeMillis();
					int iStationID = new Random(lCurrentTime)
							.nextInt(Integer.MAX_VALUE);

					if (bEncryptPasswords) {
						sPassword = PacketUtils.encryptPassword(sPassword);
					}
					buff.append("Insert into `account` values (").append(
							iNewAccountID).append(", '").append(sUsername)
							.append("', '").append(sPassword).append("',")
							.append(iStationID).append(", 0, 0, '").append(
									new Timestamp(lCurrentTime).toString())
							.append("', '").append(
									new Timestamp(lCurrentTime).toString())
							.append("', 1, 0, 0);");

					query = buff.toString();
					s = conn.createStatement();
					s.execute(query);
					s.close();
					s = null;
					AccountData data = new AccountData();
					data.setAccountID(iNewAccountID);
					data.setActiveAccount(true);
					data.setEmailAddress("unknown@changeme.com");
					data.setIsBanned(false);
					data.setIsGM(false);
					data.setIsDeveloper(false);
					data.setJoinTimestamp(lCurrentTime);
					data.setLastActive(lCurrentTime);
					data.setPassword(sPassword);

					data.setStationID(iStationID);

					data.setUsername(sUsername);
					data.setJediBitMask(0);
					vAccounts.add(data);
					theGUI.getLoginServer().addClientAccountFromDatabase(data);
				}

				lHeartbeatTimerMS = HEARTBEAT_DELAY_MS;
				valueToReturn = iNewAccountID;
			} catch (Exception e) {
				System.out
						.println("Error creating new account " + e.toString());
				System.out.println("Last SQL query: " + query);
				valueToReturn = 0;

			} finally {
				try {
					if (r != null) {
						r.close();
						r = null;
					}
					if (s != null) {
						s.close();
						s = null;
					}
				} catch (Exception e) {
					// Oh well.
				}
			}
		} else if (integrationData != null
				&& integrationData.getLogintype() == Constants.LOGIN_INTEGRATION_VBULLETIN)// vBulletin integration
		{
			Statement vB = null;
			Statement vB1 = null;
			Statement emu = null;
			ResultSet vBr = null;
			ResultSet emur = null;
			try {
				System.out
						.println("Checking vBulletin DB For account for user: "
								+ sUsername);
				vB = loginIntegrationConn.createStatement();
				vB1 = loginIntegrationConn.createStatement();
				String vBquery = "Select * From "
						+ integrationData.getTableprefix()
						+ integrationData.getTablename() + " Where `"
						+ integrationData.getUsernamefield() + "` = '"
						+ sUsername + "';";
				int vBUserID = 0;
				// String vBUserName = "";
				String vBPassword = "";
				String vBsalt = "";
				String vBMemberGroupIds = "";
				String vBHashedPassword = "";
				String vBEmail = "";
				String sUserGroups[];
				// int iUserGroupID = 0;
				boolean vBAccount = false;

				if (vB.execute(vBquery)) {
					// System.out.println("Query Executed");
					vBr = vB.getResultSet();
					while (vBr.next()) {
						System.out.println("Getting Result");
						// vBUserName =
						// vBr.getString(integrationData.getUsernamefield());
						System.out.println("VBUserName Found: "
								+ vBr.getString(integrationData
										.getUsernamefield()));
						vBPassword = vBr.getString(integrationData
								.getPasswordfield());
						vBsalt = vBr.getString(integrationData.getKeyfield());
						vBMemberGroupIds = vBr.getString("membergroupids");
						sUserGroups = vBMemberGroupIds.split(",");
						// iUserGroupID = vBr.getInt("usergroupid");
						vBUserID = vBr.getInt("userid");
						vBEmail = vBr.getString("email");
						// System.out.println("salt Value: " + vBsalt);
						System.out.println("Hashing Password Received");
						vBquery = "SELECT md5(CONCAT(md5('" + sPassword
								+ "') , '" + vBsalt + "')) As `hashed`;";
						// System.out.println("Hash Query: " + vBquery);
						if (vB1.execute(vBquery)) {
							emur = vB1.getResultSet();
							while (emur.next()) {
								vBHashedPassword = emur.getString("hashed");
							}
							emur.close();
							vB1.close();
							System.out.println("Hashed: " + vBHashedPassword
									+ " VbPass: " + vBPassword);
							if (vBHashedPassword.equals(vBPassword)) {
								System.out.println("Passwords Matched!!!");
								vBAccount = true;
								// vBr.close();
								System.out
										.println("Checking for existing Server Side Account");
								boolean emuAccount = false;
								AccountData data = new AccountData();
								if (vBAccount) {
									// lets see if we have an account already
									// for this user
									emu = conn.createStatement();
									String emuquery = "Select * From `account` Where `station_id` = "
											+ vBUserID;
									if (emu.execute(emuquery)) {
										emur = emu.getResultSet();
										while (emur.next()) {
											long lCurrentTime = System
													.currentTimeMillis();
											data.setAccountID(emur
													.getLong("account_id"));
											data.setUsername(emur
													.getString("username"));
											data.setActiveAccount(true);
											data.setEmailAddress(vBEmail);
											data.setIsBanned(false);
											data.setIsGM(false);
											data.setIsDeveloper(false);
											System.out
													.println("Setting User Groups on Existing Account");
											int iUserGroup = -1;
											for (int i = 0; i < sUserGroups.length; i++) {
												if (sUserGroups[i] != null
														&& !sUserGroups[i]
																.isEmpty()) {
													iUserGroup = Integer
															.parseInt(sUserGroups[i]);
												}
												if (iUserGroup == integrationData
														.getDevcsrgroupid()) {
													data.setIsBanned(true);
													System.out
															.println("User Was Banned");
												}
												if (iUserGroup == integrationData
														.getCsrusergroupid()) {
													data.setIsGM(true);
													System.out
															.println("User Was GM");
												}
												if (iUserGroup == integrationData
														.getDevusergroupid()) {
													data.setIsDeveloper(true);
													System.out
															.println("User Was Dev");
												}
												iUserGroup = -1;
											}
											data.setJoinTimestamp(lCurrentTime);
											data.setLastActive(lCurrentTime);
											if (bEncryptPasswords) {
												data.setPassword(PacketUtils.encryptPassword(sPassword));
											} else {
												data.setPassword(sPassword);
											}
											data.setStationID(vBUserID);
											data.setJediBitMask(0);
											emuAccount = true;
										}
										emur.close();
										if (emuAccount) {
											valueToReturn = (int) data.getAccountID();
										} else {
											// System.out.println("Creating Local Account");
											s = conn
													.createStatement();
											r = null;
											int iNewAccountID = 0;
											String query = "Select max(account_id) from `account`";
											if (s.execute(query)) {
												r = s.getResultSet();
												if (r.next()) {
													iNewAccountID = r.getInt(1) + 1;
												} else {
													iNewAccountID = 1;
												}
												r.close();
											}
											if (iNewAccountID != 0) {
												StringBuffer buff = new StringBuffer();
												long lCurrentTime = System
														.currentTimeMillis();
												int iStationID = vBUserID;

												data.setIsBanned(false);
												data.setIsGM(false);
												data.setIsDeveloper(false);
												System.out
														.println("Setting USer Groups on New Account Count: "
																+ sUserGroups.length);
												int iUserGroup = -1;
												for (int i = 0; i < sUserGroups.length; i++) {
													if (sUserGroups[i] != null
															&& !sUserGroups[i]
																	.isEmpty()) {
														iUserGroup = Integer
																.parseInt(sUserGroups[i]);
													}
													if (iUserGroup == integrationData
															.getDevcsrgroupid()) {
														data.setIsBanned(true);
														System.out
																.println("User Was Banned");
													}
													if (iUserGroup == integrationData
															.getCsrusergroupid()) {
														data.setIsGM(true);
														System.out
																.println("User Was GM");
													}
													if (iUserGroup == integrationData
															.getDevusergroupid()) {
														data
																.setIsDeveloper(true);
														System.out
																.println("User Was Dev");
													}
													iUserGroup = -1;
												}
												System.out
														.println("User group set done.");
												if (bEncryptPasswords) {
													sPassword = PacketUtils.encryptPassword(sPassword);
												}
												buff
														.append(
																"Insert into `account` values (")
														.append(iNewAccountID)
														.append(", '")
														.append(sUsername)
														.append("', '")
														.append(sPassword)
														.append("',")
														.append(iStationID)
														.append(
																", "
																		+ data
																				.getIsGM()
																		+ ", "
																		+ data
																				.getIsBanned()
																		+ ", '")
														.append(
																new Timestamp(
																		lCurrentTime)
																		.toString())
														.append("', '")
														.append(
																new Timestamp(
																		lCurrentTime)
																		.toString())
														.append(
																"', 1, "
																		+ data
																				.getIsDeveloper()
																		+ ", 0);");

												query = buff.toString();
												s = conn.createStatement();
												s.execute(query);
												data
														.setAccountID(iNewAccountID);
												data.setActiveAccount(true);
												data.setEmailAddress(vBEmail);
												data
														.setJoinTimestamp(lCurrentTime);
												data
														.setLastActive(lCurrentTime);
												data.setPassword(sPassword);
												data.setStationID(iStationID);
												data.setUsername(sUsername);
												data.setJediBitMask(0);
												vAccounts.add(data);
												theGUI
														.getLoginServer()
														.addClientAccountFromDatabase(
																data);
											}

											lHeartbeatTimerMS = HEARTBEAT_DELAY_MS;
											// System.out.println("Local Account Created.");
											valueToReturn = iNewAccountID;
										}
									} else {
										valueToReturn = Constants.ACCOUNT_CREATION_DATABASE_ERROR_ON_CREATE;
									}

								} else {
									System.out
											.print("User Did not have a vBulletin Account");
									valueToReturn = Constants.ACCOUNT_CREATION_NO_VBULLETIN_ACCOUNT_FOUND;
								}

							} else {
								
								DataLog.logEntry("Password Mismatch to vBAccount", "DatabaseInterface", Constants.LOG_SEVERITY_MINOR, true, true);
								DataLog.logEntry("Account: " + sUsername, "DatabaseInterface", Constants.LOG_SEVERITY_MINOR, true, true);
								DataLog.logEntry("Hashed: " + vBHashedPassword + " VbPass: " 	+ vBPassword, "DatabaseInterface", Constants.LOG_SEVERITY_MINOR, true, true);
								valueToReturn = Constants.ACCOUNT_CREATION_VBULLETIN_PASSWORD_MISMATCH;
							}
						} else {
							System.out.println("ret2");
							valueToReturn = Constants.ACCOUNT_CREATION_DATABASE_ERROR_ON_CREATE;
						}
					}
					if (valueToReturn <= 0) {
						System.out.println("User Name not found or End of Resultset reached while looking for user name on vBulletin DB.");
					}
				} else {
					System.out.println("ret3");
					valueToReturn = Constants.ACCOUNT_CREATION_DATABASE_ERROR_ON_CREATE;
				}

			} catch (Exception e) {
				System.out
						.println("Error while looking for account on vBulletin Database");
				valueToReturn = Constants.ACCOUNT_CREATION_DATABASE_ERROR_ON_CREATE;
			}

			finally {
				try {
					//Statement vB = null;
					//Statement vB1 = null;
					//Statement emu = null;
					//ResultSet vBr = null;
					//ResultSet emur = null;
					if (emur != null) {
						emur.close();
						emur = null;
					}
					if (vBr != null) {
						vBr.close();
						vBr = null;
					}
					if (emu != null) {
						emu.close();
						emu = null;
					}
					if (vB1 != null) {
						vB1.close();
						vB1 = null;
					}
					if (vB != null) {
						vB.close();
						vB = null;
					}
				} catch (Exception e) {
					// Oh well
				}
			}
		}
		// --------------------
		return valueToReturn;
	}

	/**
	 * Load all Players and their associated items for a given ZoneServer
	 * 
	 * @param server
	 *            -- The ZoneServer being loaded.
	 * @return -- The list of Players sorted by Object ID.
	 */
	protected ConcurrentHashMap<Long, Player> loadPlayers(ZoneServer server) {
		Statement s = null;
		ResultSet result = null;
		ConcurrentHashMap<Long, Player> vPlayersThisServer = new ConcurrentHashMap<Long, Player>();
		try {
			Vector<String> vCharacterNames = new Vector<String>();
			String query = "Select * from `character` where `server_id` = "
					+ server.getServerID() + ";";
			s = conn.createStatement();
			if (s.execute(query)) {
				result = s.getResultSet();
				while (result.next()) {
					Inflater inflate = new Inflater();
					InflaterInputStream in = new InflaterInputStream(result
							.getBinaryStream(4), inflate);
					ByteArrayOutputStream bOut = new ByteArrayOutputStream();
					DataOutputStream dataOut = new DataOutputStream(bOut);
					int dataRead = 0;
					do {
						dataRead = in.read();
						dataOut.writeByte(dataRead);
					} while (dataRead != -1);
					// in.read(uncompressedPlayer, 0,
					// uncompressedPlayer.length);
					byte[] uncompressedPlayer = bOut.toByteArray();
					byte[] uncompressedPlayerCopy = Arrays.copyOfRange(
							uncompressedPlayer, 0,
							uncompressedPlayer.length - 1);
					// long lCharacterID = result.getLong(1);
					ObjectInputStream oIn = new ObjectInputStream(
							new ByteArrayInputStream(uncompressedPlayerCopy));
					int savedAccountID = result.getInt(2);
					Player player = (Player) oIn.readObject();
					player.setIsDeleted(result.getBoolean("delete"));
					// player.setX(0);
					// player.setY(0);
					// player.setZ(0);
					// player.setPlanetID(Constants.CORELLIA);
					// player.setPlanetID(Constants.TATOOINE);
					// player.setX(3523.0f);
					// player.setY(-4800.0f);
					// player.setZ(0);

					// player.getPlayData().addBitToPVPStatus(Constants.PVP_STATUS_IS_PLAYER);

					player.fixPlayerCluster(server.getClusterName());
					player.setOnlineStatus(false);
					vCharacterNames.add(player.getFirstName().toLowerCase());

					if (player.getAccountID() != savedAccountID) {
						player.setAccountID(savedAccountID);
					}
					player.setMaxVelocity(Constants.MAX_PLAYER_RUN_SPEED_METERS_SEC);
					Vector<TangibleItem> vAllPlayerItems = player
							.getInventoryItems();
					for (int i = 0; i < vAllPlayerItems.size(); i++) {
						TangibleItem item = vAllPlayerItems.elementAt(i);
						server.addObjectToAllObjects(item, false, false);

						/*
						 * if (item.getTemplateID() == 8926) { Vector<Attribute>
						 * itemAttributes = item.getAttributeList(null); for
						 * (int j = 0; j < itemAttributes.size(); j++) {
						 * Attribute theAttribute = itemAttributes.elementAt(j);
						 * 
						 * } //item.addAttribute(new
						 * Attribute("craft_tool_status",
						 * "@crafting:tool_status_ready")); }
						 */
						if (!player.getInventory().getLinkedObjects().contains(
								item)) {
							player.getInventory().addLinkedObject(item);
						}
					}
					Vector<Waypoint> vPlayerWaypoints = player.getPlayData()
							.getWaypoints();
					for (int i = 0; i < vPlayerWaypoints.size(); i++) {
						Waypoint w = vPlayerWaypoints.elementAt(i);
						server.addObjectToAllObjects(w, false, false);
					}
					Vector<IntangibleObject> vAllDatapadObjects = player
							.getDatapad().getIntangibleObjects();
					for (int i = 0; i < vAllDatapadObjects.size(); i++) {
						server.addObjectToAllObjects(vAllDatapadObjects
								.elementAt(i), false, false);
					}
					server.addObjectToAllObjects(player.getBank(), false,false);
					server.addObjectToAllObjects(player.getDatapad(), false,false);
					TangibleItem hair = player.getHair();
					if (hair != null) {
						server.addObjectToAllObjects(player.getHair(), false,false);
					}
					server.addObjectToAllObjects(player.getInventory(), false,false);
					long objectID = player.getID();
					vPlayersThisServer.put(objectID, player);
					// player.populateSkillList();
					// updatePlayer(player, false,false);

					TangibleItem DataPad = player.getDatapad();
					Vector<IntangibleObject> vItnos = DataPad.getIntangibleObjects();
					for (int i = 0; i < vItnos.size(); i++) {
						IntangibleObject itno = vItnos.get(i);
						SOEObject ac = itno.getAssociatedCreature();
						if (ac != null) {
							server.addObjectToAllObjects(ac, false, false);
						}
					}
				}
			}
			// vAllUsedCharacterNames.put(server.getServerID(),
			// vCharacterNames);

		} catch (Exception e) {
			System.out.println("Error loading players for server ID "
					+ server.getServerID() + ": " + e.toString());
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (result != null) {
					result.close();
					result = null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception e) {
				// Oh well!
			}
		}
		return vPlayersThisServer;
	}

	protected Player loadPlayer(long playerID, int serverID) {
		Player player = null;
		String query = "Select * from `character` where `character_id` = "
				+ playerID + " and `server_id` = " + serverID + ";";
		// System.out.println(query);
		Statement s = null;
		ResultSet result = null;
		try {
			s = conn.createStatement();
			if (s.execute(query)) {
				// System.out.println("Statement executed.");
				result = s.getResultSet();
				if (result.next()) {
					// System.out.println("Got a result!");
					Inflater inflate = new Inflater();
					InflaterInputStream in = new InflaterInputStream(result.getBinaryStream(4), inflate);
					ByteArrayOutputStream bOut = new ByteArrayOutputStream();
					DataOutputStream dataOut = new DataOutputStream(bOut);
					int dataRead = 0;
					do {
						dataRead = in.read();
						dataOut.writeByte(dataRead);
					} while (dataRead != -1);
					// in.read(uncompressedPlayer, 0,
					// uncompressedPlayer.length);
					byte[] uncompressedPlayer = bOut.toByteArray();
					byte[] uncompressedPlayerCopy = Arrays.copyOfRange(
							uncompressedPlayer, 0,
							uncompressedPlayer.length - 1);
					ObjectInputStream oIn = new ObjectInputStream(
							new ByteArrayInputStream(uncompressedPlayerCopy));
					try {
						int savedAccountID = result.getInt(2);
						player = (Player) oIn.readObject();
						player.setAccountID(savedAccountID);
						player.setIsDeleted(result.getBoolean("delete"));
						oIn.close();
					} catch (Exception e) {
						System.out.println("Unable to read Player object: "
								+ e.toString());
						e.printStackTrace();
					}
				}
				result.close();
			}
			s.close();
		} catch (Exception e) {
			System.out.println("Error in loadPlayers: " + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (result != null) {
					result.close();
					result = null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception e) {
				// Oh well!
			}
		}
		return player;
	}

	/**
	 * Load ALL players for reference by the Login Server.
	 * 
	 * @return -- The list of all Players.
	 */
	protected Vector<Player> loadPlayers() {
		/*
		 * This function loads all characters for a player so we can present
		 * them to the client ui in the character list.
		 */
		if (vAllPlayers != null) {
			return vAllPlayers;
		}
		vAllPlayers = new Vector<Player>();
		Statement s = null;
		ResultSet result = null;
		try {
			String query = "Select * from `character`;";
			s = conn.createStatement();
			if (s.execute(query)) {
				result = s.getResultSet();
				while (result.next()) {
					Inflater inflate = new Inflater();
					InflaterInputStream in = new InflaterInputStream(result
							.getBinaryStream(4), inflate);
					ByteArrayOutputStream bOut = new ByteArrayOutputStream();
					DataOutputStream dataOut = new DataOutputStream(bOut);
					int dataRead = 0;
					do {
						dataRead = in.read();
						dataOut.writeByte(dataRead);
					} while (dataRead != -1);
					// in.read(uncompressedPlayer, 0,
					// uncompressedPlayer.length);
					byte[] uncompressedPlayer = bOut.toByteArray();
					byte[] uncompressedPlayerCopy = Arrays.copyOfRange(
							uncompressedPlayer, 0,
							uncompressedPlayer.length - 1);
					ObjectInputStream oIn = new ObjectInputStream(
							new ByteArrayInputStream(uncompressedPlayerCopy));
					try {
						int savedAccountID = result.getInt(2);
						Player player = (Player) oIn.readObject();
						if (player.getAccountID() != savedAccountID) {
							player.setAccountID(savedAccountID);

						}
						player.setIsDeleted(result.getBoolean("delete"));
						oIn.close();
						vAllPlayers.add(player);
						// player.updateExperience(getSkillList().get(85), 35,
						// 990);
						// player.populateSkillList();
						/*
						 * Vector<SkillMods> vSkillMods =
						 * player.getSkillModsList(); boolean bFound = false;
						 * for (int i = 0; i < vSkillMods.size(); i++) {
						 * SkillMods mod = vSkillMods.elementAt(i); if
						 * (mod.sName.equals("surveying")) { if (!bFound) {
						 * bFound = true; mod.iSkillModModdedValue = 35; } else
						 * { vSkillMods.remove(i); } } }
						 */

					} catch (Exception e) {
						System.out.println("Unable to read Player object: "
								+ e.toString());
						e.printStackTrace();
					}
				}
				result.close();
			}
			s.close();
			return vAllPlayers;
		} catch (Exception e) {
			System.out.println("Error in loadPlayers: " + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (result != null) {
					result.close();
					result = null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception e) {
				// Oh well!
			}
		}
		return vAllPlayers;
	}

	/**
	 * Loads the Experience strings from the database.
	 */
	private static void loadExperienceList() {
		vExperienceTypes = new Hashtable<Integer, Experience>();
		Statement s = null;
		ResultSet result = null;
		try {
			String query = "Select * from `xp`";
			s = conn.createStatement();
			
			if (s.execute(query)) {
				result = s.getResultSet();
				while (result.next()) {
					Experience e = new Experience();
					e.sExperienceName = result.getString(2);
					e.iExperienceID = result.getInt(1);
					vExperienceTypes.put(e.iExperienceID, e);
				}
			}
		} catch (Exception e) {
			System.out.println("Error loading experience list." + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (result != null) {
					result.close();
					result =null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception e) {
				// Oh well!
			}
		}
	}

	/**
	 * Gets the Skill List.
	 * 
	 * @return -- The Skill List.
	 */
	protected static Hashtable<Integer, Skills> getSkillList() {
		if (vSkillsListByIndex == null) {
			loadSkillList();
		}
		return vSkillsListByIndex;
	}

	protected static Skills getSkillByID(int id) {
		return vSkillsListByIndex.get(id);
	}

	protected static Skills getSkillByCommandCRC(int crc) {
		return vSkillsListByCommandCRC.get(crc);
	}

	/**
	 * Loads the list of skills from the database. This includes the skill ID,
	 * name, cost, skill points required, experience quantity and type required,
	 * etc. etc.
	 */
	private static void loadSkillList() {
		vSkillsListByIndex = new Hashtable<Integer, Skills>();
		vSkillsListByCommandCRC = new Hashtable<Integer, Skills>();
		Statement s = null;
		ResultSet result = null;
		try {
			String query = "SELECT skill_id, skill_name, skill_money_required, skill_points_required, skill_xp_type, skill_xp_cost, skill_xp_cap, skill_species_required, skill_mods, skill_certifications, skill_is_profession, skill_god_only from `skills`;";
			s = conn.createStatement();
			if (s.execute(query)) {
				result = s.getResultSet();
				while (result.next()) {

					Skills aSkill = new Skills();
					int skillID = result.getInt(1);
					aSkill.setSkillID(skillID);
					String sName = result.getString(2);
					System.out.println("Load skill " + sName + " at index "
							+ skillID);
					aSkill.setName(sName);
					// String[] sRequiredSkills =
					// result.getString(2).split(",");
					aSkill.setCreditsCost(result.getInt(3));
					aSkill.setPointsCost(result.getInt(4));
					String sXPType = result.getString(5);
					aSkill.setExperienceType(getExperienceIDFromName(sXPType));
					aSkill.setExperienceCost(result.getInt(6));
					aSkill.setExperienceCap(result.getInt(7));
					// Any skill with only 1 "_" is a root skill, and it's
					// novice skill is 1 greater than itself.
					// If the skill name contains noviceSkill, then it is itself
					// a novice skill.
					// In any other case, the "novice" skill ID is less than the
					// current skill ID.
					int iNumberUnderscores = 0;
					int iCurrentIndex = -1;
					do {
						iCurrentIndex = sName.indexOf("_", iCurrentIndex + 1);
						if (iCurrentIndex > -1) {
							iNumberUnderscores++;
						}
						// System.out.println("Found an underscore.");
					} while (iCurrentIndex > -1);
					if (iNumberUnderscores == 1) {
						System.out.println("Root skill " + sName);
						aSkill.setNoviceSkillID(skillID + 1);
					} else if (iNumberUnderscores == 2) {
						if (sName.contains("master")) {
							System.out
									.println("Master skill found: Novice skill index is "
											+ (skillID - 1));
							aSkill.setNoviceSkillID(skillID - 1);
						} else {
							System.out
									.println("This is a Novice skill -- Novice skill index = current index: "
											+ skillID);
							aSkill.setNoviceSkillID(skillID);
						}
					} else {
						boolean bFound = false;
						for (int i = skillID - 1; i >= 0 && !bFound; i--) {
							Skills noviceSkill = vSkillsListByIndex.get(i);
							if (noviceSkill != null) {
								String sNoviceName = noviceSkill.getName();
								if (sNoviceName != null) {
									if (sNoviceName.contains("novice")) {
										System.out.println("Skill " + sName
												+ " found novice skill at ID "
												+ i);
										aSkill.setNoviceSkillID(i);
										bFound = true;
									}
								}
							}
						}
						if (!bFound) {
							System.out
									.println("No novice skill found for skill name "
											+ sName);
						}
					}

					// System.out.println("Skill " + aSkill.getName() + " caps "
					// + sXPType + " experience at " +
					// aSkill.getExperienceCap());
					int[] iSpeciesRequirementsForRace = new int[Constants.SpeciesNames.length];
					int iNumSpeciesSet = 0;
					String sRace = result.getString(8);
					if (sRace != null && !sRace.equals(" ")) {
						String[] sRaceNames = sRace.split(",");
						if (sRaceNames != null) {
							// System.out.println("Species requirements for " +
							// aSkill.getName() + " follow...");
							for (int i = 0; i < Constants.SpeciesNames.length; i++) {
								for (int j = 0; j < sRaceNames.length; j++) {
									if (Constants.SpeciesNames[i]
											.equalsIgnoreCase(sRaceNames[j])) {
										iSpeciesRequirementsForRace[iNumSpeciesSet] = i;
										iNumSpeciesSet++;
										// System.out.println(Constants.SpeciesNames[i]);
									}
								}
							}
						}
						iSpeciesRequirementsForRace = Arrays.copyOfRange(
								iSpeciesRequirementsForRace, 0, iNumSpeciesSet);
					}
					String sUnparsedSkillMods = result.getString(9);
					if (sUnparsedSkillMods != null
							&& sUnparsedSkillMods.equals(" ") == false) {
						String[] sSplitSkillMods = sUnparsedSkillMods
								.split(",");
						for (int i = 0; i < sSplitSkillMods.length; i++) {
							SkillMods mod = new SkillMods();
							String[] sParsedSkillMod = sSplitSkillMods[i]
									.split("=");
							mod.setName(sParsedSkillMod[0]);
							mod.setSkillModModdedValue(Integer
									.parseInt(sParsedSkillMod[1]));
							mod.setSkillModStartingValue(0);
							// mod.bUnknownValue = false;
							// System.out.println("Loaded skill mod " +
							// mod.sName + " with value increase of " +
							// mod.iSkillModModdedValue + " for skill " +
							// aSkill.getName());
							aSkill.addSkillMod(mod);
						}
					}
					String sUnparsedSkillCommands = result.getString(10);
					if (!(sUnparsedSkillCommands == null || sUnparsedSkillCommands
							.equals(" "))) {
						String[] sSplitSkillCommands = sUnparsedSkillCommands
								.split(",");
						for (int i = 0; i < sSplitSkillCommands.length; i++) {
							// System.out.println("Skill " + aSkill.getName() +
							// " adding certification " +
							// sSplitSkillCommands[i]);
							aSkill.addCertification(sSplitSkillCommands[i]);
							String thisCert = sSplitSkillCommands[i];
							if (!(thisCert.startsWith("private") || thisCert
									.startsWith("cert"))) {
								int iCommandCRC = PacketUtils.SWGCrc(thisCert
										.toLowerCase());
								// System.out.println("Calculated CRC of 0x"+Integer.toHexString(iCommandCRC)
								// + " for skill certification name " +
								// thisCert.toLowerCase());
								// System.out.println("protected final static int "
								// + thisCert + " = 0x" +
								// Integer.toHexString(iCommandCRC) + ";");
								Skills oldSkill = vSkillsListByCommandCRC
										.get(iCommandCRC);
								if (aSkill.getName().startsWith("jedi") == false
										&& oldSkill == null) {
									vSkillsListByCommandCRC.put(iCommandCRC,
											aSkill);
								}
							}
						}
					}
					aSkill.setIsProfessionSkill(result.getBoolean(11));
					aSkill.setSpeciesSpecific(iSpeciesRequirementsForRace);
					aSkill.setIsGodModeSkill(result.getBoolean(12));
					vSkillsListByIndex.put(aSkill.getSkillID(), aSkill);
				}
				result.close();
				result = null;
				s.close();
				s = conn.createStatement();
				query = "Select skill_id, skill_requirements from `skills`";
				if (s.execute(query)) {
					result = s.getResultSet();
					while (result.next()) {
						int id = result.getInt(1); // This skill ID has these
						// requirements.
						Skills aSkill = vSkillsListByIndex.get(id); // Here's
						// the
						// skill.
						String[] sRequiredSkills = result.getString(2).split(
								","); // Here's the PARSED requirements.
						// aSkill .setRequisiteSkillString(result.getString(2));
						// System.out.println("Skill adding requirements: " +
						// aSkill.getName());
						for (int i = 0; i < sRequiredSkills.length; i++) {
							Iterator<Skills> itr = vSkillsListByIndex.values()
									.iterator();
							while (itr.hasNext()) {
								Skills comparatorSkill = itr.next();
								if (comparatorSkill.getName().equals(
										sRequiredSkills[i])) {
									aSkill.addRequiredSkillID(comparatorSkill
											.getSkillID());
								}
							}
						}
					}
				}
				result.close();
				s.close();
				result = null;
				s = null;
				Runtime.getRuntime().gc();
			}
		} catch (Exception e) {
			System.out.println("Error loading skills list: " + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (result != null) {
					result.close();
					result = null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (Exception e) {
				// Oh well!
			}
		}
	}

	/**
	 * Returns the name of the Experience type associated with the given
	 * Experience ID.
	 * 
	 * @param id
	 *            -- The Experience ID.
	 * @return -- The Experience Name.
	 */
	protected static String getExperienceNameFromID(int id) {
		if (vExperienceTypes == null) {
			return null;
		}
		Experience e = vExperienceTypes.get(id);
		if (e != null) {
			return e.sExperienceName;
		}
		return null;
	}

	/**
	 * Checks to see if the given Experience Name is valid.
	 * 
	 * @param sName
	 *            -- The Experience Name being checked.
	 * @return -- If the Experience Name is valid.
	 */
	protected static boolean isExperienceTypeValid(String sName) {
		Enumeration<Experience> vExperienceIterator = vExperienceTypes
				.elements();
		while (vExperienceIterator.hasMoreElements()) {
			Experience ex = vExperienceIterator.nextElement();
			if (ex.sExperienceName.equalsIgnoreCase(sName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the ID of the experience type, based on the experience name.
	 * 
	 * @param sName
	 *            -- The name of the experience type.
	 * @return -- The ID of the experience type.
	 */
	protected static int getExperienceIDFromName(String sName) {
		Enumeration<Integer> vExperienceKeys = vExperienceTypes.keys();
		while (vExperienceKeys.hasMoreElements()) {
			int id = vExperienceKeys.nextElement();
			Experience exp = vExperienceTypes.get(id);
			if (exp.sExperienceName.equals(sName)) {
				return id;
			}
		}
		return -1;
	}

	/**
	 * Loads the list of known Zone Servers from the database.
	 */
	private static void loadZoneServerData() {
		vServerInfoContainer = new Vector<DatabaseServerInfoContainer>();
		Statement s = null;
		ResultSet r = null;
		PreparedStatement d = null;
		String query = "Select * from galaxy;";
		try {
			s = conn.createStatement();
			if (s.execute(query)) {
				r = s.getResultSet();
				while (r.next()) {
					DatabaseServerInfoContainer container = new DatabaseServerInfoContainer();
					container.iServerID = r.getInt(1);
					container.sServerName = r.getString(2);
					container.sRemoteAddress = r.getString(3);
					container.sLocalAddress = r.getString(4);
					container.iZonePort = r.getInt(5);
					container.iCurrentPopulation = r.getInt(6);
					container.iMaxPopulation = r.getInt(7);
					container.iMaxCharactersPerAccount = r.getInt(8);
					container.iServerStatus = r.getByte(9);
					container.iPingPort = r.getInt(10);
					container.iTimeOffset = r.getInt(11);
					container.sMotd = r.getString(13);
					container.bDevOnlyServer = r.getBoolean(14);
					vServerInfoContainer.add(container);
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;

			if (vServerInfoContainer.isEmpty()) {
				DataLog
						.logEntry(
								"There were no galaxies defined. A default one is being defined using default values. Please edit the information afterwards and restart the server.",
								"DatabaseInterface",
								Constants.LOG_SEVERITY_CRITICAL, true, true);
				query = "Insert Into `galaxy` (`name`,`ip_wan`,`ip_lan`,`zoneport`,`population`,`max_population`,`max_characters_per_server`,`Status`,`pingport`,`gmt_time_offset`,`max_characters_per_account`, `motd`) Values ('Shards of the Force SWG Galaxy','127.0.0.1','127.0.0.1','44463','0','3000','3000','2','44462','-5','8','Welcome to the Shards of the Force SWG Server')";
				d = conn.prepareStatement(query);
				if (!d.execute()) {
					d.close();
					query = "Select * from galaxy;";
					try {
						s = conn.createStatement();
						if (s.execute(query)) {
							r = s.getResultSet();
							while (r.next()) {
								DatabaseServerInfoContainer container = new DatabaseServerInfoContainer();
								container.iServerID = r.getInt(1);
								container.sServerName = r.getString(2);
								container.sRemoteAddress = r.getString(3);
								container.sLocalAddress = r.getString(4);
								container.iZonePort = r.getInt(5);
								container.iCurrentPopulation = r.getInt(6);
								container.iMaxPopulation = r.getInt(7);
								container.iMaxCharactersPerAccount = r
										.getInt(8);
								container.iServerStatus = r.getByte(9);
								container.iPingPort = r.getInt(10);
								container.iTimeOffset = r.getInt(11);
								container.sMotd = r.getString(13);
								vServerInfoContainer.add(container);
							}
							r.close();
							r = null;
							if (vServerInfoContainer.isEmpty()) {
								DataLog
										.logEntry(
												"SHUTDOWN: MAJOR ERROR NO GALAXIES LOADED AFTER DEFAULT INSERTION CONTACT TECH SUPPORT.",
												"DatabaseInterface",
												Constants.LOG_SEVERITY_MAJOR,
												true, true);
								System.exit(-1);
							} else {
								DataLog
										.logEntry(
												"Default Galaxy Load Successful!",
												"DatabaseInterface",
												Constants.LOG_SEVERITY_INFO,
												true, true);
							}
						}
						s.close();
						s = null;
					} catch (SQLException e) {
						System.out.println("Error loading server data: "
								+ e.toString());
						e.printStackTrace();
					}
				} else {
					DataLog.logEntry("Insert Default Galaxy Error in query.",
							"DatabaseInterface", Constants.LOG_SEVERITY_INFO,
							true, true);
				}
			}
		} catch (SQLException e) {
			System.out.println("Error loading server data: " + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (r != null) {
					r.close();
					r = null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
				if (d != null) {
					d.close();
					d = null;
				}
			} catch (Exception e) {
				// Oh well!
			}
		}
	}

	/**
	 * Get the list of Zone Servers.
	 * 
	 * @return -- The list of Zone Servers.
	 */
	public static Vector<DatabaseServerInfoContainer> getZoneServers(
			boolean bIsDeveloper) {
		if (vServerInfoContainer == null) {
			loadZoneServerData();
		}
		if (bIsDeveloper) {
			return vServerInfoContainer;
		} else {
			Vector<DatabaseServerInfoContainer> vContainers = new Vector<DatabaseServerInfoContainer>();
			for (int i = 0; i < vServerInfoContainer.size(); i++) {
				DatabaseServerInfoContainer container = vServerInfoContainer
						.elementAt(i);
				if (!container.bDevOnlyServer) {
					vContainers.add(container);
				}
			}
			return vContainers;
		}
	}

	/**
	 * Return the Specified Zone Server Information by ID
	 * 
	 * @param iServerID
	 * @return
	 */
	public DatabaseServerInfoContainer getZoneServerData(int iServerID) {

		for (int i = 0; i < vServerInfoContainer.size(); i++) {
			DatabaseServerInfoContainer container = vServerInfoContainer
					.elementAt(i);
			if (container.iServerID == iServerID) {
				return container;
			}
		}
		return null;
	}

	/**
	 * Called by the Zone Server during character creation, this saves a new
	 * player to the database.
	 * 
	 * @param p
	 *            -- The player to be saved.
	 */
	public void saveNewPlayer(Player p) {
		String statement = null;
		PreparedStatement s = null;
		try {
			p.addBitToPVPStatus(Constants.PVP_STATUS_IS_PLAYER);
			p.addFactionToFactionList("Imperial");
			p.addFactionToFactionList("Rebel");
			p.setFactionID(Constants.FACTION_NEUTRAL);
			p.setFactionRank((byte) 1);
			// Save the player.
			// createPlayerItem(p);
			createPlayerInventory(p);
			// createPlayerHair(p);
			createPlayerDatapad(p);
			createPlayerBank(p);
			createPlayerMissionBag(p);
			createPlayerMissionSet(p);
			createPlayerUnarmedWeapon(p);
			insertStarterItems(p);

			Deed d = new Deed(137, getDeedTemplateByID(137)
					.getObject_template_id(), p.getServer());
			d.setOwner(p);
			d.setEquipped(p.getInventory(), -1);
			p.addItemToInventory(d);
			p.getServer().addObjectToAllObjects(d, false, false);

			d = new Deed(124, getDeedTemplateByID(124).getObject_template_id(),
					p.getServer());
			d.setOwner(p);
			d.setEquipped(p.getInventory(), -1);
			p.addItemToInventory(d);
			p.getServer().addObjectToAllObjects(d, false, false);

			if (p.hasSkill(31)) {
				d = new Deed(144, getDeedTemplateByID(144)
						.getObject_template_id(), p.getServer());
				d.setOwner(p);
				Vector<TangibleItem> vPI = p.getInventory().getLinkedObjects();
				for (int i = 0; i < vPI.size(); i++) {
					TangibleItem t = vPI.get(i);
					if (t.getTemplateID() == d.getTemplateID()) {
						p.getInventory().removeLinkedObject(t);
						p.removeItemFromInventory(t);
						p.getServer().removeObjectFromAllObjects(t, false);
						break;
					}
				}
				d.setEquipped(p.getInventory(),
						Constants.EQUIPPED_STATE_UNEQUIPPED);
				p.addItemToInventory(d);
				p.getServer().addObjectToAllObjects(d, false, false);
			}

			// Temporary hard code.
			statement = "Insert into `character` VALUES (?, ?, ?, ?, ?, ?);";
			s = conn.prepareStatement(statement);
			s.setLong(1, p.getID());
			s.setLong(2, p.getClient().getAccountID());
			s.setInt(3, p.getServerID());
			s.setBoolean(5, false);
			s.setLong(6, 0);
			// Serialize the player.
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(bOut);
			oOut.writeObject(p);
			oOut.flush();
			byte[] serializedPlayer = bOut.toByteArray();
			// Temp experimental code. Where does it go wrong?
			bOut = new ByteArrayOutputStream();
			Deflater deflate = new Deflater();
			deflate.setLevel(Deflater.DEFAULT_COMPRESSION);
			deflate.setStrategy(Deflater.DEFAULT_STRATEGY);
			DeflaterOutputStream out = new DeflaterOutputStream(bOut, deflate);
			out.write(serializedPlayer);
			out.finish();
			out.flush();
			byte[] compressedPlayer = bOut.toByteArray();
			s.setBinaryStream(4, new ByteArrayInputStream(compressedPlayer));
			s.execute();
			oOut.close();
			bOut = null;
			oOut = null;
			s.close();
			// vAllPlayers is null if there is no login server.
			this.updatePlayer(p, true, false);
			if (vAllPlayers != null) {
				vAllPlayers.add(p);
			}
			LoginServer server = theGUI.getLoginServer();
			if (server != null) {
				server.addClientCharacterFromDatabase(p);
			}
			theGUI.getZoneServer().addNewPlayer(p);

		} catch (Throwable t) {
			System.out
					.println("Error occured in DatabaseInterface::saveNewPlayer: "
							+ t.toString());
			t.printStackTrace();
		}
		lHeartbeatTimerMS = HEARTBEAT_DELAY_MS;
	}

	/**
	 * Loads the list of player HAM values, indexed by Race, and by Starting
	 * Profession.
	 * 
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public int[][][] loadDefaultHams() {
		try {
			int[][][] defaultHams = new int[Constants.NUM_RACES][Constants.NUM_PROFESSIONS][Constants.NUM_PLAYER_HAMS];
			String query = "Select * from starting_ham;";
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					int iSpeciesID = r.getInt(1);
					String sProfessionID = r.getString(2);
					int iProfessionID = Constants
							.getStartingProfessionID(sProfessionID);
					if (iProfessionID > -1) {
						defaultHams[iSpeciesID][iProfessionID][0] = r.getInt(3);
						defaultHams[iSpeciesID][iProfessionID][1] = r.getInt(4);
						defaultHams[iSpeciesID][iProfessionID][2] = r.getInt(5);
						defaultHams[iSpeciesID][iProfessionID][3] = r.getInt(6);
						defaultHams[iSpeciesID][iProfessionID][4] = r.getInt(7);
						defaultHams[iSpeciesID][iProfessionID][5] = r.getInt(8);
						defaultHams[iSpeciesID][iProfessionID][6] = r.getInt(9);
						defaultHams[iSpeciesID][iProfessionID][7] = r
								.getInt(10);
						defaultHams[iSpeciesID][iProfessionID][8] = r
								.getInt(11);
					}
				}
				r.close();
				s.close();
				return defaultHams;
			}
		} catch (SQLException e) {
			System.out.println("SQL Exception caught loading starting hams: "
					+ e.toString());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates the Inventory object for the given Player.
	 * 
	 * @param p
	 *            -- The player being created.
	 */
	private void createPlayerInventory(Player p) {
		TangibleItem t = new TangibleItem();
		ItemTemplate template = getTemplateDataByFilename(Constants.STF_CRC_NAMES[Constants.STF_INVENTORY]);
		// t.setSTFFileName(template.getSTFFileName());
		t.setTemplateID(template.getTemplateID());
		t.setIFFFileName(template.getIFFFileName());
		// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
		// t.setSTFDetailName(template.getSTFDetailName());
		// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
		// t.setUnknownString1("");
		// t.setUnknownString2("");
		try {
			t.setName("INVENTORY", false);
			t.setCustomizationData(null);
			// long playerID = p.getID();
			t.setOwner(p);
			t.setEquipped(p, 4);
			t.setID(p.getServer().getNextObjectID());
			t.setConditionDamage(0, false);
			t.setMaxCondition(1, false);
		} catch (Exception e) {
			// D;oh!
		}

		p.getServer().addObjectToAllObjects(t, false, false);
		p.setInventoryItem(t);
		t.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
	}

	/**
	 * Creates the Datapad for the given player.
	 * 
	 * @param p
	 *            -- The player being created.
	 */
	private void createPlayerDatapad(Player p) {
		TangibleItem t = new TangibleItem();
		ItemTemplate template = getTemplateDataByFilename(Constants.STF_CRC_NAMES[Constants.STF_DATAPAD]);
		// t.setSTFFileName(template.getSTFFileName());
		// t.setIFFFileName(template.getIFFFileName());
		// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
		// t.setSTFDetailName(template.getSTFDetailName());
		// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
		t.setTemplateID(template.getTemplateID());
		try {
			t.setName("DATAPAD", false);

			t.setCustomizationData(null);
			// long playerID = p.getID();
			t.setOwner(p);
			t.setEquipped(p, 4);
			t.setID(p.getServer().getNextObjectID());
			t.setConditionDamage(0, false);
			t.setMaxCondition(1, false);
		} catch (IOException e) {

		}
		t.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
		p.getServer().addObjectToAllObjects(t, false, false);
		p.setDatapad(t);

		// Make a speederbike and add it to the datapad.
		/*
		 * IntangibleObject speederbike = new IntangibleObject(p.getFirstName()
		 * + "'s Speederbike Swoop",
		 * Constants.MonsterNames[Constants.MONSTER_NAME_SPEEDERBIKE_SWOOP]);
		 * ItemTemplate swoopTemplate =
		 * getTemplateDataByFilename(Constants.SPEEDER_SWOOP_IFF);
		 * speederbike.setTemplateID(swoopTemplate.getTemplateID());
		 * speederbike.setID(p.getServer().getNextObjectID());
		 * p.getServer().addObjectToAllObjects(speederbike, false,false);
		 * speederbike.setCRC(Constants.SPEEDER_SWOOP_CRC);
		 * speederbike.setIFFFileName(Constants.SPEEDER_SWOOP_IFF);
		 * speederbike.setCustomizationData(null);
		 * //speederbike.setSTFFileName(swoopTemplate.getSTFFileName());
		 * //speederbike
		 * .setSTFFileIdentifier(swoopTemplate.getSTFFileIdentifier());
		 * //speederbike.setSTFDetailName(swoopTemplate.getSTFDetailName());
		 * //speederbike
		 * .setSTFDetailIdentifier(swoopTemplate.getSTFDetailIdentifier());
		 * //speederbike.setSTFLookAtName(swoopTemplate.getSTFLookAtName());
		 * //speederbike
		 * .setSTFLookAtIdentifier(swoopTemplate.getSTFLookAtIdentifier());
		 * t.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
		 * t.addIntangibleObject(speederbike); speederbike.setContainer(t,
		 * false); int creatureSpeederbikeTemplate = 6222; swoopTemplate =
		 * getTemplateDataByID(creatureSpeederbikeTemplate); Vehicle theSwoop =
		 * new Vehicle(3000, speederbike); // This NPC constructor is for
		 * creating vehicles. theSwoop.setFirstName("Speederbike");
		 * theSwoop.setLastName("Swoop"); theSwoop.setCellID(0);
		 * theSwoop.setIsVehicle();
		 * theSwoop.setIFFFileName(swoopTemplate.getIFFFileName());
		 * //theSwoop.setSTFFileName(swoopTemplate.getSTFFileName());
		 * //theSwoop.
		 * setSTFFileIdentifier(swoopTemplate.getSTFFileIdentifier());
		 * //theSwoop.setSTFDetailName(swoopTemplate.getSTFDetailName());
		 * //theSwoop
		 * .setSTFDetailIdentifier(swoopTemplate.getSTFDetailIdentifier());
		 * //theSwoop.setSTFLookAtName(swoopTemplate.getSTFLookAtName());
		 * //theSwoop
		 * .setSTFLookAtIdentifier(swoopTemplate.getSTFLookAtIdentifier());
		 * theSwoop.setMasterID(p.getID()); //int crc =
		 * creatureTemplate.getCRC(); theSwoop.setServer(p.getServer());
		 * theSwoop.setCRC(swoopTemplate.getCRC()); theSwoop.setScale(1.0f);
		 * long swoopObjectID = p.getServer().getNextObjectID();
		 * theSwoop.setID(swoopObjectID);
		 * 
		 * theSwoop.setPVPBitmask(p.getPVPStatus());
		 * theSwoop.setFactionID(p.getFactionID());
		 * theSwoop.setStance(Constants.STANCE_STANDING, false);
		 * theSwoop.clearAllStates(false); theSwoop.setLinkID(0);
		 * theSwoop.setDamage(0); theSwoop.setHealth(3000); int[] hams = new
		 * int[1]; hams[0] = 3000; theSwoop.setHam(hams);
		 * theSwoop.setCREO3Bitmask(Constants.BITMASK_CREO3_VEHICLE);
		 * theSwoop.setMasterID(p.getID());
		 * speederbike.setAssociatedCreature(theSwoop);
		 * p.getServer().addObjectToAllObjects(theSwoop, false,false);
		 */
	}

	/**
	 * Creates the Bank object for the given player.
	 * 
	 * @param p
	 *            -- The player being created.
	 */
	private void createPlayerBank(Player p) {
		TangibleItem t = new TangibleItem();
		ItemTemplate template = getTemplateDataByFilename(Constants.STF_CRC_NAMES[Constants.STF_BANK]);
		// t.setSTFFileName(template.getSTFFileName());
		// t.setIFFFileName(template.getIFFFileName());
		// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
		// t.setSTFDetailName(template.getSTFDetailName());
		// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
		t.setTemplateID(template.getTemplateID());
		try {
			t.setName("BANK", false);
			t.setCustomizationData(null);
			// long playerID = p.getID();
			t.setOwner(p);
			t.setEquipped(p, 4);
			t.setConditionDamage(0, false);
			t.setMaxCondition(1, false);
		} catch (IOException e) {

		}
		t.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
		t.setID(p.getServer().getNextObjectID());
		p.getServer().addObjectToAllObjects(t, false, false);
		p.setBank(t);
	}

	/**
	 * Creates the Mission Bag for the given player. The mission bag holds all
	 * of the Missions the player is currently running.
	 * 
	 * @param p
	 *            -- The player being created.
	 */
	private void createPlayerMissionBag(Player p) {
		TangibleItem t = new TangibleItem();
		ItemTemplate template = getTemplateDataByFilename(Constants.STF_CRC_NAMES[Constants.STF_MISSION_BAG]);
		t.setCRC(template.getCRC());
		// t.setSTFFileName(template.getSTFFileName());
		// t.setIFFFileName(template.getIFFFileName());
		// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
		// t.setSTFDetailName(template.getSTFDetailName());
		// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
		try {
			t.setName("MISSION BAG", false);// t.setName("MissionBag"); //cannot
			// be set
			t.setCustomizationData(null);
			// long playerID = p.getID();
			t.setOwner(p);
			t.setEquipped(p, -1);
			t.setConditionDamage(0, false);
			t.setMaxCondition(1, false);
		} catch (IOException e) {

		}
		t.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
		t.setID(p.getServer().getNextObjectID());
		t.setBSendsEquipedState(false);
		p.getServer().addObjectToAllObjects(t, false, false);
		p.setMissionBag(t);

	}

	/**
	 * Creates the missions that go in the players mission bag. A player gets
	 * all his missions assigned upon creation.
	 * 
	 * @param p
	 */
	private void createPlayerMissionSet(Player p) {
		Vector<MissionObject> vML = new Vector<MissionObject>();
		ItemTemplate template = getTemplateDataByFilename(Constants.STF_CRC_NAMES[Constants.STF_MISSION_OBJECT]);
		String[] MissionGiver = new String[2];
		MissionGiver[0] = "";
		MissionGiver[1] = "";
		TangibleItem t = p.getMissionBag();
		int maxMissionCount = Constants.MAX_MISSION_BAG_ITEMS;

		for (int i = 0; i < maxMissionCount; i++) {
			MissionObject m = new MissionObject();
			m.setID(p.getServer().getNextObjectID());
			m.setCustomizationData(null);
			m.setCRC(template.getCRC());
			m.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);

			if (i == (maxMissionCount - 1)) {
				m.setLLastMissionID(p.getServer().getNextObjectID());
			}
			m.setSMissionSTFString("");
			m.setSMissionSTFDetailIdentifier("");
			m.setSMissionGiver(MissionGiver);
			m.setSMissionSTFTextIdentifier("");
			m.setIDisplayObjectCRC(0); // (0xE191DBAB);
			m.setPickupX(0);
			m.setPickupY(0);
			m.setPickupZ(0);
			m.setMissionX(0);
			m.setMissionY(0);
			m.setMissionZ(0);
			m.setMissionPlanetID(-1);
			m.setIPickupPlanetID(-1);
			m.setIMissionType(Constants.MISSION_TYPE_DELIVER);
			m.setIDiffcultyLevel(0);
			m.setOwnerPlayer(p);
			m.setContainer(t, -1, false);
			m.setParentID(t.getID());
			m.setTParentObject(t);
			p.getServer().addObjectToAllObjects(m, false, false);
			vML.add(m);
		}
		t.setVMissionList(vML);
		t.setVEmptyMissionList(vML);
		p.setMissionBag(t);
	}

	private void createPlayerUnarmedWeapon(Player p) {
		ItemTemplate template = getTemplateDataByFilename("object/weapon/melee/unarmed/shared_unarmed_default_player.iff");
		if (template != null) {
			Weapon unarmedWeapon = new Weapon();
			unarmedWeapon.setCRC(template.getCRC());
			unarmedWeapon.setIsDefaultWeapon(true);
			unarmedWeapon.setSkillRequirement(675); // "species" -- Don't know
			// about this variable.
			unarmedWeapon.setIFFFileName(template.getIFFFileName());
			unarmedWeapon.setSTFFileName(template
					.getSTFFileName());
			unarmedWeapon.setSTFFileIdentifier(template
					.getSTFFileIdentifier());
			try {
				unarmedWeapon.setConditionDamage(0, false);
				unarmedWeapon.setMaxCondition(100, false);
				unarmedWeapon.setName(p.getFirstName() + "'s fists");
			} catch (Exception e) {

			}
			unarmedWeapon.setOwner(p);
			unarmedWeapon.setMinDamage(75);
			unarmedWeapon.setMaxDamage(80);
			unarmedWeapon.setRefireDelay(4.5f);
			unarmedWeapon.setAttackRange(5.0f);
			unarmedWeapon.setCustomizationData(null);
			unarmedWeapon.setID(p.getServer().getNextObjectID());
			unarmedWeapon.setBSendsEquipedState(true);
			unarmedWeapon.setArmorPiercingLevel(Constants.DAMAGE_PIERCING_NONE);
			unarmedWeapon.setWeaponType(Constants.WEAPON_TYPE_UNARMED);
			unarmedWeapon.setIsDefaultWeapon(true);
			p.getServer().addObjectToAllObjects(unarmedWeapon, false, false);
			p.equipWeapon(unarmedWeapon, false);
		}
	}

	/**
	 * Load the template data for all items in the game. (Their IFF file name,
	 * their type, their ID, what slots they occupy on the Player, etc. etc.)
	 */
	private static void loadItemTemplateData() {
		try {
			vItemTemplateData = new Hashtable<Integer, ItemTemplate>();
			vItemTemplateData.put(0, new ItemTemplate()); // Note: This is done
			// to have the
			// template ID's
			// aligned between
			// the database and
			// the server.
			String query = "Select * from `item_template`;";
			Statement s = conn.createStatement();
			ResultSet r = null;
			if (s.execute(query)) {
				r = s.getResultSet();
				while (r.next()) {
					ItemTemplate it = new ItemTemplate();
					// Load the data into this ItemTemplate object.
					// We don't care about the templateID number, since we
					// assume they are being entered in order.
					it.setTemplateID(r.getInt(1));
					it.setNeedsSerial(r.getBoolean(2));
					it.setIFFFileName(r.getString(3));
					int iRequiredSkill = r.getInt(5);
					if (iRequiredSkill > 0) {
						it.setRequiredSkillID(iRequiredSkill);
					}
					int iRequiredSpecies = r.getInt(6);
					if (iRequiredSpecies > 0) {
						it.setRequiredSpeciesMale(iRequiredSpecies);
						it.setRequiredSpeciesFemale(iRequiredSpecies + 10);
					}

					it.setCRC(r.getLong(9));
					it.setSTFFileName(r.getString(11));
					it.setSTFFileIdentifier(r.getString(4));
					it.setSTFDetailIdentifier(r.getString(13));
					it.setSTFDetailName(r.getString(14));
					it.setSTFLookAtIdentifier(r.getString(15));
					it.setSTFLookAtName(r.getString(16));
					it.setScriptType(r.getInt(18));
					it.setScriptName(r.getString(19));
					it.setIsContainer(r.getBoolean("iscontainer"));
					it.setIsFemaleItem(r.getBoolean("femaleitem"));
					String[] rest = r.getString("racerestrictions").split(",");
					int[] iRest = new int[rest.length];
					for (int i = 0; i < iRest.length; i++) {
						iRest[i] = Integer.parseInt(rest[i]);
					}
					it.setRaceRestrictions(iRest);
                                        //weapon Stats
                                        // Intergers
                                        it.setMinDmg(r.getInt("MinDmg"));
                                        it.setMaxDmg(r.getInt("MaxDmg"));
                                        it.setHealthCost(r.getInt("HealthCost"));
                                        it.setActionCost(r.getInt("ActionCost"));
                                        it.setMindCost(r.getInt("MindCost"));
                                        it.setWoundChance(r.getInt("WoundChance"));
                                        //floats
                                        it.setRefireDelay(r.getFloat("RefireDelay"));
                                        it.setZeroRange(r.getFloat("ZeroRange"));
                                        it.setIdealRange(r.getFloat("IdealRange"));
                                        it.setMaxRange(r.getFloat("MaxRange"));
                                        //bytes
                                        it.setWeaponType(r.getByte("WeaponType"));
                                        it.setDamageType(r.getByte("DamageType"));
                                        it.setArmorPiercingLevel(r.getByte("ArmorPiercing"));
					vItemTemplateData.put(it.getTemplateID(), it);
				}
				r.close();
				s.close();
			} else {
				return;
			}
			if (s != null) {
				if (!s.isClosed()) {
					s.close();
				}
			}
			s = conn.createStatement();
			query = "Select * from `starter_items`;";
			if (s.execute(query)) {
				r = s.getResultSet();
				while (r.next()) {
					int itemID = r.getInt(4);
					ItemTemplate it = vItemTemplateData.get(itemID);
					String sStarterProfession = r.getString(2);
					int iRaceID = r.getInt(3);
					int iProfessionID = Constants
							.getStartingProfessionID(sStarterProfession);
					it.setStarterItemParamater(iRaceID, iProfessionID, true);
				}
				r.close();
				s.close();
			} else {
				return;
			}
		} catch (Exception e) {
			System.out
					.println("Error loading item templates and setting starter item data: "
							+ e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the starter items into the given player, based on their starting
	 * species & profession.
	 * 
	 * @param player
	 *            -- The player being created.
	 */
	private void insertStarterItems(Player player) {
		// This is a temporary debug print to test the flow of the Script data.
		// System.out.println("DatabaseInterface::inserStarterItems() called, scripts loading.");
		// ZoneServer server = player.getServer();
		int iRaceID = player.getRaceID();
		String sStarterProfession = player.getStartingProfession();
		// String statement =
		// "Insert into `item` (objectid, serverid, ownerid, objectType, itemData) VALUE (?, ?, ?, ?, ?);";
		String query = "Select * from starter_items where profession = '"
				+ sStarterProfession + "' and species_id = " + iRaceID + ";";
		try {
			/*
			 * if (!conn.isValid(1)) { bConnected = false; initialize(); }
			 */
			Statement s = conn.createStatement();
			s.execute(query);
			ResultSet r = s.getResultSet();
			while (r.next()) {
				int templateID = r.getInt(4);
				String sCustomName = r.getString(10);
				ItemTemplate item = vItemTemplateData.get(templateID);
				if (item.getIFFFileName().contains("weapon")) {
					// String weaponName, String weaponSTF
					Weapon t = new Weapon();
					t.setTemplateID(templateID);
					// String iffFileName = item.getIFFFileName();
					t.setIFFFileName(item.getIFFFileName());
					t.setSTFFileName(item.getSTFFileName());
					t.setSTFFileIdentifier(item
							.getSTFFileIdentifier());
					t.setName("");
					// String stfFilename = item.getSTFFileName();
					// System.out.println("STF Filename for item: " +
					// stfFilename+ ", template ID: " + templateID);
					// t.setSTFFileName(stfFilename);
					// t.setSTFFileIdentifier(item.getSTFFileIdentifier());
					// t.setSTFDetailName(item.getSTFDetailName());
					// t.setSTFDetailIdentifier(item.getSTFDetailIdentifier());
					// t.setSTFLookAtName(item.getSTFLookAtName());
					// t.setSTFLookAtIdentifier(item.getSTFLookAtIdentifier());
					t.setID(player.getServer().getNextObjectID());
					t.setConditionDamage(0, false);
					t.setMaxCondition(1000, false);                               
					t.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
					player.getServer().addObjectToAllObjects(t, false, false);
					t.setOwner(player);
					player.addItemToInventory(t);
					t.setName(sCustomName);

					if (r.getBoolean(5)) {
						player.getInventory().addLinkedObject(t);
						t.setEquipped(player, 4);

					} else {
						t.setEquipped(player.getInventory(), -1);
					}
					// player.getInventoryItem().addLinkedObject(t);
					// StringBuffer sBuff = new
					// StringBuffer().append(player.getFirstName()).append("'s ");
					// sBuff.append(sCustomName);

					// This is where the script should be inserted after it is
					// parsed.

				} else if (item.getIFFFileName().contains("/instrument/")) {
					Instrument t = new Instrument();
					t.setTemplateID(templateID);
					t.setIFFFileName(item.getIFFFileName());
					t.setSTFFileName(item.getSTFFileName());
					t.setSTFFileIdentifier(item
							.getSTFFileIdentifier());
					t.setName("");
					t.setID(player.getServer().getNextObjectID());
					t.setConditionDamage(0, false);
					t.setMaxCondition(1000, false);
					t.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
					player.getServer().addObjectToAllObjects(t, false, false);
					t.setOwner(player);
					player.addItemToInventory(t);
					t.setName(sCustomName);
					if (r.getBoolean(5)) {
						player.getInventory().addLinkedObject(t);
						t.setEquipped(player, 4);

					} else {
						t.setEquipped(player.getInventory(), -1);
					}
				} else {
					TangibleItem t;
					String sIFFFilename = item.getIFFFileName();
					switch (templateID) {
					case 8922: // Clothing crafting
					case 8925: // Food or spice crafting
					case 8926: // Generic tool
					case 8927: // Jedi tool
					case 8934: // Space tool -- ships, missiles, blasters, etc.
					case 8937: // Structures
					case 8940: { // Weapon, Droid, General Item -- You are
						// identical to General, except you can
						// handle more advanced schematics.
						t = new CraftingTool();
						break;
					}
					default: {
						if (sIFFFilename.contains("/food")) {
							t = new FoodItem();
						} else {
							t = new TangibleItem();
						}
						break;
					}
					}
					t.setTemplateID(templateID);
					t.setIFFFileName(item.getIFFFileName());
					t.setID(player.getServer().getNextObjectID());
					t.setConditionDamage(0, false);
					t.setMaxCondition(1000, false);
					t.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
					player.getServer().addObjectToAllObjects(t, false, false);
					player.addItemToInventory(t);
					t.setOwner(player);
					if (r.getBoolean(5)) {
						player.getInventory().addLinkedObject(t);
						t.setEquipped(player, 4);

					} else {
						player.getInventory().addLinkedObject(t);
					}
					t.setName(sCustomName, false);
				}
			}
		} catch (SQLException ee) {
			System.out.println("SQL Exception caught: " + ee.toString());
			System.out.println("Error code: " + ee.getErrorCode());
			System.out.println("Message: " + ee.getMessage());
			System.out.println("SQL State: " + ee.getSQLState());
			ee.printStackTrace();
		} catch (Throwable t) {
			System.out.println("Exception in insertStarterItems: "
					+ t.toString());
			t.printStackTrace();
		}
	}

	/**
	 * Saves changes made by / to the player to the Database. This is called
	 * every 5 minutes by a Player, and when the player successfully
	 * disconnects.
	 * 
	 * @param p
	 *            -- The player being saved.
	 */
	protected void updatePlayer(Player p, boolean bSaveImmediately,
			boolean delete) {
		if (p != null) {
			try {
				String query = "";
				if (delete) {
					query = "Update `character` set `playerData` = ? , `delete` = ? , `deletedate` = ? where `character_id` = ? and `server_id` = ?;";
				} else {
					query = "Update `character` set `playerData` = ? where `character_id` = ? and `server_id` = ?;";
				}
				// System.out.println(query);
				PreparedStatement s = conn.prepareStatement(query);
				if (delete) {
					p.setIsDeleted(delete);
					s.setBoolean(2, delete);
					s.setLong(3, System.currentTimeMillis());
					s.setLong(4, p.getID());
					s.setInt(5, p.getServerID());
				} else {
					s.setLong(2, p.getID());
					s.setInt(3, p.getServerID());
				}
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				ObjectOutputStream oOut = new ObjectOutputStream(bOut);
				oOut.writeObject(p);
				oOut.flush();
				byte[] serializedData = bOut.toByteArray();
				bOut = new ByteArrayOutputStream();
				Deflater deflate = new Deflater();
				deflate.setLevel(Deflater.DEFAULT_COMPRESSION);
				deflate.setStrategy(Deflater.DEFAULT_STRATEGY);
				DeflaterOutputStream out = new DeflaterOutputStream(bOut,
						deflate);
				out.write(serializedData);
				out.finish();
				out.flush();
				byte[] compressedPlayer = bOut.toByteArray();
				System.out.println("Player " + p.getFirstName()
						+ " compressed size: " + compressedPlayer.length);
				s
						.setBinaryStream(1, new ByteArrayInputStream(
								compressedPlayer));
				if (bSaveImmediately) {
					s.execute();
					s.close();
					s = null;
				} else {
					// System.out.println("Delayed Save");
					addStatement(s);
				}
				oOut.close();
				oOut = null;
				bOut = null;
			} catch (Throwable t) {
				System.out.println("Error occured updating player data: "
						+ t.toString());
				t.printStackTrace();

			}
		}
		lHeartbeatTimerMS = HEARTBEAT_DELAY_MS;

	}

	private static void loadMissionTemplateData() {
		try {
			String query = "Select * from `mission_template`;";
			Statement s = conn.createStatement();

			if (s.execute(query)) {
				ResultSet r = s.getResultSet();

				while (r.next()) {
					MissionTemplate mission = new MissionTemplate();
					mission.setMissionID(r.getInt("id"));
					mission.setMissionStringFile(r.getString("template_name"));
					mission.setMissionNumberOfEntries(r
							.getInt("numberofentries"));
					mission.setMissionRequiredFaction((int) r
							.getLong("factioncrc"));
					mission.setMissionDifficulty(r.getInt("difficultylevel"));
					mission
							.setMissionTypeCRC((int) r
									.getLong("missiontypecrc"));
					mission.setMissionDisplayObjectCRC((int) r
							.getLong("displaycrc"));
					String[] alt = r.getString("allowedlairtemplates").split(
							",");
					int[] ialt = new int[alt.length];
					for (int i = 0; i < alt.length; i++) {
						ialt[i] = Integer.parseInt(alt[i]);
					}
					mission.setMissionAllowedLairTemplates(ialt);
					String[] alp = r.getString("planetsallowed").split(",");
					int[] ialp = new int[alp.length];
					for (int i = 0; i < alp.length; i++) {
						ialp[i] = Integer.parseInt(alp[i]);
					}
					mission.setMissionRequiredPlanet(ialp);
					mission.setMissionTerminalType(r.getInt("terminaltype"));
					vMissionTemplateData.put(mission.getMissionID(),
							mission);
				}
			} else {
				DataLog.logEntry(
						"Unable to load Mission data, disabling missions.",
						"DatabaseInterface", Constants.LOG_SEVERITY_CRITICAL,
						true, true);
			}
		} catch (Exception e) {
			DataLog.logException("Exception Caught", "DatabaseInterface", true,
					true, e);
		}
	}

	/**
	 * Loads the minimum / maximum caps, IFF filenames, STF filenames, etc. etc.
	 * for all resource types.
	 */
	private static void loadResourceTemplateData() {

		vResourceTemplateData = new Hashtable<Integer, ResourceTemplateData>();
		try {
			String statement = "Select * from `resourcecaps`;";

			Statement s = conn.createStatement();
			if (s.execute(statement)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					ResourceTemplateData data = new ResourceTemplateData();
					data.setResourceTypeName(r.getString(1));
					data.setResourceClassName(r.getString(2));
					int resourceTypeID = r.getInt(3);
					data.setResourceTypeID(resourceTypeID);
					// System.out.println("Loading resource cap information for "
					// + data.getResourceClass() + " " +
					// data.getResourceType());
					// Load data from columns 4 to 26. These are the resource
					// caps.
					data.setCap(Constants.RESOURCE_CAP_COLD_RESIST, r
							.getShort(4), false);
					data.setCap(Constants.RESOURCE_CAP_COLD_RESIST, r
							.getShort(5), true);
					data.setCap(Constants.RESOURCE_CAP_CONDUCTIVITY, r
							.getShort(6), false);
					data.setCap(Constants.RESOURCE_CAP_CONDUCTIVITY, r
							.getShort(7), true);
					data.setCap(Constants.RESOURCE_CAP_DECAY_RESIST, r
							.getShort(8), false);
					data.setCap(Constants.RESOURCE_CAP_DECAY_RESIST, r
							.getShort(9), true);
					data.setCap(Constants.RESOURCE_CAP_HEAT_RESIST, r
							.getShort(10), false);
					data.setCap(Constants.RESOURCE_CAP_HEAT_RESIST, r
							.getShort(11), true);
					data.setCap(Constants.RESOURCE_CAP_MALLEABILITY, r
							.getShort(12), false);
					data.setCap(Constants.RESOURCE_CAP_MALLEABILITY, r
							.getShort(13), true);
					data.setCap(Constants.RESOURCE_CAP_SHOCK_RESIST, r
							.getShort(14), false);
					data.setCap(Constants.RESOURCE_CAP_SHOCK_RESIST, r
							.getShort(15), true);
					data.setCap(Constants.RESOURCE_CAP_UNIT_TOUGHNESS, r
							.getShort(16), false);
					data.setCap(Constants.RESOURCE_CAP_UNIT_TOUGHNESS, r
							.getShort(17), true);
					data.setCap(Constants.RESOURCE_CAP_ENTANGLE_RESIST, r
							.getShort(18), false);
					data.setCap(Constants.RESOURCE_CAP_ENTANGLE_RESIST, r
							.getShort(19), true);
					data.setCap(Constants.RESOURCE_CAP_POTENTIAL_ENERGY, r
							.getShort(20), false);
					data.setCap(Constants.RESOURCE_CAP_POTENTIAL_ENERGY, r
							.getShort(21), true);
					data.setCap(Constants.RESOURCE_CAP_FLAVOR, r.getShort(22),
							false);
					data.setCap(Constants.RESOURCE_CAP_FLAVOR, r.getShort(23),
							true);
					data.setCap(Constants.RESOURCE_CAP_OVERALL_QUALITY, r
							.getShort(24), false);
					data.setCap(Constants.RESOURCE_CAP_OVERALL_QUALITY, r
							.getShort(25), true);
					data.setResourceRootName(r.getString(28));
					data.setTemplateID(r.getInt(29));
					data.setMinDespawnTimerMS(r.getLong(30) * 1000); // The
					// despawn
					// timers
					// are
					// in
					// seconds
					// in
					// the
					// database,
					// but
					// must
					// be in
					// milliseconds
					// here.
					data.setMaxDespawnTimerMS(r.getLong(31) * 1000);
					vResourceTemplateData.put(resourceTypeID, data);
					// System.out.println("Got resource.  Type: " +
					// resourceTypeID);
				}
			}
		} catch (Exception e) {
			System.out.println("Error loading resource cap data: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	protected static ResourceTemplateData getResourceTemplate(int iTemplateID) {
		return vResourceTemplateData.get(iTemplateID);
	}

	private boolean bInitializedStaticObjects = false;

	/**
	 * Initializes all Static World objects and adds them to the given Zone
	 * Server.
	 * 
	 * @param server
	 *            -- The Zone Server being started.
	 */
	protected void initializeStaticObjects(ZoneServer server) {
		if (bInitializedStaticObjects) {
			return;
		}
		DataLog.logEntry("InitializeStaticObjects", "DatabaseInterface",
				Constants.LOG_SEVERITY_INFO, true, true);
		int iNumObjects = 0;
		try {
			long objectID;
			long parentID;
			int templateID;
			ItemTemplate template;
			String sIFFName;
			float orientN;
			float orientS;
			float orientE;
			float orientW;
			float posX;
			float posY;
			float posZ;
			float fScale;
			int iPlanetID;
			String query = "Select * from `worldobjects` where `template_id` <= 992 OR (`template_id` >= 15394 AND `template_id` <= 15396) order by `id`, `parent`;"; // This is all the buildings.
			// System.out.println(query);
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				// int numEntries = 706;
				ResultSet r = s.getResultSet();
				while (r.next()) {
					iNumObjects++;
					objectID = r.getLong(1);
					parentID = r.getLong(2);
					templateID = r.getInt(3);
					template = vItemTemplateData.get(templateID);
					sIFFName = template.getIFFFileName();
					orientN = r.getFloat(4);
					orientS = r.getFloat(5);
					orientE = r.getFloat(6);
					orientW = r.getFloat(7);
					posX = r.getFloat(8);
					posY = r.getFloat(9);
					posZ = r.getFloat(10);
					fScale = r.getFloat(12);
					iPlanetID = r.getInt(13);
					if (iPlanetID == 255) {
						iPlanetID = 0; // Temp: Spawn it on Corellia only.
					}

					/*
					 * if (sIFFName.contains("cloning")) {
					 * System.out.println("Insert into `planetlocationmap` values ("
					 * + numEntries + "," + iPlanetID + ",'" +
					 * Constants.PlanetNames[iPlanetID].toLowerCase() +
					 * "','Cloning Facility'," + posX + "," + posY +
					 * ",4, 0, 0, 0);" ); numEntries++; }
					 */
					Structure building = new Structure();
					building.setTemplateID(templateID);
					// building.setIFFFileName(sIFFName);
					building.setID(objectID);
					building.setOrientationN(orientN);
					building.setOrientationS(orientS);
					building.setOrientationE(orientE);
					building.setOrientationW(orientW);
					building.setX(posX);
					building.setY(posY);
					building.setZ(posZ);
					building.setPlanetID(iPlanetID);
					building.setIsStaticObject(true);
					server.addObjectToAllObjects(building, true, false);
				}
				r.close();
				r = null;
			}
			s.close();
			s = conn.createStatement();
			// System.out.println("Done buildings.");
			query = "Select * from `worldobjects` where `template_id` = 15393  order by `id`, `parent`;";
			// System.out.println(query);
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					iNumObjects++;
					objectID = r.getLong(1);
					parentID = r.getLong(2);
					templateID = r.getInt(3);
					template = vItemTemplateData.get(templateID);
					sIFFName = template.getIFFFileName();
					orientN = r.getFloat(4);
					orientS = r.getFloat(5);
					orientE = r.getFloat(6);
					orientW = r.getFloat(7);
					posX = r.getFloat(8);
					posY = r.getFloat(9);
					posZ = r.getFloat(10);
					fScale = r.getFloat(12);
					iPlanetID = r.getInt(13);
					if (iPlanetID == 255) {
						iPlanetID = 0; // Temp: Spawn it on Corellia only.
					}
					Structure building = (Structure) server
							.getObjectFromAllObjects(parentID);
					Cell cell = null;
					//if (building != null) {
						cell = new Cell(building, building
								.getCurrentCellCount(), false);
						cell.setTemplateID(templateID);
						cell.setID(objectID);
						building.addCell(cell);
					//} else {

						// Don't make the cell!
						// System.out.println(objectID +
						// ": Creating cell with no parent building.  Requested building ID: "
						// + parentID);
						//cell = new Cell(0, 0, false);
					//}
					// cell.setCRC(template.getCRC());
					cell.setOrientationN(orientN);
					cell.setOrientationS(orientS);
					cell.setOrientationE(orientE);
					cell.setOrientationW(orientW);
					cell.setCellX(posX);
					cell.setCellY(posY);
					cell.setCellZ(posZ);
					cell.setX(building.getX());
					cell.setY(building.getY());
					cell.setZ(building.getZ());
					cell.setPlanetID(iPlanetID);
					cell.setIsStaticObject(true);
					server.addObjectToAllObjects(cell, true, false);
				}
				r.close();
				r = null;
			}
			s.close();
			s = conn.createStatement();
			query = "Select * from `worldobjects` where `template_id` > 992 and `template_id` != 15393 and `template_id` != 15394 and `template_id` != 15395 and `template_id` != 15396;";
			// System.out.println(query);
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				r.first();
				// r.last();
				// do {
				while (r.next()) {
					iNumObjects++;
					objectID = r.getLong(1);
					parentID = r.getLong(2);
					templateID = r.getInt(3);
					template = vItemTemplateData.get(templateID);
					sIFFName = template.getIFFFileName();
					orientN = r.getFloat(4);
					orientS = r.getFloat(5);
					orientE = r.getFloat(6);
					orientW = r.getFloat(7);
					posX = r.getFloat(8);
					posY = r.getFloat(9);
					posZ = r.getFloat(10);
					fScale = r.getFloat(12);
					iPlanetID = r.getInt(13);

					// planet id 255 means we have to look for a parent to get
					// the planet id.
					// if (iPlanetID == 255) {
					// Tutorial planet.
					// continue;
					// iPlanetID = 0; // Temp: Spawn it on Corellia only.
					// }
					// /System.out.println("World Object Data:");
					// System.out.println("ObjectID: " + objectID +
					// ", parentID: " + parentID + ", templateID: " + templateID
					// + ", IFF name: " + sIFFName + ", planetID: " +
					// iPlanetID);
					// System.out.println("X: " + posX + ", Y: " + posY +
					// ", Z: " + posZ);
					// System.out.flush();
					if (sIFFName.contains("object/building")) {
						// System.out.println("Ack!  Got a building, with template ID "
						// + templateID + " for Object ID " + objectID);
					} else if (sIFFName.contains("object/cell")) {
						// System.out.println("Ack!  Got a cell, with template ID "
						// + templateID + " for Object ID " + objectID);
					} else if (sIFFName.contains("object/creature")
							|| sIFFName.contains("object/mobile")) {
						NPC creature = new NPC();
						creature.setServer(server);
						creature.setTemplateID(templateID);
						// creature.setIFFFileName(sIFFName);
						creature.setID(objectID);
						// creature.setCRC(template.getCRC());
						creature.setOrientationN(orientN);
						creature.setOrientationS(orientS);
						creature.setOrientationE(orientE);
						creature.setOrientationW(orientW);
						creature.setPlanetID(iPlanetID);
						creature.setScale(fScale, false);
						creature.setIsStaticObject(true);
						if (parentID > 0) {
							// Need to spawn this thing inside the cell.
							creature.setCellID(parentID);
							Cell cell = (Cell) server
									.getObjectFromAllObjects(parentID);
							if (cell != null) {
								cell.addCellObject(creature);
								creature.setCellX(posX);
								creature.setCellY(posY);
								creature.setCellZ(posZ);
								Structure building = cell.getBuilding();
								creature.setX(building.getX());
								creature.setY(building.getY());
								creature.setZ(building.getZ());
							} else {
								creature.setX(posX);
								creature.setY(posY);
								creature.setZ(posZ);
							}
						} else {
							creature.setX(posX);
							creature.setY(posY);
							creature.setZ(posZ);
						}
						server.addObjectToAllObjects(creature, true, false);
					} else if (sIFFName.contains("object/ship")) {
						// Not yet handled -- wait for JTL.
					} else if (sIFFName.contains("object/static")) {
						SOEObject o = new StaticItem();
						o.setTemplateID(templateID);
						// o.setIFFFileName(sIFFName);
						o.setID(objectID);
						// o.setCRC(template.getCRC());
						o.setOrientationN(orientN);
						o.setOrientationS(orientS);
						o.setOrientationE(orientE);
						o.setOrientationW(orientW);
						o.setX(posX);
						o.setY(posY);
						o.setZ(posZ);
						o.setPlanetID(iPlanetID);
						if (parentID > 0) {
							// Need to spawn this thing inside the cell.
							o.setCellID(parentID);
							Cell cell = (Cell) server
									.getObjectFromAllObjects(parentID);
							if (cell != null) {
								cell.addCellObject(o);
								o.setCellX(posX);
								o.setCellY(posY);
								o.setCellZ(posZ);
								Structure building = cell.getBuilding();
								o.setX(building.getX());
								o.setY(building.getY());
								o.setZ(building.getZ());
							} else {
								o.setX(posX);
								o.setY(posY);
								o.setZ(posZ);
							}
						} else {
							o.setX(posX);
							o.setY(posY);
							o.setZ(posZ);
						}
						o.setIsStaticObject(true);
						server.addObjectToAllObjects(o, true, false);
					} else if (sIFFName.contains("object")
							&& sIFFName.contains("tangible")
							&& sIFFName.contains("terminal")
							&& !sIFFName.contains("draft")
							&& !sIFFName.contains("furniture")
							&& !template.getSTFFileIdentifier().isEmpty()) {

						Terminal t = new Terminal();
						t.setServer(server);
						t.setIsFactionTerminal((byte) 0x00);
						t.setFactionID(Constants.FACTION_NEUTRAL);
						t.setTerminalType(Constants.TERMINAL_TYPES_UNDEFINED);
						t.setID(t.getDBID());
						t.setCustomizationData(null);
						t.setPVPStatus(0);
						t.setStance(null, Constants.STANCE_STANDING, true);
						t.clearAllStates(false);
						t.setMoodID(0);
						if (template.getSTFFileIdentifier().contains("keypad")) {
							t.setTerminalType(Constants.TERMINAL_TYPES_KEYPAD);
						} else if (template.getSTFFileIdentifier().contains(
								"base_terminal")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_BASE_TERMINAL);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_ballot_box")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_BALLOT_BOX);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_bank")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_BANK);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_bazaar")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_BAZAAR);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_bestine_01")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_BESTINE_01);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_bestine_02")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_BESTINE_02);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_bestine_03")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_BESTINE_03);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_bounty_droid")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_BOUNTY_DROID);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_character_builder")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_CHARACTER_BUILDER);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_cloning")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_CLONING);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_geo_bunker")
								&& sIFFName.contains("command")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_GEO_BUNKER);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_geo_bunker")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_GEO_BUNKER2);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_dark_enclave_challenge")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_DARK_ENCLAVE_CHALLENGE);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_dark_enclave_voting")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_DARK_ENCLAVE_VOTING);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_elevator")
								&& sIFFName.contains("down")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_DOWN);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_elevator")
								&& sIFFName.contains("up")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_elevator")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP_DOWN);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_guild")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_GUILD);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_imagedesign")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_IMAGEDESIGN);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_insurance")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_INSURANCE);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_light_enclave_challenge")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_LIGHT_ENCLAVE_CHALLENGE);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_light_enclave_voting")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_LIGHT_ENCLAVE_VOTING);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_light_enclave_voting")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_LIGHT_ENCLAVE_VOTING);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_mission_artisan")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_MISSION_ARTISAN);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_mission_bounty")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_MISSION_BOUNTY);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_mission_entertainer")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_MISSION_ENTERTAINER);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_mission_imperial")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_MISSION_IMPERIAL);
							t.setIsFactionTerminal((byte) 0x01);
							t.setFactionID(Constants.FACTION_IMPERIAL);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_mission_rebel")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_MISSION_REBEL);
							t.setIsFactionTerminal((byte) 0x01);
							t.setFactionID(Constants.FACTION_REBEL);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_mission_scout")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_MISSION_SCOUT);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_mission")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_MISSION_GENERAL);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_space")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_SPACE);
						} else if (template.getSTFFileIdentifier().contains(
								"terminal_space")) {
							t
									.setTerminalType(Constants.TERMINAL_TYPES_TERMINAL_SPACE);
						}

						t.setTemplateID(templateID);
						// t.setIFFFileName(sIFFName);
						t.setID(objectID);
						// t.setCRC(template.getCRC());
						t.setPlanetID(iPlanetID);
						// System.out.println("Terminal Planet set to: " +
						// iPlanetID + " TID:" + t.getID() + " TemplateID:" +
						// templateID + " Iff: " + t.getIFFFileName() );
						// t.setSTFFileName(template.getSTFFileName());
						// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
						// t.setSTFDetailName(template.getSTFDetailName());
						// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
						t.setDBID(t.getID());
						t.setScriptName("script");
						t.setCellID(0);
						t.setCREO3Bitmask(0x108); // Maybe?
						t.setOrientationN(orientN);
						t.setOrientationS(orientS);
						t.setOrientationE(orientE);
						t.setOrientationW(orientW);
						t.setX(posX);
						t.setY(posY);
						t.setZ(posZ);
						t.setPlanetID(iPlanetID);
						t.setIsStaticObject(true);
						if (parentID != 0) {
							t.setCellID(parentID);
							SOEObject object = server
									.getObjectFromAllObjects(parentID);
							if (object != null) {
								if (object instanceof Cell) {
									Cell cell = (Cell) object;
									cell.addCellObject(t);
									t.setCellX(posX);
									t.setCellY(posY);
									t.setCellZ(posZ);
									Structure building = cell.getBuilding();
									t.setX(building.getX());
									t.setY(building.getY());
									t.setZ(building.getZ());
								} else {
									t.setX(posX);
									t.setY(posY);
									t.setZ(posZ);
								}
							}
						}
						if (t != null) {
							// System.out.println("Adding World Object Terminal to all objects. "
							// + t.getID());
							server.addObjectToAllObjects(t, true, false);
						}
					} else if (sIFFName.contains("object")
							&& sIFFName.contains("tangible")) {
						TangibleItem t = new TangibleItem();
						t.setTemplateID(templateID);
						// t.setIFFFileName(sIFFName);
						t.setID(objectID);
						// t.setCRC(template.getCRC());
						t.setPlanetID(iPlanetID);
						t.setOrientationN(orientN);
						t.setOrientationS(orientS);
						t.setOrientationE(orientE);
						t.setOrientationW(orientW);
						t.setX(posX);
						t.setY(posY);
						t.setZ(posZ);
						t.setPlanetID(iPlanetID);
						t.setIsStaticObject(true);
						if (parentID != 0) {
							SOEObject object = server
									.getObjectFromAllObjects(parentID);
							if (object != null) {
								if (object instanceof Cell) {
									Cell cell = (Cell) object;

									cell.addCellObject(t);
									t.setCellX(posX);
									t.setCellY(posY);
									t.setCellZ(posZ);
									Structure building = cell.getBuilding();
									t.setX(building.getX());
									t.setY(building.getY());
									t.setZ(building.getZ());
								} else if (object instanceof TangibleItem) {
									TangibleItem ti = (TangibleItem) object;
									ti.addLinkedObject(t);
									t.setX(posX);
									t.setY(posY);
									t.setZ(posZ);

								} else {
									t.setX(posX);
									t.setY(posY);
									t.setZ(posZ);
								}
							} else {
								// System.out.println("Error:  Tangible " +
								// objectID +
								// " attempting to enter null parent with ID " +
								// parentID);
							}
						}
						if (t != null) {
							server.addObjectToAllObjects(t, true, false);
						}
					} else if (sIFFName.contains("object/weapon")) {
						Weapon weapon = new Weapon();
						weapon.setTemplateID(templateID);
						// weapon.setIFFFileName(sIFFName);
						// weapon.setCRC(template.getCRC());
						weapon.setID(objectID);
						weapon.setPlanetID(iPlanetID);
						weapon.setOrientationN(orientN);
						weapon.setOrientationS(orientS);
						weapon.setOrientationE(orientE);
						weapon.setOrientationW(orientW);
						weapon.setX(posX);
						weapon.setY(posY);
						weapon.setZ(posZ);
						weapon.setIsStaticObject(true);
						if (parentID > 0) {
							// Need to spawn this thing inside the cell.
							weapon.setCellID(parentID);
							Cell cell = (Cell) server
									.getObjectFromAllObjects(parentID);
							if (cell != null) {
								cell.addCellObject(weapon);
								weapon.setCellX(posX);
								weapon.setCellY(posY);
								weapon.setCellZ(posZ);
								Structure building = cell.getBuilding();
								weapon.setX(building.getX());
								weapon.setY(building.getY());
								weapon.setZ(building.getZ());
							} else {
								weapon.setX(posX);
								weapon.setY(posY);
								weapon.setZ(posZ);
							}
						} else {
							weapon.setX(posX);
							weapon.setY(posY);
							weapon.setZ(posZ);
						}
						server.addObjectToAllObjects(weapon, true, false);
					} else if (sIFFName.contains("object/resource")) {
						ResourceContainer rc = new ResourceContainer();
						rc.setTemplateID(templateID);
						// rc.setIFFFileName(sIFFName);
						// rc.setCRC(template.getCRC());
						rc.setID(objectID);
						rc.setPlanetID(iPlanetID);
						rc.setOrientationN(orientN);
						rc.setOrientationE(orientE);
						rc.setOrientationS(orientS);
						rc.setOrientationW(orientW);
						rc.setX(posX);
						rc.setY(posY);
						rc.setZ(posZ);
						if (parentID > 0) {
							// Need to spawn this thing inside the cell.
							rc.setCellID(parentID);
							Cell cell = (Cell) server
									.getObjectFromAllObjects(parentID);
							if (cell != null) {
								cell.addCellObject(rc);
								rc.setCellX(posX);
								rc.setCellY(posY);
								rc.setCellZ(posZ);
								Structure building = cell.getBuilding();
								rc.setX(building.getX());
								rc.setY(building.getY());
								rc.setZ(building.getZ());
							} else {
								rc.setX(posX);
								rc.setY(posY);
								rc.setZ(posZ);
							}
						} else {
							rc.setX(posX);
							rc.setY(posY);
							rc.setZ(posZ);
						}
						rc.setIsStaticObject(true);
						server.addObjectToAllObjects(rc, true, false);
					} else {
						// System.out.println("Unknown world object: " +
						// sIFFName + " with ID " + objectID + " on Planet " +
						// iPlanetID);
					}
				} // } while (r.previous());
				// System.out.println("Records Iterated " + ctr);
				r.close();
				r = null;
			}
			s.close();
			s = null;
			
			ConcurrentHashMap<Long, Player> vAllPlayers = server.getAllPlayers();
			Enumeration<Player> vPlayersEnum = vAllPlayers.elements();
			while (vPlayersEnum.hasMoreElements()) {
				Player player = vPlayersEnum.nextElement();
				long lPlayerCellID = player.getCellID();
				if (lPlayerCellID != 0) {
					Cell cell = (Cell)server.getObjectFromAllObjects(lPlayerCellID);
					Structure theStructure = (Structure)cell.getBuilding();
					
					if (!cell.contains(player)) {
						cell.addCellObject(player);
					}
					if (!theStructure.contains(player)) { 
						System.out.println("InitializeStaticObjects error:  Player added to cell of static structure, but static structure reports does not contain player.");
					}
				}
			}
			
			DataLog.logEntry("End InitializeStaticObjects",
					"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true,
					true);

			// System.out.println("Total number of static world objects: " +
			// iNumObjects);
			Runtime.getRuntime().gc(); // This is a very expensive function.
		} catch (Exception e) {
			System.out.println("Error in initializeStaticObjects: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	protected int isNameAppropriate(String str) {
		if (vCharacterNameFilterDeveloper.contains(str)
				|| vCharacterNameFilterDeveloper.contains(str)) {
			return Constants.NAME_DECLINED_IS_DEVELOPER;
		} else if (vCharacterNameFilterCanonical.contains(str)
				|| vCharacterNameFilterCanonical.contains(str)) {
			return Constants.NAME_DECLINED_IS_CANONICAL;
		} else if (vCharacterNameFilterNumber.contains(str)
				|| vCharacterNameFilterNumber.contains(str)) {
			return Constants.NAME_DECLINED_IS_NUMBER;
		} else if (vCharacterNameFilterProfane.contains(str)
				|| vCharacterNameFilterProfane.contains(str)) {
			return Constants.NAME_DECLINED_IS_PROFANE;
		} else if (vCharacterNameFilterSyntax.contains(str)
				|| vCharacterNameFilterSyntax.contains(str)) {
			return Constants.NAME_DECLINED_SYNTAXICALLY_WRONG;
		}
		String sComparatorString = null;
		for (int i = 0; i < vCharacterNameFilterDeveloper.size(); i++) {
			sComparatorString = vCharacterNameFilterDeveloper.elementAt(i);
			if (str.contains(sComparatorString)) {
				return Constants.NAME_DECLINED_IS_DEVELOPER;
			}
		}
		for (int i = 0; i < vCharacterNameFilterCanonical.size(); i++) {
			sComparatorString = vCharacterNameFilterCanonical.elementAt(i);
			if (str.contains(sComparatorString)) {
				return Constants.NAME_DECLINED_IS_CANONICAL;
			}
		}
		for (int i = 0; i < vCharacterNameFilterNumber.size(); i++) {
			sComparatorString = vCharacterNameFilterNumber.elementAt(i);
			if (str.contains(sComparatorString)) {
				return Constants.NAME_DECLINED_IS_NUMBER;
			}
		}
		for (int i = 0; i < vCharacterNameFilterProfane.size(); i++) {
			sComparatorString = vCharacterNameFilterProfane.elementAt(i);
			if (str.contains(sComparatorString)) {
				return Constants.NAME_DECLINED_IS_PROFANE;
			}
		}
		for (int i = 0; i < vCharacterNameFilterSyntax.size(); i++) {
			sComparatorString = vCharacterNameFilterSyntax.elementAt(i);
			if (str.contains(sComparatorString)) {
				return Constants.NAME_DECLINED_SYNTAXICALLY_WRONG;
			}
		}
		return Constants.NAME_ACCEPTED;

	}

	/**
	 * Verifies that the name a Player has requested for themselves is
	 * appropriate for use in the game.
	 * 
	 * @param player
	 *            -- The player being created.
	 * @return -- The name response code. (See Constants.NAME_DECLINED_??????
	 *         for the name response codes.
	 */
	protected int isNameAppropriate(String sFirstName, String sLastName,
			ZoneServer server) {
		try {
			if (sLastName == null) {
				sLastName = Constants.NONAME;
			}
			if (sFirstName == null) {
				return Constants.NAME_DECLINED_EMPTY;
			}
			// Exact match
			if (vCharacterNameFilterDeveloper.contains(sFirstName)
					|| vCharacterNameFilterDeveloper.contains(sLastName)) {
				//System.out.println("Developer match.  First name? " + vCharacterNameFilterDeveloper.contains(sFirstName) + ", last name? " + vCharacterNameFilterDeveloper.contains(sLastName));
				return Constants.NAME_DECLINED_IS_DEVELOPER;
			} else if (vCharacterNameFilterCanonical.contains(sFirstName)
					|| vCharacterNameFilterCanonical.contains(sLastName)) {
				return Constants.NAME_DECLINED_IS_CANONICAL;
			} else if (vCharacterNameFilterNumber.contains(sFirstName)
					|| vCharacterNameFilterNumber.contains(sLastName)) {
				return Constants.NAME_DECLINED_IS_NUMBER;
			} else if (vCharacterNameFilterProfane.contains(sFirstName)
					|| vCharacterNameFilterProfane.contains(sLastName)) {
				return Constants.NAME_DECLINED_IS_PROFANE;
			} else if (vCharacterNameFilterSyntax.contains(sFirstName)
					|| vCharacterNameFilterSyntax.contains(sLastName)) {
				return Constants.NAME_DECLINED_SYNTAXICALLY_WRONG;
			}

			// End exact match.
			String sComparatorString = null;
			for (int i = 0; i < vCharacterNameFilterDeveloper.size(); i++) {
				sComparatorString = vCharacterNameFilterDeveloper.elementAt(i);
				if (sFirstName.contains(sComparatorString)
						|| sLastName.contains(sComparatorString)) {
					System.out.println("First or last name contains comparator string.");
					return Constants.NAME_DECLINED_IS_DEVELOPER;
				}
				if (sComparatorString.contains(sFirstName)
						|| sComparatorString.contains(sLastName)) {
					System.out.println("Comparator string contains first or last name.");
					
					return Constants.NAME_DECLINED_IS_DEVELOPER;
				}
			}
			for (int i = 0; i < vCharacterNameFilterCanonical.size(); i++) {
				sComparatorString = vCharacterNameFilterCanonical.elementAt(i);
				if (sFirstName.contains(sComparatorString)
						|| sLastName.contains(sComparatorString)) {
					return Constants.NAME_DECLINED_IS_CANONICAL;
				}
				if (sComparatorString.contains(sFirstName)
						|| sComparatorString.contains(sLastName)) {
					return Constants.NAME_DECLINED_IS_CANONICAL;
				}
			}
			for (int i = 0; i < vCharacterNameFilterNumber.size(); i++) {
				sComparatorString = vCharacterNameFilterNumber.elementAt(i);
				if (sFirstName.contains(sComparatorString)
						|| sLastName.contains(sComparatorString)) {
					return Constants.NAME_DECLINED_IS_NUMBER;
				}
				if (sComparatorString.contains(sFirstName)
						|| sComparatorString.contains(sLastName)) {
					return Constants.NAME_DECLINED_IS_NUMBER;
				}
			}
			for (int i = 0; i < vCharacterNameFilterProfane.size(); i++) {
				sComparatorString = vCharacterNameFilterProfane.elementAt(i);
				if (sFirstName.contains(sComparatorString)
						|| sLastName.contains(sComparatorString)) {
					return Constants.NAME_DECLINED_IS_PROFANE;
				}
				if (sComparatorString.contains(sFirstName)
						|| sComparatorString.contains(sLastName)) {
					return Constants.NAME_DECLINED_IS_PROFANE;
				}
			}
			for (int i = 0; i < vCharacterNameFilterSyntax.size(); i++) {
				sComparatorString = vCharacterNameFilterSyntax.elementAt(i);
				if (sFirstName.contains(sComparatorString)
						|| sLastName.contains(sComparatorString)) {
					return Constants.NAME_DECLINED_SYNTAXICALLY_WRONG;
				}
				if (sComparatorString.contains(sFirstName)
						|| sComparatorString.contains(sLastName)) {
					return Constants.NAME_DECLINED_SYNTAXICALLY_WRONG;
				}

			}
			Vector<String> vServerUsedNames = server.getUsedCharacterNames();
			for (int i = 0; i < vServerUsedNames.size(); i++) {
				if (sFirstName.equalsIgnoreCase(vServerUsedNames.elementAt(i))) {
					return Constants.NAME_DECLINED_IS_TAKEN;
				}
				if (sComparatorString.contains(sFirstName)
						|| sComparatorString.contains(sLastName)) {
					return Constants.NAME_DECLINED_IS_TAKEN;
				}

			}
			return Constants.NAME_ACCEPTED;
		} catch (Exception e) {
			System.out.println("Error filtering new character name: "
					+ e.toString());
			e.printStackTrace();
			return Constants.NAME_DECLINED_INTERNAL_ERROR;
		}
	}

	/**
	 * Updates the last active timestamp for the given Username.
	 * 
	 * @param sUsername
	 *            -- The username logging in.
	 */
	protected void updateLastLoginTime(String sUsername) {
		try {
			String query = "Update `account` set `lastlogin` = '"
					+ new Timestamp(System.currentTimeMillis()).toString()
					+ "' where `username` = '" + sUsername + "';";
			Statement s = conn.createStatement();
			s.execute(query);
			s.close();
			lHeartbeatTimerMS = HEARTBEAT_DELAY_MS;

		} catch (Exception e) {
			System.out.println("Error updating account last login time. "
					+ e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * This function loads the list of all resources for the ZoneServer with the
	 * given server ID. If no resources exist for the server, it creates a new
	 * resource table for the server.
	 * 
	 * @param iServerID
	 *            -- The Server ID.
	 * @return The list of spawned resources.
	 */
	protected Vector<SpawnedResourceData> loadResources(int iServerID) {
		Vector<SpawnedResourceData> resources = new Vector<SpawnedResourceData>();
		try {
			String query = "Select * from `resources` where `server_id` = "
					+ iServerID + ";";
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					ObjectInputStream oIn = new ObjectInputStream(result
							.getBinaryStream(4));
					resources.add((SpawnedResourceData) oIn.readObject());
					oIn.close();
					oIn = null;
				}
				result.close();

			}
			s.close();
			query = null;
			Runtime.getRuntime().gc();
		} catch (Exception e) {
			System.out
					.println("Error loading spawned resources from database: "
							+ e.toString());
			e.printStackTrace();
		}
		return resources;
	}

	/**
	 * Saves all the resources for a given server ID to the database.
	 * 
	 * @param vSpawnedResources
	 * @param vDespawnedResources
	 * @param iServerID
	 */
	protected void saveAllResourcesToDatabase(
			Vector<SpawnedResourceData> vSpawnedResources,
			Vector<SpawnedResourceData> vDespawnedResources, int iServerID) {
		ByteArrayOutputStream bOut = null;

		ObjectOutputStream oOut = null;
		try {
			Statement statement = conn.createStatement();
			// System.out.println("Saving resources to database.  Number of resources: "
			// + vSpawnedResources.size());
			for (int i = 0; i < vSpawnedResources.size(); i++) {
				bOut = new ByteArrayOutputStream();
				oOut = new ObjectOutputStream(bOut);
				SpawnedResourceData resourceData = vSpawnedResources
						.elementAt(i);
				String query = "Select * from `resources` where resource_id = "
						+ resourceData.getID() + ";";
				if (statement.execute(query)) {
					ResultSet r = statement.getResultSet();
					// Hackish way of doing this.
					int iNumRows = 0;
					while (r.next()) {
						iNumRows++;
					}
					PreparedStatement state = null;
					oOut.writeObject(resourceData);
					oOut.flush();
					oOut = null;
					byte[] serializedData = bOut.toByteArray();
					bOut = null;
					if (iNumRows == 0) {
						query = "Insert into `resources` (server_id, resource_id, resource_data) values (?, ?, ?);";
						state = conn.prepareStatement(query);
						state.setInt(1, iServerID);
						state.setLong(2, resourceData.getID());
						state.setBinaryStream(3, new ByteArrayInputStream(
								serializedData));

					} else {
						query = "Update `resources` set resource_data = ? where resource_id = ? and server_id = ?;";
						state = conn.prepareStatement(query);
						state.setBinaryStream(1, new ByteArrayInputStream(
								serializedData));
						state.setLong(2, resourceData.getID());
						state.setInt(3, iServerID);
					}
					state.execute();

				}
			}

		} catch (Exception e) {
			System.out.println("Error saving resources to database: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	protected static ItemTemplate getTemplateDataByID(int iTemplateID) {
		return vItemTemplateData.get(iTemplateID);
	}

	/**
	 * This allows to search for a list of templates that have the same key
	 * words in their iff file name. this will be useful for loot or any other
	 * type of lookup where we need to pick something from a selection of
	 * similar items. If nothing is found null is returned.
	 * 
	 * @param sTemplateLike
	 * @return - Vector<ItemTemplate>
	 */
	protected static Vector<ItemTemplate> getSimilarTemplates(
			String sTemplateLike) {
		sTemplateLike = sTemplateLike.toLowerCase();
		Vector<ItemTemplate> retval = new Vector<ItemTemplate>();
		Enumeration<ItemTemplate> templateEnumerator = vItemTemplateData
				.elements();
		try {
			int count = 0;
			while (templateEnumerator.hasMoreElements()) {
				ItemTemplate data = templateEnumerator.nextElement();
				if (data.getIFFFileName().contains(sTemplateLike)) {
					retval.add(data);
					count++;
				}
			}
			if (count >= 1) {
				// System.out.println("Returning " + count + " For Keyword " +
				// sTemplateLike);
				return retval;
			}
		} catch (Exception e) {
			System.out.println("Exception Caught while getSimilarTemplates "
					+ e);
			e.printStackTrace();
		}
		DataLog.logEntry(
				"Returning Null Template list at getSimilarTemplates Key Word: "
						+ sTemplateLike, "DatabaseInterface",
				Constants.LOG_SEVERITY_INFO, true, true);
		return null;
	}

	protected static ItemTemplate getTemplateDataByFilename(String sFileName) {
		Enumeration<ItemTemplate> templateEnumerator = vItemTemplateData
				.elements();
		while (templateEnumerator.hasMoreElements()) {
			ItemTemplate data = templateEnumerator.nextElement();
			if (data.getIFFFileName().equalsIgnoreCase(sFileName)) {
				return data;
			}
		}
		return null;
	}

	protected static ItemTemplate getTemplateDataByCRC(int iCRC) {
		Enumeration<ItemTemplate> templateEnumerator = vItemTemplateData
				.elements();
		while (templateEnumerator.hasMoreElements()) {
			ItemTemplate data = templateEnumerator.nextElement();
			if (data.getCRC() == iCRC) {
				return data;
			}
		}
		return null;
	}

	protected void updateResource(SpawnedResourceData theResource, int iServerID) {
		// System.out.println("Updating resource " +
		// theResource.getDestinationName());
		String query = "Update resources set resource_data = ? where resource_id = ? and server_id = ?;";
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(bOut);
			oOut.writeObject(theResource);
			oOut.flush();
			byte[] serializedResource = bOut.toByteArray();
			oOut.close();
			statement.setBinaryStream(1, new ByteArrayInputStream(
					serializedResource));
			statement.setLong(2, theResource.getID());
			statement.setInt(3, iServerID);
			statement.execute();
		} catch (IOException e) {
			System.out.println("Error serializing resource data: "
					+ e.toString());
			e.printStackTrace();
		} catch (SQLException ee) {
			System.out.println("Error executing resource data query: "
					+ ee.toString());
			ee.printStackTrace();
		}
	}

	protected void saveResource(SpawnedResourceData theResource, int iServerID) {
		// System.out.println("Saving new resource " +
		// theResource.getDestinationName());
		String query = "Insert into `resources` (server_id, resource_id, resource_data) values (?, ?, ?);";
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(bOut);
			oOut.writeObject(theResource);
			oOut.flush();
			byte[] serializedResource = bOut.toByteArray();
			oOut.close();
			statement.setBinaryStream(3, new ByteArrayInputStream(
					serializedResource));
			statement.setLong(2, theResource.getID());
			statement.setInt(1, iServerID);
			statement.execute();
		} catch (IOException e) {
			System.out.println("Error serializing resource data: "
					+ e.toString());
			e.printStackTrace();
		} catch (SQLException ee) {
			System.out.println("Error executing resource data query: "
					+ ee.toString());
			ee.printStackTrace();
		}
	}

	public Vector<SWGEmail> getAllEmailsForPlayer(long objectID, int serverID) {
		String query = "Select * from `email` where `player_id` = " + objectID
				+ " and `server_id` = " + serverID + ";";
		try {
			Vector<SWGEmail> vEmails = new Vector<SWGEmail>();
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					// long lCharacterID = result.getLong(1);
					ObjectInputStream oIn = new ObjectInputStream(result
							.getBinaryStream(4));
					SWGEmail email = (SWGEmail) oIn.readObject();
					email.setEmailID(result.getInt(result
							.findColumn("email_id")));// need to set the email
					// id to the database id
					if (!email.getDeleteFlag()) {
						vEmails.add(email);
					}
					oIn.close();
				}
				s.close();
				s = null;
			}
			return vEmails;

		} catch (Exception e) {
			System.out.println("Error executing SQL getAllEmail query: "
					+ e.toString());
			e.printStackTrace();

		}
		return null;

	}

	public Vector<SWGEmail> getNewEmailsForPlayer(long objectID, int serverID) {
		String query = "Select * from `email` where `player_id` = " + objectID
				+ " and `server_id` = " + serverID + ";";
		try {
			Vector<SWGEmail> vEmails = new Vector<SWGEmail>();
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					// long lCharacterID = result.getLong(1);
					ObjectInputStream oIn = new ObjectInputStream(result
							.getBinaryStream(4));
					SWGEmail email = (SWGEmail) oIn.readObject();
					email.setEmailID(result.getInt(result
							.findColumn("email_id")));// need to set the email
					// id to the database id
					if (!email.isRead() && !email.getDeleteFlag()) {
						vEmails.add(email);
					}
					oIn.close();
				}
				result.close();
				result = null;
			}
			s.close();
			s = null;
			return vEmails;

		} catch (Exception e) {
			System.out.println("Error executing SQL getNewEmail query: "
					+ e.toString());
			e.printStackTrace();

		}
		return null;

	}

	public SWGEmail getEmailByID(int lID, Player player) {
		String statement = "Select `email` from `email` where `email_id` = "
				+ lID + " and `player_id` = " + player.getID()
				+ " and `server_id` = " + player.getServer().getServerID()
				+ ";";
		SWGEmail mail = null;
		try {
			Statement s = conn.createStatement();
			if (s.execute(statement)) {
				ResultSet r = s.getResultSet();
				if (r.next()) { // We probably only want the first one...
					ObjectInputStream oIn = new ObjectInputStream(r
							.getBinaryStream(1));
					mail = (SWGEmail) oIn.readObject();
					mail.setEmailID(lID);// we need to set the email id from the
					// db once we get the email
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
		} catch (Exception e) {
			System.out.println("Error getting email from database: "
					+ e.toString());
			e.printStackTrace();
		}

		return mail;
	}

	public void saveEmail(SWGEmail email, Player player) {
		PreparedStatement s;
		String statement = "Insert into `email` VALUES (?, ?, ?, ?);";
		try {
			s = conn.prepareStatement(statement);
			s.setLong(2, player.getID());
			s.setInt(3, player.getServerID());
			// no need to assign an id before saving it the db does this.
			// Give the email an ID before saving it.
			// int emailID = player.getServer().getNextEmailID();
			// email.setEmailID(emailID);
			s.setInt(1, 0);
			// Serialize the email message.
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(bOut);
			oOut.writeObject(email);
			oOut.flush();
			byte[] serializedEmail = bOut.toByteArray();
			s.setBinaryStream(4, new ByteArrayInputStream(serializedEmail));
			s.execute();
			oOut.close();
			bOut = null;
			oOut = null;
			s.close();
			s = null;
		} catch (Exception e) {
			System.out.println("Error saving email: " + e.toString());
			e.printStackTrace();
		}
	}

	public void updateEmail(SWGEmail email, Player player) {
		PreparedStatement s;
		String statement = "Update `email` set `email` = ? where `player_id` = "
				+ player.getID()
				+ " and server_id = "
				+ player.getServer().getServerID()
				+ " and email_id = "
				+ email.getEmailID() + ";";
		try {
			s = conn.prepareStatement(statement);
			// Serialize the email message.
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(bOut);
			oOut.writeObject(email);
			oOut.flush();
			byte[] serializedEmail = bOut.toByteArray();
			s.setBinaryStream(1, new ByteArrayInputStream(serializedEmail));
			s.execute();
			oOut.close();
			bOut = null;
			oOut = null;
			s.close();
			s = null;
		} catch (Exception e) {
			System.out.println("Error saving email: " + e.toString());
			e.printStackTrace();
		}
	}

	public Vector<Integer> getAllUsedEmailID(int iServerID) {
		String query = "Select `email_id` from `email` where `server_id` = "
				+ iServerID + ";";
		Vector<Integer> vIDs = new Vector<Integer>();
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {

				ResultSet r = s.getResultSet();
				while (r.next()) {
					int id = r.getInt(1);
					vIDs.add(id);
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
		} catch (Exception e) {
			System.out.println("Error loading used email IDs: " + e.toString());
			e.printStackTrace();
		}
		return vIDs;
	}

	private static void loadRadialTemplateData() {
		String statement = "Select * from `radialoptions`;";

		try {
			Statement s = conn.createStatement();
			if (s.execute(statement)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					RadialTemplateData data = new RadialTemplateData();
					int id = r.getInt(1);
					data.setID(id);
					data.setCaption(r.getString(2));
					data.setUseableRange(r.getInt(3));
					data.setCommandName(r.getString(4));
					data.setUseItemAttachedTo(r.getBoolean(5));
					// System.out.println("Radial data " + id + ": " +
					// data.getCaption());
					vRadialTemplateData.put(id, data);

				}
			}
		} catch (Exception e) {
			System.out.println("Error getting email from database: "
					+ e.toString());
			e.printStackTrace();
		}

	}

	/*
	 * public ConcurrentHashMap<Long,SOEObject> getWorldObjectsMap(){ return
	 * chmAllWorldObjects; } private void loadAllWorldObjects(){
	 * chmAllWorldObjects = new ConcurrentHashMap<Long,SOEObject>(); String
	 * statement = "Select * from `worldobjects`;";
	 * 
	 * try { Statement s = conn.createStatement(); if (s.execute(statement)) {
	 * ResultSet r = s.getResultSet(); while(r.next()) { SOEObject wO = new
	 * SOEObject(); wO.setID(r.getLong("id"));
	 * wO.setParentID(r.getLong("parent"));
	 * wO.setTemplateID(r.getInt("template_id")); wO.setX(r.getFloat("x"));
	 * wO.setY(r.getFloat("y")); wO.setZ(r.getFloat("z"));
	 * wO.setOrientationN(r.getFloat("oX"));
	 * wO.setOrientationS(r.getFloat("oY"));
	 * wO.setOrientationE(r.getFloat("oZ"));
	 * wO.setOrientationW(r.getFloat("oW"));
	 * wO.setPlanetID(r.getInt("planetid")); wO.setIsWorldObject(true);
	 * wO.setIsStaticObject(true); chmAllWorldObjects.put(wO.getID(), wO); } }
	 * }catch(Exception e){
	 * System.out.println("Exception Caught while loadAllWorldObjects " + e);
	 * e.printStackTrace(); } }
	 */

	public Hashtable<Long, RadialMenuItem> getObjectRadials() {
		Hashtable<Long, RadialMenuItem> retMap = new Hashtable<Long, RadialMenuItem>();
		String statement = "Select * from `objectradialoptions` ORDER BY `buttonnumber`;";
		long id = 0;
		System.out.println("Loading object radials.");
		try {
			Statement s = conn.createStatement();
			if (s.execute(statement)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					RadialMenuItem I = new RadialMenuItem(r
							.getByte("buttonnumber"),
							r.getByte("parentbutton"), (char) r
									.getInt("buttoncommandid"), r
									.getByte("buttonstringlocation"), r
									.getString("buttonstring"));
					I.setlDBID(r.getLong("id"));
					id = r.getLong("id");
					I.setiItemCRC((int) r.getLong("CRC"));
					I.setiCondition(r.getInt("condition"));
					I.setiItemTemplate(DatabaseInterface.getTemplateDataByCRC(
							I.getiItemCRC()).getTemplateID());
					retMap.put(id, I);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Error getting objectradialoptions from database: "
							+ e.toString() + " ID: " + id);
			e.printStackTrace();
		}
		System.out.println("Loaded " + retMap.size() + " menu options.");
		return retMap;
	}

	public Hashtable<Integer, RadialTemplateData> getServerRadials() {
		return vRadialTemplateData;
	}

	public Hashtable<Integer, Vector<MapLocationData>> loadStaticMapLocations(
			ZoneServer server) {
		Hashtable<Integer, Vector<MapLocationData>> dataTable = new Hashtable<Integer, Vector<MapLocationData>>();
		for (int i = 0; i < Constants.PlanetNames.length; i++) {
			dataTable.put(i, new Vector<MapLocationData>());
		}
		try {
			String query = "Select * from `planetlocationmap` where `playerMade` = 0;";
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				int iNumLocations = 0;
				ResultSet result = s.getResultSet();
				while (result.next()) {
					MapLocationData data = new MapLocationData();
					data.setObjectID(server.getNextObjectID());
					int iPlanetID = result.getInt("planetIndex");
					data.setPlanetID(iPlanetID);
					data.setCurrentX(result.getFloat("x"));
					data.setCurrentY(result.getFloat("y"));
					data.setName(result.getString("locationName"));
					data.setObjectType(result.getByte("locationType"));
					data.setObjectSubType(result.getByte("locationSubType"));
					iNumLocations++;
					dataTable.get(iPlanetID).add(data);
				}
				result.close();
			}
			s.close();

		} catch (Exception e) {
			System.out
					.println("Error loading static map locations from database: "
							+ e.toString());

		}
		return dataTable;
	}

	public Hashtable<Integer, Vector<MapLocationData>> loadPlayerMapLocations(
			ZoneServer server) {
		Hashtable<Integer, Vector<MapLocationData>> dataTable = new Hashtable<Integer, Vector<MapLocationData>>();
		for (int i = 0; i < Constants.PlanetNames.length; i++) {
			dataTable.put(i, new Vector<MapLocationData>());
		}
		try {
			String query = "Select * from `planetlocationmap` where `playerMade` = 1;";
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				int iNumLocations = 0;
				ResultSet result = s.getResultSet();
				while (result.next()) {
					MapLocationData data = new MapLocationData();
					data.setObjectID(server.getNextObjectID());
					int iPlanetID = result.getInt("planetIndex");
					data.setPlanetID(iPlanetID);
					data.setCurrentX(result.getFloat("x"));
					data.setCurrentY(result.getFloat("y"));
					data.setName(result.getString("locationName"));
					data.setObjectType(result.getByte("locationType"));
					data.setObjectSubType(result.getByte("locationSubType"));
					iNumLocations++;
					dataTable.get(iPlanetID).add(data);
				}
				result.close();

			}
			s.close();

		} catch (Exception e) {
			System.out
					.println("Error loading static map locations from database: "
							+ e.toString());

		}
		return dataTable;
	}

	// We are going to assume that all of these will be Player made map
	// locations.
	protected void saveMapLocation(MapLocationData data) {
		String statement = "Insert into `planetLocationMap` (`planetIndex`, `planetName`, `locationName`, `x`, `y`, `locationType`, `locationSubType`, `unknownValue`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement s = conn.prepareStatement(statement);
			s.setInt(1, data.getPlanetID());
			s.setString(2, Constants.PlanetNames[data.getPlanetID()]);
			s.setString(3, data.getName());
			s.setFloat(4, data.getCurrentX());
			s.setFloat(5, data.getCurrentY());
			s.setByte(6, data.getObjectType());
			s.setByte(7, data.getObjectSubType());
			s.execute();
			s.close();
			s = null;
		} catch (Exception e) {
			System.out
					.println("Error saving new map location: " + e.toString());
		}
	}

	public Vector<Terminal> loadServerTerminals(ZoneServer server) {

		Vector<Terminal> retval = new Vector<Terminal>();
		String query = "Select * from `terminals` where `spawn` = 1 and `packed` = 0 and `destroy` = 0";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					// Are we going to have different tables for different types
					// of terminals, or is the current Terminal table going to
					// be altered to be more generic?
					// Same Table different ways of putting data into the
					// terminal object based on its purpose. - PF
					if (result.getInt("type") == Constants.TERMINAL_TYPES_TRAVEL_GENERAL
							|| result.getInt("type") == Constants.TERMINAL_TYPES_TICKET_DROID
							|| result.getInt("type") == Constants.TERMINAL_TYPES_CHARACTER_BUILDER
							|| result.getInt("type") == Constants.TERMINAL_TYPES_SKILL_TRAINER
							|| result.getInt("type") == Constants.TERMINAL_TYPES_SHUTTLE_PORT
							|| result.getInt("type") == Constants.TERMINAL_TYPES_TERMINAL_BANK
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_4
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_32
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_64
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_128
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_768) {

						Terminal t = new Terminal();
						t.setServer(server);
						t.setFirstName(result.getString("terminals_name"));
						if (result.getString("terminals_name").startsWith("@")) {
							// if the terminal name contains the prefix @ we
							// clear the name, this means use the stf for a name
							t.setFirstName("");
						}
						t.setTerminalType(result.getInt("type"));
						t.setTicketID(result.getInt("ticketid"));
						t.setLocationID(result.getInt("ticketid"));
						t.setPortID(result.getInt("ticketid"));
						t.setIsFactionTerminal(result.getByte("factionindex"));
						t.setDBID(result.getLong("terminals_id"));
						t.setCRC((int) result.getLong("terminals_crc"));
						long cellID = result.getLong("cellID");
						t.setScriptName(result.getString("script"));
						t.setCellID(cellID);

						t.setPlanetID(result.getInt("planet_id"));
						t.setFactionID(result.getInt("factionindex"));
						t.setCREO3Bitmask(0x108); // Maybe?
						if (cellID != 0) {
							Cell c = (Cell) server.getObjectFromAllObjects(cellID);
							Structure building = c.getBuilding();
							float cellX = result.getFloat("x");
							float cellY = result.getFloat("y");
							float cellZ = result.getFloat("z");
							t.setX(building.getX() + cellX);
							t.setY(building.getY() + cellY);
							t.setZ(building.getZ() + cellZ);
							t.setCellX(cellX);
							t.setCellY(cellY);
							t.setCellZ(cellZ);
						} else {
							t.setX(result.getFloat("x"));
							t.setY(result.getFloat("y"));
							t.setZ(result.getFloat("z"));
						}
						t.setOrientationN(result.getFloat("oI"));
						t.setOrientationS(result.getFloat("oJ"));
						t.setOrientationE(result.getFloat("oK"));
						t.setOrientationW(result.getFloat("oW"));
						t.setID(t.getDBID());
						t.setCustomizationData(null);
						// t.addBitToPVPStatus(Constants.PVP_STATUS_IS_NPC);
						t.setPVPStatus(0);
						t.setStance(null, Constants.STANCE_STANDING, true);
						t.clearAllStates(false);
						t.setMoodID(0);
						// System.out.println("Terminal retrieved " +
						// t.getID());
						ItemTemplate template = getTemplateDataByCRC(t.getCRC());
						if (template != null) {
							// t.setRadialMenus(template.getRadialMenuItems());
							// // Feature being implemented.
							// t.setSTFFileName(template.getSTFFileName());
							t.setIFFFileName(template.getIFFFileName());
							// System.out.println("Terminal iff filename: " +
							// t.getIFFFileName());
							// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
							// t.setSTFDetailName(template.getSTFDetailName());
							// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
							t.setTemplateID(template.getTemplateID());
						}
						if (t.getIFFFileName().contains("trainer")
								&& !t.getIFFFileName().contains("jedi")
								&& !t.getIFFFileName().contains("_fs")
								&& !t.getIFFFileName()
										.contains("industrialist")) {

							String[] TrainerName = server
									.generateRandomName("human");
							String CurrentName = t.getFullName();
							// String[] Titles = CurrentName.split(" ");
							String Profession = CurrentName.replace(" ", "_");

							TrainerName[0] = TrainerName[0].replace(" ", "");
							TrainerName[1] = TrainerName[1].replace(" ", "");
							t.setFirstName(TrainerName[0]);
							t.setLastName(TrainerName[1] + " (" + CurrentName
									+ ")");
							t.setIsSkillTrainer();

							// System.out.println("Randomizing Trainer Template");
							if (Profession.contains("Teras")) {
								Profession = "unarmed_trainer";
							} else if (Profession.contains("Combat")) {
								Profession = "combatmedic_trainer";
							} else if (Profession.contains("Scout")) {
								Profession = "scout_trainer";
							} else if (Profession.contains("Polearm")
									|| Profession.contains("Pikeman")) {
								Profession = "polearm_trainer";
							} else if (Profession.contains("Weaponsmith")) {
								Profession = "weaponsmith_trainer";
							} else if (Profession.contains("Musician")) {
								Profession = "musician_trainer";
							} else if (Profession.contains("Hunter")) {
								Profession = "bountyhunter_trainer";
							} else if (Profession.contains("Bio")) {
								Profession = "bioengineer_trainer";
							} else if (Profession.contains("Droid")) {
								Profession = "droidengineer_trainer";
							} else if (Profession.contains("Fencer")) {
								Profession = "1handsword_trainer";
							} else if (Profession.contains("Pistoleer")) {
								Profession = "pistol_trainer";
							} else if (Profession.contains("Swordsman")) {
								Profession = "2handsword_trainer";
							} else if (Profession.contains("Handler")) {
								Profession = "creaturehandler_trainer";
							} else if (Profession.contains("Architect")) {
								Profession = "architect_trainer";
							} else if (Profession.contains("Armorsmith")) {
								Profession = "armorsmith_trainer";
							} else if (Profession.contains("Artisan")) {
								Profession = "artisan_trainer";
							} else if (Profession.contains("Brawler")) {
								Profession = "brawler_trainer";
							} else if (Profession.contains("Chef")) {
								Profession = "chef_trainer";
							} else if (Profession.contains("Entertainer")) {
								Profession = "entertainer_trainer";
							} else if (Profession.contains("Marksman")) {
								Profession = "marksman_trainer";
							} else if (Profession.contains("_Medic")) {
								Profession = "medic_trainer";
							} else if (Profession.contains("Merchant")) {
								Profession = "merchant_trainer";
							} else if (Profession.contains("Tailor")) {
								Profession = "tailor_trainer";
							} else if (Profession.contains("Squad")) {
								Profession = "squad_leader_trainer";
							} else if (Profession.contains("Carbine")) {
								Profession = "carbine_trainer";
							} else if (Profession.contains("Dancer")) {
								Profession = "dancer_trainer";
							} else if (Profession.contains("Rifleman")) {
								Profession = "rifleman_trainer";
							} else if (Profession.contains("Smuggler")) {
								Profession = "smuggler_trainer";
							} else if (Profession.contains("Image")) {
								Profession = "image_designer_trainer";
							} else if (Profession.contains("Doctor")) {
								Profession = "doctor_trainer";
							} else if (Profession.contains("Ranger")) {
								Profession = "ranger_trainer";
							}
							Vector<ItemTemplate> tL = getSimilarTemplates("dressed_"
									+ Profession);

							if (tL != null) {
								int SelectedTemplate = SWGGui.getRandomInt(0,
										tL.size());
								if (tL.size() >= 2) {
									// System.out.println("Selected Template " +
									// SelectedTemplate);
									template = tL.get(SelectedTemplate);
									if (template != null) {
										// t.setRadialMenus(template.getRadialMenuItems());
										// // Feature being implemented.
										// t.setSTFFileName(template.getSTFFileName());
										t.setIFFFileName(template
												.getIFFFileName());
										// System.out.println("Randomized Terminal iff filename: "
										// + t.getIFFFileName());
										// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
										// t.setSTFDetailName(template.getSTFDetailName());
										// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
										t.setTemplateID(template
												.getTemplateID());
									}
								}
							}
						}
						retval.add(t);

					} else {
						// else
					}
				}
			}

		} catch (Exception e) {
			System.out.println("Error Retrieving Terminal Database: "
					+ e.toString());
			e.printStackTrace();
		}
		DataLog.logEntry("Server Terminals Loaded: " + retval.size(),
				"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true, true);
		return retval;
	}

	public Vector<Terminal> LoadServerTerminals(ZoneServer server,
			long TerminalID) {

		Vector<Terminal> retval = new Vector<Terminal>();
		String query = "Select * from `terminals` where `spawn` = 1 and `packed` = 0 and `destroy` = 0 and terminals_id = "
				+ TerminalID;
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					// Are we going to have different tables for different types
					// of terminals, or is the current Terminal table going to
					// be altered to be more generic?
					// Same Table different ways of putting data into the
					// terminal object based on its purpose. - PF
					if (result.getInt("type") == Constants.TERMINAL_TYPES_TRAVEL_GENERAL
							|| result.getInt("type") == Constants.TERMINAL_TYPES_TICKET_DROID
							|| result.getInt("type") == Constants.TERMINAL_TYPES_CHARACTER_BUILDER
							|| result.getInt("type") == Constants.TERMINAL_TYPES_SKILL_TRAINER
							|| result.getInt("type") == Constants.TERMINAL_TYPES_SHUTTLE_PORT
							|| result.getInt("type") == Constants.TERMINAL_TYPES_TERMINAL_BANK
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_4
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_32
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_64
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_128
							|| result.getInt("type") == Constants.TERMINAL_TYPES_NOBUILD_ZONE_768) {
						Terminal t = new Terminal();
						t.setServer(server);
						t.setFirstName(result.getString("terminals_name"));
						t.setTerminalType(result.getInt("type"));
						t.setTicketID(result.getInt("ticketid"));
						t.setLocationID(result.getInt("ticketid"));
						t.setPortID(result.getInt("ticketid"));
						t.setIsFactionTerminal(result.getByte("factionindex"));
						t.setDBID(result.getLong("terminals_id"));
						t.setCRC((int) result.getLong("terminals_crc"));
						long cellID = result.getLong("cellID");
						t.setScriptName(result.getString("script"));
						t.setCellID(cellID);

						t.setPlanetID(result.getInt("planet_id"));
						t.setFactionID(result.getInt("factionindex"));
						t.setCREO3Bitmask(0x108); // Maybe?
						if (cellID != 0) {
							Cell c = (Cell) server
									.getObjectFromAllObjects(cellID);
							Structure building = c.getBuilding();
							float cellX = result.getFloat("x");
							float cellY = result.getFloat("y");
							float cellZ = result.getFloat("z");
							t.setX(building.getX() + cellX);
							t.setY(building.getY() + cellY);
							t.setZ(building.getZ() + cellZ);
							t.setCellX(cellX);
							t.setCellY(cellY);
							t.setCellZ(cellZ);
						} else {
							t.setX(result.getFloat("x"));
							t.setY(result.getFloat("y"));
							t.setZ(result.getFloat("z"));
						}
						t.setOrientationN(result.getFloat("oI"));
						t.setOrientationS(result.getFloat("oJ"));
						t.setOrientationE(result.getFloat("oK"));
						t.setOrientationW(result.getFloat("oW"));
						t.setID(t.getDBID());
						t.setCustomizationData(null);
						// t.addBitToPVPStatus(Constants.PVP_STATUS_IS_NPC);
						t.setPVPStatus(0);
						t.setStance(null, Constants.STANCE_STANDING, true);
						t.clearAllStates(false);
						t.setMoodID(0);
						// System.out.println("Terminal retrieved " +
						// t.getID());
						ItemTemplate template = getTemplateDataByCRC(t.getCRC());
						if (template != null) {
							// t.setRadialMenus(template.getRadialMenuItems());
							// // Feature being implemented.
							// t.setSTFFileName(template.getSTFFileName());
							t.setIFFFileName(template.getIFFFileName());
							// System.out.println("Terminal iff filename: " +
							// t.getIFFFileName());
							// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
							// t.setSTFDetailName(template.getSTFDetailName());
							// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
							t.setTemplateID(template.getTemplateID());
						}
						if (t.getIFFFileName().contains("trainer")) {
							String[] TrainerName = server
									.generateRandomName("human");
							String CurrentName = t.getFullName();
							// String[] Titles = CurrentName.split(" ");
							String Profession = CurrentName.replace(" ", "_");
							TrainerName[0] = TrainerName[0].replace(" ", "");
							TrainerName[1] = TrainerName[1].replace(" ", "");
							t.setFirstName(TrainerName[0]);
							t.setLastName(TrainerName[1] + " (" + CurrentName
									+ ")");
							// System.out.println("Randomizing Trainer Template");
							if (Profession.contains("Teras")) {
								Profession = "unarmed_trainer";
							} else if (Profession.contains("Combat")) {
								Profession = "combatmedic_trainer";
							} else if (Profession.contains("Bio")) {
								Profession = "bioengineer_trainer";
							} else if (Profession.contains("Droid")) {
								Profession = "droidengineer_trainer";
							} else if (Profession.contains("Fencer")) {
								Profession = "1handsword_trainer";
							} else if (Profession.contains("Pistoleer")) {
								Profession = "pistol_trainer";
							} else if (Profession.contains("Swordsman")) {
								Profession = "2handsword_trainer";
							} else if (Profession.contains("Handler")) {
								Profession = "creaturehandler_trainer";
							} else if (Profession.contains("Pikeman")) {
								Profession = "polearm_trainer";
							} else if (Profession.contains("Squad")) {
								Profession = "squad_leader_trainer";
							}
							// System.out.println("Profession: " + Profession);
							Vector<ItemTemplate> tL = getSimilarTemplates("dressed_"
									+ Profession);

							if (tL != null && !tL.isEmpty()) {
								int SelectedTemplate = SWGGui.getRandomInt(0,
										tL.size());
								if (tL.size() >= 2) {
									// System.out.println("Selected Template " +
									// SelectedTemplate);
									template = tL.get(SelectedTemplate);
									if (template != null) {
										// t.setRadialMenus(template.getRadialMenuItems());
										// // Feature being implemented.
										// t.setSTFFileName(template.getSTFFileName());
										t.setIFFFileName(template
												.getIFFFileName());
										// System.out.println("Randomized Terminal iff filename: "
										// + t.getIFFFileName());
										// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
										// t.setSTFDetailName(template.getSTFDetailName());
										// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
										t.setTemplateID(template
												.getTemplateID());
									}
								}
							} else {
								DataLog.logEntry(
										"Terminal Template not found for: "
												+ t.getID(),
										"DatabaseInterface",
										Constants.LOG_SEVERITY_CRITICAL, true,
										true);
							}
						}
						retval.add(t);

					} else if (result.getInt("type") == Constants.TERMINAL_TYPES_TERMINAL_ELEVATOR_UP) {
						Terminal t = new Terminal();
						t.setServer(server);
						t.setFirstName(result.getString("terminals_name"));
						t.setTerminalType(result.getInt("type"));
						t.setTicketID(result.getInt("ticketid"));
						t.setLocationID(result.getInt("ticketid"));
						t.setPortID(result.getInt("ticketid"));
						t.setIsFactionTerminal(result.getByte("factionindex"));
						t.setDBID(result.getLong("terminals_id"));
						t.setCRC((int) result.getLong("terminals_crc"));
						long cellID = result.getLong("cellID");
						t.setScriptName(result.getString("script"));
						t.setCellID(cellID);

						t.setPlanetID(result.getInt("planet_id"));
						t.setFactionID(result.getInt("factionindex"));
						t.setCREO3Bitmask(0x108); // Maybe?
						if (cellID != 0) {
							Cell c = (Cell) server
									.getObjectFromAllObjects(cellID);
							Structure building = c.getBuilding();
							float cellX = result.getFloat("x");
							float cellY = result.getFloat("y");
							float cellZ = result.getFloat("z");
							t.setX(building.getX() + cellX);
							t.setY(building.getY() + cellY);
							t.setZ(building.getZ() + cellZ);
							t.setCellX(cellX);
							t.setCellY(cellY);
							t.setCellZ(cellZ);
						} else {
							t.setX(result.getFloat("x"));
							t.setY(result.getFloat("y"));
							t.setZ(result.getFloat("z"));
						}
						t.setOrientationN(result.getFloat("oI"));
						t.setOrientationS(result.getFloat("oJ"));
						t.setOrientationE(result.getFloat("oK"));
						t.setOrientationW(result.getFloat("oW"));
						t.setID(t.getDBID());
						t.setCustomizationData(null);
						// t.addBitToPVPStatus(Constants.PVP_STATUS_IS_NPC);
						t.setPVPStatus(0);
						t.setStance(null, Constants.STANCE_STANDING, true);
						t.clearAllStates(false);
						t.setMoodID(0);
						// System.out.println("Terminal retrieved " +
						// t.getID());
						ItemTemplate template = getTemplateDataByCRC(t.getCRC());
						if (template != null) {
							// t.setRadialMenus(template.getRadialMenuItems());
							// // Feature being implemented.
							// t.setSTFFileName(template.getSTFFileName());
							t.setIFFFileName(template.getIFFFileName());
							// System.out.println("Terminal iff filename: " +
							// t.getIFFFileName());
							// t.setSTFFileIdentifier(template.getSTFFileIdentifier());
							// t.setSTFDetailName(template.getSTFDetailName());
							// t.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
							t.setTemplateID(template.getTemplateID());
						}
						retval.add(t);
					} else {
						// else
					}
				}
			}

		} catch (Exception e) {
			System.out.println("Error Retrieving Terminal Database: "
					+ e.toString());
			e.printStackTrace();
		}
		DataLog.logEntry("Server Terminals Loaded: " + retval.size(),
				"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true, true);
		return retval;
	}

	public Vector<TravelDestination> getAllTravelDestinations() {

		Vector<TravelDestination> retval = new Vector<TravelDestination>();

		String query = "Select * from `tickets` order by `id`";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					TravelDestination t = new TravelDestination(result
							.getString("Destination"), result
							.getInt("planet_id"), result.getInt("id"), result
							.getBoolean("shuttleport"), result.getFloat("x"),
							result.getFloat("y"), result.getFloat("z"));
					t.setIsPlayerCityShuttle(result
							.getBoolean("playercityshuttle"));
					t.setIsStarPort(result.getBoolean("starport"));
					t.setTicketID(result.getInt("id"));

					if (t.getIsPlayerCityShuttle()) {
						t.setCost(result.getInt("cost"));
					} else {
						t.setCost(0);
					}
					retval.add(t);
				}
			}
		} catch (Exception e) {
			System.out.println("Error Retrieving Ticket Database: "
					+ e.toString());
		}

		return retval;
	}

	public TicketPriceMatrix getAllTicketPrices() {

		TicketPriceMatrix retval = new TicketPriceMatrix();

		String query = "Select * from `ticket_prices`";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					retval.setTicketPrice(result.getInt("fromplanetid"), 0,
							result.getInt("corellia"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 1,
							result.getInt("dantooine"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 2,
							result.getInt("dathomir"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 3,
							result.getInt("endor"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 4,
							result.getInt("lok"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 5,
							result.getInt("naboo"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 6,
							result.getInt("rori"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 7,
							result.getInt("talus"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 8,
							result.getInt("tatooine"));
					retval.setTicketPrice(result.getInt("fromplanetid"), 9,
							result.getInt("yavin4"));
				}
			}
		} catch (Exception e) {
			System.out.println("Error Retrieving Ticket Price Database: "
					+ e.toString());
		}
		return retval;
	}

	public Vector<Shuttle> loadShuttles() {
		Vector<Shuttle> retval = new Vector<Shuttle>();
		String query = "Select * from `shuttles`";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					Shuttle shuttle = new Shuttle(result.getInt("terminalid"),
							result.getInt("shuttletype"), result
									.getFloat("landingX"), result
									.getFloat("landingY"), result
									.getFloat("landingZ"), result
									.getInt("planetid"));
					int templateID = result.getInt("templateid");
					shuttle.setTemplateID(templateID);
					ItemTemplate template = getTemplateDataByID(templateID);
					shuttle.setCRC(template.getCRC());

					shuttle.setPVPStatus(0);
					shuttle.setCREO3Bitmask(Constants.BITMASK_CREO3_TERMINAL);
					shuttle.setFactionID(0);
					if (template != null) {
						shuttle.setIFFFileName(template.getIFFFileName());
						// shuttle.setSTFFileIdentifier(template.getSTFFileIdentifier());
						// shuttle.setSTFDetailName(template.getSTFDetailName());
						// shuttle.setSTFDetailIdentifier(template.getSTFDetailIdentifier());
						shuttle.setFirstName("Transport Shuttle");
					}
					shuttle.setCellID(result.getLong("cellid"));
					shuttle
							.setScheduleTimer(((result.getInt("timer") * 60) * 1000));
					shuttle.setTimeToDeparture(50000);
					shuttle.setTimeToArrival(200000);
					shuttle.setX(shuttle.getLandingX());
					shuttle.setY(shuttle.getLandingY());
					shuttle.setZ(shuttle.getLandingZ());
					if (shuttle.getCellID() != 0) {
						shuttle.setCellX(shuttle.getLandingX());
						shuttle.setCellY(shuttle.getLandingY());
						shuttle.setCellZ(shuttle.getLandingZ());
					}
					shuttle.setPlanetID(result.getInt("planetid"));
					shuttle.setOrientationN(result.getFloat("oI"));
					shuttle.setOrientationS(result.getFloat("oJ"));
					shuttle.setOrientationE(result.getFloat("oK"));
					shuttle.setOrientationW(result.getFloat("oW"));
					retval.add(shuttle);
				}
			}

		} catch (Exception e) {
			System.out.println("Exception caugth in loadShuttles() " + e);
			e.printStackTrace();
		}
		return retval;
	}

	protected Hashtable<Integer, ItemTemplate> getAllItemTemplateData() {
		return vItemTemplateData;
	}

	protected boolean insertSkillTrainer(long TrainerID, int TemplateID,
			String TrainerName, float x, float y, float z, float oI, float oJ,
			float oK, float oW, long cellID, int planetid) {
		boolean retval = false;
		ItemTemplate T = getTemplateDataByID(TemplateID);
		if (T != null) {
			// Insert Into `terminals` Values( 3369512235 , 111428352 , 'Artisan
			// Trainer' ,0,0,-0.29341787,0,0.95598435,-2890.6702,5.0,2144.118
			// ,'SkillTrainer.js', 8 ,0 ,0,11,0,0,1,null,0,-1,0);
			String statement = "Insert into `terminals` Values( " + TrainerID
					+ "," + (int) T.getCRC() + ",'" + TrainerName + "',0,0,"
					+ oJ + ",0," + oW + "," + x + "," + z + "," + y
					+ ",'SkillTrainer.js'," + planetid + "," + cellID
					+ ",0,11,0,0,1,null,0,-1,0 );";

			try {
				PreparedStatement s = conn.prepareStatement(statement);
				boolean res = s.execute();
				s.close();
				s = null;
				if (!res) {
					retval = true;
				}
			} catch (Exception e) {
				System.out.println("Error insertSkillTrainer location: "
						+ e.toString());
				e.printStackTrace();
			}

		}
		return retval;
	}

	protected boolean insertTerminal(long TerminalID, int TemplateID,
			String TerminalName, float x, float y, float z, float oI, float oJ,
			float oK, float oW, long cellID, int planetid, int terminalType) {
		boolean retval = false;
		ItemTemplate T = getTemplateDataByID(TemplateID);
		if (T != null) {
			// Insert Into `terminals` Values( 3369512235 , 111428352 , 'Artisan
			// Trainer' ,0,0,-0.29341787,0,0.95598435,-2890.6702,5.0,2144.118
			// ,'SkillTrainer.js', 8 ,0 ,0,11,0,0,1,null,0,-1,0);
			String statement = "Insert into `terminals` Values( " + TerminalID
					+ "," + (int) T.getCRC() + ",'" + TerminalName + "',0,0,"
					+ oJ + ",0," + oW + "," + x + "," + z + "," + y
					+ ",'SkillTrainer.js'," + planetid + "," + cellID + ",0,"
					+ terminalType + ",0,0,1,null,0,-1,0 );";

			try {
				PreparedStatement s = conn.prepareStatement(statement);
				boolean res = s.execute();
				s.close();
				s = null;
				if (!res) {
					retval = true;
				}
			} catch (Exception e) {
				System.out.println("Error insertTerminal location: "
						+ e.toString());
				e.printStackTrace();
			}

		}
		return retval;
	}

	protected static Vector<POI> getPOIListForPlanetID(int planetID) {
		if (planetID > 9) {
			return new Vector<POI>();
		}
		return vPOIsByPlanetID.get(planetID);
	}

	protected static void loadPOIList() {

		String query = "Select * from `poibadgelocations`";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					// public POI(float px, float py, int pplanet, int pbid){
					int planetID = result.getInt("planetID");
					Vector<POI> vPOIsThisPlanet = vPOIsByPlanetID.get(planetID);
					if (vPOIsThisPlanet == null) {
						vPOIsThisPlanet = new Vector<POI>();
						vPOIsByPlanetID.put(planetID, vPOIsThisPlanet);
					}
					vPOIsThisPlanet
							.add(new POI(result.getFloat("x"), result
									.getFloat("y"), planetID, result
									.getInt("badgeid")));
				}
			}
		} catch (Exception e) {
			System.out.println("Error getAllPOI " + e.toString());
			e.printStackTrace();
		}
	}

	protected static void updateGalaxyStatus(int galaxyID, int status,
			int playerCount) {
		String statement = "Update `galaxy` Set `Status` = " + status
				+ ", `population` = " + playerCount + "  Where `id` = "
				+ galaxyID;
		// System.out.println(statement);
		try {
			PreparedStatement s = conn.prepareStatement(statement);
			boolean res = s.execute();
			s.close();
			s = null;
			if (!res) {
				loadZoneServerData();
			}
		} catch (Exception e) {
			System.out.println("Error updateGalaxyStatus()" + e);
			e.printStackTrace();
		}
	}

	protected Vector<NPCTemplate> getNPCTemplates() {
		Vector<NPCTemplate> retval = new Vector<NPCTemplate>();
		String query = "Select * from `npc_template`";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					NPCTemplate T = new NPCTemplate();
					T.setIFFFileName(result.getString("iff"));
					T.setCRC(result.getInt("crc"));
					T.setappearance_cdf(result.getString("appearance_cdf"));
					T.setcrc_cdf(result.getInt("crc_cdf"));
					T.setsdbName(result.getString("name"));
					T.setexamine_information(result
							.getString("examine_information"));
					T.setHam(0, result.getInt("health"));
					T.setHam(3, result.getInt("action"));
					T.setHam(6, result.getInt("mind"));
					T.setMinDamage(result.getInt("mindmg"));
					T.setMaxDamage(result.getInt("maxdmg"));
					T.setArmor(result.getInt("armor"));
					// `resistances` blob, need to figure this field how to get
					T.setWeaponiff(result.getString("weaponiff"));
					T.setWeaponCRC(result.getInt("weaponcrc"));
					T.setConsiderRating(result.getInt("consider_rating"));
					T.setDefaultScript(result.getString("default_script"));
					T.setFactionFlag(result.getInt("faction_flag"));
					T.setNPCBitmask(result.getInt("npc_bitmask"));
					retval.add(T);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception in getNPCTemplates " + e);
			e.printStackTrace();
		}

		return retval;
	}

	protected Vector<String> getTesterPunchList() {
		Vector<String> retval = new Vector<String>();
		String query = "Select * from `testerpunchlist` order by `priority`;";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					String I = "";
					I += "ID:";
					I += result.getInt("id");
					I += ":Priority:";
					I += result.getInt("priority");
					I += " : ";
					I += result.getString("testitem");
					retval.add(I);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception Caught in getTesterPunchList " + e);
			e.printStackTrace();
		}

		return retval;
	}

	protected boolean insertPunchListItem(int iPriority, String sItem) {
		boolean retval = false;
		String statement = "Insert Into `testerpunchlist` Values(null,'"
				+ sItem + "'," + iPriority + ");";
		// System.out.println(statement);
		try {
			PreparedStatement s = conn.prepareStatement(statement);
			boolean res = s.execute();
			s.close();
			s = null;
			if (!res) {
				retval = true;
			}
		} catch (Exception e) {
			System.out.println("Error insertPunchListItem location: "
					+ e.toString());
			e.printStackTrace();
		}
		return retval;
	}

	protected boolean removePunchListItem(int iItem) {
		boolean retval = false;
		String statement = "Delete From `testerpunchlist` Where `id` = "
				+ iItem + " ;";
		// System.out.println(statement);
		try {
			PreparedStatement s = conn.prepareStatement(statement);
			boolean res = s.execute();
			s.close();
			s = null;
			if (!res) {
				retval = true;
			}
		} catch (Exception e) {
			System.out.println("Error removePunchListItem location: "
					+ e.toString());
			e.printStackTrace();
		}
		return retval;
	}

	protected boolean insertBugReport(long playerid, int accountid,
			String report) {
		boolean retval = false;
		if (report.contains("select * from")) {
			report = report
					.replace("select * from", "-replaced-sql-statement-");
		}
		String statement = "Insert Into `bugreports` Values(null," + playerid
				+ "," + accountid + "," + System.currentTimeMillis() + ",'"
				+ report + "');";
		// System.out.println(statement);
		try {
			PreparedStatement s = conn.prepareStatement(statement);
			boolean res = s.execute();
			s.close();
			s = null;
			if (!res) {
				retval = true;
			}
		} catch (Exception e) {
			System.out.println("Error insertBugReport location: "
					+ e.toString());
			e.printStackTrace();
		}
		return retval;
	}

	protected Vector<LairTemplate> getAllLairTemplates() {
		Vector<LairTemplate> retval = new Vector<LairTemplate>();
		String query = "Select * from `lair_template` order by `id`;";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					LairTemplate template = new LairTemplate();

					template.setILairTemplate(result.getInt("lair_template"));
					template.setIMob1Template(result.getInt("mob1_template"));
					template.setIMob2Template(result.getInt("mob2_template"));
					template.setIMob3Template(result.getInt("mob3_template"));
					template.setIMob4Template(result.getInt("mob4_template"));
					template.setIMob5Template(result.getInt("mob5_template"));
					template.setIMob6Template(result.getInt("mob6_template"));
					template.setIMob7Template(result.getInt("mob7_template"));
					template.setIMob8Template(result.getInt("mob8_template"));
					template.setIMob9Template(result.getInt("mob9_template"));
					template.setIBabyTemplate(result.getInt("baby_template"));
					template
							.setBSpawnsBabies(result.getBoolean("SpawnsBabies"));
					template.setIMaxWaves(result.getInt("MaxWaves"));
					template.setIMaxPerWave(result.getInt("MaxPerWave"));
					template.setIMaxBabies(result.getInt("MaxBabies"));
					template.setBSpawnsBoss(result.getBoolean("SpawnsBoss"));
					template.setIBossTemplate(result.getInt("boss_template"));
					template.setBSpawnsLoot(result.getBoolean("spawns_loot"));
					template.setILootTableId(result.getInt("loot_table_id"));
					template.setILairMaxCondition(result
							.getInt("lair_max_condition"));
					template.setILairHealIncrement(result
							.getInt("lair_heal_increment"));
					template.setBMobIsAgressive(result
							.getBoolean("mob_is_agressive"));
					template
							.setBMobIsSocial(result.getBoolean("mob_is_social"));
					template.setILairAiId(result.getInt("lair_ai_id"));
					template.setIMobAiId(result.getInt("mob_ai_id"));
					template.setSLlairName(result.getString("lair_name"));
					template.setSAllowedPlanetList(result
							.getString("allowedplanetlist"));
					template.setIMaxMobHam(result.getInt("max_mob_ham"));
					template.setILairDBID(result.getInt("id"));
					template.setSMobMaleName(result.getString("mob_male_name"));
					template.setSMobFemaleName(result
							.getString("mob_female_name"));
					template.setSMobBabyName(result.getString("mob_baby_name"));
					template.setSMobBossName(result.getString("mob_boss_name"));
					template.setSMobNameStfFile(result
							.getString("mob_name_stf_file"));
					template.setHideType(result.getInt("hide_type"));
					template.setBoneType(result.getInt("bone_type"));
					template.setMeatType(result.getInt("meat_type"));
					template.setMilkType(result.getInt("milk_type"));
					retval.add(template);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception Caught in getAllLairTemplates " + e);
			e.printStackTrace();
		}

		return retval;
	}

	// protected Hashtable<>

	public void updateHeigtAtLocation(short x, short y, short z, short planetid) {
		String statement = "Update `heightmap` Set `z` = " + z
				+ " Where `x` = " + x + " and `y` = " + y
				+ " and `planetid` = " + planetid + ";";
		try {
			PreparedStatement s = conn.prepareStatement(statement);
			boolean res = s.execute();
			s.close();
			s = null;
			if (!res) {

			}
		} catch (Exception e) {
			System.out
					.println("Error insertHeightAtLocation : " + e.toString());
			e.printStackTrace();
		}
	}

	public void insertHeightAtLocation(float x, float y, short z, int planetid) {

		String statement = "Insert Into `heightmap` Values(null," + planetid
				+ "," + x + "," + y + "," + z + ");";
		try {
			PreparedStatement s = conn.prepareStatement(statement);
			boolean res = s.execute();
			s.close();
			s = null;
			if (!res) {

			}
		} catch (Exception e) {
			System.out
					.println("Error insertHeightAtLocation : " + e.toString());
			e.printStackTrace();
		}
	}

	public short[][][] retrieveHeightMap(int planetid) {

		String query = "";
		int iplanetcount = 0;
		switch (planetid) {
		case -1: {
			query = "Select * from `heightmap` order by `id`;";
			iplanetcount = 10;
			break;
		}
		default: {
			query = "Select * from `heightmap` where `planetid` = " + planetid
					+ " order by `id`;";
			iplanetcount = 1;
		}
		}
		short[][][] retval = new short[iplanetcount][255][255];

		int ctr = 0;
		try {
			Statement s = conn.createStatement();

			if (s.execute(query)) {
				ResultSet result = s.getResultSet();
				while (result.next()) {
					retval[result.getByte("planetid")][result.getShort("x")][result
							.getShort("y")] = result.getShort("z");
					ctr++;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception Caught in getAllLairTemplates " + e);
			e.printStackTrace();
		}
		DataLog.logEntry("Retrieved " + ctr + " Heightmap Entries",
				"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true, true);
		// buildMeterHeightMapTable();
		return retval;
	}

	/*
	 * public static void buildMeterHeightMapTable(){ try{
	 * System.out.println("Meter height map Begin Please Wait."); String query
	 * =""; Statement s = conn.createStatement(); File hmm = new
	 * File(".\\HMM.txt"); hmm.createNewFile(); FileWriter W = new
	 * FileWriter(hmm); for(int p = 0; p < 10; p++) { for(float x = 7680; x >
	 * -7680; x--) { for(float y = 7680; y > -7680; y--) { //PRIMARY KEY
	 * (`x`,`y`,`z`,`planetID`) query = "Insert Into `meterheightmap` Values(" +
	 * x + "," + y + ",32767.0," + p + ");\r\n"; //System.out.println(query);
	 * try{ //s.execute(query);
	 * 
	 * W.write(query);
	 * 
	 * }catch(Exception InsertException){
	 * //DataLog.logException("InsertException", "DatabaseInterface",true, true,
	 * InsertException); } } }
	 * System.out.println("Meter height map completed."); W.close(); }
	 * }catch(Exception e){
	 * DataLog.logException("Error While Building Meter HeightMap table",
	 * "DatabaseInterface",true, true, e); } }
	 */
	/**
	 * This will return the correct height at the x z coordinates indicated for
	 * the planet indicated. X and Z are passed in as ints so that we get the
	 * nearest coordinate ending in .0
	 * 
	 * @param x
	 * @param z
	 *            : Note: this should be y but we use Z as Y and Y as Z
	 * @param planetid
	 * @return
	 */
	/*
	 * public static float getHeightAtCoordinates(int x, int z, int planetid){
	 * try{ String query = "Select z from `meterheightmap` Where x = " +
	 * (float)x + " And y = " + (float)z + " And planedid = " + planetid + ";";
	 * Statement s = conn.createStatement(); if(s.execute(query)) { ResultSet r
	 * = s.getResultSet(); return r.getFloat("z"); } return -32767.0f; //this
	 * should never happen but just in case. }catch(Exception e){
	 * DataLog.logException("Error Retrieving Height for Coordinates",
	 * "DatabaseInterface", ZoneServer.ZoneRunOptions.bLogToConsole, true, e); }
	 * return 32767.0f; }
	 */
	protected Vector<MissionTemplate> getMissionTemplates(int iTerminalType,
			int iPlanetID) {
		Vector<MissionTemplate> retval = new Vector<MissionTemplate>();
		try {
			Enumeration<MissionTemplate> mT = vMissionTemplateData.elements();
			while (mT.hasMoreElements()) {
				MissionTemplate t = mT.nextElement();
				if (t.getMissionTerminalType() == iTerminalType) {
					int[] pl = t.getMissionRequiredPlanet();
					for (int i = 0; i < pl.length; i++) {
						if (pl[i] == iPlanetID) {
							if (!retval.contains(t)) {
								retval.add(t);
							}
						} else if (pl[i] == -1) {
							if (!retval.contains(t)) {
								retval.add(t);
								i = pl.length + 1;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Exception Caught in getMissionTemplates( T:"
					+ iTerminalType + ", P:" + iPlanetID + " " + e);
			e.printStackTrace();
		}
		return retval;
	}

	private static void loadMissionCollateral() {
		try {
			String query = "Select * from `mission_collateral`;";
			Statement s = conn.createStatement();

			if (s.execute(query)) {
				ResultSet r = s.getResultSet();

				while (r.next()) {
					MissionCollateral c = new MissionCollateral();
					c.setId(r.getInt("id"));
					c.setMissionid(r.getInt("missionid"));
					c.setCollateralid(r.getInt("collateralid"));
					c.setDisplaycrc((int) r.getLong("displaycrc"));
					c.setDisplaytext(r.getString("displaytext"));
					c.setSchematicid((int) r.getLong("schematicid"));
					c.setEntertainerCellID(r.getLong("entertainerCellID"));
					c.setEntertainerPlanetID(r.getInt("entertainerPlanetID"));
					c.setEntertainerX(r.getFloat("entertainerX"));
					c.setEntertainerY(r.getFloat("entertainerY"));
					c.setEntertainerZ(r.getFloat("entertainerZ"));
					vMissionCollateralData.put(c.getId(), c);
				}
			} else {
				DataLog.logEntry(
						"Unable to load Mission data, disabling missions.",
						"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true,
						true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected MissionCollateral getMissionCollateral(int missionid,
			int collateralid) {
		MissionCollateral retval = null;
		try {
			Enumeration<MissionCollateral> mC = vMissionCollateralData
					.elements();
			while (mC.hasMoreElements()) {
				MissionCollateral c = mC.nextElement();
				if (c.getMissionid() == missionid
						&& c.getCollateralid() == collateralid) {
					return c;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception Caught in getMissionCollateral " + e);
			e.printStackTrace();
		}
		return retval;
	}

	protected Vector<MissionCollateral> getMissionCollateralVector(
			int missionid, int planetid) {
		Vector<MissionCollateral> retval = new Vector<MissionCollateral>();
		try {
			Enumeration<MissionCollateral> mC = vMissionCollateralData
					.elements();
			while (mC.hasMoreElements()) {
				MissionCollateral c = mC.nextElement();
				if (c.getMissionid() == missionid
						&& c.getEntertainerPlanetID() == planetid) {
					retval.add(c);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Exception Caught in getMissionCollateralVector "
							+ e);
			e.printStackTrace();
		}
		return retval;
	}

	public static void correctPlanetIDOnWorldObjects() {
		try {
			// System.out.println("Correcting Planet id's on World Objects that Have PlanetID 255");
			String query1 = "Select * from `worldobjects` where `planetid` = 255 and parent != 0;";
			Statement s1 = conn.createStatement();
			Statement s2 = conn.createStatement();
			Statement s3 = conn.createStatement();
			int corrections = 0;
			if (s1.execute(query1)) {
				ResultSet r1 = s1.getResultSet();
				while (r1.next()) {
					long parentid = r1.getLong("parent");
					// long objectid = r1.getLong("id");
					// System.out.println("Found Object ID:" + objectid +
					// " ParentID:" + parentid +
					// " With planetid 255 - Attempting correction");
					String query2 = "Select * from `worldobjects` where id = "
							+ parentid;
					if (s2.execute(query2)) {
						ResultSet r2 = s2.getResultSet();
						while (r2.next()) {
							int parentPlanet = r2.getInt("planetid");
							// System.out.println("Found Parent PlanetID:" +
							// parentPlanet + " Updating Child Object");
							if (parentPlanet != 255) {
								String query3 = "Update `worldobjects` set `planetid` = "
										+ parentPlanet
										+ " where `parent` = "
										+ parentid + ";";
								if (s3.execute(query3)) {
									// System.out.println("Correction Successful.");
								} else {
									// System.out.println("Correction Success.");
									corrections++;
								}
							} else {
								// System.out.println("Parent Object had planet id of 255 - After routine runs try again.");
							}
						}
					} else {
						// System.out.println("Parent Object Not Found.");
					}
				}
			}
			// System.out.println("Corrrection Routine Done. Items Corrected:" +
			// corrections);
		} catch (Exception e) {
			System.out.println("Exception while correctPlanetIDOnWorldObjects "
					+ e);
			e.printStackTrace();
		}
	}

	public void updateWorldObjectPlanet(long worldObjectID, int planetID) {
		try {
			String query = "Update `worldobjects` Set `planetid` = " + planetID
					+ " Where id = " + worldObjectID + ";";
			Statement s1 = conn.createStatement();
			if (!s1.execute(query)) {
				// System.out.println("World Object Planet Update Successful ID:"
				// + worldObjectID + " New Planet ID:" + planetID);
			}
		} catch (Exception e) {
			System.out.println("Exception Caught In updateWorldObjectPlanet "
					+ e);
			e.printStackTrace();
		}
	}

	protected void updateDynamicLairSpawn(DynamicLairSpawn lairSpawn,
			int iPlanetID, int iServerID) {
		String query;
		try {
			Statement s = conn.createStatement();
			query = "Update `DynamicLairSpawn` set `minNumToSpawn` = "
					+ lairSpawn.getMinNumToSpawn() + ", `maxNumToSpawn` = "
					+ lairSpawn.getMaxNumToSpawn()
					+ ", `minPlayersToTrigger` = "
					+ lairSpawn.getNumPlayersBeforeSpawn() + " where `id` = "
					+ lairSpawn.getSpawnID() + ";";
			s.execute(query);
			s.close();
			s = null;
		} catch (Exception e) {
			System.out.println("Error updating DynamicLairSpawn: "
					+ e.toString());
			e.printStackTrace();
		}

	}

	protected void saveDynamicLairSpawn(DynamicLairSpawn lairSpawn,
			int iServerID) {
		String query = null;
		Rectangle2D spawnBoundaries = lairSpawn.getBoundaries();
		int leftX = (int) spawnBoundaries.getX();
		int topY = (int) spawnBoundaries.getY();
		int rightX = (int) (leftX + spawnBoundaries.getWidth());
		int bottomY = (int) (topY + spawnBoundaries.getHeight());
		try {
			Statement s = conn.createStatement();
			// Get the next spawn ID.
			s.execute("Select max(`id`) from `DynamicLairSpawn`");
			ResultSet r = s.getResultSet();
			int spawnID = 1;
			if (r.next()) {
				try {
					spawnID = r.getInt(1) + 1;
				} catch (SQLException ee) {
					// Must not be any entries here.
				}
			}
			query = "Insert into `DynamicLairSpawn` values (" + spawnID + ", "
					+ lairSpawn.getSpawnTemplateID() + ", "
					+ lairSpawn.getMinNumToSpawn() + ", "
					+ lairSpawn.getMaxNumToSpawn() + ", "
					+ lairSpawn.getNumPlayersBeforeSpawn() + ", " + leftX
					+ ", " + topY + ", " + rightX + ", " + bottomY + ", "
					+ lairSpawn.getPlanetID() + ", " + iServerID + ", "
					+ lairSpawn.getColor() + ");";

			s.execute(query);
			s.close();
			s = null;

		} catch (SQLException e) {
			System.out
					.println("SQL Exception caught saving new DynamicLairSpawn: "
							+ e.toString());
			System.out.println("Query: " + query);
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error saving new DynamicLairSpawn: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	protected void loadDynamicLairSpawn(Grid theGrid, int iPlanetID,
			ZoneServer server) {
		DataLog.logEntry("Loading dynamic lair spawn list for "
				+ Constants.PlanetNames[iPlanetID], "DatabaseInterface",
				Constants.LOG_SEVERITY_INFO, true, true);
		int spawnCount = 0;
		String query = "Select * from `dynamiclairspawn` where `planetID` = "
				+ iPlanetID + " and `serverID` = " + server.getServerID() + ";";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					DynamicLairSpawn spawn = new DynamicLairSpawn(server);
					spawn.setGrid(theGrid);
					spawn.setPlanetID(iPlanetID);
					spawn.setSpawnTemplateID(r.getShort("spawnTemplateID"));
					spawn.setSpawnID(r.getInt("id"));
					int iMaxLairs = r.getInt("maxLairsToSpawn");
					int iMinLairs = r.getInt("minLairsToSpawn");
					if (iMinLairs > iMaxLairs) {
						iMaxLairs = iMinLairs;
					}
					spawn.setMaxNumToSpawn(iMaxLairs);
					spawn.setMinNumToSpawn(iMinLairs);
					spawn.setNumPlayersBeforeSpawn(r
							.getInt("minPlayersToTrigger"));
					// GridElement element =
					// theGrid.getElement(r.getInt("gridX"), r.getInt("gridY"));
					// spawn.setGridElement(element);
					spawn.setBoundaries(r.getInt("leftX"), r.getInt("topY"), r
							.getInt("rightX"), r.getInt("bottomY"));
					spawn.setColor(r.getInt("drawColor"));
					// element.addDynamicLairSpawn(spawn);
					spawnCount++;
					server.addDynamicLairSpawn(spawn);
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
			DataLog.logEntry("Loaded " + spawnCount + " lair spawns.",
					"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true,
					true);
		} catch (Exception e) {
			System.out.println("Error loading DynamicLairSpawns: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	// TODO: Make static
	protected static void loadDeedTemplates() {

		try {
			String query1 = "Select * From `deed_template` Where `enabled` = 1 Order By `id`,`deed_type` ";
			Statement s1 = conn.createStatement();
			if (s1.execute(query1)) {
				ResultSet r1 = s1.getResultSet();
				while (r1.next()) {

					DeedTemplate t = new DeedTemplate();
					t.setTemplateid(r1.getInt(1));
					t.setObject_template_id(r1.getInt(2));
					t.setObject_iff_template_id(r1.getInt(3));
					t.setNumber_cells(r1.getInt(4));
					t.setDeed_type(r1.getInt(5));
					t.setObject_base_template_id(r1.getInt(6));
					t.setBase_required(r1.getBoolean(7));
					t.setObject_admin_terminal_template_id(r1.getInt(8));
					t.setTerminal_required(r1.getBoolean(9));
					// signs are an array so that merchants can change the
					// signs.
					String[] sSign = r1.getString(10).split(",");
					int[] iSign = new int[sSign.length];
					for (int i = 0; i < sSign.length; i++) {
						iSign[i] = Integer.parseInt(sSign[i]);
					}
					t.setObject_sign_template_id(iSign);

					t.setSign_required(r1.getBoolean(11));
					t.setTerminalX(r1.getFloat(12));
					t.setTerminalY(r1.getFloat(13));
					t.setTerminalZ(r1.getFloat(14));
					t.setTerminal_oI(r1.getFloat(15));
					t.setTerminal_oJ(r1.getFloat(16));
					t.setTerminal_oK(r1.getFloat(17));
					t.setTerminal_oW(r1.getFloat(18));
					t.setTerminal_cell_id(r1.getLong(19));

					String[] sX = r1.getString(20).split(",");
					float[] sXf = new float[sX.length];
					for (int i = 0; i < sX.length; i++) {
						sXf[i] = Float.parseFloat(sX[i]);
					}

					t.setSignX(sXf);
					String[] sY = r1.getString(21).split(",");
					float[] sYf = new float[sY.length];
					for (int i = 0; i < sY.length; i++) {
						sYf[i] = Float.parseFloat(sY[i]);
					}
					t.setSignY(sYf);
					String[] sZ = r1.getString(22).split(",");
					float[] sZf = new float[sZ.length];
					for (int i = 0; i < sZ.length; i++) {
						sZf[i] = Float.parseFloat(sZ[i]);
					}
					t.setSignZ(sZf);

					String[] soI = r1.getString(23).split(",");
					float[] soIf = new float[soI.length];
					for (int i = 0; i < soI.length; i++) {
						soIf[i] = Float.parseFloat(soI[i]);
					}
					t.setSign_oI(soIf);

					String[] soJ = r1.getString(24).split(",");
					float[] soJf = new float[soJ.length];
					for (int i = 0; i < soJ.length; i++) {
						soJf[i] = Float.parseFloat(soJ[i]);
					}
					t.setSign_oJ(soJf);

					String[] soK = r1.getString(25).split(",");
					float[] soKf = new float[soK.length];
					for (int i = 0; i < soK.length; i++) {
						soKf[i] = Float.parseFloat(soK[i]);
					}
					t.setSign_oK(soKf);

					String[] soW = r1.getString(26).split(",");
					float[] soWf = new float[soW.length];
					for (int i = 0; i < soW.length; i++) {
						soWf[i] = Float.parseFloat(soW[i]);
					}
					t.setSign_oW(soWf);

					t.setMaint_per_hour(r1.getInt(27));
					t.setRequired_skill(r1.getInt(28));
					String[] alt = r1.getString("planetsallowedlist")
							.split(",");
					int[] ialt = new int[alt.length];
					for (int i = 0; i < alt.length; i++) {
						ialt[i] = Integer.parseInt(alt[i]);
					}
					t.setAllowedplanetslist(ialt);
					t.setConstruction_marker_template_id(r1
							.getInt("construction_marker_template_id"));
					t.setPower_per_hour(r1.getInt("power_per_hour"));
					t.setStructure_items_capacity(r1
							.getInt("structure_items_capacity"));
					t.setUsesPower(r1.getBoolean("usespower"));
					t.setLotsused(r1.getInt("lotsused"));
					t.setIsGuild(r1.getBoolean("isguild"));
					t.setGuild_terminal_template_id(r1
							.getInt("guild_terminal_template_id"));
					t.setGuild_terminal_x(r1.getFloat("guild_terminal_x"));
					t.setGuild_terminal_y(r1.getFloat("guild_terminal_y"));
					t.setGuild_terminal_z(r1.getFloat("guild_terminal_z"));
					t.setGuild_terminal_oI(r1.getFloat("guild_terminal_oI"));
					t.setGuild_terminal_oJ(r1.getFloat("guild_terminal_oJ"));
					t.setGuild_terminal_oK(r1.getFloat("guild_terminal_oK"));
					t.setGuild_terminal_oW(r1.getFloat("guild_terminal_oW"));
					t.setIElevatorTerminalCount(r1
							.getInt("elevatorterminalcount"));
					String[] et = r1.getString("elevatorterminalx").split(",");
					float[] fet = new float[et.length];
					for (int i = 0; i < fet.length; i++) {
						fet[i] = Float.parseFloat(et[i]);
					}
					t.setFElevatorTerminalX(fet);
					et = r1.getString("elevatorterminaly").split(",");
					fet = new float[et.length];
					for (int i = 0; i < fet.length; i++) {
						fet[i] = Float.parseFloat(et[i]);
					}
					t.setFElevatorTerminalY(fet);
					et = r1.getString("elevatorterminalz").split(",");
					fet = new float[et.length];
					for (int i = 0; i < fet.length; i++) {
						fet[i] = Float.parseFloat(et[i]);
					}
					t.setFElevatorTerminalZ(fet);
					et = r1.getString("elevatorterminaloi").split(",");
					fet = new float[et.length];
					for (int i = 0; i < fet.length; i++) {
						fet[i] = Float.parseFloat(et[i]);
					}
					t.setFElevatorTerminaloI(fet);
					et = r1.getString("elevatorterminaloj").split(",");
					fet = new float[et.length];
					for (int i = 0; i < fet.length; i++) {
						fet[i] = Float.parseFloat(et[i]);
					}
					t.setFElevatorTerminaloJ(fet);
					et = r1.getString("elevatorterminalok").split(",");
					fet = new float[et.length];
					for (int i = 0; i < fet.length; i++) {
						fet[i] = Float.parseFloat(et[i]);
					}
					t.setFElevatorTerminaloK(fet);
					et = r1.getString("elevatorterminalow").split(",");
					fet = new float[et.length];
					for (int i = 0; i < fet.length; i++) {
						fet[i] = Float.parseFloat(et[i]);
					}
					t.setFElevatorTerminaloW(fet);
					et = r1.getString("elevatorterminalcrc").split(",");
					int[] etcrc = new int[et.length];
					for (int i = 0; i < etcrc.length; i++) {
						etcrc[i] = (int) Long.parseLong(et[i]);
					}
					t.setIElevatorTerminalCRC(etcrc);
					et = r1.getString("elevatorterminalcellid").split(",");
					etcrc = new int[et.length];
					for (int i = 0; i < etcrc.length; i++) {
						etcrc[i] = Integer.parseInt(et[i]);
					}
					t.setIElevatorTerminalCellID(etcrc);
					t.setRedeedfee(r1.getInt("redeedfee"));
					t.setIBaseExtractionRate(r1.getInt("maxbaseextractrate"));
					t.setIBaseHoppersize(r1.getInt("basehoppersize"));
					t.setIXPMultiplier(r1.getInt("camp_xp_multiplier"));

					String[] sCampPropTemplateID = r1.getString(
							"camp_props_template_id").split(",");
					String[] sCampPropX = r1.getString("camp_props_x").split(
							",");
					String[] sCampPropY = r1.getString("camp_props_y").split(
							",");
					String[] sCampPropZ = r1.getString("camp_props_z").split(
							",");
					String[] sCampPropOrientationN = r1.getString(
							"camp_props_oI").split(",");
					String[] sCampPropOrientationS = r1.getString(
							"camp_props_oJ").split(",");
					String[] sCampPropOrientationE = r1.getString(
							"camp_props_oK").split(",");
					String[] sCampPropOrientationW = r1.getString(
							"camp_props_oW").split(",");

					int aSize = sCampPropTemplateID.length;
					int[] cpti = new int[aSize];
					float[] cpxf = new float[aSize];
					float[] cpyf = new float[aSize];
					float[] cpzf = new float[aSize];
					float[] cponf = new float[aSize];
					float[] cposf = new float[aSize];
					float[] cpoef = new float[aSize];
					float[] cpowf = new float[aSize];
					for (int i = 0; i < sCampPropTemplateID.length; i++) {
						cpti[i] = Integer.parseInt(sCampPropTemplateID[i]);
						cpxf[i] = Float.parseFloat(sCampPropX[i]);
						cpyf[i] = Float.parseFloat(sCampPropY[i]);
						cpzf[i] = Float.parseFloat(sCampPropZ[i]);
						cponf[i] = Float.parseFloat(sCampPropOrientationN[i]);
						cposf[i] = Float.parseFloat(sCampPropOrientationS[i]);
						cpoef[i] = Float.parseFloat(sCampPropOrientationE[i]);
						cpowf[i] = Float.parseFloat(sCampPropOrientationW[i]);
					}
					t.setICampPropTemplateID(cpti);
					t.setCampPropX(cpxf);
					t.setCampPropY(cpyf);
					t.setCampPropZ(cpzf);
					t.setCampPropOrientationN(cponf);
					t.setCampPropOrientationS(cposf);
					t.setCampPropOrientationE(cpoef);
					t.setCampPropOrientationW(cpowf);
					String[] spethams = r1.getString("pethams").split(",");
					int[] pethams = new int[spethams.length];
					for (int i = 0; i < spethams.length; i++) {
						pethams[i] = Integer.parseInt(spethams[i]);
					}
					t.setPethams(pethams);

					vDeedTemplates.add(t);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception caught in loadDeedTemplates " + e);
			e.printStackTrace();
		}
	}

	// TODO: Make static
	public static DeedTemplate getDeedTemplateByID(int templateID) {
		Enumeration<DeedTemplate> dTEnum = vDeedTemplates.elements();
		while (dTEnum.hasMoreElements()) {
			DeedTemplate t = dTEnum.nextElement();
			if (t.getTemplateid() == templateID) {
				return t;
			}
		}
		return null;
	}

	public static DeedTemplate getDeedTemplateByObjectTemplateID(int templateID) {
		Enumeration<DeedTemplate> dTEnum = vDeedTemplates.elements();
		while (dTEnum.hasMoreElements()) {
			DeedTemplate t = dTEnum.nextElement();
			if (t.getObject_template_id() == templateID) {
				return t;
			}
		}
		return null;
	}

	public static Vector<DeedTemplate> getAllDeedTemplates() {
		return vDeedTemplates;
	}

	public static int[] bfdt = {
			// house deeds
			87, 88, 89, 90, 91, 107, 108, 109, 110, 111, 112, 113, 114, 115,
			116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128,
			129, 130, 138, 139, 140, 141, 142,
			// harvester deeds
			83, 85, 86, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103,
			104, 105, 106,
			// vehicle deeds
			131, 132, 133, 134, 135, 136, 137,
			// camp Deeds
			144, 145, 146, 147, 148, 149, };

	public static Vector<DeedTemplate> getBlueFrogDeedTemplates() {

		Vector<DeedTemplate> vBFT = new Vector<DeedTemplate>();
		for (int i = 0; i < vDeedTemplates.size(); i++) {
			for (int d = 0; d < bfdt.length; d++) {
				if (vDeedTemplates.get(i).getTemplateid() == bfdt[d]) {
					DeedTemplate dT = vDeedTemplates.get(i);
					vBFT.add(dT);
				}
			}
		}
		return vBFT;
	}

	// wearable templates for blue frog
	public static Vector<String> getTools() {
		Vector<String> vToolsList = new Vector<String>();
		vToolsList.add("Survey Tools");
		return vToolsList;
	};

	public static int[][] tools = { { 14037, 14038, 14039, 14040, 14041, 14042,
			14043, 14044, 14045, 8926,},// survey tools
	};

	public static Vector<String> getWearablesSets() {
		Vector<String> vWearablesSet = new Vector<String>();// 26 sets
		vWearablesSet.add("Aprons");
		vWearablesSet.add("BackPacks");
		vWearablesSet.add("Bandoliers");
		vWearablesSet.add("Belts");
		vWearablesSet.add("Bikini");
		vWearablesSet.add("Body Suits");
		vWearablesSet.add("Boots");
		vWearablesSet.add("Bracelets");
		vWearablesSet.add("Bustier");
		vWearablesSet.add("Dresses");
		vWearablesSet.add("Earrings");
		vWearablesSet.add("Gloves");
		vWearablesSet.add("Goggles");
		vWearablesSet.add("Hats");
		vWearablesSet.add("Helmets");
		vWearablesSet.add("Ithorian Wear");
		vWearablesSet.add("Jackets");
		vWearablesSet.add("Necklace");
		vWearablesSet.add("Pants");
		vWearablesSet.add("Rings");
		vWearablesSet.add("Robes");
		vWearablesSet.add("Shirts");
		vWearablesSet.add("Shoes");
		vWearablesSet.add("Skirts");
		vWearablesSet.add("Vests");
		vWearablesSet.add("Wookie Wear");
		return vWearablesSet;
	}

	public static int[][] wearables = {
			{ 14190, 14191, 14192, 14193, }, // waprons 0
			{ 14379, 14380, 14381, 14382, 14383, 14384, 14385, 14386, },// wbackpack
			// 1
			{ 14387, 14388, 14389, 14390, 14391, 14392, 14393, 14394, 14395,
					14396, 14397, 14400, 14399, 14398, 14401, 14402, 14403,
					14404, 14405, 14406, },// wbandolier 2
			{ 14491, 14492, 14493, 14494, 14495, 14496, 14497, 14498, 14499,
					14500, 14501, 14502, 14503, 14504, 14505, 14506, 14507,
					14508, 14509, },// wbelt 3
			{ 14510, 14511, 14512, 14513, 14514, },// bikini 4
			{ 14515, 14516, 14517, 14518, 14519, 14520, 14521, 14522, 14523,
					14524, 14525, 14526, 14527, 14528, 14529, 14530, },// body
			// suit
			// 5
			{ 14531, 14532, 14533, 14534, 14535, 14536, 14537, 14538, 14539,
					14540, 14541, 14542, 14543, 14544, },// boots 6
			{ 14545, 14546, 14547, 14548, 14549, 14550, 14551, 14552, 14553,
					14554, 14555, 14556, },// bracelet 7
			{ 14557, 14558, 14559, },// bustier 8
			{ 14566, 14567, 14568, 14569, 14570, 14571, 14572, 14573, 14574,
					14575, 14576, 14577, 14578, 14579, 14580, 14581, 14582,
					14583, 14584, 14585, 14586, 14587, 14588, 14589, 14590,
					14591, },// Dress 9
			{ 14592, 14593, 14594, 14595, 14596, 14597, 14598, 14599, 14600,
					14601, 14602, 14603, 14604, 14605, 14606, 14607, },// earrings
			// 10
			{ 14608, 14609, 14610, 14611, 14612, 14613, 14614, 14615, 14616,
					14617, 14618, },// gloves 11
			{ 14619, 14620, 14621, 14622, 14623, 14624, 14625, },// goggles 12
			{ 14626, 14627, 14628, 14629, 14630, 14631, 14632, 14633, 14634,
					14635, 14636, 14637, 14638, 14639, 14640, 14641, 14642,
					14644, 14643, 14645, },// hat 13
			{ 14646, 14647, 14648, 14649, 14650, 14651, 14652, 14653, 14654, },// helmet
			// 14
			{ 14655, 14656, 14657, 14658, 14659, 14660, 14661, 14662, 14663,
					14664, 14665, 14666, 14667, 14668, 14669, 14670, 14671,
					14672, 14673, 14674, 14675, 14676, 14677, 14678, 14679,
					14680, 14681, 14682, 14683, 14684, 14685, 14686, 14687,
					14688, 14689, 14690, 14691, 14692, 14693, 14694, 14695,
					14696, 14697, 14698, 14699, 14700, 14701, 14702, 14703,
					14704, 14705, 14706, 14707, 14708, 14709, 14710, 14711,
					14712, 14713, 14714, 14715, 14716, 14717, 14718, 14719,
					14720, 14721, 14722, 14723, 14724, 14725, 14726, 14727,
					14728, 14729, 14730, 14731, 14732, 14733, 14734, 14735,
					14736, 14737, 14738, 14739, 14740, 14741, 14742, 14743,
					14744, 14745, 14746, 14747, 14748, 14749, 14750, 14751,
					14752, 14753, 14754, 14755, 14756, 14757, 14758, 14759,
					14760, 14761, 14762, 14763, 14764, 14765, 14766, 14767,
					14768, 14769, 14770, 14771, },// ithorian 15
			{ 14772, 14773, 14774, 14775, 14776, 14777, 14778, 14779, 14780,
					14781, 14782, 14783, 14784, 14785, 14786, 14787, 14788,
					14789, 14790, 14791, 14792, 14793, 14794, 14795, 14796,
					14797, },// Jacket 16
			{ 14798, 14799, 14800, 14801, 14802, 14803, 14804, 14805, 14806,
					14807, 14808, 14809, 14810, 14811, 14812, 14813, 14814,
					14815, 14816, 14817, 14818, 14819, 14820, 14821, 14822,
					14823, 14824, 14825, 14826, 14827, 14828, 14829, 14830,
					14831, 14832, 14833, 14834, 14835, 14836, 14837, 14838,
					14839, },// necklace 17
			{ 14840, 14841, 14842, 14843, 14844, 14845, 14846, 14847, 14848,
					14849, 14850, 14851, 14852, 14853, 14854, 14855, 14856,
					14857, 14858, 14859, 14860, 14861, 14862, 14863, 14864,
					14865, 14866, 14867, 14868, 14869, },// pants 18
			{ 14870, 14871, 14872, 14873, 14874, 14875, 14876, 14877, 14878, },// ring
			// 19
			{ 14879, 14880, 14881, 14882, 14883, 14884, 14885, 14886, 14887,
					14888, 14889, 14890, 14891, 14892, 14893, 14894, 14895,
					14896, 14897, 14898, 14899, 14900, 14901, 14902, },// robes
			// 20
			{ 14903, 14904, 14905, 14906, 14907, 14908, 14909, 14910, 14911,
					14912, 14913, 14914, 14915, 14916, 14917, 14918, 14919,
					14920, 14921, 14922, 14923, 14924, 14925, 14926, 14927,
					14928, 14929, 14930, 14931, 14933, 14932, },// shirts 21
			{ 14934, 14935, 14936, 14937, 14938, 14939, },// shoes 22
			{ 14942, 14941, 14940, 14943, 14944, 14945, 14946, 14947, 14948,
					14949, 14950, 14951, 14952, 14953, 14954, },// skirt 23
			{ 14958, 14959, 14960, 14961, 14962, 14963, 14964, 14965, 14966,
					14967, 14968, 14969, 14970, },// vest 24
			{ 14971, 14972, 14973, 14974, 14975, 14976, 14977, 14978, 14979,
					14980, 14981, 14982, 14983, 14984, 14985, 14986, 14987,
					14988, 14989, 14990, 14991, },// Wookie 25
	};

	public static int[][] warmorset = {
			{ 14194, 14195, 14196, 14197, 14198, 14199, 14200, 14201, 14202, },// bone
			// 0
			{ 14204, 14205, 14206, 14207, 14208, 14209, 14210, 14211, 14212,
					14213, },// bounty_hunter 1
			{ 14214, 14215, 14216, 14217, 14218, 14219, 14220, 14221, 14222, }, // chitin
			// 2
			{ 14223, 14224, 14225, 14226, 14227, 14228, 14229, 14230, 14231, },// composite
			// 3
			{ 14232, 14233, 14234, 14235, 14236, 14237, 14238, 14239, 14240, },// ithorian
			// defender
			// 4
			{ 14241, 14242, 14243, 14244, 14245, 14246, 14247, 14248, 14249, },// ithorian_guardian
			// 5
			{ 14250, 14251, 14252, 14253, 14254, 14255, 14256, 14257, 14258, },// ithorian_sentinel
			// 6
			{ 14259, 14260, 14261 },// kashyyykian_black_mtn 7
			{ 14262, 14263, 14264, 14265, },// kashyyykian_cermonial 8
			{ 14266, 14267, 14268, 14269, },// kashyyykian_hunting 9
			{ 14270, 14271, 14272, 14273, 14274, 14275, 14276, 14277, 14278,
					14279, },// mandalorian 10
			{ 14280, 14284, 14286, 14287, 14290, 14292, 14294, 14298, 14301, },// marauder
			// s01
			// 11
			{ 14285, 14288, 14289, 14296, 14299, 14300, 14302, 14304, },// marauder
			// s02
			// 12
			{ 14281, 14282, 14283, 14291, 14293, 14295, 14297, 14303, 14305,
					14306, },// marauder s03 13
			{ 14307, 14308, 14309, 14310, 14311, 14312, 14313, 14314, },// marine
			// 14
			{ 14316, 14317, 14318, 14319, 14320, 14321, 14322, 14323, 14324,
					14325, },// padded 15
			{ 14326, 14327, 14328, 14329, 14330, 14331, 14332, 14333, 14334, },// ris
			// 16
			{ 14339, 14340, 14341, 14342, 14343, 14344, 14345, 14346, 14347,
					14348, 14349, 14350, 14351, },// stormtrooper 17
			{ 14352, 14353, 14354, 14355, 14356, 14357, },// tantel 18
			{ 14358, 14359, 14360, 14361, 14362, 14363, 14364, 14365, 14366,
					14367, 14368, 14369, },// ubese 19
			{ 14370, 14371, 14372, 14373, 14374, 14375, 14376, 14377, 14378, },// zam
	// 20
	};
public static int[][] weaponset = {
			{ 15018, 15115, 15152, 15122, },// jedi
			{ 15073, },// melee 1
			{ 15231, },// ranged
	};
	protected static Vector<String> getArmorSet() {
		Vector<String> vArmorSet = new Vector<String>();
		vArmorSet.add("Bone Armor");
		vArmorSet.add("Bounty Hunter");
		vArmorSet.add("Chitin");
		vArmorSet.add("Composite");
		vArmorSet.add("Ithorian Defender");
		vArmorSet.add("Ithorian Guardian");
		vArmorSet.add("Ithorian Sentinel");
		vArmorSet.add("Kashyyykian Black Mountain");
		vArmorSet.add("Kashyyykian Ceremonial");
		vArmorSet.add("Kashyyykian Hunting");
		vArmorSet.add("Mandalorian");
		vArmorSet.add("Marauder Style 01");
		vArmorSet.add("Marauder Style 02");
		vArmorSet.add("Marauder Style 03");
		vArmorSet.add("Marine (Rebel)");
		vArmorSet.add("Padded");
		vArmorSet.add("RIS");
		vArmorSet.add("Storm Trooper");
		vArmorSet.add("Tantel");
		vArmorSet.add("Ubese");
		vArmorSet.add("Zam");
		return vArmorSet;
	}
protected static Vector<String> getWeaponSet() {
		Vector<String> vWeaponSet = new Vector<String>();
		vWeaponSet.add("Jedi Weapons");
                vWeaponSet.add("Melee Weapons");
                vWeaponSet.add("Ranged Weapons");
		return vWeaponSet;
	}
	protected static Vector<ItemTemplate> getItemTemplateWearableGroup(int group) {
		Vector<ItemTemplate> vTL = new Vector<ItemTemplate>();
		for (int i = 0; i < wearables[group].length; i++) {
			ItemTemplate T = getTemplateDataByID(wearables[group][i]);
			vTL.add(T);
		}
		return vTL;
	}

	protected static Vector<ItemTemplate> getItemTemplateArmorGroup(int group) {
		Vector<ItemTemplate> vTL = new Vector<ItemTemplate>();
		for (int i = 0; i < warmorset[group].length; i++) {
			ItemTemplate T = getTemplateDataByID(warmorset[group][i]);
			vTL.add(T);
		}
		return vTL;
	}
protected static Vector<ItemTemplate> getItemTemplateWeaponGroup(int group) {
		Vector<ItemTemplate> vTL = new Vector<ItemTemplate>();
		for (int i = 0; i < weaponset[group].length; i++) {
			ItemTemplate T = getTemplateDataByID(weaponset[group][i]);
			vTL.add(T);
		}
		return vTL;
	}
	protected static Vector<ItemTemplate> getItemTemplateToolsGroup(int group) {
		Vector<ItemTemplate> vTL = new Vector<ItemTemplate>();
		for (int i = 0; i < tools[group].length; i++) {
			ItemTemplate T = getTemplateDataByID(tools[group][i]);
			vTL.add(T);
		}
		return vTL;
	}

	// ------------------------------------------------------------------

	private static void loadCraftingSchematicsList() {

		/*
		 * try { ObjectInputStream oIn = new ObjectInputStream(new
		 * FileInputStream("schematicData")); vComponentDataFromFile=
		 * (Vector<DraftSchematicAttributeData>)oIn.readObject();
		 * System.out.println
		 * ("loadCraftingSchematicsList -- read hashtable with " +
		 * vComponentDataFromFile.size() + " elements."); for(int i=0; i <
		 * vComponentDataFromFile.size(); i++) {
		 * System.out.println(vComponentDataFromFile.elementAt(i).toString()); }
		 * } catch (FileNotFoundException eee) {
		 * System.out.println("schematicData not found."); } catch (IOException
		 * e) { System.out.println("Error reading data: " + e.toString());
		 * e.printStackTrace(); } catch (ClassCastException ee) {
		 * System.out.println("Incompatible class type loaded from file: "
		 * +ee.toString()); ee.printStackTrace(); } catch
		 * (ClassNotFoundException eeee) { // Can't happen. We have both Vector
		 * and DraftSchematicAttributeData. }
		 */
		int count = 0;
		String query = "Select * from `newschematic`";
		try {
			Statement s = conn.createStatement();
			int schematicID = 0;
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					int schematicCRC = r.getInt("SchematicCRC");
					CraftingSchematic schematic;

					String sIFFFilename = r.getString("iff_name");
					if (schematicCRC == 0) {
						if (sIFFFilename != null) {
							schematicCRC = PacketUtils.SWGCrc(sIFFFilename);
						} else {
							// Skip this one.
							continue;
						}
					}
					int iToolTabBitmask = r.getInt("tooltab");
					// TODO: Replace with new, upcoming, tool tab bitmask from
					// newSchematic
					if (sIFFFilename != null) {
						if (sIFFFilename.contains("weapon")) {
							if ((sIFFFilename.contains("tool"))
									|| (sIFFFilename.contains("structure"))) {
								// It's a regular crafting schematic.
								schematic = new CraftingSchematic();
							} else {
								schematic = new WeaponCraftingSchematic();
							}
						} else {
							schematic = new CraftingSchematic();
						}

					} else {
						schematic = new CraftingSchematic();
					}
					schematic.setIToolTabBitmask(iToolTabBitmask);
					String sSchematicName = r.getString("SchematicName");
					// System.out.println("Tool tab " + iToolTabBitmask +
					// " for schematic " + sIFFFilename);
					count++;
					schematicID = r.getInt("SchematicID");
					String sComplexity = r.getString("Complexity");
					int schematicComplexity = 0;
					try {
						schematicComplexity = Integer.parseInt(sComplexity);
					} catch (NumberFormatException ee) {
						DataLog.logEntry("Invalid schematic complexity "
								+ sComplexity + " for schematic "
								+ sSchematicName, "DatabaseInterface",
								Constants.LOG_SEVERITY_CRITICAL, true, true);
					}

					int type = 1; // TODO: Generate / collate list of "tabs" the
					// schematics appear under.
					int requiredSkill = r.getInt("skillID");
					int experienceGained = 0;
					try {
						experienceGained = r.getInt("XP");
					} catch (Exception e) {
						// D'oh!
					}
					int experienceType = r.getInt("XPType");

					schematic.setCRC(schematicCRC);
					schematic.setIndex(schematicID);
					schematic.setSchematicType(type);
					schematic.setExperienceGainedFromCrafting(experienceGained);
					schematic.setExperienceTypeToGrant(experienceType);
					schematic.addRequiredSkillID(requiredSkill);
					schematic.setComplexity(schematicComplexity);
					// Components.
					// System.out.println("Populating component list for schematic id "
					// + schematicID);
					populateComponentList(r, schematic);

					if (requiredSkill != 0) {
						Vector<CraftingSchematic> vSchematicListForSkill = vSchematicsBySkillID
								.get(requiredSkill);
						if (vSchematicListForSkill == null) {
							vSchematicListForSkill = new Vector<CraftingSchematic>();
							vSchematicsBySkillID.put(requiredSkill,
									vSchematicListForSkill);
						}
						vSchematicListForSkill.add(schematic);
					}
					schematic.setCraftedItemIFFFilename(r
							.getString("created_item_iff"));
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
			vSchematicsByIndex = new CraftingSchematic[2000]; // The last one.
			Iterator<Vector<CraftingSchematic>> vSchematicItr = vSchematicsBySkillID
					.values().iterator();
			while (vSchematicItr.hasNext()) {
				Vector<CraftingSchematic> vSchematics = vSchematicItr.next();
				for (int j = 0; j < vSchematics.size(); j++) {
					CraftingSchematic sch = vSchematics.elementAt(j);
					vSchematicsByIndex[sch.getIndex()] = sch;
				}
			}

		} catch (Exception e) {
			System.out.println("Error laoding schematic list: " + e.toString());
			e.printStackTrace();
		}
		Runtime.getRuntime().gc();
		DataLog.logEntry("Loaded " + count + " schematics",
				"DatabaseInterface", Constants.LOG_SEVERITY_INFO, true, true);

	}

	protected static Vector<CraftingSchematic> getAllSchematicsForSkill(
			int skillID) {
		return vSchematicsBySkillID.get(skillID);
	}

	protected static CraftingSchematic getSchematicByIndex(int index) {
		return vSchematicsByIndex[index];
	}

	protected static CraftingSchematic getSchematicByCRC(int crc) {
		for (int i = 0; i < vSchematicsByIndex.length; i++) {
			CraftingSchematic schematic = vSchematicsByIndex[i];
			if (schematic != null) {
				if (schematic.getCRC() == crc) {
					return schematic;
				}
			}
		}
		return null;
	}

	protected static void loadSignIndexData() {
		try {
			String query = "Select * From `sign_index_data`";
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					SignIndexData sIdx = new SignIndexData();
					sIdx.setIndex(r.getInt("index"));
					sIdx.setSign_template_id(r.getInt("sign_template_id"));
					String sArray[] = r.getString("signX").split(",");
					float fArray[] = new float[sArray.length];
					for (int i = 0; i < sArray.length; i++) {
						fArray[i] = Float.parseFloat(sArray[i]);
					}
					sIdx.setSignX(fArray);
					sArray = r.getString("signY").split(",");
					fArray = new float[sArray.length];
					for (int i = 0; i < sArray.length; i++) {
						fArray[i] = Float.parseFloat(sArray[i]);
					}
					sIdx.setSignY(fArray);
					sArray = r.getString("signZ").split(",");
					fArray = new float[sArray.length];
					for (int i = 0; i < sArray.length; i++) {
						fArray[i] = Float.parseFloat(sArray[i]);
					}
					sIdx.setSignZ(fArray);
					sArray = r.getString("signoI").split(",");
					fArray = new float[sArray.length];
					for (int i = 0; i < sArray.length; i++) {
						fArray[i] = Float.parseFloat(sArray[i]);
					}
					sIdx.setSignoI(fArray);
					sArray = r.getString("signoJ").split(",");
					fArray = new float[sArray.length];
					for (int i = 0; i < sArray.length; i++) {
						fArray[i] = Float.parseFloat(sArray[i]);
					}
					sIdx.setSignoJ(fArray);
					sArray = r.getString("signoK").split(",");
					fArray = new float[sArray.length];
					for (int i = 0; i < sArray.length; i++) {
						fArray[i] = Float.parseFloat(sArray[i]);
					}
					sIdx.setSignoK(fArray);
					sArray = r.getString("signoW").split(",");
					fArray = new float[sArray.length];
					for (int i = 0; i < sArray.length; i++) {
						fArray[i] = Float.parseFloat(sArray[i]);
					}
					sIdx.setSignoW(fArray);
					sIdx.setSkill_required(r.getInt("skill_required"));
					vServerSignIndexData.put(sIdx.getIndex(), sIdx);
				}
			}

		} catch (Exception e) {
			System.out.println("Exception caught in loadSignIndexData " + e);
			e.printStackTrace();
		}
	}

	public SignIndexData getSignIndexDataByindex(int index) {
		return vServerSignIndexData.get(index);
	}

	public HashMap<Integer, SignIndexData> getAllSignIndexData() {
		return vServerSignIndexData;
	}

	public static void populateComponentList(ResultSet r, CraftingSchematic c)
			throws SQLException {
		boolean bEndedLoadingResources = false;
		final String part = "part";
		final String STFfilename = "STFfilename";
		final String STFKey = "STFkey";
		final String partNeeded = "STFneeded";
		final String identical = "identical";
		final String quantity = "quantity";
		final String resourceOrComponent = "rescomp";
		final String optional = "optional";
		for (int i = 1; i <= 10 && !bEndedLoadingResources; i++) {
			String dataSTFFilename = part + i + STFfilename;
			String dataSTFKey = part + i + STFKey;
			String dataComponent = part + i + partNeeded;
			String dataIsIdentical = part + i + identical;
			String dataQuantity = part + i + quantity;
			String dataComponentType = part + i + resourceOrComponent;
			String dataOptional = part + i + optional;
			String STFFilename = r.getString(dataSTFFilename);
			String STFFileIdentifier = r.getString(dataSTFKey);
			String iffItemName = r.getString(dataComponent);
			String sIdenticalStatus = r.getString(dataIsIdentical);
			String sOptionalStatus = r.getString(dataOptional);
			boolean bOptional = false;
			boolean bNeedsIdentical = false;
			if (sIdenticalStatus != null) {
				bNeedsIdentical = sIdenticalStatus.equals("yes");
			}
			if (sOptionalStatus != null) {
				bOptional = sOptionalStatus.equals("yes");
			}

			int quantityOfComponent = r.getInt(dataQuantity);
			String componentType = r.getString(dataComponentType);

			byte componentFlag = 0;
			// If the componentType is "comp", then componentFlag can be 4, 5 or
			// 6.
			// If the componentType = "res", then the componentFlag must be 4.
			if (componentType.equals("res")) {
				componentFlag = 4;
			} else {
				if (bNeedsIdentical) {
					componentFlag = 6;
				} else {
					componentFlag = 5;
				}
			}
			if (STFFilename != null) {
				if (STFFilename.length() > 1) {
					CraftingSchematicComponent component = new CraftingSchematicComponent();

					component.setSTFFileName(STFFilename);
					component.setSTFFileIdentifier(STFFileIdentifier);
					component.setName(iffItemName);
					component.setComponentQuantity(quantityOfComponent);
					component.setComponentRequirementType(componentFlag);
					component.setOptionalComponent(bOptional);
					component.setComponentIdentical(bNeedsIdentical);

					c.addComponent(component);
					component.setIsResource(componentType.equals("res"));
				} else {
					bEndedLoadingResources = true;
				}
			} else {
				bEndedLoadingResources = true;
			}
		}

		// Now, the attribute data.
		// e1desc1, e1desc2, e1er, e1cr, e1cd, e1dr, e1fl, e1hr, e1ma, e1pe,
		// e1oq, e1sr, e1ut, e1slot -- this might or might not be necessary.
		// Max number of experimental attributes in table: 15
		// Starting slot: 110
		// if (bUseFileComponentData) {

		// } else {

		int offset = 110;
		int numFieldsPerExperimentalAttribute = 14;
		boolean bHasAttribute = true;
		Vector<CraftingExperimentationAttribute> vAttributes = new Vector<CraftingExperimentationAttribute>();
		for (int i = 0; i < 15 && bHasAttribute; i++) {
			String stfExperimentalName = r.getString(offset);
			if (stfExperimentalName != null) {
				if (stfExperimentalName.length() > 2) {
					String stfExperimentalValue = r.getString(offset + 1);
					int[] rawWeights = new int[11];
					rawWeights[0] = r.getInt(offset + 2);
					rawWeights[1] = r.getInt(offset + 3);
					rawWeights[2] = r.getInt(offset + 4);
					rawWeights[3] = r.getInt(offset + 5);
					rawWeights[4] = r.getInt(offset + 6);
					rawWeights[5] = r.getInt(offset + 7);
					rawWeights[6] = r.getInt(offset + 8);
					rawWeights[7] = r.getInt(offset + 9);
					rawWeights[8] = r.getInt(offset + 10);
					rawWeights[9] = r.getInt(offset + 11);
					rawWeights[10] = r.getInt(offset + 12);
					int slotIndex = r.getInt(offset + 13);
					int[] weightValues = new int[3];
					byte[] weights = new byte[3];
					int currentWeight = 0;
					for (int j = 0; j < 11
							&& currentWeight < weightValues.length; j++) {
						if (rawWeights[j] != 0) {
							weightValues[currentWeight] = rawWeights[j];
							switch (j) {
							case 0: {
								weights[currentWeight] = Constants.RESOURCE_WEIGHT_POTENCY; // Entangle
								// resistance
								break;
							}
							case 1: {
								weights[currentWeight] = Constants.RESOURCE_WEIGHT_COLD_RESISTANCE; // Entangle
								// resistance
								break;
							}
							case 2: {
								weights[currentWeight] = Constants.RESOURCE_WEIGHT_CONDUCTIVITY; // Entangle
								// resistance
								break;
							}
							case 3: {
								weights[currentWeight] = Constants.RESOURCE_WEIGHT_DECAY_RESISTANCE; // Entangle
								// resistance
								break;
							}
							case 4: {
								weights[currentWeight] = Constants.RESOURCE_WEIGHT_HEAT_RESISTANCE; // Entangle
								// resistance
								break;
							}
							case 5: {
								weights[currentWeight] = Constants.RESOURCE_WEIGHT_FLAVOR; // Entangle
								// resistance
								break;
							}
							case 6: {
								weights[currentWeight] = Constants.RESOURCE_WEIGHT_MALLEABILITY; // Entangle
								// resistance
								break;
							}
							case 7: {
								weights[currentWeight] = Constants.RESOURCE_WEIGHT_POTENTIAL_ENERGY; // Entangle
								// resistance
								break;
							}
							case 8: {
								weights[currentWeight] = (byte) Constants.RESOURCE_WEIGHT_OVERALL_QUALITY; // Entangle
								// resistance
								break;
							}
							case 9: {
								weights[currentWeight] = (byte) Constants.RESOURCE_WEIGHT_SHOCK_RESISTANCE; // Entangle
								// resistance
								break;
							}
							case 10: {
								weights[currentWeight] = (byte) Constants.RESOURCE_WEIGHT_UNIT_TOUGHNESS; // Entangle
								// resistance
								break;
							}
							case 11: {
								weights[currentWeight] = (byte) Constants.RESOURCE_WEIGHT_BULK; // Entangle
								// resistance
								break;
							}
							default: {
								// D'oh!
							}
							}
							currentWeight++;
						}
					}

					switch (currentWeight) {
					case 1: { // Only one weight to this experimental attribute.
						weights[0] = (byte) (weights[0] | 1);
						break;
					}
					case 2: {
						// Somewhat fugly -- 2 weights.
						// Need to handle: 50/50, 66/33, 75/25
						if (weightValues[0] == 50) {
							weights[0] = (byte) (weights[0] | 1);
							weights[1] = (byte) (weights[1] | 1);
						} else if (weightValues[0] == 66) {
							weights[0] = (byte) (weights[0] | 2);
							weights[1] = (byte) (weights[1] | 1);
						} else if (weightValues[0] == 33) {
							weights[0] = (byte) (weights[0] | 1);
							weights[1] = (byte) (weights[1] | 2);
						} else if (weightValues[0] == 75) {
							weights[0] = (byte) (weights[0] | 3);
							weights[1] = (byte) (weights[1] | 1);
						} else if (weightValues[0] == 25) {
							weights[0] = (byte) (weights[0] | 1);
							weights[1] = (byte) (weights[1] | 3);
						}
						break;
					}
					case 3: {
						// Even more fugly.
						// 33,33,33, 50, 25, 25,
						if (weightValues[0] == 33) {
							weights[0] = (byte) (weights[0] | 1);
							weights[1] = (byte) (weights[1] | 1);
							weights[2] = (byte) (weights[2] | 1);
						} else if (weightValues[0] == 25) {
							if (weightValues[1] == 25) {
								// 25, 25, 50
								weights[0] = (byte) (weights[0] | 1);
								weights[1] = (byte) (weights[1] | 1);
								weights[2] = (byte) (weights[2] | 2);

							} else if (weightValues[1] == 50) {
								// 25, 50, 25
								weights[0] = (byte) (weights[0] | 1);
								weights[1] = (byte) (weights[1] | 2);
								weights[2] = (byte) (weights[2] | 1);

							}
						} else if (weightValues[0] == 50) {
							// 50, 25, 25
							weights[0] = (byte) (weights[0] | 2);
							weights[1] = (byte) (weights[1] | 1);
							weights[2] = (byte) (weights[2] | 1);

						}
						break;
					}

					}
					CraftingExperimentationAttribute attrData = new CraftingExperimentationAttribute();
					attrData.setStfFileName("crafting");
					attrData.setStfFileIdentifier(stfExperimentalValue);
					attrData.setNumWeights(currentWeight);
					for (int j = 0; j < currentWeight; j++) {
						attrData.setWeight(j, weights[j]);
					}
					attrData.setIndex(slotIndex);
					vAttributes.add(attrData);
				} else {
					bHasAttribute = false;
				}
			} else {
				bHasAttribute = false;
			}
			offset += numFieldsPerExperimentalAttribute;
		}

		c.setNumAttributes(vAttributes.size());
		for (int i = 0; i < vAttributes.size(); i++) {
			c.addAttribute(i, vAttributes.elementAt(i));
		}
		// }
	}

	private static void loadDecodedIFFData() {
		try {
			String query = "Select * from `decoded_iff`;";
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					IFFData data = new IFFData();
					data.setIFFFileName(r.getString("sIFFFileName"));
					data.setSTFFileName(r.getString("sSTFFileName"));
					data.setSTFFileIdentifier(r.getString("sSTFStringName"));
					data.setSTFDetailName(r.getString("sSTFDetailFileName"));
					data.setSTFDetailIdentifier(r
							.getString("sSTFDetailStringName"));
					data.setSTFLookAtName(r.getString("sSTFLookAtFileName"));
					data.setSTFLookAtIdentifier(r
							.getString("sSTFLookAtStringName"));
					data.setSTFCraftName(r.getString("sSTFCraftFileName"));
					data.setSTFCraftIdentifier(r
							.getString("sSTFCraftStringName"));
					data.setCraftedItemIFF(r.getString("sCraftedItemIFFName"));
					data.setCraftingIngredients(r.getString(
							"sSchematicIngredients").split(","));
					data.setCRC(r.getInt("iffCRC"));
					data.setCraftedItemCRC(r.getInt("craftedItemCRC"));
					vDecodedIFFData.put(data.getCRC(), data);
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
		} catch (Exception e) {
			System.out.println("Error loading Decoded IFF table: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	// What is this here for?
	/*
	 * protected static String getResourceName(String sResource){ return
	 * vResourceNameStrings.get(sResource); }
	 * 
	 * protected static Hashtable<String , String> getAllResourceNames(){ return
	 * vResourceNameStrings; } protected static void loadResourceNameStrings(){
	 * vResourceNameStrings.put("resource","Resources");
	 * vResourceNameStrings.put("organic","Organic");
	 * vResourceNameStrings.put("creature_resources","Creature Resources");
	 * vResourceNameStrings.put("creature_food","Creature Food");
	 * vResourceNameStrings.put("milk","Milk");
	 * vResourceNameStrings.put("milk_domesticated","Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_corellia","Corellian Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_dantooine","Dantooine Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_dathomir","Dathomirian  Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_endor","Endorian Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_lok","Lokian Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_naboo","Nabooian Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_rori","Rori Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_talus","Talusian Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_tatooine","Tatooinian Domesticated Milk");
	 * vResourceNameStrings
	 * .put("milk_domesticated_yavin4","Yavinian Domesticated Milk");
	 * vResourceNameStrings.put("milk_wild","Wild Milk");
	 * vResourceNameStrings.put("milk_wild_corellia","Corellian Wild Milk");
	 * vResourceNameStrings.put("milk_wild_dantooine","Dantooine Wild Milk");
	 * vResourceNameStrings.put("milk_wild_dathomir","Dathomirian Wild Milk");
	 * vResourceNameStrings.put("milk_wild_endor","Endorian Wild Milk");
	 * vResourceNameStrings.put("milk_wild_lok","Lokian Wild Milk");
	 * vResourceNameStrings.put("milk_wild_naboo","Nabooian Wild Milk");
	 * vResourceNameStrings.put("milk_wild_rori","Rori Wild Milk");
	 * vResourceNameStrings.put("milk_wild_talus","Talusian Wild Milk");
	 * vResourceNameStrings.put("milk_wild_tatooine","Tatooinian Wild Milk");
	 * vResourceNameStrings.put("milk_wild_yavin4","Yavin IV Wild Milk");
	 * vResourceNameStrings.put("meat","Meat");
	 * vResourceNameStrings.put("meat_domesticated","Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_corellia","Corellian Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_dantooine","Dantooine Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_dathomir","Dathomirian Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_endor","Endorian Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_lok","Lokian Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_naboo","Nabooian Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_rori","Rori Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_talus","Talusian Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_tatooine","Tatooinian Domesticated Meat");
	 * vResourceNameStrings
	 * .put("meat_domesticated_yavin4","Yavinian Domesticated Meat");
	 * vResourceNameStrings.put("meat_wild","Wild Meat");
	 * vResourceNameStrings.put("meat_wild_corellia","Corellian Wild Meat");
	 * vResourceNameStrings.put("meat_wild_dantooine","Dantooine Wild Meat");
	 * vResourceNameStrings.put("meat_wild_dathomir","Dathomirian Wild Meat");
	 * vResourceNameStrings.put("meat_wild_endor","Endorian Wild Meat");
	 * vResourceNameStrings.put("meat_wild_lok","Lokian Wild Meat");
	 * vResourceNameStrings.put("meat_wild_naboo","Nabooian Wild Meat");
	 * vResourceNameStrings.put("meat_wild_rori","Rori Wild Meat");
	 * vResourceNameStrings.put("meat_wild_talus","Talusian Wild Meat");
	 * vResourceNameStrings.put("meat_wild_tatooine","Tatooinian Wild Meat");
	 * vResourceNameStrings.put("meat_wild_yavin4","Yavinian Wild Meat");
	 * vResourceNameStrings.put("meat_herbivore","Herbivore Meat");
	 * vResourceNameStrings
	 * .put("meat_herbivore_corellia","Corellian Herbivore Meat");
	 * vResourceNameStrings
	 * .put("meat_herbivore_dantooine","Dantooine Herbivore Meat");
	 * vResourceNameStrings
	 * .put("meat_herbivore_dathomir","Dathomirian Herbivore Meat");
	 * vResourceNameStrings
	 * .put("meat_herbivore_endor","Endorian Herbivore Meat");
	 * vResourceNameStrings.put("meat_herbivore_lok","Lokian Herbivore Meat");
	 * vResourceNameStrings
	 * .put("meat_herbivore_naboo","Nabooian Herbivore Meat");
	 * vResourceNameStrings.put("meat_herbivore_rori","Rori Herbivore Meat");
	 * vResourceNameStrings
	 * .put("meat_herbivore_talus","Talusian Herbivore Meat");
	 * vResourceNameStrings
	 * .put("meat_herbivore_tatooine","Tatooinian Herbivore Meat");
	 * vResourceNameStrings
	 * .put("meat_herbivore_yavin4","Yavinian Herbivore Meat");
	 * vResourceNameStrings.put("meat_carnivore","Carnivore Meat");
	 * vResourceNameStrings
	 * .put("meat_carnivore_corellia","Corellian Carnivore Meat");
	 * vResourceNameStrings
	 * .put("meat_carnivore_dantooine","Dantooine Carnivore Meat");
	 * vResourceNameStrings
	 * .put("meat_carnivore_dathomir","Dathomirian Carnivore Meat");
	 * vResourceNameStrings
	 * .put("meat_carnivore_endor","Endorian Carnivore Meat");
	 * vResourceNameStrings.put("meat_carnivore_lok","Lokian Carnivore Meat");
	 * vResourceNameStrings
	 * .put("meat_carnivore_naboo","Nabooian Carnivore Meat");
	 * vResourceNameStrings.put("meat_carnivore_rori","Rori Carnivore Meat");
	 * vResourceNameStrings
	 * .put("meat_carnivore_talus","Talusian Carnivore Meat");
	 * vResourceNameStrings
	 * .put("meat_carnivore_tatooine","Tatooinian Carnivore Meat");
	 * vResourceNameStrings
	 * .put("meat_carnivore_yavin4","Yavinian Carnivore Meat");
	 * vResourceNameStrings.put("meat_reptillian","Reptillian Meat");
	 * vResourceNameStrings
	 * .put("meat_reptilian_corellia","Corellian Reptillian Meat");
	 * vResourceNameStrings
	 * .put("meat_reptilian_dantooine","Dantooine Reptillian Meat");
	 * vResourceNameStrings
	 * .put("meat_reptilian_dathomir","Dathomirian Reptillian Meat");
	 * vResourceNameStrings
	 * .put("meat_reptilian_endor","Endorian Reptillian Meat");
	 * vResourceNameStrings.put("meat_reptilian_lok","Lokian Reptillian Meat");
	 * vResourceNameStrings
	 * .put("meat_reptilian_naboo","Nabooian Reptillian Meat");
	 * vResourceNameStrings.put("meat_reptilian_rori","Rori Reptillian Meat");
	 * vResourceNameStrings
	 * .put("meat_reptilian_talus","Talusian Reptillian Meat");
	 * vResourceNameStrings
	 * .put("meat_reptilian_tatooine","Tatooinian Reptillian Meat");
	 * vResourceNameStrings
	 * .put("meat_reptilian_yavin4","Yavinian Reptillian Meat");
	 * vResourceNameStrings.put("meat_avian","Avian Meat");
	 * vResourceNameStrings.put("meat_avian_corellia","Corellian Avian Meat");
	 * vResourceNameStrings.put("meat_avian_dantooine","Dantooine Avian Meat");
	 * vResourceNameStrings.put("meat_avian_dathomir","Dathomirian Avian Meat");
	 * vResourceNameStrings.put("meat_avian_endor","Endorian Avian Meat");
	 * vResourceNameStrings.put("meat_avian_lok","Lokian Avian Meat");
	 * vResourceNameStrings.put("meat_avian_naboo","Nabooian Avian Meat");
	 * vResourceNameStrings.put("meat_avian_rori","Rori Avian Meat");
	 * vResourceNameStrings.put("meat_avian_talus","Talusian Avian Meat");
	 * vResourceNameStrings.put("meat_avian_tatooine","Tatooinian Avian Meat");
	 * vResourceNameStrings.put("meat_avian_yavin4","Yavinian Avian Meat");
	 * vResourceNameStrings.put("meat_egg","Egg");
	 * vResourceNameStrings.put("meat_egg_corellia","Corellian Egg");
	 * vResourceNameStrings.put("meat_egg_dantooine","Dantooine Egg");
	 * vResourceNameStrings.put("meat_egg_dathomir","Dathomirian Egg");
	 * vResourceNameStrings.put("meat_egg_endor","Endorian Egg");
	 * vResourceNameStrings.put("meat_egg_lok","Lokian Egg");
	 * vResourceNameStrings.put("meat_egg_naboo","Nabooian Egg");
	 * vResourceNameStrings.put("meat_egg_rori","Rori Egg");
	 * vResourceNameStrings.put("meat_egg_talus","Talusian Egg");
	 * vResourceNameStrings.put("meat_egg_tatooine","Tatooinian Egg");
	 * vResourceNameStrings.put("meat_egg_yavin4","Yavinian Egg");
	 * vResourceNameStrings.put("meat_insect","Insect Meat");
	 * vResourceNameStrings.put("meat_insect_corellia","Corellian Insect Meat");
	 * vResourceNameStrings
	 * .put("meat_insect_dantooine","Dantooine Insect Meat");
	 * vResourceNameStrings
	 * .put("meat_insect_dathomir","Dathomirian Insect Meat");
	 * vResourceNameStrings.put("meat_insect_endor","Endorian Insect Meat");
	 * vResourceNameStrings.put("meat_insect_lok","Lokian Insect Meat");
	 * vResourceNameStrings.put("meat_insect_naboo","Nabooian Insect Meat");
	 * vResourceNameStrings.put("meat_insect_rori","Rori Insect Meat");
	 * vResourceNameStrings.put("meat_insect_talus","Talusian Insect Meat");
	 * vResourceNameStrings
	 * .put("meat_insect_tatooine","Tatooinian Insect Meat");
	 * vResourceNameStrings.put("meat_insect_yavin4","Yavinian Insect Meat");
	 * vResourceNameStrings.put("seafood","Seafood");
	 * vResourceNameStrings.put("seafood_fish_corellia","Corellian Fish Meat");
	 * vResourceNameStrings.put("seafood_fish_dantooine","Dantooine Fish Meat");
	 * vResourceNameStrings
	 * .put("seafood_fish_dathomir","Dathomirian Fish Meat");
	 * vResourceNameStrings.put("seafood_fish_endor","Endorian Fish Meat");
	 * vResourceNameStrings.put("seafood_fish_lok","Lokian Fish Meat");
	 * vResourceNameStrings.put("seafood_fish_naboo","Nabooian Fish Meat");
	 * vResourceNameStrings.put("seafood_fish_rori","Rori Fish Meat");
	 * vResourceNameStrings.put("seafood_fish_talus","Talusian Fish Meat");
	 * vResourceNameStrings.put("seafood_fish_tatooine","Tatooinian Fish Meat");
	 * vResourceNameStrings.put("seafood_fish_yavin4","Yavinian Fish Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_corellia","Corellia Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_dantooine","Dantooine Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_dathomir","Dathomirian Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_endor","Endorian Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_lok","Lokian Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_naboo","Nabooian Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_rori","Rori Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_talus","Talusian Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_tatooine","Tatooinian Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_crustacean_yavin4","Yavinian Crustacean Meat");
	 * vResourceNameStrings
	 * .put("seafood_mollusk_corellia","Corellia Mollusk Meat");
	 * vResourceNameStrings
	 * .put("seafood_mollusk_dantooine","Dantooine Mollusk Meat");
	 * vResourceNameStrings
	 * .put("seafood_mollusk_dathomir","Dathomirian Mollusk Meat");
	 * vResourceNameStrings
	 * .put("seafood_mollusk_endor","Endorian Mollusk Meat");
	 * vResourceNameStrings.put("seafood_mollusk_lok","Lokian Mollusk Meat");
	 * vResourceNameStrings
	 * .put("seafood_mollusk_naboo","Nabooian Mollusk Meat");
	 * vResourceNameStrings.put("seafood_mollusk_rori","Rori Mollusk Meat");
	 * vResourceNameStrings
	 * .put("seafood_mollusk_talus","Talusian Mollusk Meat");
	 * vResourceNameStrings
	 * .put("seafood_mollusk_tatooine","Tatooinian Mollusk Meat");
	 * vResourceNameStrings
	 * .put("seafood_mollusk_yavin4","Yavinian Mollusk Meat");
	 * vResourceNameStrings.put("creature_structural","Creature Structural");
	 * vResourceNameStrings.put("bone","Bone");
	 * vResourceNameStrings.put("bone_mammal_corellia"
	 * ,"Corellian Animal Bones");
	 * vResourceNameStrings.put("bone_mammal_dantooine"
	 * ,"Dantooine Animal Bones");
	 * vResourceNameStrings.put("bone_mammal_dathomir"
	 * ,"Dathomirian Animal Bones");
	 * vResourceNameStrings.put("bone_mammal_endor","Endorian Animal Bones");
	 * vResourceNameStrings.put("bone_mammal_lok","Lokian  Animal Bones");
	 * vResourceNameStrings.put("bone_mammal_naboo","Nabooian  Animal Bones");
	 * vResourceNameStrings.put("bone_mammal_rori","Rori  Animal Bones");
	 * vResourceNameStrings.put("bone_mammal_talus","Talusian  Animal Bones");
	 * vResourceNameStrings
	 * .put("bone_mammal_tatooine","Tatooinian  Animal Bones");
	 * vResourceNameStrings.put("bone_mammal_yavin4","Yavinian  Animal Bones");
	 * vResourceNameStrings.put("bone_horn","Horn");
	 * vResourceNameStrings.put("bone_horn_corellia","Corellian Horn");
	 * vResourceNameStrings.put("bone_horn_dantooine","Dantooine Horn");
	 * vResourceNameStrings.put("bone_horn_dathomir","Dothomirian Horn");
	 * vResourceNameStrings.put("bone_horn_endor","Endorian Horn");
	 * vResourceNameStrings.put("bone_horn_lok","Lokian Horn");
	 * vResourceNameStrings.put("bone_horn_naboo","Nabooian Horn");
	 * vResourceNameStrings.put("bone_horn_rori","Rori Horn");
	 * vResourceNameStrings.put("bone_horn_talus","Talusian Horn");
	 * vResourceNameStrings.put("bone_horn_tatooine","Tatooinian Horn");
	 * vResourceNameStrings.put("bone_horn_yavin4","Yavinian Horn");
	 * vResourceNameStrings.put("bone_avian","Avian bone");
	 * vResourceNameStrings.put("bone_avian_corellia","Corellian Avian Bones");
	 * vResourceNameStrings.put("bone_avian_dantooine","Dantooine Avian Bones");
	 * vResourceNameStrings
	 * .put("bone_avian_dathomir","Dathomirian Avian Bones");
	 * vResourceNameStrings.put("bone_avian_endor","Endorian Avian Bones");
	 * vResourceNameStrings.put("bone_avian_lok","Lokian Avian Bones");
	 * vResourceNameStrings.put("bone_avian_naboo","Nabooian Avian Bones");
	 * vResourceNameStrings.put("bone_avian_rori","Rori Avian Bones");
	 * vResourceNameStrings.put("bone_avian_talus","Talusian Avian Bones");
	 * vResourceNameStrings.put("bone_avian_tatooine","Tatooinian Avian Bones");
	 * vResourceNameStrings.put("bone_avian_yavin4","Yavinian Avian Bones");
	 * vResourceNameStrings.put("hide","Hide");
	 * vResourceNameStrings.put("hide_wooly","Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_corellia","Corellian Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_dantooine","Dantooine Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_dathomir","Dathomirian Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_endor","Endorian Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_lok","Lokian Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_naboo","Nabooian Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_rori","Rori Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_talus","Talusian Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_tatooine","Tatooinian Wooly Hide");
	 * vResourceNameStrings.put("hide_wooly_yavin4","Yavinian Wooly Hide");
	 * vResourceNameStrings.put("hide_bristley","Bristley Hide");
	 * vResourceNameStrings
	 * .put("hide_bristley_corellia","Corellian Bristley Hide");
	 * vResourceNameStrings
	 * .put("hide_bristley_dantooine","Dantooine Bristley Hide");
	 * vResourceNameStrings
	 * .put("hide_bristley_dathomir","Dathomirian Bristley Hide");
	 * vResourceNameStrings.put("hide_bristley_endor","Endorian Bristley Hide");
	 * vResourceNameStrings.put("hide_bristley_lok","Lokian Bristley Hide");
	 * vResourceNameStrings.put("hide_bristley_naboo","Nabooian Bristley Hide");
	 * vResourceNameStrings.put("hide_bristley_rori","Rori Bristley Hide");
	 * vResourceNameStrings.put("hide_bristley_talus","Talusian Bristley Hide");
	 * vResourceNameStrings
	 * .put("hide_bristley_tatooine","Tatooinian Bristley Hide");
	 * vResourceNameStrings
	 * .put("hide_bristley_yavin4","Yavinian Bristley Hide");
	 * vResourceNameStrings.put("hide_leathery","Leathery Hide");
	 * vResourceNameStrings
	 * .put("hide_leathery_corellia","Corellian Leathery Hide");
	 * vResourceNameStrings
	 * .put("hide_leathery_dantooine","Dantooine Leathery Hide");
	 * vResourceNameStrings
	 * .put("hide_leathery_dathomir","Dathomirian Leathery Hide");
	 * vResourceNameStrings.put("hide_leathery_endor","Endorian Leathery Hide");
	 * vResourceNameStrings.put("hide_leathery_lok","Lokian Leathery Hide");
	 * vResourceNameStrings.put("hide_leathery_naboo","Nabooian Leathery Hide");
	 * vResourceNameStrings.put("hide_leathery_rori","Rori Leathery Hide");
	 * vResourceNameStrings.put("hide_leathery_talus","Talusian Leathery Hide");
	 * vResourceNameStrings
	 * .put("hide_leathery_tatooine","Tatooinian Leathery Hide");
	 * vResourceNameStrings
	 * .put("hide_leathery_yavin4","Yavinian Leathery Hide");
	 * vResourceNameStrings.put("hide_scaley","Scaley Hide");
	 * vResourceNameStrings.put("hide_scaley_corellia","Corellian Scaley Hide");
	 * vResourceNameStrings
	 * .put("hide_scaley_dantooine","Dantooine Scaley Hide");
	 * vResourceNameStrings
	 * .put("hide_scaley_dathomir","Dathomirian Scaley Hide");
	 * vResourceNameStrings.put("hide_scaley_endor","Endorian Scaley Hide");
	 * vResourceNameStrings.put("hide_scaley_lok","Lokian Scaley Hide");
	 * vResourceNameStrings.put("hide_scaley_naboo","Nabooian Scaley Hide");
	 * vResourceNameStrings.put("hide_scaley_rori","Rori Scaley Hide");
	 * vResourceNameStrings.put("hide_scaley_talus","Talusian Scaley Hide");
	 * vResourceNameStrings
	 * .put("hide_scaley_tatooine","Tatooinian Scaley Hide");
	 * vResourceNameStrings.put("hide_scaley_yavin4","Yavinian Scaley Hide");
	 * vResourceNameStrings.put("flora_resources","Flora Resources");
	 * vResourceNameStrings.put("flora_food","Flora Food");
	 * vResourceNameStrings.put("cereal","Cereal");
	 * vResourceNameStrings.put("corn","Corn");
	 * vResourceNameStrings.put("corn_domesticated","Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_corellia","Corellian Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_dantooine","Dantooine Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_dathomir","Dathomirian Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_endor","Endorian Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_lok","Lokian Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_naboo","Nabooian Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_rori","Rori Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_talus","Talusian Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_tatooine","Tatooinian Domesticated Corn");
	 * vResourceNameStrings
	 * .put("corn_domesticated_yavin4","Yavinian Domesticated Corn");
	 * vResourceNameStrings.put("corn_wild","Wild Corn");
	 * vResourceNameStrings.put("corn_wild_corellia","Corellian Wild Corn");
	 * vResourceNameStrings.put("corn_wild_dantooine","Dantooine Wild Corn");
	 * vResourceNameStrings.put("corn_wild_dathomir","Dathomirian Wild Corn");
	 * vResourceNameStrings.put("corn_wild_endor","Endorian Wild Corn");
	 * vResourceNameStrings.put("corn_wild_lok","Lokian Wild Corn");
	 * vResourceNameStrings.put("corn_wild_naboo","Nabooian Wild Corn");
	 * vResourceNameStrings.put("corn_wild_rori","Rori Wild Corn");
	 * vResourceNameStrings.put("corn_wild_talus","Talusian Wild Corn");
	 * vResourceNameStrings.put("corn_wild_tatooine","Tatooinian Wild Corn");
	 * vResourceNameStrings.put("corn_wild_yavin4","Yavinian Wild Corn");
	 * vResourceNameStrings.put("rice","Rice");
	 * vResourceNameStrings.put("rice_domesticated","Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_corellia","Corellian Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_dantooine","Dantooine Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_dathomir","Dathomirian Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_endor","Endorian Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_lok","Lokian Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_naboo","Nabooian Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_rori","Rori Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_talus","Talusian Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_tatooine","Tatooinian Domesticated Rice");
	 * vResourceNameStrings
	 * .put("rice_domesticated_yavin4","Yavinian Domesticated Rice");
	 * vResourceNameStrings.put("rice_wild","Wild Rice");
	 * vResourceNameStrings.put("rice_wild_corellia","Corellian Wild Rice");
	 * vResourceNameStrings.put("rice_wild_dantooine","Dantooine Wild Rice");
	 * vResourceNameStrings.put("rice_wild_dathomir","Dathomirian Wild Rice");
	 * vResourceNameStrings.put("rice_wild_endor","Endorian Wild Rice");
	 * vResourceNameStrings.put("rice_wild_lok","Lokian Wild Rice");
	 * vResourceNameStrings.put("rice_wild_naboo","Nabooian Wild Rice");
	 * vResourceNameStrings.put("rice_wild_rori","Rori Wild Rice");
	 * vResourceNameStrings.put("rice_wild_talus","Talusian Wild Rice");
	 * vResourceNameStrings.put("rice_wild_tatooine","Tatooinian Wild Rice");
	 * vResourceNameStrings.put("rice_wild_yavin4","Yavinian Wild Rice");
	 * vResourceNameStrings.put("oats","Oats");
	 * vResourceNameStrings.put("oats_domesticated","Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_corellia","Corellian Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_dantooine","Dantooine Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_dathomir","Dathomirian Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_endor","Endorian Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_lok","Lokian Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_naboo","Nabooian Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_rori","Rori Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_talus","Talusian Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_tatooine","Tatooinian Domesticated Oats");
	 * vResourceNameStrings
	 * .put("oats_domesticated_yavin4","Yavinian Domesticated Oats");
	 * vResourceNameStrings.put("oats_wild","Wild Oats");
	 * vResourceNameStrings.put("oats_wild_corellia","Corellian Wild Oats");
	 * vResourceNameStrings.put("oats_wild_dantooine","Dantooine Wild Oats");
	 * vResourceNameStrings.put("oats_wild_dathomir","Dathomirian Wild Oats");
	 * vResourceNameStrings.put("oats_wild_endor","Endorian Wild Oats");
	 * vResourceNameStrings.put("oats_wild_lok","Lokian Wild Oats");
	 * vResourceNameStrings.put("oats_wild_naboo","Nabooian Wild Oats");
	 * vResourceNameStrings.put("oats_wild_rori","Rori Wild Oats");
	 * vResourceNameStrings.put("oats_wild_talus","Talusian Wild Oats");
	 * vResourceNameStrings.put("oats_wild_tatooine","Tatooinian Wild Oats");
	 * vResourceNameStrings.put("oats_wild_yavin4","Yavinian Wild Oats");
	 * vResourceNameStrings.put("wheat","Wheat");
	 * vResourceNameStrings.put("wheat_domesticated","Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_corellia","Corellian Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_dantooine","Dantooine Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_dathomir","Dathomirian Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_endor","Endorian Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_lok","Lokian Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_naboo","Nabooian Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_rori","Rori Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_talus","Talusian Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_tatooine","Tatooinian Domesticated Wheat");
	 * vResourceNameStrings
	 * .put("wheat_domesticated_yavin4","Yavinian Domesticated Wheat");
	 * vResourceNameStrings.put("wheat_wild","Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_corellia","Corellian Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_dantooine","Dantooine Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_dathomir","Dathomir Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_endor","Endorian Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_lok","Lokian Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_naboo","Nabooian Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_rori","Rori Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_talus","Talusian Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_tatooine","Tatooinian Wild Wheat");
	 * vResourceNameStrings.put("wheat_wild_yavin4","Yavinian Wild Wheat");
	 * vResourceNameStrings.put("seeds","Seeds");
	 * vResourceNameStrings.put("vegetable","Vegetables");
	 * vResourceNameStrings.put("vegetable_greens","Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_corellia","Corellian Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_dantooine","Dantooine Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_dathomir","Dathomirian Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_endor","Endorian Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_lok","Lokian Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_naboo","Nabooian Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_rori","Rori Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_talus","Talusian Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_tatooine","Tatooinian Vegetable Greens");
	 * vResourceNameStrings
	 * .put("vegetable_greens_yavin4","Yavinian Vegetable Greens");
	 * vResourceNameStrings.put("vegetable_beans","Beans");
	 * vResourceNameStrings.
	 * put("vegetable_beans_corellia","Corellian Vegetable Beans");
	 * vResourceNameStrings
	 * .put("vegetable_beans_dantooine","Dantooine Vegetable Beans");
	 * vResourceNameStrings
	 * .put("vegetable_beans_dathomir","Dathomirian Vegetable Beans");
	 * vResourceNameStrings
	 * .put("vegetable_beans_endor","Endorian Vegetable Beans");
	 * vResourceNameStrings.put("vegetable_beans_lok","Lokian Vegetable Beans");
	 * vResourceNameStrings
	 * .put("vegetable_beans_naboo","Nabooian Vegetable Beans");
	 * vResourceNameStrings.put("vegetable_beans_rori","Rori Vegetable Beans");
	 * vResourceNameStrings
	 * .put("vegetable_beans_talus","Talusian Vegetable Beans");
	 * vResourceNameStrings
	 * .put("vegetable_beans_tatooine","Tatooinian Vegetable Beans");
	 * vResourceNameStrings
	 * .put("vegetable_beans_yavin4","Yavinian Vegetable Beans");
	 * vResourceNameStrings.put("vegetable_tubers","Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_corellia","Corellian Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_dantooine","Dantooine Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_dathomir","Dathomirian Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_endor","Endorian Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_lok","Lokian Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_naboo","Nabooian Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_rori","Rori Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_talus","Talusian Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_tatooine","Tatooinian Vegetable Tubers");
	 * vResourceNameStrings
	 * .put("vegetable_tubers_yavin4","Yavinian Vegetable Tubers");
	 * vResourceNameStrings.put("vegetable_fungi","Fungi");
	 * vResourceNameStrings.
	 * put("vegetable_fungi_corellia","Corellian Vegetable Fungus");
	 * vResourceNameStrings
	 * .put("vegetable_fungi_dantooine","Dantooine Vegetable Fungus");
	 * vResourceNameStrings
	 * .put("vegetable_fungi_dathomir","Dathomirian Vegetable Fungus");
	 * vResourceNameStrings
	 * .put("vegetable_fungi_endor","Endorian Vegetable Fungus");
	 * vResourceNameStrings
	 * .put("vegetable_fungi_lok","Lokian Vegetable Fungus");
	 * vResourceNameStrings
	 * .put("vegetable_fungi_naboo","Nabooian Vegetable Fungus");
	 * vResourceNameStrings.put("vegetable_fungi_rori","Rori Vegetable Fungus");
	 * vResourceNameStrings
	 * .put("vegetable_fungi_talus","Talusian Vegetable Fungus");
	 * vResourceNameStrings
	 * .put("vegetable_fungi_tatooine","Tatooinian Vegetable Fungus");
	 * vResourceNameStrings
	 * .put("vegetable_fungi_yavin4","Yavinian Vegetable Fungus");
	 * vResourceNameStrings.put("fruit","Fruit");
	 * vResourceNameStrings.put("fruit_fruits","Fruits");
	 * vResourceNameStrings.put("fruit_fruits_corellia","Corellian Fruit");
	 * vResourceNameStrings.put("fruit_fruits_dantooine","Dantooine Fruit");
	 * vResourceNameStrings.put("fruit_fruits_dathomir","Dathomirian Fruit");
	 * vResourceNameStrings.put("fruit_fruits_endor","Endorian Fruit");
	 * vResourceNameStrings.put("fruit_fruits_lok","Lokian Fruit");
	 * vResourceNameStrings.put("fruit_fruits_naboo","Nabooian Fruit");
	 * vResourceNameStrings.put("fruit_fruits_rori","Rori Fruit");
	 * vResourceNameStrings.put("fruit_fruits_talus","Talusian Fruit");
	 * vResourceNameStrings.put("fruit_fruits_tatooine","Tatooinian Fruit");
	 * vResourceNameStrings.put("fruit_fruits_yavin4","Yavinian Fruit");
	 * vResourceNameStrings.put("fruit_berries","Berries");
	 * vResourceNameStrings.
	 * put("fruit_berries_corellia","Corellia Berry Fruit");
	 * vResourceNameStrings
	 * .put("fruit_berries_dantooine","Dantooine Berry Fruit");
	 * vResourceNameStrings
	 * .put("fruit_berries_dathomir","Dathomirian Berry Fruit");
	 * vResourceNameStrings.put("fruit_berries_endor","Endorian Berry Fruit");
	 * vResourceNameStrings.put("fruit_berries_lok","Lokian Berry Fruit");
	 * vResourceNameStrings.put("fruit_berries_naboo","Nabooian Berry Fruit");
	 * vResourceNameStrings.put("fruit_berries_rori","Rori Berry Fruit");
	 * vResourceNameStrings.put("fruit_berries_talus","Talusian Berry Fruit");
	 * vResourceNameStrings
	 * .put("fruit_berries_tatooine","Tatooinian Berry Fruit");
	 * vResourceNameStrings.put("fruit_berries_yavin4","Yavinian Berry Fruit");
	 * vResourceNameStrings.put("fruit_flowers","Flowers");
	 * vResourceNameStrings.
	 * put("fruit_flowers_corellia","Corellia Flower Fruit");
	 * vResourceNameStrings
	 * .put("fruit_flowers_dantooine","Dantooine Flower Fruit");
	 * vResourceNameStrings
	 * .put("fruit_flowers_dathomir","Dathomirian Flower Fruit");
	 * vResourceNameStrings.put("fruit_flowers_endor","Endorian Flower Fruit");
	 * vResourceNameStrings.put("fruit_flowers_lok","Lokian Flower Fruit");
	 * vResourceNameStrings.put("fruit_flowers_naboo","Nabooian Flower Fruit");
	 * vResourceNameStrings.put("fruit_flowers_rori","Rori Flower Fruit");
	 * vResourceNameStrings.put("fruit_flowers_talus","Talusian Flower Fruit");
	 * vResourceNameStrings
	 * .put("fruit_flowers_tatooine","Tatooinian Flower Fruit");
	 * vResourceNameStrings.put("fruit_flowers_yavin4","Yavinian Flower Fruit");
	 * vResourceNameStrings.put("flora_structural","Flora Structural");
	 * vResourceNameStrings.put("wood","Wood");
	 * vResourceNameStrings.put("wood_deciduous","Hard Wood");
	 * vResourceNameStrings
	 * .put("wood_deciduous_corellia","Corellian Deciduous Wood");
	 * vResourceNameStrings
	 * .put("wood_deciduous_dantooine","Dantooine Deciduous Wood");
	 * vResourceNameStrings
	 * .put("wood_deciduous_dathomir","Dathomirian Deciduous Wood");
	 * vResourceNameStrings
	 * .put("wood_deciduous_endor","Endorian Deciduous Wood");
	 * vResourceNameStrings.put("wood_deciduous_lok","Lokian Deciduous Wood");
	 * vResourceNameStrings
	 * .put("wood_deciduous_naboo","Nabooian Deciduous Wood");
	 * vResourceNameStrings.put("wood_deciduous_rori","Rori Deciduous Wood");
	 * vResourceNameStrings
	 * .put("wood_deciduous_talus","Talusian Deciduous Wood");
	 * vResourceNameStrings
	 * .put("wood_deciduous_tatooine","Tatooinian Deciduous Wood");
	 * vResourceNameStrings
	 * .put("wood_deciduous_yavin4","Yavinian Deciduous Wood");
	 * vResourceNameStrings.put("softwood","Soft Wood");
	 * vResourceNameStrings.put
	 * ("softwood_conifer_corellia","Corellian Conifer Wood");
	 * vResourceNameStrings
	 * .put("softwood_conifer_dantooine","Dantooine Conifer Wood");
	 * vResourceNameStrings
	 * .put("softwood_conifer_dathomir","Dathomirian Conifer Wood");
	 * vResourceNameStrings
	 * .put("softwood_conifer_endor","Endorian Conifer Wood");
	 * vResourceNameStrings.put("softwood_conifer_lok","Lokian Conifer Wood");
	 * vResourceNameStrings
	 * .put("softwood_conifer_naboo","Nabooian Conifer Wood");
	 * vResourceNameStrings.put("softwood_conifer_rori","Rori Conifer Wood");
	 * vResourceNameStrings
	 * .put("softwood_conifer_talus","Talusian Conifer Wood");
	 * vResourceNameStrings
	 * .put("softwood_conifer_tatooine","Tatooinian Conifer Wood");
	 * vResourceNameStrings
	 * .put("softwood_conifer_yavin4","Yavinian Conifer Wood");
	 * vResourceNameStrings.put("softwood_evergreen","Evergreen Soft Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_corellia","Corellia Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_dantooine","Dantooine Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_dathomir","Dathomirian Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_endor","Endorian Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_lok","Lokian Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_naboo","Nabooian Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_rori","Rori Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_talus","Talusian Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_tatooine","Tatooinian Evergreen Wood");
	 * vResourceNameStrings
	 * .put("softwood_evergreen_yavin4","Yavinian Evergreen Wood");
	 * vResourceNameStrings.put("inorganic","Inorganic");
	 * vResourceNameStrings.put("chemical","Chemical");
	 * vResourceNameStrings.put(
	 * "fuel_petrochem_liquid","Liquid Petrochem Fuel");
	 * vResourceNameStrings.put
	 * ("petrochem_fuel_liquid_unknown","Unknown Liquid Petrochem Fuel");
	 * vResourceNameStrings
	 * .put("fuel_petrochem_liquid_known","Known Liquid Petrochem Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_liquid_type1","Class 1 Liquid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_liquid_type2","Class 2 Liquid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_liquid_type3","Class 3 Liquid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_liquid_type4","Class 4 Liquid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_liquid_type5","Class 5 Liquid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_liquid_type6","Class 6 Liquid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_liquid_type7","Class 7 Liquid Petro Fuel");
	 * vResourceNameStrings.put("petrochem_inert","Inert Petrochemical");
	 * vResourceNameStrings
	 * .put("petrochem_inert_lubricating_oil","Lubricating Oil");
	 * vResourceNameStrings.put("petrochem_inert_polymer","Polymer");
	 * vResourceNameStrings.put("water","Water");
	 * vResourceNameStrings.put("water_vapor_corellia","Corellian Water Vapor");
	 * vResourceNameStrings
	 * .put("water_vapor_dantooine","Dantooine Water Vapor");
	 * vResourceNameStrings.put("water_vapor_dathomir","Dathomir Water Vapor");
	 * vResourceNameStrings.put("water_vapor_endor","Endorian Water Vapor");
	 * vResourceNameStrings.put("water_vapor_lok","Lokian Water Vapor");
	 * vResourceNameStrings.put("water_vapor_naboo","Nabooian Water Vapor");
	 * vResourceNameStrings.put("water_vapor_rori","Rori Water Vapor");
	 * vResourceNameStrings.put("water_vapor_talus","Talusian Water Vapor");
	 * vResourceNameStrings
	 * .put("water_vapor_tatooine","Tatooinian Water Vapor");
	 * vResourceNameStrings.put("water_vapor_yavin4","Yavinian Water Vapor");
	 * vResourceNameStrings.put("mineral","Mineral");
	 * vResourceNameStrings.put("fuel_petrochem_solid","Solid Petrochem Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_solid_unknown","Unknown Solid Petrochem Fuel");
	 * vResourceNameStrings
	 * .put("fuel_petrochem_solid_known","Known Solid Petrochem Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_solid_type1","Class 1 Solid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_solid_type2","Class 2 Solid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_solid_type3","Class 3 Solid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_solid_type4","Class 4 Solid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_solid_type5","Class 5 Solid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_solid_type6","Class 6 Solid Petro Fuel");
	 * vResourceNameStrings
	 * .put("petrochem_fuel_solid_type7","Class 7 Solid Petro Fuel");
	 * vResourceNameStrings.put("radioactive","Radioactive");
	 * vResourceNameStrings.put("radioactive_unknown","Unknown Radioactive");
	 * vResourceNameStrings.put("radioactive_known","Known Radioactive");
	 * vResourceNameStrings.put("radioactive_type1","Class 1 Radioactive");
	 * vResourceNameStrings.put("radioactive_type2","Class 2 Radioactive");
	 * vResourceNameStrings.put("radioactive_type3","Class 3 Radioactive");
	 * vResourceNameStrings.put("radioactive_type4","Class 4 Radioactive");
	 * vResourceNameStrings.put("radioactive_type5","Class 5 Radioactive");
	 * vResourceNameStrings.put("radioactive_type6","Class 6 Radioactive");
	 * vResourceNameStrings.put("radioactive_type7","Class 7 Radioactive");
	 * vResourceNameStrings.put("metal","Metal");
	 * vResourceNameStrings.put("metal_ferrous","Ferrous Metal");
	 * vResourceNameStrings
	 * .put("metal_ferrous_unknown","Unknown Ferrous Metal");
	 * vResourceNameStrings.put("steel","Steel");
	 * vResourceNameStrings.put("steel_rhodium","Rhodium Steel");
	 * vResourceNameStrings.put("steel_kiirium","Kiirium Steel");
	 * vResourceNameStrings.put("steel_cubirian","Cubirian Steel");
	 * vResourceNameStrings.put("steel_thoranium","Thoranium Steel");
	 * vResourceNameStrings.put("steel_neutronium","Neutronium Steel");
	 * vResourceNameStrings.put("steel_duranium","Duranium Steel");
	 * vResourceNameStrings.put("steel_ditanium","Ditanium Steel");
	 * vResourceNameStrings.put("steel_quadranium","Quadranium Steel");
	 * vResourceNameStrings.put("steel_carbonite","Carbonite Steel");
	 * vResourceNameStrings.put("steel_duralloy","Duralloy Steel");
	 * vResourceNameStrings.put("iron","Iron");
	 * vResourceNameStrings.put("iron_plumbum","Plumbum Iron");
	 * vResourceNameStrings.put("iron_polonium","Polonium Iron");
	 * vResourceNameStrings.put("iron_axidite","Axidite Iron");
	 * vResourceNameStrings.put("iron_bronzium","Bronzium Iron");
	 * vResourceNameStrings.put("iron_colat","Colat Iron");
	 * vResourceNameStrings.put("iron_dolovite","Dolovite Iron");
	 * vResourceNameStrings.put("iron_doonium","Doonium Iron");
	 * vResourceNameStrings.put("iron_kammris","Kammris Iron");
	 * vResourceNameStrings.put("metal_nonferrous","Non-Ferrous Metal");
	 * vResourceNameStrings
	 * .put("metal_nonferrous_unknown","Unknown Non-Ferrous Metal");
	 * vResourceNameStrings.put("aluminum","Aluminum");
	 * vResourceNameStrings.put("aluminum_titanium","Titanium Aluminum");
	 * vResourceNameStrings.put("aluminum_agrinium","Agrinium Aluminum");
	 * vResourceNameStrings.put("aluminum_chromium","Chromium Aluminum");
	 * vResourceNameStrings.put("aluminum_duralumin","Duralumin Aluminum");
	 * vResourceNameStrings.put("aluminum_linksteel","Link-Steel Aluminum");
	 * vResourceNameStrings.put("aluminum_phrik","Phrik Aluminum");
	 * vResourceNameStrings.put("copper","Copper");
	 * vResourceNameStrings.put("copper_desh","Desh Copper");
	 * vResourceNameStrings.put("copper_thallium","Thallium Copper");
	 * vResourceNameStrings.put("copper_beyrllius","Beyrllius Copper");
	 * vResourceNameStrings.put("copper_codoan","Codoan Copper");
	 * vResourceNameStrings.put("copper_diatium","Diatium Copper");
	 * vResourceNameStrings.put("copper_kelsh","Kelsh Copper");
	 * vResourceNameStrings.put("copper_mythra","Mythra Copper");
	 * vResourceNameStrings.put("copper_platinite","Platinite Copper");
	 * vResourceNameStrings.put("copper_polysteel","Polysteel Copper");
	 * vResourceNameStrings.put("ore","Low-Grade Ore");
	 * vResourceNameStrings.put("ore_igneous","Igneous Ore");
	 * vResourceNameStrings.put("ore_igneous_unknown","Unknown Igneous Ore");
	 * vResourceNameStrings.put("ore_extrusive","Extrusive Ore");
	 * vResourceNameStrings.put("ore_extrusive_bene","Bene Extrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_extrusive_chronamite","Chronamite Extrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_extrusive_ilimium","Ilimium Extrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_extrusive_kalonterium","Kalonterium Extrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_extrusive_keschel","Keschel Extrusive Ore");
	 * vResourceNameStrings.put("ore_extrusive_lidium","Lidium Extrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_extrusive_maranium","Maranium Extrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_extrusive_pholokite","Pholokite Extrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_extrusive_quadrenium","Quadrenium Extrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_extrusive_vintrium","Vintrium Extrusive Ore");
	 * vResourceNameStrings.put("ore_intrusive","Intrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_intrusive_berubium","Berubium Intrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_intrusive_chanlon","Chanlon Intrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_intrusive_corintium","Corintium Intrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_intrusive_derillium","Derillium Intrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_intrusive_oridium","Oridium Intrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_intrusive_dylinium","Dylinium Intrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_intrusive_hollinium","Hollinium Intrusive Ore");
	 * vResourceNameStrings.put("ore_intrusive_ionite","Ionite Intrusive Ore");
	 * vResourceNameStrings
	 * .put("ore_intrusive_katrium","Katrium Intrusive Ore");
	 * vResourceNameStrings.put("ore_sedimentary","Sedimentary Ore");
	 * vResourceNameStrings
	 * .put("ore_sedimentary_unknown","Unknown Sedimentary Ore");
	 * vResourceNameStrings.put("ore_carbonate","Carbonate Ore");
	 * vResourceNameStrings
	 * .put("ore_carbonate_alantium","Alantium Carbonate Ore");
	 * vResourceNameStrings
	 * .put("ore_carbonate_barthierium","Barthierium Carbonate Ore");
	 * vResourceNameStrings
	 * .put("ore_carbonate_chromite","Chromite Carbonate Ore");
	 * vResourceNameStrings
	 * .put("ore_carbonate_frasium","Frasium Carbonate Ore");
	 * vResourceNameStrings
	 * .put("ore_carbonate_lommite","Lommite Carbonate Ore");
	 * vResourceNameStrings
	 * .put("ore_carbonate_ostrine","Ostrine Carbonate Ore");
	 * vResourceNameStrings.put("ore_carbonate_varium","Varium Carbonate Ore");
	 * vResourceNameStrings
	 * .put("ore_carbonate_zinsiam","Zinsiam Carbonate Ore");
	 * vResourceNameStrings.put("ore_siliclastic","Siliclastic Ore");
	 * vResourceNameStrings
	 * .put("ore_siliclastic_ardanium","Ardanium Siliclastic Ore");
	 * vResourceNameStrings
	 * .put("ore_siliclastic_cortosis","Cortosis Siliclastic Ore");
	 * vResourceNameStrings
	 * .put("ore_siliclastic_crism","Crism Siliclastic Ore");
	 * vResourceNameStrings
	 * .put("ore_siliclastic_malab","Malab Siliclastic Ore");
	 * vResourceNameStrings
	 * .put("ore_siliclastic_robindun","Robindun Siliclastic Ore");
	 * vResourceNameStrings
	 * .put("ore_siliclastic_tertian","Tertian Siliclastic Ore");
	 * vResourceNameStrings.put("gemstone","Gemstone");
	 * vResourceNameStrings.put("gemstone_unknown","Unknown Gem Type");
	 * vResourceNameStrings.put("gemstone_armophous","Amorphous Gemstone");
	 * vResourceNameStrings
	 * .put("armophous_bospridium","Bospridium Amorphous Gemstone");
	 * vResourceNameStrings
	 * .put("armophous_baradium","Baradium Amorphous Gemstone");
	 * vResourceNameStrings.put("armophous_regvis","Regvis Amorphous Gemstone");
	 * vResourceNameStrings
	 * .put("armophous_plexite","Plexite Amorphous Gemstone");
	 * vResourceNameStrings.put("armophous_rudic","Rudic Amorphous Gemstone");
	 * vResourceNameStrings.put("armophous_ryll","Ryll Amorphous Gemstone");
	 * vResourceNameStrings
	 * .put("armophous_sedrellium","Sedrellium Amorphous Gemstone");
	 * vResourceNameStrings
	 * .put("armophous_stygium","Stygium Amorphous Gemstone");
	 * vResourceNameStrings
	 * .put("armophous_vendusii","Vendusii Crystal Amorphous Gemstone");
	 * vResourceNameStrings
	 * .put("armophous_baltaran","Bal'ta'ran Crystal Amorphous Gemstone");
	 * vResourceNameStrings.put("gemstone_crystalline","Crystalline Gemstone");
	 * vResourceNameStrings
	 * .put("crystalline_byrothsis","Byrothsis Crystalline Gemstone");
	 * vResourceNameStrings
	 * .put("crystalline_gallinorian","Gallinorian Rainbow Gem Crystalline Gemstone"
	 * );vResourceNameStrings.put("crystalline_green_diamond",
	 * "Green Diamond Crystalline Gemstone");
	 * vResourceNameStrings.put("crystalline_kerol_firegem"
	 * ,"Kerol Fire-Gem Crystalline Gemstone");
	 * vResourceNameStrings.put("crystalline_seafah_jewel"
	 * ,"Seafah Jewel Crystalline Gemstone");
	 * vResourceNameStrings.put("crystalline_sormahil_firegem"
	 * ,"Sormahil Fire Gem Crystalline Gemstone");
	 * vResourceNameStrings.put("crystalline_laboi_mineral_crystal"
	 * ,"Laboi Mineral Crystal Crystalline Gemstone");
	 * vResourceNameStrings.put("crystalline_vertex"
	 * ,"Vertex Crystalline Gemstone"); vResourceNameStrings.put("gas","Gas");
	 * vResourceNameStrings.put("gas_reactive","Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_unknown","Unknown Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_known","Known Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_eleton","Eleton Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_irolunn","Irolunn Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_methane","Methane Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_orveth","Orveth Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_sig","Sig Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_skevon","Skevon Reactive Gas");
	 * vResourceNameStrings.put("gas_reactive_tolium","Tolium Reactive Gas");
	 * vResourceNameStrings.put("gas_inert","Inert Gas");
	 * vResourceNameStrings.put("gas_inert_unknown","Unknown Inert Gas");
	 * vResourceNameStrings.put("gas_inert_known","Known Inert Gas");
	 * vResourceNameStrings.put("gas_inert_hydron3","Hydron-3 Inert Gas");
	 * vResourceNameStrings.put("gas_inert_malium","Malium Inert Gas");
	 * vResourceNameStrings.put("gas_inert_bilal","Bilal gas Inert Gas");
	 * vResourceNameStrings.put("gas_inert_corthel","Corthel Inert Gas");
	 * vResourceNameStrings.put("gas_inert_culsion","Culsion Inert Gas");
	 * vResourceNameStrings.put("gas_inert_dioxis","Dioxis Inert Gas");
	 * vResourceNameStrings
	 * .put("gas_inert_hurlothrombic","Hurlothrombic Inert Gas");
	 * vResourceNameStrings.put("gas_inert_kaylon","Kaylon Inert Gas");
	 * vResourceNameStrings.put("gas_inert_korfaise","Korfaise Inert Gas");
	 * vResourceNameStrings.put("gas_inert_methanagen","Methanagen Inert Gas");
	 * vResourceNameStrings.put("gas_inert_mirth","Mirth Inert Gas");
	 * vResourceNameStrings.put("gas_inert_obah","Obah Inert Gas");
	 * vResourceNameStrings.put("gas_inert_rethin","Rethin Inert Gas");
	 * vResourceNameStrings.put("energy","Energy");
	 * vResourceNameStrings.put("energy_renewable","Renewable energy");
	 * vResourceNameStrings
	 * .put("energy_renewable_site_limited","Site-Restricted Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_corellia",
	 * "Corellian Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_dathomir"
	 * ,"Dathomirian Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_dantooine"
	 * ,"Dantooine Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_endor"
	 * ,"Endor Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_lok"
	 * ,"Lokian Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_naboo"
	 * ,"Naboo Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_rori"
	 * ,"Rori Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_talus"
	 * ,"Talusian Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_tatooine"
	 * ,"Tatooinian Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_tidal_yavin4"
	 * ,"Yavinian Tidal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_corellia"
	 * ,"Corellian Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_dantooine"
	 * ,"Dantooine Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_dathomir"
	 * ,"Dathomirian Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_endor"
	 * ,"Endorian Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_lok"
	 * ,"Lokian Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_naboo"
	 * ,"Nabooian Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_rori"
	 * ,"Rori Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_talus"
	 * ,"Talusian Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_tatooine"
	 * ,"Tatooinian Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_hydron3_yavin4"
	 * ,"Yavinian Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_corellia"
	 * ,"Corellian Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_dantooine"
	 * ,"Dantooine Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_dathomir"
	 * ,"Dathomirian Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_endor"
	 * ,"Endorian Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_lok"
	 * ,"Lokian Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_naboo"
	 * ,"Nabooian Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_rori"
	 * ,"Rori Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_talus"
	 * ,"Talusian Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_tatooine"
	 * ,"Tatooinian Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_yavin4"
	 * ,"Yavinian Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited"
	 * ,"Non Site-Restricted Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_corellia"
	 * ,"Corellian Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_dantooine"
	 * ,"Dantooine Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_dathomir"
	 * ,"Dathomirian Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_endor"
	 * ,"Endorian Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_lok"
	 * ,"Lokian Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_naboo"
	 * ,"Nabooian Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_rori"
	 * ,"Rori Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_talus"
	 * ,"Talusian Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_tatooine"
	 * ,"Tatooinian Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_yavin4"
	 * ,"Yavinian Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_corellia"
	 * ,"Corellian Solar Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_dantooine"
	 * ,"Dantooine Solar Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_dathomir"
	 * ,"Dathomirian Solar Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_endor"
	 * ,"Endorian Solar Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_lok"
	 * ,"Lokian Solar Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_naboo"
	 * ,"Nabooian Solar Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_rori"
	 * ,"Rori Solar Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_talus"
	 * ,"Talusian Solar Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_tatooine"
	 * ,"Tatooinian Solar Renewable Energy");
	 * vResourceNameStrings.put("fiberplast","Fiberplast");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_yavin4",
	 * "Yavinian Solar Renewable Energy");
	 * vResourceNameStrings.put("fiberplast_corellia","Corellia Fiberplast");
	 * vResourceNameStrings.put("fiberplast_dantooine","Dantooine Fiberplast");
	 * vResourceNameStrings.put("fiberplast_dathomir","Dathomirian Fiberplast");
	 * vResourceNameStrings.put("fiberplast_endor","Endorian Fiberplast");
	 * vResourceNameStrings.put("fiberplast_lok","Lokian Fiberplast");
	 * vResourceNameStrings.put("fiberplast_naboo","Nabooian Fiberplast");
	 * vResourceNameStrings.put("fiberplast_rori","Rori Fiberplast");
	 * vResourceNameStrings.put("fiberplast_talus","Talusian Fiberplast");
	 * vResourceNameStrings.put("fiberplast_tatooine","Tatooinian Fiberplast");
	 * vResourceNameStrings.put("fiberplast_yavin4","Yavinian Fiberplast");
	 * vResourceNameStrings.put("steel_arveshian","Hardened Arveshium Steel");
	 * vResourceNameStrings
	 * .put("steel_bicorbantium","Crystallized Bicorbantium Steel");
	 * vResourceNameStrings
	 * .put("copper_borocarbitic","Conductive Borcarbitic Copper");
	 * vResourceNameStrings
	 * .put("ore_siliclastic_fermionic","Fermionic Siliclastic Ore");
	 * vResourceNameStrings.put("aluminum_perovskitic","Perovskitic Aluminum");
	 * vResourceNameStrings
	 * .put("gas_reactive_organometallic","Unstable Organometallic Reactive Gas"
	 * );
	 * vResourceNameStrings.put("fiberplast_gravitonic","Gravitonic Fiberplast"
	 * );vResourceNameStrings.put("radioactive_polymetric",
	 * "High Grade Polymetric Radioactive");
	 * vResourceNameStrings.put("seafood_fish","Fish");
	 * vResourceNameStrings.put("seafood_crustacean","Crustacean");
	 * vResourceNameStrings.put("seafood_mollusk","Mollusk");
	 * vResourceNameStrings
	 * .put("energy_renewable_unlimited_wind","Wind Energy");
	 * vResourceNameStrings
	 * .put("energy_renewable_unlimited_solar","Solar Energy");
	 * vResourceNameStrings.put("milk_homogenized","Homogenized Milk");
	 * vResourceNameStrings.put("processed_meat","Processed Meat");
	 * vResourceNameStrings.put("processed_seafood","Processed Seafood");
	 * vResourceNameStrings.put("ground_bones","Ground Bones");
	 * vResourceNameStrings.put("synthesized_hides","Synthesized Hides");
	 * vResourceNameStrings
	 * .put("smelted_metal_ferrous_unknown","Smelted Ferrous Metal");
	 * vResourceNameStrings
	 * .put("smelted_metal_nonferrous_unknown","Smelted Non-Ferrous Metal");
	 * vResourceNameStrings.put("bone_horn_ground","Ground Horn");
	 * vResourceNameStrings.put("mixed_vegetables","Mixed Vegetables");
	 * vResourceNameStrings.put("mixed_fruits","Mixed Fruits");
	 * vResourceNameStrings.put("chemical_compound","Chemical Compound");
	 * vResourceNameStrings
	 * .put("degraded_fuel_petrochem_solid","Degraded Solid Petrochem Fuel");
	 * vResourceNameStrings
	 * .put("combined_radioactive_isotpopes","Combined Radioactive Isotopes");
	 * vResourceNameStrings.put("steel_smelted","Smelted Steel");
	 * vResourceNameStrings.put("iron_smelted","Smelted Iron");
	 * vResourceNameStrings.put("aluminum_smelted","Smelted Aluminum");
	 * vResourceNameStrings.put("copper_smelted","Smelted Copper");
	 * vResourceNameStrings
	 * .put("ore_extrusive_low_grade","Low Grade Ore (igneous)");
	 * vResourceNameStrings
	 * .put("ore_carbonate_low_grade","Low Grade Ore (sedimentary)");
	 * vResourceNameStrings
	 * .put("ore_siliclastic_low_grade","Low Grade Ore (siliclastic)");
	 * vResourceNameStrings
	 * .put("gemstone_mixed_low_quality","Low Quality Gemstones");
	 * vResourceNameStrings.put("gas_reactive_mixed","Mixed Reactive Gas");
	 * vResourceNameStrings.put("gas_inert_mixed","Mixed Inert Gas");
	 * vResourceNameStrings
	 * .put("energy_renewable_site_limited_tidal_weak","Weak Tidal Renewable Energy"
	 * );vResourceNameStrings.put("energy_renewable_site_limited_hydron3_weak",
	 * "Weak Hydron-3 Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_site_limited_geothermal_weak"
	 * ,"Weak Geothermal Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_wind_weak"
	 * ,"Weak Wind Renewable Energy");
	 * vResourceNameStrings.put("energy_renewable_unlimited_solar_weak"
	 * ,"Weak Solar Renewable Energy");
	 * vResourceNameStrings.put("processed_wood","Blended Wood");
	 * vResourceNameStrings.put("water_solution","Water Solution");
	 * vResourceNameStrings.put("processed_cereal","Processed Cereal");
	 * 
	 * }
	 */
	protected static void cleanEmails() {
		try {
			int iEmailAccountsCleaned = 0;
			String query = "Select * from `email`  Group By `player_id`;";
			Statement s1 = conn.createStatement();
			Statement s2 = conn.createStatement();
			Statement s3 = conn.createStatement();
			if (s1.execute(query)) {
				ResultSet r1 = s1.getResultSet();
				while (r1.next()) {
					long lEmailPlayerID = r1.getLong("player_id");
					int iEmailServerID = r1.getInt("server_id");
					String sCharacter = "Select * from `character` Where `character_id` = "
							+ lEmailPlayerID
							+ " And `server_id` = "
							+ iEmailServerID + ";";
					// System.out.println(sCharacter);
					if (s2.execute(sCharacter)) {
						if (!s2.getResultSet().first()) {
							String sRemoveEmails = "Delete from `email` where `player_id` = "
									+ lEmailPlayerID
									+ " And `server_id` = "
									+ iEmailServerID + ";";
							// System.out.println(sRemoveEmails);
							s3.execute(sRemoveEmails);
							iEmailAccountsCleaned++;
						}
					}
				}
			}
			DataLog.logEntry("Email Accounts Cleaned up: "
					+ iEmailAccountsCleaned, "DatabaseInterface",
					Constants.LOG_SEVERITY_INFO, true, true);
		} catch (Exception e) {
			System.out
					.println("Exception caught in DatabaseInterface.cleanEmails() "
							+ e);
			e.printStackTrace();
		}
	}

	protected static void loadRestrictedAccessCells() {
		Statement s1 = null;
		ResultSet r1 = null;
		try {
			String query = "Select * from `restrictedcells` ;";
			s1 = conn.createStatement();

			if (s1.execute(query)) {
				r1 = s1.getResultSet();
				while (r1.next()) {
					vRestrictedAccessCells.add(r1.getLong("id"));
				}
				r1.close();
				r1 = null;
			}
			s1.close();
			s1 = null;
		} catch (Exception e) {
			System.out.println("Exception Caught in DatabaseInterface " + e);
			e.printStackTrace();
		} finally {
			try {
				if (r1 != null) {
					r1.close();
					r1 = null;
				}
				if (s1 != null) {
					s1.close();
					s1 = null;
				}
			} catch (Exception e) {
				// Oh well.
			}
		}
	}

	protected static boolean isRestricted(long cellID) {
		return vRestrictedAccessCells.contains(cellID);
	}

	protected static void loadZoneServerRunOptions(int iZoneServerID) {
		runOptions = new ZoneServerRunOptions();
		// set some defaults just in case
		runOptions.setServerid(iZoneServerID);
		runOptions.setBLogToConsole(true);
		try {
			String query = "Select * from `serverrunoptions` Where `zoneserverid` = "
					+ iZoneServerID + ";";
			Statement s1 = conn.createStatement();

			if (s1.execute(query)) {
				ResultSet r1 = s1.getResultSet();
				while (r1.next()) {
					runOptions.setServerid(r1.getInt("zoneserverid"));
					runOptions.setBLogToConsole(r1.getBoolean("blogtoconsole"));
				}
			}

		} catch (Exception e) {
			System.out
					.println("Exception Caught in DatabaseInterface.loadZoneServerRunOptions "
							+ e);
			e.printStackTrace();
		}

	}

	protected static ZoneServerRunOptions getServerRunOptions() {
		return runOptions;
	}

	private static void loadStartingLocations() {
		try {
			String query = "Select * from `startinglocations`";
			Statement s1 = conn.createStatement();

			if (s1.execute(query)) {
				ResultSet r1 = s1.getResultSet();
				while (r1.next()) {
					StartingLocation L = new StartingLocation();
					L.setCityName(r1.getString("cityname"));
					L.setAvailable(r1.getBoolean("available"));
					L.setPlanetID(r1.getInt("planetid"));
					L.setStyleSTF(r1.getString("stylestf"));
					L.setX(r1.getFloat("x"));
					L.setY(r1.getFloat("y"));
					L.setZ(r1.getFloat("z"));
					vStartingLocations.add(L);
				}
			}
			DataLog.logEntry("Starting Locations Loaded "
					+ vStartingLocations.size(),
					"DatabaseInterface.loadStartingLocations",
					Constants.LOG_SEVERITY_INFO, true, true);
		} catch (Exception e) {
			DataLog.logException("Error Loading Starting Locations",
					"DatabaseInterface.loadStartingLocations", true, true, e);
		}
	}

	protected static Vector<StartingLocation> getStartingLocations() {
		return vStartingLocations;
	}

	private static void loadCraftedWeaponCaps() {
		String query = "Select * from item_crafted_weapon_limits;";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					int templateID = r.getInt("itemTemplateID");
					ItemTemplate template = getTemplateDataByID(templateID);
					String sTemplateIFFFilename = template.getIFFFileName();
					boolean bFound = false;
					for (int i = 0; i < vSchematicsByIndex.length && !bFound; i++) {
						CraftingSchematic schematic = vSchematicsByIndex[i];

						if (schematic != null) {
							String craftIFFFilename = schematic
									.getCraftedItemIFFFilename();
							if (sTemplateIFFFilename.equals(craftIFFFilename)) {
								// Found it!
								bFound = true;
								// Make sure
								if (schematic instanceof WeaponCraftingSchematic) {
									WeaponCraftingSchematic weaponSchem = (WeaponCraftingSchematic) schematic;
									// Get the data from the table.
									weaponSchem
											.setCraftedWeaponRequiredSkillID(r
													.getInt("requiredSkillID"));
									weaponSchem.setWeaponType(r
											.getInt("weaponType"));

									Double[] attribute;
									// Weapon type
									// TODO: No longer double-load this value.
									attribute = new Double[1];
									attribute[0] = (double) r
											.getInt("weaponType");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_TYPE, attribute);
									// Weapon damage piercing value.
									attribute = new Double[1];
									attribute[0] = (double) r
											.getInt("armorRating");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_DAMAGE_PIERCING,
											attribute);

									// Weapon damage type
									attribute = new Double[1];
									attribute[0] = (double) r
											.getInt("damageType");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_DAMAGE_TYPE,
											attribute);

									// Attack ranges
									attribute = new Double[3];
									attribute[0] = 0.0;
									attribute[1] = r.getDouble("midRange");
									attribute[2] = r.getDouble("maxRange");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_ATTACK_RANGES,
											attribute);

									// Damage radius
									attribute = new Double[2];
									attribute[0] = r.getDouble("aoeRadiusMin");
									attribute[1] = r.getDouble("aoeRadiusMax");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_DAMAGE_AOE_RADIUS,
											attribute);

									// Minimum damage range
									attribute = new Double[2];
									attribute[0] = r
											.getDouble("rangeMinDamageMin");
									attribute[1] = r
											.getDouble("rangeMinDamageMax");
									System.out.println("Min damage: "
											+ attribute[0] + ", Max damage: "
											+ attribute[1]);
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_MIN_DAMAGE_RANGE,
											attribute);

									// Maximum damage range
									attribute = new Double[2];
									attribute[0] = r
											.getDouble("rangeMaxDamageMin");
									attribute[1] = r
											.getDouble("rangeMaxDamageMax");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_MAX_DAMAGE_RANGE,
											attribute);

									// Attack speed range
									attribute = new Double[2];
									attribute[0] = r
											.getDouble("rangeAttackSpeedMin");
									attribute[1] = r
											.getDouble("rangeAttackSpeedMax");
									weaponSchem
											.setCraftingLimit(
													Constants.WEAPON_ATTACK_SPEED_RANGE,
													attribute);

									// Range mod range
									attribute = new Double[6];
									attribute[0] = r
											.getDouble("rangeZeroRangeModMin");
									attribute[1] = r
											.getDouble("rangeZeroRangeModMax");
									attribute[2] = r
											.getDouble("rangeMidRangeMinMod");
									attribute[3] = r
											.getDouble("rangeMidRangeMaxMod");
									attribute[4] = r
											.getDouble("rangeMaxRangeMinMod");
									attribute[5] = r
											.getDouble("rangeMaxRangeMaxMod");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_RANGE_TO_HIT_MODS,
											attribute);

									// Wound mod range
									attribute = new Double[2];
									attribute[0] = r.getDouble("rangeWoundMin");
									attribute[1] = r.getDouble("rangeWoundMax");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_TO_WOUND_RANGE,
											attribute);

									// Health cost range
									attribute = new Double[2];
									attribute[0] = r
											.getDouble("rangeHealthCostMin");
									attribute[1] = r
											.getDouble("rangeHealthCostMax");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_SAC_HEALTH_RANGE,
											attribute);

									// Action cost range
									attribute = new Double[2];
									attribute[0] = r
											.getDouble("rangeActionCostMin");
									attribute[1] = r
											.getDouble("rangeActionCostMax");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_SAC_ACTION_RANGE,
											attribute);

									// Mind cost range
									attribute = new Double[2];
									attribute[0] = r
											.getDouble("rangeMindCostMin");
									attribute[1] = r
											.getDouble("rangeMindCostMax");
									weaponSchem.setCraftingLimit(
											Constants.WEAPON_SAC_MIND_RANGE,
											attribute);
								} else {
									DataLog
											.logEntry(
													"Unknown schematic type for weapon schematic attribute addition on crafted item name "
															+ schematic
																	.getCraftedItemIFFFilename()
															+ ", schematic index "
															+ schematic
																	.getIndex(),
													"DatabaseInterface.loadCraftedWeaponCaps()",
													Constants.LOG_SEVERITY_CRITICAL,
													true, true);
									// System.exit(-5);
								}
							}
						}
					}
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
		} catch (Exception e) {
			System.out.println("Error loading crafted weapon caps: "
					+ e.toString());
			e.printStackTrace();

		}
	}

	protected static void saveReceivedRadials(
			Vector<RadialMenuItem> vReceivedRadials) {
		try {
			String query = "";
			Statement s = conn.createStatement();
			if (vReceivedRadials != null && !vReceivedRadials.isEmpty()) {
				for (int i = 0; i < vReceivedRadials.size(); i++) {
					RadialMenuItem r = vReceivedRadials.get(i);
					query = "Insert Into `objectradialoptions` Values(null,"
							+ r.getiItemCRC()
							+ ","
							+ r.getButtonNumber()
							+ ","
							+ r.getParentButton()
							+ ","
							+ (byte) r.getCommandID()
							+ ",'"
							+ r.getButtonText()
							+ "',"
							+ "'IFF: "
							+ DatabaseInterface.getTemplateDataByCRC(
									r.getiItemCRC()).getIFFFileName() + "',"
							+ r.getActionLocation() + ",0 );";

					s.execute(query);
				}
			}
		} catch (Exception e) {
			DataLog.logException(
					"Exception while trying to saveReceivedRadials",
					"DatabaseInterface",
					ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	protected static void loadSpecialAttackTable() {

		try {
			String query = "Select * from skill_specials;";
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					CombatAction action = new CombatAction();
					String sActionName = r.getString("attackCommandName");
					action.setCRC((int) r.getLong("attackCRC"));
					Skills skill = getSkillByCommandCRC(action.getCRC());
					if (skill != null) {
						action.setRequiredSkillID(skill.getSkillID());
					} else {
						System.out
								.println("Null skill associated with Combat Action with CRC "
										+ Integer.toHexString(action.getCRC()));
						action.setRequiredSkillID(-1);
					}
					String sCounter = r.getString("counterSTF");
					String sEvade = r.getString("evadeSTF");
					String sHit = r.getString("hitSTF");
					String sMiss = r.getString("missSTF");
					String sBlock = r.getString("blockSTF");

					if (sCounter == null) {
						sCounter = sActionName + "_counter";
					}
					if (sEvade == null) {
						sEvade = sActionName + "_evade";
					}
					if (sHit == null) {
						sHit = sActionName + "_hit";
					}
					if (sMiss == null) {
						sMiss = sActionName + "_miss";
					}
					if (sBlock == null) {
						sBlock = sActionName + "_block";
					}
					// System.out.println("Attack stf names for " + sActionName
					// + ":\nHit: " + sHit + ", Miss: " + sMiss + ", Block: " +
					// sBlock + ", Evade: " + sEvade + ", Counter: " + sCounter
					// );
					// System.out.println("Loaded combat spam names.  Hit: " +
					// sHit + ", Miss: " + sMiss + ", Evade: " + sEvade +
					// ", Block: " + sBlock + ", Counter: " + sCounter);
					action.setCombatSTFCounterSpam(sCounter);
					action.setCombatSTFEvadeSpam(sEvade);
					action.setCombatSTFHitSpam(sHit);
					action.setCombatSTFMissSpam(sMiss);
					action.setCombatSTFBlockSpam(sBlock);
					String sTargetEffect = r.getString("target_effect");
					if (sTargetEffect != null) {
						if (sTargetEffect.equals("cone")) {
							action.setConeAngle(Integer.parseInt(r.getString(
									"coneSize").split("x")[1]));
							action.setTargetEffectType(Constants.ATTACK_TYPE_CONE);
						} else if (sTargetEffect.equals("area")) {
							action.setConeAngle(Math.max(5, r.getInt("areaSize")));
							action.setTargetEffectType(Constants.ATTACK_TYPE_AOE);
						} else {
							action.setTargetEffectType(Constants.ATTACK_TYPE_SINGLE);
						}
					} else {
						action.setTargetEffectType(Constants.ATTACK_TYPE_SINGLE);
					}
					action.setAttackDelayModifier(r.getFloat("modTime"));
					action.setDamageModifier(r.getFloat("modDamage"));
					action.setHealthCostModifier(r.getFloat("modHealth"));
					action.setActionCostModifier(r.getFloat("modAction"));
					action.setMindCostModifier(r.getFloat("modMind"));
					action.setToHitModifier(r.getFloat("modHit") / 100.0f);
					action.setForceCostModifier(r.getFloat("modForceCost"));
					String sEffectOne = r.getString("effectOne");
					if (sEffectOne != null) {
						handleAddEffectToCombatAction(action, sEffectOne, r, 1);
						String sEffectTwo = r.getString("effectTwo");
						if (sEffectTwo != null) {
							handleAddEffectToCombatAction(action, sEffectTwo,
									r, 2);
							String sEffectThree = r.getString("effectThree");
							if (sEffectThree != null) {
								handleAddEffectToCombatAction(action,
										sEffectThree, r, 3);
							}
						}
					}
					String sAnimationStringUnparsed = r
							.getString("animationString");
					if (sAnimationStringUnparsed != null) {
						String[] sAllAnimations = sAnimationStringUnparsed
								.split(",");
						for (int i = 0; i < sAllAnimations.length; i++) {
							action.setCombatActionSTF(sAllAnimations[i]);
						}
					} else {
						System.out.println("Null animation list for attack "
								+ sActionName);
					}
					//action.setRequiredWeaponType(r.getInt("weaponType"));
					action.setTargetHam(r.getInt("targetHAM"));
					vSpecialAttacks.put(action.getCRC(), action);
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
		} catch (Exception e) {
			System.out.println("Error loading special attack data: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	private static void handleAddEffectToCombatAction(CombatAction action,
			String sEffectName, ResultSet r, int effectID) throws SQLException {
		// effectOne, effectTwo, effectThree. Name, chance 0 - 1, duration in
		// seconds.
		float fEffectChance;
		long lEffectDuration;
		switch (effectID) {
		case 1: {
			fEffectChance = r.getFloat("effectOneChance");
			lEffectDuration = r.getLong("effectOneDuration");
			break;
		}
		case 2: {
			fEffectChance = r.getFloat("effectTwoChance");
			lEffectDuration = r.getLong("effectTwoDuration");
			break;
		}
		case 3: {
			fEffectChance = r.getFloat("effectThreeChance");
			lEffectDuration = r.getLong("effectThreeDuration");
			break;
		}
		default: {
			System.out
					.println("Unknown effect for combat action -- not adding the effect.");
			return;
		}
		}
		// If no chance specified, it always happens.
		if (fEffectChance == 0) {
			fEffectChance = 100.0f;
		}
		// Instant effect. Probably a posture change.
		// if (lEffectDuration == 0) {
		//
		// }
		BuffEffect buffEffect = new BuffEffect();
		buffEffect.setEffectChance(fEffectChance);
		buffEffect.setTimeRemainingMS(lEffectDuration * 1000l);
		if (sEffectName.equals("posture_down")) {
			buffEffect.setStanceApplied(Constants.STANCE_PRONE);
		} else if (sEffectName.equals("posture_up")) {
			buffEffect.setStanceApplied(Constants.STANCE_STANDING);
		} else if (sEffectName.equals("knockdown")) {
			buffEffect.setStanceApplied(Constants.STANCE_KNOCKED_DOWN);
		} else if (sEffectName.equals("dizzy")) {
			buffEffect.setStateApplied(Constants.STATE_DIZZY);
		} else if (sEffectName.equals("stun")) {
			buffEffect.setStateApplied(Constants.STATE_STUNNED);
		} else if (sEffectName.equals("blind")) {
			buffEffect.setStateApplied(Constants.STATE_BLIND);
		} else if (sEffectName.equals("avoidIncapacitation")) {
			// Missing the state ID for avoid incapacitation
			// buffEffect.setStateApplied(Constants.STATE_)
		} else if (sEffectName.equals("state_bleeding")
				|| sEffectName.equals("wound")) {
			buffEffect.setStateApplied(Constants.STATE_BLEEDING);
		} else if (sEffectName.equals("CureDisease")) {
			buffEffect.setStateApplied(Constants.STATE_DISEASED);
		} else if (sEffectName.equals("CurePoison")) {
			buffEffect.setStateApplied(Constants.STATE_POISONED);
		} else {
			System.out.println("Unknown buff effect " + effectID
					+ " with name " + sEffectName);
			return;
		}
		action.setCombatEffectAtIndex(buffEffect, effectID - 1);
	}

	protected static CombatAction getCombatActionByCRC(int crc) {
		return vSpecialAttacks.get(crc);
	}

	protected static void loadLoginIntegrationData() {
		try {
			integrationData = new LoginIntegration();
			String query = "Select * from `loginintegration` Limit 1;";
			Statement s = conn.createStatement();

			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					integrationData.setLogintype(r.getInt("logintype"));
					switch (integrationData.getLogintype()) {
					case Constants.LOGIN_INTEGRATION_NOT_INTEGRATED: {
						DataLog.logEntry("Login Integration set to Internal",
								"DatabaseInterface",
								Constants.LOG_SEVERITY_INFO, true, true);
						break;
					}
					case Constants.LOGIN_INTEGRATION_VBULLETIN: // vBulletin
					{
						// ystem.out.println("Getting integration Data for vBulletin");
						// grab the integration data 2
						integrationData.setTableprefix(r.getString("tableprefix"));
						integrationData.setTablename(r.getString("tablename"));
						integrationData.setUsernamefield(r.getString("usernamefield"));
						integrationData.setPasswordfield(r.getString("passwordfield"));
						integrationData.setKeyfield(r.getString("keyfield"));
						integrationData.setEncryptiontype(r.getInt("encryptiontype"));
						integrationData.setHostname(r.getString("hostname"));
						integrationData.setSchema(r.getString("schema"));
						integrationData.setUsername(r.getString("username"));
						integrationData.setPassword(r.getString("password"));
						integrationData.setPort(r.getInt("port"));
						integrationData.setDevcsrgroupid(r.getInt("devcsrgroupid"));
						integrationData.setCsrusergroupid(r.getInt("csrusergroupid"));
						integrationData.setDevusergroupid(r.getInt("devusergroupid"));
						// System.out.println("Data Retrieved");
						// open the connection to the vBulletin Database
						// System.out.println("Creating Connection String");
						StringBuffer b = new StringBuffer();
						b.append(sDatabaseAddressPrefix);
						b.append(integrationData.getHostname()).append(":").append(integrationData.getPort());
						b.append("/");
						b.append(integrationData.getSchema());
						b.append(sDatabaseUsernamePrefix);
						b.append(integrationData.getUsername());
						b.append(sDatabasePasswordPrefix);
						b.append(integrationData.getPassword());
						String sConnString = b.toString();
						// System.out.println("Connection String: " +
						// sConnString);
						loginIntegrationConn = DriverManager.getConnection(sConnString); // Throws an
						// SQLException
						// if the
						// connection
						// attempt
						// fails.
						integrationData.setConnected(true);
						DataLog.logEntry("Login Integration set to vBulletin", "DatabaseInterface", Constants.LOG_SEVERITY_INFO, true, true);
						break;
					}
					case Constants.LOGIN_INTEGRATION_DRAGONFLY:// dragonfly
					{

						break;
					}
					}
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
		} catch (Exception e) {
			// DataLog.logException("Exception wile loading Login Integration Data",
			// "DatabaseInterface", ZoneServer.ZoneRunOptions.bLogToConsole,
			// true, e);
			System.out.println("Error loading Login Integration Data: "
					+ e.toString());
			e.printStackTrace();
		}
	}

	public static int getLoginType() {
		return integrationData.getLogintype();
	}

	public static boolean authvBulletinUser(String sUsername, String sPassword,	AccountData data) {
		boolean retval = false;
		Statement vB = null;
		Statement vB1 = null;
		// "Select * from 'logintable' where 'username' = 'Maach';
		ResultSet vBr = null; 
		ResultSet emur = null;
		try {
			vB = loginIntegrationConn.createStatement();
			vB1 = loginIntegrationConn.createStatement();
			String vBquery = "Select * From "
					//+ integrationData.getTableprefix()
					+ integrationData.getTablename() + " Where `"
					+ integrationData.getUsernamefield() + "` = '" + sUsername
					+ "';";
			
			String vBPassword = "";
			String vBsalt = "";
			String vBMemberGroupIds = "";
			String vBHashedPassword = "";
			String sUserGroups[];
			if (vB.execute(vBquery)) {
				vBr = vB.getResultSet();
				while (vBr.next()) {
					vBPassword = vBr.getString(integrationData.getPasswordfield());
					vBsalt = vBr.getString(integrationData.getKeyfield());
					vBMemberGroupIds = vBr.getString("membergroupids");
					sUserGroups = vBMemberGroupIds.split(",");
					boolean banned = false;
					boolean dev = false;
					boolean csr = false;
					data.setIsBanned(banned);
					data.setIsGM(csr);
					data.setIsDeveloper(dev);

					int devg = (integrationData.getDevusergroupid());
					int csrg = (integrationData.getCsrusergroupid());
					int bang = (integrationData.getDevcsrgroupid());
					int iUserGroup = -1;
					for (int i = 0; i < sUserGroups.length; i++) {
						if (sUserGroups[i] != null && !sUserGroups[i].isEmpty()) {
							iUserGroup = Integer.parseInt(sUserGroups[i]);
						}
						if (iUserGroup == bang) {
							banned = true;
							data.setIsBanned(banned);
						}
						if (iUserGroup == csrg) {
							csr = true;
							data.setIsGM(csr);
						}
						if (iUserGroup == devg) {
							dev = true;
							data.setIsDeveloper(dev);
						}
						iUserGroup = -1;
					}
					vBquery = "SELECT md5(CONCAT(md5('" + sPassword + "') , '"
							+ vBsalt + "')) As `hashed`;";
					if (vB1.execute(vBquery)) {
						emur = vB1.getResultSet();
						emur.next();
						vBHashedPassword = emur.getString("hashed");
						emur.close();
						emur = null;
						if (vBHashedPassword.equals(vBPassword)) {
							retval = true;
							System.out.println("Valid password.  Received: " +vBPassword + ", found " + vBHashedPassword);
							//vBAccount = true;
							// vBr.close();
							//if (vBAccount) {
								// if the password we have is not the same we
								// will update it.
								// But this can never happen -- we check to see if it's the same.  This is unreachable code.
								/*
								if (bEncryptPasswords) {
									sPassword = getMD5(sPassword);
								}
								if (data.getPassword().compareTo(sPassword) == 0) {
									if (bUpdateGroups) {
										vBquery = "Update `account` Set `gm` = "
												+ data.getIsGM()
												+ ", `banned` = "
												+ data.getIsBanned()
												+ ", `developer` = "
												+ data.getIsDeveloper()
												+ " Where `account_id` = "
												+ data.getAccountID() + ";";
										s.execute(vBquery);
										s.close();
										System.out
												.println("User Groups Updated");
									}
									return true;
								} else {
									if (bUpdateGroups) {
										vBquery = "Update `account` Set `gm` = "
												+ data.getIsGM()
												+ ", `banned` = "
												+ data.getIsBanned()
												+ ", `developer` = "
												+ data.getIsDeveloper()
												+ " Where `account_id` = "
												+ data.getAccountID() + ";";
										s.execute(vBquery);
										s.close();
										System.out
												.println("User Groups Updated");
									}

									System.out
											.println("Password Value we had did not match vBulletin. Updating.");
									data.setPassword(sPassword);
									vBquery = "Update `account` Set `password` = '"
											+ sPassword
											+ "' Where `account_id` = "
											+ data.getAccountID() + ";";
									s.execute(vBquery);
									s.close();
									return false;
								}*/
							//} else {
							
							//}
						} else {
							retval = false;
							System.out.println("Invalid password.  Received: " +vBPassword + ", found " + vBHashedPassword);
						}
					}
					vB1.close();
					vB1 = null;
				}
				vBr.close();
				vBr = null;
			}
			vB.close();
			vB = null;
		} catch (Exception e) {
			DataLog.logException(
							"Exception while attempting to authenticate vBulletin User for Login.",
							"DatabaseInterface", true, true, e);
		} finally {
			try {
				if (emur != null) {
					emur.close();
					emur = null;
				}
				if (vBr != null) {
					vBr.close();
					vBr = null;
				}
				if (vB != null) {
					vB.close();
					vB = null;
				}
				if (vB1 != null) {
					vB1.close();
					vB1 = null;
				}
			} catch (SQLException e) {
				// D'oh!
			}
		}
		return retval;
	}

	protected static boolean passwordEcryption() {
		return bEncryptPasswords;
	}

	protected static void updateInstrumentationPlayers(Vector<Player> PlayerList) {
		// ANOTHER memory leak!
		Statement s = null;
		try {
			s = conn.createStatement();
			String query = "";
			// first delete all records from the instrumentation table.
			query = "DELETE FROM `inst_players`";
			s.execute(query);
			// now update the table with fresh data
			s.close();
			//s = null;
			for (int i = 0; i < PlayerList.size(); i++) {
				s = conn.createStatement();
				Player p = PlayerList.get(i);
				query = "Insert Into `inst_players` Values(" + p.getID() + ",'"
						+ p.getFullName() + "'," + p.getX() + "," + p.getY()
						+ "," + p.getZ() + "," + p.getPlanetID() + ","
						+ p.getServerID() + ",1);";
				// System.out.println("query:[" + query + "]");
				s.execute(query);
				s.close();
				//s = null;
			}
			
		} catch (Exception e) {
			DataLog.logException("Exception in updateInstrumentationPlayers",
					"DatabaseInterface", true, true, e);
			try {
				if (s != null) {
					s.close();
					s = null;
				}
			} catch (SQLException ee) {
				// D'oh!
			}
		}
	}

	protected static void updateInstrumentationProcess(String ProcessName,
			long uptime, long memused, long memfree, long memtotal,
			int processorcount) {
		Statement s = null;
		Statement u = null; 
		ResultSet r = null;
		try {
			s = conn.createStatement();
			String query = "";
			boolean bFound = false;
			query = "Select * From `inst_server_status` Where `process` Like '"
					+ ProcessName + "';";
			if (s.execute(query)) {
				r = s.getResultSet();

				while (r.next()) {
					if (r.getString("process").equals(ProcessName)) {
						bFound = true;
						query = "Update `inst_server_status` Set `uptime` = "
								+ uptime + ", `memused` = " + memused
								+ ", `memfree` = " + memfree
								+ ", `memtotal` = " + memtotal
								+ ", `processorcount` = " + processorcount
								+ " Where `process` Like '" + ProcessName
								+ "';";
						// System.out.println("query[ " + query + "]");
						u = conn.createStatement();
						u.execute(query);
						u.close();
						u = null;
					}
				}
				r.close();
				r = null;
			}
			s.close();
			s = null;
			if (!bFound) {
				query = "Insert Into `inst_server_status` Values('"
						+ ProcessName + "'," + uptime + "," + memused + ","
						+ memfree + "," + memtotal + "," + processorcount
						+ ");";
				// System.out.println("query[ " + query + "]");
				s = conn.createStatement();
				s.execute(query);
				s.close();
				s = null;
			}
		} catch (Exception e) {
			DataLog.logException("Exception in updateInstrumentationProcess",
					"DatabaseInterface", true, true, e);
			try {
				if (r != null) {
					r.close();
					r = null;
				}
				if (u != null) {
					u.close();
					u = null;
				}
				if (s != null) {
					s.close();
					s = null;
				}
				
			} catch (SQLException ee) {
				// D'oh!
			}
		}
	}
	
	private static void verifyLoginIntegration() {
		switch (getLoginType()) {
			case Constants.LOGIN_INTEGRATION_NOT_INTEGRATED: {
				break;
			}
			case Constants.LOGIN_INTEGRATION_VBULLETIN: {
				// Do stuff
				for (int j = 0; j < vAccounts.size(); j++) {
					AccountData data = vAccounts.elementAt(j);
					String sUsername = data.getUsername();
					String sPassword = data.getPassword();
					if (sPassword.isEmpty()) {
						continue;
					}
					if (passwordEcryption()) {
						sPassword = PacketUtils.decryptPassword(sPassword);
					}
					Statement vB = null;
					Statement vB1 = null;
					// "Select * from 'logintable' where 'username' = 'Maach';
					ResultSet vBr = null; 
					ResultSet emur = null;
					try {
						vB = loginIntegrationConn.createStatement();
						vB1 = loginIntegrationConn.createStatement();
						String vBquery = "Select * From "
								+ integrationData.getTableprefix()
								+ integrationData.getTablename() + " Where `"
								+ integrationData.getUsernamefield() + "` = '" + sUsername
								+ "';";
						
						String vBPassword = "";
						String vBsalt = "";
						String vBMemberGroupIds = "";
						String vBHashedPassword = "";
						String sUserGroups[];
						if (vB.execute(vBquery)) {
							vBr = vB.getResultSet();
							while (vBr.next()) {
								vBPassword = vBr.getString(integrationData
										.getPasswordfield());
								vBsalt = vBr.getString(integrationData.getKeyfield());
								vBMemberGroupIds = vBr.getString("membergroupids");
								sUserGroups = vBMemberGroupIds.split(",");
	
								boolean banned = false;
								boolean dev = false;
								boolean csr = false;
								data.setIsBanned(banned);
								data.setIsGM(csr);
								data.setIsDeveloper(dev);
	
								int devg = (integrationData.getDevusergroupid());
								int csrg = (integrationData.getCsrusergroupid());
								int bang = (integrationData.getDevcsrgroupid());
								int iUserGroup = -1;
								for (int i = 0; i < sUserGroups.length; i++) {
									if (sUserGroups[i] != null && !sUserGroups[i].isEmpty()) {
										iUserGroup = Integer.parseInt(sUserGroups[i]);
									}
									if (iUserGroup == bang) {
										banned = true;
										data.setIsBanned(banned);
									}
									if (iUserGroup == csrg) {
										csr = true;
										data.setIsGM(csr);
									}
									if (iUserGroup == devg) {
										dev = true;
										data.setIsDeveloper(dev);
									}
									iUserGroup = -1;
								}
								vBquery = "SELECT md5(CONCAT(md5('" + sPassword + "') , '"
										+ vBsalt + "')) As `hashed`;";
								if (vB1.execute(vBquery)) {
									System.out.print("Current password: " + sPassword); 
									emur = vB1.getResultSet();
									emur.next();
									vBHashedPassword = emur.getString("hashed");
									System.out.println(", found " + vBHashedPassword);
									emur.close();
									emur = null;
									if (!vBHashedPassword.equals(sPassword)) {
										System.out.println("Password mis-match on account " + sUsername + " -- setting password blank.  Password will be updated when the user next logs in with a correct vBulletin password.");
										data.setPassword("");
										// Save the password to accounts list.
										// Update password based on forums password.
										String sUpdateAccountQuery = "Update account set password = '' where username = '" + sUsername + "';";
										Statement updateAccountStatement = conn.createStatement();
										updateAccountStatement.execute(sUpdateAccountQuery);
										updateAccountStatement.close();
										updateAccountStatement = null;
										sUpdateAccountQuery = null;
									}									
										//vBAccount = true;
										// vBr.close();
										//if (vBAccount) {
											// if the password we have is not the same we
											// will update it.
											// But this can never happen -- we check to see if it's the same.  This is unreachable code.
											/*
											if (bEncryptPasswords) {
												sPassword = getMD5(sPassword);
											}
											if (data.getPassword().compareTo(sPassword) == 0) {
												if (bUpdateGroups) {
													vBquery = "Update `account` Set `gm` = "
															+ data.getIsGM()
															+ ", `banned` = "
															+ data.getIsBanned()
															+ ", `developer` = "
															+ data.getIsDeveloper()
															+ " Where `account_id` = "
															+ data.getAccountID() + ";";
													s.execute(vBquery);
													s.close();
													System.out
															.println("User Groups Updated");
												}
												return true;
											} else {
												if (bUpdateGroups) {
													vBquery = "Update `account` Set `gm` = "
															+ data.getIsGM()
															+ ", `banned` = "
															+ data.getIsBanned()
															+ ", `developer` = "
															+ data.getIsDeveloper()
															+ " Where `account_id` = "
															+ data.getAccountID() + ";";
													s.execute(vBquery);
													s.close();
													System.out
															.println("User Groups Updated");
												}
	
												System.out
														.println("Password Value we had did not match vBulletin. Updating.");
												data.setPassword(sPassword);
												vBquery = "Update `account` Set `password` = '"
														+ sPassword
														+ "' Where `account_id` = "
														+ data.getAccountID() + ";";
												s.execute(vBquery);
												s.close();
												return false;
											}*/
										//} else {
										
										//}
									}
								vB1.close();
								vB1 = null;
							}
							vBr.close();
							vBr = null;
						}
						vB.close();
						vB = null;
					} catch (Exception e) {
						DataLog.logException(
										"Exception while attempting to authenticate vBulletin User for Login.",
										"DatabaseInterface", true, true, e);
					} finally {
						try {
							if (emur != null) {
								emur.close();
								emur = null;
							}
							if (vBr != null) {
								vBr.close();
								vBr = null;
							}
							if (vB != null) {
								vB.close();
								vB = null;
							}
							if (vB1 != null) {
								vB1.close();
								vB1 = null;
							}
						} catch (SQLException e) {
							// D'oh!
						}
					}
				}				
				
				break;
			}
			case Constants.LOGIN_INTEGRATION_DRAGONFLY: {
				
				// TODO:  Dragonfly integration.  Do stuff, but differently.
				break;
			}
			default: {
				System.out.println("Verify login integration:  Unknown integration type " + getLoginType());
			}
		}
		//System.exit(0);
	}
	
	protected static void updatePasswordForUser(String sUsername, String sPassword) {
		String query = "update account set password = '" + sPassword + "' where username = '" + sUsername + "';";
		try {
			Statement s = conn.createStatement();
			s.execute(query);
			s.close();
			s = null;
		} catch (Exception e) {
			// D'oh!
		}
	}
}

import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Enumeration;

/**
 * The Player class represents the physical appearance data of a Player which
 * can be seen by other Players.
 * 
 * @author Darryl
 * 
 */
public class Player extends SOEObject {

	public final static long serialVersionUID = 1l;
	private final static byte iNumberOfHams = 9;
	// Variables
	// Numbers
	private boolean bActive = false;
	private int iCurrentTerminalID = 0;
	private long lAccountID = 0;
	// private long lCellID = 0;
	private long lGuildID = 0;
	// private boolean bIsGM = false;
	private int moodID = 0;
	private int emoteID = 0;
	private int iFlourishID = 0;
	private int iTellCount = 0;
	private boolean bClearPerformedAnimation = false;
	private byte iStance = Constants.STANCE_STANDING;
	private int iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_STANDING;
	private int iRaceID = 0; // Human Male
	private int spawnCRC = 0; // This is the CRC value of the character's
								// species??
	private int iBankCredits = 0;
	private int iInventoryCredits = 0;
	private int iIncapTimer = 0;
	private float fScale = 0.0f;
	private int iBattleFatigue = 0;
	private float fCurrentVelocity = 0.0f;
	private float fMaxVelocity = Constants.MAX_PLAYER_RUN_SPEED_METERS_SEC;
	private float fCurrentAcceleration = 1.0f;
	private long lTargetID = 0;
	private float fTurnRadius = Constants.DEFAULT_TURN_RADIUS;
	private short iConLevel = 0;
	private int iDanceID = 0;
	private int iSongID = 0;
	private int iUnknownCREO3Bitmask = Constants.BITMASK_CREO3_PLAYER;
	private int iServerID = 0; // PlanetID for the Combined program, cluster
								// sub-server for the clustered program.
	private String sPlayerCluster;
	private int iTotalLots = 10;
	private int iUsedLots = 0;
	public long devLastShuttleID;
	// Objects
	// Arrays
	private int[] iCurrentHam;
	private int[] iMaxHam;
	private int[] iHamWounds;
	private int[] iHamEncumberance;
	private int[] iHamMigrationTarget;

	private int iHamMigrationPoints = 0;
	private final static int HAM_MIGRATION_TIME_MINIMUM_MS = 300000;
	private final static int HAM_MIGRATION_TIME_MAXIMUM_MS = 600000;
	private final static long HAM_MIGRATION_TIME_MINIMUM_KK_MS = PacketUtils
			.toKK(HAM_MIGRATION_TIME_MINIMUM_MS);
	private final static long HAM_MIGRATION_TIME_MAXIMUM_KK_MS = PacketUtils
			.toKK(HAM_MIGRATION_TIME_MAXIMUM_MS);
	private long lState; // Vectors
	private Vector<TangibleItem> vInventoryItemsList;
	private Vector<SkillMods> vSkillModsList; // This one has to be there.
	private Vector<PlayerFactions> vFactionList; // This one kinda has to be
													// here... need the faction
													// AND the faction amount.
	// Strings
	private String sFirstName;
	private String sLastName;
	private String sPerformedAnimation;
	private String sClientEffectString;
	private String sMoodString = "none";
	private String sPerformanceString;
	private String sBiography;
	private String sStartingPlanetName = null;
	private String sStartingProfession = null;
	private float[] fStartingCoordinates;
	private float[] fHouseCoordinates;
	private float[] fBankCoordinates;
	private int iBankPlanetID = -1;
	private int iHomePlanetID = -1;
	// SOE Objects / extensions
	private TangibleItem tHairObject;
	private TangibleItem tPlayerInventory;
	private TangibleItem tDatapad;
	private TangibleItem tBank;
	private TangibleItem tMissionBag;
	private Weapon equippedWeapon;
	private Weapon defaultWeapon;
	private Instrument equippedInstrument;

	private PlayerItem thePlayer;
	// Server related
	private int iRebelFactionPoints = 0; // These could be part of the
											// PlayerFactions vector...
	private int iImperialFactionPoints = 0;
	private ConcurrentHashMap<Long, Structure> vPlayerStructures;
	private boolean bIsDeleted;
	private long lTotalPlayerOnlineTime;
	private long lPlayerCreationDate;
	private boolean bEnteringTutorial;
	private TutorialObject tutorial;

	private boolean bIsJedi = false;
	/**************************************************************/
	/************ These Values will vary with skills **************/
	private int iMaxCalledDroids; // def 2
	private int iMaxCalledAnimalPets; // def 1
	private int iMaxCalledFactionPets; // def 2

	private int iMaxDataPadDroids; // def 4
	private int iMaxDataPadAnimalPets; // def 3
	private int iMaxDataPadFactionPets; // def 2

	private int iMaxCalledPets; // def 2
	private int iMaxDataPadPets; // def 9
	/**************************************************************/

	/************************************************************
	 ***************** TRANSIENT VARIABLES **********************
	 ************************************************************/
	private transient int iDefenderListUpdateCounter = 0;
	private transient int iCurrentHamUpdateCount = 0;
	private transient int iMaxHamUpdateCount = 0;
	private transient int iHamModifiersUpdateCount = 0;
	private transient int iHamWoundsUpdateCount = 0;
	private transient int iHamEncumberanceUpdateCount = 0;
	private transient boolean bIsMounted = false;
	private transient int iEquippedItemUpdateCount = 0;
	private transient long lCurrentDeedInPlacementMode;
	// private transient boolean isWarping = false;
	// private transient boolean isLoading = false;
	public transient int iWarpOperation; // YAK! Why public? It was public for
											// testing, warp will be revamped on
											// the next patch MUAHAHAHAHA!
	private transient boolean bOnlineStatus;
	private transient int iFriendsListUpdateCounter;
	private transient long lBurstRunexpiryTimer = 0;
	private transient boolean bBurstRunning = false;
	private transient long lBurstRunRechargeTimer = 0;
	private transient long lNextHeightMapUpdate;
	private transient boolean bHeightMapCollect;
	private transient int iGroupInviteCounter;
	private transient long lGroupHost;
	private transient long lGroupTime;
	private transient int iGroupUpdateCounter;
	private transient int iSkillModsUpdateCount = 0;
	private transient int LastSUIBox;
	private transient int iLastSuiWindowType;
	private transient boolean bIsTraveling = false;
	private transient long lCurrentMount = 0;
	private transient long lLastConversationNPC = 0;
	private transient int iSkillBeingPurchasedIndex;
	private transient int iSkillListUpdateCount = 0;
	private transient int iTradeRequestCounter;
	private transient boolean bPlayerRequestedLogout;
	private transient long lLogoutTimer;
	private transient long lLogoutSpamTimer;
	// private transient int currentTimeOn;
	private transient long heartbeatTimer;
	private transient long lHealDelayMS = 0;
	private transient boolean bHasOutstandingTeachingOffer = false;
	private transient boolean bDisconnectIgnore;
	private transient int iPlaySoundUpdateCounter;
	private transient long lListeningToID;
	private transient boolean bIsInCamp;
	private transient long forageCooldown;
	private transient boolean isForaging;
	private transient byte forageType;
	private transient LinkedList<CommandQueueItem> vCommandQueue;
	private transient String sLastSuiWindowType;
	private transient Hashtable<Integer, SOEObject> SUIListWindowObjectList;
	private transient ConcurrentHashMap<Integer, SUIWindow> PendingSUIWindowList;
	private transient String[] sLastConversationMenu;
	private transient Vector<DialogOption> vLastConversationMenuOptions;
	private transient Vector<TradeObject> vTradeRequests;
	private transient Vector<Long> vIncomingTradeRequest;
	private transient TradeObject currentTradeObject;
	private transient Player teachingStudent = null;
	private transient Player teacher;
	private transient Skills skillOfferedByTeacher = null;
	private transient SOEObject synchronizedObject = null;
	private transient Vector<CraftingSchematic> vLastCreatedCraftingSchematicList = null;
	private transient Camp currentCampObject;
	private transient Vector<CreaturePet> vCalledPets;
	private transient Vector<CreaturePet> vFriendPets;
	private transient Waypoint lastForageArea;
	private transient ManufacturingSchematic currentManufacturingSchematic;
	private transient Vector<Player> vPlayersListening;
	private transient Vector<Player> vPlayersWatching;
	private transient Player playerBeingListened;
	private transient Player playerBeingWatched;
	private transient boolean isDancing;
	private transient boolean isPlayingMusic;
	private transient long lDancetick;
	private transient long lMusicTick;
	private transient int iFlourishBonus;
	private transient long lFlourishTick;
	private transient long lEffectTick;
	private transient long lRandomWatchPlayerTick;
	private transient long lMaskScentDelayTime;
	private transient int iPerformanceID;
	private transient boolean C60X11;

	private transient int[] iHamModifiers; // Buffs
	private transient long[] lHamMigrationRateKKMS;
	private transient long[] lMaxHamKK; // Bitsets
	private transient long[] iHamRegenerationKKMS;
	private transient long[] lPoisonTickTimeMS;
	private transient long[] lBleedTickTimeMS;
	private transient long[] lDiseaseTickTimeMS;
	private transient long[] lFireTickTimeMS;
	private transient int[] iPoisonPotency;
	private transient int[] iBleedPotency;
	private transient int[] iDiseasePotency;
	private transient int[] iFirePotency;
	private transient long[] lPoisonTimeMS;
	private transient long[] lDiseaseTimeMS;
	private transient long[] lFireTimeMS;
	private transient long[] lBleedTimeMS;

	private transient long[] lStateTimer;

	private transient Vector<Long> vDefenderList; // This can be a Vector<Long>
	private transient Vector<Long> vAllSpawnedObjectIDs;
	private transient Vector<SOEObject> vDelayedSpawnObjects; // This is an
																// extremely bad
																// way of doing
																// this.
	private transient Vector<Long> lNextDelayedSpawn;
	private transient TangibleItem lastUsedSurveyTool = null;
	private transient ZoneServer server;
	private transient ZoneClient client;
	private transient Group myGroup;
	private transient Terminal LastUsedTravelTerminal;
	private transient BuffEffect[] vBuffEffects;

	private transient boolean bIsSampling;

	/**
	 * private boolean bEnteringTutorial; private TutorialObject tutorial;
	 * 
	 * private transient boolean bDisconnectIgnore;
	 */
	/**
	 * Constructs a new Player for use on the given Zone Server.
	 * 
	 * @param server
	 *            -- The Zone Server this Player belongs on.
	 */
	public Player(ZoneServer server) {
		super();
		// System.out.println("Creating New Player Object");
		/*
		 * StackTraceElement [] ste = Thread.currentThread().getStackTrace();
		 * for(int i = 0; i < ste.length;i++) { System.out.println("ST: " + i +
		 * " | " + ste[i].toString()); }
		 */
		iPlaySoundUpdateCounter = -1;
		heartbeatTimer = 0;
		lPlayerCreationDate = System.currentTimeMillis();
		bPlayerRequestedLogout = false;
		bIsDeleted = false;
		if (server != null) {
			this.server = server;
			setServerID(server.getServerID());
			sPlayerCluster = server.getClusterName();
			thePlayer = new PlayerItem(this);
			// setSTFFileName(SOEObject.PLAYER_STF);
		}
		iCurrentHam = new int[iNumberOfHams];
		iMaxHam = new int[iNumberOfHams];
		iHamRegenerationKKMS = new long[iNumberOfHams];
		iHamModifiers = new int[iNumberOfHams];
		iHamWounds = new int[iNumberOfHams];
		iHamEncumberance = new int[iNumberOfHams];
		iHamMigrationTarget = new int[iNumberOfHams];
		lHamMigrationRateKKMS = new long[iNumberOfHams];
		lMaxHamKK = new long[iNumberOfHams];
		vBuffEffects = new BuffEffect[Constants.BUFF_EFFECTS.length];
		lPoisonTickTimeMS = new long[iNumberOfHams];
		lBleedTickTimeMS = new long[iNumberOfHams];
		lDiseaseTickTimeMS = new long[iNumberOfHams];
		lFireTickTimeMS = new long[iNumberOfHams];
		lPoisonTimeMS = new long[iNumberOfHams];
		lBleedTimeMS = new long[iNumberOfHams];
		lDiseaseTimeMS = new long[iNumberOfHams];
		lFireTimeMS = new long[iNumberOfHams];
		iPoisonPotency = new int[iNumberOfHams];
		iBleedPotency = new int[iNumberOfHams];
		iDiseasePotency = new int[iNumberOfHams];
		iFirePotency = new int[iNumberOfHams];
		lStateTimer = new long[Constants.NUM_STATES];
		lState = 0;
		// Vectors
		vAllSpawnedObjectIDs = new Vector<Long>();
		vInventoryItemsList = new Vector<TangibleItem>();
		// vCommandQueue = new LinkedList<CommandQueueItem>();
		vSkillModsList = new Vector<SkillMods>();
		vDefenderList = new Vector<Long>();
		vFactionList = new Vector<PlayerFactions>();
		vCommandQueue = new LinkedList<CommandQueueItem>();

		// Strings
		sFirstName = "";
		sLastName = "";
		sPerformedAnimation = "";
		sClientEffectString = "";
		sMoodString = "none";
		sPerformanceString = "";

		sBiography = "";
		sStartingPlanetName = "";
		sStartingProfession = "";

		// SOE Objects / extensions
		tHairObject = null;
		tPlayerInventory = null;
		equippedWeapon = null;
		myGroup = null;
		// Server related
		client = null;
		bOnlineStatus = false;
		iFriendsListUpdateCounter = 0;
		fStartingCoordinates = new float[3];
		fBankCoordinates = new float[3];
		fHouseCoordinates = new float[3];
		LastSUIBox = 0;
		SUIListWindowObjectList = new Hashtable<Integer, SOEObject>();
		PendingSUIWindowList = new ConcurrentHashMap<Integer, SUIWindow>();
		sLastConversationMenu = new String[7];
		vLastConversationMenuOptions = new Vector<DialogOption>();
		lNextDelayedSpawn = new Vector<Long>();
		/*
		 * vMissionBag = new Vector<MissionObject>(); for(int i = 0; i < 15;
		 * i++) { MissionObject m = new MissionObject();
		 * m.setID(client.getServer().getNextObjectID());
		 * m.setSMissionSTFString(""); m.setSMissionTag1("");
		 * m.setSMissionGiver(new String [1]); m.setSMissionTag2("");
		 * m.setIDisplayObjectCRC(0xE191DBAB); m.setOrigX(0); m.setOrigY(0);
		 * m.setOrigZ(0); m.setX(0); m.setY(0); m.setZ(0); vMissionBag.add(m); }
		 */

		/* Set Default Pet values */
		iMaxCalledDroids = 2; // def 2
		iMaxCalledAnimalPets = 1; // def 1
		iMaxCalledFactionPets = 2; // def 2

		iMaxDataPadDroids = 4; // def 4
		iMaxDataPadAnimalPets = 3; // def 3
		iMaxDataPadFactionPets = 2; // def 2

		iMaxCalledPets = 2; // def 2
		iMaxDataPadPets = 9; // def 9
		/*----------------*/
	}

	public boolean isEnteringTutorial() {
		return bEnteringTutorial;
	}

	public void setEnteringTutorial(boolean bEnteringTutorial) {
		this.bEnteringTutorial = bEnteringTutorial;
	}

	public TutorialObject getTutorial() {
		return tutorial;
	}

	public void setTutorial(TutorialObject tutorial) {
		this.tutorial = tutorial;
	}

	public void setFriendsListUpdateCounter(int i) {
		iFriendsListUpdateCounter = i;
	}

	public int getFriendsListUpdateCounter(boolean bIncrement) {
		if (bIncrement) {
			iFriendsListUpdateCounter++;
		}
		return iFriendsListUpdateCounter;
	}

	public void setOnlineStatus(boolean s) {
		bOnlineStatus = s;
	}

	public boolean getOnlineStatus() {
		return bOnlineStatus;
	}

	public String getPlayerCluster() {
		return sPlayerCluster;
	}

	public void fixPlayerCluster(String sClusterName) {
		sPlayerCluster = sClusterName;
	}

	/**
	 * Returns the location this Player first appeared at in the SWG world.
	 * 
	 * @return The player's birthplace.
	 */
	public String getBirthplace() {
		return sStartingPlanetName;
	}

	/**
	 * Initializes the player's birthplace for biographical purposes.
	 * 
	 * @param s
	 *            -- The point where the Player first appeared at in the SWG
	 *            world.
	 */
	public void initializeBirthplace(String s) {
		sStartingPlanetName = s;
	}

	/**
	 * Sets the player's birthplace for biographical purposes. If the birthplace
	 * has already been set, this function does nothing.
	 * 
	 * @param s
	 *            -- The birthplace.
	 */
	public void setBirthplace(String s) {
		if (sStartingPlanetName == null || sStartingPlanetName.equals("")) {
			sStartingPlanetName = s;
		}
	}

	// Important note: Since an array variable is a pointer, these CANNOT point
	// to the exact same object.
	// Therefore, we MUST make a copy of the object.
	/**
	 * This function initializes the Player's HAM bars.
	 * 
	 * @param i
	 *            -- The HAM.
	 */
	public void setHam(int[] hams) {
		iMaxHam = hams;
		for (int i = 0; i < hams.length; i++) {
			lMaxHamKK[i] = PacketUtils.toKK(hams[i]);
			iCurrentHam[i] = iMaxHam[i];
		}
		iCurrentHam = Arrays.copyOf(hams, hams.length);
		iHamMigrationTarget = Arrays.copyOf(hams, hams.length);
		for (int i = 0; i <= iMaxHam.length - 3; i += 3) {
			iHamRegenerationKKMS[i] = PacketUtils.toKK(iMaxHam[i + 2])
					/ TIME_DIVIDER_ON_REGENERATION_RATE_MS; // Regenerate rate
															// -- if Action,
															// regenerate your
															// Stamina in action
															// every 100
															// seconds.
		}
	}

	/**
	 * This function updates the current HAM bar of index with the new value.
	 * 
	 * @param index
	 *            -- The sub HAM bar to update.
	 * @param newHam
	 *            -- The new value of the given sub HAM bar.
	 */
	protected synchronized int updateCurrentHam(int index, int newHam) {
		int iDifference = 0;
		if (iStance != Constants.STANCE_DEAD) {
			int iPreviousHam = iCurrentHam[index];
			iCurrentHam[index] = Math
					.min(
							(iCurrentHam[index] + newHam),
							((iMaxHam[index] + iHamModifiers[index]) - iHamWounds[index]));
			iDifference = iCurrentHam[index] - iPreviousHam;
			// System.out.println("Update current ham.  Previous: " +
			// iPreviousHam + ", current: " + iCurrentHam[index]);
			if (iCurrentHam[index] != iPreviousHam) {
				// Deltas message.
				// System.out.println("New currentHam["+index+"] = " +
				// iCurrentHam[index]);
				if (client != null) {
					try {
						client.insertPacket(PacketFactory.buildHAMDelta(this,
								(byte) 6, (short) 13, index,
								iCurrentHam[index], true),
								Constants.PACKET_RANGE_CHAT_RANGE);
					} catch (Exception e) {
						// D'oh!
						DataLog.logEntry("Error building ham deltas message: "
								+ e.toString(), "Player",
								Constants.LOG_SEVERITY_CRITICAL,
								ZoneServer.ZoneRunOptions.bLogToConsole, false);
						// System.out.println("Error building ham deltas message: "
						// + e.toString());
						e.printStackTrace();
					}
				}
			}
			if (iCurrentHam[index] < 0) {
				// Incapacitate me!

				if (iStance == Constants.STANCE_INCAPACITATED) {
					// We're still incapacitated.
					lIncapacitationRemainingTimeMS = Math.max(PacketUtils
							.toKK(Math.abs(iCurrentHam[index]))
							/ iHamRegenerationKKMS[index],
							lIncapacitationRemainingTimeMS);
					updateIncapacitationTimer(lIncapacitationRemainingTimeMS);
					iIncapTimer = (int) (lIncapacitationRemainingTimeMS / 1000);
				} else {
					removeState(Constants.STATE_COMBAT);
					if (this instanceof NPC) {
						setStance(null, Constants.STANCE_DEAD, true); // If I'm
																		// an
																		// NPC,
																		// I
																		// straight
																		// up
																		// die.
					} else {
						setStance(null, Constants.STANCE_INCAPACITATED, true); // If
																				// I'm
																				// a
																				// Player,
																				// I
																				// might
																				// incapacitate,
																				// or
																				// die,
																				// depending
																				// on
																				// how
																				// often
																				// I
																				// incapacitate.
					}
					// removeState(Constants.STATE_COMBAT);
					if (lastUsedSurveyTool != null) {
						lastUsedSurveyTool.stopSurveying();
						bIsSampling = false;
					}
					// How long to regenerate?
					if (thePlayer != null) {
						if (!thePlayer.setPlayerJustIncapacitated()) {
							lIncapacitationRemainingTimeMS = Math.max(
									PacketUtils.toKK(Math
											.abs(iCurrentHam[index]))
											/ iHamRegenerationKKMS[index],
									lIncapacitationRemainingTimeMS);
							updateIncapacitationTimer(lIncapacitationRemainingTimeMS);
							iIncapTimer = (int) (lIncapacitationRemainingTimeMS / 1000);
						} else {
							setStance(null, Constants.STANCE_DEAD, true);
							bSeenCloneWindow = false;
						}
					}
				}
			}
		}
		return iDifference;
	}

	private transient boolean bSeenCloneWindow = false;
	private long lIncapacitationRemainingTimeMS = 0;

	private void updateIncapacitationTimer(long lTimerMS) {
		int iPreviousIncapTimer = iIncapTimer;
		iIncapTimer = (int) (lTimerMS / 1000);
		if (iIncapTimer != iPreviousIncapTimer) {
			try {
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 3, (short) 1,
						(short) 7, this, iIncapTimer));
			} catch (Exception e) {
				// D'oh!
			}
		}
	}

	/**
	 * This function updates the max HAM bar of index with the new value.
	 * 
	 * @param index
	 *            -- The sub HAM bar to update.
	 * @param newHam
	 *            -- The new value of the given sub HAM bar.
	 */
	protected synchronized void updateMaxHam(int index, int newHam,
			boolean bUpdateCurrentHam, boolean bFlyText) {

		int iPreviousHam = iMaxHam[index];
		iMaxHam[index] = newHam;
		// System.out.println("Update current ham.  Previous: " + iPreviousHam +
		// ", current: " + iCurrentHam[index]);
		if (iMaxHam[index] != iPreviousHam) {
			if (index % 3 == 2) {
				iHamRegenerationKKMS[index - 2] = PacketUtils
						.toKK(iMaxHam[index])
						/ TIME_DIVIDER_ON_REGENERATION_RATE_MS; // Regenerate
																// rate -- if
																// Action,
																// regenerate
																// your Stamina
																// in action
																// every 100
																// seconds.
			}
			// Deltas message.
			// System.out.println("New currentHam["+index+"] = " +
			// iCurrentHam[index]);
			try {
				client.insertPacket(PacketFactory.buildHAMDelta(this, (byte) 1,
						(short) 2, index, iMaxHam[index], bFlyText),
						Constants.PACKET_RANGE_CHAT_RANGE);
				if (bUpdateCurrentHam) {
					updateCurrentHam(index, newHam);
				}
			} catch (Exception e) {
				// D'oh!
				DataLog.logEntry("Error building ham deltas message: "
						+ e.toString(), "Player",
						Constants.LOG_SEVERITY_CRITICAL,
						ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
				// System.out.println("Error building ham deltas message: " +
				// e.toString());
				e.printStackTrace();
			}
		}

	}

	/**
	 * This function updates the current HAM wounds bar of index with the new
	 * value.
	 * 
	 * @param index
	 *            -- The sub HAM wounds bar to update.
	 * @param newHam
	 *            -- The amount to alter the HAM wound bar by.
	 */
	protected synchronized void updateHAMWounds(int index, int hamDelta,
			boolean bFlyText) {
		int iPreviousHam = iHamWounds[index];
		iHamWounds[index] = Math.max((iHamWounds[index] + hamDelta), 0);
		iHamWounds[index] = Math.min(iHamWounds[index], (iMaxHam[index] - 1));
		// System.out.println("Update current ham.  Previous: " + iPreviousHam +
		// ", current: " + iCurrentHam[index]);
		if (iHamWounds[index] != iPreviousHam) {
			// Deltas message.
			System.out.println("New HAMWounds[" + index + "] = "
					+ iHamWounds[index]);
			try {
				if (client != null) {
					client.insertPacket(PacketFactory.buildHAMDelta(this,
							(byte) 3, (short) 0x11, index, iHamWounds[index],
							bFlyText), Constants.PACKET_RANGE_CHAT_RANGE);
				}
			} catch (Exception e) {
				// D'oh!
				DataLog.logEntry("Error building ham deltas message: "
						+ e.toString(), "Player",
						Constants.LOG_SEVERITY_CRITICAL,
						ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
				// System.out.println("Error building ham deltas message: " +
				// e.toString());
				e.printStackTrace();
			}
			if (iCurrentHam[index] > ((iMaxHam[index] + iHamModifiers[index]) - iHamWounds[index])) {
				int difference = ((iMaxHam[index] + iHamModifiers[index]) - iHamWounds[index])
						- iCurrentHam[index];
				System.out.println("HAM update needed.  Altering ham down by "
						+ (difference * -1));
				updateCurrentHam(index, difference);
			}
		}
	}

	/**
	 * This function updates the max HAM bar of index with the new value.
	 * 
	 * @param index
	 *            -- The sub HAM bar to update.
	 * @param newHam
	 *            -- The new value of the given sub HAM bar.
	 */
	protected synchronized void updateHamModifiers(int index, int newHam,
			boolean bUpdateCurrentHam, boolean bFlyText) {

		int iPreviousHam = iHamModifiers[index];
		iHamModifiers[index] = newHam;
		// System.out.println("Update current ham.  Previous: " + iPreviousHam +
		// ", current: " + iCurrentHam[index]);
		if (iHamModifiers[index] != iPreviousHam) {
			if (index % 3 == 2) {
				iHamRegenerationKKMS[index - 2] = PacketUtils
						.toKK(iMaxHam[index])
						/ TIME_DIVIDER_ON_REGENERATION_RATE_MS; // Regenerate
																// rate -- if
																// Action,
																// regenerate
																// your Stamina
																// in action
																// every 100
																// seconds.
			}
			// Deltas message.
			// System.out.println("New currentHam["+index+"] = " +
			// iCurrentHam[index]);
			try {
				client.insertPacket(PacketFactory.buildHAMDelta(this, (byte) 6,
						(short) 0x0E, index, iHamModifiers[index]
								+ iMaxHam[index], bFlyText),
						Constants.PACKET_RANGE_CHAT_RANGE);
				if (bUpdateCurrentHam) {
					updateCurrentHam(index, iCurrentHam[index] + newHam);
				}
			} catch (Exception e) {
				// D'oh!
				DataLog.logEntry("Error building ham deltas message: "
						+ e.toString(), "Player",
						Constants.LOG_SEVERITY_CRITICAL,
						ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
				// System.out.println("Error building ham deltas message: " +
				// e.toString());
				e.printStackTrace();
			}
		}

	}

	/**
	 * Sets the ID of the Zone Server which this Player resides on.
	 * 
	 * @param i
	 *            -- The server ID.
	 */
	public void setServerID(int i) {
		iServerID = i;
	}

	/**
	 * Gets the ID of the Zone Server which this Player resides on.
	 * 
	 * @return The server ID.
	 */
	public int getServerID() {
		return iServerID;
	}

	/**
	 * Sets the Object ID of the Guild this Player is a member of.
	 * 
	 * @param l
	 *            -- The Guild ID.
	 */
	public void setGuildID(long l) {
		lGuildID = l;
	}

	/**
	 * Gets the Object ID of the Guild this Player is a member of.
	 * 
	 * @return The Guild ID.
	 */
	public long getGuildID() {
		return lGuildID;
	}

	/**
	 * Sets the Account ID this Player belongs to.
	 * @param l -- The Account ID.
	 */
	public void setAccountID(long l) {
		lAccountID = l;
	}

	/**
	 * Gets the Account ID this Player belongs to.
	 * 
	 * @return The Account ID
	 */
	public long getAccountID() {
		return lAccountID;
	}

	private final static long lUpdateIntervalMS = 300000;
	private long lDatabaseUpdateTimeMS = 0;

	private transient Vector<POI> vPOIsThisPlanet;
	private final static int DISTANCE_TO_TRIGGER_GRANT_POI = 20;

	private long lHamUpdateTimerMS = 0; // TODO: This value must be fiddled /
										// reset / ignored if we're in combat,
										// as we need ham updates "instantly"
										// when we're fighting.
	private final static long MAX_TIME_MS_BETWEEN_HAM_UPDATE = 1000;
	private final static long TIME_DIVIDER_ON_REGENERATION_RATE_MS = 100000;
	private transient Vector<MapLocationData> vLastSeenCloneLocations = null;

	protected Vector<MapLocationData> getLastSeenCloneLocations() {
		return vLastSeenCloneLocations;
	}

	/**
	 * The main update loop for the Player.
	 * 
	 * @param lDeltaTimeMS
	 *            -- The elapsed time since the last update.
	 */
	public void update(long lDeltaTimeMS) {
		// System.out.println("Player update -- elapsed time: " + lDeltaTimeMS);
		if (bOnlineStatus) {
			lTotalPlayerOnlineTime += lDeltaTimeMS;
		}
		if (vBuffEffects != null) {
			for (int i = 0; i < vBuffEffects.length; i++) {
				BuffEffect effect = vBuffEffects[i];
				if (effect != null) {
					effect.update(lDeltaTimeMS);
				}
			}
		} else {
			vBuffEffects = new BuffEffect[Constants.BUFF_EFFECTS.length];

			lPoisonTimeMS = new long[iNumberOfHams];
			lBleedTimeMS = new long[iNumberOfHams];
			lDiseaseTimeMS = new long[iNumberOfHams];
			lFireTimeMS = new long[iNumberOfHams];
			lPoisonTickTimeMS = new long[iNumberOfHams];
			lBleedTickTimeMS = new long[iNumberOfHams];
			lDiseaseTickTimeMS = new long[iNumberOfHams];
			lFireTickTimeMS = new long[iNumberOfHams];
			iPoisonPotency = new int[iNumberOfHams];
			iBleedPotency = new int[iNumberOfHams];
			iDiseasePotency = new int[iNumberOfHams];
			iFirePotency = new int[iNumberOfHams];
			lStateTimer = new long[Constants.NUM_STATES];
			vCommandQueue = new LinkedList<CommandQueueItem>();
		}

		if (iHamRegenerationKKMS == null) {
			iHamRegenerationKKMS = new long[iMaxHam.length];
			for (int i = 0; i < iMaxHam.length; i += 3) {
				iHamRegenerationKKMS[i] = PacketUtils.toKK(iMaxHam[i + 2])
						/ TIME_DIVIDER_ON_REGENERATION_RATE_MS; // Regenerate
																// rate -- if
																// Action,
																// regenerate
																// your Stamina
																// in action
																// every 100
																// seconds.
			}
		}
		if (bClearPerformedAnimation) {
			sPerformedAnimation = null;
			bClearPerformedAnimation = false;
		}
		if (thePlayer != null) {
			thePlayer.update(lDeltaTimeMS);
		}
		if (lHealDelayMS > 0) {
			lHealDelayMS -= lDeltaTimeMS;
			if (lHealDelayMS <= 0) {
				System.out.println("Able to heal again.");
				try {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"healing_response", "healing_response_58", 0l, "",
							"", "", 0l, "", "", "", 0l, "", "", "", 0, 0f,
							false));
				} catch (Exception e) {
					System.out
							.println("Error inserting chat system message for healing response: "
									+ e.toString());
					e.printStackTrace();
				}
			}
		}
		if (lMaskScentDelayTime > 0) {
			lMaskScentDelayTime -= lDeltaTimeMS;
		}
		// Note: For this code to work correctly with various other systems
		// (riding vehicles, for example),
		// their states must have the timers set to -1 when they are applied.
		for (int i = 0; i < lStateTimer.length; i++) {
			if (lStateTimer[i] > 0) {
				lStateTimer[i] = Math.max(0, lStateTimer[i] - lDeltaTimeMS);
				if (lStateTimer[i] == 0) {
					if (i == Constants.STATE_BLEEDING) {
						for (int j = 0; j < lBleedTimeMS.length; j++) {
							lStateTimer[i] = Math.max(lStateTimer[i],
									lBleedTimeMS[j]);
						}
						if (lStateTimer[i] == 0) {
							removeState(i);
						}
					} else if (i == Constants.STATE_POISONED) {
						for (int j = 0; j < lPoisonTimeMS.length; j++) {
							lStateTimer[i] = Math.max(lStateTimer[i],
									lPoisonTimeMS[j]);
						}
						if (lStateTimer[i] == 0) {
							removeState(i);
						}

					} else if (i == Constants.STATE_DISEASED) {
						for (int j = 0; j < lDiseaseTimeMS.length; j++) {
							lStateTimer[i] = Math.max(lStateTimer[i],
									lDiseaseTimeMS[j]);
						}
						if (lStateTimer[i] == 0) {
							removeState(i);
						}

					} else if (i == Constants.STATE_BURNING) {
						for (int j = 0; j < lFireTimeMS.length; j++) {
							lStateTimer[i] = Math.max(lStateTimer[i],
									lFireTimeMS[j]);
						}
						if (lStateTimer[i] == 0) {
							removeState(i);
						}

					} else {
						if (i == Constants.STATE_SCENT_MASKED) {
							try {
								client.insertPacket(PacketFactory
										.buildChatSystemMessage("skl_use",
												"sys_scentmask_stop"));
							} catch (IOException e) {
								// D'oh!
							}
						}
						removeState(i);
					}
				}
			}
		}
		// TODO: Put all of this into updateHam(lDeltaTimeMS);

		updateCommandQueueEnqueues(lDeltaTimeMS);
		updatePoisons(lDeltaTimeMS);
		updateDiseases(lDeltaTimeMS);
		updateBleeds(lDeltaTimeMS);
		updateFires(lDeltaTimeMS);

		lHamUpdateTimerMS += lDeltaTimeMS;
		if (lHamUpdateTimerMS >= MAX_TIME_MS_BETWEEN_HAM_UPDATE) {
			for (int i = 0; i < iCurrentHam.length; i++) {
				if (iHamMigrationTarget[i] != iMaxHam[i]) {
					if (lHamMigrationRateKKMS[i] == 0) {
						lHamMigrationRateKKMS[i] = SWGGui.getRandomLong(
								HAM_MIGRATION_TIME_MINIMUM_KK_MS,
								HAM_MIGRATION_TIME_MAXIMUM_KK_MS);
					} else {
						boolean direction = (iHamMigrationTarget[i] > iMaxHam[i]);
						long lGainedHamKK = lHamMigrationRateKKMS[i]
								* lHamUpdateTimerMS;
						lMaxHamKK[i] += lGainedHamKK;
						iMaxHam[i] = (int) (PacketUtils.unKK(lMaxHamKK[i]));
						if (direction) {
							if (iMaxHam[i] >= iHamMigrationTarget[i]) {
								iMaxHam[i] = iHamMigrationTarget[i];
							}
						} else {
							if (iMaxHam[i] < iHamMigrationTarget[i]) {
								iMaxHam[i] = iHamMigrationTarget[i];
							}
						}
					}
				}

				if (iCurrentHam[i] < ((iMaxHam[i] + iHamModifiers[i]) - iHamWounds[i])) {
					long lHamUpdateKK = (iHamRegenerationKKMS[i] * lHamUpdateTimerMS);
					long lHamUpdateValue = PacketUtils.unKK(lHamUpdateKK);
					// System.out.println("Player stance: " + iStance);
					if (iStance == Constants.STANCE_SITTING) {
						lHamUpdateValue = (lHamUpdateValue * 14) / 10;
					} else if ((lState & Constants.STATE_COMBAT) != 0) {
						lHamUpdateValue = (lHamUpdateValue * 4) / 10;
					}
					// System.out.println("Add " + lHamUpdateValue +
					// " to index " + i);
					updateCurrentHam(i, (int) lHamUpdateValue);
				}
			}
			lHamUpdateTimerMS = 0;
		}
		if (lIncapacitationRemainingTimeMS > 0) {
			lIncapacitationRemainingTimeMS -= lDeltaTimeMS;
			updateIncapacitationTimer(lIncapacitationRemainingTimeMS);
			if (lIncapacitationRemainingTimeMS <= 0) {
				setStance(null, Constants.STANCE_STANDING, true);
			}
		}
		// From there to here.
		lDatabaseUpdateTimeMS -= lDeltaTimeMS;
		if (lDatabaseUpdateTimeMS < 0) {
			lDatabaseUpdateTimeMS = lUpdateIntervalMS;
			server.getGUI().getDB().updatePlayer(this, false, false);
		}

		try {
			// this updates the players position and checks to see if the player
			// has a poi badge or not
			// if the player does not have it and is within the range og the poi
			// we give the player the badge.
			// Ack! We're getting the POI list EVERY SINGLE LOOP?
			vPOIsThisPlanet = DatabaseInterface.getPOIListForPlanetID(this
					.getPlanetID());

			if (!vPOIsThisPlanet.isEmpty()) {
				for (int i = 0; i < vPOIsThisPlanet.size(); i++) {
					POI T = vPOIsThisPlanet.get(i);
					if (!thePlayer.hasBadge(T.getBadgeID())
							&& client.getClientReadyStatus()) {
						// We're declaring a new SOEObject multiple times each
						// time through this loop?
						float x2 = T.getX();
						float y2 = T.getY();
						float x1 = getX();
						float y1 = getY();
						float deltaXSqr = (x2 - x1) * (x2 - x1);
						float deltaYSqr = (y2 - y1) * (y2 - y1);
						float distance = (float) Math.sqrt(deltaXSqr
								+ deltaYSqr); // TODO: Replace with square root
												// lookup table.
						if (distance <= DISTANCE_TO_TRIGGER_GRANT_POI) {
							// System.out.println("Badge " + T.getBadgeID() +
							// " value is: " +
							// getPlayData().hasBadge(T.getBadgeID()));
							thePlayer.addBadge(T.getBadgeID());
							client.insertPacket(PacketFactory
									.buildBadgeResponseMessage(this.getID(),
											this.getPlayData().getBadges()));
							client.insertPacket(PacketFactory
									.buildPlaySoundFileMessage(0,
											"sound/music_acq_miner.snd", 1,
											(byte) 0));
							// "You have gained a new badge! (%TO)
							client.insertPacket(PacketFactory
									.buildChatSystemMessage("badge_n",
											"prose_grant", 0, "", "", "", 0,
											"", "", "", 0, "badge_n",
											Constants.BADGE_STF_STRINGS[T
													.getBadgeID()], "", 0, 0f,
											false));
							// client.insertPacket(PacketFactory.buildFlyTextSTFMessage(this,
							// "badge_n",
							// Constants.BADGE_STF_STRINGS[T.getBadgeID()], "",
							// "",0));
							client.insertPacket(PacketFactory
									.buildChatSystemMessage("badge_n",
											Constants.BADGE_STF_STRINGS[T
													.getBadgeID()], 0, null,
											null, null, 0, null, null, null, 0,
											null, null, null, 0, 0f, true));

							// this.getClient().insertPacket(PacketFactory.buildPlayClientEffectObjectMessage(this,
							// "@sound:music_acq_miner.snd"));

							/**
							 * @todo - Send a music animation of mus_acq_miner
							 *       when a badge is acquired.
							 */
						}
					}
					// System.out.println("Badge " + T.getBadgeID() +
					// " value is: " + getPlayData().hasBadge(T.getBadgeID()));
				}
			}

			if (bBurstRunning) {
				lBurstRunexpiryTimer -= lDeltaTimeMS;
				if (lBurstRunexpiryTimer <= 0) {
					playerUnBurstRun();
				}
			}
			if (lBurstRunRechargeTimer > 0) {
				lBurstRunRechargeTimer = Math.max(0, lBurstRunRechargeTimer
						- lDeltaTimeMS);
			}
			// update players intangibles.
			Vector<IntangibleObject> vI = tDatapad.getIntangibleObjects();
			for (int i = 0; i < vI.size(); i++) {
				IntangibleObject iUpd = vI.get(i);
				iUpd.update(lDeltaTimeMS);
			}

			for (int i = 0; i < vInventoryItemsList.size(); i++) {
				if (vInventoryItemsList.elementAt(i) != null) {
					vInventoryItemsList.elementAt(i).update(lDeltaTimeMS,
							server, client, this);
				}
			}

			/**
			 * TEMPORARY USE FOR DEV USE ONLY
			 */
			if (bHeightMapCollect) {
				lNextHeightMapUpdate -= lDeltaTimeMS;
				if (lNextHeightMapUpdate <= 0 && getZ() != -1000) {
					heightMapNewCoord();
					lNextHeightMapUpdate += (1000 * 2);
				}
			}
			/**
			 * This allows us to queue stuff to be spawned to the player at a
			 * later time even while logging in.
			 */
			if (this.getDelayListCount() >= 1
					&& this.getClient().getClientReadyStatus()) {
				for (int i = 0; i < lNextDelayedSpawn.size(); i++) {
					// had to do this cause 1 its not going to be done often
					// 2 it was the only way to look at the value and not update
					// it inside the vector.
					if (lNextDelayedSpawn.get(i) <= System.currentTimeMillis()) {
						// do this
						lNextDelayedSpawn.removeElementAt(i);
						SOEObject o = this.vDelayedSpawnObjects.get(i);
						if (o != null) {
							vDelayedSpawnObjects.removeElementAt(i);
							switch (o.getDelayedSpawnAction()) {
							case (byte) 0x01:// spawn
							{
								// System.out.println("Delayed Spawn Action: Object ID: "
								// + o.getID() + " IFF:" + o.getIFFFileName() +
								// " Cell ID:" + o.getCellID());
								server.addObjectToAllObjects(o, true, false);
								this.spawnItem(o);
								break;
							}
							case (byte) 0x02:// despawn
							{
								// System.out.println("Delayed DeSpawn Action: Object ID: "
								// + o.getID() + " IFF:" + o.getIFFFileName() +
								// " Cell ID:" + o.getCellID());
								this.despawnItem(o);
								server.removeObjectFromAllObjects(o, true);
								break;
							}
							case (byte) 0x03:// cycle
							{
								// server.removeObjectFromAllObjects(o, true);
								// // This removes it from the list of all
								// objects!
								this.despawnItem(o);
								o
										.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
								this.addDelayedSpawnObject(o, System
										.currentTimeMillis() + 500);
								break;
							}
							case (byte) 0x04: // DELAYED_SPAWN_ACTION_RAW_SPAWN
							{
								server.addObjectToAllObjects(o, true, false);
								if (o != null) {
									this.spawnItem(o);
								}
							}
							}
						}
					}
				}
			}

			if (bPlayerRequestedLogout
					&& this.getStance() == Constants.STANCE_SITTING) {

				if (this.lLogoutSpamTimer <= 0) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"logout", "time_left", 0l, "", "", "", 0l, "", "",
							"", 0l, "", "", "", (int) (lLogoutTimer / 1000),
							0f, false));
					lLogoutSpamTimer = 1100 * 5;
				} else {
					// System.out.println("Logout in progress");
					lLogoutSpamTimer -= lDeltaTimeMS;
					if (lLogoutSpamTimer < 0) {
						lLogoutSpamTimer = 0;
					}
				}
				// --------------------------------------

				if (lLogoutTimer <= 0) {
					// System.out.println("Logging out Player " +
					// this.getFullName());
					bPlayerRequestedLogout = false;
					if (this.myGroup != null) {
						myGroup.removeMemberFromGroup(this,
								Constants.GROUP_REMOVE_REASON_LEAVE);
					}
					server.removeObjectFromAllObjects(this.getID());

					SOEObject m = server.getObjectFromAllObjects(this
							.getCurrentMount());
					if (m != null) {
						server.removeFromTree(m);
					}
					server.removeFromTree(this);

					Vector<PlayerFriends> vPlayerFriends = thePlayer
							.getFriendsList();
					for (int i = 0; i < vPlayerFriends.size(); i++) {
						Player friend = server.getPlayer(vPlayerFriends
								.elementAt(i).getName());
						ZoneClient friendClient = friend.getClient();
						if (friendClient != null) {
							friendClient.insertPacket(PacketFactory
									.buildFriendOfflineStatusUpdate(friend,
											this));

						}
					}
					server.forwardFriendChangedStatus(this, false);
					client.insertPacket(PacketFactory.buildLogoutClient()); // This
																			// packet
																			// causes
																			// the
																			// SWG
																			// client
																			// to
																			// send
																			// a
																			// Disconnect
																			// packet.

					return;
				} else {
					lLogoutTimer -= lDeltaTimeMS;
					if (lLogoutTimer < 0) {
						lLogoutTimer = 0;
					}
				}
			} else if (bPlayerRequestedLogout
					&& this.getStance() != Constants.STANCE_SITTING) {
				bPlayerRequestedLogout = false;
				this.getClient().insertPacket(
						PacketFactory.buildChatSystemMessage("logout",
								"aborted", 0l, "", "", "", 0l, "", "", "", 0l,
								"", "", "", 0, 0f, false));
			}
			// System.out.println("Delta time" + lDeltaTimeMS);

			if (this.currentTradeObject != null) {
				currentTradeObject.update(lDeltaTimeMS);
			}

			if (this.myGroup != null && myGroup.getUpdateObject().equals(this)) {
				myGroup.update(lDeltaTimeMS);
			} else if (this.myGroup != null
					&& !myGroup.getUpdateObject().equals(this)
					&& !myGroup.getUpdateObject().getOnlineStatus()) {
				// System.out.println("Alternate Player is updating the Group Object: "
				// + this.getFullName());
				myGroup.update(lDeltaTimeMS);
			}

			if (heartbeatTimer < 0) {
				heartbeatTimer = 1000 * 60 * 5;
				getClient().insertPacket(PacketFactory.buildHeartbeat());
			} else {
				heartbeatTimer -= lDeltaTimeMS;
				if (heartbeatTimer < 0) {
					heartbeatTimer = 0;
				}
			}
			if (this.getTutorial() != null) {
				this.getTutorial().update(lDeltaTimeMS);
			}

			if (currentCampObject != null) {
				currentCampObject.update(lDeltaTimeMS, server);
			}

			if (this.isForaging) {
				this.forageCooldown -= lDeltaTimeMS;
				if (this.forageCooldown <= 0) {
					this.forage();
				}
			}

			if (playerBeingListened != null) {
				if (!ZoneServer.isInRange(this, playerBeingListened, 10)) {
					playerBeingListened.removePlayerListening(this);
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"performance", "music_listen_out_of_range",
							playerBeingListened.getID(), playerBeingListened
									.getSTFFileName(), playerBeingListened
									.getSTFFileIdentifier(),
							playerBeingListened.getFullName(), 0, null, null,
							null, 0, null, null, null, 0, 0.0f, false));
					playerBeingListened = null;
				}
				if (!playerBeingListened.isPlayingMusic()) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"performance", "music_stop_other",
							playerBeingListened.getID(), playerBeingListened
									.getSTFFileName(), playerBeingListened
									.getSTFFileIdentifier(),
							playerBeingListened.getFullName(), 0, null, null,
							null, 0, null, null, null, 0, 0.0f, false));
				}
			}

			if (playerBeingWatched != null) {
				if (ZoneServer.isInRange(this, playerBeingWatched, 10)) {
					if (lRandomWatchPlayerTick <= 0) {
						lRandomWatchPlayerTick = Math.max(5000, SWGGui
								.getRandomLong(5000, 30000));
						this.setPerformanceString("entertained");
						this.setStance(null, Constants.STANCE_ANIMATING_SKILL,
								false);
						/*
						 * int r = SWGGui.getRandomInt(1,10); String anim =
						 * "applaud"; switch(r) { case 3: { anim = "airguitar";
						 * break; } case 7: { anim = "clap"; break; } case 9: {
						 * anim = "kiss"; break; } }
						 * 
						 * Vector<Player> vPL =
						 * server.getPlayersAroundObject(this, true); for(int i
						 * = 0; i < vPL.size(); i++) { Player p = vPL.get(i);
						 * p.getClient
						 * ().insertPacket(PacketFactory.buildPlayerAnimation
						 * (this, anim)); }
						 */
					} else {
						lRandomWatchPlayerTick -= lDeltaTimeMS;
					}
				} else {
					playerBeingWatched.removePlayerWatching(this);
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"performance", "dance_watch_out_of_range",
							playerBeingWatched.getID(), playerBeingWatched
									.getSTFFileName(), playerBeingWatched
									.getSTFFileIdentifier(), playerBeingWatched
									.getFullName(), 0, null, null, null, 0,
							null, null, null, 0, 0.0f, false));
					playerBeingWatched = null;
				}
				if (playerBeingWatched != null
						&& !playerBeingWatched.isDancing()) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"performance", "dance_stop_other",
							playerBeingWatched.getID(), playerBeingWatched
									.getSTFFileName(), playerBeingWatched
									.getSTFFileIdentifier(), playerBeingWatched
									.getFullName(), 0, null, null, null, 0,
							null, null, null, 0, 0.0f, false));
					playerBeingWatched = null;
				}
			}

			if (this.isPlayingMusic()) {
				if (lMusicTick <= 0) {
					lMusicTick = 9000;
					Structure s = null;
					if (this.getCellID() != 0) {
						Cell c = (Cell) server.getObjectFromAllObjects(this
								.getCellID());
						if (c != null) {
							s = (Structure) c.getBuilding();
						}
					}
					int iXPModifier = 0;
					int iSkillModifier = 0;
					int iHealingWoundMod = 0;
					if (this.hasSkill(11)) {
						iXPModifier += 5;
						iHealingWoundMod += 5;
						iSkillModifier++;
					}
					if (this.hasSkill(12)) {
						iXPModifier += 10;
						iHealingWoundMod += 10;
						iSkillModifier++;
					}
					if (this.hasSkill(17)) {
						iXPModifier += 5;
						iSkillModifier++;
					}
					if (this.hasSkill(18)) {
						iXPModifier += 5;
						iSkillModifier++;
					}
					if (this.hasSkill(19)) {
						iXPModifier += 5;
						iSkillModifier++;
					}
					if (this.hasSkill(20)) {
						iXPModifier += 10;
						iSkillModifier++;
					}
					if (this.hasSkill(25)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(26)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(27)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(28)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(281)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(287)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(288)) {
						iHealingWoundMod += 10;
					}
					if (this.hasSkill(289)) {
						iHealingWoundMod += 10;
					}
					if (this.hasSkill(290)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(282)) {
						iHealingWoundMod += 15;
					}
					int iListenBonus = 0;
					int iHealedWoundAmount = 0;
					if (vPlayersListening != null) {
						iListenBonus = vPlayersListening.size();
						for (int h = 0; h < vPlayersListening.size(); h++) {
							Player w = vPlayersListening.get(h);
							int[] iWounds = w.getHamWounds();
							// 6,7,8
							for (int e = 6; e < iWounds.length; e++) {
								if (iWounds[e] >= 1) {
									if (this.isInCamp()
											|| s != null
											&& s.getIFFFileName().contains(
													"cantina")) {
										if (iWounds[e] != 0) {
											w.updateHAMWounds(e,
													-iHealingWoundMod, true);
										}
										iHealedWoundAmount += iHealingWoundMod;
									}
								}
							}
						}
					}
					if (this.getGroupID() != 0) {
						Group g = (Group) server.getObjectFromAllObjects(this
								.getGroupID());
						int iGroupModifier = 0;
						Vector<SOEObject> vGM = g.getGroupMembers();
						for (int i = 0; i < vGM.size(); i++) {
							SOEObject o = vGM.get(i);
							if (o instanceof Player && !o.equals(this)) {
								Player p = (Player) o;

								if (p.hasSkill(11)) {
									iXPModifier += 5;
									iSkillModifier++;
								}
								if (p.hasSkill(12)) {
									iXPModifier += 10;
									iSkillModifier++;
								}
								if (p.hasSkill(21)) {
									iXPModifier += 5;
									iSkillModifier++;
								}
								if (p.hasSkill(22)) {
									iXPModifier += 5;
									iSkillModifier++;
								}
								if (p.hasSkill(23)) {
									iXPModifier += 5;
									iSkillModifier++;
								}
								if (p.hasSkill(24)) {
									iXPModifier += 10;
									iSkillModifier++;
								}
								iGroupModifier++;
							}
						}
						iXPModifier /= iGroupModifier;
						iSkillModifier /= iGroupModifier;
						this.updateExperience(null, DatabaseInterface
								.getExperienceIDFromName("music"),
								((iXPModifier * iSkillModifier)
										+ iFlourishBonus + iListenBonus));
						iFlourishBonus = 0;
					} else {
						this.updateExperience(null, DatabaseInterface
								.getExperienceIDFromName("music"),
								((iXPModifier * iSkillModifier)
										+ iFlourishBonus + iListenBonus));
						iFlourishBonus = 0;

					}
					if (iListenBonus >= 1) {
						if (iHealedWoundAmount >= 1) {
							this
									.updateExperience(
											null,
											DatabaseInterface
													.getExperienceIDFromName("entertainer_healing"),
											((iHealedWoundAmount * iSkillModifier) + iListenBonus));
						}
					}
				} else {
					lMusicTick -= lDeltaTimeMS;
				}
			}

			if (this.isDancing()) {
				if (lDancetick <= 0) {
					lDancetick = 9000;
					Structure s = null;
					if (this.getCellID() != 0) {
						Cell c = (Cell) server.getObjectFromAllObjects(this
								.getCellID());
						if (c != null) {
							s = c.getBuilding();
						}
					}
					int iXPModifier = 0;
					int iSkillModifier = 0;
					int iHealingWoundMod = 0;
					if (this.hasSkill(11)) {
						iXPModifier += 5;
						iHealingWoundMod += 5;
						iSkillModifier++;
					}
					if (this.hasSkill(12)) {
						iXPModifier += 10;
						iHealingWoundMod += 10;
						iSkillModifier++;
					}
					if (this.hasSkill(21)) {
						iXPModifier += 5;
						iSkillModifier++;
					}
					if (this.hasSkill(22)) {
						iXPModifier += 5;
						iSkillModifier++;
					}
					if (this.hasSkill(23)) {
						iXPModifier += 5;
						iSkillModifier++;
					}
					if (this.hasSkill(24)) {
						iXPModifier += 10;
						iSkillModifier++;
					}
					if (this.hasSkill(25)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(26)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(27)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(28)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(262)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(263)) {
						iHealingWoundMod += 15;
					}
					if (this.hasSkill(268)) {
						iHealingWoundMod += 5;
					}
					if (this.hasSkill(269)) {
						iHealingWoundMod += 10;
					}
					if (this.hasSkill(270)) {
						iHealingWoundMod += 10;
					}
					if (this.hasSkill(271)) {
						iHealingWoundMod += 15;
					}
					int iWatchBonus = 0;
					int iHealedWoundAmount = 0;
					if (vPlayersWatching != null) {
						iWatchBonus = vPlayersWatching.size();
						for (int h = 0; h < vPlayersWatching.size(); h++) {
							Player w = vPlayersWatching.get(h);
							int[] iWounds = w.getHamWounds();
							// 6,7,8
							for (int e = 6; e < iWounds.length; e++) {
								if (iWounds[e] >= 1) {
									if (this.isInCamp()
											|| s != null
											&& s.getIFFFileName().contains(
													"cantina")) {
										if (iWounds[e] != 0) {
											w.updateHAMWounds(e,
													-iHealingWoundMod, true);
										}
										iHealedWoundAmount += iHealingWoundMod;
									}
								}
							}
						}
					}
					if (this.getGroupID() != 0) {
						Group g = (Group) server.getObjectFromAllObjects(this
								.getGroupID());
						int iGroupModifier = 0;
						Vector<SOEObject> vGM = g.getGroupMembers();
						for (int i = 0; i < vGM.size(); i++) {
							SOEObject o = vGM.get(i);
							if (o instanceof Player && !o.equals(this)) {
								Player p = (Player) o;

								if (p.hasSkill(11)) {
									iXPModifier += 5;
									iSkillModifier++;
								}
								if (p.hasSkill(12)) {
									iXPModifier += 10;
									iSkillModifier++;
								}
								if (p.hasSkill(21)) {
									iXPModifier += 5;
									iSkillModifier++;
								}
								if (p.hasSkill(22)) {
									iXPModifier += 5;
									iSkillModifier++;
								}
								if (p.hasSkill(23)) {
									iXPModifier += 5;
									iSkillModifier++;
								}
								if (p.hasSkill(24)) {
									iXPModifier += 10;
									iSkillModifier++;
								}
								iGroupModifier++;
							}
						}
						iXPModifier /= iGroupModifier;
						iSkillModifier /= iGroupModifier;
						this.updateExperience(null, DatabaseInterface
								.getExperienceIDFromName("dance"),
								((iXPModifier * iSkillModifier)
										+ iFlourishBonus + iWatchBonus));
						iFlourishBonus = 0;
					} else {
						this.updateExperience(null, DatabaseInterface
								.getExperienceIDFromName("dance"),
								((iXPModifier * iSkillModifier)
										+ iFlourishBonus + iWatchBonus));
						iFlourishBonus = 0;

					}
					if (iWatchBonus >= 1) {
						if (iHealedWoundAmount >= 1) {
							this
									.updateExperience(
											null,
											DatabaseInterface
													.getExperienceIDFromName("entertainer_healing"),
											((iHealedWoundAmount * iSkillModifier) + iWatchBonus));
						}
					}
				} else {
					lDancetick -= lDeltaTimeMS;
				}
			}

			lFlourishTick -= lDeltaTimeMS;
			lEffectTick -= lDeltaTimeMS;

			if (iStance == Constants.STANCE_DEAD) {
				if (bSeenCloneWindow == false) {
					SUIWindow cloneWindow = new SUIWindow(this);
					cloneWindow
							.setWindowType(Constants.SUI_SELECT_LOCATION_TO_CLONE);
					cloneWindow.setOriginatingObject(this);
					String WindowTypeString = "handlePlayerRevive";
					String DataListTitle = "@base_player:revive_title";
					String DataListPrompt = "@base_player:clone_prompt_header";
					Vector<String> vCloneNames = new Vector<String>();
					Vector<MapLocationData> vCloneData = new Vector<MapLocationData>();
					// TODO: Now, check to see if the Player has a specific
					// Player City cloning center they are bound to, on this
					// planet. For that, players must be able to bind to a
					// specific player city cloner.
					MapLocationData playerCloneBindLocation = thePlayer
							.getCloneBindLocation();
					// If it's on this planet, let him go there.
					if (playerCloneBindLocation != null) {
						vCloneNames.add(playerCloneBindLocation.getName());
						vCloneData.add(playerCloneBindLocation);
					}

					// Now, go through all static cloners and check them as
					// well.
					Vector<MapLocationData> vStaticLocationsThisPlanet = server
							.getStaticMapLocations(Constants.PlanetNames[getPlanetID()]);
					for (int i = 0; i < vStaticLocationsThisPlanet.size(); i++) {
						MapLocationData staticPoint = vStaticLocationsThisPlanet
								.elementAt(i);
						if (staticPoint.getObjectType() == Constants.MAP_LOCATION_ID.CLONING_FACILITY
								.ordinal()
								|| staticPoint.getObjectSubType() == Constants.MAP_LOCATION_ID.CLONING_FACILITY
										.ordinal()) {
							vCloneNames.add(staticPoint.getName());
							vCloneData.add(staticPoint);
						}
					}
					System.out.println("Found " + vCloneNames.size()
							+ " cloning centers on "
							+ Constants.PlanetNames[getPlanetID()]);
					String[] vCloneNameArr = new String[vCloneNames.size()];
					vCloneNames.toArray(vCloneNameArr); // (String[])vCloneNames.toArray();
					client.insertPacket(cloneWindow.SUIScriptListBox(client,
							WindowTypeString, DataListTitle, DataListPrompt,
							vCloneNameArr, null, 0, getID()));
					vLastSeenCloneLocations = vCloneData;
					bSeenCloneWindow = true;
					// Constants.MAP_LOCATION_ID.CLONING_FACILITY;

					/*
					 * String sList[] = new String[3]; sList[0] =
					 * "@player_structure:owner_prompt " +
					 * this.getCampOwner().getFullName(); //owner sList[1] =
					 * "Time Left: " + (this.lTimeToLive / 1000 / 60) + " Mins";
					 * sList[2] = "Visitors: " + this.vCampVisitors.size();
					 * client.insertPacket(w.SUIScriptListBox(client,
					 * WindowTypeString, DataListTitle, DataListPrompt, sList,
					 * null, 0, 0));
					 * client.getPlayer().addPendingSUIWindow(w.copyWindow());
					 */
				}
			}
		} catch (Exception e) {
			DataLog.logEntry("Exception caught while checking POI Badges "
					+ e.toString(), "Player", Constants.LOG_SEVERITY_CRITICAL,
					ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
			// System.out.println("Exception caught while checking POI Badges "
			// + e);
			e.printStackTrace();
		}
	}

	protected boolean addFlourishBonus(int iBonus) {
		try {
			if (lFlourishTick <= 0) {
				lFlourishTick = 1000;
				iFlourishBonus += iBonus;
				int iXPModifier = 0;
				int iSkillModifier = 0;

				if (this.hasSkill(11)) {
					iXPModifier += 5;
					iSkillModifier++;
				}
				if (this.hasSkill(12)) {
					iXPModifier += 10;
					iSkillModifier++;
				}
				if (this.hasSkill(21)) {
					iXPModifier += 5;
					iSkillModifier++;
				}
				if (this.hasSkill(22)) {
					iXPModifier += 5;
					iSkillModifier++;
				}
				if (this.hasSkill(23)) {
					iXPModifier += 5;
					iSkillModifier++;
				}
				if (this.hasSkill(24)) {
					iXPModifier += 10;
					iSkillModifier++;
				}
				this.updateCurrentHam(Constants.HAM_INDEX_ACTION, (Math.min(-20, (-75 + iXPModifier))));
				this.getClient().insertPacket(
						PacketFactory.buildChatSystemMessage("performance",
								"flourish_perform", this.getID(), this
										.getSTFFileName(), this
										.getSTFFileIdentifier(), this
										.getFullName(), 0, null, null, null, 0,
								null, null, null, 0, 0.0f, false));
				return true;
			} else {
				this.getClient().insertPacket(
						PacketFactory.buildChatSystemMessage("performance",
								"flourish_wait_self", this.getID(), this
										.getSTFFileName(), this
										.getSTFFileIdentifier(), this
										.getFullName(), 0, null, null, null, 0,
								null, null, null, 0, 0.0f, false));
			}
			return false;
		} catch (Exception e) {
			DataLog.logException("Exception while granting flourish bonus",
					"Player", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
		return false;
	}

	public boolean isBHeightMapCollect() {
		return bHeightMapCollect;
	}

	public void setBHeightMapCollect(boolean bHeightMapCollect) {
		this.bHeightMapCollect = bHeightMapCollect;
	}

	public long getLNextHeightMapUpdate() {
		return lNextHeightMapUpdate;
	}

	public void setLNextHeightMapUpdate(long lNextHeightMapUpdate) {
		this.lNextHeightMapUpdate = lNextHeightMapUpdate;
	}

	float currentX = 0;
	public short tempx, tempy, tempplanetid, tempnobuildings;
	public transient short[][][] myHeightMap;
	public transient boolean bHeightMapActive = false;

	private void heightMapNewCoord() {
		try {
			bHeightMapCollect = false;

			short tempz = (short) getZ();
			if (tempx <= 254 && tempy <= 254
					&& myHeightMap[tempplanetid][tempx][tempy] == 32767) {
				this.getClient().insertPacket(
						PacketFactory.buildChatSystemMessage("Z For X:"
								+ (float) (7680 - (tempx * 60.472441)) + " Y: "
								+ (float) (7680 - (tempy * 60.472441))
								+ " Set at: " + (short) getZ()));
				System.out.println(this.getFullName() + " has set Z For X:"
						+ (float) (7680 - (tempx * 60.472441)) + " Y: "
						+ (float) (7680 - (tempy * 60.472441)) + " Set at: "
						+ (short) getZ());
				myHeightMap[tempplanetid][tempx][tempy] = tempz;
				server.updateHeightMapZ(tempx, tempy, tempz, tempplanetid);
				tempy++;
				if (tempy == 255) {
					tempy = 0;
					if (tempy == 255) {
						tempy = 0;
						if (tempx < 255) {
							tempx++;
							if (tempx == 255) {
								tempx = 0;
								tempplanetid++;
								if (tempplanetid == 10) {
									bHeightMapCollect = false;
									return;
								}
							}
						}
					}
				}
			}
			if (myHeightMap[tempplanetid][tempx][tempy] != 32767
					&& tempy != 255) {
				System.out.println("Moving Coords forward they were not 32767");
				while (myHeightMap[tempplanetid][tempx][tempy] != 32767
						&& tempy != 255) {
					tempy++;
					if (tempy == 255) {
						tempy = 0;
						if (tempx < 255) {
							tempx++;
							if (tempx == 255) {
								tempx = 0;
								tempplanetid++;
								if (tempplanetid == 10) {
									bHeightMapCollect = false;
									return;
								}
							}
						}
					}
				}
			}
			if (myHeightMap[tempplanetid][tempx][tempy] == 32767) {
				this.playerWarp((float) (7680 - (tempx * 60.472441)),
						(float) (7680 - (tempy * 60.472441)), (float) -1000f,
						getCellID(), tempplanetid);
			}
			bHeightMapCollect = true;
		} catch (Exception e) {

			System.out.println("Height Map Tool Exception Caught " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the countdown timer for when this character will next be
	 * updated in the Database.
	 */
	protected void initializeStartupTime() {
		lDatabaseUpdateTimeMS = lUpdateIntervalMS;
	}

	/**
	 * This function adds a Command to the Command Queue.
	 * 
	 * @param commandCRC
	 *            -- The CRC of the command.
	 * @param timer
	 *            -- The period of time this command causes any following
	 *            commands to be delayed by.
	 * @param unknown1
	 *            -- An unknown variable.
	 * @param unknown2
	 *            -- An unknown variable.
	 */
	protected void queueCommand(CommandQueueItem queueItem) {
		// vCommandQueue.add(new CommandQueueItem(commandCRC, timer, unknown1,
		// unknown2, server.getRequiredCommandSkill(commandCRC), this));
		vCommandQueue.add(queueItem);
	}

	/**
	 * Sets if the Player knows a given skill.
	 * 
	 * @param skillID
	 *            -- The ID of the Skill we are modifying.
	 * @param bGainingSkill
	 *            -- If the Player knows the skill or not.
	 */
	protected void setSkill(int skillID, boolean bGainingSkill,
			boolean bUpdateZone) {
		if (!bIsJedi) {
			thePlayer.setSkill(skillID, bGainingSkill, bUpdateZone); // How can
																		// this
																		// possibly
																		// be a
																		// null
																		// pointer?
		} else {
			// Skill must be a Force Sensitive skill, Language, or Pilot skill
			// to learn. No other skills are learnable.
			// ******* IMPORTANT *******
			// This code is for Hologrind.
			// TODO: Update for compatibility with the Village system.
			if ((skillID >= 641 && skillID <= 989) || skillID >= 1010) {
				thePlayer.setSkill(skillID, bGainingSkill, bUpdateZone);
			} else {
				if (bUpdateZone) {
					try {
						client
								.insertPacket(PacketFactory
										.buildChatSystemMessage("As a Jedi, you are not allowed to learn this skill."));
					} catch (Exception e) {
						// D'oh!
					}
				}
			}
		}
	}

	/**
	 * Checks to see if the Player knows a given skill.
	 * 
	 * @param skillID
	 *            -- The Skill ID
	 * @return If the player knows the skill.
	 */
	protected boolean hasSkill(int skillID) {
		return thePlayer.hasSkill(skillID);
	}

	/**
	 * Initializes this Player's position at server start-up.
	 * 
	 * @param moveCounter
	 *            -- The number of movements the Player has made. (Should always
	 *            be 0 for this function.)
	 * @param newN
	 *            -- The Player's North orientation.
	 * @param newS
	 *            -- The Player's South Orientation.
	 * @param newE
	 *            -- The Player's X-axis rotation.
	 * @param newW
	 *            -- The Player's Y-axis rotation.
	 * @param newX
	 *            -- The Player's X position.
	 * @param newZ
	 *            -- The Player's Z position.
	 * @param newY
	 *            -- The Player's Y position.
	 * @param fVelocity
	 *            -- The Player's velocity.
	 */
	protected void initializePosition(int moveCounter, float newN, float newS,
			float newE, float newW, float newX, float newZ, float newY,
			float fVelocity, long cellID) {
		setCellID(cellID);
		setX(newX);
		setY(newY);
		setZ(newZ);
		setOrientationN(newN);
		setOrientationE(newE);
		setOrientationS(newS);
		setOrientationW(newW);
		setVelocity(fVelocity);
		Vector<IntangibleObject> vDatapadItems = tDatapad.getIntangibleObjects();
		for (int i = 0; i < vDatapadItems.size(); i++) {
			IntangibleObject o = vDatapadItems.elementAt(i);
			NPC theCalledObject = o.getAssociatedCreature();
			if (theCalledObject != null) {
				o.setRadialCondition(Constants.RADIAL_CONDITION.INTANGIBLE_VEHICLE_SPAWNED.ordinal());
			} else {
				o.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
			}
			
		}
		for (int i = 0; i < vInventoryItemsList.size(); i++) {
			TangibleItem inventoryItem = vInventoryItemsList.elementAt(i);
			inventoryItem.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
		}
	}

	private float discardedX = 0;
	private float discardedY = 0;
	private float discardedZ = 0;

	// private int iMovementCounter = 0;

	// private int getMovementCounter() {
	// return iMovementCounter;
	// }
	/**
	 * This function updates the positon of the Player in the world, and sends
	 * the update packets to all other Players in range of this Player.
	 * 
	 * @param moveCounter
	 *            -- The number of times the Player's position has been updated.
	 * @param newN
	 *            -- The Player's North orientation
	 * @param newS
	 *            -- The Player's South orientation
	 * @param newE
	 *            -- The Player's X axis rotation.
	 * @param newW
	 *            -- The Player's Y axis rotation.
	 * @param newX
	 *            -- The Player's X coordinate.
	 * @param newZ
	 *            -- The Player's Z coordinate.
	 * @param newY
	 *            -- The Player's Y coordinate.
	 * @param fVelocity
	 *            -- The Player's Velocity
	 * @throws IOException
	 *             -- If an error occured building the movement update packets.
	 */
	protected void updatePosition(int moveCounter, float newN, float newS,
			float newE, float newW, float newX, float newZ, float newY,
			float fVelocity, long cellID) throws IOException {
		float dX;
		float dY;
		float dZ;
		dX = Math.abs(newX - discardedX);
		dY = Math.abs(newY - discardedY);
		dZ = Math.abs(newZ - discardedZ);
		// iMovementCounter = moveCounter;

		double displacement = Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
		discardedX = newX;
		discardedY = newY;
		discardedZ = newZ;
		if (this.getCellID() != 0 || displacement > 50.0) {
			if (getCellID() != 0) {
				Cell c = (Cell) server.getObjectFromAllObjects(this.getCellID());
				Structure s = c.getBuilding();
				s.despawnObjectsInBuildingCells(client);
				c.removeCellObject(this);
			}
			this.setCellID(0);
			// Get every object on the Player, set them to an appropriate value.
			Vector<IntangibleObject> vDatapadItems = tDatapad.getIntangibleObjects();
			for (int i = 0; i < vDatapadItems.size(); i++) {
				IntangibleObject o = vDatapadItems.elementAt(i);
				NPC theCalledObject = o.getAssociatedCreature();
				if (theCalledObject != null) {
					o.setRadialCondition(Constants.RADIAL_CONDITION.INTANGIBLE_VEHICLE_SPAWNED.ordinal());
				} else {
					o.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
				}
				
			}
			for (int i = 0; i < vInventoryItemsList.size(); i++) {
				TangibleItem inventoryItem = vInventoryItemsList.elementAt(i);
				inventoryItem.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
			}
			return;
		}

		if (getX() == newX && getY() == newY && getZ() == newZ) {
			return;
		}
		float MovAngle = 0;
		if (((newN * newN) + (newS * newS) + (newE * newE)) > 0.0) {
			if (newW > 0.0 && newE < 0.0) {
				newW *= -1;
			}
			MovAngle = (float) (2.0 * Math.acos(newW));
		} else {
			MovAngle = 0.0f;
		}

		// These 3 vectors need to be reworked. They are already taken care of
		// by the positional grid.
		Vector<SOEObject> vObjectsBeforeMovement = server
				.getWorldObjectsAroundObject(this);

		setCellID(cellID);
		setZ(newZ);
		setOrientationN(newN);
		setOrientationE(newE);
		setOrientationS(newS);
		setOrientationW(newW);
		setVelocity(fVelocity);
		setMovementAngle(MovAngle);

		if (bIsMounted) {
			Vehicle mountObject = (Vehicle) server
					.getObjectFromAllObjects(lCurrentMount);
			if (mountObject.getTemplateID() == 6213) {
				mountObject.setZ(newZ + Constants.JETPACK_Z_AXIS_MODIFIER);
				setZ(newZ + Constants.JETPACK_Z_AXIS_MODIFIER);
			} else {
				mountObject.setZ(newZ);
				setZ(newZ);
			}
			mountObject.setOrientationE(newE);
			mountObject.setOrientationN(newN);
			mountObject.setOrientationS(newS);
			mountObject.setOrientationW(newW);
			mountObject.setVelocity(fVelocity);
			mountObject.setMovementAngle(MovAngle);
			server.moveObjectInTree(mountObject, newX, newY, mountObject
					.getPlanetID(), mountObject.getPlanetID());
			server.moveObjectInTree(this, newX, newY, getPlanetID(),
					getPlanetID());
			getClient().insertPacket(
					PacketFactory.buildUpdateTransformMessage(mountObject),
					Constants.PACKET_RANGE_CHAT_RANGE);
			getClient().insertPacket(
					PacketFactory.buildUpdateTransformMessage(this),
					Constants.PACKET_RANGE_CHAT_RANGE);
		} else {
			server.moveObjectInTree(this, newX, newY, getPlanetID(),
					getPlanetID());
			getClient().insertPacket(
					PacketFactory.buildUpdateTransformMessage(this),
					Constants.PACKET_RANGE_CHAT_RANGE);
		}

		if (this.getPetsFollowingObject().size() >= 1) {
			for (int i = 0; i < getPetsFollowingObject().size(); i++) {
				CreaturePet pet = getPetsFollowingObject().get(i);
				pet.setX(newX + pet.getFollowXOffset());
				pet.setY(newY + pet.getFollowYOffset());
				pet.setZ(newZ);
				pet.setOrientationN(newN);
				pet.setOrientationS(newS);
				pet.setOrientationE(newE);
				pet.setOrientationW(newW);
				pet.setVelocity(fVelocity);
				pet.setMovementAngle(MovAngle);
				server.moveObjectInTree(pet, newX, newY, pet.getPlanetID(), pet
						.getPlanetID());
				getClient().insertPacket(
						PacketFactory.buildUpdateTransformMessage(pet),
						Constants.PACKET_RANGE_CHAT_RANGE);
			}
		}

		if (this.bIsTraveling) {
			return;
		}

		Vector<SOEObject> vObjectsAfterMovement = server
				.getWorldObjectsAroundObject(this);
		Vector<SOEObject> vStillSpawned = new Vector<SOEObject>();
		for (int i = 0; i < vObjectsBeforeMovement.size(); i++) {
			SOEObject p = vObjectsBeforeMovement.elementAt(i);
			if (!vObjectsAfterMovement.contains(p)) {
				// System.out.println("Player " + p.getFullName() +
				// " no longer seen by " + getFullName());
				if (p instanceof Player && !(p instanceof NPC)
						&& !(p instanceof Terminal)) {
					Player play = (Player) p;
					play.despawnItem(this);
				}
				if (p.getID() == this.getCurrentMount() && bIsMounted) {
					// System.out.println("Attempt to Despawn mount while mounted!!!!");
				} else {
					despawnItem(p);
				}
			}
		}
		for (int i = 0; i < vObjectsAfterMovement.size(); i++) {
			SOEObject p = vObjectsAfterMovement.elementAt(i);
			if (!vObjectsBeforeMovement.contains(p)) {
				if (p instanceof Player && !(p instanceof NPC)
						&& !(p instanceof Terminal)) {
					Player play = (Player) p;
					play.spawnItem(this);
				}

				if (p.getID() == this.getCurrentMount() && bIsMounted) {
					// its our mount dont do nothin!
					// System.out.println("Attempt to spawn our mount while mounted!!!");
				} else {
					spawnItem(p);

					/*
					 * long pCellID = p.getCellID();
					 * 
					 * if (pCellID == 0) {client.insertPacket(PacketFactory.
					 * buildNPCUpdateTransformMessage(p)); } else {
					 * client.insertPacket
					 * (PacketFactory.buildNPCUpdateCellTransformMessage(p,
					 * server.getObjectFromAllObjects(pCellID))); }
					 */
				}

			} else {
				vStillSpawned.add(p);
			}
		}
		Cell c = null;

		// System.out.println("Objects still spawned: " + vStillSpawned.size());
		/*
		 * for (int i = 0; i < vStillSpawned.size(); i++) { SOEObject o =
		 * vStillSpawned.elementAt(i); if (o instanceof Player && !(o instanceof
		 * NPC) && !(o instanceof Terminal)) { Player p = (Player) o; if
		 * (o.getID() != getID()) { //if(!this.isDancing ||
		 * !this.isPlayingMusic) { if (this.getCellID() != 0) {
		 * p.getClient().insertPacket
		 * (PacketFactory.buildUpdateContainmentMessage(this, c, -1)); }
		 * p.getClient
		 * ().insertPacket(PacketFactory.buildUpdateTransformMessage(this)); } }
		 * } }
		 */

		if (!this.isDancing && !this.isPlayingMusic) {
			if (this.getCellID() != 0) {
				getClient().insertPacket(
						PacketFactory
								.buildUpdateContainmentMessage(this, c, -1),
						Constants.PACKET_RANGE_CHAT_RANGE);
			}
			getClient().insertPacket(
					PacketFactory.buildUpdateTransformMessage(this),
					Constants.PACKET_RANGE_CHAT_RANGE);
		}

	}

	protected void initializeInteriorPosition(int moveCounter, float newN,
			float newS, float newE, float newW, float newX, float newZ,
			float newY, float fVelocity, long cellID) {
		setCellID(cellID);
		setCellX(newX);
		setCellY(newY);
		setCellZ(newZ);
		setOrientationN(newN);
		setOrientationE(newE);
		setOrientationS(newS);
		setOrientationW(newW);
		setVelocity(fVelocity);
		Vector<IntangibleObject> vDatapadItems = tDatapad.getIntangibleObjects();
		for (int i = 0; i < vDatapadItems.size(); i++) {
			IntangibleObject o = vDatapadItems.elementAt(i);
			NPC theCalledObject = o.getAssociatedCreature();
			if (theCalledObject != null) {
				o.setRadialCondition(Constants.RADIAL_CONDITION.INTANGIBLE_VEHICLE_SPAWNED.ordinal());
			} else {
				o.setRadialCondition(Constants.RADIAL_CONDITION.TANGIBLE_ITEM_INDOORS.ordinal());
			}
			
		}
		for (int i = 0; i < vInventoryItemsList.size(); i++) {
			TangibleItem inventoryItem = vInventoryItemsList.elementAt(i);
			inventoryItem.setRadialCondition(Constants.RADIAL_CONDITION.TANGIBLE_ITEM_INDOORS.ordinal());
		}
	}

	/**
	 * This function updates the position of the Player when the Player is
	 * inside of a Structure / Cell object.
	 * 
	 * @param moveCounter
	 *            -- The number of times the Player's position has been updated.
	 * @param newN
	 *            -- The Player's North orientation.
	 * @param newS
	 *            -- The Player's South orientation.
	 * @param newE
	 *            -- The Player's X axis rotation.
	 * @param newW
	 *            -- The Player's Y axis rotation.
	 * @param newX
	 *            -- The Player's X coordinate.
	 * @param newZ
	 *            -- The Player's Z coordinate.
	 * @param newY
	 *            -- The Player's Y coordinate.
	 * @param fVelocity
	 *            -- The Player's velocity.
	 * @param cellID
	 *            -- The Cell the Player is currently occupying.
	 * @throws IOException
	 *             If an error occured building the position update packet.
	 */
	protected void updateInteriorPosition(int moveCounter, float newN,
			float newS, float newE, float newW, float newX, float newZ,
			float newY, float fVelocity, long cellID) throws IOException {
		// float dX;
		// float dY;
		// float dZ;
		// dX = Math.abs(newX - discardedX);
		// dY = Math.abs(newY - discardedY);
		// dZ = Math.abs(newZ - discardedZ);

		// double displacement = Math.sqrt(
		// (dX * dX) +
		// (dY * dY) +
		// (dZ * dZ));
		discardedX = newX;
		discardedY = newY;
		discardedZ = newZ;
		// this was causing invalid cell id to be saved.
		/*
		 * if (lCellID == 0 || displacement > 50) { if (lCellID == 0) { lCellID
		 * = (cellID / 2) + 1; } return; }
		 */
		long oldCellID = getCellID();
		
		Cell c = (Cell) server.getObjectFromAllObjects(cellID);

		Structure s = c.getBuilding();

		float MovAngle = 0;
		if (((newN * newN) + (newS * newS) + (newE * newE)) > 0.0) {
			if (newW > 0.0 && newE < 0.0) {
				newW *= -1;
			}
			MovAngle = (float) (2.0 * Math.acos(newW));
		} else {
			MovAngle = 0.0f;
		}

		// server.getWorldObjectsAroundObject(this);
		Vector<SOEObject> vObjectsBeforeMovement = server
				.getWorldObjectsAroundObject(this);
		c.addCellObject(this);
		if (oldCellID == 0) {
			Vector<IntangibleObject> vDatapadItems = tDatapad.getIntangibleObjects();
			for (int i = 0; i < vDatapadItems.size(); i++) {
				IntangibleObject o = vDatapadItems.elementAt(i);
				NPC theCalledObject = o.getAssociatedCreature();
				if (theCalledObject != null) {
					o.setRadialCondition(Constants.RADIAL_CONDITION.INTANGIBLE_VEHICLE_SPAWNED.ordinal());
				} else {
					o.setRadialCondition(Constants.RADIAL_CONDITION.TANGIBLE_ITEM_INDOORS.ordinal());
				}
				
			}
			for (int i = 0; i < vInventoryItemsList.size(); i++) {
				TangibleItem inventoryItem = vInventoryItemsList.elementAt(i);
				inventoryItem.setRadialCondition(Constants.RADIAL_CONDITION.TANGIBLE_ITEM_INDOORS.ordinal());
			}
			
			s.spawnObjectsInBuildingCells(client);
		} else if (oldCellID != cellID) {
			Cell oldCell = (Cell)server.getObjectFromAllObjects(oldCellID);
			oldCell.removeCellObject(this);
			
		}
		synchronized(this) {
			setCellID(cellID);
		}
		setCellX(newX);
		setCellY(newY);
		setCellZ(newZ);

		// ------------------------------------
		// The way we add or remove Cell X from or to x and Cell y from or to y
		// depends on building Direction.
		if (s != null && s.getIsStaticObject()) {
			setX(s.getX() + newX);
			setY(s.getY() + newY);
			setZ(s.getZ() + newZ);
		} else if (s != null) {
			switch (s.getIFacingDirection()) {
			case 0: {
				setX(s.getX() + newX);
				setY(s.getY() + newY);
				setZ(s.getZ() + newZ);
				break;
			}
			case 1: {
				setX(s.getX() + newY);
				setY(s.getY() - newX);
				setZ(s.getZ() + newZ);
				break;
			}
			case 2: {
				setX(s.getX() - newX);
				setY(s.getY() - newY);
				setZ(s.getZ() + newZ);
				break;
			}
			case 3: {
				setX(s.getX() - newY);
				setY(s.getY() + newX);
				setZ(s.getZ() + newZ);
				break;
			}
			}
		}

		// -----------------------------

		setOrientationN(newN);
		setOrientationE(newE);
		setOrientationS(newS);
		setOrientationW(newW);
		setVelocity(fVelocity);
		setMovementAngle(MovAngle);

		if (this.bIsTraveling) {
			return;
		}

		Vector<SOEObject> vObjectsAfterMovement = server
				.getWorldObjectsAroundObject(this);
		Vector<SOEObject> vStillSpawned = new Vector<SOEObject>();

		for (int i = 0; i < vObjectsBeforeMovement.size(); i++) {
			SOEObject p = vObjectsBeforeMovement.elementAt(i);
			if (!vObjectsAfterMovement.contains(p)) {
				// System.out.println("Player " + p.getFullName() +
				// " no longer seen by " + getFullName());
				if (p instanceof Player && !(p instanceof NPC)
						&& !(p instanceof Terminal)) {
					Player play = (Player) p;
					play.despawnItem(this);
				}
				despawnItem(p);
			}
		}
		for (int i = 0; i < vObjectsAfterMovement.size(); i++) {
			SOEObject p = vObjectsAfterMovement.elementAt(i);
			if (!vObjectsBeforeMovement.contains(p)) {
				if (p instanceof Player && !(p instanceof NPC)
						&& !(p instanceof Terminal)) {
					Player play = (Player) p;
					play.spawnItem(this);
				}
				spawnItem(p);
				/*
				 * if (p instanceof Shuttle) { Shuttle shuttle = (Shuttle)p; if
				 * (!shuttle.getIsSpawned()) { shuttle.flyOut(client); } }
				 */

			} else {
				vStillSpawned.add(p);
			}
		}
		// System.out.println("Objects still spawned: " + vStillSpawned.size());
		/*
		 * for (int i = 0; i < vStillSpawned.size(); i++) { SOEObject o =
		 * vStillSpawned.elementAt(i); if (o instanceof Player && !(o instanceof
		 * NPC) && !(o instanceof Terminal)) { Player p = (Player) o; if
		 * (o.getID() != getID()) { //if(!this.isDancing &&
		 * !this.isPlayingMusic) { if (this.getCellID() != cellID) {
		 * //p.getClient
		 * ().insertPacket(PacketFactory.buildUpdateContainmentMessage(this, //
		 * server.getObjectFromAllObjects(lCellID), // false));
		 * p.getClient().insertPacket
		 * (PacketFactory.buildUpdateContainmentMessage(this, c, -1)); }
		 * p.getClient
		 * ().insertPacket(PacketFactory.buildUpdateCellTransformMessage(this,
		 * c)); } } } }
		 */
		if (!this.isDancing && !this.isPlayingMusic) {
			if (this.getCellID() != cellID) {
				getClient().insertPacket(
						PacketFactory
								.buildUpdateContainmentMessage(this, c, -1),
						Constants.PACKET_RANGE_CHAT_RANGE);
			}
			getClient().insertPacket(
					PacketFactory.buildUpdateCellTransformMessage(this, c),
					Constants.PACKET_RANGE_CHAT_RANGE);
		}

		if ((this.getCellID() != 0)) {
			if (this.getCellID() != cellID) {
				Cell oldCell = (Cell) server.getObjectFromAllObjects(this
						.getCellID());
				if (oldCell != null) {
					this.setCellID(cellID);
					oldCell.removeCellObject(this);
					c.addCellObject(this);
				}

			}
		} else {
			this.setCellID(cellID);
			c.addCellObject(this);
		}
	}

	/**
	 * Gets the full name of the Player, in the format Firstname Lastname
	 * 
	 * @return The Player's full name.
	 */
	protected String getFullName() {
		if (sLastName == null || sLastName.length() < 1) {
			return sFirstName;
		} else {
			StringBuffer buff = new StringBuffer().append(sFirstName).append(
					" ").append(sLastName);
			return buff.toString();
		}
	}

	/**
	 * Gets the Player's first name.
	 * 
	 * @return The Player's first name.
	 */
	protected String getFirstName() {
		return sFirstName;
	}

	/**
	 * Get the Player's last name.
	 * 
	 * @returns The player's last name.
	 */
	protected String getLastName() {
		return sLastName;
	}

	/**
	 * Sets the player's Active status. If the player becomes inactive, saves
	 * him to the database.
	 * 
	 * @param status
	 *            -- The player's active status.
	 */
	protected void setActive(boolean status) {
		if (!status) {
			// Save me to the database!!!
		}
		bActive = status;
	}

	/**
	 * Gets the player's active status.
	 * 
	 * @return The active status.
	 */
	protected boolean getStatus() {
		return bActive;
	}

	/**
	 * Convenience function -- returns the ID of the Player's "PlayItem".
	 * 
	 * @return The PlayItem Object ID.
	 */
	public long getPlayObjectID() {
		return thePlayer.getID();
	}

	public long getFriendsListID() {
		return thePlayer.getID() + 11;
	}

	private transient Waypoint wSurveyWaypoint; // This is simply a pointer to
												// the given waypoint.

	protected Waypoint getSurveyWaypoint() {
		return wSurveyWaypoint;
	}

	public void addSurveyWaypoint(Waypoint w) {
		if (w != null) {
			if (wSurveyWaypoint == null) {
				wSurveyWaypoint = w;
				wSurveyWaypoint.setID(server.getNextObjectID());
				addWaypoint(wSurveyWaypoint, true);
				server.addObjectToAllObjects(wSurveyWaypoint, false, false);
			} else {
				wSurveyWaypoint.setName("Survey location");
				wSurveyWaypoint.setPlanetID(getPlanetID());
				wSurveyWaypoint
						.setPlanetCRC(Constants.PlanetCRCForWaypoints[getPlanetID()]);
				wSurveyWaypoint.setX(w.getX());
				wSurveyWaypoint.setY(w.getY());
				wSurveyWaypoint.setZ(w.getZ());
				w.setIsActivated(true);
				w.setWaypointType(Constants.WAYPOINT_TYPE_PLAYER_CREATED);
				client.insertPacket(PacketFactory.buildWaypointDelta(this, w,
						Constants.DELTA_UPDATING_ITEM));
			}
		} else {
			wSurveyWaypoint = null;
		}
	}

	/**
	 * Adds a new Waypoint the PlayItem's list of waypoints. Sends a packet back
	 * to the Player containing the new waypoint.
	 * 
	 * @param w
	 *            -- The waypoint to be added.
	 */
	public void addWaypoint(Waypoint w, boolean bUpdate) {
		// System.out.println("Adding waypoint " + w.getName() + " to player " +
		// getFullName());
		thePlayer.addWaypoint(w);
		// client.insertPacket(PacketFactory.buildBaselinePLAY8(getPlayData()));
		// System.out.println("Calling buildWaypointDelta");
		if (bUpdate) {
			client.insertPacket(PacketFactory.buildWaypointDelta(this, w,
					Constants.DELTA_CREATING_ITEM));
		}
	}

	public void deleteWaypoint(Waypoint w, boolean bUpdate) {
		if (thePlayer.deleteWaypoint(w)) {
			if (bUpdate) {
				try {
					// this.despawnItem(w);
					client.insertPacket(PacketFactory.buildWaypointDelta(this,
							w, Constants.DELTA_DELETING_ITEM));
				} catch (Exception e) {
					DataLog.logEntry("Exception in Player.deleteWaypoint "
							+ e.toString(), "Player",
							Constants.LOG_SEVERITY_CRITICAL,
							ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
					// System.out.println("Exception in player.deleteWaypoint "
					// + e );
					e.printStackTrace();
				}

			}
		}
	}

	public int getLastWaypointIndex() {
		return (thePlayer.getWaypointListSize() - 1);
	}

	/**
	 * Indicates if this Player is currently riding a vehicle.
	 * 
	 * @return If the player is on/in a vehicle.
	 */
	public boolean isMounted() {
		return bIsMounted;
	}

	/**
	 * Sets the player to be mounted on "something"
	 * 
	 * @param bIsVehicle
	 *            -- Indicates if the player is mounting a vehicle or a
	 *            creature. True for vehicle, false for creature.
	 */
	public void setPlayerIsMounted(boolean bIsVehicle) {
		bIsMounted = true;
		if (bIsVehicle) {
			addState(Constants.STATE_MOUNTED_VEHICLE, -1l);
			// setStance(Constants.STANCE_DRIVING, false);
		} else {
			addState(Constants.STATE_MOUNTED_CREATURE, -1l);
			// setStance(Constants.STANCE_MOUNTED, false);
		}
		// setStance(Constants.STANCE_MOUNTED, false);
	}

	public void setPlayerIsNotMounted() {
		if (setStance(null, Constants.STANCE_STANDING, false)) { // Forcibly
																	// stand the
																	// player
																	// when they
																	// dismount,
																	// unless
																	// they
																	// dismounted
																	// because
																	// they
																	// became
																	// incapacitated.
			bIsMounted = false;
			removeState(Constants.STATE_MOUNTED_CREATURE);
			removeState(Constants.STATE_MOUNTED_VEHICLE);
		}
	}

	public long getCurrentMount() {
		return lCurrentMount;
	}

	public void setCurrentMount(long MountID) {
		lCurrentMount = MountID;
	}

	/**
	 * Get the player's current mood ID for spatial chat functions.
	 * 
	 * @return The mood ID.
	 */
	public int getMoodID() {
		return moodID;
	}

	public void setMoodID(int id) {
		if (id < 0 || id > Constants.vMoodStrings.length) {
		} else {
			moodID = id;
			setMoodString(Constants.vMoodStrings[moodID]);
		}
	}

	/**
	 * Get the Emote ID for spatial chat functions.
	 * 
	 * @return The Emote ID
	 */
	public int getPerformedEmoteID() {
		return emoteID;
	}

	/**
	 * Sets the current Emote ID for spatial chat functions.
	 * 
	 * @param id
	 *            -- The Emote ID
	 */
	public void setPerformedEmoteID(int id) {
		emoteID = id;
	}

	/**
	 * Gets a list of all of this Player's friends.
	 * 
	 * @return The friends list.
	 */
	public Vector<PlayerFriends> getFriendsList() {
		return thePlayer.getFriendsList();
	}

	/**
	 * Gets a list of all the Players which this Player is ignoring.
	 * 
	 * @return The ignore list.
	 */
	public Vector<PlayerFriends> getIgnoreList() {
		return thePlayer.getIgnoreList();
	}

	// TODO: Add in a function that detects whether the travel terminal the
	// player is using is a starport terminal.
	/**
	 * Returns whether the Player is currently using a Starport terminal instead
	 * of a Shuttleport terminal.
	 */
	public boolean isAtStarport() {
		return true;
	}

	/**
	 * Returns the ID of the Terminal this Player is currently using.
	 * 
	 * @return The terminal ID.
	 */
	public int getTerminalID() {
		return iCurrentTerminalID;
	}

	/**
	 * Return the Flourish ID this Player is currently performing, if any.
	 * 
	 * @return The Flourish ID.
	 */
	public int getFlourishID() {
		return iFlourishID;
	}

	/**
	 * Sets the Flourish ID that this Player is currently performing, if any.
	 * 
	 * @param id
	 *            -- The Flourish ID.
	 */
	public void setFlourishID(int id) {
		iFlourishID = id;
	}

	/**
	 * Returns the pointer to the Zone Server this Player is currently residing
	 * on.
	 * 
	 * @return The Zone Server.
	 */
	public ZoneServer getServer() {
		return server;
	}

	/**
	 * Sets the Zone Server this Player is active on.
	 * 
	 * @param server
	 *            -- The Zone Server.
	 */
	protected void setServer(ZoneServer server) {
		this.server = server;
	}

	/**
	 * Gets the number of "tells" this user has received / sent.
	 * 
	 * @return The tell count.
	 */
	protected int getTellCount() {
		return iTellCount;
	}

	/**
	 * Sets the tell count.
	 * 
	 * @param iTellCount
	 *            -- The number of tells the client states it's sent.
	 */
	protected void setTellCount(int iTellCount) {
		this.iTellCount = iTellCount;
	}

	/**
	 * Gets the skill animation the player is currently performing.
	 * 
	 * @return The skill animation.
	 */
	public String getPerformedAnimation() {
		bClearPerformedAnimation = false;
		return sPerformedAnimation;
	}

	public void setPerformedAnimation(String sPerformedAnimation) {
		bClearPerformedAnimation = false;
		this.sPerformedAnimation = sPerformedAnimation;
	}

	public void setBClearPerformedAnimation(boolean bClearPerformedAnimation) {
		this.bClearPerformedAnimation = bClearPerformedAnimation;
	}

	/**
	 * Unknown function.
	 * 
	 * @return
	 */
	public String getCurrentEffectOnOtherObjects() {
		return sClientEffectString;
	}

	/**
	 * Returns the Player's currently equipped weapon. Should never return null,
	 * as the Player always has a weapon equipped (even if it's just his
	 * "fists").
	 * 
	 * @return The currently equipped weapon
	 */
	public Weapon getWeapon() {
		return equippedWeapon;
	}

	/**
	 * Equips the given weapon.
	 * 
	 * @param w
	 *            -- The weapon.
	 */
	public void equipWeapon(Weapon w, boolean updateZone) {
		try {
			if (w != null) {
				equippedWeapon = w;
				if (equippedWeapon.isDefaultWeapon()) {
					defaultWeapon = equippedWeapon;
				}
				// Calculate and set the challenge rating.
				float weaponDamagePerSecond = equippedWeapon
						.getAverageDamageRound()
						/ equippedWeapon.getRefireDelay();
				setConLevel((short) weaponDamagePerSecond, updateZone);
				if (updateZone) {
					if (!vAllSpawnedObjectIDs.contains(equippedWeapon.getID())) {
						spawnItem(equippedWeapon);
					}
					client.insertPacket(PacketFactory
							.buildUpdateContainmentMessage(equippedWeapon,
									this, 4));
					client.insertPacket(PacketFactory.buildDeltasMessage(
							Constants.BASELINES_CREO, (byte) 6, (short) 1,
							(short) 5, this, equippedWeapon.getID()),
							Constants.PACKET_RANGE_CHAT_RANGE);
					client.insertPacket(PacketFactory
							.buildDeltasMessage_EquippedItem(this,
									equippedWeapon, (byte) 1,
									Constants.SLOT_ID_WEAPON),
							Constants.PACKET_RANGE_CHAT_RANGE);
					int skillRequired = equippedWeapon.getSkillRequirement();
					System.out.println("Required skill ID for weapon "
							+ equippedWeapon.getName() + ": " + skillRequired);
					if (skillRequired != -1) {
						if (!hasSkill(skillRequired)) {
							client.insertPacket(PacketFactory
									.buildChatSystemMessage("combat_effects",
											"no_proficiency", 0l, "", "", "",
											0l, "", "", "", 0l, "", "", "", 0,
											0f, false));
							setConLevel((short) 0, true);
						} else {
							System.out.println("Player has requisite skill.");
						}
					}
				}
			}
		} catch (Exception e) {
			// D'oh!
		}
	}

	public void updateEquippedWeapon(Weapon newWeapon, boolean updateZone) {
		try {
			if (equippedWeapon != null) {
				equippedWeapon.setEquipped(getInventory(), -1);
			}
			if (newWeapon != null) {
				equipWeapon(newWeapon, updateZone);
			} else {
				equipWeapon(defaultWeapon, updateZone);
			}
			if (newWeapon instanceof Instrument) {
				this.equippedInstrument = (Instrument) newWeapon;
			}
		} catch (Exception e) {
			DataLog.logException(
					"Error building deltas messages for equipped item status: "
							+ e.toString(), "Player.updateEquippedWeapon()",
					true, true, e);
		}
	}

	/**
	 * Returns the Player's current Stance.
	 * 
	 * @return The stance.
	 */
	public byte getStance() {
		return iStance;
	}

	/**
	 * Updates the Player's current stance.
	 * 
	 * @param newStance
	 *            -- The new Stance.
	 */
	public boolean setStance(CommandQueueItem action, byte newStance,
			boolean bForceNewStance) {
		if (lastUsedSurveyTool != null) {
			if (iStance == Constants.STANCE_KNEELING
					&& newStance != Constants.STANCE_KNEELING) {
				lastUsedSurveyTool.stopSurveying();
				bIsSampling = false;
				try {
					client.insertPacket(PacketFactory.buildChatSystemMessage(
							"survey", "sample_cancel", 0l, "", "", "", 0l, "",
							"", "", 0l, "", "", "", 0, 0f, false));
				} catch (Exception e) {
					// D'oh!
				}
			}
		}
		if (newStance != iStance) {
			// If we're dead, we can't just stand back up. We need to be stood
			// back up by something else.
			if (bForceNewStance
					|| (iStance != Constants.STANCE_DEAD && iStance != Constants.STANCE_INCAPACITATED)) {
				iStance = newStance;
				setCommandQueueErrorStance(iStance);
				try {
					if (client != null) {
						boolean bCanChangePosture = true;
						if (hasState(Constants.STATE_DIZZY)) {
							int iChanceToChangePosture = SWGGui
									.getRandomInt(10);
							if (iChanceToChangePosture != 0) {
								bCanChangePosture = false;
							}
							if (!bCanChangePosture) {
								// Knock me down and make me flop around.
								iStance = Constants.STANCE_KNOCKED_DOWN;
								setCommandQueueErrorStance(iStance);
								client
										.insertPacket(
												PacketFactory
														.buildObjectControllerMessage_UpdatePosture(this),
												Constants.PACKET_RANGE_CHAT_RANGE);
								client.insertPacket(PacketFactory
										.buildDeltasMessage(
												Constants.BASELINES_CREO,
												(byte) 3, (short) 1,
												(short) 0x0B, this, iStance),
										Constants.PACKET_RANGE_CHAT_RANGE);
								client.insertPacket(PacketFactory
										.buildChatSystemMessage("cbt_spam",
												"dizzy_fall_down_single"));
							}
						}
						if (bCanChangePosture) {
							client
									.insertPacket(
											PacketFactory
													.buildObjectControllerMessage_UpdatePosture(this),
											Constants.PACKET_RANGE_CHAT_RANGE);
							client.insertPacket(PacketFactory
									.buildDeltasMessage(
											Constants.BASELINES_CREO, (byte) 3,
											(short) 1, (short) 0x0B, this,
											iStance),
									Constants.PACKET_RANGE_CHAT_RANGE);
							if (this.isDancing()) {
								System.out
										.println("Player Was Dancing Resetting to not Dancing.");
								this.setIsDancing(false);
								// this.setSongID(0);// the ones set here to be
								// commented out cannot be reset ot 0 or false.
								// this.setPerformanceID(0);//seems the client
								// does that automatically when we move or
								// change posture if messed with the client will
								// stop sending updates to other players
								this.setPerformanceString("");
								// this.setC60X11(false);//
								client.insertPacket(PacketFactory
										.buildChatSystemMessage("performance",
												"dance_stop_self",
												this.getID(), this
														.getSTFFileName(),
												this.getSTFFileIdentifier(),
												this.getFullName(), 0, null,
												null, null, 0, null, null,
												null, 0, 0.0f, false));
							} else if (this.isPlayingMusic()) {
								this.setIsPlayingMusic(false);
								this.setListeningToID(0);
								this.setSongID(0);
								// @todo remove the playing musig info from the
								// player.
							}
						}
					}
				} catch (Exception e) {
					// Oh well.
				}

				return true;
			} else {
				action
						.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_IN_STANCE);
				action.setStateInvoked(getCommandQueueErrorStance());
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds a state to the player.
	 * 
	 * @param iState
	 *            -- The state to add.
	 */
	public void addState(int iStateToSet, long lTimeToApply) {
		long lPreviousState = lState;
		lState = lState | (0x01 << iStateToSet);
		if (lPreviousState != lState) {
			if (client != null) {
				try {
					client.insertPacket(PacketFactory.buildDeltasMessage(
							Constants.BASELINES_CREO, (byte) 3, (short) 1,
							(short) 0x10, this, getStateBitmask()),
							Constants.PACKET_RANGE_CHAT_RANGE);
				} catch (IOException e) {
					// D'oh!
				}
			}
		}
		if (lStateTimer == null) {
			lStateTimer = new long[Constants.NUM_STATES];
		}
		lStateTimer[iStateToSet] = lTimeToApply;
		if (iStateToSet == Constants.STATE_COMBAT && lTimeInCombat == 0) {
			lTimeInCombat = System.currentTimeMillis();
		}
	}

	private transient long lTimeInCombat = 0;

	/**
	 * Removes a state from the player.
	 * 
	 * @param iState
	 *            -- The state to remove.
	 */
	public void removeState(int iStateToSet) {
		long lPreviousState = lState;
		lState = lState & ~(0x01 << iStateToSet);
		if (lPreviousState != lState) {
			if (client != null) {
				try {
					client.insertPacket(PacketFactory.buildDeltasMessage(
							Constants.BASELINES_CREO, (byte) 3, (short) 1,
							(short) 0x10, this, getStateBitmask()),
							Constants.PACKET_RANGE_CHAT_RANGE);
				} catch (IOException e) {
					// D'oh!
				}
			}
		}
		if (lStateTimer[iStateToSet] > 0) {
			lStateTimer[iStateToSet] = 0;
		}
	}

	public boolean hasState(int iStateToSet) {
		return (lState & (1 << iStateToSet)) != 0;
	}

	/**
	 * Gets the Race ID of the Player.
	 * 
	 * @return The Player's Race ID.
	 */
	public int getRaceID() {
		return iRaceID;
	}

	/**
	 * Sets the CRC which is used to spawn this Player when other clients see
	 * him.
	 * 
	 * @param iCRC
	 *            -- The shared race CRC.
	 */
	public void setSharedCRC(int iCRC) {
		spawnCRC = iCRC;
	}

	/**
	 * Gets the CRC used to spawn this Player when other clients see him.
	 * 
	 * @return The shared CRC.
	 */
	public int getSharedCRC() {
		return spawnCRC;
	}

	/**
	 * Gets the number of updates performed on the Skill List since it was last
	 * reset.
	 * 
	 * @return The skill list update counter.
	 */
	public synchronized int getSkillListUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iSkillListUpdateCount++;
		}
		return iSkillListUpdateCount;
	}

	/**
	 * Gets the number of credits the Player currently has banked.
	 * 
	 * @return The bank credits.
	 */
	public int getBankCredits() {
		return iBankCredits;
	}

	public void transferBankCreditsToInventory() {
		iInventoryCredits += iBankCredits;
		iBankCredits = 0;
		updateCredits();
	}

	/**
	 * Gets the number of credits the Player currently has in inventory.
	 * 
	 * @return The inventory credits.
	 */
	public int getInventoryCredits() {
		return iInventoryCredits;
	}

	public void transferInventoryCreditsToBank() {
		iBankCredits += iInventoryCredits;
		iInventoryCredits = 0;
		updateCredits();
	}

	public boolean hasEnoughCredits(int amount) {
		boolean retval = false;
		int comp = iBankCredits + iInventoryCredits;
		if (comp >= amount) {
			retval = true;
		}
		return retval;
	}

	public boolean hasEnoughInventoryCredits(int amount) {
		boolean retval = false;

		if (iInventoryCredits >= amount) {
			retval = true;
		}
		return retval;
	}

	/**
	 * Returns the list of HAM modifiers applying to the Player.
	 * 
	 * @return The Ham Modifiers list.
	 */
	public int[] getHamModifiers() {
		return iHamModifiers;
	}

	public void setHamModifiers(int[] mods) {
		iHamModifiers = mods;
	}

	/**
	 * Increments the Ham Modifiers list update counter, then gets it.
	 * 
	 * @return The Ham Modifiers update counter.
	 */
	public synchronized int getHamModifiersUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iHamModifiersUpdateCount++;
		}
		return iHamModifiersUpdateCount;
	}

	/**
	 * Gets the period of time remaining in which the Player is incapacitated
	 * and unable to perform actions.
	 * 
	 * @return The incap timer.
	 */
	public int getIncapTimer() {
		return iIncapTimer;
	}

	/**
	 * Gets the Player's Scale.
	 * 
	 * @return The Scale factor of the Player.
	 */
	public float getScale() {
		return fScale;
	}

	/**
	 * Gets the Player's current Battle Fatigue level.
	 * 
	 * @return The Battle Fatigue of the Player.
	 */
	public int getBattleFatigue() {
		return iBattleFatigue;
	}

	public void updateBattleFatigue(int iFatigueToAdd, boolean updateZone) {
		int iPreviousBattleFatigue = iBattleFatigue;
		iBattleFatigue += iFatigueToAdd;
		iBattleFatigue = Math.max(iBattleFatigue, 0);
		iBattleFatigue = Math.min(iBattleFatigue, 1000);
		if (updateZone && (iBattleFatigue != iPreviousBattleFatigue)) {
			try {
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 3, (short) 1,
						(short) 15, this, iBattleFatigue));
			} catch (Exception e) {
				// D'oh!
			}
		}
	}

	/**
	 * Builds the actual state bitmask number, from the State bitset, then
	 * returns it.
	 * 
	 * @return The state bitmask.
	 */
	public long getStateBitmask() {
		return lState;
	}

	/**
	 * Gets the list of wounds to the Player's maximum ham.
	 * 
	 * @return The ham wounds list.
	 */
	public int[] getHamWounds() {
		return iHamWounds;
	}

	/**
	 * Increments the update counter on the Ham Wounds variable, then returns
	 * it.
	 * 
	 * @return The ham wounds update counter.
	 */
	public synchronized int getHamWoundsUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iHamWoundsUpdateCount++;
		}
		return iHamWoundsUpdateCount;
	}

	/**
	 * Gets the list of skill modifiers applicable for this Player.
	 * 
	 * @return The skill mods list.
	 */
	public Vector<SkillMods> getSkillModsList() {
		return vSkillModsList;
	}

	/**
	 * Searches for a particular skill mod by name, in the Player's list of
	 * skill mods, and returns it.
	 * 
	 * @param skillModName
	 *            -- The skill mod being searched for.
	 * @return The skill mod, or null if the Player does not have the skill mod.
	 */
	public SkillMods getSkillMod(String skillModName) {
		try {
			for (int i = 0; i < vSkillModsList.size(); i++) {
				SkillMods mod = vSkillModsList.elementAt(i);
				if (mod.getName().equalsIgnoreCase(skillModName)) {
					return mod;
				}
			}
		} catch (Exception e) {
			// Most likely a null pointer -- return null anyway.
			return null;
		}
		return null;
	}

	/**
	 * Increases the modifier value of a skill mod, by name. If the Player does
	 * not have the skill mod, adds the skill mod.
	 * 
	 * @param sModName
	 *            -- The skill modifier name.
	 * @param iDeltaValue
	 *            -- The value to increase the modifier by.
	 */
	public void increaseSkillModValue(String sModName, int iDeltaValue,
			boolean bUpdateZone) {
		System.out.println("Player " + sFirstName + " increasing skill mod "
				+ sModName + " by " + iDeltaValue);
		try {
			for (int i = 0; i < vSkillModsList.size(); i++) {
				SkillMods existMod = vSkillModsList.elementAt(i);
				if (existMod.getName().equals(sModName)) {
					int iSkillModValue = existMod.getSkillModModdedValue();
					iSkillModValue += iDeltaValue;
					System.out.println("New skillmod value: " + iSkillModValue);
					if (iSkillModValue == 0) {
						vSkillModsList.remove(existMod);
						if (bUpdateZone) {
							client.insertPacket(PacketFactory
									.buildSkillModsDelta(this, existMod,
											Constants.DELTA_DELETING_ITEM),
									Constants.PACKET_RANGE_CHAT_RANGE);
							// client.insertPacket(PacketFactory.buildSkillModsDelta(this,
							// existMod, Constants.DELTA_DELETING_ITEM));
						}
					} else {
						existMod.setSkillModModdedValue(iSkillModValue);
						if (bUpdateZone) {
							client.insertPacket(PacketFactory
									.buildSkillModsDelta(this, existMod,
											Constants.DELTA_UPDATING_ITEM),
									Constants.PACKET_RANGE_CHAT_RANGE);
							// client.insertPacket(PacketFactory.buildSkillModsDelta(this,
							// existMod, Constants.DELTA_UPDATING_ITEM));
						}
					}
					return;
				}
			}
			SkillMods mod = new SkillMods();
			mod.setName(sModName);
			mod.setSkillModModdedValue(iDeltaValue);
			addSkillMod(mod, bUpdateZone);
		} catch (Exception e) {
			DataLog.logEntry("Error updating skill mod: " + e.toString(),
					"Player", Constants.LOG_SEVERITY_CRITICAL,
					ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
			// System.out.println("Error updating skill mod: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Add a new Skill Mod to the list of Skill Mods.
	 * 
	 * @param mod
	 *            -- The skill mod to add.
	 */
	public void addSkillMod(SkillMods mod, boolean bUpdateZone) {
		vSkillModsList.add(mod);
		if (bUpdateZone) {
			try {
				client.insertPacket(PacketFactory.buildSkillModsDelta(this,
						mod, Constants.DELTA_CREATING_ITEM),
						Constants.PACKET_RANGE_CHAT_RANGE);
				// client.insertPacket(PacketFactory.buildSkillModsDelta(this,
				// mod, Constants.DELTA_CREATING_ITEM));
			} catch (Exception e) {
				DataLog.logEntry("Error adding skill mod: " + e.toString(),
						"Player", Constants.LOG_SEVERITY_CRITICAL,
						ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
				// System.out.println("Error adding skill mod: " +
				// e.toString());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a set of Skill Mods to the list of Skill Mods.
	 * 
	 * @param mods
	 *            -- The list of skill mods to add.
	 */
	public void addSkillMods(Vector<SkillMods> mods) {
		vSkillModsList.addAll(mods);
	}

	/**
	 * Gets the list of encumberances currently active on the Player's HAM
	 * values.
	 * 
	 * @return The HAM encumberances.
	 */
	public int[] getHamEncumberances() {
		return iHamEncumberance;
	}

	public void addEncumberance(int[] enc) {
		for (int i = 0; i < iHamEncumberance.length; i++) {
			iHamEncumberance[i] += enc[i];
		}
	}

	public void removeEncumberance(int[] enc) {
		for (int i = 0; i < iHamEncumberance.length; i++) {
			iHamEncumberance[i] -= enc[i];
		}
	}

	/**
	 * Gets the update count of the HAM encumberances active on the Player.
	 * 
	 * @return The Ham Encumberances update counter.
	 */
	public synchronized int getHamEncumberanceUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iHamEncumberanceUpdateCount++;
		}
		return iHamEncumberanceUpdateCount;
	}

	/**
	 * Gets the Player's current velocity.
	 * 
	 * @return The velocity.
	 */
	public float getVelocity() {
		return fCurrentVelocity;
	}

	/**
	 * Sets the Player's current velocity.
	 * 
	 * @param v
	 *            -- The velocity.
	 */
	public void setVelocity(float v) {
		fCurrentVelocity = Math.min(v, getMaxVelocity());
	}

	/**
	 * Gets the Maximum velocity the Player can travel at.
	 * 
	 * @return The max velocity.
	 */
	public float getMaxVelocity() {
		return fMaxVelocity;
	}

	/**
	 * Sets the Maximum velocity the Player can travel at.
	 * 
	 * @param fVelocity
	 *            -- The velocity.
	 */
	public void setMaxVelocity(float fVelocity) {
		if (fMaxVelocity != fVelocity) {
			fMaxVelocity = fVelocity;
			// Deltas message
			try {
				byte[] velocityDelta = PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 4, (short) 1,
						(short) 7, this, fMaxVelocity);
				client.insertPacket(velocityDelta,
						Constants.PACKET_RANGE_CHAT_RANGE);
			} catch (Exception e) {
				// D'oh!
			}
		}
	}

	/**
	 * Gets the Player's current acceleration value.
	 * 
	 * @return The acceleration value.
	 */
	public float getAcceleration() {
		return fCurrentAcceleration;
	}

	public void setAcceleration(float f) {
		if (fCurrentAcceleration != f) {
			// Deltas message
			fCurrentAcceleration = f;
			try {
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 4, (short) 1,
						(short) 0x0B, this, fCurrentAcceleration),
						Constants.PACKET_RANGE_CHAT_RANGE);
			} catch (Exception e) {
				// D'oh!
			}
		}

	}

	/**
	 * Gets the Player's current target ID.
	 * 
	 * @return the Target ID.
	 */
	public long getTargetID() {
		return lTargetID;
	}

	/**
	 * Sets the Player's current target ID.
	 * 
	 * @param objID
	 *            -- The Player's Target ID.
	 */
	public void setTargetID(long objID) {

		lTargetID = objID;
		if (objID != 0) {
			SOEObject t = this.server.getObjectFromAllObjects(objID);
			if (t == null) {
				System.out.println("Unknown object targeted with ID " + objID);
				return;
			} else {
				if (this.getInventoryItems().contains(t)
						|| this.getDatapad().getIntangibleObjects().contains(t)) {
					// we dont need to inform of a target in our inventory.
					return;
				} else if (t.getParentID() == this.getInventory().getID()) {
					return;
				}
			}
			try {
				// this.getClient().insertPacket(PacketFactory.buildCreo6ChangeTarget(this,
				// t), Constants.PACKET_RANGE_CHAT_RANGE);
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 6, (short) 1,
						(short) Constants.DELTAS_CREO6.TARGET_ID.ordinal(),
						this, objID), Constants.PACKET_RANGE_CHAT_RANGE);

			} catch (Exception e) {
				DataLog.logEntry(
						"Exception Caught while sending target update to players "
								+ e.toString(), "Player",
						Constants.LOG_SEVERITY_CRITICAL,
						ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
				// System.out.println("Exception Caught while sending target update to players "
				// + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the Player's current turning radius.
	 * 
	 * @return The turn radius.
	 */
	public float getTurnRadius() {
		return fTurnRadius;
	}

	/**
	 * Gets the list of all items in the Player's inventory / equipped by the
	 * Player.
	 * 
	 * @return The Inventory list.
	 */
	public Vector<TangibleItem> getInventoryItems() {
		return vInventoryItemsList;
	}

	/**
	 * Gets the Player's current mood name.
	 * 
	 * @return The mood name.
	 */
	public String getMoodString() {
		return sMoodString;
	}

	/**
	 * Sets the Player's current mood name.
	 * 
	 * @param sMood
	 *            -- The mood name.
	 */
	public void setMoodString(String sMood) {
		sMoodString = sMood;
	}

	/**
	 * Gets the Player's current song/dance performance string.
	 * 
	 * @return The Player's performance string.
	 */
	public String getPerformanceString() {
		return sPerformanceString;
	}

	public void setPerformanceString(String sPerformanceString)
			throws IOException {
		this.sPerformanceString = sPerformanceString;
		client.insertPacket(PacketFactory.buildDeltasMessage(
				Constants.BASELINES_CREO, (byte) 6, (short) 1, (short) 0x03,
				this, this.sPerformanceString, false),
				Constants.PACKET_RANGE_CHAT_RANGE);
	}

	/**
	 * Gets a list of all Players currently defending themselves from this
	 * Player.
	 * 
	 * @return The defender list.
	 */
	public Vector<Long> getDefenderList() {
		return vDefenderList;
	}

	/**
	 * Increments and then gets the Defender List update counter.
	 * 
	 * @return The defender list update counter.
	 */
	public synchronized int getDefenderListUpdateCounter(boolean bIncrement) {
		if (bIncrement) {
			iDefenderListUpdateCounter++;
		}
		return iDefenderListUpdateCounter;
	}

	/**
	 * Gets the "consider" level of this Player.
	 * 
	 * @return The Player's con level.
	 */
	public short getConLevel() {
		return iConLevel;
	}

	public void setConLevel(short con, boolean updateZone) {
		iConLevel = con;
		if (updateZone) {
			try {
				// Yes, a different level for me to see than for everyone else
				// to see -- that's how pvp consider works.
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 6, (short) 1,
						(short) 2, this, iConLevel));
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 6, (short) 1,
						(short) 2, this, (short) 1),
						Constants.PACKET_RANGE_CHAT_RANGE_EXCLUDE_SENDER);
			} catch (Exception e) {
				// D'oh!
			}
		}

	}

	/**
	 * Gets the Object ID of the Player's current group, or 0 if the Player is
	 * not currently part of a group.
	 * 
	 * @return The group ID.
	 */
	public long getGroupID() {
		if (myGroup != null) {
			return myGroup.getID();
		} else {
			return 0;
		}
	}

	public void setGroupObject(Group g) {
		myGroup = g;
	}

	/**
	 * Gets the current Dance the Player is dancing, by ID.
	 * 
	 * @return The Dance ID.
	 */
	public int getDanceID() {
		return iDanceID;
	}

	/**
	 * Gets the current Song the Player is singing, by ID.
	 * 
	 * @return The Song ID.
	 */
	public int getSongID() {
		return iSongID;
	}

	public void setSongID(int iSongID) throws IOException {
		this.iSongID = iSongID;
		client.insertPacket(PacketFactory.buildDeltasMessage(
				Constants.BASELINES_CREO, (byte) 6, (short) 1, (short) 0x0C,
				this, this.iSongID), Constants.PACKET_RANGE_CHAT_RANGE);
	}

	/**
	 * Gets the Player's current Maximum HAM values.
	 * 
	 * @return The Max Ham.
	 */
	public int[] getMaxHam() {
		return iMaxHam;
	}

	/**
	 * Gets the Player's current HAM values.
	 * 
	 * @return The current ham.
	 */
	public int[] getCurrentHam() {
		return iCurrentHam;
	}

	/**
	 * Increments and then gets the update counter for the Max HAMs.
	 * 
	 * @return The Max Ham update counter.
	 */
	public synchronized int getMaxHamUpdateCounter(boolean bIncrement) {
		if (bIncrement) {
			iMaxHamUpdateCount++;
		}
		return iMaxHamUpdateCount;
	}

	/**
	 * Increments and then gets the update counter for the Current HAMS.
	 * 
	 * @return The Current Ham update counter.
	 */
	public synchronized int getCurrentHamUpdateCounter(boolean bIncrement) {
		if (bIncrement) {
			iCurrentHamUpdateCount++;
		}
		return iCurrentHamUpdateCount;
	}

	/**
	 * Increments and then gets the update counter for the Equipped Item list.
	 * 
	 * @return The Equippped Item update counter.
	 */
	public synchronized int getEquippedItemUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iEquippedItemUpdateCount++;
		}
		return iEquippedItemUpdateCount;
	}

	/**
	 * Returns if this Player has GM privileges.
	 * 
	 * @return If this Player's Client's Account is a GM.
	 */
	protected boolean isGM() {
		return server.getAccountDataFromLoginServer(client.getAccountID())
				.getIsGM();
	}

	protected boolean isDev() {
		return server.getAccountDataFromLoginServer(client.getAccountID())
				.getIsDeveloper();
	}

	/**
	 * Attaches this Player to a specific Zone Client.
	 * 
	 * @param c
	 *            -- The Zone Client.
	 */
	public void setClient(ZoneClient c) {
		client = c;
	}

	/**
	 * Gets the pointer to this Player's Zone Client.
	 * 
	 * @return The Zone Client.
	 */
	public ZoneClient getClient() {
		return client;
	}

	/**
	 * Spawns the Player, including his PlayItem, his inventory, and his
	 * personal appearance effects (hair, etc.)
	 */
	protected synchronized void spawnPlayer() {
		// Force the player's speed back to proper whenever he is spawned.
		if (iStance == Constants.STANCE_PRONE) {
			fMaxVelocity = Constants.MAX_CRAWL_SPEED_METERS_SEC;
		} else {
			fMaxVelocity = Constants.MAX_PLAYER_RUN_SPEED_METERS_SEC;
		}
		fCurrentAcceleration = 1.0f;
		defaultWeapon.setWeaponType(Constants.WEAPON_TYPE_UNARMED);
		try {
			// this saved player object will be used to place the player in the
			// same position he was in when he saved.
			// this will eliminate the problem with always spawning north.
			/*
			 * Player savedCoords = new Player(server);
			 * savedCoords.setX(this.getX()); savedCoords.setY(this.getY());
			 * savedCoords.setZ(this.getZ());
			 * savedCoords.setOrientationN(this.getOrientationN());
			 * savedCoords.setOrientationS(this.getOrientationS());
			 * savedCoords.setOrientationE(this.getOrientationE());
			 * savedCoords.setOrientationW(this.getOrientationW());
			 * savedCoords.setPlanetID(this.getPlanetID());
			 * savedCoords.setCellID(this.getCellID());
			 * savedCoords.setCellX(this.getCellX());
			 * savedCoords.setCellY(this.getCellY());
			 * savedCoords.setCellZ(this.getCellZ());
			 */
			client.setClientNotReady();
			initializeState();

			if (iStance != Constants.STANCE_STANDING
					&& iStance != Constants.STANCE_KNEELING
					&& iStance != Constants.STANCE_PRONE
					&& iStance != Constants.STANCE_SITTING) {
				iStance = Constants.STANCE_SITTING;
			}

			// isLoading = true;
			client.insertPacket(PacketFactory.buildChatServerStatus());
			client.insertPacket(PacketFactory.buildParametersMessage());
			client.insertPacket(PacketFactory
					.buildConnectPlayerResponseMessage());
			client.insertPacket(PacketFactory.buildCmdStartScene(this, 1));
			client.insertPacket(PacketFactory.buildServerTimeMessage());
			// System.out.println("SpawnPlayer Cell id " + this.getCellID());
			if (getCellID() != 0) {
				System.out.println("Player Loaded in Cell ID:" + this.getCellID());
				Cell Loc = (Cell) server.getObjectFromAllObjects(
						this.getCellID());
				if (Loc == null) {
					DataLog.logEntry("Null Cell In Spawn Player ID:"
							+ this.getCellID(), "Player",
							Constants.LOG_SEVERITY_CRITICAL,
							ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
					// System.out.println("Null Cell In Spawn Player ID:" +
					// lCellID);
					// lCellID = getCellID();
				}
				SOEObject so = Loc.getBuilding();
				if (so instanceof TutorialObject) {
					TutorialObject to = (TutorialObject) so;

					if (!to.hasCompleted()) {
						System.out.println("Spawning tutorial building.");
						spawnItem(to);
					} else {
						this.setCellID(0);
					}
				} else if (so instanceof Structure) {
					Structure s = (Structure) so;
					if (s != null && !s.getIsStaticObject()) {
						spawnItem(s);
					} else if (s == null) {
						DataLog.logEntry("Null Structure In Spawn Player ID:"
								+ this.getCellID(), "Player",
								Constants.LOG_SEVERITY_CRITICAL,
								ZoneServer.ZoneRunOptions.isBLogToConsole(),
								true);
						// System.out.println("Null Structure in SpawnPlayer" );
						if (Loc != null) {
							s = Loc.getBuilding();
							if (s != null) {
								spawnItem(s);
							} else {
								System.out
										.println("Bad Structure cannot find.");
							}
						}
					}
				}

			}
			// System.out.println("SpawnPlayer Cell id " + this.getCellID());
			if (tutorial != null && getCellID() == 0
					&& !tutorial.hasCompleted()) {
				// System.out.println("Player detected in tutorial. Spawning tutorial 1");
				spawnItem(tutorial);
				Enumeration<Cell> cEnum = tutorial.getCellsInBuilding()
						.elements();
				while (cEnum.hasMoreElements()) {
					Cell c = cEnum.nextElement();
					if (c.getCellObjects().containsKey(this.getID())) {
						setCellID(c.getID());
					}
				}
			} else if (tutorial != null && getCellID() != 0
					&& !tutorial.hasCompleted()) {
				// System.out.println("Player detected in tutorial. Spawning tutorial 2");
				spawnItem(tutorial);
			} else if (tutorial != null && getCellID() != 0
					&& tutorial.hasCompleted()) {
				this.setCellID(0);
			}

			if (iHamModifiers == null) {
				iHamModifiers = new int[iNumberOfHams];
			}
			spawnItem(this);
			if (this.getCellID() != 0) {
				client.insertPacket(PacketFactory
						.buildUpdateContainmentMessage(this, server
								.getObjectFromAllObjects(this.getCellID()), 4));
			}

			// System.out.println("Spawn the Player Item.");
			spawnItem(thePlayer);

			server.addObjectToAllObjects(thePlayer, false, false);
			if (tHairObject != null) {
				spawnItem(tHairObject);
				server.addObjectToAllObjects(tHairObject, false,
						false);
			}
			spawnItem(tDatapad);
			server.addObjectToAllObjects(tDatapad, false, false);
			// ----------------------------------------------------------
			// Mission Related Spawning
			// System.out.println("Spawn the MissionBag Item.");
			spawnItem(tMissionBag);
			server.addObjectToAllObjects(tMissionBag, false, false);
			Vector<MissionObject> vML = tMissionBag.getVMissionList();
			for (int i = 0; i < vML.size(); i++) {
				MissionObject m = vML.elementAt(i);
				// tMissionBag.addIntangibleObject(m);
				// System.out.println("Spawn the MissionObject Item.");
				spawnItem(m);
				server.addObjectToAllObjects(m, false, false);
			}
			// ------------------------------------------------------------
			// System.out.println("Spawn the Player Inventory.");
			spawnItem(tPlayerInventory);
			server.addObjectToAllObjects(tPlayerInventory, false,
					false);
			// System.out.println("Spawn the Player Bank.");
			spawnItem(tBank);
			server.addObjectToAllObjects(tBank, false, false);

			for (int i = 0; i < vInventoryItemsList.size() /* && i < 1 */; i++) {
				TangibleItem e = vInventoryItemsList.elementAt(i);
				if (e instanceof Deed) {
					Deed d = (Deed) e;
					d.setServer(server);
					if (!d.isPlaced()) {
						// this prevents a placed house deed to spawn to the
						// inventory.
						// System.out.println("Spawn a Deed.");
						spawnItem(d);
						server.addObjectToAllObjects(d, false,
								false);
					}
				} else {
					// System.out.println("Spawn an Item.");
					spawnItem(e); // Null pointer exception -- TANO3.
					server.addObjectToAllObjects(e, false, false);
					// if this item is a backpack or something that contains
					// objects then we need to spawn its contents too or it will
					// appear empty
					if (e.getLinkedObjects().size() >= 1) {
						// System.out.println("Spawning Container With Items in it: "
						// + e.getIFFFileName());
						Vector<TangibleItem> vContainedItems = e
								.getLinkedObjects();
						for (int c = 0; c < vContainedItems.size(); c++) {
							TangibleItem contained = vContainedItems.get(c);

							// System.out.println("Spawning Item in Container: "
							// + contained.getIFFFileName());
							if (contained.getContainer().equals(e)) {
								// System.out.println("Spawn Contained Item.");
								spawnItem(contained);
							}
							server.addObjectToAllObjects(contained,
									false, false);
						}
					}
				}
			}

			// This doesn't necessarially have to happen until/unless the Player
			// opens their bank.
			Vector<TangibleItem> vBankItems = tBank.getLinkedObjects();
			for (int i = 0; i < vBankItems.size(); i++) {
				TangibleItem t = vBankItems.get(i);
				// System.out.println("Spawn Bank Item.");
				spawnItem(t);
				server.addObjectToAllObjects(t, false, false);
				if (t.getLinkedObjects().size() >= 1) {
					// System.out.println("Spawning Container In Bank With Items in it: "
					// + t.getIFFFileName());
					Vector<TangibleItem> vContainedItems = t.getLinkedObjects();
					for (int c = 0; c < vContainedItems.size(); c++) {
						TangibleItem contained = vContainedItems.get(c);
						// System.out.println("Spawning Item in Container: " +
						// contained.getIFFFileName());
						if (contained.getContainer().equals(t)) {
							// System.out.println("Spawn Contained Item.");
							spawnItem(contained);
						}
						server.addObjectToAllObjects(contained,
								false, false);
					}
				}
			}
			Vector<IntangibleObject> datapadItems = tDatapad
					.getIntangibleObjects();
			for (int i = 0; i < datapadItems.size(); i++) {
				IntangibleObject itno = datapadItems.elementAt(i);
				//System.out.println("Spawn Datapad Item with iff name "
				//		+ itno.getIFFFileName());
				// if (itno instanceof ManufacturingSchematic) {
				// tDatapad.removeIntangibleObject(itno);
				// server.removeObjectFromAllObjects(itno, false);

				// ManufacturingSchematic schematic =
				// (ManufacturingSchematic)itno;
				// CraftingSchematic cSchem = schematic.getCraftingSchematic();
				// String sCraftedItemIFFFilename =
				// cSchem.getCraftedItemIFFFilename();
				// System.out.println("Crafted item name: " +
				// sCraftedItemIFFFilename);
				// ItemTemplate template =
				// DatabaseInterface.getTemplateDataByFilename(sCraftedItemIFFFilename);
				// System.out.println("Template data retrieved.  IFF filename: "
				// + template.getIFFFileName());
				// String stfFileName = template.getSTFFileName();
				// String stfFileIdentifier = template.getSTFFileIdentifier();
				// System.out.println("Setting filename " + stfFileName +
				// ", identifier " + stfFileIdentifier +
				// " for datapad manufacturing schematic.");
				// schematic.setSTFFileNameForMFGSchematic(template.getSTFFileName());
				// schematic.setSTFFileIdentifierForMFGSchematic(template.getSTFFileIdentifier());
				// schematic.setCraftedName(template.getIFFFileName());

				// } else {
				spawnItem(itno);
				// }
				server.addObjectToAllObjects(itno, false, false);
				SOEObject ac = itno.getAssociatedCreature();
				if (ac != null) {
					server.addObjectToAllObjects(ac, false, false);
				}
			}

			// System.out.println("CMD Scene Ready.");
			client.insertPacket(PacketFactory.buildCmdSceneReady());
			// This is to be moved to "handleClientReady".
			// System.out.println("Sending Weather Message.");
			client.insertPacket(PacketFactory.buildServerWeatherMessage(server.getCurrentWeather(getPlanetID())));

			// if (true) return;

			// Can't do this.
			/*
			 * SOEObject theCell = server.getObjectFromAllObjects(getCellID());
			 * if (theCell == null) {
			 * client.insertPacket(PacketFactory.buildUpdateTransformMessage
			 * (this)); } else {
			 * client.insertPacket(PacketFactory.buildUpdateCellTransformMessage
			 * (this, theCell)); }
			 */
			// isLoading = false;
			/*
			 * this.setX(savedCoords.getX()); this.setY(savedCoords.getY());
			 * this.setZ(savedCoords.getZ());
			 * this.setOrientationN(savedCoords.getOrientationN());
			 * this.setOrientationS(savedCoords.getOrientationS());
			 * this.setOrientationE(savedCoords.getOrientationE());
			 * this.setOrientationW(savedCoords.getOrientationW());
			 * this.setPlanetID(savedCoords.getPlanetID());
			 * this.setCellID(savedCoords.getCellID());
			 * this.setCellX(savedCoords.getCellX());
			 * this.setCellY(savedCoords.getCellY());
			 * this.setCellZ(savedCoords.getCellZ());
			 * 
			 * if(savedCoords.getCellID() == 0) {
			 * client.insertPacket(PacketFactory
			 * .buildObjectControllerPlayerDataTransformToClient(this,
			 * 0x1B),Constants.PACKET_RANGE_CHAT_RANGE); } else {
			 * client.insertPacket(PacketFactory.
			 * buildObjectControllerDataTransformWithParentObjectToClient(this,
			 * 0x1B),Constants.PACKET_RANGE_CHAT_RANGE); }
			 */
		} catch (Exception e) {
			DataLog.logEntry("Exploded spawning the player: " + e.toString(),
					"Player", Constants.LOG_SEVERITY_CRITICAL,
					ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
			// System.out.println("Exploded spawning the player: " +
			// e.toString());
			e.printStackTrace();
		}
	}

	protected void despawnItem(SOEObject i) throws IOException {
		if (i == null) {
			return;
		}
		if (i.equals(this)) {
			// System.out.println("Despawn Our Selves in despawnitem");
			// why should we despawn us to our us?
			/*
			 * StackTraceElement[] elements =
			 * Thread.currentThread().getStackTrace(); for (int s = 0; s <
			 * elements.length; s++) { System.out.println(elements[s]); }
			 */
			return;
		}
		if (i instanceof CreaturePet) {
			CreaturePet pet = (CreaturePet) i;
			if (pet.isFollowing()) {
				if (pet.objectBeingFollowed() != null
						&& pet.objectBeingFollowed().equals(this)) {
					return;
				}
			}
		}
		if (i.getIsStaticObject()) {
			// System.out.println("Attempting to despawn a static item -- no need.");
			return;
		}
		long itemID = i.getID();
		boolean bFound = false;
		for (int I = 0; I < vAllSpawnedObjectIDs.size() && !bFound; I++) {
			long lCompareID = vAllSpawnedObjectIDs.elementAt(I);
			if (lCompareID == itemID) {
				bFound = true;
				client.insertPacket(PacketFactory.buildSceneDestroyObject(i));
				vAllSpawnedObjectIDs.removeElementAt(I);
				return;
			}
		}

		if (!bFound) {
			// System.out.println("Player.despawnItem: object with IFF " +
			// i.getIFFFileName() + ", ID " + itemID + " is not spawned!");
			// StackTraceElement[] trace =
			// Thread.currentThread().getStackTrace();
			// for (int j = 0; j < trace.length; j++) {
			// System.out.println(trace[j].toString());
			// }
		}
	}

	/**
	 * Spawns an Item.
	 * 
	 * @param i
	 *            -- The Item to spawn.
	 * @throws IOException
	 *             If an error occured building the packets.
	 */
	protected void spawnItem(SOEObject i) throws IOException {
		if (vAllSpawnedObjectIDs == null) {
			// System.out.println("Player::spawnItem -- vector of IDs is null.  Instantiating...");
			vAllSpawnedObjectIDs = new Vector<Long>();
		}
		
		
		// If an object is in a cell, and the Player is not in the same building, do not spawn it.
		if (i == null) {
			return;
		}
		long lObjectCellID = i.getCellID();
		Cell c = null;
		Structure s = null;
		if (lObjectCellID != 0) {
			System.out.println("Spawning object in cell.");
			c = (Cell)server.getObjectFromAllObjects(lObjectCellID);
			s = c.getBuilding();
			System.out.println("Found building: " + s.getStructureName());
			if (s.contains(this) || i.equals(this)) {
				System.out.println("Player in building -- spawn the item.");
			} else {
				System.out.println("Player outside, do NOT spawn the item.");
				return;
			}
		}
		c = null;
		s = null;
		boolean sceneEndBaseLinesSent = false;
		long itemID = i.getID();
		try {
			if (i.equals(this)) {
				// System.out.println("OUR Player in Spawn Item Detected.");
				if (this.isMounted()) {
					return;
				}

			}
			if (i.getIsStaticObject() && !(i instanceof Structure)) {
				// System.out.println("Attempting to spawn a static item -- client already knows about this item.");
				return;
			}
			// System.out.println("Spawning object with ID " + itemID + " IFF: "
			// + i.getIFFFileName() + " , " + i.getClass()+ ".");
			// System.out.println("Searching list for if spawned.");

			for (int I = 0; I < vAllSpawnedObjectIDs.size(); I++) {
				long lCompareID = vAllSpawnedObjectIDs.elementAt(I);
				// System.out.println("ID: " + itemID + ", comparator: " +
				// lCompareID + ", equal? " + (lCompareID == itemID));
				if (lCompareID == itemID) {
					// System.out.println("Player.spawnItem: object with IFF " +
					// i.getIFFFileName() + " is already spawned!");
					// StackTraceElement[] trace =
					// Thread.currentThread().getStackTrace();
					// for (int j = 0; j < trace.length; j++) {
					// System.out.println(trace.toString());
					// }
					return;
				}
			}

			boolean bShared = false;
			if ((i instanceof Player)
					&& !((i instanceof Terminal) || (i instanceof NPC))) {
				bShared = true;
			}

			if (i.getID() == this.getCurrentMount() && this.isMounted()) {
				// cant touch this while mounted !!!! Hammer Time!!!
				// System.out.println("Spawn Vehicle Packet While Mounted.");
				sceneEndBaseLinesSent = true;
			} else if (i.getIsStaticObject()) {
				// do nothing we dont need a crc spawn,
				// this means its a building in the world but we do need
				// permissions to be sent.
				// these are taken care of by the spawn building section in this
				// method, look further down.VVV
				// System.out.println("Static Object Not Sending SpawnByCRC");
			} else {
				client.insertPacket(PacketFactory.buildSceneCreateObjectByCRC(
						i, bShared));
			}
			// i.resetTransientVariables();
			c = null;
			long ltCellID = 0;
			if (i instanceof Terminal) {
				if (i.getIsStaticObject()) {
					return;
				}
				Terminal t = (Terminal) i;

				ltCellID = t.getCellID();
				if (ltCellID > 0) {
					//System.out.println("Spawning terminal ID " + t.getID() + ", name " + t.getFullName() + " in cell ID " + ltCellID);
					c = (Cell) server.getObjectFromAllObjects(ltCellID);
					c.addCellObject(t); // Cell is null, so the terminal's cell ID is incorrect.
					client.insertPacket(PacketFactory
							.buildUpdateContainmentMessage(t, c, -1));
					s = c.getBuilding();
					//System.out.println("Building name: " + s.getStructureName());
				} else {
					//System.out.println("Spawning terminal ID " + t.getID() + ", name " + t.getFullName() + " in the world.");
				}
				client.insertPacket(PacketFactory.buildBaselineCREO3(t));
				client.insertPacket(PacketFactory.buildBaselineCREO6(t));
				//System.out.println("Writing PVP status bitmask " + Integer.toBinaryString(t.getPVPStatus()));
				client.insertPacket(PacketFactory.buildUpdatePvPStatusMessage(t));
				// client.insertPacket(PacketFactory.buildSceneEndBaselines(t));
				client.insertPacket(PacketFactory
						.buildObjectControllerDataTransformObjectToClient(t,
								0x21));

			} else if (i instanceof Vehicle) {
				if (i.getIsStaticObject()) {
					return;
				}
				Vehicle v = (Vehicle) i;

				if (v.getID() == this.getCurrentMount() && this.isMounted()) {
					// System.out.println("Spawn Vehicle Packet While Mounted.");
				} else {
					client.insertPacket(PacketFactory.buildBaselineCREO1(v));
					client.insertPacket(PacketFactory.buildBaselineCREO3(v));
					client.insertPacket(PacketFactory.buildBaselineCREO4(v));
					client.insertPacket(PacketFactory.buildBaselineCREO6(v));
					client
							.insertPacket(PacketFactory
									.buildSceneEndBaselines(v));
				}

			} else if (i instanceof NPC) {
				if (i.getIsStaticObject()) {
					return;
				}
				NPC n = (NPC) i;

				if (n.getCellID() > 0) {
					c = (Cell) server.getObjectFromAllObjects(n.getCellID());
					c.addCellObject(n);
					client.insertPacket(PacketFactory
							.buildUpdateContainmentMessage(n, c, -1));
				}

				client.insertPacket(PacketFactory.buildBaselineCREO3(n));
				client.insertPacket(PacketFactory.buildBaselineCREO6(n));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(n));
				sceneEndBaseLinesSent = true;
				client.insertPacket(PacketFactory.buildSceneEndBaselines(n));
				client.insertPacket(PacketFactory
						.buildObjectControllerDataTransformObjectToClient(n,
								0x21));

			} else if (i instanceof Player) {
				if (i.getIsStaticObject()) {
					return;
				}
				Player n = (Player) i;
				if (n.getCellID() > 0) {
					c = (Cell) server.getObjectFromAllObjects(n.getCellID());
					c.addCellObject(n);
					client.insertPacket(PacketFactory
							.buildUpdateContainmentMessage(n, c, 4));
				}
				if (n.isMounted()) {
					SOEObject m = server.getObjectFromAllObjects(n
							.getCurrentMount());
					client.insertPacket(PacketFactory
							.buildUpdateContainmentMessage(n, m, 4));
				}
				client.insertPacket(PacketFactory.buildBaselineCREO1(n));
				client.insertPacket(PacketFactory.buildBaselineCREO3(n));
				client.insertPacket(PacketFactory.buildBaselineCREO4(n));
				client.insertPacket(PacketFactory.buildBaselineCREO6(n));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(n));
			} else if (i instanceof Camp) {
				if (i.getIsStaticObject()) {
					return;
				}
				Camp t = (Camp) i;
				// some items do not send the equipped state packet. Mission Bag
				// is one of them.
				if (t.bSendsEquipedState()) {
					client.insertPacket(PacketFactory.buildUpdateContainmentMessage(t, t.getContainer(),t.getEquippedStatus()));
				}
				client.insertPacket(PacketFactory.buildBaselineTANO3(t));
				client.insertPacket(PacketFactory.buildBaselineTANO6(t));
				client.insertPacket(PacketFactory.buildBaselineTANO8(t));
				client.insertPacket(PacketFactory.buildBaselineTANO9(t));
				//client.insertPacket(PacketFactory.buildUpdatePvPStatusMessage(t));
				client.insertPacket(PacketFactory.buildObjectControllerDataTransformObjectToClient(t,0x21));
				// if(t.getAdminTerminal()!=null)
				// {
				// Terminal aT = t.getAdminTerminal();
				// aT.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
				// this.addDelayedSpawnObject(aT,(System.currentTimeMillis() +
				// (1000 * 10)));
				// }

			} else if (i instanceof Factory) {
				Factory f = (Factory) i;
				client.insertPacket(PacketFactory.buildBaselineINSO3(f));
				client.insertPacket(PacketFactory.buildBaselineINSO6(f));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(f));
				sceneEndBaseLinesSent = true;
				client.insertPacket(PacketFactory.buildSceneEndBaselines(f));
				spawnItem(f.getInputHopper());
				spawnItem(f.getOutputHopper());
			} else if (i instanceof Structure) {
				s = (Structure) i;
				System.out.println("Spawning Structure name: " + s.getStructureName() + " to player " + getFirstName());
				System.out.println("Structure ID: " + Long.toHexString(s.getID()));

				if (s.getIsStaticObject()) {
					// if its a static object building we only send permissions
					// for the cells.
					// System.out.println("Sending Permissions Message for a Static Structure");
					// int cellcnt = s.getCellsInBuilding().size();
					// System.out.println("Cell Count " + cellcnt);
					Enumeration<Cell> cEnum = s.getCellsInBuilding().elements();
					while (cEnum.hasMoreElements()) {
						Cell cell = cEnum.nextElement();
						if (cell == null) {
							// System.out.println("NULL CELL IN STATIC BUILDING!!!!");
							DataLog.logEntry("Null Cell In static Building SID:"+ s.getID(), "Player",Constants.LOG_SEVERITY_CRITICAL,ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
						} else {
							if (client.getClientReadyStatus()) {
								if (DatabaseInterface.isRestricted(cell.getID())) {
									client.insertPacket(PacketFactory.buildUpdateCellPermissionMessage(cell, s, this));
								}
							} else {
								return;
							}
						}
					}
				} else {
					// if its a player structure we give it the whole spa
					// treatment
					if (s.getStructureType() == Constants.STRUCTURE_TYPE_BUILDING) {
						client.insertPacket(PacketFactory.buildBaselineBUIO3(s));
						client.insertPacket(PacketFactory.buildBaselineBUIO6(s));
						int cellcnt = s.getCellsInBuilding().size();
						for (int cid = 0; cid < cellcnt; cid++) {
							Cell cell = s.getCellByIndex(cid);
							if (cell == null) {
								DataLog.logEntry(
										"Null Cell In Player Building SID:"
												+ s.getID(), "Player",
										Constants.LOG_SEVERITY_CRITICAL,
										ZoneServer.ZoneRunOptions
												.isBLogToConsole(), true);
								// System.out.println("NULL CELL IN BUILDING!!!!");
							} else {
								spawnItem(cell);
							}
							
							// System.out.println("Spawning Cell ID:" +
							// cell.getID() + " Parent ID:" +
							// cell.getBuildingID()+ " Index:" +
							// cell.getCellNum());
							/*
							client.insertPacket(PacketFactory
									.buildSceneCreateObjectByCRC(cell, false));
							client
									.insertPacket(PacketFactory
											.buildUpdateContainmentMessage(
													cell, s, -1));
							client.insertPacket(PacketFactory
									.buildBaselineSCLT3(cell));
							client.insertPacket(PacketFactory
									.buildBaselineSCLT6(cell));
							client.insertPacket(PacketFactory
									.buildUpdateCellPermissionMessage(cell, s,
											this));
							client.insertPacket(PacketFactory
									.buildSceneEndBaselines(cell));*/
							//System.out.println("Add cell with ID " + cell.getID() + ", parent building name " + cell.getBuilding().getName() + " to list of all objects.");
							//server.addObjectToAllObjects(cell, false,
								//	false);
						}
						sceneEndBaseLinesSent = true;
						client.insertPacket(PacketFactory
								.buildSceneEndBaselines(s));
						
						Terminal guildTerminal = s.getGuildTerminal();
						TangibleItem structureBase = s.getStructureBase();
						Terminal structureSign = s.getStructureSign();
						Terminal adminTerminal = s.getAdminTerminal();
						Vector<Terminal> vElevatorTerminal = s.getVElevatorTerminals();
						spawnItem(structureBase);
						spawnItem(guildTerminal);
						spawnItem(structureSign);
						spawnItem(adminTerminal);
						if (!vElevatorTerminal.isEmpty()) {
							for (int j = 0; j < vElevatorTerminal.size(); j++) {
								spawnItem(vElevatorTerminal.elementAt(j));
							}
						}
/*
						if (s.getGuildTerminal() != null) {
							Terminal gT = s.getGuildTerminal();
							gT
									.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
							this.addDelayedSpawnObject(gT, (System
									.currentTimeMillis() + (1000 * 10)));
						}
						if (s.getStructureBase() != null) {
							TangibleItem bB = s.getStructureBase();
							bB
									.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
							this.addDelayedSpawnObject(bB, 1000 * 1);
						}
						if (s.getStructureSign() != null) {
							Terminal sT = s.getStructureSign();
							sT
									.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
							this.addDelayedSpawnObject(sT, (System
									.currentTimeMillis() + (1000 * 10)));
						}
						// System.out.println("Sign Spawned, Spawning terminal");
						if (s.getAdminTerminal() != null) {
							Terminal aT = s.getAdminTerminal();
							aT
									.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
							this.addDelayedSpawnObject(aT, (System
									.currentTimeMillis() + (1000 * 10)));
						}
						if (s.getVElevatorTerminals().size() >= 1) {
							Vector<Terminal> vE = s.getVElevatorTerminals();
							for (int e = 0; e < vE.size(); e++) {
								Terminal eT = vE.get(e);
								eT
										.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
								this.addDelayedSpawnObject(eT, (System
										.currentTimeMillis() + (1000 * 10)));
							}
						}
						*/
					} else if (s.getStructureType() == Constants.STRUCTURE_TYPE_INSTALLATION) {
						client
								.insertPacket(PacketFactory
										.buildBaselineINSO3(s));
						client
								.insertPacket(PacketFactory
										.buildBaselineINSO6(s));
						client.insertPacket(PacketFactory
								.buildUpdatePvPStatusMessage(s));
						sceneEndBaseLinesSent = true;
						client.insertPacket(PacketFactory
								.buildSceneEndBaselines(s));
					} else if (s instanceof Harvester) {
						Harvester h = (Harvester) s;
						client
								.insertPacket(PacketFactory
										.buildBaselineHINO3(h));
						client
								.insertPacket(PacketFactory
										.buildBaselineHINO6(h));
						client.insertPacket(PacketFactory
								.buildUpdatePvPStatusMessage(h));
						sceneEndBaseLinesSent = true;
						client.insertPacket(PacketFactory
								.buildSceneEndBaselines(s));
						client.insertPacket(PacketFactory.buildDeltasMessage(
								Constants.BASELINES_HINO, (byte) 3, (short) 1,
								(short) 0x06, s, s.getIAnimationBitmask()));
					} else if (s.getStructureType() == Constants.STRUCTURE_TYPE_TUTORIAL) {
						System.out.println("Spawning Tutorial Building.");
						client.insertPacket(PacketFactory.buildBaselineBUIO3(s));
						client.insertPacket(PacketFactory.buildBaselineBUIO6(s));
						TutorialObject to = (TutorialObject) s;
						Enumeration<Cell> vCellsInBuilding = to.getCellsInBuilding().elements();
						while (vCellsInBuilding.hasMoreElements()) {
							Cell cell = vCellsInBuilding.nextElement();
							if (cell == null) {
								DataLog.logEntry(
										"Null Cell In Tutorial Building SID:"
												+ s.getID(), "Player",
										Constants.LOG_SEVERITY_CRITICAL,
										ZoneServer.ZoneRunOptions
												.isBLogToConsole(), true);
								// System.out.println("NULL CELL IN BUILDING!!!!");
							} else {
								System.out.println("Spawn building cell index " + cell.getCellNum());
								spawnItem(cell);
							}
							// System.out.println("Spawning Cell ID:" +
							// cell.getID() + " Parent ID:" +
							// cell.getBuildingID()+ " Index:" +
							// cell.getCellNum());
							/*
							client.insertPacket(PacketFactory
									.buildSceneCreateObjectByCRC(cell, false));
							client
									.insertPacket(PacketFactory
											.buildUpdateContainmentMessage(
													cell, s, -1));
							client.insertPacket(PacketFactory
									.buildBaselineSCLT3(cell));
							client.insertPacket(PacketFactory
									.buildBaselineSCLT6(cell));
							client.insertPacket(PacketFactory
									.buildUpdateCellPermissionMessage(cell, s,
											this));
							client.insertPacket(PacketFactory
									.buildSceneEndBaselines(cell));*/
							//server.addObjectToAllObjects(cell, false,
									//false);
						}
						sceneEndBaseLinesSent = true;
						client.insertPacket(PacketFactory
								.buildSceneEndBaselines(s));
						//System.out.println("BYPASS spawn tutorial objects.");
						to.spawnTutorialObjects(this);
						// Terminal tt = to.getTutorialTravelTerminal();
						// tt.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_RAW_SPAWN);
						// this.addDelayedSpawnObject(tt,
						// System.currentTimeMillis() + 1000);
					}

					client.insertPacket(PacketFactory
							.buildObjectControllerDataTransformObjectToClient(
									s, 0x21));
				}
			} else if (i instanceof PlayerItem) {
				if (i.getIsStaticObject()) {
					return;
				}
				PlayerItem p = (PlayerItem) i;
				client.insertPacket(PacketFactory
						.buildUpdateContainmentMessage(p, this, 4));
				client.insertPacket(PacketFactory.buildBaselinePLAY3(p));
				client.insertPacket(PacketFactory.buildBaselinePLAY6(p));
				client.insertPacket(PacketFactory.buildBaselinePLAY8(p));
				client.insertPacket(PacketFactory.buildBaselinePLAY9(p));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(p));

			} else if (i instanceof ResourceContainer) {
				if (i.getIsStaticObject()) {
					return;
				}
				ResourceContainer r = (ResourceContainer) i;
				// Resources cannot be equipped on a Player... and in fact, it'd
				// look rather silly if they were.
				// However, they are contained by whatever cell (or inventory)
				// they are in.
				client.insertPacket(PacketFactory
						.buildUpdateContainmentMessage(r, r.getContainer(), r
								.getEquippedStatus()));
				client.insertPacket(PacketFactory.buildBaselineRCNO3(r));
				client.insertPacket(PacketFactory.buildBaselineRCNO6(r));
				// client.insertPacket(PacketFactory.buildBaselineRCNO8(r));
				// client.insertPacket(PacketFactory.buildBaselineRCNO9(r));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(r));

			} else if (i instanceof Deed) {
				if (i.getIsStaticObject()) {
					return;
				}
				Deed d = (Deed) i; // some items do not send the equipped state
									// packet. Mission Bag is one of them.

				if (d.bSendsEquipedState()) {
					client.insertPacket(PacketFactory
							.buildUpdateContainmentMessage(d, d.getContainer(),
									-1));
				}
				client.insertPacket(PacketFactory.buildBaselineTANO3(d));
				client.insertPacket(PacketFactory.buildBaselineTANO6(d));
				client.insertPacket(PacketFactory.buildBaselineTANO8(d));
				client.insertPacket(PacketFactory.buildBaselineTANO9(d));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(d));

			} else if (i instanceof Instrument) {
				if (i.getIsStaticObject()) {
					return;
				}
				TangibleItem t = (TangibleItem) i;
				// some items do not send the equipped state packet. Mission Bag
				// is one of them.
				if (t.bSendsEquipedState()) {
					client.insertPacket(PacketFactory
							.buildUpdateContainmentMessage(t, t.getContainer(),
									t.getEquippedStatus()));
				}
				client.insertPacket(PacketFactory.buildBaselineTANO3(t));
				client.insertPacket(PacketFactory.buildBaselineTANO6(t));
				client.insertPacket(PacketFactory.buildBaselineTANO8(t));
				client.insertPacket(PacketFactory.buildBaselineTANO9(t));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(t));

			} else if (i instanceof Weapon) {
				if (i.getIsStaticObject()) {
					return;
				}
				Weapon w = (Weapon) i;
				client.insertPacket(PacketFactory
						.buildUpdateContainmentMessage(w, w.getContainer(), w
								.getEquippedStatus()));
				/*
				 * if (equippedWeapon != null) { if (w.equals(equippedWeapon)) {
				 * client
				 * .insertPacket(PacketFactory.buildUpdateContainmentMessage(w,
				 * this, 4)); } else {
				 * client.insertPacket(PacketFactory.buildUpdateContainmentMessage
				 * (w, tPlayerInventory, -1)); } } else {
				 * client.insertPacket(PacketFactory
				 * .buildUpdateContainmentMessage(w, tPlayerInventory, -1)); }
				 */
				client.insertPacket(PacketFactory.buildBaselineWEAO3(w));
				client.insertPacket(PacketFactory.buildBaselineWEAO6(w));
				client.insertPacket(PacketFactory.buildBaselineWEAO8(w));
				client.insertPacket(PacketFactory.buildBaselineWEAO9(w));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(w));
			} else if (i instanceof StaticItem) {
				// System.out.println("Spawning Static Item");
				if (i.getIsStaticObject()) {
					return;
				}
				StaticItem si = (StaticItem) i;
				client.insertPacket(PacketFactory.buildBaselineSTAO3(si));
				client.insertPacket(PacketFactory.buildBaselineSTAO6(si));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(si));
				client.insertPacket(PacketFactory
						.buildObjectControllerDataTransformObjectToClient(si,
								0x21));

			} else if (i instanceof TangibleItem) {
				if (i.getIsStaticObject()) {
					return;
				}
				TangibleItem t = (TangibleItem) i;
				// int templateID = t.getTemplateID();
				if (t.bSendsEquipedState()) {
					client.insertPacket(PacketFactory
							.buildUpdateContainmentMessage(t, t.getContainer(),
									t.getEquippedStatus()));
				}

				client.insertPacket(PacketFactory.buildBaselineTANO3(t));
				client.insertPacket(PacketFactory.buildBaselineTANO6(t));
				client.insertPacket(PacketFactory.buildBaselineTANO8(t));
				client.insertPacket(PacketFactory.buildBaselineTANO9(t));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(t));

			} else if (i instanceof MissionObject) {
				if (i.getIsStaticObject()) {
					return;
				}
				MissionObject m = (MissionObject) i;
				// System.out.println("Sending Mission Object Baselines " +
				// m.getID());
				client.insertPacket(PacketFactory
						.buildUpdateContainmentMessage(m, tMissionBag, -1));
				client.insertPacket(PacketFactory.buildBaselineMISO3(m));
				client.insertPacket(PacketFactory.buildBaselineMISO6(m));
				client.insertPacket(PacketFactory.buildBaselineMISO8(m));
				client.insertPacket(PacketFactory.buildBaselineMISO9(m));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(m));
			} else if (i instanceof ManufacturingSchematic) {
				ManufacturingSchematic schematic = (ManufacturingSchematic) i;
				// Works!
				client
						.insertPacket(PacketFactory
								.buildUpdateContainmentMessage(schematic,
										schematic.getContainer(), schematic
												.getEquippedStatus()));
				client.insertPacket(PacketFactory.buildBaselineMSCO3(schematic,
						this));
				client
						.insertPacket(PacketFactory
								.buildBaselineMSCO6(schematic));
				client
						.insertPacket(PacketFactory
								.buildBaselineMSCO8(schematic));
				client
						.insertPacket(PacketFactory
								.buildBaselineMSCO9(schematic));
			} else if (i instanceof IntangibleObject) {
				if (i.getIsStaticObject()) {
					return;
				}
				// We can't carry intangible items.
				IntangibleObject o = (IntangibleObject) i;
				client.insertPacket(PacketFactory
						.buildUpdateContainmentMessage(o, tDatapad, -1));
				client.insertPacket(PacketFactory.buildBaselineITNO3(o));
				client.insertPacket(PacketFactory.buildBaselineITNO6(o));
				client.insertPacket(PacketFactory.buildBaselineITNO8(o));
				client.insertPacket(PacketFactory.buildBaselineITNO9(o));
				client.insertPacket(PacketFactory
						.buildUpdatePvPStatusMessage(o));
			} else if (i instanceof Cell) {
				if (i.getIsStaticObject()) {
					return;
				}
				Cell cell = (Cell) i;
				// System.out.println("Spawning Cell: " + cell.getID() +
				// " Index:" + cell.getCellNum());
				s = (Structure) cell.getBuilding();
				client.insertPacket(PacketFactory.buildUpdateContainmentMessage(cell, s, -1));
				client.insertPacket(PacketFactory.buildBaselineSCLT3(cell));
				client.insertPacket(PacketFactory.buildBaselineSCLT6(cell));
				client.insertPacket(PacketFactory.buildUpdateCellPermissionMessage(cell, s, this));
			} else if (i instanceof Group) {
				if (i.getIsStaticObject()) {
					return;
				}
				// System.out.println("Spawning a Group Object to: " +
				// this.getFullName());
				Group g = (Group) i;
				client.insertPacket(PacketFactory.buildBaselineGRUP3(g));
				client.insertPacket(PacketFactory.buildBaselineGRUP6(g, this));
			}
		} catch (Exception e) {
			System.out.println("Error spawning item with ID " + i.getID() + ": " + e.toString());
			e.printStackTrace();
			
		}
		if (!sceneEndBaseLinesSent) {
			if (i.getIsStaticObject()) {
				return;
			}
			client.insertPacket(PacketFactory.buildSceneEndBaselines(i));
		}
		
		vAllSpawnedObjectIDs.add(itemID);
	}

	/**
	 * Sets the Player's biography data.
	 * 
	 * @param bio
	 *            -- The new Biography data.
	 */
	protected void setBiography(String bio) {
		sBiography = bio;
	}

	/**
	 * Gets the Players biography data.
	 * 
	 * @return The biography.
	 */
	protected String getBiography() {
		return sBiography;
	}

	/**
	 * Sets the Player's First Name.
	 * 
	 * @param name
	 *            -- The new First Name.
	 */
	protected void setFirstName(String name) {
		sFirstName = name;
	}

	/**
	 * Sets the Player's Last Name.
	 * 
	 * @param name
	 *            -- The new Last Name.
	 */
	protected void setLastName(String name) {
		sLastName = name;
	}

	/**
	 * Sets the Player's Race ID.
	 * 
	 * @param i
	 *            -- The Race ID.
	 */
	protected void setRace(int i) {
		iRaceID = i;
	}

	/**
	 * Add a single item to this Player's Inventory.
	 * 
	 * @param t
	 *            -- The item to add to Inventory.
	 */
	protected void addItemToInventory(TangibleItem t) {
		vInventoryItemsList.add(t);
		// tPlayerInventory.addLinkedObject(t);

	}

	protected void removeItemFromInventory(TangibleItem t) {
		vInventoryItemsList.remove(t);
		// tPlayerInventory.removeLinkedObject(t);
	}

	/**
	 * Adds a list of items to this Player's Inventory.
	 * 
	 * @param v
	 *            -- THe items to add to Inventory.
	 */
	protected void addInventoryItem(Vector<TangibleItem> v) {
		vInventoryItemsList.addAll(v);
	}

	/**
	 * Gets a list of all items currently equipped by the Player.
	 * 
	 * @return The list of equipped items.
	 */
	protected Vector<TangibleItem> getEquippedItems() {
		Vector<TangibleItem> toReturn = new Vector<TangibleItem>();
		for (int i = 0; i < vInventoryItemsList.size(); i++) {
			TangibleItem t = vInventoryItemsList.elementAt(i);
			if (t.getContainerID() == getID()) {
				toReturn.add(t);
			}
		}
		toReturn.add(tHairObject);
		return toReturn;
	}

	/**
	 * Sets the Player's scale.
	 * 
	 * @param f
	 *            -- The new scale.
	 * @param bUpdateZone
	 * 			  -- Indicates if a DeltasMessage is to be returned or not.
	 * @return -- The DeltasMessage, or null if bUpdateZone is false.
	 * @throws IOException if an error occured building the packet
	 * @throws IndexOutOfBoundsException if the scale is outside the range.
	 */
	protected byte[] setScale(float f, boolean bUpdateZone, boolean bOverride) throws IOException, IndexOutOfBoundsException {
		if (Constants.getIsValidScale(this, f) || bOverride) {
			float oldScale = fScale;
			fScale = f;
			if (fScale != oldScale) {
				if (bUpdateZone) {
					return PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)14, this, fScale);
				} else {
					return null;
				}
			}
			return null;
		} else {
			throw new IndexOutOfBoundsException("Error: Invalid scale factor " + f + " for species " + Constants.SpeciesNames[getRaceID()]);
		}
	}
	
	protected void setIsAFK(boolean status) {
		if (status) {
			thePlayer.addBitToStatusBitmask(Constants.PLAYER_STATUS_AFK);
		} else {
			thePlayer.removeBitFromStatusBitmask(Constants.PLAYER_STATUS_AFK);
		}
	}

	protected boolean getIsAFK() {
		return ((thePlayer.getStatusBitmask() & Constants.PLAYER_STATUS_AFK) != 0);
	}

	/**
	 * Set the total number of Lots this Player has.
	 * 
	 * @param i
	 *            -- The number of lots.
	 */
	protected void setTotalLots(int i) {
		iTotalLots = i;
	}

	/**
	 * Gets the total number of Lots this Player has.
	 * 
	 * @return -- The number of lots
	 */
	protected int getTotalLots() {
		return iTotalLots;
	}

	/**
	 * Gets the number of empty Lots this Player has.
	 * 
	 * @return -- The number of free lots.
	 */
	protected int getFreeLots() {
		if (vPlayerStructures == null) {
			vPlayerStructures = new ConcurrentHashMap<Long, Structure>();
		}
		iUsedLots = 0;
		Enumeration<Structure> sEnum = vPlayerStructures.elements();
		while (sEnum.hasMoreElements()) {
			Structure s = sEnum.nextElement();
			iUsedLots += s.getLotsize();
		}
		return iTotalLots - iUsedLots;
	}

	/**
	 * Sets the HAM wounds array for this Player.
	 * 
	 * @param i
	 *            -- the new Ham Wounds.
	 */
	protected void setHamWounds(int[] i) {
		iHamWounds = i;
	}

	/**
	 * Sets the current credits in the Player's Inventory.
	 * 
	 * @param iCredits
	 *            -- The new credits value in the Player's Inventory.
	 */
	protected void setInventoryCredits(int iCredits) {
		iInventoryCredits = iCredits;
	}

	/**
	 * Sets the current credits in the Player's Bank.
	 * 
	 * @param iCredits
	 *            -- The new credits value in the Player's Bank.
	 */
	protected void setBankCredits(int iCredits) {
		iBankCredits = iCredits;
	}

	/**
	 * Attaches a TangibleItem as a Hair object to this Player.
	 * 
	 * @param hair
	 *            -- The Hair.
	 */
	protected void addHair(TangibleItem hair) {
		tHairObject = hair;
		// addTangibleItem(hair);
	}

	/**
	 * Returns the Player's Hair object.
	 * 
	 * @return The hair object.
	 */
	protected TangibleItem getHair() {
		return tHairObject;
	}

	/**
	 * Sets the Player's original starting profession, by name.
	 * 
	 * @param s
	 *            -- The starting profession.
	 */
	protected void setStartingProfession(String s) {
		sStartingProfession = s;
	}

	/**
	 * Returns the name of the Player's original starting profession.
	 * 
	 * @return The starting profession.
	 */
	protected String getStartingProfession() {
		return sStartingProfession;
	}

	/**
	 * Gets a Waypoint by ID.
	 * 
	 * @param ID
	 *            -- The Object ID of the Waypoint.
	 * @return The Waypoint associated with the given ID, or null if no such
	 *         waypoint exists.
	 */
	protected Waypoint getWaypoint(long ID) {
		return thePlayer.getWaypoint(ID);
	}

	/**
	 * Gets all of the Player's waypoints.
	 * 
	 * @return The list of waypoints.
	 */
	protected Vector<Waypoint> getWaypoints() {
		return thePlayer.getWaypoints();
	}

	/**
	 * Increments and then gets the update counter for the Player's skill
	 * modifications list.
	 * 
	 * @return
	 */
	public int getSkillModsUpdateCounter(boolean bIncrement) {
		if (bIncrement) {
			iSkillModsUpdateCount++;
		}
		return iSkillModsUpdateCount;
	}

	public void setSkillModsUpdateCounter(int counter) {
		iSkillModsUpdateCount = counter;
	}

	/**
	 * Sets the Player's Friends list.
	 * 
	 * @param vFriendsList
	 *            -- The friends list.
	 */
	public void setFriendsList(Vector<PlayerFriends> vFriendsList) {
		thePlayer.setFriendsList(vFriendsList);
	}

	/**
	 * Returns the PlayerItem associated with this Player. The PlayerItem
	 * contains information on Waypoint data, Experience, Skills, Skill Mods,
	 * Quests, Jedi settings, etc.
	 * 
	 * @return -- The PlayerItem.
	 */
	protected PlayerItem getPlayData() {
		return thePlayer;
	}

	/**
	 * Sets the PlayerItem associated with this Player. The PlayerItem contains
	 * information on Waypoint data, Experience, Skills, Skill Mods, Quests,
	 * Jedi settings, etc.
	 * 
	 * @param p
	 *            -- The new PlayerItem.
	 */
	protected void setPlayData(PlayerItem p) {
		thePlayer = p;
	}

	/**
	 * Sets the Inventory Container for this Player.
	 * 
	 * @param t
	 *            -- The Inventory.
	 */
	protected void setInventoryItem(TangibleItem t) {
		tPlayerInventory = t;
	}

	/**
	 * Gets the Inventory Container for this Player.
	 * 
	 * @return The Inventory.
	 */
	protected TangibleItem getInventory() {
		return tPlayerInventory;
	}

	/**
	 * Sets the Datapad item for this Player.
	 * 
	 * @param t
	 *            -- The Datapad.
	 */
	protected void setDatapad(TangibleItem t) {
		tDatapad = t;
	}

	/**
	 * Gets the Datapad item for this Player.
	 * 
	 * @return The Datapad.
	 */
	protected TangibleItem getDatapad() {
		return tDatapad;
	}

	/**
	 * Sets the Bank item for this Player.
	 * 
	 * @param t
	 *            -- The Bank.
	 */
	protected void setBank(TangibleItem t) {
		tBank = t;
	}

	/**
	 * Gets the Bank item for this Player.
	 * 
	 * @return The Bank.
	 */
	protected TangibleItem getBank() {
		return tBank;
	}

	/**
	 * Sets the Mission Container for this Player.
	 * 
	 * @param t
	 *            -- The Mission Container.
	 */
	protected void setMissionBag(TangibleItem t) {
		tMissionBag = t;
	}

	/**
	 * Gets the Mission Container for this Player.
	 * 
	 * @return -- The Mission Container.
	 */
	protected TangibleItem getMissionBag() {
		return tMissionBag;
	}

	protected void resetCreo1UpdateVars() {
	}

	protected void resetCreo3UpdateVars() {
	}

	protected void resetCreo4UpdateVars() {
	}

	protected void resetCreo6UpdateVars() {
	}

	protected void resetPlay3UpdateVars() {
	}

	protected void resetPlay6UpdateVars() {
	}

	protected void resetPlay8UpdateVars() {
	}

	protected void resetPlay9UpdateVars() {
	}

	protected void useItem(ZoneClient client) {
		try {
			// client.insertPacket(PacketFactory.buildChatSystemMessage("You can't use another player!"));
		} catch (Exception e) {
			System.out.println("Error building packet: " + e.toString());
			e.printStackTrace();
		}
	}

	protected float[] getStartingCoordinates() {
		return fStartingCoordinates;
	}

	protected void setStartingCoordinates(float x, float y) {
		fStartingCoordinates[0] = x;
		fStartingCoordinates[2] = y;
	}

	protected float[] getHouseCoordinates() {
		return fHouseCoordinates;
	}

	protected void setHouseCoordinates(float x, float z, float y) {
		fHouseCoordinates[0] = x;
		fHouseCoordinates[1] = z;
		fHouseCoordinates[2] = y;
	}

	protected float[] getBankCoordinates() {
		return fBankCoordinates;
	}

	protected void setBankCoordinates(float x, float y) {
		fBankCoordinates[0] = x;
		fBankCoordinates[2] = y;
	}

	protected void setBankPlanetID(int i) {
		if (i >= 0 && i < Constants.PlanetNames.length) {
			iBankPlanetID = i;
		} else {
			iBankPlanetID = -1;
		}
	}

	protected int getBankPlanetID() {
		return iBankPlanetID;
	}

	protected void setHousePlanetID(int i) {
		if (i >= 0 && i < Constants.PlanetNames.length) {
			iHomePlanetID = i;
		} else {
			iHomePlanetID = -1;
		}
	}

	protected int getHousePlanetID() {
		return iHomePlanetID;
	}

	protected void setHamMigrationTarget(int index, int value) {
		iHamMigrationTarget[index] = value;
	}

	protected int[] getHamMigrationTargets() {
		return iHamMigrationTarget;
	}

	protected void setHamMigrationPointsAvailable(int iPoints) {
		iHamMigrationPoints = iPoints;
	}

	protected int getHamMigrationPointsAvailable() {
		return iHamMigrationPoints;
	}

	protected void setRebelFactionPoints(int iFaction) {
		iRebelFactionPoints = iFaction;
	}

	protected int getRebelFactionPoints() {
		return iRebelFactionPoints;
	}

	protected void setImperialFactionPoints(int iFaction) {
		iImperialFactionPoints = iFaction;
	}

	protected int getImperialFactionPoints() {
		return iImperialFactionPoints;
	}

	protected void addFactionToFactionList(String sFaction) {
		vFactionList.add(new PlayerFactions(sFaction, 0));
	}

	protected void addFactionToFactionList(String sFaction, float newValue) {
		vFactionList.add(new PlayerFactions(sFaction, newValue));
	}

	protected void updateFaction(String sFactionName, float newValue) {
		for (int i = 0; i < vFactionList.size(); i++) {
			PlayerFactions faction = vFactionList.elementAt(i);
			if (faction.getFactionName().equals(sFactionName)) {
				faction.setFactionValue(newValue);
				return;
			}
		}
		addFactionToFactionList(sFactionName, newValue);
	}

	protected Vector<PlayerFactions> getFactionList() {
		return vFactionList;
	}

	protected byte[] clearAllStates(boolean bUpdateZone) {
		lState = 0;
		if (bUpdateZone) {
			try {
				return (PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 3, (short) 1,
						(short) 0x10, this, lState));
			} catch (Exception e) {
				// D'oh!
				return null;
			}
		} else {
			return null;
		}
	}

	protected void setCREO3Bitmask(int value) {
		iUnknownCREO3Bitmask = value;
	}

	protected int getCREO3Bitmask() {
		return iUnknownCREO3Bitmask;
	}

	/**
	 * Sets the last used travel terminal.
	 * 
	 * @param t
	 *            - Terminal
	 */
	protected void setLastUsedTravelTerminal(Terminal t) {
		LastUsedTravelTerminal = t;
	}

	/**
	 * Returns the last used travel terminal.
	 * 
	 * @return Terminal
	 */
	protected Terminal getLastUsedTravelTerminal() {
		return LastUsedTravelTerminal;
	}

	/**
	 * Returns all cash on hand by player a sum of Inventory + Bank
	 * 
	 * @return int
	 */
	protected int getCashOnHand() {
		int retval = iInventoryCredits + iBankCredits;
		// System.out.println("Inventory: " + iInventoryCredits);
		// System.out.println("Bank: " + iBankCredits);
		// System.out.println("CashOnHand: " + retval);
		return retval;
	}

	/**
	 * debitCredits() Debits the amount of credits from player. Will try to
	 * debit from bank first. Returns True if success. If not enough in bank it
	 * will try inventory. Returns True if success. If inventory is not enough
	 * it will add bank + inventory. Returns True if success. If Still not
	 * enough it will return false.
	 * 
	 * @param amount
	 *            int
	 * @return boolean - true = success false = failure not enough cash.
	 */
	protected boolean debitCredits(int amount) {
		int TotalCredits = iInventoryCredits + iBankCredits;
		boolean retval = false;
		if (iBankCredits >= amount) {
			iBankCredits = iBankCredits - amount;
			retval = true;
		} else if (iInventoryCredits >= amount) {
			iInventoryCredits = iInventoryCredits - amount;
			retval = true;
		} else if (TotalCredits >= amount) {
			TotalCredits = TotalCredits - amount;
			iBankCredits = 0;
			iInventoryCredits = TotalCredits;
			retval = true;
		}
		if (retval) {
			// update client on credits left.
			updateCredits();
		}
		return retval;
	}

	protected boolean debitInventoryCredits(int amount) {
		boolean retval = false;
		if (iInventoryCredits >= amount) {
			iInventoryCredits -= amount;
			retval = true;
			updateCredits();
		}
		return retval;
	}

	protected boolean debitBankCredits(int ammount) {
		boolean retval = false;
		if (iInventoryCredits >= ammount) {
			iInventoryCredits -= ammount;
			retval = true;
			updateCredits();
		}
		return retval;
	}

	/**
	 * Add indicated amount to bank
	 * 
	 * @param amount
	 *            int
	 */
	protected void creditBankCredits(int amount) {
		iBankCredits = iBankCredits + amount;
		updateCredits();
	}

	/**
	 * Add indicated amount to inventory
	 * 
	 * @param amount
	 *            int
	 */
	protected void creditInventoryCredits(int amount) {
		iInventoryCredits = iInventoryCredits + amount;
		updateCredits();
	}

	protected void updateCredits() {
		try {
			if (this.getOnlineStatus()) {
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 1, (short) 1,
						(short) 0, this, iBankCredits));
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 1, (short) 1,
						(short) 1, this, iInventoryCredits));
			}

		} catch (Exception e) {
			DataLog.logEntry("Eception Caught in Player.updateCredits() " + e,
					"Player", Constants.LOG_SEVERITY_CRITICAL,
					ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
			// System.out.println("Eception Caught in Player.updateCredits() " +
			// e);
			e.printStackTrace();
		}

	}

	public int getLastSUIBox() {
		// System.out.println("Returning Last SUI Box Value: " +
		// this.LastSUIBox);
		incrementLastSUIBox();
		return this.LastSUIBox;
	}

	public int getCurrentSUIBoxID() {
		return LastSUIBox;
	}

	private void incrementLastSUIBox() {
		// System.out.println("LastSUIBox Incrementing Value Before: " +
		// this.LastSUIBox);
		if (this.LastSUIBox == -1) {
			this.LastSUIBox = 0;
		}
		this.LastSUIBox = this.LastSUIBox + 1;

		// System.out.println("LastSUIBox Incrementing Value After: " +
		// this.LastSUIBox);
	}

	protected void clearSpawnedItems() {
		if (vAllSpawnedObjectIDs != null) {
			vAllSpawnedObjectIDs.removeAllElements();
		}
	}

	protected void setLastSuiWindowTypeString(String Type) {
		sLastSuiWindowType = Type;
	}

	protected String getLastSuiWindowTypeString() {
		return sLastSuiWindowType;
	}

	protected void setLastSuiWindowTypeInt(int t) {
		iLastSuiWindowType = t;
	}

	protected int getLastSuiWindowTypeInt() {
		return iLastSuiWindowType;
	}

	/**
	 * Adds one object that can be selected when using an SUI Window with a list
	 * of objects to select from.
	 * 
	 * @param i
	 * @param o
	 */
	protected void addSUIListWindowObjectList(int i, SOEObject o) {
		if (SUIListWindowObjectList != null) {
			SUIListWindowObjectList.put(i, o);
		} else {
			SUIListWindowObjectList = new Hashtable<Integer, SOEObject>();
			SUIListWindowObjectList.put(i, o);
		}

	}

	/**
	 * clears the sui list window selection contents and variables
	 */
	protected void clearSUIListWindowObjectList() {
		if (SUIListWindowObjectList != null) {
			SUIListWindowObjectList.clear();
		}
		this.iLastSuiWindowType = -1;
		this.sLastSuiWindowType = "";
	}

	/**
	 * Returns one item from the sui list window objects list.
	 * 
	 * @param item
	 * @return
	 */
	protected SOEObject getSUIListWindowObjectListItem(int item) {
		return SUIListWindowObjectList.get(item);
	}

	protected void playerTravel(float x, float y, float z, int planetID) {
		// here is where we make the player travel and delete the ticket from
		// the players inventory.
		// We will ALWAYS be exiting a cell when this occurs.
		setCellID(0);
		try {

			// TravelDestination ArrivalDestination = T.getArrivalInformation();
			int oldPlanetID = getPlanetID();
			int newPlanetID = planetID;
			if (oldPlanetID == newPlanetID) {
				float newX = x;
				float newY = y;
				this.setZ(z);
				server.moveObjectInTree(this, newX, newY,
						oldPlanetID, newPlanetID);
				this.clearSpawnedItems();
				bIsTraveling = true;
				this.spawnPlayer();

			} else {
				// for now travel is only to the current server.
				// if travel is to be between planets we need to save the
				// player, serialize it and send it to the new
				// planet server for processing and spawn the player there.
				float newX = (x);
				float newY = (y);
				this.setZ(z);
				server.moveObjectInTree(this, newX, newY,
						oldPlanetID, newPlanetID);
				this.clearSpawnedItems();
				bIsTraveling = true;
				this.spawnPlayer();
			}
		} catch (Exception e) {
			DataLog.logEntry("Eception Caught in playerTravel " + e, "Player",
					Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions
							.isBLogToConsole(), true);
			// System.out.println("Eception Caught in playerTravel " + e);
			e.printStackTrace();
		}
	}

	protected void playerTravel(TravelTicket T) {
		// here is where we make the player travel and delete the ticket from
		// the players inventory.
		try {
			// We will always travel to the out of doors. No matter what.
			setCellID(0);
			TravelDestination ArrivalDestination = T.getArrivalInformation();
			int oldPlanetID = getPlanetID();
			int newPlanetID = ArrivalDestination.getDestinationPlanet();
			if (this.getPlanetID() == ArrivalDestination.getDestinationPlanet()) {
				this.despawnItem(T);
				this.getInventoryItems().remove(T);
				// this.setPlanetID(ArrivalDestination.getDestinationPlanet());
				// -- Done by the moveObjectInTree function call.
				float newX = (ArrivalDestination.getX());
				float newY = (ArrivalDestination.getY());
				this.setZ(ArrivalDestination.getZ());
				server.moveObjectInTree(this, newX, newY,
						oldPlanetID, newPlanetID);
				this.clearSpawnedItems();
				bIsTraveling = true;
				this.spawnPlayer();

			} else {
				// for now travel is only to the current server.
				// if travel is to be between planets we need to save the
				// player, serialize it and send it to the new
				// planet server for processing and spawn the player there.
				this.despawnItem(T);
				this.getInventoryItems().remove(T);
				// this.setPlanetID(ArrivalDestination.getDestinationPlanet());
				// // Done by the move object in tree function call.
				float newX = (ArrivalDestination.getX());
				float newY = (ArrivalDestination.getY());
				this.setZ(ArrivalDestination.getZ());
				server.moveObjectInTree(this, newX, newY,
						oldPlanetID, newPlanetID);
				this.clearSpawnedItems();
				bIsTraveling = true;
				this.spawnPlayer();
			}
		} catch (Exception e) {
			DataLog.logEntry("Eception Caught in playerTravel " + e, "Player",
					Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions
							.isBLogToConsole(), true);
			// System.out.println("Eception Caught in playerTravel " + e);
			e.printStackTrace();
		}
	}

	protected void playerTravel(TravelDestination T) {

		try {

			int oldPlanetID = getPlanetID();
			int newPlanetID = T.getDestinationPlanet();
			if (this.getPlanetID() == T.getDestinationPlanet()) {
				// this.setPlanetID(ArrivalDestination.getDestinationPlanet());
				// -- Done by the moveObjectInTree function call.
				float newX = (T.getX());
				float newY = (T.getY());
				this.setZ(T.getZ());
				server.moveObjectInTree(this, newX, newY,
						oldPlanetID, newPlanetID);
				this.clearSpawnedItems();
				bIsTraveling = true;
				this.spawnPlayer();
			} else {
				/**
				 * @todo for now travel is only to the current server. if travel
				 *       is to be between planets we need to save the player,
				 *       serialize it and send it to the new planet server for
				 *       processing and spawn the player there.
				 * */
				float newX = (T.getX());
				float newY = (T.getY());
				this.setZ(T.getZ());
				server.moveObjectInTree(this, newX, newY,
						oldPlanetID, newPlanetID);
				this.clearSpawnedItems();
				bIsTraveling = true;
				this.spawnPlayer();
			}

		} catch (Exception e) {
			DataLog.logEntry("Eception Caught in playerTravel " + e, "Player",
					Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions
							.isBLogToConsole(), true);
			// System.out.println("Eception Caught in playerTravel " + e);
			e.printStackTrace();
		}
	}

	protected void playerTravelToCell(TravelDestination T) {

		try {
			int oldPlanetID = getPlanetID();
			int newPlanetID = T.getDestinationPlanet();
			if (this.getPlanetID() == T.getDestinationPlanet()) {
				this.setPlanetID(T.getDestinationPlanet());
				float newX = T.getX();
				float newY = T.getY();
				this.setZ(T.getZ());
				this.setCellID(T.getCellID());
				server.moveObjectInTree(this, newX, newY,
						oldPlanetID, newPlanetID);
				this.clearSpawnedItems();
				bIsTraveling = true;
				this.spawnPlayer();
			} else {
				// for now travel is only to the current planet.
				// if travel is to be between planets we need to save the
				// player, serialize it and send it to the new
				// planet server for processing and spawn the player there.
				this.setPlanetID(T.getDestinationPlanet());
				float newX = T.getX();
				float newY = T.getY();
				this.setZ(T.getZ());
				server.moveObjectInTree(this, newX, newY,
						oldPlanetID, newPlanetID);
				this.setCellID(T.getCellID());
				this.clearSpawnedItems();
				bIsTraveling = true;
				this.spawnPlayer();
			}

		} catch (Exception e) {
			DataLog.logEntry("Eception Caught in playerTravel " + e, "Player",
					Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions
							.isBLogToConsole(), true);
			// System.out.println("Eception Caught in playerTravel " + e);
			e.printStackTrace();
		}
	}

	protected void playerWarp(float x, float y, float z, long cellid,
			int planetid) {

		// here is where we make the player warp
		// this.isWarping = true;
		try {
			Vector<Player> vPL = server.getPlayersAroundObject(this, false);
			for (int i = 0; i < vPL.size(); i++) {
				Player p = vPL.get(i);
				p.despawnItem(this);
			}
			if (this.getPlanetID() == planetid) {
				this.setPlanetID(planetid);
				this.setX(x);
				this.setY(y);
				this.setZ(z);
				this.setCellID(cellid);
				this.clearSpawnedItems();
				// bIsTraveling = true;
				// this.spawnPlayer();
				// spawnItem(this);
				if (cellid == 0) {
					this
							.getClient()
							.insertPacket(
									PacketFactory
											.buildObjectControllerPlayerDataTransformToClient(
													this, 0x0B));
				} else {
					this
							.getClient()
							.insertPacket(
									PacketFactory
											.buildObjectControllerPlayerDataTransformWithParentToClient(
													this, 0x0B));
				}

			} else {
				// for now travel is only to the current planet.
				// if travel is to be between planets we need to save the
				// player, serialize it and send it to the new
				// planet server for processing and spawn the player there.
				this.setPlanetID(planetid);
				this.setX(x);
				this.setY(y);
				this.setZ(z);
				this.setCellID(cellid);
				this.clearSpawnedItems();
				// bIsTraveling = true;
				// this.spawnPlayer();
				// 'spawnItem(this);
				if (cellid == 0) {
					this
							.getClient()
							.insertPacket(
									PacketFactory
											.buildObjectControllerPlayerDataTransformToClient(
													this, 0x0B));
				} else {
					this
							.getClient()
							.insertPacket(
									PacketFactory
											.buildObjectControllerPlayerDataTransformWithParentToClient(
													this, 0x0B));
				}

			}
			// this.isWarping = false;
		} catch (Exception e) {
			DataLog.logEntry("Eception Caught in playerWarp " + e, "Player",
					Constants.LOG_SEVERITY_CRITICAL, ZoneServer.ZoneRunOptions
							.isBLogToConsole(), true);
			// System.out.println("Eception Caught in playerWarp " + e);
			e.printStackTrace();
		}

	}

	protected boolean IsPlayerTraveling() {
		return bIsTraveling;
	}

	protected void setPlayerIsNotTraveling() {
		bIsTraveling = false;
	}

	protected void addPendingSUIWindow(SUIWindow W) {
		// System.out.println("Add pending SUI window, ID " + W.getWindowID());
		if (PendingSUIWindowList == null) {
			PendingSUIWindowList = new ConcurrentHashMap<Integer, SUIWindow>();
		}
		if (W != null) {
			// System.out.println("Adding SUIWindow ID" + W.getWindowID());
			PendingSUIWindowList.putIfAbsent(W.getWindowID(), W);
		}
	}

	protected void removePendingSUIWindow(SUIWindow W) {
		PendingSUIWindowList.remove(W.getWindowID());
	}

	protected void removePendingSUIWindow(int W) {
		try {
			PendingSUIWindowList.remove(W);
		} catch (Exception e) {
			// who cares.
		}

	}

	protected SUIWindow getPendingSUIWindow(int W) {
		return PendingSUIWindowList.get(W);
	}

	protected void setLastConversationNPC(long NPCId) {
		lLastConversationNPC = NPCId;
	}

	protected long getLastConversationNPC() {
		return lLastConversationNPC;
	}

	protected void clearLastConversationMenu() {
		sLastConversationMenu = null;
	}

	protected void setLastConversationMenu(String[] M) {
		if (sLastConversationMenu == null) {
			sLastConversationMenu = new String[7];
		}
		sLastConversationMenu[0] = M[0];
		sLastConversationMenu[1] = M[1];
		sLastConversationMenu[2] = M[2];
		sLastConversationMenu[3] = M[3];
		sLastConversationMenu[4] = M[4];
		sLastConversationMenu[5] = M[5];
		sLastConversationMenu[6] = M[6];

	}

	protected String[] getLastConversationMenu() {
		return sLastConversationMenu;
	}

	protected void clearLastConversationMenuOptions() {
		vLastConversationMenuOptions.clear();
	}

	protected void setLastConversationMenuOptions(Vector<DialogOption> L) {
		if (vLastConversationMenuOptions == null) {
			vLastConversationMenuOptions = new Vector<DialogOption>();
		}
		vLastConversationMenuOptions.clear();
		vLastConversationMenuOptions = L;
	}

	protected Vector<DialogOption> getLastConversationMenuOptions() {
		return vLastConversationMenuOptions;
	}

	protected void setSkillBeingPurchased(int index) {
		iSkillBeingPurchasedIndex = index;
	}

	protected int getSkillBeingPurchased() {
		return iSkillBeingPurchasedIndex;
	}

	protected void playerBurstRun(CommandQueueItem action) {

		if (!bBurstRunning && lBurstRunRechargeTimer <= 0 && !this.isMounted()
				&& this.getStance() == Constants.STANCE_STANDING) {
			lBurstRunexpiryTimer = (1000 * 60);
			lBurstRunRechargeTimer = (1000 * 60 * 6);
			bBurstRunning = true;
			short[] updateOperand = new short[2];
			float[] newValue = new float[2];
			short updateCount = 2;
			updateOperand[0] = 7;
			updateOperand[1] = 11;
			newValue[0] = (float) (5.375 * 2.5);
			newValue[1] = ((float) 3.5);
			try {
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 0x04, updateCount,
						updateOperand, this, newValue));
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"cbt_spam", "burstrun_start_single", 0l, "", "", "",
						0l, "", "", "", 0l, "", "", "", 0, 0f, false));
			} catch (Exception e) {
				DataLog.logEntry("Exception while trying to Burst Run " + e,
						"Player", Constants.LOG_SEVERITY_CRITICAL,
						ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
				// System.out.println("Exception while trying to Burst Run " +
				// e);
				e.printStackTrace();
			}
		} else if (lBurstRunexpiryTimer > 0) {
			action
					.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_WITH_STATE);
			action
					.setStateInvoked(Constants.COMMAND_QUEUE_ERROR_STANCE_RUNNING); // Why
																					// not...
		} else if (lBurstRunRechargeTimer > 0) {
			try {
				long b = lBurstRunRechargeTimer;
				if (lBurstRunRechargeTimer >= (1000 * 60 * 6)) {
					lBurstRunRechargeTimer = 0;
					playerBurstRun(action);
					return;
				}
				b /= 1000;
				b /= 60;
				if (b >= 2) {
					client
							.insertPacket(PacketFactory
									.buildChatSystemMessage("You cannot burst run again for "
											+ b + " Minutes"));
				} else if (b >= 1) {
					client
							.insertPacket(PacketFactory
									.buildChatSystemMessage("You cannot burst run again for "
											+ b + " Minute"));
				} else {
					client
							.insertPacket(PacketFactory
									.buildChatSystemMessage("You cannot burst run again for "
											+ b + " Seconds"));
				}
			} catch (Exception e) {
				DataLog.logEntry("Exception while trying to Burst Run " + e,
						"Player", Constants.LOG_SEVERITY_CRITICAL,
						ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
				// System.out.println("Exception while trying to Burst Run " +
				// e);
				e.printStackTrace();
			}
		} else if (isMounted()) {
			action
					.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_IN_STANCE);
			action
					.setStateInvoked(Constants.COMMAND_QUEUE_ERROR_STANCE_MOUNTED_VEHICLE);
		} else if (this.getStance() != Constants.STANCE_STANDING) {
			action
					.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_IN_STANCE);
			action.setStateInvoked(getCommandQueueErrorStance());
		}
	}

	protected void playerUnBurstRun() {

		if (bBurstRunning) {
			bBurstRunning = false;
			short[] updateOperand = new short[2];
			float[] newValue = new float[2];
			short updateCount = 2;
			updateOperand[0] = 7;
			updateOperand[1] = 11;
			newValue[0] = (float) (5.375);
			newValue[1] = ((float) 2.5);
			try {
				client.insertPacket(PacketFactory.buildDeltasMessage(
						Constants.BASELINES_CREO, (byte) 0x04, updateCount,
						updateOperand, this, newValue));
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"cbt_spam", "burstrun_stop_single", 0l, "", "", "", 0l,
						"", "", "", 0l, "", "", "", 0, 0f, false));
				client.insertPacket(PacketFactory
						.buildChatSystemMessage("You are tired"));
			} catch (Exception e) {
				DataLog.logEntry("Exception while trying to Burst Run " + e,
						"Player", Constants.LOG_SEVERITY_CRITICAL,
						ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
				// System.out.println("Exception while trying to Burst Run " +
				// e);
				e.printStackTrace();
			}
		}
	}

	protected void setLastUsedSurveyTool(TangibleItem t) {
		lastUsedSurveyTool = t;
	}

	protected TangibleItem getLastUsedSurveyTool() {
		return lastUsedSurveyTool;
	}

	protected void updateExperience(Skills skill, int iExperienceType,
			int iExperienceGained) {
		if (iExperienceGained != 0) {
			thePlayer.updateExperience(skill, iExperienceType,
					iExperienceGained);
		}
	}

	protected void resetTimedEvents() {
		/*
		 * for (int i = 0; i < vInventoryItemsList.size(); i++) { TangibleItem
		 * item = vInventoryItemsList.elementAt(i); item.stopSurveying(); }
		 */
		if (lastUsedSurveyTool != null) {
			lastUsedSurveyTool.stopSurveying();
			bIsSampling = false;
		}
	}

	protected boolean addPlayerStructure(Structure s) {
		if (vPlayerStructures == null) {
			vPlayerStructures = new ConcurrentHashMap<Long, Structure>();
		}
		if (!vPlayerStructures.containsKey(s.getID())) {
			vPlayerStructures.put(s.getID(), s);
		}
		return vPlayerStructures.containsKey(s.getID());
	}

	protected void removePlayerStructure(Structure s) {
		if (vPlayerStructures == null) {
			vPlayerStructures = new ConcurrentHashMap<Long, Structure>();
		}
		if (vPlayerStructures.containsKey(s.getID())) {
			vPlayerStructures.remove(s.getID());
		}

	}

	protected ConcurrentHashMap<Long, Structure> getAllPlayerStructures() {
		if (vPlayerStructures == null) {
			vPlayerStructures = new ConcurrentHashMap<Long, Structure>();
		}
		return vPlayerStructures;
	}

	public long getLCurrentDeedInPlacementMode() {
		return lCurrentDeedInPlacementMode;
	}

	public void setLCurrentDeedInPlacementMode(long lCurrentDeedInPlacementMode) {
		this.lCurrentDeedInPlacementMode = lCurrentDeedInPlacementMode;
	}

	protected void addDelayedSpawnObject(SOEObject o, long lDelayTime) {
		if (vDelayedSpawnObjects == null) {
			vDelayedSpawnObjects = new Vector<SOEObject>();
		}
		vDelayedSpawnObjects.add(o);
		if (lNextDelayedSpawn == null) {
			lNextDelayedSpawn = new Vector<Long>();
		}
		lNextDelayedSpawn.add(lDelayTime);
	}

	protected SOEObject getNextDelayedSpawnObject() {
		if (vDelayedSpawnObjects == null) {
			vDelayedSpawnObjects = new Vector<SOEObject>();
		}
		return vDelayedSpawnObjects.get(0);
	}

	protected long getNextDelayTime(int item) {
		if (lNextDelayedSpawn == null) {
			lNextDelayedSpawn = new Vector<Long>();
		}
		return lNextDelayedSpawn.get(item);
	}

	protected int getDelayListCount() {
		if (lNextDelayedSpawn == null) {
			lNextDelayedSpawn = new Vector<Long>();
		}
		return lNextDelayedSpawn.size();
	}

	protected void removeDelayedSpawn(SOEObject o, long lDelayTime) {
		if (lNextDelayedSpawn.contains(lDelayTime)) {
			lNextDelayedSpawn.remove(lDelayTime);
		}
		if (vDelayedSpawnObjects.contains(o)) {
			vDelayedSpawnObjects.remove(o);
		}
	}

	protected BitSet getSkillList() {
		return thePlayer.getSkillBits();
	}

	protected void removeSpawnedObject(SOEObject o) {
		if (vAllSpawnedObjectIDs.contains(o)) {
			vAllSpawnedObjectIDs.remove(o);
		}
	}

	public boolean isDeleted() {
		return bIsDeleted;
	}

	public void setIsDeleted(boolean bIsDeleted) {
		this.bIsDeleted = bIsDeleted;
	}

	protected void playerLogout(CommandQueueItem action) {
		if ((getStateBitmask() & Constants.STATE_COMBAT) != 0) {
			action
					.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_WITH_STATE);
			action.setStateInvoked(Constants.STATE_COMBAT);
			return;
		}
		try {
			this.lLogoutTimer = 1000 * 30;
			this.bPlayerRequestedLogout = true;
			this.lLogoutSpamTimer = 1000 * 5;
			client.insertPacket(PacketFactory.buildChatSystemMessage("logout",
					"time_left", 0l, "", "", "", 0l, "", "", "", 0l, "", "",
					"", (int) (lLogoutTimer / 1000), 0f, false));
		} catch (Exception e) {
			DataLog.logEntry("Exception caught in player.playerlogout " + e,
					"Player", Constants.LOG_SEVERITY_CRITICAL,
					ZoneServer.ZoneRunOptions.isBLogToConsole(), true);
			// System.out.println("Exception caught in Player.playerLogout " +
			// e);
			e.printStackTrace();
		}
	}

	protected void resetPlayerLogout() {
		this.bPlayerRequestedLogout = false;
	}

	protected int getTradeRequestCounter() {
		return iTradeRequestCounter;
	}

	protected void setTradeRequestCounter(int iTradeRequestCounter) {
		this.iTradeRequestCounter = iTradeRequestCounter;
	}

	protected boolean addTradeRequest(TradeObject t) {
		if (vTradeRequests == null) {
			vTradeRequests = new Vector<TradeObject>();
		}
		return vTradeRequests.add(t);
	}

	// vTradeRequests;

	protected boolean removeTradeRequest(TradeObject t) {
		if (vTradeRequests == null) {
			vTradeRequests = new Vector<TradeObject>();
		}
		return vTradeRequests.remove(t);
	}

	protected TradeObject getTradeObjectByRequestID(int iRequest) {
		TradeObject retval = null;
		for (int i = 0; i < vTradeRequests.size(); i++) {
			TradeObject t = vTradeRequests.get(i);
			if (t.getITradeRequestID() == iRequest) {
				return t;
			}
		}
		return retval;
	}

	protected TradeObject getTradeObjectByRecipientID(long lRecipient) {
		TradeObject retval = null;
		for (int i = 0; i < vTradeRequests.size(); i++) {
			TradeObject t = vTradeRequests.get(i);
			if (t.getRecipient().getID() == lRecipient) {
				return t;
			}
		}
		return retval;
	}

	protected long getIncomingTradeRequesterID() {
		if (vIncomingTradeRequest == null) {
			return 0;
		}
		return vIncomingTradeRequest.get(0); // <<get the top requester
	}

	protected void addIncomingTradeRequest(long lRequesterID) {
		if (vIncomingTradeRequest == null) {
			vIncomingTradeRequest = new Vector<Long>();
		}
		if (!vIncomingTradeRequest.contains(lRequesterID)) {
			vIncomingTradeRequest.add(0, lRequesterID); // <<always add to 0
		}

	}

	protected void removeIncomingtradeRequest(long lRequesterID) {
		if (vIncomingTradeRequest == null) {
			vIncomingTradeRequest = new Vector<Long>();
		}
		if (!vIncomingTradeRequest.contains(lRequesterID)) {
			vIncomingTradeRequest.remove(lRequesterID);
		}
	}

	protected void setCurrentTradeObject(TradeObject currentTradeObject) {
		this.currentTradeObject = currentTradeObject;
	}

	protected TradeObject getCurrentTradeObject() {
		return currentTradeObject;
	}

	protected void removeCurrentTradeObject() {
		currentTradeObject = null;
	}

	public long getLPlayerCreationDate() {
		return lPlayerCreationDate;
	}

	/**
	 * returns the next number of the group invite value this increments the
	 * counter for every call
	 * 
	 * @return
	 */
	public int getGroupInviteCounter(boolean bIncrement) {
		if (bIncrement) {
			iGroupInviteCounter++;
		}
		return iGroupInviteCounter;
	}

	public long getGroupHost() {
		return lGroupHost;
	}

	public void setGroupHost(long lGroupHost) {
		this.lGroupHost = lGroupHost;
	}

	public void resetGroupTime() {
		lGroupTime = 0;
	}

	public void updateGroupTime(long lDeltaMS) {
		lGroupTime += lDeltaMS;
	}

	public long getGroupTime() {
		return lGroupTime;
	}

	public int getGroupUpdateCounter(boolean bIncrement) {
		if (bIncrement) {
			iGroupUpdateCounter++;
		}
		return iGroupUpdateCounter;
	}

	protected ResourceContainer getLastUpdatedResourceContainer() {
		return thePlayer.getLastUpdatedResourceContainer();
	}

	protected void setLastUpdatedResourceContainer(ResourceContainer container) {
		thePlayer.setLastUpdatedResourceContainer(container);
	}

	protected void setNextHealDelay(int iSkillModValue) {
		lHealDelayMS = (Constants.BASE_HEALING_ACTION_COOLDOWN_MS - (Constants.HEALING_ACTION_DECAY_PER_SKILLMOD_POINT * iSkillModValue));
	}

	protected long getNextHealDelay() {
		return lHealDelayMS;
	}

	public void setHasOutstandingTeachingOffer(
			boolean bHasOutstandingTeachingOffer) {
		this.bHasOutstandingTeachingOffer = bHasOutstandingTeachingOffer;
	}

	public boolean getHasOutstandingTeachingOffer() {
		return bHasOutstandingTeachingOffer;
	}

	public void setDisconnectIgnore(boolean bDisconnectIgnore) {
		this.bDisconnectIgnore = bDisconnectIgnore;
	}

	public boolean isDisconnectIgnore() {
		return bDisconnectIgnore;
	}

	public void setStudent(Player student) {
		this.teachingStudent = student;
	}

	public Player getStudent() {
		return teachingStudent;
	}

	public Player getTeacher() {
		return teacher;
	}

	public void setTeacher(Player player) {
		teacher = player;
	}

	public void setSkillOfferedByTeacher(Skills skill) {
		skillOfferedByTeacher = skill;
		if (skill == null) {
			setHasOutstandingTeachingOffer(false);
		} else {
			setHasOutstandingTeachingOffer(true);
		}
	}

	public Skills getSkillOfferedByTeacher() {
		return skillOfferedByTeacher;
	}

	public int getIPlaySoundUpdateCounter() {
		iPlaySoundUpdateCounter++;
		return iPlaySoundUpdateCounter;
	}

	public long getListeningToID() {
		return lListeningToID;
	}

	public void setListeningToID(long lListeningToID) {
		this.lListeningToID = lListeningToID;
		try {
			client.insertPacket(PacketFactory.buildDeltasMessage(
					Constants.BASELINES_CREO, (byte) 4, (short) 1, (short) 6,
					this, lListeningToID));
		} catch (Exception e) {
			DataLog.logException("Exception while updating listening to ID",
					"Player.setListeningToID",
					ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	/**
	 * Return the amount of space left on a players inventory. If any items such
	 * as deeds are not to be counted towards space used because they are in a
	 * state that should not be counted, it has to be accounted for here. In the
	 * case of deeds, when placed the deed is not counted towards inventory
	 * space since its not spawned to the players inventory yet its still there
	 * so the deed can be returned. This routine will count the amount of items
	 * in the players inventory and return 80 - the count of items.
	 * 
	 * @return
	 */
	protected int getInventorySlotsLeft() {
		int invCount = 0;
		for (int i = 0; i < getInventoryItems().size(); i++) {
			TangibleItem t = getInventoryItems().get(i);
			if (t instanceof Deed) {
				Deed d = (Deed) t;
				if (!d.isPlaced()) {
					invCount++;
				} else {
					invCount++;
				}
			}
		}
		return 80 - invCount;
	}

	public void setSynchronizedListenObject(SOEObject synchronizedObject) {
		this.synchronizedObject = synchronizedObject;
	}

	public SOEObject getSynchronizedListenObject() {
		return synchronizedObject;
	}

	public void setLastSentCraftingSchematicList(
			Vector<CraftingSchematic> vLastCreatedCraftingSchematicList) {
		this.vLastCreatedCraftingSchematicList = vLastCreatedCraftingSchematicList;
	}

	public Vector<CraftingSchematic> getLastSentCraftingSchematicList() {
		return vLastCreatedCraftingSchematicList;
	}

	public Camp getCurrentCampObject() {
		return currentCampObject;
	}

	public void setCurrentCampObject(Camp currentCampObject) {
		this.currentCampObject = currentCampObject;
	}

	public boolean isInCamp() {
		return bIsInCamp;
	}

	public void setIsInCamp(boolean bIsInCamp) {
		this.bIsInCamp = bIsInCamp;
	}

	protected void addCalledPet(CreaturePet pet) {
		if (vCalledPets == null) {
			vCalledPets = new Vector<CreaturePet>();
		}
		if (!vCalledPets.contains(pet)) {
			vCalledPets.add(pet);
		}
	}

	public void setCurrentManufacturingSchematic(
			ManufacturingSchematic currentManufacturingSchematic) {
		this.currentManufacturingSchematic = currentManufacturingSchematic;
	}

	protected void removeCalledPet(CreaturePet pet) {
		if (vCalledPets == null) {
			vCalledPets = new Vector<CreaturePet>();
		}
		if (vCalledPets.contains(pet)) {
			vCalledPets.remove(pet);
		}
	}

	public ManufacturingSchematic getCurrentManufacturingSchematic() {
		return currentManufacturingSchematic;
	}

	protected Vector<CreaturePet> getCalledPets() {
		if (vCalledPets == null) {
			vCalledPets = new Vector<CreaturePet>();
		}
		return vCalledPets;
	}

	protected void addFriendPet(CreaturePet pet) {
		if (vFriendPets == null) {
			vFriendPets = new Vector<CreaturePet>();
		}
		if (!vFriendPets.contains(pet)) {
			vFriendPets.add(pet);
		}
	}

	protected void removeFriendPet(CreaturePet pet) {
		if (vFriendPets == null) {
			vFriendPets = new Vector<CreaturePet>();
		}
		if (vFriendPets.contains(pet)) {
			vFriendPets.remove(pet);
		}
	}

	protected Vector<CreaturePet> getFriendPets() {
		if (vFriendPets == null) {
			vFriendPets = new Vector<CreaturePet>();
		}
		return vFriendPets;
	}

	/*------------------------------------------------------*/

	/**
	 * 338, 'outdoors_creaturehandler_novice',
	 * 'pet_follow,pet_release,pet_attack,tame',
	 * 'stored_pets=4,keep_creature=1,tame_non_aggro=5,tame_level=12' 339,
	 * 'outdoors_creaturehandler_master', 'pet_transfer,pet_rangedattack',
	 * 'keep_creature=1,tame_non_aggro=5,tame_aggro=15,private_creature_empathy=10
	 * , private_creature_training=10,private_creature_management=10,tame_level=
	 * 10, stored_pets=4' 340, 'outdoors_creaturehandler_taming_01', ' ',
	 * 'tame_non_aggro=5,tame_level=2' 341,
	 * 'outdoors_creaturehandler_taming_02', ' ',
	 * 'tame_non_aggro=5,tame_aggro=10,tame_level=2' 342,
	 * 'outdoors_creaturehandler_taming_03', 'pet_specialattack1',
	 * 'tame_non_aggro=5,tame_aggro=10,tame_level=3' 343,
	 * 'outdoors_creaturehandler_taming_04', 'pet_specialattack2',
	 * 'tame_non_aggro=5,tame_aggro=15,tame_level=5' 344,
	 * 'outdoors_creaturehandler_training_01', 'pet_stay',
	 * 'private_creature_training=10,tame_level=2,stored_pets=2,tame_non_aggro=5
	 * ' 345, 'outdoors_creaturehandler_training_02', 'pet_guard',
	 * 'stored_pets=3,private_creature_training=10,tame_level=2,tame_non_aggro=5
	 * ' 346, 'outdoors_creaturehandler_training_03', 'pet_patrol',
	 * 'private_creature_training=10,tame_level=3,stored_pets=3,tame_non_aggro=5
	 * ' 347, 'outdoors_creaturehandler_training_04', 'pet_formation',
	 * 'stored_pets=4,private_creature_training=10,tame_level=5,tame_non_aggro=5
	 * ' 348, 'outdoors_creaturehandler_healing_01', 'trick1',
	 * 'private_creature_empathy=10,tame_level=2,tame_non_aggro=5' 349,
	 * 'outdoors_creaturehandler_healing_02', 'emboldenpets',
	 * 'private_creature_empathy=10,tame_level=2,tame_non_aggro=5' 350,
	 * 'outdoors_creaturehandler_healing_03', 'trick2',
	 * 'private_creature_empathy=10,tame_level=3,tame_non_aggro=5' 351,
	 * 'outdoors_creaturehandler_healing_04', 'enragepets',
	 * 'private_creature_empathy=10,tame_level=5,tame_non_aggro=5' 352,
	 * 'outdoors_creaturehandler_support_01', 'pet_group',
	 * 'private_creature_management=10,tame_level=2,tame_non_aggro=5' 353,
	 * 'outdoors_creaturehandler_support_02', 'pet_followother',
	 * 'private_creature_management=10,tame_level=2,tame_non_aggro=5' 354,
	 * 'outdoors_creaturehandler_support_03', 'pet_friend',
	 * 'keep_creature=1,private_creature_management=10,tame_level=3,tame_non_aggro=5
	 * ' 355, 'outdoors_creaturehandler_support_04', 'train_mount',
	 * 'private_creature_management=10,tame_level=5,tame_non_aggro=5'
	 * 
	 * private int iMaxCalledDroids; // def 2 private int iMaxCalledAnimalPets;
	 * //def 1 private int iMaxCalledFactionPets; //def 2
	 * 
	 * private int iMaxDataPadDroids; //def 4 private int iMaxDataPadAnimalPets;
	 * //def 3 private int iMaxDataPadFactionPets; //def 2
	 * 
	 * private int iMaxCalledPets; //def 2 private int iMaxDataPadPets; //def 9
	 */

	public int getIMaxCalledAnimalPets() {

		iMaxCalledAnimalPets = 1;

		if (this.hasSkill(338)) {
			iMaxCalledAnimalPets++;
		}
		if (this.hasSkill(354)) {
			iMaxCalledAnimalPets++;
		}
		return iMaxCalledAnimalPets;
	}

	public void setIMaxCalledAnimalPets(int iMaxCalledAnimalPets) {
		this.iMaxCalledAnimalPets = iMaxCalledAnimalPets;
	}

	public int getIMaxCalledDroids() {
		iMaxCalledDroids = 2;
		if (this.hasSkill(479)) {
			iMaxCalledDroids += 2;
		}
		return iMaxCalledDroids;
	}

	public void setIMaxCalledDroids(int iMaxCalledDroids) {
		this.iMaxCalledDroids = iMaxCalledDroids;
	}

	public int getIMaxCalledFactionPets() {
		iMaxCalledFactionPets = 2;
		return iMaxCalledFactionPets;
	}

	public void setIMaxCalledFactionPets(int iMaxCalledFactionPets) {
		this.iMaxCalledFactionPets = iMaxCalledFactionPets;
	}

	public int getIMaxCalledPets() {
		iMaxCalledPets = 2;
		if (this.hasSkill(338)) {
			iMaxCalledPets++;
		}
		if (this.hasSkill(354)) {
			iMaxCalledPets++;
		}
		if (this.hasSkill(339)) {
			iMaxCalledPets++;
		}
		if (this.hasSkill(479)) {
			iMaxCalledPets += 2;
		}
		return iMaxCalledPets;
	}

	public void setIMaxCalledPets(int iMaxCalledPets) {
		this.iMaxCalledPets = iMaxCalledPets;
	}

	public int getIMaxDataPadAnimalPets() {
		iMaxDataPadAnimalPets = 3;
		if (this.hasSkill(338)) {
			iMaxDataPadAnimalPets += 4;
		}
		if (this.hasSkill(339)) {
			iMaxDataPadAnimalPets += 4;
		}
		if (this.hasSkill(344)) {
			iMaxDataPadAnimalPets += 2;
		}
		if (this.hasSkill(345)) {
			iMaxDataPadAnimalPets += 3;
		}
		if (this.hasSkill(346)) {
			iMaxDataPadAnimalPets += 3;
		}
		if (this.hasSkill(347)) {
			iMaxDataPadAnimalPets += 4;
		}
		return iMaxDataPadAnimalPets;
	}

	public void setIMaxDataPadAnimalPets(int iMaxDataPadAnimalPets) {
		this.iMaxDataPadAnimalPets = iMaxDataPadAnimalPets;
	}

	public int getIMaxDataPadDroids() {
		iMaxDataPadDroids = 4;
		if (this.hasSkill(472)) {
			iMaxDataPadDroids += 4;
		}
		return iMaxDataPadDroids;
	}

	public void setIMaxDataPadDroids(int iMaxDataPadDroids) {
		this.iMaxDataPadDroids = iMaxDataPadDroids;
	}

	public int getIMaxDataPadFactionPets() {
		iMaxDataPadFactionPets = 2;
		return iMaxDataPadFactionPets;
	}

	public void setIMaxDataPadFactionPets(int iMaxDataPadFactionPets) {
		this.iMaxDataPadFactionPets = iMaxDataPadFactionPets;
	}

	public int getIMaxDataPadPets() {
		iMaxDataPadPets = 9;
		if (this.hasSkill(338)) {
			iMaxDataPadPets += 4;
		}
		if (this.hasSkill(339)) {
			iMaxDataPadPets += 4;
		}
		if (this.hasSkill(344)) {
			iMaxDataPadPets += 2;
		}
		if (this.hasSkill(345)) {
			iMaxDataPadPets += 3;
		}
		if (this.hasSkill(346)) {
			iMaxDataPadPets += 3;
		}
		if (this.hasSkill(347)) {
			iMaxDataPadPets += 4;
		}
		if (this.hasSkill(472)) {
			iMaxDataPadPets += 4;
		}
		return iMaxDataPadPets;
	}

	public void setIMaxDataPadPets(int iMaxDataPadPets) {
		this.iMaxDataPadPets = iMaxDataPadPets;
	}

	public Waypoint getLastForageArea() {
		return lastForageArea;
	}

	public void setLastForageArea(Waypoint lastForageArea) {
		this.lastForageArea = lastForageArea;
	}

	public long getForageCooldown() {
		return forageCooldown;
	}

	public void setForageCooldown(long forageCooldown) {
		this.forageCooldown = forageCooldown;
	}

	public byte getForageType() {
		return forageType;
	}

	public void setForageType(byte forageType) {
		this.forageType = forageType;
	}

	public boolean isForaging() {
		return isForaging;
	}

	public void setIsForaging(boolean isForaging) {
		this.isForaging = isForaging;
	}

	private void forage() {

		try {
			this.isForaging = false;
			if (this.getForageType() == Constants.FORAGE_TYPE_GENERAL) {
				boolean bFound = false;
				TangibleItem foraged = null;

				if (SWGGui.getRandomInt(1, 1000) >= 500) {
					int iRandomForagedItem = SWGGui.getRandomInt(0,
							Constants.FORAGED_ITEMS.length);
					foraged = new FoodItem();
					foraged.setID(server.getNextObjectID());
					foraged
							.setTemplateID(Constants.FORAGED_ITEMS[iRandomForagedItem]);
					foraged.setConditionDamage(0, false);
					foraged.setMaxCondition(1000, false);
					foraged.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
					int iRandomSkillModCount = SWGGui.getRandomInt(0, 2);
					if (iRandomSkillModCount >= 1) {
						int[] iRandomSkillMod = new int[iRandomSkillModCount];
						int[] iRandomSkillModValue = new int[iRandomSkillModCount];
						for (int i = 0; i < iRandomSkillModCount; i++) {
							iRandomSkillMod[i] = SWGGui.getRandomInt(0,
									Constants.FORAGED_SKILL_MODIFIERS.length);
							iRandomSkillModValue[i] = Math.max(2, SWGGui
									.getRandomInt(2, 8));
							SkillModifier s = new SkillModifier();
							s.setCharges(1);
							long lDuration = Math.max(5000, SWGGui
									.getRandomLong(5000, 15000));
							s.setDuration(lDuration);
							s
									.setSkillModifierID(Constants.FORAGED_SKILL_MODIFIERS[iRandomSkillMod[i]]);
							s.setSkillModifierValue(iRandomSkillModValue[i]);
							foraged.addSkillModifier(s);
						}
					}
					bFound = true;
				}
				if (bFound && this.getInventorySlotsLeft() >= 1) {
					foraged
							.setDelayedSpawnAction(Constants.DELAYED_SPAWN_ACTION_SPAWN);
					server.addObjectToAllObjects(foraged, false, false);
					foraged.setEquipped(this.getInventory(),
							Constants.EQUIPPED_STATE_UNEQUIPPED);
					this.addItemToInventory(foraged);
					this.getInventory().addLinkedObject(foraged);
					this.addDelayedSpawnObject(foraged, System
							.currentTimeMillis() + 500);
					this.getClient().insertPacket(
							PacketFactory.buildChatSystemMessage("skl_use",
									"sys_forage_success"));
					this.updateExperience(null, DatabaseInterface
							.getExperienceIDFromName("camp"), 15);
				} else if (this.getInventorySlotsLeft() == 0) {
					this.getClient().insertPacket(
							PacketFactory.buildChatSystemMessage("skl_use",
									"sys_forage_noroom"));
				} else {
					this.getClient().insertPacket(
							PacketFactory.buildChatSystemMessage("skl_use",
									"sys_forage_fail"));
				}
			}

		} catch (Exception e) {
			DataLog.logException("Exception in forage", "Player",
					ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	public Instrument getEquippedInstrument() {
		return equippedInstrument;
	}

	public void unequipInstrument() {
		equippedInstrument = null;
	}

	public Player getPlayerBeingListened() {
		return playerBeingListened;
	}

	public void setPlayerBeingListened(Player playerBeingListened) {

		try {
			getClient().insertPacket(
					PacketFactory.buildChatSystemMessage("performance",
							"music_stop_other", this.playerBeingListened
									.getID(), this.playerBeingListened
									.getSTFFileName(), this.playerBeingListened
									.getSTFFileIdentifier(),
							this.playerBeingListened.getFullName(), 0, null,
							null, null, 0, null, null, null, 0, 0.0f, false));
			if (playerBeingListened != null) {
				this.setListeningToID(playerBeingListened.getID());
			} else {
				this.setListeningToID(0);
			}
			this.playerBeingListened = playerBeingListened;
		} catch (Exception e) {
			DataLog.logException("Exception while setting listening target",
					"Player", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	public Player getPlayerBeingWatched() {
		return playerBeingWatched;
	}

	public void setPlayerBeingWatched(Player playerBeingWatched) {
		if (this.playerBeingWatched != null) {
			this.playerBeingWatched.removePlayerWatching(this);
		}
		if (playerBeingWatched != null) {
			this.playerBeingWatched = playerBeingWatched;
			try {
				client.insertPacket(PacketFactory.buildChatSystemMessage(
						"performance", "dance_watch_self", this.getID(), this
								.getSTFFileName(), this.getSTFFileIdentifier(),
						this.getFullName(), playerBeingWatched.getID(),
						playerBeingWatched.getSTFFileName(), playerBeingWatched
								.getSTFFileIdentifier(), playerBeingWatched
								.getFullName(), 0, null, null, null, 0, 0.0f,
						false));
				this.setListeningToID(playerBeingWatched.getID());
				lRandomWatchPlayerTick = Math.max(5000, SWGGui.getRandomLong(
						5000, 30000));

				/**
				 * @todo send player listening to ID delta
				 */
				// client.insertPacket(PacketFactory);
			} catch (Exception e) {
				DataLog.logException(
						"Exception while setting listening target", "Player",
						ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
			}
		}
	}

	protected void addPlayerWatching(Player p) {
		if (vPlayersWatching == null) {
			vPlayersWatching = new Vector<Player>();
		}
		if (!vPlayersWatching.contains(p)) {
			vPlayersWatching.add(p);
		}
	}

	protected void removePlayerWatching(Player p) {
		if (vPlayersWatching == null) {
			vPlayersWatching = new Vector<Player>();
		}
		if (vPlayersWatching.contains(p)) {
			vPlayersWatching.remove(p);
		}
	}

	protected void addPlayerListening(Player p) {
		if (vPlayersListening == null) {
			vPlayersListening = new Vector<Player>();
		}
		if (!vPlayersListening.contains(p)) {
			vPlayersListening.add(p);
		}
	}

	protected void removePlayerListening(Player p) {
		if (vPlayersListening == null) {
			vPlayersListening = new Vector<Player>();
		}
		if (vPlayersListening.contains(p)) {
			vPlayersListening.remove(p);
		}
	}

	protected Vector<Player> getPlayersListening() {
		if (vPlayersListening == null) {
			vPlayersListening = new Vector<Player>();
		}
		return vPlayersListening;
	}

	protected Vector<Player> getPlayersWatching() {
		if (vPlayersWatching == null) {
			vPlayersWatching = new Vector<Player>();
		}
		return vPlayersWatching;
	}

	public boolean isDancing() {
		return isDancing;
	}

	public void setIsDancing(boolean isDancing) {
		System.out
				.println("Player Set To Dance " + Boolean.toString(isDancing));
		this.isDancing = isDancing;
	}

	public boolean isPlayingMusic() {
		return isPlayingMusic;
	}

	public void setIsPlayingMusic(boolean isPlayingMusic) {
		this.isPlayingMusic = isPlayingMusic;
	}

	protected void setDanceTick(long tick) {
		lDancetick = tick;
	}

	protected void setMusicTick(long tick) {
		lMusicTick = tick;
	}

	public long getLEffectTick() {
		return lEffectTick;
	}

	public void setLEffectTick(long lEffectTick) {
		this.lEffectTick = lEffectTick;
	}

	protected Vector<String> getKnownDances() {
		Vector<String> vKnownDances = new Vector<String>();
		for (int i = 0; i < Constants.DANCE_STRINGS.length; i++) {
			if (this.hasSkill(Constants.DANCE_SKILL_REQUIREMENTS[i])) {
				vKnownDances.add(Constants.DANCE_STRINGS[i][0]);
			}
		}
		return vKnownDances;
	}

	protected Vector<String> getKnownMusic() {
		Vector<String> vKnownMusic = new Vector<String>();
		for (int i = 0; i < Constants.MUSIC_STRINGS.length; i++) {
			if (this.hasSkill(Constants.MUSIC_SKILL_REQUIREMENTS[i])) {
				vKnownMusic.add(Constants.MUSIC_STRINGS[i][0]);
			}
		}
		return vKnownMusic;
	}

	public int getIPerformanceID() {
		return iPerformanceID;
	}

	public void setPerformanceID(int iPerformanceID) throws IOException {
		this.iPerformanceID = iPerformanceID;
		getClient().insertPacket(
				PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO,
						(byte) 6, (short) 1, (short) 11, this,
						this.iPerformanceID));
	}

	// These function names need be more descriptive.
	public boolean getC60X11() {
		return C60X11;
	}

	// These function names need be more descriptive.
	public void setC60X11(boolean C60X11) throws IOException {
		this.C60X11 = C60X11;
		client.insertPacket(PacketFactory.buildDeltasMessage(
				Constants.BASELINES_CREO, (byte) 6, (short) 1, (short) 0x11,
				this, this.C60X11), Constants.PACKET_RANGE_CHAT_RANGE);
	}

	public byte[] setNearbyCraftingStation(TangibleItem station) {
		return thePlayer.setNearbyCraftingStation(station);
	}

	public TangibleItem getNearbyCraftingStation() {
		return thePlayer.getNearbyCraftingStation();
	}

	public byte[] setExperimentationAndManufacturingFlag(int flag) {
		return thePlayer.setExperimentationAndManufacturingFlag(flag);
	}

	public int getExperimentationAndManufacturingFlag() {
		return thePlayer.getExperimentationAndManufacturingFlag();
	}

	public byte[] setNumExperimentationPoints(int pointCount) {
		return thePlayer.setNumExperimentationPoints(pointCount);
	}

	public int getNumExperimentationPoints() {
		return thePlayer.getNumExperimentationPoints();
	}

	protected int applyBuff(MedicalItem buffItem) throws IOException {
		int buffCRC = 0;
		int buffIndex = buffItem.getHamIndex();
		switch (buffIndex) {
		case Constants.HAM_INDEX_HEALTH: {
			buffCRC = Constants.BUFF_EFFECT_MEDICAL_ENHANCE_HEALTH;
			break;
		}
		case Constants.HAM_INDEX_ACTION: {
			buffCRC = Constants.BUFF_EFFECT_MEDICAL_ENHANCE_ACTION;
			break;
		}
		case Constants.HAM_INDEX_STRENGTH: {
			buffCRC = Constants.BUFF_EFFECT_MEDICAL_ENHANCE_STRENGTH;
			break;
		}
		case Constants.HAM_INDEX_STAMINA: {
			buffCRC = Constants.BUFF_EFFECT_MEDICAL_ENHANCE_STAMINA;
			break;
		}
		case Constants.HAM_INDEX_CONSTITUTION: {
			buffCRC = Constants.BUFF_EFFECT_MEDICAL_ENHANCE_CONSTITUTION;
			break;
		}
		case Constants.HAM_INDEX_QUICKNESS: {
			buffCRC = Constants.BUFF_EFFECT_MEDICAL_ENHANCE_QUICKNESS;
			break;

		}
		default: {
			System.out.println("Unknown stat index to buff: " + buffIndex);
			return 0;
		}
		}
		float fBuffDuration = buffItem.getDurationEffectSec();

		if (client != null) {
			if (getOnlineStatus()) {
				int iPreviousHamModifier = iHamModifiers[buffItem.getHamIndex()];
				int iNewHamModifier = buffItem.getHealAmount();
				int effectiveBuff = iNewHamModifier - iPreviousHamModifier;
				if (effectiveBuff > 0) {
					BuffEffect effect = new BuffEffect();
					effect.setAffectedPlayer(this);
					effect.setBuffHamIndex(buffItem.getHamIndex());
					effect
							.setBuffIndex(Constants.BUFF_EFFECT_MEDICAL_ENHANCE_HEALTH
									+ buffItem.getHamIndex());
					effect.setBuffPotency(buffItem.getHealAmount());
					effect.setStateApplied(0);
					effect.setTimeRemainingMS((long) (fBuffDuration * 1000.0f));
					addBuffEffect(effect);
					updateHamModifiers(buffItem.getHamIndex(), effectiveBuff,
							true, true);
					client.insertPacket(PacketFactory
							.buildObjectControllerMessageBuffStat(this,
									Constants.BUFF_EFFECTS[buffCRC],
									fBuffDuration));
					return effectiveBuff;
				}
			}
		}
		return 0;
	}

	protected int applyDisease(MedicalItem diseaseItem) throws IOException {
		return 0;
	}

	protected void applyFireBlanket(MedicalItem fireBlanket) throws IOException {

	}

	protected int healDamage(MedicalItem healItem) throws IOException {
		int maxDamageToHeal = healItem.getHealAmount();
		int index = healItem.getHamIndex();
		int damageActuallyHealed = updateCurrentHam(index, maxDamageToHeal);
		return Math.max(damageActuallyHealed, 0);
	}

	protected int applyPoison(MedicalItem poisonItem) throws IOException {
		return 0;
	}

	protected void applyPoison(int hamIndex, int potency, long lDuration) {
		lPoisonTimeMS[hamIndex] = lDuration;
		lPoisonTickTimeMS[hamIndex] = 1;
		iPoisonPotency[hamIndex] = potency;
		addState(Constants.STATE_POISONED, lDuration);
	}

	protected void applyDisease(int hamIndex, int potency, long lDuration) {
		lDiseaseTimeMS[hamIndex] = lDuration;
		lDiseaseTickTimeMS[hamIndex] = 1;
		iDiseasePotency[hamIndex] = potency;
		addState(Constants.STATE_DISEASED, lDuration);
	}

	protected void applyBleed(int hamIndex, int potency, long lDuration) {
		lBleedTimeMS[hamIndex] = lDuration;
		lBleedTickTimeMS[hamIndex] = 1;
		iBleedPotency[hamIndex] = potency;
		addState(Constants.STATE_BLEEDING, lDuration);
	}

	protected void applyFire(int hamIndex, int potency, long lDuration) {
		lFireTimeMS[hamIndex] = lDuration;
		lFireTickTimeMS[hamIndex] = 1;
		iFirePotency[hamIndex] = potency;
		addState(Constants.STATE_BURNING, lDuration);
	}

	protected int healWounds(MedicalItem woundHealItem) throws IOException {
		return 0;
	}

	protected void addBuffEffect(BuffEffect effect) {
		// TODO -- If an effect is already present, and superior to the one
		// being added, don't add it.
		if (vBuffEffects == null)
			return;
		vBuffEffects[effect.getBuffIndex()] = effect;
		effect.setAffectedPlayer(this);
		addState((int) effect.getStateApplied(), effect.getTimeRemainingMS());
		int appliedStance = effect.getStanceApplied();
		setStance(null, (byte) appliedStance, true);
	}

	protected void removeBuffEffect(int index) {
		vBuffEffects[index] = null;
		try {
			client.insertPacket(PacketFactory
					.buildObjectControllerMessageBuffStat(this,
							Constants.BUFF_EFFECTS[index], 0.0f));
		} catch (IOException e) {
			// D'oh!
		}
	}

	private void updatePoisons(long lDeltaTimeMS) {
		for (int i = 0; i < lPoisonTickTimeMS.length; i++) {
			if (lPoisonTickTimeMS[i] > 0) {
				lPoisonTickTimeMS[i] -= lDeltaTimeMS;
				if (lPoisonTickTimeMS[i] <= 0) {
					// Tick the poison
					updateCurrentHam(i, -iPoisonPotency[i]);
					lPoisonTickTimeMS[i] = Constants.DAMAGE_OVER_TIME_TICK_RATE_MS;
				}
				lPoisonTimeMS[i] -= lDeltaTimeMS;
				if (lPoisonTimeMS[i] < 0) {
					lPoisonTimeMS[i] = 0;
					lPoisonTickTimeMS[i] = 0;
					iPoisonPotency[i] = 0;
				}
			}
		}
	}

	private void updateDiseases(long lDeltaTimeMS) {
		for (int i = 0; i < lDiseaseTickTimeMS.length; i++) {
			if (lDiseaseTickTimeMS[i] > 0) {
				lDiseaseTickTimeMS[i] -= lDeltaTimeMS;
				if (lDiseaseTickTimeMS[i] <= 0) {
					// Tick the poison
					updateHAMWounds(i, -iDiseasePotency[i], true);
					lDiseaseTickTimeMS[i] = Constants.DAMAGE_OVER_TIME_TICK_RATE_MS;
				}
				lDiseaseTimeMS[i] -= lDeltaTimeMS;
				if (lDiseaseTimeMS[i] < 0) {
					lDiseaseTimeMS[i] = 0;
					lDiseaseTickTimeMS[i] = 0;
					iDiseasePotency[i] = 0;
				}
			}
		}
	}

	private void updateBleeds(long lDeltaTimeMS) {
		for (int i = 0; i < lBleedTickTimeMS.length; i++) {
			if (lBleedTickTimeMS[i] > 0) {
				lBleedTickTimeMS[i] -= lDeltaTimeMS;
				if (lBleedTickTimeMS[i] <= 0) {
					// Tick the poison
					updateCurrentHam(i, -iBleedPotency[i]);
					lBleedTickTimeMS[i] = Constants.DAMAGE_OVER_TIME_TICK_RATE_MS;
				}
				lBleedTimeMS[i] -= lDeltaTimeMS;
				if (lBleedTimeMS[i] < 0) {
					lBleedTimeMS[i] = 0;
					lBleedTickTimeMS[i] = 0;
					iBleedPotency[i] = 0;
				}

			}
		}
	}

	private void updateFires(long lDeltaTimeMS) {
		for (int i = 0; i < lFireTickTimeMS.length; i++) {
			if (lFireTickTimeMS[i] > 0) {
				lFireTickTimeMS[i] -= lDeltaTimeMS;
				if (lFireTickTimeMS[i] <= 0) {
					// Tick the poison
					updateCurrentHam(i, -iFirePotency[i] * 2);
					updateHAMWounds(i, -iFirePotency[i], true);
					lFireTickTimeMS[i] = Constants.DAMAGE_OVER_TIME_TICK_RATE_MS;
				}
				lFireTimeMS[i] -= lDeltaTimeMS;
				if (lFireTimeMS[i] < 0) {
					lFireTimeMS[i] = 0;
					lFireTickTimeMS[i] = 0;
					iFirePotency[i] = 0;
				}

			}
		}
	}

	protected boolean setCurrentDrinkFullness(int iAmountToIncrease)
			throws IOException {
		return thePlayer.setCurrentDrinkFullness(iAmountToIncrease);
	}

	protected boolean setCurrentFoodFullness(int iAmountToIncrease)
			throws IOException {
		return thePlayer.setCurrentFoodFullness(iAmountToIncrease);
	}

	private void updateCommandQueueEnqueues(long lDeltaTimeMS) {
		if (!vCommandQueue.isEmpty()) {
			CommandQueueItem queueItem = vCommandQueue.peekFirst();
			if (!queueItem.hasBeenSent()) {
				// Can we actually do the action?
				// We will assume yes for now.
				CombatAction action = queueItem.getCombatAction();
				if (action != null) {
					try {
						client.getUpdateThread().handleAttack(queueItem);
					} catch (IOException e) {
						DataLog.logException("Error handling attack",
								"ZoneClientThread.handleAttack()", true, true,
								e);
					}
				}
				int iSkillIDRequired = queueItem.getSkillIDRequired();
				if (iSkillIDRequired != -1) {
					if (!hasSkill(iSkillIDRequired)) {
						// This allows for debug of more advanced attacks.
						if (!client.getUpdateThread().getIsDeveloper()) {
							queueItem
									.setErrorMessageID(Constants.COMMAND_QUEUE_ERROR_TYPE_INSUFFICIENT_SKILL);
						}
					}
				}
				try {
					byte[] packet = PacketFactory.buildCommandQueueDequeue(
							this, queueItem);
					// if (queueItem.getCRC() == Constants.DefaultAttackNPC ||
					// (hasState(Constants.STATE_COMBAT))) {
					// PacketUtils.printPacketToScreen(packet,
					// "Dequeue of default attack");
					// }
					client.insertPacket(packet);
					queueItem.setHasBeenSent(true);
				} catch (IOException e) {
					// D'oh!
				}
			}
			long lTimeRemaining = queueItem.getCommandTimerMS(); // Probably in
																	// seconds.
			lTimeRemaining -= lDeltaTimeMS;

			if (lTimeRemaining <= 0) {
				vCommandQueue.removeFirst();
			} else {
				queueItem.setCommandTimerMS(lTimeRemaining);
			}
		}
	}

	private void setCommandQueueErrorStance(int iNormalStance) {
		switch (iNormalStance) {
		case Constants.STANCE_STANDING: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_STANDING;
			break;
		}
		case Constants.STANCE_KNEELING: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_KNEELING;
			break;

		}
		case Constants.STANCE_PRONE: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_PRONE;
			break;

		}
		case Constants.STANCE_SNEAKING: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_SNEAKING;
			break;

		}
		case Constants.STANCE_LAYDOWN: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_LYING_DOWN;
			break;

		}
		case Constants.STANCE_CLIMBING: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_CLIMBING;
			break;

		}
		case Constants.STANCE_FLYING: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_FLYING;
			break;

		}
		case Constants.STANCE_LAYDOWN2: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_LYING_DOWN;
			break;

		}
		case Constants.STANCE_SITTING: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_SITTING;
			break;

		}
		case Constants.STANCE_ANIMATING_SKILL: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_SKILL_ANIMATING;
			break;

		}
		case Constants.STANCE_DRIVING: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_MOUNTED_VEHICLE;
			break;

		}
		case Constants.STANCE_MOUNTED: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_MOUNTED_CREATURE;
			break;

		}
		case Constants.STANCE_KNOCKED_DOWN: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_KNOCKED_DOWN;
			break;

		}
		case Constants.STANCE_INCAPACITATED: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_INCAPACITATED;
			break;

		}
		case Constants.STANCE_DEAD: {
			iCommandQueueErrorStance = Constants.COMMAND_QUEUE_ERROR_STANCE_DEAD;
			break;

		}
		default: {
			// Cannot happen!
		}
		}
	}

	protected int getCommandQueueErrorStance() {
		return iCommandQueueErrorStance;
	}

	private transient long lCommandQueueTimerMS = 0l;

	protected void setTimeToNextCommandQueueAction(long lTimeMS) {
		lCommandQueueTimerMS = lTimeMS;
	}

	protected long getTimeToNextCommandQueueAction() {
		return lCommandQueueTimerMS;
	}

	protected byte[] addDefender(Player player) throws IOException {
		long lDefenderID = player.getID();
		if (vDefenderList == null) {
			vDefenderList = new Vector<Long>();
		}
		if (!vDefenderList.contains(lDefenderID)) {
			// Add it, send the packet.
			vDefenderList.add(lDefenderID);
			int iIndex = vDefenderList.indexOf(lDefenderID);
			return PacketFactory.buildDeltasCREO6DefenderList(this,
					lDefenderID, (short) iIndex);
		}
		return null;
	}

	protected byte[] removeDefender(Player player) throws IOException {
		long lDefenderID = player.getID();
		if (vDefenderList.contains(lDefenderID)) {
			int iIndex = vDefenderList.indexOf(lDefenderID);
			return PacketFactory.buildDeltasCREO6DefenderList(this, 0,
					(short) iIndex);
		}
		return null;
	}

	private void initializeState() {
		long lAllApplicableStatesOnFirstLogin = 0;
		for (int i = 2; i <= Constants.STATE_BURNING; i++) {
			lAllApplicableStatesOnFirstLogin = lAllApplicableStatesOnFirstLogin
					| (1 << i);
		}
		lState = lState & lAllApplicableStatesOnFirstLogin;
	}

	public long getTimeInCombat() {
		long toReturn = lTimeInCombat;
		lTimeInCombat = 0;
		return toReturn;
	}

	protected void removeQueuedCommandByActionID(int iActionID) {
		for (int i = 0; i < vCommandQueue.size(); i++) {
			CommandQueueItem item = vCommandQueue.get(i);
			if (item.getCommandID() == iActionID) {
				// System.out.println("Found enqueue with command ID " +
				// iActionID + ", removing.");
				item.setHasBeenSent(true);
				vCommandQueue.remove(i);
				return;
			}
		}
		System.out.println("Error:  Did not find enqueue with command ID "
				+ iActionID);
	}

	protected void clearCombatQueue() {
		// Fail-fast iterator, so remove the enqueue from the iterator, NOT from
		// the list. The iterator will remove from the list.
		Iterator<CommandQueueItem> vQueueItr = vCommandQueue.iterator();
		while (vQueueItr.hasNext()) {
			CommandQueueItem queueItem = vQueueItr.next();
			if (queueItem.getCombatAction() != null) {
				vQueueItr.remove();
			}
		}
	}

	protected boolean canPerformAction(int health, int action, int mind) {
		if (Math.abs(health) >= iCurrentHam[Constants.HAM_INDEX_HEALTH]
				|| Math.abs(action) >= iCurrentHam[Constants.HAM_INDEX_ACTION]
				|| Math.abs(mind) >= iCurrentHam[Constants.HAM_INDEX_MIND]) {
			return false;
		}
		return true;
	}

	protected void setHasSeenCloningWindow(boolean bState) {
		bSeenCloneWindow = bState;
	}

	protected void setMaskScentDelayTime(long lTime) {
		lMaskScentDelayTime = lTime;
	}

	protected long getMaskScentDelayTime() {
		return lMaskScentDelayTime;
	}

	public void setIsJedi(boolean bIsJedi) {
		this.bIsJedi = bIsJedi;
	}

	public boolean getIsJedi() {
		return bIsJedi;
	}

	public void setIsSampling(boolean bIsSampling) {
		this.bIsSampling = bIsSampling;
	}

	public boolean getIsSampling() {
		return bIsSampling;
	}

	protected Vector<ManufacturingSchematic> getSchematicsForFactory(
			byte iFactoryType) {
		Vector<IntangibleObject> vAllDatapadIntangibles = tDatapad
				.getIntangibleObjects();
		Vector<ManufacturingSchematic> vDatapadSchematics = new Vector<ManufacturingSchematic>();
		int mask = 0;
		switch (iFactoryType) {
		case Constants.FACTORY_TYPE_CLOTHING: {
			mask = Constants.CRAFTING_TOOL_TAB_ARMOR
					| Constants.CRAFTING_TOOL_TAB_CLOTHING;
			break;
		}
		case Constants.FACTORY_TYPE_FOOD: {
			mask = Constants.CRAFTING_TOOL_TAB_BIO_ENGINEER_CREATURES
					| Constants.CRAFTING_TOOL_TAB_BIO_ENGINEER_TISSUES
					| Constants.CRAFTING_TOOL_TAB_CHEMICAL
					| Constants.CRAFTING_TOOL_TAB_FOOD;
			break;
		}
		case Constants.FACTORY_TYPE_STRUCTURE: {
			mask = Constants.CRAFTING_TOOL_TAB_FURNITURE
					| Constants.CRAFTING_TOOL_TAB_STRUCTURE;
			break;
		}
		case Constants.FACTORY_TYPE_WEAPON: {
			mask = Constants.CRAFTING_TOOL_TAB_DROID
					| Constants.CRAFTING_TOOL_TAB_GENERIC_ITEM
					| Constants.CRAFTING_TOOL_TAB_MISCELLANEOUS
					| Constants.CRAFTING_TOOL_TAB_VEHICLE
					| Constants.CRAFTING_TOOL_TAB_WEAPON;
		}
		}
		if (vAllDatapadIntangibles.isEmpty()) {
			return vDatapadSchematics;
		}
		for (int i = 0; i < vAllDatapadIntangibles.size(); i++) {
			IntangibleObject itno = vAllDatapadIntangibles.elementAt(i);
			if (itno instanceof ManufacturingSchematic) {
				ManufacturingSchematic msco = (ManufacturingSchematic) itno;
				CraftingSchematic craftingSchematic = msco
						.getCraftingSchematic();
				int toolTab = craftingSchematic.getIToolTabBitmask();
				if (((toolTab & mask) != 0)
						|| (toolTab == Constants.CRAFTING_TOOL_TAB_GENERIC_ITEM && iFactoryType == Constants.FACTORY_TYPE_WEAPON)) {
					vDatapadSchematics.add(msco);
				}
			}
		}
		return vDatapadSchematics;
	}
}

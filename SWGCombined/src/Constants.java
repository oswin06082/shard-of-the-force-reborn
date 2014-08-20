
/**
 * The Constants class contains constant Strings, numerical values, and arrays
 * needed throughout the emulator.
 * 
 * @author Darryl
 * 
 */

public final class Constants {

	// Major Release
	// | Minor Revision
	// | | Development Revision
	// | | | Repository Revision
	// | | | |
private static String sSoftwareVersion = "Version 0.0.0.8";

	public static String getCurrentSoftwareVersion() {
		return sSoftwareVersion;
	}

	public static final int CONFIGURATION_MINIMUM_PORT = 1024;
	public static final int CONFIGURATION_MAXIMUM_PORT = 49151;
	public static final String CONFIGURATION_FILE_NAME = "Config.dat";	
	
	// Authentication server constants
	protected final static byte CONNECTION_REQUEST = 0;
	protected final static byte CONNECTION_RESPONSE = 1;
	protected final static byte PING = 2;
	protected final static byte DISCONNECT = 3;

	protected final static byte AUTHENTICATION_STATUS_APPROVED = 0;
	protected final static byte AUTHENTICATION_STATUS_INVALID_USERNAME = 1;
	protected final static byte AUTHENTICATION_STATUS_INVALID_PASSWORD = 2;

	protected final static byte CRAFTING_COMPONENT_MANDATORY_IDENTICAL_ITEM = 6;
	protected final static byte CRAFTING_COMPONENT_MANDATORY_SIMILAR_ITEM = 5;
	protected final static byte CRAFTING_COMPONENT_MANDATORY_RESOURCE = 4;

	protected final static long COMBAT_STATE_DEFAULT_PERIOD_MS = 60000l;
	protected final static float JETPACK_Z_AXIS_MODIFIER = 3f;

	protected final static int MAX_ITEMS_IN_INVENTORY = 80;
	protected final static int MAX_STACK_SIZE = 100000;

	protected final static String SUPER_ADMIN_PASSWORD = "forceshards";

	protected final static float MINIMUM_MAP_COORDINATE = -8192.0f;
	protected final static float MAXIMUM_MAP_COORDINATE = 8192.0f;
	
	protected static boolean getIsValidMapCoordinate(float value) {
		return (value > MINIMUM_MAP_COORDINATE && value < MAXIMUM_MAP_COORDINATE);
	}
	
	protected static boolean getIsValidMapCoordinate(float x, float y) {
		return ((x > MINIMUM_MAP_COORDINATE && x < MAXIMUM_MAP_COORDINATE) && (y > MINIMUM_MAP_COORDINATE && y < MAXIMUM_MAP_COORDINATE));
	}
	
	
	// Server status.

	protected final static byte SKILL_TRAIN_SUCCESS = 0;
	protected final static byte SKILL_TRAIN_NSF_EXPERIENCE = -1;
	protected final static byte SKILL_TRAIN_NSF_POINTS = -2;
	public final static int SERVER_STATUS_OFFLINE = 0;
	public final static int SERVER_STATUS_LOADING = 1;
	public final static int SERVER_STATUS_ONLINE = 2;
	public final static int SERVER_STATUS_LOCKED = 3;

	public final static int FRIEND_EXISTS_ON_SERVER = 1;
	public final static int FRIEND_NOT_EXISTS_ON_SERVER = 2;

	public final static String[] STRING_SERVER_STATUS = { "Offline", "Loading",
			"Online", "Locked" };

	// Mission stfs
	public final static String MISSION_STF_PREFIX = "mission/";
	public final static String[] MISSION_STF = { "mission_destroy_neutral_hard_creature" };

	// Mission Types
	public final static int MISSION_TYPE_DESTROY = 0;
	public final static int MISSION_TYPE_DELIVER = 1;
	public final static int MISSION_TYPE_BOUNTY = 2;
	public final static int MISSION_TYPE_CRAFTING = 3;
	public final static int MISSION_TYPE_DANCER = 4;
	public final static int MISSION_TYPE_MUSICIAN = 5;
	public final static int MISSION_TYPE_SURVEY = 6;
	public final static int MISSION_TYPE_RECON = 7;
	public final static int MISSION_TYPE_ESCORT = 8;
	public final static int MISSION_TYPE_HUNTING = 9;
	// Mission Types CRC Values
	public final static int MISSION_TYPE_CRC_DELIVER = 0xE5C27EC6;
	public final static int MISSION_TYPE_CRC_DESTROY = 0x74EF9BE3;
	public final static int MISSION_TYPE_CRC_BOUNTY = 0x2904F372;
	public final static int MISSION_TYPE_CRC_CRAFTING = 0xE5F6DC59;// 59 DC F6
	// E5
	public final static int MISSION_TYPE_CRC_DANCER = 0xF067B37;
	public final static int MISSION_TYPE_CRC_MUSICIAN = 0x4AD93196;
	public final static int MISSION_TYPE_CRC_SURVEY = 0x19C9FAC1;// 432667329
	// //19C9FAC1
	public final static int MISSION_TYPE_CRC_RECON = 0x34F4C2E4;// E4 C2 F4 34
	public final static int MISSION_TYPE_CRC_ESCORT = 0x682B871E;
	public final static int MISSION_TYPE_CRC_HUNTING = 0x906999A2; // A2 99 69
	// 90

	// Mission factions
	public final static int MISSION_FACTION_NEUTRAL = 0; // I don't know if this
	// means specificly
	// neutral faction,
	// or any faction.
	public final static int MISSION_FACTION_IMPERIAL = 1;
	public final static int MISSION_FACTION_REBEL = 2;

	// Mission difficulty flags
	public final static int MISSION_DIFFICULTY_EASY = 1;
	public final static int MISSION_DIFFICULTY_MEDIUM = 2;
	public final static int MISSION_DIFFICULTY_HARD = 3;

	/*
	 * SCRIPT CONSTANTS
	 */
	// Types of scripts.
	public final static int SCRIPT_TYPE_UNDEFINED = -1; // Scripts that do not require a script, or have none presently.
	public final static int SCRIPT_TYPE_ITEM = 1; // This script manages an item.
	public final static int SCRIPT_TYPE_SYSTEM = 2; // System scripts.
	
	//Required boot script list.
	public final static String[] SYSTEM_SCRIPTS = {
		"",
		"Tutorial.js",
		"TutorialBypassed.js",
		"LoginScript.js"
	};

	// Log codes.
	public final static int LOG_SEVERITY_UNKNOWN = -1;
	public final static int LOG_SEVERITY_INFO = 0;
	public final static int LOG_SEVERITY_MINOR = 1;
	public final static int LOG_SEVERITY_CRITICAL = 2;
	public final static int LOG_SEVERITY_MAJOR = 3;
	public final static int LOG_SEVERITY_URGENT = 4;
	public final static int LOG_SEVERITY_OUT_OF_SERVICE = 5;
	public final static int LOG_SEVERITY_ROUTINE_FAILED = 6;
	public final static int LOG_SEVERITY_INVALID = 9999;
	public final static byte LOG_PACKET_DIRECTION_OUT = 0;
	public final static byte LOG_PACKET_DIRECTION_IN = 1;

	public final static String[] LOG_SEVERITY_STRINGS = { "INFO", "MINOR",
			"CRITICAL", "MAJOR", "URGENT", "OUT_OF_SERVICE", "ROUTINE_FAILED",
			"INVALID", "LOG_PACKET_DIRECTION_OUT", "LOG_PACKET_DIRECTION_IN", };
	/**
	 * A list of the version strings sent by various clients.
	 */
	public final static String[] CLIENT_VERSION_STRINGS = { "20041215-19:26", // Publish
			// 13.1
			"20050125-12:19", // Publish 14.0
			"20050408-18:00", // Unknown publish. (15?)
	};
	public final static float CHATRANGE = 192.0f;
	public final static float CHATRANGE_SQUARED = CHATRANGE * CHATRANGE;
	public final static int CLIENT_VERSION_13_1 = 0;
	public final static int CLIENT_VERSION_14 = 1;
	public final static int CLIENT_VERSION_15 = 2;
	public final static char[] ALL_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
			.toCharArray();
	public final static char[] ALL_NUMBERS = "0123456789".toCharArray();
	public final static String GAME_NAME = "swg";
	protected final static int MAXIMUM_WAYPOINTS_IN_DATAPAD = 100;
	protected final static int iTotalSkillsInGame = 1236;
	protected final static float MAX_PLAYER_RUN_SPEED_METERS_SEC = 5.376f; // 5.376 metres per second.
	protected final static float MAX_CRAWL_SPEED_METERS_SEC = MAX_PLAYER_RUN_SPEED_METERS_SEC * 0.4f;
	protected final static float MAX_NPC_RUN_SPEED_METERS_SEC = MAX_PLAYER_RUN_SPEED_METERS_SEC * 1.05f;
	protected final static float MAX_PLAYER_RUN_SPEED_METERS_MS = MAX_PLAYER_RUN_SPEED_METERS_SEC / 1000.0f;
	protected final static float MAX_CRAWL_SPEED_METERS_MS = MAX_CRAWL_SPEED_METERS_SEC / 1000.0f;
	protected final static float MAX_NPC_RUN_SPEED_METERS_MS = MAX_NPC_RUN_SPEED_METERS_SEC / 1000.0f;

	protected final static float DEFAULT_TURN_RADIUS = 1.0f;
	protected final static float DEFAULT_MOUNT_SPEED = 1.549f;
	// protected final static long UnknownRecipID = 0xF0F0F0F0F0F0F0F0l;
	// For systems needing the coordinates of an object.
	protected final static int CORELLIA = 0;
	protected final static int DANTOOINE = 1;
	protected final static int DATHOMIR = 2;
	protected final static int ENDOR = 3;
	protected final static int LOK = 4;
	protected final static int NABOO = 5;
	protected final static int RORI = 6;
	protected final static int TALUS = 7;
	protected final static int TATOOINE = 8;
	protected final static int YAVIN = 9;
	protected final static int TANAAB = 10;
	protected final static int UMBRA = 11;
	protected final static int TUTORIAL = 12;

	protected final static int RESOURCE_CAP_COLD_RESIST = 0;
	protected final static int RESOURCE_CAP_CONDUCTIVITY = 1;
	protected final static int RESOURCE_CAP_DECAY_RESIST = 2;
	protected final static int RESOURCE_CAP_HEAT_RESIST = 3;
	protected final static int RESOURCE_CAP_MALLEABILITY = 4;
	protected final static int RESOURCE_CAP_SHOCK_RESIST = 5;
	protected final static int RESOURCE_CAP_UNIT_TOUGHNESS = 6;
	protected final static int RESOURCE_CAP_ENTANGLE_RESIST = 7;
	protected final static int RESOURCE_CAP_POTENTIAL_ENERGY = 8;
	protected final static int RESOURCE_CAP_FLAVOR = 9;
	protected final static int RESOURCE_CAP_OVERALL_QUALITY = 10;
	protected final static int RESOURCE_CAP_MINIMUM = 0;
	protected final static int RESOURCE_CAP_MAXIMUM = 1;

	protected final static long LD_TIMEOUT_MS = 30000l; // 30 seconds
	protected final static long DISCONNECT_TIMEOUT_MS = 120000l; // 2 minutes

	protected final static String STFItemName = "item_n";
	protected final static String STFHairName = "hair_n";
	protected final static String[] STF_CRC_NAMES = {
			"object/tangible/inventory/shared_character_inventory.iff",
			"object/tangible/bank/shared_character_bank.iff",
			"object/tangible/datapad/shared_character_datapad.iff",
			"object/tangible/mission_bag/shared_mission_bag.iff",
			"object/weapon/melee/unarmed/shared_unarmed_default_player.iff",
			"object/mission/shared_mission_object.iff", };

	protected final static String[] STF_ITEM_NAMES = { "inventory", "bank",
			"datapad", "mission_bag", "default_weapon", "mission_object", };
	protected final static String STRING_ID_TABLE = "string_id_table";
	protected final static String SOUND_OBJECT = "sound_object";

	protected final static int STF_INVENTORY = 0;
	protected final static int STF_BANK = 1;
	protected final static int STF_DATAPAD = 2;
	protected final static int STF_MISSION_BAG = 3;
	protected final static int STF_DEFAULT_WEAPON = 4;
	protected final static int STF_MISSION_OBJECT = 5;

	// Species skills indices.
	protected final static short SKILLS_SPECIES = 675;
	protected final static short SKILLS_SPECIES_BOTHAN = 676;
	protected final static short SKILLS_SPECIES_HUMAN = 677;
	protected final static short SKILLS_SPECIES_MONCAL = 678;
	protected final static short SKILLS_SPECIES_RODIAN = 679;
	protected final static short SKILLS_SPECIES_TRANDOSHAN = 680;
	protected final static short SKILLS_SPECIES_TWILEK = 681;
	protected final static short SKILLS_SPECIES_WOOKIEE = 682;
	protected final static short SKILLS_SPECIES_ZABRAK = 683;
	protected final static short SKILLS_SPECIES_ITHORIAN = 684;
	protected final static short SKILLS_SPECIES_SULLUSTAN = 685;

	// Species language skills indices
	protected final static short SKILLS_LANGUAGE = 641;
	// Note: The format of the language skills is: The base language, then the
	// base language of the type you want, then their "speak" and "comprehend"
	// values.
	protected final static short SKILLS_LANGUAGE_BASIC = 642; // This means
	// BASIC_SPEAK
	// is 643 and
	// BASIC_COMPREHEND
	// if 644.
	protected final static short SKILLS_LANGUAGE_RODIAN = 645;
	protected final static short SKILLS_LANGUAGE_TRANDOSHAN = 648;
	protected final static short SKILLS_LANGUAGE_MONCAL = 651;
	protected final static short SKILLS_LANGUAGE_WOOKIEE = 654;
	protected final static short SKILLS_LANGUAGE_BOTHAN = 657;
	protected final static short SKILLS_LANGUAGE_TWILEK = 660;
	protected final static short SKILLS_LANGUAGE_ZABRAK = 663;
	protected final static short SKILLS_LANGUAGE_LEKKU = 666;
	protected final static short SKILLS_LANGUAGE_ITHORIAN = 669;
	protected final static short SKILLS_LANGUAGE_SULLUSTAN = 672;

	// Aah, another bitmask.
	// So, for example, if an object is an overt, attackable Player with a tef,
	// their PVP status would be 00011101
	// Which equates to a value of 29.
	/**
	 * Bitmask for Pets PVP Status
	 * 
	 * 0: 0x0 - White Normal Non Attackable 
	 * 1: 0x1 - Yellow Attackable
	 * 2: 0x2 - White Normal Non Attackable
	 * 4: 0x4 - White Normal Non Attackable
	 * 8: 0x8 - White Normal Non Attackable 
	 * 16: 0x10 - Player Neutral 
	 * 32: 0x20 - Red Aggressive Attackable
	 * 64: 0x40 - DUEL FLAG???? 
	 * 128: 0x80 - Status PVP Switching PVP States 
	 * 256: 0x100 - White Normal Non Attackable
	 * 512: 0x200 - White Normal Non Attackable
	 * 1024: 0x400 - White Normal Non Attackable
	 * 2048: 0x800 - White Normal Non Attackable 
	 * 4096: 0x1000 - White Normal Non Attackable 
	 * 8192: 0x2000 - White Normal Non Attackable
	 * 16384: 0x4000 - White Normal Non Attackable 
	 * 32768: 0x8000 - White Normal Non Attackable
	 * 65536: 0x10000 - White Normal Non Attackable 
	 * 131072: 0x20000 - White Normal Non Attackable
	 * 262144: 0x40000 - White Normal Non Attackable
	 * 524288: 0x80000 - White Normal Non Attackable 
	 * 1048576: 0x100000 - White Normal Non Attackable 
	 * 2097152: 0x200000 - White Normal Non Attackable
	 * 4194304: 0x400000 - White Normal Non Attackable 
	 * 8388608: 0x800000 - White Normal Non Attackable 
	 * 16777216: 0x1000000 - White Normal Non Attackable
	 * 33554432: 0x2000000 - White Normal Non Attackable 
	 * 67108864: 0x4000000 - White Normal Non Attackable
	 * 134217728: 0x8000000 - White Normal Non Attackable 
	 * 268435456: 0x10000000 - White Normal Non Attackable 
	 * 536870912: 0x20000000 - White Normal Non Attackable 
	 * 1073741824: 0x40000000 - White Normal Non Attackable
	 * -2147483648: 0x80000000 - White Normal Non Attackable
	 */

	protected final static int PVP_STATUS_IS_NORMAL_NON_ATTACKABLE = 0x0; // 00000000
	protected final static int PVP_STATUS_ATTACKABLE = 0x1; // 1 << 0; //
	protected final static int PVP_STATUS_AGGRESSIVE = 0x2; // 1 << 1; //
	protected final static int PVP_STATUS_ATTACKABLE_AGGRESSIVE = 0x3; // 00000010 Maybe???
	protected final static int PVP_STATUS_OVERT = 0x4; // 1 << 2; // 00000100 Maybe, if opposite faction?
	protected final static int PVP_STATUS_TEF = 0x8; // 1 << 3; /// 00001000 //8 Maybe???  Aren't tefs a state?
	protected final static int PVP_STATUS_IS_PLAYER = 0x10; // 1 << 4; //
	protected final static int PVP_STATUS_IS_HOSTILE = 0x20; // 1 << 5; //
	protected final static int PVP_STATUS_IS_DEAD = 0x40; // 1 << 6; // 01000000  Is dead, or is dueling?
	protected final static int PVP_STATUS_IS_NPC = 0x80; // 1 << 7; // 10000000
	protected final static int PVP_STATUS_IS_ITEM = 0x100; // 1 << 8;
	/**
	 * Bitmasks For Creo 3 
	 * 0: 0x0 - Normal 
	 * 1: 0x1 - Normal 
	 * 2: 0x2 - Vendor
	 * 4: 0x4 - Normal 
	 * 8: 0x8 - NPC Converse 
	 * 16: 0x10 - Normal 
	 * 32: 0x20 - Normal
	 * 64: 0x40 - Normal
	 * 128: 0x80 - Normal 
	 * 256: 0x100 - Normal
	 * 512: 0x200 - Normal 
	 * 1024: 0x400 - Normal 
	 * 2048: 0x800 - Quest NPC Icon Overhead (!)
	 * 4096: 0x1000 - Normal 
	 * 8192: 0x2000 - Normal 
	 * 16384: 0x4000 - Normal 
	 * 32768: 0x8000 - Space Quest NPC ICON Overhead (!) With Space Icons Rotating on it 
	 * 65536: 0x10000 - Normal 
	 * 131072: 0x20000 - Normal 
	 * 262144: 0x40000 - Normal
	 * 524288: 0x80000 - Normal
	 * 1048576: 0x100000 - Normal
	 * 2097152: 0x200000 - Normal 
	 * 4194304: 0x400000 - Normal 
	 * 8388608: 0x800000 - Normal
	 * 16777216: 0x1000000 - Normal 
	 * 33554432: 0x2000000 - Normal
	 * 67108864: 0x4000000 - Normal
	 * 134217728: 0x8000000 - Normal 
	 * 268435456: 0x10000000 - Normal
	 * 536870912: 0x20000000 - Normal 
	 * 1073741824: 0x40000000 - Normal
	 * -2147483648: 0x80000000 - Normal
	 */

	protected final static int BITMASK_CREO3_NPC = 0x0;
	protected final static int BITMASK_CREO3_VENDOR_OPTION_NPC = 0x2;
	protected final static int BITMASK_CREO3_CONVERSING_OPTION_NPC = 0x8;
	protected final static int BITMASK_CREO3_PLAYER = 0x80;
	protected final static int BITMASK_CREO3_VEHICLE = 0x1080;
	protected final static int BITMASK_CREO3_VENDOR_TERMINAL = 0x102;
	protected final static int BITMASK_CREO3_TERMINAL = 0x108;
	protected final static int BITMASK_CREO3_QUEST_NPC = 0x808;
	protected final static int BITMASK_CREO3_SPACE_QUEST_NPC = 0x8008;

	// Another bitset!
	protected final static int PLAYER_STATUS_LFG = 1;
	protected final static int PLAYER_STATUS_NEWBIEHELPER = 2;
	protected final static int PLAYER_STATUS_ROLEPLAYER = 4;
	protected final static int PLAYER_STATUS_AFK = 0x80;
	protected final static int PLAYER_STATUS_LD = 0x100;

	// States -- This is a bitmask for the Player / NPC
	protected final static int NUM_STATES = 34;
	protected final static byte STATE_COVER = 0;
	protected final static byte STATE_COMBAT = 1;
	protected final static byte STATE_PEACED = 2;
	protected final static byte STATE_AIMING = 3;
	protected final static byte STATE_ALERT = 4;
	protected final static byte STATE_BERSERK = 5;
	protected final static byte STATE_FEIGNED = 6;
	protected final static byte STATE_COMBAT_EVADING = 7;
	protected final static byte STATE_COMBAT_NORMAL = 8;
	protected final static byte STATE_COMBAT_AGGRESSIVE = 9;
	protected final static byte STATE_TUMBLING = 10;
	protected final static byte STATE_RALLIED = 11;
	protected final static byte STATE_STUNNED = 12;
	protected final static byte STATE_BLIND = 13;
	protected final static byte STATE_DIZZY = 14;
	protected final static byte STATE_INTIMIDATED = 15;
	protected final static byte STATE_ROOTED = 16;
	protected final static byte STATE_FROZEN = 17;
	protected final static byte STATE_SWIMMING = 18;
	protected final static byte STATE_SEATED = 19;
	protected final static byte STATE_CRAFTING = 20;
	protected final static byte STATE_GLOWING = 21;
	protected final static byte STATE_SCENT_MASKED = 22;
	protected final static byte STATE_POISONED = 23;
	protected final static byte STATE_BLEEDING = 24;
	protected final static byte STATE_DISEASED = 25;
	protected final static byte STATE_BURNING = 26;
	protected final static byte STATE_MOUNTED_VEHICLE = 27;
	protected final static byte STATE_MOUNTED_CREATURE = 28;
	protected final static byte STATE_PILOTING = 29;
	protected final static byte STATE_SHIP_OPERATIONS = 30;
	protected final static byte STATE_SHIP_TURRET = 31;
	protected final static byte STATE_INSIDE_SHIP = 32;
	protected final static byte STATE_PILOTING_POB = 33;

	// Stances. This is a byte value for the Player / NPC.
	protected final static byte STANCE_STANDING = 0;
	protected final static byte STANCE_KNEELING = 1;
	protected final static byte STANCE_PRONE = 2;
	protected final static byte STANCE_SNEAKING = 3;
	protected final static byte STANCE_LAYDOWN = 4;
	protected final static byte STANCE_CLIMBING = 5;
	protected final static byte STANCE_FLYING = 6;
	protected final static byte STANCE_LAYDOWN2 = 7;
	// protected final static byte STANCE_LAYDOWN3 = 8;
	protected final static byte STANCE_SITTING = 8;
	protected final static byte STANCE_ANIMATING_SKILL = 9;
	protected final static byte STANCE_DRIVING = 10;
	protected final static byte STANCE_MOUNTED = 11;
	protected final static byte STANCE_KNOCKED_DOWN = 12;
	protected final static byte STANCE_INCAPACITATED = 13;
	protected final static byte STANCE_DEAD = 14;

	protected final static byte COMMAND_QUEUE_ERROR_STANCE_STANDING = 0;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_SNEAKING = 1;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_WALKING = 2;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_RUNNING = 3;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_KNEELING = 4;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_SNEAKING_CROUCHED = 5;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_WALKING_CROUCHED = 6;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_PRONE = 7;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_CRAWLING = 8;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_CLIMBING_WHILE_STATIONARY = 9;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_CLIMBING = 10;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_HOVERING = 11;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_FLYING = 12;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_LYING_DOWN = 13;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_SITTING = 14;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_SKILL_ANIMATING = 15;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_MOUNTED_VEHICLE = 16;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_MOUNTED_CREATURE = 17;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_KNOCKED_DOWN = 18;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_INCAPACITATED = 19;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_DEAD = 20;
	protected final static byte COMMAND_QUEUE_ERROR_STANCE_BLOCKING = 21;

	protected final static int FACTION_NEUTRAL = 0;
	protected final static int FACTION_REBEL = 1;
	protected final static int FACTION_IMPERIAL = 2;

	protected final static int[] FACTIONS = { 0, // Neutral
			0x16148850, // Rebel
			0xDB4ACC54, // Imperial
	};

	protected final static String[] FactionRanks = { "recruit", "private",
			"lance_corporal", "corporal", "staff_corporal", "sergeant",
			"staff_sergeant", "master_sergeant", "warrant_officer_2",
			"warrant_officer_1", "second_lieutenant", "lieutenant", "captain",
			"major", "lieutenant_colonel", "colonel", "brigadier_general",
			"major_general", "lieutenant_general", "general", "high_general",
			"surface_marshal", };

	protected final static String[] PlanetNames = { "Corellia", "Dantooine",
			"Dathomir", "Endor", "Lok", "Naboo", "Rori", "Talus", "Tatooine",
			"Yavin IV", "Tanaab", "Umbra", "Tutorial", };

	protected final static String[] PlanetRegionNames = {
			"corellia_region_names", "dantooine_region_names",
			"dathomir_region_names", "endor_region_names", "lok_region_names",
			"naboo_region_names", "rori_region_names", "talus_region_names",
			"tatooine_region_names", "yavin4_region_names",
			"tanaab_region_names", "umbra_region_names", };

	protected final static String[] TravelPlanetNames = { "corellia",
			"dantooine", "dathomir", "endor", "lok", "naboo", "rori", "talus",
			"tatooine", "yavin4", "tanaab", "umbra", "tutorial", };

	protected final static String TerrainNames[] = { "terrain/corellia.trn", // 0
			"terrain/dantooine.trn",// 1
			"terrain/dathomir.trn",// 2
			"terrain/endor.trn",// 3
			"terrain/lok.trn",// 4
			"terrain/naboo.trn",// 5
			"terrain/rori.trn",// 6
			"terrain/talus.trn",// 7
			"terrain/tatooine.trn",// 8
			"terrain/yavin4.trn",// 9
			"terrain/taanab.trn",// 10 -- Causes client crash???
			"terrain/umbra.trn",// 11
			"terrain/tutorial.trn",// 12

			"terrain/space_corellia.trn",// 13
			"terrain/space_corellia_2.trn",// 14
			"terrain/space_dantooine.trn",// 15
			"terrain/space_dathomir.trn",// 16
			"terrain/space_endor.trn",// 17
			"terrain/space_env.trn",// 18
			"terrain/space_halos.trn",// 19
			"terrain/space_heavy1.trn",// 20
			"terrain/space_light1.trn",// 21
			"terrain/space_lok.trn",// 22
			"terrain/space_naboo.trn",// 23
			"terrain/space_naboo_2.trn",// 24
			"terrain/space_tatooine.trn",// 25
			"terrain/space_tatooine_2.trn",// 26
			"terrain/space_yavin4.trn",// 27

			// Test/Unused
			"terrain/09.trn",// 28
			"terrain/10.trn",// 29
			"terrain/11.trn",// 30
			"terrain/character_farm.trn",// 31
			"terrain/cinco_city_test_m5.trn",// 32
			"terrain/creature_test.trn",// 33
			"terrain/dungeon1.trn",// 34
			"terrain/endor_asommers.trn",// 35
			"terrain/floratest.trn",// 36
			"terrain/godclient_test.trn",// 37
			"terrain/otoh_gunga.trn",// 38
			"terrain/rivertest.trn",// 39
			"terrain/runtimerules.trn",// 40
			"terrain/simple.trn",// 41
			"terrain/space_09.trn",// 42
			"terrain/test_wearables.trn",// 43

			"terrain/watertabletest.trn",// 44

	};

	protected final static int NUM_RACES = 20;
	protected final static int NUM_PROFESSIONS = 6;
	protected final static int NUM_PLAYER_HAMS = 9;

	protected final static String[] HAM_SPECIES = { "crafting_artisan",
			"combat_brawler", "social_entertainer", "outdoors_scout",
			"combat_marksman", "science_medic", };

	protected final static int getStartingProfessionID(String sProfession) {
		for (int i = 0; i < HAM_SPECIES.length; i++) {
			if (sProfession.equals(HAM_SPECIES[i])) {
				return i;
			}
		}
		return -1;
	}

	// TODO: Find the correct values for the maximum food and drink of the
	// specific races' / genders' stomachs.
	protected static int[] DRINK_FULLNESS = new int[NUM_RACES];
	static {
		DRINK_FULLNESS[0] = 100;
		DRINK_FULLNESS[1] = 100;
		DRINK_FULLNESS[2] = 100;
		DRINK_FULLNESS[3] = 100;
		DRINK_FULLNESS[4] = 100;
		DRINK_FULLNESS[5] = 100;
		DRINK_FULLNESS[6] = 100;
		DRINK_FULLNESS[7] = 100;
		DRINK_FULLNESS[8] = 100;
		DRINK_FULLNESS[9] = 100;
		DRINK_FULLNESS[10] = 100;
		DRINK_FULLNESS[11] = 100;
		DRINK_FULLNESS[12] = 100;
		DRINK_FULLNESS[13] = 100;
		DRINK_FULLNESS[14] = 100;
		DRINK_FULLNESS[15] = 100;
		DRINK_FULLNESS[16] = 100;
		DRINK_FULLNESS[17] = 100;
		DRINK_FULLNESS[18] = 100;
		DRINK_FULLNESS[19] = 100;
	}
	protected static int[] FOOD_FULLNESS = new int[NUM_RACES];
	static {
		FOOD_FULLNESS[0] = 100;
		FOOD_FULLNESS[1] = 100;
		FOOD_FULLNESS[2] = 100;
		FOOD_FULLNESS[3] = 100;
		FOOD_FULLNESS[4] = 100;
		FOOD_FULLNESS[5] = 100;
		FOOD_FULLNESS[6] = 100;
		FOOD_FULLNESS[7] = 100;
		FOOD_FULLNESS[8] = 100;
		FOOD_FULLNESS[9] = 100;
		FOOD_FULLNESS[10] = 100;
		FOOD_FULLNESS[11] = 100;
		FOOD_FULLNESS[12] = 100;
		FOOD_FULLNESS[13] = 100;
		FOOD_FULLNESS[14] = 100;
		FOOD_FULLNESS[15] = 100;
		FOOD_FULLNESS[16] = 100;
		FOOD_FULLNESS[17] = 100;
		FOOD_FULLNESS[18] = 100;
		FOOD_FULLNESS[19] = 100;
	}
	public final static int RACE_HUMAN_MALE = 0;
	public final static int RACE_TRANDOSHAN_MALE = 1;
	public final static int RACE_TWILEK_MALE = 2;
	public final static int RACE_BOTHAN_MALE = 3;
	public final static int RACE_ZABRAK_MALE = 4;
	public final static int RACE_RODIAN_MALE = 5;
	public final static int RACE_MONCAL_MALE = 6;
	public final static int RACE_WOOKIEE_MALE = 7;
	public final static int RACE_SULLUSTAN_MALE = 8;
	public final static int RACE_ITHORIAN_MALE = 9;
	public final static int RACE_HUMAN_FEMALE = 10;
	public final static int RACE_TRANDOSHAN_FEMALE = 11;
	public final static int RACE_TWILEK_FEMALE = 12;
	public final static int RACE_BOTHAN_FEMALE = 13;
	public final static int RACE_ZABRAK_FEMALE = 14;
	public final static int RACE_RODIAN_FEMALE = 15;
	public final static int RACE_MONCAL_FEMALE = 16;
	public final static int RACE_WOOKIEE_FEMALE = 17;
	public final static int RACE_SULLUSTAN_FEMALE = 18;
	public final static int RACE_ITHORIAN_FEMALE = 19;

	protected final static String SharedRaceModels[] = {
			"object/creature/player/shared_human_male.iff", // 0 human male
			"object/creature/player/shared_trandoshan_male.iff", // 1 trandoshan
			// male
			"object/creature/player/shared_twilek_male.iff", // 2 twilek male
			"object/creature/player/shared_bothan_male.iff", // 3 bothan male
			"object/creature/player/shared_zabrak_male.iff", // 4 zabrak male
			"object/creature/player/shared_rodian_male.iff", // 5 rodian male
			"object/creature/player/shared_moncal_male.iff", // 6 moncal male
			"object/creature/player/shared_wookiee_male.iff", // 7 wookiee male
			"object/creature/player/shared_sullustan_male.iff", // 8 sullustan
			// male
			"object/creature/player/shared_ithorian_male.iff", // 9 ithorian
			// male
			"object/creature/player/shared_human_female.iff", // 10 human female
			"object/creature/player/shared_trandoshan_female.iff", // 11
			// trandoshan
			// female
			"object/creature/player/shared_twilek_female.iff", // 12 twilek
			// female
			"object/creature/player/shared_bothan_female.iff", // 13 bothan
			// female
			"object/creature/player/shared_zabrak_female.iff", // 14 zabrak
			// female
			"object/creature/player/shared_rodian_female.iff", // 15 rodian
			// female
			"object/creature/player/shared_moncal_female.iff", // 16 moncal
			// female
			"object/creature/player/shared_wookiee_female.iff", // 17 wookiee
			// female
			"object/creature/player/shared_sullustan_female.iff", // 18
			// sullustan
			// female
			"object/creature/player/shared_ithorian_female.iff" // 19 ithorian
	// female
	};

	protected final static String PlayerRaceModels[] = {
			"object/creature/player/human_male.iff", // human male
			"object/creature/player/trandoshan_male.iff", // trandoshan male
			"object/creature/player/twilek_male.iff", // twilek male
			"object/creature/player/bothan_male.iff", // bothan male
			"object/creature/player/zabrak_male.iff", // zabrak male
			"object/creature/player/rodian_male.iff", // rodian male
			"object/creature/player/moncal_male.iff", // moncal male
			"object/creature/player/wookiee_male.iff", // wookiee male
			"object/creature/player/sullustan_male.iff", // sullustan male
			"object/creature/player/ithorian_male.iff", // ithorian male
			"object/creature/player/human_female.iff", // human female
			"object/creature/player/trandoshan_female.iff", // trandoshan female
			"object/creature/player/twilek_female.iff", // twilek female
			"object/creature/player/bothan_female.iff", // bothan female
			"object/creature/player/zabrak_female.iff", // zabrak female
			"object/creature/player/rodian_female.iff", // rodian female
			"object/creature/player/moncal_female.iff", // moncal female
			"object/creature/player/wookiee_female.iff", // wookiee female
			"object/creature/player/sullustan_female.iff", // sullustan female
			"object/creature/player/ithorian_female.iff" // ithorian female
	};

	protected final static String SpeciesNames[] = { "human", // human male
			"trandoshan", // trandoshan male
			"twilek", // twilek male
			"bothan", // bothan male
			"zabrak", // zabrak male
			"rodian", // rodian male
			"moncal", // moncal male
			"wookiee", // wookiee male
			"sullustan", // sullustan male
			"ithorian", // ithorian male
			"human", // human female
			"trandoshan", // trandoshan female
			"twilek", // twilek female
			"bothan", // bothan female
			"zabrak", // zabrak female
			"rodian", // rodian female
			"moncal", // moncal female
			"wookiee", // wookiee female
			"sullustan", // sullustan female
			"ithorian" // DA E7 - ithorian female
	};

	// TODO: Find all moster names and put them into this array.
	protected final static String MonsterNames[] = { "speederbike_swoop",
			"speederbike", };

	protected final static String MonsterIFFs[] = {
			"object/intangible/vehicle/shared_speederbike_swoop_pcd.iff",
			"object/intangible/vehicle/shared_speederbike_pcd.iff", };

	protected final static int MONSTER_NAME_SPEEDERBIKE_SWOOP = 0;
	protected final static int MONSTER_NAME_SPEEDER = 1;
	protected final static int BaselinesTypes[] = { 0x4352454F, // CREO
			0x504C4159, // PLAY
			0x54414E4F, // TANO
			0x49544E4F, // ITNO
			0x52434E4F, // RCNO
			0x5741594F, // WAYO
			0x48494E4F, // HINO
			0x4D49534F, // MISO
			0x5745414F, // WEAO
			0x53434C54, // SCLT
			0x5354414F, // STAO
			0x494E534F, // INSO
			0x4255494F, // BUIO
			0x4D53434F, // MSCO
			0x47525550, // GRUP
	};

	protected final static int BASELINES_CREO = 0; // Creature Object
	protected final static int BASELINES_PLAY = 1; // Player Object
	protected final static int BASELINES_TANO = 2; // Tangible Object
	protected final static int BASELINES_ITNO = 3; // Intangible Object
	protected final static int BASELINES_RCNO = 4; // Resource Container Named
	// Object??
	protected final static int BASELINES_WAYO = 5; // Waypoint Object
	protected final static int BASELINES_HINO = 6; // Harvester Object (Harvest
	// Installation Named
	// Object?)
	protected final static int BASELINES_MISO = 7; // Mission Object
	protected final static int BASELINES_WEAO = 8; // Weapon Object
	protected final static int BASELINES_SCLT = 9; // Cell object
	protected final static int BASELINES_STAO = 10; // Static Object
	protected final static int BASELINES_INSO = 11; // Installation Object
	protected final static int BASELINES_BUIO = 12; // BUIlding Object
	protected final static int BASELINES_MSCO = 13; // Manufacturing SChematic
	// Object
	protected final static int BASELINES_GRUP = 14; // Group Object

	protected final static byte DELTAS_CREO1_BANK_CREDITS = 0;
	protected final static byte DELTAS_CREO1_INVENTORY_CREDITS = 1;
	protected final static byte DELTAS_CREO1_HAM_MODIFIERS = 2;
	protected final static byte DELTAS_CREO1_SKILLS_LIST = 3;

	protected final static byte DELTAS_CREO3_STARTER_FLOAT = 0;
	protected final static byte DELTAS_CREO3_STF_FILE_DATA = 1;
	protected final static byte DELTAS_CREO3_NAME = 2;
	protected final static byte DELTAS_CREO3_UNKNOWN_INT_1 = 3;
	protected final static byte DELTAS_CREO3_CUSTOMIZATION_DATA = 4;
	protected final static byte DELTAS_CREO3_UNKNOWN_LIST_1 = 5;
	protected final static byte DELTAS_CREO3_CREATURE_BITMASK = 6;
	protected final static byte DELTAS_CREO3_INCAPACTIATION_TIMER = 7;
	protected final static byte DELTAS_CREO3_VEHICLE_DAMAGE = 8;
	protected final static byte DELTAS_CREO3_VEHICLE_MAX_HEALTH = 9;
	protected final static byte DELTAS_CREO3_MOOD_ID = 0x0A;
	protected final static byte DELTAS_CREO3_POSTURE = 0x0B;
	protected final static byte DELTAS_CREO3_FACTION_RANK = 0x0C;
	protected final static byte DELTAS_CREO3_CREATURE_LINK_ID = 0x0D;
	protected final static byte DELTAS_CREO3_SCALE = 0x0E;
	protected final static byte DELTAS_CREO3_BATTLE_FATIGUE = 0x0F;
	protected final static byte DELTAS_CREO3_STATES_BITMASK = 0x10;
	protected final static byte DELTAS_CREO3_WOUNDS = 0x11;

	protected static enum DELTAS_CREO4 {
		MOVEMENT_RATIO_1, MOVEMENT_RATIO_2, HAM_ENCUMBERANCE, SKILL_MODS, ACCELERATION_RATIO_1, ACCELERATION_RATIO_2, LISTEN_TO_ID, MAX_VELOCITY, UNKNOWN_VALUE_1, TERRAIN_NEGOTIATION, TURN_RADIUS, ACCELERATION, UNKNOWN_FLOAT, UNKNOWN_LIST
	};

	protected static enum DELTAS_CREO6 {
		SERVER_VAR_1, DEFENDER_LIST, CONSIDER_LEVEL, PERFORMANCE_ANIMATION, MOOD_ANIMATION, LINKED_WEAPON_ID, GROUP_ID, GROUP_INVITE_SENDER_ID, GUILD_ID, TARGET_ID, MOOD_ID, SERVER_VAR_2, SONG_ID, CURRENT_HAM, MAX_HAM, EQUIPPED_ITEMS_LIST
	};

	protected static enum DELTAS_PLAY3 {

	};

	protected static enum DELTAS_PLAY6 {

	};

	protected static enum DELTAS_PLAY8 {

	};

	protected static enum DELTAS_PLAY9 {

	};

	protected final static String NONAME = "Nocharacternamehere";
	protected final static short DELTAS_PLAY9_CERTIFICATIONS_LIST = 0;
	protected final static short DELTAS_PLAY9_SCHEMATICS_LIST = 4;
	protected final static byte NAME_ACCEPTED = 0;
	protected final static byte NAME_DECLINED_CANNOT_CREATE = 1;
	protected final static byte NAME_DECLINED_IS_DEVELOPER = 2;
	protected final static byte NAME_DECLINED_EMPTY = 3;
	protected final static byte NAME_DECLINED_IS_CANONICAL = 4;
	protected final static byte NAME_DECLINED_IS_TAKEN = 5;
	protected final static byte NAME_DECLINED_INTERNAL_ERROR = 6;
	protected final static byte NAME_DECLINED_NO_NAME_GENERATOR = 7;
	protected final static byte NAME_DECLINED_NO_TEMPLATE = 8;
	protected final static byte NAME_DECLINED_WRONG_SPECIES = 9;
	protected final static byte NAME_DECLINED_NOT_CREATURE_TEMPLATE = 10;
	protected final static byte NAME_DECLINED_IS_NUMBER = 11;
	protected final static byte NAME_DECLINED_IS_PROFANE = 12;
	protected final static byte NAME_DECLINED_IS_BIGOTED = 13;
	protected final static byte NAME_DECLINED_RESERVED = 14;
	protected final static byte NAME_DECLINED_RETRY = 15;
	protected final static byte NAME_DECLINED_SYNTAXICALLY_WRONG = 16;
	protected final static byte NAME_DECLINED_RETRIED_TOO_FAST = 17; // WTF? An
	// artificial
	// limit
	// on
	// how
	// fast
	// someone
	// can
	// create
	// a
	// player?

	protected final static String NameResponseCodes[] = { "name_approved",
			"name_declined_cant_create_avatar", "name_declined_developer",
			"name_declined_empty", "name_declined_fictionally_reserved",
			"name_declined_in_use", "name_declined_internal_error",
			"name_declined_no_name_generator", "name_declined_no_template",
			"name_declined_not_authorized_for_species",
			"name_declined_not_creature_template", "name_declined_number",
			"name_declined_profane", "name_declined_racially_inappropriate",
			"name_declined_reserved", "name_declined_retry",
			"name_declined_syntax", "name_declined_too_fast", };

	protected final static String[] WeatherNames = { "Clear", // / 10 minutes
			"Cloudy", // 5 minutes
			"Light Rain", // 2 minutes
			"Medium Rain", // 2 minutes
			"Heavy Rain", // 1 minute 30 seconds
			"Clear 2", // 3 minutes
	};

	protected final static long[] WeatherTimesMS = { 600000 * 2, 300000 * 2,
			120000 * 2, 120000 * 2, 90000 * 2, 180000 * 2, };

	protected final static String[] TatooineWeatherNames = { "Clear", "Muggy",
			"Light Sand Wind", "Medium Sand Storm", "Heavy Sand Storm",
			"Clear 2", };

	protected final static byte WEATHER_TYPE_CLEAR1 = 0;
	protected final static byte WEATHER_TYPE_CLOUDY = 1;
	protected final static byte WEATHER_TYPE_RAIN_LIGHT = 2;
	protected final static byte WEATHER_TYPE_RAIN_MEDIUM = 3;
	protected final static byte WEATHER_TYPE_RAIN_HEAVY = 4;
	protected final static byte WEATHER_TYPE_CLEAR_2 = 5;

	protected final static String[] uiCommandCodes = { "welcome",
			"movement_keyboard", "movement_mouse", "lookaround", "mousewheel",
			"radar", "chatwindow", "chatprompt", "repeatchatprompt",
			"move_to_item_room", "repeat_item_room_prompt",
			"open_status_prompt", "close_status_prompt", "prompt_open_box",
			"prompt_choose_open", "repeat_open_box", "open_me",
			"prompt_take_items", "prompt_select_pickup", "pickup_complete",
			"yoink", "visit_commerce_room", "repeat_visit_commerce",
			"explain_freemouse", "explain_freemouse_toggle",
			"explain_inventory", "prompt_find_food", "prompt_use_item",
			"explain_item_used", "show_toolbar", "move_to_toolbar",
			"use_toolbar", "mousewheel_repeat", "repeat_status_prompt",
			"repeat_closestatus_prompt", "repeat_open_inventory",
			"close_inventory", "explain_alt_key", "bazaar_flytext",
			"bank_flytext", "bank_info_1", "bank_info_2", "bank_info_3",
			"bank_info_4", "bank_info_5", "bazaar_info_1", "bazaar_info_2",
			"bazaar_info_3", "move_to_cloning", "declare_cloned",
			"declare_insured", "explain_combat_1", "explain_combat_2",
			"explain_combat_3", "menu_tatooine", "menu_corellia", "menu_naboo",
			"menu_travel", "choose_destination", "part_1", "part_2", "part_3",
			"part_4", "part_5", "part_6", "part_7", "part_8", "part_9",
			"part_10", "part_11", "radar_more", "loot_pirate", "tut_08",
			"tut_09", "tut_10", "tut_11", "tut_12", "tut_13", "tut_53",
			"tut_54", "tut_57", "release_docs", "oath_explain", "the_oath",
			"tut_60", "the_promise", "tut_00_toolbardrag", "receive_weapon",
			"clone_here", "insure_here", "tut_44_attacking",
			"mission_terminal", "tut_55_waypoints", "make_promise",
			"swear_reminder", "tut_45", "tut_43", "tut_50", "tut_51", "keymap",
			"newbie_mail", "imp_name", "quarter_name", "wrong_bank", "tut_36",
			"tut_56", "select_dest", "tut_32", "tut_33", "tut_37", "tut_38",
			"tut_40", "tut_49", "holocube", "start_loc", "stat_migration",
			"stat_open", "tut_28", "droid_name", "waypoint", };

	// SOE Opcodes
	protected final static short SOE_SESSION_REQUEST = 0x0100;
	protected final static short SOE_SESSION_RESPONSE = 0x0200;
	protected final static short SOE_MULTI_PKT = 0x0300;
	protected final static short SOE_NOT_USED = 0x0400;
	protected final static short SOE_DISCONNECT = 0x0500;
	protected final static short SOE_PING = 0x0600;
	protected final static short SOE_NET_STATUS_REQ = 0x0700;
	protected final static short SOE_NET_STATUS_RES = 0x0800;
	protected final static short SOE_CHL_DATA_A = 0x0900; // Header size: 6
	// bytes.
	protected final static short SOE_CHL_DATA_B = 0x0A00;
	protected final static short SOE_CHL_DATA_C = 0x0B00;
	protected final static short SOE_CHL_DATA_D = 0x0C00;
	protected final static short SOE_DATA_FRAG_A = 0x0D00; // Header size: 10
	// for first
	// fragment, 4 for
	// others.
	protected final static short SOE_DATA_FRAG_B = 0x0E00;
	protected final static short SOE_DATA_FRAG_C = 0x0F00;
	protected final static short SOE_DATA_FRAG_D = 0x1000;
	protected final static short SOE_OUT_ORDER_PKT_A = 0x1100;
	protected final static short SOE_OUT_ORDER_PKT_B = 0x1200;
	protected final static short SOE_OUT_ORDER_PKT_C = 0x1300;
	protected final static short SOE_OUT_ORDER_PKT_D = 0x1400;
	protected final static short SOE_ACK_A = 0x1500;
	protected final static short SOE_ACK_B = 0x1600;
	protected final static short SOE_ACK_C = 0x1700;
	protected final static short SOE_ACK_D = 0x1800;
	protected final static short DATA_A_MULTI_PKT = 0x1900; // This is never
	// sent directly.
	// It's always
	// enclosed in a
	// SOE_CHL_DATA_A,
	// and therefore
	// does not have to
	// be handled.
	protected final static short SOE_FATAL_ERR = 0x1D00;
	protected final static short SOE_FATAL_ERR_REP = 0x1E00;
	protected final static short SOE_TRANS_FIVE = 0x0005;
	// ---------------
	protected final static short DISCONNECT_REASON_KICK = 0x0000;
	protected final static short DISCONNECT_REASON_LOGOUT = 0x0600;

	// ------------------

	// Inter-Server opcodes.
	protected final static byte SERVER_CONNECTION_REQUEST = 1;
	protected final static byte SERVER_CONNECTION_RESPONSE = 2;
	protected final static byte ZONE_SERVER_REQUEST_ACCOUNT_DATA = 3;
	protected final static byte SERVER_RESPOND_ACCOUNT_DATA = 4;
	protected final static byte ZONE_SERVER_SAVED_NEW_CHARACTER = 5;
	protected final static byte SERVER_RESPOND_SAVE_NEW_CHARACTER = 6;
	protected final static byte SERVER_DISCONNECT = 7;
	protected final static byte SERVER_VOLUNTEER_UPDATE_STATUS = 8;
	protected final static byte SERVER_TO_SERVER_PING = 9;
	protected final static byte SERVER_TO_SERVER_PONG = 10;
	protected final static byte SERVER_SEND_ESTIMATED_PING = 11;
	protected final static byte SERVER_REQUEST_STATUS = 12;
	protected final static byte SERVER_ERROR_ON_TCP = 13;
	protected final static byte SERVER_ZONE_SEND_FRIEND_CHANGE_STATUS = 14;
	protected final static byte SERVER_LOGIN_SEND_FRIEND_CHANGE_STATUS = 15;

	// SOE Update Types
	protected final static short CLIENT_UI_UPDATE = 0x0001;
	protected final static short WORLD_UPDATE = 0x0002;
	protected final static short ACCOUNT_UPDATE = 0x0003;
	protected final static short SERVER_UPDATE = 0x0004;
	protected final static short OBJECT_UPDATE = 0x0005;
	protected final static short UPDATE_SIX = 0x0006;
	protected final static short UPDATE_SEVEN = 0x0007;
	protected final static short SCENE_UPDATE = 0x0008;
	protected final static short UPDATE_NINE = 0x0009;
	protected final static short UPDATE_TEN = 0x000A;
	protected final static short UPDATE_ELEVEN = 0x000B;
	protected final static short UPDATE_CHAR_CREATE = 0x000C;
	protected final static short UPDATE_THIRTEEN = 0x000D;
	protected final static short UPDATE_FOURTEEN = 0x000E;
	protected final static short UPDATE_FIFTEEN = 0x000F;

	// / SOE Command CRCs

	// BOTH
	public final static int CmdSceneReady = 0x43FD1C22; // You're a Client UI
	// Update
	public final static int ObjControllerMessage = 0x80CE5E46; // You're a
	// Object Update

	// Client->Server
	public final static int LoginClientId = 0x41131F96; // You're Login Server
	// Specific?
	public final static int LagRequest = 0x31805EE0; // You're a Client UI
	// Update -- Currently
	// unhandled.
	public final static int Unhandled1 = 0x4C3D2CFA; // You're a Client UI
	// Update
	public final static int ClientIdMsg = 0xD5899226; // You're an Account
	// Update.
	public final static int ClientRandomNameRequest = 0xD6D1B6D1; // You're a
	// World
	// Update.
	public final static int SelectCharacter = 0xB5098D76; // You're a World
	// Update
	public final static int ClientInactivityMessage = 0x0F5D5325; // You're a
	// World
	// Update
	public final static int ConnectPlayerMessage = 0x2E365218; // You're a World
	// Update
	// public final static int NewbieTutorialResponse = 0xCA88FBAD; // You're a
	// World Update (THIS IS WRONG LOOK DOWN)
	public final static int ChatSendToRoom = 0x20E4DBE3; // You'd be a... world
	// update, probably
	public final static int SuiEventNotification = 0x092D3564;
	public final static int ObjectMenuSelectMessage = 0x7CA18726; // You're an
	// Account
	// Update 26
	// 87 A1 7C
	public final static int ClientCreateCharacter = 0xB97F3074; // You're an
	// Update_Char_Create.
	// Respond with
	// ClientCreateCharacterSuccess
	// OR
	// ClientCreateCharacterFailed
	// (if it
	// failed).
	public final static int ClientDeleteCharacter = 0xE87AD031; // Added by
	// Alaguerrano.
	// 05-31-07
	public final static int ClientReadyStringResponse = 0xCA88FBAD; // this is
	// how we
	// know the
	// client is
	// ready to
	// roll
	public final static int FactionRequestMessage = 0xC1B03B81; // Faction
	// Request
	// Message -- A
	// Client UI
	// Update.
	public final static int MapLocationsRequestMessage = 0x1A7AB839;
	public final static int DropSkill = 0x006A99F1;
	public final static int RequestBazaarTerminal = 0x21B55A3B;
	// Server->Client
	public final static int ClientLogout = 0x42FD19DD; // 42 FD 19 DD // DD 19
	// FD 42
	public final static int LoginServerString = 0x0E20D7E9;
	public final static int LoginServerID = 0x58C07F21;
	public final static int LoginEnumCluster = 0xC11C63B9;
	public final static int LoginClientToken = 0xAAB296C6;
	public final static int LoginClusterStatus = 0x3436AEB6;
	public final static int EnumerateCharacterId = 0x65EA4574;
	public final static int ClientPermissionsMessage = 0xE00730E5;
	public final static int ClientRandomNameResponse = 0xE85FB868;
	public final static int ClientNameDeclinedResponse = 0x9B2C6BA7;// added by
	// PF
	// 07-02-07
	public final static int ClientCreateCharacterFailed = 0x0E219628;
	public final static int ClientCreateCharacterSuccess = 0x1DB575CC;
	public final static int ClientDeleteCharacterResponse = 0x8268989B;
	public final static int ChatServerStatus = 0x7102B15F;
	public final static int ParametersMessage = 0x487652DA;
	public final static int CmdStartScene = 0x3AE6DFAE;
	public final static int ServerTimeMessage = 0x2EBC3BD9;
	public final static int SceneCreateObjectByCrc = 0xFE89DDEA;
	public final static int SceneEndBaselines = 0x2C436037;
	public final static int ServerDeleteCharacter = 0x989B0002;// Added by
	// Alaguerrano
	// 05-31-07
	public final static int UpdateContainmentMessage = 0x56CBDE9E;
	public final static int BaselinesMessage = 0x68A75F0C;
	public final static int UpdateCellPermissionMessage = 0xF612499C;
	public final static int UpdateTransformParentMessage = 0xC867AB5A;
	public final static int PlayMusicMessage = 0x04270D8A;
	public final static int PlaySoundMessage = 0x8DF1519B;
	public final static int UpdatePvpStatusMessage = 0x08A1C126;
	public final static int UpdatePosture = 0x0BDE6B41; // 41 6B DE 0B
	// //0BDE6B41
	// UpdatePosture
	public final static int ChatRoomList = 0x70DEB197;
	public final static int ChatOnEnteredRoom = 0xE69BDC0A;
	public final static int ChatRoomMessage = 0xCD4CE444;
	public final static int ConnectPlayerResponseMessage = 0x6137556F;
	public final static int UpdateTransformMessage = 0x1B24F808;
	public final static int ChatSystemMessage = 0x6D2A6413;
	public final static int DeltasMessage = 0x12862153;// 53 21 86 12
	public final static int EnterStructurePlacementMode = 0xE8A54DC1;
	public final static int PlanetTravelPointListResponse = 0x4D32541F; // 1F 54
	// 32 4D
	public final static int EnterTicketPurchaseModeMessage = 0x904DAE1A;
	public final static int SceneDestroyObject = 0x4D45D504;
	public final static int StopClientEffectObjectByLabelMessage = 0xAD6F6B26;
	public final static int OpenedContainerMessage = 0x2E11E4AB;
	public final static int PlanetTravelListRequest = 0x96405D4D;
	public final static int GetMapLocationsMessage = 0x1A7AB839;
	public final static int GetMapLocationsResponseMessage = 0x9f80464C;
	public final static int ServerWeatherMessage = 0x486356EA;
	public final static int ClientSendEmailRequest = 0x25A29FA6;
	public final static int ServerSendEmailMessage = 0X08485E17;
	public final static int NewEmailNotification = 0xD72FE9BE;
	public final static int ClientRequestEmailContent = 0x07E3559F;
	public final static int ClientRequestDeleteEmail = 0x8F251641;
	public final static int ClientUIErrorMessage = 0xB5ABF91A;
	public final static int ChatInstantMessageToCharacter = 0x84BB21F7;
	public final static int ChatInstantMessageToClient = 0x3C565CED;
	public final static int ChatOnSendInstantMessage = 0x88DBB381;
	public final static int SuiCreatePageMessage = 0xD44B7259; // 59 72 4B D4
	public final static int ResourceListForSurveyMessage = 0x8A64B1D5;
	public final static int PlayClientEffectLocMessage = 0x02949E74;
	public final static int SurveyMessage = 0x877F79AC;
	public final static int AttributesList = 0xF3F12F2A;
	public final static int FriendListRequestResponse = 0xE97AB594;
	public final static int ChatFriendOnlineUpdate = 0x6CD2FCD8;
	public final static int ChatFriendOfflineUpdate = 0x54336726;
	public final static int ChatAccountUpdate = 0x2B2A0D94;
	public final static int PlayClientEffectObjectMessage = 0x8855434A;
	public final static int FactionResponseMessage = 0x5DD53957;
	public final static int MapLocationsResponseMessage = 0x9F80464C;
	public final static int RequestDraftComponentMessage = 0x5FD21EB0;
	public final static int RequestResourceWeightMessage = 0x9A8B385C;
	public final static int DestroyClientPathMessage = 0xA75E85EB;
	public final static int OpenChatterWindow = 0x594AD258;
	public final static int ClientOpenHarvesterOperate = 0xBD18C679;
	public final static int synchronizeduistoplisten = 0x7c158efd;
	public final static int harvesterselectresource = 0xff549d14;
	public final static int harvesteractivate = 0xce645c94;
	public final static int harvestergetresourcedata = 0xF096C059; // harvestergetresourcedata
	public final static int harvesterdeactivate = 0x2c61cb03;
	public final static int nextCraftingStage = 0x6AD8ED4D;
	public final static int createManufactureSchematic = 0xF4B66795;

	public final static int sendBazaarTerminalOpen = 0xCE04173E;

	// Object Controller Message Subheaders
	// Client->Server
	protected final static int ObjectActionEnqueue = 0x0000001B;
	protected final static int DataTransform = 0x00000071;
	protected final static int DataTransformWithParent = 0x000000F1;
	protected final static int RefreshMissionList = 0x000000F5;
	protected final static int AcceptMission = 0x000000F9;
	protected final static int CraftingExperimentationMessage = 0x00000106;
	protected final static int InsertItemIntoSchematicSlot = 0x00000107;
	protected final static int RemoveItemFromSchematicSlot = 0x00000108;

	protected final static int DequeueGenericObjController010C = 0x0000010C;

	protected final static int DequeueCraftingAssemblySuccessRating = 0x000001BE;
	protected final static int DequeueCreatePrototype = 0x000001C2;

	protected final static int DequeueExperimentationMessage = 0x00000113;
	protected final static int StartSecureTrade = 0x00000115;
	protected final static int CommandQueueEnqueue = 0x00000116;
	protected final static int ObjectMenuRequest = 0x00000146;
	protected final static int SetTargetRequest = 0x00000126;

	// Server->Client
	protected final static int NpcConversationMessage = 0x000000DF;
	protected final static int NPCConversationOptions = 0x000000E0;
	protected final static int HarvesterResourceList = 0x000000EA;
	protected final static int dequeueRetrieveHarvesterResource = 0x000000EE;
	protected final static int enqueueRetrieveHarvesterResource = 0x000000ED;
	protected final static int StartNpcConversation = 0x000000DD;
	protected final static int CombatAction = 0x000000CC;
	protected final static int MobileAnimation = 0x000000F2;
	protected final static int CraftingDraftSchematics = 0x00000102;

	protected final static int SpatialChatMessage = 0x000000F4;
	protected final static int CommandQueueDequeue = 0x00000117;
	protected final static int SitOnObject = 0x0000013B;
	protected final static int ObjectMenuResponse = 0x00000147;
	protected final static int CraftingSetCustomization = 0x0000015A;
	protected final static int PlayerEmote = 0x0000012E;
	protected final static int PlayerPostureChange = 0x00000131;
	protected final static int BiographyUpdate = 0x000001DB;
	protected final static int MobileObjectAnimate = 0x00000166;
	protected final static int ShowFlyText = 0x000001BD;
	protected final static int CraftingSchematicComponenetMessage = 0x00000103;
	protected final static int DraftSchematicComponentMessage = 0x000001BF;
	protected final static int CombatTextSpam = 0x00000134;
	protected final static int ResourceWeightMessage = 0x00000207;
	protected final static int HarvesterOpenOperatorWindow = 0x0000022B;
	protected final static int TransferItemDequeue = 0x00000448;
	protected final static int OpenStartingLocationsWindow = 0x000001FC;
	protected final static int ImageDesignStart = 0x0000023A;
	protected final static int BuffStats = 0x00000229;
	protected final static int ImageDesignChangeMessage = 0x00000238;

	// Command Queue Enqueue/Dequeue CRC's
	protected final static int declarePeace = 0x4178FD6A;
	protected final static int selectDraftSchematicToCraft = 0x89242e02;
	protected final static int spatialChatInternal = 0x7C8D63D4;
	protected final static int flourish = 0xC8998CE9;
	protected final static int socialInternal = 0x32CF1BEE;
	protected final static int serverdestroyobject = 0xE7AEC4FB;
	protected final static int placestructure = 0x7AF26B0B;
	protected final static int purchaseticket = 0xBFF5BE51;
	protected final static int mount = 0xE007BF31;
	protected final static int dismount = 0x06F978ED;
	protected final static int requestwaypointatposition = 0x4982E17B;// Added
	// by
	// Alaguerrano
	// 06-07-07
	protected final static int opencontainer = 0x70177586;
	protected final static int closeContainer = 0x310a90f6;
	protected final static int SelectStartingLocation = 0xcf2d30f4;

	protected final static int synchronizeduilisten = 0xF9996AB6;
	protected final static int requestcraftingsession = 0x094AC516;
	protected final static int cancelCraftingSession = 0x83250E2A;
	protected final static int craftingCreatePrototype = 0xD61FF415;
	protected final static int setSkillTitle = 0xdb555329;
	protected final static int maskScent = 0xB0F4C4B6;

	// ---------ENTERTAINER RELATED

	protected final static int commandstartmusic = 0xDDD1E8F1;
	protected final static int stopmusic = 0x4a0d52dd;
	protected final static int commandstartdance = 0x7B1DCBE0;
	protected final static int commandchangedance = 0x13ee2d35;
	protected final static int watch = 0xec93ca43;
	protected final static int stopwatch = 0x6651ad9a;
	protected final static int listen = 0x5855bb1b;
	protected final static int stoplisten = 0xc2e4d4d0;
	protected final static int stopdance = 0xecc171cc;
	protected final static int spotlight = 0xed4aa746;
	protected final static int colorlights = 0xb008cbfa;
	protected final static int dazzle = 0x9c7713a5;
	protected final static int firejet = 0x35ed32be;
	protected final static int ventriloquism = 0x6cb6978f;
	protected final static int distract = 0x2434ac3a;
	protected final static int smokebomb = 0xd536b419;
	protected final static int imagedesign = 0xdfc959ba;

	// ---------ENTERTAINER RELATED

	protected final static int getattributesbatch = 0x164550EF;
	protected final static int emailrelatedenqueurequest = 0xEF21F9CE;
	protected final static int ClientRequestSlashWay = 0x640543FE;
	protected final static int ToggleWaypointStatus = 0xC3EDA6B6;
	protected final static int ClientRenameWaypoint = 0x398F891A;
	protected final static int RequestSurveySession = 0xC00CFA18;
	protected final static int RequestCoreSample = 0x9223C634;
	protected final static int toggleawayfromkeyboard = 0x9B9FE4A8;
	protected final static int findfriend = 0x30BE6EE9;
	protected final static int addfriend = 0x2A2357ED;
	protected final static int removefriend = 0x8E9091D7;
	protected final static int burstrun = 0xFC3D1CB2;
	protected final static int SetGodMode = 0x6CAFFD66;
	protected final static int Tip = 0xC64D8CB0;
	protected final static int SitClient = 0xB719FA26;
	protected final static int StandClient = 0xA8A25C79;
	protected final static int ProneClient = 0xBD8D02AF;
	protected final static int KneelClient = 0x01B48B26;
	protected final static int DefaultAttackNPC = 0xA8FEF90A;
	protected final static int npcconversationstart = 0x04CDAFCE;
	protected final static int npcconversationstop = 0x97D734FE;
	protected final static int npcconversationselect = 0x305ede19;
	protected final static int boardtransport = 0x5dcd41a2;
	protected final static int who = 0x8A19D7E1;
	protected final static int lfg = 0x3AD396A5;
	protected final static int roleplay = 0x32871193;
	protected final static int helper = 0x441F4A3E;
	protected final static int searchable = 0xD40D5142;
	protected final static int transferitemweapon = 0x335676C7;
	protected final static int transferitemmisc = 0x82F75977;
	protected final static int transferitemarmor = 0x18726ca1;

	protected final static int logout = 0x03b65950;
	protected final static int setMoodInternal = 0x7759F35E;
	protected final static int setMood = 0x3BA18295;
	protected final static int getIgnoreList = 0x788ba6a3;
	// New
	protected final static int requestCharacterSheetInfo = 0x887B5461;
	protected final static int requestBadges = 0xCA604B86;
	protected final static int requestBiography = 0x1BAD8FFC;
	protected final static int requestStatMigrationData = 0x7AFCA539;
	protected final static int badgesResponseMessage = 0x6D89D25B;
	protected final static int characterSheetResponseMessage = 0x9B3A17C4;
	protected final static int statMigrationResponseMessage = 0xEFAC38C4;
	protected final static int shuttleFlyOutAnimation = 0xAB290245;
	// -----------------------
	protected final static int unknownAnimation1 = 0xF316B1C0;
	protected final static int starportFlyOutAnimation = 0x4DC86E93;
	protected final static int grantbadge = 0x2C373D3F;
	protected final static int revokebadge = 0xF06A3BAF;
	protected final static int ejectrequest = 0x18905d2;
	protected final static int unstickrequest = 0xb22ba352;
	protected final static int moveitem = 0x4b74a403;
	protected final static int rotateitem = 0xa8eb1d48;
	protected final static int paymaintenance = 0xe7e35b30;
	protected final static int destroystructure = 0x18fc1726;
	protected final static int diagnoseWounds = 0xDC7CF134;
	protected final static int healDamage = 0x0A9F00A0;
	protected final static int healWound = 0x2087CE04;
	protected final static int tendDamage = 0x18CD8967;
	protected final static int tendWound = 0x31DD4C4B;
	protected final static int groupInvite = 0x88505d58;
	protected final static int acceptGroupInvite = 0xA99E6807;
	protected final static int declineGroupInvite = 0x43E1F84F;
	protected final static int leaveGroup = 0x5061D654;
	protected final static int disbandGroup = 0x46d22d3a;
	protected final static int makeGroupLeader = 0x939ad584;
	protected final static int SetSpokenLanguage = 0xAE2907E4;
	protected final static int teachSkill = 0x5041F83A;
	protected final static int resourcecontainertransfer = 0xf7262a75;
	protected final static int resourcecontainersplit = 0x74952854;
	protected final static int addpower = 0x8f2369da;
	protected final static int harvesterdiscardhopper = 0xc89b9e26;
	protected final static int forage = 0x494f9f80;
	protected final static int medicalForage = 0xc6132b18;

	// ------------Trade Related - Some of these go both ways to client or to
	// server
	protected final static int Trade_BeginTradeMessage = 0x325932D8;// D8 32 59
	// 32
	// //325932D8
	// // begin
	// trade
	// message
	protected final static int Trade_UnCheckTradeWindowAccept = 0xE81E4382;// 82
	// 43
	// 1E
	// E8
	// //E81E4382
	// unaccept
	// transaction
	// message
	protected final static int Trade_UpdateTradeWindowCredits = 0xD1527EE8;
	protected final static int Trade_CheckTradeWindowAccept = 0xB131CA17; // 17
	// CA
	// 31
	// B1
	// //
	// B131CA17

	protected final static int Trade_VerifyTradeMessage = 0x9AE247EE; // EE 47
	// E2 9A
	// //9AE247EE
	// VerifytradeMessage
	protected final static int Trade_TradeCompleteMessage = 0xC542038B; // 8B 03
	// 42 C5
	// //
	// C542038B
	// Trade
	// COmplete
	protected final static int Trade_ClientAddItemToTradeWindow = 0x1E8D1356;// 56
	// 13
	// 8D
	// 1E
	// //1E8D1356
	// AddItemToTradeWindow/
	// or
	// Begin
	// Requested
	// Trade
	protected final static int Trade_AbortTradeMessage = 0x9CA80F98;// 98 0F A8
	// 9C
	// //9CA80F98
	// Abort
	// Trade
	// ----------

	// Directly related to skills. SWGCrc of the skill name.
	// Believe it or not, these appear to all be ComamndQueueEnqueues.
	protected final static int musician = 0x4ad93196;
	protected final static int dancer = 0xf067b37;
	protected final static int imagedesigner = 0xa843756d;
	protected final static int stopDance = 0xecc171cc;
	protected final static int stopMusic = 0x4a0d52dd;
	protected final static int flourish1 = 0x3504fd15;
	protected final static int flourish2 = 0x3847dbcc;
	protected final static int flourish3 = 0x3c86c67b;
	protected final static int flourish4 = 0x22c1967e;
	protected final static int flourish5 = 0x26008bc9;
	protected final static int flourish6 = 0x2b43ad10;
	protected final static int flourish7 = 0x2f82b0a7;
	protected final static int flourish8 = 0x17cd0d1a;
	protected final static int startDanceBasic = 0xfd2e370b;
	protected final static int startDanceRhythmic = 0xe3a4c48e;
	protected final static int startMusicStarwars1 = 0x4048719f;
	protected final static int slitherhorn = 0x9dc097e9;
	protected final static int startDanceFootloose2 = 0xc3469160;
	protected final static int startDanceFormal2 = 0x7dffd264;
	protected final static int startMusicCeremonial = 0x47779308;
	protected final static int mandoviol = 0xcf08847e;
	protected final static int startMusicRock = 0xf10c1f5e;
	protected final static int startMusicFizz = 0x15fd3e76;
	protected final static int startMusicStarwars2 = 0x4d0b5746;
	protected final static int startMusicFolk = 0xc76a43d0;
	protected final static int startMusicFanfare = 0x7ac85ef5;
	protected final static int startMusicStarwars3 = 0x49ca4af1;
	protected final static int kloohorn = 0x37bdf9e;
	protected final static int startDanceBasic2 = 0xfa05666f;
	protected final static int startDanceRhythmic2 = 0x699815;
	protected final static int startDanceFootloose = 0xf435bd0c;
	protected final static int startDanceFormal = 0x5a46a5b9;
	protected final static int harvestCorpse = 0x29d0cc5;
	protected final static int maskscent = 0xb0f4c4b6;
	protected final static int diagnose = 0xdc7cf134;
	protected final static int firstAid = 0xd5f85133;
	protected final static int quickHeal = 0x7ab1850d;
	protected final static int dragIncapacitatedPlayer = 0x273a06da;
	protected final static int customizeVehicle = 0xe04f2807;
	protected final static int access_fee = 0xd16d0a22;
	protected final static int premium_auctions = 0x149b08b7;
	protected final static int polearmLunge1 = 0x7fd6584d;
	protected final static int unarmedLunge1 = 0x38bddd4;
	protected final static int melee1hLunge1 = 0x4dc1571;
	protected final static int melee2hLunge1 = 0xe83e702f;
	protected final static int taunt = 0x52342d60;
	protected final static int warcry1 = 0x6a56ecc3;
	protected final static int intimidate1 = 0x5619e044;
	protected final static int berserk1 = 0xe212d37;
	protected final static int centerOfBeing = 0x5997ac66;
	protected final static int polearmLunge2 = 0x72957e94;
	protected final static int unarmedLunge2 = 0xec8fb0d;
	protected final static int melee1hLunge2 = 0x99f33a8;
	protected final static int melee2hLunge2 = 0xe57d56f6;
	protected final static int warcry2 = 0x6715ca1a;
	protected final static int intimidate2 = 0x5b5ac69d;
	protected final static int berserk2 = 0x3620bee;
	protected final static int unarmedHit1 = 0x3124e0dd;
	protected final static int unarmedStun1 = 0xcf15393d;
	protected final static int unarmedBlind1 = 0xca8b008b;
	protected final static int unarmedSpinAttack1 = 0xdb447550;
	protected final static int melee1hHit1 = 0x584e5d11;
	protected final static int melee1hBodyHit1 = 0xce565bff;
	protected final static int melee1hDizzyHit1 = 0x64e2d561;
	protected final static int melee1hSpinAttack1 = 0xbb88156a;
	protected final static int melee2hHit1 = 0x75364a99;
	protected final static int melee2hHeadHit1 = 0x1934bc13;
	protected final static int melee2hSweep1 = 0x49bd34a6;
	protected final static int melee2hSpinAttack1 = 0x86a5f2d2;
	protected final static int polearmHit1 = 0x844e3aec;
	protected final static int polearmLegHit1 = 0xebb64bf6;
	protected final static int polearmStun1 = 0xf34ee28;
	protected final static int polearmSpinAttack1 = 0x63319278;
	protected final static int pointBlankArea1 = 0xfdcc0480;
	protected final static int pointBlankSingle1 = 0x5842294e;
	protected final static int overChargeShot1 = 0x795d8310;
	protected final static int overChargeShot2 = 0x741ea5c9;
	protected final static int headShot1 = 0x80f6ab48;
	protected final static int takeCover = 0x3903080b;
	protected final static int headShot2 = 0x8db58d91;
	protected final static int mindShot1 = 0x922143e8;
	protected final static int bodyShot1 = 0xe3f9c921;
	protected final static int rollShot = 0x3250b4d;
	protected final static int diveShot = 0x857fcdd0;
	protected final static int kipUpShot = 0x5394d72d;
	protected final static int bodyShot2 = 0xeebaeff8;
	protected final static int healthShot1 = 0x6c4bae17;
	protected final static int legShot1 = 0x27308ffd;
	protected final static int fullAutoSingle1 = 0xbae4f3db;
	protected final static int legShot2 = 0x2a73a924;
	protected final static int actionShot1 = 0x484536a1;
	protected final static int threatenShot = 0xee14ca1e;
	protected final static int aim = 0xa0a107f8;
	protected final static int tumbleToProne = 0xb30c9b5;
	protected final static int tumbleToKneeling = 0x29346510;
	protected final static int tumbleToStanding = 0x54314196;
	protected final static int warningShot = 0xf44972f6;
	protected final static int suppressionFire1 = 0xf3f3d495;
	protected final static int strafeShot1 = 0x85b33f08;
	protected final static int strafeShot2 = 0x88f019d1;
	protected final static int mindShot2 = 0x9f626531;
	protected final static int headShot3 = 0x89749026;
	protected final static int surpriseShot = 0x61dc9eba;
	protected final static int sniperShot = 0x13cc58e1;
	protected final static int concealShot = 0x5a70c24b;
	protected final static int sneak = 0x98ecf380;
	protected final static int flushingShot1 = 0x60ce31d9;
	protected final static int startleShot1 = 0x4b23f7e;
	protected final static int flushingShot2 = 0x6d8d1700;
	protected final static int startleShot2 = 0x9f119a7;
	protected final static int flurryShot1 = 0x471eac17;
	protected final static int flurryShot2 = 0x4a5d8ace;
	protected final static int healthShot2 = 0x610888ce;
	protected final static int multiTargetPistolShot = 0x4000fc87;
	protected final static int disarmingShot2 = 0xd99906b1;
	protected final static int pointBlankSingle2 = 0x55010f97;
	protected final static int bodyShot3 = 0xea7bf24f;
	protected final static int pointBlankArea2 = 0xf08f2259;
	protected final static int disarmingShot1 = 0xd4da2068;
	protected final static int doubleTap = 0xeb830578;
	protected final static int stoppingShot = 0x1673720c;
	protected final static int fanShot = 0x10921edb;
	protected final static int pistolMeleeDefense1 = 0x6e76c41f;
	protected final static int pistolMeleeDefense2 = 0x6335e2c6;
	protected final static int actionShot2 = 0x45061078;
	protected final static int fullAutoSingle2 = 0xb7a7d502;
	protected final static int scatterShot1 = 0xad866d55;
	protected final static int wildShot1 = 0x7a48c259;
	protected final static int scatterShot2 = 0xa0c54b8c;
	protected final static int wildShot2 = 0x770be480;
	protected final static int legShot3 = 0x2eb2b493;
	protected final static int cripplingShot = 0xe68a8b94;
	protected final static int burstShot1 = 0x3881333d;
	protected final static int burstShot2 = 0x35c215e4;
	protected final static int suppressionFire2 = 0xfeb0f24c;
	protected final static int fullAutoArea1 = 0x8d1b984e;
	protected final static int chargeShot1 = 0x109f8acf;
	protected final static int fullAutoArea2 = 0x8058be97;
	protected final static int chargeShot2 = 0x1ddcac16;
	protected final static int unarmedHit2 = 0x3c67c604;
	protected final static int meditate = 0x124629f2;
	protected final static int unarmedHit3 = 0x38a6dbb3;
	protected final static int powerBoost = 0x8c2221cb;
	protected final static int forceOfWill = 0x2ef7ee38;
	protected final static int unarmedKnockdown1 = 0x26b9db1b;
	protected final static int unarmedKnockdown2 = 0x2bfafdc2;
	protected final static int unarmedDizzy1 = 0x792d1600;
	protected final static int unarmedCombo1 = 0x874523c4;
	protected final static int unarmedSpinAttack2 = 0xd6075389;
	protected final static int unarmedCombo2 = 0x8a06051d;
	protected final static int unarmedBodyHit1 = 0x57ecde87;
	protected final static int unarmedLegHit1 = 0x638b5475;
	protected final static int unarmedHeadHit1 = 0x5fec475;
	protected final static int melee1hHit2 = 0x550d7bc8;
	protected final static int melee1hHit3 = 0x51cc667f;
	protected final static int melee1hScatterHit1 = 0x5bb4f3a0;
	protected final static int melee1hDizzyHit2 = 0x69a1f3b8;
	protected final static int melee1hScatterHit2 = 0x56f7d579;
	protected final static int melee1hHealthHit1 = 0xeefd0f6;
	protected final static int melee1hSpinAttack2 = 0xb6cb33b3;
	protected final static int melee1hHealthHit2 = 0x3acf62f;
	protected final static int melee1hBodyHit2 = 0xc3157d26;
	protected final static int melee1hBodyHit3 = 0xc7d46091;
	protected final static int melee1hBlindHit1 = 0x362cfc22;
	protected final static int melee1hBlindHit2 = 0x3b6fdafb;
	protected final static int melee2hHit2 = 0x78756c40;
	protected final static int melee2hHit3 = 0x7cb471f7;
	protected final static int melee2hArea1 = 0x8b172b04;
	protected final static int melee2hArea2 = 0x86540ddd;
	protected final static int melee2hArea3 = 0x8295106a;
	protected final static int melee2hHeadHit2 = 0x14779aca;
	protected final static int melee2hHeadHit3 = 0x10b6877d;
	protected final static int melee2hSpinAttack2 = 0x8be6d40b;
	protected final static int melee2hSweep2 = 0x44fe127f;
	protected final static int melee2hMindHit1 = 0xfaee81fb;
	protected final static int melee2hMindHit2 = 0xf7ada722;
	protected final static int polearmHit2 = 0x890d1c35;
	protected final static int polearmHit3 = 0x8dcc0182;
	protected final static int polearmStun2 = 0x277c8f1;
	protected final static int polearmSpinAttack2 = 0x6e72b4a1;
	protected final static int polearmLegHit2 = 0xe6f56d2f;
	protected final static int polearmArea1 = 0x7e1ebeba;
	protected final static int polearmLegHit3 = 0xe2347098;
	protected final static int polearmArea2 = 0x735d9863;
	protected final static int polearmSweep1 = 0xde551cc4;
	protected final static int polearmSweep2 = 0xd3163a1d;
	protected final static int melee_damage_mitigation_2 = 0xcc8c765a;
	protected final static int polearmActionHit1 = 0x43162c1f;
	protected final static int polearmActionHit2 = 0x4e550ac6;
	protected final static int startDancePopular = 0x81d79b39;
	protected final static int startDancePoplock = 0x9f07f4de;
	protected final static int startDanceLyrical2 = 0xca7bcd64;
	protected final static int startDanceExotic3 = 0x954d04b5;
	protected final static int startDanceExotic4 = 0x8b0a54b0;
	protected final static int startDancePopular2 = 0xd611d2ec;
	protected final static int startDancePoplock2 = 0x76e14e96;
	protected final static int startDanceLyrical = 0x5af121a6;
	protected final static int startDanceExotic = 0x1f90a12;
	protected final static int startDanceExotic2 = 0x918c1902;
	protected final static int startMusicVirtuoso = 0x8a00b99c;
	protected final static int place_theater = 0x31e92bdc;
	protected final static int startMusicBallad = 0x5d43537a;
	protected final static int bandfill = 0x43e999ca;
	protected final static int startMusicWaltz = 0x94ba88a;
	protected final static int flutedroopy = 0x8ea035ff;
	protected final static int startMusicHazz = 0xf534fa87;
	protected final static int omnibox = 0x1380682e;
	protected final static int healState = 0x4a386bd5;
	protected final static int registerWithLocation = 0xbc150016;
	protected final static int curePoison = 0x1754a3e5;
	protected final static int extinguishFire = 0xdf49ea58;
	protected final static int cureDisease = 0xe994de9c;
	protected final static int healEnhance = 0xeee029cf;
	protected final static int revivePlayer = 0xc9759876;
	protected final static int areatrack = 0xaa4e8a8c;
	protected final static int areatrack_animal = 0x15bb8646;
	protected final static int conceal = 0x1f0512d5;
	protected final static int areatrack_direction = 0x9265e3a8;
	protected final static int areatrack_npc = 0xed1afdb4;
	protected final static int areatrack_distance = 0xc82d35a5;
	protected final static int areatrack_player = 0xfc7136a6;
	protected final static int rescue = 0x2f226eee;
	protected final static int pet_follow = 0x8f1224f5;
	protected final static int pet_release = 0x13bbd2d5;
	protected final static int pet_attack = 0x67164ae5;
	protected final static int tame = 0x3f6dc90;
	protected final static int pet_transfer = 0x155b4434;
	protected final static int pet_rangedattack = 0xcaf80bdb;
	protected final static int pet_specialattack1 = 0x38058924;
	protected final static int pet_specialattack2 = 0x3546affd;
	protected final static int pet_stay = 0x272e7bad;
	protected final static int pet_guard = 0x21a1b05c;
	protected final static int pet_patrol = 0xee9c68af;
	protected final static int pet_formation = 0xd5cc4ac0;
	protected final static int trick1 = 0xab5b43aa;
	protected final static int emboldenpets = 0x58f5818c;
	protected final static int trick2 = 0xa6186573;
	protected final static int enragepets = 0xdea1f466;
	protected final static int pet_group = 0x5fef12cb;
	protected final static int pet_followother = 0x4fa660b0;
	protected final static int pet_friend = 0x181eeb89;
	protected final static int train_mount = 0x97740b4d;
	protected final static int sampleDNA = 0xd04917be;
	protected final static int place_cantina = 0x9ce0b813;
	protected final static int ad_fees_1 = 0xcf7bef2b;
	protected final static int maintenance_fees_1 = 0x85e28983;
	protected final static int place_merchant_tent = 0x53d013cf;
	protected final static int slice_containers = 0x8666997e;
	protected final static int slice_terminals = 0xd9f5c560;
	protected final static int slice_weaponsbasic = 0xa286115f;
	protected final static int slice_armor = 0x1ff4bdfc;
	protected final static int slice_weaponsadvanced = 0x1ff7260f;
	protected final static int feignDeath = 0x4906c303;
	protected final static int panicShot = 0x6ac22291;
	protected final static int lowBlow = 0xd4dbbc99;
	protected final static int melee_damage_mitigation_1 = 0xc1cf5083;
	protected final static int lastDitch = 0x29d6ff40;
	protected final static int sprayShot = 0xabcd2684;
	protected final static int fastBlast = 0x275e22c9;
	protected final static int fireLightningCone2 = 0xc7034070;
	protected final static int droid_find = 0x1e20f2b9;
	protected final static int droid_track = 0x253414c1;
	protected final static int underHandShot = 0x329cf4c4;
	protected final static int knockdownFire = 0xcce2dc56;
	protected final static int confusionShot = 0xa1e4559d;
	protected final static int bleedingShot = 0xe5b4d271;
	protected final static int eyeShot = 0x3494a88e;
	protected final static int torsoShot = 0x3291a9be;
	protected final static int fireLightningSingle1 = 0x17a2b52;
	protected final static int fireLightningCone1 = 0xca4066a9;
	protected final static int fireLightningSingle2 = 0xc390d8b;
	protected final static int flameCone2 = 0xd8efff96;
	protected final static int fireAcidCone2 = 0x37170127;
	protected final static int ranged_damage_mitigation_3 = 0xf63d0e79;
	protected final static int ranged_damage_mitigation_1 = 0xffbf3517;
	protected final static int ranged_damage_mitigation_2 = 0xf2fc13ce;
	protected final static int flameSingle1 = 0xe3ef3074;
	protected final static int flameCone1 = 0xd5acd94f;
	protected final static int flameSingle2 = 0xeeac16ad;
	protected final static int fireAcidSingle1 = 0xec009f1f;
	protected final static int fireAcidCone1 = 0x3a5427fe;
	protected final static int fireAcidSingle2 = 0xe143b9c6;
	protected final static int applyPoison = 0xe08596db;
	protected final static int place_hospital = 0x1c11efbf;
	protected final static int applyDisease = 0xaf5e4d90;
	protected final static int healMind = 0xdfac57ee;
	protected final static int sysgroup = 0x5d6a8c24;
	protected final static int steadyaim = 0xa5bdbba6;
	protected final static int volleyFire = 0x7ef26d6a;
	protected final static int formup = 0xab9ffca3;
	protected final static int boostmorale = 0xd29dea7e;
	protected final static int rally = 0x637279ea;
	protected final static int retreat = 0x4871bbf4;
	protected final static int place_cityhall = 0x10599796;
	protected final static int manage_taxes = 0x2ddbe06;
	protected final static int grantZoningRights = 0x17cbba9;
	protected final static int city_map = 0xc2925025;
	protected final static int place_bank = 0x3c2d4458;
	protected final static int place_shuttleport = 0xd887af94;
	protected final static int city_spec_industry = 0x11870a1b;
	protected final static int city_spec_research = 0x97ff4256;
	protected final static int manage_militia = 0x70545a9d;
	protected final static int place_cloning = 0xd3475c04;
	protected final static int city_spec_cloning = 0x56351f3b;
	protected final static int city_spec_stronghold = 0x8a15c694;
	protected final static int place_faction_terminal = 0x46e31959;
	protected final static int installMissionTerminal = 0x3bb700f3;
	protected final static int recruitSkillTrainer = 0x5f42fb6e;
	protected final static int place_garage = 0x8dd1191;
	protected final static int city_spec_entertainer = 0xe00f3d8b;
	protected final static int city_spec_doctor = 0xe44e8780;
	protected final static int city_spec_missions = 0x85a62e0d;
	protected final static int city_spec_sample_rich = 0x3e87d01e;
	protected final static int place_streetlamp = 0xdff4c1f;
	protected final static int place_statue = 0x4e7487e;
	protected final static int place_fountain = 0x92142c5;
	protected final static int place_small_garden = 0x3e7b4d38;
	protected final static int place_medium_garden = 0x33547066;
	protected final static int place_large_garden = 0xe86d6d64;
	protected final static int place_exotic_garden = 0x4ce34d0;
	protected final static int language_rodese = 0x9cb74ab0;
	protected final static int language_dosh = 0xc1616d2f;
	protected final static int language_moncalamarian = 0xd1fac76a;
	protected final static int language_shyriiwook = 0x11ffb29f;
	protected final static int language_bothese = 0x7dc04de2;
	protected final static int language_ryl = 0xfcc5fbac;
	protected final static int language_zabrak = 0xe2b324c0;
	protected final static int language_lekku = 0x1b1dd0a7;
	protected final static int language_ithorian = 0xa3a6bbf;
	protected final static int language_sullustan = 0xe20f3585;
	protected final static int regeneration = 0x53517288;
	protected final static int wookieeRoar = 0xb715b035;
	protected final static int vitalize = 0x55581018;
	protected final static int equilibrium = 0x81d6710;
	protected final static int space_navigator = 0xf102da3a;
	protected final static int sample = 0x8d0c1504;
	protected final static int survey = 0x19c9fac1;
	protected final static int forceIntimidate = 0xb67bb974;
	protected final static int saberPolearmHit1 = 0x471423ba;
	protected final static int saber1hHit1 = 0x9b9757c2;
	protected final static int saber2hHit1 = 0xb6ef404a;
	protected final static int saberThrow3 = 0xf1c46e37;
	protected final static int saber1hFlurry2 = 0x48930003;
	protected final static int saber2hPhantom = 0x1712e1dd;
	protected final static int saberPolearmDervish2 = 0x6756163;
	protected final static int saber1hHeadHit1 = 0x354e8cc9;
	protected final static int saber1hComboHit1 = 0xe7671d59;
	protected final static int saber1hHit2 = 0x96d4711b;
	protected final static int saber1hHeadHit2 = 0x380daa10;
	protected final static int saber1hComboHit2 = 0xea243b80;
	protected final static int saber1hHeadHit3 = 0x3cccb7a7;
	protected final static int saber1hHit3 = 0x92156cac;
	protected final static int saber1hComboHit3 = 0xeee52637;
	protected final static int saber1hFlurry = 0xa8f3f3ad;
	protected final static int saber2hBodyHit1 = 0xe22c6b25;
	protected final static int saber2hSweep1 = 0xc11d6447;
	protected final static int saber2hHit2 = 0xbbac6693;
	protected final static int saber2hBodyHit2 = 0xef6f4dfc;
	protected final static int saber2hBodyHit3 = 0xebae504b;
	protected final static int saber2hSweep2 = 0xcc5e429e;
	protected final static int saber2hHit3 = 0xbf6d7b24;
	protected final static int saber2hSweep3 = 0xc89f5f29;
	protected final static int saber2hFrenzy = 0x5db5a3c5;
	protected final static int saberPolearmLegHit1 = 0x30d7dd42;
	protected final static int saberPolearmSpinAttack1 = 0xef9a22df;
	protected final static int saberPolearmHit2 = 0x4a570563;
	protected final static int saberPolearmLegHit2 = 0x3d94fb9b;
	protected final static int saberPolearmLegHit3 = 0x3955e62c;
	protected final static int saberPolearmSpinAttack2 = 0xe2d90406;
	protected final static int saberPolearmHit3 = 0x4e9618d4;
	protected final static int saberPolearmSpinAttack3 = 0xe61819b1;
	protected final static int saberPolearmDervish = 0xa45b8a1;
	protected final static int saberSlash1 = 0xb7f2d310;
	protected final static int saberThrow1 = 0xf8465559;
	protected final static int saberSlash2 = 0xbab1f5c9;
	protected final static int saberThrow2 = 0xf5057380;

	// These opcodes require special attention -- they are "attacks" that can be
	// done with ANY weapon. Handle them separately, customized.
	protected final static int animalScare = 0xe001c174;
	protected final static int jediMindTrick = 0xed1e2488;
	protected final static int forceChoke = 0x4b0a9d33;
	protected final static int forceKnockdown3 = 0x61643587;
	protected final static int forceLightningSingle1 = 0x583b6776;
	protected final static int forceLightningCone1 = 0xc1a411e6;
	protected final static int forceLightningSingle2 = 0x557841af;
	protected final static int forceLightningCone2 = 0xcce7373f;
	protected final static int mindBlast1 = 0xb30ab2a;
	protected final static int animalCalm = 0x22e1565b;
	protected final static int mindBlast2 = 0x6738df3;
	protected final static int animalAttack = 0xa7e89355;
	protected final static int forceWeaken1 = 0xa8bdb9d8;
	protected final static int forceIntimidate1 = 0xbd0584a0;
	protected final static int forceWeaken2 = 0xa5fe9f01;
	protected final static int forceIntimidate2 = 0xb046a279;
	protected final static int forceThrow1 = 0x9a6a843e;
	protected final static int forceKnockdown1 = 0x68e60ee9;
	protected final static int forceThrow2 = 0x9729a2e7;
	protected final static int forceKnockdown2 = 0x65a52830;
	protected final static int healHealthSelf1 = 0xafc151d3;
	protected final static int totalHealSelf = 0x18e5d089;
	protected final static int totalHealOther = 0x6b370958;
	protected final static int healMindSelf1 = 0x77754acc;
	protected final static int healActionSelf1 = 0x8bcfc965;
	protected final static int healAllSelf1 = 0x1132654e;
	protected final static int healMindSelf2 = 0x7a366c15;
	protected final static int healActionSelf2 = 0x868cefbc;
	protected final static int healHealthSelf2 = 0xa282770a;
	protected final static int healAllSelf2 = 0x1c714397;
	protected final static int healHealthWoundSelf1 = 0x9f16f755;
	protected final static int healActionWoundSelf1 = 0x2d33ac6c;
	protected final static int healMindWoundSelf1 = 0x2d6d1e0d;
	protected final static int healBattleFatigueSelf1 = 0xc3074419;
	protected final static int healActionWoundSelf2 = 0x20708ab5;
	protected final static int healHealthWoundSelf2 = 0x9255d18c;
	protected final static int healBattleFatigueSelf2 = 0xce4462c0;
	protected final static int healMindWoundSelf2 = 0x202e38d4;
	protected final static int healMindWoundOther1 = 0x21bb7ada;
	protected final static int healHealthWoundOther1 = 0xeeee94ca;
	protected final static int healActionWoundOther1 = 0x7f091bda;
	protected final static int healAllOther1 = 0x9ffecf2e;
	protected final static int healHealthWoundOther2 = 0xe3adb213;
	protected final static int healActionWoundOther2 = 0x724a3d03;
	protected final static int healMindWoundOther2 = 0x2cf85c03;
	protected final static int healAllOther2 = 0x92bde9f7;
	protected final static int healStatesOther = 0x6831f67a;
	protected final static int stopBleeding = 0x650449d9;
	protected final static int forceCureDisease = 0x288ba78;
	protected final static int forceCurePoison = 0x99ea3dc1;
	protected final static int healStatesSelf = 0x2e286256;
	protected final static int forceAbsorb1 = 0x12f561a0;
	protected final static int forceRun3 = 0x9ee630f9;
	protected final static int regainConsciousness = 0xf279ea94;
	protected final static int forceSpeed1 = 0x9dd18562;
	protected final static int forceRun1 = 0x97640b97;
	protected final static int forceSpeed2 = 0x9092a3bb;
	protected final static int forceRun2 = 0x9a272d4e;
	protected final static int forceFeedback1 = 0x966bb324;
	protected final static int forceArmor1 = 0x8ec31b36;
	protected final static int forceFeedback2 = 0x9b2895fd;
	protected final static int forceAbsorb2 = 0x1fb64779;
	protected final static int forceArmor2 = 0x83803def;
	protected final static int forceResistBleeding = 0xbe92679a;
	protected final static int forceResistDisease = 0xcbc4fc32;
	protected final static int forceResistPoison = 0x544f3691;
	protected final static int forceResistStates = 0xcb9cdfe5;
	protected final static int transferForce = 0xb371de87;
	protected final static int channelForce = 0x2d8f25d8;
	protected final static int forceShield1 = 0x62a2b941;
	protected final static int drainForce = 0x5dff0378;
	protected final static int forceMeditate = 0xbd40a262;
	protected final static int forceShield2 = 0x6fe19f98;
	protected final static int avoidIncapacitation = 0xe0422;
	// END special force attack handling.

	protected final static int jstart3 = 0x13c499c5;
	protected final static int inspacerr = 0xb1975e3b;
	protected final static int jstart1 = 0x1a46a2ab;
	protected final static int eshields = 0x57aaf097;
	protected final static int vrepair = 0x671d13fc;
	protected final static int jstart2 = 0x17058472;
	protected final static int inspacerepair = 0x4211b296;
	protected final static int inspacereload = 0xe6d31180;
	protected final static int vrepairother = 0x98d99811;
	protected final static int droidcommand_shieldnormalize = 0x2bb2b110;
	protected final static int droidcommand_shieldbacktofronttwenty = 0xb62df0a0;
	protected final static int droidcommand_shieldfronttobacktwenty = 0xc2e51c92;
	protected final static int droidcommand_shieldadjustfrontone = 0x888616c7;
	protected final static int droidcommand_shieldadjustrearone = 0x51c1430f;
	protected final static int droidcommand_shieldbacktofrontfifty = 0x28e6b477;
	protected final static int droidcommand_shieldfronttobackfifty = 0xace8740d;
	protected final static int droidcommand_shieldadjustfronttwo = 0xfe2a804d;
	protected final static int droidcommand_shieldadjustreartwo = 0x276dd585;
	protected final static int droidcommand_shieldbacktofronteighty = 0xd358b1b7;
	protected final static int droidcommand_shieldfronttobackeighty = 0xa7905d85;
	protected final static int droidcommand_shieldadjustfrontthree = 0x1e3bf053;
	protected final static int droidcommand_shieldadjustrearthree = 0xeaf87d68;
	protected final static int droidcommand_shieldbacktofronthundred = 0x1a79b65d;
	protected final static int droidcommand_shieldfronttobackhundred = 0x2125ef66;
	protected final static int droidcommand_shieldadjustfrontfour = 0xa5dd99bd;
	protected final static int droidcommand_shieldadjustrearfour = 0xd1daea5b;
	protected final static int droidcommand_shieldemergencyfront = 0x7ba279c2;
	protected final static int droidcommand_shieldemergencyrear = 0x83c031bc;
	protected final static int bstrike3 = 0xad08eced;
	protected final static int pumpreactor = 0x2b422cef;
	protected final static int eweapons = 0xfe48caff;
	protected final static int bstrike1 = 0xa48ad783;
	protected final static int nblast = 0x6babba40;
	protected final static int bstrike2 = 0xa9c9f15a;
	protected final static int droidcommand_weaponoverloadone = 0x34348e57;
	protected final static int droidcommand_weaponeffeciencyone = 0xfd5541bb;
	protected final static int droidcommand_weaponnormalize = 0x1868b55e;
	protected final static int droidcommand_engineoverloadone = 0x6eddcfb5;
	protected final static int droidcommand_engineefficiencyone = 0x3c3b72d9;
	protected final static int droidcommand_enginenormalize = 0xef88eee9;
	protected final static int droidcommand_weaponoverloadtwo = 0x429818dd;
	protected final static int droidcommand_weaponeffeciencytwo = 0x8bf9d731;
	protected final static int droidcommand_engineoverloadtwo = 0x1871593f;
	protected final static int droidcommand_engineefficiencytwo = 0x4a97e453;
	protected final static int droidcommand_weaponoverloadthree = 0x4f055115;
	protected final static int droidcommand_weaponeffeciencythree = 0xb316bc6f;
	protected final static int droidcommand_engineoverloadthree = 0x922aad61;
	protected final static int droidcommand_engineefficiencythree = 0x18caffa;
	protected final static int droidcommand_weaponoverloadfour = 0x9f771fa7;
	protected final static int droidcommand_weaponeffeciencyfour = 0x81fb9331;
	protected final static int droidcommand_engineoverloadfour = 0x212b8076;
	protected final static int droidcommand_engineefficiencyfour = 0xb6837c1f;
	protected final static int droidcommand_mutedroid = 0x490c8aaf;
	protected final static int ptrap2 = 0xfd9cc551;
	protected final static int epulse3 = 0x760f2f16;
	protected final static int ethrust = 0xa4e9c952;
	protected final static int ptrap1 = 0xf0dfe388;
	protected final static int iffscramble = 0x2705e819;
	protected final static int epulse1 = 0x7f8d1478;
	protected final static int epulse2 = 0x72ce32a1;
	protected final static int droidcommand_reactoroverloadone = 0xc6024aa4;
	protected final static int droidcommand_reactornormalize = 0xd8595451;
	protected final static int droidcommand_weapcappowerupone = 0x21f0d878;
	protected final static int droidcommand_weapcapequalize = 0xd73a72fe;
	protected final static int droidcommand_weapcaptoshieldone = 0xddac096e;
	protected final static int droidcommand_reactoroverloadtwo = 0xb0aedc2e;
	protected final static int droidcommand_weapcappoweruptwo = 0x575c4ef2;
	protected final static int droidcommand_weapcaptoshieldtwo = 0xab009fe4;
	protected final static int droidcommand_reactoroverloadthree = 0x1688f6d9;
	protected final static int droidcommand_weapcappowerupthree = 0xd0c8cc36;
	protected final static int droidcommand_weapcaptoshieldthree = 0x7011874f;
	protected final static int droidcommand_reactoroverloadfour = 0x29892ac0;
	protected final static int droidcommand_weapcappowerupfour = 0xf580bc;
	protected final static int droidcommand_weapcaptoshieldfour = 0xe090f0d1;
	protected final static int droidcommand_testweaponoverload1 = 0x12be04a5;
	protected final static int droidcommand_testweaponoverload2 = 0xe2364706;
	protected final static int droidcommand_testweaponoverload3 = 0xe6f75ab1;
	protected final static int droidcommand_testweaponoverload4 = 0xf8b00ab4;
	protected final static int droidcommand_testweaponnormalize = 0x4db09377;
	protected final static int droidcommand_testfronttobackfifty = 0x5d605219;
	protected final static int droidcommand_testbacktofrontfifty = 0xd96e9263;

	protected final static int COMBAT_EFFECT_HIT = 0;
	protected final static int COMBAT_EFFECT_MISS = 1;
	protected final static int COMBAT_EFFECT_EVADE = 2;
	protected final static int COMBAT_EFFECT_BLOCK = 3;
	protected final static int COMBAT_EFFECT_COUNTER = 4;
	protected final static int NUM_COMBAT_EFFECTS = 5;

	// Waypoint data!

	protected final static byte DELTA_CREATING_ITEM = 0;
	protected final static byte DELTA_DELETING_ITEM = 1;
	protected final static byte DELTA_UPDATING_ITEM = 2;
	protected static int[] PlanetCRCForWaypoints = { 0x63556843, // Corellia
			0x2748F9D7, // Dantooine D7 F9 48 27
			0x63E75AED, // Dathomir
			0xA94ECA19, // Endor
			0x578E8F4F, // Lok 4F 8F 8E 57
			0xAFD7B558, // Naboo
			0x51D50A39, // Rori
			0xB616FA38, // Talus
			0x57279121, // Tatooine
			0xC2A64B83, // Yavin4
	};

	protected static String[] WAYPOINT_DEFAULT_NAMES = { "@planet_n:corellia",
			"@planet_n:dantooine", "@planet_n:dathomir", "@planet_n:endor",
			"@planet_n:lok", "@planet_n:naboo", "@planet_n:rori",
			"@planet_n:talus", "@planet_n:tatooine", "@planet_n:yavin4", };

	protected final static byte WAYPOINT_TYPE_PLAYER_CREATED = 1;

	// SLOT IDS -- to be used
	protected final static byte SLOT_ID_INVALID = -1;
	protected final static byte SLOT_ID_HEAD = 0;
	protected final static byte SLOT_ID_CHEST = 1;
	protected final static byte SLOT_ID_LEFT_FOREARM = 2;
	protected final static byte SLOT_ID_RIGHT_FOREARM = 3;
	protected final static byte SLOT_ID_LEFT_BICEP = 4;
	protected final static byte SLOT_ID_RIGHT_BICEP = 5;
	protected final static byte SLOT_ID_LEGS = 6;
	protected final static byte SLOT_ID_NECKLACE = 7;
	protected final static byte SLOT_ID_RING2 = 8;
	protected final static byte SLOT_ID_BACK = 9;
	protected final static byte SLOT_ID_WEAPON = 10;
	protected final static byte SLOT_ID_HANDS = 11;
	protected final static byte SLOT_ID_GOGGLES = 12;
	protected final static byte SLOT_ID_HEAD2 = 13;
	protected final static byte SLOT_ID_RING = 14;
	protected final static byte SLOT_ID_FEET = 15;
	protected final static byte SLOT_ID_BELT = 16;
	protected final static byte SLOT_JACKET = 17;
	protected final static int SLOT_COUNT = 18;

	protected final static String SPEEDER_SWOOP_IFF = "object/intangible/vehicle/shared_speederbike_swoop_pcd.iff";
	protected final static int SPEEDER_SWOOP_CRC = 0x85231D5A;

	protected final static byte RESOURCE_POOL_1 = 1;
	protected final static byte RESOURCE_POOL_2 = 2;
	protected final static byte RESOURCE_POOL_3 = 3;
	protected final static byte RESOURCE_POOL_4 = 4;

	protected final static short RESOURCE_START_ALUMINUM = 0;
	protected final static short RESOURCE_TYPE_ALUMINUM_TITANIUM = 0;
	protected final static short RESOURCE_TYPE_ALUMINUM_AGRINIUM = 1;
	protected final static short RESOURCE_TYPE_ALUMINUM_CHROMIUM = 2;
	protected final static short RESOURCE_TYPE_ALUMINUM_DURALUMIN = 3;
	protected final static short RESOURCE_TYPE_ALUMINUM_LINK_STEEL = 4;
	protected final static short RESOURCE_TYPE_ALUMINUM_PHRIK = 5;
	protected final static short RESOURCE_END_ALUMINUM = 5;

	protected final static short RESOURCE_START_COPPER = 6;
	protected final static short RESOURCE_TYPE_COPPER_DESH = 6;
	protected final static short RESOURCE_TYPE_COPPER_THALLIUM = 7;
	protected final static short RESOURCE_TYPE_COPPER_BEYRLLIUS = 8;
	protected final static short RESOURCE_TYPE_COPPER_CODOAN = 9;
	protected final static short RESOURCE_TYPE_COPPER_DIATIUM = 10;
	protected final static short RESOURCE_TYPE_COPPER_KELSH = 11;
	protected final static short RESOURCE_TYPE_COPPER_MYTHRA = 12;
	protected final static short RESOURCE_TYPE_COPPER_PLATINITE = 13;
	protected final static short RESOURCE_TYPE_COPPER_POLYSTEEL = 14;
	protected final static short RESOURCE_END_COPPER = 14;

	protected final static short RESOURCE_START_IRON = 15;
	protected final static short RESOURCE_TYPE_IRON_PLUMBUM = 15;
	protected final static short RESOURCE_TYPE_IRON_POLONIUM = 16;
	protected final static short RESOURCE_TYPE_IRON_AXIDITE = 17;
	protected final static short RESOURCE_TYPE_IRON_BRONZIUM = 18;
	protected final static short RESOURCE_TYPE_IRON_COLAT = 19;
	protected final static short RESOURCE_TYPE_IRON_DOLOVITE = 20;
	protected final static short RESOURCE_TYPE_IRON_DOONIUM = 21;
	protected final static short RESOURCE_TYPE_IRON_KAMMRIS = 22;
	protected final static short RESOURCE_END_IRON = 22;

	protected final static short RESOURCE_START_STEEL = 23;
	protected final static short RESOURCE_TYPE_STEEL_RHODIUM = 23;
	protected final static short RESOURCE_TYPE_STEEL_KIIRIUM = 24;
	protected final static short RESOURCE_TYPE_STEEL_CUBIRIAN = 25;
	protected final static short RESOURCE_TYPE_STEEL_THORANIUM = 26;
	protected final static short RESOURCE_TYPE_STEEL_NEUTRONIUM = 27;
	protected final static short RESOURCE_TYPE_STEEL_DURANIUM = 28;
	protected final static short RESOURCE_TYPE_STEEL_DITANIUM = 29;
	protected final static short RESOURCE_TYPE_STEEL_QUADRANIUM = 30;
	protected final static short RESOURCE_TYPE_STEEL_CARBONITE = 31;
	protected final static short RESOURCE_TYPE_STEEL_DURALLOY = 32;
	protected final static short RESOURCE_END_STEEL = 32;

	protected final static short RESOURCE_START_ORE_CARBONATE = 33;
	protected final static short RESOURCE_TYPE_ORE_CARBONATE_ALANTIUM = 33;
	protected final static short RESOURCE_TYPE_ORE_CARBONATE_BARTHIERIUM = 34;
	protected final static short RESOURCE_TYPE_ORE_CARBONATE_CHROMITE = 35;
	protected final static short RESOURCE_TYPE_ORE_CARBONATE_FRASIUM = 36;
	protected final static short RESOURCE_TYPE_ORE_CARBONATE_LOMMITE = 37;
	protected final static short RESOURCE_TYPE_ORE_CARBONATE_OSTRINE = 38;
	protected final static short RESOURCE_TYPE_ORE_CARBONATE_VARIUM = 39;
	protected final static short RESOURCE_TYPE_ORE_CARBONATE_ZINSIAM = 40;
	protected final static short RESOURCE_END_ORE_CARBONATE = 40;

	protected final static short RESOURCE_START_ORE_SILICLASTIC = 41;
	protected final static short RESOURCE_TYPE_ORE_SILICLASTIC_ARDANIUM = 41;
	protected final static short RESOURCE_TYPE_ORE_SILICLASTIC_CORTOSIS = 42;
	protected final static short RESOURCE_TYPE_ORE_SILICLASTIC_CRISM = 43;
	protected final static short RESOURCE_TYPE_ORE_SILICLASTIC_MALAB = 44;
	protected final static short RESOURCE_TYPE_ORE_SILICLASTIC_ROBINDUN = 45;
	protected final static short RESOURCE_TYPE_ORE_SILICLASTIC_TERTIAN = 46;
	protected final static short RESOURCE_END_ORE_SILICLASTIC = 46;

	protected final static short RESOURCE_START_ORE_EXTRUSIVE = 47;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_BENE = 47;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_CHRONAMITE = 48;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_ILLIMIUM = 49;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_KALONTERIUM = 50;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_KESCHEL = 51;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_LIDIUM = 52;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_MARANIUM = 53;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_PHOLOKITE = 54;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_QUADRENIUM = 55;
	protected final static short RESOURCE_TYPE_ORE_EXTRUSIVE_VINTRIUM = 56;
	protected final static short RESOURCE_END_ORE_EXTRUSIVE = 56;

	protected final static short RESOURCE_START_ORE_INTRUSIVE = 57;
	protected final static short RESOURCE_TYPE_ORE_INTRUSIVE_BERUBIUM = 57;
	protected final static short RESOURCE_TYPE_ORE_INTRUSIVE_CHANLON = 58;
	protected final static short RESOURCE_TYPE_ORE_INTRUSIVE_CORINTIUM = 59;
	protected final static short RESOURCE_TYPE_ORE_INTRUSIVE_DERILLIUM = 60;
	protected final static short RESOURCE_TYPE_ORE_INTRUSIVE_ORIDIUM = 61;
	protected final static short RESORUCE_TYPE_ORE_INTRUSIVE_DYLINIUM = 62;
	protected final static short RESOURCE_TYPE_ORE_INTRUSIVE_HOLLINIUM = 63;
	protected final static short RESOURCE_TYPE_ORE_INTRUSIVE_IONITE = 64;
	protected final static short RESOURCE_TYPE_ORE_INTRUSIVE_KATRIUM = 65;
	protected final static short RESOURCE_END_ORE_INTRUSIVE = 65;

	protected final static short RESOURCE_START_GEMSTONE_AMORPHOUS = 66;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_BOSPRIDIUM = 66;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_BARADIUM = 67;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_REGVIS = 68;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_PLEXITE = 69;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_RUDIC = 70;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_RYLL = 71;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_SEDRELLIUM = 72;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_STYGIUM = 73;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_VENDUSII = 74;
	protected final static short RESOURCE_TYPE_GEMSTONE_AMORPHOUS_BALTARAN = 75;
	protected final static short RESOURCE_END_GEMSTONE_AMORPHOUS = 75;

	protected final static short RESOURCE_START_GEMSTONE_CRYSTALLINE = 76;
	protected final static short RESOURCE_TYPE_GEMSTONE_CRYSTALLINE_BYROTHSIS = 76;
	protected final static short RESOURCE_TYPE_GEMSTONE_CRYSTALLINE_GALLINORIAN = 77;
	protected final static short RESOURCE_TYPE_GEMSTONE_CRYSTALLINE_GREEN_DIAMOND = 78;
	protected final static short RESOURCE_TYPE_GEMSTONE_CRYSTALLINE_KEROL_FIREGEM = 79;
	protected final static short RESOURCE_TYPE_GEMSTONE_CRYSTALLINE_SEAFAH_JEWEL = 80;
	protected final static short RESOURCE_TYPE_GEMSTONE_CRYSTALLINE_SORMAHIL_FIREGEM = 81;
	protected final static short RESOURCE_TYPE_GEMSTONE_CRYSTALLINE_LABOI_MINERAL = 82;
	protected final static short RESOURCE_TYPE_GEMSTONE_CRYSTALLINE_VERTEX = 83;
	protected final static short RESOURCE_END_GEMSTONE_CRYSTALLINE = 83;

	protected final static short RESOURCE_START_RADIOACTIVE = 84;
	protected final static short RESOURCE_TYPE_RADIOACTIVE_CLASS1 = 84;
	protected final static short RESOURCE_TYPE_RADIOACTIVE_CLASS2 = 85;
	protected final static short RESOURCE_TYPE_RADIOACTIVE_CLASS3 = 86;
	protected final static short RESOURCE_TYPE_RADIOACTIVE_CLASS4 = 87;
	protected final static short RESOURCE_TYPE_RADIOACTIVE_CLASS5 = 88;
	protected final static short RESOURCE_TYPE_RADIOACTIVE_CLASS6 = 89;
	protected final static short RESOURCE_TYPE_RADIOACTIVE_CLASS7 = 90;
	protected final static short RESOURCE_END_RADIOACTIVE = 90;

	protected final static short RESOURCE_START_UNKNOWN = 91;
	protected final static short RESOURCE_TYPE_UNKNOWN_METAL_FERROUS = 91;
	protected final static short RESOURCE_TYPE_UNKNOWN_METAL_NONFERROUS = 92;
	protected final static short RESOURCE_TYPE_UNKNOWN_ORE_SEDIMENTARY = 93;
	protected final static short RESOURCE_TYPE_UNKNOWN_ORE_IGNEOUS = 94;
	protected final static short RESOURCE_TYPE_UNKNOWN_GEMSTONE = 95;
	protected final static short RESOURCE_TYPE_UNKNOWN_RADIOACTIVE = 96;
	protected final static short RESOURCE_END_UNKNOWN = 96;

	protected final static short RESOURCE_START_PETROCHEMICAL_SOLID = 97;
	protected final static short RESOURCE_TYPE_ENERGY_PETROCHEMICAL_SOLID_CLASS1 = 97;
	protected final static short RESOURCE_TYPE_ENERGY_PETROCHEMICAL_SOLID_CLASS2 = 98;
	protected final static short RESOURCE_TYPE_ENERGY_PETROCHEMICAL_SOLID_CLASS3 = 99;
	protected final static short RESOURCE_TYPE_ENERGY_PETROCHEMICAL_SOLID_CLASS4 = 100;
	protected final static short RESOURCE_TYPE_ENERGY_PETROCHEMICAL_SOLID_CLASS5 = 101;
	protected final static short RESOURCE_TYPE_ENERGY_PETROCHEMICAL_SOLID_CLASS6 = 102;
	protected final static short RESOURCE_TYPE_ENERGY_PETROCHEMICAL_SOLID_CLASS7 = 103;
	protected final static short RESOURCE_TYPE_ENERGY_PETROCHEMICAL_SOLID_UNKNOWN = 104;
	protected final static short RESOURCE_END_PETROCHEMICAL_SOLID = 104;

	protected final static short RESOURCE_START_PETROCHEMICAL_LIQUID = 105;
	protected final static short RESOURCE_TYPE_ENERGY_PERTOCHEMICAL_LIQUID_CLASS1 = 105;
	protected final static short RESOURCE_TYPE_ENERGY_PERTOCHEMICAL_LIQUID_CLASS2 = 106;
	protected final static short RESOURCE_TYPE_ENERGY_PERTOCHEMICAL_LIQUID_CLASS3 = 107;
	protected final static short RESOURCE_TYPE_ENERGY_PERTOCHEMICAL_LIQUID_CLASS4 = 108;
	protected final static short RESOURCE_TYPE_ENERGY_PERTOCHEMICAL_LIQUID_CLASS5 = 109;
	protected final static short RESOURCE_TYPE_ENERGY_PERTOCHEMICAL_LIQUID_CLASS6 = 110;
	protected final static short RESOURCE_TYPE_ENERGY_PERTOCHEMICAL_LIQUID_CLASS7 = 111;
	protected final static short RESOURCE_TYPE_ENERGY_PERTOCHEMICAL_LIQUID_UNKNOWN = 112;
	protected final static short RESOURCE_END_PETROCHEMICAL_LIQUID = 112;

	protected final static short RESOURCE_TYPE_LUBRICATING_OIL = 113;
	protected final static short RESOURCE_TYPE_POLYMER = 114;

	protected final static short RESOURCE_START_GAS_INERT = 115;
	protected final static short RESOURCE_TYPE_GAS_INERT_HYDRON3 = 115;
	protected final static short RESOURCE_TYPE_GAS_INERT_MALIUM = 116;
	protected final static short RESOURCE_TYPE_GAS_INERT_BILAL = 117;
	protected final static short RESOURCE_TYPE_GAS_INERT_CORTHEL = 118;
	protected final static short RESOURCE_TYPE_GAS_INERT_CULSION = 119;
	protected final static short RESOURCE_TYPE_GAS_INERT_DIOXIS = 120;
	protected final static short RESOURCE_TYPE_GAS_INERT_HURLIOTHROMBIC = 121;
	protected final static short RESOURCE_TYPE_GAS_INERT_KAYLON = 122;
	protected final static short RESOURCE_TYPE_GAS_INERT_KORFAISE = 123;
	protected final static short RESOURCE_TYPE_GAS_INERT_METHANAGEN = 124;
	protected final static short RESOURCE_TYPE_GAS_INERT_MIRTH = 125;
	protected final static short RESOURCE_TYPE_GAS_INERT_OBAH = 126;
	protected final static short RESOURCE_TYPE_GAS_INERT_RETHIN = 127;
	protected final static short RESOURCE_TYPE_UNKNOWN_GAS_INERT = 128;
	protected final static short RESOURCE_END_GAS_INERT = 128;

	protected final static short RESOURCE_START_GAS_REACTIVE = 129;
	protected final static short RESOURCE_TYPE_GAS_REACTIVE_ELETON = 129;
	protected final static short RESOURCE_TYPE_GAS_REACTIVE_IROLUNN = 130;
	protected final static short RESOURCE_TYPE_GAS_REACTIVE_METHANE = 131;
	protected final static short RESOURCE_TYPE_GAS_REACTIVE_ORVETH = 132;
	protected final static short RESOURCE_TYPE_GAS_REACTIVE_SIG = 133;
	protected final static short RESOURCE_TYPE_GAS_REACTIVE_SKEVON = 134;
	protected final static short RESOURCE_TYPE_GAS_REACTIVE_TOLIUM = 135;
	protected final static short RESOURCE_TYPE_UNKNOWN_GAS_REACTIVE = 136;
	protected final static short RESOURCE_END_GAS_REACTIVE = 136;

	protected final static short RESOURCE_START_JTL = 137;
	protected final static short RESOURCE_TYPE_JTL_ALUMINUM_PEROVSKITIC = 137;
	protected final static short RESOURCE_TYPE_JTL_COPPER_BORCARBANTIUM = 138;
	protected final static short RESOURCE_TYPE_JTL_STEEL_BICORBANTIUM = 139;
	protected final static short RESOURCE_TYPE_JTL_STEEL_ARVESHIUM = 140;
	protected final static short RESOURCE_TYPE_JTL_RADIOACTIVE_PLOYMETRIC = 141;
	protected final static short RESOURCE_TYPE_JTL_GAS_REACTIVE_UNSTABLE_ORGANOMETALLIC = 142;
	protected final static short RESOURCE_TYPE_JTL_ORE_SILICLASTIC_FERMIONIC = 143;
	protected final static short RESOURCE_END_JTL = 143;

	protected final static short RESOURCE_START_PLANETARY = 144;
	protected final static short RESOURCE_START_WATER = 144;
	protected final static short RESOURCE_TYPE_WATER_CORELLIAN = 144;
	protected final static short RESOURCE_TYPE_WATER_DANTOOINE = 145;
	protected final static short RESOURCE_TYPE_WATER_DATHOMIRIAN = 146;
	protected final static short RESOURCE_TYPE_WATER_ENDORIAN = 147;
	protected final static short RESOURCE_TYPE_WATER_LOKIAN = 148;
	protected final static short RESOURCE_TYPE_WATER_NABOOINIAN = 149;
	protected final static short RESOURCE_TYPE_WATER_RORI = 150;
	protected final static short RESOURCE_TYPE_WATER_TALUSIAN = 151;
	protected final static short RESOURCE_TYPE_WATER_TATOOINIAN = 152;
	protected final static short RESOURCE_TYPE_WATER_YAVINIAN = 153;
	protected final static short RESOURCE_END_WATER = 153;

	protected final static short RESOURCE_TYPE_ENERGY_WIND_CORELLIAN = 154;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_DANTOOINE = 155;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_DATHOMIRIAN = 156;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_ENDORIAN = 157;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_LOKIAN = 158;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_NABOOINIAN = 159;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_RORI = 160;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_TALUSIAN = 161;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_TATOOINIAN = 162;
	protected final static short RESOURCE_TYPE_ENERGY_WIND_YAVINIAN = 163;

	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_CORELLIAN = 164;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_DANTOOINE = 165;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_DATHOMIRIAN = 166;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_ENDORIAN = 167;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_LOKIAN = 168;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_NABOOINIAN = 169;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_RORI = 170;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_TALUSIAN = 171;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_TATOOINIAN = 172;
	protected final static short RESOURCE_TYPE_ENERGY_SOLAR_YAVINIAN = 173;

	protected final static short RESOURCE_TYPE_WOOD_CONIFER_CORELLIAN = 174;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_DANTOOINE = 175;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_DATHOMIRIAN = 176;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_ENDORIAN = 177;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_LOKIAN = 178;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_NABOOINIAN = 179;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_RORI = 180;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_TALUSIAN = 181;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_TATOOINIAN = 182;
	protected final static short RESOURCE_TYPE_WOOD_CONIFER_YAVINIAN = 183;

	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_CORELLIAN = 184;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_DANTOOINE = 185;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_DATHOMIRIAN = 186;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_ENDORIAN = 187;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_LOKIAN = 188;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_NABOOINIAN = 189;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_RORI = 190;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_TALUSIAN = 191;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_TATOOINIAN = 192;
	protected final static short RESOURCE_TYPE_WOOD_EVERGREEN_YAVINIAN = 193;

	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_CORELLIAN = 194;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_DANTOOINE = 195;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_DATHOMIRIAN = 196;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_ENDORIAN = 197;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_LOKIAN = 198;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_NABOOINIAN = 199;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_RORI = 200;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_TALUSIAN = 201;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_TATOOINIAN = 202;
	protected final static short RESOURCE_TYPE_WOOD_DECIDUOUS_YAVINIAN = 203;

	protected final static short RESOURCE_TYPE_FLOWERS_CORELLIAN = 204;
	protected final static short RESOURCE_TYPE_FLOWERS_DANTOOINE = 205;
	protected final static short RESOURCE_TYPE_FLOWERS_DATHOMIRIAN = 206;
	protected final static short RESOURCE_TYPE_FLOWERS_ENDORIAN = 207;
	protected final static short RESOURCE_TYPE_FLOWERS_LOKIAN = 208;
	protected final static short RESOURCE_TYPE_FLOWERS_NABOOINIAN = 209;
	protected final static short RESOURCE_TYPE_FLOWERS_RORI = 210;
	protected final static short RESOURCE_TYPE_FLOWERS_TALUSIAN = 211;
	protected final static short RESOURCE_TYPE_FLOWERS_TATOOINIAN = 212;
	protected final static short RESOURCE_TYPE_FLOWERS_YAVINIAN = 213;

	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_CORELLIAN = 214;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_DANTOOINE = 215;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_DATHOMIRIAN = 216;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_ENDORIAN = 217;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_LOKIAN = 218;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_NABOOINIAN = 219;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_RORI = 220;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_TALUSIAN = 221;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_TATOOINIAN = 222;
	protected final static short RESOURCE_TYPE_DOMESTICATED_CORN_YAVINIAN = 223;

	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_CORELLIAN = 224;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_DANTOOINE = 225;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_DATHOMIRIAN = 226;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_ENDORIAN = 227;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_LOKIAN = 228;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_NABOOINIAN = 229;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_RORI = 230;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_TALUSIAN = 231;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_TATOOINIAN = 232;
	protected final static short RESOURCE_TYPE_DOMESTICATED_OATS_YAVINIAN = 233;

	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_CORELLIAN = 234;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_DANTOOINE = 235;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_DATHOMIRIAN = 236;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_ENDORIAN = 237;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_LOKIAN = 238;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_NABOOINIAN = 239;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_RORI = 240;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_TALUSIAN = 241;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_TATOOINIAN = 242;
	protected final static short RESOURCE_TYPE_DOMESTICATED_RICE_YAVINIAN = 243;

	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_CORELLIAN = 244;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_DANTOOINE = 245;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_DATHOMIRIAN = 246;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_ENDORIAN = 247;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_LOKIAN = 248;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_NABOOINIAN = 249;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_RORI = 250;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_TALUSIAN = 251;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_TATOOINIAN = 252;
	protected final static short RESOURCE_TYPE_DOMESTICATED_WHEAT_YAVINIAN = 253;

	protected final static short RESOURCE_TYPE_WILD_CORN_CORELLIAN = 254;
	protected final static short RESOURCE_TYPE_WILD_CORN_DANTOOINE = 255;
	protected final static short RESOURCE_TYPE_WILD_CORN_DATHOMIRIAN = 256;
	protected final static short RESOURCE_TYPE_WILD_CORN_ENDORIAN = 257;
	protected final static short RESOURCE_TYPE_WILD_CORN_LOKIAN = 258;
	protected final static short RESOURCE_TYPE_WILD_CORN_NABOOINIAN = 259;
	protected final static short RESOURCE_TYPE_WILD_CORN_RORI = 260;
	protected final static short RESOURCE_TYPE_WILD_CORN_TALUSIAN = 261;
	protected final static short RESOURCE_TYPE_WILD_CORN_TATOOINIAN = 262;
	protected final static short RESOURCE_TYPE_WILD_CORN_YAVINIAN = 263;

	protected final static short RESOURCE_TYPE_WILD_OATS_CORELLIAN = 264;
	protected final static short RESOURCE_TYPE_WILD_OATS_DANTOOINE = 265;
	protected final static short RESOURCE_TYPE_WILD_OATS_DATHOMIRIAN = 266;
	protected final static short RESOURCE_TYPE_WILD_OATS_ENDORIAN = 267;
	protected final static short RESOURCE_TYPE_WILD_OATS_LOKIAN = 268;
	protected final static short RESOURCE_TYPE_WILD_OATS_NABOOINIAN = 269;
	protected final static short RESOURCE_TYPE_WILD_OATS_RORI = 270;
	protected final static short RESOURCE_TYPE_WILD_OATS_TALUSIAN = 271;
	protected final static short RESOURCE_TYPE_WILD_OATS_TATOOINIAN = 272;
	protected final static short RESOURCE_TYPE_WILD_OATS_YAVINIAN = 273;

	protected final static short RESOURCE_TYPE_WILD_RICE_CORELLIAN = 274;
	protected final static short RESOURCE_TYPE_WILD_RICE_DANTOOINE = 275;
	protected final static short RESOURCE_TYPE_WILD_RICE_DATHOMIRIAN = 276;
	protected final static short RESOURCE_TYPE_WILD_RICE_ENDORIAN = 277;
	protected final static short RESOURCE_TYPE_WILD_RICE_LOKIAN = 278;
	protected final static short RESOURCE_TYPE_WILD_RICE_NABOOINIAN = 279;
	protected final static short RESOURCE_TYPE_WILD_RICE_RORI = 280;
	protected final static short RESOURCE_TYPE_WILD_RICE_TALUSIAN = 281;
	protected final static short RESOURCE_TYPE_WILD_RICE_TATOOINIAN = 282;
	protected final static short RESOURCE_TYPE_WILD_RICE_YAVINIAN = 283;

	protected final static short RESOURCE_TYPE_WILD_WHEAT_CORELLIAN = 284;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_DANTOOINE = 285;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_DATHOMIRIAN = 286;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_ENDORIAN = 287;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_LOKIAN = 288;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_NABOOINIAN = 289;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_RORI = 290;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_TALUSIAN = 291;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_TATOOINIAN = 292;
	protected final static short RESOURCE_TYPE_WILD_WHEAT_YAVINIAN = 293;

	protected final static short RESOURCE_TYPE_BEANS_CORELLIAN = 294;
	protected final static short RESOURCE_TYPE_BEANS_DANTOOINE = 295;
	protected final static short RESOURCE_TYPE_BEANS_DATHOMIRIAN = 296;
	protected final static short RESOURCE_TYPE_BEANS_ENDORIAN = 297;
	protected final static short RESOURCE_TYPE_BEANS_LOKIAN = 298;
	protected final static short RESOURCE_TYPE_BEANS_NABOOINIAN = 299;
	protected final static short RESOURCE_TYPE_BEANS_RORI = 300;
	protected final static short RESOURCE_TYPE_BEANS_TALUSIAN = 301;
	protected final static short RESOURCE_TYPE_BEANS_TATOOINIAN = 302;
	protected final static short RESOURCE_TYPE_BEANS_YAVINIAN = 303;

	protected final static short RESOURCE_TYPE_BERRIES_CORELLIAN = 304;
	protected final static short RESOURCE_TYPE_BERRIES_DANTOOINE = 305;
	protected final static short RESOURCE_TYPE_BERRIES_DATHOMIRIAN = 306;
	protected final static short RESOURCE_TYPE_BERRIES_ENDORIAN = 307;
	protected final static short RESOURCE_TYPE_BERRIES_LOKIAN = 308;
	protected final static short RESOURCE_TYPE_BERRIES_NABOOINIAN = 309;
	protected final static short RESOURCE_TYPE_BERRIES_RORI = 310;
	protected final static short RESOURCE_TYPE_BERRIES_TALUSIAN = 311;
	protected final static short RESOURCE_TYPE_BERRIES_TATOOINIAN = 312;
	protected final static short RESOURCE_TYPE_BERRIES_YAVINIAN = 313;

	protected final static short RESOURCE_TYPE_FUNGUS_CORELLIAN = 314;
	protected final static short RESOURCE_TYPE_FUNGUS_DANTOOINE = 315;
	protected final static short RESOURCE_TYPE_FUNGUS_DATHOMIRIAN = 316;
	protected final static short RESOURCE_TYPE_FUNGUS_ENDORIAN = 317;
	protected final static short RESOURCE_TYPE_FUNGUS_LOKIAN = 318;
	protected final static short RESOURCE_TYPE_FUNGUS_NABOOINIAN = 319;
	protected final static short RESOURCE_TYPE_FUNGUS_RORI = 320;
	protected final static short RESOURCE_TYPE_FUNGUS_TALUSIAN = 321;
	protected final static short RESOURCE_TYPE_FUNGUS_TATOOINIAN = 322;
	protected final static short RESOURCE_TYPE_FUNGUS_YAVINIAN = 323;

	protected final static short RESOURCE_TYPE_GREENS_CORELLIAN = 324;
	protected final static short RESOURCE_TYPE_GREENS_DANTOOINE = 325;
	protected final static short RESOURCE_TYPE_GREENS_DATHOMIRIAN = 326;
	protected final static short RESOURCE_TYPE_GREENS_ENDORIAN = 327;
	protected final static short RESOURCE_TYPE_GREENS_LOKIAN = 328;
	protected final static short RESOURCE_TYPE_GREENS_NABOOINIAN = 329;
	protected final static short RESOURCE_TYPE_GREENS_RORI = 330;
	protected final static short RESOURCE_TYPE_GREENS_TALUSIAN = 331;
	protected final static short RESOURCE_TYPE_GREENS_TATOOINIAN = 332;
	protected final static short RESOURCE_TYPE_GREENS_YAVINIAN = 333;

	protected final static short RESOURCE_TYPE_TUBERS_CORELLIAN = 334;
	protected final static short RESOURCE_TYPE_TUBERS_DANTOOINE = 335;
	protected final static short RESOURCE_TYPE_TUBERS_DATHOMIRIAN = 336;
	protected final static short RESOURCE_TYPE_TUBERS_ENDORIAN = 337;
	protected final static short RESOURCE_TYPE_TUBERS_LOKIAN = 338;
	protected final static short RESOURCE_TYPE_TUBERS_NABOOINIAN = 339;
	protected final static short RESOURCE_TYPE_TUBERS_RORI = 340;
	protected final static short RESOURCE_TYPE_TUBERS_TALUSIAN = 341;
	protected final static short RESOURCE_TYPE_TUBERS_TATOOINIAN = 342;
	protected final static short RESOURCE_TYPE_TUBERS_YAVINIAN = 343;

	protected final static short RESOURCE_TYPE_BONE_ANIMAL_CORELLIAN = 344;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_DANTOOINE = 345;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_DATHOMIRIAN = 346;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_ENDORIAN = 347;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_LOKIAN = 348;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_NABOOINIAN = 349;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_RORI = 350;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_TALUSIAN = 351;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_TATOOINIAN = 352;
	protected final static short RESOURCE_TYPE_BONE_ANIMAL_YAVINIAN = 353;

	protected final static short RESOURCE_TYPE_BONE_AVIAN_CORELLIAN = 354;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_DANTOOINE = 355;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_DATHOMIRIAN = 356;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_ENDORIAN = 357;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_LOKIAN = 358;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_NABOOINIAN = 359;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_RORI = 360;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_TALUSIAN = 361;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_TATOOINIAN = 362;
	protected final static short RESOURCE_TYPE_BONE_AVIAN_YAVINIAN = 363;

	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_CORELLIAN = 364;
	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_DANTOOINE = 365;
	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_DATHOMIRIAN = 366;
	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_ENDORIAN = 367;
	protected final static short RESOURCE_TYPE_HIDE_BRITLEY_LOKIAN = 368;
	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_NABOOINIAN = 369;
	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_RORI = 370;
	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_TALUSIAN = 371;
	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_TATOOINIAN = 372;
	protected final static short RESOURCE_TYPE_HIDE_BRISTLEY_YAVINIAN = 373;

	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_CORELLIAN = 374;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_DANTOOINE = 375;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_DATHOMIRIAN = 376;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_ENDORIAN = 377;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_LOKIAN = 378;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_NABOOINIAN = 379;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_RORI = 380;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_TALUSIAN = 381;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_TATOOINIAN = 382;
	protected final static short RESOURCE_TYPE_HIDE_LEATHERY_YAVINIAN = 383;

	protected final static short RESOURCE_TYPE_HIDE_SCALEY_CORELLIAN = 384;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_DANTOOINE = 385;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_DATHOMIRIAN = 386;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_ENDORIAN = 387;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_LOKIAN = 388;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_NABOOINIAN = 389;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_RORI = 390;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_TALUSIAN = 391;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_TATOOINIAN = 392;
	protected final static short RESOURCE_TYPE_HIDE_SCALEY_YAVINIAN = 393;

	protected final static short RESOURCE_TYPE_HIDE_WOOLY_CORELLIAN = 394;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_DANTOOINE = 395;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_DATHOMIRIAN = 396;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_ENDORIAN = 397;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_LOKIAN = 398;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_NABOOINIAN = 399;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_RORI = 400;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_TALUSIAN = 401;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_TATOOINIAN = 402;
	protected final static short RESOURCE_TYPE_HIDE_WOOLY_YAVINIAN = 403;

	protected final static short RESOURCE_TYPE_MEAT_AVIAN_CORELLIAN = 404;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_DANTOOINE = 405;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_DATHOMIRIAN = 406;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_ENDORIAN = 407;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_LOKIAN = 408;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_NABOOINIAN = 409;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_RORI = 410;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_TALUSIAN = 411;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_TATOOINIAN = 412;
	protected final static short RESOURCE_TYPE_MEAT_AVIAN_YAVINIAN = 413;

	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_CORELLIAN = 414;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_DANTOOINE = 415;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_DATHOMIRIAN = 416;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_ENDORIAN = 417;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_LOKIAN = 418;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_NABOOINIAN = 419;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_RORI = 420;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_TALUSIAN = 421;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_TATOOINIAN = 422;
	protected final static short RESOURCE_TYPE_MEAT_CARNIVORE_YAVINIAN = 423;

	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_CORELLIAN = 424;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_DANTOOINE = 425;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_DATHOMIRIAN = 426;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_ENDORIAN = 427;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_LOKIAN = 428;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_NABOOINIAN = 429;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_RORI = 430;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_TALUSIAN = 431;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_TATOOINIAN = 432;
	protected final static short RESOURCE_TYPE_MEAT_DOMESTICATED_YAVINIAN = 433;

	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_CORELLIAN = 434;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_DANTOOINE = 435;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_DATHOMIRIAN = 436;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_ENDORIAN = 437;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_LOKIAN = 438;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_NABOOINIAN = 439;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_RORI = 440;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_TALUSIAN = 441;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_TATOOINIAN = 442;
	protected final static short RESOURCE_TYPE_MEAT_HERBIVORE_YAVINIAN = 443;

	protected final static short RESOURCE_TYPE_MEAT_INSECT_CORELLIAN = 444;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_DANTOOINE = 445;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_DATHOMIRIAN = 446;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_ENDORIAN = 447;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_LOKIAN = 448;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_NABOOINIAN = 449;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_RORI = 450;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_TALUSIAN = 451;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_TATOOINIAN = 452;
	protected final static short RESOURCE_TYPE_MEAT_INSECT_YAVINIAN = 453;

	protected final static short RESOURCE_TYPE_MEAT_WILD_CORELLIAN = 454;
	protected final static short RESOURCE_TYPE_MEAT_WILD_DANTOOINE = 455;
	protected final static short RESOURCE_TYPE_MEAT_WILD_DATHOMIRIAN = 456;
	protected final static short RESOURCE_TYPE_MEAT_WILD_ENDORIAN = 457;
	protected final static short RESOURCE_TYPE_MEAT_WILD_LOKIAN = 458;
	protected final static short RESOURCE_TYPE_MEAT_WILD_NABOOINIAN = 459;
	protected final static short RESOURCE_TYPE_MEAT_WILD_RORI = 460;
	protected final static short RESOURCE_TYPE_MEAT_WILD_TALUSIAN = 461;
	protected final static short RESOURCE_TYPE_MEAT_WILD_TATOOINIAN = 462;
	protected final static short RESOURCE_TYPE_MEAT_WILD_YAVINIAN = 463;

	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_CORELLIAN = 464;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_DANTOOINE = 465;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_DATHOMIRIAN = 466;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_ENDORIAN = 467;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_LOKIAN = 468;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_NABOOINIAN = 469;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_RORI = 470;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_TALUSIAN = 471;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_TATOOINIAN = 472;
	protected final static short RESOURCE_TYPE_MILK_DOMESTICATED_YAVINIAN = 473;

	protected final static short RESOURCE_TYPE_MILK_WILD_CORELLIAN = 474;
	protected final static short RESOURCE_TYPE_MILK_WILD_DANTOOINE = 475;
	protected final static short RESOURCE_TYPE_MILK_WILD_DATHOMIRIAN = 476;
	protected final static short RESOURCE_TYPE_MILK_WILD_ENDORIAN = 477;
	protected final static short RESOURCE_TYPE_MILK_WILD_LOKIAN = 478;
	protected final static short RESOURCE_TYPE_MILK_WILD_NABOOINIAN = 479;
	protected final static short RESOURCE_TYPE_MILK_WILD_RORI = 480;
	protected final static short RESOURCE_TYPE_MILK_WILD_TALUSIAN = 481;
	protected final static short RESOURCE_TYPE_MILK_WILD_TATOOINIAN = 482;
	protected final static short RESOURCE_TYPE_MILK_WILD_YAVINIAN = 483;

	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_CORELLIAN = 484;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_DANTOOINE = 485;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_DATHOMIRIAN = 486;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_ENDORIAN = 487;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_LOKIAN = 488;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_NABOOINIAN = 489;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_RORI = 490;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_TALUSIAN = 491;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_TATOOINIAN = 492;
	protected final static short RESOURCE_TYPE_MEAT_CRUSTACEAN_YAVINIAN = 493;

	protected final static short RESOURCE_TYPE_MEAT_FISH_CORELLIAN = 494;
	protected final static short RESOURCE_TYPE_MEAT_FISH_DANTOOINE = 495;
	protected final static short RESOURCE_TYPE_MEAT_FISH_DATHOMIRIAN = 496;
	protected final static short RESOURCE_TYPE_MEAT_FISH_ENDORIAN = 497;
	protected final static short RESOURCE_TYPE_MEAT_FISH_LOKIAN = 498;
	protected final static short RESOURCE_TYPE_MEAT_FISH_NABOOINIAN = 499;
	protected final static short RESOURCE_TYPE_MEAT_FISH_RORI = 500;
	protected final static short RESOURCE_TYPE_MEAT_FISH_TALUSIAN = 501;
	protected final static short RESOURCE_TYPE_MEAT_FISH_TATOOINIAN = 502;
	protected final static short RESOURCE_TYPE_MEAT_FISH_YAVINIAN = 503;

	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_CORELLIAN = 504;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_DANTOOINE = 505;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_DATHOMIRIAN = 506;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_ENDORIAN = 507;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_LOKIAN = 508;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_NABOOINIAN = 509;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_RORI = 510;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_TALUSIAN = 511;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_TATOOINIAN = 512;
	protected final static short RESOURCE_TYPE_MEAT_MOLLUSK_YAVINIAN = 513;

	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_CORELLIAN = 514;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_DANTOOINE = 515;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_DATHOMIRIAN = 516;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_ENDORIAN = 517;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_LOKIAN = 518;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_NABOOINIAN = 519;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_RORI = 520;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_TALUSIAN = 521;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_TATOOINIAN = 522;
	protected final static short RESOURCE_TYPE_MEAT_REPTILIAN_YAVINIAN = 523;

	protected final static short RESOURCE_TYPE_EGGS_CORELLIAN = 524;
	protected final static short RESOURCE_TYPE_EGGS_DANTOOINE = 525;
	protected final static short RESOURCE_TYPE_EGGS_DATHOMIRIAN = 526;
	protected final static short RESOURCE_TYPE_EGGS_ENDORIAN = 527;
	protected final static short RESOURCE_TYPE_EGGS_LOKIAN = 528;
	protected final static short RESOURCE_TYPE_EGGS_NABOOINIAN = 529;
	protected final static short RESOURCE_TYPE_EGGS_RORI = 530;
	protected final static short RESOURCE_TYPE_EGGS_TALUSIAN = 531;
	protected final static short RESOURCE_TYPE_EGGS_TATOOINIAN = 532;
	protected final static short RESOURCE_TYPE_EGGS_YAVINIAN = 533;

	protected final static short RESOURCE_TYPE_FIBERPLAST_CORELLIAN = 534;
	protected final static short RESOURCE_TYPE_FIBERPLAST_DANTOOINE = 535;
	protected final static short RESOURCE_TYPE_FIBERPLAST_DATHOMIRIAN = 536;
	protected final static short RESOURCE_TYPE_FIBERPLAST_ENDORIAN = 537;
	protected final static short RESOURCE_TYPE_FIBERPLAST_LOKIAN = 538;
	protected final static short RESOURCE_TYPE_FIBERPLAST_NABOOINIAN = 539;
	protected final static short RESOURCE_TYPE_FIBERPLAST_RORI = 540;
	protected final static short RESOURCE_TYPE_FIBERPLAST_TALUSIAN = 541;
	protected final static short RESOURCE_TYPE_FIBERPLAST_TATOOINIAN = 542;
	protected final static short RESOURCE_TYPE_FIBERPLAST_YAVINIAN = 543;
	protected final static short RESOURCE_END_PLANETARY = 543;

	protected final static int RESOURCE_QUALITY_COUNT = 11;
	protected final static int RESOURCE_CAPS = 2;

	protected final static byte HARVESTER_TYPE_MINERAL = 0;
	protected final static byte HARVESTER_TYPE_CHEMICAL = 1;
	protected final static byte HARVESTER_TYPE_FLORA = 2;
	protected final static byte HARVESTER_TYPE_WATER = 3;
	protected final static byte HARVESTER_TYPE_GAS = 4;
	protected final static byte HARVESTER_TYPE_WIND = 5;
	protected final static byte HARVESTER_TYPE_SOLAR = 6;
	protected final static byte HARVESTER_TYPE_FUSION = 7;

	protected final static int CHAT_CODE_SUCCESS = 0;
	protected final static int CHAT_CODE_FAILED_OFFLINE = 4;

	protected final static String BASE_PLAYER_STRING = "@base_player:swg";
	// --------------------------------------------------------------------------------------------------
	// SUI STUFF
	// --------------------------------------------------------------------------------------------------
	protected final static byte SUI_WINDOW_CREATE_COMPONENT = (byte) 3;
	protected final static byte SUI_WINDOW_ADD_COMPONENT = (byte) 4;
	protected final static byte SUI_WINDOW_MODIFY_COMPONENT = (byte) 5;

	protected final static String SUI_SCRIPT_LISTBOX = "Script.listBox";
	protected final static String SUI_WINDOW_HANDLE_SET_RANGE = "handleSetRange"; // String
	// 1.
	protected final static String SUI_WINDOW_LIST_LSTLIST = "List.lstList"; // String
	// 2
	protected final static String SUI_WINDOW_LIST_DATALIST = "List.dataList";
	protected final static String SUI_WINDOW_PROMPT_LBLPROMPT = "Prompt.lblPrompt";
	protected final static String SUI_WINDOW_SELECTED_ROW = "SelectedRow"; // String
	// 3
	protected final static String SUI_WINDOW_BG_CAPTION_TITLE = "bg.caption.lblTitle"; // String
	// 4
	protected final static String SUI_WINDOW_LABEL_TYPE_TEXT = "Text"; // String
	// 5
	protected final static String SUI_WINDOW_SURVEY_SELECT_RANGE = "@survey:select_range";
	protected final static String SUI_WINDOW_BUTTON_CANCEL = "btnCancel";
	protected final static String SUI_COMPONENT_ENABLED = "Enabled";
	protected final static String SUI_COMPONENT_VISIBLE = "Visible";
	protected final static String SUI_WINDOW_OK = "@ok";
	protected final static String SUI_WINDOW_BUTTON_OK = "btnOk";
	protected final static String SUI_WINDOW_LIST_HEADER_NAME = "Name";
	protected final static String SUI_SURVEY_RANGE_OPTION_0 = "64m x 3 pts";
	protected final static String SUI_SURVEY_RANGE_OPTION_1 = "128m x 4pts";
	protected final static String SUI_SURVEY_RANGE_OPTION_2 = "192m x 4pts";
	protected final static String SUI_SURVEY_RANGE_OPTION_3 = "256m x 5pts";
	protected final static String SUI_SURVEY_RANGE_OPTION_4 = "320m x 5pts";

	protected final static String[] vMoodStrings = { "none", "absentminded",
			"amazed", "amused", "angry", "approving", "bitter", "bloodthirsty",
			"brave", "callous", "careful", "careless", "casual", "clinical",
			"cocky", "cold", "compassionate", "condescending", "confident",
			"confused", "content", "courtly", "coy", "crude", "cruel",
			"curious", "cynical", "defensive", "depressed", "devious",
			"dimwitted", "disappointed", "discreet", "disgruntled",
			"disgusted", "dismayed", "disoriented", "distracted", "doubtful",
			"dramatic", "dreamy", "drunk", "earnest", "ecstatic",
			"embarrassed", "emphatic", "encouraging", "enthusiastic", "evil",
			"exasperated", "exuberant", "fanatical", "forgive", "frustrated",
			"guilty", "happy", "honest", "hopeful", "hopeless", "humble",
			"hysterical", "imploring", "indifferent", "indignant",
			"interested", "jealous", "joyful", "lofty", "loud", "loving",
			"lustful", "mean", "mischievous", "nervous", "neutral", "offended",
			"optimistic", "pedantic", "pessimistic", "petulant",
			"philosophical", "pitying", "playful", "polite", "pompous",
			"proud", "provocative", "puzzled", "regretful", "relieved",
			"reluctant", "resigned", "respectful", "romantic", "rude", "sad",
			"sarcastic", "scared", "scolding", "scornful", "serious",
			"shameless", "shocked", "shy", "sincere", "sleepy", "sly", "smug",
			"snobby", "sorry", "spiteful", "stubborn", "sullen", "suspicious",
			"taunting", "terrified", "thankful", "thoughtful", "tolerant",
			"uncertain", "unhappy", "unwilling", "warm", "whiny", "wicked",
			"wistful", "worried", "tired", "exhausted", "friendly", "timid",
			"lazy", "surprised", "innocent", "wise", "youthful", "adventurous",
			"annoyed", "perturbed", "sedate", "calm", "suffering", "hungry",
			"thirsty", "alert", "shifty", "relaxed", "crotchety", "surly",
			"painful", "wounded", "bubbly", "heroic", "quiet", "remorseful",
			"grumpy", "logical", "emotional", "troubled", "panicked", "nice",
			"cheerful", "emotionless", "gloomy", "ambivalent", "envious",
			"vengeful", "fearful", "enraged", "sheepish", "belligerent",
			"obnoxious", "fastidious", "squeamish", "dainty", "dignified",
			"haughty", "obscure", "goofy", "silly", "disdainful",
			"contemptuous", "diplomatic", "wary", "malevolent", "hurried",
			"patient", "firm", };
	// if u add more syllables or names update the counts
	protected final static int cntNameSyllable1Count = 129; // theres 130 but
	// for random
	// generators 0 to
	// 129 is 130
	protected final static String[] vNameSyllables1 = { "Aa", "Ba", "Ca", "Da",
			"Ea", "Fa", "Ga", "Ha", "Ia", "Ja", "Ka", "La", "Ma", "Na", "Oa",
			"Pa", "Qa", "Ra", "Sa", "Ta", "Ua", "Va", "Wa", "Xa", "Ya", "Za",
			"Ae", "Be", "Ce", "De", "Ee", "Fe", "Ge", "He", "Ie", "Je", "Ke",
			"Le", "Me", "Ne", "Oe", "Pe", "Qe", "Re", "Se", "Te", "Ue", "Ve",
			"We", "Xe", "Ye", "Ze", "Ai", "Bi", "Ci", "Di", "Ei", "Fi", "Gi",
			"Hi", "Ii", "Ji", "Ki", "Li", "Mi", "Ni", "Oi", "Pi", "Qi", "Ri",
			"Si", "Ti", "Ui", "Vi", "Wi", "Xi", "Yi", "Zi", "Ao", "Bo", "Co",
			"Do", "Eo", "Fo", "Go", "Ho", "Io", "Jo", "Ko", "Lo", "Mo", "No",
			"Oo", "Po", "Qo", "Ro", "So", "To", "Uo", "Vo", "Wo", "Xo", "Yo",
			"Zo", "Au", "Bu", "Cu", "Du", "Eu", "Fu", "Gu", "Hu", "Iu", "Ju",
			"Ku", "Lu", "Mu", "Nu", "Ou", "Pu", "Qu", "Ru", "Su", "Tu", "Uu",
			"Vu", "Wu", "Xu", "Yu", "Zu",

	};
	protected final static int cntNameSyllable2Count = 104; // theres 105 but
	// for random
	// generators 0 to
	// 104 is 105
	protected final static String[] vNameSyllables2 = { "ba", "be", "bi", "bo",
			"bu", "ca", "ce", "ci", "co", "cu", "da", "de", "di", "do", "du",
			"fa", "fe", "fi", "fo", "fu", "ga", "ge", "gi", "go", "gu", "ha",
			"he", "hi", "ho", "hu", "ja", "je", "ji", "jo", "ju", "ka", "ke",
			"ki", "ko", "ku", "la", "le", "li", "lo", "lu", "ma", "me", "mi",
			"mo", "mu", "na", "ne", "ni", "no", "nu", "pa", "pe", "pi", "po",
			"pu", "qa", "qe", "qi", "qo", "qu", "ra", "re", "ri", "ro", "ru",
			"sa", "se", "si", "so", "su", "ta", "te", "ti", "to", "tu", "va",
			"ve", "vi", "vo", "vu", "wa", "we", "wi", "wo", "wu", "xa", "xe",
			"xi", "xo", "xu", "ya", "ye", "yi", "yo", "yu", "za", "ze", "zi",
			"zo", "zu",

	};
	protected final static int cntNameSyllable3Count = 129;
	protected final static String[] vNameSyllables3 = { "aa", "ab", "ac", "ad",
			"ae", "af", "ag", "ah", "ai", "aj", "ak", "al", "am", "an", "ao",
			"ap", "aq", "ar", "as", "at", "au", "av", "aw", "ax", "ay", "az",
			"ea", "eb", "ec", "ed", "ee", "ef", "eg", "eh", "ei", "ej", "ek",
			"el", "em", "en", "eo", "ep", "eq", "er", "es", "et", "eu", "ev",
			"ew", "ex", "ey", "ez", "ia", "ib", "ic", "id", "ie", "if", "ig",
			"ih", "ii", "ij", "ik", "il", "im", "in", "io", "ip", "iq", "ir",
			"is", "it", "iu", "iv", "iw", "ix", "iy", "iz", "oa", "ob", "oc",
			"od", "oe", "of", "og", "oh", "oi", "oj", "ok", "ol", "om", "on",
			"oo", "op", "oq", "or", "os", "ot", "ou", "ov", "ow", "ox", "oy",
			"oz", "ua", "ub", "uc", "ud", "ue", "uf", "ug", "uh", "ui", "uj",
			"uk", "ul", "um", "un", "uo", "up", "uq", "ur", "us", "ut", "uu",
			"uv", "uw", "ux", "uy", "uz",

	};
	protected final static int cntLastNames1Count = 31;
	protected final static String[] vLastNames1 = { "Sky",// 0
			"Night",// 1
			"River",// 2
			"Wander",// 3
			"Break",// 4
			"Dark",// 5
			"Resting",// 6
			"Walking",// 7
			"Running",// 8
			"Light",// 9
			"Grieving",// 10
			"Silent",// 11
			"Strong",// 12
			"Nova",// 13
			"Star",// 14
			"Standing",// 15
			"Cutting",// 16
			"Swimming",// 17
			"Nebula",// 18
			"Kneeling",// 19
			"Broken",// 20
			"Rewa",// 21
			"Wind",// 22
			"Frown",// 23
			"Trawk",// 24
			"Fallen",// 25
			"Forsook",// 26
			"Led",// 27
			"Weeping",// 28
			"Lowt",// 29
			"Faint",// 30
			"Fly",// 31
			"Dawn",// 32

	};
	protected final static int cntLastNames2Count = 36;
	protected final static String[] vLastNames2 = { "bright",// 0
			"white",// 1
			"raw",// 2
			"high",// 3
			"arrow",// 4
			"dry",// 5
			"straight",// 6
			"swift",// 7
			"young",// 8
			"hissing",// 9
			"wind",// 10
			"water",// 11
			"thunder",// 12
			"lightning",// 13
			"rab",// 14
			"mag",// 15
			"grieving",// 16
			"lone",// 17
			"walker",// 18
			"red",// 19
			"rider",// 20
			"weary",// 21
			"gleaming",// 22
			"storm",// 23
			"rain",// 24
			"water",// 25
			"wind",// 26
			"sound",// 27
			"whisper",// 28
			"harsh",// 29
			"hushed",// 30
			"smoke",// 31
			"fett",// 32
			"sidious",// 33
			"sad",// 34
			"fly",// 35
			"mist",// 36
			"sabre",// 37
	};
	protected final static int cntWookieNameSyllable1Count = 120;
	protected final static String[] vWookieNameSyllables1 = { "Ra", "Re", "Ri",
			"Ro", "Ru", "Rr", "Rc", "Rk", "Rw", "Rb", "Rch", "Ca", "Ce", "Ci",
			"Co", "Cu", "Cr", "Cc", "Ck", "Cw", "Cb", "Cch", "Ka", "Ke", "Ki",
			"Ko", "Ku", "Kr", "Kc", "Kk", "Kw", "Kb", "Kch", "Wa", "We", "Wi",
			"Wo", "Wu", "Wr", "Wc", "Wk", "Ww", "Wb", "Wch", "Ba", "Be", "Bi",
			"Bo", "Bu", "Br", "Bc", "Bk", "Bw", "Bb", "Bch", "Cha", "Che",
			"Chi", "Cho", "Chu", "Chr", "Chc", "Chk", "Chw", "Chb", "Chch",
			"Aa", "Ae", "Ai", "Ao", "Au", "Ar", "Ac", "Ak", "Aw", "Ab", "Ach",
			"Ea", "Ee", "Ei", "Eo", "Eu", "Er", "Ec", "Ek", "Ew", "Eb", "Ech",
			"Ia", "Ie", "Ii", "Io", "Iu", "Ir", "Ic", "Ik", "Iw", "Ib", "Ich",
			"Oa", "Oe", "Oi", "Oo", "Ou", "Or", "Oc", "Ok", "Ow", "Ob", "Och",
			"Ua", "Ue", "Ui", "Uo", "Uu", "Ur", "Uc", "Uk", "Uw", "Ub", "Uch", };
	protected final static int cntWookieNameSyllable2Count = 149;
	protected final static String[] vWookieNameSyllables2 = { "rra", "rre",
			"rri", "rro", "rru", "arr", "err", "irr", "orr", "urr", "rar",
			"car", "kar", "war", "rer", "cer", "ker", "war", "rir", "cir",
			"kir", "wir", "ror", "cor", "kor", "wor", "rur", "cur", "kur",
			"wur", "cca", "cce", "cci", "cco", "ccu", "acc", "ecc", "icc",
			"occ", "ucc", "rac", "cac", "kac", "wac", "rec", "cec", "kec",
			"wac", "ric", "cic", "kic", "wic", "roc", "coc", "koc", "woc",
			"ruc", "cuc", "kuc", "wuc", "kka", "kke", "kki", "kko", "kku",
			"akk", "ekk", "ikk", "okk", "ukk", "rak", "cak", "kak", "wak",
			"rek", "cek", "kek", "wak", "rik", "cik", "kik", "wik", "rok",
			"cok", "kok", "wok", "ruk", "cuk", "kuk", "wuk", "wwa", "wwe",
			"wwi", "wwo", "wwu", "aww", "eww", "iww", "oww", "uww", "raw",
			"caw", "kaw", "waw", "rew", "cew", "kew", "waw", "riw", "ciw",
			"kiw", "wiw", "row", "cow", "kow", "wow", "ruw", "cuw", "kuw",
			"wuw", "bba", "bbe", "bbi", "bbo", "bbu", "abb", "ebb", "ibb",
			"obb", "ubb", "rarb", "carb", "kab", "wab", "reb", "ceb", "keb",
			"wab", "rib", "cib", "kib", "wib", "rob", "cob", "kob", "wob",
			"rub", "cub", "kub", "wub", };

	protected final static int cntTrandoshanNameSyllable1Count = 49;
	protected final static String[] vTrandoshanNameSyllables1 = {

	"Ba", "Be", "Bi", "Bo", "Bu", "Ca", "Ce", "Ci", "Co", "Cu", "Ka", "Ke",
			"Ki", "Ko", "Ku", "Na", "Ne", "Ni", "No", "Nu", "Oa", "Oe", "Oi",
			"Oo", "Ou", "Pa", "Pe", "Pi", "Po", "Pu", "Sa", "Se", "Si", "So",
			"Su", "Va", "Ve", "Vi", "Vo", "Vu", "Xa", "Xe", "Xi", "Xo", "Xu",
			"Za", "Ze", "Zi", "Zo", "Zu", };

	protected final static int cntTrandoshanNameSyllable2Count = 189;
	protected final static String[] vTrandoshanNameSyllables2 = {

	"za", "ze", "zi", "zo", "zu", "zs", "za", "zz", "zza", "zze", "zzi", "zzo",
			"zzu", "zzs", "zzk", "zzz", "zaa", "zaa", "zii", "zoo", "zuu",
			"zss", "zkk", "zzz", "ka", "ke", "ki", "ko", "ku", "ks", "ka",
			"kz", "kka", "kke", "kki", "kko", "kku", "kks", "kkk", "kkz",
			"kaa", "kaa", "kii", "koo", "kuu", "kss", "kkk", "kzz", "sa", "se",
			"si", "so", "su", "ss", "sa", "sz", "ssa", "sse", "ssi", "sso",
			"ssu", "sss", "ssk", "ssz", "saa", "saa", "sii", "soo", "suu",
			"sss", "skk", "szz", "ua", "ue", "ui", "uo", "uu", "us", "ua",
			"uz", "uua", "uue", "uui", "uuo", "uuu", "uus", "uuk", "uuz",
			"uaa", "uaa", "uii", "uoo", "uuu", "uss", "ukk", "uzz", "oa", "oe",
			"oi", "oo", "ou", "os", "oa", "oz", "ooa", "ooe", "ooi", "ooo",
			"oou", "oos", "ook", "ooz", "oaa", "oaa", "oii", "ooo", "ouu",
			"oss", "okk", "ozz", "ia", "ie", "ii", "io", "iu", "is", "ia",
			"iz", "iia", "iie", "iii", "iio", "iiu", "iis", "iik", "iiz",
			"iaa", "iaa", "iii", "ioo", "iuu", "iss", "ikk", "izz", "ea", "ee",
			"ei", "eo", "eu", "es", "ea", "ez", "eea", "eee", "eei", "eeo",
			"eeu", "ees", "eek", "eez", "eaa", "eaa", "eii", "eoo", "euu",
			"ess", "ekk", "ezz", "aa", "ae", "ai", "ao", "au", "as", "aa",
			"az", "aaa", "aae", "aai", "aao", "aau", "aas", "aak", "aaz",
			"aaa", "aaa", "aii", "aoo", "auu", "akk",

	};

	protected final static String newbie_mail_crafting_artisan_body = "@newbie_tutorial/newbie_mail:crafting_artisan_body";
	protected final static String newbie_mail_outdoors_scout_body = "@newbie_tutorial/newbie_mail:outdoors_scout_body";
	protected final static String newbie_mail_social_entertainer_body = "@newbie_tutorial/newbie_mail:social_entertainer_body";
	protected final static String newbie_mail_combat_marksman_body = "@newbie_tutorial/newbie_mail:combat_marksman_body";
	protected final static String newbie_mail_combat_brawler_body = "@newbie_tutorial/newbie_mail:combat_brawler_body";
	protected final static String newbie_mail_science_medic_body = "@newbie_tutorial/newbie_mail:science_medic_body";
	protected final static String newbie_mail_crafting_artisan_subject = "@newbie_tutorial/newbie_mail:crafting_artisan_subject";
	protected final static String newbie_mail_combat_brawler_subject = "@newbie_tutorial/newbie_mail:combat_brawler_subject";
	protected final static String newbie_mail_combat_marksman_subject = "@newbie_tutorial/newbie_mail:combat_marksman_subject";
	protected final static String newbie_mail_outdoors_scout_subject = "@newbie_tutorial/newbie_mail:outdoors_scout_subject";
	protected final static String newbie_mail_science_medic_subject = "@newbie_tutorial/newbie_mail:science_medic_subject";
	protected final static String newbie_mail_social_entertainer_subject = "@newbie_tutorial/newbie_mail:social_entertainer_subject";
	protected final static String newbie_mail_welcome_subject = "@newbie_tutorial/newbie_mail:welcome_subject";
	protected final static String newbie_mail_welcome_body = "You have taken your first steps into the immense universe of Star Wars.\nThis game mail is meant to give you a quick welcome and overview.\nIf you need additional details on anything about the game, don't be afraid to bring up the Holocron (default: CTRL H). It offers easy-to-navigate chapters that not only teach you the game, but also gives you starting missions.\n\nUnlike many other games, there is no 'one' way to play Star Wars Galaxies. You will be able to live in and interact with the Star Wars universe in a way you never have been able to before.\n\nIf you are looking for a little direction to get started, we recommend finding a local mission terminal to take some missions.\n\nAfter making some credits doing simple delivery or destroy missions, you may want to start upgrading your equipment. Eventually, you'll want to converse with non-player characters to find more interesting and lucrative missions or you may want to head to the adventure Theme Parks like Jabba's Palace on Tatooine.\n\nAfter that, you goals are largely determined by what you are interested in. For example, you may want to join the Rebellion or Empire and work your way up through the military ranks.\nYou may wish to master multiple professions and become renown throughout the galaxy for your skills. You may want to acquire, make, or sell the best equipment in the galaxy. You might want to explore the ten worlds and find a perfect spot to build your home. You may even want to try to unlock the secret to opening a second character slot that is Force-sensitive (warning: this is extremely difficult, mysterious and dangerous).\n\nThe choice is really up to you. Your best source for in-depth information on the game will come from other players.\nYou can find players who are ready and willing to answer your questions right now by typing /who newbiehelper.\nSimply pick one off the list in your chat window and send your question by typing: /tell [player name] I'm new, could you please help me?\n\nAs a final note, the game is designed around the recommended default Star Wars Galaxies controls. However if you are more comfortable with a different control set up (EQ style, FPS style or Isometric style), you can quickly remap the entire keyboard layout in the Option menus (default CTRL ) - Controls tab - Keymap button - use the dropdown menu). \n\n\nOn behalf of the entire development team, thank you and enjoy your time in Star Wars Galaxies. May the Force be with you. ";
	// "@newbie_tutorial/newbie_mail:welcome_body";
	// "You have taken your first steps into the immense universe of Star Wars. This game mail is meant to give you a quick welcome and overview. If you need additional details on anything about the game, don't be afraid to bring up the Holocron (default: CTRL H). It offers easy-to-navigate chapters that not only teach you the game, but also gives you starting missions. Unlike many other games, there is no 'one' way to play Star Wars Galaxies. You will be able to live in and interact with the Star Wars universe in a way you never have been able to before. If you are looking for a little direction to get started, we recommend finding a local mission terminal to take some missions. After making some credits doing simple delivery or destroy missions, you may want to start upgrading your equipment. Eventually, you'll want to converse with non-player characters to find more interesting and lucrative missions or you may want to head to the adventure Theme Parks like Jabba's Palace on Tatooine. After that, you goals are largely determined by what you are interested in. For example, you may want to join the Rebellion or Empire and work your way up through the military ranks. You may wish to master multiple professions and become renown throughout the galaxy for your skills. You may want to acquire, make, or sell the best equipment in the galaxy. You might want to explore the ten worlds and find a perfect spot to build your home. You may even want to try to unlock the secret to opening a second character slot that is Force-sensitive (warning: this is extremely difficult, mysterious and dangerous). The choice is really up to you. Your best source for in-depth information on the game will come from other players. You can find players who are ready and willing to answer your questions right now by typing /who newbiehelper. Simply pick one off the list in your chat window and send your question by typing: /tell [player name] I'm new, could you please help me? As a final note, the game is designed around the recommended default Star Wars Galaxies controls. However if you are more comfortable with a different control set up (EQ style, FPS style or Isometric style), you can quickly remap the entire keyboard layout in the Option menus (default CTRL ) - Controls tab - Keymap button - use the dropdown menu). On behalf of the entire development team, thank you and enjoy your time in Star Wars Galaxies. May the Force be with you. ";
	protected final static String newbie_mail_collector_subject = "@newbie_tutorial/newbie_mail:collector_subject";
	protected final static String newbie_mail_collector_body = "@newbie_tutorial/newbie_mail:collector_body";

	// Radial menu constants.
	protected final static char RADIAL_MENU_COMBAT_TARGET = 1;
	protected final static char RADIAL_MENU_COMBAT_UNTARGET = 2;
	protected final static char RADIAL_MENU_COMBAT_ATTACK = 3;
	protected final static char RADIAL_MENU_COMBAT_PEACE = 4;
	protected final static char RADIAL_MENU_COMBAT_DUEL = 5;
	protected final static char RADIAL_MENU_COMBAT_DEATH_BLOW = 6;
	protected final static char RADIAL_MENU_EXAMINE = 7;
	protected final static char RADIAL_MENU_START_TRADE = 8;
	protected final static char RADIAL_MENU_ACCEPT_TRADE = 9;
	protected final static char RADIAL_MENU_ITEM_PICKUP = 10;
	protected final static char RADIAL_MENU_ITEM_EQUIP = 11;
	protected final static char RADIAL_MENU_ITEM_UNEQUIP = 12;
	protected final static char RADIAL_MENU_ITEM_DROP = 13;
	protected final static char RADIAL_MENU_ITEM_DESTROY = 14;
	protected final static char RADIAL_MENU_ITEM_TOKEN = 15; // WTF is this?
	protected final static char RADIAL_MENU_ITEM_OPEN = 16;
	protected final static char RADIAL_MENU_ITEM_OPEN_IN_NEW_WINDOW = 17;
	protected final static char RADIAL_MENU_ITEM_ACTIVATE = 18;
	protected final static char RADIAL_MENU_ITEM_DEACTIVATE = 19;
	protected final static char RADIAL_MENU_ITEM_USE = 20;
	protected final static char RADIAL_MENU_ITEM_USE_ON_SELF = 21;
	protected final static char RADIAL_MENU_ITEM_USE_ON_TARGET = 22;
	protected final static char RADIAL_MENU_ITEM_SIT = 23;
	protected final static char RADIAL_MENU_ITEM_SEND_IN_MAIL = 24;
	protected final static char RADIAL_MENU_START_CONVERSATION = 25;
	protected final static char RADIAL_MENU_CONVERSATION_RESPOND = 26;
	protected final static char RADIAL_MENU_CONVERSATION_RESPONSE = 27; // Duplicate???
	protected final static char RADIAL_MENU_STOP_CONVERSATION = 28;
	protected final static char RADIAL_MENU_CRAFTING_OPTIONS = 29;
	protected final static char RADIAL_MENU_START_CRAFTING = 30;
	protected final static char RADIAL_MENU_CRAFT_INPUT_HOPPER = 31;
	protected final static char RADIAL_MENU_CRAFT_OUTPUT_HOPPER = 32;
	protected final static char RADIAL_MENU_MISSION_TERMINAL_LIST_MISSIONS = 33;
	protected final static char RADIAL_MENU_MISSION_SHOW_DETAILS = 34;
	protected final static char RADIAL_MENU_LOOT_TARGET = 35;
	protected final static char RADIAL_MENU_LOOT_ALL_FROM_TARGET = 36;
	protected final static char RADIAL_MENU_INVITE_TO_GROUP = 37;
	protected final static char RADIAL_MENU_JOIN_GROUP = 38;
	protected final static char RADIAL_MENU_LEAVE_GROUP = 39;
	protected final static char RADIAL_MENU_KICK_FROM_GROUP = 40;
	protected final static char RADIAL_MENU_DISBAND_GROUP = 41;
	protected final static char RADIAL_MENU_DECLINE_GROUP_INVITE = 42;
	protected final static char RADIAL_MENU_EXTRACT_OBJECT = 43;
	protected final static char RADIAL_MENU_CALL_PET = 44;
	protected final static char RADIAL_MENU_USE_ACTION_TERMINAL = 45;
	protected final static char RADIAL_MENU_CREATURE_FOLLOW = 46;
	protected final static char RADIAL_MENU_CREATURE_STOP_FOLLOWING = 47;
	protected final static char RADIAL_MENU_SPLIT_CONTAINER = 48;
	protected final static char RADIAL_MENU_START_IMAGE_DESIGN = 49;
	protected final static char RADIAL_MENU_SET_ITEM_NAME = 50;
	protected final static char RADIAL_MENU_ROTATE_ITEM = 51;
	protected final static char RADIAL_MENU_ROTATE_ITEM_RIGHT = 52;
	protected final static char RADIAL_MENU_ROTATE_ITEM_LEFT = 53;
	protected final static char RADIAL_MENU_MOVE_ITEM = 54;
	protected final static char RADIAL_MENU_MOVE_ITEM_FORWARD = 55;
	protected final static char RADIAL_MENU_MOVE_ITEM_BACK = 56;
	protected final static char RADIAL_MENU_MOVE_ITEM_UP = 57;
	protected final static char RADIAL_MENU_MOVE_ITEM_DOWN = 58;
	protected final static char RADIAL_MENU_STORE_PET = 59;
	protected final static char RADIAL_MENU_CALL_VEHICLE = 60;
	protected final static char RADIAL_MENU_STORE_VEHICLE = 61;
	protected final static char RADIAL_MENU_ABORT_MISSION = 62;
	protected final static char RADIAL_MENU_END_DUTY_MISSION = 63; // Space
	// mission
	// radial
	// menu.
	protected final static char RADIAL_MENU_SHIP_MANAGE_COMPONENTS = 64;
	protected final static char RADIAL_MENU_AUTOPILOT_TO_WAYPOINT = 65;
	protected final static char RADIAL_MENU_PROGRAM_DROID = 66;
	protected final static char RADIAL_MENU_ADMIN_SERVER_DIVIDER = 67;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU1 = 68;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU2 = 69;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU3 = 70;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU4 = 71;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU5 = 72;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU6 = 73;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU7 = 74;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU8 = 75;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU9 = 76;
	protected final static char RADIAL_MENU_ADMIN_SERVER_MENU10 = 77;
	protected final static char RADIAL_MENU_MANAGE_HARVESTER = 78;
	protected final static char RADIAL_MENU_MANAGE_HOUSE = 79;
	protected final static char RADIAL_MENU_MANAGE_GUILD_HALL = 80;
	protected final static char RADIAL_MENU_SERVER_HUE = 81; // WTF?
	protected final static char RADIAL_MENU_SERVER_OBSERVE = 82; // Probably a
	// GM
	// command,
	// displays
	// what "I"
	// see to a
	// GM?
	protected final static char RADIAL_MENU_SERVER_STOP_OBSERVING = 83;
	protected final static char RADIAL_MENU_SHOW_TRAVEL_OPTIONS = 84;
	protected final static char RADIAL_MENU_SHOW_BAZAAR_OPTIONS = 85;
	protected final static char RADIAL_MENU_SHOW_SHIPPING_OPTIONS = 86; // ???
	protected final static char RADIAL_MENU_HEAL_WOUND = 87;
	protected final static char RADIAL_MENU_HEAL_HEALTH_WOUND = 88;
	protected final static char RADIAL_MENU_HEAL_ACTION_WOUND = 89;
	protected final static char RADIAL_MENU_HEAL_STRENGTH_WOUND = 90;
	protected final static char RADIAL_MENU_HEAL_CONSTITUTION_WOUND = 91;
	protected final static char RADIAL_MENU_HEAL_QUICKNESS_WOUND = 92;
	protected final static char RADIAL_MENU_HEAL_STAMINA_WOUND = 93;
	protected final static char RADIAL_MENU_HEAL_DAMAGE = 94;
	protected final static char RADIAL_MENU_HEAL_STATE = 95;
	protected final static char RADIAL_MENU_HEAL_STUNNED_STATE = 96;
	protected final static char RADIAL_MENU_HEAL_BLINDED_STATE = 97;
	protected final static char RADIAL_MENU_HEAL_DIZZY_STATE = 98;
	protected final static char RADIAL_MENU_HEAL_INTIMIDATED_STATE = 99;
	protected final static char RADIAL_MENU_BUFF = 100;
	protected final static char RADIAL_MENU_BUFF_HEALTH = 101;
	protected final static char RADIAL_MENU_BUFF_ACTION = 102;
	protected final static char RADIAL_MENU_BUFF_STRENGTH = 103;
	protected final static char RADIAL_MENU_BUFF_CONSTITUTION = 104;
	protected final static char RADIAL_MENU_BUFF_QUICKNESS = 105;
	protected final static char RADIAL_MENU_BUFF_STAMINA = 106;
	protected final static char RADIAL_MENU_FIRSTAID = 107;
	protected final static char RADIAL_MENU_CURE_POISON = 108;// this is also
	// use bank open
	// safety
	// deposit
	protected final static char RADIAL_MENU_CURE_DISEASE = 109;
	protected final static char RADIAL_MENU_APPLY_POISON = 110;
	protected final static char RADIAL_MENU_APPLY_DISEASE = 111;
	protected final static char RADIAL_MENU_HARVEST_CORPSE = 112;
	protected final static char RADIAL_MENU_START_LISTEN = 113;
	protected final static char RADIAL_MENU_START_WATCHING = 114;
	protected final static char RADIAL_MENU_STOP_LISTEN = 115;
	protected final static char RADIAL_MENU_STOP_WATCHING = 116;
	protected final static char RADIAL_MENU_STRUCTURE_SET_PERMISSIONS = 117;
	protected final static char RADIAL_MENU_MANAGE_STRUCTURE = 118;
	protected final static char RADIAL_MENU_MANAGE_ENTRY_PERMISSIONS = 119;
	protected final static char RADIAL_MENU_MANAGE_BANNED_PERMISSIONS = 120;
	protected final static char RADIAL_MENU_MANAGE_ADMIN_RIGHTS = 121;
	protected final static char RADIAL_MENU_SET_VENDOR_PERMISSIONS = 122;
	protected final static char RADIAL_MENU_OPEN_CRAFT_HOPPER = 123;
	protected final static char RADIAL_MENU_OPEN_STATUS_WINDOW = 124;
	protected final static char RADIAL_MENU_SET_PRIVACY = 125;
	protected final static char RADIAL_MENU_TRANSFER_OWNERSHIP = 126;
	protected final static char RADIAL_MENU_DECLARE_RESIDENCY = 127;
	protected final static char RADIAL_MENU_DESTROY_STRUCTURE = 128;
	protected final static char RADIAL_MENU_PAY_MAINTENANCE = 129;
	protected final static char RADIAL_MENU_CREATE_VENDOR_FOR_STRUCTURE = 130;
	protected final static char RADIAL_MENU_VENDOR_ADD_MAINTENANCE = 131;
	protected final static char RADIAL_MENU_SERVER_ITEM_OPTIONS = 132; // Tool
	// options
	protected final static char RADIAL_MENU_SET_SURVEY_TOOL_RANGE = 133;
	protected final static char RADIAL_MENU_SET_SURVEY_TOOL_RESOLUTION = 134; // Never
	// used?
	protected final static char RADIAL_MENU_SET_SURVEY_TOOL_CLASS = 135; // Never
	// used?
	// Or
	// used
	// for
	// the
	// Village
	// survey
	// tool?
	protected final static char RADIAL_MENU_PROBE_DROID_TRACK_TARGET = 136;
	protected final static char RADIAL_MENU_PROBE_DROID_FIND_TARGET = 137;
	protected final static char RADIAL_MENU_SPAWN_PROBE_DROID = 138;
	protected final static char RADIAL_MENU_BUY_PROBE_DROID = 139;
	protected final static char RADIAL_MENU_TEACH_COMMAND = 140;
	protected final static char RADIAL_MENU_TEACH_PET_COMMAND = 141;
	protected final static char RADIAL_MENU_PET_FOLLOW = 142;
	protected final static char RADIAL_MENU_PET_STAY = 143;
	protected final static char RADIAL_MENU_PET_GUARD = 144;
	protected final static char RADIAL_MENU_PET_FRIEND = 145;
	protected final static char RADIAL_MENU_PET_ATTACK = 146;
	protected final static char RADIAL_MENU_PET_PATROL = 147;
	protected final static char RADIAL_MENU_PET_GET_PATROL_POINTS = 148;
	protected final static char RADIAL_MENU_PET_CLEAR_PATROL_POINTS = 149;
	protected final static char RADIAL_MENU_PET_ASSUME_FORMATION_1 = 150;
	protected final static char RADIAL_MENU_PET_ASSUME_FORMATION_2 = 151;
	protected final static char RADIAL_MENU_PET_TRANSFER_OWNER = 152;
	protected final static char RADIAL_MENU_PET_RELEASE_CONTROL = 153;
	protected final static char RADIAL_MENU_PET_TRICK_1 = 154;
	protected final static char RADIAL_MENU_PET_TRICK_2 = 155;
	protected final static char RADIAL_MENU_PET_TRICK_3 = 156;
	protected final static char RADIAL_MENU_PET_TRICK_4 = 157;
	protected final static char RADIAL_MENU_PET_GROUP_INVITE = 158;
	protected final static char RADIAL_MENU_TAME_CREATURE = 159;
	protected final static char RADIAL_MENU_FEED_PET = 160;
	protected final static char RADIAL_MENU_PET_SPECIAL_ATTACK_1 = 161;
	protected final static char RADIAL_MENU_PET_SPECIAL_ATTACK_2 = 162;
	protected final static char RADIAL_MENU_PET_RANGED_ATTACK = 163;
	protected final static char RADIAL_MENU_ROLL_DIE = 164;
	protected final static char RADIAL_MENU_DIE_2_FACE = 165;
	protected final static char RADIAL_MENU_DIE_3_FACE = 166;
	protected final static char RADIAL_MENU_DIE_4_FACE = 167;
	protected final static char RADIAL_MENU_DIE_5_FACE = 168;
	protected final static char RADIAL_MENU_DIE_6_FACE = 169;
	protected final static char RADIAL_MENU_DIE_7_FACE = 170;
	protected final static char RADIAL_MENU_DIE_8_FACE = 171;
	protected final static char RADIAL_MENU_DIE_COUNT_1 = 172;
	protected final static char RADIAL_MENU_DIE_COUNT_2 = 173;
	protected final static char RADIAL_MENU_DIE_COUNT_3 = 174;
	protected final static char RADIAL_MENU_DIE_COUNT_4 = 175;
	protected final static char RADIAL_MENU_CREATE_BALLOT = 176;
	protected final static char RADIAL_MENU_CAST_VOTE = 177;
	protected final static char RADIAL_MENU_SET_BOMBING_RUN = 178;
	protected final static char RADIAL_MENU_ACTIVATE_SELF_DESTRUCT = 179;
	protected final static char RADIAL_MENU_THIRTY_SECONDS = 180;
	protected final static char RADIAL_MENU_FIFTEEN_SECONDS = 181;
	protected final static char RADIAL_MENU_DISBAND_CAMPSITE = 182;
	protected final static char RADIAL_MENU_ASSUME_OWNERSHIP_OF_CAMPSITE = 183;
	protected final static char RADIAL_MENU_UPLOAD_PROBE_DROID_PROGRAM = 184;
	protected final static char RADIAL_MENU_CREATE_GUILD = 185;
	protected final static char RADIAL_MENU_DISPLAY_GUILD_INFORMATION = 186;
	protected final static char RADIAL_MENU_GUILD_LIST_MEMBERS = 187;
	protected final static char RADIAL_MENU_GUILD_LIST_PLAYERS_SPONSERED = 188;
	protected final static char RADIAL_MENU_GUILD_LIST_ENEMY_GUILDS = 189;
	protected final static char RADIAL_MENU_GUILD_SPONSER_PLAYER_FOR_MEMBERSHIP = 190;
	protected final static char RADIAL_MENU_GUILD_DISBAND = 191;
	protected final static char RADIAL_MENU_GUILD_CHANGE_GUILD_NAME = 192;
	protected final static char RADIAL_MENU_GUILD_MANAGE_GUILD = 193;
	protected final static char RADIAL_MENU_GUILD_MANAGE_MEMBER = 194;
	protected final static char RADIAL_MENU_FACTORY_OPEN_INPUT_HOPPER = 195;
	protected final static char RADIAL_MENU_FACTORY_OPEN_OUTPUT_HOPPER = 196;
	protected final static char RADIAL_MENU_FACTORY_MANAGE_SCHEMATIC = 197;
	protected final static char RADIAL_MENU_ELEVATOR_UP = 198;
	protected final static char RADIAL_MENU_ELEVATOR_DOWN = 199;
	protected final static char RADIAL_MENU_OPEN_PET_INVENTORY = 200;
	protected final static char RADIAL_MENU_OPEN_PET_DATAPAD = 201;
	protected final static char RADIAL_MENU_DIAGNOSE_PATIENT = 202;
	protected final static char RADIAL_MENU_PATIENT_TEND_WOUND = 203;
	protected final static char RADIAL_MENU_PATIENT_TEND_DAMAGE = 204;
	protected final static char RADIAL_MENU_MOUNT_PET = 205;
	protected final static char RADIAL_MENU_DISMOUNT_PET = 206;
	protected final static char RADIAL_MENU_PET_CONVERT_TO_MOUNT = 207;
	protected final static char RADIAL_MENU_ENTER_VEHICLE = 208;
	protected final static char RADIAL_MENU_EXIT_VEHICLE = 209;
	protected final static char RADIAL_MENU_NAVICOMPUTER_OPEN_DATAPAD = 210;
	protected final static char RADIAL_MENU_NAVICOMPUTER_INITIALIZE_DATAPAD = 211;
	protected final static char RADIAL_MENU_CITY_SHOW_STATUS = 212;
	protected final static char RADIAL_MENU_CITY_LIST_CITIZENS = 213;
	protected final static char RADIAL_MENU_CITY_LIST_STRUCTURES = 214;
	protected final static char RADIAL_MENU_CITY_SHOW_TREASURY = 215;
	protected final static char RADIAL_MENU_CITY_MANAGEMENT = 216;
	protected final static char RADIAL_MENU_CITY_MANAGE_NAME = 217;
	protected final static char RADIAL_MENU_CITY_MANAGE_MILITIA = 218;
	protected final static char RADIAL_MENU_CITY_MANAGE_TAXES = 219;
	protected final static char RADIAL_MENU_TREASURY_DEPOSIT_FUNDS = 220;
	protected final static char RADIAL_MENU_TREASURY_WITHDRAW_FUNDS = 221;
	protected final static char RADIAL_MENU_CITY_REGISTER_ON_MAP = 222;
	protected final static char RADIAL_MENU_CITY_RANK = 223;
	protected final static char RADIAL_MENU_CITY_ADMIN_1 = 224;
	protected final static char RADIAL_MENU_CITY_ADMIN_2 = 225;
	protected final static char RADIAL_MENU_CITY_ADMIN_3 = 226;
	protected final static char RADIAL_MENU_CITY_ADMIN_4 = 227;
	protected final static char RADIAL_MENU_CITY_ADMIN_5 = 228;
	protected final static char RADIAL_MENU_CITY_ADMIN_6 = 229;
	protected final static char RADIAL_MENU_PROGRAM_MEMORY_CHIP = 230;
	protected final static char RADIAL_MENU_TRANSFER_MEMORY_CHIP = 231;
	protected final static char RADIAL_MENU_ANALYZE_MEMORY_CHIP = 232;
	protected final static char RADIAL_MENU_EQUIP_DROID_ON_SHIP = 234;

	// Radial menu visibility indices
	protected final static byte RADIAL_VISIBILITY_OWNER = 0;
	protected final static byte RADIAL_VISIBILITY_ADMIN = 1;
	protected final static byte RADIAL_VISIBILITY_FRIENDLY = 2;
	protected final static byte RADIAL_VISIBILITY_HOSTILE = 3;
	protected final static byte RADIAL_VISIBILITY_ALL = 4;

	// ------------------------------------

	/**
	 * This lists all terminal types to be used. Add new types as needed.
	 */
	protected final static int TERMINAL_TYPES_UNDEFINED = 0;
	protected final static int TERMINAL_TYPES_TRAVEL_GENERAL = 1;
	protected final static int TERMINAL_TYPES_TICKET_DROID = 2;
	protected final static int TERMINAL_TYPES_MISSION_GENERAL = 3;
	protected final static int TERMINAL_TYPES_STRUCTURE_MAINTENANCE = 4;
	protected final static int TERMINAL_TYPES_STRUCTURE_SIGN = 5;
	protected final static int TERMINAL_TYPES_TRAVEL_TUTORIAL = 6;
	protected final static int TERMINAL_TYPES_CAMP_MANAGEMENT = 7;
	protected final static int TERMINAL_TYPES_MISSION_ENTERTAINER = 57;
	protected final static int TERMINAL_TYPES_MISSION_EXPLORER = 61; // used to
	// be 5;
	protected final static int TERMINAL_TYPES_MISSION_CRAFTER = 55;// used to be
	// 6
	protected final static int TERMINAL_TYPES_MISSION_BOUNTY_HUNTER = 56; // used
	// to
	// be
	// 7;
	protected final static int TERMINAL_TYPES_TRAVEL_CORVETTE = 8;
	protected final static int TERMINAL_TYPES_TRAVEL_INSTANCE = 9;
	protected final static int TERMINAL_TYPES_CHARACTER_BUILDER = 10;
	protected final static int TERMINAL_TYPES_SKILL_TRAINER = 11;
	protected final static int TERMINAL_TYPES_SHUTTLE_PORT = 12;
	protected final static int TERMINAL_TYPES_TERMINAL_BANK = 13;
	protected final static int TERMINAL_TYPES_BETA_TERMINAL_FOOD = 14;
	protected final static int TERMINAL_TYPES_BETA_TERMINAL_MEDICINE = 15;
	protected final static int TERMINAL_TYPES_BETA_TERMINAL_MONEY = 16;
	protected final static int TERMINAL_TYPES_TERMINAL_RESOURCE = 17;
	protected final static int TERMINAL_TYPES_TERMINAL_WARP = 18;
	protected final static int TERMINAL_TYPES_TERMINAL_XP = 19;
	protected final static int TERMINAL_TYPES_DONHAM_TERMINAL = 20;
	protected final static int TERMINAL_TYPES_KEYPAD = 21;
	protected final static int TERMINAL_TYPES_TERMINAL_FREE_S1 = 22;
	protected final static int TERMINAL_TYPES_TERMINAL_GAS_VALVE = 23;
	protected final static int TERMINAL_TYPES_TERMINAL_POWER_SWITCH = 24;
	protected final static int TERMINAL_TYPES_TERMINAL_GENERAL_SWITCH = 25;
	protected final static int TERMINAL_TYPES_TERMINAL_POWER_SWITCH2 = 26;
	protected final static int TERMINAL_TYPES_BASE_TERMINAL = 27;
	protected final static int TERMINAL_TYPES_TERMINAL_BALLOT_BOX = 28;
	protected final static int TERMINAL_TYPES_TERMINAL_BAZAAR = 29;
	protected final static int TERMINAL_TYPES_TERMINAL_BESTINE_01 = 30;
	protected final static int TERMINAL_TYPES_TERMINAL_BESTINE_02 = 31;
	protected final static int TERMINAL_TYPES_TERMINAL_BESTINE_03 = 32;
	protected final static int TERMINAL_TYPES_TERMINAL_BOUNTY_DROID = 33;
	protected final static int TERMINAL_TYPES_TERMINAL_CHARACTER_BUILDER = 34;
	protected final static int TERMINAL_TYPES_TERMINAL_CITY = 35;
	protected final static int TERMINAL_TYPES_TERMINAL_CITY_VOTE = 36;
	protected final static int TERMINAL_TYPES_TERMINAL_CLONING = 37;
	protected final static int TERMINAL_TYPES_TERMINAL_GEO_BUNKER = 38;
	protected final static int TERMINAL_TYPES_TERMINAL_DARK_ENCLAVE_CHALLENGE = 39;
	protected final static int TERMINAL_TYPES_TERMINAL_DARK_ENCLAVE_VOTING = 40;
	protected final static int TERMINAL_TYPES_TERMINAL_ELEVATOR_UP = 41;
	protected final static int TERMINAL_TYPES_TERMINAL_ELEVATOR_DOWN = 42;
	protected final static int TERMINAL_TYPES_TERMINAL_ELEVATOR_UP_DOWN = 43;
	protected final static int TERMINAL_TYPES_TERMINAL_GEO_BUNKER2 = 44;
	protected final static int TERMINAL_TYPES_TERMINAL_GUILD = 45;
	protected final static int TERMINAL_TYPES_TERMINAL_HQ = 46;
	protected final static int TERMINAL_TYPES_TERMINAL_HQ_IMPERIAL = 47;
	protected final static int TERMINAL_TYPES_TERMINAL_HQ_REBEL = 48;
	protected final static int TERMINAL_TYPES_TERMINAL_HQ_TURRET_CONTROL = 49;
	protected final static int TERMINAL_TYPES_TERMINAL_IMAGEDESIGN = 50;
	protected final static int TERMINAL_TYPES_TERMINAL_INSURANCE = 51;
	protected final static int TERMINAL_TYPES_TERMINAL_LIGHT_ENCLAVE_CHALLENGE = 52;
	protected final static int TERMINAL_TYPES_TERMINAL_LIGHT_ENCLAVE_VOTING = 53;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION1 = 54;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_ARTISAN = 55;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_BOUNTY = 56;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_ENTERTAINER = 57;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_IMPERIAL = 58;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION2 = 59;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_REBEL = 60;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_SCOUT = 61;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION3 = 62;
	protected final static int TERMINAL_TYPES_NEWBIE_CLOTHING_DISPENSER = 63;
	protected final static int TERMINAL_TYPES_NEWBIE_FOOD_DISPENSER = 64;
	protected final static int TERMINAL_TYPES_NEWBIE_INSTRUMENT_DISPENSER = 65;
	protected final static int TERMINAL_TYPES_NEWBIE_MEDICINE_DISPENSER = 66;
	protected final static int TERMINAL_TYPES_NEWBIE_TOOL_DISPENSER = 67;
	protected final static int TERMINAL_TYPES_TERMINAL_PLAYER_STRUCTURE1 = 68;
	protected final static int TERMINAL_TYPES_TERMINAL_PLAYER_STRUCTURE2 = 69;
	protected final static int TERMINAL_TYPES_TERMINAL_PLAYER_STRUCTURE3 = 70;
	protected final static int TERMINAL_TYPES_TERMINAL_PM_REGISTER = 71;
	protected final static int TERMINAL_TYPES_DOOR_SECURITY_TERMINAL = 72;
	protected final static int TERMINAL_TYPES_TERMINAL_SHIPPING = 73;
	protected final static int TERMINAL_TYPES_TERMINAL_SPACE = 74;
	protected final static int TERMINAL_TYPES_TERMINAL_TRAVEL = 75;
	protected final static int TERMINAL_TYPES_TERMINAL_WATER_PRESSURE = 76;
	protected final static int TERMINAL_TYPES_TERMINAL_ELEVATOR4 = 77;
	// ------------------------------------------------------------------------------------//
	protected final static int TERMINAL_TYPES_NOBUILD_ZONE_4 = 78;
	protected final static int TERMINAL_TYPES_NOBUILD_ZONE_32 = 79;
	protected final static int TERMINAL_TYPES_NOBUILD_ZONE_64 = 80;
	protected final static int TERMINAL_TYPES_NOBUILD_ZONE_128 = 81;
	protected final static int TERMINAL_TYPES_NOBUILD_ZONE_768 = 82;
	// --------------------------------------------------------------------------------------//
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NPC_NEUTRAL = 820;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NPC_IMPERIAL = 83;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NPC_REBEL = 84;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_SURVEY_NPC_NEUTRAL = 85;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_SURVEY_NPC_IMPERIAL = 86;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_SURVEY_NPC_REBEL = 87;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_RECON_NPC_NEUTRAL = 88;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_RECON_NPC_IMPERIAL = 89;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_RECON_NPC_REBEL = 90;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_MUSICIAN_NPC_NEUTRAL = 91;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_MUSICIAN_NPC_IMPERIAL = 92;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_MUSISCIAN_NPC_REBEL = 93;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_ESCORT_NPC_NEUTRAL = 94;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_ESCORT_NPC_IMPERIAL = 95;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_ESCORT_NPC_REBEL = 96;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_ESCORTTOME_NPC_IMPERIAL = 97;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_ESCORTTOME_NPC_REBEL = 98;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_ESCORTTOCREATOR_NPC_NEUTRAL = 99;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_DANCER_NPC_NEUTRAL = 100;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_DANCER_NPC_IMPERIAL = 101;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_DANCER_NPC_REBEL = 102;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_CRAFTING_NPC_NEUTRAL = 103;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_CRAFTING_NPC_IMPERIAL = 104;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_CRAFTING_NPC_REBEL = 105;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NONPERSISTENT_NONMILITARY_NPC_NEUTRAL = 106;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NONPERSISTENT_NONMILITARY_NPC_IMPERIAL = 107;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NONPERSISTENT_NONMILITARY_NPC_REBEL = 108;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_TATOOINE_NPC_NEUTRAL = 109;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NABOO_NPC_NEUTRAL = 110;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_CORELLIA_NPC_NEUTRAL = 111;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_GANGSTER_NPC_NEUTRAL = 112;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_SMUGGLER = 113;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NONPERSISTENT_NPC_NEUTRAL = 114;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NONPERSISTENT_NPC_IMPERIAL = 115;
	protected final static int TERMINAL_TYPES_TERMINAL_MISSION_NONPERSISTENT_NPC_REBEL = 116;
	/**
	 * Tutorila Related
	 */
	protected final static int TERMINAL_TYPES_TUTORIAL_GREET_OFFICER = 1000;

	// End Tutorial Related.

	/**
	 * This defines all shuttle types for the shuttel object Add new ones as
	 * required.
	 */
	protected final static int SHUTTLE_TYPES_INVALID_SHUTTLE = -1;
	protected final static int SHUTTLE_TYPES_UNDEFINED_SHUTTLE = 0;
	protected final static int SHUTTLE_TYPES_GENERIC_SHUTTLE = 1;
	protected final static int SHUTTLE_TYPES_STARPORT_SHUTTLE = 2;
	protected final static int SHUTTLE_TYPES_LAMBDA_SHUTTLE = 3;
	protected final static int SHUTTLE_TYPES_PICKUP_SHUTTLE = 4;
	protected final static int SHUTTLE_TYPES_SUPPLY_SHUTTLE = 5;
	protected final static int SHUTTLE_TYPES_XWING_FIGHTER = 6;
	protected final static int SHUTTLE_TYPES_TIE_FIGHTER = 7;
	protected final static int SHUTTLE_TYPES_STAR_DESTROYER = 8;
	protected final static int SHUTTLE_TYPES_CORVETTE = 9;

	/**
	 * This section sets the constant values for SUI Window Types
	 */

	protected final static int SUI_WINDOW_TYPE_ScriptlistBox = 1;
	protected final static int SUI_WINDOW_TYPE_ScriptmessageBox = 2;
	protected final static int SUI_WINDOW_TYPE_ScripttransferBox = 3;
	protected final static int SUI_WINDOW_TYPE_ScriptinputBox = 4;
	protected final static int SUI_WINDOW_TYPE_ScriptAvLoc2 = 99;

	protected static enum MAP_LOCATION_ID {
		NULL, UNKNOWN, BANK, CANTINA, CAPITOL, CLONING_FACILITY, PARKING_GARAGE, GUILD_HALL, COMBAT_HALL, COMMERCE_HALL, THEATRE, UNIVERSITY, HOTEL, MEDICAL_CENTER, SHUTTLEPORT, STARPORT, THEME_PARK, CITY, INVISIBLE1, TRAINER, TRAINER_TYPE_BRAWLER, TRAINER_TYPE_ARTISAN, TRAINER_TYPE_SCOUT, TRAINER_TYPE_ENTERTAINER, TRAINER_TYPE_MEDIC, JUNK_SHOP, TAVERN, BARRACKS, VENDOR, VENDOR_TYPE_ARMOR, VENDOR_TYPE_CLOTHING, VENDOR_TYPE_COMPONENTS, VENDOR_TYPE_DROIDS, VENDOR_TYPE_EQUIPMENT, VENDOR_TYPE_FOOD, VENDOR_TYPE_HOUSING, VENDOR_TYPE_RESOURCES, VENDOR_TYPE_TOOLS, VENDOR_TYPE_WEAPONS, VENDOR_TYPE_JUNK, TERMINAL, TERMINAL_TYPE_BANK, TERMINAL_TYPE_BAZAAR, TERMINAL_TYPE_MISSION, REBEL, IMPERIAL, REBEL_TYPE_HEADQUARTERS, IMPERIAL_TYPE_HEADQUARTERS, CAMPSITE, CITY_HALL, THEATRE_2, VENDOR_TYPE_PETS, VENDOR_TYPE_MEDICAL, INVISIBLE_2, PARKING_GARAGE_2, MUSEUM, SALON, SPACE, SPACE_TYPE_RECRUITER, SPACE_TYPE_CHASSIS_BROKER, SPACE_TYPE_CHASSIS, TRAINER_TYPE_STARSHIP_ENGINEER, TRAINER_TYPE_SHIPWRIGHT, TRAINER_TYPE_PRIVATEER_PILOT, TRAINER_TYPE_REBEL_PILOT, TRAINER_TYPE_IMPERIAL_PILOT, SHIPS,
	};

	/**
	 * Utility function for quickly testing if a hex 4-byte value may be a
	 * Floating Point number.
	 * 
	 * @param args
	 *            -- External args passed into the program
	 * @throws NumberFormatException
	 *             -- If the number being tested is not a 4-byte Floating Point
	 *             number.
	 */
	public static void main(String[] args) throws Exception {
		float value = Float.parseFloat("73686f6c");
		System.out.println(value);

		// Memtest

		/*
		 * System.out.println(Math.sqrt(100663296));
		 * 
		 * long[][] lMemoryChunk = new long[9500][9500]; long lStartTime =
		 * System.currentTimeMillis(); for (int i = 0; i < lMemoryChunk.length;
		 * i++) { //System.out.println("First dimension["+i+"]"); for (int j =
		 * 0; j < lMemoryChunk[i].length; j++) { for (int k = 0; k < 64; k++) {
		 * lMemoryChunk[i][j] |= ((k % 2) << k); } } } long lEndTime =
		 * System.currentTimeMillis(); long lDeltaTime = lEndTime - lStartTime;
		 * System.out.println("Completed in " + lDeltaTime + " ms.");
		 * System.out.println("Element[0][0] = " + lMemoryChunk[0][0]);
		 */
		if (false) {

			/*
			 * String sHex = "0x407677E6"; sHex = sHex.replace("0x", "");
			 * 
			 * int iBytes = (int) Long.parseLong(sHex, 16); float fl =
			 * Float.intBitsToFloat(iBytes); System.out.println("iBytes(" +
			 * iBytes + ") fl(" + fl + ")"); //
			 * System.out.println("intBitsOf(1f) = " + //
			 * Integer.toHexString(Float.floatToIntBits(1f)));
			 */
			long lTotalMemory = Runtime.getRuntime().totalMemory();
			long lFreeMemory = Runtime.getRuntime().freeMemory();
			long lUsedMemory = lTotalMemory - lFreeMemory;
			System.out.println("Constants.class uses " + lUsedMemory
					+ " bytes of ram.");
		}

	}

	/**
	 * Object Attribute Names List
	 */

	protected final static int OBJECT_ATTRIBUTE_ACIDEFFECTIVENESS = 0;
	protected final static int OBJECT_ATTRIBUTE_ACTION = 1;
	protected final static int OBJECT_ATTRIBUTE_ACTION_DUR = 2;
	protected final static int OBJECT_ATTRIBUTE_ACTION_E = 3;
	protected final static int OBJECT_ATTRIBUTE_ACTIONENCUMBRANCE = 4;
	protected final static int OBJECT_ATTRIBUTE_AGGRO = 5;
	protected final static int OBJECT_ATTRIBUTE_AI_DICTION = 6;
	protected final static int OBJECT_ATTRIBUTE_AREA = 7;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_ACTION_ENCUMBRANCE = 8;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_BASE = 9;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_BLAST = 10;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_ACID = 11;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_COLD = 12;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_ELECTRICAL = 13;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_HEAT = 14;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ENERGY = 15;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_ACID = 16;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_COLD = 17;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_ELECTRICAL = 18;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_HEAT = 19;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_KINETIC = 20;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_RESTRAINT = 21;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFF_STUN = 22;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_EFFECTIVENESS = 23;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_HEALTH_ENCUMBRANCE = 24;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_INTEGRITY = 25;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_MIND_ENCUMBRANCE = 26;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_MODULE = 27;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_RATING = 28;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_SPECIAL_EFFECTIVENESS = 29;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_SPECIAL_INTEGRITY = 30;
	protected final static int OBJECT_ATTRIBUTE_ARMOR_SPECIAL_TYPE = 31;
	protected final static int OBJECT_ATTRIBUTE_ARMOREFFECTIVENESS = 32;
	protected final static int OBJECT_ATTRIBUTE_ARMORENCUMBRANCE = 33;
	protected final static int OBJECT_ATTRIBUTE_ARMORINTEGRITY = 34;
	protected final static int OBJECT_ATTRIBUTE_ARMORRATING = 35;
	protected final static int OBJECT_ATTRIBUTE_ARMORSPECIALEFFECTIVENESS = 36;
	protected final static int OBJECT_ATTRIBUTE_ARMORSPECIALINTEGRITY = 37;
	protected final static int OBJECT_ATTRIBUTE_ATTACKACTIONCOST = 38;
	protected final static int OBJECT_ATTRIBUTE_ATTACKHEALTHCOST = 39;
	protected final static int OBJECT_ATTRIBUTE_ATTACKMINDCOST = 40;
	protected final static int OBJECT_ATTRIBUTE_ATTACKSPEED = 41;
	protected final static int OBJECT_ATTRIBUTE_ATTRIBMODS = 42;
	protected final static int OBJECT_ATTRIBUTE_BASE_EFFECTIVENESS = 43;
	protected final static int OBJECT_ATTRIBUTE_BASEEFFECTIVENESS = 44;
	protected final static int OBJECT_ATTRIBUTE_BASEINTEGRITY = 45;
	protected final static int OBJECT_ATTRIBUTE_BASETOHIT = 46;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_ACTION_MOD = 47;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_BUSINESS_ACCUMEN = 48;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_CAMOUFLAGE = 49;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_COMBAT_BLEEDING_DEFENSE = 50;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_CON_MOD = 51;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_COVER = 52;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_CREATURE_EMPATHY = 53;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_CREATURE_TRAINING = 54;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_DURATION = 55;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_FLAVOR = 56;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_FOCUS_MOD = 57;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_HEALING_ABILITY = 58;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_HEALING_DANCE_WOUND = 59;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_HEALING_INJURY_TREATMENT = 60;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_HEALING_MUSIC_WOUND = 61;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_HEALING_WOUND_TREATMENT = 62;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_HEALTH_MOD = 63;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_INTIMIDATION = 64;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_MELEE_DEFENSE = 65;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_MIND_MOD = 66;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_QUICK_MOD = 67;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_STAM_MOD = 68;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_STR_MOD = 69;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_STUN_DEFENSE = 70;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_TAKE_COVER = 71;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_TAME_AGGRO = 72;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_TAME_NON_AGGRO = 73;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_WARCRY = 74;
	protected final static int OBJECT_ATTRIBUTE_BIO_COMP_WILL_MOD = 75;
	protected final static int OBJECT_ATTRIBUTE_BLASTEFFECTIVENESS = 76;
	protected final static int OBJECT_ATTRIBUTE_BLASTINTEGRITY = 77;
	protected final static int OBJECT_ATTRIBUTE_BLUNTEFFECTIVENESS = 78;
	protected final static int OBJECT_ATTRIBUTE_BLUNTINTEGRITY = 79;
	protected final static int OBJECT_ATTRIBUTE_BUILDRATE = 80;
	protected final static int OBJECT_ATTRIBUTE_CAT_ARMOR_EFFECTIVENESS = 81;
	protected final static int OBJECT_ATTRIBUTE_CAT_ARMOR_ENCUMBRANCE = 82;
	protected final static int OBJECT_ATTRIBUTE_CAT_ARMOR_SPECIAL_PROTECTION = 83;
	protected final static int OBJECT_ATTRIBUTE_CAT_ARMOR_VULNERABILITY = 84;
	protected final static int OBJECT_ATTRIBUTE_CAT_MANF_SCHEM_ING_COMPONENT = 85;
	protected final static int OBJECT_ATTRIBUTE_CAT_MANF_SCHEM_ING_RESOURCE = 86;
	protected final static int OBJECT_ATTRIBUTE_CAT_SKILL_MOD_BONUS = 87;
	protected final static int OBJECT_ATTRIBUTE_CHALLENGE_LEVEL = 88;
	protected final static int OBJECT_ATTRIBUTE_CHARGE = 89;
	protected final static int OBJECT_ATTRIBUTE_CHARGES = 90;
	protected final static int OBJECT_ATTRIBUTE_CLEVERNESS = 91;
	protected final static int OBJECT_ATTRIBUTE_COLDEFFECTIVENESS = 92;
	protected final static int OBJECT_ATTRIBUTE_COMBAT_HEALING_ABILITY = 93;
	protected final static int OBJECT_ATTRIBUTE_COMPLEXITY = 94;
	protected final static int OBJECT_ATTRIBUTE_CON_DUR = 95;
	protected final static int OBJECT_ATTRIBUTE_CON_E = 96;
	protected final static int OBJECT_ATTRIBUTE_CONDITION = 97;
	protected final static int OBJECT_ATTRIBUTE_CONSTITUTION = 98;
	protected final static int OBJECT_ATTRIBUTE_CONTENTS = 99;
	protected final static int OBJECT_ATTRIBUTE_COUNT = 100;
	protected final static int OBJECT_ATTRIBUTE_COUNTER_CHARGES_REMAINING = 101;
	protected final static int OBJECT_ATTRIBUTE_COUNTER_USES_REMAINING = 102;
	protected final static int OBJECT_ATTRIBUTE_COURAGE = 103;
	protected final static int OBJECT_ATTRIBUTE_CRAFT_TOOL_STATUS = 104;
	protected final static int OBJECT_ATTRIBUTE_CRAFT_TOOL_TIME = 105;
	protected final static int OBJECT_ATTRIBUTE_CRAFTED_NAME = 106;
	protected final static int OBJECT_ATTRIBUTE_CRAFTER = 107;
	protected final static int OBJECT_ATTRIBUTE_CRAFTING_STATION = 108;
	protected final static int OBJECT_ATTRIBUTE_CRAFTING_STATION_CLOTHING = 109;
	protected final static int OBJECT_ATTRIBUTE_CRAFTING_STATION_FOOD = 110;
	protected final static int OBJECT_ATTRIBUTE_CRAFTING_STATION_STRUCTURE = 111;
	protected final static int OBJECT_ATTRIBUTE_CRAFTING_STATION_WEAPON = 112;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_ACTION = 113;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_ATTACK = 114;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_CONSTITUTION = 115;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_DAMAGE = 116;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_FOCUS = 117;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_HEALTH = 118;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_MIND = 119;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_QUICKNESS = 120;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_STAMINA = 121;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_STRENGTH = 122;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_TOHIT = 123;
	protected final static int OBJECT_ATTRIBUTE_CREATURE_WILLPOWER = 124;
	protected final static int OBJECT_ATTRIBUTE_CUSTOM_APPEARANCE = 125;
	protected final static int OBJECT_ATTRIBUTE_DAMAGE = 126;
	protected final static int OBJECT_ATTRIBUTE_DATA_MODULE = 127;
	protected final static int OBJECT_ATTRIBUTE_DATA_MODULE_RATING = 128;
	protected final static int OBJECT_ATTRIBUTE_DATA_VOLUME = 129;
	protected final static int OBJECT_ATTRIBUTE_DECAY_RATE = 130;
	protected final static int OBJECT_ATTRIBUTE_DECAYRATE = 131;
	protected final static int OBJECT_ATTRIBUTE_DEFAULT = 132;
	protected final static int OBJECT_ATTRIBUTE_DEPENDABILITY = 133;
	protected final static int OBJECT_ATTRIBUTE_DEXTERITY = 134;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_ABOVE_AVERAGE = 135;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_AVERAGE = 136;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_BELOW_AVERAGE = 137;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_CLEVERNESS = 138;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_COURAGE = 139;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_DEPENDABILITY = 140;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_DEXTERITY = 141;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_ENDURANCE = 142;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_FIERCENESS = 143;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_FORTITUDE = 144;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_HARDINESS = 145;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_HIGH = 146;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_INTELLECT = 147;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_LOW = 148;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_POWER = 149;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_QUALITY = 150;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_SOURCE = 151;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_VERY_HIGH = 152;
	protected final static int OBJECT_ATTRIBUTE_DNA_COMP_VERY_LOW = 153;
	protected final static int OBJECT_ATTRIBUTE_DOT_TYPE_DISEASE = 154;
	protected final static int OBJECT_ATTRIBUTE_DOT_TYPE_POISON = 155;
	protected final static int OBJECT_ATTRIBUTE_DURATION = 156;
	protected final static int OBJECT_ATTRIBUTE_EFFECT = 157;
	protected final static int OBJECT_ATTRIBUTE_ELECTRICALEFFECTIVENESS = 158;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTAL_ACIDEFFECTIVENESS = 159;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTAL_COLDEFFECTIVENESS = 160;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTAL_ELECTRICALEFFECTIVENESS = 161;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTAL_HEATEFFECTIVENESS = 162;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALBLASTEFFECTIVENESS = 163;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALBLASTINTEGRITY = 164;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALCOLDEFFECTIVENESS = 165;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALENERGYEFFECTIVENESS = 166;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALENERGYINTEGRITY = 167;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALKINETICEFFECTIVENESS = 168;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALKINETICINTEGRITY = 169;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALSTUNEFFECTIVENESS = 170;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTALSTUNINTEGRITY = 171;
	protected final static int OBJECT_ATTRIBUTE_ELEMENTEALHEATEFFECTIVENESS = 172;
	protected final static int OBJECT_ATTRIBUTE_ENDURANCE = 173;
	protected final static int OBJECT_ATTRIBUTE_ENERGYEFFECTIVENESS = 174;
	protected final static int OBJECT_ATTRIBUTE_ENERGYINTEGRITY = 175;
	protected final static int OBJECT_ATTRIBUTE_RES_ENTANGLE_RESISTANCE = 176;
	protected final static int OBJECT_ATTRIBUTE_ENVIRONMENTAL_HEATEFFECTIVENESS = 177;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DOT_APPLY = 178;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DOT_APPLY_POWER = 179;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DOT_ATTRIBUTE = 180;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DOT_CURE = 181;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DOT_CURE_POWER = 182;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DOT_DURATION = 183;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DOT_POTENCY = 184;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DURATION_ACTION = 185;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DURATION_CONSTITUTION = 186;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DURATION_HEALTH = 187;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DURATION_QUICKNESS = 188;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DURATION_STAMINA = 189;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_DURATION_STRENGTH = 190;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_ACTION = 191;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_CONSTITUTION = 192;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_HEALTH = 193;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_QUICKNESS = 194;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_STAMINA = 195;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_STRENGTH = 196;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_AREA = 197;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_CHARGES = 198;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_DAMAGE_ACTION = 199;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_DAMAGE_HEALTH = 200;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_DAMAGE_MIND = 201;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_RANGE = 202;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_SHOCK = 203;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_STATE = 204;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_ACTION = 205;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_CONSTITUTION = 206;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_FOCUS = 207;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_HEALTH = 208;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_MIND = 209;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_QUICKNESS = 210;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_STAMINA = 211;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_STRENGTH = 212;
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_WILLPOWER = 213;
	protected final static int OBJECT_ATTRIBUTE_EXTRACTRATE = 214;
	protected final static int OBJECT_ATTRIBUTE_FEROCITY = 215;
	protected final static int OBJECT_ATTRIBUTE_FIERCENESS = 216;
	protected final static int OBJECT_ATTRIBUTE_FILLING = 217;
	protected final static int OBJECT_ATTRIBUTE_FOCUS = 218;
	protected final static int OBJECT_ATTRIBUTE_FOCUS_DUR = 219;
	protected final static int OBJECT_ATTRIBUTE_FOCUS_E = 220;
	protected final static int OBJECT_ATTRIBUTE_FOCUSENCUMBRANCE = 221;
	protected final static int OBJECT_ATTRIBUTE_FORTITUDE = 222;
	protected final static int OBJECT_ATTRIBUTE_HARDINESS = 223;
	protected final static int OBJECT_ATTRIBUTE_HARDNESS = 224;
	protected final static int OBJECT_ATTRIBUTE_HEALING_ABILITY = 225;
	protected final static int OBJECT_ATTRIBUTE_HEALTH = 226;
	protected final static int OBJECT_ATTRIBUTE_HEALTH_DUR = 227;
	protected final static int OBJECT_ATTRIBUTE_HEALTH_E = 228;
	protected final static int OBJECT_ATTRIBUTE_HEALTHENCUMBERANCE = 229;
	protected final static int OBJECT_ATTRIBUTE_HEALTHENCUMBRANCE = 230;
	protected final static int OBJECT_ATTRIBUTE_HEATEFFECTIVENESS = 231;
	protected final static int OBJECT_ATTRIBUTE_HIT_POINTS = 232;
	protected final static int OBJECT_ATTRIBUTE_HITPOINTS = 233;
	protected final static int OBJECT_ATTRIBUTE_HOPPERSIZE = 234;
	protected final static int OBJECT_ATTRIBUTE_INTELLECT = 235;
	protected final static int OBJECT_ATTRIBUTE_IS_REPAIR_DROID = 236;
	protected final static int OBJECT_ATTRIBUTE_KILLER = 237;
	protected final static int OBJECT_ATTRIBUTE_KINETICEFFECTIVENESS = 238;
	protected final static int OBJECT_ATTRIBUTE_KINETICINTEGRITY = 239;
	protected final static int OBJECT_ATTRIBUTE_LENGTH = 240;
	protected final static int OBJECT_ATTRIBUTE_MANUFACTURE_SCHEMATIC_VOLUME = 241;
	protected final static int OBJECT_ATTRIBUTE_MAXDAMAGE = 242;
	protected final static int OBJECT_ATTRIBUTE_MAXRANGE = 243;
	protected final static int OBJECT_ATTRIBUTE_MAXRANGEMOD = 244;
	protected final static int OBJECT_ATTRIBUTE_MECHANISM_QUALITY = 245;
	protected final static int OBJECT_ATTRIBUTE_MEDICAL_MODULE = 246;
	protected final static int OBJECT_ATTRIBUTE_MEDPOWER = 247;
	protected final static int OBJECT_ATTRIBUTE_MIDRANGE = 248;
	protected final static int OBJECT_ATTRIBUTE_MIDRANGEMOD = 249;
	protected final static int OBJECT_ATTRIBUTE_MIND = 250;
	protected final static int OBJECT_ATTRIBUTE_MIND_DUR = 251;
	protected final static int OBJECT_ATTRIBUTE_MIND_E = 252;
	protected final static int OBJECT_ATTRIBUTE_MINDAMAGE = 253;
	protected final static int OBJECT_ATTRIBUTE_MINDENCUMBERANCE = 254;
	protected final static int OBJECT_ATTRIBUTE_MINDENCUMBRANCE = 255;
	protected final static int OBJECT_ATTRIBUTE_MOD_IDX_FIVE = 256;
	protected final static int OBJECT_ATTRIBUTE_MOD_IDX_FOUR = 257;
	protected final static int OBJECT_ATTRIBUTE_MOD_IDX_ONE = 258;
	protected final static int OBJECT_ATTRIBUTE_MOD_IDX_SIX = 259;
	protected final static int OBJECT_ATTRIBUTE_MOD_IDX_THREE = 260;
	protected final static int OBJECT_ATTRIBUTE_MOD_IDX_TWO = 261;
	protected final static int OBJECT_ATTRIBUTE_MOD_VAL_FIVE = 262;
	protected final static int OBJECT_ATTRIBUTE_MOD_VAL_FOUR = 263;
	protected final static int OBJECT_ATTRIBUTE_MOD_VAL_ONE = 264;
	protected final static int OBJECT_ATTRIBUTE_MOD_VAL_SIX = 265;
	protected final static int OBJECT_ATTRIBUTE_MOD_VAL_THREE = 266;
	protected final static int OBJECT_ATTRIBUTE_MOD_VAL_TWO = 267;
	protected final static int OBJECT_ATTRIBUTE_OWNER = 268;
	protected final static int OBJECT_ATTRIBUTE_PATTERN = 269;
	protected final static int OBJECT_ATTRIBUTE_PERSONALITY_MODULE = 270;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND = 271;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_0 = 272;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_1 = 273;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_10 = 274;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_11 = 275;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_12 = 276;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_13 = 277;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_14 = 278;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_15 = 279;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_16 = 280;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_17 = 281;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_2 = 282;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_3 = 283;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_4 = 284;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_5 = 285;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_6 = 286;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_7 = 287;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_8 = 288;
	protected final static int OBJECT_ATTRIBUTE_PET_COMMAND_9 = 289;
	protected final static int OBJECT_ATTRIBUTE_PLANET = 290;
	protected final static int OBJECT_ATTRIBUTE_POTENCY = 291;
	protected final static int OBJECT_ATTRIBUTE_POWER = 292;
	protected final static int OBJECT_ATTRIBUTE_POWER_LEVEL = 293;
	protected final static int OBJECT_ATTRIBUTE_POWERUP_USES = 294;
	protected final static int OBJECT_ATTRIBUTE_QUALITY = 295;
	protected final static int OBJECT_ATTRIBUTE_QUANTITY = 296;
	protected final static int OBJECT_ATTRIBUTE_QUEST_DETAILS = 297;
	protected final static int OBJECT_ATTRIBUTE_QUICK_DUR = 298;
	protected final static int OBJECT_ATTRIBUTE_QUICK_E = 299;
	protected final static int OBJECT_ATTRIBUTE_QUICKNESS = 300;
	protected final static int OBJECT_ATTRIBUTE_RANGE = 301;
	protected final static int OBJECT_ATTRIBUTE_REPAIR_MODULE = 302;
	protected final static int OBJECT_ATTRIBUTE_RES_BONE = 303;
	protected final static int OBJECT_ATTRIBUTE_RES_BULK = 304;
	protected final static int OBJECT_ATTRIBUTE_RES_COLD_RESIST = 305;
	protected final static int OBJECT_ATTRIBUTE_RES_CONDUCTIVITY = 306;
	protected final static int OBJECT_ATTRIBUTE_RES_DECAY_RESIST = 307;
	protected final static int OBJECT_ATTRIBUTE_RES_FLAVOR = 308;
	protected final static int OBJECT_ATTRIBUTE_RES_HEAT_RESIST = 309;
	protected final static int OBJECT_ATTRIBUTE_RES_HIDE = 310;
	protected final static int OBJECT_ATTRIBUTE_RES_MALLEABILITY = 311;
	protected final static int OBJECT_ATTRIBUTE_RES_MEAT = 312;
	protected final static int OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY = 313;
	protected final static int OBJECT_ATTRIBUTE_RES_QUALITY = 314;
	protected final static int OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE = 315;
	protected final static int OBJECT_ATTRIBUTE_RES_TOUGHNESS = 316;
	protected final static int OBJECT_ATTRIBUTE_RES_VOLUME = 317;
	protected final static int OBJECT_ATTRIBUTE_RESOURCE_CLASS = 318;
	protected final static int OBJECT_ATTRIBUTE_RESOURCE_CONTENTS = 319;
	protected final static int OBJECT_ATTRIBUTE_RESOURCE_NAME = 320;
	protected final static int OBJECT_ATTRIBUTE_RESTRAINEFFECTIVENESS = 321;
	protected final static int OBJECT_ATTRIBUTE_ROUNDSUSED = 322;
	protected final static int OBJECT_ATTRIBUTE_SERIAL_NUMBER = 323;
	protected final static int OBJECT_ATTRIBUTE_SKILLMODMIN = 324;
	protected final static int OBJECT_ATTRIBUTE_SOCKETS = 325;
	protected final static int OBJECT_ATTRIBUTE_SPEC_ATK_1 = 326;
	protected final static int OBJECT_ATTRIBUTE_SPEC_ATK_2 = 327;
	protected final static int OBJECT_ATTRIBUTE_STAM_DUR = 328;
	protected final static int OBJECT_ATTRIBUTE_STAM_E = 329;
	protected final static int OBJECT_ATTRIBUTE_STAMINA = 330;
	protected final static int OBJECT_ATTRIBUTE_STATE_TYPE_BLINDED = 331;
	protected final static int OBJECT_ATTRIBUTE_STATE_TYPE_DIZZY = 332;
	protected final static int OBJECT_ATTRIBUTE_STATE_TYPE_INTIMIDATED = 333;
	protected final static int OBJECT_ATTRIBUTE_STATE_TYPE_STUNNED = 334;
	protected final static int OBJECT_ATTRIBUTE_STORAGE_MODULE = 335;
	protected final static int OBJECT_ATTRIBUTE_STORAGE_MODULE_RATING = 336;
	protected final static int OBJECT_ATTRIBUTE_STR_DUR = 337;
	protected final static int OBJECT_ATTRIBUTE_STR_E = 338;
	protected final static int OBJECT_ATTRIBUTE_STRENGTH = 339;
	protected final static int OBJECT_ATTRIBUTE_STUNEFFECTIVENESS = 340;
	protected final static int OBJECT_ATTRIBUTE_STUNINTEGRITY = 341;
	protected final static int OBJECT_ATTRIBUTE_TAMABLE = 342;
	protected final static int OBJECT_ATTRIBUTE_TRAVEL_ARRIVAL_PLANET = 343;
	protected final static int OBJECT_ATTRIBUTE_TRAVEL_ARRIVAL_POINT = 344;
	protected final static int OBJECT_ATTRIBUTE_TRAVEL_DEPARTURE_PLANET = 345;
	protected final static int OBJECT_ATTRIBUTE_TRAVEL_DEPARTURE_POINT = 346;
	protected final static int OBJECT_ATTRIBUTE_TYPE = 347;
	protected final static int OBJECT_ATTRIBUTE_USEMODIFIER = 348;
	protected final static int OBJECT_ATTRIBUTE_VOLUME = 349;
	protected final static int OBJECT_ATTRIBUTE_WAYPOINT_ACTIVE = 350;
	protected final static int OBJECT_ATTRIBUTE_WAYPOINT_PLANET = 351;
	protected final static int OBJECT_ATTRIBUTE_WAYPOINT_POSITION = 352;
	protected final static int OBJECT_ATTRIBUTE_WAYPOINT_REGION = 353;
	protected final static int OBJECT_ATTRIBUTE_WEAPON_CERT_STATUS = 354;
	protected final static int OBJECT_ATTRIBUTE_WILL = 355;
	protected final static int OBJECT_ATTRIBUTE_WILL_DUR = 356;
	protected final static int OBJECT_ATTRIBUTE_WILL_E = 357;
	protected final static int OBJECT_ATTRIBUTE_WILLPOWER = 358;
	protected final static int OBJECT_ATTRIBUTE_WOUNDCHANCE = 359;
	protected final static int OBJECT_ATTRIBUTE_WPN_ATTACK_COST_ACTION = 360;
	protected final static int OBJECT_ATTRIBUTE_WPN_ATTACK_COST_HEALTH = 361;
	protected final static int OBJECT_ATTRIBUTE_WPN_ATTACK_COST_MIND = 362;
	protected final static int OBJECT_ATTRIBUTE_WPN_ATTACK_SPEED = 363;
	protected final static int OBJECT_ATTRIBUTE_WPN_DAMAGE = 364;
	protected final static int OBJECT_ATTRIBUTE_WPN_DAMAGE_MAX = 365;
	protected final static int OBJECT_ATTRIBUTE_WPN_DAMAGE_MIN = 366;
	protected final static int OBJECT_ATTRIBUTE_WPN_DAMAGE_RADIUS = 367;
	protected final static int OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MAX = 368;
	protected final static int OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MID = 369;
	protected final static int OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_ZERO = 370;
	protected final static int OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MODS = 371;
	protected final static int OBJECT_ATTRIBUTE_WPN_RANGE_MAX = 372;
	protected final static int OBJECT_ATTRIBUTE_WPN_RANGE_MID = 373;
	protected final static int OBJECT_ATTRIBUTE_WPN_WOUND_CHANCE = 374;
	protected final static int OBJECT_ATTRIBUTE_XP = 375;
	protected final static int OBJECT_ATTRIBUTE_ZERORANGEMOD = 376;
	protected final static int OBJECT_ATTRIBUTE_ENERGY_GEN_RATE = 377;// Energy
	// Generation
	protected final static int OBJECT_ATTRIBUTE_ENERGY_MAINTENANCE = 378;// Energy
	// Maintenance
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_MAINTENANCE = 379;// Surplus
	// Maintenance
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_MAINTENANCE_RATE = 380;// Base
	// Maintenance
	// Rate
	protected final static int OBJECT_ATTRIBUTE_EXAMINE_POWER = 381; // Surplus
	// Power
	protected final static int OBJECT_ATTRIBUTE_EXTRACTION_RATE = 382;// Extraction
	// Rate

	protected final static int NUM_OBJECT_ATTRIBUTES = 383;
	protected final static String[] OBJECT_ATTRIBUTES = new String[NUM_OBJECT_ATTRIBUTES];
	static {
		try {
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ACIDEFFECTIVENESS] = "acideffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ACTION] = "action";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ACTION_DUR] = "action_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ACTION_E] = "action_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ACTIONENCUMBRANCE] = "actionencumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_AGGRO] = "aggro";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_AI_DICTION] = "ai_diction";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_AREA] = "area";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_ACTION_ENCUMBRANCE] = "armor_action_encumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_BASE] = "armor_eff_base";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_BLAST] = "armor_eff_blast";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_ACID] = "armor_eff_elemental_acid";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_COLD] = "armor_eff_elemental_cold";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_ELECTRICAL] = "armor_eff_elemental_electrical";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_HEAT] = "armor_eff_elemental_heat";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ENERGY] = "armor_eff_energy";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_ACID] = "armor_eff_environmental_acid";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_COLD] = "armor_eff_environmental_cold";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_ELECTRICAL] = "armor_eff_environmental_electrical";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_HEAT] = "armor_eff_environmental_heat";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_KINETIC] = "armor_eff_kinetic";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_RESTRAINT] = "armor_eff_restraint";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFF_STUN] = "armor_eff_stun";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_EFFECTIVENESS] = "armor_effectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_HEALTH_ENCUMBRANCE] = "armor_health_encumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_INTEGRITY] = "armor_integrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_MIND_ENCUMBRANCE] = "armor_mind_encumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_MODULE] = "armor_module";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_RATING] = "armor_rating";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_SPECIAL_EFFECTIVENESS] = "armor_special_effectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_SPECIAL_INTEGRITY] = "armor_special_integrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOR_SPECIAL_TYPE] = "armor_special_type";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMOREFFECTIVENESS] = "armoreffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMORENCUMBRANCE] = "armorencumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMORINTEGRITY] = "armorintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMORRATING] = "armorrating";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMORSPECIALEFFECTIVENESS] = "armorspecialeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ARMORSPECIALINTEGRITY] = "armorspecialintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ATTACKACTIONCOST] = "attackactioncost";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ATTACKHEALTHCOST] = "attackhealthcost";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ATTACKMINDCOST] = "attackmindcost";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ATTACKSPEED] = "attackspeed";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ATTRIBMODS] = "attribmods";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BASE_EFFECTIVENESS] = "base_effectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BASEEFFECTIVENESS] = "baseeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BASEINTEGRITY] = "baseintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BASETOHIT] = "basetohit";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_ACTION_MOD] = "bio_comp_action_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_BUSINESS_ACCUMEN] = "bio_comp_business_accumen";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_CAMOUFLAGE] = "bio_comp_camouflage";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_COMBAT_BLEEDING_DEFENSE] = "bio_comp_combat_bleeding_defense";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_CON_MOD] = "bio_comp_con_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_COVER] = "bio_comp_cover";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_CREATURE_EMPATHY] = "bio_comp_creature_empathy";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_CREATURE_TRAINING] = "bio_comp_creature_training";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_DURATION] = "bio_comp_duration";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_FLAVOR] = "bio_comp_flavor";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_FOCUS_MOD] = "bio_comp_focus_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_HEALING_ABILITY] = "bio_comp_healing_ability";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_HEALING_DANCE_WOUND] = "bio_comp_healing_dance_wound";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_HEALING_INJURY_TREATMENT] = "bio_comp_healing_injury_treatment";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_HEALING_MUSIC_WOUND] = "bio_comp_healing_music_wound";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_HEALING_WOUND_TREATMENT] = "bio_comp_healing_wound_treatment";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_HEALTH_MOD] = "bio_comp_health_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_INTIMIDATION] = "bio_comp_intimidation";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_MELEE_DEFENSE] = "bio_comp_melee_defense";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_MIND_MOD] = "bio_comp_mind_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_QUICK_MOD] = "bio_comp_quick_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_STAM_MOD] = "bio_comp_stam_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_STR_MOD] = "bio_comp_str_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_STUN_DEFENSE] = "bio_comp_stun_defense";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_TAKE_COVER] = "bio_comp_take_cover";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_TAME_AGGRO] = "bio_comp_tame_aggro";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_TAME_NON_AGGRO] = "bio_comp_tame_non_aggro";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_WARCRY] = "bio_comp_warcry";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BIO_COMP_WILL_MOD] = "bio_comp_will_mod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BLASTEFFECTIVENESS] = "blasteffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BLASTINTEGRITY] = "blastintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BLUNTEFFECTIVENESS] = "blunteffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BLUNTINTEGRITY] = "bluntintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_BUILDRATE] = "buildrate";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CAT_ARMOR_EFFECTIVENESS] = "cat_armor_effectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CAT_ARMOR_ENCUMBRANCE] = "cat_armor_encumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CAT_ARMOR_SPECIAL_PROTECTION] = "cat_armor_special_protection";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CAT_ARMOR_VULNERABILITY] = "cat_armor_vulnerability";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CAT_MANF_SCHEM_ING_COMPONENT] = "cat_manf_schem_ing_component";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CAT_MANF_SCHEM_ING_RESOURCE] = "cat_manf_schem_ing_resource";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CAT_SKILL_MOD_BONUS] = "cat_skill_mod_bonus";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CHALLENGE_LEVEL] = "challenge_level";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CHARGE] = "charge";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CHARGES] = "charges";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CLEVERNESS] = "cleverness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_COLDEFFECTIVENESS] = "coldeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_COMBAT_HEALING_ABILITY] = "combat_healing_ability";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_COMPLEXITY] = "complexity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CON_DUR] = "con_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CON_E] = "con_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CONDITION] = "condition";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CONSTITUTION] = "constitution";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CONTENTS] = "contents";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_COUNT] = "count";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_COUNTER_CHARGES_REMAINING] = "counter_charges_remaining";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_COUNTER_USES_REMAINING] = "counter_uses_remaining";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_COURAGE] = "courage";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFT_TOOL_STATUS] = "craft_tool_status";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFT_TOOL_TIME] = "craft_tool_time";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFTED_NAME] = "crafted_name";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFTER] = "crafter";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFTING_STATION] = "crafting_station";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFTING_STATION_CLOTHING] = "crafting_station_clothing";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFTING_STATION_FOOD] = "crafting_station_food";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFTING_STATION_STRUCTURE] = "crafting_station_structure";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CRAFTING_STATION_WEAPON] = "crafting_station_weapon";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_ACTION] = "creature_action";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_ATTACK] = "creature_attack";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_CONSTITUTION] = "creature_constitution";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_DAMAGE] = "creature_damage";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_FOCUS] = "creature_focus";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_HEALTH] = "creature_health";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_MIND] = "creature_mind";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_QUICKNESS] = "creature_quickness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_STAMINA] = "creature_stamina";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_STRENGTH] = "creature_strength";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_TOHIT] = "creature_tohit";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CREATURE_WILLPOWER] = "creature_willpower";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_CUSTOM_APPEARANCE] = "custom_appearance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DAMAGE] = "damage";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DATA_MODULE] = "data_module";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DATA_MODULE_RATING] = "data_module_rating";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DATA_VOLUME] = "data_volume";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DECAY_RATE] = "decay_rate";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DECAYRATE] = "decayrate";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DEFAULT] = "default";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DEPENDABILITY] = "dependability";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DEXTERITY] = "dexterity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_ABOVE_AVERAGE] = "dna_comp_above_average";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_AVERAGE] = "dna_comp_average";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_BELOW_AVERAGE] = "dna_comp_below_average";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_CLEVERNESS] = "dna_comp_cleverness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_COURAGE] = "dna_comp_courage";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_DEPENDABILITY] = "dna_comp_dependability";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_DEXTERITY] = "dna_comp_dexterity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_ENDURANCE] = "dna_comp_endurance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_FIERCENESS] = "dna_comp_fierceness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_FORTITUDE] = "dna_comp_fortitude";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_HARDINESS] = "dna_comp_hardiness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_HIGH] = "dna_comp_high";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_INTELLECT] = "dna_comp_intellect";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_LOW] = "dna_comp_low";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_POWER] = "dna_comp_power";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_QUALITY] = "dna_comp_quality";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_SOURCE] = "dna_comp_source";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_VERY_HIGH] = "dna_comp_very_high";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DNA_COMP_VERY_LOW] = "dna_comp_very_low";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DOT_TYPE_DISEASE] = "dot_type_disease";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DOT_TYPE_POISON] = "dot_type_poison";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_DURATION] = "duration";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EFFECT] = "effect";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELECTRICALEFFECTIVENESS] = "electricaleffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTAL_ACIDEFFECTIVENESS] = "elemental_acideffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTAL_COLDEFFECTIVENESS] = "elemental_coldeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTAL_ELECTRICALEFFECTIVENESS] = "elemental_electricaleffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTAL_HEATEFFECTIVENESS] = "elemental_heateffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALBLASTEFFECTIVENESS] = "elementalblasteffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALBLASTINTEGRITY] = "elementalblastintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALCOLDEFFECTIVENESS] = "elementalcoldeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALENERGYEFFECTIVENESS] = "elementalenergyeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALENERGYINTEGRITY] = "elementalenergyintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALKINETICEFFECTIVENESS] = "elementalkineticeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALKINETICINTEGRITY] = "elementalkineticintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALSTUNEFFECTIVENESS] = "elementalstuneffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTALSTUNINTEGRITY] = "elementalstunintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ELEMENTEALHEATEFFECTIVENESS] = "elementealheateffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ENDURANCE] = "endurance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ENERGYEFFECTIVENESS] = "energyeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ENERGYINTEGRITY] = "energyintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_ENTANGLE_RESISTANCE] = "entangle_resistance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ENVIRONMENTAL_HEATEFFECTIVENESS] = "environmental_heateffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DOT_APPLY] = "examine_dot_apply";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DOT_APPLY_POWER] = "examine_dot_apply_power";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DOT_ATTRIBUTE] = "examine_dot_attribute";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DOT_CURE] = "examine_dot_cure";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DOT_CURE_POWER] = "examine_dot_cure_power";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DOT_DURATION] = "examine_dot_duration";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DOT_POTENCY] = "examine_dot_potency";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DURATION_ACTION] = "examine_duration_action";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DURATION_CONSTITUTION] = "examine_duration_constitution";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DURATION_HEALTH] = "examine_duration_health";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DURATION_QUICKNESS] = "examine_duration_quickness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DURATION_STAMINA] = "examine_duration_stamina";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_DURATION_STRENGTH] = "examine_duration_strength";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_ACTION] = "examine_enhance_action";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_CONSTITUTION] = "examine_enhance_constitution";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_HEALTH] = "examine_enhance_health";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_QUICKNESS] = "examine_enhance_quickness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_STAMINA] = "examine_enhance_stamina";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_ENHANCE_STRENGTH] = "examine_enhance_strength";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_AREA] = "examine_heal_area";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_CHARGES] = "examine_heal_charges";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_DAMAGE_ACTION] = "examine_heal_damage_action";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_DAMAGE_HEALTH] = "examine_heal_damage_health";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_DAMAGE_MIND] = "examine_heal_damage_mind";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_RANGE] = "examine_heal_range";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_SHOCK] = "examine_heal_shock";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_STATE] = "examine_heal_state";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_ACTION] = "examine_heal_wound_action";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_CONSTITUTION] = "examine_heal_wound_constitution";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_FOCUS] = "examine_heal_wound_focus";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_HEALTH] = "examine_heal_wound_health";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_MIND] = "examine_heal_wound_mind";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_QUICKNESS] = "examine_heal_wound_quickness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_STAMINA] = "examine_heal_wound_stamina";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_STRENGTH] = "examine_heal_wound_strength";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_HEAL_WOUND_WILLPOWER] = "examine_heal_wound_willpower";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXTRACTRATE] = "extractrate";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_FEROCITY] = "ferocity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_FIERCENESS] = "fierceness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_FILLING] = "filling";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_FOCUS] = "focus";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_FOCUS_DUR] = "focus_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_FOCUS_E] = "focus_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_FOCUSENCUMBRANCE] = "focusencumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_FORTITUDE] = "fortitude";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HARDINESS] = "hardiness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HARDNESS] = "hardness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HEALING_ABILITY] = "healing_ability";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HEALTH] = "health";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HEALTH_DUR] = "health_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HEALTH_E] = "health_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HEALTHENCUMBERANCE] = "healthencumberance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HEALTHENCUMBRANCE] = "healthencumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HEATEFFECTIVENESS] = "heateffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HIT_POINTS] = "hit_points";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HITPOINTS] = "hitpoints";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_HOPPERSIZE] = "hoppersize";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_INTELLECT] = "intellect";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_IS_REPAIR_DROID] = "is_repair_droid";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_KILLER] = "killer";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_KINETICEFFECTIVENESS] = "kineticeffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_KINETICINTEGRITY] = "kineticintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_LENGTH] = "length";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MANUFACTURE_SCHEMATIC_VOLUME] = "manufacture_schematic_volume";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MAXDAMAGE] = "maxdamage";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MAXRANGE] = "maxrange";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MAXRANGEMOD] = "maxrangemod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MECHANISM_QUALITY] = "mechanism_quality";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MEDICAL_MODULE] = "medical_module";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MEDPOWER] = "medpower";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MIDRANGE] = "midrange";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MIDRANGEMOD] = "midrangemod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MIND] = "mind";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MIND_DUR] = "mind_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MIND_E] = "mind_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MINDAMAGE] = "mindamage";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MINDENCUMBERANCE] = "mindencumberance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MINDENCUMBRANCE] = "mindencumbrance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_IDX_FIVE] = "mod_idx_five";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_IDX_FOUR] = "mod_idx_four";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_IDX_ONE] = "mod_idx_one";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_IDX_SIX] = "mod_idx_six";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_IDX_THREE] = "mod_idx_three";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_IDX_TWO] = "mod_idx_two";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_VAL_FIVE] = "mod_val_five";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_VAL_FOUR] = "mod_val_four";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_VAL_ONE] = "mod_val_one";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_VAL_SIX] = "mod_val_six";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_VAL_THREE] = "mod_val_three";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_MOD_VAL_TWO] = "mod_val_two";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_OWNER] = "owner";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PATTERN] = "pattern";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PERSONALITY_MODULE] = "personality_module";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND] = "pet_command";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_0] = "pet_command_0";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_1] = "pet_command_1";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_10] = "pet_command_10";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_11] = "pet_command_11";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_12] = "pet_command_12";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_13] = "pet_command_13";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_14] = "pet_command_14";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_15] = "pet_command_15";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_16] = "pet_command_16";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_17] = "pet_command_17";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_2] = "pet_command_2";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_3] = "pet_command_3";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_4] = "pet_command_4";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_5] = "pet_command_5";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_6] = "pet_command_6";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_7] = "pet_command_7";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_8] = "pet_command_8";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PET_COMMAND_9] = "pet_command_9";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_PLANET] = "planet";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_POTENCY] = "potency";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_POWER] = "power";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_POWER_LEVEL] = "power_level";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_POWERUP_USES] = "powerup_uses";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_QUALITY] = "quality";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_QUANTITY] = "quantity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_QUEST_DETAILS] = "quest_details";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_QUICK_DUR] = "quick_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_QUICK_E] = "quick_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_QUICKNESS] = "quickness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RANGE] = "range";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_REPAIR_MODULE] = "repair_module";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_BONE] = "res_bone";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_BULK] = "res_bulk";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_COLD_RESIST] = "res_cold_resist";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_CONDUCTIVITY] = "res_conductivity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_DECAY_RESIST] = "res_decay_resist";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_FLAVOR] = "res_flavor";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_HEAT_RESIST] = "res_heat_resist";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_HIDE] = "res_hide";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_MALLEABILITY] = "res_malleability";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_MEAT] = "res_meat";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY] = "res_potential_energy";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_QUALITY] = "res_quality";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE] = "res_shock_resistance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_TOUGHNESS] = "res_toughness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RES_VOLUME] = "res_volume";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RESOURCE_CLASS] = "resource_class";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RESOURCE_CONTENTS] = "resource_contents";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RESOURCE_NAME] = "resource_name";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_RESTRAINEFFECTIVENESS] = "restraineffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ROUNDSUSED] = "roundsused";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_SERIAL_NUMBER] = "serial_number";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_SKILLMODMIN] = "skillmodmin";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_SOCKETS] = "sockets";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_SPEC_ATK_1] = "spec_atk_1";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_SPEC_ATK_2] = "spec_atk_2";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STAM_DUR] = "stam_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STAM_E] = "stam_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STAMINA] = "stamina";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STATE_TYPE_BLINDED] = "state_type_blinded";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STATE_TYPE_DIZZY] = "state_type_dizzy";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STATE_TYPE_INTIMIDATED] = "state_type_intimidated";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STATE_TYPE_STUNNED] = "state_type_stunned";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STORAGE_MODULE] = "storage_module";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STORAGE_MODULE_RATING] = "storage_module_rating";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STR_DUR] = "str_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STR_E] = "str_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STRENGTH] = "strength";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STUNEFFECTIVENESS] = "stuneffectiveness";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_STUNINTEGRITY] = "stunintegrity";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_TAMABLE] = "tamable";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_TRAVEL_ARRIVAL_PLANET] = "travel_arrival_planet";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_TRAVEL_ARRIVAL_POINT] = "travel_arrival_point";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_TRAVEL_DEPARTURE_PLANET] = "travel_departure_planet";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_TRAVEL_DEPARTURE_POINT] = "travel_departure_point";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_TYPE] = "type";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_USEMODIFIER] = "usemodifier";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_VOLUME] = "volume";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WAYPOINT_ACTIVE] = "waypoint_active";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WAYPOINT_PLANET] = "waypoint_planet";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WAYPOINT_POSITION] = "waypoint_position";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WAYPOINT_REGION] = "waypoint_region";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WEAPON_CERT_STATUS] = "weapon_cert_status";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WILL] = "will";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WILL_DUR] = "will_dur";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WILL_E] = "will_e";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WILLPOWER] = "willpower";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WOUNDCHANCE] = "woundchance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_ATTACK_COST_ACTION] = "wpn_attack_cost_action";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_ATTACK_COST_HEALTH] = "wpn_attack_cost_health";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_ATTACK_COST_MIND] = "wpn_attack_cost_mind";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_ATTACK_SPEED] = "wpn_attack_speed";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_DAMAGE] = "wpn_damage";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_DAMAGE_MAX] = "wpn_damage_max";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_DAMAGE_MIN] = "wpn_damage_min";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_DAMAGE_RADIUS] = "wpn_damage_radius";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MAX] = "wpn_range_attack_mod_max";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MID] = "wpn_range_attack_mod_mid";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_ZERO] = "wpn_range_attack_mod_zero";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MODS] = "wpn_range_attack_mods";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_RANGE_MAX] = "wpn_range_max";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_RANGE_MID] = "wpn_range_mid";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_WPN_WOUND_CHANCE] = "wpn_wound_chance";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_XP] = "xp";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ZERORANGEMOD] = "zerorangemod";
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ENERGY_GEN_RATE] = "energy_gen_rate";// Energy
			// Generation
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_ENERGY_MAINTENANCE] = "energy_maintenance";// Energy
			// Maintenance
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_MAINTENANCE] = "examine_maintenance";// Surplus
			// Maintenance
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_MAINTENANCE_RATE] = "examine_maintenance_rate";// Base
			// Maintenance
			// Rate
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXAMINE_POWER] = "examine_power"; // Surplus
			// Power
			OBJECT_ATTRIBUTES[OBJECT_ATTRIBUTE_EXTRACTION_RATE] = "extractrate";// Extraction
			// Rate
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error initializing Constants class: "
					+ e.toString());
		}
	}

	protected final static int[] ARMOR_EFFECTIVENESS_STRINGS = {
			OBJECT_ATTRIBUTE_ARMOR_EFF_BASE, OBJECT_ATTRIBUTE_ARMOR_EFF_BLAST,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_ACID,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_COLD,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_ELECTRICAL,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ELEMENTAL_HEAT,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ENERGY,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_ACID,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_COLD,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_ELECTRICAL,
			OBJECT_ATTRIBUTE_ARMOR_EFF_ENVIRONMENTAL_HEAT,
			OBJECT_ATTRIBUTE_ARMOR_EFF_KINETIC,
			OBJECT_ATTRIBUTE_ARMOR_EFF_RESTRAINT,
			OBJECT_ATTRIBUTE_ARMOR_EFF_STUN, };

	// Constants for the spawn manager. The spawn manager is designed to handle
	// any non-player-created spawn.
	protected final static byte NUM_SPAWN_TYPES = 5;
	protected final static byte SPAWN_TYPE_STATIC = 0; // Things like the
	// Nightsister Elder at
	// the Nightsister POI
	// on Dathomir.
	protected final static byte SPAWN_TYPE_STATIC_NON_ATTACKABLE = 1; // Things
	// like
	// skill
	// trainers,
	// terminals,
	// etc.
	protected final static byte SPAWN_TYPE_DYNAMIC_NO_LAIR = 2; // Things like
	// Krayt
	// Dragons, or
	// other
	// creatures
	// that happen
	// to randomly
	// appear.
	protected final static byte SPAWN_TYPE_DYNAMIC = 3; // Things like lairs of
	// Pikets.
	protected final static byte SPAWN_TYPE_MISSION = 4; // Bounty marks and
	// other regular
	// seek-and-destroy
	// missions.

	/**
	 * Skill trainer Skills By Profession Skills have to go in the order laid
	 * out or trainer dialog will break!!!!!
	 */
	/**
	 * CHEF
	 */
	protected final static String[] CHEF_SKILLS = { "crafting_chef",// 0
			"crafting_chef_novice", // 1

			"crafting_chef_dessert_01",// 2
			"crafting_chef_dish_01",// 3
			"crafting_chef_drink_01",// 4
			"crafting_chef_techniques_01", // 5

			"crafting_chef_dessert_02",// 6
			"crafting_chef_dish_02",// 7
			"crafting_chef_drink_02",// 8
			"crafting_chef_techniques_02",// 9

			"crafting_chef_dessert_03",// 10
			"crafting_chef_dish_03", // 11
			"crafting_chef_drink_03", // 12
			"crafting_chef_techniques_03", // 13

			"crafting_chef_dessert_04",// 14
			"crafting_chef_dish_04",// 15
			"crafting_chef_drink_04", // 16
			"crafting_chef_techniques_04",// 17

			"crafting_chef_master",// 18
	};

	/**
	 * Artisan
	 */
	protected final static String[] ARTISAN_SKILLS = { "crafting_artisan",
			"crafting_artisan_novice",

			"crafting_artisan_business_01", "crafting_artisan_domestic_01",
			"crafting_artisan_engineering_01", "crafting_artisan_survey_01",

			"crafting_artisan_business_02", "crafting_artisan_domestic_02",
			"crafting_artisan_engineering_02", "crafting_artisan_survey_02",

			"crafting_artisan_business_03", "crafting_artisan_domestic_03",
			"crafting_artisan_engineering_03", "crafting_artisan_survey_03",

			"crafting_artisan_business_04", "crafting_artisan_domestic_04",
			"crafting_artisan_engineering_04", "crafting_artisan_survey_04",

			"crafting_artisan_master"

	};

	/**
	 * Marksman
	 */
	protected final static String[] MARKSMAN_SKILLS = {

	"combat_marksman", "combat_marksman_novice",

	"combat_marksman_pistol_01", "combat_marksman_carbine_01",
			"combat_marksman_support_01", "combat_marksman_rifle_01",

			"combat_marksman_pistol_02", "combat_marksman_carbine_02",
			"combat_marksman_support_02", "combat_marksman_rifle_02",

			"combat_marksman_pistol_03", "combat_marksman_carbine_03",
			"combat_marksman_support_03", "combat_marksman_rifle_03",

			"combat_marksman_pistol_04", "combat_marksman_carbine_04",
			"combat_marksman_support_04", "combat_marksman_rifle_04",

			"combat_marksman_master",

	};

	/**
	 * Brawler
	 */

	protected final static String[] BRAWLER_SKILLS = {

	"combat_brawler", "combat_brawler_novice",

	"combat_brawler_unarmed_01", "combat_brawler_polearm_01",
			"combat_brawler_2handmelee_01", "combat_brawler_1handmelee_01",

			"combat_brawler_unarmed_02", "combat_brawler_polearm_02",
			"combat_brawler_2handmelee_02", "combat_brawler_1handmelee_02",

			"combat_brawler_unarmed_03", "combat_brawler_polearm_03",
			"combat_brawler_2handmelee_03", "combat_brawler_1handmelee_03",

			"combat_brawler_unarmed_04", "combat_brawler_polearm_04",
			"combat_brawler_2handmelee_04", "combat_brawler_1handmelee_04",

			"combat_brawler_master", };

	protected final static String[] MEDIC_SKILLS = { "science_medic",
			"science_medic_novice",

			"science_medic_injury_speed_01", "science_medic_crafting_01",
			"science_medic_injury_01", "science_medic_ability_01",

			"science_medic_injury_speed_02", "science_medic_crafting_02",
			"science_medic_injury_02", "science_medic_ability_02",

			"science_medic_injury_speed_03", "science_medic_crafting_03",
			"science_medic_injury_03", "science_medic_ability_03",

			"science_medic_injury_speed_04", "science_medic_crafting_04",
			"science_medic_injury_04", "science_medic_ability_04",

			"science_medic_master", };

	protected final static String[] ENTERTAINER_SKILLS = {
			"social_entertainer", "social_entertainer_novice",

			"social_entertainer_dance_01", "social_entertainer_healing_01",
			"social_entertainer_music_01", "social_entertainer_hairstyle_01",

			"social_entertainer_dance_02", "social_entertainer_healing_02",
			"social_entertainer_music_02", "social_entertainer_hairstyle_02",

			"social_entertainer_dance_03", "social_entertainer_healing_03",
			"social_entertainer_music_03", "social_entertainer_hairstyle_03",

			"social_entertainer_dance_04", "social_entertainer_healing_04",
			"social_entertainer_music_04", "social_entertainer_hairstyle_04",

			"social_entertainer_master",

	};

	protected final static String[] SCOUT_SKILLS = { "outdoors_scout",
			"outdoors_scout_novice",

			"outdoors_scout_movement_01", "outdoors_scout_camp_01",
			"outdoors_scout_tools_01", "outdoors_scout_harvest_01",

			"outdoors_scout_movement_02", "outdoors_scout_camp_02",
			"outdoors_scout_tools_02", "outdoors_scout_harvest_02",

			"outdoors_scout_movement_03", "outdoors_scout_camp_03",
			"outdoors_scout_tools_03", "outdoors_scout_harvest_03",

			"outdoors_scout_movement_04", "outdoors_scout_camp_04",
			"outdoors_scout_tools_04", "outdoors_scout_harvest_04",

			"outdoors_scout_master", };

	protected final static String[] ONEHANDSWORD_SKILLS = { "combat_1hsword",
			"combat_1hsword_novice", "combat_1hsword_accuracy_01",
			"combat_1hsword_speed_01", "combat_1hsword_ability_01",
			"combat_1hsword_support_01", "combat_1hsword_accuracy_02",
			"combat_1hsword_speed_02", "combat_1hsword_ability_02",
			"combat_1hsword_support_02", "combat_1hsword_accuracy_03",
			"combat_1hsword_speed_03", "combat_1hsword_ability_03",
			"combat_1hsword_support_03", "combat_1hsword_accuracy_04",
			"combat_1hsword_speed_04", "combat_1hsword_ability_04",
			"combat_1hsword_support_04", "combat_1hsword_master", };

	protected final static String[] TWOHANDSWORD_SKILLS = { "combat_2hsword",
			"combat_2hsword_novice", "combat_2hsword_accuracy_01",
			"combat_2hsword_speed_01", "combat_2hsword_ability_01",
			"combat_2hsword_support_01", "combat_2hsword_accuracy_02",
			"combat_2hsword_speed_02", "combat_2hsword_ability_02",
			"combat_2hsword_support_02", "combat_2hsword_accuracy_03",
			"combat_2hsword_speed_03", "combat_2hsword_ability_03",
			"combat_2hsword_support_03", "combat_2hsword_accuracy_04",
			"combat_2hsword_speed_04", "combat_2hsword_ability_04",
			"combat_2hsword_support_04", "combat_2hsword_master", };

	protected final static String[] ARCHITECH_SKILLS = { "crafting_architect",
			"crafting_architect_novice", "crafting_architect_production_01",
			"crafting_architect_techniques_01",
			"crafting_architect_harvesting_01",
			"crafting_architect_blueprints_01",
			"crafting_architect_production_02",
			"crafting_architect_techniques_02",
			"crafting_architect_harvesting_02",
			"crafting_architect_blueprints_02",
			"crafting_architect_production_03",
			"crafting_architect_techniques_03",
			"crafting_architect_harvesting_03",
			"crafting_architect_blueprints_03",
			"crafting_architect_production_04",
			"crafting_architect_techniques_04",
			"crafting_architect_harvesting_04",
			"crafting_architect_blueprints_04", "crafting_architect_master", };

	protected final static String[] ARMORSMITH_SKILLS = {
			"crafting_armorsmith", "crafting_armorsmith_novice",
			"crafting_armorsmith_personal_01", "crafting_armorsmith_heavy_01",
			"crafting_armorsmith_deflectors_01",
			"crafting_armorsmith_complexity_01",
			"crafting_armorsmith_personal_02", "crafting_armorsmith_heavy_02",
			"crafting_armorsmith_deflectors_02",
			"crafting_armorsmith_complexity_02",
			"crafting_armorsmith_personal_03", "crafting_armorsmith_heavy_03",
			"crafting_armorsmith_deflectors_03",
			"crafting_armorsmith_complexity_03",
			"crafting_armorsmith_personal_04", "crafting_armorsmith_heavy_04",
			"crafting_armorsmith_deflectors_04",
			"crafting_armorsmith_complexity_04", "crafting_armorsmith_master", };

	protected final static String[] BIOENGINEER_SKILLS = {
			"outdoors_bio_engineer", "outdoors_bio_engineer_novice",
			"outdoors_bio_engineer_creature_01",
			"outdoors_bio_engineer_tissue_01",
			"outdoors_bio_engineer_dna_harvesting_01",
			"outdoors_bio_engineer_production_01",
			"outdoors_bio_engineer_creature_02",
			"outdoors_bio_engineer_tissue_02",
			"outdoors_bio_engineer_dna_harvesting_02",
			"outdoors_bio_engineer_production_02",
			"outdoors_bio_engineer_creature_03",
			"outdoors_bio_engineer_tissue_03",
			"outdoors_bio_engineer_dna_harvesting_03",
			"outdoors_bio_engineer_production_03",
			"outdoors_bio_engineer_creature_04",
			"outdoors_bio_engineer_tissue_04",
			"outdoors_bio_engineer_dna_harvesting_04",
			"outdoors_bio_engineer_production_04",
			"outdoors_bio_engineer_master", };

	protected final static String[] BOUNTYHUNTER_SKILLS = {
			"combat_bountyhunter", "combat_bountyhunter_novice",
			"combat_bountyhunter_investigation_01",
			"combat_bountyhunter_droidcontrol_01",
			"combat_bountyhunter_droidresponse_01",
			"combat_bountyhunter_support_01",
			"combat_bountyhunter_droidcontrol_02",
			"combat_bountyhunter_droidresponse_02",
			"combat_bountyhunter_support_02",
			"combat_bountyhunter_investigation_03",
			"combat_bountyhunter_droidcontrol_03",
			"combat_bountyhunter_droidresponse_03",
			"combat_bountyhunter_support_03",
			"combat_bountyhunter_investigation_04",
			"combat_bountyhunter_droidcontrol_04",
			"combat_bountyhunter_droidresponse_04",
			"combat_bountyhunter_support_04", "combat_bountyhunter_master", };

	protected final static String[] CARBINE_SKILLS = { "combat_carbine",
			"combat_carbine_novice", "combat_carbine_accuracy_01",
			"combat_carbine_speed_01", "combat_carbine_ability_01",
			"combat_carbine_support_01", "combat_carbine_accuracy_02",
			"combat_carbine_speed_02", "combat_carbine_ability_02",
			"combat_carbine_support_02", "combat_carbine_accuracy_03",
			"combat_carbine_speed_03", "combat_carbine_ability_03",
			"combat_carbine_support_03", "combat_carbine_accuracy_04",
			"combat_carbine_speed_04", "combat_carbine_ability_04",
			"combat_carbine_support_04", "combat_carbine_master", };

	protected final static String[] COMBATMEDIC_SKILLS = {
			"science_combatmedic", "science_combatmedic_novice",
			"science_combatmedic_healing_range_01",
			"science_combatmedic_healing_range_speed_01",
			"science_combatmedic_medicine_01",
			"science_combatmedic_support_01",
			"science_combatmedic_healing_range_02",
			"science_combatmedic_healing_range_speed_02",
			"science_combatmedic_medicine_02",
			"science_combatmedic_support_02",
			"science_combatmedic_healing_range_03",
			"science_combatmedic_healing_range_speed_03",
			"science_combatmedic_medicine_03",
			"science_combatmedic_support_03",
			"science_combatmedic_healing_range_04",
			"science_combatmedic_healing_range_speed_04",
			"science_combatmedic_medicine_04",
			"science_combatmedic_support_04", "science_combatmedic_master", };

	protected final static String[] COMMANDO_SKILLS = { "combat_commando",
			"combat_commando_novice",
			"combat_commando_heavyweapon_accuracy_01",
			"combat_commando_heavyweapon_speed_01",
			"combat_commando_thrownweapon_01", "combat_commando_support_01",
			"combat_commando_heavyweapon_accuracy_02",
			"combat_commando_heavyweapon_speed_02",
			"combat_commando_thrownweapon_02", "combat_commando_support_02",
			"combat_commando_heavyweapon_accuracy_03",
			"combat_commando_heavyweapon_speed_03",
			"combat_commando_thrownweapon_03", "combat_commando_support_03",
			"combat_commando_heavyweapon_accuracy_04",
			"combat_commando_heavyweapon_speed_04",
			"combat_commando_thrownweapon_04", "combat_commando_support_04",
			"combat_commando_master", };

	protected final static String[] CREATUREHANDLER_SKILLS = {
			"outdoors_creaturehandler", "outdoors_creaturehandler_novice",
			"outdoors_creaturehandler_taming_01",
			"outdoors_creaturehandler_training_01",
			"outdoors_creaturehandler_healing_01",
			"outdoors_creaturehandler_support_01",
			"outdoors_creaturehandler_taming_02",
			"outdoors_creaturehandler_training_02",
			"outdoors_creaturehandler_healing_02",
			"outdoors_creaturehandler_support_02",
			"outdoors_creaturehandler_taming_03",
			"outdoors_creaturehandler_training_03",
			"outdoors_creaturehandler_healing_03",
			"outdoors_creaturehandler_support_03",
			"outdoors_creaturehandler_taming_04",
			"outdoors_creaturehandler_training_04",
			"outdoors_creaturehandler_healing_04",
			"outdoors_creaturehandler_support_04",
			"outdoors_creaturehandler_master", };

	protected final static String[] DANCER_SKILLS = { "social_dancer",
			"social_dancer_novice", "social_dancer_ability_01",
			"social_dancer_wound_01", "social_dancer_knowledge_01",
			"social_dancer_shock_01", "social_dancer_ability_02",
			"social_dancer_wound_02", "social_dancer_knowledge_02",
			"social_dancer_shock_02", "social_dancer_ability_03",
			"social_dancer_wound_03", "social_dancer_knowledge_03",
			"social_dancer_shock_03", "social_dancer_ability_04",
			"social_dancer_wound_04", "social_dancer_knowledge_04",
			"social_dancer_shock_04", "social_dancer_master", };

	protected final static String[] DOCTOR_SKILLS = { "science_doctor",
			"science_doctor_novice", "science_doctor_wound_speed_01",
			"science_doctor_wound_01", "science_doctor_ability_01",
			"science_doctor_support_01", "science_doctor_wound_speed_02",
			"science_doctor_wound_02", "science_doctor_ability_02",
			"science_doctor_support_02", "science_doctor_wound_speed_03",
			"science_doctor_wound_03", "science_doctor_ability_03",
			"science_doctor_support_03", "science_doctor_wound_speed_04",
			"science_doctor_wound_04", "science_doctor_ability_04",
			"science_doctor_support_04", "science_doctor_master", };

	protected final static String[] DROIDENGINEER_SKILLS = {
			"crafting_droidengineer", "crafting_droidengineer_novice",
			"crafting_droidengineer_production_01",
			"crafting_droidengineer_techniques_01",
			"crafting_droidengineer_refinement_01",
			"crafting_droidengineer_blueprints_01",
			"crafting_droidengineer_production_02",
			"crafting_droidengineer_techniques_02",
			"crafting_droidengineer_refinement_02",
			"crafting_droidengineer_blueprints_02",
			"crafting_droidengineer_production_03",
			"crafting_droidengineer_techniques_03",
			"crafting_droidengineer_refinement_03",
			"crafting_droidengineer_blueprints_03",
			"crafting_droidengineer_production_04",
			"crafting_droidengineer_techniques_04",
			"crafting_droidengineer_refinement_04",
			"crafting_droidengineer_blueprints_04",
			"crafting_droidengineer_master", };

	protected final static String[] JEDIFS_SKILLS = { "jedi", };

	protected final static String[] JEDIPADAWAN_SKILLS = { "jedi_padawan",
			"jedi_padawan_novice", "jedi_padawan_saber_01",
			"jedi_padawan_healing_01", "jedi_padawan_force_power_01",
			"jedi_padawan_force_manipulation_01", "jedi_padawan_saber_02",
			"jedi_padawan_healing_02", "jedi_padawan_force_power_02",
			"jedi_padawan_force_manipulation_02", "jedi_padawan_saber_03",
			"jedi_padawan_healing_03", "jedi_padawan_force_power_03",
			"jedi_padawan_force_manipulation_03", "jedi_padawan_saber_04",
			"jedi_padawan_healing_04", "jedi_padawan_force_power_04",
			"jedi_padawan_force_manipulation_04", "jedi_padawan_master",

	};

	protected final static String[] JEDILSJOURNEYMAN_SKILLS = {
			"jedi_light_side_journeyman", "jedi_light_side_journeyman_novice",
			"jedi_light_side_journeyman_saber_01",
			"jedi_light_side_journeyman_healing_01",
			"jedi_light_side_journeyman_force_power_01",
			"jedi_light_side_journeyman_force_manipulation_01",
			"jedi_light_side_journeyman_saber_02",
			"jedi_light_side_journeyman_healing_02",
			"jedi_light_side_journeyman_force_power_02",
			"jedi_light_side_journeyman_force_manipulation_02",
			"jedi_light_side_journeyman_saber_03",
			"jedi_light_side_journeyman_healing_03",
			"jedi_light_side_journeyman_force_power_03",
			"jedi_light_side_journeyman_force_manipulation_03",
			"jedi_light_side_journeyman_saber_04",
			"jedi_light_side_journeyman_healing_04",
			"jedi_light_side_journeyman_force_power_04",
			"jedi_light_side_journeyman_force_manipulation_04",
			"jedi_light_side_journeyman_master",

	};

	protected final static String[] JEDILSMASTER_SKILLS = {
			"jedi_light_side_master", "jedi_light_side_master_novice",
			"jedi_light_side_master_saber_01",
			"jedi_light_side_master_healing_01",
			"jedi_light_side_master_force_power_01",
			"jedi_light_side_master_force_manipulation_01",
			"jedi_light_side_master_saber_02",
			"jedi_light_side_master_healing_02",
			"jedi_light_side_master_force_power_02",
			"jedi_light_side_master_force_manipulation_02",
			"jedi_light_side_master_saber_03",
			"jedi_light_side_master_healing_03",
			"jedi_light_side_master_force_power_03",
			"jedi_light_side_master_force_manipulation_03",
			"jedi_light_side_master_saber_04",
			"jedi_light_side_master_healing_04",
			"jedi_light_side_master_force_power_04",
			"jedi_light_side_master_force_manipulation_04",
			"jedi_light_side_master_master",

	};

	protected final static String[] JEDIDSJOURNEYMAN_SKILLS = {
			"jedi_dark_side_journeyman", "jedi_dark_side_journeyman_novice",
			"jedi_dark_side_journeyman_saber_01",
			"jedi_dark_side_journeyman_healing_01",
			"jedi_dark_side_journeyman_force_power_01",
			"jedi_dark_side_journeyman_force_manipulation_01",
			"jedi_dark_side_journeyman_saber_02",
			"jedi_dark_side_journeyman_healing_02",
			"jedi_dark_side_journeyman_force_power_02",
			"jedi_dark_side_journeyman_force_manipulation_02",
			"jedi_dark_side_journeyman_saber_03",
			"jedi_dark_side_journeyman_healing_03",
			"jedi_dark_side_journeyman_force_power_03",
			"jedi_dark_side_journeyman_force_manipulation_03",
			"jedi_dark_side_journeyman_saber_04",
			"jedi_dark_side_journeyman_healing_04",
			"jedi_dark_side_journeyman_force_power_04",
			"jedi_dark_side_journeyman_force_manipulation_04",
			"jedi_dark_side_journeyman_master",

	};

	protected final static String[] JEDIDSMASTER_SKILLS = {
			"jedi_dark_side_master", "jedi_dark_side_master_novice",
			"jedi_dark_side_master_saber_01",
			"jedi_dark_side_master_healing_01",
			"jedi_dark_side_master_force_power_01",
			"jedi_dark_side_master_force_manipulation_01",
			"jedi_dark_side_master_saber_02",
			"jedi_dark_side_master_healing_02",
			"jedi_dark_side_master_force_power_02",
			"jedi_dark_side_master_force_manipulation_02",
			"jedi_dark_side_master_saber_03",
			"jedi_dark_side_master_healing_03",
			"jedi_dark_side_master_force_power_03",
			"jedi_dark_side_master_force_manipulation_03",
			"jedi_dark_side_master_saber_04",
			"jedi_dark_side_master_healing_04",
			"jedi_dark_side_master_force_power_04",
			"jedi_dark_side_master_force_manipulation_04",
			"jedi_dark_side_master_master",

	};

	protected final static String[] IMAGEDESIGNER_SKILLS = {
			"social_imagedesigner", "social_imagedesigner_novice",
			"social_imagedesigner_hairstyle_01",
			"social_imagedesigner_exotic_01",
			"social_imagedesigner_bodyform_01",
			"social_imagedesigner_markings_01",
			"social_imagedesigner_hairstyle_02",
			"social_imagedesigner_exotic_02",
			"social_imagedesigner_bodyform_02",
			"social_imagedesigner_markings_02",
			"social_imagedesigner_hairstyle_03",
			"social_imagedesigner_exotic_03",
			"social_imagedesigner_bodyform_03",
			"social_imagedesigner_markings_03",
			"social_imagedesigner_hairstyle_04",
			"social_imagedesigner_exotic_04",
			"social_imagedesigner_bodyform_04",
			"social_imagedesigner_markings_04", "social_imagedesigner_master",

	};

	protected final static String[] MUSICIAN_SKILLS = { "social_musician",
			"social_musician_novice", "social_musician_ability_01",
			"social_musician_wound_01", "social_musician_knowledge_01",
			"social_musician_shock_01", "social_musician_ability_02",
			"social_musician_wound_02", "social_musician_knowledge_02",
			"social_musician_shock_02", "social_musician_ability_03",
			"social_musician_wound_03", "social_musician_knowledge_03",
			"social_musician_shock_03", "social_musician_ability_04",
			"social_musician_wound_04", "social_musician_knowledge_04",
			"social_musician_shock_04", "social_musician_master",

	};

	protected final static String[] PISTOL_SKILLS = { "combat_pistol",
			"combat_pistol_novice", "combat_pistol_accuracy_01",
			"combat_pistol_speed_01", "combat_pistol_ability_01",
			"combat_pistol_support_01", "combat_pistol_accuracy_02",
			"combat_pistol_speed_02", "combat_pistol_ability_02",
			"combat_pistol_support_02", "combat_pistol_accuracy_03",
			"combat_pistol_speed_03", "combat_pistol_ability_03",
			"combat_pistol_support_03", "combat_pistol_accuracy_04",
			"combat_pistol_speed_04", "combat_pistol_ability_04",
			"combat_pistol_support_04", "combat_pistol_master",

	};

	protected final static String[] POLEARM_SKILLS = { "combat_polearm",
			"combat_polearm_novice", "combat_polearm_accuracy_01",
			"combat_polearm_speed_01", "combat_polearm_ability_01",
			"combat_polearm_support_01", "combat_polearm_accuracy_02",
			"combat_polearm_speed_02", "combat_polearm_ability_02",
			"combat_polearm_support_02", "combat_polearm_accuracy_03",
			"combat_polearm_speed_03", "combat_polearm_ability_03",
			"combat_polearm_support_03", "combat_polearm_accuracy_04",
			"combat_polearm_speed_04", "combat_polearm_ability_04",
			"combat_polearm_support_04", "combat_polearm_master",

	};

	protected final static String[] MERCHANT_SKILLS = { "crafting_merchant",
			"crafting_merchant_novice", "crafting_merchant_advertising_01",
			"crafting_merchant_sales_01", "crafting_merchant_hiring_01",
			"crafting_merchant_management_01",
			"crafting_merchant_advertising_02", "crafting_merchant_sales_02",
			"crafting_merchant_hiring_02", "crafting_merchant_management_02",
			"crafting_merchant_advertising_03", "crafting_merchant_sales_03",
			"crafting_merchant_hiring_03", "crafting_merchant_management_03",
			"crafting_merchant_advertising_04", "crafting_merchant_sales_04",
			"crafting_merchant_hiring_04", "crafting_merchant_management_04",
			"crafting_merchant_master",

	};

	protected final static String[] RANGER_SKILLS = { "outdoors_ranger",
			"outdoors_ranger_novice", "outdoors_ranger_movement_01",
			"outdoors_ranger_tracking_01", "outdoors_ranger_harvest_01",
			"outdoors_ranger_support_01", "outdoors_ranger_movement_02",
			"outdoors_ranger_tracking_02", "outdoors_ranger_harvest_02",
			"outdoors_ranger_support_02", "outdoors_ranger_movement_03",
			"outdoors_ranger_tracking_03", "outdoors_ranger_harvest_03",
			"outdoors_ranger_support_03", "outdoors_ranger_movement_04",
			"outdoors_ranger_tracking_04", "outdoors_ranger_harvest_04",
			"outdoors_ranger_support_04", "outdoors_ranger_master",

	};

	protected final static String[] RIFLEMAN_SKILLS = { "combat_rifleman",
			"combat_rifleman_novice", "combat_rifleman_accuracy_01",
			"combat_rifleman_speed_01", "combat_rifleman_ability_01",
			"combat_rifleman_support_01", "combat_rifleman_accuracy_02",
			"combat_rifleman_speed_02", "combat_rifleman_ability_02",
			"combat_rifleman_support_02", "combat_rifleman_accuracy_03",
			"combat_rifleman_speed_03", "combat_rifleman_ability_03",
			"combat_rifleman_support_03", "combat_rifleman_accuracy_04",
			"combat_rifleman_speed_04", "combat_rifleman_ability_04",
			"combat_rifleman_support_04", "combat_rifleman_master",

	};

	protected final static String[] SMUGGLER_SKILLS = { "combat_smuggler",
			"combat_smuggler_novice", "combat_smuggler_underworld_01",
			"combat_smuggler_slicing_01", "combat_smuggler_combat_01",
			"combat_smuggler_spice_01", "combat_smuggler_underworld_02",
			"combat_smuggler_slicing_02", "combat_smuggler_combat_02",
			"combat_smuggler_spice_02", "combat_smuggler_underworld_03",
			"combat_smuggler_slicing_03", "combat_smuggler_combat_03",
			"combat_smuggler_spice_03", "combat_smuggler_underworld_04",
			"combat_smuggler_slicing_04", "combat_smuggler_combat_04",
			"combat_smuggler_spice_04", "combat_smuggler_master",

	};

	protected final static String[] SQUADLEADER_SKILLS = {
			"outdoors_squadleader", "outdoors_squadleader_novice",
			"outdoors_squadleader_master", "outdoors_squadleader_movement_01",
			"outdoors_squadleader_movement_02",
			"outdoors_squadleader_movement_03",
			"outdoors_squadleader_movement_04",
			"outdoors_squadleader_offense_01",
			"outdoors_squadleader_offense_02",
			"outdoors_squadleader_offense_03",
			"outdoors_squadleader_offense_04",
			"outdoors_squadleader_defense_01",
			"outdoors_squadleader_defense_02",
			"outdoors_squadleader_defense_03",
			"outdoors_squadleader_defense_04",
			"outdoors_squadleader_support_01",
			"outdoors_squadleader_support_02",
			"outdoors_squadleader_support_03",
			"outdoors_squadleader_support_04", };

	protected final static String[] TAILOR_SKILLS = { "crafting_tailor",
			"crafting_tailor_novice", "crafting_tailor_casual_01",
			"crafting_tailor_field_01", "crafting_tailor_formal_01",
			"crafting_tailor_production_01", "crafting_tailor_casual_02",
			"crafting_tailor_field_02", "crafting_tailor_formal_02",
			"crafting_tailor_production_02", "crafting_tailor_casual_03",
			"crafting_tailor_field_03", "crafting_tailor_formal_03",
			"crafting_tailor_production_03", "crafting_tailor_casual_04",
			"crafting_tailor_field_04", "crafting_tailor_formal_04",
			"crafting_tailor_production_04", "crafting_tailor_master", };

	protected final static String[] UNARMED_SKILLS = { "combat_unarmed",
			"combat_unarmed_novice", "combat_unarmed_accuracy_01",
			"combat_unarmed_speed_01", "combat_unarmed_ability_01",
			"combat_unarmed_support_01", "combat_unarmed_accuracy_02",
			"combat_unarmed_speed_02", "combat_unarmed_ability_02",
			"combat_unarmed_support_02", "combat_unarmed_accuracy_03",
			"combat_unarmed_speed_03", "combat_unarmed_ability_03",
			"combat_unarmed_support_03", "combat_unarmed_accuracy_04",
			"combat_unarmed_speed_04", "combat_unarmed_ability_04",
			"combat_unarmed_support_04", "combat_unarmed_master", };

	protected final static String[] WEAPONSMITH_SKILLS = {
			"crafting_weaponsmith", "crafting_weaponsmith_novice",
			"crafting_weaponsmith_melee_01",
			"crafting_weaponsmith_firearms_01",
			"crafting_weaponsmith_munitions_01",
			"crafting_weaponsmith_techniques_01",
			"crafting_weaponsmith_melee_02",
			"crafting_weaponsmith_firearms_02",
			"crafting_weaponsmith_munitions_02",
			"crafting_weaponsmith_techniques_02",
			"crafting_weaponsmith_melee_03",
			"crafting_weaponsmith_firearms_03",
			"crafting_weaponsmith_munitions_03",
			"crafting_weaponsmith_techniques_03",
			"crafting_weaponsmith_melee_04",
			"crafting_weaponsmith_firearms_04",
			"crafting_weaponsmith_munitions_04",
			"crafting_weaponsmith_techniques_04",
			"crafting_weaponsmith_master", };

	protected final static String[] SHIPWRIGHT_SKILLS = {
			"crafting_shipwright", "crafting_shipwright_novice",
			"crafting_shipwright_defense_01",
			"crafting_shipwright_engineering_01",
			"crafting_shipwright_propulsion_01",
			"crafting_shipwright_systems_01",
			"crafting_shipwright_engineering_02",
			"crafting_shipwright_defense_02",
			"crafting_shipwright_propulsion_02",
			"crafting_shipwright_systems_02", "crafting_shipwright_defense_03",
			"crafting_shipwright_engineering_03",
			"crafting_shipwright_propulsion_03",
			"crafting_shipwright_systems_03",
			"crafting_shipwright_engineering_04",
			"crafting_shipwright_defense_04",
			"crafting_shipwright_propulsion_04",
			"crafting_shipwright_systems_04", "crafting_shipwright_master", };

	protected static enum BADGES {
		COUNT_5, // BDG_5_Badges, //= bit(0),//5 Badges//1
		COUNT_10, // BDG_10_Badges, //= bit(1),//10 Badges//2
		COUNT_25, // BDG_25_Badges, //= bit(2),//25 Badges//4
		COUNT_50, // BDG_50_Badges, //= bit(3),//50 Badges//8
		COUNT_75, // BDG_75_Badges, //= bit(4),//75 //10
		COUNT_100, // BDG_100_Badges, //= bit(5),//100//20
		COUNT_125, // BDG_125_Badges, //= bit(6),//125//40
		POI_RABIDBEAST, // BDG_Q_MoC, //= bit(7),//Quest: Mark of Courage//80
		POI_PRISONBREAK, // BDG_Q_MoHo, //= bit(8),//Quest: Mark of Honor//100
		POI_TWOLIARS, // BDG_Q_MoI, //= bit(9),//Quest: Mark of Intellect//200
		POI_FACTORYLIBERATION, // BDG_Q_MoA, //= bit(10),//Quest: Mark of
		// Altruism//400
		POI_HEROMARK, // BDG_Q_MoHe, //= bit(11),//Quest: Mark of the Hero//800
		EXP_TAT_BENS_HUT, // BDG_Loc_BKOH, //= bit(12),//Location: Ben Kenobi's
		// Old Home
		EXP_TAT_TUSKEN_POOL, // BDG_Loc_PBFT, //= bit(13),//Location: Pool
		// Beneath Fort Tusken
		EXP_TAT_KRAYT_SKELETON, // BDG_Loc_SFGKD, //= bit(14),//Location:
		// Skeleton of the Famed Greater Kryt Dragon
		EXP_TAT_ESCAPE_POD, // BDG_Loc_EPUCR, //= bit(15),//Location: Escape Pod
		// Used by C-3P0 and R2-D2
		EXP_TAT_SARLACC_PIT, // BDG_Loc_PMS, //= bit(16),//Location:Pit of the
		// Might Sarlacc
		EXP_TAT_LARS_HOMESTEAD, // BDG_Loc_LH, //= bit(17),//Location:Lars
		// Homestead
		EXP_TAT_KRAYT_GRAVEYARD, // BDG_Loc_KDG, //= bit(18),//Location:Krayt
		// Dragon Graveyard
		EXP_NAB_GUNGAN_SACRED_PLACE, // BDG_Loc_HGSP, //= bit(19),//Location:
		// Hidden Gungan Sacred Place
		EXP_COR_AGRILAT_SWAMP, // BDG_Loc_HACS, //= bit(20),//Location: Heart of
		// the Agrilate Crystal Swamps
		EXP_YAV_TEMPLE_WOOLAMANDER, // BDG_Loc_APW, //= bit(21),//Location:
		// Ancient Palace of Woolamander
		EXP_YAV_TEMPLE_BLUELEAF, // BDG_Loc_MBLT, //= bit(22),//Location:
		// Mysterious Blue Leaf Temple
		EXP_YAV_TEMPLE_EXAR_KUN, // BDG_Loc_HTEK, //= bit(23),//Location: Hidden
		// Temple of Exar K'un
		EXP_LOK_VOLCANO, // BDG_Loc_SMAR, //= bit(24),//Location:Steaming Maw of
		// "Adi's Rest."
		EXP_DAT_TARPIT, // BDG_Loc_HTPD, //= bit(25),//Location:Horrid Tar Pits
		// of Dathomir
		EXP_DAT_SARLACC, // BDG_Loc_LSD, //= bit(26),//Location:Lesser Sarlacc
		// of Dathomir
		EXP_DAT_ESCAPE_POD, // BDG_Loc_AEP, //= bit(27),//Location:Abondoned
		// Escape Pod
		EXP_DAT_MISTY_FALLS_1, // BDG_Loc_LMF, //= bit(28),//Location:Lesser
		// Misty Falls
		EXP_DAT_MISTY_FALLS_2, // BDG_Loc_GMF, //= bit(29),//Location:Greater
		// Misty Falls
		EXP_DAN_JEDI_TEMPLE, // BDG_Loc_RJT, //= bit(30),//Location:Ruined Jedi
		// Temple
		EXP_DAN_REBEL_BASE, // BDG_Loc_ARB, //= bit(31),//Location:Abandoned
		// Rebel Base
		EVENT_PROJECT_DEAD_EYE_1, // BDG_Evt_PDE, //= bit(0),//Event: Project
		// Dead Eye
		ACC_GOOD_SAMARITAN, // BDG_Acc_GS, //= bit(1),//Accolade: Good Samaritan
		ACC_FASCINATING_BACKGROUND, // BDG_Acc_FB, //= bit(2),//Accolate:
		// Fascinating Background
		ACC_BRAVE_SOLDIER, // BDG_Acc_BS, //= bit(3),//Accolate: Brave Soldier
		ACC_INTERESTING_PERSONAGE, // BDG_Acc_IP, //= bit(4),//Accolate:
		// Interesting Personage
		ACC_PROFESSIONAL_DEMEANOR, // BDG_Acc_PD, //= bit(5),//Accolate:
		// Professional Demeanor
		WARREN_COMPASSION, // BDG_Wrn_C, //= bit(6),//Warren: Compassion
		WARREN_HERO, // BDG_Wrn_IH, //= bit(7),//Warren: Imperial Hero
		EVENT_COA2_IMPERIAL, // BDG_Evt_VA, //= bit(8),//Event: Vacca's
		// Allegiance
		EVENT_COA2_REBEL, // BDG_Evt_VA2, //= bit(9),//Event: Vacca's Allegiance
		COMBAT_1HSWORD_MASTER, // BDG_Prf_MF, //= bit(10),//Profession: Master
		// Fencer
		COMBAT_2HSWORD_MASTER, // BDG_Prf_Ms, //= bit(11),//Profession: Master
		// Swordsman
		COMBAT_BOUNTYHUNTER_MASTER, // BDG_Prf_MBH, //= bit(12),//Profession:
		// Master Bounty Hunter
		COMBAT_BRAWLER_MASTER, // BDG_Prf_MB, //= bit(13),//Profession: Master
		// Brawler
		COMBAT_CARBINE_MASTER, // BDG_Prf_MCa, //= bit(14),//Profession: Master
		// Carbineer
		COMBAT_COMMANDO_MASTER, // BDG_Prf_MCo, //= bit(15),//Profession: Master
		// Commando
		COMBAT_MARKSMAN_MASTER, // BDG_Prf_MMa, //= bit(16),//Profession: Master
		// Marksman
		COMBAT_PISTOL_MASTER, // BDG_Prf_MPis, //= bit(17),//Profession: Master
		// Pistoleer
		COMBAT_POLEARM_MASTER, // BDG_Prf_MPik, //= bit(18),//Profession: Master
		// Pikeman
		COMBAT_RIFLEMAN_MASTER, // BDG_Prf_MRi, //= bit(19),//Profession: Master
		// Rifleman
		COMBAT_SMUGGLER_MASTER, // BDG_Prf_MS, //= bit(20),//Profession: Master
		// Smuggler
		COMBAT_UNARMED_MASTER, // BDG_Prf_TKM, //= bit(21),//Profession: Teras
		// Kasi Master
		CRAFTING_ARCHITECT_MASTER, // BDG_Prf_MArc, //= bit(22),//Profession:
		// Master Architect
		CRAFTING_ARMORSMITH_MASTER, // BDG_Prf_MArm, //= bit(23),//Profession:
		// Master Armorsmith
		CRAFTING_ARTISAN_MASTER, // BDG_Prf_MArt, //= bit(24),//Profession:
		// Master Artisan
		CRAFTING_CHEF_MASTER, // BDG_Prf_MCh, //= bit(25),//Profession: Master
		// Chef
		CRAFTING_DROIDENGINEER_MASTER, // BDG_Prf_MDE, //= bit(26),//Profession:
		// Droid Engineer
		CRAFTING_MERCHANT_MASTER, // BDG_Prf_MMer, //= bit(27),//Profession:
		// Master Merchant
		CRAFTING_TAILOR_MASTER, // BDG_Prf_MT, //= bit(28),//Profession: Master
		// Tailor
		CRAFTING_WEAPONSMITH_MASTER, // BDG_Prf_MW, //= bit(29),//Profession:
		// Master Weaponsmith
		OUTDOORS_BIO_ENGINEER_MASTER, // BDG_Prf_MBE, //= bit(30),//Profession:
		// Master Bio-Engineer
		OUTDOORS_CREATUREHANDLER_MASTER, // BDG_Prf_MCH, //=
		// bit(31),//Profession: Master
		// Creature Handler
		OUTDOORS_RANGER_MASTER, // BDG_Prf_MRa, //= bit(0),//Profession: Master
		// Ranger
		OUTDOORS_SCOUT_MASTER, // BDG_Prf_MSc, //= bit(1),//Profession: Master
		// Scout
		OUTDOORS_SQUADLEADER_MASTER, // BDG_Prf_MSL, //= bit(2),//Profession:
		// Master Squad Leader
		SCIENCE_COMBATMEDIC_MASTER, // BDG_Prf_MCM, //= bit(3),//Profession:
		// Master Combat Medic
		SCIENCE_DOCTOR_MASTER, // BDG_Prf_MDo, //= bit(4),//Profession: Master
		// Doctor
		SCIENCE_MEDIC_MASTER, // BDG_Prf_MMed, //= bit(5),//Profession: Master
		// Medic
		SOCIAL_DANCER_MASTER, // BDG_Prf_MDa, //= bit(6),//Profession: Master
		// Dancer
		SOCIAL_ENTERTAINER_MASTER, // BDG_Prf_ME, //= bit(7),//Profession:
		// Master Entertainer
		SOCIAL_IMAGEDESIGNER_MASTER, // BDG_Prf_MID, //= bit(8),//Profession:
		// Master Image Designer
		SOCIAL_MUSICIAN_MASTER, // BDG_Prf_MMu, //= bit(9),//Profession: Master
		// Musician
		SOCIAL_POLITICIAN_MASTER, // BDG_Prf_MP, //= bit(10),//Profession:
		// Master Politician
		BDG_EXP_NAB_THEED_FALLS_BOTTOM, // BDG_Loc_BGFT, //= bit(11),//Location:
		// The bottom of the Great Falls at
		// Theed
		BDG_EXP_NAB_DEEJA_FALLS_TOP, // BDG_Loc_HFDP, //= bit(12),//Location:
		// The head of the falls at Dee'ja Peak
		BDG_EXP_NAB_AMIDALAS_SANDY_BEACH, // BDG_Loc_APB, //=
		// bit(13),//Location: Amidala's
		// private beach
		BDG_EXP_COR_REBEL_HIDEOUT, // BDG_Loc_RHC, //= bit(14),//Location: Rebel
		// Hideout on Corellia
		BDG_EXP_COR_ROGUE_CORSEC_BASE, // BDG_Loc_RCBC, //= bit(15),//Location:
		// Rogue Corsec Base on Corellia
		BDG_EXP_COR_TYRENA_THEATER, // BDG_Loc_OTVI, //= bit(16),//Location:
		// Outdoor theater in Vreni Island
		BDG_EXP_COR_BELA_VISTAL_FOUNTAIN, // BDG_Loc_CFBV, //=
		// bit(17),//Location: Crystal
		// fountian in Belta Vistal
		BDG_EXP_DAT_CRASHED_SHIP, // BDG_Loc_CS, //= bit(18),//Location: Crash
		// Site
		BDG_EXP_DAT_IMP_PRISON, // BDG_Loc_IP, //= bit(19),//Location: Imperial
		// Prision
		BDG_EXP_DAN_DANTARI_VILLAGE1, // BDG_Loc_DaV, //= bit(20),//Location:
		// Dantari Village
		BDG_EXP_DAN_DANTARI_VILLAGE2, // BDG_Loc_DaV2, //= bit(21),//Location:
		// Dantari Village
		BDG_EXP_END_EWOK_TREE_VILLAGE, // BDG_Loc_ETV, //= bit(22),//Location:
		// Ewok Tree Village
		BDG_EXP_END_EWOK_LAKE_VILLAGE, // BDG_Loc_ELV, //= bit(23),//Location:
		// Ewok Lake Village
		BDG_EXP_END_DULOK_VILLAGE, // BDG_Loc_DuV, //= bit(24),//Location: Dulok
		// Village
		BDG_EXP_END_IMP_OUTPOST, // BDG_Loc_MB, //= bit(25),//Location: Marauder
		// Base
		BDG_EXP_TAL_CREATURE_VILLAGE, // BDG_Loc_LVD, //= bit(26),//Location:
		// Lost Village of Durbin
		BDG_EXP_TAL_IMP_BASE, // BDG_Loc_IB, //= bit(27),//Location: Imperial
		// Base
		BDG_EXP_TAL_IMP_VS_REB_BATTLE, // BDG_Loc_SBBRIF, //=
		// bit(28),//Location: The site of a
		// battle between Rebel and Imperial
		// forces
		BDG_EXP_TAL_AQUALISH_CAVE, // BDG_Loc_CIAS, //= bit(29),//Location: A
		// cave inhabited by Aqualish soldiers
		BDG_EXP_ROR_KOBALA_SPICE_MINE, // BDG_Loc_KSM, //= bit(30),//Location:
		// The Kobola Spice Mine
		BDG_EXP_ROR_REBEL_OUTPOST, // BDG_Loc_RO, //= bit(31),//Location: A
		// Rebel Outpost
		BDG_EXP_ROR_IMP_CAMP, // BDG_Loc_IE, //= bit(0),//Location: An Imperial
		// Encampment
		BDG_EXP_ROR_IMP_HYPERDRIVE_FAC, // BDG_Loc_IHF, //= bit(1),//Location:
		// An Imperial Hyperdrive Facility
		BDG_EXP_LOK_IMP_OUTPOST, // BDG_Loc_IO, //= bit(2),//Location: An
		// Imperial Outpost
		BDG_EXP_LOK_KIMOGILA_SKELETON, // BDG_Loc_KS, //= bit(3),//Location: A
		// Kimogila Skeleton
		BDG_EXP_10_BADGES, // BDG_E_10, //= bit(4),//Novice Explorer: 10
		// Exploration Badges
		BDG_EXP_20_BADGES, // BDG_E_20, //= bit(5),//Journeyman Explorer: 20
		// Exploration Badges
		BDG_EXP_30_BADGES, // BDG_E_30, //= bit(6),//Skilled Explorer: 30
		// Exploration Badges
		BDG_EXP_40_BADGES, // BDG_E_40, //= bit(7),//Profession Explorer: 40
		// Exploration Badges
		BDG_EXP_45_BADGES, // BDG_E_45, //= bit(8),//Master Explorer: 45
		// Exploration Badges
		BDG_THM_PARK_JABBA_BADGE, // BDG_JBT, //= bit(9),//Jabba's Badge of
		// Trust: Jabba's Theme Park Completed
		BDG_THM_PARK_IMPERIAL_BADGE, // BDG_IPM, //= bit(10),//Imperial Badge of
		// Merit: Imperial Theme Park Completed
		BDG_THM_PARK_REBEL_BADGE, // BDG_RBC, //= bit(11),//Rebel Badge of
		// Courage: Rebel Theme Park Completed
		BDG_THM_PARK_NYM_BADGE, // BDG_NBH, //= bit(12),//Nym's Badge of Honor:
		// Nym's Theme Park Completed
		EVENT_COA3_IMPERIAL, // BDG_Evt_CAFC, //= bit(13),//Event: Cries of
		// Alderaan: Final Chapter
		EVENT_COA3_REBEL, // BDG_Evt_CAFC2, //= bit(14),//Event: Cries of
		// Alderaan: Final Chapter
		BDG_LIBRARY_TRIVIA, // BDG_TL, //= bit(15),//Trivial Librarian
		BDG_CORVETTE_IMP_DESTROY, // BDG_IHM, //= bit(16),//Imperial Herosim
		// Medal
		BDG_CORVETTE_IMP_RESCUE, // BDG_IVSM, //= bit(17),//Imperial Valorous
		// Services Medal
		BDG_CORVETTE_IMP_ASSASSIN, // BDG_IMH, //= bit(18),//Imperial Medal of
		// Honor
		BDG_CORVETTE_NEUTRAL_DESTROY, // BDG_JBC, //= bit(19),//Jabba's Badge of
		// Courage
		BDG_CORVETTE_NEUTRAL_RESCUE, // BDG_JBV, //= bit(20),//Jabba's Badge of
		// Valor
		BDG_CORVETTE_NEUTRAL_ASSASSIN, // BDG_JBH, //= bit(21),//Jabba's Badge
		// of Heroism
		BDG_CORVETTE_REB_DESTROY, // BDG_AHM, //= bit(22),//Alliance Heroism
		// Medal
		BDG_CORVETTE_REB_RESCUE, // BDG_AVSM, //= bit(23),//Alliance Valorous
		// Services Medal
		BDG_CORVETTE_REB_ASSASSIN, // BDG_AMH, //= bit(24),//Alliance Medal of
		// Honor
		BDG_RACING_AGRILAT_SWAMP, // BDG_Rac_AWTC, //= bit(25),//Racing: Agrilat
		// Swamp Track Champion
		BDG_RACING_KEREN_CITY, // BDG_Rac_KCTC, //= bit(26),//Racing: Keren City
		// Track Champion
		BDG_RACING_MOS_ESPA, // BDG_Rac_METC, //= bit(27),//Racing: Mos Espa
		// Track Champion
		BDG_ACCOLADE_LIVE_EVENT, // BDG_Acc_LEM, //= bit(28),//Accolade: Live
		// Even Medal
		BDG_RACING_LOK_MARATHON, // BDG_Rac_LMC, //= bit(29),//Racing: Lok
		// Marathon Champion
		BDG_RACING_NARMLE_MEMORIAL, // BDG_Rac_NMRC, //= bit(30),//Racing:
		// Narmle Memorial Rally Champion
		BDG_RACING_NASHAL_RIVER, // BDG_Rac_NRRTC, //= bit(31),//Racing: Nashal
		// River Race Track Champion
		DESTROY_DEATHSTAR, // BDG_Evt_ANH, //= bit(0),//Event: A New Hope:
		// Destroyed the Death Star//0x01
		CRAFTING_SHIPWRIGHT, // BDG_Prf_MSh, //= bit(1),//Profession: Master
		// Shipwright//0x02
		PILOT_REBEL_NAVY_NABOO, // BDG_Prf_VAP, //= bit(2),//Profession: Vortex
		// Ace Pilot//0x04
		PILOT_REBEL_NAVY_CORELLIA, // BDG_Prf_AHSAP, //= bit(3),//Profession:
		// Arkon's Havoc Squadron Ace Pilot//0x08
		PILOT_REBEL_NAVY_TATOOINE, // BDG_Prf_CPAP, //= bit(4),//Profession:
		// Crimson Phoenix Ace Pilot//0x010
		PILOT_IMPERIAL_NAVY_NABOO, // BDG_Prf_IIAP, //= bit(5),//Profession:
		// Imperial Inquisition Ace Pilot//0x020
		PILOT_IMPERIAL_NAVY_CORELLIA, // BDG_Prf_BEAP, //= bit(6),//Profession:
		// Black Epsilon Ace Pilot//0x040
		PILOT_IMPERIAL_NAVY_TATOOINE, // BDG_Prf_SSAP, //= bit(7),//Profession:
		// Storm Squadron Ace Pilot//0x080
		PILOT_NEUTRAL_NABOO, // BDG_Prf_RSFAP, //= bit(8),//Profession: Royal
		// Security Forces Ace Pilot//0x0100
		PILOT_NEUTRAL_CORELLIA, // BDG_Prf_CSFAP, //= bit(9),//Profession:
		// Corellian Security Forces Ace Pilot//0x0200
		PILOT_NEUTRAL_TATOOINE, // BDG_Prf_SAAP, //= bit(10),//Profession:
		// Smugglers Alliance Ace Pilot//0x0400
		BDG_ACCOLADE_HOME_SHOW, // BDG_Acc_HSW, //= bit(11),//Accolade: Galactic
		// Home Show Winner//0x0800
		UNUSED0, // BDG_None_0, //= bit(12),//You have not won any badges
		UNUSED1, // BDG_None_1, //= bit(13),//You have not won any badges
		UNUSED2, // BDG_None_2, //= bit(14),//You have not won any badges
		UNUSED3, // BDG_None_3, //= bit(15),//You have not won any badges
		UNUSED4, // BDG_None_4, //= bit(16),//You have not won any badges
		UNUSED5, // BDG_None_5, //= bit(17),//You have not won any badges
		UNUSED6, // BDG_None_6, //= bit(18),//You have not won any badges
		UNUSED7, // BDG_None_7, //= bit(19),//You have not won any badges
		UNUSED8, // BDG_None_8, //= bit(20),//You have not won any badges
		UNUSED9, // BDG_None_9, //= bit(21),//You have not won any badges
		UNUSED10, // BDG_None_10, //= bit(22),//You have not won any badges
		UNUSED11, // BDG_None_11, //= bit(23),//You have not won any badges
		UNUSED12, // BDG_None_12, //= bit(24),//You have not won any badges
		UNUSED13, // BDG_None_13, //= bit(25),//You have not won any badges
		UNUSED14, // BDG_None_14, //= bit(26),//You have not won any badges
		UNUSED15, // BDG_None_15, //= bit(27),//You have not won any badges
		UNUSED16, // BDG_None_16, //= bit(28),//You have not won any badges
		UNUSED17, // BDG_None_17, //= bit(29),//You have not won any badges
		UNUSED18, // BDG_None_18, //= bit(30),//You have not won any badges
		UNUSED19, // BDG_None_19, //= bit(31),//You have not won any badges
	};

	protected final static String[] BADGE_STF_STRINGS = { "count_5",
			"count_10", "count_25", "count_50", "count_75", "count_100",
			"count_125", "poi_rabidbeast", "poi_prisonbreak", "poi_twoliars",
			"poi_factoryliberation", "poi_heromark", "exp_tat_bens_hut",
			"exp_tat_tusken_pool", "exp_tat_krayt_skeleton",
			"exp_tat_escape_pod", "exp_tat_sarlacc_pit",
			"exp_tat_lars_homestead", "exp_tat_krayt_graveyard",
			"exp_nab_gungan_sacred_place", "exp_cor_agrilat_swamp",
			"exp_yav_temple_woolamander", "exp_yav_temple_blueleaf",
			"exp_yav_temple_exar_kun", "exp_lok_volcano", "exp_dat_tarpit",
			"exp_dat_sarlacc", "exp_dat_escape_pod", "exp_dat_misty_falls_1",
			"exp_dat_misty_falls_2", "exp_dan_jedi_temple",
			"exp_dan_rebel_base", "event_project_dead_eye_1",
			"acc_good_samaritan", "acc_fascinating_background",
			"acc_brave_soldier", "acc_interesting_personage",
			"acc_professional_demeanor", "warren_compassion", "warren_hero",
			"event_coa2_imperial", "event_coa2_rebel", "combat_1hsword_master",
			"combat_2hsword_master", "combat_bountyhunter_master",
			"combat_brawler_master", "combat_carbine_master",
			"combat_commando_master", "combat_marksman_master",
			"combat_pistol_master", "combat_polearm_master",
			"combat_rifleman_master", "combat_smuggler_master",
			"combat_unarmed_master", "crafting_architect_master",
			"crafting_armorsmith_master", "crafting_artisan_master",
			"crafting_chef_master", "crafting_droidengineer_master",
			"crafting_merchant_master", "crafting_tailor_master",
			"crafting_weaponsmith_master", "outdoors_bio_engineer_master",
			"outdoors_creaturehandler_master", "outdoors_ranger_master",
			"outdoors_scout_master", "outdoors_squadleader_master",
			"science_combatmedic_master", "science_doctor_master",
			"science_medic_master", "social_dancer_master",
			"social_entertainer_master", "social_imagedesigner_master",
			"social_musician_master", "social_politician_master",
			"bdg_exp_nab_theed_falls_bottom", "bdg_exp_nab_deeja_falls_top",
			"bdg_exp_nab_amidalas_sandy_beach", "bdg_exp_cor_rebel_hideout",
			"bdg_exp_cor_rogue_corsec_base", "bdg_exp_cor_tyrena_theater",
			"bdg_exp_cor_bela_vistal_fountain", "bdg_exp_dat_crashed_ship",
			"bdg_exp_dat_imp_prison", "bdg_exp_dan_dantari_village1",
			"bdg_exp_dan_dantari_village2", "bdg_exp_end_ewok_tree_village",
			"bdg_exp_end_ewok_lake_village", "bdg_exp_end_dulok_village",
			"bdg_exp_end_imp_outpost", "bdg_exp_tal_creature_village",
			"bdg_exp_tal_imp_base", "bdg_exp_tal_imp_vs_reb_battle",
			"bdg_exp_tal_aqualish_cave", "bdg_exp_ror_kobala_spice_mine",
			"bdg_exp_ror_rebel_outpost", "bdg_exp_ror_imp_camp",
			"bdg_exp_ror_imp_hyperdrive_fac", "bdg_exp_lok_imp_outpost",
			"bdg_exp_lok_kimogila_skeleton", "bdg_exp_10_badges",
			"bdg_exp_20_badges", "bdg_exp_30_badges", "bdg_exp_40_badges",
			"bdg_exp_45_badges", "bdg_thm_park_jabba_badge",
			"bdg_thm_park_imperial_badge", "bdg_thm_park_rebel_badge",
			"bdg_thm_park_nym_badge", "event_coa3_imperial",
			"event_coa3_rebel", "bdg_library_trivia",
			"bdg_corvette_imp_destroy", "bdg_corvette_imp_rescue",
			"bdg_corvette_imp_assassin", "bdg_corvette_neutral_destroy",
			"bdg_corvette_neutral_rescue", "bdg_corvette_neutral_assassin",
			"bdg_corvette_reb_destroy", "bdg_corvette_reb_rescue",
			"bdg_corvette_reb_assassin", "bdg_racing_agrilat_swamp",
			"bdg_racing_keren_city", "bdg_racing_mos_espa",
			"bdg_accolade_live_event", "bdg_racing_lok_marathon",
			"bdg_racing_narmle_memorial", "bdg_racing_nashal_river",
			"destroy_deathstar", "crafting_shipwright",
			"pilot_rebel_navy_naboo", "pilot_rebel_navy_corellia",
			"pilot_rebel_navy_tatooine", "pilot_imperial_navy_naboo",
			"pilot_imperial_navy_corellia", "pilot_imperial_navy_tatooine",
			"pilot_neutral_naboo", "pilot_neutral_corellia",
			"pilot_neutral_tatooine", "bdg_accolade_home_show", "unused",
			"unused", "unused", "unused", "unused", "unused", "unused",
			"unused", "unused", "unused", "unused", "unused", "unused",
			"unused", "unused", "unused", "unused", "unused", "unused",
			"unused",

	};

	/**
	 * @logout 1 time_left You have %DI seconds left until you may log out
	 *         safely. 2 safe_to_log_out You may now log out safely. 3 aborted
	 *         Your attempt to log out safely has been aborted. 4
	 *         must_be_sitting You must be sitting in order to log out safely.
	 */

	/**
	 * Determines the range a packet will be sent to. a Value with an X excludes
	 * the player
	 */
	protected static byte PACKET_RANGE_GROUP = 0x01;
	protected static byte PACKET_RANGE_GROUP_EXCLUDE_SENDER = 0x02;
	protected static byte PACKET_RANGE_CHAT_RANGE = 0x03;
	protected static byte PACKET_RANGE_CHAT_RANGE_EXCLUDE_SENDER = 0x04;
	protected static byte PACKET_RANGE_PLANET = 0x05;
	protected static byte PACKET_RANGE_PLANET_EXCLUDE_SENDER = 0x06;
	protected static byte PACKET_RANGE_SERVER = 0x07;
	protected static byte PACKET_RANGE_SERVER_EXCLUDE_SENDER = 0x08;
	protected static byte PACKET_RANGE_CLUSTER = 0x09;
	protected static byte PACKET_RANGE_CLUSTER_EXCLUDE_SENDER = 0x0A;
	protected static byte PACKET_RANGE_INSTANCE = 0x0B;
	protected static byte PACKET_RANGE_INSTANCE_EXCLUDE_SENDER = 0x0C;

	// TODO -- Bitmask!!!
	/**
	 * used to identify the radial condition for an item. based on codition is
	 * the radial options presented to the player when the item is used. Item
	 * condition will vary by item type or special conditions depending on item.
	 * I/E a Tangible item may be dropped indoors. but not outdoors, so indoors
	 * there has to be a radial option for drop, but the option for radial drop
	 * may be the same as an outdoor option this causes that 2 buttons now have
	 * the same number. to solve this we issue a condition in the database and
	 * compare the radial condition at time of getting radials and return the
	 * radials based on condition of the item. NOTE: This has nothing to do with
	 * the condition of the item being usable or not or havng a conditions of
	 * 1000/1000 or 0/1000 this is not the items Condition it is the Radial
	 * Condition for presentation.
	 */
	protected static enum RADIAL_CONDITION {
		NORMAL, 
		TANGIBLE_ITEM_INDOORS, 
		INTANGIBLE_VEHICLE_SPAWNED, 
		VEHICLE_GARAGE_RANGE, 
		ITEM_DROPPED_IN_CELL, 
		CAMP_TERMINAL_NORMAL, 
		PET_CONDITION_CALLED, 
		CREATURE, 
		CREATURE_TAMEABLE, 
		CREATURE_MILKABLE, 
		CREATURE_DEAD,
	};

	protected static String[] RADIAL_CONDITION_STR = { "NORMAL",// 0
			"TANGIBLE_ITEM_INDOORS",// 1
			"INTANGIBLE_VEHICLE_SPAWNED",// 2
			"VEHICLE_GARAGE_RANGE",// 3
			"ITEM_DROPPED_IN_CELL",// 4
			"CAMP_TERMINAL_NORMAL",// 5
			"PET_CONDITION_CALLED",// 6
			"CREATURE",
			"TAMEABLE CREATURE",
			"MILKABLE_CREATURE",
			"DEAD_CREATURE",
	};

	/*
	 * not in use protected static int[] TERMINAL_TEMPLATES = {
	 * 8204,//'object/tangible/beta/shared_beta_terminal_food.iff'
	 * 8205,//'object/tangible/beta/shared_beta_terminal_medicine.iff'
	 * 8206,//'object/tangible/beta/shared_beta_terminal_money.iff'
	 * 8207,//'object/tangible/beta/shared_beta_terminal_resource.iff'
	 * 8208,//'object/tangible/beta/shared_beta_terminal_warp.iff'
	 * 8209,//'object/tangible/beta/shared_beta_terminal_wound.iff'
	 * 8210,//'object/tangible/beta/shared_beta_terminal_xp.iff'
	 * 8211,//'object/tangible/beta/shared_donham_terminal.iff'9354,//
	 * 'object/tangible/dungeon/death_watch_bunker/shared_door_control_terminal.iff'
	 * 9372,//'object/tangible/dungeon/shared_keypad_terminal.iff'
	 * 9375,//'object/tangible/dungeon/shared_terminal_free_s1.iff'
	 * 9377,//'object/tangible/dungeon/shared_wall_terminal_s1.iff'
	 * 9378,//'object/tangible/dungeon/shared_wall_terminal_s2.iff'
	 * 9379,//'object/tangible/dungeon/shared_wall_terminal_s3.iff'
	 * 9380,//'object/tangible/dungeon/shared_wall_terminal_s4.iff'
	 * 10217,//'object/tangible/hq_destructible/shared_override_terminal.iff'
	 * 10219,//'object/tangible/hq_destructible/shared_security_terminal.iff'
	 * 10220,//'object/tangible/hq_destructible/shared_uplink_terminal.iff'
	 * 10263,//
	 * 'object/tangible/item/quest/force_sensitive/shared_fs_craft_puzzle_terminal.iff'
	 * 10344,//'object/tangible/lair/base/shared_objective_data_terminal.iff'
	 * 10346,//'object/tangible/lair/base/shared_objective_main_terminal.iff'
	 * 11904,//'object/tangible/mission/shared_mission_terminal.iff'
	 * 14046,//'object/tangible/terminal/base/shared_base_terminal.iff'
	 * 14047,//'object/tangible/terminal/shared_terminal_ballot_box.iff'
	 * 14048,//'object/tangible/terminal/shared_terminal_bank.iff'
	 * 14049,//'object/tangible/terminal/shared_terminal_bazaar.iff'
	 * 14050,//'object/tangible/terminal/shared_terminal_bestine_quests_01.iff'
	 * 14051,//'object/tangible/terminal/shared_terminal_bestine_quests_02.iff'
	 * 14052,//'object/tangible/terminal/shared_terminal_bestine_quests_03.iff'
	 * 14053,//'object/tangible/terminal/shared_terminal_bounty_droid.iff'
	 * 14054,//'object/tangible/terminal/shared_terminal_character_builder.iff'
	 * 14055,//'object/tangible/terminal/shared_terminal_city.iff'
	 * 14056,//'object/tangible/terminal/shared_terminal_city_vote.iff'
	 * 14057,//'object/tangible/terminal/shared_terminal_cloning.iff'
	 * 14058,//'object/tangible/terminal/shared_terminal_command_console.iff'
	 * 14059
	 * ,//'object/tangible/terminal/shared_terminal_dark_enclave_challenge.iff'
	 * 14060
	 * ,//'object/tangible/terminal/shared_terminal_dark_enclave_voting.iff'
	 * 14061,//'object/tangible/terminal/shared_terminal_elevator.iff'
	 * 14062,//'object/tangible/terminal/shared_terminal_elevator_down.iff'
	 * 14063,//'object/tangible/terminal/shared_terminal_elevator_up.iff'
	 * 14064,//'object/tangible/terminal/shared_terminal_geo_bunker.iff'
	 * 14065,//'object/tangible/terminal/shared_terminal_guild.iff'
	 * 14066,//'object/tangible/terminal/shared_terminal_hq.iff'
	 * 14067,//'object/tangible/terminal/shared_terminal_hq_imperial.iff'
	 * 14068,//'object/tangible/terminal/shared_terminal_hq_rebel.iff'
	 * 14069,//'object/tangible/terminal/shared_terminal_hq_turret_control.iff'
	 * 14070,//'object/tangible/terminal/shared_terminal_imagedesign.iff'
	 * 14071,//'object/tangible/terminal/shared_terminal_insurance.iff'
	 * 14072,//'object/tangible/terminal/shared_terminal_jukebox.iff'14073,//
	 * 'object/tangible/terminal/shared_terminal_light_enclave_challenge.iff'
	 * 14074
	 * ,//'object/tangible/terminal/shared_terminal_light_enclave_voting.iff'
	 * 14075,//'object/tangible/terminal/shared_terminal_mission.iff'
	 * 14076,//'object/tangible/terminal/shared_terminal_mission_artisan.iff'
	 * 14077,//'object/tangible/terminal/shared_terminal_mission_bounty.iff'
	 * 14078
	 * ,//'object/tangible/terminal/shared_terminal_mission_entertainer.iff'
	 * 14079,//'object/tangible/terminal/shared_terminal_mission_imperial.iff'
	 * 14080,//'object/tangible/terminal/shared_terminal_mission_newbie.iff'
	 * 14081,//'object/tangible/terminal/shared_terminal_mission_rebel.iff'
	 * 14082,//'object/tangible/terminal/shared_terminal_mission_scout.iff'
	 * 14083,//'object/tangible/terminal/shared_terminal_mission_statue.iff'
	 * 14084,//'object/tangible/terminal/shared_terminal_newbie_clothing.iff'
	 * 14085,//'object/tangible/terminal/shared_terminal_newbie_food.iff'
	 * 14086,//'object/tangible/terminal/shared_terminal_newbie_instrument.iff'
	 * 14087,//'object/tangible/terminal/shared_terminal_newbie_medicine.iff'
	 * 14088,//'object/tangible/terminal/shared_terminal_newbie_tool.iff'
	 * 14089,//'object/tangible/terminal/shared_terminal_nym_cave.iff'
	 * 14090,//'object/tangible/terminal/shared_terminal_player_structure.iff'
	 * 14091
	 * ,//'object/tangible/terminal/shared_terminal_player_structure_nosnap.iff'
	 * 14092,//
	 * 'object/tangible/terminal/shared_terminal_player_structure_nosnap_mini.iff'
	 * 14093,//'object/tangible/terminal/shared_terminal_pm_register.iff'
	 * 14094,//'object/tangible/terminal/shared_terminal_pob_ship.iff'14095,//
	 * 'object/tangible/terminal/shared_terminal_ship_interior_security_1.iff'
	 * 14096,//'object/tangible/terminal/shared_terminal_shipping.iff'
	 * 14098,//'object/tangible/terminal/shared_terminal_space.iff'
	 * 14099,//'object/tangible/terminal/shared_terminal_travel.iff'
	 * 14100,//'object/tangible/terminal/shared_terminal_water_pressure.iff'
	 * 14101,//'object/tangible/terminal/shared_test.iff'
	 * 14107,//'object/tangible/test/shared_test_terminal.iff'
	 * 14154,//'object/tangible/vendor/shared_vendor_terminal_basic.iff'
	 * 14155,//'object/tangible/vendor/shared_vendor_terminal_bulky.iff'
	 * 14156,//'object/tangible/vendor/shared_vendor_terminal_fancy.iff'
	 * 14157,//'object/tangible/vendor/shared_vendor_terminal_slim.iff'
	 * 14158,//'object/tangible/vendor/shared_vendor_terminal_small.iff'
	 * 14159,//'object/tangible/vendor/shared_vendor_terminal_standard.iff'
	 * 14170,//'object/tangible/veteran_reward/shared_data_terminal_s1.iff'
	 * 14171,//'object/tangible/veteran_reward/shared_data_terminal_s2.iff'
	 * 14172,//'object/tangible/veteran_reward/shared_data_terminal_s3.iff'
	 * 14173,//'object/tangible/veteran_reward/shared_data_terminal_s4.iff' };
	 */

	/**
	 * mission related constants
	 */
	protected static int MAX_MISSION_BAG_ITEMS = 15;

	protected static String[] BH_MISSION_ORIGINATOR_NAMES = {
			"Bounty Hunters' Guild", "Imperial Ubiqtorate",
			"Moff Krewe of Droma Sector", "Vice Admiral Thrawn", "Lord Vader",
			"Captain Nob Finial", "High Inquisitor Mal Sikander",
			"Imperial Customs",
			"Imperial Security Bureau, Planetary Headquarters",
			"Imperial Intelligence, Planetary Headquarters",
			"Imperial Army, Planetary Headquarters", "Imperial Naval Command",
			"Imperial Information Center", "COMPNOR Central Office",
			"Imperial Royal Guard", "Imperial Intelligence Ubiqtorate",
			"Moff Haille", "The Inquisitorius",
			"Benelex Bounty Hunters' Guild", "Neuvalis Bounty Hunters' Guild",
			"Salaktori Hunters' Guild", "Mantis Bounty Hunter Syndicate",
			"House Tresario Bounty Hunters' Guild",
			"Bib Fortuna, Lord Jabba's Majordomo", };

	protected static int CELL_OBJECT_TEMPLATE_ID = 15393;
	protected static byte STRUCTURE_TYPE_BUILDING = 0x01;
	protected static byte STRUCTURE_TYPE_INSTALLATION = 0x02;
	protected static byte STRUCTURE_TYPE_TUTORIAL = 0x03;
	protected static byte STRUCTURE_TYPE_HARVESTER = 0x04;
	protected static byte STRUCTURE_TYPE_CAMP = 0x05;

	protected static byte DELAYED_SPAWN_ACTION_SPAWN = 0x01;
	protected static byte DELAYED_SPAWN_ACTION_DESPAWN = 0x02;
	protected static byte DELAYED_SPAWN_ACTION_CYCLE = 0x03;
	protected static byte DELAYED_SPAWN_ACTION_RAW_SPAWN = 0x04;

	protected static short STRUCTURE_PERMISSIONS_DENY = 0;
	protected static short STRUCTURE_PERMISSIONS_PRIVATE = 1;
	protected static short STRUCTURE_PERMISSIONS_PUBLIC = 2;

	protected static long SERVER_SYSTEM_OBJECT_ID = 0;
	protected static long SERVER_STRUCTURE_MANAGER_OBJECT_ID = 19999999;

	protected static String[] RESOURCE_NAME_PREFIX = { "Re", "Iso", "Nab",
			"Or", "Ved", "Pos", "Xe", "Ex", "Sti", "Ir", "Ake", "Geh", "Fig",
			"Ie", "Ce", "Ia", "Tip", "Tri", "Rel", "Xio", "Che", "Fa", "Po",
			"Pae", "Doi", "Wa", "Rup", "Ovi", "Ove", "Tae", "Nao", "Lao",
			"Lin", "Lex", "Loex", "Paex", "Bib", "Jaw", "Xat", "Lape", "Su",
			"Sae", "Epo", "Vix", "Vu", "Ke", "Kel", "Koi", "Cix", "Pal", "Pla",
			"Sor", "Lak", "Lek", "Waf", "Weg", "Ga", "Gix", "Gox", "Nai", "Su",
			"Aa", "Ba", "Ca", "Da", "Ea", "Fa", "Ga", "Ha", "Ia", "Ja", "Ka",
			"La", "Ma", "Na", "Oa", "Pa", "Qa", "Ra", "Sa", "Ta", "Ua", "Va",
			"Wa", "Xa", "Ya", "Za", "Ae", "Be", "Ce", "De", "Ee", "Fe", "Ge",
			"He", "Ie", "Je", "Ke", "Le", "Me", "Ne", "Oe", "Pe", "Qe", "Re",
			"Se", "Te", "Ue", "Ve", "We", "Xe", "Ye", "Ze", "Ai", "Bi", "Ci",
			"Di", "Ei", "Fi", "Gi", "Hi", "Ii", "Ji", "Ki", "Li", "Mi", "Ni",
			"Oi", "Pi", "Qi", "Ri", "Si", "Ti", "Ui", "Vi", "Wi", "Xi", "Yi",
			"Zi", "Ao", "Bo", "Co", "Do", "Eo", "Fo", "Go", "Ho", "Io", "Jo",
			"Ko", "Lo", "Mo", "No", "Oo", "Po", "Qo", "Ro", "So", "To", "Uo",
			"Vo", "Wo", "Xo", "Yo", "Zo", "Au", "Bu", "Cu", "Du", "Eu", "Fu",
			"Gu", "Hu", "Iu", "Ju", "Ku", "Lu", "Mu", "Nu", "Ou", "Pu", "Qu",
			"Ru", "Su", "Tu", "Uu", "Vu", "Wu", "Xu", "Yu", "Zu", };

	protected static String[] RESOURCE_NAME_SUFFIX_A = { "aa", "ba", "ca",
			"da", "ea", "fa", "ga", "ha", "ia", "ja", "ka", "la", "ma", "na",
			"oa", "pa", "qa", "ra", "sa", "ta", "ua", "va", "wa", "xa", "ya",
			"za", "ae", "be", "ce", "de", "ee", "fe", "ge", "he", "ie", "je",
			"ke", "le", "me", "ne", "oe", "pe", "qe", "re", "se", "te", "ue",
			"ve", "we", "xe", "ye", "ze", "ai", "bi", "ci", "di", "ei", "fi",
			"gi", "hi", "ii", "ji", "ki", "li", "mi", "ni", "oi", "pi", "qi",
			"ri", "si", "ti", "ui", "vi", "wi", "xi", "yi", "zi", "ao", "bo",
			"co", "do", "eo", "fo", "go", "ho", "io", "jo", "ko", "lo", "mo",
			"no", "oo", "po", "qo", "ro", "so", "to", "uo", "vo", "wo", "xo",
			"yo", "zo", "au", "bu", "cu", "du", "eu", "fu", "gu", "hu", "iu",
			"ju", "ku", "lu", "mu", "nu", "ou", "pu", "qu", "ru", "su", "tu",
			"uu", "vu", "wu", "xu", "yu", "zu", };

	protected static String[] RESOURCE_NAME_SUFFIX_B = { "aza", "bya", "cxa",
			"dwa", "eva", "fua", "gta", "hsa", "ira", "jqa", "kpa", "loa",
			"mna", "nma", "ola", "pka", "qja", "ria", "sha", "tga", "ufa",
			"vea", "wda", "xca", "yba", "zaa", "aze", "bye", "cxe", "dwe",
			"eve", "fue", "gte", "hse", "ire", "jqe", "kpe", "loe", "mne",
			"nme", "ole", "pke", "qje", "rie", "she", "tge", "ufe", "vee",
			"wde", "xce", "ybe", "zae", "azi", "byi", "cxi", "dwi", "evi",
			"fui", "gti", "hsi", "iri", "jqi", "kpi", "loi", "mni", "nmi",
			"oli", "pki", "qji", "rii", "shi", "tgi", "ufi", "vei", "wdi",
			"xci", "ybi", "zai", "azo", "byo", "cxo", "dwo", "evo", "fuo",
			"gto", "hso", "iro", "jqo", "kpo", "loo", "mno", "nmo", "olo",
			"pko", "qjo", "rio", "sho", "tgo", "ufo", "veo", "wdo", "xco",
			"ybo", "zao", "azu", "byu", "cxu", "dwu", "evu", "fuu", "gtu",
			"hsu", "iru", "jqu", "kpu", "lou", "mnu", "nmu", "olu", "pku",
			"qju", "riu", "shu", "tgu", "ufu", "veu", "wdu", "xcu", "ybu",
			"zau",

	};

	protected final static byte CRAFTING_COMPONENT_REQUIRED = 4;
	protected final static byte CRAFTING_COMPONENT_OPTIONAL_NON_TANGIBLE = 5;
	protected final static byte CRAFTING_COMPONENT_OPTIONAL_TANGIBLE_ITEM = 6;

	protected final static int CRAFTING_STAGE_NOT_CRAFTING = 0;
	protected final static int CRAFTING_STAGE_SELECT_DRAFT_SCHEMATIC = 1;
	protected final static int CRAFTING_STAGE_INSERT_COMPONENTS = 2;
	protected final static int CRAFTING_STAGE_EXPERIMENT_CREATE_MANUFACTURE_SCHEMATIC_OR_CREATE_PROTOTYPE = 3;
	protected final static int CRAFTING_STAGE_SET_CUSTOMIZATION_DATA = 4;

	protected final static float ELEVATOR_UP_DOWN_AMOUNT = 11.3f;
	protected final static String ELEVATOR_DOWN_EFFECT = "clienteffect/elevator_descend.cef";
	protected final static String ELEVATOR_UP_EFFECT = "clienteffect/elevator_rise.cef";

	protected final static int SKILLMOD_CAP = 125; // Anything above this is
	// ignored.
	protected final static int HAM_INDEX_HEALTH = 0;
	protected final static int HAM_INDEX_STRENGTH = 1;
	protected final static int HAM_INDEX_CONSTITUTION = 2;
	protected final static int HAM_INDEX_ACTION = 3;
	protected final static int HAM_INDEX_QUICKNESS = 4;
	protected final static int HAM_INDEX_STAMINA = 5;
	protected final static int HAM_INDEX_MIND = 6;
	protected final static int HAM_INDEX_FOCUS = 7;
	protected final static int HAM_INDEX_WILLPOWER = 8;

	protected final static String[] HAM_NAMES = { "Health", "Strenght",
			"Constitution", "Action", "Quickness", "Stamina", "Mind", "Focus",
			"Willpower", };

	protected final static int TRADE_STATUS_ASKING_FOR_CONSENT = 1;
	protected final static int TRADE_STATUS_RECIPIENT_CONSENT = 2;
	protected final static int TRADE_STATUS_RECIPIENT_DECLINE = 3;

	protected final static long BASE_HEALING_ACTION_COOLDOWN_MS = 30000;
	protected final static long HEALING_ACTION_DECAY_PER_SKILLMOD_POINT = 50;

	public final static byte Group_DeleteMember = 0;
	public final static byte Group_AddMember = 1;
	public final static byte Group_GroupLeader = 2;
	public final static byte Group_ResetGroup = 3;
	public final static byte Group_ClearList = 4;

	public final static byte GROUP_REMOVE_REASON_LEAVE = 0x01;
	public final static byte GROUP_REMOVE_REASON_KICK = 0x02;

	protected final static byte DAMAGE_TYPE_KINETIC = 1;
	protected final static byte DAMAGE_TYPE_ENERGY = 2;
	protected final static byte DAMAGE_TYPE_STUN = 3;
	protected final static byte DAMAGE_TYPE_BLAST = 4;
	protected final static byte DAMAGE_TYPE_ELECTRICAL = 5;
	protected final static byte DAMAGE_TYPE_ACID = 6;
	protected final static byte DAMAGE_TYPE_HEAT = 7;
	protected final static byte DAMAGE_TYPE_COLD = 8;

	protected final static byte DAMAGE_PIERCING_NONE = 0;
	protected final static byte DAMAGE_PIERCING_LIGHT = 1;
	protected final static byte DAMAGE_PIERCING_MEDIUM = 2;
	protected final static byte DAMAGE_PIERCING_HEAVY = 3;

	// NOTE: These values are temporary values, put in place as proof of concept
	// for the combat engine.
	// None = 1.0
	// Light = 1.25
	// Medium = 1.5
	// Heavy = 1.75
	protected final static long[] DAMAGE_MULTIPLIERS_KK = new long[4];
	protected final static long[] DAMAGE_MULTIPLIERS_K = new long[4];
	static {
		DAMAGE_MULTIPLIERS_KK[DAMAGE_PIERCING_NONE] = PacketUtils.toKK(1);
		DAMAGE_MULTIPLIERS_KK[DAMAGE_PIERCING_LIGHT] = PacketUtils.toKK(5) / 4;
		DAMAGE_MULTIPLIERS_KK[DAMAGE_PIERCING_MEDIUM] = PacketUtils.toKK(3) / 2;
		DAMAGE_MULTIPLIERS_KK[DAMAGE_PIERCING_HEAVY] = PacketUtils.toKK(7) / 4;
		DAMAGE_MULTIPLIERS_K[DAMAGE_PIERCING_NONE] = PacketUtils
				.unK(DAMAGE_MULTIPLIERS_KK[DAMAGE_PIERCING_NONE]);
		DAMAGE_MULTIPLIERS_K[DAMAGE_PIERCING_LIGHT] = PacketUtils
				.unK(DAMAGE_MULTIPLIERS_KK[DAMAGE_PIERCING_LIGHT]);
		DAMAGE_MULTIPLIERS_K[DAMAGE_PIERCING_MEDIUM] = PacketUtils
				.unK(DAMAGE_MULTIPLIERS_KK[DAMAGE_PIERCING_MEDIUM]);
		DAMAGE_MULTIPLIERS_K[DAMAGE_PIERCING_HEAVY] = PacketUtils
				.unK(DAMAGE_MULTIPLIERS_KK[DAMAGE_PIERCING_HEAVY]);
	}

	protected static int getActualDamage(int baseDamageApplied,
			byte weaponArmorPiercingValue, byte armorPiercingEfectiveness) {
		return (int) PacketUtils
				.unKRound(((baseDamageApplied * DAMAGE_MULTIPLIERS_KK[weaponArmorPiercingValue]) / DAMAGE_MULTIPLIERS_K[armorPiercingEfectiveness]));
	}

	public static final int PLAY_MUSIC_FILE_LOOP = 0;
	public static final int PLAY_MUSIC_FILE_ONCE = 1;

	public final static int NUM_WEAPON_ATTRIBUTES = 14;

	public final static byte WEAPON_TYPE = 0;
	public final static byte WEAPON_DAMAGE_PIERCING = 1;
	public final static byte WEAPON_DAMAGE_TYPE = 2;

	public final static byte WEAPON_ATTACK_RANGES = 3;
	public final static byte WEAPON_DAMAGE_AOE_RADIUS = 4;
	public final static byte WEAPON_MIN_DAMAGE_RANGE = 5;
	public final static byte WEAPON_MAX_DAMAGE_RANGE = 6;
	public final static byte WEAPON_ATTACK_SPEED_RANGE = 7;
	public final static byte WEAPON_RANGE_TO_HIT_MODS = 8;
	public final static byte WEAPON_TO_WOUND_RANGE = 9;
	public final static byte WEAPON_SAC_HEALTH_RANGE = 10;
	public final static byte WEAPON_SAC_ACTION_RANGE = 11;
	public final static byte WEAPON_SAC_MIND_RANGE = 12;

	public final static byte WEAPON_TYPE_ONE_HANDED_SWORD = 1;
	public final static byte WEAPON_TYPE_TWO_HANDED_SWORD = 2;
	public final static byte WEAPON_TYPE_POLEARM = 3;
	public final static byte WEAPON_TYPE_UNARMED = 4;
	public final static byte WEAPON_TYPE_CARBINE = 5;
	public final static byte WEAPON_TYPE_PISTOL = 6;
	public final static byte WEAPON_TYPE_RIFLE = 7;
	public final static byte WEAPON_TYPE_THROWN = 8;
	public final static byte WEAPON_TYPE_HEAVY_COMMANDO = 9;
	public final static byte WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED = 10;
	public final static byte WEAPON_TYPE_JEDI_LIGHTSABER_TWO_HANDED = 11;
	public final static byte WEAPON_TYPE_JEDI_LIGHTSABER_POLEARM = 12;

	public final static String[] CRAFT_TYPE_STRINGS = { "Weapon,1",// 1
			"Armor,2",// 2
			"Food,4",// 4
			"Clothing,8",// 8
			"Vehicle,16",// 16
			"Droid,32",// 32
			"Chemical,64",// 64
			"Tissues,128",// 128
			"Creatures,256",// 256
			"Furniture,512",// 512
			"Installation,1024",// 1024
			"Lightsaber,2048",// 2048
			"Generic Item,4096",// 4096
			"Genetics,8192",// 8192
			"Tailor Mandalorian,16384",// 16384
			"Armorsmith Mandalorian,32768",// 32768
			"Droid Engineer Mandalorian,65536",// 65536
			"Starship Components,131072",// 131072
			"Ship Tool,262144",// 262144
			"Misc,524288",// 524288
			"unk,1048576",// 1048576
			"unk,2097152",// 2097152
			"unk,4194304",// 4194304
			"unk,8388608",// 8388608
			"unk,16777216",// 16777216
			"unk,33554432",// 33554432
			"unk,67108864",// 67108864
			"unk,134217728",// 134217728
			"unk,268435456",// 268435456
			"unk,536870912",// 536870912
			"unk,1073741824",// 1073741824
			"unk,2147483648",// 2147483648
			"Mission,-2147483648",// -2147483648
	};

	public static final int EQUIPPED_STATE_EQUIPPED = 4;
	public static final int EQUIPPED_STATE_UNEQUIPPED = -1;
	public static final int EQUIPPED_STATE_BEING_CRAFTED = 0;

	public static final int FACTION_BANNER_REBEL = 7597;
	public static final int FACTION_BANNER_IMPERIAL = 7596;
	public static final int FACTION_BANNER_NEUTRAL = 7598;

	public final static String PET_ANIMAL_POSITIVE_RESPONSE = "!";
	public final static String PET_ANIMAL_NEGATIVE_RESPONSE = "?";

	public final static String[] PET_NPC_RESPONSES = {
			"I did not Understand Sir!", "Yes Sir!",
			"Yes Sir, When you say that word i will obey!", };

	public final static byte FORAGE_TYPE_GENERAL = 0x01;
	public final static byte FORAGE_TYPE_MEDICAL = 0x02;

	public final static int[] FORAGED_ITEMS = { 9542, 9543, 9544, 9545, 9546,
			9547, 9548, 9549, 9550, 9551, 9552, 9553, 9554, 9555, };

	public final static int[] FORAGED_SKILL_MODIFIERS = { 2, // healing_dance_wound//
			3, // healing_music_wound//
			4, // healing_music_ability//
			5, // healing_dance_ability//
			9, // foraging//
			10, // trapping//
			12, // creature_knowledge//
			13, // creature_harvesting//
			15, // burst_run//
			16, // mask_scent//
			17, // creature_hit_bonus//
			18, // healing_injury_treatment//
			19, // healing_ability//
			20, // medical_foraging//
			21, // healing_injury_speed//
			22, // medicine_assembly//
			23, // medicine_experimentation//
			24, // surveying//
			25, // general_assembly//
			26, // general_experimentation//
			27, // clothing_customization//
			28, // armor_customization//
			32, // unarmed_accuracy//
			33, // unarmed_damage//
			34, // unarmed_speed//
			35, // polearm_accuracy//
			36, // polearm_speed//
			37, // onehandmelee_accuracy//
			38, // onehandmelee_speed//
			39, // twohandmelee_accuracy//
			40, // twohandmelee_speed//
			45, // taunt//
			47, // polearm_center_of_being_efficacy//
			48, // onehandmelee_center_of_being_efficacy//
			49, // twohandmelee_center_of_being_efficacy//
			50, // center_of_being_duration_unarmed//
			51, // center_of_being_duration_onehandmelee//
			52, // center_of_being_duration_twohandmelee//
			53, // center_of_being_duration_polearm//
			54, // warcry//
			55, // intimidate//
			56, // berserk//
			57, // pistole_accuracy//
			58, // pistol_speed//
			59, // rifle_accuracy//
			60, // rifle_speed//
			61, // carbine_accuracy//
			62, // carbine_speed//
			66, // rifle_concealment_chance//
			67, // ranged_defense//
			68, // melee_defense//
			69, // alert//
			70, // block//
			71, // posture_change_up_defense//
			72, // stun_defense//
			73, // blind_defense//
			74, // dizzy_defense//
			75, // dodge//
			76, // pistol_hit_while_moving//
			77, // pistol_aim//
			78, // posture_change_down_defense//
			79, // knockdown_defense//
			80, // counterattack//
			81, // unarmed_toughness//
			82, // combat_equillibrium//
			83, // private_med_dot//
			84, // unarmed_passive_defense//
			85, // private_med_wound//
			86, // unarmed_toughness//
			87, // healing_dance_mind//
			88, // healing_music_shock//
			89, // healing_music_mind//
			90, // instrument_assembly//
			93, // medicine_assembly//
			94, // medicine_expermentation//
			96, // healing_wound_speed//
			97, // healing_wound_treatment//
			99, // camouflage//
			100, // rescue//
			108, // bio_engineer_assembly//
			109, // bio_engineer_experimentation//
			110, // dna_harvesting//
			111, // armor_assembly//
			112, // armor_experimentation//
			113, // weapon_assembly//
			114, // weapon_experimentation//
			115, // food_assembly//
			116, // food_experimentation//
			117, // clothing_assembly//
			118, // clothing_experimentation//
			119, // structure_assembly//
			120, // structure_experimentation//
			121, // droid_assembly//
			122, // droid_customization//
			125, // feign_death//
			126, // spice_assembly//
			127, // spice_experimentation//
			128, // bounty_mission_level//
			129, // heavy_rifle_lighting_accuracy//
			130, // heavy_rifle_lighting_speed//
			132, // droid_tracks//
			133, // droid_track_chance//
			134, // droid_find_chance//
			135, // droid_find_speed//
			136, // droid_track_speed//
			137, // heavy_flame_thrower_accuracy//
			138, // heavy_rifle_acid_accuracy//
			139, // thrown_accuracy//
			140, // heavy_rocket_launcher_accuracy//
			141, // healing_range_speed//
			142, // combat_medicine_experimentation//
			143, // combat_healing_ability//
			144, // combat_medic_effectiveness//
			145, // healing_range//
			146, // combat_medicine_assembly//
			147, // combat_medicine_experimentation//
			148, // group_melee_defense//
			149, // ground_ranged_defense//
			150, // group_burst_run//
			151, // group_slope_move//
			152, // steadyaim//
			153, // volley//
			185, // take_cover//
			186, // leadership//
			187, // general_experimentation//
			188, // alert//
			190, // engine_assembly//
			191, // booster_assembly//
			192, // weapon_systems//
			193, // chassis_assembly//
			194, // power_systems//
			195, // shields_assembly//
			196, // advanced_assmebly//
			197, // chassis_expermentation//
			198, // weapons_systems_experimentation//
			199, // engine_experimentation//
			200, // booster_experimentation//
			201, // power_systems_experimentation//
			202, // shields_experimentation//
			203, // advanced_ship_experimentation//
			204, // defense_reverse//
			205, // propulsion_reverse//
			206, // engineering_reverse//
			207, // systems_reverse//
			208, // pilot_special_tactics//
			209, // missile_launching//

	};

	protected final static String[] SKILL_MODIFIER_NAMES = { "",
			"language_basic_speak", "healing_dance_wound",
			"healing_music_wound", "healing_music_ability",
			"healing_dance_ability", "hair", "markings", "face", "foraging",
			"trapping", "camp", "creature_knowledge", "creature_harvesting",
			"slope_move", "burst_run", "mask_scent", "creature_hit_bonus",
			"healing_injury_treatment", "healing_ability", "medical_foraging",
			"healing_injury_speed", "medicine_assembly",
			"medicine_experimentation", "surveying", "general_assembly",
			"general_experimentation", "clothing_customization",
			"armor_customization", "manage_vendor", "hiring",
			"vendor_item_limit", "unarmed_accuracy", "unarmed_damage",
			"unarmed_speed", "polearm_accuracy", "polearm_speed",
			"onehandmelee_accuracy", "onehandmelee_speed",
			"twohandmelee_accuracy", "twohandmelee_speed",
			"private_onehandmelee_combat_difficulty",
			"private_twohandmelee_combat_difficulty",
			"private_unarmed_combat_difficulty",
			"private_polearm_combat_difficulty", "taunt",
			"private_center_of_being_efficacy",
			"polearm_center_of_being_efficacy",
			"onehandmelee_center_of_being_efficacy",
			"twohandmelee_center_of_being_efficacy",
			"center_of_being_duration_unarmed",
			"center_of_being_duration_onehandmelee",
			"center_of_being_duration_twohandmelee",
			"center_of_being_duration_polearm", "warcry", "intimidate",
			"berserk", "pistole_accuracy", "pistol_speed", "rifle_accuracy",
			"rifle_speed", "carbine_accuracy", "carbine_speed",
			"private_rifle_combat_difficulty",
			"private_combine_combat_difficulty",
			"private_pistol_combat_difficulty", "rifle_concealment_chance",
			"ranged_defense", "melee_defense", "alert", "block",
			"posture_change_up_defense", "stun_defense", "blind_defense",
			"dizzy_defense", "dodge", "pistol_hit_while_moving", "pistol_aim",
			"posture_change_down_defense", "knockdown_defense",
			"counterattack", "unarmed_toughness", "combat_equillibrium",
			"private_med_dot", "unarmed_passive_defense", "private_med_wound",
			"unarmed_toughness", "healing_dance_mind", "healing_music_shock",
			"healing_music_mind", "instrument_assembly",
			"private_place_cantina", "private_place_theater",
			"medicine_assembly", "medicine_expermentation",
			"private_place_hospital", "healing_wound_speed",
			"healing_wound_treatment", "private_areatrack", "camouflage",
			"rescue", "stored_pets", "keep_creature", "tame_non_aggro",
			"tame_level", "private_creature_empathy",
			"private_creature_handling", "private_creature_training",
			"bio_engineer_assembly", "bio_engineer_experimentation",
			"dna_harvesting", "armor_assembly", "armor_experimentation",
			"weapon_assembly", "weapon_experimentation", "food_assembly",
			"food_experimentation", "clothing_assembly",
			"clothing_experimentation", "structure_assembly",
			"structure_experimentation", "droid_assembly",
			"droid_customization", "shop_sign", "language_all_comprehend",
			"feign_death", "spice_assembly", "spice_experimentation",
			"bounty_mission_level", "heavy_rifle_lighting_accuracy",
			"heavy_rifle_lighting_speed",
			"private_heavyweapon_combat_difficulty", "droid_tracks",
			"droid_track_chance", "droid_find_chance", "droid_find_speed",
			"droid_track_speed", "heavy_flame_thrower_accuracy",
			"heavy_rifle_acid_accuracy", "thrown_accuracy",
			"heavy_rocket_launcher_accuracy", "healing_range_speed",
			"combat_medicine_experimentation", "combat_healing_ability",
			"combat_medic_effectiveness", "healing_range",
			"combat_medicine_assembly", "combat_medicine_experimentation",
			"group_melee_defense", "ground_ranged_defense", "group_burst_run",
			"group_slope_move", "steadyaim", "volley",
			"private_place_cityhall", "private_place_bank",
			"private_place_shuttleport", "private_place_cloning",
			"private_place_garage", "private_place_small_garden",
			"private_place_medium_garden", "private_place_large_garden",
			"private_place_exotic_garden", "language_basic_speak",
			"language_basic_comprehend", "language_rodian_speak",
			"language_rodian_comprehend", "language_trandoshan_speak",
			"language_trandoshan_comprehend", "language_moncalamari_speak",
			"language_moncalamari_comprehend", "language_wookie_speak",
			"language_wookie_comprehend", "language_bothan_speak",
			"language_bothan_comprehend", "language_twilek_speak",
			"language_twilek_comprehend", "language_zabrak_speak",
			"language_zabrak_comprehend", "language_lekku_speak",
			"language_lekku_comprehend", "language_ithorian_speak",
			"language_ithorian_comprehend", "language_sullustan_speak",
			"language_sullustan_comprehend", "take_cover", "leadership",
			"general_experimentation", "alert", "private_innate_regeneration",
			"engine_assembly", "booster_assembly", "weapon_systems",
			"chassis_assembly", "power_systems", "shields_assembly",
			"advanced_assmebly", "chassis_expermentation",
			"weapons_systems_experimentation", "engine_experimentation",
			"booster_experimentation", "power_systems_experimentation",
			"shields_experimentation", "advanced_ship_experimentation",
			"defense_reverse", "propulsion_reverse", "engineering_reverse",
			"systems_reverse", "pilot_special_tactics", "missile_launching",
			"onehandlightsaber_accuracy", "onehandlightsaber_speed",
			"twohandlightsaber_accuracy", "twohandlightsaber_speed",
			"polearmlightsaber_accuracy", "polearmlightsaber_speed",
			"private_onehandlightsaber_combat_difficulty",
			"private_twohandlightsaber_combat_difficulty",
			"private_polearmlightsaber_combat_difficulty",
			"jedi_force_power_max", "jedi_force_power_regen"

	};

	/**
	 * private_entertainer_novice,
	 * musician,dancer,imagedesigner,startDance,stopDance,startMusic,stopMusic,
	 * flourish+1, flourish+2, flourish+3, flourish+4, flourish+5, flourish+6,
	 * flourish+7, flourish+8, startDance+basic, startDance+rhythmic,
	 * startMusic+starwars1, slitherhorn
	 * 
	 * private_entertainer_master, startDance+footloose2, startDance+formal2,
	 * startMusic+ceremonial, mandoviol
	 * 
	 * private_entertainer_dance_1, startDance+basic2
	 * 
	 * private_entertainer_dance_2, startDance+rhythmic2
	 * 
	 * private_entertainer_dance_3, startDance+footloose
	 * 
	 * private_entertainer_dance_4, startDance+formal
	 * 
	 * startDance+popular, startDance+poplock, registerWithLocation
	 * 
	 * startDance+lyrical2, startDance+exotic3, startDance+exotic4,
	 * place_cantina, place_theater
	 * 
	 * startDance+popular2
	 * 
	 * startDance+poplock2
	 * 
	 * startDance+lyrical
	 * 
	 * startDance+exotic, startDance+exotic2
	 * 
	 */
	protected final static int[] DANCE_SKILL_REQUIREMENTS = { 11,// {"basic","dance_1"},
			21,// {"basic2","dance_2"},
			11,// {"rhythmic","dance_3"},
			22,// {"rhythmic2","dance_4"},
			275,// {"exotic","dance_5"},
			275,// {"exotic2","dance_6"},
			263,// {"exotic3","dance_7"},
			263,// {"exotic4","dance_8"},
			262,// {"popular","dance_9"},
			272,// {"popular2","dance_10"},
			274,// {"lyrical","dance_11"},
			263,// {"lyrical2","dance_12"},
			262,// {"poplock","dance_13"},
			273,// {"poplock2","dance_14"},
			23,// {"footloose","dance_15"},
			12,// {"footloose2","dance_16"},
			24,// {"formal","dance_17"},
			12,// {"formal2","dance_18"},
			2011,// {"theatrical","dance_21"}, these are assigned by doing
			// quests so eventually will need to check
			2011,// {"theatrical2","dance_22"},
			2011,// {"breakdance","dance_29"},
			2011,// {"breakdance2","dance_30"},
			2011,// {"tumble","dance_31"},
			2011,// {"tumble2","dance_32"},
	};

	// This appears to match up dance paramaters that the client passes in, with
	// the correct animation to set for the act of dancing.
	// However, since all of the dances are "dance_ID", we could store these in
	// a 1d String array, and the array index of the client's typed string shows
	// us the dance animation to set.
	// IE the player says "/startdance lyrical". We loop through DANCE_STRINGS
	// until we find lyrical, and we set the dance animation to "dance_" +
	// String.valueOf(index);
	// It appears there are some missing, so those elements would simply be null
	// (or "")
	protected final static String[][] DANCE_STRINGS = { { "basic", "dance_1" },
			{ "basic2", "dance_2" }, { "rhythmic", "dance_3" },
			{ "rhythmic2", "dance_4" }, { "exotic", "dance_5" },
			{ "exotic2", "dance_6" }, { "exotic3", "dance_7" },
			{ "exotic4", "dance_8" }, { "popular", "dance_9" },
			{ "popular2", "dance_10" }, { "lyrical", "dance_11" },
			{ "lyrical2", "dance_12" }, { "poplock", "dance_13" },
			{ "poplock2", "dance_14" }, { "footloose", "dance_15" },
			{ "footloose2", "dance_16" }, { "formal", "dance_17" },
			{ "formal2", "dance_18" }, { "theatrical", "dance_21" },
			{ "theatrical2", "dance_22" }, { "breakdance", "dance_29" },
			{ "breakdance2", "dance_30" }, { "tumble", "dance_31" },
			{ "tumble2", "dance_32" }, };

	/**
	 * These are for Dancer and Musician Dancer Skill Musician Skill
	 * entertainer_color_lights_level_1.cef 264 283
	 * entertainer_color_lights_level_2.cef 265 284
	 * entertainer_color_lights_level_3.cef 266 285
	 * entertainer_dazzle_level_1.cef 264 283 entertainer_dazzle_level_2.cef 265
	 * 284 entertainer_dazzle_level_3.cef 266 285
	 * entertainer_spot_light_level_1.cef 264 283
	 * entertainer_spot_light_level_2.cef 265 284
	 * entertainer_spot_light_level_3.cef 266 285
	 * 
	 * These are for Musician only entertainer_fire_jets_level_1.cef 284
	 * entertainer_fire_jets_level_2.cef 285 entertainer_fire_jets_level_3.cef
	 * 286
	 * 
	 * entertainer_ventriloquism_level_1.cef 285
	 * entertainer_ventriloquism_level_2.cef 286
	 * entertainer_ventriloquism_level_3.cef 282
	 * 
	 * These are for Dancer Only entertainer_distract_level_1.cef 265
	 * entertainer_distract_level_2.cef 266 entertainer_distract_level_3.cef 267
	 * 
	 * entertainer_smoke_bomb_level_1.cef 266 entertainer_smoke_bomb_level_2.cef
	 * 267 entertainer_smoke_bomb_level_3.cef 263
	 * 
	 */
	/**
	 * private_entertainer_novice,musician, dancer, imagedesigner, startDance,
	 * stopDance, startMusic, stopMusic,
	 * flourish+1,flourish+2,flourish+3,flourish
	 * +4,flourish+5,flourish+6,flourish+7,flourish+8, startDance+basic,
	 * startDance+rhythmic, startMusic+starwars1, slitherhorn
	 * 
	 * private_entertainer_master, startDance+footloose2, startDance+formal2,
	 * startMusic+ceremonial, mandoviol
	 * 
	 * private_entertainer_music_1, startMusic+rock, fizz
	 * 
	 * private_entertainer_music_2, startMusic+starwars2
	 * 
	 * private_entertainer_music_3, startMusic+folk, fanfar
	 * 
	 * private_entertainer_music_4, startMusic+starwars3, kloohorn
	 * 
	 * startMusic+virtuoso, nalargon, place_cantina, place_theater
	 * 
	 * startMusic+ballad
	 * 
	 * startMusic+waltz, flutedroopy
	 * 
	 * startMusic+jazz, omnibox
	 */
	protected final static int[] MUSIC_SKILL_REQUIREMENTS = { 11, // starwars1
			12, // ceremonial
			17, // rock
			18, // starwars2
			19, // folk
			20, // starwars3
			282,// virtuoso
			291,// ballad
			293,// waltz
			294,// jazz
	};

	protected final static String[][] MUSIC_STRINGS = { { "starwars1", "1" },
			{ "ceremonial", "6" }, { "rock", "2" }, { "starwars2", "3" },
			{ "folk", "4" }, { "starwars3", "5" }, { "virtuoso", "10" },
			{ "ballad", "7" }, { "waltz", "8" }, { "jazz", "9" }, };

	// Resource weight type settings.
	protected final static int RESOURCE_WEIGHT_POTENCY = 0;
	protected final static int RESOURCE_WEIGHT_COLD_RESISTANCE = 16; // Integer.parseInt("00010000",
	// 2);
	protected final static int RESOURCE_WEIGHT_CONDUCTIVITY = 32; // Integer.parseInt("00100000",
	// 2);
	protected final static int RESOURCE_WEIGHT_DECAY_RESISTANCE = 48; // Integer.parseInt("00110000",
	// 2);
	protected final static int RESOURCE_WEIGHT_HEAT_RESISTANCE = 64; // Integer.parseInt("01000000",
	// 2);
	protected final static int RESOURCE_WEIGHT_FLAVOR = 80; // Integer.parseInt("01010000",
	// 2);
	protected final static int RESOURCE_WEIGHT_MALLEABILITY = 96; // Integer.parseInt("01100000",
	// 2);
	protected final static int RESOURCE_WEIGHT_POTENTIAL_ENERGY = 112; // Integer.parseInt("01110000",
	// 2);
	protected final static int RESOURCE_WEIGHT_OVERALL_QUALITY = 128; // Integer.parseInt("10000000",
	// 2);
	protected final static int RESOURCE_WEIGHT_SHOCK_RESISTANCE = 144; // Integer.parseInt("10010000",
	// 2);
	protected final static int RESOURCE_WEIGHT_UNIT_TOUGHNESS = 160; // Integer.parseInt("10100000",
	// 2);
	protected final static int RESOURCE_WEIGHT_BULK = 176; // Integer.parseInt("10110000",

	// 2);

	/**
	 * Customization Data Related
	 */
	protected static enum CUSTOM_DATA_VALUE {
		X00,
	}

	// sounds related
	protected final static String[] SOUND_FILES = {
			"amb_alarm_air_raid_lp.snd", // 0
			"amb_biogenics_reactor_bootup.snd", // 1
			"amb_cantina_large_lp.snd", // 2
			"amb_cantina_medium_lp.snd", // 3
			"amb_cantina_small_lp.snd", // 4
			"amb_cave_drip.snd", // 5
			"amb_cave_drip_rnd.snd", // 6
			"amb_cave_int_lp.snd", // 7
			"amb_city_crowd_sentients_lp.snd", // 8
			"amb_city_large_crowd_sentients_lp.snd", // 9
			"amb_city_small_crowd_lp.snd", // 10
			"amb_city_small_grp_lp.snd", // 11
			"amb_cloning_facility_int_lp.snd", // 12
			"amb_combat_area_lp.snd", // 13
			"amb_corellia_agrilat_lp.snd", // 14
			"amb_corellia_agrilat_os.snd", // 15
			"amb_corellia_bela_vistal_lp.snd", // 16
			"amb_corellia_bela_vistal_os.snd", // 17
			"amb_corellia_coronet_lp.snd", // 18
			"amb_corellia_coronet_os.snd", // 19
			"amb_corellia_doaba_guerfel_lp.snd", // 20
			"amb_corellia_doaba_guerfel_os.snd", // 21
			"amb_corellia_gold_beaches_lp.snd", // 22
			"amb_corellia_gold_beaches_os.snd", // 23
			"amb_corellia_kor_vella_lp.snd", // 24
			"amb_corellia_kor_vella_os.snd", // 25
			"amb_corellia_mesas_lp.snd", // 26
			"amb_corellia_mesas_os.snd", // 27
			"amb_corellia_mountains_lp.snd", // 28
			"amb_corellia_mountains_os.snd", // 29
			"amb_corellia_pve_bf_01_lp.snd", // 30
			"amb_corellia_pve_bf_01_os.snd", // 31
			"amb_corellia_pve_bf_02_lp.snd", // 32
			"amb_corellia_pve_bf_02_os.snd", // 33
			"amb_corellia_pvp_bf_01_lp.snd", // 34
			"amb_corellia_pvp_bf_01_os.snd", // 35
			"amb_corellia_rebel_hideout_lp.snd", // 36
			"amb_corellia_rebel_hideout_os.snd", // 37
			"amb_corellia_swamp_lp.snd", // 38
			"amb_corellia_swamp_os.snd", // 39
			"amb_corellia_the_coast_lp.snd", // 40
			"amb_corellia_the_coast_os.snd", // 41
			"amb_corellia_tyrena_lp.snd", // 42
			"amb_corellia_tyrena_os.snd", // 43
			"amb_corellia_vreni_island_lp.snd", // 44
			"amb_corellia_vreni_island_os.snd", // 45
			"amb_crowd_booing_lp.snd", // 46
			"amb_crowd_cheering_lp.snd", // 47
			"amb_dantooine_abandoned_rebel_base_lp.snd", // 48
			"amb_dantooine_abandoned_rebel_base_os.snd", // 49
			"amb_dantooine_dantari1_lp.snd", // 50
			"amb_dantooine_dantari1_os.snd", // 51
			"amb_dantooine_dantari2_lp.snd", // 52
			"amb_dantooine_dantari2_os.snd", // 53
			"amb_dantooine_dantari3_lp.snd", // 54
			"amb_dantooine_dantari3_os.snd", // 55
			"amb_dantooine_imperial_outpost_lp.snd", // 56
			"amb_dantooine_imperial_outpost_os.snd", // 57
			"amb_dantooine_jedi_temple_ruins_lp.snd", // 58
			"amb_dantooine_jedi_temple_ruins_os.snd", // 59
			"amb_dantooine_lake_lp.snd", // 60
			"amb_dantooine_lake_os.snd", // 61
			"amb_dantooine_mining_outpost_lp.snd", // 62
			"amb_dantooine_mining_outpost_os.snd", // 63
			"amb_dantooine_outpost2_lp.snd", // 64
			"amb_dantooine_outpost2_os.snd", // 65
			"amb_dantooine_secondstepes_lp.snd", // 66
			"amb_dantooine_secondstepes_os.snd", // 67
			"amb_dathomir_crash_site_lp.snd", // 68
			"amb_dathomir_crash_site_lp_2.snd", // 69
			"amb_dathomir_crash_site_os.snd", // 70
			"amb_dathomir_greatcanyonwitchterritory_lp.snd", // 71
			"amb_dathomir_greatcanyonwitchterritory_os.snd", // 72
			"amb_dathomir_imperial_prison_lp.snd", // 73
			"amb_dathomir_imperial_prison_os.snd", // 74
			"amb_dathomir_nightsisters_clan_lp.snd", // 75
			"amb_dathomir_nightsisters_clan_os.snd", // 76
			"amb_dathomir_outpost_2_lp.snd", // 77
			"amb_dathomir_outpost_2_os.snd", // 78
			"amb_dathomir_pve_bf_lp.snd", // 79
			"amb_dathomir_pve_bf_os.snd", // 80
			"amb_dathomir_pvp_bf_lp.snd", // 81
			"amb_dathomir_pvp_bf_os.snd", // 82
			"amb_dathomir_singingmountainwitchcity_lp.snd", // 83
			"amb_dathomir_singingmountainwitchcity_os.snd", // 84
			"amb_dathomir_singing_mountain_lp.snd", // 85
			"amb_dathomir_singing_mountain_os.snd", // 86
			"amb_dathomir_tarpits_lp.snd", // 87
			"amb_dathomir_tarpits_os.snd", // 88
			"amb_dathomir_trade_outpost_lp.snd", // 89
			"amb_dathomir_trade_outpost_os.snd", // 90
			"amb_desert_inside.snd", // 91
			"amb_desert_night.snd", // 92
			"amb_desert_outside.snd", // 93
			"amb_endor_dulok_village_1_lp.snd", // 94
			"amb_endor_dulok_village_1_os.snd", // 95
			"amb_endor_dulok_village_2_lp.snd", // 96
			"amb_endor_dulok_village_2_os.snd", // 97
			"amb_endor_ewok_lake_village_1_lp.snd", // 98
			"amb_endor_ewok_lake_village_1_os.snd", // 99
			"amb_endor_ewok_lake_village_2_lp.snd", // 100
			"amb_endor_ewok_lake_village_2_os.snd", // 101
			"amb_endor_ewok_tree_village_1_lp.snd", // 102
			"amb_endor_ewok_tree_village_1_os.snd", // 103
			"amb_endor_ewok_tree_village_2_lp.snd", // 104
			"amb_endor_ewok_tree_village_2_os.snd", // 105
			"amb_endor_marauder_base_lp.snd", // 106
			"amb_endor_marauder_base_os.snd", // 107
			"amb_endor_outpost2_lp.snd", // 108
			"amb_endor_outpost2_os.snd", // 109
			"amb_endor_pvp_bf_01_lp.snd", // 110
			"amb_endor_pvp_bf_01_os.snd", // 111
			"amb_endor_pvp_bf_02_lp.snd", // 112
			"amb_endor_pvp_bf_02_os.snd", // 113
			"amb_endor_smuggler_outpost_lp.snd", // 114
			"amb_endor_smuggler_outpost_os.snd", // 115
			"amb_exarkun_temple_lp.snd", // 116
			"amb_factory_exterior_lp.snd", // 117
			"amb_fire_roaring_lp.snd", // 118
			"amb_forest_indoors_lp.snd", // 119
			"amb_forest_night.snd", // 120
			"amb_forest_outside.snd", // 121
			"amb_fort_tusken_lp.snd", // 122
			"amb_fusion_power_gen_lp.snd", // 123
			"amb_geon_biolab_bacta_rm_lp.snd", // 124
			"amb_geon_biolab_main_rm_lp.snd", // 125
			"amb_geon_biolab_reactr_rm_lp.snd", // 126
			"amb_geyser_end.snd", // 127
			"amb_geyser_loop.snd", // 128
			"amb_geyser_start.snd", // 129
			"amb_grasslands_indoors_lp.snd", // 130
			"amb_grassland_outside.snd", // 131
			"amb_green_evil_fire_lp.snd", // 132
			"amb_hangar_lp.snd", // 133
			"amb_heating_kabob_lp.snd", // 134
			"amb_hospital.snd", // 135
			"amb_hover_lp.snd", // 136
			"amb_hydro_pwr_gen_ext_lp.snd", // 137
			"amb_insect_swarm_lrg.snd", // 138
			"amb_insect_swarm_lrg_lp.snd", // 139
			"amb_insect_swarm_sml.snd", // 140
			"amb_insect_swarm_sml_lp.snd", // 141
			"amb_installation_hydro.snd", // 142
			"amb_installation_photo_bio.snd", // 143
			"amb_installation_wind.snd", // 144
			"amb_jabba_audience_chamb_lp.snd", // 145
			"amb_jabba_monk_hideaway_lp.snd", // 146
			"amb_jabba_palace_entr_lp.snd", // 147
			"amb_jungle_indoors_lp.snd", // 148
			"amb_jungle_outside.snd", // 149
			"amb_kashyyk_droid_cave.snd", // 150
			"amb_kashyyk_int_avatar_dungeon.snd", // 151
			"amb_kashyyk_int_avatar_platform.snd", // 152
			"amb_kashyyk_int_cybernetics_lab.snd", // 153
			"amb_kashyyk_ryratt_trail.snd", // 154
			"amb_kashyyk_treun_lorn_laugh.snd", // 155
			"amb_kashyyk_wilderness_hvy.snd", // 156
			"amb_kashyyk_wilderness_lgt.snd", // 157
			"amb_kashyyk_wookiee_slave_cries.snd", // 158
			"amb_kashyyyk_dead_forest.snd", // 159
			"amb_kashyyyk_droid_cave.snd", // 160
			"amb_kashyyyk_elders_hut.snd", // 161
			"amb_kashyyyk_epic_island.snd", // 162
			"amb_kashyyyk_oktikuti_city.snd", // 163
			"amb_kashyyyk_rodian_safari.snd", // 164
			"amb_kashyyyk_ryratt_trail.snd", // 165
			"amb_lakeshore_outside.snd", // 166
			"amb_lava_lp.snd", // 167
			"amb_lok_imperial_outpost_lp.snd", // 168
			"amb_lok_imperial_outpost_os.snd", // 169
			"amb_lok_mtexsplodalott_lp.snd", // 170
			"amb_lok_mtexsplodalott_os.snd", // 171
			"amb_lok_new_city_lp.snd", // 172
			"amb_lok_new_city_os.snd", // 173
			"amb_lok_nym_stronghold_lp.snd", // 174
			"amb_lok_nym_stronghold_os.snd", // 175
			"amb_lok_outpost_lp.snd", // 176
			"amb_lok_outpost_os.snd", // 177
			"amb_lok_rocky_wastelands_lp.snd", // 178
			"amb_lok_rocky_wastelands_os.snd", // 179
			"amb_lok_volcano1_lp.snd", // 180
			"amb_lok_volcano1_os.snd", // 181
			"amb_lok_volcano2_lp.snd", // 182
			"amb_lok_volcano2_os.snd", // 183
			"amb_lok_volcano_lp.snd", // 184
			"amb_lok_volcano_os.snd", // 185
			"amb_marketplace_large_lp.snd", // 186
			"amb_marketplace_small_lp.snd", // 187
			"amb_mining_facility_gas_lp.snd", // 188
			"amb_mining_facility_liqd_lp.snd", // 189
			"amb_mining_facility_ore_lp.snd", // 190
			"amb_moisture_harvester_lp.snd", // 191
			"amb_mountains_indoors_lp.snd", // 192
			"amb_mountains_outside.snd", // 193
			"amb_myyydril_caverns_creepy", // 194
			"amb_myyydril_caverns_laughter.snd", // 195
			"amb_naboo_deeja_peak_lp.snd", // 196
			"amb_naboo_deeja_peak_os.snd", // 197
			"amb_naboo_emperors_retreat_lp.snd", // 198
			"amb_naboo_emperors_retreat_os.snd", // 199
			"amb_naboo_gungan_sacred_place_lp.snd", // 200
			"amb_naboo_gungan_sacred_place_os.snd", // 201
			"amb_naboo_kaadara_lp.snd", // 202
			"amb_naboo_kaadara_os.snd", // 203
			"amb_naboo_keren_lp.snd", // 204
			"amb_naboo_keren_os.snd", // 205
			"amb_naboo_lake_paonga_lp.snd", // 206
			"amb_naboo_lake_paonga_os.snd", // 207
			"amb_naboo_lake_retreat_lp.snd", // 208
			"amb_naboo_lake_retreat_os.snd", // 209
			"amb_naboo_moenia_lp.snd", // 210
			"amb_naboo_moenia_os.snd", // 211
			"amb_naboo_pve_bf_01_lp.snd", // 212
			"amb_naboo_pve_bf_01_os.snd", // 213
			"amb_naboo_pve_bf_02_lp.snd", // 214
			"amb_naboo_pve_bf_02_os.snd", // 215
			"amb_naboo_pvp_bf_lp.snd", // 216
			"amb_naboo_pvp_bf_os.snd", // 217
			"amb_naboo_rainforest_lp.snd", // 218
			"amb_naboo_rainforest_os.snd", // 219
			"amb_naboo_swamp_towne_lp.snd", // 220
			"amb_naboo_swamp_towne_os.snd", // 221
			"amb_naboo_theed_lp.snd", // 222
			"amb_naboo_theed_os.snd", // 223
			"amb_nightsisters_lp.snd", // 224
			"amb_ore_harvester_2_lp.snd", // 225
			"amb_power_generator_ext_lp.snd", // 226
			"amb_rebel_hideout_int_lp.snd", // 227
			"amb_red_evil_fire_lp.snd", // 228
			"amb_refrigeration_unit_lp.snd", // 229
			"amb_river_indoors_lp.snd", // 230
			"amb_river_large_lp.snd", // 231
			"amb_river_small_lp.snd", // 232
			"amb_rori_narmle_lp.snd", // 233
			"amb_rori_narmle_os.snd", // 234
			"amb_rori_restuss_lp.snd", // 235
			"amb_rori_restuss_os.snd", // 236
			"amb_sailbarge_lower_deck_lp.snd", // 237
			"amb_sailbarge_upper_deck_lp.snd", // 238
			"amb_seashore_indoors_lp.snd", // 239
			"amb_seashore_night_lp.snd", // 240
			"amb_seashore_outside.snd", // 241
			"amb_shield_generator_ext_lp.snd", // 242
			"amb_snow_indoors_lp.snd", // 243
			"amb_snow_night_lp.snd", // 244
			"amb_snow_outside.snd", // 245
			"amb_spaceyacht_interior_lp.snd", // 246
			"amb_space_station_lp.snd", // 247
			"amb_starport_exterior_lp.snd", // 248
			"amb_starport_int_lp.snd", // 249
			"amb_swamp_indoors_lp.snd", // 250
			"amb_swamp_night.snd", // 251
			"amb_swamp_outside.snd", // 252
			"amb_talus_dearic_lp.snd", // 253
			"amb_talus_dearic_os.snd", // 254
			"amb_talus_nashal_lp.snd", // 255
			"amb_talus_nashal_os.snd", // 256
			"amb_talus_nashal_starport_lp.snd", // 257
			"amb_talus_nashal_starport_os.snd", // 258
			"amb_talus_pvp_bf_lp.snd", // 259
			"amb_talus_pvp_bf_os.snd", // 260
			"amb_tatooine_anchorhead_lp.snd", // 261
			"amb_tatooine_anchorhead_os.snd", // 262
			"amb_tatooine_arch_mesa_lp.snd", // 263
			"amb_tatooine_arch_mesa_os.snd", // 264
			"amb_tatooine_bestine_township_lp.snd", // 265
			"amb_tatooine_bestine_township_os.snd", // 266
			"amb_tatooine_bf_canyon_maze_pvp_lp.snd", // 267
			"amb_tatooine_bf_canyon_maze_pvp_os.snd", // 268
			"amb_tatooine_bf_dune_sea_pve_lp.snd", // 269
			"amb_tatooine_bf_dune_sea_pve_os.snd", // 270
			"amb_tatooine_bf_oasis_pvp_lp.snd", // 271
			"amb_tatooine_bf_oasis_pvp_os.snd", // 272
			"amb_tatooine_bf_ridge_pve_lp.snd", // 273
			"amb_tatooine_bf_ridge_pve_os.snd", // 274
			"amb_tatooine_fort_tusken_lp.snd", // 275
			"amb_tatooine_fort_tusken_os.snd", // 276
			"amb_tatooine_jawa_mountain_fortress_lp.snd", // 277
			"amb_tatooine_jawa_mountain_fortress_os.snd", // 278
			"amb_tatooine_krayt_graveyard_lp.snd", // 279
			"amb_tatooine_krayt_graveyard_os.snd", // 280
			"amb_tatooine_mos_eisley_lp.snd", // 281
			"amb_tatooine_mos_eisley_os.snd", // 282
			"amb_tatooine_mos_entha_lp.snd", // 283
			"amb_tatooine_mos_entha_os.snd", // 284
			"amb_tatooine_mos_espa_lp.snd", // 285
			"amb_tatooine_mos_espa_os.snd", // 286
			"amb_tatooine_mos_taike_lp.snd", // 287
			"amb_tatooine_mos_taike_os.snd", // 288
			"amb_tatooine_oasis_east_lp.snd", // 289
			"amb_tatooine_oasis_east_os.snd", // 290
			"amb_tatooine_oasis_imperial_lp.snd", // 291
			"amb_tatooine_oasis_imperial_os.snd", // 292
			"amb_tatooine_oasis_northeast_lp.snd", // 293
			"amb_tatooine_oasis_northeast_os.snd", // 294
			"amb_tatooine_oasis_south_lp.snd", // 295
			"amb_tatooine_oasis_south_os.snd", // 296
			"amb_tatooine_the_grand_arena_flats_lp.snd", // 297
			"amb_tatooine_the_grand_arena_flats_os.snd", // 298
			"amb_tatooine_wayfar_lp.snd", // 299
			"amb_tatooine_wayfar_os.snd", // 300
			"amb_terminal_bank.snd", // 301
			"amb_terminal_bazaar.snd", // 302
			"amb_terminal_cloning.snd", // 303
			"amb_terminal_insurance.snd", // 304
			"amb_terminal_mission.snd", // 305
			"amb_theed_fountain.snd", // 306
			"amb_transport_int_lp.snd", // 307
			"amb_trapped_insect_lp.snd", // 308
			"amb_waterfall_indoors.snd", // 309
			"amb_waterfall_large.snd", // 310
			"amb_waterfall_small.snd", // 311
			"amb_water_gurgle_lp.snd", // 312
			"amb_water_gurgle_small_lp.snd", // 313
			"amb_wind_power_gen_ex_lp.snd", // 314
			"amb_yavin4_imperial_base_lp.snd", // 315
			"amb_yavin4_imperial_base_os.snd", // 316
			"amb_yavin4_mining_outpost_lp.snd", // 317
			"amb_yavin4_mining_outpost_os.snd", // 318
			"amb_yavin4_outpost_2_lp.snd", // 319
			"amb_yavin4_outpost_2_os.snd", // 320
			"amb_yavin4_pvp_bf_01_lp.snd", // 321
			"amb_yavin4_pvp_bf_01_os.snd", // 322
			"amb_yavin4_pvp_bf_02_lp.snd", // 323
			"amb_yavin4_pvp_bf_02_os.snd", // 324
			"cr_acklay_attack_heavy.snd", // 325
			"cr_acklay_attack_light.snd", // 326
			"cr_acklay_hit_heavy.snd", // 327
			"cr_acklay_hit_light.snd", // 328
			"cr_acklay_vocalize.snd", // 329
			"cr_alpha_bolma_attack_hvy.snd", // 330
			"cr_alpha_bolma_attack_lgt.snd", // 331
			"cr_alpha_bolma_hit_hvy.snd", // 332
			"cr_alpha_bolma_hit_lgt.snd", // 333
			"cr_alpha_bolma_vocalize.snd", // 334
			"cr_alpha_veermok_attack_hvy.snd", // 335
			"cr_alpha_veermok_attack_lgt.snd", // 336
			"cr_alpha_veermok_hit_hvy.snd", // 337
			"cr_alpha_veermok_hit_lgt.snd", // 338
			"cr_alpha_veermok_vocalize.snd", // 339
			"cr_angler_attack_hvy.snd", // 340
			"cr_angler_attack_lgt.snd", // 341
			"cr_angler_hatchling_attack_hvy.snd", // 342
			"cr_angler_hatchling_attack_lgt.snd", // 343
			"cr_angler_hatchling_hit_hvy.snd", // 344
			"cr_angler_hatchling_hit_lgt.snd", // 345
			"cr_angler_hatchling_vocalize.snd", // 346
			"cr_angler_hit_hvy.snd", // 347
			"cr_angler_hit_lgt.snd", // 348
			"cr_arachne_hatchling_attack_hvy.snd", // 349
			"cr_arachne_hatchling_attack_lgt.snd", // 350
			"cr_arachne_hatchling_hit_hvy.snd", // 351
			"cr_arachne_hatchling_hit_lgt.snd", // 352
			"cr_arachne_hatchling_vocalize.snd", // 353
			"cr_baby_bol_attack_hvy.snd", // 354
			"cr_baby_bol_attack_lgt.snd", // 355
			"cr_baby_bol_hit_hvy.snd", // 356
			"cr_baby_bol_hit_lgt.snd", // 357
			"cr_baby_bol_vocalize.snd", // 358
			"cr_bageraset_attack_hvy.snd", // 359
			"cr_bageraset_hit_hvy.snd", // 360
			"cr_bageraset_hit_lgt.snd", // 361
			"cr_bageraset_idle_breathe.snd", // 362
			"cr_bageraset_vocalize.snd", // 363
			"cr_bantha_attack_heavy.snd", // 364
			"cr_bantha_attack_lgt.snd", // 365
			"cr_bantha_emote_vocalize.snd", // 366
			"cr_bantha_hit_heavy.snd", // 367
			"cr_bantha_hit_light.snd", // 368
			"cr_bantha_idle_breathe.snd", // 369
			"cr_bark_mite_attack_hvy.snd", // 370
			"cr_bark_mite_attack_lgt.snd", // 371
			"cr_bark_mite_burrower_queen_attack_hvy.snd", // 372
			"cr_bark_mite_burrower_queen_attack_lgt.snd", // 373
			"cr_bark_mite_burrower_queen_hit_hvy.snd", // 374
			"cr_bark_mite_burrower_queen_hit_lgt.snd", // 375
			"cr_bark_mite_burrower_queen_vocalize.snd", // 376
			"cr_bark_mite_hatchling_attack_hvy.snd", // 377
			"cr_bark_mite_hatchling_attack_lgt.snd", // 378
			"cr_bark_mite_hatchling_hit_hvy.snd", // 379
			"cr_bark_mite_hatchling_hit_lgt.snd", // 380
			"cr_bark_mite_hatchling_vocalize.snd", // 381
			"cr_bark_mite_hit_hvy.snd", // 382
			"cr_bark_mite_hit_lgt.snd", // 383
			"cr_bark_mite_vocalize.snd", // 384
			"cr_baznitch_attack_heavy.snd", // 385
			"cr_baznitch_attack_light.snd", // 386
			"cr_baznitch_emote_vocalize.snd", // 387
			"cr_baznitch_hit_heavy.snd", // 388
			"cr_baznitch_hit_light.snd", // 389
			"cr_bearded_jax_attack_hvy.snd", // 390
			"cr_bearded_jax_attack_lgt.snd", // 391
			"cr_bearded_jax_hit_hvy.snd", // 392
			"cr_bearded_jax_hit_lgt.snd", // 393
			"cr_bearded_jax_vocalize.snd", // 394
			"cr_bile_drenched_quenker_attack_hvy.snd", // 395
			"cr_bile_drenched_quenker_attack_lgt.snd", // 396
			"cr_bile_drenched_quenker_hit_hvy.snd", // 397
			"cr_bile_drenched_quenker_hit_lgt.snd", // 398
			"cr_bile_drenched_quenker_vocalize.snd", // 399
			"cr_blister_rot_queen_attack_hvy.snd", // 400
			"cr_blister_rot_queen_attack_lgt.snd", // 401
			"cr_blister_rot_queen_hit_hvy.snd", // 402
			"cr_blister_rot_queen_hit_lgt.snd", // 403
			"cr_blister_rot_queen_vocalize.snd", // 404
			"cr_bloodfanged_gacklebat_attack_hvy.snd", // 405
			"cr_bloodfanged_gacklebat_attack_lgt.snd", // 406
			"cr_bloodfanged_gacklebat_hit_hvy.snd", // 407
			"cr_bloodfanged_gacklebat_hit_lgt.snd", // 408
			"cr_bloodfanged_gacklebat_vocalize.snd", // 409
			"cr_bloodseeker_mite_attack_hvy.snd", // 410
			"cr_bloodseeker_mite_attack_lgt.snd", // 411
			"cr_bloodseeker_mite_hit_hvy.snd", // 412
			"cr_bloodseeker_mite_hit_lgt.snd", // 413
			"cr_bloodseeker_mite_queen_attack_hvy.snd", // 414
			"cr_bloodseeker_mite_queen_attack_lgt.snd", // 415
			"cr_bloodseeker_mite_queen_hit_hvy.snd", // 416
			"cr_bloodseeker_mite_queen_hit_lgt.snd", // 417
			"cr_bloodseeker_mite_queen_vocalize.snd", // 418
			"cr_bloodseeker_mite_vocalize.snd", // 419
			"cr_blood_drenched_merek_king_attack_hvy.snd", // 420
			"cr_blood_drenched_merek_king_attack_lgt.snd", // 421
			"cr_blood_drenched_merek_king_hit_hvy.snd", // 422
			"cr_blood_drenched_merek_king_hit_lgt.snd", // 423
			"cr_blood_drenched_merek_king_vocalize.snd", // 424
			"cr_blooming_jax_attack_hvy.snd", // 425
			"cr_blooming_jax_attack_lgt.snd", // 426
			"cr_blooming_jax_hit_hvy.snd", // 427
			"cr_blooming_jax_hit_lgt.snd", // 428
			"cr_blooming_jax_vocalize.snd", // 429
			"cr_blurrg_attack_light.snd", // 430
			"cr_blurrg_emote_vocalize.snd", // 431
			"cr_blurrg_hit_heavy.snd", // 432
			"cr_blurrg_hit_light.snd", // 433
			"cr_blurrg_pup_attack_hvy.snd", // 434
			"cr_blurrg_pup_attack_lgt.snd", // 435
			"cr_blurrg_pup_hit_hvy.snd", // 436
			"cr_blurrg_pup_hit_lgt.snd", // 437
			"cr_blurrg_pup_vocalize.snd", // 438
			"cr_blushing_jax_attack_hvy.snd", // 439
			"cr_blushing_jax_attack_lgt.snd", // 440
			"cr_blushing_jax_hit_hvy.snd", // 441
			"cr_blushing_jax_hit_lgt.snd", // 442
			"cr_blushing_jax_vocalize.snd", // 443
			"cr_boar_wolf_attack_hvy.snd", // 444
			"cr_boar_wolf_attack_lgt.snd", // 445
			"cr_boar_wolf_cub_attack_hvy.snd", // 446
			"cr_boar_wolf_cub_attack_lgt.snd", // 447
			"cr_boar_wolf_cub_hit_hvy.snd", // 448
			"cr_boar_wolf_cub_hit_lgt.snd", // 449
			"cr_boar_wolf_cub_vocalize.snd", // 450
			"cr_boar_wolf_hit_hvy.snd", // 451
			"cr_boar_wolf_hit_lgt.snd", // 452
			"cr_boar_wolf_vocalize.snd", // 453
			"cr_bocatt_attack_hvy.snd", // 454
			"cr_bocatt_attack_lgt.snd", // 455
			"cr_bocatt_hit_hvy.snd", // 456
			"cr_bocatt_hit_lgt.snd", // 457
			"cr_bocatt_vocalize.snd", // 458
			"cr_bolle_bol_attack_hvy.snd", // 459
			"cr_bolle_bol_attack_lgt.snd", // 460
			"cr_bolle_bol_calf_attack_hvy.snd", // 461
			"cr_bolle_bol_calf_attack_lgt.snd", // 462
			"cr_bolle_bol_calf_hit_hvy.snd", // 463
			"cr_bolle_bol_calf_hit_lgt.snd", // 464
			"cr_bolle_bol_calf_vocalize.snd", // 465
			"cr_bolle_bol_hit_hvy.snd", // 466
			"cr_bolle_bol_hit_lgt.snd", // 467
			"cr_bolle_bol_vocalize.snd", // 468
			"cr_bolma_attack_hvy.snd", // 469
			"cr_bolma_attack_light.snd", // 470
			"cr_bolma_calf_attack_hvy.snd", // 471
			"cr_bolma_calf_attack_lgt.snd", // 472
			"cr_bolma_calf_hit_hvy.snd", // 473
			"cr_bolma_calf_hit_lgt.snd", // 474
			"cr_bolma_calf_vocalize.snd", // 475
			"cr_bolma_hit_hvy.snd", // 476
			"cr_bolma_hit_lgt.snd", // 477
			"cr_bolma_vocalize.snd", // 478
			"cr_bolma_youth_attack_hvy.snd", // 479
			"cr_bolma_youth_attack_lgt.snd", // 480
			"cr_bolma_youth_hit_hvy.snd", // 481
			"cr_bolma_youth_hit_lgt.snd", // 482
			"cr_bolma_youth_vocalize.snd", // 483
			"cr_bol_attack_hvy.snd", // 484
			"cr_bol_attack_light.snd", // 485
			"cr_bol_hit_hvy.snd", // 486
			"cr_bol_hit_lgt.snd", // 487
			"cr_bol_pack_runner_attack_hvy.snd", // 488
			"cr_bol_pack_runner_attack_lgt.snd", // 489
			"cr_bol_pack_runner_hit_hvy.snd", // 490
			"cr_bol_pack_runner_hit_lgt.snd", // 491
			"cr_bol_pack_runner_vocalize.snd", // 492
			"cr_bol_vocalize.snd", // 493
			"cr_bordok_attack_hvy.snd", // 494
			"cr_bordok_attack_lgt.snd", // 495
			"cr_bordok_emote_vocalize_1.snd", // 496
			"cr_bordok_emote_vocalize_2.snd", // 497
			"cr_bordok_emote_vocalize_3.snd", // 498
			"cr_bordok_foal_attack_hvy.snd", // 499
			"cr_bordok_foal_attack_lgt.snd", // 500
			"cr_bordok_foal_hit_hvy.snd", // 501
			"cr_bordok_foal_hit_lgt.snd", // 502
			"cr_bordok_foal_vocalize.snd", // 503
			"cr_bordok_hit_heavy.snd", // 504
			"cr_bordok_hit_light.snd", // 505
			"cr_borgle_attack_heavy.snd", // 506
			"cr_borgle_attack_light.snd", // 507
			"cr_borgle_emote_vocalize.snd", // 508
			"cr_borgle_hit_heavy.snd", // 509
			"cr_borgle_hit_light.snd", // 510
			"cr_brackaset_attack_heavy.snd", // 511
			"cr_brackaset_attack_lgt.snd", // 512
			"cr_brackaset_hit_hvy.snd", // 513
			"cr_brackaset_hit_lgt.snd", // 514
			"cr_brackaset_vocalize.snd", // 515
			"cr_bull_rancor_attack_hvy.snd", // 516
			"cr_bull_rancor_attack_lgt.snd", // 517
			"cr_bull_rancor_hit_hvy.snd", // 518
			"cr_bull_rancor_hit_lgt.snd", // 519
			"cr_bull_rancor_vocalize.snd", // 520
			"cr_canoid_attack_hvy.snd", // 521
			"cr_canoid_attack_lgt.snd", // 522
			"cr_canoid_hit_hvy.snd", // 523
			"cr_canoid_hit_lgt.snd", // 524
			"cr_canoid_pack_leader_attack_hvy.snd", // 525
			"cr_canoid_pack_leader_attack_lgt.snd", // 526
			"cr_canoid_pack_leader_hit_hvy.snd", // 527
			"cr_canoid_pack_leader_hit_lgt.snd", // 528
			"cr_canoid_pack_leader_vocalize.snd", // 529
			"cr_canoid_vocalize.snd", // 530
			"cr_canyon_krayt_dragon_attack_hvy.snd", // 531
			"cr_canyon_krayt_dragon_attack_lgt.snd", // 532
			"cr_canyon_krayt_dragon_hit_hvy.snd", // 533
			"cr_canyon_krayt_dragon_hit_lgt.snd", // 534
			"cr_canyon_krayt_dragon_vocalize.snd", // 535
			"cr_capperspineflap_attack_heavy.snd", // 536
			"cr_capperspineflap_attack_light.snd", // 537
			"cr_capperspineflap_hit_heavy.snd", // 538
			"cr_capperspineflap_hit_light.snd", // 539
			"cr_capperspineflap_vocalize.snd", // 540
			"cr_carrion_spat_attack_hvy.snd", // 541
			"cr_carrion_spat_attack_lgt.snd", // 542
			"cr_carrion_spat_hit_hvy.snd", // 543
			"cr_carrion_spat_hit_lgt.snd", // 544
			"cr_carrion_spat_vocalize.snd", // 545
			"cr_choku_attack_heavy.snd", // 546
			"cr_choku_attack_light.snd", // 547
			"cr_choku_hit_hvy.snd", // 548
			"cr_choku_hit_lgt.snd", // 549
			"cr_choku_pup_attack_hvy.snd", // 550
			"cr_choku_pup_attack_lgt.snd", // 551
			"cr_choku_pup_hit_hvy.snd", // 552
			"cr_choku_pup_hit_lgt.snd", // 553
			"cr_choku_pup_vocalize.snd", // 554
			"cr_choku_vocalize.snd", // 555
			"cr_chuba_attack_hvy.snd", // 556
			"cr_chuba_attack_lgt.snd", // 557
			"cr_chuba_emote_vocalize.snd", // 558
			"cr_chuba_hit_heavy.snd", // 559
			"cr_chuba_hit_light.snd", // 560
			"cr_clipped_fynock_attack_hvy.snd", // 561
			"cr_clipped_fynock_attack_lgt.snd", // 562
			"cr_clipped_fynock_hit_hvy.snd", // 563
			"cr_clipped_fynock_hit_lgt.snd", // 564
			"cr_clipped_fynock_vocalize.snd", // 565
			"cr_condordragon_attack_light.snd", // 566
			"cr_condordragon_cbt_hvy.snd", // 567
			"cr_condordragon_emote_vocalize.snd", // 568
			"cr_condordragon_hit_heavy.snd", // 569
			"cr_condordragon_hit_light.snd", // 570
			"cr_condordragon_hover.snd", // 571
			"cr_corel_butterfly_attack_hvy.snd", // 572
			"cr_corel_butterfly_attack_lgt.snd", // 573
			"cr_corel_butterfly_defender_attack_hvy.snd", // 574
			"cr_corel_butterfly_defender_attack_lgt.snd", // 575
			"cr_corel_butterfly_defender_hit_hvy.snd", // 576
			"cr_corel_butterfly_defender_hit_lgt.snd", // 577
			"cr_corel_butterfly_defender_vocalize.snd", // 578
			"cr_corel_butterfly_hit_hvy.snd", // 579
			"cr_corel_butterfly_hit_lgt.snd", // 580
			"cr_corel_butterfly_monarch_attack_hvy.snd", // 581
			"cr_corel_butterfly_monarch_attack_lgt.snd", // 582
			"cr_corel_butterfly_monarch_hit_hvy.snd", // 583
			"cr_corel_butterfly_monarch_hit_lgt.snd", // 584
			"cr_corel_butterfly_monarch_vocalize.snd", // 585
			"cr_crazed_durni_attack_hvy.snd", // 586
			"cr_crazed_durni_attack_lgt.snd", // 587
			"cr_crazed_durni_hit_hvy.snd", // 588
			"cr_crazed_durni_hit_lgt.snd", // 589
			"cr_crazed_durni_vocalize.snd", // 590
			"cr_crimson_sand_panther_attack_hvy.snd", // 591
			"cr_crimson_sand_panther_attack_lgt.snd", // 592
			"cr_crimson_sand_panther_hit_hvy.snd", // 593
			"cr_crimson_sand_panther_hit_lgt.snd", // 594
			"cr_crimson_sand_panther_vocalize.snd", // 595
			"cr_crystalsnake_attack_hvy.snd", // 596
			"cr_crystalsnake_attack_light.snd", // 597
			"cr_crystalsnake_hit_heavy.snd", // 598
			"cr_crystalsnake_hit_light.snd", // 599
			"cr_crystalsnake_run.snd", // 600
			"cr_crystalsnake_vocalize.snd", // 601
			"cr_crystalsnake_walk.snd", // 602
			"cr_cupa_attack_light.snd", // 603
			"cr_cupa_emote_vocalize.snd", // 604
			"cr_cupa_hit_heavy.snd", // 605
			"cr_cupa_hit_light.snd", // 606
			"cr_cupa_idle.snd", // 607
			"cr_cupa_run.snd", // 608
			"cr_cupa_walk.snd", // 609
			"cr_dalyrake_attack_heavy.snd", // 610
			"cr_dalyrake_attack_light.snd", // 611
			"cr_dalyrake_hit_heavy.snd", // 612
			"cr_dalyrake_hit_light.snd", // 613
			"cr_dalyrake_matriarch_attack_hvy.snd", // 614
			"cr_dalyrake_matriarch_attack_lgt.snd", // 615
			"cr_dalyrake_matriarch_hit_hvy.snd", // 616
			"cr_dalyrake_matriarch_hit_lgt.snd", // 617
			"cr_dalyrake_matriarch_vocalize.snd", // 618
			"cr_dalyrake_vocalize.snd", // 619
			"cr_dappled_gualama_attack_hvy.snd", // 620
			"cr_dappled_gualama_attack_lgt.snd", // 621
			"cr_dappled_gualama_hit_hvy.snd", // 622
			"cr_dappled_gualama_hit_lgt.snd", // 623
			"cr_dappled_gualama_vocalize.snd", // 624
			"cr_decay_mite_attack_hvy.snd", // 625
			"cr_decay_mite_attack_lgt.snd", // 626
			"cr_decay_mite_hit_hvy.snd", // 627
			"cr_decay_mite_hit_lgt.snd", // 628
			"cr_decay_mite_vocalize.snd", // 629
			"cr_deranged_wrix_attack_hvy.snd", // 630
			"cr_deranged_wrix_attack_lgt.snd", // 631
			"cr_deranged_wrix_hit_hvy.snd", // 632
			"cr_deranged_wrix_hit_lgt.snd", // 633
			"cr_deranged_wrix_vocalize.snd", // 634
			"cr_devil_gulginaw_attack_hvy.snd", // 635
			"cr_devil_gulginaw_attack_lgt.snd", // 636
			"cr_devil_gulginaw_hit_hvy.snd", // 637
			"cr_devil_gulginaw_hit_lgt.snd", // 638
			"cr_devil_gulginaw_vocalize.snd", // 639
			"cr_dewback_attack_heavy.snd", // 640
			"cr_dewback_attack_light.snd", // 641
			"cr_dewback_emote_vocalize.snd", // 642
			"cr_dewback_hit_heavy.snd", // 643
			"cr_dewback_hit_light.snd", // 644
			"cr_dewback_idle_breathe.snd", // 645
			"cr_dire_cat_attack_hvy.snd", // 646
			"cr_dire_cat_attack_lgt.snd", // 647
			"cr_dire_cat_hit_hvy.snd", // 648
			"cr_dire_cat_hit_lgt.snd", // 649
			"cr_dire_cat_vocalize.snd", // 650
			"cr_dragonet_attack_hvy.snd", // 651
			"cr_dragonet_attack_lgt.snd", // 652
			"cr_dragonet_hit_hvy.snd", // 653
			"cr_dragonet_hit_lgt.snd", // 654
			"cr_dragonet_vocalize.snd", // 655
			"cr_dune_lizard_attack_hvy.snd", // 656
			"cr_dune_lizard_attack_lgt.snd", // 657
			"cr_dune_lizard_hit_hvy.snd", // 658
			"cr_dune_lizard_hit_lgt.snd", // 659
			"cr_dune_lizard_vocalize.snd", // 660
			"cr_dung_mite_attack_hvy.snd", // 661
			"cr_dung_mite_attack_lgt.snd", // 662
			"cr_dung_mite_hit_hvy.snd", // 663
			"cr_dung_mite_hit_lgt.snd", // 664
			"cr_dung_mite_vocalize.snd", // 665
			"cr_durni_attack_hvy.snd", // 666
			"cr_durni_attack_light.snd", // 667
			"cr_durni_emote_vocalize.snd", // 668
			"cr_durni_hit_heavy.snd", // 669
			"cr_durni_hit_light.snd", // 670
			"cr_dwarfnuna_attack_heavy.snd", // 671
			"cr_dwarfnuna_attack_light.snd", // 672
			"cr_dwarfnuna_emote_vocalize.snd", // 673
			"cr_dwarfnuna_hit_heavy.snd", // 674
			"cr_dwarfnuna_hit_light.snd", // 675
			"cr_dwarf_bantha_attack_hvy.snd", // 676
			"cr_dwarf_bantha_attack_lgt.snd", // 677
			"cr_dwarf_bantha_hit_hvy.snd", // 678
			"cr_dwarf_bantha_hit_lgt.snd", // 679
			"cr_dwarf_bantha_vocalize.snd", // 680
			"cr_dwarf_eopie_attack_hvy.snd", // 681
			"cr_dwarf_eopie_attack_lgt.snd", // 682
			"cr_dwarf_eopie_hit_hvy.snd", // 683
			"cr_dwarf_eopie_hit_lgt.snd", // 684
			"cr_dwarf_eopie_vocalize.snd", // 685
			"cr_dwarf_gronda_attack_hvy.snd", // 686
			"cr_dwarf_gronda_attack_lgt.snd", // 687
			"cr_dwarf_gronda_hit_hvy.snd", // 688
			"cr_dwarf_gronda_hit_lgt.snd", // 689
			"cr_dwarf_gronda_vocalize.snd", // 690
			"cr_elder_hanadak_matriarch_attack_hvy.snd", // 691
			"cr_elder_hanadak_matriarch_attack_lgt.snd", // 692
			"cr_elder_hanadak_matriarch_hit_hvy.snd", // 693
			"cr_elder_hanadak_matriarch_hit_lgt.snd", // 694
			"cr_elder_hanadak_matriarch_vocalize.snd", // 695
			"cr_elder_pugoriss_attack_hvy.snd", // 696
			"cr_elder_pugoriss_attack_lgt.snd", // 697
			"cr_elder_pugoriss_hit_hvy.snd", // 698
			"cr_elder_pugoriss_hit_lgt.snd", // 699
			"cr_elder_pugoriss_vocalize.snd", // 700
			"cr_elder_snorbal_female_attack_hvy.snd", // 701
			"cr_elder_snorbal_female_attack_lgt.snd", // 702
			"cr_elder_snorbal_female_hit_hvy.snd", // 703
			"cr_elder_snorbal_female_hit_lgt.snd", // 704
			"cr_elder_snorbal_female_vocalize.snd", // 705
			"cr_elder_snorbal_male_attack_hvy.snd", // 706
			"cr_elder_snorbal_male_attack_lgt.snd", // 707
			"cr_elder_snorbal_male_hit_hvy.snd", // 708
			"cr_elder_snorbal_male_hit_lgt.snd", // 709
			"cr_elder_snorbal_male_vocalize.snd", // 710
			"cr_enraged_wood_mite_king_attack_hvy.snd", // 711
			"cr_enraged_wood_mite_king_attack_lgt.snd", // 712
			"cr_enraged_wood_mite_king_hit_hvy.snd", // 713
			"cr_enraged_wood_mite_king_hit_lgt.snd", // 714
			"cr_enraged_wood_mite_king_vocalize.snd", // 715
			"cr_eopie_attack_hvy.snd", // 716
			"cr_eopie_attack_lgt.snd", // 717
			"cr_eopie_hit_hvy.snd", // 718
			"cr_eopie_hit_lgt.snd", // 719
			"cr_eopie_vocalize.snd", // 720
			"cr_falumpaset_attack_hvy.snd", // 721
			"cr_falumpaset_attack_lgt.snd", // 722
			"cr_falumpaset_hit_heavy.snd", // 723
			"cr_falumpaset_hit_light.snd", // 724
			"cr_falumpaset_idle_breathe.snd", // 725
			"cr_falumpaset_vocalize.snd", // 726
			"cr_fambaa_attack_hvy.snd", // 727
			"cr_fambaa_emote_vocalize.snd", // 728
			"cr_fambaa_hit_heavy.snd", // 729
			"cr_fambaa_hit_light.snd", // 730
			"cr_fearful_fynock_youth_attack_hvy.snd", // 731
			"cr_fearful_fynock_youth_attack_lgt.snd", // 732
			"cr_fearful_fynock_youth_hit_hvy.snd", // 733
			"cr_fearful_fynock_youth_hit_lgt.snd", // 734
			"cr_fearful_fynock_youth_vocalize.snd", // 735
			"cr_female_dire_cat_attack_hvy.snd", // 736
			"cr_female_dire_cat_attack_lgt.snd", // 737
			"cr_female_dire_cat_hit_hvy.snd", // 738
			"cr_female_dire_cat_hit_lgt.snd", // 739
			"cr_female_dire_cat_vocalize.snd", // 740
			"cr_female_snorbal_calf_attack_hvy.snd", // 741
			"cr_female_snorbal_calf_attack_lgt.snd", // 742
			"cr_female_snorbal_calf_hit_hvy.snd", // 743
			"cr_female_snorbal_calf_hit_lgt.snd", // 744
			"cr_female_snorbal_calf_vocalize.snd", // 745
			"cr_female_swamp_tusk_cat_attack_hvy.snd", // 746
			"cr_female_swamp_tusk_cat_attack_lgt.snd", // 747
			"cr_female_swamp_tusk_cat_hit_hvy.snd", // 748
			"cr_female_swamp_tusk_cat_hit_lgt.snd", // 749
			"cr_female_swamp_tusk_cat_vocalize.snd", // 750
			"cr_fem_grass_slice_hound_attack_hvy.snd", // 751
			"cr_fem_grass_slice_hound_attack_lgt.snd", // 752
			"cr_fem_grass_slice_hound_hit_hvy.snd", // 753
			"cr_fem_grass_slice_hound_hit_lgt.snd", // 754
			"cr_fem_grass_slice_hound_vocalize.snd", // 755
			"cr_finch_attack_lgt.snd", // 756
			"cr_finch_hit_hvy.snd", // 757
			"cr_finch_hit_lgt.snd", // 758
			"cr_flewt_attack_hvy.snd", // 759
			"cr_flewt_attack_lgt.snd", // 760
			"cr_flewt_hit_hvy.snd", // 761
			"cr_flewt_hit_lgt.snd", // 762
			"cr_flewt_vocalize.snd", // 763
			"cr_flit_attack_heavy.snd", // 764
			"cr_flit_attack_light.snd", // 765
			"cr_flit_emote_vocalize.snd", // 766
			"cr_flit_hit_heavy.snd", // 767
			"cr_flit_hit_light.snd", // 768
			"cr_flit_youth_attack_hvy.snd", // 769
			"cr_flit_youth_attack_lgt.snd", // 770
			"cr_flit_youth_hit_hvy.snd", // 771
			"cr_flit_youth_hit_lgt.snd", // 772
			"cr_flit_youth_vocalize.snd", // 773
			"cr_forest_mite_attack_hvy.snd", // 774
			"cr_forest_mite_attack_lgt.snd", // 775
			"cr_forest_mite_hit_hvy.snd", // 776
			"cr_forest_mite_hit_lgt.snd", // 777
			"cr_forest_mite_vocalize.snd", // 778
			"cr_forest_murra_attack_hvy.snd", // 779
			"cr_forest_murra_attack_lgt.snd", // 780
			"cr_forest_murra_hit_hvy.snd", // 781
			"cr_forest_murra_hit_lgt.snd", // 782
			"cr_forest_murra_vocalize.snd", // 783
			"cr_forest_slice_hound_attack_hvy.snd", // 784
			"cr_forest_slice_hound_attack_lgt.snd", // 785
			"cr_forest_slice_hound_hit_hvy.snd", // 786
			"cr_forest_slice_hound_hit_lgt.snd", // 787
			"cr_forest_slice_hound_vocalize.snd", // 788
			"cr_frenzied_graul_attack_hvy.snd", // 789
			"cr_frenzied_graul_attack_lgt.snd", // 790
			"cr_frenzied_graul_hit_hvy.snd", // 791
			"cr_frenzied_graul_hit_lgt.snd", // 792
			"cr_frenzied_graul_vocalize.snd", // 793
			"cr_frightened_young_flewt_attack_hvy.snd", // 794
			"cr_frightened_young_flewt_attack_lgt.snd", // 795
			"cr_frightened_young_flewt_hit_hvy.snd", // 796
			"cr_frightened_young_flewt_hit_lgt.snd", // 797
			"cr_frightened_young_flewt_vocalize.snd", // 798
			"cr_fynock_attack_hvy.snd", // 799
			"cr_fynock_attack_lgt.snd", // 800
			"cr_fynock_hit_hvy.snd", // 801
			"cr_fynock_hit_lgt.snd", // 802
			"cr_fynock_vocalize.snd", // 803
			"cr_gacklebat_attack_heavy.snd", // 804
			"cr_gacklebat_attack_light.snd", // 805
			"cr_gacklebat_emote_vocalize.snd", // 806
			"cr_gacklebat_fly.snd", // 807
			"cr_gacklebat_hit_heavy.snd", // 808
			"cr_gacklebat_hit_light.snd", // 809
			"cr_gacklebat_idle.snd", // 810
			"cr_gaping_spider_attack_hvy.snd", // 811
			"cr_gaping_spider_attack_lgt.snd", // 812
			"cr_gaping_spider_broodling_attack_hvy.snd", // 813
			"cr_gaping_spider_broodling_attack_lgt.snd", // 814
			"cr_gaping_spider_broodling_hit_hvy.snd", // 815
			"cr_gaping_spider_broodling_hit_lgt.snd", // 816
			"cr_gaping_spider_broodling_vocalize.snd", // 817
			"cr_gaping_spider_hit_hvy.snd", // 818
			"cr_gaping_spider_hit_lgt.snd", // 819
			"cr_gaping_spider_queen_attack_hvy.snd", // 820
			"cr_gaping_spider_queen_attack_lgt.snd", // 821
			"cr_gaping_spider_queen_hit_hvy.snd", // 822
			"cr_gaping_spider_queen_hit_lgt.snd", // 823
			"cr_gaping_spider_queen_vocalize.snd", // 824
			"cr_gaping_spider_recluse_attack_hvy.snd", // 825
			"cr_gaping_spider_recluse_attack_lgt.snd", // 826
			"cr_gaping_spider_recluse_hit_hvy.snd", // 827
			"cr_gaping_spider_recluse_hit_lgt.snd", // 828
			"cr_gaping_spider_recluse_vocalize.snd", // 829
			"cr_gaping_spider_vocalize.snd", // 830
			"cr_generic_flying_medium_bodyfall.snd", // 831
			"cr_generic_flying_small_bodyfall.snd", // 832
			"cr_generic_flying_wingflap_fast.snd", // 833
			"cr_generic_flying_wingflap_slow.snd", // 834
			"cr_generic_foley_impact_heavy.snd", // 835
			"cr_generic_foley_impact_light.snd", // 836
			"cr_generic_giant_bodyfall.snd", // 837
			"cr_generic_huge_bodyfall.snd", // 838
			"cr_generic_huge_fs_walk_01.snd", // 839
			"cr_generic_huge_fs_walk_02.snd", // 840
			"cr_generic_huge_fs_walk_03.snd", // 841
			"cr_generic_large_attack_heavy.snd", // 842
			"cr_generic_large_attack_light.snd", // 843
			"cr_generic_large_bite_heavy.snd", // 844
			"cr_generic_large_bite_light.snd", // 845
			"cr_generic_large_bodyfall.snd", // 846
			"cr_generic_large_fs_run.snd", // 847
			"cr_generic_large_fs_walk.snd", // 848
			"cr_generic_large_fs_walk_02.snd", // 849
			"cr_generic_large_fs_walk_03.snd", // 850
			"cr_generic_large_fs_walk_04.snd", // 851
			"cr_generic_medium_bodyfall.snd", // 852
			"cr_generic_medium_fs_run.snd", // 853
			"cr_generic_medium_fs_walk.snd", // 854
			"cr_generic_medium_jump.snd", // 855
			"cr_generic_small_attack_heavy.snd", // 856
			"cr_generic_small_attack_light.snd", // 857
			"cr_generic_small_bite_heavy.snd", // 858
			"cr_generic_small_bite_light.snd", // 859
			"cr_generic_small_bodyfall.snd", // 860
			"cr_generic_small_fs_run.snd", // 861
			"cr_generic_small_fs_walk.snd", // 862
			"cr_generic_small_lick.snd", // 863
			"cr_generic_small_scratch.snd", // 864
			"cr_giant_angler_attack_hvy.snd", // 865
			"cr_giant_angler_attack_lgt.snd", // 866
			"cr_giant_angler_hit_hvy.snd", // 867
			"cr_giant_angler_hit_lgt.snd", // 868
			"cr_giant_angler_vocalize.snd", // 869
			"cr_giant_baz_nitch_attack_hvy.snd", // 870
			"cr_giant_baz_nitch_attack_lgt.snd", // 871
			"cr_giant_baz_nitch_hit_hvy.snd", // 872
			"cr_giant_baz_nitch_hit_lgt.snd", // 873
			"cr_giant_baz_nitch_vocalize.snd", // 874
			"cr_giant_carrion_spat_attack_hvy.snd", // 875
			"cr_giant_carrion_spat_attack_lgt.snd", // 876
			"cr_giant_carrion_spat_hit_hvy.snd", // 877
			"cr_giant_carrion_spat_hit_lgt.snd", // 878
			"cr_giant_carrion_spat_vocalize.snd", // 879
			"cr_giant_crystalsnake_attack_hvy.snd", // 880
			"cr_giant_crystalsnake_attack_lgt.snd", // 881
			"cr_giant_crystalsnake_hit_hvy.snd", // 882
			"cr_giant_crystalsnake_hit_lgt.snd", // 883
			"cr_giant_crystalsnake_vocalize.snd", // 884
			"cr_giant_dalyrake_attack_hvy.snd", // 885
			"cr_giant_dalyrake_attack_lgt.snd", // 886
			"cr_giant_dalyrake_hit_hvy.snd", // 887
			"cr_giant_dalyrake_hit_lgt.snd", // 888
			"cr_giant_dalyrake_vocalize.snd", // 889
			"cr_giant_dune_kimogila_attack_hvy.snd", // 890
			"cr_giant_dune_kimogila_attack_lgt.snd", // 891
			"cr_giant_dune_kimogila_hit_hvy.snd", // 892
			"cr_giant_dune_kimogila_hit_lgt.snd", // 893
			"cr_giant_dune_kimogila_vocalize.snd", // 894
			"cr_giant_flit_attack_hvy.snd", // 895
			"cr_giant_flit_attack_lgt.snd", // 896
			"cr_giant_flit_hit_hvy.snd", // 897
			"cr_giant_flit_hit_lgt.snd", // 898
			"cr_giant_flit_vocalize.snd", // 899
			"cr_giant_fs_dirt.snd", // 900
			"cr_giant_fs_mud.snd", // 901
			"cr_giant_fs_water.snd", // 902
			"cr_giant_gacklebat_attack_hvy.snd", // 903
			"cr_giant_gacklebat_attack_lgt.snd", // 904
			"cr_giant_gacklebat_hit_hvy.snd", // 905
			"cr_giant_gacklebat_hit_lgt.snd", // 906
			"cr_giant_gacklebat_vocalize.snd", // 907
			"cr_giant_gaping_spider_attack_hvy.snd", // 908
			"cr_giant_gaping_spider_attack_lgt.snd", // 909
			"cr_giant_gaping_spider_hit_hvy.snd", // 910
			"cr_giant_gaping_spider_hit_lgt.snd", // 911
			"cr_giant_gaping_spider_vocalize.snd", // 912
			"cr_giant_gubbur_attack_hvy.snd", // 913
			"cr_giant_gubbur_attack_lgt.snd", // 914
			"cr_giant_gubbur_hit_hvy.snd", // 915
			"cr_giant_gubbur_hit_lgt.snd", // 916
			"cr_giant_gubbur_vocalize.snd", // 917
			"cr_giant_hermit_spider_attack_hvy.snd", // 918
			"cr_giant_hermit_spider_attack_lgt.snd", // 919
			"cr_giant_hermit_spider_hit_hvy.snd", // 920
			"cr_giant_hermit_spider_hit_lgt.snd", // 921
			"cr_giant_hermit_spider_vocalize.snd", // 922
			"cr_giant_horned_krevol_attack_hvy.snd", // 923
			"cr_giant_horned_krevol_attack_lgt.snd", // 924
			"cr_giant_horned_krevol_hit_hvy.snd", // 925
			"cr_giant_horned_krevol_hit_lgt.snd", // 926
			"cr_giant_horned_krevol_vocalize.snd", // 927
			"cr_giant_kimogila_attack_hvy.snd", // 928
			"cr_giant_kimogila_attack_lgt.snd", // 929
			"cr_giant_kimogila_hit_hvy.snd", // 930
			"cr_giant_kimogila_hit_lgt.snd", // 931
			"cr_giant_kimogila_vocalize.snd", // 932
			"cr_giant_mawgax_attack_hvy.snd", // 933
			"cr_giant_mawgax_attack_lgt.snd", // 934
			"cr_giant_mawgax_hit_hvy.snd", // 935
			"cr_giant_mawgax_hit_lgt.snd", // 936
			"cr_giant_mawgax_vocalize.snd", // 937
			"cr_giant_pekopeko_attack_hvy.snd", // 938
			"cr_giant_pekopeko_attack_lgt.snd", // 939
			"cr_giant_pekopeko_hit_hvy.snd", // 940
			"cr_giant_pekopeko_hit_lgt.snd", // 941
			"cr_giant_pekopeko_vocalize.snd", // 942
			"cr_giant_pharple_attack_hvy.snd", // 943
			"cr_giant_pharple_attack_lgt.snd", // 944
			"cr_giant_pharple_hit_hvy.snd", // 945
			"cr_giant_pharple_hit_lgt.snd", // 946
			"cr_giant_pharple_vocalize.snd", // 947
			"cr_giant_sand_beetle_attack_hvy.snd", // 948
			"cr_giant_sand_beetle_attack_lgt.snd", // 949
			"cr_giant_sand_beetle_hit_hvy.snd", // 950
			"cr_giant_sand_beetle_hit_lgt.snd", // 951
			"cr_giant_sand_beetle_vocalize.snd", // 952
			"cr_giant_spider_attack_hvy.snd", // 953
			"cr_giant_spider_attack_lgt.snd", // 954
			"cr_giant_spider_hit_hvy.snd", // 955
			"cr_giant_spider_hit_lgt.snd", // 956
			"cr_giant_spider_vocalize.snd", // 957
			"cr_giant_spined_puc_attack_hvy.snd", // 958
			"cr_giant_spined_puc_attack_lgt.snd", // 959
			"cr_giant_spined_puc_hit_hvy.snd", // 960
			"cr_giant_spined_puc_hit_lgt.snd", // 961
			"cr_giant_spined_puc_vocalize.snd", // 962
			"cr_giant_spined_snake_attack_hvy.snd", // 963
			"cr_giant_spined_snake_attack_lgt.snd", // 964
			"cr_giant_spined_snake_hit_hvy.snd", // 965
			"cr_giant_spined_snake_hit_lgt.snd", // 966
			"cr_giant_spined_snake_vocalize.snd", // 967
			"cr_giant_stintaril_attack_hvy.snd", // 968
			"cr_giant_stintaril_attack_lgt.snd", // 969
			"cr_giant_stintaril_hit_hvy.snd", // 970
			"cr_giant_stintaril_hit_lgt.snd", // 971
			"cr_giant_stintaril_vocalize.snd", // 972
			"cr_giant_tanc_mite_attack_hvy.snd", // 973
			"cr_giant_tanc_mite_attack_lgt.snd", // 974
			"cr_giant_tanc_mite_hit_hvy.snd", // 975
			"cr_giant_tanc_mite_hit_lgt.snd", // 976
			"cr_giant_tanc_mite_vocalize.snd", // 977
			"cr_giant_veermok_attack_hvy.snd", // 978
			"cr_giant_veermok_attack_lgt.snd", // 979
			"cr_giant_veermok_hit_hvy.snd", // 980
			"cr_giant_veermok_hit_lgt.snd", // 981
			"cr_giant_veermok_vocalize.snd", // 982
			"cr_giant_worrt_attack_hvy.snd", // 983
			"cr_giant_worrt_attack_lgt.snd", // 984
			"cr_giant_worrt_hit_hvy.snd", // 985
			"cr_giant_worrt_hit_lgt.snd", // 986
			"cr_giant_worrt_vocalize.snd", // 987
			"cr_glutted_fynock_queen_attack_hvy.snd", // 988
			"cr_glutted_fynock_queen_attack_lgt.snd", // 989
			"cr_glutted_fynock_queen_hit_hvy.snd", // 990
			"cr_glutted_fynock_queen_hit_lgt.snd", // 991
			"cr_glutted_fynock_queen_vocalize.snd", // 992
			"cr_gnarled_bark_mite_queen_attack_hvy.snd", // 993
			"cr_gnarled_bark_mite_queen_attack_lgt.snd", // 994
			"cr_gnarled_bark_mite_queen_hit_hvy.snd", // 995
			"cr_gnarled_bark_mite_queen_hit_lgt.snd", // 996
			"cr_gnarled_bark_mite_queen_vocalize.snd", // 997
			"cr_gnort_attack_hvy.snd", // 998
			"cr_gnort_attack_lgt.snd", // 999
			"cr_gnort_hit_hvy.snd", // 1000
			"cr_gnort_hit_lgt.snd", // 1001
			"cr_gnort_vocalize.snd", // 1002
			"cr_gorg_attack_hvy.snd", // 1003
			"cr_gorg_attack_lgt.snd", // 1004
			"cr_gorg_hit_hvy.snd", // 1005
			"cr_gorg_hit_lgt.snd", // 1006
			"cr_gorg_vocalize.snd", // 1007
			"cr_grand_wrix_attack_hvy.snd", // 1008
			"cr_grand_wrix_attack_lgt.snd", // 1009
			"cr_grand_wrix_hit_hvy.snd", // 1010
			"cr_grand_wrix_hit_lgt.snd", // 1011
			"cr_grand_wrix_vocalize.snd", // 1012
			"cr_grass_voritor_tracker_attack_hvy.snd", // 1013
			"cr_grass_voritor_tracker_attack_lgt.snd", // 1014
			"cr_grass_voritor_tracker_hit_hvy.snd", // 1015
			"cr_grass_voritor_tracker_hit_lgt.snd", // 1016
			"cr_grass_voritor_tracker_vocalize.snd", // 1017
			"cr_graul_attack_hvy.snd", // 1018
			"cr_graul_attack_lgt.snd", // 1019
			"cr_graul_hit_hvy.snd", // 1020
			"cr_graul_hit_lgt.snd", // 1021
			"cr_graul_vocalize.snd", // 1022
			"cr_greater_desert_womprat_attack_hvy.snd", // 1023
			"cr_greater_desert_womprat_attack_lgt.snd", // 1024
			"cr_greater_desert_womprat_hit_hvy.snd", // 1025
			"cr_greater_desert_womprat_hit_lgt.snd", // 1026
			"cr_greater_desert_womprat_vocalize.snd", // 1027
			"cr_greater_gulginaw_attack_hvy.snd", // 1028
			"cr_greater_gulginaw_attack_lgt.snd", // 1029
			"cr_greater_gulginaw_hit_hvy.snd", // 1030
			"cr_greater_gulginaw_hit_lgt.snd", // 1031
			"cr_greater_gulginaw_vocalize.snd", // 1032
			"cr_greater_shaupaut_attack_hvy.snd", // 1033
			"cr_greater_shaupaut_attack_lgt.snd", // 1034
			"cr_greater_shaupaut_hit_hvy.snd", // 1035
			"cr_greater_shaupaut_hit_lgt.snd", // 1036
			"cr_greater_shaupaut_vocalize.snd", // 1037
			"cr_greater_sludge_panther_attack_hvy.snd", // 1038
			"cr_greater_sludge_panther_attack_lgt.snd", // 1039
			"cr_greater_sludge_panther_hit_hvy.snd", // 1040
			"cr_greater_sludge_panther_hit_lgt.snd", // 1041
			"cr_greater_sludge_panther_vocalize.snd", // 1042
			"cr_great_borgle_attack_hvy.snd", // 1043
			"cr_great_borgle_attack_lgt.snd", // 1044
			"cr_great_borgle_hit_hvy.snd", // 1045
			"cr_great_borgle_hit_lgt.snd", // 1046
			"cr_great_borgle_vocalize.snd", // 1047
			"cr_gronda_attack_hvy.snd", // 1048
			"cr_gronda_attack_lgt.snd", // 1049
			"cr_gronda_hit_hvy.snd", // 1050
			"cr_gronda_hit_lgt.snd", // 1051
			"cr_gronda_vocalize.snd", // 1052
			"cr_gualama_attack_hvy.snd", // 1053
			"cr_gualama_attack_lgt.snd", // 1054
			"cr_gualama_emote_vocalize.snd", // 1055
			"cr_gualama_hit_heavy.snd", // 1056
			"cr_gualama_hit_light.snd", // 1057
			"cr_gualama_patriarch_attack_hvy.snd", // 1058
			"cr_gualama_patriarch_attack_lgt.snd", // 1059
			"cr_gualama_patriarch_hit_hvy.snd", // 1060
			"cr_gualama_patriarch_hit_lgt.snd", // 1061
			"cr_gualama_patriarch_vocalize.snd", // 1062
			"cr_gubbur_attack_hvy.snd", // 1063
			"cr_gubbur_attack_lgt.snd", // 1064
			"cr_gubbur_hit_heavy.snd", // 1065
			"cr_gubbur_hit_light.snd", // 1066
			"cr_gubbur_vocalize.snd", // 1067
			"cr_guf_drolg_attack_hvy.snd", // 1068
			"cr_guf_drolg_attack_light.snd", // 1069
			"cr_guf_drolg_hit_hvy.snd", // 1070
			"cr_guf_drolg_hit_lgt.snd", // 1071
			"cr_guf_drolg_vocalize.snd", // 1072
			"cr_gulginaw_attack_hvy.snd", // 1073
			"cr_gulginaw_attack_lgt.snd", // 1074
			"cr_gulginaw_hit_hvy.snd", // 1075
			"cr_gulginaw_hit_lgt.snd", // 1076
			"cr_gulginaw_vocalize.snd", // 1077
			"cr_gurk_attack_hvy.snd", // 1078
			"cr_gurk_attack_lgt.snd", // 1079
			"cr_gurk_hit_hvy.snd", // 1080
			"cr_gurk_hit_lgt.snd", // 1081
			"cr_gurk_vocalize.snd", // 1082
			"cr_gurk_whelp_attack_hvy.snd", // 1083
			"cr_gurk_whelp_attack_lgt.snd", // 1084
			"cr_gurk_whelp_hit_hvy.snd", // 1085
			"cr_gurk_whelp_hit_lgt.snd", // 1086
			"cr_gurk_whelp_vocalize.snd", // 1087
			"cr_gurnaset_attack_hvy.snd", // 1088
			"cr_gurnaset_attack_lgt.snd", // 1089
			"cr_gurnaset_hatchling_attack_hvy.snd", // 1090
			"cr_gurnaset_hatchling_attack_lgt.snd", // 1091
			"cr_gurnaset_hatchling_hit_hvy.snd", // 1092
			"cr_gurnaset_hatchling_hit_lgt.snd", // 1093
			"cr_gurnaset_hatchling_vocalize.snd", // 1094
			"cr_gurnaset_hit_hvy.snd", // 1095
			"cr_gurnaset_hit_lgt.snd", // 1096
			"cr_gurnaset_vocalize.snd", // 1097
			"cr_gurrcat_attack_hvy.snd", // 1098
			"cr_gurrcat_attack_lgt.snd", // 1099
			"cr_gurrcat_hit_hvy.snd", // 1100
			"cr_gurrcat_hit_lgt.snd", // 1101
			"cr_gurrcat_vocalize.snd", // 1102
			"cr_gurrek_attack_hvy.snd", // 1103
			"cr_gurrek_attack_lgt.snd", // 1104
			"cr_gurrek_hit_hvy.snd", // 1105
			"cr_gurrek_hit_lgt.snd", // 1106
			"cr_gurrek_vocalize.snd", // 1107
			"cr_hanadak_attack_hvy.snd", // 1108
			"cr_hanadak_attack_lgt.snd", // 1109
			"cr_hanadak_hit_hvy.snd", // 1110
			"cr_hanadak_hit_lgt.snd", // 1111
			"cr_hanadak_vocalize.snd", // 1112
			"cr_hermit_spider_attack_hvy.snd", // 1113
			"cr_hermit_spider_attack_lgt.snd", // 1114
			"cr_hermit_spider_hit_hvy.snd", // 1115
			"cr_hermit_spider_hit_lgt.snd", // 1116
			"cr_hermit_spider_queen_attack_hvy.snd", // 1117
			"cr_hermit_spider_queen_attack_lgt.snd", // 1118
			"cr_hermit_spider_queen_hit_hvy.snd", // 1119
			"cr_hermit_spider_queen_hit_lgt.snd", // 1120
			"cr_hermit_spider_queen_vocalize.snd", // 1121
			"cr_hermit_spider_vocalize.snd", // 1122
			"cr_horned_krevol_attack_hvy.snd", // 1123
			"cr_horned_krevol_attack_lgt.snd", // 1124
			"cr_horned_krevol_hit_hvy.snd", // 1125
			"cr_horned_krevol_hit_lgt.snd", // 1126
			"cr_horned_krevol_vocalize.snd", // 1127
			"cr_huf_dun_attack_hvy.snd", // 1128
			"cr_huf_dun_attack_lgt.snd", // 1129
			"cr_huf_dun_hit_hvy.snd", // 1130
			"cr_huf_dun_hit_lgt.snd", // 1131
			"cr_huf_dun_vocalize.snd", // 1132
			"cr_huge_breath.snd", // 1133
			"cr_huge_breath_fast.snd", // 1134
			"cr_huge_fs_dirt.snd", // 1135
			"cr_huge_fs_grass.snd", // 1136
			"cr_huge_fs_mud.snd", // 1137
			"cr_huge_fs_rock.snd", // 1138
			"cr_huge_fs_sand.snd", // 1139
			"cr_huge_fs_snow.snd", // 1140
			"cr_huge_fs_water.snd", // 1141
			"cr_hurrton_attack_heavy.snd", // 1142
			"cr_hurrton_attack_light.snd", // 1143
			"cr_hurrton_emote_vocalize.snd", // 1144
			"cr_hurrton_hit_heavy.snd", // 1145
			"cr_hurrton_hit_light.snd", // 1146
			"cr_huurton_pup_attack_hvy.snd", // 1147
			"cr_huurton_pup_attack_lgt.snd", // 1148
			"cr_huurton_pup_hit_hvy.snd", // 1149
			"cr_huurton_pup_hit_lgt.snd", // 1150
			"cr_huurton_pup_vocalize.snd", // 1151
			"cr_ikopi_attack_hvy.snd", // 1152
			"cr_ikopi_attack_lgt.snd", // 1153
			"cr_ikopi_hit_hvy.snd", // 1154
			"cr_ikopi_hit_lgt.snd", // 1155
			"cr_ikopi_vocalize.snd", // 1156
			"cr_infant_brackaset_attack_hvy.snd", // 1157
			"cr_infant_brackaset_attack_lgt.snd", // 1158
			"cr_infant_brackaset_hit_hvy.snd", // 1159
			"cr_infant_brackaset_hit_lgt.snd", // 1160
			"cr_infant_brackaset_vocalize.snd", // 1161
			"cr_infant_graul_attack_hvy.snd", // 1162
			"cr_infant_graul_attack_lgt.snd", // 1163
			"cr_infant_graul_hit_hvy.snd", // 1164
			"cr_infant_graul_hit_lgt.snd", // 1165
			"cr_infant_graul_vocalize.snd", // 1166
			"cr_juvenile_canyon_krayt_attack_hvy.snd", // 1167
			"cr_juvenile_canyon_krayt_attack_lgt.snd", // 1168
			"cr_juvenile_canyon_krayt_hit_hvy.snd", // 1169
			"cr_juvenile_canyon_krayt_hit_lgt.snd", // 1170
			"cr_juvenile_canyon_krayt_vocalize.snd", // 1171
			"cr_kaadu_attack_heavy.snd", // 1172
			"cr_kaadu_attack_light.snd", // 1173
			"cr_kaadu_emote_vocalize_1.snd", // 1174
			"cr_kaadu_hit_heavy.snd", // 1175
			"cr_kaadu_hit_light.snd", // 1176
			"cr_kahmurra_attack_hvy.snd", // 1177
			"cr_kahmurra_attack_lgt.snd", // 1178
			"cr_kahmurra_hit_hvy.snd", // 1179
			"cr_kahmurra_hit_lgt.snd", // 1180
			"cr_kahmurra_vocalize.snd", // 1181
			"cr_kai_tok_attack_hvy.snd", // 1182
			"cr_kai_tok_attack_lgt.snd", // 1183
			"cr_kai_tok_hit_hvy.snd", // 1184
			"cr_kai_tok_hit_lgt.snd", // 1185
			"cr_kai_tok_vocalize.snd", // 1186
			"cr_kashyyyk_bantha_atk_hvy.snd", // 1187
			"cr_kashyyyk_bantha_atk_lgt.snd", // 1188
			"cr_kashyyyk_bantha_die.snd", // 1189
			"cr_kashyyyk_bantha_emote.snd", // 1190
			"cr_kashyyyk_bantha_hit_hvy.snd", // 1191
			"cr_kashyyyk_bantha_hit_lgt.snd", // 1192
			"cr_katarn_atk_hvy.snd", // 1193
			"cr_katarn_atk_lgt.snd", // 1194
			"cr_katarn_die.snd", // 1195
			"cr_katarn_emote.snd", // 1196
			"cr_katarn_hit_hvy.snd", // 1197
			"cr_katarn_hit_lgt.snd", // 1198
			"cr_kima_attack_heavy.snd", // 1199
			"cr_kima_attack_light.snd", // 1200
			"cr_kima_emote_vocalize.snd", // 1201
			"cr_kima_hit_heavy.snd", // 1202
			"cr_kima_hit_light.snd", // 1203
			"cr_kimogila_attack_hvy.snd", // 1204
			"cr_kimogila_hatchling_attack_hvy.snd", // 1205
			"cr_kimogila_hatchling_attack_lgt.snd", // 1206
			"cr_kimogila_hatchling_hit_hvy.snd", // 1207
			"cr_kimogila_hatchling_hit_lgt.snd", // 1208
			"cr_kimogila_hatchling_vocalize.snd", // 1209
			"cr_kimogila_hit_hvy.snd", // 1210
			"cr_kimogila_hit_lgt.snd", // 1211
			"cr_kimogila_vocalize.snd", // 1212
			"cr_king_merek_harvester_attack_hvy.snd", // 1213
			"cr_king_merek_harvester_attack_lgt.snd", // 1214
			"cr_king_merek_harvester_hit_hvy.snd", // 1215
			"cr_king_merek_harvester_hit_lgt.snd", // 1216
			"cr_king_merek_harvester_vocalize.snd", // 1217
			"cr_king_venom_nightspider_attack_hvy.snd", // 1218
			"cr_king_venom_nightspider_attack_lgt.snd", // 1219
			"cr_king_venom_nightspider_hit_hvy.snd", // 1220
			"cr_king_venom_nightspider_hit_lgt.snd", // 1221
			"cr_king_venom_nightspider_vocalize.snd", // 1222
			"cr_kittle_attack_hvy.snd", // 1223
			"cr_kittle_attack_lgt.snd", // 1224
			"cr_kittle_hit_hvy.snd", // 1225
			"cr_kittle_hit_lgt.snd", // 1226
			"cr_kittle_vocalize.snd", // 1227
			"cr_kkorrwrot_atk_hvy.snd", // 1228
			"cr_kkorrwrot_atk_lgt.snd", // 1229
			"cr_kkorrwrot_die.snd", // 1230
			"cr_kkorrwrot_emote.snd", // 1231
			"cr_kkorrwrot_hit_hvy.snd", // 1232
			"cr_kkorrwrot_hit_lgt.snd", // 1233
			"cr_kkorrwrot_roar.snd", // 1234
			"cr_kliknik_attack_hvy.snd", // 1235
			"cr_kliknik_attack_lgt.snd", // 1236
			"cr_kliknik_hatchling_attack_hvy.snd", // 1237
			"cr_kliknik_hatchling_attack_lgt.snd", // 1238
			"cr_kliknik_hatchling_hit_hvy.snd", // 1239
			"cr_kliknik_hatchling_hit_lgt.snd", // 1240
			"cr_kliknik_hatchling_vocalize.snd", // 1241
			"cr_kliknik_hit_hvy.snd", // 1242
			"cr_kliknik_hit_lgt.snd", // 1243
			"cr_kliknik_queen_attack_hvy.snd", // 1244
			"cr_kliknik_queen_attack_lgt.snd", // 1245
			"cr_kliknik_queen_hit_hvy.snd", // 1246
			"cr_kliknik_queen_hit_lgt.snd", // 1247
			"cr_kliknik_queen_vocalize.snd", // 1248
			"cr_kliknik_vocalize.snd", // 1249
			"cr_krahbu_attack_hvy.snd", // 1250
			"cr_krahbu_attack_lgt.snd", // 1251
			"cr_krahbu_hit_hvy.snd", // 1252
			"cr_krahbu_hit_lgt.snd", // 1253
			"cr_krahbu_idle.snd", // 1254
			"cr_krahbu_vocalize.snd", // 1255
			"cr_kraytdragon_attack_heavy.snd", // 1256
			"cr_kraytdragon_emote_vocalize.snd", // 1257
			"cr_kraytdragon_hit_heavy.snd", // 1258
			"cr_kraytdragon_hit_light.snd", // 1259
			"cr_krayt_dragon_attack_lgt.snd", // 1260
			"cr_kreetle_attack_hvy.snd", // 1261
			"cr_kreetle_attack_lgt.snd", // 1262
			"cr_kreetle_hit_hvy.snd", // 1263
			"cr_kreetle_hit_lgt.snd", // 1264
			"cr_kreetle_vocalize.snd", // 1265
			"cr_kupernug_attack_hvy.snd", // 1266
			"cr_kupernug_attack_lgt.snd", // 1267
			"cr_kupernug_hit_hvy.snd", // 1268
			"cr_kupernug_hit_lgt.snd", // 1269
			"cr_kusak_attack_heavy.snd", // 1270
			"cr_kusak_attack_light.snd", // 1271
			"cr_kusak_hit_hvy.snd", // 1272
			"cr_kusak_hit_lgt.snd", // 1273
			"cr_kusak_pup_attack_hvy.snd", // 1274
			"cr_kusak_pup_attack_lgt.snd", // 1275
			"cr_kusak_pup_hit_hvy.snd", // 1276
			"cr_kusak_pup_hit_lgt.snd", // 1277
			"cr_kusak_pup_vocalize.snd", // 1278
			"cr_kusak_vocalize.snd", // 1279
			"cr_kwi_attack_light.snd", // 1280
			"cr_kwi_emote_vocalize.snd", // 1281
			"cr_kwi_hit_heavy.snd", // 1282
			"cr_kwi_hit_light.snd", // 1283
			"cr_kwi_idle_breathe.snd", // 1284
			"cr_langlatch_attack_heavy.snd", // 1285
			"cr_langlatch_attack_light.snd", // 1286
			"cr_langlatch_hatchling_attack_hvy.snd", // 1287
			"cr_langlatch_hatchling_attack_lgt.snd", // 1288
			"cr_langlatch_hatchling_hit_hvy.snd", // 1289
			"cr_langlatch_hatchling_hit_lgt.snd", // 1290
			"cr_langlatch_hatchling_vocalize.snd", // 1291
			"cr_langlatch_hit_hvy.snd", // 1292
			"cr_langlatch_hit_lgt.snd", // 1293
			"cr_langlatch_juvenile_attack_hvy.snd", // 1294
			"cr_langlatch_juvenile_attack_lgt.snd", // 1295
			"cr_langlatch_juvenile_hit_hvy.snd", // 1296
			"cr_langlatch_juvenile_hit_lgt.snd", // 1297
			"cr_langlatch_juvenile_vocalize.snd", // 1298
			"cr_langlatch_vocalize.snd", // 1299
			"cr_lanternbird_attack_heavy.snd", // 1300
			"cr_lanternbird_attack_light.snd", // 1301
			"cr_lanternbird_emote_vocalize.snd", // 1302
			"cr_lanternbird_hit_heavy.snd", // 1303
			"cr_lanternbird_hit_light.snd", // 1304
			"cr_large_breath_fast.snd", // 1305
			"cr_large_breath_slow.snd", // 1306
			"cr_lesser_desert_womprat_attack_hvy.snd", // 1307
			"cr_lesser_desert_womprat_attack_lgt.snd", // 1308
			"cr_lesser_desert_womprat_hit_hvy.snd", // 1309
			"cr_lesser_desert_womprat_hit_lgt.snd", // 1310
			"cr_lesser_desert_womprat_vocalize.snd", // 1311
			"cr_leviasquall_attack_hvy.snd", // 1312
			"cr_leviasquall_attack_lgt.snd", // 1313
			"cr_leviasquall_hit_hvy.snd", // 1314
			"cr_leviasquall_hit_lgt.snd", // 1315
			"cr_leviasquall_vocalize.snd", // 1316
			"cr_lghoof_fs_metal.snd", // 1317
			"cr_lghoof_fs_rock.snd", // 1318
			"cr_lghoof_fs_wood.snd", // 1319
			"cr_lg_fs_carpet.snd", // 1320
			"cr_lg_fs_dirt.snd", // 1321
			"cr_lg_fs_grass.snd", // 1322
			"cr_lg_fs_metal.snd", // 1323
			"cr_lg_fs_mud.snd", // 1324
			"cr_lg_fs_rock.snd", // 1325
			"cr_lg_fs_sand.snd", // 1326
			"cr_lg_fs_snow.snd", // 1327
			"cr_lg_fs_water.snd", // 1328
			"cr_lg_fs_wood.snd", // 1329
			"cr_lice_ridden_remmer_queen_attack_hvy.snd", // 1330
			"cr_lice_ridden_remmer_queen_attack_lgt.snd", // 1331
			"cr_lice_ridden_remmer_queen_hit_hvy.snd", // 1332
			"cr_lice_ridden_remmer_queen_hit_lgt.snd", // 1333
			"cr_lice_ridden_remmer_queen_vocalize.snd", // 1334
			"cr_male_snorbal_calf_attack_hvy.snd", // 1335
			"cr_male_snorbal_calf_attack_lgt.snd", // 1336
			"cr_male_snorbal_calf_hit_hvy.snd", // 1337
			"cr_male_snorbal_calf_hit_lgt.snd", // 1338
			"cr_male_snorbal_calf_vocalize.snd", // 1339
			"cr_male_swamp_tusk_cat_attack_hvy.snd", // 1340
			"cr_male_swamp_tusk_cat_attack_lgt.snd", // 1341
			"cr_male_swamp_tusk_cat_hit_hvy.snd", // 1342
			"cr_male_swamp_tusk_cat_hit_lgt.snd", // 1343
			"cr_male_swamp_tusk_cat_vocalize.snd", // 1344
			"cr_malkloc_attack_hvy.snd", // 1345
			"cr_malkloc_attack_lgt.snd", // 1346
			"cr_malkloc_bull_attack_hvy.snd", // 1347
			"cr_malkloc_bull_attack_lgt.snd", // 1348
			"cr_malkloc_bull_hit_hvy.snd", // 1349
			"cr_malkloc_bull_hit_lgt.snd", // 1350
			"cr_malkloc_bull_vocalize.snd", // 1351
			"cr_malkloc_hit_hvy.snd", // 1352
			"cr_malkloc_hit_lgt.snd", // 1353
			"cr_malkloc_vocalize.snd", // 1354
			"cr_mamien_ancient_attack_hvy.snd", // 1355
			"cr_mamien_ancient_attack_lgt.snd", // 1356
			"cr_mamien_ancient_hit_hvy.snd", // 1357
			"cr_mamien_ancient_hit_lgt.snd", // 1358
			"cr_mamien_ancient_vocalize.snd", // 1359
			"cr_mamien_attack_hvy.snd", // 1360
			"cr_mamien_attack_lgt.snd", // 1361
			"cr_mamien_hit_hvy.snd", // 1362
			"cr_mamien_hit_lgt.snd", // 1363
			"cr_mamien_vocalize.snd", // 1364
			"cr_mammoth_bearded_jax_attack_hvy.snd", // 1365
			"cr_mammoth_bearded_jax_attack_lgt.snd", // 1366
			"cr_mammoth_bearded_jax_hit_hvy.snd", // 1367
			"cr_mammoth_bearded_jax_hit_lgt.snd", // 1368
			"cr_mammoth_bearded_jax_vocalize.snd", // 1369
			"cr_mantigrue_night_stalker_attack_hvy.snd", // 1370
			"cr_mantigrue_night_stalker_attack_lgt.snd", // 1371
			"cr_mantigrue_night_stalker_hit_hvy.snd", // 1372
			"cr_mantigrue_night_stalker_hit_lgt.snd", // 1373
			"cr_mantigrue_night_stalker_vocalize.snd", // 1374
			"cr_matriarch_bantha_attack_hvy.snd", // 1375
			"cr_matriarch_bantha_attack_lgt.snd", // 1376
			"cr_matriarch_bantha_hit_hvy.snd", // 1377
			"cr_matriarch_bantha_hit_lgt.snd", // 1378
			"cr_matriarch_bantha_vocalize.snd", // 1379
			"cr_mawgax_attack_hvy.snd", // 1380
			"cr_mawgax_attack_lgt.snd", // 1381
			"cr_mawgax_hit_hvy.snd", // 1382
			"cr_mawgax_hit_lgt.snd", // 1383
			"cr_mawgax_vocalize.snd", // 1384
			"cr_mawgax_youth_attack_hvy.snd", // 1385
			"cr_mawgax_youth_attack_lgt.snd", // 1386
			"cr_mawgax_youth_hit_hvy.snd", // 1387
			"cr_mawgax_youth_hit_lgt.snd", // 1388
			"cr_mawgax_youth_vocalize.snd", // 1389
			"cr_meager_tortur_attack_hvy.snd", // 1390
			"cr_meager_tortur_attack_lgt.snd", // 1391
			"cr_meager_tortur_hit_hvy.snd", // 1392
			"cr_meager_tortur_hit_lgt.snd", // 1393
			"cr_meager_tortur_vocalize.snd", // 1394
			"cr_medhoof_fs_metal.snd", // 1395
			"cr_medhoof_fs_rock.snd", // 1396
			"cr_medhoof_fs_wood.snd", // 1397
			"cr_med_breath_fast.snd", // 1398
			"cr_med_breath_lgt.snd", // 1399
			"cr_med_breath_slow.snd", // 1400
			"cr_med_fs_carpet.snd", // 1401
			"cr_med_fs_dirt.snd", // 1402
			"cr_med_fs_grass.snd", // 1403
			"cr_med_fs_metal.snd", // 1404
			"cr_med_fs_mud.snd", // 1405
			"cr_med_fs_rock.snd", // 1406
			"cr_med_fs_sand.snd", // 1407
			"cr_med_fs_scuff_lgt.snd", // 1408
			"cr_med_fs_snow.snd", // 1409
			"cr_med_fs_water.snd", // 1410
			"cr_med_fs_wood.snd", // 1411
			"cr_merek_attack_heavy.snd", // 1412
			"cr_merek_attack_light.snd", // 1413
			"cr_merek_hit_heavy.snd", // 1414
			"cr_merek_hit_light.snd", // 1415
			"cr_merek_vocalize.snd", // 1416
			"cr_mindblob_atk_hvy.snd", // 1417
			"cr_mindblob_atk_lgt.snd", // 1418
			"cr_mindblob_die.snd", // 1419
			"cr_mindblob_emote.snd", // 1420
			"cr_mindblob_hit_hvy.snd", // 1421
			"cr_mindblob_hit_lgt.snd", // 1422
			"cr_minor_gubbur_attack_hvy.snd", // 1423
			"cr_minor_gubbur_attack_lgt.snd", // 1424
			"cr_minor_gubbur_hit_hvy.snd", // 1425
			"cr_minor_gubbur_hit_lgt.snd", // 1426
			"cr_minor_gubbur_vocalize.snd", // 1427
			"cr_minor_guf_drolg_attack_hvy.snd", // 1428
			"cr_minor_guf_drolg_attack_lgt.snd", // 1429
			"cr_minor_guf_drolg_hit_hvy.snd", // 1430
			"cr_minor_guf_drolg_hit_lgt.snd", // 1431
			"cr_minor_guf_drolg_vocalize.snd", // 1432
			"cr_minor_sludge_panther_attack_hvy.snd", // 1433
			"cr_minor_sludge_panther_attack_lgt.snd", // 1434
			"cr_minor_sludge_panther_hit_hvy.snd", // 1435
			"cr_minor_sludge_panther_hit_lgt.snd", // 1436
			"cr_minor_sludge_panther_vocalize.snd", // 1437
			"cr_minor_worrt_attack_hvy.snd", // 1438
			"cr_minor_worrt_attack_lgt.snd", // 1439
			"cr_minor_worrt_hit_hvy.snd", // 1440
			"cr_minor_worrt_hit_lgt.snd", // 1441
			"cr_minor_worrt_vocalize.snd", // 1442
			"cr_minstyngar_atk_hvy.snd", // 1443
			"cr_minstyngar_atk_lgt.snd", // 1444
			"cr_minstyngar_die.snd", // 1445
			"cr_minstyngar_emote.snd", // 1446
			"cr_minstyngar_hit_hvy.snd", // 1447
			"cr_minstyngar_hit_lgt.snd", // 1448
			"cr_monkey_breath.snd", // 1449
			"cr_mottled_wrix_attack_hvy.snd", // 1450
			"cr_mottled_wrix_attack_lgt.snd", // 1451
			"cr_mottled_wrix_hit_hvy.snd", // 1452
			"cr_mottled_wrix_hit_lgt.snd", // 1453
			"cr_mottled_wrix_vocalize.snd", // 1454
			"cr_mott_attack_heavy.snd", // 1455
			"cr_mott_attack_light.snd", // 1456
			"cr_mott_bull_attack_hvy.snd", // 1457
			"cr_mott_bull_attack_lgt.snd", // 1458
			"cr_mott_bull_hit_hvy.snd", // 1459
			"cr_mott_bull_hit_lgt.snd", // 1460
			"cr_mott_bull_vocalize.snd", // 1461
			"cr_mott_calf_attack_hvy.snd", // 1462
			"cr_mott_calf_attack_lgt.snd", // 1463
			"cr_mott_calf_hit_hvy.snd", // 1464
			"cr_mott_calf_hit_lgt.snd", // 1465
			"cr_mott_calf_vocalize.snd", // 1466
			"cr_mott_combat_vocalize.snd", // 1467
			"cr_mott_emote_vocalize.snd", // 1468
			"cr_mott_hit_heavy.snd", // 1469
			"cr_mott_hit_light.snd", // 1470
			"cr_mouf_atk_hvy.snd", // 1471
			"cr_mouf_atk_lgt.snd", // 1472
			"cr_mouf_die.snd", // 1473
			"cr_mouf_emote.snd", // 1474
			"cr_mouf_hit_hvy.snd", // 1475
			"cr_mouf_hit_lgt.snd", // 1476
			"cr_mound_mite_attack_hvy.snd", // 1477
			"cr_mound_mite_attack_lgt.snd", // 1478
			"cr_mound_mite_hit_hvy.snd", // 1479
			"cr_mound_mite_hit_lgt.snd", // 1480
			"cr_mound_mite_vocalize.snd", // 1481
			"cr_mountain_dewback_attack_hvy.snd", // 1482
			"cr_mountain_dewback_attack_lgt.snd", // 1483
			"cr_mountain_dewback_hit_hvy.snd", // 1484
			"cr_mountain_dewback_hit_lgt.snd", // 1485
			"cr_mountain_dewback_vocalize.snd", // 1486
			"cr_mountain_murra_attack_hvy.snd", // 1487
			"cr_mountain_murra_attack_lgt.snd", // 1488
			"cr_mountain_murra_hit_hvy.snd", // 1489
			"cr_mountain_murra_hit_lgt.snd", // 1490
			"cr_mountain_murra_vocalize.snd", // 1491
			"cr_mountain_squill_attack_hvy.snd", // 1492
			"cr_mountain_squill_attack_lgt.snd", // 1493
			"cr_mountain_squill_hit_hvy.snd", // 1494
			"cr_mountain_squill_hit_lgt.snd", // 1495
			"cr_mountain_squill_vocalize.snd", // 1496
			"cr_mountain_worrt_attack_hvy.snd", // 1497
			"cr_mountain_worrt_attack_lgt.snd", // 1498
			"cr_mountain_worrt_hit_hvy.snd", // 1499
			"cr_mountain_worrt_hit_lgt.snd", // 1500
			"cr_mountain_worrt_vocalize.snd", // 1501
			"cr_murra_attack_hvy.snd", // 1502
			"cr_murra_attack_lgt.snd", // 1503
			"cr_murra_hit_hvy.snd", // 1504
			"cr_murra_hit_lgt.snd", // 1505
			"cr_murra_vocalize.snd", // 1506
			"cr_mutant_baz_nitch_attack_hvy.snd", // 1507
			"cr_mutant_baz_nitch_attack_lgt.snd", // 1508
			"cr_mutant_baz_nitch_hit_hvy.snd", // 1509
			"cr_mutant_baz_nitch_hit_lgt.snd", // 1510
			"cr_mutant_baz_nitch_vocalize.snd", // 1511
			"cr_mutant_rancor_attack_hvy.snd", // 1512
			"cr_mutant_rancor_attack_lgt.snd", // 1513
			"cr_mutant_rancor_hit_hvy.snd", // 1514
			"cr_mutant_rancor_hit_lgt.snd", // 1515
			"cr_mutant_rancor_vocalize.snd", // 1516
			"cr_mutated_kahmurra_attack_hvy.snd", // 1517
			"cr_mutated_kahmurra_attack_lgt.snd", // 1518
			"cr_mutated_kahmurra_hit_hvy.snd", // 1519
			"cr_mutated_kahmurra_hit_lgt.snd", // 1520
			"cr_mutated_kahmurra_vocalize.snd", // 1521
			"cr_mutated_krevol_clicker_attack_hvy.snd", // 1522
			"cr_mutated_krevol_clicker_attack_lgt.snd", // 1523
			"cr_mutated_krevol_clicker_hit_hvy.snd", // 1524
			"cr_mutated_krevol_clicker_hit_lgt.snd", // 1525
			"cr_mutated_krevol_clicker_vocalize.snd", // 1526
			"cr_mynock_attack_hvy.snd", // 1527
			"cr_mynock_attack_lgt.snd", // 1528
			"cr_mynock_hit_hvy.snd", // 1529
			"cr_mynock_hit_lgt.snd", // 1530
			"cr_mynock_vocalize.snd", // 1531
			"cr_narglatch_attack_heavy.snd", // 1532
			"cr_narglatch_attack_light.snd", // 1533
			"cr_narglatch_cub_attack_hvy.snd", // 1534
			"cr_narglatch_cub_attack_lgt.snd", // 1535
			"cr_narglatch_cub_hit_hvy.snd", // 1536
			"cr_narglatch_cub_hit_lgt.snd", // 1537
			"cr_narglatch_cub_vocalize.snd", // 1538
			"cr_narglatch_emote_vocalize.snd", // 1539
			"cr_narglatch_hit_heavy.snd", // 1540
			"cr_narglatch_hit_light.snd", // 1541
			"cr_narglatch_idle.snd", // 1542
			"cr_narglatch_run.snd", // 1543
			"cr_narglatch_walk.snd", // 1544
			"cr_nerf_hit_hvy.snd", // 1545
			"cr_nerf_hit_lgt.snd", // 1546
			"cr_nerf_idle.snd", // 1547
			"cr_nerf_vocalize.snd", // 1548
			"cr_nightspider_aggressor_attack_hvy.snd", // 1549
			"cr_nightspider_aggressor_attack_lgt.snd", // 1550
			"cr_nightspider_aggressor_hit_hvy.snd", // 1551
			"cr_nightspider_aggressor_hit_lgt.snd", // 1552
			"cr_nightspider_aggressor_vocalize.snd", // 1553
			"cr_nightspider_attack_hvy.snd", // 1554
			"cr_nightspider_attack_lgt.snd", // 1555
			"cr_nightspider_hit_hvy.snd", // 1556
			"cr_nightspider_hit_lgt.snd", // 1557
			"cr_nightspider_poison_spitter_attack_hvy.snd", // 1558
			"cr_nightspider_poison_spitter_attack_lgt.snd", // 1559
			"cr_nightspider_poison_spitter_hit_hvy.snd", // 1560
			"cr_nightspider_poison_spitter_hit_lgt.snd", // 1561
			"cr_nightspider_poison_spitter_vocalize.snd", // 1562
			"cr_nightspider_vocalize.snd", // 1563
			"cr_noxious_vrelt_scavenger_attack_hvy.snd", // 1564
			"cr_noxious_vrelt_scavenger_attack_lgt.snd", // 1565
			"cr_noxious_vrelt_scavenger_hit_hvy.snd", // 1566
			"cr_noxious_vrelt_scavenger_hit_lgt.snd", // 1567
			"cr_noxious_vrelt_scavenger_vocalize.snd", // 1568
			"cr_nudfuh_attack_hvy.snd", // 1569
			"cr_nudfuh_attack_lgt.snd", // 1570
			"cr_nudfuh_hit_hvy.snd", // 1571
			"cr_nudfuh_hit_lgt.snd", // 1572
			"cr_nudfuh_vocalize.snd", // 1573
			"cr_nuna_attack_hvy.snd", // 1574
			"cr_nuna_attack_lgt.snd", // 1575
			"cr_nuna_hit_hvy.snd", // 1576
			"cr_nuna_hit_lgt.snd", // 1577
			"cr_nuna_vocalize.snd", // 1578
			"cr_paralope_attack_hvy.snd", // 1579
			"cr_paralope_attack_lgt.snd", // 1580
			"cr_paralope_hit_hvy.snd", // 1581
			"cr_paralope_hit_lgt.snd", // 1582
			"cr_paralope_vocalize.snd", // 1583
			"cr_pekopeko_attack_heavy.snd", // 1584
			"cr_pekopeko_attack_light.snd", // 1585
			"cr_pekopeko_emote_vocalize.snd", // 1586
			"cr_pekopeko_hit_heavy.snd", // 1587
			"cr_pekopeko_hit_light.snd", // 1588
			"cr_perlek_attack_hvy.snd", // 1589
			"cr_perlek_attack_lgt.snd", // 1590
			"cr_perlek_hit_hvy.snd", // 1591
			"cr_perlek_hit_lgt.snd", // 1592
			"cr_perlek_vocalize.snd", // 1593
			"cr_pharple_attack_hvy.snd", // 1594
			"cr_pharple_attack_lgt.snd", // 1595
			"cr_pharple_hit_hvy.snd", // 1596
			"cr_pharple_hit_lgt.snd", // 1597
			"cr_pharple_vocalize.snd", // 1598
			"cr_pigmy_pugoriss_attack_hvy.snd", // 1599
			"cr_pigmy_pugoriss_attack_lgt.snd", // 1600
			"cr_pigmy_pugoriss_hit_hvy.snd", // 1601
			"cr_pigmy_pugoriss_hit_lgt.snd", // 1602
			"cr_pigmy_pugoriss_vocalize.snd", // 1603
			"cr_piket_attack_heavy.snd", // 1604
			"cr_piket_attack_light.snd", // 1605
			"cr_piket_hit_hvy.snd", // 1606
			"cr_piket_hit_lgt.snd", // 1607
			"cr_piket_longhorn_attack_hvy.snd", // 1608
			"cr_piket_longhorn_attack_lgt.snd", // 1609
			"cr_piket_longhorn_hit_hvy.snd", // 1610
			"cr_piket_longhorn_hit_lgt.snd", // 1611
			"cr_piket_longhorn_vocalize.snd", // 1612
			"cr_piket_plains_walker_attack_hvy.snd", // 1613
			"cr_piket_plains_walker_attack_lgt.snd", // 1614
			"cr_piket_plains_walker_hit_hvy.snd", // 1615
			"cr_piket_plains_walker_hit_lgt.snd", // 1616
			"cr_piket_plains_walker_vocalize.snd", // 1617
			"cr_piket_vocalize.snd", // 1618
			"cr_plumed_rasp_attack_lgt.snd", // 1619
			"cr_plumed_rasp_hit_hvy.snd", // 1620
			"cr_plumed_rasp_hit_lgt.snd", // 1621
			"cr_poisonous_krevol_queen_attack_hvy.snd", // 1622
			"cr_poisonous_krevol_queen_attack_lgt.snd", // 1623
			"cr_poisonous_krevol_queen_hit_hvy.snd", // 1624
			"cr_poisonous_krevol_queen_hit_lgt.snd", // 1625
			"cr_poisonous_krevol_queen_vocalize.snd", // 1626
			"cr_pugoriss_attack_hvy.snd", // 1627
			"cr_pugoriss_attack_lgt.snd", // 1628
			"cr_pugoriss_hit_hvy.snd", // 1629
			"cr_pugoriss_hit_lgt.snd", // 1630
			"cr_pugoriss_vocalize.snd", // 1631
			"cr_puny_gacklebat_attack_hvy.snd", // 1632
			"cr_puny_gacklebat_attack_lgt.snd", // 1633
			"cr_puny_gacklebat_hit_hvy.snd", // 1634
			"cr_puny_gacklebat_hit_lgt.snd", // 1635
			"cr_puny_gacklebat_vocalize.snd", // 1636
			"cr_puny_stintaril_attack_hvy.snd", // 1637
			"cr_puny_stintaril_attack_lgt.snd", // 1638
			"cr_puny_stintaril_hit_hvy.snd", // 1639
			"cr_puny_stintaril_hit_lgt.snd", // 1640
			"cr_puny_stintaril_vocalize.snd", // 1641
			"cr_puny_tanc_mite_attack_hvy.snd", // 1642
			"cr_puny_tanc_mite_attack_lgt.snd", // 1643
			"cr_puny_tanc_mite_hit_hvy.snd", // 1644
			"cr_puny_tanc_mite_hit_lgt.snd", // 1645
			"cr_puny_tanc_mite_vocalize.snd", // 1646
			"cr_purbole_attack_heavy.snd", // 1647
			"cr_purbole_attack_light.snd", // 1648
			"cr_purbole_elder_attack_hvy.snd", // 1649
			"cr_purbole_elder_attack_lgt.snd", // 1650
			"cr_purbole_elder_hit_hvy.snd", // 1651
			"cr_purbole_elder_hit_lgt.snd", // 1652
			"cr_purbole_elder_vocalize.snd", // 1653
			"cr_purbole_emote_vocalize.snd", // 1654
			"cr_purbole_hit_heavy.snd", // 1655
			"cr_purbole_hit_light.snd", // 1656
			"cr_purbole_idle_breathe.snd", // 1657
			"cr_purbole_youth_attack_hvy.snd", // 1658
			"cr_purbole_youth_attack_lgt.snd", // 1659
			"cr_purbole_youth_hit_hvy.snd", // 1660
			"cr_purbole_youth_hit_lgt.snd", // 1661
			"cr_purbole_youth_vocalize.snd", // 1662
			"cr_putrid_decay_mite_hatchling_attack_hvy.snd", // 1663
			"cr_putrid_decay_mite_hatchling_attack_lgt.snd", // 1664
			"cr_putrid_decay_mite_hatchling_hit_hvy.snd", // 1665
			"cr_putrid_decay_mite_hatchling_hit_lgt.snd", // 1666
			"cr_putrid_decay_mite_hatchling_vocalize.snd", // 1667
			"cr_queen_arachne_attack_hvy.snd", // 1668
			"cr_queen_arachne_attack_lgt.snd", // 1669
			"cr_queen_arachne_hit_hvy.snd", // 1670
			"cr_queen_arachne_hit_lgt.snd", // 1671
			"cr_queen_arachne_vocalize.snd", // 1672
			"cr_queen_merek_harvester_attack_hvy.snd", // 1673
			"cr_queen_merek_harvester_attack_lgt.snd", // 1674
			"cr_queen_merek_harvester_hit_hvy.snd", // 1675
			"cr_queen_merek_harvester_hit_lgt.snd", // 1676
			"cr_queen_merek_harvester_vocalize.snd", // 1677
			"cr_quenker_attack_hvy.snd", // 1678
			"cr_quenker_attack_lgt.snd", // 1679
			"cr_quenker_hit_hvy.snd", // 1680
			"cr_quenker_hit_lgt.snd", // 1681
			"cr_quenker_relic_reaper_attack_hvy.snd", // 1682
			"cr_quenker_relic_reaper_attack_lgt.snd", // 1683
			"cr_quenker_relic_reaper_hit_hvy.snd", // 1684
			"cr_quenker_relic_reaper_hit_lgt.snd", // 1685
			"cr_quenker_relic_reaper_vocalize.snd", // 1686
			"cr_quenker_vocalize.snd", // 1687
			"cr_qurvel_attack_hvy.snd", // 1688
			"cr_qurvel_attack_lgt.snd", // 1689
			"cr_qurvel_hit_hvy.snd", // 1690
			"cr_qurvel_hit_lgt.snd", // 1691
			"cr_rancor_attack_heavy.snd", // 1692
			"cr_rancor_attack_light.snd", // 1693
			"cr_rancor_emote_eat.snd", // 1694
			"cr_rancor_emote_sniff.snd", // 1695
			"cr_rancor_emote_vocalize.snd", // 1696
			"cr_rancor_hit_heavy.snd", // 1697
			"cr_rancor_hit_light.snd", // 1698
			"cr_rancor_idle.snd", // 1699
			"cr_rancor_idle_special.snd", // 1700
			"cr_rancor_run.snd", // 1701
			"cr_rancor_walk.snd", // 1702
			"cr_rancor_youth_attack_hvy.snd", // 1703
			"cr_rancor_youth_attack_lgt.snd", // 1704
			"cr_rancor_youth_hit_hvy.snd", // 1705
			"cr_rancor_youth_hit_lgt.snd", // 1706
			"cr_rancor_youth_vocalize.snd", // 1707
			"cr_recluse_gurk_king_attack_hvy.snd", // 1708
			"cr_recluse_gurk_king_attack_lgt.snd", // 1709
			"cr_recluse_gurk_king_hit_hvy.snd", // 1710
			"cr_recluse_gurk_king_hit_lgt.snd", // 1711
			"cr_recluse_gurk_king_vocalize.snd", // 1712
			"cr_remmer_attack_hvy.snd", // 1713
			"cr_remmer_attack_lgt.snd", // 1714
			"cr_remmer_hit_hvy.snd", // 1715
			"cr_remmer_hit_lgt.snd", // 1716
			"cr_remmer_vocalize.snd", // 1717
			"cr_reptflyer_attack_heavy.snd", // 1718
			"cr_reptflyer_attack_light.snd", // 1719
			"cr_reptflyer_emote_vocalize.snd", // 1720
			"cr_reptflyer_hit_heavy.snd", // 1721
			"cr_reptflyer_hit_light.snd", // 1722
			"cr_rill_attack_hvy.snd", // 1723
			"cr_rill_attack_lgt.snd", // 1724
			"cr_rill_hit_hvy.snd", // 1725
			"cr_rill_hit_lgt.snd", // 1726
			"cr_rill_vocalize.snd", // 1727
			"cr_riverside_sulfur_mynock_attack_hvy.snd", // 1728
			"cr_riverside_sulfur_mynock_attack_lgt.snd", // 1729
			"cr_riverside_sulfur_mynock_hit_hvy.snd", // 1730
			"cr_riverside_sulfur_mynock_hit_lgt.snd", // 1731
			"cr_riverside_sulfur_mynock_vocalize.snd", // 1732
			"cr_roba_attack_hvy.snd", // 1733
			"cr_roba_attack_lgt.snd", // 1734
			"cr_roba_hit_hvy.snd", // 1735
			"cr_roba_hit_lgt.snd", // 1736
			"cr_roba_vocalize.snd", // 1737
			"cr_rockmite_attack_heavy.snd", // 1738
			"cr_rockmite_attack_light.snd", // 1739
			"cr_rockmite_hit_heavy.snd", // 1740
			"cr_rockmite_hit_light.snd", // 1741
			"cr_rockmite_vocalize.snd", // 1742
			"cr_rock_beetle_attack_hvy.snd", // 1743
			"cr_rock_beetle_attack_lgt.snd", // 1744
			"cr_rock_beetle_hit_hvy.snd", // 1745
			"cr_rock_beetle_hit_lgt.snd", // 1746
			"cr_rock_beetle_vocalize.snd", // 1747
			"cr_ronto_attack_heavy.snd", // 1748
			"cr_ronto_attack_light.snd", // 1749
			"cr_ronto_emote_vocalize.snd", // 1750
			"cr_ronto_hit_heavy.snd", // 1751
			"cr_ronto_hit_light.snd", // 1752
			"cr_ronto_idle_breathe.snd", // 1753
			"cr_rotten_gut_remmer_king_attack_hvy.snd", // 1754
			"cr_rotten_gut_remmer_king_attack_lgt.snd", // 1755
			"cr_rotten_gut_remmer_king_hit_hvy.snd", // 1756
			"cr_rotten_gut_remmer_king_hit_lgt.snd", // 1757
			"cr_rotten_gut_remmer_king_vocalize.snd", // 1758
			"cr_rot_mite_attack_hvy.snd", // 1759
			"cr_rot_mite_attack_lgt.snd", // 1760
			"cr_rot_mite_hit_hvy.snd", // 1761
			"cr_rot_mite_hit_lgt.snd", // 1762
			"cr_rot_mite_vocalize.snd", // 1763
			"cr_runty_pharple_attack_hvy.snd", // 1764
			"cr_runty_pharple_attack_lgt.snd", // 1765
			"cr_runty_pharple_hit_hvy.snd", // 1766
			"cr_runty_pharple_hit_lgt.snd", // 1767
			"cr_runty_pharple_vocalize.snd", // 1768
			"cr_salt_mynock_attack_hvy.snd", // 1769
			"cr_salt_mynock_attack_lgt.snd", // 1770
			"cr_salt_mynock_hit_hvy.snd", // 1771
			"cr_salt_mynock_hit_lgt.snd", // 1772
			"cr_salt_mynock_vocalize.snd", // 1773
			"cr_sandpanther_attack_heavy.snd", // 1774
			"cr_sandpanther_attack_light.snd", // 1775
			"cr_sandpanther_emote_vocalize.snd", // 1776
			"cr_sandpanther_hit_heavy.snd", // 1777
			"cr_sandpanther_hit_light.snd", // 1778
			"cr_sand_panther_cub_attack_hvy.snd", // 1779
			"cr_sand_panther_cub_attack_lgt.snd", // 1780
			"cr_sand_panther_cub_hit_hvy.snd", // 1781
			"cr_sand_panther_cub_hit_lgt.snd", // 1782
			"cr_sand_panther_cub_vocalize.snd", // 1783
			"cr_savage_flewt_queen_attack_hvy.snd", // 1784
			"cr_savage_flewt_queen_attack_lgt.snd", // 1785
			"cr_savage_flewt_queen_hit_hvy.snd", // 1786
			"cr_savage_flewt_queen_hit_lgt.snd", // 1787
			"cr_savage_flewt_queen_vocalize.snd", // 1788
			"cr_savage_humbaba_attack_hvy.snd", // 1789
			"cr_savage_humbaba_attack_lgt.snd", // 1790
			"cr_savage_humbaba_hit_hvy.snd", // 1791
			"cr_savage_humbaba_hit_lgt.snd", // 1792
			"cr_savage_humbaba_vocalize.snd", // 1793
			"cr_scyk_attack_hvy.snd", // 1794
			"cr_scyk_attack_lgt.snd", // 1795
			"cr_scyk_hit_hvy.snd", // 1796
			"cr_scyk_hit_lgt.snd", // 1797
			"cr_scyk_vocalize.snd", // 1798
			"cr_seething_bol_crusher_attack_hvy.snd", // 1799
			"cr_seething_bol_crusher_attack_lgt.snd", // 1800
			"cr_seething_bol_crusher_hit_hvy.snd", // 1801
			"cr_seething_bol_crusher_hit_lgt.snd", // 1802
			"cr_seething_bol_crusher_vocalize.snd", // 1803
			"cr_sevorrt_attack_hvy.snd", // 1804
			"cr_sevorrt_attack_lgt.snd", // 1805
			"cr_sevorrt_hit_hvy.snd", // 1806
			"cr_sevorrt_hit_lgt.snd", // 1807
			"cr_sevorrt_vocalize.snd", // 1808
			"cr_shaggy_gurk_youth_attack_hvy.snd", // 1809
			"cr_shaggy_gurk_youth_attack_lgt.snd", // 1810
			"cr_shaggy_gurk_youth_hit_hvy.snd", // 1811
			"cr_shaggy_gurk_youth_hit_lgt.snd", // 1812
			"cr_shaggy_gurk_youth_vocalize.snd", // 1813
			"cr_shallow_torton_attack_hvy.snd", // 1814
			"cr_shallow_torton_attack_lgt.snd", // 1815
			"cr_shallow_torton_hit_hvy.snd", // 1816
			"cr_shallow_torton_hit_lgt.snd", // 1817
			"cr_shallow_torton_vocalize.snd", // 1818
			"cr_sharnaff_attack_hvy.snd", // 1819
			"cr_sharnaff_attack_lgt.snd", // 1820
			"cr_sharnaff_bull_attack_hvy.snd", // 1821
			"cr_sharnaff_bull_attack_lgt.snd", // 1822
			"cr_sharnaff_bull_hit_hvy.snd", // 1823
			"cr_sharnaff_bull_hit_lgt.snd", // 1824
			"cr_sharnaff_bull_vocalize.snd", // 1825
			"cr_sharnaff_hit_heavy.snd", // 1826
			"cr_sharnaff_hit_light.snd", // 1827
			"cr_sharnaff_idle_breathe.snd", // 1828
			"cr_sharnaff_vocalize.snd", // 1829
			"cr_shaupaut_attack_heavy.snd", // 1830
			"cr_shaupaut_attack_light.snd", // 1831
			"cr_shaupaut_emote_vocalize.snd", // 1832
			"cr_shaupaut_hit_heavy.snd", // 1833
			"cr_shaupaut_hit_light.snd", // 1834
			"cr_shear_mite_attack_hvy.snd", // 1835
			"cr_shear_mite_attack_lgt.snd", // 1836
			"cr_shear_mite_broodling_attack_hvy.snd", // 1837
			"cr_shear_mite_broodling_attack_lgt.snd", // 1838
			"cr_shear_mite_broodling_hit_hvy.snd", // 1839
			"cr_shear_mite_broodling_hit_lgt.snd", // 1840
			"cr_shear_mite_broodling_vocalize.snd", // 1841
			"cr_shear_mite_hit_hvy.snd", // 1842
			"cr_shear_mite_hit_lgt.snd", // 1843
			"cr_shear_mite_hunter_attack_hvy.snd", // 1844
			"cr_shear_mite_hunter_attack_lgt.snd", // 1845
			"cr_shear_mite_hunter_hit_hvy.snd", // 1846
			"cr_shear_mite_hunter_hit_lgt.snd", // 1847
			"cr_shear_mite_hunter_vocalize.snd", // 1848
			"cr_shear_mite_idle.snd", // 1849
			"cr_shear_mite_queen_attack_hvy.snd", // 1850
			"cr_shear_mite_queen_attack_lgt.snd", // 1851
			"cr_shear_mite_queen_hit_hvy.snd", // 1852
			"cr_shear_mite_queen_hit_lgt.snd", // 1853
			"cr_shear_mite_queen_vocalize.snd", // 1854
			"cr_shear_mite_soldier_attack_hvy.snd", // 1855
			"cr_shear_mite_soldier_attack_lgt.snd", // 1856
			"cr_shear_mite_soldier_hit_hvy.snd", // 1857
			"cr_shear_mite_soldier_hit_lgt.snd", // 1858
			"cr_shear_mite_soldier_vocalize.snd", // 1859
			"cr_shear_mite_vocalize.snd", // 1860
			"cr_sickening_dung_mite_worker_attack_hvy.snd", // 1861
			"cr_sickening_dung_mite_worker_attack_lgt.snd", // 1862
			"cr_sickening_dung_mite_worker_hit_hvy.snd", // 1863
			"cr_sickening_dung_mite_worker_hit_lgt.snd", // 1864
			"cr_sickening_dung_mite_worker_vocalize.snd", // 1865
			"cr_skreeg_adolescent_attack_hvy.snd", // 1866
			"cr_skreeg_adolescent_attack_lgt.snd", // 1867
			"cr_skreeg_adolescent_hit_hvy.snd", // 1868
			"cr_skreeg_adolescent_hit_lgt.snd", // 1869
			"cr_skreeg_adolescent_vocalize.snd", // 1870
			"cr_skreeg_attack_hvy.snd", // 1871
			"cr_skreeg_attack_lgt.snd", // 1872
			"cr_skreeg_hit_hvy.snd", // 1873
			"cr_skreeg_hit_lgt.snd", // 1874
			"cr_skreeg_infant_attack_hvy.snd", // 1875
			"cr_skreeg_infant_attack_lgt.snd", // 1876
			"cr_skreeg_infant_hit_hvy.snd", // 1877
			"cr_skreeg_infant_hit_lgt.snd", // 1878
			"cr_skreeg_infant_vocalize.snd", // 1879
			"cr_skreeg_vocalize.snd", // 1880
			"cr_slicehound_attack_heavy.snd", // 1881
			"cr_slicehound_attack_light.snd", // 1882
			"cr_slicehound_emote_vocalize.snd", // 1883
			"cr_slicehound_hit_heavy.snd", // 1884
			"cr_slicehound_hit_light.snd", // 1885
			"cr_slicehound_idle_scratch.snd", // 1886
			"cr_slicehound_run.snd", // 1887
			"cr_slicehound_walk.snd", // 1888
			"cr_slinking_voritor_hunter_attack_hvy.snd", // 1889
			"cr_slinking_voritor_hunter_attack_lgt.snd", // 1890
			"cr_slinking_voritor_hunter_hit_hvy.snd", // 1891
			"cr_slinking_voritor_hunter_hit_lgt.snd", // 1892
			"cr_slinking_voritor_hunter_vocalize.snd", // 1893
			"cr_sm_fs_carpet.snd", // 1894
			"cr_sm_fs_dirt.snd", // 1895
			"cr_sm_fs_grass.snd", // 1896
			"cr_sm_fs_metal.snd", // 1897
			"cr_sm_fs_mud.snd", // 1898
			"cr_sm_fs_rock.snd", // 1899
			"cr_sm_fs_sand.snd", // 1900
			"cr_sm_fs_snow.snd", // 1901
			"cr_sm_fs_water.snd", // 1902
			"cr_sm_fs_wood.snd", // 1903
			"cr_snorbal_attack_hvy.snd", // 1904
			"cr_snorbal_attack_lgt.snd", // 1905
			"cr_snorbal_hit_hvy.snd", // 1906
			"cr_snorbal_hit_lgt.snd", // 1907
			"cr_snorbal_matriarch_attack_hvy.snd", // 1908
			"cr_snorbal_matriarch_attack_lgt.snd", // 1909
			"cr_snorbal_matriarch_hit_hvy.snd", // 1910
			"cr_snorbal_matriarch_hit_lgt.snd", // 1911
			"cr_snorbal_matriarch_vocalize.snd", // 1912
			"cr_snorbal_vocalize.snd", // 1913
			"cr_spined_puc_attack_hvy.snd", // 1914
			"cr_spined_puc_attack_lgt.snd", // 1915
			"cr_spined_puc_hit_hvy.snd", // 1916
			"cr_spined_puc_hit_lgt.snd", // 1917
			"cr_spined_puc_vocalize.snd", // 1918
			"cr_spined_snake_attack_hvy.snd", // 1919
			"cr_spined_snake_attack_lgt.snd", // 1920
			"cr_spined_snake_hit_hvy.snd", // 1921
			"cr_spined_snake_hit_lgt.snd", // 1922
			"cr_spined_snake_vocalize.snd", // 1923
			"cr_spineflap_queen_attack_hvy.snd", // 1924
			"cr_spineflap_queen_attack_lgt.snd", // 1925
			"cr_spineflap_queen_hit_hvy.snd", // 1926
			"cr_spineflap_queen_hit_lgt.snd", // 1927
			"cr_spineflap_queen_vocalize.snd", // 1928
			"cr_spit_fire.snd", // 1929
			"cr_spit_impact.snd", // 1930
			"cr_spit_tooth_fire.snd", // 1931
			"cr_spit_tooth_impact.snd", // 1932
			"cr_spray_fire.snd", // 1933
			"cr_spray_impact.snd", // 1934
			"cr_squall_attack_hvy.snd", // 1935
			"cr_squall_attack_lgt.snd", // 1936
			"cr_squall_hit_hvy.snd", // 1937
			"cr_squall_hit_lgt.snd", // 1938
			"cr_squall_vocalize.snd", // 1939
			"cr_squill_attack_heavy.snd", // 1940
			"cr_squill_attack_light.snd", // 1941
			"cr_squill_emote_vocalize.snd", // 1942
			"cr_squill_hit_heavy.snd", // 1943
			"cr_squill_hit_light.snd", // 1944
			"cr_squill_idle_breathe.snd", // 1945
			"cr_startled_vrelt_mother_attack_hvy.snd", // 1946
			"cr_startled_vrelt_mother_attack_lgt.snd", // 1947
			"cr_startled_vrelt_mother_hit_hvy.snd", // 1948
			"cr_startled_vrelt_mother_hit_lgt.snd", // 1949
			"cr_startled_vrelt_mother_vocalize.snd", // 1950
			"cr_stintaril_attack_hvy.snd", // 1951
			"cr_stintaril_attack_lgt.snd", // 1952
			"cr_stintaril_hit_hvy.snd", // 1953
			"cr_stintaril_hit_lgt.snd", // 1954
			"cr_stintaril_vocalize.snd", // 1955
			"cr_stunted_huf_dun_attack_hvy.snd", // 1956
			"cr_stunted_huf_dun_attack_lgt.snd", // 1957
			"cr_stunted_huf_dun_hit_hvy.snd", // 1958
			"cr_stunted_huf_dun_hit_lgt.snd", // 1959
			"cr_stunted_huf_dun_vocalize.snd", // 1960
			"cr_swarming_lesser_dewback_attack_hvy.snd", // 1961
			"cr_swarming_lesser_dewback_attack_lgt.snd", // 1962
			"cr_swarming_lesser_dewback_emote_vocalize.snd", // 1963
			"cr_swarming_lesser_dewback_hit_hvy.snd", // 1964
			"cr_swarming_lesser_dewback_hit_lgt.snd", // 1965
			"cr_swirl_prong_attack_hvy.snd", // 1966
			"cr_swirl_prong_hit_hvy.snd", // 1967
			"cr_swirl_prong_hit_lgt.snd", // 1968
			"cr_swirl_prong_vocalize.snd", // 1969
			"cr_tabage_attack_hvy.snd", // 1970
			"cr_tabage_attack_lgt.snd", // 1971
			"cr_tabage_hit_hvy.snd", // 1972
			"cr_tabage_hit_lgt.snd", // 1973
			"cr_tabage_vocalize.snd", // 1974
			"cr_tanc_mite_attack_hvy.snd", // 1975
			"cr_tanc_mite_attack_lgt.snd", // 1976
			"cr_tanc_mite_hit_hvy.snd", // 1977
			"cr_tanc_mite_hit_lgt.snd", // 1978
			"cr_tanc_mite_threaten.snd", // 1979
			"cr_tauntaun_attack_light.snd", // 1980
			"cr_tauntaun_breath.snd", // 1981
			"cr_tauntaun_emote_vocalize.snd", // 1982
			"cr_tauntaun_hit_heavy.snd", // 1983
			"cr_tauntaun_hit_light.snd", // 1984
			"cr_thune_attack_heavy.snd", // 1985
			"cr_thune_attack_light.snd", // 1986
			"cr_thune_grassland_guardian_attack_hvy.snd", // 1987
			"cr_thune_grassland_guardian_attack_lgt.snd", // 1988
			"cr_thune_grassland_guardian_hit_hvy.snd", // 1989
			"cr_thune_grassland_guardian_hit_lgt.snd", // 1990
			"cr_thune_grassland_guardian_vocalize.snd", // 1991
			"cr_thune_herd_leader_attack_hvy.snd", // 1992
			"cr_thune_herd_leader_attack_lgt.snd", // 1993
			"cr_thune_herd_leader_hit_hvy.snd", // 1994
			"cr_thune_herd_leader_hit_lgt.snd", // 1995
			"cr_thune_herd_leader_vocalize.snd", // 1996
			"cr_thune_hit_hvy.snd", // 1997
			"cr_thune_hit_lgt.snd", // 1998
			"cr_thune_idl_spcl_stomp.snd", // 1999
			"cr_thune_vocalize.snd", // 2000
			"cr_torton_attack_heavy.snd", // 2001
			"cr_torton_attack_lgt.snd", // 2002
			"cr_torton_emote_vocalize.snd", // 2003
			"cr_torton_hit_heavy.snd", // 2004
			"cr_torton_hit_light.snd", // 2005
			"cr_tuskcat_attack_heavy.snd", // 2006
			"cr_tuskcat_attack_light.snd", // 2007
			"cr_tuskcat_emote_vocalize.snd", // 2008
			"cr_tuskcat_hit_heavy.snd", // 2009
			"cr_tuskcat_hit_light.snd", // 2010
			"cr_tybis_attack_hvy.snd", // 2011
			"cr_tybis_attack_lgt.snd", // 2012
			"cr_tybis_hit_hvy.snd", // 2013
			"cr_tybis_hit_lgt.snd", // 2014
			"cr_tybis_idle.snd", // 2015
			"cr_tybis_vocalize.snd", // 2016
			"cr_tybis_youth_attack_hvy.snd", // 2017
			"cr_tybis_youth_attack_lgt.snd", // 2018
			"cr_tybis_youth_hit_hvy.snd", // 2019
			"cr_tybis_youth_hit_lgt.snd", // 2020
			"cr_tybis_youth_vocalize.snd", // 2021
			"cr_uwari_beetle_atk_hvy.snd", // 2022
			"cr_uwari_beetle_atk_lgt.snd", // 2023
			"cr_uwari_beetle_die.snd", // 2024
			"cr_uwari_beetle_emote.snd", // 2025
			"cr_uwari_beetle_hit_hvy.snd", // 2026
			"cr_uwari_beetle_hit_lgt.snd", // 2027
			"cr_varactyl_atk_hvy.snd", // 2028
			"cr_varactyl_atk_lgt.snd", // 2029
			"cr_varactyl_die.snd", // 2030
			"cr_varactyl_emote.snd", // 2031
			"cr_varactyl_hit_hvy.snd", // 2032
			"cr_varactyl_hit_lgt.snd", // 2033
			"cr_variegated_womprat_attack_hvy.snd", // 2034
			"cr_variegated_womprat_attack_lgt.snd", // 2035
			"cr_variegated_womprat_hit_hvy.snd", // 2036
			"cr_variegated_womprat_hit_lgt.snd", // 2037
			"cr_variegated_womprat_vocalize.snd", // 2038
			"cr_veermok_attack_heavy.snd", // 2039
			"cr_veermok_attack_light.snd", // 2040
			"cr_veermok_emote_vocalize.snd", // 2041
			"cr_veermok_hit_heavy.snd", // 2042
			"cr_veermok_hit_light.snd", // 2043
			"cr_veermok_idle_spcl.snd", // 2044
			"cr_veermok_run.snd", // 2045
			"cr_veermok_walk.snd", // 2046
			"cr_verne_attack_heavy.snd", // 2047
			"cr_verne_attack_lgt.snd", // 2048
			"cr_verne_bull_attack_hvy.snd", // 2049
			"cr_verne_bull_attack_lgt.snd", // 2050
			"cr_verne_bull_hit_hvy.snd", // 2051
			"cr_verne_bull_hit_lgt.snd", // 2052
			"cr_verne_bull_vocalize.snd", // 2053
			"cr_verne_calf_attack_hvy.snd", // 2054
			"cr_verne_calf_attack_lgt.snd", // 2055
			"cr_verne_calf_hit_hvy.snd", // 2056
			"cr_verne_calf_hit_lgt.snd", // 2057
			"cr_verne_calf_vocalize.snd", // 2058
			"cr_verne_emote_vocalize.snd", // 2059
			"cr_verne_hit_heavy.snd", // 2060
			"cr_verne_hit_light.snd", // 2061
			"cr_verne_idle_breathe.snd", // 2062
			"cr_vesp_attack_hvy.snd", // 2063
			"cr_vesp_attack_lgt.snd", // 2064
			"cr_vesp_hit_hvy.snd", // 2065
			"cr_vesp_hit_lgt.snd", // 2066
			"cr_vesp_vocalize.snd", // 2067
			"cr_vexed_voritor_attack_hvy.snd", // 2068
			"cr_vexed_voritor_attack_lgt.snd", // 2069
			"cr_vexed_voritor_hit_hvy.snd", // 2070
			"cr_vexed_voritor_hit_lgt.snd", // 2071
			"cr_vexed_voritor_vocalize.snd", // 2072
			"cr_violent_krahbu_attack_hvy.snd", // 2073
			"cr_violent_krahbu_attack_lgt.snd", // 2074
			"cr_violent_krahbu_hit_hvy.snd", // 2075
			"cr_violent_krahbu_hit_lgt.snd", // 2076
			"cr_violent_krahbu_vocalize.snd", // 2077
			"cr_vir_vur_attack_hvy.snd", // 2078
			"cr_vir_vur_attack_lgt.snd", // 2079
			"cr_vir_vur_hit_hvy.snd", // 2080
			"cr_vir_vur_hit_lgt.snd", // 2081
			"cr_vir_vur_vocalize.snd", // 2082
			"cr_vlutore_attack_hvy.snd", // 2083
			"cr_vlutore_attack_lgt.snd", // 2084
			"cr_vlutore_hit_hvy.snd", // 2085
			"cr_vlutore_hit_lgt.snd", // 2086
			"cr_voritor_dasher_attack_hvy.snd", // 2087
			"cr_voritor_dasher_attack_lgt.snd", // 2088
			"cr_voritor_dasher_hit_hvy.snd", // 2089
			"cr_voritor_dasher_hit_lgt.snd", // 2090
			"cr_voritor_dasher_vocalize.snd", // 2091
			"cr_voritor_lizard_attack_hvy.snd", // 2092
			"cr_voritor_lizard_attack_lgt.snd", // 2093
			"cr_voritor_lizard_hit_hvy.snd", // 2094
			"cr_voritor_lizard_hit_lgt.snd", // 2095
			"cr_voritor_lizard_vocalize.snd", // 2096
			"cr_vrelt_attack_hvy.snd", // 2097
			"cr_vrelt_attack_lgt.snd", // 2098
			"cr_vrelt_hit_hvy.snd", // 2099
			"cr_vrelt_hit_lgt.snd", // 2100
			"cr_vrelt_vocalize.snd", // 2101
			"cr_vrobalet_attack_hvy.snd", // 2102
			"cr_vrobalet_attack_lgt.snd", // 2103
			"cr_vrobalet_hit_hvy.snd", // 2104
			"cr_vrobalet_hit_lgt.snd", // 2105
			"cr_vrobalet_vocalize.snd", // 2106
			"cr_vrobal_attack_hvy.snd", // 2107
			"cr_vrobal_attack_lgt.snd", // 2108
			"cr_vrobal_bull_attack_hvy.snd", // 2109
			"cr_vrobal_bull_attack_lgt.snd", // 2110
			"cr_vrobal_bull_hit_hvy.snd", // 2111
			"cr_vrobal_bull_hit_lgt.snd", // 2112
			"cr_vrobal_bull_vocalize.snd", // 2113
			"cr_vrobal_hit_hvy.snd", // 2114
			"cr_vrobal_hit_lgt.snd", // 2115
			"cr_vrobal_vocalize.snd", // 2116
			"cr_vynock_attack_hvy.snd", // 2117
			"cr_vynock_attack_lgt.snd", // 2118
			"cr_vynock_hit_hvy.snd", // 2119
			"cr_vynock_hit_lgt.snd", // 2120
			"cr_vynock_vocalize.snd", // 2121
			"cr_walluga_atk_hvy.snd", // 2122
			"cr_walluga_atk_lgt.snd", // 2123
			"cr_walluga_die.snd", // 2124
			"cr_walluga_emote.snd", // 2125
			"cr_walluga_hit_hvy.snd", // 2126
			"cr_walluga_hit_lgt.snd", // 2127
			"cr_war_gronda_attack_hvy.snd", // 2128
			"cr_war_gronda_attack_lgt.snd", // 2129
			"cr_war_gronda_hit_hvy.snd", // 2130
			"cr_war_gronda_hit_lgt.snd", // 2131
			"cr_war_gronda_vocalize.snd", // 2132
			"cr_webweaver_atk_hvy.snd", // 2133
			"cr_webweaver_atk_lgt.snd", // 2134
			"cr_webweaver_die.snd", // 2135
			"cr_webweaver_emote.snd", // 2136
			"cr_webweaver_hit_hvy.snd", // 2137
			"cr_webweaver_hit_lgt.snd", // 2138
			"cr_whisperbird_attack_hvy.snd", // 2139
			"cr_whisperbird_attack_lgt.snd", // 2140
			"cr_whisperbird_hatchling_attack_hvy.snd", // 2141
			"cr_whisperbird_hatchling_attack_lgt.snd", // 2142
			"cr_whisperbird_hatchling_hit_hvy.snd", // 2143
			"cr_whisperbird_hatchling_hit_lgt.snd", // 2144
			"cr_whisperbird_hatchling_vocalize.snd", // 2145
			"cr_whisperbird_hit_hvy.snd", // 2146
			"cr_whisperbird_hit_lgt.snd", // 2147
			"cr_whisperbird_vocalize.snd", // 2148
			"cr_wingedornith_attack_hvy.snd", // 2149
			"cr_wingedornith_attack_lgt.snd", // 2150
			"cr_wingedornith_hit_hvy.snd", // 2151
			"cr_wingedornith_hit_lgt.snd", // 2152
			"cr_wingflap_huge_fast.snd", // 2153
			"cr_wingflap_slow_02.snd", // 2154
			"cr_womprat_attack_heavy.snd", // 2155
			"cr_womprat_attack_light.snd", // 2156
			"cr_womprat_hit_heavy.snd", // 2157
			"cr_womprat_hit_light.snd", // 2158
			"cr_womprat_vocalize.snd", // 2159
			"cr_wood_mite_attack_hvy.snd", // 2160
			"cr_wood_mite_attack_lgt.snd", // 2161
			"cr_wood_mite_hatchling_attack_hvy.snd", // 2162
			"cr_wood_mite_hatchling_attack_lgt.snd", // 2163
			"cr_wood_mite_hatchling_hit_hvy.snd", // 2164
			"cr_wood_mite_hatchling_hit_lgt.snd", // 2165
			"cr_wood_mite_hatchling_vocalize.snd", // 2166
			"cr_wood_mite_hit_hvy.snd", // 2167
			"cr_wood_mite_hit_lgt.snd", // 2168
			"cr_wood_mite_matriarch_attack_hvy.snd", // 2169
			"cr_wood_mite_matriarch_attack_lgt.snd", // 2170
			"cr_wood_mite_matriarch_hit_hvy.snd", // 2171
			"cr_wood_mite_matriarch_hit_lgt.snd", // 2172
			"cr_wood_mite_matriarch_vocalize.snd", // 2173
			"cr_wood_mite_vocalize.snd", // 2174
			"cr_woolamander_attack_hvy.snd", // 2175
			"cr_woolamander_attack_lgt.snd", // 2176
			"cr_woolamander_hit_hvy.snd", // 2177
			"cr_woolamander_hit_lgt.snd", // 2178
			"cr_woolamander_idle.snd", // 2179
			"cr_woolamander_idle_spcl.snd", // 2180
			"cr_woolamander_vocalize.snd", // 2181
			"cr_worrt_attack_hvy.snd", // 2182
			"cr_worrt_attack_lgt.snd", // 2183
			"cr_worrt_hit_hvy.snd", // 2184
			"cr_worrt_hit_lgt.snd", // 2185
			"cr_worrt_vocalize.snd", // 2186
			"cr_wrix_attack_hvy.snd", // 2187
			"cr_wrix_attack_lgt.snd", // 2188
			"cr_wrix_hit_hvy.snd", // 2189
			"cr_wrix_hit_lgt.snd", // 2190
			"cr_wrix_vocalize.snd", // 2191
			"cr_yng_hanadak_rock_crusher_attack_hvy.snd", // 2192
			"cr_yng_hanadak_rock_crusher_attack_lgt.snd", // 2193
			"cr_yng_hanadak_rock_crusher_hit_hvy.snd", // 2194
			"cr_yng_hanadak_rock_crusher_hit_lgt.snd", // 2195
			"cr_yng_hanadak_rock_crusher_vocalize.snd", // 2196
			"cr_young_baz_nitch_attack_hvy.snd", // 2197
			"cr_young_baz_nitch_attack_lgt.snd", // 2198
			"cr_young_baz_nitch_hit_hvy.snd", // 2199
			"cr_young_baz_nitch_hit_lgt.snd", // 2200
			"cr_young_baz_nitch_vocalize.snd", // 2201
			"cr_young_malkloc_attack_hvy.snd", // 2202
			"cr_young_malkloc_attack_lgt.snd", // 2203
			"cr_young_malkloc_hit_hvy.snd", // 2204
			"cr_young_malkloc_hit_lgt.snd", // 2205
			"cr_young_malkloc_vocalize.snd", // 2206
			"cr_young_reptflyer_attack_hvy.snd", // 2207
			"cr_young_reptflyer_attack_lgt.snd", // 2208
			"cr_young_reptflyer_hit_hvy.snd", // 2209
			"cr_young_reptflyer_hit_lgt.snd", // 2210
			"cr_young_reptflyer_vocalize.snd", // 2211
			"cr_young_roba_attack_hvy.snd", // 2212
			"cr_young_roba_attack_lgt.snd", // 2213
			"cr_young_roba_hit_hvy.snd", // 2214
			"cr_young_roba_hit_lgt.snd", // 2215
			"cr_young_roba_vocalize.snd", // 2216
			"cr_young_spined_snake_attack_hvy.snd", // 2217
			"cr_young_spined_snake_attack_lgt.snd", // 2218
			"cr_young_spined_snake_hit_hvy.snd", // 2219
			"cr_young_spined_snake_hit_lgt.snd", // 2220
			"cr_young_spined_snake_vocalize.snd", // 2221
			"cr_zucca_boar_attack_hvy.snd", // 2222
			"cr_zucca_boar_emt_vocalize.snd", // 2223
			"cr_zucca_boar_hit_hvy.snd", // 2224
			"cr_zucca_boar_hit_lgt.snd", // 2225
			"default_interior.snd", // 2226
			"dro_astromech_attack_hvy.snd", // 2227
			"dro_astromech_attack_lgt.snd", // 2228
			"dro_astromech_beep.snd", // 2229
			"dro_astromech_bodyfall.snd", // 2230
			"dro_astromech_converse.snd", // 2231
			"dro_astromech_destroyed.snd", // 2232
			"dro_astromech_dmg.snd", // 2233
			"dro_astromech_hit_hvy.snd", // 2234
			"dro_astromech_hit_lgt.snd", // 2235
			"dro_astromech_move_lp.snd", // 2236
			"dro_astromech_ok.snd", // 2237
			"dro_astromech_rnd_amb.snd", // 2238
			"dro_astromech_sad.snd", // 2239
			"dro_astromech_scream.snd", // 2240
			"dro_astromech_screech.snd", // 2241
			"dro_astromech_whistle.snd", // 2242
			"dro_astromech_yell.snd", // 2243
			"dro_cleaning_unit_lp.snd", // 2244
			"dro_cll8_amb.snd", // 2245
			"dro_cll8_attack_hvy.snd", // 2246
			"dro_cll8_attack_lgt.snd", // 2247
			"dro_cll8_bodyfall.snd", // 2248
			"dro_cll8_footstep.snd", // 2249
			"dro_cll8_hit_hvy.snd", // 2250
			"dro_cll8_hit_lgt.snd", // 2251
			"dro_cll8_vocalize.snd", // 2252
			"dro_cyborg_bodyfall.snd", // 2253
			"dro_cyborg_hit_hvy.snd", // 2254
			"dro_cyborg_hit_lgt.snd", // 2255
			"dro_droideka_amb.snd", // 2256
			"dro_droideka_attack_hvy.snd", // 2257
			"dro_droideka_attack_lgt.snd", // 2258
			"dro_droideka_bodyfall.snd", // 2259
			"dro_droideka_curl.snd", // 2260
			"dro_droideka_fire.snd", // 2261
			"dro_droideka_hit_hvy.snd", // 2262
			"dro_droideka_hit_lgt.snd", // 2263
			"dro_droideka_roll_end.snd", // 2264
			"dro_droideka_roll_lp.snd", // 2265
			"dro_droideka_roll_st.snd", // 2266
			"dro_droideka_uncurl.snd", // 2267
			"dro_droideka_vocalize.snd", // 2268
			"dro_droideka_walk.snd", // 2269
			"dro_eg6_attack_hvy.snd", // 2270
			"dro_eg6_attack_lgt.snd", // 2271
			"dro_eg6_bodyfall.snd", // 2272
			"dro_eg6_footstep.snd", // 2273
			"dro_eg6_hit_hvy.snd", // 2274
			"dro_eg6_hit_lgt.snd", // 2275
			"dro_eg6_vocalize.snd", // 2276
			"dro_fs_biped.snd", // 2277
			"dro_gonk_voc.snd", // 2278
			"dro_it0_float.snd", // 2279
			"dro_it0_powerdown.snd", // 2280
			"dro_it0_powerup.snd", // 2281
			"dro_mse6_amb.snd", // 2282
			"dro_mse6_attack_hvy.snd", // 2283
			"dro_mse6_attack_lgt.snd", // 2284
			"dro_mse6_bodyfall.snd", // 2285
			"dro_mse6_conversation.snd", // 2286
			"dro_mse6_hit_hvy.snd", // 2287
			"dro_mse6_hit_lgt.snd", // 2288
			"dro_mse6_idle_lp.snd", // 2289
			"dro_mse6_move_lp.snd", // 2290
			"dro_mse6_vocalize.snd", // 2291
			"dro_probe_amb.snd", // 2292
			"dro_probe_attack_hvy.snd", // 2293
			"dro_probe_attack_lgt.snd", // 2294
			"dro_probe_bodyfall.snd", // 2295
			"dro_probe_fire.snd", // 2296
			"dro_probe_hit_hvy.snd", // 2297
			"dro_probe_hit_lgt.snd", // 2298
			"dro_probe_hover.snd", // 2299
			"dro_probe_scan.snd", // 2300
			"dro_probe_thrusters.snd", // 2301
			"dro_probe_vocalize.snd", // 2302
			"dro_protocol_hit_hvy.snd", // 2303
			"dro_r2_1_babble.snd", // 2304
			"dro_r2_2_warn.snd", // 2305
			"dro_r2_3_danger.snd", // 2306
			"dro_r2_4_repair.snd", // 2307
			"dro_r2_5_hit.snd", // 2308
			"dro_r2_rnd_amb.snd", // 2309
			"dro_spider_amb.snd", // 2310
			"dro_spider_attack_hvy.snd", // 2311
			"dro_spider_attack_lgt.snd", // 2312
			"dro_spider_bodyfall.snd", // 2313
			"dro_spider_hit_hvy.snd", // 2314
			"dro_spider_hit_lgt.snd", // 2315
			"dro_spider_idle_stand.snd", // 2316
			"dro_spider_vocalize.snd", // 2317
			"dro_spider_walk.snd", // 2318
			"dro_spider_walk_fast.snd", // 2319
			"dro_tt8l_door_open.snd", // 2320
			"dro_tt8l_inout.snd", // 2321
			"dro_tt8l_sideside.snd", // 2322
			"dro_was_hit.snd", // 2323
			"dth_watch_water_pressure.snd", // 2324
			"e3_landspeeder_idle.snd", // 2325
			"exp_big_2.snd", // 2326
			"exp_cryoban_test.snd", // 2327
			"exp_debris_gas_large.snd", // 2328
			"exp_debris_gas_small.snd", // 2329
			"exp_debris_liquid_large.snd", // 2330
			"exp_debris_liquid_medium.snd", // 2331
			"exp_debris_liquid_small.snd", // 2332
			"exp_debris_metal_large.snd", // 2333
			"exp_debris_metal_med.snd", // 2334
			"exp_debris_metal_small.snd", // 2335
			"exp_debris_wood_large.snd", // 2336
			"exp_debris_wood_small.snd", // 2337
			"exp_ep3_avatar_sonic_trap.snd", // 2338
			"exp_ep3_radio_static.snd", // 2339
			"exp_glop_test.snd", // 2340
			"exp_large_generic.snd", // 2341
			"exp_medium_6.snd", // 2342
			"exp_medium_generic.snd", // 2343
			"exp_small_generic.snd", // 2344
			"fs_atst_walk.snd", // 2345
			"fs_in_carpet_bare.snd", // 2346
			"fs_in_carpet_crunch.snd", // 2347
			"fs_in_metal_floor.snd", // 2348
			"fs_in_stone_floor.snd", // 2349
			"fs_in_wood_floor.snd", // 2350
			"fs_out_dirt_bare.snd", // 2351
			"fs_out_dry_dirt.snd", // 2352
			"fs_out_grass_bare.snd", // 2353
			"fs_out_grass_crunch.snd", // 2354
			"fs_out_hardground_bare.snd", // 2355
			"fs_out_hardground_boot.snd", // 2356
			"fs_out_hardground_shoe.snd", // 2357
			"fs_out_metal_bare.snd", // 2358
			"fs_out_mud_squash.snd", // 2359
			"fs_out_rock_bare.snd", // 2360
			"fs_out_rock_crunch.snd", // 2361
			"fs_out_sand_bare.snd", // 2362
			"fs_out_sand_crunch.snd", // 2363
			"fs_out_snow_bare.snd", // 2364
			"fs_out_snow_crunch.snd", // 2365
			"fs_out_softground_bare.snd", // 2366
			"fs_out_softground_boot.snd", // 2367
			"fs_out_softground_shoe.snd", // 2368
			"fs_out_stone_bare.snd", // 2369
			"fs_out_water_splash.snd", // 2370
			"fs_out_wood_bare.snd", // 2371
			"hull_breach.snd", // 2372
			"hull_breach_amb.snd", // 2373
			"intro.snd", // 2374
			"item_air_gun.snd", // 2375
			"item_alarm_air_raid_lp.snd", // 2376
			"item_analysis_tool.snd", // 2377
			"item_apply_makeup.snd", // 2378
			"item_bioscanner_complete.snd", // 2379
			"item_bioscanner_lp.snd", // 2380
			"item_blasterpack_bomb_act.snd", // 2381
			"item_book_shuffle.snd", // 2382
			"item_breaking_glass.snd", // 2383
			"item_bubbletank_lp.snd", // 2384
			"item_buckle_metal.snd", // 2385
			"item_buckle_plastisteel.snd", // 2386
			"item_buzzing_lp.snd", // 2387
			"item_clank_damaged_idle.snd", // 2388
			"item_clone.snd", // 2389
			"item_close_metal_can_cntner.snd", // 2390
			"item_clothing_stat_run_lp.snd", // 2391
			"item_clothing_stat_shutdown.snd", // 2392
			"item_clothing_stat_start.snd", // 2393
			"item_clothplant_idle_lp.snd", // 2394
			"item_clothplant_make_item.snd", // 2395
			"item_clothplant_shutdown.snd", // 2396
			"item_clothplant_shutdown_special.snd", // 2397
			"item_clothplant_startup.snd", // 2398
			"item_clothplant_startup_special.snd", // 2399
			"item_cloth_close.snd", // 2400
			"item_cloth_open.snd", // 2401
			"item_cooker_end.snd", // 2402
			"item_cooker_lp.snd", // 2403
			"item_cooker_start.snd", // 2404
			"item_countdown_lp.snd", // 2405
			"item_creak_close.snd", // 2406
			"item_creak_open.snd", // 2407
			"item_crfarm_run_lp.snd", // 2408
			"item_crfarm_shutdown.snd", // 2409
			"item_crfarm_start.snd", // 2410
			"item_crunch_pills.snd", // 2411
			"item_dice.snd", // 2412
			"item_dishes_dropped.snd", // 2413
			"item_door_metal_close.snd", // 2414
			"item_door_metal_open.snd", // 2415
			"item_door_wooden_close.snd", // 2416
			"item_door_wooden_open.snd", // 2417
			"item_drill_turn_end.snd", // 2418
			"item_drill_turn_lp.snd", // 2419
			"item_drill_turn_start.snd", // 2420
			"item_drowsy_dart_trap.snd", // 2421
			"item_electronics_break.snd", // 2422
			"item_elevator_02_descend.snd", // 2423
			"item_elevator_02_rise.snd", // 2424
			"item_elevator_descend_lp.snd", // 2425
			"item_elevator_rise_lp.snd", // 2426
			"item_enganalboard_complete.snd", // 2427
			"item_enganalboard_lp.snd", // 2428
			"item_enraging_spur_trap.snd", // 2429
			"item_extended_lasing_lp.snd", // 2430
			"item_fire_large.snd", // 2431
			"item_fire_medium.snd", // 2432
			"item_fire_small.snd", // 2433
			"item_flash_bomb_trap.snd", // 2434
			"item_flora_run_lp.snd", // 2435
			"item_flora_shutdown.snd", // 2436
			"item_flora_start.snd", // 2437
			"item_foodchem_fctry_run_lp.snd", // 2438
			"item_foodchem_fctry_shutdown.snd", // 2439
			"item_foodchem_fctry_start.snd", // 2440
			"item_foodchem_stat_run_lp.snd", // 2441
			"item_foodchem_stat_shutdown.snd", // 2442
			"item_foodchem_stat_start.snd", // 2443
			"item_forcedoor_hum.snd", // 2444
			"item_forcedoor_hum_01.snd", // 2445
			"item_forcedoor_off.snd", // 2446
			"item_forcedoor_off_01.snd", // 2447
			"item_forcedoor_on.snd", // 2448
			"item_forcedoor_on_01.snd", // 2449
			"item_freq_jammer_lp.snd", // 2450
			"item_fusioncutter_end.snd", // 2451
			"item_fusioncutter_lp.snd", // 2452
			"item_fusioncutter_start.snd", // 2453
			"item_fwks_chaser.snd", // 2454
			"item_fwks_launch_lrg.snd", // 2455
			"item_fwks_launch_sml.snd", // 2456
			"item_fwks_lit_fuse.snd", // 2457
			"item_fwks_pop_crackle.snd", // 2458
			"item_fwks_pop_loud.snd", // 2459
			"item_fwks_pop_med.snd", // 2460
			"item_fwks_pop_sml.snd", // 2461
			"item_fwks_saturn_missile.snd", // 2462
			"item_fwks_sparkler_lp.snd", // 2463
			"item_gas_expulsion.snd", // 2464
			"item_gas_leak_trap_lp.snd", // 2465
			"item_gas_leak_trap_off.snd", // 2466
			"item_gas_leak_trap_on.snd", // 2467
			"item_gas_tool_sample.snd", // 2468
			"item_gas_tool_survey.snd", // 2469
			"item_glass_wndw_shattr.snd", // 2470
			"item_hairbrush.snd", // 2471
			"item_hammer_pound.snd", // 2472
			"item_heavy_wood_door_close.snd", // 2473
			"item_heavy_wood_door_open.snd", // 2474
			"item_holo.snd", // 2475
			"item_hopper_input_energycbe.snd", // 2476
			"item_hopper_input_gas.snd", // 2477
			"item_hopper_input_liquid.snd", // 2478
			"item_hopper_input_mineral.snd", // 2479
			"item_hovertransport_lp.snd", // 2480
			"item_hydraulic_close.snd", // 2481
			"item_hydraulic_open.snd", // 2482
			"item_internalstorage_open.snd", // 2483
			"item_itm_wpn_fctry_run_lp.snd", // 2484
			"item_itm_wpn_fctry_shutdown.snd", // 2485
			"item_itm_wpn_fctry_start.snd", // 2486
			"item_itm_wpn_stat_run_lp.snd", // 2487
			"item_itm_wpn_stat_shutdown.snd", // 2488
			"item_itm_wpn_stat_start.snd", // 2489
			"item_jabba_door_in_close.snd", // 2490
			"item_jabba_door_in_open.snd", // 2491
			"item_jabba_door_out_close.snd", // 2492
			"item_jabba_door_out_open.snd", // 2493
			"item_jabba_steam_vent_lp.snd", // 2494
			"item_jetbooster_end.snd", // 2495
			"item_jetbooster_lp.snd", // 2496
			"item_jetbooster_start.snd", // 2497
			"item_lasercage_off.snd", // 2498
			"item_lasercage_on.snd", // 2499
			"item_lasercage_steady_lp.snd", // 2500
			"item_lasing_oneshot.snd", // 2501
			"item_laying_on.snd", // 2502
			"item_leather_buckle.snd", // 2503
			"item_leather_unbuckle.snd", // 2504
			"item_liquid_tool_sample.snd", // 2505
			"item_liquid_tool_survey.snd", // 2506
			"item_loud_steam_vent.snd", // 2507
			"item_lrg_glass_cont_shattr.snd", // 2508
			"item_lrg_glass_wndw_shattr.snd", // 2509
			"item_lumber_tool_sample.snd", // 2510
			"item_lumber_tool_survey.snd", // 2511
			"item_maggrapple.snd", // 2512
			"item_magnets.snd", // 2513
			"item_metalsqueak_close.snd", // 2514
			"item_metalsqueak_open.snd", // 2515
			"item_metal_barrel_smash.snd", // 2516
			"item_metal_clasps.snd", // 2517
			"item_metal_crate_smash.snd", // 2518
			"item_metal_slam.snd", // 2519
			"item_microwelder_end.snd", // 2520
			"item_microwelder_lp.snd", // 2521
			"item_microwelder_start.snd", // 2522
			"item_mineral_tool_sample.snd", // 2523
			"item_mineral_tool_survey.snd", // 2524
			"item_moisture_tool_sample.snd", // 2525
			"item_moisture_tool_survey.snd", // 2526
			"item_noise_maker_trap.snd", // 2527
			"item_open_metal_can_cntner.snd", // 2528
			"item_output_anal_complete.snd", // 2529
			"item_output_anal_lp.snd", // 2530
			"item_plasmawelder_end.snd", // 2531
			"item_plasmawelder_lp.snd", // 2532
			"item_plasmawelder_start.snd", // 2533
			"item_porcelain_break.snd", // 2534
			"item_prison_door_close.snd", // 2535
			"item_prison_door_opening.snd", // 2536
			"item_process_plant_idle_lp.snd", // 2537
			"item_process_plant_make_itm.snd", // 2538
			"item_process_plant_shutdown.snd", // 2539
			"item_process_plant_startup.snd", // 2540
			"item_proton_grenade_act.snd", // 2541
			"item_quiet_steam_vent.snd", // 2542
			"item_ratchet.snd", // 2543
			"item_rattle.snd", // 2544
			"item_razor_hum_end.snd", // 2545
			"item_razor_hum_lp.snd", // 2546
			"item_razor_hum_start.snd", // 2547
			"item_rebreather_in.snd", // 2548
			"item_rebreather_out.snd", // 2549
			"item_reliacharger_end.snd", // 2550
			"item_reliacharger_lp.snd", // 2551
			"item_reliacharger_start.snd", // 2552
			"item_repairobj.snd", // 2553
			"item_repulsor_mine_act.snd", // 2554
			"item_restbolt.snd", // 2555
			"item_ring_hero_mark.snd", // 2556
			"item_sabaac_card_shuffle.snd", // 2557
			"item_sarlaccpit.snd", // 2558
			"item_scissors.snd", // 2559
			"item_shaker.snd", // 2560
			"item_shisha_lp.snd", // 2561
			"item_siton.snd", // 2562
			"item_sml_glass_cont_crush.snd", // 2563
			"item_sml_metal_cont_crush.snd", // 2564
			"item_sonic_pulse_trap.snd", // 2565
			"item_sparking_control_panel.snd", // 2566
			"item_sparking_control_panel_special.snd", // 2567
			"item_sparks.snd", // 2568
			"item_sputter_damaged_idle.snd", // 2569
			"item_standfrom.snd", // 2570
			"item_stasis_field_lp.snd", // 2571
			"item_stasis_field_off.snd", // 2572
			"item_stasis_field_on.snd", // 2573
			"item_static_lp.snd", // 2574
			"item_steam_end.snd", // 2575
			"item_steam_lp.snd", // 2576
			"item_steam_start.snd", // 2577
			"item_struct_fctry_run_lp.snd", // 2578
			"item_struct_fctry_shutdown.snd", // 2579
			"item_struct_fctry_start.snd", // 2580
			"item_struct_stat_run_lp.snd", // 2581
			"item_struct_stat_shutdown.snd", // 2582
			"item_struct_stat_start.snd", // 2583
			"item_sub_analyzer_complete.snd", // 2584
			"item_sub_analyzer_lp.snd", // 2585
			"item_suction_attach.snd", // 2586
			"item_suction_detach.snd", // 2587
			"item_surveypad_complete.snd", // 2588
			"item_surveypad_lp.snd", // 2589
			"item_switch_off.snd", // 2590
			"item_switch_on.snd", // 2591
			"item_syringe.snd", // 2592
			"item_ticking.snd", // 2593
			"item_tiki_torch.snd", // 2594
			"item_trailing_beetle_stink.snd", // 2595
			"item_tranq_dart_release.snd", // 2596
			"item_tranq_dart_spread.snd", // 2597
			"item_unbuckle_metal.snd", // 2598
			"item_unbuckle_plastisteel.snd", // 2599
			"item_vacuum_lp.snd", // 2600
			"item_vehicle_plant_idle_lp.snd", // 2601
			"item_vehicle_plant_make_itm.snd", // 2602
			"item_vehicle_plant_shutdown.snd", // 2603
			"item_vehicle_plant_start.snd", // 2604
			"item_velcro_close.snd", // 2605
			"item_velcro_open.snd", // 2606
			"item_vend.snd", // 2607
			"item_weapon_make_item.snd", // 2608
			"item_weapon_plant_idle_lp.snd", // 2609
			"item_weapon_plant_shutdown.snd", // 2610
			"item_weapon_plant_startup.snd", // 2611
			"item_webber_trap.snd", // 2612
			"item_welding_lp.snd", // 2613
			"item_whir_damaged_idle.snd", // 2614
			"item_wood_barrl_smash.snd", // 2615
			"item_wood_break.snd", // 2616
			"item_wood_crate_smash.snd", // 2617
			"item_zipper_close.snd", // 2618
			"item_zipper_open.snd", // 2619
			"item_zoom_in_end.snd", // 2620
			"item_zoom_in_lp.snd", // 2621
			"item_zoom_in_st.snd", // 2622
			"item_zoom_out_end.snd", // 2623
			"item_zoom_out_lp.snd", // 2624
			"item_zoom_out_st.snd", // 2625
			"itm_cybernetics_installed.snd", // 2626
			"itm_cybernetics_pwrdwn.snd", // 2627
			"itm_cybernetics_pwrup.snd", // 2628
			"itm_cybernetics_repaired.snd", // 2629
			"itm_cybernetics_uninstalled.snd", // 2630
			"jawa_chatter_01.snd", // 2631
			"jawa_chatter_02.snd", // 2632
			"jawa_chatter_03.snd", // 2633
			"jawa_chatter_bounty.snd", // 2634
			"mis_bonk.snd", // 2635
			"mis_ding.snd", // 2636
			"mis_flyby.snd", // 2637
			"mis_notenoughpoints.snd", // 2638
			"mis_windup.snd", // 2639
			"music_acq_academic.snd", // 2640
			"music_acq_bountyhunter.snd", // 2641
			"music_acq_healer.snd", // 2642
			"music_acq_miner.snd", // 2643
			"music_acq_thespian.snd", // 2644
			"music_ambience_desert.snd", // 2645
			"music_ambience_desert_02.snd", // 2646
			"music_ambience_starport.snd", // 2647
			"music_ambience_swamp.snd", // 2648
			"music_ambience_theed_palace.snd", // 2649
			"music_ambience_underground.snd", // 2650
			"music_ambience_underground_a.snd", // 2651
			"music_ambience_underground_b.snd", // 2652
			"music_amb_underwater_a.snd", // 2653
			"music_amb_underwater_b.snd", // 2654
			"music_autorun_loop.snd", // 2655
			"music_autorun_lp.snd", // 2656
			"music_become_dark_jedi.snd", // 2657
			"music_become_jedi.snd", // 2658
			"music_become_light_jedi.snd", // 2659
			"music_celebration_a_loop.snd", // 2660
			"music_celebration_b_loop.snd", // 2661
			"music_chamber.snd", // 2662
			"music_combat_bfield_death.snd", // 2663
			"music_combat_bfield_def.snd", // 2664
			"music_combat_bfield_loop.snd", // 2665
			"music_combat_bfield_lp.snd", // 2666
			"music_combat_bfield_vict.snd", // 2667
			"music_combat_defeat.snd", // 2668
			"music_combat_e_thru_s_def.snd", // 2669
			"music_combat_e_thru_s_loop.snd", // 2670
			"music_combat_e_thru_s_vict.snd", // 2671
			"music_combat_loop.snd", // 2672
			"music_combat_victory.snd", // 2673
			"music_com_enter_battle.snd", // 2674
			"music_corellia_salon.snd", // 2675
			"music_credits.snd", // 2676
			"music_darth_vader_theme.snd", // 2677
			"music_e3_loop.snd", // 2678
			"music_emperor_theme.snd", // 2679
			"music_emperor_theme_loop.snd", // 2680
			"music_emperor_theme_stereo.snd", // 2681
			"music_ep3_avatar_alarm.snd", // 2682
			"music_event_celebration.snd", // 2683
			"music_event_danger.snd", // 2684
			"music_event_figrin_dan.snd", // 2685
			"music_event_romance.snd", // 2686
			"music_event_sunrise.snd", // 2687
			"music_event_sunset.snd", // 2688
			"music_exar_theme.snd", // 2689
			"music_exar_theme_loop.snd", // 2690
			"music_explore.snd", // 2691
			"music_explore_a_loop.snd", // 2692
			"music_figrin_dan_1_loop.snd", // 2693
			"music_figrin_dan_2_loop.snd", // 2694
			"music_figrin_dan_song_1.snd", // 2695
			"music_gloom_a.snd", // 2696
			"music_gloom_a_loop.snd", // 2697
			"music_gloom_b.snd", // 2698
			"music_humor.snd", // 2699
			"music_humor_a_loop.snd", // 2700
			"music_humor_b_loop.snd", // 2701
			"music_id_tent_corellia_loop.snd", // 2702
			"music_id_tent_naboo_loop.snd", // 2703
			"music_id_tent_tatooine_loop.snd", // 2704
			"music_intro_loop.snd", // 2705
			"music_int_accepted_imperial.snd", // 2706
			"music_int_accepted_neutral.snd", // 2707
			"music_int_accepted_rebel.snd", // 2708
			"music_int_complete_imperial.snd", // 2709
			"music_int_complete_neutral.snd", // 2710
			"music_int_complete_rebel.snd", // 2711
			"music_jedi_character_creation.snd", // 2712
			"music_jungle_amb.snd", // 2713
			"music_jungle_amb_a.snd", // 2714
			"music_jungle_amb_b.snd", // 2715
			"music_lando_theme.snd", // 2716
			"music_leia_theme.snd", // 2717
			"music_leia_theme_loop.snd", // 2718
			"music_leia_theme_stereo.snd", // 2719
			"music_main_title.snd", // 2720
			"music_max_rebo_1_loop.snd", // 2721
			"music_max_rebo_2_loop.snd", // 2722
			"music_max_rebo_song_1.snd", // 2723
			"music_mission_accepted.snd", // 2724
			"music_mission_complete.snd", // 2725
			"music_naboo_salon.snd", // 2726
			"music_otoh_gunga_loop.snd", // 2727
			"music_player_death.snd", // 2728
			"music_player_v_creat_death.snd", // 2729
			"music_player_v_creat_def.snd", // 2730
			"music_player_v_creat_lp.snd", // 2731
			"music_player_v_creat_vict.snd", // 2732
			"music_player_v_npc_death.snd", // 2733
			"music_player_v_npc_def.snd", // 2734
			"music_player_v_npc_lp.snd", // 2735
			"music_player_v_npc_vict.snd", // 2736
			"music_player_v_player_death.snd", // 2737
			"music_player_v_player_def.snd", // 2738
			"music_player_v_player_lp.snd", // 2739
			"music_player_v_player_vict.snd", // 2740
			"music_romance_a_loop.snd", // 2741
			"music_romance_b_loop.snd", // 2742
			"music_romance_c_loop.snd", // 2743
			"music_satisfaction.snd", // 2744
			"music_satisfaction_a_loop.snd", // 2745
			"music_satisfaction_b_loop.snd", // 2746
			"music_silence.snd", // 2747
			"music_space_player_death.snd", // 2748
			"music_starport_a_loop.snd", // 2749
			"music_starport_b_loop.snd", // 2750
			"music_tatooine_salon.snd", // 2751
			"music_teammate_die.snd", // 2752
			"music_theed_palace.snd", // 2753
			"music_theed_palace_loop.snd", // 2754
			"music_themequest_acc_criminal.snd", // 2755
			"music_themequest_acc_general.snd", // 2756
			"music_themequest_acc_imperial.snd", // 2757
			"music_themequest_acc_rebel.snd", // 2758
			"music_themequest_fail_criminal.snd", // 2759
			"music_themequest_fail_imperial.snd", // 2760
			"music_themequest_fail_rebel.snd", // 2761
			"music_themequest_victory_imperial.snd", // 2762
			"music_themequest_victory_rebel.snd", // 2763
			"music_theme_corellia.snd", // 2764
			"music_theme_dathomir.snd", // 2765
			"music_theme_endor.snd", // 2766
			"music_theme_generic.snd", // 2767
			"music_theme_generic_a.snd", // 2768
			"music_theme_generic_b.snd", // 2769
			"music_theme_generic_c.snd", // 2770
			"music_theme_naboo.snd", // 2771
			"music_theme_tatooine.snd", // 2772
			"music_underground_loop.snd", // 2773
			"music_underwater_loop.snd", // 2774
			"mus_action_timed_quest.snd", // 2775
			"mus_rodian_quest_accept.snd", // 2776
			"mus_rodian_quest_fail.snd", // 2777
			"mus_rodian_quest_sucess.snd", // 2778
			"mus_trandoshan_quest_accept.snd", // 2779
			"mus_trandoshan_quest_fail.snd", // 2780
			"mus_trandoshan_quest_sucess.snd", // 2781
			"mus_wookiee_quest_accept.snd", // 2782
			"mus_wookiee_quest_fail.snd", // 2783
			"mus_wookiee_quest_sucess.snd", // 2784
			"perform_boo.snd", // 2785
			"perform_cheer.snd", // 2786
			"perform_clap.snd", // 2787
			"perform_cough.snd", // 2788
			"perform_crickets.snd", // 2789
			"placeholder_wep_mode1_hit.snd", // 2790
			"placeholder_wep_mode2_hit.snd", // 2791
			"placeholder_wep_mode3_hit.snd", // 2792
			"pl_all_bodyfall.snd", // 2793
			"pl_all_disembark.snd", // 2794
			"pl_all_draw_item.snd", // 2795
			"pl_all_embark.snd", // 2796
			"pl_all_mount.snd", // 2797
			"pl_all_pound_on_chest.snd", // 2798
			"pl_all_sit.snd", // 2799
			"pl_all_snap_fingers.snd", // 2800
			"pl_fist_in_palm.snd", // 2801
			"pl_force_absorb.snd", // 2802
			"pl_force_blast.snd", // 2803
			"pl_force_blast_end.snd", // 2804
			"pl_force_blast_lp.snd", // 2805
			"pl_force_blast_start.snd", // 2806
			"pl_force_channel.snd", // 2807
			"pl_force_choke.snd", // 2808
			"pl_force_choke_end.snd", // 2809
			"pl_force_choke_lp.snd", // 2810
			"pl_force_choke_start.snd", // 2811
			"pl_force_destruction.snd", // 2812
			"pl_force_flip.snd", // 2813
			"pl_force_generic.snd", // 2814
			"pl_force_generic_end.snd", // 2815
			"pl_force_generic_lp.snd", // 2816
			"pl_force_generic_start.snd", // 2817
			"pl_force_healing.snd", // 2818
			"pl_force_healing_end.snd", // 2819
			"pl_force_healing_lp.snd", // 2820
			"pl_force_healing_start.snd", // 2821
			"pl_force_illusion.snd", // 2822
			"pl_force_jump.snd", // 2823
			"pl_force_knockback_end.snd", // 2824
			"pl_force_knockback_lp.snd", // 2825
			"pl_force_knockback_start.snd", // 2826
			"pl_force_lightning_begin.snd", // 2827
			"pl_force_lightning_end.snd", // 2828
			"pl_force_lightning_lp.snd", // 2829
			"pl_force_push.snd", // 2830
			"pl_force_push_end.snd", // 2831
			"pl_force_push_lp.snd", // 2832
			"pl_force_push_start.snd", // 2833
			"pl_force_speed_end.snd", // 2834
			"pl_force_speed_lp.snd", // 2835
			"pl_force_speed_start.snd", // 2836
			"pl_force_strength_end.snd", // 2837
			"pl_force_strength_lp.snd", // 2838
			"pl_force_strength_start.snd", // 2839
			"pl_force_subtract.snd", // 2840
			"pl_force_tangle.snd", // 2841
			"pl_force_throw.snd", // 2842
			"pl_force_throw_end.snd", // 2843
			"pl_force_throw_lp.snd", // 2844
			"pl_force_throw_start.snd", // 2845
			"pl_force_weaken_end.snd", // 2846
			"pl_force_weaken_lp.snd", // 2847
			"pl_force_weaken_start.snd", // 2848
			"pl_hit_unarmed.snd", // 2849
			"pl_kick_hv_hit_flesh.snd", // 2850
			"pl_kick_hv_hit_metal.snd", // 2851
			"pl_kick_hv_hit_stone.snd", // 2852
			"pl_kick_hv_hit_wood.snd", // 2853
			"pl_kick_lt_hit_flesh.snd", // 2854
			"pl_kick_lt_hit_metal.snd", // 2855
			"pl_kick_lt_hit_stone.snd", // 2856
			"pl_kick_lt_hit_wood.snd", // 2857
			"pl_punch_hv_hit_flesh.snd", // 2858
			"pl_punch_hv_hit_metal.snd", // 2859
			"pl_punch_hv_hit_stone.snd", // 2860
			"pl_punch_hv_hit_wood.snd", // 2861
			"pl_punch_lt_hit_flesh.snd", // 2862
			"pl_punch_lt_hit_metal.snd", // 2863
			"pl_punch_lt_hit_stone.snd", // 2864
			"pl_punch_lt_hit_wood.snd", // 2865
			"pl_swing_fist.snd", // 2866
			"pl_swing_kick.snd", // 2867
			"pl_swing_unarmed.snd", // 2868
			"pmusic_song1_drum_flourish1.snd", // 2869
			"pmusic_song1_drum_flourish2.snd", // 2870
			"pmusic_song1_drum_mainloop.snd", // 2871
			"pmusic_song1_khorn_flourish1.snd", // 2872
			"pmusic_song1_khorn_flourish2.snd", // 2873
			"pmusic_song1_khorn_mainloop.snd", // 2874
			"pmusic_song1_mandoviol_flourish1.snd", // 2875
			"pmusic_song1_mandoviol_flourish2.snd", // 2876
			"pmusic_song1_mandoviol_mainloop.snd", // 2877
			"pmusic_song1_nalargon_flourish1.snd", // 2878
			"pmusic_song1_nalargon_flourish2.snd", // 2879
			"pmusic_song1_nalargon_mainloop.snd", // 2880
			"pmusic_song1_shorn_flourish1.snd", // 2881
			"pmusic_song1_shorn_flourish2.snd", // 2882
			"pmusic_song1_shorn_mainloop.snd", // 2883
			"probot_impact.snd", // 2884
			"skill_qualify.snd", // 2885
			"sta_berserk_off.snd", // 2886
			"sta_berserk_on.snd", // 2887
			"sta_bleeding_off.snd", // 2888
			"sta_bleeding_on.snd", // 2889
			"sta_blind_off.snd", // 2890
			"sta_blind_on.snd", // 2891
			"sta_diseased_off.snd", // 2892
			"sta_diseased_on.snd", // 2893
			"sta_dizzy_off.snd", // 2894
			"sta_dizzy_on.snd", // 2895
			"sta_intimidated_off.snd", // 2896
			"sta_intimidated_on.snd", // 2897
			"sta_onfire_off.snd", // 2898
			"sta_onfire_on.snd", // 2899
			"sta_poisoned_off.snd", // 2900
			"sta_poisoned_on.snd", // 2901
			"sta_rooted_off.snd", // 2902
			"sta_rooted_on.snd", // 2903
			"sta_slow_off.snd", // 2904
			"sta_slow_on.snd", // 2905
			"sta_snared_off.snd", // 2906
			"sta_snared_on.snd", // 2907
			"sta_stunned_off.snd", // 2908
			"sta_stunned_on.snd", // 2909
			"str_door_blast_close.snd", // 2910
			"str_door_blast_close_stop.snd", // 2911
			"str_door_blast_open.snd", // 2912
			"str_door_blast_open_stop.snd", // 2913
			"str_door_blast_run_lp.snd", // 2914
			"str_door_blast_start.snd", // 2915
			"str_door_large_powered.snd", // 2916
			"str_door_lrg_pwrd_close_stop.snd", // 2917
			"str_door_lrg_pwrd_open_stop.snd", // 2918
			"str_door_lrg_pwrd_run_lp.snd", // 2919
			"str_door_lrg_pwrd_start.snd", // 2920
			"str_laser_fence_idle_lp.snd", // 2921
			"str_laser_fence_power_off.snd", // 2922
			"str_laser_fence_power_on.snd", // 2923
			"str_turret_move_lp.snd", // 2924
			"str_turret_pwr_off.snd", // 2925
			"str_turret_pwr_on.snd", // 2926
			"st_generic_death.snd", // 2927
			"st_generic_hit.snd", // 2928
			"swim_underwater.snd", // 2929
			"swim_water_surface.snd", // 2930
			"tmpk_alarm_burglar_lp.snd", // 2931
			"tmpk_alarm_lp.snd", // 2932
			"tmpk_alarm_lp_special.snd", // 2933
			"tmpk_siren_lp.snd", // 2934
			"tmpk_siren_lp_special.snd", // 2935
			"tmpk_starship_crashing.snd", // 2936
			"tmpk_starship_crash_explode.snd", // 2937
			"treadwell_chatter_01.snd", // 2938
			"treadwell_chatter_02.snd", // 2939
			"tut_00_bazaar_tease.snd", // 2940
			"tut_00_camera.snd", // 2941
			"tut_00_code_of_conduct.snd", // 2942
			"tut_00_congratulations.snd", // 2943
			"tut_00_dispenser1.snd", // 2944
			"tut_00_dispenser2.snd", // 2945
			"tut_00_follow_arrow.snd", // 2946
			"tut_00_holocron.snd", // 2947
			"tut_00_keymaps.snd", // 2948
			"tut_00_keymap_2.snd", // 2949
			"tut_00_mission_terminal.snd", // 2950
			"tut_00_statmigration.snd", // 2951
			"tut_00_stat_open.snd", // 2952
			"tut_00_swear.snd", // 2953
			"tut_00_swear_reminder.snd", // 2954
			"tut_00_toolbardrag.snd", // 2955
			"tut_01_welcome.snd", // 2956
			"tut_02_movement.snd", // 2957
			"tut_03_scroll_out.snd", // 2958
			"tut_04_chat.snd", // 2959
			"tut_05_remind_chat.snd", // 2960
			"tut_06_excellent.snd", // 2961
			"tut_07_comeon.snd", // 2962
			"tut_08_imperialofficer.snd", // 2963
			"tut_09_lookat.snd", // 2964
			"tut_10_radialmenu.snd", // 2965
			"tut_11_converse.snd", // 2966
			"tut_12_conversation.snd", // 2967
			"tut_13_justtype.snd", // 2968
			"tut_14_openbox.snd", // 2969
			"tut_15_opencontainer.snd", // 2970
			"tut_16_a_youcantake.snd", // 2971
			"tut_16_intheboxyouneed.snd", // 2972
			"tut_17_pickup.snd", // 2973
			"tut_18_inventory.snd", // 2974
			"tut_19_inventory.snd", // 2975
			"tut_20_selectfooditem.snd", // 2976
			"tut_21_usefood.snd", // 2977
			"tut_22_attributes.snd", // 2978
			"tut_23_toolbar.snd", // 2979
			"tut_24_toolbarslots.snd", // 2980
			"tut_25_openinventory.snd", // 2981
			"tut_26_closeinventory.snd", // 2982
			"tut_27_proceed.snd", // 2983
			"tut_28_converse.snd", // 2984
			"tut_29_itemdispenser.snd", // 2985
			"tut_30_dispenser.snd", // 2986
			"tut_31_bank.snd", // 2987
			"tut_32_bank.snd", // 2988
			"tut_33_cash.snd", // 2989
			"tut_34_proceed.snd", // 2990
			"tut_35_elevator.snd", // 2991
			"tut_36_movedownhall.snd", // 2992
			"tut_37_cloning.snd", // 2993
			"tut_38_insurance.snd", // 2994
			"tut_39_gosee.snd", // 2995
			"tut_40_converse.snd", // 2996
			"tut_41_advancewarning.snd", // 2997
			"tut_42_map.snd", // 2998
			"tut_43_zoommap.snd", // 2999
			"tut_44_attacking.snd", // 3000
			"tut_45_proceed.snd", // 3001
			"tut_46_combattoolbar.snd", // 3002
			"tut_47_defaultattack.snd", // 3003
			"tut_48_targetattribs.snd", // 3004
			"tut_49_skilltrainer.snd", // 3005
			"tut_50_skillbrowser.snd", // 3006
			"tut_51_charactersheet.snd", // 3007
			"tut_52_walkdown.snd", // 3008
			"tut_53_missions.snd", // 3009
			"tut_54_npcmission.snd", // 3010
			"tut_55_waypoints.snd", // 3011
			"tut_56_quartermaster.snd", // 3012
			"tut_57_quartermaster.snd", // 3013
			"tut_58_pledge.snd", // 3014
			"tut_59_pledge.snd", // 3015
			"tut_60_thankyou.snd", // 3016
			"ui_alarm_clock1.snd", // 3017
			"ui_alarm_clock2.snd", // 3018
			"ui_alarm_clock3.snd", // 3019
			"ui_alarm_clock4.snd", // 3020
			"ui_alarm_clock5.snd", // 3021
			"ui_backpack_close.snd", // 3022
			"ui_backpack_open.snd", // 3023
			"ui_button_arrow_back.snd", // 3024
			"ui_button_arrow_forward.snd", // 3025
			"ui_button_confirm.snd", // 3026
			"ui_button_random.snd", // 3027
			"ui_circular_load.snd", // 3028
			"ui_close_window.snd", // 3029
			"ui_cybernetics_cannot_use.snd", // 3030
			"ui_danger_message.snd", // 3031
			"ui_deselect.snd", // 3032
			"ui_dialog_warning.snd", // 3033
			"ui_equip_blaster.snd", // 3034
			"ui_incoming_im.snd", // 3035
			"ui_incoming_mail.snd", // 3036
			"ui_increment_big.snd", // 3037
			"ui_increment_small.snd", // 3038
			"ui_journal_updated.snd", // 3039
			"ui_keyboard_clicking.snd", // 3040
			"ui_negative.snd", // 3041
			"ui_objective_reached.snd", // 3042
			"ui_received_quest_item.snd", // 3043
			"ui_rollover.snd", // 3044
			"ui_select.snd", // 3045
			"ui_select_info.snd", // 3046
			"ui_select_popup.snd", // 3047
			"ui_select_rotate.snd", // 3048
			"ui_skill_qualify.snd", // 3049
			"ui_toggle_mouse_mode.snd", // 3050
			"ui_transition_doors.snd", // 3051
			"ui_use_toolbar.snd", // 3052
			"veh_aat_accel.snd", // 3053
			"veh_aat_decel.snd", // 3054
			"veh_aat_idle_lp.snd", // 3055
			"veh_aat_run_lp.snd", // 3056
			"veh_airtaxi_accel.snd", // 3057
			"veh_airtaxi_decel.snd", // 3058
			"veh_airtaxi_idle_lp.snd", // 3059
			"veh_airtaxi_rise.snd", // 3060
			"veh_airtaxi_run_lp.snd", // 3061
			"veh_av21speeder_accel.snd", // 3062
			"veh_av21speeder_decel.snd", // 3063
			"veh_av21speeder_idle_lp.snd", // 3064
			"veh_av21speeder_run_lp.snd", // 3065
			"veh_awing_enginenoise_loop.snd", // 3066
			"veh_barc_speeder_accel.snd", // 3067
			"veh_barc_speeder_decel.snd", // 3068
			"veh_barc_speeder_idle.snd", // 3069
			"veh_barc_speeder_run.snd", // 3070
			"veh_broken_bang.snd", // 3071
			"veh_broken_clank.snd", // 3072
			"veh_broken_sputter.snd", // 3073
			"veh_broken_whir.snd", // 3074
			"veh_collision.snd", // 3075
			"veh_collision_hvy.snd", // 3076
			"veh_desertskiff_accel.snd", // 3077
			"veh_desertskiff_decel.snd", // 3078
			"veh_desertskiff_idle_lp.snd", // 3079
			"veh_desertskiff_run_lp.snd", // 3080
			"veh_distant_gunboat.snd", // 3081
			"veh_distant_shuttle.snd", // 3082
			"veh_distant_skyhopper.snd", // 3083
			"veh_distant_tiefighter.snd", // 3084
			"veh_distant_transport.snd", // 3085
			"veh_distant_xwing.snd", // 3086
			"veh_enter_water.snd", // 3087
			"veh_ewokglider_collision.snd", // 3088
			"veh_ewokglider_land.snd", // 3089
			"veh_ewokglider_launch.snd", // 3090
			"veh_ewokglider_movement.snd", // 3091
			"veh_exit_water.snd", // 3092
			"veh_flashspeeder_accel.snd", // 3093
			"veh_flashspeeder_decel.snd", // 3094
			"veh_flashspeeder_idle_lp.snd", // 3095
			"veh_flashspeeder_run_lp.snd", // 3096
			"veh_fs_atat.snd", // 3097
			"veh_gianspeeder_accel.snd", // 3098
			"veh_gianspeeder_decel.snd", // 3099
			"veh_gianspeeder_idle_lp.snd", // 3100
			"veh_gianspeeder_run_lp.snd", // 3101
			"veh_gunboat_accel.snd", // 3102
			"veh_gunboat_decel.snd", // 3103
			"veh_gunboat_idle_lp.snd", // 3104
			"veh_gunboat_run_lp.snd", // 3105
			"veh_jetpack_accel.snd", // 3106
			"veh_jetpack_decel.snd", // 3107
			"veh_jetpack_idle.snd", // 3108
			"veh_jetpack_run_lp.snd", // 3109
			"veh_lambda_landing.snd", // 3110
			"veh_lambda_taking_off.snd", // 3111
			"veh_object_collision.snd", // 3112
			"veh_sailbarge_accel.snd", // 3113
			"veh_sailbarge_decel.snd", // 3114
			"veh_sailbarge_idle_lp.snd", // 3115
			"veh_sailbarge_rise.snd", // 3116
			"veh_sailbarge_run_lp.snd", // 3117
			"veh_sandcrawler_accel.snd", // 3118
			"veh_sandcrawler_decel.snd", // 3119
			"veh_sandcrawler_idle_lp.snd", // 3120
			"veh_sandcrawler_run_lp.snd", // 3121
			"veh_shuttle_idle.snd", // 3122
			"veh_shuttle_powerdown.snd", // 3123
			"veh_shuttle_powerup.snd", // 3124
			"veh_sswoop_accel.snd", // 3125
			"veh_sswoop_decel.snd", // 3126
			"veh_sswoop_idle_lp.snd", // 3127
			"veh_sswoop_run_lp.snd", // 3128
			"veh_strmtrpxport_accel.snd", // 3129
			"veh_strmtrpxport_decel.snd", // 3130
			"veh_strmtrpxport_idle_lp.snd", // 3131
			"veh_strmtrpxport_run_lp.snd", // 3132
			"veh_swoop_accel.snd", // 3133
			"veh_swoop_decel.snd", // 3134
			"veh_swoop_idle_lp.snd", // 3135
			"veh_swoop_run_lp.snd", // 3136
			"veh_s_foil_movement.snd", // 3137
			"veh_t16skyhopper_accel.snd", // 3138
			"veh_t16skyhopper_decel.snd", // 3139
			"veh_t16skyhopper_idle_lp.snd", // 3140
			"veh_t16skyhopper_rise.snd", // 3141
			"veh_t16skyhopper_run_lp.snd", // 3142
			"veh_t47snowspeeder_accel.snd", // 3143
			"veh_t47snowspeeder_decel.snd", // 3144
			"veh_t47snowspeeder_idle_lp.snd", // 3145
			"veh_t47snowspeeder_rise.snd", // 3146
			"veh_t47snowspeeder_run_lp.snd", // 3147
			"veh_tieadvanced_enginenoise_loop.snd", // 3148
			"veh_tiebomber_enginenoise_loop.snd", // 3149
			"veh_tiefighter_flyover.snd", // 3150
			"veh_tiefighter_idle_lp.snd", // 3151
			"veh_tieinterceptor_enginenoise_loop.snd", // 3152
			"veh_tie_fighter_enginenoise_loop.snd", // 3153
			"veh_tie_fighter_flyby_2.snd", // 3154
			"veh_tie_fighter_shot.snd", // 3155
			"veh_transport_flying.snd", // 3156
			"veh_transport_idle.snd", // 3157
			"veh_transport_landing.snd", // 3158
			"veh_transport_takeoff.snd", // 3159
			"veh_xp38speeder_accel.snd", // 3160
			"veh_xp38speeder_decel.snd", // 3161
			"veh_xp38speeder_idle_lp.snd", // 3162
			"veh_xp38speeder_run_lp.snd", // 3163
			"veh_xwing_flyover.snd", // 3164
			"veh_xwing_idle_lp.snd", // 3165
			"veh_x_wing_enginenoise_loop.snd", // 3166
			"veh_x_wing_flyby.snd", // 3167
			"veh_y_wing_flyby.snd", // 3168
			"voc_wookiee_loud_2sec.snd", // 3169
			"voc_wookiee_loud_4sec.snd", // 3170
			"voc_wookiee_loud_6sec.snd", // 3171
			"voc_wookiee_med_2sec.snd", // 3172
			"voc_wookiee_med_4sec.snd", // 3173
			"voc_wookiee_med_6sec.snd", // 3174
			"voc_wookiee_soft_2sec.snd", // 3175
			"voc_wookiee_soft_4sec.snd", // 3176
			"voc_wookiee_soft_6sec.snd", // 3177
			"voice_stormtrp_movealng.snd", // 3178
			"wall_of_mist_barrier_os.snd", // 3179
			"wall_of_mist_music.snd", // 3180
			"water_enter.snd", // 3181
			"water_exit.snd", // 3182
			"wep_2h_sword_kashyyk.snd", // 3183
			"wep_2h_sword_maul.snd", // 3184
			"wep_2h_sword_scythe.snd", // 3185
			"wep_2h_sword_sith.snd", // 3186
			"wep_activate_lightsaber.snd", // 3187
			"wep_antigrav_bomb_act.snd", // 3188
			"wep_atst_fire.snd", // 3189
			"wep_axe.snd", // 3190
			"wep_axe_vibro.snd", // 3191
			"wep_baton_gaderiffi.snd", // 3192
			"wep_baton_stun.snd", // 3193
			"wep_battleaxe.snd", // 3194
			"wep_battleaxe_quest.snd", // 3195
			"wep_blasterfist.snd", // 3196
			"wep_blaster_fired.snd", // 3197
			"wep_blaster_rifle.snd", // 3198
			"wep_block_turret_bolt.snd", // 3199
			"wep_blunt_vibro_hit_flesh.snd", // 3200
			"wep_blunt_vibro_hit_metal.snd", // 3201
			"wep_blunt_vibro_hit_stone.snd", // 3202
			"wep_blunt_vibro_hit_terrain.snd", // 3203
			"wep_blunt_vibro_hit_wood.snd", // 3204
			"wep_bowcaster_miss.snd", // 3205
			"wep_carbine_alliance_needler.snd", // 3206
			"wep_carbine_blaster_cdef.snd", // 3207
			"wep_carbine_blaster_cdef_corsec.snd", // 3208
			"wep_carbine_bothan_bola.snd", // 3209
			"wep_carbine_czerka_dart.snd", // 3210
			"wep_carbine_e5.snd", // 3211
			"wep_carbine_fired.snd", // 3212
			"wep_carbine_geo.snd", // 3213
			"wep_carbine_nym_slugthrower.snd", // 3214
			"wep_carbine_proton.snd", // 3215
			"wep_carbine_quest_smuggler_underslung.snd", // 3216
			"wep_carbine_underslung.snd", // 3217
			"wep_charged.snd", // 3218
			"wep_cleaver.snd", // 3219
			"wep_cryoban_explode.snd", // 3220
			"wep_cryo_gen_act.snd", // 3221
			"wep_deactivate_lightsaber.snd", // 3222
			"wep_detonator_hit_terrain.snd", // 3223
			"wep_detonator_hit_water.snd", // 3224
			"wep_disruptor_hit_flesh.snd", // 3225
			"wep_disruptor_hit_metal.snd", // 3226
			"wep_disruptor_hit_stone.snd", // 3227
			"wep_disruptor_hit_terrain.snd", // 3228
			"wep_disruptor_hit_water.snd", // 3229
			"wep_disruptor_hit_wood.snd", // 3230
			"wep_disruptor_pistol.snd", // 3231
			"wep_disruptor_rifle.snd", // 3232
			"wep_edged_vibro_hit_flesh.snd", // 3233
			"wep_edged_vibro_hit_metal.snd", // 3234
			"wep_edged_vibro_hit_stone.snd", // 3235
			"wep_edged_vibro_hit_terrain.snd", // 3236
			"wep_edged_vibro_hit_wood.snd", // 3237
			"wep_emp_rifle_hit_flesh.snd", // 3238
			"wep_emp_rifle_hit_metal.snd", // 3239
			"wep_emp_rifle_hit_stone.snd", // 3240
			"wep_emp_rifle_hit_terrain.snd", // 3241
			"wep_emp_rifle_hit_water.snd", // 3242
			"wep_emp_rifle_hit_wood.snd", // 3243
			"wep_ep1_republic_blaster.snd", // 3244
			"wep_executioners_hack.snd", // 3245
			"wep_fire_blaster.snd", // 3246
			"wep_fire_dh17.snd", // 3247
			"wep_fire_e11.snd", // 3248
			"wep_fire_jawa_stun_blaster.snd", // 3249
			"wep_flamethrower_idle_lp.snd", // 3250
			"wep_flamethrower_off.snd", // 3251
			"wep_flamethrower_on.snd", // 3252
			"wep_flamethrower_shoot_lp.snd", // 3253
			"wep_flame_thrower.snd", // 3254
			"wep_flyby_energy.snd", // 3255
			"wep_flyby_kinetic.snd", // 3256
			"wep_frag_gren_act.snd", // 3257
			"wep_generic_arm.snd", // 3258
			"wep_generic_armed.snd", // 3259
			"wep_generic_disarm.snd", // 3260
			"wep_geonos_sonic_fire.snd", // 3261
			"wep_geonos_sonic_hit_flesh.snd", // 3262
			"wep_geonos_sonic_hit_other.snd", // 3263
			"wep_geonos_sonic_miss.snd", // 3264
			"wep_glop_grenade_explode_1.snd", // 3265
			"wep_glop_grenade_explode_2.snd", // 3266
			"wep_glop_grenade_lg_pop_1.snd", // 3267
			"wep_glop_grenade_lg_pop_2.snd", // 3268
			"wep_glop_grenade_sizzle_1.snd", // 3269
			"wep_glop_grenade_sizzle_2.snd", // 3270
			"wep_glop_grenade_sm_pop_1.snd", // 3271
			"wep_glop_grenade_sm_pop_2.snd", // 3272
			"wep_glop_gren_act.snd", // 3273
			"wep_grenade_hit_terrain.snd", // 3274
			"wep_grenade_hit_water.snd", // 3275
			"wep_grenade_off.snd", // 3276
			"wep_grenade_on.snd", // 3277
			"wep_heavy_acid_beam.snd", // 3278
			"wep_heavy_acid_launcher_hit.snd", // 3279
			"wep_heavy_idle_on.snd", // 3280
			"wep_heavy_lightning_beam.snd", // 3281
			"wep_heavy_off.snd", // 3282
			"wep_heavy_on.snd", // 3283
			"wep_heavy_particle_beam.snd", // 3284
			"wep_heavy_rocket_launcher.snd", // 3285
			"wep_hit_armor_80.snd", // 3286
			"wep_hit_blaster_creature.snd", // 3287
			"wep_hit_blaster_terrain.snd", // 3288
			"wep_hit_hull_1.snd", // 3289
			"wep_hit_hull_2.snd", // 3290
			"wep_hit_hull_3.snd", // 3291
			"wep_hit_jawa_stun_blaster.snd", // 3292
			"wep_hit_knife_creature.snd", // 3293
			"wep_hit_knife_terrain.snd", // 3294
			"wep_hit_lightsaber_lightsaber.snd", // 3295
			"wep_hit_lightsaber_terrain.snd", // 3296
			"wep_hv_blaster_hit_flesh.snd", // 3297
			"wep_hv_blaster_hit_metal.snd", // 3298
			"wep_hv_blaster_hit_stone.snd", // 3299
			"wep_hv_blaster_hit_terrain.snd", // 3300
			"wep_hv_blaster_hit_water.snd", // 3301
			"wep_hv_blaster_hit_wood.snd", // 3302
			"wep_hv_rocket_hit_flesh.snd", // 3303
			"wep_hv_rocket_hit_metal.snd", // 3304
			"wep_hv_rocket_hit_stone.snd", // 3305
			"wep_hv_rocket_hit_terrain.snd", // 3306
			"wep_hv_rocket_hit_water.snd", // 3307
			"wep_hv_rocket_hit_wood.snd", // 3308
			"wep_idle1_lightsaber.snd", // 3309
			"wep_idle2_lightsaber.snd", // 3310
			"wep_imp_2nd_acid.snd", // 3311
			"wep_imp_2nd_cold.snd", // 3312
			"wep_imp_2nd_electric.snd", // 3313
			"wep_imp_2nd_heat.snd", // 3314
			"wep_imp_det_act.snd", // 3315
			"wep_imp_energy.snd", // 3316
			"wep_imp_kinetic.snd", // 3317
			"wep_ion_hit_hull_2.snd", // 3318
			"wep_kamino_saberdart_gun.snd", // 3319
			"wep_katana.snd", // 3320
			"wep_katana_quest.snd", // 3321
			"wep_knife_donkuwah.snd", // 3322
			"wep_knife_janta.snd", // 3323
			"wep_knife_stone.snd", // 3324
			"wep_knife_survival.snd", // 3325
			"wep_knife_twilek_dagger.snd", // 3326
			"wep_knife_vibroblade.snd", // 3327
			"wep_knife_vibroblade_quest.snd", // 3328
			"wep_knuckler_hit_flesh.snd", // 3329
			"wep_knuckler_hit_metal.snd", // 3330
			"wep_knuckler_hit_terrain.snd", // 3331
			"wep_knuckler_vibro.snd", // 3332
			"wep_lance_cryo.snd", // 3333
			"wep_lance_kaminoan.snd", // 3334
			"wep_lance_massassi.snd", // 3335
			"wep_lance_nightsister.snd", // 3336
			"wep_lance_shock.snd", // 3337
			"wep_lance_vibro.snd", // 3338
			"wep_lance_vibro_controller_fp.snd", // 3339
			"wep_lance_vibro_nightsister.snd", // 3340
			"wep_landmine_off.snd", // 3341
			"wep_landmine_on.snd", // 3342
			"wep_landmine_place.snd", // 3343
			"wep_laser_fired.snd", // 3344
			"wep_lg_blade_hit_flesh.snd", // 3345
			"wep_lg_blade_hit_metal.snd", // 3346
			"wep_lg_blade_hit_stone.snd", // 3347
			"wep_lg_blade_hit_terrain.snd", // 3348
			"wep_lg_blade_hit_wood.snd", // 3349
			"wep_lightning_rifle_fire.snd", // 3350
			"wep_lightning_rifle_hit.snd", // 3351
			"wep_lightsaber_deflect_shot.snd", // 3352
			"wep_lightsaber_hit.snd", // 3353
			"wep_lightsaber_hit_flesh.snd", // 3354
			"wep_lightsaber_hit_metal.snd", // 3355
			"wep_lightsaber_hit_stone.snd", // 3356
			"wep_lightsaber_hit_terrain.snd", // 3357
			"wep_lightsaber_hit_water.snd", // 3358
			"wep_lightsaber_hit_wood.snd", // 3359
			"wep_lightsaber_idle.snd", // 3360
			"wep_lightsaber_swing.snd", // 3361
			"wep_lightsaber_thrown_hit.snd", // 3362
			"wep_lightsaber_vs_lrg_hrd.snd", // 3363
			"wep_lightsaber_vs_lrg_sft.snd", // 3364
			"wep_lightsaber_vs_sml_hrd.snd", // 3365
			"wep_lightsaber_vs_sml_sft.snd", // 3366
			"wep_lrg_blunt_miss.snd", // 3367
			"wep_lrg_sharp_miss.snd", // 3368
			"wep_lt_blaster_hit_flesh.snd", // 3369
			"wep_lt_blaster_hit_metal.snd", // 3370
			"wep_lt_blaster_hit_stone.snd", // 3371
			"wep_lt_blaster_hit_terrain.snd", // 3372
			"wep_lt_blaster_hit_water.snd", // 3373
			"wep_lt_blaster_hit_wood.snd", // 3374
			"wep_lt_rocket_hit_flesh.snd", // 3375
			"wep_lt_rocket_hit_metal.snd", // 3376
			"wep_lt_rocket_hit_stone.snd", // 3377
			"wep_lt_rocket_hit_terrain.snd", // 3378
			"wep_lt_rocket_hit_water.snd", // 3379
			"wep_lt_rocket_hit_wood.snd", // 3380
			"wep_mace_hit_flesh.snd", // 3381
			"wep_mace_hit_stone.snd", // 3382
			"wep_mace_hit_terrain.snd", // 3383
			"wep_massassiknuckler.snd", // 3384
			"wep_mine_act.snd", // 3385
			"wep_nightsister_halberd_swing.snd", // 3386
			"wep_particle_cannon_fire.snd", // 3387
			"wep_particle_cannon_hit.snd", // 3388
			"wep_particle_rifle_fire.snd", // 3389
			"wep_particle_rifle_hit.snd", // 3390
			"wep_pistol_alliance_disruptor.snd", // 3391
			"wep_pistol_blaster_cdef.snd", // 3392
			"wep_pistol_blaster_cdef_corsec.snd", // 3393
			"wep_pistol_blaster_d18.snd", // 3394
			"wep_pistol_blaster_dh17.snd", // 3395
			"wep_pistol_blaster_dl44.snd", // 3396
			"wep_pistol_blaster_dl44_metal.snd", // 3397
			"wep_pistol_blaster_power5.snd", // 3398
			"wep_pistol_blaster_scout_trooper.snd", // 3399
			"wep_pistol_blaster_scout_trooper_corsec.snd", // 3400
			"wep_pistol_blaster_short_range_combat.snd", // 3401
			"wep_pistol_clone_trooper.snd", // 3402
			"wep_pistol_deathhammer_needle.snd", // 3403
			"wep_pistol_de_10.snd", // 3404
			"wep_pistol_disrupter_dx2.snd", // 3405
			"wep_pistol_fired.snd", // 3406
			"wep_pistol_flare.snd", // 3407
			"wep_pistol_flechette.snd", // 3408
			"wep_pistol_flechette_fwg5.snd", // 3409
			"wep_pistol_flechette_fwg5_quest.snd", // 3410
			"wep_pistol_geo_sonic_blaster.snd", // 3411
			"wep_pistol_intimidator.snd", // 3412
			"wep_pistol_jawa.snd", // 3413
			"wep_pistol_kyd21.snd", // 3414
			"wep_pistol_launcher.snd", // 3415
			"wep_pistol_projectile_striker.snd", // 3416
			"wep_pistol_quest_imperial_navy_formal.snd", // 3417
			"wep_pistol_renegade.snd", // 3418
			"wep_pistol_republic_blaster.snd", // 3419
			"wep_pistol_republic_blaster_quest.snd", // 3420
			"wep_pistol_scatter.snd", // 3421
			"wep_pistol_trandoshan_suppressor.snd", // 3422
			"wep_polearm_electric.snd", // 3423
			"wep_polearm_kashyyk_bladestick.snd", // 3424
			"wep_poleaxe_vibro.snd", // 3425
			"wep_projectile_hit_flesh.snd", // 3426
			"wep_projectile_hit_metal_01.snd", // 3427
			"wep_projectile_hit_metal_02.snd", // 3428
			"wep_projectile_hit_stone_01.snd", // 3429
			"wep_projectile_hit_stone_02.snd", // 3430
			"wep_projectile_hit_terrain.snd", // 3431
			"wep_projectile_hit_water.snd", // 3432
			"wep_projectile_hit_wood.snd", // 3433
			"wep_projectile_pistol.snd", // 3434
			"wep_projectile_rifle.snd", // 3435
			"wep_protontorpedo_fire.snd", // 3436
			"wep_protontorpedo_hit.snd", // 3437
			"wep_quest_battleaxe.snd", // 3438
			"wep_quest_heavy_acid_beam.snd", // 3439
			"wep_quest_heavy_particle_beam.snd", // 3440
			"wep_quest_maul.snd", // 3441
			"wep_quest_pistol_launcher.snd", // 3442
			"wep_quest_pistol_republic_blaster_quest.snd", // 3443
			"wep_quest_rifle_flame_thrower.snd", // 3444
			"wep_quest_rifle_lightning.snd", // 3445
			"wep_quest_rifle_projectile_tusken.snd", // 3446
			"wep_radar_dish_turret_bolt.snd", // 3447
			"wep_razor_knuckler.snd", // 3448
			"wep_rebel_lasershot_2.snd", // 3449
			"wep_rebel_lasershot_9.snd", // 3450
			"wep_reloaded.snd", // 3451
			"wep_rifle_acid_beam.snd", // 3452
			"wep_rifle_adventurer_hv_sniper.snd", // 3453
			"wep_rifle_beam.snd", // 3454
			"wep_rifle_berserker.snd", // 3455
			"wep_rifle_blaster_cdef.snd", // 3456
			"wep_rifle_blaster_dlt20.snd", // 3457
			"wep_rifle_blaster_dlt20a.snd", // 3458
			"wep_rifle_blaster_e11.snd", // 3459
			"wep_rifle_blaster_ionization_jawa.snd", // 3460
			"wep_rifle_blaster_laser_rifle.snd", // 3461
			"wep_rifle_bowcaster.snd", // 3462
			"wep_rifle_clone_trooper.snd", // 3463
			"wep_rifle_disrupter_dxr6.snd", // 3464
			"wep_rifle_fired.snd", // 3465
			"wep_rifle_flame_thrower.snd", // 3466
			"wep_rifle_geo_drill.snd", // 3467
			"wep_rifle_ld1.snd", // 3468
			"wep_rifle_lightning.snd", // 3469
			"wep_rifle_light_blaster_dh17_carbine.snd", // 3470
			"wep_rifle_light_blaster_dh17_carbine_black.snd", // 3471
			"wep_rifle_light_blaster_dh17_carbine_snubnose.snd", // 3472
			"wep_rifle_light_blaster_e11_carbine.snd", // 3473
			"wep_rifle_light_blaster_e11_carbine_quest.snd", // 3474
			"wep_rifle_light_blaster_e11_carbine_victor.snd", // 3475
			"wep_rifle_light_blaster_ee3.snd", // 3476
			"wep_rifle_light_blaster_imperial_scout_carbine.snd", // 3477
			"wep_rifle_light_blaster_laser_carbine.snd", // 3478
			"wep_rifle_massassi_ink.snd", // 3479
			"wep_rifle_projectile_tusken.snd", // 3480
			"wep_rifle_proton.snd", // 3481
			"wep_rifle_quest_rebel_longrifle.snd", // 3482
			"wep_rifle_sonic_sg82.snd", // 3483
			"wep_rifle_spray_stick_stohli.snd", // 3484
			"wep_rifle_t21.snd", // 3485
			"wep_rifle_tangle_gun7.snd", // 3486
			"wep_rifle_tenloss_disrupter.snd", // 3487
			"wep_rifle_trandoshan_hunters.snd", // 3488
			"wep_rifle_victor_projectile_tusken.snd", // 3489
			"wep_shoulder_launch.snd", // 3490
			"wep_shoulder_off.snd", // 3491
			"wep_shoulder_on.snd", // 3492
			"wep_sml_blunt_miss.snd", // 3493
			"wep_sml_sharp_miss.snd", // 3494
			"wep_sm_blade_hit_flesh.snd", // 3495
			"wep_sm_blade_hit_metal.snd", // 3496
			"wep_sm_blade_hit_stone.snd", // 3497
			"wep_sm_blade_hit_terrain.snd", // 3498
			"wep_sm_blade_hit_wood.snd", // 3499
			"wep_sonic_hit_flesh.snd", // 3500
			"wep_sonic_hit_metal.snd", // 3501
			"wep_sonic_hit_stone.snd", // 3502
			"wep_sonic_hit_terrain.snd", // 3503
			"wep_sonic_hit_water.snd", // 3504
			"wep_sonic_hit_wood.snd", // 3505
			"wep_sonic_pistol.snd", // 3506
			"wep_sonic_rifle.snd", // 3507
			"wep_spraystick_fire.snd", // 3508
			"wep_staff.snd", // 3509
			"wep_staff_janta.snd", // 3510
			"wep_staff_metal.snd", // 3511
			"wep_staff_reinforced.snd", // 3512
			"wep_swing_knife.snd", // 3513
			"wep_swing_lightsaber.snd", // 3514
			"wep_sword.snd", // 3515
			"wep_sword_acid.snd", // 3516
			"wep_sword_curved.snd", // 3517
			"wep_sword_curved_nyax.snd", // 3518
			"wep_sword_junti_mace.snd", // 3519
			"wep_sword_marauder.snd", // 3520
			"wep_sword_massassi.snd", // 3521
			"wep_sword_nyax.snd", // 3522
			"wep_sword_rantok.snd", // 3523
			"wep_sword_rsf.snd", // 3524
			"wep_sword_ryyk_blade.snd", // 3525
			"wep_thermaldet_idle_lp.snd", // 3526
			"wep_thermaldet_off.snd", // 3527
			"wep_thermaldet_on.snd", // 3528
			"wep_thrown_miss.snd", // 3529
			"wep_tower_turret_bolt.snd", // 3530
			"wep_turbolaser_hit_flesh.snd", // 3531
			"wep_turbolaser_hit_metal.snd", // 3532
			"wep_turbolaser_hit_stone.snd", // 3533
			"wep_turbolaser_hit_terrain.snd", // 3534
			"wep_turbolaser_hit_water.snd", // 3535
			"wep_turbolaser_hit_wood.snd", // 3536
			"wep_unarmed.snd", // 3537
			"wep_veh_blaster.snd", // 3538
			"wep_veh_blaster_hit_flesh.snd", // 3539
			"wep_veh_blaster_hit_metal.snd", // 3540
			"wep_veh_blaster_hit_stone.snd", // 3541
			"wep_veh_blaster_hit_terrain.snd", // 3542
			"wep_veh_blaster_hit_water.snd", // 3543
			"wep_veh_blaster_hit_wood.snd", // 3544
			"wep_veh_ion.snd", // 3545
			"wep_veh_ion_hit_flesh.snd", // 3546
			"wep_veh_ion_hit_metal.snd", // 3547
			"wep_veh_ion_hit_stone.snd", // 3548
			"wep_veh_ion_hit_terrain.snd", // 3549
			"wep_veh_ion_hit_water.snd", // 3550
			"wep_veh_ion_hit_wood.snd", // 3551
			"wep_veh_rocket.snd", // 3552
			"wep_vibroweapon_idle_lp.snd", // 3553
			"wep_vibroweapon_off.snd", // 3554
			"wep_vibroweapon_on.snd", // 3555
			"wep_victor_baton_gaderiffi.snd", // 3556
			"wep_wookiee_blaster_glove.snd", // 3557
			"wep_wookiee_bowcaster_adv.snd", // 3558
			"wep_wookiee_bowcaster_carbine.snd", // 3559
			"wep_wookiee_bowcaster_pistol.snd", // 3560
			"wtr_acid_hail_large_lp.snd", // 3561
			"wtr_acid_rain_large_lp.snd", // 3562
			"wtr_dust_storm_large_lp.snd", // 3563
			"wtr_freezing_rain_large_lp.snd", // 3564
			"wtr_gusting_sandstorm_hvy_lp.snd", // 3565
			"wtr_gusting_windstrm_hvy_lp.snd", // 3566
			"wtr_hail_storm_large_lp.snd", // 3567
			"wtr_heavy_rain_large_lp.snd", // 3568
			"wtr_ice_storm_large_lp.snd", // 3569
			"wtr_lightning_storm_lrg_lp.snd", // 3570
			"wtr_lightning_strike.snd", // 3571
			"wtr_light_rain_large_lp.snd", // 3572
			"wtr_rock_storm_large_lp.snd", // 3573
			"wtr_sand_tornado_large_lp.snd", // 3574
			"wtr_thunderstorm_hvy_os.snd", // 3575
			"wtr_thunderstorm_lgt_os.snd", // 3576
			"wtr_thunder_heavy.snd", // 3577
			"wtr_thunder_light.snd", // 3578
			"wtr_tornado_large_lp.snd", // 3579
	};

	// NOTE: Use these values for experimentation as well -- they are identical.
	protected final static int CRAFTING_ASSEMBLY_AMAZING_SUCCESS = 0;
	protected final static int CRAFTING_ASSEMBLY_GREAT_SUCCESS = 1;
	protected final static int CRAFTING_ASSEMBLY_GOOD_SUCCESS = 2;
	protected final static int CRAFTING_ASSEMBLY_MODERATE_SUCCESS = 3;
	protected final static int CRAFTING_ASSEMBLY_SUCCESS = 4;
	protected final static int CRAFTING_ASSEMBLY_MARGINAL_SUCCESS = 5;
	protected final static int CRAFTING_ASSEMBLY_OK = 6;
	protected final static int CRAFTING_ASSEMBLY_BARE_SUCCESS = 7;
	protected final static int CRAFTING_ASSEMBLY_CRITICAL_FAILURE = 8;
	protected final static int CRAFTING_ASSEMBLY_INTERNAL_FAILURE = 9;

	// 13 tool tabs.
	protected final static int CRAFTING_TOOL_TAB_GENERIC_ITEM = 0;
	protected final static int CRAFTING_TOOL_TAB_WEAPON = 1;
	protected final static int CRAFTING_TOOL_TAB_ARMOR = 2;
	protected final static int CRAFTING_TOOL_TAB_FOOD = 4;
	protected final static int CRAFTING_TOOL_TAB_CLOTHING = 8;
	protected final static int CRAFTING_TOOL_TAB_VEHICLE = 16;
	protected final static int CRAFTING_TOOL_TAB_DROID = 32;
	protected final static int CRAFTING_TOOL_TAB_CHEMICAL = 64;
	protected final static int CRAFTING_TOOL_TAB_BIO_ENGINEER_TISSUES = 128;
	protected final static int CRAFTING_TOOL_TAB_BIO_ENGINEER_CREATURES = 256;
	protected final static int CRAFTING_TOOL_TAB_FURNITURE = 512;
	protected final static int CRAFTING_TOOL_TAB_STRUCTURE = 1024;
	protected final static int CRAFTING_TOOL_TAB_JEDI_ITEM = 2048;
	protected final static int CRAFTING_TOOL_TAB_MISCELLANEOUS = 4096;

	protected final static int CHAT_MODIFIER_NONE = 0;
	protected final static int CHAT_MODIFIER_HOWL = 45;
	protected final static int CHAT_MODIFIER_SHOUT = 80;
	protected final static int CHAT_MODIFIER_WHISPER = 99;
	protected final static int CHAT_MODIFIER_SING = 82;

	protected final static byte MEDICAL_ITEM_TYPE_HEAL_DAMAGE = 0;
	protected final static byte MEDICAL_ITEM_TYPE_HEAL_WOUNDS = 1;
	protected final static byte MEDICAL_ITEM_TYPE_HEAL_MIND_DAMAGE = 2;
	protected final static byte MEDICAL_ITEM_TYPE_POISON = 3;
	protected final static byte MEDICAL_ITEM_TYPE_DISEASE = 4;

	protected final static byte MEDICAL_ITEM_TYPE_FIRE_BLANKET = 5;

	protected final static byte MEDICAL_ITEM_TYPE_HEAL_DAMAGE_AREA = 6;
	protected final static byte MEDICAL_ITEM_TYPE_HEAL_WOUNDS_AREA = 7;
	protected final static byte MEDICAL_ITEM_TYPE_POISON_AREA = 8;
	protected final static byte MEDICAL_ITEM_TYPE_DISEASE_AREA = 9;

	protected final static byte MEDICAL_ITEM_TYPE_BUFF = 10;

	protected final static int BUFF_EFFECT_FOOD_BOFA_TREAT = 0;
	protected final static int BUFF_EFFECT_FOOD_TRAVEL_BISCUITS = 1;
	protected final static int BUFF_EFFECT_DRINK_SPICED_TEA = 2;
	protected final static int BUFF_EFFECT_FOOD_BLOB_CANDY = 3;
	protected final static int BUFF_EFFECT_DRINK_CAF = 4;
	protected final static int BUFF_EFFECT_FOOD_PKNAB = 5;
	protected final static int BUFF_EFFECT_FOOD_KANALI_WAFERS = 6;
	protected final static int BUFF_EFFECT_DRINK_JAWA_BEER = 7;
	protected final static int BUFF_EFFECT_FOOD_TELTIER_NOODLES = 8;
	protected final static int BUFF_EFFECT_FOOD_ALMOND_KWEVVU_CRISP = 9;
	protected final static int BUFF_EFFECT_FOOD_VEGHASH = 10;
	protected final static int BUFF_EFFECT_FOOD_GRUUVAN_SHALL = 11;
	protected final static int BUFF_EFFECT_FOOD_SCRIMPI = 12;
	protected final static int BUFF_EFFECT_FOOD_AHRISA = 13;
	protected final static int BUFF_EFFECT_FOOD_RAKRIRIAN_BURNOUT_SAUCE = 14;
	protected final static int BUFF_EFFECT_FOOD_VEGEPARSINE = 15;
	protected final static int BUFF_EFFECT_FOOD_BIVOLI_TEMPARI = 16;
	protected final static int BUFF_EFFECT_FOOD_TERRATTA = 17;
	protected final static int BUFF_EFFECT_FOOD_THAKITILLO = 18;
	protected final static int BUFF_EFFECT_FOOD_VERCUPTI_OF_AGAZZA_BOLERUUEE = 19;
	protected final static int BUFF_EFFECT_DRINK_GRALINYN_JUICE = 20;
	protected final static int BUFF_EFFECT_DRINK_DEUTERIUM_PYRO = 21;
	protected final static int BUFF_EFFECT_DRINK_CORELLIAN_ALE = 22;
	protected final static int BUFF_EFFECT_DRUNK_DURINDFIRE = 23;
	protected final static int BUFF_EFFECT_DRINK_VERONIAN_BERRY_WINE = 24;
	protected final static int BUFF_EFFECT_DRINK_ITHORIAN_MIST = 25;
	protected final static int BUFF_EFFECT_DRINK_CORELLIAN_BRANDY = 26;
	protected final static int BUFF_EFFECT_DRINK_ELSHANDRUUU_PICA_THUNDERCLOUD = 27;
	protected final static int BUFF_EFFECT_DRINK_VASARIAN_BRANDY = 28;
	protected final static int BUFF_EFFECT_DRINK_ACCARRAGM = 29;
	protected final static int BUFF_EFFECT_DRINK_GARRMORL = 30;
	protected final static int BUFF_EFFECT_FOOD_WON_WON = 31;
	protected final static int BUFF_EFFECT_FOOD_CHANDAD = 32;
	protected final static int BUFF_EFFECT_FOOD_DWEEZEL = 33;
	protected final static int BUFF_EFFECT_FOOD_KIWIK_CLUSJO_SWIRL = 34;
	protected final static int BUFF_EFFECT_FOOD_VAGNERIAN_CANAPE = 35;
	protected final static int BUFF_EFFECT_FOOD_BREATH_OF_HEAVEN = 36;
	protected final static int BUFF_EFFECT_FOOD_DAMAGE_MITIGATION = 37;
	protected final static int BUFF_EFFECT_FOOD_HEAL_RECOVERY_TIMEOUT = 38;
	protected final static int BUFF_EFFECT_FOOD_INCAPACITATION_RECOVERY = 39;
	protected final static int BUFF_EFFECT_FOOD_DODGE_ATTACK = 40;
	protected final static int BUFF_EFFECT_FOOD_ATTACK_ACCURACY_BONUS = 41;
	protected final static int BUFF_EFFECT_FOOD_POISON_AND_DISEASE_RESISTANCE = 42;
	protected final static int BUFF_EFFECT_FOOD_BLEEDING_RESISTANCE = 43;
	protected final static int BUFF_EFFECT_FOOD_FIRE_RESISTANCE = 44;
	protected final static int BUFF_EFFECT_FOOD_LEARNING_BONUS = 45;
	protected final static int BUFF_EFFECT_FOOD_CRAFTING_ASSEMBLY_BONUS = 46;
	protected final static int BUFF_EFFECT_FOOD_CRAFTING_EXPERIMENTATION_BONUS = 47;
	protected final static int BUFF_EFFECT_FOOD_REDUCE_CLONING_WOUNDS = 48;
	protected final static int BUFF_EFFECT_FOOD_REDUCE_SPICE_DOWNER_TIME = 49;
	protected final static int BUFF_EFFECT_FOOD_ENTERTAINER_BUFF_EFFICIENCY = 50;
	protected final static int BUFF_EFFECT_MEDICAL_ENHANCE_HEALTH = 51;
	protected final static int BUFF_EFFECT_MEDICAL_ENHANCE_STRENGTH = 52;
	protected final static int BUFF_EFFECT_MEDICAL_ENHANCE_CONSTITUTION = 53;
	protected final static int BUFF_EFFECT_MEDICAL_ENHANCE_ACTION = 54;
	protected final static int BUFF_EFFECT_MEDICAL_ENHANCE_QUICKNESS = 55;
	protected final static int BUFF_EFFECT_MEDICAL_ENHANCE_STAMINA = 56;
	protected final static int BUFF_EFFECT_MEDICAL_POISON_RESISTANCE = 57;
	protected final static int BUFF_EFFECT_MEDICAL_DISEASE_RESISTANCE = 58;
	protected final static int BUFF_EFFECT_ENTERTAINER_ENHANCE_MIND = 59;
	protected final static int BUFF_EFFECT_ENTERTAINER_ENHANCE_FOCUS = 60;
	protected final static int BUFF_EFFECT_ENTERTAINER_ENHANCE_WILLPOWER = 61;
	protected final static int BUFF_EFFECT_SPICE_BOOSTER_BLUE = 62;
	protected final static int BUFF_EFFECT_SPICE_BOOSTER_BLUE_DOWNER = 63;
	protected final static int BUFF_EFFECT_SPICE_CRASH_N_BURN = 64;
	protected final static int BUFF_EFFECT_SPICE_CRASH_N_BURN_DOWNER = 65;
	protected final static int BUFF_EFFECT_SPICE_DROID_LUBE = 66;
	protected final static int BUFF_EFFECT_SPICE_DROID_LUBE_DOWNER = 67;
	protected final static int BUFF_EFFECT_SPICE_GIGGLEDUST = 68;
	protected final static int BUFF_EFFECT_SPICE_GIGGLEDUST_DOWNER = 69;
	protected final static int BUFF_EFFECT_SPICE_GREY_GABAKI = 70;
	protected final static int BUFF_EFFECT_SPICE_GREY_GABAKI_DOWNER = 71;
	protected final static int BUFF_EFFECT_SPICE_GUNJACK = 72;
	protected final static int BUFF_EFFECT_SPICE_GUNJACK_DOWNER = 73;
	protected final static int BUFF_EFFECT_SPICE_MUON_GOLD = 74;
	protected final static int BUFF_EFFECT_SPICE_MUON_GOLD_DOWNER = 75;
	protected final static int BUFF_EFFECT_SPICE_NEUTRON_PIXIE = 76;
	protected final static int BUFF_EFFECT_SPICE_NEUTRON_PIXIE_DOWNER = 77;
	protected final static int BUFF_EFFECT_SPICE_PYREPENOL = 78;
	protected final static int BUFF_EFFECT_SPICE_PYREPENOL_DOWNER = 79;
	protected final static int BUFF_EFFECT_SPICE_SCRAMJET = 80;
	protected final static int BUFF_EFFECT_SPICE_SCRAMJET_DOWNER = 81;
	protected final static int BUFF_EFFECT_SPICE_SEDATIVE_H4B = 82;
	protected final static int BUFF_EFFECT_SPICE_SEDATIVE_H4B_DOWNER = 83;
	protected final static int BUFF_EFFECT_SPICE_SHADOWPAW = 84;
	protected final static int BUFF_EFFECT_SPICE_SHADOWPAW_DOWNER = 85;
	protected final static int BUFF_EFFECT_SPICE_SWEETBLOSSOM = 86;
	protected final static int BUFF_EFFECT_SPICE_SWEETBLOSSOM_DOWNER = 87;
	protected final static int BUFF_EFFECT_SPICE_THRUSTERHEAD = 88;
	protected final static int BUFF_EFFECT_SPICE_THRUSTERHEAD_DOWNER = 89;
	protected final static int BUFF_EFFECT_SPICE_YARROCK = 90;
	protected final static int BUFF_EFFECT_SPICE_YARROCK_DOWNER = 91;
	protected final static int BUFF_EFFECT_SPICE_KLIKNIK_BOOST = 92;
	protected final static int BUFF_EFFECT_SPICE_KLIKNIK_BOOST_DOWNER = 93;
	protected final static int BUFF_EFFECT_SPICE_KWI_BOOST = 94;
	protected final static int BUFF_EFFECT_SPICE_KWI_BOOST_DOWNER = 95;
	protected final static int BUFF_EFFECT_JEDI_FORCE_RUN_1 = 96;
	protected final static int BUFF_EFFECT_JEDI_FORCE_RUN_2 = 97;
	protected final static int BUFF_EFFECT_JEDI_FORCE_RUN_3 = 98;
	protected final static int BUFF_EFFECT_JEDI_FORCE_SPEED_1 = 99;
	protected final static int BUFF_EFFECT_JEDI_FORCE_SPEED_2 = 100;
	protected final static int BUFF_EFFECT_JEDI_FORCE_ARMOR_1 = 101;
	protected final static int BUFF_EFFECT_JEDI_FORCE_ARMOR_2 = 102;
	protected final static int BUFF_EFFECT_JEDI_FORCE_SHIELD_1 = 103;
	protected final static int BUFF_EFFECT_JEDI_FORCE_SHIELD_2 = 104;
	protected final static int BUFF_EFFECT_JEDI_FORCE_PROTECTION_1 = 105;
	protected final static int BUFF_EFFECT_JEDI_FORCE_FEEDBACK_1 = 106;
	protected final static int BUFF_EFFECT_JEDI_FORCE_FEEDBACK_2 = 107;
	protected final static int BUFF_EFFECT_JEDI_FORCE_ABSORB_1 = 108;
	protected final static int BUFF_EFFECT_JEDI_FORCE_ABSORB_2 = 109;
	protected final static int BUFF_EFFECT_JEDI_FORCE_RESIST_DISEASE = 110;
	protected final static int BUFF_EFFECT_JEDI_FORCE_RESIST_POISON = 111;
	protected final static int BUFF_EFFECT_JEDI_FORCE_RESIST_BLEEDING = 112;
	protected final static int BUFF_EFFECT_JEDI_FORCE_RESIST_STATES = 113;
	protected final static int BUFF_EFFECT_JEDI_FORCE_AVOID_INCAPACITATION = 114;
	protected final static int BUFF_EFFECT_SKILL_MELEE_ACCURACY = 115;
	protected final static int BUFF_EFFECT_SKILL_RANGED_DEFENSE = 116;
	protected final static int BUFF_EFFECT_SKILL_CARBINE_ACCURACY = 117;
	protected final static int BUFF_EFFECT_SKILL_CARBINE_SPEED = 118;
	protected final static int BUFF_EFFECT_SKILL_MASK_SCENT = 119;
	protected final static int BUFF_EFFECT_SKILL_MELEE_DEFENSE = 120;
	protected final static int BUFF_EFFECT_SKILL_ONE_HANDED_MELEE_ACCURACY = 121;
	protected final static int BUFF_EFFECT_SKILL_ONE_HANDED_MELEE_SPEED = 122;
	protected final static int BUFF_EFFECT_SKILL_PISTOL_ACCURACY = 123;
	protected final static int BUFF_EFFECT_SKILL_PISTOL_SPEED = 124;
	protected final static int BUFF_EFFECT_SKILL_RIFLE_ACCURACY = 125;
	protected final static int BUFF_EFFECT_SKILL_RIFLE_SPEED = 126;
	protected final static int BUFF_EFFECT_SKILL_UNARMED_ACCURACY = 127;
	protected final static int BUFF_EFFECT_SKILL_UNARMED_SPEED = 128;
	protected final static int BUFF_EFFECT_SKILL_TWO_HANDED_MELEE_ACCURACY = 129;
	protected final static int BUFF_EFFECT_SKILL_TWO_HANDED_MELEE_SPEED = 130;
	protected final static int BUFF_EFFECT_SKILL_RANGED_ATTACK_ACCURACY = 131;
	protected final static int BUFF_EFFECT_SKILL_THROWN_WEAPON_ACCURACY = 132;
	protected final static int BUFF_EFFECT_SKILL_THROWN_WEAPON_SPEED = 133;
	protected final static int BUFF_EFFECT_SKILL_HEAVY_WEAPON_ACCURACY = 134;
	protected final static int BUFF_EFFECT_SKILL_HEAVY_WEAPON_SPEED = 135;
	protected final static int BUFF_EFFECT_JEDI_FRS_SUFFERING = 136;
	protected final static int BUFF_EFFECT_JEDI_FRS_SERENITY = 137;
	protected final static int BUFF_EFFECT_SKILL_POLEARM_ACCURACY = 138;
	protected final static int BUFF_EFFECT_SKILL_POLEARM_SPEED = 139;

	protected final static int[] BUFF_EFFECTS = { 0x8B72DED3, 0x62932AF7,
			0x717401BC, 0x8268C9CB, 0x0BDFFC50, 0xF12D3826, 0xFCBAD7ED,
			0x1D9DAE11, 0x9AC8258E, 0x8B82942E, 0xD94616DE, 0x6F291A67,
			0xBCB608D9, 0x234F7DA8, 0xC2D87307, 0xF02878A3, 0x2114D76D,
			0x904ACDDB, 0xDC5D4FD7, 0x50AFB0A1, 0x58BEB487, 0x522345BE,
			0x02AE6B5E, 0x728B0811, 0xABA01703, 0xBA83D0E2, 0xDFB97272,
			0x22B58BFA, 0x292DD6D3, 0xE61D3848, 0x6B9D4C68, 0xC4104F19,
			0x7E4BED62, 0x752E4991, 0x1B25BCC9, 0xFB554185, 0xF87AA911,
			0x3B105001, 0xB9EB6FC6, 0x635AC195, 0x030F591C, 0x1E197C45,
			0x6CB96F62, 0x015260E8, 0xF72CBED5, 0xD006C190, 0x65F96F03,
			0x9B38A4CB, 0x9961A0F0, 0x956C8A0E, 0x13F08F86, 0x98321369,
			0x815D85C5, 0x7F86D2C6, 0x4BF616E2, 0x71B5C842, 0xED0040D9,
			0x391AC375, 0x03595876, 0x11C1772E, 0x2E77F586, 0x3EC6FCB6,
			0x18A5AEFB, 0xC62DFBF1, 0x5E1BE4D6, 0xA920056F, 0x0C969AE9,
			0x7E3B0A02, 0x3E41BA17, 0xC11922D4, 0xE5C9CD20, 0xD2121CC4,
			0x09B6F8FC, 0x13305C97, 0xFBE87E37, 0x8228D074, 0x5DC6921F,
			0x8D549786, 0x1EBC62E5, 0x813D6F28, 0x2E03F676, 0xA02DF2DE,
			0xD7A72ACF, 0x35D64963, 0x3AAD2B89, 0xE506BE60, 0x7EC00CB8,
			0x5B3DB3C1, 0x530E31B7, 0x3AE01653, 0xE7F8C957, 0xA664F2EC,
			0x37173CAD, 0x7DE346EF, 0x629FA918, 0x714E1687, 0x9A04E4F8,
			0x9747C221, 0x9386DF96, 0xA8A97B7F, 0xA5EA5DA6, 0xFB65D2D6,
			0xF626F40F, 0x10900417, 0x1DD322CE, 0x6F1A719D, 0xB9EC27B0,
			0xB4AF0169, 0xA7FCF8F0, 0xAABFDE29, 0xD9925CB4, 0x2F969FE7,
			0xADA10184, 0xB0457693, 0x4FDD1C66, 0x548DE45B, 0xA6407812,
			0xA996D07E, 0xC6148607, 0x30EB839E, 0xC78FA3B7, 0x05C34219,
			0x0696D76F, 0x55471D61, 0x26F41BAD, 0x33ADEE46, 0x1F7E043E,
			0x027D9E64, 0x133ADA2F, 0x8F47051E, 0x5F1BD61A, 0x33329A7B,
			0xBFEEDABF, 0x232EE11E, 0xAFDFE174, 0x311A7044, 0xF531B147,
			0xA09E5934, 0xF0C5EEED, 0x6F675FB6,

	};

	protected final static long DAMAGE_OVER_TIME_TICK_RATE_MS = 10000l;

	protected final static byte ATTACK_TYPE_SINGLE = 0;
	protected final static byte ATTACK_TYPE_CONE = 1;
	protected final static byte ATTACK_TYPE_AOE = 2;

	protected final static int COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_IN_STANCE = 1;
	protected final static int COMMAND_QUEUE_ERROR_TYPE_INSUFFICIENT_SKILL = 2;
	protected final static int COMMAND_QUEUE_ERROR_TYPE_INVALID_TARGET = 3;
	protected final static int COMMAND_QUEUE_ERROR_TYPE_OUT_OF_RANGE = 4;
	protected final static int COMMAND_QUEUE_ERROR_TYPE_CANNOT_EXECUTE_WITH_STATE = 5;
	protected final static int COMMAND_QUEUE_ERROR_TYPE_GOD_MODE_STRINGS = 6;

	protected final static int MELEE_BASIC_ATTACKS[] = { -1723374040,
			-179012719, 1021473772, 120897548, 1136984016, 1456983160,
			1262603003, 576180331, 1965512955, -7095288, 920166814, 521332591,
			-928039484, -2142755361, 1804794748, -203137564, -1652130913,
			-241867226, 941846107, 66444731, 1191567975, 1377227215,
			1333843788, 647417308, 1910916428, -78470721, 840547369, 466877144,
			-865197965, -2071381912, 1867769547, -148553645, -1866113722,
			-53067521, 895502466, 246872930, 1246151870, 1599469334,
			1120137621, 735439621, 2091213717, -166623386, 1062902512,
			378592257, -1054013782, -1983242575, 1645264914, -93952374,
			-1091238675, -514971930, -1140999619, -1982333919, -1893828569,
			1399868328, -1022433030, -1855441399, -1851222349, 1783794441,
			-1898762792, 2032494374, -1440273260, -528256231, -1153623417,
			-1393133074, -1913434506, 1628515112, 984676322, -1139475421,
			-121151736, 691988283, 1718193962, -1911780630, 633505795,
			-197447632, 1150348562, 2120438985, -190619673, -587253230,
			626714580, 807231308, -1540808100, -1087110632, 558707579,
			739120546, 684656661, 914905104, 843662759, 1057645438,
			-1452669819, -1989063587, -2077070716, -2131527885, 517324391,
			202726778, -1838388199 };

	// These were pulled from the results of Meanmon13's Animation Survey... See
	// meanmon13s_attack_animation_survey.xls for details
	protected final static int RANGED_BASIC_ATTACKS[] = { -1426917621,
			-1972759855, 1265679359, 666154251, 1080072985, 884264322,
			-996202883, 1337043623, 1415023531, 2091999692, -2117788604,
			1479994801, 467348419, -545193778, 1304981139, 2084154063,
			-1615096321, 1547496078, -1804736348, -1517699127, 486287385,
			1349426508, -216616512, 1745795709, -138507693, -1074226979,
			-1728726268, 224164492, 505337519, 55915813 };

	// This is the Melee_Basic_Attacks array but with some aditional animations
	// that ONLY work when unarmed ~meanmon13
	protected final static int UNARMED_BASIC_ATTACKS[] = { -1723374040,
			-179012719, 1021473772, 120897548, 1136984016, 1456983160,
			1262603003, 576180331, 1965512955, -7095288, 920166814, 521332591,
			-928039484, -2142755361, 1804794748, -203137564, -1652130913,
			-241867226, 941846107, 66444731, 1191567975, 1377227215,
			1333843788, 647417308, 1910916428, -78470721, 840547369, 466877144,
			-865197965, -2071381912, 1867769547, -148553645, -1866113722,
			-53067521, 895502466, 246872930, 1246151870, 1599469334,
			1120137621, 735439621, 2091213717, -166623386, 1062902512,
			378592257, -1054013782, -1983242575, 1645264914, -93952374,
			-1091238675, -514971930, -1140999619, -1982333919, -1893828569,
			1399868328, -1022433030, -1855441399, -1851222349, 1783794441,
			-1898762792, 2032494374, -1440273260, -528256231, -1153623417,
			-1393133074, -1913434506, 1628515112, 984676322, -1139475421,
			-121151736, 691988283, 1718193962, -1911780630, 633505795,
			-197447632, 1150348562, 2120438985, -190619673, -587253230,
			626714580, 807231308, -1540808100, -1087110632, 558707579,
			739120546, 684656661, 914905104, 843662759, 1057645438,
			-1452669819, -1989063587, -2077070716, -2131527885, 517324391,
			202726778, -1838388199, -889074757, -475911858, -431405913,
			-403311637, 1992713736, -1978799738, 1928300633, 1989725233,
			-1751428307, -498813508, -1956661097, 1259032149, 952795368,
			-1560545153, 513685134, -1624290624, -1678878857 };

	protected final static String[] COMBAT_EFFECTS_FLYTEXT = {
			"block",
			"choke",
			"counterattack",
			"dodge",
			"go_alert",
			"go_berserk",
			"go_blind",
			"go_cover",
			"go_dizzy",
			"go_intimidated",
			"go_peace",
			"go_rally",
			"go_rooted", // Dunno if this applies to the patch we're aiming for.
			"go_snared", // Dunno if this applies to the patch we're aiming for.
			"go_steady", "go_stunned", "go_tumbling", "hit_body", "hit_head",
			"hit_larm", "hit_lleg", "hit_rarm", "hit_rleg",
			"innate_equilibrium", "innate_regeneration", "innate_vitalize",
			"strafe", "miss", "no_alert", "no_berserk", "no_blind", "no_cover",
			"no_dizzy", "no_intimidated", "no_peace", "no_rally", "no_rooted",
			"no_snared", "no_steady", "no_stunned", "no_tumbling",
			"warcry_hit", "wookiee_roar_hit" };

	protected final static int COMBAT_EFFECT_FLYTEXT_BLOCK = 0;
	protected final static int COMBAT_EFFECT_FLYTEXT_CHOKE = 1;
	protected final static int COMBAT_EFFECT_FLYTEXT_COUNTERATTACK = 2;
	protected final static int COMBAT_EFFECT_FLYTEXT_DODGE = 3;
	protected final static int COMBAT_EFFECT_FLYTEXT_GO_ALERT = 4;
	protected final static int COMBAT_EFFECT_FLYTEXT_GO_BERSERK = 5;
	protected final static int COMBAT_EFFECT_FLYTEXT_GO_BLIND = 6;
	protected final static int COMBAT_EFFECT_FLYTEXT_GO_INTO_COVER = 7;
	protected final static int COMBAT_EFFECT_FLYTEXT_DIZZY = 8;
	protected final static int COMBAT_EFFECT_FLYTEXT_INTIMIDATED = 9;
	protected final static int COMBAT_EFFECT_FLYTEXT_DECLARE_PEACE = 10;
	protected final static int COMBAT_EFFECT_FLYTEXT_RALLY = 11;
	protected final static int COMBAT_EFFECT_FLYTEXT_ROOTED = 12;
	protected final static int COMBAT_EFFECT_FLYTEXT_SNARED = 13;
	protected final static int COMBAT_EFFECT_FLYTEXT_GO_STEADY = 14;
	protected final static int COMBAT_EFFECT_FLYTEXT_GO_STUNNED = 15;
	protected final static int COMBAT_EFFECT_FLYTEXT_GO_TUMBLING = 16;
	protected final static int COMBAT_EFFECT_FLYTEXT_HIT_BODY = 17;
	protected final static int COMBAT_EFFECT_FLYTEXT_HIT_HEAD = 18;
	protected final static int COMBAT_EFFECT_FLYTEXT_HIT_LEFT_ARM = 19;
	protected final static int COMBAT_EFFECT_FLYTEXT_HIT_LEFT_LEG = 20;
	protected final static int COMBAT_EFFECT_FLYTEXT_HIT_RIGHT_ARM = 21;
	protected final static int COMBAT_EFFECT_FLYTEXT_HIT_RIGHT_LEG = 22;
	protected final static int COMBAT_EFFECT_FLYTEXT_EQUILIBRIUM = 23;
	protected final static int COMBAT_EFFECT_FLYTEXT_REGENERATE = 24;
	protected final static int COMBAT_EFFECT_FLYTEXT_VITALIZE = 25;
	protected final static int COMBAT_EFFECT_FLYTEXT_STRAFE = 26;
	protected final static int COMBAT_EFFECT_FLYTEXT_MISS = 27;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_ALERT = 28;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_BERSERK = 29;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_BLIND = 30;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_COVER = 31;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_DIZZY = 32;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_INTIMIDATED = 33;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_PEACE = 34;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_RALLY = 35;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_ROOTED = 36;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_SNARED = 37;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_GO_STEADY = 38;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_GO_STUNNED = 39;
	protected final static int COMBAT_EFFECT_FLYTEXT_NO_GO_TUMBLING = 40;
	protected final static int COMBAT_EFFECT_FLYTEXT_WARCRY = 41;
	protected final static int COMBAT_EFFECT_FLYTEXT_WOOKIEE_ROAR = 42;

	protected final static long CREATURE_ATTACK_TIME_MS = 2000l;
	protected final static int INTELLIGENCE_TYPE_NONE = 0;
	protected final static int INTELLIGENCE_TYPE_NPC_RANGED = 1; // Ranged NPC
	// AI.
	protected final static int INTELLIGENCE_TYPE_NPC_MELEE = 2; // Melee NPC AI.
	protected final static int INTELLIGENCE_TYPE_NPC_FORCE_SENSITIVE = 3; // Melee
	// /
	// Force
	// Sensitive
	// AI.
	protected final static int INTELLIGENCE_TYPE_NPC_JEDI = 4; // Different from
	// Force
	// Sensitive --
	// is less apt
	// to use Force
	// Powers, more
	// apt to use
	// lightsaber
	// attacks.
	protected final static int INTELLIGENCE_TYPE_CREATURE_PASSIVE = 5;
	protected final static int INTELLIGENCE_TYPE_CREATURE_STALKER = 6;
	protected final static int INTELLIGENCE_TYPE_CREATURE_AGGRESSIVE = 7;

	protected final static long DEFAULT_CREATURE_STALK_TIME_MS = 20000l; // Stalk
	// the
	// target
	// for
	// 20
	// seconds.
	protected final static byte CREATURE_HEALTH_TYPE_FAT = 0;
	protected final static byte CREATURE_HEALTH_TYPE_MEDIUM = 1;
	protected final static byte CREATURE_HEALTH_TYPE_SCRAWNY = 2;
	protected final static byte CREATURE_HEALTH_TYPE_SKINNY = 3;

	protected final static String[] CREATURE_HEALTH_TYPE_STF = {
			"creature_quality_fat", "creature_quality_medium",
			"creature_quality_scrawny", "creature_quality_skinny" };

	protected final static byte FACTORY_TYPE_STRUCTURE = 3;
	protected final static byte FACTORY_TYPE_WEAPON = 2;
	protected final static byte FACTORY_TYPE_FOOD = 1;
	protected final static byte FACTORY_TYPE_CLOTHING = 0;

	// SUI Window Constants
	protected final static byte SUI_FROG_TEACH_SKILL = 0;
	protected final static byte SUI_TRAVEL_SELECT_TICKET = 1;
	protected final static byte SUI_FROG_CHARACTER_BUILDER = 2;
	protected final static byte SUI_FROG_SELECT_PROFESSION = 3;
	protected final static byte SUI_FROG_GRANT_EXPERIENCE = 4;
	protected final static byte SUI_ADMIN_ADD_SKILL_TRAINER = 5;
	protected final static byte SUI_ADMIN_ADD_TERMINAL = 6;
	protected final static byte SUI_TRAVEL_SELECT_DESTINATION = 7;
	protected final static byte SUI_SELECT_SURVEY_RESOLUTION = 8;
	protected final static byte SUI_FROG_SELECT_TOOLS_CAT = 9;
	protected final static byte SUI_FROG_SELECT_WEARABLES_CAT = 10;
	protected final static byte SUI_FROG_SELECT_WEARABLE = 11;
	protected final static byte SUI_FROG_SELECT_TOOL = 12;
	protected final static byte SUI_FROG_SELECT_ARMOR = 13;
	protected final static byte SUI_FROG_SELECT_DEED = 14;
	protected final static byte SUI_SELECT_DEED = 15;
	protected final static byte SUI_STRUCTURE_CONFIRM_REDEED = 16;
	protected final static byte SUI_VEHICLE_CONFIRM_REPAIRS = 17;
	protected final static byte SUI_STRUCTURE_REMOVE_ADMIN = 18;
	protected final static byte SUI_STRUCTURE_SHOW_BANNED_LIST = 19;
	protected final static byte SUI_STRUCTURE_SHOW_ADMIN_LIST = 20;
	protected final static byte SUI_STRUCTURE_SHOW_ENTRY_LIST = 21;
	protected final static byte SUI_STRUCTURE_SHOW_STATUS = 22;
	protected final static byte SUI_REMOVE_PLAYER_FROM_ENTRY_LIST = 23;
	protected final static byte SUI_REMOVE_PLAYER_FROM_BAN_LIST = 24;
	protected final static byte SUI_START_DANCE_NO_PARAMS = 25;
	protected final static byte SUI_START_MUSIC_NO_PARAMS = 26;
	protected final static byte SUI_SELECT_LOCATION_TO_CLONE = 27;
	protected final static byte SUI_RENAME_STRUCTURE = 28;
	protected final static byte SUI_CONFIRM_DESTROY_STRUCTURE = 29;
	protected final static byte SUI_STRUCTURE_ADD_PLAYER_TO_ADMIN = 30;
	protected final static byte SUI_STRUCTURE_ADD_PLAYER_TO_ENTRY = 31;
	protected final static byte SUI_STRUCTURE_ADD_PLAYER_TO_BAN = 32;
	protected final static byte SUI_RENAME_ITEM = 33;
	protected final static byte SUI_BANK_WINDOW = 34;
	protected final static byte SUI_STRUCTURE_PAY_MAINTENANCE = 35;
	protected final static byte SUI_STRUCTURE_PAY_POWER = 36;
	protected final static byte SUI_TIP_WINDOW = 37;
	protected final static byte SUI_TRADE_WINDOW = 38;
	protected final static byte SUI_LEARN_OFFERED_SKILL = 39;
	protected final static byte SUI_FACTORY_UPDATE_INSTALLED_SCHEMATIC = 40;
	protected final static byte SUI_STRUCTURE_REMOVE_ENTRY = 41;
	protected final static byte SUI_STRUCTURE_REMOVE_BAN = 42;
	protected final static byte SUI_STRUCTURE_SHOW_SIGN = 43;
	protected final static byte SUI_MEDICAL_DIAGNOSE_PLAYER_WOUNDS = 44;
	protected final static byte SUI_DEVELOPER_COMMAND_LIST = 45;
	protected final static byte SUI_DEVELOPER_LIST_SKILLMODS = 46;
	protected final static byte SUI_TESTER_SHOW_PUNCHLIST = 47;
	protected final static byte SUI_TESTER_COMMAND_LIST = 48;
        protected final static byte SUI_FROG_SELECT_WEAPON = 49;

	protected final static long CREATURE_INTELLIGENCE_HATRED_DECAY_ON_PEACED_PLAYERS_KKMS = PacketUtils.toKK(1) / 4;
	
	protected final static byte LOGIN_INTEGRATION_NOT_INTEGRATED = 0;
	protected final static byte LOGIN_INTEGRATION_VBULLETIN = 1;
	protected final static byte LOGIN_INTEGRATION_DRAGONFLY = 2;
	
	protected final static int ACCOUNT_CREATION_INVALID_CHARACTER = 0;
	protected final static int ACCOUNT_CREATION_VBULLETIN_PASSWORD_MISMATCH = -1;
	protected final static int ACCOUNT_CREATION_NO_VBULLETIN_ACCOUNT_FOUND = -2;
	protected final static int ACCOUNT_CREATION_DATABASE_ERROR_ON_CREATE = -3;
	protected final static int ACCOUNT_CREATION_BANNED = -4;
	protected final static int ACCOUNT_CREATION_ALREADY_ACTIVE = -5;
	protected final static int ACCOUNT_PASSWORD_ENCRYPTION_KEY = (int)0x12345678;
	
	private static float[][] fAcceptableScales = new float[SpeciesNames.length][2];
	static {
		fAcceptableScales[RACE_HUMAN_MALE][0] = 0.89f;
		fAcceptableScales[RACE_HUMAN_MALE][1] = 1.11f;
		fAcceptableScales[RACE_RODIAN_MALE][0] = 0.81f;
		fAcceptableScales[RACE_RODIAN_MALE][1] = 0.94f;
		fAcceptableScales[RACE_MONCAL_MALE][0] = 0.89f;
		fAcceptableScales[RACE_MONCAL_MALE][1] = 1.0f;
		fAcceptableScales[RACE_BOTHAN_MALE][0] = 0.75f;
		fAcceptableScales[RACE_BOTHAN_MALE][1] = 0.83f;
		fAcceptableScales[RACE_WOOKIEE_MALE][0] = 1.11f;
		fAcceptableScales[RACE_WOOKIEE_MALE][1] = 1.28f;
		fAcceptableScales[RACE_TWILEK_MALE][0] = 0.92f;
		fAcceptableScales[RACE_TWILEK_MALE][1] = 1.11f;
		fAcceptableScales[RACE_TRANDOSHAN_MALE][0] = 1.03f;
		fAcceptableScales[RACE_TRANDOSHAN_MALE][1] = 1.25f;
		fAcceptableScales[RACE_ZABRAK_MALE][0] = 0.92f;
		fAcceptableScales[RACE_ZABRAK_MALE][1] = 1.06f;
		fAcceptableScales[RACE_ITHORIAN_MALE][0] = 0.92f;
		fAcceptableScales[RACE_ITHORIAN_MALE][1] = 1.06f;
		fAcceptableScales[RACE_SULLUSTAN_MALE][0] = 0.92f;
		fAcceptableScales[RACE_SULLUSTAN_MALE][1] = 1.06f;
	
		fAcceptableScales[RACE_HUMAN_FEMALE][0] = 0.83f;
		fAcceptableScales[RACE_HUMAN_FEMALE][1] = 1.08f;
		fAcceptableScales[RACE_RODIAN_FEMALE][0] = 0.78f;
		fAcceptableScales[RACE_RODIAN_FEMALE][1] = 0.92f;
		fAcceptableScales[RACE_MONCAL_FEMALE][0] = 0.86f;
		fAcceptableScales[RACE_MONCAL_FEMALE][1] = 0.94f;
		fAcceptableScales[RACE_BOTHAN_FEMALE][0] = 0.72f;
		fAcceptableScales[RACE_BOTHAN_FEMALE][1] = 0.81f;
		fAcceptableScales[RACE_WOOKIEE_FEMALE][0] = 1.08f;
		fAcceptableScales[RACE_WOOKIEE_FEMALE][1] = 1.25f;
		fAcceptableScales[RACE_TWILEK_FEMALE][0] = 0.89f;
		fAcceptableScales[RACE_TWILEK_FEMALE][1] = 1.08f;
		fAcceptableScales[RACE_TRANDOSHAN_FEMALE][0] = 1.0f;
		fAcceptableScales[RACE_TRANDOSHAN_FEMALE][1] = 1.22f;
		fAcceptableScales[RACE_ZABRAK_FEMALE][0] = 0.89f;
		fAcceptableScales[RACE_ZABRAK_FEMALE][1] = 1.03f;
		fAcceptableScales[RACE_ITHORIAN_FEMALE][0] = 0.89f;
		fAcceptableScales[RACE_ITHORIAN_FEMALE][1] = 1.03f;
		fAcceptableScales[RACE_SULLUSTAN_FEMALE][0] = 0.89f;
		fAcceptableScales[RACE_SULLUSTAN_FEMALE][1] = 1.03f;
	
	}
	
	public static boolean getIsValidScale(Player player, float fScale) {
		int iRaceID = player.getRaceID();
		float[] scaleBounds = fAcceptableScales[iRaceID];
		return (fScale >= scaleBounds[0] && fScale <= scaleBounds[1]);
	}
	
	public static float[] getScaleBoundsForSpecies(int iSpeciesID) {
		return fAcceptableScales[iSpeciesID];
	}
}

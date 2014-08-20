import java.awt.geom.Point2D;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import javax.script.ScriptException;

/**
 * The ZoneServer class is the class responsible for handling everything
 * that occurs in the game world.
 *
 * @author Darryl
 *
 */

public class ZoneServer implements Runnable {

	protected static ZoneServerRunOptions ZoneRunOptions;
	private final static long SERVER_STATUS_UPDATE_MS = 60000;
	private long lNextServerStatusUpdate = SERVER_STATUS_UPDATE_MS;
	// There are 16 meters per pixel on the image,
	// based on the world being 16384m on a side, and the planet maps being 1024 pixels on a side.
	private final static int GRID_RESOLUTION = 160;
	// center of the map, as opposed to the top left corner of the map.
	protected final static int MAX_PACKET_SIZE = 496;
	private final static int iMaxClients = 3000;
	private final static int iMaxCharactersPerAccount = 2;
	private final static int STATUS_OFFLINE = 0;
    private static long lServerStartupTime = 0;
	private int[] iCurrentWeatherByPlanet;
	private long[] lCurrentWeatherMS;
	private int iServerStatus = STATUS_OFFLINE;
	private List<DatagramPacket> vPacketsToSend = null;
	private Hashtable<String, Point2D> sStartingLocations;
	private Hashtable<Integer, Vector<MapLocationData>> vStaticMapLocationsByPlanet;
	private Hashtable<Integer, Vector<MapLocationData>> vPlayerMapLocationsByPlanet;
	private String sClusterName;
	private Thread theThread;
	private StructureUpdateThread structureThread;
	private int iServerID;
	private Vector<String> vUsedRandomNames;
	// This is where we store all our players we know about (our active
	// clients).
	private ConcurrentHashMap<SocketAddress, ZoneClient> vAllClients; // To be preloaded.
	private ConcurrentHashMap<Long, Player> vAllPlayers;
	private ConcurrentHashMap<Long, SOEObject> vAllSpawnedObjects;
	private ConcurrentHashMap<Long, SOEObject> vNoBuildZoneBypassObjects;
	private ConcurrentHashMap<Long, Structure> vPlayerStructures;
	private Hashtable<Integer, CombatAction> vCombatActions; // To be
	private Hashtable<Integer, Skills> vSkillsMasterList; // To be preloaded.
	private Hashtable<Integer, NPCUpdateThread> vNPCUpdateThreads;
	private Hashtable<Integer, Grid> coffeetree;
	private DataOutputStream dOut = null;
	private Vector<Terminal> ServerTerminals;
	private SWGGui theGui;
	private int port;
	protected PingServer pingServer;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private String sHostName;
	//private ZoneServerPacketSender vPacketSenders;
	private DatabaseInterface dbInterface;
	private long lTicks;
	private int[][][] iStartingHams;
	private Vector<Long> vUsedObjectID = null;
	private Vector<Long> vUsedSerialNumbers = null;
	private Vector<Integer> vUsedEmailID = null;
	private String sMOTD = null;
	private short[][][] iHeightMap;
	private ResourceManager resourceManager;
	private EmailServer eServer;
	private Hashtable<Integer, RadialTemplateData> vServerRadials;
	private Hashtable<Long, RadialMenuItem> chmServerObjectRadials;
	//private NPCSpawnManager[] npcSpawnManager;
	private Vector<TravelDestination> vAllTravelDestinations;
	public TicketPriceMatrix ServerTicketPriceList;
	private Vector<Shuttle> ServerShuttleList;
	private Vector<DynamicLairSpawn>[] vLairSpawnsSortedByPlanet;
	private ScriptManager scriptManager;
	private long lNextWebUpdate;
	private String sScriptDirectory = "";
	//private Vector<POI> vServerPOIList;
	private static Vector<LairTemplate> vServerLairTemplates;
	/**
	 * planetaryHeightMap [planetid][xIndex][yIndex] = height
	 */
	private short[][][] planetaryHeightMap;
//    private short[][][] planetaryHeightMapTest;
	/**
	 * cellHeightMap[planetid][cellid][xIndex][yIndex] = height
	 */
	private short[][][][] cellHeightMap;

	private Vector<String> vUsedCharacterNames;
	private ZoneServerScreen theScreen;

	private boolean bUsingLoginServer = true;
	private LoginServer lServer;
	private ZoneLoginTransciever loginTransciever;
	private InetAddress loginServerAddress;
	private int loginServerPort;


	/*
	 * Constructs a new ZoneServer object for the specified GUI, listening on
	 * the given port.
	 *
	 * @param gui --
	 *            The GUI
	 * @param port --
	 *            The port to listen for incoming connections on.
	 */
	public ZoneServer(SWGGui gui, int port, String scriptDirectory, int id, InetAddress loginServerAddress, int loginServerPort) {
		ZoneRunOptions = DatabaseInterface.getServerRunOptions();
		try {

			//logServer.setZoneServer(this);
			//logServer.startLog();

			if(DataLog.qServerLog==null)
			{
				DataLog.qServerLog = new Vector<DataLogObject>();
			}
			if(DataLog.qPacketLog==null)
			{
				DataLog.qPacketLog = new Vector<DataLogObject>();
			}

			//DataLogObject L = new DataLogObject("ZoneServer()","Server Starting", Constants.LOG_SEVERITY_INFO);
			//DataLog.qServerLog.add(L);

		} catch (Exception e) {
			System.out
			.println("System Exception While Starting the Log Server: "
					+ e.toString());
			e.printStackTrace();
		}
		theGui = gui;
		this.port = port;
		this.sScriptDirectory = scriptDirectory;
		iServerID = id;
		this.loginServerAddress = loginServerAddress;
		this.loginServerPort = loginServerPort;
        lServerStartupTime = System.currentTimeMillis();

	}
	
	public void start() {
		System.out.println("ZoneServer start.");
		theThread = new Thread(this);
		theThread.setName("ZoneServer thread");
		theThread.start();
	}
	
	private void initialize() {
		DataLog.logEntry("Zone server initializing...","ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		//System.out.println("Zone server initializing...");
		setStatus(Constants.SERVER_STATUS_LOADING);
		iServerStatus = Constants.SERVER_STATUS_LOADING;
		coffeetree = new Hashtable<Integer, Grid>();
		DataLog.logEntry("Set up planet positional grid.","ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		//System.out.println("Set up planet positional grid.");
		vLairSpawnsSortedByPlanet = new Vector[Constants.PlanetNames.length];
		for (int i = 0; i < Constants.PlanetNames.length; i++) {
			coffeetree.put(i, new Grid(this, GRID_RESOLUTION, i));
			vLairSpawnsSortedByPlanet[i] = new Vector<DynamicLairSpawn>();
		}
		//add tutorial grid
		coffeetree.put(Constants.TUTORIAL, new Grid(this, GRID_RESOLUTION, Constants.TUTORIAL));
		//vLairSpawnsSortedByPlanet[42] = new Vector<DynamicLairSpawn>();
		//-----------------------------------------------------------------
		DataLog.logEntry("Planet positional grid setup complete.","ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		//System.out.println("Planet positional grid setup complete.");

		try {
			socket = new DatagramSocket(port);
			socket.setSoTimeout(10);
			packet = new DatagramPacket(new byte[496], 496);
		} catch (IOException e) {
			DataLog.logEntry("SHUTDOWN: Error Binding Zone Server Socket: " + e ,"ZoneServer()", Constants.LOG_SEVERITY_MAJOR, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Error binding Zone Server socket: "	+ e.toString());
			e.printStackTrace();
			while(DataLog.logWritePending())
			{
				try{
					wait(10000);
				}catch(Exception logwait){
					//doh!
				}
			}
			System.exit(-1);
		}
		dbInterface = theGui.getDB();
		vSkillsMasterList = DatabaseInterface.getSkillList();
		vUsedObjectID = new Vector<Long>();
		vUsedSerialNumbers = new Vector<Long>();


		lCurrentWeatherMS = new long[Constants.PlanetNames.length];
		iCurrentWeatherByPlanet = new int[Constants.PlanetNames.length];
		for (int i = 0; i < iCurrentWeatherByPlanet.length; i++) {
			iCurrentWeatherByPlanet[i] = Constants.WEATHER_TYPE_CLEAR1;
			lCurrentWeatherMS[i] = SWGGui
			.getRandomLong(Constants.WeatherTimesMS[Constants.WEATHER_TYPE_CLEAR1]);
		}

		DatabaseServerInfoContainer container = dbInterface.getZoneServerData(iServerID);
		if (container != null) {
			//DataLog.logEntry("Found server data for server ID " + iServerID,"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Found server data for server ID " + iServerID);
			sClusterName = container.sServerName;
			sHostName = container.sRemoteAddress;
			sMOTD = container.sMotd;
		} else {					
			sClusterName = "default";
			iServerID = 2;
            try{
                sHostName = socket.getInetAddress().getHostName();
            }catch(Exception e){
                DataLog.logException("Exception while trying to set the host name variable, Name Set to localhost!!!!", "ZoneServer.java", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
                //sHostName = "localhost";
            }

            sMOTD = "Default MOTD";
		}

		DataLog.setSHostName(this.getHostName());
		DataLog.setZPort(this.getPort());

		vUsedEmailID = dbInterface.getAllUsedEmailID(iServerID);
		iStartingHams = dbInterface.loadDefaultHams();
		vAllClients = new ConcurrentHashMap<SocketAddress, ZoneClient>();
		// vAllPlayers = new ConcurrentHashMap<Long, Player>();
		// Pre-parse the players list so that only players belonging to "this"
		// server are added. Ridiculous.
		vAllSpawnedObjects = new ConcurrentHashMap<Long, SOEObject>();
		vNoBuildZoneBypassObjects = new ConcurrentHashMap<Long, SOEObject>();

		vUsedCharacterNames = new Vector<String>();
		lServer = theGui.getLoginServer();
		bUsingLoginServer = (lServer != null);
		if (bUsingLoginServer) {
			vAllPlayers = lServer.getCharacterListForServer(this);

		} else {
			vAllPlayers = dbInterface.loadPlayers(this);
			try {
				DataLog.logEntry("Creating ZoneLoginTransciever...","ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
				//System.out.println("Creating ZoneLoginTransciever...");
				loginTransciever = new ZoneLoginTransciever(this, loginServerAddress, loginServerPort);
				DataLog.logEntry("Transciever created. Authenticating...","ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
				//System.out.println("Transciever created. Authenticating...");
				loginTransciever.authenticate();
				DataLog.logEntry("Authenticated with Login Server.","ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
				//System.out.println("Authenticated with Login Server.");
			} catch (IOException e) {
				DataLog.logEntry("SHUTDOWN: Unable to communicate with LoginServer at address " + loginServerAddress.getHostAddress() + ":" + loginServerPort + ".  Error message: " + e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_MAJOR, ZoneRunOptions.bLogToConsole, true);
				//System.out.println("Unable to communicate with LoginServer at address " + loginServerAddress.getHostAddress() + ":" + loginServerPort + ".  Error message: " + e.toString());
				e.printStackTrace();
				while(DataLog.logWritePending())
				{
					try{
						wait(10000);
					}catch(Exception logexcp){
						//doh!
					}
				}
				System.exit(-1);
			}
		}
		Enumeration<Player> vPlayers = vAllPlayers.elements();
		while (vPlayers.hasMoreElements()) {
			Player player = vPlayers.nextElement();
			//System.out.println("Zone server ID " + iServerID + " adding player name " + player.getFirstName() + " to list of all used names.");
			vUsedCharacterNames.add(player.getFirstName());
		}

		//vAllPlayers = dbInterface.loadPlayers(this);
		vCombatActions = new Hashtable<Integer, CombatAction>();
		//vSkillsMasterList = dbInterface.getSkillList();

		pingServer = new PingServer(port - 1);
		vPacketsToSend = Collections.synchronizedList(new LinkedList<DatagramPacket>());
		new ZoneServerPacketSender(this);
		dbInterface.initializeStaticObjects(this);
		Enumeration<Grid> vGridEnum = coffeetree.elements();
		while (vGridEnum.hasMoreElements()) {
			Grid theGrid = vGridEnum.nextElement();
			theGrid.generateNoBuildZones();
		}
		
		//if (true) {
		//	System.exit(0);
		//}
		//loadHeightMapFromFile();

		// load the server radial menus
		vServerRadials = dbInterface.getServerRadials();
		chmServerObjectRadials = dbInterface.getObjectRadials();
		vStaticMapLocationsByPlanet = dbInterface.loadStaticMapLocations(this);
		vPlayerMapLocationsByPlanet = dbInterface.loadPlayerMapLocations(this);
		vNPCUpdateThreads = new Hashtable<Integer, NPCUpdateThread>();
		//npcSpawnManager = new NPCSpawnManager[Constants.PlanetNames.length];
		for (int i = 0; i < Constants.PlanetNames.length - 1; i++) {
			NPCUpdateThread updateThread = new NPCUpdateThread(this, i);
			//npcSpawnManager[i] = new NPCSpawnManager(this, i, updateThread);
			//updateThread.setSpawnManager(npcSpawnManager[i]);
			updateThread.startThread();
			vNPCUpdateThreads.put(i, updateThread);
		}
		//Tutorial upd thread
		NPCUpdateThread updateThread = new NPCUpdateThread(this, Constants.TUTORIAL);
		vNPCUpdateThreads.put(Constants.TUTORIAL, updateThread);

		//-----------LOAD PLAYER STRUCTURES !!!!!
		vPlayerStructures = new ConcurrentHashMap<Long, Structure>();        
		Enumeration <Player> pEnum = vAllPlayers.elements();
		while(pEnum.hasMoreElements())
		{
			Player p = pEnum.nextElement();
			ConcurrentHashMap<Long,Structure> vPS = p.getAllPlayerStructures();
			if(!vPS.isEmpty()) 
			{
				vPlayerStructures.putAll(vPS);
			}                    
		}        
		vPlayers = vAllPlayers.elements();
		while (vPlayers.hasMoreElements()) {
			Player player = vPlayers.nextElement();
			if(player.getTutorial()!=null && !player.getTutorial().hasCompleted())
			{
				DataLog.logEntry(player.getFullName() + " Player with valid tutorial object detected. Loading it.","ZoneServer()",Constants.LOG_SEVERITY_INFO,true,true);
				TutorialObject tutorial = player.getTutorial();
				Enumeration<Cell> cEnum = tutorial.getCellsInBuilding().elements();
				this.addObjectToAllObjects(tutorial, false,false);
				while(cEnum.hasMoreElements())
				{
					Cell c = cEnum.nextElement();
					this.addObjectToAllObjects(c, false,false);
				}
				this.addObjectToAllObjects(tutorial.getTutorialTravelTerminal(), true,false);
			} else if (player.getTutorial() == null) {
				long cellID = player.getCellID();
				if (cellID != 0) {
					Cell c = (Cell)getObjectFromAllObjects(cellID);
					if (c != null) {
						if (!c.contains(player)) {
							c.addCellObject(player);
						}
					}
				}
			}
		}

		DataLog.logEntry("Player Structures Loaded: " + vPlayerStructures.size(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		//System.out.println("Player Structures Loaded: " + vPlayerStructures.size());
		Enumeration <Structure> sEnum = vPlayerStructures.elements();
		while(sEnum.hasMoreElements())
		{
			Structure s = sEnum.nextElement();
			s.setServer(this);
			addObjectToAllObjects(s,true, false);
			Hashtable<Long,Cell> vCL = s.getCellsInBuilding();
			Enumeration<Cell> cEnum = vCL.elements();
			//System.out.println("Loading " + vCL.size() + " Cells");
			while(cEnum.hasMoreElements())
			{
				Cell c = cEnum.nextElement();
				
				c.setServer(this);
				c.setBuilding(s);
				addObjectToAllObjects(c,false, false);                
			}
			Terminal guildTerminal = s.getGuildTerminal();
			TangibleItem structureBase = s.getStructureBase();
			Terminal structureSign = s.getStructureSign();
			Terminal adminTerminal = s.getAdminTerminal();
			Vector<Terminal> vElevatorTerminal = s.getVElevatorTerminals();
			addObjectToAllObjects(guildTerminal, true, false);
			addObjectToAllObjects(structureSign, true, false);
			addObjectToAllObjects(structureBase, true, false);
			addObjectToAllObjects(adminTerminal, true, false);
			if (!vElevatorTerminal.isEmpty()) {
				for (int i = 0; i < vElevatorTerminal.size(); i++) {
					addObjectToAllObjects(vElevatorTerminal.elementAt(i), true, false);
				}
			}
			if (s instanceof Factory) {
				Factory factory = (Factory)s;
				TangibleItem inputHopper = factory.getInputHopper();
				TangibleItem outputHopper = factory.getOutputHopper();
				if (inputHopper==null || outputHopper == null) {
					factory.initializeHoppers();
					inputHopper = factory.getInputHopper();
					outputHopper = factory.getOutputHopper();
				}
				addObjectToAllObjects(inputHopper, false, false);
				addObjectToAllObjects(factory.getOutputHopper(), false, false);
				Vector<TangibleItem> vContainedItems = inputHopper.getLinkedObjects();
				if (!vContainedItems.isEmpty()) {
					for (int i = 0; i < vContainedItems.size(); i++) {
						
						addObjectToAllObjects(vContainedItems.elementAt(i), false, false);
					}
				}
				vContainedItems = outputHopper.getLinkedObjects();
				if (!vContainedItems.isEmpty()) {
					for (int i = 0; i < vContainedItems.size(); i++) {
						addObjectToAllObjects(vContainedItems.elementAt(i), false, false);
					}
				}
			}
			/*if (s instanceof Harvester) {
				Harvester h = (Harvester)s;
				h.deactivateInstallation();
			}*/
		}    
		// start the email server
		eServer = new EmailServer(this);
		
		//has to be loaded before the terminals are loaded because if we have vendors in player structures or terminals
		// we need to be able to set their building info.
		//start the structure update thread.
		structureThread = new StructureUpdateThread(this);

		//-----------------------------------------                

		//-----LOAD AND SPAWN TERMINALS
		ServerTerminals = dbInterface.loadServerTerminals(this);
		spawnTerminal(ServerTerminals,false);
		//-----LOAD AND SPAWN TERMINALS

		vAllTravelDestinations = dbInterface.getAllTravelDestinations();


		if(vAllTravelDestinations.isEmpty())
		{
			DataLog.logEntry("No Travel Destinations Retrieved.","ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("No Travel Destinations Retrieved.");
		}
		else
		{
			DataLog.logEntry("Travel Destinations Retrieved:" + vAllTravelDestinations.size(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Travel Destinations Retrieved:" + vAllTravelDestinations.size());
			/*   for(int i = 0 ; i < vAllTravelDestinations.size(); i++)
                    {
                        System.out.println(" - " + vAllTravelDestinations.get(i).getTicketID() + " " + vAllTravelDestinations.get(i).getDestinationPlanet() + " " + vAllTravelDestinations.get(i).getDestinationName() + " " + vAllTravelDestinations.get(i).getXYZ());
                    }*/
		}


		ServerTicketPriceList = dbInterface.getAllTicketPrices();

		ServerShuttleList = dbInterface.loadShuttles();
		DataLog.logEntry("Shuttles Loaded: " + ServerShuttleList.size(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		//System.out.println("Shuttles Loaded: " + ServerShuttleList.size());

		if (!ServerShuttleList.isEmpty()) {
			for (int i = 0; i < ServerShuttleList.size(); i++) {
				Shuttle s = ServerShuttleList.elementAt(i);
				s.setID(getNextObjectID());
				addObjectToAllObjects(s, true,false);
			}
		}

		/**
		 * Init the next Web Update Time
		 */
		lNextWebUpdate = ((1000 * 60) * 3 );        

		vServerLairTemplates = dbInterface.getAllLairTemplates();
		DataLog.logEntry("Lair Templates Retrieved: " + vServerLairTemplates.size(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		//System.out.println("Lair Templates Retrieved: " + vServerLairTemplates.size());

		/**
		 * Initialize Height Map Container
		 */
		planetaryHeightMap = dbInterface.retrieveHeightMap(-1);
		//-----------------------
            /*
                long lMemBeforeHeightMap = SWGGui.getUsedMemory();
                System.out.println("Testing Meter Heightmap");
                short [][][] thm = new short [1][15361][15361];
                long lTotalCoordinatePoints = 0;
                for(int p = 0; p < 1; p++)
                {
                    System.out.println("Planet " + p);
                    for(int x = 0; x < 15361; x++)
                    {
                        for(int y = 0; y < 15361; y++)
                        {
                            thm[p][x][y] = 32767;
                            lTotalCoordinatePoints++;
                        }
                    }                    
                }
                System.out.println("Heightmap Test Complete, Total Points: " + lTotalCoordinatePoints);
                long lMemAfterHeightMap = SWGGui.getUsedMemory();
                long lMemoryUsedByHM = lMemAfterHeightMap - lMemBeforeHeightMap;
                System.out.println("Memory Used Before Height Map: " + lMemBeforeHeightMap );
                System.out.println("Memory Used After Height Map: " + lMemAfterHeightMap );
                System.out.println("Memory Used By Height Map: " + lMemoryUsedByHM );

                This is the result of a meter resolution Height map.
                It is too big for memory it can only be done in a database Table.
                -----------------------------------------------------------------
                Testing Meter Heightmap
                Planet 0
                Heightmap Test Complete, Total Points: 235,960,321
                Memory Used Before Height Map: 255628480
                Memory Used After Height Map: 708901392
                Memory Used By Height Map: 453,272,912

                */

		for (int i = 0; i < Constants.PlanetNames.length; i++) {
			Grid planetGrid = coffeetree.get(i);
			if (planetGrid != null) {
				dbInterface.loadDynamicLairSpawn(planetGrid, i, this);
			}
		}

		scriptManager = new ScriptManager(this, sScriptDirectory);
		scriptManager.parseItemScripts(dbInterface.getAllItemTemplateData());

		//Test Resource Name Generator 
		/*
                System.out.println("Resource Name Generator Test BEGIN");
                for(int i = 0; i < 50;i++)
                {
                    System.out.println("Resource Name Generated: " + PacketUtils.generateResourceName());
                }
                System.out.println("Resource Name Generator Test END");  */
		//---------------------------------



		resourceManager = new ResourceManager(this);
		resourceManager.startThread();

		setStatus(Constants.SERVER_STATUS_ONLINE);
		DataLog.logEntry("Zone server on-line.","ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		DataLog.logEntry("Listening on address " + socket.getLocalSocketAddress(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
        DatabaseInterface.updateGalaxyStatus(iServerID, iServerStatus,getPlayersOnline());
        writeGalaxyStatusFile("ZoneServer.java", false);
        
		//System.out.println("Zone server on-line.");
		//System.out.flush();
	}

	/**
	 * Called at server startup, this function will go through the height map
	 * and interpolate or "smooth out" rough edges, and fill in missing data as
	 * best as it can.
	 */
	private void interpolateHeightMap() {

		for (int i = 0; i < iHeightMap.length; i++) {
			for (int j = 1; j < iHeightMap[i].length - 1; j += 3) {
				for (int k = 1; k < iHeightMap[i][j].length - 1; k += 3) {
					short[] nearbyHeights = new short[9];
					nearbyHeights[0] = iHeightMap[i][j][k];
					nearbyHeights[1] = iHeightMap[i][j + 1][k - 1];
					nearbyHeights[2] = iHeightMap[i][j + 1][k];
					nearbyHeights[3] = iHeightMap[i][j + 1][k + 1];
					nearbyHeights[4] = iHeightMap[i][j][k + 1];
					nearbyHeights[5] = iHeightMap[i][j][k - 1];
					nearbyHeights[6] = iHeightMap[i][j - 1][k - 1];
					nearbyHeights[7] = iHeightMap[i][j - 1][k];
					nearbyHeights[8] = iHeightMap[i][j - 1][k + 1];
					short averageHeight = 0;
					int firstNonZero = -1;
					for (int l = 0; l < nearbyHeights.length; l++) {
						if (firstNonZero == -1) {
							if (nearbyHeights[l] != 0) {
								firstNonZero = l;
							}
						}
						if (nearbyHeights[l] == 0) {
							if (firstNonZero >= 0) {
								nearbyHeights[l] = nearbyHeights[firstNonZero];
							}
						}
						averageHeight += nearbyHeights[l];

					}
					if (firstNonZero != -1) {
						for (int l = nearbyHeights.length - 1; l >= 0; l--) {
							if (nearbyHeights[l] == 0) {
								nearbyHeights[l] = nearbyHeights[firstNonZero];
								averageHeight += nearbyHeights[firstNonZero];
							}
						}
					}

					if (averageHeight != 0) {
						// System.out.println("Average height before division: "
						// + averageHeight);
						if (averageHeight % 9 > 4) {
							averageHeight += 9;
						}
						averageHeight = (short) Math
						.max((averageHeight / 9), 1);
						// System.out.println("Average height near Planet " +
						// Constants.PlanetNames[i] + ", X " + realX + ", Y " +
						// realY + " is " + averageHeight);
						for (int l = j - 1; l <= j + 1; l++) {
							for (int m = k - 1; m <= k + 1; m++) {
								iHeightMap[i][l][m] = averageHeight;
							}
						}
					}
				}
			}
		}
	}


	/**
	 * Adds a z value at the given iPlanetID, x, y coordinates of the heightmap.
	 *
	 * @param iPlanetID
	 * @param x
	 * @param y
	 * @param z
	 */

	/*protected void setHeightMapAtLocation(int iPlanetID, float x, float y,
			float z) {
		if (true)
			return;
		int iX = ((int) x + 8192) / 4;
		int iY = ((int) y + 8192) / 4;
		iX = Math.min(iX, 4095);
		iY = Math.min(iY, 4095);
		short iPreviousValue = iHeightMap[iPlanetID][iX][iY];
		if (Math.abs(iPreviousValue - z) > 1) {
			// System.out.println("Updating heightmap.");
			iHeightMap[iPlanetID][iX][iY] = (short) z;
		}
	}*/

	/**
	 * Called during server start up, this function loads the previously stored
	 * heightmap information from the filesystem.
	 */
	/*private void loadHeightMapFromFile() {
		if (true)
			return;
		ObjectInputStream oIn = null;
		try {
			oIn = new ObjectInputStream(new InflaterInputStream(
					new FileInputStream("heightmap")));
			iHeightMap = (short[][][]) oIn.readObject();
			oIn.close();
			oIn = null;
			// System.out.println("Read heightmap.");
			/*
			 * for (int iPlanetID = 0; iPlanetID < 10; iPlanetID++) { for (int x =
			 * 0; x < 4096; x++) { for (int y = 0; y < 4096; y++) { int realX =
			 * (x * 4) - 8192; int realY = (y * 4) - 8192; if
			 * (iHeightMap[iPlanetID][x][y] != 0) {
			 * System.out.println(Constants.PlanetNames[iPlanetID] + ", X: " +
			 * realX + ", Y: " + realY + ", Z: " + iHeightMap[iPlanetID][x][y]); } } }
			 * System.out.println("HeightMap data for " +
			 * Constants.PlanetNames[iPlanetID] + " printed."); }
			 
		} catch (Exception e) {
			DataLog.logEntry("Error reading in heightmap data: "	+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Error reading in heightmap data: "	+ e.toString());
			e.printStackTrace();
			iHeightMap = new short[Constants.PlanetNames.length][4096][4096]; // Should
			// be
			// 320
			// megabytes.
		}
		interpolateHeightMap();
	}*/

	/**
	 * Called during a proper server shutdown, this function writes out the
	 * heightmap information currently in RAM memory to the filesystem.
	 */
	/*protected void writeHeightMapToFile() {
		if (true)
			return;
		ObjectOutputStream oOut = null;
		try {
			oOut = new ObjectOutputStream(new DeflaterOutputStream(
					new FileOutputStream("heightmap")));
			oOut.writeObject(iHeightMap);
			oOut.flush();
			oOut.close();
		} catch (Exception e) {
			DataLog.logEntry("Error opening heightmap file: "	+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Error opening heightmap file: " + e.toString());
			e.printStackTrace();
		}
	}*/

	/**
	 * Gets the list of all clients, sorted by Internet Address.
	 *
	 * @return
	 */
	protected ConcurrentHashMap<SocketAddress, ZoneClient> getAllClients() {
		if(vAllClients==null)
		{
			vAllClients = new ConcurrentHashMap<SocketAddress, ZoneClient>();
		}
		return vAllClients;
	}

	/**
	 * Adds the given object ID to the list of all used object ID's. An object
	 * ID cannot be used more than once, to avoid client explosion issues.
	 *
	 * @param objectID --
	 *            The object ID.
	 */
	private void addObjectIDToAllUsedID(long objectID) {
		vUsedObjectID.add(objectID);
	}

	/**
	 * Get if the given object ID is already created or not.
	 *
	 * @param objectID --
	 *            The id to check.
	 * @return True if it is in use, false if it is not.
	 */
	protected boolean bIsObjectIDUsed(long objectID) {
		return vUsedObjectID.contains(objectID);
	}

	/**
	 * Adds the given object ID to the list of all used object ID's. An object
	 * ID cannot be used more than once, to avoid client explosion issues.
	 *
	 * @param objectID --
	 *            The object ID.
	 */
	protected void addSerialNumberToAllUsedSerialNumbers(long objectID) {
		vUsedSerialNumbers.add(objectID);
	}

	/**
	 * Get if the given object ID is already created or not.
	 *
	 * @param objectID --
	 *            The id to check.
	 * @return True if it is in use, false if it is not.
	 */
	protected boolean bIsSerialNumberUsed(long objectID) {
		return vUsedSerialNumbers.contains(objectID);
	}
	/**
	 * Adds the given object ID to the list of all used object ID's. An object
	 * ID cannot be used more than once, to avoid client explosion issues.
	 *
	 * @param objectID --
	 *            The object ID.
	 */
	protected void addEmailIDToAllUsedID(int objectID) {
		vUsedEmailID.add(objectID);
	}

	/**
	 * Get if the given object ID is already created or not.
	 *
	 * @param objectID --
	 *            The id to check.
	 * @return True if it is in use, false if it is not.
	 */
	protected boolean bIsEmailIDUsed(int objectID) {
		return vUsedEmailID.contains(objectID);
	}

	protected int getNextEmailID() {
		int id = 1;
		while (bIsEmailIDUsed(id)) {
			id = SWGGui.getRandomInt(Integer.MAX_VALUE);
		}
		vUsedEmailID.add(id);
		return id;
	}

	/**
	 * Gets the starting ham for the given race / profession combination.
	 *
	 * @param iRaceID --
	 *            The starting race ID.
	 * @param sProfession --
	 *            The starting profession.
	 * @return
	 */
	protected int[] getStartingHam(int iRaceID, String sProfession) {
		int iProfession = Constants.getStartingProfessionID(sProfession);
		return iStartingHams[iRaceID][iProfession];
	}

	protected Skills getSkillFromIndex(int i) {
		return vSkillsMasterList.get(i);
	}

	protected Hashtable<Integer, Skills> getAllSkillsAvailable() {
		return vSkillsMasterList;
	}

	protected Vector<Skills> getAllSkillsWithSimilarNameVector(String SkillFamily){
		Vector<Skills> SL = new Vector<Skills>();
		// This is NOT the proper way to iterate through a Hashtable.
		for(int i = 0; i < vSkillsMasterList.size(); i++) {
			Skills S = vSkillsMasterList.get(i);
			if(S != null) {
				if(S.getName().contains(SkillFamily)){
					SL.add(S);
				}
			}
		}

		return SL;
	}
	
	protected Skills getSkillByName(String sName) {
		Enumeration<Skills> vSkillsEnum = vSkillsMasterList.elements();
		while (vSkillsEnum.hasMoreElements()) {
			
			Skills skill = vSkillsEnum.nextElement();
			if (skill.getName().equalsIgnoreCase(sName)) {
				return skill;
			}
		}
		return null;
	}

	protected Vector<Player> getAllPlayersOnPlanet(int p) {
		Enumeration<Player> playersOnPlanet = vAllPlayers.elements();
		Vector<Player> allPlayersOnPlanet = new Vector<Player>();

		while(playersOnPlanet.hasMoreElements()) {
			Player currentPlayer = playersOnPlanet.nextElement();

			if(currentPlayer.getPlanetID() == p) {
				allPlayersOnPlanet.add(currentPlayer);
			}
		}

		return allPlayersOnPlanet;
	}

	/**
	 * Get all players with the specified online status on the specified planet id.
	 *
	 * @param p -- The ID of the planet to get all players from.
	 * @param status -- The status, Online is true, offline is false.
	 * @return A list of all players with the specified online status on the specified planet.
	 */
	protected Vector<Player> getStatusPlayersOnPlanet(int p, boolean bIsOnline) {
		if(vAllPlayers == null)
		{
			return new Vector<Player>();
		}
		Enumeration<Player> playersOnPlanet = vAllPlayers.elements();
		Vector<Player> allPlayersOnPlanet = new Vector<Player>();

		while(playersOnPlanet.hasMoreElements()) {
			Player currentPlayer = playersOnPlanet.nextElement();

			if(currentPlayer.getPlanetID() == p) {
				if(currentPlayer.getOnlineStatus() == bIsOnline) {
					allPlayersOnPlanet.add(currentPlayer);
				}
			}
		}
		
		return allPlayersOnPlanet;
	}
	
	protected Vector<Player> getAllOnlinePlayers() {
		//Store the online players.
		Vector<Player> vOnlinePlayers = new Vector<Player>();
		Enumeration<Player> onlinePlayers = vAllPlayers.elements();
		
		while(onlinePlayers.hasMoreElements()) {
			Player currentPlayer = onlinePlayers.nextElement();

			if(currentPlayer.getOnlineStatus() == true) {
				vOnlinePlayers.add(currentPlayer);
			}
		}		
		
		//Return online players.
		return vOnlinePlayers;		
	}

	protected String[] getAllSkillsWithSimilarNameStringArray(String SkillFamily){

		Vector<Skills> SL = new Vector<Skills>();
		for(int i = 0; i < vSkillsMasterList.size(); i++)
		{
			Skills S = vSkillsMasterList.get(i);
			if(S!=null)
			{
				if(S.getName().contains(SkillFamily)){
					SL.add(S);
				}
			}
		}
		String[] retval = new String[SL.size()];
		for(int i = 0; i < SL.size(); i++)
		{
			retval[i] = SL.get(i).getName();
		}
		return retval;
	}

	protected String[] getAllSkillNames(){
		//System.out.println("Preparing all Skill Names");
		int SkillCount = vSkillsMasterList.size();

		String[] retval = new String[SkillCount];
		//System.out.println("Skill Count " + SkillCount);

		for(int i = 0; i < SkillCount; i++)
		{
			Skills S = vSkillsMasterList.get(i);
			if(S != null)
			{
				//System.out.println(S.getName());
				retval[i] = S.getName();
			}
			//System.out.println(i + " " + retval[i]);
		}
		//System.out.println("Returned " + SkillCount + " Skill Names");
		return retval;
	}
	/**
	 * Gets the name of the skill at the given index.
	 *
	 * @param i --
	 *            The index.
	 * @return
	 */
	protected String getSkillNameFromIndex(int i) {
		return vSkillsMasterList.get(i).getName();
	}

	/**
	 * Gets the index of the given skill name in the skills list.
	 *
	 * @return The skill index, or -1 if the skill name is not found.
	 */
	protected int getSkillIndexFromName(String sProfessionName) {
		// TODO: Find a map that allows for the return of the key, as well as
		// the value.
		Enumeration<Skills> e = vSkillsMasterList.elements();
		for (int i = 0; e.hasMoreElements(); i++) {
			Skills skill = e.nextElement();
			String s = skill.getName();
			if (s.equals(sProfessionName)) {
				return skill.getSkillID();
			}
		}
		// System.out.println("The skill name " + sProfessionName + " is NOT in
		// the list of loaded skills!");
		return -1;
	}

	protected Skills getSkillFromName(String sProfessionName) {
		Enumeration<Skills> e = vSkillsMasterList.elements();
		for (int i = 0; e.hasMoreElements(); i++) {
			Skills skill = e.nextElement();
			String s = skill.getName();
			if (s.equals(sProfessionName)) {
				return skill;
			}
		}
		// System.out.println("The skill name " + sProfessionName + " is NOT in
		// the list of loaded skills!");
		return null;

	}
	/**
	 * Gets the list of skill mods for the skill at the given index.
	 *
	 * @param i --
	 *            The skill index.
	 * @return The list of skill mods for the skill.
	 */
	protected Vector<SkillMods> getSkillModsFromSkillIndex(int i) {
		return vSkillsMasterList.get(i).getAllSkillMods();
	}

	/**
	 * Gets the list of skill mods for the given skill name.
	 *
	 * @param sProfessionName --
	 *            The skill name.
	 * @return The list of skill mods.
	 */
	protected Vector<SkillMods> getSkillModsFromSkillName(String sProfessionName) {
		return vSkillsMasterList.get(getSkillIndexFromName(sProfessionName))
		.getAllSkillMods();
	}

	/**
	 * Gets the number of times the ZoneServer run function has looped.
	 *
	 * @return The loop count.
	 */
	public long getTicks() {
		return lTicks;
	}

	private long lLastSystemTimeMS = System.currentTimeMillis();

	private long lCurrentSystemTimeMS = lLastSystemTimeMS;

	private long lDeltaSystemTimeMS = 0;
	private boolean bInitialized = false;

	/**
	 * The main loop for the Zone Server. Packet reception occurs here, as well
	 * as updates to the server weather.
	 */
	public void run() {
		if(!bInitialized) {
			initialize();
		}
		while (theThread != null) {
			try {
				synchronized (this) {
					try {
						Thread.yield();
						//wait(100);
					} catch (Exception e) {
						System.out.println("Error waiting: " + e.toString());
						e.printStackTrace();
					}
				}
				//System.out.println("Zone Server loop time: " + lDeltaNanoTime + " nanoseconds.");
				lTicks++;
				lLastSystemTimeMS = lCurrentSystemTimeMS;
				lCurrentSystemTimeMS = System.currentTimeMillis();
				lDeltaSystemTimeMS = lCurrentSystemTimeMS - lLastSystemTimeMS;
				if (loginTransciever != null) {
					lNextServerStatusUpdate -= lDeltaSystemTimeMS;
					if (lNextServerStatusUpdate <= 0) {
						lNextServerStatusUpdate = SERVER_STATUS_UPDATE_MS;
						loginTransciever.volunteerStatusChange();
					}
				}
				updateWeather(lDeltaSystemTimeMS);
				if (lTicks % 500 == 0) {
					int iClientElement = 0;
					Enumeration<ZoneClient> vClientsItr = vAllClients.elements();
					while (vClientsItr.hasMoreElements()) {
						ZoneClient itrClient = vClientsItr.nextElement();
						ZoneClientThread itrClientThread = itrClient.getUpdateThread();
						if (itrClientThread== null || itrClientThread.bIsThreadActive() == false) {
							vAllClients.remove(itrClient.getClientAddress());
						}
						//if (itrClient.hasPacketsToParse()) {
						//	System.out.println("Reset packet timeout on client " + iClientElement + ", account ID " + itrClient.getAccountID() + ", has " + itrClient.getNumPacketsToParse() + " packets waiting.");
						//} else {
						//	System.out.println("Client index " + iClientElement + ", account ID " + itrClient.getAccountID() + " has no packet to parse -- timeout in " + itrClient.getUpdateThread().getPacketTimeoutMS() + " ms.");
						//}
						iClientElement++;
						
					}
				}
				socket.receive(packet);

				byte[] incData = Arrays.copyOfRange(packet.getData(), 0, packet
						.getLength()); // If this is not done, the packet will
				// have trailing zeros to the length of
				// the original buffer.
				ZoneClient client = null;
				SocketAddress address = packet.getSocketAddress();
				if (incData[1] == 1) {
					// Is there a pre-existing client?
					client = vAllClients.get(address);
					SOEInputStream dIn = new SOEInputStream(
							new ByteArrayInputStream(incData));
					// short opcode = dIn.getOpcode();
					/* int unknownID = */dIn.readInt();
					int connectionID = dIn.readInt();
					int iExpectedPacketSize = dIn.readReversedInt();
					if (client != null) {
						ZoneClientThread clientThread = client.getUpdateThread();
						if (clientThread != null) {
							clientThread.terminate();
						}
						vAllClients.remove(address);
						client = null;
					}
					client = new ZoneClient(-1, this, connectionID, Math.min(
							iExpectedPacketSize, MAX_PACKET_SIZE));
					client.setClientAddress(address);
					vAllClients.put(address, client);
					//client.setOutgoingSequence((short) 0);
					client.addPacketToParse(incData);

				} else {
					client = vAllClients.get(address);
					if (client != null) {
						incData = PacketUtils.Decrypt(incData, incData.length,
								client.getCRCSeed());
						incData = PacketUtils.decompress(incData);
						client.addPacketToParse(incData);
					}
				}

			} catch (SocketTimeoutException ee) {

				//if (!vAllClients.isEmpty()) {
				//	System.out.println("Socket timeout.");
				//}
			} catch (Exception e) {
				DataLog.logEntry("Exception in ZoneServer thread: " + e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
				//System.out.println("Exception in ZoneServer thread: " + e.toString());
				e.printStackTrace();
			}

			/**
			 * This is the Web update Section.
			 * This will call the routine that will cause a new web file to be written to the web path
			 */
			lNextWebUpdate -=  Math.max(lDeltaSystemTimeMS, 15); // Force 15 

			if(lNextWebUpdate <= 0)

			{
				lNextWebUpdate = 180000;

				DatabaseInterface.updateGalaxyStatus(iServerID, iServerStatus,getPlayersOnline());
				
				//time to Update the Web
				writeGalaxyStatusFile("Zone Server",true);
				writeGalaxyPlayerPositions();
                /**
                 * @todo Server needs to be able to know what planets it controls for status of multiple zone servers
                 */
                DatabaseInterface.updateInstrumentationProcess("ZoneServer_" + this.getServerID() + "_" + this.getHostName(), lServerStartupTime, SWGGui.getUsedMemory(), SWGGui.getFreeMemory(), SWGGui.getTotalMemory(), SWGGui.getProcessorCount());
				//lNextWebUpdate = System.currentTimeMillis() + ((1000 * 60) * 3 );
				
			}

		}
	}

	protected int getPlayersOnline(){
		int iPlayerCount = 0;
		for(int i = 0; i < 10; i++)
		{
			Vector<Player> vPL = this.getStatusPlayersOnPlanet(i,true);
			if(vPL!=null)
			{
				iPlayerCount += vPL.size();
			}
		}
		return iPlayerCount;
	}
	/**
	 * Gets the current weather index.
	 *
	 * @return The current weather index.
	 */
	public int getCurrentWeather(int iPlanetID) {
		if(iPlanetID>9)
		{
			return 0;
		}
		return iCurrentWeatherByPlanet[iPlanetID];
	}
    private long lNextTimeUpdateMS = 60000l;

	private void updateWeather(long lDeltaTimeMS) {
		//		// Temp code -- let's shorten things down a bit.
		try {
			for (int i = 0; i < iCurrentWeatherByPlanet.length - 1; i++) {
				lCurrentWeatherMS[i] -= lDeltaTimeMS;
                lNextTimeUpdateMS -= lDeltaTimeMS;
                if (lNextTimeUpdateMS <= 0) {
                    lNextTimeUpdateMS = 60000l;
					byte[] thePacket = null;
					try {
						thePacket = PacketFactory
						.buildServerTimeMessage();
					} catch (IOException e) {
						DataLog.logEntry("Error building weather packet for planet " + Constants.PlanetNames[i] + ": " + e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
						//System.out.println("Error building weather packet for planet " + Constants.PlanetNames[i] + ": " + e.toString());
						e.printStackTrace();
					}
					Enumeration<ZoneClient> vAllClientsEnum = getAllClients()
					.elements();
					while (vAllClientsEnum.hasMoreElements()) {
						ZoneClient client = vAllClientsEnum.nextElement();
						Player player = client.getPlayer();
						if (player != null) {
							if (player.getPlanetID() == i) {
								client.insertPacket(thePacket);
							}
						}
					}
                    
                }
                if (lCurrentWeatherMS[i] <= 0) {

					iCurrentWeatherByPlanet[i]++;
					iCurrentWeatherByPlanet[i] = iCurrentWeatherByPlanet[i]
					                                                     % Constants.WeatherTimesMS.length;
					/*System.out.print("ZoneServer DEBUG: Changing weather on planet " + Constants.PlanetNames[i] + ", weather now ");
					if (i == Constants.TATOOINE) {
						System.out.println(Constants.TatooineWeatherNames[iCurrentWeatherByPlanet[i]]);
					} else {
						System.out.println(Constants.WeatherNames[iCurrentWeatherByPlanet[i]]);
					}*/

					lCurrentWeatherMS[i] = SWGGui
					.getRandomLong(Constants.WeatherTimesMS[iCurrentWeatherByPlanet[i]]);
					byte[] thePacket = null;
					try {
						thePacket = PacketFactory
						.buildServerWeatherMessage(iCurrentWeatherByPlanet[i]);
					} catch (IOException e) {
						DataLog.logEntry("Error building weather packet for planet " + Constants.PlanetNames[i] + ": " + e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
						//System.out.println("Error building weather packet for planet " + Constants.PlanetNames[i] + ": " + e.toString());
						e.printStackTrace();
					}
					Enumeration<ZoneClient> vAllClientsEnum = getAllClients()
					.elements();
					while (vAllClientsEnum.hasMoreElements()) {
						ZoneClient client = vAllClientsEnum.nextElement();
						Player player = client.getPlayer();
						if (player != null) {
							if (player.getPlanetID() == i) {
								client.insertPacket(thePacket);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			DataLog.logEntry("Caught weather exception: " + e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			System.out.println("Caught weather exception: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Checks the list of all Players for this Zone Server to see if the given
	 * first name is already in use.
	 *
	 * @param sNameToCheck --
	 *            The name to check.
	 * @return True if the name is in use, false if it is not.
	 */
	public boolean isNameUsed(String sNameToCheck) {
		Player player;
		Iterator<Player> itr = vAllPlayers.values().iterator();
		while (itr.hasNext()) {
			player = itr.next();// .getPlayer();
			if (player.getFullName().toLowerCase().equals(
					sNameToCheck.toLowerCase())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Removes the given client from the list of active clients.
	 *
	 * @param client
	 *            The client to remove.
	 */
	public synchronized void removeClient(ZoneClient client) {
		if (vAllClients.contains(client)) {
			vAllClients.remove(client);
			Player player = client.getPlayer();
			if (player != null) {
				removeFromTree(player);
			}
		}
	}

	/**
	 * Adds the given Player to the given Client, so that the Client may
	 * continue the login process.
	 *
	 * @param client --
	 *            The client to receive a Player.
	 * @param player --
	 *            The Player the client wishes to use.
	 */
	public void addPlayerToClient(ZoneClient client, Player player) {
		client.addPlayer(player);
	}

	/**
	 * This function will get the skill requirement, by ID, of the given
	 * command.
	 *
	 * @param commandCRC --
	 *            The command CRC.
	 * @return The skill ID, or -1 if there is no skill requirement for this
	 *         command.
	 */
	protected int getRequiredCommandSkill(int commandCRC) {
		CombatAction action = vCombatActions.get(commandCRC);
		if (action != null) {
			return action.getRequiredSkill();
		} else {
			return -1;
		}
	}

	/**
	 * Gets the server name.
	 *
	 * @return The server name.
	 */
	protected String getClusterName() {
		// System.out.println("Cluster Name Requested. Ret Val: " +
		// sClusterName);
		return sClusterName;
	}

	/**
	 * Sets the server ID.
	 *
	 * @param i --
	 *            The server ID.
	 */
	protected void setServerID(int i) {
		iServerID = i;
	}

	/**
	 * Gets the server ID.
	 *
	 * @return The server ID.
	 */
	protected int getServerID() {
		return iServerID;
	}

	/**
	 * Gets the Player associated with the given Object ID
	 *
	 * @param objectID --
	 *            The Player being sought.
	 * @return The player, or null if no player exists with the given Object ID.
	 */
	protected Player getPlayer(long objectID) {
		return vAllPlayers.get(objectID);
	}

	protected Player getPlayer(String playerName) {
		Enumeration<Player> players = vAllPlayers.elements();
		while (players.hasMoreElements()) {
			Player p = players.nextElement();
			if (p.getFirstName().equalsIgnoreCase(playerName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Gets the Client associated with the given IP address.
	 *
	 * @param address --
	 *            The IP address of the client.
	 * @return The client, or null if we have no client stored with the given IP
	 *         address.
	 */
	protected ZoneClient getClient(SocketAddress address) {
		return vAllClients.get(address);
	}

	protected synchronized DataOutputStream getOutputStream() {
		return dOut;
	}

	/**
	 * This function determines whether any 2 objects are closer to each other
	 * than a given distance.
	 *
	 * @param source --
	 *            The object which requires the check to be performed.
	 * @param target --
	 *            The object we are checking range on.
	 * @param distance --
	 *            The distance we want to compare to.
	 * @return If the distance from the source to the target is less than the
	 *         distance passed in.
	 */
	protected static boolean isInRange(SOEObject source, SOEObject target,
			double distance) {
		return (getRangeBetweenObjects(source, target) < distance);
	}

	protected static boolean isInRange2D(SOEObject source, SOEObject target, double distance) {
		return TrigAngleUtils.getDistance((int)source.getX(), (int)source.getY(), (int)target.getX(), (int)target.getY()) < distance;
	}
	
	protected static boolean isInRange2D(SOEObject source, SOEObject target) {
		return TrigAngleUtils.getDistance((int)source.getX(), (int)source.getY(), (int)target.getX(), (int)target.getY()) < Constants.CHATRANGE;
	}
	
	/**
	 * This function determines whether any 2 objects are within CHATRANGE of
	 * each other.
	 *
	 * @param source --
	 *            The object that we're searching from.
	 * @param target --
	 *            The object we want to test in-range on.
	 * @return -- Whether the target is within range of the source.
	 */

	protected static boolean isInRange(SOEObject source, SOEObject target) {
		return (getRangeBetweenObjects(source, target) < Constants.CHATRANGE);
	}

	protected static float getRangeBetweenObjects(SOEObject source, SOEObject target) {
		
		int x1 = (int)source.getX();
		int y1 = (int)source.getY();
		int z1 = (int)source.getZ();
		int x2 = (int)target.getX();
		int y2 = (int)target.getY();
		int z2 = (int)target.getZ();
		return TrigAngleUtils.getDistance(TrigAngleUtils.getDistance(x1, y1, x2, y2), z2-z1);// System.out.println("IsinRange: Source: " + p1.getFullName() + ",
		// coordinates: X " + x1 + ", Y: " + y1 + ", Z: " + z1);
		// System.out.println("Target: " + p2.getFullName() + ", coordinates: X:
		// " + x2 + ", Y: " + y2 + ", Z: " + z2);
		/*double deltaXSquared = 0;
		double deltaYSquared = 0;
		double deltaZSquared = 0;
		double distanceBetweenObjectsSquared = 0;

		// Pythagorean theorem -- the distance between 2 points is the square
		// root of {
		// the square of the X distances + the square of the Y distances + the
		// square of the Z distances;
		deltaXSquared = (x2 - x1) * (x2 - x1);
		deltaYSquared = (y2 - y1) * (y2 - y1);
		deltaZSquared = (z2 - z1) * (z2 - z1);
		distanceBetweenObjectsSquared = deltaXSquared + deltaYSquared + deltaZSquared;
		return (float)Math.sqrt(distanceBetweenObjectsSquared);*/
	}

	/**
	 * This function adds the object being inserted to the vector, and spawns it
	 * to all other players.
	 *
	 * @param o --
	 *            The object to be added.
	 */
	private void addToTree(SOEObject o) {
		int planetID = o.getPlanetID();
		coffeetree.get(planetID).addToGrid(o);
	}

	protected Grid getGrid(int id) {
		return coffeetree.get(id);
	}
	/**
	 * This function gets all of the Player objects which are currently spawned
	 * and within CHATRANGE of the NPC.
	 *
	 * @param npc --
	 *            The NPC which we are spawning.
	 * @return A list of all Players within range of the NPC.
	 */
	protected Vector<Player> getPlayersAroundNPC(NPC npc) {
		int planetID = npc.getPlanetID();
		// System.out.println("GetPlayersAroundPlayer -- Planet ID " +
		// Constants.PlanetNames[planetID]);
		Vector<Player> vPlayersToReturn = new Vector<Player>();
		GridElement element = coffeetree.get(planetID).getNearestElement(npc.getX(), npc.getY());
		if (element != null) {
			Vector<Player> vNearbyPlayers = element.getAllNearPlayers();
			for (int i = 0; i < vNearbyPlayers.size(); i++) {
				Player p = vNearbyPlayers.elementAt(i);
				if (isInRange(p, npc, Constants.CHATRANGE)) {
					vPlayersToReturn.add(p);
				}
			}
		}
		return vPlayersToReturn;
	}

	protected Vector<SOEObject> getCreaturesAroundPlayer(Player p) {
		int planetID = p.getPlanetID();
		GridElement element = coffeetree.get(planetID).getNearestElement(p.getX(), p.getY());
		Vector<SOEObject> vCreaturesToReturn = new Vector<SOEObject>();
		if (element != null) {
			Vector<SOEObject> vNearbyPlayers = element.getAllNearCreatures();
			for (int i = 0; i < vNearbyPlayers.size(); i++) {
				SOEObject obj = vNearbyPlayers.elementAt(i);
				if (isInRange(p, obj, Constants.CHATRANGE)) {
					vCreaturesToReturn.add(p);
				}
			}
		}
		return vCreaturesToReturn;
	}
	
	/**
	 * This function gets a list of all objects spawned of any type near the
	 * passed in Player.
	 *
	 * @param p -- The player.
	 * @return The list of all nearby objects.
	 */
	protected Vector<SOEObject> getWorldObjectsAroundObject(SOEObject p) {
		int planetID = p.getPlanetID();
		Vector<SOEObject> vObjectListToReturn = new Vector<SOEObject>();
		GridElement element = coffeetree.get(planetID).getNearestElement(p.getX(), p.getY());
		if (element != null) {
			ConcurrentHashMap<Long, SOEObject> vAllNearObjects = element.getAllNearObjects();
			Enumeration<SOEObject> vGridObjects = vAllNearObjects.elements();
			while (vGridObjects.hasMoreElements()) {
				SOEObject o = vGridObjects.nextElement();
				if (!o.getIsStaticObject()) {
					if (o instanceof Cell) {
						Cell c = (Cell)o;
						Structure s = c.getBuilding();
						if (isInRange(p, s)) {
							SOEObject[] vCellObjects = (SOEObject[])c.getCellObjects().values().toArray();
							if (vCellObjects != null) {
								for (int j = 0; j < vCellObjects.length; j++) {
									SOEObject cellObject = vCellObjects[j];
									if (cellObject.getID() != p.getID()) {
										vObjectListToReturn.add(cellObject);
									}
	
								}
							}
						}
					} else if (o instanceof Shuttle) {
						Shuttle s = (Shuttle)o;
						if (isInRange(s, p, 512f)) {
							vObjectListToReturn.add(o);
						}
					}
					else if(o instanceof Terminal)
					{
						switch(o.getTemplateID())
						{                                            
						case 7716://"@No Build Zone 768"
						{
							if (isInRange(o, p, 1250f)) {vObjectListToReturn.add(o);}
							break;
						}
						case 8067://"@No Build Zone 128"
						{
							if (isInRange(o, p, 256f)) {vObjectListToReturn.add(o);}
							break;
						}
						case 8070://"@No Build Zone 64"
						{
							if (isInRange(o, p, 192f)) {vObjectListToReturn.add(o);}
							break;
						}
						case 8068://"@No Build Zone 32"
						{
							if (isInRange(o, p, 128f)) {vObjectListToReturn.add(o);}
							break;
						}    
						case 8069://"@No Build Zone 4"
						{
							if (isInRange(o, p, 64f)) {vObjectListToReturn.add(o);}
							break;
						}
						default:
						{
							if ((o.getIsStaticObject() == false) && (o.getID() != p.getID()) && isInRange(p, o)) 
							{
								vObjectListToReturn.add(o);
							}
						}
						}                                                                                
					}
					else
					{
						if ((o.getIsStaticObject() == false) && (o.getID() != p.getID()) && isInRange(p, o)) {
							vObjectListToReturn.add(o);
						}
					}
				}
				else if (o.getIsStaticObject()) {
					if (o instanceof Structure)
					{
						Structure s = (Structure)o;
						if (isInRange(p, s)) {
							vObjectListToReturn.add(s);
						}
					}
				}
			}
		} else {
			System.out.println("Null nearest grid element returned for object coords x["+p.getX()+"], y["+p.getY()+"]");
		}
		return vObjectListToReturn;
	}

	/**
	 * This function gets a list of all objects spawned of any type near the
	 * passed in Player.
	 *
	 * @param p -- The player.
	 * @param range -- The distance in which to check.
	 * @return The list of all nearby objects.
	 */
	protected Vector<SOEObject> getWorldObjectsAroundObject(SOEObject p, float range) {
		int planetID = p.getPlanetID();
		Vector<SOEObject> vObjectListToReturn = new Vector<SOEObject>();
		GridElement element = coffeetree.get(planetID).getNearestElement(p.getX(), p.getY());
		if (element != null) {
			ConcurrentHashMap<Long, SOEObject> vAllNearObjects = element.getAllNearObjects();
			Enumeration<SOEObject> vGridObjects = vAllNearObjects.elements();
			while (vGridObjects.hasMoreElements()) {
				SOEObject o = vGridObjects.nextElement();
				if (!o.getIsStaticObject()) {
					if (o instanceof Cell) {
						Cell c = (Cell)o;
						Structure s = c.getBuilding();
						if (isInRange(p, s, range)) {
							SOEObject[] vCellObjects = (SOEObject[])c.getCellObjects().values().toArray();
							if (vCellObjects != null) {
								for (int j = 0; j < vCellObjects.length; j++) {
									SOEObject cellObject = vCellObjects[j];
									if (cellObject.getID() != p.getID()) {
										vObjectListToReturn.add(cellObject);
									}
	
								}
							}
						}
					} else if (o instanceof Shuttle) {
						Shuttle s = (Shuttle)o;
						if (isInRange(s, p, 512f)) {
							vObjectListToReturn.add(o);
						}
					}
					else if(o instanceof Terminal)
					{
						switch(o.getTemplateID())
						{                                            
						case 7716://"@No Build Zone 768"
						{
							if (isInRange(o, p, 1250f)) {vObjectListToReturn.add(o);}
							break;
						}
						case 8067://"@No Build Zone 128"
						{
							if (isInRange(o, p, 256f)) {vObjectListToReturn.add(o);}
							break;
						}
						case 8070://"@No Build Zone 64"
						{
							if (isInRange(o, p, 192f)) {vObjectListToReturn.add(o);}
							break;
						}
						case 8068://"@No Build Zone 32"
						{
							if (isInRange(o, p, 128f)) {vObjectListToReturn.add(o);}
							break;
						}    
						case 8069://"@No Build Zone 4"
						{
							if (isInRange(o, p, 64f)) {vObjectListToReturn.add(o);}
							break;
						}
						default:
						{
							if ((o.getIsStaticObject() == false) && (o.getID() != p.getID()) && isInRange(p, o, range)) 
							{
								vObjectListToReturn.add(o);
							}
						}
						}                                                                                
					}
					else
					{
						if ((o.getIsStaticObject() == false) && (o.getID() != p.getID()) && isInRange(p, o, range)) {
							vObjectListToReturn.add(o);
						}
					}
				}
				else if (o.getIsStaticObject()) {
					if (o instanceof Structure)
					{
						Structure s = (Structure)o;
						if (isInRange(p, s, range)) {
							vObjectListToReturn.add(s);
						}
					}
				}
			}
		} else {
			System.out.println("Null nearest grid element returned for object coords x["+p.getX()+"], y["+p.getY()+"]");
		}
		return vObjectListToReturn;
	}

	protected Vector<SOEObject> getStaticObjectsAroundObject(SOEObject p) {
		int planetID = p.getPlanetID();
		Vector<SOEObject> vObjectListToReturn = new Vector<SOEObject>();
		GridElement element = coffeetree.get(planetID).getNearestElement(p.getX(), p.getY());
		if (element != null) {
			ConcurrentHashMap<Long, SOEObject> vAllNearObjects = element.getAllNearObjects();
			Enumeration<SOEObject> vGridObjects = vAllNearObjects.elements();
			while (vGridObjects.hasMoreElements()) {
				SOEObject object = vGridObjects.nextElement();
				if (object.getIsStaticObject()) {
					if (isInRange(p, object)) {
						vObjectListToReturn.add(object);
					}
				}
			}
		} else {
			System.out.println("Null nearest grid element returned for object coords x["+p.getX()+"], y["+p.getY()+"]");
		}
		return vObjectListToReturn;
	}

	protected Vector<SOEObject> getStaticObjectsAroundObject(SOEObject p, int range) {
		int planetID = p.getPlanetID();
		Vector<SOEObject> vObjectListToReturn = new Vector<SOEObject>();
		GridElement element = coffeetree.get(planetID).getNearestElement(p.getX(), p.getY());
		if (element != null) {
			ConcurrentHashMap<Long, SOEObject> vAllNearObjects = element.getAllNearObjects();
			Enumeration<SOEObject> vGridObjects = vAllNearObjects.elements();
			while (vGridObjects.hasMoreElements()) {
				SOEObject object = vGridObjects.nextElement();
				if (object.getIsStaticObject()) {
					if (isInRange(p, object, (float)range)) {
						vObjectListToReturn.add(object);
					}
				}
			}
		} else {
			System.out.println("Null nearest grid element returned for object coords x["+p.getX()+"], y["+p.getY()+"]");
		}
		return vObjectListToReturn;
	}

	/**
	 * This function gets a list of all the spawned Players near the given
	 * Player.
	 *
	 * @param p --
	 *            The player.
	 * @return The list of nearby players.
	 */
	protected Vector<Player> getPlayersAroundObject(SOEObject p, boolean bIncludePlayer) {
		int planetID = p.getPlanetID();
		/**
		 * @todo handle planets other than the first 10 for space and tutorial.
		 * Space "planets" cannot be handled by a Quadtree -- must be handled by an Octree -- 3 dimensions.
		 */
		/*
        if(planetID == Constants.TUTORIAL)
        {
            Vector<Player> vPL = new Vector<Player>();
            if(bIncludePlayer && p instanceof Player)
            {
                vPL.add((Player)p);
            }
            return vPL;
        }       
		 */
		// System.out.println("GetPlayersAroundPlayer -- Planet ID " +
		// Constants.PlanetNames[planetID]);
		GridElement element = coffeetree.get(planetID).getNearestElement(p.getX(), p.getY());
		Vector<Player> vPlayerList = null;
		if (element != null) {
			vPlayerList = element.getAllNearPlayers();
			if (!bIncludePlayer) {
				boolean bFound = false;
				for (int i = 0; i < vPlayerList.size() && !bFound; i++) {
					Player player = vPlayerList.elementAt(i);
					if (player.getID() == p.getID()) {
						vPlayerList.remove(i);
						bFound = true;
					}
				}
			}
		} else {
			DataLog.logEntry("Coffeetree returned null element for object at ("+p.getX()+", " + p.getY()+")", "ZoneServer", Constants.LOG_SEVERITY_CRITICAL, true, true);
		}
		if (vPlayerList == null) {
			vPlayerList = new Vector<Player>();
		}
		return vPlayerList;
	}

	protected Vector<Player> getPlayersAroundObject(SOEObject p, boolean bIncludePlayer, float range) {
		Vector<Player> vPlayerList = getPlayersAroundObject(p, bIncludePlayer);
		for (int i = 0; i < vPlayerList.size(); i++) {
			if (!isInRange(p, vPlayerList.elementAt(i), range)) {
				vPlayerList.remove(i);
				i--;
			}
		}
		return vPlayerList;
	}
	/**
	 * This function gets an SWG game object from the list of all known objects
	 * by ID.
	 *
	 * @param objectID --
	 *            The ID of the object being searched for.
	 * @return The SOEObject with the given ID, or null if the object does not
	 *         exist.
	 */
	protected SOEObject getObjectFromAllObjects(long objectID) {
		return vAllSpawnedObjects.get(objectID);
	}

	protected SOEObject removeObjectFromAllObjects(long objectID) {
		vUsedObjectID.remove(objectID);
		return vAllSpawnedObjects.remove(objectID);
	}

	/**
	 * Adds the given object to the list of all known objects in the game.
	 *
	 * @param o --
	 *            The object to add.
	 * @param bInTree --
	 *            Indicates if the object is to be spawned or not.
	 */
	protected void addObjectToAllObjects(SOEObject o, boolean bInTree,boolean debug) {
		try{
			/*if (vAllSpawnedObjects == null) {
			System.out.println("How could you possibly be null???");
			System.exit(12345);
		} else if (o == null) {
			System.out.println("Null object being added to tree!");
			return;
		}*/
			if(o==null){
				return;
			}
			
			if(!vAllSpawnedObjects.containsKey(o.getID()))
			{
				vAllSpawnedObjects.put(o.getID(), o);                    
			}
			else
			{
				// Duplicate object ID -- reassign.
				SOEObject r = vAllSpawnedObjects.get(o.getID());
				if(r != o) // Testing if the pointers are the same... this might work here, but it won't necessarially always work elsewhere.
					// Use .equals();
				{
					//reassign if and only if the objects are not the same
					long oldID = o.getID();
					o.setID(getNextObjectID());
					vAllSpawnedObjects.put(o.getID(), o);
					DataLog.logEntry("----Duplicate ID Being Inserted to all Objects! Reassigned ID!: " + o.getID() + " Old ID:" + oldID,"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
					//System.out.println("----Duplicate ID Being Inserted to all Objects! Reassigned ID!: " + o.getID() + " Old ID:" + oldID);
					//log what we just did so the server admins can look it over in case someone complaints that "hey i lost my thingymagigger!"
					//DataLogObject L = new DataLogObject("ZoneServer.addObjectToAllObjects()","----Duplicate ID Being Inserted to all Objects! Reassigned ID!: " + o.getID() + " Old ID:" + oldID,Constants.LOG_SEVERITY_CRITICAL);
					//this.DataLog.qServerLog.add(L);
				}
			}
			

			int planetID = o.getPlanetID();
			if (planetID >= 0 && planetID < Constants.PlanetNames.length) {
				if (bInTree) {
					try {
						coffeetree.get(o.getPlanetID()).addToGrid(o);
					} catch (Exception e) {
						DataLog.logEntry("Error adding object iff name " + o.getIFFFileName() + " to grid.  Coordinates: " + Constants.PlanetNames[o.getPlanetID()] + " X=" + o.getX() + ", Y=" + o.getY(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
						//System.out.println("Error adding object iff name " + o.getIFFFileName() + " to grid.  Coordinates: " + Constants.PlanetNames[o.getPlanetID()] + " X=" + o.getX() + ", Y=" + o.getY());
					}
				}
			}
			else if(planetID == Constants.TUTORIAL)
			{
				if (bInTree) {
					try {
						coffeetree.get(o.getPlanetID()).addToGrid(o);
					} catch (Exception e) {
						DataLog.logEntry("Error adding object iff name " + o.getIFFFileName() + " to grid.  Coordinates: " + Constants.TerrainNames[o.getPlanetID()] + " X=" + o.getX() + ", Y=" + o.getY(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
						//System.out.println("Error adding object iff name " + o.getIFFFileName() + " to grid.  Coordinates: " + Constants.PlanetNames[o.getPlanetID()] + " X=" + o.getX() + ", Y=" + o.getY());
					}
				}
			}
			addObjectIDToAllUsedID(o.getID());
			if (o instanceof TangibleItem) {
				TangibleItem tano = (TangibleItem)o;
				//addSerialNumberToAllUsedSerialNumbers(tano.getSerialNumber());
			} else if (o instanceof ManufacturingSchematic) {
				ManufacturingSchematic msco = (ManufacturingSchematic)o;
				//addSerialNumberToAllUsedSerialNumbers(msco.getSerialNumber());
			}
			
			if ((o instanceof NPC)) {
				int iPlanetID = o.getPlanetID();
				if(o!=null && !o.getIsStaticObject())
				{
					if (bInTree) {
						vNPCUpdateThreads.get(iPlanetID).addNPC((NPC)o);
					}
				}
				else if(o==null)
				{
					DataLog.logEntry("addObjectToAllObjects Object null to NPC Thread","ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
					//System.out.println("addObjectToAllObjects Object null to NPC Thread");
				}
				else if(o.getIsStaticObject())
				{
					// System.out.println("addObjectToAllObjects Object was World Object to NPC Thread not Added");
				}
			}

		}catch(Exception e){
			DataLog.logEntry("Exception caught in addObjectToAllObjects " + e ,"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Exception caught in addObjectToAllObjects " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Removes the given object from the list of all known objects in the game.
	 *
	 * @param o --
	 *            The object to remove.
	 * @param bInTree --
	 *            Indicates if the object had been spawned or not.
	 */
	protected void removeObjectFromAllObjects(SOEObject o, boolean bInTree) {
		vAllSpawnedObjects.remove(o.getID());
		vUsedObjectID.remove(o.getID());
		
		if (bInTree) {
			Vector<Player> vInRangePlayers = getPlayersAroundObject(o, false);
			for (int i = 0; i < vInRangePlayers.size(); i++) {
				Player p = vInRangePlayers.elementAt(i);
				try {
					p.despawnItem(o);
				} catch (Exception e) {
					DataLog.logEntry("ZoneServer::removeObjectFromAllObjects:  Error building destroy object packet: "+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
					//System.out.println("ZoneServer::removeObjectFromAllObjects:  Error building destroy object packet: "+ e.toString());
					e.printStackTrace();
				}
			}
		}
		coffeetree.get(o.getPlanetID()).removeFromGrid(o);

	}

	protected ConcurrentHashMap<Long, SOEObject> getAllObjects() {
		return vAllSpawnedObjects;
	}

	/**
	 * This function will move the Player object between "planets".
	 *
	 * @param player --
	 *            The player being moved.
	 * @param oldPlanetID --
	 *            The player's previous planet ID.
	 * @param newPlanetID --
	 *            The player's destination planet ID.
	 */
	protected void moveObjectInTree(SOEObject player, float newX, float newY, int oldPlanetID,
			int newPlanetID) {
		//System.out.println("Player moving.  Old coords: " + player.getX() + ", " + player.getY() + ".  New coords: " + newX + ", " + newY);
		if (oldPlanetID == newPlanetID) {
			if (!coffeetree.get(oldPlanetID).move(player, newX, newY)) {
				removeFromTree(player);
				player.setX(newX);
				player.setY(newY);
				addToTree(player);
			} 
		} else {
			removeFromTree(player);
			player.setPlanetID(newPlanetID);
			player.setX(newX);
			player.setY(newY);
			addToTree(player);
		}
	}


	/**
	 * Gets the port which this ZoneServer's ping thread is listening on.
	 *
	 * @return The ping server port.
	 */
	public int getPingPort() {
		return pingServer.getPort();
	}

	/**
	 * Gets the port which this server is listening for packets on.
	 *
	 * @return The port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return The number of active client connections.
	 */
	public int getClientConnectionCount() {
		return vAllClients.size();
	}

	/**
	 * Gets the maximum number of simultaneous client connections this
	 * ZoneServer supports.
	 *
	 * @return The maximum connected clients.
	 */
	public int getMaxClients() {
		return iMaxClients;
	}

	/**
	 * Gets the maximum number of characters any given account may create on
	 * this server.
	 *
	 * @return The character per account cap.
	 */
	public int getMaxChars() {
		return iMaxCharactersPerAccount;
	}

	/**
	 * Gets the time offset in hours between the timezone where the server is
	 * running and the timezone where the client is running.
	 *
	 * @return The time offset.
	 */
	public int getDistanceToServer() {
		// distance to server is unknown how it is calculated.
		return 3;
	}

	/**
	 * Gets the current status of the ZoneServer, where 0 is offline, 1 is
	 * loading, 2 is online, 3 is locked.
	 *
	 * @return The current server status.
	 */
	public int getStatus() {
		return iServerStatus;
	}

	/**
	 * Gets the GUI this server is running on.
	 *
	 * @return The GUI.
	 */
	public SWGGui getGUI() {
		return theGui;
	}

	/**
	 * Gets the canonical host name of this ZoneServer's internet address.
	 *
	 * @return The host name.
	 */
	public String getHostName() {
		if(sHostName == null)
		{
			return "ZoneServer: NullHostName";
		}
		return sHostName;
	}

	/**
	 * Gets the socket which this server is using to send and receive packets.
	 *
	 * @return The socket.
	 */
	public synchronized DatagramSocket getSocket() {
		return socket;
	}

	/**
	 * Searches for and removes the ZoneClient with the given internet address.
	 * If there is no client at that address, this function does nothing.
	 *
	 * @param address --
	 *            The Internet Address of the client to remove.
	 */
	public void removeClientByAddress(SocketAddress address) {
		if (vAllClients.containsKey(address)) {
			ZoneClient client = vAllClients.remove(address);
			Player player = client.getPlayer();
			if (player != null) {
				removeFromTree(player);
			}
		} else {
			DataLog.logEntry("Error removing client by address:  Key not found -- " + address,"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Error removing client by address:  Key not found -- " + address);
		}
	}

	/**
	 * Gets the outgoing packet queue.
	 *
	 * @return The outgoing packet queue.
	 */
	public List<DatagramPacket> getSendPacketQueue() {
		return vPacketsToSend;
	}

	/**
	 * Gets the number of packets waiting to be sent.
	 *
	 * @return The number of packets waiting to be sent.
	 */
	public int getSendPacketQueueCount() {
		if (vPacketsToSend.isEmpty()) {
			return 0;
		} else {
			return vPacketsToSend.size();
		}
	}

	/**
	 * Adds a packet to the queue of packets waiting to be sent.
	 *
	 * @param p
	 */
	public synchronized void queue(DatagramPacket p) {
		synchronized(this) {
			vPacketsToSend.add(p);
		}
		/*
                byte [] buffer = p.getData();
                String ipNPort = p.getAddress().toString() + ":" + p.getPort();
                ipNPort = ipNPort.replace("/","");
                DataLogObject L = new DataLogObject("ZoneServer.queue()","SourceIP",buffer,ipNPort,Constants.LOG_PACKET_DIRECTION_OUT);
                this.DataLog.logPacket(L);
		 * */
	}

	/**
	 * Adds a newly created player to the list of all players available on the
	 * server.
	 *
	 * @param p --
	 *            The player to add.
	 */
	protected void addNewPlayer(Player p) {
		vAllPlayers.put(p.getID(), p);
	}

	protected ConcurrentHashMap<Long, Player> getAllPlayers() {
		return vAllPlayers;
	}

	/*
	 * public static void main(String[] args) throws Exception{ InetAddress
	 * address = InetAddress.getByName("precu.dyndns.org");
	 * System.out.println("Resolved address: " + address.getHostAddress()); }
	 */

	/**
	 * This function removes a player from the tree of players for their given
	 * planet.
	 *
	 * @param player --
	 *            The player to remove.
	 */
	protected void removeFromTree(SOEObject player) {
		if (player == null) {
			return;
		}
		Vector<Player> vObjectsAfterRemoval = getPlayersAroundObject(player, false);
		for (int i = 0; i < vObjectsAfterRemoval.size(); i++) {
			SOEObject o = (SOEObject) vObjectsAfterRemoval.get(i);
			if (o instanceof Player) {
				Player recipient = (Player) o;
				ZoneClient recipClient = recipient.getClient();
				if (recipClient != null) {
					try {
						recipient.despawnItem(player);
					} catch (IOException e) {
						DataLog.logException("Error building SceneDestroyObject packet: "+ e.toString(),"ZoneServer()", ZoneRunOptions.bLogToConsole, true, e);
					}
				}
			}
			if (o instanceof NPC) {
				NPC npc = (NPC)o;
				npc.removeFromHateList(player);  // Instantly stop fighting the Player that has left this tree.
			}
		}
		coffeetree.get(player.getPlanetID()).removeFromGrid(player);
		if (player instanceof NPC) {
			int iPlanetID = player.getPlanetID();
			vNPCUpdateThreads.get(iPlanetID).removeNPC((NPC)player);
		}
	}

	/**
	 * Gets the current Message of the Day.
	 *
	 * @return The MOTD.
	 */
	protected String getMOTD() {
		return sMOTD;
	}

	protected ItemTemplate getTemplateData(int iTemplateID) {
		return DatabaseInterface.getTemplateDataByID(iTemplateID);
	}

	protected ItemTemplate getTemplateDataByCRC(int crc) {
		return DatabaseInterface.getTemplateDataByCRC(crc);
	}

	protected ResourceManager getResourceManager() {
		return resourceManager;
	}

	protected long getNextObjectID() {
		long toReturn = 0;
		do {
			toReturn = SWGGui.getRandomLong((long) (Integer.MAX_VALUE) * 2);
			// System.out.println("Generated new object ID 0x" +
			// Long.toHexString(toReturn));
			//the last object in the world object table is less than 10Mil
			//in order to allow modders to mod client without having to worry about world object intefering with
			//existing player objects and not having to go through a database rebuild of some sort
			//the minimum object id returned has to be in the 20Mil range or above.
		} while (bIsObjectIDUsed(toReturn) && toReturn <= 19999999);
		addObjectIDToAllUsedID(toReturn);
		return toReturn;
	}
	
	protected long getNextSerialNumber() {
		long toReturn = 0;
		do {
			toReturn = SWGGui.getRandomLong((long) (Long.MAX_VALUE));
			// System.out.println("Generated new object ID 0x" +
			// Long.toHexString(toReturn));
			//the last object in the world object table is less than 10Mil
			//in order to allow modders to mod client without having to worry about world object intefering with
			//existing player objects and not having to go through a database rebuild of some sort
			//the minimum object id returned has to be in the 20Mil range or above.
		} while (bIsSerialNumberUsed(toReturn));
		addSerialNumberToAllUsedSerialNumbers(toReturn);
		return toReturn;
	}

	public boolean queueEmailClientRequestEmails(ZoneClient client) {
		return eServer.qEmailRequest.offer(client);
	}

	public boolean queueEmailNewClientMessage(SWGEmail E) {
		return eServer.qNewClientMessage.offer(E);
	}

	public boolean queueEmailReadFlag(SWGEmail E) {
		return eServer.qSetReadFlag.offer(E);
	}

	public boolean queueEmailClientRequestContent(SWGEmail E) {
		return eServer.qEmailContentRequest.offer(E);
	}

	public boolean queueEmailClientrequestDelete(SWGEmail E) {
		return eServer.qEmailDeleteRequest.offer(E);
	}

	public boolean queueClientDeleteSentEmailsFromList(ZoneClient client) {
		return eServer.qClearSentEmails.offer(client);
	}

	// vServerRadials
	public Hashtable<Integer, RadialTemplateData> getServerRadialOptions() {
		return vServerRadials;
	}

	protected void addPlayerMapLocation(MapLocationData data, int i) {
		vPlayerMapLocationsByPlanet.get(i).add(data);
	}

	protected Vector<MapLocationData> getStaticMapLocations(String sPlanetName) {
		for (int i = 0; i < Constants.PlanetNames.length; i++) {
			if (sPlanetName.equalsIgnoreCase(Constants.PlanetNames[i])) {
				return vStaticMapLocationsByPlanet.get(i);
			}
		}
		return null;
	}

	protected Vector<MapLocationData> getPlayerMapLocations(String sPlanetName) {
		for (int i = 0; i < Constants.PlanetNames.length; i++) {
			if (sPlanetName.equalsIgnoreCase(Constants.PlanetNames[i])) {
				return vPlayerMapLocationsByPlanet.get(i);
			}
		}
		return null;
	}

	protected void addStaticMapLocation(MapLocationData data, int i) {
		vStaticMapLocationsByPlanet.get(i).add(data);
	}

	// Original function:
	/*protected String getTravelTerminalLocationName(int terminalID) {
    	TravelDestination t = vAllTravelDestinations.get(terminalID);
    	return t.getDepartureName();
    }*/

	protected String getTravelTerminalLocationName(int TerminalID){
		TravelDestination t = null;
		for (int i = 0; i < vAllTravelDestinations.size(); i++) {
			t = vAllTravelDestinations.elementAt(i);
			if (t.getTerminalID() == TerminalID) {
				return t.getDestinationName();
			}
		}
		return null;
	}


	protected void refreshAllTravelDestinations(){

		vAllTravelDestinations = dbInterface.getAllTravelDestinations();

	}

	protected Vector<TravelDestination> getTravelDestinationsForPlanet(Player p, int PlanetID){
		Vector<TravelDestination> retval = new Vector<TravelDestination>();
		if(p.getPlanetID() != PlanetID)
		{
			//return null;
		}
		try{
			//System.out.println("Filling playerTravel Dest Vector for " + Constants.PlanetNames[DeparturePlanetID] + " to " + Constants.PlanetNames[PlanetID]);
			/*
                if(p.getPlanetID() == PlanetID)
            {
                for(int i = 0; i < vAllTravelDestinations.size(); i++)
                {
                    if(vAllTravelDestinations.get(i).getTicketID() == p.getLastUsedTravelTerminal().getTicketID())
                    {
                        System.out.println("Last Terminal used Ticket ID = " + vAllTravelDestinations.get(i).getTicketID() + " " + vAllTravelDestinations.get(i).getDestinationName());
                        retval.add(vAllTravelDestinations.get(i));
                    }
                }
            }
			 */
			for(int i = 0; i < vAllTravelDestinations.size(); i++)
			{
				// System.out.println("Current Location:" + vAllTravelDestinations.get(i).getDestinationName());
				if(vAllTravelDestinations.get(i).getDestinationPlanet() == PlanetID)
				{
					if(vAllTravelDestinations.get(i).getTicketID() != p.getLastUsedTravelTerminal().getTicketID())
					{
						retval.add(vAllTravelDestinations.get(i));
					}
					else
					{
						retval.add(vAllTravelDestinations.get(i));
					}
				}
			}

		}
		catch(Exception e){
			DataLog.logEntry("Exception in getTravelDestinationsForPlanet "+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Exception " + e);
			e.printStackTrace();
		}

		return retval;

	}

	protected Vector<TravelDestination> getAllTravelDestinations(){
		return vAllTravelDestinations;
	}

	protected TravelDestination getTravelDestinationFromName(String Name, int iPlanetID){
		// Broken -- The same destination name exists on more than 1 planet in some cases.
		Name = Name.replace("_", " ");
		// System.out.println("Requested playerTravel Destination Sought " + Name);
		for(int i = 0; i < vAllTravelDestinations.size(); i++)
		{
			TravelDestination t = vAllTravelDestinations.elementAt(i);
			if(t.getDestinationName().equalsIgnoreCase(Name) && t.getDestinationPlanet() == iPlanetID)
			{
				// System.out.println("Requested playerTravel Destination Found " + Name);
				return vAllTravelDestinations.get(i);
			}
		}
		return null;
	}

	protected Shuttle getShuttleObjectByTicketID(int TicketID){

		Shuttle s = null;
		for(int i = 0; i < ServerShuttleList.size(); i++)
		{
			if(ServerShuttleList.get(i).getTicketID() == TicketID)
			{
				s = ServerShuttleList.get(i);
			}
		}
		return s;
	}

	protected Terminal getTicketDroidByTicketID(int TicketID){
		Terminal retval = null;
		for(int i = 0; i < ServerTerminals.size();i++)
		{
			if(ServerTerminals.get(i).getTicketID() == TicketID && ServerTerminals.get(i).getTerminalType() == Constants.TERMINAL_TYPES_TICKET_DROID)
			{
				return ServerTerminals.get(i);
			}
		}
		return retval;
	}

	protected NPCUpdateThread getNPCUpdateThreadByPlanetID(int iPlanetID) {
		return vNPCUpdateThreads.get(iPlanetID);
	}

	protected String[] generateRandomName(String newCharacterRace){

		//System.out.println("Generate Random Name.");
		StringBuffer randomFirstName = new StringBuffer();
		StringBuffer randomLastName = new StringBuffer();
		boolean proceed = false;
		if(vUsedRandomNames==null)
		{
			vUsedRandomNames = new Vector<String>();
		}

		while(!proceed)
		{


			//using the following constants vNameSyllables1, vNameSyllables2, vNameSyllables3, vLastNames1, vLastNames2
			/// make new first name
			if (newCharacterRace.contains("ookiee"))
			{
				int iR1,iR2,iR3,iR4,iR5; //random integers for name selection
				int iFistSyllableCount; //random syllable generation max 5 min 2
				//int iLastSyllableCount; //random syllable generation max 5 min 2
				boolean firstApostrophe = false;
				iFistSyllableCount = SWGGui.getRandomInt(2,5);
				//iLastSyllableCount = SWGGui.getRandomInt(2,5);
				iR1 = SWGGui.getRandomInt(Constants.cntWookieNameSyllable1Count);
				if(iR1 > Constants.cntWookieNameSyllable1Count) { iR1 = Constants.cntWookieNameSyllable1Count;}
				iR2 = SWGGui.getRandomInt(Constants.cntWookieNameSyllable2Count);
				if(iR2 > Constants.cntWookieNameSyllable2Count) { iR2 = Constants.cntWookieNameSyllable2Count;}
				iR3 = SWGGui.getRandomInt(Constants.cntWookieNameSyllable2Count);
				if(iR3 > Constants.cntWookieNameSyllable2Count) { iR3 = Constants.cntWookieNameSyllable2Count;}
				iR4 = SWGGui.getRandomInt(Constants.cntWookieNameSyllable2Count);
				if(iR4 > Constants.cntWookieNameSyllable2Count) { iR4 = Constants.cntWookieNameSyllable2Count;}
				iR5 = SWGGui.getRandomInt(Constants.cntWookieNameSyllable2Count);
				if(iR5 > Constants.cntWookieNameSyllable2Count) { iR5 = Constants.cntWookieNameSyllable2Count;}
				randomFirstName.append(Constants.vWookieNameSyllables1[iR1]);
				randomFirstName.append(Constants.vWookieNameSyllables2[iR2]);
				if(iFistSyllableCount >= 3)
				{
					if(SWGGui.getRandomInt(0,9) == 5)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vWookieNameSyllables2[iR3]);
				}
				if(iFistSyllableCount >= 4)
				{
					if(SWGGui.getRandomInt(0,9) == 5 && !firstApostrophe)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vWookieNameSyllables2[iR2]);
				}
				if(iFistSyllableCount >= 5)
				{
					if(SWGGui.getRandomInt(0,9) == 5 && !firstApostrophe)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vWookieNameSyllables2[iR3]);
				}

			}
			else if (newCharacterRace.contains("doshan"))
			{
				int iR1,iR2,iR3,iR4,iR5; //random integers for name selection
				int iFistSyllableCount; //random syllable generation max 5 min 2
				int iLastSyllableCount; //random syllable generation max 5 min 2
				boolean firstApostrophe = false;
				iFistSyllableCount = SWGGui.getRandomInt(2,4);
				iLastSyllableCount = SWGGui.getRandomInt(2,5);
				iR1 = SWGGui.getRandomInt(Constants.cntTrandoshanNameSyllable1Count);
				if(iR1 > Constants.cntTrandoshanNameSyllable1Count) { iR1 = Constants.cntTrandoshanNameSyllable1Count;}
				iR2 = SWGGui.getRandomInt(Constants.cntTrandoshanNameSyllable2Count);
				if(iR2 > Constants.cntTrandoshanNameSyllable2Count) { iR2 = Constants.cntTrandoshanNameSyllable2Count;}
				iR3 = SWGGui.getRandomInt(Constants.cntTrandoshanNameSyllable2Count);
				if(iR3 > Constants.cntTrandoshanNameSyllable2Count) { iR3 = Constants.cntTrandoshanNameSyllable2Count;}
				iR4 = SWGGui.getRandomInt(Constants.cntTrandoshanNameSyllable2Count);
				if(iR4 > Constants.cntTrandoshanNameSyllable2Count) { iR4 = Constants.cntTrandoshanNameSyllable2Count;}
				iR5 = SWGGui.getRandomInt(Constants.cntTrandoshanNameSyllable2Count);
				if(iR5 > Constants.cntTrandoshanNameSyllable2Count) { iR5 = Constants.cntTrandoshanNameSyllable2Count;}
				randomFirstName.append(Constants.vTrandoshanNameSyllables1[iR1]);
				randomFirstName.append(Constants.vTrandoshanNameSyllables2[iR2]);
				if(iFistSyllableCount >= 3)
				{
					if(SWGGui.getRandomInt(0,9) == 5)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vTrandoshanNameSyllables2[iR3]);
				}
				if(iFistSyllableCount >= 4)
				{
					if(SWGGui.getRandomInt(0,9) == 5 && !firstApostrophe)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vTrandoshanNameSyllables2[iR2]);
				}
				if(iFistSyllableCount >= 5)
				{
					if(SWGGui.getRandomInt(0,9) == 5 && !firstApostrophe)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vTrandoshanNameSyllables2[iR3]);
				}
				//---------------------------------------------------------
				//last name
				if(SWGGui.getRandomInt(0,20) != 0) // one in 20 chance that u dont get a last name even tho your species does have one.
				{
					randomFirstName.append(" ");
					int iRandomLastNameType = 0;
					iRandomLastNameType = SWGGui.getRandomInt(0,9);
					if(iRandomLastNameType == 5)
					{
						//random name based on last names table
						iR1 = SWGGui.getRandomInt(Constants.cntLastNames1Count);
						iR2 = SWGGui.getRandomInt(Constants.cntLastNames2Count);
						randomLastName.append(Constants.vLastNames1[iR1]);
						randomLastName.append(Constants.vLastNames2[iR2]);
					}
					else
					{
						//random last name based on syllable table
						boolean lastApostrophe = false;
						iLastSyllableCount = SWGGui.getRandomInt(5);
						iR1 = SWGGui.getRandomInt(Constants.cntNameSyllable1Count);
						if(iR1 > Constants.cntNameSyllable1Count) { iR1 = Constants.cntNameSyllable1Count;}
						iR2 = SWGGui.getRandomInt(Constants.cntNameSyllable2Count);
						if(iR2 > Constants.cntNameSyllable2Count) { iR2 = Constants.cntNameSyllable2Count;}
						iR3 = SWGGui.getRandomInt(Constants.cntNameSyllable3Count);
						if(iR3 > Constants.cntNameSyllable3Count) { iR3 = Constants.cntNameSyllable3Count;}
						iR4 = SWGGui.getRandomInt(Constants.cntNameSyllable2Count);
						if(iR4 > Constants.cntNameSyllable2Count) { iR4 = Constants.cntNameSyllable2Count;}
						iR5 = SWGGui.getRandomInt(Constants.cntNameSyllable3Count);
						if(iR5 > Constants.cntNameSyllable3Count) { iR5 = Constants.cntNameSyllable3Count;}

						randomLastName.append(Constants.vNameSyllables1[iR1]);
						randomLastName.append(Constants.vNameSyllables2[iR2]);
						if(iLastSyllableCount >= 3)
						{
							if(SWGGui.getRandomInt(0,9) == 5)
							{
								randomLastName.append("'");
								lastApostrophe = true;
							}
							randomLastName.append(Constants.vNameSyllables3[iR3]);
						}
						if(iLastSyllableCount >= 4)
						{
							if(SWGGui.getRandomInt(0,9) == 5 && !lastApostrophe)
							{
								randomLastName.append("'");
								lastApostrophe = true;
							}
							randomLastName.append(Constants.vNameSyllables2[iR2]);
						}
						if(iLastSyllableCount >= 5)
						{
							if(SWGGui.getRandomInt(0,9) == 5 && !lastApostrophe)
							{
								randomLastName.append("'");
								lastApostrophe = true;
							}
							randomLastName.append(Constants.vNameSyllables3[iR3]);
						}
					}
				}
			} //if we want different first names for different races put an else if with the race name here
			else
			{
				int iR1,iR2,iR3,iR4,iR5; //random integers for name selection
				int iFistSyllableCount; //random syllable generation max 5 min 2
				int iLastSyllableCount; //random syllable generation max 5 min 2
				int iRandomLastNameType;
				boolean firstApostrophe = false;
				iFistSyllableCount = SWGGui.getRandomInt(2,5);
				iLastSyllableCount = SWGGui.getRandomInt(2,5);
				iR1 = SWGGui.getRandomInt(Constants.cntNameSyllable1Count);
				if(iR1 > Constants.cntNameSyllable1Count) { iR1 = Constants.cntNameSyllable1Count;}
				iR2 = SWGGui.getRandomInt(Constants.cntNameSyllable2Count);
				if(iR2 > Constants.cntNameSyllable2Count) { iR2 = Constants.cntNameSyllable2Count;}
				iR3 = SWGGui.getRandomInt(Constants.cntNameSyllable3Count);
				if(iR3 > Constants.cntNameSyllable3Count) { iR3 = Constants.cntNameSyllable3Count;}
				iR4 = SWGGui.getRandomInt(Constants.cntNameSyllable2Count);
				if(iR4 > Constants.cntNameSyllable2Count) { iR4 = Constants.cntNameSyllable2Count;}
				iR5 = SWGGui.getRandomInt(Constants.cntNameSyllable3Count);
				if(iR5 > Constants.cntNameSyllable3Count) { iR5 = Constants.cntNameSyllable3Count;}

				//---------------------------------------------------------
				//first name
				randomFirstName.append(Constants.vNameSyllables1[iR1]);
				randomFirstName.append(Constants.vNameSyllables2[iR2]);
				if(iFistSyllableCount >= 3)
				{
					if(SWGGui.getRandomInt(0,9) == 5)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vNameSyllables3[iR3]);
				}
				if(iFistSyllableCount >= 4)
				{
					if(SWGGui.getRandomInt(0,9) == 5 && !firstApostrophe)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vNameSyllables2[iR2]);
				}
				if(iFistSyllableCount >= 5)
				{
					if(SWGGui.getRandomInt(0,9) == 5 && !firstApostrophe)
					{
						randomFirstName.append("'");
						firstApostrophe = true;
					}
					randomFirstName.append(Constants.vNameSyllables3[iR3]);
				}
				//---------------------------------------------------------
				//last name
				if(SWGGui.getRandomInt(0,20) != 0) // one in 20 chance that u dont get a last name even tho your species does have one.
				{
					randomFirstName.append(" ");
					iRandomLastNameType = SWGGui.getRandomInt(0,9);
					if(iRandomLastNameType == 5)
					{
						//random name based on last names table
						iR1 = SWGGui.getRandomInt(Constants.cntLastNames1Count);
						iR2 = SWGGui.getRandomInt(Constants.cntLastNames2Count);
						randomLastName.append(Constants.vLastNames1[iR1]);
						randomLastName.append(Constants.vLastNames2[iR2]);
					}
					else
					{
						//random last name based on syllable table
						boolean lastApostrophe = false;
						iLastSyllableCount = SWGGui.getRandomInt(5);
						iR1 = SWGGui.getRandomInt(Constants.cntNameSyllable1Count);
						if(iR1 > Constants.cntNameSyllable1Count) { iR1 = Constants.cntNameSyllable1Count;}
						iR2 = SWGGui.getRandomInt(Constants.cntNameSyllable2Count);
						if(iR2 > Constants.cntNameSyllable2Count) { iR2 = Constants.cntNameSyllable2Count;}
						iR3 = SWGGui.getRandomInt(Constants.cntNameSyllable3Count);
						if(iR3 > Constants.cntNameSyllable3Count) { iR3 = Constants.cntNameSyllable3Count;}
						iR4 = SWGGui.getRandomInt(Constants.cntNameSyllable2Count);
						if(iR4 > Constants.cntNameSyllable2Count) { iR4 = Constants.cntNameSyllable2Count;}
						iR5 = SWGGui.getRandomInt(Constants.cntNameSyllable3Count);
						if(iR5 > Constants.cntNameSyllable3Count) { iR5 = Constants.cntNameSyllable3Count;}

						randomLastName.append(Constants.vNameSyllables1[iR1]);
						randomLastName.append(Constants.vNameSyllables2[iR2]);
						if(iLastSyllableCount >= 3)
						{
							if(SWGGui.getRandomInt(0,9) == 5)
							{
								randomLastName.append("'");
								lastApostrophe = true;
							}
							randomLastName.append(Constants.vNameSyllables3[iR3]);
						}
						if(iLastSyllableCount >= 4)
						{
							if(SWGGui.getRandomInt(0,9) == 5 && !lastApostrophe)
							{
								randomLastName.append("'");
								lastApostrophe = true;
							}
							randomLastName.append(Constants.vNameSyllables2[iR2]);
						}
						if(iLastSyllableCount >= 5)
						{
							if(SWGGui.getRandomInt(0,9) == 5 && !lastApostrophe)
							{
								randomLastName.append("'");
								lastApostrophe = true;
							}
							randomLastName.append(Constants.vNameSyllables3[iR3]);
						}
					}
				}
			}
			boolean NameInUseByPlayer = true;
			boolean NameNotInUseByNPC = true;
			if(!this.isNameUsed(randomFirstName.toString()))
			{
				NameInUseByPlayer = false;
			}
			for(int i = 0; i < vUsedRandomNames.size();i++)
			{
				if(vUsedRandomNames.get(i).contains(randomFirstName.toString().replace(" ","")))
				{
					NameNotInUseByNPC = false;
				}
			}
			if(!NameInUseByPlayer && NameNotInUseByNPC)
			{
				proceed = true;
			}
			else
			{
				//System.out.println("P:" + NameInUseByPlayer + " N:" + NameNotInUseByNPC);
			}
		}
		String[] retval = new String[2];
		retval[0] = randomFirstName.toString();
		retval[1] = randomLastName.toString();
		vUsedRandomNames.add(randomFirstName.toString());
		return retval;
	}

	public Vector<RadialMenuItem> getRadialMenusByCRC(int crc){
		Vector<RadialMenuItem> R = new Vector<RadialMenuItem>();
		Enumeration<RadialMenuItem> MI = chmServerObjectRadials.elements();
		while(MI.hasMoreElements())
		{
			RadialMenuItem T = MI.nextElement();
			if(T.getiItemCRC() == crc)
			{
				R.add(T);
			}
		}
		return R;
	}

	protected boolean insertSkillTrainer(ZoneClient c, int TemplateID, String TrainerName, float x, float y, float z, float oI, float oJ, float oK, float oW,long _cellID,int _planetid){

		long NewTerminalID = this.getNextObjectID();
		boolean retval = dbInterface.insertSkillTrainer(NewTerminalID,TemplateID, TrainerName, x, y, z, oI, oJ, oK, oW, _cellID, _planetid);

		Vector<Terminal> V = dbInterface.LoadServerTerminals(this, NewTerminalID);

		this.spawnTerminal(V,false);
		this.addTerminalToServerTerminals(V);
		try{
			for(int i =0; i < V.size(); i++)
			{
				retval = ServerTerminals.add(V.get(i));
				Terminal T = V.get(i);
				c.getPlayer().spawnItem(T);
			}
		}catch(Exception e){
			DataLog.logEntry("Exception Caught while spawning a new terminal/Trainer "+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Exception Caught while spawning a new terminal/Trainer " + e);
			e.printStackTrace();
		}

		return retval;
	}

	protected boolean insertTerminal(ZoneClient c, int TemplateID, String TerminalName, float x, float y, float z, float oI, float oJ, float oK, float oW,long _cellID,int _planetid, int terminalType){

		long NewTerminalID = this.getNextObjectID();
		boolean retval = dbInterface.insertTerminal(NewTerminalID,TemplateID, TerminalName, x, y, z, oI, oJ, oK, oW, _cellID, _planetid, terminalType);

		Vector<Terminal> V = dbInterface.LoadServerTerminals(this, NewTerminalID);

		this.spawnTerminal(V,true);
		this.addTerminalToServerTerminals(V);
		try{
			for(int i =0; i < V.size(); i++)
			{
				retval = ServerTerminals.add(V.get(i));
				Terminal T = V.get(i);
				c.getPlayer().spawnItem(T);
			}
		}catch(Exception e){
			DataLog.logEntry("Exception Caught while spawning a new terminal/Terminal "+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Exception Caught while spawning a new terminal/Terminal " + e);
			e.printStackTrace();
		}

		return retval;
	}

	protected void spawnTerminal(Vector<Terminal> V, boolean debug){
		Hashtable<Long,Terminal> TL = new Hashtable<Long,Terminal>();

		if (!V.isEmpty()) {
			for (int i = 0; i < V.size(); i++) {
				Terminal t = V.elementAt(i);
				long objectID = t.getID();
				if (bIsObjectIDUsed(objectID)) {
					objectID = getNextObjectID();
					t.setID(objectID);
				}
				long cellID = t.getCellID();
				if (cellID != 0) {
					SOEObject o = getObjectFromAllObjects(cellID);
					if (o instanceof Cell) {
						Cell c = (Cell)o;
						//Structure building = (Structure)getObjectFromAllObjects(c.getBuildingID());
						c.addCellObject(t);
					} else {
					}
				}
				if(t.getTerminalType() == Constants.TERMINAL_TYPES_SKILL_TRAINER)
				{
					TL.put(t.getID(), t);                            
				}
				addObjectToAllObjects(t, true,false);
			}
		}
	}

	protected boolean addTerminalToServerTerminals(Vector<Terminal> V){
		boolean retval = false;
		for(int i =0; i < V.size(); i++)
		{
			retval = ServerTerminals.add(V.get(i));
		}
		return retval;
	}

	/**
	 * This Function will write an HTML File to a pre-determined directory for Galaxy Status.
	 */
	protected static void writeGalaxyStatusFile(String sSource,boolean bGetDevServers){
		//public File(String pathname)
		//System.out.println("Updating Galaxy Status : S: " + sSource);
		File GalaxyStatus = new File(SWGGui.getWebPath() + "/GalaxyClusterStatus.html");
		File GalaxyStatusScript = new File(SWGGui.getWebPath() + "/Status.js");
		GalaxyStatus.delete();
		try{
			GalaxyStatus.createNewFile();
			GalaxyStatusScript.createNewFile();
			FileWriter W = new FileWriter(GalaxyStatus);
			FileWriter SSCR = new FileWriter(GalaxyStatusScript);
			Vector<DatabaseServerInfoContainer> vZSList =  DatabaseInterface.getZoneServers(bGetDevServers);

			String StatusData = "";
			String StatusScript = "";

			//Java Script Goes Here
			StatusData += "<html>\r\n";
			StatusData += "<head>\r\n";
			StatusData += "<title>Shards of the Force SWG Galaxy Cluster Status</title>\r\n";
			StatusData += "<noscript>\r\n";
			StatusData += "<meta http-equiv=\"Refresh\" content=\"60\">\r\n";
			StatusData += "</noscript>\r\n";
			StatusData += "<script language=\"JavaScript\" src = \"Status.js\">\r\n";
			StatusScript += "<!--\r\n";
			StatusScript += "//(C) 2008 Shards of the Force http://www.shardsoftheforce.co.cc\r\n";
			StatusScript += "//Server Status Java Script\r\n";
			StatusScript += "//WARNING THIS IS LICENSED SOFTWARE\r\n";
			StatusScript += "//Define Variables\r\n";
			StatusScript += "var sURL = unescape(window.location.pathname);\r\n";
			StatusScript += "var LastUpdateTime = " + System.currentTimeMillis() + ";\r\n";
			StatusScript += "var ClusterName = new Array();\r\n";
			StatusScript += "var ClusterStatus = new Array();\r\n";
			StatusScript += "var ClusterPlayerCount = new Array();\r\n";
			StatusScript += "var SS = \"Off Line\";\r\n";
			StatusScript += "var date = new Date();\r\n";
			StatusScript += "var maxupdatetime = (((1000) * 60) * 5);\r\n";
			StatusScript += "var comptime = 0;\r\n";
			StatusScript += "var cDT\r\n";
			StatusScript += "var PageText = \"\";\r\n";
			StatusScript += "//Init Variables\r\n";

			for(int i = 0; i < vZSList.size(); i++)
			{
				DatabaseServerInfoContainer S = vZSList.get(i);
				StatusScript += "ClusterName[" + i + "] = \"" + S.sServerName + "\";\r\n";
				StatusScript += "ClusterStatus[" + i + "] = " + S.iServerStatus + ";\r\n";
				StatusScript += "ClusterPlayerCount[" + i + "] = " + S.iCurrentPopulation + ";\r\n";
			}
			StatusScript += "var ClusterGalaxyCount = ClusterName.length;\r\n";
			StatusScript += "//init Complete\r\n";
			StatusScript += "//define Functions\r\n";
			StatusScript += "function refresh()\r\n";
			StatusScript += "{\r\n";
			StatusScript += "     window.location.href = sURL;\r\n";
			StatusScript += "}\r\n";
			StatusScript += "\r\n";
			StatusScript += "function runStatus(){\r\n";
			StatusScript += "\r\n";
			//document header
			StatusScript += "             document.writeln(\"<style type=\\\"text/css\\\">\");\r\n";
			StatusScript += "             document.writeln(\".style1 {\");\r\n";
			StatusScript += "             document.writeln(\"text-align: left;\");\r\n";
			StatusScript += "             document.writeln(\"}\");\r\n";
			StatusScript += "             document.writeln(\".style2 {\");\r\n";
			StatusScript += "             document.writeln(\"text-align: center;\");\r\n";
			StatusScript += "             document.writeln(\"font-family: Freshbot;\");\r\n";
			StatusScript += "             document.writeln(\"font-size: large;\");\r\n";
			StatusScript += "             document.writeln(\"}\");\r\n";
			StatusScript += "             document.writeln(\".style3 {\");\r\n";
			StatusScript += "             document.writeln(\"text-align: center;\");\r\n";
			StatusScript += "             document.writeln(\"}\");\r\n";
			StatusScript += "             document.writeln(\"</style>\");\r\n";

			StatusScript += "             document.writeln(\"<table style=\\\"width: 300px; float: center\\\">\");\r\n";
			StatusScript += "             document.writeln(\"<tr>\");\r\n";
			StatusScript += "             document.writeln(\"<td class=\\\"style1\\\">\");\r\n";
			StatusScript += "             document.writeln(\"<table style=\\\"width: 100%\\\">\");\r\n";
			//StatusScript += "             document.writeln(\"<tr>\");\r\n";
			//StatusScript += "             document.writeln(\"<td class=\\\"style2\\\">Shards of the Force SWG Cluster Status</td>\");\r\n";
			//StatusScript += "             document.writeln(\"</tr>\");\r\n";
			StatusScript += "             document.writeln(\"<tr>\");\r\n";
			StatusScript += "             document.writeln(\"<td>\");\r\n";
			StatusScript += "             document.writeln(\"<table style=\\\"width: 100%\\\">\");\r\n";
			StatusScript += "             document.writeln(\"<tr>\");\r\n";
			StatusScript += "             document.writeln(\"<td style=\\\"width: 193px\\\" class=\\\"style3\\\">Galaxy Name</td>\");\r\n";
			StatusScript += "             document.writeln(\"<td class=\\\"style3\\\">Status</td>\");\r\n";
			StatusScript += "             document.writeln(\"</tr>\");\r\n";
			//end doc header
			//begin status data
			StatusScript += "         for(i = 0; i < ClusterGalaxyCount; i++ )\r\n";
			StatusScript += "         {\r\n";
			StatusScript += "             \r\n";
			StatusScript += "             comptime = maxupdatetime + LastUpdateTime;\r\n";
			StatusScript += "             if(comptime < date.getTime()){\r\n";
			StatusScript += "                 ClusterStatus[i] = 0; \r\n";
			StatusScript += "                 \r\n";
			StatusScript += "             }\r\n";
			StatusScript += "             switch(ClusterStatus[i])\r\n";
			StatusScript += "             {\r\n";
			StatusScript += "                 case 0:\r\n";
			StatusScript += "                 {\r\n";
			StatusScript += "                     SS = \"Off Line\";\r\n";
			StatusScript += "                     break;\r\n";
			StatusScript += "                 }\r\n";
			StatusScript += "                 case 1:\r\n";
			StatusScript += "                 {\r\n";
			StatusScript += "                     SS = \"Loading\";\r\n";
			StatusScript += "                     break;\r\n";
			StatusScript += "                 }\r\n";
			StatusScript += "                 case 2:\r\n";
			StatusScript += "                 {\r\n";
			StatusScript += "                     SS = \"On Line(\" + ClusterPlayerCount[i] + \")\"; \r\n";
			StatusScript += "                     break;\r\n";
			StatusScript += "                 }\r\n";
			StatusScript += "                 default:\r\n";
			StatusScript += "                 {\r\n";
			StatusScript += "                     SS = \"Unknown\"; \r\n";
			StatusScript += "                 }\r\n";
			StatusScript += "             }\r\n";
			StatusScript += "             document.writeln(\"<tr> <td style=\\\"width: 193px\\\" class=\\\"style3\\\">\");\r\n";
			StatusScript += "             document.writeln(ClusterName[i]);\r\n";
			StatusScript += "             document.writeln(\"</td><td class=\\\"style3\\\">\");\r\n";
            StatusScript += "             switch(ClusterStatus[i])\r\n";
			StatusScript += "             {\r\n";
			StatusScript += "                 case 0:\r\n";
			StatusScript += "                 {\r\n";
			StatusScript += "                     document.writeln(\"<font color=\\\"Red\\\">\");\r\n";
			StatusScript += "                     break;\r\n";
			StatusScript += "                 }\r\n";
			StatusScript += "                 case 1:\r\n";
			StatusScript += "                 {\r\n";
			StatusScript += "                     document.writeln(\"<font color=\\\"Blue\\\">\");\r\n";
			StatusScript += "                     break;\r\n";
			StatusScript += "                 }\r\n";
			StatusScript += "                 case 2:\r\n";
			StatusScript += "                 {\r\n";
			StatusScript += "                     document.writeln(\"<font color=\\\"Green\\\">\");\r\n";
			StatusScript += "                     break;\r\n";
			StatusScript += "                 }\r\n";
			StatusScript += "                 default:\r\n";
			StatusScript += "                 {\r\n";
			StatusScript += "                     document.writeln(\"<font color=\\\"Orange\\\">\");\r\n";
			StatusScript += "                 }\r\n";
			StatusScript += "             }\r\n";

			StatusScript += "             document.writeln(SS);\r\n";

            StatusScript += "             document.writeln(\"</font>\");\r\n";

            StatusScript += "             document.writeln(\"</td></tr>\");\r\n";
			StatusScript += "             \r\n";
			StatusScript += "         }\r\n";
			//status footer
			StatusScript += "             document.writeln(\"</table>\");\r\n";
			StatusScript += "             document.writeln(\"</td>\");\r\n";
			StatusScript += "             document.writeln(\"</tr>\");\r\n";
			StatusScript += "             document.writeln(\"</table>\");\r\n";
			StatusScript += "             document.writeln(\"</td>\");\r\n";
			StatusScript += "             document.writeln(\"</tr>\");\r\n";
			StatusScript += "             document.writeln(\"</table>\");\r\n";
			//StatusScript += "             document.writeln(cDT);\r\n";

			//StatusScript += "             document.writeln(\"</body>\");\r\n";
			//StatusScript += "             document.writeln(\"</html>\");\r\n";

			StatusScript += "             cDT = setTimeout(\"refresh()\", 60*1000);\r\n";
			//end status footer
			StatusScript += "}\r\n";
			StatusScript += "//-->\r\n";
			//StatusScript += "</script>\r\n";
			//StatusScript += "<script language=\"JavaScript1.1\">\r\n";
			//StatusScript += "<!--\r\n";
			//StatusScript += "function refresh()\r\n";
			//StatusScript += "{\r\n";
			//StatusScript += "      window.location.replace( sURL );\r\n";
			//StatusScript += "}\r\n";
			//StatusScript += "//-->\r\n";
			//StatusScript += "</script>\r\n";
			//StatusScript += "\r\n";
			//StatusScript += "\r\n";
			//StatusScript += "<script language=\"JavaScript1.2\">\r\n";
			//StatusScript += "<!--\r\n";
			//StatusScript += "function refresh()\r\n";
			//StatusScript += "{\r\n";
			//StatusScript += "  window.location.reload( false );\r\n";
			//StatusScript += "}\r\n";
			StatusData += "//-->\r\n";
			StatusData += "</script>\r\n";
			StatusData += "\r\n";
			StatusData += "</head>\r\n";
			StatusData += "<body> \r\n";
			StatusData += "<script language=\"JavaScript\">\r\n";
			StatusData += "<!--\r\n";
			StatusData += "        runStatus()\r\n";
			StatusData += "//-->\r\n";
			StatusData += "</script>\r\n";
			StatusData += "\r\n";
			StatusData += "</body>\r\n";
			StatusData += "</html>\r\n";

			W.write(StatusData);
			W.close();
			SSCR.write(StatusScript);
			SSCR.close();
		}catch(Exception e){
			DataLog.logEntry("Exception while Writing Web GalaxyStatus file "+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Exception while Writing Web GalaxyStatus file " + e);
			e.printStackTrace();
		}


	}

	protected void writeGalaxyPlayerPositions(){
		//System.out.println("Updating Galaxy Player");
		try{
			//File GalaxyPlayerPositions = new File(SWGGui.getWebPath() + "GalaxyPlayerPositions.csv");
			//String sPlayerPositionData = "";
            Vector<Player> vPLOut = new Vector<Player>();
			for(int i = 0; i < 10; i++)
			{
				Vector<Player> vPL = this.getStatusPlayersOnPlanet(i,true);
				if(vPL!=null)
				{
					//for(int p = 0; p < vPL.size();p++)
					//{
					//	Player T = vPL.get(p);
					//	sPlayerPositionData += T.getPlanetID() + "," + T.getX() + "," + T.getY() + "," + T.getFullName() + "\r\n";
					//}
                    vPLOut.addAll(vPL);
				}
			}
			//GalaxyPlayerPositions.delete();
			//GalaxyPlayerPositions.createNewFile();
			//FileWriter W = new FileWriter(GalaxyPlayerPositions);
			//W.write(sPlayerPositionData);
			//W.close();
            DatabaseInterface.updateInstrumentationPlayers(vPLOut);

		}catch(Exception e){
			DataLog.logEntry("Exception while Writing Player Positions "+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Exception while Writing Player Positions file " + e);
			e.printStackTrace();
		}
	}   


	protected Vector<String> getTesterPunchList(){
		return dbInterface.getTesterPunchList();
	}

	protected boolean insertPunchListItem(int iPriority, String sItem){
		return dbInterface.insertPunchListItem(iPriority, sItem);
	}

	protected boolean removePunchListItem(int iItem){
		return dbInterface.removePunchListItem(iItem);
	}

	protected boolean insertBugReport(long playerid, int accountid, String sReport){
		return dbInterface.insertBugReport(playerid, accountid, sReport);
	}

	/**
	 * This function returns a Vector of lair templates by planet id.
	 * If the boolean is true it only returns lairs for the specified planet.
	 * If the boolean is false we return all lairs for the planet specified and for 
	 * all lairs that have a planet id tag of -1 meaning all planet available.
	 * Lairs with a Planet Tag of -2 ar for planets where ther is water only and
	 * are experimental, these are fish lairs.
	 * Lairs with a Planet ID of 255 are experimental or dev only access.
	 * @param iPlanetID
	 * @param bReturnSpecific
	 * @return
	 */        
	protected Vector<LairTemplate> getLairTemplatesForPlanet(int iPlanetID, boolean bReturnSpecific){
		Vector<LairTemplate> retval = new Vector<LairTemplate>();
		if(bReturnSpecific)
		{
			for(int i = 0; i < vServerLairTemplates.size(); i++)
			{
				LairTemplate T = new LairTemplate();
				T = vServerLairTemplates.get(i);
				int[] iPlanets = T.getSAllowedPlanetList();
				boolean bFound = false;
				for(int p = 0; p < iPlanets.length && !bFound; p++)
				{
					//System.out.println("S " + p + " :" + sP[p]);
					if(iPlanets[p] == iPlanetID && T.getIMob1Template() != 0)
					{
						bFound = true;
						retval.add(T);
						//p += T.getSAllowedPlanetList().length + 2;
					}
				}                    
			}      
			return retval;
		}
		else
		{
			for(int i = 0; i < vServerLairTemplates.size(); i++)
			{
				LairTemplate T = new LairTemplate();
				T = vServerLairTemplates.get(i);
				int[] iPlanets = T.getSAllowedPlanetList();
				boolean bFound = false;
				for(int p = 0; p < iPlanets.length && !bFound; p++)
				{
					//System.out.println("S " + p + " :" + sP[p]);
					if((iPlanets[p] == iPlanetID || iPlanets[p] == -1) && T.getIMob1Template() != 0)
					{
						bFound = true;
						retval.add(T);
					}
				}                    
			}      
			return retval;
		}                    
	}

	/**
	 * This function returns a Vector of lair templates by planet id.
	 * If the boolean is true it only returns lairs for the specified planet.
	 * If the boolean is false we return all lairs for the planet specified and for 
	 * all lairs that have a planet id tag of -1 meaning all planet available.
	 * Lairs with a Planet Tag of -2 ar for planets where ther is water only and
	 * are experimental, these are fish lairs.
	 * Lairs with a Planet ID of 255 are experimental or dev only access.
	 * @param templateData -- The vector to place the LairTemplates into.
	 * @param iPlanetID -- The planet ID which we want templates for.
	 * @param bReturnSpecific -- Indicates whether to return only lairs which spawn specifically on this planet and nowhere else.
	 * @return
	 */        
	protected Vector<LairTemplate> getLairTemplatesForPlanet(Vector<LairTemplate> templateData, int iPlanetID, boolean bReturnSpecific){
		if (templateData == null) {
			templateData = new Vector<LairTemplate>();
		}
		if(bReturnSpecific)
		{
			for(int i = 0; i < vServerLairTemplates.size(); i++)
			{
				LairTemplate T = new LairTemplate();
				T = vServerLairTemplates.get(i);
				int[] iPlanets = T.getSAllowedPlanetList();
				boolean bFound = false;
				for(int p = 0; p < iPlanets.length && !bFound; p++)
				{
					//System.out.println("S " + p + " :" + sP[p]);
					if(iPlanets[p] == iPlanetID && T.getIMob1Template() != 0)
					{
						bFound = true;
						templateData.add(T);
						//p += T.getSAllowedPlanetList().length + 2;
					}
				}                    
			}      
			return templateData;
		}
		else
		{
			for(int i = 0; i < vServerLairTemplates.size(); i++)
			{
				LairTemplate T = new LairTemplate();
				T = vServerLairTemplates.get(i);
				int[] iPlanets = T.getSAllowedPlanetList();
				boolean bFound = false;
				for(int p = 0; p < iPlanets.length && !bFound; p++)
				{
					//System.out.println("S " + p + " :" + sP[p]);
					if((iPlanets[p] == iPlanetID || iPlanets[p] == -1) && T.getIMob1Template() != 0)
					{
						bFound = true;
						templateData.add(T);
					}
				}                    
			}      
			return templateData;
		}                    
	}


	/**
	 * Returns a complete list of all lairs in the server vServerLairTemplates vector.
	 * @return
	 */
	protected Vector<LairTemplate> getAllLairTemplates(){
		return vServerLairTemplates;
	}

	protected LairTemplate getLairTemplate(int iLairTemplateID){

		for(int i = 0; i < vServerLairTemplates.size(); i++)
		{
			LairTemplate T = vServerLairTemplates.get(i);
			//System.out.println("Iterating Lair Template " + T.getSLlairName() + " TID " + T.getILairTemplate() + " REQ " + iLairTemplateID);
			if(T.getILairTemplate() == iLairTemplateID)
			{                    
				//System.out.println("Returning Lair Template " + T.getSLlairName());
				return T;
			}
		}
		//System.out.println("Returning Null Lair Template");
		return null;
	}

	protected void addUsedName(String sName) {
		vUsedCharacterNames.add(sName);
	}

	protected void removeUsedName(String sName) {
		vUsedCharacterNames.remove(sName);
	}

	protected Vector<String> getUsedCharacterNames() {
		return vUsedCharacterNames;
	}

	/**
	 * TEMPORARY INSERT HEIGHT TO DB ROUTINE DEV FUNCTION ONLY
	 * 
	 */
	protected void insertHeightAtLocation(float x, float y, short z, int planetid){
		dbInterface.insertHeightAtLocation(x, y, z, planetid);
	}



	protected short[][][]getHeightMap(){
		return planetaryHeightMap;
	}

	protected void setHeightAtCoordinates(int planetid, short x, short y, short z){
		planetaryHeightMap[planetid][x][y] = z;
	}

	protected void updateHeightMapZ(short x, short y, short z, short planetid){
		dbInterface.updateHeigtAtLocation(x, y, z, planetid);
	}

	protected void activateScreen() {
		if (theScreen == null) {
			theScreen = new ZoneServerScreen(this);
		}
		theScreen.initialize();
		synchronized(theScreen) {
			theScreen.notifyAll();
		}
	}

	/**
	 * THIS IS WORK IN PROGRESS AND NOT TESTED JUST AN IDEA FOR WHEN DUNGEONS ARE DONE
	 * This function sets the height at a cell id within a dungeon.
	 * This is only for dungeons and cells spawned as world objects 
	 * and within the world object cell id ranges of 0 to 19,999,999
	 * @param o
	 * @return
	 */
	protected boolean setObjectHeightAndCoordinatesWithParent(SOEObject o){
		boolean retval = false;
		try{
			//cellHeightMap
			/**
			 * Get the object Coordinates
			 */
			float x,y,z;
			x = o.getX();
			y = o.getY();
			z = 32767;
			int planetid = o.getPlanetID();
			//should be big enough for all world cell ids. 
			//Int Max:2,147,483,647 World CellID Max: 19,999,999
			int cellid = (int)o.getCellID();
			/**
			 * Figure out the closest coordinate that we have height for.
			 */
			byte sX,sY;
			sX = (byte)((127 - (x / 60.472441)));
			sY = (byte)((127 - (y / 60.472441)));
			
			/**
			 * Get the height of the closest coordinates
			 */
			// ArrayIndexOutOfBoundsException
			try {
				z = cellHeightMap[planetid][cellid][sX][sY];
			} catch (ArrayIndexOutOfBoundsException e) {
				z = 0.0f;
			}
			/**
			 * coord XY
			 */
			float coordX, coordY;
			coordX = (float)(7680 - (sX * 60.472441));
			coordY = (float)(7680 - (sY * 60.472441));
			/**
			 * If we get a bad coordinate we have to set it to 0 unfortunately.
			 */
			if(z == 32767)
			{
				z = 0;                        
				DataLogObject L = new DataLogObject("ZoneServer.setObjectHeightAndCoordinatesWithParent","Bad Height Detected at indexes cellHeightMap[" + planetid + "][" + cellid + "][" + sX + "][" + sY + "] = 32767",Constants.LOG_SEVERITY_CRITICAL );
				DataLog.logEntry(L);
			}
			/**
			 * Set the object coordinates to that of the height point.
			 * And return true.
			 */
			o.setX(coordX);
			o.setY(coordY);
			o.setZ(z);            
			retval = true;

		}catch(Exception e){
			DataLog.logEntry("Exception Caught in ZoneServer.setObjectHeightAndCoordinatesWithParent "+ e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			//System.out.println("Exception Caught in ZoneServer.setObjectHeightAndCoordinatesWithParent " + e);
			e.printStackTrace();
		}
		return retval;
	}
	protected boolean setObjectHeightAndCoordinates(SOEObject o){
		boolean retval = false;
		try{
			/**
			 * Get the object Coordinates
			 */
			float x,y,z;
			x = o.getX();
			y = o.getY();
			z = 32767;
			int planetid = o.getPlanetID();
			/**
			 * Figure out the closest coordinate that we have height for.
			 */
			short sX,sY;
			sX = (short)((127 - (x / 60.472441)));
			sY = (short)((127 - (y / 60.472441)));
			/**
			 * coord XY
			 */
			float coordX, coordY;
			coordX = (float)(7680 - (sX * 60.472441));
			coordY = (float)(7680 - (sY * 60.472441));
			/**
			 * Get the height of the closest coordinates
			 */
			try {
				z = planetaryHeightMap[planetid][sX][sY];
			} catch (ArrayIndexOutOfBoundsException e) {
				z = 0;
			}

			/**
			 * If we get a bad coordinate we have to set it to 0 unfortunately.
			 */
			if(z == 32767)
			{
				z = 0;
				DataLog.logEntry("Bad Height Detected at indexes planetaryHeightMap[" + planetid + "][" + sX + "][" + sY + "] = 32767","ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
				//DataLogObject L = new DataLogObject("ZoneServer.setObjectHeightAndCoordinates","Bad Height Detected at indexes planetaryHeightMap[" + planetid + "][" + sX + "][" + sY + "] = 32767",Constants.LOG_SEVERITY_CRITICAL );
				//DataLog.logEntry(L);
			}
			/**
			 * Set the object coordinates to that of the height point.
			 * And return true.
			 */
			o.setX(coordX);
			o.setY(coordY);
			o.setZ(z);            
			retval = true;
		}catch(Exception e){
			DataLog.logEntry("Exception Caught in ZoneServer.setObjectHeightAndCoordinates " + e,"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			System.out.println("Exception Caught in ZoneServer.setObjectHeightAndCoordinates " + e);
			e.printStackTrace();
		}
		return retval;
	}

	protected float getHeightAtCoordinates(float x, float y, int planetid){
		float retval = 0;
		try{
			boolean bUnforseen = false;
			/** 
			 * This is temporary more dazzeled math is required here.. For now it just gets you the closest cell height.
			 */

			/**
			 * Plane Coordinates
			 */
			//float pxS,pxN,pyS,pyN,pxE,pxW,pyE,pyW;
			//pxS = x;
			//pxN = x;
			//pyS = y - 60.472441f; 
			//pyN = y + 60.472441f; 
			//pxE = x + 60.472441f; 
			//pxW = x - 60.472441f; 
			//pyE = y;
			//pyW = y;
			/*
                System.out.println("-------------------Plane----------------");
                System.out.println("North Point XY:  X:" + pxN + " Y:" + pyN );
                System.out.println("West Point XY:   X:" + pxW + " Y:" + pyW);
                System.out.println("      Player XY: X:" + x +   " Y:" + y );
                System.out.println("East Point XY:   X:" + pxE + " Y:" + pyE);
                System.out.println("South Point XY:  X:" + pxS + " Y:" + pyS);
                System.out.println("-------------------Plane----------------");
			 * */
			/**
			 * this is the math to get the index to find the nearest point in the array to the coordinates sought
			 */
			short sX,sY;
			sX = (short)((127 - (x / 60.472441)));
			sY = (short)((127 - (y / 60.472441)));
			/*----------------------------------------------------------------------------*/
			/**
			 * center XY
			 */
			float centerX, centerY;
			centerX = (float)(7680 - (sX * 60.472441));
			centerY = (float)(7680 - (sY * 60.472441));
			//System.out.println("Center XY: X:" + centerX + " Y:" + centerY);

			SOEObject centr = new Waypoint();
			SOEObject t = new Waypoint();
			centr.setX(centerX);
			centr.setY(centerY);
			t.setX(x);
			t.setY(y);
			float distance = getRangeBetweenObjects(centr, t);
			distance = distance * 2;                
			//System.out.println("Distance Center to Target " + distance);

			//float angleCenterToTarget = centr.absoluteBearingDegrees(t,centr);

			// System.out.println("Absolute Angle Center to Target " + angleCenterToTarget + " degrees");
			//float radiansCenterToTarget = centr.absoluteBearingRadians(t,centr);
			// System.out.println("Absolute Angle Center to Target " + radiansCenterToTarget + " Radians");



			/*-------------------------------------------------------------------------------------------*/
			//float c,
			float n, s, e, w, nE, nW, sE, sW;                
			/**
			 * Logically the coords would be like this
			 * nW   N   nE
			 * w    c   e
			 * sW   s   sE
			 */
			// System.out.println("Debug@HeightMap: planetid" + planetid + " sX:" + sX + " sY:" + sY);
			//c = planetaryHeightMap[planetid][sX][sY];                               
			nW = -1;    n = -1;     nE = -1;
			w = -1;     /*c*/       e = -1;
			sW = -1;    s = -1;     sE = -1;



			/**
			 * Variables for the angles
			 */
			 //double cTOn, cTOs, cTOe, cTOw,cTOnE,cTOsE,cTOnW,
			//double cTOsW;//,diff;
			//cTOn = 0; cTOs = 0; cTOe = 0; cTOw = 0;cTOnE = 0;cTOsE = 0;cTOnW = 0;
			//cTOsW = 0;
			//-------------------------------------------------------------------------------
			//int zeroCount = 0;
			int nonZeroCount = 0;

			if(sX == 0 && sY == 0)
			{            
				//System.out.println("if(sX == 0 && sY == 0)");
				//since sx == 0 and sY == 0 it meanst we have no n,nE,e,sE,nW Coordinate heights were at the corner top right
				n = 0;
				s = planetaryHeightMap[planetid][sX][sY+1];
				e = 0;
				w = planetaryHeightMap[planetid][sX+1][sY];
				nE = 0;
				sE = 0;
				nW = 0;                    
				sW = planetaryHeightMap[planetid][sX+1][sY+1];
				//zeroCount = 5;
				nonZeroCount = 3;
				//cTOs =  (Math.atan((s-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOw =  (Math.atan((w-(c))/60.472441 ) * (180 / Math.PI));                                        
				//cTOsW = (Math.atan((sW-(c))/60.472441 ) * (180 / Math.PI));                    
			}
			else if(sX == 0 && sY >=1 && sY <= 253)
			{
				//System.out.println("else if(sX == 0 && sY >=1 && sY <= 253)");
				//since sx == 0 and sy == 1 or greater we have no e,ne,se
				n = planetaryHeightMap[planetid][sX][sY-1];
				s = planetaryHeightMap[planetid][sX][sY+1];
				e = 0;
				w = planetaryHeightMap[planetid][sX+1][sY];
				nE = 0;
				sE = 0;
				nW = planetaryHeightMap[planetid][sX+1][sY-1];                    
				sW = planetaryHeightMap[planetid][sX+1][sY+1];
				//zeroCount = 3;
				nonZeroCount = 5;                    
				//cTOn = (Math.atan((n-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOs = (Math.atan((s-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOw = (Math.atan((w-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOnW = (Math.atan((nW-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOsW = (Math.atan((sW-(c))/60.472441 ) * (180 / Math.PI));
			}
			else if(sX >= 1 && sX <= 253 && sY >=1 && sY <= 253)
			{             
				// System.out.println("else if(sX >= 1 && sX <= 253 && sY >=1 && sY <= 253)");
				n = planetaryHeightMap[planetid][sX][sY-1];
				s = planetaryHeightMap[planetid][sX][sY+1];
				e = planetaryHeightMap[planetid][sX-1][sY];
				w = planetaryHeightMap[planetid][sX+1][sY];
				nE = planetaryHeightMap[planetid][sX-1][sY-1];
				sE = planetaryHeightMap[planetid][sX-1][sY+1];
				nW = planetaryHeightMap[planetid][sX+1][sY-1];                    
				sW = planetaryHeightMap[planetid][sX+1][sY+1];
				//zeroCount = 0;
				nonZeroCount = 8;                    
				//cTOn =  (Math.atan((n-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOs =  (Math.atan((s-(c))/60.472441 ) * (180 / Math.PI));
				//cTOe =  (Math.atan((e-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOw =  (Math.atan((w-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOnE = (Math.atan((nE-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOsE = (Math.atan((sE-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOnW = (Math.atan((nW-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOsW = (Math.atan((sW-(c))/60.472441 ) * (180 / Math.PI));
			}
			else if(sX == 254 && sY == 0)
			{
				//System.out.println("else if(sX == 254 && sY == 0)");
				//were at top left corner we dont have north west northwest or southwest or northeast
				n = 0;
				s = planetaryHeightMap[planetid][sX][sY+1];
				e = planetaryHeightMap[planetid][sX-1][sY];
				w = 0;
				nE = 0;
				sE = planetaryHeightMap[planetid][sX-1][sY+1];
				nW = 0;
				sW = 0;
				//zeroCount = 5;
				nonZeroCount = 3;                    
				//cTOs =  (Math.atan((s-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOe =  (Math.atan((e-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOsE = (Math.atan((sE-(c))/60.472441 ) * (180 / Math.PI));
			}
			else if(sX == 254 && sY == 254)
			{
				//System.out.println("else if(sX == 254 && sY == 254)");
				//were at bottom left corner we dont have south,west,southeast,northwest,southwest
				n = planetaryHeightMap[planetid][sX][sY-1];
				s = 0;
				e = planetaryHeightMap[planetid][sX-1][sY];
				w = 0;
				nE = planetaryHeightMap[planetid][sX-1][sY-1];
				sE = 0;
				nW = 0;
				sW = 0;
				//zeroCount = 5;
				nonZeroCount = 3;                    
				//cTOn = (Math.atan((n-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOe =  (Math.atan((e-(c))/60.472441 ) * (180 / Math.PI));                    
				//cTOnE =  (Math.atan((nE-(c))/60.472441 ) * (180 / Math.PI));
			}
			else
			{
				bUnforseen = true;
				DataLog.logEntry("Unforeseen x y combination: sX: " + sX + " sY: " + sY,"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
				//System.out.println("unforseen x y combination: sX: " + sX + " sY: " + sY);
			}

			float Average = n + s + e + w + nE + nW + sE + sW;
			Average = Average / nonZeroCount;
			float highest = -2000;
			float [] heights = new float [8];
			heights[0] = n;
			heights[1] = s;
			heights[2] = e;
			heights[3] = w;
			heights[4] = nE;
			heights[5] = nW;
			heights[6] = sE;
			heights[7] = sW;
			for(int i = 0; i < 8; i++)
			{
				if(heights[i] > highest && heights[i] != 0)
				{
					highest = heights[i];
				}
			}
			// System.out.println("Highest Coord Val is: " + highest);
			//System.out.println("Average Height was: " + Average);
			// System.out.println("Height at Indexes was: " + planetaryHeightMap[planetid][sX][sY]);
			//float Actual = planetaryHeightMap[planetid][sX][sY];
			/*
                System.out.println("X : " + sX + " Y: " + sY);
                System.out.println("  -------HeightMap-------");
                System.out.println("  nW-------n-------nE");
                System.out.println(nW + " - " + n + " - " + nE );
                System.out.println("  w--------c--------e");
                System.out.println(w + " - " + c + " - " + e );
                System.out.println("  sW-------s-------sE");
                System.out.println(sW + " - " + c + " - " + sE );
                System.out.println("  -------HeightMap-------");
			 */
			/**
			 * Angles between Heights
			 */
			/*
                System.out.println("Center to North Angle:" + (int)(cTOn));                
                System.out.println("Center to South Angle:" + (int)(cTOs));                
                System.out.println("Center to East Angle:" + (int)(cTOe));                
                System.out.println("Center to West Angle:" + (int)(cTOw));                
                System.out.println("Center to North East Angle:" + (int)(cTOnE));                
                System.out.println("Center to South East Angle:" + (int)(cTOsE));                
                System.out.println("Center to North West Angle:" + (int)(cTOnW));                
                System.out.println("Center to South West Angle:" + (int)(cTOsW));               
			 */
			//double distDiff = 60.472441 - distance;
			//double sWTOc = (90 - cTOsW);
			//double heightAtIntersection = Math.tan(sWTOc) * distDiff;
			//System.out.println("Height At Intersection Should be: " + ((sW + heightAtIntersection) + (sW + heightAtIntersection) * .08955) );
			/*--------------------------------------------------------------------------*/

			//Average = ((Average + Actual) / 2);
			if(highest > Average)
			{
				Average = highest + Average / 2;
				//System.out.println("Returning highest Average.");
			}

			//Average = (float)(((sW + heightAtIntersection) + (sW + heightAtIntersection) * .08955));
			//return center for now
			///Average = c;

			if((7680 - (sX * 60.472441)) == x && (7680 - (sY * 60.472441)) == y)
			{                
				//we are at the exact coordinates for the height map lets give them that.
				retval = planetaryHeightMap[planetid][sX][sY];
			}
			else
			{
				retval = Average;
			}                
			if(retval == 32767 || retval == -1000)
			{
				retval = 0;
			}
			if (bUnforseen) {
				System.out.println("getHeightAtCoodinates Returning Height of :" + retval + " for Index: X:" + sX + " Y:" + sY + " Float X: " + x + " Y: " + y);
			}
		}catch(Exception e){
			DataLog.logEntry("Exception caught in ZoneServer.getHeightAtCoodinates " + e,"ZoneServer()", Constants.LOG_SEVERITY_CRITICAL, ZoneRunOptions.bLogToConsole, true);
			System.out.println("Exception caught in ZoneServer.getHeightAtCoodinates " + e);
			e.printStackTrace();
		}
		return retval;
	}

	protected Vector<MissionTemplate> getMissionTemplates(int iTerminalType, int iPlanetID){
		return dbInterface.getMissionTemplates(iTerminalType, iPlanetID);
	}

	protected MissionCollateral getMissionCollateral(int missionid,int collateralid){
		return dbInterface.getMissionCollateral(missionid, collateralid);
	}
	protected Vector<MissionCollateral> getMissionCollateralVector(int missionid,int planetid){
		return dbInterface.getMissionCollateralVector(missionid, planetid);
	}      

	protected void updateWorldObjectPlanet(long worldObjectID, int planetID){
		dbInterface.updateWorldObjectPlanet(worldObjectID, planetID);
	}

	protected boolean checkObjectBypassNoBuildZone(SOEObject o){
		return vNoBuildZoneBypassObjects.contains(o);
	}

	protected void addDynamicLairSpawn(DynamicLairSpawn spawn) {
		int planetID = spawn.getPlanetID();
		DataLog.logEntry("Add dynamic spawn -- planet ID " + planetID,"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		//System.out.println("Add dynamic spawn -- planet ID " + planetID);
		vNPCUpdateThreads.get(planetID).addDynamicLairSpawn(spawn);
		vLairSpawnsSortedByPlanet[planetID].add(spawn);
	}

	protected Vector<DynamicLairSpawn> getLairSpawnForPlanet(int planetID) {
		return vLairSpawnsSortedByPlanet[planetID];
	}

	protected void addPlayerStructureToAllStructures(Structure s){
		vPlayerStructures.put(s.getID(), s);
		DataLog.logEntry("Player Structure Added, Total At: " + vPlayerStructures.size(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
		//            System.out.println("Player Structure Added, Total At: " + vPlayerStructures.size() );

	}

	protected Structure getPlayerStructureFromAllStructures(long buildingID){
		Structure s = vPlayerStructures.get(buildingID);
		return s;
	}

	protected void removePlayerStructureFromAllStructures(long buildingID){
		/* Structure s = vPlayerStructures.get(buildingID);
            while(s.isBUpdateThreadIsUsing())
            {

            }
		 */
		
		synchronized(vPlayerStructures) {
			vPlayerStructures.remove(buildingID);
		}
	}

	protected ConcurrentHashMap<Long, Structure> getAllPlayerStructures(){
		return vPlayerStructures;
	}

	protected boolean profanityCheck(String sToCheck){
		int reason = dbInterface.isNameAppropriate(sToCheck, null, this);
		return (reason == Constants.NAME_ACCEPTED);
	}

	protected boolean hasActiveClientWithAccountID(long accountID) {
		Enumeration<ZoneClient> vActiveClientEnum = vAllClients.elements();
		while (vActiveClientEnum.hasMoreElements()) {
			ZoneClient client = vActiveClientEnum.nextElement();
			if (client.getAccountID() == accountID) {
				return true;
			}
		}
		return false;
	}

	protected void runScript(int scriptType, long objectID, long targetPlayerID) throws ScriptException {
		scriptManager.runScript(scriptType, objectID, targetPlayerID);
	}

	protected AccountData getAccountDataFromLoginServer(int accountID) {
		if (bUsingLoginServer) {
			return theGui.getLoginServer().getAccountData(accountID);
		} else {
			// Get it from the transciever.
			return loginTransciever.getAccountDataFromLoginServer(accountID);
		}
	}

	protected void triggerLoginLoadNewPlayer(long playerID) {
		if(!bUsingLoginServer) {
			try {
				loginTransciever.sendLoadNewPlayerTrigger(playerID);
			} catch (Exception e) {
				DataLog.logEntry("Error sending load new player trigger to remote Login Server: " + e.toString(),"ZoneServer()", Constants.LOG_SEVERITY_INFO, ZoneRunOptions.bLogToConsole, true);
				//System.out.println("Error sending load new player trigger to remote Login Server: " + e.toString());
				e.printStackTrace();
			}
		} /*else { 
			  //Do nothing -- the DatabaseInterface has already loaded the new player into the Login Server. 
		  } */
	}

	protected void killTransciever() {
		try {
			loginTransciever.terminate();
			DatabaseInterface.updateGalaxyStatus(iServerID, 0,0);
		} catch (Exception e) {
			// Oh well.
		}
	}

	public void lockServer() {
		iServerStatus = Constants.SERVER_STATUS_LOCKED;
		if (loginTransciever != null) {
			loginTransciever.volunteerStatusChange();
		}
		DatabaseInterface.updateGalaxyStatus(iServerID, iServerStatus, getPlayersOnline());
	}

	public void unlockServer() {
		iServerStatus = Constants.SERVER_STATUS_ONLINE;
		if (loginTransciever != null) {
			loginTransciever.volunteerStatusChange();
		}
		DatabaseInterface.updateGalaxyStatus(iServerID, iServerStatus, getPlayersOnline());
	}

	protected void setStatus(int status) {
		iServerStatus = status;
		DatabaseInterface.updateGalaxyStatus(iServerID, iServerStatus, getPlayersOnline());
		if (loginTransciever != null) {
			loginTransciever.volunteerStatusChange();
		} else if (bUsingLoginServer) {
			LoginServer lServer = theGui.getLoginServer();
			if (lServer != null) {
				lServer.sendUpdateServerStatus();
			}
		}
	}

	protected void sendNotifyLoginDisconnect() {
		Enumeration<ZoneClient> clients = vAllClients.elements();
		byte[] packet1 = null;
		byte[] packet2 = null;
		byte[] packet3 = null;
		try { 
			packet1 = PacketFactory.buildChatSystemMessage("Attention:  The Login Server has shut down.");
			packet2 = PacketFactory.buildChatSystemMessage("You will not be able to re-enter the Galaxy if you log out.");
			packet3 = PacketFactory.buildChatSystemMessage("We apologize for any inconvenience.");
		} catch (Exception e) {
			// D'oh!
		}
		while (clients.hasMoreElements()) {
			ZoneClient theClient = clients.nextElement();
			if (theClient.getValidSession()) {
				theClient.insertPacket(packet1);
				theClient.insertPacket(packet2);
				theClient.insertPacket(packet3);
			}
		}
	}

	protected void sendNotifyLoginReconnect() {
		Enumeration<ZoneClient> clients = vAllClients.elements();
		byte[] packet1 = null;
		byte[] packet2 = null;
		byte[] packet3 = null;
		try { 
			packet1 = PacketFactory.buildChatSystemMessage("Attention:  The Login Service has been restored.");
			packet2 = PacketFactory.buildChatSystemMessage("All players will now be able to log in normally once again.");
			packet3 = PacketFactory.buildChatSystemMessage("We apologize for any inconvenience.");
		} catch (Exception e) {
			// D'oh!
		}
		while (clients.hasMoreElements()) {
			ZoneClient theClient = clients.nextElement();
			if (theClient.getValidSession()) {
				theClient.insertPacket(packet1);
				theClient.insertPacket(packet2);
				theClient.insertPacket(packet3);
			}
		}
	}
	protected void reloadServerRadials(){
		chmServerObjectRadials = dbInterface.getObjectRadials();
	}

	protected void forwardFriendChangedStatus(Player player, boolean status) {
		if (bUsingLoginServer) {
			lServer.forwardFriendChangedStatus(player, status);
		} else { 
			try {
				loginTransciever.sendFriendUpdatedStatus(player, status);
			} catch (IOException e) {
				// D'oh!
			}
		}
	}

	protected void sendFriendChangedOnlineStatus(String sServerName, String sFriendName, boolean bConnected) throws IOException {
		Enumeration<Player> vPlayers = vAllPlayers.elements();
		while (vPlayers.hasMoreElements()) {
			Player player = vPlayers.nextElement();
			ZoneClient client = player.getClient();
			if (player.getStatus() && client != null) {
				if (bConnected) {
					client.insertPacket(PacketFactory.buildFriendOnlineStatusUpdate(player, sServerName, sFriendName));
				} else {
					client.insertPacket(PacketFactory.buildFriendOfflineStatusUpdate(player, sServerName, sFriendName));
				}
			}
		}
	}

	protected int sendRequestPlayerExistOnServer(int serverID, String sFirstName) {
		if (bUsingLoginServer) {
			Vector<Player> vPlayers= lServer.getCharacterListForServer(serverID);
			for (int i = 0; i < vPlayers.size(); i++) {
				Player player = vPlayers.elementAt(i);
				if (player.getFirstName().equalsIgnoreCase(sFirstName)) {
					return Constants.FRIEND_EXISTS_ON_SERVER;
				}
			}
			return Constants.FRIEND_NOT_EXISTS_ON_SERVER;
		} else {

			return Constants.SERVER_STATUS_OFFLINE;
		}
	}

	public void broadcastSystemMessage(Player targetPlayer, String message) {
		
		//Get the player from the server.
		ZoneClient targetClient = targetPlayer.getClient();
		
		try {		

			//If the player is valid & currently connected.
			if(targetClient != null && targetClient.getValidSession()) {

				//Send the system message.
				targetClient.insertPacket(PacketFactory.buildChatSystemMessage(message));
			} else {

				throw new IllegalArgumentException(String.format("Player ID(%s) doesn't have a valid session with the server.", targetPlayer.getID()));
			}
		} catch(IOException e) {
			//Let it cause problems, if this throws an exception something is internally wrong.
			e.printStackTrace();
		}
	}

	public void broadcastAreaMessage(SOEObject sourceObject, String message, boolean bIncludeObject) {
		
		//Get the object and all players around the object.
		Enumeration<Player> ePlayerList = getPlayersAroundObject(sourceObject, bIncludeObject).elements();

		//While we have more players to send a message to.
		while (ePlayerList.hasMoreElements()) {	
			//Get the player and client.
			Player targetPlayer = ePlayerList.nextElement();

			//Send the message.
			broadcastSystemMessage(targetPlayer, message);
		}
	}
	
	public void broadcastPlanetaryMessage(int planetID, String message) {
		
		//Get the object and all players around the object.
		Enumeration<Player> ePlayerList = getAllPlayersOnPlanet(planetID).elements();

		//While we have more players to send a message to.
		while (ePlayerList.hasMoreElements()) {	
			
			//Get the player and client.
			Player targetPlayer = ePlayerList.nextElement();

			//Send the message.
			broadcastSystemMessage(targetPlayer, message);
		}
	}
	
	public void broadcastServerWideMessage(String message) {
		
		//Get all players on the servers.
		Enumeration<Player> ePlayerList = getAllOnlinePlayers().elements();
		

		//While we have more players to send a message to.
		while (ePlayerList.hasMoreElements()) {
				
			//Get the player and client.
			Player currentPlayer = ePlayerList.nextElement();
			
				
			//Send the message.
			broadcastSystemMessage(currentPlayer, message);
		}
	}
	
	public void broadcastServerMessage(String message) {

		//Append the string with the [SERVER]: Prefix.
		message = "[SERVER]: " + message;
		
		//Broadcast the message.
		broadcastServerWideMessage(message);
	}

	protected ScriptManager getScriptManager() {
		return scriptManager;
	}
	
	protected static float getLineLength(float l1, float l2) {
		return Math.abs(l2 - l1);
	}
	
	protected void sendToRange(byte[] packet, int range, SOEObject generatingPlayer) throws Exception {
        Vector<ZoneClient> vSendList = new Vector<ZoneClient>();
        //ZoneClient generatingClient = generatingPlayer.getClient();
		switch(range)
        {
            case 0x01: // PACKET_RANGE_GROUP = 0x01;
            {                        
            	throw new Exception("Invalid range for Zone Server sendToRange: range specified is GROUP");
            	// Should not be called this way
            	
            	//Group g = (Group)getObjectFromAllObjects(player.getGroupID());
                //vSendList.addAll(g.getPlayerObjectsInGroup());                                                
            }
            case 0x02: // PACKET_RANGE_GROUP_EXCLUDE_SENDER = 0x02;
            {
                throw new Exception("Invalid range for Zone Server sendToRange: range specified as GROUP_EXCLUDE_SENDER");
            	//Group g = (Group)myServer.getObjectFromAllObjects(thePlayer.getGroupID());
                //vSendList.addAll(g.getPlayerObjectsInGroup());
                //vSendList.remove(this.getPlayer());
            }
            case 0x03: // PACKET_RANGE_CHAT_RANGE = 0x03;
            {
                Vector<Player> vPL = getPlayersAroundObject(generatingPlayer, true);
                for(int i = 0 ; i < vPL.size(); i++)
                {
                    Player T = vPL.get(i);
                    ZoneClient tarClient= T.getClient();
                    if (tarClient!= null) {
	                    if((ZoneServer.getRangeBetweenObjects(generatingPlayer,T) <= Constants.CHATRANGE) && tarClient.getClientReadyStatus())
	                    {   
	                        vSendList.add(tarClient);
	                    }
                    }
                }
                break;
            }
            case 0x04: //  PACKET_RANGE_CHAT_RANGE_EXCLUDE_SENDER = 0x04;
            {
                Vector<Player> vPL = getPlayersAroundObject(generatingPlayer, false);
                for(int i = 0 ; i < vPL.size(); i++)
                {
                    Player T = vPL.get(i);
                    ZoneClient tarClient = T.getClient();
                    if (tarClient != null) {
                    	if (T.getID() != generatingPlayer.getID()) {
		                    if(ZoneServer.getRangeBetweenObjects(generatingPlayer,T) <= Constants.CHATRANGE && T.getClient().getClientReadyStatus())
		                    {                                   
		                        vSendList.add(tarClient);
		                    }
                    	}
                    }
                }
                break;
            }
            case 0x05: //  PACKET_RANGE_PLANET = 0x05;
            {
                Vector<Player> vPlayersOnPlanet = getAllPlayersOnPlanet(generatingPlayer.getPlanetID());
            	for (int i = 0; i < vPlayersOnPlanet.size(); i++) {
            		ZoneClient client = vPlayersOnPlanet.elementAt(i).getClient();
            		if (client != null) {
            			vSendList.add(client);
            		}
            	} 
                break;
            }
            case 0x06: //  PACKET_RANGE_PLANET_EXCLUDE_SENDER = 0x06;
            {
                Vector<Player> vPlayersOnPlanet = getAllPlayersOnPlanet(generatingPlayer.getPlanetID());
            	for (int i = 0; i < vPlayersOnPlanet.size(); i++) {
            		Player tarPlayer = vPlayersOnPlanet.elementAt(i);
            		if (tarPlayer.getID() != generatingPlayer.getID()) {
	            		ZoneClient client = vPlayersOnPlanet.elementAt(i).getClient();
	            		if (client != null) {
	            			vSendList.add(client);
	            		}
            		}
            	} 
                break;
            }
            case 0x07: //  PACKET_RANGE_SERVER = 0x07;
            {
            	vSendList.addAll(getAllClients().values());
            	break;
            }
            case 0x08: //  PACKET_RANGE_SERVER_EXCLUDE_SENDER = 0x08;
            {
            	Enumeration<ZoneClient> vAllClients = getAllClients().elements();
            	while (vAllClients.hasMoreElements()) {
            		ZoneClient client = vAllClients.nextElement();
            		if (client.getClientReadyStatus()) {
            			Player tarPlayer = client.getPlayer();
            			if (tarPlayer != null) {
            				if (tarPlayer.getID() != generatingPlayer.getID()) {
            					vSendList.add(client);
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
            	break;
            }
        }
        if(!vSendList.isEmpty())
        {
            for(int i = 0; i < vSendList.size();i++)
            {
            	ZoneClient recipient = vSendList.elementAt(i);
            	if (recipient != null) {
            		recipient.insertPacket(packet);
            	}
            }
        } // else {
        	// Nobody around to see it.
		//}
	}
	
	protected void removeSerialNumberFromAllUsedSerialNumbers(long lSerial) {
		vUsedSerialNumbers.remove(lSerial);
	}
	
}

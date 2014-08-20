import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFrame;
import net.persistentworlds.config.Config;
import net.persistentworlds.config.FatalConfigException;

/**
 * The main program file, SWGGui handles all Graphical needs as well as instantiating the Login Server and any / all
 * Ping Servers and Zone Servers.
 * @author Darryl
 * @author Interesting
 *
 */
public class SWGGui implements Runnable, KeyListener, MouseListener{
	public static Random randomizer = new Random(System.currentTimeMillis());
	private static Runtime rt;
	private JFrame spaceFrame;
	private Graphics2D g;
	private Graphics2D buffG;
	private Image buffImg;
	private Thread redrawThread;
	private int screenWidth = 800;
	private int screenHeight = 600;
	private int screenHeightDiv2 = screenHeight / 2;
	private int screenWidthDiv2 = screenWidth / 2;
	private static Image splashImage = null;
	private LoginServer loginServer;
	private ZoneServer zoneServer;
	private DatabaseInterface dbInterface;
	private String sDatabaseAuthenticationName = null;
	private String sDatabaseAuthenticationPassword = null;
	private String databaseAddress = null;
	private boolean bEncryptPasswords;
	private int iEncryptionKey;
	private final static long SHUTDOWN_INIT_NANO = 300000000000l; // 5 minutes, to the nanosecond.
	private long lTimeToShutdown = SHUTDOWN_INIT_NANO;
	private boolean bShuttingDown = false;
	private int databasePort;
	private int iLoginServerPort = -1;
	private int iZoneServerPort = -1;
	private boolean bAutoAccountRegistration = false;
	private String sDatabaseSchema = "/swgprecu";
	private static String sWebPath = "";
	private String sScriptPath = "";
	private int remoteLoginServerPort;
	private long lNextStatusUpdate;
	private InetAddress remoteLoginServerAddress;
	private boolean bUsingLoginServer;
	private boolean bUsingZoneServer;
	private static int processorCount = 0;
	private long freeMemory = 0;
	private int zoneServerID;
	private static boolean packetLogEnabled;
	private static String [] startupArguments;
	private Socket remoteAuthSocket;
	private DataInputStream remoteDataIn;
	private DataOutputStream remoteDataOut;
	private long lUptime;
	private Config serverSettings;
	private ServerSetup configManager;

	public void mouseExited(MouseEvent m) {

	}

	public void mouseReleased(MouseEvent m){

	}

	public void mouseClicked(MouseEvent m) {
		//Point clickLocation = m.getLocationOnScreen();

	}

	public void mousePressed(MouseEvent m) {
		//Point clickLocation = m.getLocationOnScreen();

	}

	public void mouseEntered(MouseEvent m) {


	}
	public void keyPressed(KeyEvent arg0) {
		bPressedAnything = true;
		pressedKeys[arg0.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent arg0) {
		pressedKeys[arg0.getKeyCode()] = false;

	}

	public void keyTyped(KeyEvent arg0) {
	}

	protected boolean[] pressedKeys;

	public boolean isKeyPressed(int iKeyCode){
		if( (iKeyCode >= pressedKeys.length) || (iKeyCode <0) ){
			return false;
		}
		return pressedKeys[iKeyCode];
	}

	private void clearKeys() {
		for (int i = 0; i < pressedKeys.length; i++) {
			pressedKeys[i] = false;
		}
		bPressedAnything = false;
	}
	private Font font;

	/**
	 * Constructs a new SWGGui object.
	 */
	private boolean bNeedAuthentication = false;
	public SWGGui() throws IOException {

		//Try to load configuration.
		try {
			configManager = new ServerSetup();
			configManager.handleLoadConfiguration();
			serverSettings = configManager.getCurrentConfiguration();

		} catch(FatalConfigException e) {

			//Fatal condition occurred, exit.
			e.printStackTrace();
			System.exit(0);
		}

		boolean bAuthorized = false;
		if (bNeedAuthentication) {
			// Very first thing you're going to do is authenticate with remote host.

			System.out.println("Authenticating with remote server auth1.shardsoftheforce.co.cc");
			Socket socket = null;
			InetAddress[] remoteAddresses = new InetAddress[3];

			remoteAddresses[0] = InetAddress.getByName("auth1.shardsoftheforce.co.cc");
			remoteAddresses[1] = InetAddress.getByName("auth2.shardsoftheforce.co.cc");
			remoteAddresses[2] = InetAddress.getByName("auth3.shardsoftheforce.co.cc");
			for (int i = 0; i < remoteAddresses.length && !bAuthorized; i++) {
				try {
					socket = new Socket(remoteAddresses[i], 44410);
					socket.setSoTimeout(2000);  // Wait 2 seconds on any read operation.
					DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
					DataInputStream dIn = new DataInputStream(socket.getInputStream());
					dOut.writeByte(Constants.CONNECTION_REQUEST);
					dOut.writeUTF(serverSettings.getRemoteAuthorizationUsername());
					dOut.writeUTF(serverSettings.getRemoteAuthorizationPassword());
					dOut.flush();
					byte connectionResponse = dIn.readByte(); // Block here until you get a response.
					if (connectionResponse == Constants.CONNECTION_RESPONSE) {
						// Good
						byte authorizationStatus = dIn.readByte();
						switch (authorizationStatus) {
						case Constants.AUTHENTICATION_STATUS_APPROVED: {
							System.out.println("Authenticated successfully with remote host.");
							bAuthorized = true;
							remoteAuthSocket = socket;
							remoteDataIn = dIn;
							remoteDataOut = dOut;
							break;
						}case Constants.AUTHENTICATION_STATUS_INVALID_PASSWORD: {
							System.out.println("Invalid password " + serverSettings.getRemoteAuthorizationPassword() + " for username " + serverSettings.getRemoteAuthorizationUsername());
							break;
						}
						case Constants.AUTHENTICATION_STATUS_INVALID_USERNAME: {
							System.out.println("Invalid username: " + serverSettings.getRemoteAuthorizationUsername());
							break;
						}
						default:{
							System.out.println("Unknown authorization status " + authorizationStatus);
						}
						}
					} else {
						// Bad
						System.out.println("Unknown connnection response " + connectionResponse);
					}

				} catch (Exception e) {
					// Unable to connect.
				}
			}
		} else {
			bAuthorized = true;
		}
		if (!bAuthorized) {
			System.out.println("Not authenticated.");
			System.exit(0);
		}
		DataLog.initialize();
		ColorManager.initialize(8);
		lNextStatusUpdate = 0;
		processorCount = rt.availableProcessors();
		freeMemory = rt.freeMemory();
		iEncryptionKey = 0xBAD503;

		//Load configuration.
		databaseAddress = serverSettings.getDatabaseAddress();
		databasePort = serverSettings.getDatabasePort();
		sDatabaseAuthenticationName = serverSettings.getDatabaseUsername();
		sDatabaseAuthenticationPassword = serverSettings.getDatabasePassword();
		iLoginServerPort = serverSettings.getLoginServerPort();
		iZoneServerPort = serverSettings.getZoneServerPort();
		sDatabaseSchema = serverSettings.getDatabaseSchema();
		bAutoAccountRegistration = serverSettings.isAutoAccountRegistrationEnabled();
		sWebPath = serverSettings.getWebPath();
		sScriptPath = serverSettings.getScriptPath();
		bEncryptPasswords = serverSettings.isSecurePasswordsEnabled();
		zoneServerID = serverSettings.getZoneServerID();
		remoteLoginServerAddress = InetAddress.getByName(serverSettings.getRemoteLoginServerAddress());
		remoteLoginServerPort = serverSettings.getRemoteLoginServerPort();
		System.out.println("Remote listen port: " + remoteLoginServerPort);
		bUsingLoginServer = serverSettings.isLoginServerEnabled();
		bUsingZoneServer = serverSettings.isZoneServerEnabled();
		// Temporary
		//bUsingZoneServer = false;
		if (!bUsingZoneServer) {
			zoneServerID = 0;
		}
		try {
			if(!bUsingZoneServer)
			{
				spaceFrame = new JFrame( "Shards of the Force Pre-CU Login Server" );
			}
			else
			{
				spaceFrame = new JFrame( "Shards of the Force Pre-CU Server" );
			}
			spaceFrame.setSize(screenWidth, screenHeight);
			spaceFrame.validate();
			spaceFrame.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE); // For database stuff.
			spaceFrame.setIgnoreRepaint(true);
			spaceFrame.setVisible( true );
			g = (Graphics2D)spaceFrame.getContentPane().getGraphics();
			buffImg = spaceFrame.createImage(1600,1200);
			buffG = (Graphics2D)buffImg.getGraphics();
			font = g.getFont();
			fontHeight = font.getSize();
			spaceFrame.addKeyListener(this);
			spaceFrame.addMouseListener(this);
			splashImage = Toolkit.getDefaultToolkit().createImage("./images/splash.png");
			pressedKeys = new boolean[(int)Character.MAX_VALUE+1];
			DataLog.logEntry("GUI Constructor:  Construct DatabaseInterface","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);
			//System.out.println("GUI Constructor:  Construct DatabaseInterface");

			dbInterface = new DatabaseInterface(sDatabaseAuthenticationName, sDatabaseAuthenticationPassword, databaseAddress, databasePort, sDatabaseSchema, this, bEncryptPasswords, iEncryptionKey,zoneServerID);
			DataLog.logEntry("Database Interface constructed.","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);

			//loginServer = null;

			if (bUsingLoginServer) {
				DataLog.logEntry("Creating Login Server...","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);
				loginServer = new LoginServer(this, iLoginServerPort, remoteLoginServerPort);
				if(bUsingZoneServer)
				{
					DataLog.logEntry("Creating Zone Server...","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);
					zoneServer = new ZoneServer(this, iZoneServerPort, sScriptPath, zoneServerID, null, -1);
				}
				else
				{
					DataLog.logEntry("No Zone Server Created this is a Login Only Server.","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);
				}
			} else {
				DataLog.logEntry("Creating Zone Server...","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);
				loginServer = null;
				zoneServer = new ZoneServer(this, iZoneServerPort, sScriptPath, zoneServerID, remoteLoginServerAddress, remoteLoginServerPort); // These must be updated.
			}

			LoginServer.setLocalZoneServer(zoneServer);
			bSentShutdownMessage = new boolean[iSHUTDOWN_MESSAGES];
			//System.out.println("GUI Creation complete.");
			DataLog.logEntry("GUI Creation complete.", "SWGGui", Constants.LOG_SEVERITY_INFO, true,true);

			rt.gc();
			long lTotalMemory = rt.totalMemory();
			long lFreeMemory = rt.freeMemory();
			long lUsedMemory = lTotalMemory - lFreeMemory;
			//System.out.println("Using " + lUsedMemory + " bytes of " + lTotalMemory + " VM available.  Free: " + lFreeMemory + " bytes.");
			DataLog.logEntry("Using " + lUsedMemory + " bytes of " + lTotalMemory + " VM available.  Free: " + lFreeMemory + " bytes.", "SWGGui", Constants.LOG_SEVERITY_INFO, true,true);

		} catch (Exception e) {
			System.out.println("Error caught: " + e.toString());
			e.printStackTrace();
		}

	}

	public boolean getIsAutoAccountRegister() {
		return bAutoAccountRegistration;
	}

	/**
	 * The main program.
	 */
	public static void main(String[] args) throws Exception {

		// First things first.
		TrigAngleUtils.initialize();
		TrigTanUtils.initialize();
		TrigSinAndCosUtils.initialize();
		packetLogEnabled = false;
		startupArguments = args;

		if((args.length >= 1 && args[0].contentEquals("-packetlog")))
		{
			packetLogEnabled = true;
			DataLog.logEntry("Packet Logging is Enabled.", "SWGGui", Constants.LOG_SEVERITY_INFO, true, true);
		}
		else
		{
			DataLog.logEntry("Packet Logging is Disabled.", "SWGGui", Constants.LOG_SEVERITY_INFO, true, true);
		}


		rt = Runtime.getRuntime();
		rt.traceMethodCalls(true);
		System.out.println("SWGGui Creating Log Server.");
		DataLog.logEntry("Starting program.","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);

		DataLog.logEntry("GUI constructed -- running...","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);
		SWGGui theWindow = new SWGGui();
		theWindow.start();
	}

	private int framesLive = 0;
	private int fontHeight = -1;

	//private final static String ARCH_NULL = "No Architects Found!";
	//private final static String ARCH_STATUS = "Architect Status: ";
	//private final static String ARCH_OFFLINE = "Offline";
	//private final static String ARCH_LOADING = "Loading...";
	//private final static String ARCH_ONLINE = "Online";

	private int splashDisplayTimeMS = 5000;

	/**
	 * The main draw function for the program.
	 * @param g -- The Graphics object to draw to.
	 */
	public void draw(Graphics2D g) {
		if (splashDisplayTimeMS > 0) {
			g.drawImage(splashImage, 0, 0, null);
		} else {
			if (fontHeight == -1) {
				font = new Font("Times New Roman", Font.TRUETYPE_FONT, 12);
				g.setFont(font);
				fontHeight = g.getFont().getSize();
			}
			//int iNoArchitectsLen = (fontHeight * ARCH_NULL.length());
			//g.setClip(0,0,iCurrentWidth, iCurrentHeight);
			g.setColor(Color.WHITE);
			g.fillRect(0,0,screenWidth, screenHeight);
			int currentX = 10;
			int currentY = 10;
			g.setColor(Color.BLACK);
			g.drawString(Constants.getCurrentSoftwareVersion(), screenWidthDiv2 - (screenWidthDiv2/2),currentY);
			currentY += fontHeight;
			int stringLength = (int)getStringWidth("Uptime: ", font);
			currentX = screenWidthDiv2 - (screenWidthDiv2/2);
			g.drawString("Uptime: " , currentX, currentY);
			currentX += stringLength;
			drawTime(g, lUptime / 1000000l, currentX, currentY);
			currentX = 10;
			currentY += fontHeight;
			if (loginServer != null) {
				g.drawString("Running Login Server", currentX, currentY);
				currentY += fontHeight;
				//g.drawString("Ticks: " + framesLive, currentX, currentY);
				//currentY += fontHeight;
				g.drawString("Clients connected to LoginServer: " + loginServer.getClientCount(), currentX, currentY);
			} else {
				g.drawString("Login Server disabled by command line argument", currentX, currentY);
			}
			currentY += fontHeight;
			g.drawString("Processor count: " + processorCount,  currentX, currentY);
			currentY += fontHeight;
			g.drawString("Available memory: " + freeMemory, currentX, currentY);
			//currentY = screenHeightDiv2;
			currentY = 10 + fontHeight * 2;
			currentX = screenWidth / 2;
			if(zoneServerID!=0)
			{
				g.drawString("Running Zone Server", currentX, currentY);
			}
			else
			{
				g.drawString("Running Login Only Server", currentX, currentY);
			}

			currentY += fontHeight;
			if(zoneServerID!=0)
			{
				g.drawString("Zone Server Hostname: " + zoneServer.getHostName(), currentX, currentY);
			}
			else
			{
				g.drawString("Login Server Hostname: " + loginServer.getHostName(), currentX, currentY);
			}
			currentY += fontHeight;
			try {
				InetAddress serverAddress = InetAddress.getByName(zoneServer.getHostName());
				g.drawString("Zone Server raw address: " + serverAddress.getHostAddress(), currentX, currentY);
			} catch (Exception e) {
				// D'oh!
			}
			if(zoneServerID!=0)
			{
				currentY += fontHeight;
				g.drawString("Zone Server Status: " + Constants.STRING_SERVER_STATUS[zoneServer.getStatus()], currentX, currentY);
				currentY += fontHeight;
				g.drawString("Players connected to Zone Server: " + zoneServer.getClientConnectionCount(), currentX, currentY);
			}

			currentY += fontHeight;
			g.drawString("Press C to view zone data.", currentX, currentY);
			currentY += fontHeight;
			g.drawString("Press S to open server setup.", currentX, currentY);			
			currentX = 10;
			//currentY += (fontHeight);

			if (vClients != null && !vClients.isEmpty()) {
				Enumeration<ZoneClient> vClientItr = vClients.elements();
				for (int i = 0; i < iPlayerIndex; i++) {
					vClientItr.nextElement();
				}
				ZoneClient displayClient = vClientItr.nextElement();
				g.drawString("Client Index: " + iPlayerIndex + ", ID: " + displayClient.getAccountID(), currentX, currentY);
				currentY += fontHeight;
				Player displayPlayer = displayClient.getPlayer();
				if (displayPlayer != null) {
					g.drawString("Player: " + displayPlayer.getFirstName(), currentX, currentY);
					currentY += fontHeight;
					if(displayPlayer.getPlanetID() > 9)
					{
						g.drawString("X: " + displayPlayer.getX() + ", Y: " + displayPlayer.getY() + ", Terrain: " + Constants.TerrainNames[displayPlayer.getPlanetID()] + ".", currentX, currentY);
					}
					else
					{
						g.drawString("X: " + displayPlayer.getX() + ", Y: " + displayPlayer.getY() + ", Planet: " + Constants.PlanetNames[displayPlayer.getPlanetID()] + ".", currentX, currentY);
					}

				} else {
					g.drawString("No player selected by this client", currentX, currentY);
				}
			} else {
				g.drawString("No connected clients!", currentX, currentY);
			}
			if (bShuttingDown) {
				currentY = screenHeightDiv2 + (screenHeightDiv2 / 2);
				currentX = screenWidthDiv2 - 40;
				g.drawString("Press a to abort shutdown.", currentX, currentY);
				currentY += fontHeight;
				g.drawString("Time to shutdown: ", currentX, currentY);
				currentY += fontHeight;
				drawTime(g, lTimeToShutdown / 1000000, currentX, currentY);

			}
		}
	}

	private ConcurrentHashMap<SocketAddress, ZoneClient> vClients = null;
	private int iPlayerIndex = 0;
	//protected int iCurrentWidth = 0;
	//protected int iCurrentHeight = 0;
	private int iLastWidth = 0;
	private int iLastHeight = 0;
	private final static String SHUTDOWN_WARNING = "Warning:  Server shutdown has been initiated by the console.";
	private final static String SHUTDOWN_ABORT = "Server shutdown has been aborted by the administrator.";
	private final static String SHUTDOWN_TIME = "Time until server shutdown: ";
	private boolean[] bSentShutdownMessage;
	private final static int iSHUTDOWN_MESSAGES = 5;

	private boolean bPressedAnything = false;
	/**
	 * The main update function.
	 * @param lTimePassedNano
	 */
	public void update(long lTimePassedNano) {
		lUptime += lTimePassedNano;

		//server status if this is a login server only is processed here.
		if(this.zoneServerID == 0)
		{
			if(lNextStatusUpdate <= 0)
			{
				lNextStatusUpdate = 1000 * 60 * 3;
				//System.out.println("Galaxy Status Tick. " + (lNextStatusUpdate / 1000 / 60) + " : " + (lTimePassedNano/1000000));
				ZoneServer.writeGalaxyStatusFile("Login Server",false);
			}
			else
			{
				lNextStatusUpdate -= (lTimePassedNano / 1000000);
				if(lNextStatusUpdate < 0)
				{
					lNextStatusUpdate = 0;
				}
			}
		}
		//---------------

		if ((framesLive % 200) == 0) {
			rt.gc();
			freeMemory = rt.freeMemory();
		}
		screenWidth = spaceFrame.getWidth();
		screenHeight = spaceFrame.getHeight();
		framesLive++;
		if (splashDisplayTimeMS > 0) {
			spaceFrame.setSize(splashImage.getWidth(null), splashImage.getHeight(null) + spaceFrame.getInsets().top);
			splashDisplayTimeMS -= targetLoopDelayMS;
			if (splashDisplayTimeMS <= 0) {
				screenWidth = 800;
				screenHeight = 600;
				spaceFrame.setSize(screenWidth, screenHeight);
				g = (Graphics2D)spaceFrame.getContentPane().getGraphics();
			}
		}
		if (screenWidth != iLastWidth || screenHeight != iLastHeight) {
			g = (Graphics2D)spaceFrame.getContentPane().getGraphics();
			iLastWidth = screenWidth;
			iLastHeight = screenHeight;
			screenWidthDiv2 = screenWidth / 2;
			screenHeightDiv2 = screenHeight / 2;
		}

		if(zoneServerID!=0 && vClients == null)
		{
			vClients = zoneServer.getAllClients();
		}
		if (isKeyPressed(KeyEvent.VK_UP)) {
			iPlayerIndex++;
		} else if (isKeyPressed(KeyEvent.VK_DOWN)) {
			iPlayerIndex--;
			if (iPlayerIndex < 0) {
				iPlayerIndex = vClients.size() - 1;
			}
		} else if (isKeyPressed(KeyEvent.VK_Q)) {
			bShuttingDown = true;
		} else if (isKeyPressed(KeyEvent.VK_C)) {
			if (zoneServer.getStatus() == Constants.SERVER_STATUS_ONLINE) {
				zoneServer.activateScreen();
			}
		} else if (isKeyPressed(KeyEvent.VK_A) && bShuttingDown) {
			bShuttingDown = false;
			for (int i = 0; i < bSentShutdownMessage.length; i++) {
				bSentShutdownMessage[i] = false;
			}
			lTimeToShutdown = SHUTDOWN_INIT_NANO;
			Enumeration<ZoneClient> vClientItr = vClients.elements();
			while (vClientItr.hasMoreElements()) {
				ZoneClient client = vClientItr.nextElement();
				try {
					client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_ABORT));
				} catch (Exception e) {
					System.out.println("Error sending shutdown abort packet to client." + e.toString());
					e.printStackTrace();
				}
			}
		} else if (isKeyPressed(KeyEvent.VK_S)) {
			System.out.println("Opening Configuration window.");

			//Try to load configuration.
			try {
				configManager.handleLoadConfiguration();
			} catch(FatalConfigException e) {

				//Fatal condition occurred, exit.
				e.printStackTrace();
				System.exit(0);
			}
		}
		if (bShuttingDown) {
			if (!bSentShutdownMessage[4] && (lTimeToShutdown <= SHUTDOWN_INIT_NANO)) {
				bSentShutdownMessage[4] = true;
				Enumeration<ZoneClient> vClientItr = vClients.elements();
				while (vClientItr.hasMoreElements()) {
					ZoneClient client = vClientItr.nextElement();
					if (client != null) {
						try {
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_WARNING));
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_TIME + "5 minutes."));
						} catch (Exception e) {
							System.out.println("Error inserting shutdown packets into client: " + e.toString());
							e.printStackTrace();
						}
					}
				}
			} else if (!bSentShutdownMessage[3] && lTimeToShutdown <= 240000000000l) {
				bSentShutdownMessage[3] = true;
				Enumeration<ZoneClient> vClientItr = vClients.elements();
				while (vClientItr.hasMoreElements()) {
					ZoneClient client = vClientItr.nextElement();
					if (client != null) {
						try {
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_WARNING));
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_TIME + "4 minutes."));
						} catch (Exception e) {
							System.out.println("Error inserting shutdown packets into client: " + e.toString());
							e.printStackTrace();
						}
					}
				}
			} else if (!bSentShutdownMessage[2] && lTimeToShutdown <= 180000000000l ) {
				bSentShutdownMessage[2] = true;
				Enumeration<ZoneClient> vClientItr = vClients.elements();
				while (vClientItr.hasMoreElements()) {
					ZoneClient client = vClientItr.nextElement();
					if (client != null) {
						try {
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_WARNING));
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_TIME + "3 minutes."));
						} catch (Exception e) {
							System.out.println("Error inserting shutdown packets into client: " + e.toString());
							e.printStackTrace();
						}
					}
				}
			} else if (!bSentShutdownMessage[1] && lTimeToShutdown <= 120000000000l) {
				bSentShutdownMessage[1] = true;
				Enumeration<ZoneClient> vClientItr = vClients.elements();
				while (vClientItr.hasMoreElements()) {
					ZoneClient client = vClientItr.nextElement();
					if (client != null) {
						try {
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_WARNING));
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_TIME + "2 minutes."));
						} catch (Exception e) {
							System.out.println("Error inserting shutdown packets into client: " + e.toString());
							e.printStackTrace();
						}
					}
				}
			} else if (!bSentShutdownMessage[0] && lTimeToShutdown <= 60000000000l) {
				bSentShutdownMessage[0] = true;
				Enumeration<ZoneClient> vClientItr = vClients.elements();
				while (vClientItr.hasMoreElements()) {
					ZoneClient client = vClientItr.nextElement();
					if (client != null) {
						try {
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_WARNING));
							client.insertPacket(PacketFactory.buildChatSystemMessage(SHUTDOWN_TIME + "1 minutes."));
						} catch (Exception e) {
							System.out.println("Error inserting shutdown packets into client: " + e.toString());
							e.printStackTrace();
						}
					}
				}
			} else if (lTimeToShutdown <= 0) {
				Enumeration<ZoneClient> vClientItr = vClients.elements();
				while (vClientItr.hasMoreElements()) {
					ZoneClient client = vClientItr.nextElement();
					Player p = client.getPlayer();
					if (p != null) {
						dbInterface.updatePlayer(client.getPlayer(), true,false);
					}
				}
				zoneServer.killTransciever();
				//zoneServer.writeHeightMapToFile();
				System.out.println("Safe shutdown by pressing Q.");
				try {
					remoteDataOut.writeByte(Constants.DISCONNECT);
					remoteDataOut.flush();
					remoteDataOut.close();
					remoteDataIn.close();
					remoteAuthSocket.close();
				} catch (Exception e) {
					// Oh well, we're closing anyway.
				}
				System.exit(0);
			}
			lTimeToShutdown -= lTimePassedNano;
		}
		if (vClients.size() > 0) {
			iPlayerIndex = iPlayerIndex % vClients.size();
		}
		if (bPressedAnything) {
			clearKeys();
		}
	}

	private long targetLoopDelayMS = 200;

	/**
	 * The main thread loop.
	 */
	public void run() {
		DataLog.logEntry("GUI running.","SWGGui",Constants.LOG_SEVERITY_INFO,true,true);
		long lWaitTimeNano = 0;
		long lCurrentTimeNano = System.nanoTime();
		long lLastTimeNano = 0;
		long lDeltaTimeNano = 0;
		while(redrawThread != null){
			try{
				synchronized(this){
					lLastTimeNano = lCurrentTimeNano;
					lCurrentTimeNano = System.nanoTime();
					lDeltaTimeNano = lCurrentTimeNano - lLastTimeNano;
					lWaitTimeNano = (targetLoopDelayMS * 1000000) - lDeltaTimeNano;
					lWaitTimeNano = Math.max(1, lWaitTimeNano);
					Thread.yield();
					wait((lWaitTimeNano / 1000000), (int)(lWaitTimeNano % 1000000));
					lCurrentTimeNano = System.nanoTime();
					update(lCurrentTimeNano - lLastTimeNano);
					draw(buffG);
					g.drawImage(buffImg, 0, 0, null);
					if (!spaceFrame.isVisible()) {
						if (dbInterface.getCanSafelyShutdown()) {
							System.out.println("Safe shutdown.");
							if(this.zoneServerID !=0)
							{
								zoneServer.killTransciever();
							}
							if (loginServer != null) {
								loginServer.sendNotifyShutdown();
							}
							try {
								remoteDataOut.writeByte(Constants.DISCONNECT);
								remoteDataOut.flush();
								remoteDataOut.close();
								remoteDataIn.close();
								remoteAuthSocket.close();
							} catch (Exception e) {
								// Oh well, we're closing anyway.
							}

							System.exit(0);
						} else {
							System.out.println("Waiting on database... shutdown not safe.");
						}
					}
				}

			}catch(Exception e){
				//who cares
				System.out.println("We exploded while running the thread: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initializes the threads.
	 */
	private void start() {
		redrawThread = new Thread(this);
		redrawThread.setName("SWGGui thread");
		redrawThread.start();
		if (bUsingLoginServer) {
			loginServer.start();
		}
		if (bUsingZoneServer) {
			zoneServer.start();
		}
	}

	/**
	 * Returns the pointer to the Login Server.
	 * @return The Login Server.
	 */
	public LoginServer getLoginServer() {
		return loginServer;
	}

	/**
	 * Returns the pointer to the Zone Server.
	 * @return The Zone Server.
	 */
	public ZoneServer getZoneServer() {
		return zoneServer;
	}

	/**
	 * Returns the pointer to the Database object.
	 * @return The DB.
	 */
	public DatabaseInterface getDB() {
		return dbInterface;
	}

	/**
	 * Gets a short between 0 inclusive and the specified value exclusive.
	 * @param range
	 * @return
	 */
	public static short getRandomShort(short range) {
		return (short)randomizer.nextInt(range);
		//int r = Math.abs(randomizer.nextInt());
		//return r % range;
	}

	/**
	 * Gets a short between the lower value inclusive and the upper value exclusive.
	 * @param lower -- The lowest value to return.
	 * @param higher -- 1 greater than the highest value to return.
	 * @return A random int between lower and higher.
	 */
	public static short getRandomShort(int lower, int higher) {
		int range = higher - lower;
		if (range == 0) {
			return (short)lower;
		} else if (range < 0) {
			return (short)(-(randomizer.nextInt(range * -1) - lower));
		} else {
			return (short)(randomizer.nextInt(range) + lower);
		}
	}


	/**
	 * Gets an int between 0 inclusive and the specified value exclusive.
	 * @param range
	 * @return
	 */
	public static int getRandomInt(int range) {
		return randomizer.nextInt(range);
		//int r = Math.abs(randomizer.nextInt());
		//return r % range;
	}

	/**
	 * Gets an int between the lower value inclusive and the upper value exclusive.
	 * @param lower -- The lowest value to return.
	 * @param higher -- 1 greater than the highest value to return.
	 * @return A random int between lower and higher.
	 */
	public static int getRandomInt(int lower, int higher) {
		int range = higher - lower;
		if (range == 0) {
			return lower;
		} else if (range < 0) {
			return (-(randomizer.nextInt(range * -1) - lower));
		} else {
			return (randomizer.nextInt(range) + lower);
		}
	}
	/**
	 * Gets a long between 0 inclusive and the specified value exclusive.
	 * @param range -- The upper limit of the value to return.
	 * @return A random long.
	 */
	public static long getRandomLong(long range) {
		long r = Math.abs(randomizer.nextLong());
		return r % range;
	}

	/**
	 * Gets a long between the lower value inclusive and the upper value exclusive.
	 * @param lower -- The lowest value to return.
	 * @param higher -- 1 greater than the highest value to return.
	 * @return A random long between lower and higher.
	 */
	public static long getRandomLong(long lower, long higher) {
		long range = higher - lower;

		if (range == 0) {
			return lower;
		} else if (range < 0) {
			return (-((randomizer.nextLong() % range) + lower));
		} else {
			return (short)((randomizer.nextLong() % range )+ lower);
		}
	}

	public static String getWebPath(){
		return sWebPath;
	}

	public static long getFreeMemory(){
		return rt.freeMemory();
	}
	public static long getTotalMemory(){
		return rt.totalMemory();
	}

	public static long getUsedMemory(){
		return rt.totalMemory() - rt.freeMemory();
	}

	public static boolean isPacketLogEnabled() {
		return packetLogEnabled;
	}

	public static void setPacketLogEnabled(boolean packetLogEnabled) {
		SWGGui.packetLogEnabled = packetLogEnabled;
	}

	public static String[] getStartupArguments() {
		return startupArguments;
	}

	public static float getStringWidth(String sText, Font font,
			Graphics2D context) {
		Font oldFont = context.getFont();
		context.setFont(font);
		FontMetrics metrics = context.getFontMetrics();
		float width = metrics.stringWidth(sText);
		context.setFont(oldFont);
		return width * 1.2f;
	}

	/***************************************************************************
	 * @param sText
	 * @param font
	 * @return TODO
	 **************************************************************************/
	public static float getStringWidth(String sText, Font font) {
		Rectangle2D bounds = getStringVisualBounds(sText, font);
		return (float) bounds.getWidth() * 1.2f;
	}

	public static Rectangle2D getStringVisualBounds(String sText, Font font) {
		TextLayout layout = new TextLayout(sText, font, new FontRenderContext(
				null, true, true));
		return layout.getBounds();
	}

	public static float getStringHeight(String sText, Font font) {
		Rectangle2D bounds = getStringVisualBounds(sText, font);
		return (float) bounds.getHeight();
	}

	public final static int NUMDIGITS = 10;
	private static int[] digits = new int[NUMDIGITS];

	private final static String[] ALLDIGITS = {
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ","
	};
	private final static byte iSTR_DAYS = 0;
	private final static byte iSTR_HOURS = 1;
	private final static byte iSTR_MINS = 2;
	private final static byte iSTR_SECONDS = 3;
	private final static byte iSTR_MINUS = 4;
	private final static String[] ALLSTRINGS = {
		"d:",
		"h:",
		"m:",
		"s",
		"-",
	};
	private int drawString(Graphics2D g, byte iStringIndex, int x, int y) {
		String s = ALLSTRINGS[iStringIndex];
		return drawString(g, s, x, y);
	}

	private int drawString(Graphics2D g, String str, int x, int y) {
		int stringWidth = (int)getStringWidth(str, font);
		g.drawString(str, x, y);
		return stringWidth;
	}


	public int drawNum(Graphics2D g, int x, int y, long num, int numDigits,
			boolean bCommas, boolean bDrawLeadingZeros, boolean bKeepZeroSize) {
		int currentX = x;

		if (num < 0) {
			num = Math.abs(num);
			currentX += drawString(g, iSTR_MINUS, x, y);
			//return 0;
		}

		numDigits = Math.min(numDigits, NUMDIGITS);
		for (int n = 0; n < numDigits; n++) {
			digits[n] = (int) (num % 10);
			num = num / 10;
		}
		//saveClip(g);

		boolean bStillZeros = true;
		boolean bPreviousStillZero = true;
		for (int d = (numDigits - 1); d > 0; d--) {
			if (digits[d] != 0) {
				bStillZeros = false;
			}
			if ((!bDrawLeadingZeros) && bStillZeros) {
				// don't draw
			} else {
				if (((!bStillZeros) || (bDrawLeadingZeros))
						&& ((!bPreviousStillZero) || (bDrawLeadingZeros))
						&& bCommas && ((d % 3) == 2) && (d < (numDigits - 1))) {
					//g.setClip(currentX, y, imageNumsWidths[10], fontHeight);
					// These are 0's.
					currentX += drawString(g, ALLDIGITS[10], currentX, y);
				}

				if (bDrawLeadingZeros || (!bStillZeros)) {
					//g.setClip(currentX, y, imageNumsWidths[digits[d]],
					//		fontHeight);
					currentX += drawString(g, ALLDIGITS[digits[d]], currentX, y);
				}
			}

			if (bStillZeros && bKeepZeroSize && (!bDrawLeadingZeros)) {
				currentX += getStringWidth(ALLDIGITS[digits[d]], font);
			}
			if (digits[d] != 0) {
				bPreviousStillZero = false;
			}
		}

		// draw the last digit ALWAYS
		//g.setClip(currentX, y, imageNumsWidths[digits[0]], fontHeight);
		currentX += drawString(g, ALLDIGITS[digits[0]], currentX, y);

		//restoreClip(g);
		return (currentX - x);
	}

	private int drawTime(Graphics2D g, long timeMs, int currentX, int currentY) {

		long timeSecl = (timeMs / 1000);
		long timeMinl = timeSecl / 60;
		long timeHoursl = timeMinl / 60;
		long timeDaysl = timeHoursl / 24;

		int timeSec = (int) (timeSecl % 60);
		int timeMin = (int) (timeMinl % 60);
		int timeHours = (int) (timeHoursl % 24);

		int startX = currentX;

		// days...
		if (timeDaysl > 0) {
			currentX += drawNum(g, currentX, currentY, timeDaysl, 4, false, false,
					false);
			currentX += drawString(g, iSTR_DAYS, currentX, currentY);
		}

		// hours
		if (timeHours > 0 || timeDaysl > 0) {
			currentX += drawNum(g, currentX, currentY, timeHours, 2, false, true,
					true);
			currentX += drawString(g, iSTR_HOURS, currentX, currentY);
		}
		// min
		currentX += drawNum(g, currentX, currentY, timeMin, 2, false, true,
				true);
		currentX += drawString(g, iSTR_MINS, currentX, currentY);

		// sec
		currentX += drawNum(g, currentX, currentY, timeSec, 2, false, true,
				true);
		currentX += drawString(g, iSTR_SECONDS, currentX, currentY);

		return currentX - startX;
	}

	private int getTimeWidth(long timeMs) {

		long timeSecl = (timeMs / 1000);
		long timeMinl = timeSecl / 60;
		long timeHoursl = timeMinl / 60;
		long timeDaysl = timeHoursl / 24;

		int timeSec = (int) (timeSecl % 60);
		int timeMin = (int) (timeMinl % 60);
		int timeHours = (int) (timeHoursl % 24);
		int currentX = 0;

		// days...
		if (timeDaysl > 0) {
			currentX += getNumWidth(timeDaysl, 4, false, false,	false);
			currentX += getStringWidth(ALLSTRINGS[iSTR_DAYS], font);
		}

		if (timeHours > 0 || timeDaysl > 0) {
			// hours
			currentX += getNumWidth(timeHours, 2, false, true,true);
			currentX += getStringWidth(ALLSTRINGS[iSTR_HOURS], font);
		}

		// min
		currentX += getNumWidth(timeMin, 2, false, true, true);
		currentX += getStringWidth(ALLSTRINGS[iSTR_MINS], font);

		// sec
		currentX += getNumWidth(timeSec, 2, false, true,true);
		currentX += getStringWidth(ALLSTRINGS[iSTR_SECONDS], font);

		return currentX;
	}

	public int getNumWidth(long num, int numDigits, boolean bCommas,
			boolean bDrawLeadingZeros, boolean bKeepZeroSize) {
		int x = 0;
		int currentX = x;

		if(num < 0){
			num = Math.abs(num);
			currentX += (int)getStringWidth(ALLSTRINGS[iSTR_MINUS], font);
		}
		numDigits = Math.min(numDigits, NUMDIGITS);
		for (int n = 0; n < numDigits; n++) {
			digits[n] = (int) (num % 10);
			num = num / 10;
		}

		boolean bStillZeros = true;
		boolean bPreviousStillZero = true;
		for (int d = (numDigits - 1); d > 0; d--) {
			if (digits[d] != 0) {
				bStillZeros = false;
			}
			if ((!bDrawLeadingZeros) && bStillZeros) {
				// don't draw
			} else {
				if (((!bStillZeros) || (bDrawLeadingZeros))
						&& ((!bPreviousStillZero) || (bDrawLeadingZeros))
						&& bCommas && ((d % 3) == 2) && (d < (numDigits - 1))) {
					currentX += getStringWidth(ALLDIGITS[10], font);
				}

				if (bDrawLeadingZeros || (!bStillZeros)) {
					currentX += getStringWidth(ALLDIGITS[d], font);
				}
			}

			if (bStillZeros && bKeepZeroSize && (!bDrawLeadingZeros)) {
				currentX += getStringWidth(ALLDIGITS[d], font);
			}

			if (digits[d] != 0) {
				bPreviousStillZero = false;
			}
		}

		// draw the last digit ALWAYS
		currentX += getStringWidth(ALLDIGITS[0], font);
		return (currentX - x);
	}

	protected static int getProcessorCount(){
		return processorCount;
	}

	protected Config getServerSettings() {
		return serverSettings;
	}
}


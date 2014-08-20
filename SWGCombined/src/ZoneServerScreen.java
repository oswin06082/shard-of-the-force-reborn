import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;


public class ZoneServerScreen implements KeyListener, MouseListener, MouseMotionListener, Runnable {
	private GridElement[][] vAllGridElements;
	private ZoneServer server;
	private JFrame theFrame;
	private Font font;
	private FontMetrics fontMetrics;
	private Graphics2D g;
	private Graphics2D scaledBuffG;
	private Image scaledBuffImg;
	private Graphics2D buffG;
	private Image buffImg;
	//private Font normalFont;
	//private Font scaledFont;
	private int screenWidth = 800;
	private int screenHeight = 600;
	private int screenWidthDiv2 = screenWidth / 2;
	private int screenHeightDiv2 = screenHeight / 2;
	private int buffImgWidth = 1024;
	private int buffImgHeight = 1024;
	
	private Image[] planetMaps;
	private int iPlanetToView = Constants.CORELLIA;
	private Thread myThread;
	private boolean[] pressedKeys;
	private boolean[] pressedMouseButtons;
	private AffineTransform theBufferedTransform;
	private AffineTransform inverseBufferedTransform;
	private AffineTransform identity;
	private AffineTransform theTransform;
	private AffineTransform inverseTransform;
	private float scaleX;
	private float scaleY;
	private Grid[] zoneServerGrid;
	private boolean bStarted;
	
	private int numFrames = 0;
	private int mouseCursorX;
	private int mouseCursorY;
	private float mouseGameCursorX;
	private float mouseGameCursorY;
	private int gameState = 0;
	//private int previousState = 0;
	private int nextState = 0;
	private boolean bStateChangeNeeded = false;
	
	private static int colorChanges = 8; // ((targetFps * 2)/2);
	private static int colorChangesX2 = 16;
	private static int changePerFrame = 32; // (255 / colorChanges);
	private static int arrowColor;
	private static int arrowColor2;
	private static int colorPart;
	private static int colorPart2;

	//private final static int NUM_STATES = 9;
	private final static int STATE_LOADING = 0; // Might not be needed.
	private final static int STATE_PLACING_DYNAMIC_SPAWNS = 1;
	private final static int STATE_SHOWING_RESOURCES = 2;
	private final static int STATE_SHOWING_PLAYERS = 3;
	private final static int STATE_SHOWING_CREATURES = 4;
	private final static int STATE_PRE_MENU = 5;
	private final static int STATE_MENU = 6;
	private final static int STATE_SHOWING_BUILDINGS = 7;
	private final static int STATE_CHOOSE_DYNAMIC_SPAWN = 8;
	//private final static int STATE_SHOWING_DYNAMIC_SPAWN = 9;
	
	private final static byte iSTR_MENU_PLACE_DYNAMIC_SPAWNS = 0;
	//private final static byte iSTR_MENU_SHOW_DYNAMIC_SPAWNS = 1;
	private final static byte iSTR_MENU_SHOW_RESOURCES = 2;
	private final static byte iSTR_MENU_SHOW_PLAYERS = 3;
	private final static byte iSTR_MENU_SHOW_CREATURES = 4;
	private final static byte iSTR_MENU_SHOW_BUILDINGS = 5;
	private final static String[] MENU_STRINGS = {
		"Place Dynamic Spawns",
		"Show Dynamic Spawns",
		"Display Spawned Resources",
		"Display Players",
		"Display Creatures",
		"Display Structures",
		
	};
	
	private final static byte iSTR_MAX_NUM_LAIRS = 0;
	private final static byte iSTR_MIN_NUM_LAIRS = 1;
	private final static byte iSTR_NUM_PLAYERS_TO_TRIGGER = 2;
	private final static byte iSTR_RESPAWN_TIME_DELAY = 3;
	private final static byte iSTR_DAYS = 4;
	private final static byte iSTR_HOURS = 5;
	private final static byte iSTR_MINS = 6;
	private final static byte iSTR_SECONDS = 7;
	private final static byte iSTR_MINUS = 8;
	private final static String[] ALLSTRINGS = {
		"Max number of lairs: ",
		"Min number of lairs: ",
		"Number of players to trigger: ",
		"Respawn delay: ",
		"d:",
		"h:",
		"m:",
		"s:",
		"-",
	};
	
	private final static String[] ALLDIGITS = {
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ","
	};
	private byte longestMenuStringChars = 1;
	private final static int fontSize = 12;
	private int fontHeight;
	//private int fontHeightDiv2;
	//private int fontWidth;
	//private int fontWidthDiv2;
	private Vector<NPC>[] vCreaturesSortedByPlanet;
	private Vector<SpawnedResourceData>[] vResourcesSortedByPlanet;
	private Vector<Player>[] vPlayersSortedByPlanet;
	private Vector<Structure>[] vStructuresSortedByPlanet;
	private static Vector<LairTemplate>[] vLairTemplates;
	private final static String loadingString = "Loading...";
	
	public ZoneServerScreen(ZoneServer server) {
		this.server = server;
		font = new Font("Serif", Font.PLAIN, fontSize);
		SPACE_WIDTH = (int)getStringWidth(" ", font);
		//scaledFont = new Font("Times New Roman", 120 * 16, Font.PLAIN);
		zoneServerGrid = new Grid[Constants.PlanetNames.length - 1];
		for (int i = 0; i < zoneServerGrid.length; i++) {
			zoneServerGrid[i] = server.getGrid(i);
		}
		Toolkit defToolKit = Toolkit.getDefaultToolkit();
		planetMaps = new Image[10];
		planetMaps[Constants.CORELLIA] = defToolKit
				.createImage("images/ui_map_corellia.jpg");
		planetMaps[Constants.DANTOOINE] = defToolKit
				.createImage("images/ui_map_dantooine.jpg");
		planetMaps[Constants.DATHOMIR] = defToolKit
				.createImage("images/ui_map_dathomir.jpg");
		planetMaps[Constants.ENDOR] = defToolKit
				.createImage("images/ui_map_endor.jpg");
		planetMaps[Constants.LOK] = defToolKit
				.createImage("images/ui_map_lok.jpg");
		planetMaps[Constants.NABOO] = defToolKit
				.createImage("images/ui_map_naboo.jpg");
		planetMaps[Constants.RORI] = defToolKit
				.createImage("images/ui_map_rori.jpg");
		planetMaps[Constants.TALUS] = defToolKit
				.createImage("images/ui_map_talus.jpg");
		planetMaps[Constants.TATOOINE] = defToolKit
				.createImage("images/ui_map_tatooine.jpg");
		planetMaps[Constants.YAVIN] = defToolKit
				.createImage("images/ui_map_yavin4.jpg");

		//for (int i = 0; i < planetMaps.length; i++) {
		//	planetMaps[i] = planetMaps[i].getScaledInstance(16384, 16384, Image.SCALE_DEFAULT); // Resize the image to it's proper 1 pixel per meter size.
		//}
		theFrame = new JFrame("Zone Server " + server.getClusterName());
		theFrame.setSize(screenWidth, screenHeight);
		theFrame.validate();
		theFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		theFrame.setIgnoreRepaint(true);
		theFrame.setVisible(true);
		theFrame.setResizable(false);
		theFrame.setSize(screenWidth, screenHeight);
		theFrame.validate();
		theFrame.toFront();
		g = (Graphics2D) theFrame.getContentPane().getGraphics();
		g.setFont(font);
		scaledBuffImg = theFrame.createImage(buffImgWidth, buffImgHeight);
		scaledBuffG = (Graphics2D) scaledBuffImg.getGraphics();
		fontMetrics = g.getFontMetrics(font);
		fontHeight = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
		//fontHeightDiv2 = fontHeight / 2;
		//fontWidth = fontMetrics.getMaxAdvance();
		//fontWidthDiv2 = fontWidth / 2;
		//System.out.println("Font metrics retrieved.  Width["+fontWidth+"], height["+fontHeight+"]");
		pressedKeys = new boolean[(int)Character.MAX_VALUE + 1];
		pressedMouseButtons = new boolean[4]; // 3 button support.
		theFrame.addKeyListener(this);
		theFrame.addMouseListener(this);
		theFrame.addMouseMotionListener(this);
		if (vLairTemplates == null) {
			vLairTemplates = new Vector[Constants.PlanetNames.length - 1];
			vCreaturesSortedByPlanet = new Vector[Constants.PlanetNames.length - 1];
			vPlayersSortedByPlanet = new Vector[Constants.PlanetNames.length - 1];
			vResourcesSortedByPlanet = new Vector[Constants.PlanetNames.length - 1];
			vStructuresSortedByPlanet = new Vector[Constants.PlanetNames.length - 1];
			vLairSpawns = new Vector[Constants.PlanetNames.length - 1];
			for (int i = 0; i < vLairTemplates.length; i++) {
				vLairTemplates[i] = new Vector<LairTemplate>();
				vCreaturesSortedByPlanet[i] = new Vector<NPC>();
				vPlayersSortedByPlanet[i] = new Vector<Player>();
				vStructuresSortedByPlanet[i] = new Vector<Structure>();
				try {
					vResourcesSortedByPlanet[i] = server.getResourceManager().getResourcesByPlanetID(i);
				} catch (NullPointerException e) {
					// D'oh!
				}
				vLairSpawns[i] = server.getLairSpawnForPlanet(i);
			}
		}
		myThread = new Thread(this);
		myThread.setName("ZoneServerScreen thread");
	}
	
	private boolean bIsHoldingShift = false;
	private boolean bIsHoldingAlt = false;
	private boolean bIsHoldingCtrl = false;
	public void keyPressed(KeyEvent arg0) {
		pressedKeys[arg0.getKeyCode()] = true;
		bIsHoldingShift = arg0.isShiftDown();
		bIsHoldingAlt = arg0.isAltDown();
		bIsHoldingCtrl = arg0.isControlDown();
	}

	public void keyReleased(KeyEvent arg0) {
		pressedKeys[arg0.getKeyCode()] = false;
		
	}

	public void keyTyped(KeyEvent arg0) {

	}
	private boolean bDragging = false;
	public void mouseDragged(MouseEvent m) {
		// This, presumably, occurs if a mouse button is clicked, and the mouse moves.
		bDragging = true;
		if (isMouseButtonPressed(MouseEvent.BUTTON1)) {
			if (mouseDraggedRectangleDimensions == null) {
				mouseDraggedRectangleDimensions = new Rectangle2D.Float(m.getX(), m.getY() - 20, 0, 0);
			} else {
				float leftX = (float)mouseDraggedRectangleDimensions.getX();
				float topY = (float)mouseDraggedRectangleDimensions.getY();
				float width = m.getX() - leftX;
				float height = m.getY() - 20 - topY;
				
				mouseDraggedRectangleDimensions.setRect(leftX, topY, width, height);
			}
		}
	}
	
	public boolean isMouseButtonPressed(int buttonID) {
		return pressedMouseButtons[buttonID];
	}
	
	private Point2D mousePoint;
	public void mouseMoved(MouseEvent m) {
		// This presumably happens if the mouse moves when a mouse button is NOT clicked.
		mouseCursorX = m.getX();
		mouseCursorY = m.getY() - 20;
		mousePoint = new Point();
		mousePoint.setLocation(mouseCursorX, mouseCursorY);
		if (theTransform != null) {
			Point2D imagePoint = theTransform.transform(mousePoint, null);
			Point2D gamePoint = inverseBufferedTransform.transform(imagePoint, null);
			mouseGameCursorX = (float)gamePoint.getX();
			mouseGameCursorY = (float)gamePoint.getY();
			elementToDisplay = zoneServerGrid[iPlanetToView].getNearestElement(mouseGameCursorX, mouseGameCursorY);
		} 
		//System.out.println("Mouse game location: x="+mouseGameCursorX + ". y="+mouseGameCursorY);
		//System.out.println("Window location: x="+mouseCursorX+ ", y="+mouseCursorY);
	}
	
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		// We clicked.
		mouseDraggedRectangleDimensions = new Rectangle2D.Float(arg0.getX(), arg0.getY() - 20, 0, 0);
		Point2D clickLocation = arg0.getPoint();
		clickLocation.setLocation(clickLocation.getX(), (clickLocation.getY() - 20));
		//Point2D imageClickLocation = theTransform.transform(clickLocation, null);
		//Point2D gameClickLocation = inverseBufferedTransform.transform(imageClickLocation, null);
		//System.out.println("Click at x="+clickLocation.getX() + ", y="+(clickLocation.getY()) + ".  In-game location: x=" + gameClickLocation.getX() +", y=" + gameClickLocation.getY());
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
		pressedMouseButtons[arg0.getButton()] = true;
		
	}

	private Rectangle2D dynamicLairRectangleSelection = null;
	public void mouseReleased(MouseEvent arg0) {
		
		// TODO Auto-generated method stub
		
		pressedMouseButtons[arg0.getButton()] = false;
		if (bDragging) {
			bDragging = false;
			int leftX = (int)mouseDraggedRectangleDimensions.getX();
			int topY = (int)mouseDraggedRectangleDimensions.getY();
			int width = (int)mouseDraggedRectangleDimensions.getWidth();
			int height = (int)mouseDraggedRectangleDimensions.getHeight();
			int leftmostX = leftX;
			int topmostY = topY;
			int rightmostX;
			int bottommostY;
			if (width < 0) {
				leftmostX = leftX - width;
				rightmostX = leftX;
			} else {
				rightmostX = leftX + width;
			}
			if (height < 0) {
				topmostY = topY + height;
				bottommostY = topY;
			} else {
				bottommostY = topY + height;
			}
			
			float gameLeftX;
			float gameRightX;
			float gameTopY;
			float gameBottomY;
			float gameWidth;
			float gameHeight;
			// Run them through theTransform, then run that through inverseBufferedTransform
			Point2D tL;
			Point2D tR;
			//Point2D bL;
			Point2D bR;
			tL = new Point2D.Float(leftmostX, topmostY);
			tR = new Point2D.Float(rightmostX, topmostY);
			//bL = new Point2D.Float(leftmostX, bottommostY);
			bR = new Point2D.Float(rightmostX, bottommostY);
			
			Point2D imageTL = theTransform.transform(tL, null);
			Point2D imageTR = theTransform.transform(tR, null);
			//Point2D imageBL = theTransform.transform(bL, null);
			Point2D imageBR = theTransform.transform(bR, null);
			
			Point2D gameTL = inverseBufferedTransform.transform(imageTL, null);
			Point2D gameTR = inverseBufferedTransform.transform(imageTR, null);
			//Point2D gameBL = inverseBufferedTransform.transform(imageBL, null);
			Point2D gameBR = inverseBufferedTransform.transform(imageBR, null);
			
			gameLeftX = (float)gameTL.getX();
			gameTopY = (float)gameTL.getY();
			gameRightX = (float)gameTR.getX();
			gameBottomY = (float)gameBR.getY();
			gameWidth = gameRightX - gameLeftX;
			gameHeight = gameTopY - gameBottomY;
			
			if (gameWidth < 0) {
				float tempX = gameLeftX;
				gameLeftX = gameRightX;
				gameRightX = tempX;
				gameWidth = gameRightX - gameLeftX;
			}
			
			if (gameHeight < 0) {
				float tempY = gameTopY;
				gameTopY = gameBottomY;
				gameBottomY = tempY;
				gameHeight = gameTopY - gameBottomY;
			}
			dynamicLairRectangleSelection = new Rectangle2D.Float(gameLeftX, gameBottomY, gameWidth, gameHeight);
			
			mouseDraggedRectangleDimensions = null;
			
			if (gameState == STATE_PLACING_DYNAMIC_SPAWNS) {
				placeDynamicSpawns(dynamicLairRectangleSelection);
			}
		}
	}

	private int lastScreenWidth;
	private int lastScreenHeight;
	private long lDeltaTimeMS;
	private long lCurrentTimeMS;
	private long lLastTimeMS;
	public void run() {
		lLastTimeMS = System.currentTimeMillis();
		while (myThread != null) {
			try {
				synchronized(this) {
					Thread.yield();
					if (theFrame.isVisible() == false) {
						wait();
					} else {
						wait(100);
					}
				}
				// Do stuff
				
				lCurrentTimeMS = System.currentTimeMillis();
				lDeltaTimeMS = lCurrentTimeMS - lLastTimeMS;
				lLastTimeMS = lCurrentTimeMS;
				update(lDeltaTimeMS);
				
				drawScaled(scaledBuffG);
				// Scale the image.
				// Then, draw the menu, if necessary.
				// This will be fugly, but oh well.
				Image tempImage = scaledBuffImg.getScaledInstance(screenWidth, screenHeight, Image.SCALE_REPLICATE);  // Why aren't you properly scaling????
				//System.out.println("Scaled the map.  Size: x["+buffImg.getWidth(null) + "],y["+buffImg.getHeight(null)+"], screenWidth["+screenWidth+"], screenHeight["+screenHeight+"]");
				//scaledBuffG.setColor(Color.WHITE);
				buffG.fillRect(0, 0, buffImgWidth, buffImgHeight);
				buffG.drawImage(tempImage, 0, 0, screenWidth, screenHeight, null);
				
				drawUnscaled(buffG);
				//g.setClip(0, 0, screenWidth, screenHeight);
				g.drawImage(buffImg, 0, 0, null); // Does this scale the image?
				
				//g.drawImage(buffImg, 0, 0, buffImg.getWidth(null), buffImg.getHeight(null),null);
				//g.setFont(normalFont);
				//g.setTransform(theTransform);
				//g.setTransform(identity);
				//Image drawImage = buffImg.getScaledInstance(600, -1, Image.SCALE_REPLICATE);
			} catch (Exception e) {
				System.out.println("Exploded running ZoneServerScreen: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	private boolean isKeyPressed(int iKeyCode){
		if( (iKeyCode >= pressedKeys.length) || (iKeyCode <0) ){
			return false;
		}
		return pressedKeys[iKeyCode];
	}

	// Draw coordinate:  0 0 on screen = -8192, 8192 on the actual planet.
	// 512 512 on screen = 0 0 on planet
	// 1024 1024 on screen = 8192 -8192 on planet.
	// For each pixel right on the screen, we increase the actual X by 16 metres.
	// For each pixel down on the screen, we decrease the actual Y by 16 metres.
	// So, in-game X = (16 * screenX) -8192;
	// in-game Y = 8192 - (16 * screenY);
	
	//(inGameX + 8192) = (16 * screenX);
	//screenX = ((inGameX + 8192) / 16)
	
	// inGameY - 8192 = -(16 * screenY);
	// (-1 * (8192 - inGameY)) = 16screenY;
	// (-1 * (8192 - inGameY)) / 16 = screenY

	// Can we transform that?  Probably.

	private void update(long lDeltaTimeMS) {
		if (theFrame.isValid()) {
			if (theFrame.isVisible()) {
				numFrames++;
				if (bStateChangeNeeded) {
					//previousState = gameState;
					gameState = nextState;
					bStateChangeNeeded = false;
				}
				colorPart = (numFrames % (colorChangesX2));
				if (colorPart > colorChanges) {
					colorPart = ((colorChangesX2) - colorPart) * changePerFrame;
				} else {
					colorPart = colorPart * changePerFrame;
				}

				colorPart2 = ((numFrames + (colorChanges)) % (colorChangesX2));
				if (colorPart2 > colorChanges) {
					colorPart2 = ((colorChangesX2) - colorPart2) * changePerFrame;
				} else {
					colorPart2 = colorPart2 * changePerFrame;
				}

				arrowColor = (colorPart << 8);
				arrowColor = arrowColor + (colorPart << 16);
				arrowColor = arrowColor + (colorPart << 24);

				arrowColor2 = (colorPart2 << 8);
				arrowColor2 = arrowColor2 + (colorPart2 << 16);
				arrowColor2 = arrowColor2 + (colorPart2 << 24);
				
				screenWidth = theFrame.getWidth();
				screenHeight =  theFrame.getHeight();
				if (screenWidth != lastScreenWidth || screenHeight != lastScreenHeight) {
					if (screenWidth < 300 || screenHeight < 300) {
						if (screenWidth < 300) {
							screenWidth = 300;
						}
						if (screenHeight < 300) {
							screenHeight = 300;
						}
					}
					buffImg = theFrame.createImage(screenWidth, screenHeight);
					buffG = (Graphics2D)buffImg.getGraphics();
					lastScreenWidth = screenWidth;
					lastScreenHeight =screenHeight;
					theFrame.setSize(screenWidth, screenHeight);
					screenWidthDiv2 = screenWidth / 2;
					screenHeightDiv2 = screenHeight / 2;
					// This needs reworked.
					scaleX = (float)screenWidth  / (float)buffImgWidth; 
					scaleY = (float)screenHeight / (float)buffImgHeight;
					//scaleX = (float)mapImageDimension / (float)screenWidth;
					//scaleY = (float)mapImageDimension / (float)screenHeight;
					theTransform = new AffineTransform();
					theTransform.setToScale(1.0 / scaleX, 1.0 / scaleY);
					//System.out.println("Screen resized -- new transform scale: xScale = " + (1.0 / scaleX) + ", yScale = " + (1.0 / scaleY));
					
					theBufferedTransform = new AffineTransform();
					//theTransform.setToScale(1.0 / scaleX, -1.0 / scaleY);
					theBufferedTransform.setToScale(1.0 / 16.0, -1.0 / 16.0);
					theBufferedTransform.translate(8192.0f, -8192.0f);
					try {
						inverseBufferedTransform = theBufferedTransform.createInverse();

					} catch (Exception e) {
						inverseBufferedTransform = new AffineTransform();
						inverseBufferedTransform.setToScale(-16.0, 16.0);
						inverseBufferedTransform.translate(-8192.0f, 8192.0f);
					}
					try {
						inverseTransform = theTransform.createInverse();
					} catch (Exception e) {
						inverseTransform = new AffineTransform();
						inverseTransform.setToScale(scaleX, scaleY);
					}
				}
			}
		}
		if (isKeyPressed(KeyEvent.VK_Q)) {
			bChangedPlanet = true;
			iPlanetToView --;
		} else if (isKeyPressed(KeyEvent.VK_W)) {
			bChangedPlanet = true;
			iPlanetToView++;
		}
		if (bChangedPlanet || lairTemplateBubbleWidth == 0) {
			iPlanetToView = (iPlanetToView + planetMaps.length) % planetMaps.length;
			vLairSpawns[iPlanetToView] = server.getLairSpawnForPlanet(iPlanetToView);
			Vector<LairTemplate> lairs = vLairTemplates[iPlanetToView];
			for (int i = 0; i < lairs.size(); i++) {
				LairTemplate lairTemplate = lairs.elementAt(i);
				String sLairType = server.getTemplateData(lairTemplate.getIMob1Template()).getIFFFileName();
				lairTemplateBubbleWidth = Math.max(
						menuBubbleWidth, 
						getStringWidth(sLairType, font));
			}
		}
		switch (gameState) {
			case STATE_PLACING_DYNAMIC_SPAWNS: {
				updatePlaceDynamicSpawns();
				break;
			}
			case STATE_LOADING: {
				updateLoading();
				break;
			}
			case STATE_SHOWING_CREATURES: {
				updateShowCreatures();
				break;
			}
			case STATE_SHOWING_PLAYERS: {
				updateShowPlayers();
				break;
			}
			case STATE_SHOWING_RESOURCES: {
				updateShowResources();
				break;
			}
			case STATE_PRE_MENU: {
				updatePreMenu();
				break;
			}
			case STATE_MENU: {
				updateMenu();
				break;
			}
			case STATE_CHOOSE_DYNAMIC_SPAWN: {
				updateChooseDynamicSpawn();
				break;
			}
			default: {
				
				break;
			}
		}
		bChangedPlanet = false;
	}
	
	private boolean bChangedPlanet = false;
	//private boolean bFirstDraw = true;
	private void drawScaled(Graphics2D g) {
		// This always happens no matter what.
		g.setColor(ColorManager.getColor(0xFFFFFF));
		g.setTransform(identity);
		//g.setFont(normalFont);
		//g.setClip(0,0, buffImgWidth, buffImgHeight);
		g.fillRect(0, 0, buffImgWidth, buffImgHeight);
		//g.setClip(100, 100, screenWidth - 200, screenHeight - 200);
		int currentX = 0;
		int currentY = 0;
		g.drawImage(planetMaps[iPlanetToView], currentX, currentY, null);
		g.setColor(ColorManager.getColor(arrowColor));
		g.setTransform(theBufferedTransform); // We need to fiddle with theTransform -- the planet map is 1024 x 1024 always.
		//g.setFont(scaledFont);
		switch (gameState) {
			case STATE_PLACING_DYNAMIC_SPAWNS: {
				drawPlaceDynamicSpawnsScaled(g);
				break;
			}
			case STATE_LOADING: {
				drawLoading(g);
				break;
			}
			case STATE_SHOWING_CREATURES: {
				drawShowCreatures(g);
				break;
			}
			case STATE_SHOWING_PLAYERS: {
				drawShowPlayers(g);
				break;
			}
			case STATE_SHOWING_RESOURCES: {
				drawShowResources(g);
				break;
			}
			default: {
				//System.out.println("Update unknown state " + gameState);
				break;
			}
		}

	}
	
	private void drawUnscaled(Graphics2D g) {
		g.setTransform(identity);
		switch (gameState) {
			case STATE_PRE_MENU: {
				drawPreMenu(g);
				break;
			}
			case STATE_MENU: {
				drawMenu(g);
				break;
			}
			case STATE_CHOOSE_DYNAMIC_SPAWN: {
				drawChooseDynamicSpawn(g);
				break;
			}
			case STATE_PLACING_DYNAMIC_SPAWNS: {
				drawPlaceDynamicSpawnsUnscaled(g);
				break;
			}
		}
	}

	private boolean bInitialized = false;
	public void initialize() {
		if (!bInitialized) {
			theFrame.setSize(screenWidth, screenHeight);
			theFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			theFrame.setIgnoreRepaint(true);
			theFrame.setResizable(false);
			theFrame.validate();
			theFrame.setLocation(100, 100);
			scaleX = (float)screenWidth  / (float)buffImgWidth; 
			scaleY = (float)screenHeight / (float)buffImgHeight;
			//scaleX = (float)mapImageDimension / (float)screenWidth;
			//scaleY = (float)mapImageDimension / (float)screenHeight;
			theTransform = new AffineTransform();
			theTransform.setToScale(1.0 / scaleX, 1.0 / scaleY);
			//System.out.println("Screen resized -- new transform scale: xScale = " + (1.0 / scaleX) + ", yScale = " + (1.0 / scaleY));
			//System.out.println("New screen size: width = " + screenWidth + ", height = " + screenHeight);
			theBufferedTransform = new AffineTransform();
			theBufferedTransform.setToScale(1.0 / 16.0, -1.0 / 16.0);
			theBufferedTransform.translate(8192.0f, -8192.0f);
			try {
				inverseBufferedTransform = theBufferedTransform.createInverse();
			} catch (Exception e) {
				inverseBufferedTransform = new AffineTransform();
				inverseBufferedTransform.setToScale(-16.0, 16.0);
				inverseBufferedTransform.translate(-8192.0f, 8192.0f);
			}
			identity = new AffineTransform();
			identity.setToIdentity();
			bInitialized = true;
		}
		theFrame.setVisible(true);
		theFrame.toFront();
		if (!bStarted) {
			bStarted = true;
			myThread.start();
		}
	}
	
	private GridElement elementToDisplay;
	private int gridDimension;
	private int gridElementIndexX;
	private int gridElementIndexY;
	private Rectangle2D mouseDraggedRectangleDimensions;
	private int iMinNumDynamicLairsToSpawn = 1;
	private int iMaxNumDynamicLairsToSpawn = 1;
	private int iMinNumPlayersToTriggerDynamicLairSpawn = 1;
	private long lDynamicLairRespawnTimeMS = 5000;
	private Vector<DynamicLairSpawn>[] vLairSpawns;
	private void updatePlaceDynamicSpawns() {
		int gridMaxIndex = zoneServerGrid[iPlanetToView].getGridCount();
		gridElementIndexX = (gridElementIndexX + gridMaxIndex) % gridMaxIndex;
		gridElementIndexY = (gridElementIndexY + gridMaxIndex) % gridMaxIndex;
		elementToDisplay = zoneServerGrid[iPlanetToView].getElement(gridElementIndexX, gridElementIndexY);
		if (elementToDisplay == null) {
			gridElementIndexX = 0;
			gridElementIndexY = 0;
			elementToDisplay = zoneServerGrid[iPlanetToView].getElement(gridElementIndexX, gridElementIndexY);
		}
		
		gridDimension = zoneServerGrid[iPlanetToView].getDimension();
		if (isKeyPressed(KeyEvent.VK_F1)) {
			changeState(STATE_CHOOSE_DYNAMIC_SPAWN);
		} else if (isKeyPressed(KeyEvent.VK_F2)) {
			changeState(STATE_PRE_MENU);
		} else if (isKeyPressed(KeyEvent.VK_UP)) {
			gridElementIndexY --;
		} else if (isKeyPressed(KeyEvent.VK_DOWN)) {
			gridElementIndexY ++;
		} else if (isKeyPressed(KeyEvent.VK_LEFT)) {
			gridElementIndexX--;
		} else if (isKeyPressed(KeyEvent.VK_RIGHT)) {
			gridElementIndexX++;
			
		} else if (isKeyPressed(KeyEvent.VK_EQUALS)) {
			
			// Shift + -- increase maximum number of lairs.
			// Control + -- increase the number of players to trigger.
			// Alt + -- increase the time period between respawns.
			// + -- increase the minimum number of lairs.
			if (bIsHoldingShift) {
				iMaxNumDynamicLairsToSpawn++;
				
			} else if (bIsHoldingCtrl) {
				iMinNumPlayersToTriggerDynamicLairSpawn++;
				
			} else if (bIsHoldingAlt) {
				lDynamicLairRespawnTimeMS += 1000;
				
			} else {
				iMinNumDynamicLairsToSpawn++;
				iMaxNumDynamicLairsToSpawn = Math.max(iMinNumDynamicLairsToSpawn, iMaxNumDynamicLairsToSpawn);
				
			}
		} else if (isKeyPressed(KeyEvent.VK_MINUS)) {
			if (bIsHoldingShift) {
				iMaxNumDynamicLairsToSpawn--;
				if (iMaxNumDynamicLairsToSpawn < 1) {
					iMaxNumDynamicLairsToSpawn=1;
				}
			} else if (bIsHoldingCtrl) {
				iMinNumPlayersToTriggerDynamicLairSpawn = Math.max(iMinNumPlayersToTriggerDynamicLairSpawn-1, 1);
			} else if (bIsHoldingAlt) {
				lDynamicLairRespawnTimeMS = Math.max(lDynamicLairRespawnTimeMS - 1000, 5000);
			} else {
				iMinNumDynamicLairsToSpawn--;
				iMinNumDynamicLairsToSpawn = Math.max(iMinNumDynamicLairsToSpawn,1);
			}
		} else if (isKeyPressed(KeyEvent.VK_ENTER)) {
			DynamicLairSpawn spawn = elementToDisplay.getDynamicLairSpawnByTemplateID((short)lairTemplate.getILairTemplate());
			
			if (spawn == null) {
				spawn = new DynamicLairSpawn(server);
				spawn.setColor(SWGGui.getRandomInt(0xFFFFFF));
				// Set up a bunch of other stuff.
				
				elementToDisplay.addDynamicLairSpawn(spawn);
				
			} else {
				// Get and update with new values.
			}
			
		} 
	}
	
	private void drawPlaceDynamicSpawnsScaled(Graphics2D g) {
		try {
			g.setTransform(theBufferedTransform);
			g.setColor(ColorManager.getColor(0x000000));
			if (elementToDisplay != null) {
				int leftX = (int)elementToDisplay.getX();
				int topY = (int)elementToDisplay.getY();
				g.fillRect(leftX, topY, gridDimension, gridDimension);
			}
			for (int i = 0; i < vLairSpawns[iPlanetToView].size(); i++) {
				DynamicLairSpawn spawn = vLairSpawns[iPlanetToView].elementAt(i);
				if ((numFrames % 2) != 0) {
					g.setColor(ColorManager.getColor(spawn.getColor()));
					Rectangle2D rectangle = spawn.getBoundaries();
					g.fillRect((int)rectangle.getX(), (int)rectangle.getY(), (int)rectangle.getWidth(), (int)rectangle.getHeight());  
				}
			}
		} catch (Exception e) {
			System.out.println("Error drawing " + e.toString());
			e.printStackTrace();
		}
	}
	
	private final int SPACE_WIDTH;
	private void drawPlaceDynamicSpawnsUnscaled(Graphics2D g) {
		try {
			g.setTransform(identity);
			int currentX = 10;
			int currentY = 30;
			int bubbleHeight = (int)(4.0f * getStringHeight(ALLSTRINGS[iSTR_NUM_PLAYERS_TO_TRIGGER], font));
			int bubbleWidth = (int)getStringWidth(ALLSTRINGS[iSTR_NUM_PLAYERS_TO_TRIGGER], font) 
				+ getNumWidth(iMinNumPlayersToTriggerDynamicLairSpawn, 3, true, false, false)
				+ 10; // I want more padding.
			if (!bDragging) {
				drawBubble(g, currentX + bubbleWidth /2, currentY + bubbleHeight / 2, bubbleWidth, bubbleHeight, 0xFFFFFF, 0x000000, 0xFF0000, 3, 5);
				currentY += (int)getStringHeight(ALLSTRINGS[iSTR_MAX_NUM_LAIRS], font);
				g.setColor(ColorManager.getColor(0x0000FF));
				currentX += drawString(g, iSTR_MAX_NUM_LAIRS, currentX, currentY) + SPACE_WIDTH;
				drawNum(g, currentX, currentY, iMaxNumDynamicLairsToSpawn, 3, true, false, false);
				currentY += (int)getStringHeight(ALLSTRINGS[iSTR_MIN_NUM_LAIRS], font);
				currentX = 10;
				currentX += drawString(g, iSTR_MIN_NUM_LAIRS, currentX, currentY) + SPACE_WIDTH;
				drawNum(g, currentX, currentY, iMinNumDynamicLairsToSpawn, 3, true, false, false);
				currentY += getStringHeight(ALLSTRINGS[iSTR_NUM_PLAYERS_TO_TRIGGER], font);
				currentX = 10;
				currentX += drawString(g, iSTR_NUM_PLAYERS_TO_TRIGGER, currentX, currentY) + SPACE_WIDTH;
				drawNum(g, currentX, currentY, iMinNumPlayersToTriggerDynamicLairSpawn, 2, true, false, false);
				currentY += getStringHeight(ALLSTRINGS[iSTR_NUM_PLAYERS_TO_TRIGGER], font);
				currentX = 10;
				currentX += drawString(g, iSTR_RESPAWN_TIME_DELAY, currentX, currentY) + SPACE_WIDTH;
				drawTime(g, lDynamicLairRespawnTimeMS, currentX, currentY);
			} else {
				float leftX = (float)mouseDraggedRectangleDimensions.getX();
				float topY = (float)mouseDraggedRectangleDimensions.getY();
				float width = (float)mouseDraggedRectangleDimensions.getWidth();
				float height = (float)mouseDraggedRectangleDimensions.getHeight();
				float mostLeftDraw = Math.min(leftX, leftX + width);
				float mostTopDraw = Math.min(topY, topY + height);
				g.setColor(ColorManager.getColor(0xFFFF00));
				g.drawRect((int)mostLeftDraw,
						(int)mostTopDraw,
						(int)Math.abs(width),
						(int)Math.abs(height));
			}
		} catch (Exception e) {
			System.out.println("Error drawing unscaled place dynamic spawns: " + e.toString());
			e.printStackTrace();
		}
	}
	private void changeState(int iState) {
		nextState = iState;
		bStateChangeNeeded = true;
	}
	
	private int drawString(Graphics2D g, String str, int x, int y) {
		int stringWidth = (int)getStringWidth(str, font);
		g.drawString(str, x, y);
		return stringWidth;
	}
	
	private int drawString(Graphics2D g, byte iStringIndex, int x, int y) {
		String s = ALLSTRINGS[iStringIndex];
		return drawString(g, s, x, y);
	}
	
	private void updateShowCreatures() {
		if (numFrames % 10 == 0) {
			for (int i = 0; i < vAllGridElements.length; i++) {
				for (int j = 0; j < vAllGridElements[i].length; j++) {
					Enumeration<SOEObject> vCreaturesThisElement = vAllGridElements[i][j].getAllObjectContained().elements();
					while (vCreaturesThisElement.hasMoreElements()) {
						SOEObject o = vCreaturesThisElement.nextElement();
						if (o instanceof NPC) {
							NPC npc = (NPC) o;
							if (!vCreaturesSortedByPlanet[iPlanetToView].contains(npc)) {
								vCreaturesSortedByPlanet[iPlanetToView].add(npc);
							}
						}
					}
				}
			}
		}
		
	}
	
	private void updateShowPlayers() {
		if (numFrames % 10 == 0) {
			for (int i = 0; i < vAllGridElements.length; i++) {
				for (int j = 0; j < vAllGridElements[i].length; j++) {
					Vector<Player> vCreaturesThisElement = vAllGridElements[i][j].getAllPlayersContained();
					for (int k = 0; k < vCreaturesThisElement.size(); k++) {
						Player player = vCreaturesThisElement.elementAt(i);
						if (!vPlayersSortedByPlanet[iPlanetToView].contains(player)) {
							vPlayersSortedByPlanet[iPlanetToView].add(player);
						}
					}
				}
			}
		}
	}
	
	private void updateShowResources() {
		if (numFrames % 1000 == 0 || vResourcesSortedByPlanet == null || bChangedPlanet) {
			vResourcesSortedByPlanet[iPlanetToView] = server.getResourceManager().getResourcesByPlanetID(iPlanetToView);
		}
	}
	
	private void updateLoading() {
		changeState(STATE_PRE_MENU);
	}
	
	private boolean bDrawnPreMenu = false;
	private void updatePreMenu() {
		if (!bDrawnPreMenu) {
			bDrawnPreMenu = true;
		} else {
			SOEObject o = null;
			try {
				// Load the Vectors.
				ConcurrentHashMap<Long, SOEObject> vAllObjectsOnServer = server.getAllObjects();
				Enumeration<SOEObject> vObjectEnum = vAllObjectsOnServer.elements();
				while (vObjectEnum.hasMoreElements()) {
					o = vObjectEnum.nextElement();
					int planetID = o.getPlanetID();
					if (planetID != 255) {
						if (o instanceof Player && !(o instanceof NPC)) {
							vPlayersSortedByPlanet[planetID].add((Player)o);
						} else if (o instanceof NPC) {
							vCreaturesSortedByPlanet[planetID].add((NPC)o);
						} else if (o instanceof Structure) {
							vStructuresSortedByPlanet[planetID].add((Structure)o);
						}
					}
				}
				for (int i = 0; i < vLairTemplates.length; i++) {
					vLairTemplates[i] = server.getLairTemplatesForPlanet(i, false);
					//iNumLairTemplates = vLairTemplates[i].size();
				}
				for (int i = 0; i < MENU_STRINGS.length; i++) {
					menuBubbleWidth = Math.max(
							menuBubbleWidth, 
							getStringWidth(MENU_STRINGS[longestMenuStringChars], font));
				}
				menuBubbleWidth += 24;
				//System.out.println("Change to menu.");
				changeState(STATE_MENU);
			} catch (Exception e) {
				if (o != null) {
					
				}
				System.out.println("Exploded in preMenu: " + e.toString());
				e.printStackTrace();
			}
		}
	}
	
	private void drawShowCreatures(Graphics2D g) {
		g.setColor(ColorManager.getColor(0xFF0000));
		for (int i = 0; i < vCreaturesSortedByPlanet[iPlanetToView].size(); i++) {
			NPC npc = vCreaturesSortedByPlanet[iPlanetToView].get(i);
			g.fillArc((int)npc.getX() - 16, (int)npc.getY() + 16, 32, 32, 0, 360);
		}
	}
	
	private void drawShowPlayers(Graphics2D g) {
		g.setColor(ColorManager.getColor(0x0000FF));
		for (int i = 0; i < vPlayersSortedByPlanet[iPlanetToView].size(); i++) {
			Player player = vPlayersSortedByPlanet[iPlanetToView].get(i);
			g.fillArc((int)player.getX() - 16, (int)player.getY() + 16, 32, 32, 0, 360);
		}
		
	}
	
	private void drawShowResources(Graphics2D g) {
		try {
			int resourceRadius;
			int currentX;
			int currentY;
			int resourceRadiusDiv2;
			for (int i = 0; i < vResourcesSortedByPlanet[iPlanetToView].size(); i++){
				SpawnedResourceData resource = vResourcesSortedByPlanet[iPlanetToView].elementAt(i);
				Vector<ResourceSpawnCoordinateData> vCoordinates = resource.getCoordinates();
				Color drawColor = ColorManager.getColor(resource.getDrawColor());
				
				for (int j = 0; j < vCoordinates.size(); j++) {
					ResourceSpawnCoordinateData coords = vCoordinates.elementAt(i);
					g.setColor(drawColor);
					resourceRadius = (int)coords.getSpawnRadius();
					resourceRadiusDiv2 = resourceRadius / 2;
					currentX = (int)coords.getSpawnX() - (resourceRadiusDiv2);
					currentY = (int)coords.getSpawnY() - (resourceRadiusDiv2);
					g.fillArc(currentX, currentY, resourceRadius, resourceRadius, 0, 360);
					g.setColor(Color.WHITE);
					g.drawString(resource.getName(), currentX, currentY); // We need to scale the font size.
				}
			}
		} catch (Exception e) {
			System.out.println("Error drawing resource spawns " + e.toString() );
			e.printStackTrace();
		}
	}

	private void drawLoading(Graphics2D g) {
		
	}
	
	private float menuBubbleWidth = 0;
	private float lairTemplateBubbleWidth = 0;
	//private int menuLongestStringWidthPix = 0;
	private int iMenuIndex = 0;
	private void updateMenu() {
		if (isKeyPressed(KeyEvent.VK_UP)) {
			iMenuIndex--;
		} else if (isKeyPressed(KeyEvent.VK_DOWN)) {
			iMenuIndex++;
		} else if (isKeyPressed(KeyEvent.VK_ENTER)) {
			switch (iMenuIndex) {
				case iSTR_MENU_PLACE_DYNAMIC_SPAWNS: {
					changeState(STATE_CHOOSE_DYNAMIC_SPAWN);
					iMenuIndex = 0;

					break;
				}
				case iSTR_MENU_SHOW_BUILDINGS: {
					changeState(STATE_SHOWING_BUILDINGS);
					break;
				}
				case iSTR_MENU_SHOW_PLAYERS: {
					changeState(STATE_SHOWING_PLAYERS);
					break;
				}
				case iSTR_MENU_SHOW_RESOURCES: {
					changeState(STATE_SHOWING_RESOURCES);
					break;
				}
				case iSTR_MENU_SHOW_CREATURES: {
					changeState(STATE_SHOWING_CREATURES);
					break;
				}
				default: {
					
					break;
				}
			}
		}
		iMenuIndex = iMenuIndex % MENU_STRINGS.length;
		
		
	}
	
	private int numMenuDisplayedItems = 5;
	private void drawMenu(Graphics2D g) {
		g.setTransform(identity);
		int currentX = 0;
		int currentY = 0;
		currentX = screenWidthDiv2;
		currentY = screenHeightDiv2;
		int iFirstItem = iMenuIndex - (numMenuDisplayedItems / 2);
		int iLastItem = iMenuIndex + (numMenuDisplayedItems / 2);
		int iFirstItemY = screenHeightDiv2 - ( (numMenuDisplayedItems/2) * (fontHeight + 2));
		int index;
		int iStringWidth;
		int iStringHeight;
		drawBubble(g, screenWidthDiv2, screenHeightDiv2, (int)menuBubbleWidth, (fontHeight + 2) * numMenuDisplayedItems, 0xFFFFFF, 0x000000, 0xFF0000, 3, 5);
		//System.out.println("Drawing items " + iFirstItem + " to " + iLastItem);
		for (int i = iFirstItem; i<= iLastItem; i++) {
			index = (MENU_STRINGS.length + i) % MENU_STRINGS.length;
			iStringWidth = (int)getStringWidth(MENU_STRINGS[index], font);
			iStringHeight = (int)getStringHeight(MENU_STRINGS[index], font);
			g.setColor(ColorManager.getColor(0x000000));
			currentY = iFirstItemY + ((i - iFirstItem) * (fontHeight + 2));
			currentX = (int)(screenWidthDiv2 - (iStringWidth / 2 ));
			//System.out.println("Draw item["+index+"] at x["+currentX+"], y["+currentY+"]");
			g.drawString(MENU_STRINGS[index], currentX, currentY);
			if (i == iMenuIndex) {
				g.setColor(ColorManager.getColor(arrowColor));
				g.drawRect(currentX - 1, currentY - (iStringHeight + 1), (iStringWidth + 2), (iStringHeight + 2));
				//System.out.println("Drawing box on menu string " + MENU_STRINGS[index]);
			}
		}
	}

	private void drawBubble(
			Graphics2D g,
			int centerX,
			int centerY,
			int width,
			int height,
			int fillColor,
			int shadowColor,
			int borderColor,
			int borderWidth,
			int shadowHeight){
		
		
		int heightDiv2 = height / 2;
		int widthDiv2 = width / 2;
		int x = centerX - widthDiv2;
		int y = centerY - heightDiv2;
		
		//shadow
		if(shadowHeight > 0){
			int shadowHeightDiv2 = Math.max(1,(shadowHeight / 2));
			g.setColor(ColorManager.getColor(shadowColor));
			g.fillRoundRect(
					x + shadowHeightDiv2,
					y + shadowHeight,
					width,
					height,
					6,6);
		}
		//border
		if(borderWidth > 0){
			g.setColor(ColorManager.getColor(borderColor));
			g.fillRoundRect(
					x - borderWidth,
					y - borderWidth,
					width + (borderWidth<<1),
					height + (borderWidth<<1),
					6,6);
		}
		
		//center
		g.setColor(ColorManager.getColor(fillColor));
		g.fillRoundRect(
				x,
				y,
				width,
				height,
				6,6);
		
	}
	
	//private int iNumLairTemplates;
	private LairTemplate lairTemplate = null;
	private void updateChooseDynamicSpawn() {
		if (isKeyPressed(KeyEvent.VK_UP)) {
			iMenuIndex--;
		} else if (isKeyPressed(KeyEvent.VK_DOWN)) {
			iMenuIndex++;
		} else if (isKeyPressed(KeyEvent.VK_ENTER)) {
			lairTemplate = vLairTemplates[iPlanetToView].elementAt(iMenuIndex);
			changeState(STATE_PLACING_DYNAMIC_SPAWNS);
			gridElementIndexX = 200;
			gridElementIndexY = 200;
		} else if (isKeyPressed(KeyEvent.VK_F1)) {
			changeState(STATE_PRE_MENU);
		}
		iMenuIndex = (iMenuIndex + vLairTemplates[iPlanetToView].size()) % vLairTemplates[iPlanetToView].size();
	}
	
	private void drawChooseDynamicSpawn(Graphics2D g) {
		Vector<LairTemplate> lairTemplatesThisPlanet = vLairTemplates[iPlanetToView];
		g.setTransform(identity);
		int currentX = 0;
		int currentY = 0;
		currentX = screenWidthDiv2;
		currentY = screenHeightDiv2;
		int iFirstItem = iMenuIndex - (numMenuDisplayedItems / 2);
		int iLastItem = iMenuIndex + (numMenuDisplayedItems / 2);
		int iFirstItemY = screenHeightDiv2 - ( (numMenuDisplayedItems/2) * (fontHeight + 2));
		int index;
		int iStringWidth;
		int iStringHeight;
		drawBubble(g, screenWidthDiv2, screenHeightDiv2, (int)lairTemplateBubbleWidth, (fontHeight + 2) * numMenuDisplayedItems, 0xFFFFFF, 0x000000, 0xFF0000, 3, 5);
		for (int i = iFirstItem; i<= iLastItem; i++) {
			
			index = (lairTemplatesThisPlanet.size() + i) % lairTemplatesThisPlanet.size();
			LairTemplate template = lairTemplatesThisPlanet.elementAt(index);
			ItemTemplate creatureTemplate = server.getTemplateData(template.getIMob1Template());
			String sLairName = creatureTemplate.getIFFFileName();
			iStringWidth = (int)getStringWidth(sLairName, font);
			iStringHeight = (int)getStringHeight(sLairName, font);
			currentX = screenWidthDiv2 - (iStringWidth / 2);
			currentY = iFirstItemY + ((i - iFirstItem) * (iStringHeight + 2));
			g.setColor(ColorManager.getColor(0x000000));
			g.drawString(sLairName, currentX, currentY);
			if (i == iMenuIndex) {
				g.setColor(ColorManager.getColor(0xFFFFFF * (numFrames % 2)));
				g.drawRect(currentX - 1, currentY - (iStringHeight + 1), iStringWidth + 2, iStringHeight + 2);
			}
		}
		
	}
	
	private void drawPreMenu(Graphics2D g) {
		AffineTransform currentTransform = g.getTransform();
		g.setTransform(identity);
		g.setColor(ColorManager.getColor(0x000000));
		float stringWidth = getStringWidth(loadingString, font);
		g.drawString(loadingString, screenWidthDiv2 - (stringWidth / 2), 20);
		g.setTransform(currentTransform);
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

	private void placeDynamicSpawns(Rectangle2D bounds) {
		
		//System.out.println("Bounds: leftX[" + bounds.getX() + "], topY[" + bounds.getY() + "], w["+bounds.getWidth() + "], h["+bounds.getHeight()+"]" );
		
		// TODO:  Save the rectangle bounds once, instead of saving the spawn multiple times (once for each grid).
		// Reason:  What happens if the server admin decides to alter the size of the grid elements?  The spawn will 
		// actually move in the game world.  We probably don't want that to happen.
		
		// Save the rectangle, then add the spawn to the grid elements.
		
		//float leftX = (float)bounds.getX();
		//float topY = (float)bounds.getY();
		//float rightX = (float)(leftX + bounds.getWidth());
		//float bottomY = (float)(topY + bounds.getHeight());
		Vector<GridElement> vElementsInSpawn = zoneServerGrid[iPlanetToView].getAllContainedElements(bounds);
		float numElements = vElementsInSpawn.size();
		if (numElements == 0) {
			
			return;
		}
		
		// Save the rectangle.
		
		
		//int minNumLairsPerElement = Math.max(Math.round((float)iMinNumDynamicLairsToSpawn / numElements), 1);
		//int maxNumLairsPerElement = Math.max(Math.round((float)iMaxNumDynamicLairsToSpawn / numElements), 1);
		//System.out.println("Number of lairs per element:  min["+minNumLairsPerElement + "], max[" + maxNumLairsPerElement + "]");
		int lairSpawnColor = SWGGui.getRandomInt(0xFFFFFF);
		DynamicLairSpawn spawn = new DynamicLairSpawn(server);
		spawn.setGrid(zoneServerGrid[iPlanetToView]);
		spawn.setMaxNumToSpawn(iMaxNumDynamicLairsToSpawn);
		spawn.setMinNumToSpawn(iMinNumDynamicLairsToSpawn);
		spawn.setNumPlayersBeforeSpawn(iMinNumPlayersToTriggerDynamicLairSpawn);
		spawn.setRespawnDelay(lDynamicLairRespawnTimeMS);
		spawn.setSpawnTemplateID((short)lairTemplate.getILairTemplate());
		spawn.setColor(lairSpawnColor);
		spawn.setPlanetID(iPlanetToView);
		spawn.setBoundaries(bounds);
		server.getGUI().getDB().saveDynamicLairSpawn(spawn, server.getServerID());
		server.addDynamicLairSpawn(spawn);
		// For drawing.
		vLairSpawns[iPlanetToView] = server.getLairSpawnForPlanet(iPlanetToView);
	}
}

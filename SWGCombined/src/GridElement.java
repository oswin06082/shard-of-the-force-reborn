import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


public class GridElement {
	// On the SOE maps:  Length increases from left to right.  Width increases from top to bottom.  Height decreases from our face towards the map.
	protected final static byte GRID_ELEMENT_TOP_LEFT = 0;
	protected final static byte GRID_ELEMENT_TOP_CENTER = 1;
	protected final static byte GRID_ELEMENT_TOP_RIGHT = 2;
	protected final static byte GRID_ELEMENT_MIDDLE_LEFT = 3;
	protected final static byte GRID_ELEMENT_MIDDLE_RIGHT = 4;
	protected final static byte GRID_ELEMENT_BOTTOM_LEFT = 5;
	protected final static byte GRID_ELEMENT_BOTTOM_CENTER = 6;
	protected final static byte GRID_ELEMENT_BOTTOM_RIGHT = 7;
	protected final static byte GRID_ELEMENT_THIS = 8; // Not in the array.
	protected final static byte NUM_GRID_ELEMENTS_NEARBY = 8;
	private float leftX; 
	private float topY;
	private float rightX;
	private float bottomY;
	//private float nearZ;
	//private float farZ;
	private GridElement[] vNearbyElements;
	private Hashtable<Long, SOEObject> vObjects;
	public final static float length = 160.0f;
	public final static float width = 160.0f;
	private long lDrawColor;
	private HashMap<Short, DynamicLairSpawn> vDynamicSpawnTemplates;
	private int xIndex;
	private int yIndex;
	private int iPlayerCount = 0;
	private int iPlanetID = -1;
	//private final static float height = 160.0f;
	public GridElement(float x, float y, int iPlanetID) {
		leftX = x;
		topY = y;
		rightX = x + length;
		bottomY = y + width;
		//nearZ = z + (height / 2.0f);
		//farZ = nearZ - height;
		vNearbyElements = new GridElement[NUM_GRID_ELEMENTS_NEARBY];
		vObjects = new Hashtable<Long, SOEObject>();
		
		vDynamicSpawnTemplates = new HashMap<Short,DynamicLairSpawn>();
	}
	
	public boolean isContainedBy(float x, float y) {
		//System.out.println("isContainedBy -- x["+x+"], y["+y+"]");
		//System.out.println("Grid rectangle:  tl["+leftX+","+topY+"], bl["+leftX+","+bottomY+"], tr["+rightX+","+topY+"], br["+rightX+","+bottomY+"]");
		boolean bContained = ((x >= leftX)
				&& (x < rightX)
				&& (y >= topY)
				&& (y < bottomY)
				);
		
		//System.out.println("Object contained by this GridElement? " + bContained);
		return bContained;
	}
	
	/*(public boolean isContainedBy(float x, float y, float z) {
		return ((x > leftX)
				&& (x < rightX)
				&& (y > topY)
				&& (y < bottomY)
				&& (z < farZ)
				&& (z > nearZ)
				);
	}*/
	
	public boolean isContainedBy(SOEObject o) {
		return isContainedBy(o.getX(), o.getY());
	}
	
	public void addObjectToGridElement(SOEObject o) {
		// If we don't already have this object, go ahead and put it in if it's in our rectangle.
		/*if (o instanceof Lair) {
			System.out.println("GridElement [" + xIndex + "]["+yIndex+"] adding object with ID " + o.getID() + ", IFF Filename " + o.getIFFFileName());
		}*/
		if (!vObjects.containsKey(o.getID())) {
			if (isContainedBy(o)) {
				vObjects.put(o.getID(), o);
				if (!o.getIsStaticObject()) { 
					Vector<Player> players = getAllNearPlayers(); 
					for (int i = 0; i < players.size(); i++) {
						try {
							players.elementAt(i).spawnItem(o);
							ZoneClient client = players.elementAt(i).getClient();
							if (o.getCellID() == 0) {
								client.insertPacket(PacketFactory.buildNPCUpdateTransformMessage(o));
							} else {
								client.insertPacket(PacketFactory.buildNPCUpdateCellTransformMessage(o, client.getServer().getObjectFromAllObjects(o.getCellID())));
							}
						} catch (Exception e) {
							// D'oh!
						}
					} 
				}
			} else {
				DataLog.logEntry("Add object to coffeetree failure:  Object is NOT contained by element x="+getX() + ", y="+getY() + " Object ID:" + o.getID(),"GridElement",Constants.LOG_SEVERITY_INFO,true,true);
				DataLog.logEntry("coords: x["+o.getX() + "], y["+ o.getY() + "], planet["+Constants.TerrainNames[o.getPlanetID()]+"]","GridElement",Constants.LOG_SEVERITY_INFO,true,true);
				DataLog.logEntry("Item type: " + o.getIFFFileName(),"GridElement",Constants.LOG_SEVERITY_INFO,true,true);
			}
		} else {
			/*System.out.println("Possible error:  element [" + xIndex + "]["+yIndex+"] already contains object with ID " + o.getID() + ", IFF Filename " + o.getIFFFileName());
			StackTraceElement[] elements = Thread.currentThread().getStackTrace();
			for (int i=1; i < elements.length; i++) {
				System.out.println(elements[i]);
			}*/
		}
	}

	public void removeObjectFromGridElement(SOEObject o, boolean bStillSpawned) {
		if (o instanceof Player) {
			iPlayerCount--;
		}
		vObjects.remove(o.getID());
		if (!bStillSpawned) {
			if (!o.getIsStaticObject()) {
				Vector<Player> players = getAllNearPlayers(); 
				for (int i = 0; i < players.size(); i++) {
					Player player = (Player)players.elementAt(i);
					try {
						// If we're forcibly removing the item, or if the item is no longer in range of the Player, remove it.
						if (!ZoneServer.isInRange(o, player)) {
							player.despawnItem(o);
						}
					} catch (Exception e) {
						// D'oh!
					}
				} 
			}
		}
	}
	
	
	public Hashtable<Long, SOEObject> getAllObjectContained() {
		return vObjects;
	}
	
	public Vector<Player> getAllPlayersContained() {
		Enumeration<SOEObject> vObjEnum = vObjects.elements();
		Vector<Player> vPlayers = new Vector<Player>();
		while (vObjEnum.hasMoreElements()) {
			SOEObject elementObject = vObjEnum.nextElement();
			if (elementObject instanceof Player) {
				if (!(elementObject instanceof NPC)) {
					vPlayers.add((Player)elementObject);
				}
			}
		}
		return vPlayers;
	}
	
	public Vector<NPC> getAllNPCsContained() {
		Enumeration<SOEObject> vObjEnum = vObjects.elements();
		Vector<NPC> vPlayers = new Vector<NPC>();
		while (vObjEnum.hasMoreElements()) {
			SOEObject elementObject = vObjEnum.nextElement();
			if (elementObject instanceof NPC) {
				vPlayers.add((NPC)elementObject);
			}
		}
		return vPlayers;
	}
	
	public float getX() {
		return leftX;
	}
	public float getY() {
		return topY;
	}
	/*public float getZ() {
		return nearZ;
	}*/
	public ConcurrentHashMap<Long, SOEObject> getAllNearObjects() {
		ConcurrentHashMap<Long, SOEObject> toReturn = new ConcurrentHashMap<Long, SOEObject>();
		toReturn.putAll(vObjects);
		for (int i = 0; i < vNearbyElements.length; i++) {
			GridElement element = vNearbyElements[i];
			if (element != null) {
				toReturn.putAll(element.getAllObjectContained());
			}
		}
		return toReturn;
	}
	
	public void setNearbyGrid(GridElement e, byte gridPosition) {
		if (gridPosition > NUM_GRID_ELEMENTS_NEARBY || gridPosition < 0) {
			return;
		}
		vNearbyElements[gridPosition] = e;
	}
	
	public Vector<Player> getAllNearPlayers() {
		Vector<Player> toReturn = new Vector<Player>();
		toReturn.addAll(getAllPlayersContained());
		for (int i = 0; i < vNearbyElements.length; i++) {
			GridElement element = vNearbyElements[i];
			if (element != null) {
				toReturn.addAll(element.getAllPlayersContained());
			}
		}
		return toReturn;
	}
	
	public Vector<SOEObject> getAllNearCreatures() {
		Vector<SOEObject> toReturn = new Vector<SOEObject>();
		toReturn.addAll(getAllPlayersContained());
		for (int i = 0; i < vNearbyElements.length; i++) {
			GridElement element = vNearbyElements[i];
			if (element != null) {
				toReturn.addAll(element.getAllPlayersContained());
				toReturn.addAll(element.getAllNPCsContained());
			}
		}
		return toReturn;
	}

	
	public void setDrawColor(long lColor) {
		lDrawColor = lColor;
	}
	
	public long getDrawColor() {
		return lDrawColor;
	}
	
	public void addDynamicLairSpawn(DynamicLairSpawn spawn) {
		vDynamicSpawnTemplates.put(spawn.getSpawnTemplateID(), spawn);
	}
	
	public DynamicLairSpawn getDynamicLairSpawnByTemplateID(short id) {
		return vDynamicSpawnTemplates.get(id);
	}
	public HashMap<Short, DynamicLairSpawn> getDynamicLairSpawn() {
		return vDynamicSpawnTemplates;
	}
	
	public void setArrayIndices(int x, int y) {
		xIndex = x;
		yIndex = y;
	}
	
	public int getXArrayIndex() {
		return xIndex;
	}
	
	public int getYArrayIndex() {
		return yIndex;
	}
	
	public boolean hasObject(long objID) {
		return vObjects.containsKey(objID);
	}
	
	public boolean hasObject(SOEObject o) {
		return hasObject(o.getID());
	}
	
	public boolean equals(GridElement element) {
		return ((element.getXArrayIndex() == xIndex) && (element.getYArrayIndex() == yIndex) && (element.getPlanetID() == iPlanetID)); 
	}
	public int getPlanetID() {
		return iPlanetID;
	}
}

import java.awt.geom.Rectangle2D;
import java.util.Vector;


public class DynamicLairSpawn {
	private final static long UPDATE_TIMEOUT = 10000; // Once every 10 seconds or so.
	private int iNumSpawned = 0;
	//private float numSpawnsPerPlayerInRegion = 0.0f;
	private Grid theGrid;
	//private GridElement myElement;
	private Rectangle2D spawnBoundaries;
	private int objectID;
	private int planetID;
	private byte iSpawnType;
	private short iSpawnTemplateID;
	private int color;
	private int iMaxNumToSpawn;
	private int iMinNumToSpawn;
	private long lRespawnDelay;
	private ZoneServer server;
	//private long lTimeToNextSpawn;
	private Vector<Lair> lairs;
	private long lNextUpdateTime = UPDATE_TIMEOUT;
	private int targetNumToSpawn;
	private int iNumPlayersBeforeSpawn = 1;
	private int iSpawnID;
	public DynamicLairSpawn() {
		spawnBoundaries = null;
		objectID = -1;
		this.server = null;
		planetID = -1;
		iNumSpawned = 0;
		iSpawnType = -1;
		iSpawnTemplateID = -1;
	}
	public DynamicLairSpawn(ZoneServer server) {
		spawnBoundaries = null;
		objectID = -1;
		this.server = server;
		planetID = -1;
		iNumSpawned = 0;
		iSpawnType = -1;
		iSpawnTemplateID = -1;
	}

	
	protected int getColor() {
		return color;
	}

	protected void setColor(int color) {
		this.color = color;
	}

	protected int getMaxNumToSpawn() {
		return iMaxNumToSpawn;
	}
	protected int getMinNumToSpawn() {
		return iMinNumToSpawn;
	}
	protected void setMinNumToSpawn(int minNum) {
		iMinNumToSpawn = minNum;
		if ((iMaxNumToSpawn < iMinNumToSpawn)  && (iMaxNumToSpawn != 0)){
			setMaxNumToSpawn(iMinNumToSpawn);
		}
	}

	protected void setMaxNumToSpawn(int maxNumToSpawn) {
		iMaxNumToSpawn = maxNumToSpawn;
		lairs = new Vector<Lair>(iMaxNumToSpawn);
	}

	protected short getSpawnTemplateID() {
		return iSpawnTemplateID;
	}

	protected void setSpawnTemplateID(short spawnTemplateID) {
		iSpawnTemplateID = spawnTemplateID;
	}

	public byte getSpawnType() {
		return iSpawnType;
	}

	public void setSpawnType(byte spawnType) {
		iSpawnType = spawnType;
	}

	protected long getRespawnDelay() {
		return lRespawnDelay;
	}

	protected void setRespawnDelay(long respawnDelay) {
		lRespawnDelay = respawnDelay;
	}

	protected int getObjectID() {
		return objectID;
	}

	protected void setObjectID(int objectID) {
		this.objectID = objectID;
	}

	protected int getPlanetID() {
		return planetID;
	}

	protected void setPlanetID(int planetID) {
		this.planetID = planetID;
	}

	protected ZoneServer getServer() {
		return server;
	}

	protected void setServer(ZoneServer server) {
		this.server = server;
	}

	protected Grid getGrid() {
		return theGrid;
	}

	protected void setGrid(Grid theGrid) {
		this.theGrid = theGrid;
	}
	
	public void update(long lDeltaTimeMS) throws Exception {
		
		lNextUpdateTime -= lDeltaTimeMS;
		for (int i = 0; i < lairs.size(); i++) {
			lairs.elementAt(i).update(lDeltaTimeMS);
		}
		
		
		if (lNextUpdateTime <= 0) {
			//System.out.println("Update dynamic spawn.");
			lNextUpdateTime = UPDATE_TIMEOUT;
			Vector<GridElement> vElements = theGrid.getAllContainedElements(spawnBoundaries);
			
			//Vector<Player> vNearbyPlayers = new Vector<Player>(); 
			int iNumPlayersInSpawn = 0;
			for (int i =0; i < vElements.size(); i++) {
				iNumPlayersInSpawn+= vElements.elementAt(i).getAllPlayersContained().size();
			}
			//myElement.getAllNearPlayers();
			if (iNumPlayersInSpawn < iNumPlayersBeforeSpawn) {
				targetNumToSpawn = 0;
				if (iNumSpawned != 0) {
					removeSpawn(0);
				}
			} else {	
				if (targetNumToSpawn == 0) {
					targetNumToSpawn = (int)SWGGui.getRandomInt(iMinNumToSpawn, iMaxNumToSpawn);
				}
				if (iNumSpawned < targetNumToSpawn) {
					//DataLog.logEntry("DynamicLairSpawn " + getSpawnID() + " on " + Constants.PlanetNames[getPlanetID()] + " -- num players " + vNearbyPlayers.size() + ", num lairs to spawn = " + targetNumToSpawn ,"DynamicLairSpawn",Constants.LOG_SEVERITY_INFO,true,true);
					Lair lair = new Lair(server);
					lair.setLairTemplate(iSpawnTemplateID, planetID);
					float minX = (float)spawnBoundaries.getX();
					float maxX = (float)(minX + spawnBoundaries.getWidth());
					float minY = (float)spawnBoundaries.getY();
					float maxY = (float)(minY + spawnBoundaries.getHeight());
					int spawnX = SWGGui.getRandomInt((int)minX, (int)maxX);
					int spawnY = SWGGui.getRandomInt((int)minY, (int)maxY);
					//System.out.println("Boundaries:  leftX["+minX+"], rightX["+maxX+"], topY["+maxY+"], bottomY["+minY+"]");
					// Heightmap data needed.
					float spawnZ = server.getHeightAtCoordinates((float)spawnX, (float)spawnY, planetID);
					lair.setLairPosition(spawnX, spawnY, spawnZ, 0);
					lair.spawn();
					addSpawn(lair);
					//server.addObjectToAllObjects(lair, true, false);
					//System.out.println("Spawn new lair at " + spawnX + ", " + spawnY + " on " + Constants.PlanetNames[getPlanetID()]);
				} else if (iNumSpawned > targetNumToSpawn) {
					// Need to despawn some lairs -- this shouldn't happen unless the target number to spawn changes.
				}
			
			}
			//System.out.println("Update dynamic spawn END.");
		}
	}
	
	public void removeSpawn(int index) {
		Lair lairToRemove = lairs.remove(index);
		if (lairToRemove != null) {
			iNumSpawned --;
			lairToRemove.killLair();
		}
	}

	public void addSpawn(Lair lair) {
		if (iNumSpawned < iMaxNumToSpawn) {
			lairs.add(lair);
			iNumSpawned++;
		} else {
			lair.killLair(); // Should never happen.
		}
	}
	
	public void setNumPlayersBeforeSpawn(int numPlayers) {
		iNumPlayersBeforeSpawn = numPlayers;
	}
	
	public int getNumPlayersBeforeSpawn() {
		return iNumPlayersBeforeSpawn;
	}
	
	protected void setSpawnID(int id) {
		iSpawnID = id;
	}
	
	protected int getSpawnID() {
		return iSpawnID;
	}
	
	protected boolean getHasActiveSpawn() {
		return !lairs.isEmpty();
	}
	
	protected Rectangle2D getBoundaries() {
		return spawnBoundaries;
	}
	
	protected void setBoundaries(Rectangle2D spawnBoundaries) {
		this.spawnBoundaries = spawnBoundaries;
	}
	protected void setBoundaries(int leftX, int topY, int rightX, int bottomY) {
		spawnBoundaries = new Rectangle2D.Float(leftX, topY, (rightX - leftX), (bottomY - topY));
	}
}

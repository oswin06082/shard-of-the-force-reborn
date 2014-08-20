
public class MapLocationData {

	private long objectID;
	private int iPlanetID;
	private String sPlanetName;
	private String sLocationName;
	private float currentX;
	private float currentY;
	private byte iLocationType;
	private byte iLocationSubType;

	public MapLocationData() {
		
	}

	public float getCurrentX() {
		return currentX;
	}

	public void setCurrentX(float currentX) {
		this.currentX = currentX;
	}

	public float getCurrentY() {
		return currentY;
	}

	public void setCurrentY(float currentY) {
		this.currentY = currentY;
	}

	public byte getObjectSubType() {
		return iLocationSubType;
	}

	public void setObjectSubType(byte locationSubType) {
		iLocationSubType = locationSubType;
	}

	public byte getObjectType() {
		return iLocationType;
	}

	public void setObjectType(byte locationType) {
		iLocationType = locationType;
	}

	public int getPlanetID() {
		return iPlanetID;
	}

	public void setPlanetID(int planetID) {
		iPlanetID = planetID;
	}

	public long getObjectID() {
		return objectID;
	}

	public void setObjectID(long objectID) {
		this.objectID = objectID;
	}

	public String getName() {
		return sLocationName;
	}

	public void setName(String locationName) {
		sLocationName = locationName;
	}

	public String getPlanetName() {
		return sPlanetName;
	}

	public void setPlanetName(String planetName) {
		sPlanetName = planetName;
	}
	
	
	
}

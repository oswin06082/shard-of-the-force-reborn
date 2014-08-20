
/**
 * The Waypoint class is a container class that holds information on Waypoints.  Their planet, their location, their type (mission waypoint, player-created waypoint, quest waypoint, etc.)
 * @author Darryl
 *
 */
public class Waypoint extends SOEObject{
	public final static long serialVersionUID = 1l;
	private boolean bActivated;
	private byte iWaypointType;
	private  String sName;
	private  int planetCRC;
	private long ownerID;

	/**
	 * Creates a new, default waypoint.
	 */
	public Waypoint() {
		super();
	}
	
	/**
	 * Gets the owner of this waypoint.
	 * @return The owner's Object ID.
	 */
	public long getOwnerID() {
		return ownerID;
	}
	
	/**
	 * Sets the owner of this waypoint.
	 * @param id -- The owner's Object ID.
	 */
	public void setOwnerID(long id) {
		ownerID = id;
	}
	
	/**
	 * Gets the Planet CRC for this waypoint.
	 * @return The Planet CRC.
	 */
	public int getPlanetCRC() {
		return planetCRC;
	}
	
	/**
	 * Sets the Planet CRC for this waypoint.
	 * @param crc -- The Planet CRC.
	 */
	public void setPlanetCRC(int crc) {
		planetCRC = crc;
	}
	
	/**
	 * Gets the name of this waypoint.
	 * @return The name.
	 */
	public String getName() {
            if(sName == null)
            {
                sName = "";
            }
		return sName;
	}
	
	/**
	 * Sets the name of this waypoint.
	 * @param sName -- The name.
	 */
	public void setName(String sName) {
		this.sName = sName;
	}
	
	/**
	 * Gets the waypoint type.
	 * @return The waypoint type.
	 */
	public byte getWaypointType() {
		return iWaypointType;
	}
	
	/**
	 * Sets the waypoint type.
	 * @param b -- The waypoint type.
	 */
	public void setWaypointType(byte b) {
		iWaypointType = b;
	}
	
	/**
	 * Gets whether this waypoint is currently active in the world.
	 * @return True if the waypoint is active, false if it isn't.
	 */
	public boolean getIsActivated() {
		return bActivated;
	}
	
	/**
	 * Sets the active status of this waypoint.
	 * @param b -- The waypoint active status.
	 */
	public void setIsActivated(boolean b) {
		bActivated = b;
	}
	
	/**
	 * Simply toggles the active status of this waypoint.  If it was on, now it is off.  If it was off, now it is on.
	 */
	public void toggleIsActivated() {
		bActivated = !bActivated;
	}
	
}

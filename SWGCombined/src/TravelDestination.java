import java.io.Serializable;


/**
 * The TravelDestination class is a container class meant to hold all information on Travel Points.
 * IE:  When you walk up to a Ticket Purchase Terminal in a Starport and use it, you get a list of locations
 * you can travel to.  Those locations are TravelDestinations
 * @author Darryl
 *
 */
public class TravelDestination implements Serializable{
		public final static long serialVersionUID = 1l;
        private int iDestinationPlanet;
	private String sDestinationName;
        
	private float locationX;
	private float locationZ;
	private float locationY;
        
        private int terminalID;
        private int Cost;
	private int ticketID;
	private long itemID;
        
	private boolean bIsStarport;
	private boolean bIsPlayerCityShuttle;
	private boolean bIsShuttleport = true;
        private long cellID;
        
        
        public TravelDestination(){
            super();
        }
        
	/**
	 * Creates a new TravelDestination.
	 * @param sName -- The Name of the Destination.
	 * @param planetID -- The Planet the destination is on.
	 * @param termID -- The terminal ID the Player is using to look at this TravelDestination.
	 * @param bIsShuttleport -- Indicates whether this is a Shuttleport destination or a Starport destination.
	 * @param x -- The X location of this destination.
	 * @param y -- The Y location of this destination.
         * @param z -- The Z location of this destination.
	 */
           
	public TravelDestination(String sName, int planetID, int termID, boolean _IsShuttleport, float x, float y,float z) {
		sDestinationName = sName;
		iDestinationPlanet = planetID;
		terminalID = termID;
		bIsShuttleport = _IsShuttleport;
                locationX = x;
                locationZ = z;
                locationY = y;
	}
	
	/**
	 * Gets the name of this TravelDestination.
	 * @return The Name.
	 */
	public String getDestinationName() {
		return sDestinationName;
	}
	
	/**
	 * Gets the Planet ID for this TravelDestination.
	 * @return The Planet ID
	 */
	public int getDestinationPlanet() {
		return iDestinationPlanet;
	}
	
	/**
	 * Gets the terminal ID this TravelDestination was viewed on.
	 * @return The Terminal ID.
	 */
	public int getTerminalID() {
		return terminalID;
	}
	
	/**
	 * Gets whether this TravelDestination is a shuttleport or starport destination.
	 * @return True if this is a shuttleport destination, otherwise returns false.
	 */
	public boolean isShuttle() {
		return bIsShuttleport;
	}
	
	/**
	 * Gets the X coordinate for this TravelDestination.
	 * @return X
	 */
	public float getX() {
		return locationX;
	}
	
	/**
	 * Gets the Z coordinate for this TravelDestination.  This is so we know at what height to spawn any Player arriving here.
	 * @return Z
	 */
	public float getZ() {
		return locationZ;
	}
	
	/**
	 * Gets the Y coordinate for this TravelDestination.
	 * @return Y
	 */
	public float getY() {
		return locationY;
	}
        
        public void setCost(int _Cost) {
            Cost = _Cost;
        }
        
        public int getCost(){
            return Cost;
        }
        
        public void setTicketID(int _ticketID){
            ticketID = _ticketID;
        }
        
        public int getTicketID(){
            return ticketID;
        }
        
        public void setItemID(long _itemID){
            itemID = _itemID;
        }
	public long getItemID(){
            return itemID;
        }
	
        public void setIsStarPort(boolean b){
            bIsStarport = b;
        }
        
        public boolean getIsStarPort(){
            return bIsStarport;
        }
        
	public void setIsPlayerCityShuttle(boolean b){
            bIsPlayerCityShuttle = b;
        }
        
        public boolean getIsPlayerCityShuttle(){
            return bIsPlayerCityShuttle;
        }
        
        public void setIsShuttlePort(boolean b){
            bIsShuttleport = b;
        }
        
        public boolean getIsShuttlePort(){
            return bIsShuttleport;
        }
        public String getXYZ(){
            String retval = " - X:" + locationX + " Y:" + locationY + " Z:" + locationZ; 
            return retval;
        }

        public long getCellID() {
            return cellID;
        }

        public void setCellID(long cellID) {
            this.cellID = cellID;
        }
        
        
}

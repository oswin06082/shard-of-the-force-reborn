import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;

/**
 * This class is used to spawn any shuttle that needs to move around and lan or take off at shuttle ports
 * @author Tomas Cruz
 */
public class Shuttle extends NPC{
    public final static long serialVersionUID = 1;
    private int iShuttleType;
    private int iTicketID;
    private boolean bIsShuttleBoarding = true;
    private long lTimeToArrival;
    private long lTimeToDeparture;
    private float landingX,landingY,landingZ;
    private int ScheduleTimer = 0;

    private long ChangeLandingZ = 0;
    
    private boolean bIsShuttleLanded = true;
    //private long lastUpdate = 0;
    //private int iShuttlePlanetID;
    
    /**
     * Default Constructor to Make a Shuttle Object.
     * Defaults will be Invalid Ticket and Invalid Shuttle Type
     */
    public Shuttle(){
        //Def Const
        iShuttleType = Constants.SHUTTLE_TYPES_INVALID_SHUTTLE;
        iTicketID = -1;
        
    }
    
    /**
     * This is the Shuttle Constructor to make a new Shuttle Object
     * @param _iTicketID Int - From the Location this Shuttle has a ticket for.
     * @param _iShuttleType Int - See Constants.SHUTTLE_TYPES_....
     */
    public Shuttle(int _iTicketID, int _iShuttleType, float x, float y, float z, int planetID){
        iShuttleType = _iShuttleType;
        iTicketID = _iTicketID;
        landingX = x;
        landingY = y;
        landingZ = z;         
        //iShuttlePlanetID = planetID;
    }
      
    protected boolean getIsShuttleLanded(){
        return bIsShuttleLanded;
    }
    
    /**    
     * @Override: NPC.Update() Function.
     * This function updates a shuttle to move it or do something with it
     * @param lElapsedTimeMS 
     * @Override: NPC.Update() Function.
     */
     public void update(long lElapsedTimeMS){
    	try {
	        boolean lastSpawnedState = getIsSpawned();
	    	boolean currentSpawnedState = (bIsShuttleLanded || bIsShuttleBoarding);
	    	if (lastSpawnedState != currentSpawnedState) {
	    		if (currentSpawnedState) {
	    			setStance(null, Constants.STANCE_STANDING,true);
	    		} else {
	    			setStance(null, Constants.STANCE_PRONE, true);
	    		}
	    	}
	    	setIsSpawned(bIsShuttleLanded || bIsShuttleBoarding);
	        //System.out.println("Shuttle Update " + lastUpdate);
	        //lastUpdate = 0;
	        if(bIsShuttleLanded)
	        {
	        	lTimeToDeparture-=lElapsedTimeMS;
	            if(lTimeToDeparture <= 0)
	            {
	            	setStance(null, Constants.STANCE_PRONE, true);
	            	//System.out.println("Shuttle Taking Off ID " + this.getTicketID());
                    Vector <Player>playerList = this.getServer().getPlayersAroundNPC(this);
                    if(!playerList.isEmpty())
                    {
                        for(int i = 0; i < playerList.size(); i++)
                        {
                        	Player player = playerList.elementAt(i);
                        	//System.out.println("Send flyout to player with first name " + player.getFirstName());
                        	try{
                                this.flyShuttle(player.getClient());
                            }catch(Exception e){
                                System.out.println("Exception Caught while updating Shuttle Takeoff " + e);
                                e.printStackTrace();
                            }

                        }
                    }
	                bIsShuttleLanded = false;
	                bIsShuttleBoarding = false;
	                long lTimeToArr = Math.min(60000*5, ScheduleTimer);
	                setTimeToArrival(lTimeToArr);
	                ChangeLandingZ = System.currentTimeMillis() + 20000;
	                
	            }
	            if(System.currentTimeMillis() >= ChangeLandingZ)
	            {
	            	//System.out.println("ChangeLandingZ triggered.");
	            	//this.setZ(landingZ);
	                bIsShuttleBoarding = true;
	                try{
	                    //this.spawnItem(this);
	                }catch(Exception e){
	                    System.out.println("Shuttle Spawn Error in Shuttle.Java " + e);
	                    e.printStackTrace();
	                }                    
	            }
	        }
	        else 
	        {
	        	lTimeToArrival -= lElapsedTimeMS;
	            
	        	if(lTimeToArrival<= 0)
	            {
	        		setStance(null, Constants.STANCE_STANDING, true);
	               // System.out.println("Shuttle Landing ID " + this.getTicketID());
	                Terminal TD = this.getServer().getTicketDroidByTicketID(iTicketID);
	                
	                if(TD!=null)
	                {
	                    Vector <Player>PlayerList = this.getServer().getPlayersAroundNPC(TD);                    
	                    if(PlayerList!=null && PlayerList.size() >= 1)
	                    {
	                        for(int i = 0; i < PlayerList.size(); i++)
	                        {
	                            try{                           
	                                this.flyShuttle(PlayerList.get(i).getClient());
	                            }catch(Exception e){
	                                System.out.println("Exception Caught while updating Shuttle Landing " + e);
	                                e.printStackTrace();
	                            }
	
	                        }
	                    }
	                }
	                bIsShuttleLanded = true;
	                
	                this.setTimeToDeparture((Math.max(90000 ,ScheduleTimer) / 3) );
	                ChangeLandingZ = System.currentTimeMillis() + 20000;
	            }  
	            if(System.currentTimeMillis() >= ChangeLandingZ)
	            {
	            	//System.out.println("SetZ triggered.");
	            	//this.setZ(16000);
	            }
	        }
    	} catch (Exception e) {
    		System.out.println("Error updating shuttle: " + e.toString());
    		e.printStackTrace();
    	}
    }
     
     /**
      * Sets the Type of Shuttle 
      * @param _iShuttleType - Int - See Constants.SHUTTLE_TYPES_....
      */
    public void setShuttleType(int _iShuttleType){
        iShuttleType = _iShuttleType;
    }
    
    /**
     * Returns the Type of Shuttle
     * @return - Int
     */
    public int getShuttleType(){
        return iShuttleType;
    }
    
    
    /**
     * Sets the Ticket ID for this shuttle.
     * @param _iTicketID
     */
    public void setTicketID(int _iTicketID){
        iTicketID = _iTicketID;
    }
    
    /**
     * Returns the ticket for this shuttle
     * @return
     */
    public int getTicketID(){
        return iTicketID;
    }
    
    /**
     * Sets the Shuttle Boarding boolean
     * @param b - boolean
     */
    public void  setIsShuttleBoarding(boolean b){
        bIsShuttleBoarding = b;
    }
    
    /**
     * Returns the state of the shuttle boarding.
     * @return
     */
    public boolean getIsShuttleBoarding(){
        return bIsShuttleBoarding;
    }
    
    /**
     * Sets the time the shuttle will arrive next.
     * @param _lTimeToArrival
     */
    public void setTimeToArrival(long _lTimeToArrival){
        lTimeToArrival = _lTimeToArrival;
    }
    
    /**
     * Gets the time the shuttle will arrive.
     * @return
     */
    public synchronized long getTimeToArrival(){
        return lTimeToArrival;
    }
    
    /**
     * Sets the time the Shuttle will depart.
     * @param _lTimeToDeparture
     */
    public void setTimeToDeparture(long _lTimeToDeparture){
        lTimeToDeparture = _lTimeToDeparture;
    }
    
    /**
     * Returns the Time To Daparture
     * @return
     */
    public synchronized long getTimeToDeparture(){
        return lTimeToDeparture;
    }
   
    /**
     * Sets the coordinates where this Shuttle will land.
     * @param x
     * @param y
     * @param z
     */
    protected void setLandingCoordinates(float x, float y, float z){
        landingX = x;
        landingY = y;
        landingZ = z;  
    }
    
    /**
     * Gets Landing Coordinate X
     * @return
     */
    public float getLandingX(){
        return landingX;
    }
    
    /**
     * Gets Landing Coordinate Y
     * @return
     */
    public float getLandingY(){
        return landingY;
    }
    
    /**
     * Gets Landing Coordinate Z
     * @return
     */
    public float getLandingZ(){
        return landingZ;
    }
    
    public void setScheduleTimer(int t){
        ScheduleTimer = t;
    }
    public int getScheduleTimer(){
        return ScheduleTimer;
    }
    
    protected void flyShuttle(ZoneClient client) throws IOException {
        client.insertPacket(PacketFactory.buildObjectControllerMessage_UpdatePosture(this));
        client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)0x0B, this, getStance()));
        int flyAnimation = 0;
        switch(this.iShuttleType)
        {
            case Constants.SHUTTLE_TYPES_GENERIC_SHUTTLE:
            {
                flyAnimation = Constants.shuttleFlyOutAnimation;
                break;
            }
            case Constants.SHUTTLE_TYPES_STARPORT_SHUTTLE:
            {
            	flyAnimation=Constants.starportFlyOutAnimation;
            	break;
            }
        }
        client.insertPacket(PacketFactory.buildObjectController_CombatAction(this, this, flyAnimation, true));
    }
    
    /**
     * This is the Shuttle Radial Menu Request Response.
     * @Override
     * @return
     */
    public Hashtable<Character, RadialMenuItem> getRadialMenus(){
            RadialMenuItem I = new RadialMenuItem((byte)0x00,(byte)0x00,Constants.RADIAL_MENU_EXAMINE,(byte)0x03, "Examine");
            Hashtable<Character, RadialMenuItem> retval = new Hashtable<Character, RadialMenuItem>();
            retval.put(Constants.RADIAL_MENU_EXAMINE, I);
            return retval;
    }
}

import java.io.Serializable;

/**
 * A Container Class for Player Experience.  Contains the Experience Name, the Experience ID, and the current accumulated experience points.
 * @author Darryl
 *
 */
public class PlayerExperience implements Serializable {
	public final static long serialVersionUID = 1;
	private int iExperienceID;
	private int iCurrentExperienceValue;
	private int iMaxExperienceValue;
	private String sExperienceName;

	/**
	 * Constructs a new, empty Experience object.
	 */
	public PlayerExperience() {
		iExperienceID = -1;
		iCurrentExperienceValue = -1;
		iMaxExperienceValue = -1;
		sExperienceName = null;
	}
	
	/**
	 * Constructs a new Experience object for the given Experience ID.
	 * @param iExperienceID -- The Experience ID.
	 */
	public PlayerExperience(int iExperienceID) {
		this.iExperienceID = iExperienceID;
		iCurrentExperienceValue = 0;
		iMaxExperienceValue = 0;
		sExperienceName = null;
	}
	
	/**
	 * Constructs a new Experience object with the given Experience ID and Experience Name
	 * @param iExperienceID -- The Experience ID
	 * @param sExperienceName -- The Experience Name
	 */
	public PlayerExperience(int iExperienceID, String sExperienceName) {
		this.iExperienceID = iExperienceID;
		this.sExperienceName = sExperienceName;
		iCurrentExperienceValue = 0;
		iMaxExperienceValue = 0;
	}
	
	/**
	 * Constructs a new Experience object with the given Experience ID, Experience Name, the given starting experience value, and the maximum experience value the Player can accumulate at present.
	 * @param iExperienceID -- The Experience ID.
	 * @param iCurrentExperienceValue -- The starting experience value.
	 * @param iMaxExperienceValue -- The maximum experience of this type the Player can accrue.
	 * @param sExperienceName -- The experience name.
	 */
	public PlayerExperience(int iExperienceID, int iCurrentExperienceValue, int iMaxExperienceValue, String sExperienceName) {
		this.iExperienceID = iExperienceID;
		this.iCurrentExperienceValue = iCurrentExperienceValue;
		this.iMaxExperienceValue = iMaxExperienceValue;
		this.sExperienceName = sExperienceName;
	}
	
	/**
	 * Set this Experience objet's ID.
	 * @param iExperienceID -- The Experience ID.
	 */
	public void setExperienceID(int iExperienceID) {
		this.iExperienceID = iExperienceID;
	}
	
	/**
	 * Gets this Experience object's ID.
	 * @return -- The Experience ID.
	 */
	public int getExperienceID() {
		return iExperienceID;
	}
	
	/**
	 * Sets the currently accrued experience level for this Experience object.
	 * @param iExp -- The current level of experience.
	 * @return -- The actual new experience amount
	 */
	public int setCurrentExperience(int iExp) {
		if (iCurrentExperienceValue < iMaxExperienceValue) {
			iCurrentExperienceValue = Math.min(iExp, iMaxExperienceValue);
		}
		return iCurrentExperienceValue;
	}
	
        /**
         * Adds Experience to the Current Value of Experience for this player and exp type
         * @param i
         */
        public boolean addToExperience(int i){
            try{
                iCurrentExperienceValue+=i;
                //need to send a delta on this change from here
                System.out.println("Maaach Need an experience Delta update here addToExperience()");
                return true;
            }catch(Exception e){
                return false;
            }
        }
        
        /**
         * Deducts the amount specified of this exp type from the players exp pool
         * @param i
         */
        public boolean deductExperience(int i){
            try{
                iCurrentExperienceValue-=i;
            //need to send a delta on this change from here
                System.out.println("Maaach Need an experience Delta update here deductExperience()");
                return true;
            }catch(Exception e){
                return false;
            }            
        }
	/**
	 * Gets the currently accrued experience level for this Experience object. 
	 * @return The current experience.
	 */
	public int getCurrentExperience() {
		return iCurrentExperienceValue;
                
	}
	
	/**
	 * Sets the maximum amount of experience that can currently accrue for this Experience item.
	 * @param iCap -- The experience cap.
	 */
	public void setMaxExperience(int iCap) {
		iMaxExperienceValue = iCap;
	}
	
	/**
	 * Gets the maximum amount of experience that can currently accrue for this Experience item.
	 * @return The experience cap.
	 */
	public int getMaxExperience() {
		return iMaxExperienceValue;
	}
	
	/**
	 * Sets the name of this Experience item.
	 * @param sName -- The Experience name.
	 */
	public void setExperienceName(String sName) {
		sExperienceName = sName;
	}
	
	/**
	 * Gets the name of this Experience item.
	 * @return The name.
	 */
	public String getExperienceName() {
		return sExperienceName;
	}
}


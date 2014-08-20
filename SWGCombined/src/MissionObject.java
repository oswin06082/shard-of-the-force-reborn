import java.util.Vector;
/**
 * Creates a new Mission Object class that allows us to send missions to players mission bag.
 * @author Tomas Cruz
 */
public final class MissionObject extends IntangibleObject{
    public final static long serialVersionUID = 1;
    
    private float pickupX,pickupY,pickupZ;
    private int iPickupPlanetID;
    private int iDisplayObjectCRC;
    private String sMissionSTFString;
    private String sMissionSTFDetailIdentifier;
    private String [] sMissionGiver;
    private String sMissionGiverName;
    private String sMissionSTFTextIdentifier;
    private Player ownerPlayer;
    private TangibleItem tParentObject;
    private long lLastMissionID;
    private int iDiffcultyLevel;
    private int iDifficultyIdentifier;
    private int iMissionType;
    private int iMissionPayout;
    private String sTargetDisplaySTFString;
    private int iMissionTypeCRC;
    private Vector<Short> vUpdates;
    private transient int iUpdateCounter = 0;
    private int iLairTemplateChosen;
    private int iMissionTemplateChosen;
    private int iCollateralID = 0;
    
    public MissionObject(){
        setCRC(0xDF064E7A);
        this.setIFFFileName("object/mission/shared_mission_object.iff");      
        vUpdates = new Vector<Short>();
        iDiffcultyLevel = 1;
        iDifficultyIdentifier = 1;
    }

    public int getIPickupPlanetID() {
        return iPickupPlanetID;
    }

    public void setIPickupPlanetID(int iPickupPlanetID) {
        if(this.iPickupPlanetID != iPickupPlanetID)
        {   
            this.iPickupPlanetID = iPickupPlanetID; 
            if(!vUpdates.contains((short)0x06))
            {
                vUpdates.add((short)0x06);
            }        
        }
        
    }

    public float getPickupX() {
        return pickupX;
    }

    public void setPickupX(float origX) {
        if(this.pickupX != origX)
        {
            this.pickupX = origX;
            if(!vUpdates.contains((short)0x06))
            {
                vUpdates.add((short)0x06);
            }
        }
    }

    public float getPickupY() {
        return pickupY;
    }

    public void setPickupY(float origY) {
        if(this.pickupY != origY)
        {
            this.pickupY = origY;
            if(!vUpdates.contains((short)0x06))
            {
                vUpdates.add((short)0x06);
            }
        }
    }

    public float getPickupZ() {
        return pickupZ;
    }

    public void setPickupZ(float origZ) {
        if(this.pickupZ != origZ)
        {
            this.pickupZ = origZ;
            if(!vUpdates.contains((short)0x06))
            {
                vUpdates.add((short)0x06);
            }
        }
    }

    public int getIDisplayObjectCRC() {
        return iDisplayObjectCRC;
    }

    public void setIDisplayObjectCRC(int iDisplayObjectCRC) {
        if(this.iDisplayObjectCRC != iDisplayObjectCRC)
        {
            this.iDisplayObjectCRC = iDisplayObjectCRC;
            if(!vUpdates.contains((short)0x0A))
            {
                vUpdates.add((short)0x0A);
            }
        }
    }

    public Player getOwnerPlayer() {
        return ownerPlayer;
    }

    public void setOwnerPlayer(Player ownerPlayer) {
        this.ownerPlayer = ownerPlayer;
    }

    public String[] getSMissionGiver() {
        return sMissionGiver;
    }

    public void setSMissionGiver(String[] sMissionGiver) {
        if(this.sMissionGiver == null || !this.sMissionGiver.equals(sMissionGiver))
        {
            this.sMissionGiver = sMissionGiver;
            sMissionGiverName = "";
            if(sMissionGiver != null)
            {
                if(!sMissionGiver[0].isEmpty())
                {
                    sMissionGiverName += sMissionGiver[0].trim();                
                }
                if(!sMissionGiver[1].isEmpty())
                {            
                    sMissionGiverName += " ";
                    sMissionGiverName += sMissionGiver[1];
                }
            }
            if(!vUpdates.contains((short)0x07))
            {
                vUpdates.add((short)0x07);
            }
        }
    }

    public String getSMissionGiverName() {
        return sMissionGiverName;
    }

    public void setSMissionGiverName(String sMissionGiverName) {
        if(this.sMissionGiverName == null || !this.sMissionGiverName.equals(sMissionGiverName))
        {
            this.sMissionGiverName = sMissionGiverName;
            if(!vUpdates.contains((short)0x07))
            {
                vUpdates.add((short)0x07);
            }
        }
    }

    public String getSMissionSTFString() {
        return sMissionSTFString;
    }

    public void setSMissionSTFString(String sMissionSTFString) {
        if(this.sMissionSTFString==null || !this.sMissionSTFString.equals(sMissionSTFString))
        {
            this.sMissionSTFString = sMissionSTFString;
            if(!vUpdates.contains((short)0x0B))
            {
                vUpdates.add((short)0x0B);
            }
        }
    }

    public String getSMissionSTFDetailIdentifier() {
        return sMissionSTFDetailIdentifier;
    }

    public void setSMissionSTFDetailIdentifier(String sMissionTag1) {
        if(this.sMissionSTFDetailIdentifier == null || !this.sMissionSTFDetailIdentifier.equals(sMissionTag1))
        {
            this.sMissionSTFDetailIdentifier = sMissionTag1;
            if(!vUpdates.contains((short)0x0B))
            {
                vUpdates.add((short)0x0B);
            }
        }
    }

    public String getSMissionSTFTextIdentifier() {
        return sMissionSTFTextIdentifier;
    }

    public void setSMissionSTFTextIdentifier(String sMissionTag2) {
        if(this.sMissionSTFTextIdentifier == null || !this.sMissionSTFTextIdentifier.equals(sMissionTag2))
        {
            this.sMissionSTFTextIdentifier = sMissionTag2;
            if(!vUpdates.contains((short)0x0C))
            {
                vUpdates.add((short)0x0C);
            }
        }
    }

    public TangibleItem getTParentObject() {
        return tParentObject;
    }

    public void setTParentObject(TangibleItem tParentObject) {
        this.tParentObject = tParentObject;
    }

    public long getLLastMissionID() {
        return lLastMissionID;
    }

    public void setLLastMissionID(long lLastMissionID) {
        this.lLastMissionID = lLastMissionID;
    }

    public int getIDiffcultyLevel() {
        return iDiffcultyLevel;
    }

    public void setIDiffcultyLevel(int iDiffcultyLevel) {
        if(this.iDiffcultyLevel != iDiffcultyLevel)
        {
            this.iDiffcultyLevel = iDiffcultyLevel;
            if(!vUpdates.contains((short)0x05))
            {
                vUpdates.add((short)0x05);
            }
        }
    }   

    public int getIMissionPayout() {
        return iMissionPayout;
    }

    public void setIMissionPayout(int iMissionPayout) {
        if(this.iMissionPayout != iMissionPayout)
        {
            this.iMissionPayout = iMissionPayout;
            if(!vUpdates.contains((short)0x08))
            {
                vUpdates.add((short)0x08);
            }
        }
    }

    public int getIMissionType() {
        return iMissionType;
    }

    public void setIMissionType(int iMissionType) {
        this.iMissionType = iMissionType;
    }

    public String getMissionTargetDisplayString() {
        //@lair_n:endor_boar_wolf_bloodfrenzied_lair_neutral_small
        return sTargetDisplaySTFString;        
    }

    public void setMissionTargetDisplayString(String sTargetDisplaySTFString) {
        //@lair_n:endor_boar_wolf_bloodfrenzied_lair_neutral_small
        if(this.sTargetDisplaySTFString == null || !this.sTargetDisplaySTFString.equals(sTargetDisplaySTFString))
        {
            this.sTargetDisplaySTFString = sTargetDisplaySTFString;
            if(!vUpdates.contains((short)0x0F))
            {
                vUpdates.add((short)0x0F);
            }
        }
    }
   
    public void setMissionX(float missionX){
        if(getX() != missionX)
        {
            setX(missionX);
            if(!vUpdates.contains((short)0x09))
            {
                vUpdates.add((short)0x09);
            }
        }
    
    }
    
    public void setMissionY(float missionY){
        if(getY() != missionY)
        {
            setY(missionY);
            if(!vUpdates.contains((short)0x09))
            {
                vUpdates.add((short)0x09);
            }
        }
    }
    
    public void setMissionZ(float missionZ){
        if(getZ() != missionZ)
        {
            setZ(missionZ);
            if(!vUpdates.contains((short)0x09))
            {
                vUpdates.add((short)0x09);
            }
        }
    }
    
    public void setMissionPlanetID(int missionPlanetID){
        if(getPlanetID() != missionPlanetID)
        {
            setPlanetID(missionPlanetID);
            if(!vUpdates.contains((short)0x09))
            {
                vUpdates.add((short)0x09);
            }
        }
    }
    
    public float getMissionX(){
        return getX();
    }
    
    public float getMissionY(){
        return getY();
    }
    
    public float getMissionZ(){
        return getZ();
    }
    
    public int getMissionPlanetID(){
        return getPlanetID();
    }

    public int getIMissionTypeCRC() {
        return iMissionTypeCRC;
    }

    public void setIMissionTypeCRC(int iUnknownMissionCRC1) {
        if(this.iMissionTypeCRC != iUnknownMissionCRC1)
        {
            this.iMissionTypeCRC = iUnknownMissionCRC1;
            if(!vUpdates.contains((short)0x0E))
            {
                vUpdates.add((short)0x0E);
            }
        }
    }
    
    protected void clearMissionObject(){

        pickupX = 0;
        pickupY = 0;
        pickupZ = 0;
        iPickupPlanetID = 0;
        iDisplayObjectCRC = 0;
        sMissionSTFString = "";
        sMissionSTFDetailIdentifier = "";
        sMissionGiver = null;
        sMissionGiverName = "";
        sMissionSTFTextIdentifier = "";
        iDiffcultyLevel = 0;
        iMissionType = 0;
        iMissionPayout = 0;
        sTargetDisplaySTFString = "";
        iMissionTypeCRC = 0;
        
    }
    
    protected MissionObject getMissionObjectCopy(){
        MissionObject m = new MissionObject();
        m.setID(this.getID());         
        m.setIDisplayObjectCRC(0xE191DBAB);
        m.setCRC(this.getCRC());
        //m.setSTFFileName(this.getSTFFileName());
        //m.setIFFFileName(this.getIFFFileName());
        //m.setSTFFileIdentifier(this.getSTFFileIdentifier());
        //m.setSTFDetailName(this.getSTFDetailName());
        //m.setSTFDetailIdentifier(this.getSTFDetailIdentifier());                                
        m.setCustomizationData(null);        
        iUpdateCounter++;
        m.setUpdateCounter(this.iUpdateCounter);        
        return m;
    }
    
    protected void clearUpdates(){
        if(vUpdates==null)
        {
            vUpdates = new Vector<Short>();
        }
        vUpdates.clear();
        if(!vUpdates.contains((short)0x0D))
        {
            vUpdates.add((short)0x0D);
        }
    }
    protected Vector<Short> getUpdatesList(){
        return vUpdates;
    }
    
    private void setUpdateCounter(int iUpdateCounter){
        this.iUpdateCounter = iUpdateCounter;
    }
    protected int getUpdateCounter(boolean bIncrement){
        if (bIncrement) {
        	iUpdateCounter++;
        }
        return iUpdateCounter;
    }

    public int getIDifficultyIdentifier() {
        return iDifficultyIdentifier;
    }

    public void setIDifficultyIdentifier(int iDifficultyIdentifier) {
        this.iDifficultyIdentifier = iDifficultyIdentifier;
    }

    public int getILairTemplateChosen() {
        return iLairTemplateChosen;
    }

    public void setILairTemplateChosen(int iLairTemplateChosen) {
        this.iLairTemplateChosen = iLairTemplateChosen;
    }

    public int getIMissionTemplateChosen() {
        return iMissionTemplateChosen;
    }

    public void setIMissionTemplateChosen(int iMissionTemplateChosen) {
        this.iMissionTemplateChosen = iMissionTemplateChosen;
    }

    public int getICollateralID() {
        return iCollateralID;
    }

    public void setICollateralID(int iCollateralID) {
        this.iCollateralID = iCollateralID;
    }
    
    
    
}

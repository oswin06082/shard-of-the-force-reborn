import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Enumeration;
/**
 
    [19:38] PlasmaFlow: the update function on the lair will keep the lairs pseudo AI updated
    [19:38] Maach_Ine: kk
    [19:38] PlasmaFlow: once the lair object is loaded and spawned it will begin to look at it self
    [19:39] PlasmaFlow: spawning a lair will set a delay to spawn mobs for it
    [19:39] PlasmaFlow: so one min or so after you spawn the lair it will begin spawning its mobs    
    [19:40] PlasmaFlow: if a wave dies the lair will spawn more mobs
    [19:40] PlasmaFlow: if the health is below a certain threshold and the current wave still have 2 defenders 
            alive it will recall them to heal it
    [19:40] PlasmaFlow: and at the same time call the next wave if the last 2 mobs are healing
    [19:41] PlasmaFlow: if the defenders that are healing are in the first wave they will heal the lair completely
    [19:42] PlasmaFlow: if its in the second wave and there are more waves remaining it will heal by a percentage based on waves left
    [19:43] PlasmaFlow: im making a lair template table that will be loaded to the server, when the server decides to spawn a new 
            lair on a planet it will choose a template based on the planet id of lairs availbale for said planet
    [19:43] PlasmaFlow: so maulers will not spawn on naboo and chubas wont spawn on dantooine
    [19:44] PlasmaFlow: each template will have the sepcified template for the lair and the mobs it contains
    [19:44] PlasmaFlow: this includes adult mobs, babies and boss spawns
    [19:44] Maach_Ine: Now we're at the point where we need to coordinate, because I'm writing in a table of "random dynamic spawns" 
            that will be drawn by the server admin onto the planetary map.
    [19:45] PlasmaFlow: well im going to give you the tools necessary for you to put a lair down, 
    [19:45] * Maach_Ine nods
    [19:45] PlasmaFlow: you will call the necessary routines and apply the template to the new lair object
    [19:45] PlasmaFlow: you should be able to normally do this
    [19:47] PlasmaFlow: Lair newLair = new Lair();       newLair.setTemplate(lairTemplateID); Or newLair.setLairRandomTemplate(planetID);
    [19:47] PlasmaFlow: then you pass in the coordinates
    [19:47] PlasmaFlow: newLair.setLairPosition(x,y,z);
    [19:47] PlasmaFlow: or
    [19:47] PlasmaFlow: newLair.setLairRandomPosition();
    [19:48] PlasmaFlow: the lair will do the rest for you
    [19:48] PlasmaFlow: then you call the newLair.spawn(); and it will add it self to all objects and set it self in motion
    [19:49] PlasmaFlow: the lair will also even in sepcified or random make sure its not in a no build zone, or on top of 
            another lair or too close to another lair and re calculate its coodinates based on its new positon and wont 
            spawn till it has a good location
    [19:56] PlasmaFlow: i plan on making a lair radius of about 128 to 250 as a random value
 *  [19:58] PlasmaFlow: also depending on the lair for example, if its a bynaire pirate lair and the pirates are for a mission of 
 *          stolen hyperdrive parts the props around the lair will be hyper drive parts and such
 */

/**
 * This class is the Lair Class.
 * It will spawn a lair and its mobs.
 * It extends the NPC Class Since the Lair it self is an npc that will be attackable.
 * @author Tomas Cruz
 */
public final class Lair extends NPC{
    public final static long serialVersionUID = 1l;
    //private Vector<TangibleItem> EquipmentList;
    //private Vector<TangibleItem> PropList;
    private ConcurrentHashMap<Long,NPC> vSpawnedChildren;
    private Vector<SOEObject> vHateList;
    //private Vector<TangibleItem> LootList;
    private LairTemplate myLairTemplate;
    
    //private SWGGui gui;
    
    private int LAIR_MINIMUM_DISTANCE_RADIUS = 128;
    //private int LAIR_MAXIMUM_DISTANCE_RADIUS = 256;
    
    private int iMaxChildren;    
    private int iBabyCount;
    private int iWaveID;    
    //private long lSpawnTime;
    //private long lDeSpawnTime;
    private long lMobSpawnDelayTime;      
    //private long lGroupID;
    //private long lGroupInviteCounter;
    //private long lGroupInviter;
    //private boolean bMobsSpawned;
    //private int iLairId;    
    private boolean bIsLairSpawned;    
    //private boolean bIsFactionBase;    
    //private int iCurrentStats;
    private int iDefenderCount;
    private boolean bPositionSet;
    
    private ZoneServer server;
    
    protected boolean getIsLairSpawned() {
    	return bIsLairSpawned;
    }
    

    public Lair(ZoneServer server){
    	super();
    	
    	//make sure it doesn't attack back.
    	setArtificialIntelligenceType(Constants.INTELLIGENCE_TYPE_NONE);
        // For convenience sake, the lair will have 2 pointers.
    	setServer(server);
    	this.server = server;
        
        myLairTemplate = null;
        setPVPStatus(Constants.PVP_STATUS_ATTACKABLE);
        setStance(null, Constants.STANCE_STANDING, true);
        setCREO3Bitmask(0);        
        //lGroupID = 0;
        //lGroupInviteCounter = 0;
        //lGroupInviter = 0;
        //bMobsSpawned = false;
        //lSpawnTime = System.currentTimeMillis();
        //lDeSpawnTime = Math.max(System.currentTimeMillis() + (1000 * 60 * 60), SWGGui.getRandomLong((1000*60*60), (1000*60*60*12)));
        iDefenderCount = 0;
        setPlanetID(-1);
        bPositionSet = false;
    }

    /*private Vector<TangibleItem> getEquipmentList() {
        return EquipmentList;
    }

    private void setEquipmentList(Vector<TangibleItem> EquipmentList) {
        this.EquipmentList = EquipmentList;
    }

    private void addToEquipmentList(TangibleItem t) {
        EquipmentList.add(t);
    }
    */
    protected Vector<SOEObject> getHateList() {
        return vHateList;
    }

    protected void setHateList(Vector<SOEObject> HateList) {
        this.vHateList = HateList;
    }
/*
    private void addHateListItem(SOEObject h) {
       HateList.add(h);
    }   
    
    private Vector<TangibleItem> getLootList() {
        return LootList;
    }

    private void setLootList(Vector<TangibleItem> LootList) {
        this.LootList = LootList;
    }

    private Vector<TangibleItem> getPropList() {
        return PropList;
    }

    private void setPropList(Vector<TangibleItem> PropList) {
        this.PropList = PropList;
    }

    private ConcurrentHashMap<Long, NPC> getSpawnedChildren() {
        return SpawnedChildren;
    }

    private void setSpawnedChildren(ConcurrentHashMap<Long, NPC> SpawnedChildren) {
        this.SpawnedChildren = SpawnedChildren;
    }

    private boolean isBIsFactionBase() {
        return bIsFactionBase;
    }

    private void setBIsFactionBase(boolean bIsFactionBase) {
        this.bIsFactionBase = bIsFactionBase;
    }

    private boolean isBMobIsAgressive() {
        return myLairTemplate.isBMobIsAgressive();
    }   

    private boolean isBMobIsSocial() {
        return myLairTemplate.isBMobIsSocial();
    }
    
    private boolean isBMobsSpawned() {
        return bMobsSpawned;
    }

    private void setBMobsSpawned(boolean bMobsSpawned) {
        this.bMobsSpawned = bMobsSpawned;
    }

    private boolean isBSpawnsBabies() {
        return myLairTemplate.isBSpawnsBabies();
    }    

    private boolean isBSpawnsBoss() {
        return myLairTemplate.isBSpawnsBoss();
    }    

    private boolean isBSpawnsLoot() {
        return myLairTemplate.isBSpawnsLoot();
    }
    
    private int getIBabyCount() {
        return iBabyCount;
    }

    private void setIBabyCount(int iBabyCount) {
        this.iBabyCount = iBabyCount;
    }

    private int getIBabyTemplate() {
        return myLairTemplate.getIBabyTemplate();
    }   

    private int getIBoss_template() {
        return myLairTemplate.getIBossTemplate();
    }   

    private int getICurrentStats() {
        return iCurrentStats;
    }

    private void setICurrentStats(int iCurrentStats) {
        this.iCurrentStats = iCurrentStats;
    }   

    private int getILairAiId() {
        return myLairTemplate.getILairAiId();
    }   

    private int getILairHealIncrement() {
        return myLairTemplate.getILairHealIncrement();
    }
    
    private int getILairTemplateId() {
        return myLairTemplate.getILairTemplate();
    }  

    private int getILairMaxCondition() {
        return myLairTemplate.getILairMaxCondition();
    }   

    private int getILootTableId() {
        return myLairTemplate.getILootTableId();
    }

    private int getIMaxBabies() {
        return myLairTemplate.getIMaxBabies();
    }
    */
    private int getIMaxChildren() {
        return iMaxChildren;
    }

    private int getIMaxPerWave() {
        return myLairTemplate.getIMaxPerWave();
    }    

    //private int getIMaxWaves() {
     //   return myLairTemplate.getIMaxWaves();
    //}

    private int getIMob1Template() {
        return myLairTemplate.getIMob1Template();
    }

    private int getIMob2Template() {
        return myLairTemplate.getIMob2Template();
    }
    
    private int getIMob3Template() {
        return myLairTemplate.getIMob3Template();
    }

    private int getIMob4Template() {
        return myLairTemplate.getIMob4Template();
    }

    private int getIMob5Template() {
        return myLairTemplate.getIMob5Template();
    }

    private int getIMob6Template() {
        return myLairTemplate.getIMob6Template();
    }

    private int getIMob7Template() {
        return myLairTemplate.getIMob7Template();
    }

    private int getIMob8Template() {
        return myLairTemplate.getIMob8Template();
    }
    
    private int getIMob9Template() {
        return myLairTemplate.getIMob9Template();
    }
    
/*  private int getIMobAiId() {
        return myLairTemplate.getIMobAiId();
    }

    private int getIWaveID() {
        return iWaveID;
    }

    private void setIWaveID(int iWaveID) {
        this.iWaveID = iWaveID;
    }

    private long getLGroupID() {
        return lGroupID;
    }

    private void setLGroupID(long lGroupID) {
        this.lGroupID = lGroupID;
    }

    private long getLGroupInviteCounter() {
        return lGroupInviteCounter;
    }

    private void setLGroupInviteCounter(long lGroupInviteCounter) {
        this.lGroupInviteCounter = lGroupInviteCounter;
    }

    private long getLGroupInviter() {
        return lGroupInviter;
    }
    
    private void setLGroupInviter(long lGroupInviter) {
        this.lGroupInviter = lGroupInviter;
    }
     
    private long getLMobSpawnDelayTime() {
        return lMobSpawnDelayTime;
    }

    /**
     * Allows for changing the time between the Lair being spawned and mobs spawning around it.
     * @param lMobSpawnDelayTime
     */
    //private void setLMobSpawnDelayTime(long lMobSpawnDelayTime) {
     //   this.lMobSpawnDelayTime = lMobSpawnDelayTime;
    //}
    
    /**
     * This function will pick a random location on a planetary map.
     * It is not intended when a lair will spawn indoors.
     * There will be lair templates for indoor spawning of Creature Animal mobs like the ones spawned 
     * in the geonosian lab and such.
     * The planet location for the lair is taken from the template chosen.
     */
    public boolean setLairRandomPosition(){
        if(myLairTemplate!=null)
        {
            int x,y,z;
            z=0;
            x = SWGGui.getRandomInt(SWGGui.getRandomInt(-8000,-1),SWGGui.getRandomInt(1,8000));
            y = SWGGui.getRandomInt(SWGGui.getRandomInt(-8000,-1),SWGGui.getRandomInt(1,8000));
            setX(x);
            setY(y);
            setY(z);
            boolean proceed = false;
            while(!proceed )
            {
                boolean hasclearance = true;
                ConcurrentHashMap<Long, SOEObject> vOAO = server.getAllObjects();
                Enumeration <SOEObject> oEnum = vOAO.elements();
                while(oEnum.hasMoreElements())
                {
                    SOEObject o = oEnum.nextElement();
                    if(o.getPlanetID() == getPlanetID() && ZoneServer.getRangeBetweenObjects(this, o) < LAIR_MINIMUM_DISTANCE_RADIUS)
                    {                
                        x += SWGGui.getRandomInt(-32,32);
                        y += SWGGui.getRandomInt(-32,32);
                        setX(x);
                        setY(y);
                        hasclearance = false;
                        oEnum = vOAO.elements();
                    }                
                }
                if(hasclearance)
                {
                    proceed = true;
                }
            }
            
            if(!server.setObjectHeightAndCoordinates(this))
            {
                System.out.println("Height Setting Error in Lair.setLairRandomPosition while getting setObjectHeightAndCoordinates");
            }            
            
            //setZ(getServer().getHeightAtCoodinates(getX(), getY(), getPlanetID()));
            bPositionSet = true;
            return bPositionSet;
        }
        return bPositionSet;
    }
    
    /**
     * Sets the Position of the lair on the chosen planet, planet is chosen by the lair template chosen.
     * @param x
     * @param y
     * @param z
     * @param cellID
     */
    public void setLairPosition(float x,float y,float z, long cellID){
        if(myLairTemplate!=null)
        {
            setX(x);
            setY(y);
            setZ(z);
            setCellID(cellID);
            boolean proceed = false;

            while(!proceed )
            {
                boolean hasclearance = true;
                ConcurrentHashMap<Long, SOEObject> vOAO = server.getAllObjects();
                Enumeration <SOEObject> oEnum = vOAO.elements();
                while(oEnum.hasMoreElements())
                {
                    SOEObject o = oEnum.nextElement();
                    if(o.getPlanetID() == getPlanetID() && ZoneServer.getRangeBetweenObjects(this, o) < LAIR_MINIMUM_DISTANCE_RADIUS)
                    {                
                        x += SWGGui.getRandomInt(-32,32);
                        y += SWGGui.getRandomInt(-32,32);
                        setX(x);
                        setY(y);
                        hasclearance = false;
                        oEnum = vOAO.elements();
                    }                
                }
                if(hasclearance)
                {
                    proceed = true;
                }
            }
            
            if(!server.setObjectHeightAndCoordinates(this))
            {
                System.out.println("Height Setting Error in Lair.setLairPosition while getting setObjectHeightAndCoordinates");
            }     
            setZ(getZ() + 1);//move it up one 
            
            bPositionSet = true;
            
        } else {
        	System.out.println("Lair template is null -- not setting position.");
        }
    }
    
    /**
     * Sets the template for this lair.
     * If the template requested is not valid this function returns null and will not spawn.
     * @param lairTemplateID - The template we want for the lair - same template id as on the item_template talble but taken from the server lair_template Vector
     * @param iPlanetID - The planet We want the lair on.
     * @return
     */
    public void setLairTemplate(int lairTemplateID, int iPlanetID){
        LairTemplate newTemplate = server.getLairTemplate(lairTemplateID);
        if(newTemplate!=null)
        {
            myLairTemplate = newTemplate;
            setPlanetID(iPlanetID);
            
        } else {
        	System.out.println("Null lair template retrieved from server for ID " + lairTemplateID + " on planet " + iPlanetID);
        }
        
    }
    
    /**
     * Sets the lair id to the planet specified
     * if the Boolean bSetSpecific is set to true then only the lairs for that planet will be returned
     * if the Boolean bSetSpecific is set to false then lairs for the specified planet and lairs tagged
     * as any planet or value -1 will be returned.
     * @note See Function getLairTemplatesForPlanet for more information on lair return values.
     * @param planetID
     * @param bSetSpecific
     * @return True if successful.
     */
    public void setLairRandomTemplate(int planetID, boolean bSetSpecific){
        
        Vector<LairTemplate> vLairSelections = server.getLairTemplatesForPlanet(planetID, bSetSpecific);  
        int it = 0;
        while(myLairTemplate == null && it < vLairSelections.size())
        {
            myLairTemplate = vLairSelections.get(SWGGui.getRandomInt(0, vLairSelections.size()));            
            it++;
        }
        if(myLairTemplate!=null)
        {
            setPlanetID(planetID);
            
        }
    }
    
    /**
     * Spawns the lair and creates the mobs for it so they spawn 
     * when the lair is ready.
     * @return True if successful.
     */
     public void spawn(){         
         try{
                if(myLairTemplate != null && getPlanetID() != -1 && bPositionSet)
                {
               	 //System.out.println("Spawning lair on planet " + getPlanetID() + ", coords " + getX() + ", " + getY());
                    //lSpawnTime = System.currentTimeMillis();
                    //lDeSpawnTime = System.currentTimeMillis() + SWGGui.getRandomLong(1000 * 60 * 60, 1000 * 60 * 60 * 12);
                    iWaveID = 0;            
                    iMaxChildren = myLairTemplate.getIMaxWaves() + myLairTemplate.getIMaxPerWave() + 1;            
                    iBabyCount = 0;        
                    //EquipmentList = new Vector<TangibleItem>();
                    //PropList = new Vector<TangibleItem>();
                    vSpawnedChildren = new ConcurrentHashMap<Long,NPC>();
                    vHateList = new Vector<SOEObject>();
                    //LootList = new Vector<TangibleItem>();
                    bIsLairSpawned = false;            
                    //iLairId = -1;            
                    setTemplateID(myLairTemplate.getILairTemplate());

                    setID(server.getNextObjectID());
                    //if(getID() >= 20000000) // This will now always be true -- don't check it.                    
                    //{                    
                        //setTemplateID(myLairTemplate.getILairTemplate());
                    
                    setVehicleMaxHealth(myLairTemplate.getILairMaxCondition());
                    setVehicleMaxHealthKK(PacketUtils.toKK(myLairTemplate.getILairMaxCondition()));
                    setVehicleDamage(0);
                    setVehicleDamageKK(0);
                    //}          
                    //set the lair template information.
                    ItemTemplate cT = server.getTemplateData(getTemplateID());
                    if(cT!=null)
                    {
                        setCRC(cT.getCRC());
                        setIFFFileName(cT.getIFFFileName());
                        setMaxVelocity(0f);
                        //setSTFDetailIdentifier(cT.getSTFDetailIdentifier());
                        //setSTFDetailName(cT.getSTFDetailName());
                        //setSTFFileIdentifier(cT.getSTFFileIdentifier());
                       // setSTFFileName(cT.getSTFFileName());
                        //setSTFLookAtIdentifier(cT.getSTFLookAtIdentifier());
                        //setSTFLookAtName(cT.getSTFLookAtName());
                        //setSTFLookAtName(cT.getSTFLookAtName());
                                            

                        server.addObjectToAllObjects(this,true,false);
                        lMobSpawnDelayTime += (1000 * 30);
                    } 
                    /* This HAS TO BE LAST!!!!!*/            
                    bIsLairSpawned = true;
                    //System.out.println("Lair Spawned " + bIsLairSpawned );
                     Vector<Player> vPL = server.getPlayersAroundNPC(this);
                    if(!vPL.isEmpty())
                    {
                        for(int c =0; c < vPL.size();c++)
                        {
                            Player p = vPL.get(c);
                            p.spawnItem(this);
                            p.getClient().insertPacket(PacketFactory.buildNPCUpdateTransformMessage(this));
                        }
                    }
                }
         }catch(Exception e){
	         System.out.println("Exception Caught in Lair.spawn() " + e);
	         e.printStackTrace();
         }
    }
    
    private void spawnWave(){
        try{
                //if(bIsLairSpawned) // Already checked.
                //{
                    // Lets Make up the mobs based on our remplate                
                    //adult spawn
        			int maxPerWave = getIMaxPerWave();
                    int maxChildrenForLair = getIMaxChildren();
                    Vector<SpawnedResourceData> vResourcesThisPlanet = server.getResourceManager().getResourcesByPlanetID(getPlanetID());
                    for(int i = 0; i < maxPerWave; i++)
                    {
                        //System.out.println("Spawn Number " + i + "of " + getIMaxPerWave());
                        //spawn the mobs
                        if(vSpawnedChildren.size() < maxChildrenForLair)
                        {
                            //System.out.println("Spawning Child Number " + (SpawnedChildren.size()));
                            CreatureAnimal LairChild = new CreatureAnimal();
                            LairChild.setID(server.getNextObjectID());
                            LairChild.setMyLairID(this.getID());
                            LairChild.setMyLair(this);      
                            LairChild.setResourceHealthRating((byte)SWGGui.getRandomInt(4));
                            int boneType = myLairTemplate.getBoneType();
                            int hideType = myLairTemplate.getHideType();
                            int meatType = myLairTemplate.getMeatType();
                            int milkType = myLairTemplate.getMilkType();
                            boolean bFound = false;
                            if (boneType != -1) {
                            	for (int j = 0; j < vResourcesThisPlanet.size() && !bFound; j++) {
                            		SpawnedResourceData data = vResourcesThisPlanet.elementAt(j);
                            		if (data.getIGenericResourceIndex() == (boneType + getPlanetID())) {
                            			// Found it.
                            			bFound = true;
                            			LairChild.setBoneResource(data);
                            		}
                            	}
                            }
                            bFound = false;
                            if (hideType != -1) {
                            	for (int j = 0; j < vResourcesThisPlanet.size() && !bFound; j++) {
                            		SpawnedResourceData data = vResourcesThisPlanet.elementAt(j);
                            		if (data.getIGenericResourceIndex() == (hideType + getPlanetID())) {
                            			// Found it.
                            			bFound = true;
                            			LairChild.setHideResource(data);
                            		}
                            	}
                            	
                            }
                            bFound = false;
                            if (meatType != -1) {
                            	for (int j = 0; j < vResourcesThisPlanet.size() && !bFound; j++) {
                            		SpawnedResourceData data = vResourcesThisPlanet.elementAt(j);
                            		if (data.getIGenericResourceIndex() == (meatType + getPlanetID())) {
                            			// Found it.
                            			bFound = true;
                            			LairChild.setMeatResource(data);
                            		}
                            	}
                            }
                            bFound = false;
                            if (milkType != -1) {
                            	for (int j = 0; j < vResourcesThisPlanet.size() && !bFound; j++) {
                            		SpawnedResourceData data = vResourcesThisPlanet.elementAt(j);
                            		if (data.getIGenericResourceIndex() == (milkType + getPlanetID())) {
                            			// Found it.
                            			bFound = true;
                            			LairChild.setMilkResource(data);
                            		}
                            	}
	
                            }
                            
                            
                            int rt = 0;
                            int mob_template_id = 0;
                            
                            while(rt == 0) 
                            {
                            	int templateID = SWGGui.getRandomInt(1, 9);
                                switch(templateID)
                                {
                                    case 1:{ rt = this.getIMob1Template(); mob_template_id = 1; break; }
                                    case 2:{ rt = this.getIMob2Template(); mob_template_id = 2; break; }
                                    case 3:{ rt = this.getIMob3Template(); mob_template_id = 3; break; }                                        
                                    case 4:{ rt = this.getIMob4Template(); mob_template_id = 4; break; }                                        
                                    case 5:{ rt = this.getIMob5Template(); mob_template_id = 5; break; }
                                    case 6:{ rt = this.getIMob6Template(); mob_template_id = 6; break; }
                                    case 7:{ rt = this.getIMob7Template(); mob_template_id = 7; break; }
                                    case 8:{ rt = this.getIMob8Template(); mob_template_id = 8; break; }
                                    case 9:{ rt = this.getIMob9Template(); mob_template_id = 9; break; }
                                }
                            }
                            LairChild.setTemplateID(rt);
                            
                            int xOffset = SWGGui.getRandomInt(-16, 16);
                            int yOffset = SWGGui.getRandomInt(-16, 16);
                            float x = getX() + xOffset;
                            float y = getY() + yOffset;
                            float z = server.getHeightAtCoordinates(x, y, getPlanetID());
                            LairChild.setX(x);
                            LairChild.setY(y);// + (SWGGui.getRandomInt(getServer().getGUI().getRandomInt(getServer().getGUI().getRandomInt(-1,-32),getServer().getGUI().getRandomInt(-32,-1)),getServer().getGUI().getRandomInt(getServer().getGUI().getRandomInt(-1,-32),getServer().getGUI().getRandomInt(0,32)))));
                            LairChild.setZ(z);
                            LairChild.setOrientationN(0);
                            LairChild.setOrientationS(0);
                            LairChild.setOrientationE(0);
                            LairChild.setCellID(getCellID());
                            LairChild.setPlanetID(getPlanetID());
                            LairChild.setMovementAngle(SOEObject.absoluteBearingRadians(LairChild, this));
                            LairChild.setOrientationW(LairChild.getMovementAngle());

                            LairChild.setBIsWild(true);
                            LairChild.setBIsTameable(false);

                            LairChild.setBIsPet(false);
                            LairChild.setIsBuilding(false);
                            LairChild.setBIsPlayingMusic(false);
                            LairChild.setIMaxSpawns(1);
                            LairChild.setBIsBaby(false);

                            //these need to come from a table for each creature type
                            int [] myHams = new int [9];
                            int ham = myLairTemplate.getIMaxMobHam();
                            for(int h = 0; h < 9; h++)
                            {
                                myHams[h] = ham + SWGGui.getRandomInt(-(ham / 30), (ham / 30));
                            }
                            LairChild.setHam(myHams);
                            LairChild.setRoamCenterX(getX());
                            LairChild.setRoamCenterY(getY());
                            LairChild.setRoamCenterZ(getZ()-1);                        
                            LairChild.setBCanRoam(true);
                            LairChild.setIRoamType(1);
                            LairChild.setIMaxRoamDistance(32);
                            LairChild.setLNextRoamTime(SWGGui.getRandomLong(1000*60,1000*60*5)); // Never referenced -- 
                            LairChild.setScale(1.0f, false);
                            LairChild.setIsBuilding(false);

                            ItemTemplate cT = server.getTemplateData(LairChild.getTemplateID());
                            if(cT!=null)
                            {
                                LairChild.setCRC(cT.getCRC());
                                LairChild.setIFFFileName(cT.getIFFFileName());
                                LairChild.setMaxVelocity(2f);
                                //LairChild.setSTFDetailIdentifier(cT.getSTFDetailIdentifier());
                                //LairChild.setSTFDetailName(cT.getSTFDetailName());
                                if(!myLairTemplate.getSMobNameStfFile().isEmpty() && mob_template_id != 0)
                                {
                                    if(myLairTemplate.getSMobMaleName().length >= 2 && SWGGui.getRandomInt(0, 1) == 1)
                                    {
                                        //LairChild.setSTFFileIdentifier(myLairTemplate.getSMobMaleName()[mob_template_id]);
                                        //LairChild.setSTFFileName(myLairTemplate.getSMobNameStfFile());
                                        if(LairChild.getSTFFileIdentifier().contains("motley")){
                                            LairChild.setScale(1.25f, false);                                    
                                        }
                                        else if(LairChild.getSTFFileIdentifier().contains("aggrivated")){
                                            LairChild.setScale(1.55f, false);                                    
                                        }
                                    }
                                    else if(myLairTemplate.getSMobFemaleName().length >= 2 && SWGGui.getRandomInt(0, 1) == 0)
                                    {
                                        //LairChild.setSTFFileIdentifier(myLairTemplate.getSMobFemaleName()[mob_template_id]);
                                        //LairChild.setSTFFileName(myLairTemplate.getSMobNameStfFile());
                                        LairChild.setScale(0.95f, false);                                    
                                        if(LairChild.getSTFFileIdentifier().contains("motley")){
                                            LairChild.setScale(1.10f, false);                                    
                                        }
                                        else if(LairChild.getSTFFileIdentifier().contains("aggrivated")){
                                            LairChild.setScale(1.45f, false);                                    
                                        }
                                    }
                                    else
                                    {
                                        LairChild.setTemplateID(cT.getTemplateID());
                                        //LairChild.setSTFFileIdentifier(cT.getSTFFileIdentifier());
                                        //LairChild.setSTFFileName(myLairTemplate.getSMobNameStfFile());
                                    }
                                }
                                else
                                {
                                    LairChild.setTemplateID(cT.getTemplateID());
                                    //LairChild.setSTFFileIdentifier(cT.getSTFFileIdentifier());
                                    //LairChild.setSTFFileName(cT.getSTFFileName());
                                }                        
                                //LairChild.setSTFLookAtIdentifier(cT.getSTFLookAtIdentifier());
                                //LairChild.setSTFLookAtName(cT.getSTFLookAtName());
                                //LairChild.setSTFLookAtName(cT.getSTFLookAtName());
                                LairChild.setServer(server);                       
                                this.vSpawnedChildren.put(LairChild.getID(), LairChild);                        
                                if(myLairTemplate.isBMobIsAgressive())
                                {
                                    /**
                                     * @todo need to define correct bit mask so its attackable and red
                                     */
                                    int PVPMask;
                                    PVPMask = 1 << 5 | 1 << 1 | 1;
                                    //PVPMask = 1 << 1;
                                    //PVPMask = 1 << 0;
                                    LairChild.setPVPStatus(PVPMask);
                                }
                                else
                                {
                                    LairChild.setPVPStatus(Constants.PVP_STATUS_ATTACKABLE);
                                }
                                if(server.setObjectHeightAndCoordinates(LairChild))
                                {
                                   // System.out.println("Height Setting Error in Lair.spawnWave while getting setObjectHeightAndCoordinates");
                                }   
                                server.addObjectToAllObjects(LairChild,true,false);
                                iDefenderCount = vSpawnedChildren.size();
                                Vector<Player> vPL = server.getPlayersAroundNPC(this);
                                if(!vPL.isEmpty())
                                {
                                    for(int c =0; c < vPL.size();c++)
                                    {
                                        Player p = vPL.get(c);
                                        p.spawnItem(LairChild);
                                    }
                                }
                                LairChild.generateCreatureWeapon(null);
                            }
                        }
                        else
                        {
                                
                                return;
                        }
                    }
                    //en adult spawn
                    //baby spawn
        			
                    if(vSpawnedChildren.size() < maxChildrenForLair)
                    {
                    	int maxBabyCount = myLairTemplate.getIMaxBabies();
                    	
                        if(iBabyCount < maxBabyCount)
                        {

                            //System.out.println("Spawning Baby Child Number " + (SpawnedChildren.size()));
                            CreatureAnimal LairChild = new CreatureAnimal();
                            LairChild.setID(server.getNextObjectID());
                            LairChild.setMyLairID(this.getID());
                            LairChild.setMyLair(this);
                            LairChild.setTemplateID(myLairTemplate.getIBabyTemplate());

                            LairChild.setX(getX());// + (SWGGui.getRandomInt(getServer().getGUI().getRandomInt(getServer().getGUI().getRandomInt(-1,-32),getServer().getGUI().getRandomInt(-32,-1)),getServer().getGUI().getRandomInt(getServer().getGUI().getRandomInt(-1,-32),getServer().getGUI().getRandomInt(0,32)))));
                            LairChild.setY(getY());// + (SWGGui.getRandomInt(getServer().getGUI().getRandomInt(getServer().getGUI().getRandomInt(-1,-32),getServer().getGUI().getRandomInt(-32,-1)),getServer().getGUI().getRandomInt(getServer().getGUI().getRandomInt(-1,-32),getServer().getGUI().getRandomInt(0,32)))));
                            LairChild.setZ(getZ());
                            LairChild.setOrientationN(0);
                            LairChild.setOrientationS(0);
                            LairChild.setOrientationE(0);                    
                            LairChild.setCellID(getCellID());
                            LairChild.setPlanetID(getPlanetID());
                            LairChild.setMovementAngle(LairChild.absoluteBearingRadians(LairChild, this));
                            LairChild.setOrientationW(LairChild.getMovementAngle());

                            LairChild.setBIsWild(true);
                            LairChild.setBIsTameable(true);

                            LairChild.setBIsPet(false);
                            LairChild.setIsBuilding(false);
                            LairChild.setBIsPlayingMusic(false);
                            LairChild.setIMaxSpawns(1);
                            LairChild.setBIsBaby(true);

                            //these need to come from a table for each creature type

                            int [] myHams = new int [9];
                            int maxHam = myLairTemplate.getIMaxMobHam() / 2;
                            for(int h = 0; h < 9; h++)
                            {
                                myHams[h] =maxHam + (int)SWGGui.getRandomInt(-(maxHam / 30), (maxHam / 30));
                            }
                            LairChild.setHam(myHams);
                            LairChild.setRoamCenterX(getX());
                            LairChild.setRoamCenterY(getY());
                            LairChild.setRoamCenterZ(getZ()-1);                        
                            LairChild.setBCanRoam(true);
                            LairChild.setIRoamType(1);
                            LairChild.setIMaxRoamDistance(32);
                            LairChild.setLNextRoamTime(System.currentTimeMillis() + SWGGui.getRandomLong(1000*60,1000*60*5));
                            LairChild.setScale(0.35f, false);
                            iBabyCount++;
                            LairChild.setIsBuilding(false);
                            ItemTemplate cT = server.getTemplateData(LairChild.getTemplateID());
                            if(cT!=null)
                            {
                                LairChild.setCRC(cT.getCRC());
                                LairChild.setIFFFileName(cT.getIFFFileName());
                                LairChild.setMaxVelocity(2f);
                                //LairChild.setSTFDetailIdentifier(cT.getSTFDetailIdentifier());
                                //LairChild.setSTFDetailName(cT.getSTFDetailName());
                                if(!myLairTemplate.getSMobNameStfFile().isEmpty())
                                {
                                    //LairChild.setSTFFileIdentifier(cT.getSTFFileIdentifier());                            
                                    //LairChild.setSTFFileName(myLairTemplate.getSMobNameStfFile());
                                    if(LairChild.getSTFFileIdentifier().startsWith("a") || LairChild.getSTFFileIdentifier().startsWith("e") || LairChild.getSTFFileIdentifier().startsWith("i") || LairChild.getSTFFileIdentifier().startsWith("o") || LairChild.getSTFFileIdentifier().startsWith("u"))
                                    {
                                        String Name = LairChild.getSTFFileIdentifier();
                                        Name = Name.replace("_"," ");
                                        LairChild.setFirstName("an " + Name + " (baby)");
                                    }
                                    else
                                    {
                                        String Name = LairChild.getSTFFileIdentifier();
                                        Name = Name.replace("_"," ");
                                        LairChild.setFirstName("a " + Name + " (baby)");
                                    }
                                }
                                else
                                {
                                    //LairChild.setSTFFileIdentifier(cT.getSTFFileIdentifier());
                                    //LairChild.setSTFFileName(cT.getSTFFileName());
                                    if(LairChild.getSTFFileIdentifier().startsWith("a") || LairChild.getSTFFileIdentifier().startsWith("e") || LairChild.getSTFFileIdentifier().startsWith("i") || LairChild.getSTFFileIdentifier().startsWith("o") || LairChild.getSTFFileIdentifier().startsWith("u"))
                                    {
                                        String Name = LairChild.getSTFFileIdentifier();
                                        Name = Name.replace("_"," ");
                                        LairChild.setFirstName("an " + Name + " (baby)");
                                    }
                                    else
                                    {
                                        String Name = LairChild.getSTFFileIdentifier();
                                        Name = Name.replace("_"," ");
                                        LairChild.setFirstName("a " + Name + " (baby)");
                                    }
                                }                        

                                //LairChild.setSTFLookAtIdentifier(cT.getSTFLookAtIdentifier());
                                //LairChild.setSTFLookAtName(cT.getSTFLookAtName());
                                //LairChild.setSTFLookAtName(cT.getSTFLookAtName());
                                //LairChild.setServer(this.getServer());                    
                                this.vSpawnedChildren.put(LairChild.getID(), LairChild);
                                 if(myLairTemplate.isBMobIsAgressive())
                                {
                                    int PVPMask;
                                    PVPMask = 1 << 5 | 1 << 1 | 1;
                                    LairChild.setPVPStatus(PVPMask);

                                }
                                else
                                {
                                    LairChild.setPVPStatus(Constants.PVP_STATUS_ATTACKABLE);
                                }
                                if(server.setObjectHeightAndCoordinates(LairChild))
                                {
                                   // System.out.println("Height Setting Error in Lair.spawnWave while getting setObjectHeightAndCoordinates");
                                }
                                server.addObjectToAllObjects(LairChild,true,false);
                                iDefenderCount = vSpawnedChildren.size();
                                
                                Vector<Player> vPL = server.getPlayersAroundNPC(this);
                                if(!vPL.isEmpty())
                                {
                                    for(int c =0; c < vPL.size();c++)
                                    {
                                        Player p = vPL.get(c);
                                        p.spawnItem(LairChild);
                                    }
                                }
                            } 
                        }
                    }
                    else
                    {
                            return;
                    }//end baby spawn            
                //}
                iDefenderCount = vSpawnedChildren.size();
                //do this last
                iWaveID++;
        }catch(Exception e){
            System.out.println("Exception Caught in Lair.sapawnWave() " + e);
            e.printStackTrace();
        }
    }
    
    public ConcurrentHashMap<Long, NPC> getChildren()
    {
    	return vSpawnedChildren;
    }
    
    @Override
    public void update(long lElapsedTime){
        //System.out.println("Update lair.");    
    	try{
                //updates are only performed if the lair spawn routine is completed.
                if(bIsLairSpawned)
                {
                    lMobSpawnDelayTime -= lElapsedTime;
                    if(lMobSpawnDelayTime <= 0)
                    {
                        if(iDefenderCount == 0)
                        {
                            spawnWave();
                        }
                    }                    
                }
            }catch(Exception e){
                System.out.println("Exception in Lair.Update() " + e);
                e.printStackTrace();
            }
          //  System.out.println("Lair updated.");
    }
    
    /**
     * This will remove and delete all its children and any other objects related to the lair permanently
     * and despawn all objects associated to it.
     * @return - True If Succesful
     */
    public void killLair(){
    	try{
            bIsLairSpawned = false;
            Enumeration<NPC> childEnum = vSpawnedChildren.elements();
            while (childEnum.hasMoreElements())
            {
                NPC a = childEnum.nextElement();
                server.removeFromTree(a);
                server.removeObjectFromAllObjects(a.getID());
                //SpawnedChildren.remove(a);
                iDefenderCount--;
            }
            vSpawnedChildren.clear();
            iDefenderCount = 0;
            server.removeFromTree(this);
            server.removeObjectFromAllObjects(this.getID());
        }catch(Exception e){
            System.out.println("Exception caught in Lair.killLair() " + e);
            e.printStackTrace();
        }
    }
    
    public void removeChild(NPC n) {
    	server.removeFromTree(n);
    	server.removeObjectFromAllObjects(n.getID());
    	iDefenderCount--;
    	vSpawnedChildren.remove(n);
    }
}

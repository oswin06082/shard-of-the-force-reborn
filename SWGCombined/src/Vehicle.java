import java.util.Vector;


/**
 * This is the Vehicle Object. Will be used for Vehicles and Mounts
 * @author Tomas Cruz
 */
public final class Vehicle extends NPC{

    public final static long serialVersionUID = 1;
    private long MasterID = 0;
    String sCustomName;
    protected final static String DEFAULT_NAME_STF = "monster_name";
    protected final static String DEFAULT_DETAIL_STF = "monster_detail";
    protected transient boolean bGarageMessage;
    protected boolean bIsPet;
    protected boolean bIsVehicle;
    protected transient long lVehicleSpawnTime = 0;
    protected transient long lPetSpawnTime = 0;
    protected transient long lMountSpawnTime = 0;
    
    
    public Vehicle(){
        super();
    }
    
    public Vehicle(int iVehicleMaxHealth, IntangibleObject datapadObject) {
        setMyParentObject(datapadObject);
        setVehicleMaxHealth(iVehicleMaxHealth);
        setVehicleMaxHealthKK(PacketUtils.toKK(iVehicleMaxHealth));
        setVehicleHealthDecreaseKKperMS(getVehicleMaxHealthKK() / getVEHICLE_TOTAL_SPAWN_TIME_MS()); // 436 damageKK per millisecond.
        setVehicleDamage(0);
        setVehicleDamageKK(0);
        bIsPet = false;
        bIsVehicle = false;
    }
    
    protected void setMasterID(long id){
        MasterID = id;
    }
    
    protected long getMasterID(){
        return MasterID;
    }
    
    protected void setIsPet(){
        bIsPet = true;        
        bIsVehicle = false;
    }
    
    protected void setIsVehicle(){
        bIsVehicle = true;
        bIsPet = false;  
    }
    
    protected boolean getIsPet(){
        return bIsPet;
    }
    
    protected boolean getIsVehicle(){
        return bIsVehicle;
    }
    /**
     * 
     * @param lElapsedTimeMS
     */
    @Override
    public void update(long lElapsedTimeMS) {
        //System.out.println("Vehicle.update: " + getIFFFileName() + ", " + getX() + ", " + getY() + ", " + getPlanetID());
       
      try{
        Player myMaster = (Player)this.getServer().getObjectFromAllObjects(MasterID);         
        if(myMaster==null)
        {
            return;
        }
        //look for a garage
        
        if(getDamage() >= 1 && this.getIsVehicle())
        {
            //System.out.println("-------------------------------------------------------------");
            Vector<SOEObject> vO = myMaster.getServer().getStaticObjectsAroundObject(this,30);
            boolean foundgarage = false;
            for(int i = 0; i < vO.size();i++)
            {
                SOEObject G = vO.get(i);                
                //System.out.println("Looking at Object: " + G.getIFFFileName() + " TID:" + G.getTemplateID());
                if(G!=null && G.getIFFFileName() != null && G.getIFFFileName().contains("garage"))
                {
                    this.setRadialCondition(Constants.RADIAL_CONDITION.VEHICLE_GARAGE_RANGE.ordinal());                        
                    if(!bGarageMessage)
                    {
                        //System.out.println("Found garage nearby");
                        //myMaster.getClient().insertPacket(PacketFactory.buildFlyTextSTFMessage(myMaster,"pet/pet_menu", "garage_proximity", "","", 0));
                        myMaster.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
                        		"pet/pet_menu",
                        		"garage_proximity",
                        		0,
                        		null,
                        		null,
                        		null,
                        		0,
                        		null,
                        		null,
                        		null,
                        		0,
                        		null,
                        		null,
                        		null,
                        		0,
                        		0f,
                        		true
                        ));

                        bGarageMessage = true;
                    }                        
                    foundgarage = true;
                }
            }            
            if(!foundgarage)
            {
                this.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
                bGarageMessage = false;
            }
        }
        
        if(getIsVehicle())
        {
            if(this.getVehicleSpawnTimeDelta(lElapsedTimeMS) <= 0 && !myMaster.isMounted())
            {
                //if it has been spawned longer than 6 hours lets despawn it. this gets reset every mount dismount.
                useItem(myMaster.getClient(), (byte)61);
            }
            else if(myMaster.isMounted())
            {
                //if we have been mounted for 6 hours (ouch my ass hurts) we reset the spawntime.
                setVehicleSpawnTime(1000 * 60 * 60 * 6);
            }
                
        }
        
        if(!myMaster.getStatus())
        {
            this.useItem(myMaster.getClient(), (byte)61);
        }
      }catch(Exception e){
          System.out.println("Exception Caught in Vehicle.update " +e);
          e.printStackTrace();
      }
    }
    
    protected void useItem(ZoneClient client, byte commandID){
    	Player player = client.getPlayer();
    	try{
            //System.out.println("Vehicle useItem");
            
            if(this.getTemplateID() == 0)
            {
                int TemplateID = client.getServer().getTemplateDataByCRC(getCRC()).getTemplateID();
                this.setTemplateID(TemplateID);
            }
            switch(getTemplateID())
            {                
                /**
                 *  6213, 'object/mobile/vehicle/shared_jetpack.iff', 1613040434
                    6214, 'object/mobile/vehicle/shared_landspeeder_av21.iff', 2842025402                    
                    6216, 'object/mobile/vehicle/shared_landspeeder_x31.iff', 658152450
                    6217, 'object/mobile/vehicle/shared_landspeeder_x34.iff', 1321433100
                    6218, 'object/mobile/vehicle/shared_landspeeder_xp38.iff', 1064205223
                    6219, 'object/mobile/vehicle/shared_speederbike.iff', 1922373615                    
                    6221, 'object/mobile/vehicle/shared_speederbike_flash.iff', 5125428
                    6222, 'object/mobile/vehicle/shared_speederbike_swoop.iff', 2943197007
                    
                 */
                case 6213:
                case 6214:                
                case 6216:
                case 6217:
                case 6218:
                case 6219:
                case 6221:
                case 6222:                   
                {
                    //System.out.println("Template Matched for Vehicle");
                    switch(commandID)
                    {
                        case (byte)234:
                        {
                            if(client.getPlayer().getID() != this.getMasterID())
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot repair that!"));
                            }
                            //System.out.println("Vehicle Repair Selected.");
                            int iRepairCost = (int)getDamage() * 3;
                            //@pet/pet_menu:confirm_repairs_t
                            //@pet/pet_menu:cannot_repair_disabled	You may not repair a disabled vehicle.
                            SUIWindow W = new SUIWindow(player);
                            W.setWindowType(Constants.SUI_VEHICLE_CONFIRM_REPAIRS);
                            W.setOriginatingObject(this);
                            String [] sList = new String [2];
                            sList[0] = "Do Not Repair";
                            sList[1] = "Repair";
                            client.getPlayer().setLastSuiWindowTypeInt(Constants.SUI_WINDOW_TYPE_ScriptmessageBox);
                            client.getPlayer().setLastSuiWindowTypeString("@pet/pet_menu:confirm_repairs_t");
                            //SUIScriptListBox(ZoneClient client, String WindowTypeString, String DataListTitle, String DataListPrompt, String sList[], Vector<SOEObject> ObjectList,long ObjectID, long PlayerID)
                            client.insertPacket(W.SUIScriptListBox(client, "handleSUI", "@pet/pet_menu:confirm_repairs_t", "It will cost " + iRepairCost + " credits to repair your vehicle.\r\nChoose Option Form the List Below",sList,null, 0, 0));
                            break;
                        }
                        case (byte)61: //store
                        {
                            if(client.getPlayer().getID() != this.getMasterID())
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot store that!"));
                            }
                            if(client.getPlayer().isMounted())
                            {
                                mountVehicle(client);                                
                            }
                            this.setIsSpawned(false);
                            IntangibleObject myParent = (IntangibleObject)client.getServer().getObjectFromAllObjects(getParentID());
                            if(myParent!=null)
                            {
                                myParent.getAssociatedCreature().setIsSpawned(false);
                                myParent.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
                            }                            
                            client.getServer().removeObjectFromAllObjects((SOEObject)this,true);  
                            break;
                        }
                        case (byte)205://mount dismount
                        {
                            //System.out.println("Vehicle Use Item Option 205");
                             if(client.getPlayer().getID() != this.getMasterID())
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot mount that!"));
                            }
                            if(client.getPlayer().getStance() == Constants.STANCE_STANDING || client.getPlayer().getStance() == Constants.STANCE_DRIVING)
                            {    
                                mountVehicle(client);    
                                setVehicleSpawnTime(1000 * 60 * 60 * 6);
                                bGarageMessage = false;
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_ANIMATING_SKILL)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while skill animating"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_CLIMBING)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while climbing"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_DEAD)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while dead"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_INCAPACITATED)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while incapacitated"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_KNEELING)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while kneeling"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_KNOCKED_DOWN)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while knocked down"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_PRONE)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while prone"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_SITTING)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while sitting"));
                            }                            
                            else
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount anything"));
                            }
                            break;
                        }
                        default:
                        {
                            System.out.println("Unhadled Command ID for Vehicle Mount:" + commandID + " :Template:" + getTemplateID());
                        }
                    }
                    break;
                }
                default:{
                    switch(commandID)
                    {                       
                        case (byte)61: //store
                        {
                            if(client.getPlayer().getID() != this.getMasterID())
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot store that!"));
                            }
                            if(client.getPlayer().isMounted())
                            {
                                mountVehicle(client);                                
                            }
                            this.setIsSpawned(false);
                            IntangibleObject myParent = (IntangibleObject)client.getServer().getObjectFromAllObjects(getParentID());
                            if(myParent!=null)
                            {
                                myParent.getAssociatedCreature().setIsSpawned(false);
                                myParent.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
                            }
                            client.getServer().removeObjectFromAllObjects((SOEObject)this,true);                                                        
                            break;
                        }
                        case (byte)205://mount dismount
                        {
                            //System.out.println("Vehicle Use Item Option 205");

                            if(client.getPlayer().getID() != this.getMasterID())
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot mount that!"));
                            }
                            if(client.getPlayer().getStance() == Constants.STANCE_STANDING || client.getPlayer().getStance() == Constants.STANCE_DRIVING)
                            {    
                                mountVehicle(client);    
                                setVehicleSpawnTime(1000 * 60 * 60 * 6);
                                bGarageMessage = false;
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_ANIMATING_SKILL)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while skill animating"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_CLIMBING)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while climbing"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_DEAD)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while dead"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_INCAPACITATED)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while incapacitated"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_KNEELING)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while kneeling"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_KNOCKED_DOWN)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while knocked down"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_PRONE)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while prone"));
                            }
                            else if(client.getPlayer().getStance() != Constants.STANCE_SITTING)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount while sitting"));
                            }                            
                            else
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot Mount anything"));
                            }
                            break;
                        }
                        default:
                        {
                            System.out.println("Unhadled Command ID for Vehicle Mount:" + commandID + " :Template:" + getTemplateID());
                        }
                    }
                }
            }        
        }catch(Exception e){
            System.out.println("Exception Caight in Vehicle.useItem " + e);
            e.printStackTrace();
        }
    }
    
    protected void mountVehicle(ZoneClient client){
        Player mountPlayer = client.getPlayer();
    	if(mountPlayer.isMounted())
        {
          //  System.out.println("Player is Mounted Dismounting");
            //player is mounted
            if(MasterID == mountPlayer.getID())
            {
                try{
                    client.insertPacket(PacketFactory.buildUpdateContainmentMessage(client.getPlayer(),null, -1),Constants.PACKET_RANGE_CHAT_RANGE);
                    mountPlayer.setPlayerIsNotMounted();
                    client.insertPacket(clearAllStates(true), Constants.PACKET_RANGE_CHAT_RANGE);
                    mountPlayer.setMaxVelocity(5.375f);
                    mountPlayer.setAcceleration(2.5f);
                }catch(Exception e){
                    System.out.println("Exception Caught while Attempting to DisMount a Vehicle " + e);
                    e.printStackTrace();
                }
            }
        }
        else
        {
            if(MasterID == mountPlayer.getID())
            {
                try{      
                    this.setX(client.getPlayer().getX());
                    this.setY(client.getPlayer().getY());
                    if(this.getTemplateID() == 6213)
                    {
                        this.setZ(client.getPlayer().getZ() + Constants.JETPACK_Z_AXIS_MODIFIER);
                    }
                    else
                    {
                        this.setZ(client.getPlayer().getZ());
                    }

                    mountPlayer.setPlayerIsMounted(true);
                    mountPlayer.setCurrentMount(this.getID());
                   // mountPlayer.setCellID(this.getID());
                    client.insertPacket(PacketFactory.buildUpdateTransformMessage(client.getPlayer()),Constants.PACKET_RANGE_CHAT_RANGE);
                    client.insertPacket(PacketFactory.buildUpdateContainmentMessage(client.getPlayer(), this, 4),Constants.PACKET_RANGE_CHAT_RANGE);
                    client.insertPacket(addState(Constants.STATE_MOUNTED_CREATURE, true, -1l), Constants.PACKET_RANGE_CHAT_RANGE);
                    mountPlayer.setMaxVelocity(21.9f);
                    mountPlayer.setAcceleration(10.95f);
                    mountPlayer.addState(Constants.STATE_MOUNTED_VEHICLE, -1l);
                    
                }catch(Exception e){
                    System.out.println("Exception Caught while Attempting to Mount a Vehicle " + e);
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    protected void setVehicleSpawnTime(long timeMS){
        lVehicleSpawnTime = timeMS;
    }
    protected long getVehicleSpawnTime(){
        return lVehicleSpawnTime;
    }
    
    protected long getVehicleSpawnTimeDelta(long lDeltaMS){
        lVehicleSpawnTime -= lDeltaMS;
        if(lVehicleSpawnTime < 0)
        {
            lVehicleSpawnTime = 0;
        }
        return lVehicleSpawnTime;
    }
    
    protected void setPetSpawnTime(long timeMS){
        lPetSpawnTime = timeMS;
    }
    protected long getPetSpawnTime(){
        return lPetSpawnTime;
    }
    
    protected void setMountSpawnTime(long timeMS){
        lMountSpawnTime = timeMS;
    }
    protected long getMountSpawnTime(){
        return lMountSpawnTime;
    }
    
}

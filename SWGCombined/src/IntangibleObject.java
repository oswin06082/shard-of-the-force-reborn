import java.io.IOException;
import java.util.Vector;

/**
 * The IntangibleObject class is a subclass of the generic SOEObject and represents Datapad objects such as pets, mounts
 * and starships.
 * @author Darryl
 *
 */


public class IntangibleObject extends SOEObject {
	public final static long serialVersionUID = 1;
	private String sCustomName; // This is for naming Intangible Objects.  EG:  Maach's Speederbike.  (Dunno if the client supports this or not.)
	protected final static String DEFAULT_NAME_STF = "monster_name";
	protected final static String DEFAULT_DETAIL_STF = "monster_detail";
	private SOEObject tContainer = null;
	private int iEquippedState = -1;
	private NPC linkedCreature;
        private transient long lVehicleCallDelayTime;
	private transient boolean bVehicleCallPending;
        private transient boolean bVehicleCallEnable;
        private transient ZoneClient callingClient;
        private transient boolean bPetIsCalled;
        //private int tempPVPBitmask;
        public IntangibleObject(){
            
        }
	/**
	 * Construct a new Intangible object, with the given custom name and STF string in the default STF file.
	 * @param name -- The customized name to appear for this specific object.
	 * @param sDetailSTFName -- The STF string of this object in the default STF file.
	 */
	public IntangibleObject(String name,  String sDetailSTFName) {
		super();
		sCustomName = name;
		//setSTFFileIdentifier(sDetailSTFName);
		//setSTFFileName(DEFAULT_NAME_STF);
	}
	
	/**
	 * Construct a new Intangible object, with the given STF filename, the given paramater in the STF file,
	 * the given Object ID, and the given custom item name.
	 * @param STFName -- The STF file in which this object type is found.
	 * @param sNameInSTF -- The item name contained within the STFName
	 * @param objectID -- This object's ID.
	 * @param customName -- The custom name of this specific item.
	 * 
	 */
	public IntangibleObject(String STFName, String sNameInSTF, long objectID, String customName) {
		super(STFName, sNameInSTF, objectID);
		sCustomName = customName;
		
	}
	
	/**
	 * Gets this item's customized name.
	 * @return The custom name.
	 */
	public String getCustomName() {
		return sCustomName;
	}
	
	/**
	 * Sets the container which this item is a member of.
	 * @param tContainer -- The container this object is a member of.
	 * @param bEquipped -- Indicates if this object is equipped by the container or not.
	 * @return The update containment message packet for the client.
	 * @throws IOException if an error occured creating the packet.
	 */
	public byte[] setContainer(SOEObject tContainer, int status, boolean bUpdateZone)  {
		this.tContainer = tContainer;
		setIsEquipped(status);
		if (bUpdateZone) {
			try {
				return PacketFactory.buildUpdateContainmentMessage(this, tContainer, status);	
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Gets the container which this item is a member of.
	 * @return -- The TangibleItem container.
	 */
	public SOEObject getContainer() {
		return tContainer;
	}

	/**
	 * Sets the customized item name for this IntangibleObject
	 * @param sName -- The custom name.
	 */
	public byte[] setCustomName(String sName, boolean bUpdate) throws IOException {
		sCustomName = sName;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_ITNO, (byte)3, (short)1, (short)2, this, sCustomName, true);
		}
		return null;
	}
	
	/**
	 * Sets the equipped state of this IntangibleObject. 
	 * @param bState -- The equipped state.
	 */
	public void setIsEquipped(int status) {
		iEquippedState = status;
	}
	
	/**
	 * Gets the equipped state of this IntangibleObject.
	 * @return The equipped state.
	 */
	public int getEquippedStatus() {
		return iEquippedState;
	}
	
	protected void useItem(ZoneClient client, byte commandID) {
		try{
                    switch(commandID)
                    {
                        case 14:
                        {
                            TangibleItem container = (TangibleItem)this.getContainer();
                            if(client.getPlayer().getDatapad().equals(container))
                            {
                                container.removeIntangibleObject(this);
                                client.getServer().removeObjectFromAllObjects(this, false);
                                client.getPlayer().despawnItem(this);
                            }
                            break;
                        }
                    }
                    switch(getTemplateID())
                    {
                        case 3528: //JET PACK
                        case 3529: //AV21 SPEEDER
                        case 3530: //LANDSPEEDER X31
                        case 3531: //LAND SPEEDER X34
                        case 3532: //FLASH SPEEDER
                        case 3533: //SPEEDER BIKE
                        case 3534: //SWOOP
                        {
                            switch(commandID)
                            {
                                case 60: //call
                                {
                                    if(lVehicleCallDelayTime <= 0 && !bVehicleCallPending && !client.getPlayer().hasState(Constants.STATE_COMBAT))
                                    {
                                        lVehicleCallDelayTime = 0;
                                        lVehicleCallDelayTime += 1000 * 5;                                        
                                        bVehicleCallPending = true;
                                        bVehicleCallEnable = true;
                                        callingClient = client;
                                        //call_vehicle_delay
                                        //call_pet_delay
                                        client.insertPacket(PacketFactory.buildChatSystemMessage(
                                        		"pet/pet_menu",
                                        		"call_vehicle_delay",
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
                                        		(int)(lVehicleCallDelayTime / 1000),
                                        		0f,
                                        		true
                                        ));

                                        //client.insertPacket(PacketFactory.buildFlyTextSTFMessage(client.getPlayer(),"pet/pet_menu", "call_vehicle_delay", "", "", (int)(((lVehicleCallDelayTime) / 1000))));
                                    }
                                    else if(client.getPlayer().hasState(Constants.STATE_COMBAT))
                                    {
                                        client.insertPacket(PacketFactory.buildChatSystemMessage(
                                        		"pet/pet_menu",
                                        		"cannot_call_in_combat",
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

                                        //callingClient.insertPacket(PacketFactory.buildFlyTextSTFMessage(callingClient.getPlayer(), "pet/pet_menu", "cannot_call_in_combat", "", "", 0));
                                    }     
                                    else if(bVehicleCallPending && bVehicleCallEnable && lVehicleCallDelayTime < 0)
                                    {
                                        //this is here for safety in case the npc update thread fails.
                                        bVehicleCallEnable = false;
                                        bVehicleCallPending = false;
                                        callVehicle(callingClient);
                                    }
                                    else if(bVehicleCallPending){
                                        client.insertPacket(PacketFactory.buildChatSystemMessage(
                                        		"pet/pet_menu",
                                        		"call_delay_finish_vehicle",
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
                                        		(int)(lVehicleCallDelayTime / 1000),
                                        		0f,
                                        		true
                                        ));

                                        //client.insertPacket(PacketFactory.buildFlyTextSTFMessage(client.getPlayer(),"pet/pet_menu", "call_delay_finish_vehicle", "", "", (int)(((lVehicleCallDelayTime) / 1000))));
                                    }

                                    
                                    break;
                                }
                                case 1:
                                {
                                    callVehicle(client);
                                    break;
                                }
                                case 61: //store
                                {                                     
                                    Vehicle theSwoop = (Vehicle)getAssociatedCreature();
                                    if(theSwoop!=null)
                                    {
                                        theSwoop.useItem(client, commandID);
                                    }                                  
                                    break;
                                }
                                default:
                                {

                                }
                            }                            
                            break;
                        }
                        //pet Control Devices
                        case 3329: //angler
                        case 3331: //bageraset
                        case 3333: //bearded_jax
                        case 3334: //blurrg
                        case 3335: //boar_wolf
                        case 3336: //bocatt
                        case 3337: //bol
                        case 3338: //bolle_bol
                        case 3339: //bolma
                        case 3341: //bordok
                        case 3343: //brackaset
                        case 3345: //carrion_spat
                        case 3346: //choku
                        case 3354: //cu_pa
                        case 3355: //dalyrake
                        case 3357: //dewback
                        case 3360: //dune_lizard
                        case 3361: //durni
                        case 3366: //eopie
                        case 3368: //falumpaset
                        case 3369: //fambaa
                        case 3376: //gnort
                        case 3377: //graul
                        case 3378: //gronda
                        case 3379: //gualama
                        case 3381: //guf_drolg
                        case 3384: //gurnaset
                        case 3385: //gurreck
                        case 3387: //hermit_spider
                        case 3390: //huf_dun
                        case 3391: //huurton
                        case 3393: //ikopi
                        case 3395: //kaadu
                        case 3397: //kima
                        case 3398: //kimogila
                        case 3399: //kliknik
                        case 3400: //krahbu
                        case 3401: //kusak
                        case 3402: //kwi
                        case 3403: //langlatch
                        case 3407: //malkloc
                        case 3409: //mawgax
                        case 3410: //merek
                        case 3411: //mott
                        case 3416: //narglatch
                        case 3423: //piket
                        case 3426: //pugoriss
                        case 3439: //rancor
                        case 3441: //roba
                        case 3444: //ronto
                        case 3446: //sharnaff
                        case 3448: //shear_mite
                        case 3450: //snorbal
                        case 3452: //squall
                        case 3455: //swirl_prong
                        case 3457: //thune
                        case 3459: //torton
                        case 3464: //tybis
                        case 3465: //veermok
                        case 3468: //verne
                        case 3469: //vesp
                        case 3470: //vir_vur
                        case 3475: //woolamander
                        case 3477: //zucca_boar
                        {
                            switch(commandID)
                            {
                                case 59: //store
                                {
                                    if(bPetIsCalled)
                                    {
                                        client.getServer().removeObjectFromAllObjects(this.getAssociatedCreature(), false);
                                        bPetIsCalled = false;
                                        this.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
                                        client.getPlayer().removeCalledPet((CreaturePet)this.getAssociatedCreature());
                                    }                                   
                                    break;
                                }
                                case 44: //call pet
                                {
                                    if(bPetIsCalled)
                                    {
                                        client.getServer().removeObjectFromAllObjects(this.getAssociatedCreature(), false);
                                        bPetIsCalled = false;
                                        this.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
                                        client.getPlayer().removeCalledPet((CreaturePet)this.getAssociatedCreature());
                                    }
                                    else
                                    {
                                        if(client.getPlayer().getCellID() == 0)
                                        {
                                            CreaturePet pet = (CreaturePet)this.getAssociatedCreature();
                                            pet.setPVPStatus(Constants.PVP_STATUS_IS_NORMAL_NON_ATTACKABLE);
                                            pet.setCREO3Bitmask(Constants.BITMASK_CREO3_NPC);
                                            pet.setFactionID(client.getPlayer().getFactionID());
                                            pet.setPlanetID(client.getPlayer().getPlanetID());
                                            pet.setX(client.getPlayer().getX() + 1.2f);
                                            pet.setY(client.getPlayer().getY() + 1.2f);
                                            pet.setZ(client.getPlayer().getZ());
                                            client.getServer().addObjectToAllObjects(pet, true, false);
                                            bPetIsCalled = true;
                                            this.setRadialCondition(Constants.RADIAL_CONDITION.PET_CONDITION_CALLED.ordinal());
                                            client.getPlayer().addCalledPet(pet);
                                            client.getPlayer().setTargetID(0);
                                            String [] sCommand = new String [1];
                                            sCommand[0] = "silent";
                                            pet.petCommand(client, (byte)142, sCommand);
                                            pet.petCommand(client, (byte)149, sCommand);
                                            pet.setIsInTrainingMode(false);
                                        }
                                        else
                                        {
                                            client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot call a pet indoors."));
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        default:
                        {
                            //System.out.println("IntangibleObject.useItem Template ID not handled " + getTemplateID());
                        }
                    }

                }catch(Exception e){
                    System.out.println("Exception Caught at IntangibleObject.useItem " + e);
                    e.printStackTrace();
                }
	}
	
	protected NPC getAssociatedCreature() {
		return linkedCreature;
	}
	
	protected void setAssociatedCreature(NPC n) {
		linkedCreature = n;
	}
        
        /**
         * Updates stuff in the object.
         * @param lElapsedTimeMS
         */
        
        protected void update(long lElapsedTimeMS) {
            //System.out.println("Intangible Update");
            try{
                
                if(callingClient!= null && callingClient.getPlayer().hasState(Constants.STATE_COMBAT) && bVehicleCallEnable)            
                {
                    bVehicleCallEnable = false;
                    lVehicleCallDelayTime = 0;
                    bVehicleCallPending = false;
                    //callingClient.insertPacket(PacketFactory.buildFlyTextSTFMessage(callingClient.getPlayer(), "pet/pet_menu", "cannot_call_in_combat", "", "", 0));
                    callingClient.insertPacket(PacketFactory.buildChatSystemMessage(
                    		"pet/pet_menu",
                    		"cannot_call_in_combat",
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
                    callingClient.insertPacket(PacketFactory.buildChatSystemMessage("Call Vehicle Cancelled"));
                }
                lVehicleCallDelayTime -= lElapsedTimeMS;
                if(bVehicleCallEnable && lVehicleCallDelayTime <= 0)
                {
                    bVehicleCallEnable = false;
                    bVehicleCallPending = false;
                    callVehicle(callingClient);
                    callingClient.insertPacket(PacketFactory.buildChatSystemMessage("Vehicle Generated"));
                }
                
                
                
            }catch(Exception e){
                System.out.println("Exception caught in Intangible.update " + e);
                e.printStackTrace();
            }
        }
	/*private void callPet(ZoneClient client){
            
        }
        private void callMount(ZoneClient client){
            
        }*/
        private void callVehicle(ZoneClient client){
                
                Vehicle theVehicle = (Vehicle)getAssociatedCreature();
                theVehicle.setParentID(getID());
                setAssociatedCreature(theVehicle);
                setRadialCondition(Constants.RADIAL_CONDITION.INTANGIBLE_VEHICLE_SPAWNED.ordinal());
                //boolean bIsSpawned = theVehicle.getIsSpawned();
                theVehicle.setIsSpawned(true);
                theVehicle.setX(client.getPlayer().getX()+1);
                theVehicle.setY(client.getPlayer().getY()+1);
                if(theVehicle.getTemplateID() == 6213)
                {
                    theVehicle.setZ(client.getPlayer().getZ() + Constants.JETPACK_Z_AXIS_MODIFIER);
                }
                else
                {
                    theVehicle.setZ(client.getPlayer().getZ());
                }
                theVehicle.setOrientationN(client.getPlayer().getOrientationN());
                theVehicle.setOrientationE(client.getPlayer().getOrientationE());
                theVehicle.setOrientationS(client.getPlayer().getOrientationS());
                theVehicle.setOrientationW(client.getPlayer().getOrientationW());
                theVehicle.setLinkID(client.getPlayer().getID());
                theVehicle.setCellID(0);
                theVehicle.setPlanetID(client.getPlayer().getPlanetID());
                theVehicle.setStance(null, Constants.STANCE_STANDING, true);
                theVehicle.setFactionID(client.getPlayer().getFactionID());
                theVehicle.setCREO3Bitmask(Constants.BITMASK_CREO3_VEHICLE);
                int iPVPBitmask = client.getPlayer().getPVPStatus();
                iPVPBitmask = iPVPBitmask & ~Constants.PVP_STATUS_IS_PLAYER;
                iPVPBitmask = iPVPBitmask | Constants.PVP_STATUS_IS_NPC;
                theVehicle.setPVPStatus(iPVPBitmask);
                theVehicle.setFactionRank(client.getPlayer().getFactionRank());
                theVehicle.clearAllStates(false);
                theVehicle.setVehicleSpawnTime(1000 * 60 * 60 * 6);
                int currentSwoopDamage = theVehicle.getDamage();
                int swoopHealthK = PacketUtils.toK(theVehicle.getHealth());
                int damageToApplyK = swoopHealthK / 20;
                int damageToApply = Math.min(1, PacketUtils.unK(damageToApplyK));
                currentSwoopDamage += damageToApply;
                theVehicle.setDamage(currentSwoopDamage);
                theVehicle.setServer(client.getServer());
                client.getServer().addObjectToAllObjects(theVehicle, true,false);
                try {
                        
                        //player.spawnItem(theVehicle, true);
                        Vector<Player> vPlayersInRange = client.getServer().getPlayersAroundObject(theVehicle, true);
                        if(!vPlayersInRange.isEmpty()) {
                                for (int i = 0; i < vPlayersInRange.size(); i++) {
                                        vPlayersInRange.elementAt(i).spawnItem(theVehicle);
                                }
                        }
                } catch (Exception e) {
                        System.out.println("Error spawning swoop: " + e.toString());
                        e.printStackTrace();

                        // D'oh!
                }
                
                return;
        }
}

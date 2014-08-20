import java.io.IOException;
import java.util.Vector;

/**
 * The TangibleItem class represents an actual item in the SWG game world.  For example:  A painting would be a TangibleItem.
 * @author Darryl
 *
 */

public class TangibleItem extends SOEObject implements Experimentable {
	public final static long serialVersionUID = 1;
	private String sName;
	private String sUnknown1;
	private String sUnknown2;
	private int iEquippedStatus = -1;
	private long iContainerID;
	private long iOwnerID;
	private SOEObject iContainer;
	private Vector<TangibleItem> vLinkedObjects; // Inventory container specific
	private Vector<IntangibleObject> vContainedIntangibleObjects; // Datapad specific
	private String sUserOptions = null;
	private int iCurrentCondition;
	private int iMaxCondition;    
	private int iSocketCount = 0;
    private boolean bHasSockets = false;
    private int iSocketsLeft = 0;
    private Vector<SocketAttachment> vAttachmentList;
    private int iToolSurveyRange = -1; // Survey tool specific
    private Vector<MissionObject> vMissionList; // Mission bag specific
    private Vector<MissionObject> vEmptyMissionList;
    private Vector<MissionObject> vAcceptedMissionList;
    
    private boolean bSendsEquipedState = true;
    private boolean bIsNoTradeItem;
    private boolean bIsBioLinkItem;
    private long lBiolinkID;
    private float fComplexity = 1.0f;
    private long lCrafterID;
    private Vector<SkillModifier> vSkillModifiers;
    private long lSaleTimeMS = 0l;
    private BazaarTerminal saleTerminal = null;
    private boolean bInstantSale = true;
    private int iCurrentBidPrice = 0;
    private int iMaxBidPrice = 20000;
    private int iStackQuantity = 0;
    
    private transient boolean bIsSurveying = false; // Survey tool specific
    private transient long lSurveyTimeMS = 0;
    private transient boolean bIsSampling = false;
    private transient boolean bSamplingCoolDown = false;
    private transient long lSampleTimeMS = 0;
    private transient SpawnedResourceData resourceToSurvey;
    
    private transient Vector<MissionObject> vRefreshedMissionList; // Mission bag specific
    

        
    /**
	 * Constructs a new TangibleItem.
	 */
	public TangibleItem() {
		super();
		vLinkedObjects = new Vector<TangibleItem>();
		vContainedIntangibleObjects = new Vector<IntangibleObject>();
        vAttachmentList = new Vector<SocketAttachment>();
        iToolSurveyRange = -1;
        vMissionList = new Vector<MissionObject>();
        vAcceptedMissionList = new Vector<MissionObject>();
        vEmptyMissionList = new Vector<MissionObject>();
        bSendsEquipedState = true;
        lCrafterID = 0;
	}
	
	private transient byte iSurveyToolType = -1;
	
	public void setSurveyToolType() {
		switch (getTemplateID()) {
		case 14036: {
			// "All???"
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_MINERAL;
			break;
		}
		case 14037: {
			// Gas
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_GAS;
			break;
		}
		case 14038: {
			// Inorganic
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_MINERAL;
			break;
		}
		case 14039: { // Chemical
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_CHEMICAL;
			break;
		}
		case 14040: {
			// Flora
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_FLORA;
			break;
		}
		case 14041: {
			// Mineral
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_MINERAL;
			break;
		}
		case 14042: {
			// Water
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_WATER;
			break;
		}
		case 14043: { 
			// "Organic"
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_FLORA;
			break;
		}
		case 14044: {
			// Solar
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_GAS;
			break;
		}
		case 14045: {
			// Wind
			iSurveyToolType = SpawnedResourceData.SURVEY_EFFECT_GAS;
			break;
		}
		}
		
	}

	protected void update(long lElapsedTimeMS, ZoneServer server, ZoneClient client, Player player) throws IOException {
            SOEObject itemContainer = getContainer();
            //System.out.println("TangibleItem " + getIFFFileName() + " in container " + itemContainer.getIFFFileName());
            long playerCellID = player.getCellID();
            if(playerCellID == 0) {
            	//System.out.println("Container outside.");
            	getContainer().setCellID(0);
                setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());
            } else {
                // condition if indoors for radials
            	//System.out.println("Container in cell ID " + itemContainer.getCellID());
            	getContainer().setCellID(playerCellID);
                setRadialCondition(Constants.RADIAL_CONDITION.TANGIBLE_ITEM_INDOORS.ordinal());
            }
            if (bIsSurveying) {
            	lSurveyTimeMS -= lElapsedTimeMS;
            	//System.out.println("TangibleItem surveying.  Time left: " + lSurveyTimeMS + " milliseconds.");
            	if (lSurveyTimeMS <= 0) {
            		//System.out.println("Time to send survey result.");
            		bIsSurveying = false;
            		lSurveyTimeMS = 0;
            		try {
            			client.insertPacket(PacketFactory.buildSurveyMessage(iToolSurveyRange, resourceToSurvey, player));
            			//client.insertPacket(PacketFactory.buildSurveyMessage(5, resourceToSurvey, player));
            		} catch (Exception e) {
            			System.out.println("Error building survey message: " + e.toString());
            			e.printStackTrace();
            		}
            	}
            } else if (bIsSampling) {
            	if (player.getStance() != Constants.STANCE_KNEELING) {
            		stopSurveying();
            		player.setIsSampling(false);
            	} else {
	            	lSampleTimeMS -= lElapsedTimeMS;
	            	if (lSampleTimeMS <= 0) {
	            		// Apply damage
	        			int damageToApply = player.getSkillMod("surveying").getSkillModModdedValue() - Constants.SKILLMOD_CAP; // This will be a negative number, which we will "add" to the player's Action
	        			int subsidiaryDamage = damageToApply / 10;
	        			
	        			if (!player.canPerformAction(subsidiaryDamage, damageToApply, subsidiaryDamage)) {
	        				client.insertPacket(PacketFactory.buildChatSystemMessage("error_message", "sample_mind", 0l, "", "", "", 0l, "", "", "",0l, "", "", "",0, 0f, false));
	        				player.setStance(null, Constants.STANCE_STANDING, false); // This function call stops the surveying process.
	        			} else {
	            			player.updateCurrentHam(Constants.HAM_INDEX_ACTION, damageToApply);
	            			player.updateCurrentHam(Constants.HAM_INDEX_HEALTH, subsidiaryDamage);
	            			player.updateCurrentHam(Constants.HAM_INDEX_MIND, subsidiaryDamage);
		            		int stackSize = server.getResourceManager().getSampledResourceAmount(resourceToSurvey, player); // Null pointer here.
		            		if (stackSize > 0) {
	                                    //sample_located	You successfully locate a %DI unit sample of %TO.
		            			
	                            client.insertPacket(PacketFactory.buildChatSystemMessage("survey", "sample_located", 0l, "", "", "", 0l, "", "", "", 0l, resourceToSurvey.getStfFileName(), resourceToSurvey.getIffFileName(), resourceToSurvey.getName(), stackSize, 0f, false));
		            			long resourceTypeID = resourceToSurvey.getID();
		            			int resourceIndex = resourceToSurvey.getIGenericResourceIndex();
		            			if ((resourceIndex >= Constants.RESOURCE_START_RADIOACTIVE && resourceIndex <= Constants.RESOURCE_END_RADIOACTIVE)
		            				|| resourceIndex == Constants.RESOURCE_TYPE_JTL_RADIOACTIVE_PLOYMETRIC) {
		            				// Add ham wounds of 300 per bar.
		            				// Decrement HAMS by 500 per bar.
		            				// If a ham < 0, incapacitate the player.
		            				for (int i = 0; i < 9; i++) {
		            					player.updateHAMWounds(i, 300, true);
		            					if (i % 3 == 0) {
		            						player.updateCurrentHam(i, -500);
		            					}
		            				}
		            				player.updateBattleFatigue(300, true);
		            			}
		            			ResourceContainer container = player.getLastUpdatedResourceContainer();
		            			System.out.println("Found resource container!");
		            			boolean bCouldUpdate = (container != null);
		            			if (container != null) {
		            				if (container.getResourceSpawnID() == resourceTypeID) {
		            					System.out.println("Resource spawn ID of container == current sampled resource.");
	            						int quantity = container.getStackQuantity();
	            						if (quantity < (Constants.MAX_STACK_SIZE - stackSize)) {
	        								client.insertPacket(container.setStackQuantity(quantity + stackSize, true));
	        								player.getClient().insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), container));
	            						} else {
	            							ResourceContainer newResourceContainer = new ResourceContainer(container, quantity);
	            							player.setLastUpdatedResourceContainer(newResourceContainer);
											player.getClient().insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), newResourceContainer));
	            						}
			            			} else {
			            				System.out.println("Resource spawn ID of container != current sampled resource.");
			            				bCouldUpdate = false;
			            			}
		            			} 
		            			
		            			if (!bCouldUpdate){
		            				System.out.println("Could not update current resource container -- searching for a match in inventory.");
		            				boolean bFound = false;
		            				Vector<TangibleItem> vInventoryItems = player.getInventoryItems();
		            				for (int i = 0; i < vInventoryItems.size() && !bFound; i++) {
		            					TangibleItem item = vInventoryItems.elementAt(i);
		            					if (item instanceof ResourceContainer) {
		            						ResourceContainer tempContainer = (ResourceContainer) item;
		            						if (tempContainer.getResourceSpawnID() == resourceTypeID) {
		            							player.setLastUpdatedResourceContainer(tempContainer);
		            							container = tempContainer;
		            							bFound = true;
		            						}
		            					}
		            				}
		            				if (!bFound) {
		            					System.out.println("No match found in inventory -- creating new resource container.");
			            				container = new ResourceContainer();
			            				player.setLastUpdatedResourceContainer(container);
			            				//container.setID(client.getServer().getNextObjectID());
			            				int containerTemplateID = resourceToSurvey.getResourceContainerTemplateID();
			            				container.setTemplateID(containerTemplateID);
			            				container.setName(resourceToSurvey.getResourceType(), false);
			            				container.setCustomizationData(null);
			            				container.setOwner(player);
			            				container.setEquipped(player.getInventory(), -1);
			            				player.addItemToInventory(container);
			            				container.setID(server.getNextObjectID());
			            				container.setConditionDamage(0, false);
			            				container.setMaxCondition(100, false);
			            				container.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
			            				container.setResourceSpawnID(resourceToSurvey.getID());
			            				container.setResourceType(resourceToSurvey.getIffFileName());
			            				
			            				container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_NAME, resourceToSurvey.getName()));
			            				container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_CLASS, resourceToSurvey.getResourceClass() + " " + resourceToSurvey.getResourceType()));
			            		
			            				server.addObjectToAllObjects(container, false, false);
			            				short coldResist = resourceToSurvey.getColdResistance();
			            				short conductivity = resourceToSurvey.getConductivity();
			            				short decayResist = resourceToSurvey.getDecayResistance();
			            				short entangleResist = resourceToSurvey.getEntangleResistance();
			            				short flavor = resourceToSurvey.getFlavor();
			            				short heatResist = resourceToSurvey.getHeatResistance();
			            				short malleability = resourceToSurvey.getMalleability();
			            				short overallQuality  = resourceToSurvey.getOverallQuality();
			            				short potentialEnergy = resourceToSurvey.getPotentialEnergy();
			            				short shockResist = resourceToSurvey.getShockResistance();
			            				short unitToughness = resourceToSurvey.getUnitToughness();
			            				
			            				if (coldResist != 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_COLD_RESIST, String.valueOf(coldResist)));
			            				}
			            				if (conductivity != 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_CONDUCTIVITY, String.valueOf(conductivity)));
			            				}
			            				if (decayResist != 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_DECAY_RESIST, String.valueOf(decayResist)));
			            				}
			            				if (entangleResist != 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_ENTANGLE_RESISTANCE, String.valueOf(entangleResist)));
			            				}
			            				if (flavor != 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_FLAVOR, String.valueOf(flavor)));
			            				}
			            				if (heatResist!= 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_HEAT_RESIST, String.valueOf(heatResist)));
			            				}
			            				if (malleability != 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_MALLEABILITY, String.valueOf(malleability)));
			            				}
			            				if (overallQuality != 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_QUALITY, String.valueOf(overallQuality)));
			            				}
			            				if (potentialEnergy!= 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY, String.valueOf(potentialEnergy)));
			            				}
			            				if (shockResist!= 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE, String.valueOf(shockResist)));
			            				}
			            				if (unitToughness != 0) {
			            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_TOUGHNESS, String.valueOf(unitToughness)));
			            				}
		            					container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_CONTENTS, String.valueOf(stackSize) + "/" + String.valueOf(Constants.MAX_STACK_SIZE)));
		            					player.spawnItem(container);
		            				}
		            				try {
		            					System.out.println("Update container quantity.");
		            					int previousQuantity = container.getStackQuantity();
		            					client.insertPacket(container.setStackQuantity(previousQuantity + stackSize, true));
		            					
		            					client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), container));
		            				} catch (Exception e) {
		            					System.out.println("Error spawning new resource container: " + e.toString());
		            					e.printStackTrace();
		            				}
		            			}
		    					player.updateExperience(null, DatabaseInterface.getExperienceIDFromName("resource_harvesting_inorganic"),(int)(stackSize * 2.5f));
		            		} else {
                                 client.insertPacket(PacketFactory.buildChatSystemMessage("survey", "sample_failed", 0l, "", "", "", 0l, "", "", "", 0l, resourceToSurvey.getStfFileName(), resourceToSurvey.getIffFileName(), resourceToSurvey.getName(), 0, 0f, false));
		            		}
			            	int surveySkillMod = player.getSkillMod("surveying").getSkillModModdedValue();
		            		lSampleTimeMS = 12000 - (Math.min(Constants.SKILLMOD_CAP, surveySkillMod) * 50);
		            		System.out.println("New sample time: " + lSampleTimeMS + " milliseconds.");
		            		bSamplingCoolDown = true;
		            		bIsSampling = false;
		            		//client.insertPacket(PacketFactory.buildChatSystemMessage("survey", "tool_recharge_time", 0l, "", "","", 0l, "", "","", 0l, "", "", "", (int)(lSampleTimeMS / 1000), 0f, false));
	        			}
	            	}
            	}
            } else if (bSamplingCoolDown) {
            	lSampleTimeMS -= lElapsedTimeMS;
            	if (lSampleTimeMS < 0) {
            		bSamplingCoolDown = false;
            		bIsSampling = true;
            		lSampleTimeMS = 3000;
                    playSampleEffect(client);
            	}
            }
    	}
        
    protected void playSampleEffect(ZoneClient client){
        try{
            client.insertPacket(PacketFactory.buildClientEffectAtLocation(SpawnedResourceData.SURVEY_SAMPLE_CLIENT_EFFECTS[iSurveyToolType], client.getPlayer()), Constants.PACKET_RANGE_CHAT_RANGE);
        }catch(Exception e){
            
        }
    }
    
    protected void insertAttachment(SocketAttachment A){
        vAttachmentList.add(A);
    }
    
    protected void removeAttachment(SocketAttachment A){
        vAttachmentList.remove(A);
    }
    
    
    protected void setHasSockets(boolean b){
        bHasSockets = b;
    }
    
    protected boolean getHasSockets(){
        return bHasSockets;
    }
    
    protected void setSocketCount(int count){
        iSocketCount = count;
    }
    
    protected int getSocketCount(){
        return iSocketCount;
    }
    
    protected void setSocketsLeft(int count){
        iSocketsLeft = count;
    }
    
    protected int getSocketsLeft(){
        return iSocketsLeft;
    }
        
	/**
	 * Gets the display name of this TangibleItem.  (Maach's Moraj Melon, for example)
	 * @return
	 */
	protected String getName() {
		return sName;
	}
	
	/**
	 * Sets the display name of this TangibleItem.
	 * @param sName -- The name.
	 */
	protected byte[] setName(String sName, boolean bUpdate) throws IOException{
		this.sName = sName;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)2, this, sName, true);
		}
		return null;
	}
	
	/**
	 * Sets an unknown string.
	 * @param s -- The String.
	 */
	protected void setUnknownString1(String s) {
		this.sUnknown1 = s;
	}
	
	/**
	 * Sets an unknown string.
	 * @param s -- The string.
	 */
	protected void setUnknownString2(String s) {
		this.sUnknown2 = s;
	}
	
	/**
	 * Gets an unknown string.
	 * @return The string.
	 */
	protected String getUnknownString1() {
		return sUnknown1;
	}
	
	/**
	 * Gets an unknown string.
	 * @return The string.
	 */
	protected String getUnknownString2() {
		return sUnknown2;
	}
	
	/**
	 * Gets if this object is equipped by it's container.
	 * @return The equipped status.
	 */
	protected int getEquippedStatus() {
		return iEquippedStatus;
	}
	
	/**
	 * Gets this object's owner's / container's ID.
	 * @return The object owner ID.
	 */
	protected long getOwnerID() {
		return iOwnerID;
	}

	/**
	 * Gets the "owner" object of this TangibleItem.  IE the container it is in.
	 * @return The owner.
	 */
	protected SOEObject getContainer() {
		return iContainer;
	}
	
	/**
	 * Sets this TangibleItem to be contained in the given object, and sets if this is equipped by that object or not.
	 * @param o -- The container.
	 * @param bIsEquipped -- If the container has equipped this item.
	 */
	protected void setEquipped(SOEObject o, int status) {
		iContainer = o;
		if (o != null) {
			//System.out.println("TangibleItem::setEquipped -- object equipped on is not null.");
			this.iContainerID = o.getID();
		} else {
			//System.out.println("TangibleItem::setEquipped -- object equipped on is null!");
			this.iContainerID = 0;
		}
		//System.out.println("Am I equipped? " + bIsEquipped);
		iEquippedStatus = status;
	}

	protected void setOwner(Player p) {
		setOwnerID(p.getID());
	}
	/**
	 * Sets the owner ID for this item.
	 * @param ID -- The owner ID.
	 */
	protected void setOwnerID(long ID) {
		iOwnerID = ID;
        iToolSurveyRange = -1;
	}
	
	/**
	 * Gets the object ID of this item's container.
	 * @return The container ID.
	 */
	protected long getContainerID() {
		if (iEquippedStatus != -1) {
			return iContainerID;
		} else {
			return 0;
		}
	}

	/**
	 * Adds a TangibleItem object to the list of objects this TangibleItem contains.
	 * @param t The object to contain.
	 */
	protected void addLinkedObject(TangibleItem t) {
		vLinkedObjects.add(t);
        t.setEquipped(this, -1);
	}
	
	/**
	 * Removes a TangibleItem object from the list of objects this TangibleItem contains.
	 * @param t -- The object to remove.
	 * @return True if the TangibleItem was contained by this TangibleItem, otherwise false.
	 */
	protected boolean removeLinkedObject(TangibleItem t) {
		return vLinkedObjects.remove(t);
	}
	
	/**
	 * Gets the list of TangibleItem object which this TangibleItem contains.
	 * @return The list of contained items.
	 */
	protected Vector<TangibleItem> getLinkedObjects() {
		return vLinkedObjects;
	}
	
	/**
	 * Adds an IntangibleObject to the group of IntangibleObjects that this TangibleItem contains.  
	 * @param o The object.
	 */
	protected void addIntangibleObject(IntangibleObject o) {
		if (vContainedIntangibleObjects == null) {
			vContainedIntangibleObjects = new Vector<IntangibleObject>();
		}
		if (!vContainedIntangibleObjects.contains(o)) {
			vContainedIntangibleObjects.add(o);
		}
	}
	
	/**
	 * Gets and removes an IntangibleObject from the group of IntangibleObjects that this TangibleItem contains.
	 * @param o The object to get
	 * @return True if the object was contained by this TangibleItem, otherwise false.
	 */
	protected boolean removeIntangibleObject(IntangibleObject o) {
		return vContainedIntangibleObjects.remove(o);
	}
	
	/**
	 * Gets the list of IntangibleObject contained by this TangibleItem.
	 * @return The list of Intangibles.
	 */
	protected Vector<IntangibleObject> getIntangibleObjects() {
		return vContainedIntangibleObjects;
	}
	
	/**
	 * Meant for inventory items that need a setting applied to them to be used, this function sets the setting.
	 * For example, Resource Survey Tools need to know at what range they are surveying.
	 * @param sOption -- The option that the user has chosen.
	 */
	protected void setUserOptions(String sOption) {
		sUserOptions = sOption;
	}
	
	/**
	 * Gets the option the user has previously chosen for this item.
	 * @return The option.
	 */
	protected String getUserOptions() {
		return sUserOptions;
	}
	
    // This function will almost never be checking the template ID once we are done -- it will be overridden by the subclasses (Weapon, Armor, Food, MedicalItem, etc. etc.)  
	protected void useItemByCommandID(ZoneClient client, byte commandID) {
		Player player = client.getPlayer();
		try{
			int iTemplateID = getTemplateID();
            switch(iTemplateID)
            {
                case 14037:
                case 14038:
                case 14039:
                case 14040:
                case 14041:
                case 14042:
                case 14043:
                case 14044:                        
                case 14045:    
                {
                    
                    switch(commandID)
                    {
                        case 20:
                        {
                            if(client.getPlayer().getCellID() != 0)
                            {
                                client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot survey indoors."));
                                return;
                            }
                            //use tool
                            System.out.println("Use Tool Requested");
                            if(iToolSurveyRange == -1)
                            {
                                useItemByCommandID(client, (byte)0x85);
                                return;
                            }        
                            surveyToolOpenSurveyWindow(client);
                            client.getPlayer().setLastUsedSurveyTool(this);                                    
                            break;
                        }
                        case 14:
                        {
                            //destroy
                            System.out.println("Destroy Tool Requested");
                            break;
                        }
                        case (byte)0x85://set range 133val
                        {
                            //survey_range	Set Range
                            //survey_resolution	Set Resolution
                            System.out.println("Set Tool Range Requested");
                            SUIWindow W = new SUIWindow(player);
                            W.setWindowType(Constants.SUI_SELECT_SURVEY_RESOLUTION);
                            W.setOriginatingObject(this);
                            String[] tList = new String [5];                                    
                            tList[0] = "64m x 3pts";
                            int optcnt = 1;
                            if(client.getPlayer().getPlayData().hasSkill(85))
                            {
                                tList[1] = "128m x 4pts";
                                optcnt++;
                            }
                            if(client.getPlayer().getPlayData().hasSkill(86))
                            {
                                tList[2] = "192m x 4pts";
                                optcnt++;
                            }
                            if(client.getPlayer().getPlayData().hasSkill(87))
                            {
                                tList[3] = "256m x 5pts";
                                optcnt++;
                            }
                            if(client.getPlayer().getPlayData().hasSkill(88))
                            {
                                tList[4] = "320m x 5pts";
                                optcnt++;
                            }
                            String[] sList = new String [optcnt];
                            for(int i = 0; i < optcnt; i++)
                            {
                                sList[i] = tList[i];
                            }
                            
                            String WindowTypeString = "handleSetRange";
                            String DataListTitle = "@sui:survey_resolution";
                            String DataListPrompt = "@survey:select_range";                                               
                            client.insertPacket(W.SUIScriptListBox(client, WindowTypeString, DataListTitle,DataListPrompt, sList, null, 0, 0));
                            break;
                        }
                        default:
                        {
                            client.getServer().getResourceManager().generateResourceListForSurveyMessage(client, this);
                        }
                    }
                    break;
                }
                case 14048: //bank terminal
                {
                    switch(commandID)
                    {
                        
                        case 20:  // 45, 1989986645, 1, 0, 20, '', '', 3, 0
                        case 7:   //   46, 1989986645, 2, 0, 7, '', '', 3, 0
                            break;
                        case 107: //   47, 1989986645, 3, 1, 107, '@sui:bank_credits', '', 3, 0
                        {
                            SUIWindow W = new SUIWindow(player);
                            W.setWindowType(Constants.SUI_BANK_WINDOW);
                            W.setOriginatingObject(client.getPlayer());
                            String sWindowTypeString = "handleDepositWithdraw";
                            String sTransferBoxTitle = "@base_player:bank_title";
                            String sTransferBoxPrompt = "@base_player:bank_prompt";
                            String sFromLabel = "Cash";
                            String sToLabel = "Bank";
                            int iFromAmount = client.getPlayer().getInventoryCredits();
                            int iToAmount = client.getPlayer().getBankCredits();
                            int iConversionRatioFrom = 1; 
                            int iConversionRatioTo = 1;
                            
                            client.insertPacket(W.SUIScriptTransferBox(client, sWindowTypeString, sTransferBoxTitle, sTransferBoxPrompt, sFromLabel, sToLabel, iFromAmount, iToAmount, iConversionRatioFrom, iConversionRatioTo));
                            break;
                        }
                        case 108: //   48, 1989986645, 4, 1, 108, '@sui:bank_items', '', 3, 0 //safety deposit
                        {                                       
                            client.insertPacket(PacketFactory.buildOpenContainerMessage(client.getPlayer().getBank(), 0));
                            client.insertPacket(PacketFactory.buildOpenContainerMessage(client.getPlayer().getInventory(), -1));
                            break;
                        }
                        case 109: //   49, 1989986645, 5, 1, 109, '@sui:bank_depositall', '', 3, 0
                        {
                            int iInvCredits = client.getPlayer().getInventoryCredits();
                            client.getPlayer().transferInventoryCreditsToBank();
                            
                            client.insertPacket(PacketFactory.buildChatSystemMessage(
                            		"base_player",
                            		"prose_deposit_success",
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
                            		iInvCredits,
                            		0f,
                            		true
                            ));
                            //client.insertPacket(PacketFactory.buildFlyTextSTFMessage(client.getPlayer(), "base_player", "prose_deposit_success", "", "", iInvCredits));
                            break;
                        }
                        case 110: //   50, 1989986645, 6, 1, 110, '@sui:bank_withdrawall', '', 3, 0
                        {
                            int iBankCredits = client.getPlayer().getBankCredits();
                            client.getPlayer().transferBankCreditsToInventory();
                            client.insertPacket(PacketFactory.buildChatSystemMessage(
                            		"base_player",
                            		"prose_withdraw_success",
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
                            		iBankCredits,
                            		0f,
                            		true
                            ));

                            //client.insertPacket(PacketFactory.buildFlyTextSTFMessage(client.getPlayer(), "base_player", "prose_withdraw_success", "", "", iBankCredits));
                            break;
                        }
                        default:
                        {
                            client.insertPacket(PacketFactory.buildChatSystemMessage("Bank Terminal Use Clicked unhandled commandID " + (int)commandID));
                        }
                    }
                    break;
                } 
                case 9702: 
                {
                    //Item IFF Name: object/tangible/furniture/cheap/shared_chair_s01.iff
                    //Item CRC: 390371593
                    //Item Template ID: 9702
                    switch(commandID)
                    {
                        case 23: //sit
                        {
                            //client.insertPacket(PacketFactory.buildUpdateContainmentMessage(client.getPlayer(), this, true));                                    
                            
                            //client.insertPacket(PacketFactory.buildObjectControllerMessage_UpdatePosture(client.getPlayer()));
                            System.out.println("Code for sitting on a chair has to be added");
                        }
                    }                            
                    break;
                }
                case 8220://camp lawn chairs
                case 8221:
                case 8222:
                {
                    switch(commandID)
                    {
                        case 23://sit on chair
                        {
                            //sit on chair packet goes here
                            break;
                        }
                    }
                    break;
                }
                //Edible Items
                //Foraged Fruits.
                case 9542:
                case 9543:
                case 9544:
                case 9545:
                case 9546:
                case 9547:
                case 9548:
                case 9549:
                case 9550:
                case 9551:
                case 9552:
                case 9553:
                case 9554:
                case 9555:
                {
                    switch(commandID)
                    {
                        case 20: //eat
                        {
                            //this.eat(client);
                        	DataLog.logEntry("Error -- attempting to eat something that is NOT food.  " + getIFFFileName() + ".", "TangibleItem.useItemByCommandID", Constants.LOG_SEVERITY_INFO, true, true);
                            break;
                        }
                    }
                }
                default:
                {
                    switch(commandID)
                    {
                        case 50: //rename
                        {
                            SUIWindow W = new SUIWindow(player);
                            W.setWindowType(Constants.SUI_RENAME_ITEM);
                            W.setOriginatingObject(this);                                    
                            String sCurrentName = "";
                            if(this.getName()!= null && !this.getName().isEmpty())
                            {                                        
                                sCurrentName = this.getName();
                            }
                            else
                            {
                                sCurrentName = "@" + this.getSTFFileName() + ":" + this.getSTFFileIdentifier();
                            }
                            client.insertPacket(W.SUIScriptTextInputBox(client, "handleFilterInput",sCurrentName, "Rename Item", true, "Enabled", "Visible", 127, sCurrentName));
                            break;
                        }
                        default:
                        {
                            //client.insertPacket(PacketFactory.buildChatSystemMessage("This TangibleItem is not handled yet."));
                            //client.insertPacket(PacketFactory.buildChatSystemMessage("Item Name: " + getName()));
                            //client.insertPacket(PacketFactory.buildChatSystemMessage("Item IFF Name: " + getIFFFileName()));
                            //client.insertPacket(PacketFactory.buildChatSystemMessage("Item CRC: " + getCRC()));
                            //client.insertPacket(PacketFactory.buildChatSystemMessage("Item Template ID: " + iTemplateID));
                            //client.insertPacket(PacketFactory.buildChatSystemMessage("Command ID: " + commandID));
                            System.out.println("This TangibleItem is not handled yet.");
                            System.out.println("Item Name: " + getName());
                            System.out.println("Item IFF Name: " + getIFFFileName());
                            System.out.println("Item CRC: " + getCRC());
                            System.out.println("Item Template ID: " + iTemplateID);
                            System.out.println("Command ID: " + commandID);
                        }
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Exception caught in useItemByCommandID " + e);
            e.printStackTrace();
        }
    }
	protected void useItem(ZoneClient client) {
		try {
                    int iTemplateID = getTemplateID();
                    switch(iTemplateID)
                    {
                        case 14037 - 14045:
                        {
                            
                            client.getServer().getResourceManager().generateResourceListForSurveyMessage(client, this);
                            break;
                        }
                        
                        default:
                        {
                            client.insertPacket(PacketFactory.buildChatSystemMessage("This TangibleItem is not handled yet."));
                            client.insertPacket(PacketFactory.buildChatSystemMessage("Item Name: " + getName()));
                            client.insertPacket(PacketFactory.buildChatSystemMessage("Item IFF Name: " + getIFFFileName()));
                            client.insertPacket(PacketFactory.buildChatSystemMessage("Item CRC: " + getCRC()));
                            client.insertPacket(PacketFactory.buildChatSystemMessage("Item Template ID: " + iTemplateID));
                            System.out.println("This TangibleItem is not handled yet.");
                            System.out.println("Item Name: " + getName());
                            System.out.println("Item IFF Name: " + getIFFFileName());
                            System.out.println("Item CRC: " + getCRC());
                            System.out.println("Item Template ID: " + iTemplateID);
                            
                        }
                    }
			/*
			if (iTemplateID >= 14037 && iTemplateID <= 14045) {
				client.insertPacket(PacketFactory.buildChatSystemMessage("Trying to use a resource tool?  This is WIP!"));
				System.out.println("Using Resource tool template ID " + iTemplateID);
				client.getServer().getResourceManager().generateResourceListForSurveyMessage(client, this);
			} else {
				client.insertPacket(PacketFactory.buildChatSystemMessage("Attempting to use " + getName() + " ."));
				if(isEquipped()) {
					client.insertPacket(PacketFactory.buildChatSystemMessage(getName() + " is already equipped, attempting to unequip item."));
					setEquipped(getContainer(),false);
					if(!isEquipped()) {
						client.insertPacket(PacketFactory.buildChatSystemMessage(getName() + " successfully unequipped."));
					} else {
						client.insertPacket(PacketFactory.buildChatSystemMessage(getName() + " unsuccessfully unequipped."));
					}
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage(getName() + " is unequipped, attempting to equip item."));
					setEquipped(getContainer(),true);
					if(isEquipped()) {
						client.insertPacket(PacketFactory.buildChatSystemMessage(getName() + " successfully equipped."));
					} else {
						client.insertPacket(PacketFactory.buildChatSystemMessage(getName() + " unsuccessfully equipped."));
					}
				}
				
			}*/
		} catch(Exception e) {
			System.out.println("Unable to send system message from TangibleItem::useItem()." + e);
                        e.printStackTrace();
		}
	}
	
	protected byte[] setConditionDamage(int iCondition, boolean bUpdate) throws IOException {
		iCurrentCondition = iCondition;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)8, this, iCurrentCondition);
		}
		return null;
	}
	protected int getConditionDamage() {
		return iCurrentCondition;
	}
	protected byte[] setMaxCondition(int iCondition, boolean bUpdate) throws IOException {
		iMaxCondition = iCondition;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)9, this, iMaxCondition);
		}
		return null;
	}
	protected int getMaxCondition() {
		return iMaxCondition;
	}
        
 
        public long getCrafterID() {
            return lCrafterID;
        }

        public void setCrafterID(long lCrafterID) {
            this.lCrafterID = lCrafterID;
        }        

        protected void setSurveyToolRange(int newRange){
            iToolSurveyRange = newRange;
        }
        
        protected int getSurveyToolRange(){
            return iToolSurveyRange;
        }
        
        protected void surveyToolOpenSurveyWindow(ZoneClient client){
            //System.out.println("surveyToolOpenSurveyWindow()");
            try{
                client.getServer().getResourceManager().generateResourceListForSurveyMessage(client, this);            
            }catch(Exception e){
                
            }
        }
        
        protected void setIsSurveying(boolean b, ZoneClient client, Player player) throws IOException{
        	if (!bIsSurveying) {
        		if (b == true) {
        			if (iSurveyToolType == -1) {
        				setSurveyToolType();
        			}
        			client.insertPacket(PacketFactory.buildClientEffectAtLocation(SpawnedResourceData.SURVEY_TOOL_CLIENT_EFFECTS[iSurveyToolType], player), Constants.PACKET_RANGE_CHAT_RANGE);
        		}
        	}
        	bIsSurveying = b;
        }
        
        protected boolean getIsSurveying() {
        	return bIsSurveying;
        }
        protected void setSurveyToolTimeMS(long lTimeMS) {
        	lSurveyTimeMS = lTimeMS;
        }
        
        protected long getSurveyToolTimeMS() {
        	return lSurveyTimeMS;
        }
        
        protected void setResourceToSurvey(SpawnedResourceData theResource) {
        	this.resourceToSurvey = theResource;
        }
        
        protected SpawnedResourceData getResourceToSurvey() {
        	return resourceToSurvey;
        }
        
        protected void setIsSampling(boolean state, Player player) {
        	if (bIsSampling && (state == false)) {
        		lSampleTimeMS = 20000;
            	try {
            		player.getClient().insertPacket(PacketFactory.buildClientEffectAtLocation(SpawnedResourceData.SURVEY_SAMPLE_CLIENT_EFFECTS[iSurveyToolType], player), Constants.PACKET_RANGE_CHAT_RANGE);
            	} catch (Exception e) {
            		// Oh well.
            	}
        	}
        	bIsSampling = state;
        }
        
        protected boolean getIsSampling() {
        	return bIsSampling;
        }
        
        protected void setSampleToolTimeMS(long timeoutMS) {
        	lSampleTimeMS = timeoutMS;
        }
        
        protected long getSampleToolTimeMS() {
        	return lSampleTimeMS;
        }
        
        protected boolean getIsCoolingDown() {
        	return bSamplingCoolDown;
        }
        protected void setIsCoolingDown(boolean state) {
        	bSamplingCoolDown = false;
        }
 
        protected void stopSurveying() {
    		setIsSampling(false, null);
    		try {
    			setIsSurveying(false, null, null);
    		} catch (IOException e) {
    			// Can't happen here.
    		}
    		setSurveyToolTimeMS(0);
    		setSampleToolTimeMS(0);
    		setIsCoolingDown(false);
        }

        public Vector<MissionObject> getVMissionList() {
            return vMissionList;
        }

        public void setVMissionList(Vector<MissionObject> vMissionList) {
            this.vMissionList = vMissionList;
        }
        
        public void addMissionObjectToRefreshList(MissionObject m){
            if(vRefreshedMissionList == null)
            {
                vRefreshedMissionList = new Vector<MissionObject>();
            }
            vRefreshedMissionList.add(m);
        }
        public void clearMissionObjectRefreshList(){
            if(vRefreshedMissionList == null)
            {
                vRefreshedMissionList = new Vector<MissionObject>();
            }
            vRefreshedMissionList.clear();
        }
        
        public Vector<MissionObject> getMissionObjectRefreshList(){
            return vRefreshedMissionList;
        }
        
        public void addMissionObjectToAcceptedList(MissionObject m){
            if(vAcceptedMissionList == null)
            {
                vAcceptedMissionList = new Vector<MissionObject>();
            }
            vAcceptedMissionList.add(m);
            for(int i = 0; i < vMissionList.size(); i++)
            {
                MissionObject tm = vMissionList.get(i);
                if(tm.getID() == m.getID())
                {
                    vMissionList.remove(i);
                    break;
                }
            }                
            
        }       
        
        public void removeAcceptedMission(MissionObject m){
            for(int i = 0; i < vAcceptedMissionList.size();i++)
            {
                MissionObject tm = vAcceptedMissionList.get(i);
                if(tm.getID() == m.getID())
                {
                    vAcceptedMissionList.remove(i);
                    break;
                }
            }
            for(int i = 0; i < vEmptyMissionList.size(); i++)
            {
                MissionObject tm = vEmptyMissionList.get(i);
                if(tm.getID() == m.getID())
                {
                    vMissionList.add(tm);
                    break;
                }
            }                
        }
        
        public Vector<MissionObject> getMissionObjectAcceptedList(){
            return vAcceptedMissionList;
        }

        public boolean bSendsEquipedState() {
            return bSendsEquipedState;
        }

        public void setBSendsEquipedState(boolean bSendsEquipedState) {
            this.bSendsEquipedState = bSendsEquipedState;
        }

        public void setVEmptyMissionList(Vector<MissionObject> vEmptyMissionList) {
            this.vEmptyMissionList = vEmptyMissionList;
        }
        
        public boolean isContainer(){
            ItemTemplate t = DatabaseInterface.getTemplateDataByCRC(this.getCRC());
            return t.isContainer();
        }
        
        public boolean isFemaleItem(){
            ItemTemplate t = DatabaseInterface.getTemplateDataByCRC(this.getCRC());
            return t.isFemaleItem();
        }
        
        public int [] getRaceRestrictions(){
            ItemTemplate t = DatabaseInterface.getTemplateDataByCRC(this.getCRC());
            return t.getRaceRestrictions();
        }
        
        protected void renameItem(String newName, ZoneClient client){
              try{
                    //System.out.println("Terminal Rename requested To: " + newName);
                    if(newName.contains("\\#"))
                    {
                        //\#FF0000Shards of the Force\#ffffff\#FF8C00 SWG Dev House\#FFFFFF
                        //System.out.println("Name Had Color Codes in it.");
                        int stlen = newName.length();

                        String sOriginalName = newName;

                        for(int i = 0; i < stlen; i++)
                        {
                            int pos = newName.indexOf("\\#");
                            if(pos != -1)
                            {
                                //System.out.println("Pos" + pos);
                                if(pos == 0)
                                {
                                    newName = newName.substring(pos+8);
                                }
                                else
                                {
                                    String t = newName.substring(0,pos);
                                    String r = t + newName.substring(pos+8);
                                    newName = r;                        
                                }                    
                                //System.out.println("newName:" + newName);
                            }                
                        }
                        if(client.getServer().profanityCheck(newName))
                        {
                            client.insertPacket(this.setName(sOriginalName, true));
                            //client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)2,this, sOriginalName,true));                                    
                            client.insertPacket(PacketFactory.buildChatSystemMessage("Item renamed."));
                            client.getServer().getGUI().getDB().updatePlayer(client.getPlayer(), false,false);
                        }
                        else
                        {
                            //System.out.println("NameDeclined");
                            client.insertPacket(PacketFactory.buildChatSystemMessage(
                            		"player_structure",
                            		"obscene",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0l,
                            		"",
                            		"",
                            		"",
                            		0,
                            		0f, false
                            ));
                        }
                    }
                    else if(client.getServer().profanityCheck(newName))
                    {
                        //System.out.println("NameAccepted");            
                        client.insertPacket(this.setName(newName, true));
                        //client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)2,this, newName, true));
                        client.insertPacket(PacketFactory.buildChatSystemMessage("Item renamed."));
                        client.getServer().getGUI().getDB().updatePlayer(client.getPlayer(), false,false);
                    }   
                    else
                    {
                        //System.out.println("NameDeclined");
                        client.insertPacket(PacketFactory.buildChatSystemMessage(
                        		"player_structure",
                        		"obscene",
                        		0l,
                        		"",
                        		"",
                        		"",
                        		0l,
                        		"",
                        		"",
                        		"",
                        		0l,
                        		"",
                        		"",
                        		"",
                        		0,
                        		0f, false
                        ));
                    }
             }catch(Exception e){
                 System.out.println("Exception Caught in Item.renameItem() " + e);
                 e.printStackTrace();
             }
        }

    public boolean isBIsBioLinkItem() {
        return bIsBioLinkItem;
    }

    public void setBIsBioLinkItem(boolean bIsBioLinkItem) {
        this.bIsBioLinkItem = bIsBioLinkItem;
    }

    public boolean isBIsNoTradeItem() {
        return bIsNoTradeItem;
    }

    public void setBIsNoTradeItem(boolean bIsNoTradeItem) {
        this.bIsNoTradeItem = bIsNoTradeItem;
    }

    public long getLBiolinkID() {
        return lBiolinkID;
    }

    public void setLBiolinkID(long lBiolinkID) {
        this.lBiolinkID = lBiolinkID;
    }

    protected void addSkillModifier(SkillModifier mod){
        if(vSkillModifiers == null)
        {
            vSkillModifiers = new Vector<SkillModifier>();
        }
        if(!vSkillModifiers.contains(mod))
        {
            vSkillModifiers.add(mod);
        }
    }

    protected void removeSkillModifier(SkillModifier mod){
        if(vSkillModifiers == null)
        {
            vSkillModifiers = new Vector<SkillModifier>();
        }
        if(vSkillModifiers.contains(mod))
        {
            vSkillModifiers.remove(mod);
        }
    }

    protected Vector<SkillModifier> getSkillModifiers(){
        if(vSkillModifiers==null)
        {
            vSkillModifiers = new Vector<SkillModifier>();
        }
        return vSkillModifiers;
    }

    protected byte[] setComplexity(float complexity, boolean bUpdate) throws IOException {
    	// TODO: Actually keep track of crafted complexity
    	fComplexity = complexity;
    	if (bUpdate) {
    		return PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)0, this, fComplexity);
    	}
    	return null;
    }
    protected float getComplexity(){ 
    	return fComplexity;
    }

	public void setLSaleTimeMS(long lSaleTimeMS) {
		this.lSaleTimeMS = lSaleTimeMS;
	}

	public long getLSaleTimeMS() {
		return lSaleTimeMS;
	}

	public void setSaleTerminal(BazaarTerminal saleTerminal) {
		this.saleTerminal = saleTerminal;
	}

	public BazaarTerminal getSaleTerminal() {
		return saleTerminal;
	}

	public void setIsInstantSale(boolean bInstantSale) {
		this.bInstantSale = bInstantSale;
	}

	public boolean getIsInstantSale() {
		return bInstantSale;
	}

	public void setCurrentBidPrice(int iCurrentBidPrice) {
		this.iCurrentBidPrice = iCurrentBidPrice;
	}

	public int getCurrentBidPrice() {
		return iCurrentBidPrice;
	}

	public void setMaxBidPrice(int iMaxBidPrice) {
		this.iMaxBidPrice = iMaxBidPrice;
	}

	public int getMaxBidPrice() {
		return iMaxBidPrice;
	}

	/**
	 * Sets the size of this resource.
	 * @param iNewQuantity
	 */
	public byte[] setStackQuantity(int iNewQuantity, boolean bUpdate) throws IOException {
		//stackSizeAttribute = null;
		iStackQuantity = iNewQuantity;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)0, (short)7, this, iStackQuantity);
		}
		return null;
	}

	public int getStackQuantity() {
		return iStackQuantity;
	}
	
	public int experiment(int[] iExperimentalIndex, int[] numExperimentationPointsUsed, Player thePlayer) throws IOException {
		// Depends on what it is.
		System.out.println("Tangible Item experimenting.");
		return 0;
	}
	
}

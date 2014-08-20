import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



/**
 *
 * @author Tomas Cruz
 */
public final class CreatureAnimal extends NPC {
	public final static long serialVersionUID = 1l;
    private long lNextMoveTime = 0;
    private long lNextCuriosityTime = 0;
    private SpawnedResourceData boneResource;
    private SpawnedResourceData meatResource;
    private SpawnedResourceData hideResource;
    private SpawnedResourceData milkResource;
    private byte iResourceHealthRating;
    private long[] lPlayersHarvested;
    private int iHarvestCount;
    
    public CreatureAnimal(){
    	super();
        setArtificialIntelligenceType(Constants.INTELLIGENCE_TYPE_CREATURE_PASSIVE);
        lPlayersHarvested = new long[3];
    }
    
    @Override
    public void update(long lElapsedTime){
    	//theAI.update(lElapsedTime);
    	
        try{
        	super.update(lElapsedTime);
            if (!(hasState(Constants.STATE_COMBAT))) {
	        	switch(getIRoamType())
	            {
	                case 1: //Free roam no restriction but around a central point.
	                {
	                    lNextMoveTime -= lElapsedTime;
	                    if (lNextMoveTime <= 0) {
		                    if(!hasState(Constants.STATE_COMBAT))
		                    {                    
		                        int desire = SWGGui.getRandomInt(1,1000);
		                        if(desire >= 975)
		                        {                       
		                            randomMove();
		                        }                    
		                    }
	                        lNextMoveTime = SWGGui.getRandomLong((1000*60), (1000*60*5)); // 1 to 5 minutes?
	                    }
	                }
	            }
	            int curiosity = SWGGui.getRandomInt(1,1000);
	            switch(curiosity)
	            {
	                case 100:
	                case 200:
	                {
	                    lNextCuriosityTime -= lElapsedTime;
	                    if(lNextCuriosityTime <= 0)
	                    {    
	                        //System.out.println("Creature is curious : " + getFullName());
	                        lNextCuriosityTime = SWGGui.getRandomLong((1000*60), (1000*60*5));
	                        if(SWGGui.getRandomInt(1) == 0)
	                        {
	                            vocalize();
	                        }
	                    }
	                    break;
	                }
	                case 455:
	                case 322:
	                case 98:
	                case 998:
	                {
	                    lNextCuriosityTime -= lElapsedTime;
	                    if(lNextCuriosityTime <= 0)
	                    {
	                        lNextCuriosityTime = SWGGui.getRandomLong((1000*60), (1000*60*5));
	                        if(SWGGui.getRandomInt(1) == 0)
	                        {
	                            animate();
	                        }
	                    }
	                    break;
	                }
	            }
            }            
        }catch(Exception e){
            System.out.println("Exception caught in CreatureAnimal.update " + e);
            e.printStackTrace();
        }
    }
    
    // TODO:  Replace by the CreatureIntelligence stuff.
    private void randomMove(){
        try{
            //System.out.println("CreatureAnimal.Move");
            SOEObject Temp = new TangibleItem();
            int newX = SWGGui.getRandomInt(SWGGui.getRandomInt(-32,-1),SWGGui.getRandomInt(0,32));
            int newY = SWGGui.getRandomInt(SWGGui.getRandomInt(-32,-1),SWGGui.getRandomInt(0,32));
            Temp.setX(getRoamCenterX() + newX);
            Temp.setY(getRoamCenterY() + newY);
            Temp.setZ(0);
            float newAngle = absoluteBearingRadians(Temp,this);
            setOrientationW(degreesToRadians(SWGGui.getRandomInt(0,359)));
            setMovementAngle(newAngle);
            setX(Temp.getX());
            setY(Temp.getY());
            sendPositionUpdate();
            
        }catch(Exception e){
            System.out.println("Exception caught in CreatureAnimal.randomMove " + e);
            e.printStackTrace();
        }
    }
    
    private void sendPositionUpdate(){
        try{
	        if(getCellID() == 0) {
	            getServer().sendToRange(PacketFactory.buildNPCUpdateTransformMessage(this), Constants.PACKET_RANGE_CHAT_RANGE, this);
	        } else {
	            Cell cell = (Cell)getServer().getObjectFromAllObjects(getCellID());
	            getServer().sendToRange(PacketFactory.buildNPCUpdateCellTransformMessage(this, cell), Constants.PACKET_RANGE_CHAT_RANGE, this);
	        }
        }catch(Exception e){
                System.out.println("Exception caught in CreatureAnimal.sendPositionUpdate " + e);
                e.printStackTrace();
        }
    }

    // TODO:  Replace by the CreatureIntelligence stuff.
    /**
     * makes the creature vocalize.
     */
    private void vocalize(){
        try{
            
            String sVocalization = null;

            switch(getTemplateID())
            {
                case 5585: //kadu cr_kaadu_emote_vocalize_1.snd
                {
                    sVocalization = "sound/cr_kaadu_emote_vocalize_1.snd";
                }
            }
            
            if (sVocalization != null) {
                getServer().sendToRange(PacketFactory.buildPlaySoundFileMessage(0,sVocalization,0,(byte)0), Constants.PACKET_RANGE_CHAT_RANGE, this);
            }
        }catch(Exception e){
            System.out.println("Exception Caught while CreatureAnimal.vocalize " + e);
            e.printStackTrace();
        }
    }
    
    // TODO:  Replace by the CreatureIntelligence stuff.
    protected void animate(){
        try{
            
            String sAnimation = null;
            int iRandom = SWGGui.getRandomInt(0,32);
            switch(iRandom)
            {
                case 0:
                {
                    sAnimation = "eat";
                }
                default:
                {
                    sAnimation = "eat";
                }
            }
            
            if (sAnimation != null) {
            	getServer().sendToRange(PacketFactory.buildNPCAnimation(this,sAnimation), Constants.PACKET_RANGE_CHAT_RANGE, this);
            }
        }catch(Exception e){
            System.out.println("Exception Caught while CreatureAnimal.animate " + e);
            e.printStackTrace();
        }
    }

	public void setBoneResource(SpawnedResourceData boneResource) {
		this.boneResource = boneResource;
	}

	public SpawnedResourceData getBoneResource() {
		return boneResource;
	}

	public void setMeatResource(SpawnedResourceData meatResource) {
		this.meatResource = meatResource;
	}

	public SpawnedResourceData getMeatResource() {
		return meatResource;
	}

	public void setHideResource(SpawnedResourceData hideResource) {
		this.hideResource = hideResource;
	}

	public SpawnedResourceData getHideResource() {
		return hideResource;
	}

	public void setResourceHealthRating(byte iResourceHealthRating) {
		this.iResourceHealthRating = iResourceHealthRating;
	}

	public byte getResourceHealthRating() {
		return iResourceHealthRating;
	}
	
	// Do NOT call this function for the milking.  That will require a separate function.
	protected int getNumResourcesHarvested(Player player) {
		// Challenge rating times (1.0 / (resourceHealthRating+1));
		int baseResourceQuantityToReturn = getConLevel();
		float healthModQuantityToReturn = baseResourceQuantityToReturn * (1.0f / (iResourceHealthRating + 1.0f));
		SkillMods harvestingMod = player.getSkillMod("creature_harvesting");
		float skillModQuantityToReturn = healthModQuantityToReturn;
		if (harvestingMod != null) {
			skillModQuantityToReturn = skillModQuantityToReturn * (harvestingMod.getSkillModModdedValue() / 100.0f);
			return Math.max(1, (int)skillModQuantityToReturn);
		} else {
			// Can't harvest without skill mod.
			System.out.println("No creature harvesting skill mod exists for player " + player.getFirstName());
			return 0;
		}
	}

	public void setMilkResource(SpawnedResourceData milkResource) {
		this.milkResource = milkResource;
	}

	public SpawnedResourceData getMilkResource() {
		return milkResource;
	}
	
	public Hashtable<Character, RadialMenuItem> getRadialMenus(ZoneClient c) {
		Player player = c.getPlayer();
		Hashtable<Character, RadialMenuItem> menus = super.getRadialMenus(c);
		// Make sure that our radial menu ID is 1 + the last one in the list.
		int lowestRadialMenuID = 0;
		Enumeration<RadialMenuItem> radialMenuEnum = menus.elements();
		while (radialMenuEnum.hasMoreElements()) {
			RadialMenuItem radial = radialMenuEnum.nextElement();
			lowestRadialMenuID = Math.max(lowestRadialMenuID, radial.getButtonNumber());
		}
		lowestRadialMenuID++;
		if (getStance() == Constants.STANCE_INCAPACITATED || getStance() == Constants.STANCE_DEAD) {
			if (player.hasSkill(31)) {
				if (!hasHarvested(player)) {
					// Add harvesting radials based on our current stuff.
					int parentID = lowestRadialMenuID;
					RadialMenuItem harvestRoot = new RadialMenuItem((byte)lowestRadialMenuID, (byte)0, Constants.RADIAL_MENU_HARVEST_CORPSE, (byte)3, "@sui:harvest_corpse");
					lowestRadialMenuID++;
					menus.put(Constants.RADIAL_MENU_HARVEST_CORPSE, harvestRoot);
					// Can have up to 3 resources.  Need to assign which one to which command ID.
					// Bone will always be SERVER_MENU_1, Hide SERVER_MENU_2, Meat SERVER_MENU_3.
					// For milkable creatures, milk will be SERVER_MENU_4
					if (boneResource != null) {
						menus.put(Constants.RADIAL_MENU_ADMIN_SERVER_MENU1, new RadialMenuItem((byte)lowestRadialMenuID, (byte)parentID, Constants.RADIAL_MENU_ADMIN_SERVER_MENU1, (byte)3, "@sui:harvest_bone"));
						lowestRadialMenuID++;
					}
					if (hideResource != null) {
						menus.put(Constants.RADIAL_MENU_ADMIN_SERVER_MENU2, new RadialMenuItem((byte)lowestRadialMenuID, (byte)parentID, Constants.RADIAL_MENU_ADMIN_SERVER_MENU2, (byte)3, "@sui:harvest_hide"));
						lowestRadialMenuID++;
					}
					if (meatResource != null) {
						menus.put(Constants.RADIAL_MENU_ADMIN_SERVER_MENU3, new RadialMenuItem((byte)lowestRadialMenuID, (byte)parentID, Constants.RADIAL_MENU_ADMIN_SERVER_MENU3, (byte)3, "@sui:harvest_meat"));
						lowestRadialMenuID++;
					}
				}
			}
		} else {
			if (milkResource != null) {
				menus.put(Constants.RADIAL_MENU_ADMIN_SERVER_MENU4, new RadialMenuItem((byte)lowestRadialMenuID, (byte)0, Constants.RADIAL_MENU_ADMIN_SERVER_MENU4, (byte)3, "@pet_menu:milk_me"));
				
			}
			if (isBIsTameable()) {
				menus.put(Constants.RADIAL_MENU_TAME_CREATURE, new RadialMenuItem((byte)lowestRadialMenuID, (byte)0, Constants.RADIAL_MENU_TAME_CREATURE, (byte)3, null));
			}
		}
		return menus;
	}
	
	protected void useItemByCommandID(ZoneClient client, byte commandID) {
		switch (commandID) {
			case Constants.RADIAL_MENU_ADMIN_SERVER_MENU1: {
				// Harvest bone.
				handleHarvest(client.getPlayer(), boneResource);
				boneResource = null; // Prevent these from being harvested again by ANYONE.
				break;
			}
			case Constants.RADIAL_MENU_ADMIN_SERVER_MENU2: {
				// Harvest hide.
				handleHarvest(client.getPlayer(), hideResource);
				hideResource =null;// Prevent these from being harvested again by ANYONE.
				break;
			}
			case Constants.RADIAL_MENU_ADMIN_SERVER_MENU3: {
				// Harvest meat.
				handleHarvest(client.getPlayer(), meatResource);
				meatResource = null;// Prevent these from being harvested again by ANYONE.
				break;
			}
			case Constants.RADIAL_MENU_ADMIN_SERVER_MENU4: {
				// Milk me!
				handleHarvestMilk(client.getPlayer());
				break;
			}
			case (byte)Constants.RADIAL_MENU_TAME_CREATURE: {
				// Try to tame me.
				handleTameRequest(client.getPlayer());
				break;
			} 
			default: {
				DataLog.logEntry("Unhandled ID " + commandID, "CreatureAnimal use item by command ID", Constants.LOG_SEVERITY_INFO, true, true);
			}
		}
	}
	
	private void handleHarvest(Player player, SpawnedResourceData resource) {
		try {
			ZoneClient client = player.getClient();
			if (!hasHarvested(player)) {
				int stackSize = getNumResourcesHarvested(player);
				long resourceTypeID = resource.getID();
				ResourceContainer container = player.getLastUpdatedResourceContainer();
				boolean bCouldUpdate = false;
				if (stackSize > 0) {
					if (container != null) {
						if (container.getResourceSpawnID() == resourceTypeID) {
							bCouldUpdate = true;
							int quantity = container.getStackQuantity();
							if (quantity < (Constants.MAX_STACK_SIZE - stackSize)) {
								client.insertPacket(container.setStackQuantity(quantity + stackSize, true));
								client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), container));
							} else {
								ResourceContainer newResourceContainer = new ResourceContainer(container, quantity);
								player.setLastUpdatedResourceContainer(newResourceContainer);
								client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), newResourceContainer));
							}
						} 
					} 
					if (!bCouldUpdate){
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
							container = new ResourceContainer();
							player.setLastUpdatedResourceContainer(container);
							//container.setID(client.getServer().getNextObjectID());
							int containerTemplateID = resource.getResourceContainerTemplateID();
							container.setTemplateID(containerTemplateID);
							container.setName(resource.getResourceType(), false);
							container.setCustomizationData(null);
							container.setOwner(player);
							container.setEquipped(player.getInventory(), -1);
							player.addItemToInventory(container);
							container.setID(getServer().getNextObjectID());
							
							container.setConditionDamage(0, false);
							container.setMaxCondition(100, false);
							container.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
							container.setResourceSpawnID(resource.getID());
							container.setResourceType(resource.getIffFileName());
							
							container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_NAME, resource.getName()));
							container.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_CLASS, resource.getResourceClass() + " " + resource.getResourceType()));
					
							getServer().addObjectToAllObjects(container, false, false);
							short coldResist = resource.getColdResistance();
							short conductivity = resource.getConductivity();
							short decayResist = resource.getDecayResistance();
							short entangleResist = resource.getEntangleResistance();
							short flavor = resource.getFlavor();
							short heatResist = resource.getHeatResistance();
							short malleability = resource.getMalleability();
							short overallQuality  = resource.getOverallQuality();
							short potentialEnergy = resource.getPotentialEnergy();
							short shockResist = resource.getShockResistance();
							short unitToughness = resource.getUnitToughness();
							
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
						int previousQuantity = container.getStackQuantity();
						client.insertPacket(container.setStackQuantity(previousQuantity + stackSize, true));
						client.insertPacket(PacketFactory.buildAttributeListMessage(player.getClient(), container));
					}
					player.setLastUpdatedResourceContainer(container);
					client.insertPacket(PacketFactory.buildChatSystemMessage("skl_use", Constants.CREATURE_HEALTH_TYPE_STF[getResourceHealthRating()], resource.getID(), null, null, resource.getResourceClass() + " " + resource.getResourceType(), 0, null, null, null, 0, null, null, null, stackSize, 0, false));
					player.updateExperience(null, 36, (stackSize / 2) + (stackSize % 2));
					addHasHarvested(player);
					// Do we have to forcibly update the radial menus?
				}
			} else {
				client.insertPacket(PacketFactory.buildChatSystemMessage("You were unable to extract resources from the corpse."));
			}
		} catch (Exception e) {
			System.out.println("Error spawning new resource container: " + e.toString());
			e.printStackTrace();
		}
	}
	
	private void handleHarvestMilk(Player player) {
		
	}
	private void handleTameRequest(Player player) {
		
	}

	private boolean hasHarvested(Player player) {
		for (int i = 0; i < lPlayersHarvested.length; i++) {
			if (lPlayersHarvested[i] == player.getID()) {
				return true;
			}
		}
		return false;
	}
	private void addHasHarvested(Player player) {
		lPlayersHarvested[iHarvestCount] = player.getID();
		iHarvestCount++;
	}
}

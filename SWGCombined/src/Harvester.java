import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class Harvester extends Structure {
	public final static long serialVersionUID = 1l;
	
	
    private SpawnedResourceData currentHarvestResource;
    private float currentResourceConcentration;
    private transient Vector<ZoneClient> vSyncronizedListeners;

    private byte harvesterUpdateCounter;
    private int iHarvesterResourceUpdateCounter;
    private String sClientEffect;
    private int iBaseExtractionRateMins;
    private long lBaseExtractionRateMSKK;
    private long lCurrentHarvestedResourceAmountKK;
    private int iCurrentHarvestedResourceAmount;
    private int iLastHarvestedResourceAmount;
    private long lCurrentExtractionRateMSKK;
    private int iTotalResourcesInHopper;
    private ResourceContainer theResource;
    private transient int iTicks = 0;
    private Hashtable<Long, SOEObject> vOutputHopper;
    private Vector<SpawnedResourceData> vResourcesAvailable;
    private byte iHarvesterType;

	/**
	 * Construct a new Structure.
	 */
	public Harvester() {
		super();
		vOutputHopper = new Hashtable<Long, SOEObject>();
	}

	/**
	 * Construct a new Structure, with the given IFF file name.
	 * @param sName -- The iff filename of the structure.
	 */
	public Harvester(String sName, long deedID) {
		super(sName, deedID);		
		vOutputHopper = new Hashtable<Long, SOEObject>();
	}

    public Harvester(int iDeedTemplateID, long deedID,Deed d, float x, float y, float z, int planetid, float oW, float oS, int iFacingDirection, String sOwnerName,long lStructureOwnerID, ZoneServer server) throws IOException {
        super(iDeedTemplateID, deedID, d, x, y, z, planetid, oW, oS, iFacingDirection, sOwnerName, lStructureOwnerID, server);
        DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(d.getDeedTemplateID());
        
        if(dT!=null)
        {
        	int itemTemplateID = dT.getObject_iff_template_id();
            ItemTemplate iT = d.getServer().getTemplateData(dT.getObject_iff_template_id());
            switch (itemTemplateID) {
	            case 15436: { // 'object/installation/generators/shared_power_generator_fusion_style_1.iff'
	            	setHarvesterType(Constants.HARVESTER_TYPE_FUSION);
	            	break;
	            }
	            case 15437: {// 'object/installation/generators/shared_power_generator_photo_bio_style_1.iff'
	            	// Not supported.  No resources to harvest;
	            	setHarvesterType((byte)-1);
	            	break;
	            }
	            case 15438: { // 'object/installation/generators/shared_power_generator_solar_style_1.iff'
	            	setHarvesterType(Constants.HARVESTER_TYPE_SOLAR);
	            	break;
	            }
	            case 15439: { // 'object/installation/generators/shared_power_generator_wind_style_1.iff'
	            	setHarvesterType(Constants.HARVESTER_TYPE_WIND);
	            	break;
	            }
	            case 15442:// 'object/installation/mining_organic/shared_mining_organic_flora_farm_heavy.iff'
	            case 15443:// 'object/installation/mining_organic/shared_mining_organic_flora_farm_medium.iff'
	            case 15441: { // 'object/installation/mining_organic/shared_mining_organic_flora_farm.iff'
	            	setHarvesterType(Constants.HARVESTER_TYPE_FLORA);
	            	break;
	            }
	            case 15444:// 'object/installation/mining_gas/shared_mining_gas_harvester_style_1.iff'                           
	            case 15445:// 'object/installation/mining_gas/shared_mining_gas_harvester_style_2.iff'                            
	            case 15446: { // 'object/installation/mining_gas/shared_mining_gas_harvester_style_3.iff'
	            	setHarvesterType(Constants.HARVESTER_TYPE_GAS);
	            	break;
	            }
	            case 15447:// 'object/installation/mining_liquid/shared_mining_liquid_harvester_style_1.iff'                       
	            case 15448:// 'object/installation/mining_liquid/shared_mining_liquid_harvester_style_2.iff'                       
	            case 15449: { // 'object/installation/mining_liquid/shared_mining_liquid_harvester_style_3.iff'
	            	setHarvesterType(Constants.HARVESTER_TYPE_CHEMICAL);
	            	break;
	            }
	            case 15450:// 'object/installation/mining_liquid/shared_mining_liquid_moisture_harvester.iff'                          
	            case 15452:// 'object/installation/mining_liquid/shared_mining_liquid_moisture_harvester_medium.iff'                          
	            case 15451: { // 'object/installation/mining_liquid/shared_mining_liquid_moisture_harvester_heavy.iff'
	            	setHarvesterType(Constants.HARVESTER_TYPE_WATER);
	            	break;
	            }
	            case 15428:// 'object/installation/mining_ore/shared_mining_ore_harvester_heavy.iff'                           
	            case 15429:// 'object/installation/mining_ore/shared_mining_ore_harvester_style_1.iff'                           
	            case 15430: { // 'object/installation/mining_ore/shared_mining_ore_harvester_style_2.iff'
	            	setHarvesterType(Constants.HARVESTER_TYPE_MINERAL);
	            	break;
	            }
            }
            if(iT!=null)
            {                       
            	
            	setIFFFileName(iT.getIFFFileName());
                vOutputHopper = new Hashtable<Long, SOEObject>();
                this.setIOutputHopperSize(d.getIOutputHopperSize() * 100);
                this.setBaseExtractionRateMS(d.getIExtractionRate());
                this.setIsBuilding(true);                       
                this.setCellID(0);
                this.setDeedID(deedID);
                this.setID(d.getServer().getNextObjectID());                        
                this.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);                                               
                this.setTemplateID(iT.getTemplateID());
                this.setIAnimationBitmask(256);

                this.setX(x);
                this.setY(y);
                this.setZ(z);
                this.setPlanetID(planetid);  
                this.setOrientationN(0);
                this.setOrientationS(oS);
                this.setOrientationE(0);
                this.setOrientationW(oW);
                this.setMaxCondition(5000, false);                        

            }
            if(dT.isBase_required())
            {
                iT = d.getServer().getTemplateData(dT.getObject_base_template_id());
                TangibleItem structureBase = new TangibleItem();                                               
                structureBase.setCellID(0);                        
                structureBase.setID(d.getServer().getNextObjectID());
                structureBase.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);
                structureBase.setRadialCondition(Constants.RADIAL_CONDITION.NORMAL.ordinal());                     
                structureBase.setTemplateID(iT.getTemplateID());
                structureBase.setX(x);
                structureBase.setY(y);
                structureBase.setZ(z + 1.0f);
                structureBase.setPlanetID(planetid);  
                if(iFacingDirection == 0 || iFacingDirection == 2 )
                {
                    structureBase.setOrientationN(getOrientationN());
                    structureBase.setOrientationS(getOrientationS());
                    structureBase.setOrientationE(getOrientationE());
                    structureBase.setOrientationW(getOrientationW());
                }
                else if(iFacingDirection == 1 )
                {                            
                    structureBase.setOrientationN(getOrientationN());
                    structureBase.setOrientationS(0.7f);
                    structureBase.setOrientationE(getOrientationE());
                    structureBase.setOrientationW(0.7f);
                }
                else if(iFacingDirection == 3)
                {
                    structureBase.setOrientationN(getOrientationN());
                    structureBase.setOrientationS(0.7f);
                    structureBase.setOrientationE(getOrientationE());
                    structureBase.setOrientationW(-0.7f);
                }                        
                structureBase.setParentID(getID());
                setStructureBase(structureBase);
            }
            
            //Construction Marker;
            Structure constructionMarkerObject = null;
            if(dT.getConstruction_marker_template_id()!=0)
            {
                iT = d.getServer().getTemplateData(dT.getConstruction_marker_template_id());
                constructionMarkerObject = new Structure();    
                constructionMarkerObject.setStructureType(Constants.STRUCTURE_TYPE_INSTALLATION);                       
                constructionMarkerObject.setCellID(0);                        
                constructionMarkerObject.setID(d.getServer().getNextObjectID());
                constructionMarkerObject.setPVPStatus(Constants.PVP_STATUS_IS_ITEM);     
                constructionMarkerObject.setTemplateID(iT.getTemplateID());                        
                constructionMarkerObject.setX(getX());
                constructionMarkerObject.setY(getY());
                constructionMarkerObject.setZ(getZ());
                constructionMarkerObject.setPlanetID(planetid);  
                //Bloody Structure Markers GO BACKWARDS!!!!!!
                if(iFacingDirection == 0 || iFacingDirection == 2)
                {
                    constructionMarkerObject.setOrientationN(getOrientationE());
                    constructionMarkerObject.setOrientationS(getOrientationW());
                    constructionMarkerObject.setOrientationE(getOrientationN());
                    constructionMarkerObject.setOrientationW(getOrientationS());
                }
                else if(iFacingDirection == 1 )
                {
                    constructionMarkerObject.setOrientationN(getOrientationN());
                    constructionMarkerObject.setOrientationS(0.7f);
                    constructionMarkerObject.setOrientationE(getOrientationE());
                    constructionMarkerObject.setOrientationW(-0.7f);
                }
                else if(iFacingDirection == 3)
                {
                    constructionMarkerObject.setOrientationN(getOrientationN());
                    constructionMarkerObject.setOrientationS(0.7f);
                    constructionMarkerObject.setOrientationE(getOrientationE());
                    constructionMarkerObject.setOrientationW(0.7f);
                }
                constructionMarkerObject.setParentID(getID());
                setConstructionMarker(constructionMarkerObject);
                //isStructureMarker = true;                       
            }
            //add elevator terminals. We do it separate from the guild because there are structures that may need elevators
            //that are not guild halls and not all guild halls use elevators.
            
        }
    }

    public void deactivateInstallation(){
    	super.deactivateInstallation();
        try {
        	getServer().sendToRange(PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO, (byte)3, (short)1,(short)0x06, this, 256), Constants.PACKET_RANGE_CHAT_RANGE_EXCLUDE_SENDER, this);
        }catch (Exception e) {
        	// D'oh!
        	
        }
    }
    
    public void update(long lElapsedTimeMS, ZoneServer server) {
    	if (vResourcesAvailable == null || vResourcesAvailable.isEmpty()) {
			initializeAvailableResources();
		}
		updateAvailableResources();
		try {
			if(isInstallationActive())
	        {                   
				iTicks++;
				// No more next harvest tick.  Update the current harvested resource amount EVERY FRAME.
				if (lCurrentHarvestedResourceAmountKK == 0) {
					// New resoutheResourcee container.
	                theResource = new ResourceContainer();
	                theResource.setID(server.getNextObjectID());
	                theResource.setResourceSpawnID(this.currentHarvestResource.getID());                            
	                //--------------
	                int containerTemplateID = currentHarvestResource.getResourceContainerTemplateID();
	                theResource.setTemplateID(containerTemplateID);
	                theResource.setName(currentHarvestResource.getResourceType(), false);
	                theResource.setCustomizationData(null);
	                theResource.setEquipped(this, -1);
	                theResource.setConditionDamage(0, false);
	                theResource.setMaxCondition(100, false);
	                theResource.addBitToPVPStatus(Constants.PVP_STATUS_IS_ITEM);
	                theResource.setResourceType(currentHarvestResource.getIffFileName());
	                theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_NAME, currentHarvestResource.getName()));
	                theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_CLASS, currentHarvestResource.getResourceClass() + " " + currentHarvestResource.getResourceType()));
	                short coldResist = currentHarvestResource.getColdResistance();
	                short conductivity = currentHarvestResource.getConductivity();
	                short decayResist = currentHarvestResource.getDecayResistance();
	                short entangleResist = currentHarvestResource.getEntangleResistance();
	                short flavor = currentHarvestResource.getFlavor();
	                short heatResist = currentHarvestResource.getHeatResistance();
	                short malleability = currentHarvestResource.getMalleability();
	                short overallQuality  = currentHarvestResource.getOverallQuality();
	                short potentialEnergy = currentHarvestResource.getPotentialEnergy();
	                short shockResist = currentHarvestResource.getShockResistance();
	                short unitToughness = currentHarvestResource.getUnitToughness();
	
	                if (coldResist != 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_COLD_RESIST, String.valueOf(coldResist)));
	                }
	                if (conductivity != 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_CONDUCTIVITY, String.valueOf(conductivity)));
	                }
	                if (decayResist != 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_DECAY_RESIST, String.valueOf(decayResist)));
	                }
	                if (entangleResist != 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_ENTANGLE_RESISTANCE, String.valueOf(entangleResist)));
	                }
	                if (flavor != 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_FLAVOR, String.valueOf(flavor)));
	                }
	                if (heatResist!= 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_HEAT_RESIST, String.valueOf(heatResist)));
	                }
	                if (malleability != 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_MALLEABILITY, String.valueOf(malleability)));
	                }
	                if (overallQuality != 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_QUALITY, String.valueOf(overallQuality)));
	                }
	                if (potentialEnergy!= 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY, String.valueOf(potentialEnergy)));
	                }
	                if (shockResist!= 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE, String.valueOf(shockResist)));
	                }
	                if (unitToughness != 0) {
	                    theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_TOUGHNESS, String.valueOf(unitToughness)));
	                }
	                theResource.addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_VOLUME, String.valueOf(theResource.getStackQuantity()) + "/" + String.valueOf(Constants.MAX_STACK_SIZE)));
	                //----------------
	                //server.addObjectIDToAllUsedID(theResource.getID());
	                server.addObjectToAllObjects(theResource, false, false);
	                if(!vOutputHopper.contains(theResource))
	                {
                        if(theResource.getStackQuantity() != 0) {  // Don't stack empty crates in the hopper! @CODEMONKEY
                            vOutputHopper.put(theResource.getID(), theResource);
                        }
	                }
				}
				int amountHarvestedThisTick = (int)PacketUtils.unKK((lCurrentExtractionRateMSKK * lElapsedTimeMS));
				lCurrentHarvestedResourceAmountKK += (lCurrentExtractionRateMSKK * lElapsedTimeMS);
				iCurrentHarvestedResourceAmount = (int)PacketUtils.unKK(lCurrentHarvestedResourceAmountKK);
				//System.out.println("Harvested " + lCurrentHarvestedResourceAmountKK+ " KK resources -- " + iCurrentHarvestedResourceAmount + " normal ones.");
				//System.out.println("Previously harvested " + iLastHarvestedResourceAmount);
				//System.out.println("Elapsed timeMS: " + lElapsedTimeMS);
				theResource.setStackQuantity((int)iCurrentHarvestedResourceAmount, false);
                iTotalResourcesInHopper += amountHarvestedThisTick;
                // Recalculate hopper contents once every 2000 ticks.  Will be approximately every 3.3 minutes, depending on server load.
                if (iTicks % 200 == 0) {
                	iTotalResourcesInHopper = 0;
                	Enumeration<SOEObject> vOutputHopperEnum = vOutputHopper.elements();
	                while (vOutputHopperEnum.hasMoreElements())
	                {
	                    ResourceContainer r = (ResourceContainer)vOutputHopperEnum.nextElement();
	                    iTotalResourcesInHopper += r.getStackQuantity();
	                }
                }
                if(iTotalResourcesInHopper >= this.getIOutputHopperSize())
                {
                    int iResOverflow = iTotalResourcesInHopper - this.getIOutputHopperSize();
                    theResource.setStackQuantity(theResource.getStackQuantity() - iResOverflow, false);
                    this.deactivateInstallation();
                }
	            //System.out.println("Harvest Tick - Amount Harvested " + (iResourceAmountHarvested / 60) + " Total In Hopper: " + rc.getQuantity());
	        }
		    //-------------END HARVESTER RELATED
		    if(vSyncronizedListeners!= null && !vSyncronizedListeners.isEmpty() && iCurrentHarvestedResourceAmount != iLastHarvestedResourceAmount)
		    {
	        /**
	         * @todo send updates about the harvester to any clients in this list!
	         */
	            Enumeration<ZoneClient> zcEnum = this.vSyncronizedListeners.elements();
	            while(zcEnum.hasMoreElements())
	            {
	                ZoneClient client = zcEnum.nextElement();
	                if(client.bHasActiveThread())
	                {
	                   client.insertPacket(PacketFactory.buildBaselineHINO7(this, vResourcesAvailable));
	                   client.insertPacket(PacketFactory.buildDeltasMessageHINO7(this, vResourcesAvailable));
	                   if(!vOutputHopper.isEmpty())
	                   {
	                       client.insertPacket(PacketFactory.buildDeltasMessageHINO7_ResourceHopper(this));
	                   }
	                }
	                else
	                {
	                    //System.out.println("Sync Listener removed.");
	                    vSyncronizedListeners.remove(client);
	                }
	            }
	        }
		    iLastHarvestedResourceAmount = iCurrentHarvestedResourceAmount;
		} catch (Exception e) {
			System.out.println("Error updating harvester: " + e.toString());
			e.printStackTrace();
		}
	}
	
    // BER (base extraction rate) = the number of resource units per MINUTE that the harvester will harvest.
    // Actual extraction rate per minute is the base * current resource concentration.
    // Actual extraction rate per millisecond = actual extraction rate / (60 * 1000);
    public int getBaseExtractionRate() {
    	//System.out.println("Harvester::getBaseExtractionRate: returning BER " + iBaseExtractionRateMins);
    	return iBaseExtractionRateMins;
    }

    public void setBaseExtractionRateMS(int iExtractionRate) {
        this.iBaseExtractionRateMins = iExtractionRate;
        lBaseExtractionRateMSKK = PacketUtils.toKK(iBaseExtractionRateMins) / 60000; // The amount of resources extracted per millisecond, bitshifted left 20 bits.  (In effect, multiplied by 1048576)
        
    }

    public float getIActualExtractionRate() {
        /**
         * @todo Modifiers for harvesters have to be calculated here
         */
        float iResourceDraw = (getBaseExtractionRate() * getCurrentResourceConcentration()) / 100;
        return iResourceDraw;
    }   

    public SpawnedResourceData getCurrentHarvestResource() {
        return currentHarvestResource;
    }

    public void setCurrentHarvestResource(SpawnedResourceData currentHarvestResource) {
        this.currentHarvestResource = currentHarvestResource;
        deactivateInstallation();
        System.out.println("Set resource " + currentHarvestResource.getName());
        setCurrentResourceConcentration(currentHarvestResource.getBestDensityAtLocation(getX(), getY()));
        
    }

    public float getCurrentResourceConcentration() {
        return currentResourceConcentration;
    }

    private void setCurrentResourceConcentration(float currentResourceConcentration) {
        
    	this.currentResourceConcentration = currentResourceConcentration;
        lCurrentExtractionRateMSKK = (long)(lBaseExtractionRateMSKK * (currentResourceConcentration / 100.0f));
        //System.out.println("Resource concentration: " + currentResourceConcentration + ", base extraction rateKKMS: " + lBaseExtractionRateMSKK + ".  Current extractedKKMS: " + lCurrentExtractionRateMSKK);
    }

    public boolean addSyncronizedListener(ZoneClient client){
        if(vSyncronizedListeners == null)
        {
            vSyncronizedListeners = new Vector<ZoneClient>();
        }
        if(!vSyncronizedListeners.contains(client))
        {
            return vSyncronizedListeners.add(client);
        }
        return vSyncronizedListeners.contains(client);
    }

    public boolean removeSyncronizedListener(ZoneClient client){
        if(vSyncronizedListeners == null)
        {
            vSyncronizedListeners = new Vector<ZoneClient>();
        }
        if(vSyncronizedListeners.contains(client))
        {
            vSyncronizedListeners.remove(client);
            return true;
        }
        return vSyncronizedListeners.contains(client);
    }

    public byte getHarvesterUpdateCounter() {
        harvesterUpdateCounter++;
        return harvesterUpdateCounter;
    }

    public int getIHarvesterResourceUpdateCounter() {
        iHarvesterResourceUpdateCounter++;
        return iHarvesterResourceUpdateCounter;
    }

    public int resetHarvesterResourceUpdateCounter() {
        iHarvesterResourceUpdateCounter = 0;
        return iHarvesterResourceUpdateCounter;
    }
    public String getSClientEffect() {
        return sClientEffect;
    }

    public void setSClientEffect(String sClientEffect) {
        this.sClientEffect = sClientEffect;
    }
    public int getTotalHopperQuantity() {
        int iTotalHopperContents = 0;
        Enumeration<SOEObject> vHopperEnum = vOutputHopper.elements(); 
        while (vHopperEnum.hasMoreElements())
        {
            ResourceContainer r = (ResourceContainer)vHopperEnum.nextElement();
            iTotalHopperContents += r.getStackQuantity();
        }
        return iTotalHopperContents;
    }
    
    public void useItemByCommandID(byte commandID) {
    	
    }
    
    public void setHarvesterType(byte type) {
    	iHarvesterType = type;
    }
    
    public byte getHarvesterType() {
    	return iHarvesterType;
    }

    public void initializeAvailableResources() {
    	ResourceManager resourceManager = getServer().getResourceManager();
    	vResourcesAvailable = null;
    	switch (iHarvesterType) {
	    	case Constants.HARVESTER_TYPE_CHEMICAL: {
	    		vResourcesAvailable = resourceManager.getResourceListForTool(14039, getPlanetID());
	    		break;
	    	}
	    	case Constants.HARVESTER_TYPE_FLORA: {
	    		vResourcesAvailable = resourceManager.getResourceListForTool(14040, getPlanetID());
	    		
	    		break;
	    	}
	    	case Constants.HARVESTER_TYPE_FUSION: {
	    		vResourcesAvailable = resourceManager.getResourceListForTool(14041, getPlanetID());
	    		for (int i = 0; i < vResourcesAvailable.size(); i++) {
	    			SpawnedResourceData resource = vResourcesAvailable.elementAt(i);
	    			int resourceIndex = resource.getIGenericResourceIndex();
	    			if ((resourceIndex >= Constants.RESOURCE_START_RADIOACTIVE && resourceIndex <= Constants.RESOURCE_END_RADIOACTIVE )
	    					|| resourceIndex == Constants.RESOURCE_TYPE_JTL_RADIOACTIVE_PLOYMETRIC
	    					|| resourceIndex == Constants.RESOURCE_TYPE_UNKNOWN_RADIOACTIVE) {
	    				
	    			} else {
	    				vResourcesAvailable.remove(i);
	    				i--;
	    			}
	    		}
	    		break;
	    	}
	    	case Constants.HARVESTER_TYPE_GAS: {
	    		vResourcesAvailable = resourceManager.getResourceListForTool(14037, getPlanetID());
	
	    		break;
	    	}
	    	case Constants.HARVESTER_TYPE_MINERAL: {
	    		vResourcesAvailable = resourceManager.getResourceListForTool(14041, getPlanetID());
	    		break;
	    	}
	    	case Constants.HARVESTER_TYPE_SOLAR: {
                vResourcesAvailable = resourceManager.getResourceListForTool(14044, getPlanetID());
	    		break;
	    	}
	    	case Constants.HARVESTER_TYPE_WATER: {
	    		vResourcesAvailable = resourceManager.getResourceListForTool(14042, getPlanetID());
	    		break;
	    	}
	    	case Constants.HARVESTER_TYPE_WIND: {
	    		vResourcesAvailable = resourceManager.getResourceListForTool(14045, getPlanetID());
	    		break;
	    	}
	    	
    	}
    }
    
    public void updateAvailableResources() {
    	for (int i = 0; i < vResourcesAvailable.size(); i++) {
    		SpawnedResourceData resource = vResourcesAvailable.elementAt(i);
    		if (resource.isSpawned()) {
    			// Do nothing -- still available.
    		} else {
    			initializeAvailableResources();
    			if (currentHarvestResource.getID() == resource.getID()) {
    				deactivateInstallation();
    				currentHarvestResource = null;
    				
    			}
    		}
    	}
    }
    
    public Vector<SpawnedResourceData> getResourcesAvailable() {
    	return vResourcesAvailable;
    }
    public Hashtable<Long, SOEObject> getOutputHopper() {
    	return vOutputHopper;
    }
    
    protected byte[] setConditionDamage(int iCondition, boolean bUpdate) throws IOException {
		super.setConditionDamage(iCondition, false);
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_HINO, (byte)3, (short)1, (short)10, this, getConditionDamage());
		}
		return null;
	}
}

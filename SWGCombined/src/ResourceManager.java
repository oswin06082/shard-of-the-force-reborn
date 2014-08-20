import java.util.Hashtable;
import java.util.Vector;


/**
 * The ResourceManager class is responsible for the spawning and despawning of ResourceData, and for creating ResourceContainers for Players as needed.
 * @author Darryl
 *
 */
public class ResourceManager implements Runnable{
	private Thread myThread;
	private ZoneServer myServer;
	private DatabaseInterface dbInterface = null;
	//private SWGGui SWGGui = null;
	private Vector<SpawnedResourceData> vAllSpawnedResources;
	private SpawnedResourceData spawnedPool1Steel;
	private SpawnedResourceData spawnedPool1Copper;
	private SpawnedResourceData spawnedPool1Aluminum;
	private SpawnedResourceData spawnedPool1ExtrusiveOre;
	private SpawnedResourceData spawnedPool1IntrusiveOre;
	private SpawnedResourceData spawnedPool1CarbonateOre;
	private SpawnedResourceData spawnedPool1CrystallineGemstone;
	private SpawnedResourceData spawnedPool1AmorphousGemstone;
	private SpawnedResourceData spawnedPool1RadioactiveOre;
	private SpawnedResourceData spawnedPool1SolidPetrochemical;
	private SpawnedResourceData spawnedPool1LiquidPetrochemical;
	private SpawnedResourceData[] spawnedPool1Polymer;
	private SpawnedResourceData[] spawnedPool1LubricatingOil;
	private Vector<SpawnedResourceData> spawnedPool2Resources;
	private Vector<SpawnedResourceData> spawnedPool3Resources;
	private Hashtable<Integer, Vector<SpawnedResourceData>> spawnedPool4ResourcesByPlanet;
	private Vector<SpawnedResourceData> vAllDespawnedResources;
    private Vector<String> vAllResourceNames;
	
	private int iGeneratedResourceCount;
	
	public ResourceManager(ZoneServer server) {
		myServer = server;
		dbInterface = null;
		vAllSpawnedResources = null;
                vAllResourceNames = new Vector<String>();
		spawnedPool1Polymer = new SpawnedResourceData[2];
		spawnedPool1LubricatingOil = new SpawnedResourceData[2];
		spawnedPool2Resources = new Vector<SpawnedResourceData>();
		spawnedPool3Resources = new Vector<SpawnedResourceData>();
		spawnedPool4ResourcesByPlanet = new Hashtable<Integer, Vector<SpawnedResourceData>>();
		// Note:  The Tutorial map is also a Planet, but it should have no resources spawned on it.  Thus the -1 in the loop.
		for (int i = 0; i < Constants.PlanetNames.length - 1; i++) {
			spawnedPool4ResourcesByPlanet.put(i, new Vector<SpawnedResourceData>());
		}
		myThread = new Thread(this);
		myThread.setName("Resorce Manager thread");
	}

	public void startThread(){
		myThread.start();
	}
	
	private boolean bInitialized = false;
	public void initialize() {
		bInitialized = true;
		//SWGGui = myServer.getGUI();
		vAllDespawnedResources = new Vector<SpawnedResourceData>();
		vAllSpawnedResources = dbInterface.loadResources(myServer.getServerID());
		if (vAllSpawnedResources.isEmpty()) {
			generateResourceTable();
		} 
		// Now there IS data in the resource table, and we need to sort it out.
		loadResourceTableIntoPointers();
                for(int i = 0; i < vAllSpawnedResources.size(); i++)
                {
                    SpawnedResourceData r = vAllSpawnedResources.get(i);
                    vAllResourceNames.add(r.getName());
                }    
                for(int i = 0; i < vAllDespawnedResources.size(); i++)
                {
                    SpawnedResourceData r = vAllDespawnedResources.get(i);
                    vAllResourceNames.add(r.getName());
                }                   
		// Now, everything is all set to go.
	}
	
	private long lCurrentTimeMS = 0;
	
	private void loadResourceTableIntoPointers() {
		for (int i = 0; i < vAllSpawnedResources.size(); i++) {
			SpawnedResourceData vResource = vAllSpawnedResources.elementAt(i);
			//System.out.println("Resource " + vResource.getName() + ", a type of " + vResource.getResourceClass() + " " + vResource.getResourceType() + ".  Spawned? " + vResource.isSpawned());
			if (vResource.isSpawned()) {
				int iPool = vResource.getPool();
				switch (iPool) {
					case Constants.RESOURCE_POOL_1: {
						String sResourceTypeLC = vResource.getIffFileName().toLowerCase();
						if (sResourceTypeLC.contains("aluminum")) {
							spawnedPool1Aluminum = vResource;
						} else if (sResourceTypeLC.contains("steel")) {
							spawnedPool1Steel = vResource;
						} else if (sResourceTypeLC.contains("copper")) {
							spawnedPool1Copper = vResource;
						} else if (sResourceTypeLC.contains("extrusive")) {
							spawnedPool1ExtrusiveOre = vResource;
						} else if (sResourceTypeLC.contains("intrusive")) {
							spawnedPool1IntrusiveOre = vResource;
						} else if (sResourceTypeLC.contains("carbonate")) {
							spawnedPool1CarbonateOre = vResource;
						} else if (sResourceTypeLC.contains("crystalline")) {
							spawnedPool1CrystallineGemstone = vResource;
						} else if (sResourceTypeLC.contains("armophous")) {
							spawnedPool1AmorphousGemstone = vResource;
						} else if (sResourceTypeLC.contains("radioactive")) {
							spawnedPool1RadioactiveOre = vResource;
						} else if (sResourceTypeLC.contains("solid")) {
							spawnedPool1SolidPetrochemical = vResource;
						} else if (sResourceTypeLC.contains("liquid")) {
							spawnedPool1LiquidPetrochemical = vResource;
						} else if (sResourceTypeLC.contains("polymer")) {
							if (spawnedPool1Polymer[0] == null) {
								spawnedPool1Polymer[0] = vResource;
							} else {
								spawnedPool1Polymer[1] = vResource;
							}
						} else if (sResourceTypeLC.contains("lubricat")) {
							if (spawnedPool1LubricatingOil[0] == null) {
								spawnedPool1LubricatingOil[0] = vResource;
							} else {
								spawnedPool1LubricatingOil[1] = vResource;
							}
						} else {
							System.out.println("Unknown pool 1 resource type: " + sResourceTypeLC);
						}
						break;
					}
					case Constants.RESOURCE_POOL_2: {
						// Pool 2 is the random pool -- we'll just throw the pointers into the vector.
						spawnedPool2Resources.add(vResource);
						break;
					}
					case Constants.RESOURCE_POOL_3: {
						spawnedPool3Resources.add(vResource);
						break;
					}
					case Constants.RESOURCE_POOL_4: {
						int iPlanetID = vResource.getPlanetID();
						spawnedPool4ResourcesByPlanet.get(iPlanetID).add(vResource);
						break;
					}
					default: {
						throw new ArrayIndexOutOfBoundsException("Error: Unknown pool for resource " + vResource);
					}
				}
			} else {
				// It's NOT a spawned resource.
				vAllDespawnedResources.add(vResource);
				vAllSpawnedResources.remove(vResource);
			}
		}

	}
	
	public void run() {
		dbInterface = myServer.getGUI().getDB();
		while (myThread != null) {
			try {
				if (!bInitialized) {
					initialize();
				}
				synchronized(this) {
					Thread.yield();
					wait(10000); // Only loop through on this thread once every 10 seconds.
				}
				lCurrentTimeMS = System.currentTimeMillis();
				// This loop will despawn old resources and spawn new replacement resources for them.
				for (int i = 0; i < vAllSpawnedResources.size(); i++) {
					SpawnedResourceData resource = vAllSpawnedResources.elementAt(i);
					if (resource.getLDespawnTimeMS() < lCurrentTimeMS) {
						if (resource.isSpawned()) {
							resource.setIsSpawned(false);
							byte iPool = resource.getPool();
							int iResourceType = resource.getType();
							int iNewResourceTypeLower = 0;
							int iNewResourceTypeUpper = 0;
							int iFirstPlanetID = resource.getPlanetID();
							if (iPool == Constants.RESOURCE_POOL_1) {
								if (iResourceType <= Constants.RESOURCE_END_ALUMINUM) {
									iNewResourceTypeUpper = Constants.RESOURCE_END_ALUMINUM;
								} else if (iResourceType <= Constants.RESOURCE_END_COPPER) {
									iNewResourceTypeLower = Constants.RESOURCE_START_COPPER;
									iNewResourceTypeUpper = Constants.RESOURCE_END_COPPER;
								} else if (iResourceType <= Constants.RESOURCE_END_STEEL) {
									iNewResourceTypeLower = Constants.RESOURCE_START_STEEL;
									iNewResourceTypeUpper = Constants.RESOURCE_END_STEEL;
								} else if (iResourceType <= Constants.RESOURCE_END_ORE_CARBONATE) {
									iNewResourceTypeLower = Constants.RESOURCE_START_ORE_CARBONATE;
									iNewResourceTypeUpper = Constants.RESOURCE_END_ORE_CARBONATE;
								} else if (iResourceType <= Constants.RESOURCE_END_ORE_EXTRUSIVE) {
									iNewResourceTypeLower = Constants.RESOURCE_START_ORE_EXTRUSIVE;
									iNewResourceTypeUpper = Constants.RESOURCE_END_ORE_EXTRUSIVE;
								}  else if (iResourceType <= Constants.RESOURCE_END_ORE_INTRUSIVE) {
									iNewResourceTypeLower = Constants.RESOURCE_START_ORE_INTRUSIVE;
									iNewResourceTypeUpper = Constants.RESOURCE_END_ORE_INTRUSIVE;
								}  else if (iResourceType <= Constants.RESOURCE_END_GEMSTONE_AMORPHOUS) {
									iNewResourceTypeLower = Constants.RESOURCE_START_GEMSTONE_AMORPHOUS;
									iNewResourceTypeUpper = Constants.RESOURCE_END_GEMSTONE_AMORPHOUS;
								}  else if (iResourceType <= Constants.RESOURCE_END_GEMSTONE_CRYSTALLINE) {
									iNewResourceTypeLower = Constants.RESOURCE_START_GEMSTONE_CRYSTALLINE;
									iNewResourceTypeUpper = Constants.RESOURCE_END_GEMSTONE_CRYSTALLINE;
								}  else if (iResourceType <= Constants.RESOURCE_END_RADIOACTIVE) {
									iNewResourceTypeLower = Constants.RESOURCE_START_RADIOACTIVE;
									iNewResourceTypeUpper = Constants.RESOURCE_END_RADIOACTIVE;
								}  else if (iResourceType <= Constants.RESOURCE_END_PETROCHEMICAL_SOLID) {
									iNewResourceTypeLower = Constants.RESOURCE_START_PETROCHEMICAL_SOLID;
									iNewResourceTypeUpper = Constants.RESOURCE_END_PETROCHEMICAL_SOLID;
								}  else if (iResourceType <= Constants.RESOURCE_END_PETROCHEMICAL_LIQUID) {
									iNewResourceTypeLower = Constants.RESOURCE_START_PETROCHEMICAL_LIQUID;
									iNewResourceTypeUpper = Constants.RESOURCE_END_PETROCHEMICAL_LIQUID;
								}  else if (iResourceType == Constants.RESOURCE_TYPE_POLYMER) {
									iNewResourceTypeLower = Constants.RESOURCE_TYPE_POLYMER;
									iNewResourceTypeUpper = Constants.RESOURCE_TYPE_POLYMER;
								}  else if (iResourceType == Constants.RESOURCE_TYPE_LUBRICATING_OIL) {
									iNewResourceTypeLower = Constants.RESOURCE_TYPE_LUBRICATING_OIL;
									iNewResourceTypeUpper = Constants.RESOURCE_TYPE_LUBRICATING_OIL;
								}
								iResourceType = SWGGui.getRandomInt(iNewResourceTypeLower, iNewResourceTypeUpper); 
								// We need to get the resource type, and spawn a new resource in the same type.
							} else if (iPool == Constants.RESOURCE_POOL_2) {
								iResourceType = -1;
								iFirstPlanetID = -1;
								// Who cares -- this pool is entirely random.
							} else if (iPool == Constants.RESOURCE_POOL_3) {
								if (iResourceType >= Constants.RESOURCE_START_IRON && iResourceType <= Constants.RESOURCE_END_IRON) {
									iResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_IRON, Constants.RESOURCE_END_IRON);
								} 
								iFirstPlanetID = -1;
								// Well, it's either a random Iron or a given JTL resource...
							} else if (iPool == Constants.RESOURCE_POOL_4) {
								// It's the planetary resource.  Respawn it exactly.
							}
							
							SpawnedResourceData newResource = spawnResource(resource.getPool(), iFirstPlanetID, iResourceType);
							dbInterface.saveResource(newResource, myServer.getServerID());
						}
						dbInterface.updateResource(resource, myServer.getServerID());
						vAllSpawnedResources.remove(resource);
						vAllDespawnedResources.add(resource);
					}
				}
			} catch (Exception e) {
				System.out.println("Error in ResourceManager thread: " + e.toString());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This function generates a fresh resource table and inputs it into the passed vector.
	 * @param resources -- The vector to store the new resources in.
	 */
	private void generateResourceTable() {
		//System.out.println("Generating first server resource spawn list.");
		/* TODO:  We need to load the rules for what resources, how many resources, where they spawn, when they spawn, etc. from somewhere.
		 * This could be information stored in the config file, I suppose, or it could be part of the database as it is now.
		 * Regardless, we need to get this data from somewhere, reference it for the resource types we're creating.
		 * Once we have that data, we start looping through various loops, creating all of the resources we have to have,
		 * for all of the planets, etc.
		 * 
		 * For now, this is hardcoded.
		 */
		
		// Spawn Pool 2 Resources.
		byte pool2ResourceTotal = 24;
		for (byte i = 0; i < pool2ResourceTotal; i++) {
			//System.out.println("Spawning pool 2 resource number " + i);
			spawnPool2Resource();
		}

		// Spawn Pool 3 Resources.
		byte pool3Iron = 16;
		byte pool3JTL = 8;
		for (byte i = 0; i < pool3JTL; i++) {
			spawnPool3Resource(SWGGui.getRandomInt(Constants.RESOURCE_START_JTL, Constants.RESOURCE_END_JTL));
		}
		
		for (byte i = 0; i < pool3Iron; i++) {
			spawnPool3Resource(SWGGui.getRandomInt(Constants.RESOURCE_START_IRON, Constants.RESOURCE_END_IRON));
		}
		
		for (int j = Constants.RESOURCE_START_PLANETARY; j <= Constants.RESOURCE_END_PLANETARY; j++) {
			spawnPool4Resource(((j - 4) % 10), j);
		}
	
		int iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_ALUMINUM, Constants.RESOURCE_END_ALUMINUM);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_STEEL, Constants.RESOURCE_END_STEEL);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_COPPER, Constants.RESOURCE_END_COPPER);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_ORE_EXTRUSIVE, Constants.RESOURCE_END_ORE_EXTRUSIVE);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_ORE_INTRUSIVE, Constants.RESOURCE_END_ORE_INTRUSIVE);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_ORE_CARBONATE, Constants.RESOURCE_END_ORE_CARBONATE);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_GEMSTONE_CRYSTALLINE, Constants.RESOURCE_END_GEMSTONE_CRYSTALLINE);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_GEMSTONE_AMORPHOUS, Constants.RESOURCE_END_GEMSTONE_AMORPHOUS);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_RADIOACTIVE, Constants.RESOURCE_END_RADIOACTIVE);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_PETROCHEMICAL_SOLID, Constants.RESOURCE_END_PETROCHEMICAL_SOLID);
		spawnPool1Resource(iPool1ResourceType);
		iPool1ResourceType = SWGGui.getRandomInt(Constants.RESOURCE_START_PETROCHEMICAL_LIQUID, Constants.RESOURCE_END_PETROCHEMICAL_LIQUID);
		spawnPool1Resource(iPool1ResourceType);
		// Polymer and lubricating oil are done twice.
		iPool1ResourceType = Constants.RESOURCE_TYPE_POLYMER;
		spawnPool1Resource(iPool1ResourceType);
		spawnPool1Resource(iPool1ResourceType);
		
		iPool1ResourceType = Constants.RESOURCE_TYPE_LUBRICATING_OIL;
		spawnPool1Resource(iPool1ResourceType);
		spawnPool1Resource(iPool1ResourceType);

		// Now we really should save all that data to the database.
		
		dbInterface.saveAllResourcesToDatabase(vAllSpawnedResources, vAllDespawnedResources, myServer.getServerID());
	}
	
	private SpawnedResourceData spawnResource(byte iPool, int iPlanet, int iResourceType) {
		SpawnedResourceData theResource = null;
		switch (iPool) {
			case Constants.RESOURCE_POOL_1: {
				theResource = spawnPool1Resource(iResourceType); // If it's a Pool 1 resource, we need to know what resource type it is.
				break;
			}
			case Constants.RESOURCE_POOL_2: {
				theResource = spawnPool2Resource(); // If it's a pool 2 resource, the resource type doesn't matter, since it's random.
				break;
			}
			case Constants.RESOURCE_POOL_3: {
				theResource = spawnPool3Resource(iResourceType); // If it's a pool 3 resource, we need to know the resource type if it's a JTL resource.
				break;
			}
			case Constants.RESOURCE_POOL_4: {
				theResource = spawnPool4Resource(iPlanet, iResourceType);
				break;
			}
			default: {
				throw new ArrayIndexOutOfBoundsException("Error:  Attempting to spawn resource in unknown pool.");
			}
		}
		return theResource;
	}
	
	private void fillInResourceData(SpawnedResourceData theResource, ResourceTemplateData template) {
		iGeneratedResourceCount++;
		short iCap = 0;
		short[][] iCaps = template.getAllCaps();
		// We've got the caps.
		for (int i = 0; i < iCaps.length; i++) {
			//System.out.println("iCap["+i+"][0] = " + iCaps[i][0] + ", iCap["+i+"][1] = " + iCaps[i][1]);
			iCap = SWGGui.getRandomShort(iCaps[i][0], iCaps[i][1]);
			theResource.setCap(i, iCap);
		}
		theResource.setResourceType(template.getResourceType());
		theResource.setResourceClass(template.getResourceClass());
		// We've got the STF filename.  Maybe.
		theResource.setStfFileName(template.getResourceTemplateName());
		theResource.setIffFileName(template.getResourceRootName());
		theResource.setGenericResourceIndex(template.getResourceTypeID());
		// We've got the name.
                /*
		String sName = null;
		StringBuffer buffName = new StringBuffer();
		int nameLen = SWGGui.getRandomInt(4, 10);
		buffName.append(Constants.ALL_CHARACTERS[SWGGui.getRandomInt(0, 26)]);
		for (int i = 1; i < nameLen; i++) {
			buffName.append(Constants.ALL_CHARACTERS[SWGGui.getRandomInt(27, Constants.ALL_CHARACTERS.length)]);
		}
		sName = buffName.toString();*/
//'		theResource.setName(sName);
                String newRandomName = "";                      
                boolean proceed = true;                  
                while(proceed)
                {
                    newRandomName = PacketUtils.generateResourceName();
                    proceed = vAllResourceNames.contains(newRandomName);
                }
                theResource.setName(newRandomName);
                vAllResourceNames.add(newRandomName);
		// We've got the resource ID.
		
		theResource.setID(myServer.getNextObjectID());
		theResource.setResourceContainerTemplateID(template.getTemplateID());
		//myServer.addObjectToAllObjects(theResource, false);
		// We've got the resource location, density, and radius.
		int numLocations = SWGGui.getRandomInt(8, 64);
		//System.out.println("Generating " + numLocations + " coordinates for resource " + theResource.getName());
		for (int i = 0; i < numLocations; i++) {
			int spawnX = SWGGui.getRandomInt(-8192, 8192);
			int spawnY = SWGGui.getRandomInt(-8192, 8192);
			int spawnDensity = SWGGui.getRandomInt(30, 100);
			int spawnRadius = SWGGui.getRandomInt(384, 1280);
			//System.out.println("X["+spawnX+"] Y["+spawnY + "] Radius["+spawnRadius+"] Density["+spawnDensity+"]");
			ResourceSpawnCoordinateData coordinates = new ResourceSpawnCoordinateData();
			coordinates.setSpawnX(spawnX);
			coordinates.setSpawnY(spawnY);
			coordinates.setSpawnRadius(spawnRadius);
			coordinates.setSpawnDensity(spawnDensity);
			theResource.addCoordinates(coordinates);
		}
		if (theResource.getPool() != Constants.RESOURCE_POOL_4) {
			int iNumPlanets = SWGGui.getRandomInt(6, 8);
			for (int i = 0; i < iNumPlanets; i++) {
				int iPlanetID = -1;
				do {
					iPlanetID = SWGGui.getRandomInt(0, Constants.PlanetNames.length - 1);
				} while (theResource.getIsSpawnedOnPlanet(iPlanetID));
				theResource.setPlanetID(i, iPlanetID);
			}
		}
		
		long lDespawnTime = SWGGui.getRandomLong(template.getDespawnTimerRangeMS());
		lDespawnTime += System.currentTimeMillis();
		lDespawnTime += template.getMinDespawnTimerMS();
		//System.out.println("Spawning resource.  Current time: " + System.currentTimeMillis() + ", despawn timer " + lDespawnTime);
		//System.out.println("In calendar: " + new Timestamp(System.currentTimeMillis()) + ", " + new Timestamp(lDespawnTime));
		
		theResource.setLDespawnTimeMS(lDespawnTime);
		//System.out.println("Built new resource: " + theResource.getName());
		//System.out.println("\t" + theResource.getStfFileName() + ", " + theResource.getIffFileName());
		//System.out.println("Pool " + theResource.getPool() + ", Type: " + theResource.getGenericResourceType());
		theResource.setDrawColor(SWGGui.getRandomInt(0xFFFFFF));
	}
	
	private void fillInResourceData(SpawnedResourceData theResource, ResourceTemplateData template, int iPlanetID) {
		fillInResourceData(theResource, template);
		theResource.setPlanetID(0, iPlanetID);
	}
	
	private SpawnedResourceData spawnPool1Resource(int iResourceType) {
		SpawnedResourceData theResource = new SpawnedResourceData();
		ResourceTemplateData template = DatabaseInterface.getResourceTemplate(iResourceType);
		theResource.setPool(Constants.RESOURCE_POOL_1);
		theResource.setType(iResourceType);
		fillInResourceData(theResource, template);
		setPool1Pointer(theResource, iResourceType);
		// Assign to pointer.
		vAllSpawnedResources.add(theResource);
		return theResource;
	}
	
	private SpawnedResourceData spawnPool2Resource() {
		short iResourceType = SWGGui.getRandomShort(0, Constants.RESOURCE_END_GAS_REACTIVE);
		ResourceTemplateData resData = DatabaseInterface.getResourceTemplate(iResourceType);
		SpawnedResourceData theResource = new SpawnedResourceData();
		theResource.setPool(Constants.RESOURCE_POOL_2);
		theResource.setType(iResourceType);
		fillInResourceData(theResource, resData);
		vAllSpawnedResources.add(theResource);
		spawnedPool2Resources.add(theResource);
		return theResource;
	}

	private SpawnedResourceData spawnPool3Resource(int iResourceType) {
		ResourceTemplateData template = DatabaseInterface.getResourceTemplate(iResourceType);
		SpawnedResourceData theResource = new SpawnedResourceData();
		theResource.setPool(Constants.RESOURCE_POOL_3);
		theResource.setType(iResourceType);
		fillInResourceData(theResource, template);
		spawnedPool3Resources.add(theResource);
		vAllSpawnedResources.add(theResource);
		return theResource;
	}

	private SpawnedResourceData spawnPool4Resource(int iPlanetID, int iResourceType) {
		ResourceTemplateData template = DatabaseInterface.getResourceTemplate(iResourceType);
		SpawnedResourceData theResource = new SpawnedResourceData();
		theResource.setPool(Constants.RESOURCE_POOL_4);
		theResource.setType(iResourceType);
		fillInResourceData(theResource, template, iPlanetID);
		spawnedPool4ResourcesByPlanet.get(iPlanetID).add(theResource);
		vAllSpawnedResources.add(theResource);
		return theResource;
	}
	
	private void setPool1Pointer(SpawnedResourceData theResource, int iResourceType) {
		if (iResourceType <= Constants.RESOURCE_END_ALUMINUM) {
			spawnedPool1Aluminum = theResource;
		} else if (iResourceType <= Constants.RESOURCE_END_COPPER) {
			spawnedPool1Copper = theResource;
		} else if (iResourceType <= Constants.RESOURCE_END_STEEL) {
			spawnedPool1Steel = theResource;
		} else if (iResourceType <= Constants.RESOURCE_END_ORE_CARBONATE) {
			spawnedPool1CarbonateOre = theResource;
		} else if (iResourceType <= Constants.RESOURCE_END_ORE_INTRUSIVE) {
			spawnedPool1IntrusiveOre = theResource;
		} else if (iResourceType <= Constants.RESOURCE_END_ORE_EXTRUSIVE) {
			spawnedPool1ExtrusiveOre = theResource;
		} else if (iResourceType <= Constants.RESOURCE_END_GEMSTONE_AMORPHOUS) {
			spawnedPool1AmorphousGemstone = theResource;
		} else if (iResourceType <= Constants.RESOURCE_END_GEMSTONE_CRYSTALLINE) {
			spawnedPool1CrystallineGemstone = theResource;
		} else if (iResourceType <= Constants.RESOURCE_END_RADIOACTIVE) {
			spawnedPool1RadioactiveOre = theResource;
		} else if (iResourceType < Constants.RESOURCE_END_PETROCHEMICAL_SOLID) {
			spawnedPool1SolidPetrochemical = theResource;
		} else if (iResourceType < Constants.RESOURCE_END_PETROCHEMICAL_LIQUID) {
			spawnedPool1LiquidPetrochemical = theResource;
		} else if (iResourceType == Constants.RESOURCE_TYPE_POLYMER) {
			if (spawnedPool1Polymer[0] == null) {
				spawnedPool1Polymer[0] = theResource;
			} else {
				spawnedPool1Polymer[1] = theResource;
			}
		} else if (iResourceType == Constants.RESOURCE_TYPE_LUBRICATING_OIL) {
			if (spawnedPool1LubricatingOil[0] == null) {
				spawnedPool1LubricatingOil[0] = theResource;
			} else {
				spawnedPool1LubricatingOil[1] = theResource;
			}
		}
		
	}
	
	protected void generateResourceListForSurveyMessage(ZoneClient client, TangibleItem item) {
		try {
			int templateID = item.getTemplateID();
			Vector<SpawnedResourceData> vResources = getResourceListForTool(templateID, client.getPlayer().getPlanetID());
			for (int i = 0; i < vResources.size(); i++) {
				SpawnedResourceData data = vResources.elementAt(i);
				if (!data.isSpawned()) {
					vResources.remove(i);
					i--;
				}
			}
			client.insertPacket(PacketFactory.buildResourceListForSurveyMessage(item, vResources));
		} catch (Exception e) {
			System.out.println("Error handling resource tool use item request: " + e.toString());
			e.printStackTrace();
		}
	}
	
	
	protected Vector<SpawnedResourceData> getResourceListForTool(int iResourceToolTemplateID, int iPlanetID) {
	
		switch (iResourceToolTemplateID) {
			case 14037: {
				return getGasResourceList(iPlanetID);
			}
			case 14038: {
				return null;
			}
			case 14039: {
				return getPetrochemicalResourceList(iPlanetID);
			}
			case 14040: {
				return getWoodResourceList(iPlanetID);
			}
			case 14041: {
				return getMineralResourceList(iPlanetID);
			}
			case 14042: {
				return getWaterVaporResourceList(iPlanetID);
			}
			case 14043: {
				return null;
			}
			case 14044: {
				return getSolarEnergyResourceList(iPlanetID);
			}
			case 14045: {
				return getWindEnergyResourceList(iPlanetID);
			}
		}
		return null;
		
	}


	private Vector<SpawnedResourceData> getWindEnergyResourceList(int iPlanetID) {
		Vector<SpawnedResourceData> resourceListToReturn = new Vector<SpawnedResourceData>();
		Vector<SpawnedResourceData> resourcesOnPlanet = spawnedPool4ResourcesByPlanet.get(iPlanetID);
		for (int i = 0; i < resourcesOnPlanet.size(); i++) {
			if (resourcesOnPlanet.elementAt(i).getType() == Constants.RESOURCE_TYPE_ENERGY_WIND_CORELLIAN + iPlanetID) {
				resourceListToReturn.add(resourcesOnPlanet.elementAt(i));
			}
		}
		return resourceListToReturn;
	}
	
	private Vector<SpawnedResourceData> getSolarEnergyResourceList(int iPlanetID) {
		Vector<SpawnedResourceData> resourceListToReturn = new Vector<SpawnedResourceData>();
		Vector<SpawnedResourceData> resourcesOnPlanet = spawnedPool4ResourcesByPlanet.get(iPlanetID);
		for (int i = 0; i < resourcesOnPlanet.size(); i++) {
			if (resourcesOnPlanet.elementAt(i).getType() == Constants.RESOURCE_TYPE_ENERGY_SOLAR_CORELLIAN + iPlanetID) {
				resourceListToReturn.add(resourcesOnPlanet.elementAt(i));
			}
		}
		return resourceListToReturn;
	}
	
	private Vector<SpawnedResourceData> getWaterVaporResourceList(int iPlanetID) {
		Vector<SpawnedResourceData> resourceListToReturn = new Vector<SpawnedResourceData>();
		Vector<SpawnedResourceData> resourcesOnPlanet = spawnedPool4ResourcesByPlanet.get(iPlanetID);
		for (int i = 0; i < resourcesOnPlanet.size(); i++) {
			if (resourcesOnPlanet.elementAt(i).getType() == Constants.RESOURCE_TYPE_WATER_CORELLIAN + iPlanetID) {
				resourceListToReturn.add(resourcesOnPlanet.elementAt(i));
			}
		}
		return resourceListToReturn;
	}
	
	private Vector<SpawnedResourceData> getWoodResourceList(int iPlanetID) {
		Vector<SpawnedResourceData> resourceListToReturn = new Vector<SpawnedResourceData>();
		Vector<SpawnedResourceData> resourcesOnPlanet = spawnedPool4ResourcesByPlanet.get(iPlanetID);
		for (int i = 0; i < resourcesOnPlanet.size(); i++) {
			SpawnedResourceData resource = resourcesOnPlanet.elementAt(i);
			int iType = resource.getType();
			System.out.println("Planetary resource " + resource.getName() + ", type: " + resource.getResourceType() + " on planet " + resource.getPlanetID());
			if ((iType <= Constants.RESOURCE_TYPE_TUBERS_YAVINIAN) && (iType >= Constants.RESOURCE_TYPE_WOOD_CONIFER_CORELLIAN) && (resource.getPlanetID() == iPlanetID)) {
				System.out.println("Add to list.");
				resourceListToReturn.add(resource);
			}
		}
		return resourceListToReturn;
	}
	
	private Vector<SpawnedResourceData> getGasResourceList(int iPlanetID) {
		Vector<SpawnedResourceData> vGasResources = new Vector<SpawnedResourceData>();
		for (int i = 0; i < spawnedPool2Resources.size(); i++) {
			SpawnedResourceData theResource = spawnedPool2Resources.elementAt(i);
			int iResourceType = theResource.getType();
			if (iResourceType >= Constants.RESOURCE_START_GAS_INERT && iResourceType <= Constants.RESOURCE_END_GAS_REACTIVE) {
				int[] iPlanets = theResource.getAllSpawnedPlanets();
				boolean bFound = false;
				for (int j = 0; j < iPlanets.length && !bFound; j++) {
					if (iPlanets[j] == iPlanetID) {
						bFound = true;
						vGasResources.add(theResource);
					}
				}
			}
		}
		for (int i = 0; i < spawnedPool3Resources.size(); i++) {
			SpawnedResourceData theResource = spawnedPool3Resources.elementAt(i);
			int iResourceType = theResource.getType();
			if (iResourceType == Constants.RESOURCE_TYPE_JTL_GAS_REACTIVE_UNSTABLE_ORGANOMETALLIC) {
				int[] iPlanets = theResource.getAllSpawnedPlanets();
				boolean bFound = false;
				for (int j = 0; j < iPlanets.length && !bFound; j++) {
					if (iPlanets[j] == iPlanetID) {
						bFound = true;
						vGasResources.add(theResource);
					}
				}
			}
		}
		return vGasResources;
	}
	
	private Vector<SpawnedResourceData> getMineralResourceList(int iPlanetID) {
		Vector<SpawnedResourceData> vResourcesToReturn = new Vector<SpawnedResourceData>();
		for (int i = 0; i < vAllSpawnedResources.size(); i++) {
			SpawnedResourceData theResource = vAllSpawnedResources.elementAt(i);
			int iResourceType = theResource.getType();
			if (((iResourceType >= Constants.RESOURCE_START_ALUMINUM) && (iResourceType <= Constants.RESOURCE_END_UNKNOWN)) 
					|| ((iResourceType >= Constants.RESOURCE_START_JTL)
							&& (iResourceType <= Constants.RESOURCE_END_JTL)
							&& (iResourceType != Constants.RESOURCE_TYPE_JTL_GAS_REACTIVE_UNSTABLE_ORGANOMETALLIC))) {
				
				int[] iResourcePlanetID = theResource.getAllSpawnedPlanets();
				for (int j = 0; j < iResourcePlanetID.length; j++) {
					if (iResourcePlanetID[j] == iPlanetID) {
						vResourcesToReturn.add(theResource);
					}
				}
			}
		}

        if(spawnedPool1SolidPetrochemical != null)
        {
            int [] iResourcePlanetID = spawnedPool1SolidPetrochemical.getAllSpawnedPlanets();
            boolean bFound = false;
            for (int i = 0; i < iResourcePlanetID.length && !bFound; i++) {
                if (iResourcePlanetID[i] == iPlanetID) {
                    bFound = true;
                    vResourcesToReturn.add(spawnedPool1SolidPetrochemical);
                }
            }
        }

		return vResourcesToReturn;
	}
	
	private Vector<SpawnedResourceData> getPetrochemicalResourceList(int iPlanetID) {
		Vector<SpawnedResourceData> vResourcesToReturn = new Vector<SpawnedResourceData>();
		if(spawnedPool1LubricatingOil != null)
        {
            for (int i = 0; i < spawnedPool1LubricatingOil.length; i++) {
                if(spawnedPool1LubricatingOil[i]!=null)
                {
                    int[] iResourcePlanetID = spawnedPool1LubricatingOil[i].getAllSpawnedPlanets();
                    boolean bFound = false;
                    for (int j = 0; j < iResourcePlanetID.length && !bFound; j++) {
                        if (iResourcePlanetID[j] == iPlanetID) {
                            bFound = true;
                            vResourcesToReturn.add(spawnedPool1LubricatingOil[i]);
                        }
                    }
                }
            }
        }
        if(spawnedPool1Polymer != null)
        {
            for (int i = 0; i < spawnedPool1Polymer.length; i++) {
                int[] iResourcePlanetID = spawnedPool1Polymer[i].getAllSpawnedPlanets();
                boolean bFound = false;
                for (int j = 0; j < iResourcePlanetID.length && !bFound; j++) {
                    if (iResourcePlanetID[j] == iPlanetID) {
                        bFound = true;
                        vResourcesToReturn.add(spawnedPool1Polymer[i]);
                    }
                }
            }
        }
        if(spawnedPool1LiquidPetrochemical != null)
        {
            int[] iResourcePlanetID = spawnedPool1LiquidPetrochemical.getAllSpawnedPlanets();
            boolean bFound = false;
            for (int i = 0; i < iResourcePlanetID.length && !bFound; i++) {
                if (iResourcePlanetID[i] == iPlanetID) {
                    bFound = true;
                    vResourcesToReturn.add(spawnedPool1LiquidPetrochemical);
                }
            }
        }
        
        if(spawnedPool4ResourcesByPlanet != null)
        {
            Vector<SpawnedResourceData> vPlanetResources = spawnedPool4ResourcesByPlanet.get(iPlanetID);
            for (int i = 0; i < vPlanetResources.size(); i++) {
                SpawnedResourceData theResource = vPlanetResources.elementAt(i);
                int iResourceType = theResource.getType();
                if (iResourceType == (Constants.RESOURCE_TYPE_FIBERPLAST_CORELLIAN + iPlanetID)) {
                    vResourcesToReturn.add(theResource);
                }
            }
        }
		//System.out.println("Built vector of petrochemical resources.  Size: " + vResourcesToReturn)
		return vResourcesToReturn;
	}
	
	protected SpawnedResourceData getResourceByName(String sName) {
		for (int i = 0; i < vAllSpawnedResources.size(); i++) {
			SpawnedResourceData theResource = vAllSpawnedResources.elementAt(i);
			if (theResource.getName().equals(sName)) {
				return theResource;
			}
		}
		for (int i = 0; i < vAllDespawnedResources.size(); i++) {
			SpawnedResourceData theResource = vAllDespawnedResources.elementAt(i);
			if (theResource.getName().equals(sName)) {
				return theResource;
			}
		}
		return null;
	}
	protected SpawnedResourceData getResourceByID(long objectID) {
		for (int i = 0; i < vAllSpawnedResources.size(); i++) {
			SpawnedResourceData theResource = vAllSpawnedResources.elementAt(i);
			if (theResource.getID() == objectID) {
				return theResource;
			}
		}
		for (int i = 0; i < vAllDespawnedResources.size(); i++) {
			SpawnedResourceData theResource = vAllDespawnedResources.elementAt(i);
			if (theResource.getID() == objectID) {
				return theResource;
			}
		}
		return null;
	}
	
	protected int getSampledResourceAmount(SpawnedResourceData data, Player player) {
		if (false) return 250; // Set to true to forcibly return 250 units found.
		if (player.getCellID() != 0) {
			return 0; // Cannot sample indoors.
		}
		int skillMod = player.getSkillMod("surveying").getSkillModModdedValue();
		float density = data.getBestDensityAtLocation(player.getX(), player.getY());
		float densityPercentage = density / 100.0f;
		int didSampleValue = SWGGui.getRandomInt(100);
		float skillModMultiplier = skillMod;  // skillMod / 20 -- (35 / 80)
		skillModMultiplier /= 80.0f;
		int actualSkillMod = skillMod;
		if (actualSkillMod <= 35) {
			actualSkillMod = (skillMod * 3) / 2;  
		} else if (actualSkillMod <= 20) {
			actualSkillMod = skillMod * 2; 
		}
		float amount = SWGGui.getRandomInt(25) * densityPercentage * skillModMultiplier;
		if (didSampleValue <= actualSkillMod) {
			
		} else if (didSampleValue <= (actualSkillMod * 2)) {
			amount = amount / 2;
			//float amount = (SWGGui.getRandomInt(25) * densityPercentage) * skillModMultiplier;
		} else {
			amount = 0;
		}
		if (amount > 0) {
			if(amount < 1.0f) {
				amount = amount + 1.0f;
			}
			amount = Math.min(amount, 20);
		}
		return (int)amount;
	}
	
	protected Vector<SpawnedResourceData> getResourcesByPlanetID(int iPlanetID) throws NullPointerException {
		Vector<SpawnedResourceData> toReturn = new Vector<SpawnedResourceData>();
		toReturn.addAll(spawnedPool4ResourcesByPlanet.get(iPlanetID));
		for (int i = 0; i < spawnedPool2Resources.size(); i++) {
			SpawnedResourceData resource = spawnedPool2Resources.elementAt(i);
			if (resource.getIsSpawnedOnPlanet(iPlanetID)) {
				toReturn.add(resource);
			}
		}
		
		for (int i = 0; i < spawnedPool3Resources.size(); i++) {
			SpawnedResourceData resource = spawnedPool3Resources.elementAt(i);
			if (resource.getIsSpawnedOnPlanet(iPlanetID)) {
				toReturn.add(resource);
			}
		}
		if (spawnedPool1Aluminum != null && spawnedPool1Aluminum.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1Aluminum);
		}
		if (spawnedPool1AmorphousGemstone != null && spawnedPool1AmorphousGemstone.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1AmorphousGemstone);
		}
		if (spawnedPool1CarbonateOre != null && spawnedPool1CarbonateOre.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1CarbonateOre);
		}
		if (spawnedPool1Copper != null && spawnedPool1Copper.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1Copper);
		}
		if (spawnedPool1CrystallineGemstone != null && spawnedPool1CrystallineGemstone.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1CrystallineGemstone);
		}
		if (spawnedPool1ExtrusiveOre != null && spawnedPool1ExtrusiveOre.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1ExtrusiveOre);
		}
		if (spawnedPool1IntrusiveOre != null && spawnedPool1IntrusiveOre.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1IntrusiveOre);
		}
		if (spawnedPool1LiquidPetrochemical != null && spawnedPool1LiquidPetrochemical.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1LiquidPetrochemical);
		}
		if (spawnedPool1RadioactiveOre != null && spawnedPool1RadioactiveOre.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1RadioactiveOre);
		}
		if (spawnedPool1SolidPetrochemical != null && spawnedPool1SolidPetrochemical.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1SolidPetrochemical);
		}
		if (spawnedPool1Steel != null && spawnedPool1Steel.getIsSpawnedOnPlanet(iPlanetID)) {
			toReturn.add(spawnedPool1Steel);
		}
		for (int i = 0; i < spawnedPool1LubricatingOil.length; i++) {
			if (spawnedPool1LubricatingOil[i] != null) {
				if (spawnedPool1LubricatingOil[i].getIsSpawnedOnPlanet(iPlanetID)) {
					toReturn.add(spawnedPool1LubricatingOil[i]);
				}
			}			
		}
		for (int i = 0; i < spawnedPool1Polymer.length; i++) {
			if (spawnedPool1Polymer[i] != null) {
				if (spawnedPool1Polymer[i].getIsSpawnedOnPlanet(iPlanetID)) {
					toReturn.add(spawnedPool1Polymer[i]);
				}
			}			
		}
		return toReturn;
	}
	
	
}

import java.io.Serializable;
import java.util.Vector;

/**
 * The Spawned Resource Data class holds information on resources that are currently available from the world.
 * @author Darryl
 *
 */
public class SpawnedResourceData implements Serializable{
	public final static long serialVersionUID = 1;
	protected final static String[] GENERIC_RESOURCE_TYPES = {
		"solar_resources",
		"chemical_resources",
		"flora_resources",
		"gas_resources",
		"mineral_resources",
		"moisture_resources",
		"wind_resources"
	};
	
	public final static String[] SURVEY_SAMPLE_CLIENT_EFFECTS = {
		"clienteffect/survey_sample_mineral.cef",
		"clienteffect/survey_sample_lumber.cef",
		"clienteffect/survey_sample_moisture.cef",
		"clienteffect/survey_sample_liquid.cef",
		"clienteffect/survey_sample_gas.cef",
	};

	public final static String[] SURVEY_TOOL_CLIENT_EFFECTS = {
		"clienteffect/survey_tool_mineral.cef",
		"clienteffect/survey_tool_lumber.cef",
		"clienteffect/survey_tool_moisture.cef",
		"clienteffect/survey_tool_liquid.cef",
		"clienteffect/survey_tool_gas.cef",
	};

	public final static byte GENERIC_TYPE_SOLAR = 0;
	public final static byte GENERIC_TYPE_CHEMICAL = 1;
	public final static byte GENERIC_TYPE_FLORA = 2;
	public final static byte GENERIC_TYPE_GAS = 3;
	public final static byte GENERIC_TYPE_MINERAL = 4;
	public final static byte GENERIC_TYPE_MOISTURE = 5;
	public final static byte GENERIC_TYPE_WIND = 6;

	public final static byte SURVEY_EFFECT_MINERAL = 0;
	public final static byte SURVEY_EFFECT_FLORA = 1;
	public final static byte SURVEY_EFFECT_WATER = 2;
	public final static byte SURVEY_EFFECT_CHEMICAL = 3;
	public final static byte SURVEY_EFFECT_GAS = 4;
	
	private String sResourceType;
	private int iGenericResourceIndex = -1;
	private String sName;
	private long lSpawnID;
	private long lDespawnTimeMS;
	private boolean bIsSpawned;
	private short iColdResistance;
	private short iConductivity;
	private short iDecayResistance;
	private short iHeatResistance;
	private short iMalleability;
	private short iShockResistance;
	private short iUnitToughness;
	private short iEntangleResistance;
	private short iPotentialEnergy;
	private short iOverallQuality;
	private short iFlavor;
	private byte iPool;
	private int[] iPlanetIDs;
	private String iffFileName;
	private String stfFileName;
	private int iResourceType;
	
	private Vector<ResourceSpawnCoordinateData> spawnCoordinates;
	private int resourceContainerTemplateID;
	private int drawColor;
	private Vector<Attribute> vResourceAttributes;
	/**
	 * Construct a new, empty, spawned resource.
	 */
	public SpawnedResourceData() {
		iPlanetIDs = new int[8];
		spawnCoordinates = new Vector<ResourceSpawnCoordinateData>();
		for (int i = 0; i < iPlanetIDs.length; i++) {
			iPlanetIDs[i] = -1;
		}
		vResourceAttributes = new Vector<Attribute>();
	}
	
	/**
	 * Constructs a new resource which will take the "spot" of an old resource in the resource table.
	 * @param oldResource -- The resource being despawned.
	 */
	public SpawnedResourceData(SpawnedResourceData oldResource) {
		iPlanetIDs = new int[8];
		byte oldPool = oldResource.getPool();
		switch (oldPool) {
			case 1: {
				
				break;
			}
			case 2: {
				
				break;
			}
			case 3: {
				
				break;
			}
			case 4: {
				
				break;
			}
		}
	}

	protected void addAttribute(Attribute a) {
		vResourceAttributes.add(a);
	}
	
	protected Vector<Attribute> getAttributes() {
		return vResourceAttributes;
	}
	/**
	 * Gets the generic packet type of this resource.  EG:  mineral_resources
	 * @return The generic resource type, or null if the generic resource index is out of range.
	 */
	public String getGenericResourceType() {
		if (iGenericResourceIndex >= 0 && iGenericResourceIndex < GENERIC_RESOURCE_TYPES.length) {
			return GENERIC_RESOURCE_TYPES[iGenericResourceIndex];
		} else {
			return null;
		}
	}
	
	public String getResourceType() {
		return sResourceType;
	}
	
	public void setResourceType(String sType) {
		sResourceType = sType;
	}
	
	private String sResourceClass;
	public void setResourceClass(String sClass) {
		sResourceClass = sClass;
		//addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_CLASS, sClass + " " + getResourceType()));
	}
	
	public String getResourceClass() {
		return sResourceClass;
	}
	
	public String getName() {
		return sName;
	}
	
	public void setName(String sName) {
		this.sName = sName;
		//addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_NAME, sName));
		

	}
	
	
	public long getID() {
		return lSpawnID;
	}
	public void setID(long id) {
		lSpawnID = id;
	}

	/**
	 * @return the iGenericResourceIndex
	 */
	protected int getIGenericResourceIndex() {
		return iGenericResourceIndex;
	}

	/**
	 * @param genericResourceIndex the iGenericResourceIndex to set
	 */
	protected void setGenericResourceIndex(int genericResourceIndex) {
		iGenericResourceIndex = genericResourceIndex;
	}

	/**
	 * @return the lDespawnTimeMS
	 */
	protected long getLDespawnTimeMS() {
		return lDespawnTimeMS;
	}

	/**
	 * @param despawnTimeMS the lDespawnTimeMS to set
	 */
	protected void setLDespawnTimeMS(long despawnTimeMS) {
		lDespawnTimeMS = despawnTimeMS;
		setIsSpawned(true);
	}

	/**
	 * @return the bIsSpawned
	 */
	protected boolean isSpawned() {
		return bIsSpawned;
	}

	/**
	 * @param isSpawned the bIsSpawned to set
	 */
	protected void setIsSpawned(boolean isSpawned) {
		bIsSpawned = isSpawned;
	}

	/**
	 * @return the iColdResistance
	 */
	protected short getColdResistance() {
		return iColdResistance;
	}

	/**
	 * @param coldResistance the iColdResistance to set
	 */
	protected void setColdResistance(short coldResistance) {
		iColdResistance = coldResistance;
		if (iColdResistance != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_COLD_RESIST, String.valueOf(coldResistance)));
		}
	}

	/**
	 * @return the iConductivity
	 */
	protected short getConductivity() {
		return iConductivity;
	}

	/**
	 * @param conductivity the iConductivity to set
	 */
	protected void setConductivity(short conductivity) {
		iConductivity = conductivity;
		if (iConductivity != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_CONDUCTIVITY, String.valueOf(conductivity)));
		}
	}

	/**
	 * @return the iDecayResistance
	 */
	protected short getDecayResistance() {
		return iDecayResistance;
	}

	/**
	 * @param decayResistance the iDecayResistance to set
	 */
	protected void setDecayResistance(short decayResistance) {
		iDecayResistance = decayResistance;
		if (iDecayResistance != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_DECAY_RESIST, String.valueOf(decayResistance)));
		}
	}

	/**
	 * @return the iHeatResistance
	 */
	protected short getHeatResistance() {
		return iHeatResistance;
	}

	/**
	 * @param heatResistance the iHeatResistance to set
	 */
	protected void setHeatResistance(short heatResistance) {
		iHeatResistance = heatResistance;
		if (iHeatResistance != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_HEAT_RESIST, String.valueOf(heatResistance)));
		}
	}

	/**
	 * @return the iMalleability
	 */
	protected short getMalleability() {
		return iMalleability;
	}

	/**
	 * @param malleability the iMalleability to set
	 */
	protected void setMalleability(short malleability) {
		iMalleability = malleability;
		if (iMalleability != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_MALLEABILITY, String.valueOf(malleability)));
		}

	}

	/**
	 * @return the iShockResistance
	 */
	protected short getShockResistance() {
		return iShockResistance;
	}

	/**
	 * @param shockResistance the iShockResistance to set
	 */
	protected void setShockResistance(short shockResistance) {
		iShockResistance = shockResistance;
		if (iShockResistance != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE, String.valueOf(shockResistance)));
		}

	}

	/**
	 * @return the iUnitToughness
	 */
	protected short getUnitToughness() {
		return iUnitToughness;
	}

	/**
	 * @param unitToughness the iUnitToughness to set
	 */
	protected void setUnitToughness(short unitToughness) {
		iUnitToughness = unitToughness;
		if (iUnitToughness != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_TOUGHNESS, String.valueOf(unitToughness)));
		}

	}

	/**
	 * @return the iEntangieResistance
	 */
	protected short getEntangleResistance() {
		return iEntangleResistance;
	}

	/**
	 * @param entangleResistance the iEntangieResistance to set
	 */
	protected void setEntangieResistance(short entangleResistance) {
		iEntangleResistance = entangleResistance;
		if (iEntangleResistance != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_ENTANGLE_RESISTANCE, String.valueOf(entangleResistance)));
		}

	}

	/**
	 * @return the iPotentialEnergy
	 */
	protected short getPotentialEnergy() {
		return iPotentialEnergy;
	}

	/**
	 * @param potentialEnergy the iPotentialEnergy to set
	 */
	protected void setPotentialEnergy(short potentialEnergy) {
		iPotentialEnergy = potentialEnergy;
		if (iPotentialEnergy != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY, String.valueOf(potentialEnergy)));
		}
	}

	/**
	 * @return the iOverallQuality
	 */
	protected short getOverallQuality() {
		return iOverallQuality;
	}

	/**
	 * @param overallQuality the iOverallQuality to set
	 */
	protected void setOverallQuality(short overallQuality) {
		iOverallQuality = overallQuality;
		if (iOverallQuality != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_QUALITY, String.valueOf(overallQuality)));
		}

	}

	/**
	 * @return the iFlavor
	 */
	protected short getFlavor() {
		return iFlavor;
	}

	/**
	 * @param flavor the iFlavor to set
	 */
	protected void setFlavor(short flavor) {
		iFlavor = flavor;
		if (iFlavor != 0) {
			addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_FLAVOR, String.valueOf(flavor)));
		}

	}
	
	protected void setPool(int iPool) {
		this.iPool = (byte)iPool;
	}
	
	protected byte getPool() {
		return iPool;
	}

	protected void setCap(int index, short value) {
		switch (index) {
			case Constants.RESOURCE_CAP_COLD_RESIST: {
				setColdResistance(value);
				break;
			}
			case Constants.RESOURCE_CAP_CONDUCTIVITY: {
				setConductivity(value);
				break;
			}
			case Constants.RESOURCE_CAP_DECAY_RESIST: {
				setDecayResistance(value);
				break;
			}
			case Constants.RESOURCE_CAP_ENTANGLE_RESIST: {
				setEntangieResistance(value);
				break;
			}
			case Constants.RESOURCE_CAP_FLAVOR: {
				setFlavor(value);
				break;
			}
			case Constants.RESOURCE_CAP_HEAT_RESIST: {
				setHeatResistance(value);
				break;
			}
			case Constants.RESOURCE_CAP_MALLEABILITY: {
				setMalleability(value);
				break;
			}
			case Constants.RESOURCE_CAP_OVERALL_QUALITY: {
				setOverallQuality(value);
				break;
			}
			case Constants.RESOURCE_CAP_POTENTIAL_ENERGY: {
				setPotentialEnergy(value);
				break;
			}
			case Constants.RESOURCE_CAP_SHOCK_RESIST: {
				setShockResistance(value);
				break;
			}
			case Constants.RESOURCE_CAP_UNIT_TOUGHNESS: {
				setUnitToughness(value);
				break;
			}
			default: {
				String sMessage = "Error:  Unknown resource cap attempted set: " + index;
				if (sResourceType != null) {
					sMessage = sMessage + " for resource type " + sResourceType;
				}
				if (sName != null) {
					sMessage = sMessage + ", resource name: " + sName;
				}
				throw new ArrayIndexOutOfBoundsException(sMessage);
			}
			
		}
	}
	
	protected void setPlanetID(int index, int planetID) {
		iPlanetIDs[index] = planetID;
	}
	
	protected int getPlanetID(){
		return iPlanetIDs[0];
	}
	
	protected int[] getAllSpawnedPlanets() {
		return iPlanetIDs;
	}
	
	protected boolean getIsSpawnedOnPlanet(int iPlanetID) {
		for (int i = 0; i < iPlanetIDs.length; i++) {
			if (iPlanetIDs[i] == iPlanetID) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the iffFileName
	 */
	protected String getIffFileName() {
		return iffFileName;
	}

	/**
	 * @param iffFileName the iffFileName to set
	 */
	protected void setIffFileName(String iffFileName) {
		this.iffFileName = iffFileName;
	}

	/**
	 * @return the stfFileName
	 */
	protected String getStfFileName() {
		return stfFileName;
	}

	/**
	 * @param stfFileName the stfFileName to set
	 */
	protected void setStfFileName(String stfFileName) {
		this.stfFileName = stfFileName;
	}
	
	protected void setType(int iType) {
		iResourceType = iType;
	}
	
	protected int getType() {
		return iResourceType;
	}
	
	protected Vector<Float> getDensitiesForSurveyToolUsage(float posX, float posY, float surveyRadius, float distanceIncrement, int numPoints) {
		Vector<Float> vDensitiesAndLocationsToReturn = new Vector<Float>();
		
		float leftX = posX - (surveyRadius / 2);
		//float rightX = posX + (surveyRadius / 2);
		float topY = posY - (surveyRadius / 2);
		//float bottomY = posY + (surveyRadius / 2);
		float currentX = leftX;
		float currentY = topY;
		int iDivider = 0;
		if (surveyRadius <= 64.0f) {
			iDivider = 3;
		} else if (surveyRadius <= 192.0f) {
			iDivider = 4;
		} else {
			iDivider = 5;
		}
		for (int Y = 0; Y < iDivider; Y++) {
			for (int X = 0; X < iDivider; X++) {
				vDensitiesAndLocationsToReturn.add(currentX);
				vDensitiesAndLocationsToReturn.add(currentY);
				vDensitiesAndLocationsToReturn.add(getBestDensityAtLocation(currentX, currentY));
				currentY += distanceIncrement;
			}
			currentY = topY;
			currentX += distanceIncrement;
		}
		
		return vDensitiesAndLocationsToReturn;
	}
	
	protected float getBestDensityAtLocation(float posX, float posY) {
		float toReturn = 0;
		for (int i = 0; i < spawnCoordinates.size(); i++) {
			ResourceSpawnCoordinateData coords = spawnCoordinates.elementAt(i);
			int spawnPosX = (int)coords.getSpawnX();
			int spawnPosY = (int)coords.getSpawnY();
			int spawnRadius = (int)coords.getSpawnRadius();
			float spawnDensityHere = 0; 
			float distanceX = (posX - spawnPosX);
			float distanceY = (posY - spawnPosY);
			float distanceXSquared = distanceX * distanceX;
			float distanceYSquared = distanceY * distanceY;
			double actualDistance = Math.sqrt(distanceXSquared + distanceYSquared);
			if (actualDistance < spawnRadius) {
				// We can see this resource!
				double densityPercentage = 1.0f - (actualDistance / spawnRadius);
				spawnDensityHere = (float)(coords.getSpawnDensity() * densityPercentage);
				
				if (spawnDensityHere > toReturn) {
					toReturn = spawnDensityHere;
				}
			}
			
		}
		if (toReturn <= 10.0f) {
			return 0;
		}
		return toReturn;
	}
	
	protected void setResourceContainerTemplateID(int id) {
		resourceContainerTemplateID = id;
	}
	
	protected int getResourceContainerTemplateID() {
		return resourceContainerTemplateID;
	}
	
	protected void setDrawColor(int color) {
		drawColor = color;
	}
	
	protected int getDrawColor() {
		return drawColor;
	}
	
	protected float getDrawX(int index) {
		ResourceSpawnCoordinateData data = spawnCoordinates.elementAt(index);
		return data.getSpawnX() - (data.getSpawnRadius()/ 2);
	}
	
	protected float getDrawY(int index) {
		ResourceSpawnCoordinateData data = spawnCoordinates.elementAt(index);
		return data.getSpawnY() - (data.getSpawnRadius()/ 2);
	}
	
	protected Vector<ResourceSpawnCoordinateData> getCoordinates() {
		return spawnCoordinates;
	}
	protected void addCoordinates(ResourceSpawnCoordinateData coordinates) {
		spawnCoordinates.add(coordinates);
	}
	
	protected Waypoint getNearestSpawnToCoordinates(float x, float y) {
		double lastDistance = Integer.MAX_VALUE;
		float wayX = 0;
		float wayY = 0;
		for (int i = 0; i < spawnCoordinates.size(); i++) {
			ResourceSpawnCoordinateData coords = spawnCoordinates.elementAt(i);
			int spawnPosX = (int)coords.getSpawnX();
			int spawnPosY = (int)coords.getSpawnY();
			float distanceX = (x - spawnPosX);
			float distanceY = (y - spawnPosY);
			float distanceXSquared = distanceX * distanceX;
			float distanceYSquared = distanceY * distanceY;
			double actualDistance = Math.sqrt(distanceXSquared + distanceYSquared);
			if (actualDistance < lastDistance) {
				lastDistance = actualDistance;
				wayX = spawnPosX;
				wayY = spawnPosY;
			}
		}
		Waypoint waypoint = new Waypoint();
		waypoint.setX(wayX);
		waypoint.setY(wayY);
		waypoint.setName("Resource location");
		return waypoint;
	}
}


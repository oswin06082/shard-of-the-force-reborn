/**
 * The ResourceTemplateData class contains all data on the spawning rules for all types of resources.  
 * @author Darryl
 *
 */
public class ResourceTemplateData {
	private String sResourceClass; // The "class name" of this specific resource.  IE:  Aluminum
	private String sResourceType; // The "type name" of this specific resource.  IE:  Duralumin
	private String sIFFName; // The "packet name" of this resource.  IE:  aluminum_duralumin
	private String sTemplateName; // The "root class" of this resource.  IE:  aluminum
	private short[][] iCaps; // The upper and lower caps of the stats of the resource.
	private long lMinDespawnTimeMS; // The minimum length of time the resource can be spawned.
	private long lMaxDespawnTimeMS; // The maximum length of time the resource can be spawned.
	private int iTemplateID; // The item_template ID for any resource of this type.
	private int iTypeID;
	
	/**
	 * Constructs a new ResourceTemplateData container.
	 */
	public ResourceTemplateData() {
		sResourceClass = null;
		sResourceType = null;
		sIFFName = null;
		sTemplateName = null;
		iCaps = new short[Constants.RESOURCE_QUALITY_COUNT][Constants.RESOURCE_CAPS];
	}
	
	/**
	 * Sets a cap for a given resource quality modifier.
	 * @param index -- The resource quality index.
	 * @param value -- The value of this cap.
	 * @param isMaxCap -- Is this the lower cap, or the upper cap?
	 */
	public void setCap(int index, short value, boolean isMaxCap) {
		if (isMaxCap) {
			iCaps[index][1] = value;
		} else {
			iCaps[index][0] = value;
		}
	}
	
	/**
	 * Gets the minimum and maximum caps for a specific quality modifier for this resource type.
	 * @param index -- The quality modifier.
	 * @return The caps for the quality modifier.
	 */
	public short[] getCaps(int index) {
		return iCaps[index];
	}
	
	/**
	 * Gets a specific quality modifier cap for his resource type.
	 * @param index The quality modifier.
	 * @param isMaxCap -- Indicates if we want the upper cap or the lower cap.
	 * @return The cap.
	 */
	public short getCap(int index, boolean isMaxCap) {
		if (isMaxCap) {
			return iCaps[index][1];
		} else {
			return iCaps[index][0];
		}
	}
	
	/**
	 * Gets all the quality modifier caps for this resource type.
	 * @return The quality modifier caps.
	 */
	public short[][] getAllCaps() {
		return iCaps;
	}
	
	/**
	 * Gets the range between the minimum and maximum for the given cap.
	 * @param index -- The caps being searched.
	 * @return The range between the caps.
	 */
	public short getCapRange(int index) {
		return (short)(iCaps[index][1] - iCaps[index][0]);
	}
	
	/**
	 * Sets the resource class name.  EG:  Duralumin.
	 * @param sName -- The class name of this resource.
	 */
	public void setResourceClassName(String sName) {
		sResourceClass = sName;
	}
	
	/**
	 * Sets the resource type name.  EG:  Aluminum.
	 * @param sName The type name of this resource.
	 */
	public void setResourceTypeName(String sName) {
		sResourceType = sName;
	}
	
	/**
	 * Sets the "template" name of this resource.  This data is needed for building the resource packet.  EG:  aluminum_duralumin
	 * @param sName -- The resource template/packet name.
	 */
	public void setResourceTemplateName(String sName) {
		sTemplateName = sName;
	}
	
	/**
	 * Sets the root type of this resource.  EG:  aluminum
	 * @param sName -- The root resource type.
	 */
	public void setResourceRootName(String sName) {
		sIFFName = sName;
	}
	
	/**
	 * Gets the resource Class.
	 * @return The resource Class.
	 */
	public String getResourceClass() {
		return sResourceClass;
	}
	
	/**
	 * Gets the resource Type.
	 * @return The resource Type.
	 */
	public String getResourceType() {
		return sResourceType;
	}
	
	/**
	 * Gets the Resource Template Name.
	 * @return The resource template name.
	 */
	public String getResourceTemplateName() {
		return sTemplateName;
	}
	
	/**
	 * Gets the base resource type name.
	 * @return The resource type name.
	 */
	public String getResourceRootName() {
		return sIFFName;
	}

	/**
	 * Sets the minimum amount of time, in milliseconds, resources of this type can stay spawned.
	 * @param lTimerMS -- The timer.
	 */
	public void setMinDespawnTimerMS(long lTimerMS) {
		lMinDespawnTimeMS = lTimerMS;
	}
	
	/**
	 * Gets the minimum amount of time, in milliseconds, resource of this type can stay spawned.
	 * @return The timer.
	 */
	public long getMinDespawnTimerMS() {
		return lMinDespawnTimeMS;
	}
	
	
	/**
	 * Sets the maximum amount of time, in milliseconds, resources of this type can stay spawned.
	 * @param lTimerMS -- The timer.
	 */
	public void setMaxDespawnTimerMS(long lTimerMS) {
		lMaxDespawnTimeMS = lTimerMS;
	}
	
	/**
	 * Gets the maximum amount of time, in milliseconds, resources of this type can stay spawned.
	 * @return The timer.
	 */
	public long getMaxDespawnTimerMS() {
		return lMaxDespawnTimeMS;
	}
	
	/**
	 * Gets the range of time, in milliseconds, resource of this type can stay spawned. 
	 * Convenience function for generating a despawn timer on a resource.
	 * @return The range of times that this resource type can be spawned for.
	 */
	public long getDespawnTimerRangeMS() {
		return ((lMaxDespawnTimeMS - lMinDespawnTimeMS) + 1);
	}

	/**
	 * Gets the template ID for any resource created of this type.
	 * @return The template ID.
	 */
	public int getTemplateID() {
		return iTemplateID;
	}

	/**
	 * Sets the template ID for any resource object created of this type.
	 * @param templateID -- The template ID.
	 */
	public void setTemplateID(int templateID) {
		iTemplateID = templateID;
	}
	
	public void setResourceTypeID(int id) {
		iTypeID = id;
	}
	
	public int getResourceTypeID() {
		return iTypeID;
	}
}

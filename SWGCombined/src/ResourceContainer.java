import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The Resource Container is an extension of a Tangible Item that holds data specific to Resources.
 * @author Darryl
 *
 	*/
public class ResourceContainer extends TangibleItem {
	public final static long serialVersionUID = 1;
	private long lResourceSpawnID;
	private String resourceType;
    private transient short iContainerHopperUpdateCount;
	
	/**
	 * Constructs a new Resource Container, and puts a quantity of 1 into it's size.
	 */
	public ResourceContainer() {
		super();
	}
	
	/**
	 * Constructs a new Resource Container with the properties of the ResourceContainer provided, with the provided size.
	 * @param resourceToCopy -- The resource container this object is to mimic.
	 * @param iStackSize -- The quantity of this resource.
	 */
	public ResourceContainer(ResourceContainer resourceToCopy, int iStackSize) {
		TangibleItem inventoryItem = (TangibleItem)resourceToCopy.getContainer();
		setEquipped(inventoryItem, -1);
		Player p = (Player)inventoryItem.getContainer();
		ZoneClient client = p.getClient();
		ZoneServer server = client.getServer();
		long lNewID = server.getNextObjectID();
		setID(lNewID);
		setTemplateID(inventoryItem.getTemplateID());
		setIFFFileName(resourceToCopy.getIFFFileName());
		try {
			setName(resourceToCopy.getName(), false);
		} catch (Exception e) {
			
		}
		setResourceSpawnID(resourceToCopy.getResourceSpawnID());
		try {
			setStackQuantity(iStackSize, false); // Don't want to generate the baseline update this time, as this is a "new" resource container.
		} catch (IOException e) {
			// Can't happen here
		}
		resourceType = resourceToCopy.getResourceType();
		Hashtable<Integer, Attribute> vCopiedAttributes = resourceToCopy.getAttributeList(null);
		Enumeration<Attribute> vCopiedAttributesItr = vCopiedAttributes.elements();
		while (vCopiedAttributesItr.hasMoreElements()) {
			Attribute a = vCopiedAttributesItr.nextElement();
			
			if (a.getAttributeName().equals(Constants.OBJECT_ATTRIBUTE_RESOURCE_CONTENTS)) {
				//addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_VOLUME, String.valueOf(iStackQuantity) + "/" + String.valueOf(MAX_STACK_SIZE)));
                addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_VOLUME, "1"));
			} else {
				addAttribute(new Attribute(a.getAttributeIndex(), a.getAttributeValue()));
			}
		}
		try {
			p.spawnItem(this);
		} catch (Exception e) {
			System.out.println("Error spawning new Resource Container for player " + p.getFirstName());
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

    public ResourceContainer(ResourceContainer resourceToCopy, int iStackSize,TangibleItem container) {
		// TODO:  Copy attributes from the resourceToCopy
		TangibleItem inventoryItem = container;
		setEquipped(inventoryItem, -1);
		Player p = (Player)inventoryItem.getContainer();
		ZoneClient client = p.getClient();
		ZoneServer server = client.getServer();
		long lNewID = server.getNextObjectID();
                setTemplateID(inventoryItem.getTemplateID());
		setID(lNewID);
		//setSTFFileName(resourceToCopy.getSTFFileName());
		//setSTFFileIdentifier(resourceToCopy.getSTFFileIdentifier());
		setIFFFileName(resourceToCopy.getIFFFileName());
		setResourceSpawnID(resourceToCopy.getResourceSpawnID());
		try {
			setName(resourceToCopy.getName(), false);
			setStackQuantity(iStackSize, false); // Don't want to generate the baseline update this time, as this is a "new" resource container.
		} catch (Exception e) {
			// D'oph!
		}
		resourceType = resourceToCopy.getResourceType();
		Hashtable<Integer, Attribute> vCopiedAttributes = resourceToCopy.getAttributeList(null);
		Enumeration<Attribute> vCopiedAttributesItr = vCopiedAttributes.elements();
		while (vCopiedAttributesItr.hasMoreElements()) {
			Attribute a = vCopiedAttributesItr.nextElement();
			
			if (a.getAttributeName().equals(Constants.OBJECT_ATTRIBUTE_RESOURCE_CONTENTS)) {
				//addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_RES_VOLUME, String.valueOf(iStackQuantity) + "/" + String.valueOf(MAX_STACK_SIZE)));
                addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_VOLUME, "1"));
			} else {
				addAttribute(new Attribute(a.getAttributeIndex(), a.getAttributeValue()));
			}
		}
		try {
			p.spawnItem(this);
		} catch (Exception e) {
			System.out.println("Error spawning new Resource Container for player " + p.getFirstName());
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	public void setQuantityAttribute(int iNewQuantity) {
		Attribute volume = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_RESOURCE_CONTENTS);
		if (volume == null) {
			volume = new Attribute(Constants.OBJECT_ATTRIBUTE_RESOURCE_CONTENTS, (String.valueOf(iNewQuantity) + "/" + String.valueOf(Constants.MAX_STACK_SIZE)));
			addAttribute(volume);
		} else {
			volume.setAttributeValue(String.valueOf(iNewQuantity) + "/" + String.valueOf(Constants.MAX_STACK_SIZE));
		}
	}
	
	protected void setResourceSpawnID(long lSpawnID) {
		lResourceSpawnID = lSpawnID;
		setSerialNumber(lSpawnID, false);
	}
	
	protected long getResourceSpawnID() {
		return lResourceSpawnID;
	}

	protected String getResourceType() {
		return resourceType;
	}
	
	protected void setResourceType(String sType) {
		resourceType = sType;
	}

    public short getIContainerHopperUpdateCount() {
        iContainerHopperUpdateCount++;
        return iContainerHopperUpdateCount;
    }
    
	/**
	 * Sets the size of this resource.
	 * @param iNewQuantity
	 */
	public byte[] setStackQuantity(int iNewQuantity, boolean bUpdate) throws IOException {
		super.setStackQuantity(iNewQuantity, false);
		setQuantityAttribute(iNewQuantity);
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_RCNO, (byte)3, (short)1, (short)0x0b, this, getStackQuantity());
		}
		return null;
	}

	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Can't experiment on a ResourceContainer -- just return.
		return 0;
	}
	

}

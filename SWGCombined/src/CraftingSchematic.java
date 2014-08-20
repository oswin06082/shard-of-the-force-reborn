import java.io.Serializable;
import java.util.Vector;

/**
 * The CraftingSchematic class contains the CRC, ID, etc. of any Crafting Schematic which the Player knows.
 * @author Darryl
 *
 */

public class CraftingSchematic implements Serializable {
	public final static long serialVersionUID = 1l;
	private int iCRC;
	private int index;
	private int iSchematicType;
	private Vector<Integer> iRequiredSkillID; // Need to support 2 (or more) skills here -- if the player gains ANY skill, they can have this schematic.
	private int iExperienceGainedFromCrafting;
	private int iExperienceTypeToGrant;
	private int iComplexity;
	private int iToolTabBitmask;
	private Vector<CraftingSchematicComponent> vComponents;
	private int iCraftedItemType;
	private CraftingExperimentationAttribute[] vAttributes;
	private String sCraftedItemIFFFilename;
	private TangibleItem itemToCraft;
	private Vector<TangibleItem>[] vFactoryItemsUsedToCraftCrate;
	
	
	/**
	 * Constructs a new Crafting Schematic.
	 */
	public CraftingSchematic() {
		vComponents = new Vector<CraftingSchematicComponent>();
        iToolTabBitmask = 1;
        iRequiredSkillID = new Vector<Integer>();
        
	}

	/**
	 * Gets the CRC of this crafting schematic.
	 * @return -- The CRC of this schematic.
	 */
	public int getCRC() {
		return iCRC;
	}
	
	/**
	 * Gets the ID of this crafting schematic.
	 * @return The ID of this schematic.
	 */
	public int getIndex() {
		return index;
	}
	
	public int getType() {
		return iSchematicType;
	}
	
	public Vector<Integer> getRequiredSkillID() {
		return iRequiredSkillID;
	}

	protected int getExperienceGainedFromCrafting() {
		return iExperienceGainedFromCrafting;
	}

	protected void setExperienceGainedFromCrafting(int experienceGainedFromCrafting) {
		iExperienceGainedFromCrafting = experienceGainedFromCrafting;
	}

	protected int getExperienceTypeToGrant() {
		return iExperienceTypeToGrant;
	}

	protected void setExperienceTypeToGrant(int experienceTypeToGrant) {
		iExperienceTypeToGrant = experienceTypeToGrant;
	}

	protected void setCRC(int icrc) {
		iCRC = icrc;
	}

	protected void addRequiredSkillID(int requiredSkillID) {
		iRequiredSkillID.add(requiredSkillID);
	}

	protected void setSchematicType(int schematicType) {
		iSchematicType = schematicType;
	}

	protected void setIndex(int objectID) {
		this.index = objectID;
	}
	
	protected void addComponent(CraftingSchematicComponent comp) {
		vComponents.add(comp);
	}
	
	protected Vector<CraftingSchematicComponent> getComponents() {
		return vComponents;
	}
	protected void setComplexity(int i) {
		iComplexity = i;
	}
	
	protected int getComplexity() {
		return iComplexity;
	}

	protected void setNumAttributes(int numAttributes) {
		vAttributes = new CraftingExperimentationAttribute[numAttributes];
	}
	
	protected void addAttribute(int index, CraftingExperimentationAttribute attr) {
		vAttributes[index] = attr;
	}
	
	protected CraftingExperimentationAttribute[] getAttributes() {
		return vAttributes;
	}

	public void setCraftedItemIFFFilename(String sCraftedItemIFFFilename) {
		this.sCraftedItemIFFFilename = sCraftedItemIFFFilename;
	}

	public String getCraftedItemIFFFilename() {
		return sCraftedItemIFFFilename;
	}

    public int getIToolTabBitmask() {
        return iToolTabBitmask;
    }

    public void setIToolTabBitmask(int iToolTabBitmask) {
        this.iToolTabBitmask = iToolTabBitmask;
    }

    public ManufacturingSchematic createManufacturingSchematic() {
    	ItemTemplate genericSchematicTemplate = DatabaseInterface.getTemplateDataByFilename("object/manufacture_schematic/shared_generic_schematic.iff");
    	ManufacturingSchematic manuSchematic = null;
    	if (genericSchematicTemplate != null) {
    		manuSchematic = new ManufacturingSchematic(genericSchematicTemplate);
        	manuSchematic.setBaseCraftingComplexity(getComplexity());
        	manuSchematic.setCraftingSchematic(this);
        	return manuSchematic;
    	} else {
    		
    		return null;
    	}
    }
    
    // TODO:  Update based on crafted item type.  Food items would be a new FoodItem();  Medical items would be a new MedicalItem
    // Weapons would be a new Weapon
    // Armor would be a new Armor
    // Lightsabers would be a new Lightsaber.
    // Etc etc
    public TangibleItem getItemToCraft() {
    	switch (iCraftedItemType) {
        	default: {
	    		itemToCraft = new TangibleItem();
	    	}
    	}    	
    	
    	
    	
    	
    	return itemToCraft;
    }

	public void setCraftedItemType(int iCraftedItemType) {
		this.iCraftedItemType = iCraftedItemType;
	}

	public int getCraftedItemType() {
		return iCraftedItemType;
	}
	
	public void addFactoryItemForCrafting(TangibleItem item, int slotID) {
		if (vFactoryItemsUsedToCraftCrate == null) {
			vFactoryItemsUsedToCraftCrate = new Vector[vComponents.size()];
		}
		vFactoryItemsUsedToCraftCrate[slotID].add(item);
	}
	
	/**
	 * Reduces the amount of materials in the linked resource containers / factory crates by the amount needed to craft one object of this schematic's type. 
	 */
	public void reduceFactoryItemsForCrafting() { 
		
		for (int i = 0; i < vFactoryItemsUsedToCraftCrate.length; i++) {
			int numToReduce = vComponents.elementAt(i).getComponentQuantity();
			int iReducedBy = 0;
			for (int j = 0; j < vFactoryItemsUsedToCraftCrate[i].size() && iReducedBy < numToReduce; j++) {
				TangibleItem item = vFactoryItemsUsedToCraftCrate[i].elementAt(j);
				int itemStackSize = item.getStackQuantity();
				iReducedBy += itemStackSize;
				if (itemStackSize <= numToReduce) {
					vFactoryItemsUsedToCraftCrate[i].remove(item);
				}  else {
					// Don't.
				}
			}
		}
	}
	
	public void clearFactoryItemForCrafting() {
		if (vFactoryItemsUsedToCraftCrate == null) {
			vFactoryItemsUsedToCraftCrate = new Vector[vComponents.size()];
			for (int i = 0; i < vFactoryItemsUsedToCraftCrate.length; i++) {
				vFactoryItemsUsedToCraftCrate[i] = new Vector<TangibleItem>();
			}
		} else {
			for (int i = 0; i < vFactoryItemsUsedToCraftCrate.length; i++) {
				vFactoryItemsUsedToCraftCrate[i].clear();
			}
		}
	}
}

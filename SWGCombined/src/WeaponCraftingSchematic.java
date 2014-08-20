import java.util.Hashtable;

public class WeaponCraftingSchematic extends CraftingSchematic {
	public final static long serialVersionUID = 1l;
	private int requiredSkillIDForCraftedItem;
	private Hashtable<Byte, Double[]> craftingExperimentationAttributeLimits;
	private int iWeaponType;
	
	
	public WeaponCraftingSchematic() {
		super();
		craftingExperimentationAttributeLimits = new Hashtable<Byte, Double[]>();
	}
	
	public void setCraftedWeaponRequiredSkillID(int skillID) {
		requiredSkillIDForCraftedItem = skillID;
	}
	
	public int getCraftedWeaponRequiredSkillID() {
		return requiredSkillIDForCraftedItem;
	}
	
	public void setCraftingLimit(byte id, Double[] value) {
		craftingExperimentationAttributeLimits.put(id, value);
	}
	
	public Double[] getCraftingLimitByID(byte id) {
		return craftingExperimentationAttributeLimits.get(id);
	}
	
	public Hashtable<Byte, Double[]> getCraftingLimits() {
		return craftingExperimentationAttributeLimits;
	}

	public void setWeaponType(int iWeaponType) {
		this.iWeaponType = iWeaponType;
	}

	public int getWeaponType() {
		return iWeaponType;
	}
	
}

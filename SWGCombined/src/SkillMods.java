import java.io.Serializable;

/**
 * Simple container class for skill mods.
 * @author Darryl
 *
 */
public class SkillMods implements Serializable{
	public final static long serialVersionUID = 1l;
	private String sName;
	private int iSkillModStartingValue;
	private int iSkillModModdedValue;
	//private boolean bUnknownValue;
	
	public String getName() {
		return sName;
	}

	public void setName(String name) {
		sName = name;
	}

	public int getSkillModStartingValue() {
		return iSkillModStartingValue;
	}

	public void setSkillModStartingValue(int skillModStartingValue) {
		iSkillModStartingValue = skillModStartingValue;
	}

	public int getSkillModModdedValue() {
		return Math.min(iSkillModModdedValue, Constants.SKILLMOD_CAP);
	}

	public void setSkillModModdedValue(int skillModModdedValue) {
		iSkillModModdedValue = Math.min(skillModModdedValue, Constants.SKILLMOD_CAP);
	}

	public SkillMods() {
		
	}
	
	
}

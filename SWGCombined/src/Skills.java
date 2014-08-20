import java.util.Vector;

/**
 * Container class for Skills.  A skill has required money costs to learn, required experience values to learn, etc. etc.
 * @author Darryl
 *
 */
public class Skills {
	private final static int MAX_PREREQ_SKILLS = 4;
	private int iSkillID = -1;
	private String sSkillName = null;
	private int[] iRequiredSkillID = null;
	//private String sExperienceName = null;
	private int iSkillCostCredits = -1;
	private int iSkillCostPoints = -1;
	private int iSkillXPUsed = -1;
	private int iSkillCostXP = -1;
	private int iSkillCapXP = -1;
	private int[] iSkillSpeciesSpecificID;
	private int iNumRequiredSkills = 0;
	private Vector<SkillMods> vSkillMods;
	private Vector<String> vCertificationsList;
    //private String sRequisiteSkills;
	private boolean bIsNoviceSkill = false;
	private boolean bIsProfessionSkill = false;
	private boolean bIsGodModeSkill = false;
	private int noviceSkillID;
	
	/**
	 * Constructs a new Skill.
	 */
	public Skills() {
		iRequiredSkillID = new int[MAX_PREREQ_SKILLS];
		vSkillMods = new Vector<SkillMods>();
		vCertificationsList = new Vector<String>();
	}
	
	/**
	 * Sets the skill ID of this skill.
	 * @param i -- The skill ID.
	 */
	public void setSkillID(int i) {
		iSkillID = i;
	}
	/**
	 * Sets the name of this skill, for writing it to the packets.
	 * @param s -- The skill name.
	 */
	public void setName(String s) {
		sSkillName = s;
		setIsNoviceSkill(s.contains("novice"));
	}
	
	/**
	 * Adds a required prerequisite skill ID to this skill.  Skills may not be learned unless the Player has all the
	 * prerequisite skills.
	 * @param i -- The prerequisite skill ID.
	 */
	public void addRequiredSkillID(int i) {
		if (iNumRequiredSkills > 3) {
			return;
		}
		iRequiredSkillID[iNumRequiredSkills] = i;
		iNumRequiredSkills++;
	}
	
	/**
	 * Sets the amount of money it takes to learn this skill from an NPC skill trainer.
	 * @param i -- The cost of this skill.
	 */
	public void setCreditsCost(int i) {
		iSkillCostCredits = i;
	}
	
	/**
	 * Sets the number of skill points this skill will take up.  Players may not learn any given skill if they do not
	 * have enough skill points left.
	 * @param i -- The points cost of this skill.
	 */
	public void setPointsCost(int i) {
		iSkillCostPoints = i;
	}
	
	/**
	 * Sets the amount of Experience points this skill requires to learn.
	 * @param i -- The experience cost.
	 */
	public void setExperienceCost(int i) {
		iSkillCostXP = i;
	}
	
	/**
	 * Sets the type of Experience this skill requires, by ID.
	 * @param i -- The Experience ID.
	 */
	public void setExperienceType(int i) {
		iSkillXPUsed = i;
	}
	
	/**
	 * Sets the maximum amount of Experience of the type this skill requires to the specified value.
	 * @param i -- The new Experience cap.
	 */
	public void setExperienceCap(int i) {
		if (i!=0) {
			iSkillCapXP = i;
		} else {
			setExperienceCap();
		}
	}
	
	/**
	 * Sets the maximum amount of Experience of the type this skill requires, to be double the amount of experience needed to learn this skill.
	 */
	public void setExperienceCap() {
		iSkillCapXP = iSkillCostXP * 2;
	}
	
	/**
	 * Sets whether this skill is a species specific skill.
	 * For example, Shyriiwook Speech is learnable only by Wookiees.
	 * @param iRaceID -- The Race index which can learn this skill.
	 */
	public void setSpeciesSpecific(int[] iRaceID) {
		iSkillSpeciesSpecificID = iRaceID;
	}

	/**
	 * Gets this Skill's index/ID.
	 * @return The ID.
	 */
	public int getSkillID() {
		return iSkillID;
	}
	
	/**
	 * Gets this Skill's packet name parameter.
	 * @return The name.
	 */
	public String getName() {
		return sSkillName;
	}
	
	/**
	 * Gets this Skill's prerequisite Skill Indices.
	 * @return The prerequisite skill ids.
	 */
	public int[] getRequiredSkillIDs() {
		return iRequiredSkillID;
	}
	
	/**
	 * Gets the cost of learning this Skill from an NPC trainer.
	 * @return The skill credits cost.
	 */
	public int getCreditsCost() {
		return iSkillCostCredits;
	}
	
	/**
	 * Gets the cost in skill points of learning this Skill.
	 * @return The skill points cost.
	 */
	public int getPointsCost() {
		return iSkillCostPoints;
	}
	
	/**
	 * Gets the amount of experience the Player must have accrued to learn this skill. 
	 * @return The skill experience cost.
	 */
	public int getExperienceCost() {
		return iSkillCostXP;
	}
	
	/**
	 * Gets the type of experience needed to learn this skill.
	 * @return The skill experience type.
	 */
	public int getExperienceType() {
		return iSkillXPUsed;
	}
	/**
	 * Gets the maximum amount of experience, of this skill's experience type, that may be learned by a Player who 
	 * has this skill.
	 * @return The experience cap.
	 */
	public int getExperienceCap() {
		return iSkillCapXP;
	}
	
	/**
	 * Gets the species requirement for this skill.
	 * @return The species ID required to learn this skill, or -1 if there is no species requirement.
	 */
	public int[] getSpeciesSpecific() {
		return iSkillSpeciesSpecificID;
	}
	
	/**
	 * Adds a Skill Modifier to this Skill.  When a Player learns a skill, they also "learn" all associated skill modifiers.
	 * @param mod -- The Skill Modifier to add.
	 */
	public void addSkillMod(SkillMods mod) {
		vSkillMods.add(mod);
	}
	
	/**
	 * Gets a specific Skill Modifier by name.
	 * @param sMod -- The name of the Skill Modifier being searched for.
	 * @return The Skill Modifier, or null if the Skill Modifier is not granted by this Skill.
	 */
	public SkillMods getSkillMod(String sMod) {
		for (int i =0; i < vSkillMods.size(); i++) {
			SkillMods mod = vSkillMods.elementAt(i);
			if (mod.getName().equalsIgnoreCase(sMod)) {
				return mod;
			}
		}
		return null;
	}
	
	/**
	 * Gets all of the Skill Modifiers granted by this Skill.
	 * @return The Skill Modifiers.
	 */
	public Vector<SkillMods> getAllSkillMods() {
		return vSkillMods;
	}
	
	public void addCertification(String sCertification) {
		vCertificationsList.add(sCertification);
	}
	
	public boolean getIsCertified(String sCertification) {
		return vCertificationsList.contains(sCertification);
	}
	
	public Vector<String> getCertificationList() {
		return vCertificationsList;
	}
        
      /*  public void setRequisiteSkillString(String R){
            sRequisiteSkills = R;
        }
        
        public String getRequisiteSkillString(){
            return sRequisiteSkills;
        }*/
	
	protected void setIsNoviceSkill(boolean b) {
		bIsNoviceSkill = b;
	}
	
	protected boolean getIsNoviceSkill() {
		return bIsNoviceSkill;
	}

	public void setIsProfessionSkill(boolean bIsProfessionSkill) {
		this.bIsProfessionSkill = bIsProfessionSkill;
	}

	public boolean getIsProfessionSkill() {
		return bIsProfessionSkill;
	}

	public void setIsGodModeSkill(boolean bIsGodModeSkill) {
		this.bIsGodModeSkill = bIsGodModeSkill;
	}

	public boolean isGodModeSkill() {
		return bIsGodModeSkill;
	}

	public void setNoviceSkillID(int noviceSkillID) {
		this.noviceSkillID = noviceSkillID;
	}

	public int getNoviceSkillID() {
		return noviceSkillID;
	}
}

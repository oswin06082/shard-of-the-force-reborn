import java.util.Vector;

/**
 * A CombatAction is any action the Server receives from the Client that is classified as being able to alter
 * the HAM, state, or status of a Player, not including voluntary changes of posture.
 * @author Darryl
 *
 */
public class CombatAction {
	private int requiredSkill;
	private int iCombatCRC;
	private boolean bAddRedTrails;
	private int iTargetHam;
	private String sCombatSTFSpam;
	private int iRequiredWeaponType = -1;
	
	private String[] sCombatSTFAttacks;
	private byte iTargetEffectType;
	private int iConeAngle;
	private float fAttackDelayModifier;
	private float fDamageModifier;
	private float fHealthCostModifier;
	private float fActionCostModifier;
	private float fMindCostModifier;
	private float fToHitModifier;
	private float fForceCostModifier;
	private BuffEffect[] combatEffect;
	private Vector<String> sCombatAnimationSTF;
	private Vector<Integer> iCombatAnimationCRC;
	
	
	/**
	 * Construct a new CombatAction with the given required Skill ID
	 * @param iSkill -- The Skill ID needed by this combat action.
	 * @param iCRC -- The Action CRC of this combat action.
	 */
	public CombatAction(int iSkill, int iCRC) {
		requiredSkill = iSkill;
		iCombatCRC = iCRC;
		combatEffect = new BuffEffect[3];
		sCombatSTFAttacks = new String[Constants.NUM_COMBAT_EFFECTS];
	}
	
	/**
	 * Construct a new CombatAction template with no data filled in yet.
	 */
	public CombatAction() {
		combatEffect = new BuffEffect[3];
		sCombatSTFAttacks = new String[Constants.NUM_COMBAT_EFFECTS];
	}

	/**
	 * Gets the skill ID required by this Combat Action
	 * @return The required skill ID.
	 */
	protected int getRequiredSkill() {
		return requiredSkill;
	}
	
	protected void setRequiredSkillID(int id) {
		requiredSkill = id;
	}
	/**
	 * Gets the CRC for this CombatAction
	 * @return The CombatAction CRC
	 */
	protected int getCRC() {
		return iCombatCRC;
	}
	
	protected void setCRC(int iCRC) {
		iCombatCRC = iCRC;
	}
	/**
	 * Gets whether the animation for this CombatAction is to have Red Trails attached to it.
	 * @return If the action should have Red Trails.
	 */
	protected boolean getTrails() {
		return bAddRedTrails;
	}

	public void setTargetHam(int iTargetHam) {
		this.iTargetHam = iTargetHam;
	}

	public int getTargetHam() {
		return iTargetHam;
	}

	public void setCombatSTFSpam(String sCombatSTFSpam) {
		this.sCombatSTFSpam = sCombatSTFSpam;
	}

	public String getCombatSTFSpam() {
		return sCombatSTFSpam;
	}
	
	public String getCombatSTFCounterSpam() {
		return sCombatSTFAttacks[Constants.COMBAT_EFFECT_COUNTER];
	}

	public void setCombatSTFCounterSpam(String combatSTFCounterSpam) {
		sCombatSTFAttacks[Constants.COMBAT_EFFECT_COUNTER] = combatSTFCounterSpam;
	}

	public String getCombatSTFEvadeSpam() {
		return sCombatSTFAttacks[Constants.COMBAT_EFFECT_EVADE];
	}

	public void setCombatSTFEvadeSpam(String combatSTFEvadeSpam) {
		sCombatSTFAttacks[Constants.COMBAT_EFFECT_EVADE] = combatSTFEvadeSpam;
	}

	public String getCombatSTFBlockSpam() {
		return sCombatSTFAttacks[Constants.COMBAT_EFFECT_BLOCK];
	}

	public void setCombatSTFBlockSpam(String combatSTFBlockSpam) {
		sCombatSTFAttacks[Constants.COMBAT_EFFECT_BLOCK] = combatSTFBlockSpam;
	}

	public String getCombatSTFMissSpam() {
		return sCombatSTFAttacks[Constants.COMBAT_EFFECT_MISS];
	}

	public void setCombatSTFMissSpam(String combatSTFMissSpam) {
		sCombatSTFAttacks[Constants.COMBAT_EFFECT_MISS] = combatSTFMissSpam;
	}

	public String getCombatSTFHitSpam() {
		return sCombatSTFAttacks[Constants.COMBAT_EFFECT_HIT];
	}

	public void setCombatSTFHitSpam(String combatSTFHitSpam) {
		sCombatSTFAttacks[Constants.COMBAT_EFFECT_HIT] = combatSTFHitSpam;
	}

	public String[] getCombatSTFSpamArr() {
		return sCombatSTFAttacks;
	}
	public byte getTargetEffectType() {
		return iTargetEffectType;
	}

	public void setTargetEffectType(byte targetEffectType) {
		iTargetEffectType = targetEffectType;
	}

	public int getConeAngle() {
		return iConeAngle;
	}

	public void setConeAngle(int coneAngle) {
		iConeAngle = coneAngle;
	}

	public float getAttackDelayModifier() {
		return fAttackDelayModifier;
	}

	public void setAttackDelayModifier(float attackDelayModifier) {
		fAttackDelayModifier = attackDelayModifier;
	}

	public float getDamageModifier() {
		return fDamageModifier;
	}

	public void setDamageModifier(float damageModifier) {
		fDamageModifier = damageModifier;
	}

	public float getHealthCostModifier() {
		return fHealthCostModifier;
	}

	public void setHealthCostModifier(float healthCostModifier) {
		fHealthCostModifier = healthCostModifier;
	}

	public float getActionCostModifier() {
		return fActionCostModifier;
	}

	public void setActionCostModifier(float actionCostModifier) {
		fActionCostModifier = actionCostModifier;
	}

	public float getMindCostModifier() {
		return fMindCostModifier;
	}

	public void setMindCostModifier(float mindCostModifier) {
		fMindCostModifier = mindCostModifier;
	}

	public float getToHitModifier() {
		return fToHitModifier;
	}

	public void setToHitModifier(float toHitModifier) {
		fToHitModifier = toHitModifier;
	}

	public float getForceCostModifier() {
		return fForceCostModifier;
	}

	public void setForceCostModifier(float forceCostModifier) {
		fForceCostModifier = forceCostModifier;
	}

	public Vector<String> getCombatAnimationSTF() {
		return sCombatAnimationSTF;
	}

	public void setCombatActionSTF(String combatActionSTF) {
		if (sCombatAnimationSTF == null) {
			sCombatAnimationSTF = new Vector<String>();
		}
		if (combatActionSTF == null || combatActionSTF.isEmpty()) {
			
		} else {
			sCombatAnimationSTF.add(combatActionSTF);
			setAnimationCRC(PacketUtils.SWGCrc(combatActionSTF));
		}
	}

	public BuffEffect[] getAllCombatEffects() {
		return combatEffect;
		
	}
	public BuffEffect getCombatEffect(int index) {
		if (combatEffect[index] != null) {
			return combatEffect[index].copy();
		}
		return null;
	}
	
	public void setCombatEffectAtIndex(BuffEffect effect, int index) {
		this.combatEffect[index] = effect;
	}

	public void setAnimationCRC(int iAnimationCRC) {
		if(iCombatAnimationCRC == null) {
			iCombatAnimationCRC = new Vector<Integer>();
		}
		iCombatAnimationCRC.add(iAnimationCRC);	
	}

	public Vector<Integer> getAnimationCRC() {
		return iCombatAnimationCRC;
	}

	public void setRequiredWeaponType(int iRequiredWeaponType) {
		this.iRequiredWeaponType = iRequiredWeaponType;
	}

	public int getRequiredWeaponType() {
		return iRequiredWeaponType;
	}
}

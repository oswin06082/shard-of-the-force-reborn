import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;


/**
 * The Weapon class represents any TangibleItem which is capable of applying damage to a target through combat.
 * Although any weapon can in theory be used by any Player, all weapons have a prerequisite skill that
 * a Player must know, to be used adequately.  (If the weapon is a Lightsaber, it may not be equipped / obtained by a 
 * non-Jedi / non-Force Sensitive Player).
 * @author Darryl
 *
 */
public class Weapon extends TangibleItem{
	public static final long serialVersionUID = 1;
	private final static int DEF_MIN_DAMAGE = 100;
	private final static int DEF_MAX_DAMAGE = 350;
	private final static float DEF_REFIRE_DELAY = 1.0f;
	private int iSkillRequirement = -1;
	private String sWeaponName;
	private int iMinimumDamage = DEF_MIN_DAMAGE;
	private int iMaximumDamage = DEF_MAX_DAMAGE;
	private int iModifiedMinimumDamage = iMinimumDamage;
	private int iModifiedMaximumDamage = iMaximumDamage;
	private int iWoundChance;
	private float fRefireDelay = DEF_REFIRE_DELAY;
	private float fModifiedRefireDelay = fRefireDelay;
    private int iHealthCost = 8;
    private int iActionCost = 8;
    private int iMindCost = 8;
	private boolean bIsPoweredUp = false;
	private boolean bDefaultWeapon = false;
	private int iStackSize;
	private byte iArmorPiercingLevel;
	private int iZeroRangeModDistance;
	private int iMediumRangeModDistance;
	private int iMaxRangeModDistance;
	private int iWeaponType;
	private float fAttackRange = 0;
	private int iNumberOfInventorySlotsTaken = 1;
	
	//private Powerup appliedPowerup;
	/**
	 * Construct a new default weapon.
	 */
	public Weapon() {
		super();
		sWeaponName = "";
	}

	/**
	 * Constructs a new weapon with the given name, and the given STF filename.
	 * @param weaponName -- The custom name of this weapon.
	 * @param weaponSTF -- The STF file for this weapon.
	 */
	public Weapon(String weaponName) {
		super();
		sWeaponName = weaponName;
	}
	
	/**
	 * Gets the name of this specific weapon.
         * @Override
	 * @return The name.
	 */
	public String getName() {
		return sWeaponName;
	}
	
    /**
     * @Override
     * @param Name
     */
    public void setName(String Name){
        sWeaponName = Name;
    }
	/**
	 * Gets the current minimum damage this weapon may attack for.
	 * @return The minimum damage of this weapon.
	 */
	public int getMinDamage() {
		if (bIsPoweredUp) {
			return iModifiedMinimumDamage;
		}
		return iMinimumDamage;
	}
	
	/**
	 * Gets the current maximum damage this weapon may attack for.
	 * @return The maximum damage of this weapon.
	 */
	public int getMaxDamage() {
		if (bIsPoweredUp) {
			return iModifiedMaximumDamage;
		}
		return iMaximumDamage;
	}
	
	/**
	 * Gets the current damage range of this weapon. (Maximum - minimum + 1).
	 * @return The damage range.
	 */
	public int getDamageRange() {
		if (bIsPoweredUp) {
			return ((iModifiedMaximumDamage - iModifiedMinimumDamage) + 1);
		}
		return ((iMaximumDamage - iMinimumDamage) + 1);
	}
	
	/**
	 * Gets the average damage per attack of this weapon.  50% of the attacks would be higher than this number, and 50% would be lower than this number.  
	 * @return The average damage.
	 */
	public int getAverageDamageRound() {
		return (((iMinimumDamage + iMaximumDamage) / 2) + ((iMinimumDamage + iMaximumDamage) % 2));
	}
	
	/**
	 * Gets the skill ID required to effectively use this weapon.
	 * @return The skill ID.
	 */
	public int getSkillRequirement() {
		return iSkillRequirement;
	}
	
	public void setSkillRequirement(int requiredSkillID) {
		iSkillRequirement = requiredSkillID;
	}
	
	public void setRefireDelay(float time) {
		fRefireDelay = time;
	}
	/**
	 * Gets the refire delay on this weapon, in seconds.
	 * @return The refire delay.
	 */
	public float getRefireDelay() {
		if (bIsPoweredUp) {
			return fModifiedRefireDelay;
		}
		return fRefireDelay;
	}
	
	/**
	 * Sets this weapon to be equipped by the given object.
	 * @param o -- The object (de)equipping this weapon.
	 * @param bIsEquipped -- The equipped status of the weapon.
	 */
	protected void setEquipped(SOEObject o, int equippedStatus, boolean updateZone) {
		super.setEquipped(o, equippedStatus);
		if (o instanceof Player) {
			if (equippedStatus == -1) {
				Player p = (Player)o;
				p.equipWeapon(this, updateZone);
			}
		}
	}
        
        protected void setHealthCost(int c){
            iHealthCost = c;
        }
        
        protected int getHealthCost(){
            return iHealthCost;
        }
        
        protected void setActionCost(int c){
            iActionCost = c;
        }
        
        protected int getActionCost(){
            return iActionCost;
        }
        
        protected void setMindCost(int c){
            iMindCost = c;
        }
        
        protected int getMindCost(){
            return iMindCost;
        }

		public void setStackSize(int iStackSize) {
			this.iStackSize = iStackSize;
		}

		public int getStackSize() {
			return iStackSize;
		}
        
	protected void useItemByCommandID(ZoneClient client, byte commandID) {
		switch (commandID) {
			case Constants.RADIAL_MENU_ITEM_EQUIP: {
				// Equip item on the Player.
				// NOTE:  This is handled by handleTransferItemWeapon.
				Player player = client.getPlayer();
				Weapon currentEquippedWeapon = player.getWeapon();
				if (currentEquippedWeapon != null) {
					if (currentEquippedWeapon.equals(this)) {
						// We're trying to equip twice... something's gone wrong.
						DataLog.logEntry("Trying to equip a weapon that is already equipped.", "Weapon::useItemByCommandID", Constants.LOG_SEVERITY_URGENT, true, true);
					} else {
						player.updateEquippedWeapon(this, true);
					}
				}
				
				break;
			}
			case Constants.RADIAL_MENU_ITEM_UNEQUIP: {
				// Note:  This is handled by transferItemMisc
				//System.out.println("Unequip weapon.");
				
				Player player = client.getPlayer();
				Weapon currentEquippedWeapon = player.getWeapon();
				if (currentEquippedWeapon != null) {
					if (currentEquippedWeapon.equals(this)) {
						// It's all good -- unequip it.
						player.updateEquippedWeapon(null, true); // Passing null forces the Player to "equip" his unarmed fists.
					} else {
						DataLog.logEntry("Trying to unequip a weapon that is not currently equipped.", "Weapon::useItemByCommandID", Constants.LOG_SEVERITY_URGENT, true, true);
					}
				}
				
				break;
			}
			default: {
				DataLog.logEntry("Unhandled command ID " + commandID, "Weapon::useItemByCommandID", Constants.LOG_SEVERITY_INFO, true, true);
			}
		}
	}
	
	public void setMinDamage(int damage) {
		iMinimumDamage = damage;
		if (iMinimumDamage >= iMaximumDamage) {
			iMaximumDamage = damage + 1;
		}
	}
	
	public void setMaxDamage(int damage) {
		iMaximumDamage = damage;
		if (iMaximumDamage <= iMinimumDamage) {
			iMinimumDamage = iMaximumDamage - 1;
		}
	}

	public void setIsDefaultWeapon(boolean bDefaultWeapon) {
		this.bDefaultWeapon = bDefaultWeapon;
	}

	public boolean isDefaultWeapon() {
		return bDefaultWeapon;
	}

	public void setArmorPiercingLevel(byte iArmorPiercingLevel) {
		this.iArmorPiercingLevel = iArmorPiercingLevel;
	}

	public byte getArmorPiercingLevel() {
		return iArmorPiercingLevel;
	}

	public int experiment(int[] iExperimentalIndex, int[] numExperimentationPointsUsed, Player thePlayer) throws IOException{
		// Massive function here.
		
		try {
			ManufacturingSchematic manuSchematic = thePlayer.getCurrentManufacturingSchematic();
			WeaponCraftingSchematic schematic = (WeaponCraftingSchematic)manuSchematic.getCraftingSchematic();
			int experimentalRole = SWGGui.getRandomInt(150);
			SkillMods weaponExperimentationSkillMod = thePlayer.getSkillMod("crafting_weapon_experimentation");
			int experimentationRating = 0;
			if (weaponExperimentationSkillMod != null) {
				experimentationRating = weaponExperimentationSkillMod.getSkillModModdedValue();
			}
			// Anything rolled 0 will be an Amazing Success
			// Anything rolled 1 to experimentatonRating will be a Great Success
			// Anything rolled above experimentationRating will be something else -- (experimentalRole / (150 - experimentationRating))
			int successRating = 0;
			if (experimentalRole == 0) {
				successRating = Constants.CRAFTING_ASSEMBLY_AMAZING_SUCCESS;
			} else if (experimentalRole <= experimentationRating) {
				successRating = Constants.CRAFTING_ASSEMBLY_GREAT_SUCCESS;
			} else {
				successRating = Constants.CRAFTING_ASSEMBLY_GOOD_SUCCESS + (experimentalRole / (150 - experimentationRating));
			}
			System.out.println("Weapon experimentation -- success rating = " + successRating);
			// The success rating determines how much of each experimental point is converted to percentage increase.
			// Critical fail sends all percentages to 0%
			// Bare success increase by 5%
			// OK increase by 10%
			// Marginal Success increase by 17%
			// Success increase by 25%
			// Moderate success increase by 50%
			// Good success increase by 72%
			// Great success increase by 90%
			// Amazing success increase by 100%
/*			protected final static int CRAFTING_ASSEMBLY_AMAZING_SUCCESS = 0;
			protected final static int CRAFTING_ASSEMBLY_GREAT_SUCCESS = 1;
			protected final static int CRAFTING_ASSEMBLY_GOOD_SUCCESS = 2;
			protected final static int CRAFTING_ASSEMBLY_MODERATE_SUCCESS = 3;
			protected final static int CRAFTING_ASSEMBLY_SUCCESS = 4;
			protected final static int CRAFTING_ASSEMBLY_MARGINAL_SUCCESS = 5;
			protected final static int CRAFTING_ASSEMBLY_OK = 6;
			protected final static int CRAFTING_ASSEMBLY_BARE_SUCCESS = 7;
			protected final static int CRAFTING_ASSEMBLY_CRITICAL_FAILURE = 8;
*/
			
			float percentageIncrease = 0;
			switch (successRating) {
				case Constants.CRAFTING_ASSEMBLY_AMAZING_SUCCESS: {
					percentageIncrease = 1.0f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_GREAT_SUCCESS: {
					percentageIncrease =  0.9f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_GOOD_SUCCESS: {
					percentageIncrease = 0.7f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_MODERATE_SUCCESS: {
					percentageIncrease = 0.50f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_SUCCESS: {
					percentageIncrease = 0.4f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_MARGINAL_SUCCESS: {
					percentageIncrease = 0.3f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_OK: {
					percentageIncrease = 0.2f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_BARE_SUCCESS: {
					percentageIncrease = percentageIncrease * 0.1f;
					break;
				}
				case Constants.CRAFTING_ASSEMBLY_CRITICAL_FAILURE: {
					percentageIncrease = 0f;
					break;
				}
				default: {
					percentageIncrease = 0f;
					break;
				}
			}
			//System.out.println("Crafting experimentation -- amount of experimental points actually used: " + percentageIncrease + " percent.");
			
			Hashtable<Byte, Double[]> vCraftingLimits = schematic.getCraftingLimits();
			// Each crafting limit will (hopefully) correspond and be affected in some way by each experimental attribute.  vExperimentalAttributes and fCurrentExperimentalValues needs must be in the same order;
			CraftingExperimentationAttribute[] vExperimentalAttributes = schematic.getAttributes(); // This is a pointer.  Whatever is updated here is also updated in the ManufacturingSchematic.
			float[] fCurrentExperimentalValues = manuSchematic.getVID9CurrentExperimentalValueArray(); // This is a pointer.  Whatever is updated here is also updated in the ManufacturingSchematic.
			ZoneClient client = thePlayer.getClient();
			for (int i =0; i < numExperimentationPointsUsed.length; i++){
				// Get the number of experimental points used, get the percentage increase, and apply it to fCurrentExperimentalValues;
				int iNumPointsThisIndex = numExperimentationPointsUsed[i];
				int iIndex = iExperimentalIndex[i];
				float fIncreaseThisIndex = percentageIncrease * iNumPointsThisIndex;
				System.out.println("Increase index " + iIndex + " by " + fIncreaseThisIndex + " points or " + fIncreaseThisIndex * 10.0f + " percent.");
				System.out.println("Current value " + fCurrentExperimentalValues[iIndex]);
				fCurrentExperimentalValues[iIndex] += (fIncreaseThisIndex / 10.0f);
				System.out.println("New value " + fCurrentExperimentalValues[iIndex]);
				fCurrentExperimentalValues[iIndex] = Math.max(fCurrentExperimentalValues[iIndex], 0); // Minimum of 0%
				fCurrentExperimentalValues[iIndex] = Math.min(fCurrentExperimentalValues[iIndex], 1.0f); // Maximum of 100%
				byte[] theDelta = PacketFactory.buildDeltasMessageMSCO7FloatArray((short)1, (short)9, manuSchematic, (short)iIndex, fCurrentExperimentalValues[iIndex], Constants.DELTA_UPDATING_ITEM);
				PacketUtils.printPacketToScreen(theDelta, "Weapon experimenting deltas message");
				client.insertPacket(theDelta);
			}
			// Now, the experimentation should actually DO something.
			
			for (int i = 0; i < vExperimentalAttributes.length || i < fCurrentExperimentalValues.length; i++) {
				System.out.println("Experimental attribute " + i + " has name " + vExperimentalAttributes[i].getStfFileIdentifier() + " and experimental value " + fCurrentExperimentalValues[i]);
				// So, all we have to do is get the correct range from vCraftingLimits, set the value in the Weapon, and update the client with the new attributes once we're done.
				// Let's make it a function.
				calculateAttribute(vExperimentalAttributes[i].getStfFileIdentifier(), fCurrentExperimentalValues[i], vCraftingLimits, client);
			}
			return successRating;
		} catch (ClassCastException e ) {
			System.out.println("Error -- Weapon experiment not experimenting on a weapon. " + e.toString());
			e.printStackTrace();
			return Constants.CRAFTING_ASSEMBLY_INTERNAL_FAILURE;
		}
	}

	protected void calculateAttribute(String sAttributeName, float fExperimentalValue, Hashtable<Byte, Double[]> vCraftingLimits, ZoneClient client) throws IOException {
		Attribute theAttribute = null;
		Double[] theRange = null;
		// Direction:  true = higher is better, false = lower is better.
		
		// TODO -- These attributes created when user advances from Assemble.
		if (sAttributeName.equals("minDamage")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_DAMAGE_MIN);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_DAMAGE_MIN, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_MIN_DAMAGE_RANGE);
			double range = theRange[1] - theRange[0];
			iMinimumDamage = (int) ((range * fExperimentalValue) + theRange[0]);
			theAttribute.setAttributeValue(String.valueOf(iMinimumDamage));

		} else if (sAttributeName.equals("maxDamage")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_DAMAGE_MAX);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_DAMAGE_MAX, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_MAX_DAMAGE_RANGE);
			double range = theRange[1] - theRange[0];
			iMaximumDamage = (int) ((range * fExperimentalValue) + theRange[0]);
			theAttribute.setAttributeValue(String.valueOf(iMaximumDamage));

		} else if (sAttributeName.equals("attackSpeed")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_SPEED);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_SPEED, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_ATTACK_SPEED_RANGE);
			double range = theRange[0] - theRange[1];
			client.insertPacket(setRefireDelay((float)(theRange[1] + (range * fExperimentalValue)), false));
			theAttribute.setAttributeValue(String.valueOf(fRefireDelay));

		} else if (sAttributeName.equals("woundChance")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_WOUND_CHANCE);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_WOUND_CHANCE, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_TO_WOUND_RANGE);
			double range = theRange[1] - theRange[0];
			iWoundChance= (int) ((range * fExperimentalValue) + theRange[0]);
			theAttribute.setAttributeValue(String.valueOf(iWoundChance));

		} else if (sAttributeName.equals("hitPoints")) {
			// Condition and condition damage.
			theRange = new Double[2];
			theRange[0] = 600.0;
			theRange[1] = 1200.0;
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_CONDITION);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_CONDITION, "0/0");
				addAttribute(theAttribute);
			}
			// Higher is better
			double range = theRange[1] - theRange[0];
			client.insertPacket(setMaxCondition((int) ((range * fExperimentalValue) + theRange[0]), true));
			client.insertPacket(setConditionDamage(0, true));
			String sAttribute = (getMaxCondition() - getConditionDamage()) + "/" + getMaxCondition();
			theAttribute.setAttributeValue(sAttribute);
		} else if (sAttributeName.equals("zeroRangeMod")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_ZERO);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_ZERO, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_RANGE_TO_HIT_MODS); // Array indices 0, 1
			if (theRange != null) {
				if (theRange.length > 1) {
					theRange = Arrays.copyOfRange(theRange, 0, 2); // Array indices 0, 1
					
					double range = theRange[1] - theRange[0];
					iZeroRangeModDistance= (int) ((range * fExperimentalValue) + theRange[0]);
				} else {
					iZeroRangeModDistance = theRange[0].intValue();
				}
				theAttribute.setAttributeValue(String.valueOf(iZeroRangeModDistance));
			}
		} else if (sAttributeName.equals("maxRangeMod")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MAX);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MAX, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_RANGE_TO_HIT_MODS); // Array indices 0, 1
			if (theRange != null) {
				if (theRange.length > 5) {
					theRange = Arrays.copyOfRange(theRange, 4, 6); // Array indices 0, 1
				
					double range = theRange[1] - theRange[0];
					iMaxRangeModDistance= (int) ((range * fExperimentalValue) + theRange[0]);
				} else {
					iMaxRangeModDistance = theRange[0].intValue();
				}
				theAttribute.setAttributeValue(String.valueOf(iMaxRangeModDistance));
					
			}
		} else if (sAttributeName.equals("midRangeMod")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MID);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MID, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_RANGE_TO_HIT_MODS); // Array indices 0, 1
			if (theRange != null) {
				if (theRange.length > 3) {
					theRange = Arrays.copyOfRange(theRange, 2, 4); // Array indices 0, 1
					double range = theRange[1] - theRange[0];
					iMediumRangeModDistance= (int) ((range * fExperimentalValue) + theRange[0]);
				} else {
					iMediumRangeModDistance = theRange[0].intValue();
				}
				theAttribute.setAttributeValue(String.valueOf(iMediumRangeModDistance));
			}
		} else if (sAttributeName.equals("attackHealthCost")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_HEALTH);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_HEALTH, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_SAC_HEALTH_RANGE); // Array indices 0, 1
			double range = theRange[0] - theRange[1];
			iHealthCost = (int)(theRange[1] + (range * fExperimentalValue));
			theAttribute.setAttributeValue(String.valueOf(iHealthCost));

		} else if (sAttributeName.equals("attackActionCost")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_ACTION);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_ACTION, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_SAC_ACTION_RANGE); // Array indices 0, 1
			double range = theRange[0] - theRange[1];
			iActionCost= (int)(theRange[1] + (range * fExperimentalValue));
			theAttribute.setAttributeValue(String.valueOf(iActionCost));

		} else if (sAttributeName.equals("attackMindCost")) {
			theAttribute = getAttributeByIndex(Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_MIND);
			if (theAttribute == null) {
				theAttribute = new Attribute(Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_MIND, 0);
				addAttribute(theAttribute);
			}
			theRange = vCraftingLimits.get(Constants.WEAPON_SAC_MIND_RANGE); // Array indices 0, 1
			double range = theRange[0] - theRange[1];
			iMindCost = (int)(theRange[1] + (range * fExperimentalValue));
			theAttribute.setAttributeValue(String.valueOf(iMindCost));

		} else {
			System.out.println("Unknown experimental attribute to calculate: " + sAttributeName);
			return;
		}
	}
	
	protected byte[] setRefireDelay(float fNewRefireDelay, boolean bUpdateZone) throws IOException {
		fRefireDelay = fNewRefireDelay;
		if (false) {
		// TODO -- Find correct vID for this, if any.
		//if (bUpdateZone) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_WEAO, (byte)3, (short)1, (short)15, this, fRefireDelay);
		}
		return null;
	
	}

	public void setWeaponType(int iWeaponType) {
		this.iWeaponType = iWeaponType;
	}

	public int getWeaponType() {
		return iWeaponType;
	}

	public void setAttackRange(float fAttackRange) {
		this.fAttackRange = fAttackRange;
	}

	public float getAttackRange() {
		return fAttackRange;
	}

	public byte[] setNumberOfInventorySlotsTaken(int iNumberOfInventorySlotsTaken, boolean bUpdateZone) throws IOException {
		this.iNumberOfInventorySlotsTaken = iNumberOfInventorySlotsTaken;
		if (bUpdateZone) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_WEAO, (byte)3, (short)1, (short)3, this, iNumberOfInventorySlotsTaken);
		} else{
			return null;
		}
	}

	public int getNumberOfInventorySlotsTaken() {
		return iNumberOfInventorySlotsTaken;
	}
	
}

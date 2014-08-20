import java.util.Vector;

/**
 * Container class for all items in the game.  This tells us everything we need to know to spawn an item, equip an item,
 * craft an item and use an item.  Anything from a building to a gnort to a T21 rifle is covered.
 * @author Darryl
 *
 */
public class ItemTemplate {
	private int iTemplateID = -1;
	private int iTangibleTemplateID = -1;
	private boolean bNeedsSerialNumber = false;
	private String sIFFFileName = "";
	private int iCRC = 0;
	private int iRequiredSkillID = -1;
	private int iRequiredSpeciesMale = -1;
	private int iRequiredSpeciesFemale = -1;
	private String sSTFFileName = "";
	private String sSTFFileIdentifier = "";
	private String sSTFDetailName = "";
	private String sSTFDetailIdentifier = "";
	private String sSTFLookAtName = "";
	private String sSTFLookAtIdentifier = "";
	private boolean[][] bStarterItem = null;
	private int iCellCount = 0;
	private Vector<RadialMenuItem> vRadialMenuItems;
	private String sScriptName = "";
	private int iScriptType = -1;
        private boolean isContainer;
        private boolean isFemaleItem;
        private int [] raceRestrictions;
        //Weapon Stats
        //Int
        private int iMinDmg = 8;
        private int iMaxDmg = 8;
        private int iHealthCost = 8;
        private int iActionCost = 8;
        private int iMindCost = 8;
        private int iWoundChance = 8;
        //Float
        private float fRefireDelay = 5;
        private float fZeroRange = 5;
        private float fIdealRange = 5;
        private float fMaxRange = 5;
        //Byte
        private byte bWeaponType = 5;
        private byte bDamageType = 5;
        private byte bArmorPiercingLevel = 5;

	/**
	 * Creates a new, empty ItemTemplate object.
	 */
	public ItemTemplate() {
		bStarterItem = new boolean[Constants.NUM_RACES][Constants.NUM_PROFESSIONS];
		vRadialMenuItems = new Vector<RadialMenuItem>();
                isContainer = false;
	}

	/**
	 * Sets the Template ID of this item.
	 * @param id -- The template ID.
	 */
	public void setTemplateID(int id) {
		iTemplateID = id;
	}

	/**
	 * Gets the Template ID of this item.
	 * @return The template ID.
	 */
	public int getTemplateID() {
		return iTemplateID;
	}

	/**
	 * Sets whether any new instance of this item needs to have an attached serial number for crafting purposes.
	 * @param b -- If items of this template need a serial number or not.
	 */
	public void setNeedsSerial(boolean b) {
		bNeedsSerialNumber = b;
	}

	/**
	 * Gets if new items of this template need a serial number.
	 * @return Whether or not items of this template need a serial number.
	 */
	public boolean needsSerial() {
		return bNeedsSerialNumber;
	}

	/**
	 * Set the base IFF filename for this template.  The IFF file contains all data the Client needs to successfully
	 * create and render an item of this template.
	 * @param sIFFName -- The filename.
	 */
	public void setIFFFileName(String sIFFName) {
		sIFFFileName = sIFFName;
	}


	/**
	 * Gets the base IFF filename for this template.  The IFF file contains all data the Client needs to successfully
	 * create and render an item of this template.
	 * @return The IFF filename.
	 */
	public String getIFFFileName() {
		return sIFFFileName;
	}

	/**
	 * Sets the CRC of the IFF filename for this template.  Required data for the sceneCreateObjectByCRC packet.
	 * @param iCRC -- The CRC of this item.
	 */
	public void setCRC(long iCRC) {
		this.iCRC = (int)iCRC;
	}

	/**
	 * Gets the CRC of the IFF filename for this template.  Required data for the sceneCreateObjectByCRC packet.
	 * @return The CRC of this item.
	 */
	public int getCRC() {
		return iCRC;
	}

	/**
	 * Sets the skill needed to use / equip this item successfully, if any.
	 * @param id -- The skill ID needed to use items of this template.
	 */
	public void setRequiredSkillID(int id) {
		iRequiredSkillID = id;
	}

	/**
	 * Gets the skill id needed to use / equip this item successfully, if any.
	 * @return The skill ID needed, or -1 if no skill is needed.
	 */
	public int getRequiredSkillID() {
		return iRequiredSkillID;
	}

	/**
	 * Sets the male gender species requirement for items of this template, if any.
	 * @param id The species requirement of items of this template.
	 * @throws An ArrayIndexOutOfBoundsException if the requirement is greater than 10 or less than -1.
	 */
	public void setRequiredSpeciesMale(int id) throws ArrayIndexOutOfBoundsException{
		if (id < -1 || id > 10) throw new ArrayIndexOutOfBoundsException("Error:  Setting the required male species to an invalid value");
		iRequiredSpeciesMale = id;
	}

	/**
	 * Gets the male gender species requirement for items of this template, if any.
	 * @return The species requirement of items of this template, or -1 if there is no requirement.
	 */
	public int getRequiredSpeciesMale() {
		return iRequiredSpeciesMale;
	}

	/**
	 * Sets the female gender species requirement for items of this template, if any.
	 * @param id The species requirement of items of this template.
	 * @throws An ArrayIndexOutOfBoundsException if the requirement is not between 10 and 19, or less than -1.
	 */

	public void setRequiredSpeciesFemale(int id) throws ArrayIndexOutOfBoundsException{
		if (id == -1 || (id >= 10 && id < 20)) {
			iRequiredSpeciesFemale = id;
		} else {
			throw new ArrayIndexOutOfBoundsException("Error:  Setting the required female species to an invalid value.");
		}
	}

	/**
	 * Gets the male gender species requirement for items of this template, if any.
	 * @return The species requirement of items of this template, or -1 if there is no requirement.
	 */
	public int getRequiredSpeciesFemale() {
		return iRequiredSpeciesFemale;
	}

	/**
	 * Sets the base STF file name for items of this template, if any.
	 * @param sSTFFileName -- The STF filename for items of this template.
	 */
	public void setSTFFileName(String sSTFFileName) {
		this.sSTFFileName = sSTFFileName;
	}

	/**
	 * Gets the base STF filename for items of this template, if any.
	 * @return The STF filename.
	 */
	public String getSTFFileName() {
		return sSTFFileName;
	}

	public void setSTFFileIdentifier(String sSTFFileIdentifier) {
		this.sSTFFileIdentifier = sSTFFileIdentifier;
	}

	public String getSTFFileIdentifier() {
		return sSTFFileIdentifier;
	}


	/**
	 * Sets the base STF file name for items of this template, if any.
	 * @param sSTFFileName -- The STF filename for items of this template.
	 */
	public void setSTFDetailName(String sSTFDetailName) {
		this.sSTFDetailName = sSTFDetailName;
	}

	/**
	 * Gets the base STF filename for items of this template, if any.
	 * @return The STF filename.
	 */
	public String getSTFDetailName() {
		return sSTFDetailName;
	}

	public void setSTFDetailIdentifier(String sSTFDetailIdentifier) {
		this.sSTFDetailIdentifier = sSTFDetailIdentifier;
	}

	public String getSTFDetailIdentifier() {
		return sSTFDetailIdentifier;
	}

	/**
	 * Sets the base STF file name for items of this template, if any.
	 * @param sSTFFileName -- The STF filename for items of this template.
	 */
	public void setSTFLookAtName(String sSTFLookAtName) {
		this.sSTFLookAtName = sSTFLookAtName;
	}

	/**
	 * Gets the base STF filename for items of this template, if any.
	 * @return The STF filename.
	 */
	public String getSTFLookAtName() {
		return sSTFLookAtName;
	}

	public void setSTFLookAtIdentifier(String sSTFLookAtIdentifier) {
		this.sSTFLookAtIdentifier = sSTFLookAtIdentifier;
	}

	public String getSTFLookAtIdentifier() {
		return sSTFLookAtIdentifier;
	}
	/**
	 * Sets whether items of this tempate are starter items for the given race and profession.
	 * @param iRaceID -- The character race.
	 * @param iProfessionID -- The character profession.
	 * @param b -- If this is a starter item or not.
	 */
	public void setStarterItemParamater(int iRaceID, int iProfessionID, boolean b) {
		if (iProfessionID < 0) {
			// Oops;
			return;
		}
		bStarterItem[iRaceID][iProfessionID] = b;
	}

	/**
	 * Sets items of this template to be starter items for the given race and profession.
	 * @param iRaceID -- The character race.
	 * @param iProfessionID -- The character profession.
	 */
	public void setStarterItemParamater(int iRaceID, int iProfessionID) {
		bStarterItem[iRaceID][iProfessionID] = true;
	}

	/**
	 * Gets if items of this template are starter items for the given race and profession.
	 * @param iRaceID -- The character race.
	 * @param iProfessionID -- The character profession.
	 * @return If this item is a starter item.
	 */
	public boolean getIsStarterItem(int iRaceID, int iProfessionID) throws ArrayIndexOutOfBoundsException {
		if (iRaceID >= bStarterItem.length) {
			throw new ArrayIndexOutOfBoundsException("Error:  Race ID is out of bounds!");
		} else if (iProfessionID >= bStarterItem[iRaceID].length){
			throw new ArrayIndexOutOfBoundsException("Error:  Profession ID is out of bounds!");
		}
		return bStarterItem[iRaceID][iProfessionID];
	}

	/**
	 * Returns whether items of this template are starter items for any race / profession.
	 * @return If this item is any starter item.
	 */
	public boolean isAnyStarterItem() {
		for (int i = 0; i < Constants.NUM_RACES; i++) {
			for (int j = 0; j < Constants.NUM_PROFESSIONS; j++) {
				if (bStarterItem[i][j]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Set the number of Cell objects buildings of this template contain.
	 * @param i -- The number of Cells.
	 */
	public void setCellCount(int i) {
		iCellCount = i;
	}

	/**
	 * Gets the number of Cell objects buildings of this template contain.
	 * @return The number of Cells.
	 */
	public int getCellCount() {
		return iCellCount;
	}

	public int getTangibleTemplateID() {
		return iTangibleTemplateID;
	}

	public void setTangibleTemplateID(int iTemplateID) {
		iTangibleTemplateID = iTemplateID;
	}

	public Vector<RadialMenuItem> getRadialMenuItems() {
		return vRadialMenuItems;
	}

	public void setScriptName(String s) {
		sScriptName = s;
	}

	public String getScriptName() {
		return sScriptName;
	}

	public void setScriptType(int i) {
		iScriptType = i;
	}

	public int getScriptType() {
		return iScriptType;
	}

        public boolean isContainer() {
            return isContainer;
        }

        public void setIsContainer(boolean isContainer) {
            this.isContainer = isContainer;
        }

        public boolean isFemaleItem() {
            return isFemaleItem;
        }

        public void setIsFemaleItem(boolean isFemaleItem) {
            this.isFemaleItem = isFemaleItem;
        }

        public int[] getRaceRestrictions() {
            return raceRestrictions;
        }

        public void setRaceRestrictions(int[] raceRestrictions) {
            this.raceRestrictions = raceRestrictions;
        }
        //Weapon Stats
        public void setMinDmg(int mindmg) {
		iMinDmg = mindmg;
	}

	public int getMinDmg() {
		return iMinDmg;
	}
        public void setMaxDmg(int maxdmg) {
		iMaxDmg = maxdmg;
	}

	public int getMaxDmg() {
		return iMaxDmg;
	}
        public void setHealthCost(int healthcost) {
		iHealthCost = healthcost;
	}

	public int getHealthCost() {
		return iHealthCost;
	}
        public void setActionCost(int actioncost) {
		iActionCost = actioncost;
	}

	public int getActionCost() {
		return iActionCost;
	}
        public void setMindCost(int mindcost) {
		iMindCost = mindcost;
	}

	public int getMindCost() {
		return iMindCost;
	}
        public void setWoundChance(int woundchance) {
		iWoundChance = woundchance;
	}

	public int getWoundChance() {
		return iWoundChance;
	}
        public void setRefireDelay(float refiredelay) {
		fRefireDelay = refiredelay;
	}

	public float getRefireDelay() {
		return fRefireDelay;
	}
        public void setZeroRange(float zerorange) {
		fZeroRange = zerorange;
	}

	public float getZeroRange() {
		return fZeroRange;
	}
         public void setIdealRange(float idealrange) {
		fIdealRange = idealrange;
	}

	public float getIdealRange() {
		return fIdealRange;
	}
         public void setMaxRange(float maxrange) {
		fMaxRange = maxrange;
	}

	public float getMaxRange() {
		return fMaxRange;
	}
        public void setWeaponType(byte weapontype) {
		bWeaponType = weapontype;
	}

	public byte getWeaponType() {
		return bWeaponType;
	}
        public void setDamageType(byte damagetype) {
		bDamageType = damagetype;
	}

	public byte getDamageType() {
		return bDamageType;
	}
        public void setArmorPiercingLevel(byte armorpiercinglevel) {
		bArmorPiercingLevel = armorpiercinglevel;
	}

	public byte getArmorPiercingLevel() {
		return bArmorPiercingLevel;
	}
}

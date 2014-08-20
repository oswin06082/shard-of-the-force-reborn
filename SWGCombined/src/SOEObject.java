import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.geom.Point2D;

/**
 * The SOEObject is the base SWG game object. All objects in the SWG world share
 * all of the attributes found in the SOEObject.
 * 
 * @author Darryl
 * 
 */
public class SOEObject implements Serializable, Comparable<SOEObject> {
	public final static long serialVersionUID = 1l;
	public final static String DEFAULT_STF = "obj_n";
	public final static String DEFAULT_NAME = "unknown_object";
	public final static String PLAYER_STF = "species";
	// private final static long DEFAULT_ID = 25;
	
	
	private String STFFileName;
	private String sSTFFileIdentifier;
	private String STFDetailName;
	private String sSTFDetailIdentifier;
	private String STFLookAtName;
	private String sSTFLookAtIdentifier;
	private String sIFFFileName;
	private long objectID;
	private long lSerialNumber;
	private Hashtable<Integer, Attribute> vAttributes;
	private Hashtable<Character, RadialMenuItem> vRadials;
	private byte[] iCustomizationData;
	private float currentX = 0;
	private float currentZ = 0;
	private float currentY = 0;
	private float currentCellX = 0;
	private float currentCellY = 0;
	private float currentCellZ = 0;
	private float orientationNorth = 0;
	private float orientationEast = 0;
	private float orientationSouth = 0;
	private float orientationWest = 1.0f;
	private int iPlanetID;
	private int iObjectCRC;
	private long lCellID = 0;
	// private transient byte[][] baselinePackets;
	// private boolean bIsWorldObject = false;
	private Vector<SOEObject> vEquippedItems;
	private int slotID = 0;
	private int iTemplateID = 0;
	private int iConditionFlag = 0;
	private int iPVPStatus = 0;
	private int iAttackedPVPStatus = Constants.PVP_STATUS_IS_HOSTILE;
	private int iFactionCRC = 0;
	private byte iFactionRank = -1;
	// private Vector<RadialMenuItem> vRadialMenus;
	private boolean bIsStaticObject = false;
	private boolean bOneClickUse = false;
	private String ScriptName;
	private long CreatorID = 0;
	private String CraftedName = "";
	private long lParentID;
	private boolean bIsBuilding;
	private int iMovementCounter = 0;
	private String sObjectTitle;
	private byte delayedSpawnAction;
	private float fMovementAngle = 0f;
	private int iObjectUpdateCounter;
	private boolean isTerminal;
	private CustomData customizationData; // To be implemented by PlasmaFlow

	private transient int iRadialCondition;
	private transient int iMovementUpdateCounter = 0;
	// private transient boolean bIsInCombat = false;

	private transient Vector<ZoneClient> vSynchListeners;

	private transient Vector<CreaturePet> vPetsFollowingObject;

	private boolean bCanBePickedUp;

	// private Script ScriptObject;
	
	/**
	 * Gets the visual customization data for this game object.
	 * 
	 * @return The customization data.
	 */
	protected byte[] getCustomData() {
		return iCustomizationData;
	}

	/**
	 * Sets the visual customization data for this game object.
	 * 
	 * @param data
	 *            -- The customization data.
	 */
	protected void setCustomizationData(byte[] data) {
		iCustomizationData = data;
	}

	/**
	 * Constructs a default SOEObject.
	 */
	public SOEObject() {
		STFFileName = DEFAULT_STF;
		sSTFFileIdentifier = DEFAULT_NAME;
		objectID = -1;
		vAttributes = new Hashtable<Integer, Attribute>();
		vRadials = new Hashtable<Character, RadialMenuItem>();
		vEquippedItems = new Vector<SOEObject>();
		iRadialCondition =  Constants.RADIAL_CONDITION.NORMAL.ordinal();
		bCanBePickedUp = true;
		// ScriptObject = new Script();

	}

	/**
	 * Constructs a new SOEObject from the given STF filename, referencing the
	 * given name parameter in the STF file, with the given Object ID,
	 * 
	 * @param STFName
	 *            -- The STF filename.
	 * @param objectName
	 *            -- The parameter in the STF filename.
	 * @param objectID
	 *            -- The Object's unique ID.
	 */
	public SOEObject(String STFName, String objectName, long objectID) {
		STFFileName = STFName;
		sSTFFileIdentifier = objectName;
		this.objectID = objectID;
		vEquippedItems = new Vector<SOEObject>();
		vAttributes = new Hashtable<Integer, Attribute>();
		iRadialCondition  = Constants.RADIAL_CONDITION.NORMAL.ordinal();
		bCanBePickedUp = true;
		// ScriptObject = new Script();
	}

	protected void setScriptName(String S) {
		ScriptName = S;
	}

	protected String getScriptName() {
		return ScriptName;
	}

	/**
	 * Gets the STF filename for this object. This is the Portion of the STF
	 * File that goes between the '@' and the ':' Using this you can build STF
	 * Strings like '@player_structure:'
	 * 
	 * @return The STF filename.
	 */
	public String getSTFFileName() {
		if (iTemplateID != 0) {
			return DatabaseInterface.getTemplateDataByID(iTemplateID)
					.getSTFFileName();
		} else if (iObjectCRC != 0) {
			return DatabaseInterface.getTemplateDataByCRC(iObjectCRC)
					.getSTFFileName();
		} else if (!sIFFFileName.isEmpty()) {
			return DatabaseInterface.getTemplateDataByFilename(sIFFFileName)
					.getSTFFileName();
		}
		return STFFileName;
	}

	/**
	 * Sets the STF filename for this object.
	 * 
	 * @param s
	 *            -- The STF filename.
	 */
	// deprecated
	// public void setSTFFileName(String s) {
	// STFFileName = s;
	// }
	public void setSTFFileName(String s) {
		STFFileName = s;
	}

	/**
	 * Gets the paramater in the STF filename this object uses. This is the part
	 * of the stf that gets the actual name from the location.
	 * 
	 * @return The paramater in the STF file.
	 */
	public String getSTFFileIdentifier() {

		if (iTemplateID != 0) {
			return DatabaseInterface.getTemplateDataByID(iTemplateID)
					.getSTFFileIdentifier();
		} else if (iObjectCRC != 0) {
			return DatabaseInterface.getTemplateDataByCRC(iObjectCRC)
					.getSTFFileIdentifier();
		} else if (!sIFFFileName.isEmpty()) {
			return DatabaseInterface.getTemplateDataByFilename(sIFFFileName)
					.getSTFFileIdentifier();
		}
		return sSTFFileIdentifier;
	}

	/**
	 * Sets the paramater in the STF filename this object uses.
	 * 
	 * @param s
	 *            -- The STF file paramater.
	 */
	// public void setSTFFileIdentifier(String s) {
	// sSTFFileIdentifier = s;
	// }
	public void setSTFFileIdentifier(String s) {
		sSTFFileIdentifier = s;
	}

	/**
	 * Gets the STF filename for this object.
	 * 
	 * @return The STF filename.
	 */
	public String getSTFDetailName() {
		if (iTemplateID != 0) {
			return DatabaseInterface.getTemplateDataByID(iTemplateID)
					.getSTFDetailName();
		} else if (iObjectCRC != 0) {
			return DatabaseInterface.getTemplateDataByCRC(iObjectCRC)
					.getSTFDetailName();
		} else if (!sIFFFileName.isEmpty()) {
			return DatabaseInterface.getTemplateDataByFilename(sIFFFileName)
					.getSTFDetailName();
		}
		return STFDetailName;
	}

	/**
	 * Sets the STF filename for this object.
	 * 
	 * @param s
	 *            -- The STF filename.
	 */
	// public void setSTFDetailName(String s) {
	// STFDetailName = s;
	// }
	/**
	 * Gets the paramater in the STF filename this object uses.
	 * 
	 * @return The paramater in the STF file.
	 */
	public String getSTFDetailIdentifier() {
		if (iTemplateID != 0) {
			return DatabaseInterface.getTemplateDataByID(iTemplateID)
					.getSTFDetailIdentifier();
		} else if (iObjectCRC != 0) {
			return DatabaseInterface.getTemplateDataByCRC(iObjectCRC)
					.getSTFDetailIdentifier();
		} else if (!sIFFFileName.isEmpty()) {
			return DatabaseInterface.getTemplateDataByFilename(sIFFFileName)
					.getSTFDetailIdentifier();
		}
		return sSTFDetailIdentifier;
	}

	/**
	 * Sets the paramater in the STF filename this object uses.
	 * 
	 * @param s
	 *            -- The STF file paramater.
	 */
	// public void setSTFDetailIdentifier(String s) {
	// sSTFDetailIdentifier = s;
	// }

	/**
	 * Gets the STF filename for this object.
	 * 
	 * @return The STF filename.
	 */
	public String getSTFLookAtName() {
		if (iTemplateID != 0) {
			return DatabaseInterface.getTemplateDataByID(iTemplateID)
					.getSTFLookAtName();
		} else if (iObjectCRC != 0) {
			return DatabaseInterface.getTemplateDataByCRC(iObjectCRC)
					.getSTFLookAtName();
		} else if (!sIFFFileName.isEmpty()) {
			return DatabaseInterface.getTemplateDataByFilename(sIFFFileName)
					.getSTFLookAtName();
		}
		return STFLookAtName;
	}

	/**
	 * Sets the STF filename for this object.
	 * 
	 * @param s
	 *            -- The STF filename.
	 */
	// public void setSTFLookAtName(String s) {
	// STFLookAtName = s;
	// }
	/**
	 * Gets the paramater in the STF filename this object uses.
	 * 
	 * @return The paramater in the STF file.
	 */
	public String getSTFLookAtIdentifier() {
		if (iTemplateID != 0) {
			return DatabaseInterface.getTemplateDataByID(iTemplateID)
					.getSTFLookAtIdentifier();
		} else if (iObjectCRC != 0) {
			return DatabaseInterface.getTemplateDataByCRC(iObjectCRC)
					.getSTFLookAtIdentifier();
		} else if (!sIFFFileName.isEmpty()) {
			return DatabaseInterface.getTemplateDataByFilename(sIFFFileName)
					.getSTFLookAtIdentifier();
		}
		return sSTFLookAtIdentifier;
	}

	/**
	 * Sets the paramater in the STF filename this object uses.
	 * 
	 * @param s
	 *            -- The STF file paramater.
	 */
	// public void setSTFLookAtIdentifier(String s) {
	// sSTFLookAtIdentifier = s;
	// }
	/**
	 * Gets this object's unique ID.
	 * 
	 * @return The object ID.
	 */
	public long getID() {
		return objectID;
	}

	/**
	 * Sets this object's unique ID.
	 * 
	 * @param id
	 *            -- The object ID.
	 */
	public void setID(long id) {
		objectID = id;
	}

	// These do not necessarially apply to ALL SOEObjects -- should not be here.

	/**
	 * Sets the ID Of the Player Object or Object that created this Object. If
	 * the ID is 0 then the creator name is System or left blank.
	 * 
	 * @param id
	 */
	protected void setCreatorID(long id) {
		CreatorID = id;
	}

	/**
	 * Gets the ObjectID of the creator of this item. If the ID is 0 then the
	 * creator name is System or left blank.
	 * 
	 * @return
	 */
	protected long getCreatorID() {
		return CreatorID;
	}

	/**
	 * Sets the name of the object as it was created by the crafter
	 * 
	 * @param Name
	 */
	protected void setCraftedName(String Name) {
		CraftedName = Name;
	}

	/**
	 * Returns the name that the original crafter gave this object.
	 * 
	 * @return
	 */
	protected String getCraftedName() {
		return CraftedName;
	}

	/**
	 * Gets a list of all of the Attributes for this object. Attributes are
	 * viewed by the Player when they select this item.
	 * 
	 * @return The Attributes.
	 */
	public Hashtable<Integer, Attribute> getAttributeList(ZoneClient c) {
		// TODO -- This should be set on object creation, not checked each time
		// this function is called.

		// System.out.println("SOEObject()getAttributeList() for " +
		// this.getClass().getName());
		Hashtable<Integer, Attribute> retVector = new Hashtable<Integer, Attribute>();

		if (vAttributes == null || vAttributes.isEmpty()) {
			// Attribute(String sName, String sValue)
			// when adding objects to send attributes for in this area make sure
			// that the base object is last and extended objects first.
			// For example Any object based from the TangibleItem has to be
			// below lets say a TravelTicket Item.
			// Otherwise the attributes sent will be for the tangible and not a
			// travel ticket.

			if (this instanceof Instrument) // Tangible Items and their children
											// begin here
			{
				try {
					Weapon W = (Weapon) this;
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, Long
									.toHexString(W.getID())));
					if (W.getCreatorID() == 0) {
						W.addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER, "System"));
						W.addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, "@"
										+ W.getSTFFileName() + ":"
										+ W.getSTFFileIdentifier()));
					} else if (this.CreatorID >= 1) {
						String sCrafterName = "";
						Player tP = (Player) c.getServer()
								.getObjectFromAllObjects(W.getCreatorID());
						if (tP != null) {
							sCrafterName = tP.getFullName();
						}
						W.addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER,
								sCrafterName));
						W.addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, W
										.getCraftedName()));
					}
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_CONDITION,
							(W.getMaxCondition() - W.getConditionDamage())
									+ " / " + W.getMaxCondition()));

					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WEAPON_CERT_STATUS,
							"Yes"));

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Instrument Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof Weapon) // Tangible Items and their
												// children begin here
			{
				try {
					Weapon W = (Weapon) this;
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, Long
									.toHexString(W.getID())));

					if (W.getCreatorID() == 0) {
						W.addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER, "System"));
						W.addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, "@"
										+ W.getSTFFileName() + ":"
										+ W.getSTFFileIdentifier()));
					} else if (this.CreatorID >= 1) {
						String sCrafterName = "";
						Player tP = (Player) c.getServer()
								.getObjectFromAllObjects(W.getCreatorID());
						if (tP != null) {
							sCrafterName = tP.getFullName();
						}
						W.addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER,
								sCrafterName));
						W.addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, W
										.getCraftedName()));
					}
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_CONDITION,
							(W.getMaxCondition() - W.getConditionDamage())
									+ " / " + W.getMaxCondition()));
					// NewAttribute = new Attribute(" "," ");
					// retVector.put(NewAttribute.getAttributeIndex(),
					// NewAttribute);

					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WPN_DAMAGE_MIN, W
									.getMinDamage()));
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WPN_DAMAGE_MAX, W
									.getMaxDamage()));
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_SPEED, W
									.getRefireDelay()));
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_HEALTH,
							W.getHealthCost()));
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_ACTION,
							W.getActionCost()));
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WPN_ATTACK_COST_MIND, W
									.getMindCost()));
					// W.addAttribute(new
					// Attribute(Constants.OBJECT_ATTRIBUTE_WPN_RANGE_MAX,W.getDamageRange()));
					String Wound = Double.toString(((W.getMinDamage() + W
							.getMaxDamage())
							/ W.getDamageRange() * 100));
					Wound = Wound.substring(0, Wound.indexOf(".") + 1) + "0";
					W
							.addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_WPN_WOUND_CHANCE,
									Wound));
					String R = Double.toString(W.getDamageRange() * 3.14);
					R = R.substring(0, R.indexOf(".") + 1) + "0";
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WPN_DAMAGE_RADIUS, R));
					W
							.addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_ZERO,
									0));
					W
							.addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MID,
									0));
					W
							.addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_WPN_RANGE_ATTACK_MOD_MAX,
									0));
					W.addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_WEAPON_CERT_STATUS,
							"Yes")); // Not necessarily. TODO -- Calculate if
										// the viewer of the certifications is
										// certified for this weapon.
				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Weapon Attributes "
									+ e);
					e.printStackTrace();
				}

			} else if (this instanceof Armor) {
				Armor A = (Armor) this;
				try {
					if (A.isBIsNoTradeItem()) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_ARMOR_SPECIAL_TYPE,
								"\\#FF0000No Trade\\#FFFFFF"));
					}

					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, Long
									.toHexString(A.getID())));
					if (A.getCreatorID() == 0) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER, "System"));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, "@"
										+ A.getSTFFileName() + ":"
										+ A.getSTFFileIdentifier()));
					} else if (this.CreatorID >= 1) {
						String sCrafterName = "";
						Player tP = (Player) c.getServer()
								.getObjectFromAllObjects(A.getCreatorID());
						if (tP != null) {
							sCrafterName = tP.getFullName();
						}
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER,
								sCrafterName));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, A
										.getCraftedName()));
					}
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_CONDITION, A
									.getMaxCondition()
									- A.getConditionDamage()
									+ " / "
									+ A.getMaxCondition()));

					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_ARMORENCUMBRANCE, ""));
					// -------------------------
					String sArrtib = "";
					for (int i = 0; i < A.getIHealthEnc().length; i++) {
						switch (i) {
						case 0: {
							sArrtib += "\r\nHealth " + A.getIHealthEnc()[i];
							break;
						}
						case 1: {
							sArrtib += "\r\nStrength " + A.getIHealthEnc()[i];
							break;
						}
						case 2: {
							sArrtib += "\r\nConstitution "
									+ A.getIHealthEnc()[i] + "\r\n";
							break;
						}
						}
					}
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_ARMOR_HEALTH_ENCUMBRANCE,
							sArrtib));
					// NewAttribute = new Attribute(Constants.object_attribute_,
					// "NOT CODED");
					// retVector.put(NewAttribute.getAttributeIndex(),
					// NewAttribute);
					// ------------------------
					sArrtib = "";
					for (int i = 0; i < A.getIActionEnc().length; i++) {
						switch (i) {
						case 0: {
							sArrtib += "\r\nAction " + A.getIActionEnc()[i];
							break;
						}
						case 1: {
							sArrtib += "\r\nQuickness " + A.getIActionEnc()[i];
							break;
						}
						case 2: {
							sArrtib += "\r\nStamina " + A.getIActionEnc()[i]
									+ "\r\n";
							break;
						}
						}
					}
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_ARMOR_ACTION_ENCUMBRANCE,
							sArrtib));

					// ---------------------
					sArrtib = "";
					for (int i = 0; i < A.getIMindEnc().length; i++) {
						switch (i) {
						case 0: {
							sArrtib += "\r\nMind " + A.getIMindEnc()[i];
							break;
						}
						case 1: {
							sArrtib += "\r\nFocus " + A.getIMindEnc()[i];
							break;
						}
						case 2: {
							sArrtib += "\r\nWillpower " + A.getIMindEnc()[i]
									+ "\r\n";
							break;
						}
						}
					}
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_ARMOR_MIND_ENCUMBRANCE,
							sArrtib));
					// --------------------------
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_CAT_ARMOR_EFFECTIVENESS,
							""));
					for (int i = 0; i < Constants.ARMOR_EFFECTIVENESS_STRINGS.length; i++) {
						addAttribute(new Attribute(
								Constants.ARMOR_EFFECTIVENESS_STRINGS[i], A
										.getEffectivenessValue(i)
										+ "%"));
					}
					if (A.getRequiredSkill() != -1) {
						String sHasSkill = "\\#FF0000No\\#FFFFFF";
						if (c.getPlayer().getPlayData().hasSkill(
								A.getRequiredSkill())) {
							sHasSkill = "Yes";
						}
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_WEAPON_CERT_STATUS,
								sHasSkill));
					}
				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Armor Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof TravelTicket) {
				try {
					TravelTicket T = (TravelTicket) this;

					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_CRAFTER,
							"Galactic Travel Authority"));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_TRAVEL_DEPARTURE_PLANET,
							Constants.PlanetNames[T.getDepartureInformation()
									.getDestinationPlanet()]));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_TRAVEL_DEPARTURE_POINT,
							T.getDepartureInformation().getDestinationName()));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_TRAVEL_ARRIVAL_PLANET,
							Constants.PlanetNames[T.getArrivalInformation()
									.getDestinationPlanet()]));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_TRAVEL_ARRIVAL_PLANET, T
									.getArrivalInformation()
									.getDestinationName()));
				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending TravelTicket Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof ResourceContainer) {
				try {
					ResourceContainer R = (ResourceContainer) this;
					addAttribute(new Attribute("condition", (R
							.getMaxCondition() - R.getConditionDamage())
							+ "/" + R.getMaxCondition()));
					addAttribute(new Attribute("resource_contents", (R
							.getStackQuantity())
							+ "/" + String.valueOf(Constants.MAX_STACK_SIZE)));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RESOURCE_NAME, R
									.getName()));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RESOURCE_CLASS,
							"@resource/resource_names:" + R.getResourceType()));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RESOURCE_CONTENTS,
							Constants.OBJECT_ATTRIBUTE_RESOURCE_CONTENTS));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_HIDE,
							Constants.OBJECT_ATTRIBUTE_RES_HIDE));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_BONE,
							Constants.OBJECT_ATTRIBUTE_RES_BONE));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_BULK,
							Constants.OBJECT_ATTRIBUTE_RES_BULK));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_MEAT,
							Constants.OBJECT_ATTRIBUTE_RES_MEAT));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_QUALITY,
							Constants.OBJECT_ATTRIBUTE_RES_QUALITY));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_CONDUCTIVITY,
							Constants.OBJECT_ATTRIBUTE_RES_CONDUCTIVITY));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_MALLEABILITY,
							Constants.OBJECT_ATTRIBUTE_RES_MALLEABILITY));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_TOUGHNESS,
							Constants.OBJECT_ATTRIBUTE_RES_TOUGHNESS));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE,
							Constants.OBJECT_ATTRIBUTE_RES_SHOCK_RESISTANCE));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_DECAY_RESIST,
							Constants.OBJECT_ATTRIBUTE_RES_DECAY_RESIST));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_HEAT_RESIST,
							Constants.OBJECT_ATTRIBUTE_RES_HEAT_RESIST));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_COLD_RESIST,
							Constants.OBJECT_ATTRIBUTE_RES_COLD_RESIST));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_FLAVOR,
							Constants.OBJECT_ATTRIBUTE_RES_FLAVOR));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY,
							Constants.OBJECT_ATTRIBUTE_RES_POTENTIAL_ENERGY));
				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending ResourceContainer Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof Deed) {
				Deed T = (Deed) this;
				DeedTemplate dT = DatabaseInterface.getDeedTemplateByID(T
						.getDeedTemplateID());
				ItemTemplate iT = DatabaseInterface.getTemplateDataByID(dT
						.getObject_iff_template_id());

				try {
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, Long
									.toHexString(T.getID())));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_CONDITION,
							(T.getMaxCondition() - T.getConditionDamage())
									+ " / " + T.getMaxCondition()));
					if (T.getCreatorID() == 0) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER, "System"));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, "@"
										+ T.getSTFFileName() + ":"
										+ T.getSTFFileIdentifier()));
					} else if (T.getCreatorID() >= 1) {
						String sCrafterName = "";
						Player tP = (Player) c.getServer()
								.getObjectFromAllObjects(T.getCreatorID());
						if (tP != null) {
							sCrafterName = tP.getFullName();
						}
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER,
								sCrafterName));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, T
										.getCraftedName()));
					}
					if (T.getHasSockets()) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_SOCKETS, T
										.getSocketsLeft()));
					}
					/**
					 * energy_gen_rate Energy Generation energy_maintenance
					 * Energy Maintenance examine_maintenance Surplus
					 * Maintenance examine_maintenance_rate Base Maintenance
					 * Rate examine_power Surplus Power extractrate Extraction
					 * Rate planet Planet
					 */
					// System.out.println(iT.getIFFFileName());
					if (iT.getIFFFileName().contains("building")
							|| iT.getIFFFileName().contains("installation")) {
						if (dT.getAllowedplanetslist()[0] != -1) {
							String sPlanetList = "";
							int[] pl = dT.getAllowedplanetslist();
							for (int i = 0; i < pl.length; i++) {
								sPlanetList += Constants.PlanetNames[pl[i]]
										+ "\r\n";
							}
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PLANET,
									sPlanetList));
						} else {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PLANET,
									"All\r\n"));
						}

						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_EXAMINE_MAINTENANCE_RATE,
								dT.getMaint_per_hour()));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_EXAMINE_MAINTENANCE,
								T.getISurplusMaintenance()));
						if (iT.getIFFFileName().contains("mining")
								|| iT.getIFFFileName().contains("generator")) {
							if (dT.usesPower()) {
								addAttribute(new Attribute(
										Constants.OBJECT_ATTRIBUTE_ENERGY_MAINTENANCE,
										T.getIEnergyMaintenanceRate()));
								addAttribute(new Attribute(
										Constants.OBJECT_ATTRIBUTE_POWER, T
												.getISurplusPower()));
							}
							if (iT.getIFFFileName().contains("generator")) {
								addAttribute(new Attribute(
										Constants.OBJECT_ATTRIBUTE_ENERGY_GEN_RATE,
										T.getIExtractionRate()));
							} else {
								addAttribute(new Attribute(
										Constants.OBJECT_ATTRIBUTE_EXTRACTION_RATE,
										T.getIExtractionRate()));
							}
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_HOPPERSIZE, (T
											.getIOutputHopperSize() * 100)));

						}
						if (iT.getIFFFileName().contains("farm")) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_ENERGY_MAINTENANCE,
									T.getIEnergyMaintenanceRate()));
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_POWER, T
											.getISurplusPower()));
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_EXTRACTION_RATE,
									T.getIExtractionRate()));
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_HOPPERSIZE, T
											.getIOutputHopperSize() * 100));
						}
						if (iT.getIFFFileName().contains("factory")) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_ENERGY_MAINTENANCE,
									T.getIEnergyMaintenanceRate()));
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_POWER, T
											.getISurplusPower()));
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_HOPPERSIZE, T
											.getIOutputHopperSize()));
						}
						// lots
						if (c.getPlayer().getFreeLots() < T.getILotSize()) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_QUANTITY,
									"Lots Required:\\#FF0000" + T.getILotSize()
											+ "\\#FFFFFF"));
						} else {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_QUANTITY,
									"Lots Required: " + T.getILotSize()));
						}
					}
					if (iT.getIFFFileName().contains("/deed/pet_deed/")) {
						/**
						 * @todo Put pet Deed Detail info here to be displayed
						 *       when looked at.
						 */
					} else {
						// System.out.println("Deed IFF Did not containt Building or Installation");
					}
				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Deed Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof CreaturePet) {
				CreaturePet pet = (CreaturePet) this;
				try {
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, Long
									.toHexString(pet.getID())));
					if (pet.getCreatorID() == 0) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER, "System"));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, "@"
										+ pet.getSTFFileName() + ":"
										+ pet.getSTFFileIdentifier()));
					} else if (pet.getCreatorID() >= 1) {
						String sCrafterName = "";
						Player tP = (Player) c.getServer()
								.getObjectFromAllObjects(pet.getCreatorID());
						if (tP != null) {
							sCrafterName = tP.getFullName();
						}
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER,
								sCrafterName));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, pet
										.getCraftedName()));
					}
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_PET_COMMAND, ""));
					/**
					 * sSpatialCommands[18] = null; //not used,
					 * sSpatialCommands[20] = "special2"; sSpatialCommands[21] =
					 * "ranged"; sSpatialCommands[22] = "store";//friggin
					 * oddball
					 */
					// follow me * sSpatialCommands[0] = "follow";
					byte i = 0;
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_0, pet
										.getSpatialCommands()[i]));
					}
					// stay sSpatialCommands[1] = "stay";
					i++;
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_1, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// guard sSpatialCommands[2] = "guard";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_2, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// friend sSpatialCommands[3] = "friend";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_3, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// attack sSpatialCommands[4] = "attack";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_4, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// patrol sSpatialCommands[5] = "patrol";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_5, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// get patrol point sSpatialCommands[6] = "setpatrol";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_6, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// clear patrol points sSpatialCommands[7] = "clearpatrol";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_7, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// wedge formation sSpatialCommands[8] = "formation1";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_8, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// column formation sSpatialCommands[9] = "formation2";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_9, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// transfer sSpatialCommands[10] = "transfer";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_10, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// store sSpatialCommands[11] = "release";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_11, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// trick 1 sSpatialCommands[12] = "trick1";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_12, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// trick 2 sSpatialCommands[13] = "trick2";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_13, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// trick 3 sSpatialCommands[14] = "trick3";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_14, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// trick 4 sSpatialCommands[15] = "trick4";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_15, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// group sSpatialCommands[16] = "group";
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_16, pet
										.getSpatialCommands()[i]));
					}
					i++;
					// follow other sSpatialCommands[17]
					if (pet.getVTrainedCommands().contains(i)) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND_17, pet
										.getSpatialCommands()[i]));
					}
				} catch (Exception e) {
					DataLog.logException("Exception in getAttributesBatch",
							"SOEObject(CreaturePet)",
							ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
				}
			} else if (this instanceof TangibleItem) {
				TangibleItem T = (TangibleItem) this;
				try {
					boolean isCraftingTool = T.getIFFFileName().contains(
							"tool.iff");

					if (T.isContainer()) {
						if (T.getEquippedStatus() == 4) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
						} else {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_VOLUME, T
											.getLinkedObjects().size() + 1));
						}
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CONTENTS, T
										.getLinkedObjects().size()
										+ " / 50"));

					} else {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					}
					if (isCraftingTool) {
						addAttribute(new Attribute("craft_tool_effectiveness",
								"1.0"));
						addAttribute(new Attribute("craft_tool_status",
								"@crafting:tool_status_ready"));
						addAttribute(new Attribute("crafter", "PlasmaFlow"));
						addAttribute(new Attribute("serial_number", Long
								.toHexString(this.getID())));
					} else {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, Long
										.toHexString(T.getID())));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CONDITION, (T
										.getMaxCondition() - T
										.getConditionDamage())
										+ " / " + T.getMaxCondition()));
						if (T.getCreatorID() == 0) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_CRAFTER,
									"System"));
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME,
									"@" + T.getSTFFileName() + ":"
											+ T.getSTFFileIdentifier()));
						} else if (T.getCreatorID() >= 1) {
							String sCrafterName = "";
							Player tP = (Player) c.getServer()
									.getObjectFromAllObjects(T.getCreatorID());
							if (tP != null) {
								sCrafterName = tP.getFullName();
							}
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_CRAFTER,
									sCrafterName));
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, T
											.getCraftedName()));
						}
						if (T.getHasSockets()) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_SOCKETS, T
											.getSocketsLeft()));
						}

						Vector<SkillModifier> vSKM = T.getSkillModifiers();
						if (vSKM.size() >= 1) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_ATTRIBMODS, ""));

							for (int i = 0; i < vSKM.size(); i++) {
								SkillModifier mod = vSKM.get(i);
								addAttribute(new Attribute("@stat_n:"
										+ mod.getModifierSTFString(), "+"
										+ mod.getSkillModifierValue() + ", "
										+ (mod.getDuration() / 1000) + " s"));
							}
						}
					}
				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending TangibleItem Attributes "
									+ e);
					e.printStackTrace();
				}

			} else if (this instanceof Waypoint)// Intangible Items and their
												// Children begin here
			{
				try {

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Waypoint Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof IntangibleObject) {
				try {
					IntangibleObject O = (IntangibleObject) this;
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_VOLUME, 1));
					if (O.getCreatorID() == 0) {
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER, "System"));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, "@"
										+ O.getSTFFileName() + ":"
										+ O.getSTFFileIdentifier()));
					} else if (this.CreatorID >= 1) {
						String sCrafterName = "";
						Player tP = (Player) c.getServer()
								.getObjectFromAllObjects(O.getCreatorID());
						if (tP != null) {
							sCrafterName = tP.getFullName();
						}
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTER,
								sCrafterName));
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, O
										.getCraftedName()));
					}
					if (this.getIFFFileName().contains("/intangible/pet")) {
						IntangibleObject dpIcon = (IntangibleObject) this;
						CreaturePet pet = (CreaturePet) dpIcon
								.getAssociatedCreature();
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_PET_COMMAND, ""));
						/**
						 * sSpatialCommands[18] = null; //not used,
						 * sSpatialCommands[20] = "special2";
						 * sSpatialCommands[21] = "ranged"; sSpatialCommands[22]
						 * = "store";//friggin oddball
						 */
						// follow me * sSpatialCommands[0] = "follow";
						byte i = 0;
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_0,
									pet.getSpatialCommands()[i]));
						}
						// stay sSpatialCommands[1] = "stay";
						i++;
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_1,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// guard sSpatialCommands[2] = "guard";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_2,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// friend sSpatialCommands[3] = "friend";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_3,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// attack sSpatialCommands[4] = "attack";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_4,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// patrol sSpatialCommands[5] = "patrol";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_5,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// get patrol point sSpatialCommands[6] = "setpatrol";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_6,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// clear patrol points sSpatialCommands[7] =
						// "clearpatrol";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_7,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// wedge formation sSpatialCommands[8] = "formation1";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_8,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// column formation sSpatialCommands[9] = "formation2";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_9,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// transfer sSpatialCommands[10] = "transfer";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_10,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// store sSpatialCommands[11] = "release";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_11,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// trick 1 sSpatialCommands[12] = "trick1";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_12,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// trick 2 sSpatialCommands[13] = "trick2";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_13,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// trick 3 sSpatialCommands[14] = "trick3";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_14,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// trick 4 sSpatialCommands[15] = "trick4";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_15,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// group sSpatialCommands[16] = "group";
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_16,
									pet.getSpatialCommands()[i]));
						}
						i++;
						// follow other sSpatialCommands[17]
						if (pet.getVTrainedCommands().contains(i)) {
							addAttribute(new Attribute(
									Constants.OBJECT_ATTRIBUTE_PET_COMMAND_17,
									pet.getSpatialCommands()[i]));
						}

					}

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending IntangibleObject Attributes "
									+ e);
					e.printStackTrace();
				}

			} else if (this instanceof Terminal) // NPC's and their Children
													// begin here
			{
				try {
					// Terminal T = (Terminal)this;
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_OWNER,
							"Shards of the Force"));
					// addAttribute(new
					// Attribute(Constants.OBJECT_ATTRIBUTE_CHALLENGE_LEVEL,
					// "***\\#. " + T.getFullName() + " may not be attacked.");
					// retVector.put(NewAttribute.getAttributeIndex(),
					// NewAttribute);

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Terminal Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof Vehicle) {
				try {

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Vehicle Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof Structure) {
				try {

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Structure Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof Shuttle) {
				try {

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Shuttle Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof NPC) {
				try {

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending NPC Attributes "
									+ e);
					e.printStackTrace();
				}
			} else if (this instanceof Player) {
				try {
					Player tP = (Player) this;

					addAttribute(new Attribute("bogus", "bogus"));
					if (tP == c.getPlayer()) {
						// Attribute addAttribute(new
						// Attribute(Constants.....,"Shards of the Force Dev");
						addAttribute(new Attribute(
								Constants.OBJECT_ATTRIBUTE_CHALLENGE_LEVEL,
								"@client:con_self"));
					}
				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending Player Attributes "
									+ e);
					e.printStackTrace();
				}

			} else if (this instanceof SOEObject)
			// if it does not fit the bill of any of the above we need to know
			// so were putting this here to
			// dispatch a default attribute telling us
			{
				try {
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_CRAFTED_NAME, this
									.getIFFFileName()));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, this
									.getID()));
					addAttribute(new Attribute(
							Constants.OBJECT_ATTRIBUTE_DEFAULT, "Default"));

				} catch (Exception e) {
					System.out
							.println("Exception Caught while sending SOEObject Attributes "
									+ e);
					e.printStackTrace();
				}

			}
		}
		if (retVector.size() != 0) {
			return retVector;
		}
		return vAttributes;
	}

	private int iHighestSpecialityAttribute = Constants.NUM_OBJECT_ATTRIBUTES;

	public void addAttribute(Attribute a) {
		int attributeIndex = a.getAttributeIndex();
		if (attributeIndex != -1) {
			Attribute currentAttribute = vAttributes.get(attributeIndex);
			if (currentAttribute == null) {
				vAttributes.put(attributeIndex, a);
			} else {
				currentAttribute.setAttributeValue(a.getAttributeValue());
			}
		} else {
			vAttributes.put(iHighestSpecialityAttribute, a);
			iHighestSpecialityAttribute++;
		}
	}

	/**
	 * Gets the default Radial Menu Items for this object. These radials have to
	 * account for different conditions. i/e equipped, not used , used, dead,
	 * incapped, opened , closed, etc, etc...
	 * 
	 * @return The Radial Items.
	 */
	public Hashtable<Character, RadialMenuItem> getRadialMenus(ZoneClient c) {
		Player player = c.getPlayer();
		if (player.getTutorial() != null) {
			if (player.getTutorial().isWaitingRadialEvent()) {
				player.getTutorial().setRadialEvent(true);
			}
		}

		if (iRadialCondition == -1) {
			// if a radial condition has been set to -1 is invalid we set it to
			// 0
			iRadialCondition = 0;
		}
		Hashtable<Character, RadialMenuItem> retHash = new Hashtable<Character, RadialMenuItem>();
		Vector<RadialMenuItem> V = c.getServer().getRadialMenusByCRC(
				this.getCRC());
		int retcount = 0;
		System.out.println("Searching for Radials for Item With CRC: " +  this.getCRC() + " RadialCondition " + Constants.RADIAL_CONDITION_STR[iRadialCondition]);
		
		for (int i = 0; i < V.size(); i++) {
			if (V.get(i).getiCondition() == iRadialCondition) {
				if (iRadialCondition == Constants.RADIAL_CONDITION.ITEM_DROPPED_IN_CELL.ordinal()) {
					Cell cell = (Cell)c.getServer().getObjectFromAllObjects(getCellID());
					Structure s = (Structure)c.getServer().getObjectFromAllObjects(cell.getParentID());
					if (s.isAdmin(player.getID())) {
						RadialMenuItem I = V.get(i);
						retHash.put(I.getCommandID(), I);
						retcount++;
					}
				} else {
					RadialMenuItem I = V.get(i);
					retHash.put(I.getCommandID(), I);
					retcount++;
				}
			}
		}
		if (retcount >= 1) {
			System.out.println("Returning " + retcount + " for this crc: "
					+ this.getCRC());
			return retHash;
		}
		System.out.println("Returning Default Radials");
		return vRadials;
	}

	/**
	 * Gets this Object's X location in the game world.
	 * 
	 * @return X.
	 */
	public float getX() {
		return currentX;
	}

	/**
	 * Gets this Object's Y location in the game world.
	 * 
	 * @return Y.
	 */
	public float getY() {
		return currentY;
	}

	/**
	 * Gets this Object's Z location in the game world.
	 * 
	 * @return Z.
	 */
	public float getZ() {
		return currentZ;
	}

	/**
	 * Sets this Object's X location in the game world.
	 * 
	 * @param x
	 *            -- X.
	 */
	public void setX(float x) {
		currentX = x;
	}

	/**
	 * Sets this Object's Y location in the game world.
	 * 
	 * @param y
	 *            -- Y.
	 */
	public void setY(float y) {
		currentY = y;
	}

	/**
	 * Sets this Object's Z location in the game world.
	 * 
	 * @param z
	 *            -- Z
	 */
	public void setZ(float z) {
		currentZ = z;
	}

	/**
	 * Gets this object's CRC. This is used when spawning the Object for a
	 * client.
	 * 
	 * @return The CRC.
	 */
	public int getCRC() {
		return iObjectCRC;
	}

	/**
	 * Sets this object's CRC. The CRC is used when spawning the Object for a
	 * client.
	 * 
	 * @param crc
	 *            -- The CRC.
	 */
	public void setCRC(int crc) {
		iObjectCRC = crc;
		this.iTemplateID = DatabaseInterface.getTemplateDataByCRC(crc)
				.getTemplateID();
		this.sIFFFileName = DatabaseInterface.getTemplateDataByCRC(crc)
				.getIFFFileName();
	}

	/**
	 * Gets the object's North orientation value.
	 * 
	 * @return The North orientation.
	 */
	public float getOrientationN() {
		return orientationNorth;
	}

	/**
	 * Gets the object's South orientation value.
	 * 
	 * @return The South orientation.
	 */
	public float getOrientationS() {
		return orientationSouth;
	}

	public float getOrientationE() {
		return orientationEast;
	}

	public float getOrientationW() {
		return orientationWest;
	}

	/**
	 * Sets the object's North orientation value.
	 * 
	 * @param oN
	 *            -- The North orientation.
	 */
	public void setOrientationN(float oN) {
		orientationNorth = oN;
	}

	/**
	 * Sets the object's South orientation value.
	 * 
	 * @param oS
	 *            -- The South orientation.
	 */
	public void setOrientationS(float oS) {
		orientationSouth = oS;
	}

	public void setOrientationE(float oE) {
		orientationEast = oE;
	}

	public void setOrientationW(float oW) {
		orientationWest = oW;
	}

	/**
	 * Sets the iff filename for this object. The iff file contains all of the
	 * data the client needs to spawn an object.
	 * 
	 * @param f
	 *            -- The iff filename.
	 */
	public void setIFFFileName(String f) {
		sIFFFileName = f;
		int iCRC = PacketUtils.SWGCrc(f);
		setCRC(iCRC);
		if (this.iObjectCRC == 0) {
			this.iObjectCRC = DatabaseInterface.getTemplateDataByFilename(
					sIFFFileName).getCRC();
		}
		this.iTemplateID = DatabaseInterface.getTemplateDataByFilename(
				sIFFFileName).getTemplateID();
	}

	/**
	 * Gets the iff filename for this object. The iff file contains all of the
	 * data the client needs to spawn an object.
	 * 
	 * @return The iff filename.
	 */
	public String getIFFFileName() {
		return sIFFFileName;
	}

	/**
	 * Sets what planet this object is spawned on.
	 * 
	 * @param i
	 *            -- The planet ID.
	 */
	public void setPlanetID(int i) {
		iPlanetID = i;
	}

	/**
	 * Gets what planet this object is spawned on.
	 * 
	 * @return The planet ID.
	 */
	public int getPlanetID() {
		return iPlanetID;
	}

	/**
	 * Gets what Cell this object is currently occupying.
	 * 
	 * @return The Cell ID, or -1 if this object is not in a cell.
	 */
	public long getCellID() {
		return lCellID;
	}

	/**
	 * Sets what cell this object is currently occupying.
	 * 
	 * @param ID
	 *            -- The Cell ID.
	 */
	protected void setCellID(long ID) {
		/*
		 * long newCell = ID; long oldCell = lCellID; if(lCellID != ID) {
		 * if(this instanceof Player) { Player p = (Player)this;
		 * if(p.getClient()!=null) {
		 * System.out.println("Set Cell ID Called Old CellID " + lCellID +
		 * " New Cell ID " + ID); StackTraceElement[] elements =
		 * Thread.currentThread().getStackTrace(); for(int i=0;
		 * i<elements.length;i++) { System.out.println("E:" + i + "|" +
		 * elements[i].toString()); } } } }
		 */
		lCellID = ID;
	}

	/**
	 * Gets this object's relative X position inside it's currently occupied
	 * cell.
	 * 
	 * @return The relative X.
	 */
	public float getCellX() {
		// System.out.println("Returning Cell Coord X: " + currentCellX);
		return currentCellX;
	}

	/**
	 * Gets this object's relative Y position inside it's currently occupied
	 * cell.
	 * 
	 * @return The relative Y.
	 */
	public float getCellY() {
		// System.out.println("Returning Cell Coord Y: " + currentCellY);
		return currentCellY;
	}

	/**
	 * Gets this object's relative Z position inside it's currently occupied
	 * cell.
	 * 
	 * @return The relative Z.
	 */
	public float getCellZ() {
		// System.out.println("Returning Cell Coord Z: " + currentCellZ);
		return currentCellZ;
	}

	/**
	 * Sets this object's relative X position inside it's currently occupied
	 * cell.
	 * 
	 * @param x
	 *            -- The relative X.
	 */
	public void setCellX(float x) {
		// System.out.println("Setting Cell Coord X To: " + x);
		currentCellX = x;
	}

	/**
	 * Sets this object's relative Y position inside it's currently occupied
	 * cell.
	 * 
	 * @param y
	 *            -- The relative Y.
	 */
	public void setCellY(float y) {
		// System.out.println("Setting Cell Coord Y To: " + y);
		currentCellY = y;
	}

	/**
	 * Sets this object's relative Z position inside it's currently occupied
	 * cell.
	 * 
	 * @param z
	 *            -- The relative Z.
	 */
	public void setCellZ(float z) {
		// System.out.println("Setting Cell Coord Z To: " + z);
		currentCellZ = z;
	}

	/**
	 * Sets the object currently equipped in the given slot ID.
	 * 
	 * @param object
	 *            -- The object equipped.
	 * @param slotID
	 *            -- The slot ID in which the object is equipped.
	 */
	public void setEquippedItemInSlot(SOEObject object, byte slotID) {
		if (vEquippedItems == null) {
			vEquippedItems = new Vector<SOEObject>();
		}
		vEquippedItems.add(object);
	}

	/**
	 * Removes the currently equipped object from the given slot ID.
	 * 
	 * @param slotID
	 *            -- The slot ID.
	 */
	public void removeEquippedItemFromSlot(byte slotID) {
		for (int i = 0; i < vEquippedItems.size(); i++) {
			if (vEquippedItems.get(i).getSlotID() == slotID) {
				vEquippedItems.remove(i);
			}
		}
	}

	/**
	 * Finds if the given slot ID has an object equipped or not.
	 * 
	 * @param slot
	 *            -- The slot ID.
	 * @return True if the slot ID is taken, otherwise false.
	 */
	public boolean isSlotTaken(byte slot) {
		for (int i = 0; i < vEquippedItems.size(); i++) {
			if (vEquippedItems.elementAt(i).getSlotID() == slot) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the object currently equipped in the given slot ID.
	 * 
	 * @param slot
	 *            -- The slot ID.
	 * @return The object in the slot ID, or null of no object is equipped
	 *         there.
	 */
	public SOEObject getObjectInSlot(byte slot) {
		for (int i = 0; i < vEquippedItems.size(); i++) {
			if (vEquippedItems.elementAt(i).getSlotID() == slot) {
				return vEquippedItems.elementAt(i);
			}
		}
		return null;
	}

	/**
	 * Gets the slot ID this object takes when equipped by a Player.
	 * 
	 * @return The slot ID.
	 */
	public int getSlotID() {
		return slotID;
	}

	protected void setTemplateID(int iTemplateID) {
		try {
			this.iTemplateID = iTemplateID;
			this.sIFFFileName = DatabaseInterface.getTemplateDataByID(
					iTemplateID).getIFFFileName();
			this.iObjectCRC = DatabaseInterface
					.getTemplateDataByID(iTemplateID).getCRC();
		} catch (Exception e) {
			DataLog.logException(
					"Exception while attempting to set template Data, TID: "
							+ iTemplateID, "SOEObject",
					ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
		}
	}

	protected int getTemplateID() {
		return iTemplateID;
	}

	protected void useItemByCommandID(ZoneClient client, byte commandID) {
		try {
			int iTemplateID = getTemplateID();
			switch (iTemplateID) {
			default: {
				client
						.insertPacket(PacketFactory
								.buildChatSystemMessage("This TangibleItem is not handled yet."));
				// client.insertPacket(PacketFactory.buildChatSystemMessage("Item Name: "
				// + this.getName()));
				client.insertPacket(PacketFactory
						.buildChatSystemMessage("Item IFF Name: "
								+ getIFFFileName()));
				client.insertPacket(PacketFactory
						.buildChatSystemMessage("Item CRC: " + getCRC()));
				client.insertPacket(PacketFactory
						.buildChatSystemMessage("Item Template ID: "
								+ iTemplateID));
				client.insertPacket(PacketFactory
						.buildChatSystemMessage("Command ID: "
								+ (int) commandID));
				System.out.println("This TangibleItem is not handled yet.");
				// System.out.println("Item Name: " + this.getName());
				System.out.println("Item IFF Name: " + getIFFFileName());
				System.out.println("Item CRC: " + getCRC());
				System.out.println("Item Template ID: " + iTemplateID);

			}
			}
		} catch (Exception e) {
			System.out.println("Exception caught in useItemByCommandID " + e);
			e.printStackTrace();
		}
	}

	protected void useItem(ZoneClient client) {
		try {
			client
					.insertPacket(PacketFactory
							.buildChatSystemMessage("Received use item request on object with ID "
									+ getID()));
		} catch (Exception e) {

		}
	}

	protected void setConditionFlag(int c) {
		iConditionFlag = c;
	}

	protected int getConditionFlag() {
		return iConditionFlag;
	}

	/**
	 * Adds the given bit to the Player's pvp status bitmask.
	 * 
	 * @param status
	 *            -- The status bit to add.
	 */
	public void addBitToPVPStatus(int status) {
		iPVPStatus |= status;
		iAttackedPVPStatus |= status;
	}

	public void setPVPStatus(int bitmask) {
		iPVPStatus = bitmask;
		iAttackedPVPStatus = bitmask | Constants.PVP_STATUS_IS_HOSTILE;
	}

	/**
	 * Removes the given bit from the Player's pvp status bitmask. If the player
	 * does not have the given status bit, the function does nothing.
	 * 
	 * @param status
	 *            -- The status bit to remove.
	 */
	public void removeBitFromPVPStatus(int status) {
		iPVPStatus = iPVPStatus & ~status;
		iAttackedPVPStatus = iPVPStatus | Constants.PVP_STATUS_IS_HOSTILE;
	}

	/**
	 * Gets the pvp status bitmask.
	 * 
	 * @return The pvp status bitmask.
	 */
	public int getPVPStatus() {
		return iPVPStatus;
	}

	public int getAttackedPVPStatus() {
		return iAttackedPVPStatus;
	}
	/**
	 * Gets the faction CRC of the player.
	 * 
	 * @return -- The faction crc.
	 */
	public int getFactionCRC() {
		return iFactionCRC;
	}

	public int getFactionID() {
		return iFactionID;
	}

	private int iFactionID = -1;

	/**
	 * Sets the Player's faction ID, based on the faction index.
	 * 
	 * @param i
	 *            -- The faction ID index.
	 */
	protected void setFactionID(int i) {
		iFactionID = i;
		if (i < 0 || i > Constants.FACTIONS.length) {

		} else {
			iFactionCRC = Constants.FACTIONS[i];
		}
	}

	protected void setFactionRank(byte i) {
		iFactionRank = i;
	}

	protected byte getFactionRank() {
		return iFactionRank;
	}

	protected void setIsStaticObject(boolean b) {
		bIsStaticObject = b;
	}

	protected boolean getIsStaticObject() {
		return bIsStaticObject;
	}

	protected void setOneClickUse(boolean b) {
		bOneClickUse = b;
	}

	protected boolean getOneClickUse() {
		return bOneClickUse;
	}

	protected String getChallengeLevelString(Player p) {

		/*
		 * this is supposed to be what we send. But for now until we figure out
		 * the %TT we need to send these strings.
		 * 
		 * @client: 8 con_1 ***\#. %TT is no match for you. 9 con_2 ***\#. %TT
		 * looks weak to you. 10 con_3 ***\#. %TT looks like an even match. 11
		 * con_4 ***\#. %TT looks tough. 12 con_5 ***\#. %TT looks extremely
		 * tough. 13 con_6 ***\#. %TT looks like instant death.
		 */
		int level = 0;
		String retval = "";
		if (this instanceof NPC) {
			NPC o = (NPC) this;

			if (o != null) {
				if (o.getCREO3Bitmask() != 0x108) {
					int cona, conb;
					cona = o.getMaxHam()[0];
					conb = p.getMaxHam()[0];
					level = ((conb) / (cona) * 100);
				}
			}
			if (level <= 0) // white
			{
				retval = "***\\#. " + o.getFullName() + " may not be attacked.";
			} else if (level >= 1 && level <= 20) // gray
			{
				retval = "***\\#. " + o.getFullName() + " is no match for you.";
			} else if (level >= 21 && level <= 40) // green
			{
				retval = "***\\#. " + o.getFullName() + " looks weak to you.";
			} else if (level >= 41 && level <= 60) // blue
			{
				retval = "***\\#. " + o.getFullName()
						+ " looks like an even match.";
			} else if (level >= 61 && level <= 80) // yellow
			{
				retval = "***\\#. " + o.getFullName() + " looks tough.";
			} else if (level >= 81 && level <= 100) // red
			{
				retval = "***\\#. " + o.getFullName()
						+ " looks extremely tough.";
			} else if (level >= 101) // Purple
			{
				retval = "***\\#. " + o.getFullName() + " like instant death.";
			}
		} else {
			retval = "***\\#. may not be attacked";
		}
		return retval;
	}

	protected float calculateAngleToTarget(Player player, long TargetID) {
		SOEObject t = player.getClient().getServer().getObjectFromAllObjects(
				TargetID);

		float x1 = t.getX();
		float y1 = t.getY();

		float x2 = player.getX();
		float y2 = player.getY();

		float dx = x2 - x1;
		float dy = y2 - y1;
		double angle = 0.0d;

		// Calculate angle
		if (dx == 0.0) {
			if (dy == 0.0) {
				angle = 0.0;
			} else if (dy > 0.0) {
				angle = Math.PI / 2.0;
			} else {
				angle = Math.PI * 3.0 / 2.0;
			}
		} else if (dy == 0.0) {
			if (dx > 0.0) {
				angle = 0.0;
			} else {
				angle = Math.PI;
			}
		} else {
			if (dx < 0.0) {
				angle = Math.atan(dy / dx) + Math.PI;
			} else if (dy < 0.0) {
				angle = Math.atan(dy / dx) + (2 * Math.PI);
			} else {
				angle = Math.atan(dy / dx);
			}
		}

		// Convert to degrees
		angle = angle * 180 / Math.PI;

		return (float) angle;
	}

	protected static float degreesToRadians(float Degrees) {
		return (float) ((Math.PI / 180) * Degrees);
	}

	protected static float radiansToDegrees(float Radians) {
		return (float) (180 / Math.PI) * Radians;
	}

	protected static float absoluteBearingRadians(SOEObject oC, SOEObject oT) {
		return (float) degreesToRadians(absoluteBearingDegrees(oC, oT));
	}

	protected static float absoluteBearingDegrees(SOEObject oC, SOEObject oT) {
		// this is the Target
		double x1 = 0;
		double y1 = 0;

		// This is the Center -- me.
		double x2 = 0;
		double y2 = 0;

		if (oC.getCellID() == 0) {
			x1 = (double) oT.getX();
			y1 = (double) oT.getY();
			x2 = (double) oC.getX();
			y2 = (double) oC.getY();
		} else if (oC.getCellID() >= 1) {
			x1 = (double) oT.getCellX();
			y1 = (double) oT.getCellY();
			x2 = (double) oC.getCellX();
			y2 = (double) oC.getCellY();
		}

		double xo = x1 - x2;
		double yo = y1 - y2;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-right
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-left
			bearing = 180 - arcSin; // arcsin is negative here, actuall 360 -
									// ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-right
			bearing = arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-left
			bearing = 360 + arcSin; // arcsin is negative here, actually 180 +
									// ang
		}
		return (float) bearing;
	}

	protected static float absoluteBearingRadians(Player sourcePlayer,
			Player targetPlayer) {
		return (float) degreesToRadians(absoluteBearingDegrees(sourcePlayer,
				targetPlayer));
	}

	protected void updateAngle(ZoneClient c) {
		try {
			Vector<Player> PL = c.getServer().getPlayersAroundObject(
					(SOEObject) this, false);
			SOEObject Parent = c.getServer().getObjectFromAllObjects(
					this.getCellID());
			for (int i = 0; i < PL.size(); i++) {
				if (this.getCellID() >= 1) {
					/**
					 * @todo - Incell NPC Pos Update Angle.
					 */
					PL.get(i).getClient().insertPacket(
							PacketFactory.buildNPCUpdateCellTransformMessage(
									(NPC) this, Parent));
					System.out
							.println("TODO: When an npc tries to face you in cell it warps to 0 0 in the building, in cell upd is disabled");
				} else {
					PL
							.get(i)
							.getClient()
							.insertPacket(
									PacketFactory
											.buildNPCUpdateTransformMessage((NPC) this));
				}
			}
		} catch (Exception e) {
			System.out
					.println("Exception Caught while updating Angle of Object "
							+ e);
			e.printStackTrace();
		}
	}

	protected void setParentID(long id) {
		lParentID = id;
	}

	protected long getParentID() {
		return lParentID;
	}
	protected void setRadialCondition(int condition) {
		iRadialCondition = condition; 
	}

	protected int getRadialCondition() {
		return iRadialCondition;
	}

	public boolean isBIsBuilding() {
		return bIsBuilding;
	}

	public void setIsBuilding(boolean bIsBuilding) {
		this.bIsBuilding = bIsBuilding;
	}

	public int getIMovementCounter() {
		return iMovementCounter;
	}

	public int incrementMovementCounter() {
		iMovementCounter++;
		return iMovementCounter;
	}

	public String getSObjectTitle() {
		return sObjectTitle;
	}

	public void setSObjectTitle(String sObjectTitle) {
		this.sObjectTitle = sObjectTitle;
	}

	public byte getDelayedSpawnAction() {
		return delayedSpawnAction;
	}

	public void setDelayedSpawnAction(byte delayedSpawnAction) {
		this.delayedSpawnAction = delayedSpawnAction;
	}

	protected void setMovementAngle(float angle) {
		fMovementAngle = angle;
	}

	/**
	 * Unknown function.
	 * 
	 * @return
	 */
	public float getMovementAngle() {
		// See PlasmaFlow's core for figuring this value out.
		return fMovementAngle;
	}

	/**
	 * Auto-increments and returns the current movement update counter.
	 * 
	 * @return The movement update counter.
	 */
	public int getMoveUpdateCount() {
		// Get the value, and auto increment it.
		iMovementUpdateCounter++;
		return iMovementUpdateCounter;
	}

	public int getIObjectUpdateCounter(boolean bIncrement) {
		if (bIncrement) {
			iObjectUpdateCounter++;
		}
		return iObjectUpdateCounter;
	}

	public void setIObjectUpdateCounter(int iObjectUpdateCounter) {
		this.iObjectUpdateCounter = iObjectUpdateCounter;
	}

	public boolean isTerminal() {
		return isTerminal;
	}

	public void setIsTerminal(boolean isTerminal) {
		this.isTerminal = isTerminal;
	}

	protected void addSynchListener(ZoneClient client) {
		if (vSynchListeners == null) {
			vSynchListeners = new Vector<ZoneClient>();
		}
		if (!vSynchListeners.contains(client)) {
			vSynchListeners.add(client);
		}
	}

	protected void removeSynchListener(ZoneClient client) {
		if (vSynchListeners == null) {
			vSynchListeners = new Vector<ZoneClient>();
		}
		if (vSynchListeners.contains(client)) {
			vSynchListeners.remove(client);
		}
	}

	protected void addPetFollowingObject(CreaturePet pet) {
		if (vPetsFollowingObject == null) {
			vPetsFollowingObject = new Vector<CreaturePet>();
		}
		if (!vPetsFollowingObject.contains(pet)) {
			vPetsFollowingObject.add(pet);
		}
	}

	protected void removePetFollowingObject(CreaturePet pet) {
		if (vPetsFollowingObject == null) {
			vPetsFollowingObject = new Vector<CreaturePet>();
		}
		if (vPetsFollowingObject.contains(pet)) {
			vPetsFollowingObject.remove(pet);
		}
	}

	protected Vector<CreaturePet> getPetsFollowingObject() {
		if (vPetsFollowingObject == null) {
			vPetsFollowingObject = new Vector<CreaturePet>();
		}
		return vPetsFollowingObject;
	}

	public boolean canBePickedUp() {
		return bCanBePickedUp;
	}

	public void setCanBePickedUp(boolean bCanBePickedUp) {
		this.bCanBePickedUp = bCanBePickedUp;
	}

	protected Attribute getAttributeByIndex(int index) {
		return vAttributes.get(index);
	}
	public void setSerialNumber(long lSerialNumber, boolean bDisplaySerial) {
		this.lSerialNumber = lSerialNumber;
		addAttribute(new Attribute(Constants.OBJECT_ATTRIBUTE_SERIAL_NUMBER, Long.toHexString(lSerialNumber)));
	}

	public long getSerialNumber() {
		return lSerialNumber;
	}
	
	/**
	 * Compares this SOEObject with another SOEObject based on their Object ID.  An object with a higher object ID will be "greater" than one with a lower object ID.
	 * @param o -- The object this object is being compared to.
	 */
	public int compareTo(SOEObject o) {
		long tarObjectID = o.getID();
		if (objectID > tarObjectID) {
			return 1;
		} else if (objectID < tarObjectID) {
			return -1;
		} else {
			return 0;
		}
	}

	public boolean equals(SOEObject o) {
		return (o.getID() == getID());
	}
}

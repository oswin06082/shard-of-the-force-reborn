import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;

/**
 * The PlayerItem object handles all of the Player data that only the Player can see.  Waypoint data, information
 * on skills, skillmods, friends list, ignore list, etc. is handled by the PlayerItem.
 * @author Darryl
 *
 */
public class PlayerItem extends SOEObject implements Serializable {
	public final static long serialVersionUID = 1;
	private int iCurrentForce = 99999;
	private int iMaxForce = 99999;
	private long iForceUpdateKKMS = 1;
	private int iJediFlag = 4;
	private int iDrinkFullness = 0;
	private int iFoodFullness = 0;
	private int iCurrentLanguage = 0;
	private int iStatusBitmask = 0x80000000;

	private String sTitle;
	
	private Vector<Waypoint> vWaypointList;
	//private BitSet vCertificationsList; // TODO:  Convert to bitset. -- Doesn't even need to exist.
	private Hashtable<Integer, PlayerExperience> vExperienceList;
	private Vector<PlayerFriends> vFriendsList; // This is convenient...
	private Vector<PlayerFriends> vIgnoreList; // This is convenient
	private BitSet vSchematicList; 
	private BitSet skillsBits;
	private int[] badgeBits; // Can stay as an int array, or could become a BitSet.
	private Player myOwner;
	private long lMarriedPlayerID;
	private byte iCsrOrDeveloperFlag = 0;
    private Structure residence; // Not needed here.  Also, could be a long residenceID.
    private MapLocationData cloneBindLocation;
    
    private transient int iNumExperimentationPoints = 0;
    private transient int iCraftingExperimentationAndManufactureFlag = 0;
    private transient int craftingStage = 0;
	private transient int iWaypointListUpdateCount = 0;
	private transient int iExperienceListUpdateCount = 0;
	private transient int iSchematicListUpdateCount = 0;
	private transient int iFriendsListUpdateCount = 0;
	private transient int iIgnoreListUpdateCount = 0;
	private transient int iCertificationsListUpdateCount = 0;
	private transient long[] lIncapacitationTimers;
	private transient ResourceContainer lastUpdatedResourceContainer = null;
	private transient TangibleItem nearbyCraftingStation = null;
	
    /**
	 * Constructs a new PlayerItem for the given Player.
	 * @param p -- The Player
	 */
	public PlayerItem(Player p) {
        //System.out.println("Creating PlayerItem Object");
		myOwner = p;
		sTitle = "";
		vWaypointList = new Vector<Waypoint>();
		vExperienceList = new Hashtable<Integer, PlayerExperience>();
		setIFFFileName("object/player/shared_player.iff");
		setCRC(PacketUtils.SWGCrc(getIFFFileName()));
		vFriendsList = new Vector<PlayerFriends>();
		vIgnoreList = new Vector<PlayerFriends>();
		vSchematicList = new BitSet();
		skillsBits = new BitSet();
		badgeBits = new int[15];
		setID(p.getServer().getNextObjectID());
		p.getServer().addObjectToAllObjects(this, false,false);
		p.setPlayData(this);
		addBitToPVPStatus(Constants.PVP_STATUS_IS_PLAYER);
		lIncapacitationTimers = new long[3];
		cloneBindLocation = null;
	}
	
	/**
	 * Main update function for the PlayerItem.
	 * @param lDeltaTimeMS -- The period of time passed, in milliseconds, since the PlayerItem was last updated.
	 */
	public void update(long lDeltaTimeMS) {
		if (lIncapacitationTimers == null) {
			lIncapacitationTimers = new long[3];
		}
		for (int i = 0; i < lIncapacitationTimers.length; i++) {
			lIncapacitationTimers[i] = Math.max(0, lIncapacitationTimers[i] - lDeltaTimeMS);
		}
		// TODO:  Force regeneration needs to be based on the Force Power mod, not the player's max force.
		if (iMaxForce > 0) {
			if (iCurrentForce < iMaxForce) {
				long lUpdateForceValueKK = iForceUpdateKKMS * lDeltaTimeMS;
				long lUpdateForceValue = PacketUtils.unK(PacketUtils.unKRound(lUpdateForceValueKK));
				iCurrentForce += lUpdateForceValue;
			}
		}
	}
	
	/**
	 * Sets the skill status for this Player.
	 * @param skillID -- The Skill ID we are setting.
	 * @param bGainingSkill -- Indicates if we are gaining or losing this skill.
	 */
	protected void setSkill(int skillID, boolean bGainingSkill, boolean updateZone) {
		ZoneClient client = myOwner.getClient();
		ZoneServer server = client.getServer();
		Skills s = client.getServer().getSkillFromIndex(skillID);
		Vector<SkillMods> vPlayerMods = myOwner.getSkillModsList();
		Vector<SkillMods> vSkillMods = server.getSkillModsFromSkillIndex(skillID);
		//System.out.println("Got skill mods for skill index " + s.getSkillID() + ", name " + s.getName());
		for (int i = 0; i < vSkillMods.size(); i++) {
			System.out.println("Skill " + s.getName() + "has mod " + i + " " + vSkillMods.elementAt(i).getName() + " with value " + vSkillMods.elementAt(i).getSkillModModdedValue());
		}
		Vector<CraftingSchematic> vSchematics = DatabaseInterface.getAllSchematicsForSkill(s.getSkillID());
		if (bGainingSkill) {
			System.out.println("Learn skill " + s.getName());
			if (hasSkill(s.getSkillID())) {
				System.out.println("But I already know this skill!");
			}
			if (!hasSkill(s.getSkillID())) {
				skillsBits.set(skillID, bGainingSkill);
				// Set the experience cap.
				if (s.getIsNoviceSkill()) { 
					// We also need to get the "profession skill" for this skill.
					
					if (s.getPointsCost() == 15 && s.getSkillID() < 100) {
						setSkill(skillID - 2, true, updateZone);
					}
					setSkill(skillID - 1, true, updateZone);
					PlayerExperience exp;
					Skills treeSkill;
					int treeExperienceType = 0;
					int treeExperienceCap = 0;
					String experienceName;
					// Set up the XP ratings and skill caps.
					for (int i = skillID + 2; i <= skillID + 14; i += 4) {
						treeSkill = server.getSkillFromIndex(i);
						treeExperienceType = treeSkill.getExperienceType();
						if (treeExperienceType > 0) {
							treeExperienceCap = treeSkill.getExperienceCost() * 2;
							//System.out.println("treeExperienceCap = " + treeExperienceCap);
							exp = vExperienceList.get(treeExperienceType);
							experienceName = DatabaseInterface.getExperienceNameFromID(treeExperienceType);
							if (exp == null) {
								exp = new PlayerExperience(treeExperienceType, experienceName);
								vExperienceList.put(treeExperienceType, exp);
							}
							exp.setMaxExperience(treeExperienceCap);
							//System.out.println(treeSkill.getName() + " caps " + experienceName + " at " + exp.getMaxExperience());
						} 
					}
				} else {
					int experienceCap = s.getExperienceCap();
					int experienceType = s.getExperienceType();
					
					if (experienceType != -1) {
						PlayerExperience exp = vExperienceList.get(experienceType);
						if (exp != null) {
							exp.setMaxExperience(experienceCap);
						} else {
							String sExperienceName = DatabaseInterface.getExperienceNameFromID(experienceType);
							if (sExperienceName != null) {
								exp = new PlayerExperience(experienceType, sExperienceName);
								exp.setMaxExperience(experienceCap);
								vExperienceList.put(experienceType, exp);
							} // else this is an experienceless skill (IE language or some novice skill.
						}
					}
				}
				skillPointsLeft -= s.getPointsCost();
				if (vSchematics != null) {
					for (int i = 0; i < vSchematics.size(); i++) {
						CraftingSchematic schematic = vSchematics.elementAt(i);
						vSchematicList.set(schematic.getIndex(), true);
					}
				}
				try {
					//System.out.println("Adding skill " + s.getName());
					if (updateZone) {
						client.insertPacket(PacketFactory.buildSkillsDelta(myOwner, s.getName(), (byte)1), Constants.PACKET_RANGE_CHAT_RANGE);
					}
					if (vSkillMods != null) {
						if (!vSkillMods.isEmpty()) {
							for (int i = 0; i < vSkillMods.size(); i++) {
								SkillMods mod = vSkillMods.elementAt(i);
								boolean bFound = false;
								for (int j = 0; j < vPlayerMods.size() && !bFound; j++) {
									SkillMods playerMod = vPlayerMods.elementAt(j);
									if (mod.getName().equals(playerMod.getName())) {
										if (mod.getName().contains("language")) {
											System.out.println("Incrementing "+ mod.getName() + " by " + mod.getSkillModModdedValue() + " for skill " + s.getName());
										}
										myOwner.increaseSkillModValue(mod.getName(), mod.getSkillModModdedValue(), updateZone);
										bFound = true;
									}
								}
								if (!bFound) {
									System.out.println("Adding skill mod " + mod.getName());
									myOwner.addSkillMod(mod, updateZone);
								}
							}
						} 
					} 
					if (updateZone) {
						client.insertPacket(PacketFactory.buildCertificationsDelta(this, s.getCertificationList(), (byte)1));
					}
					if (vSchematics != null) {
						if (!vSchematics.isEmpty()) {
							if (updateZone) {
									client.insertPacket(PacketFactory.buildDraftSchematicsDelta(this, vSchematics, 1, false)); // Reset the list.
							}
						}
					}
				} catch (Exception e) {
					System.out.println("Error building deltas messages: " + e.toString());
					e.printStackTrace();
				}
			}
		} else {
			if (hasSkill(skillID)) {
				skillsBits.set(skillID, bGainingSkill);
				skillPointsLeft += s.getPointsCost();
				boolean needSchematicUpdate = false;
				if (vSchematics != null) {
					for (int i = 0; i < vSchematics.size(); i++) {
						CraftingSchematic schematic = vSchematics.elementAt(i);
						Vector<Integer> vSkillIDForSchematic = schematic.getRequiredSkillID();
						boolean bStillCanUseSchematic = false;
						for (int j = 0; j < vSkillIDForSchematic.size() && !bStillCanUseSchematic; j++) {
							bStillCanUseSchematic = hasSkill(vSkillIDForSchematic.elementAt(j));
						}
						if (vSchematicList.get(schematic.getIndex()) && !bStillCanUseSchematic) {
							vSchematicList.set(schematic.getIndex(), false);
							needSchematicUpdate = true;
						}
					}
				}
				try {
					if (updateZone) {
						client.insertPacket(PacketFactory.buildSkillsDelta(myOwner, s.getName(), (byte)0), Constants.PACKET_RANGE_CHAT_RANGE);
					}
					if (updateZone) {
						client.insertPacket(PacketFactory.buildCertificationsDelta(this, s.getCertificationList()));
					}
					if (updateZone && needSchematicUpdate) {
						Vector<CraftingSchematic> vNewSchematicList = new Vector<CraftingSchematic>();
						for (int i = vSchematicList.nextSetBit(0); i >= 0; i = vSchematicList.nextSetBit(i+1)) {
							vNewSchematicList.add(DatabaseInterface.getSchematicByIndex(i));
						}
						client.insertPacket(PacketFactory.buildDraftSchematicsDelta(this, vNewSchematicList, 1, true)); // Reset the list.
					}
					if (vSkillMods != null) {
						if (!vSkillMods.isEmpty()) {
							for (int i = 0; i < vSkillMods.size(); i++) {
								SkillMods mod = vSkillMods.elementAt(i);
								boolean bFound = false;
								for (int j = 0; j < vPlayerMods.size() && !bFound; j++) {
									SkillMods playerMod = vPlayerMods.elementAt(j);
									if (mod.getName().equals(playerMod.getName())) {
										myOwner.increaseSkillModValue(mod.getName(), -mod.getSkillModModdedValue(), updateZone);
										bFound = true;
									}
								}
							}
						} 
					} 
				} catch (Exception e) {
					System.out.println("Error building deltas messages: " + e.toString());
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Gets if we have this skill or not.
	 * @param skillID -- The skill ID we are checking.
	 * @return true if we have the skill, otherwise returns false.
	 */
	protected boolean hasSkill(int skillID) {
		return skillsBits.get(skillID);
	}
	
	/**
	 * Adds a waypoint to the list of Waypoints. 
	 * @param w -- The Waypoint.
	 */
	protected void addWaypoint(Waypoint w) {
		vWaypointList.add(w);
	}

	protected boolean deleteWaypoint(Waypoint w){
		return vWaypointList.remove(w);
	}
	
	protected int getWaypointListSize() {
		return vWaypointList.size();
	}
        
	/**
	 * Gets the Friends list.
	 * @return The Friends list.
	 */
	protected Vector<PlayerFriends> getFriendsList() {
            if(vFriendsList==null)
            {
                vFriendsList = new Vector<PlayerFriends>();
            }
            return vFriendsList;
	}
	
	/**
	 * Gets the Ignore list.
	 * @return The Ignore List.
	 */
	protected Vector<PlayerFriends> getIgnoreList() {
            if(vIgnoreList==null)
            {
                vIgnoreList = new Vector<PlayerFriends>();
            }
            return vIgnoreList;
	}
	
	
	protected int getCertificationsListUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iCertificationsListUpdateCount++;
		}
		return iCertificationsListUpdateCount;
	}
	protected void setCertificationsListUpdateCount(int count) {
		iCertificationsListUpdateCount = count;
	}
	
	/**
	 * Gets the Player's title.
	 * @return The Player's title.
	 */
	public String getTitle() {
		return sTitle;
	}
	
    public byte[] setTitle(String sTitle, boolean bSendDelta){
        this.sTitle = sTitle;
        try{
            if(bSendDelta)
            {
                return PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)3, (short)1, (short)7, this, sTitle, false);
            }
        }catch(Exception e){
            DataLog.logException("Error While Setting Player Title", "PlayerItem", ZoneServer.ZoneRunOptions.bLogToConsole, true, e);
        }
        return new byte [0];
    }

	/**
	 * Gets the list of Waypoints.
	 * @return The Player's waypoints.
	 */
	public Vector<Waypoint> getWaypoints() {
		return vWaypointList;
	}
	
	/**
	 * Gets the list of Experience values for the Player.
	 * @return The Experience list.
	 */
	public Hashtable<Integer, PlayerExperience> getExperienceList() {
		return vExperienceList;
	}

	/**
	 * Updates the value of a given Experience for the Player.
	 * @param iExperienceType -- The Experience type being updated.
	 * @param iExperienceGained -- The amount of experience gained.
	 */
	public void updateExperience(Skills s, int iExperienceType, int iExperienceGained) {
		// TODO:  Ensure that we do not "overflow" the experience beyond the maximum that they are allowed to know.
		PlayerExperience exp = vExperienceList.get(iExperienceType);
		if (exp != null) {
			if (s != null) {
				exp.setMaxExperience(s.getExperienceCap());
			}
			int iPreviousExperience = exp.getCurrentExperience();
			int iNewExperience = exp.setCurrentExperience(exp.getCurrentExperience() + iExperienceGained);
			int iExpCap = exp.getMaxExperience();
			
			if (iNewExperience != 0) {
				if (iNewExperience != iPreviousExperience) 
                {
                    myOwner.getClient().insertPacket(PacketFactory.buildExperienceDelta(this, exp, Constants.DELTA_UPDATING_ITEM));
                    String sExpString = "";
                    int iExpAmount = Math.abs(iExperienceGained);
                    if (iExperienceGained < 0) 
                    {                                 
                        //prose_revoke_xp	You lost %DI %TO experience points.
                        //prose_revoke_xp1	You lost %DI %TO experience point.
                        if(iExpAmount >= 2)
                        {
                            sExpString  = "prose_revoke_xp";
                        }
                        else
                        {
                            sExpString  = "prose_revoke_xp1";
                        }                                            
                    } 
                    else 
                    {
                        //prose_grant_xp	You earned %DI %TO experience points.
                        //prose_grant_xp1	You earned %DI %TO experience point.
                        if(iExpAmount >= 2)
                        {
                            sExpString  = "prose_grant_xp";
                        }
                        else
                        {
                            sExpString  = "prose_grant_xp1";
                        }                                            
                    }
                    try {    
                    	  System.out.println("Sending Experience Message. 1");
                          if (iNewExperience == iExpCap && iPreviousExperience != iExpCap) 
                          {
                              System.out.println("Sending Experience Message. 2");
                              // Hit the cap
                              //base_player : prose_hit_xp_cap	You have achieved your current limit for %TO experience.
                              //myOwner.getClient().insertPacket(PacketFactory.buildFlyTextSTFMessage(this.getMyPlayer(),"base_player","prose_hit_xp_cap","exp_n",exp.getExperienceName(),0));
                              myOwner.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
                              		"base_player",
                              		"prose_hit_xp_cap",
                              		0,
                              		null,
                              		null,
                              		null,
                              		0,
                              		null,
                              		null,
                              		null,
                              		0,
                              		"exp_n",
                              		exp.getExperienceName(),
                              		null,
                              		0,
                              		0f,
                              		true
                              ));

                              
                          }
                          else
                          {
                        	  // This should be sent regardless.
                        	  System.out.println("Sending Experience Message. 3");
                              //myOwner.getClient().insertPacket(PacketFactory.buildFlyTextSTFMessage(this.getMyPlayer(),"base_player", sExpString, "exp_n",exp.getExperienceName(), iExpAmount));
                              myOwner.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
                              		"base_player",
                              		sExpString,
                              		0,
                              		null,
                              		null,
                              		null,
                              		0,
                              		null,
                              		null,
                              		null,
                              		0,
                              		"exp_n",
                              		exp.getExperienceName(),
                              		null,
                              		iExpAmount,
                              		0f,
                              		true
                              ));

                          }
                    } catch (Exception e) {
                            System.out.println("Exception Caught in PlayerItem.updateExperience " + e);
                            e.printStackTrace();
                    }					
				}
			} else { 
                //System.out.println("Exp was 0");
				myOwner.getClient().insertPacket(PacketFactory.buildExperienceDelta(this, exp, Constants.DELTA_DELETING_ITEM));
			}
			return;
		}
		if (iExperienceGained != 0) {
			exp = new PlayerExperience();
			exp.setExperienceID(iExperienceType);
			exp.setCurrentExperience(iExperienceGained);
			exp.setExperienceName(DatabaseInterface.getExperienceNameFromID(iExperienceType));
			
			if (s != null) {
				exp.setMaxExperience(s.getExperienceCap());
			}
			vExperienceList.put(iExperienceType, exp);
			myOwner.getClient().insertPacket(PacketFactory.buildExperienceDelta(this, exp, Constants.DELTA_CREATING_ITEM));
			try {
				// "You have gained %DI %TO experience"
				myOwner.getClient().insertPacket(PacketFactory.buildChatSystemMessage(
						"faction_recruiter",
						"training_experience_granted",
						0l,
						"",
						"",
						"",
						0l,
						"",
						"",
						"",
						0l,
						"exp_n",
						exp.getExperienceName(),
						"",
						iExperienceGained,
						0f, false));
			} catch (IOException e) {
				System.out.println("Caught IOException building experience Chat System Message: " + e.toString());
				e.printStackTrace();
				
			}
		}
	}
	
	/**
	 * Increment and then get the Experience list update counter.
	 * @return The experience list update counter.
	 */
	public int getExperienceListUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iExperienceListUpdateCount++;
		}
		return iExperienceListUpdateCount;
	}
	
	/**
	 * Increment and then get the Waypoint list update counter.
	 * @return The waypoint list update counter.
	 */
	public int getWaypointUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iWaypointListUpdateCount++;
		}
		return iWaypointListUpdateCount;
	}
	
	/**
	 * Gets the value of the Player's current force pool.
	 * @return The current Force value.
	 */
	public int getCurrentForce() {
		return Math.min(0, iCurrentForce);
	}
	
	/**
	 * Gets the value of the Player's maximum available force.
	 * @return The maximum Force value.
	 */
	public int getMaxForce() {
		return Math.min(0, iMaxForce);
	}
	
	/**
	 * Sets the Player's currently available force.
	 * @param iForce -- The amount of Force available to the Player.
	 */
	public void setCurrentForce(int iForce) {
		iCurrentForce = Math.min(iMaxForce, iForce);
	}
	
	/**
	 * Sets the Maximum amount of Force available to the Player.  Also updates the rate at which the current Force regenerates.
	 * @param iForce -- The maximum available Force.
	 */
	public void setMaxForce(int iForce) {
		iMaxForce = iForce;
		iForceUpdateKKMS = PacketUtils.toKK(iMaxForce);
		iForceUpdateKKMS = iForceUpdateKKMS / 60000;
	}
	
	/**
	 * Gets the list of known Crafting Schematics.
	 * @return The Schematic List.
	 */
	public BitSet getSchematics() {
		return vSchematicList;
	}
	
	/**
	 * Gets the update counter for the Schematics List.
	 * @return The Schematic List update count.
	 */
	public int getSchematicUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iSchematicListUpdateCount++;
		}
		return iSchematicListUpdateCount;
	}
	
	public void setSchematicUpdateCount(int count) {
		iSchematicListUpdateCount = count;
	}
	
	/**
	 * Gets the value of the Jedi flag for this player.
	 * @return The Jedi Flag.
	 */
	public int getJediFlag() {
		return iJediFlag;
	}
	
	/**
	 * Gets a specific Waypoint, by ID.
	 * @param ID -- The Object ID of the Waypoint being sought.
	 * @return The Waypoint with the given ID, or null if the Waypoint does not exist.
	 */
	protected Waypoint getWaypoint(long ID) {
		for (int i = 0; i < vWaypointList.size(); i++) {
			Waypoint w = vWaypointList.elementAt(i);
			if (w.getID() == ID) {
				return w;
			}
		}
		return null;
	}

	/**
	 * Sets the Friends list.
	 * @param vFriendsList -- The new Friends List.
	 */
	protected void setFriendsList(Vector<PlayerFriends> vFriendsList) {
		this.vFriendsList = vFriendsList;
	}
	
	/**
	 * Increments and then gets the update counter for the Friends List.
	 * @return The Friends List Update Counter.
	 */
	public int getFriendsListUpdateCount(boolean bIncrement) {
		if (bIncrement) {
			iFriendsListUpdateCount++;
		}
		return iFriendsListUpdateCount;
	}
	
	/**
	 * Increments and then gets the update counter for the Ignore List.
	 * @return The Ignore List Update Counter.
	 */
	public int getIgnoreListUpdateCount(boolean bIncrement) {
		if (bIncrement) {	
			iIgnoreListUpdateCount++;
		}
		return iIgnoreListUpdateCount;
	}

	/**
	 * Gets the ID of the currently spoken Language.
	 * @return The Language ID.
	 */
	public int getCurrentLanguageID() {
		return iCurrentLanguage;
	}
	
	public void setCurrentLanguageID(ZoneClient client, int languageID) throws IOException{
		if (iCurrentLanguage != languageID) {
			client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)0x09, this, languageID));
			iCurrentLanguage = languageID;
		}
	}
	
	/**
	 * Gets the current drink fullness of the player's stomach.
	 * @return The stomach's current drink fullness.
	 */
	public int getCurrentDrinkFullness() {
		return Math.min(iDrinkFullness,  getMaxDrinkFullness());
	}
	
	/**
	 * Gets the maximum value of drink fullness of the player's stomach.
	 * @return The stomach's maximum drink fullness. 
	 */
	public int getMaxDrinkFullness() {
		return Constants.DRINK_FULLNESS[myOwner.getRaceID()];
	}
	
	/**
	 * Gets the current food fullness of the Player's stomach.
	 * @return The stomach's current food fullness.
	 */
	public int getCurrentFoodFullness() {
		return Math.min(iFoodFullness, getMaxFoodFullness());
	}
	
	/**
	 * Gets the maximum value of food fullness of the player's stomach.
	 * @return The stomach's maximum food fullness.
	 */
	public int getMaxFoodFullness() {
		return Constants.FOOD_FULLNESS[myOwner.getRaceID()];
	}

	public boolean setCurrentDrinkFullness(int iAmountToIncrease) throws IOException {
		int newFullness = iDrinkFullness + iAmountToIncrease;
		if ((newFullness < getMaxDrinkFullness()) && (newFullness >= 0)) {
			iDrinkFullness = newFullness;
			myOwner.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)12, this, iDrinkFullness));
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean setCurrentFoodFullness(int iAmountToIncrease) throws IOException {
		int newFullness = iDrinkFullness + iAmountToIncrease;
		if ((newFullness < getMaxDrinkFullness()) && (newFullness >= 0)) {
			iDrinkFullness = newFullness;
			myOwner.getClient().insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)10, this, iFoodFullness));
			return true;
		} else {
			return false;
		}
		
	}
	
	public Player getMyPlayer() {
		return myOwner;
	}
	
	public void addBadge(int iBadgeID) {
		// 32 bits in an int value.
		int iArrayPosition = iBadgeID / 32;
		int iBitPosition = iBadgeID % 32;
		badgeBits[iArrayPosition] = badgeBits[iArrayPosition] | (1 << iBitPosition);
		//System.out.println("Added badge array index " + iArrayPosition + ", badge index " + iBitPosition +".  Badge bits this array index: " + Integer.toBinaryString(badgeBits[iArrayPosition]));
	}
	
    protected boolean hasBadge(int iBadgeID) {
		int iArrayPosition = iBadgeID / 32;
		int iBitPosition = iBadgeID % 32;
		//System.out.println("Has badge?  Array position " + iArrayPosition + ", Badge index " + iBitPosition + ", badge bits: " + badgeBits[iArrayPosition]);
		int mask = (badgeBits[iArrayPosition] & (1 << iBitPosition)); 
		//System.out.println("Masking value: " + mask);
		return mask != 0;
	}
	
	public int[] getBadges() {
		return badgeBits;
	}
        
        protected void setBadges(int [] b){
            badgeBits = b;
        }
                
	
	protected void setMarriedPlayerID(long lID) {
		lMarriedPlayerID = lID;
	}
	protected long getMarriedPlayerID() {
		return lMarriedPlayerID;
	}
	
	protected void addBitToStatusBitmask(int iState) {
		iStatusBitmask |= iState;
	}
	
	protected int getStatusBitmask() {
		return iStatusBitmask;
	}
	protected void removeBitFromStatusBitmask(int iState) {
		iStatusBitmask = iStatusBitmask & ~iState;
	}
	
	protected void setCsrOrDeveloperFlag(byte flagValue) {
		iCsrOrDeveloperFlag = flagValue;
	}
	protected byte getCsrOrDeveloperFlag() {
		return iCsrOrDeveloperFlag;
	}

	private int skillPointsLeft = 250;
    protected int getSkillPointsLeft(){
        return skillPointsLeft;
    }
        
    protected byte canTrainSkill(Skills S){
        PlayerExperience newSkillExperience;                      
        int experienceID = S.getExperienceType();
        int experienceCost = S.getExperienceCost();
        int skillPointsTaken = S.getPointsCost();
        
        if (skillPointsTaken <= skillPointsLeft) {
            if (experienceCost > 0) {
                newSkillExperience = vExperienceList.get(experienceID);
            	if (newSkillExperience.getCurrentExperience() >= experienceCost) {
            		return 0;
            	} else {
            		return -1;
            	}
            } else {
        		setSkill(S.getSkillID(), true, true);
        		
        		//newSkillExperience.setMaxExperience(S.getNewExperienceCap());
        		//updateExperience(experienceID, -experienceCost);
        		return 0;
            }
        } else {
        	return -2;
        }
        
    }
    

    public Structure getResidence() {
        return residence;
    }

    public void setResidence(Structure residence) {
        this.residence = residence;
    }
        
    public byte[] setCraftingStage(int iStage) {
    	if (craftingStage != iStage) {
    		craftingStage = iStage;
    		try {
    			//return PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)1, this, craftingStage);
    			return PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)2, this, craftingStage);
    		} catch (Exception e) {
    			return null;
    		}
    	} else {
    		return null;
    	}
    }
    
    public int getCraftingStage() {
    	return craftingStage;
    }
    
    public BitSet getSkillBits() {
    	return skillsBits;
    }
    
    /**
     * Sets one of the 3 timers on the Player for incapacitation / measuring of Death.
     * @return -- Whether or not this incapacitation should "kill" the Player and force them to clone.
     */
    protected boolean setPlayerJustIncapacitated() {
    	for (int i= 0; i < lIncapacitationTimers.length; i++) {
    		if (lIncapacitationTimers[i] <= 0) {
    			lIncapacitationTimers[i] = 1000 * 60 * 10;
    			return false;
    		}
    	}
    	return true;
    }
    
    protected void setLastUpdatedResourceContainer(ResourceContainer container) {
    	lastUpdatedResourceContainer = container;
    }
    
    protected ResourceContainer getLastUpdatedResourceContainer() {
    	return lastUpdatedResourceContainer;
    }
    
    protected MapLocationData getCloneBindLocation() {
    	return cloneBindLocation;
    }
    
    protected void setCloneBindLocation(MapLocationData data) {
    	cloneBindLocation = data;
    }

	public byte[] setNearbyCraftingStation(TangibleItem nearbyCraftingStation) {
		this.nearbyCraftingStation = nearbyCraftingStation;
		try {
			if (nearbyCraftingStation != null) {
				return PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)3, this, nearbyCraftingStation.getID());
			} else {
				return PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)3, this, 0l);
			}
		} catch (IOException e) {
			return null;
		}
	}

	public TangibleItem getNearbyCraftingStation() {
		return nearbyCraftingStation;
	}

	public byte[] setExperimentationAndManufacturingFlag(int flag) {
		iCraftingExperimentationAndManufactureFlag = flag;
		try {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)1, this, 3);
		} catch (IOException e) {
			return null;
		}
		
	}
	
	public int getExperimentationAndManufacturingFlag() {
		return iCraftingExperimentationAndManufactureFlag;
	}
	
	public byte[] setNumExperimentationPoints(int pointCount) {
		iNumExperimentationPoints = pointCount;
		try {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_PLAY, (byte)9, (short)1, (short)5, this, iNumExperimentationPoints);
		} catch (IOException e) {
			return null;
		}
	}
	
	public int getNumExperimentationPoints() {
		return iNumExperimentationPoints;
	}
}

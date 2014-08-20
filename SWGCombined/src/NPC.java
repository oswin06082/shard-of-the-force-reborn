import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The NPC class represents any non-player character present in the world.  This can include anything from 
 * probe droids to creatures to vehicles to Krayt Dragons.  Each NPC may or may not have a Path they are following, 
 * and each NPC may or may not have a combat routine they can follow when/if fighting something.
 * @author Darryl
 */
public class NPC extends Player {
	public final static long serialVersionUID = 1;
	private final static long VEHICLE_TOTAL_SPAWN_TIME_MS = 4 * 60 * 60 * 1000;
	private final static int MIN_DAMAGE_TO_REPORT = 20;
	private int iVehicleDamage = 0;
	private int iVehicleMaxHealth;
	private long lLinkID = 0;
	private String sVehicleEffect = null;
    private boolean bIsTerminal = false;
	private IntangibleObject myParent;
	private long lVehicleMaxHealthKK = 0;
	private long lVehicleDamageKK = 0;
	private long lVehicleHealthDecreaseKKperMS = 0;
	private boolean bIsSkillTrainer = false;
    private long myLairID;
    private Lair myLair;
    private boolean bDeathblows;
    private boolean bIsWild;
    private boolean bIsTameable;
    private boolean bIsPet;
    private boolean bIsPlayingMusic;
    private int iMaxSpawns;
    private boolean bIsBaby;
    private float roamCenterX,roamCenterY,roamCenterZ;
    private boolean bCanRoam;
    private int iRoamType;
    private int iMaxRoamDistance;
    private long lNextRoamTime;
    private int iArtificialIntelligenceType;
	private transient boolean bIsSpawned = false;
	private transient Hashtable<SOEObject, Integer> vCreatureHateList;
	private transient Hashtable<SOEObject, Integer> vPlayerPeacedHateList;
	// The faction CRC, in the case of NPCs, refers to what "species" the faction belongs to.
	// For example, if the creature is a Piket, then iFactionCRC = PacketUtils.SWGCrc("piket");
	// If the creature is an Imperial Surface Marshall, then iFactionCRC = Constants.FACTIONS[Constants.FACTION_IMPERIAL];
	// NPCs (of any type) are prevented from attacking any other NPC of their own faction.
	
	
	/**
	 * Constructs a new NPC, with an empty Path.
	 */
	public NPC(int iVehicleMaxHealth, IntangibleObject datapadObject) {
		super(null);
		myParent = datapadObject;
		this.iVehicleMaxHealth = iVehicleMaxHealth;
		lVehicleMaxHealthKK = PacketUtils.toKK(iVehicleMaxHealth);
		lVehicleHealthDecreaseKKperMS = lVehicleMaxHealthKK / VEHICLE_TOTAL_SPAWN_TIME_MS; // 436 damageKK per millisecond.
		iVehicleDamage = 0;
		lVehicleDamageKK = 0;
        bIsWild = false;
        vCreatureHateList = new Hashtable<SOEObject, Integer>();
        vPlayerPeacedHateList = new Hashtable<SOEObject, Integer>();
	}

	public NPC() {
		super(null);
        vCreatureHateList = new Hashtable<SOEObject, Integer>();
        vPlayerPeacedHateList = new Hashtable<SOEObject, Integer>();
	}
	
	/**
	 * Main update function for this NPC.
	 * @param lElapsedTimeMS  -- The time elapsed, in milliseconds, since the last time this function was called.
         * Will not be updated for terminals.
	 */
	public void update(long lElapsedTimeMS) {
        try {
			if(!this.bIsTerminal)
	        {
	        	if (!bDead) {
	            	//super.update(lElapsedTimeMS);
					updateHam(lElapsedTimeMS);
	        	} else {
	        		lDespawnDelayTimer -= lElapsedTimeMS;
	        		if (lDespawnDelayTimer <= 0) {
	        			// Despawn me.
	        			bIsSpawned = false;
	        			myLair.removeChild(this);
	        		}
	        	}
	        }
	        updateIntelligence(lElapsedTimeMS);
        } catch (Exception e) {
        	System.out.println("Error in NPC update: " + e.toString());
        	e.printStackTrace();
        }
	}
	
	
	/**
	 * Updates the HAM values of the NPC.  If the HAMS are already at maximum, this function does nothing.
	 * @param lElapsedTimeMS -- The time elapsed since the HAMS were last updated.
	 */
	private void updateHam(long lElapsedTimeMS) {
		if (iVehicleMaxHealth > 0) {
			updateVehicleHealth(lElapsedTimeMS);
    	} else {
        	updateCreatureHealth(lElapsedTimeMS);
        }
	}
	
	private int iLastDamage = 0;
	private void updateVehicleHealth(long lElapsedTimeMS) {
		//iLastDamage = iVehicleDamage;
		long lDamageToApplyKK = lVehicleHealthDecreaseKKperMS * lElapsedTimeMS;
		lVehicleDamageKK += lDamageToApplyKK;
		iVehicleDamage = (int)PacketUtils.unKK(lVehicleDamageKK);
		if ((Math.abs(iLastDamage - iVehicleDamage) >= MIN_DAMAGE_TO_REPORT) || (iLastDamage == 0)) {
			iLastDamage = iVehicleDamage;
			// Outdated code -- send to chat range.
			//System.out.println("Sending CREO3 delta for vehicle ID " + getID());
			ZoneServer server = getServer();
			if (server != null) {
				try {
					server.sendToRange(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)8, this, iVehicleDamage), Constants.PACKET_RANGE_CHAT_RANGE, this);
				} catch (Exception e) {
					// D'oh!
				}
			} else {
				System.out.println("Vehicle does not know what server it belongs to!!!");
			}
		}
	}
	
	private void updateCreatureHealth(long lTimeElapsedMS) {
		// Do stuff.
	}
	
	public void setDamage(int iHealth) {
		iVehicleDamage = iHealth;
		lVehicleDamageKK = PacketUtils.toKK(iHealth);
	}
	
	public int getDamage() {
		return iVehicleDamage;
	}
	
	public void setHealth(int iHealth) {
		iVehicleMaxHealth = iHealth;
		lVehicleMaxHealthKK = PacketUtils.toKK(iHealth);
	}
	
	public int getHealth() {
		return iVehicleMaxHealth;
	}
	
	public void setLinkID(long lID) {
		lLinkID = lID;
	}
	
	public long getLinkID() {
		return lLinkID;
	}
	
	public String getVehicleEffectString() {
		return sVehicleEffect;
	}
	
	public void setVechicleEffectString(String s) {
		sVehicleEffect = s;
	}
	protected boolean getIsSpawned() {
		return bIsSpawned;
	}
	protected void setIsSpawned(boolean b) {
		bIsSpawned = b;
	}
	
	protected IntangibleObject getDatapadObject() {
		return myParent;
	}
	
    protected void setMyParentObject(IntangibleObject Parent){
        myParent = Parent;
    }
    
    protected void setVehicleMaxHealth(int VehicleMaxHealth){
        iVehicleMaxHealth = VehicleMaxHealth;
    }
    
    protected void setVehicleMaxHealthKK(long VehicleMaxHealthKK){
        lVehicleMaxHealthKK = VehicleMaxHealthKK;
    }
    protected void setVehicleHealthDecreaseKKperMS(long VehicleHealthDecreaseKKperMS){
        lVehicleHealthDecreaseKKperMS = VehicleHealthDecreaseKKperMS;
    }
    protected long getVehicleMaxHealthKK(){
        return lVehicleMaxHealthKK;
    }
    
    protected long getVEHICLE_TOTAL_SPAWN_TIME_MS(){
        return VEHICLE_TOTAL_SPAWN_TIME_MS;
    }
    
    protected void setVehicleDamage(int VehicleDamage){
        iVehicleDamage = VehicleDamage;
    }
    
    protected int getVehicleDamage(){
        return iVehicleDamage;
    }
    
    protected void setVehicleDamageKK(long VehicleDamageKK){
        lVehicleDamageKK = VehicleDamageKK;
    }
    protected long getVehicleDamageKK(){
        return lVehicleDamageKK;
    }
    protected void animateNPC(ZoneClient c, String sAnimation){
       try{
            Vector<Player> currentPlayers = c.getServer().getPlayersAroundNPC(this);
            byte [] A = PacketFactory.buildNPCAnimation(this, sAnimation);
            for(int i = 0; i < currentPlayers.size(); i++)
            {
                Player T = currentPlayers.get(i);                    
                T.getClient().insertPacket(A);                              
            }
        }catch(Exception e){
                System.out.println("Exception Caught while sending NPC Animation " + e);
                e.printStackTrace();
            }  
    }
    
    protected void speakNPC(ZoneClient c, String sSpeech, short Mood1, short Mood2){
       try{
            Vector<Player> currentPlayers = c.getServer().getPlayersAroundNPC(this);
            
            for(int i = 0; i < currentPlayers.size(); i++)
            {
                Player T = currentPlayers.get(i);                    
                T.getClient().insertPacket(PacketFactory.buildObjectControllerMessageSpatial(this, T, T, sSpeech, (short)0, (short)0));                              
            }
        }catch(Exception e){
                System.out.println("Exception Caught while sending NPC Speech " + e);
                e.printStackTrace();
            }  
    }
    
    protected void setIsSkillTrainer() {
        bIsSkillTrainer = true;
    }
    
    protected boolean IsSkillTrainer(){
        return bIsSkillTrainer;
    }

    public long getMyLairID() {
        return myLairID;
    }

    public void setMyLairID(long myLairID) {
        this.myLairID = myLairID;
    }

    public Lair getMyLair() {
        return myLair;
    }

    public void setMyLair(Lair myLair) {
        this.myLair = myLair;
    }

    public boolean isBIsWild() {
        return bIsWild;
    }

    public void setBIsWild(boolean bIsWild) {
        this.bIsWild = bIsWild;
    }

    public boolean isBIsTameable() {
        return bIsTameable;
    }

    public void setBIsTameable(boolean bIsTameable) {
        this.bIsTameable = bIsTameable;
    }

    public boolean isPet() {
        return bIsPet;
    }

    public void setBIsPet(boolean bIsPet) {
        this.bIsPet = bIsPet;
    }

    public boolean isBIsPlayingMusic() {
        return bIsPlayingMusic;
    }

    public void setBIsPlayingMusic(boolean bIsPlayingMusic) {
        this.bIsPlayingMusic = bIsPlayingMusic;
    }

    public int getIMaxSpawns() {
        return iMaxSpawns;
    }

    public void setIMaxSpawns(int iMaxSpawns) {
        this.iMaxSpawns = iMaxSpawns;
    }

    public boolean isBIsBaby() {
        return bIsBaby;
    }

    public void setBIsBaby(boolean bIsBaby) {
        this.bIsBaby = bIsBaby;
    }

    public boolean isBCanRoam() {
        return bCanRoam;
    }

    public void setBCanRoam(boolean bCanRoam) {
        this.bCanRoam = bCanRoam;
    }

    public int getIMaxRoamDistance() {
        return iMaxRoamDistance;
    }

    public void setIMaxRoamDistance(int iMaxRoamDistance) {
        this.iMaxRoamDistance = iMaxRoamDistance;
    }

    public int getIRoamType() {
        return iRoamType;
    }

    public void setIRoamType(int iRoamType) {
        this.iRoamType = iRoamType;
    }

    public long getLNextRoamTime() {
        return lNextRoamTime;
    }

    public void setLNextRoamTime(long lNextRoamTime) {
        this.lNextRoamTime = lNextRoamTime;
    }

    public float getRoamCenterX() {
        return roamCenterX;
    }

    public void setRoamCenterX(float roamCenterX) {
        this.roamCenterX = roamCenterX;
    }

    public float getRoamCenterY() {
        return roamCenterY;
    }

    public void setRoamCenterY(float roamCenterY) {
        this.roamCenterY = roamCenterY;
    }

    public float getRoamCenterZ() {
        return roamCenterZ;
    }

    public void setRoamCenterZ(float roamCenterZ) {
        this.roamCenterZ = roamCenterZ;
    }
    
    private transient boolean bDead = false;
    private transient long lDespawnDelayTimer = 0;
    public byte[] setStance(byte newStance,boolean bOverride, boolean updateZone) {
    	super.setStance(null, newStance, bOverride);
    	if (newStance == Constants.STANCE_DEAD) {
    		bDead = true;
    		lDespawnDelayTimer = 30000l;
    	}
    	if (updateZone) {
    		try {
		    	byte[] packet = PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)0x0B, this, getStance());
		    	return packet;
    		} catch (Exception e) {
    			return null;
    		}
    	} else return null;
    }
    
    public byte[] addState(int iState, boolean updateZone, long lDuration) {
    	super.addState(iState, lDuration);
    	if (updateZone) {
    		try {
    			return PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)0x10, this, getStateBitmask());
    		} catch (Exception e) {
    			return null;
    		}
    	} else {
    		return null;
    	}
    }
    
    public byte[] removeState(int iState, boolean updateZone) {
    	super.removeState(iState);
    	if (updateZone) {
    		try {
    			return PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)0x10, this, getStateBitmask());
    		} catch (Exception e) {
    			return null;
    		}
    	} else {
    		return null;
    	}
    }
    
	public void setArtificialIntelligenceType(int iArtificialIntelligenceType) {
		this.iArtificialIntelligenceType = iArtificialIntelligenceType;
	}

	public int getArtificialIntelligenceType() {
		return iArtificialIntelligenceType;
	}
	
    private void updateIntelligence(long lElapsedTimeMS) throws Exception {
    	if (getStance() == Constants.STANCE_DEAD) {
    		// Do other stuff if you're dead, like decrement the despawn counter and despawn if it's less than 0.
    	} else {
	    	if (hasState(Constants.STATE_COMBAT)) {
	    		// We're fighting
	    		lTimeToNextAttack -= lElapsedTimeMS;
	    	}
	    	Enumeration<Integer> vPeacedHateListEnum = vPlayerPeacedHateList.elements();
	    	while (vPeacedHateListEnum.hasMoreElements()) {
	    		Integer theInt = vPeacedHateListEnum.nextElement();
	    		System.out.println("Old hatred: " + theInt.intValue());
	    		long currentHateKK = PacketUtils.toKK(theInt);
	    		currentHateKK -= (Constants.CREATURE_INTELLIGENCE_HATRED_DECAY_ON_PEACED_PLAYERS_KKMS * lElapsedTimeMS);
	    		theInt = Math.max(0, (int)PacketUtils.unKK(currentHateKK));
	    		System.out.println("New hatred: " + theInt.intValue());
	    	}
	    	switch (iArtificialIntelligenceType) {
		    	case Constants.INTELLIGENCE_TYPE_NONE: {
		    		// Do nothing -- no intelligence here.  Likely a terminal or some other such thing.
		    		break;
		    	}
		    	case Constants.INTELLIGENCE_TYPE_CREATURE_AGGRESSIVE: {
		    		updateAggressiveIntelligence(lElapsedTimeMS);
		    		break;
		    	}
		    	case Constants.INTELLIGENCE_TYPE_CREATURE_PASSIVE: {
		    		updatePassiveIntelligence(lElapsedTimeMS);
		    		break;
		    	}
		    	case Constants.INTELLIGENCE_TYPE_CREATURE_STALKER: {
		    		updateStalkerIntelligence(lElapsedTimeMS);
		    		break;
		    	}
		    	case Constants.INTELLIGENCE_TYPE_NPC_FORCE_SENSITIVE: {
		    		updateForceSensitiveIntelligence(lElapsedTimeMS);
		    		break;
		    	}
		    	case Constants.INTELLIGENCE_TYPE_NPC_JEDI: {
		    		updateJediIntelligence(lElapsedTimeMS);
		    		break;
		    	}
		    	case Constants.INTELLIGENCE_TYPE_NPC_MELEE: {
		    		updateMeleeIntelligence(lElapsedTimeMS);
		    		break;
		    	}
		    	case Constants.INTELLIGENCE_TYPE_NPC_RANGED: {
		    		updateRangedIntelligence(lElapsedTimeMS);
		    		break;
		    	}
		    	default: {
		    		System.out.println("Unknown intelligence type for NPC with ID " + getID());
		    		break;
		    	}
	    	}
	    	// Are we pursuing the target?
	    	// Remember, we've already moved towards the target.  
	    	// Keep a list of the last range, compared to this range.  If it is not significantly decreasing over a period of seconds, we are pursuing.
	    	// When we've pursued for too long, give up the chase and return to origin.
    	}
    }
    
    //private float fLastRangeToTarget = 0;
    // To be used for the stalker AI
    private transient long lTargetOfInterestID = 0l;
    private transient long lTimeToNextAttack = 0;
    // NOTE about creature intelligences:  If the creature is a herd animal, it will signal the rest of the herd to also react to 
    // it's chosen target based on intelligence.  If another member of the herd falls under attack, it will rush to the aid of that
    // herd member.  If the lair itself gets damaged, ONE and only one member of the herd will somewhat devote itself to healing the lair.
    
    // The Aggressive intelligence is more prone to attack something just for the hell of it.  
    // This intelligence is marked by creatures generally attacking until either they die, or the target dies,
    // with little or no regard for anything else.
    private void updateAggressiveIntelligence(long lElapsedTimeMS) throws Exception {
    	// If we don't hate anything, grab the closest player or NPC to hate.  Any player / NPC within 16 metres will be a viable target, provided it is not of the same species I am.
    	if (lCurrentHateID == 0) {
        	Vector<SOEObject> vObjectsInRange = getServer().getWorldObjectsAroundObject(this, 16.0f);
	    	if (!vObjectsInRange.isEmpty()) {
	    		float fClosestObjectRange = 0;
    			SOEObject tarObject = null;
	    		for (int i = 0; i < vObjectsInRange.size(); i++) {
	    			tarObject = vObjectsInRange.elementAt(i);
	    			if (tarObject instanceof Player) {
	    				Player tarPlayer = (Player)tarObject;
	    				// Can't hate something of your own exact species.
	    				if (tarPlayer.getTemplateID() != getTemplateID()) {
	    					float rangeToTarget = ZoneServer.getRangeBetweenObjects(this, tarPlayer);
	    					if ((fClosestObjectRange == 0) || (fClosestObjectRange > rangeToTarget)) {
	    						if (tarPlayer.hasState(Constants.STATE_SCENT_MASKED)) {
	    							int iMaskScentSkillLevel = tarPlayer.getSkillMod("mask_scent").getSkillModModdedValue();
	    							int roll = SWGGui.getRandomInt(150);
	    							if (roll > iMaskScentSkillLevel) {
	    								tarPlayer.removeState(Constants.STATE_SCENT_MASKED);
	    								tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage("skl_use", "sys_scentmask_break"));
	    	    						fClosestObjectRange = rangeToTarget;
	    	    						lTargetOfInterestID = tarObject.getID();
	    							} else {
	    								tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage("skl_use", "sys_scentmask_success", 0, null, null, null, getID(),getSTFFileName(), getSTFFileIdentifier(), getFirstName(), 0, null, null, null, 0, 0f, false));
	    								tarPlayer.updateExperience(null, 36, getConLevel());
	    							}
	    						} else {
		    						fClosestObjectRange = rangeToTarget;
		    						lTargetOfInterestID = tarObject.getID();
	    						}
	    					}
	    				}
		    		}
	    		}
	    		if (lTargetOfInterestID != 0) {
		    		if (tarObject instanceof Lair) {
		    			//System.out.println("NOT hating object -- it's a lair.  NPCs should not be allowed to destroy lairs.");
		    		} else {
			    		//System.out.println("NPC " + getID() + " now hates target with ID " + lTargetOfInterestID);
			    		if (tarObject != null) {
				    		if (tarObject instanceof Player) {
				    			if (!(tarObject instanceof NPC)) {
				    				Player player = (Player)tarObject;
				    				ZoneClient client = player.getClient();
				    				try {
				    					client.insertPacket(PacketFactory.buildNPCSpeak(player, this, null, "I hate you!", (short)0, (short)0));
				    				} catch (Exception e) {
				    					// Oh well.  We tried, anyway.
				    				}
				    			}
				    		}
			    		}
		    			addToHateList(getServer().getObjectFromAllObjects(lTargetOfInterestID), 1); // This will trigger the "I hate you" code next update.
		    		}
	    		}
	    	}
    	} else {
    		Player tarPlayer = (Player)getServer().getObjectFromAllObjects(lCurrentHateID);
    		if (tarPlayer == null) {
    			removeFromHateList(lCurrentHateID);
    		} else {
        		if (!ZoneServer.isInRange2D(tarPlayer, this, 96)) {
        			removeFromHateList(tarPlayer);
        			return;
        		}
    			if (!ZoneServer.isInRange2D(tarPlayer, this, 5)) {
	    			moveTowardsTarget(tarPlayer,lElapsedTimeMS);
	    		} else {
					Weapon unarmedWeapon = getWeapon();
					if (unarmedWeapon == null) {
						generateCreatureWeapon(unarmedWeapon);
					}
	    			if (lTimeToNextAttack <= 0) {
	    				lTimeToNextAttack = Constants.CREATURE_ATTACK_TIME_MS;
	    				// Attack.
	    				// Do we want to just straight up attack, or do we want some kind of special attack?
	    				// If we're in this loop, we only have, at most, 2 different special attacks.
	    				// So, odds are that we're just going to straight up attack.
	    				CombatAction theAttack = null;
	    				int specialAttackChance = SWGGui.getRandomInt(20);
	    				if(specialAttackChance == 0) {
	    					// Let's do one.
	    					boolean bAttack1 = (SWGGui.getRandomInt(1) == 1);
	    					if (bAttack1) {
	    						// theAttack = DatabaseInterface.getCreatureSpecialAttackForTemplate(this.getTemplateID())[0];
	    					} else {
	    						// theAttack = DatabaseInterface.getCreatureSpecialAttackForTemplate(this.getTemplateID())[1];
	    					}
	    				} else {
	    					theAttack = DatabaseInterface.getCombatActionByCRC(Constants.DefaultAttackNPC);
	    					if (theAttack != null) {
	    						updateAttackTarget(tarPlayer, theAttack);
	    					}
	    				}
	    				// Actually attack the player.
	    				
	    			}
	    		}
    		}
    	}
    }
    
    // The passive intelligence will never attack a target of it's own volition.
    // In general, the passive intelligence will fight to defend itself, and it may 
    // or may not attack another NPC target if that NPC target is factionally opposed to it,
    // but it will never attack another Player unprovoked.
    private void updatePassiveIntelligence(long lElapsedTimeMS) throws Exception {
    	// Something has attacked us if the current hate list is not empty
    	if (lCurrentHateID != 0) {
    		Player tarPlayer = (Player)getServer().getObjectFromAllObjects(lCurrentHateID);
    		if (tarPlayer == null) {
    			removeFromHateList(lCurrentHateID);
    		} else {
        		if (!ZoneServer.isInRange2D(tarPlayer, this, 96)) {
        			removeFromHateList(tarPlayer);
        			return;
        		}
	    		// Like the Aggressive AI, move towards them if out of range, attack if in range.
	    		int[] iCurrentHams = getCurrentHam();
	    		int[] iMaxHams = getMaxHam();
	    		boolean bRetreat = false;
	    		for (int i = 0; i < iCurrentHams.length && !bRetreat; i++) {
	    			if (iCurrentHams[i] < (iMaxHams[i] / 10)) {
	    				moveAwayFromTarget(tarPlayer, lElapsedTimeMS);
	    				bRetreat = true;
	    			}
	    		}
	    		if (!ZoneServer.isInRange2D(tarPlayer, this, 5)) {
	    			if (!bRetreat) {
	    				moveTowardsTarget(tarPlayer,lElapsedTimeMS);
	    			}
	    		} else {
					Weapon unarmedWeapon = getWeapon();
					if (unarmedWeapon == null) {
						generateCreatureWeapon(unarmedWeapon);
					}
	    			if (lTimeToNextAttack <= 0) {
	    				lTimeToNextAttack = Constants.CREATURE_ATTACK_TIME_MS;
	    				// Attack.
	    				// Do we want to just straight up attack, or do we want some kind of special attack?
	    				// If we're in this loop, we only have, at most, 2 different special attacks.
	    				// So, odds are that we're just going to straight up attack.
	    				CombatAction theAttack = null;
	    				int specialAttackChance = SWGGui.getRandomInt(20);
	    				if(specialAttackChance == 0) {
	    					// Let's do one.
	    					boolean bAttack1 = (SWGGui.getRandomInt(1) == 1);
	    					if (bAttack1) {
	    						// theAttack = DatabaseInterface.getCreatureSpecialAttackForTemplate(this.getTemplateID())[0];
	    					} else {
	    						// theAttack = DatabaseInterface.getCreatureSpecialAttackForTemplate(this.getTemplateID())[1];
	    					}
	    				} else {
	    					theAttack = DatabaseInterface.getCombatActionByCRC(Constants.DefaultAttackNPC);
	    					if (theAttack != null) {
	    						updateAttackTarget(tarPlayer, theAttack);
	    					}
	    				}
	    			}
	    		}
    		}
    	}
    }
    
    private long lStalkTimeMS = Constants.DEFAULT_CREATURE_STALK_TIME_MS;
    //private long lPursuitTimeMS = Constants.DEFAULT_CREATURE_STALK_TIME_MS;
    // The Stalker intelligence is more or less like the Aggressive intelligence, except that 
    // it will not attack something "right away".  It will "stalk" that target for a period of a few seconds first,
    // observing movement, strengths and weaknesses.  If the stalker intelligence determines that it is not likely to win
    // a fight, it may not attack at all.
    private void updateStalkerIntelligence(long lElapsedTimeMS) {
    	try {
	    	if (lCurrentHateID == 0) {
	    		// Find something nearby to stalk.
	    		Vector<SOEObject> vObjectsInRange = getServer().getWorldObjectsAroundObject(this, 16.0f);
		    	if (!vObjectsInRange.isEmpty()) {
		    		float fClosestObjectRange = 0;
	    			SOEObject tarObject = null;
		    		for (int i = 0; i < vObjectsInRange.size(); i++) {
		    			tarObject = vObjectsInRange.elementAt(i);
		    			if (tarObject instanceof Player) {
		    				Player tarPlayer = (Player)tarObject;
		    				// Can't hate something of your own exact species.
		    				if (tarPlayer.getTemplateID() != getTemplateID()) {
		    					float rangeToTarget = ZoneServer.getRangeBetweenObjects(this, tarPlayer);
		    					if ((fClosestObjectRange == 0) || (fClosestObjectRange > rangeToTarget)) {
		    						if (tarPlayer.hasState(Constants.STATE_SCENT_MASKED)) {
		    							int iMaskScentSkillLevel = tarPlayer.getSkillMod("mask_scent").getSkillModModdedValue();
		    							int roll = SWGGui.getRandomInt(150);
		    							if (roll > iMaskScentSkillLevel) {
		    								tarPlayer.removeState(Constants.STATE_SCENT_MASKED);
		    								tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage("skl_use", "sys_scentmask_break"));
		    	    						fClosestObjectRange = rangeToTarget;
		    	    						lTargetOfInterestID = tarObject.getID();
		    							} else {
		    								tarPlayer.getClient().insertPacket(PacketFactory.buildChatSystemMessage("skl_use", "sys_scentmask_success", 0, null, null, null, getID(),getSTFFileName(), getSTFFileIdentifier(), getFirstName(), 0, null, null, null, 0, 0f, false));
		    								tarPlayer.updateExperience(null, 36, getConLevel());
		    							}
		    						} else {
			    						fClosestObjectRange = rangeToTarget;
			    						lTargetOfInterestID = tarObject.getID();
		    						}
		    					}
		    				}
			    		}
		    		}
		    		if (lTargetOfInterestID != 0) {
			    		if (tarObject instanceof Lair) {
			    			//System.out.println("NOT hating object -- it's a lair.  NPCs should not be allowed to destroy lairs.");
			    		} else {
				    		//System.out.println("NPC " + getID() + " now hates target with ID " + lTargetOfInterestID);
				    		if (tarObject != null) {
					    		if (tarObject instanceof Player) {
					    			if (!(tarObject instanceof NPC)) {
					    				Player player = (Player)tarObject;
					    				ZoneClient client = player.getClient();
					    				try {
					    					client.insertPacket(PacketFactory.buildNPCSpeak(player, this, null, "I hate you!", (short)0, (short)0));
					    				} catch (Exception e) {
					    					// Oh well.  We tried, anyway.
					    				}
					    			}
					    		}
				    		}
			    			addToHateList(getServer().getObjectFromAllObjects(lTargetOfInterestID), 1); // This will trigger the "I hate you" code next update.
			    		}
		    		}
		    	}
	    	} else {
	    		Player tarPlayer = (Player)getServer().getObjectFromAllObjects(lCurrentHateID);
	    		if (tarPlayer == null) {
	    			removeFromHateList(lCurrentHateID);
	    		} else {
	    			if (lStalkTimeMS <= 0 || hasState(Constants.STATE_COMBAT)) { // If we're not stalking any more, OR we've been attacked 
		        		if (!ZoneServer.isInRange2D(tarPlayer, this, 96)) {
		        			removeFromHateList(tarPlayer);
		        			return;
		        		}
		    			if (!ZoneServer.isInRange2D(tarPlayer, this, 5)) {
			    			moveTowardsTarget(tarPlayer,lElapsedTimeMS);
			    		} else {
							Weapon unarmedWeapon = getWeapon();
							if (unarmedWeapon == null) {
								generateCreatureWeapon(unarmedWeapon);
							}
			    			if (lTimeToNextAttack <= 0) {
			    				lTimeToNextAttack = Constants.CREATURE_ATTACK_TIME_MS;
			    				// Attack.
			    				// Do we want to just straight up attack, or do we want some kind of special attack?
			    				// If we're in this loop, we only have, at most, 2 different special attacks.
			    				// So, odds are that we're just going to straight up attack.
			    				CombatAction theAttack = null;
			    				int specialAttackChance = SWGGui.getRandomInt(20);
			    				if(specialAttackChance == 0) {
			    					// Let's do one.
			    					boolean bAttack1 = (SWGGui.getRandomInt(1) == 1);
			    					if (bAttack1) {
			    						// theAttack = DatabaseInterface.getCreatureSpecialAttackForTemplate(this.getTemplateID())[0];
			    					} else {
			    						// theAttack = DatabaseInterface.getCreatureSpecialAttackForTemplate(this.getTemplateID())[1];
			    					}
			    				} else {
			    					theAttack = DatabaseInterface.getCombatActionByCRC(Constants.DefaultAttackNPC);
			    					if (theAttack != null) {
			    						updateAttackTarget(tarPlayer, theAttack);
			    					}
			    				}
			    				// Actually attack the player.
			    				
			    			}
			    		}
		    		} else {
		    			// Stalk 'em
		    			lStalkTimeMS -= lElapsedTimeMS;
		    			// Keep a distance of about 16m away.
		    			int iRange = TrigAngleUtils.getDistance((int)getX(), (int)getY(), (int)tarPlayer.getX(), (int)tarPlayer.getY());
		    			// If too close, move off.
		    			// If too far, move closer.
		    			// Boundaries:  No closer than 15m, no farther than 20m.
		    			if (iRange >= 20) {
		    				if (iRange >= 70) {
		    					removeFromHateList(tarPlayer); // Give up -- we don't want to start chasing things all over the planet.
		    				} else {
		    					moveTowardsTarget(tarPlayer, lElapsedTimeMS);
		    				}
		    			} else if (iRange <= 16){
		    				moveAwayFromTarget(tarPlayer, lElapsedTimeMS);
		    			} // else do nothing.
		    		}
	    		}
	    	}
    	} catch (Exception e) {
    		// D'oh!
    	}
    }
    
    // The Force Sensitive intelligence is an NPC intelligence which has full 360 degree view around it.  This intelligence
    // is much more likely to use force-based attacks when in combat the Jedi intelligence (since Jedi tend to rely more on the extreme power
    // if the lightsaber).  This intelligence, if based on the Light side, will tend to not attack any target unprovoked, but will stalk users of the
    // Dark side.  If this intelligence is of the Dark side, it will attack anything of an opposing faction which it perceives as "weak" in comparison to itself or it's group.
    // IE a single Nightsister Slave may not attack a fully templated player Jedi, whereas a group of 10 of them will.
    private void updateForceSensitiveIntelligence(long lElapsedTimeMS) {
    	
    }
    
    // The Jedi intelligence, like the Force Sensitive intelligence, is an NPC intelligence which has full 360 degree view of targets.  
    // However, unlike the Force Sensitive intelligence, the Jedi intelligence is much more likely to try to slice and dice it's opponent with liberal use of it's lightsaber.
    // A Jedi whom is based on the Light side will (almost) never use the force to attack it's target, preferring to use the force to enhance his or her own defenses.
    // However, a Dark Jedi is more likely to use the offensive force attacks (but still not nearly as likely as a Force Sensitive).
    // Also, like force sensitives, Light Jedi will tend to not attack anything on sight except Dark Jedi or Dark Force Sensitives,
    // while Dark Jedi may attack anything not aligned with or in service to themselves.
    private void updateJediIntelligence(long lElapsedTimeMS) {
    	
    }
    
    // The NPC Melee intelligence (and the ranged one as well, for that matter), are a factional based intelligence.
    // They will tend to ignore any same/similar faction NPC or Player, and attack on sight any opposing faction NPC or Player.
    // Obviously, the main difference between these two AIs is that, while the melee intelligence will seek to close distance with it's target
    // rapidly so as to be able to attack as soon as possible, the Ranged intelligence will seek to keep distance between itself and it's attacker,
    // so as to be able to deal ranged damage while avoiding melee damage itself.
    // *** A Ranged intelligence may switch dynamically to a melee intelligence (and vice versa) isntead of altering the distance between itself and it's target. ***
    private void updateMeleeIntelligence(long lElapsedTimeMS) {
    	
    }
    private void updateRangedIntelligence(long lElapsedTimeMS) {
    	
    }
    
    private transient long lCurrentHateID = 0l;
    private transient int iCurrentHateLevel = 0;
    protected void addToHateList(SOEObject o, int hateRating) {
    	int totalHate = hateRating;
    	if (vCreatureHateList == null) {
    		vCreatureHateList = new Hashtable<SOEObject, Integer>();
    	}
    	System.out.println("Add to hate list.  Initial hate: " + hateRating);	
    	if (vCreatureHateList.containsKey(o)) {
    		int iPreviousHate = vCreatureHateList.get(o);
    		System.out.println("Creature hates attacker already -- previous hate level was " + iPreviousHate);
    		totalHate += iPreviousHate;
    	}
    	System.out.println("Total hate now " + totalHate);
    	vCreatureHateList.put(o, totalHate);
    	if (o instanceof Player) {
    		Player p = (Player)o;
    		ZoneClient client = p.getClient();
    		if (client != null) {
    			try {
    				client.insertPacket(PacketFactory.buildUpdatePvPStatusMessage(this, getAttackedPVPStatus()));
    			} catch (Exception e) {
    				System.out.println("Error sending updated attacked PVP status to hated Player " + p.getFirstName() + ": " + e.toString());
    				e.printStackTrace();
    			}
    		}
    	}
    	if (totalHate > iCurrentHateLevel) {
    		lCurrentHateID = o.getID();
    	}
    }
    
    protected int getCurrentHateRating(SOEObject o) {
    	try {
    		return vCreatureHateList.get(o);
    	} catch (Exception e) {
    		return 0;
    	}
    }
    
    // Removes the current hate from the list (likely because it died), and finds the next most hated object.
    // If there are no other hated objects, returns the creature to random movement mode.
    protected void removeFromHateList(SOEObject o) {
    	System.out.println("Removing hated object with ID " + o.getID());
    	vCreatureHateList.remove(o);
    	if (lCurrentHateID == o.getID()) {
        	lCurrentHateID = 0;
        	iCurrentHateLevel = 0;
			if (!vCreatureHateList.isEmpty()) {
	    		Enumeration<Integer> vHates = vCreatureHateList.elements();
	    		int iHateIndex = -1;
	    		int iNumHatesIterated = 0;
	    		while (vHates.hasMoreElements()) {
	    			int iHateHere = vHates.nextElement();
	    			if (iHateHere > iCurrentHateLevel) {
	    				iCurrentHateLevel = iHateHere;
	    				iHateIndex= iNumHatesIterated;
	    			}
	    			iNumHatesIterated++;
	    			
	    		}
	    		if (iHateIndex != -1) {
	    			Enumeration<SOEObject> vHateIDs = vCreatureHateList.keys();
	    			for (int i = 0; i < iHateIndex; i++) {
	    				vHateIDs.nextElement();
	    			}
	    			lCurrentHateID = vHateIDs.nextElement().getID();
	    		}
	    	}
		}
    	if (lCurrentHateID == 0) {
    		try {
    			getServer().sendToRange(removeState(Constants.STATE_COMBAT, true), Constants.PACKET_RANGE_CHAT_RANGE, this);
    	    	if (o instanceof Player) {
    	    		Player p = (Player)o;
    	    		ZoneClient client = p.getClient();
    	    		if (client != null) {
	    				client.insertPacket(PacketFactory.buildUpdatePvPStatusMessage(this, getAttackedPVPStatus()));
    	    		}
    	    	}

    		} catch (Exception e) {
    			// Oh well
    		}
    	}
	}

    protected void removeFromHateList(long lID) {
    	SOEObject o = getServer().getObjectFromAllObjects(lID);
    	if (o != null) {
    		removeFromHateList(o);
    	}
    }
    
    private void moveTowardsTarget(Player tarPlayer, long lElapsedTimeMS) throws Exception {
		float currentX = getX();
		float currentY = getY();
		float range = ZoneServer.getRangeBetweenObjects(this, tarPlayer);
		// How far away are we?
		float distanceToTravel = Math.min((Constants.MAX_NPC_RUN_SPEED_METERS_MS * lElapsedTimeMS), range - 3.0f); // Move me to within 3 metres of my target.
		setVelocity(Constants.MAX_NPC_RUN_SPEED_METERS_SEC);
		// Displacement this frame.  Also need angle to target between me and the target.
		float bearingToTargetDegrees = SOEObject.absoluteBearingDegrees(this, tarPlayer);
		float movementAngle = SOEObject.absoluteBearingRadians(this, tarPlayer);
		// Now, we have the range, and the angle of the triangle.
		// We need to find the X displacement and the Y displacement
		float xDisplacementK = TrigSinAndCosUtils.sinKDeg((int)bearingToTargetDegrees) * distanceToTravel;
		float yDisplacementK = TrigSinAndCosUtils.cosKDeg((int)bearingToTargetDegrees) * distanceToTravel;
		float xDisplacement = xDisplacementK / PacketUtils.K;
		float yDisplacement = yDisplacementK / PacketUtils.K;
		// Where abouts is the target in relation to us.
		int octet = (int)(bearingToTargetDegrees / 45.0f);
		// In trueDisplacement, x is always first, y is always second.
		Point2D.Float trueDisplacement = PacketUtils.getDisplacement2D(xDisplacement, yDisplacement, octet);
		setX(currentX + trueDisplacement.x);
		setY(currentY + trueDisplacement.y);
		setZ(getServer().getHeightAtCoordinates(getX(), getY(), getPlanetID()));
		setMovementAngle(movementAngle);
		getServer().sendToRange(PacketFactory.buildNPCUpdateTransformMessage(this), Constants.PACKET_RANGE_CHAT_RANGE, this);
		//System.out.println(getID() + " NPC move towards target.  Previous: x["+currentX+"], y["+currentY+"]  New x["+getX()+"], y["+getY()+"]");
		//System.out.println("Movement heading: " + bearingToTargetDegrees + ", distance covered: " + distanceToTravel + ", time since last update: " + lElapsedTimeMS + "ms");
    }
    
    // Same as moveTowardsTarget, except invert the true displacement variables.  Instead of adding them to the current position, subtract them.
    private void moveAwayFromTarget(Player tarPlayer, long lElapsedTimeMS) throws Exception {
		float currentX = getX();
		float currentY = getY();
		float range = ZoneServer.getRangeBetweenObjects(this, tarPlayer);
		// How far away are we?
		float distanceToTravel = Math.min((Constants.MAX_NPC_RUN_SPEED_METERS_MS * lElapsedTimeMS) / 4, 80.0f - range); // Move me to within 3 metres of my target.
		setVelocity(Constants.MAX_NPC_RUN_SPEED_METERS_SEC);
		// Displacement this frame.  Also need angle to target between me and the target.
		float bearingToTargetDegrees = SOEObject.absoluteBearingDegrees(this, tarPlayer);
		float movementAngle = SOEObject.absoluteBearingRadians(this, tarPlayer);
		// Now, we have the range, and the angle of the triangle.
		// We need to find the X displacement and the Y displacement
		float xDisplacementK = TrigSinAndCosUtils.sinKDeg((int)bearingToTargetDegrees) * distanceToTravel;
		float yDisplacementK = TrigSinAndCosUtils.cosKDeg((int)bearingToTargetDegrees) * distanceToTravel;
		float xDisplacement = xDisplacementK / PacketUtils.K;
		float yDisplacement = yDisplacementK / PacketUtils.K;
		// Where abouts is the target in relation to us.
		int octet = (int)(bearingToTargetDegrees / 45.0f);
		// In trueDisplacement, x is always first, y is always second.
		Point2D.Float trueDisplacement = PacketUtils.getDisplacement2D(xDisplacement, yDisplacement, octet);
		setX(currentX - trueDisplacement.x);
		setY(currentY - trueDisplacement.y);
		setZ(getServer().getHeightAtCoordinates(getX(), getY(), getPlanetID()));
		setMovementAngle(movementAngle);
		getServer().sendToRange(PacketFactory.buildNPCUpdateTransformMessage(this), Constants.PACKET_RANGE_CHAT_RANGE, this);
    }
    
    protected void generateCreatureWeapon(Weapon unarmedWeapon) throws Exception {
    	unarmedWeapon = new Weapon();
		ItemTemplate template = DatabaseInterface.getTemplateDataByFilename("object/weapon/creature/shared_creature_default_weapon.iff");
		unarmedWeapon.setCRC(template.getCRC());
		unarmedWeapon.setIsDefaultWeapon(false);
		unarmedWeapon.setSkillRequirement(-1); 
		unarmedWeapon.setIFFFileName(template.getIFFFileName());
		unarmedWeapon.setSTFFileName(template.getSTFFileName());
		unarmedWeapon.setSTFFileIdentifier(template.getSTFFileIdentifier());
		unarmedWeapon.setName("");
		try {
    		unarmedWeapon.setConditionDamage(0, false); 
    		unarmedWeapon.setMaxCondition(100, false);
    		unarmedWeapon.setName(null);
		} catch (Exception e ) {
			// Cannot actually happen -- not updating Zone.
		}
		unarmedWeapon.setOwner(this);
		unarmedWeapon.setMinDamage(75); // Min damage 
		unarmedWeapon.setMaxDamage(80); // Max damage
		unarmedWeapon.setRefireDelay(Constants.CREATURE_ATTACK_TIME_MS / 1000.0f);
		unarmedWeapon.setAttackRange(5.0f);
		unarmedWeapon.setCustomizationData(null);
		unarmedWeapon.setID(getServer().getNextObjectID());
        unarmedWeapon.setBSendsEquipedState(true);
        unarmedWeapon.setArmorPiercingLevel(Constants.DAMAGE_PIERCING_NONE);
        unarmedWeapon.setWeaponType(Constants.WEAPON_TYPE_UNARMED);
		getServer().addObjectToAllObjects(unarmedWeapon, false, false);
		equipWeapon(unarmedWeapon, false);
		Vector<Player> vNearbyPlayers = getServer().getPlayersAroundNPC(this);
		for (int i = 0; i < vNearbyPlayers.size(); i++) {
			Player nearPlayer = vNearbyPlayers.elementAt(i);
			nearPlayer.spawnItem(unarmedWeapon);
		}
		getServer().sendToRange(PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)6, (short)1, (short)5, this, unarmedWeapon.getID()), Constants.PACKET_RANGE_CHAT_RANGE_EXCLUDE_SENDER, this);
    }
    
    private void updateAttackTarget(Player tarPlayer, CombatAction specialAttack) throws Exception {
    	ZoneServer server = getServer();
		byte targetStance = tarPlayer.getStance();
		ZoneClient client = tarPlayer.getClient();
		// TODO:  The player can only attack if he is Standing, Kneeling or Prone.  
		byte playerStance = getStance();
		if (playerStance != Constants.STANCE_STANDING && playerStance != Constants.STANCE_KNEELING && playerStance != Constants.STANCE_PRONE) {
			// Can't attack -- stand up first
			server.sendToRange(setStance(Constants.STANCE_STANDING, true, true), Constants.PACKET_RANGE_CHAT_RANGE, this);
			return; // Delay attack for 1 frame, to allow target player to see me stand up.
		}
		if (targetStance == Constants.STANCE_INCAPACITATED) {
			// Only allowable attack on target is DeathBlow or Sniper Shot
			// If those are the attacks, kill the target and 
			if (bDeathblows) {
				if (!(tarPlayer instanceof NPC)) {
					tarPlayer.setStance(null, Constants.STANCE_DEAD, true);
					tarPlayer.setHasSeenCloningWindow(false);
					if (client != null) {
						client.insertPacket(PacketFactory.buildChatSystemMessage("base_player", "prose_victim_dead", 0, null, null, null, getID(), getSTFFileName(), getSTFFileIdentifier(), getFirstName(), 0, null, null, null, 0, 0.0f, false));
					}
				} 
			} // else don't do anything -- the target is incapacitated, and we don't deathblow.  Leave it alone.
			removeFromHateList(tarPlayer);
			
			return;
		} 
		Weapon weapon = getWeapon();
		float fAttackRange = weapon.getAttackRange();
		fAttackRange = Math.max(fAttackRange, 5.0f);
		//System.out.println("Attack coordinates.  PlayerX: " + player.getX() + ", Y: " + player.getY() + ", Z: " + player.getZ() + "\nTargetX: " + tarPlayer.getX() + ", Y: " + tarPlayer.getY() + ", Z: " + tarPlayer.getZ() +"\nAttack range: " + fAttackRange);
		if (!ZoneServer.isInRange(this, tarPlayer, fAttackRange)) {
			float fRangeToTarget = ZoneServer.getRangeBetweenObjects(this, tarPlayer);
			// If it's because of the Z axis, loosen the range restriction by 5 metres and try again.
			if (fRangeToTarget > (ZoneServer.getLineLength(this.getZ(), tarPlayer.getZ()) + 5.0f)) {
				//System.out.println("Target out of range.  Range between player and target: " + ZoneServer.getRangeBetweenObjects(player, tarPlayer));
				System.out.println("NPC:updateAttackTarget error -- target out of range -- should have updated movement this frame.");
				return;
			} // else, it's the fraked up Z axis preventing us from attacking.
		} 
		// Actually apply damage based on the current weapon.
		// Is the player actually certified to use this weapon?

		// Did the player actually hit?
		// Roll between 0 and 150.
		// A roll of 0 is ALWAYS hit, AND damage * 2.
		// A roll of 150 is ALWAYS miss.
		int iAttackRoll = SWGGui.getRandomInt(150);
		int iHitState = Constants.COMBAT_EFFECT_HIT;
		int iWeaponType = weapon.getWeaponType();
		SkillMods targetDefenseMod = null;
		SkillMods targetDodgeMod = tarPlayer.getSkillMod("dodge");
		SkillMods targetBlockMod = tarPlayer.getSkillMod("block");
		SkillMods targetCounterAttackMod = tarPlayer.getSkillMod("counterattack");
		boolean bMelee= ((iWeaponType == Constants.WEAPON_TYPE_UNARMED) || (iWeaponType == Constants.WEAPON_TYPE_ONE_HANDED_SWORD) || (iWeaponType == Constants.WEAPON_TYPE_TWO_HANDED_SWORD) ||
				(iWeaponType == Constants.WEAPON_TYPE_POLEARM) || (iWeaponType >= Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED));

		if (bMelee) {
			if (weapon.getAttackRange() == 0) {
				weapon.setAttackRange(5.0f);
			}
			targetDefenseMod = tarPlayer.getSkillMod("melee_defense");
		} else {
			if (weapon.getAttackRange() == 0) {
				weapon.setAttackRange(64.0f);
			}
			targetDefenseMod = tarPlayer.getSkillMod("ranged_defense");
		}
		// iAttackRoll > 100 + (playerAccuracyMod) - (targetDefenseMod) is not a hit.
		int iDamageToApply = SWGGui.getRandomInt(weapon.getMinDamage(), weapon.getMaxDamage());
		float iDamageModifier = specialAttack.getDamageModifier();
		float fTotalDamage = (iDamageToApply * iDamageModifier);
		if ((fTotalDamage - (int)fTotalDamage) >= 0.5f) {
			fTotalDamage += 1.0f;
		}
		if (tarPlayer.getStance() == Constants.STANCE_KNOCKED_DOWN) {
			fTotalDamage = fTotalDamage * 1.4f;
		}
		iDamageToApply = (int)fTotalDamage;
			
		lTimeToNextAttack = Constants.CREATURE_ATTACK_TIME_MS;
		int iHamToDamage = specialAttack.getTargetHam();
		int[] iTargetHams = tarPlayer.getCurrentHam();
		// EvP combat -- the Player ALWAYS has current hams.
		// In EvE combat, the Player is an NPC, but should also ALWAYS have current hams.
		// If not, remove from hate list.
		if (iTargetHams == null) {
			removeFromHateList(tarPlayer);
			return;
		}
		if (iHamToDamage == -1 || iHamToDamage == 9) {
			iHamToDamage = SWGGui.getRandomInt(3);
			switch (iHamToDamage) {
				case 0: {
					break;
				}
				case 1: {
					iHamToDamage = 3;
					break;
				}
				case 2: {
					iHamToDamage = 6;
					break;
				}
				default: {
					iHamToDamage = 0;
					break;
				}
			}
		}

		// Here's where we actually start applying stuff.
		// Before we do that, did we actually hit?  Are we certified on the weapon?  (If not, damage should not exceed 10 points.)
		// Did the target evade?  (No damage).  Block?  (1/4 damage).  Counterattack?  (Reflect 1/4 damage).
		int iDamageToReflect = 0;
		// Critical hit.
		if (iAttackRoll == 0) {
			iHitState = Constants.COMBAT_EFFECT_HIT;
			iDamageToApply = iDamageToApply * 2;
		} else {
			if (targetDefenseMod != null) { 
				if (iAttackRoll < targetDefenseMod.getSkillModModdedValue()) {
					iHitState = Constants.COMBAT_EFFECT_MISS;
					iDamageToApply = 0;
				}
			} else {
				if (iAttackRoll > 100) {
					iHitState = Constants.COMBAT_EFFECT_MISS;
					iDamageToApply = 0;
				} else {
					iHitState = Constants.COMBAT_EFFECT_HIT;
				}
			}
		}
		int iBlockRoll = SWGGui.getRandomInt(150);
		int iDodgeRoll = SWGGui.getRandomInt(150);
		int iCounterAttackRoll = SWGGui.getRandomInt(150);
		boolean bBlock = false;
		boolean bDodge = false;
		if (targetBlockMod != null) {
			if (iBlockRoll < targetBlockMod.getSkillModModdedValue()) {
				bBlock = true;
				iDamageToApply = iDamageToApply / 4;
				iHitState = Constants.COMBAT_EFFECT_BLOCK;
				Weapon tarPlayerWeapon = tarPlayer.getWeapon();
				if (tarPlayerWeapon != null) {
					int tarWeaponType= tarPlayerWeapon.getWeaponType();
					if (tarWeaponType >= Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED) {
						iDamageToApply = 0;
					}
				}
			}
		} else {
			if (iBlockRoll < 10) {
				bBlock = true;
				iDamageToApply = iDamageToApply / 4;
				iHitState = Constants.COMBAT_EFFECT_BLOCK;
				Weapon tarPlayerWeapon = tarPlayer.getWeapon();
				if (tarPlayerWeapon != null) {
					int tarWeaponType= tarPlayerWeapon.getWeaponType();
					if (tarWeaponType >= Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED) {
						iDamageToApply = 0;
					}
				}
			}
		}
		if (!bBlock) {
			if (targetDodgeMod != null) {
				if (iDodgeRoll < targetDodgeMod.getSkillModModdedValue()) {
					bDodge = true;
					iDamageToApply = 0;
					iHitState = Constants.COMBAT_EFFECT_EVADE;
				}
			} else {
				if (iDodgeRoll < 10) {
					bDodge = true;
					iDamageToApply = 0;
					iHitState = Constants.COMBAT_EFFECT_EVADE;
				}
			}
		}
		if ((!bDodge) && (!bBlock)) {
			if (targetCounterAttackMod != null) {
				
				if (iCounterAttackRoll < targetCounterAttackMod.getSkillModModdedValue()) {
					iDamageToReflect = iDamageToApply /4;
					iHitState = Constants.COMBAT_EFFECT_COUNTER;
				}
			} else {
				if (iCounterAttackRoll < 10) {
					iDamageToReflect = iDamageToApply /4;
					iHitState = Constants.COMBAT_EFFECT_COUNTER;
				}
			}
		}
		
		iHamToDamage = Math.min(iHamToDamage, iTargetHams.length - 2);
		iHamToDamage = Math.max(iHamToDamage, 0);
		String sHitEffect = null;
		int r = 0;
		int g = 0;
		int b = 0;
		switch(iHamToDamage) {
			case Constants.HAM_INDEX_HEALTH: {
				sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_HIT_BODY];
				r = 255;
				break;
			}
			case Constants.HAM_INDEX_ACTION: {
				int iLocation = SWGGui.getRandomInt(19, 22);
				sHitEffect = Constants.COMBAT_EFFECTS_FLYTEXT[iLocation];
				g = 255;
				break;
			}
			case Constants.HAM_INDEX_MIND: {
				sHitEffect = Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_HIT_HEAD];
				b = 255;
				break;
			}
		}
		if (iHitState == Constants.COMBAT_EFFECT_COUNTER) {
			r = 255;
			g = 255;
			b = 0;
			sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_COUNTERATTACK];
		} else if (iHitState == Constants.COMBAT_EFFECT_BLOCK) {
			r = 255;
			g = 0;
			b = 255;
			sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_BLOCK];
		} else if (iHitState == Constants.COMBAT_EFFECT_EVADE) {
			r = 0;
			g = 255;
			b = 255;
			sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_DODGE];
		} else if (iHitState == Constants.COMBAT_EFFECT_MISS) {
			r = 255;
			g = 255;
			b = 255;
			sHitEffect =Constants.COMBAT_EFFECTS_FLYTEXT[Constants.COMBAT_EFFECT_FLYTEXT_MISS];
		}
		
		
		int iTotalDamage = iDamageToApply;

		// If the damage to apply was 0 or less, we must have missed or been blocked somehow.
		Vector<Integer> vCRCs = specialAttack.getAnimationCRC();
		int iCRCToSend = 0;
		if (vCRCs != null) {
			if (!vCRCs.isEmpty()) {
				iCRCToSend = vCRCs.elementAt(0);
			}
		} else {
			if (iWeaponType <= Constants.WEAPON_TYPE_POLEARM) {
				iCRCToSend = Constants.MELEE_BASIC_ATTACKS[SWGGui.getRandomInt(Constants.MELEE_BASIC_ATTACKS.length)];
			} else if (iWeaponType == Constants.WEAPON_TYPE_UNARMED){
				iCRCToSend = Constants.UNARMED_BASIC_ATTACKS[SWGGui.getRandomInt(Constants.UNARMED_BASIC_ATTACKS.length)];
			} else if (iWeaponType < Constants.WEAPON_TYPE_JEDI_LIGHTSABER_ONE_HANDED) {
				iCRCToSend = Constants.RANGED_BASIC_ATTACKS[SWGGui.getRandomInt(Constants.RANGED_BASIC_ATTACKS.length)];
			}
		}
		// Now, damage the player initiating the attack -- NO.  No damage is taken by the player for executing a standard attack, ONLY a special attack.
		// Here is the commented out code for that.

		// Creatures can attack naturally -- it doesn't require "concentration" for them to be able to.
		/*int iHealthCost = weapon.getHealthCost();
		int iActionCost = weapon.getActionCost();
		int iMindCost = weapon.getMindCost();
		float fHealthCostModifier = specialAttack.getHealthCostModifier();
		float fActionCostModifier = specialAttack.getActionCostModifier();
		float fMindCostModifier = specialAttack.getMindCostModifier();
		// Don't deduct ham from "me" if I'm just default attacking.
		if (specialAttack.getCRC() == 0xa8fef90a) {
			fHealthCostModifier = 0;
			fActionCostModifier = 0;
			fMindCostModifier = 0;
		}
		int iTotalHealthCost = (int)(iHealthCost * fHealthCostModifier);
		int iTotalActionCost = (int)(iActionCost * fActionCostModifier);
		int iTotalMindCost = (int)(iMindCost * fMindCostModifier);*/

		
		int iSecondaryDamage = Math.max(iDamageToApply / 10, 1);
		iTotalDamage += iSecondaryDamage * 2;
		
		// Insert all the packets here.

		tarPlayer.addState(Constants.STATE_COMBAT, Constants.COMBAT_STATE_DEFAULT_PERIOD_MS);
		addState(Constants.STATE_COMBAT, Constants.COMBAT_STATE_DEFAULT_PERIOD_MS);
		if (iDamageToReflect != 0) {
			updateCurrentHam(iHamToDamage, -iDamageToReflect);
		}
		Vector<Player> vPlayersInRange = getServer().getPlayersAroundObject(this, true);
		for (int i = 0; i < vPlayersInRange.size(); i++) {
			Player thePlayer = vPlayersInRange.elementAt(i);
			thePlayer.getClient().insertPacket(PacketFactory.buildCombatTextSpam(this, thePlayer, tarPlayer, "cbt_spam", specialAttack.getCombatSTFSpamArr()[iHitState], iTotalDamage));
		}
		if (client != null) {
			client.insertPacket(PacketFactory.buildAttackFlyText(tarPlayer, tarPlayer, "combat_effects", sHitEffect, r, g, b));
		}

		if (iDamageToApply > 0) {
			tarPlayer.updateCurrentHam(iHamToDamage, -iDamageToApply);
			if (iTargetHams.length > 1) {
				if (iHamToDamage == 0) {
					tarPlayer.updateCurrentHam(3, -iSecondaryDamage);
					tarPlayer.updateCurrentHam(6, -iSecondaryDamage);
				} else if (iHamToDamage == 3) {
					tarPlayer.updateCurrentHam(0, -iSecondaryDamage);
					tarPlayer.updateCurrentHam(6, -iSecondaryDamage);
				} else if (iHamToDamage == 6) {
					tarPlayer.updateCurrentHam(0, -iSecondaryDamage);
					tarPlayer.updateCurrentHam(3, -iSecondaryDamage);
				}
			}
			for (int i = 0; i < iTargetHams.length; i++) {
				if (iTargetHams[i] <= 0) {
					if (client != null) {
						client.insertPacket(PacketFactory.buildChatSystemMessage("base_player", "prose_victim_incap", 0, null, null, null, getID(), getSTFFileName(), getSTFFileIdentifier(), getFirstName(), 0, null, null, null, 0, 0.0f, false));
					}
				}
			}
			// States to apply.
			BuffEffect[] combatEffects = specialAttack.getAllCombatEffects();
			for (int i = 0; i < combatEffects.length; i++) {
				BuffEffect buffEffect = combatEffects[i];
				if (buffEffect != null) {
					float iChanceToApply = buffEffect.getEffectChance() * 100;
					int iRandomChance = SWGGui.getRandomInt(100);
					long lDuration = buffEffect.getTimeRemainingMS();
					if (iRandomChance < iChanceToApply) {
						if (lDuration == 0) {
							// It's a stance change.  What kind of stance change is it?
							int iStance = buffEffect.getStanceApplied();
							if (iStance == Constants.STANCE_PRONE) {
								// Posture down
							} else if (iStance == Constants.STANCE_STANDING) {
								// Posture up
							} else {
								// Knock down.
							}
						} else {
							// It's a state.
							tarPlayer.addBuffEffect(buffEffect.copy());
						}
					}		
				}
			}
		}
		if (iCRCToSend != 0) {
			server.sendToRange(PacketFactory.buildObjectController_CombatAction(this, tarPlayer, iCRCToSend, true), Constants.PACKET_RANGE_CHAT_RANGE, this);
		}
    }
    
    protected void setDeathblows(boolean state) {
    	bDeathblows = state;
    }
    
    protected boolean getDeathblows(){
    	return bDeathblows;
    }
    
	protected byte[] setScale(float f, boolean bUpdateZone) throws IOException {
		super.setScale(f, false, true);
		if (bUpdateZone) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_CREO, (byte)3, (short)1, (short)14, this, getScale());
		} else {
			return null;
		}
	}

}

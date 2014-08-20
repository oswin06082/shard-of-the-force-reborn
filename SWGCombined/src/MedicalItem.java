import java.io.IOException;
import java.util.Vector;


public class MedicalItem extends TangibleItem {
	public final static long serialVersionUID = 1l;
	private final static float AREA_HEAL_RANGE = 32.0f;
	private final static float AREA_HEAL_RADIUS = 8.0f;
	private byte iMedicalItemType;
	private int iHealAmount = 0;
	private float fDuration = 0f;
	private int iHamIndex = -1;
	
	public MedicalItem() {
		super();
	}
	
	protected void setMedicalItemType(byte iType) {
		iMedicalItemType = iType;
	}
	
	protected void setDurationEffectSec(float d) {
		fDuration = d;
	}
	
	protected byte getMedicalItemType() {
		return iMedicalItemType;
	}
	
	protected float getDurationEffectSec() {
		return fDuration;
	}
	
	protected int getHealAmount() {
		return iHealAmount;
	}
	protected void setHealAmount(int iHealAmount) {
		this.iHealAmount = iHealAmount;
	}
	
	protected void setHamIndex(int index) {
		if (index >= Constants.HAM_INDEX_HEALTH && index <= Constants.HAM_INDEX_WILLPOWER) {
			iHamIndex = index;
		} else throw new ArrayIndexOutOfBoundsException("Illegal ham index " + index);
	}
	
	protected int getHamIndex() {
		return iHamIndex;
	}
	
	@Override 
	protected void useItem(ZoneClient client) {
		Player player = client.getPlayer();
		
		switch (iMedicalItemType) {
			case Constants.MEDICAL_ITEM_TYPE_BUFF: {
				handleApplyBuff(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_DISEASE: {
				handleApplyDisease(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_DISEASE_AREA: {
				handleApplyAreaDisease(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_FIRE_BLANKET: {
				handleApplyFireBlanket(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_HEAL_DAMAGE: {
				handleHealDamage(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_HEAL_DAMAGE_AREA: {
				handleHealAreaDamage(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_HEAL_MIND_DAMAGE: {
				handleHealMindDamage(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_HEAL_WOUNDS: {
				handleHealWounds(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_HEAL_WOUNDS_AREA: {
				handleHealAreaWounds(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_POISON: {
				handleApplyPoison(client, player);
				break;
			}
			case Constants.MEDICAL_ITEM_TYPE_POISON_AREA: {
				handleApplyAreaPoison(client, player);
				break;
			}
			default: {
				System.out.println("Unknown medical item type " + iMedicalItemType);
			}
		}
	}
	
	private void handleApplyBuff(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			int buffAmountApplied = 0;
			if (targetID == 0) {
				player.applyBuff(this); // You don't gain experience for buffing yourself.
				// Apply buff to the player
			} else {
				SOEObject tarObject = client.getServer().getObjectFromAllObjects(targetID);
				if (tarObject instanceof Player) {
					if (!(tarObject instanceof NPC)) {
						Player tarPlayer = (Player)tarObject;
						if (ZoneServer.isInRange(tarPlayer, player, AREA_HEAL_RANGE)) {
							buffAmountApplied = tarPlayer.applyBuff(this);
						}
					}
				}
			}
			if (buffAmountApplied > 0) {
				player.updateExperience(null, DatabaseInterface.getExperienceIDFromName("medical"), buffAmountApplied / 2);
			}
		} catch (IOException e) {
			DataLog.logException("Error applying buff", "MedicalItem.handleApplyBuff", true, true, e);
		}
	}
	
	private void handleApplyDisease(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			if (targetID == 0) {
				// Tell the player he's stupid
				client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot apply a disease to yourself."));
			} else {
				// Apply buff to the target
				SOEObject tarObject = client.getServer().getObjectFromAllObjects(targetID);
				
				if (tarObject instanceof Player) {
					Player tarPlayer = (Player)tarObject;
					if (ZoneServer.isInRange(tarPlayer, player, AREA_HEAL_RANGE)) {
						tarPlayer.applyDisease(this);
					} else {
						client.insertPacket(PacketFactory.buildChatSystemMessage(tarPlayer.getFirstName() + " is out of range."));
					}
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot apply a disease to that target."));
				}
			}
		} catch (IOException e) {
			DataLog.logException("Error applying buff", "MedicalItem.handleApplyDisease", true, true, e);
		}
	}
	
	private void handleApplyAreaDisease(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			
			Vector<Player> vPlayersInRange = null;
			if (targetID == 0) {
				vPlayersInRange = client.getServer().getPlayersAroundObject(this, false, AREA_HEAL_RADIUS);
			} else {
				SOEObject o = client.getServer().getObjectFromAllObjects(targetID);
				if (o != null) {
					vPlayersInRange = client.getServer().getPlayersAroundObject(o, true, AREA_HEAL_RADIUS);
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage("Internal error applying area disease -- target with ID " + targetID + " not found."));
					return;
				}
			}
			if (vPlayersInRange != null) {
				if (!vPlayersInRange.isEmpty()) {
					for (int i = 0; i < vPlayersInRange.size(); i++) {
						Player tarPlayer= vPlayersInRange.elementAt(i);
						tarPlayer.applyDisease(this);
					}
				}
			}
		} catch (IOException e) {
			DataLog.logException("Error applying area disease", "MedicalItem.handleApplyAreaDisease", true, true, e);
		}
	}

	private void handleApplyFireBlanket(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			if (targetID == 0) {
				// Tell the player he's stupid
				player.applyFireBlanket(this);
			} else {
				// Apply buff to the target
				SOEObject tarObject = client.getServer().getObjectFromAllObjects(targetID);
				if (tarObject instanceof Player) {
					Player tarPlayer = (Player)tarObject;
					tarPlayer.applyFireBlanket(this);
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot apply a fire blanket to that target."));
				}

			}
		} catch (IOException e) {
			DataLog.logException("Error applying fire blanket", "MedicalItem.handleApplyFireBlanket", true, true, e);
		}
	}
	
	private void handleHealDamage(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			if (targetID == 0) {
				player.healDamage(this);
			} else {
				SOEObject tarObject = client.getServer().getObjectFromAllObjects(targetID);
				if (tarObject != null) {
					if (tarObject instanceof Player) {
						Player tarPlayer = (Player)tarObject;
						if (ZoneServer.isInRange(tarPlayer, player, AREA_HEAL_RANGE)) {
							int damageActuallyHealed = tarPlayer.healDamage(this);
							player.updateExperience(null, DatabaseInterface.getExperienceIDFromName("medical"), damageActuallyHealed / 10);
						}
					}
				}
				// Apply buff to the target
			}
		} catch (IOException e) {
			DataLog.logException("Error healing damage", "MedicalItem.handleHealDamage", true, true, e);
		}
	}
	
	private void handleHealAreaDamage(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		int totalDamageHealed = 0;
		try {
			Vector<Player> vPlayersInRange = null;
			if (targetID == 0) {
				// Heal objects around the player.
				vPlayersInRange = client.getServer().getPlayersAroundObject(player, true, AREA_HEAL_RADIUS);
			} else {
				SOEObject tarObject = client.getServer().getObjectFromAllObjects(targetID);
				if (tarObject != null) {
					vPlayersInRange = client.getServer().getPlayersAroundObject(tarObject, true, AREA_HEAL_RADIUS);
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage("Internal error applying area disease -- target with ID " + targetID + " not found."));
				}
			
			}
			if (vPlayersInRange != null) {
				if (!vPlayersInRange.isEmpty()) {
					for (int i = 0; i < vPlayersInRange.size(); i++) {
						Player tarPlayer= vPlayersInRange.elementAt(i);
						totalDamageHealed += tarPlayer.healDamage(this);
					}
				}
			}
			player.updateExperience(null, DatabaseInterface.getExperienceIDFromName("medical"), totalDamageHealed / 15);
		} catch (IOException e) {
			DataLog.logException("Error applying buff", "MedicalItem.handleApplyBuff", true, true, e);
		}
	}


	private void handleApplyPoison(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			if (targetID == 0) {
				// Tell the player he's stupid
				client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot apply a disease to yourself."));
			} else {
				// Apply buff to the target
				SOEObject tarObject = client.getServer().getObjectFromAllObjects(targetID);
				
				if (tarObject instanceof Player) {
					Player tarPlayer = (Player)tarObject;
					if (ZoneServer.isInRange(tarPlayer, player, AREA_HEAL_RANGE)) {
						tarPlayer.applyPoison(this);
					} else {
						client.insertPacket(PacketFactory.buildChatSystemMessage(tarPlayer.getFirstName() + " is out of range."));
					}
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage("You cannot apply a disease to that target."));
				}
			}
		} catch (IOException e) {
			DataLog.logException("Error applying buff", "MedicalItem.handleApplyDisease", true, true, e);
		}
	}
	
	private void handleApplyAreaPoison(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			
			Vector<Player> vPlayersInRange = null;
			if (targetID == 0) {
				vPlayersInRange = client.getServer().getPlayersAroundObject(this, false, AREA_HEAL_RADIUS);
			} else {
				SOEObject o = client.getServer().getObjectFromAllObjects(targetID);
				if (o != null) {
					vPlayersInRange = client.getServer().getPlayersAroundObject(o, true, AREA_HEAL_RADIUS);
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage("Internal error applying area disease -- target with ID " + targetID + " not found."));
					return;
				}
			}
			if (vPlayersInRange != null) {
				if (!vPlayersInRange.isEmpty()) {
					for (int i = 0; i < vPlayersInRange.size(); i++) {
						Player tarPlayer= vPlayersInRange.elementAt(i);
						if (tarPlayer.getID() != player.getID()) {
							tarPlayer.applyPoison(this);
						}
					}
				}
			}
		} catch (IOException e) {
			DataLog.logException("Error applying area disease", "MedicalItem.handleApplyAreaDisease", true, true, e);
		}
	}


	private void handleHealWounds(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			if (targetID == 0) {
				player.healWounds(this);
			} else {
				SOEObject tarObject = client.getServer().getObjectFromAllObjects(targetID);
				if (tarObject != null) {
					if (tarObject instanceof Player) {
						Player tarPlayer = (Player)tarObject;
						if (ZoneServer.isInRange(tarPlayer, player, AREA_HEAL_RANGE)) {
							tarPlayer.healWounds(this);
						}
					}
				}
				// Apply buff to the target
			}
		} catch (IOException e) {
			DataLog.logException("Error healing damage", "MedicalItem.handleHealDamage", true, true, e);
		}
	}
	
	private void handleHealAreaWounds(ZoneClient client, Player player) {
		long targetID = player.getTargetID();
		try {
			Vector<Player> vPlayersInRange = null;
			if (targetID == 0) {
				// Heal objects around the player.
				vPlayersInRange = client.getServer().getPlayersAroundObject(player, true, AREA_HEAL_RADIUS);
			} else {
				SOEObject tarObject = client.getServer().getObjectFromAllObjects(targetID);
				if (tarObject != null) {
					vPlayersInRange = client.getServer().getPlayersAroundObject(tarObject, true, AREA_HEAL_RADIUS);
				} else {
					client.insertPacket(PacketFactory.buildChatSystemMessage("Internal error applying area disease -- target with ID " + targetID + " not found."));
				}
			
			}
			if (vPlayersInRange != null) {
				if (!vPlayersInRange.isEmpty()) {
					for (int i = 0; i < vPlayersInRange.size(); i++) {
						Player tarPlayer= vPlayersInRange.elementAt(i);
						tarPlayer.healWounds(this);
					}
				}
			}
			
		} catch (IOException e) {
			DataLog.logException("Error applying buff", "MedicalItem.handleApplyBuff", true, true, e);
		}
	}
	
	protected void handleHealMindDamage(ZoneClient client, Player player){
		if (player.hasSkill(575)) {
			handleHealDamage(client, player);
			// Apply mind wounds to the Player.
			player.updateHAMWounds(Constants.HAM_INDEX_MIND, 100, true);
			player.updateHAMWounds(Constants.HAM_INDEX_FOCUS, 100, true);
			player.updateHAMWounds(Constants.HAM_INDEX_WILLPOWER, 100, true);
		}
	}

	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Depends on what it is.
		return 0;
	}

}

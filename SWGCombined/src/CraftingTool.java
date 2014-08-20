import java.io.IOException;
import java.util.Vector;


public class CraftingTool extends TangibleItem {
	public final static long serialVersionUID = 1l;
	private TangibleItem itemBeingCrafted = null;
	private transient long lTimeLeftOnCraftingProcessMS = 0;
	private transient long lTimeToUpdateCraftingMS = 0;
	private boolean bStillCrafting = false;
	public CraftingTool() {
		// TODO Auto-generated constructor stub
		super();
		itemBeingCrafted = null;
	}

	protected void update(long lElapsedTimeMS, ZoneServer server, ZoneClient client, Player player) throws IOException {
		super.update(lElapsedTimeMS, server, client, player);
		if (itemBeingCrafted != null) {
			lTimeLeftOnCraftingProcessMS -= lElapsedTimeMS;
			lTimeToUpdateCraftingMS -= lElapsedTimeMS;
			if (bStillCrafting) {
				if (lTimeLeftOnCraftingProcessMS <= 0) {
					bStillCrafting = false;
					lTimeLeftOnCraftingProcessMS = 0;
					lTimeToUpdateCraftingMS = 0;
					client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)7, this, 0));
					// TODO:  Inventory size check.  
					TangibleItem inventory = player.getInventory();
					
					Vector<TangibleItem> vAllInventoryItems = inventory.getLinkedObjects();
					if (vAllInventoryItems.size() > Constants.MAX_ITEMS_IN_INVENTORY) {
						client.insertPacket(PacketFactory.buildChatSystemMessage("system_msg", "prototype_not_transferred", 0, null, null, null,0, null, null, null,0, null, null, null,0, 0, false));
						
					} else {
						putCraftedItemIntoInventory(player, client, inventory);
					}
				} else if (lTimeToUpdateCraftingMS <= 0) {
					lTimeToUpdateCraftingMS = Math.min(lTimeLeftOnCraftingProcessMS, 5000l);
					client.insertPacket(PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)7, this, (int)(lTimeLeftOnCraftingProcessMS / 1000)));
				}
			}
		}
	}
	
	protected byte[] setItemBeingCrafted(TangibleItem item, CraftingSchematic schematic, boolean bUpdate) throws IOException {
		itemBeingCrafted = item;
		lTimeLeftOnCraftingProcessMS =schematic.getComplexity() * 1000l;
		lTimeToUpdateCraftingMS = Math.min(lTimeLeftOnCraftingProcessMS, 5000l);
		bStillCrafting = true;
		if (bUpdate) {
			return PacketFactory.buildDeltasMessage(Constants.BASELINES_TANO, (byte)3, (short)1, (short)7, this, schematic.getComplexity());
		}
		return null;
	}
	
	protected void putCraftedItemIntoInventory(Player player, ZoneClient client, TangibleItem inventory) throws IOException {
		player.addItemToInventory(itemBeingCrafted);
		inventory.addLinkedObject(itemBeingCrafted);
		itemBeingCrafted.setEquipped(inventory, Constants.EQUIPPED_STATE_UNEQUIPPED);
		client.insertPacket(PacketFactory.buildUpdateContainmentMessage(itemBeingCrafted, player.getInventory(), itemBeingCrafted.getEquippedStatus()));
		client.insertPacket(PacketFactory.buildChatSystemMessage("system_msg", "prototype_transferred", 0, null, null, null,0, null, null, null,0, null, null, null,0, 0, false));
		
	}
	
	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Depends on what it is.
		return 0;
	}

}

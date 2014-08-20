import java.io.IOException;


public class FoodItem extends TangibleItem {
	public final static long serialVersionUID = 1l;
	
	private int iFilling;
	private boolean bIsFood;
	private BuffEffect buffEffect;
	
	public FoodItem() {
		super();
		buffEffect = null;
	}
	public void setIsFood(boolean bIsFood) {
		this.bIsFood = bIsFood;
	}
	public boolean getIsFood() {
		return bIsFood;
	}
	public void setFilling(int iFilling) {
		this.iFilling = iFilling;
	}
	public int getFilling() {
		return iFilling;
	}
	
	public void eat(Player player) throws IOException {
		boolean bCanEat = false;
		if (bIsFood) {
			bCanEat = player.setCurrentFoodFullness(iFilling);
		} else {
			bCanEat = player.setCurrentDrinkFullness(iFilling);
		}
		if (bCanEat) {
			// Check my stack size.
			// If this is the last one, delete it from inventory and from the list of all objects by ID.
			// Otherwise, decrease stack size by 1.
			int stackSize = getStackQuantity();
			if (stackSize == 1) {
				// Despawn me.
				player.despawnItem(this);
				player.removeItemFromInventory(this);
				ZoneServer server = player.getClient().getServer();
				
				server.removeObjectFromAllObjects(this, false);
				
			} else {
				// Decrement the stack size.
				player.getClient().insertPacket(setStackQuantity(stackSize - 1, true)); 
			}
			// Create a copy of the buff effect to give to the Player.
			BuffEffect playerEffect = buffEffect.copy();
			player.addBuffEffect(playerEffect); // If any.
		}
	}

	public void setBuffEffect(BuffEffect buffEffect) {
		this.buffEffect = buffEffect;
	}
	public BuffEffect getBuffEffect() {
		return buffEffect;
	}
	
	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Depends on what it is.
		return 0;
	}

}

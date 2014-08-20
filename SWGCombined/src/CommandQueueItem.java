
/**
 * A CommandQueueItem is any Item that the server receives from a Player in a CommandQueueEneuque Object Controller Message.
 * These CommandQueueItems may have a delay attached to them, which indicates that the "next" CommandQueueItem may not be executed
 * until the delay attached to "this" one has expired.
 * @author Darryl
 *
 */
public class CommandQueueItem {
	private int commandCRC;
	private long commandTimerMS;
	private int iErrorMessageID;
	private int iSkillBitSetRequired;
	private int commandID;
	private int iStateInvoked;
	private boolean bHasBeenSent = false;
	private CombatAction combatAction;
	private long lTargetID;
	private String[] sParams;
	
	
	/**
	 * Calculatesd the real time delay on this action, based on the base time delay sent by the client and the 
	 * player's skill mods for that combat action.
	 * @param player -- The player executing the action.
	 * @param baseTimeDelay  -- The base time delay, in seconds, sent by the client.
	 * @return The adjusted time delay on this action.
	 */
	private float calculateRealTimeDelay(Player player, float baseTimeDelay) {
		// The CommandQueueItem will use this function to get at the players' skill mods, and calculate the acutal
		// combat delay time on this action.  (Obviously some actions have no delay time.)
		return 0f;
	}
	
	/**
	 * Construct a new CommandQueueItem for this player.
	 * @param CRC -- The CRC of the CommandQueueEnqueue command.
	 * @param baseTimer -- The base time passed to us for this action by the client.
	 * @param unk1 -- An unknown paramater
	 * @param unk2 -- An unknown paramater
	 * @param skillsRequired -- The skill(s) required to execute this action.
	 * @param player -- The player executing this action.
	 */
	public CommandQueueItem(int CRC, float baseTimer, int unk1, int commandID, int skillsRequired, Player player) {
		//commandOwner = player;
		commandCRC = CRC;
		iErrorMessageID = unk1;
		this.commandID = commandID;
		iSkillBitSetRequired = skillsRequired;
		//commandTimer = calculateRealTimeDelay(commandOwner, baseTimer);
	}
	
	/**
	 * Set the ID of this command.
	 * @param i -- the ID of this command.
	 */
	protected void setCommandID(int i) {
		commandID = i;
	}
	
	/**
	 * Gets the ID of this command.
	 * @return The ID of this command.
	 */
	protected int getCommandID() {
		return commandID;
	}
	/**
	 * Gets the time delay attached to this command.  When the delay reaches 0, the next command may be executed.
	 * @return The time delay, in seconds, attached to the execution of this command.
	 */
	protected float getTimer() {
		return (commandTimerMS/1000.0f);
	}
	/**
	 * Get the first unknown paramater for this command.
	 * @return Unknown Paramater 1.
	 */
	protected int getErrorMessageID() {
		return iErrorMessageID;
	}
	/**
	 * Get the action CRC for this command
	 * @return The CRC for this command.
	 */
	protected int getCRC() {
		return commandCRC;
	}
	
	/**
	 * Get any state which may be caused by executing this command.
	 * @return The state invoked by this command.
	 */
	protected int getInvokedState() {
		return iStateInvoked;
	}
	
	/**
	 * Get the required skill ID(s) to execute this command.
	 * @return The skill ID(s) needed for this command.
	 */
	protected int getSkillIDRequired() {
		return iSkillBitSetRequired;
	}
	
	public void setCommandCRC(int commandCRC) {
		this.commandCRC = commandCRC;
	}

	public void setCommandTimer(float commandTimer) {
		commandTimerMS = (long)(commandTimer * 1000.0f);
	}

	public void setCommandTimerMS(long lTimeMS) {
		commandTimerMS = lTimeMS;
	}
	
	public long getCommandTimerMS() {
		return commandTimerMS;
	}
	
	public void setErrorMessageID(int errorMessageID) {
		iErrorMessageID = errorMessageID;
	}

	public void setSkillBitSetRequired(int skillBitSetRequired) {
		iSkillBitSetRequired = skillBitSetRequired;
	}

	public void setStateInvoked(int stateInvoked) {
		iStateInvoked = stateInvoked;
	}

	/*public void setCommandOwner(Player commandOwner) {
		this.commandOwner = commandOwner;
	}*/

	public void setHasBeenSent(boolean bHasBeenSent) {
		this.bHasBeenSent = bHasBeenSent;
	}

	public boolean hasBeenSent() {
		return bHasBeenSent;
	}

	public void setCombatAction(CombatAction combatAction) {
		this.combatAction = combatAction;
	}

	public CombatAction getCombatAction() {
		return combatAction;
	}

	public void setTargetID(long lTargetID) {
		this.lTargetID = lTargetID;
	}

	public long getTargetID() {
		return lTargetID;
	}

	public void setParams(String[] sParams) {
		this.sParams = sParams;
	}

	public String[] getParams() {
		return sParams;
	}

}

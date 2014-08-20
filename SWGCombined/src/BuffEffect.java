
public class BuffEffect {
	private int iBuffIndex;
	private int iBuffPotency;
	private Player affectedPlayer;
	private long lStateApplied;
	private long lTimeRemainingMS;
	private int iBuffHamIndex;
	private float fEffectChance = 100.0f;
	private int iStanceApplied; // Posture up, Posture Down, or Knockdown.  So, STANCE_STANDING, STANCE_PRONE, or STANCE_KNOCKED_DOWN
	
	public BuffEffect() {
		
	}
	
	public BuffEffect copy() {
		BuffEffect copyEffect = new BuffEffect();
		copyEffect.setBuffIndex(iBuffIndex);
		copyEffect.setBuffPotency(iBuffPotency);
		copyEffect.setStateApplied(lStateApplied);
		copyEffect.setTimeRemainingMS(lTimeRemainingMS);
		copyEffect.setBuffHamIndex(iBuffHamIndex);
		return copyEffect;
	}

	public void update(long lDeltaTimeMS) {
		lTimeRemainingMS -= lDeltaTimeMS;
		if (lTimeRemainingMS <= 0) {
			// Remove effect.
			affectedPlayer.removeBuffEffect(iBuffIndex);
		}
	}

	public int getBuffIndex() {
		return iBuffIndex;
	}

	public void setBuffIndex(int buffIndex) {
		iBuffIndex = buffIndex;
	}

	public int getBuffPotency() {
		return iBuffPotency;
	}

	public void setBuffPotency(int buffPotency) {
		iBuffPotency = buffPotency;
	}

	public Player getAffectedPlayer() {
		return affectedPlayer;
	}

	public void setAffectedPlayer(Player affectedPlayer) {
		this.affectedPlayer = affectedPlayer;
	}

	public long getStateApplied() {
		return lStateApplied;
	}

	public void setStateApplied(long stateApplied) {
		lStateApplied = stateApplied;
	}

	public long getTimeRemainingMS() {
		return lTimeRemainingMS;
	}

	public void setTimeRemainingMS(long timeRemainingMS) {
		lTimeRemainingMS = timeRemainingMS;
	}

	public void setBuffHamIndex(int iBuffHamIndex) {
		this.iBuffHamIndex = iBuffHamIndex;
	}

	public int getBuffHamIndex() {
		return iBuffHamIndex;
	}

	public void setEffectChance(float fEffectChance) {
		this.fEffectChance = fEffectChance;
	}

	public float getEffectChance() {
		return fEffectChance;
	}

	public void setStanceApplied(int iStanceApplied) {
		this.iStanceApplied = iStanceApplied;
	}

	public int getStanceApplied() {
		return iStanceApplied;
	}
	
	
	
	
}

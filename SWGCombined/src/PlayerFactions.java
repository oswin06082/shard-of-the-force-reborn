import java.io.Serializable;


public class PlayerFactions implements Serializable{
	public final static long serialVersionUID = 1l;
	private String sFactionName;
	private float iFactionValue;
	public PlayerFactions(String sFactionName, float iFactionValue) {
		this.sFactionName = sFactionName;
		this.iFactionValue = iFactionValue;
	}
	
	protected float getFactionValue() {
		return iFactionValue;
	}
	
	protected void setFactionValue(float iFactionValue) {
		this.iFactionValue = iFactionValue;
	}
	
	protected String getFactionName() {
		return sFactionName;
	}
	
	protected void setFactionName(String sFactionName) {
		this.sFactionName = sFactionName;
	}
	
}

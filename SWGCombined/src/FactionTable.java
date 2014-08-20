import java.util.Enumeration;
import java.util.Hashtable;


public class FactionTable {
	private int iFactionID;
	private Hashtable<Integer, FactionTable> vOpposingFactions;
	private Hashtable<Integer, FactionTable> vFriendlyFactions;

	public FactionTable(int factionID) {
		iFactionID = factionID;
		vOpposingFactions = new Hashtable<Integer, FactionTable>();
		vFriendlyFactions = new Hashtable<Integer, FactionTable>();
	}
	
	protected int getFactionID() {
		return iFactionID;
	}
	protected void addOpposingFaction(FactionTable faction) {
		vOpposingFactions.put(faction.getFactionID(), faction);
	}
	
	protected FactionTable getOpposingFaction(int factionID) {
		return vOpposingFactions.get(factionID);
	}
	
	protected Enumeration<FactionTable> getAllOpposingFactionsEnum() {
		return vOpposingFactions.elements(); 
	}
	
	protected void addFriendlyFaction(FactionTable faction) {
		vFriendlyFactions.put(faction.getFactionID(), faction);
	}
	
	protected FactionTable getFriendlyFaction(int factionID) {
		return vFriendlyFactions.get(factionID);
	}
	
	protected Enumeration<FactionTable> getAllFriendlyFactionsEnum() {
		return vFriendlyFactions.elements(); 
	}
	

}

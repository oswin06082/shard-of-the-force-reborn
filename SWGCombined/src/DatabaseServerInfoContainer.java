/**
 * Container class for settings on a given Zone Server.
 * @author Darryl
 *
 */
public class DatabaseServerInfoContainer {
	public int iServerID = 0;
	public String sServerName = "";
	public String sRemoteAddress = "";
	public String sLocalAddress = "";
	public int iZonePort = 0;
	public int iCurrentPopulation = 0;
	public int iMaxPopulation = 0;
	public int iMaxCharactersPerAccount = 0;
	public byte iServerStatus = 0;
	public int iPingPort = 0;
	public int iTimeOffset = 0;
	public String sMotd = "";
	public boolean bDevOnlyServer = false;
	
	public DatabaseServerInfoContainer() {
		
	}
	
}

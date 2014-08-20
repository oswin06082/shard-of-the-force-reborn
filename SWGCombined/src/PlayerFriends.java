import java.io.Serializable;

/**
 * The PlayerFriends class is a simple class that associates a given Player Friends' name with a Zone Server.
 * This class is needed since Players may have friends with the same Character name on different Zone Servers.
 * @author Darryl
 *
 */
public class PlayerFriends implements Serializable {
	public final static long serialVersionUID = 1l;
	private String sName;
	private String sServerName;

	/**
	 * Construct a new Player Friends item, with the given friend name, residing on the given Zone Server name.
	 * @param name
	 * @param server
	 */
	public PlayerFriends(String name, String server) {
		sName = name;
		sServerName = server;
	}
	
	/**
	 * Get the Player Friend's name.
	 * @return The name.
	 */
	public String getName() {
		return sName;
	}
	
	/**
	 * Get the Player Friend's server name.
	 * @return The server name.
	 */
	public String getServerName() {
		return sServerName;
	}

}

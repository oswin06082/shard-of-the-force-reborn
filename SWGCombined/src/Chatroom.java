import java.util.Vector;

/**
 * Container class for a Chatroom object.
 * @author Darryl
 *
 */
public class Chatroom {
	private String sRoomName;
	private String sCreatorName;
	private String sRoomDescription;
	private Vector<String> vModerators;
	private Vector<String> vPlayersInRoom;
	private ChatServer vMyServer;
	/**
	 * Constructs a ChatRoom on the given ChatServer, with a given name, creator, and description. 
	 * @param s -- The ChatServer which this room is to exist on.
	 * @param room -- The room name.
	 * @param creator -- The name of the creator of this ChatRoom.
	 * @param description -- The description of this ChatRoom.
	 */
	public Chatroom(ChatServer s, String room, String creator, String description) {
		vMyServer = s;
		sRoomName = room;
		sCreatorName = creator;
		sRoomDescription = description;
	}
	
	/**
	 * Get the name of this ChatRoom.
	 * @return The ChatRoom name.
	 */
	public String getRoomName() {
		return sRoomName;
	}
	
	/**
	 * Get the name of the creator of this ChatRoom.
	 * @return The creator's name.
	 */
	public String getCreator() {
		return sCreatorName;
	}
	
	/**
	 * Get the description of this ChatRoom.
	 * @return The ChatRoom description.
	 */
	public String getDescription() {
		return sRoomDescription;
	}
	
	/**
	 * Get the list of the names of the Moderators for this ChatRoom
	 * @return The Moderator name list.
	 */
	public Vector<String> getModeratorList() {
		return vModerators;
	}
	
	/**
	 * Get the list of the names of the Players in this ChatRoom.
	 * @return The Player name list.
	 */
	public Vector<String> getPlayersInRoom() {
		return vPlayersInRoom;
	}
	
	/**
	 * Return the parent server which this ChatRoom resides on.
	 * @return The ChatServer to which this ChatRoom belongs.
	 */
	public ChatServer getServer() {
		return vMyServer;
	}
}

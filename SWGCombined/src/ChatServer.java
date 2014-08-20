import java.util.Vector;

/**
 * A ChatServer is a server dedicated to handling all activity in ChatRooms for a given ZoneServer.
 * It is believed to be more or less like an IRC server.
 * @author Darryl
 *
 */
public class ChatServer implements Runnable{
	private Vector<Chatroom> vRoomsInServer;
	private int serverID;
	private ZoneServer zServer;
	private Thread myThread;
        
	/**
	 * Constructs a ChatServer belonging to the given ZoneServer.
	 * @param server -- The ZoneServer this ChatServer is handling.
	 */
	public ChatServer(ZoneServer server) {
		zServer = server;
		myThread = new Thread(this);
		//myThread.start();
	}
	
	/**
	 * Gets the ID of this ChatServer.
	 * @return The ID of this ChatServer.
	 */
	public int getID() {
		return serverID;
	}
	/**
	 * Get a list of rooms on this ChatServer.
	 * @return The list of ChatRooms currently created on this ChatServer.
	 */
	public Vector<Chatroom> getRooms() {
		return vRoomsInServer;
	}
	
	/**
	 * The run function of this ChatServer's thread.  Loops through each room and updates it's status.
	 * If any chat messages are pending to be sent, it sends them to all the connected players.
	 */
	public void run() {
		
		while (myThread != null) {
			try {
				synchronized(this) {
					Thread.yield();
					wait(100);
				}
			} catch (Exception e) {
				System.out.println("Error in ChatRoom thread: " + e.toString());
				e.printStackTrace();
			}
		}
	}
	
	protected ZoneServer getZoneServer() { 
		return zServer;
	}
}

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

/**
 * The ZoneServerPacketSender is responsible solely for sending as many UDP packets to various clients, as fast as it can. 
 * @author Darryl
 *
 */
public class ZoneServerPacketSender implements Runnable {
	private Thread myThread;
	//private ZoneServer server;
	private DatagramSocket serverSocket;
	private List<DatagramPacket> vPacketsToSend;
	
	/**
	 * Constructs a new packet sender for the given Zone Server.
	 * @param server -- The Zone Server which this ZoneServerPacketSender is to run on.
	 */
	public ZoneServerPacketSender(ZoneServer server) {
		//this.server = server;
		serverSocket = server.getSocket();
		vPacketsToSend = server.getSendPacketQueue();
		myThread = new Thread(this);
		myThread.setName("ZoneServerPacketSender thread");
		myThread.start();
	}
	
	/**
	 * The main thread loop.
	 */
	public void run() {
                
		while (myThread != null) {
			try {
				synchronized(this) {
					Thread.yield();
					wait(10);
				}
				synchronized(vPacketsToSend) {
					if (!vPacketsToSend.isEmpty()) {
						// While the packet queue is not empty, send the first packet in the queue.... how can that possibly throw no such element exceptions?
						//long lStartSendTime = System.nanoTime();
						serverSocket.send(vPacketsToSend.remove(0));
						//long lEndSendTime = System.nanoTime();
						//long lDeltaSendTime = lEndSendTime - lStartSendTime;
						//System.out.println("Took " + lDeltaSendTime + " nanoseconds to send a packet.");
						//System.out.flush();//
					}
				}
			} catch (Exception e) {
				System.out.println("ServerPacketSender thread exception: " + e.toString());
				e.printStackTrace();
			}
		}
	}
}

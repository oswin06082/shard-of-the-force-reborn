import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * The Ping Server is a simple server designed to bounce any incoming packets back to the sender.
 * Only 1 Ping Server should ever run for each Zone Server.
 * @author Darryl
 *
 */
public class PingServer implements Runnable {

	private DatagramSocket socket;
	private Thread myThread;
	private int iPort;
	private DatagramPacket incomingPacket;
	/**
	 * Constructs a new Ping Server to listen on the specified port.
	 * @param port -- The port.
	 */
	public PingServer(int port) {
		try {
			socket = new DatagramSocket(port);
			socket.setSoTimeout(10);
		} catch (IOException e) {
			System.out.println("Error binding to port " + port + ": " + e.toString());
			e.printStackTrace();
			socket = null;
		}
		iPort = port;
		incomingPacket = new DatagramPacket(new byte[496], 496);
		myThread = new Thread(this);
		myThread.setName("PingServer thread");
		myThread.start();
	}

	/**
	 * Main loop of the Ping Server.
	 */
	public void run() {
		while (myThread != null) {
			try {
				synchronized(this) {
					try {
						Thread.yield();
						wait(100);
					} catch (Exception e) {
						// Who cares.
					}
				}
				//byte[] incBuffer = new byte[496];
				socket.receive(incomingPacket);
				byte[] incBuffer = incomingPacket.getData();
				byte[] realData = Arrays.copyOfRange(incBuffer, 0, incomingPacket.getLength());
				DatagramPacket outgoingPacket = new DatagramPacket(realData, realData.length, incomingPacket.getSocketAddress());
				socket.send(outgoingPacket);
			} catch (SocketTimeoutException ee) {
				// Who cares
			} catch (IOException e) {
				System.out.println("Error in ping handler: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the port the Ping Server is currently operating on.
	 * @return The port.
	 */
	public int getPort() {
		return iPort;
	}
}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoginServerZoneTransciever implements Runnable{
	
	private ServerSocket serverSocket;
	private Thread myThread;
	private LoginServer server;
	public LoginServerZoneTransciever(LoginServer server, int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10);
		serverSocket.setReceiveBufferSize(496);
		this.server = server;
		myThread = new Thread(this);
		myThread.start();
		DataLog.logEntry("LoginZoneTransciever listening on address " + serverSocket.getInetAddress().toString() + ":" + port,"LoginServerZoneTransciever",Constants.LOG_SEVERITY_INFO,true,true);
	}
	
	public void run() {
		while (myThread != null) {
			try {
				synchronized(this){
					Thread.yield();
					wait(100);
				}
				Socket socket = serverSocket.accept();
				
				new LoginZoneCommunicationThread(server, socket);
			} catch (Exception e) {
				// D'oh!
			}
		}
	}
	
	public void kill() {
		try {
			serverSocket.close();
		} catch (Exception e) {
			// D'oh!
		}
		myThread = null;
	}
	
}

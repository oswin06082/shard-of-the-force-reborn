import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class LicenseTransciever implements Runnable{
	private final static byte CONNECTION_REQUEST = 0;
	private final static byte CONNECTION_RESPONSE = 1;
	private final static byte PING = 2;
	private final static byte DISCONNECT = 3;
	
	private Socket socket;
	private Thread myThread;
	private DataInputStream dIn;
	private DataOutputStream dOut;
	boolean bFlaggedForRemoval = false;
	private SWGLicenseServer server;
	public LicenseTransciever(SWGLicenseServer server, Socket socket) {
		this.server = server;
		try {
			this.socket = socket;
			dOut = new DataOutputStream(socket.getOutputStream());
			dIn = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.out.println("Error initializing socket streams -- unable to continue with this connection.");
			bFlaggedForRemoval = true;
		}
		myThread = new Thread(this);
		myThread.start();
	}
	
	public void run() {
		while (myThread != null) {
	 		try {
	 			Thread.yield();
	 			if (bFlaggedForRemoval) {
	 				server.removeTransciever(this);
	 				myThread = null;
	 			} else {
		 			byte opcode = dIn.readByte();
		 			switch (opcode) {
			 			case CONNECTION_REQUEST: {
			 				String sUsername = dIn.readUTF();
			 				String sPassword = dIn.readUTF();
			 				sendConnectionResponse(server.authenticate(sUsername, sPassword));
			 				break;
			 			}
			 			case CONNECTION_RESPONSE: {
			 				// Should never happen.
			 				System.out.println("Authentication server received connection response -- WTF???");
			 				break;
			 			}
			 			case PING: {
			 				sendPong();
			 				break;
			 			}
			 			case DISCONNECT: {
			 				bFlaggedForRemoval = true;
			 				break;
			 			}
			 			default: {
			 				System.out.println("LicenseTransciever:  Unhandled opcode from server: " + Integer.toHexString(opcode));
			 			}
		 			}
	 			}
			} catch (Exception e) {
				// D'oh!
			}
		}
	}
	
	private void sendPong() throws IOException{
		dOut.writeByte(PING);
		dOut.flush();
	}
	
	private void sendConnectionResponse(byte iAuthStatus) throws IOException {
		dOut.writeByte(CONNECTION_RESPONSE);
		dOut.writeByte(iAuthStatus);
		dOut.flush();
	}
	
}

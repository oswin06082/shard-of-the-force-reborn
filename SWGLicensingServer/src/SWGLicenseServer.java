import java.sql.Statement;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.Connection;
import java.util.Vector;


public class SWGLicenseServer implements Runnable{
	private ServerSocket socket;
	private int iServerPort = 44410;
	private Thread myThread;
	private Connection conn;
	private Vector<AuthenticationData> vAuthenticationData;
	private Vector<LicenseTransciever> vTranscieverList;
	private final static byte AUTHENTICATION_STATUS_APPROVED = 0;
	private final static byte AUTHENTICATION_STATUS_INVALID_USERNAME = 1;
	private final static byte AUTHENTICATION_STATUS_INVALID_PASSWORD = 2;
	public static void main(String[] args) {
		String sUsername = args[0];
		String sPassword = args[1];
		String sDatabaseServerAddress = args[2];
		String sSchemaName = args[3];
		int port = Integer.parseInt(args[4]);
		SWGLicenseServer server = new SWGLicenseServer(sUsername, sPassword, sDatabaseServerAddress, sSchemaName, port);
		System.out.println("Server created.");
	}
	

	private SWGLicenseServer(String sUsername, String sPassword, String sDatabaseAddress, String sSchemaName, int iDatabasePort) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println("Unable to load MySQL/J connector -- terminating.");
			System.exit(-1);
		}
		StringBuffer s = new StringBuffer();
		s.append("jdbc:mysql://");
		s.append(sDatabaseAddress).append(":").append(iDatabasePort);
		s.append("/");
		s.append(sSchemaName);
		s.append("?user=");
		s.append(sUsername);
		s.append("&password=");
		s.append(sPassword);
		String sConnString = s.toString();
		System.out.println("Connection string: " + sConnString);
		try {
			conn = DriverManager.getConnection(sConnString); // Throws an SQLException if the connection attempt fails.
		} catch (Exception e) {
			System.out.println("Error initializing database connection: " + e.toString() + ", terminating.");
			System.exit(-1);
		}
		try {
			socket = new ServerSocket(iServerPort);
		} catch (IOException e) {
			System.out.println("Error binding server socket: " + e.toString() + ", terminating.");
		}
		vAuthenticationData = new Vector<AuthenticationData>();
		vTranscieverList = new Vector<LicenseTransciever>();
		initialize();
		myThread = new Thread(this);
		myThread.start();
	}
	
	private void initialize() {
		String query = "Select * from authtable;";
		try {
			Statement s = conn.createStatement();
			if (s.execute(query)) {
				ResultSet r = s.getResultSet();
				while (r.next()) {
					String sUsername = r.getString("username");
					String sPassword = r.getString("password");
					boolean bAllowedToRun = r.getBoolean("active");
					AuthenticationData authData = new AuthenticationData();
					authData.setUsername(sUsername);
					authData.setPassword(sPassword);
					authData.setAllowedAuth(bAllowedToRun);
					vAuthenticationData.add(authData);
				}
			}
			System.out.println("Loaded authentication table.  Number of valid servers: " + vAuthenticationData.size());
		} catch (Exception e) {
			System.out.println("Error loading authentication table: " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (myThread != null) {
			try {
				Thread.yield();
				Socket clientSocket = socket.accept();
				LicenseTransciever transciever = new LicenseTransciever(this, clientSocket);
				vTranscieverList.add(transciever);
			} catch (Exception e) {
				// D'oh!
			}
		}
	}
	
	public void removeTransciever(LicenseTransciever transciever) {
		vTranscieverList.remove(transciever);
	}
	
	protected byte authenticate(String sUsername, String sPassword) {
		for (int i = 0; i < vAuthenticationData.size(); i++) {
			AuthenticationData authData = vAuthenticationData.elementAt(i);
			if (authData.getUsername().equalsIgnoreCase(sUsername)) {
				if (authData.getPassword().equalsIgnoreCase(sPassword)) {
					return AUTHENTICATION_STATUS_APPROVED;
				} else {
					return AUTHENTICATION_STATUS_INVALID_PASSWORD;
				}
			}
		}
		return AUTHENTICATION_STATUS_INVALID_USERNAME;
	}
}

package net.persistentworlds.toolkit.configuration;
import java.io.Serializable;

public class DatabaseConfiguration implements Serializable {

	//Constants.
	private static final long serialVersionUID = 1L;
	
	//Instance variables.
	private String databaseHost;
	private String databaseUserName;
	private String databasePassword;
	private int databasePort;
	private String databaseSchema;
	private transient boolean saveConnection;
	
	public DatabaseConfiguration() {
		databaseHost = "127.0.0.1";
		databaseUserName = "root";
		databasePassword = null;
		databasePort = 3306;
		databaseSchema = "/pwemu";
		saveConnection = true;
	}

	public String getDatabaseHost() {
		return databaseHost;
	}

	public void setDatabaseHost(String databaseHost) {
		this.databaseHost = databaseHost;
	}

	public String getDatabaseUserName() {
		return databaseUserName;
	}

	public void setDatabaseUserName(String databaseUserName) {
		this.databaseUserName = databaseUserName;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public int getDatabasePort() {
		return databasePort;
	}

	public void setDatabasePort(int databasePort) {
		this.databasePort = databasePort;
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}

	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	public boolean isSaveConnectionEnabled() {
		return saveConnection;
	}

	public void enableSaveConnection(boolean saveConnection) {
		this.saveConnection = saveConnection;
	}
}

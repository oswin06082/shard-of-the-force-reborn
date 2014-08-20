package net.persistentworlds.config;
import java.io.Serializable;

/**
 * This object is a container for all configuration data.
 * Most of the data is stored in primitive types.
 * This will be changed in the future.
 * 
 * @author Interesting
 * @note Interesting is way too <s>busy</s> lazy to write JavaDoc for get/set methods in this object.
 */
public class Config implements Serializable {
	/*
	 * NOTICE: Update this time you change ANY VARIABLE.
	 */
	private static final long serialVersionUID = 3L;
	
	/* GENERAL */
	private String webPath;
	private String scriptPath;
	
	/* SERVER */
	private int zoneServerID;
	private int loginServerPort;
	private int zoneServerPort;
	private boolean enableLoginServer;
	private boolean enableZoneServer;
	private String remoteLoginServerAddress;
	private int remoteLoginServerPort;
	
	/* DATABASE */
	private String databaseAddress;
	private int databasePort;
	private String databaseUsername;
	private String databasePassword;
	private String databaseSchema;
	private boolean enableSecurePasswords;
	
	/* GAME */
	private boolean autoRegisterAccounts;
	private boolean enableTanaab;
	private boolean enableUnknownPlanet;
	private boolean enableSameFactionHunting;
	
	/* REMOTE AUTHORIZATION */
	private String sRemoteAuthorizationUsername;
	private String sRemoteAuthorizationPassword;
	
	public Config() {
		webPath = ""; //No default.
		scriptPath = ""; //No default.
		zoneServerID = 2;
		loginServerPort = 44453;
		zoneServerPort = 44463;
		enableLoginServer = true;
		enableZoneServer = true;
		remoteLoginServerAddress = "localhost";
		remoteLoginServerPort = 35000;
		databaseAddress = "localhost";
		databasePort = 3306;
		databaseUsername = "root";
		databasePassword = "";	//No default.
		databaseSchema = "/pwemu";
		enableSecurePasswords = true;
		autoRegisterAccounts = true;
		enableTanaab = false;
		enableUnknownPlanet = false;
		enableSameFactionHunting = true;
		sRemoteAuthorizationUsername = null;
		sRemoteAuthorizationPassword = null;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String path) {
		webPath = path;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String path) {
		scriptPath = path;
	}

	public int getZoneServerID() {
		return zoneServerID;
	}

	public void setZoneServerID(int ID) {
		zoneServerID = ID;
	}

	public int getLoginServerPort() {
		return loginServerPort;
	}

	public void setLoginServerPort(int port) {
		loginServerPort = port;
	}

	public int getZoneServerPort() {
		return zoneServerPort;
	}

	public void setZoneServerPort(int port) {
		zoneServerPort = port;
	}

	public boolean isLoginServerEnabled() {
		return enableLoginServer;
	}

	public void setLoginServerEnabled(boolean enable) {
		enableLoginServer = enable;
	}
	
	public boolean isZoneServerEnabled() {
		return enableZoneServer;
	}

	public void setZoneServerEnabled(boolean enable) {
		enableZoneServer = enable;
	}	

	public String getRemoteLoginServerAddress() {
		return remoteLoginServerAddress;
	}

	public void setRemoteLoginServerAddress(String address) {
		remoteLoginServerAddress = address;
	}

	public int getRemoteLoginServerPort() {
		return remoteLoginServerPort;
	}

	public void setRemoteLoginServerPort(int port) {
		remoteLoginServerPort = port;
	}

	public String getDatabaseAddress() {
		return databaseAddress;
	}

	public void setDatabaseAddress(String address) {
		databaseAddress = address;
	}

	public int getDatabasePort() {
		return databasePort;
	}

	public void setDatabasePort(int port) {
		databasePort = port;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public void setDatabaseUsername(String username) {
		databaseUsername = username;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String password) {
		databasePassword = password;
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}

	public void setDatabaseSchema(String schema) {
		if(schema.startsWith("/")) {
			databaseSchema = schema;
		} else {
			databaseSchema = "/" + schema;
		}
	}

	public boolean isSecurePasswordsEnabled() {
		return enableSecurePasswords;
	}

	public void setSecurePasswordsEnabled(boolean enable) {
		enableSecurePasswords = enable;
	}

	public boolean isAutoAccountRegistrationEnabled() {
		return autoRegisterAccounts;
	}

	public void setAutoAccountRegistrationEnabled(boolean enable) {
		autoRegisterAccounts = enable;
	}

	public boolean isTanaabEnabled() {
		return enableTanaab;
	}

	public void setTanaabEnabled(boolean enable) {
		enableTanaab = enable;
	}

	public boolean isUnknownPlanetEnabled() {
		return enableUnknownPlanet;
	}

	public void setUnknownPlanetEnabled(boolean enable) {
		enableUnknownPlanet = enable;
	}

	public boolean isSameFactionHuntingEnabled() {
		return enableSameFactionHunting;
	}

	public void setEnableSameFactionHunting(boolean enable) {
		enableSameFactionHunting = enable;
	}

	public void setRemoteAuthorizationUsername(String sRemoteAuthorizationUsername) {
		this.sRemoteAuthorizationUsername = sRemoteAuthorizationUsername;
	}

	public String getRemoteAuthorizationUsername() {
		return sRemoteAuthorizationUsername;
	}

	public void setRemoteAuthorizationPassword(String sRemoteAuthorizationPassword) {
		this.sRemoteAuthorizationPassword = sRemoteAuthorizationPassword;
	}

	public String getRemoteAuthorizationPassword() {
		return sRemoteAuthorizationPassword;
	}
}
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import net.persistentworlds.config.Config;

public class ConfigDebugger {
	
	//Instance variables.
	private BufferedWriter out;

	public ConfigDebugger() throws IOException {
		out = new BufferedWriter(new FileWriter(new File("Config.txt")));
	}
	
	public void showConfiguration(Config configuration) throws IOException {
		
		//Parse Configuration info.
		log("= General =");
		log("Script path: " + configuration.getScriptPath());
		log("Web path: " + configuration.getWebPath());
		log("\r\n= Server =");
		log("Zone server ID: " + configuration.getZoneServerID());
		log("Login server port: " + configuration.getLoginServerPort());
		log("Zone server port: " + configuration.getZoneServerPort());
		log("Enable Login server: " + configuration.isLoginServerEnabled());
		log("Enable Zone server: " + configuration.isZoneServerEnabled());
		log("Remote Login server address: " + configuration.getRemoteLoginServerAddress());
		log("Remote Login server port: " + configuration.getRemoteLoginServerPort());
		log("\r\n= Database =");
		log("Database address: " + configuration.getDatabaseAddress());
		log("Database port: " + configuration.getDatabasePort());
		log("Database username: " + configuration.getDatabaseUsername());
		log("Database password: " + configuration.getDatabasePassword());
		log("Database schema: " + configuration.getDatabaseSchema());
		log("Enable secure passwords: " + configuration.isSecurePasswordsEnabled());
		log("\r\n= Game =");
		log("Auto register accounts: " + configuration.isAutoAccountRegistrationEnabled());
		log("Enable tanaab: " + configuration.isTanaabEnabled());
		log("Enable unknown planet: " + configuration.isUnknownPlanetEnabled());
		log("Enable same faction hunting: " + configuration.isSameFactionHuntingEnabled());
		log("\r\n= Authentication =");
		log("Remote Authorization Username: " + configuration.getRemoteAuthorizationUsername());
		log("Remote Authorization Password: " + configuration.getRemoteAuthorizationPassword());
		
		out.close();
	}
	
	private void log(String line) throws IOException {
		out.write(line);
		out.newLine();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ConfigDebugger debugger = new ConfigDebugger();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("Config.dat")));
		Config currentConfiguration = (Config) in.readObject();
		debugger.showConfiguration(currentConfiguration);
	}	
}

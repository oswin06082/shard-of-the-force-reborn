package net.persistentworlds.toolkit.configuration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.persistentworlds.toolkit.ToolkitConstants;
import net.persistentworlds.toolkit.ToolkitUtilities;

public class ConfigurationInterface {

	
	public static DatabaseConfiguration loadDatabaseConfiguration() {
		//Instance variables.
		DatabaseConfiguration configuration = null;
		
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(ToolkitConstants.CONFIGURATION_FILENAME_DATABASE_CONNECTION));
			configuration = (DatabaseConfiguration) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			//don't do anything.
		} catch (IOException e) {
			ToolkitUtilities.handleFatalException(e);
		} catch (ClassNotFoundException e) {
			ToolkitUtilities.handleFatalException(e);
		}
		
		//Return loaded configuration, or null if none could be loaded.
		return configuration;
	}
}

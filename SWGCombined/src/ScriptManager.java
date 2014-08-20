import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

/*
 * TODO: Re-write parsing methods to be callable at any time during server's operation instead of at startup. This way there won't have to be a specific method to reload scripts.
 */

/**
 * The ScriptManager class is responsible for loading, compiling, validating, and running scripts.
 * 
 * @author William "Interesting" Ledford
 * 
 */
public class ScriptManager {
	private static Hashtable<Long, Integer> cItemScripts;
	private static Hashtable<Integer, Integer> cSystemScripts;
	private static Vector<File> vScriptFiles;
	private static Hashtable<Integer, CompiledScript> cCompiledScripts;
	private ScriptEngine engine;
	private ZoneServer server;
	private ItemScriptAPI itemScriptAPI;
	private ScriptAPI scriptAPI;
	private boolean runScripts;
	private File rootScriptDirectory;

	/**
	 * Instantiate this server's ScriptManager.
	 * @param z The zone server running this ScriptManager.
	 * @param dir Directory where all the scripts are stored.
	 */
	public ScriptManager(ZoneServer z, String dir) {
		server = z;
		cItemScripts = new Hashtable<Long, Integer>();
		cSystemScripts = new Hashtable<Integer, Integer>();
		vScriptFiles = new Vector<File>();
		cCompiledScripts = new Hashtable<Integer, CompiledScript>();
		itemScriptAPI = new ItemScriptAPI(server);
		scriptAPI = new ScriptAPI(server);
		ScriptEngineManager factory = new ScriptEngineManager();
		engine = factory.getEngineByName("js");
		rootScriptDirectory = new File(dir);

		initScriptEngine();
	}

	/**
	 * Initialize the script engine.
	 * <br><br>The following takes place, if any of these tasks fail the script engine is disabled:
	 * <br>* - The script directory is recursively parsed, if it is a valid directory.
	 * <br>* - The number of script files loaded are validated.
	 * <br>* - The loaded script engine (javax.script.ScriptEngine) is checked for compile capability.
	 * <br>* - The number of compiled scripts are validated.
	 * <br>* - The loaded system script files are validated.
	 * <br>* - The loaded system script files are parsed.
	 * <br>* - The loaded system scripts are validated.
	 */
	private void initScriptEngine() {
		
		//Attempt to access the file based on the given directory.
		if(rootScriptDirectory.isDirectory()) {
			
			//If the path is valid, recursively parse all sub-directories.
			parseScriptFiles(rootScriptDirectory);
			
			//If there were files to load, begin compiling.
			if(vScriptFiles.size() > 0) {
				
				//Print a message with the number of scripts loaded.
				DataLog.logEntry(vScriptFiles.size() + " script files loaded.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
				
				//If the engine allows compiling, start compiling.
				if(engine instanceof Compilable) {
					compileScriptFiles();
					
					//If we actually had scripts to compile.
					if(cCompiledScripts.size() > 0) {
						
						//Print a message with the number of scripts compiled.
						DataLog.logEntry(cCompiledScripts.size() + " script files compiled.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
						
						//Check if the system scripts are valid.
						if(systemScriptsValid()) {
							
							//If they are, enable scripts.
							parseSystemScripts();
							
							//Check the loaded scripts.
							if(loadedSystemScriptsValid()) {
								
								//If everything is valid, enable scripts.
								runScripts = true;
							} else {
								
								//If they aren't valid, disable scripts.
								DataLog.logEntry("One or more System script could not be loaded, disabling ScriptEngine. If you wish to run the scripts, ensure the system scripts in " + rootScriptDirectory.getPath() + " are exactly the same as they are in the SVN trunk.","ScriptManager",Constants.LOG_SEVERITY_MAJOR,true,true);
								runScripts = false;								
							}
						} else {
							
							//If the aren't, disable scripts.
							DataLog.logEntry("One or more System script could not be loaded, disabling ScriptEngine. If you wish to run the scripts, ensure the system scripts in " + rootScriptDirectory.getPath() + " are exactly the same as they are in the SVN trunk.","ScriptManager",Constants.LOG_SEVERITY_MAJOR,true,true);
							runScripts = false;
						}
					} else {
						
						//If we didn't, disable scripts.
						DataLog.logEntry("No script files could be compiled, disabling ScriptEngine. If you wish to run the scripts, ensure there are files in " + rootScriptDirectory.getPath() + " and restart the server.","ScriptManager",Constants.LOG_SEVERITY_MAJOR,true,true);
						runScripts = false;
					}
				} else {
					
					//If the engine doesn't allow compiling, disable scripts.
					DataLog.logEntry("The " + ScriptEngine.LANGUAGE + " does not support compiling scripts. Compileable scripts is a requirement of the script engine, disabling ScriptEngine. If you wish to use scripts, remove any scripts using the " + ScriptEngine.LANGUAGE + " language and restart the server.","ScriptManager",Constants.LOG_SEVERITY_MAJOR,true,true);
					runScripts = false;
				}
			} else {
				
				//If there are no scripts in the directory, disable scripts.
				DataLog.logEntry(rootScriptDirectory.getPath() + " is empty, disabling ScriptEngine. If you wish to run scripts, populate the directory and restart the server..","ScriptManager",Constants.LOG_SEVERITY_MAJOR,true,true);
				runScripts = false;
			}
		} else {
			
			//If the directory is invalid, disable scripts.
			DataLog.logEntry(rootScriptDirectory.getPath() + " is not a valid directory, disabling ScriptEngine. If you wish to run scripts, create the directory and restart the server.","ScriptManager",Constants.LOG_SEVERITY_MAJOR,true,true);
			runScripts = false;
		}
	}

	/**
	 * Check the loaded files and compiled scripts for all required system scripts.
	 * @returns True if the required files are loaded/compiled, false if they are not.
	 */
	private boolean systemScriptsValid() {
		boolean valid = true;								//Variable to store validity.
		

		//Check the scripts.
		for(int k = 1; k < Constants.SYSTEM_SCRIPTS.length; k++) {
			//Variables.
			String currentSystemScriptName = Constants.SYSTEM_SCRIPTS[k];
			int currentSystemScriptID = getFileID(currentSystemScriptName);
			
			//Check loaded files.
			if(isSystemScriptFileLoaded(currentSystemScriptName)) {
				
				//Check compiled scripts.
				if(!cCompiledScripts.containsKey(currentSystemScriptID)) {
					
					//Invalid.
					DataLog.logEntry("System script " + currentSystemScriptName + " not found in compiled scripts.","ScriptManager",Constants.LOG_SEVERITY_MAJOR,true,true);
					valid = false;
				}
			} else {
				
				//Invalid.
				DataLog.logEntry("System script " + currentSystemScriptName + " not found in loaded script files.","ScriptManager",Constants.LOG_SEVERITY_MAJOR,true,true);
				valid = false;
			}
		}
		
		//Return the result.
		return valid;
	}
	
	/**
	 * Check if the required system scripts were parsed successfully.
	 * @returns True if they were parsed successfully, false if they were not.
	 */
	private boolean loadedSystemScriptsValid() {
		boolean valid = true;
		
		//Check the scripts.
		for(int k = 1; k < Constants.SYSTEM_SCRIPTS.length; k++) {
			
			String currentSystemScriptName = Constants.SYSTEM_SCRIPTS[k];
			int currentSystemScriptID = getFileID(currentSystemScriptName);				
			
			if(!cSystemScripts.containsValue(currentSystemScriptID)) {
				
				DataLog.logEntry("System script " + currentSystemScriptName + " not found in loaded scripts.", "ScriptManager", Constants.LOG_SEVERITY_MAJOR, true, true);
				valid = false;
				break;
			}
		}
		return valid;
	}
	
	/**
	 * Check if the specified system script file is loaded in the loaded script files.
	 * @param fileName The file name of the system script to look for.
	 * @returns True if the system script file is loaded, false if it isn't.
	 */
	private boolean isSystemScriptFileLoaded(String fileName) {
		//Store the result.
		boolean valid = false;
		
		//Check the script files.
		for(int k = 0; k < vScriptFiles.size(); k++) {
			
			String currentFileName = vScriptFiles.get(k).getName();
			
			if(currentFileName.equals(fileName)) {
				
				valid = true;
				break;
			}
		}
		
		//Return the result.
		return valid;
	}

	/**
	 * Parse all .js files in the specified directory recursively.
	 * @param currentFile The directory to parse.
	 */
	private void parseScriptFiles(File currentFile) {
		
		//If the file is a directory, recursively parse the files in the directory.
		if(currentFile.isDirectory()) {
			File[] subDirectory = currentFile.listFiles();
			
			//Recursively parse the files in the directory.
			for(int i = 0 ; i < subDirectory.length; i++) {
				parseScriptFiles(subDirectory[i]);
			}
		} else {
			
			//If the file is a file; and valid, add to our list of script files.
			if(currentFile.getName().endsWith(".js")) {
				
				//If the current file doesn't already exist in the loaded files
				if(!vScriptFiles.contains(currentFile)) {
					
					//Add it.
					vScriptFiles.add(currentFile);
				}
			}
		}
	}
	
	//TODO: Determine how I'm going to alert myself to errors in the compile process.
	protected void reloadScripts() {
		if(runScripts) {
			parseScriptFiles(rootScriptDirectory);
			compileScriptFiles();
			System.out.println("Loaded script files: " + vScriptFiles.size());
			System.out.println("Compiled script files: " + cCompiledScripts.size());
		}
	}

	/**
	 * Compile all loaded script files.
	 */
	private void compileScriptFiles() {
		//Instance variables.
		Enumeration<File> iterator = vScriptFiles.elements();
		Compilable ide = (Compilable) engine;
		
		//Continue compiling until we have scripts.
		while(iterator.hasMoreElements()) {
			File currentFile = iterator.nextElement();
			int currentFileID = getFileID(currentFile.getAbsolutePath());	//Get the ID of file we're compiling, and use it to store the file in the collection.
			
			//If the file is valid.
			if(currentFileID > -1) {
				try {
					//Read the contents of the script.
					FileReader in = new FileReader(currentFile);
					CompiledScript output = null;
					
					try {
						
						//Compile the script.
						output = ide.compile(in);
					} catch(ScriptException e) {
						
						//This means the script has a syntax error, and can't be compiled.
						//Ignore the script.
						System.out.println("Encountered error while compiling script " + currentFile.getName() + ", the script is being ignored.");
						System.out.println("Error: " + e.getMessage());
					}
					
					//Check if the script compiled successfully.
					if(output != null) {
						
						//Insert the script.
						cCompiledScripts.put(currentFileID, output);
						
						System.out.println(String.format("%s compiled.", vScriptFiles.get(currentFileID)));
					} else {
						
						//If the current file has a previous compile
						if(cCompiledScripts.get(currentFileID) != null) {
							
							System.out.println(String.format("Unable to re-compile %s, it's previous compile has been removed.", vScriptFiles.get(currentFileID)));
							
							//Delete its compile, it's out of date, even though the new version didn't compile.
							cCompiledScripts.remove(currentFileID);
						}
					}
					
				} catch(FileNotFoundException e) {
					
					//This should not happen unless the JVM encounters some sort of freakish error.
					System.out.println("FileReader in ScriptManager::compileScriptFiles() encountered impossible condition; " + currentFile.getName() + " is in the script file collection, but cannot be found.");
					System.out.println("Please report this error to the development team, the script will be ignored.");System.out.println("Please report this error to the development team, the script will be ignored.");
				}
			} else {
				
				//This should not happen unless the JVM encounters some sort of freakish error.
				System.out.println("ScriptEngine::compileScriptFiles() encountered impossible condition, " + currentFileID + " returned for file " + currentFile.getName());
				System.out.println("Please report this error to the development team, the script will be ignored.");
			}
		}
	}

	/**
	 * Get the index of the specified file relative to the loaded script files.
	 * @param targetFileName The name of the file to search for.
	 * @returns The index of the specified file in vScriptFiles, or -1 if it couldn't be found.
	 */
	private int getFileID(String targetFileName) {
		int id = -1;
		
		//Search the list of files for the file matching the target file name.
		for(int k = 0; k < vScriptFiles.size(); k++) {

			//If the name matches, success.
			if(vScriptFiles.get(k).getAbsolutePath().endsWith(targetFileName)) {
				id = k;
				break;	//Success, exit the loop.
			}
		}
		
		return id;
	}

	/**
	 * Parse item scripts. The information is taken from the database.
	 * @param itemTemplate The collection of item template information from the database.
	 */
	protected void parseItemScripts(Hashtable<Integer, ItemTemplate> itemTemplate) {
		Enumeration<ItemTemplate> templateData = itemTemplate.elements();
		
		//If scripts are enabled.
		if(runScripts) {
			
			//Parse the script files while we still have more data.
			while(templateData.hasMoreElements()) {
				//Instance variables.
				ItemTemplate itemData = templateData.nextElement();
				long iTemplateID = itemData.getTemplateID();
				int iScriptType = itemData.getScriptType();
				String sScriptName = itemData.getScriptName();

				//Check if we need to ignore the script or not.
				if(iScriptType != Constants.SCRIPT_TYPE_UNDEFINED && sScriptName.equals("noScript.js") == false) {
					
					//If we don't
					if(iScriptType == Constants.SCRIPT_TYPE_ITEM) {
						int iScriptFileID = getFileID(sScriptName);
						
						//And the script has a matching valid file.
						if(iScriptFileID > -1) {
							
							//Insert the matching ID into the collection.
							cItemScripts.put(iTemplateID, iScriptFileID);
						} else {
							//Else, ignore the script.
							DataLog.logEntry("Item Template ID #" + iTemplateID + " uses the script " + sScriptName + "; however, it was not found in the script directory. The script will be ignored..","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
						}
					} else {
						//Else, ignore the script.
						DataLog.logEntry("Item ID #" + iTemplateID + " has invalid scriptType (" + iScriptType + ", expected " + Constants.SCRIPT_TYPE_ITEM + ", the script will be ignored.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
					}
				}
			}
			
			//Output parsed item scripts.
			DataLog.logEntry("Loaded " + cItemScripts.size() + " item scripts from " + (itemTemplate.size() - 1) + " item_template entries.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);	//Minus 1, to account for entry 0 in DatabaseInterface
		} else {
			//Scripts are disabled, we have nothing to do.
			DataLog.logEntry("Unable to parse item scripts, scripts have been disabled.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
		}
	}
	
	/**
	 * Parse system script files.
	 */
	protected void parseSystemScripts() {
		//Get a pointer to the array for convenience.
		String[] systemScripts = Constants.SYSTEM_SCRIPTS;

		//Parse the scripts while we have more data.
		for(int k = 1; k < systemScripts.length; k++) {
			//Instance variables
			String sScriptName = systemScripts[k];
			int iScriptFileID = getFileID(sScriptName);

			//If script has a matching valid file.
			if(iScriptFileID > -1) {

				//Insert the matching ID into the collection.
				cSystemScripts.put(k, iScriptFileID);
			} else {
				//Else, ignore the script.
				DataLog.logEntry("System Script ID #" + k + " uses the script " + sScriptName + "; however, it was not found in the script directory. The script will be ignored..","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
			}
		}

		//Output parsed item scripts.
		DataLog.logEntry("Loaded " + cSystemScripts.size() + " system scripts from " + (systemScripts.length - 1) + " constant entries.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);		//Minus 1 to account for entry 0 in Constants
	}

	/**
	 * Handle running an item script.
	 * @param parentObjectID The ID of the object that called this script, this is the index of the item script in the loaded item scripts.
	 * @param targetID The ID of the player or object that called this script.
	 * @throws ScriptException If an error occurred while running the item script.
	 */
	private void handleRunItem(long parentObjectID, long targetID) throws ScriptException {
		DataLog.logEntry("Item Script for item " + parentObjectID + " attempting to run.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
		
		//If the object ID is invalid.
		if(cItemScripts.containsKey(parentObjectID)) {
			//Insert variables into the script engine.
			SimpleScriptContext settings = new SimpleScriptContext();
			settings.setAttribute("api", itemScriptAPI, ScriptContext.ENGINE_SCOPE);
			settings.setAttribute("itemTemplateID", parentObjectID, ScriptContext.ENGINE_SCOPE);
			settings.setAttribute("targetPlayerID", targetID, ScriptContext.ENGINE_SCOPE);
			
			try {
				int iCompiledScriptID = cItemScripts.get(parentObjectID);
				
				//If the object has a valid matching compiled script.
				if(cCompiledScripts.containsKey(iCompiledScriptID)) {
				
					//Get the compiled script.
					CompiledScript script = cCompiledScripts.get(iCompiledScriptID);
					
					//If the script exists.
					if(script != null) {
						//Run the script.
						script.eval(settings);
					} else {
						//The object has an invalid script, exit.
						throw new ScriptException("ScriptManager::handleRunItem() passed an objectID (" + parentObjectID + ") for a script/compiled script that doesn't exist, or is disabled/being ignored by ScriptManager.");
					}
				} else {
					//The object has an invalid script, exit.
					throw new ScriptException("ScriptManager::handleRunItem() given an invalid compiled script id (" + iCompiledScriptID + "), the Script is either being ignored (Failure to Compile) or something went wrong internally.");
				}
			} catch(NullPointerException e) {
				//The object has an invalid script, exit.
				throw new ScriptException("ScriptManager::handleRunItem() passed an objectID (" + parentObjectID + ") for a script/compiled script that doesn't exist, or is disabled/being ignored by ScriptManager.");
			}	
		} else {
			//Exit, we have nothing to do here.
			throw new ScriptException("ScriptManager::handleRunItem() passed an object ID out of bounds.");
		}
	}

	/**
	 * Handle running a system script.
	 * @param scriptID The ID of the system script to run. This is always relative to Constants.SYSTEM_SCRIPT.
	 * @param targetID The ID of the object or player requesting the script.
	 * @throws ScriptException If an error occurred while running the item script.
	 */
	private void handleRunSystemScript(int scriptID, long targetID) throws ScriptException {
		DataLog.logEntry("System script " + scriptID + " attempting to run.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
		
		//If the object ID is invalid.
		if(cSystemScripts.containsKey(scriptID)) {
			//Insert variables into the script engine.
			SimpleScriptContext settings = new SimpleScriptContext();
			settings.setAttribute("api", scriptAPI, ScriptContext.ENGINE_SCOPE);
			settings.setAttribute("targetPlayerID", targetID, ScriptContext.ENGINE_SCOPE);
			
			try {
				int iCompiledScriptID = cSystemScripts.get(scriptID);
				
				//If the object has a valid matching compiled script.
				if(cCompiledScripts.containsKey(iCompiledScriptID)) {
				
					//Get the compiled script.
					CompiledScript script = cCompiledScripts.get(iCompiledScriptID);
					
					//If the script exists.
					if(script != null) {
						//Run the script.
						script.eval(settings);
					} else {
						//The object has an invalid script, exit.
						throw new ScriptException("ScriptManager::handleRunSystem() passed a script ID (" + scriptID + ") for a script/compiled script that doesn't exist, or is disabled/being ignored by ScriptManager.");
					}
				} else {
					//The object has an invalid script, exit.
					throw new ScriptException("ScriptManager::handleRunSystem() given an invalid compiled script id (" + iCompiledScriptID + "), the Script is either being ignored (Failure to Compile) or something went wrong internally.");
				}
			} catch(NullPointerException e) {
				//The object has an invalid script, exit.
				throw new ScriptException("ScriptManager::handleRunSystem() passed a script ID (" + scriptID + ") for a script/compiled script that doesn't exist, or is disabled/being ignored by ScriptManager.");
			}	
		} else {
			//Exit, we have nothing to do here.
			throw new ScriptException("ScriptManager::handleRunSystem() passed a script ID out of bounds.");
		}
	}

	/**
	 * Run the script assigned to the specified object.
	 * @param scriptType -- The type of object that owns this script, from Constants:
	 * 		Undefined/Invalid = -1: This is usually used as a place holder in the database, using this will result in failure to run the script.
	 * 		Item = 1: In game items, found in a player's inventory/bank/house.
	 * 		System = 2: Internal scripts. These are not accessible to anything but the server.
	 * @param objectID -- The object ID or template ID of the object requesting this script.
	 * @param targetPlayerID -- The ID of this player, use Player.getID().
	 */
	protected void runScript(int scriptType, long objectID, long targetPlayerID) throws ScriptException {
		if(runScripts) {
			switch(scriptType) {
				case Constants.SCRIPT_TYPE_ITEM: {
					DataLog.logEntry("Item Template ID " + objectID + " requesting runScript(), attempting to run.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
					handleRunItem(objectID, targetPlayerID);
					break;
				}
				case Constants.SCRIPT_TYPE_SYSTEM: {
					DataLog.logEntry("System Script ID " + objectID + " requesting runScript() instead of runSystemScript(), ignoring request.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
					throw new ScriptException("System Script ID " + objectID + " requesting runScript() instead of runSystemScript(), ignoring request.");
				}
				case Constants.SCRIPT_TYPE_UNDEFINED: {
					DataLog.logEntry("Object ID " + objectID + " has an invalid script type. Ignoring request.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
					throw new ScriptException("Object ID " + objectID + " has an invalid script type. Ignoring request.");
				}
			}
		} else {
			DataLog.logEntry("ScriptManager::runScript() recieved request to run a script with type " + scriptType + " and an ID of " + objectID + ", but scripts are disabled. Ignoring request.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
			throw new ScriptException("ScriptManager::runScript() recieved request to run a script with type " + scriptType + " and an ID of " + objectID + ", but scripts are disabled. Ignoring request.");			
		}
	}
	
	/**
	 * Run a system script.
	 * @param scriptID The ID of the system script to run. This is always relative to Constants.SYSTEM_SCRIPT.
	 * @param targetPlayerID The ID of the object or player requesting the script.
	 * @throws ScriptException If an error occurred while running the item script.
	 */
	protected void runSystemScript(int scriptID, long targetPlayerID) throws ScriptException {
		if(runScripts) {
			DataLog.logEntry("System Script ID " + scriptID + " requesting runSystemScript(), attempting to run.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
			handleRunSystemScript(scriptID, targetPlayerID);
		} else {
			DataLog.logEntry("ScriptManager::runSystemScript() recieved request to run a system script with ID of " + scriptID + ", but scripts are disabled. Ignoring request.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
			throw new ScriptException("ScriptManager::runSystemScript() recieved request to run a system script with ID of " + scriptID + ", but scripts are disabled. Ignoring request.");
		}
	}
	
	/**
	 * This method is for developer only testing of scripts. It is designed to work with the developer command @runscript. It also allows for more convenient testing.
	 * <br><br>This method will be disabled in any public releases, as it allows one to run a script that was potentially developed for a specific object to be ran with any object.
	 * In other words, a script developed for item template ID #102 can be run for world object #203. Note that using a script for an object it was not designed for WILL cause problems (IE: This example).
	 * 
	 * @param scriptName
	 * @param runScriptType
	 * @param runTargetObjectID
	 * @param runTargetPlayerID
	 * @throws ScriptException
	 */
	protected void runScriptGenericMode(String scriptName, int runScriptType, long runTargetObjectID, long runTargetPlayerID) throws ScriptException {

		//Check if the script engine is enabled.
		if(runScripts) {

			//Local instance variables.
			SimpleScriptContext settings = new SimpleScriptContext();
			CompiledScript script = null;
			int compiledScriptID = -1;

			//Log the request.
			DataLog.logEntry(String.format("Requested recieved to run %s in \"generic\" mode, targeting object ID %s and player ID %s.", scriptName), "ScriptManager", Constants.LOG_SEVERITY_INFO, true, true);

			//Get compiled script with the specified name.
			compiledScriptID = getFileID(scriptName);

			if(cCompiledScripts.containsKey(compiledScriptID)) {
				
				//Get the compiled script.
				script = cCompiledScripts.get(compiledScriptID);

				//If the script exists.
				if(script != null) {

					//Check for script type.
					switch(runScriptType) {
	
						//Item.
						case Constants.SCRIPT_TYPE_ITEM: {
							settings.setAttribute("api", itemScriptAPI, ScriptContext.ENGINE_SCOPE);
							settings.setAttribute("itemTemplateID", runTargetObjectID, ScriptContext.ENGINE_SCOPE);
							break;
						}
	
						//System.
						case Constants.SCRIPT_TYPE_SYSTEM: {
							settings.setAttribute("api", scriptAPI, ScriptContext.ENGINE_SCOPE);
							break;
						}
	
						default: {
							DataLog.logEntry(String.format("ScriptManager::runScriptGenericMode() received request to run script file %s with an invalid type of %s.", scriptName, runScriptType), "ScriptManager", Constants.LOG_SEVERITY_INFO, true, true);
							throw new ScriptException(String.format("ScriptManager received request to run script file %s with an invalid type of %s.", scriptName, runScriptType));
						}
					}

					//Set default variables.
					settings.setAttribute("targetPlayerID", runTargetPlayerID, ScriptContext.ENGINE_SCOPE);					
					
					//Run the script.
					script.eval(settings);
				} else {
					
					//The object has an invalid script, exit.
					DataLog.logEntry(String.format("ScriptManager recieved request to run %s, but it is invalid or being ignored by ScriptManager.", scriptName), "ScriptManager", Constants.LOG_SEVERITY_INFO, true, true);
					throw new ScriptException(String.format("ScriptManager recieved request to run %s, but it is invalid or being ignored by ScriptManager.", scriptName));
				}					

			} else {
				
				//Invalid script file name.
				DataLog.logEntry(String.format("ScriptManager::runScriptGenericMode() received request to run a non-existant script file with the name of %s.", scriptName), "ScriptManager", Constants.LOG_SEVERITY_INFO, true, true);
				throw new ScriptException(String.format("ScriptManager received request to run a non-existant script file with the name of %s.", scriptName));
			}
		} else {

			DataLog.logEntry("ScriptManager::runSystemScript() recieved request to run " + scriptName + ", but scripts are disabled. Ignoring request.","ScriptManager",Constants.LOG_SEVERITY_INFO,true,true);
			throw new ScriptException("ScriptManager recieved request to run " + scriptName + ", but scripts are disabled. Ignoring request.");
		}
	}
}
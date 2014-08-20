

public class Utilities {

	/**
	 * Check the string, if it matches the name of a planet return the planet ID.
	 * @param planetName -- The string to check.
	 * @returns The planet ID, or -1 if the planet couldn't be found.
	 */
	public static int getPlanetIDFromString(String planetName) {
		//Store the planet ID.
		int planet = -1;
		
		//Convert to lower case.
		planetName = planetName.toLowerCase();
		
		//Check for a matching planet.
		if(planetName.equals(Constants.PlanetNames[0])) {
			
			//Set the planet.
			planet = Constants.CORELLIA;
		} else if(planetName.equals(Constants.PlanetNames[1])) {
			
			//Set the planet.
			planet = Constants.DANTOOINE;
		} else if(planetName.equals(Constants.PlanetNames[2])) {
			
			//Set the planet.
			planet = Constants.DATHOMIR;
		} else if(planetName.equals(Constants.PlanetNames[3])) {
			
			//Set the planet.
			planet = Constants.ENDOR;
		} else if(planetName.equals(Constants.PlanetNames[4])) {
			
			//Set the planet.
			planet = Constants.LOK;
		} else if(planetName.equals(Constants.PlanetNames[5])) {
			
			//Set the planet.
			planet = Constants.NABOO;
		} else if(planetName.equals(Constants.PlanetNames[6])) {
			
			//Set the planet.
			planet = Constants.RORI;
		} else if(planetName.equals(Constants.PlanetNames[7])) {
			
			//Set the planet.
			planet = Constants.TALUS;
		} else if(planetName.equals(Constants.PlanetNames[8])) {
			
			//Set the planet.
			planet = Constants.TATOOINE;
		} else if(planetName.startsWith(Constants.PlanetNames[9]) || planetName.equals(Constants.PlanetNames[9])) {
			
			//Set the planet.
			planet = Constants.YAVIN;
		} else if(planetName.equals(Constants.PlanetNames[10])) {
			
			//Set the planet.
			planet = Constants.TANAAB;
		} else if(planetName.equals(Constants.PlanetNames[11])) {
			
			//Set the planet.
			planet = Constants.UMBRA;
		} else if(planetName.equals(Constants.PlanetNames[12])) {
			
			//Set the planet.
			planet = Constants.TUTORIAL;
		}
		
		return planet;
	}
	
	public static int getScriptTypeFromString(String scriptType) {
		int scriptTypeID = Constants.SCRIPT_TYPE_UNDEFINED;
		
		if(scriptType.equalsIgnoreCase("item")) {
			
			scriptTypeID = Constants.SCRIPT_TYPE_ITEM;			
		} else if(scriptType.equalsIgnoreCase("system")) {
			
			scriptTypeID = Constants.SCRIPT_TYPE_SYSTEM;
		}
		
		return scriptTypeID;
	}
}

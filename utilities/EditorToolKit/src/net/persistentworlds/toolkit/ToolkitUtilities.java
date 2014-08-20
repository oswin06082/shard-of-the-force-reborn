package net.persistentworlds.toolkit;

/**
 * Class of Utility methods.
 * @author Interesting
 *
 */
public class ToolkitUtilities {

	/**
	 * Convenience method to print an exception and exit.
	 * @param e The exception to print.
	 */
	public static void handleFatalException(Exception e) {
		e.printStackTrace();
		System.exit(0);
	}
	
	/**
	 * Convenience method to get a String from a character array.
	 * @param chars Array of characters.
	 * @returns A string containing the characters.
	 */
	public static String getStringFromChars(char[] chars) {
		return new String(chars);
	}
	
	/**
	 * Checks if a database schema starts with a "/", if it doesn't, one is added at the beginning of the string.
	 * @param s -- The String to check.
	 * @returns -- The string with a "/" added at the beginning, or the string if one is already there.
	 */
	public static String getSchemaFromString(String s) {
		if(!s.startsWith("/")) {
			s = "/" + s;
		}
		return s;
	}
	
	public static boolean isStringEmpty(String string) {
		//Instance variables.
		boolean valid = false;

		//Check string.
		if(string == null || string.isEmpty() || string.contains(" ")) {
			valid = true;
		}

		//Return validity.
		return valid;
	}

	public static boolean isStringValidPort(String port) {
		//Instance variables.
		boolean valid = true;

		//Check
		if(!port.equals("") || port != null) {
			try {
				//Attempt to format the string into an integer.
				int iPort = Integer.parseInt(port);

				//Check if the port is within range.
				if(iPort < ToolkitConstants.CONFIGURATION_MINIMUM_PORT || iPort > ToolkitConstants.CONFIGURATION_MAXIMUM_PORT) {

					//Port is out of bounds.
					valid = false;
				}
			} catch(NumberFormatException e) {

				//Port isn't an int.
				valid = false;
			}
		} else {

			//Port isn't an int.
			valid = false;
		}

		//Return validity.
		return valid;
	}
}

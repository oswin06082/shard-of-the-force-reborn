/**
 * Container class for all of the available Dialog Options that an NPC may speak.
 * Will eventually contain info on what parent dialog triggers this dialog option to be displayed, 
 * whether choosing this dialog causes some other action to occur (skill gain, NPC AI option, etc.)
 * @author Darryl
 *
 */
public class DialogOption {
	private boolean bObscurelyNamedInCore1;
	private String sSTFFile;
	private String sStringInSTF;
	
	/**
	 * Constructs a dialog option
	 * @param bUseObscure -- Unknown boolean.
	 * @param stf -- The STF filename which this dialog option can be found in.
	 * @param stringInSTF -- The specific string of this dialog option in the given STF file.
	 */
	public DialogOption(boolean bUseObscure, String stf, String stringInSTF) {
		bObscurelyNamedInCore1 = bUseObscure;
		sSTFFile = stf;
		sStringInSTF = stringInSTF;
	}
	
	/**
	 * Get the unknown boolean value.
	 * @return The unknown boolean value.
	 */
	public boolean getShowDialog() {
		return bObscurelyNamedInCore1;
	}
	
	/**
	 * Get the STF filename which this dialog option is a member of.
	 * @return The STF file name.
	 */
	public String getSTFFile() {
		return sSTFFile;
	}
	/**
	 * Get the string name of this dialog option within it's STF file.
	 * @return The string paramater.
	 */
	public String getStringSTF() {
		return sStringInSTF;
	}
	
}

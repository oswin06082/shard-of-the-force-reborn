import java.io.Serializable;

/**
 * The AccountData class contains information on an Account.  Username, password, activity, etc.
 * @author Darryl
 *
 */
public class AccountData implements Serializable{
	public final static long serialVersionUID = 1234l;
	private long iAccountID;
	private String sUsername;
	private String sPassword;
	private int iStationID; // Might not even be needed?
	private boolean bIsGM;
	private boolean bIsBanned;
	private String sEmailAddress;
	private long lJoinTimestamp;
	private long lLastActiveTimestamp;
	private boolean bIsActiveAccount;
	private boolean bIsDeveloper;
        private long lJediBitMask;
        private long lAccountOpenDate;
	
	/**
	 * Construct an empty AccountData container for holding a clients Account Data.
	 */
	public AccountData() {
		
	}
	
	/**
	 * Sets this AccountData object's Account ID
	 * @param i -- The Account ID
	 */
	public void setAccountID(long i) {
		iAccountID = i;
	}
	/**
	 * Get the Account ID for this account
	 * @return -- The Account ID
	 */
	public long getAccountID() {
		return iAccountID;
	}
	
	/**
	 * Set the Username for this Account.
	 * @param s -- The Username
	 */
	public void setUsername(String s) {
		sUsername = s;
	}
	/**
	 * Get the Username for this Account.
	 * @return The Userame.
	 */
	public String getUsername() {
		return sUsername;
	}
	/**
	 * Sets the Password for this Account
	 * @param s -- The Password
	 */
	public void setPassword(String s) {
		sPassword = s;
	}
	
	/**
	 * Get the password for this Account
	 * @return -- The Password
	 */
	public String getPassword() {
		return sPassword;
	}
	/**
	 * Sets the Station ID for this Account
	 * @param i -- The Station ID
	 */
	public void setStationID(int i) {
		iStationID = i;
	}
	/**
	 * Gets the Station ID for this account.
	 * @return The Station ID
	 */
	public int getStationID() {
		return iStationID;
	}
	/**
	 * Sets if this account is a GameMaster / Administrator account.
	 * @param b -- the Administrator status of this account.
	 */
	public void setIsGM(boolean b) {
		bIsGM = b;
	}
	/**
	 * Gets if this account is a GameMaster / Administrator account.
	 * @return The Administrator status of this account.
	 */
	public boolean getIsGM() {
		return bIsGM;
	}
	/**
	 * Sets if this account is banned.
	 * @param b -- The banned status of this account.
	 */
	public void setIsBanned(boolean b) {
		bIsBanned = b;
	}
	/**
	 * Gets if this account is banned.
	 * @return The banned status of this account.
	 */
	public boolean getIsBanned() {
		return bIsBanned;
	}
	/**
	 * Sets the email address of this account.
	 * @param s -- The email address.
	 */
	public void setEmailAddress(String s) {
		sEmailAddress = s;
	}
	/**
	 * Gets the email address of this account
	 * @return The email address.
	 */
	public String getEmailAddress() {
		return sEmailAddress;
	}
	/**
	 * Sets the timestamp of when this account was first created.
	 * @param l -- The number of milliseconds between January 1, 1970, 00:00:00 GMT and when this account was created.
	 */
	public void setJoinTimestamp(long l) {
		lJoinTimestamp = l;
	}
	/**
	 * Returns the timestamp when this account was created.
	 * @return The number of milliseconds elapsed between January 1, 1970, 00:00:00 GMT and when this account was created.
	 */
	public long getJoinTimestamp() {
		return lJoinTimestamp;
	}
	/**
	 * Sets when this account was last accessed.
	 */
	public void setLastActive() {
		lLastActiveTimestamp = System.currentTimeMillis();
	}
	/**
	 * Sets when this account was last accessed.
	 * @param l -- The number of milliseconds elapsed between January 1, 1970, 00:00:00 GMT and when this account was last accessed.
	 */
	public void setLastActive(long l) {
		lLastActiveTimestamp = l;
	}
	/**
	 * Gets when this account was last accessed.
	 * @return The number of milliseconds elapsed between January 1, 1970, 00:00:00 GMT and when this account was last accessed.
	 */
	public long getLastActive() {
		return lLastActiveTimestamp;
	}
	/**
	 * Sets whether this is an "active" account.
	 * @param b -- The active status of this account.
	 */
	public void setActiveAccount(boolean b) {
		bIsActiveAccount = b;
	}
	/**
	 * Gets whether this account is active.
	 * @return The active status of this account.
	 */
	public boolean getIsActive() {
		return bIsActiveAccount;
	}
	
	public boolean getIsDeveloper() {
		return bIsDeveloper;
	}
	
	public void setIsDeveloper(boolean b) {
		bIsDeveloper = b;
	}
        
        public void setJediBitMask(long m){
            lJediBitMask = m;
        }
        
        public long getJediNitMask(){
            return lJediBitMask;
        }

    public long getLAccountOpenDate() {
        return lAccountOpenDate;
    }

    public void setLAccountOpenDate(long lAccountOpenDate) {
        this.lAccountOpenDate = lAccountOpenDate;
    }
        
        
}

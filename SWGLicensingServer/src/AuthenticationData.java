
public class AuthenticationData {
	private String sUsername;
	private String sPassword;
	private boolean bAllowedAuth;
	private boolean bActive;

	public AuthenticationData() {
		
	}
	
	public String getPassword() {
		return sPassword;
	}

	public void setPassword(String password) {
		sPassword = password;
	}

	public boolean isAllowedAuth() {
		return bAllowedAuth;
	}

	public void setAllowedAuth(boolean allowedAuth) {
		bAllowedAuth = allowedAuth;
	}

	public boolean isActive() {
		return bActive;
	}

	public void setActive(boolean active) {
		bActive = active;
	}

	public void setUsername(String sUsername) {
		this.sUsername = sUsername;
	}

	public String getUsername() {
		return sUsername;
	}
	
}



/**
 * This class will be used to create a container that will carry the login integration data for the user database
 * to various types of CMS Systems like vBulletin.
 * @author Tomas Cruz
 *
 */
public class LoginIntegration {

  private int logintype = 0;
  private String tableprefix = "";
  private String tablename = "";
  private String usernamefield = "";
  private String passwordfield = "";
  private String keyfield = "";
  private int encryptiontype = 1;
  private String hostname = "";
  private String schema = "";
  private String username = "";
  private String password = "";
  private int port = 3306;
  private int devusergroupid = 0;
  private int csrusergroupid = 0;
  private int devcsrgroupid = 0;
  private boolean bConnected = false;

  public LoginIntegration(){
      this.logintype = 1;
      bConnected = false;
  }
  public LoginIntegration(int logintype){
      this.logintype = logintype;
      bConnected = false;
  }

    public int getCsrusergroupid() {
        return csrusergroupid;
    }

    public void setCsrusergroupid(int csrusergroupid) {
        this.csrusergroupid = csrusergroupid;
    }

    public int getDevcsrgroupid() {
        return devcsrgroupid;
    }

    public void setDevcsrgroupid(int devcsrgroupid) {
        this.devcsrgroupid = devcsrgroupid;
    }

    public int getDevusergroupid() {
        return devusergroupid;
    }

    public void setDevusergroupid(int devusergroupid) {
        this.devusergroupid = devusergroupid;
    }

    public int getEncryptiontype() {
        return encryptiontype;
    }

    public void setEncryptiontype(int encryptiontype) {
        this.encryptiontype = encryptiontype;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getKeyfield() {
        return keyfield;
    }

    public void setKeyfield(String keyfield) {
        this.keyfield = keyfield;
    }

    public int getLogintype() {
        return logintype;
    }

    public void setLogintype(int logintype) {
        this.logintype = logintype;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordfield() {
        return passwordfield;
    }

    public void setPasswordfield(String passwordfield) {
        this.passwordfield = passwordfield;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTableprefix() {
        return tableprefix;
    }

    public void setTableprefix(String tableprefix) {
        this.tableprefix = tableprefix;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernamefield() {
        return usernamefield;
    }

    public void setUsernamefield(String usernamefield) {
        this.usernamefield = usernamefield;
    }

    public boolean isConnected() {
        return bConnected;
    }

    public void setConnected(boolean bConnected) {
        this.bConnected = bConnected;
    }



}

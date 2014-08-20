/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Container for Untime Optiond settable from the DB for the Zone Server
 * NOTE:  This does not have to be a class... these can be in the ZoneServer class itself.
 * @author Tomas Cruz
 */
public class ZoneServerRunOptions {

    int serverid;
    boolean bLogToConsole;

    public ZoneServerRunOptions(){
        
    }

    public boolean isBLogToConsole() {
        return bLogToConsole;
    }

    public void setBLogToConsole(boolean bLogToConsole) {
        this.bLogToConsole = bLogToConsole;
    }

    public int getServerid() {
        return serverid;
    }

    public void setServerid(int serverid) {
        this.serverid = serverid;
    }
    
}

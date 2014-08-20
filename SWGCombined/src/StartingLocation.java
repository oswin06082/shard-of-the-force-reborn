/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Tomas Cruz
 */
public class StartingLocation {

    private String sCityName;
    private int iPlanetID;
    private String sStyleSTF;
    private boolean bAvailable;
    float x,y,z;

    public StartingLocation(){
        
    }

    public StartingLocation(String sCityName,int iPlanetID,String sStyleSTF,boolean bAvailable,float x,float y, float z){
        this.sCityName = sCityName;
        this.iPlanetID = iPlanetID;
        this.sStyleSTF = sStyleSTF;
        this.bAvailable = bAvailable;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isAvailable() {
        return bAvailable;
    }

    public void setAvailable(boolean bAvailable) {
        this.bAvailable = bAvailable;
    }

    public int getPlanetID() {
        return iPlanetID;
    }

    public void setPlanetID(int iPlanetID) {
        this.iPlanetID = iPlanetID;
    }

    public String getCityName() {
        return sCityName;
    }

    public void setCityName(String sCityName) {
        this.sCityName = sCityName;
    }

    public String getStyleSTF() {
        return sStyleSTF;
    }

    public void setStyleSTF(String sStyleSTF) {
        this.sStyleSTF = sStyleSTF;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

}

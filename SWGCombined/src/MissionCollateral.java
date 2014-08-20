/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tomas Cruz
 */
public class MissionCollateral {
    
    private int id;
    private int missionid;
    private int collateralid;
    private int displaycrc;
    private String displaytext;
    private int schematicid;
    private float entertainerX,entertainerY,entertainerZ;
    private long entertainerCellID;
    private int entertainerPlanetID;

    public int getCollateralid() {
        return collateralid;
    }

    public void setCollateralid(int collateralid) {
        this.collateralid = collateralid;
    }

    public int getDisplaycrc() {
        return displaycrc;
    }

    public void setDisplaycrc(int displaycrc) {
        this.displaycrc = displaycrc;
    }

    public String getDisplaytext() {
        return displaytext;
    }

    public void setDisplaytext(String displaytext) {
        this.displaytext = displaytext;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMissionid() {
        return missionid;
    }

    public void setMissionid(int missionid) {
        this.missionid = missionid;
    }

    public int getSchematicid() {
        return schematicid;
    }

    public void setSchematicid(int schematicid) {
        this.schematicid = schematicid;
    }

    public long getEntertainerCellID() {
        return entertainerCellID;
    }

    public void setEntertainerCellID(long entertainerCellID) {
        this.entertainerCellID = entertainerCellID;
    }

    public int getEntertainerPlanetID() {
        return entertainerPlanetID;
    }

    public void setEntertainerPlanetID(int entertainerPlanetID) {
        this.entertainerPlanetID = entertainerPlanetID;
    }

    public float getEntertainerX() {
        return entertainerX;
    }

    public void setEntertainerX(float entertainerX) {
        this.entertainerX = entertainerX;
    }

    public float getEntertainerY() {
        return entertainerY;
    }

    public void setEntertainerY(float entertainerY) {
        this.entertainerY = entertainerY;
    }

    public float getEntertainerZ() {
        return entertainerZ;
    }

    public void setEntertainerZ(float entertainerZ) {
        this.entertainerZ = entertainerZ;
    }
    
    
}

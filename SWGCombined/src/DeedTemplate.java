/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Carries Data for a particular Deed Item.
 * @author Tomas Cruz
 */
public class DeedTemplate {
    
    private int templateid;
    private int object_template_id = 0;
    private int object_iff_template_id = 0;
    private int number_cells = 0;
    private int deed_type = 0;
    private int object_base_template_id = 0;    
    private boolean base_required = false;
    private int object_admin_terminal_template_id = 0;    
    private boolean terminal_required  = false;
    private int[] object_sign_template_id ;    
    private boolean sign_required  = false;
    private float terminalX = 0;
    private float terminalY = 0;
    private float terminalZ = 0;
    private float terminal_oI = 0;
    private float terminal_oJ = 0;
    private float terminal_oK = 0;
    private float terminal_oW = 0;
    private long terminal_cell_id = 0;
    private float [] signX;
    private float [] signY;
    private float [] signZ;
    private float [] sign_oI;
    private float [] sign_oJ;
    private float [] sign_oK;
    private float [] sign_oW;
    private int maint_per_hour = 0;
    private int required_skill = 0;
    private int [] allowedplanetslist;
    private int construction_marker_template_id;
    private int power_per_hour;
    private int structure_items_capacity;
    private boolean usespower;
    private int lotsused;
    private boolean isGuild;
    private int guild_terminal_template_id;
    private float guild_terminal_x, guild_terminal_y,guild_terminal_z,guild_terminal_oI,guild_terminal_oJ,guild_terminal_oK,guild_terminal_oW;
    
    private int iElevatorTerminalCount;
    private float[] fElevatorTerminalX,fElevatorTerminalY,fElevatorTerminalZ,fElevatorTerminaloI,fElevatorTerminaloJ,fElevatorTerminaloK,fElevatorTerminaloW;
    private int [] iElevatorTerminalCRC;
    private int [] iElevatorTerminalCellID;
    private int redeedfee;
    private int iBaseExtractionRate;
    private int iBaseHoppersize;

    private int iXPMultiplier;

    private int [] iCampPropTemplateID;
    private float [] campPropX,campPropY,campPropZ,campPropOrientationN,campPropOrientationS,campPropOrientationE,campPropOrientationW;
    private int [] pethams;
  
    public int getTemplateid() {
        return templateid;
    }

    public void setTemplateid(int templateid) {
        this.templateid = templateid;
    }    
    
    public boolean isBase_required() {
        return base_required;
    }

    public void setBase_required(boolean base_required) {
        this.base_required = base_required;
    }

    public int getDeed_type() {
        return deed_type;
    }

    public void setDeed_type(int deed_type) {
        this.deed_type = deed_type;
    }

    public int getMaint_per_hour() {
        return maint_per_hour;
    }

    public void setMaint_per_hour(int maint_per_hour) {
        this.maint_per_hour = maint_per_hour;
    }

    public int getNumber_cells() {
        return number_cells;
    }

    public void setNumber_cells(int number_cells) {
        this.number_cells = number_cells;
    }

    public int getObject_admin_terminal_template_id() {
        return object_admin_terminal_template_id;
    }

    public void setObject_admin_terminal_template_id(int object_admin_terminal_template_id) {
        this.object_admin_terminal_template_id = object_admin_terminal_template_id;
    }

    public int getObject_base_template_id() {
        return object_base_template_id;
    }

    public void setObject_base_template_id(int object_base_template_id) {
        this.object_base_template_id = object_base_template_id;
    }

    public int getObject_iff_template_id() {
        return object_iff_template_id;
    }

    public void setObject_iff_template_id(int object_iff_template_id) {
        this.object_iff_template_id = object_iff_template_id;
    }

    public int [] getObject_sign_template_id() {
        return object_sign_template_id;
    }

    public void setObject_sign_template_id(int[] object_sign_template_id) {
        this.object_sign_template_id = object_sign_template_id;
    }

    public int getObject_template_id() {
        return object_template_id;
    }

    public void setObject_template_id(int object_template_id) {
        this.object_template_id = object_template_id;
    }

    public int getRequired_skill() {
        return required_skill;
    }

    public void setRequired_skill(int required_skill) {
        this.required_skill = required_skill;
    }   

    public boolean isSign_required() {
        return sign_required;
    }

    public void setSign_required(boolean sign_required) {
        this.sign_required = sign_required;
    }

    public float getTerminalX() {
        return terminalX;
    }

    public void setTerminalX(float terminalX) {
        this.terminalX = terminalX;
    }

    public float getTerminalY() {
        return terminalY;
    }

    public void setTerminalY(float terminalY) {
        this.terminalY = terminalY;
    }

    public float getTerminalZ() {
        return terminalZ;
    }

    public void setTerminalZ(float terminalZ) {
        this.terminalZ = terminalZ;
    }

    public long getTerminal_cell_id() {
        return terminal_cell_id;
    }

    public void setTerminal_cell_id(long terminal_cell_id) {
        this.terminal_cell_id = terminal_cell_id;
    }

    public float getTerminal_oI() {
        return terminal_oI;
    }

    public void setTerminal_oI(float terminal_oI) {
        this.terminal_oI = terminal_oI;
    }

    public float getTerminal_oJ() {
        return terminal_oJ;
    }

    public void setTerminal_oJ(float terminal_oJ) {
        this.terminal_oJ = terminal_oJ;
    }

    public float getTerminal_oK() {
        return terminal_oK;
    }

    public void setTerminal_oK(float terminal_oK) {
        this.terminal_oK = terminal_oK;
    }

    public float getTerminal_oW() {
        return terminal_oW;
    }

    public void setTerminal_oW(float terminal_oW) {
        this.terminal_oW = terminal_oW;
    }

    public boolean isTerminal_required() {
        return terminal_required;
    }

    public void setTerminal_required(boolean terminal_required) {
        this.terminal_required = terminal_required;
    }

    public int[] getAllowedplanetslist() {
        return allowedplanetslist;
    }

    public void setAllowedplanetslist(int[] allowedplanetslist) {
        this.allowedplanetslist = allowedplanetslist;
    }

    public int getConstruction_marker_template_id() {
        return construction_marker_template_id;
    }

    public void setConstruction_marker_template_id(int construction_marker_template_id) {
        this.construction_marker_template_id = construction_marker_template_id;
    }

    public int getPower_per_hour() {
        return power_per_hour;
    }

    public void setPower_per_hour(int power_per_hour) {
        this.power_per_hour = power_per_hour;
    }

    public float[] getSignX() {
        return signX;
    }

    public void setSignX(float[] signX) {
        this.signX = signX;
    }

    public float[] getSignY() {
        return signY;
    }

    public void setSignY(float[] signY) {
        this.signY = signY;
    }

    public float[] getSignZ() {
        return signZ;
    }

    public void setSignZ(float[] signZ) {
        this.signZ = signZ;
    }

    public float[] getSign_oI() {
        return sign_oI;
    }

    public void setSign_oI(float[] sign_oI) {
        this.sign_oI = sign_oI;
    }

    public float[] getSign_oJ() {
        return sign_oJ;
    }

    public void setSign_oJ(float[] sign_oJ) {
        this.sign_oJ = sign_oJ;
    }

    public float[] getSign_oK() {
        return sign_oK;
    }

    public void setSign_oK(float[] sign_oK) {
        this.sign_oK = sign_oK;
    }

    public float[] getSign_oW() {
        return sign_oW;
    }

    public void setSign_oW(float[] sign_oW) {
        this.sign_oW = sign_oW;
    }

    public int getStructure_items_capacity() {
        return structure_items_capacity;
    }

    public void setStructure_items_capacity(int structure_items_capacity) {
        this.structure_items_capacity = structure_items_capacity;
    }

    public boolean usesPower() {
        return usespower;
    }

    public void setUsesPower(boolean usespower) {
        this.usespower = usespower;
    }

    public int getLotsused() {
        return lotsused;
    }

    public void setLotsused(int lotsused) {
        this.lotsused = lotsused;
    }

    public int getGuild_terminal_template_id() {
        return guild_terminal_template_id;
    }

    public void setGuild_terminal_template_id(int guild_terminal_template_id) {
        this.guild_terminal_template_id = guild_terminal_template_id;
    }

    public boolean isIsGuild() {
        return isGuild;
    }

    public void setIsGuild(boolean isGuild) {
        this.isGuild = isGuild;
    }

    public float getGuild_terminal_oI() {
        return guild_terminal_oI;
    }

    public void setGuild_terminal_oI(float guild_terminal_oI) {
        this.guild_terminal_oI = guild_terminal_oI;
    }

    public float getGuild_terminal_oJ() {
        return guild_terminal_oJ;
    }

    public void setGuild_terminal_oJ(float guild_terminal_oJ) {
        this.guild_terminal_oJ = guild_terminal_oJ;
    }

    public float getGuild_terminal_oK() {
        return guild_terminal_oK;
    }

    public void setGuild_terminal_oK(float guild_terminal_oK) {
        this.guild_terminal_oK = guild_terminal_oK;
    }

    public float getGuild_terminal_oW() {
        return guild_terminal_oW;
    }

    public void setGuild_terminal_oW(float guild_terminal_oW) {
        this.guild_terminal_oW = guild_terminal_oW;
    }

    public float getGuild_terminal_x() {
        return guild_terminal_x;
    }

    public void setGuild_terminal_x(float guild_terminal_x) {
        this.guild_terminal_x = guild_terminal_x;
    }

    public float getGuild_terminal_y() {
        return guild_terminal_y;
    }

    public void setGuild_terminal_y(float guild_terminal_y) {
        this.guild_terminal_y = guild_terminal_y;
    }

    public float getGuild_terminal_z() {
        return guild_terminal_z;
    }

    public void setGuild_terminal_z(float guild_terminal_z) {
        this.guild_terminal_z = guild_terminal_z;
    }

    public float[] getFElevatorTerminalX() {
        return fElevatorTerminalX;
    }

    public void setFElevatorTerminalX(float[] fElevatorTerminalX) {
        this.fElevatorTerminalX = fElevatorTerminalX;
    }

    public float[] getFElevatorTerminalY() {
        return fElevatorTerminalY;
    }

    public void setFElevatorTerminalY(float[] fElevatorTerminalY) {
        this.fElevatorTerminalY = fElevatorTerminalY;
    }

    public float[] getFElevatorTerminalZ() {
        return fElevatorTerminalZ;
    }

    public void setFElevatorTerminalZ(float[] fElevatorTerminalZ) {
        this.fElevatorTerminalZ = fElevatorTerminalZ;
    }

    public float[] getFElevatorTerminaloI() {
        return fElevatorTerminaloI;
    }

    public void setFElevatorTerminaloI(float[] fElevatorTerminaloI) {
        this.fElevatorTerminaloI = fElevatorTerminaloI;
    }

    public float[] getFElevatorTerminaloJ() {
        return fElevatorTerminaloJ;
    }

    public void setFElevatorTerminaloJ(float[] fElevatorTerminaloJ) {
        this.fElevatorTerminaloJ = fElevatorTerminaloJ;
    }

    public float[] getFElevatorTerminaloK() {
        return fElevatorTerminaloK;
    }

    public void setFElevatorTerminaloK(float[] fElevatorTerminaloK) {
        this.fElevatorTerminaloK = fElevatorTerminaloK;
    }

    public float[] getFElevatorTerminaloW() {
        return fElevatorTerminaloW;
    }

    public void setFElevatorTerminaloW(float[] fElevatorTerminaloW) {
        this.fElevatorTerminaloW = fElevatorTerminaloW;
    }

    public int[] getIElevatorTerminalCRC() {
        return iElevatorTerminalCRC;
    }

    public void setIElevatorTerminalCRC(int[] iElevatorTerminalCRC) {
        this.iElevatorTerminalCRC = iElevatorTerminalCRC;
    }

    public int[] getIElevatorTerminalCellID() {
        return iElevatorTerminalCellID;
    }

    public void setIElevatorTerminalCellID(int[] iElevatorTerminalCellID) {
        this.iElevatorTerminalCellID = iElevatorTerminalCellID;
    }

    public int getIElevatorTerminalCount() {
        return iElevatorTerminalCount;
    }

    public void setIElevatorTerminalCount(int iElevatorTerminalCount) {
        this.iElevatorTerminalCount = iElevatorTerminalCount;
    }

    public int getIBaseExtractionRate() {
        return iBaseExtractionRate;
    }

    public void setIBaseExtractionRate(int iBaseExtractionRate) {
        this.iBaseExtractionRate = iBaseExtractionRate;
    }

    public int getIBaseHoppersize() {
        return iBaseHoppersize;
    }

    public void setIBaseHoppersize(int iBaseHoppersize) {
        this.iBaseHoppersize = iBaseHoppersize;
    }

    public int getRedeedfee() {
        return redeedfee;
    }

    public void setRedeedfee(int redeedfee) {
        this.redeedfee = redeedfee;
    }

    public int getIXPMultiplier() {
        return iXPMultiplier;
    }

    public void setIXPMultiplier(int iXPMultiplier) {
        this.iXPMultiplier = iXPMultiplier;
    }

    public float[] getCampPropOrientationE() {
        return campPropOrientationE;
    }

    public void setCampPropOrientationE(float[] campPropOrientationE) {
        this.campPropOrientationE = campPropOrientationE;
    }

    public float[] getCampPropOrientationN() {
        return campPropOrientationN;
    }

    public void setCampPropOrientationN(float[] campPropOrientationN) {
        this.campPropOrientationN = campPropOrientationN;
    }

    public float[] getCampPropOrientationS() {
        return campPropOrientationS;
    }

    public void setCampPropOrientationS(float[] campPropOrientationS) {
        this.campPropOrientationS = campPropOrientationS;
    }

    public float[] getCampPropOrientationW() {
        return campPropOrientationW;
    }

    public void setCampPropOrientationW(float[] campPropOrientationW) {
        this.campPropOrientationW = campPropOrientationW;
    }

    public float[] getCampPropX() {
        return campPropX;
    }

    public void setCampPropX(float[] campPropX) {
        this.campPropX = campPropX;
    }

    public float[] getCampPropY() {
        return campPropY;
    }

    public void setCampPropY(float[] campPropY) {
        this.campPropY = campPropY;
    }

    public float[] getCampPropZ() {
        return campPropZ;
    }

    public void setCampPropZ(float[] campPropZ) {
        this.campPropZ = campPropZ;
    }

    public int[] getICampPropTemplateID() {
        return iCampPropTemplateID;
    }

    public void setICampPropTemplateID(int[] iCampPropTemplateID) {
        this.iCampPropTemplateID = iCampPropTemplateID;
    }

    public int[] getPethams() {
        return pethams;
    }

    public void setPethams(int[] pethams) {
        this.pethams = pethams;
    }

    
    
    
}

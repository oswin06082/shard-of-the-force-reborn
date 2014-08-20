/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This is a template object for storing the different signs a merchant can apply to a building.
 * @author Tomas Cruz
 */
public class SignIndexData {
    
    private int index;
    private int sign_template_id;
    private float [] signX;
    private float [] signY;
    private float [] signZ;
    private float [] signoI;
    private float [] signoJ;
    private float [] signoK;
    private float [] signoW;
    private int skill_required;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public int getSign_template_id() {
        return sign_template_id;
    }

    public void setSign_template_id(int sign_template_id) {
        this.sign_template_id = sign_template_id;
    }

    public float[] getSignoI() {
        return signoI;
    }

    public void setSignoI(float[] signoI) {
        this.signoI = signoI;
    }

    public float[] getSignoJ() {
        return signoJ;
    }

    public void setSignoJ(float[] signoJ) {
        this.signoJ = signoJ;
    }

    public float[] getSignoK() {
        return signoK;
    }

    public void setSignoK(float[] signoK) {
        this.signoK = signoK;
    }

    public float[] getSignoW() {
        return signoW;
    }

    public void setSignoW(float[] signoW) {
        this.signoW = signoW;
    }

    public int getSkill_required() {
        return skill_required;
    }

    public void setSkill_required(int skill_required) {
        this.skill_required = skill_required;
    }

    
    
    
    
}

import java.io.Serializable;

/**
 * SkillModifier is used to set items up with Skill Mods to be granted to players upon using or eating an item.
 * @author Tomas Cruz
 */
public class SkillModifier implements Serializable {
    public final static long serialVersionUID = 1l;

    private int iSkillModifierID;
    private int iSkillModifierValue;
    private int iCharges;
    private long lDuration;

    public SkillModifier(){

    }

    public int getSkillModifierID() {
        return iSkillModifierID;
    }

    public void setSkillModifierID(int iSkillModifierID) {
        this.iSkillModifierID = iSkillModifierID;
    }

    public int getSkillModifierValue() {
        return iSkillModifierValue;
    }

    public void setSkillModifierValue(int iSkillModifierValue) {
        this.iSkillModifierValue = iSkillModifierValue;
    }

    public int getCharges() {
        return iCharges;
    }

    public void setCharges(int iCharges) {
        this.iCharges = iCharges;
    }

    public void decrementCharges(int iCharges){
        this.iCharges -= iCharges;
        if(this.iCharges <= 0)
        {
            this.iCharges = 0;
        }
    }

    public long getDuration() {
        return lDuration;
    }

    public void setDuration(long lDuration) {
        this.lDuration = lDuration;
    }

    public void decrementDuration(long lDuration){
        this.lDuration -= lDuration;
        if(this.lDuration <= 0)
        {
            this.lDuration = 0;
        }
    }

    public void incrementDuration(long lDuration){
        this.lDuration += lDuration;
    }

    public String getModifierSTFString(){
        return Constants.SKILL_MODIFIER_NAMES[this.iSkillModifierID];
    }

}

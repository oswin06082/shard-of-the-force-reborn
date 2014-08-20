/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Hashtable;
import java.util.Vector;
/**
 * This is the Armor Class for Handling Armor Objects
 * @author Tomas Cruz
 */
public class Armor extends TangibleItem {
    public final static long serialVersionUID = 1l;
    
    
    private int [] iHealthEnc;
    private int [] iActionEnc;
    private int [] iMindEnc;
    
    private Vector<Integer> vEffectiveness;
    private Hashtable<Integer,Integer> vSpecialEffectiveness;
    
    public Armor(){
           iHealthEnc = new int [3];
           iActionEnc = new int [3];
           iMindEnc = new int [3];
           iHealthEnc[0] = 20;
           iHealthEnc[1] = 20;
           iHealthEnc[2] = 20;
           iActionEnc[0] = 20;
           iActionEnc[1] = 20;
           iActionEnc[2] = 20;
           iMindEnc[0] = 20;
           iMindEnc[1] = 20;
           iMindEnc[2] = 20;
           
           
           vEffectiveness = new Vector<Integer>();
           vSpecialEffectiveness = new Hashtable<Integer,Integer>();
           
           for(int i = 0; i < Constants.ARMOR_EFFECTIVENESS_STRINGS.length; i++)
           {
               vEffectiveness.add(i,20);
           }
    }
    
    public void update(long lElapsedDelta){
        
    }
    
    public int getRequiredSkill(){
        ItemTemplate t = DatabaseInterface.getTemplateDataByID(this.getTemplateID());
        if(t == null)
        {
            return -1;
        }
        return t.getRequiredSkillID();
    }
    
    public int[] getIActionEnc() {
        return iActionEnc;
    }

    public void setIActionEnc(int[] iActionEnc) {
        this.iActionEnc = iActionEnc;
    }

    public int[] getIHealthEnc() {
        return iHealthEnc;
    }

    public void setIHealthEnc(int[] iHealthEnc) {
        this.iHealthEnc = iHealthEnc;
    }

    public int[] getIMindEnc() {
        return iMindEnc;
    }

    public void setIMindEnc(int[] iMindEnc) {
        this.iMindEnc = iMindEnc;
    }

    public Vector<Integer> getVEffectiveness() {
        return vEffectiveness;
    }

    public void setVEffectiveness(Vector<Integer> vEffectiveness) {
        this.vEffectiveness = vEffectiveness;
    }
    
    public void addEffectivenessValue(int iEffectiveness, int iValue){
        this.vEffectiveness.add(iEffectiveness, iValue);
    }
    
    public int getEffectivenessValue(int iEffectiveness){
            int iCurCond = this.getMaxCondition() - this.getConditionDamage();
            //System.out.println("iCurCond: " + iCurCond);
            int iStoredEff = this.vEffectiveness.get(iEffectiveness);
            //System.out.println("iStoredEff: " + iStoredEff);
            float iEffValue = (float)iStoredEff / (float)this.getMaxCondition();   
            //System.out.println("iEffValue: " + iEffValue);
            float dRetEff = iEffValue * iCurCond ;
           //System.out.println("Ret Eff: " + dRetEff);
            return (int)dRetEff;      
    }
    
    public int getEffectivenessAttributeCount(){
        return vEffectiveness.size();
    }   
  

    public Hashtable<Integer, Integer> getVSpecialEffectiveness() {
        return vSpecialEffectiveness;
    }

    public void setVSpecialEffectiveness(Hashtable<Integer, Integer> vSpecialEffectiveness) {
        this.vSpecialEffectiveness = vSpecialEffectiveness;
    }
    
    public void addSpecialEffectivenessValue(Integer iSpecialEffectiveness, int iValue){
        this.vSpecialEffectiveness.put(iSpecialEffectiveness, iValue);
    }
    
    public int getSpecialEffectivenessValue(Integer iSpecialEffectiveness){
        if(this.vSpecialEffectiveness.containsKey(iSpecialEffectiveness))
        {
            int iCurCond = this.getMaxCondition() - this.getConditionDamage();
            int iStoredEff = this.vSpecialEffectiveness.get(iSpecialEffectiveness);
            double iEffValue = this.getMaxCondition()  /  iStoredEff;   
            double dRetEff = iCurCond * iEffValue;
            return (int)dRetEff;
        }
        return 0;
    }
    
    protected int [] getArmorEncumberances(){
        int [] iArmorEncumberances = new int [9];
        int ctr = 0;
        for(int i = 0; i < iArmorEncumberances.length; i++)
        {
            if(i >= 0 && i <= 2)
            {
                iArmorEncumberances[i] = this.iHealthEnc[ctr];
                ctr++;
            }
            else if(i >= 3 && i <= 5)
            {
                iArmorEncumberances[i] = this.iActionEnc[ctr];
                ctr++;
            }
            else if(i >= 6 && i <= 8)
            {
                iArmorEncumberances[i] = this.iMindEnc[ctr];
                ctr++;
            }
            if(ctr == 3)
            {
                ctr=0;
            }
        }
        return iArmorEncumberances;
    }
    
	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Depends on what it is.
		return 0;
	}

}

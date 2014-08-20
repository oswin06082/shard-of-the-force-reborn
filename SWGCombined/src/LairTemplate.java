/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tomas Cruz
 */
public class LairTemplate {

      int iLairDBID;
      int iLairTemplate;
      int iMob1Template;
      int iMob2Template;
      int iMob3Template;
      int iMob4Template;
      int iMob5Template;
      int iMob6Template;
      int iMob7Template;
      int iMob8Template;
      int iMob9Template;
      int iBabyTemplate;
      boolean bSpawnsBabies;
      int iMaxWaves;
      int iMaxPerWave;
      int iMaxBabies;
      boolean bSpawnsBoss;
      int iBossTemplate;
      boolean bSpawnsLoot;
      int iLootTableId;
      int iLairMaxCondition;
      int iLairHealIncrement;
      boolean bMobIsAgressive;
      boolean bMobIsSocial;
      int iLairAiId;
      int iMobAiId;
      String sLlairName;
      int[] iAllowedPlanetList;
      int iMaxMobHam;
      String sMobMaleName;
      String sMobFemaleName;
      String sMobBabyName;
      String sMobBossName;
      String sMobNameStfSile;
      private int iMeatType;
      private int iBoneType;
      private int iHideType;
      private int iMilkType;

    public int getILairDBID() {
        return iLairDBID;
    }

    public void setILairDBID(int iLairDBID) {
        this.iLairDBID = iLairDBID;
    }

    
    public boolean isBMobIsAgressive() {
        return bMobIsAgressive;
    }

    public void setBMobIsAgressive(boolean bMobIsAgressive) {
        this.bMobIsAgressive = bMobIsAgressive;
    }

    public boolean isBMobIsSocial() {
        return bMobIsSocial;
    }

    public void setBMobIsSocial(boolean bMobIsSocial) {
        this.bMobIsSocial = bMobIsSocial;
    }

    public boolean isBSpawnsBabies() {
        return bSpawnsBabies;
    }

    public void setBSpawnsBabies(boolean bSpawnsBabies) {
        this.bSpawnsBabies = bSpawnsBabies;
    }

    public boolean isBSpawnsBoss() {
        return bSpawnsBoss;
    }

    public void setBSpawnsBoss(boolean bSpawnsBoss) {
        this.bSpawnsBoss = bSpawnsBoss;
    }

    public boolean isBSpawnsLoot() {
        return bSpawnsLoot;
    }

    public void setBSpawnsLoot(boolean bSpawnsLoot) {
        this.bSpawnsLoot = bSpawnsLoot;
    }

    public int getIBabyTemplate() {
        return iBabyTemplate;
    }

    public void setIBabyTemplate(int iBabyTemplate) {
        this.iBabyTemplate = iBabyTemplate;
    }

    public int getIBossTemplate() {
        return iBossTemplate;
    }

    public void setIBossTemplate(int iBossTemplate) {
        this.iBossTemplate = iBossTemplate;
    }

    public int getILairAiId() {
        return iLairAiId;
    }

    public void setILairAiId(int iLairAiId) {
        this.iLairAiId = iLairAiId;
    }

    public int getILairHealIncrement() {
        return iLairHealIncrement;
    }

    public void setILairHealIncrement(int iLairHealIncrement) {
        this.iLairHealIncrement = iLairHealIncrement;
    }

    public int getILairMaxCondition() {
        return iLairMaxCondition;
    }

    public void setILairMaxCondition(int iLairMaxCondition) {
        this.iLairMaxCondition = iLairMaxCondition;
    }

    public int getILairTemplate() {
        return iLairTemplate;
    }

    public void setILairTemplate(int iLairTemplate) {
        this.iLairTemplate = iLairTemplate;
    }

    public int getILootTableId() {
        return iLootTableId;
    }

    public void setILootTableId(int iLootTableId) {
        this.iLootTableId = iLootTableId;
    }

    public int getIMaxBabies() {
        return iMaxBabies;
    }

    public void setIMaxBabies(int iMaxBabies) {
        this.iMaxBabies = iMaxBabies;
    }

    public int getIMaxPerWave() {
        return iMaxPerWave;
    }

    public void setIMaxPerWave(int iMaxPerWave) {
        this.iMaxPerWave = iMaxPerWave;
    }

    public int getIMaxWaves() {
        return iMaxWaves;
    }

    public void setIMaxWaves(int iMaxWaves) {
        this.iMaxWaves = iMaxWaves;
    }

    public int getIMob1Template() {
        return iMob1Template;
    }

    public void setIMob1Template(int iMob1Template) {
        this.iMob1Template = iMob1Template;
    }

    public int getIMob2Template() {
        return iMob2Template;
    }

    public void setIMob2Template(int iMob2Template) {
        this.iMob2Template = iMob2Template;
    }

    public int getIMob3Template() {
        return iMob3Template;
    }

    public void setIMob3Template(int iMob3Template) {
        this.iMob3Template = iMob3Template;
    }

    public int getIMob4Template() {
        return iMob4Template;
    }

    public void setIMob4Template(int iMob4Template) {
        this.iMob4Template = iMob4Template;
    }

    public int getIMob5Template() {
        return iMob5Template;
    }

    public void setIMob5Template(int iMob5Template) {
        this.iMob5Template = iMob5Template;
    }

    public int getIMob6Template() {
        return iMob6Template;
    }

    public void setIMob6Template(int iMob6Template) {
        this.iMob6Template = iMob6Template;
    }

    public int getIMob7Template() {
        return iMob7Template;
    }

    public void setIMob7Template(int iMob7Template) {
        this.iMob7Template = iMob7Template;
    }

    public int getIMob8Template() {
        return iMob8Template;
    }

    public void setIMob8Template(int iMob8Template) {
        this.iMob8Template = iMob8Template;
    }

    public int getIMob9Template() {
        return iMob9Template;
    }

    public void setIMob9Template(int iMob9Template) {
        this.iMob9Template = iMob9Template;
    }

    public int getIMobAiId() {
        return iMobAiId;
    }

    public void setIMobAiId(int iMobAiId) {
        this.iMobAiId = iMobAiId;
    }

    public int[] getSAllowedPlanetList() {
        return iAllowedPlanetList;
    }

    public void setSAllowedPlanetList(String sAllowedPlanetList) {
        String[] allowedPlanets = sAllowedPlanetList.split(",");
        iAllowedPlanetList = new int[allowedPlanets.length];
        for (int i = 0; i < allowedPlanets.length; i++) {
        	try {
        		iAllowedPlanetList[i] = Integer.parseInt(allowedPlanets[i]);
        	} catch (NumberFormatException e) {
        		System.out.println("SetAllowedPlanetList:  Number format exception for planet ID " + allowedPlanets[i]);
        	}
        }
    }

    public String getSLlairName() {
        return sLlairName;
    }

    public void setSLlairName(String sLlairName) {
        this.sLlairName = sLlairName;
    }

    public int getIMaxMobHam() {
        return iMaxMobHam;
    }

    public void setIMaxMobHam(int iMaxMobHam) {
        this.iMaxMobHam = iMaxMobHam;
    }

    public String getSMobBabyName() {
        return sMobBabyName;
    }

    public void setSMobBabyName(String sMobBabyName) {
        this.sMobBabyName = sMobBabyName;
    }

    public String getSMobBossName() {
        return sMobBossName;
    }

    public void setSMobBossName(String sMobBossName) {
        this.sMobBossName = sMobBossName;
    }

    public String [] getSMobFemaleName() {
        return sMobFemaleName.split(",");
    }

    public void setSMobFemaleName(String sMobFemaleName) {
        this.sMobFemaleName = sMobFemaleName;
    }

    public String [] getSMobMaleName() {
        return sMobMaleName.split(",");
    }

    public void setSMobMaleName(String sMobMaleName) {        
        this.sMobMaleName = sMobMaleName;
    }

    public String getSMobNameStfFile() {
        return sMobNameStfSile;
    }

    public void setSMobNameStfFile(String sMobNameStfSile) {
        this.sMobNameStfSile = sMobNameStfSile;
    }

	public void setMeatType(int iMeatType) {
		this.iMeatType = iMeatType;
	}

	public int getMeatType() {
		return iMeatType;
	}

	public void setBoneType(int iBoneType) {
		this.iBoneType = iBoneType;
	}

	public int getBoneType() {
		return iBoneType;
	}

	public void setHideType(int iHideType) {
		this.iHideType = iHideType;
	}

	public int getHideType() {
		return iHideType;
	}

	public void setMilkType(int iMilkType) {
		this.iMilkType = iMilkType;
	}

	public int getMilkType() {
		return iMilkType;
	}
  
    

}

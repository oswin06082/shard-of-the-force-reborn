import java.util.Vector;


public class NPCTemplate {
	private int iTemplateID = -1;
	private String sIFFFileName = "";
	private int iCRC = 0;
	private String sSTFFileName = "";
	private String sSTFFileIdentifier = "";
	private String sSTFDetailName = "";
	private String sSTFDetailIdentifier = "";
	private String sSTFLookAtName = "";
	private String sSTFLookAtIdentifier = "";
	private Vector<RadialMenuItem> vRadialMenuItems;
	private String sScriptName = "";
	private int iScriptType = -1;
	private Vector<Attribute> vAttributeList;
	private int[] iHAMs;
        private String appearance_cdf;
        private int crc_cdf;
        private String sdbName;
	private String examine_information;
        private int MaxDamage;
        private int MinDamage;
        private int Armor;
        private String Weaponiff;
        private int WeaponCRC;
        private int ConsiderRating;
        private String sDefaultScript;
        private int iFactionFlag;
        private int iNPCBitmask;
        
        
	public NPCTemplate() {
		vRadialMenuItems = new Vector<RadialMenuItem>();
		vAttributeList = new Vector<Attribute>();
		iHAMs = new int[9];
	}

	protected int getCRC() {
		return iCRC;
	}

	protected void setCRC(int icrc) {
		iCRC = icrc;
	}

	protected int getScriptType() {
		return iScriptType;
	}

	protected void setScriptType(int scriptType) {
		iScriptType = scriptType;
	}

	protected int getTemplateID() {
		return iTemplateID;
	}

	protected void setTemplateID(int templateID) {
		iTemplateID = templateID;
	}

	protected String getIFFFileName() {
		return sIFFFileName;
	}

	protected void setIFFFileName(String fileName) {
		sIFFFileName = fileName;
	}

	protected String getScriptName() {
		return sScriptName;
	}

	protected void setScriptName(String scriptName) {
		sScriptName = scriptName;
	}

	protected String getSTFDetailIdentifier() {
		return sSTFDetailIdentifier;
	}

	protected void setSTFDetailIdentifier(String detailIdentifier) {
		sSTFDetailIdentifier = detailIdentifier;
	}

	protected String getSTFDetailName() {
		return sSTFDetailName;
	}

	protected void setSTFDetailName(String detailName) {
		sSTFDetailName = detailName;
	}

	protected String getSTFFileIdentifier() {
		return sSTFFileIdentifier;
	}

	protected void setSTFFileIdentifier(String fileIdentifier) {
		sSTFFileIdentifier = fileIdentifier;
	}

	protected String getSTFFileName() {
		return sSTFFileName;
	}

	protected void setSTFFileName(String fileName) {
		sSTFFileName = fileName;
	}

	protected String getSTFLookAtIdentifier() {
		return sSTFLookAtIdentifier;
	}

	protected void setSTFLookAtIdentifier(String lookAtIdentifier) {
		sSTFLookAtIdentifier = lookAtIdentifier;
	}

	protected String getSTFLookAtName() {
		return sSTFLookAtName;
	}

	protected void setSTFLookAtName(String lookAtName) {
		sSTFLookAtName = lookAtName;
	}

	protected Vector<RadialMenuItem> getRadialMenuItems() {
		return vRadialMenuItems;
	}

	protected void addRadialMenuItem(Vector<RadialMenuItem> radialMenuItems) {
		vRadialMenuItems.addAll(radialMenuItems);
	}
	
	protected void addRadialMenuItem(RadialMenuItem item) {
		vRadialMenuItems.add(item);
	}
	
	protected Vector<Attribute> getAttributes() {
		return vAttributeList;
	}

	protected void addAttribute(Vector<Attribute> radialMenuItems) {
		vAttributeList.addAll(radialMenuItems);
	}
	
	protected void addAttribute(Attribute item) {
		vAttributeList.add(item);
	}
	
	protected void setHam(int index, int value) {
		if (index >= iHAMs.length || index < 0) {
			return;
		}
		iHAMs[index]=value;
	}
	
	protected int[] getHam() {
		return iHAMs;
	}
	protected int getHam(int index) {
		return iHAMs[index];
	}
	
        protected void setappearance_cdf(String s){
            appearance_cdf = s;
        }
        protected String getappearance_cdf(){
            return appearance_cdf;
        }
        
        protected void setcrc_cdf(int i){
            crc_cdf = i;
        }
        
        protected int getcrc_cdf(){
            return crc_cdf;
        }
        
        protected void setsdbName(String name){
            sdbName = name;
        }
        
        protected String getsdbName(){
            return sdbName;
        }
        
        protected void setexamine_information(String info){
            examine_information = info;
        }
        
        protected String getexamine_information(){
            return examine_information;
        }
        
        protected void setMaxDamage(int d){
            MaxDamage = d;
        }
        
        protected int getMaxDamage(){
            return MaxDamage;
        }
        
        protected void setMinDamage(int d){
            MinDamage = d;
        }
        
        protected int getMinDamage(){
            return MinDamage;
        }
        
        protected void setArmor(int a){
            Armor = a;
        }
        
        protected int getArmor(){
            return Armor;
        }
        
        protected void setWeaponiff(String iff){
            Weaponiff = iff;
        }
        
        protected String getWeaponiff(){
            return Weaponiff;
        }
        
        protected void setWeaponCRC(int crc){
            WeaponCRC = crc;
        }
        
        protected int getWeaponCRC(){
            return WeaponCRC;
        }
        
        protected void setConsiderRating(int r){
            ConsiderRating = r;
        }
        
        protected int getConsiderRating(){
            return ConsiderRating;
        }
        
        protected void setDefaultScript(String s){
            sDefaultScript = s;
        }
        
        protected String getDefaultScript(){
            return sDefaultScript;
        }
        
        protected void setFactionFlag(int f){
            iFactionFlag = f;
        }
        
        protected int getFactionFlag(){
            return iFactionFlag;
        }
        
        protected void setNPCBitmask(int m){
            iNPCBitmask = m;            
        }
        
        protected int getNPCBitmask(){
            return iNPCBitmask;
        }
}

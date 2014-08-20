
public class IFFData {
	private String sIFFFileName;
	private String sSTFFileName;
	private String sSTFFileIdentifier;
	private String sSTFDetailName;
	private String sSTFDetailIdentifier;
	private String sSTFLookAtName;
	private String sSTFLookAtIdentifier;
	private String sSTFCraftName;
	private String sSTFCraftIdentifier;
	private String sCraftedItemIFF;
	private String[] sCraftingIngredients;
	private int iCRC;
	private int iCraftedItemCRC;
	
	public IFFData() {
		
	}

	protected int getCraftedItemCRC() {
		return iCraftedItemCRC;
	}

	protected void setCraftedItemCRC(int craftedItemCRC) {
		iCraftedItemCRC = craftedItemCRC;
	}

	protected int getCRC() {
		return iCRC;
	}

	protected void setCRC(int icrc) {
		iCRC = icrc;
	}

	protected String getCraftedItemIFF() {
		return sCraftedItemIFF;
	}

	protected void setCraftedItemIFF(String craftedItemIFF) {
		sCraftedItemIFF = craftedItemIFF;
	}

	protected String[] getCraftingIngredients() {
		return sCraftingIngredients;
	}

	protected void setCraftingIngredients(String[] craftingIngredients) {
		sCraftingIngredients = craftingIngredients;
	}

	protected String getIFFFileName() {
		return sIFFFileName;
	}

	protected void setIFFFileName(String fileName) {
		sIFFFileName = fileName;
	}

	protected String getSTFCraftIdentifier() {
		return sSTFCraftIdentifier;
	}

	protected void setSTFCraftIdentifier(String craftIdentifier) {
		sSTFCraftIdentifier = craftIdentifier;
	}

	protected String getSTFCraftName() {
		return sSTFCraftName;
	}

	protected void setSTFCraftName(String craftName) {
		sSTFCraftName = craftName;
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
	
	
}

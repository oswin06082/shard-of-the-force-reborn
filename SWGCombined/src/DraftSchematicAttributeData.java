import java.io.Serializable;


public class DraftSchematicAttributeData implements Serializable{
	public final static long serialVersionUID = 1l;
	private int numSlots;
	private String[] slotSTFFileName;
	private String[] slotSTFFileIdentifier;
	
	private int numExperimentalAttributes;
	private String[] attributeSTFFileName;
	private String[] attributeSTFFileIdentifier;
	
	private String sFilename;
	private String sCraftedItemIFFFilename;
	public DraftSchematicAttributeData(String sFilename) {
		this.sFilename = sFilename;
	}
	
	public void setNumSlots(int numSlots) {
		this.numSlots = numSlots;
		slotSTFFileIdentifier = new String[numSlots];
		slotSTFFileName = new String[numSlots];
	}
	
	public void setSlotSTFFilename(String s, int index) {
		slotSTFFileName[index] = s;
	}
	
	public void setSlotSTFFileIdentifier(String s, int index) {
		slotSTFFileIdentifier[index] = s;
	}
	
	public void setNumAttributes(int numAttributes) {
		this.numExperimentalAttributes = numAttributes;
		attributeSTFFileName = new String[numExperimentalAttributes];
		attributeSTFFileIdentifier = new String[numExperimentalAttributes];
	}
	
	public void setAttributeSTFFileName(String s, int index) {
		attributeSTFFileName[index] = s;
		
	}
	
	public void setAttributeSTFFileIdentifier(String s, int index) {
		attributeSTFFileIdentifier[index] = s;
	}
	
	public void setCraftedItemName(String sFilename) {
		sCraftedItemIFFFilename = sFilename;
	}
	
	public String toString() {
		StringBuffer toReturn = new StringBuffer();
			
		toReturn.append("Filename: " + sFilename);
		toReturn.append("\nNumber of slots: " + numSlots);
		for (int i = 0; i < numSlots; i++) {
			toReturn.append("\nSlot["+i+"] STF Filename: " + slotSTFFileName[i] + ", Identifier: " + slotSTFFileIdentifier[i]);
		}
		toReturn.append("\nNumber of experimental attributes: " + numExperimentalAttributes);
		for (int i=0; i < numExperimentalAttributes; i++) {
			if (attributeSTFFileName[i] == null) {
				toReturn.append("\nNo experimental attribute at index " + i);
			} else {
				toReturn.append("\nAttribute["+i+"] STF Filename: " + attributeSTFFileName[i] + ", Identifier: " + attributeSTFFileIdentifier[i]);
			}
			
		}
		toReturn.append("\nCrafted item IFF: " + sCraftedItemIFFFilename);
		return toReturn.toString();
	}

	protected int getNumSlots() {
		return numSlots;
	}

	protected String[] getSlotSTFFileName() {
		return slotSTFFileName;
	}

	protected String[] getSlotSTFFileIdentifier() {
		return slotSTFFileIdentifier;
	}

	protected int getNumExperimentalAttributes() {
		return numExperimentalAttributes;
	}

	protected String[] getAttributeSTFFileName() {
		return attributeSTFFileName;
	}

	protected String[] getAttributeSTFFileIdentifier() {
		return attributeSTFFileIdentifier;
	}

	protected String getFilename() {
		return sFilename;
	}

	protected String getSCraftedItemIFFFilename() {
		return sCraftedItemIFFFilename;
	}
	
}

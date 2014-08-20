import java.io.Serializable;


public class CraftingSchematicComponent implements Serializable {
	public final static long serialVersionUID = 1l;
	private boolean bOptionalComponent;
	private String sSTFFileName;
	private String sSTFFileIdentifier;
	private String sComponentName;
	private int iComponentQuantity;
	private byte componentRequirementType;
	private boolean bComponentsIdentical;
	private boolean bIsResource = false;
	
	public CraftingSchematicComponent() {
		super();
	}
	
	protected boolean isOptionalComponent() {
		return bOptionalComponent;
	}
	protected void setOptionalComponent(boolean optionalComponent) {
		bOptionalComponent = optionalComponent;
	}
	protected byte getComponentRequirementType() {
		return componentRequirementType;
	}
	protected void setComponentRequirementType(byte componentRequirementType) {
		this.componentRequirementType = componentRequirementType;
	}
	protected int getComponentQuantity() {
		return iComponentQuantity;
	}
	protected void setComponentQuantity(int componentQuantity) {
		iComponentQuantity = componentQuantity;
	}
	protected String getSTFFileName() {
		return sSTFFileName;
	}
	protected void setSTFFileName(String componentTitle) {
		sSTFFileName = componentTitle;
	}
	protected String getSTFFileIdentifier() {
		return sSTFFileIdentifier;
	}
	protected void setSTFFileIdentifier(String componentType) {
		sSTFFileIdentifier = componentType;
	}
	protected void setComponentIdentical(boolean b) {
		bComponentsIdentical = b;
	}
	protected boolean getComponentIdentical() {
		return bComponentsIdentical;
	}
	
	protected String getName() {
		return sComponentName;
	}
	protected void setName(String sName) {
		sComponentName = sName;
	}
		
	public String toString() {
		return "Schematic component data: STFFilename[" + sSTFFileName + "], STFFileIdentifier["+sSTFFileIdentifier + "], component[" + sComponentName + "], "
		+ "componentQuantity[" + iComponentQuantity + "], identical[" + bComponentsIdentical + "], optional["+bOptionalComponent+"]";
	}

	public void setIsResource(boolean bIsResource) {
		this.bIsResource = bIsResource;
	}

	public boolean getIsResource() {
		return bIsResource;
	}
}

import java.io.Serializable;


public class ManufacturingSchematicAttribute implements Serializable {
	public final static long serialVersionUID = 1l;
	private String sAttributeType;
	private String sAttributeName;
	private float fAttributeValue;
	
	public ManufacturingSchematicAttribute() {
		sAttributeName = null;
		sAttributeType = null;
		fAttributeValue = 0;
	}
	
	public void setAttributeType(String sType) {
		sAttributeType = sType;
	}
	
	public String getAttributeType() {
		return sAttributeType;
	}
	
	public void setAttributeName(String sName) {
		sAttributeName = sName;
	}
	
	public String getAttributeName() {
		return sAttributeName;
	}
	
	public void setAttributeValue(float fValue) {
		fAttributeValue = fValue;
	}
	
	public float getAttributeValue() {
		return fAttributeValue;
	}
}

import java.io.Serializable;
/**
 * The Attribute Class contains attribute data for an SOEObject.  (IE the kinetic resistance stat on a piece of Armor.)
 * @author Darryl
 *
 */
public class Attribute implements Serializable {
	public final static long serialVersionUID = 1;
	private String attributeName;
	
	private String attributeValue;
	private final int iAttributeIndex;
	
	public Attribute(String sName, int sValue) {
		//attributeName = sName;
		attributeValue = String.valueOf(sValue);
		iAttributeIndex = -1;
	}
	
	public Attribute(String sName, String sValue) {
		//attributeName = sName;
		attributeValue = sValue;
		iAttributeIndex = -1;
	}
	
	/**
	 * Constructs a new Attribute with the specified Attribute Name and Attribute String Value.
	 * @param sName -- The name of the attribute.
	 * @param sValue -- The value of the attribute.
	 */
	public Attribute(int sName, String sValue) {
		//attributeName = Constants.OBJECT_ATTRIBUTES[sName];
		attributeValue = sValue;
		iAttributeIndex = sName;
	}
	
	/**
	 * Constructs a new Attribute with the specified Attribute Name and Attribute Numerical Value.
	 * @param sName -- The name of the attribute
	 * @param sValue -- The value of the attribute
	 */
	public Attribute(int sName, long sValue) {
		//attributeName = Constants.OBJECT_ATTRIBUTES[sName];
		attributeValue = String.valueOf(sValue);
		iAttributeIndex = sName;
	}
	
	/**
	 * Constructs a new Attribute with the specified Attribute Name and Attribute Numerical Value.
	 * @param sName -- The name of the attribute.
	 * @param sValue -- The value of the attriubte.
	 */
	public Attribute(int sName, short sValue) {
		attributeName = Constants.OBJECT_ATTRIBUTES[sName];
		attributeValue = String.valueOf(sValue);
		iAttributeIndex = sName;
	}
	
	/**
	 * Constructs a new Attribute with the specified Attribute Name and Attribute Numerical Value.
	 * @param sName -- The name of the attribute.
	 * @param sValue -- The value of the attribute.
	 */
	
	public Attribute(int sName, int sValue) {
		attributeName = Constants.OBJECT_ATTRIBUTES[sName];
		attributeValue = String.valueOf(sValue);
		iAttributeIndex = sName;
	}
	/**
	 * Constructs a new Attribute with the specified Attribute Name and Attribute Numerical Value.
	 * @param sName -- The name of the attribute.
	 * @param sValue -- The value of the attribute.
	 */
	
	public Attribute(int sName, double sValue) {
		attributeName = Constants.OBJECT_ATTRIBUTES[sName];
		attributeValue = String.valueOf(sValue);
		iAttributeIndex = sName;
	}
	/**
	 * Constructs a new Attribute with the specified Attribute Name and Attribute Numerical Value.
	 * @param sName -- The name of the attribute.
	 * @param sValue -- The value of the attribute.
	 */
	
	public Attribute(int sName, float sValue) {
		attributeName = Constants.OBJECT_ATTRIBUTES[sName];
		attributeValue = String.valueOf(sValue);
		iAttributeIndex = sName;
	}
	/**
	 * Constructs a new Attribute with the specified Attribute Name and a blank Attribute Value.
	 * @param sName -- The name of the attribute.
	 */
	public Attribute(int sName) {
		attributeName = Constants.OBJECT_ATTRIBUTES[sName];
		attributeValue = "";
		iAttributeIndex = sName;
	}
	
	
	/**
	 * Gets the name of this Attribute
	 * @return The attribute name.
	 */
	public String getAttributeName() {
		if (attributeName != null) {
			return attributeName;
		}
		if (iAttributeIndex == -1) {
			return "Internal Error";
		}
		return Constants.OBJECT_ATTRIBUTES[iAttributeIndex];
	}
	
	/**
	 * Gets the value of this Attribute.
	 * @return The attribute value.
	 */
	public String getAttributeValue() {
		return attributeValue;
	}
	
	public void setAttributeValue(String s) {
		attributeValue = s;
	}

	public int getAttributeIndex() {
		return iAttributeIndex;
	}
	
	
}

import java.io.Serializable;


public class CraftingExperimentationAttribute implements Serializable{
	public final static long serialVersionUID = 1l;
	private int index;
	private String stfFileName;
	private String stfFileIdentifier;
	private byte[] weightAndTypeBitmask = null;
	
	public CraftingExperimentationAttribute() {
		
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setStfFileName(String stfFileName) {
		this.stfFileName = stfFileName;
	}

	public String getStfFileName() {
		return stfFileName;
	}

	public void setStfFileIdentifier(String stfFileIdentifier) {
		this.stfFileIdentifier = stfFileIdentifier;
	}

	public String getStfFileIdentifier() {
		return stfFileIdentifier;
	}
	
	public void setBit(int weightIndex, int byteIndex) throws IndexOutOfBoundsException{
		//if (true) return;
		if (index > 7 || index < 0) {
			throw new IndexOutOfBoundsException("Error setting bit " + index + " on an 8-bit value.");
		}
		weightAndTypeBitmask[weightIndex] = (byte) (weightAndTypeBitmask[weightIndex] | (1 << byteIndex));
	}
	
	public void clearBit(int weightIndex, int byteIndex) {
		if (index > 7 || index < 0) {
			throw new IndexOutOfBoundsException("Error setting bit " + index + " on an 8-bit value.");
		}
		weightAndTypeBitmask[weightIndex] = (byte)(weightAndTypeBitmask[weightIndex] & ~(1 << index));
	}
	
	public void setWeight(int weightIndex, byte weightValue) {
		weightAndTypeBitmask[weightIndex] = weightValue;
	}
	
	public byte[] getWeightAndTypeBitmask() {
		return weightAndTypeBitmask;
	}
	
	public void setNumWeights(int numWeights) {
		weightAndTypeBitmask = new byte[numWeights];
	}
	
	public void setWeight(byte[] weights) {
		weightAndTypeBitmask = weights;
	}
}

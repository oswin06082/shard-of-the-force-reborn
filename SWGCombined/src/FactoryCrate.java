import java.util.TreeSet;


public class FactoryCrate extends TangibleItem{
	public final static long serialVersionUID = 1l;
	private TreeSet<SOEObject> vObjectsInCrate;
	private byte iCrateCapacity = 25; // Variable, depending on the type of object being manufactured.  TODO:  Find info on.
	private byte iNumObjectsInCrate = 0;
	public FactoryCrate() { 
		super();
		vObjectsInCrate = new TreeSet<SOEObject>();
	}
	
	/**
	 * Returns and removes the first (least) object in the crate, or null if the crate is empty.
	 * @return
	 */
	public SOEObject getObjectFromCrate() {
		return vObjectsInCrate.pollFirst();
	}
	
	/**
	 * Adds the given SOEObject to the factory crate.  Sets the serial number to be the same as any objects already in the crate (game prerequisite)
	 * @param o -- The object to be added to the crate
	 * @return True, if the object was in fact added.  Otherwise returns false.
	 */
	public boolean addToCrate(TangibleItem o) {
		if (iNumObjectsInCrate < iCrateCapacity) {
			if (!vObjectsInCrate.isEmpty()) {
				SOEObject baseObjectInCrate = vObjectsInCrate.first();
				o.setSerialNumber(baseObjectInCrate.getSerialNumber(), true);
			}
			vObjectsInCrate.add(o);
			iNumObjectsInCrate ++; 
			
			return true;
		}
		return false;
	}
	
	/**
	 * Factory crates are not, in fact, experimentable.
	 */
	public void experiment() {
		return;
	}
	
	public byte getQuantity() {
		return iNumObjectsInCrate;
	}
	
	public long getSerialNumber() {
		if (!vObjectsInCrate.isEmpty()) {
			return vObjectsInCrate.first().getSerialNumber();
		}
		return 0;
	}
	
	public String getContainedItemSTFFilename() {
		if (!vObjectsInCrate.isEmpty()) {
			return vObjectsInCrate.first().getSTFFileName();
		}
		return null;
	}
	public boolean isEmpty() {
		return vObjectsInCrate.isEmpty();
	}
	
}

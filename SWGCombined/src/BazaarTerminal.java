import java.util.Hashtable;

public class BazaarTerminal extends Terminal {
	public final static long serialVersionUID = 1l;
	private Hashtable<Long, TangibleItem> vItemsForSaleThisTerminal;
	private String sPacketName;
	
	public BazaarTerminal() {
		super();
	}
	
	protected TangibleItem getItemForSale(long objectID) {
		return vItemsForSaleThisTerminal.get(objectID);
	}
	
	protected TangibleItem removeItemForSale(long objectID) {
		return vItemsForSaleThisTerminal.remove(objectID);
	}
	
	protected void setItemForSale(TangibleItem t) {
		vItemsForSaleThisTerminal.put(t.getID(), t);
	}
	
	protected Hashtable<Long, TangibleItem> getAllSaleItems() {
		return vItemsForSaleThisTerminal;
	}
	
}

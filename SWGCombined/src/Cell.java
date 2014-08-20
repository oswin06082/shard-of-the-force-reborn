import java.util.Hashtable;
import java.util.Enumeration;

/**
 * A Cell object is an interior subdivision of a Structure object, which itself
 * may contain Players, NPCs, or Tangible Items.
 * 
 * @author Darryl
 * 
 */
public class Cell extends SOEObject {
	public final static long serialVersionUID = 1;
	private Structure parent;
	private boolean bIsSpawned;
	private Hashtable<Long, SOEObject> vObjectsInCell;
	private int iCellNum = 0;
	private boolean bCanEnter = true;
	private final int cellObjectCRC = 0x0C5401EE;
	private transient ZoneServer server;

	/**
	 * Construct a new Cell with the given offset, contained by the Structure
	 * with the given building ID.
	 * 
	 * @param buildingID
	 *            -- The ID of the parent Structure.
	 * @param cellOffset
	 *            -- The "offset" of this cell in the Structure.
	 */
	public Cell(Structure building, long cellOffset, boolean debug) {
		super();
		parent = building;
		setParentID(building.getID());
		//setID(parentBuildingID + cellOffset);
		iCellNum = (int) cellOffset;
		vObjectsInCell = new Hashtable<Long, SOEObject>();

		this.setCRC(cellObjectCRC);
	}

	/**
	 * Get this cell's parent Structure's Object ID.
	 * 
	 * @return The ObjectID of the parent Structure.
	 */
	public Structure getBuilding() {
		return parent;
	}

	/**
	 * Sets whether this Cell is actually spawned in the world.
	 * 
	 * @param b
	 *            -- If this Cell is spawned.
	 */
	public void setIsSpawned(boolean b) {
		bIsSpawned = b;
	}

	/**
	 * Gets if this Cell is actually spawned in the world.
	 * 
	 * @return If this cell is spawned.
	 */
	public boolean isSpawned() {
		return bIsSpawned;
	}

	/**
	 * Add an object to this Cell's "inventory".
	 * 
	 * @param o
	 *            -- The object to be contained by the Cell.
	 */
	public void addCellObject(SOEObject o) {
		try {

			if (!vObjectsInCell.contains(o)) {
				vObjectsInCell.put(o.getID(), o);
			}

			if (o instanceof Terminal) {
				// do nothing
			} else if (o instanceof Player) {
				// System.out.println("Class of item in cell: " + o.getClass());
				Player p = (Player) o;
				if (!p.getFullName().isEmpty() && p.getClient() != null
						&& p.getClient().getClientReadyStatus()) {
					// System.out.println("Player Inserted to Cell: " +
					// this.getID() + " Index: " + this.getCellNum() + " Name: "
					// + p.getFullName());
					if (parent instanceof TutorialObject) {
						Enumeration<SOEObject> cObjects = this.getCellObjects()
								.elements();
						while (cObjects.hasMoreElements()) {
							SOEObject co = cObjects.nextElement();
							// p.removeSpawnedObject(co);
							p.spawnItem(co);
						}
					} else if (parent instanceof Structure) {
						// Taken care of in Player.updateInteriorPosition
						//parent.spawnObjectsInBuildingCells(p.getClient());
					}

				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void removeCellObject(SOEObject o) {
		vObjectsInCell.remove(o.getID());
	}

	/**
	 * Get a Hashtable of all the SOEObjects contained in this Cell, sorted by
	 * the SOEObject's Object ID.
	 * 
	 * @return The list of objects in this Cell.
	 */
	public Hashtable<Long, SOEObject> getCellObjects() {
		return vObjectsInCell;
	}

	/**
	 * Get a specific object contained in this Cell.
	 * 
	 * @param objectID
	 *            -- The ObjectID of the SOEObject being searched for.
	 * @return The SOEObject with the ObjectID being searched for, or null if
	 *         the SOEObject is not contained in this cell.
	 */
	public SOEObject getObjectInCell(long objectID) {
		return vObjectsInCell.get(objectID);
	}

	protected void setCellNum(int i) {
		iCellNum = i;
	}

	protected int getCellNum() {
		return iCellNum;
	}

	protected void setCanEnter(boolean b) {
		bCanEnter = b;
	}

	protected boolean getCanEnter() {
		return bCanEnter;
	}

	public ZoneServer getServer() {
		return server;
	}

	public void setServer(ZoneServer server) {
		this.server = server;
	}

	public void setBuilding(Structure s) {
		parent = s;
	}
	
	public boolean contains(SOEObject o) {
		return vObjectsInCell.containsKey(o.getID());
	}
}

import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


public class Grid {
	private final static int MAP_DIMENSION = 16384;
	//private final static int HIGHEST_POINT_IN_GAME = 512;
	private int gridCount;
	private int iElementDimension;
	private GridElement[][] theGrid;
	private int iPlanetID;
	public Grid(ZoneServer server, int sizeOfElements, int iPlanetID){
		this.iPlanetID = iPlanetID;
		iElementDimension = sizeOfElements;
		gridCount = MAP_DIMENSION / iElementDimension;
		theGrid = new GridElement[gridCount][gridCount];
		int x = 0;
		int y = 0;
		float gridX = -8192;
		float gridY = -8192;
		try {
			for (x = 0; x < gridCount; x++) {
				gridX = -8192 + (sizeOfElements * x);
				for (y = 0; y < gridCount; y++) {
					gridY = (-8192 + (sizeOfElements * y));
					theGrid[x][y] = new GridElement(gridX, gridY, this.iPlanetID );
					theGrid[x][y].setDrawColor(SWGGui.getRandomLong(0xFFFFFF));
					theGrid[x][y].setArrayIndices(x, y);
				}
			}
			for (x = 1; x < gridCount - 1; x++) {
				for (y = 1; y < gridCount - 1; y++) {
					theGrid[x][y].setNearbyGrid(theGrid[x-1][y-1], GridElement.GRID_ELEMENT_TOP_LEFT);
					theGrid[x][y].setNearbyGrid(theGrid[x][y-1], GridElement.GRID_ELEMENT_TOP_CENTER);
					theGrid[x][y].setNearbyGrid(theGrid[x+1][y-1], GridElement.GRID_ELEMENT_TOP_RIGHT);
					theGrid[x][y].setNearbyGrid(theGrid[x-1][y], GridElement.GRID_ELEMENT_MIDDLE_LEFT);
					theGrid[x][y].setNearbyGrid(theGrid[x+1][y], GridElement.GRID_ELEMENT_MIDDLE_RIGHT);
					theGrid[x][y].setNearbyGrid(theGrid[x-1][y+1], GridElement.GRID_ELEMENT_BOTTOM_LEFT);
					theGrid[x][y].setNearbyGrid(theGrid[x][y+1], GridElement.GRID_ELEMENT_BOTTOM_CENTER);
					theGrid[x][y].setNearbyGrid(theGrid[x+1][y+1], GridElement.GRID_ELEMENT_BOTTOM_RIGHT);
				}
			}
		} catch (OutOfMemoryError e) {
			theGrid = null;
			Runtime.getRuntime().gc();
			
		
		}
	}
	
	public GridElement getNearestElement(float x, float y) {
		x = (x + 8192) / iElementDimension;
		y = (y + 8192) / iElementDimension;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (x > theGrid.length) {
			return null;
		}
		if (y > theGrid[(int)x].length) {
			return null;
		}
		return theGrid[(int)x][(int)y];
	}

	public void addToGrid(SOEObject o) {
		GridElement element = getNearestElement(o.getX(), o.getY());
		element.addObjectToGridElement(o);
	}
	
	public void removeFromGrid(SOEObject o) {
		GridElement element = getNearestElement(o.getX(), o.getY());
		element.removeObjectFromGridElement(o, false);
	}

	public ConcurrentHashMap<Long, SOEObject> getObjectsAroundObject(SOEObject o) {
		GridElement element = getNearestElement(o.getX(), o.getY());
		return element.getAllNearObjects();
	}
	
	public boolean move(SOEObject o, float newX, float newY) {
		GridElement element = getNearestElement(o.getX(), o.getY());
		GridElement newElement = getNearestElement(newX, newY);
		if (element == null || newElement == null) {
			return false;
		}
		// Same spot -- nothing special needed
		if (element.equals(newElement)) {
			o.setX(newX);
			o.setY(newY);
			return true;
		} else {
			int oldXIndex = element.getXArrayIndex();
			int oldYIndex = element.getYArrayIndex();
			int newXIndex = newElement.getXArrayIndex();
			int newYIndex = newElement.getYArrayIndex();
			// Difference in the two can be +- 1
			
			int diffX = (oldXIndex - newXIndex);
			int diffY = (oldYIndex - newYIndex);
			if (diffX >= -1 && diffX <= 1) {
				if (diffY >= -1 && diffY <= 1) {
					element.removeObjectFromGridElement(o, true);
					o.setX(newX);
					o.setY(newY);
					newElement.addObjectToGridElement(o);
					return true;
				}
			}
		}
		DataLog.logEntry("Object " + o.getIFFFileName() + " has moved significant distance -- probably shuttled or planet-hopped?", "Grid.move", Constants.LOG_SEVERITY_MINOR, true, true);
		DataLog.logEntry("OldX["+o.getX()+"] OldY["+ o.getY()+"]", "Grid.move", Constants.LOG_SEVERITY_MINOR, true, true);
		DataLog.logEntry("NewX["+newX+"] NewY["+ newY+"]", "Grid.move", Constants.LOG_SEVERITY_MINOR, true, true);
		return false;
	}
	
	public GridElement[][] getAllElements() {
		return theGrid;
	}
	
	public int getDimension() {
		return iElementDimension;
	}
	
	public GridElement getElement(int x, int y) {
		if (x >= theGrid.length  || x < 0) {
			return null;
		} else if (y < 0 || y > theGrid[x].length) {
			return null;
		}
		return theGrid[x][y];
	}
	
	/**
	 * The grid count represents the maximum index of the X and Y dimensions of the Grid.
	 * @return
	 */
	public int getGridCount() {
		return gridCount;
	}
	
	protected Vector<GridElement> getAllContainedElements(Rectangle2D rect) {
		Vector<GridElement> elementsToReturn = new Vector<GridElement>();
		for (int x = 0; x < theGrid.length; x++) {
			for (int y = 0; y < theGrid[x].length; y++) {
				if (rect.contains(theGrid[x][y].getX(), theGrid[x][y].getY())) {
					elementsToReturn.add(theGrid[x][y]);
				}
			}
		}
		return elementsToReturn;
	}
	
	protected synchronized void generateNoBuildZones() {
		synchronized(this) {
			Polygon[][] theShapes = new Polygon[theGrid.length][theGrid[0].length];
			Rectangle2D[][] theRectangles = new Rectangle2D[theGrid.length][theGrid[0].length];
			for (int i = 0; i < theGrid.length; i++) {
				for(int j = 0; j < theGrid[i].length; j++) {
					theShapes[i][j] = new Polygon();
					Hashtable<Long, SOEObject> vAllObjects = theGrid[i][j].getAllObjectContained();
					Enumeration<SOEObject> vAllObjectsEnum = vAllObjects.elements();
					while (vAllObjectsEnum.hasMoreElements()) {
						SOEObject o = vAllObjectsEnum.nextElement();
						if (o instanceof Structure) {
							Structure s = (Structure)o;
							theShapes[i][j].addPoint((int)s.getX(), (int)s.getY());
						}
					}
					theRectangles[i][j] = theShapes[i][j].getBounds2D();
				}
			}
			System.out.println("Planet " + Constants.PlanetNames[iPlanetID] + " grid rectangles.");
			for (int i = 0; i < theRectangles.length; i++) {
				for (int j = 0; j < theRectangles[i].length; j++) {
					double x1 = theRectangles[i][j].getMinX();
					double x2 = theRectangles[i][j].getMaxX();
					double y1 = theRectangles[i][j].getMinY();
					double y2 = theRectangles[i][j].getMaxY();
					if (x1 != 0 || y1 != 0 || x2 != 0 || y2 != 0) {
						System.out.println("Rectangle for element ["+i+"]["+j+"] bounds x1["+x1+ "],y1["+y1+"],x2["+x2+"],y2["+y2+"]");
					}
				}
			}
			System.out.flush();
		}
	}
}
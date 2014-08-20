import java.io.Serializable;


public class ResourceSpawnCoordinateData implements Serializable{
	public final static long serialVersionUID = 1l;
	private float spawnX = 0;
	private float spawnY = 0;
	private float spawnR = 0;
	private float spawnD = 0;
	protected float getSpawnDensity() {
		return spawnD;
	}
	protected void setSpawnDensity(float spawnD) {
		this.spawnD = spawnD;
	}
	protected float getSpawnRadius() {
		return spawnR;
	}
	protected void setSpawnRadius(float spawnR) {
		this.spawnR = spawnR;
	}
	protected float getSpawnX() {
		return spawnX;
	}
	protected void setSpawnX(float spawnX) {
		this.spawnX = spawnX;
	}
	protected float getSpawnY() {
		return spawnY;
	}
	protected void setSpawnY(float spawnY) {
		this.spawnY = spawnY;
	}
	
	
}

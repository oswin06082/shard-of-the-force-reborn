import java.io.Serializable;

/**
 *
 * @author Tomas Cruz
 */
public class POI implements Serializable {
    public final static long serialVersionUID = 1;
    private float x,y;
    private int planetid;
    private int badgeid;
    
    public POI(float px, float py, int pplanet, int pbid){
        x = px;
        y = py;
        planetid = pplanet;
        badgeid = pbid;                
    }
    
    protected float getX(){
        return x;
    }
    protected float getY(){
        return y;
    }
    
    protected int getPlanetID(){
        return planetid;
    }
    protected int getBadgeID(){
        return badgeid;
    }
}

import java.io.Serializable;
/**
 * Allows to have one object for containing coordinate Values.
 * @author Tomas Cruz
 */
public class Coordinates implements Serializable {
    public final static long serialVersionUID = 1;
    
    private float x,y,z,cx,cy,cz,oI,oJ,oK,oW,Angle;
    
    public Coordinates(){
        
    }
    
    protected float getX(){
        return x;
    }
    
    protected void setX(float v){
        x = v;
    }
    
    protected float getY(){
        return y;
    }
    
    protected void setY(float v){
        y = v;
    }
    
    protected float getZ(){
        return z;
    }
    
    protected void setZ(float v){
        z = v;
    }
    
    protected float getCellX(){
        return cx;
    }
    
    protected void setCellX(float v){
        cx = v;
    }
    
    protected float getCellY(){
        return cy;
    }
    
    protected void setCellY(float v){
        cy = v;
    }
    
    protected float getCellZ(){
        return cz;
    }
    
    protected void setCellZ(float v){
        cz = v;
    }
    
    protected float getOrientationN(){
        return oI;
    }
    
    protected void setOrientationN(float v){
        oI = v;
    }
    
    protected float getOrientationS(){
        return oJ;
    }
    
    protected void setOrientationS(float v){
        oJ = v;
    }
    
    protected float getOrientationE(){
        return oK;
    }
    
    protected void setOrientationE(float v){
        oK = v;
    }
    
    protected float getOrientationW(){
        return oW;
    }
    
    protected void setOrientationW(float v){
        oW = v;
    }
    
    protected float getMovementAngle(){
        return Angle;
    }
    
    protected void setMovementAngle(float v){
        Angle = v;
    }
}

import java.io.Serializable;

/**
 * An Experience value.
 * @author Darryl
 *
 */
public class Experience implements Serializable{
	public final static long serialVersionUID = 1l;
	public String sExperienceName;
	public int iExperienceID;
	
	/**
	 * Construct a new Experience object.
	 */
	public Experience() {
		sExperienceName = null;
		iExperienceID = -1;
	}
}

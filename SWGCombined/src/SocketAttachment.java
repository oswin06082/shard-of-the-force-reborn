/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * this is the socket attachments class for adding socket attachments.
 * a socket attachment is used to extend and add functions or abilities to
 * many items like weapons, armor, clothing .
 * 
 * @author Tomas Cruz
 */
public final class SocketAttachment extends TangibleItem {
	public final static long serialVersionUID = 1l;
    public SocketAttachment(){
        
    }
    
	public int experiment(long iExperimentalIndex, int numExperimentationPointsUsed, Player thePlayer) {
		// Can't experiment on a SocketAttachment -- just return.
		return 0;
	}

}

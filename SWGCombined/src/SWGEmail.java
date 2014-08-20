import java.util.Vector;
import java.io.Serializable;

/**
 * The SWGEmail class contains all the information about an in-game email sent between 2 people.
 * @author Darryl
 *
 */
public class SWGEmail implements Serializable{
	public final static long serialVersionUID = 1l;
	private transient int objectID = 0; //we dont want to save the id we get it from the db and set it there
	private long recipientID = 0;
	private long originatorID = 0;
	private Vector<Waypoint> vWaypointAttachmentList = null;
	private String sMessageHeader = null;
	private String sMessageBody = null;
	private boolean bRead  = false;
	private boolean bIsNew = true;
	private long messageCreationTime;
        private boolean bDeleteEmail;
        private transient ZoneClient transactionRequester; // Aye -- we definitely don't want to save the ZoneClient.  lol
    	private transient boolean bSent;
	/**
	 * Constructs a new Email message at the current time.
	 */
	public SWGEmail() {
		messageCreationTime = System.currentTimeMillis();
                bRead = false;
                bDeleteEmail = false;
                bSent = false;
	}
	
	/**
	 * Constructs a new Email message with the given ID at the current time.
	 * @param objectID
	 */
	public SWGEmail(int objectID) {
		this.objectID = objectID;
		messageCreationTime = System.currentTimeMillis();
        bRead = false;
        vWaypointAttachmentList = new Vector<Waypoint>();  
        bDeleteEmail = false;
        bSent = false;
	}
	
	/**
	 * Constructs a new Email message with the given ID, the given creator ID, the given receiver ID, the given subject (header), the given message body, a Vector of attachments (may be null), and the given creation time..
	 * @param objectID -- The id of this email message.
	 * @param sender -- The object ID of the player who created this message.
	 * @param receiver -- The object ID of the player who received this message.
	 * @param header -- The subject line of this message.
	 * @param body -- The text body of this message.
	 * @param attachments -- The list of Waypoints which may be attached to this message.
	 * @param creationTime -- The time at which this message was received.
	 */
	public SWGEmail(int objectID, long sender, long receiver, String header, String body, Vector<Waypoint> attachments,boolean readFlag) {
		try{
            this.objectID = objectID;
			originatorID = sender;
			recipientID = receiver;
			sMessageHeader = header;
			sMessageBody = body;
            vWaypointAttachmentList = attachments;
            messageCreationTime = System.currentTimeMillis();
            bRead = readFlag;
            }
        catch (Exception e){
            System.out.println("Exception in SWGEmail: " + e.toString());
            e.printStackTrace();
        }
        bDeleteEmail = false;
        bSent = false;
	}
	
	/**
	 * Gets the Email Object ID
	 * @return The Email ID.
	 */
	public int getEmailID() {
		return objectID;
	}
	
	/**
	 * Gets the Object ID of the Player who created this Email.
	 * @return The creator object ID.
	 */
	public long getSenderID() {
		return originatorID;
	}
	
	/**
	 * Gets the Object ID of the Player who received this Email.
	 * @return The receiver object ID.
	 */
	public long getRecipID() {
		return recipientID;
	}
	
	/**
	 * Indicates if this email has been opened or not.
	 * @return True if the email has been opened, otherwise returns false.
	 */
	public boolean isRead() {
		return bRead;
	}
	
        /**
         * Sets the Read Bool to True to indicate that the message has been read.
         * @return void
         */
        public void setRead() {
                bRead = true;
        }
        
	/**
	 * Gets the email message's subject line.
	 * @return The header
	 */
	public String getHeader(){ 
		return sMessageHeader;
	}
	
	/**
	 * Gets the email message's body.
	 * @return The body.
	 */
	public String getBody() {
		return sMessageBody;
	}
	
	/**
	 * Gets the list of attachments for this email.
	 * @return The email attachments.
	 */
	public Vector<Waypoint> getAttachments() {
		return vWaypointAttachmentList;
	}
	
	/**
	 * Gets the time at which this message was received.
	 * @return The message receipt time.
	 */
	public long getMessageTime() {
		return messageCreationTime;
	}
        
        /**
         * Sets the id for this email object
         * @param ID
         */
        protected void setEmailID(int ID){
            this.objectID = ID;
        }
        
        /**
         * Sets the value of who asked for information on an email when the email object is used to request email data or a transaction
         * @param C - ZoneClient
         */
        protected void setTransactionRequester(ZoneClient C){
            transactionRequester = C;
        }
        
        /**
         * Gets the Transaction Requester for this email object when the email object is used to request email data or a transaction
         * @return ZoneClient
         */
        protected ZoneClient getTransactionRequester(){
            return transactionRequester;
        }
        
        /**
         * Sets the Deletion Flag on the email
         */
        protected void setDeleteFlag(){
            bDeleteEmail = true;
        }
        
        /**
         * Returns the Value of the Delete Flag in Boolean form
         * @return boolean
         */
        protected boolean getDeleteFlag(){
            return bDeleteEmail;
        }
        
        /**
         * Sets the flag for the email router as to if this email has been sent or not
         */
        protected void setSentFlag(){
            bSent = true;
        }
        
        /**
         * Returns the state of the email wether is sent or not
         * @return boolean
         */
        protected boolean isSent(){
            return bSent;
        }
        
        /**
         * Sets the recipient ID for this email object. 
         * This is normally used to set the id for us in the vSentEmails Vector.
         * @param R
         */
        protected void setRecipientID(long R){
            recipientID = R;
        }
        
        /**
         * Used to retrieve the Recipient ID for this Email Object.
         * This is normally used to get the id for us in the vSentEmails Vector.
         * @return long recipientID
         */
        protected long getRecipientID(){
            return recipientID;
        }
        
        protected void setIsNew(boolean b) {
        	bIsNew = b;
        }
        
        protected boolean getIsNew() {
        	return bIsNew;
        }
}

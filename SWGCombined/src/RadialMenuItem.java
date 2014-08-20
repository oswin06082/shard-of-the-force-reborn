import java.io.Serializable;

/**
 * The RadialMenuItem class is a simple class for holding information on the available Radial Menu options for an object.
 * @author Darryl
 * Updated by plasmaflow
 */
public class RadialMenuItem implements Serializable  {
	public final static long serialVersionUID = 1l;
        private byte bButtonNumber; //this is the button number in the radial first button is 1
        private byte bParentButton; //this tells us if a button is under another button, this tells us which button this button will reside if this is the top button it will always be 0
	private char bCommandID; //this is the actual command id for the command being executed, this varies by object but are usually common across objects i/e examine is always 0x07
	private byte bActionLocation; // this byte will be 0x01 if the client needs to execute the command or 0x03 if the server executes the command. if we tell the client its 0x01 even if the coomand is server side then it will try to do it client side and server will never get it.
	private String sButtonText; //this always comes blank from the client. its up ot us to send a new label if its not the default label.
        
        //the following values are used for when the object is used by the server when storing the cached radials from the database
	private long lDBID;
        private int iItemCRC;
        private int iItemTemplate;
        private long lItemID;
        private int iCondition;
       private byte iVisibility;
        
	/**
         * Construct a RadialMenuItem with the passed Parameters:
         * @param ButtonNumber
         * @param ParentButton
         * @param CommandID
         * @param ActionLocation
         * @param ButtonText
         */
	public RadialMenuItem(byte ButtonNumber, byte ParentButton, char CommandID,byte ActionLocation, String ButtonText) {
		bButtonNumber = ButtonNumber;
                bParentButton = ParentButton;
                bCommandID = CommandID;
                bActionLocation = ActionLocation;
                sButtonText = ButtonText;
	}

	
        /**
         * returns the button number for this button, all buttons begimn at 1, 0 is not a valid button since 0 means no parent
         * @return
         */
        public byte getButtonNumber(){
            return bButtonNumber;
        }
        
        public void setButtonNumber(byte number) {
        	bButtonNumber = number;
        }
        /**
         * returns the parent button for this button, if 0 it means it has no parent. Parents begin at 1
         * @return
         */
        public byte getParentButton(){
            return bParentButton;
        }
        
        /**
         * returns the command code value for this button
         * @return byte
         */
        public char getCommandID(){
            return bCommandID;
        }
        
        /**
         * returns the action location for this button
         * @return byte
         */
        public byte getActionLocation(){
            return bActionLocation;
        }
        
        /**
         * returns the text string to be used for this button Eg: @pet/pet_menu:pet_mount
         * @return String
         */
        public String getButtonText(){
            return sButtonText;
        }
        
        /**
         * Sets the Text String to use for this button Eg: @pet/pet_menu:pet_mount
         * @param ButtonText
         */
        protected void setButtonText(String ButtonText){
            sButtonText = ButtonText;
        }
        
        /**
         * changes the location of the button action to 0x03 server side
         */
        protected void setActionLocationServer(){
            bActionLocation = 0x03;
        }
        
        /**
         * changes the location of the button action to 0x01 cliend side
         */
        protected void setActionLocationClient(){
            bActionLocation = 0x01;
        }
        
        /**
         * sets the database id for this menu item
         * @param id
         */
        protected void setlDBID(long id){
            lDBID = id;
        }
        
        /**
         * sets the items crc value that this button should be used for.,
         * @param CRC
         */
        protected void setiItemCRC(int CRC){
            iItemCRC = CRC;
        }
        
        /**
         * sets the template id this buttono should be used for
         * @param template
         */
        protected void setiItemTemplate(int template){
            iItemTemplate = template;
        }
        
        /**
         * sets the item id that this button should be used for
         * @param ID
         */
        protected void setlItemID(long ID){
            lItemID = ID;
        }
        
        /**
         * sets the condition for using this button
         * @param cond
         */
        protected void setiCondition(int cond){
            iCondition = cond;
        }
        
        /**
         * returns the database id that this button is in the db
         * @return
         */
        protected long getlDBID(){
            return lDBID;
        }
        
        /**
         * returns the crc of the item that this button should be used for.
         * @return
         */
        protected int getiItemCRC(){
            return iItemCRC;
        }
        
        /**
         * returns the template id that this button should be used for,
         * @return
         */
        protected int getiItemTemplate(){
            return iItemTemplate;
        }
        
        /**
         * returns the item id that this button is to be used for.
         * @return
         */
        protected long getlItemID(){
            return lItemID;
        }
        
        /**
         * This item returns the condition for which this button should be used i/e dead, incapped mounted or in use etc...
         * @return
         */
        protected int getiCondition(){
            return iCondition;
        }
        
        /**
         * Returns the contents of the radial item as a new radial item.
         * this is to be used when getting a radial from the server database of radials.
         * @return RadialMenuItem
         */
        protected RadialMenuItem getRadialOption(){
            return new RadialMenuItem(bButtonNumber, bParentButton,bCommandID,bActionLocation,sButtonText);
        }
        
        protected void setVisibility(byte iVisibility) {
        	this.iVisibility = iVisibility;
        }
        
        protected byte getVisibility() {
        	return iVisibility;
        }
}


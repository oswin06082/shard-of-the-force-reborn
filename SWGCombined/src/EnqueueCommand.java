
/**
 * this class makes an object out of the enqueue commands to carry data for commands to be queued 
 * and processed
 * @author Tomas Cruz
 */
public class EnqueueCommand {

        private long objectID; // This is the ID of the player who has spoken.
        private int tickCounter;
        private int commandID;
        private int commandCRC;
        private long targetID;
        private String[] parameters;
        
        /**
         * Default Constructor
         */
        public EnqueueCommand(){  }
        
        /**
         * Sets all parameters for the Enqueue Command Object.
         * @param lObjectID - Who sends the command.
         * @param iTickCounter - Timer for this command.
         * @param iCommandID - Command Identifier.
         * @param iCommandCRC - Command CRC Identifier.
         * @param lTargetID - The Object that is to be acted upon.
         * @param sParameters - This is the parameters received in the command enqueue
         * @param sSplitChar - The Specific character that divides all parameters in the sParameters Variable.
         */
        public EnqueueCommand(long lObjectID,int iTickCounter,int iCommandID,int iCommandCRC,long lTargetID,String sParameters,String sSplitChar){
            objectID = lObjectID;
            tickCounter = iTickCounter;
            commandID = iCommandID;
            commandCRC = iCommandCRC;
            targetID = lTargetID;
            parameters = sParameters.split(sSplitChar);
        }
        
        /**
         * Sets all parameters for the Enqueue Command Object.
         * @param lObjectID - Who sends the command.
         * @param iTickCounter - Timer for this command.
         * @param iCommandID - Command Identifier.
         * @param iCommandCRC - Command CRC Identifier.
         * @param lTargetID - The Object that is to be acted upon.
         * @param sParameters - The parameter Array
         */
         public EnqueueCommand(long lObjectID,int iTickCounter,int iCommandID,int iCommandCRC,long lTargetID,String sParameters[]){
            objectID = lObjectID;
            tickCounter = iTickCounter;
            commandID = iCommandID;
            commandCRC = iCommandCRC;
            targetID = lTargetID;
            parameters = sParameters;
        }
         
        /**
         * Sets the Object ID Requesting this command
         * @param lObjectID - Who sends the command.
         */
        public void setObjectID(long lObjectID){
            objectID = lObjectID;
        }
        
        /**
         * Sets the Command Tick Counter
         * @param iTickCounter - Timer for this command.
         */
        public void setTickCounter(int iTickCounter){
            tickCounter = iTickCounter;
        }
        
        /**
         * Sets the Command Identifier
         * @param iCommandID - Command Identifier.
         */
        public void setCommandID(int iCommandID){
            commandID = iCommandID;
        }
        
        /**
         * Sets the CRC Value of the Command
         * @param iCommandCRC - Command CRC Identifier.
         */
        public void setCommandCRC(int iCommandCRC){
            commandCRC = iCommandCRC;
        }
        
        /**
         * Sets the Target Object to be Acted Upon.
         * @param lTargetID - The Object that is to be acted upon.
         */
        public void setTargetID(long lTargetID){
            targetID = lTargetID;
        }
        
        /**
         * Sets the Value of the Parameter Array
         * @param sParameters - This is the parameters received in the command enqueue
         * @param sSplitChar - The Specific character that divides all parameters in the sParameters Variable.
         */
        public void setParameters(String sParameters, String sSplitChar){
            parameters = sParameters.split(sSplitChar);
        }
        
        /**
         * Sets the value of the whole parameter array
         * @param sParameters[] String Array
         */
        public void setParameters(String sParameters[]){
            parameters = sParameters;
        }
        
        /**
         * Returns the Object ID
         * @return long
         */
        public long getObjectID(){
            return objectID;
        }
        
        /**
         * Returns the Tick Counter
         * @return int
         */
        public int getTickCounter(){
            return tickCounter;
        }
        
        /**
         * Returns the Command ID
         * @return int
         */
        public int getCommandID(){
            return commandID;
        }
        
        /**
         * Returns the Command CRC Value
         * @return int
         */
        public int getCommandCRC(){
            return commandCRC;
        }
        
        /**
         * Returns the Target ID to Be Acted Upon
         * @return long
         */
        public long getTargetID(){
            return targetID;
        }
        
        /**
         * Returns One Parameter from the Parameter Array 0 Based
         * @param SubString Int
         * @return String[SubString] - Parameter String
         */
        public String getParameter(int SubString){
            return parameters[SubString];
        }
        
        /**
         * Returns the Entire Parameter Array
         * @return String[]
         */
        public String [] getAllParameters(){
            return parameters;
        }
        
        /**
         * Returns the Byte Value of the Substring in the Parameter Array
         * @param SubString Int - The Subscript we want returned
         * @return byte
         */
        public byte getByteParam(int SubString){
            return Byte.parseByte(parameters[SubString]);
        }
        
        /**
         * Returns the Short Value of the Substring in the Parameter Array
         * @param SubString Int - The Subscript we want returned
         * @return short
         */
        public short getShortParam(int SubString){
            return Short.parseShort(parameters[SubString]);
        }
        
        /**
         * Returns the Integer Value of the Substring in the Parameter Array
         * @param SubString Int - The Subscript we want returned
         * @return int
         */
        public int getIntParam(int SubString){
            return Integer.parseInt(parameters[SubString]);
        }
        
        /**
         * Returns the Long Value of the Substring in the Parameter Array
         * @param SubString Int - The Subscript we want returned
         * @return long
         */
        public long getLongParam(int SubString){
            return Long.parseLong(parameters[SubString]);
        }
        
        /**
         * Returns the Float Value of the Substring in the Parameter Array
         * @param SubString Int - The Subscript we want returned
         * @return float
         */
        public float getFloatParam(int SubString){
            return Float.parseFloat(parameters[SubString]);
        }
}

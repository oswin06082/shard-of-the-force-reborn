/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.Date;

/**
 * This object is used to create a way of sending log entries to the log class
 * @author Tomas Cruz
 * @param DataLogObject() Defines a Default Object to be filled by doing sets
 * ----------------------------------------------------------------------------
 * @param String LogSource - Who is making this log entry - I/E: Zone Server - Login Server - Routine Name - Purely Informational.
 * @param String Source - Source IP Address or Host Name Information.
 * @param Date TimeStamp - Time Stamp for this Log;
 * @param byte Direction - What direction was this data flowing: 0 = Out , 1 = In, 127 = not set;
 * @param SOEOutputStream OutputPacket - Packet Data to be logged to file as output data.
 * @param SOEOutputStream OutputPacket - Packet Data to be logged to file as input data.
 * @param String Destination - Destination IP Address or Host Name Information.
 * @param String LogEntryText - Log Text to be put on the file for this entry.
 * @param byte LogType - Determines the Log Type for this entry - 0 = PacketLog , 1 = General Information Log , 127 = not set
 * @param int LogSeverity - Determines how severe is the Log information being saved. 0 = Information , 1 and Above is to be determined by application coders.
 * 
 */
public class DataLogObject implements Serializable {
    public final static long serialVersionUID = 1l;
	private String LogSource;
    private String Source;
    private Date TimeStamp;
    private byte Direction;
    private byte[] packetData;
    private String Destination;
    private String LogEntryText;
    private int LogSeverity;
    private byte LogType;
    private int CRCSeed;
    private Exception e;
    
        /**
         * @param None
         * Initializes a default Data Log Object;
         */
    public DataLogObject(){
        
        TimeStamp = new Date();
        TimeStamp.setTime(System.currentTimeMillis());
        Direction = 127;
        LogType = 127;
    }
    
        /**
         * Defines a Packet Log Object for an inbound Packet to be Logged
         * @param (String sLogSource,String sSource,byte[] bPacketData, String sDestination)
         */
    public DataLogObject(String sLogSource,String sSource,byte[] bPacketData, String sDestination,byte bDirection ){
        
        TimeStamp = new Date();
        LogType = 0;
        TimeStamp.setTime(System.currentTimeMillis());
        Direction = bDirection;
        LogSource = sLogSource;
        Source = sSource;
        packetData = bPacketData;
        Destination = sDestination;
    }
    
         /**
         * Defines a General Log Application Entry
         * @param (String sLogSource,String sLogEntryText,int iLogSeverity)
         */
    public DataLogObject(String sLogSource,String sLogEntryText,int iLogSeverity){
       
        TimeStamp = new Date();
        LogType = 1;
        TimeStamp.setTime(System.currentTimeMillis());
        LogSource = sLogSource;
        Direction = 127;
        LogEntryText = sLogEntryText;
        LogSeverity = iLogSeverity;
        
    }

    public DataLogObject(String sLogSource,String sLogEntryText,int iLogSeverity,Exception e){

        TimeStamp = new Date();
        LogType = 1;
        TimeStamp.setTime(System.currentTimeMillis());
        LogSource = sLogSource;
        Direction = 127;
        LogEntryText = sLogEntryText;
        LogSeverity = iLogSeverity;
        this.e = e;

    }
    public Exception getException(){
        return e;
    }

    public void setLogSource(String S){
        LogSource = S;
    }
    public void setSource(String S){
        Source = S;
    }
    public void setTimeStamp(long T){
        TimeStamp.setTime(T);
    }
    public void setLogDirection(byte b){
        Direction = b;
    }
    public void setPacketData(byte[] P){
        packetData = P;
    }
    public void setDestination(String S){
        Destination = S;
    }
    public void setLogentryText(String S){
        LogEntryText = S;
    }
    public void setLogSeverity(int S){
        LogSeverity = S;
    }
    public void setLogType(byte b){
        LogType = b;
    }
    public void setCRCSeed(int Seed){
        CRCSeed = Seed;
    }
    public String getLogSource(){
        return LogSource;
    }
    public String getSource(){
        if(Source == null)
        {
            return "Unknown";
        }
        return Source;
    }
    public Date getdTimeStamp(){
        return TimeStamp;
    }
    public long getlTimeStamp(){
        return TimeStamp.getTime();
    }
    public String getSTimeStamp(){
        return TimeStamp.toString();
    }
    public byte getLogDirection(){
        return Direction;
    }
     public byte[] getPacketData(){
        return packetData;
    }
    public String getDestination(){
        return Destination;
    }
    public String getLogentryText(){
        return LogEntryText;
    }
    public int getLogSeverity(){
        return LogSeverity;
    }
    public byte getLogType(){
        return LogType;
    }
    public int getCRCSeed(){
        return CRCSeed;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Date;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;
import java.util.Calendar;
import java.text.SimpleDateFormat;


/**
 * This is the Datalog Server.
 * @author Tomas Cruz
 */
public class DataLog implements Runnable {
    private static DataLog log;
	private Date StartTime;
    private String PacketLogFileName;
    private String ServerLogFileName;
    private static boolean dataLogRun;
    public static Vector<DataLogObject> qPacketLog;
    public static Vector<DataLogObject> qServerLog;
    //private DataLog LogServer;
    private Thread myThread;
    private BufferedWriter PacketLogFile;
    private BufferedWriter ServerLogFile;
    private int PacketSequence;
    //private ZoneServer zServer;
    private static String sHostName;
    private static int zPort;
    private static long lLoopYieldTime;
    //protected DataLog lServer;
    
   private DataLog(){
       lLoopYieldTime = 10000;
       if(qPacketLog==null)
       {
           qPacketLog = new Vector<DataLogObject>();
       }
       if(qServerLog==null)
       {
           qServerLog = new Vector<DataLogObject>();
       }
       dataLogRun = true;
       //start
       myThread = new Thread(this);
       myThread.setName("DataLog thread");
       myThread.start();
       logEntry("Log Server Started", "LogServer",Constants.LOG_SEVERITY_INFO, true, true);
   } 
   
   public static void initialize() {
	   log = new DataLog();
       lLoopYieldTime = 10000;
   }
   public void run(){
       lLoopYieldTime = 10000;
       try {
           if(qPacketLog==null)
           {
               qPacketLog = new Vector<DataLogObject>();
           }
           if(qServerLog==null)
           {
               qServerLog = new Vector<DataLogObject>();
           }
                               
           StartTime = new Date();
           String T;
           StartTime.setTime(System.currentTimeMillis());
           T = StartTime.toString();
           T = T.replace(":", "-");
           T = T.replace(" ", "-");

           PacketLogFileName = "./PacketLog-" + T + ".Log";
           ServerLogFileName = "./ServerLog-" + T + ".Log";
           dataLogRun = false;
           PacketSequence = 0;

           PacketLogFile = new BufferedWriter(new FileWriter(PacketLogFileName));
           ServerLogFile = new BufferedWriter(new FileWriter(ServerLogFileName));
           
           
                   
       } catch (Exception e) {
           System.out.println("Exception while opening Log Files: " + e.toString());
           e.printStackTrace();
       }


      
       while(myThread != null)
       {
          
           try {
                synchronized(this) {
                        Thread.yield();
                        wait(lLoopYieldTime);
                }
                //System.out.println("Log server Running " + now());
                if(!SWGGui.isPacketLogEnabled())
                {
                    qPacketLog.clear();
                }
                while(!qPacketLog.isEmpty())
                {
                    DataLogObject L = null;
                    try{
                        L = qPacketLog.firstElement();
                    }catch(Exception qe){
                        if(qe instanceof java.util.NoSuchElementException)
                        {
                            break;
                        }
                    }
                    qPacketLog.remove(L);
                    switch(L.getLogDirection())
                    {
                        case 0: //Outbound
                        {
                            PacketLogFile.write("Packet: " + PacketSequence);
                            PacketLogFile.newLine();
                            PacketLogFile.write("Log Origin: " + L.getLogSource());
                            PacketLogFile.newLine();
                            PacketLogFile.write("Time Stamp: " + L.getSTimeStamp());
                            PacketLogFile.write(" Data Length: " + L.getPacketData().length);
                            PacketLogFile.newLine();
                            PacketSequence++;                            
                            PacketLogFile.write(getSHostName() + ":" + getZPort() + " -> " + L.getDestination() + " (Server -> Client)");
                            PacketLogFile.newLine();
                            PacketLogFile.newLine();
                            byte[] pData = L.getPacketData();
                            
                            
                            int nLength = L.getPacketData().length;
                            String ByteData = "";
                            String Chars = "";
                            
                            int ctr = 0;
                            for(int i = 0; i < nLength; i++)
                            {
                                byte b = pData[i];
                                ByteData += PacketUtils.getByteCode(b) + " ";
                                if(b >= 0x21 && b <= 0x7E)
                                {
                                    Chars += (char)b;
                                }
                                else
                                {                                    
                                    Chars += (char)'.';
                                }

                                if(ctr == 15)
                                {
                                    ctr=0;
                                    if(ByteData.length() < 48)
                                    {
                                        String spaces = "";
                                        for(int sp = 0; sp < (48 - ByteData.length()); sp++ )
                                        {
                                            spaces += " ";
                                        }
                                        PacketLogFile.write(ByteData + spaces +  "     " + Chars);
                                    }
                                    else
                                    {
                                        PacketLogFile.write(ByteData + "     " + Chars);
                                    }
                                    ByteData = "";
                                    Chars = "";
                                    PacketLogFile.newLine();
                                }
                                else
                                {
                                    ctr++;
                                }
                            }
                            if(ByteData.length() < 48)
                            {
                                String spaces = "";
                                for(int i = 0; i < (48 - ByteData.length()); i++ )
                                {
                                    spaces += " ";
                                }
                                PacketLogFile.write(ByteData + spaces +  "     " + Chars);
                            }
                            else
                            {
                                PacketLogFile.write(ByteData + "     " + Chars);
                            }
                            PacketLogFile.newLine();
                            PacketLogFile.write("----------------------------------------------------------------------");
                            PacketLogFile.newLine();
                            PacketLogFile.write("======================================================================");
                            PacketLogFile.newLine();
                            PacketLogFile.newLine();
                            PacketLogFile.flush();
                            break;
                        }
                        case 1://Inbound
                        {                           
                            PacketLogFile.write("Packet: " + PacketSequence);
                            PacketLogFile.newLine();
                            PacketLogFile.write("Log Origin: " + L.getLogSource());
                            PacketLogFile.newLine();
                            PacketLogFile.write("Time Stamp: " + L.getSTimeStamp());
                            PacketLogFile.write(" Data Length: " + L.getPacketData().length);
                            PacketLogFile.newLine();
                            PacketSequence++;                            
                            PacketLogFile.write(L.getSource() + " -> " + getSHostName() + ":" + getZPort() + " (Client -> Server)");
                            PacketLogFile.newLine();
                            PacketLogFile.newLine();
                            byte[] pData = L.getPacketData();
                            int nLength = L.getPacketData().length;
                            String ByteData = "";
                            String Chars = "";
                            
                            int ctr = 0;
                            for(int i = 0; i < nLength; i++)
                            {
                                byte b = pData[i];
                                ByteData += PacketUtils.getByteCode(b) + " ";
                                if(b >= 0x21 && b <= 0x7E)
                                {
                                    Chars += (char)b;
                                }
                                else
                                {                                    
                                    Chars += (char)'.';
                                }

                                if(ctr == 15)
                                {
                                    ctr=0;
                                    if(ByteData.length() < 48)
                                    {
                                        String spaces = "";
                                        for(int sp = 0; sp < (48 - ByteData.length()); sp++ )
                                        {
                                            spaces += " ";
                                        }
                                        PacketLogFile.write(ByteData + spaces +  "     " + Chars);
                                    }
                                    else
                                    {
                                        PacketLogFile.write(ByteData + "     " + Chars);
                                    }
                                    ByteData = "";
                                    Chars = "";
                                    PacketLogFile.newLine();
                                }
                                else
                                {
                                    ctr++;
                                }
                            }
                            if(ByteData.length() < 48)
                            {
                                String spaces = "";
                                for(int i = 0; i < (48 - ByteData.length()); i++ )
                                {
                                    spaces += " ";
                                }
                                PacketLogFile.write(ByteData + spaces +  "     " + Chars);
                            }
                            else
                            {
                                PacketLogFile.write(ByteData + "     " + Chars);
                            }
                            
                            PacketLogFile.newLine();
                            PacketLogFile.write("----------------------------------------------------------------------");
                            PacketLogFile.newLine();
                            PacketLogFile.write("======================================================================");
                            PacketLogFile.newLine();
                            PacketLogFile.newLine();
                            PacketLogFile.flush();
                            break;
                        }
                        default:
                        {
                            
                        }
                    }
                    
                }
                if(!qServerLog.isEmpty())
                {
                    while(!qServerLog.isEmpty())
                    {
                        //System.out.println("Writing Log entry");
                        DataLogObject L = qServerLog.remove(0);
                        switch(L.getLogType())
                        {
                            case 1://general log
                            {
                               // System.out.println("Writing Log Entry to File.");
                                ServerLogFile.write(now()  + " Source:" + L.getLogSource() + " | " + L.getLogentryText() + " | SEV: " + Constants.LOG_SEVERITY_STRINGS[L.getLogSeverity()]);
                                ServerLogFile.newLine();
                                ServerLogFile.flush();
                                break;
                            }
                            case 2://Exception Trace
                            {
                               // System.out.println("Writing Log Entry to File.");
                                ServerLogFile.write("V-------------------------------------EXCEPTION LOG-------------------------------------V");
                                ServerLogFile.newLine();
                                ServerLogFile.write(now() + " Source:" + L.getLogSource() + " | " + L.getLogentryText() + " | SEV: " + Constants.LOG_SEVERITY_STRINGS[L.getLogSeverity()] );
                                ServerLogFile.newLine();
                                ServerLogFile.write("-----Stack Trace Begin-----");
                                ServerLogFile.newLine();
                                StackTraceElement[] elements = L.getException().getStackTrace();
                                ServerLogFile.write("-----TOTAL ELEMENTS: " + elements.length + "-----");
                                ServerLogFile.newLine();
                                for (int i = 0; i < elements.length; i++)
                                {
                                    ServerLogFile.write("STE: " + i + " | " + elements[i].toString());
                                    ServerLogFile.newLine();
                                }
                                ServerLogFile.write("------Stack Trace End------");
                                ServerLogFile.newLine();
                                ServerLogFile.write("^-------------------------------------EXCEPTION LOG-------------------------------------^");
                                ServerLogFile.newLine();
                                ServerLogFile.flush();
                                break;
                            }
                            default:
                            {

                            }
                        }
                    }
                }
                
           } catch (Exception e) {
                System.out.println("Error in DataLog.run: " + e.toString());
                e.printStackTrace();
           }
           
       }
   }
   protected void stopLog(){
       dataLogRun = false;
   }
   protected void startLog(){
       if(!dataLogRun)
       {
           dataLogRun = true;
           //start
           myThread = new Thread(this);
           myThread.setName("DataLog thread");
           myThread.start();
           logEntry("Log Server Started", "LogServer.startLog()",Constants.LOG_SEVERITY_INFO, true, true);
       }
       
   }

   public static boolean logServerState(){
       return dataLogRun;
   }
   public static void logPacket(DataLogObject L){
       qPacketLog.add(L);
       
   }
   public static void logEntry(DataLogObject L){
       qServerLog.add(L);
   }

   /**
    * Creates a New Log entry in the server log file
    * @param sLogEntry - Log Entry Text
    * @param sSource - Where the Log entry was generated i/e method name or class name
    * @param iLogSeverity - How important is this? See Constants.LOG_...
    * @param bLogToConsole - Send the Log information to screen?
    * @param bLogToFile - Send the Log information to the Server Log File?
    */
   protected static void logEntry(String sLogEntry, String sSource,int iLogSeverity, boolean bLogToConsole, boolean bLogToFile){

       if(bLogToConsole){
           System.out.println("Log : " + now() + " : " + sSource + " : " + sLogEntry + " : SEV: " + Constants.LOG_SEVERITY_STRINGS[iLogSeverity]);
           System.out.flush();

       }
       if(bLogToFile){
           if(qServerLog!=null)
           {
               DataLogObject L = new DataLogObject(sSource,sLogEntry,iLogSeverity);
               L.setLogType((byte)1);
               qServerLog.add(L);
               //System.out.println("Log Server Queue Count " + qServerLog.size());
           }
       }
       if(iLogSeverity >= Constants.LOG_SEVERITY_MINOR && !bLogToConsole)
       {
           System.err.println("Log : " + now() + " : " + sSource + " : " + sLogEntry + " : SEV: " + Constants.LOG_SEVERITY_STRINGS[iLogSeverity]);
       }
   }

   /**
    * Logs an Exception to the Server Log File.
    * @param sLogEntry - Actual Entry Text
    * @param sSource - Where the Exception Occured
    * @param bLogToConsole - Send Exception Info to Console?
    * @param bLogToFile - Send Exception to File?
    * @param e - Exception Thrown
    */
   protected static void logException(String sLogEntry, String sSource, boolean bLogToConsole, boolean bLogToFile,Exception e){
       if(bLogToConsole){
           System.out.println("Exception Log : " + now() + " : " + sSource + " : " + sLogEntry + " : SEV: " + Constants.LOG_SEVERITY_STRINGS[Constants.LOG_SEVERITY_CRITICAL]);
           System.out.println("----------------STACK TRACE FOLLOWS----------------");
           StackTraceElement[] elements = e.getStackTrace();
           for (int i = 0; i < elements.length; i++)
           {
                System.out.println("STE: " + i + " | " + elements[i].toString());
           }
           System.out.println("------------------STACK TRACE END------------------");
           System.out.flush();
           e.printStackTrace();
       }
       if(bLogToFile){
           if(qServerLog!=null)
           {
               DataLogObject L = new DataLogObject(sSource,sLogEntry,Constants.LOG_SEVERITY_CRITICAL,e);
               L.setLogType((byte)2);
               qServerLog.add(L);
               //System.out.println("Log Server Queue Count " + qServerLog.size());
           }
       }

   }
  protected static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

  public static String now() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());

  }

  protected static boolean logWritePending(){
	  return ((!qPacketLog.isEmpty()) ||  (!qServerLog.isEmpty()));
  }

    public String getSHostName() {
        if(sHostName == null || sHostName.isEmpty())
        {
            return "NullHostName";
        }
        return sHostName;
    }

    public static void setSHostName(String sHost) {
        sHostName = sHost;
    }

    public static int getZPort() {
        return zPort;
    }

    public static void setZPort(int Port) {
        zPort = Port;
    }

    public static long getLLoopYieldTime() {
        return lLoopYieldTime;
    }

    public static void setLLoopYieldTime(long loopYieldTime) {
        lLoopYieldTime = loopYieldTime;
    }



}

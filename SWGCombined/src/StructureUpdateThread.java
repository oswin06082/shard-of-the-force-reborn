
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;



public class StructureUpdateThread implements Runnable {

        //ConcurrentHashMap<Long, Structure> vPlayerStructures;
        ZoneServer server;
        
	private Thread myThread;
	private boolean bFirstLoop = true;
	public StructureUpdateThread(ZoneServer server/*, ConcurrentHashMap<Long, Structure> vPlayerStructures*/) {
		this.server = server;
                //this.vPlayerStructures = vPlayerStructures;
		initialize();
	}
	
	
	public void run() {
                DataLog.logEntry("Structure Update thread Started","StructureUpdateThread",Constants.LOG_SEVERITY_INFO,true,true);
		// TODO Auto-generated method stub
		long lLastUpdateTimeMS; // The last time we checked the time.
		long lCurrentUpdateTimeMS; // The current time.
		long lDeltaUpdateTimeMS; // The difference between the last time and the current time

		lLastUpdateTimeMS = System.currentTimeMillis();
		while (myThread != null) {
			try {
				synchronized(this) {
					Thread.yield(); //"Hints" that other threads can go ahead and do stuff.
					wait(5000);
				}
				
				lCurrentUpdateTimeMS = System.currentTimeMillis();
				lDeltaUpdateTimeMS = lCurrentUpdateTimeMS - lLastUpdateTimeMS;
				update(lDeltaUpdateTimeMS);
				lLastUpdateTimeMS = lCurrentUpdateTimeMS;
			} catch (Exception e) {
				// Ruh roh.
			}
		}
                DataLog.logEntry("Structure Update thread Terminated","StructureUpdateThread",Constants.LOG_SEVERITY_INFO,true,true);
	}

	private void initialize() {
		myThread = new Thread(this);
		myThread.setName("Structure update thread");
		myThread.start();
	}
	
	private void update(long lDeltaUpdateTimeMS) {
		// Write the code that updates the structures here.
        ConcurrentHashMap<Long, Structure> vStructures = server.getAllPlayerStructures();
        synchronized(vStructures) {
        	Enumeration <Structure> sEnum = vStructures.elements();
            while(sEnum.hasMoreElements())
            {
                Structure s = sEnum.nextElement();
                //s.setBUpdateThreadIsUsing(true);
                synchronized(s) {
                	if (bFirstLoop) {
                		s.fixTerminalPointers();
                	}
                	s.update(lDeltaUpdateTimeMS,server);
                }
                //s.setBUpdateThreadIsUsing(false);
            }
        }
        bFirstLoop = false;
	}
        
}

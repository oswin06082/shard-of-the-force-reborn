

import java.io.IOException;

/**
 * @author Adam Schmelzle
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TrigTanUtils {

	private static boolean bInitialized = false;
	public static String ALLTANLOOKUPFILE = "/newtan.dat";
	//public static String TANLOOKUPFILE = "/tank.dat";
	//public static String ATANLOOKUPFILE = "/atank.dat";
	
	public static boolean isInitialized(){
		return bInitialized;
	}
	
	
	public static void initialize() throws IOException{
		//int ii = 2 / 0;
		
		//read all the data from dat files...
		if(bInitialized){
			return;
		}

            //Class c = this.getClass();
            
		
		/*InputStream is = Startup.currentMidlet.getClass().getResourceAsStream(ALLTANLOOKUPFILE);
            DataInputStream dataStream = new DataInputStream(is);
            int length = dataStream.readInt();
            tanLookupK = new int[361];
            int[] newTanLookupK = new int[46];
            for(int i=0; i < length; i++){
            	newTanLookupK[i] = dataStream.readInt();
            }
            dataStream.close();
          */
		
		//TANGENT
		int[] newTanLookupK = new int[46];
		double dRadians;
		double dTan;
		double dTanK;
		int iTanK;
		//int iTan;
		//System.out.println("atanSize="+atanLookupK.length);
		for(int iDegrees = 0; iDegrees < 46; iDegrees++){
			dRadians = Math.toRadians(iDegrees);
			dTan = Math.tan(dRadians);
			//System.out.println("sin("+iDegrees+") = "+dSin);
			dTanK = (dTan * 1024) + 0.5;
			iTanK = (int)dTanK;
			newTanLookupK[iDegrees] = iTanK;
			//System.out.println("tan("+iDegrees+") = "+iTanK);
		}
			tanLookupK = new int[361];
            //now reconstruct the tanLookupK array...
            //0 -> 45 degrees is the same as the data from file
            for(int index = 0; index <= 45; index++){
            	tanLookupK[index] = newTanLookupK[index];
            }
            
            //46 -> 90 degrees is the inverse of the data from file
            for(int index = 46; index <= 89; index++){
            	tanLookupK[index] = PacketUtils.KK / tanLookupK[90 - index];
            }
            
            //91 -> 180 degrees is -1 * (89->0)
            for(int index = 90; index <= 180; index++){
            	tanLookupK[index] = -1 * tanLookupK[180 - index];
            }
            
            //181 -> 360 degrees is (01->180)
            for(int index = 181; index <= 360; index++){
            	tanLookupK[index] = tanLookupK[index-180];
            }
            
            
            //ATAN here
            //1024 iterations that go up to 45degrees
            atanLookupK= new int[1025];
            int iLastTanMatchIndex = 0;
            int iLastTanMatchValueK;
            int iLowerNextValK;
            int iHigherNextValK;
            
            atanLookupK[0] = 0;
            int iLastDiff;
            int iNextLowDiff;
            int iNextHighDiff;
            for(int iATanIndex = 1; (iATanIndex < 1025); iATanIndex++){
            	iLastTanMatchValueK = tanLookupK[iLastTanMatchIndex];
            	if(iLastTanMatchIndex < 45){
            		iLowerNextValK = tanLookupK[iLastTanMatchIndex + 1];
            		if(iLastTanMatchIndex < 44){
            			iHigherNextValK = tanLookupK[iLastTanMatchIndex + 2];
            		}else{
            			iHigherNextValK = 99999;
            		}
            	}else{
        			iHigherNextValK = 99999;
        			iLowerNextValK = 99999;
            		
            	}
            	
            	iLastDiff = Math.abs(iLastTanMatchValueK - iATanIndex);
            	iNextLowDiff = Math.abs(iLowerNextValK - iATanIndex);
            	iNextHighDiff = Math.abs(iHigherNextValK - iATanIndex);
            	
            	if(iLastDiff < iNextLowDiff){
            		if(iLastDiff < iNextHighDiff){
            			//last closest
            			//don't change anything
            		}else{
            			//next high is the closest
            			iLastTanMatchIndex = iLastTanMatchIndex + 2;
            		}
            	}else if(iNextLowDiff < iNextHighDiff){
            		//next low is closest	
        			iLastTanMatchIndex = iLastTanMatchIndex + 1;
            		
            	}else{
            		//next high is closest
        			iLastTanMatchIndex = iLastTanMatchIndex + 2;
            	}
            	
            	atanLookupK[iATanIndex] = PacketUtils.toK(iLastTanMatchIndex);
            		
            }
		
		bInitialized = true;
	}
	
	
	protected static int[] tanLookupK;
	protected static int[] atanLookupK; 

	/*
	 * private static int getTableIndexRadK(int fRadiansK) { // just in case
	 * fRadians is beyond 2pi, or negative fRadiansK = (int) (fRadiansK % (2 *
	 * PIK)); if (fRadiansK <= 0) { fRadiansK = (int) ((PIK * 2) + fRadiansK); }
	 * 
	 * fRadiansK = (int) (fRadiansK / (PIK * 2)); return ((int) ((fRadiansK *
	 * LOOKUP_ENTRIES) / 1000)) % LOOKUP_ENTRIES; }
	 * 
	 * public static int tanKRadK(int fRadiansK) { return
	 * tanLookupK[getTableIndexRadK(fRadiansK)]; }
	 */

	private static int getTableIndexDeg(int degrees) {
		int result = degrees % 360;
		if (result < 0)
			result += 360; // take care of negatives

		// if (result == 360)
		// result = 0;
		return result;
	}

	private static int getTableIndexFractionK(int fractionK) {
		return fractionK;
	}

	public static int tanKDeg(int degrees) {
		return tanLookupK[getTableIndexDeg(degrees)];
	}

	public static int tanKDegK(int degreesK) {
		return tanLookupK[getTableIndexDeg(PacketUtils.unKRound(degreesK))];
	}

	public final static int NINETYK = 92160;

	public static int atanFractionK(int fractionK) {
		if(fractionK < 0){
			return PacketUtils.unK(atanKFractionK(fractionK)-512);
		}else if(fractionK == 0){
			return PacketUtils.unK(atanKFractionK(fractionK));
		}else{
			return PacketUtils.unKRound(atanKFractionK(fractionK));
		}
	}

	public static int atanKFractionK(int fractionK) {
		//try{
		if (fractionK < 0) {
			fractionK = Math.abs(fractionK);
			if (fractionK > PacketUtils.K) {
				fractionK = PacketUtils.KK / fractionK;
				return -1
						* (NINETYK - atanLookupK[getTableIndexFractionK(fractionK)]);
			}
			return -1 * atanLookupK[getTableIndexFractionK(fractionK)];
			
		} 
		if (fractionK > PacketUtils.K) {
			fractionK = PacketUtils.KK / fractionK;
			return (NINETYK - atanLookupK[getTableIndexFractionK(fractionK)]);
		}
		return atanLookupK[getTableIndexFractionK(fractionK)];
		
	}
	
}



import java.io.IOException;

public class TrigSinAndCosUtils {

	/**
	 * @author Adam Schmelzle
	 * 
	 * TODO To change the template for this generated type comment go to Window -
	 * Preferences - Java - Code Style - Code Templates
	 */
	
	//public class TrigSinUtils {

		// for 360 lookup entries
		// protected final static int SIN_LOOKUP_ENTRIES = 361;

		// protected final static int ASIN_LOOKUP_ENTRIES = 1025;

		// protected final static int LOOKUP_ENTRIESK = 360000;
		// protected static int PIK = 3217; // Pi * 1024

		//private static boolean bInitialized = false;
		public static String SINLOOKUPFILE = "/newsin.dat";
		//public static String ASINLOOKUPFILE = "/asink.dat";
		
		/*
		public static boolean isInitialized(){
			return bInitialized;
		}
		
		public static void initialize() throws IOException{
			//read all the data from dat files...
			if(bInitialized){
				return;
			}

	            //Class c = this.getClass();
	            InputStream is = Startup.currentMidlet.getClass().getResourceAsStream(SINLOOKUPFILE);
	            DataInputStream dataStream = new DataInputStream(is);
	            int length = dataStream.readInt();
	            sinLookupK = new int[length];
	            for(int i=0; i < length; i++){
	            	sinLookupK[i] = dataStream.readInt();
	            }
	            dataStream.close();
	            
	            is = Startup.currentMidlet.getClass().getResourceAsStream(ASINLOOKUPFILE);
	            dataStream = new DataInputStream(is);
	            length = dataStream.readInt();
	            asinLookupK = new int[length];
	            for(int i=0; i < length; i++){
	            	asinLookupK[i] = dataStream.readInt();
	            }
	            dataStream.close();
	            
	            
	            //is.close();
			
			bInitialized = true;
		}
		*/
		
		protected static int[] sinLookupK;
		protected static int[] asinLookupK; 

		
		public static int sinKDeg(int degrees) {
			return sinLookupK[getTableIndexDeg(degrees)];
		}

		public static int sinKDegK(int degreesK) {
			// return sinLookupK[getTableIndexDeg(degreesK / 1000)];
			return sinLookupK[getTableIndexDeg(PacketUtils.unKRound(degreesK))];
		}

		public static int asinKFractionK(int fractionK) {
			return asinLookupK[getTableIndexFractionK(fractionK)];
		}

		public static int asinFractionK(int fractionK) {
			return PacketUtils.unKRound(asinLookupK[getTableIndexFractionK(fractionK)]);
		}

		private static boolean bInitialized = false;
		
		public static boolean isInitialized(){
			return bInitialized;
		}
		
		public static void initialize() throws IOException{
			//read all the data from dat files...
			if(bInitialized){
				return;
			}
			//SIN LOOKUP
			
			
            /*InputStream is = Startup.currentMidlet.getClass().getResourceAsStream(SINLOOKUPFILE);
            DataInputStream dataStream = new DataInputStream(is);
            int length = dataStream.readInt();
            
            sinLookupK = new int[361];
            int[] newSinLookupK = new int[91];
            for(int i=0; i < length; i++){
            	newSinLookupK[i] = dataStream.readInt();
            }
            */
			
			int[] newSinLookupK = new int[91];
			double dRadians;
			double dSin;
			double dSinK;
			int iSinK;
			for(int iDegrees = 0; iDegrees < 91; iDegrees++){
				dRadians = Math.toRadians(iDegrees);
				dSin = Math.sin(dRadians);
				
				dSinK = (dSin * 1024) + 0.5;
				iSinK = (int)dSinK;
				newSinLookupK[iDegrees] = iSinK;
			}
			sinLookupK = new int[361];
            //now reconstruct the sinLookupK array...
            //0 -> 90 degrees is the same
            for(int index = 0; index <= 90; index++){
            	sinLookupK[index] = newSinLookupK[index];
            }

            //sin 90->180 = 90->0
            for(int index = 91; index <= 180; index++){
            	sinLookupK[index] = newSinLookupK[180 - index];
            }

            //sin 181->270 = (-1 * (0 -> 180))
            for(int index = 181; index <= 360; index++){
            	sinLookupK[index] = -1 * sinLookupK[index - 180];
            }

            //now we have the sin for 0->360 degrees,
            //reconstruct the aSin lookup from this info...
            asinLookupK = new int[1025];
            int iLastSinMatchIndex = 0;
            int iLastSinMatchValueK;
            int iLowerNextValK;
            int iHigherNextValK;
            
            asinLookupK[0] = 0;
            int iLastDiff;
            int iNextLowDiff;
            int iNextHighDiff;
            for(int iASinIndex = 1; (iASinIndex < 1025); iASinIndex++){
            	iLastSinMatchValueK = sinLookupK[iLastSinMatchIndex];
            	if(iLastSinMatchIndex < 360){
            		iLowerNextValK = sinLookupK[iLastSinMatchIndex + 1];
            		if(iLastSinMatchIndex < 359){
            			iHigherNextValK = sinLookupK[iLastSinMatchIndex + 2];
            		}else{
            			iHigherNextValK = 99999;
            		}
            	}else{
        			iHigherNextValK = 99999;
        			iLowerNextValK = 99999;
            		
            	}
            	
            	iLastDiff = Math.abs(iLastSinMatchValueK - iASinIndex);
            	iNextLowDiff = Math.abs(iLowerNextValK - iASinIndex);
            	iNextHighDiff = Math.abs(iHigherNextValK - iASinIndex);
            	
            	if(iLastDiff < iNextLowDiff){
            		if(iLastDiff < iNextHighDiff){
            			//last closest
            			//don't change anything
            		}else{
            			//next high is the closest
            			iLastSinMatchIndex = iLastSinMatchIndex + 2;
            		}
            	}else if(iNextLowDiff < iNextHighDiff){
            		//next low is closest	
        			iLastSinMatchIndex = iLastSinMatchIndex + 1;
            		
            	}else{
            		//next high is closest
        			iLastSinMatchIndex = iLastSinMatchIndex + 2;
            	}
            	
            	asinLookupK[iASinIndex] = PacketUtils.toK(iLastSinMatchIndex);
            		
            }

			bInitialized = true;
			
		}
		
		/*
		 * private static int getTableIndexRadK(int fRadiansK) { // just in case
		 * fRadians is beyond 2pi, or negative fRadiansK = (int) (fRadiansK % (2 *
		 * PIK)); if (fRadiansK <= 0) { fRadiansK = (int) ((PIK * 2) + fRadiansK); }
		 * 
		 * fRadiansK = (int) (fRadiansK / (PIK * 2)); return ((int) ((fRadiansK *
		 * LOOKUP_ENTRIES) / 1000)) % LOOKUP_ENTRIES; }
		 * 
		 * public static int cosKRadK(int fRadiansK) { return
		 * cosLookupK[getTableIndexRadK(fRadiansK)]; }
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

		public static int cosKDeg(int degrees) {
			//return sinKDeg(degrees + 90);
			return sinLookupK[getTableIndexDeg(degrees + 90)];
		}

		public final static int ninetyK = PacketUtils.toK(90);
		public final static int oneEightyK = PacketUtils.toK(180);
		public static int cosKDegK(int degreesK) {
			//return sinKDegK(degreesK + ninetyK);
			return sinLookupK[getTableIndexDeg(PacketUtils.unKRound(degreesK + ninetyK))];
		}

		// acos
		public static int acosKFractionK(int fractionK) {
			
			if(fractionK < 0){
				return oneEightyK -(ninetyK - asinLookupK[getTableIndexFractionK(Math.abs(fractionK))]);
				
			}
			return (ninetyK - asinLookupK[getTableIndexFractionK(fractionK)]);
			
		}

		public static int acosFractionK(int fractionK) {
			return PacketUtils.unKRound(ninetyK - asinLookupK[getTableIndexFractionK(fractionK)]);
		}

}

import java.io.IOException;


/**
 * @author Adam Schmelzle
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TrigAngleUtils {

	//for 360 lookup entries
	//protected final static int LOOKUP_ENTRIES = 360;
	//protected final static int LOOKUP_ENTRIESK = 360000;
	//protected static int PIK = 3142; // 3.1415926 * 1000
	public static int NINETYK = PacketUtils.toK(90);
	public static int ONEEIGHTYK = PacketUtils.toK(180);
	public static int TWOSEVENTYK = PacketUtils.toK(270);
	public static int THREESIXTYK = PacketUtils.toK(360);

	public static int NINETYK_NEG = PacketUtils.toK(-90);
	public static int ONEEIGHTYK_NEG = PacketUtils.toK(-180);
	public static int TWOSEVENTYK_NEG = PacketUtils.toK(-270);
	public static int THREESIXTYK_NEG = PacketUtils.toK(-360);
	
	//protected static int[] sqrt0to1000_K;
	protected static boolean bInitialized = false;
	public static boolean isInitialized(){
		return bInitialized;
	}
	
	public final static String SQRTLOOKUPFILE = "/sqrt.dat";	
	public final static String NEWSQRTLOOKUPFILE = "/newsqrt.dat";	
	public static void initialize() throws IOException{
		//read all the data from dat files...
		if(bInitialized){
			return;
		}

		
            //NEW sqrt stuff

		/*
		InputStream is = Startup.currentMidlet.getClass().getResourceAsStream(NEWSQRTLOOKUPFILE);
            DataInputStream dataStream = new DataInputStream(is);
            int length = dataStream.readInt();
            newSqrt_0to1000KK = new long[length];
            
            for(int i=0; i < length; i++){
            	newSqrt_0to1000KK[i] = dataStream.readLong();
            }
            
            dataStream.close();
            dataStream = null;
            is = null;
            */
		// Initialize newSqrt_0to1000KK here.
		//new Sqrt!
		newSqrt_0to1000KK = new long[1001];
		double dSqrt;
		long sqrtKK;
		newSqrt_0to1000KK[0] = 0;
		for(int i=1; i < 1001; i++){
			dSqrt = Math.pow(i, 0.5);
			sqrtKK = (long)(dSqrt * 1024 * 1024);
			newSqrt_0to1000KK[i] = sqrtKK;
		}

		bInitialized = true;
	}
	
	private static long[] newSqrt_0to1000KK;
	
	//public static int getAngleFromSlopeK(int dXK, int dYK) {
	//	//return Game.unK(getAngleKFromSlopeK(dXK, dYK));
	//	//use the tan?
	//	return TrigTanUtils.atanFractionK( ((dYK * Game.K) / dXK));
	//}

	public static int getAngleKFromSlopeK(int dXK, int dYK) {

		//use the tan?
		//return TrigTanUtils.atanKFractionK( ((dYK * Game.K) / dXK));
		
			
			if (dXK == 0) {
				if (dYK == 0)
					return 0;
				else if (dYK > 0) {
					return NINETYK;
				} else {
					return TWOSEVENTYK;
				}
			} else if (dYK == 0) {
				if (dXK > 0) {
					return 0;
				}
				return ONEEIGHTYK;
			}
	
			int slopeK = (Math.abs(dYK) * PacketUtils.K) / Math.abs(dXK);
			int modifier = 0;
			int multiplier = 1;
	
			if (dXK < 0) {
				//left half
				if (dYK < 0) {
					//lower left quad
					//reflect along y=x
					modifier = ONEEIGHTYK;
					multiplier = 1;
				} else {
					//upper left quad
					//reflect about y axis
					modifier = -ONEEIGHTYK;
					multiplier = -1;
				}
			} else {
				//right half
				if (dYK < 0) {
					//lower right quad
					//reflect along x axis
					modifier = 0;
					multiplier = -1;
				} else {
					//upper right quad
					//leave it alone
					modifier = 0;
					multiplier = 1;
				}
			}
			int angleK = 0;
			angleK = TrigTanUtils.atanKFractionK(slopeK);
			angleK = (angleK + modifier) * multiplier;
			angleK = (angleK % THREESIXTYK);
			if (angleK < 0)
				angleK += THREESIXTYK;
			if (angleK == THREESIXTYK)
				angleK = 0;
			return angleK;
	}

	public static int getDistanceSQR(int x1, int x2, int y1, int y2) {
		return getDistanceSQR(x2 - x1, y2 - y1);
	}

	public static int getDistanceSQR(int dX, int dY) {
		return (dX) * (dX) + (dY) * (dY);
	}

	public static int getDistance(int x1, int y1, int x2, int y2) {
		return getDistance(x2 - x1, y2 - y1);
	}

	public static int getDistance(int dX, int dY) {
		return (int)getSqrt((dX * dX) + (dY * dY));
	}

	public static long getDistanceL(long x1, long y1, long x2, long y2) {
		return getDistanceL(x2 - x1, y2 - y1);
	}

	public static long getDistanceL(long dX, long dY) {
		return getSqrt((dX * dX) + (dY * dY));
	}

	
	public static long getSqrt(long l){
		return PacketUtils.unKK(getSqrtKK(l));
	}
	
	public static long getSqrtKK(long l){
		if(l > 1000000000){
			//as long as l > 1billion, divide it by KK, and try again
			return getSqrtKK( (l / PacketUtils.KK) ) * PacketUtils.K;
		}
		return getSqrtKK( (int)l );
		
	}
	
	public static long getSqrtKK(int i){
		//try{
		if (i >= 0) {
			if (i <= 1000) {
				return newSqrt_0to1000KK[ i ];
			} else if (i <= 100000) {
				return newSqrt_0to1000KK[ i / 100] * 10;
			} else if (i <= 10000000) {
				return newSqrt_0to1000KK[ i / 10000] * 100;
			} else if (i <= 1000000000) {
				return newSqrt_0to1000KK[ i / 1000000] * 1000;
			} else {
				return 33000;
			}
		}
		return 0;
	}
	
	
	
	/**
	 * Determines the difference between the angles. The
	 * returned value is always between +/- Game.toK(180) degrees
	 * 
	 * @param angle1
	 * @param angle2
	 * @return
	 */
	public static int getMinAngularDiffKDegK(int angle1K, int angle2K){
		int angleDiffK = (angle1K - angle2K);
		return getMinAngleK(angleDiffK);
	}

	public static int getMinAngleK(int angleK){
		angleK = (angleK % THREESIXTYK);
		if( (angleK != 0) ){ //&& (angleDiffK != 180000) ){
			if(angleK > ONEEIGHTYK){
				angleK = (angleK - THREESIXTYK);
			}else if(angleK < ONEEIGHTYK_NEG){
				angleK = (angleK + THREESIXTYK);
			}
		}
		return angleK;

	}
	
	public static int getMinPositiveAngleK(int angleK){
		angleK = (angleK % THREESIXTYK);
		if(angleK < 0){
			angleK += THREESIXTYK;
		}
		return angleK;
	}
	
	public static int getMinPositiveAngularDiffKDegK(int angle1K, int angle2K){
		int angleDiffK = (angle1K - angle2K);
		return getMinPositiveAngleK(angleDiffK);
	}
	
	public static int getMinAngularDistanceK(
			int angleK,
			int pointXK, int pointYK,
			int x1, int y1, int x2, int y2){
		
		//only need to check 2 corners at most.
		//if the point is inside the rect, we return 0
		int x1K = PacketUtils.toK(x1);
		int x2K = PacketUtils.toK(x2);
		int y1K = PacketUtils.toK(y1);
		int y2K = PacketUtils.toK(y2);

		
		int rectAngleK1 = 0;
		int rectAngleK2= 0;
		if(pointXK < x1K){
			//left of rect
			if(pointYK < y1K){
				//left-above rect
				//use points (x2,y1) , (x1,y2)
				rectAngleK1 = getAngleKFromSlopeK(
						(x2K-pointXK),
						(y1K-pointYK));
				rectAngleK2 = getAngleKFromSlopeK(
						(x1K-pointXK),
						(y2K-pointYK));
			}else if(pointYK > y2K){
				//below rect
				//use points (x1,y1) , (x2,y2)
				rectAngleK1 = getAngleKFromSlopeK(
						(x1K-pointXK),
						(y1K-pointYK));
				rectAngleK2 = getAngleKFromSlopeK(
						(x2K-pointXK),
						(y2K-pointYK));
			}else{
				//beside rect
				//use points (x1,y1) , (x1,y2)
				rectAngleK1 = getAngleKFromSlopeK(
						(x1K-pointXK),
						(y1K-pointYK));
				rectAngleK2 = getAngleKFromSlopeK(
						(x1K-pointXK),
						(y2K-pointYK));
			}
			
		}else if(pointXK > x2K){
			//right of rect
			//left of rect
			if(pointYK < y1K){
				//above rect
				//use points (x1,y1) , (x2,y2)
				rectAngleK1 = getAngleKFromSlopeK(
						(x1K-pointXK),
						(y1K-pointYK));
				rectAngleK2 = getAngleKFromSlopeK(
						(x2K-pointXK),
						(y2K-pointYK));
			}else if(pointYK > y2K){
				//below rect
				//use points (x2,y1) , (x1,y2)
				rectAngleK1 = getAngleKFromSlopeK(
						(x2K-pointXK),
						(y1K-pointYK));
				rectAngleK2 = getAngleKFromSlopeK(
						(x1K-pointXK),
						(y2K-pointYK));
			}else{
				//beside rect
				//use points (x2,y1) , (x2,y2)
				rectAngleK1 = getAngleKFromSlopeK(
						(x2K-pointXK),
						(y1K-pointYK));
				rectAngleK2 = getAngleKFromSlopeK(
						(x2K-pointXK),
						(y2K-pointYK));
			}

		}else{
			//directly above/below rect
			if(pointYK < y1K){
				//directly above
				//use points (x1,y1) , (x2,y1)
				rectAngleK1 = getAngleKFromSlopeK(
						(x1K-pointXK),
						(y1K-pointYK));
				rectAngleK2 = getAngleKFromSlopeK(
						(x2K-pointXK),
						(y1K-pointYK));
			}else if(pointYK > y2K){
				//directly below
				//use points (x1,y2) , (x2,y2)
				rectAngleK1 = getAngleKFromSlopeK(
						(x1K-pointXK),
						(y2K-pointYK));
				rectAngleK2 = getAngleKFromSlopeK(
						(x2K-pointXK),
						(y2K-pointYK));
			}else{
				//inside the damn thing
				return 0;
			}
		}
		
		
		//min diff will be...
		int diff1K = getMinAngleK(rectAngleK1 - angleK);
		int diff2K = getMinAngleK(rectAngleK2 - angleK);
		//if we are in-between the two angles,
		//return 0
		//we are in-between, if the sum of the differences
		//are <= the difference betwee the 2 angles
		int diffSumK = Math.abs(diff1K) + Math.abs(diff2K);
		int rectAnglesDiffK = Math.abs(getMinAngularDiffKDegK(rectAngleK1, rectAngleK2));
		//added some leniency
		if(diffSumK <= (rectAnglesDiffK+1000)){
			//inside
			return 0;
		}
		
		//not inside, return the smaller distance
		if(Math.abs(diff1K) < Math.abs(diff2K)){
			return diff1K;
		}
		return diff2K;
		
	}
	
	public static int getMinAngularDistanceK(int angleK,
			int pointXK, int pointYK,
			int circleX, int circleY,
			int circleRadius){
	
		int pointX = PacketUtils.unK(pointXK);
		int pointY = PacketUtils.unK(pointYK);
		
		//angular distance should be relatively easy...
		int pointDistance = getDistance( (circleX-pointX), (circleY-pointY));
		//if the point is inside the circle,
		//simply return 0, since there can be no angle
		if(pointDistance <= circleRadius){
			return 0;
		}
		
		int ratioK = (int)(PacketUtils.toKK(circleRadius) / PacketUtils.toK(pointDistance));
		int angleDiffKFromCenter = TrigSinAndCosUtils.asinKFractionK(ratioK);
		int angleKToCenter = getAngleKFromSlopeK(
				PacketUtils.toK(circleX-pointX),
				PacketUtils.toK(circleY-pointY));
		
		//now get the smaller of the two differences
		int positiveK = getMinAngleK(angleKToCenter + angleDiffKFromCenter);
		int negativeK = getMinAngleK(angleKToCenter - angleDiffKFromCenter);

		int diff1K = getMinAngleK(negativeK - angleK);
		int diff2K = getMinAngleK(positiveK - angleK);

		//if we are in-between the two angles,
		//return 0
		//we are in-between, if the sum of the differences
		//are <= the difference betwee the 2 angles
		int diffSumK = Math.abs(diff1K) + Math.abs(diff2K);
		int newAnglesDiffK = Math.abs(getMinAngularDiffKDegK(positiveK, negativeK));
		//added some leniency
		if(diffSumK <= (newAnglesDiffK+1000)){
			//inside
			return 0;
		}
		
		//not inside, return the smaller distance
		if(Math.abs(diff1K) < Math.abs(diff2K)){
			return diff1K;
		}
		return diff2K;
	}
	
}

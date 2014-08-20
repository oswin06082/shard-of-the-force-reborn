import java.awt.Color;
import java.util.Hashtable;




public class ColorManager {
	
	private static Hashtable<Long, Color> vColorMap;
	private static int iGradientSize;
	private static boolean bInitialize = false;
	/** 
	 * Constructs a new ColorManager for getting colors for drawing.
	 * @param iGradientSize -- The "space" between the colors.  Note:  The smaller the gradient size, the more colors stored in the ColorManager. 
	 */
	public static void initialize (int gradient) {
		if (bInitialize == true) {
			return;
		}
		
		iGradientSize = gradient;
		int iNumColors = (iGradientSize * iGradientSize * iGradientSize * 2);
		vColorMap = new Hashtable<Long, Color>(iNumColors);
		Color color = null;
		long colorKey = 0;
		int iAlpha = 0;
		for (int r = 0; r <= 255; r+= iGradientSize) {
			//System.out.println("New color: r["+r+"]");
			for (int g = 0; g <= 255; g+= iGradientSize) {
				for (int b = 0; b <= 255; b+= iGradientSize) {
					iAlpha = 0;
					colorKey = (r << 24) | (g << 16) | (b << 8) | iAlpha;
					color = new Color((int)colorKey);
					vColorMap.put(colorKey, color);
					
					iAlpha = 255;
					colorKey = (r << 24) | (g << 16) | (b << 8) | iAlpha;
					color = new Color((int)colorKey);
					vColorMap.put(colorKey, color);
				}
			}
		}
		vColorMap.put(0l, Color.BLACK);
		vColorMap.put(0x00000000FFFFFF00l, Color.WHITE);
		bInitialize = true;
	}
	
	
    public static Color getColor(int r, int g, int b, int a) {
        long color = ((r << 24) | (g << 16) | (b << 8) | a);
        return getColor(color);
    }

    public static Color getColor(int r, int g, int b) {
        return getColor(r, g, b, 255);
    }

    public static Color getColor(long color) {
    	if (!bInitialize) {
    		initialize(8);
    	}
    	//System.out.println("getColor("+Long.toHexString(color));
        Color c; // = colorsHash[color];
        //if (c == null) {
            //find closest color by steps...
            long r = ((color >> 24) & 0xff);
            long g = ((color >> 16) & 0xff);
            long b = ((color >> 8)  & 0xff);
            long a = ((color >> 0) & 0xff);

            a = (a / 255) * 255;
            r = (r / iGradientSize) * iGradientSize;
            g = (g / iGradientSize) * iGradientSize;
            b = (b / iGradientSize) * iGradientSize;
            
            color = (r << 24) | (g << 16) | (b << 8) | a;
           // System.out.println("Nearest match: 0x" + Long.toHexString(color));
            c = vColorMap.get(color);
        //}
        return c;
    }
	
}

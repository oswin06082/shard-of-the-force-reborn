import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * The Packet Utils class provides lower-level mathematical functions for manipulating packets and primitive values.
 * @author Darryl
 *
 */
public class PacketUtils {

	private final static int MAX_PACKET_SIZE = 496;
	
	/**
	 * Bitshifts the passed int left 10 bits.  This has the same effect as multiplying the int by 1024.  Returns the result of the bitshifting.
	 * @param i -- The int passed in.
	 * @return The result of the bitshifting.
	 */
	protected static int toK(int i) {
		return i << 10;
	}

	/**
	 * Bitshifts the passed long left 10 bits.  This has the same effect as multiplying the long by 1024.  Returns the result of the bitshifting.
	 * @param l -- The long passed in.
	 * @return The result of the bitshifting.
	 */
	protected static long toK(long l) {
		return l << 10;
	}

	/**
	 * Bitshifts the passed long left 20 bits.  This has the same effect as multiplying the long by 1048576.  Returns the result of the bitshifting.
	 * @param l -- The long passed in.
	 * @return The result of the bitshifting.
	 */
	protected static long toKK(long l) {
		return l << 20;
	}

	/**
	 * Bitshifts the passed int right 10 bits.  This has the same effect as dividing the int by 1024.  Returns the result of the bitshifting.
	 * @param iK -- The int passed in.
	 * @return The result of the bitshifting.
	 */
	protected static int unK(int iK) {
		return iK >> 10;
	}

	/**
	 * Rounds the passed int up, then bitshifts right 10 bits.  This has the same effect as dividing the int by 1024 and adding 1.  Returns the result of the bitshifting.
	 * @param iK -- The int passed in.
	 * @return The result of the bitshifting.
	 */
	protected static int unKRound(int iK) {
		iK += 512;
		return iK >> 10;
	}

	/**
	 * Rounds the passed long up, then bitshifts right 10 bits.  This has the same effect as dividing the long by 1024 and adding 1.  Returns the result of the bitshifting.
	 * @param lK -- The long passed in.
	 * @return The result of the bitshifting.
	 */
	protected static long unKRound(long lK) {
		lK += 512;
		return lK >> 10;
	}

	/**
	 * Bitshifts the long right 10 bits.  This has the same effect as dividing the long by 1024.  Returns the result of the bitshifting.
	 * @param lK -- The long passed in.
	 * @return The result of the bitshifting.
	 */
	protected static long unK(long lK) {
		return lK >> 10;
	}

	/**
	 * Bitshifts the int right 20 bits.  This has the same effect as dividing the int by 1048576.  Returns the result of the bitshifting.
	 * @param iK -- The long passed in.
	 * @return The result of the bitshifting.
	 */
	protected static int unKK(int iK) {
		return iK >> 20;
	}

	/**
	 * Bitshifts the long right 20 bits.  This has the same effect as dividing the long by 1048576.  Returns the result of the bitshifting.
	 * @param lK -- The long passed in.
	 * @return The result of the bitshifting.
	 */
	protected static long unKK(long lK) {
		return lK >> 20;
	}

	
	private final static int g_nCrcTable[] =
	{
	    0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f,
	    0xe963a535, 0x9e6495a3, 0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988,
	    0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91, 0x1db71064, 0x6ab020f2,
	    0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
	    0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9,
	    0xfa0f3d63, 0x8d080df5, 0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172,
	    0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b, 0x35b5a8fa, 0x42b2986c,
	    0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
	    0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423,
	    0xcfba9599, 0xb8bda50f, 0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924,
	    0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d, 0x76dc4190, 0x01db7106,
	    0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
	    0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d,
	    0x91646c97, 0xe6635c01, 0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e,
	    0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457, 0x65b0d9c6, 0x12b7e950,
	    0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
	    0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7,
	    0xa4d1c46d, 0xd3d6f4fb, 0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0,
	    0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9, 0x5005713c, 0x270241aa,
	    0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
	    0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81,
	    0xb7bd5c3b, 0xc0ba6cad, 0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a,
	    0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683, 0xe3630b12, 0x94643b84,
	    0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
	    0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb,
	    0x196c3671, 0x6e6b06e7, 0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc,
	    0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5, 0xd6d6a3e8, 0xa1d1937e,
	    0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
	    0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55,
	    0x316e8eef, 0x4669be79, 0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236,
	    0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f, 0xc5ba3bbe, 0xb2bd0b28,
	    0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
	    0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f,
	    0x72076785, 0x05005713, 0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38,
	    0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21, 0x86d3d2d4, 0xf1d4e242,
	    0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
	    0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69,
	    0x616bffd3, 0x166ccf45, 0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2,
	    0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db, 0xaed16a4a, 0xd9d65adc,
	    0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
	    0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693,
	    0x54de5729, 0x23d967bf, 0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94,
	    0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d
	};

	private final static int swg_crctable[] = {
	    0x0000000,       
	    0x04C11DB7, 0x09823B6E, 0x0D4326D9, 0x130476DC, 0x17C56B6B,
	    0x1A864DB2, 0x1E475005, 0x2608EDB8, 0x22C9F00F, 0x2F8AD6D6,
	    0x2B4BCB61, 0x350C9B64, 0x31CD86D3, 0x3C8EA00A, 0x384FBDBD,
	    0x4C11DB70, 0x48D0C6C7, 0x4593E01E, 0x4152FDA9, 0x5F15ADAC,
	    0x5BD4B01B, 0x569796C2, 0x52568B75, 0x6A1936C8, 0x6ED82B7F,
	    0x639B0DA6, 0x675A1011, 0x791D4014, 0x7DDC5DA3, 0x709F7B7A,
	    0x745E66CD, 0x9823B6E0, 0x9CE2AB57, 0x91A18D8E, 0x95609039,
	    0x8B27C03C, 0x8FE6DD8B, 0x82A5FB52, 0x8664E6E5, 0xBE2B5B58,
	    0xBAEA46EF, 0xB7A96036, 0xB3687D81, 0xAD2F2D84, 0xA9EE3033,
	    0xA4AD16EA, 0xA06C0B5D, 0xD4326D90, 0xD0F37027, 0xDDB056FE,
	    0xD9714B49, 0xC7361B4C, 0xC3F706FB, 0xCEB42022, 0xCA753D95,
	    0xF23A8028, 0xF6FB9D9F, 0xFBB8BB46, 0xFF79A6F1, 0xE13EF6F4,
	    0xE5FFEB43, 0xE8BCCD9A, 0xEC7DD02D, 0x34867077, 0x30476DC0,
	    0x3D044B19, 0x39C556AE, 0x278206AB, 0x23431B1C, 0x2E003DC5,
	    0x2AC12072, 0x128E9DCF, 0x164F8078, 0x1B0CA6A1, 0x1FCDBB16,
	    0x018AEB13, 0x054BF6A4, 0x0808D07D, 0x0CC9CDCA, 0x7897AB07,
	    0x7C56B6B0, 0x71159069, 0x75D48DDE, 0x6B93DDDB, 0x6F52C06C,
	    0x6211E6B5, 0x66D0FB02, 0x5E9F46BF, 0x5A5E5B08, 0x571D7DD1,
	    0x53DC6066, 0x4D9B3063, 0x495A2DD4, 0x44190B0D, 0x40D816BA,
	    0xACA5C697, 0xA864DB20, 0xA527FDF9, 0xA1E6E04E, 0xBFA1B04B,
	    0xBB60ADFC, 0xB6238B25, 0xB2E29692, 0x8AAD2B2F, 0x8E6C3698,
	    0x832F1041, 0x87EE0DF6, 0x99A95DF3, 0x9D684044, 0x902B669D,
	    0x94EA7B2A, 0xE0B41DE7, 0xE4750050, 0xE9362689, 0xEDF73B3E,
	    0xF3B06B3B, 0xF771768C, 0xFA325055, 0xFEF34DE2, 0xC6BCF05F,
	    0xC27DEDE8, 0xCF3ECB31, 0xCBFFD686, 0xD5B88683, 0xD1799B34,
	    0xDC3ABDED, 0xD8FBA05A, 0x690CE0EE, 0x6DCDFD59, 0x608EDB80,
	    0x644FC637, 0x7A089632, 0x7EC98B85, 0x738AAD5C, 0x774BB0EB,
	    0x4F040D56, 0x4BC510E1, 0x46863638, 0x42472B8F, 0x5C007B8A,
	    0x58C1663D, 0x558240E4, 0x51435D53, 0x251D3B9E, 0x21DC2629,
	    0x2C9F00F0, 0x285E1D47, 0x36194D42, 0x32D850F5, 0x3F9B762C,
	    0x3B5A6B9B, 0x0315D626, 0x07D4CB91, 0x0A97ED48, 0x0E56F0FF,
	    0x1011A0FA, 0x14D0BD4D, 0x19939B94, 0x1D528623, 0xF12F560E,
	    0xF5EE4BB9, 0xF8AD6D60, 0xFC6C70D7, 0xE22B20D2, 0xE6EA3D65,
	    0xEBA91BBC, 0xEF68060B, 0xD727BBB6, 0xD3E6A601, 0xDEA580D8,
	    0xDA649D6F, 0xC423CD6A, 0xC0E2D0DD, 0xCDA1F604, 0xC960EBB3,
	    0xBD3E8D7E, 0xB9FF90C9, 0xB4BCB610, 0xB07DABA7, 0xAE3AFBA2,
	    0xAAFBE615, 0xA7B8C0CC, 0xA379DD7B, 0x9B3660C6, 0x9FF77D71,
	    0x92B45BA8, 0x9675461F, 0x8832161A, 0x8CF30BAD, 0x81B02D74,
	    0x857130C3, 0x5D8A9099, 0x594B8D2E, 0x5408ABF7, 0x50C9B640,
	    0x4E8EE645, 0x4A4FFBF2, 0x470CDD2B, 0x43CDC09C, 0x7B827D21,
	    0x7F436096, 0x7200464F, 0x76C15BF8, 0x68860BFD, 0x6C47164A,
	    0x61043093, 0x65C52D24, 0x119B4BE9, 0x155A565E, 0x18197087,
	    0x1CD86D30, 0x029F3D35, 0x065E2082, 0x0B1D065B, 0x0FDC1BEC,
	    0x3793A651, 0x3352BBE6, 0x3E119D3F, 0x3AD08088, 0x2497D08D,
	    0x2056CD3A, 0x2D15EBE3, 0x29D4F654, 0xC5A92679, 0xC1683BCE,
	    0xCC2B1D17, 0xC8EA00A0, 0xD6AD50A5, 0xD26C4D12, 0xDF2F6BCB,
	    0xDBEE767C, 0xE3A1CBC1, 0xE760D676, 0xEA23F0AF, 0xEEE2ED18,
	    0xF0A5BD1D, 0xF464A0AA, 0xF9278673, 0xFDE69BC4, 0x89B8FD09,
	    0x8D79E0BE, 0x803AC667, 0x84FBDBD0, 0x9ABC8BD5, 0x9E7D9662,
	    0x933EB0BB, 0x97FFAD0C, 0xAFB010B1, 0xAB710D06, 0xA6322BDF,
	    0xA2F33668, 0xBCB4666D, 0xB8757BDA, 0xB5365D03, 0xB1F740B4,
	};
	
	/**
	 * Computes the 32 bit hash value for the given byte array.
	 * @param buffer -- The byte array data to hash.
	 * @return -- The CRC hash of the array.
	 */
	
	private final static long STARTING_CRC = 0xFFFFFFFFl;
	private static int SWGCrc(byte[] buffer) {
        int length = (buffer.length);
        long CRC = STARTING_CRC;

        
        int index = 0;

        for(short counter = 0; counter < length; counter++){
        	// Index = bitwise exclusive or for the first byte of the CRC and the buffer here.
            index = (int)(buffer[counter] ^ (CRC >>> 24));
            //System.out.println("["+counter+"] index = " + index + "("+Integer.toHexString(index)+"), CRC = " + CRC + ", (CRC>>24) = "+ Long.toHexString(CRC>>>24) + ", buffer["+counter+"] = " + buffer[counter]);
            // Then, the CRC equals the original CRC AND some value in the crc table, exclusive or with the original CRC and the current CRC shifted left 8.
            
            CRC = (STARTING_CRC & swg_crctable[ index ]) ^ (STARTING_CRC & (CRC << 8));
            //System.out.println("\tCRC = " + CRC + ", " + Long.toHexString(CRC) + ", swg_crc[index] = " + Integer.toHexString(swg_crctable[ index ]));
        }
        // Return the bitwise inverse of the CRC.
        return (int)~CRC;
	}

	public static void main(String[] args) throws Exception {
		//System.out.println("Calculating CRC for SharedRaceModels[Wookiee_FEMALE]");
		//int iCRC = SWGCrc("object/creature/player/shared_human_male.iff");
		//System.out.println("CRC for human female: " + Integer.toHexString(iCRC));
		//System.out.println("It is " + Integer.toHexString(iCRC));
		//System.out.println("Calculating CRC for PlayerRaceModels[Wookiee_FEMALE]");
		//iCRC = SWGCrc(Constants.PlayerRaceModels[Constants.RACE_WOOKIEE_FEMALE]);
		//System.out.println("It is " + Integer.toHexString(iCRC));
		
		int x = 0x41200000;
		float f = Float.intBitsToFloat(x);
		System.out.println(f);
	}

	/**
	 * Calculates the CRC32 hash of the passed String value.
	 * @param buffer -- The String to calculate the CRC of.
	 * @return -- The CRC.
	 */
	public static int SWGCrc(String buffer) {
		byte[] info = buffer.getBytes();
		return SWGCrc(info);
	}
	
	/**
	 * This function tests whether the CRC we receive is valid for the CRC seed we passed.
	 * @param pData -- The buffer which we are testing the CRC on.
	 * @param nLength -- The length of the buffer.
	 * @param nCrcSeed -- The CRC which we are testing.
	 * @return If the CRC is valid.
	 */
	public static boolean CrcTest(byte[] pData, short nLength, int nCrcSeed)
	{
		
		short nCrcLength = 2;	
		boolean crctest = true;
        int p_crc = GenerateCrc(pData,(short)pData.length,nCrcSeed);
        //int crc = 0;
        int mask = 0;
        //int pullbyte = 0;
		int crc = (short)(pData[pData.length - 2] << 8) + (short)(pData[pData.length - 1]);

        for (int i = 0; i < nCrcLength; i++) {
        	mask <<= 8;
        	mask |= 0xFF;
        } 
        p_crc &= mask;
        if(p_crc != crc) {
            crctest = false;
        }
	    return crctest;
	}
	
	/**
	 * Generates a 16 bit CRC for a given packet.
	 * @param pData -- The packet
	 * @param nLength -- The length of the packet.
	 * @param nCrcSeed -- The CRC seed.
	 * @return The CRC for this packet.
	 */
	public static int GenerateCrc(byte[] pData, short nLength, int nCrcSeed)
	{
		int arrayIndex = (~nCrcSeed) & 0xFF;
		int nCrc = g_nCrcTable[arrayIndex];
		nCrc ^= 0x00FFFFFF;
		int nIndex = (nCrcSeed >> 8) ^ nCrc;
		nCrc = (nCrc >> 8) & 0x00FFFFFF;
		nCrc ^= g_nCrcTable[(nIndex) & 0xFF];
		nIndex = (nCrcSeed >> 16) ^ nCrc;
		nCrc = (nCrc >> 8) & 0x00FFFFFF;
		nCrc ^= g_nCrcTable[(nIndex) & 0xFF];
		nIndex = (nCrcSeed >> 24) ^ nCrc;
		nCrc = (nCrc >> 8) & 0x00FFFFFF;
		nCrc ^= g_nCrcTable[(nIndex) & 0xFF];
		for (int i = 0; i< nLength; i++) {
			
			nIndex = (pData[i] ^ nCrc);
			nCrc = (nCrc >> 8) & 0x00FFFFFF;
			nCrc ^= g_nCrcTable[nIndex & 0xFF];
		}
		return ~nCrc;
	}

	/**
	 * Generates a random CRC seed for this session.
	 * @return The random seed.
	 */
	public static int getRandomSeed() {
		if (true) return (int)(Math.random() * (long)(Integer.MAX_VALUE * 2)); 
		short[] test = new short[2];
		test[0] = (short)((Math.random() * Double.MAX_VALUE) % 65535);
		test[1] = (short)((Math.random() * Double.MAX_VALUE) % 65535);
		//System.out.println("Generating random crc seed.  Test[0] = " + test[0] + ", test[1] = " + test[1]);
		int toReturn;
		int temp1 = test[0];
		int temp2 = test[1];
		//toReturn = (test[0] << 32) + (test[1]);
		//toReturn = test[0];
		toReturn = (temp1);
		toReturn = toReturn << 16;
		toReturn += temp2;
		
		//System.out.println("Hope this worked.  Return value: " + toReturn);
		return toReturn;
	}
	
	/**
	 * Appends the randomly generated CRC to this packet.
	 * @param pData -- The packet.
	 * @param nLength -- The size of the packet.
	 * @param nCrcSeed -- The CRC seed for the client's session.
	 * @return The Packet, with the CRC appended.
	 * @throws IOException If an error occured appending the CRC.
	 */
	public static ByteArrayOutputStream AppendCRC(ByteArrayOutputStream pData, int nLength, int nCrcSeed) throws IOException
	{
		
		short nCrcLength = 2;
		int crc = GenerateCrc(pData.toByteArray(),(short)(nLength-nCrcLength),nCrcSeed);
		byte[] newOutput = pData.toByteArray();
        for( short i = 0; i < nCrcLength; i++ )
        {
            newOutput[(nLength - 1) - i] = (byte)((crc >> (8 * i)) & 0xFF);
        }
        ByteArrayOutputStream newData = new ByteArrayOutputStream();
        newData.write(newOutput);
        return newData;
	}

	/**
	 * Appends a randomly generated CRC seed to the packet data based on the CRC seed.
	 * @param pData -- The packet data.
	 * @param nLength -- The size of the packet data.
	 * @param nCrcSeed -- The CRC seed for this client session.
	 * @return The packet data, with the CRC appended.
	 * @throws IOException If an error occured appending the CRC.
	 */
	public static byte[] AppendCRC(byte[] pData, int nLength, int nCrcSeed) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream dOut = new DataOutputStream(bOut);
		dOut.write(pData);
		return AppendCRC(bOut, nLength, nCrcSeed).toByteArray();
		
	}
	

	/**
	 * This function prints packet data out to the screen, in hexidecimal format.
	 * @param packet -- The packet data.
	 */
	protected static void printPacketData(byte[] packet) {
		StringBuffer b = new StringBuffer().append("PRINT PACKET DATA:\n---------------------------------------------------\n");
                int ctr = 0;
		for (int i = 0; i < packet.length - 1; i++) {
			b.append(getByteCode(packet[i]) + " ");
                        if(ctr == 15)
                        {
                            b.append("\n");
                            ctr = 0;
                        }
                        else
                        {
                            ctr++;      
                        }
		}
		b.append(getByteCode(packet[packet.length-1]) + " \n---------------------------------------------------");
		System.out.println(b.toString());
		System.out.flush();
	}

	/**
	 * This function builds a String which contains the hexidecimal-formatted packet data.
	 * @param packet -- The packet data to Stringify.
	 * @return The String containing the hexidecimal-formatted packet data.
	 */
	protected static String getPacketData(byte[] packet) {
		StringBuffer b = new StringBuffer().append("Packet data: 0x");
		for (int i = 0; i < packet.length - 1; i++) {
			b.append(Integer.toHexString(packet[i])).append(", 0x");
			
		}
		b.append(Integer.toHexString(packet[packet.length-1]));
		b.append("\n");
		return b.toString();
	}


        /**
         * This function receives a byte code and returns a string that represents
         * The byte code in a string form. 
         * @param b -- The byte code to identify
         * @return The string containing the string hex value of the byte passed in.
         * eg: byte 10 returns "0A", byte 16 returns "10"
         */
        protected static String getByteCode(byte b) {
                     
            switch(b)
            {
                case 0:{return "00";}
		case 1:{return "01";}
		case 2:{return "02";}
		case 3:{return "03";}
		case 4:{return "04";}
		case 5:{return "05";}
		case 6:{return "06";}
		case 7:{return "07";}
		case 8:{return "08";}
		case 9:{return "09";}
		case 10:{return "0A";}
		case 11:{return "0B";}
		case 12:{return "0C";}
		case 13:{return "0D";}
		case 14:{return "0E";}
		case 15:{return "0F";}
		case 16:{return "10";}
		case 17:{return "11";}
		case 18:{return "12";}
		case 19:{return "13";}
		case 20:{return "14";}
		case 21:{return "15";}
		case 22:{return "16";}
		case 23:{return "17";}
		case 24:{return "18";}
		case 25:{return "19";}
		case 26:{return "1A";}
		case 27:{return "1B";}
		case 28:{return "1C";}
		case 29:{return "1D";}
		case 30:{return "1E";}
		case 31:{return "1F";}
		case 32:{return "20";}
		case 33:{return "21";}
		case 34:{return "22";}
		case 35:{return "23";}
		case 36:{return "24";}
		case 37:{return "25";}
		case 38:{return "26";}
		case 39:{return "27";}
		case 40:{return "28";}
		case 41:{return "29";}
		case 42:{return "2A";}
		case 43:{return "2B";}
		case 44:{return "2C";}
		case 45:{return "2D";}
		case 46:{return "2E";}
		case 47:{return "2F";}
		case 48:{return "30";}
		case 49:{return "31";}
		case 50:{return "32";}
		case 51:{return "33";}
		case 52:{return "34";}
		case 53:{return "35";}
		case 54:{return "36";}
		case 55:{return "37";}
		case 56:{return "38";}
		case 57:{return "39";}
		case 58:{return "3A";}
		case 59:{return "3B";}
		case 60:{return "3C";}
		case 61:{return "3D";}
		case 62:{return "3E";}
		case 63:{return "3F";}
		case 64:{return "40";}
		case 65:{return "41";}
		case 66:{return "42";}
		case 67:{return "43";}
		case 68:{return "44";}
		case 69:{return "45";}
		case 70:{return "46";}
		case 71:{return "47";}
		case 72:{return "48";}
		case 73:{return "49";}
		case 74:{return "4A";}
		case 75:{return "4B";}
		case 76:{return "4C";}
		case 77:{return "4D";}
		case 78:{return "4E";}
		case 79:{return "4F";}
		case 80:{return "50";}
		case 81:{return "51";}
		case 82:{return "52";}
		case 83:{return "53";}
		case 84:{return "54";}
		case 85:{return "55";}
		case 86:{return "56";}
		case 87:{return "57";}
		case 88:{return "58";}
		case 89:{return "59";}
		case 90:{return "5A";}
		case 91:{return "5B";}
		case 92:{return "5C";}
		case 93:{return "5D";}
		case 94:{return "5E";}
		case 95:{return "5F";}
		case 96:{return "60";}
		case 97:{return "61";}
		case 98:{return "62";}
		case 99:{return "63";}
		case 100:{return "64";}
		case 101:{return "65";}
		case 102:{return "66";}
		case 103:{return "67";}
		case 104:{return "68";}
		case 105:{return "69";}
		case 106:{return "6A";}
		case 107:{return "6B";}
		case 108:{return "6C";}
		case 109:{return "6D";}
		case 110:{return "6E";}
		case 111:{return "6F";}
		case 112:{return "70";}
		case 113:{return "71";}
		case 114:{return "72";}
		case 115:{return "73";}
		case 116:{return "74";}
		case 117:{return "75";}
		case 118:{return "76";}
		case 119:{return "77";}
		case 120:{return "78";}
		case 121:{return "79";}
		case 122:{return "7A";}
		case 123:{return "7B";}
		case 124:{return "7C";}
		case 125:{return "7D";}
		case 126:{return "7E";}
		case 127:{return "7F";}
		case (byte)128:{return "80";}
		case (byte)129:{return "81";}
		case (byte)130:{return "82";}
		case (byte)131:{return "83";}
		case (byte)132:{return "84";}
		case (byte)133:{return "85";}
		case (byte)134:{return "86";}
		case (byte)135:{return "87";}
		case (byte)136:{return "88";}
		case (byte)137:{return "89";}
		case (byte)138:{return "8A";}
		case (byte)139:{return "8B";}
		case (byte)140:{return "8C";}
		case (byte)141:{return "8D";}
		case (byte)142:{return "8E";}
		case (byte)143:{return "8F";}
		case (byte)144:{return "90";}
		case (byte)145:{return "91";}
		case (byte)146:{return "92";}
		case (byte)147:{return "93";}
		case (byte)148:{return "94";}
		case (byte)149:{return "95";}
		case (byte)150:{return "96";}
		case (byte)151:{return "97";}
		case (byte)152:{return "98";}
		case (byte)153:{return "99";}
		case (byte)154:{return "9A";}
		case (byte)155:{return "9B";}
		case (byte)156:{return "9C";}
		case (byte)157:{return "9D";}
		case (byte)158:{return "9E";}
		case (byte)159:{return "9F";}
		case (byte)160:{return "A0";}
		case (byte)161:{return "A1";}
		case (byte)162:{return "A2";}
		case (byte)163:{return "A3";}
		case (byte)164:{return "A4";}
		case (byte)165:{return "A5";}
		case (byte)166:{return "A6";}
		case (byte)167:{return "A7";}
		case (byte)168:{return "A8";}
		case (byte)169:{return "A9";}
		case (byte)170:{return "AA";}
		case (byte)171:{return "AB";}
		case (byte)172:{return "AC";}
		case (byte)173:{return "AD";}
		case (byte)174:{return "AE";}
		case (byte)175:{return "AF";}
		case (byte)176:{return "B0";}
		case (byte)177:{return "B1";}
		case (byte)178:{return "B2";}
		case (byte)179:{return "B3";}
		case (byte)180:{return "B4";}
		case (byte)181:{return "B5";}
		case (byte)182:{return "B6";}
		case (byte)183:{return "B7";}
		case (byte)184:{return "B8";}
		case (byte)185:{return "B9";}
		case (byte)186:{return "BA";}
		case (byte)187:{return "BB";}
		case (byte)188:{return "BC";}
		case (byte)189:{return "BD";}
		case (byte)190:{return "BE";}
		case (byte)191:{return "BF";}
		case (byte)192:{return "C0";}
		case (byte)193:{return "C1";}
		case (byte)194:{return "C2";}
		case (byte)195:{return "C3";}
		case (byte)196:{return "C4";}
		case (byte)197:{return "C5";}
		case (byte)198:{return "C6";}
		case (byte)199:{return "C7";}
		case (byte)200:{return "C8";}
		case (byte)201:{return "C9";}
		case (byte)202:{return "CA";}
		case (byte)203:{return "CB";}
		case (byte)204:{return "CC";}
		case (byte)205:{return "CD";}
		case (byte)206:{return "CE";}
		case (byte)207:{return "CF";}
		case (byte)208:{return "D0";}
		case (byte)209:{return "D1";}
		case (byte)210:{return "D2";}
		case (byte)211:{return "D3";}
		case (byte)212:{return "D4";}
		case (byte)213:{return "D5";}
		case (byte)214:{return "D6";}
		case (byte)215:{return "D7";}
		case (byte)216:{return "D8";}
		case (byte)217:{return "D9";}
		case (byte)218:{return "DA";}
		case (byte)219:{return "DB";}
		case (byte)220:{return "DC";}
		case (byte)221:{return "DD";}
		case (byte)222:{return "DE";}
		case (byte)223:{return "DF";}
		case (byte)224:{return "E0";}
		case (byte)225:{return "E1";}
		case (byte)226:{return "E2";}
		case (byte)227:{return "E3";}
		case (byte)228:{return "E4";}
		case (byte)229:{return "E5";}
		case (byte)230:{return "E6";}
		case (byte)231:{return "E7";}
		case (byte)232:{return "E8";}
		case (byte)233:{return "E9";}
		case (byte)234:{return "EA";}
		case (byte)235:{return "EB";}
		case (byte)236:{return "EC";}
		case (byte)237:{return "ED";}
		case (byte)238:{return "EE";}
		case (byte)239:{return "EF";}
		case (byte)240:{return "F0";}
		case (byte)241:{return "F1";}
		case (byte)242:{return "F2";}
		case (byte)243:{return "F3";}
		case (byte)244:{return "F4";}
		case (byte)245:{return "F5";}
		case (byte)246:{return "F6";}
		case (byte)247:{return "F7";}
		case (byte)248:{return "F8";}
		case (byte)249:{return "F9";}
		case (byte)250:{return "FA";}
		case (byte)251:{return "FB";}
		case (byte)252:{return "FC";}
		case (byte)253:{return "FD";}
		case (byte)254:{return "FE";}
		case (byte)255:{return "FF";}
            }
            return "BADCACA";
        }
        
        
        public static void printPacketToScreen(byte[] ba, String sComment) {
        	printPacketToScreen(ba, ba.length, sComment);
        }
        
        public static void printPacketToScreen(byte[] ba, int nLength,String sComment) {
            
            System.out.println("-----> Print Packet To Screen. <-----");
            System.out.println("-----> " + sComment + " <-----");
            System.out.println("-----> Len:" + nLength + " <-----");
            System.out.println("<------------------------------------------------>");
            int ctr = 0;
            for(int i = 0; i < nLength; i++)
            {
                System.out.print(getByteCode(ba[i])+ " ");
                if(ctr == 15)
                {
                    ctr = 0;
                    System.out.println(" ");
                }
                else
                {
                    ctr++;
                }
            }
            System.out.println(" ");
            System.out.println("<------------------------------------------------>");
        }
        

} //end class
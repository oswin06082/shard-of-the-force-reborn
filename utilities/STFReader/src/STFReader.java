import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



public class STFReader {
	private String sFilename = null;
	//private Hashtable<String, String> vMatchedStrings = null;
	
 	public static void main(String[] args) {
		
		if (args.length > 0) {
			new STFReader(args[0]);
		} else {
			new STFReader();
		}
	}
	
	
	public STFReader() {
		sFilename = "obj_attr_n.stf";
		initialize();
	}
	
	public STFReader(String filename) {
		this.sFilename = filename;
		initialize();
	}
	
	private void initialize() {
		FileInputStream f = null;
		try {
			f = new FileInputStream(new File(sFilename));
		} catch (FileNotFoundException e) {
			System.out.println("Error loading file " + sFilename + ": " + e.toString());
			e.printStackTrace();
		}
		if (f != null) {
			try {
				SOEInputStream dIn = new SOEInputStream(f);
				byte[] serializedFile = new byte[dIn.available() + 1];
				dIn.read(serializedFile);
				//PacketUtils.printPacketData(serializedFile);
				dIn.close();
				ByteArrayInputStream bIn = new ByteArrayInputStream(serializedFile);
				dIn = new SOEInputStream(bIn);
				readFile(dIn);
			} catch (IOException e) {
				System.out.println("Error reading: " + e.toString());
				e.printStackTrace();
			}
		}
	}
	
	private void readFile(SOEInputStream dIn) throws IOException{
		// This is the first half of the file.
		/*int unknown1 =*/ dIn.readInt();
		/*byte unknownByte = */  dIn.readByte();
		int iArraySize = dIn.readInt();
		int iNumStrings = dIn.readInt();
		if (iNumStrings != (iArraySize - 1)) {
			System.out.println("Possible error:  Number of strings != array size - 1");
		}
		String[] vFirstStrings = new String[iArraySize];
		for (int i = 0; i < iNumStrings; i++) {
			int iStringNumber = dIn.readInt();
			if (iStringNumber != (i+1)) {
				System.out.println("Possible error:  String number is " + iStringNumber + ", expected " + (i+1));
			}
			/*int iSeparator =*/ dIn.readInt(); // Always -1
			vFirstStrings[i] = dIn.readUTF16();
			//System.out.println("Got string: " + vFirstStrings[i]);
		}
		
		// This is the second half of the file.
		String[] vSecondStrings = new String[iArraySize];
		for (int i = 0; i < iNumStrings; i++) {
			int iStringNumber = dIn.readInt();
			int iStringLength = dIn.readInt();
			byte[] stringBytes = new byte[iStringLength];
			for (int j = 0; j < iStringLength; j++) {
				stringBytes[j] = dIn.readByte();
			}
			vSecondStrings[iStringNumber - 1] = new String(stringBytes);
		}
		for (int i = 0; i < vFirstStrings.length; i++) {
			System.out.println("Client var name: " + vSecondStrings[i] + ", description: " + vFirstStrings[i]);
		}
	}
}

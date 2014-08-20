import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;


public class IFFReader {
	private final static String sDefaultDirectory = "c:/swguncomp/";
	private Vector<DraftSchematicAttributeData> vDataList;
	private final static byte[] GARBAGE_SEPARATOR = {
		0, 1,
	};
	
	private final static String GARBAGE_SEPARATOR_STRING = new String(GARBAGE_SEPARATOR);
	private BufferedWriter buffOut;
	private FileWriter fileOut; 
	
	public static void main(String[] args) throws Exception {
		long lStartTime = System.currentTimeMillis();
		IFFReader reader = new IFFReader();
		if (args != null && args.length > 0) {
			reader.initialize(args[0]);
		} else {
			reader.initialize(sDefaultDirectory);
		}
		long lEndTime = System.currentTimeMillis();
		long lDeltaTime = lEndTime - lStartTime;
		System.out.println("Run complete.  Elapsed time: " + lDeltaTime);
	}
	
	public IFFReader() throws Exception{
		fileOut = new FileWriter("sqlquery.sql");
		buffOut = new BufferedWriter(fileOut);
		vDataList = new Vector<DraftSchematicAttributeData>();
		
	}
	
	private void initialize(String sDirectory) throws Exception{
		File directory = new File(sDirectory);
		File[] allIFFFiles = directory.listFiles();
		handleDirectory(allIFFFiles); // Recursively scans all files in the given directory.
		
		System.out.println("Read " + vDataList.size() + " templates.");
		for (int i = 0; i < vDataList.size(); i++) {
			System.out.println(vDataList.elementAt(i));
		}
		
		
		
	}
	
	private void handleDirectory(File[] subDirectories) {
		for (int i = 0; i < subDirectories.length; i++) {
			if (subDirectories[i].isDirectory()) {
				handleDirectory(subDirectories[i].listFiles());
			} else if (subDirectories[i].isFile()) {
				readFileData(subDirectories[i]);
			}
		}
		
	}
	
	private final static int FORM = 0x464f524D;
	private final static int SDSC = 0x53445343;
	private final static int DERV = 0x44455256; // Unknown
	private final static int XXXX = 0x58585858; // Immediately preceeds a string size.
	private final static int PCNT = 0x50434E54;
	private void readFileData(File file){
		try {
			String sCanonicalPath = file.getCanonicalPath();
			sCanonicalPath = sCanonicalPath.replace("C:" + File.separator + "swguncomp" + File.separator, "").replace(File.separator, "/");
			if (sCanonicalPath.endsWith(".iff")) {

				DraftSchematicAttributeData data = new DraftSchematicAttributeData(sCanonicalPath);
				FileInputStream fIn = new FileInputStream(file);
				byte[] fileBytes = new byte[fIn.available() + 1];
				fIn.read(fileBytes);
				String firstSection = "slots";
				String secondSection = "attributes";
				String fileAsString = new String(fileBytes);
				String slotsString = //null;
				fileAsString.split(firstSection)[1];
				String attributesString = null;
				try {
					attributesString = fileAsString.split(secondSection)[1];
				} catch (ArrayIndexOutOfBoundsException ee) {
					System.out.println("No attributes list for " + sCanonicalPath);
				}
				byte[] slotsRawData = slotsString.getBytes();
				readSlotsData(data, sCanonicalPath, slotsRawData);
				if (attributesString != null) {
					byte[] attributesRawData = attributesString.getBytes();
					readAttributesData(data, sCanonicalPath, attributesRawData);
				}
				vDataList.add(data);
			}
			
		} catch (Exception e) {
			System.out.println("Error in readFileData for file " + file.getName() + ": " + e.toString());
			e.printStackTrace();
		}
	}

	private void readSlotsData(DraftSchematicAttributeData data, String sFilename, byte[] slotsRawData) throws IOException {
		DataInputStream dIn= new DataInputStream(new ByteArrayInputStream(slotsRawData));
		dIn.readShort(); // Skip null first 2 bytes.
		int numSlots = dIn.readInt();
		numSlots = Integer.reverseBytes(numSlots); // Get the right number of slots.
		data.setNumSlots(numSlots);
		for (int i = 0; i < numSlots; i++) {
			//System.out.println("Iteration " + i);
			dIn.skip(37);
			StringBuffer buff = new StringBuffer();
			byte lastReadValue = dIn.readByte();
			while (lastReadValue != 0) {
				buff.append((char)lastReadValue);
				lastReadValue = dIn.readByte();
			}
			
			//System.out.println("sNameString = " + buff.toString());
			buff = new StringBuffer();
			dIn.skip(2);
			lastReadValue = dIn.readByte();
			while (lastReadValue != 0) {
				buff.append((char)lastReadValue);
				lastReadValue = dIn.readByte();
			}
			//System.out.println("STF Filename: " + buff.toString());
			data.setSlotSTFFilename(buff.toString(), i);
			dIn.skip(1);
			buff = new StringBuffer();
			lastReadValue = dIn.readByte();
			while (lastReadValue != 0) {
				buff.append((char)lastReadValue);
				lastReadValue = dIn.readByte();
			}
			//System.out.println("STF File Identifier: " + buff.toString());
			data.setSlotSTFFileIdentifier(buff.toString(), i);
			dIn.skip(6);
			String hardPoint = dIn.readUTF();
			//System.out.println("Hardpoint string: " + hardPoint);
			dIn.skip(8);
		}
		
	}

	private void readAttributesData(DraftSchematicAttributeData data, String sFilename, byte[] rawAttributeData) throws IOException {
		//PacketUtils.printPacketToScreen(rawAttributeData, "ReadAttributesData");
		DataInputStream dIn = new DataInputStream(new ByteArrayInputStream(rawAttributeData));
		dIn.skip(2); // Skip first 2 bytes.
		int numAttributes = Integer.reverseBytes(dIn.readInt()); // Number of attributes.
		data.setNumAttributes(numAttributes);
		//System.out.println("File " + sFilename + ", Num attributes: " + numAttributes);
		StringBuffer buff = new StringBuffer();
		byte lastReadValue = 0;
		for (int i = 0; i < numAttributes; i++) {
			dIn.skip(37);
			lastReadValue = dIn.readByte();
			while (lastReadValue != 0) {
				buff.append((char)lastReadValue);
				lastReadValue = dIn.readByte();
			}
			
			//System.out.println("sNameString = " + buff.toString()); // name
			buff = new StringBuffer();
			dIn.skip(2);
			lastReadValue = dIn.readByte();
			while (lastReadValue != 0) {
				buff.append((char)lastReadValue);
				lastReadValue = dIn.readByte();
			}
			//System.out.println("STF Filename: " + buff.toString()); // crafting
			dIn.skip(1);
			buff = new StringBuffer();
			lastReadValue = dIn.readByte();
			while (lastReadValue != 0) {
				buff.append((char)lastReadValue);
				lastReadValue = dIn.readByte();
			}
			//System.out.println("STF File Identifier: " + buff.toString()); // complexity
            
            StringBuffer stfbuff = new StringBuffer();
            stfbuff.append(buff.toString());
            
			dIn.skip(8);
			
			// experiment
			buff = new StringBuffer();
			lastReadValue = dIn.readByte();
			while (lastReadValue != 0) {
				buff.append((char)lastReadValue);
				lastReadValue = dIn.readByte();
			}
			//System.out.println("Experiment header: " + buff.toString());
			buff = new StringBuffer();
			dIn.skip(2);
			lastReadValue = dIn.readByte();
			if (lastReadValue == 0) {
				//System.out.println("No experimental property index " + i);
				dIn.skip(34);
			} else {
				while (lastReadValue != 0) {
					buff.append((char)lastReadValue);
					lastReadValue = dIn.readByte();
				}
				//System.out.println("Experimentation STF Filename: " + buff.toString());

                data.setAttributeSTFFileIdentifier2(stfbuff.toString(), i);
                data.setAttributeSTFFileName(buff.toString(), i);

                dIn.skip(1);
				buff = new StringBuffer();
				lastReadValue = dIn.readByte();
				while (lastReadValue != 0) {
					buff.append((char)lastReadValue);
					lastReadValue = dIn.readByte();
				}
				//System.out.println("Experimentation STF File Identifier: " + buff.toString());
				data.setAttributeSTFFileIdentifier(buff.toString(), i);
				dIn.skip(32);
			}
		}
		
		// Crafted item .iff filename here.
		dIn.skip(8);
		buff = new StringBuffer();
		lastReadValue = dIn.readByte();
		while (lastReadValue != 0) {
			buff.append((char)lastReadValue);
			lastReadValue = dIn.readByte();
		}
		//System.out.println("Crafted item file name string: " + buff.toString());
		buff = new StringBuffer();
		dIn.skip(1);
		lastReadValue = dIn.readByte();
		while (lastReadValue != 0) {
			buff.append((char)lastReadValue);
			lastReadValue = dIn.readByte();
		}
		//System.out.println("Crafted item file name: " + buff.toString());
		data.setCraftedItemName(buff.toString());
	}
	
}

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.util.Vector;

public class STFReader {
	private Vector<File> loadedFiles;
	private File outputDirectory;
	private int placeInList;

	private void initialize(File targetFile) {
		FileInputStream f = null;

		try {
			f = new FileInputStream(targetFile);
		} catch (FileNotFoundException e) {
			System.out.println("Error loading file " + targetFile.getPath() + ": " + e.toString());
			e.printStackTrace();
		}
		if (f != null) {
			try {
				STFInputStream dIn = new STFInputStream(f);
				byte[] serializedFile = new byte[dIn.available() + 1];
				dIn.read(serializedFile);
				dIn.close();
				ByteArrayInputStream bIn = new ByteArrayInputStream(serializedFile);
				dIn = new STFInputStream(bIn);
				readFile(dIn, targetFile.getName());
			} catch (IOException e) {
				System.out.println("Error reading: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	private void readFile(STFInputStream dIn, String fileReadName) throws IOException{
		File ouptutTarget = new File(outputDirectory.getPath() + "\\" + fileReadName + ".txt");
		BufferedWriter out = new BufferedWriter(new FileWriter(ouptutTarget));
		dIn.readInt();
		dIn.readByte();
		int iArraySize = dIn.readInt();
		int iNumStrings = dIn.readInt();

		if (iNumStrings != (iArraySize - 1)) {
			System.out.println("Possible error encountered in " + fileReadName + ":  Number of strings != array size - 1");
		}

		String[] vFirstStrings = new String[iArraySize];

		for (int i = 0; i < iNumStrings; i++) {
			int iStringNumber = dIn.readInt();
			if (iStringNumber != (i+1)) {
				System.out.println("Possible error encountered in " + fileReadName + ":  String number is " + iStringNumber + ", expected " + (i+1));
			}
			dIn.readInt();
			vFirstStrings[i] = dIn.readUTF16();
		}

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

		out.write(fileReadName, 0, fileReadName.length());
		out.newLine();
		for (int i = 0; i < vFirstStrings.length; i++) {
			//vSecondStrings[i];		//Variable name
			//vFirstStrings[i];		//Data
			String outputString = vSecondStrings[i] + ": " + vFirstStrings[i];
			out.write(outputString, 0, outputString.length());
			out.newLine();
		}
		out.newLine();
		out.newLine();
		out.flush();
		out.close();
	}

	private void populateFiles(File currentFile) {
		if(currentFile.isDirectory()) {
			File[] subDirectory = currentFile.listFiles();
			for(int i = 0; i < subDirectory.length; i++) {
				populateFiles(subDirectory[i]);
			}
		} else {
			loadedFiles.add(currentFile);
		}
	}

	public STFReader(File startDirectory) throws FileNotFoundException, IOException {
		loadedFiles = new Vector<File>();
		outputDirectory = new File("output");

		try {
			if(startDirectory.isDirectory()) {
				if(!outputDirectory.isDirectory()) {
					boolean success = outputDirectory.mkdir();
					if(!success) {
						throw new IOException();
					}
				}
				populateFiles(startDirectory);

				try {
					for(int k = 0; k < loadedFiles.size(); k++) {
						initialize(loadedFiles.get(k));
					}
				} catch(Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			} else {
				throw new FileNotFoundException(startDirectory.getName() + " is not a directory.");
			}
		} catch(IOException e) {
			System.out.println("IOException encountered while interacting with file " + loadedFiles.get(placeInList).getName());
		}
	}

	public static void main(String args[]) {
		String usageString = "Usage: java STFReader targetdirectory";

		if(args.length == 1) {
			try {
				File f = new File(args[0]);

				if(f.isDirectory()) {
					STFReader system = new STFReader(f);
				} else {
					System.out.println(usageString);
				}
			} catch(FileNotFoundException e) {
				System.out.println(usageString);
			} catch(IOException e) {
				System.out.println("A fatal I/O error has occured, please close all programs using the specified directory and try again.");
			}
		} else {
			System.out.println(usageString);
		}
	}
}
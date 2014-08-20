import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class DatabaseRevisionUpdater {

	//Constants.
	public static final String VERSION_DECLARATION_PEDEGREE = "INSERT INTO `database_information`";
	public static final String VERSION_DECLARATION_TEMPLATE = "%s VALUES (%s);";
	public static final int MAX_LINES = 12500;

	//Instance variables.
	private String[] fileContents;
	private File targetFile;
	private File subversionEntriesFile;
	private int entries, currentRevision;

	public DatabaseRevisionUpdater(File targetFile, File subversionEntriesFile) throws IOException {
		this.targetFile = targetFile;
		this.subversionEntriesFile = subversionEntriesFile;
		fileContents = new String[MAX_LINES];
		entries = 0;
		currentRevision = loadSubversionRevision();
		loadFile();
	}

	private void loadFile() throws IOException {

		//Instance variables.
		BufferedReader in = new BufferedReader(new FileReader(targetFile));
		String line = "";
		boolean continueReading = true;

		//Parse the file.
		while(continueReading) {

			//Read the next line.
			line = in.readLine();

			//If line isn't EOF.
			if(line != null) {

				//Add it.
				fileContents[entries] = line;
				entries++;
			} else {

				//Stop looping.
				continueReading = false;
			}
		}

		//Close stream.
		in.close();
	}

	private int loadSubversionRevision() throws IOException {

		//Instance variables.
		BufferedReader in = new BufferedReader(new FileReader(subversionEntriesFile));
		int revision = -1;

		//Read the file.
		for(int k = 1; k <= 11; k++) {

			//Update position in file.
			String line = in.readLine();

			//If this is the fourth line.
			switch(k) {
				case 11: {
					revision = Integer.parseInt(line);
					break;
				}
			}
		}

		//Close stream.
		in.close();

		//Return result.
		return revision;
	}

	protected int seek(String targetLine) {

		//Instance variables.
		int lineNumber = -1;

		//Search for the line.
		for(int k = 0; k < entries; k++) {
			if(fileContents[k].contains(targetLine)) {
				lineNumber = k;
			}
		}

		//Return index.
		return lineNumber;
	}

	protected void replaceLine(int index, String newLine) {

		//Done this way in order to keep formatting/whitespaces/indentation without trying to predict it.
		fileContents[index] = fileContents[index].replace(fileContents[index], newLine);
	}

	protected void writeFile() throws IOException {

		//Instance variables.
		BufferedWriter out = new BufferedWriter(new FileWriter(targetFile));

		//Write file contents.
		for(int k = 0; k <= entries; k++) {

			String currentLine = fileContents[k];

			if(currentLine != null) {
				out.write(currentLine);
				out.newLine();
			}
		}

		//Close IO stream.
		out.close();
	}

	protected String getRevisionString(int revision) {
		return String.format(VERSION_DECLARATION_TEMPLATE, VERSION_DECLARATION_PEDEGREE, revision);
	}

	protected int getCurrentRevision() {
		return currentRevision;
	}

	public static void main(String[] args) {

		//Check arguments.
		switch(args.length) {
			case 2: {
				try {
	
					//Update revision number.
					DatabaseRevisionUpdater updater = new DatabaseRevisionUpdater(new File(args[0]), new File(args[1]));
					int currentRevision = updater.getCurrentRevision();
					int nextRevision = currentRevision + 1;
					String nextRevisionDeclaration = updater.getRevisionString(nextRevision);
					int declarationPosition = updater.seek(DatabaseRevisionUpdater.VERSION_DECLARATION_PEDEGREE);
						
					//This program was added in revision 912.
					if(currentRevision >= 942) {
	
						if(declarationPosition >= 0) {
	
							updater.replaceLine(declarationPosition, nextRevisionDeclaration);
							updater.writeFile();
							System.out.println("Revision updated to " + nextRevision + ".");
						} else {
	
							System.out.println("Unable to find Database version entry.");
						}
					} else {
	
						System.out.println("Your subversion repository is older than the oldest supported revision. Please update.");
					}
				} catch (IOException e) {
	
					//Nothing to do here.
					e.printStackTrace();
				}
				break;
			}
			default: {
				System.out.println("Usage: DatabaseRevisionUpdater [Location of DatabaseVersion.sql] [Location of any /.svn/entries]");
				break;
			}
		}
	}
}
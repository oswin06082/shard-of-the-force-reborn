import java.io.FilenameFilter;
import java.io.File;

public class STFFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		boolean accept;
		if(name.toLowerCase().endsWith(".stf") || dir.isDirectory()) {
			accept = true;
		} else {
			accept = false;
		}
		return accept;
	}
}
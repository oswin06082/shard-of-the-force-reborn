import net.persistentworlds.toolkit.Toolkit;
import net.persistentworlds.toolkit.gui.LoginWindow;

/**
 * For now the toolkit will only edit creatures and creature loot.
 * This will change as more editable content is added.
 * @author Interesting
 *
 */
public class ToolkitTest implements Toolkit {
	
	//Instance variables.
	private LoginWindow loginInterface;
	
	public ToolkitTest() {
		loginInterface = new LoginWindow(this);
		loginInterface.showWindow();
	}
	
	public void safeExit() {
		loginInterface.hideWindow();
		System.exit(0);
	}
	
	public static void main(String args[]) {
		@SuppressWarnings("unused")
		ToolkitTest toolkit = new ToolkitTest();
	}
}

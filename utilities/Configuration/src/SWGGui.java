import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.persistentworlds.config.FatalConfigException;
import net.persistentworlds.config.Config;

public class SWGGui implements Runnable {
	
	private JTextArea text;
	
	public void run() {
		while(true) {
			synchronized(this) {
				try {
					
					//This is to simulate activity in the server, hoping to get a hold of any blocking bugs.
					System.out.println("SWGGUI Emulation Thread cycle.");
					wait(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void showConfiguration(Config configuration) {
		JFrame window = new JFrame("Output Configuration");
		JPanel panel = new JPanel();
		text = new JTextArea();
		StringBuffer buffer = new StringBuffer();
		
		//Setup window.
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//Add components.
		panel.add(text);
		window.add(panel);
		
		//Parse Configuration info.
		addLine(buffer, "= General =");
		addLine(buffer, "Script path: " + configuration.getScriptPath());
		addLine(buffer, "Web path: " + configuration.getWebPath());
		addLine(buffer, "= Server =");
		addLine(buffer, "Zone server ID: " + configuration.getZoneServerID());
		addLine(buffer, "Login server port: " + configuration.getLoginServerPort());
		addLine(buffer, "Zone server port: " + configuration.getZoneServerPort());
		addLine(buffer, "Enable Login server: " + configuration.isLoginServerEnabled());
		addLine(buffer, "Enable Zone server: " + configuration.isZoneServerEnabled());
		addLine(buffer, "Remote Login server address: " + configuration.getRemoteLoginServerAddress());
		addLine(buffer, "Remote Login server port: " + configuration.getRemoteLoginServerPort());
		addLine(buffer, "= Database =");
		addLine(buffer, "Database address: " + configuration.getDatabaseAddress());
		addLine(buffer, "Database port: " + configuration.getDatabasePort());
		addLine(buffer, "Database username: " + configuration.getDatabaseUsername());
		addLine(buffer, "Database password: " + configuration.getDatabasePassword());
		addLine(buffer, "Database schema: " + configuration.getDatabaseSchema());
		addLine(buffer, "Enable secure passwords: " + configuration.isSecurePasswordsEnabled());
		addLine(buffer, "= Game =");
		addLine(buffer, "Auto register accounts: " + configuration.isAutoAccountRegistrationEnabled());
		addLine(buffer, "Enable tanaab: " + configuration.isTanaabEnabled());
		addLine(buffer, "Enable unknown planet: " + configuration.isUnknownPlanetEnabled());
		addLine(buffer, "Enable same faction hunting: " + configuration.isSameFactionHuntingEnabled());
		addLine(buffer, "= Authentication =");
		addLine(buffer, "Remote Authorization Username: " + configuration.getRemoteAuthorizationUsername());
		add(buffer, "Remote Authorization Password: " + configuration.getRemoteAuthorizationPassword());
		
		//Display configuration info.
		text.setText(buffer.toString());
		
		//Finish window setup.
		window.pack();
		window.setVisible(true);
	}
	
	private void addLine(StringBuffer buffer, String line) {
		buffer.append(line + "\n");
	}
	
	private void add(StringBuffer buffer, String text) {
		buffer.append(text);
	}

	public static void main(String[] args) {
		//Instance variables.
		final SWGGui gui = new SWGGui();
		final ServerSetup setup = new ServerSetup();	
		JFrame window = new JFrame("SWGGui");
		JPanel panel = new JPanel();
		JButton button = new JButton("Show");

		//Setup components.
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setup.handleLoadConfiguration();
					Config result = setup.getCurrentConfiguration();
					gui.showConfiguration(result);
				} catch(FatalConfigException ex) {
					ex.printStackTrace();
					System.exit(0);
				}
			}
		});

		//Add components.
		panel.add(button);
		window.add(panel);

		//Setup window.
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
		
		Thread t = new Thread(gui);
		t.start();
	}
}

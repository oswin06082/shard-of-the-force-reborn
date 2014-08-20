import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.persistentworlds.config.Config;
import net.persistentworlds.config.FatalConfigException;

/*
 * TODO:
 *		* Cleanup documentation/add new JavaDoc
 *		* Add a close button to the error list dialog.
 */
public class ServerSetup extends WindowAdapter implements Runnable, ActionListener, DocumentListener, ItemListener {

	//Instance variables.
	private JFrame invisibleWindow;
	private JDialog serverSetupWindow;
	private JPasswordField databasePasswordField, remoteAuthPasswordField;
	private JTextField databaseUsernameField, databaseSchemaField, remoteLoginServerAddressField, databaseAddressField, zoneServerIDField, remoteLoginServerPortField, databasePortField, zoneServerPortField, loginServerPortField, remoteAuthUsernameField;
	private JCheckBox enableLoginServerBox, enableZoneServerBox, securePasswordBox, accountAutoRegistrationBox, enableTanaabBox, enableUnknownPlanetBox, enableTraitorBox;
	private File configurationFile, currentWebPath, currentScriptPath;
	private String inputErrorString;
	private Config currentConfiguration;
	private boolean isCurrentConfigSaved, isPreviousConfigSaved, serverStartup;

	/**
	 * Construct the Server configuration interface and manager.
	 */
	public ServerSetup() {

		//Instantiate variables.
		configurationFile = new File(Constants.CONFIGURATION_FILE_NAME);
		inputErrorString = "";
		isCurrentConfigSaved = false;
		isPreviousConfigSaved = false;
		serverStartup = true;
		currentConfiguration = new Config();

		try {

			//Build the interface components on a thread.
			SwingUtilities.invokeAndWait(this);

		} catch (InterruptedException e) {

			//Nothing we can do.
			System.out.println("An unrecoverable error has occured in ServerSetup::ServerSetup(). Please report this to the developers as soon as possible.");
			e.printStackTrace();
			System.exit(0);

		} catch (InvocationTargetException e) {

			//Nothing we can do.
			System.out.println("An unrecoverable error has occured in ServerSetup::ServerSetup(). Please report this to the developers as soon as possible.");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void run() {

		//Initialize the window.
		initInterface();
	}

	private void initInterface() {
		//Local instance variables.
		JTabbedPane mainPanel = new JTabbedPane();
		JPanel generalTab = new JPanel();
		JPanel generalPanel = new JPanel();
		JPanel serverPanel = new JPanel();
		JPanel clusterPanel = new JPanel();
		JPanel databaseTab = new JPanel();
		JPanel authenticationTab = new JPanel();
		JPanel securitySettingsPanel = new JPanel();
		JPanel gameTab = new JPanel();
		JSeparator separatorMenuItem = new JSeparator();
		JLabel webDirectoryLabel = new JLabel("Web directory:");
		JLabel scriptDirectoryLabel = new JLabel("Script directory:");
		JLabel zoneServerPortLabel = new JLabel("Zone Broadcast Port:");
		JLabel loginServerPortLabel = new JLabel("Login Broadcast Port:");
		JLabel zoneServerIDLabel = new JLabel("Galaxy ID:");
		JLabel remoteLoginServerAddressLabel = new JLabel("Remote Login Server Address:");
		JLabel remoteLoginServerPortLabel = new JLabel("Remote Login Server Port:");
		JLabel databaseAddressLabel = new JLabel("Database Address:");
		JLabel databasePortLabel = new JLabel("Database Port:");
		JLabel databaseUsernameLabel = new JLabel("Database Username:");
		JLabel databasePasswordLabel = new JLabel("Database Password:");
		JLabel databaseSchemaLabel = new JLabel("Database Schema:");
		JLabel remoteAuthUsernameLabel = new JLabel("Authentication Server Username:");
		JLabel remoteAuthPasswordLabel = new JLabel("Authentication Server Password:");
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		JMenuItem defaultSettingsMenuItem = new JMenuItem("Default settings");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		JButton webDirectoryButton = new JButton("Browse");
		JButton scriptDirectoryButton = new JButton("Browse");

		//Instance variables.
		invisibleWindow = new JFrame();
		serverSetupWindow = new JDialog(invisibleWindow, "Shards of the Force Server Configuration", true);
		databaseUsernameField = new JTextField(10);
		databaseSchemaField = new JTextField(10);
		databasePasswordField = new JPasswordField(10);
		enableLoginServerBox = new JCheckBox("Enable Login Server");
		enableZoneServerBox = new JCheckBox("Enable Zone Server");
		zoneServerIDField = new JTextField(2);
		securePasswordBox = new JCheckBox("Store Passwords Securely:");
		accountAutoRegistrationBox = new JCheckBox("Account auto-registration:");
		enableTanaabBox = new JCheckBox("Tanaab Active:");
		enableUnknownPlanetBox = new JCheckBox("Unknown Planet Active:");
		enableTraitorBox = new JCheckBox("Same Faction Hunting Allowed:");
		remoteLoginServerAddressField = new JTextField(10);
		remoteLoginServerPortField = new JTextField(5);
		databaseAddressField = new JTextField(10);
		databasePortField = new JTextField(5);
		zoneServerPortField = new JTextField(10);
		loginServerPortField = new JTextField(10);
		remoteAuthUsernameField = new JTextField(10);
		remoteAuthPasswordField = new JPasswordField(10);

		//Setup components.
		/* BORDERS */
		generalPanel.setBorder(BorderFactory.createTitledBorder("General Settings"));
		serverPanel.setBorder(BorderFactory.createTitledBorder("Server Settings"));
		clusterPanel.setBorder(BorderFactory.createTitledBorder("Cluster Settings"));
		databaseTab.setBorder(BorderFactory.createTitledBorder("Database Settings"));
		securitySettingsPanel.setBorder(BorderFactory.createTitledBorder("Security Settings"));
		gameTab.setBorder(BorderFactory.createTitledBorder("Game Settings"));
		authenticationTab.setBorder(javax.swing.BorderFactory.createTitledBorder("Authentication Settings"));

		/* TOOL-TIPS */
		webDirectoryLabel.setToolTipText("The directory where the web status page will be stored.");
		scriptDirectoryLabel.setToolTipText("The directory where server script files are stored.");
		zoneServerPortLabel.setToolTipText("The UDP port to accept Zone connections. Players on your server will need to change this in the client config file.");
		loginServerPortLabel.setToolTipText("The UDP port to accept Login connections.");
		enableLoginServerBox.setToolTipText("Enable Login server? If this is checked, you will be hosting both the Login server on this machine. If this is not checked, you will need to host the Login server on another machine.");
		enableZoneServerBox.setToolTipText("Enable Zone server? If this is checked, you will be hosting both Zone server on this machine. If this is not checked, you will need to host the Zone server on another machine.");
		zoneServerIDLabel.setToolTipText("The ID of the Galaxy you wish to host on this machine, this should match the galaxy in the Galaxy table.");
		remoteLoginServerAddressLabel.setToolTipText("The IP address of the machine running the login server. This option is disabled if the login server is enabled on this machine.");
		remoteLoginServerPortLabel.setToolTipText("The broadcast port of the machine running the login server. This option is disabled if the login server is enabled on this machine.");
		databaseAddressLabel.setToolTipText("The IP Address of the machine that hosts your MySQL server.");
		databasePortLabel.setToolTipText("The port the MySQL server is broadcasting on.");
		databaseUsernameLabel.setToolTipText("The username you have setup for the server to login to your MySQL server.");
		databasePasswordLabel.setToolTipText("The password for the username you have setup for the server to login to your MySQL server.");
		databaseSchemaLabel.setToolTipText("The name of the database you executed the .SQL files in.");
		securePasswordBox.setToolTipText("Should the database store account passwords securely? Leaving this checked is recommended.");
		enableTraitorBox.setToolTipText("Should Bounty Hunters be allowed to pick up same faction cases?");
		accountAutoRegistrationBox.setToolTipText("Create a new account for users who connect for the first time? Disabling this means you will have to create accounts manually.");
		enableTanaabBox.setToolTipText("Enable travel to the planet Tanaab?");
		enableUnknownPlanetBox.setToolTipText("Enable travel to the unkown planet?");
		remoteAuthUsernameLabel.setToolTipText("The username you were given with this program.  If you weren't given one, leave this blank.");
		remoteAuthPasswordLabel.setToolTipText("The password you were given with this program.  If you weren't given one, leave this blank.");

		/* LABEL POSITIONING */
		securePasswordBox.setHorizontalTextPosition(SwingConstants.LEADING);
		accountAutoRegistrationBox.setHorizontalTextPosition(SwingConstants.LEADING);
		enableTanaabBox.setHorizontalTextPosition(SwingConstants.LEADING);
		enableUnknownPlanetBox.setHorizontalTextPosition(SwingConstants.LEADING);
		enableTraitorBox.setHorizontalTextPosition(SwingConstants.LEADING);

		/* KEY FUNCTIONALITY */
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		defaultSettingsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));

		/* EVENT BEHAVIOR */
		scriptDirectoryButton.setActionCommand("browse script");
		scriptDirectoryButton.addActionListener(this);
		webDirectoryButton.setActionCommand("browse web");
		webDirectoryButton.addActionListener(this);
		defaultSettingsMenuItem.setActionCommand("set default");
		defaultSettingsMenuItem.addActionListener(this);
		saveMenuItem.setActionCommand("save");
		saveMenuItem.addActionListener(this);
		exitMenuItem.setActionCommand("exit");
		exitMenuItem.addActionListener(this);
		serverSetupWindow.addWindowListener(this);
		zoneServerIDField.getDocument().addDocumentListener(this);
		remoteLoginServerPortField.getDocument().addDocumentListener(this);
		databasePortField.getDocument().addDocumentListener(this);
		zoneServerPortField.getDocument().addDocumentListener(this);
		loginServerPortField.getDocument().addDocumentListener(this);
		databaseUsernameField.getDocument().addDocumentListener(this);
		databaseSchemaField.getDocument().addDocumentListener(this);
		remoteLoginServerAddressField.getDocument().addDocumentListener(this);
		databaseAddressField.getDocument().addDocumentListener(this);
		databasePasswordField.getDocument().addDocumentListener(this);
		remoteAuthUsernameField.getDocument().addDocumentListener(this);
		remoteAuthPasswordField.getDocument().addDocumentListener(this);
		enableLoginServerBox.addItemListener(this);
		enableZoneServerBox.addItemListener(this);
		securePasswordBox.addItemListener(this);
		accountAutoRegistrationBox.addItemListener(this);
		enableTanaabBox.addItemListener(this);
		enableUnknownPlanetBox.addItemListener(this);
		enableTraitorBox.addItemListener(this);

		//Setup layout.
		GroupLayout generalPanelLayout = new GroupLayout(generalPanel);
		generalPanel.setLayout(generalPanelLayout);
		generalPanelLayout.setHorizontalGroup(
				generalPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(generalPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(webDirectoryLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(webDirectoryButton)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
						.addComponent(scriptDirectoryLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scriptDirectoryButton)
						.addGap(8, 8, 8))
		);
		generalPanelLayout.setVerticalGroup(
				generalPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(generalPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(webDirectoryButton)
						.addComponent(scriptDirectoryLabel)
						.addComponent(webDirectoryLabel)
						.addComponent(scriptDirectoryButton))
		);

		GroupLayout serverPanelLayout = new GroupLayout(serverPanel);
		serverPanel.setLayout(serverPanelLayout);
		serverPanelLayout.setHorizontalGroup(
				serverPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(serverPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(serverPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(zoneServerPortLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(loginServerPortLabel, GroupLayout.Alignment.TRAILING))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
								.addGroup(serverPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(loginServerPortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(zoneServerPortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addContainerGap(178, Short.MAX_VALUE))
		);
		serverPanelLayout.setVerticalGroup(
				serverPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, serverPanelLayout.createSequentialGroup()
						.addGroup(serverPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(loginServerPortLabel)
								.addComponent(loginServerPortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(serverPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(zoneServerPortLabel)
										.addComponent(zoneServerPortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addContainerGap())
		);

		GroupLayout clusterPanelLayout = new GroupLayout(clusterPanel);
		clusterPanel.setLayout(clusterPanelLayout);
		clusterPanelLayout.setHorizontalGroup(
				clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(clusterPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(zoneServerIDLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(remoteLoginServerAddressLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(remoteLoginServerPortLabel, GroupLayout.Alignment.TRAILING))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(clusterPanelLayout.createSequentialGroup()
												.addComponent(remoteLoginServerPortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addContainerGap(216, Short.MAX_VALUE))
												.addGroup(GroupLayout.Alignment.TRAILING, clusterPanelLayout.createSequentialGroup()
														.addGroup(clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
																.addComponent(zoneServerIDField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																.addComponent(remoteLoginServerAddressField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
																.addGroup(clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
																		.addComponent(enableZoneServerBox)
																		.addComponent(enableLoginServerBox)))))
		);
		clusterPanelLayout.setVerticalGroup(
				clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(clusterPanelLayout.createSequentialGroup()
						.addGroup(clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(enableLoginServerBox)
								.addComponent(zoneServerIDLabel)
								.addComponent(zoneServerIDField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(remoteLoginServerAddressLabel)
										.addComponent(remoteLoginServerAddressField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(enableZoneServerBox))
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(clusterPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(remoteLoginServerPortLabel)
												.addComponent(remoteLoginServerPortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addContainerGap(20, Short.MAX_VALUE))
		);

		GroupLayout generalTabLayout = new GroupLayout(generalTab);
		generalTab.setLayout(generalTabLayout);
		generalTabLayout.setHorizontalGroup(
				generalTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(generalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(serverPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(clusterPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		generalTabLayout.setVerticalGroup(
				generalTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(generalTabLayout.createSequentialGroup()
						.addComponent(generalPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(serverPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(clusterPanel, GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
		);

		GroupLayout securitySettingsPanelLayout = new GroupLayout(securitySettingsPanel);
		securitySettingsPanel.setLayout(securitySettingsPanelLayout);
		securitySettingsPanelLayout.setHorizontalGroup(
				securitySettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(securitySettingsPanelLayout.createSequentialGroup()
						.addComponent(securePasswordBox)
						.addContainerGap(218, Short.MAX_VALUE))
		);
		securitySettingsPanelLayout.setVerticalGroup(
				securitySettingsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, securitySettingsPanelLayout.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(securePasswordBox))
		);

		GroupLayout databaseTabLayout = new GroupLayout(databaseTab);
		databaseTab.setLayout(databaseTabLayout);
		databaseTabLayout.setHorizontalGroup(
				databaseTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(databaseTabLayout.createSequentialGroup()
						.addGroup(databaseTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(databaseAddressLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(databasePortLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(databaseUsernameLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(databasePasswordLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(databaseSchemaLabel, GroupLayout.Alignment.TRAILING))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(databaseTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(databaseAddressField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(databasePortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(databaseUsernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(databasePasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(databaseSchemaField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addGap(163, 163, 163))
										.addComponent(securitySettingsPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		databaseTabLayout.setVerticalGroup(
				databaseTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, databaseTabLayout.createSequentialGroup()
						.addGroup(databaseTabLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(databaseAddressLabel)
								.addComponent(databaseAddressField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(databaseTabLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(databasePortLabel)
										.addComponent(databasePortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(databaseTabLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(databaseUsernameLabel)
												.addComponent(databaseUsernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(databaseTabLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(databasePasswordLabel)
														.addComponent(databasePasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(databaseTabLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
																.addComponent(databaseSchemaLabel)
																.addComponent(databaseSchemaField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
																.addComponent(securitySettingsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);

		GroupLayout gameTabLayout = new GroupLayout(gameTab);
		gameTab.setLayout(gameTabLayout);
		gameTabLayout.setHorizontalGroup(
				gameTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(gameTabLayout.createSequentialGroup()
						.addGroup(gameTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(enableTraitorBox, GroupLayout.Alignment.TRAILING)
								.addComponent(enableUnknownPlanetBox, GroupLayout.Alignment.TRAILING)
								.addComponent(enableTanaabBox, GroupLayout.Alignment.TRAILING)
								.addComponent(accountAutoRegistrationBox, GroupLayout.Alignment.TRAILING))
								.addContainerGap(210, Short.MAX_VALUE))
		);
		gameTabLayout.setVerticalGroup(
				gameTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(gameTabLayout.createSequentialGroup()
						.addComponent(accountAutoRegistrationBox)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(enableTanaabBox)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(enableUnknownPlanetBox)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(enableTraitorBox)
						.addContainerGap(147, Short.MAX_VALUE))
		);

		GroupLayout authenticationTabLayout = new GroupLayout(authenticationTab);
		authenticationTab.setLayout(authenticationTabLayout);
		authenticationTabLayout.setHorizontalGroup(
				authenticationTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(authenticationTabLayout.createSequentialGroup()
						.addGroup(authenticationTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(remoteAuthPasswordLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(remoteAuthUsernameLabel, GroupLayout.Alignment.TRAILING))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(authenticationTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(remoteAuthUsernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(remoteAuthPasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addContainerGap(133, Short.MAX_VALUE))
		);
		authenticationTabLayout.setVerticalGroup(
				authenticationTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(authenticationTabLayout.createSequentialGroup()
						.addGroup(authenticationTabLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(remoteAuthUsernameLabel)
								.addComponent(remoteAuthUsernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(authenticationTabLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(remoteAuthPasswordLabel)
										.addComponent(remoteAuthPasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addContainerGap(193, Short.MAX_VALUE))
		);

		GroupLayout layout = new GroupLayout(serverSetupWindow.getContentPane());
		serverSetupWindow.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
		);

		//Add components.
		mainPanel.addTab("Server", null, generalTab, "Manager general server settings.");
		mainPanel.addTab("Database", null, databaseTab, "Manager server database connection settings.");
		mainPanel.addTab("Game", null, gameTab, "Manager game play settings.");
		mainPanel.addTab("Authentication", authenticationTab);
		fileMenu.add(saveMenuItem);
		fileMenu.add(defaultSettingsMenuItem);
		fileMenu.add(separatorMenuItem);
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		serverSetupWindow.setJMenuBar(menuBar);

		//Finish setting up window.
		serverSetupWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		serverSetupWindow.setResizable(false);
	}

	private void handleSafeExit() {

		//If there have been no changes to the current settings.
		if(isCurrentConfigSaved) {

			//If this is not server startup.
			if(!serverStartup) {

				//Alert the user that nothing will change until the configuration is reloaded.
				JOptionPane.showMessageDialog(serverSetupWindow, "Changes will not take place until next server startup.", "Notice", JOptionPane.INFORMATION_MESSAGE);
			}

			//Hide the window and let the server start.
			hideServerSetupWindow();
		} else {

			//Prompt to ensure there are no accidental screw ups.
			int chosenButton = JOptionPane.showConfirmDialog(serverSetupWindow, "The current settings have not been saved, if you exit now you will lose them. Are you sure you want to exit?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			//If the user selects yes.
			if(chosenButton == JOptionPane.YES_OPTION) {

				//If there is no pre-existing configuration file.
				if(isPreviousConfigSaved) {

					//The new settings have been lost, but we have a pre-existing Configuration.
					JOptionPane.showMessageDialog(serverSetupWindow, "Current changes to the settings have been dropped.", "Notice", JOptionPane.INFORMATION_MESSAGE);

					//Exit configuration.
					hideServerSetupWindow();
				} else {

					//Exit.
					JOptionPane.showMessageDialog(serverSetupWindow, "The server cannot continue without a configuration, exiting.", "Error", JOptionPane.ERROR_MESSAGE);

					//Hide window and exit, this is an unreachable condition after server startup,
					//so it's safe to use System.exit() instead of exiting safely via SWGGui
					hideServerSetupWindow();
					System.exit(0);
				}
			}
		}
	}

	private File handleBrowseFile() {
		//Instance variables.
		JFileChooser browser = new JFileChooser();
		int chosenButton = -1;
		File chosenFile = null;

		//Setup the dialog.
		browser.setDialogType(JFileChooser.OPEN_DIALOG);				//This sets the text in the dialog.
		browser.setMultiSelectionEnabled(false);						//This disables multi-selection.
		browser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);	//This sets what type of files we can select.

		//Open dialog, get the clicked button from the returned integer.
		chosenButton = browser.showOpenDialog(serverSetupWindow);

		//Handle the button selection.
		if(chosenButton == JFileChooser.APPROVE_OPTION) {

			//Assign the selected file to the return value.
			chosenFile = browser.getSelectedFile();
		}

		//Return the selected file.
		return chosenFile;
	}

	private void handleUpdateGUI() {

		//Set all data to defaults.
		currentScriptPath = new File(currentConfiguration.getScriptPath());
		currentWebPath = new File(currentConfiguration.getWebPath());

		//Set all component field defaults.
		zoneServerIDField.setText(Integer.toString(currentConfiguration.getZoneServerID()));
		loginServerPortField.setText(Integer.toString(currentConfiguration.getLoginServerPort()));
		zoneServerPortField.setText(Integer.toString(currentConfiguration.getZoneServerPort()));
		enableLoginServerBox.setSelected(currentConfiguration.isLoginServerEnabled());
		enableZoneServerBox.setSelected(currentConfiguration.isZoneServerEnabled());
		remoteLoginServerAddressField.setText(currentConfiguration.getRemoteLoginServerAddress());
		remoteLoginServerPortField.setText(Integer.toString(currentConfiguration.getRemoteLoginServerPort()));
		databaseAddressField.setText(currentConfiguration.getDatabaseAddress());
		databasePortField.setText(Integer.toString(currentConfiguration.getDatabasePort()));
		databaseUsernameField.setText(currentConfiguration.getDatabaseUsername());
		databasePasswordField.setText(currentConfiguration.getDatabasePassword());
		databaseSchemaField.setText(currentConfiguration.getDatabaseSchema());
		securePasswordBox.setSelected(currentConfiguration.isSecurePasswordsEnabled());
		accountAutoRegistrationBox.setSelected(currentConfiguration.isAutoAccountRegistrationEnabled());
		enableTanaabBox.setSelected(currentConfiguration.isTanaabEnabled());
		enableUnknownPlanetBox.setSelected(currentConfiguration.isUnknownPlanetEnabled());
		enableTraitorBox.setSelected(currentConfiguration.isSameFactionHuntingEnabled());
		remoteAuthUsernameField.setText(currentConfiguration.getRemoteAuthorizationUsername());
		remoteAuthUsernameField.setText(currentConfiguration.getRemoteAuthorizationPassword());
	}

	private void handleDefaultSettings() {
		currentConfiguration = new Config();
		handleUpdateGUI();
	}

	private void handleSaveSettings() {

		//Pop up a modal dialog displaying verification status.
		//This is done to prevent any changes to the interface while validation is in progress.

		if(isInputValid()) {
			//Close status dialog.

			//Parse/format the interface input into a Config object.
			parseConfig();

			//Save configuration.
			try {

				//Attempt to save the Config.
				saveConfiguration();

				//A configuration has been saved.
				isCurrentConfigSaved = true;

				//The current configuration has been saved.
				isPreviousConfigSaved = true;

			} catch (FileNotFoundException e) {

				//Could not create the file.
				JOptionPane.showMessageDialog(serverSetupWindow, "Unable to write " + Constants.CONFIGURATION_FILE_NAME + ", please ensure the file does not exist and is not write protected.", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {

				//Unable to write successfully.
				JOptionPane.showMessageDialog(serverSetupWindow, "Unable to write to " + Constants.CONFIGURATION_FILE_NAME + ", please ensure the file does not exist and is not write protected.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {

			//Input is invalid, display errors.
			showErrorListDialog("Invalid Settings", inputErrorString);
		}
	}

	private void parseConfig() {

		//Parse and format interface input into a config.
		try {
			currentConfiguration.setWebPath(currentWebPath.getAbsolutePath());
			currentConfiguration.setScriptPath(currentScriptPath.getAbsolutePath());
			currentConfiguration.setZoneServerPort(Integer.parseInt(zoneServerPortField.getText()));
			currentConfiguration.setLoginServerPort(Integer.parseInt(loginServerPortField.getText()));
			currentConfiguration.setZoneServerID(Integer.parseInt(zoneServerIDField.getText()));
			currentConfiguration.setLoginServerEnabled(enableLoginServerBox.isSelected());
			currentConfiguration.setZoneServerEnabled(enableZoneServerBox.isSelected());
			currentConfiguration.setRemoteLoginServerAddress(remoteLoginServerAddressField.getText());
			currentConfiguration.setRemoteLoginServerPort(Integer.parseInt(remoteLoginServerPortField.getText()));
			currentConfiguration.setDatabaseAddress(databaseAddressField.getText());
			currentConfiguration.setDatabasePort(Integer.parseInt(databasePortField.getText()));
			currentConfiguration.setDatabaseUsername(databaseUsernameField.getText());
			currentConfiguration.setDatabasePassword(parsePasswordString(databasePasswordField.getPassword()));
			currentConfiguration.setDatabaseSchema(databaseSchemaField.getText());
			currentConfiguration.setSecurePasswordsEnabled(securePasswordBox.isSelected());
			currentConfiguration.setAutoAccountRegistrationEnabled(accountAutoRegistrationBox.isSelected());
			currentConfiguration.setTanaabEnabled(enableTanaabBox.isSelected());
			currentConfiguration.setUnknownPlanetEnabled(enableUnknownPlanetBox.isSelected());
			currentConfiguration.setEnableSameFactionHunting(enableTraitorBox.isSelected());
			currentConfiguration.setRemoteAuthorizationUsername(remoteAuthUsernameField.getText());
			currentConfiguration.setRemoteAuthorizationPassword(parsePasswordString(remoteAuthPasswordField.getPassword()));
		} catch(NumberFormatException e) {

			//This isn't supposed to happen.
			JOptionPane.showMessageDialog(serverSetupWindow, "Unable to parse configuration from input. The input was validated successfully, but one field value is not an integer!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void actionPerformed(ActionEvent e) {
		//Get the selected menu item's command.
		String command = e.getActionCommand().toLowerCase();

		//Handle event.
		if(command.equals("browse web")) {

			//Open the JFileChooser.
			File webPath = handleBrowseFile();

			//If the user selected anything.
			if(webPath != null) {

				//Update current file.
				currentWebPath = webPath;

				//Settings are no longer saved.
				isCurrentConfigSaved = false;

			}
		} else if(command.equals("browse script")) {

			//Open the JFileChooser.
			File scriptPath = handleBrowseFile();

			//If the user selected anything.
			if(scriptPath != null) {

				//Update current file.
				currentScriptPath = scriptPath;

				//Settings are no longer saved.
				isCurrentConfigSaved = false;
			}
		} else if(command.equals("set default")) {

			//Prompt to ensure there are no accidental screw ups.
			int chosenButton = JOptionPane.showConfirmDialog(serverSetupWindow, "Are you sure you want to reset to default settings?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			//If the user selects yes.
			if(chosenButton == JOptionPane.YES_OPTION) {

				//Reset all components to default.
				//No need to setCurrentConfigurationSaved() here, the following method fires events.
				handleDefaultSettings();
			}
		} else if(command.equals("save")) {

			//Attempt to save configuration settings.
			handleSaveSettings();
		} else if(command.equals("exit")) {

			//Attempt to exit the JVM safely.
			handleSafeExit();
		}
	}

	public void changedUpdate(DocumentEvent e) {

		//The current config is no longer saved.
		isCurrentConfigSaved = false;
	}

	public void insertUpdate(DocumentEvent e) {

		//The current config is no longer saved.
		isCurrentConfigSaved = false;
	}

	public void removeUpdate(DocumentEvent e) {

		//The current config is no longer saved.
		isCurrentConfigSaved = false;
	}

	public void itemStateChanged(ItemEvent e) {

		//The current config is no longer saved.
		isCurrentConfigSaved = false;

		//If the user toggled the login server box.
		if(e.getSource() == enableLoginServerBox) {

			//Get the check box and status.
			JCheckBox loginBox = (JCheckBox) e.getSource();
			boolean isEnabled = loginBox.isSelected();

			//Toggle the appropriate fields.
			remoteLoginServerAddressField.setEnabled(!isEnabled);
			remoteLoginServerAddressField.setEditable(!isEnabled);
			remoteLoginServerPortField.setEnabled(!isEnabled);
			remoteLoginServerPortField.setEditable(!isEnabled);
			loginServerPortField.setEnabled(isEnabled);
			loginServerPortField.setEditable(isEnabled);

		} else if(e.getSource() == enableZoneServerBox) {

			//Get the check box and status.
			JCheckBox zoneBox = (JCheckBox) e.getSource();
			boolean isEnabled = zoneBox.isSelected();

			//Toggle the appropriate fields.
			zoneServerIDField.setEnabled(isEnabled);
			zoneServerIDField.setEditable(isEnabled);
			zoneServerPortField.setEnabled(isEnabled);
			zoneServerPortField.setEditable(isEnabled);
		}
	}

	public void windowClosing(WindowEvent e) {

		//Attempt to exit the JVM safely.
		handleSafeExit();
	}

	protected void saveConfiguration() throws FileNotFoundException, IOException {
		//Instance variables.
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(configurationFile));

		//Save the configuration.
		out.writeObject(currentConfiguration);

		//Close the output stream.
		out.close();
	}

	protected boolean isInputValid() {
		//Instance variables.
		boolean valid = true;
		StringBuffer errorString = new StringBuffer();

		//Validate web path.
		if(!isValidPath(currentWebPath)) {

			//Path is invalid.
			valid = false;

			//Determine the error string based on validity of the path.
			if(currentWebPath == null) {

				errorString.append(String.format("The selected %s path is invalid or non-existent, please select a path that exists.\n\n", "Web"));
			} else {

				errorString.append(String.format("The selected %s path(%s) is invalid or non-existent, please select a path that exists.\n\n", "Web", currentWebPath.getAbsolutePath()));
			}
		}

		//Validate script path.
		if(!isValidPath(currentScriptPath)) {

			//Path is invalid.
			valid = false;

			//Determine the error string based on validity of the path.
			if(currentScriptPath == null) {

				errorString.append(String.format("The selected %s path is invalid or non-existent, please select a path that exists.\n\n", "Web"));
			} else {

				errorString.append(String.format("The selected %s path(%s) is invalid or non-existent, please select a path that exists.\n\n", "Web", currentScriptPath.getAbsolutePath()));
			}
		}

		//Validate zone broadcast port.
		if(!isValidPort(zoneServerPortField.getText())) {

			//Port is invalid.
			valid = false;
			errorString.append(String.format("The input %s port(%s) is invalid or out of range, please select a real numerical value between %s and %s.\n\n", "Zone Broadcast", zoneServerPortField.getText(), Constants.CONFIGURATION_MINIMUM_PORT, Constants.CONFIGURATION_MAXIMUM_PORT));
		}

		//Validate login broadcast port.
		if(!isValidPort(loginServerPortField.getText())) {

			//Port is invalid.
			valid = false;
			errorString.append(String.format("The input %s port(%s) is invalid or out of range, please select a real numerical value between %s and %s.\n\n", "Login Broadcast", loginServerPortField.getText(), Constants.CONFIGURATION_MINIMUM_PORT, Constants.CONFIGURATION_MAXIMUM_PORT));
		}

		//Validate galaxy id.
		if(!isValidInputGalaxy(zoneServerIDField.getText())) {

			//ID is invalid.
			valid = false;
			errorString.append(String.format("The input Galaxy ID(%s) is invalid or out of range, please select a real numerical value between %s and %s.\n\n", zoneServerIDField.getText(), 1, Integer.MAX_VALUE));
		}

		//If the login server isn't enabled, we don't care about the remote login address/port.
		if(enableLoginServerBox.isSelected()) {

			//Validate remote login server address.
			if(!isValidInputString(remoteLoginServerAddressField.getText())) {

				//Address is invalid.
				valid = false;
				errorString.append(String.format("The input %s address(%s) is invalid, addresses cannot be blank or contain spaces.\n\n", "Remote Login Server", remoteLoginServerAddressField.getText()));
			}

			//Validate remote login server port.
			if(!isValidPort(remoteLoginServerPortField.getText())) {

				//Port is invalid.
				valid = false;
				errorString.append(String.format("The input %s port(%s) is invalid or out of range, please select a real numerical value between %s and %s.\n\n", "Remote Login Server", remoteLoginServerPortField.getText(), Constants.CONFIGURATION_MINIMUM_PORT, Constants.CONFIGURATION_MAXIMUM_PORT));
			}
		}

		//Validate database address.
		if(!isValidInputString(databaseAddressField.getText())) {

			//Address is invalid.
			valid = false;
			errorString.append(String.format("The input %s address(%s) is invalid, addresses cannot be blank or contain spaces.\n\n", "Database", databaseAddressField.getText()));
		}

		//Validate database port.
		if(!isValidPort(databasePortField.getText())) {

			//Port is invalid.
			valid = false;
			errorString.append(String.format("The input %s port(%s) is invalid or out of range, please select a real numerical value between %s and %s.\n\n", "Database", databasePortField.getText(), Constants.CONFIGURATION_MINIMUM_PORT, Constants.CONFIGURATION_MAXIMUM_PORT));
		}

		//Validate database username.
		if(!isValidInputString(databaseUsernameField.getText())) {

			//Username is invalid.
			valid = false;
			errorString.append(String.format("The input %s(%s) is invalid, the %s cannot be blank or contain spaces.\n\n", "Database Username", databaseUsernameField.getText(), "Database Username"));
		}

		//Validate database password.
		if(databasePasswordField.getPassword().length <= 0) {

			//Password is invalid.
			//No variable output here, the password is sensitive.
			valid = false;
			errorString.append("The input Database Password is invalid, the Database Password cannot be blank.\n\n");
		}

		//Validate database schema.
		String databaseSchema = databaseSchemaField.getText();
		if(!isValidInputString(databaseSchema) || databaseSchema.startsWith("/ ") || databaseSchema.equals("/")) {

			//Schema is invalid.
			valid = false;
			errorString.append(String.format("The input %s(%s) is invalid, the %s cannot be blank or contain spaces.\n\n", "Database Schema", databaseSchema, "Database Schema"));
		}

		//Validate zone/Login logic.
		if(!enableLoginServerBox.isSelected() && !enableZoneServerBox.isSelected()) {

			//Schema is invalid.
			valid = false;
			errorString.append("Both the Login Server and Zone Server are disabled, both cannot be disabled.");
		}

		//Update error string.
		inputErrorString = errorString.toString();

		//Return validity.
		return valid;
	}

	private boolean isValidInputString(String string) {
		//Instance variables.
		boolean valid = true;

		//Check string.
		if(string == null || string.isEmpty() || string.contains(" ")) {
			valid = false;
		}

		//Return validity.
		return valid;
	}

	private boolean isValidPath(File path) {
		//Instance variables.
		boolean valid = false;

		//Check the path.
		if(!path.getAbsolutePath().equals("") && path != null) {


			//Check if the directory exists as a directory.
			if(path.exists() && path.isDirectory()) {

				//Path is valid.
				valid = true;
			}
		}

		//Return validity.
		return valid;
	}

	private boolean isValidPort(String port) {
		//Instance variables.
		boolean valid = true;

		//Check
		if(!port.equals("") || port != null) {
			try {
				//Attempt to format the string into an integer.
				int iPort = Integer.parseInt(port);

				//Check if the port is within range.
				if(iPort < Constants.CONFIGURATION_MINIMUM_PORT || iPort > Constants.CONFIGURATION_MAXIMUM_PORT) {

					//Port is out of bounds.
					valid = false;
				}
			} catch(NumberFormatException e) {

				//Port isn't an int.
				valid = false;
			}
		} else {

			//Port isn't an int.
			valid = false;
		}

		//Return validity.
		return valid;
	}

	private boolean isValidInputGalaxy(String ID) {
		//Instance variables.
		boolean valid = true;

		//Check
		try {
			//Attempt to format the string into an integer.
			int iID = Integer.parseInt(ID);

			//Ensure it's within range.
			if(iID < 1) {

				//ID is out of bounds.
				valid = false;
			}
		} catch(NumberFormatException e) {

			//ID isn't an int.
			valid = false;
		}

		//Return validity.
		return valid;
	}

	protected String parsePasswordString(char[] fieldData) {
		//Local instance variables.
		StringBuffer buffer = new StringBuffer();

		for(char currentChar: fieldData) {
			buffer.append(currentChar);
		}

		//Return password.
		return buffer.toString();
	}

	private void showErrorListDialog(String dialogTitle, String errorString) {

		//Instance variables.
		JDialog dialog = new JDialog(serverSetupWindow, dialogTitle, true);
		JLabel iconLabel = new JLabel();
		JTextArea errorText = new JTextArea(errorString);
		JScrollPane scrollPane = new JScrollPane(errorText);

		//Setup window.
		dialog.setResizable(false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		//Setup components.
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
		iconLabel.setText("The following errors occured:");
		errorText.setColumns(20);
		errorText.setEditable(false);
		errorText.setRows(5);
		errorText.setWrapStyleWord(true);
		errorText.setLineWrap(true);
		scrollPane.setViewportView(errorText);

		//Setup layout.
		GroupLayout layout = new GroupLayout(dialog.getContentPane());
		dialog.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(iconLabel)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE))
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(iconLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
						.addContainerGap())
		);

		//Finish setup.
		dialog.pack();
		dialog.setVisible(true);
	}

	public void showServerSetupWindow() {
		
		//If this is the first run
		if(serverStartup) {

			//Show default settings.
			handleDefaultSettings();
		} else {

			//Show current settings.
			handleUpdateGUI();
		}

		//Either way, show the window.
		serverSetupWindow.pack();
		serverSetupWindow.setVisible(true);
	}

	public void hideServerSetupWindow() {

		//Hide the window.
		serverSetupWindow.setVisible(false);
		serverSetupWindow.dispose();
	}

	public void handleLoadConfiguration() throws FatalConfigException {

		boolean setupIncomplete = true;
		boolean launchServerSetup = true;

		while(setupIncomplete) {
			
			//If this is server startup.
			if(serverStartup) {
				
				//Attempt to load the configuration from the file.
				try {

					//Attempt to load from the file.
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(Constants.CONFIGURATION_FILE_NAME)));
					currentConfiguration = (Config) in.readObject();
					in.close();

					//This is no longer the first run of ServerSetup
					serverStartup = false;
					launchServerSetup = false;
					isCurrentConfigSaved = false;
					isPreviousConfigSaved = true;
				} catch(FileNotFoundException e) {

					//File doesn't exist, continue.
					System.out.println("No configuration found, launching setup.");
				} catch(InvalidClassException e) {

					//Config out of date, continue.
					System.out.println(String.format("The configuration in %s is out of date or invalid, launching setup.", Constants.CONFIGURATION_FILE_NAME));
				} catch(ClassNotFoundException e) {

					//Class not found, fatal.
					throw new FatalConfigException("The config class could not be loaded, ensure your directories are setup properly. Unable to continue.");
				} catch(IOException e) {

					//IO error, fatal.
					throw new FatalConfigException("An IO error has occurred, unable to continue.");
				}
			}
			
			//If we need to launch server setup.
			if(launchServerSetup) {

				//Show the server setup window.
				showServerSetupWindow();
			}

			//Exit loop.
			setupIncomplete = false;
		}
	}

	public Config getCurrentConfiguration () {
		return currentConfiguration;
	}
}
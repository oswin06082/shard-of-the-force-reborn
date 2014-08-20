package net.persistentworlds.toolkit.gui;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import net.persistentworlds.toolkit.Toolkit;
import net.persistentworlds.toolkit.ToolkitConstants;
import net.persistentworlds.toolkit.ToolkitUtilities;
import net.persistentworlds.toolkit.configuration.ConfigurationInterface;
import net.persistentworlds.toolkit.configuration.DatabaseConfiguration;

public class LoginWindow extends ListenerAdapater implements Runnable {

	//Constants.
	private static final long serialVersionUID = 1L;

	//Instance variables.
	private JDialog window;
	private JButton cancelButton;
	private JButton connectButton;
	private JTextField databaseHostField;
	private JPasswordField databasePasswordField;
	private JTextField databasePortField;
	private JTextField databaseSchemaField;
	private JTextField databaseUsernameField;
	private JButton defaultButton;
	private JCheckBox saveConnectionBox;
	private DatabaseConfiguration configuration;
	private boolean currentConfigurationChanged, previousConfigurationSaved;
	private Toolkit mainProgram;

	public LoginWindow(Toolkit mainProgramCallback) {
		try {
			mainProgram = mainProgramCallback;			
			SwingUtilities.invokeAndWait(this);
			
			//Attempt to load a saved configuration.
			DatabaseConfiguration loadedConfig = ConfigurationInterface.loadDatabaseConfiguration();
			
			//Decide if we need to display default or saved configuration.
			if(loadedConfig != null) {
				
				//Previous configuration saved.
				previousConfigurationSaved = true;
				
				//If it was loaded successfully, update current configuration.
				configuration = loadedConfig;				
			} else {

				//Previous configuration not saved.
				previousConfigurationSaved = false;				
				
				//If it wasn't, load default configuration.
				handleDefaultDatabaseConfiguration();				
			}
		} catch (InterruptedException e) {
			ToolkitUtilities.handleFatalException(e);
		} catch (InvocationTargetException e) {
			ToolkitUtilities.handleFatalException(e);
		}
	}

	public void run() {
		initComponents();
	}

	private void initComponents() {

		//Local instance variables.
		JPanel mainPanel = new JPanel();
		JLabel databaseHostLabel = new JLabel("Database Host:");
		JLabel databasePortLabel = new JLabel("Database Port:");
		JLabel databaseUsernameLabel = new JLabel("Database Username:");
		JLabel databasePasswordLabel = new JLabel("Database Password:");
		JLabel databaseSchemaLabel = new JLabel("Database Schema:");

		//Instance variables.
		window = new JDialog();
		databaseHostField = new JTextField(10);
		databasePortField = new JTextField(5);
		databaseUsernameField = new JTextField(10);
		databasePasswordField = new JPasswordField(10);
		databaseSchemaField = new JTextField(10);
		saveConnectionBox = new JCheckBox("Save Connection", true);
		defaultButton = new JButton("Default");
		connectButton = new JButton("Connect");
		cancelButton = new JButton("Cancel");

		//Setup window.
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.setTitle("Persistent Worlds Editor ToolKit v" + ToolkitConstants.VERSION_NUMBER);
		window.setModal(true);
		window.setResizable(false);

		//Setup border.
		mainPanel.setBorder(BorderFactory.createTitledBorder("Database Settings"));

		//Setup layout.
		GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		mainPanelLayout.setHorizontalGroup(
				mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(mainPanelLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(databaseHostLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(databaseUsernameLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(databasePasswordLabel, GroupLayout.Alignment.TRAILING)
								.addComponent(databaseSchemaLabel, GroupLayout.Alignment.TRAILING))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(mainPanelLayout.createSequentialGroup()
												.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(databaseSchemaField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(databaseHostField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
														.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
																.addGroup(mainPanelLayout.createSequentialGroup()
																		.addComponent(databasePortLabel)
																		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(databasePortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																		.addComponent(saveConnectionBox)))
																		.addComponent(databasePasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addComponent(databaseUsernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																		.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		mainPanelLayout.setVerticalGroup(
				mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(mainPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(mainPanelLayout.createSequentialGroup()
										.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
												.addComponent(databaseHostLabel)
												.addComponent(databaseHostField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
												.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(databaseUsernameLabel)
														.addComponent(databaseUsernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
														.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
																.addComponent(databasePasswordLabel)
																.addComponent(databasePasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
																.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
																		.addComponent(databaseSchemaLabel)
																		.addComponent(databaseSchemaField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
																		.addGroup(mainPanelLayout.createSequentialGroup()
																				.addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
																						.addComponent(databasePortLabel)
																						.addComponent(databasePortField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																						.addGap(67, 67, 67)
																						.addComponent(saveConnectionBox)))
																						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		GroupLayout layout = new GroupLayout(window.getContentPane());
		window.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGap(72, 72, 72)
						.addComponent(defaultButton)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(connectButton)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(cancelButton)
						.addContainerGap(67, Short.MAX_VALUE))
						.addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(mainPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(cancelButton)
								.addComponent(connectButton)
								.addComponent(defaultButton))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		//Setup components.
		defaultButton.addActionListener(this);
		connectButton.addActionListener(this);
		cancelButton.addActionListener(this);
		databaseHostField.getDocument().addDocumentListener(this);
		databasePasswordField.getDocument().addDocumentListener(this);
		databasePortField.getDocument().addDocumentListener(this);
		databaseSchemaField.getDocument().addDocumentListener(this);
		databaseUsernameField.getDocument().addDocumentListener(this);
		window.addWindowListener(this);
	}

	public void showWindow() {
		window.pack();
		window.setVisible(true);
	}

	public void hideWindow() {
		window.setVisible(false);
		window.dispose();
	}

	public void actionPerformed(ActionEvent e) {

		//Get source.
		Object source = e.getSource();

		if(source.equals(defaultButton)) {

			//Set all components to default values.
			handleDefaultDatabaseConfiguration();
			
		} else if(source.equals(connectButton)) {

			//Verify input configuration.
			//Parse configuration.
			//Attempt test connection to verify configuration.
			//if connection successful
				//Save configuration.
				//Exit interface.
			//else if connection isn't successful
				//Display errors.
		} else if(source.equals(cancelButton)) {

			//Exit. In the future, go back to main screen.
			handleSafeExit();
		}
	}

	public void windowClosing(WindowEvent e) {
		handleSafeExit();
	}
	
	private void handleSafeExit() {

		//If there were any changes
		if(currentConfigurationChanged) {
			
			//Display a warning about exiting.
			int chosenButton = JOptionPane.showConfirmDialog(window, "The current settings have not been saved, if you exit now you will lose them. Are you sure you want to exit?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	
			//If the user selects yes.
			if(chosenButton == JOptionPane.YES_OPTION) {
	
				//Check if the user has even changed anything.
				if(previousConfigurationSaved) {
	
					//The new settings have been lost, but we have a pre-existing Configuration.
					JOptionPane.showMessageDialog(window, "Current changes to the settings have been dropped.", "Notice", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		}

		//Exit window.
		hideWindow();

		//In the future, go back to main screen. For now, just exit.
		mainProgram.safeExit();
	}
	
	private void handleDefaultDatabaseConfiguration() {
		
		//If the configuration has changed in the first place.
		if(currentConfigurationChanged) {		

			//Update the configuration.
			configuration = new DatabaseConfiguration();

			//Set defaults.
			updateInterfaceLoadedConfiguration(configuration, true);
			
			//Update the interface.
			updateConfigurationChanged();
		}
	}

	/*private String getInputValidationErrors() {

		//Instance variables.
		String validationString = ToolkitConstants.LOGIN_WINDOW_INPUT_VALID;
		
		//Validate database address.
		if(ToolkitUtilities.isStringEmpty(databaseHostField.getText())) {

			//Address is invalid.
			//errorString.append(String.format("The input %s address(%s) is invalid, addresses cannot be blank or contain spaces.\n\n", "Database", databaseAddressField.getText()));
			validationString = validationString + String.format("The input %s address(%s) is invalid, addresses cannot be blank or contain spaces.\n\n", "Database", databaseHostField.getText());
		}

		//Validate database port.
		if(!ToolkitUtilities.isStringValidPort(databasePortField.getText())) {

			//Port is invalid.
			//errorString.append(String.format("The input %s port(%s) is invalid or out of range, please select a real numerical value between %s and %s.\n\n", "Database", databasePortField.getText(), Constants.CONFIGURATION_MINIMUM_PORT, Constants.CONFIGURATION_MAXIMUM_PORT));
		}

		//Validate database username.
		if(ToolkitUtilities.isStringEmpty(databaseUsernameField.getText())) {

			//Username is invalid.
			//errorString.append(String.format("The input %s(%s) is invalid, the %s cannot be blank or contain spaces.\n\n", "Database Username", databaseUsernameField.getText(), "Database Username"));
		}

		//Validate database password.
		if(databasePasswordField.getPassword().length <= 0) {

			//Password is invalid.
			//No variable output here, the password is sensitive.
			//errorString.append("The input Database Password is invalid, the Database Password cannot be blank.\n\n");
		}

		//Validate database schema.
		String databaseSchema = databaseSchemaField.getText();
		if(ToolkitUtilities.isStringEmpty(databaseSchema) || databaseSchema.startsWith("/ ") || databaseSchema.equals("/")) {

			//Schema is invalid.
			//errorString.append(String.format("The input %s(%s) is invalid, the %s cannot be blank or contain spaces.\n\n", "Database Schema", databaseSchema, "Database Schema"));
		}

		//Return validity.
		return validationString;	
	}*/
	
	private void updateConfigurationChanged() {
		
		//If the configuration was changed.
		if(currentConfigurationChanged) {
			
			//It's no longer changed.
			currentConfigurationChanged = false;
		} else {
			
			//Else, it's changed.
			currentConfigurationChanged = true;
		}
		
		//Update default button.
		defaultButton.setEnabled(currentConfigurationChanged);
	}
	
	private void updateInterfaceLoadedConfiguration(DatabaseConfiguration config, boolean saveConnectionBoxSelected) {
		
		//Set defaults.
		saveConnectionBox.setSelected(saveConnectionBoxSelected);
		databaseHostField.setText(config.getDatabaseHost());
		databasePortField.setText(Integer.toString(config.getDatabasePort()));
		databaseUsernameField.setText(config.getDatabaseUserName());
		databasePasswordField.setText(config.getDatabasePassword());
		databaseSchemaField.setText(config.getDatabaseSchema());
	}
	
	public void insertUpdate(DocumentEvent e) {

		//The current configuration has been changed.
		currentConfigurationChanged = true;
	}

	public void removeUpdate(DocumentEvent e) {
		
		//The current configuration has been changed.
		currentConfigurationChanged = true;
	}
}
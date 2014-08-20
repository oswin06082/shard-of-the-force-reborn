/*
 * ConfigInterface.java
 *
 * Created on December 15, 2008, 9:43 PM
 */



/**
 *
 * @author  Ben
 */
public class ConfigInterface extends javax.swing.JFrame {

    /** Creates new form ConfigInterface */
    public ConfigInterface() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JTabbedPane mainPanel = new javax.swing.JTabbedPane();
        javax.swing.JPanel generalTab = new javax.swing.JPanel();
        javax.swing.JPanel generalPanel = new javax.swing.JPanel();
        javax.swing.JLabel webDirectoryLabel = new javax.swing.JLabel();
        webDirectoryButton = new javax.swing.JButton();
        javax.swing.JLabel scriptDirectoryLabel = new javax.swing.JLabel();
        scriptDirectoryButton = new javax.swing.JButton();
        javax.swing.JPanel serverPanel = new javax.swing.JPanel();
        javax.swing.JLabel zoneServerPortLabel = new javax.swing.JLabel();
        javax.swing.JLabel loginServerPortLabel = new javax.swing.JLabel();
        loginServerPortField = new javax.swing.JFormattedTextField();
        zoneServerPortField = new javax.swing.JFormattedTextField();
        javax.swing.JPanel clusterPanel = new javax.swing.JPanel();
        enableLoginServerBox = new javax.swing.JCheckBox();
        javax.swing.JLabel zoneServerIDLabel = new javax.swing.JLabel();
        zoneServerIDField = new javax.swing.JFormattedTextField();
        javax.swing.JLabel remoteLoginServerAddressLabel = new javax.swing.JLabel();
        javax.swing.JLabel remoteLoginServerPortLabel = new javax.swing.JLabel();
        remoteLoginServerAddressField = new javax.swing.JFormattedTextField();
        remoteLoginServerPortField = new javax.swing.JFormattedTextField();
        enableZoneServerBox = new javax.swing.JCheckBox();
        javax.swing.JPanel databaseTab = new javax.swing.JPanel();
        javax.swing.JLabel databaseAddressLabel = new javax.swing.JLabel();
        javax.swing.JLabel databasePortLabel = new javax.swing.JLabel();
        javax.swing.JLabel databaseUsernameLabel = new javax.swing.JLabel();
        javax.swing.JLabel databasePasswordLabel = new javax.swing.JLabel();
        javax.swing.JLabel databaseSchemaLabel = new javax.swing.JLabel();
        databaseAddressField = new javax.swing.JFormattedTextField();
        databasePortField = new javax.swing.JFormattedTextField();
        databaseUsernameField = new javax.swing.JTextField();
        databasePasswordField = new javax.swing.JPasswordField();
        databaseSchemaField = new javax.swing.JTextField();
        javax.swing.JPanel securitySettingsPanel = new javax.swing.JPanel();
        securePasswordBox = new javax.swing.JCheckBox();
        javax.swing.JPanel gameTab = new javax.swing.JPanel();
        accountAutoRegistrationBox = new javax.swing.JCheckBox();
        enableTanaabBox = new javax.swing.JCheckBox();
        enableUnknownPlanetBox = new javax.swing.JCheckBox();
        enableTraitorBox = new javax.swing.JCheckBox();
        authenticationTab = new javax.swing.JPanel();
        remoteAuthUsernameLabel = new javax.swing.JLabel();
        remoteAuthPasswordLabel = new javax.swing.JLabel();
        remoteAuthUsernameField = new javax.swing.JTextField();
        remoteAuthPasswordField = new javax.swing.JPasswordField();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveMenuItem = new javax.swing.JMenuItem();
        defaultSettingsMenuItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator separatorMenuItem = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Persistent Worlds Server Configuration");

        mainPanel.setPreferredSize(new java.awt.Dimension(400, 283));

        generalPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General Settings"));

        webDirectoryLabel.setText("Web directory:");
        webDirectoryLabel.setToolTipText("The directory where the web status page will be stored.");

        webDirectoryButton.setText("Browse");

        scriptDirectoryLabel.setText("Script directory:");
        scriptDirectoryLabel.setToolTipText("The directory where server script files are stored.");

        scriptDirectoryButton.setText("Browse");

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(webDirectoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webDirectoryButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addComponent(scriptDirectoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scriptDirectoryButton)
                .addGap(8, 8, 8))
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(webDirectoryButton)
                .addComponent(scriptDirectoryLabel)
                .addComponent(webDirectoryLabel)
                .addComponent(scriptDirectoryButton))
        );

        serverPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Server Settings"));

        zoneServerPortLabel.setText("Zone Broadcast Port:");
        zoneServerPortLabel.setToolTipText("The UDP port to accept Zone connections. Players on your server will need to change this in the client config file.");

        loginServerPortLabel.setText("Login Broadcast Port:");
        loginServerPortLabel.setToolTipText("The UDP port to accept Login connections.");

        loginServerPortField.setColumns(10);

        zoneServerPortField.setColumns(10);

        javax.swing.GroupLayout serverPanelLayout = new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(zoneServerPortLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(loginServerPortLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loginServerPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zoneServerPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(178, Short.MAX_VALUE))
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, serverPanelLayout.createSequentialGroup()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginServerPortLabel)
                    .addComponent(loginServerPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zoneServerPortLabel)
                    .addComponent(zoneServerPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        clusterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Cluster Settings"));
        clusterPanel.setPreferredSize(new java.awt.Dimension(395, 114));

        enableLoginServerBox.setText("Enable Login Server");
        enableLoginServerBox.setToolTipText("Enable login server? If this is checked, you will be hosting both the login and zone server on this machine. If this is not checked, you will need to host the Login server on another machine.");

        zoneServerIDLabel.setText("Galaxy ID:");
        zoneServerIDLabel.setToolTipText("The ID of the Galaxy you wish to host on this machine, this should match the galaxy in the Galaxy table.");

        zoneServerIDField.setColumns(2);

        remoteLoginServerAddressLabel.setText("TEMP Server Address:");
        remoteLoginServerAddressLabel.setToolTipText("PLACEHOLDER TOOLTIP.");

        remoteLoginServerPortLabel.setText("TEMP Server Port:");
        remoteLoginServerPortLabel.setToolTipText("PLACEHOLDER TOOLTIP.");

        remoteLoginServerAddressField.setColumns(10);
        remoteLoginServerAddressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remoteLoginServerAddressFieldActionPerformed(evt);
            }
        });

        remoteLoginServerPortField.setColumns(5);

        enableZoneServerBox.setText("Enable Zone Server");

        javax.swing.GroupLayout clusterPanelLayout = new javax.swing.GroupLayout(clusterPanel);
        clusterPanel.setLayout(clusterPanelLayout);
        clusterPanelLayout.setHorizontalGroup(
            clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clusterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(zoneServerIDLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(remoteLoginServerAddressLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(remoteLoginServerPortLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(clusterPanelLayout.createSequentialGroup()
                        .addComponent(remoteLoginServerPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(216, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clusterPanelLayout.createSequentialGroup()
                        .addGroup(clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(zoneServerIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(remoteLoginServerAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addGroup(clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(enableZoneServerBox)
                            .addComponent(enableLoginServerBox)))))
        );
        clusterPanelLayout.setVerticalGroup(
            clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clusterPanelLayout.createSequentialGroup()
                .addGroup(clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enableLoginServerBox)
                    .addComponent(zoneServerIDLabel)
                    .addComponent(zoneServerIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remoteLoginServerAddressLabel)
                    .addComponent(remoteLoginServerAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enableZoneServerBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remoteLoginServerPortLabel)
                    .addComponent(remoteLoginServerPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout generalTabLayout = new javax.swing.GroupLayout(generalTab);
        generalTab.setLayout(generalTabLayout);
        generalTabLayout.setHorizontalGroup(
            generalTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(generalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(serverPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(clusterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        generalTabLayout.setVerticalGroup(
            generalTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalTabLayout.createSequentialGroup()
                .addComponent(generalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(serverPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clusterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
        );

        mainPanel.addTab("Server", null, generalTab, "Manager general server settings.");

        databaseTab.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Settings"));

        databaseAddressLabel.setText("Database Address:");
        databaseAddressLabel.setToolTipText("The IP Address of the machine that hosts your MySQL server.");

        databasePortLabel.setText("Database Port:");
        databasePortLabel.setToolTipText("The port the MySQL server is broadcasting on.");

        databaseUsernameLabel.setText("Database Username:");
        databaseUsernameLabel.setToolTipText("The username you have setup for the server to login to your MySQL server.");

        databasePasswordLabel.setText("Database Password:");
        databasePasswordLabel.setToolTipText("The password for the username you have setup for the server to login to your MySQL server.");

        databaseSchemaLabel.setText("Database Schema:");
        databaseSchemaLabel.setToolTipText("The name of the database you executed the .SQL files in.");

        databaseAddressField.setColumns(10);

        databasePortField.setColumns(5);

        databaseUsernameField.setColumns(10);

        databasePasswordField.setColumns(10);

        databaseSchemaField.setColumns(10);

        securitySettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Security Settings"));

        securePasswordBox.setSelected(true);
        securePasswordBox.setText("Store Passwords Securely:");
        securePasswordBox.setToolTipText("Should the database store account passwords securely? Leaving this checked is recommended.");
        securePasswordBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout securitySettingsPanelLayout = new javax.swing.GroupLayout(securitySettingsPanel);
        securitySettingsPanel.setLayout(securitySettingsPanelLayout);
        securitySettingsPanelLayout.setHorizontalGroup(
            securitySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(securitySettingsPanelLayout.createSequentialGroup()
                .addComponent(securePasswordBox)
                .addContainerGap(218, Short.MAX_VALUE))
        );
        securitySettingsPanelLayout.setVerticalGroup(
            securitySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, securitySettingsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(securePasswordBox))
        );

        javax.swing.GroupLayout databaseTabLayout = new javax.swing.GroupLayout(databaseTab);
        databaseTab.setLayout(databaseTabLayout);
        databaseTabLayout.setHorizontalGroup(
            databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseTabLayout.createSequentialGroup()
                .addGroup(databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(databaseAddressLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(databasePortLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(databaseUsernameLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(databasePasswordLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(databaseSchemaLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(databaseAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(databasePortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(databaseUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(databasePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(databaseSchemaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(163, 163, 163))
            .addComponent(securitySettingsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        databaseTabLayout.setVerticalGroup(
            databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databaseTabLayout.createSequentialGroup()
                .addGroup(databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseAddressLabel)
                    .addComponent(databaseAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databasePortLabel)
                    .addComponent(databasePortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseUsernameLabel)
                    .addComponent(databaseUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databasePasswordLabel)
                    .addComponent(databasePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(databaseTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseSchemaLabel)
                    .addComponent(databaseSchemaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addComponent(securitySettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.addTab("Database", null, databaseTab, "Manager server database connection settings.");

        gameTab.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Settings"));

        accountAutoRegistrationBox.setText("Account auto-registration:");
        accountAutoRegistrationBox.setToolTipText("Create a new account for users who connect for the first time? Disabling this means you will have to create accounts manually.");
        accountAutoRegistrationBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        enableTanaabBox.setText("Tanaab Active:");
        enableTanaabBox.setToolTipText("Enable travel to the planet Tanaab?");
        enableTanaabBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        enableUnknownPlanetBox.setText("Unknown Planet Active:");
        enableUnknownPlanetBox.setToolTipText("Enable travel to the unkown planet?");
        enableUnknownPlanetBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        enableTraitorBox.setText("Same Faction Hunting Allowed:");
        enableTraitorBox.setToolTipText("Should Bounty Hunters be allowed to pick up same faction cases?");
        enableTraitorBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout gameTabLayout = new javax.swing.GroupLayout(gameTab);
        gameTab.setLayout(gameTabLayout);
        gameTabLayout.setHorizontalGroup(
            gameTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gameTabLayout.createSequentialGroup()
                .addGroup(gameTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(enableTraitorBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(enableUnknownPlanetBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(enableTanaabBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(accountAutoRegistrationBox, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(210, Short.MAX_VALUE))
        );
        gameTabLayout.setVerticalGroup(
            gameTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gameTabLayout.createSequentialGroup()
                .addComponent(accountAutoRegistrationBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enableTanaabBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enableUnknownPlanetBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enableTraitorBox)
                .addContainerGap(147, Short.MAX_VALUE))
        );

        mainPanel.addTab("Game", null, gameTab, "Manager game play settings.");

        authenticationTab.setBorder(javax.swing.BorderFactory.createTitledBorder("Authentication Settings"));

        remoteAuthUsernameLabel.setText("Authentication Server Username:");
        remoteAuthUsernameLabel.setToolTipText("The username you were given with this program.  If you weren't given one, leave this blank.");

        remoteAuthPasswordLabel.setText("Authentication Server Password:");
        remoteAuthPasswordLabel.setToolTipText("The password you were given with this program.  If you weren't given one, leave this blank.");

        remoteAuthUsernameField.setColumns(10);

        remoteAuthPasswordField.setColumns(10);

        javax.swing.GroupLayout authenticationTabLayout = new javax.swing.GroupLayout(authenticationTab);
        authenticationTab.setLayout(authenticationTabLayout);
        authenticationTabLayout.setHorizontalGroup(
            authenticationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(authenticationTabLayout.createSequentialGroup()
                .addGroup(authenticationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remoteAuthPasswordLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(remoteAuthUsernameLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(authenticationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(remoteAuthUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remoteAuthPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(133, Short.MAX_VALUE))
        );
        authenticationTabLayout.setVerticalGroup(
            authenticationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(authenticationTabLayout.createSequentialGroup()
                .addGroup(authenticationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remoteAuthUsernameLabel)
                    .addComponent(remoteAuthUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(authenticationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(remoteAuthPasswordLabel)
                    .addComponent(remoteAuthPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(193, Short.MAX_VALUE))
        );

        mainPanel.addTab("Authentication", authenticationTab);

        fileMenu.setText("File");

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        defaultSettingsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        defaultSettingsMenuItem.setText("Default settings");
        fileMenu.add(defaultSettingsMenuItem);
        fileMenu.add(separatorMenuItem);

        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void remoteLoginServerAddressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remoteLoginServerAddressFieldActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_remoteLoginServerAddressFieldActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConfigInterface().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox accountAutoRegistrationBox;
    private javax.swing.JPanel authenticationTab;
    private javax.swing.JFormattedTextField databaseAddressField;
    private javax.swing.JPasswordField databasePasswordField;
    private javax.swing.JFormattedTextField databasePortField;
    private javax.swing.JTextField databaseSchemaField;
    private javax.swing.JTextField databaseUsernameField;
    private javax.swing.JMenuItem defaultSettingsMenuItem;
    private javax.swing.JCheckBox enableLoginServerBox;
    private javax.swing.JCheckBox enableTanaabBox;
    private javax.swing.JCheckBox enableTraitorBox;
    private javax.swing.JCheckBox enableUnknownPlanetBox;
    private javax.swing.JCheckBox enableZoneServerBox;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JFormattedTextField loginServerPortField;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPasswordField remoteAuthPasswordField;
    private javax.swing.JLabel remoteAuthPasswordLabel;
    private javax.swing.JTextField remoteAuthUsernameField;
    private javax.swing.JLabel remoteAuthUsernameLabel;
    private javax.swing.JFormattedTextField remoteLoginServerAddressField;
    private javax.swing.JFormattedTextField remoteLoginServerPortField;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton scriptDirectoryButton;
    private javax.swing.JCheckBox securePasswordBox;
    private javax.swing.JButton webDirectoryButton;
    private javax.swing.JFormattedTextField zoneServerIDField;
    private javax.swing.JFormattedTextField zoneServerPortField;
    // End of variables declaration//GEN-END:variables

}

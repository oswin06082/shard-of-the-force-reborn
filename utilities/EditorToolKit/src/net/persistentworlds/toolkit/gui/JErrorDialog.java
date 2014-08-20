package net.persistentworlds.toolkit.gui;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Create a modal dialog to display a list of errors, usually unrelated errors for 
 * verification of GUI input. Note that all methods update the interface automatically.
 * @author Interesting
 *
 *
 */
public class JErrorDialog implements Runnable, ActionListener {

	//Instance variables.
	private Window parent;
	private String errorString;
	private String dialogTitle;
	private JDialog dialog;
	private JTextArea errorText;

	public JErrorDialog(Window parent, String dialogTitle) throws InterruptedException, InvocationTargetException {
		this.parent = parent;
		this.dialogTitle = dialogTitle;
		errorString = "";
		SwingUtilities.invokeAndWait(this);
	}

	public void run() {
		initComponents();
	}

	private void initComponents() {

		//Instance variables.
		dialog = new JDialog(parent, dialogTitle, Dialog.ModalityType.APPLICATION_MODAL);
		errorText = new JTextArea(errorString);
		
		//Local instance variables.
		JLabel iconLabel = new JLabel();
		JScrollPane scrollPane = new JScrollPane(errorText);
		JButton okayButton = new JButton();

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
		okayButton.setText("Okay");
		okayButton.addActionListener(this);
		okayButton.setActionCommand("okay");

		//Setup layout
		GroupLayout layout = new GroupLayout(dialog.getContentPane());
		dialog.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
								.addComponent(iconLabel)
								.addComponent(okayButton, GroupLayout.Alignment.TRAILING))
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(iconLabel)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
						.addComponent(okayButton)
						.addContainerGap())
		);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("okay")) {
			hideDialog();
		}
	}

	public void addError(String error) {

		//Check for two new lines
		if(error.endsWith("\n\n")) {

			//Do nothing, line is correct.
			//This is the only way to get this check to work properly.
		} else if(error.endsWith("\n")) {

			//Line has one newline, add another.
			error = error + "\n";
		} else {

			//Line has no new lines, add two.
			error = error + "\n\n";
		}

		//Update error String.
		errorString = errorString + error;

		//Update GUI.
		errorText.setText(errorString);
	}

	public String getErrors() {
		return errorString;
	}

	public void clearErrors() {
		errorString = "";
		errorText.setText("");
	}

	public void showDialog() {
		dialog.pack();
		dialog.setVisible(true);
	}

	public void hideDialog() {
		dialog.setVisible(false);
		dialog.dispose();
	}
}

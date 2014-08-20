package net.persistentworlds.toolkit.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * An abstract adapter class for receiving events from several Listeners. This class serves as a convenience for creating listener objects.
 * Extend this class and Override the methods for the events you care about.
 * @author Interesting
 *
 */
public abstract class ListenerAdapater implements WindowListener, ActionListener, DocumentListener {

	public void actionPerformed(ActionEvent e) {
	}

	public void changedUpdate(DocumentEvent e) {
	}

	public void insertUpdate(DocumentEvent e) {
	}

	public void removeUpdate(DocumentEvent e) {
	}

	public void windowActivated(WindowEvent arg0) {
	}

	public void windowClosed(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}
}
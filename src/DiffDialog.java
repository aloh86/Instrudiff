import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/************************************************************************
 * DiffDialog creates the dialog for choosing two files for the insturment
 * setting diff.
 * @author Alex Oh
 *
 */
public class DiffDialog extends JDialog implements ActionListener, DocumentListener {
	
	private static final long serialVersionUID = 1L;
	private int dialogChoice = 10;
	
	JTextField fileOneField;
	JTextField fileTwoField;
	
	JButton diffButton;
	JButton browseButtonOne;
	JButton browseButtonTwo;
	
	final JFileChooser fc;
	
	String [] filePaths;
	
	/************************************************************************
	 * Initializes and creates swing components
	 */
	DiffDialog() {
		fileOneField = new JTextField( 10 );
		fileOneField.addActionListener( this );
		fileOneField.getDocument().addDocumentListener( this );
		
		fileTwoField = new JTextField( 10 );
		fileTwoField.addActionListener( this );
		fileTwoField.getDocument().addDocumentListener( this );
		
		diffButton = new JButton( "Start Diff" );
		diffButton.addActionListener( this );
		diffButton.setEnabled( false );
		
		browseButtonOne = new JButton( "..." );
		browseButtonOne.addActionListener( this );
		browseButtonOne.setActionCommand( "browseOne" );
		
		browseButtonTwo = new JButton( "..." );
		browseButtonTwo.addActionListener( this );
		browseButtonTwo.setActionCommand( "browseTwo" );
		
		fc = new JFileChooser();
		
		filePaths = new String[ 2 ];
		
		createUI();
		this.pack();
		this.setLocationRelativeTo( null );
		this.setModal( true );
		this.setTitle( "Open Files for Diff" );
		this.setResizable( false );
	}
	
	/************************************************************************
	 * Creates the user interface.
	 */
	private void createUI() {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.X_AXIS ) );
		
		// create the file path text fields, and browse buttons
		final int HSTRUT_SIZE = 5;
		Component hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		centerPanel.add( hStrut );
		centerPanel.add( new JLabel( "File 1:" ) );
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		centerPanel.add( hStrut );
		centerPanel.add( fileOneField );
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		centerPanel.add( hStrut );
		centerPanel.add( browseButtonOne );
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE + 10 );
		centerPanel.add( hStrut );
		centerPanel.add( new JLabel( "File 2:" ) );
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		centerPanel.add( hStrut );
		centerPanel.add( fileTwoField );
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		centerPanel.add( hStrut );
		centerPanel.add( browseButtonTwo );
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		
		// add the file path fields and browse buttons to a panel that will
		// hold it.
		JPanel centerPanelHold = new JPanel();
		centerPanelHold.setLayout( new FlowLayout() );
		centerPanelHold.add( centerPanel );
		add( centerPanelHold, BorderLayout.CENTER );
		
		// add the "Start Diff" button to a panel
		JPanel southPanel = new JPanel();
		southPanel.setLayout( new FlowLayout() );
		southPanel.add( diffButton );
		
		// add the button panel to the souther part of the dialog
		add( southPanel, BorderLayout.SOUTH );
		
		// create space around the edges of the dialog
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		add( hStrut, BorderLayout.NORTH );
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		add( hStrut, BorderLayout.WEST );
		hStrut = Box.createHorizontalStrut( HSTRUT_SIZE );
		add( hStrut, BorderLayout.EAST );
	}

	/************************************************************************
	 * Handles event calls for the user interaction with swing components.
	 */
	@Override
	public void actionPerformed( ActionEvent e ) {
		// get the action that was performed
		String actionCommand = e.getActionCommand();
		
		if ( actionCommand.equals( "Start Diff" ) ) {
			this.dispose();
		}
		
		// both browse buttons do the same thing: open the file chooser
		// dialog. If the user has selected to open a file, save the 
		// file path.
		else if ( actionCommand.equals( "browseOne" ) ) {
			int returnVal = fc.showOpenDialog( this );
			
			if ( returnVal == JFileChooser.APPROVE_OPTION ) {
				dialogChoice = JFileChooser.APPROVE_OPTION;
				fileOneField.setText( fc.getSelectedFile().toString() );
				filePaths[ 0 ] = fileOneField.getText();
			}
			if ( returnVal == JFileChooser.CANCEL_OPTION )
				dialogChoice = JFileChooser.CANCEL_OPTION;
		}
		
		else if ( actionCommand.equals( "browseTwo" ) ) {
			int returnVal = fc.showOpenDialog( this );
			
			if ( returnVal == JFileChooser.APPROVE_OPTION ) {
				dialogChoice = JFileChooser.APPROVE_OPTION;
				fileTwoField.setText( fc.getSelectedFile().toString() );
				filePaths[ 1 ] = fileTwoField.getText();
			}
			if ( returnVal == JFileChooser.CANCEL_OPTION )
				dialogChoice = JFileChooser.CANCEL_OPTION;
		}
	}

	/************************************************************************
	 * Function for DocumentListener interface. Do something
	 * everytime there is a change detected in the text fields.
	 */
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// do nothing here
	}

	/************************************************************************
	 * Function for DocumentListener interface. Check validity of file path
	 * every-time text has been inserted into the file field text field.
	 */
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		checkFilePaths();
	}

	/************************************************************************
	 * Function for DocumentListener interface. Check validity of file path
	 * every-time text has been removed from the field field text field.
	 */
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		checkFilePaths();
	}
	
	/************************************************************************
	 * Check the validity of file paths inside the fileOneField and
	 * fileTwoField. Disable the "Start Diff" button if the paths are invalid.
	 */
	private void checkFilePaths() {		
		// disable start diff button if the file fields are empty or the
		// paths are not valid.
		String f1Field = fileOneField.getText().trim();
		String f2Field = fileTwoField.getText().trim();
		
		File file1 = new File( f1Field );
		File file2 = new File( f2Field );
		
		if ( file1.exists() && file2.exists() )
			diffButton.setEnabled( true );
		else
			diffButton.setEnabled( false );
	}
	
	/************************************************************************
	 * Accessor for the file paths contained within the file path text fields
	 * @return String[] the file paths for the selected files
	 */
	public String [] getFilePaths() {
		return filePaths;
	}
	
	/************************************************************************
	 * Get the user's response to the dialog: either chose to open file or
	 * canceled to choose a file.
	 * @return boolean true if user selected to open file, false if canceled
	 */
	public boolean getDialogChoice() {
		if ( dialogChoice == JFileChooser.APPROVE_OPTION )
			return true;
		else
			return false;
	}
}

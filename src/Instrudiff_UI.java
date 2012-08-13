import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.lang.String;
import java.util.ArrayList;
/**
 * Instrudiff_UI.java
 */

/**
 * This class initializes the GUI of the program and registers event
 * listeners.
 * @author Alex
 *
 */
public class Instrudiff_UI implements ActionListener {
	JFrame frame; // the main window

	JTextArea outArea; // the text area that holds the diff output
	
	JPanel viewPanel; // the panel for the card layout
	
	JTree xmlTree;
	
	Container pane;
	
	CardLayout dealer;
	
	// struts to create invisible space
	Component hStrut; 
	Component vStrut;
	
	JScrollPane outView;
	JScrollPane treeView;
	
	public static final int VERTICAL_STRUT_SIZE = 10;
	public static final int HORIZONTAL_STRUT_SIZE = 10;
	
	/**************************************************************************
	 * Constructor initializes swing components and shows the GUI
	 */
	Instrudiff_UI() {
		// initialize main window settings
		frame = new JFrame( "Instrudiff" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 640, 480 );
		frame.setLocationRelativeTo( null );
		pane = frame.getContentPane();
		
		createMenu();
		createToolBar();
		createCardLayout();
		
		vStrut = Box.createVerticalStrut( VERTICAL_STRUT_SIZE );
		pane.add( vStrut, BorderLayout.SOUTH );
		
		// show the gui
		frame.setVisible( true );
	}
	
	/**************************************************************************
	 * createMenu - create the menu to add  to the toolbar
	 */
	private void createMenu() {
		JMenuBar menuBar;
		JMenu fileMenu;
		JMenuItem openItem;
		JMenuItem diffItem;
		JMenuItem exitItem;
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu( "File" );
		
		menuBar.add( fileMenu );
		
		openItem = new JMenuItem( "Open Config File" );
		openItem.addActionListener( this );
		fileMenu.add( openItem );
		diffItem = new JMenuItem( "Instrument Diff" );
		diffItem.addActionListener( this );
		fileMenu.add( diffItem );
		exitItem = new JMenuItem( "Exit" );
		exitItem.addActionListener( this );
		fileMenu.add( exitItem );
		frame.setJMenuBar( menuBar );
	} 
	
	/**************************************************************************
	 * createToolBar - creates the toolbar so that a menu can be added.
	 */
	private void createToolBar() {
		JPanel barPanel = new JPanel();
		barPanel.setLayout( new BoxLayout( barPanel, BoxLayout.Y_AXIS ) );
		
		JPanel barButtonPanel = new JPanel();
		barButtonPanel.setLayout( new FlowLayout( FlowLayout.LEFT, 10, 5 ) );
		JButton fileButton = new JButton();
		fileButton.setBorder( BorderFactory.createEmptyBorder() );
		fileButton.setIcon( new ImageIcon( "rsc/fileIcon.png" ) );
		fileButton.setToolTipText( "Open Config File" );
		fileButton.setActionCommand( "openConfigButton" );
		fileButton.addActionListener( this );
		barButtonPanel.add( fileButton );
		
		JButton diffButton = new JButton();
		diffButton.setBorder( BorderFactory.createEmptyBorder() );
		diffButton.setIcon( new ImageIcon( "rsc/compare.png" ) );
		diffButton.setToolTipText( "Detect Instrument Setting Changes" );
		diffButton.setActionCommand( "diffToolButton" );
		diffButton.addActionListener( this );
		barButtonPanel.add( diffButton );
		
		barPanel.add( barButtonPanel );
		
		frame.add( barPanel, BorderLayout.NORTH );
	}
	
	/**************************************************************************
	 * createCardLayout - creates a card layout where different panels can
	 * be displayed. The program initially shows a blank panel. The other panel
	 * is a text area that displays the diff results.
	 */
	private void createCardLayout( ) {
		viewPanel = new JPanel();
		dealer = new CardLayout();
		viewPanel.setLayout( dealer );
		
		// blank layout when program first starts
		JPanel blank = new JPanel();
		viewPanel.add( "blank", blank );
		
		// the card layout for viewing the instrument changes 
		outArea = new JTextArea();
		outArea.setEditable( false );
		outView = new JScrollPane( outArea );
		JPanel docViewPanel = new JPanel();
		docViewPanel.setLayout(  new BoxLayout( docViewPanel, BoxLayout.X_AXIS ) );
		hStrut = Box.createHorizontalStrut( HORIZONTAL_STRUT_SIZE );
		docViewPanel.add( hStrut );
		docViewPanel.add( outView );
		hStrut = Box.createHorizontalStrut( HORIZONTAL_STRUT_SIZE );
		docViewPanel.add( hStrut );
		viewPanel.add( "output", docViewPanel );
		
		// the card layout for viewing an instrument file
		xmlTree = new JTree();
		treeView = new JScrollPane( xmlTree );
		JPanel treeViewPanel = new JPanel();
		treeViewPanel.setLayout(  new BoxLayout( treeViewPanel, BoxLayout.X_AXIS ) );
		hStrut = Box.createHorizontalStrut( HORIZONTAL_STRUT_SIZE );
		treeViewPanel.add( hStrut );
		treeViewPanel.add( treeView );
		hStrut = Box.createHorizontalStrut( HORIZONTAL_STRUT_SIZE );
		treeViewPanel.add( hStrut );
		viewPanel.add( "treeView", treeViewPanel );
		
		pane.add( viewPanel, BorderLayout.CENTER );
	}
	
	/**************************************************************************
	 * Handles event calls for the user interaction with swing components.
	 */
	@Override
	public void actionPerformed( ActionEvent e ) {
		String actionCommand = e.getActionCommand();
		
		if ( actionCommand.equals( "Exit" ) ) {
			System.exit( 0 );
		}
		
		else if ( actionCommand.equals( "Instrument Diff" ) ||
				actionCommand.equals( "diffToolButton" ) ) {
			DiffDialog diffDialog = new DiffDialog();
			diffDialog.setVisible( true );
			
			if ( diffDialog.getDialogChoice() == true ) {
				ParseDiff pd = new ParseDiff();
				ArrayList< String > out = pd.beginParse( diffDialog.getFilePaths() );
				outArea.setText( "" );
				
				for ( int i = 0; i < out.size(); i++ )
					outArea.append( out.get( i ) );
				
				outArea.setCaretPosition( 0 );
				dealer.show( viewPanel, "output" );
			}
			else
				return;
		}
		
		else if ( actionCommand.equals( "Open Config File") || 
				actionCommand.equals( "openConfigButton") ) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog( new JDialog() );
			
			if ( returnVal == JFileChooser.APPROVE_OPTION ) {
				InstrumentReader reader = new InstrumentReader();
				DefaultMutableTreeNode root = reader.beginParse( fc.getSelectedFile().toString() );
				DefaultTreeModel treeModel = new DefaultTreeModel( root );
				xmlTree.setModel( treeModel );
				dealer.show( viewPanel, "treeView" );
			}
			else
				return;
		}
	}
	
	/**************************************************************************
	 * main starts the program
	 * @param args command line arguments
	 * @return void
	 */
	public static void main( String [] args ) {
		
		// set the look and feel of the program to reflect the operating 
		// system's native look and feel.
		try {
			UIManager.setLookAndFeel( 
					UIManager.getSystemLookAndFeelClassName() );
		}
		catch ( UnsupportedLookAndFeelException e ) {
			
		}
		catch ( IllegalAccessException e ) {
			
		}
		catch ( InstantiationException e ) {
			
		}
		catch ( ClassNotFoundException e ) {
			
		}
		
		// create and show the GUI
		Instrudiff_UI ui = new Instrudiff_UI();
	}
}

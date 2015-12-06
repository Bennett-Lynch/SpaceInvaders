/*
 * Citations:
 * Original class modeled after: https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TabbedPaneDemoProject/src/components/TabbedPaneDemo.java
 */

package network;

import javax.swing.*;
import processing.core.PApplet;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/** UI that is to be used as the primary launcher.
 * User can choose to launch a server or client (including multiple clients), all from the same execution. */
public class Launcher extends JPanel
{
    private static final long serialVersionUID = 3223420205953751686L;

    static final int SPACING = 5;
    static final String DEFAULT_HOST = "127.0.0.1";
    static final String DEFAULT_PORT = "15963";

    /** The host that the user has set by the time he launches a new client. */
    static public String host = "127.0.0.1";

    /** The port that the user has set by the time he launches a new server or client. */
    static public int port = 15963;

    public Launcher()
    {
	super( new GridLayout( 1, 1 ) );

	JTabbedPane tabbedPane = new JTabbedPane();

	String serverDescript = "Host a new server and listen for incoming connections.";
	JComponent tab1 = makeServerPanel( serverDescript );
	tabbedPane.addTab( "Server", null, tab1, serverDescript );
	tabbedPane.setMnemonicAt( 0, KeyEvent.VK_1 );

	String clientDescript = "Connect to an already-running server.";
	JComponent tab2 = makeClientPanel( clientDescript );
	tabbedPane.addTab( "Client", null, tab2, clientDescript );
	tabbedPane.setMnemonicAt( 1, KeyEvent.VK_2 );

	// Add the tabbed pane to this panel.
	add( tabbedPane );

	// The following line enables to use scrolling tabs.
	tabbedPane.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );
    }

    protected JComponent makeServerPanel( String description )
    {
	// Setup the main panel
	JPanel panel = new JPanel( false );
	panel.setLayout( new BorderLayout( SPACING, SPACING ) );

	// Setup a box panel that we will use to fill the center panel of the border layout
	JPanel centerPanel = new JPanel();
	centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.PAGE_AXIS ) );

	// Add a text description label at the top
	JLabel topLabel = new JLabel( description, JLabel.CENTER );
	panel.add( topLabel, BorderLayout.PAGE_START );

	// Add a host label
	JLabel hostLabel = new JLabel( "Host:" );

	// Add a host input field
	JTextField hostInput;
	try
	{
	    hostInput = new JTextField( InetAddress.getLocalHost().getHostAddress() );
	}
	catch ( UnknownHostException e )
	{
	    hostInput = new JTextField( DEFAULT_HOST );
	}
	hostInput.setEnabled( false );

	// Combine our host label and host input field into a horizontal box layout
	JPanel hostPanel = new JPanel();
	hostPanel.setLayout( new BoxLayout( hostPanel, BoxLayout.LINE_AXIS ) );
	hostPanel.add( hostLabel );
	hostPanel.add( Box.createRigidArea( new Dimension( SPACING, 0 ) ) );
	hostPanel.add( hostInput );
	centerPanel.add( hostPanel );

	centerPanel.add( Box.createRigidArea( new Dimension( 0, SPACING ) ) );

	// Add a port label
	JLabel portLabel = new JLabel( "Port:" );

	// Add a port input field
	JTextField portInput = new JTextField( DEFAULT_PORT );

	// Combine our port label and port input field into a horizontal box layout
	JPanel portPanel = new JPanel();
	portPanel.setLayout( new BoxLayout( portPanel, BoxLayout.LINE_AXIS ) );
	portPanel.add( portLabel );
	portPanel.add( Box.createRigidArea( new Dimension( SPACING, 0 ) ) );
	portPanel.add( portInput );
	centerPanel.add( portPanel );

	// centerPanel.add( Box.createRigidArea( new Dimension( 0, SPACING ) ) );

	// Add a check box for a full screen mode option
	JCheckBox fullScreen = new JCheckBox( "Full Screen Mode" );
	fullScreen.setAlignmentX( CENTER_ALIGNMENT );
	centerPanel.add( fullScreen );

	// Now that we're done modifying our center panel we can add it to the primary border layout
	panel.add( centerPanel, BorderLayout.CENTER );

	// Add a start button
	JButton button = new JButton( "Start" );
	button.setPreferredSize( new Dimension( 40, 25 ) );
	panel.add( button, BorderLayout.PAGE_END );

	// Add action listener to button
	button.addActionListener( new ActionListener()
	{
	    @Override
	    public void actionPerformed( ActionEvent e )
	    {
		// Set the host and port to be used as static variables (since a Processing 2.x bug prevents us from passing args to the sketch directly)
		port = Integer.parseInt( portInput.getText() );

		if ( fullScreen.isSelected() )
		{
		    PApplet.main( new String[] { "--present", sketches.ServerSketch.class.getName() } );
		}
		else
		{
		    PApplet.main( sketches.ServerSketch.class.getName() );
		}
	    }
	} );

	return panel;
    }

    protected JComponent makeClientPanel( String description )
    {
	// Setup the main panel
	JPanel panel = new JPanel( false );
	panel.setLayout( new BorderLayout( SPACING, SPACING ) );

	// Setup a box panel that we will use to fill the center panel of the border layout
	JPanel centerPanel = new JPanel();
	centerPanel.setLayout( new BoxLayout( centerPanel, BoxLayout.PAGE_AXIS ) );

	// Add a text description label at the top
	JLabel topLabel = new JLabel( description, JLabel.CENTER );
	panel.add( topLabel, BorderLayout.PAGE_START );

	// Add a host label
	JLabel hostLabel = new JLabel( "Host:" );

	// Add a host input field
	JTextField hostInput = new JTextField( DEFAULT_HOST );

	// Combine our host label and host input field into a horizontal box layout
	JPanel hostPanel = new JPanel();
	hostPanel.setLayout( new BoxLayout( hostPanel, BoxLayout.LINE_AXIS ) );
	hostPanel.add( hostLabel );
	hostPanel.add( Box.createRigidArea( new Dimension( SPACING, 0 ) ) );
	hostPanel.add( hostInput );
	centerPanel.add( hostPanel );

	centerPanel.add( Box.createRigidArea( new Dimension( 0, SPACING ) ) );

	// Add a port label
	JLabel portLabel = new JLabel( "Port:" );

	// Add a port input field
	JTextField portInput = new JTextField( DEFAULT_PORT );

	// Combine our port label and port input field into a horizontal box layout
	JPanel portPanel = new JPanel();
	portPanel.setLayout( new BoxLayout( portPanel, BoxLayout.LINE_AXIS ) );
	portPanel.add( portLabel );
	portPanel.add( Box.createRigidArea( new Dimension( SPACING, 0 ) ) );
	portPanel.add( portInput );
	centerPanel.add( portPanel );

	// centerPanel.add( Box.createRigidArea( new Dimension( 0, SPACING ) ) );

	// Add a check box for a full screen mode option
	JCheckBox fullScreen = new JCheckBox( "Full Screen Mode" );
	fullScreen.setAlignmentX( CENTER_ALIGNMENT );
	centerPanel.add( fullScreen );

	// Now that we're done modifying our center panel we can add it to the primary border layout
	panel.add( centerPanel, BorderLayout.CENTER );

	// Add a connect button
	JButton button = new JButton( "Connect" );
	button.setPreferredSize( new Dimension( 40, 25 ) );
	panel.add( button, BorderLayout.PAGE_END );

	// Add action listener to button
	button.addActionListener( new ActionListener()
	{
	    @Override
	    public void actionPerformed( ActionEvent e )
	    {
		// Set the host and port to be used as static variables (since a Processing 2.x bug prevents us from passing args to the sketch directly)
		host = hostInput.getText();
		port = Integer.parseInt( portInput.getText() );

		if ( fullScreen.isSelected() )
		{
		    PApplet.main( new String[] { "--present", sketches.ClientSketch.class.getName() } );
		}
		else
		{
		    PApplet.main( sketches.ClientSketch.class.getName() );
		}
	    }
	} );

	return panel;
    }

    /** Create the GUI and show it. For thread safety, this method should be invoked from the event dispatch thread. */
    private static void createAndShowGUI()
    {
	// Create and set up the window.
	JFrame frame = new JFrame( "Launcher" );
	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

	// Add content to the window.
	frame.add( new Launcher(), BorderLayout.CENTER );

	// Display the window.
	frame.pack();
	frame.setLocationRelativeTo( null ); // Center the window
	frame.setVisible( true );
    }

    public static void main( String[] args )
    {
	// Schedule a job for the event dispatch thread:
	// creating and showing this application's GUI.
	SwingUtilities.invokeLater( new Runnable()
	{
	    @Override
	    public void run()
	    {
		// Turn off metal's use of bold fonts
		UIManager.put( "swing.boldMetal", Boolean.FALSE );
		createAndShowGUI();
	    }
	} );
    }
}
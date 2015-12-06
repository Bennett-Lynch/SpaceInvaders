package sketches;

import gameplay.ServerLogic;
import network.Launcher;
import network.ServerListener;
import utility.Constants;

/** A PApplet sketch that is to be run for clients only.
 * The server creates no player objects but it does create a set of starting world objects.
 * The sketch is the first point in the pipeline following the user hitting "Start" from the launcher.
 * It is the sketch's responsibility to create a ServerListener thread (which will then create threads for incoming connections as needed). */
public class ServerSketch extends GameObjectPApplet
{
    private static final long serialVersionUID = -9089402430534148197L;

    @Override
    public void setup()
    {
	// Call the GameObjectPApplet setup first
	super.setup();

	// Set the network ID for this sketch (used to assign ownership to objects)
	networkID = Constants.SERVER_ID;

	// Create our gameplay-defining game object
	gameplay = new ServerLogic( this );

	// Create a primary Server thread to listen for incoming connections (using the port specified with the launcher)
	ServerListener sl = new ServerListener( this, Launcher.port );
	new Thread( sl ).start();
    }

}

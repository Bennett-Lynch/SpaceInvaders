package sketches;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import components.Spawner;
import designs.GameObject;
import events.KeyboardInputEvent;
import gameplay.ClientLogic;
import network.ClientReadThread;
import network.ClientWriteThread;
import network.Launcher;
import processing.core.PVector;

/** A PApplet sketch that is to be run for clients only.
 * The sketch is the first point in the pipeline following the user hitting "Connect" from the launcher.
 * It is the sketch's responsibility to instantiate a player object and a client connection thread. */
public class ClientSketch extends GameObjectPApplet
{
    private static final long serialVersionUID = 5032115180710332230L;

    /** The rectangle representing the player for this client. */
    public GameObject player = null;

    @Override
    public void setup()
    {
	// Call the GameObjectPApplet setup first
	super.setup();

	Socket socket = null;
	try
	{
	    socket = new Socket( Launcher.host, Launcher.port );
	    System.out.println( "CLIENT: Connected to server address " + Launcher.host + " on port " + Launcher.port + "." );

	    ClientReadThread reader = new ClientReadThread( this, socket );
	    new Thread( reader ).start();
	}
	catch ( IOException e1 )
	{
	    e1.printStackTrace();
	    System.exit( 1 );
	}

	// Wait for the client connection to receive its client ID from the server
	while ( networkID == 0 )
	{
	    try
	    {
		Thread.sleep( 10 );
	    }
	    catch ( InterruptedException e )
	    {
		e.printStackTrace();
	    }
	}

	// Our networkID has now been set by the ClientReadThread

	while ( getRandomSpawnPoint() == null )
	{
	    System.out.println();
	    try
	    {
		// Need to process GameObjectUpdateEvent so that we receive the SpawnPoint game objects
		eventManager.ProcessEvents();
		Thread.sleep( 10 );
	    }
	    catch ( InterruptedException e )
	    {
		e.printStackTrace();
	    }
	}

	// Create our gameplay-defining game object (which will be responsible for instantiating the player)
	gameplay = new ClientLogic( this );

	// Now that we know our client number and have instantiated player objects we can open up a thread to write
	ClientWriteThread writer = new ClientWriteThread( this, socket, networkID );
	new Thread( writer ).start();

	// Load the char_death script that will be responsible for handling the CharacterDeathEvent
	// scriptManager.bindArgument( "sketch", this );
	// scriptManager.bindArgument( "player", player );
	// scriptManager.bindArgument( "eventManager", eventManager );
	// scriptManager.loadScript( "char_controller.js" );
    }

    @Override
    public void keyPressed()
    {
	eventManager.EnqueueEvent( new KeyboardInputEvent( gameTime(), true, key == CODED, key == CODED ? keyCode : Character.toLowerCase( key ) ) );
    }

    @Override
    public void keyReleased()
    {
	eventManager.EnqueueEvent( new KeyboardInputEvent( gameTime(), false, key == CODED, key == CODED ? keyCode : Character.toLowerCase( key ) ) );
    }

    public PVector getRandomSpawnPoint()
    {
	ArrayList<GameObject> spawnPoints = new ArrayList<GameObject>();

	// First go through the collection of game objects to find which ones are considered spawn points
	for ( GameObject go : sceneObjects.values() )
	{
	    if ( go.getComponent( Spawner.class ) != null )
	    {
		spawnPoints.add( go );
	    }
	}

	if ( spawnPoints.size() == 0 )
	{
	    return null;
	}

	// Randomly choose one to spawn on
	int randomSpawn = Math.round( random( 0, spawnPoints.size() - 1 ) );

	return spawnPoints.get( randomSpawn ).position;
    }

    public PVector getSpawnPoint( int spawnIndex )
    {
	// First go through the collection of game objects to find which ones are considered spawn points
	for ( GameObject go : sceneObjects.values() )
	{
	    Spawner sp = go.getComponent( Spawner.class );
	    if ( sp != null && sp.spawnIndex == spawnIndex )
	    {
		return go.position;
	    }
	}

	return null;
    }
}

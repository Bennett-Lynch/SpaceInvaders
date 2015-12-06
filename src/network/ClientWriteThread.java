package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import designs.GameObject;
import eventManagement.Event;
import eventManagement.EventListener;
import eventManagement.NetworkedEvent;
import events.DestroyObjectsEvent;
import sketches.ClientSketch;
import sketches.GameObjectPApplet;
import utility.Constants;

public class ClientWriteThread implements Runnable, EventListener
{

    private ClientSketch sketch;
    private Socket socket = null;

    public int clientNetworkID;

    /** Events that we are waiting to send across the network. */
    private Queue<Event> pendingEvents = new ConcurrentLinkedQueue<Event>();

    public ClientWriteThread( ClientSketch sketch, Socket socket, int clientNetworkID )
    {
	this.sketch = sketch;
	this.socket = socket;
	this.clientNetworkID = clientNetworkID;
    }

    @Override
    public void run()
    {
	try ( ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream() ); )
	{
	    // Register for events that we wish to sync across the network (they must inherit NetworkedEvent)
	    sketch.eventManager.RegisterForEvent( DestroyObjectsEvent.class, this );

	    // Create a PrintWriter that we can use to detect if the socket connection has been closed
	    PrintWriter writer = new PrintWriter( socket.getOutputStream() );

	    // Begin our main loop, where we send the server a copy of our player game object and then read a list of objects from the server
	    while ( !writer.checkError() )
	    {
		// Send an updated version of all client-owned game objects (player ship and player projectiles)
		for ( GameObject go : sketch.sceneObjects.values() )
		{
		    if ( go.ownerID == clientNetworkID )
		    {
			out.reset();
			out.writeObject( go );
		    }
		}

		// If we have any events waiting to be synced across the network, send them
		while ( !pendingEvents.isEmpty() )
		{
		    out.writeObject( pendingEvents.poll() );
		}

		Thread.sleep( Constants.THREAD_WRITE_DELAY ); // Can throttle update speed here (in milliseconds)
	    }

	    // Exited while loop due to writer.checkError() returning true
	    socket.close();
	    CloseClient();
	}
	catch ( IOException | InterruptedException e )
	{
	    e.printStackTrace();
	    CloseClient();
	}
    }

    private void CloseClient()
    {
	System.out.println( "Lost connection with server." );
	System.exit( 1 );
    }

    @Override
    public void HandleEvent( GameObjectPApplet sketch, Event event )
    {
	if ( event instanceof NetworkedEvent )
	{
	    HandleEvent( sketch, (NetworkedEvent) event );
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, NetworkedEvent event )
    {
	// Only send this event to the server if we authored it
	if ( event.originalCallersNetworkID == clientNetworkID )
	{
	    pendingEvents.add( event );
	}
    }
}

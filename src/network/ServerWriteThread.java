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
import sketches.GameObjectPApplet;
import sketches.ServerSketch;
import utility.Constants;

public class ServerWriteThread implements Runnable, EventListener
{
    private ServerSketch sketch;
    private Socket socket = null;
    private int clientNumber;

    /** Events that we are waiting to send across the network. */
    private Queue<Event> pendingEvents = new ConcurrentLinkedQueue<Event>();

    public ServerWriteThread( ServerSketch sketch, Socket socket, int clientNumber )
    {
	this.sketch = sketch;
	this.socket = socket;
	this.clientNumber = clientNumber;
    }

    @Override
    public void run()
    {
	try ( ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream() ); ) // Create an output writer for the just-established client
	{
	    // Register for events that we wish to sync across the network (they must inherit NetworkedEvent)
	    sketch.eventManager.RegisterForEvent( DestroyObjectsEvent.class, this );

	    // The first thing we always do is send the client a copy of his client ID
	    out.writeObject( clientNumber );

	    // Create a PrintWriter that we can use to detect if the socket connection has been closed
	    PrintWriter writer = new PrintWriter( socket.getOutputStream() );

	    while ( !writer.checkError() )
	    {
		// Now we will send the client a list of all objects that he does not own
		for ( GameObject go : sketch.sceneObjects.values() )
		{
		    if ( go.ownerID != clientNumber )
		    {
			out.reset();
			out.writeObject( go );
		    }
		}

		// If we have any events waiting to be synced across the network (events that were NOT initially raised by this client), send them
		while ( !pendingEvents.isEmpty() )
		{
		    out.writeObject( pendingEvents.poll() );
		}

		Thread.sleep( Constants.THREAD_WRITE_DELAY ); // Can throttle update speed here (in milliseconds)
	    }

	    // Exited while loop due to writer.checkError() returning true
	    socket.close();
	    RemoveClientFromGame();
	}
	catch ( IOException | InterruptedException e )
	{
	    e.printStackTrace();
	    RemoveClientFromGame();
	}
    }

    private void RemoveClientFromGame()
    {
	System.out.println( "Closed socket with client ID " + clientNumber + "." );

	// Find game objects that belonged to the player and delete them (syncing to all other clients in the process)
	for ( GameObject go : sketch.sceneObjects.values() )
	{
	    if ( go.ownerID == clientNumber )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), Constants.SERVER_ID, go ) );
	    }
	}
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
	// If the event belongs to anyone but this client, send it (could be from server, could be from another client)
	if ( event.originalCallersNetworkID != clientNumber )
	{
	    pendingEvents.add( event );
	}
    }

}

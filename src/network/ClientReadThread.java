package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import designs.GameObject;
import eventManagement.Event;
import events.GameObjectUpdateEvent;
import sketches.ClientSketch;

public class ClientReadThread implements Runnable
{
    private ClientSketch sketch;
    private Socket socket = null;

    public ClientReadThread( ClientSketch sketch, Socket socket )
    {
	this.sketch = sketch;
	this.socket = socket;
    }

    @Override
    public void run()
    {
	try ( ObjectInputStream in = new ObjectInputStream( socket.getInputStream() ); )
	{
	    Object input = null;

	    // The first thing we always do is request a copy of our client ID from the server
	    if ( ( input = in.readObject() ) != null )
	    {
		if ( input instanceof Integer )
		{
		    sketch.networkID = (Integer) input;
		}
	    }

	    System.out.println( "CLIENT: Received client ID " + sketch.networkID + "." );

	    // Create a PrintWriter that we can use to detect if the socket connection has been closed
	    PrintWriter writer = new PrintWriter( socket.getOutputStream() );

	    // Begin our main loop, where we send the server a copy of our player game object and then read a list of objects from the server
	    while ( !writer.checkError() )
	    {
		// Now we wait for the server to send us a list of all other game objects (may be world objects, may be players)
		// Continue reading until we have received a null object (treated as an end-of-line object)
		while ( ( input = in.readObject() ) != null )
		{
		    if ( input instanceof GameObject )
		    {
			GameObject go = (GameObject) input;
			sketch.eventManager.EnqueueEvent( new GameObjectUpdateEvent( sketch.gameTime(), go ) );
		    }
		    else if ( input instanceof Event )
		    {
			Event e = (Event) input;
			e.timeStamp = sketch.gameTime(); // Need to set the timestamp to the current time due to diff clients tracking time independently
			sketch.eventManager.EnqueueEvent( e );
		    }
		}
	    }

	    // Exited while loop due to writer.checkError() returning true
	    socket.close();
	    // CloseClient();
	}
	catch ( IOException | ClassNotFoundException e )
	{
	    e.printStackTrace();
	    // CloseClient();
	}
    }

}

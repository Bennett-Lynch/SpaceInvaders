package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import designs.GameObject;
import eventManagement.Event;
import events.GameObjectUpdateEvent;
import sketches.ServerSketch;

public class ServerReadThread implements Runnable
{
    private ServerSketch sketch;
    private Socket socket = null;

    public ServerReadThread( ServerSketch sketch, Socket socket )
    {
	this.sketch = sketch;
	this.socket = socket;
    }

    @Override
    public void run()
    {
	try ( ObjectInputStream in = new ObjectInputStream( socket.getInputStream() ); ) // Create an input reader for the just-established client
	{
	    Object input = null;

	    // Create a PrintWriter that we can use to detect if the socket connection has been closed
	    PrintWriter writer = new PrintWriter( socket.getOutputStream() );

	    while ( !writer.checkError() )
	    {
		// Listen to incoming packets from client until he sends a null term
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

	    // Exited while loop due to writer.checkError() returning true or receiving a null readObject
	    socket.close();
	}
	catch ( IOException | ClassNotFoundException e )
	{
	    e.printStackTrace();
	}
    }

}

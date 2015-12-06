/*
 * Citations:
 * Original class modeled after: http://docs.oracle.com/javase/tutorial/networking/sockets/examples/KnockKnockServer.java and http://docs.oracle.com/javase/tutorial/networking/sockets/examples/KKMultiServer.java
 */

package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import sketches.ServerSketch;

/** A thread that is to be run from the server only.
 * Only one instance of this thread should exist, and its purpose is to listen for incoming connections and assign them to new sockets/threads. */
public class ServerListener implements Runnable
{
    private ServerSketch sketch;
    private int portNumber;

    // Constructor requires a reference to the hosting PApplet sketch
    public ServerListener( ServerSketch sketch, int portNumber )
    {
	this.sketch = sketch;
	this.portNumber = portNumber;
    }

    @Override
    public void run()
    {
	boolean listening = true;

	System.out.printf( "SERVER: Now listening for incoming connections on port %d%n", portNumber );

	int numClients = 0;

	// Create a new ServerSocket object to listen on the specified port
	try ( ServerSocket serverSocket = new ServerSocket( portNumber ) )
	{
	    while ( listening )
	    {
		// Wait for an incoming connection and then assign it as a new client socket
		Socket clientSocket = serverSocket.accept();

		// Create a new thread for the just-created client socket
		ServerWriteThread writer = new ServerWriteThread( sketch, clientSocket, ++numClients );
		new Thread( writer ).start();

		ServerReadThread reader = new ServerReadThread( sketch, clientSocket );
		new Thread( reader ).start();

		System.out.printf( "SERVER: Created new socket for connection with %s, assigned client ID %d.%n", clientSocket.getInetAddress(), numClients );
	    }
	}
	catch ( IOException e )
	{
	    System.err.printf( "Exception caught when trying to listen on port %d or listening for a connection%n", portNumber );
	    System.err.println( e.getMessage() );
	    System.exit( 1 );
	}
    }

}
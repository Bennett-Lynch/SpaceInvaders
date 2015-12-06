/*
 * Citations:
 * Some design techniques and basic code borrowed from: http://gamedev.stackexchange.com/a/38756
 */

package eventManagement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import sketches.GameObjectPApplet;

@SuppressWarnings( "rawtypes" )
public class EventManager
{
    private HashMap<Class, LinkedList<EventListener>> allListeners = new HashMap<Class, LinkedList<EventListener>>();

    private GameObjectPApplet sketch;

    private PriorityBlockingQueue<Event> eventPriorityQueue;

    public EventManager( GameObjectPApplet sketch )
    {
	this.sketch = sketch;
	eventPriorityQueue = new PriorityBlockingQueue<Event>( 1, new EventComparator() );
    }

    public void RegisterForEvent( Class eventType, EventListener listener )
    {
	LinkedList<EventListener> listeners = allListeners.get( eventType );

	if ( listeners == null )
	{
	    listeners = new LinkedList<EventListener>();
	    allListeners.put( eventType, listeners );
	}

	if ( !listeners.contains( listener ) )
	{
	    listeners.add( listener );
	}
    }

    public void UnregisterForEvent( Class eventType, EventListener listener )
    {
	LinkedList<EventListener> listeners = allListeners.get( eventType );

	listeners.remove( listener );
    }

    public void EnqueueEvent( Event event )
    {
	eventPriorityQueue.add( event );
    }

    public void ProcessEvents()
    {
	// Check for memory leak
	// if ( eventPriorityQueue.size() > 1000 )
	// {
	// for ( Event e : eventPriorityQueue )
	// {
	// System.out.println( e );
	// }
	// System.out.printf( "Exiting at Time=[%f] on Sketch=[%s]%n", sketch.gameTime(), sketch.getClass().getSimpleName() );
	// System.exit( 1 );
	// }

	while ( !eventPriorityQueue.isEmpty() && eventPriorityQueue.peek().timeStamp <= sketch.gameTime() )
	{
	    Event e = eventPriorityQueue.poll();

	    LinkedList<EventListener> listeners = allListeners.get( e.getClass() );

	    // Distribute the event to the script manager, which will allow scripts to handle the particular event
	    // sketch.scriptManager.distributeEvent( e );

	    // System.out.println( "Processing event with " + ( listeners == null ? 0 : listeners.size() ) + " listeners: " + e );

	    if ( listeners == null )
	    {
		return;
	    }

	    for ( EventListener listener : listeners )
	    {
		listener.HandleEvent( sketch, e );
	    }
	}
    }

}

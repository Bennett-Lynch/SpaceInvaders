package utility;

import eventManagement.Event;
import eventManagement.EventListener;
import events.KeyboardInputEvent;
import processing.core.PApplet;
import sketches.GameObjectPApplet;

/** A simpler version of the AdvancedInput class.
 * Instead of using hashmaps to set and retrieve input values, this class simply uses a few pre-defined boolean values.
 * This results in much better performance when being called on a frequent basis. */
public class Input implements EventListener
{
    private boolean left, right;

    // private GameObjectPApplet sketch;

    public Input( GameObjectPApplet sketch )
    {
	// this.sketch = sketch;
	sketch.eventManager.RegisterForEvent( KeyboardInputEvent.class, this );
    }

    @Override
    public void HandleEvent( GameObjectPApplet sketch, Event event )
    {
	if ( event instanceof KeyboardInputEvent )
	{
	    HandleEvent( sketch, (KeyboardInputEvent) event );
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, KeyboardInputEvent event )
    {
	if ( event.keyDown )
	{
	    if ( ( !event.coded && event.key == 'a' ) || ( event.coded && event.key == PApplet.LEFT ) )
	    {
		left = true;
	    }
	    else if ( ( !event.coded && event.key == 'd' ) || ( event.coded && event.key == PApplet.RIGHT ) )
	    {
		right = true;
	    }
	}
	else
	{
	    if ( ( !event.coded && event.key == 'a' ) || ( event.coded && event.key == PApplet.LEFT ) )
	    {
		left = false;
	    }
	    else if ( ( !event.coded && event.key == 'd' ) || ( event.coded && event.key == PApplet.RIGHT ) )
	    {
		right = false;
	    }
	}
    }

    /** Returns the player's horizontal input direction.
     *
     * @return -1 for left,<br>
     *         +1 for right,<br>
     *         0 for both or neither left/right */
    public int horizontalAxis()
    {
	return ( left ? -1 : 0 ) + ( right ? +1 : 0 );
    }

}

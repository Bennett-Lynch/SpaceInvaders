package components;

import designs.Component;
import designs.GameObject;
import eventManagement.Event;
import eventManagement.EventListener;
import events.KeyboardInputEvent;
import gameobjects.PlayerProjectile;
import processing.core.PApplet;
import sketches.GameObjectPApplet;

/** A controller component allows a game object's owner to move it around in the world.
 * Requires a RigidBody component. */
public class Controller extends Component implements EventListener
{
    private static final long serialVersionUID = 8608347147792355093L;

    private static final float CONTROLLER_VELOCITY = 7.5f;

    private static final float MIN_TIME_BETWEEN_SHOTS = 0.5f;
    private float lastShootTime = -MIN_TIME_BETWEEN_SHOTS;

    public Controller( GameObject gameObject )
    {
	super( gameObject );
    }

    @Override
    public void ownerInitialize( GameObjectPApplet sketch )
    {
	sketch.eventManager.RegisterForEvent( KeyboardInputEvent.class, this );
    }

    @Override
    public void ownerFixedUpdate( GameObjectPApplet sketch )
    {
	gameObject.pendingDirection.add( sketch.input.horizontalAxis() * CONTROLLER_VELOCITY, 0, 0 );
    }

    @Override
    public void unregisterForAllEvents( GameObjectPApplet sketch )
    {
	sketch.eventManager.UnregisterForEvent( KeyboardInputEvent.class, this );
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
	    if ( ( !event.coded && event.key == ' ' ) || ( !event.coded && event.key == 'w' ) || ( event.coded && event.key == PApplet.UP ) )
	    {
		// Make sure the player is still alive, and that enough time has elapsed
		if ( utility.Math.timeDifference( lastShootTime, sketch.gameTime() ) >= MIN_TIME_BETWEEN_SHOTS && sketch.sceneObjects.containsKey( gameObject.GUID ) )
		{
		    lastShootTime = sketch.gameTime();

		    PlayerProjectile p = new PlayerProjectile( sketch.networkID, sketch );
		    p.position.set( gameObject.position.x, gameObject.position.y + getComponent( ImageRenderer.class ).halfHeight() );
		    p.initializeComponents( sketch, sketch.networkID );
		    sketch.sceneObjects.put( p.GUID, p );
		}
	    }
	}
    }
}

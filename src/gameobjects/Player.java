package gameobjects;

import components.Collider;
import components.Controller;
import components.ImageRenderer;
import designs.GameObject;
import eventManagement.Event;
import eventManagement.EventListener;
import events.CollisionEvent;
import events.DestroyObjectsEvent;
import sketches.GameObjectPApplet;

public class Player extends GameObject implements EventListener
{
    private static final long serialVersionUID = 6736243099061807844L;

    public Player( int ownerID, GameObjectPApplet sketch, int cachedImageIdx, float scale )
    {
	super( ownerID );

	addComponent( new ImageRenderer( this, cachedImageIdx, sketch.imageCache[cachedImageIdx].width * scale, sketch.imageCache[cachedImageIdx].height * scale ) );
	addComponent( new Controller( this ) );
	// addComponent( new ScriptedBehavior( this, "char_controller.js" ) );
	addComponent( new Collider( this, getComponent( ImageRenderer.class ).width, getComponent( ImageRenderer.class ).height, true, false, true ) );

	sketch.eventManager.RegisterForEvent( CollisionEvent.class, this );
    }

    @Override
    public void unregisterForAllEvents( GameObjectPApplet sketch )
    {
	sketch.eventManager.UnregisterForEvent( CollisionEvent.class, this );

	super.unregisterForAllEvents( sketch );
    }

    @Override
    public void HandleEvent( GameObjectPApplet sketch, Event event )
    {
	if ( event instanceof CollisionEvent )
	{
	    HandleEvent( sketch, (CollisionEvent) event );
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, CollisionEvent event )
    {
	if ( event.primaryCollider.gameObject == this )
	{
	    if ( event.collidedWith.gameObject instanceof Voxel || event.collidedWith.gameObject instanceof Alien )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, this, event.collidedWith.gameObject ) );
	    }
	}
    }

}

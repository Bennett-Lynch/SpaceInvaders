package gameplay;

import designs.GameObject;
import eventManagement.Event;
import eventManagement.EventListener;
import events.CollisionEvent;
import events.DestroyObjectsEvent;
import events.ScreenCollisionEvent;
import gameobjects.AlienProjectile;
import gameobjects.Alien;
import gameobjects.Player;
import gameobjects.PlayerProjectile;
import gameobjects.Voxel;
import sketches.ClientSketch;
import sketches.GameObjectPApplet;
import utility.Constants;

/** This class is instantiated once per client sketch and is used primarily to handle events to affect gameplay. */
public class ClientLogic extends GameplayLogic implements EventListener
{
    private ClientSketch sketch;

    public ClientLogic( ClientSketch sketch )
    {
	this.sketch = sketch;

	sketch.eventManager.RegisterForEvent( CollisionEvent.class, this );
	sketch.eventManager.RegisterForEvent( ScreenCollisionEvent.class, this );
    }

    @Override
    public void HandleEvent( GameObjectPApplet sketch, Event event )
    {
	if ( event instanceof CollisionEvent )
	{
	    HandleEvent( sketch, (CollisionEvent) event );
	}
	else if ( event instanceof ScreenCollisionEvent )
	{
	    HandleEvent( sketch, (ScreenCollisionEvent) event );
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, CollisionEvent event )
    {
	if ( event.primaryCollider.gameObject instanceof PlayerProjectile && this.sketch.player != null ) // Checking to make sure player exists so we don't kill an alien during game reset
	{
	    if ( event.collidedWith.gameObject instanceof Voxel || event.collidedWith.gameObject instanceof Alien )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, event.primaryCollider.gameObject, event.collidedWith.gameObject ) );
	    }
	    else if ( event.collidedWith.gameObject instanceof AlienProjectile )
	    {
		// Destroy only the player's projectile on proj collisions
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, event.primaryCollider.gameObject ) );
	    }
	}
	// A collision event with source of player should only fire on the client owning that player's owner fixed update
	// We are detecting player -> alienProj collision on client side for more reactive gameplay
	else if ( event.primaryCollider.gameObject instanceof Player )
	{
	    if ( event.collidedWith.gameObject instanceof AlienProjectile || event.collidedWith.gameObject instanceof Alien )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, event.primaryCollider.gameObject, event.collidedWith.gameObject ) );
		this.sketch.player = null;
	    }
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, ScreenCollisionEvent event )
    {
	if ( event.collider.gameObject instanceof PlayerProjectile )
	{
	    sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, event.collider.gameObject ) );
	}
    }

    private static final int MAX_NUM_ALIENS = 11 * 5;

    @Override
    public void update()
    {
	if ( sketch.player == null )
	{
	    int alienCount = 0;

	    // Only spawn a new player when there are a full set of alien ships on the screen
	    for ( GameObject go : sketch.sceneObjects.values() )
	    {
		if ( go instanceof Alien )
		{
		    alienCount++;
		}
	    }

	    if ( alienCount == MAX_NUM_ALIENS )
	    {
		createPlayer();
	    }
	}
    }

    private void createPlayer()
    {
	// Create a game object to represent this client/player
	sketch.player = new Player( sketch.networkID, sketch, ( sketch.networkID - 1 ) % sketch.numPlayerImages, Constants.GAME_OBJECT_SCALE );
	sketch.player.position.set( sketch.getSpawnPoint( ( sketch.networkID - 1 ) % sketch.numPlayerImages ) ); // Could add a var for "numSpawnPoints", but it's same as num player images for now...

	// Add it to the list of scene game objects (so that it can be part of the update routine)
	sketch.sceneObjects.put( sketch.player.GUID, sketch.player );
	sketch.player.initializeComponents( sketch, sketch.networkID );
    }

}

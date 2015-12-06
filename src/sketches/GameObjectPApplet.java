/*
 * Citations:
 * y-axis invert hack borrowed from: http://stackoverflow.com/a/16610610/4039739
 * Usage of override functions to remedy size() bug taken from: https://github.com/processing/processing/issues/2039
 * Art assets were created by Seth Byrd and were downloaded from http://opengameart.org/content/cute-characters-monsters-and-game-assets
 */

package sketches;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import designs.GameObject;
import eventManagement.Event;
import eventManagement.EventListener;
import eventManagement.EventManager;
import events.DestroyObjectsEvent;
import events.GameObjectUpdateEvent;
import gameplay.GameplayLogic;
import processing.core.PApplet;
import processing.core.PImage;
import scripting.ScriptManager;
import time.Timeline;
import utility.Constants;
import utility.Input;
import utility.Physics;

/** A customized version of PApplet that contains shared behavior between both the Server sketch and Client sketch (which they both can inherit from). */
public class GameObjectPApplet extends PApplet implements EventListener
{
    private static final long serialVersionUID = 3211483672331394852L;

    /** A list of all game objects in the scene */
    public ConcurrentHashMap<UUID, GameObject> sceneObjects = new ConcurrentHashMap<UUID, GameObject>();

    /** A list of recently deleted objects. Key is the object's GUID and value is the time that it was removed.
     * This is used to ensure that we don't immediately re-add just-deleted objects due to networking latency. */
    public ConcurrentHashMap<UUID, Float> recentlyDeleted = new ConcurrentHashMap<UUID, Float>();

    /** A list of special UI game objects (that aren't synced with other clients) */
    public LinkedList<GameObject> UIObjects = new LinkedList<GameObject>();

    /** A special game object that is used to control the state and behavior of the game.
     * The server and clients can define custom behavior based on the version that they instantiate. */
    public GameplayLogic gameplay;

    /** A timestamp for when the last fixed update occurred. */
    protected float lastFixedUpdate;

    /** The network ID of this client or server. */
    public int networkID;

    /** An event manager that is responsible for managing registration of all events and for distributing the handle calls to each listener. */
    public EventManager eventManager = new EventManager( this );

    /** A script manager that is responsible for loading and executing scripts. */
    public ScriptManager scriptManager = new ScriptManager( this );

    /** An object to track input status (behaves like a singleton, but not strictly enforced with "static" so that it's safe to launch multiple clients) */
    public Input input = new Input( this );

    /** Create a physics instance (mainly so that it can refer back to this sketch to raise events when needed). */
    public Physics physics = new Physics( this );

    /** This individual sketch's record of time (starting at time 0 with the creation of the sketch). */
    public Timeline timeline = new Timeline( this, programTime() );

    /** Used to measure the time difference between game loop draw calls. */
    private float lastLoopTime = programTime();

    @Override
    public int sketchWidth()
    {
	return Constants.WINDOW_WIDTH;
    }

    @Override
    public int sketchHeight()
    {
	return Constants.WINDOW_HEIGHT;
    }

    @Override
    public String sketchRenderer()
    {
	return JAVA2D;
    }

    public int numPlayerImages = 4;

    public enum ImageIndex
    {
	PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4, PURPLE_ALIEN_1, PURPLE_ALIEN_2, GREEN_ALIEN_1, GREEN_ALIEN_2, RED_ALIEN_1, RED_ALIEN_2, BLUE_PROJECTILE, GREEN_PROJECTILE, RED_PROJECTILE, PURPLE_PROJECTILE
    };

    public PImage[] imageCache = new PImage[ImageIndex.values().length];

    @Override
    public void setup()
    {
	frameRate( Constants.FRAME_RATE );

	// Load our data assets
	loadAssets();

	rectMode( PApplet.CENTER );
	imageMode( PApplet.CENTER );

	eventManager.RegisterForEvent( GameObjectUpdateEvent.class, this );
	eventManager.RegisterForEvent( DestroyObjectsEvent.class, this );
    }

    protected void loadAssets()
    {
	// Player images
	imageCache[ImageIndex.PLAYER_1.ordinal()] = loadImage( "player1.png" );
	imageCache[ImageIndex.PLAYER_2.ordinal()] = loadImage( "player2.png" );
	imageCache[ImageIndex.PLAYER_3.ordinal()] = loadImage( "player3.png" );
	imageCache[ImageIndex.PLAYER_4.ordinal()] = loadImage( "player4.png" );

	// Alien ships
	imageCache[ImageIndex.PURPLE_ALIEN_1.ordinal()] = loadImage( "purple-alien-1.png" );
	imageCache[ImageIndex.PURPLE_ALIEN_2.ordinal()] = loadImage( "purple-alien-2.png" );
	imageCache[ImageIndex.GREEN_ALIEN_1.ordinal()] = loadImage( "green-alien-1.png" );
	imageCache[ImageIndex.GREEN_ALIEN_2.ordinal()] = loadImage( "green-alien-2.png" );
	imageCache[ImageIndex.RED_ALIEN_1.ordinal()] = loadImage( "red-alien-1.png" );
	imageCache[ImageIndex.RED_ALIEN_2.ordinal()] = loadImage( "red-alien-2.png" );

	// Projectiles
	imageCache[ImageIndex.BLUE_PROJECTILE.ordinal()] = loadImage( "blue-projectile.png" );
	imageCache[ImageIndex.GREEN_PROJECTILE.ordinal()] = loadImage( "green-projectile.png" );
	imageCache[ImageIndex.RED_PROJECTILE.ordinal()] = loadImage( "red-projectile.png" );
	imageCache[ImageIndex.PURPLE_PROJECTILE.ordinal()] = loadImage( "purple-projectile.png" );
    }

    @Override
    public void draw()
    {
	// Invert the y-axis (so 0,0 is viewed as the bottom left)
	translate( 0, sketchHeight() );
	scale( 1, -1 );

	// Redraw the background every frame (to erase previous contents)
	background( color( Constants.BACKGROUND_COLOR[0], Constants.BACKGROUND_COLOR[1], Constants.BACKGROUND_COLOR[2] ) );

	// Make sure that these two operations are always sequential (so there's no significant computation delay in between)
	timeline.increaseTimeBy( utility.Math.timeDifference( lastLoopTime, programTime() ) );
	lastLoopTime = programTime();

	// Run all frame update overrides (while passing in the network ID to check for ownership updates)
	for ( GameObject go : sceneObjects.values() )
	{
	    go.frameUpdateComponents( this, networkID );
	}

	for ( GameObject go : UIObjects )
	{
	    go.frameUpdateComponents( this, networkID );
	}

	// Perform however many fixed update calls are needed
	if ( utility.Math.timeDifference( lastFixedUpdate, gameTime() ) >= Constants.FIXED_DELTA_TIME )
	{
	    int numFixedUpdates = floor( utility.Math.timeDifference( lastFixedUpdate, gameTime() ) / Constants.FIXED_DELTA_TIME );

	    for ( GameObject go : sceneObjects.values() )
	    {
		for ( int i = 0; i < numFixedUpdates; ++i )
		{
		    go.fixedUpdateComponents( this, networkID );
		}
	    }

	    lastFixedUpdate = gameTime();
	}

	// Process all of the currently queued events
	eventManager.ProcessEvents();

	// Permanently remove any objects that have been destroyed for more than 5 seconds
	recentlyDeleted.values().removeIf( ts -> utility.Math.timeDifference( ts, gameTime() ) > 5 );

	// Update our gameplay-defining game object
	gameplay.update();
    }

    /** Return the inverted coordinate representation of the y mouse position. */
    public int mouseY()
    {
	return Constants.WINDOW_HEIGHT - mouseY;
    }

    /** Get the Processing sketch's time (number of seconds elapsed since THIS sketch was opened). */
    private float programTime()
    {
	// Note that millis() is different for each sketch (every new sketch starts at time 0).
	return millis() / 1000f;
    }

    /** Return this sketch's game time (separate from program time... game time may be paused, sped up, etc.) */
    public float gameTime()
    {
	return timeline.currentTime();
    }

    /** Close the sketch (without sending a system.exit to close all other windows). */
    public void closeSketch()
    {
	destroy();
	frame.setVisible( false );
    }

    @Override
    public void HandleEvent( GameObjectPApplet sketch, Event event )
    {
	if ( event instanceof GameObjectUpdateEvent )
	{
	    HandleEvent( sketch, (GameObjectUpdateEvent) event );
	}
	else if ( event instanceof DestroyObjectsEvent )
	{
	    HandleEvent( sketch, (DestroyObjectsEvent) event );
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, GameObjectUpdateEvent event )
    {
	if ( !recentlyDeleted.containsKey( event.go.GUID ) )
	{
	    sceneObjects.put( event.go.GUID, event.go );
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, DestroyObjectsEvent event )
    {
	for ( GameObject go : event.objectsToDestroy )
	{
	    recentlyDeleted.put( go.GUID, sketch.gameTime() );
	    sceneObjects.remove( go.GUID );
	    go.unregisterForAllEvents( this );
	}
    }

}

package gameplay;

import components.AlienAI;
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
import gameobjects.SpawnPoint;
import gameobjects.Voxel;
import sketches.GameObjectPApplet;
import sketches.GameObjectPApplet.ImageIndex;
import sketches.ServerSketch;
import utility.Constants;

public class ServerLogic extends GameplayLogic implements EventListener
{
    public boolean isGameActive = false;

    static private float ALIEN_BASE_HORIZ_SPEED = 1.5f;
    static final private float ALIEN_BASE_VERT_SPEED = 1.0f;
    static final private float DESCEND_DURATION = 1.0f;
    float lastDescendTimestamp = Float.MAX_VALUE;

    private int alienHorizDirection = 1;
    private static final float REVERSE_TIME_LIMIT = 1.0f;
    private float lastReverseTimestamp = -REVERSE_TIME_LIMIT;

    private static final float MAX_SPEED_MODIFIER = 5.0f;
    private float alienSpeedModifier = 1.0f;

    private static final float MIN_TIME_BETWEEN_SHOTS = 0.25f;
    private static final float MAX_TIME_BETWEEN_SHOTS = 0.5f;
    private float nextScheduledShootTime = Float.MAX_VALUE;

    private Alien[][] grid = new Alien[numRows][numCols];
    private static final int numRows = 5;
    private static final int numCols = 11;
    private int numAliensRemaining = numRows * numCols;

    private ServerSketch sketch;

    public ServerLogic( ServerSketch sketch )
    {
	this.sketch = sketch;

	sketch.eventManager.RegisterForEvent( CollisionEvent.class, this );
	sketch.eventManager.RegisterForEvent( ScreenCollisionEvent.class, this );
	sketch.eventManager.RegisterForEvent( DestroyObjectsEvent.class, this );

	nextScheduledShootTime = sketch.gameTime() + sketch.random( MIN_TIME_BETWEEN_SHOTS, MAX_TIME_BETWEEN_SHOTS );

	instantiateAliens();
	rebuildAllGreenObstacles();
	buildSpawnPoints();
    }

    private void instantiateAliens()
    {
	// First delete any already-existing aliens
	for ( GameObject go : sketch.sceneObjects.values() )
	{
	    if ( go instanceof Alien )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, go ) );
	    }
	}

	numAliensRemaining = numRows * numCols;
	alienSpeedModifier = 1.0f;
	lastDescendTimestamp = Float.MAX_VALUE;
	alienHorizDirection = 1;
	lastReverseTimestamp = -REVERSE_TIME_LIMIT;

	int vertSpacing = 10; // The spacing between each alien
	int horizSpacing = 12;
	float fixedWidth = sketch.imageCache[ImageIndex.RED_ALIEN_1.ordinal()].width * Constants.GAME_OBJECT_SCALE; // The width of the WIDEST alien image
	float yPos = 0; // Tracking current height since diff aliens have diff height, can't just use a multiple of row number
	float lastHeight = 0; // Half of the height for the previous alien row

	for ( int row = 0; row < numRows; ++row )
	{
	    for ( int col = 0; col < numCols; ++col )
	    {
		Alien alien = null;
		switch ( row )
		{
		    case 0:
			if ( col == 0 )
			{
			    yPos += vertSpacing + lastHeight;
			    lastHeight = sketch.imageCache[ImageIndex.PURPLE_ALIEN_1.ordinal()].height * Constants.GAME_OBJECT_SCALE * 0.5f;
			    yPos += lastHeight;
			}

			alien = new Alien( sketch.networkID, sketch, ImageIndex.PURPLE_ALIEN_1.ordinal(), ImageIndex.PURPLE_ALIEN_2.ordinal(), ImageIndex.PURPLE_PROJECTILE.ordinal(), Constants.GAME_OBJECT_SCALE );
			break;
		    case 1:
		    case 2:
			if ( col == 0 )
			{
			    yPos += vertSpacing + lastHeight;
			    lastHeight = sketch.imageCache[ImageIndex.GREEN_ALIEN_1.ordinal()].height * Constants.GAME_OBJECT_SCALE * 0.5f;
			    yPos += lastHeight;
			}

			alien = new Alien( sketch.networkID, sketch, ImageIndex.GREEN_ALIEN_1.ordinal(), ImageIndex.GREEN_ALIEN_2.ordinal(), ImageIndex.GREEN_PROJECTILE.ordinal(), Constants.GAME_OBJECT_SCALE );
			break;
		    case 3:
		    case 4:
			if ( col == 0 )
			{
			    yPos += vertSpacing + lastHeight;
			    lastHeight = sketch.imageCache[ImageIndex.RED_ALIEN_1.ordinal()].height * Constants.GAME_OBJECT_SCALE * 0.5f;
			    yPos += lastHeight;
			}

			alien = new Alien( sketch.networkID, sketch, ImageIndex.RED_ALIEN_1.ordinal(), ImageIndex.RED_ALIEN_2.ordinal(), ImageIndex.RED_PROJECTILE.ordinal(), Constants.GAME_OBJECT_SCALE );
			break;
		}
		alien.position.x = ( fixedWidth * 0.5f ) + col * ( fixedWidth + horizSpacing );
		alien.position.y = Constants.WINDOW_HEIGHT - yPos;
		sketch.sceneObjects.put( alien.GUID, alien );
		alien.initializeComponents( sketch, sketch.networkID );

		grid[row][col] = alien;
	    }
	}
    }

    /** The direction that all alien ships are currently moving in together.
     * Negative for left, positive for right. */
    public float getAlienHorizVelocity()
    {
	return alienHorizDirection * ALIEN_BASE_HORIZ_SPEED * alienSpeedModifier;
    }

    public float getAlienVertVelocity()
    {
	return utility.Math.timeDifference( lastDescendTimestamp, sketch.gameTime() ) < DESCEND_DURATION ? -ALIEN_BASE_VERT_SPEED : 0;
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
	else if ( event instanceof DestroyObjectsEvent )
	{
	    HandleEvent( sketch, (DestroyObjectsEvent) event );
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, CollisionEvent event )
    {
	if ( !isGameActive )
	{
	    return;
	}

	if ( event.primaryCollider.gameObject instanceof AlienProjectile )
	{
	    if ( event.collidedWith.gameObject instanceof Voxel )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, event.primaryCollider.gameObject, event.collidedWith.gameObject ) );
	    }
	}
	// Voxels are set to trigger collisions, but aliens aren't, so we check for src being voxel (and only destroy the voxel)
	else if ( event.primaryCollider.gameObject instanceof Voxel )
	{
	    if ( event.collidedWith.gameObject instanceof Alien )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, event.primaryCollider.gameObject ) );
	    }
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, ScreenCollisionEvent event )
    {
	if ( event.collider.gameObject instanceof Alien && event.horizontalDirection != 0 )
	{
	    if ( utility.Math.timeDifference( lastReverseTimestamp, sketch.gameTime() ) >= REVERSE_TIME_LIMIT )
	    {
		lastReverseTimestamp = sketch.gameTime();
		alienHorizDirection *= -1;

		if ( isGameActive )
		{
		    lastDescendTimestamp = sketch.gameTime();
		}
	    }
	}
	else if ( event.collider.gameObject instanceof Alien && event.verticalDirection < 0 && isGameActive )
	{
	    resetGame();
	}
	else if ( event.collider.gameObject instanceof AlienProjectile )
	{
	    sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, event.collider.gameObject ) );
	}
    }

    private void HandleEvent( GameObjectPApplet sketch, DestroyObjectsEvent event )
    {
	for ( GameObject go : event.objectsToDestroy )
	{
	    if ( go instanceof Alien )
	    {

		for ( int row = 0; row < numRows; ++row )
		{
		    for ( int col = 0; col < numCols; ++col )
		    {
			if ( grid[row][col] != null && grid[row][col].GUID.equals( go.GUID ) )
			{
			    grid[row][col] = null;
			    numAliensRemaining--;
			    float percentRemaining = (float) numAliensRemaining / ( numRows * numCols );
			    alienSpeedModifier = 1.0f + ( 1 - percentRemaining ) * ( MAX_SPEED_MODIFIER - 1 );
			    return;
			}
		    }
		}

	    }
	}
    }

    private void resetGame()
    {
	isGameActive = false;
	ALIEN_BASE_HORIZ_SPEED = 1.5f;
	removeAllProjectiles();
	instantiateAliens();
	rebuildAllGreenObstacles();
    }

    private void newLevel()
    {
	ALIEN_BASE_HORIZ_SPEED *= 1.2f;
	removeAllProjectiles();
	instantiateAliens();
	rebuildAllGreenObstacles();
    }

    private void removeAllProjectiles()
    {
	for ( GameObject go : sketch.sceneObjects.values() )
	{
	    if ( go instanceof PlayerProjectile || go instanceof AlienProjectile )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, go ) );
	    }
	}
    }

    @Override
    public void update()
    {
	// If the game is not yet active, see if the player has fired a projectile (so that we can start it)
	if ( !isGameActive )
	{
	    for ( GameObject go : sketch.sceneObjects.values() )
	    {
		if ( go instanceof PlayerProjectile )
		{
		    isGameActive = true;
		    break;
		}
	    }
	}

	// If the game is active but no players remain, stop the game
	if ( isGameActive )
	{
	    boolean playerExists = false;
	    for ( GameObject go : sketch.sceneObjects.values() )
	    {
		if ( go instanceof Player )
		{
		    playerExists = true;
		    break;
		}
	    }

	    if ( !playerExists )
	    {
		resetGame();
	    }
	}

	if ( numAliensRemaining == 0 )
	{
	    if ( isGameActive )
	    {
		newLevel();
	    }
	    else
	    {
		resetGame();
	    }
	}

	if ( isGameActive && numAliensRemaining > 0 && sketch.gameTime() >= nextScheduledShootTime )
	{
	    Alien valid = null;

	    while ( valid == null )
	    {
		int randCol = (int) sketch.random( numCols );

		for ( int row = numRows - 1; row >= 0; --row )
		{
		    if ( grid[row][randCol] != null )
		    {
			valid = grid[row][randCol];
			break;
		    }
		}
	    }

	    if ( valid != null )
	    {
		valid.getComponent( AlienAI.class ).shoot( sketch );
		nextScheduledShootTime = sketch.gameTime() + sketch.random( MIN_TIME_BETWEEN_SHOTS, MAX_TIME_BETWEEN_SHOTS );
	    }
	}
    }

    private void rebuildAllGreenObstacles()
    {
	// First delete any already-existing voxels
	for ( GameObject go : sketch.sceneObjects.values() )
	{
	    if ( go instanceof Voxel )
	    {
		sketch.eventManager.EnqueueEvent( new DestroyObjectsEvent( sketch.gameTime(), sketch.networkID, go ) );
	    }
	}

	int numCols = 4;
	for ( int col = 0; col < numCols; ++col )
	{
	    float x = ( col + 1.0f ) * ( Constants.WINDOW_WIDTH / ( numCols + 1.0f ) );
	    float y = 125;

	    constructObstacle( x, y, sketch.color( 5, 245, 119 ), 6, 12, 15 );
	}
    }

    /** Construct a collection of voxels, centered at the given coordinates. */
    private void constructObstacle( float x, float y, int color, int numRows, int numCols, int size )
    {
	for ( int row = 0; row < numRows; ++row )
	{
	    for ( int col = 0; col < numCols; ++col )
	    {
		// Ignore the top left/right corners
		if ( row == 0 && ( col == 0 || col == 1 || col == numCols - 1 || col == numCols - 2 ) )
		{
		    continue;
		}
		else if ( row == 1 && ( col == 0 || col == numCols - 1 ) )
		{
		    continue;
		}

		// Ignore the middle cells in the bottom half
		if ( row >= numRows * 0.5f && col >= numCols * 0.25f && col < numCols * 0.75f )
		{
		    continue;
		}

		Voxel v = new Voxel( sketch.networkID, size, size, color );
		v.position.x = x + size * col - ( size * ( numCols - 1 ) * 0.5f );
		v.position.y = y + size * -row + ( size * ( numRows - 1 ) * 0.5f );
		sketch.sceneObjects.put( v.GUID, v );
		v.initializeComponents( sketch, sketch.networkID );
	    }
	}
    }

    private void buildSpawnPoints()
    {
	// Make the spawn points (below the obstacles)
	int numCols = 4;
	for ( int col = 0; col < numCols; ++col )
	{
	    SpawnPoint sp = new SpawnPoint( sketch.networkID, col );
	    sp.position.x = ( col + 1.0f ) * ( Constants.WINDOW_WIDTH / ( numCols + 1.0f ) );
	    sp.position.y = sketch.imageCache[ImageIndex.PLAYER_1.ordinal()].height * Constants.GAME_OBJECT_SCALE * 0.5f;
	    sketch.sceneObjects.put( sp.GUID, sp );
	    sp.initializeComponents( sketch, sketch.networkID );
	}
    }
}

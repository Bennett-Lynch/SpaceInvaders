/*
 * Citations:
 * Interval overlap logic borrowed from: http://stackoverflow.com/a/3269471/4039739
 */

package utility;

import java.util.Collection;
import components.Collider;
import designs.GameObject;
import events.CollisionEvent;
import events.ScreenCollisionEvent;
import processing.core.PApplet;
import processing.core.PVector;
import sketches.GameObjectPApplet;

/** A static class that handles collisions between all game objects known to exist in the scene. */
public class Physics
{
    private GameObjectPApplet sketch;

    public Physics( GameObjectPApplet sketch )
    {
	this.sketch = sketch;
    }

    public void detectCollisions( Collider primary, PVector direction, Collection<GameObject> allGameObjects )
    {
	PVector result = PVector.add( primary.gameObject.position, direction );

	// Iterate over all game objects in the scene
	for ( GameObject go : allGameObjects )
	{
	    // Get the collider on the other game object
	    Collider other = go.getComponent( Collider.class );

	    // If no collider exists, or this is the same game object as the primary collider, skip to next iteration
	    if ( other == null || go == primary.gameObject )
	    {
		continue;
	    }

	    boolean horizontalOverlap = intervalOverlap( primary.topY(), primary.botY(), other.topY(), other.botY() );
	    if ( horizontalOverlap )
	    {
		// If the collider is to the right of the player
		if ( other.gameObject.position.x > primary.gameObject.position.x )
		{
		    if ( other.leftX() - primary.halfWidth() < result.x )
		    {
			sketch.eventManager.EnqueueEvent( new CollisionEvent( sketch.gameTime(), primary, other ) );
		    }

		}
		// If the collider is to the left of the player
		else
		{
		    if ( other.rightX() + primary.halfWidth() > result.x )
		    {
			sketch.eventManager.EnqueueEvent( new CollisionEvent( sketch.gameTime(), primary, other ) );
		    }
		}
	    }

	    boolean verticalOverlap = intervalOverlap( primary.leftX(), primary.rightX(), other.leftX(), other.rightX() );
	    if ( verticalOverlap )
	    {
		// If the collider is above the player
		if ( other.gameObject.position.y > primary.gameObject.position.y )
		{
		    if ( other.botY() - primary.halfHeight() < result.y )
		    {
			sketch.eventManager.EnqueueEvent( new CollisionEvent( sketch.gameTime(), primary, other ) );
		    }

		}
		// If the collider is below the player
		else
		{
		    if ( other.topY() + primary.halfHeight() > result.y )
		    {
			sketch.eventManager.EnqueueEvent( new CollisionEvent( sketch.gameTime(), primary, other ) );
		    }
		}
	    }
	}
    }

    public void detectScreenCollision( Collider primary, PVector direction )
    {
	PVector result = PVector.add( primary.gameObject.position, direction );

	int horiz = 0, vert = 0;

	if ( result.x < primary.halfWidth() )
	{
	    horiz = -1;
	}
	else if ( result.x > Constants.WINDOW_WIDTH - primary.halfWidth() )
	{
	    horiz = 1;
	}

	if ( result.y < primary.halfHeight() )
	{
	    vert = -1;
	}
	else if ( result.y > Constants.WINDOW_HEIGHT - primary.halfHeight() )
	{
	    vert = 1;
	}

	if ( horiz != 0 || vert != 0 )
	{
	    sketch.eventManager.EnqueueEvent( new ScreenCollisionEvent( sketch.gameTime(), primary, horiz, vert ) );
	}
    }

    public PVector constrainToScreen( Collider primary, PVector direction )
    {
	PVector result = PVector.add( primary.gameObject.position, direction );

	result.x = PApplet.constrain( result.x, primary.halfWidth(), Constants.WINDOW_WIDTH - primary.halfWidth() );
	result.y = PApplet.constrain( result.y, primary.halfHeight(), Constants.WINDOW_HEIGHT - primary.halfHeight() );

	return PVector.sub( result, primary.gameObject.position );
    }

    private boolean intervalOverlap( float a1, float a2, float b1, float b2 )
    {
	if ( a1 > a2 )
	{
	    float a1was = a1;
	    a1 = a2;
	    a2 = a1was;
	}

	if ( b1 > b2 )
	{
	    float b1was = b1;
	    b1 = b2;
	    b2 = b1was;
	}

	return a1 < b2 && b1 < a2;
    }
}

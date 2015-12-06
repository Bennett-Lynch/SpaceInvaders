package components;

import designs.Component;
import designs.GameObject;
import sketches.GameObjectPApplet;

/** A collider represents an object that should not be able to move through other colliders.
 * Note that this is specifically a *rectangle* collider, but is referred to just as collider for this 2D project. */
public class Collider extends Component
{
    private static final long serialVersionUID = -3708292392195850317L;

    /** Width and height */
    public float width, height;

    private boolean triggerColliderEvents, triggerScreenEvents, constrainToScreen;

    /** @param gameObject
     *            a reference to the parent GameObject
     * @param width
     *            the width of the collider (often the same as the rectangle width, but doesn't necessarily have to be)
     * @param height
     *            the height of the collider (often the same as the rectangle height, but doesn't necessarily have to be)
     * @param triggerScreenEvents
     *            the object will trigger events when it hits the screen
     * @param constrainToScreen
     *            the object will have its movement clamped to the screen */
    public Collider( GameObject gameObject, float width, float height, boolean triggerColliderEvents, boolean triggerScreenEvents, boolean constrainToScreen )
    {
	super( gameObject );
	this.width = width;
	this.height = height;
	this.triggerColliderEvents = triggerColliderEvents;
	this.triggerScreenEvents = triggerScreenEvents;
	this.constrainToScreen = constrainToScreen;
    }

    // TODO this was owner, can change to non owner to make alien projs disappear
    @Override
    public void ownerFixedUpdate( GameObjectPApplet sketch )
    {
	if ( triggerColliderEvents )
	{
	    sketch.physics.detectCollisions( this, gameObject.pendingDirection, sketch.sceneObjects.values() );
	}

	if ( triggerScreenEvents )
	{
	    sketch.physics.detectScreenCollision( this, gameObject.pendingDirection );
	}

	if ( constrainToScreen )
	{
	    gameObject.pendingDirection.set( sketch.physics.constrainToScreen( this, gameObject.pendingDirection ) );
	}
    }

    public float halfWidth()
    {
	return width / 2;
    }

    public float halfHeight()
    {
	return height / 2;
    }

    public float topY()
    {
	return gameObject.position.y + halfHeight();
    }

    public float botY()
    {
	return gameObject.position.y - halfHeight();
    }

    public float leftX()
    {
	return gameObject.position.x - halfWidth();
    }

    public float rightX()
    {
	return gameObject.position.x + halfWidth();
    }

}

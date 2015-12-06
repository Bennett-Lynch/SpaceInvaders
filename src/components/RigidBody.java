package components;

import designs.Component;
import designs.GameObject;
import processing.core.PVector;
import sketches.GameObjectPApplet;

/** A rigid body has a persistent velocity. */
public class RigidBody extends Component
{
    private static final long serialVersionUID = -8289765479013726634L;

    public PVector velocity = new PVector();

    public RigidBody( GameObject gameObject, PVector velocity )
    {
	super( gameObject );
	this.velocity.set( velocity );
    }

    @Override
    public void fixedUpdate( GameObjectPApplet sketch )
    {
	// By doing this in fixedUpdate (rather than ownerFixedUpdate) we can easily enable client-side movement prediction
	gameObject.pendingDirection.add( velocity );
    }

}

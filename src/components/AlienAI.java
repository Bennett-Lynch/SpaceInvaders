package components;

import designs.Component;
import designs.GameObject;
import gameobjects.AlienProjectile;
import gameplay.ServerLogic;
import sketches.GameObjectPApplet;

public class AlienAI extends Component
{
    private static final long serialVersionUID = 917574059137992296L;

    private int projImgIdx = 0;

    transient private ServerLogic sl;
    transient private RigidBody rb;

    public AlienAI( GameObject gameObject, int projImgIdx )
    {
	super( gameObject );

	this.projImgIdx = projImgIdx;
    }

    @Override
    public void ownerFixedUpdate( GameObjectPApplet sketch )
    {
	if ( sl == null )
	{
	    sl = (ServerLogic) ( sketch.gameplay );
	}
	if ( rb == null )
	{
	    rb = getComponent( RigidBody.class );
	}

	rb.velocity.set( sl.getAlienHorizVelocity(), sl.getAlienVertVelocity() );
    }

    public void shoot( GameObjectPApplet sketch )
    {
	AlienProjectile p = new AlienProjectile( sketch.networkID, sketch, projImgIdx );
	p.position.set( gameObject.position.x, gameObject.position.y - getComponent( ImageRenderer.class ).halfHeight() );
	p.initializeComponents( sketch, sketch.networkID );
	sketch.sceneObjects.put( p.GUID, p );
    }

}

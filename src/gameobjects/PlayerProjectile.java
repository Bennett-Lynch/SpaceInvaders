package gameobjects;

import components.Collider;
import components.ImageRenderer;
import components.RigidBody;
import designs.GameObject;
import processing.core.PVector;
import sketches.GameObjectPApplet;
import sketches.GameObjectPApplet.ImageIndex;

public class PlayerProjectile extends GameObject
{
    private static final long serialVersionUID = 6209835372806567854L;

    public PlayerProjectile( int ownerID, GameObjectPApplet sketch )
    {
	super( ownerID );

	int idx = ImageIndex.BLUE_PROJECTILE.ordinal();
	addComponent( new ImageRenderer( this, idx, sketch.imageCache[idx].width, sketch.imageCache[idx].height ) );
	addComponent( new Collider( this, 5, 17, true, true, false ) ); // Collider is intentionally smaller than the image
	addComponent( new RigidBody( this, new PVector( 0, 10 ) ) );
    }
}

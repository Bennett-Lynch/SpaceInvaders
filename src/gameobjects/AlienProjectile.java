package gameobjects;

import components.Collider;
import components.ImageRenderer;
import components.RigidBody;
import designs.GameObject;
import processing.core.PVector;
import sketches.GameObjectPApplet;

public class AlienProjectile extends GameObject
{
    private static final long serialVersionUID = -7411416486099386674L;

    public AlienProjectile( int ownerID, GameObjectPApplet sketch, int imageIdx )
    {
	super( ownerID );

	addComponent( new ImageRenderer( this, imageIdx, sketch.imageCache[imageIdx].width, sketch.imageCache[imageIdx].height ) );
	addComponent( new Collider( this, 5, 17, true, true, false ) ); // Collider is intentionally smaller than the image
	addComponent( new RigidBody( this, new PVector( 0, -10 ) ) );
    }
}

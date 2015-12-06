package gameobjects;

import components.AlienAI;
import components.AnimatedRenderer;
import components.Collider;
import components.ImageRenderer;
import components.RigidBody;
import designs.GameObject;
import processing.core.PVector;
import sketches.GameObjectPApplet;

public class Alien extends GameObject
{
    private static final long serialVersionUID = 2400513988858292152L;

    public Alien( int ownerID, GameObjectPApplet sketch, int alienImgIdx, int secondaryAlienImgIdx, int projImgIdx, float scale )
    {
	super( ownerID );

	addComponent( new AnimatedRenderer( this, alienImgIdx, sketch.imageCache[alienImgIdx].width * scale, sketch.imageCache[alienImgIdx].height * scale, secondaryAlienImgIdx, 1 ) );
	addComponent( new Collider( this, getComponent( ImageRenderer.class ).width, getComponent( ImageRenderer.class ).height, false, true, false ) );
	addComponent( new AlienAI( this, projImgIdx ) );
	addComponent( new RigidBody( this, new PVector( 0, 0 ) ) );
    }
}

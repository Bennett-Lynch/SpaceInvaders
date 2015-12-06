package gameobjects;

import components.Collider;
import components.RectRenderer;
import designs.GameObject;

public class Voxel extends GameObject
{
    private static final long serialVersionUID = 5359256917364151179L;

    public Voxel( int ownerID, int width, int height, int color )
    {
	super( ownerID );

	addComponent( new RectRenderer( this, width, height ) );
	getComponent( RectRenderer.class ).fillColor = color;
	addComponent( new Collider( this, width, height, true, false, false ) );
    }

}

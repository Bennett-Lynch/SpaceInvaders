package gameobjects;

import components.Spawner;
import designs.GameObject;

public class SpawnPoint extends GameObject
{
    private static final long serialVersionUID = -4470638367899579644L;

    public SpawnPoint( int ownerID, int spawnIndex )
    {
	super( ownerID );

	addComponent( new Spawner( this, spawnIndex ) );
	// addComponent( new RectRenderer( this, 25, 25 ) );
    }

}

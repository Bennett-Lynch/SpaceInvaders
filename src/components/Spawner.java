package components;

import designs.Component;
import designs.GameObject;

public class Spawner extends Component
{
    private static final long serialVersionUID = -8221301941684235130L;

    public int spawnIndex;

    public Spawner( GameObject gameObject, int spawnIndex )
    {
	super( gameObject );
	this.spawnIndex = spawnIndex;
    }

}

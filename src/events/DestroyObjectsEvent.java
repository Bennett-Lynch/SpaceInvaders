package events;

import designs.GameObject;
import eventManagement.NetworkedEvent;

/** This class is primarily meant to be sent over the network to inform clients to remove a game object from their list of objects.
 * It can also be used locally, however. */
public class DestroyObjectsEvent extends NetworkedEvent
{
    private static final long serialVersionUID = 9153455342496650183L;

    public GameObject[] objectsToDestroy = null;

    public DestroyObjectsEvent( float timeStamp, int originalCallersNetworkID, GameObject... objectsToDestroy )
    {
	super( EventPriority.High, timeStamp, originalCallersNetworkID );

	this.objectsToDestroy = objectsToDestroy;
    }
}

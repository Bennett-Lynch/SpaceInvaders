package events;

import designs.GameObject;
import eventManagement.Event;

/** This is called whenever a remote game object (not owned by this client) is updated (either seen for the first time, or has its state updated). */
public class GameObjectUpdateEvent extends Event
{
    private static final long serialVersionUID = 80040149229072848L;

    public GameObject go;

    public GameObjectUpdateEvent( float timeStamp, GameObject go )
    {
	super( EventPriority.Medium, timeStamp );
	this.go = go;
    }
}

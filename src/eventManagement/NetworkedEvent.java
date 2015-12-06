package eventManagement;

/** Networked events track the original caller's network ID, so that the events aren't sent back and forth in an infinite loop. */
public class NetworkedEvent extends Event
{
    private static final long serialVersionUID = 6146978625757393161L;

    public int originalCallersNetworkID;

    public NetworkedEvent( EventPriority eventPriority, float timeStamp, int originalCallersNetworkID )
    {
	super( eventPriority, timeStamp );

	this.originalCallersNetworkID = originalCallersNetworkID;
    }
}

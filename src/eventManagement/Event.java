package eventManagement;

import java.io.Serializable;

public abstract class Event implements Serializable
{
    private static final long serialVersionUID = -4951793371937057299L;

    /** Priority must be listed from high to low with how enum ordinals are used for comparison. */
    public enum EventPriority
    {
	High, Medium, Low
    };

    public EventPriority eventPriority;

    public float timeStamp;

    public Event( EventPriority eventPriority, float timeStamp )
    {
	this.eventPriority = eventPriority;
	this.timeStamp = timeStamp;
    }

    @Override
    public String toString()
    {
	return "Time=[" + timeStamp + "] Prior=[" + eventPriority.ordinal() + "] Type=[" + this.getClass().getName() + "]";
    }

}

package events;

import components.Collider;
import eventManagement.Event;

public class ScreenCollisionEvent extends Event
{
    private static final long serialVersionUID = -3176896874567311039L;

    public Collider collider;

    /** Negative for left, positive for right. */
    public int horizontalDirection;

    /** Negative for bottom, positive for top. */
    public int verticalDirection;

    public ScreenCollisionEvent( float timeStamp, Collider collider, int horizontalDirection, int verticalDirection )
    {
	super( EventPriority.High, timeStamp );

	this.collider = collider;
	this.horizontalDirection = horizontalDirection;
	this.verticalDirection = verticalDirection;
    }

    @Override
    public String toString()
    {
	return super.toString() + " Collider=[" + collider.gameObject + "]";
    }
}

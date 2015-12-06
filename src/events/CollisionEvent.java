package events;

import components.Collider;
import eventManagement.Event;

public class CollisionEvent extends Event
{
    private static final long serialVersionUID = -8329505223508215478L;

    public Collider primaryCollider;
    public Collider collidedWith;

    public CollisionEvent( float timeStamp, Collider primaryCollider, Collider collidedWith )
    {
	super( EventPriority.High, timeStamp );

	this.primaryCollider = primaryCollider;
	this.collidedWith = collidedWith;
    }

    @Override
    public String toString()
    {
	return super.toString() + " Primary=[" + primaryCollider.gameObject + "] Secondary=[" + collidedWith.gameObject + "]";
    }
}

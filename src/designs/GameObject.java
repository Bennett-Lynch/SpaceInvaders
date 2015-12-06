/*
 * Citations:
 * GameObject concept (but not code) borrowed from Unity
 * getComponent syntax help provided from: http://stackoverflow.com/questions/32892939/how-to-find-and-return-an-object-of-derived-type-in-a-list-of-base-type
 */

package designs;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.UUID;
import processing.core.PVector;
import sketches.GameObjectPApplet;

/** Class that should be the base of any object that is to be represented in the game world. */
public class GameObject implements Serializable
{
    private static final long serialVersionUID = -4940730444820075680L;

    /** A unique identifier to represent each Game Object */
    public UUID GUID;

    /** GameObjects can have an arbitrary number of components */
    private LinkedList<Component> components = new LinkedList<Component>();

    /** The object's position in 2D world space */
    public PVector position = new PVector();

    /** The object's rotation (2 dimensional display so only 1 possible rotation axis: z) */
    public float rotation;

    /** The unique ID representing who owns this object. 0 for server, any positive int for specific clients. */
    public int ownerID;

    /** After all components have been processed, the game object will be moved in this direction.
     * Note that this only applies for fixed updates, since physics movement should be done in fixed updates.
     * This is implemented to provide a central access point so that for multiple components all attempting to move the same object. */
    public PVector pendingDirection = new PVector();

    /** If pendingTeleport is not null, it will be used instead of the pendingDirection. */
    private PVector pendingTeleport = null;

    /** Constructor always requires the unique ID for the owner.
     * Other member fields should be updated after instantiation.
     *
     * @param ownerID
     *            The unique ID representing who owns this object. 0 for server, any positive int for specific clients. */
    public GameObject( int ownerID )
    {
	this.ownerID = ownerID;

	GUID = java.util.UUID.randomUUID();
    }

    /** Add a new component to this game object.
     * The added component is returned to enable convenient method chaining. */
    public <T extends Component> T addComponent( T c )
    {
	// if ( getComponent( c.getClass() ) != null )
	// {
	// throw new Exception( "A component of this type already exists on this game object." );
	// }

	components.add( c );
	return c;
    }

    /** Find a component of a specific type that belongs to this GameObject (may return null)
     * This method is only recommended to be used sparingly (consider storing the reference to reduce need for repeated calls) */
    @SuppressWarnings( "unchecked" )
    public <T extends Component> T getComponent( Class<T> type )
    {
	for ( Component c : components )
	{
	    if ( type.isInstance( c ) )
	    {
		return (T) c;
	    }
	}
	return null;
    }

    public GameObject initializeComponents( GameObjectPApplet sketch, int callerNetworkID )
    {
	for ( Component c : components )
	{
	    if ( ownerID == callerNetworkID )
	    {
		c.ownerInitialize( sketch );
	    }

	    c.initialize( sketch );
	}

	return this;
    }

    /** Call the frameUpdate() method on all of the components attached to this GameObject.
     * If the sketchClientID given is the same as the game object's ownerID, then the ownerFrameUpdate will be called as well.
     * This same game object is returned to enable convenient method chaining. */
    public GameObject frameUpdateComponents( GameObjectPApplet sketch, int callerNetworkID )
    {
	for ( Component c : components )
	{
	    if ( ownerID == callerNetworkID )
	    {
		c.ownerFrameUpdate( sketch );
	    }

	    c.frameUpdate( sketch );

	}

	return this;
    }

    /** Call the fixedUpdate() method on all of the components attached to this GameObject.
     * If the sketchClientID given is the same as the game object's ownerID, then the ownerFixedUpdate will be called as well. */
    public GameObject fixedUpdateComponents( GameObjectPApplet sketch, int callerNetworkID )
    {
	pendingDirection.set( 0, 0 );

	for ( Component c : components )
	{
	    if ( ownerID == callerNetworkID )
	    {
		c.ownerFixedUpdate( sketch );
	    }

	    c.fixedUpdate( sketch );
	}

	// If a component send a pending teleport position, use that instead of a normal translation
	if ( pendingTeleport != null )
	{
	    position.set( pendingTeleport );
	    pendingTeleport = null;
	}
	else
	{
	    // After all of the components have run, move our object in the resulting overall pending direction
	    position.add( pendingDirection );
	}

	return this;
    }

    public void setPendingTeleport( PVector v )
    {
	pendingTeleport = new PVector();
	pendingTeleport.set( v );
    }

    public void unregisterForAllEvents( GameObjectPApplet sketch )
    {
	for ( Component c : components )
	{
	    c.unregisterForAllEvents( sketch );
	}
    }
}

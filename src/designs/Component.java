/*
 * Citations:
 * Component concept (but not code) borrowed from Unity
 */

package designs;

import java.io.Serializable;
import sketches.GameObjectPApplet;

/** A Component represents a single, concrete behavior that a GameObject can have */
public abstract class Component implements Serializable
{
    private static final long serialVersionUID = -4204432422480012899L;

    /** Represents the parent GameObject that this component belongs to */
    public GameObject gameObject;

    /** Constructor always requires a reference to the parent GameObject */
    public Component( GameObject gameObject )
    {
	this.gameObject = gameObject;
    }

    /** Allows us to call getComponent() on components instead of only game objects. */
    public <T extends Component> T getComponent( Class<T> type )
    {
	return gameObject.getComponent( type );
    }

    /** This is called as the last step of the setup function in the sketch scenes. If game objects are manually positioned in the setup function, their position can then be retrieved in initialize. */
    public void initialize( GameObjectPApplet sketch )
    {
    }

    /** This is called as the last step of the setup function in the sketch scenes. If game objects are manually positioned in the setup function, their position can then be retrieved in initialize. */
    public void ownerInitialize( GameObjectPApplet sketch )
    {
    }

    /** The update method that will be called on the game object every time a new frame is drawn */
    public void frameUpdate( GameObjectPApplet sketch )
    {
    }

    /** The update method that will be called on the game object in fixed intervals */
    public void fixedUpdate( GameObjectPApplet sketch )
    {
    }

    /** Similar to frameUpdate, but only called by the client that owns the parent game object */
    public void ownerFrameUpdate( GameObjectPApplet sketch )
    {
    }

    /** Similar to fixedUpdate, but only called by the client that owns the parent game object */
    public void ownerFixedUpdate( GameObjectPApplet sketch )
    {
    }

    public void unregisterForAllEvents( GameObjectPApplet sketch )
    {

    }
}

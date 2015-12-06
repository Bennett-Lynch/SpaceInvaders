/*
 * Citations:
 * Original design borrowed from example given in class by Dr. Roberts.
 */

package components;

import java.io.FileNotFoundException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import designs.Component;
import designs.GameObject;
import sketches.GameObjectPApplet;

/** This component is attached to game objects that wish to have JavaScript scripts run on them.
 * A "start" function will be invoked at the beginning of execution.
 * An "update" function will be invoked every frame.
 * A "fixedUpdate" function will be invoked every fixed time delta interval. */
public class ScriptedBehavior extends Component
{
    private static final long serialVersionUID = -7296300936202120386L;

    /** The javax.script JavaScript engine used by this class. */
    transient private ScriptEngine js_engine = new ScriptEngineManager().getEngineByName( "JavaScript" );

    /** The Invocable reference to the engine. */
    transient private Invocable js_invocable = (Invocable) js_engine;

    /** The file name of the script associated with this class (given in the constructor). */
    private String scriptName;

    /** @param gameObject
     *            the parent game object of this component
     * @param scriptName
     *            the name of the script to be executed (with no file path; e.g., "hello_world.js"), the script must be located in the \bin\scripts directory. */
    public ScriptedBehavior( GameObject gameObject, String scriptName )
    {
	super( gameObject );
	this.scriptName = scriptName;
    }

    /** Used to bind the provided object to the name in the scope of the scripts being executed by this engine. */
    public void bindArgument( String name, Object obj )
    {
	js_engine.put( name, obj );
    }

    /** Will load the script source from the provided filename. */
    private void loadScript( String fileName )
    {
	try
	{
	    js_engine.eval( new java.io.FileReader( "src/scripts/" + fileName ) );
	}
	catch ( FileNotFoundException | ScriptException e )
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void ownerInitialize( GameObjectPApplet sketch )
    {
	bindArgument( "gameObject", gameObject );
	bindArgument( "sketch", sketch );

	loadScript( scriptName );

	try
	{
	    js_invocable.invokeFunction( "start" );
	}
	catch ( NoSuchMethodException e )
	{
	    // e.printStackTrace();
	}
	catch ( ScriptException e )
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void ownerFrameUpdate( GameObjectPApplet sketch )
    {
	try
	{
	    js_invocable.invokeFunction( "update" );
	}
	catch ( NoSuchMethodException e )
	{
	    // e.printStackTrace();
	}
	catch ( ScriptException e )
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void ownerFixedUpdate( GameObjectPApplet sketch )
    {
	try
	{
	    js_invocable.invokeFunction( "fixedUpdate" );
	}
	catch ( NoSuchMethodException e )
	{
	    // e.printStackTrace();
	}
	catch ( ScriptException e )
	{
	    e.printStackTrace();
	}
    }
}

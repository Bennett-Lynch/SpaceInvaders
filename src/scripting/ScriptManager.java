/*
 * Citations:
 * Original design borrowed from example given in class by Dr. Roberts.
 */

package scripting;

import java.io.FileNotFoundException;
import javax.script.*;
import eventManagement.Event;
import sketches.GameObjectPApplet;

/** Class to create and manage a JavaScript engine. */
public class ScriptManager
{
    // private GameObjectPApplet sketch;

    /* The javax.script JavaScript engine used by this class. */
    private ScriptEngine js_engine = new ScriptEngineManager().getEngineByName( "JavaScript" );

    /* The Invocable reference to the engine. */
    private Invocable js_invocable = (Invocable) js_engine;

    public ScriptManager( GameObjectPApplet sketch )
    {
	// this.sketch = sketch;
    }

    /** Used to bind the provided object to the name in the scope of the scripts being executed by this engine. */
    public void bindArgument( String name, Object obj )
    {
	js_engine.put( name, obj );
    }

    /** Will load the script source from the provided filename.
     * Note: provide ONLY the file name (e.g., "hello_world.js"), not the entire file path. */
    public void loadScript( String fileName )
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

    /** Will invoke the handleEvent function for a given event.
     * E.g., "handleKeyboardInputEvent" */
    public void distributeEvent( Event e, Object... args )
    {
	try
	{
	    bindArgument( "event", e );
	    js_invocable.invokeFunction( "handle" + e.getClass().getSimpleName(), args );
	}
	catch ( NoSuchMethodException e1 )
	{
	    // e1.printStackTrace();
	}
	catch ( ScriptException e1 )
	{
	    e1.printStackTrace();
	}
    }
}

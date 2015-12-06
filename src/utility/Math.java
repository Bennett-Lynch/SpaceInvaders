/**
 * Citations:
 * Some method concepts (but not code) borrowed from Unity
 */

package utility;

import processing.core.PApplet;

/** A class used to provide simple utility functions not already found in PApplet. */
final public class Math
{
    // Private constructor for static class
    private Math()
    {
    }

    /** A convenience function to call constrain(value, 0, 1) */
    static public float constrain01( float value )
    {
	return PApplet.constrain( value, 0, 1 );
    }

    /** Get the absolute difference between two timestamps. */
    static public float timeDifference( float startTime, float endTime )
    {
	return java.lang.Math.abs( endTime - startTime );
    }

}

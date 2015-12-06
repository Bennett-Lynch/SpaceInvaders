package utility;

/** A class used to store static variables shared by both the server and client. */
public class Constants
{
    static public final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;
    static public final int[] BACKGROUND_COLOR = { 50, 50, 50 };
    static public final int FRAME_RATE = 60;
    static public final int SERVER_ID = 0;
    static public final float GRAVITY = -8; // A constant downward velocity (not used as an actual acceleration)
    static public final float FIXED_DELTA_TIME = 0.02f; // A fixed physics update should occur once every 0.02 seconds (or 50 physics updates per second)
    static public final float GAME_OBJECT_SCALE = 0.75f;

    static public final int THREAD_WRITE_DELAY = 100; // Delay (in ms) between write attempts

    // Private constructor for static class
    private Constants()
    {
    }
}

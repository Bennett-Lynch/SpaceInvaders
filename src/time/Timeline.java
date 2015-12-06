package time;

import sketches.GameObjectPApplet;

public class Timeline
{
    // private GameObjectPApplet sketch;

    /** The time that anchors the start of this timeline. */
    private float startTimeAnchor;

    /** Local time is the time elapsed since the startTimeAnchor. */
    private float elapsedTime;

    /** Every observed increase in real world seconds is multiplied by this amount when progressing this timeline. */
    private float speedModifier = 1.0f;

    /** If frozen, this timeline is unable to progress forwards. */
    private boolean frozen = false;

    public Timeline( GameObjectPApplet sketch, float startTimeAnchor )
    {
	// this.sketch = sketch;
	this.startTimeAnchor = startTimeAnchor;
	elapsedTime = 0;
    }

    public void increaseTimeBy( float seconds )
    {
	if ( !frozen )
	{
	    elapsedTime += seconds * speedModifier;
	}
    }

    /** currentTime is equal to the realTimeStart + the localTime (note that localTime may not be 1:1 to real time) */
    public float currentTime()
    {
	return startTimeAnchor + elapsedTime;
    }

    public void setTimeSpeedModifier( float percent )
    {
	this.speedModifier = percent;
    }

    public void freezeTimeline()
    {
	frozen = true;
    }

    public void unfreezeTimeline()
    {
	frozen = false;
    }
}

package events;

import eventManagement.Event;

public class KeyboardInputEvent extends Event
{
    private static final long serialVersionUID = 8320359119885446939L;

    public boolean keyDown, coded;
    public int key;

    public KeyboardInputEvent( float timeStamp, boolean keyDown, boolean coded, int key )
    {
	super( EventPriority.High, timeStamp );

	this.keyDown = keyDown;
	this.coded = coded;
	this.key = key;
    }

}

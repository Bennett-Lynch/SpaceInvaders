package eventManagement;

import sketches.GameObjectPApplet;

public interface EventListener
{
    public void HandleEvent( GameObjectPApplet sketch, Event event );
}

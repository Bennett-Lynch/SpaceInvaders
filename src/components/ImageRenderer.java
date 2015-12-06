package components;

import designs.GameObject;
import sketches.GameObjectPApplet;

/** An ImageRenderer component uses an index (to reference a cached image in the primary sketch) to represent game objects. */
public class ImageRenderer extends RectRenderer
{
    private static final long serialVersionUID = -6864307730457632777L;

    /** The first index of the image cache array. */
    public int imgIdx;

    public ImageRenderer( GameObject gameObject, int cachedImageIdx, float width, float height )
    {
	super( gameObject, width, height );
	this.imgIdx = cachedImageIdx;
    }

    @Override
    public void frameUpdate( GameObjectPApplet sketch )
    {
	sketch.image( sketch.imageCache[imgIdx], gameObject.position.x, gameObject.position.y, width, height );
    }
}

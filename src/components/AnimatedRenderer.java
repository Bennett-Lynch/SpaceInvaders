package components;

import designs.GameObject;
import sketches.GameObjectPApplet;

public class AnimatedRenderer extends ImageRenderer
{
    private static final long serialVersionUID = 6128636941670924073L;

    /** The first index of the image cache array. */
    public int secondaryImgIdx;

    private float changeInterval;
    private float lastChangeTime;

    boolean primaryImg = true;

    public AnimatedRenderer( GameObject gameObject, int cachedImageIdx, float width, float height, int secondaryImgIdx, float changeInterval )
    {
	super( gameObject, cachedImageIdx, width, height );
	this.secondaryImgIdx = secondaryImgIdx;
	this.changeInterval = changeInterval;
    }

    @Override
    public void initialize( GameObjectPApplet sketch )
    {
	lastChangeTime = sketch.gameTime();
    }

    @Override
    public void ownerFixedUpdate( GameObjectPApplet sketch )
    {
	if ( utility.Math.timeDifference( lastChangeTime, sketch.gameTime() ) >= changeInterval )
	{
	    primaryImg = !primaryImg;
	    lastChangeTime = sketch.gameTime();
	}
    }

    @Override
    public void frameUpdate( GameObjectPApplet sketch )
    {
	sketch.image( sketch.imageCache[primaryImg ? imgIdx : secondaryImgIdx], gameObject.position.x, gameObject.position.y, width, height );
    }

}

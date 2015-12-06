package components;

import designs.Component;
import designs.GameObject;
import sketches.GameObjectPApplet;

public class RectRenderer extends Component
{
    private static final long serialVersionUID = -8775057443380229328L;

    /** Colors used when drawing the rectangle. */
    public int fillColor, strokeColor;

    /** Width and height */
    public float width, height;

    public RectRenderer( GameObject gameObject, float width, float height )
    {
	super( gameObject );
	this.width = width;
	this.height = height;
    }

    @Override
    public void frameUpdate( GameObjectPApplet sketch )
    {
	sketch.fill( fillColor );
	sketch.stroke( strokeColor );
	sketch.rect( gameObject.position.x, gameObject.position.y, width, height );
    }

    public float halfWidth()
    {
	return width / 2;
    }

    public float halfHeight()
    {
	return height / 2;
    }

    public float topY()
    {
	return gameObject.position.y + halfHeight();
    }

    public float botY()
    {
	return gameObject.position.y - halfHeight();
    }

    public float leftX()
    {
	return gameObject.position.x - halfWidth();
    }

    public float rightX()
    {
	return gameObject.position.x + halfWidth();
    }

}

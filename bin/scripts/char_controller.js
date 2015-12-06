var CONTROLLER_VELOCITY = 7.5;

function fixedUpdate()
{	
	gameObject.pendingDirection.add( sketch.input.horizontalAxis() * CONTROLLER_VELOCITY, 0, 0 );
}
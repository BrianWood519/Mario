package mario.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import mario.game.MainGame;
import mario.screens.GameScreen;


public class Mario extends Sprite {

	private float			speed	= 1.0f; // 10 pixels per second.
	private float			xPos;
	private float			yPos;

	private int				lives;

	public Body				body;
	public World			world;
	public Texture			texture;
	public Sprite			sprite;

	private TextureRegion	marioStanding;
	private boolean			facingRight;
	private boolean			facingLeft;

	public State			currentState;
	public State			previousState;

	public enum State {
		JUMPING, FALLING, STANDING, RUNNING, DEAD
	};

	public Mario(World world, Screen screen, int x, int y) {
		// Get little mario images loaded from atlas
		super(((GameScreen) screen).getImages().findRegion("little_mario"));
		this.world = world;

		// Set Starting Position
		xPos = x;
		yPos = y;
		// Create mario body and texture
		createMario(x, y);

		// define mario standing image from atlas
		marioStanding = new TextureRegion(getTexture(), 0, 10, 16, 16);
		// set how big it should be
		setBounds(0, 0, 16 / MainGame.PPM, 16 / MainGame.PPM);
		setRegion(marioStanding);

	}

	public void update(float time) {
		// Attach sprite image to body
		setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

		// Make sprite face right direction that Mario is running

	}

	public void createMario(int x, int y) {
		// Create Body Definition
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		// Set body's starting position in the world
		bodyDef.position.set(x / MainGame.PPM, y / MainGame.PPM);

		// Create body in the world using body definition
		body = world.createBody(bodyDef);

		// Create shape for fixture
		CircleShape circle = new CircleShape();
		circle.setRadius(6 / MainGame.PPM);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		circle.dispose();
		// Set fixture to mario bit
		fixtureDef.filter.categoryBits = MainGame.MARIO;
		//Set what mario can collide with
		fixtureDef.filter.maskBits = MainGame.GROUND | MainGame.BRICK | MainGame.COIN | MainGame.OBJECT;

		// Create our fixture and attach it to the body
		body.createFixture(fixtureDef).setUserData(this);

		// Create Marios head for block collisions
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2 / MainGame.PPM, 6 / MainGame.PPM),
				new Vector2(2 / MainGame.PPM, 6 / MainGame.PPM));
		fixtureDef.shape = head;
		// Dont collide with things, just use as sensor
		fixtureDef.isSensor = true;
		body.createFixture(fixtureDef).setUserData("head");
	}

	// ===================================================================================
	// ==================================== Methods ======================================
	// ===================================================================================
	public boolean moveLeft() {
		// Make sure not moving too fast
		if (body.getLinearVelocity().x >= -1)
			body.applyLinearImpulse(new Vector2(-0.1f, 0), body.getWorldCenter(), true);

		return true;
	}

	public boolean moveRight() {
		// Make sure not moving too fast
		if (body.getLinearVelocity().x <= 1)
			body.applyLinearImpulse(new Vector2(0.1f, 0), body.getWorldCenter(), true);

		return true;
	}

	public boolean jump() {
		// Only let mario jump if hes not already
		// And if his last state was running or standing, to avoid getting stuck rising
		// into block
		if (currentState != State.JUMPING) {
			body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
			currentState = State.JUMPING;
		}

		return true;
	}

	public void updateState() {

		// If mario is in a new state, save current as previous state
		if (currentState != getState())
			previousState = currentState;

		// update current state
		currentState = getState();

	}

	private State getState() {

		// if(marioIsDead)
		// return State.DEAD;

		if ((body.getLinearVelocity().y > 0 && currentState == State.JUMPING)
				|| (body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
			return State.JUMPING;

		else if (body.getLinearVelocity().y < 0)
			return State.FALLING;

		// if mario is positive or negative in the X axis he is running
		else if (body.getLinearVelocity().x != 0)
			return State.RUNNING;

		// if none of these return then he must be standing
		else
			return State.STANDING;
	}

	// ===================================================================================
	// ==================================== Getters ======================================
	// ==================================== & Setters ====================================
	// ===================================================================================
	public float getSpeed() {
		return speed;
	}

	public float getxPos() {
		return xPos;
	}

	public float getyPos() {
		return yPos;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setxPos(float xPos) {
		this.xPos = xPos;
	}

	public void setyPos(float yPos) {
		this.yPos = yPos;
	}

}

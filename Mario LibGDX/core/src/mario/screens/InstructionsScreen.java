package mario.screens;

import java.util.ArrayList;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import mario.game.CollisionListener;
import mario.game.MainGame;
import mario.objects.Brick;
import mario.objects.Coin;
import mario.objects.Pipe;
import mario.sprite.Mario;
import mario.sprite.Mario.State;


public class InstructionsScreen extends GameScreen {

	Mario								mario;
	ArrayList<Pipe>						pipes;

	// Define Keys
	private static final int			LEFT	= Keys.DPAD_LEFT;
	private static final int			RIGHT	= Keys.DPAD_RIGHT;
	private static final int			UP		= Keys.DPAD_UP;
	private static final int			DOWN	= Keys.DPAD_DOWN;
	private static final int			SPACE	= Keys.SPACE;

	// Loads Maps
	private TmxMapLoader				mapLoader;
	private TiledMap					map;
	private OrthogonalTiledMapRenderer	renderer;

	// Images
	private TextureAtlas				images;
	private Texture						textTexture;
	private Sprite						level1;
	private Sprite						level2;
	private Sprite						level3;


	// Add PPM here too to shorten code
	public static final float			PPM		= MainGame.PPM;
	private World						world;


	Box2DDebugRenderer					debugRender;
	private OrthographicCamera			gameCam;
	private Viewport					gamePort;
	private Viewport					textViewPort;


	// ===================================================================================
	// ================================= Constructor =====================================
	// ===================================================================================
	public InstructionsScreen(MainGame game) {
		gameCam = new OrthographicCamera();
		gamePort = new FitViewport(MainGame.V_WIDTH / PPM, MainGame.V_HEIGHT / PPM, gameCam);
		textViewPort = new FitViewport(MainGame.V_WIDTH, MainGame.V_HEIGHT, new OrthographicCamera());

		super.game = game;

		// Create atlas of images
		images = new TextureAtlas("assets/Mario_Images.pack");

		//Create level text images

		textTexture = new Texture("assets/InstructionsList.png");
		level1 = new Sprite(textTexture);
		level1.setSize(7 * 40 / PPM, 5 * 40 / PPM);
		level1.setPosition(53 / PPM, 10 / PPM);

		textTexture = new Texture("assets/Go Back.png");
		level2 = new Sprite(textTexture);
		level2.setSize(120 / PPM, 95 / PPM);
		level2.setPosition(290 / PPM, 60 / PPM);


		mapLoader = new TmxMapLoader();
		map = mapLoader.load("assets/Levels/Instructions.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / PPM);
		// Set Game Camera Position at beginning
		gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

		game.batch.enableBlending();

		// Create world where all bodies will be stored
		world = new World(new Vector2(0, -10), true);
		debugRender = new Box2DDebugRenderer();

		// Create ground and blocks and all world elements
		pipes = new ArrayList<Pipe>();
		createWorld(world);
		// Create Mario
		mario = new Mario(world, this, 32, 64);

		//Set collision detection listener
		world.setContactListener(new CollisionListener());

	}

	// ===================================================================================
	// ================================= Create World ====================================
	// ===================================================================================
	private void createWorld(World world) {
		BodyDef bdef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fdef = new FixtureDef();
		Body body;
		// Gets Ground layer from map objects and creates static body
		for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();

			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2) / PPM,
					(rect.getY() + rect.getHeight() / 2) / PPM);

			body = world.createBody(bdef);

			shape.setAsBox(rect.getWidth() / 2 / PPM, rect.getHeight() / 2 / PPM);
			fdef.shape = shape;
			body.createFixture(fdef);
		}

		// Create Pipes
		for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();

			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2) / PPM,
					(rect.getY() + rect.getHeight() / 2) / PPM);

			body = world.createBody(bdef);

			shape.setAsBox(rect.getWidth() / 2 / PPM, rect.getHeight() / 2 / PPM);
			fdef.shape = shape;
			body.createFixture(fdef);
		}

		// Create Pipe Tops
		for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
			pipes.add(new Pipe(this, object, "Start Screen"));
		}
	}

	// ===================================================================================
	// =============================== Update + Render ===================================
	// ===================================================================================
	protected void keyPressCheck(float time) {
		// If Mario goes left
		if (Gdx.input.isKeyPressed(LEFT)) {
			mario.moveLeft();
		}

		// If Mario goes right
		if (Gdx.input.isKeyPressed(RIGHT)) {
			mario.moveRight();
		}

		// If Mario jumps
		if (Gdx.input.isKeyJustPressed(SPACE) || Gdx.input.isKeyJustPressed(UP)) {
			mario.jump();
		}

		//if down arrow pressed, check to enter pipes
		if (Gdx.input.isKeyPressed(DOWN)) {
			for (Pipe pipe : pipes)
				if (pipe.isActive()) {
					//Use the pipe
					pipe.usePipe();
					//make pipe inactive so its only used once
					pipe.inactive();
				}
		}

	}


	private void update(float time) {
		// Check if any keys are pressed
		keyPressCheck(time);

		// Step world 60 frames every second
		world.step(1 / 60f, 6, 2);

		// update Mario
		mario.update(time);
		// Update his state
		mario.updateState();

		// Render what is showing on the camera
		renderer.setView(gameCam);
	}

	@Override public void render(float delta) {
		// Updates keys and camera and other things
		// Do this BEFORE rendering
		update(delta);

		// Clear and replace background with color
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Render Map
		renderer.render();

		// Show Debug render lines
		//debugRender.render(world, gameCam.combined);

		game.batch.setProjectionMatrix(gameCam.combined);
		game.batch.begin();

		// Draw Logo and text
		level1.draw(game.batch);
		level2.draw(game.batch);

		// Draw Mario giving sprite batch
		mario.draw(game.batch);

		game.batch.end();
	}


	// ===================================================================================
	// ==================================== Getters ======================================
	// ==================================== & Setters ====================================
	// ===================================================================================
	public TextureAtlas getImages() {
		return images;
	}

	@Override public void dispose() {
		game.batch.dispose();
	}

	@Override public void show() {
		// TODO Auto-generated method stub

	}

	@Override public void resize(int width, int height) {
		gamePort.update(width, height);

	}

	@Override public void pause() {
		// TODO Auto-generated method stub

	}

	@Override public void resume() {
		// TODO Auto-generated method stub

	}

	@Override public void hide() {
		// TODO Auto-generated method stub

	}

	public World getWorld() {
		return world;
	}

	public TiledMap getMap() {
		return map;
	}
}

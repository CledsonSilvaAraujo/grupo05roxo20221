package br.uff.ic.lek.game;

import br.uff.ic.lek.PlayerData;
import br.uff.ic.lek.actors.Avatar;
import br.uff.ic.lek.utils.CameraZoomAdjust;
import br.uff.ic.lek.utils.ClassToast;
import br.uff.ic.lek.utils.PathPlanning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;

import java.util.ArrayList;


public class World {
	private static ClassHud hud;
	private static float mapWidthPixel;
	private static float mapHeightPixel;

	public static World world=null;
	public static OrthographicCamera camera;
	public static Stage stageCG;
	public static TweenManager tweenManager;
	public static TextureAtlas atlasPlayerS_W_E_N;
	public static TextureAtlas atlasPlayerSW_NW_SE_NE;
	public static ArrayList<Rectangle> bounds;
	public static int xTiles, yTiles;
	public static int tileWidth;
	public static int tileHeight;
	public static int avatarStartTileX;
	public static int avatarStartTileY;
	public static String mensagem;

	private CameraController controller;
	private GestureDetector gestureDetector;
	private CameraZoomAdjust cameraZoomAdjust;
	private OrthogonalTiledMapRenderer tiledMapRender;
	private AssetManager assets;
	private Avatar avatar;
	private float count=100.0f;
	private BitmapFont font;

	public WorldController worldController;
	public InputMultiplexer inputMultiplexer;
	public SpriteBatch batch;
	public TiledMap map;
	public Music backgroundMusic;
	public PathPlanning pathPlan;

	private World() {
		ClassToast.initToastFactory();
		Color backgroundColor = new Color(0f, 0f, 0f, 0.5f);
		Color fontColor = new Color(1, 1, 0, 0.5f);
		String msg = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
		ClassToast.toastRich(msg, backgroundColor, fontColor, 2f);

		this.map = new TmxMapLoader().load("maps/alchemy.tmx");
		World.bounds = new ArrayList<Rectangle>();
		xTiles = map.getProperties().get("width", Integer.class);
		yTiles = map.getProperties().get("height", Integer.class);
		Gdx.app.log("World ", " xTiles="+xTiles + " yTiles="+yTiles);

		World.tileWidth = map.getProperties().get("tilewidth", Integer.class);
		World.tileHeight = map.getProperties().get("tileheight", Integer.class);
		World.mapWidthPixel =  xTiles * map.getProperties().get("tilewidth", Integer.class);
		World.mapHeightPixel = yTiles * map.getProperties().get("tileheight", Integer.class);

		for (int i = 0; i < xTiles; i++) {
			for (int j = 0; j < yTiles; j++) {
				TiledMapTileLayer cur = (TiledMapTileLayer) this.map.getLayers().get("base");
				Cell cell = cur.getCell(i, j);
				if(cell != null) {
					bounds.add(new Rectangle((i) * 32, j * 32, 32, 32));
				}
			}
		}

		this.assets = new AssetManager();
		this.assets.load("img/guerreiraS_W_E_N_210x280.pack", TextureAtlas.class);
		this.assets.load("img/guerreiraSW_NW_SE_NE_210x280.pack", TextureAtlas.class);
		this.assets.finishLoading();
		World.atlasPlayerS_W_E_N = this.assets.get("img/guerreiraS_W_E_N_210x280.pack");
		World.atlasPlayerSW_NW_SE_NE = this.assets.get("img/guerreiraSW_NW_SE_NE_210x280.pack");

		this.avatar = new Avatar(new Sprite(World.atlasPlayerS_W_E_N.findRegion("South02")), (avatarStartTileX)*World.tileWidth, avatarStartTileY*World.tileHeight, PlayerData.myPlayerData().getAuthUID());
		System.out.println("BANANA"+this.avatar.getAuthUID());
		World.camera = new OrthographicCamera(this.avatar.getX(), this.avatar.getY());
		World.camera.zoom = 0.5f;

		pathPlan = new PathPlanning(this);
		pathPlan.create();

		this.avatar.setX(avatarStartTileX*World.tileWidth);
		this.avatar.setY(avatarStartTileY*World.tileHeight);
	}

	public static void load() {
		if (world==null)
			world = new World();

		World.tweenManager = new TweenManager();
		world.worldController = new WorldController(World.world);


		world.batch = new SpriteBatch();
		World.hud = new ClassHud(world.batch);
		world.controller = new CameraController();
		world.gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, world.controller);
		world.inputMultiplexer = new InputMultiplexer();
		world.inputMultiplexer.addProcessor(World.hud.stage);
		world.inputMultiplexer.addProcessor(world.worldController);
		world.inputMultiplexer.addProcessor(world.gestureDetector);

		world.tiledMapRender = new OrthogonalTiledMapRenderer(world.map);
		World.camera = world.getCamera();
		World.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		World.camera.position.set(world.getAvatar().getX() - world.getAvatar().getWidth() / 2, world.getAvatar().getY() - world.getAvatar().getHeight() / 2, 0);
		World.camera.update();
		world.font = new BitmapFont();
		world.font.setColor(Color.RED);
		mensagem="debug";
		world.cameraZoomAdjust = new CameraZoomAdjust(0.1f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Tween.registerAccessor(OrthographicCamera.class, new ClassMyCameraAccessor());
	}

	public static void disparaAnimacaoCamera(float zoom, float duracao) {
			TweenCallback oneGoes = new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
							System.out.println("faz zoom ");
					}
			};
			TweenCallback anotherComes = new TweenCallback() { //** myCallBack object runs time reset **//
					@Override
					public void onEvent(int type, BaseTween<?> source) {
							System.out.println("volta para zoom original ");
					}
			};

			float zoomOriginal = World.camera.zoom;

			Timeline ttt = Timeline.createSequence();
			// Move the objects around, one after the other
			ttt.beginSequence();
			ttt.push(Tween.to(World.camera, ClassMyCameraAccessor.ZOOM, duracao)
							.target(zoom)
							.ease(Expo.INOUT)
							.setCallback(oneGoes) // use myTweenCallback created above //
							.setCallbackTriggers(TweenCallback.BEGIN)
			); //.delay(0.01f)
			ttt.push(Tween.to(World.camera, ClassMyCameraAccessor.ZOOM, duracao)
							.target(zoomOriginal)
							.ease(Expo.INOUT)
							.setCallback(oneGoes) // use myTweenCallback created above //
							.setCallback(anotherComes) // use myTweenCallback created above //
							.setCallbackTriggers(TweenCallback.BEGIN)
			); //.delay(0.01f)
			ttt.end();

			ttt.start(World.tweenManager);
	}

	public static void dispose() {
			if(world.map != null) world.map.dispose();
			if(World.atlasPlayerS_W_E_N != null) World.atlasPlayerS_W_E_N.dispose();
			if(World.atlasPlayerSW_NW_SE_NE != null) World.atlasPlayerSW_NW_SE_NE.dispose();
			if(world.backgroundMusic != null) world.backgroundMusic.dispose();
			if(world.pathPlan != null) world.pathPlan.dispose();
	}

	public static float getMapWidthPixel() {
		return World.mapWidthPixel;
	}

	public static float getMapHeightPixel() {
			return World.mapHeightPixel;
	}

	public static ArrayList<Rectangle> getBounds() {
		return bounds;
	}

	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		World.tweenManager.update(Gdx.graphics.getDeltaTime());

		this.tiledMapRender.getBatch().setProjectionMatrix(World.camera.combined);
		this.tiledMapRender.setView(World.camera);
		this.tiledMapRender.render();

		cameraZoomAdjust.multitouch(World.camera, delta);// para fazer map zoom in & out

		this.tiledMapRender.getBatch().begin();
		mensagem = "debug";
		World.world.getAvatar().draw(camera, font, mensagem, this.tiledMapRender.getBatch());
		this.tiledMapRender.getBatch().end();

		float x = World.world.getAvatar().getX() + World.world.getAvatar().getWidth();
		float y = World.world.getAvatar().getY() + World.world.getAvatar().getHeight();
		World.world.pathPlan.render(delta);

		batch.setProjectionMatrix(World.world.hud.stage.getCamera().combined);
		World.world.hud.stage.draw();

		Matrix4 uiMatrix = World.camera.combined.cpy();
		float screenHeight = Gdx.graphics.getHeight();
		float screenWidth = Gdx.graphics.getWidth();
		uiMatrix.setToOrtho2D(0, 0, screenWidth, screenHeight);
		this.tiledMapRender.getBatch().setProjectionMatrix(uiMatrix);
		Batch batch = this.tiledMapRender.getBatch();

		ClassToast.showToasts(batch);
	}

	public Avatar getAvatar() {
			return this.avatar;
	}

	public OrthographicCamera getCamera() {
			return World.camera;
	}

	public void setBounds(ArrayList<Rectangle> bounds) {
			World.bounds = bounds;
	}
}

class CameraController implements GestureDetector.GestureListener {
	float velX, velY;
	boolean flinging = false;
	float initialScale = 1;

	public boolean touchDown (float x, float y, int pointer, int button) {
		flinging = false;
		initialScale = World.camera.zoom;
		Gdx.app.log("botao", "touchdown at " + x + ", " + y);
		
		return false;
	}

	public void update () {
		if (flinging) {
			velX *= 0.98f;
			velY *= 0.98f;
			World.camera.position.add(
				-velX * Gdx.graphics.getDeltaTime(),
				velY * Gdx.graphics.getDeltaTime(),
				0
			);
			if (Math.abs(velX) < 0.01f) velX = 0;
			if (Math.abs(velY) < 0.01f) velY = 0;
		}
	}

	@Override
	public boolean tap (float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress (float x, float y) {
		return false;
	}

	@Override
	public boolean fling (float velocityX, float velocityY, int button) {
		flinging = true;
		velX = World.camera.zoom * velocityX * 0.5f;
		velY = World.camera.zoom * velocityY * 0.5f;

		return false;
	}

	@Override
	public boolean pan (float x, float y, float deltaX, float deltaY) {
		World.camera.position.add(-deltaX * World.camera.zoom, deltaY * World.camera.zoom, 0);
		return false;
	}

	@Override
	public boolean panStop (float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom (float originalDistance, float currentDistance) {
		float ratio = originalDistance / currentDistance;
		World.camera.zoom = initialScale * ratio;
		return false;
	}

	@Override
	public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
		return false;
	}

	@Override
	public void pinchStop () {
	}
}


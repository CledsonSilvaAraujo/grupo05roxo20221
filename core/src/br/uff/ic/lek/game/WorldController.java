package br.uff.ic.lek.game;

import br.uff.ic.lek.actors.Avatar;
import br.uff.ic.lek.actors.Player;
import br.uff.ic.lek.actors.PlayerLocal;
import br.uff.ic.lek.actors.Enemy;
import br.uff.ic.lek.actors.Avatar.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;


public class WorldController implements InputProcessor {
	private World world;
	private PlayerLocal mainPlayer;
	private List<Avatar> avatars = new ArrayList<Avatar>();
	private List<Player> players = new ArrayList<Player>();
	private List<Enemy> enemies = new ArrayList<Enemy>();

	private OrthographicCamera camera;

	private float minCameraX;
	private float maxCameraX;
	private float minCameraY;
	private float maxCameraY;

	private boolean requestMove;
	private boolean fezTouchDown;
	private int firstX, firstY;

	public WorldController(World world) {
		this.world = world;
		this.camera = world.getCamera();
		this.mainPlayer = world.getMainPlayer();
		this.avatars = world.getAvatars();
		this.players = world.getPlayers();
		this.enemies = world.getEnemies();
	}

	public void update(float delta) {
		this.setEnemiesTargets();
		this.updateAvatars(delta);
		this.minCameraX = this.camera.zoom * (this.camera.viewportWidth / 2);
		this.maxCameraX = World.getMapWidthPixel() - minCameraX;
		this.minCameraY = camera.zoom * (this.camera.viewportHeight / 2);
		this.maxCameraY = World.getMapHeightPixel() - minCameraY;
		this.camera.position.set(
			Math.min(maxCameraX, Math.max(this.avatars.get(0).getX(), minCameraX)),
			Math.min(maxCameraY, Math.max(this.mainPlayer.getY(), minCameraY)),
			0
		);
		this.camera.update();
	}

	public void onNotification(Dictionary<String,String> cmdData) {
		for (Player player : this.players) {
			if (player.getAuthUID() == cmdData.get("uID")) {
				if (cmdData.get("cmd") == "MOVE") {
					player.setTarget(
						Float.parseFloat(cmdData.get("px")),
						Float.parseFloat(cmdData.get("py"))
					);
				}
			}
		}
	}

	public void comandoMoveTo(float moveToX, float moveToY) {}

	@Override
	public boolean keyDown(int keycode) {
		if((keycode == Input.Keys.W)) {
			this.mainPlayer.setOrientation(Avatar.Compass.NORTH);
			this.mainPlayer.setState(State.WALKING);
			this.mainPlayer.getSpeed().y = Avatar.MAX_SPEED;
		}
		if((keycode == Input.Keys.S)) {
			this.mainPlayer.setOrientation(Avatar.Compass.SOUTH);
			this.mainPlayer.setState(State.WALKING);
			this.mainPlayer.getSpeed().y = -Avatar.MAX_SPEED;
		}
		if((keycode == Input.Keys.A)) {
			this.mainPlayer.setOrientation(Avatar.Compass.WEST);
			this.mainPlayer.setState(State.WALKING);
			this.mainPlayer.getSpeed().x = -Avatar.MAX_SPEED;
		}
		if((keycode == Input.Keys.D)) {
			this.mainPlayer.setOrientation(Avatar.Compass.EAST);
			this.mainPlayer.setState(State.WALKING);
			this.mainPlayer.getSpeed().x = Avatar.MAX_SPEED;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if((keycode == Input.Keys.W) || (keycode == Input.Keys.S)) {
			this.mainPlayer.getSpeed().y = 0;
		}
		if((keycode == Input.Keys.A) || (keycode == Input.Keys.D)) {
			this.mainPlayer.getSpeed().x = 0;
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		System.out.println("called touchDown");
		requestMove = true;
		fezTouchDown = true;
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		System.out.println("called touchUp");

		if (!requestMove) return true;

		System.out.println("move request!");

		Vector3 target = new Vector3(screenX, screenY, 0);

		this.camera.unproject(target);
		this.world.setMainPlayerTarget(target);
		this.world.pathPlan.targetChanged(screenX, screenY);
		fezTouchDown = false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Gdx.app.log("WorldController ", "arrastou x="+screenX +" y="+screenY +"  pointer="+pointer);

		if (fezTouchDown == true) {
			firstX = screenX; firstY=screenY;
			fezTouchDown = false;
		}

		if (manhattanDistance(screenX, screenY) > 128)
			requestMove=false;

		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

	private void updateAvatars(float delta) {
		for(Avatar avatar : this.avatars) {
			avatar.update(delta);
		}
	}

	private void setEnemiesTargets() {
		for (Enemy enemy : this.enemies) enemy.setTarget(this.players);
	}

	private int manhattanDistance(int screenX, int screenY){
		int distance = Math.abs(screenX - firstX) + Math.abs(screenY - firstY) ;
		return distance;
	}
}

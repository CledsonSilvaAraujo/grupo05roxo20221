package br.uff.ic.lek.game;

import br.uff.ic.lek.actors.Avatar;
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
	private Avatar player;
	private List<Avatar> avatars = new ArrayList<Avatar>();
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
		this.camera = this.world.getCamera();
		this.avatars = this.world.getAvatars();
		this.player = this.avatars.get(0);
	}

	public void update(float delta) {
		this.updateAvatars(delta);
		this.minCameraX = this.camera.zoom * (this.camera.viewportWidth / 2);
		this.maxCameraX = World.getMapWidthPixel() - minCameraX;
		this.minCameraY = camera.zoom * (this.camera.viewportHeight / 2);
		this.maxCameraY = World.getMapHeightPixel() - minCameraY;
		this.camera.position.set(
			Math.min(maxCameraX, Math.max(this.avatars.get(0).getX(), minCameraX)),
			Math.min(maxCameraY, Math.max(this.player.getY(), minCameraY)),
			0
		);
		this.camera.update();
	}

	public void onNotification(Dictionary<String,String> cmdData) {
		for (Avatar avatar : this.avatars) {
			if (avatar.getAuthUID() == cmdData.get("uID")) {
				if (cmdData.get("cmd") == "MOVE") {
					avatar.setTarget(
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
			this.player.setOrientation(Avatar.Compass.NORTH);
			this.player.setState(State.WALKING);
			this.player.getVelocity().y = Avatar.MAX_SPEED;
		}
		if((keycode == Input.Keys.S)) {
			this.player.setOrientation(Avatar.Compass.SOUTH);
			this.player.setState(State.WALKING);
			this.player.getVelocity().y = -Avatar.MAX_SPEED;
		}
		if((keycode == Input.Keys.A)) {
			this.player.setOrientation(Avatar.Compass.WEST);
			this.player.setState(State.WALKING);
			this.player.getVelocity().x = -Avatar.MAX_SPEED;
		}
		if((keycode == Input.Keys.D)) {
			this.player.setOrientation(Avatar.Compass.EAST);
			this.player.setState(State.WALKING);
			this.player.getVelocity().x = Avatar.MAX_SPEED;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if((keycode == Input.Keys.W) || (keycode == Input.Keys.S)) {
			this.player.getVelocity().y = 0;
		}
		if((keycode == Input.Keys.A) || (keycode == Input.Keys.D)) {
			this.player.getVelocity().x = 0;
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

		float x = this.player.getX();
		float y = this.player.getY();

		this.world.getAvatar().setState(State.WALKING);

		Vector3 target = new Vector3(screenX, screenY, 0);

		this.camera.unproject(target);

		if (Math.abs(target.x - x) > Math.abs(target.y - y)) {
			if ((target.x - x) > 0)
				this.player.setOrientation(Avatar.Compass.EAST);
			else
				this.player.setOrientation(Avatar.Compass.WEST);
		} else {
			if ((target.y - y) > 0)
				this.player.setOrientation(Avatar.Compass.NORTH);
			else
				this.player.setOrientation(Avatar.Compass.SOUTH);
		}

		this.player.setTarget(target);
		world.pathPlan.targetChanged(screenX, screenY);
		fezTouchDown = false;
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

	private int manhattanDistance(int screenX, int screenY){
		int distance = Math.abs(screenX - firstX) + Math.abs(screenY - firstY) ;
		return distance;
	}
}

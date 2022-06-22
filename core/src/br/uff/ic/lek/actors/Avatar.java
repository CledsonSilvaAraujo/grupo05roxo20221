package br.uff.ic.lek.actors;

import br.uff.ic.lek.PlayerData;
import br.uff.ic.lek.game.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Avatar extends Sprite {
	public static final float MAX_SPEED = 128f;

	public enum Compass {
		SOUTH, NORTH, WEST, EAST, SOUTH_WEST, NORTH_WEST, SOUTH_EAST, NORTH_EAST
	}
	
	public enum State {
		IDLE, WALKING, DYING
	}

	private String authUID;
	private State state = State.IDLE;
	private AvatarPower avatarPower;

	private Compass orientation;
	private Vector3 position = new Vector3(0,0,0);
	private Vector3 target = new Vector3(0,0,0);
	private Vector3 speed = new Vector3(0,0,0);

	private float elapsedTime;
	private float tempoAcumulado;
	private double playerMovementAngle;

	private TextureRegion currentFrame;
	private Animation<TextureRegion> walkingWest;
	private Animation<TextureRegion> walkingEast;
	private Animation<TextureRegion> walkingNorth;
	private Animation<TextureRegion> walkingSouth;
	private TextureRegion[] walkingWestFrames = new TextureRegion[3];
	private TextureRegion[] walkingEastFrames = new TextureRegion[3];
	private TextureRegion[] walkingNorthFrames = new TextureRegion[3];
	private TextureRegion[] walkingSouthFrames = new TextureRegion[3];
	private Animation<TextureRegion> walkingSouthWest;
	private Animation<TextureRegion> walkingNorthWest;
	private Animation<TextureRegion> walkingSouthEast;
	private Animation<TextureRegion> walkingNorthEast;
	private TextureRegion[] walkingSouthWestFrames = new TextureRegion[3];
	private TextureRegion[] walkingNorthWestFrames = new TextureRegion[3];
	private TextureRegion[] walkingSouthEastFrames = new TextureRegion[3];
	private TextureRegion[] walkingNorthEastFrames = new TextureRegion[3];

	public Avatar(Sprite sprite, float x, float y, String authUID) {
		super(sprite);
		this.setPosition(x, y);
		this.authUID = authUID;
		this.avatarPower = new AvatarPower(100.0f);
		position.x = x;
		position.y = y;
		position.z = (float) 0.0;
		this.state = State.IDLE;
		this.orientation = Compass.SOUTH;

		walkingSouthFrames[0] = World.atlasPlayerS_W_E_N.findRegion("South01");
		walkingSouthFrames[1] = World.atlasPlayerS_W_E_N.findRegion("South02");
		walkingSouthFrames[2] = World.atlasPlayerS_W_E_N.findRegion("South03");
		this.walkingSouth = new Animation<>(0.1f, walkingSouthFrames);

		walkingWestFrames[0] = World.atlasPlayerS_W_E_N.findRegion("West01");
		walkingWestFrames[1] = World.atlasPlayerS_W_E_N.findRegion("West02");
		walkingWestFrames[2] = World.atlasPlayerS_W_E_N.findRegion("West03");
		this.walkingWest = new Animation<>(0.1f, walkingWestFrames);

		walkingEastFrames[0] = World.atlasPlayerS_W_E_N.findRegion("East01");
		walkingEastFrames[1] = World.atlasPlayerS_W_E_N.findRegion("East02");
		walkingEastFrames[2] = World.atlasPlayerS_W_E_N.findRegion("East03");
		this.walkingEast = new Animation<>(0.1f, walkingEastFrames);

		walkingNorthFrames[0] = World.atlasPlayerS_W_E_N.findRegion("North01");
		walkingNorthFrames[1] = World.atlasPlayerS_W_E_N.findRegion("North02");
		walkingNorthFrames[2] = World.atlasPlayerS_W_E_N.findRegion("North03");
		this.walkingNorth = new Animation<>(0.1f, walkingNorthFrames);

		walkingSouthWestFrames[0] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthWest01");
		walkingSouthWestFrames[1] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthWest02");
		walkingSouthWestFrames[2] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthWest03");
		this.walkingSouthWest = new Animation<>(0.1f, walkingSouthWestFrames);

		walkingNorthWestFrames[0] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthWest01");
		walkingNorthWestFrames[1] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthWest02");
		walkingNorthWestFrames[2] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthWest03");
		this.walkingNorthWest = new Animation<>(0.1f, walkingNorthWestFrames);

		walkingSouthEastFrames[0] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthEast01");
		walkingSouthEastFrames[1] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthEast02");
		walkingSouthEastFrames[2] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthEast03");
		this.walkingSouthEast = new Animation<>(0.1f, walkingSouthEastFrames);

		walkingNorthEastFrames[0] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthEast01");
		walkingNorthEastFrames[1] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthEast02");
		walkingNorthEastFrames[2] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthEast03");
		this.walkingNorthEast = new Animation<>(0.1f, walkingNorthEastFrames);

		tempoAcumulado = 0.0f;
	}

	public void update(float delta) {
		this.move(delta);
		this.collide(delta);
	}

	public void draw(OrthographicCamera camera, BitmapFont font, String mensagem, Batch batch) {
		this.update(Gdx.graphics.getDeltaTime());
		elapsedTime += Gdx.graphics.getDeltaTime();

		if (this.getState() == State.IDLE) {
			if(this.orientation == Compass.SOUTH) {
				this.currentFrame = World.atlasPlayerS_W_E_N.findRegion("South02");
			} else if(this.orientation == Compass.NORTH) {
				this.currentFrame = World.atlasPlayerS_W_E_N.findRegion("North02");
			} else	if(this.orientation == Compass.WEST) {
				this.currentFrame = World.atlasPlayerS_W_E_N.findRegion("West02");
			} else 	if(this.orientation == Compass.EAST) {
				this.currentFrame = World.atlasPlayerS_W_E_N.findRegion("East02");
			}
		}

		if (this.getState() == State.WALKING) {
			if(this.orientation == Compass.WEST) {
				this.currentFrame = (TextureRegion) walkingWest.getKeyFrame(this.elapsedTime, true);
			} else 	if(this.orientation == Compass.EAST) {
				this.currentFrame = (TextureRegion)walkingEast.getKeyFrame(this.elapsedTime, true);
			} else 	if(this.orientation == Compass.NORTH) {
				this.currentFrame = (TextureRegion)walkingNorth.getKeyFrame(this.elapsedTime, true);
			} else 	if(this.orientation == Compass.SOUTH) {
				this.currentFrame = (TextureRegion)walkingSouth.getKeyFrame(this.elapsedTime, true);
			} else 	if(this.orientation == Compass.SOUTH_WEST) {
				this.currentFrame = (TextureRegion)walkingSouthWest.getKeyFrame(this.elapsedTime, true);
			} else 	if(this.orientation == Compass.SOUTH_EAST) {
				this.currentFrame = (TextureRegion)walkingSouthEast.getKeyFrame(this.elapsedTime, true);
			} else 	if(this.orientation == Compass.NORTH_WEST) {
				this.currentFrame = (TextureRegion)walkingNorthWest.getKeyFrame(this.elapsedTime, true);
			} else 	if(this.orientation == Compass.NORTH_EAST) {
				this.currentFrame = (TextureRegion)walkingNorthEast.getKeyFrame(this.elapsedTime, true);
			}
		}

		batch.draw(this.currentFrame, this.getX(), this.getY());

		if ("debug".equals(mensagem)) {
			double myX = this.getX();
			double myY = this.getY();
			DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
			df.setMaximumFractionDigits(0);
			mensagem = "("+df.format(myX)+", "+df.format(myY)+")";
		}
		
		font.draw(batch, mensagem,  this.getX(), this.getY()+this.getHeight()*1.3f);

		batch.end();
		batch.begin();
	}
	
	public State getState() {
		return state;
	}

	public String getAuthUID() {
		return authUID;
	}

	public Compass getOrientation() {
		return orientation;
	}

	public Vector3 getVelocity() {
		return speed;
	}

	public PlayerData getFirebaseData() {
		PlayerData pd = PlayerData.myPlayerData();
		pd.setAuthUID(this.authUID);
		pd.setWriterUID(this.authUID);
		pd.setGameState(PlayerData.States.READYTOPLAY);
		pd.setChat("empty");

		class CmdObject {
			private String command;

			public CmdObject(Float px, Float py,String event){
				this.command = "{cmd:"+event+",px:"+ px +",py:"+py+"}";
			}

			public String getCommand() {
				return this.command;
			}
		};

		pd.setCmd(new CmdObject(this.getX(),this.getY(),"Question").getCommand());
		pd.setAvatarType("A");
		return pd;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setAuthUID(String authUID) {
		this.authUID = authUID;
	}

	public void setTarget(float x,float y) {
		this.target.x = x;
		this.target.y = y;
	}

	public void setTarget(Vector3 target) {
		this.target = target;
	}

	public void setOrientation(Compass orientation) {
		this.orientation = orientation;
	}

	public void defineOrientation(double anguloGraus) {
		if (anguloGraus > 22.5 &&  anguloGraus <= 67.5)
			this.orientation = Compass.NORTH_EAST;
		else if (anguloGraus > 67.5 &&  anguloGraus <= 112.5)
			this.orientation = Compass.NORTH;
		else if (anguloGraus > 112.5 &&  anguloGraus <= 157.5)
			this.orientation = Compass.NORTH_WEST;
		else if (anguloGraus > 157.5 &&  anguloGraus <= 202.5)
			this.orientation = Compass.WEST;
		else if (anguloGraus > 202.5 &&  anguloGraus <= 247.5)
			this.orientation = Compass.SOUTH_WEST;
		else if (anguloGraus > 247.5 &&  anguloGraus <= 292.5)
			this.orientation = Compass.SOUTH;
		else if (anguloGraus > 292.5 &&  anguloGraus <= 337.5)
			this.orientation = Compass.SOUTH_EAST;
		else
			this.orientation = Compass.EAST;
	}

	protected void move(float delta) {
		if (this.isInTarget()) {
			this.setState(State.IDLE);
			return;
		}

		double distancia = Math.sqrt((position.x - this.target.x)*(position.x - this.target.x) + (position.y - this.target.y)*(position.y - this.target.y));
		float x = (float) ((this.target.x -position.x)/distancia);
		float y = (float) ((this.target.y -position.y)/distancia);
		double angulo1 = Math.acos((double) x)*180.0/Math.PI;
		double angulo2 = Math.asin((double) y)*180.0/Math.PI;

		if(angulo2 >= 0.0)
			playerMovementAngle = angulo1;
		else
			playerMovementAngle = 360.0 - angulo1;

		defineOrientation(playerMovementAngle);
		double posx = (this.target.x -position.x)/distancia * Avatar.MAX_SPEED*delta + position.x;
		double posy = (this.target.y -position.y)/distancia * Avatar.MAX_SPEED*delta + position.y;
		position.x = (float) posx;
		position.y = (float) posy;
		this.setPosition(position.x, position.y);
		this.setState(State.WALKING);
	}

	protected void collide(float delta) {
		avatarPower.setPower(avatarPower.getPower() - 0.01f);

		if(this.speed.x > Avatar.MAX_SPEED)
			this.speed.x = Avatar.MAX_SPEED;
		else if(this.speed.x < -Avatar.MAX_SPEED)
			this.speed.x = -Avatar.MAX_SPEED;

		if(this.speed.y > Avatar.MAX_SPEED)
			this.speed.y = Avatar.MAX_SPEED;
		else if(this.speed.y < -Avatar.MAX_SPEED)
			this.speed.y = -Avatar.MAX_SPEED;
			
		if(Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)) {
			tempoAcumulado += delta;
			if (tempoAcumulado > 1.0){
				tempoAcumulado = 0.0f;
			}
		}

		if(this.getX() < 0) {
			Gdx.input.vibrate(50);
			this.setX(1);
			position.x = this.getX();
			this.getVelocity().x = 0;
			this.setState(State.IDLE);
		} else if(this.getX() > (World.getMapWidthPixel() - this.getWidth())) {
			Gdx.input.vibrate(50);
			this.setX(World.getMapWidthPixel() - this.getWidth() - 1);
			position.x = this.getX();
			this.getVelocity().x = 0;
			this.setState(State.IDLE);
		}
		if(this.getY() < 0) {
			Gdx.input.vibrate(50);
			this.setY(1);
			position.y = this.getY();
			this.getVelocity().y = 0;
			this.setState(State.IDLE);
		} else if(this.getY() > (World.getMapHeightPixel() - this.getHeight())) {
			Gdx.input.vibrate(50);
			this.setY(World.getMapHeightPixel() - this.getHeight() -1);
			position.y = this.getY();
			this.getVelocity().y = 0;
			this.setState(State.IDLE);
		}
	}

	private boolean isInTarget() {
		return this.position.dst(this.target.x, this.target.y, 0) < 32/2;
	}
}

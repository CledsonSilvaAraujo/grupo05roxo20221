package br.uff.ic.lek.actors;

import br.uff.ic.lek.PlayerData;
import br.uff.ic.lek.game.World;
import br.uff.ic.lek.game.WorldController;
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

    String authUID;
    private AvatarPower avatarPower;
    private Compass orientation;
    public enum Compass {
        SOUTH, NORTH, WEST, EAST, SOUTH_WEST, NORTH_WEST, SOUTH_EAST, NORTH_EAST
    }
    public enum State {
        IDLE, WALKING, DYING
    }
    private State state = State.IDLE;
    private Vector3 vetorUnitarioMovimento;
    private double playerMovementAngle;
    public static float SPEED = 128f;
    private Vector3 velocity = new Vector3();
    private Vector3 temp = new Vector3(0,0,0);
    private Vector3 current = new Vector3(0,0,0);
    private Vector3 goodPos = new Vector3(0,0,0);
    private Animation walkingWest;
    private Animation walkingEast;
    private Animation walkingNorth;
    private Animation walkingSouth;
    private TextureRegion[] walkingWestFrames = new TextureRegion[3];
    private TextureRegion[] walkingEastFrames = new TextureRegion[3];
    private TextureRegion[] walkingNorthFrames = new TextureRegion[3];
    private TextureRegion[] walkingSouthFrames = new TextureRegion[3];
    private Animation walkingSouthWest;
    private Animation walkingNorthWest;
    private Animation walkingSouthEast;
    private Animation walkingNorthEast;
    private TextureRegion[] walkingSouthWestFrames = new TextureRegion[3];
    private TextureRegion[] walkingNorthWestFrames = new TextureRegion[3];
    private TextureRegion[] walkingSouthEastFrames = new TextureRegion[3];
    private TextureRegion[] walkingNorthEastFrames = new TextureRegion[3];
    private float elapsedTime;
    private TextureRegion currentFrame;
    private float tempoAcumulado;
    public boolean houveColisao;

    public Avatar(Sprite sprite, float x, float y, String authUID) {
        super(sprite);
        vetorUnitarioMovimento = new Vector3(0,1,0);
        this.setPosition(x, y);
        this.authUID = authUID;
        this.avatarPower = new AvatarPower(100.0f);
        temp.x = x;
        temp.y = y;
        temp.z = (float) 0.0;
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

    public void setState(State state) {
        this.state = state;
    }

    public String getAuthUID() {
        return authUID;
    }

    public void setAuthUID(String authUID) {
        this.authUID = authUID;
    }

    public State getState() {
        return state;
    }

    public Compass getOrientation(){
        return orientation;
    }

    public void setOrientation(Compass orientation){
        this.orientation = orientation;
    }

    public void defineOrientation(double anguloGraus){
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
        else  //		if (anguloGraus > 337.5 &&  anguloGraus <= 22.5)
            this.orientation = Compass.EAST;
    }

    protected void move(float delta){
        System.out.println("entrei e sou");
        if(!WorldController.clicado) return;
        System.out.println("passei e sou o "+this.authUID);
        double distancia = Math.sqrt((current.x - WorldController.target.x)*(current.x - WorldController.target.x) + (current.y - WorldController.target.y)*(current.y - WorldController.target.y));
        vetorUnitarioMovimento.x = (float) ((WorldController.target.x -current.x)/distancia);
        vetorUnitarioMovimento.y = (float) ((WorldController.target.y -current.y)/distancia);
        double angulo1 = Math.acos((double) vetorUnitarioMovimento.x )*180.0/Math.PI;
        double angulo2 = Math.asin((double) vetorUnitarioMovimento.y )*180.0/Math.PI;

        if(angulo2 >= 0.0){
            playerMovementAngle = angulo1;
        }
        else{
            playerMovementAngle = 360.0 - angulo1;
        }

        defineOrientation(playerMovementAngle);
        double posx = (WorldController.target.x -current.x)/distancia * Avatar.SPEED*delta  + current.x;
        double posy = (WorldController.target.y -current.y)/distancia * Avatar.SPEED*delta  + current.y;
        System.out.println("esse é o delta "+delta);
        current.x = (float) posx;
        current.y = (float) posy;
        this.setPosition(current.x, current.y);
        this.setState(State.WALKING);
        if(this.current.dst(WorldController.target.x, WorldController.target.y, 0) < 32/2) {
            WorldController.clicado = false;
            this.setState(State.IDLE);
        }
    }

    protected void collide(float delta){
        avatarPower.setPower(avatarPower.getPower() - 0.01f);


        if(this.velocity.x > Avatar.SPEED) {
            this.velocity.x = Avatar.SPEED;
        }
        if(this.velocity.x < -Avatar.SPEED) {
            this.velocity.x = -Avatar.SPEED;
        }
        if(this.velocity.y > Avatar.SPEED) {
            this.velocity.y = Avatar.SPEED;
        }
        if(this.velocity.y < -Avatar.SPEED) {
            this.velocity.y = -Avatar.SPEED;
        }

        temp.x = this.getX();
        temp.y = this.getY();
        current = temp;

        if(Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)) {
            tempoAcumulado += delta;
            if (tempoAcumulado > 1.0){
                tempoAcumulado = 0.0f;
            }
        }

        if(houveColisao == false){
            goodPos.x = (int) current.x;
            goodPos.y = (int) current.y;
        }
        if(this.getX() < 0) {
            Gdx.input.vibrate(50);
            this.setX(1);
            temp.x = this.getX();
            this.getVelocity().x = 0;
            this.setState(State.IDLE);
        } else if(this.getX() > (World.getMapWidthPixel() - this.getWidth())) {
            Gdx.input.vibrate(50);
            this.setX(World.getMapWidthPixel() - this.getWidth() - 1);
            temp.x = this.getX();
            this.getVelocity().x = 0;
            this.setState(State.IDLE);
        }
        if(this.getY() < 0) {
            Gdx.input.vibrate(50);
            this.setY(1);
            temp.y = this.getY();
            this.getVelocity().y = 0;
            this.setState(State.IDLE);
        } else if(this.getY() > (World.getMapHeightPixel() - this.getHeight())) {
            Gdx.input.vibrate(50);
            this.setY(World.getMapHeightPixel() - this.getHeight() -1);
            temp.y = this.getY();
            this.getVelocity().y = 0;
            this.setState(State.IDLE);
        }
    }

    public void update(float delta) {

        move(delta);
        collide(delta);
    }

    public void draw(OrthographicCamera camera, BitmapFont font, String mensagem, Batch batch) {
        this.update(Gdx.graphics.getDeltaTime());
        elapsedTime += Gdx.graphics.getDeltaTime();
        if(this.getState() == State.IDLE) {

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
        if(this.getState() == State.WALKING) {
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

        if ("debug".equals(mensagem)){
            double myX = this.getX();
            double myY = this.getY();
            DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(0); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
            mensagem = "("+df.format(myX)+", "+df.format(myY)+")";
        } else {
        }
        font.draw(batch, mensagem,  this.getX(), this.getY()+this.getHeight()*1.3f);

        batch.end();
        batch.begin();
    }

    @Override
    public float getX() {
        return super.getX();
    }

    @Override
    public float getY() {
        return super.getY();
    }

    @Override
    public float getWidth() {
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        return super.getHeight();
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public PlayerData getFirebaseData(){
        PlayerData pd = PlayerData.myPlayerData();
        pd.setAuthUID(this.authUID);
        pd.setWriterUID(this.authUID);
        pd.setGameState(PlayerData.States.READYTOPLAY);
        pd.setChat("empty");
        class CmdObject {
            Float px;
            Float py;
            String event;
            String comando;
            public CmdObject(Float px, Float py,String event){
                this.px = px;
                this.py = py;
                this.event = event;
                this.comando = "{cmd:"+event+",px:"+ px +",py:"+py+"}";
            }

        };
        pd.setCmd(new CmdObject(this.getX(),this.getY(),"Question").comando);
        pd.setAvatarType("A");
        return pd;
    }
}

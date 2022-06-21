/*
    Fábrica de Software para Educação
    Professor Lauro Kozovits, D.Sc.
    ProfessorKozovits@gmail.com
    Universidade Federal Fluminense, UFF
    Rio de Janeiro, Brasil
    Subprojeto: Alchemie Zwei

    Partes do software registradas no INPI como integrantes de alguns apps para smartphones
    Copyright @ 2016..2022

    Se você deseja usar partes do presente software em seu projeto, por favor mantenha esse cabeçalho e peça autorização de uso.
    If you wish to use parts of this software in your project, please keep this header and ask for authorization to use.

 */
package br.uff.ic.lek.game;

import br.uff.ic.lek.actors.Avatar;
import br.uff.ic.lek.actors.Avatar.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.List;

public class WorldController implements InputProcessor {

    private World world;
    private List<Avatar> avatars = new ArrayList<Avatar>();
    private Avatar player;
    private OrthographicCamera camera;
    private boolean requestMove;
    private boolean fezTouchDown;
    private int firstX, firstY;
    public float minCameraX;
    public float maxCameraX;
    public float minCameraY;
    public float maxCameraY;
    public static Vector3 vec;
    public static Vector3 target;
    public static boolean clicado;


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
                0);
        this.camera.update();
    }

    @Override
    public boolean keyDown(int keycode) {
        if((keycode == Input.Keys.W)) {
            this.player.setOrientation(Avatar.Compass.NORTH);
            this.player.setState(State.WALKING);
            this.player.getVelocity().y = Avatar.SPEED;
            WorldController.clicado = false;
            //Gdx.app.log(" ", "avatar moving upwards!");
        }
        if((keycode == Input.Keys.S)) {
            this.player.setOrientation(Avatar.Compass.SOUTH);
            this.player.setState(State.WALKING);
            this.player.getVelocity().y = -Avatar.SPEED;
            WorldController.clicado = false;
            //Gdx.app.log(" ", "avatar moving downwards!");
        }
        if((keycode == Input.Keys.A)) {
            this.player.setOrientation(Avatar.Compass.WEST);
            this.player.setState(State.WALKING);
            this.player.getVelocity().x = -Avatar.SPEED;
            WorldController.clicado = false;
            //Gdx.app.log(" ", "avatar moving to the left!");
        }
        if((keycode == Input.Keys.D)) {
            this.player.setOrientation(Avatar.Compass.EAST);
            this.player.setState(State.WALKING);
            this.player.getVelocity().x = Avatar.SPEED;
            WorldController.clicado = false;
            //Gdx.app.log(" ", "avatar moving to the right!");
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
        requestMove = true;
        fezTouchDown = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float x = this.player.getX();
        float y = this.player.getY();
        if(requestMove){
            this.world.getAvatar().setState(State.WALKING);
            WorldController.vec = new Vector3(screenX, screenY, 0);
            this.camera.unproject(vec);
            WorldController.target = new Vector3(vec);
            if (Math.abs(WorldController.target.x - x) > Math.abs(WorldController.target.y - y)){
                if ((WorldController.target.x - x) > 0){
                    this.player.setOrientation(Avatar.Compass.EAST);
                } else {
                    this.player.setOrientation(Avatar.Compass.WEST);
                }
            } else {
                if ((WorldController.target.y - y) > 0){
                    this.player.setOrientation(Avatar.Compass.NORTH);
                } else {
                    this.player.setOrientation(Avatar.Compass.SOUTH);
                }
            }

            WorldController.clicado = true;
            world.pathPlan.targetChanged(screenX, screenY);
            fezTouchDown = false;
        }
        return true;
    }

    int manhattanDistance(int screenX, int screenY){
        int distance = Math.abs(screenX - firstX) + Math.abs(screenY - firstY) ;
        return distance;
    }

    public void comandoMoveTo(float moveToX, float moveToY){
        float x = this.player.getX();
        float y = this.player.getY();
        if(true){
            this.world.getAvatar().setState(State.WALKING);
            WorldController.vec = new Vector3(moveToX, moveToY, 0);
            WorldController.target = new Vector3(vec);
            if (Math.abs(WorldController.target.x - x) > Math.abs(WorldController.target.y - y)){
                if ((WorldController.target.x - x) > 0){
                    this.player.setOrientation(Avatar.Compass.EAST);
                } else {
                    this.player.setOrientation(Avatar.Compass.WEST);
                }
            } else {
                if ((WorldController.target.y - y) > 0){
                    this.player.setOrientation(Avatar.Compass.NORTH);
                } else {
                    this.player.setOrientation(Avatar.Compass.SOUTH);
                }
            }
            WorldController.clicado = true;
            fezTouchDown = false;
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Gdx.app.log("WorldController ", "arrastou x="+screenX +" y="+screenY +"  pointer="+pointer);
        if (fezTouchDown == true){
            firstX = screenX; firstY=screenY;
            fezTouchDown = false;
        }
        if (manhattanDistance(screenX, screenY) > 128){
            requestMove=false;
        }
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

    private void updateAvatars(float delta){
        this.avatars.get(0).update(delta);
//        for(Avatar avatar: this.avatars){
//            avatar.update(delta);
//        }
    }
}

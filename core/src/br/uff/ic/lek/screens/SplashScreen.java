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
package br.uff.ic.lek.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import br.uff.ic.lek.game.ClassThreadComandos;
import com.badlogic.gdx.utils.Align;

import br.uff.ic.lek.game.World;

public class SplashScreen implements Screen {

	/*

	 	At Android launcher Android use the following:

		config.useWakelock = true;
		config.useAccelerometer = true;
		useImmersiveMode (true);


	 */
    public static String USER;
    public static String PARTY;
    private Texture texture = new Texture(Gdx.files.internal("img/numberfive.jpg"));
    private Texture textureLog= new Texture(Gdx.files.internal("img/TinyLuck1.png"));
    private Texture textureUser=new Texture(Gdx.files.internal("img/Usuario.png"));
    private Texture textureParty=new Texture(Gdx.files.internal("img/Sala.png"));
    private Image sala = new Image(textureParty);
    private Image usuario =new Image(textureUser);
    private Image tittle = new Image(textureLog);
    private Image splashImage = new Image(texture);
    private Stage stage = new Stage();
    private PlayScreen ps;
    private TextField party;
    private TextField username;

    @Override
    public void show() {
        // If the image is not the same size as the screen
        this.splashImage.setWidth(Gdx.graphics.getWidth());
        this.splashImage.setHeight(Gdx.graphics.getHeight());
        this.tittle.setWidth(1000);
        this.tittle.setHeight(500);
        this.tittle.setPosition(Gdx.graphics.getWidth()/2-500,Gdx.graphics.getHeight()-450);
        this.usuario.setWidth(93);
        this.usuario.setHeight(25);
        this.usuario.setPosition(Gdx.graphics.getWidth()/2-200,Gdx.graphics.getHeight()/5+340);
        this.sala.setWidth(56);
        this.sala.setHeight(23);
        this.sala.setPosition(Gdx.graphics.getWidth()/2-200,Gdx.graphics.getHeight()/8+220);
        ps = new PlayScreen();
        Gdx.input.setInputProcessor(this.stage);
        Skin skin =new Skin(Gdx.files.internal("skin/uiskin.json"));
        TextButton btnLogin = new TextButton("clike me",skin);
        btnLogin.setPosition(Gdx.graphics.getWidth()/2-200,Gdx.graphics.getHeight()/9.5f);
        btnLogin.setSize(400,100);

        username= new TextField("",skin);
        username.setPosition(Gdx.graphics.getWidth()/2-200,Gdx.graphics.getHeight()/5+240);
        username.setSize(400,100);

        party= new TextField("",skin);
        party.setPosition(Gdx.graphics.getWidth()/2-200,Gdx.graphics.getHeight()/8+120);
        party.setSize(400,100);
        this.stage.addActor(splashImage);
        this.stage.addActor(tittle);
        stage.addActor(sala);
        stage.addActor(usuario);
        stage.addActor(username);
        stage.addActor(party);
        stage.addActor(btnLogin);

        btnLogin.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button){
                USER=username.getText();
                PARTY=party.getText();
                criamundo();
            }
        });



    }
    public void criamundo(){
        this.splashImage.addAction(
                Actions.sequence(
                        Actions.alpha(0.0f),
                        Actions.fadeIn(1.0f),
                        Actions.run(new Runnable() { //Actions.delay(1) Actions.alpha(0.0f),
                                        @Override
                                        public void run() {
                                            World.load();
                                        }
                                    }
                        ),
                        Actions.fadeOut(1.0f),
                        Actions.run(new Runnable() { //Actions.delay(1) Actions.alpha(0.0f),
                                        @Override
                                        public void run() {
                                            ((Game) Gdx.app.getApplicationListener()).setScreen(ps);
                                        }
                                    }
                        )
                )
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // sets the clear color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // clear the batch
        this.stage.act(); // update all actors
        this.stage.draw(); // draw all actors on the Stage.getBatch()

    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        this.dispose();
    }

    @Override
    public void dispose() {
        this.texture.dispose();
        this.stage.dispose();
    }
}

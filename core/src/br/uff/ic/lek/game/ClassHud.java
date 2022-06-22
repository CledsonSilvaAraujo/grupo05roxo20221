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

/*
O nome HUD ou Head-up Display vem dos painéis de aviação onde a tarefa
 de pilotar ou jogar se desenrola e o painel de comandos é projetado sobre visor.
 É importante ter em mente que o sistema de input é multiplexado
 (veja World.world.inputMultiplexer), para que os comandos
 para os botões não se misturem aos comandos para o jogo. Cada um está num sistema
  de coordenadas diferente.
 */
package br.uff.ic.lek.game;

/**
 * Created by Prof. Lauro Eduardo Kozovits, TCC, UFF. Projeto Memória Periódica on 19/09/2016.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;



public class ClassHud implements InputProcessor, Disposable{
    public static Stage stage;

    private Integer worldTimer;
    private boolean timeUp;
    private float timeCount;
    private static Integer score;

    public Table tableGameOver;
    private final Table table;

    public static String jogador1="Quem Joga?\n-Você";
    public static String jogador2="Quem Joga?\n-Oponente";
    public static String jogador=jogador1;

		private Label countdownLabel;
    private static Label scoreLabel;

		public static ClassCommandButton btPLAY;
    public static ClassCommandButton btRESIZE;
    public static ClassCommandButton btINTERNET;
    public static ClassCommandButton btSOUND;
    public static ClassCommandButton btHELP;
    public static ClassCommandButton btINFO;
    public static ClassCommandButton btEXIT;

    // LEK public static ClassGameOverButton btGAME_OVER;

    // LEK public ClassDisplayText displayText;

    public BitmapFont fontSmall; //www.1001fonts.com
    public BitmapFont fontMedium; //www.1001fonts.com
    public BitmapFont fontLarge; //www.1001fonts.com

    public ClassHud(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        int V_WIDTH = Gdx.graphics.getWidth();
        int V_HEIGHT = Gdx.graphics.getHeight();


        final Viewport viewport;
        viewport = new ExtendViewport(V_WIDTH, V_HEIGHT, new OrthographicCamera());

        ClassHud.stage = new Stage(viewport, sb);

        this.table = new Table();
        this.table.right();
        this.table.setFillParent(true);

				FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("LiberationSans-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        float correcao =Gdx.graphics.getDensity(); // Gdx.graphics.getDensity(); //Gdx.graphics.getWidth()/1920.0f; //Gdx.graphics.getDensity()*
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-+()[]_^";
        parameter.size = (int)(16f * correcao);
        fontSmall = generator.generateFont(parameter);
        parameter.size = (int)(32f * correcao);
        fontMedium = generator.generateFont(parameter);
        parameter.size = (int)(64f * correcao);
        fontLarge = generator.generateFont(parameter);
        generator.dispose();

        final Table tableLeft;
        final Table tableRight;
        tableLeft = new Table();

        tableRight = new Table();

        TextureAtlas atlas;
        atlas = new TextureAtlas(Gdx.files.internal("menu.pack"));

        this.tableGameOver = new Table();
        this.tableGameOver.setFillParent(true);
        this.tableGameOver.center();
        this.tableGameOver.setVisible(true);
        Sprite frente = atlas.createSprite("roundAplay");
        Sprite fundo = atlas.createSprite("roundApause");
        ClassHud.btPLAY  = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_PLAY);
        tableRight.add(ClassHud.btPLAY).expandX().top().right();

        tableRight.row();
        frente = atlas.createSprite("roundAhelp");
        fundo = null;
        ClassHud.btHELP  = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_HELP);
        tableRight.add(ClassHud.btHELP).expandX().top().right();

        tableRight.row();
        Sprite frente1 = atlas.createSprite("roundAresize2");
        Sprite fundo1 =  atlas.createSprite("roundAresize1");
        ClassHud.btRESIZE  = new ClassCommandButton(frente1, fundo1, ClassCommandButton.BT_RESIZE);
        tableRight.add(ClassHud.btRESIZE).expandX().top().right();

        tableRight.row();
        frente = atlas.createSprite("roundAglassZoom");
        fundo = null;
        ClassHud.btINTERNET  = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_INTERNET_SEARCH);
        tableRight.add(ClassHud.btINTERNET).expandX().top().right();

        tableRight.row();
        frente = atlas.createSprite("roundAsoundON");
        fundo = atlas.createSprite("roundAsoundOFF");
        ClassHud.btSOUND = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_SOUND);
        tableRight.add(ClassHud.btSOUND).expandX().top().right();

        tableRight.row();
        frente = atlas.createSprite("roundAinfo");
        fundo = null;
        ClassHud.btINFO  = new ClassCommandButton( frente, fundo, ClassCommandButton.BT_INFO);
        tableRight.add(ClassHud.btINFO).expandX().top().right();

				tableRight.row();
        frente = atlas.createSprite("roundByeAndroid");
        fundo = null;
        ClassHud.btEXIT  = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_EXIT);
        tableRight.add(ClassHud.btEXIT).expandX().top().right();

        table.add(tableLeft).right();
        table.add(tableRight).right();

        ClassHud.stage.addActor(this.table);
        this.tableGameOver.setVisible(false);
        ClassHud.stage.addActor(this.tableGameOver);
    }

    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        fontSmall.dispose();
        fontMedium.dispose();
        fontLarge.dispose();
        ClassHud.stage.dispose();
    }


    public boolean isTimeUp() { return timeUp; }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}


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


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import br.uff.ic.lek.PlayerData;
import br.uff.ic.lek.utils.ClassToast;


public class ClassCommandButton extends ClassActorAccessor {
    public static final int  BT_NONE = 0;
    public static final int  BT_PLAY = 1;
    public static final int  BT_RESIZE = 2;
    public static final int  BT_INTERNET_SEARCH = 3;
    public static final int  BT_SOUND = 4;
    public static final int  BT_INFO = 5;
    public static final int  BT_HELP = 6;
    public static final int  BT_EXIT = 7;

    protected ShapeRenderer shapeRenderer = new ShapeRenderer();
    protected Sprite frente, fundo;
    protected boolean frontface;
    protected int acao;
    protected static int acaoAtual= ClassCommandButton.BT_NONE;
    protected SpriteDrawable spriteDrawableFrente;
    protected SpriteDrawable spriteDrawableFundo;
    protected ClassCommandButton previousButton;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(this.getColor());

        float x = getX();
        float y = getY();
        float height = getHeight();
        float width = getWidth();

        if (ClassCommandButton.acaoAtual == acao){
            batch.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            shapeRenderer.translate(x, y, 0);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Filled);
            shapeRenderer.setColor(0, 0, 1, 0);
            shapeRenderer.rect(0, 0, width, height);
            shapeRenderer.end();
            batch.begin();
        }

        if(frontface)
            spriteDrawableFrente.draw(batch, x, y, width, height);
        else
            spriteDrawableFundo.draw(batch, x, y, width, height);
    }

    // ClassCommandButton são usados no HUD
    public ClassCommandButton(Sprite tfrente, Sprite tfundo, int acao)  {
        super();
        frontface = true;
        previousButton = null;
        this.frente = tfrente;
        this.fundo = tfundo; //new Sprite(tfundo);
        //setScale(0.5f, 0.5f);
        // caber 10 botoes em uma tela vertical
        float escala = Gdx.graphics.getHeight()/(8.0f*frente.getHeight());
        setBounds(frente.getX(),frente.getY(),frente.getWidth()*escala,frente.getHeight()*escala);

        this.spriteDrawableFrente = new SpriteDrawable(this.frente);
        if (this.fundo == null){
            frontface = true;
            this.spriteDrawableFundo = this.spriteDrawableFrente;
        } else {
            this.spriteDrawableFundo = new SpriteDrawable(this.fundo);
        }
        setTouchable(Touchable.enabled);
        setOrigin(frente.getWidth()/2f, frente.getHeight()/2f);
        this.acao = acao;
        final int action = acao;
        Gdx.app.log("botao", "adiciona listener"+action);
        addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("botao", "touchDown " + x + ", " + y + " acao"+action);
                // LEK GameControl.gameState = GameControl.GAME_RUNNING;
                return true;// true não repassa false repassa
            }

            public boolean executouUmaVez = false;
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("botao", "touchDown " + x + ", " + y + " acao"+action);
                ClassCommandButton.acaoAtual = action;

                if(action == ClassCommandButton.BT_INTERNET_SEARCH) {
                    World.disparaAnimacaoCamera(2.5f, 7.0f);
                }
                else if(action == ClassCommandButton.BT_HELP) {
                    System.out.println("Avatar " + World.world.getMainPlayer().getX());
                    ClassThreadComandos.objetoAndroidFireBase.writePlayerData(World.world.getMainPlayer());
                    Color backgroundColor = new Color(0f, 0f, 0f, 0.5f);
                    Color fontColor = new Color(1, 1, 0, 0.5f);
                    String msg = "save device data";
                    ClassToast.toastRich(msg, backgroundColor, fontColor, 5f);
                }
                else if(action == ClassCommandButton.BT_PLAY) {
                    World.world.getMainPlayer().setFirebaseState(PlayerData.States.PLAYING);

                    PlayerData data = World.world.getMainPlayer().getFirebaseData();

                    ClassThreadComandos.objetoAndroidFireBase.writePlayerData(World.world.getMainPlayer());
                    ClassThreadComandos.objetoAndroidFireBase.writePartyData(World.world.getMainPlayer());
                    ClassThreadComandos.objetoAndroidFireBase.waitForPlayers(World.world.getMainPlayer().getFirebaseData().party);
                    Color backgroundColor = new Color(0f, 0f, 0f, 0.5f);
                    Color fontColor = new Color(1, 1, 0, 0.5f);
                    ClassToast.toastRich(
                            "getting players",
                            backgroundColor,
                            fontColor,
                            5f
                    );
                }
                else if(action == ClassCommandButton.BT_EXIT) {
                    ClassThreadComandos.objetoAndroidFireBase.finishAndRemoveTask();
                }
            }
        });
    }
    static boolean flag=false;

}

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
NPC.java
Nesse módulo os outros personagens (inimigos) animados por IA deveriam ser representados
Ainda não planejado. É preciso estudar os steering behaviors da LibGDX
 */

package br.uff.ic.lek.actors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import br.uff.ic.lek.actors.Avatar;

public class AvatarNPC extends Avatar {

    public AvatarNPC(Sprite sprite, float x, float y, String authUID) {

        super(sprite, x, y, "tulipa");
    }
    protected void move(float delta){

    }

}

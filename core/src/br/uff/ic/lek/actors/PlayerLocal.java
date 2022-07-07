package br.uff.ic.lek.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import br.uff.ic.lek.PlayerData;
import br.uff.ic.lek.game.ClassThreadComandos;


public class PlayerLocal extends Player {
	public PlayerLocal(Sprite sprite, float x, float y, String authUID) {
		super(sprite, x, y, authUID);
		ClassThreadComandos.objetoAndroidFireBase.writePlayerData(this);
	}

	public void setTarget(float x,float y) {
		this.target.x = x;
		this.target.y = y;
		ClassThreadComandos.objetoAndroidFireBase.writePlayerData(this);
	}

	public void setTarget(Vector3 target) {
		this.target = target;
		ClassThreadComandos.objetoAndroidFireBase.writePlayerData(this);
	}

	public void setFirebaseState(PlayerData.States state) {
		this.firebaseState = state;
		ClassThreadComandos.objetoAndroidFireBase.writePlayerData(this);
	}
}

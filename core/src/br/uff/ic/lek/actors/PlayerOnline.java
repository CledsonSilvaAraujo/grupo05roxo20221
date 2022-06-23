package br.uff.ic.lek.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;


public class PlayerOnline extends Player {
	public PlayerOnline(Sprite sprite, float x, float y, String authUID) {
		super(sprite, x, y, authUID);
	}

	protected void move(float delta) { }
}

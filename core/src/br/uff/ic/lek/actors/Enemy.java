package br.uff.ic.lek.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.List;


public class Enemy extends Avatar {
	public Enemy(Sprite sprite, float x, float y, String authUID) {
		super(sprite, x, y, authUID);
	}

	public void setTarget(final List<Player> players) {
		this.setTarget(players.get(0).getPosition());
	}

	protected boolean isInTarget(float delta) {
		return this.getTargetDistance() < 48;
	}
}

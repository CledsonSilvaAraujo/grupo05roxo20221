package br.uff.ic.lek.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.List;


public class Enemy extends Avatar {
	public static final float MAX_DISTANCE_FROM_TARGET = 100.0f;

	public Enemy(Sprite sprite, float x, float y, String authUID) {
		super(sprite, x, y, authUID);
	}

	public void setTarget(final List<Player> players) {
		this.setTarget(players.get(0).getPosition());
	}

	@Override
	protected boolean isInTarget() {
		return this.getTargetDistance() < MAX_DISTANCE_FROM_TARGET;
	}
}

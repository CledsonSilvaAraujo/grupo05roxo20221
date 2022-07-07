package br.uff.ic.lek.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;


public class AvatarPower {
	public static final float LIFE_STATUS_OK = 50.0f;
	public static final float LIFE_STATUS_CRITICAL = 20.0f;

	private ShapeRenderer powerIndicator;
	private float power;

	public AvatarPower(float f) {
		power = f;
		powerIndicator = null;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float l) {
		if (l < 0.0f)
			l = 0.0f;
		power = l;
	}

	public void draw(OrthographicCamera camera, float x, float y) {
		powerIndicator.setProjectionMatrix(camera.combined);
		powerIndicator.end();
		float rectWidth = 20.0f;
		float rectHeight = 6.0f;
		powerIndicator.begin(ShapeType.Filled);
		powerIndicator.setColor(new Color(1, 1, 1, 0.5f));
		powerIndicator.rect(x, y, rectWidth, rectHeight);
		powerIndicator.end();

		powerIndicator.begin(ShapeType.Filled);
		float r, g, b;
		if (power > AvatarPower.LIFE_STATUS_OK) {
			r = 0.0f;
			g = 0.5f;
			b = 0.5f;
		} else if (power > AvatarPower.LIFE_STATUS_CRITICAL) {
			r = 1.0f;
			g = 1.0f;
			b = 0.0f;
		} else {
			r = 1.0f;
			g = 0.0f;
			b = 0.0f;
		}
		powerIndicator.setColor(new Color(r, g, b, 0.5f));
		float powerRect = rectWidth * power / 100.0f;
		powerIndicator.rect(x, y, powerRect, rectHeight);
		powerIndicator.end();
		powerIndicator.begin(ShapeType.Line);
		powerIndicator.setColor(Color.BLACK);
		powerIndicator.rect(x, y, powerRect, rectHeight);
		powerIndicator.end();

		powerIndicator.begin(ShapeType.Line);
		powerIndicator.setColor(Color.BLACK);
		powerIndicator.rect(x, y, rectWidth, rectHeight);
		powerIndicator.end();
	}
}

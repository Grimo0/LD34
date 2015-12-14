package fr.langladure.ld34;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * @author Radnap
 */
public class Plant {

	private float x;
	private float y;
	private Array<AnimatedElement> steps;
	private int currentStep;
	private AnimatedElement step;


	public Plant(TextureAtlas atlas, float ratio, float x, float y) {
		this.x = x;
		this.y = y;
		steps = new Array<>();

		AnimatedElement step = new AnimatedElement(ratio, 0.3f, (TextureRegion[]) atlas.findRegions("plantStep0").toArray(TextureRegion.class), false);
		step.setX(x - step.getWidth() / 2f);
		step.setY(y);
		steps.add(step);

		step = new AnimatedElement(ratio, 0.4f, (TextureRegion[]) atlas.findRegions("plantStep1").toArray(TextureRegion.class), false);
		step.setX(x - step.getWidth() / 2f);
		step.setY(y);
		steps.add(step);

		step = new AnimatedElement(ratio, 0.4f, (TextureRegion[]) atlas.findRegions("plantStep2").toArray(TextureRegion.class), false);
		step.setX(x - step.getWidth() / 2f);
		step.setY(y);
		steps.add(step);


		currentStep = 0;
		this.step = steps.get(0);
	}


	public float getY() {
		return y;
	}

	public float getX() {
		return x;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public void nextStep() {
		if (currentStep >= steps.size - 1)
			return;
		step = steps.get(++currentStep);
	}

	public boolean isAnimationFinished() {
		return step.isAnimationFinished();
	}

	public void act(float delta) {
		step.act(delta);
	}

	public void draw (Batch batch) {
		step.draw(batch);
	}
}

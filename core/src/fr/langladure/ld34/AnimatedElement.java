package fr.langladure.ld34;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Radnap
 */
public class AnimatedElement extends GraphicItem {

	private String name;

	private boolean loop;
	private Animation animation;
	private float stateTime;


	public AnimatedElement(String name, float ratio, float duration, TextureRegion[] frames, boolean loop) {
		this(ratio, duration, frames, loop);
		this.name = name;
	}

	public AnimatedElement(float ratio, float duration, TextureRegion[] frames, boolean loop) {
		super(ratio);
		this.loop = loop;
		this.animation = new Animation(duration, frames);
		stateTime = 0f;
		setTextureRegion(frames[0]);
	}


	public String getName() {
		return name;
	}

	public void restart() {
		stateTime = 0;
		act(0f);
	}

	public boolean isAnimationFinished() {
		return animation.isAnimationFinished(stateTime);
	}

	public void act(float delta) {
		stateTime += delta;
		setTextureRegion(animation.getKeyFrame(stateTime, loop));
	}
}

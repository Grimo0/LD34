package fr.langladure.ld34;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * @author Radnap
 */
public class Calamity extends InputAdapter {

	static int LEFT = 0;
	static int RIGHT = 1;

	private String name;

	private AnimatedElement leftArrow;
	private AnimatedElement rightArrow;

	private AnimatedElement back;

	private Array<Combination> combinations;
	private int currentCombination;
	private int taped;

	private class Combination {
		int arrow;
		int number;

		public Combination(int a, int n) {
			arrow = a;
			number = n;
		}
	}


	public Calamity(String name, TextureAtlas atlas, float ratio, float screenWidth, float screenHeight) {
		this.name = name;

		JsonValue jsonValue = new JsonReader().parse(GameBase.resolver.resolve("game/" + name + ".json"));

		float backDuration = jsonValue.getFloat("back_duration");

		combinations = new Array<>();
		jsonValue = jsonValue.get("combinations");
		jsonValue = jsonValue.child();
		while (jsonValue != null) {
			combinations.add(new Combination(jsonValue.get("a").asInt(), jsonValue.get("n").asInt()));
			jsonValue = jsonValue.next();
		}

		currentCombination = 0;
		taped = 0;


		leftArrow = new AnimatedElement(ratio, 0.03f, (TextureRegion[]) atlas.findRegions("leftArrow").toArray(TextureRegion.class), false);
		leftArrow.setPosition(0.25f * screenWidth - leftArrow.getWidth() / 2f, 0.1f * screenHeight);
		rightArrow = new AnimatedElement(ratio, 0.03f, (TextureRegion[]) atlas.findRegions("rightArrow").toArray(TextureRegion.class), false);
		rightArrow.setPosition(0.75f * screenWidth - rightArrow.getWidth() / 2f, 0.1f * screenHeight);

		back = new AnimatedElement(ratio, backDuration, (TextureRegion[]) atlas.findRegions(name + "_back").toArray(TextureRegion.class), true);
	}


	public boolean isFinished() {
		return currentCombination == -1;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (currentCombination == -1)
			return super.keyDown(keycode);

		Combination combination = combinations.get(currentCombination);
		if (combination.arrow == LEFT && keycode == Input.Keys.LEFT) {
			if (taped >= combination.number - 1) {
				if (currentCombination >= combinations.size - 1)
					currentCombination = -1;
				else
					currentCombination++;
				taped = 0;
				leftArrow.setScale(1f);
			} else {
				leftArrow.restart();
				leftArrow.setScale(leftArrow.getScale() + 0.1f);
				taped++;
			}
			return true;
		} else if (combination.arrow == RIGHT && keycode == Input.Keys.RIGHT) {
			if (taped >= combination.number - 1) {
				if (currentCombination >= combinations.size - 1)
					currentCombination = -1;
				else
					currentCombination++;
				rightArrow.setScale(1f);
				taped = 0;
			} else {
				rightArrow.restart();
				rightArrow.setScale(rightArrow.getScale() + 0.1f);
				taped++;
			}
			return true;
		}

		return super.keyDown(keycode);
	}

	public void act(float delta) {
		back.act(delta);

		rightArrow.act(delta);
		leftArrow.act(delta);
	}

	public void draw(Batch batch) {
		back.draw(batch);

		if (currentCombination == -1)
			return;

		if (combinations.get(currentCombination).arrow == LEFT)
			leftArrow.draw(batch);
		else
			rightArrow.draw(batch);
	}
}

package fr.langladure.ld34;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import fr.langladure.ld34.screens.GameScreen;

import java.util.Random;

/**
 * @author Radnap
 */
public class Calamity extends InputAdapter {

	static int LEFT = 0;
	static int RIGHT = 1;

	private String name;
	private boolean started;

	private Plant plant;

	private float ratio;

	private AnimatedElement leftArrow;
	private AnimatedElement rightArrow;

	private AnimatedElement animationBefore;
	private AnimatedElement animationAfter;
	private Array<AnimatedElement> backElements;
	private Array<AnimatedElement> frontElements;

	private Array<Combination> combinations;
	private Random random;
	private boolean pressedFine;
	private boolean pressedWrong;
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


	public Calamity(String name, TextureAtlas atlas, float ratio, float screenWidth, float screenHeight, Plant plant) {
		this.name = name;
		started = false;
		this.plant = plant;
		this.ratio = ratio;

		JsonValue jsonValue = new JsonReader().parse(GameBase.resolver.resolve("game/" + name + ".json"));

		JsonValue element = jsonValue.get("animation_before");
		if (element != null) {
			animationBefore = new AnimatedElement(ratio, element.getFloat("duration"),
					(TextureRegion[]) atlas.findRegions(element.getString("name")).toArray(TextureRegion.class), false);
			animationBefore.setPosition(element.getInt("x") * ratio, element.getInt("y") * ratio);
		}

		element = jsonValue.get("animation_After");
		if (element != null) {
			animationAfter = new AnimatedElement(ratio, element.getFloat("duration"),
					(TextureRegion[]) atlas.findRegions(element.getString("name")).toArray(TextureRegion.class), false);
			animationAfter.setPosition(element.getInt("x") * ratio, element.getInt("y") * ratio);
		}

		AnimatedElement animatedElement;
		backElements = new Array<>();
		element = jsonValue.get("back_elements");
		element = element == null ? null : element.child();
		while (element != null) {
			animatedElement = new AnimatedElement(element.getString("name"), ratio, element.getFloat("duration"),
					(TextureRegion[]) atlas.findRegions(element.getString("name")).toArray(TextureRegion.class), element.getBoolean("loop", false));
			animatedElement.setPosition(element.getInt("x") * ratio, element.getInt("y") * ratio);
			backElements.add(animatedElement);
			element = element.next();
		}

		int leftElements = 0;
		int rightElements = 0;
		frontElements = new Array<>();
		element = jsonValue.get("front_elements");
		element = element == null ? null : element.child();
		while (element != null) {
			animatedElement = new AnimatedElement(element.getString("name"), ratio, element.getFloat("duration"),
					(TextureRegion[]) atlas.findRegions(element.getString("name")).toArray(TextureRegion.class), element.getBoolean("loop", false));
			animatedElement.setPosition(element.getInt("x") * ratio, element.getInt("y") * ratio);
			frontElements.add(animatedElement);
			if (plant.getX() > animatedElement.getX() + animatedElement.getWidth() / 2f)
				rightElements++;
			else
				leftElements++;
			element = element.next();
		}

		random = new Random();
		combinations = new Array<>();
		/*jsonValue = jsonValue.get("combinations").child();
		while (jsonValue != null) {
			combinations.add(new Combination(jsonValue.getInt("a"), jsonValue.getInt("n")));
			jsonValue = jsonValue.next();
		}*/
		for (int i = 0; i < 30; i++) {
			if (random.nextBoolean())
				combinations.add(new Combination(1, 1));
			else
				combinations.add(new Combination(0, 1));
		}

		pressedFine = false;
		pressedWrong = false;
		currentCombination = 0;
		taped = 0;


		leftArrow = new AnimatedElement(ratio, 0.03f, (TextureRegion[]) atlas.findRegions("leftArrow").toArray(TextureRegion.class), false);
		leftArrow.setPosition(0.35f * screenWidth - leftArrow.getWidth() / 2f, 0.05f * screenHeight);
		rightArrow = new AnimatedElement(ratio, 0.03f, (TextureRegion[]) atlas.findRegions("rightArrow").toArray(TextureRegion.class), false);
		rightArrow.setPosition(0.65f * screenWidth - rightArrow.getWidth() / 2f, 0.05f * screenHeight);
	}


	public boolean isFinished() {
		return currentCombination == -1 && (animationAfter == null || animationAfter.isAnimationFinished());
	}

	public void start() {
		started = true;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (!started || currentCombination == -1 || (animationBefore != null && !animationBefore.isAnimationFinished()))
			return super.keyDown(keycode);

		Combination combination = combinations.get(currentCombination);
		if (keycode == Input.Keys.LEFT) {
			if (combination.arrow != LEFT) {
				pressedWrong = true;
				return true;
			}

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
			pressedFine = true;
			return true;
		} else if (keycode == Input.Keys.RIGHT) {
			if (combination.arrow != RIGHT) {
				pressedWrong = true;
				return true;
			}

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
			pressedFine = true;
			return true;
		}

		return super.keyDown(keycode);
	}

	public void act(float delta) {
		for (AnimatedElement backElement : backElements) {
			backElement.act(delta);
		}

		for (AnimatedElement frontElement : frontElements) {
			if (started && currentCombination != -1 && "fire".equals(frontElement.getName())) {
				if (pressedFine) {
					frontElement.setX(frontElement.getX() - 200 * Math.signum(plant.getX() - frontElement.getX() - frontElement.getWidth() / 2f) * delta * ratio);
//					frontElement.setY(frontElement.getY() - 5 * Math.signum(plant.getY() - frontElement.getY()) * delta * ratio);
				} else if (pressedWrong) {
					frontElement.setX(frontElement.getX() + 170 * Math.signum(plant.getX() - frontElement.getX() - frontElement.getWidth() / 2f) * delta * ratio);
					frontElement.setY(frontElement.getY() + 50 * Math.signum(plant.getY() - frontElement.getY()) * delta * ratio);
				} else {
					if (random.nextBoolean())
						frontElement.setX(frontElement.getX() + 12 * Math.signum(plant.getX() - frontElement.getX() - frontElement.getWidth() / 2f) * delta * ratio);
					else
						frontElement.setY(frontElement.getY() + 3 * Math.signum(plant.getY() - frontElement.getY()) * delta * ratio);
				}

				if (Math.abs(plant.getX() - frontElement.getX() - frontElement.getWidth()) / 2f < 19 * ratio) {
					currentCombination = -1;
					GameScreen.gameOver = true;
				}
			}
			frontElement.act(delta);
		}

		if (animationBefore != null)
			animationBefore.act(delta);

		if (started) {
			rightArrow.act(delta);
			leftArrow.act(delta);
		}

		if (currentCombination == -1 && animationAfter != null)
			animationAfter.act(delta);

		pressedFine = false;
		pressedWrong = false;
	}

	public void drawBack(Batch batch) {
		for (AnimatedElement backElement : backElements) {
			backElement.draw(batch);
		}

		if (animationBefore != null)
			animationBefore.draw(batch);
		if (currentCombination == -1 && animationAfter != null)
			animationAfter.draw(batch);
	}

	public void drawFront(Batch batch) {
		for (AnimatedElement frontElement : frontElements) {
			frontElement.draw(batch);
		}

		if (currentCombination == -1 || !started || (animationBefore != null && animationBefore.isAnimationFinished()))
			return;

		if (combinations.get(currentCombination).arrow == LEFT)
			leftArrow.draw(batch);
		else
			rightArrow.draw(batch);
	}
}

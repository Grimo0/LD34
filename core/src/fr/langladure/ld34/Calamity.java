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

import java.util.HashMap;
import java.util.Map;
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
	private Map<AnimatedElement, AnimatedElement> dieElements;

	private Array<Combination> combinations;
	private Random random;
	private boolean pressedFine;
	private boolean pressedWrong;
	private int lastPressed;
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

		JsonValue jsonValue = new JsonReader().parse(TheBulb.resolver.resolve("game/" + name + ".json"));

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

		AnimatedElement dieElement;
		frontElements = new Array<>();
		element = jsonValue.get("front_elements");
		element = element == null ? null : element.child();
		while (element != null) {
			animatedElement = new AnimatedElement(element.getString("name"), ratio, element.getFloat("duration"),
					(TextureRegion[]) atlas.findRegions(element.getString("name")).toArray(TextureRegion.class), element.getBoolean("loop", false));
			animatedElement.setPosition(element.getInt("x") * ratio, element.getInt("y") * ratio);
			frontElements.add(animatedElement);
			element = element.next();
		}


		random = new Random();
		dieElements = new HashMap<>();
		int leftElements = 0;
		int rightElements = 0;
		float lastY = 120;
		for (int i = 0; i < 30; i++) {
			animatedElement = new AnimatedElement(name, ratio, 0.06f, (TextureRegion[]) atlas.findRegions(name).toArray(TextureRegion.class), true);
			frontElements.add(animatedElement);
			dieElement = new AnimatedElement(name+"_die", ratio, 0.06f, (TextureRegion[]) atlas.findRegions(name+"_die").toArray(TextureRegion.class), false);
			dieElements.put(animatedElement, dieElement);
			if (random.nextBoolean()) {
				animatedElement.setPosition((float) (plant.getX() + 200 + Math.random() * 150 * ratio), Math.max(lastY - (int)(Math.random() * 2) * 6f * ratio, 20f));
				rightElements++;
			} else {
				animatedElement.setPosition((float) (plant.getX() - 200 - Math.random() * 150 * ratio), Math.max(lastY - (int)(Math.random() * 2) * 6f * ratio, 20f));
				leftElements++;
			}
			lastY = animatedElement.getY();
		}

		combinations = new Array<>();
		jsonValue = jsonValue.get("combinations");
		jsonValue = jsonValue == null ? null : jsonValue.child();
		while (jsonValue != null) {
			combinations.add(new Combination(jsonValue.getInt("a"), jsonValue.getInt("n")));
			jsonValue = jsonValue.next();
		}
		if (combinations.size == 0) {
			while (leftElements > 0 || rightElements > 0) {
				if ((random.nextBoolean() || leftElements == 0) && rightElements > 0) {
					combinations.add(new Combination(RIGHT, 1));
					rightElements--;
				} else if (leftElements > 0) {
					combinations.add(new Combination(LEFT, 1));
					leftElements--;
				}
			}
		}

		pressedFine = false;
		pressedWrong = false;
		lastPressed = -1;
		currentCombination = 0;
		taped = 0;


		leftArrow = new AnimatedElement(ratio, 0.03f, (TextureRegion[]) atlas.findRegions("leftArrow").toArray(TextureRegion.class), false);
		leftArrow.setPosition(0.35f * screenWidth - leftArrow.getWidth() / 2f, 0.05f * screenHeight);
		rightArrow = new AnimatedElement(ratio, 0.03f, (TextureRegion[]) atlas.findRegions("rightArrow").toArray(TextureRegion.class), false);
		rightArrow.setPosition(0.65f * screenWidth - rightArrow.getWidth() / 2f, 0.05f * screenHeight);
	}


	public boolean isFinished() {
		for (Map.Entry<AnimatedElement, AnimatedElement> dieElement : dieElements.entrySet()) {
			if (!dieElement.getValue().isAnimationFinished())
				return false;
		}

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
			lastPressed = LEFT;
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
			lastPressed = RIGHT;
			pressedFine = true;
			return true;
		}

		return super.keyDown(keycode);
	}

	public void act(float delta) {
		for (AnimatedElement backElement : backElements) {
			backElement.act(delta);
		}

		int closerI = -1;
		float closerDistance = Float.MAX_VALUE;
		for (int i = 0; i < frontElements.size; i++) {
			AnimatedElement frontElement = frontElements.get(i);
			if (started && frontElement.getName() != null && name.equals(frontElement.getName())) {
				float distance = plant.getX() - frontElement.getX() - frontElement.getWidth() / 2f;
				if (pressedFine && ((distance > 0 && lastPressed == LEFT) || (distance < 0 && lastPressed == RIGHT)) && Math.abs(distance) < closerDistance) {
					closerDistance = Math.abs(distance);
					closerI = i;
				} else if (pressedWrong && currentCombination != -1) {
					frontElement.setX(frontElement.getX() + 600 * Math.signum(distance) * delta * ratio);
					frontElement.setY(frontElement.getY() + 150 * Math.signum(plant.getY() - frontElement.getY()) * delta * ratio);
				} else if (currentCombination != -1) {
					if (random.nextBoolean())
						frontElement.setX(frontElement.getX() + 25 * Math.signum(distance) * delta * ratio);
					else
						frontElement.setY(frontElement.getY() + 5 * Math.signum(plant.getY() - frontElement.getY()) * delta * ratio);
				}

				if (Math.abs(distance) < 19 * ratio) {
					currentCombination = -1;
					GameScreen.gameOver = true;
				}
			}
			frontElement.act(delta);
		}
		if (closerI != -1) {
			AnimatedElement element = frontElements.get(closerI);
			AnimatedElement dieElement = dieElements.get(element);
			dieElement.setPosition(element.getX(), element.getY());
			frontElements.set(closerI, dieElement);
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

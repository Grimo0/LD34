package fr.langladure.ld34.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import fr.langladure.ld34.AnimatedElement;
import fr.langladure.ld34.Calamity;
import fr.langladure.ld34.GameBase;
import fr.langladure.ld34.Plant;


/**
 * @author Radnap
 */
public class GameScreen extends AbstractScreen {

	private Sprite background;
	private AnimatedElement clouds;
	private Sprite frame;

	private Plant plant;

	private Array<Calamity> calamities;
	private Calamity calamity;
	private int calamityN;


	public GameScreen(GameBase game) {
		super(game);
	}


	@Override
	public void loadAssets() {
		super.loadAssets();
		game.assetsFinder.load("game");
	}

	@Override
	public void create() {
		TextureAtlas atlas = game.assetManager.get("game/gamePack.atlas", TextureAtlas.class);


		background = atlas.createSprite("bg");

		float ratio = SCREEN_WIDTH / background.getWidth();
		background.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		clouds = new AnimatedElement(ratio, 1f, (TextureRegion[]) atlas.findRegions("clouds").toArray(TextureRegion.class), true);

		frame = atlas.createSprite("bg_frame");
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		plant = new Plant(atlas, ratio, SCREEN_WIDTH / 2f, 0.15f * SCREEN_HEIGHT);

		calamities = new Array<>();
		calamity = new Calamity("rain", atlas, ratio, SCREEN_WIDTH, SCREEN_HEIGHT);
		calamities.add(calamity);
		calamities.add(new Calamity("fire", atlas, ratio, SCREEN_WIDTH, SCREEN_HEIGHT));
		calamityN = 0;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		Gdx.input.setInputProcessor(calamity);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		if (calamityN == plant.getCurrentStep() && calamity.isFinished()) {
			plant.nextStep();
		} else if (calamityN != -1 && calamity.isFinished() && plant.isAnimationFinished()) {
			if (calamityN < calamities.size - 1) {
				calamity = calamities.get(++calamityN);
				Gdx.input.setInputProcessor(calamity);
			} else {
				calamityN = -1;
				game.loadingScreen.setFadeWhenLoaded(true);
				game.loadingScreen.setNextScreen(game.mainMenuScreen);
				game.setScreen(game.loadingScreen);
			}
		}

		clouds.act(delta);
		calamity.act(delta);
		plant.act(delta);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		background.draw(game.batch);
		clouds.draw(game.batch);
		calamity.draw(game.batch);
		plant.draw(game.batch);
		frame.draw(game.batch);
		game.batch.end();
	}

	@Override
	public void dispose() {
		super.dispose();

		game.assetsFinder.unload("game");
	}
}

package fr.langladure.ld34.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import fr.langladure.ld34.Calamity;
import fr.langladure.ld34.GameBase;
import fr.langladure.ld34.Plant;


/**
 * @author Radnap
 */
public class GameScreen extends AbstractScreen {

	private PooledEngine engine;

	private Sprite background;
	private Sprite frame;
	private Plant plant;
	private Calamity calamity;


	public GameScreen(GameBase game) {
		super(game);
	}


	@Override
	public void loadAssets() {
		super.loadAssets();
		game.assetsFinder.load("game");

		engine = new PooledEngine();
	}

	@Override
	public void create() {
		TextureAtlas atlas = game.assetManager.get("game/gamePack.atlas", TextureAtlas.class);


		background = atlas.createSprite("bg");

		float ratio = SCREEN_WIDTH / background.getWidth();
		background.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		frame = atlas.createSprite("bg_frame");
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		plant = new Plant(atlas, ratio, SCREEN_WIDTH / 2f, 0.15f * SCREEN_HEIGHT);

		calamity = new Calamity("rain", atlas, ratio, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(calamity);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		engine.update(delta);

		if (calamity.isFinished()) {
			plant.nextStep();
		}

		plant.act(delta);
		calamity.act(delta);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		background.draw(game.batch);
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

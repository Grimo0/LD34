package fr.langladure.ld34.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import fr.langladure.ld34.GameBase;


/**
 * @author Radnap
 */
public class GameScreen extends AbstractScreen {

	private PooledEngine engine;

	private Sprite background;
	private Sprite plant;


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


//		float worldWidth = 1;
//		viewport.setWorldSize(worldWidth, worldWidth * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
//		updateViewPort(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		///// Background /////
		background = atlas.createSprite("bg");

		float ratio = SCREEN_WIDTH / background.getWidth();
		background.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		plant = atlas.createSprite("plant1D");
		plant.setSize(plant.getWidth() * ratio, plant.getHeight() * ratio);
		plant.setPosition(4f * SCREEN_WIDTH / 5f - plant.getWidth() / 2f, (SCREEN_HEIGHT - plant.getHeight()) / 2f);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();
		InputMultiplexer multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		engine.update(delta);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		background.draw(game.batch);
		plant.draw(game.batch);
		game.batch.end();

	}

	@Override
	public void dispose() {
		super.dispose();

		game.assetsFinder.unload("game");
	}
}

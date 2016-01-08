package fr.langladure.ld34.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import fr.langladure.ld34.Calamity;
import fr.langladure.ld34.TheBulb;
import fr.langladure.ld34.Plant;


/**
 * @author Radnap
 */
public class GameScreen extends AbstractScreen {

	public static boolean gameOver = false;

	private final float FADE_TIME = 1f;

	private Sprite background;
	private Sprite frame;
	private Sprite fade;
	private Label gameOverLabel;
	private float fadeTimer;

	private Plant plant;

	private Array<Calamity> calamities;
	private Calamity calamity;
	private int calamityNext;


	public GameScreen(TheBulb game) {
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

		gameOver = false;

		background = atlas.createSprite("bg");

		float ratio = SCREEN_WIDTH / background.getWidth();
		background.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		frame = atlas.createSprite("bg_frame");
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		fade = atlas.createSprite("fade");
		fade.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		fade.setAlpha(0f);
		fadeTimer = -1f;

		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
//		fontParams.minFilter = Texture.TextureFilter.Linear;
//		fontParams.magFilter = Texture.TextureFilter.Linear;
		fontParams.shadowColor = new Color(140f/255f, 41f/255f, 32f/255f, 1f);
		fontParams.shadowOffsetX = (int) (2*ratio);
		fontParams.shadowOffsetY = (int) (2*ratio);

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		fontParams.size = (int) (0.1f * SCREEN_HEIGHT);
		labelStyle.font = TheBulb.titleGen.generateFont(fontParams);
		labelStyle.fontColor = new Color(163f/255f, 48f/255f, 38f/255f, 1f);

		gameOverLabel = new Label("Game Over", labelStyle);
		gameOverLabel.setPosition((SCREEN_WIDTH - gameOverLabel.getWidth())/2, (SCREEN_HEIGHT - gameOverLabel.getHeight())/2);
		gameOverLabel.setColor(1f,1f,1f,0f);


		plant = new Plant(atlas, ratio, SCREEN_WIDTH / 2f, 0.15f * SCREEN_HEIGHT);

		calamities = new Array<>();
		calamity = new Calamity("fire", atlas, ratio, SCREEN_WIDTH, SCREEN_HEIGHT, plant);
		calamities.add(calamity);
		calamities.add(new Calamity("water", atlas, ratio, SCREEN_WIDTH, SCREEN_HEIGHT, plant));
		calamityNext = 1;
	}


	public void gameOver() {
		if (calamityNext != -1) {
			calamityNext = -1;
			fadeTimer = 0f;
		}
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

		if (fadeTimer >= 2f*FADE_TIME) {
			game.loadingScreen.setFadeWhenLoaded(true);
			game.loadingScreen.setNextScreen(game.mainMenuScreen);
			game.setScreen(game.loadingScreen);
			return;
		} else if (plant.isAnimationFinished() && fadeTimer >= 0f) {
			fadeTimer += delta;
			gameOverLabel.setColor(1f,1f,1f, Math.min(fadeTimer / FADE_TIME, 1f));
			fade.setAlpha(Math.min(fadeTimer / FADE_TIME, 1f));
		}

		if (calamityNext != -1) {
			if (plant.isAnimationFinished() && calamity.isFinished()) {
				plant.nextStep();
				if (calamityNext < calamities.size) {
					calamity = calamities.get(calamityNext++);
					Gdx.input.setInputProcessor(calamity);
				} else {
					calamityNext = -1;
					fadeTimer = 0f;
				}
			} else if (plant.isAnimationFinished()) {
				calamity.start();
			}
		}

		if (calamity != null)
			calamity.act(delta);
		plant.act(delta);

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		background.draw(game.batch);

		if (calamity != null)
			calamity.drawBack(game.batch);
		plant.draw(game.batch);
		if (calamity != null)
			calamity.drawFront(game.batch);

		frame.draw(game.batch);
		fade.draw(game.batch);
		gameOverLabel.draw(game.batch, 1f);
		game.batch.end();

		if (gameOver) {
			gameOver();
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		game.assetsFinder.unload("game");
	}
}

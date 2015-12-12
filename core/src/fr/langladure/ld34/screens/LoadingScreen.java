package fr.langladure.ld34.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import fr.langladure.ld34.GameBase;

/**
 * @author Radnap
 */
public class LoadingScreen extends AbstractScreen {

	private Stage stage;

	private Image logo;
	private Image loadingBar;
	private Label pass;

	private AbstractScreen nextScreen;
	private boolean fadeWhenLoaded;

	private float percent;


	/**
	 * Will load all the assets it needs during instantiation.
	 */
	public LoadingScreen(GameBase game) {
		super(game);
		fadeWhenLoaded = true;

		stage = new Stage(viewport, game.batch);

		// Tell the assetManager to load assets for the loading screen
		game.assetsFinder.load("loading");
		// Wait until they are finished loading
		game.assetManager.finishLoading();

		// Get our textureatlas from the assetManager
		TextureAtlas atlas = game.assetManager.get("loading/loadingPack.atlas", TextureAtlas.class);


		/// Create the screen background and adapt the world to it
		Image screenBg = new Image(atlas.findRegion("loadingBg"));

		float ratio = SCREEN_WIDTH / screenBg.getWidth();
		screenBg.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		stage.addActor(screenBg);


		// Grab the regions from the atlas and create some images
		logo = new Image(atlas.findRegion("logo"));
		stage.addActor(logo);
		logo.setSize(logo.getWidth() * ratio, logo.getHeight() * ratio);
		logo.setX((stage.getWidth() - logo.getWidth()) / 2f);
		logo.setY((stage.getHeight() - logo.getHeight()) * 3f / 4f);

		// Place the loading bar
		loadingBar = new Image(atlas.findRegion("loadingBar"));
		stage.addActor(loadingBar);
		loadingBar.setSize(SCREEN_WIDTH, SCREEN_WIDTH * loadingBar.getHeight() / loadingBar.getWidth());
		loadingBar.setX(-loadingBar.getWidth());
		loadingBar.setY(logo.getY() - 2.5f * loadingBar.getHeight());

		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParams.minFilter = Texture.TextureFilter.Linear;
		fontParams.magFilter = Texture.TextureFilter.Linear;
		fontParams.size = (int) (0.5f * loadingBar.getHeight());
		fontParams.shadowColor = Color.BLACK;
		fontParams.shadowOffsetX = 2;
		fontParams.shadowOffsetY = 2;
		BitmapFont font = GameBase.title2Gen.generateFont(fontParams);

		pass = new Label("Cliquez pour continuer", new Label.LabelStyle(font, new Color(0.9f, 0.9f, 0.85f, 1f)));
		stage.addActor(pass);
		pass.setX((stage.getWidth() - pass.getWidth()) / 2f);
		pass.setY(loadingBar.getY() + 1f * loadingBar.getHeight());
	}

	public void setNextScreen(AbstractScreen nextScreen) {
		this.nextScreen = nextScreen;
	}

	public void setFadeWhenLoaded(boolean fadeWhenLoaded) {
		this.fadeWhenLoaded = fadeWhenLoaded;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		loadingBar.setSize(SCREEN_WIDTH, SCREEN_WIDTH * loadingBar.getHeight() / loadingBar.getWidth());
		loadingBar.setPosition(-loadingBar.getWidth() + (viewport.getWorldWidth() - SCREEN_WIDTH) / 2f, loadingBar.getY());
	}

	@Override
	public void show() {
		super.show();
		percent = 0;
		pass.setVisible(false);

		Gdx.input.setInputProcessor(stage);

		// Add everything to be loaded
		nextScreen.loadAssets();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.setDebugAll(GameBase.DEVMODE);

		if (game.assetManager.update()) { // Load some, will return true if done loading;
			if (!fadeWhenLoaded && percent > 0.95f)
				pass.setVisible(true);
			if (fadeWhenLoaded
					|| Gdx.input.isKeyPressed(Input.Keys.SPACE)
					|| Gdx.input.isKeyPressed(Input.Keys.ENTER)
					|| Gdx.input.justTouched()) {
				System.gc();
				nextScreen.create();
				loadingBar.setX(-loadingBar.getWidth());
				game.setScreen(nextScreen);
			}
		}

		// Interpolate the percentage to make it more smooth
		percent = Interpolation.linear.apply(percent, game.assetManager.getProgress(), 0.1f);

		// Update positions (and size) to match the percentage
		loadingBar.setPosition((percent - 1f) * loadingBar.getWidth() + (viewport.getWorldWidth() - SCREEN_WIDTH) / 2f, loadingBar.getY());

		// Show the loading screen
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void dispose() {
		super.dispose();

		// Dispose the loading assets as we no longer need them
		game.assetsFinder.unload("loading");
		if (stage != null) {
			stage.dispose();
			stage = null;
		}
	}
}

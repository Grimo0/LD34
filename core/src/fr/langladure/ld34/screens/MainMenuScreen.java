package fr.langladure.ld34.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import fr.langladure.ld34.GameBase;

/**
 * @author Radnap
 */
public class MainMenuScreen extends AbstractScreen {

	private Stage stage;
	private Label title;
	private Image credits;
	private Table table;


	public MainMenuScreen(GameBase game) {
		super(game);
	}

	@Override
	public void loadAssets() {
		if (stage != null)
			return;

		super.loadAssets();
		game.assetsFinder.load("mainMenu");

		game.optionScreen.loadAssets();
	}

	@Override
	public void create() {
		if (stage != null)
			return;

		super.create();

		stage = new Stage(viewport, game.batch);

		TextureAtlas atlas = game.assetManager.get("mainMenu/mainMenuPack.atlas", TextureAtlas.class);


		// Create the screen background and adapt the world to it
		Image screenBg = new Image(atlas.findRegion("mainMenuBg"));

		float worldHeight = screenBg.getHeight();
		float worldWidth = screenBg.getWidth();

		viewport.setWorldSize(worldWidth, worldHeight);
		updateViewPort(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Make the background shorter dimension fill the screen while keeping its ratio
		if (worldWidth / worldHeight < screenBg.getWidth() / screenBg.getHeight())
			screenBg.setSize(worldHeight * screenBg.getWidth() / screenBg.getHeight(), worldHeight);
		else
			screenBg.setSize(worldWidth, worldWidth * screenBg.getHeight() / screenBg.getWidth());

		// Center the background along the overflowing edge
		screenBg.setX((camera.viewportWidth - screenBg.getWidth()) / 2f);
		screenBg.setY((camera.viewportHeight - screenBg.getHeight()) / 2f);

		stage.addActor(screenBg);


		// Skin and fonts
		Skin skin = new Skin();

		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParams.minFilter = Texture.TextureFilter.Linear;
		fontParams.magFilter = Texture.TextureFilter.Linear;
		fontParams.shadowColor = new Color(0f, 0f, 0f, 0.3f);
		fontParams.shadowOffsetX = 3;
		fontParams.shadowOffsetY = 3;

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		fontParams.size = (int) (0.15f * stage.getHeight());
		labelStyle.font = GameBase.titleGen.generateFont(fontParams);
		labelStyle.fontColor = Color.BLACK;
		skin.add("title", labelStyle);

		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		fontParams.size = (int) (0.095f * stage.getHeight());
		textButtonStyle.font = GameBase.titleGen.generateFont(fontParams);
		textButtonStyle.overFontColor = Color.BLACK;
		textButtonStyle.fontColor = new Color(0.14f, 0.14f, 0.18f, 1f);
		skin.add("default", textButtonStyle);


		title = new Label(GameBase.NAME, skin, "title");
		stage.addActor(title);
		title.setX((stage.getWidth() - title.getWidth()) / 2f);
		title.setY(0.87f * stage.getHeight() - 0.5f * title.getHeight());


		credits = new Image(atlas.findRegion("credits"));
		stage.addActor(credits);
		float newHeight = 0.67f * stage.getHeight();
		credits.setSize(newHeight * credits.getWidth() / credits.getHeight(), newHeight);
		credits.setX(0.85f * stage.getWidth() - credits.getWidth());
		credits.setY((0.8f * stage.getHeight() - credits.getHeight()) / 2f);


		table = new Table(skin);
		stage.addActor(table);
		table.left().bottom();
		// Setting the default value of the cells
		table.defaults().left().spaceTop(Value.percentHeight(0.3f));
		table.setX(0.15f * stage.getWidth());
		table.setY(0.2f * stage.getHeight());

		TextButton newGame = new TextButton("Nouvelle partie", skin);
		newGame.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.loadingScreen.setFadeWhenLoaded(false);
				game.loadingScreen.setNextScreen(game.gameScreen);
				game.setScreen(game.loadingScreen);
			}
		});
		table.add(newGame).row();

		TextButton options = new TextButton("Options", skin);
		options.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.optionScreen);
			}
		});
		table.add(options).row();
		game.optionScreen.create();

		TextButton exit = new TextButton("Quitter", skin);
		exit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		table.add(exit).row();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void show() {
		super.show();

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.setDebugAll(GameBase.DEVMODE);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();

		game.assetsFinder.unload("mainMenu");
		if (stage != null) {
			stage.dispose();
			stage = null;
		}
	}
}

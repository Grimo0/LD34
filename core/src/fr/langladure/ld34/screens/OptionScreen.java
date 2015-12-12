package fr.langladure.ld34.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
public class OptionScreen extends AbstractScreen {

	private Stage stage;
	private Cell optionsCell;
	private Table optionsTable;
	private Table graphicTable;


	public OptionScreen(GameBase game) {
		super(game);
	}

	@Override
	public void loadAssets() {
		if (stage != null)
			return;

		super.loadAssets();
		game.assetsFinder.load("mainMenu");
	}

	@Override
	public void create() {
		if (stage != null)
			return;

		super.create();

		stage = new Stage(viewport, game.batch);

		TextureAtlas atlas = game.assetManager.get("mainMenu/mainMenuPack.atlas", TextureAtlas.class);

		/// Create the screen background and adapt the world to it
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

		/// Create skin and fonts
		Skin skin = new Skin();

		FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParams.minFilter = Texture.TextureFilter.Linear;
		fontParams.magFilter = Texture.TextureFilter.Linear;
		fontParams.size = (int) (0.1f * stage.getHeight());
		fontParams.shadowColor = new Color(0f, 0f, 0f, 0.3f);
		fontParams.shadowOffsetX = 3;
		fontParams.shadowOffsetY = 3;
		BitmapFont font = GameBase.titleGen.generateFont(fontParams);

		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.font = font;
		textButtonStyle.overFontColor = Color.BLACK;
		textButtonStyle.fontColor = new Color(0.14f, 0.14f, 0.18f, 1f);
		skin.add("default", textButtonStyle);


		Table table = new Table(skin);
		stage.addActor(table);
		table.setFillParent(true);
		table.left().bottom();
		// Setting the default value of the cells
		table.defaults().left().spaceTop(Value.percentHeight(0.5f));
		table.setX(0.15f * stage.getWidth());
		table.setY(0.2f * stage.getHeight());


		optionsTable = new Table(skin);
		optionsTable.defaults().left().spaceBottom(Value.percentHeight(0.15f));

		TextButton soundButton = new TextButton("Son : oui", skin);
		soundButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				TextButton soundButton = (TextButton) actor;
				if (soundButton.getLabel().toString().endsWith("oui"))
					soundButton.setText("Son : non");
				else
					soundButton.setText("Son : oui");
			}
		});
		optionsTable.add(soundButton).row();

		TextButton graphicButton = new TextButton("Résolution", skin);
		graphicButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				optionsCell.setActor(graphicTable);
			}
		});
		optionsTable.add(graphicButton).row();

		optionsCell = table.add(optionsTable);
		optionsCell.row();


		graphicTable = new Table(skin);
		graphicTable.defaults().left().spaceBottom(Value.percentHeight(0.15f));

		TextButton lowResolution = new TextButton(" 800x600", skin);
		lowResolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.graphics.setDisplayMode(800, 600, false);
			}
		});
		graphicTable.add(lowResolution).row();

		TextButton highResolution = new TextButton(" 1280x768", skin);
		highResolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.graphics.setDisplayMode(1280, 768, false);
			}
		});
		graphicTable.add(highResolution).row();


		TextButton backButton = new TextButton("Retour", skin);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (optionsCell.getActor() == graphicTable)
					optionsCell.setActor(optionsTable);
				else {
					optionsCell.setActor(optionsTable);
					game.setScreen(game.mainMenuScreen);
				}
			}
		});
		table.add(backButton).row();
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

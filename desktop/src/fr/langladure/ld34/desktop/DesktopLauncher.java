package fr.langladure.ld34.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fr.langladure.ld34.GameBase;

public class DesktopLauncher {
	public static void main(String[] arg) {
		System.setProperty("user.name", "EnglishWords"); // to avoid an LWJGLException

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = GameBase.NAME + " " + GameBase.VERSION;
		cfg.height = 768;
		cfg.width = 1280;
		cfg.resizable = false;

		new LwjglApplication(new GameBase(), cfg);

//        Gdx.input.setCursorCatched(true);
	}
}

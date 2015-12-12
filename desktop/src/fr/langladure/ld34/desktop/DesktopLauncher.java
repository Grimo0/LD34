package fr.langladure.ld34.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fr.langladure.ld34.GameBase;

public class DesktopLauncher {
	public static void main(String[] arg) {
		System.setProperty("user.name", "EnglishWords"); // to avoid an LWJGLException

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = GameBase.NAME + " " + GameBase.VERSION;
		cfg.height = 576;
		cfg.width = 768;
//		cfg.height = 384;
//		cfg.width = 512;
		cfg.resizable = false;

		new LwjglApplication(new GameBase(), cfg);

//        Gdx.input.setCursorCatched(true);
	}
}

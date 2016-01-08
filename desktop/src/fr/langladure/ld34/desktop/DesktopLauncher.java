package fr.langladure.ld34.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fr.langladure.ld34.TheBulb;

public class DesktopLauncher {
	public static void main(String[] arg) {
		System.setProperty("user.name", "EnglishWords"); // to avoid an LWJGLException

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = TheBulb.NAME + " " + TheBulb.VERSION;
		cfg.height = 576;
		cfg.width = 768;
//		cfg.height = 384;
//		cfg.width = 512;
		cfg.resizable = false;

		new LwjglApplication(new TheBulb(), cfg);

//        Gdx.input.setCursorCatched(true);
	}
}

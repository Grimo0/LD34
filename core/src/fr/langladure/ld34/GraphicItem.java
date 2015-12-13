package fr.langladure.ld34;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Radnap
 */
public class GraphicItem {

	private float ratio;
	private float x;
	private float y;
	private float width;
	private float height;
	private float scale;
	private TextureRegion textureRegion;


	public GraphicItem(float ratio) {
		this.ratio = ratio;
		scale = 1f;
		x = 0f;
		y = 0f;
	}


	public float getRatio() {
		return ratio;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setWidth(float width) {
		this.width = width * ratio;
	}

	public void setHeight(float height) {
		this.height = height * ratio;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public TextureRegion getTextureRegion() {
		return textureRegion;
	}

	public void setTextureRegion(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
		setWidth(textureRegion.getRegionWidth());
		setHeight(textureRegion.getRegionHeight());
	}


	public void draw(Batch batch) {
		batch.draw(textureRegion, getX() - getWidth() * (scale - 1f) / 2f, getY() - getHeight() * (scale - 1f) / 2f, getWidth() * scale, getHeight() * scale);
	}
}

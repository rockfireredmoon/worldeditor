package org.icemoon.eartheternal.common;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Viewport implements Serializable {
	private int x;
	private int y;

	private float scale = 1.0f;
	private int selX;
	private int selY;
	int endSelX;
	int endSelY;
	private int width;
	private int height;

	public Viewport() {
	}

	public void clearSelect() {
		selX = 0;
		selY = 0;
		endSelX = 0;
		endSelY = 0;
	}

	public void down() {
		y += 50;
		System.out.println("y now: " + y);
	}

	public void endSelect(int endSelX, int endSelY) {
		if (endSelX > getSelX()) {
			this.endSelX = endSelX;
		} else {
			this.endSelX = this.getSelX();
			this.selX = endSelX;
		}
		if (endSelY > getSelY()) {
			this.endSelY = endSelY;
		} else {
			this.endSelY = this.getSelY();
			this.selY = endSelY;
		}
		System.out.println("End sel: " + endSelX+ ","  +endSelY);
	}

	public int getActualX(MapImage mapImage, int x) {
		float sw = (float) getWidth() / (float) mapImage.getWidth();
		return (int)(x / sw);
	}

	public int getActualY(MapImage mapImage, int y) {
		float sw = (float) getHeight() / (float) mapImage.getHeight();
		return (int)(y / sw);
	}

	public final int getEndSelX() {
		return endSelX;
	}

	public final int getEndSelY() {
		return endSelY;
	}

	public int getHeight() {
		return height;
	}

	public final float getScale() {
		return scale;
	}

	public int getScaledX(MapImage mapImage, int x) {
		float sw = (float) getWidth() / (float) mapImage.getWidth();
		return (int)(x * sw);
	}

	public int getScaledY(MapImage mapImage, int y) {
		float sw = (float) getHeight() / (float) mapImage.getHeight();
		return (int)(y * sw);
	}

	public int getSelX() {
		return selX;
	}

	public int getSelY() {
		return selY;
	}

	public int getWidth() {
		return width;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public boolean isHalfSelected() {
		return getSelX() > 0 && getSelY() > 0 && endSelX == 0 && endSelY == 0;
	}

	public boolean isInSelect(int x, int y) {
		return x > getSelX() && x < endSelX && y > getSelY() && y < endSelY;
	}

	public boolean isSelected() {
		return endSelX > 0 && endSelY > 0;
	}

	public void left() {
		x -= 50;
		System.out.println("x now: " + x);
	}

	public void reset() {
		x = 0;
		y = 0;
		scale = 1;
		clearSelect();
	}

	public void right() {
		x += 50;
		System.out.println("x now: " + x);
	}

	public final void setEndSelX(int endSelX) {
		this.endSelX = endSelX;
	}

	public void setEndSelY(int endSelY) {
		this.endSelY = endSelY;
	}

	public final void setHeight(int height) {
		this.height = height;
	}

	public final void setScale(float scale) {
		this.scale = scale;
	}

	public void setSelX(int selX) {
		this.selX = selX;
	}

	public void setSelY(int selY) {
		this.selY = selY;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final void setY(int y) {
		this.y = y;
	}

	public void startSelect(int selX, int selY) {
		this.selX = selX;
		this.selY = selY;
		System.out.println("Start sel: " + selX+ ","  +selY);
	}

	public int translatePositionX(double position) {
		return (int) ((float) (position - x) * scale);
	}

	public int translatePositionY(double position) {
		return (int) ((float) (position - y) * scale);
	}

	public void up() {
		y -= 50;
		System.out.println("y now: " + y);
	}

	public void zoomIn() {
		scale += 0.25;
	}

	public void zoomOnSelection(MapImage mapImage) {
		System.out.println("Scale: "+ scale + " Width: " + width + " Height: " + height);
		
		// Translate selections to actual positions
		float actualSelX = getSelX() * scale;
		float actualSelY = getSelY() * scale;
		float actualEndX = endSelX * scale;
		float actualEndY = endSelY * scale;
		System.out.println("Actual sel:" + actualSelX + "," + actualSelY + " / " + actualEndX + "," + actualEndY);
		
		x = (int)(actualSelX);
		y = (int)(actualSelY);

		final float sw = endSelX - getSelX();
		float scaleX = mapImage.getWidth() / sw;
		final float sh = endSelY - getSelY();
		float scaleY = mapImage.getHeight() / sh;

		float xscale = Math.min(scaleX, scaleY);
		System.out.println("selx " + getSelX() + " sely:" + getSelY() + " selw: " + sw + " selh: " + sh + " x: " + x + " y:"  +y + " xscale: " + xscale);

		scale = Math.min(scaleX, scaleY);

		clearSelect();

	}

	public void zoomOut() {
		scale -= 0.25;
	}

}
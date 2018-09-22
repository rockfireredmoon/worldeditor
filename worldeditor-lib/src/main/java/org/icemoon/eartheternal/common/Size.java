package org.icemoon.eartheternal.common;

import java.io.Serializable;

public class Size implements Serializable {

	public double x;
	public double y;

	public Size() {
	}

	public Size(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public final void setX(double x) {
		this.x = x;
	}

	public final void setY(double y) {
		this.y = y;
	}

}
package org.icemoon.eartheternal.common;

import static org.icemoon.eartheternal.common.Util.format;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Orient implements Serializable {

	private double v1;
	private double v2;
	private double v3;
	private double v4;

	public Orient() {
	}

	public Orient(double v1, double v2, double v3, double v4) {
		super();
		this.v1 = v1;
		this.v3 = v2;
		this.v3 = v3;
		this.v4 = v4;
	}

	public Orient(String locationString) {
		parseString(locationString);
	}

	public double getV1() {
		return v1;
	}

	public double getV2() {
		return v2;
	}

	public double getV3() {
		return v3;
	}

	public double getV4() {
		return v4;
	}

	public void parseString(String value) {
		StringTokenizer t = new StringTokenizer(value, ",");
		v1 = Double.parseDouble(t.nextToken().trim());
		v3 = Double.parseDouble(t.nextToken().trim());
		v3 = Double.parseDouble(t.nextToken().trim());
		v4 = Double.parseDouble(t.nextToken().trim());
	}

	public void set(double v1, double v2, double v3, double v4) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;		
	}

	public void setV1(double v1) {
		this.v1 = v1;
	}

	public void setV2(double v2) {
		this.v2 = v2;
	}

	public void setV3(double v3) {
		this.v3 = v3;
	}

	public void setV4(double v4) {
		this.v4 = v4;
	}

	@Override
	public String toString() {
		return format(getV1()) + "," + format(getV2()) + "," + format(getV3()) + "," + format(getV4());
	}
}

package org.icemoon.eartheternal.common;

import java.io.Serializable;

public class Sidekick implements Serializable {
	private long id;
	private int summonType;
	private int summonParam;

	public Sidekick(long id, int a, int b) {
		super();
		this.id = id;
		this.summonType = a;
		this.summonParam = b;
	}

	public int getSummonType() {
		return summonType;
	}

	public int getSummonParam() {
		return summonParam;
	}

	public long getId() {
		return id;
	}

	public void setA(int a) {
		this.summonType = a;
	}

	public void setB(int b) {
		this.summonParam = b;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String toString() {
		return id + "," + summonType + "," + summonParam;  
	}
}
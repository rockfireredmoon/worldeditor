package org.icemoon.eartheternal.common;

import java.io.IOException;

@SuppressWarnings("serial")
public abstract class AbstractMultiINIFileEntity<K extends Object, R extends IRoot> extends AbstractINIFileEntity<K, R> {

	private long start;
	private long end;

	public AbstractMultiINIFileEntity(R database) {
		super(database);
	}

	public AbstractMultiINIFileEntity(R database, String file, K id) {
		super(database, file, id);
	}

	public void bounds(long start, long end) {
		if (end < start) {
			throw new IllegalArgumentException("End is before start");
		}
		this.start = start;
		this.end = end;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public long getEndPosition() {
		return end;
	}

	public long getStartPosition() {
		return start;
	}

	@Override
	protected void doLoad() throws IOException {
		// The parent loads the entity
	}

}

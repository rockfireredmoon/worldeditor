package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public abstract class AbstractTableFileEntity<K extends Object, R extends IRoot> extends AbstractEntity<K, R> implements TableFile {


	private long line;

	public AbstractTableFileEntity() {
		super();
	}

	public AbstractTableFileEntity(R database) {
		super(database);
	}

	public AbstractTableFileEntity(R database, String file, K id) {
		super(database, file, id);
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public final long getLine() {
		return line;
	}

	public void line(long line) {
		this.line = line;
	}


}

package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public abstract class AbstractSeparateINIFileEntity<K extends Serializable, R extends IRoot> extends AbstractINIFileEntity<K, R> {

	private long start;
	private long end;

	public AbstractSeparateINIFileEntity(R database) {
		super(database);
	}

	public AbstractSeparateINIFileEntity(R database, String file, K id) {
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
		reset();
		final FileObject f = VFS.getManager().resolveFile(getFile());
		INIReader<AbstractINIFileEntity<K, R>, K, R> reader = createReader(f);
		reader.loadFile(f);
	}

	protected void reset() {
	}

	protected INIReader<AbstractINIFileEntity<K, R>, K, R> createReader(final FileObject f) {
		return new INIReader<AbstractINIFileEntity<K, R>, K, R>() {
			private boolean one;

			@Override
			protected AbstractINIFileEntity<K, R> createItem() {
				if (!one) {
					one = true;
					return AbstractSeparateINIFileEntity.this;
				}
				throw new IllegalStateException(f + " only may contain a single entity.");
			}

			@Override
			protected void doAddItem(FileObject file, AbstractINIFileEntity<K, R> item) {
			}
		};
	}

}

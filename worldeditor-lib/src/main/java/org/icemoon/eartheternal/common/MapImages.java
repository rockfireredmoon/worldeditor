package org.icemoon.eartheternal.common;

import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class MapImages extends AbstractEntities<MapImage, String, String, IStatic> {
	public MapImages(String root) {
		this(null, root);
	}

	public MapImages(IStatic database, String root) {
		super(database, String.class, root);
	}

	@Override
	public void doLoad() throws IOException {
		FileObject[] files = VFS.getManager().resolveFile(getFile()).getChildren();
		if (files == null) {
			throw new IOException("Directory " + getFile() + " could not be read. Does it exist and is it readable?");
		}
		for (FileObject f : files) {
			MapImage mim = new MapImage(getDatabase(), f.getName().getURI());
			add(mim);
		}
	}

	@Override
	protected MapImage createItem() {
		return new MapImage(getDatabase());
	}
}

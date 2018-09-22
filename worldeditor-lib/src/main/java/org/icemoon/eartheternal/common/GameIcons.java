package org.icemoon.eartheternal.common;

import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class GameIcons extends AbstractEntities<GameIcon, String, String, IStatic> {
	public GameIcons(String root) {
		this(null, root);
	}

	public GameIcons(IStatic database, String root) {
		super(database, String.class, root);
	}

	@Override
	public void doLoad() throws IOException {
		FileObject f = VFS.getManager().resolveFile(getFile());
		FileObject[] files = f.getChildren();
		if (files == null) {
			throw new IOException("Directory could not be read. Does it exist and is it readable?");
		}
		for (FileObject i : files) {
			GameIcon script = new GameIcon(getDatabase(), i.getName().getURI());
			add(script);
		}
	}

	@Override
	protected GameIcon createItem() {
		return new GameIcon(getDatabase());
	}
}

package org.icemoon.eartheternal.common;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class Sceneries<R extends IRoot> extends AbstractMultiINIFileEntities<Scenery<R>, Long, Long, R> {
	public static final String SCENERY_ADDITIVE = "SceneryAdditive";
	protected static final long BASE_SCENERY_ID = 1000000;
	private String root;
	private World<R> world;

	public Sceneries(R database, String root, World<R> world) {
		super(database, Long.class);
		this.root = root;
		this.world = world;
		setTrimComments(true);
	}

	@Override
	protected Scenery<R> createItem() {
		return new Scenery<R>(getDatabase(), this);
	}

	@Override
	protected void onRemove(Scenery<R> instance) {
		world.getWorld().remove(instance.getEntityId());
	}

	@Override
	protected final void onAdd(Scenery<R> entity) {
		if (world.getWorld().containsKey(entity.getEntityId()))
			throw new IllegalArgumentException("Scenery ID is already used in another Zone");
		world.getWorld().put(entity.getEntityId(), entity);
	}

	@Override
	protected String[] getFiles() {
		try {
			String path = root + "/" + getEntityId();
			final FileObject f = VFS.getManager().resolveFile(path);
			return Util.toFileURIList(f.findFiles(new FileSelector() {
				@Override
				public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
					return fileInfo.getFile().equals(f);
				}

				@Override
				public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
					return fileInfo.getFile().equals(f)
							|| fileInfo.getFile().getName().getBaseName().matches("x[0-9]+y[0-9]+\\.txt");
				}
			}));
		} catch (FileSystemException fse) {
			throw new RuntimeException(fse);
		}
	}
}

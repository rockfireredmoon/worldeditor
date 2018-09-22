package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public abstract class AbstractShops<S extends AbstractShop<I>, I extends AbstractShopItem>
		extends AbstractMultiINIFileEntities<S, SpawnKey, String, IDatabase> {
	private String shopFileName;

	protected AbstractShops(String shopFileName, String... files) {
		this(null, shopFileName, files);
	}

	protected AbstractShops(IDatabase database, String shopFileName, String... files) {
		super(database, SpawnKey.class, files);
		this.shopFileName = shopFileName;
		setMinId(new SpawnKey(0l, 0l));
		setMaxId(new SpawnKey(65535l, Long.valueOf(Integer.MAX_VALUE)));
		setTrimComments(false);
	}

	protected void onSavingNew(S instance) {
		instance.setFile(getFile() + "/" + instance.getEntityId().getZone() + "/" + shopFileName);
	}

	@Override
	protected String[] getFiles() {
		List<String> shops = new ArrayList<String>();
		for (String f : super.getFiles()) {
			try {
				FileObject dir = VFS.getManager().resolveFile(f);
				for (FileObject fo : dir.findFiles(new FileSelector() {
					@Override
					public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
						return true;
					}

					@Override
					public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
						return fileInfo.getFile().getName().getBaseName().equals(shopFileName);
					}
				})) {
					shops.add(fo.getName().getURI());
				}
			} catch (FileSystemException fse) {
				throw new IllegalStateException("Could not find files.", fse);
			}
		}
		return shops.toArray(new String[0]);
	}

	protected void doAddItem(FileObject file, S item) {
		item.getEntityId().setZone(Long.parseLong(file.getName().getParent().getBaseName()));
		super.doAddItem(file, item);
	}

	protected S configureFilterObject(S obj) {
		obj.setEntityId(null);
		return obj;
	}
}

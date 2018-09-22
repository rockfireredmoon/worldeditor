package org.icemoon.worldeditor.components;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.model.Model;
import org.icemoon.eartheternal.common.Log;
import org.icemoon.eartheternal.common.NonHiddenFilesFilter;
import org.icemoon.eartheternal.common.Util;

public class FileListModel extends Model<ArrayList<String>> {

	/**
	 * 
	 */
	private String root;
	private ArrayList<String> cache;
	private long lastRead;
	private long timeout = 600000;

	public FileListModel(String root) {
		this.root = root;
	}

	public ArrayList<String> getObject() {
		if (cache == null || System.currentTimeMillis() > lastRead + timeout) {
			lastRead = System.currentTimeMillis();
			cache = new ArrayList<String>();
			try {
				FileObject r = VFS.getManager().resolveFile(root);
				if (r != null && r.exists()) {
					for (FileObject f : r.findFiles(new NonHiddenFilesFilter())) {
						cache.add(Util.getBasename(f.getName().getBaseName()));
					}
				}
			} catch (FileSystemException fse) {
				throw new RuntimeException(fse);
			}
		}
		return cache;
	}
}
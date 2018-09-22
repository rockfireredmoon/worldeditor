package org.icemoon.eartheternal.common;

import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;

public class NonHiddenFilesFilter implements FileSelector {

	@Override
	public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
		if(fileInfo.getBaseFolder().equals(fileInfo.getFile())) {
			return false;
		}
		String fname = fileInfo.getFile().getName().getBaseName();
		return !fname.startsWith(".") && !fname.endsWith("~");
	}

	@Override
	public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
		return fileInfo.getDepth() < 1;
	}
}
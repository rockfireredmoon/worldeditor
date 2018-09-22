package org.icemoon.eartheternal.common;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;

@SuppressWarnings("serial")
public class AIScripts extends AbstractScripts<AIScript, String> {
	public AIScripts(String root) {
		this(null, root);
	}

	public AIScripts(IDatabase database, String root) {
		super(database, String.class, root);
	}

	@Override
	protected AIScript createItem() {
		return new AIScript(getDatabase());
	}

	@Override
	protected String filenameToEntityId(String key, FileObject file) {
		return FilenameUtils.getBaseName(key);
	}
}

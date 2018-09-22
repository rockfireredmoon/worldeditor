package org.icemoon.eartheternal.common;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;

@SuppressWarnings("serial")
public class QuestScripts extends AbstractScripts<QuestScript, Long> {
	public QuestScripts(String root) {
		this(null, root);
	}

	public QuestScripts(IDatabase database, String root) {
		super(database, Long.class, root);
	}

	@Override
	protected QuestScript createItem() {
		return new QuestScript(getDatabase());
	}

	@Override
	protected Long filenameToEntityId(String key, FileObject file) {
		return Long.parseLong(FilenameUtils.getBaseName(file.getName().getBaseName()));
	}
}

package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public abstract class AbstractScript<K extends Serializable> extends AbstractEntity<K, IDatabase> implements Script<K> {
	private String script;

	public AbstractScript(IDatabase database, String file, K id) {
		super(database, file, id);
	}

	public AbstractScript(IDatabase database) {
		super(database);
	}

	public Language getLanguage() {
		return StringUtils.isBlank(getFile()) || getFile().toLowerCase().endsWith(".nut") ? Language.SQUIRREL : Language.GSL;
	}

	public void setLanguage(Language language) {
		if (StringUtils.isBlank(getFile())) {
			setFile("." + language.getExtension());
		} else {
			throw new IllegalStateException("Cannot set language once file is set.");
		}
	}

	public final String getScript() {
		return script;
	}

	public final void setScript(String script) {
		this.script = script;
	}

	@Override
	public String toString() {
		return getEntityId() == null ? "" : getEntityId().toString();
	}

	@Override
	protected void doLoad() throws IOException {
		FileObject f = VFS.getManager().resolveFile(getFile());
		InputStream fin = f.getContent().getInputStream();
		try {
			setScript(IOUtils.toString(fin));
		} finally {
			fin.close();
		}
	}

	public enum ScriptType {
		GSL, SQUIRREL
	}
}

package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class Attachment extends AbstractMultiINIFileEntity<String, ISiteData> {
	private List<AttachedFile> files = new ArrayList<AttachedFile>();
	private transient AttachedFile parsingFile;
	private Attachments attachments;

	public Attachment(Attachments attachments) {
		this(null, attachments);
	}

	public Attachment(ISiteData database, Attachments attachments) {
		super(database);
		this.attachments = attachments;
	}

	public Attachment(ISiteData database, String file, String id, Attachments attachments) {
		super(database, file, id);
		this.attachments = attachments;
	}

	public Attachments getAttachments() {
		return attachments;
	}

	public final List<AttachedFile> getFiles() {
		return files;
	}

	public AttachedFile newAttachment(String by, String clientFileName, String contentType, long size) {
		AttachedFile f = new AttachedFile(getDatabase(), System.currentTimeMillis(), clientFileName, contentType, size, by);
		files.add(f);
		return f;
	}

	@Override
	public void set(String name, String value, String section) {
		if (section.equals("FILE") && name.equals("")) {
			parsingFile = new AttachedFile(getDatabase());
			files.add(parsingFile);
		} else if (section.equals("FILE") && name.equals("ID")) {
			parsingFile.setEntityId(Long.parseLong(value));
		} else if (section.equals("FILE")) {
			parsingFile.set(name, value, section);
		} else if (name.equals("ID")) {
			setEntityId(value);
		} else if (!name.equals("")) {
			Log.todo("Attachment", "Unhandled property " + name + " = " + value);
		}
	}

	public final void setFiles(List<AttachedFile> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return getEntityId() == null ? "<New>" : getEntityId();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("ID=" + getEntityId());
		writer.println("");
		for (AttachedFile f : files) {
			writer.println("[FILE]");
			f.write(writer);
			writer.println("");
		}
	}

	public class AttachedFile extends AbstractINIFileEntity<Long, ISiteData> {
		private String by;
		private long size;
		private String type;
		private String filename;

		public AttachedFile() {
			this(null);
		}

		public AttachedFile(ISiteData database) {
			super(database);
		}

		public AttachedFile(ISiteData database, long id) {
			super(database, null, id);
		}

		public AttachedFile(ISiteData database, long id, String filename, String type, long size, String by) {
			super(database, null, id);
			this.filename = filename;
			this.type = type;
			this.size = size;
			this.by = by;
		}

		public Attachment getAttachment() {
			return Attachment.this;
		}

		public final String getBy() {
			return by;
		}

		public final String getFilename() {
			return filename;
		}

		public final long getSize() {
			return size;
		}

		public final String getType() {
			return type;
		}

		@Override
		public void set(String name, String value, String section) {
			if (name.equals("ID")) {
				setEntityId(Long.parseLong(value));
			} else if (name.equals("By")) {
				by = value;
			} else if (name.equals("Filename")) {
				filename = value;
			} else if (name.equals("Size")) {
				size = Long.parseLong(value);
			} else if (name.equals("Type")) {
				type = value;
			} else {
				Log.todo("AttachedFile", "Unhandled property " + name + " = " + value);
			}
		}

		public final void setBy(String by) {
			this.by = by;
		}

		@Override
		public void setEntityId(Long id) {
			super.setEntityId(id);
			try {
				FileObject fileObj = VFS.getManager().resolveFile(attachments.getFile());
				final FileObject imgDir = fileObj.getParent().resolveFile("_Images");
				FileObject classDir = imgDir.resolveFile(attachments.getEntityId());
				classDir.createFolder();
				setFile(classDir.resolveFile(String.valueOf(id)).getName().getURI());
			} catch (FileSystemException fse) {
				throw new RuntimeException(fse);
			}
		}

		public final void setFilename(String filename) {
			this.filename = filename;
		}

		public final void setSize(long size) {
			this.size = size;
		}

		public final void setType(String type) {
			this.type = type;
		}

		@Override
		public void write(INIWriter writer) {
			writer.println("ID=" + getEntityId());
			writer.println("By=" + by);
			writer.println("Filename=" + filename);
			writer.println("Size=" + size);
			writer.println("Type=" + type);
		}

		@Override
		protected void doLoad() throws IOException {
		}
	}
}

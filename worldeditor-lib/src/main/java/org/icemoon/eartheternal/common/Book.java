package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;

@SuppressWarnings("serial")
public class Book extends AbstractSeparateINIFileEntity<Long, IDatabase> {
	private List<String> pages = new ArrayList<String>();
	private String title;

	public Book() {
		this(null);
	}

	public Book(IDatabase database) {
		super(database);
	}

	public final String getTitle() {
		return title;
	}

	public final List<String> getPages() {
		return pages;
	}

	public final void setPages(List<String> pages) {
		this.pages = pages;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected void reset() {
		super.reset();
		pages.clear();
	}

	@Override
	public String toString() {
		return getTitle();
	}

	@Override
	protected INIReader<AbstractINIFileEntity<Long, IDatabase>, Long, IDatabase> createReader(FileObject f) {
		INIReader<AbstractINIFileEntity<Long, IDatabase>, Long, IDatabase> reader = super.createReader(f);
		reader.setStart("BOOK");
		return reader;
	}

	@Override
	public void set(String name, String value, String section) {
		if (section.equals("BOOK")) {
			if (name.equals("TITLE")) {
				title = value;
			} else if (!name.equals("")) {
				Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
			}
		} else if (section.equalsIgnoreCase("PAGE")) {
			if (name.equals("TEXT")) {
				pages.add(value);
			} else if (!name.equals("")) {
				Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
			}
		} else {
			Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
		}
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[BOOK]");
		writer.println("TITLE=" + title);
		writer.println();
		for (String p : pages) {
			writer.println("[PAGE]");
			writer.println("TEXT=" + p);
		}
	}
}

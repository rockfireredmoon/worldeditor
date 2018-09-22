package org.icemoon.worldeditor;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@SuppressWarnings("serial")
public class FileTreeProvider implements ITreeProvider<File> {
	
	private File root;
	
	public FileTreeProvider(File root) {
		this.root = root;
	}
	
	@Override
	public void detach() {			
	} 

	@Override
	public Iterator<? extends File> getRoots() {
		File[] listFiles = root.listFiles();
		return Arrays.asList(listFiles == null ? new File[0] : listFiles).iterator();
	}

	@Override
	public boolean hasChildren(File object) {
		return object.isDirectory();
	}

	@Override
	public Iterator<? extends File> getChildren(File object) {
		return Arrays.asList(object.listFiles()).iterator();
	}

	@Override
	public IModel<File> model(File object) {
		return new Model<File>(object);
	}
	
}
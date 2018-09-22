package org.icemoon.eartheternal.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class SpawnPackages extends AbstractMultiINIFileEntities<SpawnPackage, String, String, IDatabase> {

	public SpawnPackages(String root) {
		this(null, root);
	}
	
	public SpawnPackages(IDatabase database, String root) {
		super(database, String.class,root);
		setTrimComments(false);
	}

	@Override
	public String[] getFiles() {
		try {
			FileObject pkgFile = VFS.getManager().resolveFile(getFile());
			List<String> s = new ArrayList<String>();
			BufferedReader r = new BufferedReader(new InputStreamReader(pkgFile.getContent().getInputStream()));
			try {
				String line = null;
				while ((line = r.readLine()) != null) {
					line = line.trim();
					if (!line.equals("") && !line.startsWith(";")) {
						s.add(pkgFile.getParent().getName().getURI() + "/" + line);
					}
				}
			} finally {
				r.close();
			}
			return s.toArray(new String[0]);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected SpawnPackage createItem() {
		return new SpawnPackage(getDatabase());
	}

//	@Override
//	protected void createNew(SpawnPackage instance) throws IOException {
//		// Just append. If the package name is new, it will get created
//		FileObject f = VFS.getManager().resolveFile(instance.getFile());
//		INIWriter pw = new INIWriter(f.getContent().getOutputStream(true));
//		try {
//			pw.println();
//			instance.write(pw);
//		} finally {
//			pw.close();
//		}
//	}

}

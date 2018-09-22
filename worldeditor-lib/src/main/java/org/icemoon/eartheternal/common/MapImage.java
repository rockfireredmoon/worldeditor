package org.icemoon.eartheternal.common;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class MapImage extends AbstractEntity<String, IStatic> {
	private int width;
	private int height;

	public MapImage() {
		this(null);
	}

	public MapImage(IStatic database) {
		super(database);
	}

	public MapImage(IStatic database, String file) throws IOException {
		super(database);
		setFile(file);
		String name = FilenameUtils.getBaseName(file);
		// There are a couple of oddities with map images
		// 1. Europe_Map_Region_DeadWood != Europe_Map_Region_Deadwood (case
		// issue)
		// 2. Map_World_BG (MapDef says MapWorldBG)
		if (name.equals("Map_World_BG")) {
			name = "MapWorldBG";
		} else if (name.equals("Europe_Map_Region_DeadWood")) {
			name = "Europe_Map_Region_Deadwood";
		}
		setEntityId(name);
		// Load once to get size
		BufferedImage img = getImage();
		if (img != null) {
			width = img.getWidth();
			height = img.getHeight();
		}
	}

	public final int getHeight() {
		return height;
	}

	public final BufferedImage getImage() {
		try {
			FileObject f = VFS.getManager().resolveFile(getFile());
			final InputStream inputStream = f.getContent().getInputStream();
			try {
				return ImageIO.read(inputStream);
			} finally {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public final int getWidth() {
		return width;
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	protected void doLoad() throws IOException {
	}
}

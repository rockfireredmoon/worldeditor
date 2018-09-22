package org.icemoon.eartheternal.common;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class GameIcon extends AbstractEntity<String, IStatic> {

	public GameIcon() {
		this(null);
	}
	public GameIcon(IStatic database) {
		super(database);
	}

	public GameIcon(IStatic database, String file) throws IOException {
		super(database);
		setFile(file);
		String name = FilenameUtils.getName(file);
		setEntityId(name);
	}

	public final BufferedImage getImage() {
		try {
			final InputStream inputStream = VFS.getManager().resolveFile(getFile()).getContent().getInputStream();
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

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	protected void doLoad() throws IOException {
	}
}

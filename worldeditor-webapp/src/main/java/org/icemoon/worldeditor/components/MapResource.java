package org.icemoon.worldeditor.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;
import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.GameIcon;
import org.icemoon.eartheternal.common.GameIcons;
import org.icemoon.eartheternal.common.MapDef;
import org.icemoon.eartheternal.common.MapImage;
import org.icemoon.eartheternal.common.MapPoint;
import org.icemoon.eartheternal.common.Viewport;

public class MapResource extends RenderedDynamicImageResource {
	private static final long serialVersionUID = 1L;
	private MapImage mapImage;
	private IModel<Viewport> viewport;
	private IModel<? extends List<MapPoint>> points;
	private IModel<MapDef> model;
	private IModel<GameIcons> icons;

	public MapResource(IModel<MapDef> model, IModel<? extends List<MapPoint>> points, IModel<Viewport> viewport, MapImage mapImage,
			IModel<GameIcons> icons) {
		super(viewport.getObject().getWidth(), viewport.getObject().getHeight());
		this.icons = icons;
		this.model = model;
		this.points = points;
		this.mapImage = mapImage;
		this.viewport = viewport;
		setFormat("JPEG");
	}

	@Override
	public synchronized int getHeight() {
		return viewport.getObject().getHeight();
	}

	@Override
	public synchronized int getWidth() {
		return viewport.getObject().getWidth();
	}

	@Override
	protected boolean render(Graphics2D graphics, final Attributes attributes) {
		Viewport vp = viewport.getObject();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int tlx = vp.getX();
		int tly = vp.getY();
		int w = (int) ((float) mapImage.getWidth() / vp.getScale());
		int h = (int) ((float) mapImage.getHeight() / vp.getScale());
		int brx = tlx + w;
		int bry = tly + h;
		if (mapImage != null) {
			final BufferedImage image = mapImage.getImage();
			graphics.drawImage(image, 0, 0, getWidth(), getHeight(), tlx, tly, brx, bry, null);
		}
		final MapDef mapDef = model.getObject();
		final List<MapPoint> pointsObj = points.getObject();
		if (pointsObj != null) {
			for (MapPoint point : pointsObj) {
				Image img = null;
				try {
					if (point.getImageName().startsWith("gameIcon:")) {
						GameIcon gi = icons.getObject().get(point.getImageName().substring(9));
						if (gi != null) {
							FileObject f = VFS.getManager().resolveFile(gi.getFile());
							final InputStream inputStream = f.getContent().getInputStream();
							try {
								img = ImageIO.read(inputStream);
							} finally {
								inputStream.close();
							}
						}
					} else {
						img = ImageIO.read(MapPanel.class.getResource(point.getImageName()));
					}
					if (img != null) {
						float sx = (float) point.getDiameter() / (float) img.getWidth(null);
						float sy = (float) point.getDiameter() / (float) img.getHeight(null);
						float scale = Math.max(sx, sy);
						int iw = (int) ((float) img.getWidth(null) * scale);
						int ih = (int) ((float) img.getHeight(null) * scale);
						int ix = point.getActualX(mapImage, mapDef, vp) - (iw / 2);
						int iy = point.getActualY(mapImage, mapDef, vp) - (ih / 2);
						graphics.drawImage(img, ix, iy, iw, ih, null);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (vp.isSelected()) {
			graphics.setColor(Color.yellow);
			int sx = vp.getScaledX(mapImage, vp.getSelX());
			int sy = vp.getScaledY(mapImage, vp.getSelY());
			int esx = vp.getScaledX(mapImage, vp.getEndSelX());
			int esy = vp.getScaledY(mapImage, vp.getEndSelY());
			graphics.drawRect(sx, sy, esx - sx, esy - sy);
			// graphics.drawRect(sx + 1, sy + 1, esx - sx - 2, esy- sy - 2);
			// graphics.drawRect(vp.getSelX(), vp.getSelY(), vp.endSelX -
			// vp.getSelX(), vp.endSelY - vp.getSelY());
			// graphics.drawRect(vp.getSelX() + 1, vp.getSelY() + 1, vp.endSelX
			// - vp.getSelX() - 2, vp.endSelY - vp.getSelY() - 2);
		}
		return true;
	}
}
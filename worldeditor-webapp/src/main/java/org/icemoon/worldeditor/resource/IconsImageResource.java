package org.icemoon.worldeditor.resource;

import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;
import org.icemoon.eartheternal.common.GameIcon;

@SuppressWarnings("serial")
public class IconsImageResource extends RenderedDynamicImageResource {
	private GameIcon icon1;
	private GameIcon icon2;

	public IconsImageResource(int width, int height, GameIcon icon1, GameIcon icon2) {
		super(width, height);
		this.icon1 = icon1;
		this.icon2 = icon2;
	}

	@Override
	protected boolean render(Graphics2D graphics, final Attributes attributes) {
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		if (icon2 != null) {
			graphics.drawImage(icon2.getImage(), 0, 0, getWidth(), getHeight(), null);
		}
		if (icon1 != null) {
			graphics.drawImage(icon1.getImage(), 0, 0, getWidth(), getHeight(), null);
		}
		return true;
	}
}
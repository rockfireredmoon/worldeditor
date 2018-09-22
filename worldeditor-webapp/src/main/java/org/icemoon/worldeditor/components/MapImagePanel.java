package org.icemoon.worldeditor.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.icemoon.eartheternal.common.MapImage;
import org.icemoon.eartheternal.common.MapImages;
import org.icemoon.worldeditor.Application;

@SuppressWarnings("serial")
public class MapImagePanel extends Panel {
	private final static ResourceReference EMPTY = new PackageResourceReference(MapImagePanel.class, "empty.gif");

	public MapImagePanel(String id, final IModel<String> mapNameModel) {
		this(id, mapNameModel, 128);
	}

	public MapImagePanel(String id, final IModel<String> mapNameModel, int size) {
		super(id, mapNameModel);
		final Image image = new Image("image", new Model<ResourceReference>() {
			@Override
			public ResourceReference getObject() {
				String icon = mapNameModel == null ? null : mapNameModel.getObject();
				MapImage gi = icon == null ? null : Application.getApp().getStaticDatabase().getMapImages().get(icon);
				if (gi == null) {
					return EMPTY;
				} else {
					return new SharedResourceReference(MapImages.class, icon);
				}
			}
		});
		add(new AttributeModifier("style", "vertical-align: top ; width: " + size + "px"));
		image.add(new AttributeModifier("width", size + "px"));
		add(image);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(MapImagePanel.class, "MapImagePanel.css")));
	}
}
package org.icemoon.worldeditor.components;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.icemoon.eartheternal.common.GameIcon;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.model.FileObjectResourceReference;

@SuppressWarnings("serial")
public class GameIconPanel extends Panel {
	private final static ResourceReference EMPTY = new PackageResourceReference(GameIconPanel.class, "empty.gif");

	private final class GameIconResourceModel extends Model<ResourceReference> {
		private final IModel<String> iconModel;

		private GameIconResourceModel(IModel<String> iconModel) {
			this.iconModel = iconModel;
		}

		@Override
		public ResourceReference getObject() {
			String icon = iconModel == null ? null : iconModel.getObject();
			GameIcon gi = icon == null ? null : Application.getApp().getStaticDatabase().getGameIcons().get(icon);
			if (gi == null) {
				return EMPTY;
			} else {
				return new FileObjectResourceReference(iconModel.getObject(), "image/png", gi.getFile());
			}
		}
	}

	public GameIconPanel(String id, final IModel<String> icon1Model) {
		this(id, icon1Model, null, 24);
	}

	public GameIconPanel(String id, final IModel<String> icon1Model, final IModel<String> icon2Model) {
		this(id, icon1Model, icon2Model, 24);
	}

	public GameIconPanel(String id, final IModel<String> icon1Model, int size) {
		this(id, icon1Model, null, size);
	}

	public GameIconPanel(String id, final IModel<String> icon1Model, final IModel<String> icon2Model, int size) {
		super(id);
		final Image image = new NonCachingImage("icon1", new GameIconResourceModel(icon1Model)) {

			@Override
			protected boolean shouldAddAntiCacheParameter() {
				return false;
			}

		};
		add(new AttributeModifier("style", "vertical-align: top ; width: " + size + "px ; height: " + size + "px"));
		image.add(new AttributeModifier("width", size + "px"));
		image.add(new AttributeModifier("height", size + "px"));

		add(image);
		final Image image2 = new NonCachingImage("icon2", new GameIconResourceModel(icon2Model)) {

			@Override
			protected boolean shouldAddAntiCacheParameter() {
				return false;
			}

		};
		image2.add(new AttributeModifier("style", "left: -" + size + "px ; margin-right: -" + size + "px"));
		add(image2);
		image2.add(new AttributeModifier("width", size + "px"));
		image2.add(new AttributeModifier("height", size + "px"));

	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(GameIconPanel.class, "GameIconPanel.css")));
	}
}
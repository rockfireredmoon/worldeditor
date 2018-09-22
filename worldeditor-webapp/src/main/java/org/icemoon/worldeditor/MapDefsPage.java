package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IStatic;
import org.icemoon.eartheternal.common.MapDef;
import org.icemoon.eartheternal.common.MapDefs;
import org.icemoon.eartheternal.common.MapImage;
import org.icemoon.eartheternal.common.MapImages;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.eartheternal.common.MapDef.MapType;
import org.icemoon.worldeditor.components.MapImagePanel;
import org.icemoon.worldeditor.components.PageSizePanel;
import org.icemoon.worldeditor.components.RectanglePanel;
import org.icemoon.worldeditor.model.MapDefListModel;
import org.icemoon.worldeditor.model.UniqueListModel;
import org.icemoon.worldeditor.table.ClassedPropertyColumn;

@SuppressWarnings("serial")
public class MapDefsPage extends AbstractEntityPage<MapDef, String, String, MapDefs, IDatabase> {
	private MapImagePanel parentMapImagePreview;
	private MapImagePanel imagePreview;

	public MapDefsPage() {
		super("id", String.class);
	}

	@Override
	protected void buildForm(Form<MapDef> form) {
		form.add(new AjaxButton("viewMap") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				PageParameters params = new PageParameters();
				params.set("id", getSelected().getEntityId());
				setResponsePage(DynamicMapPage.class, params);
			}

			@Override
			public boolean isEnabled() {
				return getSelected().getEntityId() != null;
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		form.add(new FixedAutocompleteComponent<String>("name", new PropertyModel<String>(this, "selected.name"),
				new UniqueListModel<String, MapDef>(new MapDefListModel(new PropertyModel<IDatabase>(this, "database")), "name")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		}.setRequired(true));
		form.add(new FixedAutocompleteComponent<String>("primary", new PropertyModel<String>(this, "selected.primary"),
				new ListModel<String>() {
					public List<String> getObject() {
						List<String> l = new ArrayList<String>();
						for (ZoneDef instance : getDatabase().getZoneDefs().values()) {
							if (Util.isNotNullOrEmpty(instance.getWarpName())) {
								l.add("Maps-" + instance.getWarpName());
							}
						}
						return l;
					}
				}) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		}.setRequired(true));
		form.add(new ListChoice<MapType>("type", Arrays.asList(MapType.values())).setMaxRows(1));
		form.add(new PageSizePanel("pages"));
		form.add(new TextField<Integer>("priority", Integer.class));
		form.add(new RectanglePanel("bounds"));
		IModel<MapImages> mapImagesModel = new Model<MapImages>(Application.getApp().getStaticDatabase().getMapImages());
		// Parent map image
		final IModel<String> imageModel = new PropertyModel<String>(this, "selected.image");
		final ImageChooser imageChooser = new ImageChooser("image", new Model<String>("Map Image"), mapImagesModel, "id",
				imageModel, MapImage.class) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, MapImage entity) {
				if (target != null) {
					target.add(imagePreview);
				}
			}
		};
		imageChooser.setOutputMarkupId(true);
		form.add(imageChooser);
		form.add(imagePreview = new MapImagePanel("imagePreview", imageModel, 96));
		imagePreview.setOutputMarkupId(true);
		// Parent map image
		final IModel<String> parentMapImageModel = new PropertyModel<String>(this, "selected.parentMapImage");
		final ImageChooser parentMapImageChooser = new ImageChooser("parentMapImage", new Model<String>("Parent Map Image"),
				mapImagesModel, "entityId", parentMapImageModel, MapImage.class) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, MapImage entity) {
				if (target != null) {
					target.add(parentMapImagePreview);
				}
			}
		};
		parentMapImageChooser.setOutputMarkupId(true);
		form.add(parentMapImageChooser);
		form.add(parentMapImagePreview = new MapImagePanel("parentMapImagePreview", parentMapImageModel, 96));
		parentMapImagePreview.setOutputMarkupId(true);
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(MapDefsPage.class, "MapDefsPage.css")));
	}

	@Override
	protected MapDef createNewInstance() {
		return new MapDef(getDatabase());
	}

	@Override
	public MapDefs getEntityDatabase() {
		return getDatabase().getMapDefs();
	}

	@Override
	protected void buildColumns(List<IColumn<MapDef, String>> columns) {
		columns.add(new ClassedPropertyColumn<MapDef>(new ResourceModel("column.name"), "name", "name", "name"));
		columns.add(new ClassedPropertyColumn<MapDef>(new ResourceModel("column.type"), "type", "type", "type"));
	}

	private class ImageChooser extends SelectorPanel<String, MapImage, String, IStatic> {
		private ImageChooser(String id, IModel<String> title,
				IModel<? extends Entities<MapImage, String, String, IStatic>> entities, String displayNameProperty,
				IModel<String> model, Class<MapImage> filterObjectClass) {
			super(id, title, entities, displayNameProperty, model, filterObjectClass, String.class, null);
		}

		@Override
		protected void buildExtraChooserColumns(List<IColumn<MapImage, String>> columns) {
			columns.add(0, new AbstractColumn<MapImage, String>(new Model<String>(""), "icon") {
				public void populateItem(Item<ICellPopulator<MapImage>> cellItem, String componentId, IModel<MapImage> model) {
					cellItem.add(new AttributeModifier("class", "icon"));
					MapImagePanel panel = new MapImagePanel(componentId, new Model<String>(model.getObject().getEntityId()), 48);
					cellItem.add(panel);
				}
			});
		}
	}
}

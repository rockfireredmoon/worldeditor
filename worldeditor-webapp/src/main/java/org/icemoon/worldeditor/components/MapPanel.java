package org.icemoon.worldeditor.components;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.icemoon.eartheternal.common.GameIcons;
import org.icemoon.eartheternal.common.MapDef;
import org.icemoon.eartheternal.common.MapImage;
import org.icemoon.eartheternal.common.MapPoint;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.eartheternal.common.Viewport;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.components.AjaxMouseEventBehavior.EventType;
import org.odlabs.wiquery.ui.core.CoreUIJavaScriptResourceReference;

@SuppressWarnings("serial")
public class MapPanel extends Panel {
	private IModel<? extends List<MapPoint>> allPoints;
	private IModel<Viewport> viewport = new Model<Viewport>(new Viewport());
	private WebMarkupContainer imageContainer;
	private int height = -1;
	private int width = -1;
	private IModel<List<MapPoint>> points;
	private boolean useImageMap = true;
	{
		points = new IModel<List<MapPoint>>() {
			private transient List<MapPoint> points;

			@Override
			public void detach() {
				points = null;
			}

			@Override
			public List<MapPoint> getObject() {
				if (points == null) {
					final List<MapPoint> ap = allPoints.getObject();
					points = (List<MapPoint>) (ap == null ? Collections.emptyList()
							: MapUtil.getPointsForMap(ap, getModelObject()));
				}
				return points;
			}

			@Override
			public void setObject(List<MapPoint> object) {
			}
		};
	}

	public MapPanel(String id, IModel<? extends List<MapPoint>> allPoints) {
		super(id);
		this.allPoints = allPoints;
		setDefaultModel(new IModel<MapDef>() {
			@Override
			public void detach() {
			}

			@Override
			public MapDef getObject() {
				final List<MapPoint> ap = MapPanel.this.allPoints.getObject();
				List<MapDef> availableMaps = (List<MapDef>) (ap == null ? Collections.emptyList() : MapUtil.getUniqueMaps(ap));
				return availableMaps == null || availableMaps.isEmpty() ? null : availableMaps.get(0);
			}

			@Override
			public void setObject(MapDef object) {
			}
		});
	}

	public MapPanel(String id, IModel<MapDef> model, IModel<? extends List<MapPoint>> allPoints) {
		super(id, model);
		this.allPoints = allPoints;
	}

	public final boolean isUseImageMap() {
		return useImageMap;
	}

	public final void setUseImageMap(boolean useImageMap) {
		this.useImageMap = useImageMap;
	}

	public MapPanel setWidth(int width) {
		this.width = width;
		return this;
	}

	public MapPanel setHeight(int height) {
		this.height = height;
		return this;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		addImage();
		addNavigation();
		addOptions();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onBeforeRender() {
		calcScales();
		super.onBeforeRender();
	}

	protected void calcScales() {
		MapImage img = getMapImage();
		int iw = img == null ? 700 : img.getWidth();
		int ih = img == null ? 300 : img.getHeight();
		if (width != -1 || height != -1) {
			if (width == -1 && height != -1) {
				// Scale heightwise
				float scale = (float) height / (float) ih;
				ih = height;
				iw *= scale;
			} else if (width != -1 && height == -1) {
				// Scale widthwise
				float scale = (float) width / (float) iw;
				ih *= scale;
				iw = width;
			} else {
				// Distort
				iw = width;
				ih = height;
			}
		}
		viewport.getObject().setWidth(iw);
		viewport.getObject().setHeight(ih);
	}

	protected void addImage() {
		imageContainer = new WebMarkupContainer("imageContainer");
		imageContainer.setOutputMarkupId(true);
		imageContainer.add(new AttributeModifier("style", new Model<String>() {
			public String getObject() {
				return "height: " + viewport.getObject().getHeight() + "px;";
			}
		}));
		final Image image = new Image("backgroundImage", new Model<IResource>() {
			@Override
			public IResource getObject() {
				MapImage img = getMapImage();
				return img == null ? null : new MapResource(getModel(), points, viewport, img, new Model<GameIcons>() {
					@Override
					public GameIcons getObject() {
						return Application.getApp().getStaticDatabase().getGameIcons();
					}
				});
			}
		});
		image.add(new AjaxMouseEventBehavior(EventType.CLICK, EventType.DBLCLICK, EventType.MOUSEDOWN, EventType.MOUSEUP) {
			@Override
			protected void onMouseEvent(AjaxRequestTarget target, EventType type, double dx, double dy) {
				final Viewport viewportObj = viewport.getObject();
				int x = viewportObj.getActualX(getMapImage(), (int) dx);
				int y = viewportObj.getActualY(getMapImage(), (int) dy);
				if (type.equals(EventType.MOUSEDOWN) && !viewportObj.isSelected()) {
					viewportObj.startSelect(x, y);
				} else if (type.equals(EventType.MOUSEUP) && viewportObj.isHalfSelected()) {
					viewportObj.endSelect(x, y);
					target.add(imageContainer);
				} else if (type.equals(EventType.CLICK)) {
					if (x == viewportObj.getEndSelX() && y == viewportObj.getEndSelY()) {
						// we get a click after mouse up, so ignore it
					} else {
						if (viewportObj.isInSelect(x, y)) {
							viewportObj.zoomOnSelection(getMapImage());
						} else {
							viewportObj.clearSelect();
						}
						target.add(imageContainer);
					}
				}
			}
		});
		imageContainer.add(image.setOutputMarkupId(true));
		// Map
		WebMarkupContainer imageMap = new WebMarkupContainer("imageMap") {
			public boolean isVisible() {
				return useImageMap;
			}
		};
		imageContainer.add(imageMap);
		imageMap.add(new ListView<MapPoint>("mapPoint", points) {
			@Override
			protected void populateItem(ListItem<MapPoint> item) {
				final MapPoint point = item.getModelObject();
				MapDef map = MapPanel.this.getModelObject();
				MapImage img = getMapImage();
				Viewport vp = viewport.getObject();
				int x = point.getActualX(img, map, vp);
				int y = point.getActualY(img, map, vp);
				item.add(new AttributeModifier("coords", x + "," + y + "," + (point.getDiameter() / 2)));
				item.add(new AttributeModifier("alt", point.getText()));
				item.setOutputMarkupId(true);
				item.setMarkupId("mapPoint" + point.hashCode());
			}

			public boolean isVisible() {
				return useImageMap;
			}
		});
		imageContainer.add(new ListView<MapPoint>("individualTooltips", points) {
			@Override
			protected void populateItem(ListItem<MapPoint> item) {
				final MapPoint point = item.getModelObject();
				MapDef map = MapPanel.this.getModelObject();
				MapImage img = getMapImage();
				Viewport vp = viewport.getObject();
				int x = point.getActualX(img, map, vp);
				int y = point.getActualY(img, map, vp);
				item.add(new AttributeModifier("style",
						"position: relative; left: " + (x - (point.getDiameter() / 2)) + "px; top: "
								+ (y - (point.getDiameter() / 2)) + "px; width: " + point.getDiameter() + "px; height:"
								+ point.getDiameter() + "px;"));
				item.setOutputMarkupId(true);
				item.setMarkupId("mapPoint" + point.hashCode());
			}

			public boolean isVisible() {
				return !useImageMap;
			}
		});
		imageContainer.add(new WebMarkupContainer("imageMapTooltips") {
			public boolean isVisible() {
				return useImageMap;
			}
		});
		imageContainer.add(new Label("individualTooltipsScript") {
			public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
				StringBuilder bui = new StringBuilder();
				bui.append("$(document).ready(function() {\n");
				for (MapPoint p : points.getObject()) {
					bui.append("$(\"#mapPoint");
					bui.append(p.hashCode());
					bui.append("\").qtip({ content: { text: ");
					bui.append("$('#mapPoint");
					bui.append(p.hashCode());
					bui.append("Content')");
					// bui.append("'");
					// bui.append(StringEscapeUtils.escapeJavaScript(p.getText()));
					// bui.append("'");
					bui.append("}, show: { ready: true } });\n");
				}
				bui.append("});\n");
				replaceComponentTagBody(markupStream, openTag, bui.toString());
			}

			public boolean isVisible() {
				return !useImageMap;
			}
		});
		// Map
		imageContainer.add(new ListView<MapPoint>("mapPointTooltipContent", points) {
			@Override
			protected void populateItem(ListItem<MapPoint> item) {
				item.setMarkupId("mapPoint" + item.getModelObject().hashCode() + "Content");
				item.add(new MapPointPanel("mapPointTooltipContent", item.getModel()));
			}
		});
		add(imageContainer);
	}

	protected void addOptions() {
		// new PropertyModel<List<MapDef>>(this, "availableMaps")
		Form<?> mapForm = new Form<Object>("mapForm");
		mapForm.add(
				new ListChoice<MapDef>("map", new PropertyModel<MapDef>(this, "defaultModelObject"), new IModel<List<MapDef>>() {
					private transient List<MapDef> availableMaps;

					@Override
					public void detach() {
						availableMaps = null;
					}

					@Override
					public List<MapDef> getObject() {
						if (availableMaps == null) {
							final List<MapPoint> ap = allPoints.getObject();
							availableMaps = (List<MapDef>) (ap == null ? Collections.emptyList() : MapUtil.getUniqueMaps(ap));
						}
						return availableMaps;
					}

					@Override
					public void setObject(List<MapDef> object) {
					}
				}).setMaxRows(1).setNullValid(false).add(new AjaxFormComponentUpdatingBehavior("onchange") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						target.add(imageContainer);
					}
				}));
		add(mapForm);
	}

	protected void addNavigation() {
		add(new AjaxLink<String>("left") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				viewport.getObject().left();
				target.add(imageContainer);
			}
		});
		add(new AjaxLink<String>("right") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				viewport.getObject().right();
				target.add(imageContainer);
			}
		});
		add(new AjaxLink<String>("center") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				viewport.getObject().reset();
				target.add(imageContainer);
			}
		});
		add(new AjaxLink<String>("up") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				viewport.getObject().up();
				target.add(imageContainer);
			}
		});
		add(new AjaxLink<String>("down") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				viewport.getObject().down();
				target.add(imageContainer);
			}
		});
		add(new AjaxLink<String>("zoomin") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				viewport.getObject().zoomIn();
				target.add(imageContainer);
			}
		});
		add(new AjaxLink<String>("zoomout") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				viewport.getObject().zoomOut();
				target.add(imageContainer);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public IModel<MapDef> getModel() {
		return (IModel<MapDef>) getDefaultModel();
	}

	public MapDef getModelObject() {
		final IModel<MapDef> model = getModel();
		return model == null ? null : model.getObject();
	}

	public MapImage getMapImage() {
		return MapUtil.getMapImage(getModelObject(), Application.getApp().getStaticDatabase());
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(MapPanel.class, "MapPanel.css")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "jquery.qtip.css")));
		response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(Application.class, "jquery.qtip.js")));
		response.render(JavaScriptHeaderItem.forReference(CoreUIJavaScriptResourceReference.get()));
	}
}

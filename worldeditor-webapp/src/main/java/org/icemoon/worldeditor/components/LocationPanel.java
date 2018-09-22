package org.icemoon.worldeditor.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Location;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.worldeditor.SelectorPanel;
import org.icemoon.worldeditor.ZoneDefsPage;
import org.icemoon.worldeditor.model.ZoneDefsModel;

@SuppressWarnings("serial")
public class LocationPanel extends FormComponentPanel<Location> {
	private TextField<Double> xText;
	private TextField<Double> yText;
	private TextField<Double> zText;
	private Double x, y, z;
	private Long instance;
	private WebMarkupContainer instanceContainer = new WebMarkupContainer("instanceContainer");
	private SelectorPanel<Long, ZoneDef, String, IDatabase> selector;
	private boolean allowClear;
	private IModel<IDatabase> database;

	public LocationPanel(final String id, IModel<IDatabase> database) {
		super(id);
		this.database = database;
	}

	public LocationPanel(final String id, IModel<Location> model, IModel<IDatabase> database) {
		super(id, model);
		this.database = database;
	}

	@Override
	protected void onInitialize() {
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		setType(Location.class);
		// Location - dependent on instance
		xText = new TextField<Double>("x", new PropertyModel<Double>(this, "x"), Double.class) {
			@Override
			public boolean isEnabled() {
				return instance != null;
			}
		};
		xText.setOutputMarkupId(true);
		xText.setRequired(true);
		xText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(xText);
		yText = new TextField<Double>("y", new PropertyModel<Double>(this, "y"), Double.class) {
			@Override
			public boolean isEnabled() {
				return instance != null;
			}
		};
		yText.setOutputMarkupId(true);
		yText.setRequired(true);
		yText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(yText);
		zText = new TextField<Double>("z", new PropertyModel<Double>(this, "z"), Double.class) {
			@Override
			public boolean isEnabled() {
				return instance != null;
			}
		};
		zText.setOutputMarkupId(true);
		zText.setRequired(true);
		zText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(zText);
		// Instance
		instanceContainer.add(selector = new SelectorPanel<Long, ZoneDef, String, IDatabase>("instance",
				new Model<String>("Instance"), new ZoneDefsModel(database), "warpName", "entityId",
				new PropertyModel<Long>(this, "instance"), ZoneDef.class, Long.class, ZoneDefsPage.class) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, ZoneDef entity) {
				super.onEntitySelected(target, entity);
				target.add(xText);
				target.add(yText);
				target.add(zText);
				onInstanceSelected(target, entity);
			}
		});
		instanceContainer.setOutputMarkupId(true);
		selector.setShowLabel(true);
		selector.setShowClear(allowClear);
		add(instanceContainer);
		super.onInitialize();
	}
	
	public void refresh(AjaxRequestTarget target) {
		target.add(get("instanceContainer"));
		target.add(get("x"));
		target.add(get("y"));
		target.add(get("z"));
	}

	protected void onInstanceSelected(AjaxRequestTarget target, ZoneDef entity) {
	}

	@Override
	protected void onBeforeRender() {
		if (!hasErrorMessage()) {
			Location total = getModelObject();
			x = total == null ? 0 : total.getX();
			y = total == null ? 0 : total.getY();
			z = total == null ? 0 : total.getZ();
			instance = total == null ? null : total.getInstance();
			xText.clearInput();
			yText.clearInput();
			zText.clearInput();
			super.onBeforeRender();
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(LocationPanel.class, "LocationPanel.css")));
	}

	@Override
	protected void convertInput() {
		final Double nx = xText.getConvertedInput();
		final Double ny = yText.getConvertedInput();
		final Double nz = zText.getConvertedInput();
		Location loc = getConvertedInput();
		if (loc == null) {
			loc = new Location(0, 0, 0, instance);
		}
		loc.setX(nx == null ? 0 : nx);
		loc.setY(ny == null ? 0 : ny);
		loc.setZ(nz == null ? 0 : nz);
		setConvertedInput(loc);
	}

	public final Long getInstance() {
		return instance;
	}

	public final void setInstance(Long instance) {
		this.instance = instance;
	}

	public LocationPanel setShowInstance(boolean showInstance) {
		instanceContainer.setVisible(showInstance);
		return this;
	}

	public void setAllowClear(boolean allowClear) {
		this.allowClear = allowClear;
		if (selector != null) {
			selector.setShowClear(allowClear);
		}
	}
}
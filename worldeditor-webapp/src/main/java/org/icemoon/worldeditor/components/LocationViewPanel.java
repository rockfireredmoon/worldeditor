package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Location;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.worldeditor.Application;

@SuppressWarnings("serial")
public class LocationViewPanel extends Panel {
	private boolean useGetByNumber;

	public LocationViewPanel(final String id, IModel<Location> model) {
		this(id, model, true);
	}

	public LocationViewPanel(final String id, IModel<Location> model, boolean useGetByNumber) {
		super(id, model);
		this.useGetByNumber = useGetByNumber;
	}

	@Override
	protected void onInitialize() {
		add(new Label("x", new PropertyModel<Double>(getDefaultModel(), "x")));
		add(new Label("y", new PropertyModel<Double>(getDefaultModel(), "y")));
		add(new Label("z", new PropertyModel<Double>(getDefaultModel(), "z")));
		add(new Label("instance", new PropertyModel<String>(this, "instanceName")));
		super.onInitialize();
	}

	public String getInstanceName() {
		final Long instanceId = ((Location) getDefaultModelObject()).getInstance();
		ZoneDef instance = instanceId == null ? null
				: (useGetByNumber ? Application.getAppSession(getRequestCycle()).getDatabase().getZoneDefs().get(instanceId)
						: Application.getAppSession(getRequestCycle()).getDatabase().getZoneDefs().get(instanceId));
		return instance == null ? "<Unknown " + instanceId + ">" : instance.getWarpName();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(LocationViewPanel.class, "LocationViewPanel.css")));
	}
}
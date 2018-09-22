package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.InstanceScript;
import org.icemoon.eartheternal.common.InstanceScripts;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.worldeditor.model.ZoneDefsModel;

@SuppressWarnings("serial")
public class InstanceScriptsPage extends AbstractScriptsPage<InstanceScript, Long, InstanceScripts> {
	private String zoneFilter;

	public InstanceScriptsPage() {
		super(Long.class);
	}

	@Override
	protected void buildForm(Form<InstanceScript> form) {
		super.buildForm(form);
		form.add(new SelectorPanel<Long, ZoneDef, String, IDatabase>("entityId", new Model<String>("Zone"), new ZoneDefsModel(this),
				"name", new PropertyModel<Long>(this, "selected.entityId"), ZoneDef.class, Long.class, ZoneDefsPage.class) {
			@Override
			public boolean isChangeAllowed() {
				return !editing;
			}
		}.setShowLabel(true).setShowClear(true));
	}

	@Override
	protected void addIdField() {
	}

	@Override
	protected void buildColumns(List<IColumn<InstanceScript, String>> columns) {
		super.buildColumns(columns);
		columns.add(new LinkColumn<InstanceScript, ZoneDef, Long, String, IDatabase>(new ResourceModel("column.zone"), "zone",
				new PropertyModel<String>(this, "zoneFilter"), new ZoneDefsModel(this), "entityId", "name"));
	}

	protected boolean entityMatches(InstanceScript object, InstanceScript filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		ZoneDef z = new ZoneDefsModel(this).getObject().get(object.getEntityId());
		if (z == null || Util.notMatches(z.getName(), zoneFilter)) {
			return false;
		}
		return true;
	}

	@Override
	protected InstanceScript createNewInstance() {
		return new InstanceScript(getDatabase());
	}

	@Override
	public InstanceScripts getEntityDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase().getInstanceScripts();
	}
}

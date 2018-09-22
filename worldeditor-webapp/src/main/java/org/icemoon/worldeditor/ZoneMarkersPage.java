package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.eartheternal.common.ZoneMarker;
import org.icemoon.eartheternal.common.ZoneMarkers;
import org.icemoon.eartheternal.common.ZoneMarker.Marker;
import org.icemoon.worldeditor.components.ItemBuilder;
import org.icemoon.worldeditor.components.PositionPanel;
import org.icemoon.worldeditor.model.ZoneDefsModel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class ZoneMarkersPage extends AbstractEntityPage<ZoneMarker, Long, String, ZoneMarkers, IDatabase> {
	protected static final class SanctuaryBuilder extends ItemBuilder<Marker> {
		public SanctuaryBuilder(String id) {
			super(id);
		}

		@Override
		protected void onEdit(AjaxRequestTarget target, Marker newItem) {
			// comment = newItem.getComment();
		}

		@Override
		protected void buildForm(Form<Marker> newItemForm) {
			super.buildForm(newItemForm);
			newItemForm.add(new TextField<String>("description").setRequired(true).setOutputMarkupId(true));
			newItemForm.add(new PositionPanel("location"));
			newItemForm.add(new TextField<String>("map").setOutputMarkupId(true));
		}

		@Override
		protected Marker getItem() {
			return new Marker();
		}
	}

	protected String zoneFilter;

	public ZoneMarkersPage() {
		super("entityId", Long.class);
	}

	@Override
	protected void buildForm(Form<ZoneMarker> form) {
		form.add(new SanctuaryBuilder("markers"));
		form.add(new SelectorPanel<Long, ZoneDef, String, IDatabase>("entityId", new Model<String>("Zone"), new ZoneDefsModel(this),
				"name", new PropertyModel<Long>(this, "selected.entityId"), ZoneDef.class, Long.class, ZoneDefsPage.class) {
			@Override
			public boolean isChangeAllowed() {
				return !editing;
			}
		}.setShowLabel(true).setShowClear(true));
	}

	@Override
	protected void addIdColumn(List<IColumn<ZoneMarker, String>> columns) {
	}

	@Override
	protected void addIdField() {
	}

	protected void buildColumns(List<IColumn<ZoneMarker, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<ZoneMarker, String>(new ResourceModel("column.entityId"), "entityId",
				"entityId", "entityId"));
		columns.add(new LinkColumn<ZoneMarker, ZoneDef, Long, String, IDatabase>(new ResourceModel("column.zone"), "zone",
				new PropertyModel<String>(this, "zoneFilter"), new ZoneDefsModel(this), "entityId", "name"));
	}

	protected boolean entityMatches(ZoneMarker object, ZoneMarker filter) {
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
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(ZoneMarkersPage.class, "ZoneMarkersPage.css")));
	}

	@Override
	public ZoneMarkers getEntityDatabase() {
		return getDatabase().getZoneMarkers();
	}

	@Override
	protected ZoneMarker createNewInstance() {
		return new ZoneMarker(getDatabase());
	}
}

package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.AbstractShop;
import org.icemoon.eartheternal.common.AbstractShopItem;
import org.icemoon.eartheternal.common.AbstractShops;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.SpawnKey;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.worldeditor.components.SelectionBuilder;
import org.icemoon.worldeditor.model.CreaturesModel;
import org.icemoon.worldeditor.model.ZoneDefsModel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public abstract class AbstractShopsPage<S extends AbstractShop<I>, I extends AbstractShopItem, D extends AbstractShops<S, I>> extends AbstractEntityPage<S, SpawnKey, String, D, IDatabase> {

	protected String creatureFilter;
	protected String zoneFilter;

	public AbstractShopsPage() {
		super("entityId", SpawnKey.class);
	}

	@Override
	protected void buildForm(Form<S> form) {
		form.add(new SelectorPanel<Long, ZoneDef, String, IDatabase>("zone", new Model<String>("Zone"), new ZoneDefsModel(this), "name",
				new PropertyModel<Long>(this, "selected.entityId.zone"), ZoneDef.class, Long.class, ZoneDefsPage.class) {
			@Override
			public boolean isChangeAllowed() {
				return !editing;
			}
		}.setShowLabel(true).setShowClear(true));
		form.add(new SelectorPanel<Long, Creature, String, IDatabase>("creature", new Model<String>("Creature"), new CreaturesModel(this),
				"displayName", new PropertyModel<Long>(this, "selected.entityId.creature"), Creature.class, Long.class,
				CreaturesPage.class) {
			@Override
			public boolean isChangeAllowed() {
				return !editing;
			}
		}.setShowLabel(true).setShowClear(true));
		form.add(createSelectionBuilder());
	}

	protected abstract SelectionBuilder<?,?,?,?,?,?> createSelectionBuilder();

	@Override
	protected void addIdColumn(List<IColumn<S, String>> columns) {
	}

	@Override
	protected void addIdField() {
	}

	protected void buildColumns(List<IColumn<S, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<S, String>(new ResourceModel("column.zoneId"), "entityId.zone",
				"entityId.zone", "entityId.zone"));
		columns.add(new LinkColumn<S, ZoneDef, Long, String, IDatabase>(new ResourceModel("column.zone"), "zone",
				new PropertyModel<String>(this, "zoneFilter"), new ZoneDefsModel(this), "entityId.zone", "name"));
		columns.add(new TextFilteredClassedPropertyColumn<S, String>(new ResourceModel("column.creatureId"), "entityId.creature",
				"entityId.creature", "entityId.creature"));
		columns.add(new LinkColumn<S, Creature, Long, String, IDatabase>(new ResourceModel("column.creature"), "creature",
				new PropertyModel<String>(this, "creatureFilter"), new CreaturesModel(this), "entityId.creature", "displayName"));
	}

	protected boolean entityMatches(S object, S filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		Creature c = new CreaturesModel(this).getObject().get(object.getEntityId().getCreature());
		if (c == null || Util.notMatches(c.getDisplayName(), creatureFilter)) {
			return false;
		}
		ZoneDef z = new ZoneDefsModel(this).getObject().get(object.getEntityId().getZone());
		if (z == null || Util.notMatches(z.getName(), zoneFilter)) {
			return false;
		}
		return true;
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(AbstractShopsPage.class, "AbstractShopsPage.css")));
	}

	@Override
	protected S configureFilterObject(S obj) {
		obj.setEntityId(null);
		return obj;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void select(S selected, int selectedIndex) {
		super.select(selected, selectedIndex);
		if (form != null)
			((SelectionBuilder) form.get("items")).resetItemEdit();
	}
}

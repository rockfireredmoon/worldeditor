package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.LootCreature;
import org.icemoon.eartheternal.common.LootCreatures;
import org.icemoon.eartheternal.common.LootPackage;
import org.icemoon.eartheternal.common.LootPackages;
import org.icemoon.eartheternal.common.LootSet;
import org.icemoon.eartheternal.common.LootSets;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.components.SelectionBuilder;
import org.icemoon.worldeditor.model.CreaturesModel;

@SuppressWarnings("serial")
public class LootCreaturesPage extends AbstractEntityPage<LootCreature, Long, String, LootCreatures, IDatabase> {
	private String creatureFilter;
	private String packagesFilter;

	public LootCreaturesPage() {
		super("entityId", Long.class);
	}

	public List<String> getLootPackages() {
		List<String> l = new ArrayList<String>();
		for (String s : getSelected().getPackages()) {
			if (Application.getAppSession(getRequestCycle()).getDatabase().getLootPackages().contains(s))
				l.add(s);
		}
		return l;
	}

	public void setLootPackages(List<String> lootPackages) {
		List<String> l = new ArrayList<String>(getSelected().getPackages());
		for (String s : getSelected().getPackages()) {
			if (Application.getAppSession(getRequestCycle()).getDatabase().getLootPackages().contains(s))
				l.remove(s);
		}
		l.addAll(lootPackages);
		getSelected().setPackages(l);
	}

	public List<String> getLootSets() {
		List<String> l = new ArrayList<String>();
		for (String s : getSelected().getPackages()) {
			if (Application.getAppSession(getRequestCycle()).getDatabase().getLootSets().contains(s))
				l.add(s);
		}
		return l;
	}

	public void setLootSets(List<String> lootSets) {
		List<String> l = new ArrayList<String>(getSelected().getPackages());
		for (String s : getSelected().getPackages()) {
			if (Application.getAppSession(getRequestCycle()).getDatabase().getLootSets().contains(s))
				l.remove(s);
		}
		l.addAll(lootSets);
		getSelected().setPackages(l);
	}

	@Override
	protected void buildForm(final Form<LootCreature> form) {
		form.add(new CheckBox("explicit"));
		form.add(new SelectionBuilder<String, LootSet, String, LootSets, String, IDatabase>("lootSets",
				new PropertyModel<List<String>>(this, "lootSets"), String.class, LootSet.class, LootSetsPage.class,
				new Model<LootSets>() {
					@Override
					public LootSets getObject() {
						return Application.getAppSession(getRequestCycle()).getDatabase().getLootSets();
					}
				}, "entityId"));
		form.add(new SelectionBuilder<String, LootPackage, String, LootPackages, String, IDatabase>("lootPackages",
				new PropertyModel<List<String>>(this, "lootPackages"), String.class, LootPackage.class, LootPackagesPage.class,
				new Model<LootPackages>() {
					@Override
					public LootPackages getObject() {
						return Application.getAppSession(getRequestCycle()).getDatabase().getLootPackages();
					}
				}, "entityId"));
		form.add(new SelectorPanel<Long, Creature, String, IDatabase>("creature", new Model<String>("Creature"),
				new CreaturesModel(this), "displayName", new PropertyModel<Long>(this, "selected.creature"), Creature.class,
				Long.class, CreaturesPage.class).setShowLabel(true));
		form.add(new TextArea<String>("comment"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void select(LootCreature selected, int selectedIndex) {
		super.select(selected, selectedIndex);
		if (form != null) {
			((SelectionBuilder) form.get("lootPackages")).resetItemEdit();
			((SelectionBuilder) form.get("lootSets")).resetItemEdit();
		}
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(LootCreaturesPage.class, "LootPackagesPage.css")));
		super.onRenderEntityHead(response);
	}

	@Override
	protected boolean entityMatches(LootCreature object, LootCreature filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		Creature c = new CreaturesModel(this).getObject().get(object.getCreature());
		if (c == null || Util.notMatches(c.getDisplayName(), creatureFilter)) {
			return false;
		}
		if (Util.notMatches(Util.toCommaSeparatedList(object.getPackages()), packagesFilter)) {
			return false;
		}
		return true;
	}

	@Override
	protected void addIdField() {
	}

	@Override
	protected void addIdColumn(List<IColumn<LootCreature, String>> columns) {
	}

	@Override
	protected LootCreature createNewInstance() {
		return new LootCreature(getDatabase());
	}

	@Override
	public LootCreatures getEntityDatabase() {
		return getDatabase().getLootCreatures();
	}

	@Override
	protected void buildColumns(List<IColumn<LootCreature, String>> columns) {
		columns.add(
				new LinkColumn<LootCreature, Creature, Long, String, IDatabase>(new ResourceModel("column.creature"), "creature",
						new PropertyModel<String>(this, "creatureFilter"), new CreaturesModel(this), "creature", "displayName"));
		columns.add(new PackageColumn(new ResourceModel("column.packages"), "packages",
				new PropertyModel<String>(this, "packagesFilter")));
	}

	static final class PackageColumn extends FilteredAbstractColumn<LootCreature, String> {
		private IModel<String> filterModel;

		PackageColumn(IModel<String> displayModel, String sortProperty, IModel<String> filterModel) {
			super(displayModel, sortProperty);
			this.filterModel = filterModel;
		}

		@Override
		public void populateItem(Item<ICellPopulator<LootCreature>> cellItem, String componentId,
				final IModel<LootCreature> rowModel) {
			cellItem.add(new Label(componentId, new Model<String>() {
				@Override
				public String getObject() {
					return Util.limit(Util.toCommaSeparatedList(rowModel.getObject().getPackages()), 30);
				}
			}));
		}

		@Override
		public Component getFilter(String componentId, final FilterForm<?> form) {
			return new TextFilter<String>(componentId, filterModel, form);
		}
	}
}

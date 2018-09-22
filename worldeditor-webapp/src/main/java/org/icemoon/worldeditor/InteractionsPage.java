package org.icemoon.worldeditor;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.InteractDef;
import org.icemoon.eartheternal.common.InteractDefs;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.eartheternal.common.InteractDef.Classification;
import org.icemoon.eartheternal.common.InteractDef.Type;
import org.icemoon.worldeditor.components.CoinPanel;
import org.icemoon.worldeditor.components.PositionPanel;
import org.icemoon.worldeditor.model.CreaturesModel;
import org.icemoon.worldeditor.model.QuestsModel;
import org.icemoon.worldeditor.model.ZoneDefsModel;
import org.icemoon.worldeditor.table.ClassedPropertyColumn;

@SuppressWarnings("serial")
public class InteractionsPage extends AbstractEntityPage<InteractDef, String, String, InteractDefs, IDatabase> {
	public InteractionsPage() {
		super("entityId", String.class);
	}

	@Override
	protected void buildForm(final Form<InteractDef> form) {
		final PositionPanel warpToLocation = new PositionPanel("warpToLocation");
		warpToLocation.setOutputMarkupId(true);
		form.add(warpToLocation.setRequired(true));
		form.add(new TextField<String>("message"));
		form.add(new TextField<String>("scriptFunction"));
		form.add(new CheckBox("henge"));
		form.add(new CoinPanel("cost"));
		form.add(new TextField<Integer>("useTime", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE))
				.setRequired(true));
		form.add(new TextField<Integer>("facing", Integer.class).add(new RangeValidator<Integer>(0, 360)).setRequired(true));
		form.add(new SelectorPanel<Long, ZoneDef, String, IDatabase>("zone", new Model<String>("Zone"), new ZoneDefsModel(this), "name",
				new PropertyModel<Long>(this, "selected.zone"), ZoneDef.class, Long.class, ZoneDefsPage.class).setShowLabel(true)
						.setShowClear(true));
		form.add(new SelectorPanel<Long, ZoneDef, String, IDatabase>("instanceId", new Model<String>("Target Instance"),
				new ZoneDefsModel(this), "name", new PropertyModel<Long>(this, "selected.instanceId"), ZoneDef.class, Long.class,
				ZoneDefsPage.class) {
			protected void onEntitySelected(AjaxRequestTarget target, ZoneDef entity) {
				getSelected().setWarpToLocation(entity.getLocation().toPosition());
				warpToLocation.setModelObject(entity.getLocation().toPosition());
				target.add(this);
				target.add(warpToLocation);
			}
		}.setShowLabel(true).setShowClear(true));
		form.add(new SelectorPanel<Long, Quest, String, IDatabase>("quest", new Model<String>("Qyest"), new QuestsModel(this), "title",
				new PropertyModel<Long>(this, "selected.quest"), Quest.class, Long.class, QuestsPage.class) {
			protected void onEntitySelected(AjaxRequestTarget target, Quest entity) {
				// getSelected().setQuest(entity.getEntityId());
			}
		}.setShowLabel(true).setShowClear(true));
		form.add(new CheckBox("questComplete"));
		form.add(new SelectorPanel<Long, Creature, String, IDatabase>("objectId", new Model<String>("Object"), new CreaturesModel(this),
				"displayName", Creature.class, Long.class, CreaturePage.class).setShowClear(true).setShowLabel(true));
		form.add(new ListChoice<Classification>("classification", Arrays.asList(Classification.values())).setMaxRows(1)
				.setRequired(true));
		form.add(new ListChoice<Type>("type", Arrays.asList(Type.values())).setMaxRows(1).setRequired(true));
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(InteractionsPage.class, "InteractionsPage.css")));
	}

	@Override
	protected InteractDef createNewInstance() {
		return new InteractDef(getDatabase());
	}

	@Override
	public InteractDefs getEntityDatabase() {
		return getDatabase().getInteractions();
	}

	@Override
	protected void buildColumns(List<IColumn<InteractDef, String>> columns) {
		columns.add(new ClassedPropertyColumn<InteractDef>(new ResourceModel("column.type"), "type", "type", "type"));
		columns.add(new ClassedPropertyColumn<InteractDef>(new ResourceModel("column.classification"), "classification",
				"classification", "classification"));
	}
}

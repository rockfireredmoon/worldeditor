package org.icemoon.worldeditor;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.BaseCreature;
import org.icemoon.eartheternal.common.BaseCreatures;
import org.icemoon.eartheternal.common.CreatureCategory;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.eartheternal.common.Profession;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.dialogs.AppearanceEditorDialog;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public abstract class AbstractCreaturesPage<T extends BaseCreature, D extends BaseCreatures<T, R>, R extends IRoot> extends AbstractEntityPage<T, Long, String, D, R> {

	public AbstractCreaturesPage() {
		super("displayName", Long.class);
	}
	
	@Override
	protected final void onEdit() {
		((AppearanceEditorDialog)form.get("appearance")).reset();
	}
	
	@Override
	protected final void onNew() {
		((AppearanceEditorDialog)form.get("appearance")).reset();
	}

	@Override
	protected final void buildForm(Form<T> form) {
		form.add(new TextField<String>("displayName").setRequired(true).setLabel(new Model<String>("displayName")));
		form.add(new TextField<Integer>("level", Integer.class).setRequired(true).add(
			new RangeValidator<Integer>(1, Integer.MAX_VALUE)));

		form.add(new ListChoice<Profession>("profession", Arrays.asList(Profession.values())).setMaxRows(1).setNullValid(true));
		form.add(new ListChoice<CreatureCategory>("creatureCategory", Arrays.asList(CreatureCategory.values())).setMaxRows(1)
			.setNullValid(true));

		form.add(new TextField<Integer>("dexterity", Integer.class).setRequired(true).add(
			new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("constitution", Integer.class).setRequired(true).add(
			new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("psyche", Integer.class).setRequired(true).add(
			new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("spirit", Integer.class).setRequired(true).add(
			new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("strength", Integer.class).setRequired(true).add(
			new RangeValidator<Integer>(0, Integer.MAX_VALUE)));

		form.add(new TextField<Integer>("offhandWeaponDamage", Integer.class)
			.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("castingSetbackChance", Integer.class)
			.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("channelingBreakChance", Integer.class).add(new RangeValidator<Integer>(0,
			Integer.MAX_VALUE)));
		
		form.add(new TextField<Integer>("damageResistMelee", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("damageResistFire", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("damageResistFrost", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("damageResistMystic", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("damageResistDeath", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));

		form.add(new TextField<Float>("willRegen").add(new RangeValidator<Float>(0f, Float.MAX_VALUE)));
		form.add(new TextField<Float>("mightRegen").add(new RangeValidator<Float>(0f, Float.MAX_VALUE)));

		form.add(new AppearanceEditorDialog("appearance"));
		
		onBuildForm(form);
	}

	protected abstract void onBuildForm(Form<T> form);

	@Override
	protected void onInitialize() {
		super.onInitialize();
	}

	@Override
	protected boolean entityMatches(T object, T filter) {
		if(!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getDisplayName(), filter.getDisplayName())) {
			return false;
		}
		if (filter.getLevel() > 0 && object.getLevel() != filter.getLevel()) {
			return false;
		}
		return true;
	}

	@Override
	protected final void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(AbstractCreaturesPage.class, "jquery.qtip.css")));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(AbstractCreaturesPage.class, "jquery.qtip.js")));
		onRenderCreatureHead(response);
	}

	protected void onRenderCreatureHead(IHeaderResponse response) {
	}

	@Override
	protected final void buildColumns(List<IColumn<T, String>> columns) {
		columns.add(new AbstractColumn<T, String>(new Model<String>("")) {
			public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> model) {
				Profession profession = model.getObject().getProfession();
				IModel<ResourceReference> ref = new Model<ResourceReference>();
				if (profession != null) {
					ref.setObject(new PackageResourceReference(AbstractCreaturesPage.class, profession.name().toLowerCase() + ".png"));
					cellItem.add(new AttributeAppender("class", "profession " + profession.name().toLowerCase()));
				} else {
					cellItem.add(new AttributeModifier("class", "profession"));
				}
				cellItem.add(new ImagePanel(componentId, ref));
			}
		});
		columns.add(new TextFilteredClassedPropertyColumn<T, String>(new ResourceModel("displayName"), "displayName",
			"displayName", "displayName"));
		columns
			.add(new TextFilteredClassedPropertyColumn<T, Integer>(new ResourceModel("level"), "level", "level", "level"));
		onBuildColumns(columns);
	}

	protected abstract void onBuildColumns(List<IColumn<T, String>> columns);

	class ImagePanel extends Panel {
		public ImagePanel(String id, IModel<ResourceReference> model) {
			super(id, model);
			add(new Image("image", model));
		}
	}
}

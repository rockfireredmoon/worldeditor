package org.icemoon.worldeditor;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.BipedAnimation;
import org.icemoon.eartheternal.common.DropProfile;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.eartheternal.common.ZoneDefs;
import org.icemoon.eartheternal.common.Dialog.DialogParagraph;
import org.icemoon.eartheternal.common.Dialog.ParagraphType;
import org.icemoon.eartheternal.common.ZoneDef.PVPMode;
import org.icemoon.eartheternal.common.ZoneDef.TimeOfDay;
import org.icemoon.worldeditor.components.EntityActionsPanel;
import org.icemoon.worldeditor.components.XYZPanel;
import org.icemoon.worldeditor.model.DropProfilesModel;
import org.icemoon.worldeditor.model.UniqueListModel;
import org.icemoon.worldeditor.model.ZoneDefListModel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class ZoneDefsPage extends AbstractEntityPage<ZoneDef, Long, String, ZoneDefs, IDatabase> {
	private final class ZoneDefsActions extends EntityActionsPanel<ZoneDef> {
		private ZoneDefsActions(IModel<ZoneDef> model) {
			super(model);
		}

		@Override
		protected void onNew() {
			doCreateNew();
			ZoneDefsPage.this.onNew();
		}

		@Override
		protected void addAdditionalActions(Form<?> form) {
			super.addAdditionalActions(form);
			form.add(new AjaxButton("spawns") {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					setResponsePage(SceneryPage.class, new PageParameters().add("zoneId", getSelected().getEntityId()));
				}

				@Override
				public boolean isEnabled() {
					return isEditing();
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
				}
			});
		}
	}

	public ZoneDefsPage() {
		super("entityId", Long.class);
	}

	@Override
	protected void buildForm(Form<ZoneDef> form) {
		addIdType(form);
		form.add(new TextField<String>("name").setRequired(true));
		// form.add(new TextField<String>("environmentType"));
		final FixedAutocompleteComponent<String> environmentType = new FixedAutocompleteComponent<String>("environmentType",
				new PropertyModel<String>(this, "selected.environmentType"), new UniqueListModel<String, ZoneDef>(
						new ZoneDefListModel(new PropertyModel<IDatabase>(this, "database")), "environmentType")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		};
		form.add(new DropDownChoice<TimeOfDay>("timeOfDay", Arrays.asList(TimeOfDay.values())).setNullValid(true));
		form.add(environmentType);
		form.add(new TextField<String>("description"));
		form.add(new TextField<String>("mapName"));
		form.add(new TextField<String>("terrainConfig"));
		form.add(new TextField<String>("regions"));
		form.add(new SelectorPanel<String, DropProfile, String, IDatabase>("dropRateProfile", new Model<String>("Drop Profile"),
				new DropProfilesModel(this), "entityId", new PropertyModel<String>(this, "selected.dropRateProfile"),
				DropProfile.class, String.class, null).setShowLabel(true).setShowClear(true));
		form.add(new ListChoice<PVPMode>("mode", Arrays.asList(PVPMode.values())).setMaxRows(1).setRequired(false));
		form.add(new TextArea<String>("areaEnvironment"));
		form.add(new TextField<Integer>("pageSize", Integer.class).add(new RangeValidator<Integer>(1, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("maxAggroRange", Integer.class).add(new RangeValidator<Integer>(1, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("maxLeashRange", Integer.class).add(new RangeValidator<Integer>(1, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("minLevel", Integer.class).add(new RangeValidator<Integer>(0, 9999)));
		form.add(new TextField<Integer>("maxLevel", Integer.class).add(new RangeValidator<Integer>(0, 9999)));
		form.add(new XYZPanel("location"));
		form.add(new TextField<String>("warpName"));
		final FixedAutocompleteComponent<String> shardName = new FixedAutocompleteComponent<String>("shardName",
				new PropertyModel<String>(this, "selected.shardName"), new UniqueListModel<String, ZoneDef>(
						new ZoneDefListModel(new PropertyModel<IDatabase>(this, "database")), "shardName")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		};
		form.add(shardName);
		form.add(new CheckBox("persist"));
		form.add(new CheckBox("audit"));
		form.add(new CheckBox("grove"));
		form.add(new CheckBox("arena"));
		form.add(new CheckBox("guildHall"));
		form.add(new CheckBox("instance"));
		form.add(new CheckBox("environmentCycle"));
	}

	protected void buildColumns(List<IColumn<ZoneDef, String>> columns) {
		columns.add(
				new TextFilteredClassedPropertyColumn<ZoneDef, String>(new ResourceModel("column.name"), "name", "name", "name"));
		columns.add(new TextFilteredClassedPropertyColumn<ZoneDef, String>(new ResourceModel("column.environment"),
				"environmentType", "environmentType", "environmentType"));
		columns.add(new TextFilteredClassedPropertyColumn<ZoneDef, String>(new ResourceModel("column.warpName"), "warpName",
				"warpName", "warpName"));
	}

	protected EntityActionsPanel<ZoneDef> createActionsPanel() {
		return new ZoneDefsActions(getSelectedModel());
	}

	protected boolean entityMatches(ZoneDef object, ZoneDef filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getName(), filter.getName())) {
			return false;
		}
		if (Util.notMatches(object.getEnvironmentType(), filter.getEnvironmentType())) {
			return false;
		}
		if (Util.notMatches(object.getWarpName(), filter.getWarpName())) {
			return false;
		}
		if (Util.notMatches(object.getShardName(), filter.getShardName())) {
			return false;
		}
		return true;
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(ZoneDefsPage.class, "ZoneDefsPage.css")));
	}

	@Override
	protected ZoneDef createNewInstance() {
		return new ZoneDef(getDatabase());
	}

	@Override
	public ZoneDefs getEntityDatabase() {
		return getDatabase().getZoneDefs();
	}
}

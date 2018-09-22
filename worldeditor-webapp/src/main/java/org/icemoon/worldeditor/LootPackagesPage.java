package org.icemoon.worldeditor;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.LootPackage;
import org.icemoon.eartheternal.common.LootPackages;
import org.icemoon.eartheternal.common.LootSet;
import org.icemoon.eartheternal.common.LootSets;
import org.icemoon.worldeditor.components.SelectionBuilder;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class LootPackagesPage extends AbstractEntityPage<LootPackage, String, String, LootPackages, IDatabase> {
	public LootPackagesPage() {
		super("entityId", String.class);
	}

	@Override
	protected void buildForm(final Form<LootPackage> form) {
		form.add(new TextField<Integer>("auto"));
		form.add(new TextField<String>("flags").setRequired(true).add(new IValidator<String>() {
			@Override
			public void validate(IValidatable<String> validatable) {
				if (validatable.getValue().equals("*"))
					return;
				else if (validatable.getValue().startsWith("+") || validatable.getValue().startsWith("-")) {
					String spec = validatable.getValue().substring(1);
					if (spec.length() == 0) {
						validatable.error(new IValidationError() {
							@Override
							public Serializable getErrorMessage(IErrorMessageSource messageSource) {
								return "Flag spec must follow + and -";
							}
						});
						return;
					} else {
						final String valid = "aAnNhHeElL";
						for (char c : spec.toCharArray()) {
							if (!valid.contains(String.valueOf(c))) {
								validatable.error(new IValidationError() {
									@Override
									public Serializable getErrorMessage(IErrorMessageSource messageSource) {
										return "Flag spec must be one or more of " + valid;
									}
								});
								return;
							}
						}
					}
				} else {
					validatable.error(new IValidationError() {
						@Override
						public Serializable getErrorMessage(IErrorMessageSource messageSource) {
							return "Flags must be either *, or start with + or - (followed by types)";
						}
					});
				}
			}
		}));
		form.add(new SelectionBuilder<String, LootSet, String, LootSets, Long, IDatabase>("sets", String.class, LootSet.class, LootSetsPage.class,
				new Model<LootSets>() {
					@Override
					public LootSets getObject() {
						return Application.getAppSession(getRequestCycle()).getDatabase().getLootSets();
					}
				}, "entityId"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void select(LootPackage selected, int selectedIndex) {
		super.select(selected, selectedIndex);
		if (form != null)
			((SelectionBuilder) form.get("sets")).resetItemEdit();
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(LootPackagesPage.class, "LootPackagesPage.css")));
		super.onRenderEntityHead(response);
	}

	@Override
	protected LootPackage createNewInstance() {
		return new LootPackage(getDatabase());
	}

	@Override
	public LootPackages getEntityDatabase() {
		return getDatabase().getLootPackages();
	}

	@Override
	protected void buildColumns(List<IColumn<LootPackage, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<LootPackage, String>(new ResourceModel("column.auto"), "auto", "auto",
				"auto"));
	}
}

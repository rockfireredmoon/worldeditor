package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.DropProfile;
import org.icemoon.eartheternal.common.DropProfiles;
import org.icemoon.eartheternal.common.IDatabase;

@SuppressWarnings("serial")
public class DropProfilesPage extends AbstractEntityPage<DropProfile, String, String, DropProfiles, IDatabase> {
	public DropProfilesPage() {
		super("entityId", String.class);
	}

	@Override
	protected void buildForm(Form<DropProfile> form) {
		form.add(new TextField<Integer>("qL0None", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("qL1Norm", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("qL2Unc", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("qL3Rare", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("qL4Epic", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("qL5Leg", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("qL6Art", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("qL0NoneL", Integer.class).add(new RangeValidator<Integer>(-1, 999)));
		form.add(new TextField<Integer>("qL1NormL", Integer.class).add(new RangeValidator<Integer>(-1, 999)));
		form.add(new TextField<Integer>("qL2UncL", Integer.class).add(new RangeValidator<Integer>(-1, 999)));
		form.add(new TextField<Integer>("qL3RareL", Integer.class).add(new RangeValidator<Integer>(-1, 999)));
		form.add(new TextField<Integer>("qL4EpicL", Integer.class).add(new RangeValidator<Integer>(-1, 999)));
		form.add(new TextField<Integer>("qL5LegL", Integer.class).add(new RangeValidator<Integer>(-1, 999)));
		form.add(new TextField<Integer>("qL6ArtL", Integer.class).add(new RangeValidator<Integer>(-1, 999)));
		form.add(new TextField<String>("qL0NoneF", String.class).setRequired(true));
		form.add(new TextField<String>("qL1NormF", String.class).setRequired(true));
		form.add(new TextField<String>("qL2UncF", String.class).setRequired(true));
		form.add(new TextField<String>("qL3RareF", String.class).setRequired(true));
		form.add(new TextField<String>("qL4EpicF", String.class).setRequired(true));
		form.add(new TextField<String>("qL5LegF", String.class).setRequired(true));
		form.add(new TextField<String>("qL6ArtF", String.class).setRequired(true));
		form.add(new TextField<Integer>("a0", Integer.class).add(new RangeValidator<Integer>(-1, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("a1", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("a2", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("a3", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("a4", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("a5", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("a6", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(DropProfilesPage.class, "DropProfilesPage.css")));
	}

	@Override
	protected DropProfile createNewInstance() {
		final DropProfile book = new DropProfile(getDatabase());
		return book;
	}

	@Override
	public DropProfiles getEntityDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase().getDropProfiles();
	}

	@Override
	protected void buildColumns(List<IColumn<DropProfile, String>> columns) {
	}
}

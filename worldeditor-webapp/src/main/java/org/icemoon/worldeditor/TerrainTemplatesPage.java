package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.TerrainTemplate;
import org.icemoon.eartheternal.common.TerrainTemplates;

@SuppressWarnings("serial")
public class TerrainTemplatesPage extends AbstractEntityPage<TerrainTemplate, String, String, TerrainTemplates, IDatabase> {
	public TerrainTemplatesPage() {
		super("entityId", String.class);
	}

	@Override
	protected void buildForm(Form<TerrainTemplate> form) {
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(TerrainTemplatesPage.class, "DropProfilesPage.css")));
	}

	@Override
	protected TerrainTemplate createNewInstance() {
		final TerrainTemplate book = new TerrainTemplate(getDatabase());
		return book;
	}

	@Override
	public TerrainTemplates getEntityDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase().getTerrainTemplates();
	}

	@Override
	protected void buildColumns(List<IColumn<TerrainTemplate, String>> columns) {
	}
}

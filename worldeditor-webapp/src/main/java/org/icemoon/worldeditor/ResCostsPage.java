package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.ResCost;
import org.icemoon.eartheternal.common.ResCosts;
import org.icemoon.worldeditor.components.CoinPanel;
import org.icemoon.worldeditor.table.ClassedPropertyColumn;

@SuppressWarnings("serial")
public class ResCostsPage extends AbstractEntityPage<ResCost, Integer, String, ResCosts, IDatabase> {
	public ResCostsPage() {
		super("entityId", Integer.class);
	}

	@Override
	protected void buildForm(Form<ResCost> form) {
		form.add(new CoinPanel("resurrect"));
		form.add(new CoinPanel("rebirth"));
	}

	protected void buildColumns(List<IColumn<ResCost, String>> columns) {
		columns.add(
				new ClassedPropertyColumn<ResCost>(new ResourceModel("column.resurrect"), "resurrect", "resurrect", "resurrect"));
		columns.add(new ClassedPropertyColumn<ResCost>(new ResourceModel("column.rebirth"), "rebirth", "rebirth", "rebirth"));
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(ResCostsPage.class, "ResCostsPage.css")));
	}

	@Override
	protected ResCost createNewInstance() {
		return new ResCost(getDatabase());
	}

	@Override
	public ResCosts getEntityDatabase() {
		return getDatabase().getResCosts();
	}
}

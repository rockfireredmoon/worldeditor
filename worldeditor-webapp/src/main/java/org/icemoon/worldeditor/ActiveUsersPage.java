package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.entities.ActiveUser;
import org.icemoon.worldeditor.entities.ActiveUsers;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class ActiveUsersPage extends AbstractEntityPage<ActiveUser, String, String, ActiveUsers, IRoot> {
	public ActiveUsersPage() {
		super("entityId", String.class);
		setNeedsStore(false);
	}

	@Override
	protected void onNew() {
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
	}

	@Override
	protected void buildForm(Form<ActiveUser> form) {
		form.add(new Label("user", new PropertyModel<String>(this, "selected.user.name")));
		form.add(new Label("pageName", new Model<String>() {
			@Override
			public String getObject() {
				return getSelected().getPage() == null ? "None" : getSelected().getPage().getSimpleName();
			}
		}));
	}

	protected void buildColumns(List<IColumn<ActiveUser, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<ActiveUser, String>(new ResourceModel("column.user"), "user", "user.name",
				"user"));
	}

	protected boolean entityMatches(ActiveUser object, ActiveUser filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getUser(), filter.getUser())) {
			return false;
		}
		return true;
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(ActiveUsersPage.class, "DatabaseStoresPage.css")));
	}

	protected ActiveUser createNewInstance() {
		return new ActiveUser(getDatabase());
	}

	@Override
	public ActiveUsers getEntityDatabase() {
		return Application.getApp().getActiveUsers();
	}
}

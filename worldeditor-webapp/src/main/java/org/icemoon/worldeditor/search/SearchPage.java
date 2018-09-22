package org.icemoon.worldeditor.search;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.worldeditor.player.AbstractUserPage;

@SuppressWarnings("serial")
public class SearchPage extends AbstractUserPage {

	private String searchText;

	@Override
	protected void onInitialize() {
		super.onInitialize();

		Form<?> form = new Form<Object>("searchForm");
		form.add(new TextField<String>("searchText", new PropertyModel<String>(this, "searchText")).setRequired(true));
		form.add(new Button("search") {
			@Override
			public void onSubmit() {
				setResponsePage(DoSearchPage.class, new PageParameters().add("searchText", searchText));
			}
		});
		form.add(new Button("lucky") {
			@Override
			public void onSubmit() {
				setResponsePage(DoSearchPage.class, new PageParameters().add("lucky", true).add("searchText", searchText));
			}
		});
		add(form);

	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(SearchPage.class, "SearchPage.css")));
	}
}

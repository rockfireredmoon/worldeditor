package org.icemoon.worldeditor.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.player.AbstractUserPage;
import org.icemoon.worldeditor.search.SearchResult.ResultType;

@SuppressWarnings("serial")
public class DoSearchPage extends AbstractUserPage {
	private String searchText;
	private boolean lucky;
	private SearchResult singleResult;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		searchText = getPageParameters().get("searchText").toString();
		lucky = getPageParameters().get("lucky").toBoolean();
		Form<?> form = new Form<Object>("searchForm");
		form.add(new TextField<String>("searchText", new PropertyModel<String>(this, "searchText")).setRequired(true));
		form.add(new Button("search") {
			@Override
			public void onSubmit() {
				setResponsePage(DoSearchPage.class, new PageParameters().add("searchText", searchText));
			}
		});
		add(form);
		// Results
		ListView<SearchResult> results = new ListView<SearchResult>("results",
				new PropertyModel<List<SearchResult>>(this, "results")) {
			@Override
			protected void populateItem(ListItem<SearchResult> item) {
				final SearchResult result = item.getModelObject();
				Link<String> link = new Link<String>("activate") {
					@Override
					public void onClick() {
						result.activate(this);
					}
				};
				link.add(new Label("displayName", new Model<String>(result.getDisplayName())));
				item.add(new NonCachingImage("image", result.getImage()));
				item.add(new Label("resultType", new Model<ResultType>(result.getResultType())));
				item.add(link);
				item.add(new Label("description", new Model<String>(Util.trimDisplay(result.getDescription(), 255))));
				item.add(new Label("subtext", new Model<String>(result.getSubtext())));
			}
		};
		add(results);
	}

	@Override
	protected void onAfterRender() {
		super.onAfterRender();
		if (lucky && singleResult != null) {
			singleResult.activate(this);
		}
	}

	public List<SearchResult> getResults() {
		List<SearchResult> res = new ArrayList<SearchResult>();
		singleResult = null;
		IModel<IDatabase> database = new PropertyModel<IDatabase>(this, "database");
		for (SearchSource s : new SearchSource[] { 
				new CharacterSearchSource(new PropertyModel<IUserData>(this, "userData")), 
				new CreatureSearchSource(database),
				new QuestSearchSource(database) }) {
			for (Iterator<SearchResult> it = s.search(searchText); it.hasNext();) {
				final SearchResult r = it.next();
				res.add(r);
				if (lucky) {
					singleResult = r;
					break;
				}
			}
			if (singleResult != null && lucky) {
				break;
			}
		}
		Collections.sort(res);
		return res;
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(DoSearchPage.class, "DoSearchPage.css")));
	}
}

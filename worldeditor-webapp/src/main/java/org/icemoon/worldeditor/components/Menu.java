package org.icemoon.worldeditor.components;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.worldeditor.Place;

@SuppressWarnings("serial")
public class Menu extends Panel {
	private boolean root;

	public Menu(final String id, IModel<List<Place>> model) {
		this(id, model, true);
	}

	protected Menu(final String id, IModel<List<Place>> model, boolean root) {
		super(id, model);
		this.root = root;
		if (root) {
			add(new AttributeAppender("class", "rootMenu"));
		} else {
			add(new AttributeAppender("class", "subMenu ui-helper-reset ui-widget ui-widget-content"));
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(Menu.class, "Menu.css")));
	}

	@SuppressWarnings("unchecked")
	public IModel<List<Place>> getModel() {
		return (IModel<List<Place>>) getDefaultModel();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new ListView<Place>("mainNavigationLink", getModel()) {
			@Override
			protected void populateItem(ListItem<Place> item) {
				final Place modelObject = item.getModelObject();
				MarkupContainer link = null;
				if (modelObject.getPage() != null || modelObject.getPlaces().isEmpty()) {
					if (modelObject.getPage().equals(getPage().getClass())) {
						item.add(new AttributeAppender("class", new Model<String>("selected")));
					}
					link = new BookmarkablePageLink<String>("navLink", modelObject.getPage());
					item.add(new WebMarkupContainer("children").setVisible(false));
				} else {
					final Menu menu = new Menu("children", new ListModel<Place>(modelObject.getPlaces()), false);
					menu.setOutputMarkupId(true);
					link = new AjaxLink<String>("navLink", new PropertyModel<String>(menu, "markupId")) {
						@Override
						public void onClick(AjaxRequestTarget target) {
							target.appendJavaScript(String.format("$('#%s').fadeIn();", getModelObject()));
						}
					};
					link.add(new AttributeAppender("onblur", new Model<String>() {
						@Override
						public String getObject() {
							return String.format("$('#%s').fadeOut();", menu.getMarkupId());
						}
					}));
					item.add(menu);
				}
				item.add(link);
				link.add(new Label("navLabel", modelObject.getText()));
			}
		});
	}
}
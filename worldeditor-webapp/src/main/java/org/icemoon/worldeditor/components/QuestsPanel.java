package org.icemoon.worldeditor.components;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.worldeditor.QuestPage;

@SuppressWarnings("serial")
public class QuestsPanel extends Panel {
	public QuestsPanel(final IModel<IDatabase> database, IModel<? extends List<Long>> ids) {
		super("questsPanel");
		final Form<Object> questsForm = new Form<Object>("questsForm");
		final WebMarkupContainer questsContainer = new WebMarkupContainer("questsContainer");
		questsContainer.setOutputMarkupId(true);
		final ListView<Long> questsList = new ListView<Long>("quests", ids) {
			@Override
			protected void populateItem(final ListItem<Long> item) {
				final Quest quest = database.getObject().getQuests().get(item.getModelObject());
				final Link<String> viewLink = new Link<String>("viewQuest") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("id", quest.getEntityId());
						setResponsePage(QuestPage.class, params);
					}
				};
				viewLink.add(new Label("questName", new Model<String>() {
					@Override
					public String getObject() {
						return quest == null ? "<Unknown " + item.getModelObject() + ">" : quest.getTitle();
					}
				}));
				item.add(viewLink);
				item.add(new Label("partySize", new Model<Integer>() {
					@Override
					public Integer getObject() {
						return quest == null ? 0 : quest.getPartySize();
					}
				}));
				item.add(new Label("level", new Model<Integer>() {
					@Override
					public Integer getObject() {
						return quest == null ? 0 : quest.getLevel();
					}
				}));
			}
		};
		questsList.setReuseItems(false);
		questsContainer.add(questsList);
		questsForm.add(questsContainer);
		add(questsForm);
	}
}

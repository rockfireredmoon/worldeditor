package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Reward;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.ItemsPage;
import org.icemoon.worldeditor.model.GameItemIconModel;

@SuppressWarnings("serial")
public class RewardViewPanel extends Panel {
	private IModel<IDatabase> database;

	public RewardViewPanel(final String id, IModel<Reward> model, IModel<IDatabase> database) {
		super(id, model);
		this.database = database;
	}

	@Override
	protected void onInitialize() {
		final PropertyModel<Long> itemModel = new PropertyModel<Long>(this, "modelObject.itemId");
		add(new GameIconPanel("rewardIcon", new GameItemIconModel(itemModel, "icon1", database), new GameItemIconModel(itemModel, "icon2", database),
				24));
		Link<String> l = new Link<String>("viewReward") {
			@Override
			public void onClick() {
				setResponsePage(ItemsPage.class,
						new PageParameters().add("id", RewardViewPanel.this.getModelObject().getEntityId()));
			}
		};
		l.add(new Label("rewardName", new Model<String>() {
			public String getObject() {
				final Reward reward = getModelObject();
				if (reward == null) {
					return null;
				}
				GameItem item = reward == null ? null
						: Application.getAppSession(getRequestCycle()).getDatabase().getItems().get(reward.getItemId());
				return item == null ? "<Unknown " + reward.getItemId() + ">" : item.getDisplayName();
			}
		}));
		add(l);
		super.onInitialize();
	}

	@SuppressWarnings("unchecked")
	public IModel<Reward> getModel() {
		return (IModel<Reward>) getDefaultModel();
	}

	public Reward getModelObject() {
		return getModel().getObject();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(RewardViewPanel.class, "RewardViewPanel.css")));
	}
}
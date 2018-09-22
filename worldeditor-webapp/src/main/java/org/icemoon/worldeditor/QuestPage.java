package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.lang.Bytes;
import org.icemoon.eartheternal.common.Act;
import org.icemoon.eartheternal.common.Attachment;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.MapPoint;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.eartheternal.common.Objective;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.eartheternal.common.Reward;
import org.icemoon.worldeditor.components.CoinViewPanel;
import org.icemoon.worldeditor.components.MapPanel;
import org.icemoon.worldeditor.components.RewardViewPanel;
import org.icemoon.worldeditor.components.UploadPanel;
import org.icemoon.worldeditor.model.EntityAvatarModel;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.odlabs.wiquery.ui.tabs.Tabs;

@SuppressWarnings("serial")
public class QuestPage extends AbstractAuthenticatedPage {
	protected Tabs tabs;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		// Session
		setDefaultModel(new Model<Quest>() {
			public Quest getObject() {
				return getDatabase().getQuests().get(getPageParameters().get("id").toLong());
			}
		});
		// Tabs
		tabs = new Tabs("tabs");
		addQuest();
		addSummary();
		addRewards();
		add(tabs);
	}

	protected void addSummary() {
		// Requires
		final Link<String> requiresLink = new Link<String>("requiresLink") {
			@Override
			public void onClick() {
				setResponsePage(QuestPage.class, new PageParameters().add("id", QuestPage.this.getModelObject().getRequires()));
			}

			public boolean isVisible() {
				final Quest modelObject = QuestPage.this.getModelObject();
				return modelObject != null && modelObject.getRequires() != null;
			}
		};
		requiresLink.add(new Label("requiresLabel", new Model<String>() {
			public String getObject() {
				final Quest modelObject = QuestPage.this.getModelObject();
				if (modelObject == null) {
					return null;
				}
				final Long requires = modelObject.getRequires();
				if (requires == null) {
					return null;
				}
				final Quest quest = getDatabase().getQuests().get(requires);
				return quest == null ? "<Unknown " + requires + ">" : quest.getTitle();
			}
		}));
		tabs.add(requiresLink);
		// Leads to
		final ListView<Quest> leadsTo = new ListView<Quest>("leadsTo", new ListModel<Quest>() {
			public List<Quest> getObject() {
				final Quest modelObject = QuestPage.this.getModelObject();
				if (modelObject == null) {
					return null;
				}
				final List<Quest> leadsTo2 = getDatabase().getQuests().getLeadsTo(modelObject);
				return leadsTo2;
			}
		}) {
			@Override
			protected void populateItem(final ListItem<Quest> item) {
				final Link<String> leadsToLink = new Link<String>("leadsToLink") {
					@Override
					public void onClick() {
						setResponsePage(QuestPage.class, new PageParameters().add("id", item.getModelObject().getEntityId()));
					}
				};
				leadsToLink.add(new Label("leadsToLabel", new PropertyModel<String>(item.getModel(), "title")));
				item.add(leadsToLink);
			}
		};
		WebMarkupContainer leadsToContainer = new WebMarkupContainer("leadsToContainer") {
			public boolean isVisible() {
				return leadsTo.getModelObject().size() > 0;
			}
		};
		leadsToContainer.add(leadsTo);
		tabs.add(leadsToContainer);
		// Map
		tabs.add(new MapPanel("mapPanel", new ListModel<MapPoint>() {
			public List<MapPoint> getObject() {
				return MapUtil.getPointsForQuest(getDatabase(), getModelObject());
			}
		}).setWidth(400));
		tabs.add(new Label("bodyText", new PropertyModel<String>(getModel(), "bodyText")));
		tabs.add(new Label("completeText", new PropertyModel<String>(getModel(), "completeText")));
		// Giver
		Link<String> giverLink = new Link<String>("viewGiver") {
			@Override
			public void onClick() {
				setResponsePage(CreaturePage.class, new PageParameters().add("id", QuestPage.this.getModelObject().getGiverId()));
			}
		};
		giverLink.add(new Label("giver", new Model<String>() {
			public String getObject() {
				final Long giverId = QuestPage.this.getModelObject().getGiverId();
				if (giverId == null) {
					return "<None set>";
				}
				Creature c = getDatabase().getCreatures().get(giverId);
				return c == null ? "<Unknown creature " + giverId + ">" : c.getDisplayName();
			}
		}));
		tabs.add(giverLink);
		// Acts
		ListView<Act> acts = new ListView<Act>("acts", new PropertyModel<List<Act>>(this, "modelObject.acts")) {
			@Override
			protected void populateItem(ListItem<Act> item) {
				item.add(new Label("actNumber", new Model<String>(String.valueOf((char) ('\u278A' + item.getIndex())))));
				item.add(new Label("actText", new Model<String>(item.getModelObject().getText())));
				item.add(new ListView<Objective>("objectives", new PropertyModel<List<Objective>>(item.getModel(), "objectives")) {
					@Override
					protected void populateItem(ListItem<Objective> item) {
						item.add(new AttributeModifier("class",
								new Model<String>(item.getModelObject().getType().name().toLowerCase() + "Objective objective")));
						item.add(new Image("objectiveImage", new PackageResourceReference(QuestPage.class,
								item.getModelObject().getType().name().toLowerCase() + ".png")));
						item.add(new Label("objectiveText", new Model<String>(item.getModelObject().getDescription())));
					}
				});
			}
		};
		tabs.add(acts);
		// Ender
		Link<String> enderLink = new Link<String>("viewEnder") {
			@Override
			public void onClick() {
				setResponsePage(CreaturePage.class, new PageParameters().add("id", QuestPage.this.getModelObject().getEnderId()));
			}
		};
		enderLink.add(new Label("ender", new Model<String>() {
			public String getObject() {
				final Long enderId = QuestPage.this.getModelObject().getEnderId();
				if (enderId == null) {
					return "<None set>";
				}
				Creature c = getDatabase().getCreatures().get(enderId);
				return c == null ? "<Unknown creature " + enderId + ">" : c.getDisplayName();
			}
		}));
		tabs.add(enderLink);
	}

	protected void addRewards() {
		// Map
		add(new Label("exp", new PropertyModel<String>(getModel(), "exp")));
		add(new CoinViewPanel("coin", new PropertyModel<Long>(getModel(), "coin")));
		final PropertyModel<List<Reward>> rewardsModel = new PropertyModel<List<Reward>>(this, "rewards");
		ListView<Reward> l = new ListView<Reward>("rewards", rewardsModel) {
			@Override
			protected void populateItem(final ListItem<Reward> item) {
				item.add(new RewardViewPanel("reward", item.getModel(), new PropertyModel<IDatabase>(QuestPage.this, "database")));
				item.setOutputMarkupId(true);
				item.setMarkupId("reward" + item.getIndex());
			}

			public boolean isVisible() {
				final List<Reward> rewards = getRewards();
				return rewards != null && rewards.size() > 0;
			}
		};
		add(l);
	}

	public List<Reward> getRewards() {
		List<Reward> r = new ArrayList<Reward>();
		final Quest quest = getModelObject();
		if (quest != null) {
			for (Act act : quest.getActs()) {
				r.addAll(act.getRewards());
			}
		}
		return r;
	}

	public IModel<Quest> getModel() {
		return (IModel<Quest>) getDefaultModel();
	}

	public Quest getModelObject() {
		return getModel().getObject();
	}

	protected void addQuest() {
		add(new NonCachingImage("photo", new EntityAvatarModel(getModel())));
		add(new Label("title", new PropertyModel<String>(getModel(), "title")));
		add(new Label("level", new PropertyModel<Integer>(getModel(), "level")));
		add(new Label("partySize", new PropertyModel<Integer>(getModel(), "partySize")));
		final Dialog uploadPhotoDialog = new Dialog("uploadPhotoDialog") {
		};
		add(new Image("repeatImage", new PackageResourceReference(QuestPage.class, "loop_alt2_32x28.png")) {
			public boolean isVisible() {
				return getModelObject() != null && getModelObject().isRepeat();
			}
		});
		uploadPhotoDialog.setWidth(400);
		uploadPhotoDialog.setTitle("Upload Photo");
		uploadPhotoDialog.add(new UploadPanel("uploadPhotoPanel", new Model<Attachment>() {
			@Override
			public Attachment getObject() {
				return AttachmentUtil.getAttachment(getModelObject(), Application.getApp().getSiteData());
			}

			public void setObject(Attachment attachment) {
				attachment.getAttachments().save(attachment);
			}
		}, Bytes.kilobytes(3096), new Model<String>() {
			public String getObject() {
				return "Please choose a file to upload as a phose for the quest '" + QuestPage.this.getModelObject().getTitle()
						+ "' . The file should be " + "no bigger than 3MB.";
			}
		}));
		add(uploadPhotoDialog);
		add(new AjaxLink<String>("uploadPhoto") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				uploadPhotoDialog.open(target);
			}
		});
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		super.onRenderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(QuestPage.class, "QuestPage.css")));
	}
}

package org.icemoon.worldeditor.components;

import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.AbstractEntity;
import org.icemoon.eartheternal.common.Act;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.MapPoint;
import org.icemoon.eartheternal.common.Objective;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.worldeditor.model.EntityAvatarModel;
import org.icemoon.worldeditor.model.PartySizeModel;

@SuppressWarnings("serial")
public class MapPointPanel extends Panel {
	public MapPointPanel(final String id, IModel<MapPoint> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		add(new NonCachingImage("avatar", new EntityAvatarModel(new PropertyModel<AbstractEntity<?,?>>(this, "modelObject.entity"))));
		add(new Label("text", new PropertyModel<String>(this, "modelObject.entity")));
		add(new Label("type", new Model<String>() {
			public String getObject() {
				AbstractEntity<?,?> entity = getModelObject().getEntity();
				if (entity instanceof GameCharacter) {
					return "Player Character";
				} else if (entity instanceof Creature) {
					return "Non-Player Character";
				} else if (entity instanceof Quest) {
					return "Quest";
				} else {
					return entity.getClass().getSimpleName();
				}
			}
		}));
		add(new Label("fromQuest", new PropertyModel<Integer>(this, "modelObject.entity.act.quest.title")) {
			public boolean isVisible() {
				return getModelObject().getEntity() instanceof Objective;
			}
		});
		add(new Label("subtitle", new PropertyModel<Integer>(this, "subtitle")));
		add(new Label("level", new PropertyModel<Integer>(this, "quest.level")) {
			public boolean isVisible() {
				return hasQuest();
			}
		});
		add(new Label("partySize", new PartySizeModel(new PropertyModel<Integer>(this, "quest.partySize"))) {
			public boolean isVisible() {
				return hasQuest();
			}
		});
		add(new Label("exp", new PropertyModel<Integer>(this, "quest.exp")) {
			public boolean isVisible() {
				return hasQuest();
			}
		});
		add(new CoinViewPanel("coin", new PropertyModel<Long>(this, "quest.coin")) {
			public boolean isVisible() {
				return hasQuest();
			}
		});
		super.onInitialize();
	}

	protected boolean hasQuest() {
		return getQuest() != null;
	}

	public String getSubtitle() {
		Entity<?> o = getModelObject().getEntity();
		if (o instanceof Objective) {
			Objective obj = (Objective) o;
			final List<Objective> objectives = obj.getAct().getObjectives();
			String sub = "";
			if (objectives.size() > 1) {
				sub += (obj.getEntityId() + 1) + " of " + objectives.size();
			}
			final List<Act> acts = obj.getAct().getQuest().getActs();
			if (acts.size() > 1) {
				sub += " (Act " + (obj.getAct().getEntityId() + 1) + " of " + acts.size() + ")";
			}
			return sub;
		}
		return "";
	}

	public Quest getQuest() {
		Entity<?> o = getModelObject().getEntity();
		if (o instanceof Quest) {
			return (Quest) o;
		} else if (o instanceof Objective) {
			return ((Objective) o).getAct().getQuest();
		}
		return null;
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(MapPointPanel.class, "MapPointPanel.css")));
	}

	public IModel<MapPoint> getModel() {
		return (IModel<MapPoint>) getDefaultModel();
	}

	public MapPoint getModelObject() {
		return getModel().getObject();
	}
}
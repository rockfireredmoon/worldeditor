package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.Creatures;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.MapPoint;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.worldeditor.components.MapPanel;
import org.icemoon.worldeditor.components.QuestsPanel;

@SuppressWarnings("serial")
public class CreaturePage extends AbstractCreaturePage<Creature, Creatures, IDatabase> {
	@Override
	protected void onInitialize() {
		super.onInitialize();
		addMaps();
	}

	protected void addMaps() {
		// Map
		tabs.add(new MapPanel("mapPanel", new ListModel<MapPoint>() {
			public List<MapPoint> getObject() {
				return MapUtil.getPointsForCreature(getDatabase(), getModelObject());
			}
		}).setWidth(400));
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(CreaturePage.class, "CreaturePage.css")));
		super.onRenderHead(response);
	}

	@Override
	protected Creatures getEntityDatabase() {
		return getDatabase().getCreatures();
	}

	protected void addQuests() {
		tabs.add(new QuestsPanel(new IModel<IDatabase>() {
			@Override
			public void detach() {
			}

			@Override
			public IDatabase getObject() {
				return Application.getAppSession(getRequestCycle()).getDatabase();
			}

			@Override
			public void setObject(IDatabase object) {
			}
		}, new ListModel<Long>() {
			@Override
			public List<Long> getObject() {
				return new ArrayList<Long>(Application.getAppSession(getRequestCycle()).getDatabase().getQuests()
						.getQuestsWithCreature(getModelObject()));
			}
		}));
	}
}

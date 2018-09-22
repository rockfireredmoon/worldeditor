package org.icemoon.worldeditor.components;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.worldeditor.CreaturesPage;
import org.icemoon.worldeditor.SelectorPanel;
import org.icemoon.worldeditor.model.CreaturesModel;

@SuppressWarnings("serial")
public class CreatureChooser extends SelectorPanel<Long, Creature, String, IDatabase> {
	public CreatureChooser(String id, IModel<String> title, IModel<Long> model, IModel<? extends IDatabase> database) {
		super(id, title, new CreaturesModel(database), "displayName", model, Creature.class, Long.class, CreaturesPage.class);
	}

	@Override
	protected void buildExtraChooserColumns(List<IColumn<Creature, String>> columns) {
		columns.add(new AbstractColumn<Creature, String>(new Model<String>("Level"), "level") {
			public void populateItem(Item<ICellPopulator<Creature>> cellItem, String componentId, IModel<Creature> model) {
				cellItem.add(new Label(componentId, new Model<Integer>(model.getObject().getLevel())));
			}
		});
	}
}
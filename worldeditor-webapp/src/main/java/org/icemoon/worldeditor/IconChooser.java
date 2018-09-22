package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.icemoon.eartheternal.common.GameIcon;
import org.icemoon.eartheternal.common.GameIcons;
import org.icemoon.eartheternal.common.IStatic;
import org.icemoon.worldeditor.components.GameIconPanel;

@SuppressWarnings("serial")
public class IconChooser extends SelectorPanel<String, GameIcon, String, IStatic> {

	public IconChooser(String id, IModel<String> title, 
			IModel<String> model) {
		super(id, title, new GameIconsModel(), null, model, GameIcon.class, String.class, null);
	}
	
	public IconChooser(String id, IModel<String> title, IModel<GameIcons> entities,
			IModel<String> model) {
		super(id, title, entities, "entityId", model, GameIcon.class, String.class, null);
	}

	@Override
	protected void buildExtraChooserColumns(List<IColumn<GameIcon, String>> columns) {
		columns.add(0, new AbstractColumn<GameIcon, String>(new Model<String>(""), "icon") {
			public void populateItem(Item<ICellPopulator<GameIcon>> cellItem, String componentId, IModel<GameIcon> model) {
				cellItem.add(new AttributeModifier("class", "icon"));
				GameIconPanel panel = new GameIconPanel(componentId, new Model<String>(model.getObject().getEntityId()), null,
					32);
				cellItem.add(panel);
			}
		});
	}
}
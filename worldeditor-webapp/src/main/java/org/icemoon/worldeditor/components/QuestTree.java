package org.icemoon.worldeditor.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.worldeditor.table.ClassedPropertyColumn;

public class QuestTree extends TableTree<Quest, String> implements ISortStateLocator {
	private static final long serialVersionUID = 1L;

	private IModel<Quest> selected;
	private SingleSortState sortState;

	public QuestTree(String id, ITreeProvider<Quest> provider, IModel<Set<Quest>> state) {
		super(id, createColumns(), provider, Integer.MAX_VALUE, state);
//		add(new HeadersToolbar(this, this));
//		addBottomToolbar(new NoRecordsToolbar(this));
		sortState = new SingleSortState();
		sortState.setPropertySortOrder("entityId", SortOrder.ASCENDING);
	}

	protected void onDetach() {
		if (selected != null) {
			selected.detach();
		}
		super.onDetach();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
	}

	public IModel<Quest> getSelected() {
		return selected;
	}

	protected boolean isSelected(Quest foo) {
		IModel<Quest> model = getProvider().model(foo);
		try {
			return selected != null && selected.equals(model);
		} finally {
			model.detach();
		}
	}

	public void select(Quest foo, final AjaxRequestTarget target) {
		if (selected != null) {
			updateNode(selected.getObject(), target);

			selected.detach();
			selected = null;
		}

		selected = getProvider().model(foo);

		updateNode(foo, target);
	}

	@Override
	protected Component newContentComponent(String id, IModel<Quest> model) {
		return new Folder<Quest>(id, this, model);
	}

	@Override
	protected Item<Quest> newRowItem(String id, int index, IModel<Quest> model) {
		return new OddEvenItem<Quest>(id, index, model);
	}

	@Override
	public ISortState getSortState() {
		return sortState;
	}

	private static List<IColumn<Quest, String>> createColumns() {
		List<IColumn<Quest, String>> columns = new ArrayList<IColumn<Quest, String>>();
		columns.add(new ClassedPropertyColumn<Quest>(new ResourceModel("column.entityId"), "entityId", "entityId", "entityId"));
		columns.add(new TreeColumn<Quest, String>(new ResourceModel("column.name")));
		columns.add(new ClassedPropertyColumn<Quest>(new ResourceModel("column.level"), "level", "level", "level"));
		columns.add(new ClassedPropertyColumn<Quest>(new ResourceModel("column.partySize"), "partySize", "partySize", "partySize"));
		columns.add(new ClassedPropertyColumn<Quest>(new ResourceModel("column.exp"), "exp", "exp", "exp"));
		columns.add(new AbstractColumn<Quest, String>(new ResourceModel("column.coin")) {
			private static final long serialVersionUID = -2204921359683191749L;

			@Override
			public void populateItem(Item<ICellPopulator<Quest>> cellItem, String componentId, IModel<Quest> rowModel) {
				cellItem.add(new CoinViewPanel(componentId, new Model<Long>(rowModel.getObject().getCoin())));
			}
		});
		return columns;
	}
}
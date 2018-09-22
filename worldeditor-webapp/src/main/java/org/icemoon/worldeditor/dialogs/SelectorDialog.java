package org.icemoon.worldeditor.dialogs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.worldeditor.model.FilterableSortableEntitiesDataProvider;
import org.icemoon.worldeditor.table.ClassedPropertyColumn;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.dialog.Dialog;

@SuppressWarnings("serial")
public class SelectorDialog<K extends Serializable, T extends Entity<K>, L extends Serializable, R extends IRoot> extends Panel
		implements ISelected<T> {
	private WebMarkupContainer container;

	public SelectorDialog(String id, IModel<String> title, FilterableSortableEntitiesDataProvider<T, K, L, R> provider,
			String displayNameProperty) {
		this(id, title, provider, displayNameProperty, true);
	}

	public SelectorDialog(String id, IModel<String> title, FilterableSortableEntitiesDataProvider<T, K, L, R> provider,
			String displayNameProperty, boolean addIdColumn) {
		super(id);
		Dialog dialog = new Dialog("dialog");
		add(dialog);
		dialog.setModal(true);
		dialog.setTitle(title);
		dialog.setWidth(800);
		final PropertyModel<String> filterTextModel = new PropertyModel<String>(provider,
				"filterState." + (displayNameProperty == null ? "entityId" : displayNameProperty));
		List<IColumn<T, String>> columns = new ArrayList<IColumn<T, String>>();
		columns.add(new ActionColumn<T>(new ResourceModel("selectorColumn.actions"), new PropertyModel<ISelected<T>>(this, "")));
		if (displayNameProperty != null) {
			columns.add(new ClassedPropertyColumn<T>(new ResourceModel("selectorColumn.name"), displayNameProperty,
					displayNameProperty, displayNameProperty));
		}
		if (addIdColumn) {
			columns.add(new ClassedPropertyColumn<T>(new ResourceModel("selectorColumn.id"), "entityId", "entityId", "entityId"));
		}
		buildExtraColumns(columns);
		final TextField<String> filterTextField = new TextField<String>("selectorFilterText", filterTextModel, String.class);
		container = new WebMarkupContainer("selectorContainer");
		container.setOutputMarkupId(true);
		container.setVisible(false);
		container.setOutputMarkupPlaceholderTag(true);
		final AjaxFallbackDefaultDataTable<T, String> table = new AjaxFallbackDefaultDataTable<T, String>("selectorTable", columns,
				provider, 10);
		container.add(table);
		container.add(new AjaxButton("clear") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				filterTextField.setConvertedInput("");
				filterTextModel.setObject("");
				target.add(filterTextField);
				target.add(table);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		container.add(filterTextField);
		filterTextField.setOutputMarkupId(true);
		filterTextField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				target.add(table);
			}
		});
		dialog.add(container);
	}

	protected void buildExtraColumns(List<IColumn<T, String>> columns) {
		// For subclasses to override
	}

	protected void onSelectEntity(AjaxRequestTarget target, T newEntity) {
		// For subclasses to override
	}

	protected static final class ActionColumn<T> extends AbstractColumn<T, String> {
		private IModel<ISelected<T>> dialog;

		protected ActionColumn(IModel<String> displayModel, IModel<ISelected<T>> dialog) {
			super(displayModel);
			this.dialog = dialog;
		}

		public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, final IModel<T> model) {
			cellItem.add(new AttributeModifier("class", "actions"));
			cellItem.add(new ActionPanel<T>(dialog, componentId, model, cellItem.getIndex()));
		}
	}

	static class ActionPanel<T> extends Panel {
		public ActionPanel(final IModel<ISelected<T>> dialog, String id, final IModel<T> model, final int index) {
			super(id, model);
			add(new AjaxLink<T>("select", model) {
				@Override
				public void onClick(AjaxRequestTarget target) {
					dialog.getObject().onActionSelected(target, getModelObject());
				}
			});
		}
	}

	public JsStatement open() {
		return getDialog().open();
	}

	public void open(AjaxRequestTarget target) {
		container.setVisible(true);
		target.add(container);
		getDialog().open(target);
	}

	public Dialog getDialog() {
		return (Dialog) get("dialog");
	}

	@Override
	public void onActionSelected(AjaxRequestTarget target, T modelObject) {
		getDialog().close(target);
		onSelectEntity(target, modelObject);
	}
}
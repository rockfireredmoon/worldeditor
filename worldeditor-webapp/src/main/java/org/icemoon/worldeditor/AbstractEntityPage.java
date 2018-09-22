package org.icemoon.worldeditor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.GoAndClearFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.StringValue;
import org.icemoon.eartheternal.common.AbstractINIFileEntity;
import org.icemoon.eartheternal.common.AbstractMultiINIFileEntity;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.INIWriter;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.AbstractMultiINIFileEntities.IDType;
import org.icemoon.worldeditor.components.ConfirmDialog;
import org.icemoon.worldeditor.components.EntityActionsPanel;
import org.icemoon.worldeditor.entities.ActiveUser;
import org.icemoon.worldeditor.model.FilterableSortableEntitiesDataProvider;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;
import org.odlabs.wiquery.ui.effects.SlideEffectJavaScriptResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public abstract class AbstractEntityPage<T extends Entity<K>, K extends Serializable, L extends Serializable, D extends Entities<T, K, L, R>, R extends IRoot>
		extends AbstractAuthenticatedPage {
	final static Logger LOG = LoggerFactory.getLogger(AbstractEntityPage.class);
	protected boolean defeatSave;
	protected boolean editing;
	protected Form<T> form;
	protected Class<K> idClass;
	protected IDType idType = IDType.HIGHEST;
	private EntityActionsPanel<T> actionsPanel;
	private ConfirmDialog<T> cloneDialog;
	private String defaultSortColumn;
	private ConfirmDialog<T> deleteDialog;
	private FeedbackPanel feedback;
	private int firstRow;
	private int lastRow;
	private IModel<T> selected = new Model<T>();
	private int selectedIndex;
	private AjaxFallbackDefaultDataTable<T, String> table;
	private FilterableSortableEntitiesDataProvider<T, K, L, R> tableModel;

	public AbstractEntityPage(Class<K> idClass) {
		this("entityId", idClass);
	}

	public AbstractEntityPage(String defaultSortColumn, Class<K> idClass) {
		super();
		this.defaultSortColumn = defaultSortColumn;
		this.idClass = idClass;
	}

	public String getConfirmCloneText() {
		final T object = selected.getObject();
		return object == null ? null
				: MessageFormat.format(getLocalizer().getString("confirmClone", AbstractEntityPage.this), object.toString());
	}

	public String getConfirmDeleteText() {
		final T object = selected.getObject();
		return object == null ? null
				: MessageFormat.format(getLocalizer().getString("confirmDelete", AbstractEntityPage.this), object.toString());
	}

	public abstract D getEntityDatabase();

	public FeedbackPanel getMainFeedbackPanel() {
		return feedback;
	}

	public final T getSelected() {
		return selected.getObject();
	}

	public IModel<T> getSelectedModel() {
		return selected;
	}

	public void select(T selected, int selectedIndex) {
		if (form != null) {
			form.clearInput();
		}
		this.selected.setObject(selected);
		this.selectedIndex = selectedIndex;
		ActiveUser ae = Application.getAppSession(getRequestCycle()).getActiveUser();
		if (ae != null) {
			ae.setEditingEntity(selected);
		}
	}

	protected void addAdditionalEntityActions(Form<?> form) {
	}

	protected final void addForm() {
		// Common fields
		form = new Form<T>("form", new CompoundPropertyModel<T>(new PropertyModel<T>(this, "selected"))) {
			@Override
			protected void onBeforeRender() {
				onBeforeFormRender();
				super.onBeforeRender();
			}

			// protected void delegateSubmit(IFormSubmitter submittingComponent)
			// {
			// // TODO Auto-generated method stub
			// if(submittingComponent instanceof Button &&
			// ((Button)submittingComponent).getId().equals("saveButton"))
			// super.delegateSubmit(submittingComponent);
			// else
			// System.out.println("Skip!");
			// }
			@Override
			protected void onSubmit() {
				if (defeatSave) {
					defeatSave = false;
					return;
				}
				K entityId = getSelected().getEntityId();
				additionalValidation();
				if (!form.hasError() && !editing) {
					entityId = processNewId(entityId);
					if (entityId == null) {
						if (idType == IDType.MANUAL)
							form.error("You must provide an ID.");
						else
							form.error(
									"The current auto ID mode cannot determine the next ID, so try other modes or choose one manually.");
					} else if (getEntityDatabase().contains(entityId)) {
						form.error("An instance with the ID " + entityId + " already exists");
					}
				}
				if (!form.hasError()) {
					beforeSave(getSelected());
					save(getSelected());
					afterSave(getSelected());
					form.info(MessageFormat.format(getLocalizer().getString("saved", AbstractEntityPage.this),
							getSelected().toString()));
					if (!editing)
						doCreateNew();
				}
			}
		};
		form.modelChanged();
		form.setOutputMarkupId(true);
		form.add(new Label("editTitle", new Model<String>() {
			@Override
			public String getObject() {
				return getLocalizer().getString(isEditing() ? "editing" : "creating", AbstractEntityPage.this);
			}
		}));
		addIdField();
		// Type specific fields
		buildForm(form);
		// Save / reset
		form.add(new Button("saveButton"));
		form.add(new Button("resetButton"));
		// Navigate through items
		form.add(new AjaxButton("nextButton") {
			@Override
			public boolean isEnabled() {
				return selectedIndex < tableModel.size() - 1 && isEditing();
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				int size = (int) tableModel.size();
				if (size > 0) {
					int next = selectedIndex + 1;
					if (next >= size) {
						next = size - 1;
					}
					form.modelChanging();
					selectModelObjectRow(tableModel.iterator(next, 1).next(), next);
					// Scroll up?
					if (lastRow != -1 && next > lastRow) {
						table.setCurrentPage(table.getCurrentPage() + 1);
					}
					form.modelChanged();
					itemChanged(target);
				}
			}
		}.setDefaultFormProcessing(false));
		form.add(new AjaxButton("previousButton") {
			@Override
			public boolean isEnabled() {
				return selectedIndex > 0 && isEditing();
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				int size = (int) tableModel.size();
				if (size > 0) {
					int next = selectedIndex - 1;
					if (next < 0) {
						next = 0;
					}
					form.modelChanging();
					selectModelObjectRow(tableModel.iterator(next, 1).next(), next);
					// Scroll up?
					if (firstRow != -1 && next < firstRow) {
						table.setCurrentPage(table.getCurrentPage() - 1);
					}
					form.modelChanged();
					itemChanged(target);
				}
			}
		}.setDefaultFormProcessing(false));
		add(form);
	}
	
	protected void beforeSave(T entity) {
		
	}
	
	protected void afterSave(T entity) {
		
	}

	protected void addIdColumn(List<IColumn<T, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<T, K>(idClass, new ResourceModel("column.entityId"), "entityId",
				"entityId", "entityId"));
	}

	protected void addIdField() {
		form.add(new TextField<K>("entityId", idClass) {
			@Override
			public boolean isEnabled() {
				return isIdEnabled();
			}
		}.setRequired(true).setOutputMarkupId(true));
	}

	protected void addFiles(final Form<?> form, List<String> files) {
		final ListChoice<String> listChoice = new ListChoice<String>("file", new PropertyModel<String>(this, "selected.file"),
				files) {
			@Override
			public boolean isEnabled() {
				return !editing;
			}
		};
		listChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (!editing) {
					setNextFreeId();
					Component entityId = getComponent().getParent().get("entityId");
					if (entityId != null)
						target.add(entityId);
					Component idType = getComponent().getParent().get("idType");
					if (idType != null)
						target.add(idType);
				}
			}
		});
		listChoice.setRequired(true);
		listChoice.setMaxRows(1);
		listChoice.setChoiceRenderer(new IChoiceRenderer<String>() {
			@Override
			public Object getDisplayValue(String object) {
				return FilenameUtils.getBaseName(object);
			}

			@Override
			public String getIdValue(String object, int index) {
				return object;
			}
		});
		form.add(listChoice);
	}

	protected void addIdType(final Form<?> form) {
		form.add(new DropDownChoice<IDType>("idType", new PropertyModel<IDType>(this, "idType"), Arrays.asList(IDType.values())) {
			@Override
			public boolean isEnabled() {
				return getParent().get("file") == null || StringUtils.isNotBlank(getSelected().getFile());
			}

			@Override
			public boolean isVisible() {
				return !editing;
			}
		}.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				setNextFreeId();
				target.add(form);
			}
		}));
	}

	protected void additionalValidation() {
	}

	protected final void addTable() {
		List<IColumn<T, String>> columns = new ArrayList<IColumn<T, String>>();
		columns.add(new FilteredAbstractColumn<T, String>(new Model<String>("Actions")) {
			public Component getFilter(final String id, final FilterForm<?> form) {
				return new GoAndClearFilter(id, form) {
					protected void onClearSubmit(final Button button) {
						@SuppressWarnings("unchecked")
						Form<Object> form = (Form<Object>) button.getForm();
						final T newFilter = createNewInstance();
						tableModel.setFilterState(newFilter);
						form.setDefaultModelObject(newFilter);
					}
				};
			}

			public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> model) {
				cellItem.add(new AttributeModifier("class", "actions"));
				cellItem.add(new ActionPanel(componentId, model, calcRowIndex(cellItem.getIndex())));
			}
		});
		tableModel = createModel();
		addIdColumn(columns);
		buildColumns(columns);
		FilterForm<T> filterForm = new FilterForm<T>("filterForm", tableModel);
		table = new AjaxFallbackDefaultDataTable<T, String>("table", columns, tableModel, 25) {
			@Override
			protected Item<T> newRowItem(String id, final int index, final IModel<T> model) {
				final int rowIndex = calcRowIndex(index);
				Item<T> row = super.newRowItem(id, index, model);
				row.add(new AttributeAppender("class", new Model<String>() {
					@Override
					public String getObject() {
						return (model.getObject().equals(getSelected()) ? " selected" : " deselected") + " entity"
								+ model.getObject().getEntityId().toString();
					}
				}));
				row.add(new AjaxEventBehavior("onclick") {
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						selectModelObjectRow(model.getObject(), rowIndex);
						itemChanged(target);
					}
				});
				// keep track of start and end of page
				if (firstRow == -1) {
					firstRow = rowIndex;
				}
				lastRow = rowIndex;
				return row;
			}

			@Override
			protected void onPageChanged() {
				resetPageIndexes();
				super.onPageChanged();
			}
		};
		table.addTopToolbar(new FilterToolbar(table, filterForm, tableModel));
		filterForm.add(table);
		add(filterForm);
		actionsPanel = createActionsPanel();
		actionsPanel.setOutputMarkupId(true);
		add(actionsPanel);
	}

	protected abstract void buildColumns(List<IColumn<T, String>> columns);

	protected abstract void buildForm(Form<T> form);

	protected int calcRowIndex(int pageRowIndex) {
		return (int) table.getCurrentPage() * (int) table.getItemsPerPage() + pageRowIndex;
	}

	protected T configureFilterObject(T obj) {
		return obj;
	}

	protected EntityActionsPanel<T> createActionsPanel() {
		return new EntityActionsPanel<T>(selected) {
			@Override
			protected void onExportList(OutputStream out) {
				try {
					StringWriter sw = new StringWriter();
					INIWriter w = new INIWriter(sw);
					// INIWriter w = new INIWriter(new OutputStreamWriter(out));
					for (Iterator<T> it = tableModel.iterator(0, tableModel.size()); it.hasNext();) {
						T t = it.next();
						if (t instanceof AbstractINIFileEntity) {
							AbstractINIFileEntity<?, ?> e = (AbstractINIFileEntity<?, ?>) t;
							e.write(w);
							w.println();
						}
					}
					w.flush();
					out.write(sw.toString().getBytes());
				} catch (IOException ioe) {
					throw new RuntimeException(ioe);
				}
			}

			@Override
			protected void onNew() {
				doCreateNew();
				AbstractEntityPage.this.onNew();
			}
		};
	}

	protected FilterableSortableEntitiesDataProvider<T, K, L, R> createModel() {
		final T filterObject = configureFilterObject(createNewInstance());
		return new FilterableSortableEntitiesDataProvider<T, K, L, R>(defaultSortColumn,
				new PropertyModel<Entities<T, K, L, R>>(this, "entityDatabase"), filterObject) {
			@Override
			protected boolean matches(T object, T filter) {
				return entityMatches(object, filter);
			}
		};
	}

	protected abstract T createNewInstance();

	protected void doCreateNew() {
		editing = false;
		final T instance = createNewInstance();
		final K pnd = peekNewId();
		if (pnd != null)
			instance.setEntityId(pnd);
		select(instance, -1);
	}

	protected boolean entityMatches(T object, T filter) {
		if (Util.notMatches(object.getEntityId(), filter.getEntityId())) {
			return false;
		}
		return true;
	}

	protected FilterableSortableEntitiesDataProvider<T, K, L, R> getTableModel() {
		return tableModel;
	}

	protected boolean isEditing() {
		return editing;
	}

	protected boolean isIdEnabled() {
		return !isEditing()
				&& (form.get("file") == null || (getSelected() != null && StringUtils.isNotBlank(getSelected().getFile())))
				&& (form.get("idType") == null || idType == IDType.MANUAL);
	}

	protected void itemChanged(AjaxRequestTarget target) {
		checkOtherUsers();
		target.add(form);
		target.add(table);
		target.add(actionsPanel);
		target.add(feedback);
		target.appendJavaScript("$('input[title]').qtip(); $('a[title]').qtip();");
	}

	protected void onBeforeFormRender() {
		resetPageIndexes();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onBeforeRender() {
		final AppSession appSession = Application.getAppSession(getRequestCycle());
		if (appSession.isAuthenticated()) {
			if (!appSession.isAdmin()) {
				if (Application.getApp().getUserData() == null) {
					appSession.setAdmin(true);
				}
			} else {
				final StringValue loadId = getPageParameters().get("id");
				if (!loadId.isNull()) {
					if (idClass.equals(String.class)) {
						selectModelObjectRow(getEntityDatabase().get((K) loadId.toString()), -1);
					} else if (idClass.equals(Integer.class)) {
						selectModelObjectRow(getEntityDatabase().get((K) (Integer.valueOf(loadId.toInt()))), -1);
					} else {
						selectModelObjectRow(getEntityDatabase().get((K) (Long.valueOf(loadId.toLong()))), -1);
					}
				}
			}
		}
		super.onBeforeRender();
	}

	protected void onEdit() {
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		resetPageIndexes();
		doCreateNew();
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);
		addTable();
		addForm();
		deleteDialog = new ConfirmDialog<T>("confirmDeleteDialog", selected, new Model<String>("Confirm Deletion"),
				new PropertyModel<String>(this, "confirmDeleteText")) {
			@Override
			protected void onConfirm(T object, AjaxRequestTarget target) {
				getEntityDatabase().delete(object);
				final String javascript = "$(\".entity" + object.getEntityId() + "\").fadeOut();";
				target.appendJavaScript(javascript);
				if (getSelected().equals(object)) {
					doCreateNew();
					target.add(form);
				}
			}
		};
		add(deleteDialog);
		cloneDialog = new ConfirmDialog<T>("confirmCloneDialog", selected, new Model<String>("Confirm Clone"),
				new PropertyModel<String>(this, "confirmCloneText")) {
			@Override
			protected void onConfirm(T original, AjaxRequestTarget target) {
				final Entities<T, K, L, R> entityDatabase = getEntityDatabase();
				final T newEntity = entityDatabase.cloneEntity(original);
				// entityDatabase.save(newEntity);
				select(newEntity, -1);
				setNextFreeId();
				editing = false;
				// info("Item " + original.getEntityId() + " (" +
				// original.toString() + ") cloned to ID " +
				// newEntity.getEntityId());
				itemChanged(target);
			}
		};
		add(cloneDialog);
		checkOtherUsers();
	}

	protected void checkOtherUsers() {
		List<ActiveUser> l = Application.getApp().getActiveUsers()
				.getOtherPageUsers(Application.getAppSession(getRequestCycle()).getActiveUser(), getPageClass());
		if (!l.isEmpty()) {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < l.size(); i++) {
				if (l.size() > 1 && i == l.size() - 1) {
					b.append(" and ");
				} else if (i > 0) {
					b.append(", ");
				}
				b.append(l.get(i).getUser().getName());
			}
			warn(b.toString() + " " + (l.size() == 1 ? "is" : "are") + " currently also active on this page.");
		}
	}

	protected void onNew() {
	}

	protected void onRenderEntityHead(IHeaderResponse response) {
	}

	protected final void onRenderHead(IHeaderResponse response) {
		super.onRenderHead(response);
		onRenderEntityHead(response);
		response.render(JavaScriptHeaderItem.forReference(SlideEffectJavaScriptResourceReference.get()));
	}

	@SuppressWarnings("unchecked")
	protected K peekNewId() {
		if (idClass.equals(Long.class)) {
			return getEntityDatabase().getNextFreeId((K) new Long(1), IDType.HIGHEST, null);
		} else if (idClass.equals(Integer.class)) {
			return getEntityDatabase().getNextFreeId((K) new Integer(1), IDType.HIGHEST, null);
		}
		return null;
		// throw new UnsupportedOperationException();
	}

	protected K processNewId(K displayed) {
		return displayed;
	}

	protected void resetPageIndexes() {
		firstRow = -1;
		lastRow = -1;
	}

	protected final void save(T entity) {
		getEntityDatabase().save(entity);
	}

	protected void selectModelObjectRow(int selectedIndex) {
		T t = table.getDataProvider().iterator(selectedIndex, 1).next();
		selectModelObjectRow(t, selectedIndex);
	}

	@SuppressWarnings("unchecked")
	protected void selectModelObjectRow(T t, int selectedIndex) {
		editing = true;
		if (selectedIndex == -1) {
			// TODO check sort is default
			selectedIndex = t == null ? -1 : getEntityDatabase().indexOf(t);
		}
		T clone = (T) UnoptimizedDeepCopy.copy(t);
		if (t instanceof AbstractMultiINIFileEntity) {
			AbstractMultiINIFileEntity<?,?> af = (AbstractMultiINIFileEntity<?,?>) t;
			LOG.debug("Item at:" + af.getStartPosition() + " / " + af.getEndPosition());
		}
		select(clone, selectedIndex);
		onEdit();
	}

	protected void setNextFreeId() {
		getSelected().setEntityId(getEntityDatabase().getNextFreeId(getSelected() == null ? null : getSelected().getEntityId(),
				idType, getSelected() == null ? null : getSelected().getFile()));
	}

	class ActionPanel extends Panel {
		public ActionPanel(String id, final IModel<T> model, final int rowIndex) {
			super(id, model);
			add(new AjaxLink<String>("select") {
				@Override
				public void onClick(AjaxRequestTarget target) {
					selectModelObjectRow(model.getObject(), rowIndex);
					itemChanged(target);
				}
			});
			add(new AjaxLink<String>("remove") {
				@Override
				public void onClick(AjaxRequestTarget target) {
					selectModelObjectRow(model.getObject(), rowIndex);
					itemChanged(target);
					deleteDialog.open(target);
				}
			});
			add(new AjaxLink<String>("clone") {
				@Override
				public void onClick(AjaxRequestTarget target) {
					selectModelObjectRow(model.getObject(), rowIndex);
					itemChanged(target);
					cloneDialog.open(target);
				}
			});
		}
	}
}

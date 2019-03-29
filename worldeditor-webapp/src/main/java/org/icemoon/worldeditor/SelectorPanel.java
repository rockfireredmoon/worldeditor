package org.icemoon.worldeditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.AbstractEntities;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.worldeditor.dialogs.SelectorDialog;
import org.icemoon.worldeditor.model.FilterableSortableEntitiesDataProvider;

@SuppressWarnings("serial")
public class SelectorPanel<K extends Serializable, T extends Entity<K>, L extends Serializable, R extends IRoot> extends FormComponentPanel<K> {
	private IModel<? extends Entities<T, K, L, R>> entities;
	private SelectorDialog<K, T, L, R> dialog;
	private String idProperty;
	private Class<? extends Page> editorPage;
	private boolean showLabel;
	private boolean showClear;
	private boolean changeAllowed = true;

	public SelectorPanel(final String id, IModel<String> title, IModel<? extends Entities<T, K, L, R>> entities,
			String displayNameProperty, IModel<K> model, Class<T> filterObjectClass, Class<K> keyClass,
			Class<? extends Page> editorPage) {
		this(id, title, entities, displayNameProperty, "entityId", model, filterObjectClass, keyClass, editorPage);
	}

	public SelectorPanel(final String id, IModel<String> title, IModel<? extends Entities<T, K, L, R>> entities,
			String displayNameProperty, String idProperty, IModel<K> model, Class<T> filterObjectClass, Class<K> keyClass,
			Class<? extends Page> editorPage) {
		super(id, model);
		init(title, entities, displayNameProperty, idProperty, filterObjectClass, keyClass, editorPage);
	}

	public SelectorPanel(final String id, IModel<String> title, IModel<? extends AbstractEntities<T, K, L, R>> entities,
			String displayNameProperty, Class<T> filterObjectClass, Class<K> keyClass, Class<? extends Page> editorPage) {
		this(id, title, entities, displayNameProperty, "entityId", filterObjectClass, keyClass, editorPage);
	}

	public SelectorPanel(final String id, IModel<String> title, IModel<? extends AbstractEntities<T, K, L, R>> entities,
			String displayNameProperty, String idProperty, Class<T> filterObjectClass, Class<K> keyClass,
			Class<? extends Page> editorPage) {
		super(id);
		init(title, entities, displayNameProperty, idProperty, filterObjectClass, keyClass, editorPage);
	}

	public boolean isChangeAllowed() {
		return changeAllowed;
	}

	public SelectorPanel<K, T, L, R> setShowClear(boolean showClear) {
		this.showClear = showClear;
		return this;
	}

	protected void buildExtraChooserColumns(List<IColumn<T, String>> columns) {
		// For subclasses to override
	}

	protected K buildId(T object) {
		return new PropertyModel<K>(object, idProperty).getObject();
	}

	@SuppressWarnings("unchecked")
	private void init(IModel<String> title, IModel<? extends Entities<T, K, L, R>> entities, final String displayNameProperty,
			final String idProperty, Class<T> filterObjectClass, Class<K> keyClass, Class<? extends Page> editorPage) {
		setOutputMarkupId(true);
		this.entities = entities;
		this.editorPage = editorPage;
		this.idProperty = idProperty;
		setType(keyClass);
		FilterableSortableEntitiesDataProvider<T, K, L, R> provider;
		try {
			provider = new FilterableSortableEntitiesDataProvider<T, K, L, R>(
					displayNameProperty == null ? "entityId" : displayNameProperty,
					(IModel<? extends AbstractEntities<T, K, L, R>>) entities, filterObjectClass.newInstance()) {
				@Override
				protected boolean matches(T object, T filter) {
					Object o1 = displayNameProperty == null ? object.getEntityId()
							: new PropertyModel<Object>(object, displayNameProperty).getObject();
					Object o2 = displayNameProperty == null ? filter.getEntityId()
							: new PropertyModel<Object>(filter, displayNameProperty).getObject();
					return o2 == null || (o1 != null && String.valueOf(o1).toLowerCase().contains(String.valueOf(o2).toLowerCase()))
							|| (o1 != null
									&& String.valueOf(buildId(object)).toLowerCase().contains(String.valueOf(o2).toLowerCase()));
				}
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		add(dialog = new SelectorDialog<K, T, L, R>("chooser", title, provider, displayNameProperty) {
			protected void buildExtraColumns(List<IColumn<T, String>> columns) {
				buildExtraChooserColumns(columns);
			}

			@Override
			protected void onSelectEntity(AjaxRequestTarget target, T newEntity) {
				final K newId = buildId(newEntity);
				((SelectorPanel)getParent()).getModel().setObject(newId);
				target.add(getParent().get("selectorLink"));
				onEntitySelected(target, newEntity);
			}
		});
		add(new AjaxButton("choose") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				dialog.open(target);
			}
			@Override
			public boolean isVisible() {
				return isChangeAllowed();
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		add(new AjaxButton("clear") {
			@Override
			public boolean isVisible() {
				return showClear && isChangeAllowed();
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				clear();
				target.add(getParent().get("selectorLink"));
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		AjaxLink<K> link = new AjaxLink<K>("selectorLink", getModel()) {
			@Override
			protected void onBeforeRender() {
//				setEnabled(getParent().getDefaultModelObject() != null);
				super.onBeforeRender();
			}

			@Override
			public void onClick(AjaxRequestTarget target) {

				K modelObject =  (K)((SelectorPanel)getParent()).getModel().getObject();
//				K modelObject = getModelObject();
				T entity = modelObject == null ? null : getEntityForId(modelObject);
				if (entity != null)
					onEntityLinkSelected(target, entity);
			}

			@Override
			public boolean isEnabled() {
				// TODO Auto-generated method stub
				return super.isEnabled();
			}
		};
		link.setEnabled(editorPage != null);
		link.add(new Label("selectorLinkLabel", new PropertyModel<String>(this, "selectedLabel")) {
			@Override
			public boolean isVisible() {
				return isShowLabel();
			}
		});
		link.setOutputMarkupId(true);
		add(link);
	}
	
	public void show(AjaxRequestTarget target) {
		dialog.open(target);
	}

	public String getSelectedLabel() {
		K modelObject = getModelObject();
		T entity = modelObject == null ? null : getEntityForId(modelObject);
		return modelObject == null ? "Nothing" : (entity == null ? "<Unknown " + modelObject + ">" : entity.toString());
	}

	public boolean isShowLabel() {
		return showLabel;
	}

	public SelectorPanel<K, T, L, R> setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
		return this;
	}

	private void clear() {
		getModel().setObject(null);
	}

	protected void convertInput() {
		final K modelObject = getModelObject();
		setConvertedInput(modelObject);
	}

	public T getEntityForId(K key) {
		return entities.getObject().get(key);
	}

	public T getEntityForName(String name) {
		for (T e : entities.getObject().values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}

	protected final void onEntityLinkSelected(AjaxRequestTarget target, T entity) {
		PageParameters parms = new PageParameters();
		parms.add("id", entity.getEntityId());
		setResponsePage(editorPage, parms);
		doOnEntityLinkSelected(target, entity);
	}

	protected void doOnEntityLinkSelected(AjaxRequestTarget target, T entity) {
		// For subclasses to update on selection
	}

	protected void onEntitySelected(AjaxRequestTarget target, T entity) {
		// For subclasses to update on selection
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(SelectorPanel.class, "SelectorPanel.css")));
	}

	class EntityNameListModel extends ListModel<String> {
		private List<String> entityNames = null;

		public List<String> getObject() {
			if (entityNames == null) {
				entityNames = new ArrayList<String>();
				for (T e : entities.getObject().values()) {
					entityNames.add(e.toString());
				}
			}
			return entityNames;
		}
	}
}
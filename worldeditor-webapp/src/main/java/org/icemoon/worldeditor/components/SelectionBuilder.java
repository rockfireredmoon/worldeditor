package org.icemoon.worldeditor.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.RawValidationError;
import org.icemoon.eartheternal.common.AbstractEntities;
import org.icemoon.eartheternal.common.AbstractEntity;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.worldeditor.ItemsPage;
import org.icemoon.worldeditor.SelectorPanel;

@SuppressWarnings("serial")
public class SelectionBuilder<K extends Serializable, T extends AbstractEntity<K, R>, LK extends Serializable, L extends AbstractEntities<T, K, LK, R>, E, R extends IRoot>
		extends FormComponentPanel<List<E>> {
	private K newItemId;
	private int editingItem = -1;
	private boolean creatingItem;
	private final Class<K> keyClass;
	private final Class<? extends Page> listPage;
	private final Class<T> entityClass;
	private final IModel<L> db;
	private final String displayNameExpression;
	protected Form<Object> form;
	protected List<E> items = new ArrayList<E>();

	public SelectionBuilder(final String id, Class<K> keyClass, Class<T> entityClass, Class<? extends Page> listPage, IModel<L> db,
			String displayNameExpression) {
		super(id);
		this.keyClass = keyClass;
		this.listPage = listPage;
		this.entityClass = entityClass;
		this.db = db;
		this.displayNameExpression = displayNameExpression;
	}

	public SelectionBuilder(final String id, IModel<List<E>> model, Class<K> keyClass, Class<T> entityClass,
			Class<? extends Page> listPage, IModel<L> db, String displayNameExpression) {
		super(id, model);
		this.db = db;
		this.keyClass = keyClass;
		this.listPage = listPage;
		this.entityClass = entityClass;
		this.displayNameExpression = displayNameExpression;
	}

	@SuppressWarnings("unchecked")
	public E convert(K key) {
		return (E) key;
	}

	@SuppressWarnings("unchecked")
	public K deconvert(E key) {
		return (K) key;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(SelectionBuilder.class, "SelectionBuilder.css")));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		form = new Form<Object>("newItemForm");
		buildForm(form);
		/*
		 * Wrap everything in a container that we refresh. This is done so we
		 * can tell the difference between this component being re-rendered as a
		 * whole (i.e. the selected entity changing) and this component itself
		 * refresh (i.e. a new item was added)
		 */
		WebMarkupContainer wmc = new WebMarkupContainer("selectionBuilder");
		wmc.setOutputMarkupId(true);
		wmc.add(form);
		wmc.add(new ListView<E>("items", new PropertyModel<List<E>>(this, "items")) {
			@Override
			protected void populateItem(final ListItem<E> item) {
				final Link<String> viewLink = new Link<String>("viewItem") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("id", item.getModelObject());
						setResponsePage(ItemsPage.class, params);
					}
				};
				viewLink.add(new Label("itemName", new Model<String>() {
					@Override
					public String getObject() {
						K modelObject = deconvert(item.getModelObject());
						if (modelObject == null)
							return "";
						else {
							final T gameItem = db.getObject().get(modelObject);
							if (gameItem == null)
								return "<Missing item " + modelObject + ">";
							else
								return new PropertyModel<String>(gameItem, displayNameExpression).getObject();
						}
					}
				}));
				item.add(viewLink);
				item.add(new AjaxLink<String>("removeItem") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						items.remove(item.getModelObject());
						target.add(getParent().getParent().getParent());
					}
				});
				item.add(new AjaxLink<String>("editItem") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						E mo = item.getModelObject();
						newItemId = deconvert(mo);
						editingItem = item.getIndex();
						creatingItem = false;
						onEdit(target, mo);
						target.add(getParent().getParent().getParent());
					}
				});
			}
		}.setReuseItems(true));
		wmc.add(new AjaxButton("newItem") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				newItemId = null;
				editingItem = -1;
				creatingItem = true;
				onNew(target);
				target.add(getParent());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getParent());
			}

			@Override
			public boolean isVisible() {
				return !creatingItem && editingItem == -1;
			}
		}.setDefaultFormProcessing(false));
		// This
		add(wmc);
		add(new IValidator<List<E>>() {
			@Override
			public void validate(IValidatable<List<E>> validatable) {
				if (editingItem != -1 || creatingItem) {
					validatable.error(new RawValidationError(
							"New or updated item must be saved (i.e. 'Add' or 'Update' buttons must be pressed)."));
				}
			}
		});
	}

	protected void onNew(AjaxRequestTarget target) {
	}

	protected void onEdit(AjaxRequestTarget target, E mo) {
	}

	protected void onSelection(AjaxRequestTarget target, T entity) {
	}

	protected void buildForm(Form<?> newItemForm) {
		newItemForm.add(new SelectorPanel<K, T, LK, R>("itemSelector", new Model<String>("Item"), db, displayNameExpression,
				new PropertyModel<K>(this, "newItemId"), entityClass, keyClass, listPage) {
			@Override
			public boolean isVisible() {
				return editingItem != -1 || creatingItem;
			}

			@Override
			protected void onEntitySelected(AjaxRequestTarget target, T entity) {
				super.onEntitySelected(target, entity);
				target.add(getParent());
				onSelection(target, entity);
			}
		}.setShowLabel(true));
		newItemForm.add(new AjaxButton("saveItem") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				items.set(editingItem, convert(newItemId));
				resetItemEdit();
				target.add(getParent().getParent());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getParent().getParent());
			}

			@Override
			public boolean isVisible() {
				return editingItem != -1;
			}
		});
		newItemForm.add(new AjaxButton("createItem") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				items.add(convert(newItemId));
				resetItemEdit();
				target.add(getParent().getParent());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getParent().getParent());
			}

			@Override
			public boolean isVisible() {
				return creatingItem;
			}
		});
	}

	public void resetItemEdit() {
		editingItem = -1;
		newItemId = null;
		creatingItem = false;
	}

	@Override
	protected void onBeforeRender() {
		if (!hasErrorMessage()) {
			((WebMarkupContainer) get("selectionBuilder").get("items")).removeAll();
			List<E> modelObject = getModelObject();
			items = new ArrayList<E>();
			if (modelObject != null)
				items.addAll(modelObject);
			resetItemEdit();
		}
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		setConvertedInput(new ArrayList<E>(items));
	}
}
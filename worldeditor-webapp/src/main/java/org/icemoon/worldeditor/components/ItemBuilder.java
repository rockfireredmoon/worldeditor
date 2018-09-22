package org.icemoon.worldeditor.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.RawValidationError;

@SuppressWarnings("serial")
public abstract class ItemBuilder<O> extends FormComponentPanel<List<O>> {
	private int editingItem = -1;
	private boolean creatingItem;
	private final String displayNameExpression;
	protected Form<O> form;
	private O item;
	private List<O> items = new ArrayList<O>();

	public ItemBuilder(final String id) {
		this(id, null);
	}

	public ItemBuilder(final String id, String displayNameExpression) {
		super(id);
		this.displayNameExpression = displayNameExpression;
	}

	public ItemBuilder(final String id, IModel<List<O>> model, String displayNameExpression) {
		super(id, model);
		this.displayNameExpression = displayNameExpression;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(ItemBuilder.class, "ItemBuilder.css")));
	}

	@Override
	protected void onInitialize() {
		// TODO Auto-generated method stub
		super.onInitialize();
		/*
		 * Wrap everything in a container that we refresh. This is done so we
		 * can tell the difference between this component being re-rendered as a
		 * whole (i.e. the selected entity changing) and this component itself
		 * refresh (i.e. a new item was added)
		 */
		WebMarkupContainer wmc = new WebMarkupContainer("selectionBuilder");
		wmc.setOutputMarkupId(true);
		// Characters
		wmc.add(new ListView<O>("items", new PropertyModel<List<O>>(this, "items")) {
			@Override
			protected void populateItem(final ListItem<O> item) {
				item.add(new Label("itemName", new Model<String>() {
					@Override
					public String getObject() {
						O io = item.getModelObject();
						if (io == null)
							return "";
						else {
							if (StringUtils.isBlank(displayNameExpression))
								return io.toString();
							else
								return new PropertyModel<String>(io, displayNameExpression).getObject();
						}
					}
				}));
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
						O mo = item.getModelObject();
						editingItem = item.getIndex();
						ItemBuilder.this.item = mo;
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
				editingItem = -1;
				creatingItem = true;
				item = getItem();
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
		form = new Form<O>("newItemForm", new CompoundPropertyModel<O>(new PropertyModel<O>(this, "item"))) {
			@Override
			public boolean isEnabled() {
				return editingItem != -1 || creatingItem;
			}
		};
		buildForm(form);
		wmc.add(form);
		// This
		add(wmc);
		add(new IValidator<List<O>>() {
			@Override
			public void validate(IValidatable<List<O>> validatable) {
				if (editingItem != -1 || creatingItem) {
					validatable.error(new RawValidationError(
							"New or updated item must be saved (i.e. 'Add' or 'Update' buttons must be pressed)."));
				}
			}
		});
	}

	@Override
	protected void onBeforeRender() {
		if (!hasErrorMessage()) {
			((WebMarkupContainer) get("selectionBuilder").get("items")).removeAll();
			List<O> modelObject = getModelObject();
			items = new ArrayList<O>();
			if (modelObject != null)
				items.addAll(modelObject);
			resetItemEdit();
		}
		super.onBeforeRender();
	}

	protected void onNew(AjaxRequestTarget target) {
	}

	protected void onEdit(AjaxRequestTarget target, O mo) {
	}

	protected void buildForm(Form<O> newItemForm) {
		newItemForm.add(new Label("itemDisplay", new Model<String>("Item")) {
			@Override
			public boolean isVisible() {
				return editingItem != -1 || creatingItem;
			}
		});
		newItemForm.add(new AjaxButton("saveItem") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				items.set(editingItem, item);
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
				items.add(item);
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

	@Override
	protected void convertInput() {
		setConvertedInput(new ArrayList<O>(items));
	}

	public void resetItemEdit() {
		editingItem = -1;
		creatingItem = false;
	}

	protected abstract O getItem();
}
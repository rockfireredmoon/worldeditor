package org.icemoon.worldeditor.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Reward;
import org.icemoon.worldeditor.ItemsPage;
import org.icemoon.worldeditor.SelectorPanel;
import org.icemoon.worldeditor.model.GameItemIconModel;
import org.icemoon.worldeditor.model.GameItemsModel;

@SuppressWarnings("serial")
public class RewardPanel extends FormComponentPanel<Reward> {
	private TextField<Integer> parm1Field;
	private CheckBox parm2Field;
	private int itemCount;
	private boolean itemRequired;
	private Long itemId;
	private SelectorPanel<Long, GameItem, String, IDatabase> selector;
	private boolean allowClear;
	private GameIconPanel iconPanel;
	private IModel<IDatabase> database;

	public RewardPanel(final String id, IModel<IDatabase> database) {
		super(id);
		this.database = database;
	}

	public RewardPanel(final String id, IModel<Reward> model, IModel<IDatabase> database) {
		super(id, model);
		this.database = database;
	}

	public final int getItemCount() {
		return itemCount;
	}

	public final void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public final boolean isItemRequired() {
		return itemRequired;
	}

	public final void setItemRequired(boolean itemRequired) {
		this.itemRequired = itemRequired;
	}

	public final Long getItemId() {
		return itemId;
	}

	public final void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Override
	protected void onInitialize() {
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		setType(Reward.class);
		WebMarkupContainer itemContainer = new WebMarkupContainer("itemContainer");
		final PropertyModel<Long> itemModel = new PropertyModel<Long>(this, "itemId");
		itemContainer.add(iconPanel = new GameIconPanel("rewardIcon", new GameItemIconModel(itemModel, "icon1", database),
				new GameItemIconModel(itemModel, "icon2", database), 24));
		iconPanel.setOutputMarkupId(true);
		itemContainer.add(selector = new SelectorPanel<Long, GameItem, String, IDatabase>("itemId", new Model<String>("Result"),
				new GameItemsModel(database), "displayName", new PropertyModel<Long>(this, "itemId"), GameItem.class, Long.class,
				ItemsPage.class) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, GameItem entity) {
				super.onEntitySelected(target, entity);
				target.add(parm1Field);
				target.add(parm2Field);
				target.add(iconPanel);
				onItemSelected(target, entity);
			}
		});
		selector.setShowLabel(true);
		selector.setShowClear(allowClear);
		parm1Field = new TextField<Integer>("itemCount", new PropertyModel<Integer>(this, "itemCount")) {
			@Override
			public boolean isEnabled() {
				return itemId != null;
			}
		};
		parm1Field.add(new RangeValidator<Integer>(1, Integer.MAX_VALUE));
		parm1Field.setRequired(true);
		parm1Field.setOutputMarkupId(true);
		itemContainer.add(parm1Field);
		parm2Field = new CheckBox("required", new PropertyModel<Boolean>(this, "itemRequired")) {
			@Override
			public boolean isEnabled() {
				return itemId != null;
			}
		};
		parm2Field.setOutputMarkupId(true);
		itemContainer.add(parm2Field);
		add(itemContainer);
		super.onInitialize();
	}

	protected void onItemSelected(AjaxRequestTarget target, GameItem entity) {
	}

	@Override
	protected void onBeforeRender() {
		Reward total = getModelObject();
		itemCount = total == null ? 1 : total.getItemCount();
		itemRequired = total == null ? false : total.isRequired();
		itemId = total == null ? null : total.getItemId();
		super.onBeforeRender();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(RewardPanel.class, "RewardPanel.css")));
	}

	@Override
	protected void convertInput() {
		final Integer n1 = parm1Field.getConvertedInput();
		final Boolean n2 = parm2Field.getConvertedInput();
		if (n1 != null && n2 != null) {
			final Reward reward = getModelObject();
			reward.setItemCount(n1);
			reward.setRequired(n2);
			reward.setItemId(itemId);
			setConvertedInput(reward);
		}
	}

	public void setAllowClear(boolean allowClear) {
		this.allowClear = allowClear;
		if (selector != null) {
			selector.setShowClear(allowClear);
		}
	}
}
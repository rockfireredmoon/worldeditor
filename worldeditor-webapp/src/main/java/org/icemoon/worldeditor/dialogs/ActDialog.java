package org.icemoon.worldeditor.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.icemoon.eartheternal.common.Act;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Reward;
import org.icemoon.worldeditor.LowASCIIValidator;
import org.icemoon.worldeditor.UnoptimizedDeepCopy;
import org.icemoon.worldeditor.components.InternalForm;
import org.icemoon.worldeditor.components.RewardPanel;
import org.odlabs.wiquery.ui.dialog.Dialog;

@SuppressWarnings("serial")
public class ActDialog extends FormComponentPanel<Act> {
	private Dialog actDialog;
	private Form<?> actForm;
	private Reward reward;
	private List<Reward> rewards = new ArrayList<Reward>();
	private RewardPanel rewardPanel;
	private IModel<IDatabase> database;

	public ActDialog(String id, IModel<String> title, IModel<Act> model, final IModel<IDatabase> database) {
		super(id, model);
		this.database = database;
		reward = new Reward(database.getObject());
		add(actDialog = new Dialog("actDialogWidget") {
			@Override
			public void close(AjaxRequestTarget ajaxRequestTarget) {
				super.close(ajaxRequestTarget);
				onDialogClose(ajaxRequestTarget);
			}
		});
		actDialog.setModal(true);
		actDialog.setTitle(title);
		actDialog.setWidth(620);
		// Feedback
		final FeedbackPanel feedbackPanel = new FeedbackPanel("actFeedback");
		feedbackPanel.setOutputMarkupId(true);
		// Text
		final TextArea<String> textArea = new TextArea<String>("text", new PropertyModel<String>(this, "modelObject.text"));
		textArea.add(new LowASCIIValidator());
		textArea.setType(String.class);
		textArea.setOutputMarkupId(true);
		textArea.setRequired(true);
		// Save Act
		Button saveAct = new AjaxButton("saveAct") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ActDialog.this.getModelObject().getRewards().clear();
				ActDialog.this.getModelObject().getRewards().addAll(rewards);
				actSaved(target);
				actDialog.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
			}
		};
		// Nested new reward form
		final Form<?> newRewardForm = new InternalForm<Object>("newRewardForm") {
			@Override
			public boolean isEnabled() {
				return rewards.size() < 4;
			}

			@Override
			public boolean isVisible() {
				Act act = ActDialog.this.getModel().getObject();
				if (act == null)
					return false;
				int idx = act.getQuest().getActs().indexOf(act);
				return idx == act.getQuest().getActs().size() - 1;
			}
		};
		final Button saveNewReward = new AjaxButton("saveNewReward") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (reward.getEntityId() == null) {
					reward.setEntityId(ActDialog.this.getModelObject().getNextRewardId());
				}
				rewards.add(reward);
				reward = new Reward(database.getObject());
				target.add(actForm);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
			}

			@Override
			public boolean isEnabled() {
				return rewardPanel.getItemId() != null && rewards.size() < 4;
			}
		};
		saveNewReward.setOutputMarkupId(true);
		newRewardForm.add(saveNewReward);
		newRewardForm.add(rewardPanel = new RewardPanel("newReward", new PropertyModel<Reward>(this, "reward"), database) {
			@Override
			protected void onItemSelected(AjaxRequestTarget target, GameItem entity) {
				super.onItemSelected(target, entity);
				target.add(saveNewReward);
			}
		});
		rewardPanel.setOutputMarkupId(true);
		// Form
		actForm = new InternalForm<Act>("actForm");
		actForm.setOutputMarkupId(true);
		actForm.add(feedbackPanel);
		actForm.add(textArea);
		actForm.add(new TextArea<String>("scriptAcceptCondition",
				new PropertyModel<String>(this, "modelObject.scriptAcceptCondition")).add(new LowASCIIValidator()));
		actForm.add(new TextArea<String>("scriptAcceptAction", new PropertyModel<String>(this, "modelObject.scriptAcceptAction")).add(new LowASCIIValidator()));
		actForm.add(new TextArea<String>("scriptCompleteCondition",
				new PropertyModel<String>(this, "modelObject.scriptCompleteCondition")).add(new LowASCIIValidator()));
		actForm.add(
				new TextArea<String>("scriptCompleteAction", new PropertyModel<String>(this, "modelObject.scriptCompleteAction")).add(new LowASCIIValidator()));
		actForm.add(newRewardForm);
		actForm.add(saveAct);
		// Rewards
		actForm.add(new WebMarkupContainer("noRewards") {
			@Override
			public boolean isVisible() {
				final Act act = getModelObject();
				return act == null ? true : rewards.size() == 0;
			}
		});
		final PropertyModel<List<Reward>> rewardsModel = new PropertyModel<List<Reward>>(this, "rewards");
		ListView<Reward> l = new ListView<Reward>("rewards", rewardsModel) {
			@Override
			protected void populateItem(final ListItem<Reward> item) {
				item.add(new AjaxLink<String>("removeReward") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						rewards.remove(item.getModelObject());
						target.add(actForm);
					}
				});
				item.add(new RewardPanel("reward", item.getModel(), database));
				item.setOutputMarkupId(true);
				item.setMarkupId("reward" + item.getIndex());
			}
		};
		actForm.add(l);
		actDialog.add(actForm);
		add(actDialog);
	}

	protected void onDialogClose(AjaxRequestTarget target) {
	}

	protected void actSaved(AjaxRequestTarget target) {
	}

	public void open(AjaxRequestTarget target) {
		final Act act = ActDialog.this.getModelObject();
		if (act != null) {
			rewards = (List<Reward>) UnoptimizedDeepCopy.copy(act.getRewards());
			reward = new Reward(database.getObject(), act);
		}
		target.add(actForm);
		actDialog.open(target);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new PackageResourceReference(ActDialog.class, "ActDialog.css")));
	}

	@Override
	protected void convertInput() {
		final Act modelObject = getModelObject();
		if (modelObject != null) {
			modelObject.getRewards().clear();
			modelObject.getRewards().addAll(rewards);
		}
	}
}
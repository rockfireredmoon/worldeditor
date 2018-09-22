package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.BipedAnimation;
import org.icemoon.eartheternal.common.Dialog;
import org.icemoon.eartheternal.common.Dialogs;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Dialog.DialogParagraph;
import org.icemoon.eartheternal.common.Dialog.DialogSequence;
import org.icemoon.eartheternal.common.Dialog.ParagraphType;
import org.icemoon.worldeditor.components.ConfirmDialog;

@SuppressWarnings("serial")
public class DialogsPage extends AbstractEntityPage<Dialog, String, String, Dialogs, IDatabase> {
	private int page = 0;
	private ConfirmDialog<Integer> deleteParagraphDialog;

	public DialogsPage() {
		super("entityId", String.class);
	}

	@Override
	protected void buildForm(final Form<Dialog> form) {
		WebMarkupContainer navContainer = new WebMarkupContainer("navContainer");
		navContainer.setOutputMarkupId(true);
		ListView<Integer> paragraphList = new ListView<Integer>("paragraphList",
				new PropertyModel<List<Integer>>(this, "paragraphNumbers")) {
			@Override
			protected void populateItem(final ListItem<Integer> item) {
				AjaxLink<Integer> l = new AjaxLink<Integer>("paragraphLink", item.getModel()) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						Integer pg = getModelObject();
						setParagraphNo(pg);
						target.add(getParent().getParent().getParent().getParent().get("editorContainer"));
						target.add(getParent().getParent().getParent().getParent().get("navContainer"));
					}
				};
				l.add(new AttributeModifier("class", new Model<String>() {
					@Override
					public String getObject() {
						return item.getModelObject() == getParagraphNo() ? "ui-button ui-state-active pageLink"
								: "ui-button ui-state-default pageLink";
					}
				}));
				l.add(new Label("paragraphLabel", new Model<Integer>(item.getModelObject())));
				item.add(l);
			}
		};
		paragraphList.setReuseItems(false);
		navContainer.add(paragraphList);
		WebMarkupContainer editorContainer = new WebMarkupContainer("editorContainer");
		editorContainer.setOutputMarkupId(true);
		editorContainer.add(new TextArea<String>("sayValue", new PropertyModel<String>(this, "value")) {
			@Override
			public boolean isVisible() {
				DialogParagraph para = getParagraph();
				return para != null && para.getType() == ParagraphType.SAY;
			}
		}.add(new LowASCIIValidator(false, false)));
		editorContainer.add(new DropDownChoice<BipedAnimation>("emoteValue", new Model<BipedAnimation>() {
			@Override
			public BipedAnimation getObject() {
				try {
					return BipedAnimation.valueOf(getValue());
				} catch (Exception nfe) {
					return null;
				}
			}

			@Override
			public void setObject(BipedAnimation val) {
				setValue(String.valueOf(val));
			}
		}, Arrays.asList(BipedAnimation.values())) {
			@Override
			public boolean isVisible() {
				DialogParagraph para = getParagraph();
				return para != null && para.getType() == ParagraphType.EMOTE;
			}
		}.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(form);
			}
		}));
		editorContainer.add(new TextField<Long>("waitValue", new Model<Long>() {
			@Override
			public Long getObject() {
				try {
					return Long.valueOf(getValue());
				} catch (NumberFormatException nfe) {
					return 0l;
				}
			}

			@Override
			public void setObject(Long val) {
				setValue(String.valueOf(val));
			}
		}, Long.class) {
			@Override
			public boolean isVisible() {
				DialogParagraph para = getParagraph();
				return para != null && para.getType() == ParagraphType.WAIT;
			}
		}.add(new RangeValidator<Long>(0l, Long.MAX_VALUE)));
		form.add(editorContainer);
		form.add(navContainer);
		form.add(new TextField<Long>("minInterval", Long.class).add(new RangeValidator<Long>(0l, Long.MAX_VALUE)));
		form.add(new TextField<Long>("maxInterval", Long.class).add(new RangeValidator<Long>(0l, Long.MAX_VALUE)));
		form.add(new DropDownChoice<DialogSequence>("sequence", Arrays.asList(DialogSequence.values()))
				.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						target.add(form);
					}
				}));
		editorContainer
				.add(new DropDownChoice<ParagraphType>("paragraphType", new PropertyModel<ParagraphType>(this, "paragraph.type"),
						Arrays.asList(ParagraphType.values())).add(new AjaxFormComponentUpdatingBehavior("onchange") {
							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								target.add(form);
							}
						}));
		form.add(new AjaxButton("newParagraph") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				defeatSave = true;
				getSelected().getParagraphs().add(new DialogParagraph());
				setParagraphNo(getSelected().getParagraphs().size());
				target.add(getParent().get("navContainer"));
				target.add(getParent().get("editorContainer"));
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		form.add(new AjaxButton("deleteParagraph") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				deleteParagraphDialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		deleteParagraphDialog = new ConfirmDialog<Integer>("confirmDeleteParagraphDialog",
				new PropertyModel<Integer>(this, "pageNo"), new Model<String>("Delete Paragraph"),
				new Model<String>("Are you sure you wish to delete this paragraph?")) {
			@Override
			protected void onConfirm(Integer original, AjaxRequestTarget target) {
				getSelected().getParagraphs().remove(original - 1);
				defeatSave = true;
				target.add(form.get("navContainer"));
				target.add(form.get("editorContainer"));
			}
		};
		add(deleteParagraphDialog);
	}

	public List<Integer> getParagraphNumbers() {
		List<Integer> l = new ArrayList<Integer>();
		final List<DialogParagraph> pages = getSelected().getParagraphs();
		for (int i = 1; i <= Math.max(pages.size(), 1); i++) {
			l.add(i);
		}
		return l;
	}

	public int getParagraphNo() {
		return Math.max(1, Math.min(page, getSelected().getParagraphs().size()));
	}

	public final void setParagraphNo(int page) {
		this.page = page;
	}

	public final DialogParagraph getParagraph() {
		final int pageNo = getParagraphNo();
		if (getSelected().getParagraphs().isEmpty() || pageNo > getSelected().getParagraphs().size())
			return null;
		else {
			return getSelected().getParagraphs().get(pageNo - 1);
		}
	}

	public final void setParagraph(DialogParagraph paragraph) {
		final List<DialogParagraph> paras = getSelected().getParagraphs();
		final int pageNo = getParagraphNo();
		if (pageNo <= paras.size())
			paras.set(pageNo - 1, paragraph);
	}

	public final String getValue() {
		final int pageNo = getParagraphNo();
		if (getSelected().getParagraphs().isEmpty() || pageNo > getSelected().getParagraphs().size())
			return "";
		else {
			return getSelected().getParagraphs().get(pageNo - 1).getValue();
		}
	}

	public final void setValue(String content) {
		final List<DialogParagraph> pages = getSelected().getParagraphs();
		final int pageNo = getParagraphNo();
		if (pageNo <= pages.size())
			pages.get(pageNo - 1).setValue(content == null ? "" : content);
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(DialogsPage.class, "DialogsPage.css")));
	}

	@Override
	protected Dialog createNewInstance() {
		final Dialog book = new Dialog(getDatabase());
		book.getParagraphs().add(new DialogParagraph());
		return book;
	}

	@Override
	public Dialogs getEntityDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase().getDialogs();
	}

	@Override
	protected void buildColumns(List<IColumn<Dialog, String>> columns) {
		// columns.add(new TextFilteredClassedPropertyColumn<Dialog, String>(new
		// ResourceModel("title"), "title", "title", "title"));
	}
}

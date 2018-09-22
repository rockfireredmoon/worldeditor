package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Appearance;
import org.icemoon.eartheternal.common.Book;
import org.icemoon.eartheternal.common.Books;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.CreatureCategory;
import org.icemoon.eartheternal.common.GameIcon;
import org.icemoon.eartheternal.common.GameIcons;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.Hint;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.ItemQuality;
import org.icemoon.eartheternal.common.AbstractMultiINIFileEntities.IDType;
import org.icemoon.eartheternal.common.Appearance.Name;
import org.icemoon.eartheternal.common.GameItem.BindingType;
import org.icemoon.eartheternal.common.GameItem.Type;
import org.icemoon.worldeditor.behaviors.CodeMirrorBehavior;
import org.icemoon.worldeditor.behaviors.CodeMirrorBehavior.DefaultCodeMirrorSettings;
import org.icemoon.worldeditor.components.ConfirmDialog;
import org.icemoon.worldeditor.components.GameIconPanel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class BooksPage extends AbstractEntityPage<Book, Long, String, Books, IDatabase> {
	private int page = 0;
	private ConfirmDialog<Integer> deletePageDialog;
	private boolean preview;
	private String icon1;
	private String icon2;
	private String prop;
	private String propDisplayName;
	private GameIconPanel iconPreview;
	private GameIconPanel icon1Preview;
	private GameIconPanel icon2Preview;
	private float propSize = 1;
	private Long itemId;
	private Long creatureId;
	private FormComponent<String> propNameComponent;
	private FormComponent<String> propDisplayNameComponent;
	private IconChooser iconChooser1;
	private IconChooser iconChooser2;

	public BooksPage() {
		super("entityId", Long.class);
	}

	@Override
	protected void buildForm(Form<Book> form) {
		addIdType(form);
		form.add(new TextField<String>("title").setRequired(true).setLabel(new Model<String>("Title")).add(new LowASCIIValidator()));
		DefaultCodeMirrorSettings codeMirrorSettings = new DefaultCodeMirrorSettings();
		codeMirrorSettings.setMode("xml");
		codeMirrorSettings.addAddOn("addon/display/fullscreen.js");
		codeMirrorSettings.addAddOn("addon/mode/multiplex.js");
		codeMirrorSettings.addCss("addon/display/fullscreen.css");
		codeMirrorSettings.getExtraKeys().put("F11",
				"function(cm) { cm.setOption(\"fullScreen\", !cm.getOption(\"fullScreen\"));}");
		codeMirrorSettings.getExtraKeys().put("Esc",
				"function(cm) { if (cm.getOption(\"fullScreen\")) cm.setOption(\"fullScreen\", false);}");
		codeMirrorSettings.setHighlightSelection(true);
		codeMirrorSettings.setLineWrapping(true);
		codeMirrorSettings.setTheme("abcdef");
		WebMarkupContainer navContainer = new WebMarkupContainer("navContainer");
		navContainer.setOutputMarkupId(true);
		ListView<Integer> pageList = new ListView<Integer>("pageList", new PropertyModel<List<Integer>>(this, "pageNumbers")) {
			@Override
			protected void populateItem(final ListItem<Integer> item) {
				AjaxLink<Integer> l = new AjaxLink<Integer>("pageLink", item.getModel()) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						Integer pg = getModelObject();
						setPageNo(pg);
						target.add(getParent().getParent().getParent().getParent().get("editorContainer"));
						target.add(getParent().getParent().getParent().getParent().get("navContainer"));
					}
				};
				l.add(new AttributeModifier("class", new Model<String>() {
					@Override
					public String getObject() {
						return item.getModelObject() == getPageNo() ? "ui-button ui-state-active pageLink"
								: "ui-button ui-state-default pageLink";
					}
				}));
				l.add(new Label("pageLabel", new Model<Integer>(item.getModelObject())));
				item.add(l);
			}
		};
		pageList.setReuseItems(false);
		navContainer.add(pageList);
		// <li wicket:id="pageLink">
		// <a wicket:id="pageLabel" class="pageLink">[Page]</a>
		// </li>
		WebMarkupContainer editorContainer = new WebMarkupContainer("editorContainer");
		editorContainer.setOutputMarkupId(true);
		editorContainer.add(new TextArea<String>("content", new PropertyModel<String>(this, "content")) {
			@Override
			public boolean isVisible() {
				return !preview;
			}
		}.add(new LowASCIIValidator()).add(new CodeMirrorBehavior(new Model<DefaultCodeMirrorSettings>(codeMirrorSettings))));
		editorContainer.add(new AjaxLink<String>("item") {
			@Override
			public boolean isVisible() {
				return editing && itemId != null;
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				PageParameters params = new PageParameters();
				params.set("id", itemId);
				setResponsePage(ItemsPage.class, params);
			}
		}.add(new Label("itemLabel", new PropertyModel<Long>(this, "itemId"))));
		editorContainer.add(new AjaxLink<String>("creature") {
			@Override
			public boolean isVisible() {
				return editing && creatureId != null;
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				PageParameters params = new PageParameters();
				params.set("id", creatureId);
				setResponsePage(CreaturesPage.class, params);
			}
		}.add(new Label("creatureLabel", new PropertyModel<Long>(this, "creatureId"))));
		WebMarkupContainer previewContainer = new WebMarkupContainer("previewContainer") {
			@Override
			public boolean isVisible() {
				return preview;
			}
		};
		previewContainer
				.add(new Label("previewContent", new PropertyModel<String>(this, "previewContent")).setEscapeModelStrings(false));
		editorContainer.add(previewContainer);
		form.add(editorContainer);
		form.add(navContainer);
		form.add(new CheckBox("preview", new PropertyModel<Boolean>(this, "preview")) {
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
		});
		form.add(new AjaxButton("newPage") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				defeatSave = true;
				getSelected().getPages().add("");
				setPageNo(getSelected().getPages().size());
				target.add(getParent().get("navContainer"));
				target.add(getParent().get("editorContainer"));
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		form.add(new AjaxButton("deletePage") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				deletePageDialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		WebMarkupContainer add = new WebMarkupContainer("additionalContainer");
		add.add(propNameComponent = new TextField<String>("prop", new PropertyModel<String>(this, "prop"), String.class).setRequired(true));
		add.add(propDisplayNameComponent = new TextField<String>("propDisplayName", new PropertyModel<String>(this, "propDisplayName"), String.class)
				.setRequired(true));
		add.add(new TextField<Float>("propSize", new PropertyModel<Float>(this, "propSize"), Float.class)
				.add(new RangeValidator<Float>(0f, 999f)));
		addIcons(add);
		form.add(add);
		setRequiredItems();
	}

	@Override
	protected void afterSave(Book book) {
		if (editing) {
			/* First remove items and creatures for pages we no longer have */
			Map<Long, GameItem> toRemove = new HashMap<Long, GameItem>();
			Map<Integer, GameItem> pageItems = new HashMap<Integer, GameItem>();
			for (GameItem gi : getDatabase().getItems().values()) {
				int bookId = gi.getDynamicMax(12);
				int bookPage = gi.getDynamicMax(11);
				if (gi.getType() == Type.SPECIAL && bookPage > -1 && bookId == book.getEntityId().intValue()) {
					pageItems.put(bookPage, gi);
					/* This is for the book, do we still have the page? */
					if (gi.getIvMax2() > book.getPages().size()) {
						toRemove.put(gi.getEntityId(), gi);
					}
				}
			}
			for (Creature c : getDatabase().getCreatures().values()) {
				if (c.getExtraData().containsKey("item")) {
					Long itemId = Long.parseLong(c.getExtraData().get("item"));
					if (toRemove.containsKey(itemId)) {
						getDatabase().getCreatures().delete(c);
						break;
					}
				}
			}
			/* Actually remove items */
			for (GameItem i : toRemove.values()) {
				getDatabase().getItems().remove(i);
			}
			/* Now add any pages that don't yet have items */
			for (int i = 0; i < book.getPages().size(); i++) {
				if (!pageItems.containsKey(i + 1)) {
					GameItem gi = createPageItem(book, i);
					createPageCreature(gi);
				}
			}
		} else {
			/*
			 * If creating a new book, just create a new item and creature for
			 * each page
			 */
			for (int i = 0; i < book.getPages().size(); i++) {
				GameItem gi = createPageItem(book, i);
				createPageCreature(gi);
			}
		}
	}

	protected GameItem createPageItem(Book book, int i) {
		GameItem gi = new GameItem(getDatabase());
		gi.setDisplayName(book.getTitle());
		gi.setType(Type.SPECIAL);
		gi.setFlavorText("Double-click to open, or use /books");
		gi.setIcon1(icon1);
		gi.setIcon2(icon2);
		gi.setLevel(1);
		gi.setValue(1);
		gi.setMinUseLevel(1);
		;
		gi.setQuality(ItemQuality.COMMON);
		gi.setBindingType(BindingType.PICKUP);
		gi.setIvType1(12);
		gi.setIvType2(11);
		gi.setIvMax1(book.getEntityId().intValue());
		gi.setIvMax2(i + 1);
		try {
			gi.setFile(VFS.getManager().resolveFile(getDatabase().getItems().getFile()).getParent().resolveFile("ItemDef_Books.txt")
					.getPublicURIString());
		} catch (FileSystemException e) {
			throw new RuntimeException("Failed to resolve item definition file.", e);
		}
		gi.setEntityId(getDatabase().getItems().getNextFreeId(null, IDType.HIGHEST, gi.getFile()));
		getDatabase().getItems().save(gi);
		return gi;
	}

	protected Creature createPageCreature(GameItem item) {
		Creature c = new Creature(getDatabase());
		c.setWillRegen(1);
		;
		c.setMightRegen(1);
		Appearance ap = new Appearance();
		ap.setName(Name.P1);
		ap.setAsset(prop);
		ap.setSize(propSize);
		c.setAppearance(ap);
		c.setLevel(1);
		c.setDisplayName(propDisplayName);
		c.setCreatureCategory(CreatureCategory.INANIMATE);
		c.setAiPackage("nothing");
		c.setHints(Arrays.asList(Hint.USABLE_SPARKLY, Hint.ITEM_GIVER));
		c.getExtraData().put("item", String.valueOf(item.getEntityId()));
		try {
			String fp = VFS.getManager().resolveFile(getDatabase().getCreatures().getFile()).getParent()
					.resolveFile("CreatureDef_Books.txt").getPublicURIString();
			c.setFile(fp);
		} catch (FileSystemException e) {
			throw new RuntimeException("Failed to resolve item definition file.", e);
		}
		c.setEntityId(getDatabase().getCreatures().getNextFreeId(null, IDType.HIGHEST, c.getFile()));
		getDatabase().getCreatures().save(c);
		return c;
	}

	protected void addIcons(WebMarkupContainer container) {
		final IModel<String> icon1Model = new PropertyModel<String>(this, "icon1");
		final IModel<String> icon2Model = new PropertyModel<String>(this, "icon2");
		IModel<GameIcons> gameIconsModel = new GameIconsModel();
		iconChooser1 = new IconChooser("icon1", new Model<String>("Icon 1"), gameIconsModel, icon1Model) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, GameIcon entity) {
				if (target != null) {
					target.add(iconPreview);
					target.add(icon1Preview);
				}
			}
		};
		iconChooser1.setOutputMarkupId(true);
		container.add(iconChooser1);
		container.add(iconPreview = new GameIconPanel("iconPreview", icon1Model, icon2Model, 32));
		iconPreview.setOutputMarkupId(true);
		container.add(icon1Preview = new GameIconPanel("icon1Preview", icon1Model, null));
		icon1Preview.setOutputMarkupId(true);
		container.add(icon2Preview = new GameIconPanel("icon2Preview", icon2Model, null));
		icon2Preview.setOutputMarkupId(true);
		iconChooser2 = new IconChooser("icon2", new Model<String>("Icon 2"), gameIconsModel, icon2Model) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, GameIcon entity) {
				if (target != null) {
					target.add(iconPreview);
					target.add(icon2Preview);
				}
			}
		};
		iconChooser2.setOutputMarkupId(true);
		container.add(iconChooser2);
		container.add(new AjaxLink<String>("swap") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				String o2 = icon2Model.getObject();
				icon2Model.setObject(icon1Model.getObject());
				icon1Model.setObject(o2);
				target.add(icon1Preview);
				target.add(iconChooser1);
				target.add(icon2Preview);
				target.add(iconChooser2);
				target.add(iconPreview);
			}
		});
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		deletePageDialog = new ConfirmDialog<Integer>("confirmDeletePageDialog", new PropertyModel<Integer>(this, "pageNo"),
				new Model<String>("Delete Page"), new Model<String>("Are you sure you wish to delete this page?")) {
			@Override
			protected void onConfirm(Integer original, AjaxRequestTarget target) {
				getSelected().getPages().remove(original - 1);
				defeatSave = true;
				target.add(form.get("navContainer"));
				target.add(form.get("editorContainer"));
			}
		};
		add(deletePageDialog);
	}

	@Override
	public void select(Book selected, int selectedIndex) {
		super.select(selected, selectedIndex);
		preview = false;
	}

	public String getPreviewContent() {
		TextArea<String> textArea = (TextArea<String>) form.get("editorContainer").get("content");
		return textArea != null && textArea.getInput() != null ? textArea.getInput().replace("\\\n", " ").replace("\n", "<br/>")
				: "";
	}

	@Override
	protected void onEdit() {
		resetItem();
		/*
		 * Look for the first item that has is this book. TODO more efficient
		 * lookup
		 */
		for (GameItem gi : getDatabase().getItems().values()) {
			if (gi.getType() == Type.SPECIAL && gi.getIvType1() == 11
					&& Long.valueOf(gi.getIvMax1()).equals(getSelected().getEntityId())) {
				icon1 = gi.getIcon1();
				icon2 = gi.getIcon2();
				itemId = gi.getEntityId();
				/*
				 * Now we know the item ID, we can search creatures for the
				 * creature def for the spawn
				 */
				for (Creature c : getDatabase().getCreatures().values()) {
					if (gi.getEntityId().toString().equals(c.getExtraData().get("item"))) {
						/* Found the spawn */
						prop = c.getAppearance().getProp();
						propSize = (float) c.getAppearance().getSize();
						propDisplayName = c.getDisplayName();
						creatureId = c.getEntityId();
						break;
					}
				}
				break;
			}
		}
		setRequiredItems();
	}

	@Override
	protected void onNew() {
		resetItem();
		setRequiredItems();
	}

	protected void resetItem() {
		prop = null;
		propSize = 1;
		icon1 = null;
		icon2 = null;
		propDisplayName = null;
		itemId = null;
		creatureId = null;
	}

	public List<Integer> getPageNumbers() {
		List<Integer> l = new ArrayList<Integer>();
		final List<String> pages = getSelected().getPages();
		for (int i = 1; i <= Math.max(pages.size(), 1); i++) {
			l.add(i);
		}
		return l;
	}

	public int getPageNo() {
		return Math.max(1, Math.min(page, getSelected().getPages().size()));
	}

	public final void setPageNo(int page) {
		this.page = page;
		Book sel = getSelected();
		itemId = null;
		creatureId = null;
		if (sel != null) {
			for (GameItem gi : getDatabase().getItems().values()) {
				if (gi.getType() == Type.SPECIAL && gi.getIvType1() == 11 && Long.valueOf(gi.getIvMax1()).equals(sel.getEntityId())
						&& gi.getIvMax2() == page) {
					itemId = gi.getEntityId();
					for (Creature c : getDatabase().getCreatures().values()) {
						if (gi.getEntityId().toString().equals(c.getExtraData().get("item"))) {
							creatureId = c.getEntityId();
							break;
						}
					}
					break;
				}
			}
		}
	}

	public final String getContent() {
		final int pageNo = getPageNo();
		if (getSelected().getPages().isEmpty() || pageNo > getSelected().getPages().size())
			return "";
		else {
			return getSelected().getPages().get(pageNo - 1);
		}
	}

	public final void setContent(String content) {
		final List<String> pages = getSelected().getPages();
		final int pageNo = getPageNo();
		if (pageNo <= pages.size())
			pages.set(pageNo - 1, content == null ? "" : content);
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(BooksPage.class, "BooksPage.css")));
	}

	@Override
	protected Book createNewInstance() {
		final Book book = new Book(getDatabase());
		book.getPages().add("");
		return book;
	}

	@Override
	public Books getEntityDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase().getBooks();
	}

	@Override
	protected void buildColumns(List<IColumn<Book, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<Book, String>(new ResourceModel("title"), "title", "title", "title"));
	}
	
	protected void setRequiredItems() {

		propNameComponent.setRequired(!editing);
		propDisplayNameComponent.setRequired(!editing);
		iconChooser2.setRequired(!editing);
		iconChooser1.setRequired(!editing);
	}
}

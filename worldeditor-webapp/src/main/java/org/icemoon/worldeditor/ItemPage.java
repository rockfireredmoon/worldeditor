package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.lang.Bytes;
import org.icemoon.eartheternal.common.Attachment;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GameItems;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.worldeditor.components.QuestsPanel;
import org.icemoon.worldeditor.components.UploadPanel;
import org.icemoon.worldeditor.model.EntityAvatarModel;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.odlabs.wiquery.ui.tabs.Tabs;

@SuppressWarnings("serial")
public class ItemPage extends AbstractAuthenticatedPage {
	protected Tabs tabs;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		// Session
		setDefaultModel(new Model<GameItem>() {
			public GameItem getObject() {
				return getEntityDatabase().get(getPageParameters().get("id").toLong());
			}
		});
		// Tabs
		tabs = new Tabs("tabs");
		addYou();
		add(tabs);
		addQuests();
	}

	protected void addQuests() {
		tabs.add(new QuestsPanel(new IModel<IDatabase>() {
			@Override
			public void detach() {
			}

			@Override
			public IDatabase getObject() {
				return Application.getAppSession(getRequestCycle()).getDatabase();
			}

			@Override
			public void setObject(IDatabase object) {
			}
		}, new ListModel<Long>() {
			@Override
			public List<Long> getObject() {
				return new ArrayList<Long>(
						Application.getAppSession(getRequestCycle()).getDatabase().getQuests().getQuestsWithItem(getModelObject()));
			}
		}));
	}

	@SuppressWarnings("unchecked")
	public IModel<GameItem> getModel() {
		return (IModel<GameItem>) getDefaultModel();
	}

	public GameItem getModelObject() {
		return getModel().getObject();
	}

	protected GameItems getEntityDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase().getItems();
	}

	protected void addYou() {
		add(new NonCachingImage("avatar", new EntityAvatarModel(getModel())));
		add(new Label("displayName", new PropertyModel<String>(getModel(), "displayName")));
		add(new Label("level", new PropertyModel<Integer>(getModel(), "level")));
		add(new Label("quality", new PropertyModel<Integer>(getModel(), "quality")));
		final Dialog uploadAvatarDialog = new Dialog("uploadPhotoDialog") {
		};
		uploadAvatarDialog.setWidth(400);
		uploadAvatarDialog.setTitle("Upload Avatar");
		uploadAvatarDialog.add(new UploadPanel("uploadPhotoPanel", new Model<Attachment>() {
			@Override
			public Attachment getObject() {
				return AttachmentUtil.getAttachment(getModelObject(), Application.getApp().getSiteData());
			}

			public void setObject(Attachment attachment) {
				attachment.getAttachments().save(attachment);
			}
		}, Bytes.kilobytes(500), new Model<String>() {
			public String getObject() {
				return "Please choose a file to upload as an avatar for " + ItemPage.this.getModelObject().getDisplayName()
						+ " . The file should be " + "no bigger than 500K.";
			}
		}));
		add(uploadAvatarDialog);
		add(new AjaxLink<String>("uploadPhoto") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				uploadAvatarDialog.open(target);
			}
		});
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		super.onRenderHead(response);
		response.render(CssHeaderItem.forReference(new PackageResourceReference(ItemPage.class, "ItemPage.css")));
	}
}

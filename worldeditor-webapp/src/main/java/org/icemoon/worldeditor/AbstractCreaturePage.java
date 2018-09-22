package org.icemoon.worldeditor;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Bytes;
import org.icemoon.eartheternal.common.Attachment;
import org.icemoon.eartheternal.common.BaseCreature;
import org.icemoon.eartheternal.common.BaseCreatures;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.worldeditor.components.UploadPanel;
import org.icemoon.worldeditor.model.EntityAvatarModel;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.odlabs.wiquery.ui.tabs.Tabs;

@SuppressWarnings("serial")
public abstract class AbstractCreaturePage<T extends BaseCreature, D extends BaseCreatures<T, R>, R extends IRoot> extends AbstractAuthenticatedPage {
	protected Tabs tabs;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		// Session
		setDefaultModel(new Model<T>() {
			public T getObject() {
				return getEntityDatabase().get(getPageParameters().get("id").toLong());
			}
		});
		// Tabs
		tabs = new Tabs("tabs");
		addYou();
		add(tabs);
		addQuests();
	}

	protected abstract void addQuests();

	public IModel<T> getModel() {
		return (IModel<T>) getDefaultModel();
	}

	public T getModelObject() {
		return getModel().getObject();
	}

	protected abstract D getEntityDatabase();

	protected void addYou() {
		add(new NonCachingImage("avatar", new EntityAvatarModel(getModel())));
		add(new Label("displayName", new PropertyModel<String>(getModel(), "displayName")));
		add(new Label("level", new PropertyModel<Integer>(getModel(), "level")));
		add(new Label("profession", new PropertyModel<Integer>(getModel(), "profession")));
		add(new Label("gender", new PropertyModel<String>(getModel(), "appearance.gender")));
		add(new Label("race", new PropertyModel<String>(getModel(), "appearance.race")));
		add(new Image("professionImage", new Model<ResourceReference>() {
			public ResourceReference getObject() {
				return new PackageResourceReference(AbstractCreaturePage.class,
						getModelObject().getProfession().name().toLowerCase() + ".png");
			}
		}));
		final Dialog uploadAvatarDialog = new Dialog("uploadAvatarDialog") {
		};
		uploadAvatarDialog.setWidth(400);
		uploadAvatarDialog.setTitle("Upload Avatar");
		uploadAvatarDialog.add(new UploadPanel("uploadAvatarPanel", new Model<Attachment>() {
			@Override
			public Attachment getObject() {
				return AttachmentUtil.getAttachment(getModelObject(), Application.getApp().getSiteData());
			}

			public void setObject(Attachment attachment) {
				attachment.getAttachments().save(attachment);
			}
		}, Bytes.kilobytes(500), new Model<String>() {
			public String getObject() {
				return "Please choose a file to upload as an avatar for "
						+ AbstractCreaturePage.this.getModelObject().getDisplayName() + " . The file should be "
						+ "no bigger than 500K.";
			}
		}));
		add(uploadAvatarDialog);
		add(new AjaxLink<String>("uploadAvatar") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				uploadAvatarDialog.open(target);
			}
		});
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		super.onRenderHead(response);
		response.render(
				CssHeaderItem.forReference(new PackageResourceReference(AbstractCreaturePage.class, "AbstractCreaturePage.css")));
	}
}

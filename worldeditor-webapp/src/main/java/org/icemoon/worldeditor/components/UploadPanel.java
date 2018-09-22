package org.icemoon.worldeditor.components;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.lang.Bytes;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.Attachment;
import org.icemoon.eartheternal.common.Attachment.AttachedFile;
import org.icemoon.worldeditor.Application;

@SuppressWarnings("serial")
public class UploadPanel extends Panel {

	private Bytes maxSize = Bytes.kilobytes(3000);
	private final IModel<String> text;

	public UploadPanel(String id, IModel<Attachment> attachment) {
		this(id, attachment, Bytes.kilobytes(500), null);
	}

	public UploadPanel(String id, IModel<Attachment> attachment, final Bytes maxSize, IModel<String> text) {
		super(id, attachment);
		this.maxSize = maxSize;
		if (text == null) {
			text = new Model<String>() {
				public String getObject() {
					return "Please choose a file to upload. It may not be bigger than " + maxSize.kilobytes() + "K";
				}
			};
		}
		this.text = text;
	}

	@SuppressWarnings("unchecked")
	public IModel<Attachment> getModel() {
		return (IModel<Attachment>) getDefaultModel();
	}

	public Attachment getModelObject() {
		return getModel().getObject();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
		final FileUploadForm progressUploadForm = new FileUploadForm(maxSize, "progressUpload");
		progressUploadForm.add(new UploadProgressBar("progress", progressUploadForm, progressUploadForm.fileUploadField));
		add(progressUploadForm);
		add(uploadFeedback);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(UploadPanel.class, "UploadPanel.css")));
	}

	private class FileUploadForm extends Form<Void> {
		FileUploadField fileUploadField;

		public FileUploadForm(Bytes maxSize, String name) {
			super(name);
			setMultiPart(true);
			add(fileUploadField = new FileUploadField("fileInput"));
			add(new Label("uploadText", text));
			setMaxSize(maxSize);
		}

		@Override
		protected void onSubmit() {
			final List<FileUpload> uploads = fileUploadField.getFileUploads();
			if (uploads != null) {
				Attachment att = UploadPanel.this.getModelObject();
				for (FileUpload upload : uploads) {
					Principal account = Application.getAppSession(getRequestCycle()).getUser();
					AttachedFile newFile = att.newAttachment(account.getName(), upload.getClientFileName(),
						upload.getContentType(), upload.getSize());
					try {
						FileObject uploadTo = VFS.getManager().resolveFile(newFile.getFile());
						OutputStream out = uploadTo.getContent().getOutputStream(false);
						try {
							InputStream in = upload.getInputStream();
							try {
								IOUtils.copy(in, out);
							} finally {
								in.close();
							}
						} finally {
							out.close();
						}
						UploadPanel.this.getModel().setObject(att);
					} catch (Exception e) {
						throw new IllegalStateException("Unable to write file", e);
					}
				}
			}
		}
	}
}

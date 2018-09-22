package org.icemoon.worldeditor;

import java.text.MessageFormat;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.components.FooterPanel;
import org.icemoon.worldeditor.components.HeaderPanel;
import org.icemoon.worldeditor.entities.ActiveUser;
import org.odlabs.wiquery.ui.core.CoreUIJavaScriptResourceReference;

@SuppressWarnings("serial")
public abstract class AbstractPage extends WebPage {
	@Override
	protected void onInitialize() {
		add(new Label("pageTitle", new Model<String>() {
			@Override
			public String getObject() {
				return MessageFormat.format(getPage().getString("pageTitle"),
						Util.decamel(getPage().getClass().getSimpleName().replaceFirst("Page$", "")), true);
			}
		}));
		ActiveUser ae = Application.getAppSession(getRequestCycle()).getActiveUser();
		if (ae != null) {
			ae.setEditingEntity(null);
			ae.setPage(getPageClass());
		}
		super.onInitialize();
		createHeader();
		createFooter();
	}

	public IDatabase getDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase();
	}

	public IUserData getUserData() {
		return Application.getApp().getUserData();
	}

	protected void createFooter() {
		add(new FooterPanel());
	}

	protected void createHeader() {
		add(new HeaderPanel());
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		response.render(UnescapedMetaDataHeaderItem.forMetaTag("Content-Security-Policy", "default-src 'self';"));
		response.render(CssHeaderItem.forReference(new CssResourceReference(AbstractPage.class, "AbstractPage.css")));
		response.render(JavaScriptHeaderItem.forReference(CoreUIJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "jquery.qtip.js")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "jquery.qtip.css")));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "AbstractPage.js")));
		onRenderHead(response);
	}

	protected void onRenderHead(IHeaderResponse response) {
	}
}

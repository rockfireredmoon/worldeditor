package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public class FooterPanel extends Panel {

	public FooterPanel() {
		super("footer");
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		// response.renderCSSReference(new
		// PackageResourceReference(FooterPanel.class, "FooterPanel.css"));
	}
}

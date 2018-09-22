package org.icemoon.worldeditor.player;

import org.icemoon.worldeditor.AbstractAuthenticatedPage;
import org.icemoon.worldeditor.AppSession;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.ZoneDefsPage;

@SuppressWarnings("serial")
public class AbstractUserPage extends AbstractAuthenticatedPage {

	@Override
	protected void onBeforeRender() {
		AppSession appSession = Application.getAppSession(getRequestCycle());
		if (appSession.isAdmin()) {
			setResponsePage(ZoneDefsPage.class);
		}
		super.onBeforeRender();
	}
}

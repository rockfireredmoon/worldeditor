package org.icemoon.worldeditor;

import org.apache.wicket.RestartResponseAtInterceptPageException;

@SuppressWarnings("serial")
public abstract class AbstractAuthenticatedPage extends AbstractPage {
	private boolean needsStore = true;

	public final boolean isNeedsStore() {
		return needsStore;
	}

	public final void setNeedsStore(boolean needsStore) {
		this.needsStore = needsStore;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final AppSession appSession = Application.getAppSession(getRequestCycle());
		if (!appSession.isAuthenticated()) {
			throw new RestartResponseAtInterceptPageException(LoginPage.class);
		} else if (appSession.isAuthenticated()) {
			checkForStore(appSession);
		}
	}

	protected void checkForStore(final AppSession appSession) {
		if (needsStore) {
			try {
				appSession.getDatabase();
			} catch (IllegalArgumentException iae) {
				throw new RestartResponseAtInterceptPageException(DatabaseStoresPage.class);
			} catch (IllegalStateException iae) {
				throw new RestartResponseAtInterceptPageException(DatabaseStoresPage.class);
			}
		}
	}
}

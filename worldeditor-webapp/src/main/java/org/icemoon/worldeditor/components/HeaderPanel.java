package org.icemoon.worldeditor.components;

import java.security.Principal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.Account.Permission;
import org.icemoon.worldeditor.AppSession;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.Place;
import org.icemoon.worldeditor.ZoneDefsPage;
import org.icemoon.worldeditor.player.HomePage;

@SuppressWarnings("serial")
public class HeaderPanel extends Panel {
	private Link<String> signoutLink;
	private Link<String> link;

	public HeaderPanel() {
		super("header");
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(signoutLink = new Link<String>("signOut") {
			@Override
			public void onClick() {
				final AppSession appSession = Application.getAppSession(getRequestCycle());
				appSession.signOut();
				setResponsePage(Application.get().getHomePage());
			}
		});
		signoutLink.add(new Label("signOutText", new Model<String>() {
			@Override
			public String getObject() {
				AppSession sess = Application.getAppSession(getRequestCycle());
				if (sess.isAuthenticated())
					if (StringUtils.isBlank(sess.getDatabaseId()))
						return "Sign Out " + sess.getUsername();
					else
						return "Sign Out " + sess.getUsername() + "@" + sess.getDatabaseId();
				else
					return "Sign In";
			}
		}));
		add(new Menu("menu", new PropertyModel<List<Place>>(this, "session.places")));
		link = new Link<String>("switch") {
			@Override
			public void onClick() {
				final boolean admin = Application.getAppSession(getRequestCycle()).isAdmin();
				Application.getAppSession(getRequestCycle()).setAdmin(!admin);
				if (admin) {
					setResponsePage(HomePage.class);
				} else {
					setResponsePage(ZoneDefsPage.class);
				}
			}
		};
		link.add(new Label("switchLabel", new Model<String>() {
			public String getObject() {
				return getLocalizer().getString(
						Application.getAppSession(getRequestCycle()).isAdmin() ? "switchToGame" : "switchToAdmin",
						HeaderPanel.this);
			}
		}));
		add(link);
	}

	@Override
	protected void onBeforeRender() {
		signoutLink.setVisible(Application.getAppSession(getRequestCycle()).isAuthenticated());
		final Principal account = Application.getAppSession(getRequestCycle()).getUser();
		link.setVisible(
				account != null && account instanceof Account && ((Account) account).getPermissions().contains(Permission.ADMIN));
		super.onBeforeRender();
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new PackageResourceReference(HeaderPanel.class, "HeaderPanel.css")));
	}
}

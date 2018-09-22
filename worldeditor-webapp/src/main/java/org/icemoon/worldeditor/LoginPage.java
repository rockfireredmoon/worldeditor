package org.icemoon.worldeditor;

import java.security.Principal;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.Account.Permission;

@SuppressWarnings("serial")
public class LoginPage extends AbstractPage {
	private String username;
	private String password;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Form<Object> form = new Form<Object>("loginForm");
		TextField<String> usernameField = new TextField<String>("username", new PropertyModel<String>(this, "username"));
		form.add(usernameField.setRequired(true));
		PasswordTextField passwordField = new PasswordTextField("password", new PropertyModel<String>(this, "password"));
		form.add(passwordField.setRequired(true));
		Button loginButton = new Button("login") {
			@Override
			public void onSubmit() {
				if (!Application.getAppSession(getRequestCycle()).authenticate(username, password)) {
					error("Login failed.");
				} else {
					Principal acc = Application.getAppSession(getRequestCycle()).getUser();
					continueToOriginalDestination();
					if (acc instanceof Account && ((Account) acc).getPermissions().contains(Permission.ADMIN)) {
						setResponsePage(ZoneDefsPage.class);
					} else {
						setResponsePage(Application.get().getHomePage());
					}
				}
			}
		};
		form.add(loginButton);
		add(new FeedbackPanel("loginFeedback"));
		add(form);
	}

	@Override
	protected void createFooter() {
		// TODO better way (abstact to "AbstractAuthenticatedPage"?)
	}

	@Override
	protected void createHeader() {
		// TODO better way (abstact to "AbstractAuthenticatedPage"?)
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		super.onRenderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(LoginPage.class, "LoginPage.css")));
	}
}

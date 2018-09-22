package org.icemoon.worldeditor;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.lang.Objects;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.entities.DatabaseStore;
import org.icemoon.worldeditor.entities.DatabaseStores;
import org.icemoon.worldeditor.entities.DatabaseStore.Source;
import org.icemoon.worldeditor.entities.DatabaseStore.Status;
import org.icemoon.worldeditor.table.ClassedPropertyColumn;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class DatabaseStoresPage extends AbstractEntityPage<DatabaseStore, String, String, DatabaseStores, IRoot> {
	public String gitUsername;
	public String gitPassword;
	public String message;
	public String email;

	public DatabaseStoresPage() {
		super("entityId", String.class);
		setNeedsStore(false);
	}

	@Override
	protected void onNew() {
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		if (getEntityDatabase().size() == 0)
			error("You do not have any database stores configured. Before you can use this editor, you must "
					+ "point it to a copy of the Earth Eternal game data. You can either choose a 'local' "
					+ "data directory (i.e. located where this web server is located), or you may provide a "
					+ "Git location. The remote repository will be retrieved to a local working copy (which "
					+ "you may then periodically synchronize your changes and remote changes with to and from the master repository)");
		else {
			AppSession sess = Application.getAppSession(getRequestCycle());
			if (sess.getDatabaseId() == null || getEntityDatabase().get(sess.getDatabaseId()) == null) {
				error("You do not have an active database store. You must choose one below and active it "
						+ "before you can edit the entities it contains.");
			} else {
				DatabaseStore store = getEntityDatabase().get(sess.getDatabaseId());
				switch (store.getStatus()) {
				case EMPTY:
					error(String.format(
							"Your active database store is '%s', but it is empty. You should user the 'Check Out' button pull the game data from the location before you can work on it.",
							sess.getDatabaseId()));
					break;
				case ERROR:
					error(String.format("Your active database store is '%s', but there is an error with it. Please see the log.",
							sess.getDatabaseId()));
					break;
				case INCOMING_CHANGES:
					info(String.format(
							"Your active database store is '%s', and there are INCOMING changes available. You must pull (retrieve) these before you can push your own changes.",
							sess.getDatabaseId()));
					break;
				case OUTGOING_CHANCES:
					info(String.format(
							"Your active database store is '%s', and you have changes waiting to be commited and pushed to the remote store..",
							sess.getDatabaseId()));
					break;
				case UNPUSHED_CHANGES:
					error(String.format(
							"Your active database store is '%s', and you have changes waiting to be pushed to the remote store. This may be "
									+ "the result of a failed push (incorrect credentials perhaps?). You can try again by pressing the Push button again",
							sess.getDatabaseId()));
					break;
				case MISSING:
					error(String.format("Your active database store is '%s', but it is missing. Please check configuration.",
							sess.getDatabaseId()));
					break;
				case UP_TO_DATE:
					info(String.format("Your active database store is '%s', and it is up-to-date.", sess.getDatabaseId()));
					break;
				default:
					break;
				}
			}
		}
	}

	protected ProgressMonitor createProgressMonitor() {
		return new TextProgressMonitor();
	}

	@Override
	protected void buildForm(Form<DatabaseStore> form) {
		form.add(new Button("activate") {
			@Override
			public boolean isEnabled() {
				AppSession sess = Application.getAppSession(getRequestCycle());
				return sess != null && !Objects.equal(getSelected().getEntityId(), sess.getDatabaseId());
			}

			@Override
			public void onSubmit() {
				AppSession sess = Application.getAppSession(getRequestCycle());
				sess.setDatabaseId(getSelected().getEntityId());
			}
		}.setDefaultFormProcessing(false));
		form.add(new Button("pull") {
			@Override
			public boolean isVisible() {
				return getSelected().getStatus() == Status.INCOMING_CHANGES;
			}

			@Override
			public void onSubmit() {
				try {
					getSelected().pull(createProgressMonitor());
				} catch (Exception e) {
					e.printStackTrace();
					error("Failed to pull. " + e.getMessage());
				}
			}
		});
		form.add(new Button("push") {
			@Override
			public boolean isVisible() {
				return getSelected().getStatus() == Status.UNPUSHED_CHANGES;
			}

			@Override
			public void onSubmit() {
				try {
					getSelected().push(gitUsername, gitPassword, createProgressMonitor());
				} catch (Exception e) {
					e.printStackTrace();
					error("Failed to pull. " + e.getMessage());
				}
			}
		});
		form.add(new Button("commitPush") {
			@Override
			public boolean isVisible() {
				return getSelected().getStatus() == Status.OUTGOING_CHANCES;
			}

			@Override
			public void onSubmit() {
				try {
					getSelected().commitAndPush(message, Application.getAppSession(getRequestCycle()).getUsername(), email,
							gitUsername, gitPassword, createProgressMonitor());
				} catch (Exception e) {
					e.printStackTrace();
					error("Failed to pull. " + e.getMessage());
				}
			}
		});
		form.add(new Button("checkout") {
			@Override
			public boolean isVisible() {
				return getSelected().getStatus() == Status.EMPTY;
			}

			@Override
			public void onSubmit() {
				try {
					getSelected().checkout(createProgressMonitor());
					info(getSelected().getEntityId() + " has been cloned.");
				} catch (Exception e) {
					e.printStackTrace();
					error("Failed to checkout. " + e.getMessage());
				}
			}
		});
		form.add(new TextField<String>("url") {
			@Override
			public boolean isEnabled() {
				return !editing;
			}
		});
		form.add(new TextField<String>("branch") {
			@Override
			public boolean isEnabled() {
				return !editing && getSelected().getSource() == Source.GIT;
			}
		});
		form.add(new TextArea<String>("message", new PropertyModel<String>(this, "message")) {
			@Override
			public boolean isRequired() {
				return getSelected().getStatus() == Status.OUTGOING_CHANCES;
			}
		});
		form.add(new TextField<String>("creator").setEnabled(false));
		form.add(new ListChoice<Source>("source", Arrays.asList(Source.values())) {
			@Override
			public boolean isEnabled() {
				return !editing;
			}

			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
		}.setMaxRows(1).setRequired(true));
		form.add(new TextField<String>("gitUsername", new PropertyModel<String>(this, "gitUsername")) {
			@Override
			public boolean isVisible() {
				return getSelected().getStatus() == Status.OUTGOING_CHANCES || getSelected().getStatus() == Status.UNPUSHED_CHANGES;
			}
		}.setRequired(true));
		form.add(new TextField<String>("email", new PropertyModel<String>(this, "email")));
		form.add(new PasswordTextField("gitPassword", new PropertyModel<String>(this, "gitPassword")) {
			@Override
			public boolean isRequired() {
				return getSelected().getStatus() == Status.OUTGOING_CHANCES || getSelected().getStatus() == Status.UNPUSHED_CHANGES;
			}
		});
		form.add(new WebMarkupContainer("upToDate") {
			@Override
			public boolean isVisible() {
				return getSelected().getStatus() == Status.UP_TO_DATE;
			}
		});
		form.add(new WebMarkupContainer("available") {
			@Override
			public boolean isVisible() {
				return getSelected().getStatus() == Status.AVAILABLE;
			}
		});
		form.add(new Image("githubLogo", new PackageResourceReference(DatabaseStoresPage.class, "GitHub-Mark-Light-64px.png")));
	}

	protected void buildColumns(List<IColumn<DatabaseStore, String>> columns) {
		columns.add(
				new TextFilteredClassedPropertyColumn<DatabaseStore, String>(new ResourceModel("column.url"), "url", "url", "url"));
		columns.add(new ClassedPropertyColumn<DatabaseStore>(new ResourceModel("column.status"), "status", "status"));
	}

	protected boolean entityMatches(DatabaseStore object, DatabaseStore filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getUrl(), filter.getUrl())) {
			return false;
		}
		return true;
	}

	@Override
	protected void additionalValidation() {
		super.additionalValidation();
		if (!editing) {
			if (getSelected().getSource() == Source.LOCAL) {
				// Make sure local directory exists
				try {
					FileObject fo = VFS.getManager().resolveFile(getSelected().getUrl());
					if (!fo.getType().equals(FileType.FOLDER)) {
						form.error(String.format("%s is not a folder.", getSelected().getUrl()));
					}
				} catch (Exception e) {
					form.error("Invalid local URL. " + e.getMessage());
				}
			}
		}
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(DatabaseStoresPage.class, "DatabaseStoresPage.css")));
	}

	@Override
	protected DatabaseStore createNewInstance() {
		final DatabaseStore databaseStore = new DatabaseStore(null, Application.getApp().getDatabaseStores());
		databaseStore.setCreator(Application.getAppSession(getRequestCycle()).getUsername());
		return databaseStore;
	}

	@Override
	public DatabaseStores getEntityDatabase() {
		return Application.getApp().getDatabaseStores();
	}
}

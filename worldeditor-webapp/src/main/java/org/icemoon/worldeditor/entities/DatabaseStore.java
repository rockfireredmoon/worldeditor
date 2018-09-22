package org.icemoon.worldeditor.entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.icemoon.eartheternal.common.AbstractMultiINIFileEntity;
import org.icemoon.eartheternal.common.DefaultDatabase;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.INIWriter;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.eartheternal.common.Log;

public class DatabaseStore extends AbstractMultiINIFileEntity<String, IRoot> {
	private static final long serialVersionUID = 1L;
	private static final long CHECK_INTERVAL = 120000;

	public enum Status {
		EMPTY, MISSING, INCOMING_CHANGES, OUTGOING_CHANCES, UNPUSHED_CHANGES, UP_TO_DATE, AVAILABLE, ERROR
	}

	public enum Source {
		LOCAL, GIT
	}

	private String url;
	private String branch;
	private String creator;
	private Source source;
	private DatabaseStores stores;
	private long lastRemoteStatusTime;
	private Status lastRemoteStatus;

	public DatabaseStore() {
		this(null, null);
	}

	public DatabaseStore(IRoot root, DatabaseStores stores) {
		super(root);
		this.stores = stores;
	}

	public Status getStatus() {
		if (source != null) {
			final FileObject root = getRoot();
			switch (source) {
			case LOCAL:
				try {
					return root != null && root.getType() == FileType.FOLDER ? Status.AVAILABLE : Status.MISSING;
				} catch (FileSystemException fse) {
					return Status.ERROR;
				}
			case GIT:
				try {
					if (root != null && root.isFolder()) {
						if (lastRemoteStatus == null || System.currentTimeMillis() > lastRemoteStatusTime + CHECK_INTERVAL) {
							try {
								lastRemoteStatus = getRemoteStatus();
							} finally {
								lastRemoteStatusTime = System.currentTimeMillis();
							}
						}
						return lastRemoteStatus;
					} else
						return Status.EMPTY;
				} catch (Exception fse) {
					fse.printStackTrace();
				}
			default:
				break;
			}
		}
		return Status.ERROR;
	}

	private Repository getRepository() throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.setWorkTree(getWorkspaceDir(getRoot()));
		return builder.build();
	}

	private Status getRemoteStatus() throws Exception {
		Git git = new Git(getRepository());
		try {
			FetchCommand fetch = git.fetch();
			fetch.setProgressMonitor(new TextProgressMonitor());
			fetch.call();
			// Check the log, if there are entries, there is stuff incoming
			ProcessBuilder pb = new ProcessBuilder("git", "log",
					"..origin/" + (StringUtils.isNotBlank(branch) ? branch : "master"));
			pb.directory(getWorkspaceDir(getRoot()));
			pb.redirectErrorStream(true);
			Process p = pb.start();
			InputStream s = p.getInputStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(s));
			int line = 0;
			String t = null;
			while ((t = r.readLine()) != null) {
				line++;
			}
			IOUtils.copy(s, System.out);
			try {
				int ret = p.waitFor();
				if (ret != 0)
					throw new IOException("Command failed to exit code " + ret);
			} catch (InterruptedException ie) {
				throw new IOException("Failed command.", ie);
			}
			if (line > 0) {
				return Status.INCOMING_CHANGES;
			}
			org.eclipse.jgit.api.Status status = git.status().call();
			if (status.hasUncommittedChanges()) {
				return Status.OUTGOING_CHANCES;
			}
			// Check for unpushed
			final String range = "origin/" + (StringUtils.isNotBlank(branch) ? branch : "master") + ".."
					+ (StringUtils.isNotBlank(branch) ? branch : "master");
			pb = new ProcessBuilder("git", "log", range);
			pb.directory(getWorkspaceDir(getRoot()));
			pb.redirectErrorStream(true);
			p = pb.start();
			s = p.getInputStream();
			r = new BufferedReader(new InputStreamReader(s));
			line = 0;
			t = null;
			while ((t = r.readLine()) != null) {
				line++;
			}
			IOUtils.copy(s, System.out);
			try {
				int ret = p.waitFor();
				if (ret != 0)
					throw new IOException("Command failed to exit code " + ret);
			} catch (InterruptedException ie) {
				throw new IOException("Failed command.", ie);
			}
			if (line > 0) {
				return Status.UNPUSHED_CHANGES;
			}
			return Status.UP_TO_DATE;
		} finally {
			git.close();
		}
	}

	protected File getWorkspaceDir(FileObject fo) throws IOException {
		if (!fo.getName().getScheme().equals("file"))
			throw new IOException("Can only checkout to a local file system.");
		File parent = new File(fo.getName().getPath());
		return parent;
	}

	public FileObject getRoot() {
		if (source != null) {
			switch (source) {
			case LOCAL:
				try {
					return VFS.getManager().resolveFile(getUrl());
				} catch (Exception fse) {
				}
				break;
			case GIT:
				try {
					return VFS.getManager().resolveFile(stores.getWorkspace()).resolveFile(getEntityId());
				} catch (Exception fse) {
				}
			}
		}
		return null;
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("ID")) {
			setEntityId(value);
		} else if (name.equals("URL")) {
			url = value;
		} else if (name.equals("Branch")) {
			branch = value;
		} else if (name.equals("Source")) {
			source = Source.valueOf(value);
		} else if (name.equals("Creator")) {
			creator = value;
		} else if (!name.equals("")) {
			Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
		}
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("ID=" + getEntityId());
		writer.println("Source=" + source);
		writer.println("URL=" + url);
		writer.println("Creator=" + creator);
		if (StringUtils.isNotBlank(branch))
			writer.println("Branch=" + branch);
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final String getBranch() {
		return branch;
	}

	public final void setBranch(String branch) {
		this.branch = branch;
	}

	public final String getCreator() {
		return creator;
	}

	public final void setCreator(String creator) {
		this.creator = creator;
	}

	public final Source getSource() {
		return source;
	}

	public final void setSource(Source source) {
		this.source = source;
	}

	public IDatabase createDatabase() {
		final FileObject root = getRoot();
		if (root != null)
			return new DefaultDatabase(root);
		return null;
	}

	public void checkout(ProgressMonitor monitor) throws Exception {
		FileObject root = VFS.getManager().resolveFile(stores.getWorkspace() + "/" + getEntityId());
		if (root.exists())
			throw new IOException("Cannot checkout, root " + root + " already exists.");
		if (!root.getName().getScheme().equals("file"))
			throw new IOException("Can only checkout to a local file system.");
		File parent = new File(root.getName().getPath());
		if (!parent.exists()) {
			if (!parent.mkdirs()) {
				throw new IOException("Could not create parent directory.");
			}
		}
		CloneCommand clone = new CloneCommand();
		clone.setDirectory(parent);
		clone.setURI(url);
		if (StringUtils.isNotBlank(branch)) {
			clone.setBranch(branch);
		}
		clone.setProgressMonitor(monitor);
		clone.call();
	}

	public void pull(ProgressMonitor monitor) throws Exception {
		Git git = new Git(getRepository());
		try {
			PullCommand fetch = git.pull();
			fetch.setProgressMonitor(monitor);
			fetch.call();
		} finally {
			git.close();
			lastRemoteStatus = null;
		}
	}

	public void push(String username, String password, ProgressMonitor monitor) throws Exception {
		// TODO git add files (deletions / changes ok as handled by commit -a)
		Git git = new Git(getRepository());
		try {
			doPush(username, password, monitor, git);
		} finally {
			git.close();
			lastRemoteStatus = null;
		}
	}

	public void commitAndPush(String message, String author, String authorEmail, String username, String password,
			ProgressMonitor monitor) throws Exception {
		// TODO git add files (deletions / changes ok as handled by commit -a)
		Git git = new Git(getRepository());
		try {
			CommitCommand commit = git.commit();
			commit.setAll(true);
			commit.setCommitter(author, authorEmail);
			commit.setMessage(message);
			commit.call();
			doPush(username, password, monitor, git);
		} finally {
			git.close();
			lastRemoteStatus = null;
		}
	}

	protected void doPush(String username, String password, ProgressMonitor monitor, Git git)
			throws GitAPIException, InvalidRemoteException, TransportException {
		PushCommand push = git.push();
		System.out.println(">> " + username + " password: " + password);
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(username, password.toCharArray());
		push.setCredentialsProvider(cp);
		push.setProgressMonitor(monitor);
		push.call();
	}
}
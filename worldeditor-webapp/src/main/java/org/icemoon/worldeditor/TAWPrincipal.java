package org.icemoon.worldeditor;

import java.io.Serializable;
import java.util.List;

import org.icemoon.eartheternal.common.EEPrincipal;
import org.icemoon.eartheternal.common.Account.Permission;

@SuppressWarnings("serial")
public class TAWPrincipal implements EEPrincipal, Serializable {
	private final String name;
	private final String sessId;
	private final String sessionName;
	private final String uid;
	private final String xcsrfToken;
	private final List<Permission> perms;

	public TAWPrincipal(String name, String xcsrfToken, String sessId, String sessionName, String uid, List<Permission> perms) {
		this.name = name;
		this.uid = uid;
		this.xcsrfToken = xcsrfToken;
		this.sessId = sessId;
		this.sessionName = sessionName;
		this.perms = perms;
	}

	public final String getSessId() {
		return sessId;
	}

	public final String getSessionName() {
		return sessionName;
	}

	public final String getUid() {
		return uid;
	}

	public final String getXcsrfToken() {
		return xcsrfToken;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Permission> getPermissions() {
		return perms;
	}
}
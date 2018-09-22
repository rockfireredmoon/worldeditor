package org.icemoon.worldeditor.entities;

import java.io.IOException;
import java.security.Principal;

import org.apache.wicket.Page;
import org.icemoon.eartheternal.common.AbstractEntity;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IRoot;

public class ActiveUser extends AbstractEntity<String, IRoot> {
	private static final long serialVersionUID = 1L;
	private Principal user;
	private Class<? extends Page> page;
	private Entity<?> editingEntity;

	public ActiveUser() {
		this(null);
	}
	public ActiveUser(IRoot root) {
		super(root);
	}

	public final Class<? extends Page> getPage() {
		return page;
	}

	public final void setPage(Class<? extends Page> page) {
		this.page = page;
	}

	public final Entity<?> getEditingEntity() {
		return editingEntity;
	}

	public final void setEditingEntity(Entity<?> editingEntity) {
		this.editingEntity = editingEntity;
	}

	public final Principal getUser() {
		return user;
	}

	public final void setUser(Principal user) {
		this.user = user;
	}

	@Override
	protected void doLoad() throws IOException {
	}
}
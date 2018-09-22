package org.icemoon.worldeditor.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.wicket.Page;
import org.icemoon.eartheternal.common.AbstractEntities;
import org.icemoon.eartheternal.common.IRoot;

@SuppressWarnings("serial")
public class ActiveUsers extends AbstractEntities<ActiveUser, String, String, IRoot> {
	public ActiveUsers() {
		this(null);
	}

	public ActiveUsers(IRoot database) {
		super(database, String.class);
	}

	@Override
	protected ActiveUser createItem() {
		return new ActiveUser(getDatabase());
	}

	@Override
	protected void doLoad() throws IOException {
		// TODO Auto-generated method stub
	}

	public List<ActiveUser> getOtherPageUsers(ActiveUser activeUser, Class<? extends Page> pageClass) {
		List<ActiveUser> l = new ArrayList<ActiveUser>();
		for (ActiveUser au : values) {
			if (!Objects.equals(activeUser, au) && au != null && pageClass.equals(au.getPage())) {
				l.add(au);
			}
		}
		return l;
	}
}

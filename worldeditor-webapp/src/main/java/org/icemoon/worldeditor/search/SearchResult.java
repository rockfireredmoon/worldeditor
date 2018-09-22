package org.icemoon.worldeditor.search;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.IResource;
import org.icemoon.eartheternal.common.Util;

public interface SearchResult extends Comparable<SearchResult>, Serializable {

	public enum ResultType {
		CHARACTER, ACCOUNT, QUEST, CREATURE;

		public String toString() {
			switch (this) {
			case CHARACTER:
				return "Player Character";
			case ACCOUNT:
				return "Player Account";
			case CREATURE:
				return "Non-player Character";
			default:
				return Util.toEnglish(this.name(), true);
			}
		}
	}

	IModel<IResource> getImage();

	String getDisplayName();

	int getMatch();
	
	ResultType getResultType();

	String getDescription();

	String getSubtext();

	void activate(Component component);
}

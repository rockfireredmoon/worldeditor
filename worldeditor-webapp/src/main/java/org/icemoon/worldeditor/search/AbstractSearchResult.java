package org.icemoon.worldeditor.search;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.IResource;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.worldeditor.model.EntityAvatarModel;

@SuppressWarnings("serial")
public abstract class AbstractSearchResult<T extends Entity<?>> implements SearchResult {
	private int matchProbability;
	private T entity;
	private ResultType resultType;

	public AbstractSearchResult(T entity, ResultType resultType) {
		this(entity, 100, resultType);
	}

	public AbstractSearchResult(T entity, int matchProbability, ResultType resultType) {
		this.entity = entity;
		this.matchProbability = matchProbability;
		this.resultType = resultType;
	}

	@Override
	public ResultType getResultType() {
		return resultType;
	}

	@Override
	public String getDisplayName() {
		return getEntity().toString();
	}

	public T getEntity() {
		return entity;
	}

	@Override
	public int getMatch() {
		return matchProbability;
	}

	public final void setMatchProbability(int matchProbability) {
		this.matchProbability = matchProbability;
	}

	@Override
	public int compareTo(SearchResult o) {
		int i = Integer.valueOf(getMatch()).compareTo(Integer.valueOf(o.getMatch()));
		return i == 0 ? getDisplayName().compareTo(o.getDisplayName()) : i;
	}

	@Override
	public IModel<IResource> getImage() {
		return new EntityAvatarModel(new Model<T>(entity));
	}
}

package org.icemoon.worldeditor.search;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.icemoon.eartheternal.common.Entity;

public abstract class AbstractEntityIterator<T extends Entity<?>>  implements Iterator<SearchResult> {

	private String search;
	private Iterator<T> iterator;
	private T entity;
	private Boolean match;
	private int matchProbability;

	public AbstractEntityIterator(String search, Iterator<T> iterator) {
		this.search = search;
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {
		if (match == null) {
			match = checkMatch();
		}
		return match;
	}
	
	protected T getEntity() {
		return entity;
	}

	protected boolean checkMatch() {
		while (true) {
			if (entity == null) {
				if (!iterator.hasNext()) {
					return false;
				}
				entity = iterator.next();
			}
			matchProbability = matches(search);
			if (matchProbability> -1) {
				return true;
			} else {
				entity = null;
			}
		}
	}

	protected abstract int matches(String search);

	@Override
	public SearchResult next() {
		if (match == null) {
			match = checkMatch();
			if (!match) {
				throw new NoSuchElementException();
			}
		}
		SearchResult result = createResult(matchProbability, entity);
		entity = null;
		match = null;
		return result;
	}

	protected abstract SearchResult createResult(final int matchProbability, final T character);

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}

package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.icemoon.eartheternal.common.AbstractMultiINIFileEntities.IDType;

public interface Entities<T extends Entity<K>, K extends Serializable, L extends Serializable, R extends IRoot> extends Entity<L> {
	T cloneEntity(T original);

	boolean contains(K key);

	void delete(T instance);

	T get(K key);

	@Override
	L getEntityId();

	K getFirstId(String file);

	K getLastId(String file);

	K getNextFreeId(K start, IDType idType, String file);

	List<T> getValuesByFile(String file);

	int indexOf(T entity);

	boolean isEmpty();

	Set<K> keySet();

	void remove(T instance);

	void save(T instance);

	int size();

	List<T> values();

}
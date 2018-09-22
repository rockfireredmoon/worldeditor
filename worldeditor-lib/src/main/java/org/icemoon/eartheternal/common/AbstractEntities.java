package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.icemoon.eartheternal.common.AbstractMultiINIFileEntities.IDType;

@SuppressWarnings("serial")
public abstract class AbstractEntities<T extends Entity<K>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends AbstractEntity<L, R> implements Serializable, Entities<T, K, L, R> {
	
	protected TreeMap<K, T> map = new TreeMap<K, T>();
	protected List<T> values = new ArrayList<T>();
	protected Map<String, List<T>> valuesByFile = new HashMap<String, List<T>>();
	protected List<T> duplicates = new ArrayList<T>();
	private boolean collectDuplicates;
	private Class<K> keyClass;
	private K minId;
	private K maxId;

	public AbstractEntities(R database, Class<K> keyClass) {
		super(database);
		this.keyClass = keyClass;
	}

	public AbstractEntities(R database, Class<K> keyClass, String file) {
		super(database);
		this.keyClass = keyClass;
		setFile(file);
	}

	public final boolean isCollectDuplicates() {
		return collectDuplicates;
	}

	public final void setCollectDuplicates(boolean collectDuplicates) {
		this.collectDuplicates = collectDuplicates;
	}

	public final List<T> getDuplicates() {
		return duplicates;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T cloneEntity(T original) {
		checkLoad();
		if (original.getEntityId() == null) {
			throw new IllegalArgumentException("Origina ID is null.");
		}
		T newEntity;
		try {
			newEntity = (T) original.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		newEntity.setEntityId(getNextFreeId(original.getEntityId(), IDType.HIGHEST, original.getFile()));
		newEntity.setFile(original.getFile());
		return newEntity;
	}

	@Override
	public final boolean contains(K key) {
		checkLoad();
		return map.get(key) != null;
	}

	@Override
	public void delete(T instance) {
		checkLoad();
		remove(instance);
	}

	@Override
	public T get(K key) {
		checkLoad();
		final T t = map.get(key);
		if (t != null && !t.isLoaded()) {
			t.load();
		}
		return t;
	}

	@Override
	public L getEntityId() {
		L id = super.getEntityId();
		if (id == null) {
			// TODO
			// return FilenameUtils.getBaseName(getFile());
			return null;
		}
		return id;
	}

	@Override
	public K getFirstId(String file) {
		checkLoad();
		if (file == null && !map.isEmpty()) {
			return map.keySet().iterator().next();
		} else {
			for (Map.Entry<K, T> en : map.entrySet()) {
				if (en.getValue().getFile().equals(file)) {
					return en.getKey();
				}
			}
		}
		return null;
	}

	@Override
	public K getLastId(String file) {
		checkLoad();
		List<K> l = new ArrayList<K>();
		if (file == null)
			l.addAll(map.keySet());
		else {
			for (Map.Entry<K, T> en : map.entrySet()) {
				if (en.getValue().getFile().equals(file)) {
					l.add(en.getKey());
				}
			}
		}
		Collections.reverse(l);
		return l.size() == 0 ? null : l.iterator().next();
	}

	protected K getMinId() {
		return minId == null ? getDefaultId() : minId;
	}
	
	protected void setMinId(K minId) {
		this.minId = minId;
	}
	
	protected void setMaxId(K maxId) {
		this.maxId = maxId;
	}

	@SuppressWarnings("unchecked")
	protected K getDefaultId() {
		if (keyClass.equals(Integer.class)) {
			return (K) Integer.valueOf(0);
		} else if (keyClass.equals(Long.class)) {
			return (K) Long.valueOf(0);
		} else if (keyClass.equals(String.class)) {
			return (K) "";
		}
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	protected K getMaxId() {
		if(maxId != null)
			return maxId;
		else if (keyClass.equals(Integer.class)) {
			return (K) Integer.valueOf(Integer.MAX_VALUE);
		} else if (keyClass.equals(Long.class)) {
			return (K) Long.valueOf(Long.MAX_VALUE);
		} else if (keyClass.equals(String.class)) {
			return (K) "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";
		}
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	protected K increment(K value, int amount) {
		if (keyClass.equals(Integer.class)) {
			return (K) Integer.valueOf((Integer) value + amount);
		} else if (keyClass.equals(Long.class)) {
			return (K) Long.valueOf((Long) value + amount);
		} else if (keyClass.equals(String.class)) {
			return (K) "";
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public K getNextFreeId(K start, IDType idType, String file) {
		checkLoad();
		int incr = 1;
		switch (idType) {
		case FIRST:
			start = getFirstId(file);
			break;
		case LOWEST:
			start = getFirstId(file);
			incr = -1;
			break;
		case LAST:
			start = getLastId(file);
			incr = -1;
			break;
		case HIGHEST:
			start = getLastId(file);
			break;
		default:
			break;
		}
		if (start == null) {
			start = getDefaultId();
		}
		K min = getMinId();
		K max = getMaxId();
		while (start != null && map.containsKey(start)) {
			if (Objects.equals(start, min) && incr < 0) {
				return null;
			}
			if (Objects.equals(start, max) && incr > 0) {
				return null;
			}
			start = increment(start, incr);
		}
		if (start == null)
			start = getMinId();
		return start;
	}

	@Override
	public List<T> getValuesByFile(String file) {
		checkLoad();
		final List<T> vbf = valuesByFile.get(file);
		return vbf == null ? null : new ProxiedList<T>(vbf);
	}

	@Override
	public int indexOf(T entity) {
		checkLoad();
		return values.indexOf(entity);
	}

	@Override
	public final boolean isEmpty() {
		checkLoad();
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		checkLoad();
		return map.keySet();
	}

	@Override
	public void load() {
		checkLoad();
	}

	@Override
	public void remove(T instance) {
		checkLoad();
		onRemove(instance);
		map.remove(instance.getEntityId());
		values.remove(instance);
		if (instance.getFile() != null) {
			List<T> valuesByFile = this.valuesByFile.get(instance.getFile());
			if (valuesByFile != null) {
				valuesByFile.remove(instance);
			}
		}
	}
	
	protected void onRemove(T instance) {
	}

	@Override
	public void save(T instance) {
		checkLoad();
		replace(instance);
	}

	@Override
	public final int size() {
		checkLoad();
		return values.size();
	}

	@Override
	public final List<T> values() {
		checkLoad();
		return getValues();
	}

	protected final boolean shouldRemove(T entity, String file) {
		return entity.getFile() != null && entity.getFile().equals(file);
	}

	protected final void add(T entity) {
		if (!entity.isLoaded())
			entity.load();
		if (values.contains(entity)) {
			K newId = getNextNewId();
			if (collectDuplicates)
				duplicates.add(entity);
			throw new IllegalArgumentException(
					entity.getEntityId() + " (" + entity + ") already exists. The suggested next ID is " + newId);
		}
		onAdd(entity);
		map.put(entity.getEntityId(), entity);
		values.add(entity);
		if (entity.getFile() != null) {
			List<T> valuesByFile = this.valuesByFile.get(entity.getFile());
			if (valuesByFile == null) {
				valuesByFile = new ArrayList<T>();
				this.valuesByFile.put(entity.getFile(), valuesByFile);
			}
			valuesByFile.add(entity);
		}
	}

	protected void onAdd(T entity) {
	}

	protected K getNextNewId() {
		/**
		 * Subclasses can override if they can provide their own sequential ID
		 * (e.g. props). This will be used when duplicates are detected, and
		 * when new entities are created on demand
		 */
		return null;
	}

	protected abstract T createItem();

	protected final List<T> getValues() {
		checkLoad();
		return new ProxiedList<T>(values);
	}

	protected final void replace(T entity) {
		checkLoad();
		map.put(entity.getEntityId(), entity);
		if (values.contains(entity)) {
			values.set(values.indexOf(entity), entity);
		} else {
			values.add(entity);
		}
		if (entity.getFile() != null) {
			List<T> valuesByFile = this.valuesByFile.get(entity.getFile());
			if (valuesByFile == null) {
				valuesByFile = new ArrayList<T>();
				this.valuesByFile.put(entity.getFile(), valuesByFile);
			}
			if (valuesByFile.contains(entity)) {
				valuesByFile.set(valuesByFile.indexOf(entity), entity);
			} else {
				valuesByFile.add(entity);
			}
		}
	}

	private static final class ProxiedList<E extends Entity<?>> implements List<E> {
		private List<E> values;

		ProxiedList(List<E> values) {
			this.values = values;
		}

		@Override
		public void add(int index, E element) {
			values.add(index, element);
		}

		@Override
		public boolean add(E e) {
			return values.add(e);
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			return values.addAll(c);
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			return values.addAll(index, c);
		}

		@Override
		public void clear() {
			values.clear();
		}

		@Override
		public boolean contains(Object o) {
			return values.contains(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return values.containsAll(c);
		}

		@Override
		public E get(int index) {
			E t = values.get(index);
			checkLoad(t);
			return t;
		}

		@Override
		public int indexOf(Object o) {
			return values.indexOf(o);
		}

		@Override
		public boolean isEmpty() {
			return values.isEmpty();
		}

		@Override
		public Iterator<E> iterator() {
			final Iterator<E> it = values.iterator();
			return new Iterator<E>() {
				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public E next() {
					E t = it.next();
					checkLoad(t);
					return t;
				}

				@Override
				public void remove() {
					it.remove();
				}
			};
		}

		@Override
		public int lastIndexOf(Object o) {
			return values.lastIndexOf(o);
		}

		@Override
		public ListIterator<E> listIterator() {
			return new ProxyListIterator<E>(values.listIterator());
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			return new ProxyListIterator<E>(values.listIterator(index));
		}

		@Override
		public E remove(int index) {
			E t = values.remove(index);
			checkLoad(t);
			return t;
		}

		@Override
		public boolean remove(Object o) {
			return values.remove(o);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return values.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return values.retainAll(c);
		}

		@Override
		public E set(int index, E element) {
			E t = values.set(index, element);
			checkLoad(t);
			return t;
		}

		@Override
		public int size() {
			return values.size();
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			List<E> l = values.subList(fromIndex, toIndex);
			for (E t : l) {
				checkLoad(t);
			}
			return l;
		}

		@Override
		public Object[] toArray() {
			Object[] l = values.toArray();
			loadArray(l);
			return l;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			T[] arr = values.toArray(a);
			loadArray(arr);
			return arr;
		}

		private void checkLoad(E t) {
			if (t != null && !t.isLoaded()) {
				t.loadIfNotLoaded();
			}
		}

		@SuppressWarnings("unchecked")
		private void loadArray(Object[] a) {
			for (Object t : a) {
				checkLoad((E) t);
			}
		}

		private final class ProxyListIterator<K> implements ListIterator<E> {
			private final ListIterator<E> it;

			private ProxyListIterator(ListIterator<E> it) {
				this.it = it;
			}

			@Override
			public void add(E e) {
				it.add(e);
			}

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public boolean hasPrevious() {
				return it.hasPrevious();
			}

			@Override
			public E next() {
				E t = it.next();
				checkLoad(t);
				return t;
			}

			@Override
			public int nextIndex() {
				return it.nextIndex();
			}

			@Override
			public E previous() {
				E t = it.previous();
				checkLoad(t);
				return t;
			}

			@Override
			public int previousIndex() {
				return it.previousIndex();
			}

			@Override
			public void remove() {
				it.remove();
			}

			@Override
			public void set(E e) {
				it.set(e);
			}
		}
	}
}

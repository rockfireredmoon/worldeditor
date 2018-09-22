package org.icemoon.eartheternal.common;

public interface DuplicateHandler<T> {
	void duplicate(T original, T duplicate);
}
package org.icemoon.eartheternal.common;

public interface IRoot {
	
	<T> DuplicateHandler<T> getDuplicateHandler(Class<T> clazz);
	
	void setDuplicateHandler(Class<?> clazz, DuplicateHandler<?> handler);
}

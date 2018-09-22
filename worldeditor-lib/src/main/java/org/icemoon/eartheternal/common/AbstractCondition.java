package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public abstract class AbstractCondition implements Condition {
	@Override
	public String toString() {
		return Util.decamel(getClass().getSimpleName());
	}
}

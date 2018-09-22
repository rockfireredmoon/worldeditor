package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class InstanceScript extends AbstractScript<Long> {
	public InstanceScript() {
		this(null);
	}
	public InstanceScript(IDatabase database) {
		super(database);
	}
}

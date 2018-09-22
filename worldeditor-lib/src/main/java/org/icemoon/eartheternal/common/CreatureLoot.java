package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CreatureLoot extends AbstractINIFileEntity<Long, IDatabase> {
	private List<String> packages = new ArrayList<String>();

	public CreatureLoot() {
		this(null);
	}

	public CreatureLoot(IDatabase database) {
		super(database);
	}

	public CreatureLoot(IDatabase database, Long id) {
		super(database, null, id);
	}

	public List<String> getPackages() {
		return packages;
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("CreatureDefID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equals("Package")) {
			packages.add(value);
		} else if (!name.equals("")) {
			Log.todo("CreatureLoot", "Unhandled property " + name + " = " + value);
		}
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	@Override
	public String toString() {
		return String.valueOf(getEntityId());
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("CreatureDefID=" + getEntityId());
		for (String lpi : packages) {
			writer.println("Package=" + lpi);
		}
	}

	@Override
	protected void doLoad() throws IOException {
		// TODO Auto-generated method stub
	}
}

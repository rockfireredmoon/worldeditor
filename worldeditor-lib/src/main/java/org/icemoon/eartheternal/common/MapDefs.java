package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MapDefs extends AbstractMultiINIFileEntities<MapDef, String, String, IDatabase> {

	public MapDefs(String file) {
		this(null, file);
	}
	
	public MapDefs(IDatabase database, String file) {
		super(database, String.class, file);
	}

	public List<MapDef> getByPrimary(String primary) {
		List<MapDef> a = new ArrayList<MapDef>();
		for (MapDef def : values()) {
			if (def.getPrimary().equals(primary)) {
				a.add(def);
			}
		}
		return a;
	}

	@Override
	protected MapDef createItem() {
		return new MapDef(getDatabase());
	}
}

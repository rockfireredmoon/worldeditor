package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GameItems extends AbstractMultiINIFileEntities<GameItem, Long, String, IDatabase> {

	public GameItems(String... file) {
		this(null, file);
	}
	
	public GameItems(IDatabase database, String... file) {
		super(database, Long.class, file);
		setMinId(1l);
		setMaxId(Long.valueOf(Integer.MAX_VALUE));
	}

	public List<GameItem> getByLevel(int level) {
		List<GameItem> l = new ArrayList<GameItem>();
		for (GameItem t : values()) {
			if (t.getLevel() == level) {
				l.add(t);
			}
		}
		return l;
	}

	@Override
	protected GameItem createItem() {
		return new GameItem(getDatabase());
	}

	@Override
	protected Long extractMaxId(String k, String v, Long max, IDType type) {
		if (k.equals("mID")) {
			long val = Long.parseLong(v);
			switch (type) {
			case HIGHEST:
				if (max == null || val > max)
					max = val;
				break;
			case LOWEST:
				if (max == null || val < max)
					max = val;
				break;
			}
		}
		return max;
	}
}

package org.icemoon.eartheternal.common;

import java.util.List;

import org.icemoon.eartheternal.common.Appearance.Race;

@SuppressWarnings("serial")
public class Creatures extends AbstractMultiINIFileEntities<Creature, Long, String, IDatabase> implements BaseCreatures<Creature, IDatabase> {
	private CreaturesSupport<Creature> cs = new CreaturesSupport<Creature>();

	public Creatures(String... file) {
		this(null, file);
	}
	
	public Creatures(IDatabase database, String... file) {
		super(database, Long.class, file);
		setMinId(1l);
		setMaxId(Long.valueOf(Integer.MAX_VALUE));
	}

	@Override
	public Creature getByDisplayName(String displayName) {
		return cs.getByDisplayName(displayName, values());
	}

	@Override
	public List<Creature> getByLevel(int level) {
		return cs.getByLevel(level, values());
	}

	@Override
	public List<Creature> getByRace(Race r) {
		return cs.getByRace(r, values);
	}

	@Override
	public List<Creature> getBySubName(String subName) {
		return cs.getBySubName(subName, values());
	}

	@Override
	protected Creature createItem() {
		return new Creature(getDatabase());
	}

	protected Long extractMaxId(String k, String v, Long max, IDType type) {
		if (k.equals("ID")) {
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

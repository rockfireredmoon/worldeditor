package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Quests extends AbstractMultiINIFileEntities<Quest, Long, String, IDatabase> {
	public Quests(String... files) {
		this(null, files);
	}
	
	public Quests(IDatabase database, String... files) {
		super(database, Long.class, files);
		setMinId(1l);
		setMaxId(Long.valueOf(Integer.MAX_VALUE));
	}

	public List<Quest> getByOpenLevelExcludingBB(int i) {
		List<Quest> l = new ArrayList<Quest>();
		for (Quest q : this.values()) {
			if (!q.isBounty() && q.getLevel() == i) {
				l.add(q);
			}
		}
		return l;
	}

	public List<Quest> getLeadsTo(Quest object) {
		List<Quest> l = new ArrayList<Quest>();
		for (Quest q : getValues()) {
			if (q.getRequires() != null && q.getRequires().equals(object.getEntityId())) {
				l.add(q);
			}
		}
		return l;
	}

	public List<Long> getQuestsWithCreature(Creature modelObject) {
		List<Long> l = new ArrayList<Long>();
		for (Quest q : values()) {
			if (q.containsCreature(modelObject)) {
				l.add(q.getEntityId());
			}
		}
		return l;
	}

	public List<Long> getQuestsWithItem(GameItem modelObject) {
		List<Long> l = new ArrayList<Long>();
		for (Quest q : values()) {
			if (q.containsItem(modelObject)) {
				l.add(q.getEntityId());
			}
		}
		return l;
	}

	public List<Quest> getRootQuests() {
		List<Quest> r = new ArrayList<Quest>();
		for (Quest quest : getValues()) {
			if (quest.getRequires() == null) {
				r.add(quest);
			}
		}
		return r;
	}

	@Override
	protected Quest createItem() {
		return new Quest(getDatabase());
	}

	@Override
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

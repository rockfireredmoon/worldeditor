package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public class Quest extends AbstractMultiINIFileEntity<Long, IDatabase> {
	private String title;
	private String bodyText;
	private String completeText;
	private int level;
	private int suggestedLevel = 0;
	private int maxLevel = 0;
	private long exp;
	private int partySize = 1;
	private int numRewards;
	private long coin;
	private boolean unabandon;
	private Long giverId;
	private Long enderId;
	private boolean repeat;
	private TimeUnit repeatDelayTimeUnit = TimeUnit.MINUTES;
	private long repeatDelayAmount;
	private Location giverLocation = new Location();
	private Location enderLocation = new Location();
	private List<Act> acts = new ArrayList<Act>();
	private Long requires;
	private Act newAct;
	private Profession profession = null;
	private int heroism;
	private boolean accountQuest;

	public Quest() {
		this(null);
	}

	public Quest(IDatabase database) {
		super(database);
	}

	public boolean containsItem(GameItem item) {
		for (Act a : getActs()) {
			for (Objective o : a.getObjectives()) {
				if (item.getEntityId().equals(o.getItemId())) {
					return true;
				}
				if (o.getType().equals(ObjectiveType.GATHER)) {
					if (o.getData1().contains(item.getEntityId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean containsCreature(Creature creature) {
		if (creature.getEntityId().equals(getGiverId()) || creature.getEntityId().equals(getEnderId())) {
			return true;
		}
		for (Act a : getActs()) {
			for (Objective o : a.getObjectives()) {
				if (creature.getEntityId().equals(o.getCreatureId())) {
					return true;
				}
				if (o.getType().equals(ObjectiveType.TALK)) {
					if (o.getData2().contains(creature.getEntityId())) {
						return true;
					}
				} else if (o.getType().isActivateType()) {
					if (o.getData1().contains(creature.getEntityId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public final TimeUnit getRepeatDelayTimeUnit() {
		return repeatDelayTimeUnit;
	}

	public final void setRepeatDelayTimeUnit(TimeUnit repeatDelayTimeUnit) {
		this.repeatDelayTimeUnit = repeatDelayTimeUnit;
	}

	public final long getRepeatDelayAmount() {
		return repeatDelayAmount;
	}

	public final void setRepeatDelayAmount(long repeatDelayAmount) {
		this.repeatDelayAmount = repeatDelayAmount;
	}

	public Act getAct(Integer id) {
		for (Act a : acts) {
			if (a.getEntityId().equals(id)) {
				return a;
			}
		}
		return null;
	}

	public final List<Act> getActs() {
		return acts;
	}

	public final String getBodyText() {
		return bodyText;
	}

	public final long getCoin() {
		return coin;
	}

	public final String getCompleteText() {
		return completeText;
	}

	public final Long getEnderId() {
		return enderId;
	}

	public final Location getEnderLocation() {
		return enderLocation;
	}

	public final long getExp() {
		return exp;
	}

	public final Long getGiverId() {
		return giverId;
	}

	public final Location getGiverLocation() {
		return giverLocation;
	}

	public final int getHeroism() {
		return heroism;
	}

	public final int getLevel() {
		return level;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public Integer getNextActId() {
		int start = 0;
		while (true) {
			Act a = getAct(start);
			if (a != null) {
				start++;
			} else {
				return start;
			}
		}
	}

	public final int getNumRewards() {
		return numRewards;
	}

	public final int getPartySize() {
		return partySize;
	}

	public Profession getProfession() {
		return profession;
	}

	public final Long getRequires() {
		return requires;
	}

	public int getSuggestedLevel() {
		return suggestedLevel;
	}

	public final String getTitle() {
		return title;
	}

	public final boolean isAccountQuest() {
		return accountQuest;
	}

	public boolean isBounty() {
		return getTitle().startsWith("BOUNTY:");
	}

	public final boolean isRepeat() {
		return repeat;
	}

	public final boolean isUnabandon() {
		return unabandon;
	}

	@Override
	public void set(String name, String value, String section) {
		if (section.equalsIgnoreCase("ACT")) {
			if (name.equals("")) {
				newAct = new Act(getDatabase(), this, acts.size());
				acts.add(newAct);
			} else {
				try {
					newAct.set(name, value, section);
				} catch (Exception e) {
					Log.error(getArtifactName(),
							"Failed parsing quest ACT " + getEntityId() + " in " + getFile() + " at " + getStartPosition(), e);
				}
			}
		} else if (name.equalsIgnoreCase("ID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equalsIgnoreCase("Title")) {
			title = value;
		} else if (name.equalsIgnoreCase("BodyText")) {
			bodyText = value;
		} else if (name.equalsIgnoreCase("CompleteText")) {
			completeText = value;
		} else if (name.equalsIgnoreCase("Suggested")) {
			suggestedLevel = Integer.parseInt(value);
		} else if (name.equalsIgnoreCase("Level")) {
			StringTokenizer t = new StringTokenizer(value, ", ");
			maxLevel = 0;
			if (t.hasMoreTokens()) {
				level = Integer.parseInt(t.nextToken());
				if (t.hasMoreTokens()) {
					maxLevel = Integer.parseInt(t.nextToken());
				}
			}
		} else if (name.equalsIgnoreCase("Exp")) {
			exp = Long.parseLong(value);
		} else if (name.equalsIgnoreCase("PartySize")) {
			partySize = Integer.parseInt(value);
		} else if (name.equalsIgnoreCase("NumRewards")) {
			numRewards = Integer.parseInt(value);
		} else if (name.equalsIgnoreCase("Coin")) {
			coin = Long.parseLong(value);
		} else if (name.equalsIgnoreCase("Heroism")) {
			heroism = Integer.parseInt(value);
		} else if (name.equalsIgnoreCase("Unabandon")) {
			unabandon = "1".equals(value);
		} else if (name.equalsIgnoreCase("QuestGiverID")) {
			giverId = Long.parseLong(value);
		} else if (name.equalsIgnoreCase("QuestEnderID")) {
			enderId = Long.parseLong(value);
		} else if (name.equalsIgnoreCase("Requires")) {
			requires = Long.parseLong(value);
		} else if (name.equalsIgnoreCase("Repeat")) {
			repeat = "1".equals(value);
		} else if (name.equalsIgnoreCase("RepeatDelay")) {
			try {
				repeatDelayAmount = Integer.parseInt(value.replaceAll("[^0-9]", ""));
				if (value.toUpperCase().endsWith("M")) {
					repeatDelayTimeUnit = TimeUnit.MINUTES;
				} else if (value.toUpperCase().endsWith("H")) {
					repeatDelayTimeUnit = TimeUnit.MINUTES;
				} else if (value.toUpperCase().endsWith("D")) {
					repeatDelayTimeUnit = TimeUnit.DAYS;
				} else
					throw new IllegalArgumentException("Unknown time unit. End string with H, M or D");
			} catch (Exception e) {
				repeatDelayAmount = 0;
				repeatDelayTimeUnit = TimeUnit.MINUTES;
				Log.error(getArtifactName(), "Bad repeat delay, assuming instant repeat.", e);
			}
		} else if (name.equalsIgnoreCase("AccountQuest")) {
			accountQuest = "1".equals(value);
		} else if (name.equals("Profession")) {
			profession = Profession.fromCode(Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("sGiver")) {
			String[] v = value.split(";");
			for (String s : v) {
				// TODO - Are there multiple locations in giver now?
				if (!s.equals("")) {
					try {
						giverLocation = new Location(s);
					} catch (NumberFormatException nfe) {
						Log.error(getArtifactName(), "Bad sGgiver for quest " + getEntityId() + ".", nfe);
					}
					break;
				}
			}
		} else if (name.equalsIgnoreCase("sEnder")) {
			String[] v = value.split(";");
			for (String s : v) {
				// TODO - Are there multiple locations in ender now?
				if (!s.equals("")) {
					try {
						enderLocation = new Location(s);
					} catch (NumberFormatException nfe) {
						Log.error(getArtifactName(), "Bad sEnder for quest " + getEntityId() + ".", nfe);
					}
					break;
				}
			}
		} else if (!name.equals("") || !value.equals("")) {
			Log.todo("Quest", "Unhandled property " + name + " = " + value);
		}
	}

	public final void setAccountQuest(boolean accountQuest) {
		this.accountQuest = accountQuest;
	}

	public final void setActs(List<Act> acts) {
		this.acts = acts;
	}

	public final void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

	public final void setCoin(long coin) {
		this.coin = coin;
	}

	public final void setCompleteText(String completeText) {
		this.completeText = completeText;
	}

	public final void setEnderId(Long enderId) {
		this.enderId = enderId;
	}

	public final void setEnderLocation(Location enderLocation) {
		this.enderLocation = enderLocation;
	}

	public final void setExp(long exp) {
		this.exp = exp;
	}

	public final void setGiverId(Long giverId) {
		this.giverId = giverId;
	}

	public final void setGiverLocation(Location giverLocation) {
		this.giverLocation = giverLocation;
	}

	public final void setHeroism(int heroism) {
		this.heroism = heroism;
	}

	public final void setLevel(int level) {
		this.level = level;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public final void setNumRewards(int numRewards) {
		this.numRewards = numRewards;
	}

	public final void setPartySize(int partySize) {
		this.partySize = partySize;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}

	public final void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public final void setRequires(Long requires) {
		this.requires = requires;
	}

	public void setSuggestedLevel(int suggestedLevel) {
		this.suggestedLevel = suggestedLevel;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final void setUnabandon(boolean unabandon) {
		this.unabandon = unabandon;
	}

	@Override
	public String toString() {
		return getTitle();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("ID=" + getEntityId());
		if (requires != null) {
			writer.println("Requires=" + requires);
		}
		if (profession != null) {
			writer.println("Profession=" + profession);
		}
		writer.println("Title=" + title);
		writer.println("BodyText=" + bodyText);
		writer.println("CompleteText=" + Util.nonNull(completeText));
		if (maxLevel > 0) {
			writer.println("Level=" + level + "," + maxLevel);
		} else {
			if (level > 0)
				writer.println("Level=" + level);
		}
		if (suggestedLevel > 0) {
			writer.println("Suggested=" + suggestedLevel);
		}
		writer.println("Exp=" + exp);
		writer.println("PartySize=" + partySize);
		writer.println("NumRewards=" + numRewards);
		writer.println("Coin=" + coin);
		if (accountQuest)
			writer.println("AccountQuest=1");
		if (heroism > 0)
			writer.println("Heroism=" + heroism);
		writer.println("Unabandon=" + Util.toBooleanString(unabandon));
		writer.println("QuestGiverID=" + giverId);
		writer.println("QuestEnderID=" + enderId);
		writer.println("Repeat=" + Util.toBooleanString(repeat));
		if (repeatDelayTimeUnit != null && repeatDelayAmount > 0) {
			writer.println("RepeatDelay=" + repeatDelayAmount + repeatDelayTimeUnit.name().toUpperCase().charAt(0));
		}
		writer.println("sGiver=" + giverLocation.toString());
		writer.println("sEnder=" + enderLocation.toString());
		if (acts.size() > 0) {
			for (Act act : acts) {
				writer.println("[ACT]");
				act.write(writer);
			}
		}
	}
}

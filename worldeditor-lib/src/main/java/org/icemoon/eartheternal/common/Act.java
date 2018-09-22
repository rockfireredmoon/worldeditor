package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Act extends AbstractINIFileEntity<Integer, IDatabase> {
	private Quest quest;
	private List<Objective> objectives = new ArrayList<Objective>();
	private List<Reward> rewards = new ArrayList<Reward>();
	private String text;
	private String scriptAcceptCondition;
	private String scriptAcceptAction;
	private String scriptCompleteCondition;
	private String scriptCompleteAction;

	public Act(Quest quest) {
		this(null, quest);
	}

	public Act(IDatabase database, Quest quest) {
		super(database);
		this.quest = quest;
	}

	public Act(IDatabase database, Quest quest, Integer id) {
		super(database, null, id);
		this.quest = quest;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Act other = (Act) obj;
		if (quest == null) {
			if (other.quest != null)
				return false;
		} else if (!quest.equals(other.quest))
			return false;
		return true;
	}

	public Integer getNextRewardId() {
		int i = 0;
		while (true) {
			if (getReward(i) == null) {
				return i;
			}
			i++;
		}
	}

	public final List<Objective> getObjectives() {
		return objectives;
	}

	public final Quest getQuest() {
		return quest;
	}

	public Reward getReward(Integer id) {
		for (Reward r : rewards) {
			if (id.equals(r.getEntityId())) {
				return r;
			}
		}
		return null;
	}

	public final List<Reward> getRewards() {
		return rewards;
	}

	public final String getScriptAcceptAction() {
		return scriptAcceptAction;
	}

	public final String getScriptAcceptCondition() {
		return scriptAcceptCondition;
	}

	public final String getScriptCompleteAction() {
		return scriptCompleteAction;
	}

	public final String getScriptCompleteCondition() {
		return scriptCompleteCondition;
	}

	public final String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((quest == null) ? 0 : quest.hashCode());
		result = prime * result + super.hashCode();
		return result;
	}

	public Objective newObjective() {
		return new Objective(getDatabase(), objectives.size(), this);
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("Act.BodyText")) {
			text = value;
		} else if (name.startsWith("Obj.")) {
			String[] split = name.split("\\.");
			int index = Integer.parseInt(split[1]);
			while (objectives.size() < index + 1) {
				objectives.add(new Objective(getDatabase(), objectives.size(), this));
			}
			Objective obj = objectives.get(index);
			obj.set(split[2], value, null);
		} else if (name.startsWith("RewardItem.")) {
			if (StringUtils.isBlank(value)) {
				Log.error(String.valueOf(getEntityId()), "Invalid reward item", new IllegalArgumentException("Empty reward item"));
			} else {
				String[] split = name.split("\\.");
				int index = Integer.parseInt(split[1]);
				while (rewards.size() < index + 1) {
					Reward reward = new Reward(getDatabase(), this);
					reward.setEntityId(rewards.size());
					rewards.add(reward);
				}
				Reward reward = rewards.get(index);
				reward.set(name, value, null);
			}
		} else if (name.startsWith("ScriptAcceptCondition")) {
			scriptAcceptCondition = colonSeparatedToNewlineSeparated(value);
		} else if (name.startsWith("ScriptAcceptAction")) {
			scriptAcceptAction = colonSeparatedToNewlineSeparated(value);
		} else if (name.startsWith("ScriptCompleteCondition")) {
			scriptCompleteCondition = colonSeparatedToNewlineSeparated(value);
		} else if (name.startsWith("ScriptCompleteAction")) {
			scriptCompleteAction = colonSeparatedToNewlineSeparated(value);
		} else {
			Log.todo("Act", "Unhandled property " + name + " = " + value);
		}
	}

	public final void setObjectives(List<Objective> objectives) {
		this.objectives = objectives;
	}

	public final void setQuest(Quest quest) {
		this.quest = quest;
	}

	public final void setRewards(List<Reward> rewards) {
		this.rewards = rewards;
	}

	public final void setScriptAcceptAction(String scriptAcceptAction) {
		this.scriptAcceptAction = scriptAcceptAction;
	}

	public final void setScriptAcceptCondition(String scriptAcceptCondition) {
		this.scriptAcceptCondition = scriptAcceptCondition;
	}

	public final void setScriptCompleteAction(String scriptCompleteAction) {
		this.scriptCompleteAction = scriptCompleteAction;
	}

	public final void setScriptCompleteCondition(String scriptCompleteCondition) {
		this.scriptCompleteCondition = scriptCompleteCondition;
	}

	public final void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("Act.BodyText=" + text);
		for (Objective obj : objectives) {
			obj.write(writer);
		}
		for (Reward reward : rewards) {
			reward.write(writer);
		}
		if (StringUtils.isNotBlank(scriptAcceptCondition)) {
			writer.println("ScriptAcceptCondition=" + newlineSeparatedToColonSeparated(scriptAcceptCondition));
		}
		if (StringUtils.isNotBlank(scriptAcceptAction)) {
			writer.println("ScriptAcceptAction=" + newlineSeparatedToColonSeparated(scriptAcceptAction));
		}
		if (StringUtils.isNotBlank(scriptCompleteCondition)) {
			writer.println("ScriptCompleteCondition=" + newlineSeparatedToColonSeparated(scriptCompleteCondition));
		}
		if (StringUtils.isNotBlank(scriptCompleteAction)) {
			writer.println("ScriptCompleteAction=" + newlineSeparatedToColonSeparated(scriptCompleteAction));
		}
	}

	String colonSeparatedToNewlineSeparated(String in) {
		StringBuilder b = new StringBuilder();
		for (String a : in.split(";")) {
			if (b.length() > 0)
				b.append("\n");
			b.append(a.trim());
		}
		return b.toString();
	}

	String newlineSeparatedToColonSeparated(String in) {
		StringBuilder b = new StringBuilder();
		for (String a : in.replace("\r", "").split("\n")) {
			if (b.length() > 0)
				b.append(";");
			b.append(a.trim());
		}
		return b.toString();
	}

	@Override
	protected void doLoad() throws IOException {
	}
}
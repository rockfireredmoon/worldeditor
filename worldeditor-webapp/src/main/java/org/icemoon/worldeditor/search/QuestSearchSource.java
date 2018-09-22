package org.icemoon.worldeditor.search;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.icemoon.eartheternal.common.Act;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Objective;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.worldeditor.QuestPage;

@SuppressWarnings("serial")
public class QuestSearchSource implements SearchSource {
	private IModel<IDatabase> database;

	public QuestSearchSource(IModel<IDatabase> database) {
		this.database = database;
	}

	@Override
	public Iterator<SearchResult> search(String search) {
		return new QuestIterator(search, database.getObject().getQuests().values().iterator());
	}

	public static boolean stringMatches(String text, String search) {
		return text != null && text.toLowerCase().contains(search.toLowerCase());
	}

	private static final class QuestResult extends AbstractSearchResult<Quest> {
		private QuestResult(Quest character, int matchProbability) {
			super(character, ResultType.QUEST);
		}

		@Override
		public String getSubtext() {
			return getDisplayName() + ". Rec. " + getEntity().getPartySize() + " party. " + getEntity().getExp() + " XP Reward";
		}

		@Override
		public String getDescription() {
			return getEntity().getBodyText();
		}

		@Override
		public void activate(Component component) {
			component.setResponsePage(QuestPage.class, new PageParameters().add("id", getEntity().getEntityId()));
		}
	}

	class QuestIterator extends AbstractEntityIterator<Quest> {
		public QuestIterator(String search, Iterator<Quest> iterator) {
			super(search, iterator);
		}

		@Override
		protected int matches(String search) {
			if (stringMatches(getEntity().getTitle(), search)) {
				return 100;
			}
			if (stringMatches(getEntity().getBodyText(), search)) {
				return 100;
			}
			if (stringMatches(getEntity().getCompleteText(), search)) {
				return 100;
			}
			for (Act act : getEntity().getActs()) {
				if (stringMatches(act.getText(), search)) {
					return 100;
				}
				for (Objective obj : act.getObjectives()) {
					if (stringMatches(obj.getDescription(), search)) {
						return 100;
					}
				}
			}
			return -1;
		}

		protected SearchResult createResult(final int matchProbability, final Quest quest) {
			return new QuestResult(quest, matchProbability);
		}
	}
}

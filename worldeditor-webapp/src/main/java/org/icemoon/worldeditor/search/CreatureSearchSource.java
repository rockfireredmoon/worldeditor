package org.icemoon.worldeditor.search;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.CreaturePage;

@SuppressWarnings("serial")
public class CreatureSearchSource implements SearchSource {
	private IModel<IDatabase> database;

	public CreatureSearchSource(IModel<IDatabase> database) {
		this.database = database;
	}

	@Override
	public Iterator<SearchResult> search(String search) {
		return new CreatureIterator(search, database.getObject().getCreatures().values().iterator());
	}

	public static boolean stringMatches(String text, String search) {
		return text != null && text.toLowerCase().contains(search.toLowerCase());
	}

	private static final class CreatureResult extends AbstractSearchResult<Creature> {
		private CreatureResult(Creature character, int matchProbability) {
			super(character, ResultType.CREATURE);
		}

		@Override
		public String getSubtext() {
			return getEntity().getSubName();
		}

		@Override
		public String getDescription() {
			return "Level " + getEntity().getLevel() + " " + Util.toEnglish(getEntity().getProfession(), true);
		}

		@Override
		public void activate(Component component) {
			component.setResponsePage(CreaturePage.class, new PageParameters().add("id", getEntity().getEntityId()));
		}
	}

	class CreatureIterator extends AbstractEntityIterator<Creature> {
		public CreatureIterator(String search, Iterator<Creature> iterator) {
			super(search, iterator);
		}

		@Override
		protected int matches(String search) {
			if (stringMatches(getEntity().getDisplayName(), search)) {
				return 100;
			}
			return -1;
		}

		protected SearchResult createResult(final int matchProbability, final Creature character) {
			return new CreatureResult(character, matchProbability);
		}
	}
}

package org.icemoon.worldeditor.search;

import java.util.Iterator;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.player.CharacterPage;

@SuppressWarnings("serial")
public class CharacterSearchSource implements SearchSource {
	private IModel<IUserData> userData;

	public CharacterSearchSource(IModel<IUserData> userData) {
		this.userData = userData;
	}

	@Override
	public Iterator<SearchResult> search(String search) {
		return new CharacterIterator(search, userData.getObject().getCharacters().values().iterator());
	}

	public static boolean stringMatches(String text, String search) {
		return text != null && text.toLowerCase().contains(search.toLowerCase());
	}

	private static final class CharacterResult extends AbstractSearchResult<GameCharacter> {
		private CharacterResult(GameCharacter character, int matchProbability) {
			super(character, ResultType.CHARACTER);
		}

		@Override
		public String getSubtext() {
			return getEntity().getStatusText();
		}

		@Override
		public String getDescription() {
			return "Level " + getEntity().getLevel() + " " + Util.toEnglish(getEntity().getProfession(), true);
		}

		@Override
		public void activate(Component component) {
			component.setResponsePage(CharacterPage.class, new PageParameters().add("id", getEntity().getEntityId()));
		}
	}

	class CharacterIterator extends AbstractEntityIterator<GameCharacter> {
		public CharacterIterator(String search, Iterator<GameCharacter> iterator) {
			super(search, iterator);
		}

		@Override
		protected int matches(String search) {
			if (stringMatches(getEntity().getDisplayName(), search)) {
				return 100;
			}
			return -1;
		}

		protected SearchResult createResult(final int matchProbability, final GameCharacter character) {
			return new CharacterResult(character, matchProbability);
		}
	}
}

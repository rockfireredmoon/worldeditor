package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.worldeditor.Application;

@SuppressWarnings("serial")
public final class GameCharacterListModel extends ListModel<GameCharacter> {
	public List<GameCharacter> getObject() {
		return new ArrayList<GameCharacter>(Application.getApp().getUserData().getCharacters().values());
	}
}
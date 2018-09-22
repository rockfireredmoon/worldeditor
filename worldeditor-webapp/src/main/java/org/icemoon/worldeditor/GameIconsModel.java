package org.icemoon.worldeditor;

import org.apache.wicket.model.Model;
import org.icemoon.eartheternal.common.GameIcons;

final class GameIconsModel extends Model<GameIcons> {
	private static final long serialVersionUID = 1L;

	public GameIcons getObject() {
		return Application.getApp().getStaticDatabase().getGameIcons();
	}
}
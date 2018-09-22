package org.icemoon.worldeditor.model;

import org.icemoon.eartheternal.common.AIScript;
import org.icemoon.eartheternal.common.AIScripts;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class AIScriptsModel extends AbstractDatabaseModel<AIScripts, AIScript, String, String, IDatabase> {
	public AIScriptsModel(AbstractPage page) {
		super(page);
	}

	public AIScripts getObject() {
		return database.getObject().getAIScripts();
	}
}
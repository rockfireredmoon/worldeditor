package org.icemoon.worldeditor;

import org.icemoon.eartheternal.common.AIScript;
import org.icemoon.eartheternal.common.AIScripts;

@SuppressWarnings("serial")
public class AIScriptsPage extends AbstractScriptsPage<AIScript, String, AIScripts> {
	public AIScriptsPage() {
		super(String.class);
	}

	@Override
	protected AIScript createNewInstance() {
		return new AIScript(getDatabase());
	}

	@Override
	public AIScripts getEntityDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase().getAIScripts();
	}
}

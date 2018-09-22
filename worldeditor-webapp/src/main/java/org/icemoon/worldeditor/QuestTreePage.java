package org.icemoon.worldeditor;

import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.worldeditor.components.QuestTree;
import org.icemoon.worldeditor.model.LinkedHashSetState;
import org.icemoon.worldeditor.player.AbstractUserPage;

@SuppressWarnings("serial")
public class QuestTreePage extends AbstractUserPage {
	private QuestTree tree;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final IModel<Set<Quest>> state = new LinkedHashSetState<Quest>();
		tree = new QuestTree("quests", new ActProvider(), state);
		tree.select(null, null);
		add(tree);
	}

	@Override
	protected void onBeforeRender() {
		for (Iterator<? extends Quest> qi = tree.getProvider().getRoots(); qi.hasNext();) {
			expand(qi.next());
		}
		super.onBeforeRender();
	}

	void expand(Quest q) {
		tree.expand(q);
		for (Iterator<? extends Quest> qi = tree.getProvider().getChildren(q); qi.hasNext();) {
			expand(qi.next());
		}
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(QuestTreePage.class, "QuestTreePage.css")));
	}

	class ActProvider implements ITreeProvider<Quest> {
		@Override
		public void detach() {
		}

		@Override
		public boolean hasChildren(Quest object) {
			return getDatabase().getQuests().getLeadsTo(object).size() != 0;
		}

		@Override
		public Iterator<Quest> getChildren(Quest object) {
			return getDatabase().getQuests().getLeadsTo(object).iterator();
		}

		@Override
		public IModel<Quest> model(Quest object) {
			return new Model<Quest>(object);
		}

		@Override
		public Iterator<Quest> getRoots() {
			return getDatabase().getQuests().getRootQuests().iterator();
		}
	}
}

package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.eartheternal.common.QuestScript;
import org.icemoon.eartheternal.common.QuestScripts;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.model.QuestsModel;

@SuppressWarnings("serial")
public class QuestScriptsPage extends AbstractScriptsPage<QuestScript, Long, QuestScripts> {
	private String titleFilter;
	
	public QuestScriptsPage() {
		super(Long.class);
	}
	@Override
	protected void buildForm(Form<QuestScript> form) {
		super.buildForm(form);
		form.add(new SelectorPanel<Long, Quest, String, IDatabase>("entityId", new Model<String>("Quest"), new QuestsModel(this), "title",
				new PropertyModel<Long>(this, "selected.entityId"), Quest.class, Long.class, QuestsPage.class) {
			@Override
			public boolean isChangeAllowed() {
				return !editing;
			}
		}.setShowLabel(true).setShowClear(true));
	}

	@Override
	protected void addIdField() {
	}
	@Override
	protected void buildColumns(List<IColumn<QuestScript, String>> columns) {
		super.buildColumns(columns);
		columns.add(new LinkColumn<QuestScript, Quest, Long, String, IDatabase>(new ResourceModel("column.quest"), "quest",
				new PropertyModel<String>(this, "titleFilter"), new QuestsModel(this), "entityId", "title"));
	}

	protected boolean entityMatches(QuestScript object, QuestScript filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		Quest z = new QuestsModel(this).getObject().get(object.getEntityId());
		if (z == null || Util.notMatches(z.getTitle(), titleFilter)) {
			return false;
		}
		return true;
	}

	@Override
	protected QuestScript createNewInstance() {
		return new QuestScript(getDatabase());
	}

	@Override
	public QuestScripts getEntityDatabase() {
		return Application.getAppSession(getRequestCycle()).getDatabase().getQuestScripts();
	}
}

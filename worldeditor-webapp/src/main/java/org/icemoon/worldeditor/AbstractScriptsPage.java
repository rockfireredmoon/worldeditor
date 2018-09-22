package org.icemoon.worldeditor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Language;
import org.icemoon.eartheternal.common.Script;
import org.icemoon.worldeditor.behaviors.CodeMirrorBehavior;
import org.icemoon.worldeditor.behaviors.CodeMirrorBehavior.DefaultCodeMirrorSettings;

@SuppressWarnings("serial")
public abstract class AbstractScriptsPage<S extends Script<K>, K extends Serializable, D extends Entities<S, K, String, IDatabase>>
		extends AbstractEntityPage<S, K, String, D, IDatabase> {
	public AbstractScriptsPage(Class<K> keyClass) {
		super("entityId", keyClass);
	}

	@Override
	protected void buildForm(Form<S> form) {
		form.add(new DropDownChoice<Language>("language", Arrays.asList(Language.values())) {
			public boolean isEnabled() {
				return !editing;
			}
		});
		DefaultCodeMirrorSettings codeMirrorSettings = new DefaultCodeMirrorSettings();
		codeMirrorSettings.setMode("squirrel");
		codeMirrorSettings.addAddOn("addon/display/fullscreen.js");
		codeMirrorSettings.addCss("addon/display/fullscreen.css");
		codeMirrorSettings.getExtraKeys().put("F11",
				"function(cm) { cm.setOption(\"fullScreen\", !cm.getOption(\"fullScreen\"));}");
		codeMirrorSettings.getExtraKeys().put("Esc",
				"function(cm) { if (cm.getOption(\"fullScreen\")) cm.setOption(\"fullScreen\", false);}");
		codeMirrorSettings.setHighlightSelection(true);
		codeMirrorSettings.setLineWrapping(true);
		codeMirrorSettings.setTheme("abcdef");
		form.add(new TextArea<String>("script").setRequired(true).setLabel(new Model<String>("Script"))
				.add(new CodeMirrorBehavior(codeMirrorSettings)));
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(AbstractScriptsPage.class, "AbstractScriptsPage.css")));
	}

	@Override
	protected void buildColumns(List<IColumn<S, String>> columns) {
	}
}

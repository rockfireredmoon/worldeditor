package org.icemoon.worldeditor.tools;

import java.text.ParseException;
import java.util.UUID;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Color;
import org.icemoon.eartheternal.common.DuplicateHandler;
import org.icemoon.eartheternal.common.EternalObjectNotation;
import org.icemoon.eartheternal.common.Log;
import org.icemoon.eartheternal.common.RGB;
import org.icemoon.eartheternal.common.Sceneries;
import org.icemoon.eartheternal.common.Scenery;
import org.icemoon.eartheternal.common.AbstractMultiINIFileEntities.IDType;
import org.icemoon.worldeditor.AbstractAuthenticatedPage;
import org.icemoon.worldeditor.components.ColorField;

@SuppressWarnings("serial")
public class ToolsPage extends AbstractAuthenticatedPage {
	private String objectString;
	private String formatted;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new FeedbackPanel("feedback"));
		// EON
		Form<?> eonForm = new Form<Object>("eonForm");
		eonForm.add(new TextArea<String>("objectString", new PropertyModel<String>(this, "objectString")).setRequired(true));
		eonForm.add(new TextArea<String>("formatted", new PropertyModel<String>(this, "formatted")));
		eonForm.add(new Button("parse") {
			@Override
			public void onSubmit() {
				try {
					EternalObjectNotation eon = new EternalObjectNotation(objectString);
					formatted = eon.toPrettyString();
				} catch (ParseException e) {
					error(e.getMessage());
				}
			}
		});
		add(eonForm);
		// Widgets
		Form<?> widgetsForm = new Form<Object>("widgetForm");
		widgetsForm.add(new ColorField("color", new Model<RGB>(new Color(0, 0xff, 0))));
		add(widgetsForm);
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(ToolsPage.class, "ToolsPage.css")));
	}
}

package org.icemoon.worldeditor;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.odlabs.wiquery.core.events.Event;
import org.odlabs.wiquery.core.events.MouseEvent;
import org.odlabs.wiquery.core.events.WiQueryEventBehavior;
import org.odlabs.wiquery.core.javascript.JsScope;
import org.odlabs.wiquery.ui.dialog.Dialog;

@SuppressWarnings("serial")
public class ChooserPanel<T> extends FormComponentPanel<T> {

	private TextField<T> text;
	private T value;

	public ChooserPanel(final String id, ITreeProvider<T> provider, IModel<String> title) {
		super(id);

		final Dialog dialog = new Dialog("dialog");
		dialog.setTitle(title);
		dialog.setWidth(400);
		dialog.setHeight(400);
		NestedTree<T> tree = new NestedTree<T>("tree", provider, new PropertyModel<Set<T>>(this, "selected")) {

			@Override
			public void renderHead(IHeaderResponse response) {
//				response.render(new HumanTheme());
			}

			@Override
			protected Component newContentComponent(String id, IModel<T> model) {
				return new Folder<T>(id, this, model) {
					protected MarkupContainer newLinkComponent(String id, final IModel<T> model) {
						return new AjaxFallbackLink<String>(id, new PropertyModel<String>(model, "object.name")) {
							private static final long serialVersionUID = 1L;

							@Override
							public void onClick(AjaxRequestTarget target) {
								fileSelected(model);
								dialog.close(target);
								target.add(text);
							}
						};
					}
				};
			}
		};
		add(dialog);
		dialog.add(tree);

		Button button = new Button("choose");
		button.add(new WiQueryEventBehavior(new Event(MouseEvent.CLICK) {

			@Override
			public JsScope callback() {
				return JsScope.quickScope(dialog.open().render());
			}

		}));
		
		add(new AjaxButton("clear") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				clear();
				target.add(text);
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		
		add(button);
		text = new TextField<T>("text", new PropertyModel<T>(this, "value"));
		text.setOutputMarkupId(true);
		add(text);
	}
	
	private void clear() {
		getModel().setObject(null);
		text.clearInput();
	}
	
	private void fileSelected(IModel<T> selected) {
		T selectedObject = selected.getObject();
		getModel().setObject(selectedObject);
	}

	public final Set<T> getSelected() {
		LinkedHashSet<T> t = new LinkedHashSet<T>();
		t.add(value);
		return t;
	}

	@SuppressWarnings("unchecked")
	public final void setSelected(Set<T> selected) {
		value = selected.isEmpty() ? (T)Collections.emptySet() : selected.iterator().next();
	}

	@Override
	protected void convertInput() {
		setConvertedInput(text.getConvertedInput());
	}

}
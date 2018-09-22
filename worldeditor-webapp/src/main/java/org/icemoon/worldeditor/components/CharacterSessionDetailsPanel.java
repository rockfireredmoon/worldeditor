package org.icemoon.worldeditor.components;

import java.text.DateFormat;
import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.GameCharacter;

@SuppressWarnings("serial")
public class CharacterSessionDetailsPanel extends Panel {

	public CharacterSessionDetailsPanel(final String id, IModel<? extends GameCharacter> model) {
		super(id, model);
		add(new Label("lastLogon", new Model<String>() {
			public String getObject() {
				return getCharacter().getLastLogon() == null ? "Never" : DateFormat.getDateTimeInstance().format(
					getCharacter().getLastLogon());
			}
		}));
		add(new Label("lastLogoff", new Model<String>() {
			public String getObject() {
				return getCharacter().getLastLogoff() == null ? "Never" : DateFormat.getDateTimeInstance().format(
					getCharacter().getLastLogoff());
			}
		}));
		add(new Label("lastSession", new Model<String>() {
			public String getObject() {
				return getCharacter().getLastSession() == 0 ? "Never" : DateFormat.getTimeInstance(DateFormat.SHORT).format(
					new Date(getCharacter().getLastSession()));
			}
		}));
		add(new Label("timeLogged", new PropertyModel<Long>(this, "defaultModelObject.timeLogged")));
		add(new Label("secondsLogged", new PropertyModel<Long>(this, "defaultModelObject.secondsLogged")));
		add(new Label("sessionsLogged", new PropertyModel<Integer>(this, "defaultModelObject.sessionsLogged")));
	}

	protected GameCharacter getCharacter() {
		return (GameCharacter) getDefaultModelObject();
	}
}
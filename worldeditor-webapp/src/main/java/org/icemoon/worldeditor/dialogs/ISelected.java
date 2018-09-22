package org.icemoon.worldeditor.dialogs;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface ISelected<T> {

	void onActionSelected(AjaxRequestTarget target, T modelObject);
	
}
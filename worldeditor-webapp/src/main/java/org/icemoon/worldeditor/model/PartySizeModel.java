package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@SuppressWarnings("serial")
public class PartySizeModel extends Model<String> {
	
	private IModel<Integer> model;
	
	public PartySizeModel(IModel<Integer> model) {
		this.model = model;
	}
	
	public String getObject() {
		int size = model.getObject();
		switch(size) {
		case 0:
			return "<Zero - Error>";
		case 1:
			return "Solo quest";
		default:
			return size + " party quest";
		} 
	}

}

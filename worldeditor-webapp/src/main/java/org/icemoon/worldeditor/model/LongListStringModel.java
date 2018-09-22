package org.icemoon.worldeditor.model;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.icemoon.eartheternal.common.Util;

@SuppressWarnings("serial")
public class LongListStringModel extends Model<String> {
	
	private IModel<List<Long>> model;

	public LongListStringModel(IModel<List<Long>> model) {
		this.model = model;
	}
	
	public String getObject() {
		return Util.toCommaSeparatedList(model.getObject());
	}
	
	public void setObject(String object) {
		model.setObject(Util.toLongList(object));
	}

}

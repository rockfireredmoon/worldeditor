package org.icemoon.worldeditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class Place implements Serializable {

	private Class<? extends Page> page;
	private IModel<?> text;
	private String id;
	private List<Place> places = new ArrayList<Place>();

	public Place(String id, Class<? extends Page> page, IModel<?> text) {
		super();
		this.id = id;
		this.page = page;
		this.text = text;
	}
	

	public final List<Place> getPlaces() {
		return places;
	}

	public String getId() {
		return id;
	}

	public Place setId(String id) {
		this.id = id;
		return this;
	}

	public Class<? extends Page> getPage() {
		return page;
	}

	public Place setPage(Class<? extends Page> page) {
		this.page = page;
		return this;
	}

	public IModel<?> getText() {
		return text;
	}

	public Place setText(IModel<?> text) {
		this.text = text;
		return this;
	}

}

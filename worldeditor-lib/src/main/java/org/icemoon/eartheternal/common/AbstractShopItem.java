package org.icemoon.eartheternal.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class AbstractShopItem implements Serializable {
	protected Long id;
	protected String comment;

	protected AbstractShopItem() {
		
	}
	public AbstractShopItem(String line) {
		line = line.trim();
		StringBuilder b = new StringBuilder();
		for (char c : line.toCharArray()) {
			if (!Character.isDigit(c))
				break;
			b.append(c);
		}
		line = line.substring(b.length()).trim();
		if (line.length() > 0) {
			comment = line;
			while (comment.startsWith(";"))
				comment = comment.substring(1);
		}
		id = Long.parseLong(b.toString());
	}

	public AbstractShopItem(Long id, String comment) {
		super();
		this.id = id;
		this.comment = comment;
	}

	public final Long getId() {
		return id;
	}

	public final void setId(Long id) {
		this.id = id;
	}

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractShopItem other = (AbstractShopItem) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id + (comment == null ? "" : " ;" + comment);
	}

	public abstract void write(INIWriter writer);
}
package org.icemoon.worldeditor;

import java.util.Collections;
import java.util.Map;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.value.ValueMap;

class UnescapedMetaDataHeaderItem extends HeaderItem {
	public static final String META_TAG = "meta";
	public static final String LINK_TAG = "link";

	private final Map<String, Object> tagAttributes;
	private final String tagName;

	/**
	 * Build a new {@link UnescapedMetaDataHeaderItem} having {@code tagName} as
	 * tag.
	 * 
	 * @param tagName the name of the tag
	 */
	public UnescapedMetaDataHeaderItem(String tagName) {
		this.tagName = Args.notEmpty(tagName, "tagName");
		this.tagAttributes = new ValueMap();
	}

	/**
	 * Add a tag attribute to the item. If the attribute value is a {@link IModel},
	 * the object wrapped inside the model is used as actual value.
	 * 
	 * @param attributeName  the attribute name
	 * @param attributeValue the attribute value
	 * @return The current item.
	 */
	public UnescapedMetaDataHeaderItem addTagAttribute(String attributeName, Object attributeValue) {
		Args.notEmpty(attributeName, "attributeName");
		Args.notNull(attributeValue, "attributeValue");

		tagAttributes.put(attributeName, attributeValue);
		return this;
	}

	/**
	 * Generate the string representation for the current item.
	 * 
	 * @return The string representation for the current item.
	 */
	public String generateString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append('<').append(tagName);

		for (Map.Entry<String, Object> entry : tagAttributes.entrySet()) {
			Object value = entry.getValue();

			if (value instanceof IModel) {
				value = ((IModel<?>) value).getObject();
			}

			if (value != null) {
				buffer.append(' ').append(Strings.escapeMarkup(entry.getKey())).append('=').append('"')
						.append(value.toString()).append('"');
			}
		}

		buffer.append(" />\n");

		return buffer.toString();
	}

	@Override
	public Iterable<?> getRenderTokens() {
		return Collections.singletonList(generateString());
	}

	@Override
	public void render(Response response) {
		response.write(generateString());
	}

	/**
	 * Factory method to create &lt;meta&gt; tag.
	 * 
	 * @param name    the 'name' attribute of the tag
	 * @param content the 'content' attribute of the tag
	 * @return A new {@link UnescapedMetaDataHeaderItem}
	 */
	public static UnescapedMetaDataHeaderItem forMetaTag(String name, String content) {
		return forMetaTag(Model.of(name), Model.of(content));
	}

	/**
	 * Factory method to create &lt;meta&gt; tag.
	 * 
	 * @param name    the 'name' attribute of the tag as String model
	 * @param content the 'content' attribute of the tag as String model
	 * @return A new {@link UnescapedMetaDataHeaderItem}
	 */
	public static UnescapedMetaDataHeaderItem forMetaTag(IModel<String> name, IModel<String> content) {
		UnescapedMetaDataHeaderItem headerItem = new UnescapedMetaDataHeaderItem(META_TAG);

		headerItem.addTagAttribute("name", name);
		headerItem.addTagAttribute("content", content);

		return headerItem;
	}

	/**
	 * Factory method to create &lt;link&gt; tag.
	 * 
	 * @param rel  the 'rel' attribute of the tag
	 * @param href the 'href' attribute of the tag
	 * @return A new {@link UnescapedMetaDataHeaderItem}
	 */
	public static UnescapedMetaDataHeaderItem forLinkTag(String rel, String href) {
		return forLinkTag(Model.of(rel), Model.of(href));
	}

	/**
	 * Factory method to create &lt;link&gt; tag.
	 * 
	 * @param rel  the 'rel' attribute of the tag as String model
	 * @param href the 'href' attribute of the tag as String model
	 * @return A new {@link UnescapedMetaDataHeaderItem}
	 */
	public static UnescapedMetaDataHeaderItem forLinkTag(IModel<String> rel, IModel<String> href) {
		UnescapedMetaDataHeaderItem headerItem = new UnescapedMetaDataHeaderItem(LINK_TAG);

		headerItem.addTagAttribute("rel", rel);
		headerItem.addTagAttribute("href", href);

		return headerItem;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof UnescapedMetaDataHeaderItem
				&& ((UnescapedMetaDataHeaderItem) obj).generateString().equals(generateString());
	}
}

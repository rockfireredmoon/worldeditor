package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class EternalObjectNotation implements Serializable {
	public static String format(Object object) {
		if (object instanceof String) {
			return "\"" + object + "\"";
		} else {
			return String.valueOf(object);
		}
	}

	public static Object parse(String text) throws ParseException {
		text = text.trim();
		if (text.startsWith("\"")) {
			int eidx = text.lastIndexOf("\"");
			return text.substring(1, eidx);
		} else if (text.startsWith("{")) {
			return new EternalObject(text);
		} else if (text.startsWith("[")) {
			return new EternalArray(text);
		} else if (text.startsWith("[")) {
			return new EternalArray(text);
		} else if (text.indexOf('.') != -1) {
			return Double.parseDouble(text);
		} else if (text.equals("null")) {
			return null;
		} else {
			try {
				return Long.parseLong(text);
			} catch (NumberFormatException nfe) {
				throw new ParseException("Expected number but got '" + text + "'", 0);
			}
		}
	}

	public static String prettyPrint(int depth, Object object) {
		if (object instanceof String) {
			return "\"" + object + "\"";
		} else if (object instanceof EternalArray) {
			return ((EternalArray) object).prettyPrint(depth);
		} else if (object instanceof EternalObject) {
			return ((EternalObject) object).prettyPrint(depth);
		} else {
			return String.valueOf(object);
		}
	}

	private static int findClose(String data) {
		return findClose(data, 0);
	}

	private static int findClose(String data, int offset) {
		int squares = 0;
		int curlies = 0;
		for (int i = offset; i < data.length(); i++) {
			char ch = data.charAt(i);
			if (ch == ',' && squares == 0 && curlies == 0) {
				return i;
			} else if (ch == '{') {
				curlies++;
			} else if (ch == '}') {
				curlies--;
				if (curlies < 0) {
					return i;
				}
			} else if (ch == '[') {
				squares++;
			} else if (ch == ']') {
				squares--;
				if (squares < 0) {
					return i;
				}
			}
		}
		return data.length();
	}

	private String name;
	private Object object;

	public EternalObjectNotation(Object object) {
		this(null, object);
	}

	public EternalObjectNotation(String data) throws ParseException {
		String content = data == null ? "" : data.trim().replace('\n', ' ').replace('\r', ' ');
		if (content.matches("^[a-zA-Z_0-9]+:.*")) {
			int idx = content.indexOf(":");
			if (idx != -1) {
				name = data.substring(0, idx);
				content = data.substring(idx + 1);
			}
		}
		if (!content.equals("")) {
			object = parse(content);
		} else
			object = new EternalObject();
	}

	public EternalObjectNotation(String name, Object object) {
		this.object = object;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Object getObject() {
		return object;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toPrettyString() {
		StringBuilder bui = new StringBuilder();
		if (name != null) {
			bui.append(name);
			bui.append(":");
		}
		bui.append(prettyPrint(1, object));
		return bui.toString();
	}

	@Override
	public String toString() {
		StringBuilder bui = new StringBuilder();
		if (name != null) {
			bui.append(name);
			bui.append(":");
		}
		if (object != null) {
			bui.append(format(object));
		}
		return bui.toString();
	}

	public static class EternalArray extends ArrayList<Object> implements Serializable {
		public EternalArray() {
		}

		public EternalArray(String content) throws ParseException {
			content = content.trim();
			// Object
			if (content.startsWith("[")) {
				if (!content.endsWith("]")) {
					throw new ParseException("Incorrect format. No closing }.", 0);
				}
				content = content.substring(1, content.length() - 1);
				if (!content.trim().equals("")) {
					int sidx = 0;
					while (sidx < content.length()) {
						int close = findClose(content, sidx);
						String row = content.substring(sidx, close);
						add(parse(row));
						sidx = close + 1;
					}
				}
			}
		}

		public String prettyPrint(int depth) {
			StringBuilder indent = new StringBuilder();
			for (int i = 0; i < depth * 4; i++) {
				indent.append(' ');
			}
			StringBuilder bui = new StringBuilder("[");
			StringBuilder ii = new StringBuilder();
			for (Object key : this) {
				if (ii.length() > 1) {
					ii.append(",\n");
				} else {
					ii.append("\n");
				}
				ii.append(indent);
				ii.append(EternalObjectNotation.prettyPrint(depth + 1, key));
			}
			if (ii.length() != 0) {
				ii.append("\n");
			}
			bui.append(ii);
			bui.append(indent);
			bui.append("]");
			return bui.toString();
		}

		@Override
		public String toString() {
			StringBuilder bui = new StringBuilder("[");
			for (Object value : this) {
				if (bui.length() > 1) {
					bui.append(",");
				}
				bui.append(format(value));
			}
			bui.append("]");
			return bui.toString();
		}
	}

	@SuppressWarnings("serial")
	public static class EternalObject extends LinkedHashMap<Object, Object> implements Serializable {
		public EternalObject() {
			super();
		}

		public EternalObject(String content) throws ParseException {
			this();
			parseContent(content);
		}

		public void parseContent(String content) throws ParseException {
			content = content.trim();
			// Object
			if (content.startsWith("{")) {
				if (!content.endsWith("}")) {
					throw new ParseException("Incorrect format. No closing }.", 0);
				}
				int sidx = 0;
				while (true) {
					// Extract the key
					int idx = content.indexOf('[', sidx);
					if (idx == -1) {
						break;
					}
					int eidx = content.indexOf(']', idx);
					String keyString = content.substring(idx + 1, eidx);
					Object key;
					if (!keyString.startsWith("\"") && !keyString.endsWith("\"")) {
						key = Long.parseLong(keyString);
					} else {
						key = keyString.substring(1, keyString.length() - 1);
					}
					// Extract the value
					int vidx = content.indexOf('=', eidx + 1);
					if (vidx == -1) {
						throw new ParseException("Incorrect format, expected '='.", 0);
					}
					// Find the close of this value
					String valueText = content.substring(vidx + 1);
					int close = findClose(valueText);
					valueText = valueText.substring(0, close);
					put(key, parse(valueText));
					sidx = vidx + close;
				}
			}
		}

		public String prettyPrint(int depth) {
			StringBuilder indent = new StringBuilder();
			for (int i = 0; i < depth * 4; i++) {
				indent.append(' ');
			}
			StringBuilder bui = new StringBuilder("{");
			StringBuilder ii = new StringBuilder();
			for (Object key : keySet()) {
				if (ii.length() > 1) {
					ii.append(",\n");
				} else {
					ii.append("\n");
				}
				ii.append(indent);
				ii.append("[");
				if (key instanceof String) {
					ii.append("\"");
				}
				ii.append(String.valueOf(key));
				if (key instanceof String) {
					ii.append("\"");
				}
				ii.append("]=");
				ii.append(EternalObjectNotation.prettyPrint(depth + 1, get(key)));
			}
			if (ii.length() != 0) {
				ii.append("\n");
			}
			bui.append(ii);
			bui.append(indent);
			bui.append("}");
			return bui.toString();
		}

		@Override
		public String toString() {
			return toString(true);
		}

		public String toString(boolean quoteKeys) {
			StringBuilder bui = new StringBuilder("{");
			for (Object key : keySet()) {
				if (bui.length() > 1) {
					bui.append(",");
				}
				if (quoteKeys) {
					bui.append("[");
					if (key instanceof String) {
						bui.append("\"");
					}
				}
				bui.append(String.valueOf(key));
				if (quoteKeys) {
					if (key instanceof String) {
						bui.append("\"");
					}
					bui.append("]");
				}
				bui.append("=");
				bui.append(format(get(key)));
			}
			bui.append("}");
			return bui.toString();
		}
	}
}

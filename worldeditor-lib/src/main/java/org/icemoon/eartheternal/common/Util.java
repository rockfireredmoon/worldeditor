package org.icemoon.eartheternal.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.vfs2.FileObject;
import org.icemoon.eartheternal.common.EternalObjectNotation.EternalArray;

public class Util {
	public static String compact(double v) {
		return ((long) v) == v ? String.valueOf((long) v) : String.valueOf(v);
	}

	public static void copyFileObjectToFile(final FileObject inputFileObject, final File outputFile) throws IOException {
		FileOutputStream out = new FileOutputStream(outputFile);
		try {
			InputStream in = inputFileObject.getContent().getInputStream();
			try {
				IOUtils.copy(in, out);
			} finally {
				in.close();
			}
		} finally {
			out.close();
		}
	}

	public static void copyFileToFileObject(final File inputFile, final FileObject outputFileObject) throws IOException {
		FileInputStream in = new FileInputStream(inputFile);
		try {
			OutputStream out = outputFileObject.getContent().getOutputStream();
			try {
				IOUtils.copy(in, out);
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	public static long count(String string, String search) {
		int r = -1;
		int c = 0;
		do {
			r = string.indexOf(search, r + 1);
			if (r != -1) {
				c++;
			}
		} while (r != -1);
		return c;
	}

	public static EternalArray createColorArray(List<RGB> colors) {
		EternalArray a = new EternalArray();
		for (RGB rgb : colors) {
			a.add(toHexNumber(rgb).toLowerCase());
		}
		return a;
	}

	public static String debugString(String string) {
		StringBuilder bui = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\r') {
				bui.append("\\r");
			} else if (c == '\n') {
				bui.append("\\n");
			} else {
				bui.append(c);
			}
		}
		return bui.toString();
	}

	public static String escapeHTMLForJavaScript(String text) {
		return StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(text));
	}

	public static String format(double number) {
		if ((long) number == number) {
			return String.valueOf((long) number);
		}
		return String.valueOf(number);
	}

	public static String formatElapsedTime(long timeLogged) {
		long time = timeLogged / 1000;
		int seconds = (int) (time % 60);
		int minutes = (int) ((time % 3600) / 60);
		int hours = (int) (time / 3600);
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static String getBasename(String name) {
		int idx = name.indexOf(".");
		if (idx != -1) {
			name = name.substring(0, idx);
		}
		return name;
	}

	public static boolean isNotNullOrEmpty(String sval) {
		return !isNullOrEmpty(sval);
	}

	public static boolean isNullOrEmpty(Object sval) {
		return sval == null || (sval instanceof String && sval.equals(""))
				|| (sval instanceof Number && ((Number) sval).doubleValue() == 0);
	}

	public static boolean isNullOrEmpty(String sval) {
		return sval == null || sval.equals("");
	}

	public static boolean matches(Object value, Object match) {
		if (isNullOrEmpty(match)) {
			return true;
		}
		return value instanceof String ? value.toString().toLowerCase().contains(((String) match).toLowerCase())
				: Objects.equals(value, match);
	}

	public static boolean matches(String value, String match) {
		if (isNullOrEmpty(match)) {
			return true;
		}
		return nonNull(value).toLowerCase().contains(match.toLowerCase());
	}

	public static String nonNull(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	public static boolean notMatches(Object value, Object match) {
		return !matches(value, match);
	}

	public static boolean notMatches(String value, String match) {
		return !matches(value, match);
	}

	public static long parseElapsedTime(String timeLogged) {
		StringTokenizer t = new StringTokenizer(timeLogged, ":");
		int hours = Integer.parseInt(t.nextToken());
		int minutes = Integer.parseInt(t.nextToken());
		int seconds = Integer.parseInt(t.nextToken());
		return (hours * 3600000) + (minutes * 60000) + (seconds * 1000);
	}

	public static String toBooleanString(boolean value) {
		return value ? "1" : "0";
	}

	public static String toCommaSeparatedList(Collection selectedFiles) {
		return toSeparatedList(selectedFiles, ",");
	}

	public static String toCommaSeparatedList(Object[] selectedFiles) {
		return toCommaSeparatedList(Arrays.asList(selectedFiles));
	}

	public static String toCompactNumberList(Collection<? extends Number> list) {
		return toCompactNumberList(list, ",");
	}

	public static String toCompactNumberList(Collection<? extends Number> list, String separator) {
		StringBuilder b = new StringBuilder();
		for (Number n : list) {
			if (b.length() > 0)
				b.append(separator);
			b.append(compact(n.doubleValue()));
		}
		return b.toString();
	}

	public static List<Double> toDoubleList(String listText) {
		while (listText.endsWith(";")) {
			listText = listText.substring(0, listText.length() - 1);
		}
		listText = listText.trim();
		List<Double> l = new ArrayList<Double>();
		if (Util.isNullOrEmpty(listText)) {
			l.add(0.0);
		} else {
			for (String s : listText.split(",")) {
				l.add(Double.parseDouble(s.trim()));
			}
		}
		return l;
	}

	public static String decamel(String val) {
		StringBuilder b = new StringBuilder();
		boolean wasUpper = false;
		for (char c : val.toCharArray()) {
			boolean isUpper = Character.isUpperCase(c);
			if (isUpper && !wasUpper) {
				wasUpper = isUpper;
				if (b.length() > 0)
					b.append(' ');
			}
			else if(Character.isLowerCase(c) && wasUpper) {
				wasUpper = false;
			}
			b.append(c);
		}
		return b.toString();
	}

	public static String toEnglish(Object object) {
		return toEnglish(object, true);
	}

	public static String toEnglish(Object o, boolean name) {
		if (o == null) {
			return "";
		}
		String str = String.valueOf(o);
		boolean newWord = true;
		StringBuffer newStr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			ch = Character.toLowerCase(ch);
			if (ch == '_') {
				ch = ' ';
			}
			if (ch == ' ') {
				newWord = true;
			}
			if (newWord) {
				ch = Character.toUpperCase(ch);
				newWord = false;
			}
			newStr.append(ch);
		}
		return newStr.toString();
	}

	public static String[] toFileURIList(FileObject[] children) {
		String[] l = new String[children.length];
		for (int i = 0; i < children.length; i++) {
			l[i] = children[i].getName().getURI();
		}
		return l;
	}

	public static String toHexDigits(int value) {
		return String.format("%02x", value);
	}

	public static String toHexNumber(RGB color) {
		return toHexDigits(color.getRed()) + toHexDigits(color.getGreen()) + toHexDigits(color.getBlue());
	}

	public static String toHexString(RGB color) {
		if (color == null) {
			return "auto";
		}
		return "#" + toHexNumber(color);
	}

	public static List<Integer> toIntegerList(String listText) {
		List<Integer> l = new ArrayList<Integer>();
		for (String s : listText.split(",")) {
			l.add(Integer.parseInt(s.trim()));
		}
		return l;
	}

	public static String toLocationList(List<Location> markerLocations) {
		StringBuilder bui = new StringBuilder();
		for (Location loc : markerLocations) {
			bui.append(loc.toString());
			bui.append(";");
		}
		return bui.toString();
	}

	public static List<Location> toLocationList(String locationText) {
		List<Location> l = new ArrayList<Location>();
		for (String s : locationText.split(";")) {
			String trim = s.trim();
			if (!trim.equals("")) {
				l.add(new Location(trim));
			}
		}
		return l;
	}

	public static Long toLong(String value, Long defaultValue) {
		value = value.trim();
		if (value.equals("")) {
			return defaultValue;
		}
		return Long.parseLong(value);
	}

	public static List<Long> toLongList(String listText) {
		while (listText.endsWith(";")) {
			listText = listText.substring(0, listText.length() - 1);
		}
		listText = listText.trim();
		List<Long> l = new ArrayList<Long>();
		if (Util.isNullOrEmpty(listText)) {
			l.add(0l);
		} else {
			for (String s : listText.split(",")) {
				l.add(Long.parseLong(s.trim()));
			}
		}
		return l;
	}

	public static String toMultilineText(String[] warpTo) {
		return warpTo == null ? "" : toSeparatedList(Arrays.asList(warpTo), "\n");
	}

	public static Collection<String> toNameValueStringList(Map<String, String> pairs) {
		List<String> l = new ArrayList<String>();
		for (Map.Entry<String, String> en : pairs.entrySet()) {
			l.add(en.getKey() + "=" + en.getValue());
		}
		return l;
	}

	public static List<RGB> toRGBList(List<String> rgbList) {
		List<RGB> rgbs = new ArrayList<RGB>();
		if (rgbList != null) {
			for (String rgb : rgbList) {
				rgbs.add(new Color(rgb));
			}
		}
		return rgbs;
	}

	public static String toSeparatedList(Collection list, String separator) {
		StringBuilder bui = new StringBuilder();
		if (list != null) {
			for (Object o : list) {
				if (bui.length() > 0) {
					bui.append(separator);
				}
				bui.append(String.valueOf(o));
			}
		}
		return bui.toString();
	}

	public static String trimDisplay(String text, int max) {
		if (max > 3 && text.length() > (max - 3)) {
			return text.substring(0, max - 3) + "...";
		}
		return text;
	}

	public static String trimTail(String text) {
		char[] c = text.toCharArray();
		for (int i = c.length - 1; i >= 0; i--) {
			char ch = c[i];
			if (ch != ' ' && ch != '\r' && ch != '\n' && ch != '\t') {
				return text.substring(0, i + 1);
			}
		}
		return "";
	}

	public static String removeNumbers(String sp) {
		StringBuilder b = new StringBuilder(sp.length());
		for (char c : sp.toCharArray()) {
			if (!Character.isDigit(c))
				b.append(c);
		}
		return b.toString();
	}

	public static String numbersOnly(String sp) {
		StringBuilder b = new StringBuilder(sp.length());
		for (char c : sp.toCharArray()) {
			if (Character.isDigit(c))
				b.append(c);
		}
		return b.toString();
	}

	public static String limit(String str, int maxLen) {
		if (str.length() > maxLen)
			str = str.substring(0, maxLen);
		return str;
	}
}

package org.icemoon.worldeditor;

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.RawValidationError;

@SuppressWarnings("serial")
public class LowASCIIValidator implements IValidator<String> {
	private boolean allowNewlines = true;
	private boolean allowTabs = true;

	public LowASCIIValidator() {
	}

	public LowASCIIValidator(boolean allowNewlines, boolean allowTabs) {
		this.allowNewlines = allowNewlines;
		this.allowTabs = allowTabs;
	}

	@Override
	public void validate(IValidatable<String> validatable) {
		Set<Character> bad = new HashSet<Character>();
		for (char c : validatable.getValue().toCharArray()) {
			if (c > 31 && c < 128 || (allowNewlines && (c == 10 || c == 13)) || (allowTabs && c == 7))
				continue;
			else if (!bad.contains(c))
				bad.add(c);
		}
		if (bad.size() > 0) {
			StringBuilder b = new StringBuilder();
			for (Character c : bad) {
				if (b.length() > 0)
					b.append(", ");
				if(c < 31)
					b.append(String.format("0x%02x", c));
				else {
					b.append("'");
					b.append(c);
					b.append("'");
				}
			}
			validatable.error(new RawValidationError(
					String.format("Invalid characters (including %s) in text, please only use ASCII range character (7 bit only).",
							b.toString())));
		}
	}
}
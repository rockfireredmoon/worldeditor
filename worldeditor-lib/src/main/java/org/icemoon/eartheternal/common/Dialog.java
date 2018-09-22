package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;

@SuppressWarnings("serial")
public class Dialog extends AbstractSeparateINIFileEntity<String, IDatabase> {
	public enum ParagraphType {
		SAY, EMOTE, WAIT
	}

	public enum DialogSequence {
		SEQUENTIAL, RANDOM
	}

	public static class DialogParagraph implements Serializable {
		private ParagraphType type = ParagraphType.SAY;
		private String value;

		public final ParagraphType getType() {
			return type;
		}

		public final void setType(ParagraphType type) {
			this.type = type;
		}

		public final String getValue() {
			return value;
		}

		public final void setValue(String value) {
			this.value = value;
		}
	}

	private List<DialogParagraph> paragraphs = new ArrayList<DialogParagraph>();
	private long minInterval = 20000;
	private long maxInterval = 30000;
	private DialogSequence sequence = DialogSequence.SEQUENTIAL;

	public Dialog() {
		this(null);
	}

	public Dialog(IDatabase database) {
		super(database);
	}

	public final DialogSequence getTrigger() {
		return sequence;
	}

	public final void setSequence(DialogSequence trigger) {
		this.sequence = trigger;
	}

	public final List<DialogParagraph> getParagraphs() {
		return paragraphs;
	}

	public final void setParagraphs(List<DialogParagraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public final long getMinInterval() {
		return minInterval;
	}

	public final void setMinInterval(long minInterval) {
		this.minInterval = minInterval;
	}

	public final long getMaxInterval() {
		return maxInterval;
	}

	public final void setMaxInterval(long maxInterval) {
		this.maxInterval = maxInterval;
	}

	@Override
	protected void reset() {
		super.reset();
		paragraphs.clear();
		minInterval = 20000;
		maxInterval = 30000;
		sequence = DialogSequence.SEQUENTIAL;
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	protected INIReader<AbstractINIFileEntity<String, IDatabase>, String, IDatabase> createReader(FileObject f) {
		INIReader<AbstractINIFileEntity<String, IDatabase>, String, IDatabase> reader = super.createReader(f);
		reader.setStart("ENTRY");
		return reader;
	}

	@Override
	public void set(String name, String value, String section) {
		if (section.equals("ENTRY")) {
			if (name.equalsIgnoreCase("MININTERVAL")) {
				minInterval = Long.parseLong(value);
			} else if (name.equalsIgnoreCase("MAXINTERVAL")) {
				maxInterval = Long.parseLong(value);
			} else if (name.equalsIgnoreCase("TRIGGER")) {
				sequence = DialogSequence.values()[Integer.parseInt(value)];
			} else if (!name.equals("")) {
				Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
			}
		} else if (section.equalsIgnoreCase("PARAGRAPH")) {
			if (name.equals("")) {
				paragraphs.add(new DialogParagraph());
			} else if (name.equalsIgnoreCase("TYPE")) {
				paragraphs.get(paragraphs.size() - 1).setType(ParagraphType.values()[Integer.parseInt(value)]);
			} else if (name.equalsIgnoreCase("VALUE")) {
				paragraphs.get(paragraphs.size() - 1).setValue(value);
			} else if (!name.equals("")) {
				Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
			}
		} else {
			Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
		}
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("MinInterval=" + minInterval);
		writer.println("MaxInterval=" + maxInterval);
		writer.println("Sequence=" + sequence.ordinal());
		writer.println();
		for (DialogParagraph p : paragraphs) {
			writer.println("[PARAGRAPH]");
			writer.println("Type=" + p.getType().ordinal());
			writer.println("Value=" + p.getValue());
		}
	}
}

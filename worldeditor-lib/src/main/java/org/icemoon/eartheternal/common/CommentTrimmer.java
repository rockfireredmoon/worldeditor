package org.icemoon.eartheternal.common;

public class CommentTrimmer {
	private String comment;
	private String content;

	public CommentTrimmer(String line) {
		int cidx = line.length();
		while (true) {
			int idx = line.lastIndexOf(';', cidx);
			if (idx == -1)
				break;
			cidx = idx - 1;
		}
		if (cidx == line.length())
			content = line;
		else {
			content = line.substring(0, cidx).trim();
			comment = line.substring(cidx + 1).trim();
			while(comment.startsWith(";"))
				comment = comment.substring(1);
		}
	}

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	public final String getContent() {
		return content;
	}

	public final void setContent(String content) {
		this.content = content;
	}
}

package org.icemoon.eartheternal.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

public class INIWriter extends PrintWriter {

	private long count;

	public INIWriter(File file) throws FileNotFoundException {
		super(file);
	}

	public INIWriter(OutputStream out) {
		super(out);
	}

	public INIWriter(Writer w) {
		super(w);
	}

	public long getCount() {
		return count;
	}

	@Override
	public void println() {
		print("\n");
		flush();
	}
	
	public void writeGroupedSeparatedList(List<?> list, int maxGroup, String separator) {
		for (int i = 0; i < list.size(); i++) {
			if (i % 10 == 0) {
				if (i > 0) {
					println("");
				}
				print("Abilities=" + list.get(i));
			} else {
				print("," + list.get(i));
			}
		}
		if (list.size() > 0) {
			println("");
		}
	}

	@Override
	public void write(String s, int off, int len) {
		s = s.substring(off, off + len);
		s = s.replace("\r\n", "___CRLF___");
		s = s.replace("\n", "___CRLF___");
		s = s.replace("\r", "___CRLF___");
		s = s.replace("___CRLF___", "\r\n");
		super.write(s, 0, s.length());
		count += Util.count(s, "\r\n");
	}

}
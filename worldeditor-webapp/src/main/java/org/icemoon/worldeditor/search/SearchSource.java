package org.icemoon.worldeditor.search;

import java.io.Serializable;
import java.util.Iterator;

public interface SearchSource extends Serializable {
	Iterator<SearchResult> search(String search);
}

package org.icemoon.eartheternal.common;

import java.util.Comparator;
import java.util.List;


public class HSVComparator implements Comparator<List<RGB>> {
	
	public final static HSVComparator DEFAULT = new HSVComparator();
	
	@Override
	public int compare(List<RGB> l1, List<RGB> l2) {
		for (int i = 0; i < l1.size(); i++) {
			RGB o1 = l1.get(i);
			if (i >= l2.size()) {
				return 1;
			}
			RGB o2 = l2.get(i);
			int r = new Integer(o1.getRed()).compareTo(o2.getRed());
			if (r == 0) {
				r = new Integer(o1.getGreen()).compareTo(o2.getGreen());
				if (r == 0) {
					r = new Integer(o1.getBlue()).compareTo(o2.getBlue());
					if (r != 0) {
						return r;
					}
				} else {
					return r;
				}
			} else {
				return r;
			}
		}
		return 0;
	}
}
package org.icemoon.eartheternal.common;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.icemoon.eartheternal.common.Appearance.ClothingType;

public class SetItem implements Serializable {

	private final static Map<String, Pattern> patterns = new HashMap<String, Pattern>();
	private final static Map<String, String> icons = new HashMap<String, String>();
	static {
		// Load the patterns used to determine decent names for generated set
		// items
		try {
			BufferedReader r = new BufferedReader(
				new InputStreamReader(SetItem.class.getResource("/itempatterns.txt").openStream()));
			try {
				String line;
				while ((line = r.readLine()) != null) {
					line = line.trim();
					if (!line.startsWith("#") && line.length() > 0) {
						int idx = line.indexOf("=");
						if (idx == -1) {
							Log.error("Patterns", "Invalid pattern syntax '" + line + "'");
						}
						final String val = line.substring(idx + 1);
						String[] args = val.split("\\|");
						final String key = line.substring(0, idx);
						if (args.length > 1) {
							icons.put(key, args[1]);
						}
						patterns.put(key, Pattern.compile(args[0], Pattern.CASE_INSENSITIVE));
					}
				}
			} finally {
				r.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<SetItem> createSetItems(Appearance ap) {
		List<SetItem> l = new ArrayList<SetItem>();
		Map<EquipType, SetItem> e = new HashMap<EquipType, SetItem>();
		if (ap != null) {
			for (ClothingItem i : ap.getClothing()) {
				EquipType t = i.getType().toEquipType();
				if (t != null && !e.containsKey(t)) {
					SetItem item = new SetItem("", t);
					item.setClothingItem(i);

					e.put(t, item);
					l.add(item);
				}
			}
			for (AttachmentItem i : ap.getAttachments()) {
				EquipType t = i.getEquipType();
				if (t != null) {
					SetItem item = e.get(t);
					if (item == null) {
						item = new SetItem("", t);
						e.put(t, item);
						l.add(item);
					}
					item.getAttachmentItems().add(i);
				}
			}
		}

		for (SetItem i : l) {

			// Determine the best background color icon to use
			List<RGB> rgbs = new ArrayList<RGB>();
			if (i.getClothingItem() != null) {
				rgbs.addAll(i.getClothingItem().getColors());
			}
			for (AttachmentItem a : i.getAttachmentItems()) {
				rgbs.addAll(a.getColors());
			}
			String bgIcon = "Icon-32-BG-Black.png";
			if (rgbs.size() > 0) {
				Map<RGB, Integer> count = new HashMap<RGB, Integer>();
				for (RGB r : rgbs) {
					Integer c = count.get(r);
					if (c == null) {
						c = new Integer(0);
					}
					count.put(r, c + 1);
				}
				List<Entry<RGB, Integer>> x = new ArrayList<Map.Entry<RGB, Integer>>(count.entrySet());
				Collections.sort(x, new Comparator<Entry<RGB, Integer>>() {
					@Override
					public int compare(Entry<RGB, Integer> o1, Entry<RGB, Integer> o2) {
						return o1.getValue().compareTo(o2.getValue());
					}
				});

				// Convert to HSV
				RGB mostCommonColor = x.get(0).getKey();
				float[] hsv = new float[3];
				Color.RGBtoHSB(mostCommonColor.getRed(), mostCommonColor.getGreen(), mostCommonColor.getBlue(), hsv);
				int[] hsvi = { (int) (hsv[0] * 360.0), (int) (hsv[1] * 100.0), (int) (hsv[2] * 100.0) };

				if (hsvi[0] < 6 && hsvi[1] > 80 && hsvi[2] > 32) {
					bgIcon = "Icon-32-BG-Red.png";
				} else if (hsvi[0] >= 6 && hsvi[0] < 58 && hsvi[1] > 80 && hsvi[2] > 62) {
					bgIcon = "Icon-32-BG-Yellow.png";
				} else if (hsvi[0] >= 58 && hsvi[0] < 60 && hsvi[1] > 60 && hsvi[2] > 42) {
					bgIcon = "Icon-32-BG-Divine.png";
				} else if (hsvi[0] >= 60 && hsvi[0] < 147 && hsvi[1] > 50 && hsvi[2] > 50) {
					bgIcon = "Icon-32-BG-Green.png";
				} else if (hsvi[0] >= 147 && hsvi[0] < 188 && hsvi[1] > 50 && hsvi[2] > 50) {
					bgIcon = "Icon-32-BG-Cyan.png";
				} else if (hsvi[0] >= 188 && hsvi[0] < 196 && hsvi[1] > 50 && hsvi[2] > 50) {
					bgIcon = "Icon-32-BG-Aqua.png";
				} else if (hsvi[0] >= 196 && hsvi[0] < 248 && hsvi[1] > 50 && hsvi[2] > 50) {
					bgIcon = "Icon-32-BG-Blue.png";
				} else if (hsvi[0] >= 248 && hsvi[0] < 300 && hsvi[1] > 50 && hsvi[2] > 50) {
					bgIcon = "Icon-32-BG-Purple.png";
				} else if ((hsvi[0] >= 300 || hsvi[0] <= 6) && hsvi[1] < 50 && hsvi[2] > 50) {
					bgIcon = "Icon-32-BG-Pink.png";
				} else if (hsvi[1] < 12 && hsvi[1] < 80) {
					bgIcon = "Icon-32-BG-Grey.png";
				} else if (hsvi[1] < 12 && hsvi[2] >= 80) {
					// White
					bgIcon = null;
				}
			}
			i.setBackgroundIcon(bgIcon);

			// Maybe we can get some default labels for the items using the
			// patterns
			if (i.getClothingItem() != null) {
				String label = matchAsset(i.getClothingItem().getAsset());
				if (label != null) {
					i.setDefaultLabel(label);
				}
			}
			for (AttachmentItem a : i.getAttachmentItems()) {
				String label = matchAsset(a.getAsset());
				if (label != null) {
					i.setDefaultLabel(label);
					break;
				}
			}

			// Look for an icon for the label
			String labelIcon = icons.get(i.getDefaultLabel());
			if (labelIcon != null) {
				i.setIcon(labelIcon);
			}

			// Make sure shoulders are the right way around
			if (i.getEquip().equals(EquipType.SHOULDERS) && i.getAttachmentItems().size() > 1) {
				if (i.getAttachmentItems().get(0).getNode().equals("right_shoulder")) {
					i.getAttachmentItems().add(1, i.getAttachmentItems().remove(0));
				}
			}
		}

		return l;
	}
	public static Map<String, Pattern> getPatterns() {
		return patterns;
	}
	private static String matchAsset(String assetName) {
		for (String k : patterns.keySet()) {
			Pattern p = patterns.get(k);
			if (p.matcher(assetName).matches()) {
				return k;
			}
		}
		return null;
	}
	private String name;
	private EquipType equip;
	private ClothingItem clothingItem;
	private List<AttachmentItem> attachmentItems = new ArrayList<AttachmentItem>();
	private String icon;
	private String label;

	private String defaultLabel;

	private String backgroundIcon;

	private long cost;

	public SetItem(String name, EquipType equip) {
		this.setName(name);
		this.setEquip(equip);
		final ClothingType clothingItemType = equip.toClothingItemType();
		if (clothingItemType == null) {
			this.setDefaultLabel(equip.toString());
		} else {
			if (clothingItemType.equals(ClothingType.ARMS)) {
				// Better default label for "arms"
				this.setDefaultLabel("Sleeves");
			} else {
				this.setDefaultLabel(clothingItemType.toString());
			}
		}
		this.setIcon(equip.getDefaultIcon());
	}

	public List<AttachmentItem> getAttachmentItems() {
		return attachmentItems;
	}

	public String getBackgroundIcon() {
		return backgroundIcon;
	}

	public ClothingItem getClothingItem() {
		return clothingItem;
	}

	public long getCost() {
		return cost;		
	}

	public String getDefaultLabel() {
		return defaultLabel;
	}

	public EquipType getEquip() {
		return equip;
	}

	public String getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

	public void setAttachmentItems(List<AttachmentItem> attachmentItems) {
		this.attachmentItems = attachmentItems;
	}

	public void setBackgroundIcon(String backgroundIcon) {
		this.backgroundIcon = backgroundIcon;
	}

	public void setClothingItem(ClothingItem clothingItem) {
		this.clothingItem = clothingItem;
	}

	public void setCost(long cost) {
		this.cost = cost;		
	}

	public void setEquip(EquipType equip) {
		this.equip = equip;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void setDefaultLabel(String defaultLabel) {
		this.defaultLabel = defaultLabel;
	}
}
package org.icemoon.eartheternal.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class GameItem extends AbstractMultiINIFileEntity<Long, IDatabase> {
	// public final static int COLOR_DIVISOR = 32;
	public final static float COLOR_DIVISOR = 10.0f;
	private final static Set<String> WORD_BLACKLIST = new HashSet<String>();
	static {
		try {
			InputStream in = GameItem.class.getResourceAsStream("/commonwords.txt");
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = r.readLine()) != null) {
					line = line.trim();
					if (!line.startsWith("#") && !line.equals("")) {
						WORD_BLACKLIST.add(line);
					}
				}
			} finally {
				in.close();
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	private String displayName;
	private Type type = Type.UNKNOWN;
	private String icon1;
	private String icon2;
	private int containerSlots;
	private int level;
	private BindingType bindingType;
	private EquipType equipType;
	private ItemQuality quality;
	private int minUseLevel;
	private boolean ownershipRestriction;
	private ArmourType armourType;
	private int armourResistMelee;
	private int armourResistFire;
	private int armourResistFrost;
	private int armourResistMystic;
	private int armourResistDeath;
	private ItemAppearance appearance;
	private WeaponType weaponType;
	private SpecialItemType specialItemType;
	// Value?
	private long value;
	private int valueType;
	// Not sure -
	private String subText;
	private String flavorText;
	private int ivMax1;
	private int ivType1;
	private int ivMax2;
	private int ivType2;
	// Weapon plans
	private long resultItemId;
	private long keyComponentId;
	private List<Long> craftItemIds = new ArrayList<Long>();
	// Damage
	private int weaponDamageMin;
	private int weaponDamageMax;
	// Bonuses
	private int bonusDexterity;
	private int bonusStrength;
	private int bonusPsyche;
	private int bonusSpirit;
	private int bonusConstitution;
	// Potion, scrolls etc
	private long useAbilityId;
	private long actionAbilityId;
	// Charm
	private float healingMod;
	private float meleeHitMod;
	private float meleeCritMod;
	private float regenHealthMod;
	private float castSpeedMod;
	private float attackSpeedMod;
	private float blockMod;
	private float parryMod;
	private float runSpeedMod;
	private float magicHitMod;
	private float magicCritMod;
	private List<RGB> itemColours;
	private List<RGB> roughItemColours;
	private Map<String, String> params = new HashMap<String, String>();
	private String normalisedName;
	private int autoTitleType;
	private int weaponExtraDamageRating;
	private int weaponExtraDamageType;
	private int bonusWill;
	private int bonusHealth;
	private int speed;
	private long copper;
	private int equipEffectId;

	public GameItem() {
		this(null);
	}

	public GameItem(IDatabase database) {
		super(database);
	}

	public final long getActionAbilityId() {
		return actionAbilityId;
	}

	public ItemAppearance getAppearance() {
		return appearance;
	}

	public int getArmourResistDeath() {
		return armourResistDeath;
	}

	public int getArmourResistFire() {
		return armourResistFire;
	}

	public int getArmourResistFrost() {
		return armourResistFrost;
	}

	public int getArmourResistMelee() {
		return armourResistMelee;
	}

	public int getArmourResistMystic() {
		return armourResistMystic;
	}

	public ArmourType getArmourType() {
		return armourType;
	}

	public final float getAttackSpeedMod() {
		return attackSpeedMod;
	}

	public final int getAutoTitleType() {
		return autoTitleType;
	}

	public BindingType getBindingType() {
		return bindingType;
	}

	public final float getBlockMod() {
		return blockMod;
	}

	public int getBonusConstitution() {
		return bonusConstitution;
	}

	public int getBonusDexterity() {
		return bonusDexterity;
	}

	public final int getBonusHealth() {
		return bonusHealth;
	}

	public final int getBonusPsyche() {
		return bonusPsyche;
	}

	public int getBonusSpirit() {
		return bonusSpirit;
	}

	public int getBonusStrength() {
		return bonusStrength;
	}

	public final int getBonusWill() {
		return bonusWill;
	}

	public final float getCastSpeedMod() {
		return castSpeedMod;
	}

	public int getContainerSlots() {
		return containerSlots;
	}

	public final long getCopper() {
		return copper;
	}

	public final List<Long> getCraftItemIds() {
		return craftItemIds;
	}

	public String getDisplayName() {
		return displayName;
	}

	public final int getEquipEffectId() {
		return equipEffectId;
	}

	public EquipType getEquipType() {
		return equipType;
	}

	public String getFlavorText() {
		return flavorText;
	}

	public final float getHealingMod() {
		return healingMod;
	}

	public final String getIcon1() {
		return icon1;
	}

	public final String getIcon2() {
		return icon2;
	}

	public synchronized List<RGB> getItemColours(boolean rough) {
		if ((rough && roughItemColours == null) || (!rough && itemColours == null)) {
			ItemAppearance app = getAppearance();
			List<RGB> c = new ArrayList<RGB>();
			if (app != null) {
				for (AttachmentItem a : app.getAttachments()) {
					for (RGB x : a.getColors()) {
						if (rough) {
							x = Color.roughenHSV(x, COLOR_DIVISOR);
						}
						if (!c.contains(x)) {
							c.add(x);
						}
					}
				}
				if (app.getClothingColor() != null) {
					for (RGB x : app.getClothingColor()) {
						if (rough) {
							x = Color.roughenHSV(x, COLOR_DIVISOR);
						}
						if (!c.contains(x)) {
							c.add(x);
						}
					}
				}
			}
			Collections.sort(c);
			if (rough) {
				roughItemColours = c;
			} else {
				itemColours = c;
			}
		}
		return rough ? roughItemColours : itemColours;
	}

	public int getDynamicMax(int type) {
		if (ivType1 == type)
			return ivMax1;
		if (ivType2 == type)
			return ivMax2;
		return -1;
	}

	public final int getIvMax1() {
		return ivMax1;
	}

	public final int getIvMax2() {
		return ivMax2;
	}

	public final int getIvType1() {
		return ivType1;
	}

	public final int getIvType2() {
		return ivType2;
	}

	public final long getKeyComponentId() {
		return keyComponentId;
	}

	public int getLevel() {
		return level;
	}

	public final float getMagicCritMod() {
		return magicCritMod;
	}

	public final float getMagicHitMod() {
		return magicHitMod;
	}

	public final float getMeleeCritMod() {
		return meleeCritMod;
	}

	public final float getMeleeHitMod() {
		return meleeHitMod;
	}

	public int getMinUseLevel() {
		return minUseLevel;
	}

	public synchronized String getNormalisedName() {
		if (normalisedName == null && getDisplayName() != null) {
			final String displayName = getDisplayName().toLowerCase().replace("(", " (");
			List<String> wl = new ArrayList<String>(Arrays.asList(displayName.split("[ ]+")));
			for (Iterator<String> it = wl.iterator(); it.hasNext();) {
				String i = it.next();
				if (WORD_BLACKLIST.contains(i)) {
					it.remove();
				}
			}
			StringBuilder bi = new StringBuilder();
			for (String w : wl) {
				if (bi.length() > 0) {
					bi.append(" ");
				}
				bi.append(w);
			}
			normalisedName = bi.toString();
			System.out.println("Normalised: " + normalisedName);
		}
		return normalisedName;
	}

	public final int getNumberOfItems() {
		return craftItemIds.size();
	}

	public Map<String, String> getParams() {
		return params;
	}

	public final float getParryMod() {
		return parryMod;
	}

	public ItemQuality getQuality() {
		return quality;
	}

	public final float getRegenHealthMod() {
		return regenHealthMod;
	}

	public final long getResultItemId() {
		return resultItemId;
	}

	public final float getRunSpeedMod() {
		return runSpeedMod;
	}

	public final SpecialItemType getSpecialItemType() {
		return specialItemType;
	}

	public final int getSpeed() {
		return speed;
	}

	public final String getSubText() {
		return subText;
	}

	public Type getType() {
		return type;
	}

	public final long getUseAbilityId() {
		return useAbilityId;
	}

	public long getValue() {
		return value;
	}

	public final int getValueType() {
		return valueType;
	}

	public final int getWeaponDamageMax() {
		return weaponDamageMax;
	}

	public final int getWeaponDamageMin() {
		return weaponDamageMin;
	}

	public final int getWeaponExtraDamageRating() {
		return weaponExtraDamageRating;
	}

	public final int getWeaponExtraDamageType() {
		return weaponExtraDamageType;
	}

	public final WeaponType getWeaponType() {
		return weaponType;
	}

	public boolean isAssetUsed(String asset) {
		if (appearance != null) {
			if (asset.equalsIgnoreCase(appearance.getClothingType())) {
				return true;
			}
			for (AttachmentItem a : appearance.getAttachments()) {
				if (asset.equalsIgnoreCase(a.getAsset())) {
					return true;
				}
			}
		}
		return false;
	}

	public final boolean isCharm() {
		return Type.CHARM.equals(type);
	}

	public boolean isOwnershipRestriction() {
		return ownershipRestriction;
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("mID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equals("mType")) {
			type = Type.fromCode(Integer.parseInt(value));
		} else if (name.equals("mDisplayName")) {
			displayName = value;
		} else if (name.equals("mContainerSlots")) {
			containerSlots = Integer.parseInt(value);
		} else if (name.equals("mIcon")) {
			String[] icons = value.split("\\|");
			if (icons.length > 0) {
				icon1 = icons[0];
				if (icons.length > 1) {
					icon2 = icons[1];
				}
			}
		} else if (name.equals("mBindingType")) {
			bindingType = BindingType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mEquipType")) {
			equipType = EquipType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mFlavorText")) {
			flavorText = value;
		} else if (name.equals("mValue")) {
			this.value = Long.parseLong(value);
			
			
		} else if (name.equals("mValueType")) {
			this.valueType = Integer.parseInt(value);
		} else if (name.equals("mLevel")) {
			level = Integer.parseInt(value);
		} else if (name.equals("mMinUseLevel")) {
			minUseLevel = Integer.parseInt(value);
		} else if (name.equals("mAutoTitleType")) {
			autoTitleType = Integer.parseInt(value);
		} else if (name.equals("mQualityLevel")) {
			quality = ItemQuality.fromCode(Integer.parseInt(value));
		} else if (name.equals("mOwnershipRestriction")) {
			ownershipRestriction = "1".equals(value);
		} else if (name.equals("mArmorType")) {
			armourType = ArmourType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mSpecialItemType")) {
			specialItemType = SpecialItemType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mWeaponType")) {
			weaponType = WeaponType.fromCode(Integer.parseInt(value));
		} else if (name.equals("mAppearance")) {
			try {
				appearance = new ItemAppearance(value);
			} catch (ParseException e) {
				Log.error("Item", "Failed to parse appearance in " + getFile() + ". " + e.getMessage());
			}
		} else if (name.equals("mArmorResistMelee")) {
			armourResistMelee = Integer.parseInt(value);
		} else if (name.equals("mArmorResistFire")) {
			armourResistFire = Integer.parseInt(value);
		} else if (name.equals("mArmorResistFrost")) {
			armourResistFrost = Integer.parseInt(value);
		} else if (name.equals("mArmorResistMystic")) {
			armourResistMystic = Integer.parseInt(value);
		} else if (name.equals("mArmorResistDeath")) {
			armourResistDeath = Integer.parseInt(value);
		} else if (name.equals("mBonusConstitution")) {
			bonusConstitution = Integer.parseInt(value);
		} else if (name.equals("mBonusSpirit")) {
			bonusSpirit = Integer.parseInt(value);
		} else if (name.equals("mBonusDexterity")) {
			bonusDexterity = Integer.parseInt(value);
		} else if (name.equals("mBonusPsyche")) {
			bonusPsyche = Integer.parseInt(value);
		} else if (name.equals("mBonusStrength")) {
			bonusStrength = Integer.parseInt(value);
		} else if (name.equals("resultItemId")) {
			resultItemId = Long.parseLong(value);
		} else if (name.equals("keyComponentId")) {
			keyComponentId = Long.parseLong(value);
		} else if (name.equals("craftItemDefId")) {
			craftItemIds.add(Long.parseLong(value));
		} else if (name.equals("mWeaponDamageMin")) {
			weaponDamageMin = Integer.parseInt(value);
		} else if (name.equals("mWeaponDamageMax")) {
			weaponDamageMax = Integer.parseInt(value);
		} else if (name.equals("mIvType1")) {
			ivType1 = Integer.parseInt(value);
		} else if (name.equals("mIvMax1")) {
			ivMax1 = Integer.parseInt(value);
		} else if (name.equals("mIvType2")) {
			ivType2 = Integer.parseInt(value);
		} else if (name.equals("mIvMax2")) {
			ivMax2 = Integer.parseInt(value);
		} else if (name.equals("mSv1")) {
			subText = value;
		} else if (name.equals("isCharm")) {
			// Rely on type ????
		} else if (name.equals("numberOfItems")) {
			// Already known ????
		} else if (name.equals("mUseAbilityId")) {
			useAbilityId = Long.parseLong(value);
		} else if (name.equals("mActionAbilityId")) {
			actionAbilityId = Long.parseLong(value);
		} else if (name.equals("mHealingMod")) {
			healingMod = Float.parseFloat(value);
		} else if (name.equals("mBlockMod")) {
			blockMod = Float.parseFloat(value);
		} else if (name.equals("mMeleeHitMod")) {
			meleeHitMod = Float.parseFloat(value);
		} else if (name.equals("mMeleeCritMod")) {
			meleeCritMod = Float.parseFloat(value);
		} else if (name.equals("mRegenHealthMod")) {
			regenHealthMod = Float.parseFloat(value);
		} else if (name.equals("mCastSpeedMod")) {
			castSpeedMod = Float.parseFloat(value);
		} else if (name.equals("mAttackSpeedMod")) {
			attackSpeedMod = Float.parseFloat(value);
		} else if (name.equals("mParryMod")) {
			parryMod = Float.parseFloat(value);
		} else if (name.equals("mRunSpeedMod")) {
			runSpeedMod = Float.parseFloat(value);
		} else if (name.equals("mMagicHitMod")) {
			magicHitMod = Float.parseFloat(value);
		} else if (name.equals("mMagicCritMod")) {
			magicCritMod = Float.parseFloat(value);
		} else if (name.equals("Params")) {
			String[] a = value.split("&");
			for (String s : a) {
				int i = s.indexOf('=');
				if (i != -1)
					params.put(s.substring(0, i), s.substring(i + 1));
				else
					params.put(s, null);
			}
		} else if (name.equals("_mCopper")) {
			copper = Long.parseLong(value);
		} else if (name.equals("mWeaponExtraDamangeRating")) {
			weaponExtraDamageRating = Integer.parseInt(value);
		} else if (name.equals("mWeaponExtraDamageType")) {
			weaponExtraDamageType = Integer.parseInt(value);
		} else if (name.equals("mBonusWill")) {
			bonusWill = Integer.parseInt(value);
		} else if (name.equals("_mBonusHealth")) {
			bonusHealth = Integer.parseInt(value);
		} else if (name.equals("mEquipEffectId")) {
			equipEffectId = Integer.parseInt(value);
		} else if (!name.equals("")) {
			Log.todo("Item", "Unhandle property " + name + " = " + value);
		}
	}

	public final void setActionAbilityId(long actionAbilityId) {
		this.actionAbilityId = actionAbilityId;
	}

	public void setAppearance(ItemAppearance appearance) {
		this.appearance = appearance;
	}

	public void setArmourResistDeath(int armourResistDeath) {
		this.armourResistDeath = armourResistDeath;
	}

	public void setArmourResistFire(int armourResistFire) {
		this.armourResistFire = armourResistFire;
	}

	public void setArmourResistFrost(int armourResistFrost) {
		this.armourResistFrost = armourResistFrost;
	}

	public void setArmourResistMelee(int armourResistMelee) {
		this.armourResistMelee = armourResistMelee;
	}

	public void setArmourResistMystic(int armourResistMystic) {
		this.armourResistMystic = armourResistMystic;
	}

	public void setArmourType(ArmourType armourType) {
		this.armourType = armourType;
	}

	public final void setAttackSpeedMod(float attackSpeedMod) {
		this.attackSpeedMod = attackSpeedMod;
	}

	public final void setAutoTitleType(int autoTitleType) {
		this.autoTitleType = autoTitleType;
	}

	public void setBindingType(BindingType bindingType) {
		this.bindingType = bindingType;
	}

	public final void setBlockMod(float blockMod) {
		this.blockMod = blockMod;
	}

	public void setBonusConstitution(int bonusConstitution) {
		this.bonusConstitution = bonusConstitution;
	}

	public void setBonusDexterity(int bonusDexterity) {
		this.bonusDexterity = bonusDexterity;
	}

	public final void setBonusHealth(int bonusHealth) {
		this.bonusHealth = bonusHealth;
	}

	public final void setBonusPsyche(int bonusPsyche) {
		this.bonusPsyche = bonusPsyche;
	}

	public void setBonusSpirit(int bonusSpirit) {
		this.bonusSpirit = bonusSpirit;
	}

	public void setBonusStrength(int bonusStrength) {
		this.bonusStrength = bonusStrength;
	}

	public final void setBonusWill(int bonusWill) {
		this.bonusWill = bonusWill;
	}

	public final void setCastSpeedMod(float castSpeedMod) {
		this.castSpeedMod = castSpeedMod;
	}

	public final void setCharm(boolean charm) {
		throw new UnsupportedOperationException();
	}

	public void setContainerSlots(int containerSlots) {
		this.containerSlots = containerSlots;
	}

	public final void setCopper(long copper) {
		this.copper = copper;
	}

	public final void setCraftItemIds(List<Long> craftItemIds) {
		this.craftItemIds = craftItemIds;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public final void setEquipEffectId(int equipEffectId) {
		this.equipEffectId = equipEffectId;
	}

	public void setEquipType(EquipType equipType) {
		this.equipType = equipType;
	}

	public void setFlavorText(String flavorText) {
		this.flavorText = flavorText;
	}

	public final void setHealingMod(float healingMod) {
		this.healingMod = healingMod;
	}

	public final void setIcon1(String icon1) {
		this.icon1 = icon1;
	}

	public final void setIcon2(String icon2) {
		this.icon2 = icon2;
	}

	public final void setIvMax1(int ivMax1) {
		this.ivMax1 = ivMax1;
	}

	public final void setIvMax2(int ivMax2) {
		this.ivMax2 = ivMax2;
	}

	public final void setIvType1(int ivType1) {
		this.ivType1 = ivType1;
	}

	public final void setIvType2(int ivType2) {
		this.ivType2 = ivType2;
	}

	public final void setKeyComponentId(long keyComponentId) {
		this.keyComponentId = keyComponentId;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public final void setMagicCritMod(int magicCritMod) {
		this.magicCritMod = magicCritMod;
	}

	public final void setMagicHitMod(float magicHitMod) {
		this.magicHitMod = magicHitMod;
	}

	public final void setMeleeCritMod(float meleeCritMod) {
		this.meleeCritMod = meleeCritMod;
	}

	public final void setMeleeHitMod(float meleeHitMod) {
		this.meleeHitMod = meleeHitMod;
	}

	public void setMinUseLevel(int minUseLevel) {
		this.minUseLevel = minUseLevel;
	}

	public final void setNumberOfItems(int numberOfItems) {
		throw new UnsupportedOperationException("Cannot set number of items, change the craft items Ids list");
	}

	public void setOwnershipRestriction(boolean ownershipRestriction) {
		this.ownershipRestriction = ownershipRestriction;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public final void setParryMod(float parryMod) {
		this.parryMod = parryMod;
	}

	public void setQuality(ItemQuality quality) {
		this.quality = quality;
	}

	public final void setRegenHealthMod(float regenHealthMod) {
		this.regenHealthMod = regenHealthMod;
	}

	public final void setResultItemId(long resultItemId) {
		this.resultItemId = resultItemId;
	}

	public final void setRunSpeedMod(float runSpeedMod) {
		this.runSpeedMod = runSpeedMod;
	}

	public final void setSpecialItemType(SpecialItemType specialItemType) {
		this.specialItemType = specialItemType;
	}

	public final void setSpeed(int speed) {
		this.speed = speed;
	}

	public final void setSubText(String sv1) {
		this.subText = sv1;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public final void setUseAbilityId(long useAbilityId) {
		this.useAbilityId = useAbilityId;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public final void setValueType(int valueType) {
		this.valueType = valueType;
	}

	public final void setWeaponDamageMax(int weaponDamageMax) {
		this.weaponDamageMax = weaponDamageMax;
	}

	public final void setWeaponDamageMin(int weaponDamageMin) {
		this.weaponDamageMin = weaponDamageMin;
	}

	public final void setWeaponExtraDamageRating(int weaponExtraDamageRating) {
		this.weaponExtraDamageRating = weaponExtraDamageRating;
	}

	public final void setWeaponExtraDamageType(int weaponExtraDamageType) {
		this.weaponExtraDamageType = weaponExtraDamageType;
	}

	public final void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}

	@Override
	public String toString() {
		return Util.nonNull(displayName);
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		// Common
		writer.println("mID=" + getEntityId());
		writer.println("mType=" + type.code);
		writer.println("mDisplayName=" + displayName);
		if (appearance != null) {
			writer.println("mAppearance=" + appearance.toString());
		}
		if (StringUtils.isNotBlank(icon1) || StringUtils.isNotBlank(icon2))
			writer.println("mIcon=" + Util.nonNull(icon1) + "|" + Util.nonNull(icon2));
		if (level > 0) {
			writer.println("mLevel=" + level);
		}
		writer.println("mValue=" + value);
		if (valueType > 0) {
			writer.println("mValueType=" + valueType);
		}
		if (minUseLevel != 0) {
			writer.println("mMinUseLevel=" + minUseLevel);
		}
		if (quality != null) {
			writer.println("mQualityLevel=" + quality.getCode());
		}
		if (equipType != null) {
			writer.println("mEquipType=" + equipType.code);
		}
		if (bindingType != null) {
			writer.println("mBindingType=" + bindingType.code);
		}
		if (!Util.isNullOrEmpty(flavorText)) {
			writer.println("mFlavorText=" + flavorText);
		}
		if (autoTitleType > 0) {
			writer.println("mAutoTitleType=" + autoTitleType);
		}
		// Bonus stats (common)
		if (bonusConstitution > 0) {
			writer.println("mBonusConstitution=" + bonusConstitution);
		}
		if (bonusDexterity > 0) {
			writer.println("mBonusDexterity=" + bonusDexterity);
		}
		if (bonusPsyche > 0) {
			writer.println("mBonusPsyche=" + bonusPsyche);
		}
		if (bonusSpirit > 0) {
			writer.println("mBonusSpirit=" + bonusSpirit);
		}
		if (bonusStrength > 0) {
			writer.println("mBonusStrength=" + bonusStrength);
		}
		// Inventory stuff - not sure exactly
		if (ivMax1 > 0) {
			writer.println("mIvMax1=" + ivMax1);
		}
		if (ivMax2 > 0) {
			writer.println("mIvMax2=" + ivMax2);
		}
		if (ivType1 > 0) {
			writer.println("mIvType1=" + ivType1);
		}
		if (ivType2 > 0) {
			writer.println("mIvType2=" + ivType2);
		}
		if (weaponExtraDamageType > 0)
			writer.println("mWeaponExtraDamageType=" + weaponExtraDamageType);
		if (weaponExtraDamageRating > 0)
			writer.println("mWeaponExtraDamangeRating=" + weaponExtraDamageRating);
		if (bonusWill > 0)
			writer.println("mBonusWill=" + bonusWill);
		if (bonusHealth > 0)
			writer.println("_mBonusHealth=" + bonusHealth);
		if (copper > 0)
			writer.println("_mCopper=" + copper);
		if (speed > 0)
			writer.println("_mSpeed=" + speed);
		if (equipEffectId > 0)
			writer.println("mEquipEffectId=" + equipEffectId);
		// Bags
		if (type.equals(Type.CONTAINER)) {
			writer.println("mContainerSlots=" + flavorText);
		}
		// Armour
		if (type.equals(Type.ARMOUR) && armourType != null) {
			writer.println("mArmorType=" + armourType.code);
		}
		if (armourResistMelee > 0) {
			writer.println("mArmorResistMelee=" + armourResistMelee);
		}
		if (armourResistFire > 0) {
			writer.println("mArmorResistFire=" + armourResistFire);
		}
		if (armourResistFrost > 0) {
			writer.println("mArmorResistFrost=" + armourResistFrost);
		}
		if (armourResistMystic > 0) {
			writer.println("mArmorResistMystic=" + armourResistMystic);
		}
		if (armourResistDeath > 0) {
			writer.println("mArmorResistDeath=" + armourResistDeath);
		}
		// Weapon
		if (type.equals(Type.WEAPON)) {
			if (weaponType != null)
				writer.println("mWeaponType=" + weaponType.getCode());
			if (weaponDamageMin > 0) {
				writer.println("mWeaponDamageMin=" + weaponDamageMin);
			}
			if (weaponDamageMax > 0) {
				writer.println("mWeaponDamageMax=" + weaponDamageMax);
			}
		}
		// Charm
		if (type.equals(Type.CHARM)) {
			writer.println("isCharm=1");
			if (meleeHitMod > 0) {
				writer.println("mMeleeHitMod=" + meleeHitMod);
			}
			if (meleeHitMod > 0) {
				writer.println("mMeleeCritMod=" + meleeCritMod);
			}
			if (magicHitMod > 0) {
				writer.println("mMagicHitMod=" + magicHitMod);
			}
			if (magicCritMod > 0) {
				writer.println("mMagicCritMod=" + magicCritMod);
			}
			if (parryMod > 0) {
				writer.println("mParryMod=" + parryMod);
			}
			if (blockMod > 0) {
				writer.println("mBlockMod=" + blockMod);
			}
			if (runSpeedMod > 0) {
				writer.println("mRunSpeedMod=" + runSpeedMod);
			}
			if (regenHealthMod > 0) {
				writer.println("mRegenHealthMod=" + regenHealthMod);
			}
			if (attackSpeedMod > 0) {
				writer.println("mAttackSpeedMod=" + attackSpeedMod);
			}
			if (castSpeedMod > 0) {
				writer.println("mCastSpeedMod=" + castSpeedMod);
			}
			if (healingMod > 0) {
				writer.println("mHealingMod=" + healingMod);
			}
		}
		// Charm
		if (useAbilityId > 0) {
			writer.println("mUseAbilityId=" + useAbilityId);
		}
		if (actionAbilityId > 0) {
			writer.println("mActionAbilityId=" + actionAbilityId);
		}
		// Special
		if (type.equals(Type.SPECIAL)) {
			if (specialItemType != null)
				writer.println("mSpecialItemType=" + specialItemType.getCode());
		}
		// Plan
		if (type.equals(Type.RECIPE)) {
			writer.println("resultItemId=" + resultItemId);
			writer.println("keyComponentId=" + keyComponentId);
			writer.println("numberOfItems=" + craftItemIds.size());
			for (Long id : craftItemIds) {
				writer.println("craftItemDefId=" + id);
			}
		}
		// Trash?
		if (type.equals(Type.QUEST_ITEMS)) {
			writer.println("mSv1=" + subText);
		}
		// Pet
		for (Map.Entry<String, String> en : params.entrySet()) {
			writer.println("Params=" + en.getKey() + (en.getValue() == null ? "" : "=" + en.getValue()));
		}
	}

	public enum ArmourType {
		UNKNOWN(0), CLOTH(1), LIGHT(2), MEDIUM(3), HEAVY(4), SHIELD(5);
		public static ArmourType fromCode(int code) {
			for (ArmourType type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			Log.todo("Item", "Unhandle ArmourType code " + code);
			return null;
		}

		private int code;

		private ArmourType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}

	public enum BindingType {
		NORMAL(0), PICKUP(1), EQUIP(2);
		public static BindingType fromCode(int code) {
			for (BindingType type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			Log.todo("Item", "Unhandle BindingType code " + code);
			return null;
		}

		private int code;

		private BindingType(int code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}

	public enum Type {
		UNKNOWN(0), SYSTEM(1), WEAPON(2), ARMOUR(3), CHARM(4), CONSUMABLE(5), CONTAINER(6), BASIC(7), SPECIAL(8), QUEST_ITEMS(
				9), RECIPE(10);
		public static Type fromCode(int code) {
			for (Type type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			Log.todo("Item", "Unhandle Type code " + code);
			return null;
		}

		private int code;

		private Type(int code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}
}

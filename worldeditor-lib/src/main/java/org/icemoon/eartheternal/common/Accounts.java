package org.icemoon.eartheternal.common;

import org.apache.commons.io.FilenameUtils;

@SuppressWarnings("serial")
public class Accounts extends AbstractSeparateINIFileEntities<Account, Long, String, IUserData> {
	public final static String NEXT_ACCOUNT_ID = "NextAccountID";

	public Accounts(String... files) {
		this(null, files);
	}

	public Accounts(IUserData database, String... files) {
		super(database, Long.class, files);
		setMinId(1l);
		setMaxId(Long.valueOf(Integer.MAX_VALUE));
	}

	public Account getAccount(GameCharacter character) {
		for (Account acc : getValues()) {
			if (acc.getCharacters().contains(character.getEntityId())) {
				return acc;
			}
		}
		return null;
	}

	public Account getAccount(String username) {
		for (Account acc : getValues()) {
			if (username.equals(acc.getName())) {
				return acc;
			}
		}
		return null;
	}

	public Account getAccountByGrove(String grove) {
		for (Account acc : getValues()) {
			if (grove.equals(acc.getGroveName())) {
				return acc;
			}
		}
		return null;
	}

	@Override
	protected Account createItem() {
		return new Account(getDatabase());
	}

	@Override
	protected Long createKey(String filename) {
		return Long.parseLong(FilenameUtils.getBaseName(filename));
	}

	public synchronized void save(Account instance) {
		instance.setFile(getFile() + "/" + String.format("%08d", instance.getEntityId()) + ".txt");
		super.save(instance);
	}
}

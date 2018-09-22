package org.icemoon.eartheternal.common;

import org.apache.commons.vfs2.FileObject;

public interface IUserData extends IRoot {
	Accounts getAccounts();

	GameCharacters getCharacters();

	World<IUserData> getGroveList();

	SessionVars getSessionVars();
	
	FileObject getServerDirectory();
}

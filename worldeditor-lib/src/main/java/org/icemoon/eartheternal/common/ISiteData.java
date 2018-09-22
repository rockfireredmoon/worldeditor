package org.icemoon.eartheternal.common;

import java.security.Principal;
import java.util.prefs.Preferences;

import org.apache.commons.vfs2.FileObject;

public interface ISiteData extends IRoot {

	AttachmentsList getAttachmentsList();
	
	FileObject getServerDirectory();
	
	Preferences getPreferences(Principal user);
}

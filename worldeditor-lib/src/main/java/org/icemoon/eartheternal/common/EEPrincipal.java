package org.icemoon.eartheternal.common;

import java.security.Principal;
import java.util.List;

import org.icemoon.eartheternal.common.Account.Permission;

public interface EEPrincipal extends Principal {
	List<Permission> getPermissions();
}

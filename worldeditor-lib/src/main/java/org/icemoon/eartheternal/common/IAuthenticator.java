package org.icemoon.eartheternal.common;

public interface IAuthenticator {
	EEPrincipal login(String username, char[] password);
}

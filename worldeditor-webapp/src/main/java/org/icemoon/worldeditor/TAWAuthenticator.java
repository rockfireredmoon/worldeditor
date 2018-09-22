package org.icemoon.worldeditor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.icemoon.eartheternal.common.EEPrincipal;
import org.icemoon.eartheternal.common.IAuthenticator;
import org.icemoon.eartheternal.common.Account.Permission;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

public class TAWAuthenticator implements IAuthenticator {
	private String serverUrl;

	public TAWAuthenticator(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	@Override
	public EEPrincipal login(String username, char[] password) {
		try {
			HttpURLConnection connection = (HttpURLConnection) (new URL(serverUrl + "/user/token.json").openConnection());
			connection.setDoOutput(true); // Triggers POST.
			connection.setUseCaches(false);
			String charset = "UTF-8";
//			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/json");
			OutputStream output = connection.getOutputStream();
			try {
				output.write("".getBytes(charset));
			} finally {
				output.close();
			}
			String xcsrfToken;
			if (connection.getResponseCode() == 200) {
				InputStream response = connection.getInputStream();
				JsonObject o = (JsonObject) Jsoner.deserialize(new InputStreamReader(response, charset));
				xcsrfToken = o.getString("token");
			} else
				throw new IOException("Server returned " + connection.getResponseCode());
			// Now we have the token, we can attempt authentication
			connection = (HttpURLConnection) (new URL(serverUrl + "/user/login.json").openConnection());
			connection.setUseCaches(false);
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Cookie", "X-CSRF-TOKEN=" + xcsrfToken);
			connection.setRequestProperty("Content-Type", "application/json");
//			connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
//			connection.setRequestProperty("Content-Type", "x-www-form-urlencoded;charset=" + charset);
			 output = connection.getOutputStream();
			try {
//				output.write(("username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(new String(password), "UTF-8")).getBytes());
				JsonObject o = new JsonObject();
				o.put("username", username);
				o.put("password", new String(password));
				output.write(o.toJson().getBytes(charset));
			} finally {
				output.close();
			}
			if (connection.getResponseCode() == 200) {
				InputStream response = connection.getInputStream();
				JsonObject o = (JsonObject) Jsoner.deserialize(new InputStreamReader(response, charset));
				List<Permission> perms = new ArrayList<Permission>();
				final JsonObject userObj = (JsonObject) o.get("user");
				final JsonObject rolesObj = (JsonObject) userObj.get("roles");
				if (rolesObj != null) {
					for (Map.Entry<String, Object> en : rolesObj.entrySet()) {
						if (en.getValue().equals("administrator") || en.getValue().equals("developers")) {
							perms.add(Permission.ADMIN);
						}
					}
				}
				return new TAWPrincipal(username, xcsrfToken, o.getString("sessid"), o.getString("session_name"),
						userObj.getString("uid"), perms);
			} else if (connection.getResponseCode() == 401) {
				return null;
			} else
				throw new IOException("Server returned " + connection.getResponseCode());
		} catch (IOException ioe) {
			throw new IllegalStateException("I/O error.", ioe);
		} catch (DeserializationException dse) {
			throw new IllegalStateException("Failed to deserialize.", dse);
		}
	}
}

package de.fraunhofer.abm.util;

import java.util.Objects;

/**
 * Constants used by the platform
 * 
 * @author Hariharan
 *
 */
public class AbmApplicationConstants {
	private static final String ADMIN_ADDRESS = "adminAddress";
	private static final String SERVER_HOST = "serverHost";
	private static final String SERVER_PORT = "serverPort";
	private static final String USER_ADDRESS = "userAddress";
	private static final String USER_NAME = "userName";
	private static final String USER_PASSWORD = "userPassword";
	private static final String GOOGLE_CLIENT_ID = "googleClientId";
	private static final String GITHUB_TOKEN = "githubToken";

	/**
	 * Get property if defined as system property
	 * 
	 * @param name
	 *            Property Name
	 */
	private static String getIfDefined(String name) {
		String value = System.getProperty(name);
		Objects.requireNonNull(value);
		return value;
	}

	public static String[] adminAddress() {
		return new String[] { getIfDefined(ADMIN_ADDRESS) };
	}

	public static String serverHost() {
		return getIfDefined(SERVER_HOST);
	}

	public static String serverPort() {
		return getIfDefined(SERVER_PORT);
	}

	public static String googleClientId() {
		return getIfDefined(GOOGLE_CLIENT_ID);
	}

	public static String githubToken() {
		return getIfDefined(GITHUB_TOKEN);
	}

	public static String userAddress() {
		return getIfDefined(USER_ADDRESS);
	}

	public static String userName() {
		return getIfDefined(USER_NAME);
	}

	public static String userPassword() {
		return getIfDefined(USER_PASSWORD);
	}

}

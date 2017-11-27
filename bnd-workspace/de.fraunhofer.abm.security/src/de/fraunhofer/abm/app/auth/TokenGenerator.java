package de.fraunhofer.abm.app.auth;

import java.util.UUID;

public final class TokenGenerator {
	private TokenGenerator() {
	}

	/**
	 * Token for user registration activation. It can be extended to use JSON Web
	 * Tokens if needed.
	 * 
	 * @return
	 */
	public static String generateToken() {
		return UUID.randomUUID().toString();
	}
}

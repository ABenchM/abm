package de.fraunhofer.abm.app.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JwtTokenGenerator for create and verification of JWT tokens.
 * 
 * @author Hariharan
 *
 */
public final class JwtTokenGenerator {
	public static final String TOKEN_ISSUER = "ABM";

	// No need to instantiate
	private JwtTokenGenerator() {

	}

	public static String createToken(String uuid, String username) {
		int twoDays = 2;
		Instant expiration = Instant.now().plus(twoDays, ChronoUnit.DAYS);
		return Jwts.builder().setIssuer(TOKEN_ISSUER).setSubject(username).setExpiration(Date.from(expiration))
				.signWith(SignatureAlgorithm.HS256, uuid).compact();
	}

	// public static boolean verifyToken(String token) {

	// }
}

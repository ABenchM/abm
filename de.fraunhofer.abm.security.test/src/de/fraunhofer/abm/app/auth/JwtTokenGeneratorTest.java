package de.fraunhofer.abm.app.auth;

import org.junit.Test;

public class JwtTokenGeneratorTest {
	@Test
	public void testCreateToken() {
		String uuid = "332326c4-0a78-4f98-a1c4-90026c77b934";
		String jwt = JwtTokenGenerator.createToken(uuid, "hariharan");
		System.out.println(jwt);
	}
}

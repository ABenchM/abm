package de.fraunhofer.abm.app.auth;

import org.junit.Assert;
import org.junit.Test;

public class PasswordTest {
	@Test
	public void testPasswordCheck() throws Exception {
		String key = "password";
		String saltedKey = Password.getSaltedHash(key);
		Assert.assertTrue("Password checking", Password.check(key, saltedKey));
	}
}

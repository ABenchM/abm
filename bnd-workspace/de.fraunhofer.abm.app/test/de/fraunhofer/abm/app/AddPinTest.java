package de.fraunhofer.abm.app;

import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.abm.app.auth.Password;
import junit.framework.TestCase;

public class AddPinTest extends TestCase {
	
	@Test
	public void testAddPin() throws Exception {
		String key = "password";
		String saltedKey = Password.getSaltedHash(key);
		Assert.assertTrue("Password checking", Password.check(key, saltedKey));
	}
	
}

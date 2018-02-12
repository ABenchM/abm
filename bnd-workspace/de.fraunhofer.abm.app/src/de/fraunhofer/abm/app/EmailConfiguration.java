package de.fraunhofer.abm.app;

import static de.fraunhofer.abm.util.AbmApplicationConstants.*;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.osgi.service.component.annotations.Component;

@Component
public class EmailConfiguration implements EmailConfigInterface {

	/*
	 * No authentication is needed to send these messages from within UPB, but we
	 * cannot use this address when sending messages from outside it.
	 */
	private String userAddress = userAddress();
	private String userName = userName();
	private String userPassword = userPassword();
	private String[] adminAddresses = adminAddress();
	private boolean userAuth = false;

	private String serverHost = serverHost();
	private String serverPort = serverPort();

	@Override
	public InternetAddress getFrom() throws Exception {
		return new InternetAddress(userAddress);
	}

	@Override
	public InternetAddress[] getTo() throws Exception {
		InternetAddress[] addressList = new InternetAddress[adminAddresses.length];
		for (int i = 0; i < adminAddresses.length; i++) {
			addressList[i] = new InternetAddress(adminAddresses[i]);
		}
		return addressList;
	}

	public Session getSession() {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.starttls.enable", "true");
		properties.setProperty("mail.smtp.host", serverHost);
		properties.setProperty("mail.smtp.port", serverPort);
		if (!userAuth) {
			return Session.getDefaultInstance(properties);
		}
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.user", userName);
		properties.setProperty("mail.smtp.password", userPassword);
		return Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userAddress, userPassword);
			};
		});
	}
}

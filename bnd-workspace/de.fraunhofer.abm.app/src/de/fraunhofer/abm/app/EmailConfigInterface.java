package de.fraunhofer.abm.app;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;

public interface EmailConfigInterface {
	
	public InternetAddress getFrom() throws Exception;
	public InternetAddress[] getTo() throws Exception;
	public Session getSession();
}

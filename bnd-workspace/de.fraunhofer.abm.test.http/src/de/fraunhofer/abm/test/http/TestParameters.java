package de.fraunhofer.abm.test.http;

import java.io.IOException;
import java.util.Map;

public interface TestParameters {

    public String BASE_URI = "http://localhost:8080";
    //public String BASE_URI = "http://abm.cs.upb.de";

    public String USER = "test1";
    public String PASSWORD = "test";
    public String NEWPASSWORD = "test1";
    public String QUERY = "java";
    
    public void login(final String user, final String password) throws IOException;
    public void addSessionCookie(final Map<String, String> headers);


    


}

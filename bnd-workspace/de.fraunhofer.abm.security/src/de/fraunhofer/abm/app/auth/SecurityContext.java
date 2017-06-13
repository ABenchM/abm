package de.fraunhofer.abm.app.auth;

public class SecurityContext {

    private static final SecurityContext instance = new SecurityContext();

    private static ThreadLocal<String> username = new ThreadLocal<>();

    private SecurityContext() {
    }

    public static SecurityContext getInstance() {
        return instance;
    }

    public String getUser() {
        return username.get();
    }

    public void setUser(String user) {
        username.set(user);
    }
}

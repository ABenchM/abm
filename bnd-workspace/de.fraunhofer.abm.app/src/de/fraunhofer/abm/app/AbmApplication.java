package de.fraunhofer.abm.app;

import static de.fraunhofer.abm.util.AbmApplicationConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.osgi.dto.DTO;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import de.fraunhofer.abm.app.auth.Authenticator;
import de.fraunhofer.abm.app.auth.Authorizer;
import de.fraunhofer.abm.app.auth.SecurityContext;
import de.fraunhofer.abm.crawler.api.Crawler;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.http.client.HttpUtils;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;
@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js", "angular-cookies.js"}, priority=1000)
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="de.fraunhofer.abm.rest")
public class AbmApplication implements REST {


    @Reference(cardinality=ReferenceCardinality.AT_LEAST_ONE,bind="addCrawler",unbind="removeCrawler",policy=ReferencePolicy.DYNAMIC)
    private volatile List<Crawler> crawlerList = new ArrayList<>();

    @Reference
    private Authenticator authenticator;

    @Reference
    private UserAdmin userAdmin;

    @Reference
    private Authorizer authorizer;
    
    int i = 0;
    

    public List<RepositoryDTO> getSearch(RESTRequest request, String query) throws Exception {
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("RegisteredUser");
    	users.add("UserAdmin"); 
        authorizer.requireRoles(users);
        List<RepositoryDTO> result =new ArrayList<>();
        for(Crawler c : crawlerList) {
        	System.out.println(c.getClass().getSimpleName());
//        	if(c.getClass().getSimpleName().equals("GithubCrawler")) {
        		System.out.println("Using "+ c.getClass().getSimpleName() + " to search the query");
        		result.addAll(c.search(query));
//        	}
        	
         
        }
       // List<RepositoryDTO> result = crawler.search(query);
        //crawler = new GithubCrawler();
                   
        return result;
    }

    
    protected void addCrawler(Crawler crawler) {
    	System.out.println("calling "+ ++i ); 
    	crawlerList.add(crawler);
    }
    
    protected void removeCrawler(Crawler crawler) {
    	crawlerList.remove(crawler);
    }
    
    interface LoginRequest extends RESTRequest {
        Credentials _body();
    }

    public void postLogin(LoginRequest lr) throws Exception {
        Credentials credentials = lr._body();
        if(credentials.username.equals("google-oauth")) {
            doGoogleLogin(lr, credentials);
        } else {
        	boolean authenticated = authenticator.authenticate(credentials.username, credentials.password);
            if(authenticated) {
                lr._response().setStatus(HttpServletResponse.SC_OK);
                lr._request().getSession().setAttribute("user", credentials.username);
                SecurityContext.getInstance().setUser(credentials.username);
            } else {
                lr._response().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doGoogleLogin(LoginRequest lr, Credentials credentials) throws IOException {
        String idToken = credentials.password;
        String json = HttpUtils.get("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idToken, null, "UTF-8");
        JSONObject response = new JSONObject(json);
        if(response.has("aud") && response.get("aud").equals(googleClientId())) {
            String uniqueGoogleUserId = response.getString("sub");
            Role user = userAdmin.getRole(uniqueGoogleUserId);
            if(user == null) {
                User googleUser = (User) userAdmin.createRole(uniqueGoogleUserId, Role.USER);
                googleUser.getProperties().put("login_provider", "google");
                Group registeredUsers = (Group) userAdmin.getRole("RegisteredUser");
                registeredUsers.addMember(googleUser);

                if(response.has("email")) {
                    googleUser.getProperties().put("email", response.get("email"));
                }
                if(response.has("given_name")) {
                    googleUser.getProperties().put("firstname", response.get("given_name"));
                }
                if(response.has("family_name")) {
                    googleUser.getProperties().put("lastname", response.get("family_name"));
                }
                if(response.has("locale")) {
                    googleUser.getProperties().put("locale", response.get("locale"));
                }
            }
            lr._response().setStatus(HttpServletResponse.SC_OK);
            lr._request().getSession().setAttribute("user", uniqueGoogleUserId);
            SecurityContext.getInstance().setUser(uniqueGoogleUserId);
        } else {
            throw new SecurityException("Wrong client ID");
        }
    }

    public void getLogout(RESTRequest rr) throws Exception {
        rr._request().getSession().invalidate();
    }

    public static class Credentials extends DTO {
        public String username;
        public String password;
    }
}

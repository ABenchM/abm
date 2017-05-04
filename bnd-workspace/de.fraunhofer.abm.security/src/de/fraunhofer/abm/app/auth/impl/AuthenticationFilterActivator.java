package de.fraunhofer.abm.app.auth.impl;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Filter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import de.fraunhofer.abm.app.auth.AuthenticationFilter;
import de.fraunhofer.abm.app.auth.RequestDelayFilter;

@Component(immediate=true,service=Object.class)
public class AuthenticationFilterActivator {

    @Activate
    public void activate() {
        // create and register the filter with the whiteboard pattern
        AuthenticationFilter filter = new AuthenticationFilter();
        Dictionary<String, Object> filterProps = new Hashtable<>();
        filterProps.put(HTTP_WHITEBOARD_FILTER_PATTERN, new String[] {"/rest/*", "/ws/*"});
        BundleContext ctx = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        ctx.registerService(Filter.class, filter, filterProps);

        RequestDelayFilter rdfilter = new RequestDelayFilter();
        filterProps = new Hashtable<>();
        filterProps.put(HTTP_WHITEBOARD_FILTER_PATTERN, "/rest/*");
        ctx.registerService(Filter.class, rdfilter, filterProps);
    }
}

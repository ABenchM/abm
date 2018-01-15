package de.fraunhofer.abm.app.websocket;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@Component
public class HermesStatusServlet extends WebSocketServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Reference
	private volatile HttpService m_httpService;
	
	 @Activate
	    public void start()  {
	        try {
	            //Store the current CCL
	            ClassLoader ccl = Thread.currentThread().getContextClassLoader();

	            //We have to set the CCL to Jetty's bundle classloader
	            BundleWiring bundleWiring = findJettyBundle().adapt(BundleWiring.class);
	            ClassLoader classLoader = bundleWiring.getClassLoader();
	            Thread.currentThread().setContextClassLoader(classLoader);

	            m_httpService.registerServlet("/ws/hermes", this, null, null);

	            //Restore the CCL
	            Thread.currentThread().setContextClassLoader(ccl);

	        } catch (ServletException | NamespaceException e) {
	            e.printStackTrace();
	        }
	    }

	    private Bundle findJettyBundle() {
	        BundleContext ctx = FrameworkUtil.getBundle(HermesStatusServlet.class).getBundleContext();
	        return Arrays.stream(ctx.getBundles()).filter(b -> b.getSymbolicName().equals("org.apache.felix.http.jetty")).findAny().get();
	    }

	
	@Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(TimeUnit.MINUTES.toMillis(10));
        factory.register(HermesStatusSocket.class);
    }
	

}

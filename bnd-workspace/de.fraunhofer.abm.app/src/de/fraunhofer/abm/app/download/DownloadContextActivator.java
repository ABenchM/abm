package de.fraunhofer.abm.app.download;

import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import de.fraunhofer.abm.app.websocket.BuildStatusServlet;
import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.collection.dao.HermesResultDao;

@Component
public class DownloadContextActivator {
    @Reference
    private volatile HttpService httpService;

    @Reference
    private BuildResultDao buildResultDao;
    
    @Reference
    private HermesResultDao hermesResultDao;

    @Activate
    public void start()  {
        try {
            //Store the current CCL
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();

            //We have to set the CCL to Jetty's bundle classloader
            BundleWiring bundleWiring = findJettyBundle().adapt(BundleWiring.class);
            ClassLoader classLoader = bundleWiring.getClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);

            //httpService.registerServlet("/download", this, null, null);
            httpService.registerResources("/download", "", new FileHttpContext(buildResultDao,"/download"));
            httpService.registerResources("/downloadHermes", "", new HermesHttpContext(hermesResultDao,"/downloadHermes"));

            //Restore the CCL
            Thread.currentThread().setContextClassLoader(ccl);

        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    private Bundle findJettyBundle() {
        BundleContext ctx = FrameworkUtil.getBundle(BuildStatusServlet.class).getBundleContext();
        return Arrays.stream(ctx.getBundles()).filter(b -> b.getSymbolicName().equals("org.apache.felix.http.jetty")).findAny().get();
    }
}

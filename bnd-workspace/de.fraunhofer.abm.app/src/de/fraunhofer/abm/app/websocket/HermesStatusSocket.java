package de.fraunhofer.abm.app.websocket;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket
public class HermesStatusSocket  {
	
	private static final transient Logger logger = LoggerFactory.getLogger(HermesStatusSocket.class);
	
	//private Session session;
	private RemoteEndpoint remote;
	//private String hermesId;
	
	
	 public RemoteEndpoint getRemote() {
	        return remote;
	    }
	
	 @OnWebSocketConnect
	    public void onConnect(Session session) {
	       // this.session = session;
	        this.remote = session.getRemote();
	    }
	 
	   @OnWebSocketClose
	    public void onClose(int statusCode, String reason) {
	        //this.session = null;
	        logger.debug("WebSocket closed. Removing progress listener and closing service tracker");
	       // SuiteBuilder builder = suiteBuilderTracker.getService();
	       // BuildProcess process = builder.getBuildProcess(buildId);
	      //  process.removeBuildProgressListener(this);
	      //  suiteBuilderTracker.close();
	      //  dtoServiceTracker.close();
	    }

}

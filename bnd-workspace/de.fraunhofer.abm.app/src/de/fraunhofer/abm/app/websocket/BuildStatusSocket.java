package de.fraunhofer.abm.app.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildProgressListener;
import de.fraunhofer.abm.builder.api.BuildStep;
import de.fraunhofer.abm.domain.BuildStepDTO;
import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.suitebuilder.BuildProcess;
import de.fraunhofer.abm.suitebuilder.SuiteBuilder;
import osgi.enroute.dto.api.DTOs;

@WebSocket
public class BuildStatusSocket implements BuildProgressListener {
    private static final transient Logger logger = LoggerFactory.getLogger(BuildStatusSocket.class);

    private Session session;
    private RemoteEndpoint remote;
    private ServiceTracker<SuiteBuilder, SuiteBuilder> suiteBuilderTracker;
    private ServiceTracker<DTOs, DTOs> dtoServiceTracker;
    private String buildId;

    public BuildStatusSocket() {
        BundleContext ctx = FrameworkUtil.getBundle(BuildStatusSocket.class).getBundleContext();
        suiteBuilderTracker = new ServiceTracker<>(ctx, SuiteBuilder.class, null);
        suiteBuilderTracker.open();
        dtoServiceTracker = new ServiceTracker<>(ctx, DTOs.class, null);
        dtoServiceTracker.open();
    }

    public RemoteEndpoint getRemote() {
        return remote;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        this.session = null;
        logger.debug("WebSocket closed. Removing progress listener and closing service tracker");
        SuiteBuilder builder = suiteBuilderTracker.getService();
        BuildProcess process = builder.getBuildProcess(buildId);
        process.removeBuildProgressListener(this);
        suiteBuilderTracker.close();
        dtoServiceTracker.close();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        this.remote = session.getRemote();
    }

    @OnWebSocketMessage
    public void onText(String message) {
        if (session == null) {
            // no connection, do nothing.
            // this is possible due to async behavior
            // TODO can we sleep for a while and try again or something like that?!?
            logger.warn("Session not yet available");
            return;
        }

        try {
            DTOs dtoService = dtoServiceTracker.getService();
            Request req = dtoService.decoder(Request.class).get(message);
            if("cancel".equals(req.msg)) {
                stopBuild(req.id);
            } else if("listen".equals(req.msg)) {
                registerListener(dtoService, req.id);
            }
        } catch (Exception e) {
            // TODO return something to the user and add log
            e.printStackTrace();
        }
    }

    private void registerListener(DTOs dtoService, String buildId) throws Exception {
        SuiteBuilder builder = suiteBuilderTracker.getService();
        if(builder != null) {
            logger.debug("Looking for build {}", buildId);
            BuildProcess process = builder.getBuildProcess(buildId);
            if(process != null) {
                process.addBuildProgressListener(this);
                String json = "{\"msg\": \"registered\"}";
                remote.sendString(json);
            } else {
                remote.sendString("{\"msg\": \"error\", \"text\": \"There is no running build with ID "+buildId+"\"}");
            }
        } else {
            remote.sendString("no SuiteBuilder available");
        }
    }

    private void stopBuild(String buildId) throws IOException {
        logger.debug("Trying to stop build {}", buildId);
        SuiteBuilder builder = suiteBuilderTracker.getService();
        if(builder != null) {
            logger.debug("Looking for build {}", buildId);
            BuildProcess process = builder.getBuildProcess(buildId);
            if(process != null) {
                String repoId = process.cancel();
                if(repoId != null) {
                    remote.sendString("{\"msg\": \"build_cancelled\", \"id\": \"" + buildId + "\", \"repository\": \"" + repoId + "\"}");
                } else {
                    remote.sendString("{\"msg\": \"error\", \"text\": \"Build with ID "+buildId+" could not be stopped\"}");
                }
            } else {
                remote.sendString("{\"msg\": \"error\", \"text\": \"There is no running build with ID "+buildId+"\"}");
            }
        } else {
            remote.sendString("{\"msg\": \"error\", \"text\": \"Build system is not available\"}");
        }
    }

    @Override
    public void buildInitialized(RepositoryDTO repository, List<BuildStep<?>> steps) {
        if(this.session == null) {
            return;
        }

        try {
            List<BuildStepDTO> buildStepDTOs = new ArrayList<>();
            for (int i = 0; i < steps.size(); i++) {
                buildStepDTOs.add(steps.get(i).toDTO(i));
            }
            DTOs dtoService = dtoServiceTracker.getService();
            String json = dtoService.encoder(buildStepDTOs).put();
            json = "{\"msg\": \"buildsteps\", \"steps\": " + json + ", \"repository\": \""+repository.id+"\"}";
            remote.sendString(json);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void buildStepChanged(BuildStep<?> step) {
        if(this.session == null) {
            return;
        }

        try {
            BuildStepDTO buildStepDTO = step.toDTO(0);
            DTOs dtoService = dtoServiceTracker.getService();
            String json = dtoService.encoder(buildStepDTO).put();
            json = "{\"msg\": \"step_changed\", \"step\": " + json + "}";
            remote.sendString(json);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void buildFinished(RepositoryDTO repository) {
        if(this.session == null) {
            return;
        }

        try {
            String json = "{\"msg\": \"build_finished\", \"repository\": \""+repository.id+"\"}";
            remote.sendString(json);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void buildProcessComplete() {
        sendUpdate("build_process_finished");
    }

    private void sendUpdate(String data) {
        if(this.session == null) {
            return;
        }

        try {
            String json = "{\"msg\": \"update\", \"data\": \""+data+"\"}";
            remote.sendString(json);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static class Request {
        public Request() {}
        public String msg;
        public String id;
    }
}
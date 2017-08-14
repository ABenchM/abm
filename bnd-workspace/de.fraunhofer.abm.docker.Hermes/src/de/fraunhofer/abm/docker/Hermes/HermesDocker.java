package de.fraunhofer.abm.docker.Hermes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.AbstractDockerStep.Result;
import de.fraunhofer.abm.builder.docker.base.StreamRedirectThread;


public class HermesDocker {
	
	private static final transient Logger logger = LoggerFactory.getLogger(HermesDocker.class);
	
	private static final String NOT_SET = "NOT_SET";
	
	public static enum STATUS {
        WAITING,
        IN_PROGRESS,
        FAILED,
        SUCCESS,
        CANCELLED
    }
	
	private enum STATE {
        CONTINUE,
        CLEAN_UP
    }
	
    private ExecutorService executor;
	protected String output = "";
	protected String errorOutput = "";
	protected STATUS status = STATUS.WAITING; 
	private File confDir;
	protected String imageName;
	protected String containerName;
	protected Throwable throwable;
			
	 protected void setStatus(STATUS status) {
	        this.status = status;
	        
	    }
	 
	 public void setImageName(String imageName) {
	        this.imageName = imageName;
	    }
	 
	 public String getImageName()
	 {
		 return imageName;
	 }

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public STATUS getStatus() {
		return status;
	}

	public File getConfDir() {
		return confDir;
	}

	public void setConfDir(File confDir) {
		this.confDir = confDir;
	}
	 
	 protected Result execHermes(String cmd, File dir) throws IOException, InterruptedException {
	        logger.debug("Executing command [{}] in directory {}", cmd, dir.getAbsolutePath());
	        String[] env = 	environmentToArray();;
	        	        
	        Process p = Runtime.getRuntime().exec(cmd, env, dir);
	        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
	        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
	        executor.submit(new StreamRedirectThread(p.getInputStream(), stdout));
	        executor.submit(new StreamRedirectThread(p.getErrorStream(), stderr));
	        Result result = new Result();
	        result.exitValue = p.waitFor();
	        result.stdout = BuildUtils.toString(stdout);
	        result.stderr = BuildUtils.toString(stderr);
	        return result;
	    }	
	 
	 protected void setThrowable(Throwable t) {
	        this.throwable = t;
	        setStatus(STATUS.FAILED);
	    }

	 public Throwable getThrowable() {
	        return throwable;
	    }
	 
	 
	 public static String[] environmentToArray() {
	        Map<String,String> env = System.getenv();
	        String[] envArray = new String[env.size()];
	        int index = 0;
	        for (Entry<String,String> entry : env.entrySet()) {
	            String arrayEntry = entry.getKey() + '=' + entry.getValue();
	            envArray[index++] = arrayEntry;
	        }
	        return envArray;
	    }
	 
}

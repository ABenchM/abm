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

import de.fraunhofer.abm.builder.docker.base.StreamRedirectThread;
import de.fraunhofer.abm.builder.docker.base.AbstractDockerStep.Result;


public class RunHermes{
	
	private String containerName = "hermes";
	private File confDir;
	private ExecutorService executor;
	protected String errorOutput = "";
	protected String output = "";
	protected Throwable throwable;
	private String step;
	
	
	
	public static enum STATUS {
        WAITING,
        IN_PROGRESS,
        FAILED,
        SUCCESS,
        CANCELLED
    }
	
	private STATUS status = STATUS.WAITING;
	
	protected void setStatus(STATUS status) {
        this.status = status;
        
    }
	
	

	public STATUS getStatus() {
		return status;
	}



	public RunHermes(String step) {
		super();
		this.step = step;
	}



	public String getContainerName() {
		return containerName;
	}


    
	public String getStep() {
		return step;
	}



	public File getConfDir() {
		return confDir;
	}



	public void setConfDir(File confDir) {
		this.confDir = confDir;
	}
	
	protected void setThrowable(Throwable t) {
        this.throwable = t;
        setStatus(STATUS.FAILED);
    }
	
	public Throwable getThrowable() {
        return throwable;
    }


	private static final transient Logger logger = LoggerFactory.getLogger(RunHermes.class);
	
 	
	
	public void execute(String cmd) throws InterruptedException
	
	{
		setStatus(STATUS.IN_PROGRESS);
		 try {
	            logger.debug("Running docker build container:{}", getContainerName());

	            Result result = execHermes(cmd,getConfDir());
	            output = result.stdout;
	            errorOutput = result.stderr;
	            setStatus(result.exitValue == 0 ? STATUS.SUCCESS : STATUS.FAILED);
	        } catch(InterruptedException ie) {
	            logger.info("Interrupted process at", getStep());
	            setStatus(STATUS.CANCELLED);
	        } catch (Throwable t) {
	            logger.error("Couldn't run docker container:" + getContainerName(), t);
	            errorOutput = BuildUtils.createErrorString("Couldn't run docker container:" + getContainerName(), t);
	            setThrowable(t);
	        }
		
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

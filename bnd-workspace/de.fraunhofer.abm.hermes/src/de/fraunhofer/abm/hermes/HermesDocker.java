package de.fraunhofer.abm.hermes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.AbstractHermesStep;
import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.docker.base.StreamRedirectThread;



public abstract class HermesDocker<T> extends AbstractHermesStep<T>  {
	
	 private static final transient Logger logger = LoggerFactory.getLogger(HermesDocker.class);

	 private ExecutorService executor;
	 
	 public HermesDocker(File repoDir, ExecutorService executor)
	 {
		 super(repoDir);
		 this.executor = executor;
		 
	 }
	 
	 protected Result exec(String cmd, File dir) throws IOException, InterruptedException {
	        logger.debug("Executing command [{}] in directory {}", cmd, dir.getAbsolutePath());
	        String[] env = 	environmentToArray();
	        String[] _cmd = cmd.split(" ");
	        Process p = Runtime.getRuntime().exec(_cmd, env, dir);
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
	 
	 public static class Result {
	        public int exitValue;
	        public String stdout = "";
	        public String stderr = "";
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

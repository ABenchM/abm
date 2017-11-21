package de.fraunhofer.abm.builder.docker.base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.abm.builder.api.BuildUtils;
import de.fraunhofer.abm.builder.api.HermesStep;
import de.fraunhofer.abm.builder.api.HermesStepListener;



public abstract class AbstractHermesStep<T> implements HermesStep<T> {
		
	private static final transient Logger logger = LoggerFactory.getLogger(AbstractHermesStep.class);
				
		protected String id = UUID.randomUUID().toString();
		protected String name = "Unnamed step";
	    protected STATUS status = STATUS.WAITING;
	    protected String output = "";
	    protected String errorOutput = "";
	    protected Throwable throwable;
	    String repoDir;
	    private List<HermesStepListener> listeners = new ArrayList<>();
	    
	   // private ExecutorService executor;
	    
	    public AbstractHermesStep(String repoDir /*, ExecutorService executor*/ ){
	    	this.repoDir = repoDir;
	    	//this.executor = executor;
	    }
	    
	    @Override
	    public String getId() {
	        return id;
	    }

	    @Override
	    public String getName() {
	        return name;
	    }

	    @Override
	    public STATUS getStatus() {
	        return status;
	    }
	    
	    protected void setStatus(STATUS status) {
	        this.status = status;
	        fireStatusChanged();
	    }
	    
	    @Override
	    public String getOutput() {
	        return output;
	    }

	    @Override
	    public String getErrorOutput() {
	        return errorOutput;
	    }
	    
	    @Override
	    public Throwable getThrowable() {
	        return throwable;
	    }
	    
	    protected void setThrowable(Throwable t) {
	        this.throwable = t;
	        setStatus(STATUS.FAILED);
	    }

	    @Override
	    public void addHermesStepListener(HermesStepListener hsl) {
	        listeners.add(hsl);
	    }
	    
	    @Override
	    public void removeHermesStepListener(HermesStepListener hsl) {
	        listeners.remove(hsl);
	    }
	    
	    protected void fireStatusChanged() {
	        for (HermesStepListener hermesStepListener : listeners) {
	            hermesStepListener.statusChanged(this);
	        }
	    }
	    
	    protected Result exec(String cmd, File dir) throws IOException, InterruptedException {
	        logger.debug("Executing command [{}] in directory {}", cmd, dir.getAbsolutePath());
	        String[] env = 	environmentToArray();
	        String[] _cmd = cmd.split(" ");
	        Process p = Runtime.getRuntime().exec(_cmd, env, dir);
	        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
	        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
	       // executor.submit(new StreamRedirectThread(p.getInputStream(), stdout));
	       // executor.submit(new StreamRedirectThread(p.getErrorStream(), stderr));
	        Result result = new Result();
	        TimeUnit.SECONDS.sleep(3);
	        result.exitValue = p.waitFor();
	        result.stdout = BuildUtils.toString(stdout);
	        System.out.println(stdout);
	        result.stderr = BuildUtils.toString(stderr);
	        System.out.println(stderr);
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




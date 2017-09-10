package de.fraunhofer.abm.builder.api;

import de.fraunhofer.abm.domain.HermesStepDTO;

public interface HermesStep<T> {

	public static enum STATUS{
		WAITING,
        IN_PROGRESS,
        FAILED,
        SUCCESS,
        CANCELLED
	}
	
	    public String getId();
	    public String getName();
	    public STATUS getStatus();
	    public String getOutput();
	    public String getErrorOutput();
	    public Throwable getThrowable();
	    public T execute();
	  
	    
	    public void addHermesStepListener(HermesStepListener hsl);
	    public void removeHermesStepListener(HermesStepListener hsl);
	    
	    public HermesStepDTO toDTO(int index);
	
}

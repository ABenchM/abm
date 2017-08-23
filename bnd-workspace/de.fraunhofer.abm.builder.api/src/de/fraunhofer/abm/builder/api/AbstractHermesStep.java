package de.fraunhofer.abm.builder.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.fraunhofer.abm.builder.api.BuildStep.STATUS;



public abstract class AbstractHermesStep<T> implements HermesStep<T> {
	
	protected String id = UUID.randomUUID().toString();
	protected String name = "Unnamed step";
    protected STATUS status = STATUS.WAITING;
    protected String output = "";
    protected String errorOutput = "";
    protected Throwable throwable;
    
    private List<HermesStepListener> listeners = new ArrayList<>();
    
    public AbstractHermesStep(){
    	
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

}

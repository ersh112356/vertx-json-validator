/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vertx.core.schema.utils;

/**
 *
 * @author Eran
 */
public class Result{
 
    /** Holds the outcome result. */
    private boolean result = true;
    /** Holds the cause of the result. */
    private String cause = null;
    
    /**
     * The constructor.
     * 
     * @param result- the outcome result.
     * @param cause- the cause of the result.
     * 
     */
    public Result(boolean result, String cause){
        
        this.result = result;
        this.cause = cause;
    }
    
    /**
     * Return the result.
     * 
     * @return the result.
     */
    public boolean result(){
        
        return result;
    }
    
    /**
     * Return the cause.
     * 
     * @return the cause.
     */
    public String cause(){
        
        return cause;
    }
    
    /**
     * A human representation of this object.
     * 
     * @return human representation of this object.
     */
    @Override
    public String toString(){
        
        return result+", "+cause;
    }
}
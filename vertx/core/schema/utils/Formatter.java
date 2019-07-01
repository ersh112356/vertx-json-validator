/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vertx.core.schema.utils;

import io.vertx.core.json.JsonObject;
import java.util.Map;

/**
 *
 * @author Eran
 */
public class Formatter{
 
    /**
     * The constructor.
     * All private.
     */
    private Formatter(){
    }
    
    /**
     * Try to format a given entry.
     * 
     * @param entry- the entry to format.
     * 
     * @return a prettify string representation of the entry.
     */
    public static String format(Object entry){
        
        if(entry instanceof Map)
        {
            return new JsonObject((Map)entry).encode();
        }
        
        return entry.toString();
    }
}
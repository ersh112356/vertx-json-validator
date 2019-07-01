package vertx.core.schema.resolver;

import vertx.core.schema.utils.Result;

/**
 * 
 * @author eransha
 */
public final class BooleanValidator{

    /**
     * 
     * @param instance
     * @param schema
     * @return 
     */
    public static Result isValid(Object instance, JsonSchemaResolver.Schema schema){
        
        if(!isBoolean(instance))
        {
            return new Result(false,"not a boolean");
        }

        if(instance==null)
        {   // Apply default value.
            instance = schema.get("default");
        }

        return new Result(true,null);
    }

    /**
     * 
     * @param value
     * @return 
     */
    private static boolean isBoolean(Object value){
        
        return value==null || value instanceof Boolean;
    }
}

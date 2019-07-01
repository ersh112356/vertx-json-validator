package vertx.core.schema.resolver;

import vertx.core.schema.utils.Result;

/**
 * 
 */
public final class IntegerValidator{
    
    /**
     * 
     * @param instance
     * @param schema
     * @return 
     */
    public static Result isValid(Object instance, JsonSchemaResolver.Schema schema){
        
        if(!isInteger(instance))
        {
            return new Result(false,"not an integer");
        }

        if(instance == null)
        {   // Apply default value.
            instance = schema.get("default");
        }

        final Integer number = (Integer) instance;

        if(number != null)
        {   // Validate divisibleBy.
            final Integer divisibleBy = schema.get("divisibleBy");

            if(divisibleBy != null && number % divisibleBy != 0)
            {
                return new Result(false,"bad divisibleBy");
            }

            // Validate minimum.
            final Integer minimum = schema.get("minimum");

            if(minimum != null)
            {
                if(Boolean.TRUE.equals(schema.get("exclusiveMinimum")) ? (number <= minimum) : (number < minimum))
                {
                    return new Result(false,"out of range");
                }
            }

            // Validate maximum.
            final Integer maximum = schema.get("maximum");

            if(maximum != null)
            {
                if(Boolean.TRUE.equals(schema.get("exclusiveMaximum")) ? (maximum <= number) : (maximum < number))
                {
                    return new Result(false,"out of range");
                }
            }
        }

        return new Result(true,null);
    }

    /**
     * 
     * @param value
     * @return 
     */
    private static boolean isInteger(Object value){
        
        return value==null || value instanceof Integer;
    }
}
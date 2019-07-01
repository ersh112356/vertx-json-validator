package vertx.core.schema.resolver;

import vertx.core.schema.utils.Result;

/**
 * 
 * @author eransha
 */
public final class NumberValidator{

    /**
     * 
     * @param instance
     * @param schema
     * @return 
     */
    public static Result isValid(Object instance, JsonSchemaResolver.Schema schema){
        
        if(!isNumber(instance))
        {
            return new Result(false,"not a number");
        }

        if(instance == null)
        {   // Apply default value.
            instance = schema.get("default");
        }

        final Number number = (Number) instance;

        if(number != null)
        {   // Validate divisibleBy.
            final Number divisibleBy = schema.get("divisibleBy");

            if(divisibleBy != null && number.doubleValue() % divisibleBy.doubleValue() != 0)
            {
                return new Result(false,"bad divisibleBy");
            }

            // Validate minimum.
            final Number minimum = schema.get("minimum");

            if(minimum != null)
            {
                if(Boolean.TRUE.equals(schema.get("exclusiveMinimum")) ? (number.doubleValue() <= minimum.doubleValue()) : (number.doubleValue() < minimum.doubleValue()))
                {
                    return new Result(false,"out of range");
                }
            }

            // Validate maximum.
            final Number maximum = schema.get("maximum");

            if(maximum != null)
            {
                if (Boolean.TRUE.equals(schema.get("exclusiveMaximum")) ? (maximum.doubleValue() <= number.doubleValue()) : (maximum.doubleValue() < number.doubleValue()))
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
    private static boolean isNumber(Object value){
        
        return value==null || value instanceof Number;
    }
}

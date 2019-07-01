package vertx.core.schema.resolver;

import vertx.core.schema.utils.Result;
import javafx.util.Pair;

/**
 * JsonSchema validator according to draft-v4
 */
public final class JsonSchema{

    /**
     * 
     * @param instance
     * @param schemaRef
     * @return 
     */
    public static boolean conformsSchema(Object instance, String schemaRef){
        
        return schemaRef == null || conformsSchema(instance,resolve(schemaRef)).result();
    }

    public static Result conformsSchema(Object instance,JsonSchemaResolver.Schema schema){

        if(schema==null)
        {
            return new Result(true,null);
        }

        if(schema.containsKey("$ref"))
        {
            return conformsSchema(instance, JsonSchemaResolver.resolveSchema((String) schema.get("$ref"), schema.getParent()));
        }

        final String type = schema.get("type");
        Result result;
        
        if(!(result=AnyValidator.isValid(instance,schema)).result())
        {
            return result;
        }

        if(type!=null)
        {
            switch(type)
            {
                case "null":
                {
                    boolean b = isNull(instance);
                    
                    return new Result(b,b?null : "not null");
                }
                case "array":
                    return ArrayValidator.isValid(instance, schema);
                case "string":
                    return StringValidator.isValid(instance, schema);
                case "number":
                    return NumberValidator.isValid(instance, schema);
                case "integer":
                    return IntegerValidator.isValid(instance, schema);
                case "boolean":
                    return BooleanValidator.isValid(instance, schema);
                case "object":
                    return ObjectValidator.isValid(instance, schema);
                default:
                    throw new RuntimeException("Unsupported type: " + type);
            }
        }

        return new Result(true,null);
    }

    /**
     * 
     * @param value
     * @return 
     */
    private static boolean isNull(Object value){
        
        return value==null;
    }

    /**
     * 
     * @param id
     * @return 
     */
    private static JsonSchemaResolver.Schema resolve(String id){
        
        return JsonSchemaResolver.resolveSchema(id);
    }
}

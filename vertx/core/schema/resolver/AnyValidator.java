package vertx.core.schema.resolver;

import vertx.core.schema.utils.Result;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author eransha
 */
public final class AnyValidator{

    /**
     * 
     * @param instance
     * @param schema
     * @return 
     */
    public static Result isValid(Object instance, JsonSchemaResolver.Schema schema){
        
        if(instance==null)
        {   // Validate required.
            
            if(Boolean.TRUE.equals(schema.get("required")))
            {
                return new Result(false,"required is missing");
            }
        }

        if(instance==null)
        {   // Apply default value.
            instance = schema.get("default");
        }

        if(instance!=null)
        {   // Validate enum.
            List<Object> _enum = schema.get("enum");
            
            if(_enum!=null && !_enum.contains(instance))
            {
                return new Result(false,"bad enum");
            }

            // TODO: type

            // Validate allOf.
            List<Object> allOf = schema.get("allOf");
            
            if(allOf!=null)
            {
                for(int i=0;i<allOf.size();i++)
                {
                    Object item = allOf.get(i);

                    if(item instanceof Map)
                    {   // Convert to schema.
                        item = JsonSchemaResolver.resolveSchema((Map<String,Object>) item);
                        allOf.set(i,item);
                    }

                    Result result;
                    
                    if(!(result=JsonSchema.conformsSchema(instance,(JsonSchemaResolver.Schema)item)).result())
                    {
                        return new Result(false,((JsonSchemaResolver.Schema)item).getId()+" found a problem "+result.cause());
                    }
                }
            }

            // Validate anyOf.
            List<Object> anyOf = schema.get("anyOf");
            
            if(anyOf != null)
            {
                boolean match = false;
                
                for(int i=0;i<anyOf.size();i++)
                {
                    Object item = anyOf.get(i);

                    if(item instanceof Map)
                    {   // Convert to schema.
                        item = JsonSchemaResolver.resolveSchema((Map<String, Object>) item);
                        anyOf.set(i, item);
                    }

                    if(JsonSchema.conformsSchema(instance, (JsonSchemaResolver.Schema) item).result())
                    {
                        match = true;
                        
                        break;
                    }
                }
                
                if(!match)
                {   // Needs to be taken from above.
                    return new Result(false,"");
                }
            }

            // Validate oneOf.
            List<Object> oneOf = schema.get("oneOf");
            
            if(oneOf != null)
            {
                int matches = 0;
                
                for(int i=0;i<oneOf.size();i++)
                {
                    Object item = oneOf.get(i);

                    if(item instanceof Map)
                    {   // Convert to schema.
                        item = JsonSchemaResolver.resolveSchema((Map<String, Object>) item, schema.getParent());
                        oneOf.set(i, item);
                    }
                    
                    if(JsonSchema.conformsSchema(instance, (JsonSchemaResolver.Schema) item).result())
                    {
                        matches++;
                    }
                }
                
                if(matches == 0)
                {
                    return new Result(false,"oneOf is bad");
                }
            }

            // Validate not.
            Object not = schema.get("not");
            
            if(not!=null)
            {
                if (not instanceof Map)
                {   // Convert to schema.
                    not = JsonSchemaResolver.resolveSchema((Map<String, Object>) not, schema.getParent());
                    schema.put("not", not);
                }

                Result result;
                
                if((result=JsonSchema.conformsSchema(instance, (JsonSchemaResolver.Schema) not)).result())
                {   // TODO
                    return new Result(false,result.cause());
                }
            }

            // TODO: definitions
        }

        return new Result(true,null);
    }
}
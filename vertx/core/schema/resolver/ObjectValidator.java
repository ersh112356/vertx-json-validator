package vertx.core.schema.resolver;

import vertx.core.schema.utils.Result;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import vertx.core.schema.utils.Formatter;

/**
 * 
 * @author eransha
 */
public final class ObjectValidator{
    
    /**
     * TRy to validate an object.
     * 
     * @param instance
     * @param schema
     * 
     * @return 
     */
    public static Result isValid(Object instance, JsonSchemaResolver.Schema schema){
        
        if(!isObject(instance))
        {
            return new Result(false,"not an object");
        }

        if(instance==null)
        {   // Apply a default value.
            instance = schema.get("default");
        }

        if(instance instanceof JsonObject)
        {   // From now on work with maps.
            instance = ((JsonObject)instance).getMap();
        }

        final Map object = (Map)instance;

        // Required takes precedence if instance is null.
        final List<String> required = schema.get("required");

        if(object==null && required!=null && required.size()>0)
        {
            return new Result(false,"got no required");
        }

        if(object!=null)
        {   // Validate maxProperties.
            Integer maxProperties = schema.get("maxProperties");

            if(maxProperties!=null)
            {
                if(object.keySet().size()>maxProperties)
                {
                    return new Result(false,"got too much properties");
                }
            }

            // Validate minProperties.
            Integer minProperties = schema.get("minProperties");

            if(minProperties!=null)
            {
                if(object.keySet().size()<minProperties)
                {
                    return new Result(false,"got not sufficient properties");
                }
            }

            if(required!=null)
            {   // Validate required.
                
                for(String field : required)
                {
                    if(!object.containsKey(field))
                    {
                        return new Result(false,field+" is missing");
                    }
                }
            }

            // TODO: validate additionalProperties

            // Validate dependencies.
            Map<String, Object> dependencies = schema.get("dependencies");

            if(dependencies!=null)
            {
                for(Map.Entry<String, Object> entry : dependencies.entrySet())
                {
                    if(object.containsKey(entry.getKey()))
                    {
                        if(entry.getValue() instanceof List)
                        {
                            List<String> propertyDependencies = (List<String>) entry.getValue();
                            
                            for(String propertyDependency : propertyDependencies)
                            {
                                if(!object.containsKey(propertyDependency))
                                {
                                    return new Result(false,propertyDependency+" is missing ");
                                }
                            }
                        }

                        if(entry.getValue() instanceof Map)
                        {
                            JsonSchemaResolver.Schema schemaDependency = JsonSchemaResolver.resolveSchema((Map<String,Object>)entry.getValue(),schema.getParent());
                            
                            Result result;
                            
                            if(!(result=JsonSchema.conformsSchema(object.get(entry.getKey()),schemaDependency)).result())
                            {
                                return new Result(false,Formatter.format(entry.getValue())+" found a problem "+result.cause());
                            }
                        }
                    }
                }
            }

            // Validate properties.
            final Map<String,Object> properties = schema.get("properties");

            if(properties!=null)
            {
                for(Map.Entry<String,Object> entry : properties.entrySet())
                {
                    String name = entry.getKey();
                    Object property = entry.getValue();
                    JsonSchemaResolver.Schema propertySchema = null;

                    if(property instanceof JsonSchemaResolver.Schema)
                    {
                        propertySchema = (JsonSchemaResolver.Schema) property;
                    }
                    else
                    {
                        if(property instanceof Map)
                        {   // Convert to schema.
                            propertySchema = JsonSchemaResolver.resolveSchema((Map<String,Object>)property, schema);
                            entry.setValue(propertySchema);
                        }
                    }

                    Object item = object.get(name);
                    //setParentIfNotNull(propertySchema, schema);

                    Result result;
                    
                    if(!(result=JsonSchema.conformsSchema(item,propertySchema)).result())
                    {
                        return new Result(false,name+": found a problem "+result.cause());
                    }
                }
            }

            // Validate patternProperties.
            final Map<String, Object> patternProperties = schema.get("patternProperties");

            if(patternProperties != null)
            {
                for(Map.Entry<String, Object> entry : patternProperties.entrySet())
                {
                    String name = entry.getKey();
                    Pattern pattern = Pattern.compile(name);
                    Object property = entry.getValue();
                    JsonSchemaResolver.Schema propertySchema = null;

                    if(property instanceof JsonSchemaResolver.Schema)
                    {
                        propertySchema = (JsonSchemaResolver.Schema) property;
                    }
                    else
                    {
                        if(property instanceof Map)
                        {   // Convert to schema.
                            propertySchema = JsonSchemaResolver.resolveSchema((Map<String, Object>) property);
                            entry.setValue(propertySchema);
                        }
                    }

                    for(Object key : object.keySet())
                    {
                        if(pattern.matcher((String) key).matches())
                        {
                            Object item = object.get(key);
                            //setParentIfNotNull(propertySchema, schema);

                            Result result;
                            
                            if(!(result=JsonSchema.conformsSchema(item,propertySchema)).result())
                            {
                                return new Result(false,Formatter.format(item)+" found a problem "+result.cause());
                            }
                        }
                    }
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
    private static boolean isObject(Object value){
        
        return value==null || value instanceof Map || value instanceof JsonObject;
    }
}

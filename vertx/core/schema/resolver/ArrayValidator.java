package vertx.core.schema.resolver;

import vertx.core.schema.utils.Result;
import io.vertx.core.json.JsonArray;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vertx.core.schema.utils.Formatter;

/**
 * 
 * @author eransha
 */
public final class ArrayValidator{

    /**
     * 
     * @param instance
     * @param schema
     * @return 
     */
    public static Result isValid(Object instance, JsonSchemaResolver.Schema schema){
        
        if(!isArray(instance))
        {
            return new Result(false,"not an array");
        }

        if(instance==null)
        {   // Apply default value.
            instance = schema.get("default");
        }

        if(instance instanceof JsonArray)
        {   // From now on work with lists.
            instance = ((JsonArray)instance).getList();
        }

        final List array = (List)instance;

        if(array!=null)
        {   // Validate additionalItems.
            Boolean additionalItems = schema.get("additionalItems");

            if(additionalItems!=null && !additionalItems)
            {
                List<Object> items = schema.get("items");
                
                if(array.size()>items.size())
                {
                    return new Result(false,"not sufficient items");
                }
            }

            // Validate maxItems.
            Integer maxItems = schema.get("maxItems");

            if(maxItems!=null)
            {
                if(array.size()>maxItems)
                {
                    return new Result(false,"too much maxItems");
                }
            }

            // Validate minItems.
            Integer minItems = schema.get("minItems");

            if(minItems!=null)
            {
                if(array.size()<minItems)
                {
                    return new Result(false,"not sufficient minItems");
                }
            }

            // Validate uniqueItems.
            Boolean uniqueItems = schema.get("uniqueItems");

            if(uniqueItems!=null && uniqueItems)
            {
                Set<Object> set = new HashSet<>();

                for(Object o : array)
                {
                    if(!set.add(o))
                    {
                        return new Result(false,"got duplications in uniqueItems");
                    }
                }

                set.clear();
            }

            Object items = schema.get("items");
            JsonSchemaResolver.Schema itemsSchema = null;

            if(items instanceof JsonSchemaResolver.Schema)
            {
                itemsSchema = (JsonSchemaResolver.Schema)items;
            }
            else
            {
                if(items instanceof Map)
                {   // Convert to schema.
                    itemsSchema = JsonSchemaResolver.resolveSchema((Map<String, Object>) items, schema.getParent());
                    schema.put("items", itemsSchema);
                }
            }

            //setParentIfNotNull(itemsSchema, schema);

            for(Object item : array)
            {
                Result result;
                
                if(!(result=JsonSchema.conformsSchema(item,itemsSchema)).result())
                {
                    return new Result(false,Formatter.format(item)+" found a problem "+result.cause());
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
    private static boolean isArray(Object value){
        
        return value == null || value instanceof List || value instanceof JsonArray;
    }

    /**
     * 
     * @param schema
     * @param parent 
     *//*
    private static void setParentIfNotNull(JsonSchemaResolver.Schema schema, JsonSchemaResolver.Schema parent){
        
        if(schema!=null)
        {
            schema.setParent(parent);
        }
    }*/
}
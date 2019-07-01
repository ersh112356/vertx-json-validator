/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vertx.core.schema;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vertx.core.response.ResponseObject;
import vertx.core.schema.resolver.JsonSchemaResolver;
import vertx.core.schema.resolver.JsonSchemaResolver.Schema;
import vertx.core.schema.utils.Result;

/**
 *
 * @author Eran
 */
public class JsonSchema{
    
    /** Holds the inner SimpleJson Schema. */
    private final Schema schema;
    
    /**
     * The constructor.
     * 
     * @param jo- the schema as a JsonObject.
     */
    public JsonSchema(JsonObject jo){
        
        schema = JsonSchemaResolver.resolveSchema(jo.getMap());
    }
    
    /**
     * Start to validate the Json against the schema.
     * 
     * @param json- the Json to validate against the schema.
     * 
     * @return a JsonObject that contains a positive reply on success, or an error otherwise.
     */
    public ResponseObject validate(JsonObject json){
        
        Result result = vertx.core.schema.resolver.JsonSchema.conformsSchema(json,schema);
        String cause = result.cause();
        
        ResponseObject outcome = ResponseObject.create(result.result(),cause);
        
        return outcome;
    }
    
    /**
     * Start to validate the Json against the schema.
     * 
     * @param json- the Json to validate against the schema.
     * 
     * @return a JsonObject that contains a positive reply on success, or an error otherwise.
     */
    public ResponseObject validate(JsonArray json){
        
        Result result = vertx.core.schema.resolver.JsonSchema.conformsSchema(json,schema);
        String cause = result.cause();
        
        ResponseObject outcome = ResponseObject.create(result.result(),cause);
        
        return outcome;
    }
}
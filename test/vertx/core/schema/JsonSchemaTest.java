/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.vertx.core.schema;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vertx.core.schema.JsonSchema;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import vertx.core.response.ResponseObject;

/**
 *
 * @author eransha
 */
public class JsonSchemaTest{
    
    public JsonSchemaTest(){
    }
    
    @BeforeClass
    public static void setUpClass(){
    }
    
    @AfterClass
    public static void tearDownClass(){
    }
    
    @Before
    public void setUp(){
    }
    
    @After
    public void tearDown(){
    }

    /**
     * Test of validate method, of class JsonSchema.
     */
    @Test
    public void testValidate_JsonObject(){
        
        System.out.println("validate");
        
        try
        {   // A good one that matches the schema.
            String goodJsonObjectData = "{\n" +
                            "            \"id\": 1,\n" +
                            "            \"name\": \"A green door\",\n" +
                            "            \"price\": 12.50,\n" +
                            "            \"tags\": [\"home\", \"green\"]\n" +
                            "        }";
            
            // A good one that contains one more parameter.
            String goodJsonObjectData1 = "{\n" +
                            "            \"id\": 1,\n" +
                            "            \"ref\":\"ref\",\n" +
                            "            \"name\": \"A green door\",\n" +
                            "            \"price\": 12.50,\n" +
                            "            \"tags\": [\"home\", \"green\"]\n" +
                            "        }";
            
            // A bad one where id was replaced by ref.
            String badJsonObjectData = "{\n" +
                            "            \"ref\": 1,\n" +
                            "            \"name\": \"A green door\",\n" +
                            "            \"price\": 12.50,\n" +
                            "            \"tags\": [\"home\", \"green\"]\n" +
                            "        }";
            
            // A bad one where id becomes a string.
            String badJsonObjectData1 = "{\n" +
                            "            \"id\": \"1\",\n" +
                            "            \"name\": \"A green door\",\n" +
                            "            \"price\": 12.50,\n" +
                            "            \"tags\": [\"home\", \"green\"]\n" +
                            "        }";
            
            // The schema itself.
            String schemaObjectData = "{\n" +
                                "            \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                                "            \"title\": \"Product\",\n" +
                                "            \"description\": \"A product from Acme's catalog\",\n" +
                                "            \"type\": \"object\",\n" +
                                "            \"properties\": {\n" +
                                "                \"id\": {\n" +
                                "                    \"description\": \"The unique identifier for a product\",\n" +
                                "                    \"type\": \"integer\"\n" +
                                "                },\n" +
                                "                \"name\": {\n" +
                                "                    \"description\": \"Name of the product\",\n" +
                                "                    \"type\": \"string\"\n" +
                                "                },\n" +
                                "                \"price\": {\n" +
                                "                    \"type\": \"number\",\n" +
                                "                    \"minimum\": 0,\n" +
                                "                    \"exclusiveMinimum\": true\n" +
                                "                },\n" +
                                "                \"tags\": {\n" +
                                "                    \"type\": \"array\",\n" +
                                "                    \"items\": {\n" +
                                "                        \"type\": \"string\"\n" +
                                "                    },\n" +
                                "                    \"minItems\": 1,\n" +
                                "                    \"uniqueItems\": true\n" +
                                "                }\n" +
                                "            },\n" +
                                "            \"required\": [\"id\", \"name\", \"price\"]\n" +
                                "        }";
            
            JsonObject goodObjectJson = new JsonObject(goodJsonObjectData);
            JsonObject goodObjectJson1 = new JsonObject(goodJsonObjectData1);
            JsonObject badObjectJson = new JsonObject(badJsonObjectData);
            JsonObject badObjectJson1 = new JsonObject(badJsonObjectData1);
            
            JsonObject sch = new JsonObject(schemaObjectData);
            JsonSchema schema = new JsonSchema(sch);
            
            ResponseObject response = schema.validate(goodObjectJson);
            response
                .ok(c -> {
                    System.out.println("ok");
                })
               .error(c -> {
                    System.out.println("error "+c);
                    fail("");
                });
            
            response = schema.validate(goodObjectJson1);
            response
                .ok(c -> {
                    System.out.println("ok");
                })
               .error(c -> {
                    System.out.println("error "+c);
                    fail("");
                });
            
            response = schema.validate(badObjectJson);
            response
                .ok(c -> {
                    System.out.println("ok");
                    fail("");
                })
               .error(c -> {
                    System.out.println("error "+c);
                });
            
            response = schema.validate(badObjectJson1);
            response
                .ok(c -> {
                    System.out.println("ok");
                    fail("");
                })
               .error(c -> {
                    System.out.println("error "+c);
                });
            
            
            // A good on that matches the schema.
            String goodJsonArrayData = "[\n" +
                                        "  \"home\",\n" +
                                        "  \"green\"\n" +
                                        "]";
            
            // A bad one that contains other set of elements in the array.
            String badJsonArrayData = "[\n" +
                                        "  {\n" +
                                        "    \"key\": \"value\"\n" +
                                        "  },\n" +
                                        "  {\n" +
                                        "    \"key1\": \"value1\"\n" +
                                        "  }\n" +
                                        "]";
            
            String schemaArrayData = "{\n" +
                                    "     \"definitions\": {},\n" +
                                    "     \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                                    "     \"$id\": \"http://example.com/root.json\",\n" +
                                    "     \"type\": \"array\",\n" +
                                    "     \"title\": \"The Root Schema\",\n" +
                                    "     \"items\": {\n" +
                                    "       \"$id\": \"#/items\",\n" +
                                    "       \"type\": \"string\",\n" +
                                    "       \"title\": \"The Items Schema\",\n" +
                                    "       \"default\": \"\",\n" +
                                    "       \"examples\": [\n" +
                                    "         \"home\",\n" +
                                    "         \"green\"\n" +
                                    "       ],\n" +
                                    "       \"pattern\": \"^(.*)$\"\n" +
                                    "     }\n" +
                                    "   }";
            
            JsonArray goodArrayJson = new JsonArray(goodJsonArrayData);
            JsonArray badArrayJson = new JsonArray(badJsonArrayData);
            
            sch = new JsonObject(schemaArrayData);
            schema = new JsonSchema(sch);
            
            response = schema.validate(goodArrayJson);
            response
                .ok(c -> {
                    System.out.println("ok");
                })
               .error(c -> {
                    System.out.println("error "+c);
                    fail("");
                });
            
            response = schema.validate(badArrayJson);
            response
                .ok(c -> {
                    System.out.println("ok");
                    fail("");
                })
               .error(c -> {
                    System.out.println("error "+c);
                });
            
            System.out.println("Done.");
        }
        catch(Throwable t)
        {
            fail(""+t);
        }
    }
}

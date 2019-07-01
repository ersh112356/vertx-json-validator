/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.vertx.core.schema.resolver;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import vertx.core.schema.resolver.JsonSchema;
import vertx.core.schema.resolver.JsonSchemaResolver;
import vertx.core.schema.resolver.JsonSchemaResolver.Schema;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import vertx.core.schema.utils.Result;

/**
 *
 * @author eransha
 */
public class JsonSchemaResolverTest{
    
    public JsonSchemaResolverTest(){
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
     * Test of resolveSchema method, of class JsonSchemaResolver.
     */
    @Test
    public void testResolveSchema_String(){
        
        System.out.println("resolveSchema");
        
        try
        {   // A good on that matches the schema.
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
            
            // A bad one where name is too short.
            String badJsonObjectData2 = "{\n" +
                            "            \"id\": 1,\n" +
                            "            \"name\": \"A\",\n" +
                            "            \"price\": 12.50,\n" +
                            "            \"tags\": [\"home\", \"green\"]\n" +
                            "        }";
            
            // A bad one where name is too long.
            String badJsonObjectData3 = "{\n" +
                            "            \"id\": 1,\n" +
                            "            \"name\": \"A green door with a long name\",\n" +
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
                                "                    \"type\": \"string\",\n" +
                                "                     \"minLength\": 2,\n"+
                                "                     \"maxLength\": 20\n"+
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
            JsonObject badObjectJson2 = new JsonObject(badJsonObjectData2);
            JsonObject badObjectJson3 = new JsonObject(badJsonObjectData3);
            
            JsonObject schema = new JsonObject(schemaObjectData);
            Schema sc = JsonSchemaResolver.resolveSchema(schema.getMap());
            
            Result result = JsonSchema.conformsSchema(goodObjectJson,sc);
            assertTrue(result.result());
            
            result = JsonSchema.conformsSchema(goodObjectJson1,sc);
            assertTrue(result.result());
            
            result = JsonSchema.conformsSchema(badObjectJson,sc);
            assertFalse(result.result());
            
            result = JsonSchema.conformsSchema(badObjectJson1,sc);
            assertFalse(result.result());
            
            result = JsonSchema.conformsSchema(badObjectJson2,sc);
            assertFalse(result.result());
            
            result = JsonSchema.conformsSchema(badObjectJson3,sc);
            assertFalse(result.result());
            
            
            // A good one that matches the schema.
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
            
            schema = new JsonObject(schemaArrayData);
            sc = JsonSchemaResolver.resolveSchema(schema.getMap());
            
            result = JsonSchema.conformsSchema(goodArrayJson,sc);
            assertTrue(result.result());
            
            result = JsonSchema.conformsSchema(badArrayJson,sc);
            assertFalse(result.result());
            
            // A good one that matches the schema (II).
            String goodObjectJson_II = "{\n" +
                        "  \"firstName\": \"John\",\n" +
                        "  \"lastName\": \"Doe\",\n" +
                        "  \"age\": 21\n" +
                        "}";
            
            // A bad one that contains an integer instead of a string (II).
            String badObjectData_II = "{\n" +
                            "  \"firstName\": 1,\n" +
                            "  \"lastName\": \"Doe\",\n" +
                            "  \"age\": 21\n" +
                            "}";
            
            // A bad one that contains a negative integer instead of positive (II).
            String badJsonObjectData1_II = "{\n" +
                                "  \"firstName\": \"John\",\n" +
                                "  \"lastName\": \"Doe\",\n" +
                                "  \"age\": -21\n" +
                                "}";
            
            
            
            String schemaObjectData_II = "{\n" +
                        "  \"$id\": \"https://example.com/person.schema.json\",\n" +
                        "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                        "  \"title\": \"Person\",\n" +
                        "  \"type\": \"object\",\n" +
                        "  \"properties\": {\n" +
                        "    \"firstName\": {\n" +
                        "      \"type\": \"string\",\n" +
                        "      \"description\": \"The person's first name.\"\n" +
                        "    },\n" +
                        "    \"lastName\": {\n" +
                        "      \"type\": \"string\",\n" +
                        "      \"description\": \"The person's last name.\"\n" +
                        "    },\n" +
                        "    \"age\": {\n" +
                        "      \"description\": \"Age in years which must be equal to or greater than zero.\",\n" +
                        "      \"type\": \"integer\",\n" +
                        "      \"minimum\": 0\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            
            
            schema = new JsonObject(schemaObjectData_II);
            sc = JsonSchemaResolver.resolveSchema(schema.getMap());
            
            result = JsonSchema.conformsSchema(new JsonObject(goodObjectJson_II),sc);
            assertTrue(result.result());
            
            result = JsonSchema.conformsSchema(new JsonObject(badObjectData_II),sc);
            assertFalse(result.result());
            
            result = JsonSchema.conformsSchema(new JsonObject(badJsonObjectData1_II),sc);
            assertFalse(result.result());
            
            
            // A good one that matches the schema (III).
            String goodObjectJson_III = "{\n" +
                        "  \"latitude\": 48.858093,\n" +
                        "  \"longitude\": 2.294694\n" +
                        "}";
            
            // A bad one that contains a string instead of a number (III).
            String badObjectData_III = "{\n" +
                        "  \"latitude\": \"48\",\n" +
                        "  \"longitude\": 2.294694\n" +
                        "}";
            
            // A bad one that contains a negative integer instead of positive number (III).
            String badJsonObjectData1_III = "{\n" +
                        "  \"latitude\": 48.858093,\n" +
                        "  \"longitude\": -200.294694\n" +
                        "}";
            
            // A bad one that contains a much bigger number (III).
            String badJsonObjectData2_III = "{\n" +
                        "  \"latitude\": 248.858093,\n" +
                        "  \"longitude\": 2.294694\n" +
                        "}";
            
            String schemaObjectData_III = "{\n" +
                        "  \"$id\": \"https://example.com/geographical-location.schema.json\",\n" +
                        "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                        "  \"title\": \"Longitude and Latitude Values\",\n" +
                        "  \"description\": \"A geographical coordinate.\",\n" +
                        "  \"required\": [ \"latitude\", \"longitude\" ],\n" +
                        "  \"type\": \"object\",\n" +
                        "  \"properties\": {\n" +
                        "    \"latitude\": {\n" +
                        "      \"type\": \"number\",\n" +
                        "      \"minimum\": -90,\n" +
                        "      \"maximum\": 90\n" +
                        "    },\n" +
                        "    \"longitude\": {\n" +
                        "      \"type\": \"number\",\n" +
                        "      \"minimum\": -180,\n" +
                        "      \"maximum\": 180\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            
            schema = new JsonObject(schemaObjectData_III);
            sc = JsonSchemaResolver.resolveSchema(schema.getMap());
            
            result = JsonSchema.conformsSchema(new JsonObject(goodObjectJson_III),sc);
            assertTrue(result.result());
            
            result = JsonSchema.conformsSchema(new JsonObject(badObjectData_III),sc);
            assertFalse(result.result());
            
            result = JsonSchema.conformsSchema(new JsonObject(badJsonObjectData1_III),sc);
            assertFalse(result.result());
            
            result = JsonSchema.conformsSchema(new JsonObject(badJsonObjectData2_III),sc);
            assertFalse(result.result());
            
            
            // A good one that matches the schema (IV).
            String goodObjectJson_IV = "{\n" +
                        "  \"fruits\": [ \"apple\", \"orange\", \"pear\" ],\n" +
                        "  \"vegetables\": [\n" +
                        "    {\n" +
                        "      \"veggieName\": \"potato\",\n" +
                        "      \"veggieLike\": true\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"veggieName\": \"broccoli\",\n" +
                        "      \"veggieLike\": false\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
            
            // A bad one that contains an integer instead of a string (IV).
            String badObjectData_IV = "{\n" +
                        "  \"fruits\": [ 2, \"orange\", \"pear\" ],\n" +
                        "  \"vegetables\": [\n" +
                        "    {\n" +
                        "      \"veggieName\": \"potato\",\n" +
                        "      \"veggieLike\": true\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"veggieName\": \"broccoli\",\n" +
                        "      \"veggieLike\": false\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
            
            // A bad one that contains a number instead of positive a string (IV).
            String badJsonObjectData1_IV = "{\n" +
                        "  \"fruits\": [ \"apple\", \"orange\", \"pear\" ],\n" +
                        "  \"vegetables\": [\n" +
                        "    {\n" +
                        "      \"veggieName\": 2,\n" +
                        "      \"veggieLike\": true\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"veggieName\": \"broccoli\",\n" +
                        "      \"veggieLike\": false\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
            
            // A bad one that is missing vegetable (IV).
            /*String badJsonObjectData2_IV = "{\n" +
                        "  \"fruits\": [ \"apple\", \"orange\", \"pear\" ]\n" +
                        "}";*/
            
            String schemaObjectData_IV = "{\n" +
                        "  \"$id\": \"https://example.com/arrays.schema.json\",\n" +
                        "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                        "  \"description\": \"A representation of a person, company, organization, or place\",\n" +
                        "  \"type\": \"object\",\n" +
                        "  \"properties\": {\n" +
                        "    \"fruits\": {\n" +
                        "      \"type\": \"array\",\n" +
                        "      \"items\": {\n" +
                        "        \"type\": \"string\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    \"vegetables\": {\n" +
                        "      \"type\": \"array\",\n" +
                        "      \"items\": { \"$ref\": \"#/definitions/veggie\" }\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"definitions\": {\n" +
                        "    \"veggie\": {\n" +
                        "      \"type\": \"object\",\n" +
                        "      \"required\": [ \"veggieName\", \"veggieLike\" ],\n" +
                        "      \"properties\": {\n" +
                        "        \"veggieName\": {\n" +
                        "          \"type\": \"string\",\n" +
                        "          \"description\": \"The name of the vegetable.\"\n" +
                        "        },\n" +
                        "        \"veggieLike\": {\n" +
                        "          \"type\": \"boolean\",\n" +
                        "          \"description\": \"Do I like this vegetable?\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            
            schema = new JsonObject(schemaObjectData_IV);
            sc = JsonSchemaResolver.resolveSchema(schema.getMap());
            
            result = JsonSchema.conformsSchema(new JsonObject(goodObjectJson_IV),sc);
            assertTrue(result.result());
            
            result = JsonSchema.conformsSchema(new JsonObject(badObjectData_IV),sc);
            assertFalse(result.result());
            
            result = JsonSchema.conformsSchema(new JsonObject(badJsonObjectData1_IV),sc);
            assertFalse(result.result());
            
            /*result = JsonSchema.conformsSchema(new JsonObject(badJsonObjectData2_IV),sc);
            assertFalse(result.result());*/
            
            System.out.println("Done.");
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            
            fail(""+t);
        }
    }
}
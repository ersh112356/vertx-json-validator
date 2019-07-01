# vertx-json-validator

A simple JSON schema validator for the Vert.x world.
This JSON validator is well adapted to be used in conjunction with Vert.x (https://vertx.io).
At the moment it supports Draft 4, Draft 6 or Draft 7, and Java8+.

When to use this:
There are a few good JSON schema validators out there, I was looking for a decent one that can integrate against Vert.x, yet ended empty handed.
It’s simple to integrate and quite fast.
The code includes a few samples of usage, so you just need to put the dependencies into your classpath, write a few simple lines of code, and that’s it!

Depedencies:
vertx-core-3.x
jackson-annotations-2.9.8
jackson-core-2.9.8
jackson-databind-2.9.8
netty-buffer-4.1.30.Final
accessors-smart-1.2
asm-5.0.4
json-path-2.4.0
json-smart-2.3
slf4j-api-1.7.25
vertx-response (optional)

All can be found here.

 
A quickstart:

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
            
            Result result = JsonSchema.conformsSchema(new JsonObject(goodObjectJson_IV),sc);
            assertTrue(result.result());  

Have fun!

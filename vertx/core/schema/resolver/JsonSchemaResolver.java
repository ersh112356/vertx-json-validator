/*
*
*
*/
package vertx.core.schema.resolver;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.vertx.core.json.JsonObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 * @author eransha
 */
public final class JsonSchemaResolver{

    /**
     * 
     */
    public static final class Schema extends HashMap<String,Object>{

        /** The schema. */
        private Schema parent;
        /** The id of the schema. */
        private final String id;

        /**
         * 
         * @param map 
         */
        private Schema(Map<String, Object> map){
            
            this(map,(String)map.get("id"));
        }

        /**
         * 
         * @param map
         * @param id 
         */
        private Schema(Map<String, Object> map, String id){
            
            super(map);
            this.id = id;
        }

        /**
         * 
         * @param parent 
         */
        public void setParent(Schema parent){
            
            this.parent = parent;
        }

        /**
         * 
         * @return 
         */
        public Schema getParent(){
            
            return parent;
        }

        /**
         * 
         * @return 
         */
        public String getId(){
            
            return id;
        }

        /**
         * 
         * @param <T>
         * @param key
         * @return 
         */
        @SuppressWarnings("unchecked")
        public <T> T get(String key)
        {
            return (T) super.get(key);
        }
    }

    /** */
    private static final Pattern ABSOLUTE = Pattern.compile("^.*://.*");
    /** */
    private static Map<String, Schema> loadedSchemas = new HashMap<>();

    /**
     * 
     * @param uri
     * @return 
     */
    public static Schema resolveSchema(String uri)
    {
        return resolveSchema(uri, null);
    }

    /**
     * 
     * @param uri
     * @param parent
     * @return 
     */
    public synchronized static Schema resolveSchema(String uri, Schema parent){
        
        uri = resolveUri(uri,parent);
        
        if(!loadedSchemas.containsKey(uri))
        {
            tryToLoad(uri,parent);
        }
        
        return loadedSchemas.get(uri);
    }

    /**
     * 
     * @param schema
     * @return 
     */
    public static Schema resolveSchema(Map<String, Object> schema){
        
        return new JsonSchemaResolver.Schema(schema);
    }

    /**
     * 
     * @param schema
     * @param parent
     * @return 
     */
    public static Schema resolveSchema(Map<String, Object> schema, Schema parent){
        
        final Schema _schema = new JsonSchemaResolver.Schema(schema);
        _schema.setParent(parent);

        return _schema;
    }

    /**
     * 
     * @param uri
     * @param parent
     * @return 
     */
    private static String resolveUri(String uri, Schema parent){
        
        if(ABSOLUTE.matcher(uri).matches())
        {   // If it is an absolute URI return it, nothing to resolve/
            return uri;
        }

        if(parent==null)
        {
            throw new RuntimeException("relative URI without a base URI");
        }
        
        String id = parent.getId();
        
        if(id==null)
        {   // Internal, must starts with #/.
            return uri.substring(2);
        }
        
        String parentBaseUri;
        int idx = id.indexOf('#');

        if(idx!=-1)
        {
            parentBaseUri = parent.getId().substring(0,parent.getId().indexOf('#'));
        }
        else
        {
            parentBaseUri = parent.getId();
        }

        if(uri.charAt(0)=='#')
        {
            return parentBaseUri + uri;
        }

        throw new RuntimeException("non relative URI");
    }

    /**
     * Try to load the ref.
     * 
     * @param ref- the URI to use.
     * @param parent- the parent schema.
     */
    private static void tryToLoad(String ref, Schema parent){
        
        try
        {
            JsonObject json;
            final URI uri = new URI(ref);
            final String scheme = uri.getScheme();
            
            if(scheme==null)
            {
                try
                {   // Internal, create a search string. I.e.: ['path1']['path2']...
                    String path = "$['"+ref.replaceAll("/","']['")+"']";
                    DocumentContext jsonContext = JsonPath.parse(parent);
                    Map elements = jsonContext.read(path);

                    json = new JsonObject(elements);
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {   // There is a scheme so we can load from http, classpath, or file.
                switch(scheme)
                {
                    case "classpath":
                        json = loadFromClasspath(uri);
                        break;
                    case "http":
                    case "https":
                        json = loadFromURL(uri);
                        break;
                    case "file":
                        json = loadFromFile(uri);
                        break;
                    default:
                        throw new RuntimeException("Unknown Protocol: " + scheme);
                }
            }

            final Schema schema = new Schema(json.getMap(),ref);
            final String schemaId = schema.getId();

            if(schemaId!=null)
            {
                if(loadedSchemas.containsKey(schemaId))
                {
                    throw new RuntimeException("Schema ID [" + schemaId + "] already in use!");
                }
                
                // register the schema into the registry using its Id
                loadedSchemas.put(schemaId,schema);
            }
            else
            {
                if(loadedSchemas.containsKey(ref))
                {
                    throw new RuntimeException("Schema URI [" + uri + "] already in use!");
                }

                // register the schema into the registry using its URI
                loadedSchemas.put(ref, schema);
            }

        }
        catch(URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param uri
     * @return 
     */
    private static JsonObject loadFromURL(final URI uri){
        
        try
        {
            try(Reader r = new BufferedReader(new InputStreamReader(uri.toURL().openStream(), "UTF-8")))
            {

                Writer writer = new StringWriter();

                char[] buffer = new char[1024];
                int n;
                
                while((n=r.read(buffer))!=-1)
                {
                    writer.write(buffer, 0, n);
                }

                final JsonObject json = new JsonObject(writer.toString());
                final String fragment = uri.getFragment();
                
                if(fragment!=null)
                {
                    if(json.containsKey(fragment))
                    {
                        return json.getJsonObject(fragment);
                    }
                    else
                    {
                        throw new RuntimeException("Fragment #" + fragment + " not found!");
                    }
                }

                return json;
            }
        }
        catch(IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * 
     * @param uri
     * @return 
     */
    private static JsonObject loadFromClasspath(final URI uri){
        
        try
        {
            final String path = uri.getPath();
            
            if(path==null || "".equals(path))
            {
                throw new RuntimeException("Invalid path [" + uri.toString() + "]");
            }
            
            try(Reader r = new BufferedReader(new InputStreamReader(JsonSchemaResolver.class.getResourceAsStream(path), "UTF-8")))
            {
                Writer writer = new StringWriter();

                char[] buffer = new char[1024];
                int n;
                
                while((n=r.read(buffer))!=-1)
                {
                    writer.write(buffer,0,n);
                }

                final JsonObject json = new JsonObject(writer.toString());
                final String fragment = uri.getFragment();
                
                if(fragment!=null && !"".equals(fragment))
                {
                    String[] nodes = fragment.split("/");
                    JsonObject subjson = json;

                    for(int i = "".equals(nodes[0]) ? 1 : 0 ; i < nodes.length; i++)
                    {
                        if(subjson.containsKey(nodes[i]))
                        {
                            subjson = subjson.getJsonObject(nodes[i]);
                        }
                        else
                        {
                            throw new RuntimeException("Fragment Node #" + nodes[i] + " not found!");
                        }
                    }

                    return subjson;
                }

                return json;
            }
        }
        catch(IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * 
     * @param uri
     * @return 
     */
    private static JsonObject loadFromFile(final URI uri){
        
        try
        {
            final String path = uri.getPath();
            
            if(path==null || "".equals(path))
            {
                throw new RuntimeException("Invalid path [" + uri.toString() + "]");
            }
            
            try(Reader r = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8")))
            {

                Writer writer = new StringWriter();

                char[] buffer = new char[1024];
                int n;
                
                while((n=r.read(buffer))!=-1)
                {
                    writer.write(buffer, 0, n);
                }

                final JsonObject json = new JsonObject(writer.toString());
                final String fragment = uri.getFragment();
                
                if(fragment!=null)
                {
                    if(json.containsKey(fragment))
                    {
                        return json.getJsonObject(fragment);
                    }
                    else
                    {
                        throw new RuntimeException("Fragment #" + fragment + " not found!");
                    }
                }

                return json;
            }
        }
        catch(IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }
}
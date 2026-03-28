import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

import java.io.StringReader;


public class PoliceAPIDemo{ 

    public static void main(String[] args) throws Exception {
        String jsonString = getJsonString("https://data.police.uk/api/crimes-street-dates");
    }

    public static void outputStringToJson(String jsonString, String outputFile) throws Exception{
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        try (JsonReader reader = Json.createReader(new StringReader(jsonString));
            FileWriter w = new FileWriter(outputFile);) {
            //Turn it into a json object (allows for further manipulation)
            JsonStructure jsonObject = reader.read();
            //Doesnt implement auto closeable
            JsonWriterFactory jf = Json.createWriterFactory(properties);
            
            JsonWriter jg = jf.createWriter(w);
            jg.write(jsonObject);
        }
    }

    //Returns a whole JSON file as a string from a given url
    public static String getJsonString(String url) throws IOException, InterruptedException, URISyntaxException {
        //Build a request for a certain url
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .version(HttpClient.Version.HTTP_2)
            .GET()
            .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
        .send(request, HttpResponse.BodyHandlers.ofString());
        String jsonString = response.body();
        return jsonString;
    }
}
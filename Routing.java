import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

import java.io.StringReader;
import java.io.*;

import java.util.stream.Collectors;

public class Routing {
    public static void main(String[] args) throws Exception {
        
        // London to Manchester (longitude,latitude format)
        double startLon = -0.1276, startLat = 51.5074;
        double endLon = -2.2426,   endLat = 53.4808;

        String url = "http://router.project-osrm.org/route/v1/driving/"
                   + startLon + "," + startLat + ";"
                   + endLon   + "," + endLat
                   + "?overview=full&steps=true&geometries=geojson";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
            new InputStreamReader(conn.getInputStream())
        );
        String response = in.lines().collect(Collectors.joining());
        in.close();

        System.out.println(response);
        outputStringToJson(response,"route.json");
    }

    public static void extractCoordinates() {
        
    }

    public static void outputStringToJson(String jsonString, String outputFile) throws Exception{
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        try (JsonReader reader = Json.createReader(new StringReader(jsonString));
            FileWriter w = new FileWriter(outputFile);) {
            //Turn it into a json object (allows for further manipulation)
            JsonStructure jsonObject = reader.read();
            JsonWriterFactory jf = Json.createWriterFactory(properties);
            
            JsonWriter jg = jf.createWriter(w);
            jg.write(jsonObject);
            jg.close();
            w.close();
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
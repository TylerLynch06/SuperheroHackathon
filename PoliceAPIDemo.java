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

import java.util.Scanner;


public class PoliceAPIDemo{

    public static void main(String[] args) throws Exception {
        Scanner reader = new Scanner(System.in);
        //nw, ne, se, sw lat/lon pairs
        String[] coords = new String[4];
        String[] compass = {"NW", "NE", "SE", "SW"};
        String date;
        
        while (true) {
            for (int i = 0; i < 4; i++) {
                System.out.printf("%nEnter %s coords (format: 'lat, lon'): ", compass[i]);
                coords[i] = reader.nextLine();
            }

            System.out.printf("%nEnter date (format: YYYY-MM): ");
            date = reader.nextLine();

            String url = String.format("https://data.police.uk/api/crimes-street/all-crime?date=%s&poly=%s:%s:%s:%s", date, coords[0], coords[1], coords[2], coords[3]);
            System.out.println(url);
            String jsonString = getJsonString(url);
            outputStringToJson(jsonString, "test.json");
        }
    }

    public static void dumpToFile(String jsonString, String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename);
            writer.println(jsonString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
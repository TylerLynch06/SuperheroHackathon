import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

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
import javax.json.stream.JsonParserFactory;
import javax.json.JsonArray;

import java.io.*;

import java.util.stream.Collectors;

public class Router {

    public Route createRoute(double startLat, double startLon, double endLat, double endLon) {
        Route route = null;
        try {
            String response = callMapApi(startLat, startLon, endLat, endLon);
            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject root = reader.readObject();
            //pointDouble

            JsonArray coordinates = root.getJsonArray("routes").getJsonObject(0).getJsonObject("geometry").getJsonArray("coordinates");
            double[][] points = new double[coordinates.size()][2];
            for (int i = 0; i < coordinates.size(); i++) {
                JsonArray point = coordinates.getJsonArray(i);
                double latitude = point.getJsonNumber(1).doubleValue();
                double longitude = point.getJsonNumber(0).doubleValue();
                double[] pointDouble = {(float)latitude, (float) longitude};
                points[i] = pointDouble;
            }
            route = new Route(points);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return route;
    }

    private String callMapApi(double startLat, double startLon, double endLat, double endLon) throws Exception {
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
        return response;      
    }

    //Returns a whole JSON file as a string from a given url
    private String getJsonString(String url) throws IOException, InterruptedException, URISyntaxException {
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
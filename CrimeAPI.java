import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonStructure;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import javax.json.JsonValue;
import javax.json.JsonObject;

import java.util.Set;
import java.util.HashSet;

/*
CrimeAPI
Requests and parses the UK police data API, fetching crimes committed during a specific month for a specified region
*/

public class CrimeAPI {
    private String[] coords;

    public CrimeAPI(String[] coords) {
        //[Northwest, Northeast, Southeast, Southwest] latitude/longitude pairs to draw a region polygon (format: "lat,lon" for each compasspoint)
        this.coords = coords;
    }

    //return the crimes that were committed in the location during a specified month
    //Date format: "YYYY-MM"
    public Set<Crime> getCrimesByDate(String date) {
        JsonArray crimesJson = getCrimesJson(date); //request the API and get the data, returns in JSON format

        Set<Crime> crimes = new HashSet<>(); //Set of Crime objects

        for (JsonValue crimeObj: crimesJson) {
            //parse the JsonValue into an actually useful JsonObject cos JsonValue doesn't have methods to get the values
            JsonReader reader = Json.createReader(new StringReader(crimeObj.toString()));
            JsonObject object = reader.readObject();

            Crime crime = new Crime(
                object.getJsonObject("location").getString("latitude"),
                object.getJsonObject("location").getString("longitude"),
                object.getString("category")
            );
            
            crimes.add(crime);
        }

        return crimes;
    }

    public JsonArray getCrimesJson(String date) {
        String url = String.format(
            "https://data.police.uk/api/crimes-street/all-crime?date=%s&poly=%s:%s:%s:%s", date, coords[0], coords[1], coords[2], coords[3]
        );
    
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(url))
            .version(HttpClient.Version.HTTP_2)
            .GET()
            .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = response.body();

            //Parse the string into json object
            JsonReader reader = Json.createReader(new StringReader(jsonString));
            JsonArray jsonArray = reader.readArray();

            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("UNABLE TO RETRIEVE CRIME DATA");
            System.exit(0);
            return null;
        }
    }
}
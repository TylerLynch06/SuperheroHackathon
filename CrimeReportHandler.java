import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublisher;

import java.time.LocalDateTime;

import java.util.Set;
import java.util.HashSet;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonStructure;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import javax.json.JsonValue;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonObject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;

public class CrimeReportHandler extends IncidentHandler {
    //url for the JSON file of crimes, hosted on jsonbin.io
    private static final String serverURL = "https://api.jsonbin.io/v3/b/69c7f3f55fdde574550ac5f2";
    private static JsonObject metadata;

    public CrimeReportHandler() {
        super();
        
        //declare api metadata
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObject metadata = factory.createObjectBuilder()
        .add("id", "69c7f3f55fdde574550ac5f2")
        .add("private", true)
        .add("createdAt", "2026-03-28T15:29:57.759Z")
        .build();
        this.metadata = metadata;
    }

    //json POST update the json
    public void reportCrime(CrimeReport report) {
        //must first request all current crimes in order to update the whole file
        JsonObject wholeFile = getWholeFile(serverURL);
        JsonArray reportsJson = wholeFile.getJsonArray("record");

        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObject newReport = factory.createObjectBuilder()
            .add("crimeType", report.getType())
            .add("latitude", report.getLatitude())
            .add("longitude", report.getLongitude())
            .add("timestamp", DateTimeTools.dateToString(report.getTimestamp()))
            .build();

        //Update the reportsJson array by building a new array
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (JsonValue obj: reportsJson) {
            builder.add(obj);
        }
        builder.add(newReport);
        JsonArray newReportsJson = builder.build();

        //build a new object for the whole file
        JsonObject newWholeFile = factory.createObjectBuilder()
            .add("record", newReportsJson)
            .add("metadata", metadata)
            .build();

        try {
            HttpRequest request = HttpRequest.newBuilder()
            .header("X-Access-Key", jsonKey)
            .header("Content-Type", "application/json")
            .uri(new URI(serverURL))
            .version(HttpClient.Version.HTTP_2)
            .PUT(HttpRequest.BodyPublishers.ofString(newReportsJson.toString()))
            .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = response.body();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR POSTING TO JSON SERVER");
            System.exit(0);
        }
    }

    //json GET: 
    public Set<CrimeReport> getRecentCrimeReports() {
        Set<CrimeReport> crimeReports = new HashSet<>();

        JsonArray reportsJson = getJsonData(serverURL);
        
        for (JsonValue reportObj: reportsJson) {
            //parse the JsonValue into an actually useful JsonObject cos JsonValue doesn't have methods to get the values
            JsonReader reader = Json.createReader(new StringReader(reportObj.toString()));
            JsonObject object = reader.readObject();

            //only return reports that are under 1 hour old
            //TODO: periodically remove old reports from the database
            long secondsAgo = DateTimeTools.secondsBetween(
                DateTimeTools.stringToDate(object.getString("timestamp")),
                LocalDateTime.now()
            );
            if (secondsAgo < TIMELIMIT) {
                CrimeReport report = new CrimeReport(
                    object.getJsonNumber("latitude").doubleValue(),
                    object.getJsonNumber("longitude").doubleValue(),
                    object.getString("crimeType"),
                    object.getString("timestamp")
                );
            
                crimeReports.add(report);
            }
        }

        return crimeReports;
    }
}
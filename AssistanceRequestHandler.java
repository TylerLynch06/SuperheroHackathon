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

public class AssistanceRequestHandler extends IncidentHandler {
    //url for the JSON file of assistance requests, hosted on jsonbin.io
    private static final String serverURL = "https://api.jsonbin.io/v3/b/69c81ff26860e0745bfcb156";
    private static JsonObject metadata;

    public AssistanceRequestHandler() {
        super();
        
        //declare api metadata
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObject metadata = factory.createObjectBuilder()
        .add("id", "69c81ff26860e0745bfcb156")
        .add("private", true)
        .add("createdAt", "2026-03-28T18:37:38.948Z")
        .build();
        this.metadata = metadata;
    }

    //json POST update the json
    public void requestAssistance(AssistanceRequest aRequest) {
        //must first request all current assistance requests in order to update the file
        JsonObject wholeFile = getWholeFile(serverURL);
        JsonArray requestsJson = wholeFile.getJsonArray("record");

        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObject newRequest = factory.createObjectBuilder()
            .add("description", aRequest.getDescription())
            .add("latitude", aRequest.getLatitude())
            .add("longitude", aRequest.getLongitude())
            .add("timestamp", DateTimeTools.dateToString(aRequest.getTimestamp()))
            .build();

        //Update the reportsJson array by building a new array
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (JsonValue obj: requestsJson) {
            builder.add(obj);
        }
        builder.add(newRequest);
        JsonArray newRequestsJson = builder.build();

        //build a new object for the whole file
        JsonObject newWholeFile = factory.createObjectBuilder()
            .add("record", newRequestsJson)
            .add("metadata", metadata)
            .build();

        try {
            HttpRequest request = HttpRequest.newBuilder()
            .header("X-Access-Key", jsonKey)
            .header("Content-Type", "application/json")
            .uri(new URI(serverURL))
            .version(HttpClient.Version.HTTP_2)
            .PUT(HttpRequest.BodyPublishers.ofString(newRequestsJson.toString()))
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
    public Set<AssistanceRequest> getRecentAssistanceRequests() {
        Set<AssistanceRequest> assistanceRequests = new HashSet<>();

        JsonArray requestsJson = getJsonData(serverURL);
        
        for (JsonValue requestObj: requestsJson) {
            //parse the JsonValue into an actually useful JsonObject cos JsonValue doesn't have methods to get the values
            JsonReader reader = Json.createReader(new StringReader(requestObj.toString()));
            JsonObject object = reader.readObject();

            //only return requests that are under 1 hour old
            //TODO: periodically remove old reports from the database
            long secondsAgo = DateTimeTools.secondsBetween(
                DateTimeTools.stringToDate(object.getString("timestamp")),
                LocalDateTime.now()
            );
            if (secondsAgo < TIMELIMIT) {
                AssistanceRequest report = new AssistanceRequest(
                    object.getJsonNumber("latitude").doubleValue(),
                    object.getJsonNumber("longitude").doubleValue(),
                    object.getString("description"),
                    object.getString("timestamp")
                );
            
                assistanceRequests.add(report);
            }
        }

        return assistanceRequests;
    }
}
import java.io.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonStructure;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import javax.json.JsonValue;
import javax.json.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public abstract class IncidentHandler {
    protected final String jsonKey;
    protected final long TIMELIMIT = 3600; //How many seconds back in time to check for reports/requests from

    public IncidentHandler() {
        this.jsonKey = getAccessKey("JSON-AccessKey.txt"); //JSONBin.io access key
    }

    private String getAccessKey(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("ERROR READING JSON ACCESS KEY");
            System.exit(0);
            return null;
        }
    }

    //get json array without the JSONBin metadata
    public JsonArray getJsonData(String serverURL) {
        JsonReader reader = readServer(serverURL);
        JsonObject jsonObject = reader.readObject();

        return jsonObject.getJsonArray("record");
    }

    //get entire JSONBin file with metadata, necessary for post request
    public JsonObject getWholeFile(String serverURL) {
        JsonReader reader = readServer(serverURL);
        return reader.readObject();
    }

    //Return reader capable of parsing the file
    public JsonReader readServer(String serverURL) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
            .header("X-Access-Key", jsonKey)
            .uri(new URI(serverURL))
            .version(HttpClient.Version.HTTP_2)
            .GET()
            .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String jsonString = response.body();

            return Json.createReader(new StringReader(jsonString));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("UNABLE TO RETRIEVE JSON DATA FROM SERVER");
            System.exit(0);
            return null;
        }
    }
}
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.JsonValue;
import java.io.StringReader;

import java.net.URISyntaxException;
import java.io.IOException;

import java.util.Set;
import java.util.HashSet;

public class TestCrimeAPI {
    public static void main(String[] args) {
        //central london near st pauls'
        //Northwest, northeast, southeast, southwest
        String[] regionBoundaries = {
            "51.517651,-0.101350",
            "51.519324,-0.079203",
            "51.509857,-0.074201",
            "51.509443,-0.103340"
        };

        CrimeAPI crimeFinder = new CrimeAPI(regionBoundaries);
        Set<Crime> crimes = crimeFinder.getCrimesByDate("2024-10"); //get set of crimes
        
        for (Crime c: crimes) {
            System.out.printf("%s was committed at %s, %s%n", c.getType(), c.getLatitude(), c.getLongitude());
        }
    }
}
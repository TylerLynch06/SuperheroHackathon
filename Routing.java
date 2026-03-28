import java.net.*;
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
                   + "?overview=false";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
            new InputStreamReader(conn.getInputStream())
        );
        String response = in.lines().collect(Collectors.joining());
        in.close();

        System.out.println(response);
    }
}
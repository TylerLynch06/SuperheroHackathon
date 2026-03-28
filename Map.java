// Map.java
import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.*;
import javax.swing.*;
import java.util.*;
import org.jxmapviewer.input.*;
import java.util.HashSet;
import java.util.Set;
import java.awt.*;
import java.awt.geom.Point2D;

public class Map extends JPanel {

    private JXMapViewer mapViewer;
    private Set<Waypoint> allWaypoints; // Store all crime and route points

    Map() {
        setLayout(new BorderLayout());

        mapViewer = new JXMapViewer();

        // Set up the tile factory for OpenStreetMap
        TileFactoryInfo info = new TileFactoryInfo(
            1, 15, 17,
            256, true, true,
            "https://tile.openstreetmap.org",
            "x", "y", "z") {
                @Override
                public String getTileUrl(int x, int y, int zoom) {
                    int z = 17 - zoom;
                    return this.baseURL + "/" + z + "/" + x + "/" + y + ".png";
                }
            };

        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Set a default address location (London)
        GeoPosition london = new GeoPosition(51.513447, -0.089056);
        mapViewer.setAddressLocation(london);
        mapViewer.setZoom(2);

        // Initialize waypoints (to store all points, both route and crime)
        allWaypoints = new HashSet<>();

        // Add markers for the route (sample route)
        Set<Waypoint> waypoints = new HashSet<>();
        Router router = new Router();
        Route route = router.createRoute(51.5074, -0.1278, 51.530811, -0.081829);
        for (double[] array : route.getCoordinates()) {
            double lat = array[0];
            double lon = array[1];
            waypointPainter(waypoints, lat, lon, "route");
        }

        allWaypoints.addAll(waypoints);

        // Fetch initial crime data from the API
        addAPICrimePoints();

        // Combine all waypoints (route + API crimes) and set the painter
        WaypointPainter<Waypoint> painter = paintMarker(mapViewer);
        painter.setWaypoints(allWaypoints);
        mapViewer.setOverlayPainter(painter);

        add(mapViewer, BorderLayout.CENTER); // Add map viewer to panel
    }

    // Method to add the initial crime points from the API (blue)
    private void addAPICrimePoints() {
        String[] regionBoundaries = {
            "51.517651,-0.101350",
            "51.519324,-0.079203",
            "51.509857,-0.074201",
            "51.509443,-0.103340"
        };

        CrimeAPI crimeFinder = new CrimeAPI(regionBoundaries);
        Set<Crime> crimes = crimeFinder.getCrimesByDate("2026-01");

        for (Crime c : crimes) {
            waypointPainter(allWaypoints, c.getLatitude(), c.getLongitude(), "crime");
        }
    }

    // Method to add a new reported crime (yellow)
    public void addReportedCrime(Crime reportedCrime) {
        waypointPainter(allWaypoints, reportedCrime.getLatitude(), reportedCrime.getLongitude(), "reported");
        WaypointPainter<Waypoint> painter = paintMarker(mapViewer);
        painter.setWaypoints(allWaypoints);
        mapViewer.setOverlayPainter(painter);
        repaint(); // Refresh the map
    }

    // Utility method to add waypoints
    public static void waypointPainter(Set<Waypoint> waypoints, double lat, double longitude, String type) {
        Waypoint currentPoint = new CustomWaypoint(lat, longitude, type);
        waypoints.add(currentPoint);
    }

    // Method to paint the markers on the map
    public static WaypointPainter<Waypoint> paintMarker(JXMapViewer map) {
        WaypointPainter<Waypoint> painter = new WaypointPainter<>() {
            @Override
            protected void doPaint(Graphics2D g, JXMapViewer map, int w, int h) {
                for (Waypoint wp : getWaypoints()) {
                    CustomWaypoint customWaypoint = (CustomWaypoint) wp; // Cast to CustomWaypoint
                    Point2D pt = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
                    Point2D mapCenter = map.getCenter();
                    int x = (int) (pt.getX() - mapCenter.getX() + w / 2);
                    int y = (int) (pt.getY() - mapCenter.getY() + h / 2);
                    
                    // Draw crime points (blue) and new reported crimes (yellow)
                    if ("crime".equals(customWaypoint.getType())) {
                        g.setColor(Color.BLUE);
                        g.fillOval(x - 10, y - 10, 10, 10); // Blue for original crimes (larger)
                    } else if ("reported".equals(customWaypoint.getType())) {
                        g.setColor(Color.YELLOW);
                        g.fillOval(x - 8, y - 8, 8, 8); // Yellow for reported crimes (slightly smaller)
                    } else if ("route".equals(customWaypoint.getType())) {
                        g.setColor(Color.RED);
                        g.fillOval(x - 5, y - 5, 5, 5); // Red for route points (smaller)
                    }
                }
            }
        };
        return painter;
    }

    // Custom Waypoint class that holds the type (crime, reported, route)
    static class CustomWaypoint extends DefaultWaypoint {
        private String type;

        public CustomWaypoint(double lat, double lon, String type) {
            super(lat, lon);
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
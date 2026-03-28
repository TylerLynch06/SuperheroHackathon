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

        // Pan and zoom event listeners
        //mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        // Add crime markers
        String[] regionBoundaries = {
            "51.517651,-0.101350",
            "51.519324,-0.079203",
            "51.509857,-0.074201",
            "51.509443,-0.103340"
        };

        CrimeAPI crimeFinder = new CrimeAPI(regionBoundaries);
        Set<Crime> crimes = crimeFinder.getCrimesByDate("2026-01");

        Set<Waypoint> crimePoints = new HashSet<>();
        for (Crime c : crimes) {
            double crimeLat = c.getLatitude();
            double crimeLongitude = c.getLongitude();
            waypointPainter(crimePoints, crimeLat, crimeLongitude, "crime");
        }

        // Add markers for the route
        Set<Waypoint> waypoints = new HashSet<>();
        Router router = new Router();
        Route route = router.createRoute(51.5074, -0.1278, 51.530811, -0.081829);
        for (double[] array : route.getCoordinates()) {
            double lat = array[0];
            double longitude = array[1];
            waypointPainter(waypoints, lat, longitude, "route");
        }

        // Combine both crime markers and route markers into the same set
        Set<Waypoint> allWaypoints = new HashSet<>();
        allWaypoints.addAll(crimePoints);
        allWaypoints.addAll(waypoints);

        // Now paint both the crime points and route points together
        WaypointPainter<Waypoint> painter = paintMarker(mapViewer);
        painter.setWaypoints(allWaypoints);
        mapViewer.setOverlayPainter(painter);

        add(mapViewer, BorderLayout.CENTER); // Add map viewer to panel
    }

    public static void waypointPainter(Set<Waypoint> waypoints, double lat, double longitude, String type) {
        Waypoint currentPoint = new CustomWaypoint(lat, longitude, type);
        waypoints.add(currentPoint);
    }

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
                    
                    if ("crime".equals(customWaypoint.getType())) {
                        // Draw crime points (blue, bigger)
                        g.setColor(Color.BLUE);
                        g.fillOval(x - 10, y - 10, 10, 10); // Bigger blue marker
                    } else if ("route".equals(customWaypoint.getType())) {
                        // Draw route points (red, smaller)
                        g.setColor(Color.RED);
                        g.fillOval(x - 5, y - 5, 5, 5); // Smaller red marker
                    }
                }
            }
        };
        return painter;
    }

    // Custom Waypoint class that holds the type (crime/route)
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
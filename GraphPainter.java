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


public class GraphPainter {

    WaypointPainter<Waypoint> painter;
    JXMapViewer mapViewer; 
    HashSet<Waypoint> routeWaypoints = new HashSet<Waypoint>();
    HashSet<Waypoint> crimeWaypoints = new HashSet<Waypoint>();
    HashSet<Waypoint> reportedWayPoints = new HashSet<Waypoint>();

    boolean doPlotCrime = true;
    boolean doPlotRoute = true;

    public GraphPainter(JXMapViewer mapViewer) {
        painter = new WaypointPainter<>();//paintMarker(mapViewer);
        this.mapViewer = mapViewer;
    }

    public void paintWaypoints() {

        // Fetch initial crime data from the API
        // Combine all waypoints (route + API crimes) and set the painter

        WaypointPainter<Waypoint> painter = paintMarker(mapViewer);
        HashSet<Waypoint> allWaypoints = new HashSet<>();

        if (doPlotCrime) {
            allWaypoints.addAll(crimeWaypoints);
        }
        if (doPlotRoute) {
            allWaypoints.addAll(routeWaypoints);
        }
        allWaypoints.addAll(reportedWayPoints);

        painter.setWaypoints(allWaypoints);
        mapViewer.setOverlayPainter(painter);
    }

    public void setRouteWaypoints() {
        Router router = new Router();
        Route route = router.createRoute(51.5074, -0.1278, 51.530811, -0.081829);
        for (double[] array : route.getCoordinates()) {
            double lat = array[0];
            double lon = array[1];
            waypointPainter(routeWaypoints, lat, lon, "route");
        }
    }


    public void setRouteWaypoints(double startLat, double startLong, double endLat, double endLong) {
        Router router = new Router();
        Route route = router.createRoute(startLat, startLong, endLat, endLong);
        for (double[] array : route.getCoordinates()) {
            double lat = array[0];
            double lon = array[1];
            waypointPainter(routeWaypoints, lat, lon, "route");
        }
    }

    // Method to add the initial crime points from the API (blue)
    public void setCrimeWaypoints() {
        String[] regionBoundaries = {
            "51.517651,-0.101350",
            "51.519324,-0.079203",
            "51.509857,-0.074201",
            "51.509443,-0.103340"
        };

        CrimeAPI crimeFinder = new CrimeAPI(regionBoundaries);
        Set<Crime> crimes = crimeFinder.getCrimesByDate("2026-01");

        for (Crime c : crimes) {
            waypointPainter(crimeWaypoints, c.getLatitude(), c.getLongitude(), "crime");
        }
    }

    public void resetWaypoints() {
        crimeWaypoints.clear();
        routeWaypoints.clear();
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
                        g.setColor(new Color(105,179,231));
                        g.fillOval(x - 10, y - 10, 10, 10); // Blue for original crimes (larger)
                    } else if ("reported".equals(customWaypoint.getType())) {
                         g.setColor(Color.BLACK);
                        g.fillOval(x - 8, y - 8, 15, 15); // Yellow for reported crimes (slightly smaller)
                    } else if ("route".equals(customWaypoint.getType())) {
                        g.setColor(new Color 	(218,41,28));
                        g.fillOval(x - 5, y - 5, 5, 5); // Red for route points (smaller)
                    }
                }
            }
        };
        return painter;
    }

    // Utility method to add waypoints
    public void waypointPainter(Set<Waypoint> wayPointSet, double lat, double longitude, String type) {
        Waypoint currentPoint = new CustomWaypoint(lat, longitude, type);
        wayPointSet.add(currentPoint);
    }

    //Adds the crime to the way point set
    public void reportCrime(Crime crime) {
        waypointPainter(reportedWayPoints, crime.getLatitude(), crime.getLongitude(), "reported");
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

    public void toggleDoPlotCrime() {
        doPlotCrime = !doPlotCrime;
        paintWaypoints();
        System.out.println("Value: "+doPlotCrime);
    }

    public void toggleDoPlotRoute() {
        doPlotRoute = !doPlotRoute;
        paintWaypoints();
    }
}

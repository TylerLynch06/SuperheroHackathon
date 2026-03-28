// Map.java
import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.*;
import javax.swing.*;
import java.util.*;
import org.jxmapviewer.input.*;

import java.awt.*;
import java.awt.geom.Point2D;


public class GraphPainter {

    WaypointPainter<Waypoint> painter;
    JXMapViewer mapViewer; 
    HashSet<Waypoint> routeWaypoints = new HashSet<Waypoint>();
    HashSet<Waypoint> crimeWaypoints = new HashSet<Waypoint>();
    HashSet<Waypoint> reportedWayPoints = new HashSet<Waypoint>();

    boolean doPlotCrime = false;
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

    public void setRouteWaypoints(double startLat, double startLong, double endLat, double endLong) {
        int subPointCount = 8;
        Router router = new Router();
        boolean first = true;
        double prevLat = 0;
        double prevLon = 0;
        Route route = router.createRoute(startLat, startLong, endLat, endLong);
        for (double[] array : route.getCoordinates()) {
            double lat = array[0];
            double lon = array[1];
            if (!first) {
                setSubpoints(lat,lon, prevLat,prevLon, subPointCount);
            }
            prevLat = lat;
            prevLon = lon;
            first = false;
            waypointPainter(routeWaypoints, lat, lon, "route");
        }
    }

    private void setSubpoints(double lat1, double lon1, double lat2, double lon2, int points) {
        if (points == 1) {
            waypointPainter(routeWaypoints, (lat1+lat2)/2, (lon1+lon2)/2, "route");
            return;
        }
        else {
            setSubpoints(lat1,lon1,(lat1+lat2)/2,(lon1+lon2)/2, points/2);
            setSubpoints((lat1+lat2)/2,(lon1+lon2)/2,lat2, lon2, points/2);
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
            waypointPainter(crimeWaypoints, c.getLatitude(), c.getLongitude(), "crime", c);
        }
    }

    public void resetWaypoints() {
        crimeWaypoints.clear();
        routeWaypoints.clear();
        paintWaypoints();
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
                         g.setColor(new Color(54, 69, 79));
                        g.fillOval(x - 8, y - 8, 15, 15); // Yellow for reported crimes (slightly smaller)
                    } else if ("route".equals(customWaypoint.getType())) {
                        g.setColor(new Color (218,41,28));
                        g.fillOval(x - 5, y - 5, 5, 5); // Red for route points (smaller)
                    }
                }
            }
        };
        return painter;
    }

    // Utility method to add waypoints
    public void waypointPainter(Set<Waypoint> wayPointSet, double lat, double lon, String type, Crime crime) {
        wayPointSet.add(new CustomWaypoint(lat, lon, type, crime));
    }

    // Overload for non-crime points (route) — no Crime object needed
    public void waypointPainter(Set<Waypoint> wayPointSet, double lat, double lon, String type) {
        waypointPainter(wayPointSet, lat, lon, type, null);
    }

    //Adds the crime to the way point set
    public void reportCrime(Crime crime) {
        waypointPainter(reportedWayPoints, crime.getLatitude(), crime.getLongitude(), "reported", crime);
    }

    // Custom Waypoint class that holds the type (crime, reported, route)
    static class CustomWaypoint extends DefaultWaypoint {
        private String type;
        private Crime crime; // nullable — route points won't have one

        public CustomWaypoint(double lat, double lon, String type, Crime crime) {
            super(lat, lon);
            this.type = type;
            this.crime = crime;
        }

        // Convenience constructor for non-crime waypoints (route, etc.)
        public CustomWaypoint(double lat, double lon, String type) {
            this(lat, lon, type, null);
        }

        public String getType() { return type; }
        public Crime getCrime() { return crime; } // null if not a crime point
    }

    public void clearRoutes() {
        routeWaypoints.clear();
        paintWaypoints();
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

    public Set<Crime> getAllCrimes() {
        Set<Crime> result = new HashSet<>();
        for (Waypoint wp : crimeWaypoints) {
            Crime c = ((CustomWaypoint) wp).getCrime();
            if (c != null) result.add(c);
        }
        for (Waypoint wp : reportedWayPoints) {
            Crime c = ((CustomWaypoint) wp).getCrime();
            if (c != null) result.add(c);
        }
        return Collections.unmodifiableSet(result);
    }   
}

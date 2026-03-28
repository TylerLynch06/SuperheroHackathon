import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.*;
import javax.swing.*;
import java.util.*;
import org.jxmapviewer.input.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.security.CodeSource;

public class Map extends JPanel {

    private JXMapViewer mapViewer;
    private Set<Waypoint> allWaypoints; // Store all crime and route points
    private GraphPainter graphPainter;
    private boolean doPlaceMarker = false; // Flag to check if the user can click on the map
    private boolean doFindRoute = false;    

    private static final int HIT_RADIUS_PX = 10;
    
    public Map() {
        setLayout(new BorderLayout());

        mapViewer = new JXMapViewer();

        // Set up the tile factory for OpenStreetMap
        TileFactoryInfo info = new TileFactoryInfo(
            1, 15, 17,
            256, true, true,
            "https://a.tiles.openrailwaymap.org/standard",
            "x", "y", "z") {
        @Override
        public String getTileUrl(int x, int y, int zoom) {
            int z = 17 - zoom;
            return "https://server.arcgisonline.com/ArcGIS/rest/services/Canvas/World_Light_Gray_Base/MapServer/tile/" + z + "/" + y + "/" + x;
        }
    };

        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Set a default address location (London)
        GeoPosition london = new GeoPosition(51.513447, -0.089056);
        mapViewer.setAddressLocation(london);
        mapViewer.setZoom(2);

        graphPainter = new GraphPainter(mapViewer); 

        // graphPainter.setDoPlotCrime(false);
        graphPainter.setCrimeWaypoints();
        graphPainter.paintWaypoints();

        add(mapViewer, BorderLayout.CENTER); // Add map viewer to panel

        // Add mouse listener for capturing clicks
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Point2D clickPoint = e.getPoint();

                Crime hitCrime = findWaypointAtPoint(clickPoint);
                if (hitCrime != null) {
                    showCrimeInfo(hitCrime);
                }

                if (doPlaceMarker) {
                    // Get the latitude and longitude of the clicked point
                    GeoPosition clickedPosition = mapViewer.convertPointToGeoPosition(clickPoint);

                    double latitude = clickedPosition.getLatitude();
                    double longitude = clickedPosition.getLongitude();

                    Crime crime = new Crime(latitude, longitude, "reported");
                    graphPainter.reportCrime(crime);
                    if (doFindRoute) {
                        routeToCrime(latitude, longitude);
                    }
                    graphPainter.paintWaypoints();
                    

                    // Disable further clicks on the map until the "Report Crime" button is pressed again
                    doPlaceMarker = false;
                    doFindRoute = false;
                } else {
                    // Inform the user to click the "Report Crime" button first
                    System.out.println("Please click the 'Report Crime' button first.");
                }
            }
        });
    }

    public Crime findWaypointAtPoint(Point2D clickPoint) {
        Set<Crime> crimes = graphPainter.getAllCrimes();
        Crime closest = null;  
        double bestDist = HIT_RADIUS_PX;

        for(Crime crime : crimes) {
           GeoPosition pos = new GeoPosition(crime.getLatitude(), crime.getLongitude());
            Point2D waypointPoint = mapViewer.convertGeoPositionToPoint(pos);

            double dist = clickPoint.distance(waypointPoint);
            if (dist < bestDist) {
                bestDist = dist;
                closest = crime;
            }
        }
        return closest;
    }

    public void showCrimeInfo(Crime crime) {
        String message = String.format(
            "Crime at (%.5f, %.5f)\nType: %s",
            crime.getLatitude(),
            crime.getLongitude(),
            crime.getType()          // adjust to your actual Crime getters
        );
        JOptionPane.showMessageDialog(this, message, "Waypoint info",
            JOptionPane.INFORMATION_MESSAGE);
    }   

    public void enableMapClicking(boolean enable) {
        this.doPlaceMarker = enable;
    }

    public void toggleFindRoute() {
        doFindRoute = !doFindRoute;
    }

    // Method to add a new reported crime (yellow)
    public void addReportedCrime(Crime reportedCrime) {
        graphPainter.reportCrime(reportedCrime);
        graphPainter.paintWaypoints();
    }

    public void toggleCrimePlot() {
        graphPainter.toggleDoPlotCrime();
    }

    public void toggleRoutePlot() {
        graphPainter.toggleDoPlotRoute();
    }

    public void routeToCrime(double startLat, double startLong) {
        String[] regionBoundaries = {
            "51.517651,-0.101350",
            "51.519324,-0.079203",
            "51.509857,-0.074201",
            "51.509443,-0.103340"
        };

        CrimeAPI crimeFinder = new CrimeAPI(regionBoundaries);
        Set<Crime> crimes = crimeFinder.getCrimesByDate("2026-01");


        for (Crime c : crimes) {
            double endLat = c.getLatitude();
            double endLong = c.getLongitude();
            graphPainter.setRouteWaypoints(startLat, startLong, endLat, endLong); 
            break;
        }
    }

    public void clearWaypoints() {
        graphPainter.resetWaypoints();
    }
}
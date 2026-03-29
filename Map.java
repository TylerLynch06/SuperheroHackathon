import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.FilteredImageSource;

public class Map extends JPanel {

    private JXMapViewer mapViewer;
    private Set<Waypoint> allWaypoints;
    private GraphPainter graphPainter;

    private boolean doPlaceMarker = false; // Flag to check if the user can click on the map
    private boolean doFindRoute = false;    

    private static final int HIT_RADIUS_PX = 10;

    private Runnable onMapClickComplete; // called after user picks a point (or cancels)

    public Map() {
        setLayout(new BorderLayout());
        mapViewer = new JXMapViewer();

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

        mapViewer.setTileFactory(new DefaultTileFactory(info));
        mapViewer.setAddressLocation(new GeoPosition(51.513447, -0.089056));
        mapViewer.setZoom(2);

        graphPainter = new GraphPainter(mapViewer);
        graphPainter.setCrimeWaypoints();
        graphPainter.paintWaypoints();

        add(mapViewer, BorderLayout.CENTER);

        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point2D clickPoint = e.getPoint();

                Crime hitCrime = findWaypointAtPoint(clickPoint);
                if (hitCrime != null) {
                    showCrimeInfo(hitCrime);
                    return;
                }

                if (doPlaceMarker) {
                    GeoPosition pos = mapViewer.convertPointToGeoPosition(clickPoint);
                    double lat = pos.getLatitude();
                    double lon = pos.getLongitude();
                    System.out.println(lat + " " + lon); 
                    Frame owner = (Frame) SwingUtilities.getWindowAncestor(Map.this);

                    if (doFindRoute) {
                        RequestAssistanceDialog dialog = new RequestAssistanceDialog(owner);
                        dialog.setVisible(true);
                        if (dialog.isSubmitted()) {
                            Crime marker = new Crime(lat, lon, "assistance");
                            graphPainter.reportCrime(marker);
                            graphPainter.paintWaypoints();
                            routeToCrime(lat, lon);
                            graphPainter.paintWaypoints();
                        }
                    } else {
                        CrimeReportDialog dialog = new CrimeReportDialog(owner);
                        dialog.setVisible(true);
                        String crimeType = dialog.getCrimeType();
                        if (crimeType != null) {
                            Crime crime = new Crime(lat, lon, crimeType);
                            graphPainter.reportCrime(crime);
                            graphPainter.paintWaypoints();
                        }
                    }

                    doPlaceMarker = false;
                    doFindRoute   = false;
                    if (onMapClickComplete != null) onMapClickComplete.run();
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
        JOptionPane.showMessageDialog(this, message, "Crime",
            JOptionPane.INFORMATION_MESSAGE);
    }   

    /** Register a callback that fires once the user has picked a point (or cancelled). */
    public void setOnMapClickComplete(Runnable callback) {
        this.onMapClickComplete = callback;
    }

    public void enableMapClicking(boolean enable) {
        this.doPlaceMarker = enable;
    }

    public void toggleFindRoute() {
        doFindRoute = !doFindRoute;
    }

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
                "51.52591394790356,-0.1302051544189453", "51.525860547398565,-0.04832267761230469",
                "51.50099581189912,-0.048193931579589844", "51.501022526737486,-0.13016223907470703"
        };
        CrimeAPI crimeFinder = new CrimeAPI(regionBoundaries);
        Set<Crime> crimes = crimeFinder.getCrimesByDate("2026-01");
        for (Crime c : crimes) {
            graphPainter.setRouteWaypoints(startLat, startLong, c.getLatitude(), c.getLongitude());
            break;
        }
    }

    public void clearWaypoints() {
        graphPainter.resetWaypoints();
    }

    public void clearRoutes() {
        graphPainter.clearRoutes();
    }
}
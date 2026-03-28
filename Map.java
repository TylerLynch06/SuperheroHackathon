import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class Map extends JPanel {

    private JXMapViewer mapViewer;
    private Set<Waypoint> allWaypoints;
    private GraphPainter graphPainter;
    private boolean doPlaceMarker = false;
    private boolean doFindRoute   = false;

    private Runnable onMapClickComplete; // called after user picks a point (or cancels)

    public Map() {
        setLayout(new BorderLayout());

        mapViewer = new JXMapViewer();

        TileFactoryInfo info = new TileFactoryInfo(
            1, 15, 17, 256, true, true,
            "https://a.tiles.openrailwaymap.org/standard", "x", "y", "z") {
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
                if (!doPlaceMarker) return;

                Point2D clickPoint = e.getPoint();
                GeoPosition pos = mapViewer.convertPointToGeoPosition(clickPoint);
                double lat = pos.getLatitude();
                double lon = pos.getLongitude();

                Frame owner = (Frame) SwingUtilities.getWindowAncestor(Map.this);

                if (doFindRoute) {
                    RequestAssistanceDialog dialog = new RequestAssistanceDialog(owner);
                    dialog.setVisible(true);

                    if (dialog.isSubmitted()) {
                        // Only add marker if user submitted
                        Crime marker = new Crime(lat, lon, "assistance");
                        graphPainter.reportCrime(marker);
                        graphPainter.paintWaypoints();

                        routeToCrime(lat, lon);
                        graphPainter.paintWaypoints();
                    }
                    // else: cancelled — do nothing, no marker added
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
        });
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
            "51.517651,-0.101350",
            "51.519324,-0.079203",
            "51.509857,-0.074201",
            "51.509443,-0.103340"
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
}
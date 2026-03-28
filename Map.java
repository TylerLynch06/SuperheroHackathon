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
    private GraphPainter graphPainter;

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

        graphPainter = new GraphPainter(mapViewer); 

        //graphPainter.setDoPlotCrime(false);

        //Paint here
        graphPainter.setCrimeWaypoints();
        graphPainter.setRouteWaypoints();
        //Reported crimes are added as waypoints in the 'addReportedCrimeMethod'
        graphPainter.paintWaypoints();

        add(mapViewer, BorderLayout.CENTER); // Add map viewer to panel
    }

    // Method to add a new reported crime (yellow)
    public void addReportedCrime(Crime reportedCrime) {
        //waypointPainter(allWaypoints, reportedCrime.getLatitude(), reportedCrime.getLongitude(), "reported");
        //WaypointPainter<Waypoint> painter = paintMarker(mapViewer);
        //painter.setWaypoints(allWaypoints);
        //mapViewer.setOverlayPainter(painter);
        //repaint(); // Refresh the map
        graphPainter.reportCrime(reportedCrime);
        graphPainter.paintWaypoints();
    }

    public void doPlotCrime(boolean value) {
        graphPainter.setDoPlotCrime(value);
    }

    public void doPlotRoute(boolean value) {
        graphPainter.setDoPlotRoute(value);
    }

}
import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.*;
import javax.swing.*;
import java.util.*;
import org.jxmapviewer.input.*;

import java.util.HashSet;
import java.util.Set;

public class BasicMap {
   
    public static void main(String[] args) {
        JFrame frame = new JFrame("Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JXMapViewer mapViewer = new JXMapViewer();

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

        GeoPosition london = new GeoPosition(51.5074, -0.1278);

        mapViewer.setAddressLocation(london);
        mapViewer.setZoom(4);

        // Pan and zoom
        mapViewer.addMouseListener(new PanMouseInputListener(mapViewer));
        mapViewer.addMouseMotionListener(new PanMouseInputListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        // Add marker
            
        Set<Waypoint> waypoints = new HashSet<>();
        Router router = new Router();
        Route route = router.createRoute(-0.1278, 51.5074, -0.081829, 51.530811);
        for(double[] array : route.getCoordinates()) {
            double lat = array[1];
            double longitude = array[0];
            System.out.println(lat);
            System.out.println(longitude);
            waypointPainter(waypoints, lat, longitude);
        }
        WaypointPainter<Waypoint> painter = new WaypointPainter<>();
        painter.setWaypoints(waypoints);
        mapViewer.setOverlayPainter(painter);

        frame.add(mapViewer);
        frame.setVisible(true);
    }

    public static void waypointPainter(Set<Waypoint> waypoints, double lat, double longitude) {
        

        Waypoint currentPoint = new DefaultWaypoint(lat, -longitude);
        waypoints.add(currentPoint);
       
    }
}

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
        mapViewer.setZoom(5);

        // Pan and zoom
        mapViewer.addMouseListener(new PanMouseInputListener(mapViewer));
        mapViewer.addMouseMotionListener(new PanMouseInputListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        frame.add(mapViewer);
        frame.setVisible(true);

    }
}

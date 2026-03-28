import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.*;
import javax.swing.*;
import java.util.*;

public class BasicMap {
    public static void main(String[] args) {
        // JXMapViewer mapViewer = new JXMapViewer();
        // TileFactoryInfo info = new OSMTileFactoryInfo();
        // DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        // mapViewer.setTileFactory(tileFactory);

        // GeoPosition london = new GeoPosition(51.5074, -0.1276);
        // mapViewer.setZoom(7);
        // mapViewer.setAddressLocation(london);

        // JFrame frame = new JFrame("Crime ");
        // frame.add(mapViewer);
        // frame.setSize(800,600);
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setVisible(true);false

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

        frame.add(mapViewer);
        frame.setVisible(true);
    }
}

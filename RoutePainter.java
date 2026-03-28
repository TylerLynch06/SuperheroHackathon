import org.jxmapviewer.*;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.painter.Painter;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;


public class RoutePainter implements Painter<JXMapViewer> {
    
    private List<GeoPosition> route;

    public RoutePainter(List<GeoPosition> route) {
        this.route = route;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int b) {
        g = (Graphics2D) g.create();
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(3));
        
        int lastX = 0, lastY = 0;
        boolean first = true;

        for (GeoPosition pos : route) {
            Point2D pt = map.getTileFactory().geoToPixel(pos, map.getZoom());
            Point2D center = map.getCenter();
            int x = (int)(pt.getX() - center.getX() + w/2);
            int y = (int)(pt.getY() - center.getY()) + b/2;
            if (!first) {
                g.drawLine(lastX, lastY, x, y);
            }
            lastX = x;
            lastY = y;
            first = false;
        }
    }
}

import org.jxmapviewer.*;
import org.jxmapviewer.viewer.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
        // Match accent colour to crime type
        Color accent;
        String type = crime.getType();
        if ("reported".equals(type) || "assistance".equals(type)) {
            accent = OptionsPanel.ACCENT_AMBER;
        } else {
            accent = OptionsPanel.ACCENT_RED;
        }

        JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Crime Info", 
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setUndecorated(true);
        dialog.setBackground(OptionsPanel.BG_PANEL);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(OptionsPanel.BG_PANEL);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 1),
            new EmptyBorder(20, 24, 20, 24)
        ));

        // Title bar
        JLabel title = new JLabel("Crime Report");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        title.setForeground(accent);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Info rows
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(OptionsPanel.BG_PANEL);
        info.add(makeInfoRow("Type",      crime.getType()));
        info.add(makeInfoRow("Latitude",  String.format("%.5f", crime.getLatitude())));
        info.add(makeInfoRow("Longitude", String.format("%.5f", crime.getLongitude())));

        // Close button — same style as OptionsPanel buttons
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setForeground(accent);
        closeBtn.setBackground(new Color(38, 38, 44));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 65), 1),
            new EmptyBorder(7, 20, 7, 20)
        ));
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnRow.setBackground(OptionsPanel.BG_PANEL);
        btnRow.setBorder(new EmptyBorder(16, 0, 0, 0));
        btnRow.add(closeBtn);

        root.add(title, BorderLayout.NORTH);
        root.add(info,  BorderLayout.CENTER);
        root.add(btnRow, BorderLayout.SOUTH);

        dialog.add(root);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

// Helper — one label/value row matching the muted panel style
    private JPanel makeInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(OptionsPanel.BG_PANEL);
        row.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel key = new JLabel(label);
        key.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        key.setForeground(OptionsPanel.TEXT_MUTED);
        key.setPreferredSize(new Dimension(70, 20));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        val.setForeground(Color.WHITE);

        row.add(key, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
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
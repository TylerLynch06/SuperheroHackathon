import java.awt.*;
import javax.swing.*;

public class BatComputer extends JFrame {

    public BatComputer() throws Exception {

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 720);
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 22));

        JPanelTitle title = new JPanelTitle(this);
        title.setPreferredSize(new Dimension(960, 52));
        makeDraggable(title);
        add(title, BorderLayout.NORTH);

        Map map = new Map();
        add(map, BorderLayout.CENTER);

        OptionsPanel optionsPanel = new OptionsPanel(map);
        // No fixed preferredSize — let it grow when input panels appear
        add(optionsPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void makeDraggable(JPanel titleBar) {
        final Point[] dragStart = {null};
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                dragStart[0] = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (dragStart[0] == null) return;
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - dragStart[0].x,
                            loc.y + e.getY() - dragStart[0].y);
            }
        });
    }
}
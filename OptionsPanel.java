import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class OptionsPanel extends JPanel {

    private ReportCrimePanel reportCrimePanel;
    private RequestAssistancePanel requestAssistancePanel;

    static final Color BG_PANEL     = new Color(22, 22, 26);
    static final Color BG_TOOLBAR   = new Color(28, 28, 32);
    static final Color BORDER_COLOR = new Color(50, 50, 58);
    static final Color TEXT_PRIMARY = new Color(220, 220, 228);
    static final Color TEXT_MUTED   = new Color(110, 110, 125);
    static final Color ACCENT_RED   = new Color(196, 43, 43);
    static final Color ACCENT_BLUE  = new Color(48, 120, 214);
    static final Color ACCENT_AMBER = new Color(190, 150, 30);

    public OptionsPanel(Map map) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        reportCrimePanel       = new ReportCrimePanel(map);
        requestAssistancePanel = new RequestAssistancePanel();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        buttonPanel.setBackground(BG_TOOLBAR);
        buttonPanel.add(createReportButton(map));
        buttonPanel.add(createViewRecentCrimesButton(map));
        buttonPanel.add(createRequestAssistanceButton(map));
        buttonPanel.add(createClearRoutesButton(map));

        add(buttonPanel);
        add(reportCrimePanel);
        add(requestAssistancePanel);
    }

    private void togglePanel(JPanel toShow, JPanel toHide) {
        boolean nowVisible = !toShow.isVisible();
        toShow.setVisible(nowVisible);
        toHide.setVisible(false);

        // Walk up to the root frame and revalidate so BorderLayout resizes
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.revalidate();
            window.repaint();
        } else {
            revalidate();
            repaint();
        }
    }

    private JButton makeButton(String label, Color accent) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(accent);
        btn.setBackground(new Color(38, 38, 44));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 65), 1),
            BorderFactory.createEmptyBorder(7, 20, 7, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(175, 38));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(accent.darker().darker());
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accent, 1),
                    BorderFactory.createEmptyBorder(7, 20, 7, 20)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(38, 38, 44));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(55, 55, 65), 1),
                    BorderFactory.createEmptyBorder(7, 20, 7, 20)
                ));
            }
        });
        return btn;
    }

    private JButton createReportButton(Map map) {
        JButton btn = makeButton("Report Crime", ACCENT_RED);
        btn.addActionListener(e -> {
            map.enableMapClicking(true);
            togglePanel(reportCrimePanel, requestAssistancePanel);
        });
        return btn;
    }

    private JButton createViewRecentCrimesButton(Map map) {
        JButton btn = makeButton("View Recent Crimes", ACCENT_BLUE);
        btn.addActionListener(e -> {
            String[] coords = {
                "51.517651,-0.101350", "51.519324,-0.079203",
                "51.509857,-0.074201", "51.509443,-0.103340"
            };
            CrimeAPI crimeAPI = new CrimeAPI(coords);
            Set<Crime> recentCrimes = crimeAPI.getCrimesByDate("2026-01");
            map.toggleCrimePlot();
        });
        return btn;
    }

    private JButton createRequestAssistanceButton(Map map) {
        JButton btn = makeButton("Request Assistance", ACCENT_AMBER);
        btn.addActionListener(e -> {
            map.enableMapClicking(true);
            map.toggleFindRoute();
            togglePanel(requestAssistancePanel, reportCrimePanel);
        });
        return btn;
    }

    private JButton createClearRoutesButton(Map map) {
        JButton btn = makeButton("Clear Routes", TEXT_MUTED);
        btn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) { window.revalidate(); window.repaint(); }
        });
        return btn;
    }
}
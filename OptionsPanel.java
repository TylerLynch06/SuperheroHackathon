import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Set;

public class OptionsPanel extends JPanel {

    static final Color BG_PANEL = new Color(22, 22, 26);
    static final Color BG_TOOLBAR = new Color(28, 28, 32);
    static final Color BORDER_COLOR = new Color(50, 50, 58);
    static final Color TEXT_MUTED = new Color(110, 110, 125);
    static final Color ACCENT_RED = new Color(196, 43, 43);
    static final Color ACCENT_BLUE = new Color(48, 120, 214);
    static final Color ACCENT_AMBER = new Color(190, 150, 30);

    private JButton reportBtn;
    private JButton assistBtn;
    private JLabel hintLabel;

    private CrimeReportHandler reportHandler;
    private AssistanceRequestHandler requestHandler;

    public OptionsPanel(Map map) {
        //initialise the handlers for crime reports and assistance requests
        reportHandler = new CrimeReportHandler();
        requestHandler = new AssistanceRequestHandler();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        buttonPanel.setBackground(BG_TOOLBAR);

        reportBtn = makeButton("Report Crime", ACCENT_RED);
        assistBtn = makeButton("Request Assistance", ACCENT_AMBER);
        JButton viewBtn = makeButton("View Recent Crimes", ACCENT_BLUE);
        JButton clearBtn = makeButton("Clear Routes", TEXT_MUTED);

        reportBtn.addActionListener(e -> {
            map.enableMapClicking(true);
            setActiveButton(reportBtn, ACCENT_RED);
            showHint("Click the map where the crime was committed");
        });

        assistBtn.addActionListener(e -> {
            map.enableMapClicking(true);
            map.toggleFindRoute();
            setActiveButton(assistBtn, ACCENT_AMBER);
            showHint("Click the map where you need assistance");
        });

        viewBtn.addActionListener(e -> {
            String[] coords = {
                "51.517651,-0.101350", "51.519324,-0.079203",
                "51.509857,-0.074201", "51.509443,-0.103340"
            };
            new CrimeAPI(coords).getCrimesByDate("2026-01");
            map.toggleCrimePlot();
        });

        clearBtn.addActionListener(e -> map.clearRoutes());

        buttonPanel.add(reportBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(assistBtn);
        buttonPanel.add(clearBtn);

        // Hint label shown below the buttons while awaiting a map click
        hintLabel = new JLabel(" ");
        hintLabel.setForeground(new Color(160, 160, 175));
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        add(buttonPanel);
        add(hintLabel);

        // When the user picks a point on the map, reset both buttons and clear the hint
        map.setOnMapClickComplete(() -> SwingUtilities.invokeLater(() -> {
            resetButton(reportBtn, ACCENT_RED);
            resetButton(assistBtn, ACCENT_AMBER);
            hintLabel.setText(" ");
        }));
    }

    private void showHint(String text) {
        hintLabel.setText(text);
    }

    private void setActiveButton(JButton btn, Color accent) {
        // Fill the button with the accent colour to show it's "armed"
        btn.setBackground(accent);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent.brighter(), 1),
            BorderFactory.createEmptyBorder(7, 20, 7, 20)
        ));
    }

    private void resetButton(JButton btn, Color accent) {
        btn.setBackground(new Color(38, 38, 44));
        btn.setForeground(accent);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 65), 1),
            BorderFactory.createEmptyBorder(7, 20, 7, 20)
        ));
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
                // Only change hover style if the button isn't in active/armed state
                if (!btn.getBackground().equals(accent)) {
                    btn.setBackground(accent.darker().darker());
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accent, 1),
                        BorderFactory.createEmptyBorder(7, 20, 7, 20)
                    ));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(accent)) {
                    btn.setBackground(new Color(38, 38, 44));
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(55, 55, 65), 1),
                        BorderFactory.createEmptyBorder(7, 20, 7, 20)
                    ));
                }
            }
        });

        return btn;
    }
}
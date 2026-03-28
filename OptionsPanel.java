import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class OptionsPanel extends JPanel {

    private ReportCrimePanel reportCrimePanel;
    private RequestAssistancePanel requestAssistancePanel;

    public OptionsPanel(Map map) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        reportCrimePanel = new ReportCrimePanel(map);
        requestAssistancePanel = new RequestAssistancePanel();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.add(createReportButton());
        buttonPanel.add(createViewRecentCrimesButton());
        buttonPanel.add(createCrimeToggleButton());
        buttonPanel.add(createRequestAssistanceButton());
        add(buttonPanel);

        add(reportCrimePanel);
        add(requestAssistancePanel);
    }

    private JButton styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));

        Color hoverColor = bgColor.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JButton createReportButton() {
        JButton reportButton = new JButton("Report Crime");
        reportButton.addActionListener(e -> {
            boolean nowVisible = !reportCrimePanel.isVisible();
            reportCrimePanel.setVisible(nowVisible);
            if (nowVisible) requestAssistancePanel.setVisible(false);
            revalidate();
            repaint();
        });
        return styleButton(reportButton, new Color(60, 60, 60), new Color(220, 50, 50));
    }

    private JButton createCrimeToggleButton() {
        JButton toggle = new JButton("Toggle Crime");
        toggle.addActionListener(e -> {

        });
        return styleButton(toggle, new Color(60, 60, 60), new Color(220, 50, 50));
    }

    private JButton createViewRecentCrimesButton() {
        JButton viewRecentCrimesButton = new JButton("View Recent Crimes");
        viewRecentCrimesButton.addActionListener(e -> {
            String[] coords = {
                "51.517651,-0.101350",
                "51.519324,-0.079203",
                "51.509857,-0.074201",
                "51.509443,-0.103340"
            };
            CrimeAPI crimeAPI = new CrimeAPI(coords);
            Set<Crime> recentCrimes = crimeAPI.getCrimesByDate("2026-01");
        });
        JButton styled = styleButton(viewRecentCrimesButton, new Color(60, 60, 60), new Color(50, 150, 220));
        styled.setFont(new Font("Arial", Font.BOLD, 10));
        return styled;
    }

    private JButton createRequestAssistanceButton() {
        JButton requestAssistanceButton = new JButton("Request Assistance");
        requestAssistanceButton.addActionListener(e -> {
            boolean nowVisible = !requestAssistancePanel.isVisible();
            requestAssistancePanel.setVisible(nowVisible);
            if (nowVisible) reportCrimePanel.setVisible(false);
            revalidate();
            repaint();
        });
        return styleButton(requestAssistanceButton, new Color(60, 60, 60), new Color(220, 200, 50));
    }
}
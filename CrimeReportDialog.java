import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CrimeReportDialog extends JDialog {

    private double lat;
    private double lon;
    private JTextField crimeTypeField = new JTextField(20);
    private String result = null; // null means cancelled

    public CrimeReportDialog(Frame owner, double lat, double lon) {
        super(owner, "Report Crime", true);
        this.lat = lat;
        this.lon = lon;
        setUndecorated(true);
        setSize(380, 220);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(28, 28, 32));
        root.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(22, 22, 26));
        titleBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 58)),
            new EmptyBorder(10, 16, 10, 12)
        ));
        JLabel title = new JLabel("Report Crime");
        title.setForeground(new Color(220, 220, 228));
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton closeBtn = new JButton("✕");
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setBackground(new Color(22, 22, 26));
        closeBtn.setForeground(new Color(120, 120, 135));
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeBtn.setBackground(new Color(196, 43, 43));
                closeBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeBtn.setBackground(new Color(22, 22, 26));
                closeBtn.setForeground(new Color(120, 120, 135));
            }
        });
        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(closeBtn, BorderLayout.EAST);

        // Body
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(new Color(28, 28, 32));
        body.setBorder(new EmptyBorder(20, 24, 16, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);

        JLabel label = new JLabel("Crime type");
        label.setForeground(new Color(140, 140, 155));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 0;
        body.add(label, gbc);

        styleTextField(crimeTypeField);
        crimeTypeField.setToolTipText("e.g. Robbery, Assault, Vandalism");
        gbc.gridy = 1;
        body.add(crimeTypeField, gbc);

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(new Color(28, 28, 32));

        JButton cancelBtn = makeButton("Cancel", new Color(55, 55, 65), new Color(160, 160, 175));
        cancelBtn.addActionListener(e -> dispose());

        JButton submitBtn = makeButton("Submit", new Color(48, 120, 214), Color.WHITE);
        submitBtn.addActionListener(e -> {
            String text = crimeTypeField.getText().trim();
            if (text.isEmpty()) {
                crimeTypeField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(196, 43, 43), 1),
                    new EmptyBorder(8, 10, 8, 10)
                ));
                crimeTypeField.setToolTipText("Please enter a crime type");
                return;
            }

            CrimeReportHandler reportHandler = new CrimeReportHandler();

            CrimeReport incident = new CrimeReport(
                lon,
                lat,
                text,
            );

            reportHandler.reportCrime(incident);

            result = text;
            dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(submitBtn);
        gbc.gridy = 2;
        gbc.insets = new Insets(6, 0, 0, 0);
        body.add(btnRow, gbc);

        root.add(titleBar, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Returns the entered crime type, or null if the dialog was cancelled. */
    public String getCrimeType() {
        return result;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(38, 38, 46));
        field.setForeground(new Color(220, 220, 228));
        field.setCaretColor(new Color(180, 180, 200));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(80, 110, 180), 1),
                    new EmptyBorder(8, 10, 8, 10)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                    new EmptyBorder(8, 10, 8, 10)
                ));
            }
        });
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
}
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BaseInputPanel extends JPanel {

    protected JTextField locationField = new JTextField(18);
    protected JLabel locationLabel = new JLabel("Location (postcode)");
    protected JButton submitButton  = new JButton("Submit");

    public BaseInputPanel() {
        setBackground(new Color(18, 18, 22));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 58)),
            new EmptyBorder(12, 18, 12, 18)
        ));
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));
        setVisible(false);

        styleButton(submitButton);
    }

    protected void styleLabel(JLabel label) {
        label.setForeground(new Color(140, 140, 155));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    protected void styleTextField(JTextField field) {
        field.setBackground(new Color(32, 32, 38));
        field.setForeground(new Color(220, 220, 228));
        field.setCaretColor(new Color(180, 180, 200));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        // Highlight border on focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(80, 110, 180), 1),
                    new EmptyBorder(6, 10, 6, 10)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                    new EmptyBorder(6, 10, 6, 10)
                ));
            }
        });
    }

    protected void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(220, 220, 228));
        button.setBackground(new Color(48, 120, 214));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(7, 18, 7, 18));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(60, 135, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(48, 120, 214));
            }
        });
    }

    protected double[] getCoordinates(String postcode) throws Exception {
        String url = "https://nominatim.openstreetmap.org/search?q="
                + postcode.replace(" ", "+") + "&format=json";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "BatComputerApp")
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        if (body.equals("[]")) throw new Exception("No results found for: " + postcode);
        double lat = Double.parseDouble(body.split("\"lat\":\"")[1].split("\"")[0]);
        double lon = Double.parseDouble(body.split("\"lon\":\"")[1].split("\"")[0]);
        return new double[]{lat, lon};
    }
}
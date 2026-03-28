import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BaseInputPanel extends JPanel {

    protected JTextField locationField = new JTextField(15);
    protected JLabel locationLabel = new JLabel("Location (PostCode):");
    protected JButton submitButton = new JButton("Submit");

    public BaseInputPanel() {
        setBackground(new Color(45, 45, 45));
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        setVisible(false);

        styleLabel(locationLabel);
        styleTextField(locationField);
        styleButton(submitButton);
    }

    protected void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 12));
    }

    protected void styleTextField(JTextField field) {
        field.setBackground(new Color(60, 60, 60));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    protected void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(80, 80, 80));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 30));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = button.getBackground();
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(original.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(original);
            }
        });
    }

    protected double[] getCoordinates(String postcode) throws Exception {
        String url = "https://nominatim.openstreetmap.org/search?q="
                + postcode.replace(" ", "+")
                + "&format=json";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "YourAppName")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        if (body.equals("[]")) {
            throw new Exception("No results found for postcode: " + postcode);
        }

        double lat = Double.parseDouble(body.split("\"lat\":\"")[1].split("\"")[0]);
        double lon = Double.parseDouble(body.split("\"lon\":\"")[1].split("\"")[0]);

        return new double[]{lat, lon};
    }
}
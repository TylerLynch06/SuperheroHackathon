import javax.swing.*;
import java.awt.FlowLayout;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OptionsPanel extends JPanel {

    private JTextField locationField = new JTextField(15);
    private JTextField descriptionField = new JTextField(15);
    private JLabel locationLabel = new JLabel("Location (PostCode):");
    private JLabel descriptionLabel = new JLabel("Crime Type:");
    private JButton submitButton = new JButton("Submit");

    public OptionsPanel() {
        setLayout(new FlowLayout());

        locationLabel.setVisible(false);
        locationField.setVisible(false);
        descriptionLabel.setVisible(false);
        descriptionField.setVisible(false);
        submitButton.setVisible(false);

        add(createReportButton());
        add(locationLabel);
        add(locationField);
        add(descriptionLabel);
        add(descriptionField);
        add(submitButton);

        setupSubmitButton();
    }

    private JButton createReportButton() {
        JButton reportButton = new JButton("Report Crime");
        reportButton.addActionListener(e -> {
            locationLabel.setVisible(true);
            locationField.setVisible(true);
            descriptionLabel.setVisible(true);
            descriptionField.setVisible(true);
            submitButton.setVisible(true);
            revalidate();
            repaint();
        });
        return reportButton;
    }

    private void setupSubmitButton() {
        submitButton.addActionListener(e -> {
            String location = locationField.getText();
            String crimeType = descriptionField.getText();

            try {
                double[] coords = getCoordinates(location);
                double lat = coords[0];
                double lon = coords[1];

                Crime reportedCrime = new Crime(lat, lon, crimeType);

                System.out.println("Latitude: " + lat);
                System.out.println("Longitude: " + lon);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Could not find coordinates for that postcode.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private double[] getCoordinates(String postcode) throws Exception {
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
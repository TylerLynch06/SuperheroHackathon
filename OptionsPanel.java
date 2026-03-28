import javax.swing.*;
import java.awt.*;
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
    private JPanel inputPanel = new JPanel(new FlowLayout());

    public OptionsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createReportButton());
        buttonPanel.add(createViewRecentCrimesButton());
        buttonPanel.add(createRequestAssistanceButton());
        add(buttonPanel);

        inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(locationLabel);
        inputPanel.add(locationField);
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionField);
        inputPanel.add(submitButton);
        inputPanel.setVisible(false);
        add(inputPanel);

        setupSubmitButton();
    }

    private JButton createReportButton() {
        JButton reportButton = new JButton("Report Crime");
        reportButton.addActionListener(e -> {
            inputPanel.setVisible(!inputPanel.isVisible());
            revalidate();
            repaint();
        });
        return reportButton;
    }

    private JButton createViewRecentCrimesButton() {
        JButton viewRecentCrimesButton = new JButton("View Recent Crimes");
        viewRecentCrimesButton.addActionListener(e -> {
            // get recent crimes and show them on the map
        });
        return viewRecentCrimesButton;
    }

    private JButton createRequestAssistanceButton() {
        JButton requestAssistanceButton = new JButton("Request Assistance");
        requestAssistanceButton.addActionListener(e -> {
            // send request signal to other users
        });
        return requestAssistanceButton;
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
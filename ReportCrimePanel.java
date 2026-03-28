import javax.swing.*;
import java.awt.*;

public class ReportCrimePanel extends BaseInputPanel {

    private JTextField crimeTypeField = new JTextField(15);
    private JLabel crimeTypeLabel = new JLabel("Crime Type:");
    private Map map;

    public ReportCrimePanel(Map map) {
        this.map = map;

        add(locationLabel);
        add(locationField);
        add(crimeTypeLabel);
        add(crimeTypeField);
        add(submitButton);

        styleLabel(crimeTypeLabel);
        styleTextField(crimeTypeField);
        setupSubmitButton();
    }

    private void setupSubmitButton() {
        submitButton.addActionListener(e -> {
            String location = locationField.getText();
            String crimeType = crimeTypeField.getText();
            try {
                double[] coords = getCoordinates(location);
                Crime reportedCrime = new Crime(coords[0], coords[1], crimeType);
                System.out.println(coords[0]);
                map.addReportedCrime(reportedCrime);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Could not find coordinates for that postcode.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
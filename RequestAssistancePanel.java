import javax.swing.*;
import java.awt.*;

public class RequestAssistancePanel extends BaseInputPanel {

    private JTextField reasonField = new JTextField(15);
    private JLabel reasonLabel = new JLabel("Reason:");

    public RequestAssistancePanel() {
        add(locationLabel);
        add(locationField);
        add(reasonLabel);
        add(reasonField);
        add(submitButton);

        styleLabel(reasonLabel);
        styleTextField(reasonField);
        setupSubmitButton();
    }

    private void setupSubmitButton() {
        submitButton.addActionListener(e -> {
            String location = locationField.getText();
            String reason = reasonField.getText();
            try {
                double[] coords = getCoordinates(location);
                System.out.println("Assistance requested - Lat: " + coords[0] + " Lon: " + coords[1] + " Reason: " + reason);
                // send over network here
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Could not find coordinates for that postcode.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
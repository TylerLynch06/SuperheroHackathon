import javax.swing.*;
import java.awt.*;

public class ToggleCrimePlotPanel extends BaseInputPanel {

    private JTextField crimeTypeField = new JTextField(15);
    private JLabel crimeTypeLabel = new JLabel("Crime Type:");
    private Map map;

    public ToggleCrimePlotPanel(Map map) {
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
            System.out.println("Value:");
            map.toggleCrimePlot();
        });
    }
}
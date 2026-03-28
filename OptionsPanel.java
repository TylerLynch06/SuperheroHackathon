import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

public class OptionsPanel extends JPanel {

    public OptionsPanel() {
        setLayout(new FlowLayout());

        add(createSolveButton());
        add(createCheckButton());
        add(createFileChooser());
        add(createClearButton());
    }

    private JButton createSolveButton() {
        JButton solveButton = new JButton("Solve");
        return solveButton;
    }

    private JButton createCheckButton() {
        JButton checkButton = new JButton("Check Solution");
        return checkButton;
    }

    private JButton createFileChooser() {
        JButton fileChooserButton = new JButton("Load Puzzle");
        return fileChooserButton;
    }

    private JButton createClearButton() {
        JButton clearButton = new JButton("Clear");
        return clearButton;
    }
}

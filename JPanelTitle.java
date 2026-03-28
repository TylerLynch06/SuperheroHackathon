import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class JPanelTitle extends JPanel {
    public JPanelTitle() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(45, 45, 45));
        this.setPreferredSize(new Dimension(750, 50));

        JLabel titleLabel = new JLabel("Bat Computer");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));

        JButton exitButton = new JButton("X");
        exitButton.setFocusPainted(false);
        exitButton.setBackground(new Color(200, 50, 50));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        exitButton.addActionListener(e -> System.exit(0));

        this.add(titleLabel, BorderLayout.WEST);
        this.add(exitButton, BorderLayout.EAST);
    }
}

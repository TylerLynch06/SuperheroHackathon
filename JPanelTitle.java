import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class JPanelTitle extends JPanel {

    public JPanelTitle(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(28, 28, 32));
        setPreferredSize(new Dimension(750, 52));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 58)));

        //testing with image instead of title
        ImageIcon bcImage = new ImageIcon("BatComputer-Padded-Height30.png");

        //JLabel titleLabel = new JLabel("Bat Computer");
        //titleLabel.setForeground(new Color(230, 230, 235));
        //titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        //titleLabel.setBorder(new EmptyBorder(0, 18, 0, 0));

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightButtons.setBackground(new Color(28, 28, 32));
        rightButtons.add(createFullscreenButton(frame));
        rightButtons.add(createExitButton());

        add(new JLabel(bcImage), BorderLayout.WEST);
        //add(titleLabel, BorderLayout.WEST);
        add(rightButtons, BorderLayout.EAST);
    }

    private JButton createFullscreenButton(JFrame frame) {
        JButton btn = new JButton("□");
        styleWindowButton(btn);
        btn.addActionListener(e -> {
            GraphicsDevice gd = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();
            if (gd.getFullScreenWindow() == null) {
                gd.setFullScreenWindow(frame);
            } else {
                gd.setFullScreenWindow(null);
            }
        });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(50, 50, 62));
                btn.setForeground(new Color(220, 220, 228));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(28, 28, 32));
                btn.setForeground(new Color(140, 140, 150));
            }
        });
        return btn;
    }

    private JButton createExitButton() {
        JButton btn = new JButton("✕");
        styleWindowButton(btn);
        btn.addActionListener(e -> System.exit(0));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(196, 43, 43));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(28, 28, 32));
                btn.setForeground(new Color(140, 140, 150));
            }
        });
        return btn;
    }

    private void styleWindowButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(28, 28, 32));
        btn.setForeground(new Color(140, 140, 150));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(52, 52));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
import java.awt.*;
import javax.swing.*;

public class BatComputer extends JFrame {

    public BatComputer() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 900);
        this.setUndecorated(true);
        
        this.setLayout(new BorderLayout());

        JPanelTitle title = new JPanelTitle();
        title.setPreferredSize(new Dimension(900, 50)); 
        this.add(title, BorderLayout.NORTH);

        MapPanel map = new MapPanel();
        this.add(map, BorderLayout.CENTER);

        OptionsPanel optionsPanel = new OptionsPanel();
        optionsPanel.setPreferredSize(new Dimension(900, 150));
        this.add(optionsPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }
}

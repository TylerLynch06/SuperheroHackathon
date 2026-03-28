import java.awt.*;
import javax.swing.*;

public class MapPanel extends JPanel {
    public MapPanel() {
        this.setBackground(new Color(60, 63, 65)); 
        this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        Map map = new Map();
    }
}

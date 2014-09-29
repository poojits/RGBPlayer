import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by poojit on 9/28/14.
 */
public class GUI {
    private JFrame window;
    private JLabel label;

    public GUI() {
        this.window = new JFrame();
        this.label = new JLabel();
        window.getContentPane().add(label, BorderLayout.CENTER);
        window.pack();
        window.setVisible(true);
    }

    public void imshow(Image img) {
        this.label.setIcon(new ImageIcon(img.getImg()));
        window.pack();
    }

    public void imshow(BufferedImage img) {
        this.label.setIcon(new ImageIcon(img));
        window.pack();
    }
}

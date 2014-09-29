package cs576;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class App {
    public static void main(String[] args) {
        String filename = args[0];
        /*int width = Integer.parseInt(args[1]);
        int height = Integer.parseInt(args[2]);*/
        GUI gui = new GUI();
        /*Image img = new Image(filename, width, height, BufferedImage.TYPE_INT_RGB);
        gui.imshow(img);*/

        VideoCapture cap = new VideoCapture(filename);
        Image frame = new Image(352, 288, BufferedImage.TYPE_INT_RGB);
        cap.open();
        if(!cap.isOpened()) {
            System.err.println("Cannot start VideoCapture: "+filename);
            System.exit(1);
        }
        while (cap.read(frame)) {
            gui.imshow(frame);
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Video finished playing");
    }
}

package cs576;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class App {
    public static void main(String[] args) {
        String filename = args[0];
        //int width = Integer.parseInt(args[1]);
        //int height = Integer.parseInt(args[2]);

        //Image img = new Image(filename, width, height, BufferedImage.TYPE_INT_RGB);
        //img.imshow();

        GUI gui = new GUI();
        VideoCapture cap = new VideoCapture(filename);
        cap.open();
        int framesRead = 0;
        if(cap.isOpened()) {
            int totalFrames = cap.getNumFrames();
            while (framesRead < totalFrames) {
                Image frame = cap.read();
                gui.imshow(frame);
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                framesRead++;
            }
        }
    }
}

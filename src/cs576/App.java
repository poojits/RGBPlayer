package cs576;

import java.awt.image.BufferedImage;


public class App {
    public static void main(String[] args) {
        String filename = args[0];
        int width = Integer.parseInt(args[1]);
        int height = Integer.parseInt(args[2]);

        Image img = new Image(filename, width, height, BufferedImage.TYPE_INT_RGB);
        img.imshow();
    }
}

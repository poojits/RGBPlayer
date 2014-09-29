package cs576;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class RGBPlayer {
    private static String filename = "";
    private static float scaleW = 1.0f;
    private static float scaleH = 1.0f;
    private static float fps = 30.0f;
    private static int antiAliasing = 0;
    private static int analysis = 0;

    public static void main(String[] args) {
        parseParameters(args);
        GUI gui = new GUI();
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
    private static void parseParameters(String[] args){
        String menu = "Usage: RGBPlayer <filename> <scaleW> <scaleH> <fps> <anti-aliasing> <analysis>\n"
                +"   <filename>\t\tPath to the *.rgb video file\n"
                +"   <scaleW>\t\tScaling factor for width\n"
                +"   <scaleH>\t\tScaling factor for height\n"
                +"   <fps>\t\tOutput frame rate of the video\n"
                +"   <anti-aliasing>\tSwitch to turn anti-aliasing on or off (default 0)\n"
                +"   <analysis>\t\tAnalysis and Extra credit (default 0)";
        if(args.length<4 || args.length>6) {
            System.out.println(menu);
            System.exit(1);
        }
        filename = args[0];
        scaleW = Float.parseFloat(args[1]);
        scaleH = Float.parseFloat(args[2]);
        fps = Float.parseFloat(args[3]);
        if(args.length>4) {
            antiAliasing = Integer.parseInt(args[4]);
        }
        if(args.length==6) {
            analysis = Integer.parseInt(args[5]);
        }
    }
}

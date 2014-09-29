

import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
        int original_width = 352;
        int original_height = 288;
        int modified_width = original_width;
        int modified_height = original_height;
        boolean isScaled = false;
        if (scaleW != 1.0 || scaleH != 1.0) {
            modified_width = (int) Math.floor(scaleW * original_width);
            modified_height = (int) Math.floor(scaleH * original_height);
            isScaled = true;
        }
        VideoCapture cap = new VideoCapture(filename, original_width, original_height);
        Image original_frame = new Image(original_width, original_height, BufferedImage.TYPE_INT_RGB);
        Image modified_frame = new Image(modified_width, modified_height, BufferedImage.TYPE_INT_RGB);

        cap.open();
        if (!cap.isOpened()) {
            System.err.println("Cannot start VideoCapture: " + filename);
            System.exit(1);
        }
        ArrayList<BufferedImage> video = new ArrayList<BufferedImage>();
        int frameCount = 1;
        int totalFrames = cap.getNumFrames();
        while (cap.read(original_frame)) {
            if (isScaled) {
                Image.resize(original_frame, modified_frame, scaleW, scaleH, antiAliasing, analysis);
                video.add(modified_frame.clone());
            } else {
                video.add(original_frame.clone());
            }
            System.out.println("Processing (" + frameCount + "/" + totalFrames + ")...Please Wait");
            frameCount++;
        }
        for (int i = 0; i < video.size(); i++) {
            gui.imshow(video.get(i));
            try {
                long sleep = (long) Math.floor((double) 1000 / fps);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Video finished playing");
    }

    private static void parseParameters(String[] args) {
        String menu = "Usage: java -Xmx1024m RGBPlayer <filename> <scaleW> <scaleH> <fps> <anti-aliasing> <analysis>\n"
                + "   <filename>\t\tPath to the *.rgb video file\n"
                + "   <scaleW>\t\tScaling factor for width\n"
                + "   <scaleH>\t\tScaling factor for height\n"
                + "   <fps>\t\tOutput frame rate of the video\n"
                + "   <anti-aliasing>\tSwitch to turn anti-aliasing on or off (default 0)\n"
                + "   <analysis>\t\tAnalysis and Extra credit (default 0)";
        if (args.length < 4 || args.length > 6) {
            System.out.println(menu);
            System.exit(1);
        }
        filename = args[0];
        scaleW = Float.parseFloat(args[1]);
        scaleH = Float.parseFloat(args[2]);
        fps = Float.parseFloat(args[3]);
        if (args.length > 4) {
            antiAliasing = Integer.parseInt(args[4]);
        }
        if (args.length == 6) {
            analysis = Integer.parseInt(args[5]);
        }
    }
}

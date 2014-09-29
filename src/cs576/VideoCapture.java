package cs576;

import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by poojit on 9/28/14.
 */
public class VideoCapture {
    private int height;
    private int width;
    private int colorModel;
    private float fps;
    private int length;
    private int numFrames;
    private int oneFrameDataLength;
    private InputStream is;
    private int framesRead = 0;
    private String filename;
    private boolean isOpened = false;

    public VideoCapture(String filename) {
        this.width = 352;
        this.height = 288;
        this.fps = 30;
        this.colorModel = BufferedImage.TYPE_INT_RGB;
        this.filename = filename;
    }

    public Image read() {
        int offset = 0;
        int numRead = 0;
        byte[] data = new byte[this.oneFrameDataLength];
        try {
            while (offset < data.length &&
                    (numRead = this.is.read(data, offset, data.length - offset)) >= 0) {
                offset += numRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image frame = new Image(this.width,this.height,BufferedImage.TYPE_INT_RGB);
        frame.setBufferedImage(data);
        return frame;
    }

    public void open() {
        File file = new File(this.filename);
        try {
            this.is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.length = (int) file.length();
        this.oneFrameDataLength = this.height*this.width*3;
        this.numFrames = this.length/oneFrameDataLength;
        isOpened = true;
    }

    public boolean isOpened() {
        return this.isOpened;
    }

    public int getNumFrames() {
        return numFrames;
    }


}

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
    private byte[] frameData;
    private String filename;
    private boolean isOpened = false;

    public VideoCapture(String filename) {
        this.width = 352;
        this.height = 288;
        this.fps = 30;
        this.colorModel = BufferedImage.TYPE_INT_RGB;
        this.filename = filename;
    }

    public boolean grabFrame() {
        int offset = 0;
        int numRead = 0;
        this.frameData = new byte[this.oneFrameDataLength];
        try {
            while (offset < this.frameData.length &&
                    (numRead = this.is.read(this.frameData, offset, this.frameData.length - offset)) >= 0) {
                offset += numRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if(numRead>0)
            return true;
        else
            return false;
    }

    public boolean retrieveFrame(Image frame) {
        frame.setBufferedImage(this.frameData);
        return true;
    }

    public boolean read(Image img) {
        if(grabFrame()) {
            return retrieveFrame(img);
        }
        else {
            return false;
        }
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

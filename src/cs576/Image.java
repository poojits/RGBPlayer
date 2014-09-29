package cs576;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by poojit on 9/28/14.
 */
public class Image {
    private int height;
    private int width;
    private int colorModel;
    private byte[] a;
    private byte[] r;
    private byte[] g;
    private byte[] b;
    private int length;
    private byte[] data;
    private BufferedImage img;

    public Image(String filename, int width, int height, int colorModel) {
        this.width = width;
        this.height = height;
        this.colorModel = colorModel;
        setBufferedImage(filename);
    }

    public Image(int width, int height, int colorModel) {
        this.width = width;
        this.height = height;
        this.colorModel = colorModel;
    }

    private void getDataFromFile(String filename) {
        try {
            File file = new File(filename);
            InputStream is = new FileInputStream(file);

            this.length = (int) file.length();
            this.data = new byte[this.length];

            int offset = 0;
            int numRead = 0;
            while (offset < this.length && (numRead = is.read(this.data, offset, this.length - offset)) >= 0) {
                offset += numRead;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBufferedImage(byte[] data) {
        this.img = new BufferedImage(this.width, this.height, this.colorModel);
        this.data = data;
        int idx = 0;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                byte a = 0;
                byte r = this.data[idx];
                byte g = this.data[idx + this.height * this.width];
                byte b = this.data[idx + this.height * (this.width << 1)];

                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                this.img.setRGB(x, y, pix);
                idx++;
            }
        }
    }

    public void setBufferedImage(String filename) {
        this.img = new BufferedImage(this.width, this.height, this.colorModel);
        getDataFromFile(filename);
        setBufferedImage(this.data);
    }

    public BufferedImage getImg() {
        return this.img;
    }
}

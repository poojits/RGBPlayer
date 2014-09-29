package cs576;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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
        this.data = new byte[this.width * this.height * 3];
        this.img = new BufferedImage(this.width, this.height, this.colorModel);
        setBufferedImage(filename);
    }

    public Image(int width, int height, int colorModel) {
        this.width = width;
        this.height = height;
        this.colorModel = colorModel;
        this.data = new byte[this.width * this.height * 3];
        this.img = new BufferedImage(this.width, this.height, this.colorModel);
    }

    public static void resize(Image src, Image dst, float scaleW, float scaleH, int anti_aliasing) {
        if (anti_aliasing == 0) {
            for (int y = 0; y < dst.height(); y++) {
                for (int x = 0; x < dst.width(); x++) {
                    int[] rgb = src.getRGB((int) Math.floor(x / scaleW), (int) Math.floor(y / scaleH));
                    int pix = 0xff000000 | ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | (rgb[2] & 0xff);
                    dst.setRGB(x, y, pix);
                }
            }
        } else {
            for (int y = 0; y < dst.height(); y++) {
                for (int x = 0; x < dst.width(); x++) {
                    int src_X = (int) Math.floor(x / scaleW);
                    int src_Y = (int) Math.floor(y / scaleH);
                    int[] averageXY = src.getAverageRGB(src_X, src_Y);
                    int pix = 0xff000000 | ((averageXY[0] & 0xff) << 16) | ((averageXY[1] & 0xff) << 8) | (averageXY[2] & 0xff);
                    dst.setRGB(x, y, pix);
                }
            }
        }
    }

    private int[] getAverageRGB(int x, int y) {
        float[] rgb = new float[3];
        rgb[0] = 0;
        rgb[1] = 0;
        rgb[2] = 0;
        if (x > 0 && y > 0 && x < this.width - 1 && y < this.height - 1) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 9.0f;
                    }
                }
            }
        } else if (x == 0 && y == 0) {
            for (int i = 0; i <= 1; i++) {
                for (int j = 0; j <= 1; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 4.0f;
                    }
                }
            }
        } else if (x == 0 && y == this.height - 1) {
            for (int i = 0; i <= 1; i++) {
                for (int j = y - 1; j <= y; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 4.0f;
                    }
                }
            }
        } else if (x == this.width - 1 && y == 0) {
            for (int i = x - 1; i <= x; i++) {
                for (int j = 0; j <= 1; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 4.0f;
                    }
                }
            }
        } else if (x == this.width - 1 && y == this.height - 1) {
            for (int i = x - 1; i <= x; i++) {
                for (int j = y - 1; j <= y; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 4.0f;
                    }
                }
            }
        } else if (x == 0) {
            for (int i = 0; i <= 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 6.0f;
                    }
                }
            }
        } else if (y == 0) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = 0; j <= 1; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 6.0f;
                    }
                }
            }
        } else if (x == this.width - 1) {
            for (int i = x - 1; i <= x; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 6.0f;
                    }
                }
            }
        } else if (y == this.height - 1) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y; j++) {
                    int[] points = this.getRGB(i, j);
                    for (int c = 0; c < 3; c++) {
                        rgb[c] += points[c] / 6.0f;
                    }
                }
            }
        }
        int[] rgbInt = new int[3];
        rgbInt[0] = (int) Math.floor(rgb[0]);
        rgbInt[1] = (int) Math.floor(rgb[1]);
        rgbInt[2] = (int) Math.floor(rgb[2]);
        return rgbInt;
    }

    public BufferedImage clone() {
        WritableRaster raster = this.img.copyData(null);
        return new BufferedImage(this.img.getColorModel(), raster, false, null);
    }

    private void getDataFromFile(String filename) {
        try {
            File file = new File(filename);
            InputStream is = new FileInputStream(file);

            this.length = (int) file.length();

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
        getDataFromFile(filename);
        setBufferedImage(this.data);
    }

    public BufferedImage getImg() {
        return this.img;
    }

    public int height() {
        return this.height;
    }

    public int width() {
        return this.width;
    }

    public void setRGB(int x, int y, int pix) {
        this.img.setRGB(x, y, pix);
    }

    public int[] getRGB(int x, int y) {
        int xx = x;
        int yy = y;
        if (x >= this.width) xx--;
        if (y >= this.height) yy--;
        int[] rgb = new int[3];
        int pix = this.img.getRGB(xx, yy);
        rgb[0] = (pix >> 16) & 0xff;
        rgb[1] = (pix >> 8) & 0xff;
        rgb[2] = (pix & 0xff);
        return rgb;
    }
}

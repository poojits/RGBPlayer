/**
 * Created by poojit on 9/29/14.
 */

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

public class ImageSeamOperations {

    // LAPLACIAN KERNEL
    //    private float[] kernel = {
    //            0, 1, 0,
    //            1, -4, 1,
    //            0, 1, 0};
    // SOEBEL KERNEL (COMBINED)
    private final float[] soebel1 = {1, 2, 1, 0, 0, 0, -1, -2, -1};
    private final ConvolveOp ops1 = new ConvolveOp(new Kernel(3, 3, soebel1));
    private final float[] soebel2 = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
    private final ConvolveOp ops2 = new ConvolveOp(new Kernel(3, 3, soebel2));
    private final ColorConvertOp greyscale;
    int debugint = 0;
    boolean debug = false;
    private BufferedImage img;
    private BufferedImage energyAddon = null;
    private BufferedImage energyImg = null;
    private float[][][] seamTable = null;

    public ImageSeamOperations(BufferedImage img, BufferedImage energyAddon) {
        this.img = img;
        this.energyAddon = energyAddon;
        greyscale = new ColorConvertOp(img.getColorModel().getColorSpace(), ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    }

    public ImageSeamOperations(BufferedImage img) {
        this.img = img;
        greyscale = new ColorConvertOp(img.getColorModel().getColorSpace(), ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    }

    private static float[][][] createHorizontalSeamTable(BufferedImage energyImage, float[][][] seamTable) {
        float[] pixelCol = new float[energyImage.getHeight()];
        float[][][] result;
        if (seamTable == null) result = new float[energyImage.getWidth()][energyImage.getHeight()][2];
        else result = seamTable;
        int h = energyImage.getHeight();
        int w = energyImage.getWidth();
        energyImage.getRaster().getPixels(0, 0, 1, h, pixelCol);
        // init first col of pixels
        for (int y = 0; y < h; y++) {
            result[0][y][0] = pixelCol[y];
        }
        // now for the following rows find the next component in the seam
        float right;
        float left;
        float min;
        float middle;
        float currentEnergy;
        int nextStep;
        for (int x = 1; x < w; x++) {
            energyImage.getRaster().getPixels(x, 0, 1, h, pixelCol);
            for (int y = 0; y < h; y++) {
                // find 'next in path'
                middle = result[x - 1][y][0];
                currentEnergy = pixelCol[y];
                nextStep = 0;
                if (y > 1 && y < h - 2) {
                    left = result[x - 1][y - 1][0];
                    right = result[x - 1][y + 1][0];
                    nextStep = 0;
                    if (right < middle || left < middle) {
                        if (right <= left) {
                            min = right;
                            nextStep = 1;
                        } else {
                            min = left;
                            nextStep = -1;
                        }
                    } else {
                        min = middle;
                    }
                    currentEnergy = currentEnergy + min;
                } else if (y > 1) {
                    // left border of the image, no left possible
                    right = result[x - 1][y - 1][0];
                    if (right < middle) {
                        min = right;
                        nextStep = -1;
                    } else {
                        min = middle;
                        nextStep = 0;
                    }
                    currentEnergy = currentEnergy + min;
                } else if (y < h - 2) {
                    // right border of the image, no right possible
                    left = result[x - 1][y + 1][0];
                    if (left < middle) {
                        min = left;
                        nextStep = 1;
                    } else {
                        min = middle;
                        nextStep = 0;
                    }
                    currentEnergy = currentEnergy + min;
                }
                result[x][y][0] = currentEnergy;
                result[x][y][1] = nextStep;
            }
        }
        return result;
    }

    /**
     * Generates a table containing all possible seams using dynamic programming.
     * To find a seam start at the last row and use backtracking: y -> y-1, x -> x+ nextX
     *
     * @param energyImage the greyscale image containing the energy per pixel.
     * @param seamTable   used for performance: no need to generate a whole new table each time.
     * @return the seam table of the dynamic programming continaing [x][y][0:energy, 1:nextX]
     */
    private static float[][][] createVerticalSeamTable(BufferedImage energyImage, float[][][] seamTable) {
//        float pixel[] = new float[1];
        float[] pixelRow = new float[energyImage.getWidth()];
        float[][][] result;
        if (seamTable == null)
            result = new float[energyImage.getWidth()][energyImage.getHeight()][2];
        else
            result = seamTable;
        int h = energyImage.getHeight();
        int w = energyImage.getWidth();
        // init first row of pixels
        energyImage.getRaster().getPixels(0, 0, energyImage.getWidth(), 1, pixelRow);
        for (int x = 0; x < w; x++) {
            result[x][0][0] = pixelRow[x];
        }
        // now for the following rows find the next component in the seam
        float right;
        float left;
        float min;
        float middle;
        float currentEnergy;
        int nextStep;
        for (int y = 1; y < h; y++) {
            // get row first:
            energyImage.getRaster().getPixels(0, y, energyImage.getWidth(), 1, pixelRow);
            for (int x = 0; x < w; x++) {
                // find 'next in path'
                middle = result[x][y - 1][0];
                currentEnergy = pixelRow[x];
                //currentEnergy = energyImage.getRaster().getPixel(x, y, pixel)[0];
                nextStep = 0;
                if (x > 1 && x < w - 2) {
                    left = result[x - 1][y - 1][0];
                    right = result[x + 1][y - 1][0];
                    nextStep = 0;
                    if (right < middle || left < middle) {
                        if (right < left) {
                            min = right;
                            nextStep = 1;
                        } else {
                            min = left;
                            nextStep = -1;
                        }
                    } else {
                        min = middle;
                    }
                    currentEnergy = currentEnergy + min;
                } else if (x > 1) {
                    // right border of the image, no left possible
                    left = result[x - 1][y - 1][0];
                    if (left < middle) {
                        min = left;
                        nextStep = -1;
                    } else {
                        min = middle;
                        nextStep = 0;
                    }
                    currentEnergy = currentEnergy + min;
                } else if (x < w - 2) {
                    // left border of the image, no right possible
                    right = result[x + 1][y - 1][0];
                    if (right < middle) {
                        min = right;
                        nextStep = 1;
                    } else {
                        min = middle;
                        nextStep = 0;
                    }
                    currentEnergy = currentEnergy + min;
                }
                result[x][y][0] = currentEnergy;
                result[x][y][1] = nextStep;
            }
        }
        return result;
    }

    private VerticalSeam findOptimalVerticalSeam() {
        if (energyImg == null) {
            energyImg = generateEnergyImage();
        }
        float[][][] table = createVerticalSeamTable(energyImg, seamTable);

        // find the optimal seam.
        float energy = Float.MAX_VALUE;
        int seamstart = 0;
        int height = energyImg.getHeight();
        for (int i = 1; i < table.length - 1; i++) {
            if (table[i][height - 1][0] < energy) {
                energy = table[i][height - 1][0];
                seamstart = i;
            }
        }
        return new VerticalSeam(energy, seamstart, table, energyImg.getWidth(), energyImg.getHeight());
    }

    private HorizontalSeam findOptimalHorizontalSeam() {
        if (energyImg == null) energyImg = generateEnergyImage();
        float[][][] table = createHorizontalSeamTable(energyImg, seamTable);

        // find the optimal seam.
        float energy = Float.MAX_VALUE;
        int seamstart = 0;
        int width = energyImg.getWidth();
        // TODO: find random lowest! not only from the left hand side!
        for (int i = 1; i < table[0].length - 1; i++) {
            if (table[width - 1][i][0] < energy) {
                energy = table[width - 1][i][0];
                seamstart = i;
            }
        }
        return new HorizontalSeam(energy, seamstart, table, energyImg.getWidth(), energyImg.getHeight());
    }

    public void carveVerticalSeam() {
        VerticalSeam s = findOptimalVerticalSeam();
        int height = img.getHeight();
        // lookup table for the seam coordinates: y -> x as result of the backtracking.
        int[] seamLookup = new int[height];
        // backtracking:
        int nextX = s.startX;
        int minX = img.getWidth();
        int maxX = 0;
        seamLookup[height - 1] = nextX;
        seamLookup[height - 2] = nextX;
        for (int y = height - 2; y >= 1; y--) {
            nextX += (int) s.table[nextX][y][1];
            seamLookup[y - 1] = nextX;
            minX = Math.min(nextX, minX);
            maxX = Math.max(nextX, maxX);
        }
        WritableRaster raster = img.getRaster();
        // leave everything left of the seam untouched:
        Graphics2D g2 = img.createGraphics();
        // copy pixels:
        int[] pixel = new int[3];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = seamLookup[y]; x <= maxX; x++) {
                raster.getPixel(x + 1, y, pixel);
                raster.setPixel(x, y, pixel);
            }
        }
        // shift image data to the left:
        if (maxX < img.getWidth() - 1) {
            g2.drawImage(img, maxX + 1, 0, img.getWidth() - 1, img.getHeight(), maxX + 2, 0, img.getWidth(), img.getHeight(), null);
        }
        // crop image one col:
        img = img.getSubimage(0, 0, img.getWidth() - 1, img.getHeight());

        // carve seam from energy image:
        carveGradientImageVertical(seamLookup, maxX, raster);


        if (energyAddon != null) {
            pixel = new int[4];
            raster = energyAddon.getRaster();
            g2 = energyAddon.createGraphics();
            for (int y = 0; y < energyAddon.getHeight(); y++) {
                for (int x = seamLookup[y]; x <= maxX; x++) {
                    raster.getPixel(x + 1, y, pixel);
                    raster.setPixel(x, y, pixel);
                }
            }

            if (maxX < energyAddon.getWidth() - 1) {
                g2.drawImage(energyAddon, maxX - 1, 0, energyAddon.getWidth() - 1, energyAddon.getHeight(), maxX, 0, energyAddon.getWidth(), energyAddon.getHeight(), null);
            }
            // crop image:
            energyAddon = energyAddon.getSubimage(0, 0, energyAddon.getWidth() - 1, energyAddon.getHeight());
        }
    }

    private void carveGradientImageVertical(int[] seamLookup, int maxX, WritableRaster raster) {
        // carve seam from energy image:
        int[] ePixel = new int[1];
        int[] pixel = new int[3];
        Graphics2D g2 = energyImg.createGraphics();
        WritableRaster eRaster = energyImg.getRaster();
        for (int y = 0; y < energyImg.getHeight(); y++) {
            for (int x = seamLookup[y]; x <= maxX; x++) {
                eRaster.getPixel(x + 1, y, ePixel);
                eRaster.setPixel(x, y, ePixel);
            }
        }
        if (maxX < energyImg.getWidth() - 1) {
            g2.drawImage(energyImg, maxX - 1, 0, energyImg.getWidth() - 1, energyImg.getHeight(), maxX, 0, energyImg.getWidth(), energyImg.getHeight(), null);
        }
        // crop image:
        energyImg = energyImg.getSubimage(0, 0, energyImg.getWidth() - 1, energyImg.getHeight());

        // recompute gradient along the seam ...
        eRaster = energyImg.getRaster();
        for (int y = 0; y < energyImg.getHeight(); y++) {
            for (int x = seamLookup[y] - 1; x <= seamLookup[y] + 1; x++) {
                // for each pixel compute the whole gradient new:
                int gr1 = 0;
                int gr2 = 0;
                int grey;
                for (int gx = 0; gx <= 2; gx++) {
                    for (int gy = 0; gy <= 2; gy++) {
                        int xx = x + (gx - 1);
                        int yy = y + (gy - 1);
                        if (xx < 0) {
                            xx = 0;
                        }
                        if (xx >= img.getWidth()) {
                            xx = img.getWidth() - 1;
                        }
                        if (yy < 0) {
                            yy = 0;
                        }
                        if (yy >= img.getHeight()) {
                            yy = img.getHeight() - 1;
                        }
                        raster.getPixel(xx, yy, pixel);
                        grey = (int) (0.299 * pixel[0] + 0.587 * pixel[1] + 0.114 * pixel[2]);
                        gr1 += soebel1[gx + 3 * gy] * grey;
                        gr2 += soebel2[gx + 3 * gy] * grey;
                    }
                }
                pixel[0] = (Math.abs(gr1) + Math.abs(gr2)) >> 1;
                eRaster.setPixel(x, y, pixel);
            }
        }
    }

    private void carveGradientImageHorizontal(int[] seamLookup, int maxY, WritableRaster raster) {
        // carve seam from energy image:
        int[] ePixel = new int[1];
        int[] pixel = new int[3];
        Graphics2D g2 = energyImg.createGraphics();
        WritableRaster eRaster = energyImg.getRaster();
        for (int x = 0; x < energyImg.getWidth(); x++) {
            for (int y = seamLookup[x]; y <= maxY; y++) {
                eRaster.getPixel(x, y + 1, ePixel);
                eRaster.setPixel(x, y, ePixel);
            }
        }
        if (maxY < energyImg.getHeight() - 1) {
            g2.drawImage(energyImg, 0, maxY - 1, energyImg.getWidth(), energyImg.getHeight() - 1, 0, maxY, energyImg.getWidth(), energyImg.getHeight(), null);
        }
        // crop image:
        energyImg = energyImg.getSubimage(0, 0, energyImg.getWidth(), energyImg.getHeight() - 1);

        // recompute gradient along the seam ...
        eRaster = energyImg.getRaster();
        for (int x = 0; x < energyImg.getWidth(); x++) {
            for (int y = seamLookup[x] - 1; y <= seamLookup[x] + 1; y++) {
                // for each pixel compute the whole gradient new:
                int gr1 = 0;
                int gr2 = 0;
                int grey;
                for (int gx = 0; gx <= 2; gx++) {
                    for (int gy = 0; gy <= 2; gy++) {
                        int xx = x + (gx - 1);
                        int yy = y + (gy - 1);
                        if (xx < 0) {
                            xx = 0;
                        }
                        if (xx >= img.getWidth()) {
                            xx = img.getWidth() - 1;
                        }
                        if (yy < 0) {
                            yy = 0;
                        }
                        if (yy >= img.getHeight()) {
                            yy = img.getHeight() - 1;
                        }
                        raster.getPixel(xx, yy, pixel);
                        grey = (int) (0.299 * pixel[0] + 0.587 * pixel[1] + 0.114 * pixel[2]);
                        gr1 += soebel1[gx + 3 * gy] * grey;
                        gr2 += soebel2[gx + 3 * gy] * grey;
                    }
                }
                pixel[0] = (Math.abs(gr1) + Math.abs(gr2)) >> 1;
                eRaster.setPixel(x, y, pixel);
            }
        }
    }

    public void carveHorizontalSeam() {
        HorizontalSeam s = findOptimalHorizontalSeam();
//        int height = img.getHeight();
        int width = img.getWidth();
        // lookup table for the seam coordinates: x -> y as result of the backtracking.
        int[] seamLookup = new int[width];
        // backtracking:
        int nextY = s.startY;
        int minY = img.getHeight() - 1;
        int maxY = 0;
        seamLookup[width - 1] = nextY;
        seamLookup[width - 2] = nextY;
        for (int x = width - 2; x >= 1; x--) {
            nextY += (int) s.table[x][nextY][1];
            seamLookup[x - 1] = nextY;
            minY = Math.min(nextY, minY);
            maxY = Math.max(nextY, maxY);
        }
        WritableRaster raster = img.getRaster();

        // copy bounding boxes left and right from the seam ...
        Graphics2D g2 = img.createGraphics();
        int[] pixel = new int[3];
        for (int x = 0; x < raster.getWidth(); x++) {
            for (int y = seamLookup[x]; y <= maxY; y++) {
                raster.getPixel(x, y + 1, pixel);
                raster.setPixel(x, y, pixel);
            }
        }

        if (maxY < img.getHeight() - 1) {
            g2.drawImage(img, 0, maxY + 1, img.getWidth(), img.getHeight() - 1, 0, maxY + 2, img.getWidth(), img.getHeight(), null);
        }
        // crop image one row:
        img = img.getSubimage(0, 0, img.getWidth(), img.getHeight() - 1);

        carveGradientImageHorizontal(seamLookup, maxY, raster);

        if (energyAddon != null) {
            raster = energyAddon.getRaster();
            pixel = new int[4];
            g2 = energyAddon.createGraphics();

            for (int x = 0; x < raster.getWidth(); x++) {
                for (int y = seamLookup[x]; y <= maxY; y++) {
                    raster.getPixel(x, y + 1, pixel);
                    raster.setPixel(x, y, pixel);
                }
            }

            if (maxY < energyAddon.getHeight() - 1) {
                g2.drawImage(energyAddon, 0, maxY + 1, energyAddon.getWidth(), energyAddon.getHeight() - 1, 0, maxY + 2, energyAddon.getWidth(), energyAddon.getHeight(), null);
            }
            // crop image:
            energyAddon = energyAddon.getSubimage(0, 0, energyAddon.getWidth(), energyAddon.getHeight() - 1);

        }
    }

    /**
     * This method generates the energy image. Change this one to use another one besides the gradient.
     *
     * @return a grayscale image denoting the energy of the pixels.
     */
    private BufferedImage generateEnergyImage() {
        return getGradientImage();
    }

    @SuppressWarnings({"UnusedAssignment"})
    private BufferedImage getGradientImage() {
        // convert image to greyscale:
        BufferedImage img_grey = greyscale.filter(img, null);
        // run kernels on it:
//        BufferedImage grad03 = op.filter(img_grey, null);
//        return grad03;
        BufferedImage grad01 = ops1.filter(img_grey, null);
        BufferedImage grad02 = ops2.filter(img_grey, null);

        WritableRaster r01 = grad01.getRaster();
        WritableRaster r02 = grad02.getRaster();
        int h = img.getHeight();
        int[] pixel = new int[1];
        int[] col01 = new int[h];
        int[] col02 = new int[h];
        int[] temp = new int[1];
        int[] checkEnergy = new int[4];
        for (int x = 0; x < r01.getWidth(); x++) {
            r01.getPixels(x, 0, 1, h, col01);
            r02.getPixels(x, 0, 1, h, col02);
            for (int y = 0; y < h; y++) {
                temp[0] = (Math.abs(col01[y]) + Math.abs(col02[y])) >> 1;
                if (energyAddon != null) {
                    if (energyAddon.getRaster().getPixel(x, y, checkEnergy)[0] > 0) {
                        temp[0] = 255;
                    } else if (energyAddon.getRaster().getPixel(x, y, checkEnergy)[1] > 0) {
                        temp[0] = 0;
                    }
                }
                r01.setPixel(x, y, temp);
            }
        }
        return grad01;
    }

    public BufferedImage getImg() {
        return img;
    }
}

class VerticalSeam implements Comparable {

    public int startX;
    public float[][][] table;
    private float energy;
    private int tableWidth;
    private int tableHeight;

    public VerticalSeam(float energy, int startX, float[][][] table, int tableWidth, int tableHeight) {
        this.energy = energy;
        this.startX = startX;
        this.table = table;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;
    }

    public int compareTo(Object o) {
        int result = 0;
        if (o instanceof VerticalSeam) {
            VerticalSeam s = (VerticalSeam) o;
            float e = s.energy - energy;
            result = (int) Math.signum(e);
        }
        return result;
    }
}

class HorizontalSeam implements Comparable {

    public int startY;
    public float[][][] table;
    private float energy;
    private int tableWidth;
    private int tableHeight;


    public HorizontalSeam(float energy, int startY, float[][][] table, int tableWidth, int tableHeight) {
        this.energy = energy;
        this.startY = startY;
        this.table = table;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;
    }

    public int compareTo(Object o) {
        int result = 0;
        if (o instanceof HorizontalSeam) {
            HorizontalSeam s = (HorizontalSeam) o;
            float e = s.energy - energy;
            result = (int) Math.signum(e);
        }
        return result;
    }
}
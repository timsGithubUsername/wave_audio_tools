package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {
    private static final int OFFSET = 20;

    public static BufferedImage processWithWeight(int xSize, int ySize, float[] data) {
        //calculate which points from the data array are plotted
        int step = data.length / xSize;
        //set the scale factor so that the highest value isnt bigger than ySize
        float scaleFactor = (ySize - OFFSET) / (2 * getMax(data));
        //prepare image
        BufferedImage image = new BufferedImage(xSize, ySize, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint (new Color(255, 255, 255));
        graphics.fillRect ( 0, 0, xSize, ySize);

        float temp; //holds the hight of the current peak as float
        int tempVal; //the hight on the plot
        int black = new Color(0, 0, 0).getRGB();
        //for every point at length
        for (int x = 0; x < xSize; x++) {
            temp = 0;
            //calculate average over step
            for(int i = x; i < x + step; i++) temp += scaleFactor * data[step * x];
            tempVal = (int) (temp / step);
            //if positive fill upper halve...
            if(tempVal >= 0) {
                for(int y = ySize - (tempVal + ySize/2); y <= ySize/2; y++) image.setRGB(x, y, black);
            } //...else fill under halve
            else {
                for(int y = ySize/2; y < Math.abs(tempVal) + ySize/2; y++) image.setRGB(x, y, black);
            }
        }

        return image;
    }
    public static BufferedImage process(int xSize, int ySize, float[] data) {
        int step = data.length / xSize;

        float scaleFactor = (ySize - OFFSET) / getMax(data);

        BufferedImage image = new BufferedImage(xSize, ySize, BufferedImage.TYPE_4BYTE_ABGR);

        float tempVal;
        int black = new Color(0, 0, 0).getRGB();
        int white = new Color(255, 255, 255).getRGB();

       for (Integer x = 0; x < xSize; x++) {
           tempVal = Math.abs(scaleFactor * data[step * x]);
           for (int y = 0; y < ySize; y++) {
               if (tempVal >= (ySize -y)) image.setRGB(x, y, black);
               else image.setRGB(x, y, white);
           }
       }

        return image;
    }

    private static float getMax(float[] data) {
        float max = 0f;

        for (int i = 0; i < data.length; i++) {
            if (Math.abs(data[i]) > max) max = Math.abs(data[i]);
        }

        return max;
    }
}

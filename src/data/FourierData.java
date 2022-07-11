package data;

import graphics.ImageProcessor;
import math.FourierProcessor;

import java.awt.image.BufferedImage;

public class FourierData {
    final int DEFAULT_X_SIZE = 1920;
    final int DEFAULT_Y_SIZE = 1080;

    private BufferedImage plotReal;
    private BufferedImage plotImaginary;
    private BufferedImage plotMagnitude;
    private float[] real;
    private float[] imaginary;
    private float[] magnitude;

    /**
     * Create Fourier Data from sample Array. If you pass a FourierProcessor object you are able to track the progress
     * while the calculation runs. It is expected to do this in a own thread!
     * @param sampleArray the sample array to create the fourier transformation from
     * @param processor the processor to track the progress
     */
    public FourierData(float[] sampleArray, FourierProcessor processor){
        real = processor.dftReal2(sampleArray);
        imaginary = processor.dftImg2(sampleArray);
        fillMagnitude();
    }

    /**
     * Create Fourier Data from sample Array.
     * @param sampleArray the sample array to create the fourier transformation from
     */
    public FourierData(float[] sampleArray){
        FourierProcessor processor = new FourierProcessor();

        real = processor.dftReal2(sampleArray);
        imaginary = processor.dftImg2(sampleArray);
        fillMagnitude();
    }

    //Calculate the Magnetude Array from the real and imaginary part
    private void fillMagnitude() {
        magnitude = new float[real.length];
        for(int i = 0; i < magnitude.length; i++){
            magnitude[i] = (float) Math.sqrt(Math.pow(real[i],2) + Math.pow(imaginary[i], 2));
        }
    }

    public float[] getReal() {
        return real;
    }

    public float[] getImaginary() {
        return imaginary;
    }

    //todo there should be another solution to track the progress in the ui
    public float[] getSamples(){
        return (new FourierProcessor()).iDft(real, imaginary);
    }

    public float[] getMagnitude() {
        return magnitude;
    }

    //the idea behind the plots: they dont get calculated if you dont need them
    public BufferedImage getPlotMagnitude() {
        if(plotMagnitude == null) plotMagnitude = ImageProcessor.process(DEFAULT_X_SIZE, DEFAULT_Y_SIZE,magnitude);
        return plotMagnitude;
    }

    public BufferedImage getPlotReal() {
        if(plotReal == null) plotReal = ImageProcessor.process(DEFAULT_X_SIZE, DEFAULT_Y_SIZE,real);
        return plotReal;
    }

    public BufferedImage getPlotImaginary() {
        if(plotImaginary == null) plotImaginary = ImageProcessor.process(DEFAULT_X_SIZE, DEFAULT_Y_SIZE,imaginary);
        return plotImaginary;
    }
}

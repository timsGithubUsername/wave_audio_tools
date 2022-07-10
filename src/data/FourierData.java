package data;

import graphics.ImageProcessor;
import math.FourierProcessor;

import java.awt.image.BufferedImage;

public class FourierData {
    //todo make accessable
    final int DEFAULT_X_SIZE = 1920;
    final int DEFAULT_Y_SIZE = 1080;

    private BufferedImage plotReal;
    private BufferedImage plotImaginary;
    private BufferedImage plotMagnitude;
    private float[] real;
    private float[] imaginary;
    private float[] magnitude;

    //public FourierOfTrack(MusicTrack track){
    //    real = FourierProcessor.dftReal(MAX_FREQUENCY, track.getSampleArray(), DEFAULT_STEP_SIZE);
    //    imaginary = FourierProcessor.dftImg(MAX_FREQUENCY, track.getSampleArray(), DEFAULT_STEP_SIZE);
    //    plotReal = ImageProcessor.process(DEFAULT_X_SIZE,DEFAIULT_Y_SIZE,real);
    //    plotImaginary = ImageProcessor.process(DEFAULT_X_SIZE,DEFAIULT_Y_SIZE,imaginary);
    //}
    public FourierData(float[] sampleArray, FourierProcessor processor){
        real = processor.dftReal2(sampleArray);
        imaginary = processor.dftImg2(sampleArray);
        fillMagnitude();
    }

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

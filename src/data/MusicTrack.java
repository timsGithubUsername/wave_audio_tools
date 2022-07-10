package data;

import math.FourierProcessor;

import javax.sound.sampled.AudioFormat;
import java.awt.image.BufferedImage;

public interface MusicTrack {
    String getName();
    byte[] getMusicFileByteArray();
    byte[] getFormatByteArray();
    byte[] getMusicDataByteArray();
    float[] getSampleArray();
    AudioFormat getFormat();
    BufferedImage getWavePlot();
    FourierData getFourierData(FourierProcessor processor);
    float[] geNumberOfSamples(int numberOfSamples);
}

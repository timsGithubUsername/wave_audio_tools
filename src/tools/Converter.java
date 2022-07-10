package tools;

import javax.sound.sampled.AudioFormat;

public interface Converter {
    float[] getSampleArray(byte[] byteArray, AudioFormat fmt);
    byte[] getByteArray(float[] sampleArray, AudioFormat fmt);
}

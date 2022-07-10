package math;

import static java.lang.Math.*;

public class FourierProcessor {

    private static FourierProcessor fourierProcessor;
    public static FourierProcessor getInstance(){
        if(fourierProcessor == null) fourierProcessor = new FourierProcessor();
        return fourierProcessor;
    }

    private int progress = -1;

    public float[] dftReal2(float[] samples){
        int numOfSamples = samples.length;
        int percent = 0;

        float[] output = new float[samples.length / 2];
        float temp;
        int tempPercent = 0;

        for(int u = 0; u < output.length; u++){
            temp = 0f;

            for(int n = 0; n < numOfSamples; n++){
                temp += samples[n] * cos((2 * PI * n * u) / (float) numOfSamples);
            }

            tempPercent = (int) (( u / (double) output.length) * 100);
            if(percent < tempPercent) {
                percent = tempPercent;
                progress = percent / 2;
            }

            output[u] = temp;
        }

        return output;
    }

    public float[] dftImg2(float[] samples){
        int numOfSamples = samples.length;
        int percent = 0;

        float[] output = new float[samples.length / 2];
        float temp;
        int tempPercent = 0;

        for(int u = 0; u < output.length; u++){
            temp = 0f;

            for(int n = 0; n < numOfSamples; n++){
                temp += samples[n] * sin((2 * PI * n * u) / (float) numOfSamples);
            }

            tempPercent = (int) (( u / (double) output.length) * 100);
            if(percent < tempPercent) {
                percent = tempPercent;

                progress = 50 + percent / 2;
            }

            output[u] = temp;
        }

        progress = -1;
        return output;
    }

    //todo
    /**
     * Make a Sample Array based on a real- and imaginary part of the dft
     * @param real real part of the dft
     * @param img imaginary part of the dft
     * @return sampleArray with sampleCount samples
     */
    public float[] iDft(float[] real, float[] img){
        int sampleCount = real.length;
        float[] output = new float[sampleCount];
        float temp;

        for(int n = 0; n < sampleCount; n++){
            temp = 0f;

            for(int u = 0; u < real.length; u++){
                temp += (real[u] * cos((2 * PI * n * u) / sampleCount)) - (img[u] * sin((2 * PI * n * u) / sampleCount));
            }

            output[n] = temp / sampleCount;
        }
        return output;
    }

    public int getProgress() {
        return progress;
    }
}

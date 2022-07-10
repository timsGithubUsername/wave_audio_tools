package math;

public class ConvolveProcessor {

    private static ConvolveProcessor convolveProcessor;
    public static ConvolveProcessor getInstance(){
        if(convolveProcessor == null) convolveProcessor = new ConvolveProcessor();
        return convolveProcessor;
    }

    private int progress = -1;

    public float[] convolve(float[] samples, float[] weight){
        float[] output = new float[samples.length];
        int percent = 0;

        float temp = 0;
        int tempPercent = 0;

        for(int n = 0; n < samples.length; n++){
            for(int k = 0; k < weight.length; k++){
                if(n - k < 0) temp += 0;
                else temp += samples[n - k] * weight[k];
            }
            output[n] = temp;
            temp = 0;

            tempPercent = (int) (( n / (double) output.length) * 100);
            if(percent < tempPercent) {
                percent = tempPercent;

                progress = percent;
            }
        }

        normalize(output, getMaxValue(samples));

        progress = -1;
        return output;
    }

    public int getProgress() {
        return progress;
    }

    private void normalize(float[] samples, float maxValueGoal){
        float factor = maxValueGoal / getMaxValue(samples);

        for(int i = 0; i < samples.length; i++){
            samples[i] = samples[i] * factor;
        }
    }

    private float getMaxValue(float[] samples){
        float max = 0;

        for(int i = 0; i < samples.length; i++) if(max < samples[i]) max = samples[i];

        return max;
    }
}

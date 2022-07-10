package tools;

import javax.sound.sampled.AudioFormat;

import static java.lang.Math.*;
import static java.lang.Math.log;
import static javax.sound.sampled.AudioFormat.*;

public class ConverterImpl1 implements Converter{
    //todo 44 is the size of all information before the actual data. that should be calculated!
    //int headerSize = 44;

    /**
     * converts a byte array of the audio data in a sample array with floats between 1 and -1
     * @param byteArray the audio data to convert
     * @param fmt the format information for the audio data
     * @return the sample array with floats between 1 and -1
     */
    @Override
    public float[] getSampleArray(byte[] byteArray, AudioFormat fmt) {
        int bitsPerSample = fmt.getSampleSizeInBits();
        int bytesPerSample = (int) ceil(bitsPerSample / 8);

        return fillSampleArray(bitsPerSample, bytesPerSample, fmt.getEncoding(), pow(2.0, bitsPerSample - 1),
                byteArray, byteArray.length / bytesPerSample);
    }

    private float[] fillSampleArray(int bitsPerSample, int bytesPerSample, Encoding encoding, double highestValue, byte[] audioData, int sampleCount) {
        float[] samples = new float[sampleCount];
        int s = 0;
        long temp = 0;
        float sample = 0f;
        for(int i = 0; i < audioData.length;) {
            temp = unpackBits(audioData, i, bytesPerSample);
            sample = 0f;
            //todo this section is pretty much copy pasted...
            if (encoding == Encoding.PCM_SIGNED) {
                temp = extendSign(temp, bitsPerSample);
                sample = (float) (temp / highestValue);

            } else if (encoding == Encoding.PCM_UNSIGNED) {
                temp = unsignedToSigned(temp, bitsPerSample);
                sample = (float) (temp / highestValue);

            } else if (encoding == Encoding.PCM_FLOAT) {
                if (bitsPerSample == 32) {
                    sample = Float.intBitsToFloat((int) temp);
                } else if (bitsPerSample == 64) {
                    sample = (float) Double.longBitsToDouble(temp);
                }
            } else if (encoding == Encoding.ULAW) {
                sample = bitsToMuLaw(temp);

            } else if (encoding == Encoding.ALAW) {
                sample = bitsToALaw(temp);
            }

            samples[s] = sample;

            i += bytesPerSample;
            s++;
        }
        return samples;
    }

    private long unpackBits(byte[] audioData, int i, int bytesPerSample) {
        long temp = 0;
        for (int b = 0; b < bytesPerSample; b++) {
            temp = temp | ((audioData[i + b] & 0xffL) << (8 * b));
        }
        return temp;
    }

    /*
     * Get the actual data from the byte array
     * @param byteArray the byte array from the file
     * @return a byte array wich contains only the data
     */
   //private byte[] getAudioData(byte[] byteArray) {
   //    byte[] audioData = new byte[byteArray.length - headerSize];

   //    for(int i = 44; i < audioData.length; i++) audioData[i] = byteArray[i];

   //    return audioData;
   //}

    /**
     * converts a sample array with floats between 1 and -1 to a byte array
     * @param sampleArray the sample array to convert
     * @param fmt the format information
     * @return a byte array which represent the audio data
     */
    @Override
    public byte[] getByteArray(float[] sampleArray, AudioFormat fmt) {
        //todo refactor
        int bitsPerSample = fmt.getSampleSizeInBits();
        int bytesPerSample = (int) ceil(bitsPerSample / 8);
        boolean isBigEndian = fmt.isBigEndian();
        Encoding encoding = fmt.getEncoding();
        double highestValue = pow(2.0, bitsPerSample - 1);
        byte[] byteArray = new byte[sampleArray.length * bytesPerSample];

        int i = 0;
        int s = 0;
        while (s < sampleArray.length) {
            float sample = sampleArray[s];
            long temp = 0L;

            if (encoding == Encoding.PCM_SIGNED) {
                temp = (long) (sample * highestValue);

            } else if (encoding == Encoding.PCM_UNSIGNED) {
                temp = (long) (sample * highestValue);
                temp = signedToUnsigned(temp, bitsPerSample);

            } else if (encoding == Encoding.PCM_FLOAT) {
                if (bitsPerSample == 32) {
                    temp = Float.floatToRawIntBits(sample);
                } else if (bitsPerSample == 64) {
                    temp = Double.doubleToRawLongBits(sample);
                }
            } else if (encoding == Encoding.ULAW) {
                temp = muLawToBits(sample);

            } else if (encoding == Encoding.ALAW) {
                temp = aLawToBits(sample);
            }

            packBits(byteArray, i, temp, bytesPerSample);

            i += bytesPerSample;
            s++;
        }

        return byteArray;
    }

    private static void packBits(byte[]  bytes,
                                   int     i,
                                   long    temp,
                                   int     bytesPerSample) {
        for (int b = 0; b < bytesPerSample; b++) {
            bytes[i + b] = (byte) ((temp >>> (8 * b)) & 0xffL);
        }
    }

    //todo this section is also pretty much copy pasted
    private static long extendSign(long temp, int bitsPerSample) {
        int bitsToExtend = Long.SIZE - bitsPerSample;
        return (temp << bitsToExtend) >> bitsToExtend;
    }

    private static long unsignedToSigned(long temp, int bitsPerSample) {
        return temp - (int) pow(2.0, bitsPerSample - 1);
    }

    private static long signedToUnsigned(long temp, int bitsPerSample) {
        return temp + (long) pow(2.0, bitsPerSample - 1);
    }

    // mu-law constant
    private static final double MU = 255.0;
    // A-law constant
    private static final double A = 87.7;
    // natural logarithm of A
    private static final double LN_A = log(A);

    private static float bitsToMuLaw(long temp) {
        temp ^= 0xffL;
        if ((temp & 0x80L) != 0) {
            temp = -(temp ^ 0x80L);
        }

        float sample = (float) (temp / pow(2.0, 7));

        return (float) (
                signum(sample)
                        *
                        (1.0 / MU)
                        *
                        (pow(1.0 + MU, abs(sample)) - 1.0)
        );
    }

    private static long muLawToBits(float sample) {
        double sign = signum(sample);
        sample = abs(sample);

        sample = (float) (
                sign * (log(1.0 + (MU * sample)) / log(1.0 + MU))
        );

        long temp = (long) (sample * pow(2.0, 7));

        if (temp < 0) {
            temp = -temp ^ 0x80L;
        }

        return temp ^ 0xffL;
    }

    private static float bitsToALaw(long temp) {
        temp ^= 0x55L;
        if ((temp & 0x80L) != 0) {
            temp = -(temp ^ 0x80L);
        }

        float sample = (float) (temp / pow(2.0, 7));

        float sign = signum(sample);
        sample = abs(sample);

        if (sample < (1.0 / (1.0 + LN_A))) {
            sample = (float) (sample * ((1.0 + LN_A) / A));
        } else {
            sample = (float) (exp((sample * (1.0 + LN_A)) - 1.0) / A);
        }

        return sign * sample;
    }

    private static long aLawToBits(float sample) {
        double sign = signum(sample);
        sample = abs(sample);

        if (sample < (1.0 / A)) {
            sample = (float) ((A * sample) / (1.0 + LN_A));
        } else {
            sample = (float) ((1.0 + log(A * sample)) / (1.0 + LN_A));
        }

        sample *= sign;

        long temp = (long) (sample * pow(2.0, 7));

        if (temp < 0) {
            temp = -temp ^ 0x80L;
        }

        return temp ^ 0x55L;
    }
}
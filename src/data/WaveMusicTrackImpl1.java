package data;

import graphics.ImageProcessor;
import math.FourierProcessor;
import tools.Converter;
import tools.ConverterImpl1;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * This class holds and generates all necessary data structures for a music track to manipulate it, load it or save it.
 */
public class WaveMusicTrackImpl1 implements MusicTrack {
    /**
     * This numbers are always the same for wave files. This class works as only with and for wave files.
     */
    private final int RIFF_SIZE = 12;
    private final int FMT_SIZE = 24;
    private final int DATA_OFFSET = 8;

    private byte[] musicDataByteArray;
    private byte[] formatByteArray;
    private byte[] musicByteArray;
    private float[] musicSampleArray;
    private Converter converter = new ConverterImpl1();
    private AudioFormat fmt;
    private String name;

    BufferedImage wavePlot;

    private FourierData fourierData;

    /**
     * Create MusicTrack from wma-File
     *
     * @param file the File
     * @throws Exception if the file dont exist or the audio format is not supported
     */
    public WaveMusicTrackImpl1(File file) {
        try {
            setByteArrayAndFmt(file);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        musicSampleArray = converter.getSampleArray(musicDataByteArray, fmt);
        name = file.getName();
    }

    /**
     * Create MusicTrack from SampleArray
     *
     * @param sampleArray the sample Array
     * @param fmt         the audio format of the underlying file
     * @param name        the name of the track
     */
    public WaveMusicTrackImpl1(float[] sampleArray, AudioFormat fmt, byte[] formatByteArray, String name) {
        musicDataByteArray = converter.getByteArray(sampleArray, fmt);
        this.fmt = fmt;
        this.musicSampleArray = sampleArray;
        this.formatByteArray = formatByteArray;
        this.name = name;

        setAudioByteArray();
    }

    /**
     * Reduced constructor to create a musicTrackImpl object only with a sample array and a name
     * @param sampleArray   the audio format of the underlying file
     * @param name          the name of the track
     */
    public WaveMusicTrackImpl1(float[] sampleArray, String name){
        this.musicSampleArray = sampleArray;
        this.name = name;
    }
    private void setAudioByteArray() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //RIFF
        output.writeBytes("RIFF".getBytes(StandardCharsets.UTF_8));
        output.write(4 + 32 + 8 + musicDataByteArray.length); //Chunk Size: 4 + (8 + SubChunk1Size (FMT)) + (8 + SubChunk2Size (DATA))
        output.writeBytes("WAVE".getBytes());
        //FMT
        output.writeBytes(formatByteArray);
        //DATA
        output.writeBytes(musicDataByteArray);

        musicByteArray = output.toByteArray();
    }

    /*
     * Sets the byte array and the AudioFormat.
     * @param file the File we load to set the desired variables
     * @throws UnsupportedAudioFileException when audio file isnt supported
     * @throws IOException if audiofile is corrupted or dont exist
     */
    private void setByteArrayAndFmt(File file) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
        musicByteArray = audioInput.readAllBytes();
        setAudioData();
        setFormat();
        fmt = audioInput.getFormat();
    }

    private void setFormat() {
        byte[] formatData = new byte[FMT_SIZE];

        for (int i = RIFF_SIZE; i < RIFF_SIZE + FMT_SIZE; i++) formatData[i - RIFF_SIZE] = musicByteArray[i];

        formatByteArray = formatData;
    }

    /*
     * Get the actual data from the byte array
     * @param byteArray the byte array from the file
     * @return a byte array wich contains only the data
     */
    private void setAudioData() {
        byte[] audioData = new byte[musicByteArray.length - (RIFF_SIZE + FMT_SIZE + DATA_OFFSET)]; //x-44

        for (int i = RIFF_SIZE + FMT_SIZE + DATA_OFFSET; i < musicByteArray.length; i++)
            audioData[i - (RIFF_SIZE + FMT_SIZE + DATA_OFFSET)] = musicByteArray[i];
        // 44 -> x
        musicDataByteArray = audioData;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte[] getMusicFileByteArray() {
        return musicByteArray;
    }

    @Override
    public byte[] getFormatByteArray() {
        return formatByteArray;
    }

    @Override
    public byte[] getMusicDataByteArray() {
        return musicDataByteArray;
    }

    @Override
    public float[] getSampleArray() {
        return musicSampleArray;
    }

    @Override
    public AudioFormat getFormat() {
        return fmt;
    }

    @Override
    public BufferedImage getWavePlot() {
        if (wavePlot == null) wavePlot = ImageProcessor.processWithWeight(1920, 1080, musicSampleArray);
        return wavePlot;
    }

    @Override
    public FourierData getFourierData(FourierProcessor processor) {
        if(fourierData == null) fourierData = new FourierData(getNumberOfSamples(40000), processor);
        return fourierData;
    }

    /**
     * Takes a number of samples and calculates a sample array with the specified number of samples. It divides the
     * given sample array into this number of segments and calculates the average of these segments.
     * @param numberOfSamples The number of samples of the new sample array
     * @return a sample array with the given number of samples
     */
    @Override
    public float[] getNumberOfSamples(int numberOfSamples) {
        if(numberOfSamples > musicSampleArray.length) return musicSampleArray;

        float[] output = new float[numberOfSamples];
        float temp;
        int segment = musicSampleArray.length / numberOfSamples;

        for (int i = 0; i < numberOfSamples; i++){
            temp = 0;

            for(int s = i * segment; s < (i+1) * segment; s++){
                temp += musicSampleArray[s];
            }

            output[i] = temp / segment;
        }

        return output;
    }

}
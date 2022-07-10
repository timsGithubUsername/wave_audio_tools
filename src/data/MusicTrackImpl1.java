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

public class MusicTrackImpl1 implements MusicTrack {
    //todo the size of all information before the actual data. that should be calculated!
    int riffSize = 12;
    int fmtSize = 24;
    int dataOffset = 8;

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
     * Create MusicTrack from File
     *
     * @param file the File
     * @throws Exception if the file dont exist or the audio format is not supported
     */
    public MusicTrackImpl1(File file) {
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
     */
    public MusicTrackImpl1(float[] sampleArray, AudioFormat fmt, byte[] formatByteArray, String name) {
        musicDataByteArray = converter.getByteArray(sampleArray, fmt);
        this.fmt = fmt;
        this.musicSampleArray = sampleArray;
        this.formatByteArray = formatByteArray;
        this.name = name;

        setAudioByteArray();
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
        byte[] formatData = new byte[fmtSize];

        for (int i = riffSize; i < riffSize + fmtSize; i++) formatData[i - riffSize] = musicByteArray[i];

        formatByteArray = formatData;
    }

    /*
     * Get the actual data from the byte array
     * @param byteArray the byte array from the file
     * @return a byte array wich contains only the data
     */
    private void setAudioData() {
        byte[] audioData = new byte[musicByteArray.length - (riffSize + fmtSize + dataOffset)]; //x-44

        for (int i = riffSize + fmtSize + dataOffset; i < musicByteArray.length; i++)
            audioData[i - (riffSize + fmtSize + dataOffset)] = musicByteArray[i];
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
    public FourierData getFourierData() {
        if(fourierData == null) fourierData = new FourierData(geNumberOfSamples(40000));
        return fourierData;
    }

    @Override
    public float[] geNumberOfSamples(int numberOfSamples) {
        float[] output = new float[numberOfSamples];

        for (int i = 0; i < numberOfSamples; i++) {
            output[i] = musicSampleArray[i * (musicSampleArray.length / numberOfSamples)];
        }
        return output;
    }

}
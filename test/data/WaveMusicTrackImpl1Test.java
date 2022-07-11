package data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFormat;

import static org.junit.jupiter.api.Assertions.*;

class WaveMusicTrackImpl1Test {
    MusicTrack waveMusicTrack;

    @BeforeEach
    void createWaveMusicTrackObject(){
        float[] testSamples = {1.0f,1.0f,2.0f,2.0f,3.0f,3.0f,4.0f,4.0f,5.0f,5.0f,6.0f,6.0f};
        String name = "WaveMusicTrack";

        waveMusicTrack = new WaveMusicTrackImpl1(testSamples, name);
    }

    @Test
    void getNumberOfSamples() {
        //length
        assertEquals(6, waveMusicTrack.getNumberOfSamples(6).length);
        assertEquals(7, waveMusicTrack.getNumberOfSamples(7).length);
        assertEquals(12, waveMusicTrack.getNumberOfSamples(20).length);

        //content
        assertEquals(1.0, waveMusicTrack.getNumberOfSamples(6)[0]);
        assertEquals(3.0, waveMusicTrack.getNumberOfSamples(6)[2]);
        assertEquals(4.0f / 3.0f, waveMusicTrack.getNumberOfSamples(4)[0]);
    }
}
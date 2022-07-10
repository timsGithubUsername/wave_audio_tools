package tools;

import data.MusicTrack;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.ByteArrayInputStream;

public class MusicPlayer {
    static MusicPlayer player = new MusicPlayer();

    static public Clip clip;
    static long clipTimePosition;
    static MusicTrack currentTrack;


    public static MusicPlayer getInstance() {
        return player;
    }

    private static void loadMusic(MusicTrack track) {
        if(currentTrack != null){
            if(currentTrack.getName().equals(track.getName())) return;
            clip.stop();
            clip.setMicrosecondPosition(0);
            clipTimePosition = 0;
        }

        currentTrack = track;

        try {
            AudioInputStream audiInput = new AudioInputStream(new ByteArrayInputStream(track.getMusicDataByteArray()), track.getFormat(), track.getMusicDataByteArray().length);
            clip = AudioSystem.getClip();
            clip.open(audiInput);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void play(MusicTrack track, boolean loop) {
        loadMusic(track);
        loop(loop);

        player.clip.setMicrosecondPosition(clipTimePosition);
        player.clip.start();
    }

    public static void stop() {
        clipTimePosition = player.clip.getMicrosecondPosition();
        player.clip.stop();
    }

    private static void loop(boolean looping) {
        if (looping) {
            player.clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            player.clip.loop(0);
        }
    }

    public static boolean isTrackPlaying(){
        return clip.isActive();
    }
}

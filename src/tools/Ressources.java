package tools;

import data.MusicTrack;
import data.MusicTrackImpl1;

import java.io.File;
import java.util.ArrayList;

public class Ressources {

    private static Ressources instance;
    public static Ressources getInstance(){
        return instance;
    }

    private ArrayList<MusicTrack> impulseResponses = new ArrayList<>();

    public Ressources(String[] args){
        instance = this;

        for(int i = 0; i < args.length; i++){
            addToImpulseResponses(args[i]);
        }
    }
   // private void fillImpulseResponses() {
   //     String thisLocation = "./impulse Responses/";
   //
   //     try {
   //         impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + "Cement Blocks.wav")));
   //         impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + "French 18th Century Salon.wav")));
   //         impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + "Greek 7 Echo Hall.wav")));
   //         impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + "Highly Damped Large Room.wav")));
   //         impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + "In The Silo.wav")));
   //         impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + "Rays.wav")));
   //         impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + "Right Glass Triangle.wav")));
   //         impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + "St Nicolaes Church.wav")));
   //     } catch (Exception e) {
   //         e.printStackTrace();
   //     }
   // }
    private void addToImpulseResponses(String path){
        String thisLocation = "./impulse responses/";

        try {
            impulseResponses.add(new MusicTrackImpl1(new File(thisLocation + path)));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<MusicTrack> getImpulseResponses() { return impulseResponses;}
}

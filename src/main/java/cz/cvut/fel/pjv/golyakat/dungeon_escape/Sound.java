package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {

    Clip clip;
    URL soundURL[] = new URL[30]; // Storage for the music

    public Sound() {
        soundURL[0] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/main_sound.wav");
        soundURL[1] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/sword_hit.wav");
        soundURL[2] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/monster_attack.wav");

    }

    public void setFile(int i){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        }catch (Exception e){

        }
    }

    public void playSound(){
        clip.start();
    }

    public void loopSound(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void StopSound(){
        clip.stop();
    }
}

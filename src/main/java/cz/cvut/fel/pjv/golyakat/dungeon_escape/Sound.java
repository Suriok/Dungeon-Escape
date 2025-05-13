package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * The {@code Sound} class handles playing sound effects and music in the game.
 * <p>
 * It uses the Java Sound API ({@code javax.sound.sampled} package) – specifically,
 * the {@link Clip} class for playback and {@link AudioSystem} to load audio
 * streams from WAV files stored in the resources.
 * </p>
 */
public class Sound {

    /** The {@link Clip} instance that plays the current sound. */
    private Clip clip;

    /**
     * The {@link URL} array acts as a “catalog” of all sound files
     * that can be played in the game. It is preconfigured for up to
     * 30 sounds – to add more, simply insert the path in the constructor
     * and call {@link #setFile(int)} with the appropriate index.
     */
    private final URL[] soundURL = new URL[30];

    /**
     * Constructor populates the {@link #soundURL} array with paths to the desired
     * sound files stored in the classpath (the {@code resources} directory).
     * <ul>
     *   <li>index 0 – background music</li>
     *   <li>index 1 – sword hit sound</li>
     *   <li>index 2 – monster attack</li>
     * </ul>
     */
    public Sound() {
        soundURL[0] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/main_sound.wav");
        soundURL[1] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/sword_hit.wav");
        soundURL[2] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/monster_attack.wav");
    }

    /**
     * Loads a sound file by the given index and prepares it for playback.
     *
     * @param i the index in the {@link #soundURL} array. If the index is invalid
     *          or loading fails, the clip will not be set.
     */
    public void setFile(int i) {
        try {
            // 1) Get the audio stream from the chosen resource
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            // 2) Create a new Clip (sound buffer)
            clip = AudioSystem.getClip();
            // 3) Load the audio data into the Clip for playback
            clip.open(ais);
        } catch (Exception e) {
            // Silent catch – in production it would be better to log the error
        }
    }

    /** Immediately starts playing the currently loaded clip. */
    public void playSound() {
        clip.start();
    }

    /** Plays the sound in an infinite loop until it is stopped. */
    public void loopSound() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /** Immediately stops playing the clip. */
    public void StopSound() {
        clip.stop();
    }
}

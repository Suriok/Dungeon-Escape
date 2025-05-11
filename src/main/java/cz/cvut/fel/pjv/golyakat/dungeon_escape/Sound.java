package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * Třída {@code Sound} obstarává přehrávání zvuků a hudby ve hře.
 * <p>
 * Využívá rozhraní Java Sound API (balík {@code javax.sound.sampled}) – konkrétně
 * třídy {@link Clip} pro vlastní přehrávání a {@link AudioSystem} pro načtení audio
 * streamu ze souboru WAV uloženého v resources.
 * </p>
 */
public class Sound {

    /** Instance {@link Clip}, která přehrává aktuální zvuk. */
    private Clip clip;

    /**
     * Pole {@link URL} slouží jako „katalog“ všech zvukových souborů,
     * které můžeme ve hře přehrát. Zde je předpřipraveno místo až
     * pro 30 zvuků – pro další stačí přidat cestu do konstruktoru
     * a volat {@link #setFile(int)} s odpovídajícím indexem.
     */
    private final URL[] soundURL = new URL[30];

    /**
     * Konstruktor naplní pole {@link #soundURL} cestami k požadovaným zvukovým
     * souborům uloženým v classpath (adresář {@code resources}).
     * <ul>
     *   <li>index 0 – hudba na pozadí</li>
     *   <li>index 1 – zvuk zásahu mečem</li>
     *   <li>index 2 – útok monstra</li>
     * </ul>
     */
    public Sound() {
        soundURL[0] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/main_sound.wav");
        soundURL[1] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/sword_hit.wav");
        soundURL[2] = getClass().getResource("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sound/monster_attack.wav");
    }

    /**
     * Načte zvukový soubor podle zadaného indexu a připraví jej k přehrání.
     *
     * @param i index do pole {@link #soundURL}. Pokud je index neplatný
     *          nebo dojde k chybě při načítání, k nastavení klipu nedojde.
     */
    public void setFile(int i) {
        try {
            // 1) Získáme audio stream z voleného resource
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            // 2) Vytvoříme nový Clip (zvukový buffer)
            clip = AudioSystem.getClip();
            // 3) Načteme data do Clipu, aby bylo možné přehrávání
            clip.open(ais);
        } catch (Exception e) {
            // Tiché zachycení – v produkční verzi by bylo lepší chybové hlášení zalogovat
        }
    }

    /** Okamžitě spustí přehrávání aktuálně nastaveného klipu. */
    public void playSound() {
        clip.start();
    }

    /** Spustí zvuk v nekonečné smyčce, dokud není zastaven. */
    public void loopSound() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /** Okamžitě zastaví přehrávání klipu. */
    public void StopSound() {
        clip.stop();
    }
}

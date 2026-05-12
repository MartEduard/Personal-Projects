package audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class AudioPlayer {

    public static int MENU_1 = 0; //!< Indexul melodiei de meniu
    public static int LEVEL_1 = 1; //!< Indexul melodiei de meniu
    public static int LEVEL_2 = 2; //!< Indexul melodiei pentru nivelul 2

    public static int DIE = 0;
    public static int JUMP = 1;
    public static int GAMEOVER = 2;
    public static int LVL_COMPLETED = 3;
    public static int ATTACK_ONE = 4;
    public static int ATTACK_TWO = 5;
    public static int ATTACK_THREE = 6;

    private Clip[] songs; //!< Matricea de melodii
    private Clip[] effects; //!< Matricea de efecte sonore
    private int currentSongId; //!< ID-ul melodiei curente
    private float volume = 0.75f; //!< Volumul
    private boolean songMute; //!< Starea de mute a melodiei
    private boolean effectMute; //!< Starea de mute a efectului sonor
    private Random rand = new Random(); //!< Generator de numere aleatorii

    public AudioPlayer() { //! Constructor
        loadSongs();
        loadEffects();
        playSong(MENU_1);
    }

    private void loadSongs() { //! Metoda pentru încărcarea melodiilor
        String[] names = { "menu", "level1", "level2" };
        songs = new Clip[names.length];
        for (int i = 0; i < songs.length; i++)
            songs[i] = getClip(names[i]);
    }

    private void loadEffects() { //! Metoda pentru încărcarea efectelor sonore
        String[] effectNames = { "die", "jump", "gameover", "lvlcompleted", "attack1", "attack2", "attack3" };
        effects = new Clip[effectNames.length];
        for (int i = 0; i < effects.length; i++)
            effects[i] = getClip(effectNames[i]);

        updateEffectsVolume();

    }

    private Clip getClip(String name) { //! Metoda pentru obținerea unui clip
        URL url = getClass().getResource("/audio/" + name + ".wav");
        AudioInputStream audio;

        try {
            audio = AudioSystem.getAudioInputStream(url);
            Clip c = AudioSystem.getClip();
            c.open(audio);
            return c;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {

            e.printStackTrace();
        }

        return null;

    }

    public void setVolume(float volume) { //! Metoda pentru setarea volumului
        this.volume = volume;
        updateSongVolume();
        updateEffectsVolume();
    }

    public void stopSong() { //! Metoda pentru oprirea melodiei
        if (songs[currentSongId].isActive())
            songs[currentSongId].stop();
    }

    public void setLevelSong(int lvlIndex) { //! Metoda pentru oprirea efectului sonor
        if (lvlIndex % 2 == 0)
            playSong(LEVEL_1);
        else
            playSong(LEVEL_2);
    }

    public void lvlCompleted() { //! Metoda pentru redarea efectului sonor de nivel completat
        stopSong();
        playEffect(LVL_COMPLETED);
    }

    public void playAttackSound() { //! Metoda pentru redarea efectului sonor de nivel completat
        int start = 4;
        start += rand.nextInt(3);
        playEffect(start);
    }

    public void playEffect(int effect) { //! Metoda pentru redarea unui efect sonor
        if (effects[effect].getMicrosecondPosition() > 0)
            effects[effect].setMicrosecondPosition(0);
        effects[effect].start();
    }

    public void playSong(int song) {  //! Metoda pentru redarea unei melodii
        stopSong();

        currentSongId = song;
        updateSongVolume();
        songs[currentSongId].setMicrosecondPosition(0);
        songs[currentSongId].loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void toggleSongMute() { //! Metoda pentru comutarea stării de mute a melodiei
        this.songMute = !songMute;
        for (Clip c : songs) {
            BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(songMute);
        }
    }

    public void toggleEffectMute() { //! Metoda pentru comutarea stării de mute a efectului sonor
        this.effectMute = !effectMute;
        for (Clip c : effects) {
            BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(effectMute);
        }
        if (!effectMute)
            playEffect(JUMP);
    }

    private void updateSongVolume() { //! Metoda pentru actualizarea volumului melodiei

        FloatControl gainControl = (FloatControl) songs[currentSongId].getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);

    }

    private void updateEffectsVolume() { //! Metoda pentru actualizarea volumului efectelor sonore
        for (Clip c : effects) {
            FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }

}

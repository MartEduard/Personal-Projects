package Gamestates;

import Main.Game;
import audio.AudioPlayer;
import ui.MenuButton;

import java.awt.event.MouseEvent;

public class State { //! Clasa de bază pentru stările jocului
    protected Game game; //! Jocul
    public State(Game game) { //! Constructor
        this.game=game;
    }
    public boolean isIn(MouseEvent e, MenuButton mb){ //! Verifică dacă cursorul este pe buton
        return mb.getBounds().contains(e.getX(),e.getY());
    }
    public Game getGame() { //! Returnează jocul
        return game;
    }
    @SuppressWarnings("incomplete-switch")
    public void setGameState(Gamestate state){ //! Setează starea jocului
        switch (state){
            case MENU -> game.getAudioPlayer().playSong(AudioPlayer.MENU_1);
            case PLAYING -> game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLevelIndex());
        }
        Gamestate.state= state;

    }
}

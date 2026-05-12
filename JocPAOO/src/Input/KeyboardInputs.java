package Input;
import Gamestates.Gamestate;
import Main.*;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
public class KeyboardInputs implements KeyListener  {
    private GamePanel gamePanel; //!< Referință la panoul jocului
    public KeyboardInputs(GamePanel gamePanel) { //!< Constructor
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) { //!< Dacă se apasă o tastă, se apelează metoda corespunzătoare stării jocului
        switch(Gamestate.state) {
            case MENU:
                gamePanel.getGame().getMenu().keyPressed(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().keyPressed(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getGameOptions().keyPressed(e);
                break;
            case CREDITS:
                gamePanel.getGame().getCredits().keyPressed(e);
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { //!< Dacă se eliberează o tastă, se apelează metoda corespunzătoare stării jocului

        switch(Gamestate.state) {
            case MENU:
                gamePanel.getGame().getMenu().keyReleased(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().keyReleased(e);
                break;
            default:
                break;
        }


    }
}

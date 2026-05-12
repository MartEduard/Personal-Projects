package Input;
import Gamestates.Gamestate;
import Main.*;
import java.awt.event.*;

public class MouseInputs implements MouseListener, MouseMotionListener {
    private GamePanel gamePanel; //!< Referință la panoul jocului
    public MouseInputs(GamePanel gamePanel) { //!< Constructor
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseDragged(MouseEvent e) { //!< Dacă se trage de mouse, se apelează metoda corespunzătoare stării jocului
        switch(Gamestate.state){
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseDragged(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getGameOptions().mouseDragged(e);
            default:
                break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) { //!< Dacă mouse-ul se mișcă, se apelează metoda corespunzătoare stării jocului
        switch(Gamestate.state) {
            case MENU:
                gamePanel.getGame().getMenu().mouseMoved(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseMoved(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getGameOptions().mouseMoved(e);
            default:
                break;

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { //!< Dacă se face click, se apelează metoda corespunzătoare stării jocului
        switch(Gamestate.state) {
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseClicked(e);
                break;
            default:
                break;

        }
    }

    @Override
    public void mousePressed(MouseEvent e) { //!< Dacă se apasă pe mouse, se apelează metoda corespunzătoare stării jocului
        switch(Gamestate.state) {
            case MENU:
                gamePanel.getGame().getMenu().mousePressed(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mousePressed(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getGameOptions().mousePressed(e);
            default:
                break;

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) { //!< Dacă se eliberează mouse-ul, se apelează metoda corespunzătoare stării jocului
        switch(Gamestate.state) {
            case MENU:
                gamePanel.getGame().getMenu().mouseReleased(e);
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseReleased(e);
                break;
            case OPTIONS:
                gamePanel.getGame().getGameOptions().mouseReleased(e);
            default:
                break;

        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

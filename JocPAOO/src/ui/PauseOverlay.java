package ui;

import Gamestates.Gamestate;
import Gamestates.Playing;
import Main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLOutput;

import static utils.Constants.UI.PauseButtons.*;
import static utils.Constants.UI.URMButtons.*;
import static utils.Constants.UI.VolumeButtons.*;

public class PauseOverlay {
    private Playing playing; //!< Referință către joc
    private AudioOptions audioOptions; //!< Opțiunile audio
    private BufferedImage backgroundImg; //!< Imaginea de fundal
    private int bgX, bgY, bgW, bgH; //!< Poziția și dimensiunile imaginii

    private UrmButton menuB, replayB, unpauseB;

    public PauseOverlay(Playing playing) { //! Constructor
        this.playing = playing;
        loadBackground();
        audioOptions = playing.getGame().getAudioOptions();
        createUrmButtons();

    }



    private void createUrmButtons() { //! Creează butoanele de meniu, replay și unpause
        int menuX =(int)(313*Game.SCALE);
        int replayX = (int)(387*Game.SCALE);
        int unpauseX = (int)(462*Game.SCALE);
        int bY = (int)(325*Game.SCALE);

        menuB = new UrmButton(menuX, bY, URM_SIZE, URM_SIZE, 2);
        replayB = new UrmButton(replayX, bY, URM_SIZE, URM_SIZE, 1);
        unpauseB = new UrmButton(unpauseX, bY, URM_SIZE, URM_SIZE, 0);
    }



    private void loadBackground() { //! Încarcă imaginea de fundal
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        bgW = (int)(backgroundImg.getWidth()* Game.SCALE);
        bgH = (int)(backgroundImg.getHeight()* Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int)( 25* Game.SCALE);
    }

    public void update() { //! Actualizează overlay-ul

        menuB.update();
        replayB.update();
        unpauseB.update();

        audioOptions.update();

    }
    public void draw(Graphics g) { //! Desenează overlay-ul
        //background
        g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);
        //butoane de muzica

        // URM Buttons
        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);
        audioOptions.draw(g);


    }
    public void mouseDragged(MouseEvent e) { //! Metodă care se ocupă de trăgerea cursorului
        audioOptions.mouseDragged(e);
    }
    public void mouseMoved(MouseEvent e) {  //! Metodă care se ocupă de mișcarea cursorului

        menuB.setMouseOver(false);
        replayB.setMouseOver(false);
        unpauseB.setMouseOver(false);


        if(isIn(e,menuB))
            menuB.setMouseOver(true);
        else if(isIn(e,replayB))
            replayB.setMouseOver(true);
        else if(isIn(e,unpauseB))
            unpauseB.setMouseOver(true);
        else
            audioOptions.mouseMoved(e);

    }
    public void mouseReleased(MouseEvent e) { //! Metodă care se ocupă de eliberarea butoanelor

        if (isIn(e,menuB)){
            if(menuB.isMousePressed()) {
                playing.resetAll();
                playing.setGameState(Gamestate.MENU);
                playing.unpauseGame();
            }

        }
        else if (isIn(e,replayB)){
            if(replayB.isMousePressed()) {
                playing.resetAll();
                playing.unpauseGame();
            }

        }
        else if (isIn(e,unpauseB)){
            if(unpauseB.isMousePressed())
                playing.unpauseGame();
        }
        else
            audioOptions.mouseReleased(e);


        menuB.resetBools();
        replayB.resetBools();
        unpauseB.resetBools();

    }
    public void mousePressed(MouseEvent e) { //! Metodă care se ocupă de apăsarea butoanelor

        if(isIn(e,menuB))
            menuB.setMousePressed(true);
        else if(isIn(e,replayB))
            replayB.setMousePressed(true);
        else if(isIn(e,unpauseB))
            unpauseB.setMousePressed(true);
        else
            audioOptions.mousePressed(e);
    }
    private boolean isIn(MouseEvent e, PauseButton b) { //! Verifică dacă cursorul este pe un buton
        return b.getBounds().contains(e.getX(), e.getY());
    }
}

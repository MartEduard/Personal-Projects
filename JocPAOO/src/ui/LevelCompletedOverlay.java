package ui;

import Gamestates.Gamestate;
import Gamestates.Playing;
import Main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.*;

public class LevelCompletedOverlay {

    private Playing playing; //!< Referință către joc
    private UrmButton menu, next; //!< Butonul de meniu și cel de next
    private BufferedImage img; //!< Imaginea de fundal
    private int bgX, bgY, bgW, bgH; //!< Poziția și dimensiunile imaginii

    public LevelCompletedOverlay(Playing playing) { //! Constructor
        this.playing = playing;
        initImg();
        initButtons();
    }

    private void initButtons() { //! Creează butoanele
        int menuX = (int)(330*Game.SCALE);
        int nextX = (int)(445*Game.SCALE);
        int y = (int)(195*Game.SCALE);
        next = new UrmButton(nextX,y,URM_SIZE,URM_SIZE,0);
        menu = new UrmButton(menuX,y,URM_SIZE,URM_SIZE,2);
    }

    private void initImg() { //! Creează imaginea de fundal
        img = LoadSave.GetSpriteAtlas(LoadSave.COMPLETED_IMAGE);
        bgW = (int)(img.getWidth() * Game.SCALE);
        bgH = (int)(img.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH /2 - bgW / 2;
        bgY = (int)(75*Game.SCALE);

    }
    public void draw(Graphics g) { //! Desenează overlay-ul
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT);
        g.drawImage(img,bgX,bgY,bgW,bgH,null);
        next.draw(g);
        menu.draw(g);

    }
    public void update() { //! Actualizează overlay-ul
        next.update();
        menu.update();

    }
    private boolean isIn(UrmButton b, MouseEvent e) { //! Verifică dacă cursorul este în buton
        return b.getBounds().contains(e.getX(), e.getY());

    }
    public void mouseMoved(MouseEvent e) { //! Metodă care se ocupă de mișcarea cursorului
        next.setMouseOver(false);
        menu.setMouseOver(false);

        if(isIn(menu,e))
            menu.setMouseOver(true);
        else if (isIn(next,e))
            next.setMouseOver(true);

    }
    public void mouseReleased(MouseEvent e) { //! Metodă care se ocupă de eliberarea butoanelor
        if(isIn(menu,e)) {
            if (menu.isMousePressed()){
                playing.resetAll();
                playing.setGameState(Gamestate.MENU);
            }

        }else if (isIn(next,e))
            if(next.isMousePressed()) {
                playing.loadNextLevel();
                playing.getGame().getAudioPlayer().setLevelSong(playing.getLevelManager().getLevelIndex());
            }

        menu.resetBools();
        next.resetBools();
    }
    public void mousePressed(MouseEvent e){ //! Metodă care se ocupă de apăsarea butoanelor
        if(isIn(menu,e))
            menu.setMousePressed(true);
        else if (isIn(next,e))
            next.setMousePressed(true);
    }

}

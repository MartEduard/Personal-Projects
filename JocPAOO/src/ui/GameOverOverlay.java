package ui;
import Gamestates.Gamestate;
import Gamestates.Playing;
import Main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.URM_SIZE;

public class GameOverOverlay {
    private Playing playing; //!< Referință către joc
    private BufferedImage img; //!< Imaginea de fundal
    private int imgX,imgY,imgW,imgH; //!< Poziția și dimensiunile imaginii
    private UrmButton menu,play; //!< Butonul de meniu și cel de play

    public GameOverOverlay(Playing playing) { //! Constructor
        this.playing = playing;
        createImg();
        createButtons();
    }

    private void createButtons() { //! Creează butoanele
        int menuX = (int)(330*Game.SCALE);
        int playX = (int)(440*Game.SCALE);
        int y = (int)(195*Game.SCALE);
        play = new UrmButton(playX,y,URM_SIZE,URM_SIZE,0);
        menu = new UrmButton(menuX,y,URM_SIZE,URM_SIZE,2);
    }

    private void createImg() { //! Creează imaginea de fundal
        img= LoadSave.GetSpriteAtlas(LoadSave.DEATH_SCREEN);
        imgW=(int)(img.getWidth()*Game.SCALE);
        imgH=(int)(img.getHeight()*Game.SCALE);
        imgX=Game.GAME_WIDTH/2-imgW/2;
        imgY=(int)(100*Game.SCALE);
    }

    public void draw(Graphics g){ //! Desenează overlay-ul
        g.setColor(new Color(0,0,0,200));
        g.fillRect(0,0, Game.GAME_WIDTH, Game.GAME_WIDTH);

        g.drawImage(img,imgX,imgY,imgW,imgH,null);
        menu.draw(g);
        play.draw(g);
    }
    public void keyPressed(KeyEvent e){ //! Metodă care se ocupă de apăsarea unei taste
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            playing.resetAll();
            Gamestate.state = Gamestate.MENU;
        }
    }
    public void update(){ //! Actualizează overlay-ul
        menu.update();
        play.update();
    }
    private boolean isIn(UrmButton b, MouseEvent e) { //! Verifică dacă cursorul este în buton
        return b.getBounds().contains(e.getX(), e.getY());

    }
    public void mouseMoved(MouseEvent e) { //! Metodă care se ocupă de mișcarea cursorului
        play.setMouseOver(false);
        menu.setMouseOver(false);

        if(isIn(menu,e))
            menu.setMouseOver(true);
        else if (isIn(play,e))
            play.setMouseOver(true);

    }
    public void mouseReleased(MouseEvent e) { //! Metodă care se ocupă de eliberarea butoanelor
        if(isIn(menu,e)) {
            if (menu.isMousePressed()){
                playing.resetAll();
                playing.setGameState(Gamestate.MENU);
                //playing.loadNextLevel();

            }

        }else if (isIn(play,e))
            if(play.isMousePressed()) {
                playing.resetAll();
                playing.getGame().getAudioPlayer().setLevelSong(playing.getLevelManager().getLevelIndex());
            }

        menu.resetBools();
        play.resetBools();
    }
    public void mousePressed(MouseEvent e){ //! Metodă care se ocupă de apăsarea butoanelor
        if(isIn(menu,e))
            menu.setMousePressed(true);
        else if (isIn(play,e))
            play.setMousePressed(true);
    }

}

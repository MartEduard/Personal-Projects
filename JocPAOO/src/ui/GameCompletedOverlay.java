package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import Gamestates.Gamestate;
import Gamestates.Playing;
import Levels.LevelManager;
import Main.Game;
import utils.DatabaseHelper;
import utils.LoadSave;

public class GameCompletedOverlay {
    private Playing playing;   //!< Referință către joc
    private BufferedImage img; //!< Imaginea de fundal
    private MenuButton quit, credit; //!< Butonul de quit și cel de credit
    private int imgX, imgY, imgW, imgH; //!< Poziția și dimensiunile imaginii

    public GameCompletedOverlay(Playing playing) { //! Constructor
        this.playing = playing;
        createImg();
        createButtons();

    }

    private void createButtons() { //! Creează butoanele
        quit = new MenuButton(Game.GAME_WIDTH / 2, (int) (270 * Game.SCALE), 3, Gamestate.MENU);
        credit = new MenuButton(Game.GAME_WIDTH / 2, (int) (200 * Game.SCALE), 2, Gamestate.CREDITS);
    }

    private void createImg() { //! Creează imaginea de fundal
        img = LoadSave.GetSpriteAtlas(LoadSave.GAME_COMPLETED);
        imgW = (int) (img.getWidth() * Game.SCALE);
        imgH = (int) (img.getHeight() * Game.SCALE);
        imgX = Game.GAME_WIDTH / 2 - imgW / 2;
        imgY = (int) (100 * Game.SCALE);

    }

    public void draw(Graphics g) { //! Desenează overlay-ul
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.drawImage(img, imgX, imgY, imgW, imgH, null);

        credit.draw(g);
        quit.draw(g);
    }

    public void update() { //! Actualizează overlay-ul
        credit.update();
        quit.update();


    }

    private boolean isIn(MenuButton b, MouseEvent e) { //! Verifică dacă cursorul este în buton
        return b.getBounds().contains(e.getX(), e.getY());
    }

    public void mouseMoved(MouseEvent e) { //! Metodă care se ocupă de mișcarea cursorului
        credit.setMouseOver(false);
        quit.setMouseOver(false);

        if (isIn(quit, e))
            quit.setMouseOver(true);
        else if (isIn(credit, e))
            credit.setMouseOver(true);
    }

    public void mouseReleased(MouseEvent e) { //! Metodă care se ocupă de eliberarea butoanelor
        if (isIn(quit, e)) {
            if (quit.isMousePressed()) {
                playing.resetAll();
                playing.resetGameCompleted();
                playing.setGamestate(Gamestate.MENU);
                DatabaseHelper.saveCurrentLevel(0);

            }
        } else if (isIn(credit, e))
            if (credit.isMousePressed()) {
                playing.resetAll();
                playing.resetGameCompleted();
                playing.setGamestate(Gamestate.CREDITS);
            }

        quit.resetBools();
        credit.resetBools();
    }

    public void mousePressed(MouseEvent e) { //! Metodă care se ocupă de apăsarea butoanelor
        if (isIn(quit, e))
            quit.setMousePressed(true);
        else if (isIn(credit, e))
            credit.setMousePressed(true);
    }
}
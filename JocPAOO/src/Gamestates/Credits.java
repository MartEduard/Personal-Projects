package Gamestates;

import Main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Credits extends State implements Statemethods {
    private BufferedImage backgroundImg; //! Imaginea de fundal
    public Credits(Game game) { //! Constructor
        super(game);
        loadImg();
    }

    private void loadImg() { //! Incarcă imaginea
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.CREDITS_BACKGROUND);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Graphics g) { //! Desenează imaginea de fundal
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) { //! Dacă se apasă tasta ESC, se revine la meniu
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            Gamestate.state = Gamestate.MENU;

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

package Gamestates;

import Main.Game;
import ui.AudioOptions;
import ui.PauseButton;
import ui.UrmButton;
import utils.LoadSave;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.*;

public class GameOptions extends State implements Statemethods {

    private AudioOptions audioOptions; //! Opțiunile audio
    private BufferedImage backgroundImg, optionsBackgroundImg; //! Imaginile de fundal
    private int bgX, bgY, bgW, bgH; //! Dimensiunile imaginilor de fundal
    private UrmButton menuB; //! Butonul de meniu

    public GameOptions(Game game) { //! Constructor
        super(game);
        loadImgs();
        loadButton();
        audioOptions = game.getAudioOptions();
    }

    private void loadButton() {
        int menuX = (int) (387 * Game.SCALE);
        int menuY = (int) (325 * Game.SCALE);

        menuB = new UrmButton(menuX, menuY, URM_SIZE, URM_SIZE, 2);
    }

    private void loadImgs() { //! Încarcă imaginile de fundal
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.OPTIONS_BACKGROUND);
        optionsBackgroundImg = LoadSave.GetSpriteAtlas(LoadSave.OPTIONS_MENU);

        bgW = (int) (optionsBackgroundImg.getWidth() * Game.SCALE);
        bgH = (int) (optionsBackgroundImg.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int) (33 * Game.SCALE);
    }

    @Override
    public void update() { //! Actualizează butoanele
        menuB.update();
        audioOptions.update();

    }

    @Override
    public void draw(Graphics g) { //! Desenează imaginile de fundal și butoanele
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(optionsBackgroundImg, bgX, bgY, bgW, bgH, null);

        menuB.draw(g);
        audioOptions.draw(g);

    }

    public void mouseDragged(MouseEvent e) { //! Dacă se trage de mouse, se actualizează poziția acestuia
        audioOptions.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) { //! Dacă se apasă pe mouse, se actualizează poziția acestuia
        if (isIn(e, menuB)) {
            menuB.setMousePressed(true);
        } else
            audioOptions.mousePressed(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) { //! Dacă se eliberează mouse-ul, se actualizează poziția acestuia
        if (isIn(e, menuB)) {
            if (menuB.isMousePressed())
                Gamestate.state = Gamestate.MENU;
        } else
            audioOptions.mouseReleased(e);

        menuB.resetBools();

    }

    @Override
    public void mouseMoved(MouseEvent e) { //! Dacă mouse-ul se mișcă, se actualizează poziția acestuia
        menuB.setMouseOver(false);

        if (isIn(e, menuB))
            menuB.setMouseOver(true);
        else
            audioOptions.mouseMoved(e);

    }

    @Override
    public void keyPressed(KeyEvent e) { //! Dacă se apasă tasta ESC, se revine la meniu
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            Gamestate.state = Gamestate.MENU;

    }

    @Override
    public void keyReleased(KeyEvent e) {


    }

    @Override
    public void mouseClicked(MouseEvent e) {


    }

    private boolean isIn(MouseEvent e, PauseButton b) { //! Verifică dacă mouse-ul este în buton
        return b.getBounds().contains(e.getX(), e.getY());
    }

}

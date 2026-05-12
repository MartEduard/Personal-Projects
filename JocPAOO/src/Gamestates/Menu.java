package Gamestates;

import Main.Game;
import ui.MenuButton;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Menu extends State implements Statemethods {
    private MenuButton[] buttons = new MenuButton[4]; //!<Un vector de butoane
    private BufferedImage backgroundImg, BgImage;  //!< Imaginile de fundal
    private int menuX, menuY, menuWidth, menuHeight;  //!< Dimensiunile imaginii de fundal

    public Menu(Game game) { //! Constructor
        super(game);
        loadButtons();
        loadBackground();
        BgImage = LoadSave.GetSpriteAtlas(LoadSave.FUNDAL);
    }

    private void loadBackground() { //! Încarcă imaginile de fundal
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
        menuWidth = (int)(backgroundImg.getWidth()*Game.SCALE);
        menuHeight = (int)(backgroundImg.getHeight()*Game.SCALE);
        menuX = Game.GAME_WIDTH/2 - menuWidth/2;
        menuY = (int)(1*Game.SCALE);
    }

    private void loadButtons() { //! Încarcă butoanele
        buttons[0] = new MenuButton(Game.GAME_WIDTH/2,(int)(120*Game.SCALE),0,Gamestate.PLAYING);
        buttons[1] = new MenuButton(Game.GAME_WIDTH/2,(int)(190*Game.SCALE),1,Gamestate.OPTIONS);
        buttons[2] = new MenuButton(Game.GAME_WIDTH/2,(int)(260*Game.SCALE),2,Gamestate.CREDITS);
        buttons[3] = new MenuButton(Game.GAME_WIDTH/2,(int)(330*Game.SCALE),3,Gamestate.QUIT);
    }

    @Override
    public void update() { //! Actualizează butoanele
        for(MenuButton mb: buttons){
            mb.update();
        }
    }

    @Override
    public void draw(Graphics g) { //! Desenează imaginea de fundal și butoanele
        g.drawImage(BgImage,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT,null);
        g.drawImage(backgroundImg,menuX,menuY,menuWidth,menuHeight,null);
        for(MenuButton mb: buttons){
            mb.draw(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) { //! Se ocupă de evenimentele de apăsare a mouse-ului
        for(MenuButton mb: buttons){
            if(isIn(e,mb)) {
                mb.setMousePressed(true);
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) { //! Se ocupă de evenimentele de eliberare a mouse-ului
        for(MenuButton mb: buttons){
            if(isIn(e,mb)){
                if(mb.isMousePressed())
                    mb.applyGamestate();
                if(mb.getState()==Gamestate.PLAYING)
                    game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLevelIndex());
                break;
            }
        }
        resetButtons();
    }

    private void resetButtons() { //! Resetează butoanele
        for(MenuButton mb: buttons){
            mb.resetBools();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) { //! Se ocupă de evenimentele de mișcare a mouse-ului
        for(MenuButton mb: buttons){
            mb.setMouseOver(false);
        }
        for(MenuButton mb: buttons){
            if(isIn(e,mb))
            {
                mb.setMouseOver(true);
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) { //! Dacă se apasă tasta ENTER, se trece la starea PLAYING
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            Gamestate.state = Gamestate.PLAYING;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
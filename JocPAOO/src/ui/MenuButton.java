package ui;
import Gamestates.Gamestate;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.Buttons.*;

public class MenuButton {
    private int xPos, yPos, rowIndex, index; //!< Poziția și indexul butonului
    private int xOffsetCenter = B_WIDTH / 2; //!< Offset-ul pentru centrarea butonului
    private Gamestate state; //!< Starea la care se duce jocul
    private BufferedImage[] imgs; //!< Imaginile butonului
    private boolean mouseOver, mousePressed;
    private Rectangle bounds; //!< Hitbox-ul butonului

    public Rectangle getBounds() { //!< Returnează hitbox-ul butonului
        return bounds;
    }

    public MenuButton(int xPos, int yPos, int rowIndex, Gamestate state) { //!< Constructor
        this.xPos = xPos;
        this.yPos = yPos;
        this.rowIndex = rowIndex;
        this.state = state;
        loadImgs();
        initBounds();
    }

    private void initBounds() { //!< Inițializează hitbox-ul
        bounds = new Rectangle(xPos-xOffsetCenter, yPos, B_WIDTH, B_HEIGHT);
    }

    private void loadImgs() { //!< Încarcă imaginile butonului
        imgs = new BufferedImage[3];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
        for(int i = 0; i < imgs.length; i++)
            imgs[i] = temp.getSubimage(i * B_WIDTH_DEFAULT, rowIndex*B_HEIGHT_DEFAULT, B_WIDTH_DEFAULT, B_HEIGHT_DEFAULT);
    }
    public void draw(Graphics g) { //!< Desenează butonul
        g.drawImage(imgs[index], xPos - xOffsetCenter, yPos, B_WIDTH,B_HEIGHT,null);
    }
    public void update(){ //!< Actualizează butonul
        index = 0;
        if(mouseOver)
            index = 1;
        if(mousePressed)
            index = 2;
    }
    public boolean isMouseOver() { //!< Verifică dacă cursorul este deasupra butonului
        return mouseOver;
    }
    public boolean isMousePressed() {   //!< Verifică dacă butonul este apăsat
        return mousePressed;
    }
    public void setMouseOver(boolean mouseOver) { //!< Setează starea de mouseOver
        this.mouseOver = mouseOver;
    }
    public void setMousePressed(boolean mousePressed) { //!< Setează starea de mousePressed
        this.mousePressed = mousePressed;
    }
    public void applyGamestate(){ //!< Aplică starea jocului
        Gamestate.state = state;
    }
    public void resetBools() { //!< Resetează stările butonului
        mouseOver = false;
        mousePressed = false;
    }
    public Gamestate getState(){ //!< Returnează starea butonului
        return state;
    }

}

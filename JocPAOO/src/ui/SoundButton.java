package ui;

import utils.LoadSave;
import static utils.Constants.UI.PauseButtons.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SoundButton extends PauseButton {
    private BufferedImage[][] soundImgs; //!< Imaginile butonului de sunet
    private boolean mouseOver,mousePressed; //!< Starea cursorului
    private boolean muted; //!< Starea sunetului
    private int rowIndex, colIndex; //!< Indicii pentru imaginea butonului
    public SoundButton(int x, int y, int width, int height) { //! Constructor
        super(x, y, width, height);

        loadSoundImgs();
    }

    private void loadSoundImgs() { //! Încarcă imaginile butonului de sunet
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SOUND_BUTTONS);
        soundImgs = new BufferedImage[2][3];
        for(int j = 0; j < soundImgs.length; j++)
            for(int i =0;i<soundImgs[j].length;i++)
                soundImgs[j][i] = temp.getSubimage(i*SOUND_SIZE_DEFAULT, j*SOUND_SIZE_DEFAULT, SOUND_SIZE_DEFAULT, SOUND_SIZE_DEFAULT);
    }
    public void update() { //! Actualizează butonul
        if(muted)
            rowIndex = 1;
        else
            rowIndex = 0;
        colIndex = 0;
        if(mouseOver)
            colIndex = 1;
        if(mousePressed)
            colIndex = 2;

    }
    public void resetBools() { //! Resetează stările
        mouseOver = false;
        mousePressed = false;
    }
    public void draw(Graphics g) { //! Desenează butonul
        g.drawImage(soundImgs[rowIndex][colIndex],x,y,width,height,null);
    }
    public boolean isMousePressed() { //! Verifică dacă butonul este apăsat
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) { //! Setează starea de apăsare
        this.mousePressed = mousePressed;
    }

    public boolean isMouseOver() { //! Verifică dacă cursorul este deasupra butonului
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) { //! Setează starea de mouseOver
        this.mouseOver = mouseOver;
    }

    public boolean isMuted() {  //! Verifică dacă sunetul este oprit
        return muted;
    }

    public void setMuted(boolean muted) { //! Setează starea sunetului
        this.muted = muted;
    }
}

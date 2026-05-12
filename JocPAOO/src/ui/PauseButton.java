package ui;

import java.awt.*;

public class PauseButton {
    protected int x,y,width,height; //!< Poziția și dimensiunile butonului
    protected Rectangle bounds; //!< Hitbox-ul butonului

    public PauseButton(int x, int y, int width, int height) { //! Constructor
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        createBounds();
    }

    private void createBounds() { //! Creează hitbox-ul
        bounds = new Rectangle(x, y, width, height);
    }

    public int getX() { //! Returnează poziția x
        return x;
    }

    public void setX(int x) {   //! Setează poziția x
        this.x = x;
    }

    public int getY() { //! Returnează poziția y
        return y;
    }

    public void setY(int y) {  //! Setează poziția y
        this.y = y;
    }

    public int getWidth() { //! Returnează lățimea
        return width;
    }

    public void setWidth(int width) { //! Setează lățimea
        this.width = width;
    }



    public Rectangle getBounds() { //! Returnează hitbox-ul
        return bounds;
    }

    public void setBounds(Rectangle bounds) { //! Setează hitbox-ul
        this.bounds = bounds;
    }
}

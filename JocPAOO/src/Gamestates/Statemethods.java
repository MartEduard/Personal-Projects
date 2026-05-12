package Gamestates;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface Statemethods {
    void update(); //! Actualizează starea
    void draw(Graphics g); //! Desenează starea
    void mouseClicked(MouseEvent e); //! Metode pentru evenimentele mouse și tastatură
    void mousePressed(MouseEvent e); //! Metode pentru evenimentele mouse și tastatură
    void mouseReleased(MouseEvent e); //! Metode pentru evenimentele mouse și tastatură
    void mouseMoved(MouseEvent e);  //! Metode pentru evenimentele mouse și tastatură
    void keyPressed(KeyEvent e); //! Metode pentru evenimentele mouse și tastatură
    void keyReleased(KeyEvent e); //! Metode pentru evenimentele mouse și tastatură
}

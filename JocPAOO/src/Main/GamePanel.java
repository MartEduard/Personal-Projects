package Main;
import Input.*;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import static Main.Game.GAME_HEIGHT;
import static Main.Game.GAME_WIDTH;


public class GamePanel extends JPanel
{
    private MouseInputs mouseInputs; //!< Inputurile mouse-ului
    private Game game; //!< Jocul


    public GamePanel(Game game) { //! Constructor
        mouseInputs = new MouseInputs(this);
        this.game = game;
        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs); //click-uri
        addMouseMotionListener(mouseInputs); // miscari ale mouse-ului
    }
    private void setPanelSize() { //! Setează dimensiunile panoului
        Dimension size = new Dimension(GAME_WIDTH,GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println("size: " + GAME_WIDTH + " : " + GAME_HEIGHT);
    }


//    public void updateGame() {
//        // TODO
//    }
    public void paintComponent(Graphics g) { //! Desenează componenta
        super.paintComponent(g); //! apelam constructorul lui JFrame
        game.render(g);
    }
    public Game getGame(){ //! Returnează jocul
        return game;
    }
}

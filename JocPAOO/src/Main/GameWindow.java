package Main;
import javax.swing.JFrame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;


public class GameWindow
{
    private JFrame jframe; //!< Fereastra jocului
    public GameWindow(GamePanel gamePanel) {  //! Constructor
        jframe = new JFrame("Amenințarea Confederației"); // nou obiect tip JFrame

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cand ies din joc, se incheie si rularea din IntelliJ
        jframe.add(gamePanel); // pentru a folosi Panel-urile din joc
        jframe.pack(); // va folosi dimensiunile precizate in GamePanel
        jframe.setLocationRelativeTo(null); // Jocul se va deschide in mijlocul ecranului
        jframe.setResizable(false); // rezolutia nu mai poate fi schimbata
        jframe.setVisible(true); // Se foloseste pentru a porni panel-ul
        jframe.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }
        });
    }

}

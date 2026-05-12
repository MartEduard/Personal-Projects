package Main;
import Gamestates.*;

import java.awt.Graphics;

import audio.AudioPlayer;
import ui.AudioOptions;
import utils.DatabaseHelper;
import utils.LoadSave;

import java.lang.Runnable;
import java.sql.Connection;
import java.sql.SQLException;

/** Clasa publica Game implementeaza interfata publica Runnable
 *
 */
public class Game implements Runnable
{

    private AudioOptions audioOptions;
    private final GameWindow gameWindow; //!< Fereastra jocului
    private final GamePanel gamePanel; //!< Rama jocului
    private Thread gameThread; //!< Thread-ul jocului
    private Credits credits;
    private final int FPS_SET = 120; //!< FPS-urile jocului
    private final int UPS_SET = 200; //!< UPS-urile jocului ( Update per second) .
    private Playing playing; //!< Starea jocului
    private Menu menu; //!< Meniul jocului
    private GameOptions gameOptions; //!< Optiunile jocului
    private AudioPlayer audioPlayer; //!< Player-ul audio



    public final static int TILES_DEFAULT_SIZE = 32; //!< Dimensiunea implicita a unei dale
    public final static float SCALE = 2f; //!< Scara jocului
    public final static int TILES_IN_WIDTH =  26; //!< Numarul minim de dale pe axa X
    public final static int TILES_IN_HEIGHT =  14; //!< Numarul constant de dale pe axa Y
    public final static int TILES_SIZE = (int)(TILES_DEFAULT_SIZE * SCALE); //!< Dimensiunea unei dale
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH; //!< Latimea jocului
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT; //!< Inaltimea jocului

    private final boolean SHOW_FPS_UPS = true; //!< Afiseaza FPS-urile si UPS-urile jocului
    public Game() { //!< Constructor

        LoadSave.GetAllLevels();
        initClasses();

        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        startGameLoop();

    }

    private void initClasses()  { //!< Initializeaza clasele
        audioOptions = new AudioOptions(this);
        audioPlayer = new AudioPlayer();
        menu = new Menu(this);
        playing = new Playing(this);
        credits = new Credits(this);
        gameOptions = new GameOptions(this);



    }

    private void startGameLoop(){ //!< Porneste bucla jocului
        gameThread = new Thread(this);
        gameThread.start();
}
    public void update() { //!< Actualizeaza starea jocului

        switch(Gamestate.state){
            case MENU:
                menu.update();
                break;
            case PLAYING:
                playing.update();
                break;
            case OPTIONS:
                gameOptions.update();
                break;
            case CREDITS:
                credits.update();
                break;
            case QUIT:
            default:
                System.exit(0);
                break;
        }
    }
    public void render(Graphics g){ //!< Deseneaza starea jocului
        switch(Gamestate.state){
            case MENU:
                menu.draw(g);
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case OPTIONS:
                gameOptions.draw(g);
                break;
            case CREDITS:
                credits.draw(g);
                break;
            default:
                break;
        }

    }
    @Override
    public void run() { //!< Ruleaza jocul
        double timePerFrame = 1_000_000_000.0 / FPS_SET; // nano secunde
        double timePerUpdate = 1_000_000_000.0 / UPS_SET;
        long previousTime = System.nanoTime();
        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();
        double deltaU = 0;
        double deltaF = 0;

        while(true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime-previousTime)/timePerUpdate; //deltaU va fi 1.0 sau mai mult cand durata ultimei actualizari este egala sau mai mare decat timePerUpdate
            deltaF += (currentTime-previousTime)/timePerFrame;
            previousTime = currentTime;
            if(deltaU >=1){
                update();
                updates++;
                deltaU--;
            }
            if(deltaF >= 1){
                gamePanel.repaint();
                frames++;
                deltaF--;
            }
            if(SHOW_FPS_UPS)
                if(System.currentTimeMillis() - lastCheck >= 1000) { // FPS
                    lastCheck = System.currentTimeMillis();
                    System.out.println("FPS: " +  frames + " | UPS: " + updates);
                    frames = 0;
                    updates=0;
                }
        }
    }
    public void windowFocusLost() { //!< Daca fereastra jocului nu este in focus, jucatorul se opreste din mers
        if(Gamestate.state == Gamestate.PLAYING)
        {
            playing.getPlayer().resetDirBooleans();
        }
    }
    public Menu getMenu() { //!< Returneaza meniul jocului
        return menu;
    }
    public Playing getPlaying(){ //!< Returneaza starea jocului
        return playing;
    }
    public AudioOptions getAudioOptions(){ //!< Returneaza optiunile audio
        return audioOptions;
    }
    public GameOptions getGameOptions(){ //!< Returneaza optiunile jocului
        return gameOptions;
    }
    public AudioPlayer getAudioPlayer(){ //!< Returneaza player-ul audio
        return audioPlayer;
    }
    public Credits getCredits(){ //!< Returneaza credits
        return credits;
    }
}

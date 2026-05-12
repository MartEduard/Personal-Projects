package Gamestates;

import Entities.EnemyManager;
import Entities.Player;
import Levels.Level;
import Levels.LevelManager;
import Main.Game;
import objects.ObjectManager;
import ui.GameCompletedOverlay;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utils.DatabaseHelper;
import utils.LoadSave;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static utils.Constants.UI.Environment.*;


public class Playing extends State implements Statemethods {
    private Player player; //!< Jucătorul
    private LevelManager levelManager; //!< Managerul de nivel
    private EnemyManager enemyManager; //!< Managerul de inamici
    private ObjectManager objectManager; //!< Managerul de obiecte
    private PauseOverlay pauseOverlay; //!< Ecranul de pauză
    private GameOverOverlay gameOverOverlay; //!< Ecranul de game over
    private LevelCompletedOverlay levelCompletedOverlay; //!< Ecranul de nivel completat
    private GameCompletedOverlay gameCompletedOverlay;  //!< Ecranul de joc completat
    private boolean paused = false; //!< Dacă jocul este în pauză


    private int xLvlOffset; //!< Offset-ul nivelului pe axa x
    private int leftBorder = (int)(0.25*Game.GAME_WIDTH); //!< Marginea stângă
    private int rightBorder = (int)(0.75*Game.GAME_WIDTH); //!< Marginea dreaptă
    private int maxLvlOffsetX; //!< Offset-ul maxim pe axa x

    private BufferedImage background, middleground, extensionGreen, smallCloud; //! Imaginile de fundal
    private int[] smallCloudsPos; //!< Pozițiile norilor mici
    private Random rnd = new Random(); //!< Random

    private boolean gameOver; //!< Dacă jocul s-a terminat
    private boolean lvlCompleted; //!< Dacă nivelul s-a terminat
    private boolean gameCompleted; //!< Dacă jocul s-a terminat

    private boolean playerDying; //!< Dacă jucătorul moare


    public Playing(Game game) { //! Constructor
        super(game);
        initClasses();
        loadBackground();

        calcLvlOffset();
        loadStartLevel();

    }
    public void loadNextLevel() { //! Încarcă următorul nivel
        levelManager.setLevelIndex(levelManager.getLevelIndex()+1);
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        resetAll();
        DatabaseHelper.saveCurrentLevel(levelManager.getLevelIndex());
    }

    private void loadStartLevel() { //! Încarcă nivelul de start
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
        objectManager.loadObjects(levelManager.getCurrentLevel());
    }

    private void calcLvlOffset() { //! Calculează offset-ul nivelului
        maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
    }
    public void loadBackground(){ //! Încarcă imaginile de fundal
        background = LoadSave.GetSpriteAtlas(LoadSave.BG_IMAGE);
        middleground = LoadSave.GetSpriteAtlas(LoadSave.MIDDLEGROUND);
        extensionGreen = LoadSave.GetSpriteAtlas(LoadSave.EXTENSION_GREEN);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        for(int i = 0; i < smallCloudsPos.length; i++){
            smallCloudsPos[i] = (int)(90*Game.SCALE)+ rnd.nextInt((int)(100*Game.SCALE));
        }
    }

    private void initClasses() { //! Inițializează clasele
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        objectManager = new ObjectManager(this);
        player = new Player(200,200,(int)(90*Game.SCALE),(int)(60*Game.SCALE),this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);
        gameCompletedOverlay= new GameCompletedOverlay(this);


    }
    public void windowFocusLost() { //! Dacă fereastra pierde focus-ul
        player.resetDirBooleans();
    }
    public void unpauseGame(){ //! Dacă jocul este repornit
        paused = false;
    }
    public Player getPlayer(){ //! Returnează jucătorul
        return player;
    }

    @Override
    public void update() { //! Actualizează jocul

        if(paused) {
            pauseOverlay.update();
        } else if(gameCompleted) {
            gameCompletedOverlay.update();
        } else if(lvlCompleted){
            levelCompletedOverlay.update();
        } else if(gameOver){
            gameOverOverlay.update();
        } else if(playerDying){
            player.update();
        }else  {
            levelManager.update();
            objectManager.update(levelManager.getCurrentLevel().getLevelData(),player);
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData());
            checkCloseToBorder();
        }



    }



    @Override
    public void draw(Graphics g) { //! Desenează jocul
        g.drawImage(background,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT,null);
        drawForest(g);
        drawClouds(g);

        levelManager.draw(g, xLvlOffset);
        objectManager.draw(g,xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);
        if(paused) {
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver) {
            gameOverOverlay.draw(g);

        } else if(lvlCompleted) {
            levelCompletedOverlay.draw(g);
        } else if (gameCompleted)
            gameCompletedOverlay.draw(g);

    }

    private void drawForest(Graphics g){ //! Desenează pădurea
        for(int i=0;i<10;i++) {
            g.drawImage(middleground, i*MIDDLEGROUND_WIDTH-(int)(xLvlOffset*0.2), (int) (110 * Game.SCALE), MIDDLEGROUND_WIDTH, MIDDLEGROUND_HEIGHT, null);
            g.drawImage(extensionGreen, i*MIDDLEGROUND_WIDTH, (int) (320 * Game.SCALE), MIDDLEGROUND_WIDTH, MIDDLEGROUND_HEIGHT, null);
        }
    }
    private void drawClouds(Graphics g){ //! Desenează norii
        for(int i=0;i<smallCloudsPos.length;i++) {
            g.drawImage(smallCloud, SMALL_CLOUDS_WIDTH*4*i-(int)(xLvlOffset*0.7), smallCloudsPos[i], SMALL_CLOUDS_WIDTH, SMALL_CLOUDS_HEIGHT, null);
        }
    }
    private void checkCloseToBorder() { //! Verifică dacă jucătorul este aproape de margine
        int playerX = (int)(player.getHitbox().x);
        int diff = playerX - xLvlOffset;
        if(diff > rightBorder)
            xLvlOffset += diff-rightBorder;
        else if(diff < leftBorder)
            xLvlOffset += diff-leftBorder;

        xLvlOffset = Math.max(Math.min(xLvlOffset,maxLvlOffsetX),0);

    }
    public void resetAll() { //! Resetează jocul
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        player.resetAll();
        playerDying = false;
        enemyManager.resetAllEnemies();
        objectManager.resetAllObjects();


    }
    public void setGameCompleted(){
        gameCompleted=true;
    }
    public void resetGameCompleted(){ //! Resetează jocul completat
        gameCompleted = false;
    }
    public void setGameOver(boolean gameOver){ //! Setează game over
        this.gameOver=gameOver;
    }
    public void checkEnemyHit(Rectangle2D.Float attackBox) { //! Verifică dacă inamicul a fost lovit
        enemyManager.checkEnemyHit(attackBox);
    }
    public EnemyManager getEnemyManager(){ //! Returnează managerul de inamici
        return enemyManager;
    }
    public void setMaxLvlOffset(int lvlOffset){ //! Setează offset-ul maxim
        this.maxLvlOffsetX=lvlOffset;
    }

    public void setLevelCompleted(boolean levelCompleted){ //! Setează nivelul completat
        game.getAudioPlayer().lvlCompleted();
        if (levelManager.getLevelIndex() + 1 >= levelManager.getAmountOfLevels()) {
            // No more levels
            gameCompleted = true;
            levelManager.setLevelIndex(0);
            levelManager.loadNextLevel();
            resetAll();
            return;
        }
        this.lvlCompleted = levelCompleted;
    }
    public ObjectManager getObjectManager(){ //! Returnează managerul de obiecte
        return objectManager;
    }
    public void checkPotionTouched(Rectangle2D.Float hitbox) { //! Verifică dacă poțiunea a fost luată
        objectManager.checkObjectTouched(hitbox);
    }
    public void checkObjectHit(Rectangle2D.Float attackBox) { //! Verifică dacă obiectul a fost lovit
        objectManager.checkObjectHit(attackBox);
    }
    public void checkSpikesTouched(Player player) { //! Verifică dacă jucătorul a fost lovit de țepi
        objectManager.checkSpikesTouched(player);
    }
    public LevelManager getLevelManager(){ //! Returnează managerul de nivel
        return levelManager;
    }


    @Override
    public void mouseClicked(MouseEvent e) { //! Dacă se face click
        if (!gameOver)
            if(e.getButton() == MouseEvent.BUTTON1)
                player.setAttacking(true);
    }

    @Override
    public void mousePressed(MouseEvent e) { //! Dacă se apasă pe mouse
        if (gameOver)
            gameOverOverlay.mousePressed(e);
        else if (paused)
            pauseOverlay.mousePressed(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mousePressed(e);
        else if (gameCompleted)
            gameCompletedOverlay.mousePressed(e);



    }

    @Override
    public void mouseReleased(MouseEvent e) { //! Dacă se eliberează mouse-ul
        if (gameOver)
            gameOverOverlay.mouseReleased(e);
        else if (paused)
            pauseOverlay.mouseReleased(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mouseReleased(e);
        else if (gameCompleted)
            gameCompletedOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) { //! Dacă mouse-ul se mișcă
        if (gameOver)
            gameOverOverlay.mouseMoved(e);
        else if (paused)
            pauseOverlay.mouseMoved(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mouseMoved(e);
        else if (gameCompleted)
            gameCompletedOverlay.mouseMoved(e);
    }

    @Override
    public void keyPressed(KeyEvent e) { //! Dacă se apasă o tastă
        if (!gameOver && !gameCompleted && !lvlCompleted)
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_W:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);
                    break;
                case KeyEvent.VK_F:
                    player.powerAttack();
                    break;
                case KeyEvent.VK_SHIFT:
                    player.setPlayerSpeed(1.5f);
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = !paused;
                    break;
            }
    }

    public void mouseDragged(MouseEvent e) { //! Dacă se trage de mouse
        if(!gameOver  && !gameCompleted && !lvlCompleted)
            if(paused) {
                pauseOverlay.mouseDragged(e);
            }
    }

    @Override
    public void keyReleased(KeyEvent e) { //! Dacă se eliberează o tastă
        if(!gameOver  && !gameCompleted && !lvlCompleted)
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_W:
                    player.setJump(false);
                    break;
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_SHIFT:
                    player.setPlayerSpeed(1f);
                    break;
            }

    }


    public void setPlayerDying(boolean playerDying) { //! Setează dacă jucătorul moare
        this.playerDying = playerDying;
    }

    public void setGamestate(Gamestate gamestate) {  //! Setează starea jocului
        Gamestate.state = gamestate;
    }
}

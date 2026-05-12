package Entities;

import Gamestates.Playing;
import Levels.Level;
import utils.LoadSave;

import static utils.Constants.EnemyConstants.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public class EnemyManager {

    private Playing playing; //!< Referință către obiectul Playing
    private BufferedImage[][] orangeRobotArr; //!< Matricea de imagini pentru robotul portocaliu
    private BufferedImage[][] greenRobotArr; //!< Matricea de imagini pentru robotul verde
    private BufferedImage[][] purpleRobotArr; //!< Matricea de imagini pentru robotul mov
    private Level currentLevel; //!< Nivelul curent

    public EnemyManager(Playing playing){ //! Constructor
        this.playing = playing;
        loadEnemyImgs();
    }


    public void loadEnemies(Level level) { //! Încarcă inamicii pentru nivelul dat
        this.currentLevel = level;
    }

    public void update(int[][] lvlData){ //! Actualizează starea inamicilor
        boolean isAnyActive = false;
        for(OrangeRobot oR : currentLevel.getOrangeRobots()){
            if(oR.isActive()) {
                oR.update(lvlData, playing);
                isAnyActive = true;
            }
        }
        for(GreenRobot gR : currentLevel.getGreenRobots()){
            if(gR.isActive()) {
                gR.update(lvlData, playing);
                isAnyActive = true;
            }
        }
        for(PurpleRobot pR : currentLevel.getPurpleRobots()){
            if(pR.isActive()) {
                pR.update(lvlData, playing);
                isAnyActive = true;
            }
        }

        if(!isAnyActive){
            playing.setLevelCompleted(true);

        }
    }
    public void draw(Graphics g, int xLvlOffset){ //! Desenează inamicii
        drawOrangeRobots(g, xLvlOffset);
        drawGreenRobots(g, xLvlOffset);
        drawPurpleRobots(g, xLvlOffset);
    }

    private void drawOrangeRobots(Graphics g, int xLvlOffset) { //! Desenează roboții portocalii
        for(OrangeRobot oR : currentLevel.getOrangeRobots()){
            if(oR.isActive()) {
                int drawOffset = 0;
                if(oR.getState() == HIT)
                    drawOffset = (int)oR.getPushDrawOffset();
                g.drawImage(orangeRobotArr[oR.getState()][oR.getAniIndex()], (int) (oR.getHitbox().x) - ROBOT_DRAWOFFSET_X - xLvlOffset + oR.flipX(),
                        (int) oR.getHitbox().y - ROBOT_DRAWOFFSET_Y + drawOffset, ROBOT_SIZE * oR.flipW(), ROBOT_SIZE, null); // +(int)oR.getPushDrawOffset() la y
                //oR.drawAttackBox(g, xLvlOffset);
            }
        }
    }
    private void drawGreenRobots(Graphics g, int xLvlOffset) { //! Desenează roboții verzi
        for(GreenRobot gR : currentLevel.getGreenRobots()){
            if(gR.isActive()) {
                int drawOffset = 0;
                if(gR.getState() == HIT)
                    drawOffset = (int)gR.getPushDrawOffset();
                g.drawImage(greenRobotArr[gR.getState()][gR.getAniIndex()], (int) (gR.getHitbox().x) - ROBOT_DRAWOFFSET_X - xLvlOffset + gR.flipX(),
                        (int) gR.getHitbox().y - ROBOT_DRAWOFFSET_Y + drawOffset, ROBOT_SIZE * gR.flipW(), ROBOT_SIZE, null); // +(int)oR.getPushDrawOffset() la y
                //oR.drawAttackBox(g, xLvlOffset);
            }
        }
    }
    private void drawPurpleRobots(Graphics g, int xLvlOffset) { //! Desenează roboții mov
        for(PurpleRobot pR : currentLevel.getPurpleRobots()){
            if(pR.isActive()) {
                int drawOffset = 0;
                if(pR.getState() == HIT)
                    drawOffset = (int)pR.getPushDrawOffset();
                g.drawImage(purpleRobotArr[pR.getState()][pR.getAniIndex()], (int) (pR.getHitbox().x) - ROBOT_DRAWOFFSET_X - xLvlOffset + pR.flipX(),
                        (int) pR.getHitbox().y - ROBOT_DRAWOFFSET_Y + drawOffset, ROBOT_SIZE * pR.flipW(), ROBOT_SIZE, null); // +(int)oR.getPushDrawOffset() la y
                //oR.drawAttackBox(g, xLvlOffset);
            }
        }
    }
    public void checkEnemyHit(Rectangle2D.Float attackBox){ //! Verifică dacă un inamic a fost lovit
        for(OrangeRobot oR : currentLevel.getOrangeRobots()){

            if(oR.isActive()){
                if(oR.getState() != DEATH && oR.getState() != HIT)
                    if(attackBox.intersects(oR.getHitbox())) {
                        oR.hurt(20);
                        return;
                }
            }
        }
        for(GreenRobot gR : currentLevel.getGreenRobots()){

            if(gR.isActive()){
                if(gR.getState() != DEATH && gR.getState() != HIT)
                    if(attackBox.intersects(gR.getHitbox())) {
                        gR.hurt(18);
                        return;
                    }
            }
        }
        for(PurpleRobot pR : currentLevel.getPurpleRobots()){

            if(pR.isActive()){
                if(pR.getState() != DEATH && pR.getState() != HIT)
                    if(attackBox.intersects(pR.getHitbox())) {
                        pR.hurt(15);
                        return;
                    }
            }
        }
    }

    private void loadEnemyImgs()  {  //! Încarcă imaginile pentru inamici
        orangeRobotArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.ORANGE_ROBOT),6,5, ROBOT_SIZE_DEFAULT, ROBOT_SIZE_DEFAULT);
        greenRobotArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.GREEN_ROBOT),6,5, ROBOT_SIZE_DEFAULT, ROBOT_SIZE_DEFAULT);
        purpleRobotArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.PURPLE_ROBOT),6,5, ROBOT_SIZE_DEFAULT, ROBOT_SIZE_DEFAULT);
    }
    private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) { //! Obține o matrice de imagini dintr-un atlas
        BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
        for (int j = 0; j < tempArr.length; j++)
            for (int i = 0; i < tempArr[j].length; i++)
                tempArr[j][i] = atlas.getSubimage(i * spriteW, j * spriteH, spriteW, spriteH);
        return tempArr;
    }
    public void resetAllEnemies(){ //! Resetează toți inamicii
        for(OrangeRobot oR : currentLevel.getOrangeRobots())
            oR.resetEnemy();
        for(GreenRobot gR : currentLevel.getGreenRobots())
            gR.resetEnemy();
        for(PurpleRobot pR : currentLevel.getPurpleRobots())
            pR.resetEnemy();

    }
}

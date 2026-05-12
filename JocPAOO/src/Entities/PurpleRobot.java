package Entities;

import Gamestates.Playing;
import Main.Game;

import java.awt.geom.Rectangle2D;

import static utils.Constants.Directions.*;
import static utils.Constants.EnemyConstants.*;
import static utils.HelpMethods.*;

public class PurpleRobot extends Enemy {
    public PurpleRobot(float x, float y) { //! Constructor
        super(x, y, ROBOT_SIZE, ROBOT_SIZE, PURPLE_ROBOT);
        initHitbox(15,17);
        initAttackBox();
    }

    private void initAttackBox() { //! Inițializează cutia de atac
        attackBox = new Rectangle2D.Float(x,y,(int)(20* Game.SCALE),(int)(17* Game.SCALE));
    }

    public void update(int[][] lvlData, Playing playing){ //! Actualizează starea robotului verde
        updateBehavior(lvlData,playing);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox(){ //! Actualizează cutia de atac
        if(walkDir==RIGHT){
            attackBox.x=hitbox.x+hitbox.width+(int)(Game.SCALE*12)- ROBOT_DRAWOFFSET_X;
        }else if (walkDir==LEFT){
            attackBox.x=hitbox.x-hitbox.width-(int)(Game.SCALE*15)+ ROBOT_DRAWOFFSET_X;
        }
        attackBox.y = hitbox.y + (int)(Game.SCALE*10)- ROBOT_DRAWOFFSET_Y;
    }

    private void updateBehavior(int[][] lvlData, Playing playing) { //! Actualizează comportamentul robotului verde
        if(firstUpdate)
            firstUpdateCheck(lvlData);

        if(inAir)
            inAirChecks(lvlData,playing);
        else {
            switch(state){
                case IDLE:
                    if (IsFloor(hitbox, lvlData))
                        newState(RUNNING);
                    else
                        inAir = true;
                    break;
                case RUNNING:
                    if(canSeePlayer(lvlData,playing.getPlayer())) {
                        turnTowardsPlayer(playing.getPlayer());
                        if (isPlayerCloseForAttack(playing.getPlayer()))
                            newState(ATTACK);
                    }

                    move(lvlData);
                    break;
                case ATTACK:
                    if(aniIndex ==0)
                        attackChecked = false;
                    if(aniIndex == 1 && !attackChecked)
                        checkPlayerHit(attackBox,playing.getPlayer());
                    break;
                case HIT:
                    if (aniIndex <= GetSpriteAmount(enemyType, state)-2)
                        pushBack(pushBackDir, lvlData, 2f);
                    updatePushBackDrawOffset();
                    break;
            }
        }
    }
}
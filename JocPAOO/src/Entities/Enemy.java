package Entities;
import Gamestates.Playing;
import Main.Game;

import java.awt.geom.Rectangle2D;

import static utils.Constants.*;
import static utils.Constants.EnemyConstants.*;
import static utils.HelpMethods.*;
import static utils.Constants.Directions.*;

public abstract class Enemy extends Entity {
    protected int enemyType; //!< Tipul inamicului
    protected boolean firstUpdate = true; //!< Verifică dacă este prima actualizare
    protected int walkDir = LEFT; //!< Direcția de mers
    protected int tileY; //!< Poziția pe axa Y
    protected float attackDistance = Game.TILES_SIZE; //!< Distanța de atac
    protected boolean active = true; //!< Starea de activitate a inamicului
    protected boolean attackChecked; //!< Verifică dacă atacul a fost efectuat

    public Enemy(float x, float y, int width, int height, int enemyType) { //! Constructor
        super(x, y, width, height);
        this.enemyType=enemyType;
        maxHealth=GetMaxHealth(enemyType);
        currentHealth=maxHealth;
        walkSpeed = Game.SCALE*0.35f;
    }
    protected void firstUpdateCheck(int[][] lvlData) { //! Verifică dacă este prima actualizare
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
        firstUpdate = false;
    }
    protected void updateInAir(int[][] lvlData) { //! Actualizează starea de "în aer"
        if(CanMoveHere(hitbox.x,hitbox.y + airSpeed,hitbox.width,hitbox.height,lvlData)) {
            hitbox.y += airSpeed;
            airSpeed += GRAVITY;
        } else {
            inAir = false;
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox,airSpeed);
            tileY = (int)(hitbox.y/Game.TILES_SIZE);
        }
    }
    protected void inAirChecks(int[][] lvlData, Playing playing) { //! Verifică dacă inamicul este în aer
        if (state != HIT && state != DEATH) {
            updateInAir(lvlData);
            playing.getObjectManager().checkSpikesTouched(this);

        }
    }
    public int flipX() { //! Întoarce offset-ul x al inamicului
        if(walkDir==RIGHT)
            return width;
        else return 0;
    }
    public int flipW() { //! Întoarce  latimea inamicul
        if(walkDir==RIGHT)
            return -1;
        else return 1;

    }
    protected void move(int[][] lvlData){ //! Mișcă inamicul
        float xSpeed = 0;
        if(walkDir == LEFT)
            xSpeed = -walkSpeed;
        else
            xSpeed = walkSpeed;
        if(CanMoveHere(hitbox.x+xSpeed,hitbox.y,hitbox.width,hitbox.height,lvlData))
            if(IsFloor(hitbox,xSpeed,lvlData)) {
                hitbox.x += xSpeed;
                return;
            }

        changeWalkDir();

    }
    protected void turnTowardsPlayer(Player player){ //! Întoarce inamicul către jucător
        if(player.hitbox.x>hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }
    protected boolean canSeePlayer(int[][] lvlData, Player player){ //! Verifică dacă inamicul poate vedea jucătorul
        int playerTileY = (int)(player.getHitbox().y / Game.TILES_SIZE);
        if(playerTileY==tileY)
            if(isPlayerInRange(player))
                if(IsSightClear(lvlData,hitbox,player.hitbox,tileY))
                    return true;
        return false;

    }

    protected boolean isPlayerInRange(Player player) { //! Verifică dacă jucătorul este în raza de acțiune
        int absValue = (int)Math.abs(player.hitbox.x -hitbox.x);
        return absValue <= attackDistance*5;
    }
    protected boolean isPlayerCloseForAttack(Player player) { //! Verifică dacă jucătorul este suficient de aproape pentru a fi atacat
        int absValue = (int)Math.abs(player.hitbox.x -hitbox.x);
        switch(enemyType){
            case ORANGE_ROBOT,GREEN_ROBOT,PURPLE_ROBOT -> {
                return absValue <= attackDistance;
            }

        }
        return false;
    }

    public void hurt(int amount) { //! Rănește inamicul
        currentHealth -= amount;
        if (currentHealth <= 0)
            newState(DEATH);
        else {
            newState(HIT);
            if (walkDir == LEFT)
                pushBackDir = RIGHT;
            else
                pushBackDir = LEFT;
            pushBackOffsetDir = UP;
            pushDrawOffset = 0;
        }
    }
    protected void checkPlayerHit(Rectangle2D.Float attackBox, Player player){ //! Verifică dacă jucătorul a fost lovit
        if(attackBox.intersects(player.hitbox))
            player.changeHealth(-GetEnemyDmg(enemyType),this);
        attackChecked = true;
    }


    protected void updateAnimationTick() { //! Actualizează animația
        aniTick++;
        if(aniTick>=ANI_SPEED) {
            aniTick=0;
            aniIndex++;
            if(aniIndex>=GetSpriteAmount(enemyType,state))
                if (enemyType == ORANGE_ROBOT || enemyType == GREEN_ROBOT || enemyType == PURPLE_ROBOT){
                    aniIndex = 0;

                    switch (state) {
                        case ATTACK, HIT -> state = IDLE;
                        case DEATH -> active = false;
                    }

                }
        }
    }



    protected void changeWalkDir() { //! Schimbă direcția de mers
        if(walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }



    public boolean isActive(){ //! Verifică dacă inamicul este activ
        return active;
    }
    public void resetEnemy(){ //! Resetează inamicul
        hitbox.x=x;
        hitbox.y=y;
        firstUpdate=true;
        currentHealth=maxHealth;
        newState(IDLE);
        active=true;
        airSpeed=0;
        pushDrawOffset =0;
    }
    public float getPushDrawOffset() { //! Obține offset-ul de împingere
        return pushDrawOffset;
    }

}
package Entities;

import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static utils.Constants.Directions.*;
import static utils.HelpMethods.CanMoveHere;

public abstract class Entity {
    protected float x, y; //!< Coordonatele entității
    protected int width, height; //!< Lățimea și înălțimea entității
    protected Rectangle2D.Float hitbox; //!< Hitbox-ul entității
    protected int aniTick, aniIndex; //!< Variabile pentru animație
    protected int state; //!< Starea entității
    protected float airSpeed; //!< Viteza în aer a entității
    protected boolean inAir = false; //!< Verifică dacă entitatea este în aer
    protected int maxHealth; //!< Sănătatea maximă a entității
    protected int currentHealth; //!< Sănătatea curentă a entității
    protected Rectangle2D.Float attackBox; //!< Cutia de atac a entității
    protected float walkSpeed = 1.0f * Game.SCALE; //!< Viteza de mers a entității

    protected int pushBackDir; //!< Direcția de împingere
    protected float pushDrawOffset; //!< Offset-ul de desenare la împingere
    protected int pushBackOffsetDir = UP; //!< Direcția offset-ului de împingere

    public Entity(float x, float y,int width, int height) { //! Constructor
        this.x=x;
        this.y=y;
        this.width = width;
        this.height = height;
        initHitbox(width,height);
    }
    protected void drawHitbox(Graphics g, int xLvlOffset){ //! Desenează hitbox-ul
        g.setColor(Color.PINK);
        g.drawRect((int)hitbox.x-xLvlOffset, (int)hitbox.y, (int)hitbox.width,(int)hitbox.height);
    }
    protected void drawAttackBox(Graphics g, int lvlOffsetX) { //! Desenează cutia de atac
        g.setColor(Color.RED);
        g.drawRect((int)(attackBox.x-lvlOffsetX),(int)attackBox.y,(int)attackBox.width,(int)attackBox.height);
    }
    protected void updatePushBackDrawOffset() { //! Actualizează offset-ul de desenare la împingere
        float speed = 0.95f;
        float limit = -30f;

        if (pushBackOffsetDir == UP) {
            pushDrawOffset -= speed;
            if (pushDrawOffset <= limit)
                pushBackOffsetDir = DOWN;
        } else {
            pushDrawOffset += speed;
            if (pushDrawOffset >= 0)
                pushDrawOffset = 0;
        }
    }
    protected void pushBack(int pushBackDir, int[][] lvlData, float speedMulti) { //! Împinge înapoi entitatea
        float xSpeed = 0;
        if (pushBackDir == LEFT)
            xSpeed = -walkSpeed;
        else
            xSpeed = walkSpeed;

        if (CanMoveHere(hitbox.x + xSpeed * speedMulti, hitbox.y, hitbox.width, hitbox.height, lvlData))
            hitbox.x += xSpeed * speedMulti;
    }

    protected void initHitbox(int width, int height) { //! Inițializează hitbox-ul
        hitbox = new Rectangle2D.Float(x,y,(int)(width*Game.SCALE),(int)(height*Game.SCALE));
    }
    public Rectangle2D.Float getHitbox(){ //! Obține hitbox-ul
        return hitbox;
    }
    public int getState(){ //! Obține starea
        return state;
    }
    public int getAniIndex() { //! Obține indexul animației
        return aniIndex;
    }
    protected void newState(int state) { //! Schimbă starea
        this.state = state;
        aniTick = 0;
        aniIndex = 0;
    }
}
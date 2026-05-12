package objects;

import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import static utils.Constants.*;
import static utils.Constants.ObjectConstants.*;

public class GameObject {
    protected int x,y,objType; //!< Poziția și tipul obiectului
    protected Rectangle2D.Float hitbox; //!< Hitbox-ul obiectului
    protected boolean doAnimation, active=true; //!< Verifică dacă se face animație și starea de activitate
    protected int aniTick, aniIndex; //!< Contorul de animație și indexul animației
    protected int xDrawOffset, yDrawOffset; //!< Offset-ul de desenare pe axele X și Y
    public GameObject(int x, int y, int objType) { //! Constructor
        this.x = x;
        this.y = y;
        this.objType = objType;
    }
    protected void updateAnimationTick() { //! Actualizează contorul de animație
        aniTick++;
        if(aniTick>=ANI_SPEED) {
            aniTick=0;
            aniIndex++;
            if(aniIndex>=GetSpriteAmount(objType)) {
                aniIndex=0;
                if(objType==BARREL|| objType==BOX) {
                    doAnimation=false;
                    active=false;

                } else if(objType==CANNON_LEFT || objType==CANNON_RIGHT) {
                    doAnimation=false;
                }


            }
        }
    }
    protected int getAniIndex() { //! Întoarce indexul animației
        return aniIndex;
    }
    public void reset(){ //! Resetează obiectul
        aniIndex =0;
        aniTick=0;
        active=true;
        if(objType==BARREL || objType==BOX || objType==CANNON_LEFT || objType==CANNON_RIGHT)
            doAnimation=false;
        else
            doAnimation=true;
    }
    protected void initHitbox(int width, int height){ //! Inițializează hitbox-ul
        hitbox = new Rectangle2D.Float(x, y, (int)(width* Game.SCALE), (int)(height* Game.SCALE));

    }

    public int getyDrawOffset() {  //! Întoarce offset-ul de desenare pe axa Y
        return yDrawOffset;
    }

    public void setyDrawOffset(int yDrawOffset) { //! Setează offset-ul de desenare pe axa Y
        this.yDrawOffset = yDrawOffset;
    }

    public int getxDrawOffset() { //! Întoarce offset-ul de desenare pe axa X
        return xDrawOffset;
    }

    public void setxDrawOffset(int xDrawOffset) { //! Setează offset-ul de desenare pe axa X
        this.xDrawOffset = xDrawOffset;
    }

    public Rectangle2D.Float getHitbox() { //! Întoarce hitbox-ul
        return hitbox;
    }

    public void setHitbox(Rectangle2D.Float hitbox) { //! Setează hitbox-ul
        this.hitbox = hitbox;
    }

    public int getObjType() { //! Întoarce tipul obiectului
        return objType;
    }

    public void setObjType(int objType) {
        this.objType = objType;
    }

    public boolean isActive() { //! Verifică dacă obiectul este activ
        return active;
    }

    public void setActive(boolean active) { //! Setează starea de activitate
        this.active = active;
    }

    public void drawHitbox(Graphics g, int xLvlOffset){ //! Desenează hitbox-ul
        g.setColor(Color.PINK);
        g.drawRect((int)hitbox.x-xLvlOffset, (int)hitbox.y, (int)hitbox.width,(int)hitbox.height);
    }
    public void setAnimation(boolean doAnimation) { //! Setează animația
        this.doAnimation = doAnimation;
    }


    public int getAniTick() { //! Întoarce contorul de animație
        return aniTick;
    }
}

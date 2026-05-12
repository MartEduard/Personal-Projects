package objects;

import Main.Game;

public class Cannon extends GameObject{

    private int tileY;

    public Cannon(int x, int y, int objType) { //! Constructor
        super(x, y, objType);
        tileY = y/ Game.TILES_SIZE;
        initHitbox(40,26);
        hitbox.x -= (int)(1*Game.SCALE);
        hitbox.y += (int)(6*Game.SCALE);

    }
    public void update(){ //! Actualizează tunul
        if(doAnimation)
            updateAnimationTick();
    }
    public int getTileY(){ //! Întoarce poziția pe axa Y
        return tileY;
    }
}

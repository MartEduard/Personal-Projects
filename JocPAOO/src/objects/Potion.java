package objects;

import Main.Game;

public class Potion extends GameObject {
    private float hoverOffset; //!< Offset-ul de hover
    private int maxHoverOffset, hoverDir=1; //!< Offset-ul maxim de hover și direcția de hover
    public Potion(int x, int y, int objType) { //! Constructor
        super(x, y, objType);
        doAnimation=true;
        initHitbox(7,14);
        xDrawOffset = (int)(3* Game.SCALE);
        yDrawOffset = (int)(2* Game.SCALE);
        maxHoverOffset = (int)(10* Game.SCALE);

    }
    public void update(){ //! Actualizează poțiunea
        updateAnimationTick();
        updateHover();
    }

    private void updateHover() { //! Actualizează hover-ul
        hoverOffset += (0.075f * Game.SCALE*hoverDir);
        if(hoverOffset >= maxHoverOffset){
            hoverDir = -1;
        }
        else if (hoverOffset <= 0){
            hoverDir = 1;
        }
        hitbox.y=y+ hoverOffset;
    }
}

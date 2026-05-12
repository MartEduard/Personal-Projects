package objects;

import Main.Game;

import static utils.Constants.ObjectConstants.*;

public class GameContainer extends GameObject{ //!< Containerele din joc
    public GameContainer(int x, int y, int objType) {  //! Constructor
        super(x, y, objType);
        createHitbox();
    }

    private void createHitbox() { //! Creează hitbox-ul
        if(objType==BOX){
            initHitbox(25,18);
            xDrawOffset=(int)(7* Game.SCALE);
            yDrawOffset=(int)(12* Game.SCALE);
        } else {
            initHitbox(23,25);
            xDrawOffset=(int)(8* Game.SCALE);
            yDrawOffset=(int)(5* Game.SCALE);
        }
        hitbox.y+=yDrawOffset + (int)(Game.SCALE*2);
        hitbox.x+=xDrawOffset/2;
    }
    public void update() { //! Actualizează containerul
        if(doAnimation)
            updateAnimationTick();
    }
}

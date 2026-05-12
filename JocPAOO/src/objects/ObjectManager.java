package objects;

import Gamestates.Playing;
import Levels.Level;
import Main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Entities.Player;
import Entities.Enemy;

import static utils.Constants.ObjectConstants.*;
import static utils.Constants.Projectiles.*;
import static utils.HelpMethods.CanCannonSeePlayer;
import static utils.HelpMethods.IsProjectileHittingLevel;

public class ObjectManager {
    private Playing playing; //!< Starea jocului
    private BufferedImage[][] potionImgs, containerImgs; //!< Imaginile pentru potiuni și containere
    private BufferedImage[] cannonImgs, grassImgs; //!< Imaginile pentru tunuri și iarbă
    private BufferedImage spikeImg, cannonBallImg; //!< Imaginile pentru capcane și proiectile
    private ArrayList<Potion> potions; //!< Lista de potiuni
    private ArrayList<GameContainer> containers; //!< Lista de containere
    private ArrayList<Projectile> projectiles=new ArrayList<>(); //!< Lista de proiectile
    private Level currentLevel; //!< Nivelul curent

    public ObjectManager(Playing playing) { //! Constructor
        this.playing = playing;
        currentLevel = playing.getLevelManager().getCurrentLevel();
        loadImgs();

    }
    public void checkSpikesTouched(Player player){ //! Verifică dacă jucătorul a atins capcanele
        for(Spike s : currentLevel.getSpikes()){
            if(s.getHitbox().intersects(player.getHitbox())){
                player.kill();

            }
        }

    }
    public void checkSpikesTouched(Enemy e){ //! Verifică dacă inamicul a atins capcanele
        for(Spike s : currentLevel.getSpikes()){
            if(s.getHitbox().intersects(e.getHitbox())){
                e.hurt(200);

            }
        }

    }

    public void checkObjectTouched(Rectangle2D.Float hitbox){ //! Verifică dacă jucătorul a atins obiectele
        for(Potion p: potions)
            if(p.isActive()){
                if(hitbox.intersects(p.getHitbox())){
                    if(playing.getPlayer().getCurrentHealth() == 100 && p.getObjType()==RED_POTION || (playing.getPlayer().getPowerValue()==200 && p.getObjType()==BLUE_POTION) )
                        return;
                    p.setActive(false);
                    applyEffectToPlayer(p);
                }
            }
    }
    public void applyEffectToPlayer(Potion p){ //! Aplică efectul potiunii asupra jucătorului
        if(p.getObjType()==RED_POTION)
            playing.getPlayer().changeHealth(RED_POTION_VALUE);
        else
            playing.getPlayer().changePower(BLUE_POTION_VALUE);
    }
    public void checkObjectHit(Rectangle2D.Float attackBox){ //! Verifică dacă jucătorul a lovit obiectele
        for(GameContainer gc: containers){
            if(gc.isActive() && !gc.doAnimation){
                if(gc.getHitbox().intersects(attackBox)){
                    gc.setAnimation(true);
                    int type = 0;
                    if(gc.getObjType()==BARREL)
                        type = 1;
                    potions.add(new Potion((int)(gc.getHitbox().x+gc.getHitbox().width/2),(int)(gc.getHitbox().y-gc.getHitbox().height/2),type));
                    return;
                }
            }
        }

    }
    public void loadObjects(Level newLevel) { //! Încarcă obiectele
        currentLevel = newLevel;
        potions = new ArrayList<>(newLevel.getPotions());
        containers = new ArrayList<>(newLevel.getGameContainers());
        projectiles.clear();
    }

    private void loadImgs() { //! Încarcă imaginile
        BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
        potionImgs = new BufferedImage[2][7];
        for(int j =0; j<potionImgs.length; j++) {
            for(int i =0; i<potionImgs[j].length; i++) {
                potionImgs[j][i] = potionSprite.getSubimage(i*12, j*16, 12, 16);
            }
        }
        BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
        containerImgs = new BufferedImage[2][8];
        for(int j =0; j<containerImgs.length; j++) {
            for(int i =0; i<containerImgs[j].length; i++) {
                containerImgs[j][i] = containerSprite.getSubimage(i*40, j*30, 40, 30);
            }
        }
        spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);
        cannonImgs = new BufferedImage[7];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);
        for(int i=0;i< cannonImgs.length;i++) {
            cannonImgs[i] = temp.getSubimage(i*40,0,40,26);
        }
        cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
        BufferedImage grassTemp = LoadSave.GetSpriteAtlas(LoadSave.GRASS_ATLAS);
        grassImgs = new BufferedImage[2];
        for(int i = 0; i < grassImgs.length; i++)
            grassImgs[i]=grassTemp.getSubimage(32*i,0,32,32);
    }
    public void update(int[][] lvlData, Player player){ //! Actualizează obiectele
        for(Potion p: potions){
            if(p.isActive())
                p.update();
            for(GameContainer gc: containers){
                if(gc.isActive())
                    gc.update();
            }
        }
        updateCannons(lvlData, player);
        updateProjectiles(lvlData,player);
    }

    private void updateProjectiles(int[][] lvlData, Player player) { //! Actualizează proiectilele
        for(Projectile p: projectiles){
            if(p.isActive()) {
                p.updatePos();
                if(p.getHitbox().intersects(player.getHitbox())) {
                    player.changeHealth(-35);
                    p.setActive(false);
                } else if(IsProjectileHittingLevel(p, lvlData)){
                    p.setActive(false);
                }
            }

        }
    }

    private boolean isPlayerInRange(Cannon c, Player player) { //! Verifică dacă jucătorul este în raza tunului
        int absValue=(int)(Math.abs(player.getHitbox().x-c.getHitbox().x));
        return absValue <= Game.TILES_SIZE*5;
    }
    private boolean isPlayerInFrontOfCannon(Cannon c, Player player) { //! Verifică dacă jucătorul este în fața tunului
        if(c.getObjType()==CANNON_LEFT){
            if(c.getHitbox().x>player.getHitbox().x)
                return true;
        } else if(c.getHitbox().x<player.getHitbox().x)
            return true;
        return false;
    }

    private void updateCannons(int[][] lvlData, Player player) { //! Actualizează tunurile
        for(Cannon c: currentLevel.getCannons()){
            if(!c.doAnimation)
                if(c.getTileY()==player.getTileY())
                    if(isPlayerInRange(c,player))
                        if(isPlayerInFrontOfCannon(c,player))
                            if(CanCannonSeePlayer(lvlData,player.getHitbox(),c.getHitbox(),c.getTileY())){
                                c.setAnimation(true);
                            }
            c.update();
            if(c.getAniIndex()==4&& c.getAniTick()==0)
                shootCannon(c);
        }
    }

    private void shootCannon(Cannon c) { //! Tunul trage
        c.setAnimation(true);
        int dir = 1;
        if(c.getObjType()==CANNON_LEFT)
            dir = -1;

        projectiles.add(new Projectile((int)c.getHitbox().x,(int)c.getHitbox().y,dir));
    }


    public void draw(Graphics g, int xLvlOffset) { //! Desenează obiectele
        drawPotions(g,xLvlOffset);
        drawContainers(g,xLvlOffset);
        drawTraps(g,xLvlOffset);
        drawCannons(g,xLvlOffset);
        drawProjectiles(g,xLvlOffset);
        drawGrass(g,xLvlOffset);
    }

    private void drawGrass(Graphics g, int xLvlOffset) { //! Desenează iarbă
        for (Grass grass : currentLevel.getGrass())
            g.drawImage(grassImgs[grass.getType()], grass.getX() - xLvlOffset, grass.getY(), (int) (32 * Game.SCALE), (int) (32 * Game.SCALE), null);
    }

    private void drawProjectiles(Graphics g, int xlvlOffset) { //! Desenează proiectilele
        for(Projectile p : projectiles)
            if(p.isActive())
                g.drawImage(cannonBallImg,(int)(p.getHitbox().x-xlvlOffset),(int)(p.getHitbox().y),CANNON_BALL_WIDTH,CANNON_BALL_HEIGHT,null);
    }

    private void drawCannons(Graphics g, int xlvlOffset) { //! Desenează tunurile
        for(Cannon c: currentLevel.getCannons()){
            int x = (int)(c.getHitbox().x-xlvlOffset);
            int width = CANNON_WIDTH;
            if(c.getObjType()==CANNON_RIGHT){
                x+=width;
                width*=-1;
            }
            g.drawImage(cannonImgs[c.getAniIndex()],x,(int)(c.getHitbox().y),width,CANNON_HEIGHT,null);
        }
    }

    private void drawTraps(Graphics g, int xlvlOffset) { //! Desenează capcanele
        for(Spike s : currentLevel.getSpikes())
            g.drawImage(spikeImg, (int)(s.getHitbox().x-xlvlOffset),(int)(s.getHitbox().y - s.getyDrawOffset()),SPIKE_WIDTH,SPIKE_HEIGHT,null);
    }

    private void drawContainers(Graphics g, int xlvlOffset) { //! Desenează containerele
        for(GameContainer gc: containers){
            if(gc.isActive()){
                int type = 0;
                if(gc.getObjType()==BARREL)
                    type = 1;

                g.drawImage(containerImgs[type][gc.getAniIndex()],
                        (int)(gc.getHitbox().x-gc.getxDrawOffset()-xlvlOffset),
                        (int)(gc.getHitbox().y-gc.getyDrawOffset()),
                        CONTAINER_WIDTH,CONTAINER_HEIGHT
                        ,null);
            }
        }
    }

    private void drawPotions(Graphics g, int xlvlOffset) { //! Desenează potiunile
        for(Potion p: potions){
            if(p.isActive()){
                int type = 0;
                if(p.getObjType()==RED_POTION)
                    type = 1;
                g.drawImage(potionImgs[type][p.getAniIndex()],(int)(p.getHitbox().x-p.getxDrawOffset()-xlvlOffset),(int)(p.getHitbox().y-p.getyDrawOffset()),POTION_WIDTH,POTION_HEIGHT,null);
            }
        }
    }


    public void resetAllObjects() { //! Resetează toate obiectele

        loadObjects(playing.getLevelManager().getCurrentLevel());
        for(Potion p: potions){
            p.reset();
        }
        for(GameContainer gc: containers){
            gc.reset();
        }
        for(Cannon c: currentLevel.getCannons()){
            c.reset();
        }

    }
}

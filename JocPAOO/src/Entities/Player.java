package Entities;

import Gamestates.Playing;
import Main.Game;
import audio.AudioPlayer;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.Directions.*;
import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;
import static utils.Constants.*;
public class Player extends Entity {

    private boolean moving = false, attacking = false; //!< Verifică dacă jucătorul se mișcă sau atacă
    private boolean left, right, jump; //!< Direcțiile de mișcare și salt ale jucătorului

    private BufferedImage[][] animations; //!< Animațiile jucătorului
    private int[][] lvlData; //!< Datele nivelului
    private final float xDrawOffset = 37 * Game.SCALE; //!< Offset-ul de desenare pe axa X
    private final float yDrawOffset = 32 * Game.SCALE; //!< Offset-ul de desenare pe axa Y
    private int tileY=0; //!< Poziția pe axa Y a jucătorului
    private Playing playing; //!< Referință către obiectul Playing

    // jump and fall
    private float jumpSpeed = -2.25f * Game.SCALE; //!< Viteza de salt
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE; //!< Viteza de cădere după coliziune

    //StatusBarUI
    private BufferedImage statusBarImg; //!< Imaginea pentru bara de stare

    private int statusBarWidth = (int) (192 * Game.SCALE); //!< Lățimea barei de stare
    private int statusBarHeight = (int) (58 * Game.SCALE); //!< Înălțimea barei de stare
    private int statusBarX = (int) (10 * Game.SCALE); //!< Poziția pe axa X a barei de stare
    private int statusBarY = (int) (10 * Game.SCALE); //!< Poziția pe axa Y a barei de stare

    private int healthBarWidth = (int) (150 * Game.SCALE); //!< Lățimea barei de sănătate
    private int healthBarHeight = (int) (4 * Game.SCALE); //!< Înălțimea barei de sănătate
    private int healthBarXStart = (int) (34 * Game.SCALE); //!< Poziția de start pe axa X a barei de sănătate
    private int healthBarYStart = (int) (14 * Game.SCALE); //!< Poziția de start pe axa Y a barei de sănătate

    private int healthWidth = healthBarWidth; //!< Lățimea actuală a barei de sănătate

    private int powerBarWidth = (int)(104*Game.SCALE); //!< Lățimea barei de putere
    private int powerBarHeight = (int)(2*Game.SCALE); //!< Înălțimea barei de putere
    private int powerBarXStart = (int)(44*Game.SCALE); //!< Poziția de start pe axa X a barei de putere
    private int powerBarYStart = (int)(34*Game.SCALE); //!< Poziția de start pe axa Y a barei de putere
    private int powerWidth = powerBarWidth; //!< Lățimea actuală a barei de putere
    private int powerMaxValue=200; //!< Valoarea maximă a puterii
    private int powerValue = powerMaxValue; //!< Valoarea actuală a puterii

    // Attack Box
    private int flipX = 0; //!< Întoarce jucătorul pe axa X
    private int flipW = 1; //!< Întoarce jucătorul pe axa W

    private boolean attackChecked; //!< Verifică dacă atacul a fost efectuat
    private boolean powerAttackActive = false; //!< Verifică dacă atacul de putere este activ
    private int powerAttackTick; //!< Contor pentru atacul de putere
    private int powerGrowSpeed=15; //!< Viteza de creștere a puterii
    private int powerGrowTick; //!< Contor pentru creșterea puterii


    public Player(float x, float y, int width, int height, Playing playing) { //! Constructor
        super(x,y,width,height);
        this.playing = playing;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.walkSpeed = 1.0f*Game.SCALE;
        loadAnimations();
        initHitbox(16,28); //21 si 25
        initAttackBox();

    }
    public void setSpawn(Point spawn){  //! Setează punctul de spawn
        this.x = spawn.x;
        this.y = spawn.y;
        hitbox.x=x;
        hitbox.y=y;
    }

    private void initAttackBox() { //! Inițializează cutia de atac
        attackBox = new Rectangle2D.Float(x,y,(int)(25*Game.SCALE),(int)(20*Game.SCALE));
        resetAttackBox();
    }

    public void setPlayerSpeed(float speed) { //! Setează viteza jucătorului
        walkSpeed = (int)(speed*Game.SCALE);
    }
    public void update() { //! Actualizează starea jucătorului
        updateHealthBar();
        updatePowerBar();
        if(currentHealth <=0) {
            if(state != DEATH){
                state = DEATH;
                aniTick=0;
                aniIndex=0;
                playing.setPlayerDying(true);
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
                if(!IsEntityOnFloor(hitbox,lvlData)){
                    inAir=true;
                    airSpeed=0;
                }
            } else if(aniIndex==GetSpriteAmount(DEATH)-1&&aniTick >= ANI_SPEED - 1) {
                playing.setGameOver(true);
                playing.getGame().getAudioPlayer().stopSong();
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);

            } else {
                updateAnimationTick();
                if (inAir)
                    if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                        hitbox.y += airSpeed;
                        airSpeed += GRAVITY;
                    } else
                        inAir = false;

            }
            return;
        }

        updateAttackBox();
        if (state == HIT) {
            if (aniIndex <= GetSpriteAmount(state)) // baga un -1 daca nu merge, sau -3
                pushBack(pushBackDir, lvlData, 1.25f);
            updatePushBackDrawOffset();
        } else
            updatePos();
        if(moving) {
            checkPotionTouched();
            checkSpikesTouched();
            tileY = (int)(hitbox.y /Game.TILES_SIZE);
            if(powerAttackActive){
                powerAttackTick++;
                if(powerAttackTick>=35){
                    powerAttackTick = 0;
                    powerAttackActive = false;
                }

            }
        }
        if(attacking || powerAttackActive)
            checkAttack();
        //updateHitbox();
        updateAnimationTick();
        setAnimation();

    }

    private void checkSpikesTouched() { //! Verifică dacă jucătorul a atins țepii
        playing.checkSpikesTouched(this);
    }

    private void checkPotionTouched() { //! Verifică dacă jucătorul a atins poțiunea
        playing.checkPotionTouched(hitbox);
    }

    private void checkAttack() { //! Verifică atacul
        if(attackChecked || (aniIndex != 1 && state == ATTACK_1))
            return;
        attackChecked = true;

        if(powerAttackActive)
            attackChecked = false;

        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
        playing.getGame().getAudioPlayer().playAttackSound();
    }

    private void updateAttackBox() {  //! Actualizează cutia de atac
        if(right&&left){
            if(flipW==1){
                setAttackBoxOnRightSide();
            } else {
                setAttackBoxOnLeftSide();
            }
        }
        else if(right || (powerAttackActive && flipW == 1)){
            setAttackBoxOnRightSide();
        }else if (left || (powerAttackActive && flipW == -1)){
            setAttackBoxOnLeftSide();
        }
        attackBox.y = hitbox.y + (int)(Game.SCALE*10);
    }

    private void updateHealthBar() { //! Actualizează bara de hp
        healthWidth = (int)((currentHealth / (float)maxHealth) * healthBarWidth);
    }
    private void updatePowerBar(){ //! Actualizează bara de putere
        powerWidth = (int)((powerValue / (float) powerMaxValue)*powerBarWidth);
        powerGrowTick++;
        if(powerGrowTick>=powerGrowSpeed){
            powerGrowTick = 0;
            changePower(1);
        }
    }

    public void render(Graphics g, int lvlOffset) { //! Desenează jucătorul
        int drawY = (int)(hitbox.y -yDrawOffset);
        if(state==HIT){
            drawY +=pushDrawOffset;
        }
        g.drawImage(animations[state][aniIndex], (int) (hitbox.x - xDrawOffset) - lvlOffset + flipX, drawY, width * flipW, height, null); // ((int) (hitbox.y - yDrawOffset +  pushDrawOffset)
        //g.drawImage(animations[state][aniIndex], (int)(hitbox.x-xDrawOffset)-lvlOffset+flipX,(int)(hitbox.y-yDrawOffset),width*flipW,height,null);
        //drawHitbox(g, lvlOffset);
        //drawAttackBox(g,lvlOffset);
        drawUI(g);
    }



    private void drawUI(Graphics g) { //! Desenează interfața utilizatorului
        // Background UI
        g.drawImage(statusBarImg,statusBarX,statusBarY,statusBarWidth,statusBarHeight,null);
        // Health bar
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart+statusBarX,healthBarYStart+statusBarY,healthWidth,healthBarHeight);

        //Power Bar
        g.setColor(Color.YELLOW);
        g.fillRect(powerBarXStart+statusBarX,powerBarYStart+statusBarY,powerWidth,powerBarHeight);
    }


    private void setAnimation() {  //! Setează animația
        int startAni = state;
        if(state==HIT)
            return;
        if(moving)
            state = RUNNING;
        else
            state = IDLE;

        if(inAir){
            if(airSpeed < 0)
                state = JUMP;
            else
                state = FALL;
        }
        if(powerAttackActive) {
            state = ATTACK_2;
            aniIndex=2;
            aniTick = 0;
            return;
        }
        if(attacking) {
            state = ATTACK_1;
            if(startAni != ATTACK_1){
                aniIndex = 1;
                aniTick = 0;
                return;
            }

        }
        if(startAni != state){
            resetAniTick();
        }
    }

    private void resetAniTick() { //! Resetează contorul de animație
        aniTick = 0;
        aniIndex = 0;
    }

    private void updateAnimationTick() {  //! Actualizează contorul de animație
        aniTick++;
        if(aniTick >= ANI_SPEED)
        {
            aniTick = 0;
            aniIndex++;
            if(aniIndex >= GetSpriteAmount(state)){
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
                if(state==HIT) {
                    newState(IDLE);
                    airSpeed = 0f;
                    if(!IsFloor(hitbox,0,lvlData))
                        inAir=true;

                }
            }
        }
    }
    private void updatePos() { //! Actualizează poziția
        moving = false;
        if(jump)
            jump();
//        if(!left && !right && !inAir)
//            return;
        if(!inAir)
            if(!powerAttackActive)
                if((!left && !right) || (right && left))
                    return;
        float xSpeed = 0;
        if(left && !right) {

            xSpeed -= walkSpeed;
            flipX = width;
            flipW = -1;
        }
        if(right && !left) {
            xSpeed += walkSpeed;
            flipX = 0;
            flipW = 1;
        }
        if(powerAttackActive){
            if((!left && !right) || left && right) {
                    if (flipW == -1)
                        xSpeed = -walkSpeed;
                    else
                        xSpeed = walkSpeed;

            }
            xSpeed *=3;
        }


        if(!inAir){
            if(!IsEntityOnFloor(hitbox,lvlData))
                inAir = true;

        }
        if(inAir&&!powerAttackActive){
            if(CanMoveHere(hitbox.x,hitbox.y + airSpeed, hitbox.width,hitbox.height,lvlData)){
                hitbox.y += airSpeed;
                airSpeed += GRAVITY;
                updateXPos(xSpeed);
            } else {
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox,airSpeed);
                if(airSpeed > 0 )
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXPos(xSpeed);
            }
            
        }else {
            updateXPos(xSpeed);
        }
        moving = true;
    }

    private void jump() { //! Sare
        if(inAir)
            return;
        playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() { //! Resetează starea de "în aer"
        inAir = false;
        airSpeed = 0;
    }
    public void changePower(int value){ //! Schimbă puterea
        powerValue +=value;
        powerValue = Math.max(Math.min(powerValue,powerMaxValue),0);
    }


    private void updateXPos(float xSpeed) { //! Actualizează poziția pe axa X
        if(CanMoveHere(hitbox.x+xSpeed,hitbox.y,hitbox.width,hitbox.height,lvlData)){
            hitbox.x +=xSpeed;
        } else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
            if(powerAttackActive){
                    powerAttackActive = false;
                    powerAttackTick = 0;
            }

        }
    }
    public void changeHealth(int value) { //! Schimbă hp-ul
        if (value < 0) {
            if (state == HIT)
                return;
            else
                newState(HIT);
        }

        currentHealth += value;
        currentHealth = Math.max(Math.min(currentHealth, maxHealth), 0);
    }
    public void changeHealth(int value, Enemy e) { //! Schimbă hp în funcție de un inamic
        if (state == HIT)
            return;
        changeHealth(value);
        pushBackOffsetDir = UP;
        pushDrawOffset = 0;

        if (e.getHitbox().x < hitbox.x)
            pushBackDir = RIGHT;
        else
            pushBackDir = LEFT;
    }

    private void loadAnimations() { //! Încarcă animațiile
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animations = new BufferedImage[11][10];
        for(int j =0;j<animations.length;j++)
            for(int i =0;i<animations[j].length;i++)
                animations[j][i] = img.getSubimage(i*90,j*60,90,60);

        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }
    public void loadLvlData(int[][] lvlData){ //! Încarcă datele nivelului
        this.lvlData = lvlData;
        if(!IsEntityOnFloor(hitbox,lvlData))
            inAir = true;
    }
    public void resetAll(){ //! Resetează tot
        resetDirBooleans();
        inAir = false;
        attacking=false;

        moving=false;
        airSpeed=0f;
        state = IDLE;
        currentHealth=maxHealth;
        powerAttackActive = false;
        powerAttackTick = 0;
        powerValue = powerMaxValue;

        hitbox.x = x;
        hitbox.y = y;

        resetAttackBox();

        if(!IsEntityOnFloor(hitbox,lvlData))
            inAir = true;

    }
    private void resetAttackBox(){ //! Resetează cutia de atac
        if(flipW==1){
            setAttackBoxOnRightSide();
        } else {
            setAttackBoxOnLeftSide();
        }
    }
    private void setAttackBoxOnRightSide(){ //! Setează cutia de atac pe partea dreaptă
        attackBox.x=hitbox.x+hitbox.width+(int)(Game.SCALE*10);
    }
    private void setAttackBoxOnLeftSide(){ //! Setează cutia de atac pe partea stângă
        attackBox.x=hitbox.x-hitbox.width-(int)(Game.SCALE*19);
    }

    public void resetDirBooleans() { //! Resetează direcțiile de mișcare
        left = right = jump = false;
    }

    public void setAttacking(boolean attacking) { //! Setează starea de atac
        this.attacking = attacking;

    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }


    public void setJump(boolean jump){
        this.jump = jump;
    }

    public void kill() { //! Omoară jucătorul
        currentHealth = 0;
    }
    public int getTileY() {
        return tileY;
    }

    public void powerAttack() {
        if(powerAttackActive)
            return;
        if(powerValue >= 60){
            powerAttackActive = true;
            changePower(-60);
        }
    }

    public int getCurrentHealth() {
        return currentHealth;
    }
    public int getPowerValue(){
        return powerValue;
    }
}

package utils;


import Main.Game;
import objects.*;

import java.awt.geom.Rectangle2D;



public class HelpMethods {
    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData){ //! Verifică dacă se poate mișca într-o anumită direcție
        if(!IsSolid(x,y,lvlData))
            if(!IsSolid(x+width,y+height,lvlData))
                if(!IsSolid(x+width,y,lvlData))
                    if(!IsSolid(x,y+height,lvlData))
                        return true;
        return false;
    }
    private static boolean IsSolid(float x, float y, int[][] lvlData) { //! Verifică dacă un anumit pixel este solid
        int maxWidth = lvlData[0].length * Game.TILES_SIZE;
        if (x < 0 || x >= maxWidth)
            return true;
        if (y < 0 || y >= Game.GAME_HEIGHT)
            return true;
        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        return IsTileSolid((int) xIndex, (int) yIndex, lvlData);
    }
    public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData){ //! Verifică dacă proiectilul a lovit un zid
        return IsSolid((int)(p.getHitbox().x+ p.getHitbox().width/2),(int)(p.getHitbox().y+ p.getHitbox().height/2), lvlData);
    }

    public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) { //! Verifică dacă un anumit tile este solid
        int value = lvlData[yTile][xTile];
        if(value >= 48 || value < 0 || value != 11){
            return true;
        }
        return false;
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed){ //! Obține poziția X a entității în funcție de viteza pe axa X
        int currentTile = (int)(hitbox.x / Game.TILES_SIZE);
        if(xSpeed > 0 ) {
            // Right
            int tileXPos = currentTile * Game.TILES_SIZE;
            int xOffset = (int)(Game.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1; // ca sa nu intru in zid
        } else {
            return currentTile * Game.TILES_SIZE;
        }
    }
    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) { //! Obține poziția Y a entității în funcție de viteza pe axa Y
        int currentTile = (int) (hitbox.y / Game.TILES_SIZE);
        if (airSpeed > 0) {
            // Falling & touching floor
            int tileYPos = currentTile * Game.TILES_SIZE;
            int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset -1;
        } else
            return currentTile * Game.TILES_SIZE;
    }
    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox,int[][] lvlData){ //! Verifică dacă entitatea este pe podea
        // Verific pixelul din stanga jos si dreapta jos
        if(!IsSolid(hitbox.x,hitbox.y+hitbox.height+1,lvlData))
            if(!IsSolid(hitbox.x+hitbox.width,hitbox.y+hitbox.height+1,lvlData))
                return false;
        return true;
    }
    public static boolean IsFloor(Rectangle2D.Float hitbox,float xSpeed,int[][] lvlData) { //! Verifică dacă entitatea este pe podea
        if(xSpeed > 0)
            return IsSolid(hitbox.x+hitbox.width,hitbox.y+hitbox.height+1,lvlData); // hitbox+x.xSpeed era inainte
        else
            return IsSolid(hitbox.x+xSpeed,hitbox.y+hitbox.height+1,lvlData);

    }
    public static boolean IsFloor(Rectangle2D.Float hitbox, int[][] lvlData) { //! Verifică dacă entitatea este pe podea
        if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
            if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData))
                return false;
        return true;
    }
    public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox,  Rectangle2D.Float secondHitbox, int yTile ){ //! Verifică dacă tunul poate vedea jucătorul
        int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);
        if(firstXTile > secondXTile)
            return IsAllTilesClear(secondXTile,firstXTile,yTile,lvlData);
        else
            return IsAllTilesClear(firstXTile,secondXTile,yTile,lvlData);

    }
    public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData){ //! Verifică dacă toate tile-urile sunt libere
        if(IsAllTilesWalkable(xStart,xEnd,y,lvlData))
            for(int i =0;i<xEnd-xStart;i++) {
                if (IsTileSolid(xStart + i, y, lvlData))
                    return false;
            }
        return true;

    }
    public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) { //! Verifică dacă toate tile-urile sunt accesibile
        for(int i =0;i<xEnd-xStart;i++) {
            if (IsTileSolid(xStart + i, y, lvlData))
                return false;
            if (!IsTileSolid(xStart + i, y + 1, lvlData))
                return false;
        }
        return true;

    }
    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float enemyBox, Rectangle2D.Float playerBox, int yTile) { //! Verifică dacă linia de vedere este liberă
        int firstXTile = (int) (enemyBox.x / Game.TILES_SIZE);
        int secondXTile;
        if(IsSolid(playerBox.x, playerBox.y +playerBox.height+1,lvlData))
            secondXTile = (int)(playerBox.x/Game.TILES_SIZE);
        else
            secondXTile = (int)((playerBox.x+playerBox.width)/Game.TILES_SIZE);
        if(firstXTile>secondXTile)
            return IsAllTilesWalkable(secondXTile,firstXTile,yTile,lvlData);
        else
            return IsAllTilesWalkable(firstXTile,secondXTile,yTile,lvlData);
    }











}

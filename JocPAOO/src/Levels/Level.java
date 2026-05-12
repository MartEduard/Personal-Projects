package Levels;
import Entities.GreenRobot;
import Entities.OrangeRobot;
import Entities.PurpleRobot;
import Main.Game;
import utils.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import objects.*;
import static utils.Constants.EnemyConstants.*;
import static utils.Constants.ObjectConstants.*;


public class Level {
    private BufferedImage img; //!< Imaginea nivelului
    private ArrayList<OrangeRobot> orangeRobots= new ArrayList<>(); //!< Lista de roboți portocalii
    private ArrayList<GreenRobot> greenRobots= new ArrayList<>(); //!< Lista de roboți verzi
    private ArrayList<PurpleRobot> purpleRobots= new ArrayList<>(); //!< Lista de roboți mov
    private ArrayList<Potion> potions= new ArrayList<>(); //!< Lista de potiuni
    private ArrayList<Spike> spikes= new ArrayList<>(); //!< Lista de spike-uri
    private ArrayList<GameContainer> gameContainers= new ArrayList<>(); //!< Lista de containere
    private ArrayList<Cannon> cannons= new ArrayList<>(); //!< Lista de tunuri
    private ArrayList<Grass> grass= new ArrayList<>(); //!< Lista de iarbă
    private int[][] lvlData; //!< Datele nivelului
    private int lvlTilesWide; //!< Numărul de dale pe lățime
    private int maxTilesOffset; //!< Offset-ul maxim
    private int maxLvlOffsetX; //!< Offset-ul maxim pe axa X
    private Point playerSpawn; //!< Poziția de spawn a jucătorului

    public Level(BufferedImage img){ //! Constructor
        this.img=img;
        lvlData = new int[img.getHeight()][img.getWidth()];
        loadLevel();
        calcLvlOffSets();

    }
    private void loadLevel(){ //! Încarcă nivelul
        for (int y = 0; y < img.getHeight(); y++)
            for (int x = 0; x < img.getWidth(); x++) {
                Color c = new Color(img.getRGB(x, y));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();

                loadLevelData(red, x, y);
                loadEntities(green, x, y);
                loadObjects(blue, x, y);
            }
    }
    private void loadLevelData(int redValue, int x, int y) { //! Încarcă datele nivelului
        if (redValue >= 50)
            lvlData[y][x] = 0;
        else
            lvlData[y][x] = redValue;
        switch (redValue) {
            case 0, 1, 2, 3, 30, 31, 33, 34, 35, 36, 37, 38, 39 ->
                    grass.add(new Grass((int) (x * Game.TILES_SIZE), (int) (y * Game.TILES_SIZE) - Game.TILES_SIZE, getRndGrassType(x)));
        }
    }
    private int getRndGrassType(int xPos){ //! Returnează tipul de iarbă
        return xPos % 2;
    }
    private void loadEntities(int greenValue, int x, int y){ //! Încarcă entitățile
        switch(greenValue){
            case ORANGE_ROBOT -> orangeRobots.add(new OrangeRobot(x*Game.TILES_SIZE,y*Game.TILES_SIZE));
            case GREEN_ROBOT -> greenRobots.add(new GreenRobot(x*Game.TILES_SIZE,y*Game.TILES_SIZE));
            case PURPLE_ROBOT -> purpleRobots.add(new PurpleRobot(x*Game.TILES_SIZE,y*Game.TILES_SIZE));
            case 100 -> playerSpawn = new Point(x*Game.TILES_SIZE,y*Game.TILES_SIZE);
        }
    }


    private void calcLvlOffSets() { //! Calculează offset-ul nivelului
        lvlTilesWide = img.getWidth();
        maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
        maxLvlOffsetX=Game.TILES_SIZE*maxTilesOffset;
    }
    private void loadObjects(int blueValue, int x, int y){ //! Încarcă obiectele
        switch (blueValue){
            case RED_POTION, BLUE_POTION -> potions.add(new Potion(x*Game.TILES_SIZE,y*Game.TILES_SIZE,blueValue));
            case BOX, BARREL -> gameContainers.add(new GameContainer(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
            case SPIKE -> spikes.add(new Spike(x * Game.TILES_SIZE, y * Game.TILES_SIZE, SPIKE));
            case CANNON_LEFT, CANNON_RIGHT -> cannons.add(new Cannon(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
        }
    }


    public int getSpriteIndex(int x, int y) { //! Returnează indexul sprite-ului
        return lvlData[y][x];
    }

    public int[][] getLevelData(){ //! Returnează datele nivelului
        return lvlData;
    }
    public int getLvlOffset(){  //! Returnează offset-ul nivelului
        return maxLvlOffsetX;
    }
    public ArrayList<OrangeRobot> getOrangeRobots(){ //! Returnează lista de roboți portocalii
        return orangeRobots;
    }
    public Point getPlayerSpawn(){ //! Returnează poziția de spawn a jucătorului
        return playerSpawn;
    }
    public ArrayList<GameContainer> getGameContainers(){    //! Returnează lista de containere
        return gameContainers;
    }
    public ArrayList<Potion> getPotions(){ //! Returnează lista de potiuni
        return potions;
    }
    public ArrayList<Spike> getSpikes(){ //! Returnează lista de țepi
        return spikes;
    }
    public ArrayList<Cannon> getCannons(){
        return cannons;
    }
    public ArrayList<Grass> getGrass(){ //! Returnează lista de iarbă
        return grass;
    }
    public ArrayList<GreenRobot> getGreenRobots(){ //! Returnează lista de roboți verzi
        return greenRobots;
    }
    public ArrayList<PurpleRobot> getPurpleRobots(){ //! Returnează lista de roboți mov
        return purpleRobots;
    }
}

package Levels;
import Gamestates.Gamestate;
import Main.Game;
import utils.DatabaseHelper;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LevelManager {
    private Game game; //!< Referință la joc
    private BufferedImage[] levelSprite; //!< Vector de imagini pentru nivel
    private ArrayList<Level> levels; //!< Lista de niveluri
    private static int lvlIndex; //!< Indexul nivelului



    public LevelManager(Game game) { //! Constructor

        this.game = game;
        importOutsideSprites();
        levels = new ArrayList<>();
        buildAllLevels();
        lvlIndex = DatabaseHelper.loadCurrentLevel();
    }
    public void loadNextLevel() {  //! Încarcă următorul nivel

        Level newLevel = levels.get(lvlIndex);
        game.getPlaying().getEnemyManager().loadEnemies(newLevel);
        game.getPlaying().getPlayer().loadLvlData(newLevel.getLevelData());
        game.getPlaying().setMaxLvlOffset(newLevel.getLvlOffset());
        game.getPlaying().getObjectManager().loadObjects(newLevel);

    }

    private void buildAllLevels() { //! Construiește toate nivelurile
        BufferedImage[] allLevels = LoadSave.GetAllLevels();
        for(BufferedImage img : allLevels) {
            levels.add(new Level(img));
        }
    }

    private void importOutsideSprites() { //! Importă imaginile pentru nivel
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[48];
        for(int j =0;j<4;j++)
            for(int i =0;i<12;i++){
                int index = j*12+i;
                levelSprite[index] = img.getSubimage(i*32,j*32,32,32);
            }
    }

    public void draw(Graphics g, int lvlOffset){ //! Desenează nivelul
        for(int j =0; j < Game.TILES_IN_HEIGHT; j++)
            for(int i = 0; i < levels.get(lvlIndex).getLevelData()[0].length; i++){
                int index = levels.get(lvlIndex).getSpriteIndex(i,j);
                //g.drawImage(levelSprite[index], Game.TILES_SIZE * i-lvlOffset, Game.TILES_SIZE * j, Game.TILES_SIZE, Game.TILES_SIZE , null);
                int x = Game.TILES_SIZE*i-lvlOffset;
                int y = Game.TILES_SIZE*j;
                if(index!=48 && index != 49)
                    g.drawImage(levelSprite[index], x, y, Game.TILES_SIZE, Game.TILES_SIZE, null);
            }

    }
    public void update(){ //!
        // Neimplementat
    }
    public Level getCurrentLevel(){ //! Returnează nivelul curent
        return levels.get(lvlIndex);
    }
    public int getAmountOfLevels(){ //! Returnează numărul de niveluri
        return levels.size();
    }
    public static int getLevelIndex(){ //! Returnează indexul nivelului
        return lvlIndex;
    }
    public void setLevelIndex(int lvlIndex){ //! Setează indexul nivelului
        this.lvlIndex = lvlIndex;
    }

}

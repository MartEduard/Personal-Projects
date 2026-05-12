package utils;

import Entities.OrangeRobot;
import Main.Game;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;




public class LoadSave {
    public static final String PLAYER_ATLAS = "/player_cavaler2.png"; // /player_cavaler.png
    public static final String LEVEL_ATLAS = "/outside_sprites.png";
    public static final String MENU_BUTTONS = "/button_atlas.png";
    public static final String MENU_BACKGROUND = "/menu_background.png";
    public static final String PAUSE_BACKGROUND = "/pause_menu.png";
    public static final String SOUND_BUTTONS = "/sound_button.png";
    public static final String URM_BUTTONS = "/urm_buttons.png";
    public static final String VOLUME_BUTTONS = "/volume_buttons.png";
    public static final String FUNDAL = "/Fundal.png";
    public static final String BG_IMAGE = "/Background.png";
    public static final String MIDDLEGROUND = "/Middleground.png";
    public static final String EXTENSION_GREEN = "/Extension_green.png";
    public static final String SMALL_CLOUDS = "/small_clouds.png";
    public static final String ORANGE_ROBOT = "/Robot_atlas_reversed.png";
    public static final String STATUS_BAR = "/health_power_bar.png";
    public static final String COMPLETED_IMAGE = "/completed_sprite.png";
    public static final String POTION_ATLAS = "/potions_sprites.png";
    public static final String CONTAINER_ATLAS = "/objects_sprites.png";
    public static final String TRAP_ATLAS = "/trap_atlas.png";
    public static final String CANNON_ATLAS = "/cannon_atlas.png";
    public static final String CANNON_BALL = "/ball.png";
    public static final String DEATH_SCREEN = "/death_screen.png";
    public static final String OPTIONS_MENU = "/options_background.png";
    public static final String GRASS_ATLAS = "/grass_atlas.png";
    public static final String GAME_COMPLETED = "/game_completed.png";
    public static final String OPTIONS_BACKGROUND = "/optiuni.png";
    public static final String CREDITS_BACKGROUND = "/credits.png";
    public static final String GREEN_ROBOT = "/RobotVerzui.png";
    public static final String PURPLE_ROBOT = "/RobotMov.png";



    public static BufferedImage GetSpriteAtlas(String fileName)  {
        BufferedImage img=null;
        InputStream is = LoadSave.class.getResourceAsStream(fileName); // import la imaginea pe care vrem sa o returnam
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { is.close();
            } catch (IOException e) {

            }
        }

        return img;
    }
    public static BufferedImage[] GetAllLevels() {
        URL url = LoadSave.class.getResource("/lvls");
        File file = null;

        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        File[] files = file.listFiles();
        File[] filesSorted = new File[files.length];

        for (int i = 0; i < filesSorted.length; i++)
            for (int j = 0; j < files.length; j++) {
                if (files[j].getName().equals((i + 1) + ".png"))
                    filesSorted[i] = files[j];

            }

        BufferedImage[] imgs = new BufferedImage[filesSorted.length];

        for (int i = 0; i < imgs.length; i++)
            try {
                imgs[i] = ImageIO.read(filesSorted[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }

        return imgs;
    }

}

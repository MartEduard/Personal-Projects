package utils;

import Main.Game;

public class Constants {
    public static final float GRAVITY = 0.04f*Game.SCALE; //!< Gravitația
    public static final int ANI_SPEED = 25; //!< Viteza de animație
    public static class Projectiles{ //!< Constante pentru proiectile
        public static final int CANNON_BALL_DEFAULT_WIDTH = 15;
        public static final int CANNON_BALL_DEFAULT_HEIGHT = 15;
        public static final int CANNON_BALL_WIDTH = (int)(Game.SCALE*CANNON_BALL_DEFAULT_WIDTH);
        public static final int CANNON_BALL_HEIGHT = (int)(Game.SCALE*CANNON_BALL_DEFAULT_HEIGHT);
        public static final float SPEED = 0.75f*Game.SCALE;

    }
    public static class ObjectConstants { //!< Constante pentru obiecte

        public static final int RED_POTION = 0;
        public static final int BLUE_POTION = 1;
        public static final int BARREL = 2;
        public static final int BOX = 3;
        public static final int SPIKE = 4;
        public static final int CANNON_LEFT = 5;
        public static final int CANNON_RIGHT = 6;


        public static final int RED_POTION_VALUE = 25;
        public static final int BLUE_POTION_VALUE = 30;

        public static final int CONTAINER_WIDTH_DEFAULT = 40;
        public static final int CONTAINER_HEIGHT_DEFAULT = 30;
        public static final int CONTAINER_WIDTH = (int) (Game.SCALE * CONTAINER_WIDTH_DEFAULT);
        public static final int CONTAINER_HEIGHT = (int) (Game.SCALE * CONTAINER_HEIGHT_DEFAULT);

        public static final int POTION_WIDTH_DEFAULT = 12;
        public static final int POTION_HEIGHT_DEFAULT = 16;
        public static final int POTION_WIDTH = (int) (Game.SCALE * POTION_WIDTH_DEFAULT);
        public static final int POTION_HEIGHT = (int) (Game.SCALE * POTION_HEIGHT_DEFAULT);

        public static final int SPIKE_WIDTH_DEFAULT = 32;
        public static final int SPIKE_HEIGHT_DEFAULT = 32;
        public static final int SPIKE_WIDTH = (int) (Game.SCALE * SPIKE_WIDTH_DEFAULT);
        public static final int SPIKE_HEIGHT = (int) (Game.SCALE * SPIKE_HEIGHT_DEFAULT);

        public static int CANNON_WIDTH_DEFAULT = 40;
        public static int CANNON_HEIGHT_DEFAULT = 26;
        public static int CANNON_WIDTH = (int) (Game.SCALE * CANNON_WIDTH_DEFAULT);
        public static int CANNON_HEIGHT = (int) (Game.SCALE * CANNON_HEIGHT_DEFAULT);
        public static int GetSpriteAmount(int object_type) { //!< Returnează numărul de sprite-uri pentru un obiect
            return switch (object_type) {
                case RED_POTION, BLUE_POTION -> 7;
                case BARREL, BOX -> 8;
                case CANNON_LEFT, CANNON_RIGHT -> 7;
                default -> 1;
            };

        }
    }

    public static class EnemyConstants{ //!< Constante pentru inamici

        public static final int ORANGE_ROBOT = 0;
        public static final int GREEN_ROBOT = 1;
        public static final int PURPLE_ROBOT = 2;

        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int ATTACK = 2;
        public static final int HIT = 3;
        public static final int DEATH = 4;

        public static final int ROBOT_SIZE_DEFAULT = 32;
        public static final int ROBOT_SIZE = (int)(ROBOT_SIZE_DEFAULT *Game.SCALE);

        public static final int ROBOT_DRAWOFFSET_X = (int)(11*Game.SCALE);
        public static final int ROBOT_DRAWOFFSET_Y = (int)(15*Game.SCALE);


        public static int GetSpriteAmount(int enemy_type, int enemy_state){ //!< Returnează numărul de sprite-uri pentru un inamic
            switch (enemy_type){
                case ORANGE_ROBOT, GREEN_ROBOT, PURPLE_ROBOT:

                    switch (enemy_state){
                        case IDLE:
                            return 5;
                        case RUNNING:
                            return 6;
                        case ATTACK:
                        case DEATH:
                            return 4;
                        case HIT:
                            return 2;
                    }
            }
            return 0;
        }
        public static int GetMaxHealth(int enemy_type) { //!< Returnează viața maximă a unui inamic
            return switch(enemy_type) {
                case ORANGE_ROBOT -> 50;
                case GREEN_ROBOT -> 80;
                case PURPLE_ROBOT -> 250;
                default -> 10;
            };

        }
        public static int GetEnemyDmg(int enemy_type) { //!< Returnează daunele unui inamic
            return switch(enemy_type){
                case ORANGE_ROBOT -> 15;
                case GREEN_ROBOT -> 25;
                case PURPLE_ROBOT -> 45;
                default -> 1;
            };
        }
    }
    public static class UI {
        public static class Environment { //!< Constante pentru mediu
            public static int MIDDLEGROUND_WIDTH_DEFAULT = 384; // Folosit si de Extension Green
            public static int MIDDLEGROUND_HEIGHT_DEFAULT = 216;
            public static int SMALL_CLOUDS_WIDTH_DEFAULT= 74;
            public static int SMALL_CLOUDS_HEIGHT_DEFAULT = 24;

            public static int MIDDLEGROUND_WIDTH = (int)(MIDDLEGROUND_WIDTH_DEFAULT*Game.SCALE);
            public static int MIDDLEGROUND_HEIGHT = (int)(MIDDLEGROUND_HEIGHT_DEFAULT*Game.SCALE);
            public static int SMALL_CLOUDS_WIDTH = (int)(SMALL_CLOUDS_WIDTH_DEFAULT*Game.SCALE);
            public static int SMALL_CLOUDS_HEIGHT = (int)(SMALL_CLOUDS_HEIGHT_DEFAULT*Game.SCALE);
        }
        public static class Buttons { //!< Constante pentru butoane
            public static final int B_WIDTH_DEFAULT = 140;
            public static final int B_HEIGHT_DEFAULT = 56;
            public static final int B_WIDTH = (int)(B_WIDTH_DEFAULT* Game.SCALE);
            public static final int B_HEIGHT = (int)(B_HEIGHT_DEFAULT* Game.SCALE);
        }
        public static class PauseButtons { //!< Constante pentru butoanele de pauză
            public static final int SOUND_SIZE_DEFAULT = 42;
            public static final int SOUND_SIZE = (int)(SOUND_SIZE_DEFAULT* Game.SCALE);

        }
        public static class URMButtons { //!< Constante pentru butoanele URM
            public static final int URM_DEFAULT_SIZE = 56;
            public static final int URM_SIZE =  (int)(URM_DEFAULT_SIZE* Game.SCALE);
        }
        public static class VolumeButtons { //!< Constante pentru butoanele de volum
            public static final int VOLUME_DEFAULT_WIDTH = 28;
            public static final int VOLUME_DEFAULT_HEIGHT = 44;
            public static final int SLIDER_DEFAULT_WIDTH = 215;

            public static final int VOLUME_WIDTH = (int)(VOLUME_DEFAULT_WIDTH* Game.SCALE);
            public static final int VOLUME_HEIGHT = (int)(VOLUME_DEFAULT_HEIGHT* Game.SCALE);
            public static final int SLIDER_WIDTH = (int)(SLIDER_DEFAULT_WIDTH* Game.SCALE);


        }
    }
    public static class Directions{ //!< Constante pentru direcții
        public static final int LEFT = 0;
       public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }

    public static class PlayerConstants{ //!< Constante pentru jucător
        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int ATTACK_1 = 2;
        public static final int ATTACK_2 = 3;
        public static final int ATTACK_NO_MOVEMENT = 4;
        public static final int HIT = 5;
        public static final int TURN_AROUND = 6;
        public static final int JUMP = 7;
        public static final int FALL = 8;
        public static final int DEATH = 9;

        public static int GetSpriteAmount(int player_action) { //!< Returnează numărul de sprite-uri pentru jucător
            switch(player_action)
            {
                case IDLE:
                case RUNNING:
                case DEATH:
                    return 10;
                case ATTACK_1:
                case ATTACK_NO_MOVEMENT:
                    return 4;
                case ATTACK_2:
                    return 6;
                case HIT:
                    return 1;
                case FALL:
                case JUMP:
                case TURN_AROUND:
                    return 3;
                default:
                    return 1;
            }
        }
    }
}

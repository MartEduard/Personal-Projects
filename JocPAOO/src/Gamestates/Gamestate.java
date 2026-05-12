package Gamestates;

public enum Gamestate {
    PLAYING, MENU, OPTIONS, CREDITS, QUIT; //! Stările jocului
    public static Gamestate state = MENU; //! Starea curentă
}

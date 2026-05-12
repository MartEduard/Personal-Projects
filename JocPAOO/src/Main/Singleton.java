package Main;

public class Singleton {
    private Game game;
    private static Singleton instance = null;
    private Singleton() {
        System.out.println("Singleton instantiat");
        game = new Game();

    }
    public static Singleton getInstance()  {
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }

    public Game getGame(){
        return game;
    }
}

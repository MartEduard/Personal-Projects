package objects;

public class Grass {

    private int x, y, type; //!< Tipul de iarbă

    public Grass(int x, int y, int type) { //!< Constructor
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() { //! Întoarce poziția pe axa X
        return x;
    }

    public int getY() { //! Întoarce poziția pe axa Y
        return y;

    }

    public int getType() { //! Întoarce tipul de iarbă
        return type;
    }
}
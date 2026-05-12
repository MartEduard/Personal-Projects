package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:src/utils/Salvat.db"; //!< Calea catre baza de date

    public static Connection connect() { //! Functie pentru conectare la baza de date
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void saveCurrentLevel(int levelIndex) { //! Functie pentru salvarea nivelului curent
        String sql = "UPDATE game_state SET current_level = ? WHERE id = 1";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, levelIndex);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Nivel salvat cu succes!");
        }
    }

    public static int loadCurrentLevel() { //! Functie pentru incarcarea nivelului curent
        String sql = "SELECT current_level FROM game_state WHERE id = 1";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("current_level");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Nivel incarcat cu succes!");
        }

        return 0;
    }
}
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
    private static final String URL = "jdbc:postgresql://pedago.univ-avignon.fr:5432/etd";
    private static final String USER = "uapv2402927";
    private static final String PASSWORD = "zMdsfH";
    private Connection conn;

    public void connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie avec succès.");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Driver PostgreSQL introuvable.");
            e.printStackTrace();
            throw new SQLException("Impossible de charger le driver PostgreSQL.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base : " + e.getMessage());
            throw e;
        }
    }

    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
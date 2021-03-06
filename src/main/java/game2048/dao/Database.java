package game2048.dao;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class to create a connection to the database and handles of creating the database.
 */
public class Database {
    private String databaseUrl;
    
    public Database(boolean testing) {
        if (testing) {
            databaseUrl = "jdbc:sqlite:test.db";
        } else {
            databaseUrl = "jdbc:sqlite:main.db";
        }
    }
    
    /**
     * Method for getting connection.
     * @return connection object
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(databaseUrl);
        } catch (SQLException e) {
            System.out.println("Getting database connection failed");
        }
        return conn;
    }
    /**
     * Creates SQLtable with columns of id, score, boardsize, date.
     * If table already exists, method won't do anything.
     */
    public void createHighscoreTable() {
        try {
            Connection db = getConnection();    
            Statement s = db.createStatement();
            s.execute("CREATE TABLE Scores (id INTEGER PRIMARY KEY, score Integer, boardsize Integer, date String)");
            s.close();
            db.close();
        } catch (Exception e) {
        }
    }
    
    public void deleteTestDatabase() {
        try {
            Path pathToTestDatabase = FileSystems.getDefault().getPath("test.db");
            Files.delete(pathToTestDatabase);
        } catch (Exception e) {
            System.out.println("Test database was not deleted.");
        }
    }
}

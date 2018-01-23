/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import control.Globals;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adatbázis csatoló osztály, singleton dizájnnal
 * @author djani * 
 */
public class DB_Connector {

    private static DB_Connector instance;
    private Connection connection;
    private String tableName = Globals.DB_TABLENAME;
    private String databaseName = Globals.DB_DBNAME;

    private DB_Connector() {
        Setup();
    }
    
    /**
     * Adatbázishoz csatlakozó osztály lekérése
     * @return az adatbázishoz csatlakozó osztály
     */
    public static DB_Connector getInstance() {
        if (instance == null) {
            instance = new DB_Connector();
        }
        return instance;
    }

    private void Setup() {
        try {
            connection = connectToDatabase();
        } catch (Exception ex) {
            Logger.getLogger(DB_Connector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Connection connectToDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        String url = "jdbc:derby:" + databaseName + ";create=true;"; // databaseName -> name of the database
        // create=true  -> create the DB if it doesn't exist

        connection = DriverManager.getConnection(url);

        // create the tabel if it doesn't exist
        createTableIfNotExists();

        return connection;
    }

    private void createTableIfNotExists() {
        if (connection != null) {

            String sqlGetAllTables = "select * from SYS.SYSTABLES where tablename = '" + tableName + "'";
            String sqlCreateTable = "CREATE TABLE APP." + tableName + " (name varchar(50), pass varchar(50), score int)";

            try (
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sqlGetAllTables)) {

                if (!resultSet.next()) {
                    System.out.println("\nCREATING TABLE! \n");
                    statement.execute(sqlCreateTable);
                }

            } catch (SQLException ex) {
                Logger.getLogger(DB_Connector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Adatbázis kapcsolat lekérése
     * @return adatbázis kapcsolat
     */
    public Connection getConnection() {
        return connection;
    }
}

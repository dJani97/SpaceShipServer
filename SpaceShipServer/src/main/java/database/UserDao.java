/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import control.Globals;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author djani
 */
public class UserDao implements Dao<User, String> {

    String tableName = Globals.DB_TABLENAME;
    Connection connection;

    /**
     * Felhasználó elérő objektum
     * @param connection adatbázis kapcsolat
     */
    public UserDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<User> listAllRecords() {

        List<User> users = new ArrayList<>();
        String sqlStatement = "SELECT * FROM " + tableName;

        if (connection != null) {
            try (
                    Statement s = connection.createStatement();
                    ResultSet resultSet = s.executeQuery(sqlStatement)) {

                String name, pass;
                int score;

                while (resultSet.next()) {
                    name = resultSet.getString("name");
                    pass = resultSet.getString("pass");
                    score = resultSet.getInt("score");

                    users.add(new User(name, pass, score));
                }

            } catch (Exception ex) {
                Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return users;
    }

    @Override
    public void createRecord(User t) {
        if(!isRecordInDB(t.getName())){
            String sqlStatement = "INSERT INTO " + tableName + " VALUES(?, ?, ?)";
            try (PreparedStatement prepStmt = connection.prepareStatement(sqlStatement)) {
                prepStmt.setString(1, t.getName());
                prepStmt.setString(2, t.getPass());
                prepStmt.setInt(3, t.getScore());
                prepStmt.executeUpdate();

            } catch (Exception ex) {
                Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public User readRecord(String userName) {
        ResultSet resultSet = null;
        String sqlStatement = "SELECT * FROM " + tableName + " WHERE name = ?";
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlStatement)) {
            prepStmt.setString(1, userName);
            resultSet = prepStmt.executeQuery();
            resultSet.next();
            
            return new User(resultSet.getString("name"), 
                    resultSet.getString("pass"), 
                    resultSet.getInt("score"));

        } catch (SQLException ex) {
            if(!(ex.getErrorCode() == 20000)) {
                Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
            }            
            return null;
        }
        catch (Exception ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void updateRecord(User t) {
        String sqlStatement = "UPDATE " + tableName + " SET score = ? where name = ?";
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlStatement)) {
            prepStmt.setInt(1, t.getScore());
            prepStmt.setString(2, t.getName());
            prepStmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteRecord(User t) {
        String sqlStatement = "DELETE FROM " + tableName + " WHERE name = ?";
        try (PreparedStatement prepStmt = connection.prepareStatement(sqlStatement);) {
            prepStmt.setString(1, t.getName());
            prepStmt.executeUpdate();

        } catch (Exception ex) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isRecordInDB(String userName) {
        User user = this.readRecord(userName);
        return user != null;
    }
}

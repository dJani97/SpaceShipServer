/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseTests;

import database.DB_Connector;
import database.UserDao;
import java.sql.Connection;
import java.util.List;
import model.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dobszai
 */
public class UserDaoTest {
    
    public UserDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void daoTest() {
        String userName = "testDummy";
        String userPass = "123";
        int userScore = 5;
        int newUserScore = 15;
        
        Connection connection = DB_Connector.getInstance().getConnection();
        UserDao dao = new UserDao(connection);
        
        // új dummy user
        User u =  new User(userName, userPass, userScore);
        dao.createRecord(u);
        
        // listáz
        List<User> userlist = dao.listAllRecords();
        assertTrue(userlist.size() > 0);
        assertTrue(dao.isRecordInDB(userName));
        
        // dummy adatainak ellenőrzése
        User userFromDB = dao.readRecord(userName);
        assertEquals(userFromDB.getName(), userName);
        assertEquals(userFromDB.getPass(), userPass);
        assertEquals(userFromDB.getScore(), userScore);
        
        // update tesztelése
        userFromDB.setScore(newUserScore);
        dao.updateRecord(userFromDB);
        userFromDB = null;
        userFromDB = dao.readRecord(userName);
        assertEquals(userFromDB.getScore(), newUserScore);
        
        // dummy törlése
        dao.deleteRecord(u);
        assertFalse(dao.isRecordInDB(userName));
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}

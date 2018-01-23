/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseTests;

import control.Controller;
import control.Globals;
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
 * @author djani
 */
public class AntiHackTest {
    
    public AntiHackTest() {
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
    public void deleteHackers() {
        Connection connection = DB_Connector.getInstance().getConnection();
        UserDao dao = new UserDao(connection);
        
        // új user
        User u =  new User("Hacker", "H4CK3R", Globals.USR_SCORE_LIMIT+1);
        dao.createRecord(u);
        
        // deleteHackers futtatása
        List<User> userlist = dao.listAllRecords();
        for (User user : userlist) {
            if(user.getScore() > Globals.USR_SCORE_LIMIT) {
                dao.deleteRecord(user);
            }
        }
        
        // a hacker már nincs az adatbázisban
        assertFalse(dao.isRecordInDB(u.getName()));
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}

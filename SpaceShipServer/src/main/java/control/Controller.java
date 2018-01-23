/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import database.DB_Connector;
import database.UserDao;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import model.User;
import server.ServerWorker;
import server.SocketServerWorker;

/**
 * Vezérlő osztály
 *
 * @author dobszai
 */
public class Controller {

    UserDao dao; // szándékosan így példányosítottam, így adat lekéréskor nem kell típust kényszeríteni
    List<User> userlist = new ArrayList<>();
    List<ServerWorker> workerList = new CopyOnWriteArrayList<>();
    SocketServerWorker socketServerWorker;

    /**
     * A vezérlő példányosítása a start() metódus felel a szerver elindításáért
     */
    public void start() {
        setup();
        setupProperShutdown();
        deleteHackers();

        Scanner keyboard = new Scanner(System.in);
        int input;
        do {
            System.out.println("1 - exit server");
            input = keyboard.nextInt();
        } while (input != 1);
    }

    private void setup() {
        Connection connection = DB_Connector.getInstance().getConnection();
        dao = new UserDao(connection);
        socketServerWorker = new SocketServerWorker(this, Globals.SRV_PORT);
        Thread socketServerWorkerThread = new Thread(socketServerWorker);
        socketServerWorkerThread.start();
    }

    /**
     * Hasznos információk kiírása a konzolba a szerverről
     */
    public void printServerInfo() {
        System.out.println("\n___ SERVER INFORMATION ___:\n "
                + "Number of clients: " + workerList.size() + "\n"
                + "Client list: " + workerList + "\n"
                + "Recorded highscores: " + dao.listAllRecords());
    }

    //<editor-fold defaultstate="collapsed" desc="GET / SET / ADD">
    /**
     * Felhasználó lista lekérése
     *
     * @return az összes felhasználó adatait tartalmazó lista
     */
    public List<User> getUserList() {
        userlist = dao.listAllRecords();
        return Collections.unmodifiableList(userlist);
    }

    /**
     * Felhasználó lekérése
     *
     * @param username keresett felhasználó neve
     * @return a kért felhasználó; nem létező felhasználó esetén esetén null
     */
    public User getUser(String username) {
        return dao.readRecord(username);
    }

    /**
     * Ellenőrzi, hogy már létezik-e az adott felhasználó
     *
     * @param username keresett felhasználó neve
     * @return
     */
    public boolean isUserInDB(String username) {
        return dao.isRecordInDB(username);
    }

    /**
     * új szál hozzáfűzése a szerver szálakat tartalmazó listához
     *
     * @param sw hozzáadni kívánt ServerWorker
     */
    public void addServerWorker(ServerWorker sw) {
        workerList.add(sw);
    }

    /**
     * Új felhasználó létrehozása
     *
     * @param user létrehozni kívánt felhasználó
     */
    public void createUser(User user) {
        dao.createRecord(user);
        if (Globals.LOG_EVENT_DRIVEN_LOGGING) {
            printServerInfo();
        }
    }

    /**
     * Felhasználó pontszámának frissítése
     *
     * @param user akinek a pontszámát frissítjük
     */
    public void updateUserScore(User user) {
        System.out.println("Updating user " + user.getName() + " with score " + user.getScore());
        dao.updateRecord(user);
        if (Globals.LOG_EVENT_DRIVEN_LOGGING) {
            printServerInfo();
        }
    }
    //</editor-fold>

    private void setupProperShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                printServerInfo();

                for (ServerWorker serverWorker : workerList) {
                    serverWorker.closeConnection();
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="// launchServerInfo() - commented">
    /*
    private void launchServerInfo() {
    Thread serverInfo = new Thread(new Runnable() {
    @Override
    public void run() {
    try {
    printServerInfo();
    Thread.sleep(Globals.LOG_SLEEP_TIME);
    } catch (InterruptedException ex) {
    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    });
    serverInfo.start();
    }*/
//</editor-fold>

    private void deleteHackers() {
        if(dao != null) {
            userlist = dao.listAllRecords();
            for (User user : userlist) {
                if(user.getScore() > Globals.USR_SCORE_LIMIT) {
                    dao.deleteRecord(user);
                }
            }
        }
    }
    
    public void deleteWorker(ServerWorker worker) {
        workerList.remove(worker);
        worker = null;
        printServerInfo();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import client.ClientWorker;
import view.ScorePanel;
import view.GamePanel;
import model.Ship;
import model.GameObject;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import model.Player;

/**
 * @author djani
 */
public class Controller implements Runnable {

    int userNameTryCount = 0;
    boolean loaderActive;
    boolean newUser = true;
    ClientWorker clientWorker = null;

    int asteroidNum = 10;
    Ship ship;
    GamePanel gamePanel;
    ScorePanel scorePanel;
    List<GameObject> asteroids = new CopyOnWriteArrayList<>();
    Image imgAsteroid;
    Thread controllerThread;
    int timeSurvived;
    Player player = new Player();
    List<Player> playerList;

    boolean connected = false;

    public Controller(GamePanel gamePanel, ScorePanel scorePanel) {
        this.gamePanel = gamePanel;
        this.scorePanel = scorePanel;
        focusOnPanel();
        setup();
    }

    private void setup() {
        gamePanel.setController(this);
        scorePanel.setController(this);
        Image background = new ImageIcon(this.getClass().
                getResource(Globals.IMG_BACKGROUND)).getImage();
        gamePanel.setBackground(background);
        gamePanel.setPreferredSize(new Dimension(
                Globals.PANEL_WIDTH, Globals.PANEL_HEIGHT));

        GameObject.setSleepTime(Globals.SLEEP_TIME);
        GameObject.setPanelWidth(gamePanel.getPreferredSize().width);
        GameObject.setPanelHeight(gamePanel.getPreferredSize().height);
        Ship.setMaxTurnVelocity(Globals.SHIP_TURN_VELOCITY);
        Ship.setTurnFrameCount(Globals.SHIP_TURN_ACCELERATION);
        Ship.setAcceleration(Globals.SHIP_ACCELERATION);
        Ship.setMaxVelocity(Globals.SHIP_MAX_VELOCITY);

        imgAsteroid = new ImageIcon(this.getClass().
                getResource(Globals.IMG_ASTEROID)).getImage();

        displayHelpDialog();

        establishConnection();
    }

    public void startGame() {
        scorePanel.setStartGameBtnActive(false);
        resetGameVariables();
        
        Image img = new ImageIcon(this.getClass().
                getResource(Globals.IMG_SHIP_STILL)).getImage();

        Image imgThrusting = new ImageIcon(this.getClass().
                getResource(Globals.IMG_SHIP_THRUSTING)).getImage();

        ship = new Ship(gamePanel.getWidth() / 2, gamePanel.getHeight() / 2, 0, 0, 0, 0, img, imgThrusting, this);
        Thread t = new Thread(ship);
        t.start();

        controllerThread = new Thread(this);
        controllerThread.start();
    }

    private void resetGameVariables() {
        if (connected) {
            loadPlayerScores();
        }
        if (ship != null) {
            ship.setAlive(false);
        }
        asteroids.clear();
        ship = null;
        timeSurvived = 0;
        repaint();
    }

    private void endGame() {
        player.setScore(timeSurvived / 1000);
        uploadResults(player);
        System.out.println(player);
        resetGameVariables();
        scorePanel.setStartGameBtnActive(true);
    }

    public void draw(Graphics g) {
        if (ship != null) {
            ship.draw(g);
        }
        for (GameObject asteroid : asteroids) {
            asteroid.draw(g);
        }
    }

    public void repaint() {
        gamePanel.repaint();
    }

    public void keyEvent(KeyEvent evt, boolean isPressed) {
        if (ship != null) {
            if (evt.getKeyChar() == 'w' || evt.getKeyChar() == 'W') {
                ship.setThrust(isPressed);
            }
            if (evt.getKeyChar() == 'a' || evt.getKeyChar() == 'A') {
                ship.setTurnLeft(isPressed);
            }
            if (evt.getKeyChar() == 'd' || evt.getKeyChar() == 'D') {
                ship.setTurnRight(isPressed);
            }
        }
    }

    private void createNewAsteroid() {
        GameObject asteroid = null;

        do {
            float posX = (float) (Math.random() * gamePanel.getPreferredSize().width);
            float posY = (float) (Math.random() * gamePanel.getPreferredSize().height);
            float dX = (float) (Math.random() * Globals.ASTEROID_MAX_VELOCITY) - Globals.ASTEROID_MAX_VELOCITY / 2;
            float dY = (float) (Math.random() * Globals.ASTEROID_MAX_VELOCITY) - Globals.ASTEROID_MAX_VELOCITY / 2;
            float angle = (float) (Math.random() * 360);
            float angVel = (float) (Math.random() * Globals.ASTEROID_MAX_ROTATION);

            asteroid = new GameObject(posX, posY, dX, dY, angle, angVel, imgAsteroid, this);

        } while (ship.collides(asteroid, Globals.ASTEROID_SPAWN_DISTANCE_MULTIPLER));

        asteroids.add(asteroid);
        Thread asteroidThread = new Thread(asteroid);
        asteroidThread.start();
    }

    @Override
    public void run() {

        while (ship != null && ship.isAlive()) {

            if (asteroids.size() < Globals.ASTEROIDS_START_COUNT + timeSurvived / Globals.ASTEROID_SPAWN_MILLISECONDS) {
                createNewAsteroid();
            }

            shipCollision();

            try {
                Thread.sleep(Globals.SLEEP_TIME);
                timeSurvived += Globals.SLEEP_TIME;
                scorePanel.setTimeSurvived(timeSurvived);
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Thread.sleep(Globals.SLEEP_TIME*30);
        } catch (InterruptedException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        endGame();
    }

    private void shipCollision() {
        for (GameObject asteroid : asteroids) {
            if (ship.collides(asteroid)) {
                asteroid.setAlive(false);
                asteroids.remove(asteroid);

                ship.setAlive(false);
            }
        }
    }

    private String displayNameDialog(boolean taken) {
        while (true) {
            String s;
            if (taken) {
                s = (String) JOptionPane.showInputDialog(gamePanel, "That name is already taken, please chose another one:\n");
            } else {
                s = (String) JOptionPane.showInputDialog(gamePanel, "Please enter your name:\n");
            }

            if ((s != null) && (s.length() > 0) && (!s.contains(";"))) {
                player.setName(s);
                return s;
            }
        }
    }

    private String displayPasswordDialog(boolean newPass) {
        newUser = newPass;
        while (true) {
            String s;
            if (newPass) {
                s = (String) JOptionPane.showInputDialog(gamePanel, "Enter a password to finish registration:\n(and make sure to remember it next time)");
            } else {
                s = (String) JOptionPane.showInputDialog(gamePanel, "Enter your password to log in:");
            }

            if ((s != null) && (s.length() > 0) && (!s.contains(";"))) {
                return s;
            }
        }
    }

    private void displayConnectionFailureDialog() {
        JOptionPane.showMessageDialog(gamePanel, "Failed to connect to the server!\nMake sure you have internet connection, and click on the reconnect button to enable saving.");
        scorePanel.setConnected(false); // tudom hogy 2x van meghívva, így biztosabb.
    }

    public void displayHelpDialog() {
        JOptionPane.showMessageDialog(gamePanel, Globals.HELP_STRING);
    }

    public void establishConnection() {
        //<editor-fold defaultstate="collapsed" desc="source">
        // source: https://stackoverflow.com/questions/30265720/java-joptionpane-radio-buttons
        //</editor-fold>

        String[] values = {"online server", "localhost"};

        Object selected = JOptionPane.showInputDialog(gamePanel, "", "Choose a server", JOptionPane.DEFAULT_OPTION, null, values, "online server");
        if (selected != null) {
            String selectedString = selected.toString();
            if("localhost".equals(selectedString)) {
                establishConnection("localhost");
            } else {
                establishConnection(Globals.SRV_ADRESS);
            }
        } else {
            System.out.println("User cancelled");
        }
    }

    private void establishConnection(String address ){
        try {
            Socket connection = new Socket(address, Globals.SRV_PORT);
            clientWorker = new ClientWorker(connection, this);
            clientWorker.start();
            scorePanel.setConnected(true);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            displayConnectionFailureDialog();
            scorePanel.setConnected(false);
        }
    }

    public void loadPlayerScores() {
        Thread loaderThread = new ScoreLoader(this, clientWorker, scorePanel);
        loaderThread.start();
    }

    private void uploadResults(Player player) {
        if (clientWorker != null) {
            clientWorker.setScore(player.getScore());
        }
        /*
        boolean needsUpdate = false;
        if (playerList.contains(player)) {
            for (Player previousP : playerList) {
                if (previousP.equals(player)) {
                    needsUpdate = player.getScore() > previousP.getScore();
                }
            }
            if (needsUpdate) {
                dao.updateRecord(player);
            }
        } else {
            dao.createRecaord(player);
            playerList.add(player);
        }*/
    }

    public void focusOnPanel() {
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
    }

    public String askForName() {
        String name;
        name = displayNameDialog((userNameTryCount > 0)); // <- bool
        userNameTryCount++;
        scorePanel.setPlayerName(name);
        return name;
    }

    public String askForNewPass() {
        return displayPasswordDialog(true);
    }

    public String askForOldPass() {
        return displayPasswordDialog(false);
    }

    public void loggedIn() {
        if (newUser) {
            JOptionPane.showMessageDialog(gamePanel, "Successful login!\nYou can use the same details next time!");
        } else {
            JOptionPane.showMessageDialog(gamePanel, "Welcome back, pilot!\n");
        }
        userNameTryCount = 0;
        loadPlayerScores();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        scorePanel.setConnected(connected);
    }

    public void clearPlayerList() {
        playerList.clear();
    }

    public synchronized boolean isLoaderActive() {
        return loaderActive;
    }

    public synchronized void setLoaderActive(boolean loaderActive) {
        this.loaderActive = loaderActive;
    }
}

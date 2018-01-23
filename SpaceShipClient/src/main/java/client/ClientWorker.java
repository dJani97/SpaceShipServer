package client;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import control.Controller;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Message;
import model.Player;

/**
 *
 * @author dobszai
 */
public class ClientWorker extends Thread {
    //private String name;
    private int score = 0;
    private Controller controller;
    private long waitingTimeForScores = 10;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private boolean connected;
    private List<Player> playerList = new ArrayList<>();
    private boolean playerListReady;
    private enum Status {
        UNKNOWN, NEW_USR_REGISTRATION, EXISTING_USR_LOGIN, LOGGED_IN 
    }
    Status userStatus = Status.UNKNOWN;
    
    
    public ClientWorker(Socket connection, Controller controller) {
        this.controller = controller;
        this.connection = connection;
    }
        
    public ClientWorker(String ipAdress, int port) throws UnknownHostException, IOException {
        // egyéb célokra, mind például bejelentkezés nélküli HIGHSCORE lekérdezés
        // (Androidos lekérdező projekt folyamatban)
        // + feltétel a hibá külső lekezelése
        System.out.println(InetAddress.getByName(ipAdress));
        Socket newConnection = new Socket(InetAddress.getByName(ipAdress), port);
        this.connection = newConnection;
        setupStreams();
    }

    //<editor-fold defaultstate="collapsed" desc="setupStreams()">
    private void setupStreams() throws IOException {
        System.out.print("Setting up streams for new ClientWorker...");
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        System.out.println("...DONE!");
        connected = true;
    }
    //</editor-fold>
    
    @Override
    public void run() {
        Message message;
        
        if(!connected) {
            // erre azért van szükség, mert kívülről is meghívható a setupStreams()
            // metódus, ez esetben pedig alapból (connected) lesz a kliens
            try {
            setupStreams();
            } catch (IOException ex) {
                Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
                connected = false;
            }
        }
                
        while(connected) {
            if (userStatus == Status.UNKNOWN) {
                String name = controller.askForName();
                sendMessage(new Message("NAME", name));
                userStatus = null;
                controller.setConnected(true);
            }
            
            message = null;                        
            try {
                message = (Message) input.readObject();
                //if(!"HEARTBEAT".equals(message.getCommand())) System.out.println(message + " from " + connection);
                
                switch(message.getCommand()) {
                    case "HEARTBEAT":
                        sendMessage(new Message("HEARTBEAT"));
                        break;
                        
                    case "SCOREBOARD_START":
                        System.out.println("Starting to read palyer scores...");
                        break;
                        
                    case "USER":
                        processPlayer(message.getContent());
                        break;
                      
                    case "WRONGNAME":
                        userStatus = Status.UNKNOWN;
                        break;
                                
                    case "SCOREBOARD_END":
                        playerListReady = true;
                        break;
                        
                    case "NEWPASS":
                        userStatus = Status.NEW_USR_REGISTRATION;
                        sendMessage(new Message("PASS", controller.askForNewPass()));
                        break;
                        
                    case "OLDPASS":
                        userStatus = Status.EXISTING_USR_LOGIN;
                        sendMessage(new Message("PASS", controller.askForOldPass()));
                        break;
                    
                    case "PWACCEPT":
                        userStatus = Status.LOGGED_IN;
                        controller.loggedIn();
                        break;
                    
                        
                    default:
                        System.out.println("Unknown command: " + message);
                }                

            } catch (Exception ex) {
                Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
                connected = false;
            }
        }
        closeConnection();
    }
    
    //<editor-fold defaultstate="collapsed" desc="closeConnection()">
    private void closeConnection(boolean notifyClient) {
        controller.setConnected(false);
        if(notifyClient) {
            sendMessage(new Message("CLOSE"));
            System.out.println("Closing connection w/ notification to: " + connection);
        } else {
            System.out.println("Closing connection w/o notification to: " + connection);
        }
        try {
            input.close();
        } catch (IOException ex) {
            //Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.close();
        } catch (IOException ex) {
            //Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connection.close();
        } catch (IOException ex) {
            //Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        connected = false;
    }
    public void closeConnection() {
        closeConnection(false);
    }//</editor-fold>
    
    public void setScore(int newScore) {
        if(newScore > score) {
            score = newScore;
        }
        
        if(userStatus == Status.LOGGED_IN) {
            System.out.println("sending score!");
            sendMessage(new Message("SCORE", Integer.toString(score)));
        }
    }
    
    public List<Player> getHighScores() {
        playerListReady = false;
        playerList = new ArrayList<>();
        sendMessage(new Message("SCOREBOARD"));
        
        while(!playerListReady) {
            try {
                Thread.sleep(waitingTimeForScores);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return Collections.unmodifiableList(playerList);
    }
    
    private void sendMessage(Message message) {
        //System.out.println("outgoing: " + message);
        if(connected) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException ex) {
                Logger.getLogger(ClientWorker.class.getName()).log(Level.SEVERE, null, ex);
                connected = false;
            }
        }
    }

    private void processPlayer(String content) {
        String[] data = content.split(";");
        String name = data[0];
        int score = Integer.parseInt(data[1]);
        Player player = new Player(name, score);
        playerList.add(player);
    }
}

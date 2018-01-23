/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import control.Controller;
import control.Globals;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Message;
import model.User;

/**
 * Szerver szálat kezelő osztály
 * @author dobszai
 */
public class ServerWorker implements Runnable {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private boolean connected;
    private User user = null;
    private int timeout = 0;
    private Timer timer;
    private Controller controller;
    private enum Status {
        UNKNOWN, NEW_USR_REGISTRATION, EXISTING_USR_LOGIN, LOGGED_IN 
    }
    Status userStatus = Status.UNKNOWN;

    /**
     * Konstruktor
     * @param connection Socket kapcsolat
     * @param controller vezérlő osztály
     */
    public ServerWorker(Socket connection, Controller controller) {
        this.controller = controller;
        this.connection = connection;
    }
    
    @Override
    public void run() {
        user = new User();
        Message message;
        
        try {
            setupStreams();
            startTimeoutTimer();
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while (connected) {
            try {
                message = (Message) input.readObject();
                if(!"HEARTBEAT".equals(message.getCommand())) System.out.println(message + " from " + connection);

                switch(message.getCommand()) {
                    case "HEARTBEAT":
                        timeout = 0;
                        break;
                        
                    case "CLOSE":
                        disconnect();
                        break;

                    case "SCOREBOARD":
                        sendScoreBoard();
                        break;

                    case "NAME":
                        if(userStatus == Status.UNKNOWN) {
                            if((!message.getContent().contains(";"))
                                    && message.getContent().length() > 0) {
                                user.setName(message.getContent());
                                if(!controller.isUserInDB(user.getName())) {
                                    userStatus = Status.NEW_USR_REGISTRATION;
                                    sendMessage(new Message("NEWPASS"));
                                } else {
                                    userStatus = Status.EXISTING_USR_LOGIN;
                                    sendMessage(new Message("OLDPASS"));
                                }
                            } else {
                                sendMessage(new Message("WRONGNAME"));
                            }
                        }
                        break;

                    case "PASS":
                        if(userStatus == Status.NEW_USR_REGISTRATION) {
                            if((!message.getContent().contains(";"))
                                    && message.getContent().length() > 0) {
                                user.setPass(message.getContent());
                                controller.createUser(user);
                                sendMessage(new Message("PWACCEPT"));
                                userStatus = Status.LOGGED_IN;
                            } else {
                                sendMessage(new Message("NEWPASS"));
                            }
                            
                        } else if(userStatus == Status.EXISTING_USR_LOGIN) {
                            user = controller.getUser(user.getName());
                            if (message.getContent().equals(user.getPass())) {
                                sendMessage(new Message("PWACCEPT"));
                            } else {
                                sendMessage(new Message("OLDPASS"));
                            }
                        }
                        break;
                        
                    case "SCORE":
                        try {
                            int incomingScore = Integer.parseInt(message.getContent());
                            System.out.println("UPDATE request for " + user.getName() + " with " + incomingScore);
                            if((incomingScore >= user.getScore()) && (incomingScore < Globals.USR_SCORE_LIMIT)) {
                                user.setScore(incomingScore);
                                controller.updateUserScore(user);
                            } else {
                                System.out.println("Score (" + incomingScore + ") is lower than before, or too high.");
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println("Client sent an invalid score!");
                        }
                        break;
                        

                    default:
                        System.out.println("Unknown command: " + message);
                }
                
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
                connected = false;
            }
            
        }
        closeConnection();
        controller.deleteWorker(this);
    }

    //<editor-fold defaultstate="collapsed" desc="setupStreams()">
    private void setupStreams() throws IOException {
        System.out.print("Setting up streams for new ServerWorker...");
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        System.out.println("...DONE!");
        this.connected = true;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="closeConnection()">
    private void closeConnection(boolean notifyClient) {
        if(notifyClient) {
            sendMessage(new Message("CLOSE"));
            System.out.println("Closing connection w/ notification to: " + connection);
        } else {
            System.out.println("Closing connection w/o notification to: " + connection);
        }
        try {
            input.close();
        } catch (IOException ex) {
            //Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output.close();
        } catch (IOException ex) {
            //Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            connection.close();
        } catch (IOException ex) {
            //Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        timer.setRunning(false);
        connected = false;
    }
    
    public void closeConnection() {
        closeConnection(false);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="startTimeoutTimer()">
    private void startTimeoutTimer() {
        timer = new Timer(this, Globals.SRV_TIMEOUT_TIMER);
        timer.start();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="heartBeat()">

    /**
     * Életjel kérés küldése a kliensnek
     */
    public void heartBeat() {
        sendMessage(new Message("HEARTBEAT"));
        //System.out.println("HEARTBEAT to " + connection);
        
        if(timeout > Globals.SRV_TIMEOUT) {
            System.out.println("Client timeout " + connection);
            disconnect();
            closeConnection();
            timer.setRunning(false);
        }
        
        timeout += Globals.SRV_TIMEOUT_TIMER;
    }
    //</editor-fold>

    private void sendScoreBoard() {
        System.out.println("getting users");
        List<User> users = controller.getUserList();
        System.out.println("sending START signal for scores");
        sendMessage(new Message("SCOREBOARD_START"));
        for (User user : users) {
            sendMessage(new Message("USER", user.getName() + ";" + Integer.toString(user.getScore())));
        }
        System.out.println("sending STOP signal for scores");
        sendMessage(new Message("SCOREBOARD_END"));
    }

    private void sendMessage(Message message) {
        if(connected) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
                connected = false;
            }
        }
    }

    /**
     * Kapcsoalt megszakítása
     */
    public void disconnect() {
        this.connected = false;
    }
}

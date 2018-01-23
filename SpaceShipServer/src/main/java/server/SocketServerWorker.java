/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import control.Controller;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bejövő kapcsolatokat fogadó szerverszál
 * @author djani
 */
public class SocketServerWorker implements Runnable {
   
    private ServerSocket server;
    private int port;
    private Controller controller;
    private boolean active;
    
    /**
     * Kapcsolatokat kezelő szál létrehozása
     * @param controller Kontroller osztály
     * @param port használni kívánt port
     */
    public SocketServerWorker(Controller controller, int port) {
        System.out.println("Starting SpaceShip server...");
        this.controller = controller;
        this.port = port;
    }
    
    private void startWorker(Socket connection){
        ServerWorker worker = new ServerWorker(connection, controller);
        Thread workerThread = new Thread(worker);
        workerThread.start();
        controller.addServerWorker(worker);
    }

    @Override
    public void run() {
        active = true;
        try {
            server = new ServerSocket(port, 100);
            while (active) {
                try {
                    System.out.println("Waiting for connection...");
                    Socket connection = server.accept();
                    System.out.println("Accepting connection from: " + connection);
                    startWorker(connection);
                    
                } catch (EOFException ex) {
                    System.out.println("Failed to connect to client!");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Szál aktivitásának lekérdezése
     * @return igaz/hamis
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Szál aktivitásának beállítása
     * @param active aktivitás beállítása (igaz/hamis)
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Izőzítő osztály
 * @author dobszai
 */
public class Timer extends Thread {
    ServerWorker worker;
    boolean running;
    int thickMilisecons;

    /**
     * Új időzítő létrehozása
     * @param worker szerver szál
     * @param thickMilisecons kattintások közötti idő
     */
    public Timer(ServerWorker worker, int thickMilisecons) {
        this.worker = worker;
        this.thickMilisecons = thickMilisecons;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                worker.heartBeat();
                Thread.sleep(thickMilisecons);
            } catch (InterruptedException ex) {
                Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @return szerver szál
     */
    public ServerWorker getWorker() {
        return worker;
    }

    /**
     * Időzítő állapotának lekérése
     * @return (igaz/hamis)
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Időzítő állapotának megváltoztatása
     * @param running (igaz/hamis)
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}

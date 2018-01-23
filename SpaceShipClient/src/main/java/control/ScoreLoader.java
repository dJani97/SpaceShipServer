/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import client.ClientWorker;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Player;
import view.ScorePanel;

/**
 *
 * @author dobszai
 */
public class ScoreLoader extends Thread {
    
    Controller controller;
    ClientWorker clientWorker;
    ScorePanel scorePanel;

    public ScoreLoader(Controller controller, ClientWorker clientWorker, ScorePanel scorePanel) {
        this.controller = controller;
        this.clientWorker = clientWorker;
        this.scorePanel = scorePanel;
    }    
    
    @Override
    public void run() {
        if(!controller.isLoaderActive()){
            controller.setLoaderActive(true);
            System.out.println("Loader alive");
            try {
                if (clientWorker != null) {
                    Thread.sleep(Globals.SCORE_REF_DELAY);
                    System.out.println("Loader started");
                    List<Player> playerList = clientWorker.getHighScores();
                    System.out.println(playerList);
                    scorePanel.clearAllPlayers();
                    scorePanel.addPlayers(playerList);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        controller.setLoaderActive(false);
    }
}

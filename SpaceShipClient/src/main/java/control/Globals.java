/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

/**
 *
 * @author djani
 */
public class Globals {
    public static final String SRV_ADRESS = "80.211.202.225"; //szerverem: 80.211.202.225
    public static final int SRV_PORT = 8888; //szerverem: 8888
    public static final long SCORE_REF_DELAY = 1000;
    
    public static final int SLEEP_TIME = (int) 1000/60;
    public static final int PANEL_HEIGHT = 700;
    public static final int PANEL_WIDTH = 900;
    public static final float SHIP_TURN_VELOCITY = 4.5f;
    public static final int SHIP_TURN_ACCELERATION = 5; // frames untill full rotation
    public static final float SHIP_ACCELERATION = 0.5f;
    public static final float SHIP_MAX_VELOCITY = 5f;
    
    public static final int ASTEROIDS_START_COUNT = 0;
    public static final float ASTEROID_MAX_VELOCITY= 2f;
    public static final float ASTEROID_MAX_ROTATION= 2f;
    public static final int ASTEROID_SPAWN_DISTANCE_MULTIPLER = 4;
    public static final int ASTEROID_SPAWN_MILLISECONDS = 1000;
    
    public static final String GAME_TITLE = "SpaceShip";
    
    public static final String IMG_ASTEROID = "/images/asteroid_blue.png";
    public static final String IMG_SHIP_STILL = "/images/shipStill.png";
    public static final String IMG_SHIP_THRUSTING = "/images/shipThrusting.png";
    public static final String IMG_SHOT = "/images/shot.png";
    public static final String IMG_BACKGROUND = "/images/background.jpg";
    public static final String IMG_DERBIS = "/images/debris2_blue.png";
    
    public static final String HELP_STRING = "Welcome to SpaceShip!\n"
            + "You can control the ship with the W, A and D buttons. Your goal is to avoid all asteroids as far as possible.\n"
            + "In order to be able to save, you must log in to the server.\n"
            + "You can reopen this dialog by clicking on the (?) in the upper right corner.\n\n"
            + "Good luck!";
    
}

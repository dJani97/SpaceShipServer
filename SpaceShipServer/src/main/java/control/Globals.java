/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

/**
 *
 * @author dobszai
 */
public class Globals {
    public static final int SRV_PORT = 8888;
    public static final int SRV_TIMEOUT = 200000;            // ms
    public static final int SRV_TIMEOUT_TIMER = 1000;       // ms
    public static final int SRV_SLEEP_WHEN_IDLE = 100;
    public static final int LOG_SLEEP_TIME = 15000;
    public static final boolean LOG_EVENT_DRIVEN_LOGGING = true;
    public static final String DB_TABLENAME = "SPACESHIP_2";
    public static final String DB_DBNAME = "SpaceShipDB_2";
    public static final String[] FORBIDDEN_NAMES = {"testDummy"};
    
    public static final int USR_SCORE_LIMIT = 250;
    
    
    /* list of possible commands:
    HEARTBEAT               (in/out)
    NAME                    (in)
    SCOREBOARD              (in)
    SCOREBOARD_START        (out)       ezek között:
    SCOREBOARD_END          (out)           Message("username", "score")
    CLOSE                   (in/out)
    PASS                    (in)        bejövő jelszó
    NEWPASS                 (out)       új jelszó kérése
    OLDPASS                 (out)       létező jelszó bekérése
    PWACCEPT                (out)       jelszó elfogadva
    USER                    (out)       a single user's data ("USER", "name;score")
    
    
    */
}

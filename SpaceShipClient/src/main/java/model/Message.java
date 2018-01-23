/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 * Hálózati kommunikációra használt üzenet objektum
 * @author dobszai
 */
public class Message implements Serializable {
    
    String command;
    String content;

    /**
     * Új üzenet létrehozása
     * @param command parancs
     * @param content tartalom
     */
    public Message(String command, String content) {
        this.command = command;
        this.content = content;
    }
    /**
     * Új üzenet létrehozása
     * @param commandOnly parancs
     */
    public Message(String commandOnly) {
        this.command = commandOnly;
        this.content = null;
    }

    /**
     * Parancs lekérdezése
     * @return parancs
     */
    public String getCommand() {
        return command;
    }

    /**
     * Tartalom lekérdezése
     * @return tartalom
     */
    public String getContent() {
        return content;
    }
    
    @Override
    public String toString() {
        return "" + command + " " + content;
    }
}

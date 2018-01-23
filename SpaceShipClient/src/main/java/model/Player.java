/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Egy játékos adatait tároló osztály
 * @author djani
 */
public class Player implements Serializable, Comparable<Player> {
    
    private String name;
    private int score;

    /**
     * Új üres játékos létrehozása
     */
    public Player() {
        this.name = "";
        this.score = 0;
    }
    
    /**
     * Új játékos létrehozása névvel
     * @param name név
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
    }
    
    /**
     * Új játékos létrehozása névvel és pontszámmal
     * @param name név
     * @param score pontszám
     */
    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     *
     * @return játékos név
     */
    public String getName() {
        return name;
    }

    /**
     * Játékos név módosítása
     * @param name játékos új neve
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Játékos pontszámának lekérdezése
     * @return játékos pontszáma
     */
    public int getScore() {
        return score;
    }

    /**
     * Játékos pontszámának módosítása
     * @param score játékos új pontszáma
     */
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Player o) {
        return o.score - this.score;
    }

    @Override
    public String toString() {
        return name + ":" + score;
    }
}

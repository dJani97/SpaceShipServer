/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * Felhasználót tároló osztály
 * @author dobszai
 */
public class User {
    String name;
    String pass;
    int score;

    /**
     * Új felhasználó létrehozása
     * @param name név
     * @param pass jelszó
     * @param score pontszám
     */
    public User(String name, String pass, int score) {
        this.name = name;
        this.pass = pass;
        this.score = score;
    }
    
    /**
     * Új üres felhasználó létrehozása
     */
    public User() {
        this.name = null;
        this.pass = null;
        this.score = 0;
    }
    
    /**
     *
     * @return név
     */
    public String getName() {
        return name;
    }

    /**
     * új név
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return jelszó
     */
    public String getPass() {
        return pass;
    }

    /**
     * új jelszó
     * @param pass
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     *
     * @return pontszám
     */
    public int getScore() {
        return score;
    }

    /**
     *
     * @param score új pontszám
     */
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", score=" + score + '}';
    }
    
    
    
    
}

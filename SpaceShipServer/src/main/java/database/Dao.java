/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.util.List;
import model.User;

/**
 * Generikus adatelérő objektum
 * @author dobszai
 */
public interface Dao <Type, PrimaryKey> {

    /**
     * Adott Type-ú rekordok kilistázása
     * @return rekorlista
     */
    public List<Type> listAllRecords();

    /**
     * Új rekordot hoz létre az adatbázisban az adott Type alapján
     * @param t tárolni kívánt objektum
     */
    public void createRecord(Type t);

    /**
     * Adott Type-ú rekord lekérése
     * @param k elsődleges kulcs
     * @return rekord
     */
    public Type readRecord(PrimaryKey k);

    /**
     * Adott Type-ú rekord frissítése
     * @param t frissíteni kívánt rekord
     */
    public void updateRecord(Type t);

    /**
     * Adott Type-ú rekord törlése
     * @param t törölni kívánt rekord
     */
    public void deleteRecord(Type t);

    /**
     * Megállapítja hogy adott Type-ú rekord létezik-e az adatbázisban
     * @param k keresett rekord elsődleges kulcsa
     * @return igaz / hamis
     */
    public boolean isRecordInDB(PrimaryKey k);
}
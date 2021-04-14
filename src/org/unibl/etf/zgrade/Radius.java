package org.unibl.etf.zgrade;

import java.io.Serializable;

/**
 * @author dimitrije
 */
public class Radius implements Serializable{
    private int sjever;
    private int jug;
    private int istok;
    private int zapad;

    public Radius(int sjever, int jug, int istok, int zapad) {
        this.sjever = sjever;
        this.jug = jug;
        this.istok = istok;
        this.zapad = zapad;
    }
    
    public Radius(){
        this.sjever = 0;
        this.jug = 0;
        this.istok = 0;
        this.zapad = 0;
    }
    public int getSjever() {
        return sjever;
    }

    public int getJug() {
        return jug;
    }

    public int getIstok() {
        return istok;
    }

    public int getZapad() {
        return zapad;
    }

    public void setSjever(int sjever) {
        this.sjever = sjever;
    }

    public void setJug(int jug) {
        this.jug = jug;
    }

    public void setIstok(int istok) {
        this.istok = istok;
    }

    public void setZapad(int zapad) {
        this.zapad = zapad;
    }

    @Override
    public String toString() {
        return "Radius{" + "sjever=" + sjever + ", jug=" + jug + ", istok=" + istok + ", zapad=" + zapad + '}';
    }  
}
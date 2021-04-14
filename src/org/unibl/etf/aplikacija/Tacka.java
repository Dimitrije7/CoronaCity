package org.unibl.etf.aplikacija;

import java.io.Serializable;

/**
 * @author dimitrije
 */
public class Tacka implements Serializable{
    private int x;
    private int y;

    public Tacka(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Tacka() {
        this.x=-1;
        this.y=-1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.y;
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
        final Tacka other = (Tacka) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
}
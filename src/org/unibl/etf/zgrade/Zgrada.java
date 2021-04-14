package org.unibl.etf.zgrade;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import org.unibl.etf.stanovnici.Osoba;

/**
 * @author dimitrije
 */
public class Zgrada implements Serializable{
    public CopyOnWriteArrayList<Osoba> listaOsoba;

    public Zgrada() {
        this.listaOsoba=new CopyOnWriteArrayList<>();
    } 
}
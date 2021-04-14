package org.unibl.etf.aplikacija;

import java.io.Serializable;

/**
 * @author dimitrije
 */
public class Alarm implements Serializable{
    private int idKuceZr;//kuca zarazene osobe
    private Tacka pozicijaZr;//mjesto na kojem se nalazi zarazena osoba

    public Alarm(int idKuceZr, Tacka pozicijaZr) {
        this.idKuceZr = idKuceZr;
        this.pozicijaZr = pozicijaZr;
    }

    public int getIdKuceZr() {
        return idKuceZr;
    }

    public Tacka getPozicijaZr() {
        return pozicijaZr;
    }

    public void setIdKuceZr(int idKuceZr) {
        this.idKuceZr = idKuceZr;
    }

    public void setPozicijaZr(Tacka pozicijaZr) {
        this.pozicijaZr = pozicijaZr;
    }
}
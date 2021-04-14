package org.unibl.etf.zgrade;

import java.util.Random;
import javafx.scene.paint.Color;
import static org.unibl.etf.aplikacija.Main.mapaKuca;
import static org.unibl.etf.aplikacija.Main.mapaPunktova;
import static org.unibl.etf.aplikacija.Main.pomMapaKuca;
import org.unibl.etf.aplikacija.Parametri;
import org.unibl.etf.aplikacija.Tacka;
import org.unibl.etf.stanovnici.Osoba;

/**
 * @author dimitrije
 */
public class Kuca extends Zgrada{
    private int idKuce;
    private int brojUkucana;
    private Tacka pozicija;
    private float r;
    private float g;
    private float b;
    private transient Color boja;

    public Kuca(int idKuce, Tacka pozicija,float rKomp,float gKomp,float bKomp) {
        super();
        this.idKuce = idKuce;
        this.brojUkucana = 0;
        this.pozicija = pozicija;
        this.r=rKomp;
        this.b=bKomp;
        this.g=gKomp;
        this.boja=Color.color(r, g, b);
    }
    
    public Tacka getPozicija() {
        return pozicija;
    }

    public int getIdKuce() {
        return idKuce;
    }

    public int getBrojUkucana() {
        return brojUkucana;
    }

    public Color getBoja() {
        return boja;
    }

    public void setBoja(Color boja) {
        this.boja = boja;
    }
        
    public void setBrojUkucana(int brojUkucana) {
        this.brojUkucana = brojUkucana;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }
    
    
    public void setBoja(){
        this.boja=Color.color(r,g,b);
    }
    
    public void dodajUkucana(Osoba o){
        this.listaOsoba.add(o);
        this.brojUkucana++;
    }
    
    
    public static void napraviKuce(int brojKuca){
        for(int i=0;i<brojKuca;++i){           
            Random rand=new Random();
            float r=rand.nextFloat();
            float g=rand.nextFloat();
            float b=rand.nextFloat();
            boolean indikator=true;
            while(indikator){
                int x=2+rand.nextInt(Parametri.VELICINA_MAPE-3);
                int y=2+rand.nextInt(Parametri.VELICINA_MAPE-3);
                Tacka t=new Tacka(x,y);
                
                
                if(!mapaPunktova.containsKey(t)){
                    if(!pomMapaKuca.containsKey(t)){
                        indikator=false;
                        Kuca k=new Kuca(i,t,r,g,b);
                        pomMapaKuca.put(t, k);
                        mapaKuca.put(i, k);
                    }
                }
            }
        }
    }
}
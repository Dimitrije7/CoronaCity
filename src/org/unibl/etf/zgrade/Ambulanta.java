package org.unibl.etf.zgrade;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import static org.unibl.etf.aplikacija.Main.mapaKuca;
import org.unibl.etf.aplikacija.MojLogger;
import org.unibl.etf.aplikacija.Parametri;
import org.unibl.etf.aplikacija.Tacka;
import org.unibl.etf.stanovnici.Osoba;

/**
 * @author dimitrije
 */
public class Ambulanta extends Zgrada implements Runnable,Serializable{
    private static int ID_ZA_AMBULANTE=0;
    private int kapacitet;
    private int id;
    private boolean flagKraj;
    public Tacka pozicija;
    
    public Ambulanta() {
        super();
        this.kapacitet=0;
        this.id=ID_ZA_AMBULANTE++;
        this.flagKraj=false;
        this.pozicija=new Tacka();
    }

    @Override
    public String toString() {
        return "Zarazeni u ambulanti "+this.id+": "+this.listaOsoba.size()+"/"+this.kapacitet+"\n";
    }

    public void setFlagKraj(boolean flagKraj) {
        this.flagKraj = flagKraj;
    }
        
    
    private void postaviKapacitet(){
        Random rand=new Random();
        int pocetak=(int)Math.round((Parametri.BROJ_ODRASLIH+Parametri.BROJ_STARIH+Parametri.BROJ_DIJECE)*0.1);
        int kraj=(int)Math.round((Parametri.BROJ_ODRASLIH+Parametri.BROJ_STARIH+Parametri.BROJ_DIJECE)*0.3);
        int interval=kraj-pocetak;
        this.kapacitet=pocetak+rand.nextInt(interval+1);
    }
    
    public void dodajPacijenta(Osoba o){
        this.listaOsoba.add(o);
        o.getPozicija().setX(this.pozicija.getX());
        o.getPozicija().setY(this.pozicija.getY());
    }

    public int getKapacitet() {
        return kapacitet;
    }
    
    public int getStanjeZarazenih(){
        return this.listaOsoba.size();
    }
    
    public int getId(){
        return id;
    }
    @Override
    public void run(){        
        postaviKapacitet();
        while(!flagKraj){
            if(!this.listaOsoba.isEmpty()){
                for(Osoba o:this.listaOsoba){
                    LinkedBlockingDeque<Double> temp=o.getTemperature();
                    if(temp.size()>=3){
                        double tempX=temp.pop();
                        double tempY=temp.pop();
                        double tempZ=temp.pop();
                        double prosjek=(double)(tempX+tempY+tempZ)/3;
                        if(prosjek<37){
                            o.setFlagBolestan(false);
                            //vrati ga kuci
                            o.otpustiIzAmbulante();
                            listaOsoba.remove(o);
                            Parametri.azurirajStanje(o, false);
                            //postaviti ostalima iz kuce da se mogu kretati
                            Kuca kuca=mapaKuca.get(o.getIdKuce());
                            long brojBolesnih=kuca.listaOsoba
                                                  .stream()
                                                  .filter(s->s.isFlagBolestan())
                                                  .count();
                            if(brojBolesnih==0){
                                for(Osoba s:kuca.listaOsoba){
                                    s.setFlagZabrana(false);
                                }
                            }
                        }
                    }
                }
            }
            if(!flagKraj){
                try {
                    Thread.sleep(45000);
                } catch (InterruptedException ex) {
                    MojLogger.log(Level.SEVERE,"Problem sa ambulantom",ex);
                }
            }
        }
    }
}
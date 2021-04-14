package org.unibl.etf.stanovnici;

import static java.lang.Thread.sleep;
import java.util.Random;
import java.util.logging.Level;
import static org.unibl.etf.aplikacija.Main.listaOsoba;
import static org.unibl.etf.aplikacija.Main.mapaKuca;
import org.unibl.etf.aplikacija.MojLogger;
import org.unibl.etf.aplikacija.Parametri;
import org.unibl.etf.aplikacija.Tacka;
import org.unibl.etf.zgrade.Kuca;
import org.unibl.etf.zgrade.Radius;

/**
 * @author dimitrije
 */
public class Dijete extends Osoba{

    public Dijete(String ime, String prezime, String pol, int godinaRodjenja, int id, int idKuce, Tacka pozicija,float rKomp,float gKomp,float bKomp) {
        super(ime, prezime, pol, godinaRodjenja, id, idKuce, pozicija,rKomp,gKomp,bKomp);
    }
    public Dijete(){
        super("","","",1900,-1,-1,new Tacka(),(float)0.5,(float)0.3,(float)0.4);
    }
    @Override
    public void napraviRadius(){
        Kuca kuca=mapaKuca.get(this.getIdKuce());
        int sjever=kuca.getPozicija().getY()-1;
        int jug=Parametri.VELICINA_MAPE-3-sjever;
        int zapad=kuca.getPozicija().getX()-1;
        int istok=Parametri.VELICINA_MAPE-3-zapad;
        Radius rad=new Radius(sjever,jug,istok,zapad);
        this.setRadius(rad);
    }
        
    @Override
    public void pomjeriSe(Tacka novaPoz){
        int x=this.getPozicija().getX();
        int y=this.getPozicija().getY();
        
        this.getPozicija().setX(novaPoz.getX());
        this.getPozicija().setY(novaPoz.getY());
    }
    
    @Override
    public void idiKuci(){
        if(flagIdiKuci){
            Kuca kuca=mapaKuca.get(this.idKuce);
            int x=this.pozicija.getX();
            int y=this.pozicija.getY();
            
            //ukloni indekse iz stare okoline
            if(!this.pozicija.equals(kuca.getPozicija()) && !flagBolestan){
                while(x!=kuca.getPozicija().getX()){
                    if(kuca.getPozicija().getX()<x){
                        x--;
                        this.pozicija.setX(x);
                    }
                    else{
                        x++;
                        this.pozicija.setX(x);
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException ex) {
                        MojLogger.log(Level.SEVERE,"Problem sa klasom Osoba",ex);
                    }
                }
                while(y!=kuca.getPozicija().getY()){
                    if(kuca.getPozicija().getY()<y){
                        y--;
                        this.pozicija.setY(y);
                    }
                    else{
                        y++;
                        this.pozicija.setY(y);
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException ex) {
                        MojLogger.log(Level.SEVERE,"Problem sa klasomOsoba",ex);
                    }
                }
                this.flagIdiKuci=false;
            }   
        }
    }
    
    public static void napraviDijecu(int brojDijece){
        Random rand=new Random();
        String pol="";
        int godiste=2002;
        if(rand.nextBoolean()){
            pol="muski";
        }
        else
        {
            pol="zenski";
        }
        for(int i=0;i<brojDijece;++i){
            Dijete d=new Dijete();
            d.setIme("dijete"+i);
            d.setPrezime("prezime"+i);
            d.setPol(pol);
            d.setGodinaRodjenja(godiste+rand.nextInt(18));
            d.setIdOsobe(Parametri.ID_COUNTER++);
            listaOsoba.add(d);
        }
    }
}
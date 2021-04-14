package org.unibl.etf.stanovnici;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import javafx.scene.paint.Color;
import static org.unibl.etf.aplikacija.Main.mapa;
import static org.unibl.etf.aplikacija.Main.mapaKuca;
import org.unibl.etf.aplikacija.MojLogger;
import org.unibl.etf.aplikacija.Parametri;
import org.unibl.etf.aplikacija.Polje;
import org.unibl.etf.aplikacija.Tacka;
import org.unibl.etf.zgrade.Kuca;
import org.unibl.etf.zgrade.Radius;

/**
 * @author dimitrije
 */
public abstract class Osoba extends Thread implements Serializable{
    
    protected String ime;
    protected String prezime;
    private String pol;
    private int godinaRodjenja;
    protected int id;
    protected int idKuce;
    private LinkedBlockingDeque<Double> temperature;
    protected Tacka pozicija;
    private transient Color boja;
    private Radius radius;
    private float r;
    private float g;
    private float b;
    protected volatile boolean flagZabrana;
    protected volatile boolean flagBolestan;
    protected volatile boolean flagIdiKuci;
    private volatile boolean flagKraj;
    private volatile boolean flagIspisuj;
    
    public Osoba(String ime, String prezime, String pol, int godinaRodjenja, int id, int idKuce, Tacka pozicija,float rKomp,float gKomp,float bKomp) {
        this.ime = ime;
        this.prezime = prezime;
        this.pol = pol;
        this.godinaRodjenja = godinaRodjenja;
        this.id = id;
        this.idKuce = idKuce;
        this.pozicija = pozicija;
        this.r=rKomp;
        this.b=bKomp;
        this.g=gKomp;
        this.boja=Color.color(r, g, b);
        this.temperature=new LinkedBlockingDeque<>();
        this.temperature.add(36.5);
        this.radius=new Radius();
        this.flagZabrana=false;
        this.flagBolestan=false;
        this.flagIdiKuci=false;
        this.flagKraj=false;
        this.flagIspisuj=false;
    }

    public double getTemperatura() {
        if(temperature.isEmpty()){
            return 37.0;
        }
        return temperature.getFirst();
    } 
        
    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getPol() {
        return pol;
    }

    public int getGodinaRodjenja() {
        return godinaRodjenja;
    }

    public int getIdOsobe() {
        return id;
    }

    public int getIdKuce() {
        return idKuce;
    }

    public Tacka getPozicija() {
        return pozicija;
    }

    public Color getBoja() {
        return boja;
    }

    public Radius getRadius() {
        return new Radius(this.radius.getSjever(),this.radius.getJug()
                      ,this.radius.getIstok(),this.radius.getZapad());
    }
        
    public boolean isFlagZabrana() {
        return flagZabrana;
    }

    public boolean isFlagIdiKuci() {
        return flagIdiKuci;
    }
    
    public boolean isFlagBolestan() {
        return flagBolestan;
    }

    public boolean isFlagIspisuj() {
        return flagIspisuj;
    }

    public LinkedBlockingDeque<Double> getTemperature() {
        return temperature;
    }
    
    public void setFlagBolestan(boolean flagBolestan) {
        this.flagBolestan = flagBolestan;
    }         

    public void setIme(String ime) {
        this.ime = ime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public void setPol(String pol) {
        this.pol = pol;
    }

    public void setGodinaRodjenja(int godinaRodjenja) {
        this.godinaRodjenja = godinaRodjenja;
    }

    public void setIdOsobe(int id) {
        this.id = id;
    }

    public void setIdKuce(int idKuce) {
        this.idKuce = idKuce;
    }

    public void setFlagIdiKuci(boolean flagIdiKuci) {
        this.flagIdiKuci = flagIdiKuci;
    }

    public void setBoja(Color boja) {
        this.boja = boja;
    }

    public void setRadius(Radius radius) {
        this.radius.setSjever(radius.getSjever());
        this.radius.setJug(radius.getJug());
        this.radius.setZapad(radius.getZapad());
        this.radius.setIstok(radius.getIstok());
    }
    
    abstract public void napraviRadius();
    
    public void setTemperatura(double temperatura) {
        if(flagBolestan){
            this.temperature.offer(temperatura);
        }
        else{
            if(this.temperature.isEmpty()){
                this.temperature.offer(temperatura);
            }else{
                this.temperature.remove();
                this.temperature.offer(temperatura);
            }
        }
    }

    public void setFlagZabrana(boolean flagZabrana) {
        this.flagZabrana = flagZabrana;
    }

    public void setFlagKraj(boolean flagKraj) {
        this.flagKraj = flagKraj;
    }
    
    public void otpustiIzAmbulante(){
        Kuca kuca=mapaKuca.get(this.idKuce);
        this.pozicija.setX(kuca.getPozicija().getX());
        this.pozicija.setY(kuca.getPozicija().getY());
    }
    
    public void idiKuci(){
        if(flagIdiKuci){
            Kuca kuca=mapaKuca.get(this.idKuce);
            int x=this.pozicija.getX();
            int y=this.pozicija.getY();
            if(!this.pozicija.equals(kuca.getPozicija()) && !flagBolestan){
                for(int i=x-1;i<=x+1;++i){
                     for(int j=y-1;j<=y+1;++j){
                        Polje pp=mapa.get(new Tacka(i,j));
                        if(!pp.listaId.isEmpty()){
                            pp.listaId.remove(0);
                            mapa.replace(new Tacka(i,j), pp);
                        }
                     }
                 }

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
                        MojLogger.log(Level.SEVERE,"Problem sa klasom Osoba",ex);
                    }
                }
                this.flagIdiKuci=false;
            } 
        }
    }
    
    public void posaljiPorodicuKuci(){
        Kuca kuca=mapaKuca.get(this.idKuce);
        for(Osoba o:kuca.listaOsoba){
            if(!o.equals(this)){
                o.setFlagZabrana(true);
                o.setFlagIdiKuci(true);
            }
        }
    }
    
    @Override
    public String toString() {
        return ime+" "+prezime+" id=("+Integer.toString(id)+") ("+pozicija.getX()+","+pozicija.getY()+")\n";
    } 
    
    public boolean verifikacijaPravca(int pravac){
        int x=pozicija.getX();
        int y=pozicija.getY();
        
        switch (pravac) {
            case 0:
                if(x-1<1){
                    return false;
                }    break;
            case 1:
                if(y-1<1){
                    return false;
                }   break;
            case 2:
                if(x+1>Parametri.VELICINA_MAPE-2){
                    return false;
                }   break;
            default:
                if(y+1>Parametri.VELICINA_MAPE-2){
                    return false;
                }   break;
        }
        return true;
    }
    
    public boolean provjeraPozicije(Tacka novaPoz){
        if(!flagZabrana){
            Polje p=mapa.get(novaPoz);
            int x=p.koordinate.getX();
            int y=p.koordinate.getY();
            for(int i=x-1;i<=x+1;++i){
                for(int j=y-1;j<=y+1;++j){
                    Polje pp=mapa.get(new Tacka(i,j));
                    if(!pp.listaId.isEmpty()){
                        int pom=pp.listaId.get(0);
                        if(pom!=this.idKuce){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public void pomjeriSe(Tacka novaPoz){
        int x=this.pozicija.getX();
        int y=this.pozicija.getY();

        for(int i=x-1;i<=x+1;++i){
             for(int j=y-1;j<=y+1;++j){
                Polje pp=mapa.get(new Tacka(i,j));
                if(!pp.listaId.isEmpty()){
                    pp.listaId.remove(0);
                    mapa.replace(new Tacka(i,j), pp);
                }  
            }
        }
        for(int i=novaPoz.getX()-1;i<=novaPoz.getX()+1;++i){
             for(int j=novaPoz.getY()-1;j<=novaPoz.getY()+1;++j){
                Polje pp=mapa.get(new Tacka(i,j));
                pp.listaId.add(this.idKuce);
                mapa.replace(new Tacka(i,j), pp);
            }
        }
        
        this.pozicija.setX(novaPoz.getX());
        this.pozicija.setY(novaPoz.getY());
    }

    @Override
    public void run(){
        napraviRadius();
        setTemperatura(36.5);
        boolean inicijalniPomjeraj=false;
        while(!inicijalniPomjeraj){
            synchronized(mapa){
                if(!flagZabrana && provjeraPozicije(this.pozicija)){//lazy evaluation caca igrice
                    pomjeriSe(this.pozicija);
                    inicijalniPomjeraj=true;
                    flagIspisuj=true;
                }
            }
            try {
                sleep(500);
            } catch (InterruptedException ex) {
               MojLogger.log(Level.SEVERE,"Problem sa klasom Osoba",ex);
            } 
        }
        Random rand=new Random();
        while(!flagKraj){
            idiKuci();
            while(flagZabrana){
                //idiKuci();
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    MojLogger.log(Level.SEVERE,"Problem sa klasom Osoba",ex);
                }
            }
            
            int noviPravac=rand.nextInt(4);
            
            while(!verifikacijaPravca(noviPravac)){
                noviPravac=rand.nextInt();
            }
            
            int x=this.pozicija.getX();
            int y=this.pozicija.getY();
            synchronized(mapa){
                if(!flagZabrana){
                    switch (noviPravac) {
                        case 0:
                            if(provjeraPozicije(new Tacka(x-1,y))){
                                pomjeriSe(new Tacka(x-1,y));
                            }    break;
                        case 1:
                            if(provjeraPozicije(new Tacka(x,y-1))){
                                pomjeriSe(new Tacka(x,y-1));
                            }    break;
                        case 2:
                            if(provjeraPozicije(new Tacka(x+1,y))){
                                pomjeriSe(new Tacka(x+1,y));
                            }    break;
                        default:
                            if(provjeraPozicije(new Tacka(x,y+1))){
                                pomjeriSe(new Tacka(x,y+1));
                            }    break;
                    }
                }
            }
            int spavanje=rand.nextInt(3);
            try {
                sleep((spavanje+1)*1000);
            } catch (InterruptedException ex) {
                MojLogger.log(Level.SEVERE,"Problem sa klasom Osoba",ex);
            }
        }
    }
}
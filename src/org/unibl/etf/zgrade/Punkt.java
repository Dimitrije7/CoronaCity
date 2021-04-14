package org.unibl.etf.zgrade;

import java.io.Serializable;
import java.util.Random;
import java.util.logging.Level;
import org.unibl.etf.aplikacija.Alarm;
import static org.unibl.etf.aplikacija.Main.listaOsoba;
import static org.unibl.etf.aplikacija.Main.mapaPunktova;
import static org.unibl.etf.aplikacija.Main.pomMapaKuca;
import static org.unibl.etf.aplikacija.Main.stackAlarma;
import org.unibl.etf.aplikacija.MojLogger;
import org.unibl.etf.aplikacija.Parametri;
import org.unibl.etf.aplikacija.Tacka;

/**
 * @author dimitrije
 */
public class Punkt extends Thread implements Serializable{
    private Tacka pozicija;
    private boolean flagKraj;

    public Punkt(Tacka pozicija) {
        this.pozicija = pozicija;
        this.flagKraj=false;
    }
    
    private void posaljiAlarm(Tacka pozicija,int idKuce){
        Alarm alarm = new Alarm(idKuce,pozicija);
        stackAlarma.push(alarm);
    }

    public void setFlagKraj(boolean flagKraj) {
        this.flagKraj = flagKraj;
    }
    
    @Override
    public void run() {
        while(!flagKraj){
            synchronized(listaOsoba){
                for(int i=pozicija.getX()-1;i<=pozicija.getX()+1;++i){
                    for(int j=pozicija.getY()-1;j<=pozicija.getY()+1;++j){
                        Tacka t=new Tacka(i,j);
                        if(!pomMapaKuca.containsKey(t)){
                            listaOsoba.stream()
                                      .filter(s->s.getTemperatura()>37 && s.getPozicija().equals(t) && !s.isFlagBolestan())
                                      .forEach(s->{
                                          s.setFlagZabrana(true);
                                          s.setFlagBolestan(true);
                                          posaljiAlarm(s.getPozicija(),s.getIdKuce());
                                          s.posaljiPorodicuKuci();
                                          Parametri.azurirajStanje(s, true);
                                      });
                        }
                    }
                }
            }
            try {
                sleep(300);
            }
            catch (InterruptedException ex){ 
                MojLogger.log(Level.SEVERE,"Problem sa punktom",ex);
            }
        }
    }
    
    public static void napraviPunktove(int brojPunktova){
        while(brojPunktova>0){
            Random rand=new Random();
            Tacka t=new Tacka(rand.nextInt(Parametri.VELICINA_MAPE-3)+2,rand.nextInt(Parametri.VELICINA_MAPE-3)+2);
            if(!pomMapaKuca.containsKey(t)){
                mapaPunktova.put(t,new Punkt(t));
                brojPunktova--;
            }
        }
        mapaPunktova.forEach((k,v)->v.start());
    }
}
package org.unibl.etf.watcher;

import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import static org.unibl.etf.aplikacija.Main.listaOsoba;
import org.unibl.etf.aplikacija.MojLogger;

/**
 * @author dimitrije
 */
public class Ispisivac extends Thread{
    private TextArea konzola;
    
    public Ispisivac(TextArea a){
        konzola=a;
        setDaemon(true);
    }
    
    @Override
    public void run(){
        while(true){
            listaOsoba.forEach(s->{
                if(!s.isFlagZabrana() && s.isFlagIspisuj()){
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            konzola.appendText(s.getIme()+" "+s.getPrezime()+" ID="+s.getIdOsobe()+" ("+s.getPozicija().getX()+","+s.getPozicija().getY()+")\n");
                        }
                    });
                }
            });
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                MojLogger.log(Level.SEVERE, "Nesto ne valja sa ispisivacem", ex);
            }
        }
    }
}
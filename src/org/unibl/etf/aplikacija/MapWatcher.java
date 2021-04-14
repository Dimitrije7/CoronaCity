package org.unibl.etf.aplikacija;

import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import static org.unibl.etf.aplikacija.Main.listaOsoba;
import static org.unibl.etf.aplikacija.Main.mapa;
import static org.unibl.etf.aplikacija.Main.mapaAmbulanti;
import static org.unibl.etf.aplikacija.Main.mapaKuca;
import static org.unibl.etf.aplikacija.Main.mapaPunktova;
import static org.unibl.etf.aplikacija.Main.stackAlarma;
import org.unibl.etf.zgrade.Punkt;

/**
 * @author dimitrije
 */
public class MapWatcher extends Thread{

    private boolean krajFlag;
    private Button dugmeAlarma;
    
    public MapWatcher(Button dugme) {
        super();
        this.krajFlag=false;
        this.dugmeAlarma=dugme;
                
    }

    public void setKrajFlag(boolean krajFlag) {
        this.krajFlag = krajFlag;
    }
    
    @Override
    public void run(){
        while(!krajFlag){
            
            synchronized(mapa){
                if(!stackAlarma.isEmpty()){
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            dugmeAlarma.setStyle("-fx-text-fill:#f54842");
                        }
                    });
                }
                
                Punkt defPolje=new Punkt(new Tacka());
                
                //Resetuj boje svih polja na podrazumjevanu
                mapa.forEach((k,v)->v.polje.setFill(Color.LAVENDER));
                
                //Oboji osobe
                listaOsoba.forEach(s->{
                    Tacka t=s.getPozicija();
                    Polje p=mapa.get(t);
                    p.polje.setFill(s.getBoja());
                    mapa.replace(t, p);
                });
                
                //Obojji kuce
                mapaKuca.forEach((k,v)->{
                    Tacka t=v.getPozicija();
                    Polje p=mapa.get(t);
                    p.polje.setFill(v.getBoja());
                    mapa.replace(t, p);
                });
                
                //Oboji ambulante
                mapaAmbulanti.forEach((k,v)->{
                    Polje p=mapa.get(k);
                    p.polje.setFill(Color.FIREBRICK);
                    mapa.replace(k,p);
                });
                
                //Oboji punktove
                mapa.forEach((k,v)->{
                    if(!defPolje.equals(mapaPunktova.getOrDefault(k,defPolje))){
                        v.polje.setFill(Color.LIGHTBLUE);
                    }
                });
            }
            try {
                sleep(100);
            } catch (InterruptedException ex) {
                MojLogger.log(Level.SEVERE,"Problem sa MapWatcherom",ex);    
            }
        } 
    }
}
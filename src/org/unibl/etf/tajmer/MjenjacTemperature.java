package org.unibl.etf.tajmer;

import java.util.Random;
import static org.unibl.etf.aplikacija.Main.listaOsoba;
import org.unibl.etf.stanovnici.Osoba;

/**
 * @author dimitrije
 */
public class MjenjacTemperature implements Runnable{
    @Override
    public void run(){
        
        Random generator=new Random();
        for (Osoba s : listaOsoba) {
            double novaTemp=0;
            if(generator.nextDouble()>0.25){
                novaTemp=36.0+(double)generator.nextInt(10)/10;
            }
            else{
                novaTemp=37+generator.nextInt(6)+(double)generator.nextInt(10)/10;
            }
            s.setTemperatura(novaTemp);
        }
    }
}
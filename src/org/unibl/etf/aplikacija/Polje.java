package org.unibl.etf.aplikacija;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author dimitrije
 */
public class Polje implements Serializable{
    public Tacka koordinate;
    public transient Rectangle polje;
    public CopyOnWriteArrayList<Integer> listaId;

    public Polje(Tacka koordinate) {
        this.listaId=new CopyOnWriteArrayList<>();
        this.koordinate = koordinate;
        polje=new Rectangle();
        polje.setWidth(Parametri.SIRINA_POLJA);
        polje.setHeight(Parametri.VISINA_POLJA);
        polje.setFill(Color.LAVENDER);
        polje.setStroke(Color.LIGHTGREY);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Polje other = (Polje) obj;
        if (!Objects.equals(this.koordinate, other.koordinate)) {
            return false;
        }
        return true;
    }
    
    public void initializeRectangle(){
        polje=new Rectangle();
        polje.setWidth(Parametri.SIRINA_POLJA);
        polje.setHeight(Parametri.VISINA_POLJA);
        polje.setFill(Color.LAVENDER);
        polje.setStroke(Color.LIGHTGREY);
    }   
}
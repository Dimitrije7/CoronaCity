package org.unibl.etf.aplikacija;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import org.unibl.etf.stanovnici.Osoba;
import org.unibl.etf.zgrade.Ambulanta;
import org.unibl.etf.zgrade.Kuca;
import org.unibl.etf.zgrade.Punkt;

/**
 * @author dimitrije
 */
public class SerijalizabilniGrad implements Serializable{
    public ConcurrentHashMap<Tacka,Polje> mapa;
    public ConcurrentHashMap<Integer,Kuca> mapaKuca;
    public ConcurrentHashMap<Tacka,Kuca> pomMapaKuca;
    public ConcurrentHashMap<Tacka,Punkt> mapaPunktova;
    public LinkedBlockingDeque<Alarm> stackAlarma;
    public CopyOnWriteArrayList<Osoba> listaOsoba;
    public ConcurrentHashMap<Tacka,Ambulanta> mapaAmbulanti;
    
    public int VELICINA_MAPE;
    public int SIRINA_POLJA;
    public int VISINA_POLJA;
    public int MAX_BROJ_KUCA;
    public int BROJ_DIJECE;
    public int BROJ_ODRASLIH;
    public int BROJ_STARIH;
    public int ID_COUNTER;
    public int BROJ_AMBULANTNIH_VOZILA;
    public int BROJ_KONTROLNIH_PUNKTOVA;
    public int BROJ_KUCA;
    public int MAX_BROJ_AMBULANTI;
    public int BROJ_AMBULANTI;
    
    public int BROJ_BOLESNIH;
    public int BROJ_OPORAVLJENIH;
    public int BROJ_BOLESNIH_ODRASLIH;
    public int BROJ_BOLESNIH_STARIH;
    public int BROJ_BOLESNE_DIJECE;
    public int BROJ_BOLESNIH_MUSKARACA;
    public int BROJ_BOLESNIH_ZENA;
    
    public SerijalizabilniGrad(){
        VELICINA_MAPE=Parametri.VELICINA_MAPE;
        SIRINA_POLJA=Parametri.SIRINA_POLJA;
        VISINA_POLJA=Parametri.VISINA_POLJA;
        MAX_BROJ_KUCA=Parametri.MAX_BROJ_KUCA;
        BROJ_DIJECE=Parametri.BROJ_DIJECE;
        BROJ_ODRASLIH=Parametri.BROJ_ODRASLIH;
        BROJ_STARIH=Parametri.BROJ_STARIH;
        ID_COUNTER=Parametri.ID_COUNTER;
        BROJ_AMBULANTNIH_VOZILA=Parametri.BROJ_AMBULANTNIH_VOZILA;
        BROJ_KONTROLNIH_PUNKTOVA=Parametri.BROJ_KONTROLNIH_PUNKTOVA;
        BROJ_KUCA=Parametri.BROJ_KUCA;
        MAX_BROJ_AMBULANTI=Parametri.MAX_BROJ_AMBULANTI;
        BROJ_AMBULANTI=Parametri.BROJ_AMBULANTI;
        BROJ_BOLESNIH=Parametri.BROJ_BOLESNIH;
        BROJ_OPORAVLJENIH=Parametri.BROJ_OPORAVLJENIH;
        BROJ_BOLESNIH_ODRASLIH=Parametri.BROJ_BOLESNIH_ODRASLIH;
        BROJ_BOLESNIH_STARIH=Parametri.BROJ_BOLESNIH_STARIH;
        BROJ_BOLESNE_DIJECE=Parametri.BROJ_BOLESNE_DIJECE;
        BROJ_BOLESNIH_MUSKARACA=Parametri.BROJ_BOLESNIH_MUSKARACA;
        BROJ_BOLESNIH_ZENA=Parametri.BROJ_BOLESNIH_ZENA;
        
        mapa=new ConcurrentHashMap<>(Main.mapa);
        mapaKuca=new ConcurrentHashMap<>(Main.mapaKuca);
        pomMapaKuca=new ConcurrentHashMap<>(Main.pomMapaKuca);
        mapaPunktova=new ConcurrentHashMap<>(Main.mapaPunktova);
        stackAlarma=new LinkedBlockingDeque<>(Main.stackAlarma);
        listaOsoba=new CopyOnWriteArrayList<>(Main.listaOsoba);
        mapaAmbulanti=new ConcurrentHashMap<>(Main.mapaAmbulanti); 
    } 
}
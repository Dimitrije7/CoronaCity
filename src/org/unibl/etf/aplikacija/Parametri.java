package org.unibl.etf.aplikacija;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import static org.unibl.etf.aplikacija.Main.listaOsoba;
import org.unibl.etf.stanovnici.Osoba;

/**
 * @author dimitrije
 */
public class Parametri {
    public static int VELICINA_MAPE;
    public static final int SIRINA_POLJA=20;
    public static final int VISINA_POLJA=20;
    public static final int MAX_BROJ_KUCA=30;
    public static int BROJ_DIJECE;
    public static int BROJ_ODRASLIH;
    public static int BROJ_STARIH;
    public static int ID_COUNTER;
    public static int BROJ_AMBULANTNIH_VOZILA;
    public static int BROJ_KONTROLNIH_PUNKTOVA;
    public static int BROJ_KUCA;
    public static int MAX_BROJ_AMBULANTI;
    public static int BROJ_AMBULANTI=4;
    public static int BROJ_BOLESNIH=0;
    public static int BROJ_OPORAVLJENIH=0;
    public static int BROJ_BOLESNIH_ODRASLIH=0;
    public static int BROJ_BOLESNIH_STARIH=0;
    public static int BROJ_BOLESNE_DIJECE=0;
    public static int BROJ_BOLESNIH_MUSKARACA=0;
    public static int BROJ_BOLESNIH_ZENA=0;
    
    public static void inicijalizacijaParametara(int brojDijece,int brojOdraslih,int brojStarih,int brojHitne,int brojKuca,int brojPunktova){
        ID_COUNTER=0;
        BROJ_DIJECE=brojDijece;
        BROJ_ODRASLIH=brojOdraslih;
        BROJ_STARIH=brojStarih;
        BROJ_AMBULANTNIH_VOZILA=brojHitne;
        BROJ_KUCA=brojKuca;
        BROJ_KONTROLNIH_PUNKTOVA=brojPunktova;
        MAX_BROJ_AMBULANTI=VELICINA_MAPE*4;
    }
    
    public static synchronized void azurirajStanje(Osoba o,boolean bolestan){
        if(bolestan){
            BROJ_BOLESNIH++;
            if((2020-o.getGodinaRodjenja())<18){
                BROJ_BOLESNE_DIJECE++;
            }else if((2020-o.getGodinaRodjenja())<65){
                BROJ_BOLESNIH_ODRASLIH++;
            }else{
                BROJ_BOLESNIH_STARIH++;
            }
            
            if("zenski".equals(o.getPol())){
                BROJ_BOLESNIH_ZENA++;
            }
            else{
                BROJ_BOLESNIH_MUSKARACA++;
            }
        }
        else{
            BROJ_BOLESNIH--;
            if((2020-o.getGodinaRodjenja())<18){
                BROJ_BOLESNE_DIJECE--;
            }else if((2020-o.getGodinaRodjenja())<65){
                BROJ_BOLESNIH_ODRASLIH--;
            }else{
                BROJ_BOLESNIH_STARIH--;
            }
            
            if("zenski".equals(o.getPol())){
                BROJ_BOLESNIH_ZENA--;
            }
            else{
                BROJ_BOLESNIH_MUSKARACA--;
            }
        }
        try{
            BufferedWriter pisac=new BufferedWriter(new FileWriter(".."+File.separator+"CoronaCity"+File.separator+"zarazeni"));
            pisac.write(Integer.toString(BROJ_BOLESNIH)+"\n");
            pisac.write(Integer.toString(listaOsoba.size()-BROJ_BOLESNIH)+"\n");
            pisac.close();
        } catch (FileNotFoundException ex) {
            MojLogger.log(Level.SEVERE, "Nepostojeci fajl sa podacima o zarazenim", ex);
        } catch (IOException ex) {
            MojLogger.log(Level.SEVERE,"Gresla u citanju fajla sa zarazenim",ex);
        }
    }
    
}
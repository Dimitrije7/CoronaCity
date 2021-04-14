package org.unibl.etf.stanovnici;

import java.util.Random;
import static org.unibl.etf.aplikacija.Main.listaOsoba;
import static org.unibl.etf.aplikacija.Main.mapa;
import static org.unibl.etf.aplikacija.Main.mapaKuca;
import org.unibl.etf.aplikacija.Parametri;
import org.unibl.etf.aplikacija.Polje;
import org.unibl.etf.aplikacija.Tacka;
import org.unibl.etf.zgrade.Kuca;
import org.unibl.etf.zgrade.Radius;

/**
 * @author dimitrije
 */
public class StaraOsoba extends Osoba{

    public StaraOsoba(String ime, String prezime, String pol, int godinaRodjenja, int id, int idKuce, Tacka pozicija,float rKomp,float gKomp,float bKomp) {
        super(ime, prezime, pol, godinaRodjenja, id, idKuce, pozicija,rKomp,gKomp,bKomp);
    }
    
    public StaraOsoba(){
        super("","","",1900,-1,-1,new Tacka(),(float)0.5,(float)0.3,(float)0.4);
    }
    private int podesiRadius(int smjerDopune,int trStanje,int smjerProvjere,int indexSmjera,Radius rad){
        if(smjerDopune-trStanje>=smjerProvjere){
            int noviSmjerDopune=trStanje+smjerProvjere;
            switch (indexSmjera) {
                case 0:
                    rad.setSjever(noviSmjerDopune);
                    break;
                case 1:
                    rad.setJug(noviSmjerDopune);
                    break;
                case 2:
                    rad.setIstok(noviSmjerDopune);
                    break;
                default:
                    rad.setZapad(noviSmjerDopune);
                    break;
            }
            smjerProvjere=0;
        }
        else{
            if(smjerDopune-trStanje>0){
                smjerProvjere-=(smjerDopune-trStanje);
                switch (indexSmjera) {
                    case 0:
                        rad.setSjever(smjerDopune);
                        break;
                    case 1:
                        rad.setJug(smjerDopune);
                        break;
                    case 2:
                        rad.setIstok(smjerDopune);
                        break;
                    default:
                        rad.setZapad(smjerDopune);
                        break;
                }
            }
        }
        return smjerProvjere;
    }
    
    @Override
    public void napraviRadius(){
        Kuca kuca=mapaKuca.get(this.getIdKuce());
        int radius=3;
        Radius rad=new Radius();
        //koliko ima slobodnog mjesta na svaku stranu
        int sjever=kuca.getPozicija().getX()-1;
        int jug=Parametri.VELICINA_MAPE-3-sjever;
        int zapad=kuca.getPozicija().getY()-1;
        int istok=Parametri.VELICINA_MAPE-3-zapad;

        int pomSjever=0;
        int pomJug=0;
        int pomZapad=0;
        int pomIstok=0;

        if(sjever>=radius){
            rad.setSjever(radius);
        }
        else{
            rad.setSjever(sjever);
            pomSjever=radius-sjever;
        }
        if(jug>=radius){
            rad.setJug(radius);
        }
        else{
            rad.setJug(jug);
            pomJug=radius-jug;
        }
        if(zapad>=radius){
            rad.setZapad(radius);
        }
        else{
            rad.setZapad(zapad);
            pomZapad=radius-zapad;
        }
        if(istok>=radius){
            rad.setIstok(radius);
        }
        else{
            rad.setIstok(istok);
            pomIstok=radius-istok;
        }

        for(int i=0;i<4;++i){
            switch (i) {
                case 0://sjever
                    while(pomSjever>0){
                        pomSjever=podesiRadius(jug,rad.getJug(),pomSjever,1,rad);
                        pomSjever=podesiRadius(zapad,rad.getZapad(),pomSjever,3,rad);
                        pomSjever=podesiRadius(istok,rad.getIstok(),pomSjever,2,rad);
                    }
                    break;
                case 1://jug
                    while(pomJug!=0){
                        pomJug=podesiRadius(sjever,rad.getSjever(),pomJug,0,rad);
                        pomJug=podesiRadius(zapad,rad.getZapad(),pomJug,3,rad);
                        pomJug=podesiRadius(istok,rad.getIstok(),pomJug,2,rad);
                    }
                    break;
                case 2://istok
                    while(pomIstok!=0){
                        pomIstok=podesiRadius(sjever,rad.getSjever(),pomIstok,0,rad);
                        pomIstok=podesiRadius(zapad,rad.getZapad(),pomIstok,3,rad);
                        pomIstok=podesiRadius(jug,rad.getJug(),pomIstok,1,rad);
                    }
                    break;
                default://zapad
                    while(pomZapad!=0){
                        pomZapad=podesiRadius(sjever,rad.getSjever(),pomZapad,0,rad);
                        pomZapad=podesiRadius(istok,rad.getIstok(),pomZapad,2,rad);
                        pomZapad=podesiRadius(jug,rad.getJug(),pomZapad,1,rad);
                    }
                    break;
            }
        }
        this.setRadius(rad);   
    }
    
    @Override
    public boolean provjeraPozicije(Tacka novaPoz){
        Polje p=mapa.get(novaPoz);
        int x=p.koordinate.getX();
        int y=p.koordinate.getY();
        Radius rad=this.getRadius();
        Tacka pozKuce=mapaKuca.get(this.getIdKuce()).getPozicija();
        if((y>pozKuce.getY()+rad.getJug())||(y<pozKuce.getY()-rad.getSjever())){
            return false;
        }
        if((x>pozKuce.getX()+rad.getIstok())||(x<pozKuce.getX()-rad.getZapad())){
            return false;
        }
        for(int i=x-1;i<=x+1;++i){
            for(int j=y-1;j<=y+1;++j){
                Polje pp=mapa.get(new Tacka(i,j));
                if(!pp.listaId.isEmpty()){
                    int pom=pp.listaId.get(0);
                    if(pom!=this.getIdKuce()){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static void napraviStarije(int brojStarih){
        Random rand=new Random();
        String pol="";
        int godiste=1920;
        if(rand.nextBoolean()){
            pol="muski";
        }
        else
        {
            pol="zenski";
        }
        for(int i=0;i<brojStarih;++i){
            StaraOsoba d=new StaraOsoba();
            d.setIme("stari"+i);
            d.setPrezime("prezime"+i);
            d.setPol(pol);
            d.setGodinaRodjenja(godiste+rand.nextInt(35));
            d.setIdOsobe(Parametri.ID_COUNTER++);
            listaOsoba.add(d);
        }
    }

}
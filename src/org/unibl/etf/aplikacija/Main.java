package org.unibl.etf.aplikacija;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static javafx.scene.text.TextAlignment.CENTER;



import javafx.stage.Stage;
import org.unibl.etf.stanovnici.Dijete;
import org.unibl.etf.stanovnici.OdraslaOsoba;
import org.unibl.etf.stanovnici.Osoba;
import org.unibl.etf.stanovnici.StaraOsoba;
import org.unibl.etf.tajmer.MjenjacTemperature;
import org.unibl.etf.watcher.FileWatcher;
import org.unibl.etf.watcher.Ispisivac;
import org.unibl.etf.zgrade.Ambulanta;
import org.unibl.etf.zgrade.Kuca;
import org.unibl.etf.zgrade.Punkt;

/**
 * @author dimitrije
 */
public class Main extends Application{
    
    public static ConcurrentHashMap<Tacka,Polje> mapa = new ConcurrentHashMap<Tacka,Polje>();
    public static ConcurrentHashMap<Integer,Kuca> mapaKuca=new ConcurrentHashMap<Integer,Kuca>();
    public static ConcurrentHashMap<Tacka,Kuca> pomMapaKuca=new ConcurrentHashMap<Tacka,Kuca>();
    public static ConcurrentHashMap<Tacka,Punkt> mapaPunktova=new ConcurrentHashMap<Tacka,Punkt>();
    public static LinkedBlockingDeque<Alarm> stackAlarma=new LinkedBlockingDeque<>();
    public static CopyOnWriteArrayList<Osoba> listaOsoba=new CopyOnWriteArrayList<>();
    public static ConcurrentHashMap<Tacka,Ambulanta> mapaAmbulanti=new ConcurrentHashMap<Tacka,Ambulanta>();
    
    @Override
    public void start(Stage stage) throws IOException{
        Scene scene;
        long pocetnoVrijeme=System.nanoTime();
        Button posaljiHitnu=new Button("Posalji hitnu pomoc");
        TextArea konzola=new TextArea();
        Label prikazStanja=new Label("Zarazeni:0          Zdravi:0");
        
        Ispisivac ispisivac=new Ispisivac(konzola);
        MojLogger.setup();
        MapWatcher watcher=new MapWatcher(posaljiHitnu);
        MjenjacTemperature mjenjac=new MjenjacTemperature();
        ScheduledExecutorService promjenaTemp = Executors.newSingleThreadScheduledExecutor();
        FileWatcher fileWatcher=new FileWatcher(prikazStanja);
        fileWatcher.start();
        
        
        BorderPane root=new BorderPane();
        VBox desnaStrana=new VBox(30);
        GridPane mreza=new GridPane();
           
        //Pocetni prozor za unos parametara
        Stage prva=new Stage();
        BorderPane prviRoot=new BorderPane();
        GridPane prviGrid=new GridPane();
        prviRoot.setCenter(prviGrid);
        VBox tekst=new VBox();
        VBox unos=new VBox();
        
        Label naslov=new Label("CORONA CITY 2020");
        naslov.setFont(new Font("Bold",30));
        naslov.setTextFill(Color.web("#0f578a"));
        prviRoot.setTop(naslov);
        
        BorderPane.setAlignment(naslov, Pos.CENTER);
        prva.setWidth(500);
        prva.setHeight(500);
        
        BorderPane.setAlignment(prviGrid, Pos.CENTER);
        prviGrid.add(tekst, 0, 0);
        prviGrid.add(unos,1,0);
        prviGrid.setHgap(50);
        tekst.setSpacing(26);
        unos.setSpacing(15);
        
        Button ponovoPokreni=new Button("Ponovo pokreni");
        prviGrid.add(ponovoPokreni,0,1);
        
        prviRoot.setBackground(Background.EMPTY);
        
        Label labelaStari=new Label("Broj starijih");
        Label labelaOdrasli=new Label("Broj odraslih");
        Label labelaDijeca=new Label("Broj dijece");
        Label labelaKuce=new Label("Broj kuca");
        Label labelaPunktovi=new Label("Broj puktova");
        Label labelaHitna=new Label("Broj vozila hitne");
        
        tekst.getChildren().addAll(labelaOdrasli,labelaStari,labelaDijeca,labelaKuce,labelaPunktovi,labelaHitna);
        
        TextField unosStari=new TextField();
        TextField unosOdrasli=new TextField();
        TextField unosDijeca=new TextField();
        TextField unosKuce=new TextField();
        TextField unosPunktovi=new TextField();
        TextField unosVozila=new TextField();
        
        unos.getChildren().addAll(unosOdrasli,unosStari,unosDijeca,unosKuce,unosPunktovi,unosVozila);
        
        Button iniciranje=new Button("Pokreni simulaciju");
        iniciranje.setAlignment(Pos.CENTER);
        prviGrid.add(iniciranje, 1, 1);
        prviGrid.setAlignment(Pos.CENTER);
        prviGrid.setVgap(50);
        
        ponovoPokreni.setOnAction(e->{
            SerijalizabilniGrad grad=null;
            try{    
                ObjectInputStream in=new ObjectInputStream(new FileInputStream(".."+File.separator+"CoronaCity"+File.separator+"serijalizacije"+File.separator+"serijalizacija"));  
                grad=(SerijalizabilniGrad) in.readObject();    
                in.close();
            }catch(IOException | ClassNotFoundException ex){
                MojLogger.log(Level.SEVERE, "Nesto se uzebalo u deserijalizaciji", ex);
            }
            
            mapa.putAll(grad.mapa);
            mapaKuca.putAll(grad.mapaKuca);
            pomMapaKuca.putAll(grad.pomMapaKuca);
            mapaPunktova.putAll(grad.mapaPunktova);
            stackAlarma.addAll(grad.stackAlarma);
            listaOsoba.addAll(grad.listaOsoba);
            mapaAmbulanti.putAll(grad.mapaAmbulanti);
                
            Parametri.BROJ_AMBULANTI=grad.BROJ_AMBULANTI;
            Parametri.BROJ_AMBULANTNIH_VOZILA=grad.BROJ_AMBULANTNIH_VOZILA;
            Parametri.BROJ_BOLESNE_DIJECE=grad.BROJ_BOLESNE_DIJECE;
            Parametri.BROJ_BOLESNIH=grad.BROJ_BOLESNIH;
            Parametri.BROJ_BOLESNIH_MUSKARACA=grad.BROJ_BOLESNIH_MUSKARACA;
            Parametri.BROJ_BOLESNIH_ODRASLIH=grad.BROJ_BOLESNIH_ODRASLIH;
            Parametri.BROJ_BOLESNIH_STARIH=grad.BROJ_BOLESNIH_STARIH;
            Parametri.BROJ_BOLESNIH_ZENA=grad.BROJ_BOLESNIH_ZENA;
            Parametri.BROJ_DIJECE=grad.BROJ_DIJECE;
            Parametri.BROJ_KONTROLNIH_PUNKTOVA=grad.BROJ_KONTROLNIH_PUNKTOVA;
            Parametri.BROJ_KUCA=grad.BROJ_KUCA;
            Parametri.BROJ_ODRASLIH=grad.BROJ_ODRASLIH;
            Parametri.BROJ_OPORAVLJENIH=grad.BROJ_OPORAVLJENIH;
            Parametri.BROJ_STARIH=grad.BROJ_STARIH;
            Parametri.ID_COUNTER=grad.ID_COUNTER;
            Parametri.MAX_BROJ_AMBULANTI=grad.MAX_BROJ_AMBULANTI;
            Parametri.VELICINA_MAPE=grad.VELICINA_MAPE;
                
            mapa.forEach((k,v)->{
                v.initializeRectangle();
            });
                
            for(int i=0;i<Parametri.VELICINA_MAPE;++i){
                for(int j=0;j<Parametri.VELICINA_MAPE;++j){
                    Tacka pozicija=new Tacka(i,j);

                    Polje polje=mapa.get(new Tacka(i,j));
                    mreza.add(polje.polje,i,j);
                }
            }
                
            mapaPunktova.forEach((t,p)->{
                Label l=new Label();
                l.setPrefSize(Parametri.VISINA_POLJA, Parametri.VISINA_POLJA);
                l.setFont(new Font("Bold",12));
                l.setText("P");
                l.setTextAlignment(CENTER);
                mreza.add(l, t.getX(), t.getY());
            });
                
            mapaAmbulanti.forEach((t,a)->{
                Label l=new Label();
                l.setPrefSize(Parametri.VISINA_POLJA, Parametri.VISINA_POLJA);
                l.setFont(new Font("Bold",12));
                l.setText("A");
                l.setTextAlignment(CENTER);
                mreza.add(l, t.getX(), t.getY());
            });
                
            mapaKuca.forEach((k,v)->{
                Label pom=new Label();
                pom.setPrefSize(Parametri.VISINA_POLJA, Parametri.VISINA_POLJA);
                pom.setFont(new Font("Bold",12));
                pom.setText("K");
                pom.setTextAlignment(CENTER);
                mreza.add(pom, v.getPozicija().getX(), v.getPozicija().getY());
            });
                
            mapaKuca.forEach((k,v)->{
                v.setBoja();
            });
                
            listaOsoba.forEach(s->{
                Kuca k=mapaKuca.get(s.getIdKuce());
                s.setBoja(k.getBoja());
            });

            listaOsoba.forEach(o->{
                o.setFlagKraj(false);
                o.start();
            });

            mapaPunktova.forEach((k,v)->{
                v.setFlagKraj(false);
                v.start();
            });

            mapaAmbulanti.forEach((k,v)->{
                v.setFlagKraj(false);
                new Thread(v).start();
            });
                
                
            ispisivac.start();
            promjenaTemp.scheduleAtFixedRate(mjenjac, 3, 30, TimeUnit.SECONDS);
            prva.close();
            stage.show();
            watcher.start();
        });
        
        iniciranje.setOnAction(e->{
            Parametri.VELICINA_MAPE=new Random().nextInt(16)+15;
            for(int i=0;i<Parametri.VELICINA_MAPE;++i){
                for(int j=0;j<Parametri.VELICINA_MAPE;++j){
                    Tacka pozicija=new Tacka(i,j);
                    Polje polje=new Polje(pozicija);
                    mapa.put(pozicija,polje);
                    mreza.add(polje.polje,i,j);
                }
            }
                        
            int brojDijece=0;
            int brojOdraslih=0;
            int brojStarih=0;
            int brojKuca=0;
            int brojAmbulantnihVozila=0;
            int brojPunktova=0;
            
            try{
                brojDijece=Integer.parseUnsignedInt(unosDijeca.getText(),10);
                brojOdraslih=Integer.parseUnsignedInt(unosOdrasli.getText(),10);
                brojKuca=Integer.parseUnsignedInt(unosKuce.getText(), 10);
                brojPunktova=Integer.parseUnsignedInt(unosPunktovi.getText(), 10);
                brojAmbulantnihVozila=Integer.parseUnsignedInt(unosVozila.getText(),10);
                brojStarih=Integer.parseUnsignedInt(unosStari.getText(), 10);
                
                if((brojOdraslih+brojStarih)>brojKuca){
                    if(brojKuca<=Parametri.MAX_BROJ_KUCA){
                        //Inicijalizacija parametara
                        Parametri.inicijalizacijaParametara(brojDijece,brojOdraslih,brojStarih,brojAmbulantnihVozila,brojKuca,brojPunktova);
                        
                        //Kreiranje kuca
                        Kuca.napraviKuce(brojKuca);
                        
                        //Kreiranje dijece,starijih i odraslih
                        Dijete.napraviDijecu(brojDijece);
                        StaraOsoba.napraviStarije(brojStarih);
                        OdraslaOsoba.napraviOdrasleOsobe(brojOdraslih);
                        
                        //Kreiranje punktova
                        Punkt.napraviPunktove(brojPunktova);
                        
                        //Dodavanje labela za punktove
                        mapaPunktova.forEach((t,p)->{
                            Label l=new Label();
                            l.setPrefSize(Parametri.VISINA_POLJA, Parametri.VISINA_POLJA);
                            l.setFont(new Font("Bold",12));
                            l.setText("P");
                            l.setTextAlignment(CENTER);
                            mreza.add(l, t.getX(), t.getY());
                        });
                        
                        //Kreiranje ambulanti
                        Ambulanta a1=new Ambulanta();
                        a1.pozicija.setX(0);
                        a1.pozicija.setY(0);
                        new Thread(a1).start();
                        mapaAmbulanti.put(new Tacka(0,0), a1);
                        
                        Ambulanta a2=new Ambulanta();
                        a2.pozicija.setX(0);
                        a2.pozicija.setY(Parametri.VELICINA_MAPE-1);
                        new Thread(a2).start();
                        mapaAmbulanti.put(new Tacka(0,Parametri.VELICINA_MAPE-1), a2);
                        
                        Ambulanta a3=new Ambulanta();
                        a3.pozicija.setX(Parametri.VELICINA_MAPE-1);
                        a3.pozicija.setY(0);
                        new Thread(a3).start();
                        mapaAmbulanti.put(new Tacka(Parametri.VELICINA_MAPE-1,0), a3);
                        
                        Ambulanta a4=new Ambulanta();
                        a4.pozicija.setX(Parametri.VELICINA_MAPE-1);
                        a4.pozicija.setY(Parametri.VELICINA_MAPE-1);
                        new Thread(a4).start();
                        mapaAmbulanti.put(new Tacka(Parametri.VELICINA_MAPE-1,Parametri.VELICINA_MAPE-1), a4);
                        
                        //Dodavanje labela za ambulante
                        mapaAmbulanti.forEach((t,a)->{
                            Label l=new Label();
                            l.setPrefSize(Parametri.VISINA_POLJA, Parametri.VISINA_POLJA);
                            l.setFont(new Font("Bold",12));
                            l.setText("A");
                            l.setTextAlignment(CENTER);
                            mreza.add(l, t.getX(), t.getY());
                        });
                        
                        //Dodavanje labela za kuce
                        mapaKuca.forEach((k,v)->{
                            Label pom=new Label();
                            pom.setPrefSize(Parametri.VISINA_POLJA, Parametri.VISINA_POLJA);
                            pom.setFont(new Font("Bold",12));
                            pom.setText("K");
                            pom.setTextAlignment(CENTER);
                            mreza.add(pom, v.getPozicija().getX(), v.getPozicija().getY());
                        });
                        
                        /*
                            Algoritam za rasporedjivanje osoba po kucama:
                            1.U svaku kucu se stavi po jedna osoba koja ima vise od 18 godina.Potreban broj osoba je obezbjedjen pri unosu.
                            2.Ostale osobe se jedna po jedna slucajnim odabirom indeksa iz opsega [0,BROJ_KUCA-1] rasporedjuju u kucu odredjenu
                              generisanim indeksom.
                        */
                        
                        //Pocetak rasporedjivanje osoba
                        mapaKuca.forEach((k,v)->{
                            Optional<Osoba> osobaOp=listaOsoba.stream()
                                                              .filter(s->(Calendar.getInstance().get(Calendar.YEAR)-s.getGodinaRodjenja()>18) && (s.getIdKuce()==-1))
                                                              .findFirst();
                            Osoba o=osobaOp.get();
                            if(o!=null){
                                o.setBoja(v.getBoja());
                                o.setIdKuce(k);
                                o.getPozicija().setX(v.getPozicija().getX());
                                o.getPozicija().setY(v.getPozicija().getY());

                                v.dodajUkucana(o);
                            }
                        });
                        
                        Random rand=new Random();
                        listaOsoba.forEach(s->{
                            if(s.getIdKuce()==-1){
                                int indeks=rand.nextInt(Parametri.BROJ_KUCA);
                                Kuca k=mapaKuca.get(indeks);
                                s.setIdKuce(indeks);
                                s.setBoja(k.getBoja());
                                s.getPozicija().setX(k.getPozicija().getX());
                                s.getPozicija().setY(k.getPozicija().getY());
                                k.dodajUkucana(s);
                                mapaKuca.replace(indeks, k);
                            }
                        });
                        //Kraj rasporedjivanja osoba
                        
                        ispisivac.start();
                        promjenaTemp.scheduleAtFixedRate(mjenjac, 3, 30, TimeUnit.SECONDS);
                        prva.close();
                        stage.show();
                        watcher.start();
                    } 
                }           
            }catch(NumberFormatException ex){
                MojLogger.log(Level.FINEST, "Nije unesen broj.", ex);
            }
        });
        
        
        Scene prvaScena=new Scene(prviRoot);
        prva.setScene(prvaScena);
        prva.show();
        //Kraj koda za pocetni prozor
        
        konzola.setWrapText(true);
        konzola.setPrefWidth(300);
        desnaStrana.setAlignment(Pos.CENTER);
        desnaStrana.setPrefWidth(200);
        
        prikazStanja.setPrefHeight(25);
        prikazStanja.setTextAlignment(CENTER);
        prikazStanja.setWrapText(true);
        root.setTop(prikazStanja);
        BorderPane.setAlignment(prikazStanja, Pos.CENTER);
        
        Button omoguciKretanje=new Button("Omoguci kretanje");
        Button pregledAmbulanti=new Button("Pregled ambulanti");
        Button pregledStatistike=new Button("Pregled statistike");
        Button kreirajAmbulantu=new Button("Kreiraj ambulantu");
        Button zaustaviSim=new Button("Zaustavi simulaciju");
        Button zavrsiSim=new Button("Zavrsi simulaciju");
        
        zavrsiSim.setWrapText(true);
        zavrsiSim.setMaxWidth(100);
        zavrsiSim.setTextAlignment(CENTER);
        
        zaustaviSim.setWrapText(true);
        zaustaviSim.setMaxWidth(100);
        zaustaviSim.setTextAlignment(CENTER);
        
        kreirajAmbulantu.setWrapText(true);
        kreirajAmbulantu.setMaxWidth(100);
        kreirajAmbulantu.setTextAlignment(CENTER);
        
        pregledStatistike.setWrapText(true);
        pregledStatistike.setMaxWidth(100);
        pregledStatistike.setTextAlignment(CENTER);
        
        pregledAmbulanti.setWrapText(true);
        pregledAmbulanti.setMaxWidth(100);
        pregledAmbulanti.setTextAlignment(CENTER);
        
        posaljiHitnu.setWrapText(true);
        posaljiHitnu.setMaxWidth(100);
        posaljiHitnu.setTextAlignment(CENTER);
                
        omoguciKretanje.setWrapText(true);
        omoguciKretanje.setMaxWidth(100);
        omoguciKretanje.setTextAlignment(CENTER);
        
        omoguciKretanje.setOnAction(e->{
            listaOsoba.forEach(s->{
                if(!s.isAlive()){
                    s.start();
                }
            });
        });
        
        pregledAmbulanti.setOnAction(e->{
            Stage scenaAmbulanti=new Stage();
            scenaAmbulanti.setTitle("Prikaz stanja ambulanti");
            TextArea noviRoot=new TextArea();
            scenaAmbulanti.setScene(new Scene(noviRoot,500,200));
            for(int i=0;i<Parametri.VELICINA_MAPE;++i){
                for(int j=0;j<Parametri.VELICINA_MAPE;++j){
                    if(mapaAmbulanti.containsKey(new Tacka(i,j))){
                        noviRoot.appendText(mapaAmbulanti.get(new Tacka(i,j)).toString());
                    }
                }
            }
            scenaAmbulanti.show();
        });
        
        posaljiHitnu.setOnAction(e->{
            mapaAmbulanti.forEach((k,a)->{
                if(!stackAlarma.isEmpty()){
                    int zaDodati=stackAlarma.size();
                    int brAuta=Parametri.BROJ_AMBULANTNIH_VOZILA;
                    int raspolozivo=a.getKapacitet()-a.getStanjeZarazenih();
                    if(raspolozivo>0){
                        if(raspolozivo>zaDodati){
                            while(zaDodati>0 && brAuta>0 && stackAlarma.size()!=0){
                                Alarm alarm=stackAlarma.pop();
                                Kuca kuca=mapaKuca.get(alarm.getIdKuceZr());
                                a.dodajPacijenta(kuca.listaOsoba
                                        .stream()
                                        .filter(s->s.isFlagBolestan() && s.getPozicija().equals(alarm.getPozicijaZr()))
                                        .findFirst()
                                        .get());
                                zaDodati--;
                                brAuta--;
                            }
                            if(stackAlarma.isEmpty()){
                                posaljiHitnu.setStyle("-fx-text-fill: 	#000000");
                            }
                        }
                        else{
                            while(raspolozivo>0 && brAuta>0 && stackAlarma.size()!=0){
                                Alarm alarm=stackAlarma.pop();
                                Kuca kuca=mapaKuca.get(alarm.getIdKuceZr());
                                Optional<Osoba> osoba=kuca.listaOsoba
                                        .stream()
                                        .filter(s->s.isFlagBolestan() && s.getPozicija().equals(alarm.getPozicijaZr()))
                                        .findFirst();
                                a.dodajPacijenta(osoba.get());
                                raspolozivo--;
                                brAuta--;
                            }
                            if(stackAlarma.isEmpty()){
                                posaljiHitnu.setStyle("-fx-text-fill: 	#000000");
                            }
                        }
                    }
                }
                else{
                    posaljiHitnu.setStyle("-fx-text-fill: 	#000000");
                }
            });
        });
        
        kreirajAmbulantu.setOnAction(e->{
            Ambulanta novaAmbulanta=new Ambulanta();
            boolean napravljena=false;
            for(int i=0;i<Parametri.VELICINA_MAPE;++i){
                for(int j=0;j<Parametri.VELICINA_MAPE;++j){
                    if(((i==0 && j!=0)||(i!=0&&j==0)||(i==29 && j!=29)||(j==29 && i!=29))&&(!napravljena)){
                        Tacka t=new Tacka(i,j);
                        if(!mapaAmbulanti.containsKey(t)){
                            novaAmbulanta.pozicija.setX(i);
                            novaAmbulanta.pozicija.setY(j);
                            mapaAmbulanti.put(t, novaAmbulanta);
                            new Thread(novaAmbulanta).start();
                            napravljena=true;
                        }
                    }     
                }
            }
        });
        
        pregledStatistike.setOnAction(e->{
            Stage novaScena=new Stage();
            novaScena.setTitle("Statistika");
            novaScena.setWidth(1200);
            novaScena.setHeight(500);
            Scene sc=new Scene(new Group());
            Button download=new Button("Preuzmi CSV");
            
            download.setWrapText(true);
            download.setTextAlignment(CENTER);
            download.setTranslateX(550);
            download.setTranslateY(400);
            
            download.setOnAction(f->{
                try {
                    BufferedWriter pisac=new BufferedWriter(new FileWriter("."+File.separator+"statistickiPodaci.csv"));
                    pisac.write(Parametri.BROJ_BOLESNIH_MUSKARACA+","+Parametri.BROJ_BOLESNIH_ZENA+"\n");
                    pisac.write(Parametri.BROJ_BOLESNIH_ODRASLIH+","+Parametri.BROJ_BOLESNE_DIJECE+","+Parametri.BROJ_BOLESNIH_STARIH+"\n");
                    pisac.write(Parametri.BROJ_BOLESNIH_MUSKARACA+","+Parametri.BROJ_BOLESNIH_ZENA);
                    pisac.close();
                } catch (IOException ex) {
                    MojLogger.log(Level.SEVERE, "Greska u pravljenju fajla", ex);
                }
            });
            
            ObservableList<PieChart.Data> listaZarazeni=FXCollections.observableArrayList(
                new PieChart.Data("Zdravi",listaOsoba.size()-Parametri.BROJ_BOLESNIH),
                new PieChart.Data("Bolesni", Parametri.BROJ_BOLESNIH));
            
            ObservableList<PieChart.Data> listaPoPolu=FXCollections.observableArrayList(
                new PieChart.Data("Muskarci", Parametri.BROJ_BOLESNIH_MUSKARACA),
                new PieChart.Data("Zene",Parametri.BROJ_BOLESNIH_ZENA));
            
            ObservableList<PieChart.Data> listaPoStarosti=FXCollections.observableArrayList(
                new PieChart.Data("Stariji", Parametri.BROJ_BOLESNIH_STARIH),
                new PieChart.Data("Odrasli", Parametri.BROJ_BOLESNIH_ODRASLIH),
                new PieChart.Data("Dijeca", Parametri.BROJ_BOLESNE_DIJECE));
            
            PieChart stZarazeni=new PieChart(listaZarazeni);
            PieChart stPoPolu=new PieChart(listaPoPolu);
            PieChart stPoStarosti=new PieChart(listaPoStarosti);
            
            stZarazeni.setTitle("Broj zarazeni i oboljelih");
            stZarazeni.setTranslateX(0);
            stZarazeni.setPrefSize(350, 350);
            
            stPoPolu.setTitle("Broj zarazenih po polu");
            stPoPolu.setTranslateX(400);
            stPoPolu.setPrefSize(350, 350);
            
            stPoStarosti.setTitle("Statistika po starosnoj dobi");
            stPoStarosti.setTranslateX(800);
            stPoStarosti.setPrefSize(350, 350);
            
            ((Group) sc.getRoot()).getChildren().addAll(stZarazeni,stPoPolu,stPoStarosti,download);
            novaScena.setScene(sc);
            novaScena.show();
            
        });
        
        zavrsiSim.setOnAction(e->{
            long krajRada=System.nanoTime();
            long ukupnoTrajanje=krajRada-pocetnoVrijeme;
            
            Date dNow = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat ("'Sim-JavaKov-20-'hh:mm:ss'-'dd.MM.yyyy.");
            
            try {
                PrintWriter upisivanje=new PrintWriter(new FileWriter(new File(".."+File.separator+"CoronaCity"+File.separator+"informacije o simulacijama"+File.separator+ft.format(dNow))));
                upisivanje.println("Vrijeme trajanja: "+(double)ukupnoTrajanje/1_000_000_000.0);
                upisivanje.println("Broj stanovnika: "+listaOsoba.size());
                upisivanje.println("Broj Odraslih: "+Parametri.BROJ_ODRASLIH);
                upisivanje.println("Broj Starih: "+Parametri.BROJ_STARIH);
                upisivanje.println("Broj Dijece: "+Parametri.BROJ_DIJECE);
                upisivanje.println("Broj Ambulanti: "+mapaAmbulanti.size());
                upisivanje.println("Broj Punktova: "+mapaPunktova.size());
                upisivanje.println("Broj Kuca: "+mapaKuca.size());
                upisivanje.println("=========================\n       STATISTIKA\n=========================");
                upisivanje.println("Broj zdravih: "+(listaOsoba.size()-Parametri.BROJ_BOLESNIH));
                upisivanje.println("Broj bolesnih: "+Parametri.BROJ_BOLESNIH);
                
                upisivanje.println("Broj bolesnih muskaraca: "+Parametri.BROJ_BOLESNIH_MUSKARACA);
                upisivanje.println("Broj bolesnih zena: "+Parametri.BROJ_BOLESNIH_ZENA);
                upisivanje.println("Broj bolesnih Odraslih: "+Parametri.BROJ_BOLESNIH_ODRASLIH);
                upisivanje.println("Broj bolesnih Starih: "+Parametri.BROJ_BOLESNIH_STARIH);
                upisivanje.println("Broj bolesne Dijece: "+Parametri.BROJ_BOLESNE_DIJECE);
                upisivanje.close();
            } catch (IOException ex) {
                MojLogger.log(Level.WARNING, "Greska pri evidentiranju trajanja simulacije", ex);
            }
            
            watcher.setKrajFlag(true);
            fileWatcher.setKraj(true);
            mapaAmbulanti.forEach((k,v)->v.setFlagKraj(true));
            mapaPunktova.forEach((k,v)->v.setFlagKraj(true));
            listaOsoba.forEach(s->s.setFlagKraj(true));
            System.exit(0);
            
        });
        
        zaustaviSim.setOnAction(e->{
            watcher.setKrajFlag(true);
            fileWatcher.setKraj(true);
            mapaAmbulanti.forEach((k,v)->v.setFlagKraj(true));
            mapaPunktova.forEach((k,v)->v.setFlagKraj(true));
            listaOsoba.forEach(s->s.setFlagKraj(true));//problem je kad se pokrecu sta ce se desiti
            
            SerijalizabilniGrad grad=new SerijalizabilniGrad();
            
            try{
                FileOutputStream fout=new FileOutputStream(".."+File.separator+"CoronaCity"+File.separator+"serijalizacije"+File.separator+"serijalizacija");  
                ObjectOutputStream out=new ObjectOutputStream(fout);  
                out.writeObject(grad);  
                out.flush();    
                out.close();
            }
            catch(IOException ex){
                MojLogger.log(Level.SEVERE,"Greska pri serijalizaciji", ex);
            }
            
            System.exit(0);
        });
        
        desnaStrana.getChildren().addAll(omoguciKretanje,posaljiHitnu,pregledAmbulanti,pregledStatistike,
                kreirajAmbulantu,zaustaviSim,zavrsiSim);
        root.setRight(desnaStrana);
        root.setCenter(mreza);
        root.setLeft(konzola);
        
        scene=new Scene(root);
        stage.setTitle("CoronaCity-2020");
        stage.setScene(scene);
    }
    
    public static void main(String args[]){
        launch(args);
    }
}
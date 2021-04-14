package org.unibl.etf.watcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.Label;
import static org.unibl.etf.aplikacija.Main.listaOsoba;
import org.unibl.etf.aplikacija.MojLogger;

/**
 * @author dimitrije
 */
public class FileWatcher extends Thread{
    private WatchService watchService;
    private Path dir;
    private WatchKey key;
    private boolean kraj=false;
    private Label labela;
    
    public FileWatcher(Label labela) throws IOException{
        watchService=FileSystems.getDefault().newWatchService();
        dir=Paths.get(".."+File.separator+"CoronaCity");
        key=dir.register(watchService, ENTRY_MODIFY);
        this.labela=labela;
    }

    public void setKraj(boolean kraj) {
        this.kraj = kraj;
    }
    
    @Override
    public void run(){
        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                labela.setText("Zarazeni:0"+"          Zdravi:"+listaOsoba.size());
            }
        });
        while(!kraj){
            for(WatchEvent<?> event:key.pollEvents()){
                try {
                    BufferedReader citac=new BufferedReader(new FileReader(".."+File.separator+"CoronaCity"+File.separator+"zarazeni"));
                    int zarazeni=Integer.parseInt(citac.readLine());
                    int zdravi=Integer.parseInt(citac.readLine());
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run(){
                            labela.setText("Zarazeni:"+zarazeni+"          Zdravi:"+zdravi);
                        }
                    });
                    citac.close();
                } catch (FileNotFoundException ex) {
                    MojLogger.log(Level.SEVERE, "Nepostojeci fajl sa podacima o zarazenim", ex);
                } catch (IOException ex) {
                    MojLogger.log(Level.SEVERE,"Gresla u citanju fajla sa zarazenim",ex);
                }
            }
            try{
                sleep(500);
            }
            catch(Exception e){
                MojLogger.log(Level.SEVERE, "Greska u klasi File Watcher", e);
            }
        }
    }
}
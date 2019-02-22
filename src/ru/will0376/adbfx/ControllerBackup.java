package ru.will0376.adbfx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ControllerBackup implements Initializable {
    private ResourceBundle resources;
    @FXML
    CheckBox alertbool;
    @FXML
    CheckBox apk;
    @FXML
    CheckBox obb;
    @FXML
    CheckBox shared;
    @FXML
    CheckBox nosystem;
    @FXML
    CheckBox all;
    @FXML
    ListView<String> listapp;
    @FXML
    ListView<String> listbackup;
    @FXML
    TextField inputname;

    String ver = "1.0";
    File backupFolder = new File(Vars.c.getPath()+"\\backups");
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Vars.c.printText("Backup module started! Version: "+ver);
        this.resources = resources;
        checkFolder();
        fillapplist();
        fillbackuplist();
        listapp.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if(!Main.debug)
            alertbool.setOpacity(0);

    }

    private void fillbackuplist() {
        listbackup.setItems(FXCollections.observableArrayList(""));
        checkFolder();
        List<String> paths = new ArrayList<>();
        File[] files = backupFolder.listFiles();
        System.out.println(backupFolder);
        try {
            for (File file : files) {
                paths.add(file.getAbsolutePath());
                System.out.println(file.getAbsolutePath());
            }
        }
        catch (NullPointerException e){
            System.out.println("Error loading backup folder!");
            Vars.c.printText("Error loading backup folder!");
            e.printStackTrace();
        }
        listbackup.setItems(FXCollections.observableArrayList(paths));
    }

    private void fillapplist() {
        //adb shell "pm list packages"|cut -f 2 -d ":"
        listapp.setItems(FXCollections.observableArrayList(""));
        Vars.c.startProgram(false,"shell","\"pm list packages\"|cut -f 2 -d \":\"");
        while(Vars.threadstartprogram.isAlive()){ } //wait :D
        List<String> pkg = new ArrayList<>(); //= Arrays.asList(Vars.c.oldlog.split("\\s"));
        for(String pkgs : Vars.c.oldlog.split("\\s"))
        {
            if(!pkgs.equals("") && !pkgs.equals(" "))
                pkg.add(pkgs);
        }
        listapp.setItems(FXCollections.observableArrayList(pkg));
    }
    public void reloadapp(){
        fillapplist();
    }
    public void reloadbackup(){
        fillbackuplist();
    }
    public void remove(){
        if(alert("Remove",resources.getString("warring.remove"))){
        System.out.println(listbackup.getSelectionModel().getSelectedItem());
        File tmp = new File(listbackup.getSelectionModel().getSelectedItem());
            try {
                Files.delete(tmp.toPath());
                Vars.c.printRes(false,"warring.success");
            } catch (IOException e) {
                Vars.c.printRes(false,"warring.error");
                e.printStackTrace();
            }
        fillbackuplist();
      }
    }

    public void restore(){
        if(alert("Restore",resources.getString("warring.restore"))){
            Vars.c.startProgram("restore",listbackup.getSelectionModel().getSelectedItem());
        }
    }

    public void save(){
        Vars.c.printRes(false,"warring.wait0.5");
        Vars.c.printRes(false,"warring.wait");
        String filenamefromtime = new SimpleDateFormat("dd.MM.yyyy_hh_mm").format(new Date());
        if(!inputname.getText().equals("")){
            filenamefromtime = inputname.getText();
        }
        checkFolder();

        List start = new ArrayList();
        start.add("backup");  start.add("-f");  start.add(backupFolder+"\\"+filenamefromtime+".ab");
        for(String parm : getCB())
            start.add(parm);
        for(String app : listapp.getSelectionModel().getSelectedItems())
            start.add(app);

        /**"backup","-f","\""+backupFolder+"\\"+filenamefromtime+".ab"+"\""*/
        String tam[] = (String[]) start.stream().toArray(String[]::new);
        thread(tam);
    }

    private void thread(String[] tam){
        Vars.c.printRes(false,"warring.threadbackup");
        Platform.runLater(() -> {
            Vars.c.startProgram(tam);
            while (Vars.threadstartprogram.isAlive()) { }
            reloadbackup();
            Vars.c.printRes(false,"backup.Done");

        });
    }
    private ArrayList<String> getCB(){
        ArrayList<String> parm = new ArrayList<>();
        if(apk.isSelected())
            parm.add("-apk");
        if(obb.isSelected())
            parm.add("-obb");
        if(shared.isSelected())
            parm.add("-shared");
        if(all.isSelected())
            parm.add("-all");
        if(nosystem.isSelected())
            parm.add("-nosystem");
        return parm;
    }

    private void checkFolder(){
        if(backupFolder.exists()){ }
        else backupFolder.mkdir();
    }

    private boolean alert(String... text){
        if(!alertbool.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(text[0]);
            alert.setHeaderText(text[1]);
            alert.setContentText(resources.getString("warring.okcancel"));
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.CANCEL) {
                return false;
            }
            else return option.get() == ButtonType.OK;
        }
        else{
            return true;
        }
    }
    public void openFolder() {
        Vars.c.openFolder(backupFolder);
    }
}

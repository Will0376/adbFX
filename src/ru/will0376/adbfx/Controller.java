package ru.will0376.adbfx;

import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Controller implements Initializable {
    @FXML
    public TextArea TextField;
    @FXML
    Hyperlink HL1;
    @FXML
    Menu device;
    @FXML
    Text deviceused;

    File pathtoadb = null;
    private ResourceBundle resources;
    private Scene stage;
    private String[] phrases = new String[]{"devices [-l]","Failed to install","cut:"};
    private String DeviceSerial = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getPathtoadb();
        new DownloaderGH().init(TextField);
        this.resources = resources;
        TextField.setWrapText(true);
        TextField.setEditable(false);
        TextField.clear();
        printText("AdbFX Version: "+Main.ver);
        printRes(false,"key.main.note.FirstStart");
        printRes(false,"key.main.note.EnableDebugging");
        printRes(false,"key.all.Tire");
        startUP();
    }

    /**
     * todo:
     *       Сделать бкапер кеша и сейвов
     *       FastBoot Manager(shell + flasher :D)
     */

    public void сlearFl(ActionEvent event) {
        TextField.clear();
    }

    public void refleshDevices(ActionEvent event) {
        startUP();
    }

    private void startUP() {
        startProgram("devices");
        while (Vars.threadstartprogram.isAlive()){}
        addDevicesToMenu(oldlog.replaceAll("List of devices attached","").replaceAll("(unauthorized|device|sideload|offline)","").trim());
    }

    private void addDevicesToMenu(String allDevices) {
        device.getItems().remove(1,device.getItems().size());
            String tmp = allDevices.split(" ")[0];
            DeviceSerial = tmp;
            printText(printRes(true, "main.usedDevice")+tmp);
            deviceused.setText(tmp);

        for(int i = 0;i < allDevices.split(" ").length;i++) {
            String tmp2 = allDevices.split(" ")[i].trim();
            MenuItem mi = new MenuItem(tmp2);
            if (!tmp2.equals("")){
                EventHandler eh = event -> {
                    DeviceSerial = tmp2;
                printText(printRes(true, "main.usedDevice")+tmp2);
                    deviceused.setText(tmp2);
                };
                mi.setOnAction(eh);
                device.getItems().add(mi);
            }
        }
    }

    public void hl1open() {
        openBrw("https://www.xda-developers.com/install-adb-windows-macos-linux/");
    }

    void openBrw(String url) {
        try {
            URI uri = new URI(url);
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            }
        }
        catch (URISyntaxException | IOException e1) {
            e1.printStackTrace();
        }
    }

    public void pullUname() {
        startProgram("shell","uname -a");
    }

    public void openFiles(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Apk", "*.apk"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All", "*.*"));
        List<File> files = fileChooser.showOpenMultipleDialog((Window) Main.ps);
        if (files != null) {
            installToPhone(files);
        }

    }

    private void installToPhone(List<File> files) {
        Thread install = new Thread(() -> {
            for (File file : files) {
                System.out.println("Install: " + file.getName());
                printText("Install: " + file.getName());
                startProgram("install", file.getAbsolutePath());
                while (Vars.threadstartprogram.isAlive()) {
                }
            }
        });
        install.setName("Install To Phone TH");
        install.start();
    }

    public void openAbout(ActionEvent event) {
        openFXML("About","null","400","270");
    }

    public void adbShell(ActionEvent event) {
        openFXML("Shell","Shell","668","122");
    }

    public void openWifiModule(ActionEvent event) {
        openFXML("Wifi_Main","Wifi Main","464","170");
    }

    public void openFAQ(ActionEvent event) {
        openFXML("FAQ","FAQ","618","335");
    }

    public void openBackup(){
        openFXML("Backup","Backup","588","595");
    }

    public void openAppMan(){
        openFXML("AppManager","null","600","385");
    }

    private void openFXML(String... text) {
        try {
            Vars.c = this;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/"+text[0]+".fxml"));
            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
            Parent root1 = fxmlLoader.load();
            Stage stage1 = new Stage();
            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
            if(text.length != 1)
                if(!text[1].equals("null"))
                    stage1.setTitle(text[1]);
                else
                    stage1.setTitle(text[0]);
            stage1.setScene(new Scene(root1, Integer.parseInt(text[2]), Integer.parseInt(text[3])));
            stage1.initOwner((Window)Main.ps);
            stage1.setResizable(false);

            stage1.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doExit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public void changeLangAuto() throws IOException {
        stage = Main.getScene();
        stage.setRoot(FXMLLoader.load(getClass().getResource("Fxml/Main.fxml"),ResourceBundle.getBundle("ru/will0376/adbfx/Locales/Locale", new Locale(Locale.getDefault().getLanguage()))));
    }

    public void changeLangEn() throws IOException {
        stage = Main.getScene();
        Main.locale = new Locale("en");
        stage.setRoot(FXMLLoader.load(getClass().getResource("Fxml/Main.fxml"),ResourceBundle.getBundle("ru/will0376/adbfx/Locales/Locale", new Locale("en"))));
    }

    public void changeLangRu() throws IOException {
        stage = Main.getScene();
        Main.locale = new Locale("ru");
        stage.setRoot(FXMLLoader.load(getClass().getResource("Fxml/Main.fxml"),ResourceBundle.getBundle("ru/will0376/adbfx/Locales/Locale", new Locale("ru"))));
    }

    String printRes(boolean ret, String... key) {

        try {
            if (key.length == 2) {
                if(ret) return(resources.getString(key[0]) + key[1]);
                else
                    printText(resources.getString(key[0]) + key[1]);
            }
            else {
                if(ret) return(resources.getString(key[0]));
                else
                    printText(resources.getString(key[0]));
            }
        }
        catch(MissingResourceException e) {
            e.printStackTrace();
            printText("Error! Key: "+ key[0] +" not found! Locale:" + resources.getLocale() );
        }
        return null;
    }

    void printText(String text) {
        TextField.appendText(text + System.getProperty("line.separator"));
    }

    String oldlog = "";
    void startProgram(boolean printlog, String... arg){
        oldlog = "";
        Thread start = new Thread(() -> {
            try {
                List<String> list = new ArrayList<String>();
                list.add(pathtoadb.getAbsolutePath());
                if(DeviceSerial != null) {
                    list.add("-s");
                    list.add(DeviceSerial);
                }
                for (int i = 0;i<arg.length;i++)
                    list.add(arg[i]);
                for(int i = 0;i < list.size();i++) {
                    System.out.print(list.get(i)+" ");
                }
                System.out.print(System.lineSeparator());
                ProcessBuilder procBuilder = new ProcessBuilder(list);
                procBuilder.redirectErrorStream(true);
                Process process = procBuilder.start();

                InputStream processStdOutput = process.getInputStream();
                Reader r = new InputStreamReader(processStdOutput);
                BufferedReader br = new BufferedReader(r);
                String line;
                while ((line = br.readLine()) != null) {

                    oldlog += line+" ";

                        System.out.println(line);
                        if (textTester(line)) {
                            if(printlog) {
                               getTextFromPhrase(line);
                             }
                            } else {
                            if(printlog) {
                                printText(line);
                            }
                        }
                }
                process.waitFor();
            }
            catch (IOException | InterruptedException e){
                StringWriter errors = new StringWriter();
                e.printStackTrace();
                printText(errors.toString());
            }
        });
        start.setName("StartProgramTH");
        start.start();
        Vars.threadstartprogram = start;
    }

    void startProgram(String... arg){
        startProgram(true,arg);
    }

    private void getTextFromPhrase(String line) {
        if (line.contains(phrases[0])) {
            printRes(false, "main.error.adb.command");
        } else if (line.contains(phrases[1])) {
            printText("Fail! Error:" + line.split("Failure")[1]);
        } else if (line.contains(phrases[2])) {
            printRes(false, "main.error.busybox.not.found");
        }
        else if(line.contains("\\[\\s\\d{2}\\%\\]")){

        }
    }

    private boolean textTester(String line) {
        for (String phrase : phrases) {
            if (line.contains(phrase)) {
                return true;
            } else if (line.matches("\\[\\s\\d\\d\\%\\].+")) {
                return true;
            }
        }
        return false;
    }

    boolean isWindows(){
        String os = System.getProperty("os.name").toLowerCase();
        return (os.contains("win"));
    }

    private void getPathtoadb() {
        if(isWindows())
            pathtoadb = new File(System.getProperty("user.home") + "/.adblibs/" + "adb.exe");
        else
            pathtoadb = new File(System.getProperty("user.home") + "/.adblibs/" + "adb");
    }

    String getPath(){
        if(isWindows()){
            return pathtoadb.getAbsolutePath().substring(0,pathtoadb.toString().length() - 7);
        }
        else
            return pathtoadb.getAbsolutePath().substring(0,pathtoadb.toString().length() - 3);

    }

    void openFolder(File file){

        Desktop desktop = null;

        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(file);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    String getOldlog(){
        return oldlog;
    }

    public void deviceReboot(){
        startProgram("shell","reboot");
    }

    public void deviceRebootRecovery(){
        startProgram("shell","reboot recovery");
    }

    public void deviceRebootBootloader(){
        startProgram("shell","reboot fastboot");
    }

    public void deviceSoftReboot(){
        startProgram("shell","su","-c","pkill","zygote");
    }

}
	  
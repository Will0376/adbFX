package ru.will0376.adbfx;

import java.awt.Desktop;
import java.io.*;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Controller implements Initializable {
	   @FXML
	   public TextArea TextField;
	   @FXML
	   Hyperlink HL1;
	   ResourceBundle resources;
	   private Scene stage;
		@Override
		public void initialize(URL location, ResourceBundle resources) {
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
	 * перейти на использование adb.exe заместо библиотек - Done
	 * Переписать Wi-Fi адб и шелл под adb.exe;
	 *
	 * wi-fi - done
	 * shell - 1/2 done.(проблемма с ping типом)
	 */

	   public void сlearFl(ActionEvent event) {
		   TextField.clear();
	   }
	   
	   public void refleshDevices(ActionEvent event) {
		   startUP();
	   }

	   protected void startUP() {
		   startProgram("devices");
		}
	   public void hl1open() {
			openBrw("https://www.xda-developers.com/install-adb-windows-macos-linux/");
		}
	   public void openBrw(String url) {
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
	   public void installToPhone(List<File> files) {
			   Thread install = new Thread(() -> {
				for(int i = 0; i < files.size();i++)
				startProgram("install",files.get(i).getAbsolutePath());
			});
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
	   public void openPL(ActionEvent event) {
		   openFXML("ProgramList","ProgramList(TEST)","812","585");
	   }
	   public void openFAQ(ActionEvent event) {
		   openFXML("FAQ","FAQ","618","335");
	   }
	   public void openFXML(String... text) {
		   try {
		   	Main.c = this;
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
		public String printRes(boolean ret,String... key) {

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
		public void printText(String text) {
			TextField.appendText(text + System.getProperty("line.separator"));
		 }
		 /**
		  * @return 0 - O; 1 - error arg!2 - error adb not found!
		  * */

		 public int startProgram(String... arg){
			 System.out.println();
		 	String testtext1 = "devices [-l]";
		 	String testtext2 = "help";
		 	String testtext3 = "version";

			 Thread start = new Thread(() -> {
			 	try {
				 File pathtoadb = new File(System.getProperty("user.home") + "\\.adblibs\\" + "adb.exe");
				 List<String> list = new ArrayList<String>();
				 list.add(pathtoadb.getAbsolutePath());
				 for (int i = 0;i<arg.length;i++)
				 	list.add(arg[i]);
				 for(int i = 0;i < list.size();i++) {
					 System.out.print(list.get(i)+" ");
				 }
				 System.out.print(System.lineSeparator());
				 ProcessBuilder procBuilder = new ProcessBuilder(list);
				 procBuilder.redirectErrorStream(true);
				 Process process = procBuilder.start();
				 InputStream stdout = process.getInputStream();
				 InputStreamReader isrStdout = new InputStreamReader(stdout);
				 BufferedReader brStdout = new BufferedReader(isrStdout);
				 String line = brStdout.lines().collect(Collectors.joining("\n"));
				 Main.pr = process;
				 if(line.contains(testtext1) && line.contains(testtext2) && line.contains(testtext3)){
				 printRes(false,"main.error.adb.command");
				 }
				 else{
				 	printText(line);
					 System.out.println(line);
				 }

				 process.waitFor();
			 }
			 catch (IOException | InterruptedException e){
				 e.printStackTrace();
			 }
			 });
			 start.start();
	   		return 0;
		 }
}
	  
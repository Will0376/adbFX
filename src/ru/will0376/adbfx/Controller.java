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
import ru.will0376.adbfx.Locales.Vars;

public class Controller implements Initializable {
	   @FXML
	   public TextArea TextField;
	   @FXML
	   Hyperlink HL1;
	   ResourceBundle resources;
	   private Scene stage;
	   private String[] phrases = new String[]{"devices [-l]","Failed to install"};
	   public File pathtoadb = null;
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
	 * todo: for todo :)
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
			  for (int i = 0;i < files.size();i++) {
					System.out.println("Install: " + files.get(i).getName());
					printText("Install: " + files.get(i).getName());
					startProgram("install", files.get(i).getAbsolutePath());
					while(Vars.threadstartprogram.isAlive()){}
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
	   public void openPL(ActionEvent event) {
		   openFXML("ProgramList","ProgramList(TEST)","812","585");
	   }
	   public void openFAQ(ActionEvent event) {
		   openFXML("FAQ","FAQ","618","335");
	   }
	   public void openBackup(){
	   	openFXML("Backup","Backup","588","595");
	   }
	   public void openFXML(String... text) {
		   try {
		  // 	Main.c = this;
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
	public String oldlog = "";
		 public int startProgram(boolean printlog,String... arg){
		 	oldlog = "";
			 Thread start = new Thread(() -> {
			 	try {
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

					InputStream processStdOutput = process.getInputStream();
					Reader r = new InputStreamReader(processStdOutput);
					BufferedReader br = new BufferedReader(r);
					String line;
					while ((line = br.readLine()) != null) {

						oldlog += line+" ";
						if(printlog) {
							System.out.println(line);
							if (textTester(line)) {
								printText(getTextFromPhrase(line));
							} else {
								printText(line);
							}
						}
					}
				 process.waitFor();
			 }
			 catch (IOException | InterruptedException e){
				 e.printStackTrace();
			 }
			 });
			 start.setName("StartProgramTH");
			 start.start();
	   		Vars.threadstartprogram = start;
			 return 0;
		 }
	public void startProgram(String... arg){
	   	startProgram(true,arg);
	}

	private String getTextFromPhrase(String line) {
			if (line.contains(phrases[0])) {
				printRes(true, "main.error.adb.command");
			}
			else if (line.contains(phrases[1])) {
				printText("Fail! Error:"+line.split("Failure")[1]);
			}
			return "";
		 }

	private boolean textTester(String line) {

		for (int i = 0; i < phrases.length; i++) {
			if (line.contains(phrases[i])) {
				return true;
			}
		}
		return false;
	}

	public boolean isWindows(){
		String os = System.getProperty("os.name").toLowerCase();
		return (os.contains("win"));
	}

	public void getPathtoadb() {
		if(isWindows())
			 pathtoadb = new File(System.getProperty("user.home") + "\\.adblibs\\" + "adb.exe");
		else
			pathtoadb = new File(System.getProperty("user.home") + "\\.adblibs\\" + "adb");

	}
	public String getPath(){
	   	if(isWindows()){
	   		return pathtoadb.getAbsolutePath().substring(0,pathtoadb.toString().length() - 7);
	   		}
		else
			return pathtoadb.getAbsolutePath().substring(0,pathtoadb.toString().length() - 3);

	}
	public void openFolder(File file){

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
}
	  
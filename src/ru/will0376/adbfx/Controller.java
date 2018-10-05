package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.managers.PackageManager;

public class Controller implements Initializable {
	   @FXML
	   private Button button;
	  
	   @FXML
	   private TextArea TextField;
	   @FXML
	   private TextField textFwifi;
	   
	   @FXML
	   private BorderPane v;
	   
	   ResourceBundle resources;
	   
	   private Scene stage;
	   
	   @SuppressWarnings("unused")
	private boolean localhost = false;
	  
		@Override
		public void initialize(URL location, ResourceBundle resources) {
			this.resources = resources;
			TextField.setWrapText(true);
			TextField.setEditable(false);
			TextField.clear();
			printText("AdbFX Version: "+Main.ver);
			printRes("key.main.note.FirstStart");
			printRes("key.main.note.EnableDebugging");
			startUP();
		}
			
	 
	   public void сlearFl(ActionEvent event) {
		   TextField.clear();
	   }
	   
	   public void refleshDevices(ActionEvent event) {
		   startUP();
	   }
	   
	   protected void startUP() {
			List<JadbDevice> devices = connectToPhone();
			if (devices != null) {
				System.out.println(devices.get(0));
				printText(devices.get(0).toString());
			}
		}
	   
	   public void pullUname() {
			try {
				 
				 List<JadbDevice> devices = connectToPhone();
					 if(devices != null) {
					 String text = null;
						try (BufferedReader br = new BufferedReader(new InputStreamReader(devices.get(0).executeShell("uname", "-a"), "UTF-8"))) {
							text = br.lines().collect(Collectors.joining(System.lineSeparator()));
						}
						System.out.println(text);
						printText(text);
					 }
			} catch (IOException | JadbException e) {
				e.printStackTrace();
			}
			
	   }
	   public void saveToFile() {
		   DateFormat dateFormat = new SimpleDateFormat("dd.MM.HH.mm.ss");
			Date date = new Date();
		   File log = new File("./Log"+dateFormat.format(date)+".log");
		   if(!log.exists()) {
			   try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 if(log.exists()) {
				 try {
					FileWriter writer = new FileWriter(log);
					writer.write(TextField.getText());
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
			   
		   }
	   }
	   public void openFiles(ActionEvent event) {
		    FileChooser fileChooser = new FileChooser();
                List<File> files = fileChooser.showOpenMultipleDialog((Window) Main.ps);
                if (files != null) {
                	installToPhone(files);
                }          
		    
	   }
	   public void installToPhone(List<File> files) {
			  
			   Thread install = new Thread(new Runnable() {
				
				@Override
				public void run() {
					 List<JadbDevice> devices = connectToPhone();
					for(int i = 0;i< files.size();i++) {
						String pt = files.get(i).toPath().toString();
						String end = pt.substring(pt.lastIndexOf("."), pt.length());
						if(!end.equals(".apk")) {
							System.out.println(files.get(i)+" "+getRes("key.main.error.IsNotApk"));
							printText(files.get(i)+" "+ getRes("key.main.error.IsNotApk"));
						}
						else {
						printText(getRes("key.main.error.InstallApk")+"("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
						System.out.println(getRes("key.main.error.InstallApk")+"("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
						
						
					try {
						new PackageManager(devices.get(0)).install(files.get(i));
					} catch (IOException | JadbException | NullPointerException e) {
						StringWriter writer = new StringWriter();
			            PrintWriter printWriter= new PrintWriter(writer);
			            e.printStackTrace(printWriter);
						System.out.println("~~~~FAIL!!!~~~~");
						e.printStackTrace();
						printText("~~~~FAIL!!!~~~~");
						printText(writer.toString());
					}
					System.out.println("Success");
					printText("Success");
					
				}
			  }
			 }
			});
			   install.start();		   
	   }
	   
	   public List<JadbDevice> connectToPhone() {
		   try {
				JadbConnection jadb = new JadbConnection();
				List<JadbDevice> devices = null;
				try {
				devices = jadb.getDevices();
				}
				catch(ConnectException e)
				{
					printRes("key.main.error.ConnectionRefused");
					System.out.println(getRes("key.main.error.ConnectionRefused"));
				}
				if(devices != null && devices.size() == 0) {
					System.out.println(getRes("key.main.error.PlsRecconect"));
					printRes("key.main.error.PlsRecconect");
				}
				else if(devices != null && devices.size() >= 1) {
					return devices;
					
				}
				else {
					printRes("key.main.error.Null");
					System.out.println(getRes("key.main.error.Null"));
				}
			} catch (IOException | JadbException e) {
				e.printStackTrace();
			}
		  return null;
	   }

	   public void openAbout(ActionEvent event) {
		   openFXML("About","About","400","270");
	   }
	   public void adbShell(ActionEvent event) {
		   openFXML("Shell","Shell(Beta!)","668","378");
	   }
	   public void openWifiModule(ActionEvent event) {
		   openFXML("Wifi_Main","Wifi Main(Beta)","670","420");
	   }
	   public void openPL(ActionEvent event) {
		   openFXML("ProgramList","ProgramList(TEST)","812","585");
	   }
	   public void openFXML(String... text) {
		   try {
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/"+text[0]+".fxml"));
	            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
	            Parent root1 = fxmlLoader.load();
	            Stage stage1 = new Stage();
	            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
	            if(text.length != 1)
	            stage1.setTitle(text[1]);
	            else
	            stage1.setTitle(text[0]);	
	            stage1.setScene(new Scene(root1, Integer.parseInt(text[2]), Integer.parseInt(text[3])));
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
		public void printRes(String... key) {
			try {
			if(key.length == 2)
				printText(resources.getString(key[0]) + key[1]);
			else
				printText(resources.getString(key[0]));
			}
			catch(MissingResourceException e) {
				e.printStackTrace();
				printText("Error! Key: "+ key[0] +" not found! Locale:" + resources.getLocale() );
			}
		}
		public void printText(String text) {
			TextField.appendText(text + System.getProperty("line.separator"));
		 }
		public String getRes(String... key) {
			try {
				if(key.length == 2)
					return(resources.getString(key[0]) + key[1]);
				else
					return(resources.getString(key[0]));
				}
				catch(MissingResourceException e) {
					e.printStackTrace();
					printText("Error! Key: "+ key[0] +" not found! Locale:" + resources.getLocale() );
				}
			return null;
		}
}
	  
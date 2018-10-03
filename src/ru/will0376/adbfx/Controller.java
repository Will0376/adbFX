package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
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
							System.out.println(files.get(i)+" is not apk");
							TextField.appendText(files.get(i)+" is not apk");
						}
						else {
						printText("Install("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
						System.out.println("Install("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
						
						
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
				List<JadbDevice> devices = jadb.getDevices();
				if(devices.size() == 0) {
					System.out.println("No devices found(Reconnect phone)");
					printText("No devices found(Reconnect phone)");
				}
				else if(devices.size() >= 1) {
					return devices;
					
				}
			} catch (IOException | JadbException e) {
				e.printStackTrace();
			}
		  return null;
	   }

	   public void openAbout(ActionEvent event) {
		   try {
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/About.fxml"));
	            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
	            Parent root1 = fxmlLoader.load();
	            Stage stage1 = new Stage();
	            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
	            stage1.setTitle("About");
	            stage1.setScene(new Scene(root1, 400, 270));
	            stage1.setResizable(false);
	            stage1.show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
	   public void adbShell(ActionEvent event) {
		   try {
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/Shell.fxml"));
	            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
	            Parent root1 = fxmlLoader.load();
	            Stage stage1 = new Stage();
	            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
	            stage1.setTitle("Shell(Beta!)");
	            stage1.setScene(new Scene(root1, 668, 378));
	            stage1.setResizable(false);
	            stage1.show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
	   public void openWifiModule(ActionEvent event) {
		   try {
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/Wifi_Main.fxml"));
	            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
	            Parent root1 = fxmlLoader.load();
	            Stage stage1 = new Stage();
	            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
	            stage1.setTitle("Wifi Main(Beta)");
	            stage1.setScene(new Scene(root1, 670, 420));
	            stage1.setResizable(false);
	            stage1.show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
	   public void openPL(ActionEvent event) {
		   try {
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/ProgramList.fxml"));
	            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
	            Parent root1 = fxmlLoader.load();
	            Stage stage1 = new Stage();
	            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
	            stage1.setTitle("PL(test)");
	            stage1.setScene(new Scene(root1, 812, 585));
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

	   public void changeLangAuto()  throws IOException {
		   stage = Main.getScene();
		   stage.setRoot(FXMLLoader.load(getClass().getResource("Fxml/Main.fxml"),ResourceBundle.getBundle("ru/will0376/adbfx/Locales/Locale", Main.locale)));
	   }
	   public void changeLangEn()  throws IOException {
		   stage = Main.getScene();
		   Main.locale = new Locale("en");
		   stage.setRoot(FXMLLoader.load(getClass().getResource("Fxml/Main.fxml"),ResourceBundle.getBundle("ru/will0376/adbfx/Locales/Locale", new Locale("en"))));
	   }
	   public void changeLangRu()  throws IOException {
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
}
	  
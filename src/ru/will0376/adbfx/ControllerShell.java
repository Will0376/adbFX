package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

public class ControllerShell implements Initializable {
	
	   @FXML
	   private TextArea outShell;
	   @FXML
	   private TextArea inputShell;
	   @FXML
	   private TextField timerFd;
	   private boolean abort = false;
	   ResourceBundle resources;
	   
	 public int timer = 0;
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		printText("");
	}
	public String text = null;
	
	@SuppressWarnings("deprecation")
	public void sendShell(ActionEvent event) {
		timer = 0;
		abort = false;
			 List<JadbDevice> devices = connectToPhone();
				 if(devices != null) {
				
				 Thread myThread = new Thread(new Runnable() {
					    @SuppressWarnings("unused")
						@Override
					    public void run() {
					    	if(devices != null) {
							try {
								BufferedReader br = new BufferedReader(new InputStreamReader(devices.get(0).executeShell(inputShell.getText()), "UTF-8"));
								text = br.lines().collect(Collectors.joining(System.lineSeparator()));
							}
							 catch (IOException | JadbException e) {
									e.printStackTrace();
								}
							 System.out.println(text);
								printText(text);
					    	}
					    	else {
					    		System.out.println(getRes("key.main.error.PlsRecconect"));
								printRes("key.main.error.PlsRecconect");
					    	}
					    	
					    }
				 	});
				 myThread.start();
				 while(myThread.isAlive()) {
					 int maxtimer = 0;
					 if (!timerFd.getText().equals("") && !timerFd.getText().equals(null)) {
						 maxtimer = Integer.parseInt(timerFd.getText());
					 }
					 
					 if (maxtimer != 0 && myThread.isAlive()){
					 timer++;
					 System.out.println("max: "+maxtimer);
					 System.out.println("timer: "+timer);
					 if(timer == maxtimer || abort) {
						
						 System.out.println("Timer!!!");
						 printText("Timer!");
						 myThread.stop();
						 return;
					 }
					}
					 else {
						 timer = 0;
						 return;
					 }
				 }
				 
				}
				 timer = 0;
				 
		 }


		public void aboutTimer() {
			 try {
		            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/AboutShellTimer.fxml"));
		            fxmlLoader.setResources(ResourceBundle.getBundle("ru.will0376.adbfx.Locales.Locale", Main.locale));
		            Parent root1 = fxmlLoader.load();
		            Stage stage1 = new Stage();
		            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
		            stage1.setTitle("AboutTimer");
		            stage1.setScene(new Scene(root1, 221, 140));
		            stage1.setResizable(false);
		            stage1.show();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
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
			outShell.appendText(text + System.getProperty("line.separator"));
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
	 
	 public void doAboutThread() {
		 abort = true;
	 }
	 public void clearLog() {
		 outShell.clear();
		 printText("!!!The Shell mode is still in beta. possible bugs!!!");
	 }
	 public void clearInput() {
		 inputShell.clear();
	 }
	 public void disableTimer() {
		 timerFd.setText("0");
	 }
	 public void defTimer() {
		 timerFd.setText("11000");
	 }
	 public void saveLog() {
		   DateFormat dateFormat = new SimpleDateFormat("dd.MM.HH.mm.ss");
			Date date = new Date();
		   File log = new File("./Log_Shell"+dateFormat.format(date)+".log");
		   if(!log.exists()) {
			   try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 if(log.exists()) {
				 try {
					FileWriter writer = new FileWriter(log);
					writer.write(outShell.getText());
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
			   
		   }
	 }
}

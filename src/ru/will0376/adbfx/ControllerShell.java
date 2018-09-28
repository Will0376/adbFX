package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
	   
	 public int timer = 0;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	public String text = null;
	@SuppressWarnings("deprecation")
	public void sendShell(ActionEvent event) {
		timer = 0;
			 List<JadbDevice> devices = connectToPhone();
				 if(devices != null) {
				
				 Thread myThread = new Thread(new Runnable() {
					    @Override
					    public void run() {
							try (BufferedReader br = new BufferedReader(new InputStreamReader(devices.get(0).executeShell(inputShell.getText()), "UTF-8"))) {
								text = br.lines().collect(Collectors.joining(System.lineSeparator()));
							}
							 catch (IOException | JadbException e) {
									e.printStackTrace();
								}
							System.out.println(text);
							printText(text);
					    
					    }
				 	});
				 myThread.start();
				 
				 while(true) {
					 int maxtimer = Integer.parseInt(timerFd.getText());
					 if (maxtimer != 0){
					 timer++;
					 
					 if(timer >= maxtimer) {
						 System.out.println("Timer!!!");
						 printText("Timer!");
						 myThread.stop();
						 return;
					 }
					}
					 else {
						 return;
					 }
				 }
				}
					
		 }


		public void aboutTimer() {
			 try {
		            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Fxml/AboutShellTimer.fxml"));
		            Parent root1 = fxmlLoader.load();
		            Stage stage1 = new Stage();
		            stage1.getIcons().add(new Image(getClass().getResourceAsStream("Images/logo.png")));
		            stage1.setTitle("AboutTimer");
		            stage1.setScene(new Scene(root1, 214, 132));
		            stage1.setResizable(false);
		            stage1.show();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
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
	 public void printText(String text) {
		  outShell.appendText(text + System.getProperty("line.separator"));
	 }
	 public void clearLog() {
		 outShell.clear();
	 }
	 public void clearInput() {
		 inputShell.clear();
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

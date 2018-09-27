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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.managers.PackageManager;

public class controller implements Initializable {
	 public String n = System.getProperty("line.separator");
	   @FXML
	   private Button button;
	  
	   @FXML
	   private TextArea TextField;
	  
		@Override
		public void initialize(URL location, ResourceBundle resources) {
			TextField.setWrapText(true);
			TextField.setEditable(false);
			TextField.clear();
			startUP();
		}
	 
	   public void ñlearFl(ActionEvent event) {
		   TextField.clear();
	   }
	   public void refleshDevices(ActionEvent event) {
		   startUP();
	   }
	   protected void startUP() {
		try {
			JadbConnection jadb = new JadbConnection();
			List<JadbDevice> devices = jadb.getDevices();
			if(devices.size() == 0) {
				System.out.println("No devices found");
				TextField.appendText("No devices found"+n);
			}
			else if(devices.size() == 1) {
				System.out.println(devices.get(0));
				TextField.appendText(devices.get(0).toString()+n);
				
			}
		} catch (IOException | JadbException e) {
			e.printStackTrace();
		}
	   }
	   public void pullUname() {
			try {
				 JadbConnection jadb = new JadbConnection();
				 List<JadbDevice> devices = jadb.getDevices();

				 if(devices.size() == 0) {
						System.out.println("No devices found");
						TextField.appendText("No devices found"+n);
					}
				 else {
					 
					 JadbDevice device = devices.get(0);
					 String text = null;
						try (BufferedReader br = new BufferedReader(new InputStreamReader(device.executeShell("uname", "-a"), "UTF-8"))) {
							text = br.lines().collect(Collectors.joining(System.lineSeparator()));
						}
						System.out.println(text);
						TextField.appendText(text+n);
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
		   try {
				JadbConnection jadb = new JadbConnection();
				List<JadbDevice> devices = jadb.getDevices();
				if(devices.size() == 0) {
					System.out.println("No devices found(Reconnect phone)");
					TextField.appendText("No devices found(Reconnect phone)"+n);
				}
				else if(devices.size() == 1) {
					for(int i = 0;i< files.size();i++) {
						String pt = files.get(i).toPath().toString();
						String end = pt.substring(pt.lastIndexOf("."), pt.length());
						if(!end.equals(".apk")) {
							System.out.println(files.get(i)+" is not apk");
							TextField.appendText(files.get(i)+" is not apk"+n);
						}
						else {
						System.out.println("Install("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
						TextField.appendText("Install("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString()+n);
					new PackageManager(devices.get(0)).install(files.get(i));
					System.out.println("Success");
					TextField.appendText("Success");
						}
						}
					
				}
			} catch (IOException | JadbException e) {
				StringWriter writer = new StringWriter();
	            PrintWriter printWriter= new PrintWriter(writer);
	            e.printStackTrace(printWriter);
				System.out.println("~~~~FAIL!!!~~~~");
				e.printStackTrace();
				TextField.appendText("~~~~FAIL!!!~~~~"+n);
				TextField.appendText(writer.toString());
			}
	   }
	   
	   public void openAbout(ActionEvent event) {
		   try {
	            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("About.fxml"));
	            Parent root1 = fxmlLoader.load();
	            Stage stage1 = new Stage();
	            stage1.setTitle("About");
	            stage1.setScene(new Scene(root1, 400, 270));
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
}
	  
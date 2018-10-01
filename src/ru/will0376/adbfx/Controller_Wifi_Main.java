package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class Controller_Wifi_Main implements Initializable  {
		public boolean connected = false;
	 @FXML
	   private TextArea output;
	 @FXML
	   private TextField textip;
	 @FXML
	 private TextField textcomfl;
	 	
	 ResourceBundle resources;
	 
	 private File file;
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		printText(resources.getString("key.wifi.main.InstallModule"));
	}
	public void connect(ActionEvent event) {
		execute("connect",  textip.getText());
}
	public void disconnect(ActionEvent event) {
		execute("kill-server");
	}
	public void reconnect(ActionEvent event) {
		execute("kill-server");
		execute("connect", textip.getText());
	}
	public void execute(String... command) {
		try {
			ProcessBuilder pb = null;
			String file = new File(".").getCanonicalPath()+"\\adblibs\\";
			 if(command.length == 1 && command[0].equals("devices")) {
				pb = new ProcessBuilder(file+"adb.exe",command[0]);
				
			 }
			
			if(command.length == 1 && command[0].equals("install") && this.file != null)
				 pb = new ProcessBuilder(file+"adb.exe", command[0],this.file.getAbsoluteFile().toString());
			
			if(command.length == 1)
			 pb = new ProcessBuilder(file+"adb.exe", command[0]);
			
			else if(command.length == 2)
			pb = new ProcessBuilder(file+"adb.exe", command[0], command[1]);
			
			else if(command.length == 3)
				pb = new ProcessBuilder(file+"adb.exe", command[0], command[1], command[2]);
			
			else if(command.length == 4)
				pb = new ProcessBuilder(file+"adb.exe", command[0], command[1], command[2], command[3]);
			
			else if(command.length == 5)
				pb = new ProcessBuilder(file+"adb.exe", command[0], command[1], command[2], command[3], command[4]);
			
			else if(command.length == 6)
				pb = new ProcessBuilder(file+"adb.exe", command[0], command[1], command[2], command[3], command[4], command[5]);
			else {
				pb = new ProcessBuilder(file+"adb.exe", command[0], command[1], command[2], command[3], command[4], command[5]);
				printText(resources.getString("key.wifi.main.error.6thCommand"));
			}
			pb.redirectErrorStream(true);
			Process proc = pb.start();

			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;
			int exit = -1;

			while ((line = br.readLine()) != null) {
			    // Outputs your process execution
			    System.out.println(line);
			    printText(line);
			    try {
			        exit = proc.exitValue();
			        if (exit == 0)  {
			            // Process finished
			        }
			    } catch (IllegalThreadStateException t) {
			        // The process has not yet finished. 
			        // Should we stop it?
			            proc.destroy();
			    	}
				}
			}
		catch(IllegalThreadStateException | IOException | NullPointerException t) {
			t.printStackTrace();
		}
	}
	public void killServer(ActionEvent event) {
		execute("kill-server");
	}
	public void printText(String text) {
		output.appendText(text + System.getProperty("line.separator"));
	 }
	public void reflesh(ActionEvent event) {
		execute("devices");
	}
	public void installApk(ActionEvent event) {
		openFiles();
	}
	public void clearLog(ActionEvent event) {
		output.clear();
	}
	 public void openFiles() {
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
				for(int i = 0;i< files.size();i++) {
					String pt = files.get(i).toPath().toString();
					String end = pt.substring(pt.lastIndexOf("."), pt.length());
					if(!end.equals(".apk")) {
						System.out.println(files.get(i)+" is not apk");
						printText(files.get(i)+" is not apk");
					}
					else {
						printText("Install("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
					System.out.println("Install("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
					
					
				try {
					for(int ii = 0; ii < files.size();ii++)
					execute("install",files.get(ii).getAbsoluteFile().toString());
				} catch (NullPointerException e) {
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
		public void exeCommand() {
			execute("shell",textcomfl.getText());
		}
}

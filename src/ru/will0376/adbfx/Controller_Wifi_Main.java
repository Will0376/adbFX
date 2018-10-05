package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class Controller_Wifi_Main implements Initializable  {
	 @FXML
	 private TextArea output;
	 @FXML
	 private TextField textip;
	 @FXML
	 private TextField textcomfl;
	 	
	 ResourceBundle resources;
	 
	 private File file;
	 
	 String adbfile ;
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			adbfile = new File(".").getCanonicalPath()+"\\adblibs\\"+"adb.exe";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.resources = resources;
		printRes("key.wifi.main.InstallModule");
		output.setWrapText(true);
		textip.setText(getIpFromFile().replaceAll("null", ""));
	}
	public void connect(ActionEvent event) {
		printRes("key.wifi.main.ConnectTo"," "+textip.getText());
		execute("connect",  textip.getText());
}
	public void disconnect(ActionEvent event) {
		printRes("key.wifi.main.Disconnected");
		execute("kill-server");
	}
	public void reconnect(ActionEvent event) {
		printRes("key.wifi.main.RecconectTo"," "+textip.getText());
		execute("kill-server");
		execute("connect", textip.getText());
	}
	public void execute(String... command) {
		try {
			ProcessBuilder pb = null;
			
			 if(command.length == 1 && command[0].equals("devices")) {
				pb = new ProcessBuilder(adbfile,command[0]);
				
			 }
			
			if(command.length == 1 && command[0].equals("install") && this.file != null)
				 pb = new ProcessBuilder(adbfile, command[0],this.file.getAbsoluteFile().toString());
			
			if(command.length == 1)
			 pb = new ProcessBuilder(adbfile, command[0]);
			
			else
			pb = new ProcessBuilder(adbfile, command[0], command[1]);

			pb.redirectErrorStream(true);
			Process proc = pb.start();

			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;
			int exit = -1;

			while ((line = br.readLine()) != null) {
			    System.out.println(line);
			    printText(line);
			    try {
			        exit = proc.exitValue();
			        if (exit == 0)  {
			        }
			    } catch (IllegalThreadStateException t) {
			            proc.destroy();
			    	}
				}
			}
		catch(IllegalThreadStateException | IOException | NullPointerException t) {
			t.printStackTrace();
		}
	}
	
	public void printText(String text) {
		output.appendText(text + System.getProperty("line.separator"));
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
	
	public void reflesh(ActionEvent event) {
		execute("devices");
	}
	public void installApk(ActionEvent event) {
		openFiles();
	}
	public void clearLog(ActionEvent event) {
		output.clear();
		printRes("key.wifi.main.InstallModule");
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
						System.out.println(files.get(i)+" "+getRes("key.main.error.IsNotApk"));
						printText(files.get(i)+" "+ getRes("key.main.error.IsNotApk"));
					}
					else {
					printText(getRes("key.main.error.InstallApk")+"("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
					System.out.println(getRes("key.main.error.InstallApk")+"("+(i + 1)+"/"+files.size()+"): " + files.get(i).toString());
					
					
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
				
			}
		  }
		 }
		});
		   install.start();		   
      }
		public void exeCommand() {
			execute("shell",textcomfl.getText());
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

		private String getIpFromFile() {
			File file = new File(adbfile.replaceAll("adb.exe", "")+"config.cfg");
			if (file.exists()) {
				try {
					FileReader fr = new FileReader(file); 
				      char [] a = new char[200];
				      fr.read(a);
				      String ret = null;
				      for(char c : a) {
				         ret += c;
				      }
				      fr.close();
				      return ret.replaceAll("Ip:", "");
				} catch ( IOException e) {
					e.printStackTrace();
				}
			}
			return "Error read file";
		}
		public void saveIpToFile() {
			File file = new File(adbfile.replaceAll("adb.exe", "")+"config.cfg");
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file);
				fw.write("Ip:"+textip.getText());
				System.out.println("Saved! " + "Ip:"+textip.getText());
				fw.flush();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
			
}

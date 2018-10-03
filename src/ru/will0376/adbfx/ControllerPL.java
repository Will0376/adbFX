package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

public class ControllerPL implements Initializable {
	 ResourceBundle resources;
	 
	 @FXML
	 ListView<String> out;
	 @FXML
	 TextArea outLog;
	 @FXML
	 Text all;
	 String text;
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
		out.autosize();
	}
	public void getList() {
		out.getItems().clear();
		String text = doShell("pm list packages");
		 if( text != null) {
			 String[] ary = text.split("package:");
			 for(int i = 0; i < ary.length;i++) {
				 out.getItems().add(i, ary[i]);
				 if(Main.debug)printText(String.valueOf(i));
			 	}	
		 }
	}
	public void delete() {
			MultipleSelectionModel<String> ee = out.getSelectionModel();
			System.out.println(ee.getSelectedItem().toString());
			System.out.println(doShell("su pm uninstall "+ ee.getSelectedItem().toString()));
			getList();
	}
	
	private String doShell(String doto){
		List<JadbDevice> devices = connectToPhone();
		if(devices == null) {
			printText("ERROR! devices = null");
			printRes("key.pl.main.error.PlsRecconect");
		}
		else if(devices.size() == 0) {
			printText("ERROR! devices = 0");
			printRes("key.pl.main.error.PlsRecconect");
		}
		else {
			System.out.println(doto);
			 Thread myThread = new Thread(new Runnable() {
				 
					@SuppressWarnings("unused")
					@Override
				    public void run() {
				    	if(devices != null) {
						try {
							BufferedReader br = new BufferedReader(new InputStreamReader(devices.get(0).executeShell(doto), "UTF-8"));
							text = br.lines().collect(Collectors.joining(System.lineSeparator()));
						
						 
						if(!text.equals("null")) {
							System.out.println("Success!");
							printText("Success!");
							
						}
						else {
							System.out.println("Null");
							printText("Null");
						}
				    }
						catch (IOException | JadbException | NullPointerException e) {
							StringWriter writer = new StringWriter();
				            PrintWriter printWriter= new PrintWriter(writer);
				            e.printStackTrace(printWriter);
							System.out.println("~~~~FAIL!!!~~~~");
							e.printStackTrace();
							printText("~~~~FAIL!!!~~~~");
							printText(writer.toString());
						}
				    	
				    	
				    	}
				    	else {
				    		System.out.println("Devices not found!");
							printText("Devices not found!");
				    	}
			 }
			 	});
			 myThread.start();
			while(true) {
				if(!myThread.isAlive() && text != null)
					return text;
					}
				 }
		return doto;
			 }
	
	
	
	///
	 public List<JadbDevice> connectToPhone() {
		   try {
				JadbConnection jadb = new JadbConnection();
				List<JadbDevice> devices = jadb.getDevices();
				if(devices.size() == 0) {
					System.out.println("No devices found(Reconnect phone)");
					printText("No devices found! Reconnect phone Or be connected on wi-fi (in the wi-fi adb tab)");
				}
				else if(devices.size() >= 1) {
					return devices;
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
			outLog.appendText(text + System.getProperty("line.separator"));
		 }

}

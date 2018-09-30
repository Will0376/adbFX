package ru.will0376.adbfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

public class Controller_Wifi_Main implements Initializable  {

	 @FXML
	   private TextArea output;
	 @FXML
	   private TextField textip;
	 
	 Thread th;
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	public JadbConnection jadb;
	public void connect() {
		
		th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("test");
				try {
					jadb = new JadbConnection(textip.getText(), 5555);
					System.out.println("test2");
					System.out.println(jadb.getAnyDevice());
					System.out.println("test3");
					BufferedReader br = new BufferedReader(new InputStreamReader(jadb.getDevices().get(0).executeShell("uname -a"), "UTF-8"));
					System.out.println(br.lines().collect(Collectors.joining(System.lineSeparator())));
				//	List<JadbDevice> devices = jadb.getDevices();
					System.out.println("test33");
					System.out.println(jadb.getDevices().get(0).getSerial());
					System.out.println("test4");
					//System.out.println(devices.get(0).getState());
				} catch (IOException | JadbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("test1");
			}
		});	
			th.start();
			
	}
	public void disconnect() {
		if(th.isAlive())
			th.stop();
	}

}
